package com.ah.be.common.cache;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhInterfaceStats;


public class ClientInfoBean {
	
	public static final int DATA_SOURCE_DEVICE = 0;
	
	public static final int DATA_SOURCE_DB = 1;
	
	private String userName;
	
	private String profileName;
	
	private int vlan;
	
	private String ssid;
	
	private String hostName;
	
	private String orginalOsInfo;
	
	private String osInfo;
	
	private boolean isOnline;
	
	private long timeout;
	
	private int radioType;
	
	private String option55;
	
	//record client mac, owner
	private String clientMac;
	
	private Long domainId;
	
	//insert or update client_device_info table flag
	private boolean writeDbFlag;
	
	private int dataSource = DATA_SOURCE_DEVICE;
	
	public ClientInfoBean() {
		super();
	}

	public ClientInfoBean(String clientMac, Long domainId, String userName, String profileName, int vlan,
			String ssid, String hostName, String osInfo, boolean isOnline, long timeout, int radioType) {
		super();
		this.clientMac = clientMac;
		this.domainId = domainId;
		this.userName = userName;
		this.profileName = profileName;
		this.vlan = vlan;
		this.ssid = ssid;
		this.hostName = hostName;
		this.osInfo = osInfo;
		this.isOnline = isOnline;
		this.timeout = timeout;
		this.radioType = radioType;
		
	}
	
	public void setAllClientOsInfo(String orginalOsInfo, String option55, String convertedOsInfo) {
		if ((StringUtils.isBlank(orginalOsInfo) || orginalOsInfo.equalsIgnoreCase("unknown")) && StringUtils.isBlank(option55)) {
			return;
		}
		this.setOrginalOsInfo(orginalOsInfo);
		this.setOption55(option55);
		if (StringUtils.isNotBlank(orginalOsInfo) && !orginalOsInfo.equalsIgnoreCase("unknown")) {
			this.osInfo = orginalOsInfo;
		} else if(StringUtils.isNotBlank(convertedOsInfo)) {
			this.osInfo = convertedOsInfo;
		}
		
	}

	public int getDataSource() {
		return dataSource;
	}

	public void setDataSource(int dataSource) {
		this.dataSource = dataSource;
	}

	public boolean isWriteDbFlag() {
		return writeDbFlag;
	}

	public void setWriteDbFlag(boolean writeDbFlag) {
		this.writeDbFlag = writeDbFlag;
	}

	public String getClientMac() {
		return clientMac;
	}

	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public int getVlan() {
		return vlan;
	}

	public void setVlan(int vlan) {
		this.vlan = vlan;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getOsInfo() {
		return osInfo;
	}

	public void setOsInfo(String osInfo) {
		this.osInfo = osInfo;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public int getRadioType() {
		return radioType;
	}

	public void setRadioTypeReal(int radioType) {
		this.radioType=radioType;
	}
	
	public void setRadioType(int radioType) {
		switch(radioType)
		{
		case AhAssociation.CLIENTMACPROTOCOL_BMODE:
		case AhAssociation.CLIENTMACPROTOCOL_GMODE:
		case AhAssociation.CLIENTMACPROTOCOL_NGMODE:
			this.radioType=AhInterfaceStats.RADIOTYPE_24G;
				break;
		case AhAssociation.CLIENTMACPROTOCOL_AMODE:
		case AhAssociation.CLIENTMACPROTOCOL_NAMODE:
		case AhAssociation.CLIENTMACPROTOCOL_ACMODE:
			this.radioType=AhInterfaceStats.RADIOTYPE_5G;
				break;
		default:
			this.radioType = AhInterfaceStats.RADIOTYPE_OTHER;
				break;			
		}
	}

	public String getOption55() {
		return option55;
	}

	public void setOption55(String option55) {
		this.option55 = option55;
	}

	public String getOrginalOsInfo() {
		return orginalOsInfo;
	}

	public void setOrginalOsInfo(String orginalOsInfo) {
		this.orginalOsInfo = orginalOsInfo;
	}
	
}
