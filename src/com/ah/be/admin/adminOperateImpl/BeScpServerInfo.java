/**
 * 
 */
package com.ah.be.admin.adminOperateImpl;

/**
 * @author root
 *
 */
public class BeScpServerInfo 
{
	private String m_strIp;
	
	private String m_strPort;
	
	private String m_strFilePath;
	
	private String m_strUsrName;
	
	private String m_strPsd;
	
	public String getScpIp()
	{
		return m_strIp;
	}
	
	public void setScpIp(String strIp)
	{
		m_strIp = strIp;
	}
	
	public String getScpPort()
	{
		return m_strPort;
	}
	
	public void setScpPort(String strPort)
	{
		m_strPort = strPort;
	}
	
	public String getFilePath()
	{
		return m_strFilePath;
	}
	
	public void setFilePath(String strFilePath)
	{
		m_strFilePath = strFilePath;
	}
	
	public String getScpUsr()
	{
		return m_strUsrName;
	}
	
	public void setScpUsr(String strUsr)
	{
		m_strUsrName = strUsr;
	}
	
	public String getScpPsd()
	{
		return m_strPsd;
	}
	
	public void setScpPsd(String strPsd)
	{
		m_strPsd = strPsd;
	}
}
