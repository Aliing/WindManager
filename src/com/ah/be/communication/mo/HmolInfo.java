package com.ah.be.communication.mo;

import com.ah.bo.admin.HMServicesSettings;

public class HmolInfo {

	private String hmolName;

	public static final short HHMTYPE_DEMO = 1;
	public static final short HHMTYPE_PLANNER = 2;
	public static final short HHMTYPE_PRODUCT = 3;

	private short hmolType = HHMTYPE_DEMO;

	private String hmolVersion;

	private String hmolIpAddress;

	private String virtualHostName;

	private int heartbeatTimeOutValue;

	private int maxApNum;

	private int maxVhmNum;

	private int leftApCount; // reserve for 3.4 upgrade

	private int leftVhmCount; // reserve for 3.4 upgrade

	private String userName; // reserver for 3.4 upgrade

	private String password; // reserver for 3.4 upgrade

	private String macAddress;

	private String systemId;

	public static final short HHMSTATUS_NORMAL = HMServicesSettings.HM_OLINE_STATUS_NORMAL;
	public static final short HHMSTATUS_MAINTAIN = HMServicesSettings.HM_OLINE_STATUS_MAINT;

	private short hmolStatus = HHMSTATUS_NORMAL;

	public String getHmolName() {
		return hmolName;
	}

	public void setHmolName(String hmolName) {
		this.hmolName = hmolName;
	}

	public short getHmolType() {
		return hmolType;
	}

	public void setHmolType(short hmolType) {
		this.hmolType = hmolType;
	}

	public String getHmolVersion() {
		return hmolVersion;
	}

	public void setHmolVersion(String hmolVersion) {
		this.hmolVersion = hmolVersion;
	}

	public String getHmolIpAddress() {
		return hmolIpAddress;
	}

	public void setHmolIpAddress(String hmolIpAddress) {
		this.hmolIpAddress = hmolIpAddress;
	}

	public int getMaxApNum() {
		return maxApNum;
	}

	public void setMaxApNum(int maxApNum) {
		this.maxApNum = maxApNum;
	}

	public int getMaxVhmNum() {
		return maxVhmNum;
	}

	public void setMaxVhmNum(int maxVhmNum) {
		this.maxVhmNum = maxVhmNum;
	}

	public int getLeftApCount() {
		return leftApCount;
	}

	public void setLeftApCount(int leftApCount) {
		this.leftApCount = leftApCount;
	}

	public int getLeftVhmCount() {
		return leftVhmCount;
	}

	public void setLeftVhmCount(int leftVhmCount) {
		this.leftVhmCount = leftVhmCount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public void setHmolStatus(short hmolStatus) {
		this.hmolStatus = hmolStatus;
	}

	public short getHmolStatus() {
		return hmolStatus;
	}

	public String getVirtualHostName() {
		return virtualHostName;
	}

	public void setVirtualHostName(String virtualHostName) {
		this.virtualHostName = virtualHostName;
	}

	public int getHeartbeatTimeOutValue() {
		return heartbeatTimeOutValue;
	}

	public void setHeartbeatTimeOutValue(int heartbeatTimeOutValue) {
		this.heartbeatTimeOutValue = heartbeatTimeOutValue;
	}

}
