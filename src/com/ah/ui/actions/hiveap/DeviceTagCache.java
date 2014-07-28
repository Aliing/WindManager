package com.ah.ui.actions.hiveap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;

public class DeviceTagCache {
	
	private static DeviceTagCache deviceTagCache;
	
	private Map<Long, Set<String>> hostNameMap = new HashMap<Long, Set<String>>();
	
	private Map<Long, Set<DeviceTag>> deviceTagMap = new HashMap<Long, Set<DeviceTag>>();
	
	private Map<Long, DeviceTagList> deviceTagListMap = new HashMap<Long, DeviceTagList>();
	
	private DeviceTagCache(){}
	
	public static DeviceTagCache getInstance(){
		if(deviceTagCache == null){
			deviceTagCache.initial();
		}
		return deviceTagCache;
	}
	
	public void initial(){
		deviceTagCache = null;
		deviceTagCache = new DeviceTagCache();
		String hostSql = "select hostName, owner, classificationTag1, classificationTag2, classificationTag3 from "+HiveAp.class.getSimpleName();
		List<?> hostRes = QueryUtil.executeNativeQuery(hostSql);
		if(hostRes != null && hostRes.isEmpty()){
			for(Object res : hostRes){
				Object[] argRes = (Object[])res;
				updateCache(argRes[0].toString(), Long.valueOf(argRes[1].toString()), 
						argRes[2].toString(), argRes[3].toString(), argRes[4].toString());
			}
		}
	}
	
	public void updateCache(HiveAp device){
		if(device == null){
			return;
		}
		updateCache(device.getHostName(), device.getOwner().getId(), 
				device.getClassificationTag1(), device.getClassificationTag2(), device.getClassificationTag3());
	}
	
	public void updateCache(String hostName, long domainId, String strTag1, String strTag2, String strTag3){
		if(hostNameMap.get(domainId) == null){
			hostNameMap.put(domainId, new HashSet<String>());
		}
		hostNameMap.get(domainId).add(hostName);
		
		DeviceTag tagObj = new DeviceTag(hostName, strTag1, strTag2, strTag3);
		if(deviceTagMap.get(domainId) == null){
			deviceTagMap.put(domainId, new HashSet<DeviceTag>());
		}
		deviceTagMap.get(domainId).add(tagObj);
		
		if(deviceTagListMap.get(domainId) == null){
			deviceTagListMap.put(domainId, new DeviceTagList());
		}
		deviceTagListMap.get(domainId).getTag1Set().add(strTag1);
		deviceTagListMap.get(domainId).getTag2Set().add(strTag2);
		deviceTagListMap.get(domainId).getTag3Set().add(strTag3);
	}
	
	public static class DeviceTagList{
		
		private Set<String> tag1Set, tag2Set, tag3Set;

		public Set<String> getTag1Set() {
			if(tag1Set == null){
				this.tag1Set = new HashSet<String>();
			}
			return tag1Set;
		}

		public Set<String> getTag2Set() {
			if(tag2Set == null){
				tag2Set = new HashSet<String>();
			}
			return tag2Set;
		}

		public Set<String> getTag3Set() {
			if(tag3Set == null){
				tag3Set = new HashSet<String>();
			}
			return tag3Set;
		}
		
	}
	
	public static class DeviceTag{
		
		private String hostName, tag1, tag2, tag3;

		public DeviceTag(String hostName, String tag1, String tag2, String tag3){
			this.tag1 = tag1;
			this.tag2 = tag2;
			this.tag3 = tag3;
			this.hostName = hostName;
		}
		
		public String getHostName() {
			return hostName;
		}

		public void setHostName(String hostName) {
			this.hostName = hostName;
		}
		
		public String getTag1() {
			return tag1;
		}

		public void setTag1(String tag1) {
			this.tag1 = tag1;
		}

		public String getTag2() {
			return tag2;
		}

		public void setTag2(String tag2) {
			this.tag2 = tag2;
		}

		public String getTag3() {
			return tag3;
		}

		public void setTag3(String tag3) {
			this.tag3 = tag3;
		}
		
		public boolean equals(DeviceTag deviceTage){
			boolean blnTag1, blnTag2, blnTag3;
			
			if(this.tag1 == null){
				blnTag1 = deviceTage.getTag1() == null;
			}else{
				blnTag1 = this.tag1.equals(deviceTage.getTag1());
			}
			
			if(this.tag2 == null){
				blnTag2 = deviceTage.getTag2() == null;
			}else{
				blnTag2 = this.tag2.equals(deviceTage.getTag2());
			}
			
			if(this.tag3 == null){
				blnTag3 = deviceTage.getTag3() == null;
			}else{
				blnTag3 = this.tag3.equals(deviceTage.getTag3());
			}
			
			return blnTag1 && blnTag2 && blnTag3;
		}
		
	}

}
