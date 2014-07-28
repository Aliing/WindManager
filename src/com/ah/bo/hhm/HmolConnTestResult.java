package com.ah.bo.hhm;

import java.util.ArrayList;
import java.util.List;

public class HmolConnTestResult {
	

	private int devicesCount;  
	
	private String domainName;
	
	private String serviceAddress;
	
	private boolean vhmDoingTestFlag = false;

	public boolean isVhmDoingTestFlag() {
		return vhmDoingTestFlag;
	}

	public void setVhmDoingTestFlag(boolean vhmDoingTestFlag) {
		this.vhmDoingTestFlag = vhmDoingTestFlag;
	}

	public int getDevicesCount() {
		return devicesCount;
	}

	public void setDevicesCount(int devicesCount) {
		this.devicesCount = devicesCount;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getServiceAddress() {
		return serviceAddress;
	}

	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}

	private List<ConnectivityTestResultItem> noConnectDevicesList = new ArrayList<ConnectivityTestResultItem>();
	

	private List<ConnectivityTestResultItem> failConnectDeviceList = new ArrayList<ConnectivityTestResultItem>();
	
	public List<ConnectivityTestResultItem> getNoConnectDevicesList() {
		return noConnectDevicesList;
	}

	public void setNoConnectDevicesList(
			List<ConnectivityTestResultItem> noConnectDevicesList) {
		this.noConnectDevicesList = noConnectDevicesList;
	}

	public List<ConnectivityTestResultItem> getFailConnectDeviceList() {
		return failConnectDeviceList;
	}

	public void setFailConnectDeviceList(
			List<ConnectivityTestResultItem> failConnectDeviceList) {
		this.failConnectDeviceList = failConnectDeviceList;
	}

	public int getNoConnectDevicesListLength() {
		return getNoConnectDevicesList().size();
	}

	public int getFailConnectDeviceListLength() {
		return getFailConnectDeviceList().size();
	}
	
	public boolean isConnTestSuccess(){
		boolean blnResult = false;
		if (getNoConnectDevicesListLength() == 0 && getFailConnectDeviceListLength() == 0) {
			blnResult = true;
		} else {
			blnResult = false;
		}
		return blnResult;
	}
	
}
