package com.ah.util.bo.device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.hiveap.AhInterface;
import com.ah.util.MgrUtil;
import com.ah.util.bo.BoGenerationUtil;
import com.ah.util.devices.impl.Device;

/**
 * 
 * @date Dec 26, 2011
 * @author wx
 *
 * used for: fetch settings from devices.properties for certain device
 */
public class DeviceInterfaceUtil {
	
	public static final String DEVICE_INTERFACE_NAME_ETH0 = 
		MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth0");
	public static final String DEVICE_INTERFACE_NAME_ETH1 = 
		MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth1");
	public static final String DEVICE_INTERFACE_NAME_ETH2 = 
		MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth2");
	public static final String DEVICE_INTERFACE_NAME_ETH3 = 
		MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth3");
	public static final String DEVICE_INTERFACE_NAME_ETH4 = 
		MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth4");
	public static final String DEVICE_INTERFACE_NAME_ETH5 = 
			MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth5");
	public static final String DEVICE_INTERFACE_NAME_ETH6 = 
			MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth6");
	public static final String DEVICE_INTERFACE_NAME_ETH7 = 
			MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth7");
	public static final String DEVICE_INTERFACE_NAME_ETH8 = 
			MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.port.eth8");
	public static final String DEVICE_INTERFACE_NAME_WIFI0 = 
			MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.wifi0");
	public static final String DEVICE_INTERFACE_NAME_WIFI1 = 
			MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.wifi1");
	public static final String DEVICE_INTERFACE_NAME_USB = 
		MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.usb");
	
	public static List<String> allSupportedPorts = Arrays.asList(
			new String[] {
					DEVICE_INTERFACE_NAME_ETH0,
					DEVICE_INTERFACE_NAME_ETH1,
					DEVICE_INTERFACE_NAME_ETH2,
					DEVICE_INTERFACE_NAME_ETH3,
					DEVICE_INTERFACE_NAME_ETH4,
					DEVICE_INTERFACE_NAME_ETH5,
					DEVICE_INTERFACE_NAME_ETH6,
					DEVICE_INTERFACE_NAME_ETH7,
					DEVICE_INTERFACE_NAME_ETH8,
					DEVICE_INTERFACE_NAME_WIFI0,
					DEVICE_INTERFACE_NAME_WIFI1,
					DEVICE_INTERFACE_NAME_USB
			});
	
	public static Map<Short, String> mapPortTypeAndName = new HashMap<Short, String>();
	static {
		mapPortTypeAndName.clear();
		mapPortTypeAndName.put(AhInterface.DEVICE_IF_TYPE_ETH0, DEVICE_INTERFACE_NAME_ETH0);
		mapPortTypeAndName.put(AhInterface.DEVICE_IF_TYPE_ETH1, DEVICE_INTERFACE_NAME_ETH1);
		mapPortTypeAndName.put(AhInterface.DEVICE_IF_TYPE_ETH2, DEVICE_INTERFACE_NAME_ETH2);
		mapPortTypeAndName.put(AhInterface.DEVICE_IF_TYPE_ETH3, DEVICE_INTERFACE_NAME_ETH3);
		mapPortTypeAndName.put(AhInterface.DEVICE_IF_TYPE_ETH4, DEVICE_INTERFACE_NAME_ETH4);
		mapPortTypeAndName.put(AhInterface.DEVICE_IF_TYPE_ETH5, DEVICE_INTERFACE_NAME_ETH5);
		mapPortTypeAndName.put(AhInterface.DEVICE_IF_TYPE_ETH6, DEVICE_INTERFACE_NAME_ETH6);
		mapPortTypeAndName.put(AhInterface.DEVICE_IF_TYPE_ETH7, DEVICE_INTERFACE_NAME_ETH7);
		mapPortTypeAndName.put(AhInterface.DEVICE_IF_TYPE_ETH8, DEVICE_INTERFACE_NAME_ETH8);
		mapPortTypeAndName.put(AhInterface.DEVICE_IF_TYPE_WIFI0, DEVICE_INTERFACE_NAME_WIFI0);
		mapPortTypeAndName.put(AhInterface.DEVICE_IF_TYPE_WIFI1, DEVICE_INTERFACE_NAME_WIFI1);
		mapPortTypeAndName.put(AhInterface.DEVICE_IF_TYPE_USB, DEVICE_INTERFACE_NAME_USB);
	}
	
	public static final String DEVICE_INTERFACE_LINK_STATUS_OK = 
		MgrUtil.getUserMessage("interface.ethx.device.interface.link.status.ok");
	public static final String DEVICE_INTERFACE_LINK_STATUS_FAILED = 
		MgrUtil.getUserMessage("interface.ethx.device.interface.link.status.failed");
	
	public static final String DEVICE_INTERFACE_ACCESSMODE_TRUNK =
		MgrUtil.getUserMessage("interface.ethx.device.interface.mode.trunk");
	public static final String DEVICE_INTERFACE_ACCESSMODE_ACCESS =
		MgrUtil.getUserMessage("interface.ethx.device.interface.mode.access");
	
	public static final String DEVICE_INTERFACE_EVENT_PORT_NAME_ETH0 = "eth0";
	public static final String DEVICE_INTERFACE_EVENT_PORT_NAME_ETH1 = "eth1";
	public static final String DEVICE_INTERFACE_EVENT_PORT_NAME_ETH2 = "eth2";
	public static final String DEVICE_INTERFACE_EVENT_PORT_NAME_ETH3 = "eth3";
	public static final String DEVICE_INTERFACE_EVENT_PORT_NAME_ETH4 = "eth4";
	public static final String DEVICE_INTERFACE_EVENT_PORT_NAME_ETH5 = "eth5";
	public static final String DEVICE_INTERFACE_EVENT_PORT_NAME_ETH6 = "eth6";
	public static final String DEVICE_INTERFACE_EVENT_PORT_NAME_ETH7 = "eth7";
	public static final String DEVICE_INTERFACE_EVENT_PORT_NAME_ETH8 = "eth8";
	public static final String DEVICE_INTERFACE_EVENT_PORT_NAME_USB = "ppp0";
	public static final String DEVICE_INTERFACE_EVENT_PORT_NAME_WIFI0 = "wifi0";
	public static final String DEVICE_INTERFACE_EVENT_PORT_NAME_WIFI1 = "wifi1";
	
	public static final String DEVICE_INTERFACE_EVENT_PORT_NAME_USB_3G = "ppp0";
	public static final String DEVICE_INTERFACE_EVENT_PORT_NAME_USB_4G = "usb0";
	
	public static Map<String, DeviceInterfaceAdapter> getDeviceInterfacesSupported(short deviceModel) {
		List<String> ethPorts = getDeviceEthPorts(deviceModel);
		boolean blnSupportUsb = isDeviceSupportInterface(Device.SUPPORTED_USB,deviceModel);
		boolean blnSupportWifi0 = isDeviceSupportInterface(Device.SUPPORTED_WIFI0,deviceModel);
		boolean blnSupportWifi1 = isDeviceSupportInterface(Device.SUPPORTED_WIFI1,deviceModel);
		if ((ethPorts == null || ethPorts.isEmpty())
				&& !blnSupportUsb && !blnSupportWifi0 && !blnSupportWifi1) {
			return null;
		}
		List<String> psePorts = getDevicePsePorts(deviceModel);
		
		Map<String, DeviceInterfaceAdapter> deviceInterfaceAdapters = new HashMap<String, DeviceInterfaceAdapter>();
		
		//generate device interface information from configuration
		DeviceInterfaceAdapter deviceInterfaceAdapter = null;
		for (String portOri : allSupportedPorts) {
			String port = StringUtils.lowerCase(portOri);
			boolean blnEthPort = false;
			boolean blnPsePort = false;
			if (ethPorts.contains(port)) {
				blnEthPort = true;
			}
			if (psePorts.contains(port)) {
				blnPsePort = true;
			}
			if (blnEthPort || blnPsePort) {
				deviceInterfaceAdapter = new DeviceInterfaceAdapter(BoGenerationUtil
						.genDefaultDeviceInterface(portOri));
				deviceInterfaceAdapter.setIfterLowerName(port);
				deviceInterfaceAdapter.setBlnSupportPSE(blnPsePort);
				deviceInterfaceAdapters.put(port, deviceInterfaceAdapter);
			}
		}
		if (blnSupportUsb) {
			String port = StringUtils.lowerCase(DEVICE_INTERFACE_NAME_USB);
			deviceInterfaceAdapter = new DeviceInterfaceAdapter(BoGenerationUtil
					.genDefaultDeviceInterface(DEVICE_INTERFACE_NAME_USB));
			deviceInterfaceAdapter.setIfterLowerName(port);
			deviceInterfaceAdapter.setBlnUsbPort(true);
			deviceInterfaceAdapters.put(port, deviceInterfaceAdapter);
		}
		
		if(blnSupportWifi0){
			String port = StringUtils.lowerCase(DEVICE_INTERFACE_NAME_WIFI0);
			deviceInterfaceAdapter = new DeviceInterfaceAdapter(BoGenerationUtil
					.genDefaultDeviceInterface(DEVICE_INTERFACE_NAME_WIFI0));
			deviceInterfaceAdapter.setIfterLowerName(port);
			deviceInterfaceAdapters.put(port, deviceInterfaceAdapter);
		}
		
		if(blnSupportWifi1){
			String port = StringUtils.lowerCase(DEVICE_INTERFACE_NAME_WIFI1);
			deviceInterfaceAdapter = new DeviceInterfaceAdapter(BoGenerationUtil
					.genDefaultDeviceInterface(DEVICE_INTERFACE_NAME_WIFI1));
			deviceInterfaceAdapter.setIfterLowerName(port);
			deviceInterfaceAdapters.put(port, deviceInterfaceAdapter);
		}
		
		return deviceInterfaceAdapters;
	}
	
	public static Map<String, DeviceInterfaceAdapter> getDeviceInterfacesSupported4Switch(short deviceModel) {
		List<String> ethPorts = getDeviceEthPorts(deviceModel);
		boolean blnSupportUsb = isDeviceSupportInterface(Device.SUPPORTED_USB,deviceModel);
		boolean blnSupportWifi0 = isDeviceSupportInterface(Device.SUPPORTED_WIFI0,deviceModel);
		boolean blnSupportWifi1 = isDeviceSupportInterface(Device.SUPPORTED_WIFI1,deviceModel);
		if ((ethPorts == null || ethPorts.isEmpty())
				&& !blnSupportUsb && !blnSupportWifi0 && !blnSupportWifi1) {
			return null;
		}
		
		Map<String, DeviceInterfaceAdapter> deviceInterfaceAdapters = new HashMap<String, DeviceInterfaceAdapter>();
		
		//generate device interface information from configuration
		DeviceInterfaceAdapter deviceInterfaceAdapter = null;
		for(String port : ethPorts){
			String portName = StringUtils.lowerCase(MgrUtil
					.getEnumString("enum.switch.interface."+port));
			deviceInterfaceAdapter = new DeviceInterfaceAdapter(BoGenerationUtil
					.genDefaultDeviceInterface(Short.valueOf(port)));
			deviceInterfaceAdapter.setIfterLowerName(portName);
			deviceInterfaceAdapter.setIfterNum(Short.valueOf(port));
			deviceInterfaceAdapters.put(portName, deviceInterfaceAdapter);
		}
		
		if (blnSupportUsb) {
			String portName = StringUtils.lowerCase(MgrUtil
					.getEnumString("enum.switch.interface."+AhInterface.DEVICE_IF_TYPE_USB));
			deviceInterfaceAdapter = new DeviceInterfaceAdapter(BoGenerationUtil
					.genDefaultDeviceInterface(AhInterface.DEVICE_IF_TYPE_USB));
			deviceInterfaceAdapter.setIfterLowerName(portName);
			deviceInterfaceAdapter.setIfterNum(Short.valueOf(AhInterface.DEVICE_IF_TYPE_USB));
			deviceInterfaceAdapter.setBlnUsbPort(true);
			deviceInterfaceAdapters.put(portName, deviceInterfaceAdapter);
		}
		
		return deviceInterfaceAdapters;
	}
	
	public static short getDeviceIfTypeWithCertainIfName(String ifName) {
		for (Iterator<Short> iter = mapPortTypeAndName.keySet().iterator(); iter.hasNext();) {
			Short key = iter.next();
			if (mapPortTypeAndName.get(key).equalsIgnoreCase(ifName)) {
				return key;
			}
		}
		return -1;
	}
	
	public static String getDeviceIfNameWithCertainIfType(Short ifType) {
		if (mapPortTypeAndName.containsKey(ifType)) {
			return mapPortTypeAndName.get(ifType);
		}
		return "";
	}
	
	private static List<String> getDeviceEthPorts(short deviceModel) {
		String[] ports = AhConstantUtil.getEnumValues(Device.ETH_PORTS, deviceModel);
		if (ports == null || ports.length == 0) {
			return new ArrayList<String>();
		}
		List<String> ethPorts = new ArrayList<String>(ports.length);
		for (String port : ports) {
			ethPorts.add(StringUtils.lowerCase(port));
		}
		return ethPorts;
	}
	
	private static List<String> getDevicePsePorts(short deviceModel) {
		String[] ports = AhConstantUtil.getEnumValues(Device.SUPPORTED_PSE_ETH, deviceModel);
		if (ports == null || ports.length == 0) {
			return new ArrayList<String>();
		}
		List<String> psePorts = new ArrayList<String>(ports.length);
		for (String port : ports) {
			psePorts.add(StringUtils.lowerCase(port));
		}
		return psePorts;
	}
	
	private static boolean isDeviceSupportInterface(Device decice,short deviceModel) {
		return AhConstantUtil.isTrueAll(decice, deviceModel);
	}
	
}
