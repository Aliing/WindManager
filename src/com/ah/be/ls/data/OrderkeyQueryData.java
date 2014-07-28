package com.ah.be.ls.data;

public class OrderkeyQueryData {
	
	private byte m_data_type;
	
	private String m_strHMIP;
	
	private String m_strOrderKey;
	
	private int m_HMType;
	
	private String m_strFulfillmentID;
	
	private String currentType;
	
	private int currentAp;
	
	private int currentVhm;
	
	private int currentCvg = 0;
	
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
	
	public void setHMType(int iType)
	{
		m_HMType = iType;
	}
	
	public int getHMType()
	{
		return m_HMType;
	}
	
	public void setFulfillmentID(String strId)
	{
		m_strFulfillmentID = strId;
	}
	
	public String getFulfillmentID()
	{
		return m_strFulfillmentID;
	}

	public String getCurrentType()
	{
		return currentType;
	}

	public void setCurrentType(String currentType)
	{
		this.currentType = currentType;
	}

	public int getCurrentAp()
	{
		return currentAp;
	}

	public void setCurrentAp(int currentAp)
	{
		this.currentAp = currentAp;
	}

	public int getCurrentVhm()
	{
		return currentVhm;
	}

	public void setCurrentVhm(int currentVhm)
	{
		this.currentVhm = currentVhm;
	}

	public int getCurrentCvg()
	{
		return currentCvg;
	}

	public void setCurrentCvg(int currentCvg)
	{
		this.currentCvg = currentCvg;
	}

}
