package com.ah.util.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.ConfigTemplateStormControl;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.util.MgrUtil;
import com.ah.util.bo.device.DeviceInterfaceUtil;

/**
 * used to generate some object/collections for general using
 * 
 */
public class BoGenerationUtil {

	public static Map<Long, ConfigTemplateSsid> genDefaultSsidInterfaces() {
		Map<Long, ConfigTemplateSsid> ssidInterfaces = new HashMap<Long, ConfigTemplateSsid>();
		
		ConfigTemplateSsid eth0 = new ConfigTemplateSsid();
		eth0.setInterfaceName(MgrUtil.getResourceString("config.configTemplate.eth0"));
		ssidInterfaces.put(ConfigTemplate.SSID_INTERFACES_MAPKEY_ETH0, eth0);
		ConfigTemplateSsid eth1 = new ConfigTemplateSsid();
		eth1.setInterfaceName(MgrUtil.getResourceString("config.configTemplate.eth1"));
		ssidInterfaces.put(ConfigTemplate.SSID_INTERFACES_MAPKEY_ETH1, eth1);
		ConfigTemplateSsid red0 = new ConfigTemplateSsid();
		red0.setInterfaceName(MgrUtil.getResourceString("config.configTemplate.red0"));
		ssidInterfaces.put(ConfigTemplate.SSID_INTERFACES_MAPKEY_RED0, red0);
		ConfigTemplateSsid agg0 = new ConfigTemplateSsid();
		agg0.setInterfaceName(MgrUtil.getResourceString("config.configTemplate.agg0"));
		ssidInterfaces.put(ConfigTemplate.SSID_INTERFACES_MAPKEY_AGG0, agg0);
		
		ConfigTemplateSsid eth2 = new ConfigTemplateSsid();
		eth2.setInterfaceName(MgrUtil.getResourceString("config.configTemplate.eth2"));
		ssidInterfaces.put(ConfigTemplate.SSID_INTERFACES_MAPKEY_ETH2, eth2);
		ConfigTemplateSsid eth3 = new ConfigTemplateSsid();
		eth3.setInterfaceName(MgrUtil.getResourceString("config.configTemplate.eth3"));
		ssidInterfaces.put(ConfigTemplate.SSID_INTERFACES_MAPKEY_ETH3, eth3);
		ConfigTemplateSsid eth4 = new ConfigTemplateSsid();
		eth4.setInterfaceName(MgrUtil.getResourceString("config.configTemplate.eth4"));
		ssidInterfaces.put(ConfigTemplate.SSID_INTERFACES_MAPKEY_ETH4, eth4);
		
		return ssidInterfaces;
	}
	
	public static List<ConfigTemplateStormControl> getDefaultStormControl(HmDomain onwer,ConfigTemplate configTemplate){
		List<ConfigTemplateStormControl> stormControlList = new ArrayList<ConfigTemplateStormControl>();
		
		ConfigTemplateStormControl interface_access = new ConfigTemplateStormControl();
		interface_access.setInterfaceType(MgrUtil.getResourceString("config.configTemplate.access"));
		interface_access.setRateLimitValue(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_DEFULT_VALUE);
		stormControlList.add(interface_access);
		ConfigTemplateStormControl interface_8021Q = new ConfigTemplateStormControl();
		interface_8021Q.setInterfaceType(MgrUtil.getResourceString("config.configTemplate.8021Q"));
		interface_8021Q.setRateLimitValue(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_DEFULT_VALUE);
		stormControlList.add(interface_8021Q);
		
		return stormControlList;
	}
	
	public static DeviceInterface genDefaultDeviceInterface(String ifName) {
		DeviceInterface deviceInterface = new DeviceInterface();
		deviceInterface.setInterfaceName(ifName);
		deviceInterface.setDeviceIfType(DeviceInterfaceUtil
				.getDeviceIfTypeWithCertainIfName(ifName));
		return deviceInterface;
	}
	
	public static DeviceInterface genDefaultDeviceInterface(short ifType) {
		DeviceInterface deviceInterface = new DeviceInterface();
		deviceInterface.setInterfaceName(MgrUtil
				.getEnumString("enum.switch.interface."+ifType));
		deviceInterface.setDeviceIfType(ifType);
		return deviceInterface;
	}
}
