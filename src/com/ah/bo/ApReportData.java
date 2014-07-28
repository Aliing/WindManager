package com.ah.bo;


public interface ApReportData extends HmBo {
	
	long getOwnerId();
	
	long getTimeStamp();
	
	void setAppSeconds(short appSeconds);
	
	void setOwnerId(long ownerId);
	
	void setTimeStamp(long timeStamp);
	
	void setApMac(String apMac);
	
	void setClientMac(String clientMac);
	
	void setPacketsUpLoad(int packetsUpLoad);
	
	void setPacketsDownLoad(int packetsDownLoad);
	
	void setBytesDownLoad(long bytesDownLoad);
	
	void setBytesUpLoad(long bytesUpLoad);
	
	void setPassThrough(boolean passThrough);
	
	void setApplication(short application);
	
	void setSeconds(short seconds);
	
	void setInterface4Client(short interface4Client);
	
	void setPeerInterface(short peerInterface);
	
	void setClientOsType(String clientOsType);
	
	void setUserName(String userName);
	
	void setUserProfile(int userProfile);
	
	void setUserProfileName(String userProfileName);
	
	void setVLan(int vLan);
	
	void setRadioType(short radioType);
	
	void setOsName(String osName);
	
	void setSsid(String ssid);
	
	void setHostName(String hostName);
}
