package com.ah.be.os;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.ah.be.app.DebugUtil;

/**
 * @filename FileManager.java
 * @version V1.0.0.0
 * @author juyizhou
 * @createtime 2007-9-7 03:37:09 Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *             All right reserved.
 */
/**
 * modfy history*
 */
public class FileManager
{

	private static final FileManager	instance	= new FileManager();

	public static FileManager getInstance()
	{
		return instance;
	}

	/**
	 * copy direcotry content
	 *
	 * @param srcPath
	 *            the source path of to be copied directory
	 * @param dstPath
	 *            the destination path
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public void copyDirectory(String srcPath, String dstPath)
		throws IllegalArgumentException,
		IOException,
		BeNoPermissionException
	{
		if (srcPath == null || dstPath == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		File srcDir = new File(srcPath);

		if (!srcDir.exists())
		{
			throw new FileNotFoundException(srcPath + " is not exist");
		}
		if (srcDir.isFile())
		{
			throw new IllegalArgumentException("Invalid argument " + srcDir);
		}

		File dstDir = new File(dstPath);
		if (dstDir.isFile())
		{
			throw new IllegalArgumentException("Invalid argument " + dstDir);
		}
		if (!dstDir.exists())
		{
			// create directory , including any necessary but nonexistent parent
			// directories
			dstDir.mkdirs();
		}

		if (!srcDir.canRead())
		{
			throw new BeNoPermissionException("No permission to read " + srcDir);
		}
		if (!dstDir.canWrite())
		{
			throw new BeNoPermissionException("No permission to write "
				+ dstDir);
		}

		// deep copy directory content
		File[] srcfiles = srcDir.listFiles();
		for (File srcfile : srcfiles) {
			if (srcfile.isFile()) {
				copyFile(srcfile.getPath(), dstDir + File.separator
						+ srcfile.getName());
			}
			if (srcfile.isDirectory()) {
				copyDirectory(srcPath + File.separator + srcfile.getName(),
						dstPath + File.separator + srcfile.getName());
			}
		}
	}

	/**
	 * copy File
	 *
	 * @param srcPath -
	 * @param dstPath =
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public void copyFile(String srcPath, String dstPath)
		throws IllegalArgumentException,
		IOException,
		BeNoPermissionException
	{
		if (srcPath == null || dstPath == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		File srcFile = new File(srcPath);
		if (!srcFile.exists())
		{
			throw new FileNotFoundException(srcFile + " is not exist");
		}
		if (!srcFile.isFile())
		{
			throw new IllegalArgumentException("Invalid argument " + srcFile);
		}

		File dstFile = new File(dstPath);

		if (!srcFile.canRead())
		{
			throw new BeNoPermissionException("No permission to read "
				+ srcFile);
		}
		if (dstFile.exists() && !dstFile.canWrite())
		{
			throw new BeNoPermissionException("No permission to write "
				+ dstFile);
		}

		FileInputStream input = new FileInputStream(srcFile);
		FileOutputStream output = new FileOutputStream(dstFile);
		byte[] b = new byte[1024 * 4];
		int len;
		while ((len = input.read(b)) != -1)
		{
			output.write(b, 0, len);
		}
		output.flush();
		output.close();
		input.close();
	}

	public void copyFile(File source, File dest, boolean append) throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;

		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(dest, append);
			byte[] inputBuf = new byte[1024];
			int len;

			while ((len = in.read(inputBuf)) != -1) {
				out.write(inputBuf, 0, len);
			}

			out.flush();
		} finally {
			if (in != null) {
				in.close();
			}

			if (out != null) {
				out.close();
			}
		}
	}

	public void copyFile(File source, File dest) throws IOException {
		copyFile(source, dest, false);
	}

	public void copyFile(String source, String dest, boolean append) throws IOException {
		copyFile(new File(source), new File(dest), append);
	}

//	public void copyFile(String source, String dest) throws IOException {
//		copyFile(new File(source), new File(dest), false);
//	}

	/**
	 * Creates the directory named given by dirPath
	 *
	 * @param dirPath -
	 * @return true if and only if the directory was created; false if already
	 *         exists
	 * @throws IllegalArgumentException -
	 */
	public boolean createDirectory(String dirPath)
		throws IllegalArgumentException
	{
		if (dirPath == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		File file = new File(dirPath);

		// true if and only if the directory was created; false otherwise
		// mkdirs can create no-exist parent directory
		return file.mkdirs();
	}

	public void createFile(String contents, String filePath) throws IOException
	{
		File file = new File(filePath);
		if (null != file.getParentFile()
				&& !file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		FileOutputStream output = null;
		try {
			output = new FileOutputStream(filePath);
			output.write(contents.getBytes());
			output.flush();
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	/**
	 * Creates the file named by this abstract pathname.
	 *
	 * @param filePath -
	 * @param lines -
	 * @return true if and only if the file was created; false if the named file
	 *         already exists. If have permission to write, content given by
	 *         lines will be written to this file. BeNoPermissionException when
	 *         user have no read permission
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public boolean createFile(String filePath, String[] lines)
		throws IllegalArgumentException,
		IOException,
		BeNoPermissionException
	{
		if (filePath == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		File file = new File(filePath);
		// true if the named file does not exist and was successfully
		// created; false if the named file already exists
		boolean isSuccess = file.createNewFile();

		// if the named file already exists and file can not be written, will
		// throw BeNoPermissionException
		if (!isSuccess && !file.canWrite())
		{
			throw new BeNoPermissionException("No permission to write " + file);
		}

		writeFile(filePath, lines, false);

		return isSuccess;
	}
	
	public void createFile(byte[] contents, String filePath) throws IOException
	{
		File file = new File(filePath);
		if (null != file.getParentFile()
				&& !file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		FileOutputStream output = null;
		try {
			output = new FileOutputStream(filePath);
			output.write(contents);
			output.flush();
		} finally {
			if (output != null) {
				try{
					output.close();
				}catch(Exception e){
					throw e;
				}
			}
		}
	}

	/**
	 * delete the directory named by this abstract pathname.
	 *
	 * @param dirPath -
	 * @return true if and only if the directory was deleted; false
	 *         otherwise(for example user have no write permission)
	 *         NullPointerException when user have no read permission
	 *         BeNoPermissionException when user have no read permission
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 * @throws NullPointerException -
	 * @throws BeNoPermissionException -
	 */
	public boolean deleteDirectory(String dirPath)
		throws IllegalArgumentException,
		FileNotFoundException,
		NullPointerException,
		BeNoPermissionException
	{
		if (dirPath == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		File dir = new File(dirPath);
		if (!dir.exists())
		{
			throw new FileNotFoundException(dir + " is not exist");
		}

		if (!dir.isDirectory())
		{
			throw new IllegalArgumentException("Invalid argument " + dirPath);
		}

		if (dir.list().length == 0)
		{
			return dir.delete();
		}

		if (!dir.canRead())
		{
			throw new BeNoPermissionException("No permission to read " + dir);
		}

		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				deleteDirectory(file.getAbsolutePath());
			} else {
				file.delete();
			}
		}

		return dir.delete();
	}

	/**
	 * delete the file named by this abstract pathname.
	 *
	 * @param filePath -
	 * @return true if and only if the file was deleted; false otherwise
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 */
	public boolean deletefile(String filePath)
		throws IllegalArgumentException,
		FileNotFoundException
	{
		if (filePath == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		File file = new File(filePath);
		if (!file.exists())
		{
			throw new FileNotFoundException(file + " is not exist");
		}

		if (!file.isFile())
		{
			throw new IllegalArgumentException("Invalid argument " + filePath);
		}

		return file.delete();
	}

	/**
	 * return an list of the files name in the directory denoted by this
	 * pathname
	 *
	 * @param path -
	 * @return BeNoPermissionException when user have no read permission
	 * @throws FileNotFoundException -
	 * @throws IllegalArgumentException -
	 * @throws BeNoPermissionException -
	 */
	public List<String> getFileNamesOfDirecotry(String path)
		throws FileNotFoundException,
		IllegalArgumentException,
		BeNoPermissionException
	{
		List<File> fileList = getFilesFromFolder(new File(path), false);
		List<String> fileNameList = null;

		if (fileList != null)
		{
			fileNameList = new ArrayList<String>(fileList.size());

			for (File file : fileList)
			{
				fileNameList.add(file.getName());
			}
		}

		return fileNameList;
	}

	/**
	 * return an list of the files name in the directory denoted by this
	 * pathname
	 *
	 * @param path -
	 * @param flag
	 *            get file or sub-directory or both <br>
	 *            0: both 1: only file 2: only directory
	 * @param isRecur
	 *            if true will return files/subdirs' names in sub-directories
	 * @return BeNoPermissionException when user have no read permission
	 * @throws FileNotFoundException -
	 * @throws IllegalArgumentException -
	 * @throws BeNoPermissionException -
	 */
	public List<String> getFileAndSubdirectoryNames(
		String path,
		short flag,
		boolean isRecur)
		throws FileNotFoundException,
		IllegalArgumentException,
		BeNoPermissionException
	{
		List<File> fileList = getFilesAndSubdirectories(new File(path), flag,
			isRecur);
		List<String> fileNameList = null;

		if (fileList != null)
		{
			fileNameList = new ArrayList<String>(fileList.size());

			for (File file : fileList)
			{
				fileNameList.add(file.getName());
			}
		}

		return fileNameList;
	}

	/**
	 * return an list of files&subdirectories in the directory given by a_dir
	 *
	 * @param a_dir -
	 * @param flag
	 *            get file or sub-directory or both <br>
	 *            0: both 1: only file 2: only directory
	 * @param isRecur
	 *            if true will return files/subdirs in sub-directories
	 * @return BeNoPermissionException when user have no read permission
	 * @throws FileNotFoundException -
	 * @throws IllegalArgumentException -
	 * @throws BeNoPermissionException -
	 */
	public List<File> getFilesAndSubdirectories(
		File a_dir,
		short flag,
		boolean isRecur)
		throws FileNotFoundException,
		IllegalArgumentException,
		BeNoPermissionException
	{
		if (a_dir == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		if (!a_dir.exists())
		{
			throw new FileNotFoundException(a_dir + " is not exist");
		}

		if (!a_dir.isDirectory())
		{
			throw new IllegalArgumentException("Argument " + a_dir
				+ " is not a directory.");
		}

		if (!a_dir.canRead())
		{
			throw new BeNoPermissionException("No permission to read " + a_dir);
		}

		List<File> fileList = new ArrayList<File>();

		for (File file : a_dir.listFiles())
		{
			if (flag == BeOsLayerModule.BOTH
				|| (flag == BeOsLayerModule.ONLYDIRECTORY && file.isDirectory())
				|| (flag == BeOsLayerModule.ONLYFILE && file.isFile()))
			{
				fileList.add(file);
			}

			if (file.isDirectory() && isRecur)
			{
				fileList.addAll(getFilesAndSubdirectories(file, flag, isRecur));
			}
		}

		return fileList.size() > 0 ? fileList : null;
	}

	/**
	 * return an list of filelists in the directory given by a_dir
	 *
	 * @param a_dir -
	 * @param isRecur
	 *            if true will return files in sub-directories
	 * @return BeNoPermissionException when user have no read permission
	 * @throws FileNotFoundException -
	 * @throws IllegalArgumentException -
	 * @throws BeNoPermissionException -
	 */
	public List<File> getFilesFromFolder(File a_dir, boolean isRecur)
		throws FileNotFoundException,
		IllegalArgumentException,
		BeNoPermissionException
	{
		if (a_dir == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		if (!a_dir.exists())
		{
			throw new FileNotFoundException(a_dir + " is not exist");
		}

		if (!a_dir.isDirectory())
		{
			throw new IllegalArgumentException("Argument " + a_dir
				+ " is not a directory.");
		}

		if (!a_dir.canRead())
		{
			throw new BeNoPermissionException("No permission to read " + a_dir);
		}

		List<File> fileList = new ArrayList<File>();

		for (File file : a_dir.listFiles())
		{
			if (file.isFile())
			{
				fileList.add(file);
			}

			if (file.isDirectory() && isRecur)
			{
				fileList.addAll(getFilesFromFolder(file, isRecur));
			}
		}

		return fileList.size() > 0 ? fileList : null;
	}

	/**
	 * move file
	 *
	 * @param srcPath -
	 * @param dstPath -
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public void moveFile(String srcPath, String dstPath)
		throws IllegalArgumentException,
		IOException,
		BeNoPermissionException
	{
		copyFile(srcPath, dstPath);

		deletefile(srcPath);
	}

	/**
	 * move Directory
	 *
	 * @param srcPath -
	 * @param dstPath -
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public void moveDirectory(String srcPath, String dstPath)
		throws IllegalArgumentException,
		IOException,
		BeNoPermissionException
	{
		copyDirectory(srcPath, dstPath);

		deleteDirectory(srcPath);
	}

	public String readFromFile(String pathname) throws IOException {
		FileChannel fileChannel = new FileInputStream(pathname).getChannel();
		ByteBuffer bb = ByteBuffer.allocate((int) fileChannel.size());
		fileChannel.read(bb);
		fileChannel.close();
		bb.flip();
		byte[] bytes;

		if (bb.hasArray()) {
			bytes = bb.array();
		} else {
			bytes = new byte[bb.limit()];
			bb.get(bytes);
		}

		return new String(bytes);
	}

	public String readFromFile(File file) throws IOException {
		FileChannel fileChannel = new FileInputStream(file).getChannel();
		ByteBuffer bb = ByteBuffer.allocate((int) fileChannel.size());
		fileChannel.read(bb);
		fileChannel.close();
		bb.flip();
		byte[] bytes;

		if (bb.hasArray()) {
			bytes = bb.array();
		} else {
			bytes = new byte[bb.limit()];
			bb.get(bytes);
		}

		return new String(bytes);
	}

	/**
	 * read contents of file named by filepath
	 *
	 * @param filePath -
	 * @return lines text of file BeNoPermissionException when user have no read
	 *         permission
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public String[] readFile(String filePath)
		throws IllegalArgumentException,
		IOException,
		BeNoPermissionException
	{
		if (filePath == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		File file = new File(filePath);

		if (!file.exists())
		{
			throw new FileNotFoundException(filePath + " is not exist");
		}

		if (!file.canRead())
		{
			throw new BeNoPermissionException("No permission to read " + file);
		}

		FileInputStream fis = new FileInputStream(file);
		BufferedReader bf = new BufferedReader(new InputStreamReader(fis));
		String line;
		Vector<String> vct = new Vector<String>();
		while ((line = bf.readLine()) != null)
		{
			vct.addElement(line);
		}

		bf.close();
		fis.close();

		return vct.toArray(new String[vct.size()]);
	}

	/**
	 * read corresponding value with key in the file named by filePath
	 *
	 * @param filePath -
	 * @param key
	 *            the specified key
	 * @return BeNoPermissionException when user have no read permission
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public String readFile(String filePath, String key)
		throws IllegalArgumentException,
		IOException,
		BeNoPermissionException
	{
		if (filePath == null || key == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		File file = new File(filePath);

		if (!file.exists())
		{
			throw new FileNotFoundException(file + " is not exist");
		}

		if (!file.canRead())
		{
			throw new BeNoPermissionException("No permission to read " + file);
		}

		FileInputStream fis = new FileInputStream(file);
		Properties pro = new Properties();
		pro.load(fis);
		fis.close();

		return pro.getProperty(key);
	}

	/**
	 * write contents to file named by filepath
	 *
	 * @param filePath -
	 * @param lines -
	 * @param append
	 *            boolean if <code>true</code>, then data will be written to
	 *            the end of the file rather than the beginning.
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public void writeFile(String filePath, String[] lines, boolean append)
		throws IllegalArgumentException,
		IOException,
		BeNoPermissionException
	{
		if (filePath == null || lines == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		File file = new File(filePath);
		if (!file.exists())
		{
			throw new FileNotFoundException(file + " is not exist");
		}

		if (!file.canWrite())
		{
			throw new BeNoPermissionException("No permission to write " + file);
		}

		FileWriter fw = new FileWriter(filePath, append);
		PrintWriter out = new PrintWriter(fw);
		for (String line : lines) {
			out.write(line);
			out.println();
			out.flush();
		}
		fw.close();
		out.close();
	}

	/**
	 * check whether file contain the same line
	 *
	 * @param filePath -
	 * @param line -
	 * @return -
	 */
	boolean isFileContentExisted(String filePath, String line)
	{
		try
		{
			String[] lines = readFile(filePath);
			for (String line1 : lines) {
				if (line1.equals(line)) {
					return true;
				}
			}

			return false;
		}
		catch (Exception e)
		{
			DebugUtil.commonDebugWarn(
				"FileManager.isFileContentExisted() Catch exception ", e);
			return false;
		}
	}

	/**
	 * write corresponding value specified by key in the file named by filePath
	 *
	 * @param filePath -
	 * @param key
	 *            specified key
	 * @param value
	 *            corresponding value
	 * @param comment
	 *            comment about set property
	 * @return the previous value of the specified key in this property list, or
	 *         null if it did not have one. BeNoPermissionException when user
	 *         have no write permission
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 * @throws ClassCastException -
	 * @throws BeNoPermissionException -
	 */
	public Object writeFile(
		String filePath,
		String key,
		String value,
		String comment)
		throws IllegalArgumentException,
		IOException,
		ClassCastException,
		BeNoPermissionException
	{
		if (filePath == null || key == null || value == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		File file = new File(filePath);

		if (!file.exists())
		{
			throw new FileNotFoundException(file + " is not exist");
		}

		if (!file.canWrite())
		{
			throw new BeNoPermissionException("No permission to write " + file);
		}

		FileInputStream fis = new FileInputStream(file);
		Properties pro = new Properties();
		pro.load(fis);
		Object oldObj = pro.setProperty(key, value);
		fis.close();

		FileOutputStream fos = new FileOutputStream(file);
		pro.store(fos, comment);
		fos.close();

		return oldObj;
	}

	/**
	 * compress files of directory into zip file given by zipPath,will override
	 * if zip file exists
	 *
	 * @param dirPath -
	 * @param zipPath -
	 * @param comment
	 *            comment of zip file
	 * @return checksum of zip with Adler-32 arithmetic BeNoPermissionException
	 *         when user have no read permission
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public long directoryCompress(String dirPath, String zipPath, String comment)
		throws
		IllegalArgumentException,
		IOException,
		BeNoPermissionException
	{
		if (dirPath == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		File dir = new File(dirPath);
		if (!dir.exists())
		{
			throw new IllegalArgumentException("Invalid argument " + dirPath);
		}
		if (dir.isFile())
		{
			throw new IllegalArgumentException("Invalid argument " + dirPath);
		}

		List<File> fileList = getFilesFromFolder(dir, true);

		String[] fileArray = new String[fileList.size()];
		int index = 0;
		for (File file : fileList) {
			fileArray[index++] = file.getPath();
		}

		return fileCompress(fileArray, zipPath, comment);
	}

	/**
	 * compress files into zip file given by zipPath,will override if zip file
	 * exists
	 *
	 * @param files -
	 * @param zipPath -
	 * @param comment
	 *            comment of zip file
	 * @return checksum of zip with Adler-32 arithmetic
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 */
	public long fileCompress(String[] files, String zipPath, String comment)
		throws
		IllegalArgumentException,
		IOException
	{
		if (files == null || zipPath == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		FileOutputStream f = new FileOutputStream(zipPath);
		CheckedOutputStream csum = new CheckedOutputStream(f, new Adler32());
		ZipOutputStream out = new ZipOutputStream(
			new BufferedOutputStream(csum));
		out.setComment(comment);
		for (String file : files) {
			BufferedReader in = new BufferedReader(new FileReader(file));
			out.putNextEntry(new ZipEntry(file));
			int c;
			while ((c = in.read()) != -1)
				out.write(c);
			in.close();
		}
		out.close();
		// Checksum valid only after the file has been closed!
		return csum.getChecksum().getValue();
	}

	/**
	 * decompress zip file to destination directory given by dstpath
	 *
	 * @param zipPath -
	 * @param dstPath -
	 * @throws IOException -
	 * @throws IllegalArgumentException -
	 * @throws BeNoPermissionException -
	 */
	public void deCompress(String zipPath, String dstPath)
		throws IOException,
		IllegalArgumentException,
		BeNoPermissionException
	{
		if (zipPath == null || dstPath == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		ZipFile zfile = new ZipFile(zipPath);
		Enumeration<?> zList = zfile.entries();
		ZipEntry ze;
		byte[] buf = new byte[1024];
		while (zList.hasMoreElements())
		{
			// get ZipEntry for zip file
			ze = (ZipEntry) zList.nextElement();
			if (ze.isDirectory())
			{
				continue;
			}
			// get inputStream from ZipEntry and write to outputSteam
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
				createFilewithDir(dstPath, ze.getName(), new String[0])));
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readLen;
			while ((readLen = is.read(buf, 0, 1024)) != -1)
			{
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
			System.out.println("Extracted: " + ze.getName());
		}
		zfile.close();
	}

	/**
	 * create file, if parent directroy isn't exist,will be created too
	 *
	 * @param path -
	 * @param name -
	 * @param lines -
	 * @return -
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public File createFilewithDir(String path, String name, String[] lines)
		throws IllegalArgumentException,
		IOException,
		BeNoPermissionException
	{
		if (path == null || name == null || lines == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		File file = new File(path, name);

		boolean isSuccess = true;
		if (!file.exists())
		{
			File dir = new File(file.getParent());
			if (!dir.exists())
			{
				dir.mkdirs();
			}
			isSuccess = file.createNewFile();
		}

		// if the named file already exists and file can not be written, will
		// throw BeNoPermissionException
		if (!isSuccess && !file.canWrite())
		{
			throw new BeNoPermissionException("No permission to write " + file);
		}

		writeFile(file.getPath(), lines, false);

		return file;
	}

	public void writeFile(String pathname, String src) throws IOException {
		writeFile(pathname, src, false);
	}

	public void writeFile(String pathname, String src, boolean append) throws IOException {
		byte[] bytes = src.getBytes();
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		writeFile(pathname, bb, append);
	}

	public void writeFile(String pathname, ByteBuffer src) throws IOException {
		writeFile(pathname, src, false);
	}

	public void writeFile(String pathname, ByteBuffer src, boolean append) throws IOException {
		FileChannel fileChannel = null;

		try {
			fileChannel = new FileOutputStream(pathname, append).getChannel();
			fileChannel.write(src);
		} finally {
			if (fileChannel != null) {
				try {
					fileChannel.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

	/**
	 * if file exists,return true if file exists,false otherwise.
	 *
	 * @param filePath -
	 * @return -
	 */
	public boolean existsFile(String filePath)
	{
		return (new File(filePath)).exists();
	}

}