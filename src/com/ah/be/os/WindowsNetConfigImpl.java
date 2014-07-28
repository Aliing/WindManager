package com.ah.be.os;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import com.ah.be.app.DebugUtil;

/**
 * 
 *@filename		WindowsNetConfigImpl.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-9-17 02:30:54
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 */
public class WindowsNetConfigImpl implements NetConfigImplInterface {

	private final static String			WINCMD_GATEWAY	= "Default Gateway";

	public WindowsNetConfigImpl() {
	}

	/**
	 * get local hostname
	 *
	 * @return -
	 */
	public String getHostName()
	{
		try
		{
			InetAddress localHost = InetAddress.getLocalHost();
			return localHost.getHostName();
		}
		catch (UnknownHostException e)
		{
			// if no IP address for the host could be found, i think it cannot
			// happen if 127.0.0.1 exists
			DebugUtil
				.commonDebugWarn(
					"WindowsNetConfigImpl.getHostName(): Catch UnknownHostException: ",
					e);
		}

		return null;
	}

	/**
	 * get net interface list
	 *
	 * @return -
	 */
	private List<NetworkInterface> getNetInterfaces()
	{
		List<NetworkInterface> netInterfaces = new ArrayList<NetworkInterface>();

		try
		{
			Enumeration<NetworkInterface> enumi = NetworkInterface
				.getNetworkInterfaces();

			while (enumi.hasMoreElements())
			{
				NetworkInterface netface = enumi.nextElement();

				netInterfaces.add(netface);
			}

			return netInterfaces;
		}
		catch (SocketException e)
		{
			DebugUtil
				.commonDebugWarn(
					"WindowsNetConfigImpl.getNetInterfaces(): Catch SocketException: ",
					e);
		}

		return null;
	}

	/**
	 * get MGT interface's ip
	 *
	 * @return -
	 */
	public String getIP_eth0()
	{
		List<NetworkInterface> netInterfaces = getNetInterfaces();
		if (netInterfaces.size() <= 1)
		{
			return "";
		}

		NetworkInterface eth0 = netInterfaces.get(1);
		Enumeration<?> e = eth0.getInetAddresses();
		if (e.hasMoreElements())
		{
			InetAddress ip_eth0;
			try
			{
				// ignore multi-ip configure, just get first ip if exists
				ip_eth0 = (InetAddress) e.nextElement();
			}
			catch (NoSuchElementException ex)
			{
				// if net interface has no ip address
				DebugUtil
					.commonDebugWarn(
						"WindowsNetConfigImpl.getIP_eth0(): Catch NoSuchElementException: ",
						ex);
				return "";
			}

			return ip_eth0.getHostAddress();
		}

		return "";
	}

	/**
	 * get LAN interface's ip
	 *
	 * @return -
	 */
	public String getIP_eth1()
	{
		List<NetworkInterface> netInterfaces = getNetInterfaces();
		if (netInterfaces.size() <= 2)
		{
			return "";
		}

		NetworkInterface eth0 = netInterfaces.get(2);
		Enumeration<?> e = eth0.getInetAddresses();
		if (e.hasMoreElements())
		{
			InetAddress ip_eth1;
			try
			{
				ip_eth1 = (InetAddress) e.nextElement();
			}
			catch (NoSuchElementException ex)
			{
				// if net interface has no ip address
				DebugUtil
					.commonDebugWarn("WindowsNetConfigImpl.getIP_eth1(): Failed to get IP_ETH1. Only one NIC exists on current machine.");
				return "";
			}

			return ip_eth1.getHostAddress();
		}

		return "";
	}

	/**
	 * get enable of Lan port
	 *
	 * @return -
	 */
	public boolean getEnable_Eth1()
	{
		List<NetworkInterface> netInterfaces = getNetInterfaces();
		if (netInterfaces.size() <= 2)
		{
			return false;
		}

		NetworkInterface eth0 = netInterfaces.get(2);
		Enumeration<?> e = eth0.getInetAddresses();

		return e.hasMoreElements();
	}

	/**
	 * get net mask given masklength
	 * 
	 * @param maskLength -
	 * @return -
	 */
	private String getNetmask(int maskLength)
	{
		int[] mask = new int[4];
		int MASK8 = 255;
		int maskSize = 8;
		int shift;
		int ipRun = 4;

		if (!((maskLength > -1) && (maskLength < 33)))
		{
			maskLength = 32;
		}
		int startMask = (maskLength - 1) / maskSize;

		for (int i = 0; i < ipRun; i++)
		{
			// full mask
			if (i < startMask)
			{
				mask[i] = MASK8;
				// variable mask
			}
			else
				if (i == startMask)
				{
					shift = ((i + 1) * maskSize) - maskLength;
					mask[i] = (MASK8 << shift) & MASK8;
					// no mask
				}
				else
				{
					mask[i] = 0;
				}
		}

		StringBuilder createNetmask = new StringBuilder();
		createNetmask.append(mask[0]);
		for (int i = 1; i < ipRun; i++)
		{
			createNetmask.append(".").append(mask[i]);
		}

		return createNetmask.toString();
	}

	/**
	 * get MGT interface subnet mask
	 *
	 * @return -
	 */
	public String getNetmask_eth0()
	{
		List<NetworkInterface> netInterfaces = getNetInterfaces();
		if (netInterfaces.size() <= 1)
		{
			return "";
		}

		NetworkInterface eth0 = netInterfaces.get(1);
		List<InterfaceAddress> addrList = eth0.getInterfaceAddresses();
		if (addrList.size() == 0)
		{
			return "";
		}
		InterfaceAddress addr = addrList.get(0);
		return getNetmask(addr.getNetworkPrefixLength());
	}

	/**
	 * get LAN interface subnet mask
	 *
	 * @return -
	 */
	public String getNetmask_eth1()
	{
		List<NetworkInterface> netInterfaces = getNetInterfaces();
		if (netInterfaces.size() <= 2)
		{
			return "";
		}

		NetworkInterface eth0 = netInterfaces.get(2);
		List<InterfaceAddress> addrList = eth0.getInterfaceAddresses();
		if (addrList.size() == 0)
		{
			return "";
		}
		InterfaceAddress addr = addrList.get(0);
		return getNetmask(addr.getNetworkPrefixLength());
	}

	/**
	 * execute ipconfig cmd and get output
	 *
	 * @return -
	 * @throws IOException -
	 */
	private String winIpConfigCommand() throws IOException
	{
		Process p = Runtime.getRuntime().exec("ipconfig /all");
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

		StringBuilder buffer = new StringBuilder();
		for (;;)
		{
			int c = stdoutStream.read();
			if (c == -1)
				break;
			buffer.append((char) c);
		}
		String outputText = buffer.toString();
		stdoutStream.close();
		return outputText;
	}

	/**
	 * get default gateway
	 * 
	 * @param ipConfigOutput -
	 * @return -
	 */
	private String getDefaultGateway(String ipConfigOutput)
	{
		// String localHost = null;
		// try
		// {
		// localHost = InetAddress.getLocalHost().getHostAddress();
		// }
		// catch (java.net.UnknownHostException ex)
		// {
		// ex.printStackTrace();
		// }
		
		try {
			StringTokenizer tokenizer = new StringTokenizer(ipConfigOutput, "\n");

			while (tokenizer.hasMoreTokens())
			{
				String line = tokenizer.nextToken().trim();

				// //
				// if (line.indexOf(localHost) < 0)
				// {
				// continue;
				// }

				int position = line.indexOf(WINCMD_GATEWAY);
				if (position < 0)
				{
					continue;
				}

				position = line.indexOf(":");
				
				String defaultGateway = line.substring(position + 2).trim();

				if (defaultGateway.length() > 0)
				{
					return defaultGateway;
				}
			}

			return "";
			
		} catch (Exception e) {
			//gateway is blank will cause StringIndexOutOfBoundsException 
			return "";
		}
	}

	/**
	 * get Default gateway
	 *
	 * @return -
	 */
	public String getGateway()
	{
		try
		{
			return getDefaultGateway(winIpConfigCommand());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	public String getDomainName()
	{
		// NTSystem class is not including in jre for linux platform, so let's
		// find another solvement.
		// // There is no guarantee it will exist or behave the same even from
		// one
		// // build release to another.So be careful!
		// NTSystem system = new NTSystem();
		// String domainName = system.getDomain();
		// if (domainName == null)
		// {
		// return "";
		// }
		//
		// return domainName;
		return "";
	}

	/**
	 * get primary DNS
	 *
	 * @return -
	 */
	public String getPrimaryDNS()
	{
		// first resolvent
		// String dnsServers = null;
		//		
		// try
		// {
		// Hashtable<String,String> env = new Hashtable<String,String>();
		// env.put(Context.INITIAL_CONTEXT_FACTORY,
		// "com.sun.jndi.dns.DnsContextFactory");
		// DirContext ictx = new InitialDirContext(env);
		// dnsServers = (String)
		// ictx.getEnvironment().get("java.naming.provider.url");
		// }
		// catch (NamingException e)
		// {
		// }
		//		
		// if (dnsServers == null)
		// {
		// return "";
		// }
		//		
		// // then parse dnsServers(like: "DNS Servers: dns://10.155.3.249
		// dns://10.1.1.100")

		// second resolvent
//		List<?> nameservers = sun.net.dns.ResolverConfiguration.open()
//			.nameservers();
//		if (nameservers == null || nameservers.size() == 0)
//		{
//			return "";
//		}
//
//		return (String) nameservers.get(0);
		
		return ""; //mark: not invoke from sun jars
	}

	public String getSecondDNS()
	{
//		List<?> nameservers = sun.net.dns.ResolverConfiguration.open()
//			.nameservers();
//		if (nameservers == null || nameservers.size() <= 1)
//		{
//			return "";
//		}
//
//		return (String) nameservers.get(1);
		
		return ""; //mark: not invoke from sun jars
	}

	public String getTertiaryDns()
	{
//		List<?> nameservers = sun.net.dns.ResolverConfiguration.open()
//			.nameservers();
//		if (nameservers == null || nameservers.size() <= 2)
//		{
//			return "";
//		}
//
//		return (String) nameservers.get(2);
		
		return ""; //mark: not invoke from sun jars
	}

	public Vector<?> getRoutes()
	{
		return null;
	}

	/**
	 * @see com.ah.be.os.NetConfigImplInterface#getNetConfig()
	 */
	public NetConfigureDTO getNetConfig()
	{
		NetConfigureDTO netDTO = new NetConfigureDTO();
		netDTO.setDomainName(getDomainName());
		netDTO.setGateway(getGateway());
		netDTO.setHostName(getHostName());
		netDTO.setIpAddress_eth1(getIP_eth1());
		netDTO.setIpAddress_eth0(getIP_eth0());
		netDTO.setNetmask_eth1(getNetmask_eth1());
		netDTO.setNetmask_eth0(getNetmask_eth0());
		netDTO.setPrimaryDns(getPrimaryDNS());
		netDTO.setSecondDns(getSecondDNS());
		netDTO.setTertiaryDns(getTertiaryDns());
		// netDTO.setVctRoute(getRoute());
		netDTO.setEnabled_eth1(getEnable_Eth1());

		return netDTO;
	}

	public Vector<Vector<String>> getRoute()
	{
		return null;
	}

	public void updateNetConfig(NetConfigureDTO dto)
		throws IllegalArgumentException,
		IOException,
		BeNoPermissionException
	{
		throw new BeNoPermissionException(
			"No permission to execute this operation on windows platform!");
	}

	public Vector<String> updateRouteConfig(Vector<Vector<String>> arg_Route)
	{
		return null;
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
		return "add route isn't effective on windows platform now.";
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
		return "remove route isn't effective on windows platform now.";
	}

	/**
	 * get Hivemanager eth ip address, if both eth active, return second
	 *
	 * @return -
	 */
	public String getHiveManagerIPAddr()
	{
		// mark: from 3.4 version ,eth1 port function as HA port, eth0 function as HiveManager port
		String ip_eth0 = getIP_eth0();
		if (ip_eth0.length() > 0)
		{
			return ip_eth0;
		}

		return getIP_eth1();
	}

	/**
	 * get HiveManager eth netmask if both eth active, return second
	 *
	 * @return -
	 */
	public String getHiveManagerNetmask()
	{
		String mask_eth0 = getNetmask_eth0();
		if (mask_eth0.length() > 0)
		{
			return mask_eth0;
		}

		return getNetmask_eth1();
	}

	/**
	 * refresh shell route config,because routes config will be clear when
	 * restart network.
	 */
	public void refreshShellRouteConfig()
	{

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
		
	}
	
	/**
	 * update dns configuration only
	 *
	 * @param primaryDns -
	 * @param secondDns -
	 * @param tertiaryDns -
	 */
	public void updateDNSConfiguration(String primaryDns, String secondDns, String tertiaryDns)
			throws Exception{
		
	}

	@Override
	public void setLocalHostName(String newHostName) {
		// TODO Auto-generated method stub
		
	}

}