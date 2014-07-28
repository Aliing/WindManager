package com.ah.ui.actions.monitor.enrolledclients.entity;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class GeneralInfoForUI {
	
	@Transient
	private String status = "----";
	
	@Transient
	private String lastConnect = "----";
	
	@Transient
	private String phoneNum = "----";
	
	@Transient
	private String deviceType = "----";
	
	@Transient
	private String udid = "----";
	
	@Transient
	private String deviceStorage = "----";
	
	@Transient
	private String batteryLevel ="----";
	
	@Transient
	private String batteryPercentage;
	
	@Transient
	private String storagePercentage;

	@Transient
	private String passwordPresent;
	
	@Transient
	private String dataProtection;
	
	public GeneralInfoForUI() {
		super();
	}

	public GeneralInfoForUI(String status, String lastConnect, String phoneNum,
			String deviceType, String udid, String deviceStorage,
			String batteryLevel) {
		super();
		this.status = status;
		this.lastConnect = lastConnect;
		this.phoneNum = phoneNum;
		this.deviceType = deviceType;
		this.udid = udid;
		this.deviceStorage = deviceStorage;
		this.batteryLevel = batteryLevel;
	}

	public String getPasswordPresent() {
		return passwordPresent;
	}

	public void setPasswordPresent(String passwordPresent) {
		this.passwordPresent = passwordPresent;
	}

	public String getDataProtection() {
		return dataProtection;
	}

	public void setDataProtection(String dataProtection) {
		this.dataProtection = dataProtection;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLastConnect() {
		return lastConnect;
	}

	public void setLastConnect(String lastConnect) {
		this.lastConnect = lastConnect;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getDeviceStorage() {
		return deviceStorage;
	}

	public void setDeviceStorage(String deviceStorage) {
		this.deviceStorage = deviceStorage;
	}

	public String getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(String batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public String getBatteryPercentage() {
		return batteryPercentage;
	}

	public void setBatteryPercentage(String batteryPercentage) {
		this.batteryPercentage = batteryPercentage;
	}

	public String getStoragePercentage() {
		return storagePercentage;
	}

	public void setStoragePercentage(String storagePercentage) {
		this.storagePercentage = storagePercentage;
	}
}
