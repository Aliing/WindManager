package com.ah.be.config.create.source;

import java.util.List;

import com.ah.xml.be.config.RoutingProtocolTypeValue;

public interface RoutingProfileInt {
	
	public static enum AadvertiseType{
		eth0, eth1, both, none
	}
	
	public static enum RoutingPolicyType{
		primary, secondary, both
	}
	
	public static final String RULE_PREFIX = "rule_";

	public boolean isConfigRouting();
	
	public boolean isConfigRoutingProtocol();
	
	public boolean isConfigRoutingRequest();
	
	public int getRoutingSubNetworkSize();
	
	public boolean isConfigRoutingSubNetwork(int index);
	
	public String getRoutingSubNetworkValue(int index);
	
	public boolean isRoutingSubNetworkTunnel(int index);
	
	public boolean isProtocolEnable();
	
	public boolean isConfigProtocolType();
	
	public boolean isConfigProtocolRipv2();
	
	public boolean isConfigProtocolOspf();
	
	public boolean isConfigProtocolBgp();
	
	public boolean isEnableRouteRequest();
	
	public int getRouteInterval();
	
	public RoutingProtocolTypeValue getProtocolTypeValue();
	
	public AadvertiseType getAadvertise();
	
	public boolean isConfigArea();
	
	public String getArea();
	
	public boolean isConfigRouterId();
	
	public String getRouterId();
	
	public boolean isConfigMd5Key();
	
	public String getMd5Key();
	
	public boolean isConfigKeepalive();
	
	public int getKeepaliveValue();
	
	public boolean isConfigSystemNumber();
	
	public int getSystemNumber();
	
	public int getNeighborSize();
	
	public String getNeighborValue(int index);
	
    int getPolicyRuleSize();

    int getSourceType(int ruleIndex);

    String getSourceName(int ruleIndex);

    List<String> getSourceValue(int ruleIndex);

    int getDestinationType(int ruleIndex);

//    String getDestinationName(int ruleIndex);

    String getDestinationValue(int ruleIndex);

    String getOut(int ruleIndex, int outIndex);

}
