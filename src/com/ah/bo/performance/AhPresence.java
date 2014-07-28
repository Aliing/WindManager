package com.ah.bo.performance;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("bd")
public class AhPresence {
	public static final byte NEW_CLIENT = 0;
	public static final byte AGING_CLIENT = 1;
	public static final byte EXIST_CLIENT = 2;
	
	@XStreamAlias("vs")
	private short versionNumber;
	@XStreamAlias("pf")
	private byte partnerFlag;
	@XStreamAlias("sn")
	private String sensorId;
	@XStreamAlias("sq")
	private long seqNumber;
	@XStreamAlias("tp")
	private String messageType;
	@XStreamAlias("ht")
	private List<AhPresenceDeviceInfo> deviceInfoList;
	@XStreamOmitField
	private String macAddress;
	@XStreamOmitField
	private byte triggerType;
	
	public short getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(short versionNumber) {
		this.versionNumber = versionNumber;
	}

	public byte getPartnerFlag() {
		return partnerFlag;
	}
	
	public String getSensorId() {
		return sensorId;
	}
	
	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public long getSeqNumber() {
		return seqNumber;
	}
	public void setSeqNumber(long seqNumber) {
		this.seqNumber = seqNumber;
	}
	
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public List<AhPresenceDeviceInfo> getDeviceInfoList() {
		return deviceInfoList;
	}
	public void setDeviceInfoList(List<AhPresenceDeviceInfo> deviceInfoList) {
		this.deviceInfoList = deviceInfoList;
	}
	
	public AhPresence() {
		partnerFlag = 1;
	}
	
	public static String macAddressFormat(String strMacAddress) {
		String ip = new String();
		if(12 != strMacAddress.length())
			return strMacAddress;
		
		ip = strMacAddress.substring(0, 2);
		for(int i = 1; i < 6; i++) {
			ip += ":" + strMacAddress.substring(i*2, i*2 + 2);
		}
		return ip;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public byte getTriggerType()
	{
		return triggerType;
	}
	
	public void setTriggerType(byte triggerType)
	{
		this.triggerType = triggerType;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}