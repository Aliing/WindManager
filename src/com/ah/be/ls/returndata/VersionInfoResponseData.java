package com.ah.be.ls.returndata;

import com.ah.be.ls.data.PacketVersionInfoResponseData;

public class VersionInfoResponseData {
	
	private PacketVersionInfoResponseData m_valid_response;
	
	private int m_response_type;
	
	public void setValidResponse(PacketVersionInfoResponseData oData)
	{
		m_valid_response = oData;
	}
	
	public PacketVersionInfoResponseData getValidResponse()
	{
		return m_valid_response;
	}
	
	public void setResponseType(int iType)
	{
	    m_response_type = iType;	
	}
	
	public int getResponseType()
	{
		return m_response_type;
	}

}
