package com.ah.be.os;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

public interface NetConfigImplInterface
{
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
	 * get os hostname
	 *
	 *@param 
	 *
	 *@return
	 */
	public String getHostName();
	
	/**
	 * set cache hostname
	 * 
	 * @param
	 * @return
	 */
	public void  setLocalHostName(String newHostName);

	String getDomainName();
	
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

}