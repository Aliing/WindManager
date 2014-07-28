package com.ah.be.communication.mo;

public class SimulateHiveAPResult {
	private String	macAddress;

	private String	wtpName;

	private short	code;

	public short getCode() {
		return code;
	}

	public void setCode(short code) {
		this.code = code;
	}

	public SimulateHiveAPResult() {
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getWtpName() {
		return wtpName;
	}

	public void setWtpName(String wtpName) {
		this.wtpName = wtpName;
	}
}
