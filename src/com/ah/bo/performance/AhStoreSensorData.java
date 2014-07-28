package com.ah.bo.performance;

import java.util.List;

import com.ah.be.common.PresenceUtil;

public class AhStoreSensorData {
	public String storeName;
	public int objectCount;
	public int clientMacCount;
	public int deviceCount;
	public double bandWidthCount;
	public List<AhPresenceSensorData> sensorDataList;

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public int getObjectCount() {
		return objectCount;
	}

	public void setObjectCount(int objectCount) {
		this.objectCount = objectCount;
	}

	public int getClientMacCount() {
		return clientMacCount;
	}

	public String getBandWidthCount() {
		return PresenceUtil.convertValue(bandWidthCount);
	}

	public void setBandWidthCount(double bandWidthCount) {
		this.bandWidthCount = bandWidthCount;
	}

	public void setClientMacCount(int clientMacCount) {
		this.clientMacCount = clientMacCount;
	}

	public List<AhPresenceSensorData> getSensorDataList() {
		return sensorDataList;
	}

	public void setSensorDataList(List<AhPresenceSensorData> sensorDataList) {
		this.sensorDataList = sensorDataList;
	}

	public int getDeviceCount() {
		return deviceCount;
	}

	public void setDeviceCount(int deviceCount) {
		this.deviceCount = deviceCount;
	}

}
