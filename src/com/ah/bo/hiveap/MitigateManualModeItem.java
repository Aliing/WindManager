/**
 *@filename		MitigateManualModeItem.java
 *@version
 *@author		Fiona
 *@createtime	2011-4-1 PM 05:36:03
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.hiveap;

import java.io.Serializable;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class MitigateManualModeItem implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	
	private Long idpId;
	
	private String hostName;
	
	private String apMac;
	
	private String rssiStr;
	
	private String sameChannel;
	
	private int clientCount;
	
	private Long apId;

	public Long getApId()
	{
		return apId;
	}

	public void setApId(Long apId)
	{
		this.apId = apId;
	}

	public String getRssiStr()
	{
		return rssiStr;
	}

	public void setRssiStr(String rssiStr)
	{
		this.rssiStr = rssiStr;
	}

	public String getSameChannel()
	{
		return sameChannel;
	}

	public void setSameChannel(String sameChannel)
	{
		this.sameChannel = sameChannel;
	}

	public int getClientCount()
	{
		return clientCount;
	}

	public void setClientCount(int clientCount)
	{
		this.clientCount = clientCount;
	}

	public String getHostName()
	{
		return hostName;
	}

	public void setHostName(String hostName)
	{
		this.hostName = hostName;
	}

	public Long getIdpId()
	{
		return idpId;
	}

	public void setIdpId(Long idpId)
	{
		this.idpId = idpId;
	}

	public String getApMac()
	{
		return apMac;
	}

	public void setApMac(String apMac)
	{
		this.apMac = apMac;
	}

}
