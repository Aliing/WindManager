/**
 *@filename		PacketHeadData.java
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



public class PacketHeadData {
	
	private byte m_bType;
	
	private int m_iLength;
	
	private byte m_protocol_version;
	
	private byte m_bIsecret;
	
	public void setType(byte bType)
	{
		m_bType = bType;
	}
	
	public byte getType()
	{
		return m_bType;
	}
	
	public void setLength(int iLength)
	{
		m_iLength = iLength;
	}
	
	public int getLength()
	{
		return m_iLength;
	}
	
	public void setProtocolVersion(byte bprotocol_version)
	{
		m_protocol_version = bprotocol_version;
	}
	
	public byte getProtocolVersion()
	{
		return m_protocol_version;
	}
	
	public void setSecretFlag(byte bSecFlag)
	{
		m_bIsecret = bSecFlag;
	}
	
	public byte getSecretFlag()
	{
		return m_bIsecret;
	}
	

}
