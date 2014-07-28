/**
 *@filename		AlgConfigurationInfo.java
 *@version
 *@author		Fiona
 *@createtime	2007-12-6 PM 06:57:00
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *remove dns alg 2009-02-05
 *
 *add dns alg 2009-05-21
 */
package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.util.EnumConstUtil;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Embeddable
public class AlgConfigurationInfo implements Serializable 
{
	private static final long	serialVersionUID	= 1L;
	
	@Transient
	private GatewayType gatewayType = null;
	
	public enum GatewayType {
		FTP, SIP, TFTP, DNS, HTTP
	}
	
	private boolean ifEnable = false;
	
	private short qosClass = EnumConstUtil.QOS_CLASS_BACKGROUND;
	
	private int timeout = 30;
	
	private int duration = 60;

	public short getQosClass()
	{
		return qosClass;
	}

	public void setQosClass(short qosClass)
	{
		this.qosClass = qosClass;
	}

	public int getTimeout()
	{
		return timeout;
	}

	public void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}

	public int getDuration()
	{
		return duration;
	}

	public void setDuration(int duration)
	{
		this.duration = duration;
	}

	public GatewayType getGatewayType()
	{
		return gatewayType;
	}

	public void setGatewayType(GatewayType gatewayType)
	{
		this.gatewayType = gatewayType;
	}
	
	public String getkey() {
		return gatewayType.name();
	}

	public String getValue() {
		return MgrUtil.getEnumString("enum.gatewayType."
			+ gatewayType.name());
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

	public boolean isIfEnable()
	{
		return ifEnable;
	}

	public void setIfEnable(boolean ifEnable)
	{
		this.ifEnable = ifEnable;
	}
}
