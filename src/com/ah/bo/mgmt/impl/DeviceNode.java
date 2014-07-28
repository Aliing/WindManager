package com.ah.bo.mgmt.impl;

public class DeviceNode {

	public DeviceNode(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getClientCount() {
		return clientCount;
	}

	public void setClientCount(int clientCount) {
		this.clientCount = clientCount;
	}

	public short getManageStatus() {
		return manageStatus;
	}

	public void setManageStatus(short manageStatus) {
		this.manageStatus = manageStatus;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public final String id, name;

	private int clientCount;
	private short manageStatus;
	private boolean connected;
}
