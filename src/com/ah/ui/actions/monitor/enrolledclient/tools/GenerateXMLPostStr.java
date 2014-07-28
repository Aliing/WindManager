package com.ah.ui.actions.monitor.enrolledclient.tools;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("content")
public class GenerateXMLPostStr {
	@XStreamAlias("CustomerId")
	private String customId;
	
	@XStreamAlias("HmId")
	private String hmId;
	
	@XStreamAlias("PageNumber")
	private String pageNumber;
	
	@XStreamAlias("PageSize")
	private String pageSize;
	
	@XStreamAlias("Status")
	private String status;
	
	@XStreamAlias("OsType")
	private String ostype;
	
	@XStreamAlias("OwnerType")
	private String ownerType;
	
	@XStreamAlias("ActiveStatus")
	private String activeStatus;
	
	@XStreamAlias("SortList")
	private List<SortParamForClient> sort;
	
	@XStreamAlias("DeviceList")
	private List<DeviceForClient> deviceList;
	
	@XStreamAlias("MacAddress")
	private String macAddress;
	
	@XStreamAlias("Limit")
	private int limit;
	
	@XStreamAsAttribute
	private String version;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<SortParamForClient> getSort() {
		return sort;
	}

	public void setSort(List<SortParamForClient> sort) {
		this.sort = sort;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOstype() {
		return ostype;
	}

	public void setOstype(String ostype) {
		this.ostype = ostype;
	}

	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}

	public String getActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(String activeStatus) {
		this.activeStatus = activeStatus;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public String getHmId() {
		return hmId;
	}

	public void setHmId(String hmId) {
		this.hmId = hmId;
	}

	public List<DeviceForClient> getDeviceList() {
		return deviceList;
	}

	public void setDeviceList(List<DeviceForClient> deviceList) {
		this.deviceList = deviceList;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}
