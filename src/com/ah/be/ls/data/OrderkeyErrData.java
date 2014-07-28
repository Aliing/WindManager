package com.ah.be.ls.data;

public class OrderkeyErrData {
	
	private String m_strOrderKey;
	
	private byte m_data_type;
	

	public void setOrderKey(String strKey)
	{
		m_strOrderKey = strKey;
	}
	
	public String getOrderKey()
	{
	    return m_strOrderKey;	
	}

	public void setDataType(byte dataType)
	{
		m_data_type = dataType;
	}
	
	public byte getDataType()
	{
		return m_data_type;
	}
}
