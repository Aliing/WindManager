package com.ah.be.config.create.source;


/**
 * @author zhang
 * @version 2008-4-15 15:23:07
 */

public interface SystemProfileInt {
	
	public String getMgmtOptionGuiName();
	
	public String getMgmtOptionName();
	
	public boolean isHiveAp11n();

	public boolean isConfigMgmtOpt();
	
	public boolean isConfigHighThreshold();

	public int getHighThreshold();
	
	public int getUnderSpeedThreshold();
	
	public boolean isConfigSmartPoe();
	
	public boolean getSmartPoeEnable();
	
	public boolean isSystemLedBright();
	
	public boolean isSystemLedSoft();
	
	public boolean isSystemLedDim();
	
	public boolean isSystemLedOff();
	
	public boolean isIcmpRedirectEnable();
	
	public boolean isWebServerEnable();
	
	public boolean isConfigSystemLed();
	
	public boolean isSupportPoEMode();
	
	public short getPoEPowerMode();
	
	public String getPoEPrimaryEth();
}
