package com.ah.ui.actions.monitor.enrolledclients.entity;


import java.util.List;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("content")
public class EnrolledClientActivityLogList
{
	@XStreamAlias("LogList")
	private List<EnrolledClientActivityLogItem>	logList;

	public List<EnrolledClientActivityLogItem> getLogList() {
		return logList;
	}

	public void setLogList(List<EnrolledClientActivityLogItem> logList) {
		this.logList = logList;
	}

}
