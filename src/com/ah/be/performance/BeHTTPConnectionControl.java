package com.ah.be.performance;

import com.ah.be.common.ConfigUtil;

public class BeHTTPConnectionControl
{
	private static BeHTTPConnectionControl beHTTPConnectionControl;
	private int connectCount = 100;
	private int usedConnectCount = 0;
	private BeHTTPConnectionControl()
	{
		connectCount = Integer.parseInt(ConfigUtil.getConfigInfo(
				ConfigUtil.SECTION_PERFORMANCE,
				ConfigUtil.KEY_HTTPCONNECTIONS_LIMIT, "100"));		
	}

	public static BeHTTPConnectionControl getInstance() {
		if(null == beHTTPConnectionControl) {
			beHTTPConnectionControl	= new BeHTTPConnectionControl();
		}
		return beHTTPConnectionControl;
	}
	public synchronized boolean add()
	{
		if(usedConnectCount >= connectCount) {
			return false;
		}
		else {
			usedConnectCount++;
			return true;
		}
	}
	
	public synchronized boolean isFull()
	{
		if(usedConnectCount >= connectCount) {
			return true;
		}
		else {
			return false;
		}
			
	}
	public synchronized void delete()
	{
		if(usedConnectCount >= 1) {
			usedConnectCount--;
		}
	}
}