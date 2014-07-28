package com.ah.ws.rest.models.hmapi;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "device")
public class RestDeviceModel {
	private String audit;

	private String hostName;
	private String alarm;
	private String interfaceIp;
	private String externalIp;
	private String topologyName;
	private String nodeId;
	private boolean connection;
	private String deviceMode;
	private int clients;
	private String upTime;
	private String hiveOS;
	private String deviceFunciton;
	private String appSignature;
	private String countryCode;
	private String defaultGateWay;
	private String dhcpClient;
	private String discoveryTime;
	private String eth0LLDPPort;
	private String eth0LLDPSysId;
	private String eth0LLDPSysName;
	private String eth1LLDPPort;
	private String eth1LLDPSysId;
	private String eth1LLDPSysName;
	private String hive;

	private String HWModel;
	private String inOrOutDoor;
	private String location;
	private String mgtVlan;
	private String nativeVlan;
	private String netmask;
	private String networkPolicy;
	private String serialNumber;
	private String wifi0Channel;
	private String wifi0Power;
	private String wifi0RadioProfile;
	private String wifi1Channel;
	private String wifi1Power;
	private String wifi1RadioProfile;

	public String getAudit() {
		return audit;
	}

	public void setAudit(String audit) {
		this.audit = audit;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getAlarm() {
		return alarm;
	}

	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}

	public String getInterfaceIp() {
		return interfaceIp;
	}

	public void setInterfaceIp(String interfaceIp) {
		this.interfaceIp = interfaceIp;
	}

	public String getExternalIp() {
		return externalIp;
	}

	public void setExternalIp(String externalIp) {
		this.externalIp = externalIp;
	}

	public String getTopologyName() {
		return topologyName;
	}

	public void setTopologyName(String topologyName) {
		this.topologyName = topologyName;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public boolean isConnection() {
		return connection;
	}

	public void setConnection(boolean connection) {
		this.connection = connection;
	}

	public String getDeviceMode() {
		return deviceMode;
	}

	public void setDeviceMode(String deviceMode) {
		this.deviceMode = deviceMode;
	}

	public int getClients() {
		return clients;
	}

	public void setClients(int clients) {
		this.clients = clients;
	}

	public String getUpTime() {
		return upTime;
	}

	public void setUpTime(String upTime) {
		this.upTime = upTime;
	}

	public String getHiveOS() {
		return hiveOS;
	}

	public void setHiveOS(String hiveOS) {
		this.hiveOS = hiveOS;
	}

	public String getDeviceFunciton() {
		return deviceFunciton;
	}

	public void setDeviceFunciton(String deviceFunciton) {
		this.deviceFunciton = deviceFunciton;
	}

	public String getAppSignature() {
		return appSignature;
	}

	public void setAppSignature(String appSignature) {
		this.appSignature = appSignature;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getDefaultGateWay() {
		return defaultGateWay;
	}

	public void setDefaultGateWay(String defaultGateWay) {
		this.defaultGateWay = defaultGateWay;
	}

	public String getDhcpClient() {
		return dhcpClient;
	}

	public void setDhcpClient(String dhcpClient) {
		this.dhcpClient = dhcpClient;
	}

	public String getDiscoveryTime() {
		return discoveryTime;
	}

	public void setDiscoveryTime(String discoveryTime) {
		this.discoveryTime = discoveryTime;
	}

	public String getEth0LLDPPort() {
		return eth0LLDPPort;
	}

	public void setEth0LLDPPort(String eth0lldpPort) {
		eth0LLDPPort = eth0lldpPort;
	}

	public String getEth0LLDPSysId() {
		return eth0LLDPSysId;
	}

	public void setEth0LLDPSysId(String eth0lldpSysId) {
		eth0LLDPSysId = eth0lldpSysId;
	}

	public String getEth0LLDPSysName() {
		return eth0LLDPSysName;
	}

	public void setEth0LLDPSysName(String eth0lldpSysName) {
		eth0LLDPSysName = eth0lldpSysName;
	}

	public String getEth1LLDPPort() {
		return eth1LLDPPort;
	}

	public void setEth1LLDPPort(String eth1lldpPort) {
		eth1LLDPPort = eth1lldpPort;
	}

	public String getEth1LLDPSysId() {
		return eth1LLDPSysId;
	}

	public void setEth1LLDPSysId(String eth1lldpSysId) {
		eth1LLDPSysId = eth1lldpSysId;
	}

	public String getEth1LLDPSysName() {
		return eth1LLDPSysName;
	}

	public void setEth1LLDPSysName(String eth1lldpSysName) {
		eth1LLDPSysName = eth1lldpSysName;
	}

	public String getHive() {
		return hive;
	}

	public void setHive(String hive) {
		this.hive = hive;
	}

	public String getHWModel() {
		return HWModel;
	}

	public void setHWModel(String hWModel) {
		HWModel = hWModel;
	}

	public String getInOrOutDoor() {
		return inOrOutDoor;
	}

	public void setInOrOutDoor(String inOrOutDoor) {
		this.inOrOutDoor = inOrOutDoor;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMgtVlan() {
		return mgtVlan;
	}

	public void setMgtVlan(String mgtVlan) {
		this.mgtVlan = mgtVlan;
	}

	public String getNativeVlan() {
		return nativeVlan;
	}

	public void setNativeVlan(String nativeVlan) {
		this.nativeVlan = nativeVlan;
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	public String getNetworkPolicy() {
		return networkPolicy;
	}

	public void setNetworkPolicy(String networkPolicy) {
		this.networkPolicy = networkPolicy;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getWifi0Channel() {
		return wifi0Channel;
	}

	public void setWifi0Channel(String wifi0Channel) {
		this.wifi0Channel = wifi0Channel;
	}

	public String getWifi0Power() {
		return wifi0Power;
	}

	public void setWifi0Power(String wifi0Power) {
		this.wifi0Power = wifi0Power;
	}

	public String getWifi0RadioProfile() {
		return wifi0RadioProfile;
	}

	public void setWifi0RadioProfile(String wifi0RadioProfile) {
		this.wifi0RadioProfile = wifi0RadioProfile;
	}

	public String getWifi1Channel() {
		return wifi1Channel;
	}

	public void setWifi1Channel(String wifi1Channel) {
		this.wifi1Channel = wifi1Channel;
	}

	public String getWifi1Power() {
		return wifi1Power;
	}

	public void setWifi1Power(String wifi1Power) {
		this.wifi1Power = wifi1Power;
	}

	public String getWifi1RadioProfile() {
		return wifi1RadioProfile;
	}

	public void setWifi1RadioProfile(String wifi1RadioProfile) {
		this.wifi1RadioProfile = wifi1RadioProfile;
	}

}
