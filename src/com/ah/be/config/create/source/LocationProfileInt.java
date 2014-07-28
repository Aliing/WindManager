package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;

/**
 * @author zhang
 * @version 2007-12-18  06:01:07
 */

public interface LocationProfileInt {
	
	public String getLocationGuiName();
	
	public String getLocationName();

	public String getApVersion();
	
	public boolean isConfigLocation() throws CreateXMLException;
	
	public String getUpdateTime();
	
	public boolean isEnableLocationServer();
	
	public boolean isEnableRogueAp();
	
	public boolean isEnableStation();
	
	public boolean isEnableTag();
	
	public String getLocationServer() throws CreateXMLException;
	
	public boolean isConfigRateThreshold();
	
	public int getRateThresholdTag();
	
	public int getRateThresholdStation();
	
	public int getRateThresholdRogue();
	
	public boolean isLocationAerohive();
	
	public boolean isLocationAeroscout();
	
	public int getRssiUpdateThreshold();
	
	public int getRssiValidPeriod();
	
	public int getRssiHoldTime();
	
	public int getReportInterval();
	
	public int getSuppressReport();
	
	public int getAerohiveMacSize();
	
	public String getAerohiveMacAddr(int index);
	
	public int getAerohiveOuiSize();
	
	public String getAerohiveOuiAddr(int index);
	
	public boolean isEnableListMatch();
	
	public boolean isConfigLocationEkahau();
	
	public String getMcastMac();
	
	public boolean isConfigEkahauServer();
	
	public String getEkahauServerValue() throws CreateXMLException;
	
	public int getEkahauServerPort();
}
