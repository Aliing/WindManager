package com.ah.be.ls.data;

public class UploadFileInfoRequestData {
	
	private byte m_packet_type;
	
	private byte m_protocol_version;
	
    private byte m_data_type;
    
    private boolean m_need_Act_key_Flag;

	private String m_strAck;
	
	private String m_strSystemId;	
	
	private byte m_send_status;
	
	private long m_lOffset;
	
	private int m_iDataLength;
	
	public void setPacketType(byte bType)
	{
		m_packet_type = bType; 
	}
	
	public byte getPacketType()
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
	
	public void setActKey(String strActKey)
	{
		m_strAck = strActKey;
	}
	
	public String getActKey()
	{
		return m_strAck;
	}
	
	public void setSendStatus(byte bStatus)
	{
		m_send_status = bStatus;
	}
	
	public byte getSendStatus()
	{
		return m_send_status;
	}
	
	public void setType(byte bType)
	{
		m_data_type = bType;	
	}
	
	public byte getType()
	{
		return m_data_type;
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
	
	public void setNeedActKeyFlag(boolean bFlag)
	{
		m_need_Act_key_Flag = bFlag;
	}
	
	public boolean getNeedActKeyFlag()
	{
		return m_need_Act_key_Flag;
	}
	
	public void setSystemId(String strSystemId)
	{
		m_strSystemId = strSystemId;
	}
	
	public String getSystemId()
	{
		return m_strSystemId;
	}
}
