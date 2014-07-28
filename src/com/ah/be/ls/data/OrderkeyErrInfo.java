package com.ah.be.ls.data;


public class OrderkeyErrInfo {
	private byte                m_data_type;
	
	private boolean				responseFlag;
	
	public void setDataType(byte dataType)
	{
		m_data_type = dataType;
	}
	
	public byte getDataType()
	{
		return m_data_type;
	}

	public boolean getResponseFlag() {
		return responseFlag;
	}

	public void setResponseFlag(boolean responseFlag) {
		this.responseFlag = responseFlag;
	}	

}
