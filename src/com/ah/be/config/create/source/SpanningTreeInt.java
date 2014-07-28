package com.ah.be.config.create.source;

import com.ah.xml.be.config.SpanningTreeModeValue;

public interface SpanningTreeInt {

	public boolean isConfigSpanningTree();
	
	public boolean isEnableDeviceSpanningTree();
	
	public boolean isEnableSpanningTree();
	
	public SpanningTreeModeValue getSpanningMode();
	
	public boolean isModeMstp();
	
	public int getForwardTime();
	
	public boolean isConfigHelloTime();
	
	public int getHelloTime();
	
	public int getMaxAge();
	
	public boolean isConfigMaxHops();
	
	public int getMaxHops();
	
	public int getPriority();
	
	public boolean isConfigRegion();
	
	public String getRegionValue();
	
	public boolean isConfigRevision();
	
	public int getRevision();
	
	public boolean isConfigForceVersion();
	
	public int getForceVersion();
	
	public int getMstInstanceSize();
	
	public String getMstInstanceName(int index);
	
	public int getInstancePriority(int index);
	
	public int getInstanceVlanSize(int index);
	
	public String getInstanceVlanName(int index, int i);
}
