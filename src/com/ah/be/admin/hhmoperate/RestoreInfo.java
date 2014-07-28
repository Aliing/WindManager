package com.ah.be.admin.hhmoperate;

public class RestoreInfo {
	
	private boolean m_bResult;
	
	private String m_error_msg;
	
	public void setResult(boolean bResult)
	{
		m_bResult = bResult;
	}
	
	public boolean getResult()
	{
		return m_bResult;
	}
	
	public void setErrorMsg(String strMsg)
	{
		m_error_msg = strMsg;
	}
	
	public String getErrorMsg()
	{
		return m_error_msg;
	}

}
