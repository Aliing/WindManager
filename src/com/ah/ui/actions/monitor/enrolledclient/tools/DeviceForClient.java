package com.ah.ui.actions.monitor.enrolledclient.tools;

import java.io.Serializable;

import javax.persistence.Embeddable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@Embeddable
@XStreamAlias("Device")
public class DeviceForClient implements Serializable {
	private static final long serialVersionUID = 6666258939391045397L;
	
	@XStreamAlias("macAddress")
	@XStreamAsAttribute
	private String macAddress;
	
	@XStreamAlias("enrollmentStatus")
	@XStreamAsAttribute
	private String enrollmentStatus;
	
	@XStreamAlias("deviceId")
	@XStreamAsAttribute
	private String deviceId;

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getEnrollmentStatus() {
		return enrollmentStatus;
	}

	public void setEnrollmentStatus(String enrollmentStatus) {
		this.enrollmentStatus = enrollmentStatus;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public boolean isEnrolled() {
		if (enrollmentStatus!=null && enrollmentStatus.toLowerCase().startsWith("t")) {
			return true;
		}
		return false;
	}
	
	
}
