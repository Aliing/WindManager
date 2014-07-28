package com.ah.be.config.create.source;


/**
 * 
 * @author zhang
 *
 */
public interface DnsProfileInt {
	
	public String getMgmtServiceDnsGuiName();
	
	public String getMgmtServiceDnsName();
	
	public String getApVersion();

	public boolean isConfigureDns();
	
	public boolean isConfigDomainName();
	
	public String getDnsServerDomainName();
	
	public int getDnsServerIpSize();
	
	public String getDnsServerIp(int index);
	
	public boolean isConfigureFirst(int index);
	
	public boolean isConfigureSecond(int index);
	
	public boolean isConfigureThird(int index);
}
