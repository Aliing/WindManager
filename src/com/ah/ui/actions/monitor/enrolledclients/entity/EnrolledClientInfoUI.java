package com.ah.ui.actions.monitor.enrolledclients.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@SuppressWarnings("serial")
@Embeddable
@XStreamAlias("Device")
public class EnrolledClientInfoUI implements HmBo,Serializable{

	private static final long serialVersionUID = -5575087165366925184L;
	
	public static final String ENROLLED_CLIENT_STATUS_ONE = "Active";
	
	public static final String ENROLLED_CLIENT_STATUS_TWO = "Inactive";
	
	public static final String ENROLLED_CLIENT_MANAGED_NO = "No";
	
	public static final String ENROLLED_CLIENT_MANAGED_YES = "Yes";

	@Transient
	@XStreamAlias("DeviceId")
	private String deviceId;
	
	@Transient
	@XStreamAlias("ActiveStatus")
	private String status;
	
	@Transient
	@XStreamAlias("Status")
	private String managed;
	
	@Transient
	@XStreamAlias("DeviceName")
	private String name;
	
	@Transient
	@XStreamAlias("LastConnectedTime")
	private String lastCon;
	
	@Transient
	@XStreamAlias("OsType")
	private String platForm;
	
	@Transient
	@XStreamAlias("OwnerType")
	private String ownerShip;
	
	@Transient
	@XStreamAlias("OsVersion")
	private String osVersion;
	
	@Transient
	@XStreamAlias("SystemModel")
	private String sysMode;
	
	@Transient
	@XStreamAlias("ModelName")
	private String modeName;
	
	@Transient
	@XStreamAlias("DataProtectEnabled")
	private String dataProtected;
	
	@Transient
	@XStreamAlias("PasscodePresent")
	private String passcodePresented;
	
	@Transient
	@XStreamAlias("EnrollUserName")
	private String enrollUserName;
	
	@Transient
	@XStreamAlias("EnrollUserGroup")
	private String enrollUserGroup;
	
	@Transient
	@XStreamAlias("WiFiMAC")
	private String wifiMac;
	
	@Transient
	private Long id;
	
	public String getWifiMac() {
		return wifiMac;
	}

	public void setWifiMac(String wifiMcc) {
		this.wifiMac = wifiMcc;
	}

	public String getEnrollUserName() {
		return enrollUserName;
	}

	public void setEnrollUserName(String enrollUserName) {
		this.enrollUserName = enrollUserName;
	}

	public String getEnrollUserGroup() {
		return enrollUserGroup;
	}

	public void setEnrollUserGroup(String enrollUserGroup) {
		this.enrollUserGroup = enrollUserGroup;
	}

	public String getModeName() {
		return modeName;
	}

	public void setModeName(String modeName) {
		this.modeName = modeName;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getManaged() {
		return managed;
	}

	public void setManaged(String managed) {
		this.managed = managed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastCon() {
		return lastCon;
	}

	public void setLastCon(String lastCon) {
		this.lastCon = lastCon;
	}

	public String getPlatForm() {
		return platForm;
	}

	public void setPlatForm(String platForm) {
		this.platForm = platForm;
	}

	public String getOwnerShip() {
		return ownerShip;
	}

	public void setOwnerShip(String ownerShip) {
		this.ownerShip = ownerShip;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getSysMode() {
		return sysMode;
	}

	public void setSysMode(String sysMode) {
		this.sysMode = sysMode;
	}

	public String getDataProtected() {
		return dataProtected;
	}

	public void setDataProtected(String dataProtected) {
		this.dataProtected = dataProtected;
	}

	public String getPasscodePresented() {
		return passcodePresented;
	}

	public void setPasscodePresented(String passcodePresented) {
		this.passcodePresented = passcodePresented;
	}

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
		
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVersion(Timestamp version) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub
		
	}
	
}
