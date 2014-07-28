package com.ah.be.admin.hhmoperate.https.data;

public class HHMuploadHeadData {
	
	private byte m_packet_type;	
	
	private byte m_protocol_version;
	
	private String m_HHM_flag;	
	
	private String m_file_path;
	
	private String m_file_name;
	
    private byte m_send_status;
	
	private long m_lOffset;
	
	private int m_iDataLength;
	
	public void setPackType(byte bType)
	{
	    m_packet_type = bType;	
	}
	
	public byte getPackType()
	{
		return m_packet_type;
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
	
	public void setSendStatus(byte bStatus)
	{
		m_send_status = bStatus;
	}
	
	public byte getSendStatus()
	{
		return m_send_status;
	}
	
	public void setOffset(long lOffset)
	{
		m_lOffset = lOffset;
	}
	
	public long getOffset()
	{
		return m_lOffset;
	}
	
	public void setDataLength(int iLength)
	{
		m_iDataLength = iLength;
	}
	
	public int getDataLength()
	{
		return m_iDataLength;
	}	
}
