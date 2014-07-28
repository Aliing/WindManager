package com.ah.util.bo.device;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.devices.impl.Device;

/**
 * 
 * @date Feb 15, 2012
 * @author wx
 *
 * used for: to fetch device properties from devices.properties with more meaningful name
 */
public class DeviceProperties {
	private HiveAp hiveAp = null;
	
	public DeviceProperties(Long deviceId) {
		this(QueryUtil.findBoById(HiveAp.class, deviceId));
	}
	
	public DeviceProperties(String mac) {
		this(QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", mac));
	}
	
	public DeviceProperties(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
	}
	
	/*
	 * pse section start
	 */
	public static boolean isPSEPortSupport4CertainModel(short model) {
		String[] ports = AhConstantUtil.getEnumValues(Device.SUPPORTED_PSE_ETH, model);
		if (ports != null
				&& ports.length > 0) {
			return true;
		}
		
		return false;
	}
	
	public boolean isPSEPortSupport() {
		if (!checkArgument()) {
			return false;
		}
		
		String[] ports = AhConstantUtil.getEnumValues(Device.SUPPORTED_PSE_ETH, this.hiveAp.getHiveApModel());
		if (ports != null
				&& ports.length > 0) {
			return true;
		}
		
		return false;
	}
	
	public List<Short> getPSEPorts() {
		if (!checkArgument()) {
			return null;
		}
		
		String[] ports = AhConstantUtil.getEnumValues(Device.SUPPORTED_PSE_ETH, this.hiveAp.getHiveApModel());
		if (ports != null
				&& ports.length > 0) {
			List<Short> psePorts = new ArrayList<Short>(ports.length);
			for (String port : ports) {
				psePorts.add(DeviceInterfaceUtil.getDeviceIfTypeWithCertainIfName(port));
			}
			return psePorts;
		}
		
		return null;
	}
	
	/*
	 * pse section end
	 */
	
	private boolean checkArgument() {
		if (this.hiveAp == null) {
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		//System.out.println(AhConstantUtil.isTrueAll(Device.SUPPORTED_PSE_ETH, HiveAp.HIVEAP_MODEL_340));
		//System.out.println(AhConstantUtil.isTrueAll(Device.SUPPORTED_PSE_ETH, HiveAp.HIVEAP_MODEL_BR200));
		//System.out.println(AhConstantUtil.isTrueAll(Device.SUPPORTED_PSE_ETH, HiveAp.HIVEAP_MODEL_BR200_WP));
	}
}
