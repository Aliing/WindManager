package com.ah.ws.rest.models;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VhmLimitOperatorAccessSettings {

	private List<String> avaliableSsids;
	private List<String> avaliableLocalUserGroups;
	
	// settings for specified user management operator user
	private List<String> userSelectedSsids;
	private List<String> userSelectedLocalUserGroups;
	
	private short mode; // VHM mode, 1:express 2:enterprise
	
	public short getMode() {
		return mode;
	}
	public void setMode(short mode) {
		this.mode = mode;
	}
	
	public List<String> getAvaliableSsids() {
		return avaliableSsids;
	}
	public void setAvaliableSsids(List<String> avaliableSsids) {
		this.avaliableSsids = avaliableSsids;
	}
	public List<String> getAvaliableLocalUserGroups() {
		return avaliableLocalUserGroups;
	}
	public void setAvaliableLocalUserGroups(List<String> avaliableLocalUserGroups) {
		this.avaliableLocalUserGroups = avaliableLocalUserGroups;
	}
	public List<String> getUserSelectedSsids() {
		return userSelectedSsids;
	}
	public void setUserSelectedSsids(List<String> userSelectedSsids) {
		this.userSelectedSsids = userSelectedSsids;
	}
	public List<String> getUserSelectedLocalUserGroups() {
		return userSelectedLocalUserGroups;
	}
	public void setUserSelectedLocalUserGroups(
			List<String> userSelectedLocalUserGroups) {
		this.userSelectedLocalUserGroups = userSelectedLocalUserGroups;
	}
	
	
}
