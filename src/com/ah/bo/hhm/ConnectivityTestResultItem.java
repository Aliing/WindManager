/**
 *@filename		ConnectivityTestResultItem.java
 *@version
 *@author		Fiona
 *@createtime	2011-2-25 AM 10:32:46
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.hhm;

/**
 * @author wpliang
 * @version		V1.0.0.0 
 */
public class ConnectivityTestResultItem {
	
	private String hostName;
	
	private String ipAddress;
	
	private String hwModel;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getHwModel() {
		return hwModel;
	}

	public void setHwModel(String hwModel) {
		this.hwModel = hwModel;
	}

}
