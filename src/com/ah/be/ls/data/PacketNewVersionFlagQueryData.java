package com.ah.be.ls.data;

public class PacketNewVersionFlagQueryData {
	
    private byte m_data_type;	
    
    private boolean m_need_Act_key_Flag;
	
	private String m_strActKey;
	
	private String m_strSystemId;
	
	private String m_strInnerVersion;
	
	private byte m_updateLimited;
	
	private byte m_bProType;	
	
	private short m_bHmType;
	
	private boolean isNeedOrderKey = false;
	
	private String m_strOrderkey;
	
	private int uid;
	
	public void setDataType(byte dataType)
	{
		m_data_type = dataType;
	}
	
	public byte getDataType()
	{
		return m_data_type;
	}
	
	public void setNeedActKeyFlag(boolean bFlag)
	{
		m_need_Act_key_Flag = bFlag;
	}
	
	public boolean getNeedActKeyFlag()
	{
		return m_need_Act_key_Flag;
	}
	
	public void setActKey(String strActKey)
	{
		m_strActKey = strActKey;
	}

	public String getActKey()
	{
		return m_strActKey;
	}
	
	public void setSystemId(String strSystemId)
	{
		m_strSystemId = strSystemId;
	}
	
	public String getSystemId()
	{
		return m_strSystemId;
	}
	
	public void setInnerVersion(String strInnerVersion)
	{
		m_strInnerVersion = strInnerVersion;
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
	
	public void setProType(byte bProtype)
	{
		m_bProType = bProtype;
	}
	
	public byte getProType()
	{
		return m_bProType;
	}	
	
	public short getHmType()
	{
		return m_bHmType;
	}
	
	public void setHmType(short sType)
	{
		m_bHmType = sType;
	}
	
	public boolean isNeedOrderkey()
	{
		return isNeedOrderKey;
	}
	
	public void setNeedOrderkey(boolean bFlag)
	{
		isNeedOrderKey= bFlag;
	}
	
	public String getOrderkey()
	{
		return m_strOrderkey;
	}
	
	public void setOrderkey(String strOrderkey)
	{
		m_strOrderkey = strOrderkey;
	}
	
	public int getUid()
	{
		return uid;
	}
	
	public void setUid(int id)
	{
		uid = id;
	}
}
