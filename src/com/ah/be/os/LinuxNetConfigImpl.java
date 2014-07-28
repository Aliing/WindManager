package com.ah.be.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.snmp.SnmpAgent;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.SingleTableItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * 
 *@filename		LinuxNetConfigImpl.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-9-14 02:40:01
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 */
public class LinuxNetConfigImpl implements NetConfigImplInterface {

	private static final Tracer log = new Tracer(LinuxNetConfigImpl.class.getSimpleName());

	/**
	 * filename
	 */
	private final String				str_Ifcfg_eth0				= "/etc/sysconfig/network-scripts/ifcfg-eth0";
	private final String				str_Ifcfg_eth1				= "/etc/sysconfig/network-scripts/ifcfg-eth1";
	private final String				str_Network					= "/etc/sysconfig/network";
	private final String				str_Resolv					= "/etc/resolv.conf";
	private final String				str_Hosts					= "/etc/hosts";

	private final String				FILE_PATH_ROUTE_FILE		= System
																		.getenv("HM_ROOT")
																		+ "/WEB-INF/aerohive-routes";
    
	
	private      String					FILE_PATH_ROUTE_FILE_BAK	= "/hivemap"
																		+ System
																			.getenv("HM_ROOT")
																		+ "/WEB-INF/aerohive-routes";

	private final String				FILE_PATH_NEWWORK_SCRIPT	= "/etc/sysconfig/network-scripts/";

	// host name
	private static String				hostName;

	public LinuxNetConfigImpl() {
       File oFile = new File("/hivemap"+ System.getenv("HM_ROOT"));
       
       if (oFile.exists() && oFile.isDirectory())
       {
    	   FILE_PATH_ROUTE_FILE_BAK	= "/hivemap"+ System.getenv("HM_ROOT")+ "/WEB-INF/aerohive-routes";
       }
       else
       {
    	   FILE_PATH_ROUTE_FILE_BAK	= "/hivemap" + System.getenv("CATALINA_HOME") + "/webapps/ROOT/WEB-INF/aerohive-routes";
       }
	}

	/**
	 * read from file
	 * 
	 * @param arg_FileName
	 *            [in]File's name
	 * @return Vector<String> string list of file
	 */
	private Vector<String> readFromFile(String arg_FileName)
	{
		Vector<String> vector_Content;
		try
		{
			String string_Temp;
			vector_Content = new Vector<String>();
			BufferedReader in = new BufferedReader(new FileReader(arg_FileName));
			string_Temp = in.readLine();
			while (null != string_Temp)
			{
				vector_Content.addElement(string_Temp);
				string_Temp = in.readLine();
			}
			in.close();
		}
		catch (Exception e)
		{
			vector_Content = null;
		}
		return vector_Content;
	}

	/**
	 * find string
	 * 
	 * @param arg_Vector
	 *            string list which find from
	 * @param arg_Key
	 *            Key of find string
	 * @param arg_KeyPos
	 *            the key string is pos of split string,from zero
	 * @param arg_MatchKey
	 *            find string whether match key
	 * @param arg_Split
	 *            split string
	 * @param arg_Pos
	 *            the find string is pos of split string,from zero
	 * @return -
	 */
	private String findString(
		Vector<String> arg_Vector,
		String arg_Key,
		int arg_KeyPos,
		boolean arg_MatchKey,
		String arg_Split,
		int arg_Pos)
	{
		String string_Return = "";
		int int_Index;
		boolean boolean_Key = false;
		boolean boolean_Find = false;
		for (String string_Temp : arg_Vector)
		{
			if (string_Temp.startsWith("#"))
				continue;
			if (string_Temp.equalsIgnoreCase(""))
				continue;
			int_Index = string_Temp.indexOf(arg_Key);
			if ((int_Index >= 0 && arg_MatchKey)
				|| (int_Index < 0 && !arg_MatchKey))
			{
				String[] string_Array;
				string_Array = string_Temp.split(arg_Split);
				if (string_Array == null)
					continue;
				if (string_Array.length <= 0)
					continue;
				int i, j = 0;
				for (i = 0; i < string_Array.length; i++)
				{
					if (string_Array[i].length() == 0) {
					}
					else
					{
						if (arg_KeyPos == j && arg_MatchKey)
						{
							if (string_Array[i].trim()
								.equalsIgnoreCase(arg_Key))
								boolean_Key = true;
						}
						if (arg_Pos == j)
						{
							string_Return = string_Array[i].trim();
							boolean_Find = true;
							break;
						}
						j++;
					}
				}
				if (boolean_Find
					&& ((arg_MatchKey && boolean_Key) || !arg_MatchKey))
					break;
			}
		}
		if (boolean_Find && ((arg_MatchKey && boolean_Key) || !arg_MatchKey))
			return string_Return;
		else
			return "";
	}

	/**
	 * find string
	 * 
	 * @param arg_Vector
	 *            string list which find from
	 * @param arg_Key
	 *            Key of find string
	 * @param arg_linePos
	 *            which matched line should be resolve,index from zero
	 * @param arg_KeyPos
	 *            the key string is pos of split string,from zero
	 * @param arg_MatchKey
	 *            find string whether match key
	 * @param arg_Split
	 *            split string
	 * @param arg_Pos
	 *            the find string is pos of split string,from zero
	 * @return -
	 */
	private String findString(
		Vector<String> arg_Vector,
		String arg_Key,
		int arg_linePos,
		int arg_KeyPos,
		boolean arg_MatchKey,
		String arg_Split,
		int arg_Pos)
	{
		String string_Return = "";
		int int_Index;
		int matchedLine = 0;
		boolean boolean_Key = false;
		boolean boolean_Find = false;
		for (String string_Temp : arg_Vector)
		{
			if (string_Temp.startsWith("#"))
				continue;
			if (string_Temp.equalsIgnoreCase(""))
				continue;
			int_Index = string_Temp.indexOf(arg_Key);
			if ((int_Index >= 0 && arg_MatchKey)
				|| (int_Index < 0 && !arg_MatchKey))
			{
				if (matchedLine++ < arg_linePos)
				{
					continue;
				}

				String[] string_Array;
				string_Array = string_Temp.split(arg_Split);
				if (string_Array == null)
					continue;
				if (string_Array.length <= 0)
					continue;
				int i, j = 0;
				for (i = 0; i < string_Array.length; i++)
				{
					if (string_Array[i].length() == 0) {
					}
					else
					{
						if (arg_KeyPos == j && arg_MatchKey)
						{
							if (string_Array[i].trim()
								.equalsIgnoreCase(arg_Key))
								boolean_Key = true;
						}
						if (arg_Pos == j)
						{
							string_Return = string_Array[i].trim();
							boolean_Find = true;
							break;
						}
						j++;
					}
				}
				if (boolean_Find
					&& ((arg_MatchKey && boolean_Key) || !arg_MatchKey))
					break;
			}
		}
		if (boolean_Find && ((arg_MatchKey && boolean_Key) || !arg_MatchKey))
			return string_Return;
		else
			return "";
	}

	/**
	 * delete string by key
	 * 
	 * @param arg_Vector
	 *            string list which be replace
	 * @param arg_Key
	 *            key of string
	 * @param arg_MatchKey
	 *            find string whether match key
	 */
	private void deleteString(
		Vector<String> arg_Vector,
		String arg_Key,
		boolean arg_MatchKey)
	{
		String string_Temp;
		int i;
		int int_Index;
		for (i = 0; i < arg_Vector.size();)
		{
			string_Temp = arg_Vector.elementAt(i);
			if (string_Temp.startsWith("#"))
			{
				i++;
				continue;
			}
			if (arg_Key.equalsIgnoreCase(""))
			{
				i++;
				continue;
			}
			int_Index = string_Temp.indexOf(arg_Key);
			if ((int_Index >= 0 && arg_MatchKey)
				|| (int_Index < 0 && !arg_MatchKey))
			{
				arg_Vector.remove(i);
			}
			else
				i++;
		}
	}

	/**
	 * replace string and if arg_Key is not exist ,add a new string to string
	 * list and new string is arg_Key+arg_Splict+arg_New
	 * 
	 * @param arg_Vector
	 *            string list which be replace
	 * @param arg_Key
	 *            key of string
	 * @param arg_Splict
	 *            splict string
	 * @param arg_Old
	 *            old value
	 * @param arg_New
	 *            new value
	 * @param arg_AddNew
	 *            whether add new as arg_Key+arg_Splict+arg_New
	 */
	private void replaceString(
		Vector<String> arg_Vector,
		String arg_Key,
		String arg_Splict,
		String arg_Old,
		String arg_New,
		boolean arg_AddNew)
	{
		String string_Temp;
		int i;
		int int_Index;
		for (i = 0; i < arg_Vector.size(); i++)
		{
			string_Temp = arg_Vector.elementAt(i);
			if (string_Temp.startsWith("#"))
				continue;
			if (arg_Key.equalsIgnoreCase("") || arg_Key.equalsIgnoreCase(" ")
				|| arg_Key.equalsIgnoreCase("\t"))
				continue;
			int_Index = string_Temp.indexOf(arg_Key);
			if (int_Index >= 0)
			{
				int_Index = string_Temp.indexOf(arg_Old);
				if (int_Index >= 0 && !arg_Old.equalsIgnoreCase(""))
				{
					string_Temp = string_Temp.replaceAll(arg_Old, arg_New);
				}
				if (arg_AddNew)
				{
					string_Temp = arg_Key + arg_Splict + arg_New;
				}
				arg_Vector.setElementAt(string_Temp, i);
				break;
			}
		}
		if (i >= arg_Vector.size() && arg_AddNew)
		{
			// no match and add a new string
			string_Temp = arg_Key + arg_Splict + arg_New;
			arg_Vector.addElement(string_Temp);
		}
	}

	/**
	 * update string and if arg_Key is not exist ,update or add string as
	 * arg_Key+arg_Splict+arg_New
	 * 
	 * @param arg_Vector
	 *            string list which be replace
	 * @param arg_Key
	 *            key of string
	 * @param arg_Splict
	 *            splict string
	 * @param arg_New
	 *            new value
	 */
	private void updateString(
		Vector<String> arg_Vector,
		String arg_Key,
		String arg_Splict,
		String arg_New)
	{
		String string_Temp;
		int i;
		int int_Index;
		for (i = 0; i < arg_Vector.size(); i++)
		{
			string_Temp = arg_Vector.elementAt(i);
			if (string_Temp.startsWith("#"))
				continue;
			if (arg_Key.equalsIgnoreCase("") || arg_Key.equalsIgnoreCase(" ")
				|| arg_Key.equalsIgnoreCase("\t"))
				continue;
			int_Index = string_Temp.indexOf(arg_Key);
			if (int_Index >= 0)
			{
				string_Temp = arg_Key + arg_Splict + arg_New;
				arg_Vector.setElementAt(string_Temp, i);
				break;
			}
		}
		if (i >= arg_Vector.size())
		{
			// no match and add a new string
			string_Temp = arg_Key + arg_Splict + arg_New;
			arg_Vector.addElement(string_Temp);
		}
	}

	/**
	 * save string list to file
	 * 
	 * @param arg_Vector
	 *            string list
	 * @param arg_FileName
	 *            file's name
	 * @throws IOException -
	 */
	private void saveToFile(Vector<String> arg_Vector, String arg_FileName)
		throws IOException
	{
		try
		{
			PrintWriter out = new PrintWriter(new FileWriter(arg_FileName));
			for (String string_Temp : arg_Vector)
			{
				out.println(string_Temp);
			}
			out.close();
			// System.out.println("save to file:"+arg_FileName);
		}
		catch (IOException e)
		{
			DebugUtil
				.commonDebugWarn("LinuxNetConfigImpl.saveToFile(): No permission to write file:"
					+ arg_FileName);
			throw e;
		}
	}

	/**
	 * get os hostname
	 *
	 * @return -
	 */
	public String getHostName()
	{
		if (hostName != null) return hostName;
		
		Vector<String> vct_Hosts = readFromFile(str_Hosts);

		if (vct_Hosts != null)
		{
			hostName = findString(vct_Hosts, "localhost", 2, false, " |\t", 2);
		}

		if (hostName == null)
		{
			hostName = "";
		}
		return hostName;
	}

	public void setHostName(String newHostname) throws IOException
	{
		Vector<String> vct_Hosts = readFromFile(str_Hosts);

		String oldHostname = getHostName();
		String oldDomainname = getDomainName();
		String oldIP = getIP_eth0();

		if (!oldHostname.equalsIgnoreCase(newHostname))
		{
			deleteString(vct_Hosts, "localhost", false);

			vct_Hosts.addElement(oldIP + "\t" + newHostname + "."
				+ oldDomainname + "\t" + newHostname);
			saveToFile(vct_Hosts, str_Hosts);
		}
	}
	
	public void setLocalHostName(String newHostName){
		hostName = newHostName;
	}

	public String getDomainName()
	{
		Vector<String> vct_Resolv = readFromFile(str_Resolv);

		String domainName = null;
		if (vct_Resolv != null)
		{
			domainName = findString(vct_Resolv, "search", 0, true, " |\t", 1);
		}

		if (domainName == null)
		{
			domainName = "";
		}
		return domainName;
	}

	/**
	 * get MGT interface IP
	 *
	 * @return -
	 */
	public String getIP_eth0()
	{
		Vector<String> vct_Ifcfg0 = readFromFile(str_Ifcfg_eth0);

		String ip_eth0 = null;
		if (vct_Ifcfg0 != null)
		{
			ip_eth0 = findString(vct_Ifcfg0, "IPADDR", 0, true, "=", 1);
		}

		if (ip_eth0 == null)
		{
			ip_eth0 = "";
		}
		return ip_eth0;
	}

	public String getEth0Ip() throws SocketException, UnknownHostException {
		String eth0Ip = null;

		overloop:
		for (Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces.hasMoreElements();) {
			NetworkInterface networkInterface = networkInterfaces.nextElement();
			String networkInterfaceName = networkInterface.getName();

			if ("eth0".equalsIgnoreCase(networkInterfaceName)) {
				Enumeration<InetAddress> eth0Addresses = networkInterface.getInetAddresses();

				while (eth0Addresses.hasMoreElements()) {
					InetAddress eth0Address = eth0Addresses.nextElement();

					if (eth0Address instanceof Inet4Address) {
						eth0Ip = eth0Address.getHostAddress();
						break overloop;
					}
				}
			}
		}

		if (eth0Ip == null) {
			InetAddress localAddress = InetAddress.getLocalHost();

			if (localAddress instanceof Inet4Address) {
				eth0Ip = localAddress.getHostAddress();
			}
		}

		return eth0Ip;
	}

	/**
	 * get LAN interface IP
	 *
	 * @return -
	 */
	public String getIP_eth1()
	{
		Vector<String> vct_Ifcfg1 = readFromFile(str_Ifcfg_eth1);

		String ip_eth1 = null;
		if (vct_Ifcfg1 != null)
		{
			ip_eth1 = findString(vct_Ifcfg1, "IPADDR", 0, true, "=", 1);
		}

		if (ip_eth1 == null)
		{
			ip_eth1 = "";
		}
		return ip_eth1;
	}

	/**
	 * get enable of Lan port
	 *
	 * @return -
	 */
	public boolean getEnable_Eth1()
	{
		Vector<String> vct_Ifcfg1 = readFromFile(str_Ifcfg_eth1);

		boolean enable_eth1 = false;
		if (vct_Ifcfg1 != null)
		{
			String ip_eth1 = findString(vct_Ifcfg1, "IPADDR", 0, true, "=", 1);
			// String netmask_eth1 = findString(vct_Ifcfg1, "NETMASK", 0, true,
			// "=", 1);
			String bootFlag = findString(vct_Ifcfg1, "ONBOOT", 0, true, "=", 1);
			if (bootFlag.equalsIgnoreCase("yes"))
			{
				// check ip/mask=0.0.0.0/?
				if (ip_eth1.equals("0.0.0.0"))
				{
					return false;
				}

				enable_eth1 = true;
			}
		}

		return enable_eth1;
	}

	/**
	 * get MGT interface's netmask
	 *
	 * @return -
	 */
	public String getNetmask_eth0()
	{
		Vector<String> vct_Ifcfg0 = readFromFile(str_Ifcfg_eth0);

		String netmask_eth0 = null;
		if (vct_Ifcfg0 != null)
		{
			netmask_eth0 = findString(vct_Ifcfg0, "NETMASK", 0, true, "=", 1);
		}

		if (netmask_eth0 == null)
		{
			netmask_eth0 = "";
		}
		return netmask_eth0;
	}

	/**
	 * get LAN interface's netmask
	 *
	 * @return -
	 */
	public String getNetmask_eth1()
	{
		Vector<String> vct_Ifcfg1 = readFromFile(str_Ifcfg_eth1);

		String netmask_eth1 = null;
		if (vct_Ifcfg1 != null)
		{
			netmask_eth1 = findString(vct_Ifcfg1, "NETMASK", 0, true, "=", 1);
		}

		if (netmask_eth1 == null)
		{
			netmask_eth1 = "";
		}
		return netmask_eth1;
	}

	public String getGateway()
	{
		Vector<String> vct_Network = readFromFile(str_Network);

		String gateway = null;
		if (vct_Network != null)
		{
			gateway = findString(vct_Network, "GATEWAY", 0, true, "=", 1);
		}

		if (gateway == null)
		{
			gateway = "";
		}
		return gateway;
	}

	public String getPrimaryDNS()
	{
		Vector<String> vct_Resolv = readFromFile(str_Resolv);

		String primaryDNS = null;
		if (vct_Resolv != null)
		{
			primaryDNS = findString(vct_Resolv, "nameserver", 0, true, " |\t",
				1);
		}

		if (primaryDNS == null)
		{
			primaryDNS = "";
		}
		return primaryDNS;
	}

	public String getSecondDNS()
	{
		Vector<String> vct_Resolv = readFromFile(str_Resolv);

		String secondDNS = null;
		if (vct_Resolv != null)
		{
			secondDNS = findString(vct_Resolv, "nameserver", 1, 0, true,
				" |\t", 1);
		}

		if (secondDNS == null)
		{
			secondDNS = "";
		}
		return secondDNS;
	}

	public String getTertiaryDns()
	{
		Vector<String> vct_Resolv = readFromFile(str_Resolv);

		String tertiaryDNS = null;
		if (vct_Resolv != null)
		{
			tertiaryDNS = findString(vct_Resolv, "nameserver", 2, 0, true,
				" |\t", 1);
		}

		if (tertiaryDNS == null)
		{
			tertiaryDNS = "";
		}
		return tertiaryDNS;
	}

	private final String	SPLITCHAR			= " ";

	private final short		INDEX_DESTINATION	= 0;

	private final short		INDEX_NETMASK		= 1;

	private final short		INDEX_GATEWAY		= 2;

	// every row in aerohive_routes file should have three word, there are
	// dest,mask,gateway
	private final short		ROUTEINFOLENGTH		= 3;

	/**
	 * get route info
	 * 
	 * @return route infos
	 */
	public Vector<Vector<String>> getRoute()
	{
		Vector<Vector<String>> vct_routes = new Vector<Vector<String>>();
		String[] routeArray = null;
		try
		{
			routeArray = FileManager.getInstance().readFile(
				FILE_PATH_ROUTE_FILE);
		}
		catch (Exception e)
		{
			DebugUtil
				.commonDebugWarn(
					"LinuxNetConfigImpl.getRoute(): read file aerohive-routes content, catch exception: ",
					e);
		}

		if (routeArray == null || routeArray.length == 0)
		{
			// no content, return empty vector
			return vct_routes;
		}

		for (String route : routeArray) {
			String routeInfo = route.trim();

			if (routeInfo.length() == 0) {
				continue;
			}

			String[] infoArray = routeInfo.split(SPLITCHAR);
			if (infoArray.length < ROUTEINFOLENGTH) {
				// invalid data
				DebugUtil
						.commonDebugWarn("LinuxNetConfigImpl.getRoute(): route info have no enough value");
				continue;
			}

			if (infoArray.length > ROUTEINFOLENGTH) {
				// should not happen
				// if reach here, maybe have blank str, but let's ignore it.

				continue;
			}

			// got return value
			Vector<String> vct_value = new Vector<String>();
			String destination;
			String netmask;
			String gateway;

			destination = infoArray[INDEX_DESTINATION];
			netmask = infoArray[INDEX_NETMASK];
			gateway = infoArray[INDEX_GATEWAY];

			vct_value.add(destination);
			vct_value.add(netmask);
			vct_value.add(gateway);

			vct_routes.add(vct_value);
		}

		return vct_routes;
	}

	/**
	 * enable LAN Interface (eth1)
	 *
	 * @throws IOException -
	 */
	public void enableLANInterface() throws IOException
	{
		// enable eth1 now
		execShell("ifconfig eth1 up");

		// enable eth1 when next boot
		Vector<String> vct_Ifcfg1 = readFromFile(str_Ifcfg_eth1);
		String oldBootFlag = findString(vct_Ifcfg1, "ONBOOT", 0, true, "=", 1);
		// if (oldBootFlag.equalsIgnoreCase("yes"))
		// {
		// return;
		// }
		replaceString(vct_Ifcfg1, "ONBOOT", "=", oldBootFlag, "yes", true);
		saveToFile(vct_Ifcfg1, str_Ifcfg_eth1);

		// post operation
		changeEnableLANPort(true);
		
		//send link up trap
		if(SnmpAgent.sendLinkUpTrap("eth1")) {
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MINOR, HmSystemLog.FEATURE_ADMINISTRATION,
				MgrUtil.getUserMessage("hm.system.log.linux.net.config.send.eth1.up"));
		}
	}

	/**
	 * disable LAN Interface (eth1)
	 *
	 * @throws IOException -
	 */
	public void disableLANInterface() throws IOException
	{
		// disable eth1 now
		execShell("ifconfig eth1 down");

		// disable eth1 when next boot
		Vector<String> vct_Ifcfg1 = readFromFile(str_Ifcfg_eth1);
		String oldBootFlag = findString(vct_Ifcfg1, "ONBOOT", 0, true, "=", 1);
		// if (oldBootFlag.equalsIgnoreCase("no"))
		// {
		// return;
		// }
		replaceString(vct_Ifcfg1, "ONBOOT", "=", oldBootFlag, "no", true);
		saveToFile(vct_Ifcfg1, str_Ifcfg_eth1);

		// post operation
		changeEnableLANPort(false);
		
		//send link up trap
		if(SnmpAgent.sendLinkDownTrap("eth1")) {
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_ADMINISTRATION,
					MgrUtil.getUserMessage("hm.system.log.linux.net.config.send.eth1.down"));
		}
	}

	/**
	 * post operation when change enable of LAN port<br>
	 * for VHM network script files.
	 * 
	 * @param isEnable new enable value.
	 */
	private void changeEnableLANPort(boolean isEnable)
	{
		try
		{
			// get all script files
			File scriptDir = new File(FILE_PATH_NEWWORK_SCRIPT);
			if (!scriptDir.exists() || !scriptDir.isDirectory())
			{
				return;
			}

			File[] fileArray = scriptDir.listFiles();
			List<File> fileList = new ArrayList<File>();

			boolean oldEnable = !isEnable;
			String fileprefix = oldEnable ? "ifcfg-eth1:" : "ifcfg-eth0:";
			for (File file : fileArray) {
				if (file.getName().contains(fileprefix)) {
					fileList.add(file);
				}
			}

			// rename to new file name
			String oldStr = isEnable ? "eth0" : "eth1";
			String newStr = isEnable ? "eth1" : "eth0";
			for (File file : fileList)
			{
				String newFileName = file.getName().replace(oldStr, newStr);
				String newFilePath = FILE_PATH_NEWWORK_SCRIPT + newFileName;
				file.renameTo(new File(newFilePath));

				// replace 'DEVICE' property
				String newDevice = FileManager.getInstance().readFile(
					newFilePath, "DEVICE");
				newDevice = newDevice.replace(oldStr, newStr);
				FileManager.getInstance().writeFile(
					FILE_PATH_NEWWORK_SCRIPT + newFileName, "DEVICE",
					newDevice, null);
			}
		}
		catch (Exception e)
		{
			DebugUtil
				.commonDebugWarn(
					"LinuxNetConfigImpl.changeEnableLANPort(): post operation for lan port enable changed catch exception.",
					e);
		}
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

		// Vector<String> vct_Resolv_bak = readFromFile(str_Resolv);
		Vector<String> vct_Resolv = readFromFile(str_Resolv);

		String domainName = "";
		if (vct_Resolv != null) {
			domainName = findString(vct_Resolv, "search", 0, true, " |\t", 1);
		}

		vct_Resolv.clear();
		vct_Resolv.add("search " + domainName);
		vct_Resolv.add("");
		if (primaryDns != null && primaryDns.trim().length() > 0) {
			vct_Resolv.add("nameserver " + primaryDns);
		}
		if (secondDns != null && secondDns.trim().length() > 0) {
			vct_Resolv.add("nameserver " + secondDns);
		}
		if (tertiaryDns != null && tertiaryDns.trim().length() > 0) {
			vct_Resolv.add("nameserver " + tertiaryDns);
		}
		saveToFile(vct_Resolv, str_Resolv);

		// do not need restart network service
		// // restart network to active net config
		// String errorMsg = restartNetworkService();
		//
		// if (errorMsg.length() > 0) {
		// saveToFile(vct_Resolv_bak, str_Resolv);
		//
		// restartNetworkService();
		//
		// throw new BeNoPermissionException(errorMsg.substring(errorMsg.indexOf("Error"),
		//			errorMsg.lastIndexOf(".")));
		//}
	}
	
	/**
	 * update current net config
	 * 
	 * @param dto -
	 */
	public void updateNetConfig(NetConfigureDTO dto)
		throws IllegalArgumentException,
		IOException,
		BeNoPermissionException
	{
		if (dto == null)
		{
			throw new IllegalArgumentException("Invalid argument");
		}

		String ipAddress_MGT = dto.getIpAddress_eth0();
		String netmask_MGT = dto.getNetmask_eth0();

		String ipAddress_LAN = dto.getIpAddress_eth1();
		String netmask_LAN = dto.getNetmask_eth1();
		boolean enableEth1 = dto.isEnabled_eth1();

		String gateway = dto.getGateway();
		String hostName = dto.getHostName();
		String domainName = dto.getDomainName();
		String primaryDns = dto.getPrimaryDns();
		String secondDns = dto.getSecondDns();
		String tertiaryDns = dto.getTertiaryDns();
		// Vector<Vector<String>> vctRoute = dto.getVctRoute();

		// check ip/mask, can not in same subnet with other domains.
		if (!enableEth1
			&& isInSameSubnetWithOtherDomains(ipAddress_MGT, netmask_MGT))
		{
			throw new BeNoPermissionException(
				"MGT port ip/mask can not in same subnet with other VHMs.");
		}

		if (enableEth1
			&& isInSameSubnetWithOtherDomains(ipAddress_LAN, netmask_LAN))
		{
			throw new BeNoPermissionException(
				"LAN port ip/mask can not in same subnet with other VHMs.");
		}

		// read file content
		Vector<String> vct_Ifcfg0 = readFromFile(str_Ifcfg_eth0);
		Vector<String> vct_Ifcfg1 = readFromFile(str_Ifcfg_eth1);
		Vector<String> vct_Network = readFromFile(str_Network);
		Vector<String> vct_Resolv = readFromFile(str_Resolv);
		Vector<String> vct_Hosts = readFromFile(str_Hosts);

		// bak data for recover when update failed.
		Vector<String> vct_Ifcfg0_bak = readFromFile(str_Ifcfg_eth0);
		Vector<String> vct_Ifcfg1_bak = readFromFile(str_Ifcfg_eth1);
		Vector<String> vct_Network_bak = readFromFile(str_Network);
		Vector<String> vct_Resolv_bak = readFromFile(str_Resolv);
		Vector<String> vct_Hosts_bak = readFromFile(str_Hosts);

		// first get old config value
		String old_ipAddress_MGT;
		String old_ipAddress_LAN;
		String old_netmask_MGT;
		String old_netmask_LAN;
		String old_bootProto_LAN;
		String old_onBoot_LAN;
		String old_gateway;
		String old_primaryDNS;
		String old_secondDNS;
		String old_tertiaryDns;
		String old_domainName;
		String old_hostName;

		boolean old_enableEth1 = false;

		String old_hivemanagerIP = getHiveManagerIPAddr();
		String old_hivemanagerMask = getHiveManagerNetmask();

		String newBootProto_LAN = "static";
		String newOnBoot_LAN = "yes";

		if (vct_Ifcfg0 != null)
		{
			old_ipAddress_MGT = findString(vct_Ifcfg0, "IPADDR", 0, true, "=",
				1);
			old_netmask_MGT = findString(vct_Ifcfg0, "NETMASK", 0, true, "=", 1);
		}
		else
		{
			old_ipAddress_MGT = "";
			old_netmask_MGT = "";
		}
		if (vct_Ifcfg1 != null)
		{
			old_ipAddress_LAN = findString(vct_Ifcfg1, "IPADDR", 0, true, "=",
				1);
			old_netmask_LAN = findString(vct_Ifcfg1, "NETMASK", 0, true, "=", 1);
			old_bootProto_LAN = findString(vct_Ifcfg1, "BOOTPROTO", 0, true,
				"=", 1);
			old_onBoot_LAN = findString(vct_Ifcfg1, "ONBOOT", 0, true, "=", 1);

			old_enableEth1 = old_onBoot_LAN.equalsIgnoreCase("yes");
		}
		else
		{
			old_ipAddress_LAN = "";
			old_netmask_LAN = "";
			old_bootProto_LAN = "";
			old_onBoot_LAN = "";
		}
		if (vct_Network != null)
		{
			for (int i = 0; i < vct_Network.size(); i++)
			{
				vct_Network.set(i, vct_Network.elementAt(i).replaceAll(" |\t",
					""));
			}

			old_gateway = findString(vct_Network, "GATEWAY", 0, true, "=", 1);
		}
		else
		{
			old_gateway = "";
		}
		if (vct_Resolv != null)
		{
			old_primaryDNS = findString(vct_Resolv, "nameserver", 0, 0, true,
				" |\t", 1);
			old_secondDNS = findString(vct_Resolv, "nameserver", 1, 0, true,
				" |\t", 1);
			old_tertiaryDns = findString(vct_Resolv, "nameserver", 2, 0, true,
				" |\t", 1);
			old_domainName = findString(vct_Resolv, "search", 0, true, " |\t",
				1);
		}
		else
		{
			old_primaryDNS = "";
			old_secondDNS = "";
			old_tertiaryDns = "";
			old_domainName = "";
		}
		if (vct_Hosts != null)
		{
			old_hostName = findString(vct_Hosts, "localhost", 2, false, " |\t",
				2);
		}
		else
		{
			old_hostName = "";
		}

		// start to update
		if (vct_Ifcfg0 != null)
		{
			if (!old_ipAddress_MGT.equals(ipAddress_MGT)
				|| !old_netmask_MGT.equals(netmask_MGT)
				|| !old_gateway.equals(gateway))
			{
				this.replaceString(vct_Ifcfg0, "IPADDR", "=",
					old_ipAddress_MGT, ipAddress_MGT, true);
				this.replaceString(vct_Ifcfg0, "NETMASK", "=", old_netmask_MGT,
					netmask_MGT, true);
				this.replaceString(vct_Ifcfg0, "GATEWAY", "=", old_gateway,
					gateway, true);
				this.saveToFile(vct_Ifcfg0, str_Ifcfg_eth0);
			}
		}
		if (vct_Ifcfg1 != null && enableEth1)
		{
			if (!old_ipAddress_LAN.equals(ipAddress_LAN)
				|| !old_netmask_LAN.equals(netmask_LAN)
				|| !old_gateway.equals(gateway)
				|| !old_bootProto_LAN.equalsIgnoreCase(newBootProto_LAN)
				|| !old_onBoot_LAN.equalsIgnoreCase(newOnBoot_LAN))
			{
				this.replaceString(vct_Ifcfg1, "IPADDR", "=",
					old_ipAddress_LAN, ipAddress_LAN, true);
				this.replaceString(vct_Ifcfg1, "NETMASK", "=", old_netmask_LAN,
					netmask_LAN, true);
				this.replaceString(vct_Ifcfg1, "BOOTPROTO", "=",
					old_bootProto_LAN, newBootProto_LAN, true);
				this.replaceString(vct_Ifcfg1, "ONBOOT", "=", old_onBoot_LAN,
					newOnBoot_LAN, true);
				this.replaceString(vct_Ifcfg1, "GATEWAY", "=", old_gateway,
					gateway, true);
				this.saveToFile(vct_Ifcfg1, str_Ifcfg_eth1);
			}
		}
		if (vct_Network != null)
		{
			if (!old_gateway.equals(gateway)
				|| !old_hostName.equalsIgnoreCase(hostName)
				|| !old_domainName.equalsIgnoreCase(domainName))
			{
				this.replaceString(vct_Network, "GATEWAY=", "", old_gateway,
					gateway, true);
				this.updateString(vct_Network, "HOSTNAME", "=", hostName + "."
					+ domainName);
				this.updateString(vct_Network, "DOMAINNAME", "=", domainName);
				this.saveToFile(vct_Network, str_Network);
			}
		}
		if (vct_Resolv != null)
		{
			if (!old_primaryDNS.equals(primaryDns)
				|| !old_secondDNS.equals(secondDns)
				|| !old_tertiaryDns.equals(tertiaryDns)
				|| !old_domainName.equalsIgnoreCase(domainName))
			{
//				this.replaceString(vct_Resolv, "nameserver", " ",
//					old_primaryDNS, primaryDns, true);
//				this.updateString(vct_Resolv, "nameserver", " ", secondDns, 1);
//				this
//					.updateString(vct_Resolv, "nameserver", " ", tertiaryDns, 2);
//				this.updateString(vct_Resolv, "search", " ", domainName);
//				this.saveToFile(vct_Resolv, str_Resolv);
				
				vct_Resolv.clear();
				vct_Resolv.add("search " + domainName);
				vct_Resolv.add("");
				if (primaryDns != null && primaryDns.trim().length() > 0) {
					vct_Resolv.add("nameserver " + primaryDns);
				}
				if (secondDns != null && secondDns.trim().length() > 0) {
					vct_Resolv.add("nameserver " + secondDns);
				}
				if (tertiaryDns != null && tertiaryDns.trim().length() > 0) {
					vct_Resolv.add("nameserver " + tertiaryDns);
				}
				saveToFile(vct_Resolv, str_Resolv);
			}
		}
		if (vct_Hosts != null)
		{
			if (!old_ipAddress_MGT.equals(ipAddress_MGT)
				|| !old_hostName.equalsIgnoreCase(hostName)
				|| !old_domainName.equalsIgnoreCase(domainName))
			{
				this.deleteString(vct_Hosts, "localhost", false);

				vct_Hosts.addElement(ipAddress_MGT + "\t" + hostName + "."
					+ domainName + "\t" + hostName);
				this.saveToFile(vct_Hosts, str_Hosts);
			}
		}

		// set Lan port enable/disable
		if (enableEth1 && !old_enableEth1)
		{
			enableLANInterface();
		}
		else
			if (!enableEth1 && old_enableEth1)
			{
				disableLANInterface();
			}

		// Route update, if route't net section is same with neigthor mgt's nor
		// lan's,remove it.
		if (!old_ipAddress_MGT.equals(ipAddress_MGT)
			|| !old_netmask_MGT.equals(netmask_MGT)
			|| (enableEth1 && (!old_ipAddress_LAN.equals(ipAddress_LAN) || !old_netmask_LAN
				.equals(netmask_LAN))) || (old_enableEth1 != enableEth1))
		{
			refreshRouteInfo(ipAddress_MGT, netmask_MGT, enableEth1,
				ipAddress_LAN, netmask_LAN);
		}

		// if hive manager ip/netmask changed, notify other module
		if ((!getHiveManagerIPAddr().equals(old_hivemanagerIP))
			|| (!getHiveManagerNetmask().equals(old_hivemanagerMask)))
		{
			// update IP Address bo
			updateDefaultIPAddress();
		}
		
		//effect host name change on current shell.
		updateHostName(hostName, domainName);

		// restart network to active net config
		String errorMsg = restartNetworkService();

		if (errorMsg.length() > 0)
		{
			// recover net config
			recoverNetconfig(vct_Ifcfg0_bak, vct_Ifcfg1_bak, vct_Network_bak,
				vct_Resolv_bak, vct_Hosts_bak);

			throw new BeNoPermissionException(errorMsg.substring(errorMsg
				.indexOf("Error"), errorMsg.lastIndexOf(".")));
		}

		// bcz route config be cleared after network restart, so ,let's config
		// it ag.
		// refresh shell route config
		refreshShellRouteConfig();
	}
	
	private void updateDefaultIPAddress() throws IOException
	{
		try {
			List<IpAddress> list = QueryUtil.executeQuery(IpAddress.class, null, new FilterParams("defaultFlag", true));
			if (list == null || list.size() == 0) {
				return;
			}
			
			IpAddress ip = list.get(0);
			List<SingleTableItem> items = new ArrayList<SingleTableItem>();
			SingleTableItem single = new SingleTableItem();
			single.setDescription("Default " + NmsUtil.getOEMCustomer().getNmsName() + " IP address");
			single.setIpAddress(getHiveManagerIPAddr());
			single.setNetmask(getHiveManagerNetmask());
			single.setType(SingleTableItem.TYPE_GLOBAL);
			items.add(single);
			
			ip.setItems(items);
			
			QueryUtil.updateBo(ip);
		} catch (Exception e) {
			DebugUtil.commonDebugError("update default ip address catch exception", e);
			throw new IOException("Update default IP address error. "+e.getMessage());
		}
	}
	
	/**
	 * update hostname on current shell
	 *
	 * @param hostName -
	 * @param domainName -
	 */
	private void updateHostName(String hostName,String domainName)
	{
		try
		{
			String[] commands = new String[3];
			commands[0] = "bash";
			commands[1] = "-c";
			commands[2] = "hostname "+hostName+"."+domainName;
			Process process = Runtime.getRuntime().exec(commands);

//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//				process.getInputStream()));
//
//			String line;
//			while ((line = reader.readLine()) != null)
//			{
//				// error happens
//				if (line.indexOf("Error") > 0)
//				{
//					return line;
//				}
//			}

			process.waitFor();
		}
		catch (Exception e)
		{
			DebugUtil
				.commonDebugWarn(
					"LinuxNetConfigImpl.updateHostName(): execute 'hostname ...' command catch exception.",
					e);
		}
	}

	/**
	 * run 'network restart' shell
	 *
	 * @return -
	 */
	private String restartNetworkService()
	{
		try
		{
			// restart network , let config valid now
			String[] commands = new String[3];
			commands[0] = "bash";
			commands[1] = "-c";
			commands[2] = "/etc/init.d/network restart";
			Process process = Runtime.getRuntime().exec(commands);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null)
			{
				// error happens
				if (line.indexOf("Error") > 0)
				{
					return line;
				}
			}

			// wait restart network end
			process.waitFor();
			
			return "";
		}
		catch (Exception e)
		{
			DebugUtil
				.commonDebugWarn(
					"LinuxNetConfigImpl.restartNetworkService(): network restart process catch exception.",
					e);

			return "";
		}
	}

	/**
	 * recover net config when update failed
	 * 
	 * @param vct_Ifcfg0 -
	 * @param vct_Ifcfg1 -
	 * @param vct_Network -
	 * @param vct_Resolv -
	 * @param vct_Hosts -
	 */
	private void recoverNetconfig(
		Vector<String> vct_Ifcfg0,
		Vector<String> vct_Ifcfg1,
		Vector<String> vct_Network,
		Vector<String> vct_Resolv,
		Vector<String> vct_Hosts)
	{
		try
		{
			// save to file
			saveToFile(vct_Ifcfg0, str_Ifcfg_eth0);
			saveToFile(vct_Ifcfg1, str_Ifcfg_eth1);
			saveToFile(vct_Network, str_Network);
			saveToFile(vct_Resolv, str_Resolv);
			saveToFile(vct_Hosts, str_Hosts);

			// restart network
			// restart network , let config valid now
			String[] string_Path_Array = new String[3];
			string_Path_Array[0] = "bash";
			string_Path_Array[1] = "-c";
			string_Path_Array[2] = "/etc/init.d/network restart";
			Process process = Runtime.getRuntime().exec(string_Path_Array);

			process.waitFor();
		}
		catch (Exception e)
		{
			DebugUtil.commonDebugWarn(
				"LinuxNetConfigImpl.recoverNetconfig(): catch exception", e);
		}
	}

	/**
	 * refresh route info for domain remove operation<br>
	 * when domain removed, we need remove corresponding route
	 * 
	 * @param domainIP -
	 * @param domainMask -
	 */
	public void refreshRouteInfo4Domain(String domainIP, String domainMask)
	{
		try
		{
			if (!FileManager.getInstance().existsFile(FILE_PATH_ROUTE_FILE))
			{
				DebugUtil
					.commonDebugInfo("LinuxNetConfigImpl.refreshRouteInfo4Domain(): Del some invalid route from file, but file aerohive-routes doesn't exist");
				return;
			}

			// 1. get route data, check it.
			String[] fileContent = FileManager.getInstance().readFile(
				FILE_PATH_ROUTE_FILE);
			if (fileContent.length == 0)
			{
				return;
			}

			List<String> newContent = new ArrayList<String>();
			List<String[]> delContent = new ArrayList<String[]>();

			for (String routeInfo : fileContent) {
				String[] routes = routeInfo.split(" ");
				if (isInSameSubnet(domainIP, domainMask, routes[INDEX_GATEWAY],
						domainMask)) {
					delContent.add(routes);
				} else {
					newContent.add(routeInfo);
				}
			}

			// 2.run route del command
			List<String[]> succList = removeRouteFromShell(delContent);

			if (succList.size() < delContent.size())
			{
				DebugUtil
					.commonDebugWarn("LinuxNetConfigImpl.refreshRouteInfo4Domain(): Del some invalid route from shell configuration, but not all success!");
			}

			// 3. write new content to route file,if not exists,log error.
			// should not happen.
			String[] newArray = new String[newContent.size()];
			newContent.toArray(newArray);

			FileManager.getInstance().writeFile(FILE_PATH_ROUTE_FILE, newArray,
				false);

			// bak file
			if (!FileManager.getInstance().existsFile(FILE_PATH_ROUTE_FILE_BAK))
			{
				DebugUtil
					.commonDebugWarn("LinuxNetConfigImpl.refreshRouteInfo4Domain(): Del some invalid route from file, but file aerohive-routes(bak) doesn't exist");
			}
			else
			{
				FileManager.getInstance().writeFile(FILE_PATH_ROUTE_FILE_BAK,
					newArray, false);
			}
		}
		catch (Exception e)
		{
			DebugUtil
				.commonDebugWarn("LinuxNetConfigImpl.refreshRouteInfo4Domain(): Catch Exception: "
					+ e);
		}
	}

	/**
	 * refresh route info, since ip / mask changed
	 * 
	 * @param ip_eth0 -
	 * @param mask_eth0 -
	 * @param enableEth1 -
	 * @param ip_eth1 -
	 * @param mask_eth1 -
	 */
	private void refreshRouteInfo(
		String ip_eth0,
		String mask_eth0,
		boolean enableEth1,
		String ip_eth1,
		String mask_eth1)
	{
		try
		{
			if (!FileManager.getInstance().existsFile(FILE_PATH_ROUTE_FILE))
			{
				DebugUtil
					.commonDebugInfo("LinuxNetConfigImpl.refreshRouteInfo(): Del some invalid route from file, but file aerohive-routes doesn't exist");
				return;
			}

			// 1. get route data, check it.
			String[] fileContent = FileManager.getInstance().readFile(
				FILE_PATH_ROUTE_FILE);
			if (fileContent.length == 0)
			{
				return;
			}

			List<String> newContent = new ArrayList<String>();
			List<String[]> delContent = new ArrayList<String[]>();

			for (String routeInfo : fileContent) {
				String[] routes = routeInfo.split(" ");
				if (!isInSameSubnet(ip_eth0, mask_eth0, routes[INDEX_GATEWAY],
						mask_eth0)
						&& !(enableEth1 && isInSameSubnet(ip_eth1, mask_eth1,
						routes[INDEX_GATEWAY], mask_eth1))
						&& !isInSameSubnetWithOtherDomains(routes[INDEX_GATEWAY],
						enableEth1)) {
					delContent.add(routes);
				} else {
					newContent.add(routeInfo);
				}
			}

			// 2.run route del command
			List<String[]> succList = removeRouteFromShell(delContent);

			if (succList.size() < delContent.size())
			{
				DebugUtil
					.commonDebugWarn("LinuxNetConfigImpl.refreshRouteInfo(): Del some invalid route from shell configuration, but not all success!");
			}

			// 3. write new content to route file,if not exists,log error.
			// should not happen.
			String[] newArray = new String[newContent.size()];
			newContent.toArray(newArray);

			FileManager.getInstance().writeFile(FILE_PATH_ROUTE_FILE, newArray,
				false);

			// bak file
			if (!FileManager.getInstance().existsFile(FILE_PATH_ROUTE_FILE_BAK))
			{
				DebugUtil
					.commonDebugWarn("LinuxNetConfigImpl.refreshRouteInfo(): Del some invalid route from file, but file aerohive-routes(bak) doesn't exist");
			}
			else
			{
				FileManager.getInstance().writeFile(FILE_PATH_ROUTE_FILE_BAK,
					newArray, false);
			}
		}
		catch (Exception e)
		{
			DebugUtil
				.commonDebugWarn("LinuxNetConfigImpl.refreshRouteInfo(): Catch Exception: "
					+ e);
		}
	}

	/**
	 * check whether be in same subnet with other domains.
	 * 
	 * @param ip -
	 * @param mask -
	 * @return -
	 */
	private boolean isInSameSubnetWithOtherDomains(String ip, String mask)
	{
		try
		{
			File scriptDir = new File(FILE_PATH_NEWWORK_SCRIPT);
			if (!scriptDir.exists() || !scriptDir.isDirectory())
			{
				return false;
			}

			File[] fileArray = scriptDir.listFiles();
			List<String> ipList = new ArrayList<String>();
			List<String> maskList = new ArrayList<String>();
			for (File file : fileArray) {
				if (file.getName().contains("ifcfg-eth1:")
						|| file.getName().contains("ifcfg-eth0:")) {
					ipList.add(FileManager.getInstance().readFile(
							file.getPath(), "IPADDR"));
					maskList.add(FileManager.getInstance().readFile(
							file.getPath(), "NETMASK"));
				}
			}

			for (int i = 0, size = ipList.size(); i < size; i++)
			{
				String domainIP = ipList.get(i);
				String domainmask = maskList.get(i);
				if (isInSameSubnet(domainIP, domainmask, ip, mask))
				{
					return true;
				}
			}

			return false;
		}
		catch (Exception e)
		{
			DebugUtil
				.commonDebugWarn("LinuxNetConfigImpl.isInSameSubnetWithOtherDomains(): Catch Exception: "
					+ e);
			return false;
		}
	}

	/**
	 * API add for VHM feature<br>
	 * check whether 'gateway' in the same subnet with some domain
	 * 
	 * @param gateway -
	 * @param isEnableEth1 -
	 * @return -
	 */
	private boolean isInSameSubnetWithOtherDomains(
		String gateway,
		boolean isEnableEth1)
	{
		try
		{
			File scriptDir = new File(FILE_PATH_NEWWORK_SCRIPT);
			if (!scriptDir.exists() || !scriptDir.isDirectory())
			{
				return false;
			}

			File[] fileArray = scriptDir.listFiles();
			List<String> ipList = new ArrayList<String>();
			List<String> maskList = new ArrayList<String>();
			String fileprefix = isEnableEth1 ? "ifcfg-eth1:" : "ifcfg-eth0:";
			for (File file : fileArray) {
				if (file.getName().contains(fileprefix)) {
					ipList.add(FileManager.getInstance().readFile(
							file.getPath(), "IPADDR"));
					maskList.add(FileManager.getInstance().readFile(
							file.getPath(), "NETMASK"));
				}
			}

			for (int i = 0, size = ipList.size(); i < size; i++)
			{
				String ip = ipList.get(i);
				String mask = maskList.get(i);
				if (isInSameSubnet(ip, mask, gateway, mask))
				{
					return true;
				}
			}

			return false;
		}
		catch (Exception e)
		{
			DebugUtil
				.commonDebugWarn("LinuxNetConfigImpl.isInSameSubnetWithOtherDomains(): Catch Exception: "
					+ e);
			return false;
		}
	}
	
	private void refreshGateway() {
		try {
			String gateway = null;
			String gateway_ = null;

			Vector<String> vct_Ifcfg0 = readFromFile(str_Ifcfg_eth0);
			if (vct_Ifcfg0 != null) {
				gateway = findString(vct_Ifcfg0, "GATEWAY", 0, true, "=", 1);
			}
			
			Vector<String> vct_Network = readFromFile(str_Network);
			if (vct_Network != null) {
				gateway_ = findString(vct_Network, "GATEWAY", 0, true, "=", 1);
			}
			
			// write GATEWAY= field into /etc/sysconfig/network
			if (gateway != null && gateway.length() > 0
					&& (gateway_ == null || gateway_.length() == 0)) {
				vct_Network.add("GATEWAY=" + gateway);
				saveToFile(vct_Network, str_Network);
			}
		} catch (Exception e) {
			DebugUtil.commonDebugWarn("LinuxNetConfigImpl.refreshGateway() Catch exception ", e);
		}
	}

	/**
	 * refresh shell route config,because routes config will be clear when restart network.
	 */
	public void refreshShellRouteConfig()
	{
		// also refresh gateway script file
		refreshGateway();
		
		try
		{
			if (!FileManager.getInstance().existsFile(FILE_PATH_ROUTE_FILE))
			{
				DebugUtil
					.commonDebugWarn("LinuxNetConfigImpl.refreshShellRouteConfig(): File aerohive-routes doesn't exist,Create it!");
				FileManager.getInstance().createFilewithDir(
					FILE_PATH_ROUTE_FILE.substring(0, FILE_PATH_ROUTE_FILE
						.lastIndexOf("/")),
					FILE_PATH_ROUTE_FILE.substring(FILE_PATH_ROUTE_FILE
						.lastIndexOf("/") + 1), new String[0]);
				return;
			}

			// 1. get route data, check it.
			String[] fileContent = FileManager.getInstance().readFile(
				FILE_PATH_ROUTE_FILE);
			if (fileContent.length == 0)
			{
				DebugUtil
					.commonDebugInfo("LinuxNetConfigImpl.refreshShellRouteConfig(): File aerohive-routes is empty.");
				return;
			}

			// bak file
			if (!FileManager.getInstance().existsFile(FILE_PATH_ROUTE_FILE_BAK))
			{
				DebugUtil
					.commonDebugWarn("LinuxNetConfigImpl.refreshShellRouteConfig(): File aerohive-routes(bak) doesn't exist,Create it!");
				FileManager.getInstance().createFilewithDir(
					FILE_PATH_ROUTE_FILE_BAK.substring(0,
						FILE_PATH_ROUTE_FILE_BAK.lastIndexOf("/")),
					FILE_PATH_ROUTE_FILE_BAK.substring(FILE_PATH_ROUTE_FILE_BAK
						.lastIndexOf("/") + 1), new String[0]);
			}

			// at first, clear file content
			FileManager.getInstance().deletefile(FILE_PATH_ROUTE_FILE);
			FileManager.getInstance().createFile(FILE_PATH_ROUTE_FILE,
				new String[0]);
			// bak file
			if (!FileManager.getInstance().existsFile(FILE_PATH_ROUTE_FILE_BAK))
			{
				FileManager.getInstance().createFilewithDir(
					FILE_PATH_ROUTE_FILE_BAK.substring(0,
						FILE_PATH_ROUTE_FILE_BAK.lastIndexOf("/")),
					FILE_PATH_ROUTE_FILE_BAK.substring(FILE_PATH_ROUTE_FILE_BAK
						.lastIndexOf("/") + 1), new String[0]);
			}
			else
			{
				FileManager.getInstance().deletefile(FILE_PATH_ROUTE_FILE_BAK);
				FileManager.getInstance().createFile(FILE_PATH_ROUTE_FILE_BAK,
					new String[0]);
			}

			// add route config to shell, refresh file content
			for (String routeInfo : fileContent) {
				String[] routes = routeInfo.split(" ");
				addRoute4Refresh(routes);
			}
		}
		catch (Exception e)
		{
			DebugUtil
				.commonDebugWarn(
					"LinuxNetConfigImpl.refreshShellRouteConfig() Catch exception ",
					e);
		}
	}

	/**
	 * add route to os & save to file modified from addRoute(),for restart
	 * software operation.
	 * 
	 * @param routeInfo
	 *            value: [destination,netmask,gateway]
	 * @return wrong message outputted by execute shell, return null when
	 *         succeed
	 */
	private String addRoute4Refresh(String[] routeInfo)
	{
		String destination = routeInfo[INDEX_DESTINATION];
		String netmask = routeInfo[INDEX_NETMASK];
		String gateway = routeInfo[INDEX_GATEWAY];

		// -net || -host
		String hostOrNetParam = getHostOrNetParam(destination, netmask);
		String cmd = "route add " + hostOrNetParam + " ";

		// MGT || LAN
		String eth = getDevName(gateway);
		if (eth == null)
		{
			return "Add failed. Please check gateway validity.";
		}

		if (hostOrNetParam.equals(ROUTEPARAM_TONET))
		{
			// to net
			cmd = cmd + destination + " netmask " + netmask + " gw " + gateway
				+ " " + eth;
		}
		else
		{
			// to host
			cmd = cmd + destination + " gw " + gateway + " " + eth;
		}

		String result = execShell(cmd);
		if (null == result)
		{
			// route add success
			saveRouteInfoToFile(routeInfo);
		}
		else
			if (result.indexOf("File exists") > 0)
			{
				// // SIOCADDRT: File exists
				// result = "Add failed. The same route already exists.";

				// route add success
				saveRouteInfoToFile(routeInfo);
			}
			else
				if (result.indexOf("Operation not permitted") > 0)
				{
					// SIOCADDRT: Operation not permitted
					result = "Add failed. Operation not permitted.";
				}
				else
					if (result.indexOf("Network is unreachable") > 0)
					{
						// SIOCADDRT: Network is unreachable
						result = "Add failed. Network is unreachable.";
					}
					else
					{
						// SIOCDELRT: No such process
						result = "Add failed. Please try again.";
					}

		if (result == null)
		{
			DebugUtil
				.commonDebugInfo("LinuxNetConfigImpl.addRoute4Refresh(): Add route success.destination="
					+ destination
					+ ",gateway="
					+ gateway
					+ ",netmask="
					+ netmask);
		}
		else
		{
			DebugUtil
				.commonDebugWarn("LinuxNetConfigImpl.addRoute4Refresh(): Add route failed. destination="
					+ destination
					+ ",gateway="
					+ gateway
					+ ",netmask="
					+ netmask + "  Failed msg=" + result);
		}

		return result; // if failed, result content exception message
	}

	/**
	 * get net config data
	 *
	 * @return data object in type NetConfigureDTO
	 */
	public NetConfigureDTO getNetConfig()
	{
		protect4OSConfig();
		
		NetConfigureDTO netDTO = new NetConfigureDTO();
		netDTO.setDomainName(getDomainName());
		netDTO.setGateway(getGateway());
		netDTO.setHostName(getHostName());
		netDTO.setIpAddress_eth1(getIP_eth1());
		netDTO.setEnabled_eth1(getEnable_Eth1());
		netDTO.setIpAddress_eth0(getIP_eth0());
		netDTO.setNetmask_eth1(getNetmask_eth1());
		netDTO.setNetmask_eth0(getNetmask_eth0());
		netDTO.setPrimaryDns(getPrimaryDNS());
		netDTO.setSecondDns(getSecondDNS());
		
		// set default dns servers
		if ("".equals(netDTO.getPrimaryDns()) && "".equals(netDTO.getSecondDns())) {
			try {
				updateDNSConfiguration(HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP1, HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP2, null);
				netDTO.setPrimaryDns(HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP1);
				netDTO.setSecondDns(HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP2);
			} catch (Exception ex) {
				DebugUtil.adminDebugError("set default dns servers in file error : "+ex.getMessage());
			}
		}
		netDTO.setTertiaryDns(getTertiaryDns());

		return netDTO;
	}
	
	/**
	 * protect method for configuration files
	 */
	private void protect4OSConfig() {
		// ifcfg-eth0
		try {
			if (!FileManager.getInstance().existsFile(str_Ifcfg_eth0)) {
				FileManager.getInstance().createFile(
						str_Ifcfg_eth0,
						new String[] { "DEVICE=eth0", "BOOTPROTO=static", "IPADDR=", "NETMASK=",
								"ONBOOT=yes", "TYPE=Ethernet", "GATEWAY=" });
			}

			String onBoot = FileManager.getInstance().readFile(str_Ifcfg_eth0, "ONBOOT");
			if (onBoot == null) {
				FileManager.getInstance().writeFile(str_Ifcfg_eth0, new String[] { "ONBOOT=yes" },
						true);
			} else if (!onBoot.equalsIgnoreCase("yes")) {
				FileManager.getInstance().writeFile(str_Ifcfg_eth0, "ONBOOT", "yes", null);
			}
		} catch (Exception e) {
			DebugUtil.error("protect4Config(): protect ifcfg-eth0 catch exception.", e);
		}
	}

	/**
	 * execute shell and output error message, if succeed , return null maybe should return more
	 * friendly message to user.
	 * 
	 * @param command -
	 * @return -
	 */
	private String execShell(String command)
	{
		try
		{
			Process process = Runtime.getRuntime().exec(command);
			InputStream is = process.getErrorStream();
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(is));
			process.waitFor();
			return reader.readLine();
		}
		catch (Exception e)
		{
			DebugUtil.commonDebugWarn(
				"LinuxNetConfigImpl.execShell() Catch exception: ", e);
		}

		return null;
	}

	/**
	 * remove route from shell
	 * 
	 * @param delRouteInfos -
	 * @return success list
	 */
	private List<String[]> removeRouteFromShell(List<String[]> delRouteInfos)
	{
		List<String[]> succList = new ArrayList<String[]>();
		for (String[] delRouteInfo : delRouteInfos)
		{
			String destination = delRouteInfo[INDEX_DESTINATION];
			String netmask = delRouteInfo[INDEX_NETMASK];
			String gateway = delRouteInfo[INDEX_GATEWAY];

			// MGT || LAN
			String eth = getDevName(gateway);
			if (eth == null)
			{
				succList.add(delRouteInfo);
				continue;
			}

			// -net || -host
			String hostOrNetParam = getHostOrNetParam(destination, netmask);
			String cmd = "route del " + hostOrNetParam + " ";

			if (hostOrNetParam.equals(ROUTEPARAM_TONET))
			{
				// to net
				cmd = cmd + destination + " netmask " + netmask + " " + eth;
			}
			else
			{
				// to host
				cmd = cmd + destination + " " + eth;
			}

			String result = execShell(cmd);
			if (result == null)
			{
				succList.add(delRouteInfo);
			}
		}

		return succList;
	}

	/**
	 * remove route from os & save result to file
	 * 
	 * @param delRouteInfos
	 *            item: [destination,netmask,gateway]
	 * @return wrong message outputted by execute shell, return null when
	 *         succeed
	 */
	public String removeRoute(List<String[]> delRouteInfos)
	{
		// 1. execute remove shell
		List<String[]> succList = removeRouteFromShell(delRouteInfos);

		// 2. returnMsg
		String returnMsg;
		if (succList.size() == delRouteInfos.size())
		{
			// all succeed
			returnMsg = null;
		}
		else
			if (succList.size() == 0)
			{
				// all failed
				returnMsg = MgrUtil.getUserMessage("hm.system.log.linux.net.config.add.all.fail");
				return returnMsg;
			}
			else
			{
				returnMsg = MgrUtil.getUserMessage("hm.system.log.linux.net.config.add.part.fail");
			}

		// 3. refresh file content
		String[][] delArray = new String[succList.size()][];
		removeRouteInfoFromFile(succList.toArray(delArray));

		return returnMsg;
	}

	/**
	 * add route to os & save to file
	 * 
	 * @param routeInfo
	 *            value: [destination,netmask,gateway]
	 * @return wrong message outputted by execute shell, return null when
	 *         succeed
	 */
	public String addRoute(String[] routeInfo)
	{
		String destination = routeInfo[INDEX_DESTINATION];
		String netmask = routeInfo[INDEX_NETMASK];
		String gateway = routeInfo[INDEX_GATEWAY];

		// -net || -host
		String hostOrNetParam = getHostOrNetParam(destination, netmask);
		String cmd = "route add " + hostOrNetParam + " ";

		// MGT || LAN
		String eth = getDevName(gateway);
		if (eth == null)
		{
			return MgrUtil.getUserMessage("hm.system.log.linux.net.config.add.fail5");
		}

		if (hostOrNetParam.equals(ROUTEPARAM_TONET))
		{
			// to net
			cmd = cmd + destination + " netmask " + netmask + " gw " + gateway
				+ " " + eth;
		}
		else
		{
			// to host
			cmd = cmd + destination + " gw " + gateway + " " + eth;
		}

		String result = execShell(cmd);
		if (null == result)
		{
			// route add success
			saveRouteInfoToFile(routeInfo);
		}
		else
			if (result.indexOf("File exists") > 0)
			{
				// SIOCADDRT: File exists
				result = MgrUtil.getUserMessage("hm.system.log.linux.net.config.add.fail4");
			}
			else
				if (result.indexOf("Operation not permitted") > 0)
				{
					// SIOCADDRT: Operation not permitted
					result = MgrUtil.getUserMessage("hm.system.log.linux.net.config.add.fail3");
				}
				else
					if (result.indexOf("Network is unreachable") > 0)
					{
						// SIOCADDRT: Network is unreachable
						result = MgrUtil.getUserMessage("hm.system.log.linux.net.config.add.fail2");
					}
					else
					{
						// SIOCDELRT: No such process
						result = MgrUtil.getUserMessage("hm.system.log.linux.net.config.add.fail");
					}

		if (result == null)
		{
			DebugUtil
				.commonDebugInfo("LinuxNetConfigImpl.addRoute(): Add route success.destination="
					+ destination
					+ ",gateway="
					+ gateway
					+ ",netmask="
					+ netmask);
		}
		else
		{
			DebugUtil
				.commonDebugWarn("LinuxNetConfigImpl.addRoute(): Add route failed. destination="
					+ destination
					+ ",gateway="
					+ gateway
					+ ",netmask="
					+ netmask + "  Failed msg=" + result);
		}

		return result; // if failed, result content exception message
	}

	/**
	 * write route info into file
	 * 
	 * @param routeInfo -
	 */
	private void saveRouteInfoToFile(String[] routeInfo)
	{
		try
		{
			String routeInfo_str = routeInfo[INDEX_DESTINATION] + " "
				+ routeInfo[INDEX_NETMASK] + " " + routeInfo[INDEX_GATEWAY];

			// route file,if exist,append info to file, if not, create it.
			if (FileManager.getInstance().existsFile(FILE_PATH_ROUTE_FILE))
			{
				// check whether file contain same line
				if (!FileManager.getInstance().isFileContentExisted(
					FILE_PATH_ROUTE_FILE, routeInfo_str))
				{
					FileManager.getInstance().writeFile(FILE_PATH_ROUTE_FILE,
						new String[]
						{ routeInfo_str }, true);
				}
			}
			else
			{
				FileManager.getInstance().createFile(FILE_PATH_ROUTE_FILE,
					new String[]
					{ routeInfo_str });
			}

			// bak file
			if (FileManager.getInstance().existsFile(FILE_PATH_ROUTE_FILE_BAK))
			{
				if (!FileManager.getInstance().isFileContentExisted(
					FILE_PATH_ROUTE_FILE_BAK, routeInfo_str))
				{
					FileManager.getInstance().writeFile(
						FILE_PATH_ROUTE_FILE_BAK, new String[]
						{ routeInfo_str }, true);
				}
			}
			else
			{
				FileManager.getInstance().createFile(FILE_PATH_ROUTE_FILE_BAK,
					new String[]
					{ routeInfo_str });
			}
		}
		catch (Exception e)
		{
			DebugUtil
				.commonDebugWarn(
					"LinuxNetConfigImpl.saveRouteInfoToFile(): Write route info into file, catch exception: ",
					e);
		}
	}

	/**
	 * remove route info from file
	 * 
	 * @param routeInfos -
	 */
	private void removeRouteInfoFromFile(String[][] routeInfos)
	{
		try
		{
			if (!FileManager.getInstance().existsFile(FILE_PATH_ROUTE_FILE))
			{
				DebugUtil
					.commonDebugWarn("LinuxNetConfigImpl.removeRouteInfoFromFile(): Remove route from file, but file aerohive-routes doesn't exist");
				return;
			}

			// 1. remove route from file content,and get new content
			String[] fileContent = FileManager.getInstance().readFile(
				FILE_PATH_ROUTE_FILE);
			if (fileContent.length == 0)
			{
				DebugUtil
					.commonDebugInfo("LinuxNetConfigImpl.removeRouteInfoFromFile(): Remove route from file, but file is empty");
				return;
			}

			List<String> newContent = new ArrayList<String>();
			String[] delRouteInfos = new String[routeInfos.length];
			for (int i = 0; i < routeInfos.length; i++)
			{
				delRouteInfos[i] = routeInfos[i][INDEX_DESTINATION] + " "
					+ routeInfos[i][INDEX_NETMASK] + " "
					+ routeInfos[i][INDEX_GATEWAY];
			}

			boolean isDelete = false;
			for (String content : fileContent) {
				for (String delRoute : delRouteInfos) {
					if (delRoute.equals(content)) {
						isDelete = true;
					}
				}

				if (!isDelete) {
					newContent.add(content);
				}

				isDelete = false;
			}

			String[] newArray = new String[newContent.size()];
			newContent.toArray(newArray);

			// 2. write new content to route file,if not exists,log error.
			// should not happen.
			FileManager.getInstance().writeFile(FILE_PATH_ROUTE_FILE, newArray,
				false);

			// bak file
			if (!FileManager.getInstance().existsFile(FILE_PATH_ROUTE_FILE_BAK))
			{
				DebugUtil
					.commonDebugWarn("LinuxNetConfigImpl.removeRouteInfoFromFile(): Remove route from file, but file aerohive-routes(bak) doesn't exist");
			}
			else
			{
				FileManager.getInstance().writeFile(FILE_PATH_ROUTE_FILE_BAK,
					newArray, false);
			}
		}
		catch (Exception e)
		{
			DebugUtil
				.commonDebugWarn(
					"LinuxNetConfigImpl.removeRouteInfoFromFile(): Catch exception: ",
					e);
		}
	}

	private final String	ROUTEPARAM_TONET	= "-net";

	private final String	ROUTEPARAM_TOHOST	= "-host";

	/**
	 * judge ip address is a host addr or a net addr
	 * 
	 * @param destIP -
	 * @param netmask -
	 * @return -
	 */
	private String getHostOrNetParam(String destIP, String netmask)
	{
		if (netmask.equals("255.255.255.255"))
		{
			return ROUTEPARAM_TOHOST;
		}
		else
		{
			return ROUTEPARAM_TONET;
		}

		// if (isValidSubnetAddress(destIP, netmask))
		// {
		// return ROUTEPARAM_TONET;
		// }
		// else
		// {
		// return ROUTEPARAM_TOHOST;
		// }
	}

	/**
	 * get eth name that it locate in the same subnet with route gateway
	 * 
	 * @param gateway_route -
	 * @return -
	 */
	private String getDevName(String gateway_route)
	{
		// get eth name
		String device_eth0 = null;
		String device_eth1 = null;

		try
		{
			device_eth0 = FileManager.getInstance().readFile(str_Ifcfg_eth0,
				"DEVICE");
			device_eth1 = FileManager.getInstance().readFile(str_Ifcfg_eth1,
				"DEVICE");
		}
		catch (FileNotFoundException e)
		{
			if (device_eth0 == null)
			{
				return null;
			}
		}
		catch (Exception e)
		{
			DebugUtil
				.commonDebugWarn(
					"LinuxNetConfigImpl.getDevName(): Read file aerohive-routes content,catch exception: ",
					e);

			return null;
		}

		// get default gateway in MGT's subnet or LAN's subnet
		String ip_MGT = getIP_eth0();
		String netmask_MGT = getNetmask_eth0();
		String ip_LAN = getIP_eth1();
		String netmask_LAN = getNetmask_eth1();
		boolean isEnableLan = getEnable_Eth1();
		if ((ip_MGT.length() > 0 && netmask_MGT.length() > 0)
			&& (isInSameSubnet(ip_MGT, netmask_MGT, gateway_route, netmask_MGT) || (!isEnableLan && isInSameSubnetWithOtherDomains(
				gateway_route, isEnableLan))))
		{
			return device_eth0;
		}
		else
			if ((ip_LAN.length() > 0 && netmask_LAN.length() > 0)
				&& (isInSameSubnet(ip_LAN, netmask_LAN, gateway_route,
					netmask_LAN) || (isEnableLan && isInSameSubnetWithOtherDomains(
					gateway_route, isEnableLan))))
			{
				return device_eth1;
			}
			else
			{
				return null;
			}
	}

	/**
	 * check whether or not ip1 and ip2 are in same subnet old version function
	 * see CommonFunction.checkSubnet()
	 * 
	 * @param ip1 -
	 * @param netmask1 -
	 * @param ip2 -
	 * @param netmask2 -
	 * @return -
	 */
	private boolean isInSameSubnet(
		String ip1,
		String netmask1,
		String ip2,
		String netmask2)
	{
		try
		{
			String[] ipArray1 = ip1.split("\\.");
			String[] maskArray1 = netmask1.split("\\.");
			String[] subnet1 = new String[4];

			String[] ipArray2 = ip2.split("\\.");
			String[] maskArray2 = netmask2.split("\\.");
			String[] subnet2 = new String[4];

			for (int i = 0; i < ipArray1.length; i++)
			{
				subnet1[i] = String.valueOf(Integer.valueOf(ipArray1[i])
					& Integer.valueOf(maskArray1[i]));
			}

			for (int i = 0; i < ipArray2.length; i++)
			{
				subnet2[i] = String.valueOf(Integer.valueOf(ipArray2[i])
					& Integer.valueOf(maskArray2[i]));
			}

			for (int i = 0; i < subnet1.length; i++)
			{
				if (!subnet1[i].equals(subnet2[i]))
				{
					return false;
				}
			}

			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * get Hivemanager eth ip address, if both eth active, return second
	 *
	 * @return -
	 */
	public String getHiveManagerIPAddr()
	{
		// get eth0
		Vector<String> vct_Ifcfg0 = readFromFile(str_Ifcfg_eth0);

		if (vct_Ifcfg0 != null)
		{
			String ip_eth0 = findString(vct_Ifcfg0, "IPADDR", 0, true, "=", 1);

			// HMOL deployed on Amazon doesn't necessarily have the "IPADDR" option in "/etc/sysconfig/network-scripts/ifcfg-eth0",
			// obtaining the IP of eth0 via Java interface is extremely necessary.
			if (ip_eth0 == null || ip_eth0.trim().isEmpty()) {
				try {
					ip_eth0 = getEth0Ip();
				} catch (Exception e) {
					log.error("getHiveManagerIPAddr", "Get ETH0 IP error.", e);
				}
			}

			String bootFlag = findString(vct_Ifcfg0, "ONBOOT", 0, true, "=", 1);
			if (bootFlag.equalsIgnoreCase("yes"))
			{
				return ip_eth0;
			}
		}
		
		// mark: from 3.4 version ,eth1 port function as HA port, eth0 function as HiveManager port
		// get eth1
		Vector<String> vct_Ifcfg1 = readFromFile(str_Ifcfg_eth1);

		if (vct_Ifcfg1 != null)
		{
			String ip_eth1 = findString(vct_Ifcfg1, "IPADDR", 0, true, "=", 1);
			String bootFlag = findString(vct_Ifcfg1, "ONBOOT", 0, true, "=", 1);
			// 2.1 and pre-version, lan port 's ip maybe be 0.0.0.0,
			if (bootFlag.equalsIgnoreCase("yes") && !ip_eth1.equals("0.0.0.0"))
			{
				return ip_eth1;
			}
		}

		return "";
	}

	/**
	 * get HiveManager eth netmask if both eth active, return second
	 *
	 * @return -
	 */
	public String getHiveManagerNetmask()
	{
		// get eth0
		Vector<String> vct_Ifcfg0 = readFromFile(str_Ifcfg_eth0);

		if (vct_Ifcfg0 != null)
		{
			String mask_eth0 = findString(vct_Ifcfg0, "NETMASK", 0, true, "=",
				1);
			String bootFlag = findString(vct_Ifcfg0, "ONBOOT", 0, true, "=", 1);
			if (bootFlag.equalsIgnoreCase("yes"))
			{
				return mask_eth0;
			}
		}
		
		// get eth1
		Vector<String> vct_Ifcfg1 = readFromFile(str_Ifcfg_eth1);

		if (vct_Ifcfg1 != null)
		{
			String ip_eth1 = findString(vct_Ifcfg1, "IPADDR", 0, true, "=", 1);
			String mask_eth1 = findString(vct_Ifcfg1, "NETMASK", 0, true, "=",
				1);
			String bootFlag = findString(vct_Ifcfg1, "ONBOOT", 0, true, "=", 1);
			if (bootFlag.equalsIgnoreCase("yes") && !ip_eth1.equals("0.0.0.0"))
			{
				return mask_eth1;
			}
		}

		return "";
	}

}