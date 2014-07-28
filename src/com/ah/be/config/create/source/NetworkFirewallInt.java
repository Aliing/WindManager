package com.ah.be.config.create.source;

public interface NetworkFirewallInt {

	public boolean isConfigNetFirewall();
	
	public int getNetFirewallRullSize();
	
	public String getNetFirewallRullName(int index);
	
	public int getNetFirewallRullPosition(int index);
	
	public String getNetFirewallRullFrom(int index);
	
	public String getNetFirewallRullTo(int index);
	
	public String getNetFirewallRullService(int index);
	
	public String getNetFirewallRullAction(int index);
	
	public String getNetFirewallRullLogging(int index);
}
