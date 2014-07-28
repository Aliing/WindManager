/**
 *@filename		PacketInvalidActResponseData.java
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

public class PacketInvalidActResponseData {
	
	private byte m_data_type;
	
	private byte m_bProType;
	
	private byte m_bOperation;
	
	private String m_strDescription;
	
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
	
	public void setOperation(byte bOperation)
	{
		m_bOperation = bOperation;
	}
	
	public byte getOperation()
	{
		return m_bOperation;
	}
	
	public void setDesc(String strDesc)
	{
		m_strDescription = strDesc;
	}
	
	public String getDesc()
	{
		return m_strDescription;
	}

}
