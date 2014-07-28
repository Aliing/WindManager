package com.ah.ws.rest.models.hmapi;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "client")
public class RestClientModel {
	private String vendor;
    private String channel;
	private String RSSI;
	private String reportTime;
	private byte health;
	private String macAddress;
	private String localIpAddress;
	private String natIpAddress;
	private String hostName;
	private String userName;
	private String clientOS;
	private String location;
	private String vlan;
	private String type;
	private String lastTowHoursData;
	private String sessionStartTime;
	private String deviceName;
	private String signalToNoiseRatio;
	private String SSIDOrSecurityObj;
	private String interfaceName;
	private String clientAuthMethod;
	private String associationMode;
	private String BSSID;
	private String comment1;
	private String comment2;
	private String companyName;
	private String deviceMac;
	private String emailAddress;
	private String encryption;
	private String userProfileAttribute;
	private String vendorName;
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getRSSI() {
		return RSSI;
	}
	public void setRSSI(String rSSI) {
		RSSI = rSSI;
	}
	public byte getHealth() {
		return health;
	}
	public void setHealth(byte health) {
		this.health = health;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getLocalIpAddress() {
		return localIpAddress;
	}
	public void setLocalIpAddress(String localIpAddress) {
		this.localIpAddress = localIpAddress;
	}
	public String getNatIpAddress() {
		return natIpAddress;
	}
	public void setNatIpAddress(String natIpAddress) {
		this.natIpAddress = natIpAddress;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getClientOS() {
		return clientOS;
	}
	public void setClientOS(String clientOS) {
		this.clientOS = clientOS;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getVlan() {
		return vlan;
	}
	public void setVlan(String vlan) {
		this.vlan = vlan;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLastTowHoursData() {
		return lastTowHoursData;
	}
	public void setLastTowHoursData(String lastTowHoursData) {
		this.lastTowHoursData = lastTowHoursData;
	}
	public String getSessionStartTime() {
		return sessionStartTime;
	}
	public void setSessionStartTime(String sessionStartTime) {
		this.sessionStartTime = sessionStartTime;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getSignalToNoiseRatio() {
		return signalToNoiseRatio;
	}
	public void setSignalToNoiseRatio(String signalToNoiseRatio) {
		this.signalToNoiseRatio = signalToNoiseRatio;
	}
	public String getSSIDOrSecurityObj() {
		return SSIDOrSecurityObj;
	}
	public void setSSIDOrSecurityObj(String sSIDOrSecurityObj) {
		SSIDOrSecurityObj = sSIDOrSecurityObj;
	}
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getClientAuthMethod() {
		return clientAuthMethod;
	}
	public void setClientAuthMethod(String clientAuthMethod) {
		this.clientAuthMethod = clientAuthMethod;
	}
	public String getAssociationMode() {
		return associationMode;
	}
	public void setAssociationMode(String associationMode) {
		this.associationMode = associationMode;
	}
	public String getBSSID() {
		return BSSID;
	}
	public void setBSSID(String bSSID) {
		BSSID = bSSID;
	}
	public String getComment1() {
		return comment1;
	}
	public void setComment1(String comment1) {
		this.comment1 = comment1;
	}
	public String getComment2() {
		return comment2;
	}
	public void setComment2(String comment2) {
		this.comment2 = comment2;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getDeviceMac() {
		return deviceMac;
	}
	public void setDeviceMac(String deviceMac) {
		this.deviceMac = deviceMac;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getEncryption() {
		return encryption;
	}
	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}
	public String getUserProfileAttribute() {
		return userProfileAttribute;
	}
	public void setUserProfileAttribute(String userProfileAttribute) {
		this.userProfileAttribute = userProfileAttribute;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getReportTime() {
		return reportTime;
	}
	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
	}

}
