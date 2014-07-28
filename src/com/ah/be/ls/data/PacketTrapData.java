package com.ah.be.ls.data;

public class PacketTrapData {
	
	private byte m_data_type;
	
    private String m_strActKey;
	
	private String m_strHMIP;
	
	private String m_strSystemId;
	
	private byte  m_trap_type;
	
	private byte m_severity;
	
	private long m_time;
	
	private short m_sReason;
	
	private String m_strDescrition;
	
	private String m_strNetIP;
	
	public void setDataType(byte bType)
	{
		m_data_type = bType;
	}
	
	public byte getDataType()
	{
		return m_data_type;
	}
	
	public void setActKey(String strKey)
	{
		m_strActKey = strKey;
	}
	
	public String getActKey()
	{
		return m_strActKey;
	}
	
	public void setHmIP(String strIP)
	{
		m_strHMIP = strIP;
	}
	
	public String getHmIP()
	{
		return m_strHMIP;
	}
	
	public void setSystemId(String strSystemId)
	{
		m_strSystemId = strSystemId;
	}
	
	public String getSystemId()
	{
		return m_strSystemId;
	}
	
	public void setTrapType(byte bType)
	{
		m_trap_type = bType;
	}
	
	public byte getTrapType()
	{
		return m_trap_type;
	}
	
	public void setSeverity(byte bSeverity)
	{
		m_severity = bSeverity;
	}
	
	public byte getSeverity()
	{
		return m_severity;
	}
	
	public void setTrapTime(long lTime)
	{
		m_time = lTime;
	}
	
	public long getTime()
	{
		return m_time;
	}
	
	public void setReason(short sReason)
	{
		m_sReason = sReason;
	}
	
	public short getReason()
	{
		return m_sReason;
	}
	
	public void setDescription(String strDesc)
	{
		m_strDescrition = strDesc;
	}
	
	public String getDescription()
	{
		return m_strDescrition;
	}
	
	public void setNetIp(String strIp)
	{
		m_strNetIP = strIp;
	}
	
	public String getNetIp()
	{
		return m_strNetIP;
	}

}
