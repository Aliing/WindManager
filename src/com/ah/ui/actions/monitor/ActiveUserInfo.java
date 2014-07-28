package com.ah.ui.actions.monitor;

public class ActiveUserInfo {
	
	String userIpAddress="";
	String userName="";
	String userSessionUndoTime="";
	String userSessionTotalTime="";
	String sessionId="";
	
	public String getUserIpAddress() {
		return userIpAddress;
	}
	public void setUserIpAddress(String userIpAddress) {
		this.userIpAddress = userIpAddress;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserSessionUndoTime() {
		return userSessionUndoTime;
	}
	public void setUserSessionUndoTime(String userSessionUndoTime) {
		this.userSessionUndoTime = userSessionUndoTime;
	}
	public String getUserSessionTotalTime() {
		return userSessionTotalTime;
	}
	public void setUserSessionTotalTime(String userSessionTotalTime) {
		this.userSessionTotalTime = userSessionTotalTime;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
