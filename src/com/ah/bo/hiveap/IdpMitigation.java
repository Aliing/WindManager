package com.ah.bo.hiveap;

import java.util.List;

public class IdpMitigation {

	public static final byte ADD = 0; // Report and mitigation is on
	public static final byte REMOVEALL_STATUS_CHANGED = 1;
	public static final byte REMOVEALL_QUIET_TIME_OUT = 2;
	public static final byte REMOVEALL_DURATION_TIME_OUT = 3;
	public static final byte REMOVEALL_NO_EXEC = 4;
	public static final byte REPORTED = 5; // Only report, mitigation is not on
	public static final byte MITIGATE_START = 6;
	public static final byte REPORT_START = 7;
	
	private byte flag;
	private String bssid;
	private List<Idp> clients;

	public byte getFlag() {
		return flag;
	}

	public void setFlag(byte flag) {
		this.flag = flag;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public List<Idp> getClients() {
		return clients;
	}

	public void setClients(List<Idp> clients) {
		this.clients = clients;
	}

}
