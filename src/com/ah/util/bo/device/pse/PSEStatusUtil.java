package com.ah.util.bo.device.pse;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.performance.AhDevicePSEPower;
import com.ah.bo.performance.AhPSEStatus;
import com.ah.util.bo.device.DeviceInterfaceUtil;
import com.ah.util.bo.device.DevicePortEventProperties;
import com.ah.util.bo.device.DeviceProperties;

public class PSEStatusUtil {
	
	/**
	 * get all pse ports that are supported by a certain model of device
	 * @param deviceModel
	 * @return
	 */
	public static List<AhPSEStatus> getAllSupportedPSEPorts(HiveAp hiveAp) {
		return getAllSupportedPSEPorts(hiveAp, new DeviceProperties(hiveAp));
	}
	public static List<AhPSEStatus> getAllSupportedPSEPorts(HiveAp hiveAp, DeviceProperties dProperties) {
		List<AhPSEStatus> pseStatusList = new ArrayList<AhPSEStatus>();
		
		if (dProperties == null) {
			dProperties = new DeviceProperties(hiveAp);
		}
		if (dProperties.isPSEPortSupport()) {
			List<Short> ports = dProperties.getPSEPorts();
			List<AhPSEStatus> existingPseStatus = getAllExistedPSEInfo(hiveAp);
			if (existingPseStatus != null
					&& existingPseStatus.size() > 0) {
				for (AhPSEStatus pseStatus : existingPseStatus) {
					if (ports.contains(pseStatus.getInterfType())) {
						pseStatusList.add(pseStatus);
						ports.remove((Short)pseStatus.getInterfType());
					}
				}
			}
			if (ports != null
					&& ports.size() > 0) {
				for (Short port : ports) {
					pseStatusList.add(genDefaultAhPSEStatus(port));
				}
			}
		}
		
		return pseStatusList;
	}
	
	public static AhDevicePSEPower getAhDevicePSEPower(HiveAp hiveAp) {
		return QueryUtil.findBoByAttribute(AhDevicePSEPower.class, 
				"mac", hiveAp.getMacAddress(), hiveAp.getOwner().getId());
	}
	
	private static List<AhPSEStatus> getAllExistedPSEInfo(HiveAp hiveAp) {
		List<AhPSEStatus> pseStatusList = QueryUtil.executeQuery(AhPSEStatus.class, null,
				new FilterParams("mac=:s1",
						new Object[] {hiveAp.getMacAddress()}), hiveAp.getOwner().getId());
		if (pseStatusList != null
				&& pseStatusList.size() > 0) {
			for (AhPSEStatus pseStatus : pseStatusList) {
				pseStatus.setInterfType(DevicePortEventProperties.getDeviceInterf(pseStatus.getInterfName()));
				pseStatus.setInterfName(DeviceInterfaceUtil.getDeviceIfNameWithCertainIfType(pseStatus.getInterfType()));
			}
		}
		return pseStatusList;
	}
	
	/**
	 * get pse info from DB, order by interface name
	 * 
	 * @param hiveAp
	 * @return
	 */
	public static List<AhPSEStatus> getAllExistedPSEInfoFromDb(HiveAp hiveAp) {
		/*
		 * make order by condition: 
		 * if interfname include string 'eth1/', fortmat it. e.g. eth1/1 -> eth1/001, eth1/21 -> eth1/021;
		 * else use original interfname. 
		 */
		//TODO SortParams sort = new SortParams("case when position('eth1/' in interfname) > 0 then lpad(substring(interfname from position('/' in interfname) + 1), 3, '0') else interfname end");
		SortParams sort = new SortParams("id");
		String macAddress = hiveAp.getMacAddress();
		macAddress = (macAddress == null ? macAddress : macAddress.toLowerCase());
		FilterParams filter = new FilterParams("lower(mac)", macAddress);
		List<AhPSEStatus> pseStatusList = QueryUtil.executeQuery(AhPSEStatus.class, sort,filter, hiveAp.getOwner().getId());
		return pseStatusList;
	}
	
	private static AhPSEStatus genDefaultAhPSEStatus(short ifType) {
		return genDefaultAhPSEStatus(ifType, DeviceInterfaceUtil.getDeviceIfNameWithCertainIfType(ifType));
	}
	
	private static AhPSEStatus genDefaultAhPSEStatus(short ifType, String ifName) {
		AhPSEStatus pseStatus = new AhPSEStatus();
		
		pseStatus.setInterfName(ifName);
		pseStatus.setInterfType(ifType);
		pseStatus.setStatus(AhInterface.PSE_STATUS_DISABLED);
		pseStatus.setPdType(AhInterface.PSE_PDTYPE_NONE);
		
		return pseStatus;
	}
}
