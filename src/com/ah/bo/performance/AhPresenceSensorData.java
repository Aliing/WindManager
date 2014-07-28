package com.ah.bo.performance;

import com.ah.be.common.PresenceUtil;

public class AhPresenceSensorData {
	public String macAddress;
	public String hostName;
	public int objects;
	public int clientMacs;
	public long timeStamp;
	public double bandWidth;
	public String convertBandWidth;
	public boolean connectStatus;

	public AhPresenceSensorData() {

	}
	public AhPresenceSensorData(String macAddress, String hostName) {
		this.macAddress = macAddress;
		this.hostName = hostName;
	}
	public AhPresenceSensorData(String macAddress, long timeStamp) {
		this.macAddress = macAddress;
		this.timeStamp = timeStamp;
	}

	public int getObjects() {
		return objects;
	}

	public void setObjects(int objects) {
		this.objects = objects;
	}

	public int getClientMacs() {
		return clientMacs;
	}

	public void setClientMacs(int clientMacs) {
		this.clientMacs = clientMacs;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public double getBandWidth() {
		return bandWidth;
	}

	public void setBandWidth(double bandWidth) {
		this.bandWidth = bandWidth;
	}

	public String getConvertBandWidth() {
		return PresenceUtil.convertValue(bandWidth);
	}

	public boolean isConnectStatus() {
		return connectStatus;
	}

	public void setConnectStatus(boolean connectStatus) {
		this.connectStatus = connectStatus;
	}

}
