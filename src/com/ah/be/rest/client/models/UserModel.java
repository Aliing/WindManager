package com.ah.be.rest.client.models;

import java.util.List;

public class UserModel extends ResultModel{

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public boolean isDefaultFlg() {
		return defaultFlg;
	}
	public void setDefaultFlg(boolean defaultFlg) {
		this.defaultFlg = defaultFlg;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	//1:hmonline  3:cloudauth  4:Redirector(Staging)
	public short getServer() {
		return server;
	}
	public void setServer(short server) {
		this.server = server;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	// actually it is the user email address of login
	public String userName;
	public String customerId;
	public String partnerId;
	public String productId;
	public boolean defaultFlg;
	public String groupName;
	public short server;
	public String timeZone;
	
	//add for 6.1r3 and later HM
	public boolean accessRedirector;
	public String userNameInMyhive;
	
	// add for bug fix 28726 in Glasgow: get back VHM limit operator access
	private String userSelectedSsids;
	private String userSelectedLocalUserGroups;
	// if true, means limit operator access was set on Myhive, need use Myhive data override HMOL's data
	boolean configedLimitOperatorAccessOnMyhive;

	public boolean isConfigedLimitOperatorAccessOnMyhive() {
		return configedLimitOperatorAccessOnMyhive;
	}

	public void setConfigedLimitOperatorAccessOnMyhive(
			boolean configedLimitOperatorAccessOnMyhive) {
		this.configedLimitOperatorAccessOnMyhive = configedLimitOperatorAccessOnMyhive;
	}
	
	public String getUserSelectedSsids() {
		return userSelectedSsids;
	}
	public void setUserSelectedSsids(String userSelectedSsids) {
		this.userSelectedSsids = userSelectedSsids;
	}
	public String getUserSelectedLocalUserGroups() {
		return userSelectedLocalUserGroups;
	}
	public void setUserSelectedLocalUserGroups(String userSelectedLocalUserGroups) {
		this.userSelectedLocalUserGroups = userSelectedLocalUserGroups;
	}
	public boolean isAccessRedirector() {
		return accessRedirector;
	}
	public void setAccessRedirector(boolean accessRedirector) {
		this.accessRedirector = accessRedirector;
	}
	public String getUserNameInMyhive() {
		return userNameInMyhive;
	}
	public void setUserNameInMyhive(String userNameInMyhive) {
		this.userNameInMyhive = userNameInMyhive;
	}

}
