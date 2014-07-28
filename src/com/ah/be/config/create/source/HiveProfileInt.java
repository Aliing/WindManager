package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;


/**
 * 
 * @author zhang
 *
 */
public interface HiveProfileInt {
	
	public static final String MANAGE_SNMP = "SNMP";
	public static final String MANAGE_SSH = "SSH";
	public static final String MANAGE_TELNET = "Telnet";
	public static final String MANAGE_PING = "ping";
	
	public enum ConnectingThreshold{
		high, low, medium
	}
	
	public String getHiveGuiName();
	
	public String getServiceFilterGuiName();
	
	public String getServiceFilterName();
	
	public String getApVersion();
	
	public boolean isConfigHiveProfile();
	
	public String getHiveName();
	
	public String getHiveUpdateTime();
	
	public String getHiveSecurityUpdateTime();
	
	public String getHiveWlanUpdateTime();
	
	public String getHiveLevelUpdateTime();
	
	public String getStationLevelUpdateTime();
	
	public int getHiveFragThreshold();
	
//	public boolean isConfigNativeVlan() throws CreateXMLException;
	
	public int getNativeVlanId() throws CreateXMLException;
	
	public int getRtsThreshold();
	
	public boolean isConfigurePassword();
	
	public String getPassword();
	
	public boolean isConfigureManage();
	
//	public boolean isConfigureSecurity();
	
	public boolean isEnableManageWithType(String manageType);
	
//	public boolean isConfigureMacFilter();
	
	public String getMacFilterName();
	
	public boolean isConfigureWlan();
	
	public boolean isConfigureHiveLevel();
	
	public boolean isConfigureStationLevel();
	
	public boolean isEnableHiveLevelWithType(SsidProfileInt.FrameType hiveType);
	
	public int getThresholdHiveLevelWithType(SsidProfileInt.FrameType hiveType);
	
	public int getAlarmHiveLevelWithType(SsidProfileInt.FrameType hiveType);
	
	public boolean isEnableHiveStationWithType(SsidProfileInt.FrameType hiveType);
	
	public int getThresholdHiveStationWithType(SsidProfileInt.FrameType hiveType);
	
	public int getAlarmHiveStationWithType(SsidProfileInt.FrameType hiveType);
	
	public String getBanHiveStationWithType(SsidProfileInt.FrameType hiveType);
	
	public boolean isConfigNeighbor();
	
	public String getConnectingThreshold();
	
	public int getPollingInterval();
	
	public boolean isConfigWlanIdp();
	
	public int getMaxMitigatorNum();
	
	public boolean isMitigationModeAuto();
	
	public boolean isMitigationModeSemiAuto();
	
	public boolean isMitigationModeManual();
	
	public boolean isConfigIdpInNetAp();
	
	public boolean isEnableIdpInNetAp();

}
