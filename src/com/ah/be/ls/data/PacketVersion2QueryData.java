package com.ah.be.ls.data;

public class PacketVersion2QueryData {
	
private byte m_data_type;
	
    private byte m_bProType;
    
    private String m_sSystemId;
    
    private boolean isOrderkey;
    
    private String m_sOrderkey;
    
    private int    m_iUid;
    
    private short m_bHmType ;
    
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
	
	public void setSystemId(String strId)
	{
		m_sSystemId = strId;
	}
	
	public String getSystemId()
	{
		return m_sSystemId;
	}
	
	public void setOrderkey(String strOrderkey)
	{
		m_sOrderkey = strOrderkey;
	}
	
	public String getOrderkey()
	{
		return m_sOrderkey;
	}
	
	public void setIsOrderkey(boolean bFlag)
	{
		isOrderkey = bFlag;	
	}
	
	public boolean IsOrderkey()
	{
		return isOrderkey;
	}
	
	public void setUid(int iId)
	{
		m_iUid = iId;
	}
	
	public int getUid()
	{
		return m_iUid;
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
