package com.ah.be.common.cache;

import javax.persistence.Column;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.Folder;
import com.ah.bo.mgmt.impl.MapHierarchyCacheImpl;
import com.ah.bo.performance.AhInterfaceStats;

public class SimpleHiveAp {

	private Long id;
	private Long domainId;
	private String macAddress;
	private String hostname;
	private String serialNumber;
	private short manageStatus;
	private String softVer;
	private String capwapClientIp;
	private int activeClientCount;
	private int client24Count;
	private int client5Count;
	private int clientWireCount;
	private Long mapContainerId;
	private short hiveApModel;
	private boolean simulated;
	private String ipAddress;
	private short deviceType;
	private short connectStatus = HiveAp.CONNECT_DOWN;
	
	private int wifi0RadioType=AhInterfaceStats.RADIOTYPE_24G;
	private int wifi1RadioType=AhInterfaceStats.RADIOTYPE_5G;
	
	private String tag1;
	private String tag2;
	private String tag3;
	
	//capwap linkip
	@Column(length = 15)
	private String capwapLinkIp;
	
	public String getCapwapLinkIp() {
		return capwapLinkIp;
	}

	public void setCapwapLinkIp(String capwapLinkIp) {
		this.capwapLinkIp = capwapLinkIp;
	}

	//proxy info
	private String proxyName;
	private int proxyPort;
	private String proxyUsername;
	private String proxyPassword;
	
	//switch for delay alarm from Guadalupe
	private boolean enableDelayAlarm = true;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public short getManageStatus() {
		return manageStatus;
	}

	public void setManageStatus(short manageStatus) {
		this.manageStatus = manageStatus;
	}

	public String getSoftVer() {
		return softVer;
	}

	public void setSoftVer(String softVer) {
		this.softVer = softVer;
	}

	public int getActiveClientCount() {
		return activeClientCount;
	}

	public void setActiveClientCount(int activeClientCount) {
		this.activeClientCount = activeClientCount;
	}

	public Long getMapContainerId() {
		return mapContainerId;
	}

	public void setMapContainerId(Long mapContainerId) {
		this.mapContainerId = mapContainerId;
	}
	
	public String getMapName(){
		String result="-";
		if (mapContainerId!=null) {
		    Folder folder=MapHierarchyCacheImpl.getInstance().getFolder(mapContainerId);
            if (null != folder) {
                result = folder.getName();
            }
            //FIXME merge code from head
//			if(null!=mapContainerNode){
//				if(null==mapContainerNode.getParentMap() || mapContainerNode.getMapType() == 1){
//					result=mapContainerNode.getMapName();
//				}else{
//					result = mapContainerNode.getParentMap().getLabel()+"_"+mapContainerNode.getMapName();
//				}
//			}
		}
		return result;
	}

	public String getCapwapClientIp() {
		return capwapClientIp;
	}

	public void setCapwapClientIp(String capwapClientIp) {
		this.capwapClientIp = capwapClientIp;
	}
	
	public String getIpAddress() {
		return this.ipAddress;
	}
	
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof SimpleHiveAp))
			return false;

		final SimpleHiveAp ap = (SimpleHiveAp) o;

		if (macAddress != null ? !macAddress.equals(ap.macAddress) : ap.macAddress != null)
			return false;

//		if (hostname != null ? !hostname.equals(ap.hostname) : ap.hostname != null)
//			return false;
//
//		if (serialNumber != null ? !serialNumber.equals(ap.serialNumber) : ap.serialNumber != null)
//			return false;

		if (domainId != null ? !domainId.equals(ap.domainId) : ap.domainId != null)
			return false;

//		if (manageStatus != ap.manageStatus)
//			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = (macAddress != null ? macAddress.hashCode() : 0);
		result = 29 * result + domainId.intValue();
		return result;
	}

	public short getHiveApModel() {
		return hiveApModel;
	}

	public void setHiveApModel(short hiveApModel) {
		this.hiveApModel = hiveApModel;
	}

	public boolean isSimulated() {
		return simulated;
	}

	public void setSimulated(boolean simulated) {
		this.simulated = simulated;
	}

	public short getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(short deviceType) {
		this.deviceType = deviceType;
	}

	@Override
	public String toString() {
		return "ApMac=" + macAddress + "SoftVersion=" + softVer + "ApModel=" + hiveApModel + "isSimulated=" + simulated
				+ "IpAddress" + ipAddress +", Tag="+(tag1+"|"+tag2+"|"+tag3);
	}

	public int getWifi0RadioType() {
		return wifi0RadioType;
	}

	public void setWifi0RadioType(int wifi0RadioType) {
		this.wifi0RadioType = wifi0RadioType;
	}

	public int getWifi1RadioType() {
		return wifi1RadioType;
	}

	public void setWifi1RadioType(int wifi1RadioType) {
		this.wifi1RadioType = wifi1RadioType;
	}

    public String getTag1() {
        return tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public String getTag3() {
        return tag3;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }

	public String getProxyName() {
		return proxyName;
	}

	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}
    
	public boolean isPhysicalSwitch() {
		if(deviceType == HiveAp.Device_TYPE_SWITCH)
			return true;
		if(deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER) {
			HiveAp hiveAp = new HiveAp(this.hiveApModel);
			if(hiveAp.getDeviceInfo().isSptEthernetMore_24())
				return true;
		}
		return false;
	}

	public int getClient24Count() {
		return client24Count;
	}

	public void setClient24Count(int client24Count) {
		this.client24Count = client24Count;
	}

	public int getClient5Count() {
		return client5Count;
	}

	public void setClient5Count(int client5Count) {
		this.client5Count = client5Count;
	}

	public int getClientWireCount() {
		return clientWireCount;
	}

	public void setClientWireCount(int clientWireCount) {
		this.clientWireCount = clientWireCount;
	}

	public short getConnectStatus() {
		return connectStatus;
	}

	public void setConnectStatus(short connectStatus) {
		this.connectStatus = connectStatus;
	}

	public boolean isEnableDelayAlarm() {
		return enableDelayAlarm;
	}

	public void setEnableDelayAlarm(boolean enableDelayAlarm) {
		this.enableDelayAlarm = enableDelayAlarm;
	}
}