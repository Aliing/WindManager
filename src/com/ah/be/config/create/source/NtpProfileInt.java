package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;

/**
 * 
 * @author zhang
 *
 */
public interface NtpProfileInt {
	
	public String getNtpGuiName();
	
	public String getNtpName();
	
	public String getApVersion();
	
	public boolean isConfigureNtp();
	
	public int getIntervalValue();
	
	public boolean isEnableNtpServer();
	
	public int getNtpServerSize();
	
	public String getNtpServerAddress(int index) throws CreateXMLException;
	
	public boolean isNtpServerFirst(int index);
	
	public boolean isNtpServerSecond(int index);
	
	public boolean isNtpServerThird(int index);
	
	public boolean isNtpServerFourth(int index);
	
	public boolean isEnableVpnTunnel(String serverAddr) throws CreateXMLException;
	
//	public boolean isIpAddr(int index);
}
