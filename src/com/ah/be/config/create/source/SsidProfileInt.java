package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;
import com.ah.xml.be.config.SsidMulticastConversionToUnicastValue;


/**
 * @author jzhang
 * @version V1.0.0.0
 */

public interface SsidProfileInt {
	
	public static final String MANAGE_SNMP="snmp";
	public static final String MANAGE_SSH="ssh";
	public static final String MANAGE_TELNET="telnet";
	public static final String MANAGE_PING="ping";

	public static final String SCREENING_ADDRESS_SWEEP="address-sweep";
	public static final String SCREENING_ICMP_FLOOD="icmp-flood";
	public static final String SCREENING_IP_SPOOF="ip-spoof";
	public static final String SCREENING_PORT_SCAN="port-scan";
	public static final String SCREENING_RADIUS_ATTACK="radius-attack";
	public static final String SCREENING_SYN_FLOOD="syn-flood";
	public static final String SCREENING_UDP_FLOOD="udp-flood";
	public static final String SCREENING_ARP_FLOOD="arp-flood";
	public static final String SCREENING_TCP_SYN_CHECK = "tcp-syn-check";
	
	public static final String SCREENING_ACTION_ALARM="alarm";
	public static final String SCREENING_ACTION_BAN="ban";
	public static final String SCREENING_ACTION_BAN_FOREVER="ban-forever";
	public static final String SCREENING_ACTION_DISCONNECT="disconnect";
	public static final String SCREENING_ACTION_DROP="drop";
	
	public static final int WMM_AC_BESTEFFORT=0;
	public static final int WMM_AC_BACKGROUND=1;
	public static final int WMM_AC_VIDEO=2;
	public static final int WMM_AC_VOICE=3;
	
	public static enum FrameType{
		assoc_req("assoc-req"),
		assoc_resp("assoc-resp"),
		auth("auth"),
		deauth("deauth"),
		disassoc("disassoc"),
		eapol("eapol"),
		probe_req("probe-req"),
		probe_resp("probe-resp");
		
		private String value;
		
		private FrameType(String value){
			this.value = value;
		}
		
		public String value(){
			return this.value;
		}
	}
	
	public static enum RateSet11aOr11g {
		ONE("1"), TWO("2"), FIVE("5.5"),SIX("6"), NINE("9"), ELEVEN("11"), TWELVE("12"),
		EIGHTEEN("18"), TWENTY_FOUR("24"), THIRTY_SIX("36"), FORTY_EIGHT("48"), FIFTY_FOUR("54"), BASIC("-basic");
		
		private String value;
		
		RateSet11aOr11g(String value){
			this.value = value;
		}
		
		public String getValue(){
			return this.value;
		}
	}
	
	public static enum UserProfileDenyAction{
		ban, banForever, disconnect
	}
	
	public static enum AuthMethodType{
		pap, chap, msChapV2
	}
	
	public static short WALL_GARDEN_IPADDRESS = 0;
	public static short WALL_GARDEN_HOSTNAME = 1;
	
	public String getSsidGuiKey();
	
	public String getWlanGuiKey();
	
	public String getWlanName();
	
	public String getIpDosGuiKey();
	
	public String getIpDosName();
	
	public String getServiceFilterGuikey();
	
	public String getServiceFilterName();
	
	public String getRadiusAssGuiName();
	
	public String getRadiusAssName();
	
	public String getApVersion();

	public boolean isConfigureSsid();
	
	public boolean isYesDefault();
	
	public boolean isNoDefault();
	
	public String getSsidName();
	
	public String getSsidUpdateTime();
	
	public int getDtimPeriod();
	
	public int getFragThreshold();
	
	public boolean isHideSsidEnable();
	
	public boolean isIgnoreBroadcastProbeEnable();
	
	public int getRtsThreshold();
	
	public int getSsidScheduleSize();
	
	public String getSsidScheduleNextName();
	
//	public boolean isDnsServerEnable();
	
	public boolean isSsidManageEnable(String manageType);
	
//	public boolean isConfigureMacFilter();
	
	public String getMacFilter();
	
	public boolean isConfigureScreenElement(String screenType);
	
	public int getScreenThresholdValue(String screenType);
	
	public boolean isConfigureActionWithScreen(String screenType, String actionType);
	
	public int getScreenActionValue(String screenType, String actionType);
	
	public boolean isEnableWithStationType(FrameType stationType);
	
	public int getAlarmValueWithStationType(FrameType stationType);
	
	public String getBanValueWithStationType(FrameType stationType);
	
	public int getThresholdValueWithStationType(FrameType stationType);
	
	public boolean isEnableWithSsidType(FrameType stationType);
	
	public int getAlarmValueWithSsidType(FrameType stationType);
	
	public int getThresholdValueWithSsidType(FrameType stationType);
	
	public boolean isConfigureQosClass();
	
	public String getQosClassifierName();
	
	public boolean isConfigureQosMarker();
	
//	public boolean isConfigUapsd();
	
	public boolean isEnableUapsd();
	
	public String getQosMarkerName();
	
	public boolean isConfigureScreen();
	
	public boolean isConfigureWlan();
	
	public boolean isConfigureStationLevel();
	
	public boolean isConfigureSsidLevel();
	
	public boolean isEnableSsidWmm();
	
	public boolean isEnableSsidWnm();
	
	public boolean isEnableScreenTcpSynCheck() throws Exception;
	
	public boolean isConfig11aRateSet();
	
	public boolean isConfig11gRateSet();
	
	public boolean isConfig11nRateSet();
	
	public boolean isConfig11ngRateSet() throws CreateXMLException;
	
	public boolean isConfig11acRateSet();
	
	public String get11aRateSetValue();
	
	public String get11gRateSetValue();
	
	public String get11nRateSetValue();
	
	public String getExpand_11nRateSetValue() throws CreateXMLException;
	
	public String get11acRageSets();
	
	public boolean isEnableInternalServers();
	
	public int getRoamingUpdateInterval();
	
	public int getRoamingAgeout();
	
	public boolean isConfigInterStationTraffic();
	
	public boolean isEnableInterStationTraffic();
	
	public int getMaxClient();
	
	public int getPskUserGroupSize();
	
	public String getPskUserGroupName(int index);
	
	public boolean isEnableLegacy();
	
	public boolean isConfigAirScreen();
	
	public int getAirScreenSize();
	
	public String getAirScreenRuleName(int index);
	
	public int getClientAgeOut();
	
	public String getSecurityObjectName();
	
	public int getCuThresholdValue();
	
	public int getMemberThresholdValue();
	
	public SsidMulticastConversionToUnicastValue getMulticastConversionValue();
	
	public boolean isEnableRrm();
	
	public boolean isEnableAdmctl();
	
	public boolean isConfigAcNumber(int index);
	
	public int getEgressMulticastThreshold();
	
	public int getEgressMulticastInterval();
	
	public boolean isEnableConnectionAlarm();
	
	public boolean isConfigPriority();
	
	public int getPriority();
}
