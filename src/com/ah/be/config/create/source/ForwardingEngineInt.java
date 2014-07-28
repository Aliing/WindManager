package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;
import com.ah.xml.be.config.ForwardingActionValue;

/**
 * @author zhang
 * @version 2007-12-20 10:35:29
 */

public interface ForwardingEngineInt {
	
	public String getMgmtServiceGuiName();
	
	public String getMgmtServiceName();

	public String getApVersion();
	
	public boolean isConfigForwardingEngine() throws CreateXMLException;
	
	public String getUpdateTime();
	
	public boolean isEnableInterSsidFlood();

	public boolean isEnableProxyArp();
	
	public int getIpSession();
	
	public int getMacSession();
	
	public boolean isConfigIpSession();
	
	public boolean isConfigMacSession();
	
	public boolean isEnableLogDroppedPackets();
	
	public boolean isEnableLogSessionsEnable();
	
	public boolean isEnableDropFragmentedPackets();
	
	public boolean isEnableDropManagementTraffic();
	
	public boolean isConfigTunnel();
	
	public boolean isEnableTcpMss();
	
	public boolean isConfigTcpMssThresholdSize();
	
	public int getTcpMssThresholdSize();
	
	public boolean isConfigStaticRule();
	
	public int getStaticRuleSize();
	
	public String getStaticRuleName(int index);
	
	public ForwardingActionValue getRuleActionValue(int index);
	
	public String getRuleInifValue(int index);
	
	public boolean isConfigRuleSrcOui(int index);
	
	public String getRuleSrcValue(int index);
	
	public String getRuleDstMacValue(int index);
	
	public String getRuleTxMacValue(int index);
	
	public String getRuleOutIfValue(int index);
	
	public String getRuleRxMacValue(int index);
	
	public int getMultiNativeVlanSize();
	
	public String getMultiNativeVlanName(int index);
	
	public boolean isConfigForwardAllowAll();
	
	public boolean isConfigForwardBlockAll();
	
	public int getForwardExceptSize();
	
	public String getForwardExceptValue(int index);
	
	public boolean isConfigForwardsyncVlan();
}
