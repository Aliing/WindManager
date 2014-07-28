package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;
import com.ah.xml.be.config.AhPermitDenyValue;
import com.ah.xml.be.config.UpL3TunnelActionValue;

/**
 * 
 * @author zhang
 *
 */
public interface UserProfileInt {
	
//	public static final String POLICY_ACTION_DENY = "deny";
//	public static final String POLICY_ACTION_PERMIT = "permit";
	
	public String getUserProfileGuiName();
	
	public String getApVersion();
	
	public boolean isConfigUserProfile();

	public String getUserProfileName();
	
	public String getUpdateTime();
	
//	public boolean isConfigCr() throws CreateXMLException;
	
//	public boolean isConfigGroupId() throws CreateXMLException;
	
	public int getUserProfileGroupId();
	
	public boolean isConfigureQosPolicy();
	
	public String getQosPolicyName();
	
	public boolean isConfigureVlan();
	
	public int getVlanId() throws CreateXMLException;
	
	public boolean isConfigureMobilityPolicy();
	
	public String getMobilityPolicyName();
	
	public int getUserProfileAttributeSize() throws CreateXMLException;
	
	public String getUserProfileAttributeNextName(int i);
	
	public int getUserProfileScheduleSize();
	
	public String getUserProfileScheduleDenyMode();
	
	public String getUserProfileScheduleName();
	
	public AhPermitDenyValue getIpPolicyActionType();
	
	public AhPermitDenyValue getMacPolicyActionType();
	
	public boolean isConfigureIpOrMacPolicy();
	
	public boolean isConfigureIpPolicy();
	
	public String getIpFromAirPolicyName();
	
	public String getIpToAirPolicyName();
	
	public boolean isConfigureMacPolicy();
	
	public String getMacFromAirPolicyName();
	
	public String getMacToAirPolicyName();
	
//	public boolean isConfigCac();
	
	public int getAirTime();
	
	public boolean isConfigCacSharaTime();
	
	public boolean isConfigTunnelPolicy();
	
	public String getTunnelPolicyName();
	
	public boolean isConfigAirScreen();
	
	public int getAirScreenSize();
	
	public String getAirScreenRuleName(int index);
	
	public boolean isBandwidthEnable();
	
	public int getBandwidthValue();
	
	public boolean isConfigActionLog();
	
	public boolean isConfigActionBoost();
	
	public String getBeforeValue(int index, int allSize);
	
	public boolean isConfigBefore(int index);
	
	public boolean isConfigL3TunnelAction();
	
	public UpL3TunnelActionValue getL3TunnelAction();
	
	public boolean isConfigQosMap();
	
	public boolean isConfigQosMap8021p();
	
	public boolean isConfigQosMapDiffserv();
	
	public String getQosMapName();
	
}
