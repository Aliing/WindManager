package com.ah.be.fault;

import com.ah.be.common.NmsUtil;
import com.ah.util.EnumItem;

public class BeFaultConst {

	public static final String[]	TRAP_SEND_MAIL_TYPEX		= {
			"IDP", 						// 0
			"Client_Management",		// 1
			"AMRP_Management", 			// 2
			"Hardware_CPU", 			// 3
			"Hardware_Memory", 			// 4
			"Auth", 					// 5
			"Interface", 				// 6
			"L2DoS",					// 7
			"Hardware_Radio", 			// 8
			"CAPWAP", 					// 9
			"Configuration", 			// 10
			"Screen", 					// 11
			"License Expiration", 		// 12
			"Kernel",					// 13
			"Air Screen", 				// 14
			"VPN Service",				// 15
			"User Database",		    // 16
			"TCA Alarm",                // 17
			"Client",                   // 18
			"Hivemanager",              // 19
			"System"                   // 20
			//"ClientRegister"			//21
	};

	public static final String[] TRAP_ALERT_SEVERITY_TYPEX = {
		"Clear",						// 1
		"Info",							// 2
		"Minor",						// 3
		"Major",						// 4
		"Critical"						// 5
	};

	public static final int ALERT_SERVERITY_CLEAR = 1;
	public static final int ALERT_SERVERITY_INFO = 2;
	public static final int ALERT_SERVERITY_MINOR = 3;
	public static final int ALERT_SERVERITY_MAJOR = 4;
	public static final int ALERT_SERVERITY_CRITICAL = 5;

	public static final EnumItem[] TRAP_CODE_TYPEX ={
		new EnumItem(0, "Link Up"),
		new EnumItem(1, "Link Down"),
	};

	public static final EnumItem TRAP_DESC_CAPWAP_LINK_DOWNX = new EnumItem(1, "The CAPWAP connection with " +
			NmsUtil.getOEMCustomer().getNmsName() + " was lost.");
	public static final EnumItem TRAP_DESC_CAPWAP_LINK_UPX = new EnumItem(2, "The CAPWAP connection with " +
			NmsUtil.getOEMCustomer().getNmsName() + " was restored.");

	public static final EnumItem[] TRAP_PROBABLE_CAUSE_TYPEX ={
		new EnumItem(0, "ahClear"),
		new EnumItem(1, "ahUnknown"),
		new EnumItem(2, "ahFlashFailure"),
		new EnumItem(3, "ahFanFailure"),
		new EnumItem(4, "ahPowerSupplyFailure"),
		new EnumItem(5, "ahSoftwareUpgradeFailure"),
		new EnumItem(6, "ahRadioFailure")
	};

	public static final EnumItem[] TRAP_LDAP_ALERT_TYPEX ={
		new EnumItem(1, "net join"),
		new EnumItem(2, "bind dn")
	};

	// alarm type
	public static final short		ALARM_TYPE_SNMP_RADIO			= 1;
	public static final short		ALARM_TYPE_SNMP_CONFIG			= 2;
	public static final short		ALARM_TYPE_CAPWAP				= 6;
	public static final short		ALARM_TYPE_BOMB_WARNING			= 9;
	public static final short		ALARM_TYPE_DOS					= 10;
	public static final short		ALARM_TYPE_AD					= 11;
	public static final short       ALARM_TYPE_CLIENT               = 12;
	public static final short       ALARM_TYPE_SYSTEM               = 13;

	public static final short      	ALARM_TYPY_HM                   =40;// HM Alarm
	public static final short		ALARM_TYPE_HM_MAIN				= 20;//from HiveManager
	public static final short		ALARM_TYPE_HM_SLAVE				= 30;//from Slave HiveManager

	// alarm subtype(0:special code , normally start from 1~)
	public static final short 		ALARM_SUBTYPE_FAILURE_INTERFERENCEALERT			= 8;
	public static final short 		ALARM_SUBTYPE_FAILURE_KERNELDUMP 				= 0;
	public static final short 		ALARM_SUBTYPE_FAILURE_AHCLEAR 					= 0;
	public static final short 		ALARM_SUBTYPE_FAILURE_AHUNKNOWN 				= 1;
	public static final short 		ALARM_SUBTYPE_FAILURE_AHFLASHFAILURE 			= 2;
	public static final short 		ALARM_SUBTYPE_FAILURE_AHFANFAILURE 				= 3;
	public static final short 		ALARM_SUBTYPE_FAILURE_AHPOWERSUPPLYFAILURE 		= 4;
	public static final short 		ALARM_SUBTYPE_FAILURE_AHSOFTWAREUPGRADEFAILURE 	= 5;
	public static final short 		ALARM_SUBTYPE_FAILURE_AHRADIOFAILURE 			= 6;
	public static final short 		ALARM_SUBTYPE_FAILURE_AHCONFFAILURE 			= 7;

	public static final short		ALARM_SUBTYPE_CAPWAP_LINK 			= 1;
	public static final short		ALARM_SUBTYPE_CAPWAP_PASSPHRASE 	= 2;
	public static final short		ALARM_SUBTYPE_CAPWAP_DELAY 			= 3;

	public static final short       ALARM_SUBTYPE_TCA =10100;

	public static final short		ALARM_SUBTYPE_DOS_BSSIDSPOOLING		= 1;

	public static final short		ALARM_SUBTYPE_BOMB_WARNING_LICENSEEXPIRATION  = 1;

	public static final short		ALARM_SUBTYPE_AD_NETJOIN			= 1;
	public static final short		ALARM_SUBTYPE_AD_BINDDN				= 2;

	public static final short		ALARM_SUBTYPE_SYSTEM_POWER			= 100;
	public static final short		ALARM_SUBTYPE_SYSTEM_SIM			= 101;
	public static final short		ALARM_SUBTYPE_SYSTEM_IDM_PROXY		= 103;

	//WLAN connection alarm type
	public static final short 		ALARM_SUBTYPE_WLAN_TXRETRY			= 1;
	public static final short 		ALARM_SUBTYPE_WLAN_TXFRAMEERROR		= 2;
	public static final short 		ALARM_SUBTYPE_WLAN_PROBREQUEST		= 3;
	public static final short		ALARM_SUBTYPE_WLAN_CHANNELUTIL		= 4;
	public static final short 		ALARM_SUBTYPE_WLAN_EGRESSMULTICAST	= 5;
	public static final short		ALARM_SUBTYPE_WLAN_INGRESSMULTICAST = 6;

	public static final short		ALARM_SUBTYPE_UNKNOWN				= 999;

	// tag1 type
	public static final short		ALARM_TAG1_KERNELDUMP				= 1;
	public static final short		ALARM_TAG1_FAILURE					= 2;
	public static final short		ALARM_TAG1_INTERFERENCEALERT		= 3;

	//hm type
	public static final short       ALARM_HM_EUCLID_SERVER         =1;

	public static final long        REMOVE_EVENT_ALARM_INTERVAL = 60 * 60; // remove one time per one hour, unit is seconds
}
