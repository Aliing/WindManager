package com.ah.be.sa3party;

public class Elem_wifi_info {
    private String m_wifi_name;
    private int    m_report_interval;
    private int    m_run_time;
    private int    m_num_channel;
    private int[]  m_channels = null;
    
    public void setWifiName(String strName)
    {
    	m_wifi_name = strName;
    }
    
    public String getWifiName()
    {
    	return m_wifi_name;
    }
    
    public void setReportInterval(int interval)
    {
    	m_report_interval = interval;
    }
    
    public int getReportInterval()
    {
    	return m_report_interval;
    }
    
    public void setRuntime(int iRuntime)
    {
    	m_run_time = iRuntime;
    }
    
    public int getRuntime()
    {
    	return m_run_time;
    }
    
    public void setNumChannel(int inum)
    {
    	m_num_channel = inum;
    }
    
    public void setchannel(int iChannel, int index)
    {
    	if (null == m_channels)
    	{
    		m_channels = new int[m_num_channel];
    	}
    	m_channels[index] = iChannel;
    }
    
    public int[] getchannels()
    {
    	return m_channels;
    }
}
