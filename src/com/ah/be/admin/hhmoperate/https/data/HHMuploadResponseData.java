package com.ah.be.admin.hhmoperate.https.data;

public class HHMuploadResponseData {
    private byte m_packet_type;
	
	private byte m_protocol_version;	
	
    private byte m_send_status;
	
	private long m_lOffset;
	
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
	

}
