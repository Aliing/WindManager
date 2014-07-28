package com.ah.be.communication.mo;

public class CredentialInfo {
	private String				vhmName;

	private String				dnsUrl;

//	public static final short	ACCOUNT_TYPE_TRIAL	= 1;
//	public static final short	ACCOUNT_TYPE_PERM	= 2;

	public static final short	USER_TYPE_EVAL				= 1;
	public static final short	USER_TYPE_PLAN_EVAL			= 2;
	public static final short	USER_TYPE_REGULAR			= 3;

	private short				vhmType				= USER_TYPE_EVAL;

	private int					validDays;

	private String				userName;

	private String				vhmId;

	private String				clearPassword;

	public String getClearPassword() {
		return clearPassword;
	}

	public void setClearPassword(String clearPassword) {
		this.clearPassword = clearPassword;
	}

	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		this.vhmName = vhmName;
	}

	public String getDnsUrl() {
		return dnsUrl;
	}

	public void setDnsUrl(String dnsUrl) {
		this.dnsUrl = dnsUrl;
	}

	public short getVhmType() {
		return vhmType;
	}

	public void setVhmType(short vhmType) {
		this.vhmType = vhmType;
	}

	public int getValidDays() {
		return validDays;
	}

	public void setValidDays(int validDays) {
		this.validDays = validDays;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getVhmId() {
		return vhmId;
	}

	public void setVhmId(String vhmId) {
		this.vhmId = vhmId;
	}

}
