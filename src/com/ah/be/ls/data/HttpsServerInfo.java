package com.ah.be.ls.data;

public class HttpsServerInfo {
	
	private String m_https_host;
	
	private String m_https_query;
	
	private int m_https_port;
	
	public void setHost(String strHost)
	{
		m_https_host = strHost;
	}
	
	public String getHost()
	{
		return m_https_host;
	}
	
	public void setQuery(String strQuery)
	{
		m_https_query = strQuery;
	}
	
	public String getQuery()
	{
		return m_https_query;
	}
	
	public void setPort(int iPort)
	{
		m_https_port = iPort;
	}
	
	public int getPort()
	{
		return m_https_port;
	}

}
