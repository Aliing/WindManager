package com.ah.be.ls.data;

public class PacketNewVersionFlagResponseData {
	
    private byte m_data_type;
	
	private byte m_new_version_flag;	
	
	public void setDataType(byte dataType)
	{
		m_data_type = dataType;
	}
	
	public byte getDataType()
	{
		return m_data_type;
	}	
	
	public void setNewVersionFlag(byte bFlag)
	{
		m_new_version_flag = bFlag;
	}
	
	public byte getNewVersionFlag()
	{
		return m_new_version_flag;
	}
}
