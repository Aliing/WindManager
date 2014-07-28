/**
 *@filename		AhWtpAttributes.java
 *@version
 *@author		Francis
 *@createtime	2007-10-10 02:43:13 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Inc.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.capwap;

// java import
import java.io.Serializable;

/**
 * @author Francis
 * @version V1.0.0.0
 */
class AhWtpAttributes implements Serializable {

	private static final long	serialVersionUID	= 1L;

	protected int						capwapVer;
	protected int						regionCode;
	protected int						countryCode;
	protected int						ipType;
	protected int						apType;
	protected String					serialNum;
	protected String					hwVer;
	protected String					swVer;
	protected String					location;
	protected String					ip;
	protected String					hostname;
	protected String					mac;
	protected String					netmask;
	protected String					gateway;

	public int getCapwapVer() {
		return capwapVer;
	}

	public void setCapwapVer(int capwapVer) {
		this.capwapVer = capwapVer;
	}

	public int getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(int regionCode) {
		this.regionCode = regionCode;
	}

	public int getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(int countryCode) {
		this.countryCode = countryCode;
	}

	public int getIpType() {
		return ipType;
	}

	public void setIpType(int ipType) {
		this.ipType = ipType;
	}

	public int getApType() {
		return apType;
	}

	public void setApType(int apType) {
		this.apType = apType;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public String getHwVer() {
		return hwVer;
	}

	public void setHwVer(String hwVer) {
		this.hwVer = hwVer;
	}

	public String getSwVer() {
		return swVer;
	}

	public void setSwVer(String swVer) {
		this.swVer = swVer;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

}