package com.ah.ws.rest.models.mo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.ah.ws.rest.models.dto.Device;

@XmlRootElement
public class DeviceOperation {

	public DeviceOperation() {
	}

	private List<Device> devices  = new ArrayList<Device>();

	public static final short OPT_STATUS_FAILED 	= -1;
	public static final short OPT_STATUS_INIT 		= 0;
	public static final short OPT_STATUS_SUCCESS 	= 1;
	public static final short OPT_STATUS_ADD_EXISTED 	= 2;
	public static final short OPT_STATUS_DEL_NOTEXISTED = 3;
	public static final short OPT_STATUS_SYN_ADDED 		= 4;
	public static final short OPT_STATUS_CHANGED 		= 5;

	private short optStatus = OPT_STATUS_INIT;

	private String optMessage;

	public static final int OPT_RETRY_COUNT_MAX 		= 5;

	private int retryCount;

	@XmlElement
	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public short getOptStatus() {
		return optStatus;
	}

	public void setOptStatus(short optStatus) {
		this.optStatus = optStatus;
	}

	public String getOptMessage() {
		return optMessage;
	}

	public void setOptMessage(String optMessage) {
		this.optMessage = optMessage;
	}

	@XmlTransient
	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public void addAll(List<Device> device) {
		this.devices.addAll(device);
	}

	public void add(Device device) {
		this.devices.add(device);
	}

	@XmlTransient
	public List<String> getAllSerialNumbers() {
		List<String> sns = new ArrayList<String>();
		for (Device device : devices) {
			sns.add(device.getSerialNumber());
		}
		return sns;
	}
}
