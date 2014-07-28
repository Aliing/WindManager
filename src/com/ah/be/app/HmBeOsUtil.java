package com.ah.be.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import com.ah.be.os.BeNoPermissionException;
import com.ah.be.os.BeOSLayerModuleImpl;
import com.ah.be.os.BeOsLayerModule;
import com.ah.be.os.LinuxServerTimeManager;
import com.ah.be.os.NetConfigureDTO;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.EnumItem;

/**
 *@filename		HmBeOsUtil.java
 *@version
 *@author		Steven
 *@createtime	2007-9-5 04:20:30
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
public class HmBeOsUtil
{
	public static String os = System.getProperty("os.name");
	
	public static BeOsLayerModule module_Os = new BeOSLayerModuleImpl(os.toLowerCase().contains("windows") ? 
			BeOsLayerModule.OS_WINDOWS : BeOsLayerModule.OS_LINUX);
	
	public static List<String> HIVEMANAGER_ALL_TIMEZONE = null;
	
	public static EnumItem[] HIVEMANAGER_ALL_ENUM_TIMEZONE = null;
	
	public static String[] HIVEMANAGER_UNSUPPORT_TIMEZONE = new String[]{
		"Pacific/Apia",
		//"Pacific/Midway", support in MyHive, user is using it
		"Pacific/Pago_Pago",
		"Pacific/Samoa",
		"US/Samoa",
		"America/Adak",
		"America/Atka",
		"US/Aleutian",
		"America/Bahia_Banderas",
		"America/Guayaquil",
		"America/Argentina/San_Luis",
		"America/Paramaribo",
		"Antarctica/Rothera",
		"Atlantic/South_Georgia",
		"Atlantic/Cape_Verde",
		"Africa/Blantyre",
		"Africa/Bujumbura",
		"Africa/Gaborone",
		"Africa/Harare",
		"Africa/Kigali",
		"Africa/Lubumbashi",
		"Africa/Lusaka",
		"Africa/Maputo",
		"Antarctica/Syowa",
		"Europe/Samara",
		"Europe/Volgograd",
		"Asia/Riyadh87",
		"Asia/Riyadh88",
		"Asia/Riyadh89",
		"Mideast/Riyadh87",
		"Mideast/Riyadh88",
		"Mideast/Riyadh89",
		"Asia/Tehran",
		"Iran",
		//"Asia/Dubai", support in MyHive, user is using it
		//"Asia/Muscat", support in MyHive, user is using it
		"Asia/Aqtau",
		"Asia/Aqtobe",
		"Asia/Oral",
		"Antarctica/Vostok",
		"Asia/Qyzylorda",
		"Asia/Hovd",
		//"Asia/Jakarta", support in MyHive, user is using it
		"Asia/Pontianak",
		"Asia/Choibalsan",
		"Asia/Makassar",
		"Asia/Ujung_Pandang",
		//"Australia/Perth", support in MyHive, user is using it
		"Australia/West",
		"Australia/Eucla",
		"Asia/Dili",
		"Asia/Jayapura",
		"Asia/Sakhalin",
		"Pacific/Chuuk",
		"Pacific/Guam",
		"Pacific/Saipan",
		"Antarctica/Macquarie",
		"Pacific/Guadalcanal",
		"Pacific/Noumea",
		"Pacific/Pohnpei",
		"Pacific/Nauru"
	};

	/**
	 * get ha scripts file path
	 *
	 *@param 
	 *
	 *@return
	 */
	public static final String	getHAScriptsPath()
	{
		if (os.toLowerCase().contains("windows"))
		{
			//
			return ".\\";
		}
		else
			if (os.toLowerCase().contains("linux"))
			{
				return "/HiveManager/ha/scripts/";
			}
		
		return "/HiveManager/ha/scripts/";
	}
	
	/**
	 * Get all timezone in the server.
	 * 
	 * @return TimeZone -
	 * @throws RemoteException -
	 */
	public static List<String> getAllTimeZone()
	{
		if (null == HIVEMANAGER_ALL_TIMEZONE) {
			if (null != AhAppContainer.HmBe) {
				HIVEMANAGER_ALL_TIMEZONE = AhAppContainer.HmBe.getOsModule().getAllTimeZone();
			} else {
				HIVEMANAGER_ALL_TIMEZONE = module_Os.getAllTimeZone();
			}
		}
		return HIVEMANAGER_ALL_TIMEZONE;
	}

	/**
	 * Get the server time.
	 * 
	 * @return ServerTime -
	 * @throws RemoteException -
	 */
	public static Calendar getServerTime()
	{
		if (null != AhAppContainer.HmBe) {
			return AhAppContainer.HmBe.getOsModule().getServerTime();
		} else {
			return module_Os.getServerTime();
		}
	}

	/**
	 * Get the server timezone.
	 * 
	 * @return ServerTimeZone -
	 * @throws RemoteException -
	 */
	public static String getServerTimeZone()
	{
		if (null != AhAppContainer.HmBe) {
			return AhAppContainer.HmBe.getOsModule().getServerTimeZone();
		} else {
			return module_Os.getServerTimeZone();
		}
	}
	
	/**
	 * Get the index of current time zone in comboBox
	 * @param the string of time zone
	 * @return int
	 */
	public static int getServerTimeZoneIndex(String arg_TimeZone) {
		arg_TimeZone = (null == arg_TimeZone || "".equals(arg_TimeZone)) ? getServerTimeZone() : arg_TimeZone;

		// set current timezone
		for (EnumItem enumItem : getEnumsTimeZone()) {
			String zone = enumItem.getValue();
			if (arg_TimeZone.equals(zone.substring(zone.indexOf(" ") + 1))) {
				return enumItem.getKey();
			}
		}
		return 0;
	}
	
	/**
	 * Get the current time zone in comboBox from old one, because remove some timezone from 4.0r1
	 *
	 *@param old index
	 *@return time zone string
	 */
	public static String getNewTimeZoneByOldOne(int arg_Index) {
		LinuxServerTimeManager osTimeZone = new LinuxServerTimeManager();
		List<String> oldTimeZones = osTimeZone.getAllTimeZoneOldOne();
		if (25 < arg_Index && arg_Index < 44) {
			arg_Index += 1;
		} else if (43 < arg_Index && arg_Index < 64) {
			arg_Index += 2;
		} else if (63 < arg_Index && arg_Index < 429) {
			arg_Index += 3;
		} else if (428 < arg_Index && arg_Index < 439) {
			arg_Index += 4;
		} else if (438 < arg_Index && arg_Index < 503) {
			arg_Index += 5;
		} else if (503 == arg_Index) {
			arg_Index = 443;
		} else if (503 < arg_Index && arg_Index < 513) {
			arg_Index += 6;
		} else if (513 == arg_Index || 514 == arg_Index) {
			arg_Index -= 5;
		} else if (514 < arg_Index) {
			arg_Index += 4;
		}
		
		if (null != oldTimeZones && arg_Index < oldTimeZones.size()) {
			String oneZone = oldTimeZones.get(arg_Index);
			
			String shortTimeZone = oneZone.substring(oneZone.indexOf(" ") + 1);
			
			// remove the unsupport timezone for HA
			overloop:
			for (String timeZone : HmBeOsUtil.HIVEMANAGER_UNSUPPORT_TIMEZONE) {
				if (timeZone.equalsIgnoreCase(shortTimeZone)) {
					String tzOffStr = oneZone.substring(0, oneZone.indexOf(" "));
					
					// set current timezone
					for (EnumItem enumItem : getEnumsTimeZone()) {
						String zone = enumItem.getValue();
						if (tzOffStr.equals(zone.substring(0, zone.indexOf(" ")))) {
							shortTimeZone = zone.substring(zone.indexOf(" ") + 1);
							break overloop;
						}
					}
					break;
				}
			}
			return shortTimeZone;
		}
		return "America/Los_Angeles";
	}
	
	/**
	 * Get the comboBox objects of time zone
	 * @return EnumItem[]
	 */
	public static EnumItem[] getEnumsTimeZone() {
		if (null == HIVEMANAGER_ALL_ENUM_TIMEZONE) {
			// call be function
			List<String> timeZones = getAllTimeZone();
			if (timeZones == null) {
				HIVEMANAGER_ALL_ENUM_TIMEZONE = new EnumItem[0];
			} else {
				HIVEMANAGER_ALL_ENUM_TIMEZONE = new EnumItem[timeZones.size()];
				int index = 0;
				for (String timeZone : timeZones) {
					HIVEMANAGER_ALL_ENUM_TIMEZONE[index] = new EnumItem(index, timeZone);
					index++;
				}
			}
		}
		return HIVEMANAGER_ALL_ENUM_TIMEZONE;
	}
	
	/**
	 * Get the off set of this time zone with an index
	 * @param arg_Index
	 * @return String
	 */
	public static String getTimeZoneOffSet(String arg_Zone) {
		String timeZoneString = getTimeZoneWholeStr(arg_Zone);
		if (timeZoneString.startsWith("(GMT)")) {
			return "0:00";
		} else if (timeZoneString.charAt(4) == '+') {
			return timeZoneString.substring(timeZoneString.indexOf('+') + 1, timeZoneString.indexOf(')'));
		} else {
			return timeZoneString.substring(timeZoneString.indexOf('T') + 1, timeZoneString.indexOf(')'));
		}
	}
	
	public static String getTimeZoneWholeStr(String short_One) {
		if (null != short_One && !"".equals(short_One)) {
			for (EnumItem enumItem : getEnumsTimeZone()) {
				String zone = enumItem.getValue();
				if (short_One.equals(zone.substring(zone.indexOf(" ") + 1))) {
					return enumItem.getValue();
				}
			}
		}
		return "(GMT-08:00) America/Los_Angeles";
	}
	
	/**
	 * check whether or not ip1 and ip2 are in same subnet
	 * 
	 * @param ip1 -
	 * @param netmask1 -
	 * @param ip2 -
	 * @param netmask2 -
	 * @return -
	 */
	public static boolean isInSameSubnet(String ip1, String ip2, String netmask) {
		String[] ipArray1 = ip1.split("\\.");
		String[] maskArray = netmask.split("\\.");
		String[] subnet1 = new String[4];
		String[] ipArray2 = ip2.split("\\.");
		String[] subnet2 = new String[4];

		for (int i = 0; i < ipArray1.length; i++) {
			subnet1[i] = String.valueOf(Integer.valueOf(ipArray1[i])
					& Integer.valueOf(maskArray[i]));
		}

		for (int i = 0; i < ipArray2.length; i++) {
			subnet2[i] = String.valueOf(Integer.valueOf(ipArray2[i])
					& Integer.valueOf(maskArray[i]));
		}

		for (int i = 0; i < subnet1.length; i++) {
			if (!subnet1[i].equals(subnet2[i])) {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * Get the time zone string with an index
	 * @param arg_Index
	 * @return String
	 */
	public static String getTimeZoneString(int arg_Index) {
		if (arg_Index >= 0 && arg_Index < getEnumsTimeZone().length) {
			String timeZoneString = getEnumsTimeZone()[arg_Index].getValue();
			return timeZoneString.substring(timeZoneString.indexOf(" ")+1);
		}
		return "America/Los_Angeles";
	}
	
	/**
	 * Get the time zone string in the time zone drop down list with an index
	 * @param arg_Index
	 * @return String
	 */
	public static String getTimeZoneStringWhole(int arg_Index) {
		if (arg_Index >= 0 && arg_Index < getEnumsTimeZone().length) {
			String[] timeZoneString = getEnumsTimeZone()[arg_Index].getValue().split(" ");
			return timeZoneString[1]+" "+(timeZoneString[0].substring(1, timeZoneString[0].length()-1));
		}
		return "America/Los_Angeles GMT -08:00";
	}
	
	/**
	 * Get the off set of the time zone by string
	 * @param arg_TimeZone
	 * @return int
	 */
//	public static int getTimeZoneOffSet(String arg_TimeZone) {
//		arg_TimeZone = (null == arg_TimeZone || "".equals(arg_TimeZone)) ? getServerTimeZone() : arg_TimeZone;
//		return TimeZone.getTimeZone(arg_TimeZone).getRawOffset() / 3600000;
//	}

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
	public static String setServerTime(
		String arg_Zone,
		String arg_Date,
		String[] arg_Server,
		int arg_NTP)
	{
		return AhAppContainer.HmBe.getOsModule().setServerTime(arg_Zone,
			arg_Date, arg_Server, arg_NTP);
	}

	/**
	 * Check if NTP Service is running on HM.
	 * 
	 * @return boolean
	 */
	public static boolean ifNTPServiceStart()
	{
		return AhAppContainer.HmBe.getOsModule().ifNTPServiceStart();
	}

	/**
	 * copy direcotry content,include file&directory
	 * 
	 * @param srcPath
	 *            the source path of to be copied directory
	 * @param dstPath
	 *            the destination path
	 * @return
	 * @exception IllegalArgumentException
	 * @exception FileNotFoundException
	 * @exception IOException
	 * @exception BeNoPermissionException
	 */
	public static void copyDirectory(String srcPath, String dstPath)
		throws IllegalArgumentException,
		FileNotFoundException,
		IOException,
		BeNoPermissionException
	{
		AhAppContainer.HmBe.getOsModule().copyDirectory(srcPath, dstPath);
	}

	/**
	 * copy File
	 * 
	 * @param
	 * @return
	 * @exception FileNotFoundException
	 * @exception IOException
	 * @exception BeNoPermissionException
	 */
	public static void copyFile(String srcPath, String dstPath)
		throws IllegalArgumentException,
		FileNotFoundException,
		IOException,
		BeNoPermissionException
	{
		AhAppContainer.HmBe.getOsModule().copyFile(srcPath, dstPath);
	}

	/**
	 * Creates the directory named given by dirPath
	 * 
	 * @param dirPath
	 * @return true if and only if the directory was created; false if already
	 *         exists
	 * @exception IllegalArgumentException
	 */
	public static boolean createDirectory(String dirPath)
		throws IllegalArgumentException
	{
		return AhAppContainer.HmBe.getOsModule().createDirectory(dirPath);
	}

	/**
	 * Creates the file named by this abstract pathname.
	 * 
	 * @param filePath
	 * @return true if and only if the file was created; false if the named file
	 *         already exists
	 * @exception IllegalArgumentException
	 * @exception IOException
	 * @exception BeNoPermissionException
	 */
	public static boolean createFile(String filePath, String[] lines)
		throws IllegalArgumentException,
		IOException,
		BeNoPermissionException
	{
		return AhAppContainer.HmBe.getOsModule().createFile(filePath, lines);
	}

	/**
	 * create file, if parent directroy isn't exist,will be created too
	 * 
	 * @param path
	 * @param name
	 * @param lines,
	 *            new String[0] if file empty
	 * @return
	 */
	public File createFilewithDir(String path, String name, String[] lines)
		throws IllegalArgumentException,
		IOException,
		FileNotFoundException,
		BeNoPermissionException
	{
		return AhAppContainer.HmBe.getOsModule().createFilewithDir(path, name,
			lines);
	}

	/**
	 * delete the directory named by this abstract pathname.
	 * 
	 * @param dirPath
	 * @return true if and only if the directory was deleted; false otherwise
	 * @exception IllegalArgumentException
	 * @exception FileNotFoundException
	 * @exception BeNoPermissionException
	 */
	public static boolean deleteDirectory(String dirPath)
		throws IllegalArgumentException,
		FileNotFoundException,
		BeNoPermissionException
	{
		return AhAppContainer.HmBe.getOsModule().deleteDirectory(dirPath);
	}

	/**
	 * delete the file named by this abstract pathname.
	 * 
	 * @param filePath
	 * @return true if and only if the file was deleted; false otherwise
	 * @exception IllegalArgumentException
	 * @exception FileNotFoundException
	 */
	public static boolean deletefile(String filePath)
		throws IllegalArgumentException,
		FileNotFoundException
	{
		return AhAppContainer.HmBe.getOsModule().deletefile(filePath);
	}

	/**
	 * return an list of the files name in the directory denoted by this
	 * pathname
	 * 
	 * @param path
	 * @return list of file name
	 * @exception IllegalArgumentException
	 * @exception FileNotFoundException
	 * @exception BeNoPermissionException
	 */
	public static List<String> getFileNamesOfDirecotry(String path)
		throws FileNotFoundException,
		IllegalArgumentException,
		BeNoPermissionException
	{
		return AhAppContainer.HmBe.getOsModule().getFileNamesOfDirecotry(path);
	}

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
	 * @return list of file/diretory name
	 * @exception IllegalArgumentException
	 * @exception FileNotFoundException
	 * @exception BeNoPermissionException
	 *                when user have no read permission
	 */
	public static List<String> getFileAndSubdirectoryNames(
		String path,
		short flag,
		boolean isRecur)
		throws FileNotFoundException,
		IllegalArgumentException,
		BeNoPermissionException
	{
		return AhAppContainer.HmBe.getOsModule().getFileAndSubdirectoryNames(
			path, flag, isRecur);
	}

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
	 * @return list of file
	 * @exception IllegalArgumentException
	 * @exception FileNotFoundException
	 * @exception BeNoPermissionException
	 *                when user have no read permission
	 */
	public static List<File> getFilesAndSubdirectories(
		File a_dir,
		short flag,
		boolean isRecur)
		throws FileNotFoundException,
		IllegalArgumentException,
		BeNoPermissionException
	{
		return AhAppContainer.HmBe.getOsModule().getFilesAndSubdirectories(
			a_dir, flag, isRecur);
	}

	/**
	 * move file
	 * 
	 * @param
	 * @return
	 * @exception IOException
	 * @exception FileNotFoundException
	 * @exception BeNoPermissionException
	 *                when user have no read permission
	 */
	public static void moveFile(String srcPath, String dstPath)
		throws IllegalArgumentException,
		FileNotFoundException,
		IOException,
		BeNoPermissionException
	{
		AhAppContainer.HmBe.getOsModule().moveFile(srcPath, dstPath);
	}

	/**
	 * move Directory
	 * 
	 * @param
	 * @return
	 * @exception IOException
	 * @exception FileNotFoundException
	 * @exception BeNoPermissionException
	 *                when user have no read permission
	 */
	public static void moveDirectory(String srcPath, String dstPath)
		throws IllegalArgumentException,
		FileNotFoundException,
		IOException,
		BeNoPermissionException
	{
		AhAppContainer.HmBe.getOsModule().moveDirectory(srcPath, dstPath);
	}

	/**
	 * read contents of file named by filepath
	 * 
	 * @param filePath
	 * @return lines text of file
	 * @exception IOException
	 * @exception FileNotFoundException
	 * @exception BeNoPermissionException
	 *                when user have no read permission
	 */
	public static String[] readFile(String filePath)
		throws IllegalArgumentException,
		FileNotFoundException,
		IOException,
		BeNoPermissionException
	{
		return AhAppContainer.HmBe.getOsModule().readFile(filePath);
	}

	/**
	 * read corresponding value with key in the file named by filePath
	 * 
	 * @param filePath
	 * @param key
	 *            the specified key
	 * @return value
	 * @exception IOException
	 * @exception FileNotFoundException
	 * @exception BeNoPermissionException
	 *                when user have no read permission
	 */
	public static String readFile(String filePath, String key)
		throws IllegalArgumentException,
		FileNotFoundException,
		IOException,
		BeNoPermissionException
	{
		return AhAppContainer.HmBe.getOsModule().readFile(filePath, key);
	}

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
	public static void writeFile(String filePath, String[] lines, boolean append)
		throws IllegalArgumentException,
		IOException,
		FileNotFoundException,
		BeNoPermissionException
	{
		AhAppContainer.HmBe.getOsModule().writeFile(filePath, lines, append);
	}

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
	 *         null if it did not have one.
	 * @exception IOException
	 * @exception FileNotFoundException
	 * @exception ClassCastException
	 * @exception BeNoPermissionException
	 *                when user have no read permission
	 */
	public static Object writeFile(
		String filePath,
		String key,
		String value,
		String comment)
		throws IllegalArgumentException,
		FileNotFoundException,
		IOException,
		ClassCastException,
		BeNoPermissionException
	{
		return AhAppContainer.HmBe.getOsModule().writeFile(filePath, key,
			value, comment);
	}

	/**
	 * check whether the file is exist.
	 */
	public static boolean isFileExist(String filePath)
	{
		return AhAppContainer.HmBe.getOsModule().isFileExist(filePath);
	}

	/**
	 * get net config data
	 * 
	 * @param
	 * @return data object in type NetConfigureDTO
	 */
	public static NetConfigureDTO getNetConfig()
	{
		return AhAppContainer.HmBe.getOsModule().getNetConfig();
	}
	
	/**
	 * set cache hostName
	 *
	 * @param
	 *
	 */
	public static void setLocalHostName(String newHostName)
	{
		AhAppContainer.HmBe.getOsModule().setLocalHostName(newHostName);
	}

	/**
	 * update current net config
	 * 
	 * @param
	 * @return
	 * @exception IOException
	 * @exception IllegalArgumentException
	 */
	public static void updateNetConfig(NetConfigureDTO dto)
		throws IllegalArgumentException,
		IOException,
		BeNoPermissionException
	{
		AhAppContainer.HmBe.getOsModule().updateNetConfig(dto);
	}

	/**
	 * get route info of eth0,eth1
	 * 
	 * @param
	 * @return result message
	 */
	public static Vector<Vector<String>> getRoute()
	{
		return AhAppContainer.HmBe.getOsModule().getRoute();
	}

//	/**
//	 * update route config , route info given by arg_Route
//	 * 
//	 * @param arg_Route
//	 * @return
//	 */
//	public static Vector<String> updateRouteConfig(
//		Vector<Vector<String>> arg_Route)
//	{
//		return AhAppContainer.HmBe.getOsModule().updateRouteConfig(arg_Route);
//	}

	/**
	 * add route to os & save to file
	 * 
	 * @param routeInfo
	 *            value: [destination,netmask,gateway]
	 * @return wrong message outputted by execute shell, return null when
	 *         succeed
	 */
	public static String addRoute(String[] routeInfo)
	{
		return AhAppContainer.HmBe.getOsModule().addRoute(routeInfo);
	}

	/**
	 * remove route from os & save result to file
	 * 
	 * @param delRouteInfos
	 *            item: [destination,netmask,gateway]
	 * @return wrong message outputted by execute shell, return null when
	 *         succeed
	 */
	public static String removeRoute(List<String[]> delRouteInfos)
	{
		return AhAppContainer.HmBe.getOsModule().removeRoute(delRouteInfos);
	}

	/**
	 * get Hivemanager eth ip address, if both eth active, return second
	 * 
	 * @param
	 * @return
	 */
	public static String getHiveManagerIPAddr()
	{
		return AhAppContainer.HmBe.getOsModule().getHiveManagerIPAddr();
	}

	/**
	 * get HiveManager eth netmask if both eth active, return second
	 * 
	 * @param
	 * @return
	 */
	public static String getHiveManagerNetmask()
	{
		return AhAppContainer.HmBe.getOsModule().getHiveManagerNetmask();
	}

	/**
	 * return an list of filelists in the directory given by a_dir
	 * 
	 * @param a_dir
	 * @param isRecur
	 *            if true will return files in sub-directories
	 * @return
	 */
	public static List<File> getFilesFromFolder(File a_dir, boolean isRecur)
		throws FileNotFoundException,
		IllegalArgumentException,
		BeNoPermissionException
	{
		return AhAppContainer.HmBe.getOsModule().getFilesFromFolder(a_dir,
			isRecur);
	}

	/**
	 * refresh shell route config,because routes config will be clear when
	 * restart network.
	 * 
	 * @param
	 * @return
	 */
	public static void refreshShellRouteConfig()
	{
		AhAppContainer.HmBe.getOsModule().refreshShellRouteConfig();
	}

	/**
	 * get hivemanager path
	 * 
	 * @param
	 * @return
	 */
	public static String getHiveManagerPath()
	{
		return AhAppContainer.HmBe.getOsModule().getHiveManagerPath();
	}

//	/**
//	 * get capwap server name
//	 * 
//	 * @param
//	 * @return
//	 */
//	public static String getCAPWAPServerName()
//	{
//		return AhAppContainer.HmBe.getOsModule().getCAPWAPServerName();
//	}

	/**
	 * get os hostname
	 * 
	 * @param
	 * @return
	 */
	public static String getHostName()
	{
		return AhAppContainer.HmBe.getOsModule().getHostName();
	}

	/**
	 * get HiveManager up time
	 * 
	 * @param
	 * @return, time unit is seconds.
	 */
	public static long getStartupTime()
	{
		return AhAppContainer.HmBe.getOsModule().getStartupTime();
	}

	/**
	 * exec shell command
	 * 
	 * @param cmd
	 * @return error message if existed, or return null
	 */
	public static String execCommand(String cmd)
		throws IOException,
		IllegalArgumentException
	{
		return AhAppContainer.HmBe.getOsModule().execCommand(cmd, 0);
	}
	
	/**
	 * exec shell command
	 * 
	 * @param cmd
	 * @param nanos
	 * @return error message if existed, or return null
	 */
	public static String execCommand(String cmd, int nanos)
		throws IOException,
		IllegalArgumentException
	{
		return AhAppContainer.HmBe.getOsModule().execCommand(cmd, nanos);
	}
	
	/**
	 * get enable of Lan port
	 * 
	 * @param
	 * @return
	 */
	public static boolean getEnable_Eth1()
	{
		return AhAppContainer.HmBe.getOsModule().getEnable_Eth1();
	}
	
	/**
	 * refresh route info for domain remove operation<br>
	 * when domain removed, we need remove corresponding route
	 * 
	 * @param
	 * @return
	 */
	public static void refreshRouteInfo4Domain(String domainIP, String domainMask)
	{
		AhAppContainer.HmBe.getOsModule().refreshRouteInfo4Domain(domainIP, domainMask);
	}
	
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
	public static String parseSubnet(String ip, String netmask)
	{
		return AhAppContainer.HmBe.getOsModule().parseSubnet(ip, netmask);
	}
	
	/**
	 * get MGT interface IP
	 * 
	 * @param
	 * @return
	 */
	public static String getIP_eth0()
	{
		return AhAppContainer.HmBe.getOsModule().getIP_eth0();
	}
	
	/**
	 * get MGT interface netmask
	 * 
	 * @param
	 * @return
	 */
	public static String getNetmask_eth0()
	{
		return AhAppContainer.HmBe.getOsModule().getNetmask_eth0();
	}
	
	/**
	 * get LAN interface IP
	 * 
	 * @param
	 * @return
	 */
	public static String getIP_eth1()
	{
		return AhAppContainer.HmBe.getOsModule().getIP_eth1();
	}
	
	/**
	 * get LAN interface netmask
	 * 
	 * @param
	 * @return
	 */
	public static String getNetmask_eth1()
	{
		return AhAppContainer.HmBe.getOsModule().getNetmask_eth1();
	}
	
	/**
	 * update dns configuration only
	 * 
	 * @param
	 * 
	 * @return
	 */
	public static void updateDNSConfiguration(String primaryDns, String secondDns, String tertiaryDns)
			throws Exception {
		AhAppContainer.HmBe.getOsModule()
				.updateDNSConfiguration(primaryDns, secondDns, tertiaryDns);
	}
	
	/**
	 * Ping system by ip or host name to check if it is active
	 * 
	 * @param system ip or host name
	 */
	public static boolean pingSystemToCheckActive(String ipOrHost) {
		try {
			InetAddress address = InetAddress.getByName(ipOrHost);
			return address.isReachable(3000);
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Check if this system is in maintenance mode
	 *
	 *@return boolean
	 */
	public static boolean getMaintenanceModeFromDb() {
		List<?> list = QueryUtil.executeQuery("select hmStatus from " + HMServicesSettings.class.getSimpleName(), null, new FilterParams(
				"owner.domainName", HmDomain.HOME_DOMAIN), 1);
		if (!list.isEmpty()) {
			short hmStatus = (Short) list.get(0);
			return HMServicesSettings.HM_OLINE_STATUS_MAINT == hmStatus;
		}
		return false;
	}
}
