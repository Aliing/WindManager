package com.ah.be.ls.data;

public class CommErrInfo {
	
	private byte m_data_type;
	
	private int error_code;
	
	private String error_string;
	
	public void setDataType(byte dataType)
	{
		m_data_type = dataType;
	}
	
	public byte getDataType()
	{
		return m_data_type;
	}
	
	public void setErrCode(int iCode)
	{
		error_code = iCode;
	}
	
	public int getErrCode()
	{
		return error_code;
	}
	
	public void setErrInfo(String strErrInfo)
	{
		error_string = strErrInfo;
	}
	
	public String getErrInfo()
	{
		return error_string;
	}

}
