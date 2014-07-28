package com.ah.ui.actions.monitor.enrolledclients.entity;

import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@Embeddable
@XStreamAlias("content")
public class DeviceList {
	
	@Transient
	@XStreamAlias("TotalPages")
	private String totalPages  = "0";
	
	@Transient
	@XStreamAlias("TotalNumber")
	private String totalNumber = "0";
	
	@XStreamAlias("DeviceList")
	private List<EnrolledClientInfoUI> deviceList;

	public DeviceList() {
		super();
	}

	public String getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(String totalPages) {
		this.totalPages = totalPages;
	}

	public List<EnrolledClientInfoUI> getDeviceList() {
		return deviceList;
	}

	public void setDeviceList(List<EnrolledClientInfoUI> deviceList) {
		this.deviceList = deviceList;
	}

	public String getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(String totalNumber) {
		this.totalNumber = totalNumber;
	}
	
}
