package com.ah.ui.actions.monitor.enrolledclients.entity;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
@Embeddable
@XStreamAlias("Log")
public class EnrolledClientActivityLogItem
{	
	@Transient
	@XStreamAlias("Status")
	public String status;
	@Transient
	@XStreamAlias("ActionName")
	public String actionName;
	@Transient
	@XStreamAlias("DeviceName")
	public String deviceName;
	@Transient
	@XStreamAlias("StartTime")
	public long startTime;
	@Transient
	@XStreamAlias("EndTime")
	public long endTime;
	@Transient
	@XStreamAlias("FailedReason")
	public String failedReason;
	
	public EnrolledClientActivityLogItem()
	{
		super();
	}

	public String getStatus() {
		return status;
	}

	public String getStatusString() {
		if (status.equals("0")) {
			return "<span class=\"css_log_status_notsend\"></span>&nbsp;Not Sent";
		} else if (status.equals("1")) {
			return "<span class=\"css_log_status_ok\"></span>&nbsp;Acknowledged";
		} else if (status.equals("2")) {
			return "<span class=\"css_log_status_error\"></span>&nbsp;Error";
		} else if (status.equals("3")) {
			return "<span class=\"css_log_status_formaterror\"></span>&nbsp;Command Format Error";
		} else if (status.equals("4")) {
			return "<span class=\"css_log_status_warn\"></span>&nbsp;Idle";
		} else if (status.equals("5")) {
			return "<span class=\"css_log_status_warn\"></span>&nbsp;Not Now";
		} else if (status.equals("6")) {
			return "<span class=\"css_log_status_warn\"></span>&nbsp;Not Used";
		} else if (status.equals("7")) {
			return "<span class=\"css_log_status_warn\"></span>&nbsp;Pending";
		} else {
			return "";
		}
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getFailedReason() {
		return failedReason;
	}

	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}

}
