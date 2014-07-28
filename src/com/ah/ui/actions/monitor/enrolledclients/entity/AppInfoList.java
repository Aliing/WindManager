package com.ah.ui.actions.monitor.enrolledclients.entity;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("content")
public class AppInfoList {
	
	@XStreamAlias("TotalPages")
	private String totalPage;
	
	@XStreamAlias("TotalNumber")
	private String totalNumber;
	
	public String getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(String totalNumber) {
		this.totalNumber = totalNumber;
	}

	@XStreamAlias("AppList")
	private List<AppInfoForUI> appList;

	public AppInfoList() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AppInfoList(String totalPage, List<AppInfoForUI> appList) {
		super();
		this.totalPage = totalPage;
		this.appList = appList;
	}

	public String getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(String totalPage) {
		this.totalPage = totalPage;
	}

	public List<AppInfoForUI> getAppList() {
		return appList;
	}

	public void setAppList(List<AppInfoForUI> appList) {
		this.appList = appList;
	}
	
}
