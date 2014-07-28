package com.ah.be.protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.ah.util.Tracer;

/**
 * FTP util class
 *@filename		AhFtpClient.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-5-7 02:03:26
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
public class AhFtpClient {

	private static final Tracer	log					= new Tracer(AhFtpClient.class.getSimpleName());

	private FTPClient			ftpClient			= new FTPClient();

	public static final int		BINARY_FILE_TYPE	= FTP.BINARY_FILE_TYPE;
	public static final int		ASCII_FILE_TYPE		= FTP.ASCII_FILE_TYPE;

	public void open(String server, int port, String userName, String password) throws Exception {
		try {
			ftpClient.connect(server, port);
			log.info("Connected to ftp server " + server + ",port " + port);
			log.info(ftpClient.getReplyString());

			int reply = ftpClient.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				throw new Exception("FTP server refused connection, reply="
						+ ftpClient.getReplyString());
			}

			if (!ftpClient.login(userName, password)) {
				ftpClient.logout();
				throw new Exception("FTP server refused login(username: " + userName
						+ ", password: " + password + ")");
			}

			// set as binary at first
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			
		} catch (Exception e) {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException f) {
					// do nothing
				}
			}

			log.error("open", "catch exception", e);
			throw e;
		}
	}

	public void setFileType(int fileType) {
		try {
			ftpClient.setFileType(fileType);
		} catch (Exception e) {
			log.error("setFileType", "catch exception", e);
		}
	}

	public boolean upload(String destFileName, String srcFileName) {
		InputStream srcInputStream = null;
		try {
			srcInputStream = new FileInputStream(srcFileName);
			if (destFileName.indexOf("/") != -1) {
				String path = destFileName.substring(0, destFileName.lastIndexOf("/"));
				ftpClient.makeDirectory(path);
			}
			return ftpClient.storeFile(destFileName, srcInputStream);
		} catch (Exception e) {
			log.error("upload", "catch exception", e);
			return false;
		} finally {
			if (srcInputStream != null) {
				try {
					srcInputStream.close();
				} catch (Exception e2) {
					// do nothing
				}
			}
		}
	}

	public boolean download(String srcFileName, String localName) throws Exception {

		// passive mode
		ftpClient.enterLocalPassiveMode();

		if (ftpClient.listFiles(srcFileName).length == 0) {
			throw new Exception("Cannot find source file (" + srcFileName + ")");
		}

		File localFile = new File(localName);
		FileOutputStream output = null;
		boolean flag;
		try {
			output = new FileOutputStream(localFile);
			flag = ftpClient.retrieveFile(srcFileName, output);
			output.close();
			if (flag) {
				log.info("File download successfully, file: " + localFile.getAbsolutePath());
			}
		} catch (Exception e) {
			throw new Exception("Unable to download file. " + e.getMessage());
		} finally {
			output.close();
		}

		return flag;
	}

	public void close() {
		try {
			ftpClient.disconnect();
		} catch (Exception e) {
			log.error("close", "catch exception", e);
		}
	}
}
