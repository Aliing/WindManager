package com.ah.ui.actions.monitor.enrolledclients.entity;

import javax.persistence.Embeddable;

import com.thoughtworks.xstream.annotations.XStreamAlias;


@Embeddable
@XStreamAlias("content")
public class DeviceSecurityInfo
{
	@XStreamAlias("id")
	private int Id;
	
	@XStreamAlias("deviceId")
	private String deviceId;
	
	@XStreamAlias("customerId")
	private String customerId;
	
	@XStreamAlias("udid")
	private String udid;
	
	@XStreamAlias("HardwareEncryptionCaps")
	private String		hardwareEncryptionCaps;
	
	@XStreamAlias("PasscodePresent")
	private String	passcodePresent;
	
	@XStreamAlias("PasscodeCompliant")
	private String	passcodeCompliant;
	
	@XStreamAlias("PasscodeCompliantWithProfiles")
	private String	passcodeCompliantWithProfiles;

	public boolean getDataProtection() {
		return hardwareEncryptionCaps.equals("3") && passcodePresent.equals("true");
	}

	public DeviceSecurityInfo()
	{
		
	}

	public int getId() {
		return Id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getUdid() {
		return udid;
	}

	public void setId(int id) {
		Id = id;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getHardwareEncryptionCaps()
	{
		return hardwareEncryptionCaps;
	}

	public void setHardwareEncryptionCaps(String hardwareEncryptionCaps)
	{
		this.hardwareEncryptionCaps = hardwareEncryptionCaps;
	}

	public String getPasscodePresent()
	{
		return passcodePresent;
	}

	public void setPasscodePresent(String passcodePresent)
	{
		this.passcodePresent = passcodePresent;
	}

	public String getPasscodeCompliant()
	{
		return passcodeCompliant;
	}

	public void setPasscodeCompliant(String passcodeCompliant)
	{
		this.passcodeCompliant = passcodeCompliant;
	}

	public String getPasscodeCompliantWithProfiles()
	{
		return passcodeCompliantWithProfiles;
	}

	public void setPasscodeCompliantWithProfiles(String passcodeCompliantWithProfiles)
	{
		this.passcodeCompliantWithProfiles = passcodeCompliantWithProfiles;
	}
}
