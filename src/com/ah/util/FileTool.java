package com.ah.util;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class FileTool {
	private static Logger	log	= Logger.getLogger("FileTool");

	public static int getFileLength(String filePath) throws Exception {
		File f = new File(filePath);
		if (f.length() > Integer.MAX_VALUE) {
			throw new Exception("file length > 2G.");
		}
		return (int) f.length();
	}

	public static void removeFile(String fileName) {
		File file = new File(fileName);
		if (!file.delete()) {
			log.error("delete " + fileName + " failed!");
		}
	}

	public static List<Object> readObjects(String fileName) {
		if (fileName == null) {
			return null;
		}

		File file = new File(fileName);

		if (!file.exists()) {
			return null;
		}

		List<Object> list = new ArrayList<Object>();
		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);

			Object temp = null;

			while ((temp = ois.readObject()) != null) {
				list.add(temp);
			}

			ois.close();
		} catch (EOFException eof) {

		} catch (Exception e) {
			log.error("read " + "error in reading file: " + fileName, e);
		} finally {
			if (ois != null) {
				try {
					ois.close();
					fis.close();
				} catch (IOException e) {
					log.error("read " + "error in closing file: " + fileName, e);
				}
			}
		}

		return list;
	}

	public static Object readObject(String fileName) {
		Object obj = null;
		if (fileName == null) {
			return null;
		}

		File file = new File(fileName);

		if (!file.exists()) {
			return null;
		}

		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);

			Object temp = null;

			if ((temp = ois.readObject()) != null) {
				obj = temp;
			}

			ois.close();
		} catch (EOFException eof) {

		} catch (Exception e) {
			log.error("read " + "error in reading file: " + fileName, e);
		} finally {
			if (ois != null) {
				try {
					ois.close();
					fis.close();
				} catch (IOException e) {
					log.error("read " + "error in closing file: " + fileName, e);
				}
			}
		}

		return obj;
	}

	public static void save(Object object, String fileName) {
		if (object == null || fileName == null) {
			return;
		}

		File file = new File(fileName);
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(file, false);

			if (!file.exists() || file.length() < 1) {
				oos = new ObjectOutputStream(fos);
			} else {
				oos = new ObjectOutputStream(fos);
			}

			oos.writeObject(object);
			oos.flush();
		} catch (FileNotFoundException e) {
			log.error("save" + " file: " + fileName + " is not found", e);
		} catch (IOException e) {
			log.error("save" + " error in saving map to file: " + fileName, e);
		} finally {
			if (oos != null) {
				try {
					oos.close();
					fos.close();
				} catch (IOException e) {
					log.error("read" + " error in closing file: " + fileName, e);
				}
			}
		}
	}

	public static String[] listFilenames(String dir, String regex) {
		File path = new File(dir);
		return path.list(new DirFilter(regex));
	}

	public static File[] listFiles(String dir, String regex) {
		File path = new File(dir);
		return path.listFiles(new DirFilter(regex));
	}
}

class DirFilter implements FilenameFilter {
	private Pattern	pattern;

	public DirFilter(String regex) {
		pattern = Pattern.compile(regex);
	}

	public boolean accept(File dir, String name) {
		return pattern.matcher(new File(name).getName()).matches();
	}
}