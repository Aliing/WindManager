package com.ah.be.config.create.source;

import com.ah.xml.be.config.AhAllowBlockValue;

public interface WebSecurityProxyInt {
	
	public enum ProxyType{
		barracuda, websense
	}
	
	public boolean isConfigWebProxy();

	public boolean isConfigWebProxy(ProxyType type);
	
	public boolean isConfigHttpHost(ProxyType type);
	
	public String getHttpHost(ProxyType type);
	
	public boolean isConfigHttpPort(ProxyType type);
	
	public int getHttpPort(ProxyType type);
	
	public boolean isConfigHttpsHost(ProxyType type);
	
	public String getHttpsHost(ProxyType type);
	
	public boolean isConfigHttpsPort(ProxyType type);
	
	public int getHttpsPort(ProxyType type);
	
	public int getSubnetSize(ProxyType type);
	
	public String getSubnetValue(ProxyType type, int index);
	
	public AhAllowBlockValue getSubnetAction(ProxyType type, int index);
	
	public boolean isConfigAccountId(ProxyType type);
	
	public String getAccountId(ProxyType type);
	
	public boolean isConfigDefaultUsername(ProxyType type);
	
	public String getDefaultUsername(ProxyType type);
	
	public boolean isConfigDefaultDomain(ProxyType type);
	
	public String getDefaultDomain(ProxyType type);
	
	public boolean isEnableWebProxy(ProxyType type);
	
	public boolean isConfigAccountKey(ProxyType type);
	
	public String getAccountKey(ProxyType type);
	
	public int getWhitelistSize(ProxyType type);
	
	public String getWhitelistName(ProxyType type, int index);
	
	public boolean isEnableOpenDNS();
	
	public String getOpenDNSDID(Long userProfileId);
	
	public String getOpenDNSServer1();
	
	public String getOpenDNSServer2();
}
