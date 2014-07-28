package com.ah.util;

import java.net.InetAddress;

import org.apache.commons.lang.StringUtils;

import com.ah.be.app.DebugUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.SingleTableItem;
import com.ah.ui.actions.config.ImportCsvFileAction;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class NetTool {
	
	public static String getHostName()
	{
		try
		{
		    InetAddress addr = InetAddress.getLocalHost();
		
		    return addr.getHostName();
		}
		catch(Exception ex)
		{	
			DebugUtil.adminDebugWarn("NetTool.getHostName() catch exception", ex);
			return "host";
		}
	}
	
	/**
	 * Change input string to valid ip object
	 *
	 *@param String inputValue, can be a range
	 *
	 *@return SingleTableItem: ipAddress, netmask, ipObjType
	 */
	public static SingleTableItem getIpObjectByInput(String inputValue, boolean isRange) {
		SingleTableItem ipObj = null;
		inputValue = inputValue.trim();
		if (!StringUtils.isBlank(inputValue)) {
			short ipType = 0;
			String ipStr = inputValue;
			String netStr = null;
			// it is an ipaddress "10.155.20.89"
			if (ImportCsvFileAction.getIpAddressWrongFlag(inputValue)) {
				// it is an ipaddress/netmask "10.155.20.89/255.255.255.0 or 10.155.20.89/24" 
				String[] ipNets = inputValue.split("/");
				if (ipNets.length == 2) {
					String ipStrTest = ipNets[0].trim();
					// check ipaddress format
					if (!ImportCsvFileAction.getIpAddressWrongFlag(ipStrTest)) {
						String netStrTest = ipNets[1].trim();
						try {
							netStr = AhDecoder.int2Netmask(Integer.parseInt(netStrTest));
							ipStr = ipStrTest;
							ipType = IpAddress.TYPE_IP_NETWORK;
						} catch (NumberFormatException nfe) {
							// check network format
							if (!ImportCsvFileAction.getNetmaskWrongFlag(netStrTest)) {
								ipStr = ipStrTest;
								netStr = netStrTest;
								ipType = IpAddress.TYPE_IP_NETWORK;
							}
						}
					}
				} else if(isRange) {
					String[] ipRange = inputValue.split("-");
					if (ipRange.length == 2) {
						String ip1 = ipRange[0].trim();
						String ip2 = ipRange[1].trim();
						// check ipaddress format
						if (!ImportCsvFileAction.getIpAddressWrongFlag(ip1) && !ImportCsvFileAction.getIpAddressWrongFlag(ip2)) {
							// the end ip is larger than start ip
							if (AhEncoder.ip2Long(ip2) > AhEncoder.ip2Long(ip1)) {
								ipStr = ip1;
								netStr = ip2;
								ipType = IpAddress.TYPE_IP_RANGE;
							}
						}
					}
				}
			} else {
				ipType = IpAddress.TYPE_IP_ADDRESS;
			}
			if (ipType == 0) {
				ipType = IpAddress.TYPE_HOST_NAME;
			}
			ipObj = new SingleTableItem();
			ipObj.setIpAddress(ipStr);
			ipObj.setNetmask(netStr);
			ipObj.setType(ipType);
		}
		return ipObj;
	}
}
