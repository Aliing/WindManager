package com.ah.ws.rest.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.ah.ws.rest.models.mo.DeviceOperation;

@XmlRootElement
public class DeviceOptModel {
	private String vhmId;

	private List<DeviceOperation> deviceOperation = new ArrayList<DeviceOperation>();

	public String getVhmId() {
		return vhmId;
	}

	public void setVhmId(String vhmId) {
		this.vhmId = vhmId;
	}

	@XmlElement
	public List<DeviceOperation> getDeviceOperation() {
		return deviceOperation;
	}

	public DeviceOptModel() {
	}

	public void setDeviceOperation(List<DeviceOperation> deviceOperation) {
		this.deviceOperation = deviceOperation;
	}

	public void addAll(List<DeviceOperation> deviceOperation) {
		this.deviceOperation.addAll(deviceOperation);
	}

	public void add(DeviceOperation deviceOperation) {
		this.deviceOperation.add(deviceOperation);
	}
}
