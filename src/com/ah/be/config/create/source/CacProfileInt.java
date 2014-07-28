package com.ah.be.config.create.source;


/**
 * @author zhang
 * @version 2008-8-7 10:25:09
 */

public interface CacProfileInt {
	
	public String getMgmtServiceGuiName();
	
	public String getMgmtServiceName();

	public boolean isConfigCac();
	
	public boolean isEnableCac();
	
	public int getCacAirtimePerSecond();
	
	public int getRoamingAirtime();
}
