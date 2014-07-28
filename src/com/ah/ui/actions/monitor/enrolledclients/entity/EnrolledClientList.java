package com.ah.ui.actions.monitor.enrolledclients.entity;

import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.ui.actions.monitor.enrolledclient.tools.DeviceForClient;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@Embeddable
@XStreamAlias("content")
public class EnrolledClientList {
	
	@Transient
	@XStreamAlias("DeviceUrlSuffix")
	private String deviceUrlSuffix = "";
	
	@XStreamAlias("DeviceList")
	private List<DeviceForClient> deviceList;

	public EnrolledClientList() {
		super();
	}

	public String getDeviceUrlSuffix() {
		return deviceUrlSuffix;
	}


	public void setDeviceUrlSuffix(String deviceUrlSuffix) {
		this.deviceUrlSuffix = deviceUrlSuffix;
	}


	public List<DeviceForClient> getDeviceList() {
		return deviceList;
	}


	public void setDeviceList(List<DeviceForClient> deviceList) {
		this.deviceList = deviceList;
	}


	
}
