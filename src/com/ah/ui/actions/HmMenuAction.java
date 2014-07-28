package com.ah.ui.actions;

import java.util.List;

import com.ah.bo.wlan.SsidProfile;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/*
 * @author Chris Scheers
 */

public class HmMenuAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(HmMenuAction.class
			.getSimpleName());

	public String navigationTree() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		if ("track".equals(operation)) {
			log.info("navigationTree", "Page load for " + pageOp + ": "
					+ loadTime + "ms.");
		} else {
			log.info("navigationTree", "Node key: " + key);
			expandTree(key, "expand".equals(operation));
		}
		return "json";
	}

	public String getJSONString() {
		return "{}";
	}

	private String pageOp;

	int loadTime;

	public void setLoadTime(int loadTime) {
		this.loadTime = loadTime;
	}

	public void setPageOp(String pageOp) {
		this.pageOp = pageOp;
	}

	public String topMenu() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		
		/*
		 * clear last opened feature in express configuration guide
		 */
		if(isEasyMode()) {
			removeLastExConfigGuide();
			removeExConfigGuideFeature();
			MgrUtil.removeSessionAttribute(SsidProfile.class.getSimpleName() + "Source");
		}
		
		if (getL1Feature(operation) != null) {
			// Top nav feature
			clearSearchResult();
			removeSessionAttributes();
			return operation;
		} else {
			// Default is Hive AP Configuration
			if(isTeacherView()) {
				return L1_FEATURE_HOME;
			} else {
				if (isHMOnline()) {
					return L1_FEATURE_CONFIGURATION;
				}
				return L1_FEATURE_DASH;
			}
			
		}
	}

	public String hiveTopMenuItem(String l1Feature, String defaultL2Feature,
			String[] supported) throws Exception {
		if (operation == null) {
			operation = defaultL2Feature;
		}
		if (isL2Feature(operation, l1Feature)) {
			setSelectedL2Feature(operation);
		}
		if (getSelectedL2Feature() != null) {
			if (MgrUtil.contains(supported, operation)) {
				// Left nav feature
				return operation;
			} else {
				return "placeHolder";
			}
		}
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		boolean l1FeatureChanged = false;
		setSelectedL1Feature(l1Feature);
		if (getSelectedL1Feature() == null) {
			log.info("hiveTopMenuItem(getSelectedL1Feature()==null)", "l1Feature="+l1Feature+";defaultL2Feature="+defaultL2Feature+";operation="+operation);
			// Look for L1 feature that the user has access to
			setAccessibleL1Feature();
			l1FeatureChanged = true;
		}
		if (getSelectedL1Feature() != null) {
			if(l1FeatureChanged){
				return getSelectedL1Feature().getKey();
			}
			List<NavigationNode> childNodes = getSelectedL1Feature()
					.getChildNodes();
			if (childNodes.size() > 0) {
				NavigationNode childNode = childNodes.get(0);
				operation = childNode.getKey();
				// if the selected feature is item under hivemanager operation,
				// try to select admin group item instead of hivemanager
				// operation.
				if (L2_FEATURE_HM_OPERATIONS.equals(operation)
						&& childNodes.size() > 1) {
					for (NavigationNode child : childNodes) {
						if (L2_FEATURE_ADMIN_MGMT.equals(child.getKey())) {
							childNode = child;
							operation = child.getKey();
							break;
						}
					}
				}
				while (childNode.getChildNodes().size() > 0) {
					childNode = childNode.getChildNodes().get(0);
					operation = childNode.getKey();
				}
				return hiveTopMenuItem(getSelectedL1Feature().getKey(),
						defaultL2Feature, supported);
			}
		}
		return "placeHolder";
	}

	public String userMgrMenu() throws Exception {
		return hiveTopMenuItem(L1_FEATURE_USER_MGR, L2_FEATURE_UM_TEMP_CREATE,
				new String[] { L2_FEATURE_UM_TEMP_CREATE,
						L2_FEATURE_UM_TEMP_REVOKE, L2_FEATURE_UM_PERM,
						L2_FEATURE_UM_TEMP_PRINT });
	}

	public String userReportMenu() throws Exception {
		return hiveTopMenuItem(L1_FEATURE_USER_REPORTS, L2_FEATURE_UP_USER_DAY,
				new String[] { L2_FEATURE_UP_USER_DAY, L2_FEATURE_UP_SESS_DAY,
						L2_FEATURE_UP_SESS_TIME_DAY, L2_FEATURE_UP_SESS_NAS,
						L2_FEATURE_GM_CLIENTMONITOR });
	}

	public String homeMenu() throws Exception {
		return hiveTopMenuItem(L1_FEATURE_HOME, L2_FEATURE_USER_PASSWORD_MODIFY,
				new String[] { L2_FEATURE_START_HERE,
						L2_FEATURE_SYSTEM_OVERVIEW,L2_FEATURE_CID_CLIENTS,
						L2_FEATURE_USER_PASSWORD_MODIFY,
						L2_FEATURE_ADMINISTRATION, L2_FEATURE_RESTART_SOFTWARE,
						L2_FEATURE_REBOOTAPP, L2_FEATURE_SHUTDOWN_APPLIANCE,
						L2_FEATURE_CLEAR_DB, L2_FEATURE_BACKUP_DB,
						L2_FEATURE_RESTORE_DB, L2_FEATURE_UPDATE_SOFTWARE,
						L2_FEATURE_SUPPORT_BUNDLE, L2_FEATURE_ADMIN_GROUPS,
						L2_FEATURE_ADMINISTRATORS, L2_FEATURE_SYSTEMLOG,
						L2_FEATURE_AUDITLOG, L2_FEATURE_UPGRADELOG,L2_FEATURE_KDDRLOG,
						L2_FEATURE_HM_SETTINGS, L2_FEATURE_HM_SERVICES,
						L2_FEATURE_MIB_FILE, L2_FEATURE_RADIUS_DICTIONARY,
						L2_FEATURE_MACOUI_DICTIONARY, L2_FEATURE_LICENSEMGR,
						L2_FEATURE_VHMMANAGEMENT, L2_FEATURE_MAILLIST,L2_FEATURE_PORTAL_SETTINGS,
						L2_FEATURE_HA_MONITORING,L2_FEATURE_OEM_SETTINGS});
	}
	
	public String dashMenu() throws Exception {
		return hiveTopMenuItem(L1_FEATURE_DASH, L2_FEATURE_DASHBOARD,
				new String[] { L2_FEATURE_DASHBOARD});
	}

	public String monitorMenu() throws Exception {
		return hiveTopMenuItem(L1_FEATURE_MONITOR, L2_FEATURE_MANAGED_HIVE_APS,
				new String[] { L2_FEATURE_MANAGED_HIVE_APS, L2_FEATURE_FRIENDLY_APS,
						L2_FEATURE_ROGUE_APS, L2_FEATURE_HIVEAP_UPDATE_RESULTS,
						L2_FEATURE_CLIENTMONITOR, L2_FEATURE_ROGUECLIENT,
						L2_FEATURE_ENROLLED_CLIENTS,
						L2_FEATURE_CLIENTMODIFICATIONS,
						L2_FEATURE_CLIENTSURVEY,
						L2_FEATURE_LOCATIONCLIENTWATCH, L2_FEATURE_EVENTS,
						L2_FEATURE_ALARMS, 
						L2_FEATURE_L3FIREWALLLOG,
						L2_FEATURE_BRANCH_ROUTERS,
						L2_FEATURE_SWITCHES,
						L2_FEATURE_VPN_GATEWAYS, L2_FEATURE_DEVICE_HIVEAPS,
						L2_FEATURE_ONETIMEPASSWORD,
						L2_FEATURE_DEVICE_INVENTORY_MONITOR,
						L2_FEATURE_SUBNETWORK_ALLOCATIONS, L2_FEATURE_WIREDCLIENT,
						L2_FEATURE_WIRELESSCLIENT,L2_FEATURE_BONJOUR_GATEWAY_MONITORING

						});

	}
	
	public String rpMenu() throws Exception {
		return hiveTopMenuItem(L1_FEATURE_REPORT, L2_FEATURE_REPORT_RECURREPORT,
				new String[] {L2_FEATURE_HIVEAPREPORT,
						L2_FEATURE_REPORT_RECURREPORT,
						L2_FEATURE_REPORT_RETAILANALYTICS,
						L2_FEATURE_SSIDREPORT, L2_FEATURE_CLIENTREPORT,
						L2_FEATURE_CHANNELPOWERNOISE, L2_FEATURE_RADIOAIRTIME,
						L2_FEATURE_RADIOTRAFFICMETRICS,
						L2_FEATURE_RADIOTROUBLESHOOTING,
						L2_FEATURE_RADIOINTERFERENCE, L2_FEATURE_SSIDAIRTIME,
						L2_FEATURE_SSIDTRAFFICMETRICS,
						L2_FEATURE_SSIDTROUBLESHOOTING,
						L2_FEATURE_MOSTCLIENTSAPS, L2_FEATURE_CLIENTSESSION,
						L2_FEATURE_CLIENTCOUNT, L2_FEATURE_CLIENTAIRTIME,
						L2_FEATURE_UNIQUECLIENTCOUNT, L2_FEATURE_CLIENTAUTH,
						L2_FEATURE_CLIENTVENDOR, L2_FEATURE_SECURITYROGUEAPS,
						L2_FEATURE_SECURITYROGUECLIENTS,
						L2_FEATURE_REPORTSETTING,
						L2_FEATURE_SECURITYCOMPLIANCE,
						L2_FEATURE_SECURITYPCICOMPLIANCE,L2_FEATURE_SECURITY_NONHIVEAP,
						L2_FEATURE_SECURITY_NONCLIENT,
						L2_FEATURE_MESHNEIGHBORS, L2_FEATURE_INVENTORY,
						L2_FEATURE_HIVEAPSLA, L2_FEATURE_CLIENTSLA,
						L2_FEATURE_CUSTOMREPORT,L2_FEATURE_SUMMARYUSAGE,
						L2_FEATURE_DETAILUSAGE,L2_FEATURE_MAXCLIENTREPORT,
						L2_FEATURE_HIVEAPCONNECTION,
						L2_FEATURE_REPORT_WIREDBRREPORTS,
						L2_FEATURE_REPORT_WIREDCVGREPORTS,
						L2_FEATURE_REPORT_VPNBRREPORTS,
						L2_FEATURE_REPORT_VPNCVGREPORTS,
						L2_FEATURE_REPORT_VPNTHROUGHPUT,
						L2_FEATURE_REPORT_VPNLATENCY,
						L2_FEATURE_REPORT_VPNAVAILABILITY,
						L2_FEATURE_REPORT_WANTHROUGHPUT,
						L2_FEATURE_REPORT_WANAVAILABILITY,
						L2_FEATURE_REPORT_GWVPNAVAILABILITY,
						L2_FEATURE_REPORT_GWWANTHROUGHPUT,
						L2_FEATURE_REPORT_GWWANAVAILABILITY
						});

	}

	public String configurationMenu() throws Exception {
		if (operation != null
				&& (operation.equals(L2_FEATURE_ADD_HIVEAP) || operation
						.equals(L2_FEATURE_UPDATE_CONFIG))) {
			setSelectedL1Feature(L1_FEATURE_MONITOR);
		}

		/*
		 * clear last opened feature in express configuration guide
		 */
		if(isEasyMode()) {
			removeLastExConfigGuide();
			removeExConfigGuideFeature();
			MgrUtil.removeSessionAttribute(SsidProfile.class.getSimpleName() + "Source");
		}

		return hiveTopMenuItem(L1_FEATURE_CONFIGURATION,
				L2_FEATURE_CONFIGURATION_GUIDE, new String[] {
						L2_FEATURE_CONFIGURATION_GUIDE, L2_FEATURE_MAC_FILTERS,
						L2_FEATURE_IP_ADDRESS, L2_FEATURE_QOS_CLASSIFICATION,
						L2_FEATURE_QOS_MARKING, L2_FEATURE_QOS_RATE_CONTROL,
						L2_FEATURE_HIVE_PROFILES, 
						L2_FEATURE_SSID_PROFILES_FULL,
						L2_FEATURE_SSID_PROFILES,
						L2_FEATURE_MAC_DOS, L2_FEATURE_IP_DOS,
						L2_FEATURE_SCHEDULER, L2_FEATURE_MAC_OR_OUI,
						L2_FEATURE_CAPTIVE_PORTAL_WEB, L2_FEATURE_IP_POLICY,
						L2_FEATURE_CWP_PAGE_CUSTOMIZATION,
						L2_FEATURE_NETWORK_SERVICE, L2_FEATURE_VLAN,L2_FEATURE_VLAN_GROUP,
						L2_FEATURE_WIFICLIENT_PREFERRED_SSID,
						L2_FEATURE_MSTP_REGION,
						L2_FEATURE_INTER_SUBNET_ROAMING, L2_FEATURE_IDS_POLICY,
						L2_FEATURE_CONFIG_MDM,
						L2_FEATURE_MDM_PROFILES,
						L2_FEATURE_LOCAL_USER_GROUP, L2_FEATURE_USER_PROFILE,
						L2_FEATURE_RADIUS_USER_PROFILE_RULE,
						L2_FEATURE_MGMT_SERVICE_DNS, L2_FEATURE_ADD_HIVEAP,
						L2_FEATURE_MGMT_SERVICE_SYSLOG,
						L2_FEATURE_UPDATE_CONFIG, L2_FEATURE_ONBOARD_UI_SETTING,L2_FEATURE_MGMT_SERVICE_SNMP,
						L2_FEATURE_MGMT_SERVICE_TIME,
						L2_FEATURE_IDENTITY_BASED_TUNNELS,
						L2_FEATURE_MGMT_SERVICE_OPTION, L2_FEATURE_LOCAL_USER,
						L2_FEATURE_PERSONALIZED_PSK,
						L2_FEATURE_PERSONALIZED_PSK_GROUP,
						L2_FEATURE_USER_PROFILE_ATTRIBUTE,
						L2_FEATURE_MAC_POLICY, L2_FEATURE_RADIUS_SERVER_ASSIGN,
						L2_FEATURE_IP_FILTERS, L2_FEATURE_SERVICE_FILTERS,
						L2_FEATURE_RADIO_PROFILE,
						L2_FEATURE_RADIUS_SERVER_HIVEAP,
						L2_FEATURE_ALG_CONFIGURATION,
						L2_FEATURE_ETHERNET_ACCESS, L2_FEATURE_LOCATION_SERVER,
						L2_FEATURE_RADIUS_ACTIVE_DIRECTORY,
						L2_FEATURE_CONFIGURATION_TEMPLATE,
						L2_FEATURE_HIVEAP_FILE,
						L2_FEATURE_QOS_CLASSFIER_AND_MARKER,
						L2_FEATURE_SERVERCSR, L2_FEATURE_CERTIFICATES,L2_FEATURE_CLIENT_CA,
						L2_FEATURE_CWPCERTMGMT, L2_FEATURE_HIVEMANAGERCA,
						L2_FEATURE_SSHKEYSGEN, L2_FEATURE_MGMT_IP_TRACKING,
						L2_FEATURE_HIVEAP_AUTO_PROVISIONING,
						L2_FEATURE_ACCESS_CONSOLE, L2_FEATURE_VLAN_DHCP_SERVER,
						L2_FEATURE_LLDPCDP_PROFILE, L2_FEATURE_VPN_SERVICE,
						L2_FEATURE_VPN_NETWORK,
						L2_FEATURE_COMPLIANCE_POLICY,
						L2_FEATURE_AIR_SCREEN_RULE,
						L2_FEATURE_AIR_SCREEN_RULE_GROUP,L2_FEATURE_PSE_PROFILE,
						L2_FEATURE_RADIUS_SERVER_PROXY, L2_FEATURE_RADIUS_LIBRARY_SIP,
						L2_FEATURE_OS_OBJECT, L2_FEATURE_DOMAIN_OBJECT,
						L2_FEATURE_DNS_SERVICE,L2_FEATURE_PPPOE,L2_FEATURE_BONJOUR_GATEWAY_SETTINGS,L2_FEATURE_CLI_BLOB_SETTINGS,
						L2_FEATURE_L3_FIREWALL_POLICY,L2_FEATURE_USB_MODEM,
						L2_FEATURE_PORTTYPE,L2_FEATURE_L3_RADIUSATTR,L2_FEATURE_ROUTING_POLICY,L2_FEATURE_ROUTING_PROFILE_POLICY,
						L2_FEATURE_CONFIG_HIVE_APS,L2_FEATURE_CONFIG_VPN_GATEWAYS,
						L2_FEATURE_CONFIG_BRANCH_ROUTERS,L2_FEATURE_CONFIG_DEVICE_HIVEAPS,
						L2_FEATURE_CONFIG_SWITCHES,
						L2_FEATURE_DEVICE_INVENTORY,
						L2_FEATURE_HIVEAP_UPDATE_RESULTS, L2_FEATURE_APP_PROFILE, L2_FEATURE_APPLICATION_SERVICE});
	}

	public String toolsMenu() throws Exception {
		return hiveTopMenuItem(L1_FEATURE_TOOLS, L2_FEATURE_PLANNING_TOOL,
				new String[] { L2_FEATURE_PLANNING_TOOL,
						L2_FEATURE_HM_SIMULATOR,
						L2_FEATURE_CLIENT_CONNECTION_MONITOR,L2_FEATURE_CLIENT_MGMT_TEST,
						L2_FEATURE_PACKET_CAPTURE, L2_FEATURE_VLAN_PROBE,
						L2_FEATURE_RADIUS_TEST, L2_FEATURE_ADLDAP_TEST, 
						L2_FEATURE_LIBRARY_SIP_TEST,L2_FEATURE_IDM_TEST,
						L2_FEATURE_SPECTRAL_ANALYSIS,
						L2_FEATURE_CLOUDAUTH_CUSTOMER_RETRIEVE});
	}
	
	public String teacherViewMenu() throws Exception {
		return hiveTopMenuItem(L1_FEATURE_TEACHERVIEW, L2_FEATURE_TV_GUIDE,
				new String[] { L2_FEATURE_TV_GUIDE,
				L2_FEATURE_TV_CLASS,
				L2_FEATURE_TV_COMPUTERCART,
				L2_FEATURE_TV_STUDENTROSTER,
				L2_FEATURE_TV_RESOURCEMAP,
				L2_FEATURE_TV_SCHEDULEMAP}); 
	}
}