package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.config.create.source.MacAddressTableInt;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.ForwardingDB;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.MacAddressLearningEntry;

public class MacAddressTableImpl implements MacAddressTableInt {
	
	//private HiveAp hiveAp;
	
	private ForwardingDB fdb;
	
	private List<Map<String, Object>> staticList = new ArrayList<Map<String, Object>>();
	
	private List<Integer> selectVlanList = new ArrayList<Integer>();
		
	public MacAddressTableImpl(HiveAp hiveAp) {
		//this.hiveAp = hiveAp;
		this.fdb = hiveAp.getForwardingDB();
		if (fdb != null) {
			for (MacAddressLearningEntry entry : fdb.getMacAddressEntries()) {
				int vlanId = entry.getVlanId();
				if (vlanId > 0) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("mac", getMacAddressStr(entry.getMacAddress()));
					map.put("vlan", vlanId);
					map.put("interface", DeviceInfType.getInstance(entry.getDeviceInfoConstant(), hiveAp.getHiveApModel()).getCLIName(hiveAp.getHiveApModel()));
					staticList.add(map);
				}				 
			}
			selectVlanList = fdb.getVlanList(); 
		}
	}
	
	private String getMacAddressStr(String macAddress) {
		if (macAddress == null) {
			return "";
		} 
		macAddress = macAddress.replace(":", "").replace(".", "").replace("-", "");
		if (macAddress.length() < 12) {
			return macAddress;
		} else {
			return macAddress.substring(0, 2) + "-" + macAddress.substring(2, 4) + "-" + macAddress.substring(4,6) + "-"
			     + macAddress.substring(6, 8) + "-" + macAddress.substring(8,10) + "-" + macAddress.substring(10,12);
		}
	}
	
	public int getMacAddressStaticSize() {
		return staticList.size();
	}
	
	public String getStaticMacAddress(int index) {
		return (String) staticList.get(index).get("mac");
	}
	
	public int getStaticVlanId(int index) {
		return (int) staticList.get(index).get("vlan");
	}
	
	public String getStaticInterface(int index) {
		return (String) staticList.get(index).get("interface");
	}
	
	public boolean isConfigFDB() {
		return fdb != null;
	}

    public int getIdleTimeout() {
    	return fdb.getIdleTimeout();
    }

	public List<MacAddressLearningEntry> getMacAddressLearningEntries() {
		return fdb.getMacAddressEntries();
	}
	
	public int getSelectVlanSize() {
		return selectVlanList.size();
	}
	
	public int getSelectVlanId(int index) {
		return selectVlanList.get(index);
	}
	
	public boolean isEnableMacLearnForAllVlans() {
		return fdb.isDisableMacLearnForAllVlans();		
	}
	
	public boolean isEnableNotification() {
		return fdb.isEnableNotification();
	}
	
	public int getNotificationInterval() {
		return fdb.getNotificationInterval();
	}
	
	public static void main(String args[]) {
		MacAddressTableImpl ob = new MacAddressTableImpl(new HiveAp());
		System.out.println(ob.getMacAddressStr("11-22-33-44-55-66"));
		System.out.println(ob.getMacAddressStr("1122.3344.5566"));
		System.out.println(ob.getMacAddressStr("11:22:33:44:55:66"));
		System.out.println(ob.getMacAddressStr("1122:3344:5566"));
	}

	
}
