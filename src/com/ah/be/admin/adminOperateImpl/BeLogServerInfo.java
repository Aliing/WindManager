package com.ah.be.admin.adminOperateImpl;

import java.util.ArrayList;
import java.util.List;

public class BeLogServerInfo {

	private boolean bLogServer = false;

	private boolean bFullNet = true;

	private List<String> lSubNet = new ArrayList<String>();

	public BeLogServerInfo() {
	}

	public boolean getIsLogServer() {
		return bLogServer;
	}

	public void setIsLogServer(boolean bFlag) {
		bLogServer = bFlag;
	}

	public boolean getIsFullNet() {
		return bFullNet;
	}

	public void setIsFullNet(boolean bFlag) {
		bFullNet = bFlag;
	}

	public List<String> getSubNet() {
		return lSubNet;
	}

	public void setSubNet(List<String> oList) {
		lSubNet = oList;
	}

}