package com.ah.be.admin.hhmoperate;

//import com.ah.bo.admin.HmDomain;

public class BackupInfo {
	
	private boolean m_bResult;

	private String m_str_filepath;
	
	private String m_str_filename;
	
	private String m_error_msg;
	
	public void setFilePath(String strPath)
	{
		m_str_filepath = strPath;
	}
	
	public String getFilePath()
	{
		return m_str_filepath;
	}
	
	public void setFileName(String strFileName)
	{
		m_str_filename = strFileName;
	}
	
	public String getFileName()
	{
		return m_str_filename;
	}
	
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
