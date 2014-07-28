package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;

/**
 * 
 * @author zhang
 *
 */
public interface MacPolicyProfileInt {
	
	public enum macPolicyAction{
		permit, deny
	}
	
	public String getMacPolicyGuiName();
	
	public String getApVersion();

	public String getUpdateTime();
	
	public String getMacPolicyName();
	
	public int getMacPolicyIdSize();
	
	public int getMacPolicyIdName(int index);
	
	public String getPolicyFromValue(int index) throws Exception;
	
	public String getPolicyToValue(int index) throws Exception;
	
	public boolean isConfigPolicyAction(int index, IpPolicyProfileInt.IpPolicyActionValue actionType);
	
	public boolean isConfigPolicyLog(int index) throws CreateXMLException;
	
	public boolean isConfigPolicyLogType(int index, IpPolicyProfileInt.IpPolicyLog logType);
	
	public boolean isConfigBefore(int index);

	public int getPolicyBeforeIdValue(int index);
	
	public String getBeforeValue(int index);
}
