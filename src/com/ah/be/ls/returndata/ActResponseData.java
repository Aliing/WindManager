package com.ah.be.ls.returndata;

import com.ah.be.ls.data.PacketInvalidActResponseData;
import com.ah.be.ls.data.PacketValidActResponseData;

public class ActResponseData {
	
	private PacketInvalidActResponseData m_invalid_response_data;
	
	private PacketValidActResponseData m_valid_respon_data ;
	
	private int m_response_type;
	
	public void setValidResponseData(PacketValidActResponseData oData)
	{
		m_valid_respon_data = oData;
	}
	
	public PacketValidActResponseData getValidResponseData()
	{
		return m_valid_respon_data;
	}
	
	public void setInvalidResponseData(PacketInvalidActResponseData oData)
	{
		m_invalid_response_data = oData;
	}
	
	public PacketInvalidActResponseData getInvalidResponse()
	{
		return m_invalid_response_data;
	}
	
	public void setResponseType(int iType )
	{
		m_response_type = iType;
	}
	
	public int getResponseType()
	{
		return m_response_type;
	}

}
