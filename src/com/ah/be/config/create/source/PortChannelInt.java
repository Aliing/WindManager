package com.ah.be.config.create.source;

import com.ah.xml.be.config.PortChannelLoadBalanceModeValue;


public interface PortChannelInt {
	
	public boolean isConfigPortChannel();
	
	public int getPortChannelSize();
	
	public String getPortChannelName(int index);
	
	public PortChannelLoadBalanceModeValue getChannelModeValue();
			
}