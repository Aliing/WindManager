package com.ah.ui.actions.monitor.enrolledclients.entity;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@Embeddable
@XStreamAlias("content")
public class EnrolledDeviceDetailInfo{
	
	@Transient
	@XStreamAlias("DeviceId")
	private String deviceId;
	@Transient
	@XStreamAlias("ActiveStatus")
	private String activeStatus;
	@Transient
	@XStreamAlias("Status")
	private int status;
	@Transient
	@XStreamAlias("LastConnectedTime")
	private String lastCon;
	@Transient
	@XStreamAlias("OwnerType")
	private String ownerShip;
	@Transient
	@XStreamAlias("PublicIp")
	private String publicIp;
	@Transient
	@XStreamAlias("OsType")
	private String platForm;
	@Transient
	@XStreamAlias("Longitude")
	private String longitude;
	@Transient
	@XStreamAlias("Latitude")
	private String latitude;
	@Transient
	@XStreamAlias("Address")
	private String address;
	@Transient
	@XStreamAlias("ProductName")
	private String productName;
	@Transient
	@XStreamAlias("SerialNumber")
	private String serialNumber;
	@Transient
	@XStreamAlias("DeviceCapacity")
	private String deviceCapacity;
	@Transient
	@XStreamAlias("AvailableDeviceCapacity")
	private String availableDeviceCapacity;
	@Transient
	@XStreamAlias("CellularTechnology")
	private String cellularTechnology;
	@Transient
	@XStreamAlias("BatteryLevel")
	private String batteryLevel;
	@Transient
	@XStreamAlias("IMEI")
	private String imei;
	@Transient
	@XStreamAlias("MEID")
	private String meid;
	@Transient
	@XStreamAlias("ModemFirmwareVersion")
	private String modemFirmwareVersion;
	@Transient
	@XStreamAlias("CurrentMNC")
	private String currentMNC;
	@Transient
	@XStreamAlias("Challenge")
	private String challenge;
	@Transient
	@XStreamAlias("UserID")
	private String userId;
	@Transient
	@XStreamAlias("UserLongName")
	private String userLongName;
	@Transient
	@XStreamAlias("UserShortName")
	private String userShortName;
	@Transient
	@XStreamAlias("DeviceName")
	private String deviceName;
	@Transient
	@XStreamAlias("OsVersion")
	private String osVersion;
	@Transient
	@XStreamAlias("BuildVersion")
	private String buildVersion;
	@Transient
	@XStreamAlias("ModelName")
	private String modelName;
	@Transient
	@XStreamAlias("Model")
	private String model;
	@Transient
	@XStreamAlias("Udid")
	private String udid;
	public EnrolledDeviceDetailInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public EnrolledDeviceDetailInfo(String deviceId, String activeStatus,
			int status, String lastCon, String ownerShip, String publicIp,
			String platForm, String longitude, String latitude, String address,
			String productName, String serialNumber, String deviceCapacity,
			String availableDeviceCapacity, String cellularTechnology,
			String batteryLevel, String imei, String meid,
			String modemFirmwareVersion, String currentMNC, String challenge,
			String userId, String userLongName, String userShortName,
			String deviceName, String osVersion, String buildVersion,
			String modelName, String model, String udid) {
		super();
		this.deviceId = deviceId;
		this.activeStatus = activeStatus;
		this.status = status;
		this.lastCon = lastCon;
		this.ownerShip = ownerShip;
		this.publicIp = publicIp;
		this.platForm = platForm;
		this.longitude = longitude;
		this.latitude = latitude;
		this.address = address;
		this.productName = productName;
		this.serialNumber = serialNumber;
		this.deviceCapacity = deviceCapacity;
		this.availableDeviceCapacity = availableDeviceCapacity;
		this.cellularTechnology = cellularTechnology;
		this.batteryLevel = batteryLevel;
		this.imei = imei;
		this.meid = meid;
		this.modemFirmwareVersion = modemFirmwareVersion;
		this.currentMNC = currentMNC;
		this.challenge = challenge;
		this.userId = userId;
		this.userLongName = userLongName;
		this.userShortName = userShortName;
		this.deviceName = deviceName;
		this.osVersion = osVersion;
		this.buildVersion = buildVersion;
		this.modelName = modelName;
		this.model = model;
		this.udid = udid;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(String activeStatus) {
		this.activeStatus = activeStatus;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getLastCon() {
		return lastCon;
	}
	public void setLastCon(String lastCon) {
		this.lastCon = lastCon;
	}
	public String getOwnerShip() {
		return ownerShip;
	}
	public void setOwnerShip(String ownerShip) {
		this.ownerShip = ownerShip;
	}
	public String getPublicIp() {
		return publicIp;
	}
	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}
	public String getPlatForm() {
		return platForm;
	}
	public void setPlatForm(String platForm) {
		this.platForm = platForm;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getDeviceCapacity() {
		return deviceCapacity;
	}
	public void setDeviceCapacity(String deviceCapacity) {
		this.deviceCapacity = deviceCapacity;
	}
	public String getAvailableDeviceCapacity() {
		return availableDeviceCapacity;
	}
	public void setAvailableDeviceCapacity(String availableDeviceCapacity) {
		this.availableDeviceCapacity = availableDeviceCapacity;
	}
	public String getCellularTechnology() {
		return cellularTechnology;
	}
	public void setCellularTechnology(String cellularTechnology) {
		this.cellularTechnology = cellularTechnology;
	}
	public String getBatteryLevel() {
		return batteryLevel;
	}
	public void setBatteryLevel(String batteryLevel) {
		this.batteryLevel = batteryLevel;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getMeid() {
		return meid;
	}
	public void setMeid(String meid) {
		this.meid = meid;
	}
	public String getModemFirmwareVersion() {
		return modemFirmwareVersion;
	}
	public void setModemFirmwareVersion(String modemFirmwareVersion) {
		this.modemFirmwareVersion = modemFirmwareVersion;
	}
	public String getCurrentMNC() {
		return currentMNC;
	}
	public void setCurrentMNC(String currentMNC) {
		this.currentMNC = currentMNC;
	}
	public String getChallenge() {
		return challenge;
	}
	public void setChallenge(String challenge) {
		this.challenge = challenge;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserLongName() {
		return userLongName;
	}
	public void setUserLongName(String userLongName) {
		this.userLongName = userLongName;
	}
	public String getUserShortName() {
		return userShortName;
	}
	public void setUserShortName(String userShortName) {
		this.userShortName = userShortName;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	public String getBuildVersion() {
		return buildVersion;
	}
	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getUdid() {
		return udid;
	}
	public void setUdid(String udid) {
		this.udid = udid;
	}
	
	
}
