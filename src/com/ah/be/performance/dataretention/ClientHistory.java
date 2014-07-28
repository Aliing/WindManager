package com.ah.be.performance.dataretention;

import com.ah.be.event.BeBaseEvent;

public class ClientHistory extends BeBaseEvent {

	private static final long serialVersionUID = 7726394928165491369L;
	//associated
	public long timeStampWithAerohiveDeviceTimeZone;
	public String clientMAC;
	public String hostName;
	public String OS_type;
	public String networkDeviceMAC;
	public Long owner;
	public String userName;
	public String userProfile;
	public String eMail;
	public String SSId;
	public byte authentication;
	public byte[] ip4;
	public byte[] ip6;
	
	//clientinfo
	public String ip;
	
	//clientosinfo
	public String OS;
	
	private String option55;
	
	//watched
	public long clientMACor0ifAll;
	public boolean located;
	
	//monitored
	public boolean monitored;
	
	public static final short CLIENT_REQUEST_ASSOCIATE = 1;

	public static final short CLIENT_REQUEST_DEASSOCIATE = 2;

	public static final short CLIENT_REQUEST_CLIENTINFO = 3;

	public static final short CLIENT_REQUEST_CLIENTOSINFO = 4;
	
	public static final short CLIENT_REQUEST_WATCH = 5;
	
	public static final short CLIENT_REQUEST_MONITOR = 6;
	
	public int type;

	public long getTimeStampWithAerohiveDeviceTimeZone() {
		return timeStampWithAerohiveDeviceTimeZone;
	}

	public void setTimeStampWithAerohiveDeviceTimeZone(
			long timeStampWithAerohiveDeviceTimeZone) {
		this.timeStampWithAerohiveDeviceTimeZone = timeStampWithAerohiveDeviceTimeZone;
	}

	public String getClientMAC() {
		return clientMAC;
	}

	public void setClientMAC(String clientMAC) {
		this.clientMAC = clientMAC;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getOS_type() {
		return OS_type;
	}

	public void setOS_type(String oS_type) {
		OS_type = oS_type;
	}

	public String getNetworkDeviceMAC() {
		return networkDeviceMAC;
	}

	public void setNetworkDeviceMAC(String networkDeviceMAC) {
		this.networkDeviceMAC = networkDeviceMAC;
	}

	public Long getOwner() {
		return owner;
	}

	public void setOwner(Long owner) {
		this.owner = owner;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(String userProfile) {
		this.userProfile = userProfile;
	}

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public String getSSId() {
		return SSId;
	}

	public void setSSId(String sSId) {
		SSId = sSId;
	}

	public byte getAuthentication() {
		return authentication;
	}

	public void setAuthentication(byte authentication) {
		this.authentication = authentication;
	}

	public byte[] getIp4() {
		return ip4;
	}

	public void setIp4(byte[] ip4) {
		this.ip4 = ip4;
	}

	public byte[] getIp6() {
		return ip6;
	}

	public void setIp6(byte[] ip6) {
		this.ip6 = ip6;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getOS() {
		return OS;
	}

	public void setOS(String oS) {
		OS = oS;
	}

	public long getClientMACor0ifAll() {
		return clientMACor0ifAll;
	}

	public void setClientMACor0ifAll(long clientMACor0ifAll) {
		this.clientMACor0ifAll = clientMACor0ifAll;
	}

	public boolean isLocated() {
		return located;
	}

	public void setLocated(boolean located) {
		this.located = located;
	}

	public boolean isMonitored() {
		return monitored;
	}

	public void setMonitored(boolean monitored) {
		this.monitored = monitored;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getOption55() {
		return option55;
	}

	public void setOption55(String option55) {
		this.option55 = option55;
	} 
	
}
