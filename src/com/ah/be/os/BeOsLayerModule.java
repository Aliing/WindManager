package com.ah.be.os;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipException;

/**
 *@filename		BeOsLayerModule.java
 *@version
 *@author		juyizhou
 *@createtime	2007-9-3 02:02:53
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
public interface BeOsLayerModule
{

	// os type definition
	public static final short	OS_WINDOWS			= 0;

	public static final short	OS_LINUX			= 1;

	// start NTP service on HM
	public static final short	START_NTP_SERVICE	= 1;

	// stop NTP service on HM
	public static final short	STOP_NTP_SERVICE	= 2;

	NetConfigImplInterface getNetworkService();

	ServerTimeInterface getTimeService();

	HiveManagerIntervalNTP getNTPService();

	/**
	 * Get the server time.
	 * 
	 * @return ServerTime -
	 */
	public Calendar getServerTime();

	/**
	 * Get the server timezone.
	 * 
	 * @return ServerTimeZone -
	 */
	public String getServerTimeZone();

	/**
	 * Get all timezone in the server.
	 * 
	 * @return TimeZone -
	 */
	public List<String> getAllTimeZone();

	/**
	 * Set up the server time.
	 *
	 * @param arg_Zone -
	 *            the time zone string
	 * @param arg_Date -
	 *            set time manually : date format is MMDDHHmmYYYY.ss
	 *            synchronize with server :"", 
	 * @param arg_Server -
	 *            synchronize with server :the NTP server and interval
	 *            set time manually :null
	 * @param arg_NTP -
	 *            BeOsLayerModule.START_NTP_SERVICE: start NTP service
	 *            BeOsLayerModule.STOP_NTP_SERVICE:stop NTP service
	 * @return String : "" if set successful, the error message if set failed
	 */
	public String setServerTime(
		String arg_Zone,
		String arg_Date,
		String[] arg_Server,
		int arg_NTP);

	/**
	 * Check if NTP Service is running on HM.
	 * 
	 * @return boolean
	 */
	public boolean ifNTPServiceStart();

	/**
	 * copy direcotry content
	 * 
	 * @param srcPath
	 *            the source path of to be copied directory
	 * @param dstPath
	 *            the destination path
	 * @return BeNoPermissionException when user have no read permission
	 */
	public void copyDirectory(String srcPath, String dstPath)
		throws IllegalArgumentException,
		FileNotFoundException,
		IOException,
		BeNoPermissionException;

	/**
	 * copy File
	 * 
	 * @param
	 * @return BeNoPermissionException when user have no read permission
	 */
	public void copyFile(String srcPath, String dstPath)
		throws IllegalArgumentException,
		FileNotFoundException,
		IOException,
		BeNoPermissionException;

	/**
	 * Creates the directory named given by dirPath
	 * 
	 * @param dirPath
	 * @return true if and only if the directory was created; false if already
	 *         exists
	 */
	public boolean createDirectory(String dirPath)
		throws IllegalArgumentException;

	/**
	 * Creates the file named by this abstract pathname.
	 * 
	 * @param filePath
	 * @param lines
	 * @return true if and only if the file was created; false if the named file
	 *         already exists. If have permission to write, content given by
	 *         lines will be written to this file. BeNoPermissionException when
	 *         user have no read permission
	 */
	public boolean createFile(String filePath, String[] lines)
		throws IllegalArgumentException,
		IOException,
		BeNoPermissionException;

	/**
	 * delete the directory named by this abstract pathname.
	 * 
	 * @param dirPath
	 * @return true if and only if the directory was deleted; false
	 *         otherwise(for example user have no write permission)
	 *         NullPointerException when user have no read permission
	 *         BeNoPermissionException when user have no read permission
	 */
	public boolean deleteDirectory(String dirPath)
		throws IllegalArgumentException,
		FileNotFoundException,
		NullPointerException,
		BeNoPermissionException;

	/**
	 * delete the file named by this abstract pathname.
	 * 
	 * @param filePath
	 * @return true if and only if the file was deleted; false otherwise
	 */
	public boolean deletefile(String filePath)
		throws IllegalArgumentException,
		FileNotFoundException;

	/**
	 * return an list of the files name in the directory denoted by this
	 * pathname
	 * 
	 * @param path
	 * @return BeNoPermissionException when user have no read permission
	 */
	public List<String> getFileNamesOfDirecotry(String path)
		throws FileNotFoundException,
		IllegalArgumentException,
		BeNoPermissionException;

	/**
	 * return an list of filelists in the directory given by a_dir
	 * 
	 * @param a_dir
	 * @param isRecur
	 *            if true will return files in sub-directories
	 * @return BeNoPermissionException when user have no read permission
	 */
	public List<File> getFilesFromFolder(File a_dir, boolean isRecur)
		throws FileNotFoundException,
		IllegalArgumentException,
		BeNoPermissionException;

	// CONST PARAM
	// use in getFileAndSubdirectoryNames() and getFilesAndSubdirectories()
	public static final short	BOTH			= 0;
	public static final short	ONLYFILE		= 1;
	public static final short	ONLYDIRECTORY	= 2;

	/**
	 * return an list of the files name in the directory denoted by this
	 * pathname
	 * 
	 * @param path
	 * @param isRecur
	 *            if true will return files/subdirs' names in sub-directories
	 * @param flag
	 *            get file or sub-directory or both <br>
	 *            0: both 1: only file 2: only directory
	 * @return BeNoPermissionException when user have no read permission
	 */
	public List<String> getFileAndSubdirectoryNames(
		String path,
		short flag,
		boolean isRecur)
		throws FileNotFoundException,
		IllegalArgumentException,
		BeNoPermissionException;

	/**
	 * return an list of filelists&subdirectories in the directory given by
	 * a_dir
	 * 
	 * @param a_dir
	 * @param isRecur
	 *            if true will return files/subdirs in sub-directories
	 * @param flag
	 *            get file or sub-directory or both <br>
	 *            0: both 1: only file 2: only directory
	 * @return BeNoPermissionException when user have no read permission
	 */
	public List<File> getFilesAndSubdirectories(
		File a_dir,
		short flag,
		boolean isRecur)
		throws FileNotFoundException,
		IllegalArgumentException,
		BeNoPermissionException;

	/**
	 * move file
	 * 
	 * @param
	 * @return
	 */
	public void moveFile(String srcPath, String dstPath)
		throws IllegalArgumentException,
		FileNotFoundException,
		IOException,
		BeNoPermissionException;

	/**
	 * move Directory
	 * 
	 * @param
	 * @return
	 */
	public void moveDirectory(String srcPath, String dstPath)
		throws IllegalArgumentException,
		FileNotFoundException,
		IOException,
		BeNoPermissionException;

	/**
	 * read contents of file named by filepath
	 * 
	 * @param filePath
	 * @return lines text of file BeNoPermissionException when user have no read
	 *         permission
	 */
	public String[] readFile(String filePath)
		throws IllegalArgumentException,
		FileNotFoundException,
		IOException,
		BeNoPermissionException;

	/**
	 * read corresponding value with key in the file named by filePath
	 * 
	 * @param filePath
	 * @param key
	 *            the specified key
	 * @return BeNoPermissionException when user have no read permission
	 */
	public String readFile(String filePath, String key)
		throws IllegalArgumentException,
		FileNotFoundException,
		IOException,
		BeNoPermissionException;

	/**
	 * write contents to file named by filepath
	 * 
	 * @param filePath
	 * @param lines
	 * @param append
	 *            boolean if <code>true</code>, then data will be written to
	 *            the end of the file rather than the beginning.
	 * @return true if and only if the write executed success; false otherwise
	 *         BeNoPermissionException when user have no write permission
	 */
	public void writeFile(String filePath, String[] lines, boolean append)
		throws IllegalArgumentException,
		IOException,
		FileNotFoundException,
		BeNoPermissionException;

	/**
	 * write corresponding value specified by key in the file named by filePath
	 * 
	 * @param filePath
	 * @param key
	 *            specified key
	 * @param value
	 *            corresponding value
	 * @param comment
	 *            comment about set property
	 * @return the previous value of the specified key in this property list, or
	 *         null if it did not have one. BeNoPermissionException when user
	 *         have no write permission
	 */
	public Object writeFile(
		String filePath,
		String key,
		String value,
		String comment)
		throws IllegalArgumentException,
		FileNotFoundException,
		IOException,
		ClassCastException,
		BeNoPermissionException;

	/**
	 * check whether the file is exist.
	 */
	public boolean isFileExist(String filePath);

	/**
	 * compress files of directory into zip file given by zipPath,will override
	 * if zip file exists
	 * 
	 * @param dirPath
	 * @param zipPath
	 * @param comment
	 *            comment of zip file
	 * @return checksum of zip with Adler-32 arithmetic BeNoPermissionException
	 *         when user have no read permission
	 */
	public long directoryCompress(String dirPath, String zipPath, String comment)
		throws FileNotFoundException,
		IllegalArgumentException,
		IOException,
		BeNoPermissionException;

	/**
	 * compress files into zip file given by zipPath,will override if zip file
	 * exists
	 * 
	 * @param files
	 * @param zipPath
	 * @param comment
	 *            comment of zip file
	 * @return checksum of zip with Adler-32 arithmetic
	 */
	public long fileCompress(String[] files, String zipPath, String comment)
		throws FileNotFoundException,
		IllegalArgumentException,
		IOException;

	/**
	 * decompress zip file to destination directory given by dstpath
	 * 
	 * @param zipPath
	 * @param dstPath
	 * @return
	 */
	public void deCompress(String zipPath, String dstPath)
		throws IOException,
		IllegalArgumentException,
		ZipException,
		BeNoPermissionException;

	/**
	 * exec shell command
	 * 
	 * @param cmd
	 * @param nanos
	 * @return error message if existed, or return null
	 */
	public String execCommand(String cmd, int nanos)
		throws IOException,
		IllegalArgumentException;

	/**
	 * reboot device
	 * 
	 * @param
	 * @return
	 */
	public void reboot() throws IOException, IllegalArgumentException;

	/**
	 * get net config data
	 * 
	 * @param
	 * @return data object in type NetConfigureDTO
	 */
	public NetConfigureDTO getNetConfig();

	/**
	 * update current net config
	 * 
	 * @param
	 * @return
	 */
	public void updateNetConfig(NetConfigureDTO dto)
		throws IllegalArgumentException,
		IOException,
		BeNoPermissionException;

	/**
	 * get route info of eth0,eth1
	 * 
	 * @param
	 * @return result message
	 */
	public Vector<Vector<String>> getRoute();

	/**
	 * add route to os & save to file
	 * 
	 * @param routeInfo
	 *            value: [destination,netmask,gateway]
	 * @return wrong message outputted by execute shell, return null when
	 *         succeed
	 */
	public String addRoute(String[] routeInfo);

	/**
	 * remove route from os & save result to file
	 * 
	 * @param delRouteInfos
	 *            item: [destination,netmask,gateway]
	 * @return wrong message outputted by execute shell, return null when
	 *         succeed
	 */
	public String removeRoute(List<String[]> delRouteInfos);

//	/**
//	 * update route config , route info given by arg_Route
//	 * 
//	 * @param arg_Route
//	 * @return
//	 */
//	public Vector<String> updateRouteConfig(Vector<Vector<String>> arg_Route);

	/**
	 * get Hivemanager eth ip address, if both eth active, return second
	 * 
	 * @param
	 * @return
	 */
	public String getHiveManagerIPAddr();

	/**
	 * get HiveManager eth netmask if both eth active, return second
	 * 
	 * @param
	 * @return
	 */
	public String getHiveManagerNetmask();

	/**
	 * refresh shell route config,because routes config will be clear when
	 * restart network.
	 * 
	 * @param
	 * @return
	 */
	public void refreshShellRouteConfig();

	/**
	 * create file, if parent directroy isn't exist,will be created too
	 * 
	 * @param
	 * @return
	 */
	public File createFilewithDir(String path, String name, String[] lines)
		throws IllegalArgumentException,
		IOException,
		FileNotFoundException,
		BeNoPermissionException;

	/**
	 * get hivemanager path
	 * 
	 * @param
	 * @return
	 */
	public String getHiveManagerPath();

//	/**
//	 * get capwap server name
//	 * 
//	 * @param
//	 * @return
//	 */
//	public String getCAPWAPServerName();

	/**
	 * get os hostname
	 * 
	 * @param
	 * @return
	 */
	public String getHostName();
	
	/**
	 * set cache hostname
	 * 
	 * @param
	 * @return
	 */
	public void  setLocalHostName(String newHostName);

	/**
	 * get HiveManager up time
	 * 
	 * @param
	 * @return, time unit is seconds.
	 */
	public long getStartupTime();
	
	/**
	 * get enable of Lan port
	 * 
	 * @param
	 * @return
	 */
	public boolean getEnable_Eth1();
	
	/**
	 * refresh route info for domain remove operation<br>
	 * when domain removed, we need remove corresponding route
	 * 
	 * @param
	 * @return
	 */
	public void refreshRouteInfo4Domain(String domainIP, String domainMask);
	
	/**
	 * return subnet display from ip & mask
	 * 
	 * @param ip:
	 *            format "192.168.1.2"
	 * @param netmask:
	 *            format "255.255.255.0"
	 * 
	 * @return format "192.168.1.0"
	 */
	public String parseSubnet(String ip, String netmask);
	
	/**
	 * get MGT interface IP
	 * 
	 * @param
	 * @return
	 */
	public String getIP_eth0();
	
	/**
	 * get MGT interface's netmask
	 * 
	 * @param
	 * @return
	 */
	public String getNetmask_eth0();
	
	/**
	 * get LAN interface IP
	 * 
	 * @param
	 * @return
	 */
	public String getIP_eth1();
	
	/**
	 * get LAN interface's netmask
	 * 
	 * @param
	 * @return
	 */
	public String getNetmask_eth1();
	
	/**
	 * update dns configuration only
	 *
	 *@param 
	 *
	 *@return
	 */
	public void updateDNSConfiguration(String primaryDns, String secondDns, String tertiaryDns)
			throws Exception;


	/**
     * Returns the hardware address (usually MAC) of the interface if it
     * has one and if it can be accessed given the current privileges.
     *
     * @return	a string of mac address or <code>null</code> if
     *		the mac address doesn't exist or is not accessible.
	 * @throws SocketException if an I/O error occurs.
	 */
	String getMacAddress() throws SocketException;

}