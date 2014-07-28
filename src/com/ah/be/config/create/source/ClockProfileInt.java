package com.ah.be.config.create.source;


/**
 * 
 * @author zhang
 *
 */
public interface ClockProfileInt {
	
	public String getMgmtServiceTimeGuiName();
	
	public String getMgmtServiceTimeName();
	
	public String getApVersion();
	
	public String getUpdateTime();
	
	public int getTimeZone();
	
	public String getTimeZoneMin();
	
	public boolean isConfigureDaylightTime();
	
	public String getDayLightTime();
}
