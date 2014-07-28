/**
 *@filename		PacketDownLoadQueryData.java
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

public class PacketVersionInfoQueryData {
	
	private byte m_data_type;
	
    private byte m_bProType;
    
    private short m_bHmType;
    
    private byte m_updateLimited;
	
    private String m_strInnerVersion;
	
	public void setDataType(byte dataType)
	{
		m_data_type = dataType;
	}
	
	public byte getDataType()
	{
		return m_data_type;
	}
	
	public void setProType(byte bProType)
	{
		m_bProType = bProType;
	}
	
	public byte getProType()
	{
		return m_bProType;
	}
	
	public void setInnerVersion(String strVersion)
	{
		m_strInnerVersion = strVersion;
	}
	
	public String getInnerVersion()
	{
		return m_strInnerVersion;
	}
	
	public void setUpdateLimited(byte bLimited)
	{
		m_updateLimited = bLimited;
	}
	
	public byte getUpdateLimited()
	{
		return m_updateLimited;
	}
	
	public short getHmType()
	{
		return m_bHmType;
	}
	
	public void setHmType(short sType)
	{
		m_bHmType = sType;
	}
}
