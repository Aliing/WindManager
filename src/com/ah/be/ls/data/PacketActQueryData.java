/**
 *@filename		PacketActQueryData.java
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

public class PacketActQueryData {
	
	private byte m_data_type;
	
	private int m_iActCode;
	
	private String m_strActKey;
	
	private String m_strHMIP;
	
	private String m_strSystemId;
	
	private String m_strNetIp;	
	
	public void setDataType(byte dataType)
	{
		m_data_type = dataType;
	}
	
	public byte getDataType()
	{
		return m_data_type;
	}
	
	public void setActKey(String strActKey)
	{
		m_strActKey = strActKey;
	}

	public String getActKey()
	{
		return m_strActKey;
	}
	
	public void setHMIP(String strHMIP)
	{
		m_strHMIP = strHMIP;
	}
	
	public String getHMIP()
	{
		return m_strHMIP;
	}
	
	public void setSystemId(String strSystemId)
	{
		m_strSystemId = strSystemId;
	}
	
	public String getSystemId()
	{
		return m_strSystemId;
	}	
	
	public void setActCode(int iActCode)
	{
		m_iActCode = iActCode;
	}
	
	public int getActCode()
	{
		return m_iActCode;
	}	
	
	public void setNetIp(String strNetIp)
	{
		m_strNetIp = strNetIp;
	}
	
	public String getNetIp()
	{
		return m_strNetIp;
	}	
}
