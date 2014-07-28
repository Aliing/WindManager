/**
 *@filename		BeParaModule.java
 *@version		v1.17
 *@author		Fiona
 *@createtime	2007-9-3 02:00:37 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *add dns alg 2009-05-21
 */
package com.ah.be.parameter;

import com.ah.be.common.NmsUtil;

/**
 * @author Fiona
 * @version v1.17
 */
public interface BeParaModule {
	/**
	 * Define default config template name
	 */
	public static final String DEFAULT_DEVICE_GROUP_NAME = "def-policy-template";

	/**
	 * Define Hive profile name
	 */
	public static final String DEFAULT_HIVEID_PROFILE_NAME = NmsUtil.getOEMCustomer().getWirelessUnitName() + "0";

	/**
	 * Define default radio profile name
	 */
	public static final String DEFAULT_RADIO_PROFILE_NAME_A = "radio_a0";

	public static final String DEFAULT_RADIO_PROFILE_NAME_BG = "radio_g0";

	public static final String DEFAULT_RADIO_PROFILE_NAME_NA = "radio_na0";

	public static final String DEFAULT_RADIO_PROFILE_NAME_NG = "radio_ng0";
	
	public static final String DEFAULT_RADIO_PROFILE_NAME_AC = "radio_ac0";

	/**
	 * Define some radio profile template
	 */
	public static final String RADIO_PROFILE_TEMPLATE_SYMBOL_SCANNER_BG = "Symbol-Scanner-11b/g-Profile";

	public static final String RADIO_PROFILE_TEMPLATE_SYMBOL_SCANNER_NG = "Symbol-Scanner-11ng-Profile";

	public static final String RADIO_PROFILE_TEMPLATE_LEGACY_CLIENTS_BG = "Legacy-Clients-11b/g-Profile";

	public static final String RADIO_PROFILE_TEMPLATE_LEGACY_CLIENTS_NG = "Legacy-Clients-11ng-Profile";

	public static final String RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_A = "High-Capacity-11a-Profile";

	public static final String RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_BG = "High-Capacity-11b/g-Profile";

	public static final String RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_NA = "High-Capacity-40MHz-11na-Profile";

	public static final String RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_NA_OLD = "High-Capacity-11na-Profile";

	public static final String RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_NG = "High-Capacity-20MHz-11ng-Profile";

	public static final String RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_NG_OLD = "High-Capacity-11ng-Profile";

	public static final String RADIO_PROFILE_TEMPLATE_BLACK_BERRY_BG = "BlackBerry-11b/g-Profile";

	public static final String RADIO_PROFILE_TEMPLATE_BLACK_BERRY_NG = "BlackBerry-11ng-Profile";

	public static final String RADIO_PROFILE_TEMPLATE_SPECTRA_LINK_BG = "SpectraLink-11b/g-Profile";

	public static final String RADIO_PROFILE_TEMPLATE_SPECTRA_LINK_NG = "SpectraLink-11ng-Profile";
	
	public static final String RADIO_PROFILE_NAME_HIGH_CAPACITY_AC = "High-Capacity-80MHz-11ac-Profile";

	public static final String[] RADIO_PROFILE_NAME_A = {DEFAULT_RADIO_PROFILE_NAME_A, RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_A};

	public static final String[] RADIO_PROFILE_NAME_BG = {DEFAULT_RADIO_PROFILE_NAME_BG, RADIO_PROFILE_TEMPLATE_SYMBOL_SCANNER_BG,
		RADIO_PROFILE_TEMPLATE_LEGACY_CLIENTS_BG, RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_BG, RADIO_PROFILE_TEMPLATE_BLACK_BERRY_BG,
		RADIO_PROFILE_TEMPLATE_SPECTRA_LINK_BG};

	public static final String[] RADIO_PROFILE_NAME_NA = {DEFAULT_RADIO_PROFILE_NAME_NA, RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_NA};
	
	public static final String[] RADIO_PROFILE_NAME_AC = {DEFAULT_RADIO_PROFILE_NAME_AC, RADIO_PROFILE_NAME_HIGH_CAPACITY_AC};

	public static final String[] RADIO_PROFILE_NAME_NG = {DEFAULT_RADIO_PROFILE_NAME_NG, RADIO_PROFILE_TEMPLATE_SYMBOL_SCANNER_NG,
		RADIO_PROFILE_TEMPLATE_LEGACY_CLIENTS_NG, RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_NG, RADIO_PROFILE_TEMPLATE_BLACK_BERRY_NG,
		RADIO_PROFILE_TEMPLATE_SPECTRA_LINK_NG};

	public static final String[] RADIO_PROFILE_NAMES = {DEFAULT_RADIO_PROFILE_NAME_A, RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_A,
		DEFAULT_RADIO_PROFILE_NAME_BG, RADIO_PROFILE_TEMPLATE_SYMBOL_SCANNER_BG, RADIO_PROFILE_TEMPLATE_LEGACY_CLIENTS_BG,
		RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_BG, RADIO_PROFILE_TEMPLATE_BLACK_BERRY_BG, RADIO_PROFILE_TEMPLATE_SPECTRA_LINK_BG,
		DEFAULT_RADIO_PROFILE_NAME_NA, RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_NA, DEFAULT_RADIO_PROFILE_NAME_NG,
		RADIO_PROFILE_TEMPLATE_SYMBOL_SCANNER_NG, RADIO_PROFILE_TEMPLATE_LEGACY_CLIENTS_NG, RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_NG,
		RADIO_PROFILE_TEMPLATE_BLACK_BERRY_NG, RADIO_PROFILE_TEMPLATE_SPECTRA_LINK_NG,DEFAULT_RADIO_PROFILE_NAME_AC, RADIO_PROFILE_NAME_HIGH_CAPACITY_AC};

	/**
	 * Define default QoS Classification name
	 */
	public static final String DEFAULT_QOS_CLASSIFICATION_LAND_NAME = "def-land-cls";
	public static final String DEFAULT_QOS_CLASSIFICATION_AP_NAME = "def-ap-cls";

	/**
	 * Define default QoS marking name
	 */
	public static final String DEFAULT_QOS_MARKING_LAND_NAME = "def-land-mkr";
	public static final String DEFAULT_QOS_MARKING_AP_NAME = "def-ap-mkr";
	
	public static final String DEFAULT_NETWORK_REPORT="Default Report Template";
	public static final String DEFAULT_NETWORK_REPORT_SAMPLE="Sample Report";

	/**
	 * Define default IP Address name
	 */
	public static final String DEFAULT_IP_ADDRESS_NAME = NmsUtil.getOEMCustomer().getNmsName() + "-IP-Address";

	/**
	 * Define old default MAC Address name
	 */
	//public static final String DEFAULT_MAC_ADDRESS_NAME = NmsUtil.getOEMCustomer().getCompanyNameWithoutBlank() + "-MAC-Address";
	public static final String DEFAULT_MAC_ADDRESS_NAME = NmsUtil.getOEMCustomer().getCompanyNameWithoutBlank() + "-MAC-OUI";

	/**
	 * Define new default MAC OUI name
	 */
	public static final String DEFAULT_MAC_OUI_NAME = NmsUtil.getOEMCustomer().getCompanyNameWithoutBlank() /*+ "-MAC-OUI"*/;

	/**
	 * Define the default MAC OUIs
	 */
	public static final String[][] DEFAULT_MAC_OUIS = {
		// name, mac oui, comment
		{DEFAULT_MAC_OUI_NAME, NmsUtil.getHiveApMacOuis(), "default mac oui"},
		{"Cisco-Model-7921", "001da2", "Cisco Wi-Fi Phone Model 7921"},
		{"Apple-iPhone", "001b63", "Apple iPhone"},
		{"Aeroscout", "000ccc", "Aeroscout Wi-Fi tags"},
		{"Vocera-VoIP", "0009ef", "Vocera VoIP client"},
		{"Symbol-WiFi", "001570", "Symbol Wi-Fi device"},
		{"Cisco-Model-7920", "001759", "Cisco Wi-Fi Phone Model 7920"},
		{"D-Link-SIP-Phone", "00179a", "D-Link SIP Phone"},
		{"Samsung-Tablets-044665","044665","Samsung GALAXY S2"},
		{"Samsung-Tablets-5C0A5B","5C0A5B","Samsung GALAXY S3"},
		{"Samsung-Tablets-6021C0","6021C0","Samsung GALAXY Note 10.1"},
		{"Samsung-Tablets-5CF8A1","5CF8A1","Samsung GALAXY Note 10.1"}
	};

	/**
	 * Define the default OS Object
	 */
	public static final String[] DEFAULT_OS_OBJECTS_NAMES = {
		"Windows", "WindowsPhone", "MacOS", "iPad", "iPhone","iPod/iPhone/iPad", "Android", "Symbian", "Blackberry", "Chrome",
	};

	public static final String[][] DEFAULT_OS_OBJECTS_WINDOWS = {
		{"Windows NT 5.1", "Windows XP"},
		{"Windows NT 5.2", "Windows 2003"},
		{"Windows NT 6.0", "Windows Vista; Windows 2008"},
		{"Windows NT 6.1", "Windows 7"}
	};
	
	public static final String[][] DEFAULT_OS_OBJECTS_WINDOWS_DHCP = {
		{"Windows XP", ""},
		{"Windows 2000", ""},
		{"Windows ME", ""},
		{"Windows 98", ""},
		{"Windows 95", ""},
		{"Windows NT 4", ""},
		{"Windows 98SE", ""},
		{"Windows 7/Vista", ""},
		{"Windows 8", ""}
	};

	public static final String[][] DEFAULT_OS_OBJECTS_WINDOWSPHONE = {
	};
	
	public static final String[][] DEFAULT_OS_OBJECTS_WINDOWSPHONE_DHCP = {
		{"Windows Phone", "370103060f2c2e2"},
	};
	
	public static final String[][] DEFAULT_OS_OBJECTS_MACOS = {
		{"Mac OS X", "Mac OS"}
	};
	
	public static final String[][] DEFAULT_OS_OBJECTS_MACOS_DHCP = {
		{"Mac OS X", ""},
		{"Mac OS 9", ""},
		{"Mac OS X Lion", ""}
	};

	public static final String[][] DEFAULT_OS_OBJECTS_IOS = {
		{"iPad", "iPad"}
	};
	
	public static final String[][] DEFAULT_OS_OBJECTS_IOS_DHCP = {
	};

	public static final String[][] DEFAULT_OS_OBJECTS_IPHONE = {
		{"iPhone", "iPhone"}
	};
	
	public static final String[][] DEFAULT_OS_OBJECTS_IPHONE_DHCP = {
		
	};
	
	public static final String[][] DEFAULT_OS_OBJECTS_CHROME = {
		{"CrOS", "Chrome"}
	};
	
	public static final String[][] DEFAULT_OS_OBJECTS_CHROME_DHCP = {
		{"CrOS",""}
	};

	public static final String[][] DEFAULT_OS_OBJECTS_IPOD_IPHONE_IPAD = {
		{"iPod", "iPod"},{"iPhone", "iPhone"},{"iPad", "iPad"}
	};
	
	public static final String[][] DEFAULT_OS_OBJECTS_IPOD_IPHONE_IPAD_DHCP = {
		{"Apple iOS", ""}
	};
	
	public static final String[][] DEFAULT_OS_OBJECTS_ANDROID = {
		{"Android", "Android"}
	};
	
	public static final String[][] DEFAULT_OS_OBJECTS_ANDROID_DHCP = {
		{"Android",""}
	};
	
	public static final String[][] DEFAULT_OS_OBJECTS_SYMBIAN = {
		{"Symbian","Symbian"}
	};
	
	public static final String[][] DEFAULT_OS_OBJECTS_SYMBIAN_DHCP = {
		{"Symbian", "12,6,15,1,3,28,120"},
		{"Symbian", "12,6,15,1,3,28,120,119"}
	};
	
	public static final String[][] DEFAULT_OS_OBJECTS_BLACKBERRY = {
		{"Blackberry","Blackberry"}
	};
	
	public static final String[][] DEFAULT_OS_OBJECTS_BLACKBERRY_DHCP = {
		{"Blackberry", "1,3,6,15"}
	};
	
	public static final String[][][][] DEFAULT_OS_OBJECTS_CONFIGURATION = {
		{DEFAULT_OS_OBJECTS_WINDOWS, 			DEFAULT_OS_OBJECTS_WINDOWS_DHCP},
		{DEFAULT_OS_OBJECTS_WINDOWSPHONE, 		DEFAULT_OS_OBJECTS_WINDOWSPHONE_DHCP},
		{DEFAULT_OS_OBJECTS_MACOS,				DEFAULT_OS_OBJECTS_MACOS_DHCP},
		{DEFAULT_OS_OBJECTS_IOS,				DEFAULT_OS_OBJECTS_IOS_DHCP},
		{DEFAULT_OS_OBJECTS_IPHONE,				DEFAULT_OS_OBJECTS_IPHONE_DHCP},
		{DEFAULT_OS_OBJECTS_IPOD_IPHONE_IPAD,	DEFAULT_OS_OBJECTS_IPOD_IPHONE_IPAD_DHCP},
		{DEFAULT_OS_OBJECTS_ANDROID,			DEFAULT_OS_OBJECTS_ANDROID_DHCP},
		{DEFAULT_OS_OBJECTS_SYMBIAN,			DEFAULT_OS_OBJECTS_SYMBIAN_DHCP},
		{DEFAULT_OS_OBJECTS_BLACKBERRY,			DEFAULT_OS_OBJECTS_BLACKBERRY_DHCP},
		{DEFAULT_OS_OBJECTS_CHROME,			    DEFAULT_OS_OBJECTS_CHROME_DHCP}
	};

	/**
	 * Define the network object--->network pre define Services;
	 * default L7 service
	 * { "AOL_IMAIM", "", "", "300", "Messaging aim", "", "2", "38" },
		{ "BITTORRE", "", "", "300", "File Transfer", "", "2", "81" },
		{ "FACEBOOK", "", "", "300", "Social Networking", "", "2", "191" },
		{ "GTALK", "", "", "300", "Messaging google", "", "2", "255" },
		{ "MicrosoftMSNP", "", "", "300", "Microsoft Notification Protocol", "", "2", "395" },
		{ "PT", "", "", "300", "chatMessaging paltalk", "", "2", "490" },
		{ "QQ", "", "", "300", "Messaging qq", "", "2", "503" },
		{ "SKYPE", "", "", "300", "Messaging skype", "", "2", "562" },
		{ "YahooYMSG", "", "", "300", "Yahoo! Instant Messaging", "", "2", "703" },
		{ "YOUTUBE", "", "", "300", "Youtube! Streaming Media", "", "2", "706" }
	 * 
	 */
	public static final String[][] NETWORK_PRE_DEFIND_SERVICES = {
		// Service Name,protocol,port number,number of timeOut,comment,ALG type
		{ "BGP", "6", "179", "1800", "Border Gateway Protocol", "1", "1", "" },
		{ "DHCP-Client", "17", "68", "60",
			"Dynamic Host Configuration Protocol (Client)", "1", "1", "" },
		{ "DHCP-Server", "17", "67", "60",
			"Dynamic Host Configuration Protocol (Server)", "1", "1", "" },
		{ "DNS", "17", "53", "60", "Domain Name System", "5", "1", "" },
		{ "FINGER", "6", "79", "1800", "UNIX program providing user information", "1", "1", "" },
		{ "FTP", "6", "21", "1800", "File Transfer Protocol", "2", "1", "" },
		{ "FTP-DATA", "6", "20", "1800", "File Transfer Protocol for data transmission", "1", "1", "" },
		{ "GOPHER", "6", "70", "1800", "Document sharing protocol", "1", "1", "" },
		{ "HTTP", "6", "80", "300", "Hypertext Transfer Protocol", "1", "1", "" },
		{ "HTTP-8080", "6", "8080", "300", "Hypertext Transfer Protocol 8080", "1", "1", "" },
		{ "HTTPS", "6", "443", "1800", "Secure Hypertext Transfer Protocol", "1", "1", "" },
		{ "ICA", "6", "1494", "7200", "Citrix Independent Computing Architecture", "1", "1", "" },
		{ "ICMP", "1", "0", "10", "Internet Control Message Protocol", "1", "1", "" },
		{ "IKE", "17", "500", "60", "Internet Key Exchange", "1", "1", "" },
		{ "IMAP-TCP", "6", "143", "60", "Internet Message Access Protocol(TCP)", "1", "1", "" },
		{ "IMAP-UDP", "17", "143", "60", "Internet Message Access Protocol(UDP)", "1", "1", "" },
		{ "L2TP", "17", "1701", "60", "Layer 2 Tunneling Protocol", "1", "1", "" },
		{ "LDAP", "6", "389", "1800", "Lightweight Directory Access Protocol", "1", "1", "" },
		{ "MSNP", "6", "1863", "1800", "Microsoft Notification Protocol", "1", "1", "" },
		{ "NBNAME", "17", "137", "60", "NetBIOS Name Service", "1", "1", "" },
		{ "NFS", "17", "111", "2400", "Network File System", "1", "1", "" },
		{ "NNTP", "6", "119", "1800", "Network News Transfer Protocol", "1", "1", "" },
		{ "NTP", "17", "123", "60", "Network Time Protocol", "1", "1", "" },
		{ "OSPF", "89", "0", "60", "Open Shortest Path First", "1", "1", "" },
		{ "PC-Anywhere", "17", "5632", "60", "Remote PC access", "1", "1", "" },
		{ "PCoIP-Control", "6", "50002", "7200", "PC-over-IP for session establishment and control", "1", "1", "" },
		{ "PCoIP-Media", "17", "50002", "7200", "PC-over-IP for optimal performance of media and streaming", "1", "1", "" },
		{ "POP3-TCP", "6", "110", "60", "Post Office Protocol v3(TCP)", "1", "1", "" },
		{ "POP3-UDP", "17", "110", "60", "Post Office Protocol v3(UDP)", "1", "1", "" },
		{ "PPTP", "6", "1723", "1800", "Point-to-Point Tunneling Protocol", "1", "1", "" },
		{ "RADIUS", "17", "1812", "60", "RADIUS Authentication", "1", "1", "" },
		{ "RADIUS-ACCT", "17", "1813", "60", "RADIUS Accounting", "1", "1", "" },
		{ "RIP", "17", "520", "60", "Routing Information Protocol", "1", "1", "" },
		{ "RLOGIN", "6", "513", "1800", "Remote Login", "1", "1", "" },
		{ "RSH", "6", "514", "1800", "Remote shell", "1", "1", "" },
		{ "SIP", "17", "5060", "600", "Session Initiation Protocol", "4", "1", "" },
		{ "SNMP", "17", "161", "60", "Simple Network Management Protocol", "1", "1", "" },
		{ "SNMP-Trap", "17", "162", "60", "Simple Network Management Protocol Trap", "1", "1", "" },
		{ "SMB", "6", "139", "1800", "Server Message Block Protocol", "1", "1", "" },
		{ "SMTP-TCP", "6", "25", "60", "Simple Mail Transfer Protocol(TCP)", "1", "1", "" },
		{ "SMTP-UDP", "17", "25", "60", "Simple Mail Transfer Protocol(UDP)", "1", "1", "" },
		{ "SSH", "6", "22", "7200", "Secure Shell", "1", "1", "" },
		{ "SYSLOG", "17", "514", "60", "System log forwarding service", "1", "1", "" },
		{ "TeacherView-HTTP", "6", "80", "300", "Hypertext Transfer Protocol", "6", "1", "" },
		{ "TELNET", "6", "23", "1800", "Protocol for remote logins from a local terminal", "1", "1", "" },
		{ "TFTP", "17", "69", "60", "Trivial File Transfer Protocol", "3", "1", "" },
		{ "VOCERA-Control", "17", "5002", "300", "Vocera control messages", "1", "1", "" },
		{ "VOCERA-Media", "17", "5200", "60", "Vocera data stream", "1", "1", "" },
		{ "VOIP-SVP", "119", "0", "600", "Voice over IP using SpectraLink Voice Priority", "1", "1", "" },
		{ "YMSG", "6", "5050", "1800", "Yahoo! Instant Messaging", "1", "1", "" }
		};

	/*
	 * Define the Device default network service
	 * L7 default service
	 * "AOL_IMAIM", "BITTORRE",
		 "FACEBOOK", "GTALK", "MicrosoftMSNP", "PT", "QQ", "SKYPE", "YahooYMSG", "YOUTUBE"
	 */
	public static final String[] CLI_DEFAULT_NETWORK_SERVICE = {
		"DNS", "FTP", "FTP-DATA", "HTTP", "HTTPS", "ICMP", "RADIUS", "RADIUS-ACCT", "SIP",
		"SNMP", "SNMP-Trap", "SSH", "TELNET", "TFTP", "VOIP-SVP", "DHCP-Client", "DHCP-Server",
		"VOCERA-Control", "VOCERA-Media", "IMAP-TCP", "IMAP-UDP", "POP3-TCP", "POP3-UDP",
		"SMTP-TCP", "SMTP-UDP", "PCoIP-Control", "PCoIP-Media", "ICA"
	};

	/*
	 * old default network services change to non-default
	 */
	public static final String[] OLD_DEFAULT_NETWORK_SERVICE = {
		"IMAP", "POP3", "SMTP", "FTP-ALG", "TFTP-ALG", "SIP-ALG" };
	public static final String[] NEW_DEFAULT_NETWORK_SERVICE = {
		"IMAP-TCP", "POP3-TCP", "SMTP-TCP", "FTP", "TFTP", "SIP" };

	/**
	 * Define the default QoS Services;
	 */
	public static final String[] QOS_CLASSIFICATION_DEFAULT_SERVICES = {
		"ANY","BGP","FTP","FTP-DATA","HTTP","HTTPS","ICMP","RADIUS","RADIUS-ACCT",
			"SIP","SNMP","SNMP-Trap","SSH","TELNET","TFTP","VOIP-SVP" };


	/**
	 * Define default QoS Rate Control name
	 */
	public static final String DEFAULT_QOS_RATE_CONTROL_NAME = "def-user-qos";

	/**
	 * Define default QoS Rate Control weight
	 */
	public static final int[] DEFAULT_QOS_RATE_CONTROL_WEIGHT = new int[]{10, 20, 30, 40, 50, 60, 0, 0};

	/**
	 * Define default MAC Dos(SSID/Hive) name
	 */
	public static final String DEFAULT_MAC_DOS_NAME = NmsUtil.getOEMCustomer().getCompanyNameWithoutBlank() + "-MAC-DoS";

	/**
	 * Define default MAC DosStation name
	 */
	public static final String DEFAULT_MAC_DOS_STATION_NAME = NmsUtil.getOEMCustomer().getCompanyNameWithoutBlank() + "-MAC-DoS-Station";

	/**
	 * Define IP Dos(SSID) name
	 */
	public static final String DEFAULT_IP_DOS_NAME = NmsUtil.getOEMCustomer().getCompanyNameWithoutBlank() + "-IP-DoS";

	/**
	 * Define User Profile name
	 */
	public static final String DEFAULT_USER_PROFILE_NAME = "default-profile";

	/**
	 * Define SSID Profile name
	 */
	public static final String DEFAULT_SSID_PROFILE_NAME = "ssid0";

	/**
	 * Define some radio profile template
	 */
	public static final String SSID_PROFILE_TEMPLATE_SYMBOL_SCANNER = "(For-Cloning)-Symbol-Scanner";

	public static final String SSID_PROFILE_TEMPLATE_LEGACY_CLIENTS = "(For-Cloning)-Legacy-Clients";

	public static final String SSID_PROFILE_TEMPLATE_HIGH_CAPACITY = "(For-Cloning)-HighCapacity-a/g/n";

	public static final String SSID_PROFILE_TEMPLATE_BLACK_BERRY = "(For-Cloning)-BlackBerry";

	public static final String SSID_PROFILE_TEMPLATE_SPECTRA_LINK = "(For-Cloning)-SpectraLink";

	public static final String[] SSID_PROFILE_NAMES = {DEFAULT_SSID_PROFILE_NAME, SSID_PROFILE_TEMPLATE_SYMBOL_SCANNER,
		SSID_PROFILE_TEMPLATE_LEGACY_CLIENTS, SSID_PROFILE_TEMPLATE_HIGH_CAPACITY, SSID_PROFILE_TEMPLATE_BLACK_BERRY,
		SSID_PROFILE_TEMPLATE_SPECTRA_LINK};

	/**
	 * Define default Service Filter name
	 */
	public static final String DEFAULT_SERVICE_FILTER_NAME = "def-service-filter";

	/**
	 * Define default Mgmt Service SNMP name
	 */
	public static final String DEFAULT_SERVICE_SNMP_NAME = "def-service-snmp";

	/**
	 * Define default ALG Service name
	 */
	public static final String DEFAULT_SERVICE_ALG_NAME = "def-service-alg";

	/**
	 * Define default RADIUS Proxy name for Cloud Auth
	 */
	public static final String DEFAULT_RADIUS_RADSEC_PROXY_NAME = "def-cloud-auth-proxy";
	
	/**
	 * Define default RADIUS User Profile Rule name
	 */
	public static final String DEFAULT_RADIUS_UP_RULE_NAME = "def-radius-user-profile-rule";

	/**
	 * Define default Report name
	 */
	public static final String DEFAULT_REPORT_NAME = "default_report_name";

	/**
	 * Define default IP Policy name
	 */
	public static final String DEFAULT_IPPOLICY_NAME = "Guest-Internet-Access-Only";

	/**
	 * Define default WIPS Policy name
	 */
	public static final String DEFAULT_WIPS_POLICY_NAME = "def-rogue-detection-wips";

	/**
	 * Define default IP Tracking name
	 */
	public static final String DEFAULT_IP_TRACKING_AP_NAME = "Default_AP";

	public static final String DEFAULT_IP_TRACKING_BR_NAME = "Default_Router";

	public static final String DEFAULT_IP_TRACKING_VPN_GATEWAY_NAME = "Default_VPN_Gateway";

	public static final String DEFAULT_IP_TRACKING_AP_NAME_NEW = "QS-IP-Track-AP";

	public static final String DEFAULT_IP_TRACKING_BR_NAME_NEW = "QS-IP-Track-Router";
	
	public static final String DEFAULT_IP_TRACKING_WAN_BR_NAME_NEW = "QS-IP-Track-WAN-Router";

	public static final String DEFAULT_IP_TRACKING_VPN_GATEWAY_NAME_NEW = "QS-IP-Track-VPN_Gateway";

	/**
	 * Pre-defined VPN Network for quick start
	 */
	public static final String PRE_DEFINED_VPN_NETWORK_OBJECT_NAME = "QS-172.28.0.0/16";
	
	public static final String PRE_DEFINED_VPN_NETWORK_FOR_USERPROFILE = "QS-USR-172.28.0.0";
	
	/**
	 * Pre-defined VPN Network for management network
	 */
	public static final String PRE_DEFINED_VPN_NETWORK_FOR_MANAGEMENT = "QS-MGT-172.18.0.0";
	
	
	/**
	 * Define default PSE profile name
	 */
	public static final String DEFAULT_PSE_PROFILE_NAME = "default-pse";
	

	/**
	 * Pre-defined User Profile for quick start
	 * <b>Please do not use this pre-defined name</b>
	 */
	//remove quick start policies
	public static final String PRE_DEFINED_USER_PROFILE_NAME = "QS-User-Profile";

	/**
	 * Pre-defined SSID Profile for quick start
	 */
	//remove quick start policies
	//public static final String PRE_DEFINED_SSID_PROFILE_NAME = "QS-SSID";
	
	/**
	 * Pre-defined IDM SSID(ppsk) Profile for quick start
	 * <b>Please do not use this pre-defined name</b>
	 */
	//remove quick start policies
	public static final String PRE_DEFINED_IDM_SSID_PPSK_PROFILE_NAME = "QS-IDM-SSID-PPSK";
	
	/**
	 * Pre-defined IDM SSID(802.1X) Profile for quick start
	 * <b>Please do not use this pre-defined name</b>
	 */
	//remove quick start policies
	public static final String PRE_DEFINED_IDM_SSID_ENTERPRISE_PROFILE_NAME = "QS-IDM-SSID-802.1X";

	/**
	 * Pre-defined LAN Profile for quick start
	 */
	public static final String PRE_DEFINED_LAN_PROFILE_NAME = "QS-LAN";
	
	/**
	 * Pre-defined PSE Profile for quick start
	 */
	//remove quick start policies
	//public static final String PRE_DEFINED_PSE_PROFILE_NAME = "QS-PSE";

	/**
	 * Pre-defined Network Policy
	 */
	//remove quick start policies
	//public static final String DEFAULT_NETWORK_POLICY_NAME = "QuickStart-Wireless-Only";

	/**
	 * Pre-defined Network Policy for quick start
	 */
	//remove quick start policies
	//public static final String PRE_DEFINED_NETWORK_POLICY_NAME = "QuickStart-Wireless-Routing";
	
	/**
	 * Pre-defined Bonjour Only Network Policy
	 * <b>Please do not use this pre-defined name</b>
	 */
	//remove quick start policies
	public static final String PRE_DEFINED_BONJOUR_NETWORK_POLICY = "QuickStart-Bonjour-Policy";
	
	/**
	 * Pre-defined IDM Network Policy
	 * <b>Please do not use this pre-defined name</b>
	 */
	//remove quick start policies
	public static final String PRE_DEFINED_IDM_NETWORK_POLICY = "QuickStart-IDM-Policy";
	
	/**
	 * Pre-defined Wireless + Switch NetWork Policy
	 */
	//remove quick start policies
	//public static final String PRE_DEFINED_WIRELESS_AND_SWITCH = "QuickStart-AP-Router-Switch";
	
	/**
	 * Pre-defined Bonjour Gateway Profile for Aerohive users
	 * <b>Please do not use this pre-defined name</b>
	 */
	//remove quick start policies
	public static final String PRE_DEFINED_BONJOUR_PROFILE_AEROHIVE = "QS-Bonjour-Service";
	
	/**
	 * Pre-defined Bonjour Gateway Profile for Apple users
	 */
	
	public static final String PRE_DEFINED_BONJOUR_PROFILE_APPLE = "QS-Bonjour-Only";
	
	/**
	 * Pre-defined port type profile
	 */
	
	//remove quick start policies
	//public static final String PRE_DEFINED_PORTTYPE_ACCESS = "QS-PortType-Access";
	
	//remove quick start policies
	//public static final String PRE_DEFINED_PORTTYPE_PHONEDATA = "QS-PortType-Phone&Data";
	
	//remove quick start policies
	//public static final String PRE_DEFINED_PORTTYPE_802 = "QS-PortType-802.1Q";
	
	//remove quick start policies
	//public static final String PRE_DEFINED_PORTTYPE_MIRROR = "QS-PortType-Mirror";
	
	//remove quick start policies
	//public static final String PRE_DEFINED_PORTTYPE_WAN = "QS-PortType-Wan";
	
	//remove quick start policies
	//public static final String PRE_DEFINED_PORTTYPE_ACCESS_ROUTER = "QS-PortType-Access-Router";
	
	//remove quick start policies
	//public static final String PRE_DEFINED_PORTTYPE_802_ROUTER = "QS-PortType-802.1Q-Router";
	
	//remove quick start policies
	//public static final String PRE_DEFINED_PORTTYPE_WAN_ROUTER = "QS-PortType-Wan-Router";
	
	/**
	 * Pre-defined Classifier Maps for quick start
	 */
	public static final String[][] NETWORK_PRE_DEFIND_SERVICES_FOR_QOS = {
		// Service Name, qosClass
		{ "DHCP-Client", "4" },
		{ "DHCP-Server", "4" },
		{ "DNS", "4" },
		{ "FTP", "2" },
		{ "HTTP", "2" },
		{ "HTTP-8080", "2" },
		{ "HTTPS", "2" },
		{ "ICA", "3" },
		{ "PCoIP-Control", "3" },
		{ "PCoIP-Media", "3" },
		{ "SIP", "6" },
		{ "SMB", "2" },
		{ "SSH", "2" },
		{ "TFTP", "2" },
		{ "VOCERA-Control", "5" },
		{ "VOCERA-Media", "6" },
		{ "VOIP-SVP", "6" }};

	/**
	 * Key words for Device Domain Object
	 */
	public static final String DEVICE_DOMAIN_OBJECT_KEY_WORD_KNOWN = "Known";

	public static final String DEVICE_DOMAIN_OBJECT_KEY_WORD_UNKNOWN = "Unknown";

	int getWatchDogReportInterval();

	/**
	 * Insert or update the default profiles.
	 */
	public void constructDefaultProfile();

	/**
	 * insert default user group for user management
	 *
	 *@param
	 *
	 *@return
	 */
	public void insertDefaultGMUserGroup() throws Exception;

}