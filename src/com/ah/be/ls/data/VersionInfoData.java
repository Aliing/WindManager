package com.ah.be.ls.data;

public class VersionInfoData {
	
	private  byte m_bProType;
	
	private  long m_lFileSize;
	
	private  String m_strVersion;
	
	private  int m_iUid;
	
	public void setProType(byte bType)
	{
		m_bProType = bType;
	}
	
	public byte getProType()
	{
		return m_bProType;
	}
	
	public void setFileSize(long lFileSize)
	{
		m_lFileSize = lFileSize;
	}
	
	public long getFileSize()
	{
		return m_lFileSize;
	}
	
	public void setVersion(String strVersion)
	{
		m_strVersion = strVersion;
	}
	
	public String getVersion()
	{
		return m_strVersion;
	}
	
	public void setUid(int iId)
	{
	    m_iUid = iId;	
	}
	
	public int getUid()
	{
		return m_iUid;
	}

}
