package com.ah.be.common.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Vector;

import com.ah.be.log.BeLogTools;

public class FileReadWriter {

	// static Test test=new Test();
	public static void main(String[] args) {
		// String testAddress="/home/feeling/test";
		// String domainAddress="/etc/resolv.conf";
		// String ipAddress="/etc/sysconfig/network-scripts/ifcfg-eth0";

		Vector<String> vct = readFile("/etc", "resolv.conf");
		if (vct != null) {
			for (Object o : vct) {
				System.out.println("value=" + o.toString());
			}
		}
		// writeFile(testAddress, pro);
		// File file=new File(domainAddress);
		// BufferedReader bf=null;
		// FileInputStream fis=null;
		// if(file.exists())
		// {
		// try
		// {
		// fis=new FileInputStream(file);
		// bf=new BufferedReader(new InputStreamReader(fis));
		// while(true){
		// String s=bf.readLine();
		// if(s==null)
		// break;
		// System.out.println(s);
		// }
		// }
		// catch(Exception e)
		// {
		// System.out.print(e.getMessage());
		// }
		// }

	}

	public static Vector<String> readFile(String path, String fileName) {
		File file;
		if (path == null || path.trim().equals("")) {
			file = new File(fileName);
		} else {
			file = new File(path + "/" + fileName);
		}
		BufferedReader bf;
		FileInputStream fis;
		Vector<String> vct = null;
		if (file.exists()) {
			try {
				fis = new FileInputStream(file);
				bf = new BufferedReader(new InputStreamReader(fis));
				String s;
				while ((s = bf.readLine()) != null) {
					if (vct == null) {
						vct = new Vector<String>();
					}
					vct.addElement(s);
				}
				bf.close();
				fis.close();
			} catch (Exception ex) {
				BeLogTools.commonLog(BeLogTools.ERROR, ex.getMessage());
			}
		}
		return vct;
	}

	public static void writeFile(String filePath, String[] args) {
		try {
			FileWriter fw = new FileWriter(filePath);
			PrintWriter out = new PrintWriter(fw);
			for (int i = 0; i < args.length; i++) {
				out.write(args[i]);
				out.println();
				out.flush();
			}
			fw.close();
			out.close();
		} catch (Exception ex) {
			BeLogTools.commonLog(BeLogTools.ERROR, ex.getMessage());
		}
	}

	public static void writeFile(String filePath, Vector<String> vct) {
		try {
			FileWriter fw = new FileWriter(filePath);
			PrintWriter out = new PrintWriter(fw);
			for (int i = 0; i < vct.size(); i++) {
				out.write(vct.get(i));
				out.println();
				out.flush();
			}
			fw.close();
			out.close();
		} catch (Exception ex) {
			BeLogTools.commonLog(BeLogTools.ERROR, ex.getMessage());
		}
	}

	public static Properties readFile(String filePath) {
		File file = new File(filePath);
		FileInputStream fis;
		Properties pro = null;
		if (file.exists()) {
			try {
				fis = new FileInputStream(file);
				pro = new Properties();
				pro.load(fis);
				fis.close();
			} catch (Exception ex) {
				BeLogTools.commonLog(BeLogTools.ERROR, ex.getMessage());
			}
		}
		return pro;
	}

	public static void writeFile(String filePath, Properties pro) {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(filePath);
			pro.store(fos, null);
			PrintStream p = new PrintStream(fos);
			p.close();
			fos.close();
		} catch (Exception ex) {
			BeLogTools.commonLog(BeLogTools.ERROR, ex.getMessage());
		}
	}

	public static void removeFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	}

	public static void createFile(String filePath) {

		File file = new File(filePath);
		if (file.exists()) {
			return;
		}
		try {
			file.createNewFile();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static boolean existsFile(String filePath) {
		return (new File(filePath)).exists();
	}

}