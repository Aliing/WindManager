package com.ah.ws.rest.models;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * user info model for get/update/delete user from IDM's ADMIN managemetn UI
 *
 * @author root
 *
 */
@XmlRootElement(name="users")
public class CustomerUserInfo {
	public CustomerUserInfo() {
	}

	private String userEmail;

	private String userName;

	private String description;

	private String groupName;

	private int i18n;

	private int gmCss;

	private String timezone;

	private short dateFormat;

	private short timeFormat;

	private String ccEmail;

	private boolean defaultFlag;

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getCcEmail() {
		return ccEmail;
	}

	public void setCcEmail(String ccEmail) {
		this.ccEmail = ccEmail;
	}

	public int getI18n() {
		return i18n;
	}

	public void setI18n(int i18n) {
		this.i18n = i18n;
	}

	public int getGmCss() {
		return gmCss;
	}

	public void setGmCss(int gmCss) {
		this.gmCss = gmCss;
	}

	public short getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(short dateFormat) {
		this.dateFormat = dateFormat;
	}

	public short getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(short timeFormat) {
		this.timeFormat = timeFormat;
	}

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	@Override
	public String toString() {
		return "CustomerUserInfo [userEmail=" + userEmail + ", userName="
				+ userName + ", description=" + description + ", groupName="
				+ groupName + ", i18n=" + i18n + ", gmCss=" + gmCss
				+ ", timezone=" + timezone + ", dateFormat=" + dateFormat
				+ ", timeFormat=" + timeFormat + ", ccEmail=" + ccEmail + "]";
	}

}
