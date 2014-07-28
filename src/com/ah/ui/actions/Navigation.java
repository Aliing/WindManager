package com.ah.ui.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.LicenseInfo;
import com.ah.be.license.LicenseOperationTool;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ha.HAUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.xml.navigation.XmlNavigationNode;
import com.ah.xml.navigation.XmlNavigationTree;
import com.opensymphony.xwork2.ActionSupport;

/*
 * @author Chris Scheers
 */
public class Navigation extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(Navigation.class
			.getSimpleName());

	protected HmUser userContext;

	public HmUser getUserContext() {
		return userContext;
	}

	public void setUserContext(HmUser hmUser) {
		this.userContext = hmUser;
	}

	protected BeVersionInfo versionInfo;

	public BeVersionInfo getVersionInfo() {
		return versionInfo;
	}

	/*
	 * Left menu options
	 */
	// private NavigationNode l2Features;
	//
	// public NavigationNode getL2Features() {
	// return l2Features;
	// }
	/*
	 * 3rd level menu options
	 */
	private String[] l3Features;

	public String[] getL3Features() {
		return l3Features;
	}

	public void setL3Features(String[] features) {
		l3Features = features;
	}

	protected NavigationNode selectedL1Feature;

	protected NavigationNode selectedL2Feature;

	protected String selectedL3Feature;

	public NavigationNode getSelectedL1Feature() {
		return selectedL1Feature;
	}

	public void setSelectedL1Feature(String selectedL1Feature) {
		this.selectedL1Feature = getL1Feature(selectedL1Feature);
	}

	public void setAccessibleL1Feature() {
		if (null == userContext) {
			log.info("setAccessibleL1Feature", "userContext is null");
		} else {
			log.info("setAccessibleL1Feature", "userContext="+userContext.getEmailAddress());

			if (null == userContext.getNavigationTree()) {
				log.info("setAccessibleL1Feature", "userContext.getNavigationTree() is null");
			} else {
				log.info("setAccessibleL1Feature", "userContext.getNavigationTree()="+userContext.getNavigationTree().getKey());

				if (null == userContext.getNavigationTree().getChildNodes()) {
					log.info("setAccessibleL1Feature", "userContext.getNavigationTree().getChildNodes() is null");
				} else {
					if (!userContext.getNavigationTree().getChildNodes().isEmpty()) {
						this.selectedL1Feature = userContext.getNavigationTree()
								.getChildNodes().get(0);
					}
				}
			}
		}
	}

	public NavigationNode getL1Feature(String feature) {
		NavigationNode node = getFeatureNode(feature);
		if (node != null && node.getParentNode() != null) {
			return null;
		} else {
			return node;
		}
	}

	protected boolean isL2Feature(String feature, String parentFeature) {
		NavigationNode featureNode = getFeatureNode(feature);
		if (featureNode == null || featureNode.getParentNode() == null) {
			return false;
		}
		do {
			featureNode = featureNode.getParentNode();
		} while (featureNode.getParentNode() != null);
		return featureNode.getKey().equals(parentFeature);
	}

	public NavigationNode getSelectedL2Feature() {
		return selectedL2Feature;
	}

	public String getSelectedL2FeatureKey() {
		if (selectedL2Feature == null) {
			return null;
		} else {
			return selectedL2Feature.getKey();
		}
	}

	public void setSelectedL2Feature(NavigationNode selectedL2Feature) {
		this.selectedL2Feature = selectedL2Feature;
	}

	public void setSelectedL2Feature(String selectedL2Feature) {
		if (getFeatureNodes() != null) {
			this.selectedL2Feature = getFeatureNodes().get(selectedL2Feature);
			if (this.selectedL2Feature != null) {
				this.selectedL1Feature = this.selectedL2Feature.getParentNode();
				NavigationNode visibleTree = null;
				while (this.selectedL1Feature.getParentNode() != null) {
					visibleTree = this.selectedL1Feature;
					this.selectedL1Feature = this.selectedL1Feature
							.getParentNode();
				}
				setStyleClasses(userContext.getNavigationTree());
				if (visibleTree != null && visibleTree.isCollapsible()) {
					expandTree(visibleTree, true);
					visibleTree.setSelectedTree(true);
				}
				String styleClass = this.selectedL2Feature.getStyleClass();
				this.selectedL2Feature.setStyleClass(styleClass + " "
						+ styleClass + "Sel");
				this.selectedL2Feature.setTdStyleClass("leftNavTDSel");
				if (this.selectedL2Feature.getIndent() > 0) {
					NavigationNode summaryNode = findSummaryNode();
					if (summaryNode != null) {
						expandTree(summaryNode, true);
					}
				}
			}
		}
	}

	public NavigationNode findSummaryNode() {
		NavigationNode l1node = this.selectedL2Feature.getParentNode();
		if (l1node.getParentNode() != null) {
			l1node = l1node.getParentNode();
		}
		NavigationNode summaryNode = null;
		for (NavigationNode l2Feature : l1node.getChildNodes()) {
			if (l2Feature.isSummary()) {
				summaryNode = l2Feature;
			} else if (l2Feature == this.selectedL2Feature
					|| l2Feature == this.selectedL2Feature.getParentNode()) {
				return summaryNode;
			}
		}
		return null;
	}

	public String getSelectedL3Feature() {
		return selectedL3Feature;
	}

	public void setSelectedL3Feature(String selectedL3Feature) {
		this.selectedL3Feature = selectedL3Feature;
	}

	/**
	 * Top Level features
	 */
	public static final String L1_FEATURE_HOME = "home";

	public static final String L1_FEATURE_MONITOR = "monitor";
	
	public static final String L1_FEATURE_DASH = "dash";

	public static final String L1_FEATURE_TOPOLOGY = "topology";

	public static final String L1_FEATURE_CONFIGURATION = "configuration";

	public static final String L1_FEATURE_TOOLS = "tools";

	public static final String L1_FEATURE_USER_MGR = "userMgr";

	public static final String L1_FEATURE_USER_REPORTS = "userReport";

	public static final String L1_FEATURE_TEACHERVIEW="tv";

	public static final String L1_FEATURE_REPORT = "hiveReports";

	/**
	 * Home menu features
	 */
	public static final String L2_FEATURE_START_HERE = "startHereMgr";

	public static final String L2_FEATURE_SYSTEM_OVERVIEW = "systemOverview";

	public static final String L2_FEATURE_USER_PASSWORD_MODIFY = "userPasswordModify";

	public static final String L2_FEATURE_ADMINISTRATION = "administration";

	public static final String L2_FEATURE_RESTART_SOFTWARE = "restartSoftware";

	public static final String L2_FEATURE_REBOOTAPP = "rebootApp";

	public static final String L2_FEATURE_SHUTDOWN_APPLIANCE = "shutDownApp";

	public static final String L2_FEATURE_CLEAR_DB = "clearDB";

	public static final String L2_FEATURE_BACKUP_DB = "backupDB";

	public static final String L2_FEATURE_RESTORE_DB = "restoreDB";

	public static final String L2_FEATURE_UPDATE_SOFTWARE = "updateSoftware";	
	
	public static final String L2_FEATURE_SUPPORT_BUNDLE = "supportBundle";

	public static final String L2_FEATURE_ADMIN_GROUPS = "adminGroups";

	public static final String L2_FEATURE_ADMINISTRATORS = "administrators";

	public static final String L2_FEATURE_LOG_MGR = "logMgr";

	public static final String L2_FEATURE_HM_SETTINGS = "hmSettings";

	public static final String L2_FEATURE_HM_SERVICES = "hmServices";

	public static final String L2_FEATURE_HA_MONITORING = "haMonitor";

	public static final String L2_FEATURE_PORTAL_SETTINGS = "portalSettings";

	public static final String L2_FEATURE_AUXILIARY_FILE_MGMT = "auxiFileMgmt";

	public static final String L2_FEATURE_MACOUI_DICTIONARY = "macouiDictionary";

	public static final String L2_FEATURE_VHMMANAGEMENT = "vhmManagement";

	public static final String L2_FEATURE_LICENSEMGR = "licenseMgr";

	public static final String L2_FEATURE_SYSTEMLOG = "systemLog";

	public static final String L2_FEATURE_AUDITLOG = "auditLog";

	public static final String L2_FEATURE_UPGRADELOG = "upgradeLog";
	
	public static final String L2_FEATURE_KDDRLOG = "kddrLog";

	public static final String L2_FEATURE_MAILLIST = "mailList";

	public static final String L2_FEATURE_MIB_FILE = "mibFiles";

	public static final String L2_FEATURE_RADIUS_DICTIONARY = "radiusDicts";

	public static final String L2_FEATURE_HM_OPERATIONS = "hiveManagerOperations";

	public static final String L2_FEATURE_ADMIN_MGMT = "adminMgr";

	public static final String L2_FEATURE_HM_ACCESS = "hmAccess";

	public static final String L2_FEATURE_HELPSETTING = "helpSetting";
	
	public static final String L2_FEATURE_CID_CLIENTS = "cidClients";

	public static final String L2_FEATURE_OEM_SETTINGS = "oemSettings";
	/**
	 * Configuration menu features
	 */
	public static final String L2_FEATURE_CONFIGURATION_GUIDE = "configGuide";

	public static final String L2_FEATURE_ADD_HIVEAP = "addHiveAP";

	public static final String L2_FEATURE_UPDATE_CONFIG = "updateConfig";

	public static final String L2_FEATURE_ADVANCED_CONFIGURATION = "advancedConfig";

	public static final String L2_FEATURE_HIVEMANAGERCA = "hiveManagerCA";

	public static final String L2_FEATURE_SERVERCSR = "serverCSR";
	
	public static final String L2_FEATURE_CLIENT_CA = "clientCA";

	public static final String L2_FEATURE_CERTIFICATES = "certificates";

	public static final String L2_FEATURE_CWPCERTMGMT = "cwpCertMgmt";

	public static final String L2_FEATURE_SSHKEYSGEN = "sshKeysGen";

	public static final String L2_FEATURE_LAN = "lanProfiles";
	
	public static final String L2_FEATURE_PORTTYPE = "portAccess";

	public static final String L2_FEATURE_L3_RADIUSATTR = "radiusAttrs";
	
	/**
	 * Tools menu features
	 */

	public static final String L2_FEATURE_PLANNING_TOOL = "planTool";

	public static final String L2_FEATURE_CLIENTSURVEY = "clientSurvey";

	public static final String L2_FEATURE_HM_SIMULATOR = "apSimulator";

	public static final String L2_FEATURE_CLIENT_CONNECTION_MONITOR = "clientConnectMonitor";

	public static final String L2_FEATURE_PACKET_CAPTURE = "packetCapture";

	public static final String L2_FEATURE_VLAN_PROBE = "vlanProbe";
	
	public static final String L2_FEATURE_CLIENT_MGMT_TEST = "clientMgmtTest";

	public static final String L2_FEATURE_RADIUS_TEST = "radiusTest";

	public static final String L2_FEATURE_ADLDAP_TEST = "adLdapTest";

	public static final String L2_FEATURE_LIBRARY_SIP_TEST = "librarySipTest";

	public static final String L2_FEATURE_SPECTRAL_ANALYSIS = "spectralAnalysis";

	public static final String L2_FEATURE_CLOUDAUTH_CUSTOMER_RETRIEVE = "retrieveCACustomerId";
	
	public static final String L2_FEATURE_IDM_TEST = "idmTest";

	/*
	 * HiveAP Configuration features
	 */
	public static final String L2_FEATURE_HIVE_PROFILES = "hiveProfiles";

	public static final String L2_FEATURE_MAC_FILTERS = "macFilters";

	public static final String L2_FEATURE_IP_FILTERS = "ipFilters";

	public static final String L2_FEATURE_SERVICE_FILTERS = "managementFilters";

	public static final String L2_FEATURE_IP_ADDRESS = "ipAddress";

	public static final String L2_FEATURE_MAC_POLICY = "macPolicies";

	public static final String L2_FEATURE_IP_POLICY = "ipPolicies";

	public static final String L2_FEATURE_L3_FIREWALL_POLICY = "l3FirewallPolicies";

	public static final String L2_FEATURE_VLAN = "vlan";
	
	public static final String L2_FEATURE_VLAN_GROUP = "vlanGroup";
	
	public static final String L2_FEATURE_MSTP_REGION = "mstpRegion";
	
	public static final String L2_FEATURE_WIFICLIENT_PREFERRED_SSID = "wifiClinetPerferredSsid";

	public static final String L2_FEATURE_RADIUS_SERVER_ASSIGN = "radiusServerAssignments";

	public static final String L2_FEATURE_RADIUS_SERVER_HIVEAP = "radiusServiceOnHiveAps";

	public static final String L2_FEATURE_RADIUS_ACTIVE_DIRECTORY = "activeDirectoryOrLdap";

	public static final String L2_FEATURE_LOCAL_USER_GROUP = "localUserGroups";

	public static final String L2_FEATURE_RADIUS_SERVER_PROXY = "radiusProxy";

	public static final String L2_FEATURE_RADIUS_LIBRARY_SIP = "librarySip";

	public static final String L2_FEATURE_USER_PROFILE = "userProfiles";

	public static final String L2_FEATURE_RADIUS_USER_PROFILE_RULE = "radiusUserProfileRule";

	public static final String L2_FEATURE_LOCAL_USER = "localUsers";

	public static final String L2_FEATURE_PERSONALIZED_PSK_GROUP = "personalizedPskGroups";

	public static final String L2_FEATURE_PERSONALIZED_PSK = "personalizedPsks";

	public static final String L2_FEATURE_USER_PROFILE_ATTRIBUTE = "userAttributeGroups";

	public static final String L2_FEATURE_NETWORK_SERVICE = "networkService";

	public static final String L2_FEATURE_SSID_PROFILES = "ssidProfiles";

	public static final String L2_FEATURE_SSID_PROFILES_FULL = "ssidProfilesFull";

	public static final String L2_FEATURE_DOSPREVENTION = "dosPrevention";

	public static final String L2_FEATURE_MAC_DOS = "macDos";

	public static final String L2_FEATURE_IP_DOS = "ipDos";

	public static final String L2_FEATURE_SCHEDULER = "scheduler";

	public static final String L2_FEATURE_MAC_DOS_SSID = "macDoSSsids";

	public static final String L2_FEATURE_MAC_DOS_SSID_STATION = "macDoSSsidStations";

	public static final String L2_FEATURE_MAC_OR_OUI = "macAddress";

	public static final String L2_FEATURE_CAPTIVE_PORTAL_WEB = "captivePortalWeb";

	public static final String L2_FEATURE_CWP_PAGE_CUSTOMIZATION = "cwpPageCustomization";

	public static final String L2_FEATURE_INTER_SUBNET_ROAMING = "interSubnetRoaming";

	public static final String L2_FEATURE_QOS_CLASSIFICATION = "qosClassification";

	public static final String L2_FEATURE_QOS_MARKING = "qosMarking";

	public static final String L2_FEATURE_QOS_RATE_CONTROL = "qosRateControl";

	public static final String L2_FEATURE_IDENTITY_BASED_TUNNELS = "identityBasedTunnels";

	public static final String L2_FEATURE_IDS_POLICY = "idsPolicies";
	
	public static final String L2_FEATURE_CONFIG_MDM="configmdm";
	
	public static final String L2_FEATURE_MDM_PROFILES="mdmProfiles";
	public static final String L2_FEATURE_ONBOARD_UI_SETTING="onBoardUISetting";

	public static final String L2_FEATURE_RADIO_PROFILE = "radioProfiles";

	public static final String L2_FEATURE_MGMT_SERVICE_DNS = "mgmtServiceDns";

	public static final String L2_FEATURE_MGMT_SERVICE_SYSLOG = "mgmtServiceSyslog";

	public static final String L2_FEATURE_MGMT_SERVICE_OPTION = "mgmtServiceOption";

	public static final String L2_FEATURE_MGMT_SERVICE_SNMP = "mgmtServiceSnmp";

	public static final String L2_FEATURE_MGMT_SERVICE_TIME = "mgmtServiceTime";

	public static final String L2_FEATURE_ALG_CONFIGURATION = "algConfiguration";

	public static final String L2_FEATURE_PSE_CONFIGURATION = "pseConfiguration";

	public static final String L2_FEATURE_BONJOURGATEWAY_CONFIGURATION = "bonjourGateWayConfiguration";

	public static final String L2_FEATURE_8021X_CONFIGURATION = "8021xMacTableConfiguration";

	public static final String L2_FEATURE_ETHERNET_ACCESS = "ethernet";

	public static final String L2_FEATURE_LOCATION_SERVER = "locationServer";

	public static final String L2_FEATURE_MGMT_IP_TRACKING = "mgmtIpTrack";

	public static final String L2_FEATURE_HIVEAP_AUTO_PROVISIONING = "autoProvisioning";

	public static final String L2_FEATURE_ACCESS_CONSOLE = "accessConsole";

	public static final String L2_FEATURE_VLAN_DHCP_SERVER = "vlanDhcpServer";

	public static final String L2_FEATURE_LLDPCDP_PROFILE = "lldpcdpProfiles";

	public static final String L2_FEATURE_VPN_SERVICE = "vpnServices";

	public static final String L2_FEATURE_VPN_NETWORK = "vpnNetworks";

	public static final String L2_FEATURE_COMPLIANCE_POLICY = "compliancePolicy";

	public static final String L2_FEATURE_AIR_SCREEN_RULE = "airScreenRules";

	public static final String L2_FEATURE_AIR_SCREEN_RULE_GROUP = "airScreenRuleGroups";

	public static final String L2_FEATURE_OS_OBJECT = "osObject";

	public static final String L2_FEATURE_DOMAIN_OBJECT = "domainObject";

	public static final String L2_FEATURE_DEVICE_POLICY = "devicePolicy";
	// DNS Service Node
	public static final String L2_FEATURE_DNS_SERVICE = "dnsService";
	//USB Modem
	public static final String L2_FEATURE_USB_MODEM = "usbModem";

	public static final String L2_FEATURE_PPPOE = "pppoe";
	
	public static final String L2_FEATURE_PSE_PROFILE = "pseProfile";

	public static final String L2_FEATURE_BONJOUR_GATEWAY_SETTINGS = "bonjourGatewaySettings";

	public static final String L2_FEATURE_CLI_BLOB_SETTINGS = "cliBlob";

	public static final String L2_FEATURE_OS_DETECTION = "OSDETECTION";

	public static final String L2_FEATURE_ROUTING_POLICY="routingPolicy";
	
	public static final String L2_FEATURE_ROUTING_PROFILE_POLICY="routingProfilePolicy";
	
	
	
	public static final String L2_FEATURE_APP_PROFILE = "appProfile";

	public static final String L2_FEATURE_APPLICATION_SERVICE = "appService";
	/*
	 * Teacher View features
	 */
	public static final String L2_FEATURE_TV_GUIDE="tvGuide";
	public static final String L2_FEATURE_TV_CLASS="tvClass";
	public static final String L2_FEATURE_TV_COMPUTERCART="tvComputerCart";
	public static final String L2_FEATURE_TV_STUDENTROSTER="tvStudentRoster";
	public static final String L2_FEATURE_TV_RESOURCEMAP="tvResourceMap";
	public static final String L2_FEATURE_TV_SCHEDULEMAP="tvScheduleMap";
	/*
	 * HiveAP Management features
	 */

	public static final String L2_FEATURE_MANAGED_HIVE_APS = "managedHiveAps";

	public static final String L2_FEATURE_NEW_HIVE_APS = "newHiveAps";

	public static final String L2_FEATURE_AUTO_DISCOVERED = "autoDiscovered";

	public static final String L2_FEATURE_MANUALLY_PROVISIONED = "manuallyProvisioned";

	public static final String L2_FEATURE_FRIENDLY_APS = "friendlyAps";

	public static final String L2_FEATURE_ROGUE_APS = "rogueAps";

	public static final String L2_FEATURE_CONFIGURATION_TEMPLATE = "configTemplate";

	public static final String L2_FEATURE_HIVEAP_FILE = "hiveApFiles";

	public static final String L2_FEATURE_QOS_CLASSFIER_AND_MARKER = "qosClassfierAndMarker";

	public static final String L2_FEATURE_HIVEAP_UPDATE_RESULTS = "hiveApUpdateRts";

	public static final String L2_FEATURE_BRANCH_ROUTERS = "branchRouters";
	
	public static final String L2_FEATURE_SWITCHES = "switches";

	public static final String L2_FEATURE_VPN_GATEWAYS = "vpnGateways";

	public static final String L2_FEATURE_DEVICE_HIVEAPS = "deviceHiveAps";
	
	public static final String L2_FEATURE_CONFIG_HIVE_APS = "configHiveAps";
	
	public static final String L2_FEATURE_CONFIG_VPN_GATEWAYS = "configVpnGateways";
	
	public static final String L2_FEATURE_CONFIG_BRANCH_ROUTERS = "configBranchRouters";
	
	public static final String L2_FEATURE_CONFIG_SWITCHES = "configSwitches";
	
	public static final String L2_FEATURE_CONFIG_DEVICE_HIVEAPS = "configDeviceHiveAps";

	public static final String L2_FEATURE_DEVICES = "devices";
	
	public static final String L2_FEATURE_DEVICE_INVENTORY = "deviceInventory";
	
	public static final String L2_FEATURE_DEVICE_INVENTORY_MONITOR = "deviceInventoryMonitor";

	/*
	 * Dash features
	 */
	public static final String L2_FEATURE_DASHBOARD = "dashboard";
	
	/*
	 * Monitoring features
	 */
	public static final String L2_FEATURE_REPORT_NETWORKUSAGE="networkUsage";
	
	public static final String L2_FEATURE_REPORT_RECURREPORT="recurReport";

	public static final String L2_FEATURE_REPORTS = "hiveReports";

	public static final String L2_FEATURE_SUMMARY = "summary";

	public static final String L2_FEATURE_EVENTS = "events";

	public static final String L2_FEATURE_ALARMS = "alarms";

	public static final String L2_FEATURE_CLIENTS = "hiveClients";

	public static final String L2_FEATURE_CLIENTMONITOR = "clientMonitor";

	public static final String L2_FEATURE_WIREDCLIENT = "wiredClient";

	public static final String L2_FEATURE_WIRELESSCLIENT = "wirelessClient";

	public static final String L2_FEATURE_ROGUECLIENT = "rogueClient";

	public static final String L2_FEATURE_ENROLLED_CLIENTS = "enrolledClients";
	
	public static final String L2_FEATURE_CLIENTMODIFICATIONS = "clientModifications";

	public static final String L2_FEATURE_LOCATIONCLIENTWATCH = "watchList";

	public static final String L2_FEATURE_HIVEAPREPORT = "radioReports";

	public static final String L2_FEATURE_SSIDREPORT = "ssidReports";

	public static final String L2_FEATURE_CLIENTREPORT = "clientReports";

	public static final String L2_FEATURE_CHANNELPOWERNOISE = "channelPowerNoise";

	public static final String L2_FEATURE_RADIOAIRTIME = "radioAirTime";

	public static final String L2_FEATURE_RADIOTRAFFICMETRICS = "radioTrafficMetrics";

	public static final String L2_FEATURE_RADIOTROUBLESHOOTING = "radioTroubleShooting";

	public static final String L2_FEATURE_RADIOINTERFERENCE = "radioInterference";

	public static final String L2_FEATURE_SSIDAIRTIME = "ssidAirTime";

	public static final String L2_FEATURE_SSIDTRAFFICMETRICS = "ssidTrafficMetrics";

	public static final String L2_FEATURE_SSIDTROUBLESHOOTING = "ssidTroubleShooting";

	public static final String L2_FEATURE_MOSTCLIENTSAPS = "mostClientsAPs";

	public static final String L2_FEATURE_CLIENTSESSION = "clientSession";

	public static final String L2_FEATURE_CLIENTCOUNT = "clientCount";

	public static final String L2_FEATURE_CLIENTAIRTIME = "clientAirTime";

	public static final String L2_FEATURE_UNIQUECLIENTCOUNT = "uniqueClientCount";

	public static final String L2_FEATURE_CLIENTAUTH = "clientAuth";

	public static final String L2_FEATURE_CLIENTVENDOR = "clientVendor";

	public static final String L2_FEATURE_SECURITYROGUEAPS = "securityRogueAPs";

	public static final String L2_FEATURE_SECURITYROGUECLIENTS = "securityRogueClients";

	public static final String L2_FEATURE_SECURITYCOMPLIANCE = "compliance";

	public static final String L2_FEATURE_SECURITYPCICOMPLIANCE = "pciCompliance";

	public static final String L2_FEATURE_SECURITY_NONHIVEAP="hiveApNonCompliance";

	public static final String L2_FEATURE_SECURITY_NONCLIENT="clientNonCompliance";

	public static final String L2_FEATURE_MESHNEIGHBORS = "meshNeighbors";

	public static final String L2_FEATURE_INVENTORY = "inventory";

	public static final String L2_FEATURE_HIVEAPSLA = "hiveApSla";

	public static final String L2_FEATURE_CLIENTSLA = "clientSla";

	public static final String L2_FEATURE_CUSTOMREPORT = "customReport";

	public static final String L2_FEATURE_SUMMARYUSAGE = "summaryUserUsage";
	public static final String L2_FEATURE_DETAILUSAGE = "detailUserUsage";

	public static final String L2_FEATURE_REPORTSETTING = "reportSetting";

	public static final String L2_FEATURE_MAXCLIENTREPORT = "maxClient";

	public static final String L2_FEATURE_HIVEAPCONNECTION = "hiveApConnection";

	public static final String L2_FEATURE_ACCESSPOINTS = "accessPoints";

	public static final String L2_FEATURE_L3FIREWALLLOG = "l3firewallLog";
	
	public static final String L2_FEATURE_REPORT_RETAILANALYTICS = "retailAnalytics";

	//VPNREPORT
	//public static final String L2_FEATURE_REPORT_WIREDREPORTS = "wiredReports";
	public static final String L2_FEATURE_REPORT_WIREDBRREPORTS = "wiredBrReports";
	public static final String L2_FEATURE_REPORT_WIREDCVGREPORTS = "wiredCvgReports";
	//public static final String L2_FEATURE_REPORT_VPNREPORTS = "vpnReports";
	public static final String L2_FEATURE_REPORT_VPNBRREPORTS = "vpnBrReports";
	public static final String L2_FEATURE_REPORT_VPNCVGREPORTS = "vpnCvgReports";
	public static final String L2_FEATURE_REPORT_VPNTHROUGHPUT = "vpnThroughput";
	public static final String L2_FEATURE_REPORT_VPNLATENCY = "vpnLatency";
	public static final String L2_FEATURE_REPORT_VPNAVAILABILITY = "vpnAvailability";
//	public static final String L2_FEATURE_REPORT_WANLATENCY = "wanLatency";
	public static final String L2_FEATURE_REPORT_WANAVAILABILITY = "wanAvailability";
	public static final String L2_FEATURE_REPORT_WANTHROUGHPUT = "wanThroughput";

	public static final String L2_FEATURE_REPORT_GWVPNAVAILABILITY = "gwVpnAvailability";
	public static final String L2_FEATURE_REPORT_GWWANTHROUGHPUT = "gwWanThroughput";
	public static final String L2_FEATURE_REPORT_GWWANAVAILABILITY = "gwWanAvailability";

	//One time password
	public static final String L2_FEATURE_ONETIMEPASSWORD = "oneTimePassword";

	// Subnetwork allocation
	public static final String L2_FEATURE_SUBNETWORK_ALLOCATIONS = "subnetworkAllocations";

	public static final String L2_FEATURE_BONJOUR_GATEWAY_MONITORING = "bonjourGatewayMonitoring";

	/*
	 * Map features
	 */

	public static final String L2_FEATURE_MAP_VIEW = "mapView";

	/*
	 * UserManager features
	 */
	public static final String L2_FEATURE_UM_TEMP_CREATE = "createAccount";

	public static final String L2_FEATURE_UM_TEMP_REVOKE = "revokeAccount";

	public static final String L2_FEATURE_UM_PERM = "permAccount";

	public static final String L2_FEATURE_UM_TEMP_PRINT = "printTemplate";

	public static final String L2_FEATURE_UP_USER_DAY = "usersPerDay";

	public static final String L2_FEATURE_UP_SESS_DAY = "sessPerDay";

	public static final String L2_FEATURE_UP_SESS_TIME_DAY = "avgStPerDay";

	public static final String L2_FEATURE_UP_SESS_NAS = "sessPerNas";

	public static final String L2_FEATURE_GM_CLIENTMONITOR="gmClientMonitor";

	/*
	 * Sorting/Paging operations
	 */
	public static final String OPERATION_SORT = "sort";

	public static final String OPERATION_FIRST_PAGE = "firstPage";

	public static final String OPERATION_RESIZE_PAGE = "resizePage";

	public static final String OPERATION_PREVIOUS_PAGE = "previousPage";

	public static final String OPERATION_NEXT_PAGE = "nextPage";

	public static final String OPERATION_LAST_PAGE = "lastPage";

	public static final String OPERATION_GOTO_PAGE = "gotoPage";

	/*
	 * Tree images
	 */
	public static final String TREE_GRID_T = "/images/menutree/treenode_grid_t.gif";

	public static final String TREE_GRID_L = "/images/menutree/treenode_grid_l.gif";

	public static final String TREE_GRID_X = "/images/menutree/treenode_grid_x.gif";

	public static final String TREE_GRID_Y = "/images/menutree/treenode_grid_y.gif";

	public static final String TREE_GRID_V = "/images/menutree/treenode_grid_v.gif";
	/*
	 * Maps feature strings to feature nodes
	 */
	protected Map<String, NavigationNode> getFeatureNodes() {
		if (userContext == null) {
			return null;
		} else {
			return userContext.getFeatureNodes();
		}
	}

	protected NavigationNode getFeatureNode(String featureId) {
		if (getFeatureNodes() == null) {
			return null;
		}
		return getFeatureNodes().get(featureId);
	}

	/*
	 * Navigation tree from XML, needs to be customized once user signs in.
	 */
	protected static XmlNavigationTree xmlNavigationTree;

	public static synchronized XmlNavigationTree getXmlNavigationTree() {
		return xmlNavigationTree;
	}

	public static String getFeatureName(String feature) {
		XmlNavigationNode node = findXmlNavigationNode(feature,
				getXmlNavigationTree().getTree());
		if (node == null) {
			return "*** Unknown ***";
		} else {
			return node.getDescription();
		}
	}

	public static XmlNavigationNode findXmlNavigationNode(String feature,
			XmlNavigationNode node) {
		for (XmlNavigationNode childNode : node.getNode()) {
			if (childNode.getKey().equals(feature)) {
				return childNode;
			}
			XmlNavigationNode lowerNode = findXmlNavigationNode(feature,
					childNode);
			if (lowerNode != null) {
				return lowerNode;
			}
		}
		return null;
	}

	protected boolean isL1Feature(String feature) {
		for (XmlNavigationNode childNode : getXmlNavigationTree().getTree()
				.getNode()) {
			if (childNode.getKey().equals(feature)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * XML navigation tree is loaded upon tomcat startup.
	 */
	public static synchronized void loadXmlNavigationTree(String path) {
		try {
			xmlNavigationTree = MgrUtil.unmarshal(path
					+ "WEB-INF/navigation.xml");
			// MgrUtil.marshal(xmlNavigationTree.getRoot());
		} catch (Exception e) {
			log.error("loadXmlNavigationTree", "Load navigation tree failed: ", e);
		}
	}

	public static boolean isExpressHMMode(HmUser user) {
		HmStartConfig startConfig = QueryUtil
				.findBoByAttribute(HmStartConfig.class, "owner", user
						.getDomain());
		return startConfig == null || startConfig.getModeType() == HmStartConfig.HM_MODE_EASY;
	}

	public static boolean isExpressHMMode(HmDomain domain){
		HmStartConfig startConfig = QueryUtil
		.findBoByAttribute(HmStartConfig.class, "owner", domain);
		return startConfig == null || startConfig.getModeType() == HmStartConfig.HM_MODE_EASY;
	}

	public static boolean isLicenseValid(HmUser user) {
		HmDomain hmDom = user.getDomain();
		if (hmDom == null) {
			return true;
		}
		String domainName = hmDom.getDomainName();
		if (HmDomain.HOME_DOMAIN.equals(domainName) || !NmsUtil.isHostedHMApplication()) {
			return HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID == HmBeLicenseUtil.LICENSE_VALID;
		} else {
			LicenseInfo orderInfo = HmBeLicenseUtil.VHM_ORDERKEY_INFO.get(domainName);
			if (null == orderInfo) {
				orderInfo = LicenseOperationTool.getOrderKeyInfoFromDatabase(domainName, false);
			}
			if (BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(orderInfo.getLicenseType())) {
				return false;
			} else if (orderInfo.getTotalDays() > 0 && orderInfo.getLeftHours() <= 0) {
				return false;
			} else if (orderInfo.getHiveAps() <= 0 && orderInfo.getCvgNumber() <= 0 && !orderInfo.isZeroDeviceKeyValid()) {
				return false;
			}
			return true;
		}
	}

	/*
	 * Create customized navigation tree according to user privileges. Also create table to mak
	 * feature names to feature nodes.
	 */
	public static void createNavigationTree(HmUser userContext) {
		NavigationNode tree = createNavigationNode(userContext,
				xmlNavigationTree.getTree(), isExpressHMMode(userContext));
		userContext.setNavigationTree(tree);

		// Map feature strings to their NavigationNode
		Map<String, NavigationNode> featureNodes = new HashMap<String, NavigationNode>();
		createFeatureNodesMap(featureNodes, tree.getChildNodes());
		userContext.setFeatureNodes(featureNodes);

		// cache reboot app node
		rebootApp_node = featureNodes.get(Navigation.L2_FEATURE_REBOOTAPP);

		// Add annotations
		addNavigationAnnotations(tree.getChildNodes());
		initTreeVisibility(tree);

		refreshFeatureDescription(userContext);
	}

	private static NavigationNode rebootApp_node;

	public static final String FEATURENAME_REVERTVERSION = "Revert Software Version";

	private static void refreshFeatureDescription(HmUser user) {
		if (!user.getOwner().isHomeDomain() && rebootApp_node != null) {
			rebootApp_node.setDescription(FEATURENAME_REVERTVERSION);
			rebootApp_node.setShortDescription(FEATURENAME_REVERTVERSION);
		}
	}

	/**
	 * API for switch to vhm user
	 *
	 * @param vhmUser -
	 */
	public static void createNavigationTree4VHM(HmUser vhmUser) {
		HmUser sessionUserContext = BaseAction.getSessionUserContext();

		NavigationNode tree = createNavigationNode(vhmUser, xmlNavigationTree
				.getTree(), isExpressHMMode(vhmUser));
		sessionUserContext.setNavigationTree(tree);

		// Map feature strings to their NavigationNode
		Map<String, NavigationNode> featureNodes = new HashMap<String, NavigationNode>();
		createFeatureNodesMap(featureNodes, tree.getChildNodes());

		// cache reboot app node
		rebootApp_node = featureNodes.get(Navigation.L2_FEATURE_REBOOTAPP);

		sessionUserContext.setFeatureNodes(featureNodes);
		// Add annotations
		addNavigationAnnotations(tree.getChildNodes());
		initTreeVisibility(tree);

		refreshFeatureDescription(vhmUser);
	}

	protected static void createFeatureNodesMap(
			Map<String, NavigationNode> featureNodes, List<NavigationNode> nodes) {
		for (NavigationNode feature : nodes) {
			featureNodes.put(feature.getKey(), feature);
			createFeatureNodesMap(featureNodes, feature.getChildNodes());
		}
	}

	protected static void addNavigationAnnotations(List<NavigationNode> nodes) {
		for (NavigationNode l1Feature : nodes) {
			int rowIndex = 0;
			NavigationNode summaryNode = null;
			for (NavigationNode l2Feature : l1Feature.getChildNodes()) {
				l2Feature.setRowIndex(rowIndex++);
				l2Feature.setChildCount(l2Feature.getChildNodes().size());
				NavigationNode lastl3Node = null;
				for (NavigationNode l3Feature : l2Feature.getChildNodes()) {
					l3Feature.setRowIndex(rowIndex++);
					l3Feature.setChildCount(l3Feature.getChildNodes().size());
					l2Feature.setChildCount(l2Feature.getChildCount()
							+ l3Feature.getChildNodes().size());
					l3Feature.setTreeImage(TREE_GRID_T);
					lastl3Node = l3Feature;
					NavigationNode lastl4Node = null;
					for (NavigationNode l4Feature : l3Feature.getChildNodes()) {
						l4Feature.setRowIndex(rowIndex++);
						l4Feature.setChildCount(l4Feature.getChildNodes()
								.size());
						l3Feature.setChildCount(l3Feature.getChildCount()
								+ l4Feature.getChildNodes().size());
						l2Feature.setChildCount(l2Feature.getChildCount()
								+ l4Feature.getChildNodes().size());
						l4Feature.setTreeImage(TREE_GRID_T);
						lastl4Node = l4Feature;

						NavigationNode lastl5Node = null;
						for (NavigationNode l5Feature : l4Feature
								.getChildNodes()) {
							l5Feature.setRowIndex(rowIndex++);
							l5Feature.setTreeImage(TREE_GRID_T);
							lastl5Node = l5Feature;
						}
						if (lastl5Node != null) {
							lastl5Node.setTreeImage(TREE_GRID_L);
						}
					}
					if (lastl4Node != null) {
						lastl4Node.setTreeImage(TREE_GRID_L);
					}
				}
				if (lastl3Node != null) {
					lastl3Node.setTreeImage(TREE_GRID_L);
				}
				if (l2Feature.isSummary()) {
					summaryNode = l2Feature;
					summaryNode.setChildCount(0);
				} else if (l2Feature.getIndent() > 0) {
					summaryNode.setChildCount(summaryNode.getChildCount()
							- l2Feature.getChildCount() - 1);
				} else {
					summaryNode = null;
				}
			}
		}
	}

	protected static void setStyleClasses(NavigationNode tree) {
		for (NavigationNode l1Feature : tree.getChildNodes()) {
			for (NavigationNode l2Feature : l1Feature.getChildNodes()) {
				l2Feature.setSelectedTree(false);
				if (!l2Feature.getChildNodes().isEmpty()
						&& l2Feature.isCollapsible()) {
					l2Feature.setStyleClass("leftNavH2");
				} else {
					l2Feature.setStyleClass("leftNavL1");
				}
				for (NavigationNode l3Feature : l2Feature.getChildNodes()) {
					l3Feature.setStyleClass("leftNavL1");
					for (NavigationNode l4Feature : l3Feature.getChildNodes()) {
						l4Feature.setStyleClass("leftNavL1");
						for (NavigationNode l5Feature : l4Feature
								.getChildNodes()) {
							l5Feature.setStyleClass("leftNavL1");
						}
					}
				}
			}
		}
	}

	protected static void initTreeVisibility(NavigationNode tree) {
		for (NavigationNode l1Feature : tree.getChildNodes()) {
			for (NavigationNode l2Feature : l1Feature.getChildNodes()) {
				l2Feature.setExpanded(false);
				l2Feature.setRowDisplay("");
				for (NavigationNode l3Feature : l2Feature.getChildNodes()) {
					l3Feature.setExpanded(false);
					if (l2Feature.isCollapsible()) {
						l3Feature.setRowDisplay("display:none;");
					} else {
						l3Feature.setRowDisplay("");
					}
					for (NavigationNode l4Feature : l3Feature.getChildNodes()) {
						l4Feature.setRowDisplay("display:none;");
					}
				}
				if (l2Feature.getIndent() > 0) {
					l2Feature.setRowDisplay("display:none;");
				}
			}
		}
	}

	protected void expandTree(String featureId, boolean expanded) {
		NavigationNode tree = getFeatureNode(featureId);
		if (tree != null) {
			expandTree(tree, expanded);
		}
	}

	protected static void expandTree(NavigationNode tree, boolean expanded) {
		tree.setExpanded(expanded);
		if (tree.isSummary() && tree.getParentNode().getParentNode() == null) {
			boolean start = false;
			for (NavigationNode l2Feature : tree.getParentNode()
					.getChildNodes()) {
				if (start) {
					if (l2Feature.getIndent() > 0) {
						if (expanded) {
							l2Feature.setRowDisplay("");
						} else {
							l2Feature.setRowDisplay("display:none;");
						}
					} else {
						return;
					}
				} else if (l2Feature.getKey().equals(tree.getKey())) {
					start = true;
				}
			}
		} else {
			for (NavigationNode child : tree.getChildNodes()) {
				if (expanded) {
					child.setRowDisplay("");
				} else {
					child.setRowDisplay("display:none;");
				}

				expandTree(child, expanded);
			}
		}
	}

	private static boolean teacherViewEnable;
	private static boolean presenceEnable=true;
	private static boolean enableClientMgt = false;

	/*
	 * Create customized navigation tree according to user privileges.
	 */
	public static NavigationNode createNavigationNode(HmUser user,
			XmlNavigationNode xmlNode, boolean isExpressMode) {
		/*
		 *  To show or not show teacher view L1 menu should depend on the settings of HOME domain.
		 */
		List<?> homeTcPreFlag = QueryUtil.executeQuery("SELECT enableTeacher, presenceEnable FROM " + HMServicesSettings.class.getSimpleName(), null, new FilterParams("owner.domainName", HmDomain.HOME_DOMAIN), (Long) null, 1);
		String domainName = null == user.getSwitchDomain() ? user.getOwner().getDomainName() : user.getSwitchDomain().getDomainName();
		
		List<?> domainFlags = QueryUtil.executeQuery("SELECT enableTeacher, enableClientManagement FROM " + HMServicesSettings.class.getSimpleName(), null, new FilterParams("owner.domainName", domainName), (Long) null, 1);
		
		boolean enableTc = false;
		if (!domainFlags.isEmpty()) {
			Object[] objFlags = (Object[])domainFlags.get(0);
			enableTc = (Boolean)objFlags[0];
			enableClientMgt = (Boolean)objFlags[1];
		}

		if(!homeTcPreFlag.isEmpty()) {
			Object[] objFlags = (Object[])homeTcPreFlag.get(0);

			teacherViewEnable = (Boolean)objFlags[0] && enableTc;
			
			presenceEnable= (Boolean)objFlags[1];
		} else {
			teacherViewEnable = false;
			presenceEnable=false;
		}

		// create node tree
		NavigationNode rootNode = createNavigationNode(xmlNode, false);

		for (XmlNavigationNode l1FeatureXmlNode : xmlNode.getNode()) {
			NavigationNode l1FeatureNode = createNavigationNode(
					l1FeatureXmlNode, false);
			log.debug("createNavigationNode", "L1: "
					+ l1FeatureXmlNode.getDescription());

			createL2Features(user, isExpressMode, l1FeatureXmlNode,
					l1FeatureNode, true);

			if (!l1FeatureNode.getChildNodes().isEmpty()) {
				// Parent node of l1FeatureNode should remain null
				rootNode.addChildNode(l1FeatureNode);
			}
		}
		return rootNode;
	}

	protected static void createL2Features(HmUser user, boolean isExpressMode,
			XmlNavigationNode l1FeatureXmlNode, NavigationNode l1FeatureNode,
			boolean top) {
		// passive node cannot view these features
		if (HAUtil.isSlave() && !(L1_FEATURE_HOME.equals(l1FeatureNode.getKey()) || L1_FEATURE_CONFIGURATION.equals(l1FeatureNode.getKey()))) {
			return;
		}
		for (XmlNavigationNode l2FeatureXmlNode : l1FeatureXmlNode.getNode()) {
			log.debug("createL2Features", "  L2: "
					+ l2FeatureXmlNode.getDescription());
			if (top && l2FeatureXmlNode.isSummary() != null
					&& l2FeatureXmlNode.isSummary()) {
				NavigationNode summaryNode = checkNodePermissions(user,
						l1FeatureNode, l2FeatureXmlNode, isExpressMode, false);
				if (summaryNode != null
						&& L1_FEATURE_CONFIGURATION.equals(l1FeatureNode
								.getKey())) {
					summaryNode.setIndent(-1);
				}

				int num1 = l1FeatureNode.getChildNodes().size();
				createL2Features(user, isExpressMode, l2FeatureXmlNode,
						l1FeatureNode, false);
				int num2 = l1FeatureNode.getChildNodes().size();

				/**
				 * This block code is for summary nodes, we need add summary nodes for those user
				 * group restored from previous version(before 3.4). At normal context, summary node
				 * should always have children, but in this case, summary node maybe have no child.
				 * <yizhou>
				 */
				if (num2 == num1) {
					l1FeatureNode.removeChildNode(summaryNode);
				}
			} else if (l2FeatureXmlNode.getNode().isEmpty()) {
				checkNodePermissions(user, l1FeatureNode, l2FeatureXmlNode,
						isExpressMode, !top);
			} else {
				// Has 3rd level menu options
				NavigationNode l2FeatureNode = createNavigationNode(
						l2FeatureXmlNode, !top);
				boolean l2HeaderNodeFlag = true;

				for (XmlNavigationNode l3FeatureXmlNode : l2FeatureXmlNode
						.getNode()) {
					log.debug("createL2Features", "    L3: "
							+ l3FeatureXmlNode.getDescription());
					if (l3FeatureXmlNode.getNode().isEmpty()) {
						NavigationNode leafNode = checkNodePermissions(user,
								l2FeatureNode, l3FeatureXmlNode, isExpressMode,
								!top);
						if (leafNode != null) {
							leafNode.setSummary(true);
						}
					} else {
						// Has 4th level menu options
						NavigationNode l3FeatureNode = createNavigationNode(
								l3FeatureXmlNode, !top);

						for (XmlNavigationNode l4FeatureXmlNode : l3FeatureXmlNode
								.getNode()) {
							log.debug("createL2Features", "      L4: "
									+ l4FeatureXmlNode.getDescription());

							if (l4FeatureXmlNode.getNode().isEmpty()) {
								checkNodePermissions(user, l3FeatureNode,
										l4FeatureXmlNode, isExpressMode, !top);
							} else {
								// 5th level
								NavigationNode l4FeatureNode = createNavigationNode(
										l4FeatureXmlNode, !top);

								for (XmlNavigationNode l5FeatureXmlNode : l4FeatureXmlNode
										.getNode()) {
									log.debug("createL2Features",
											"      L5: "
													+ l5FeatureXmlNode
															.getDescription());
									checkNodePermissions(user, l4FeatureNode,
											l5FeatureXmlNode, isExpressMode,
											!top);
								}

								if (!l4FeatureNode.getChildNodes().isEmpty()) {
									l4FeatureNode.setParentNode(l3FeatureNode);
									l3FeatureNode.addChildNode(l4FeatureNode);
									l3FeatureNode.setHeaderNode(true);
									l2FeatureNode.setHeaderNode(false);
									l2HeaderNodeFlag = false;
								}
							}
						}

						if (!l3FeatureNode.getChildNodes().isEmpty()) {
							l3FeatureNode.setParentNode(l2FeatureNode);
							l2FeatureNode.addChildNode(l3FeatureNode);

							if (!l3FeatureNode.isHeaderNode()
									&& l2HeaderNodeFlag) {
								l2FeatureNode.setHeaderNode(true);
							}
						}
					}
				}

				if (!l2FeatureNode.getChildNodes().isEmpty()) {
					l2FeatureNode.setParentNode(l1FeatureNode);
					l1FeatureNode.addChildNode(l2FeatureNode);
				}
				if (L2_FEATURE_DEVICES.equals(l2FeatureNode.getKey())) {
					l2FeatureNode.setIndent(-1);
				}
			}
		}
	}

	protected static NavigationNode checkNodePermissions(HmUser user,
			NavigationNode parent, XmlNavigationNode xmlNode,
			boolean isExpressMode, boolean indent) {
		// support express mode and full mode
		if ((xmlNode.isFullModeOnly() != null && xmlNode.isFullModeOnly())
				&& isExpressMode) {
			return null;
		}

		// express mode cannot view ssid profile
		if (xmlNode.getKey().equals(L2_FEATURE_SSID_PROFILES) && !isExpressMode){
			return null;
		}

		// hmonline home domain cannot view license management , clear db , rebootApp
		if ((xmlNode.getKey().equals(L2_FEATURE_LICENSEMGR) || xmlNode.getKey().equals(L2_FEATURE_CLEAR_DB) || xmlNode.getKey().equals(L2_FEATURE_REBOOTAPP))
				&& NmsUtil.isHostedHMApplication() && user.getDomain().isHomeDomain()){
			return null;
		}
		
		//standalone hm express mode cannot view report setting
//		if(isExpressMode && !NmsUtil.isHostedHMApplication() && xmlNode.getKey().equals(L2_FEATURE_REPORTSETTING)){
//			return null;
//		}
				
		// hmonline new vhm cannot view user feature
		if (NmsUtil.isHostedHMApplication() && !user.getOwner().isHomeDomain() && null != user.getCustomerId() && xmlNode.getKey().equals(L2_FEATURE_ADMINISTRATORS)) {
			return null;
		}

		// hm ha passive node
		if (xmlNode.getKey().equals(L2_FEATURE_LICENSEMGR) && HAUtil.isSlave()) {
			return null;
		}

		// hm ha monitor
		if (xmlNode.getKey().equals(L2_FEATURE_HA_MONITORING)){
			if (NmsUtil.isHostedHMApplication()) {
				return null;
			} else {
				List<?> list = QueryUtil.executeQuery("select id from "+HASettings.class.getSimpleName(), null, new FilterParams("haStatus", HASettings.HASTATUS_ENABLE), 1);
				if (list.isEmpty()) {
					return null;
				}
			}
		}

		// passive node cannot view these features
		if (HAUtil.isSlave() && (L2_FEATURE_AUXILIARY_FILE_MGMT.equals(xmlNode.getKey())
				|| L2_FEATURE_MACOUI_DICTIONARY.equals(xmlNode.getKey()) || L2_FEATURE_MIB_FILE.equals(xmlNode.getKey())
				|| L2_FEATURE_RADIUS_DICTIONARY.equals(xmlNode.getKey())
				|| L2_FEATURE_DASHBOARD.equals(xmlNode.getKey()))) {
			return null;
		}
		
		if(NmsUtil.isHostedHMApplication() && xmlNode.getKey().equals(L2_FEATURE_CLOUDAUTH_CUSTOMER_RETRIEVE)) {
		    return null;
		}
		
		// MDM Profiles displayed when enableClientManagement is true
		//HMServicesSettings clientSetting = QueryUtil.findBoByAttribute(HMServicesSettings.class,"owner",user.getDomain());
		//boolean isExpress = isExpressHMMode(user);
		/*if(xmlNode.getKey().equals(L2_FEATURE_MDM_PROFILES) && (isExpress || !clientSetting.isEnableClientManagement()) ) {
			return null;
		}*/
		
		if(xmlNode.getKey().equals(L2_FEATURE_CLIENT_MGMT_TEST) || xmlNode.getKey().equals(L2_FEATURE_CLIENT_CA)) {
			if (!enableClientMgt) {
				return null;
			}
		}
		if(xmlNode.getKey().equals(L2_FEATURE_MDM_PROFILES)){
			return null;
		}
		//Hide Enrolled Client menu when enableClientManagement is false
//		if(xmlNode.getKey().equals(L2_FEATURE_ENROLLED_CLIENTS) && (isExpress || !clientSetting.isEnableClientManagement()) ) {
//			return null;
//		}
		if(xmlNode.getKey().equals(L2_FEATURE_ENROLLED_CLIENTS)){
			return null;
		}
		//Hide CID Clients node when disable ACM
		if(xmlNode.getKey().equals(L2_FEATURE_CID_CLIENTS)) {
			return null;
		}
		
		if(xmlNode.getKey().equals(L2_FEATURE_ONBOARD_UI_SETTING)) {
			return null;
		}
		
		//Hide Client CA Management when disable ACM
//		if(xmlNode.getKey().equals(L2_FEATURE_CLIENT_CA) && (isExpress || !clientSetting.isEnableClientManagement()) ) {
//			return null;
//		}
		
		// check device inventory permission
		// only for VHM customer
		if (L2_FEATURE_DEVICE_INVENTORY.equals(xmlNode.getKey())
				|| L2_FEATURE_DEVICE_INVENTORY_MONITOR.equals(xmlNode.getKey())) {
			if (!NmsUtil.isHostedHMApplication()
					|| user.getOwner().isHomeDomain()) {
				return null;
			}
		}

		//oem settings
		if(xmlNode.getKey().endsWith(L2_FEATURE_OEM_SETTINGS)){
			String str = ConfigUtil.getConfigInfo("oem model", "oem");
			if("false".equals(str))
				return null;
		}
		
		//Supplemental CLI not supported in Express mode and disabled state
		if (xmlNode.getKey().endsWith(L2_FEATURE_CLI_BLOB_SETTINGS) && (isExpressMode || !enableSupplementalCLI(user))){
			return null;
		}
		
		NavigationNode childNode = null;
		HmPermission permission = user.getUserGroup().getFeaturePermissions()
				.get(xmlNode.getKey());
		if (permission != null) {
			if (!extCheckPermission(xmlNode.getKey()))
			{
				return null;
			}

			childNode = createNavigationNode(xmlNode, indent);
			childNode.setParentNode(parent);
			parent.addChildNode(childNode);
		}
		return childNode;
	}
	
	
	private static boolean enableSupplementalCLI(HmUser user){
		if(null != user){
			HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner",user.getSwitchDomain() == null? user.getDomain():user.getSwitchDomain());
			if(null != bo && bo.isEnableSupplementalCLI()){
				return true;
			}
		}
		
		return false;
	}

	// the extension check for feature permission
	private static boolean extCheckPermission(String key) {
		if (key.equals(L2_FEATURE_SUMMARYUSAGE)|| key.equals(L2_FEATURE_DETAILUSAGE)
				|| key.equals(L2_FEATURE_PORTAL_SETTINGS)) {
			return NmsUtil.isHostedHMApplication();
		} else if (key.equals(L2_FEATURE_MIB_FILE) && NmsUtil.isHMForOEM()) {
			return false;
		}else if(key.equals(L2_FEATURE_REPORT_RETAILANALYTICS) && !presenceEnable){
			return false;
		}

		return !(!teacherViewEnable
				&& (key.equals(L2_FEATURE_TV_CLASS) || key.equals(L2_FEATURE_TV_COMPUTERCART)
				|| key.equals(L2_FEATURE_TV_STUDENTROSTER) || key
				.equals(L2_FEATURE_TV_RESOURCEMAP)));
	}

	protected static NavigationNode createNavigationNode(
			XmlNavigationNode xmlNode, boolean indent) {
		NavigationNode node = new NavigationNode(xmlNode.getKey());
		node.setAction(xmlNode.getAction());
		if (xmlNode.isCollapsible() != null) {
			node.setCollapsible(xmlNode.isCollapsible());
		}
		if (xmlNode.isSummary() != null) {
			node.setSummary(xmlNode.isSummary());
		}
		node.setDescription(xmlNode.getDescription());
		if (xmlNode.getShortDescription() == null) {
			node.setShortDescription(xmlNode.getDescription());
		} else {
			node.setShortDescription(xmlNode.getShortDescription());
		}

		if (indent) {
			node.setIndent(8);
		}

		return node;
	}

}