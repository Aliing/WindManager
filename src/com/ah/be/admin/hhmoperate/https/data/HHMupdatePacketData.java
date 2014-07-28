package com.ah.be.admin.hhmoperate.https.data;

import com.ah.bo.admin.HmDomain;

public class HHMupdatePacketData {
	
    private byte m_packet_type;
	
	private int  m_head_length;
	
	private byte m_protocol_version;
	
	private String m_HHM_flag;	
	
	private String m_file_path;
	
	private String m_file_name;
	
	private String m_HHM_version;

	private HmDomain m_domain;
	
	public void setPackType(byte bType)
	{
	    m_packet_type = bType;	
	}
	
	public byte getPackType()
	{
		return m_packet_type;
	}
	
	public void setHeadLength(int iLength)
	{
		m_head_length = iLength;
	}

	public int getHeadLength()
	{
		return m_head_length;
	}
	
	public void setProtocolVersion(byte bVersion)
	{
		m_protocol_version = bVersion;
	}
	
	public byte getProtocolVersion()
	{
		return m_protocol_version;
	}
	
	public void setHHMFlag(String strFlag)
	{
		m_HHM_flag = strFlag;
	}
	
	public String getHHMFlag()
	{
		return m_HHM_flag;
	}	
	
	public void setFilePath(String strPath)
	{
		m_file_path = strPath;
	}
	
	public String getFilePath()
	{
		return m_file_path;
	}
	
	public void setFileName(String strFileName)
	{
		m_file_name = strFileName;
	}
	
	public String getFileName()
	{
		return m_file_name;
	}
	
	public void setHHMVersion(String strVersion)
	{
		m_HHM_version = strVersion;
	}
	
	public String getHHMVersion()
	{
		return m_HHM_version;
	}
	
	public void setHHMDomain(HmDomain oDomain)
	{
		m_domain = oDomain;
	}
	
	public HmDomain getHHMDomain()
	{
		return m_domain;
	}
}
