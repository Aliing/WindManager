/**
 *@filename		SnmpConstance.java
 *@version
 *@author		Frank
 *@createtime	2007-9-11 10:36:13 AM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.snmp;

/**
 * @author Frank
 * @version V1.0.0.0
 */
public class SnmpConstance {
	public static final String AH_FAILURE_TRAP = "1.3.6.1.4.1.26928.1.1.1.1.1.1";
	public static final String AH_THRESHOLD_CROSSING_EVENT = "1.3.6.1.4.1.26928.1.1.1.1.1.2";
	public static final String AH_STATE_CHANGE_EVENT = "1.3.6.1.4.1.26928.1.1.1.1.1.3";
	public static final String AH_CONNECTION_CHANGE_EVENT = "1.3.6.1.4.1.26928.1.1.1.1.1.4";
	public static final String AH_IDP_STATION_EVENT = "1.3.6.1.4.1.26928.1.1.1.1.1.5";
	public static final String AH_CLIENTINFO_EVENT = "1.3.6.1.4.1.26928.1.1.1.1.1.6";
	public static final String AH_POE_EVENT = "1.3.6.1.4.1.26928.1.1.1.1.1.7";
	public static final String AH_CHANNELPOWERCHANGE_EVENT = "1.3.6.1.4.1.26928.1.1.1.1.1.8";
	public static final String CAPWAP_EVENT = "6";
	public static final String DOWNLOAD_CONFIG_EVENT = "7";// no e-mail
	public static final String CAPWAP_DTLS_HANDSHAKE_FAIL = "8";
	public static final String AH_TIME_BOMB_WARNING_EVENT = "9";
	public static final String AH_INTERFERENCE_ALERT_EVENT = "1.3.6.1.4.1.26928.1.1.1.1.1.10";
	public static final String AH_AIRSCREEN_REPORT_EVENT = "101";
	public static final String AH_VPN_SERVICE_EVENT = "102";
	public static final String AH_SSID_STATECHANGE_EVENT = "103";
	public static final String AH_BANDWIDTH_SENTINEL_EVENT = "1.3.6.1.4.1.26928.1.1.1.1.1.11";
	public static final String AH_INTERFACE_CLIENT_EVENT = "1.3.6.1.4.1.26928.1.1.1.1.1.12";
	public static final String AH_SECURITY_ALARM_TRAP = "1.3.6.1.4.1.26928.1.1.1.1.1.13";
															// notification
	public static final String KERNEL_DUMP_EVENT = "10";

	public static final String AH_AP_ID = "1.3.6.1.4.1.26928.1.1.1.1.2.1";
	public static final String AH_AP_NAME = "1.3.6.1.4.1.26928.1.1.1.1.2.2";
	public static final String AH_SEVERITY = "1.3.6.1.4.1.26928.1.1.1.1.2.3";
	public static final String AH_OBJECT_NAME = "1.3.6.1.4.1.26928.1.1.1.1.2.4";
	public static final String AH_PROBABLE_CAUSE = "1.3.6.1.4.1.26928.1.1.1.1.2.5";
	public static final String AH_CUR_VALUE = "1.3.6.1.4.1.26928.1.1.1.1.2.6";
	public static final String AH_THRESHOLD_HIGH = "1.3.6.1.4.1.26928.1.1.1.1.2.7";
	public static final String AH_THRESHOLD_LOW = "1.3.6.1.4.1.26928.1.1.1.1.2.8";
	public static final String AH_PREVIOUS_STATE = "1.3.6.1.4.1.26928.1.1.1.1.2.9";
	public static final String AH_CURRENT_STATE = "1.3.6.1.4.1.26928.1.1.1.1.2.10";
	public static final String AH_TRAP_DESC = "1.3.6.1.4.1.26928.1.1.1.1.2.11";
	public static final String AH_CODE = "1.3.6.1.4.1.26928.1.1.1.1.2.12";
	public static final String AH_IF_INDEX = "1.3.6.1.4.1.26928.1.1.1.1.2.13";
	public static final String AH_OBJECT_TYPE = "1.3.6.1.4.1.26928.1.1.1.1.2.14";
	public static final String AH_REMOTE_ID = "1.3.6.1.4.1.26928.1.1.1.1.2.15";
	public static final String AH_IDP_TYPE = "1.3.6.1.4.1.26928.1.1.1.1.2.16";
	public static final String AH_IDP_CHANNEL = "1.3.6.1.4.1.26928.1.1.1.1.2.17";
	public static final String AH_IDP_RSSI = "1.3.6.1.4.1.26928.1.1.1.1.2.18";
	public static final String AH_IDP_COMPLIANCE = "1.3.6.1.4.1.26928.1.1.1.1.2.19";
	public static final String AH_SSID = "1.3.6.1.4.1.26928.1.1.1.1.2.20";
	public static final String AH_STATION_TYPE = "1.3.6.1.4.1.26928.1.1.1.1.2.21";
	public static final String AH_IDP_STATION_DATA = "1.3.6.1.4.1.26928.1.1.1.1.2.22";
	public static final String AH_REMOVED = "1.3.6.1.4.1.26928.1.1.1.1.2.23";
	public static final String AH_CLIENT_MAC = "1.3.6.1.4.1.26928.1.1.1.1.2.24";
	public static final String AH_CLIENT_IP = "1.3.6.1.4.1.26928.1.1.1.1.2.25";
	public static final String AH_CLIENT_HOSTNAME = "1.3.6.1.4.1.26928.1.1.1.1.2.26";
	public static final String AH_CLIENT_USERNAME = "1.3.6.1.4.1.26928.1.1.1.1.2.27";
	public static final String AH_POWER_SRC = "1.3.6.1.4.1.26928.1.1.1.1.2.28";
	public static final String AH_POEETH0ON = "1.3.6.1.4.1.26928.1.1.1.1.2.29";
	public static final String AH_POEETH0PWR = "1.3.6.1.4.1.26928.1.1.1.1.2.30";
	public static final String AH_POEETH1ON = "1.3.6.1.4.1.26928.1.1.1.1.2.31";
	public static final String AH_POEETH1PWR = "1.3.6.1.4.1.26928.1.1.1.1.2.32";
	public static final String AH_RADIOCHANNEL = "1.3.6.1.4.1.26928.1.1.1.1.2.33";
	public static final String AH_RADIOTXPOWER = "1.3.6.1.4.1.26928.1.1.1.1.2.34";
	public static final String AH_CLIENT_AUTH_METHOD = "1.3.6.1.4.1.26928.1.1.1.1.2.35";
	public static final String AH_CLIENT_ENCRYPTION_METHOD = "1.3.6.1.4.1.26928.1.1.1.1.2.36";
	public static final String AH_CLIENT_MAC_PROTOCOL = "1.3.6.1.4.1.26928.1.1.1.1.2.37";
	public static final String AH_CLIENT_VLAN = "1.3.6.1.4.1.26928.1.1.1.1.2.38";
	public static final String AH_CLIENT_USER_PROFID = "1.3.6.1.4.1.26928.1.1.1.1.2.39";
	public static final String AH_CLIENT_CHANNEL = "1.3.6.1.4.1.26928.1.1.1.1.2.40";
	public static final String AH_CLIENT_CWPUsed = "1.3.6.1.4.1.26928.1.1.1.1.2.41";
	public static final String AH_CLIENT_BSSID = "1.3.6.1.4.1.26928.1.1.1.1.2.42";
	public static final String AH_POEETH0_MAX_SPEED = "1.3.6.1.4.1.26928.1.1.1.1.2.43";
	public static final String AH_POEETH1_MAX_SPEED = "1.3.6.1.4.1.26928.1.1.1.1.2.44";
	public static final String AH_POEWIFI0_SETTING = "1.3.6.1.4.1.26928.1.1.1.1.2.45";
	public static final String AH_POEWIFI1_SETTING = "1.3.6.1.4.1.26928.1.1.1.1.2.46";
	public static final String AH_POEWIFI2_SETTING = "1.3.6.1.4.1.26928.1.1.1.1.2.47";
	public static final String AH_ASSOCIATION_TIME = "1.3.6.1.4.1.26928.1.1.1.1.2.48";



	public static final int TRAP_STATE_UP_INDEX = 1;
	public static final int TRAP_STATE_DOWN_INDEX = 2;

	public static final int TRAP_OBJECT_TYPE_CLIENT_INDEX = 1;
	public static final int TRAP_OBJECT_TYPE_NEIGHBOR_INDEX = 2;

	public static final int TRAP_STATE_CAUSE_CLEAR_INDEX = 0;
	public static final int TRAP_STATE_CAUSE_UNKNOWN_INDEX = 1;
	public static final int TRAP_STATE_CAUSE_FLASH_FAILURE_INDEX = 2;
	public static final int TRAP_STATE_CAUSE_FAN_FAILURE_INDEX = 3;
	public static final int TRAP_STATE_CAUSE_POWER_SUPPLY_FAILURE_INDEX = 4;
	public static final int TRAP_STATE_CAUSE_SOFTWARE_UPGRADE_FAILURE_INDEX = 5;
	public static final int TRAP_STATE_CAUSE_RADIO_FAILURE_INDEX = 6;

	public static final int TRAP_ALERT_SEVERITY_CLEAR_INDEX = 1;
	public static final int TRAP_ALERT_SEVERITY_INFO_INDEX = 2;
	public static final int TRAP_ALERT_SEVERITY_MINOR_INDEX = 3;
	public static final int TRAP_ALERT_SEVERITY_MAJOR_INDEX = 4;
	public static final int TRAP_ALERT_SEVERITY_CRITICAL_INDEX = 5;

	public static final int AH_TRAP_CODE_LINK_UP = 0;
	public static final int AH_TRAP_CODE_LINK_DOWN = 1;

	public static final String TRAP_ALERT_SEVERITY_CLEAR_NAME = "Clear";
	public static final String TRAP_ALERT_SEVERITY_INFO_NAME = "Info";
	public static final String TRAP_ALERT_SEVERITY_MINOR_NAME = "Minor";
	public static final String TRAP_ALERT_SEVERITY_MAJOR_NAME = "Major";
	public static final String TRAP_ALERT_SEVERITY_CRITICAL_NAME = "Critical";

	public static final String TRAP_STATE_UP_NAME = "Up";
	public static final String TRAP_STATE_DOWN_NAME = "Down";

	public static final String TRAP_OBJECT_TYPE_CLIENT_NAME = "Client";
	public static final String TRAP_OBJECT_TYPE_NEIGHBOR_NAME = "Neighbor";

	public static final String TRAP_STATE_CAUSE_CLEAR_NAME = "Clear";
	public static final String TRAP_STATE_CAUSE_UNKNOWN_NAME = "Unknown";
	public static final String TRAP_STATE_CAUSE_FLASH_FAILURE_NAME = "Flash Failure";
	public static final String TRAP_STATE_CAUSE_FAN_FAILURE_NAME = "Fan Failure";
	public static final String TRAP_STATE_CAUSE_POWER_SUPPLY_FAILURE_NAME = "Power Supply Failure";
	public static final String TRAP_STATE_CAUSE_SOFTWARE_UPGRADE_FAILURE_NAME = "Software Upgrade Failure";
	public static final String TRAP_STATE_CAUSE_RADIO_FAILURE_NAME = "Radio Failure";

	public static final int TRAP_IDP_STATION_DATA_OPEN_POLICY_INDEX = 1;
	public static final int TRAP_IDP_STATION_DATA_WEP_POLICY_INDEX = 2;
	public static final int TRAP_IDP_STATION_DATA_WPA_POLICY_INDEX = 4;
	public static final int TRAP_IDP_STATION_DATA_WMM_POLICY_INDEX = 8;
	public static final int TRAP_IDP_STATION_DATA_OUI_POLICY_INDEX = 16;
	public static final int TRAP_IDP_STATION_DATA_SSID_POLICY_INDEX = 32;
	public static final int TRAP_IDP_STATION_DATA_SHORT_P_POLICY_INDEX = 64;
	public static final int TRAP_IDP_STATION_DATA_SHORT_B_POLICY_INDEX = 128;
	public static final int TRAP_IDP_STATION_DATA_AD_HOC_POLICY_INDEX = 256;

	public static final int TRAP_IDP_REMOVED_TRUE_INDEX = 1;
	public static final int TRAP_IDP_REMOVED_FALSE_INDEX = 0;

	public static final int TRAP_IDP_STATION_TYPE_AP_INDEX = 1;
	public static final int TRAP_IDP_STATION_TYPE_CLIENT_INDEX = 2;

	public static final int TRAP_IDP_TYPE_ROGUE_INDEX = 1;
	public static final int TRAP_IDP_TYPE_VALID_INDEX = 2;
	public static final int TRAP_IDP_TYPE_EXTERNAL_INDEX = 3;

	public static final String TRAP_IDP_STATION_DATA_OPEN_POLICY_NAME = "Open";
	public static final String TRAP_IDP_STATION_DATA_WEP_POLICY_NAME = "WEP";
	public static final String TRAP_IDP_STATION_DATA_WPA_POLICY_NAME = "WPA";
	public static final String TRAP_IDP_STATION_DATA_WMM_POLICY_NAME = "WMM";
	public static final String TRAP_IDP_STATION_DATA_OUI_POLICY_NAME = "OUI";
	public static final String TRAP_IDP_STATION_DATA_SSID_POLICY_NAME = "SSID";
	public static final String TRAP_IDP_STATION_DATA_SHORT_P_POLICY_NAME = "Short Preamble";
	public static final String TRAP_IDP_STATION_DATA_SHORT_B_POLICY_NAME = "Short Beacon";
	public static final String TRAP_IDP_STATION_DATA_AD_HOC_POLICY_NAME = "AD HOC";

	public static final String TRAP_IDP_REMOVED_TRUE_NAME = "Missed";
	public static final String TRAP_IDP_REMOVED_FALSE_NAME = "Discovered";

	public static final String TRAP_IDP_STATION_TYPE_AP_NAME = "Station AP";
	public static final String TRAP_IDP_STATION_TYPE_CLIENT_NAME = "Station Client";

	public static final String TRAP_IDP_TYPE_ROGUE_NAME = "Rogue";
	public static final String TRAP_IDP_TYPE_VALID_NAME = "Valid";
	public static final String TRAP_IDP_TYPE_EXTERNAL_NAME = "External";

	// snmp index type
	public static final int SNMP_INDEX_TYPE_INT = 0;
	public static final int SNMP_INDEX_TYPE_OTECT = 1;
	public static final int SNMP_INDEX_TYPE_IP = 2;
	public static final int SNMP_INDEX_TYPE_OTECT_HEX = 3;

}