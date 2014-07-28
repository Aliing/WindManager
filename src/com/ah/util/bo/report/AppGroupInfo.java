package com.ah.util.bo.report;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.network.Application;

public class AppGroupInfo {
	
	private boolean selected;

	private List<Application> appList = new ArrayList<Application>();

	public void addApplication(Application app) {
		appList.add(app);
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	

	public List<Application> getAppList() {
		return appList;
	}

	public void setAppList(List<Application> appList) {
		this.appList = appList;
	}
	
	
	
	
}
