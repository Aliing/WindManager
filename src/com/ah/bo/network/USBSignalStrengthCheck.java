package com.ah.bo.network;

import javax.persistence.Embeddable;

@Embeddable
public class USBSignalStrengthCheck {
	private String type;
	
	private String serialPort;
	
	private String checkCmd;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSerialPort() {
		return serialPort;
	}

	public void setSerialPort(String serialPort) {
		this.serialPort = serialPort;
	}

	public String getCheckCmd() {
		return checkCmd;
	}

	public void setCheckCmd(String checkCmd) {
		this.checkCmd = checkCmd;
	}
	
}
