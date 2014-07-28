/**
 *@filename		HiveApInfoForLs.java
 *@version
 *@author		Fiona
 *@createtime	2010-8-13 AM 10:33:12
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.hiveap;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class HiveApInfoForLs
{
	private String serialNumber;

	private String macAddress;
	
	private long firstConnectTime;
	
	private long lastConnectTime;
	
	private long totalConnectTime;
	
	private long totalConnectTimes;
	
	private String productName;
	
	private String softVer;
	
	private String timeZone;
	
	private String vhmName;
	
	// system id or vhm id
	private String hmId;

	public String getSerialNumber()
	{
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber)
	{
		this.serialNumber = serialNumber;
	}

	public String getMacAddress()
	{
		return macAddress;
	}

	public void setMacAddress(String macAddress)
	{
		this.macAddress = macAddress;
	}

	public long getFirstConnectTime()
	{
		return firstConnectTime;
	}

	public void setFirstConnectTime(long firstConnectTime)
	{
		this.firstConnectTime = firstConnectTime;
	}

	public long getLastConnectTime()
	{
		return lastConnectTime;
	}

	public void setLastConnectTime(long lastConnectTime)
	{
		this.lastConnectTime = lastConnectTime;
	}

	public long getTotalConnectTime()
	{
		return totalConnectTime;
	}

	public void setTotalConnectTime(long totalConnectTime)
	{
		this.totalConnectTime = totalConnectTime;
	}

	public String getTimeZone()
	{
		return timeZone;
	}

	public void setTimeZone(String timeZone)
	{
		this.timeZone = timeZone;
	}

	public String getVhmName()
	{
		return vhmName;
	}

	public void setVhmName(String vhmName)
	{
		this.vhmName = vhmName;
	}

	public String getHmId()
	{
		return hmId;
	}

	public void setHmId(String hmId)
	{
		this.hmId = hmId;
	}

	public long getTotalConnectTimes()
	{
		return totalConnectTimes;
	}

	public void setTotalConnectTimes(long totalConnectTimes)
	{
		this.totalConnectTimes = totalConnectTimes;
	}

	public String getProductName()
	{
		return productName;
	}

	public void setProductName(String productName)
	{
		this.productName = productName;
	}

	public String getSoftVer()
	{
		return softVer;
	}

	public void setSoftVer(String softVer)
	{
		this.softVer = softVer;
	}
}
