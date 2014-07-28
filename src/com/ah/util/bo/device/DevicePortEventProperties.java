package com.ah.util.bo.device;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.hiveap.AhInterface;

public class DevicePortEventProperties {
	public static Map<String, Short> portSupported = new HashMap<String, Short>(); 
	static {
		portSupported.clear();
		portSupported.put("eth0", AhInterface.DEVICE_IF_TYPE_ETH0);
		portSupported.put("eth1", AhInterface.DEVICE_IF_TYPE_ETH1);
		portSupported.put("eth2", AhInterface.DEVICE_IF_TYPE_ETH2);
		portSupported.put("eth3", AhInterface.DEVICE_IF_TYPE_ETH3);
		portSupported.put("eth4", AhInterface.DEVICE_IF_TYPE_ETH4);
		portSupported.put("eth5", AhInterface.DEVICE_IF_TYPE_ETH5);
		portSupported.put("eth6", AhInterface.DEVICE_IF_TYPE_ETH6);
		portSupported.put("eth7", AhInterface.DEVICE_IF_TYPE_ETH7);
		portSupported.put("eth8", AhInterface.DEVICE_IF_TYPE_ETH8);
	}
	
	public static short getDeviceInterf(String port) {
		if (StringUtils.isBlank(port)) {
			return -1;
		}
		
		String portLower = port.toLowerCase();
		if (portSupported.containsKey(portLower)) {
			return portSupported.get(portLower);
		}
		
		return -1;
	}
}
