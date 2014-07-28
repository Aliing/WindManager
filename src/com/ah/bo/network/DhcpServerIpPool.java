/**
 *@filename		DhcpServerIpPool.java
 *@version
 *@author		Fiona
 *@createtime	2008-10-9 AM 10:15:59
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.ah.bo.HmBoBase;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Embeddable
public class DhcpServerIpPool implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	
	@Column(length = HmBoBase.IP_ADDRESS_LENGTH)
	private String startIp;
	
	@Column(length = HmBoBase.IP_ADDRESS_LENGTH)
	private String endIp;

	public String getStartIp()
	{
		return startIp;
	}

	public void setStartIp(String startIp)
	{
		this.startIp = startIp;
	}

	public String getEndIp()
	{
		return endIp;
	}

	public void setEndIp(String endIp)
	{
		this.endIp = endIp;
	}

}
