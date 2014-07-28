package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;

/**
 * 
 * @author zhang
 *
 */
public interface MobilityPolicyProfileInt {
	
	public boolean isConfigMobilityPolicy();
	
	public String getMobilityPolicyGuiName();
	
	public String getApVersion();

	public String getUpdateTime();
	
	public String getMobilityPolicyName();
	
	public boolean isConfigureDnxp() throws CreateXMLException;
	
	public boolean isMgtIpInFrom() throws CreateXMLException;
	
	public boolean isMgtIpInTo() throws CreateXMLException;
	
	public boolean isConfigureInxp() throws CreateXMLException;
	
//	public boolean isEnableDnxpNomadicRoaming();
	
	public int getMobilityPolicyFromSize();
	
	public String getMobInxpToAddress() throws CreateXMLException;
	
	public String getMobInxpToPassword();
	
	public String getMobInxpFromAddress(int index) throws CreateXMLException;
	
	public String getMobInxpFromPassword(int index);
	
	public boolean isConfigUnroamThreshold() throws CreateXMLException;
	
	public String getUnroamThresholdValue();
	
	public boolean isConfigNomadicRoaming();
}
