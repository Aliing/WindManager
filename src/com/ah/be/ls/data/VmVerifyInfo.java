package com.ah.be.ls.data;

public class VmVerifyInfo {

    private byte m_data_type;
	
	private String m_strHMIP;
	
	private String m_strOrderKey;
	
	private String m_strSystemID;
	
	public static final int VM_VERIFy_OK = 0;
	public static final int VM_VERIFy_Err = 1;
	
	public void setDataType(byte dataType)
	{
		m_data_type = dataType;
	}
	
	public byte getDataType()
	{
		return m_data_type;
	}
	
	public void setHmIp(String strIp)
	{
		m_strHMIP = strIp;
	}
	
	public String getHmIp()
	{
		return m_strHMIP;
	}
	
	public void setOrderKey(String strKey)
	{
		m_strOrderKey = strKey;
	}
	
	public String getOrderKey()
	{
	    return m_strOrderKey;	
	}	
	
	
	public void setSystemID(String strId)
	{
		m_strSystemID = strId;
	}
	
	public String getSystemID()
	{
		return m_strSystemID;
	}

}
