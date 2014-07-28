package com.ah.be.communication.mo;

import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.QueryUtil;

public class UserInfo {

	private String vhmName = "home";

	private String username;

	private String password;

	private String Fullname;

	private String emailAddress;

	private short groupAttribute; // reserve

	// for VAD user
	private int maxAPNum = 0;

	private short defaultApp;

	private boolean defaultFlag = false;
	
	private String timeZone;
	
	private String	clearPassword;
	
	private boolean accessMyHive = false;

	public boolean isAccessMyHive()
	{
		return accessMyHive;
	}

	public void setAccessMyHive(boolean accessMyHive)
	{
		this.accessMyHive = accessMyHive;
	}

	public static UserInfo getUserInfo(HmUser user) {
		UserInfo userInfo = new UserInfo();
		userInfo.setUsername(user.getUserName());
		userInfo.setFullname(user.getUserFullName());
		userInfo.setPassword(user.getPassword());
		userInfo.setEmailAddress(user.getEmailAddress());
		userInfo.setGroupAttribute((short) user.getUserGroup().getGroupAttribute());
		userInfo.setMaxAPNum(user.getMaxAPNum());
		userInfo.setDefaultApp(user.getDefapplication());
		userInfo.setVhmName(user.getOwner().getDomainName());
		userInfo.setDefaultFlag(user.getDefaultFlag());
		userInfo.setTimeZone(user.getTimeZone());
		userInfo.setAccessMyHive(user.isAccessMyhive());
		return userInfo;
	}

	public HmUser toHmUser() {
		HmUser user = new HmUser();
		user.setUserName(getUsername());
		user.setUserFullName(getFullname());
		user.setPassword(getPassword());
		user.setEmailAddress(getEmailAddress());

		HmUserGroup group = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupAttribute",
				groupAttribute);
		user.setUserGroup(group);

		user.setMaxAPNum(getMaxAPNum());
		user.setDefapplication(getDefaultApp());
		user.setDefaultFlag(isDefaultFlag());
		user.setTimeZone(getTimeZone());
		user.setAccessMyhive(isAccessMyHive());

		return user;
	}
	
	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public short getDefaultApp() {
		return defaultApp;
	}

	public void setDefaultApp(short defaultApp) {
		this.defaultApp = defaultApp;
	}

	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		this.vhmName = vhmName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullname() {
		return Fullname;
	}

	public void setFullname(String fullname) {
		Fullname = fullname;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public short getGroupAttribute() {
		return groupAttribute;
	}

	public void setGroupAttribute(short groupAttribute) {
		this.groupAttribute = groupAttribute;
	}

	public int getMaxAPNum() {
		return maxAPNum;
	}

	public void setMaxAPNum(int maxAPNum) {
		this.maxAPNum = maxAPNum;
	}

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public String getClearPassword() {
		return clearPassword;
	}

	public void setClearPassword(String clearPassword) {
		this.clearPassword = clearPassword;
	}
	
	public boolean isVadAdmin() {
		return groupAttribute == HmUserGroup.VAD_ATTRIBUTE;
	}

}
