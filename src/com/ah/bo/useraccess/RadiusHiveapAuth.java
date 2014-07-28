/**
 *@filename		RadiusHiveapAuth.java
 *@version
 *@author		Fiona
 *@createtime	2007-10-9 PM 03:28:12
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.useraccess;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.ah.bo.network.IpAddress;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Embeddable
@SuppressWarnings("serial")
public class RadiusHiveapAuth implements Serializable
{	
	@Column(length = 31)
	private String		sharedKey	= "";

	@Column(length = 64)
	private String		description	= "";

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "IP_ADDRESS_ID", nullable = true)
	private IpAddress	ipAddress;
	
	@Transient
	private boolean enableTls = false;

	public String getSharedKey()
	{
		return sharedKey;
	}

	public void setSharedKey(String sharedKey)
	{
		this.sharedKey = sharedKey;
	}

	public IpAddress getIpAddress()
	{
		return ipAddress;
	}

	public void setIpAddress(IpAddress ipAddress)
	{
		this.ipAddress = ipAddress;
	}
	
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	@Transient
	public String restoreId;

	public String getRestoreId()
	{
		return restoreId;
	}
	public void setRestoreId(String restoreId)
	{
		this.restoreId = restoreId;
	}
	
	@Transient
	public String getStringSharedKey()
	{
		StringBuffer strBuf = new StringBuffer();
		for(int i = 0; i < sharedKey.length(); i ++)
		{
			strBuf.append("*");
		}
		return strBuf.toString();
	}
	
	@Transient
	public boolean isEnableTls() {
		return enableTls;
	}

	@Transient
	public void setEnableTls(boolean enableTls) {
		this.enableTls = enableTls;
	}
}
