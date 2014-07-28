package com.ah.be.config.create.source;

public interface MonitorProfileInt {
	
	public int getSessionSize();
	
	public String getSessionName(int i);
	
	public boolean isSessionEnable(int i);
	
	public String getInfDestinationName(int i);
	
	public int getSourceVlanSize(int i);
	
	public String getSourceVlanName(int i, int j);
	
	public boolean isSourceVlanIngress(int i, int j);
	
	public int getSourceInterfaceSize(int i);
	
	public String getSourceInterfaceName(int i, int j);
	
	public boolean isConfigSourceInterfaceIngress(int i, int j);
	
	public boolean isConfigSourceInterfaceBoth(int i, int j);
	
	public boolean isConfigSourceInterfaceEgress(int i, int j);
}
