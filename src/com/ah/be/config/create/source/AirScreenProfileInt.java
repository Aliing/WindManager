package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;
import com.ah.xml.be.config.AirscreenAuthModeWpa;
import com.ah.xml.be.config.ReconnectionConnectionCaseValue;

/**
 * @author zhang
 * @version 2009-6-5 10:45:58
 */

public interface AirScreenProfileInt {

	public int getSourceSize();
	
	public int getActionSize();
	
	public int getBehaviorSize();
	
	public int getRuleSize();
	
	public String getActionName(int index);
	
	public boolean isConfigActionDeAuth(int index);
	
	public boolean isConfigActionLocalBan(int index);
	
	public boolean isConfigActionReportToHm(int index);
	
	public int getLocalBanInterval(int index);
	
	public String getSourceName(int index);
	
	public String getSourceOui(int index) throws CreateXMLException;
	
	public String getSourceRssi(int index);
	
	public boolean isConfigSourceOpen(int index);
	
	public boolean isConfigSourceWep(int index);
	
	public boolean isConfigSourceWepOpen(int index);
	
	public boolean isConfigSourceWepShared(int index);
	
	public boolean isConfigSourceDynamicWep(int index);
	
	public boolean isConfigSourceWpa(int index);
	
	public boolean isConfigSourceWpaPsk(int index);
	
	public boolean isConfigSourceWpa8021X(int index);
	
	public boolean isConfigSourceWpa2Psk(int index);
	
	public boolean isConfigSourceWpa28021X(int index);
	
	public AirscreenAuthModeWpa getSourceEncryptionMode(int index);
	
	public String getBehaviorName(int index);
	
	public ReconnectionConnectionCaseValue getBehaviorConnectionType(int index);
	
	public int getBehaviorInterval(int index);
	
	public int getBehaviorThreshold(int index);
	
	public String getAirRuleName(int ruleIndex);
	
	public boolean isConfigRuleSource(int ruleIndex);
	
	public String getAirRuleSourceName(int ruleIndex);
	
	public int getAirRuleActionSize(int ruleIndex);
	
	public String getAirRuleActionName(int ruleIndex, int actionIndex);
	
	public int getAirRuleBehaviorSize(int ruleIndex);
	
	public String getAirRuleBehaviorName(int ruleIndex, int behaviorIndex);
}
