package com.ah.util.compress.tar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import com.ice.tar.TarBuffer;
import com.ice.tar.TarEntry;
import com.ice.tar.TarOutputStream;
import com.ice.tar.TarProgressDisplay;

import com.ah.util.Tracer;

public class AhTar implements TarProgressDisplay {

	private static final Tracer log = new Tracer(AhTar.class.getSimpleName());

	private static final int BUFFER_SIZE = 2048;

	private boolean debug = false;

	public AhTar() {
		
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String tarArchive(String pathname) throws IOException {
   		return tarArchive(pathname, true, null);
	}

	public String tarArchive(String pathname, boolean recurse) throws IOException {
		return tarArchive(pathname, recurse, null);
	}

	public String tarArchive(String pathname, boolean recurse, String tarPathname) throws IOException {
		return tarArchive(new File(pathname), recurse, tarPathname);
	}

	public String tarArchive(File file) throws IOException {
   		return tarArchive(file, true, null);
	}

	public String tarArchive(File file, boolean recurse) throws IOException {
   		return tarArchive(file, recurse, null);
	}

	/**
	 * Tars a file or directory into an archive and returns the path of archive generated.
	 *
	 * @param file the target file or directory to be archived.
	 * @param recurse if true, process its children if the <tt>file</tt> is a directory.
	 * @param archivePathname the path of archive to be generated. If <tt>null</tt> or an empty string is provided, the actual result will be the string <tt>file.getPath()</tt> + ".tar.gz" and the archive to be generated will reside in the same repository as that of the <tt>file</tt>.
	 * @throws IOException if any I/O error occurs.
	 * @return the path of archive to be generated.
	 */
	public String tarArchive(File file, boolean recurse, String archivePathname) throws IOException {
		boolean succ = false;
		String candidate;

		if (archivePathname == null || archivePathname.trim().isEmpty()) {
			candidate = file.getPath() + ".tar.gz";
		} else {
			candidate = archivePathname;
		}

		if (debug) {
			log.info("tarArchive", "Taring " + (file.isFile() ? "file" : "directory") + " '" + file.getPath() + "' into archive '" + candidate + "'.");
		}

		TarFileFilter fileFilter = new TarFileFilter(candidate);
		TarOutputStream tarOut = null;

		try {
			FileOutputStream fOut = new FileOutputStream(candidate);
			GZIPOutputStream gzip = new GZIPOutputStream(fOut, BUFFER_SIZE);
			tarOut = new TarOutputStream(gzip, TarBuffer.DEFAULT_BLKSIZE);
			tarOut.setDebug(debug);
			tarOut.setBufferDebug(debug);

			if (file.isFile() || recurse) {
				writeEntry(tarOut, file, fileFilter);
			} else {
				// Tar the files only at the first level of directory.
				for (File subFile : file.listFiles(fileFilter)) {
					if (subFile.isFile()) {
						writeEntry(tarOut, subFile, fileFilter);
					}
				}
			}

			succ = true;

			return candidate;
		} finally {
			if (tarOut != null) {
				try {
					tarOut.close();
				} catch (IOException ioe) {
					log.error("tarArchive", "I/O Close Error.", ioe);
				}
			}

			// Remove incomplete archive.
			if (!succ) {
				File imcompleteArchive = new File(candidate);

				if (imcompleteArchive.exists()) {
					if (debug) {
						log.info("tarArchive", "Deleting the incomplete archive '" + candidate + "' from the unsuccessful tar.");
					}

					boolean deleted = imcompleteArchive.delete();

					if (debug) {
						log.info("tarArchive", "The incomplete archive '" + candidate + "' " + (deleted ? "was" : "wasn't") + " deleted.");
					}
				}
			}
		}
	}

	@Override
	public void showTarProgressMessage(String s) {
		log.info("showTarProgressMessage", s);
	}

	private void writeEntry(TarOutputStream out, File file, TarFileFilter fileFilter) throws IOException {
		if (file.isFile()) { // Tar file.
			if (debug) {
				log.info("writeEntry", "Adding file '" + file.getPath() + "' into archive.");
			}

			BufferedInputStream bIn = null;

			try {
				FileInputStream fIn = new FileInputStream(file);
				bIn = new BufferedInputStream(fIn, BUFFER_SIZE);
				TarEntry entry = new TarEntry(file);

				if (debug) {
					printDebugMsg(entry);
				}

				out.putNextEntry(entry);
				byte[] buf = new byte[BUFFER_SIZE];
				int numRead;

				while ((numRead = bIn.read(buf, 0, BUFFER_SIZE)) != -1) {
					out.write(buf, 0, numRead);
				}
			} finally {
				if (bIn != null) {
					try {
						bIn.close();
					} catch (IOException ioe) {
						log.error("writeEntry", "I/O Close Error.", ioe);
					}
				}
			}

			// Note that the file entry must be closed once it is written into archive.
			out.closeEntry();

			if (debug) {
				log.info("writeEntry", "File '" + file.getPath() + "' was added into archive.");
			}
		} else { // Tar directory.
			if (debug) {
				log.info("writeEntry", "Adding directory '" + file.getPath() + "' into archive.");
			}

			for (File subFile : file.listFiles(fileFilter)) {
				writeEntry(out, subFile, fileFilter);
			}

			if (debug) {
				log.info("writeEntry", "Directory '" + file.getPath() + "' was added into archive.");
			}
		}
	}

	private void printDebugMsg(TarEntry entry) {
		StringBuilder entryInfoBuf = new StringBuilder("TarEntry Info:")
				.append("\nName: ").append(entry.getName())
				.append("\nUser Name").append(entry.getUserName())
				.append("\nGroup Name").append(entry.getGroupName())
				.append("\nGroup Id").append(entry.getGroupId())
				.append("\nSize").append(entry.getSize())
				.append("\nGNUTarFormat").append(entry.isGNUTarFormat())
				.append("\nUnixTarFormat").append(entry.isUnixTarFormat())
				.append("\nUSTarFormat").append(entry.isUSTarFormat());
		log.info("printDebugMsg", entryInfoBuf.toString());
	}

	class TarFileFilter implements FileFilter {

		private final String excludedPathname;

		public TarFileFilter(String excludedPathname) {
			this.excludedPathname = excludedPathname;
		}

		public TarFileFilter(File excludedFile) {
			this.excludedPathname = excludedFile.getPath();
		}

		@Override
		public boolean accept(File pathname) {
			// Archive in generating shouldn't be involved in the files to be tared.
			return !excludedPathname.equals(pathname.getPath());
		}
	}

}