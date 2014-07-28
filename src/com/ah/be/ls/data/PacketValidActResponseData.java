/**
 *@filename		PacketValidActResponseData.java
 *@version
 *@author		xiaolanbao
 *@createtime	2009-4-7 09:37:14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.ls.data;


public class PacketValidActResponseData {
	
	private byte m_data_type;
	
	private int m_act_code;
	
	private short m_period;
	
	private byte m_retry_times;
	
	private int m_interval;	
	
	public void setDataType(byte dataType)
	{
		m_data_type = dataType;
	}
	
	public byte getDataType()
	{
		return m_data_type;
	}
	
	public void setActCode(int iCode)
	{
		m_act_code = iCode;
	}
	
	public int getActCode()
	{
		return m_act_code;
	}
	
	public void setPerid(short sPeriod)
	{
	    m_period = sPeriod;	
	}

	public short getPeriod()
	{
		return m_period;
	}
	
	public void setRetryTimes(byte bTimes)
	{
		m_retry_times = bTimes;
	}
	
	public byte getRetryTimes()
	{
		return m_retry_times;
	}
	
	public void setInterval(int iInterval)
	{
		m_interval = iInterval;
	}
	
	public int getInterval()
	{
		return m_interval;
	}	
}
