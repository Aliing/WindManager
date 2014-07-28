package com.ah.be.config.create.source;

import java.io.IOException;

import com.ah.be.config.create.CreateXMLException;


/**
 * @author zhang
 * @version 2008-1-3 14:14:54
 */

public interface CapwapProfileInt {

//	public boolean isConfigCapwap() throws CreateXMLException;
	
	public String getCapwapGuiName();
	
	public String getApVersion();
	
	public boolean isConfigCapwapPrimary();
	
	public boolean isConfigCapwapBackup();

	public String getCapwapIpPrimary();
	
	public String getCapwapIpBackup();
	
	public boolean isConfigCapwapPort();
	
	public int getServerPort();
	
	public String getUpdateTime();
	
	public int getCwpHeartbeatInterval();
	
	public int getCwpDeadInterval();
	
	public boolean isEnableCwpDtls();
	
	public boolean isConfigCwpDtlsBootPassPhrase();
	
	public boolean isConfigHmDefinedPassphrase();
	
	public String getHmDefinedPassphraseValue() throws IOException;
	
	public int getHmDefinedPassphraseKey();
	
	public String getCwpDtlsBootPassPhrase();
	
	public boolean isEnableVpnTunnel(String serverAddr) throws CreateXMLException;
	
	public String getVhmValue();
	
	public boolean isConfigPCI();
	
	public boolean isEnablePCI();
	
	public boolean isEnableAutoDiscovery();
}
