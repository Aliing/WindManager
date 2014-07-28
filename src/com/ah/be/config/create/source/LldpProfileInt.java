package com.ah.be.config.create.source;


/**
 * @author zhang
 * @version 2008-10-16 09:47:35
 */

public interface LldpProfileInt {
	
	public int getRepeatCount();
	
	public int getDelayTime();
	
	public String getLLDPGuiName();
	
	public String getLLDPName();

	public boolean isConfigLldp();
	
	public boolean isEnableLldp();
	
	public boolean isOverrideConfig();
	
	public boolean isEnableCdp();
	
	public int getHoldTime();
	
	public int getTimer();
	
	public int getLldpMaxEntries();
	
	public int getCdpMaxEntries();
	
	public boolean isConfigCdp();
	
	public boolean isEnableReceiveOnly();
	
	public int getLldpMaxPower();
	
}
