package com.ah.be.config.create.source;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.MacFilter;

import com.ah.be.config.create.CreateXMLException;
import com.ah.xml.be.config.AhPermitDenyValue;

/**
 * 
 * @author zhang
 *
 */
public interface SecurityProfileInt {
	
	public static final String ENCRYPTION_TYPE_OPEN = "open";
	public static final String ENCRYPTION_TYPE_WPA = "WPA";
	public static final String ENCRYPTION_TYPE_WEP = "wep";
	
	public static final short MAC_FILTER_ACTION_PERMIT = MacFilter.FILTER_ACTION_PERMIT;
	public static final short MAC_FILTER_ACTION_DENY = MacFilter.FILTER_ACTION_DENY;
	
	public String getMacFilterGuiName();
	
	public String getIdsGuiName();
	
	public String getApVersion();
	
	public boolean isConfigureSecurity();
	
	public String getUpdateTime();
	
	public String getWlanIdpUpdateTime();
	
	public int getMacFilterBindSize();
	
	public String getMacFilterName(int index);
	
	public int getMacFilterAddressSize(int index);
	
	public int getMacFilterOuiSize(int i);
	
	public HiveAp getHiveAp();
	
	public String getMacAddress(int i, int j) throws CreateXMLException;
	
	public String getMacOui(int i, int j) throws CreateXMLException;
	
	public boolean isConfigureWlanIdp();
	
	public String getWlanIdpProfileName();
	
	public boolean isEnableWlanIdpAdhoc();
	
//	public boolean isConfigureApDetection();
	
	public boolean isEnableShortBeacon();
	
	public boolean isEnableShortPreamble();
	
	public boolean isEnableWmm();
	
	public boolean isEnableConnected();
	
	public int getConnectedVlanSize();
	
	public int getConnectedVlanName(int index) throws Exception ;
	
	public boolean isEnableApOui();
	
	public int getApOuiSize();
	
	public String getApOuiAddress(int index) throws Exception;
	
	public boolean isEnableApPolicySsid();
	
	public int getApPolicySsidSize();
	
	public String getApPolicySsidName(int index);
	
	public boolean isEnableEncryption(int index);
	
	public boolean isConfigureEncryptionType(int index, String type);

	public boolean isAddressActionIsPermit(int i, int j);
	
	public boolean isOuiActionIsPermit(int i, int j);
	
	public AhPermitDenyValue getDefaultActionValue(int i);
	
	public boolean isEnableApPolicy();
	
	public int getIDSPolicyPeriod();
	
	public int getIDSPolicyDuration();
	
	public int getIDSPolicyQuietTime();
	
	public boolean isStaReportEnable();
	
	public int getStaReportDuration();
	
	public int getStaReportInterval();
	
	public int getStaReportAgeout();
	
	public int getMitigateDeauthTime();
	
	public int getStaReportAgeTime();
	
}
