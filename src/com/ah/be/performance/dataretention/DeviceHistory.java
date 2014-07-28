package com.ah.be.performance.dataretention;

import java.util.Calendar;

import com.ah.be.performance.dataretention.NetworkDeviceConfigTracking.VlanObj;
import com.ah.be.event.BeBaseEvent;

public class DeviceHistory extends BeBaseEvent  {

	private static final long serialVersionUID = -6557403812516388619L;
	//topology change
	private Calendar timeStampWithDeviceTimeZone;
	private Long vHMdomain;
	private String deviceMAC;
	private byte hours2GMT;
	private long[] topologyGroupPkFromTopToBottom;
	private long topologyGroupPK;
	private long topologyContainerPK;
	private boolean wiFi0is5GHz;
	private long networkPolicyPK;
	private String[] SSIDs;
	private long[] userProfilePK;
	private VlanObj[] vLanId;
	private int milliSeconds2GMT;
	private String[] tags;
	
	//network device history operation
	public static final int DEVICE_HISTORY_TOPOLOGY_CHANGE = 1;
	public static final int DEVICE_HISTORY_TOPOLOGYGROUP_CHANGE = 2;
	public static final int DEVICE_HISTORY_POLICY_CHANGE = 3;
	public static final int DEVICE_HISTORY_TAGS_CHANGE = 4;
	
	private int type;
	
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Calendar getTimeStampWithDeviceTimeZone() {
		return timeStampWithDeviceTimeZone;
	}
	public void setTimeStampWithDeviceTimeZone(Calendar timeStampWithDeviceTimeZone) {
		this.timeStampWithDeviceTimeZone = timeStampWithDeviceTimeZone;
	}
	public Long getvHMdomain() {
		return vHMdomain;
	}
	public void setvHMdomain(Long vHMdomain) {
		this.vHMdomain = vHMdomain;
	}
	public String getDeviceMAC() {
		return deviceMAC;
	}
	public void setDeviceMAC(String deviceMAC) {
		this.deviceMAC = deviceMAC;
	}
	public byte getHours2GMT() {
		return hours2GMT;
	}
	public void setHours2GMT(byte hours2gmt) {
		hours2GMT = hours2gmt;
	}
	public long[] getTopologyGroupPkFromTopToBottom() {
		return topologyGroupPkFromTopToBottom;
	}
	public void setTopologyGroupPkFromTopToBottom(
			long[] topologyGroupPkFromTopToBottom) {
		this.topologyGroupPkFromTopToBottom = topologyGroupPkFromTopToBottom;
	}
	public long getTopologyGroupPK() {
		return topologyGroupPK;
	}
	public void setTopologyGroupPK(long topologyGroupPK) {
		this.topologyGroupPK = topologyGroupPK;
	}
	public long getTopologyContainerPK() {
		return topologyContainerPK;
	}
	public void setTopologyContainerPK(long topologyContainerPK) {
		this.topologyContainerPK = topologyContainerPK;
	}
	public boolean isWiFi0is5GHz() {
		return wiFi0is5GHz;
	}
	public void setWiFi0is5GHz(boolean wiFi0is5GHz) {
		this.wiFi0is5GHz = wiFi0is5GHz;
	}
	public long getNetworkPolicyPK() {
		return networkPolicyPK;
	}
	public void setNetworkPolicyPK(long networkPolicyPK) {
		this.networkPolicyPK = networkPolicyPK;
	}
	public String[] getSSIDs() {
		return SSIDs;
	}
	public void setSSIDs(String[] sSIDs) {
		SSIDs = sSIDs;
	}
	public long[] getUserProfilePK() {
		return userProfilePK;
	}
	public void setUserProfilePK(long[] userProfilePK) {
		this.userProfilePK = userProfilePK;
	}
	public VlanObj[] getvLanId() {
		return vLanId;
	}
	public void setvLanId(VlanObj[] vLanId) {
		this.vLanId = vLanId;
	}
	public int getMilliSeconds2GMT() {
		return milliSeconds2GMT;
	}
	public void setMilliSeconds2GMT(int milliSeconds2GMT) {
		this.milliSeconds2GMT = milliSeconds2GMT;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	
	
	

}
