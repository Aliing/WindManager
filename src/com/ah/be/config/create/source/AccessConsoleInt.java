package com.ah.be.config.create.source;

import com.ah.xml.be.config.AcModeValue;

/**
 * @author zhang
 * @version 2008-9-16 16:52:36
 */

public interface AccessConsoleInt {
	
	public enum ProtocolSuitType{
		open, wpa_auto_psk, wpa_tkip_psk, wpa_aes_psk, 
		wpa2_tkip_psk, wpa2_aes_psk
	}
	
	public String getAccessConsoleGuiName();
	
	public String getAccessConsoleName();
	
	public boolean isConfigAccessConsole();
	
	public AcModeValue getConsoleMode();
	
	public int getMaxClient();
	
	public boolean isHideSsid();
	
	public boolean isTelentEnable();
	
	public boolean isConfigMacFilter();
	
	public String getMacFilterName();
	
	public String getAscIIkey();
	
	public boolean isConfigProtocolSuitWithType(ProtocolSuitType type);

}
