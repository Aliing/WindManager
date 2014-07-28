package com.ah.ws.rest.models.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Device {
	private String serialNumber;

	public static final short CONNECTION_STATUS_DISCONNECTED = 0;
	public static final short CONNECTION_STATUS_CONNECTED = 1;
	public static final short CONNECTION_STATUS_REDIRECTED = 2;

	private short connectionStatus;

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public short getConnectionStatus() {
		return connectionStatus;
	}

	public void setConnectionStatus(short connectionStatus) {
		this.connectionStatus = connectionStatus;
	}

	public Device() {
	}
}
