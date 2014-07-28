package com.ah.be.os;

import java.io.Serializable;

/**
 *@filename		NetConfigureDTO.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-10-28 09:51:09
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
@SuppressWarnings("serial")
public class NetConfigureDTO implements Serializable {

	private String	ipAddress_eth0	= null;
	private String	netmask_eth0	= null;

	private boolean	enabled_eth1;

	private String	ipAddress_eth1	= null;
	private String	netmask_eth1	= null;

	private String	gateway			= null;
	private String	primaryDns		= null;
	private String	secondDns		= null;
	private String	tertiaryDns		= null;
	private String	hostName		= null;
	private String	domainName		= null;

	private int		mgtSpeedDuplex;
	private int		lanSpeedDuplex;

	// route feature is separated, so let's ignore this field
	// private Vector<Vector<String>> vctRoute = null;

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getIpAddress_eth1() {
		return ipAddress_eth1;
	}

	public void setIpAddress_eth1(String ipAddress_LAN) {
		this.ipAddress_eth1 = ipAddress_LAN;
	}

	public String getIpAddress_eth0() {
		return ipAddress_eth0;
	}

	public void setIpAddress_eth0(String ipAddress_MGT) {
		this.ipAddress_eth0 = ipAddress_MGT;
	}

	public String getNetmask_eth1() {
		return netmask_eth1;
	}

	public void setNetmask_eth1(String netmask_LAN) {
		this.netmask_eth1 = netmask_LAN;
	}

	public String getNetmask_eth0() {
		return netmask_eth0;
	}

	public void setNetmask_eth0(String netmask_MGT) {
		this.netmask_eth0 = netmask_MGT;
	}

	public String getPrimaryDns() {
		return primaryDns;
	}

	public void setPrimaryDns(String primaryDns) {
		this.primaryDns = primaryDns;
	}

	public String getSecondDns() {
		return secondDns;
	}

	public void setSecondDns(String secondDns) {
		this.secondDns = secondDns;
	}

	public String getTertiaryDns() {
		return tertiaryDns;
	}

	public void setTertiaryDns(String tertiaryDns) {
		this.tertiaryDns = tertiaryDns;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public boolean isEnabled_eth1() {
		return enabled_eth1;
	}

	public void setEnabled_eth1(boolean enabled_eth1) {
		this.enabled_eth1 = enabled_eth1;
	}

	public int getLanSpeedDuplex() {
		return lanSpeedDuplex;
	}

	public void setLanSpeedDuplex(int lanSpeedDuplex) {
		this.lanSpeedDuplex = lanSpeedDuplex;
	}

	public int getMgtSpeedDuplex() {
		return mgtSpeedDuplex;
	}

	public void setMgtSpeedDuplex(int mgtSpeedDuplex) {
		this.mgtSpeedDuplex = mgtSpeedDuplex;
	}
}
