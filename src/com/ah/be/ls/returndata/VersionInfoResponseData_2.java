package com.ah.be.ls.returndata;

import com.ah.be.ls.data.CommErrInfo;
import com.ah.be.ls.data.PacketVersion2ResponseData;

public class VersionInfoResponseData_2 {
	private int m_response_type;
	private PacketVersion2ResponseData m_valid_response;
	private CommErrInfo      m_invalid_response;
	
	public void setResponseType(int iType)
	{
		m_response_type = iType;
	}
	
	public int getResponseType()
	{
		return m_response_type;
	}
	
	public void setVaildResponse(PacketVersion2ResponseData oResponse)
	{
		m_valid_response = oResponse;
	}
	
	public PacketVersion2ResponseData getValidResponse()
	{
		return m_valid_response;
	}
	
	public void setInvalidResponse(CommErrInfo oResponse)
	{
		m_invalid_response = oResponse;
	}
	
	public CommErrInfo getInvalidResponse()
	{
		return m_invalid_response;
	}

}
