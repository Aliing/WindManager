package com.ah.bo.performance;

public class SlaDataField {

	private String apName;

	private String apMac;

	private String clientMac;

//	private String clientHostName;

	private int status;
	
	private int action;

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public String getClientMac() {
		return clientMac;
	}

	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}

//	public String getClientHostName() {
//		return clientHostName;
//	}
//
//	public void setClientHostName(String clientHostName) {
//		this.clientHostName = clientHostName;
//	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public void addStatus() {
		this.status++;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	

}
