package com.ah.ui.actions.monitor.enrolledclients.entity;

import java.util.TimeZone;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.bo.admin.HmDomain;
import com.ah.util.MgrUtil;
import com.ah.util.datetime.AhDateTimeUtil;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@Embeddable
@XStreamAlias("content")
public class EnrolledClientDetail {
	
	@Transient
	@XStreamAlias("DeviceId")
	private String deviceId;
	@Transient
	@XStreamAlias("ActiveStatus")
	private String activeStatus;
	@Transient
	@XStreamAlias("Udid")
	private String udid;
	@Transient
	@XStreamAlias("EnrollmentStatus")
	private String enrollmentStatus;
	@Transient
	@XStreamAlias("LastConnectedTime")
	private long lastConnectedTime;
	@Transient
	@XStreamAlias("OwnerType")
	private String ownerType;
	@Transient
	@XStreamAlias("PublicIp")
	private String publicIp;
	@Transient
	@XStreamAlias("OsType")
	private String osType;
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
	@XStreamAlias("Longitude")
	private String longitude;
	@Transient
	@XStreamAlias("Latitude")
	private String latitude;
	@Transient
	@XStreamAlias("Address")
	private String address;
	@Transient
	@XStreamAlias("DeviceName")
	private String deviceName;
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
	@XStreamAlias("BatteryLevel")
	private String batteryLevel;
	@Transient
	@XStreamAlias("CellularTechnology")
	private String cellularTechnology;
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
	private TimeZone tz;
	@Transient
	private HmDomain owner;

	public EnrolledClientDetail() {
		super();
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
	
	public boolean isActiveStatusOn() {
		return activeStatus.equals("1");
	}
	
	public String getActiveStatusString() {
		return activeStatus.equals("1")? MgrUtil.getUserMessage("monitor.enrolled.client.status.on")
				: MgrUtil.getUserMessage("monitor.enrolled.client.status.off");
	}

	public void setActiveStatus(String activeStatus) {
		this.activeStatus = activeStatus;
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getEnrollmentStatus() {
		return enrollmentStatus;
	}

	public void setEnrollmentStatus(String enrollmentStatus) {
		this.enrollmentStatus = enrollmentStatus;
	}

	public long getLastConnectedTime() {
		return lastConnectedTime;
	}

	public void setLastConnectedTime(long lastConnectedTime) {
		this.lastConnectedTime = lastConnectedTime;
	}
	
	public String getLastConnectedTimeString() {
		return AhDateTimeUtil.getSpecifyDateTime(lastConnectedTime, tz, owner);
	}
	
	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}

	public String getPublicIp() {
		return publicIp;
	}

	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}

	public String getOsType() {
		return osType;
	}
	
	public String getOsTypeString() {
		if (ACM_OSTYPE_IOS.equals(osType)) {
			return MgrUtil.getUserMessage("monitor.enrolled.client.os.version.os");
		} else if (ACM_OSTYPE_ANDROID.equals(osType)){
			return MgrUtil.getUserMessage("monitor.enrolled.client.os.version.andriod");
		} else if (ACM_OSTYPE_MACOSX.equals(osType)){
			return MgrUtil.getUserMessage("monitor.enrolled.client.os.version.osx");
		} else if (ACM_OSTYPE_CHROME.equals(osType)){
			return MgrUtil.getUserMessage("monitor.enrolled.client.os.version.chrome");
		} else {
			return "--";
		}
	}

	public void setOsType(String osType) {
		this.osType = osType;
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

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
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
	
	public String getDeviceCapacityString() {
		if (deviceCapacity==null) return "0";
		return deviceCapacity.substring(0,deviceCapacity.indexOf(".")+3);
	}

	public void setDeviceCapacity(String deviceCapacity) {
		this.deviceCapacity = deviceCapacity;
	}

	public String getAvailableDeviceCapacity() {
		return availableDeviceCapacity;
	}
	
	public String getAvailableDeviceCapacityString() {
		if (availableDeviceCapacity==null) return "0";
		return availableDeviceCapacity.substring(0,availableDeviceCapacity.indexOf(".")+3);
	}

	public void setAvailableDeviceCapacity(String availableDeviceCapacity) {
		this.availableDeviceCapacity = availableDeviceCapacity;
	}
	
	public String getStoragePercentage() {
		if (availableDeviceCapacity==null || deviceCapacity == null) {
			return "0%";
		}
		String per= String.valueOf(1-(Double.parseDouble(availableDeviceCapacity))/(Double.parseDouble(deviceCapacity)));
		return getPercentage(per);
	}

	public String getBatteryLevel() {
		return batteryLevel;
	}
	
	public String getBatteryLevelString() {
		return getPercentage(batteryLevel);
	}

	public void setBatteryLevel(String batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public String getCellularTechnology() {
		return cellularTechnology;
	}
	
	public String getCellularTechnologyString() {
		if ("1".equals(cellularTechnology)) {
			return MgrUtil.getUserMessage("monitor.enrolled.device.network.cellular.tech.info.gsm");
		} else if ("2".equals(cellularTechnology)) {
			return MgrUtil.getUserMessage("monitor.enrolled.device.network.cellular.tech.info.cdma");
		} else {
			return "None";
		}
	}

	public void setCellularTechnology(String cellularTechnology) {
		this.cellularTechnology = cellularTechnology;
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
		if (modemFirmwareVersion==null || modemFirmwareVersion.isEmpty()) {
			return "--";
		}
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
	
	public static final String ACM_OSTYPE_IOS="1";
	public static final String ACM_OSTYPE_ANDROID="2";
	public static final String ACM_OSTYPE_MACOSX="3";
	public static final String ACM_OSTYPE_CHROME="4";
	
	public boolean isOsTypeApple() {
		if (osType!=null && !osType.isEmpty()) {
			if (osType.equals(ACM_OSTYPE_IOS) || osType.equals(ACM_OSTYPE_MACOSX)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isOsTypeAndroid() {
		if (osType!=null && !osType.isEmpty()) {
			if (osType.equals(ACM_OSTYPE_ANDROID)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isOsTypeMacOsx() {
		if (osType!=null && !osType.isEmpty()) {
			if (osType.equals(ACM_OSTYPE_MACOSX)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isOsTypeChrome() {
		if (osType!=null && !osType.isEmpty()) {
			if (osType.equals(ACM_OSTYPE_CHROME)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean getNeedScanResultData() {
		if (osType!=null && !osType.isEmpty()) {
			if (osType.equals(ACM_OSTYPE_ANDROID) || osType.equals(ACM_OSTYPE_CHROME)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean getDisplayCellularRssi(){
		if (osType!=null && !osType.isEmpty()) {
			if (osType.equals(ACM_OSTYPE_ANDROID)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean getDisplayWifiRssi(){
		if (osType!=null && !osType.isEmpty()) {
			if (osType.equals(ACM_OSTYPE_ANDROID)  || osType.equals(ACM_OSTYPE_CHROME)) {
				return true;
			}
		}
		return false;
	}
	public boolean getDisplayWifiSsid(){
		return getDisplayWifiRssi();
	}
	public boolean getDisplayWifiBssid(){
		return getDisplayWifiRssi();
	}
	public boolean getDisplayWifiLinkSpeed(){
		return getDisplayWifiRssi();
	}
	
	private String getPercentage(String value){
		Double tempValue = Double.parseDouble(value)*100;
		if(String.valueOf(tempValue).indexOf(".") > 0 
				&& String.valueOf(tempValue).substring(String.valueOf(tempValue).indexOf(".") + 1).length() >= 2 ){
					return String.valueOf(tempValue).substring(0,String.valueOf(tempValue).indexOf(".") + 3) + "%";
			}
		if(String.valueOf(tempValue).indexOf(".") > 0
				&& String.valueOf(tempValue).substring(String.valueOf(tempValue).indexOf(".") + 1 ).length() < 2 ){
			return String.valueOf(tempValue) + "0%";
		}
		return value + "%";
	}

	public TimeZone getTz() {
		return tz;
	}

	public void setTz(TimeZone tz) {
		this.tz = tz;
	}

	public HmDomain getOwner() {
		return owner;
	}

	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

}
