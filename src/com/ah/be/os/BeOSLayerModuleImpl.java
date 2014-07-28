package com.ah.be.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipException;

import com.ah.be.app.BaseModule;
import com.ah.be.app.DebugUtil;

/**
 *
 *@filename		BeOSLayerModuleImpl.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-10-29 10:34:39
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 */
public class BeOSLayerModuleImpl extends BaseModule implements BeOsLayerModule {

	private short osType;

	private long serverStartTime;

	private FileManager fileManager;

	private NetConfigImplInterface netConfig;

	private ServerTimeInterface timeManager;
	
	private HiveManagerIntervalNTP ntpTimer;

	/**
	 * Construct method
	 */
	private BeOSLayerModuleImpl() {
		setModuleId(BaseModule.ModuleID_Os);
		setModuleName("BeOsModule");
	}

	/**
	 * Construct method
	 *
	 * @param osType -
	 */
	public BeOSLayerModuleImpl(short osType) {
		this();
		this.osType = osType;
		serverStartTime = System.currentTimeMillis();
		fileManager = FileManager.getInstance();

		switch (osType) {
			case OS_WINDOWS:
				netConfig = new WindowsNetConfigImpl();
				timeManager = new WinServerTimeManager();
				break;
			case OS_LINUX:
				netConfig = new LinuxNetConfigImpl();
				timeManager = new LinuxServerTimeManager();
				ntpTimer = new HiveManagerIntervalNTP();
				break;
			default:
				break;
		}
	}
	
	@Override
	public boolean run() {
		// refresh shell route config
		refreshShellRouteConfig();

		// start ntp timer
		if (ntpTimer != null) {
			ntpTimer.ntpTimeOnce();
			ntpTimer.startNTPTimer();
		}

		return true;
	}

	@Override
	public boolean shutdown() {
		// stop ntp timer
		if (ntpTimer != null) {
			ntpTimer.stopNtpTimer();
		}

		return true;
	}

	@Override
	public NetConfigImplInterface getNetworkService() {
		return netConfig;
	}

	@Override
	public ServerTimeInterface getTimeService() {
		return timeManager;
	}

	@Override
	public HiveManagerIntervalNTP getNTPService() {
		return ntpTimer;
	}

	/**
	 * Get the server time at Calendar format.
	 * 
	 * @return ServerTime -
	 */
	public Calendar getServerTime() {
		return timeManager.getServerTime();
	}

	/**
	 * Get the server timeZone.
	 * 
	 * @return ServerTime -
	 */
	public String getServerTimeZone() {
		return timeManager.getServerTimeZone();
	}

	/**
	 * Get all timezone in the server.
	 * 
	 * @return TimeZone -
	 */
	public List<String> getAllTimeZone() {
		return timeManager.getAllTimeZone();
	}

	/**
	 * Set up the server time.
	 * 
	 * @param arg_Zone -
	 *            the time zone string
	 * @param arg_Date -
	 *            set time manually : date format is MMDDHHmmYYYY.ss synchronize with server :"",
	 * @param arg_Server -
	 *            synchronize with server :the NTP server and interval set time manually :null
	 * @param arg_NTP -
	 *            BeOsLayerModule.START_NTP_SERVICE: start NTP service
	 *            BeOsLayerModule.STOP_NTP_SERVICE:stop NTP service
	 * @return String : "" if set successful, the error message if set failed
	 */
	public String setServerTime(String arg_Zone, String arg_Date, String[] arg_Server, int arg_NTP) {
		return timeManager.setServerTime(arg_Zone, arg_Date, arg_Server, arg_NTP);
	}

	/**
	 * Check if NTP Service is running on HM.
	 * 
	 * @return boolean
	 */
	public boolean ifNTPServiceStart() {
		return timeManager.ifNTPServiceStart();
	}

	/**
	 * copy directory content
	 * 
	 * @param srcPath
	 *            the source path of to be copied directory
	 * @param dstPath
	 *            the destination path
	 */
	public void copyDirectory(String srcPath, String dstPath) throws IllegalArgumentException,
			FileNotFoundException, IOException, BeNoPermissionException {
		fileManager.copyDirectory(srcPath, dstPath);
	}

	/**
	 * copy File
	 * 
	 * @param srcPath -
	 * @param dstPath -
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public void copyFile(String srcPath, String dstPath) throws IllegalArgumentException,
			FileNotFoundException, IOException, BeNoPermissionException {
		fileManager.copyFile(srcPath, dstPath);
	}

	/**
	 * Creates the directory named given by dirPath
	 * 
	 * @param dirPath -
	 * @return true if and only if the directory was created; false if already exists
	 * @throws IllegalArgumentException -
	 */
	public boolean createDirectory(String dirPath) throws IllegalArgumentException {
		return fileManager.createDirectory(dirPath);
	}

	/**
	 * Creates the file named by this abstract pathname.
	 * 
	 * @param filePath -
	 * @return true if and only if the file was created; false if the named file already exists
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public boolean createFile(String filePath, String[] lines) throws IllegalArgumentException,
			IOException, BeNoPermissionException {
		return fileManager.createFile(filePath, lines);
	}

	/**
	 * delete the directory given by dirPath
	 * 
	 * @param dirPath -
	 * @return true if and only if the directory was deleted; false otherwise(for example user have
	 *         no write permission) NullPointerException when user have no read permission
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 * @throws NullPointerException -
	 * @throws BeNoPermissionException -
	 */
	public boolean deleteDirectory(String dirPath) throws IllegalArgumentException,
			FileNotFoundException, NullPointerException, BeNoPermissionException {
		return fileManager.deleteDirectory(dirPath);
	}

	/**
	 * delete the file named by this abstract pathname.
	 * 
	 * @param filePath -
	 * @return true if and only if the file was deleted; false otherwise
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 */
	public boolean deletefile(String filePath) throws IllegalArgumentException,
			FileNotFoundException {
		return fileManager.deletefile(filePath);
	}

	/**
	 * return an list of the files name in the directory denoted by this pathname
	 * 
	 * @param path -
	 * @return -
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 * @throws BeNoPermissionException -
	 */
	public List<String> getFileNamesOfDirecotry(String path) throws FileNotFoundException,
			IllegalArgumentException, BeNoPermissionException {
		return fileManager.getFileNamesOfDirecotry(path);
	}

	/**
	 * return an list of the files name in the directory denoted by this pathname
	 * 
	 * @param path -
	 * @param isRecur
	 *            if true will return files/subdirs' names in sub-directories
	 * @param flag
	 *            get file or sub-directory or both <br>
	 *            0: both 1: only file 2: only directory
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 * @throws BeNoPermissionException -
	 */
	public List<String> getFileAndSubdirectoryNames(String path, short flag, boolean isRecur)
			throws FileNotFoundException, IllegalArgumentException, BeNoPermissionException {
		return fileManager.getFileAndSubdirectoryNames(path, flag, isRecur);
	}

	/**
	 * return an list of filelists&subdirectories in the directory given by a_dir
	 * 
	 * @param a_dir -
	 * @param isRecur
	 *            if true will return files/subdirs in sub-directories
	 * @param flag
	 *            get file or sub-directory or both <br>
	 *            0: both 1: only file 2: only directory
	 * @return BeNoPermissionException when user have no read permission
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 * @throws BeNoPermissionException -
	 */
	public List<File> getFilesAndSubdirectories(File a_dir, short flag, boolean isRecur)
			throws FileNotFoundException, IllegalArgumentException, BeNoPermissionException {
		return fileManager.getFilesAndSubdirectories(a_dir, flag, isRecur);
	}

	/**
	 * move file
	 * 
	 * @param srcPath -
	 * @param dstPath -
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public void moveFile(String srcPath, String dstPath) throws IllegalArgumentException,
			FileNotFoundException, IOException, BeNoPermissionException {
		fileManager.moveFile(srcPath, dstPath);
	}

	/**
	 * move Directory
	 * 
	 * @param srcPath -
	 * @param dstPath -
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public void moveDirectory(String srcPath, String dstPath) throws IllegalArgumentException,
			FileNotFoundException, IOException, BeNoPermissionException {
		fileManager.moveDirectory(srcPath, dstPath);
	}

	/**
	 * read contents of file named by filepath
	 * 
	 * @param filePath -
	 * @return lines text of file
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public String[] readFile(String filePath) throws IllegalArgumentException,
			FileNotFoundException, IOException, BeNoPermissionException {
		return fileManager.readFile(filePath);
	}

	/**
	 * read corresponding value with key in the file named by filePath
	 * 
	 * @param filePath -
	 * @param key the specified key
	 * @return -
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public String readFile(String filePath, String key) throws IllegalArgumentException,
			FileNotFoundException, IOException, BeNoPermissionException {
		return fileManager.readFile(filePath, key);
	}

	/**
	 * write contents to file named by filepath
	 * 
	 * @param filePath -
	 * @param lines -
	 * @param append
	 *            boolean if <code>true</code>, then data will be written to the end of the file
	 *            rather than the beginning.
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public void writeFile(String filePath, String[] lines, boolean append)
			throws IllegalArgumentException, IOException, FileNotFoundException,
			BeNoPermissionException {
		fileManager.writeFile(filePath, lines, append);
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
	 * @return the previous value of the specified key in this property list, or null if it did not
	 *         have one.
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 * @throws IOException -
	 * @throws ClassCastException
	 * @throws BeNoPermissionException -
	 */
	public Object writeFile(String filePath, String key, String value, String comment)
			throws IllegalArgumentException, FileNotFoundException, IOException,
			ClassCastException, BeNoPermissionException {
		return fileManager.writeFile(filePath, key, value, comment);
	}

	/**
	 * check whether the file is exist.
	 * 
	 * @see com.ah.be.os.BeOsLayerModule#isFileExist(java.lang.String)
	 */
	public boolean isFileExist(String filePath) {
		return fileManager.existsFile(filePath);
	}

	/**
	 * compress files of directory into zip file given by zipPath,will override if zip file exists
	 * 
	 * @param dirPath -
	 * @param zipPath -
	 * @param comment
	 *            comment of zip file
	 * @return checksum of zip with Adler-32 arithmetic
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public long directoryCompress(String dirPath, String zipPath, String comment)
			throws FileNotFoundException, IllegalArgumentException, IOException,
			BeNoPermissionException {
		return fileManager.directoryCompress(dirPath, zipPath, comment);
	}

	/**
	 * compress files into zip file given by zipPath,will override if zip file exists
	 * 
	 * @param files -
	 * @param zipPath -
	 * @param comment comment of zip file
	 * @return checksum of zip with Adler-32 arithmetic
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 * @throws IOException -
	 */
	public long fileCompress(String[] files, String zipPath, String comment)
			throws FileNotFoundException, IllegalArgumentException, IOException {
		return fileManager.fileCompress(files, zipPath, comment);
	}

	/**
	 * decompress zip file to destination directory given by dstpath
	 * 
	 * @param zipPath -
	 * @param dstPath -
	 * @throws IllegalArgumentException -
	 * @throws ZipException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public void deCompress(String zipPath, String dstPath) throws IOException,
			IllegalArgumentException, ZipException, BeNoPermissionException {
		fileManager.deCompress(zipPath, dstPath);
	}

	/**
	 * return an list of filelists in the directory given by a_dir
	 * 
	 * @param a_dir -
	 * @param isRecur
	 *            if true will return files in sub-directories
	 * @return -
	 * @throws IllegalArgumentException -
	 * @throws FileNotFoundException -
	 * @throws BeNoPermissionException -
	 */
	public List<File> getFilesFromFolder(File a_dir, boolean isRecur) throws FileNotFoundException,
			IllegalArgumentException, BeNoPermissionException {
		return fileManager.getFilesFromFolder(a_dir, isRecur);
	}

	/**
	 * create file, if parent directory isn't exist,will be created too
	 * 
	 * @param path -
	 * @param name -
	 * @param lines -
	 * @return -
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 * @throws FileNotFoundException -
	 * @throws BeNoPermissionException -
	 */
	public File createFilewithDir(String path, String name, String[] lines)
			throws IllegalArgumentException, IOException, FileNotFoundException,
			BeNoPermissionException {
		return fileManager.createFilewithDir(path, name, lines);
	}

	/**
	 * exec shell command
	 * 
	 * @param cmd -
	 * @param nanos -
	 * @return error message if existed, or return null
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 */
	public String execCommand(String cmd, int nanos) throws IOException, IllegalArgumentException {
		if (cmd == null || cmd.length() == 0) {
			throw new IllegalArgumentException("Invalid argument");
		}

		String string_Path_Array[] = new String[3];
		switch (osType) {
		case OS_WINDOWS:
			string_Path_Array[0] = "cmd";
			string_Path_Array[1] = "/c";
			string_Path_Array[2] = cmd;
			break;

		case OS_LINUX:
			string_Path_Array[0] = "bash";
			string_Path_Array[1] = "-c";
			string_Path_Array[2] = cmd;
			break;

		default:
			break;
		}

		Process p = Runtime.getRuntime().exec(string_Path_Array);
		if (nanos > 0) {
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				return e.getMessage();
			}
			if (p.exitValue() == 0) return null;
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		String errorMsg = null;
		if (reader.ready()) {
			if ((errorMsg = reader.readLine()) != null) {
				// error
				return errorMsg;
			} else {
				// success
			}
		}

		return errorMsg;
	}

	/**
	 * reboot device
	 * 
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 */
	public void reboot() throws IOException, IllegalArgumentException {
		execCommand("reboot", 0);
	}

	/**
	 * get net config data
	 *
	 * @return data object in type NetConfigureDTO
	 */
	public NetConfigureDTO getNetConfig() {
		return netConfig.getNetConfig();
	}

	/**
	 * update current net config
	 * 
	 * @param dto -
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 */
	public void updateNetConfig(NetConfigureDTO dto) throws IllegalArgumentException, IOException,
			BeNoPermissionException {
		netConfig.updateNetConfig(dto);
	}

	/**
	 * get route info of eth0,eth1
	 *
	 * @return -
	 */
	public Vector<Vector<String>> getRoute() {
		return netConfig.getRoute();
	}

	// /**
	// * update route config , route info given by arg_Route
	// *
	// * @param arg_Route
	// * @return result message
	// */
	// public Vector<String> updateRouteConfig(Vector<Vector<String>> arg_Route)
	// {
	// return netConfig.updateRouteConfig(arg_Route);
	// }

	/**
	 * add route to os & save to file
	 * 
	 * @param routeInfo
	 *            value: [destination,netmask,gateway]
	 * @return wrong message outputted by execute shell, return null when succeed
	 */
	public String addRoute(String[] routeInfo) {
		return netConfig.addRoute(routeInfo);
	}

	/**
	 * remove route from os & save result to file
	 * 
	 * @param delRouteInfos
	 *            item: [destination,netmask,gateway]
	 * @return wrong message outputted by execute shell, return null when succeed
	 */
	public String removeRoute(List<String[]> delRouteInfos) {
		return netConfig.removeRoute(delRouteInfos);
	}

	/**
	 * get Hivemanager eth ip address, if both eth active, return second
	 *
	 * @return -
	 */
	public String getHiveManagerIPAddr() {
		return netConfig.getHiveManagerIPAddr();
	}

	/**
	 * get HiveManager eth netmask if both eth active, return second
	 *
	 * @return -
	 */
	public String getHiveManagerNetmask() {
		return netConfig.getHiveManagerNetmask();
	}

	/**
	 * refresh shell route config,because routes config will be clear when restart network.
	 *
	 */
	public void refreshShellRouteConfig() {
		netConfig.refreshShellRouteConfig();
	}

	/**
	 * get hivemanager path
	 *
	 * @return -
	 */
	public String getHiveManagerPath() {
		return System.getenv("HM_ROOT");
	}

	// /**
	// * get capwap server name
	// *
	// * @param
	// * @return
	// */
	// public String getCAPWAPServerName()
	// {
	// List<?> capwapSettings = QueryUtil.executeQuery(CapwapSettings.class,
	// null, null);
	// if (capwapSettings.size() == 0)
	// {
	// return getHiveManagerIPAddr();
	// }
	//
	// return ((CapwapSettings) capwapSettings.get(0)).getServerName();
	// }

	/**
	 * get os hostname
	 *
	 * @return -
	 */
	public String getHostName() {
		return netConfig.getHostName();
	}
	
	/**
	 * set cache hostname
	 * 
	 * @param
	 * @return
	 */
	public void  setLocalHostName(String newHostName){
		netConfig.setLocalHostName(newHostName);
	}

	/**
	 * get HiveManager up time
	 *
	 * @return time unit is seconds.
	 */
	public long getStartupTime() {
		switch (osType) {
		case OS_WINDOWS: {
			long currentTime = System.currentTimeMillis();
			return (currentTime - serverStartTime) / 1000;
		}

		case OS_LINUX: {
			/**
			 * uptime cli result:<br>
			 * 1. 04:42:52 up 1 min, 1 user, load average: 0.28, 0.14, 0.05 <br>
			 * 2. 11:46:29 up 15 min, 8 users, load average: 0.00, 0.15, 0.26 <br>
			 * 3. 11:45:03 up 2:33, 3 users, load average: 0.17, 0.14, 0.10 <br>
			 * 4. 11:24:07 up 24 days, 15:29, 21 users, load average: 0.00, 0.04, 0.07 <br>
			 * 5. 11:24:07 up 1 day, 15:29, 21 users, load average: 0.00, 0.04, 0.07 <br>
			 */

			long uptime = 0;

			try {
				String[] string_Path_Array = new String[3];
				string_Path_Array[0] = "bash";
				string_Path_Array[1] = "-c";
				string_Path_Array[2] = "uptime";
				Process process = Runtime.getRuntime().exec(string_Path_Array);

				BufferedReader reader = new BufferedReader(new InputStreamReader(process
						.getInputStream()));

				String line;
				while ((line = reader.readLine()) != null) {
					if (line.indexOf("up") > 0) {
						// parse it.
						String[] splitArray = line.split("\\s+");
						List<String> list = new ArrayList<String>(splitArray.length);
						list.addAll(Arrays.asList(splitArray));

						if (list.indexOf("days,") > 0) {
							int days = Integer.valueOf(list.get(list.indexOf("days,") - 1));
							uptime += (days * 24 * 60 * 60);
						}

						if (list.indexOf("day,") > 0) {
							uptime += (24 * 60 * 60);
						}

						if (list.indexOf("min,") > 0) {
							int mins = Integer.valueOf(list.get(list.indexOf("min,") - 1));
							uptime += (60 * mins);
						} else {
							int userIndex = (list.indexOf("users,") > 0) ? list.indexOf("users,")
									: list.indexOf("user,");
							String timeString = list.get(userIndex - 2);
							int hour = Integer.valueOf(
									timeString.substring(0, timeString.indexOf(":")));
							int min = Integer.valueOf(
									timeString.substring(timeString.indexOf(":") + 1, timeString
											.indexOf(",")));

							uptime += (hour * 60 * 60);
							uptime += (min * 60);
						}
					}
				}
			} catch (Exception e) {
				DebugUtil
						.commonDebugWarn("BeOSLayerModuleImpl.getStartupTime() catch exception", e);
			}

			return uptime;
		}

		default:
			DebugUtil.commonDebugWarn("BeOSLayerModuleImpl.getStartupTime() invalid os type.");
			return 0;
		}
	}

	/**
	 * get enable of Lan port
	 *
	 * @return -
	 */
	public boolean getEnable_Eth1() {
		return netConfig.getEnable_Eth1();
	}

	/**
	 * refresh route info for domain remove operation<br>
	 * when domain removed, we need remove corresponding route
	 * 
	 * @param domainIP -
	 * @param domainMask -
	 */
	public void refreshRouteInfo4Domain(String domainIP, String domainMask) {
		netConfig.refreshRouteInfo4Domain(domainIP, domainMask);
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
	public String parseSubnet(String ip, String netmask) {
		String[] ipArray = ip.split("\\.");
		String[] maskArray = netmask.split("\\.");
		String subnet = "";

		for (int i = 0; i < ipArray.length; i++) {
			subnet += String.valueOf(Integer.valueOf(ipArray[i]) & Integer.valueOf(maskArray[i]))
					+ ".";
		}

		return subnet.substring(0, subnet.length() - 1);
	}
	
	/**
	 * get MGT interface IP
	 *
	 * @return -
	 */
	public String getIP_eth0()
	{
		return netConfig.getIP_eth0();
	}
	
	/**
	 * get MGT interface's netmask
	 *
	 * @return -
	 */
	public String getNetmask_eth0()
	{
		return netConfig.getNetmask_eth0();
	}
	
	/**
	 * get LAN interface IP
	 *
	 * @return -
	 */
	public String getIP_eth1()
	{
		return netConfig.getIP_eth1();
	}
	
	/**
	 * get LAN interface's netmask
	 *
	 * @return -
	 */
	public String getNetmask_eth1()
	{
		return netConfig.getNetmask_eth1();
	}
	
	/**
	 * update dns configuration only
	 *
	 * @param primaryDns -
	 * @param secondDns -
	 * @param tertiaryDns -
	 */
	public void updateDNSConfiguration(String primaryDns, String secondDns, String tertiaryDns)
			throws Exception {
		netConfig.updateDNSConfiguration(primaryDns, secondDns, tertiaryDns);
	}

	@Override
	public String getMacAddress() throws SocketException {
		String macAddress = null;

		for (Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces(); nis.hasMoreElements();) {
			NetworkInterface ni = nis.nextElement();
			byte[] byteMacs = ni.getHardwareAddress();

			if (byteMacs != null && byteMacs.length > 0) {
				StringBuilder macBuf = new StringBuilder();

				for (byte byteMac : byteMacs) {
					String hexMac = Integer.toHexString(byteMac & 0xFF).toUpperCase();

					if (hexMac.length() == 1) {
						macBuf.append("0");
					}

					macBuf.append(hexMac);
				}

				macAddress = macBuf.toString();
				break;
			}
		}

		return macAddress;
	}

}