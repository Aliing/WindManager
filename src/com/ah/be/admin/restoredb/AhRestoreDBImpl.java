/**
 *
 */
package com.ah.be.admin.restoredb;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.StringUtils;

import com.ah.be.admin.adminOperateImpl.BeOperateHMCentOSImpl;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmDomain;
import com.ah.ws.rest.client.auth.BasicAuthFilter;
import com.ah.ws.rest.client.utils.PathConstant;
import com.ah.ws.rest.client.utils.PortalResUtils;
import com.ah.ws.rest.models.idm.VHMCustomerInfo;

/**
 * @author root
 */
public class AhRestoreDBImpl
{
	// -----the first functiones to restore begin---

	/**
	 * @description: restore map
	 */
	public static void restoreTopoMap()
	{
		RestoreMapContainer.restoreMapContainer();
	}

	/**
	 * @description: restore plannedAP
	 */
	public static void restorePlannedAP()
	{
		RestorePlannedAP.restorePlannedAP();
	}

	/**
	 * @description: restore IP Address
	 */
	public static boolean restoreIpAdress()
	{
		return RestoreConfigNetwork.restoreIpAdress();
	}

	/**
	 * @description: restore captive web portal
	 */
	public static boolean restoreCapWebPortal()
	{
		return RestoreConfigTemplate.restoreCwp();
	}

	public static void restoreCwpCert()
	{
		RestoreConfigTemplate.restoreCwpCert();
	}

	/**
	 * @description: restore DosPrevention
	 */
	public static boolean restoreDosPrevention()
	{
		return RestoreConfigTemplate.restoreDosPrevention();
	}

	/**
	 * @description: restore Vlans
	 */
	public static boolean restoreVlans()
	{
		return RestoreConfigNetwork.restoreVlan();
	}

	/**
	 * @description: restore Vlans
	 */
	public static boolean restoreRadiusOperatorNameAttr()
	{
		return RestoreConfigNetwork.restoreRadiusOperatorNameAttr();
	}

	/**
	 * @description: restore Layer 3 roaming
	 */
	public static boolean restoreLayer3Roaming()
	{
		return RestoreConfigTemplate.restoreL3Roaming();
	}

	/**
	 * @description: restore Schedules
	 */
	public static void restoreSchedules()
	{
		RestoreSchedule schedule = new RestoreSchedule();
		schedule.saveToDatabase();
	}

	/**
	 * @description: restore Radio Profiles
	 */
	public static boolean restoreRadioProf()
	{
		return RestoreConfigSecurity.restoreRadioProfile();
	}

	/**
	 * @description: restore Mac Address
	 */
	public static boolean restoreMacAddress()
	{
		return RestoreConfigNetwork.restoreMacOrOui();
	}

	public static boolean restoreLocationClientWatch()
	{
		return RestoreConfigNetwork.restoreLocationClientWatch();
	}

	/**
	 * @description: restore Network Service
	 */
	public static boolean restoreNetworkService()
	{
		return RestoreConfigNetwork.restoreNetworkService();
	}

	/**
	 * @description: restore User Attribute
	 */
	public static boolean restoreUserAttribute()
	{
		return RestoreConfigNetwork.restoreUserAttribute();
	}

	/**
	 * @description: restore Rate Control & Queuing
	 */
	public static void restoreRateControlAndQueuing()
	{
		RestoreRateControl oRateControl = new RestoreRateControl();

		oRateControl.saveToDataBase();
	}

	/**
	 * @description: restore the schedule backup data
	 */
	public static void restoreScheduleBackupData()
	{
		RestoreAdmin.restoreScheduleBackup();
	}

	/**
	 * @description: restore the schedule mail Notification
	 */
	public static void restoreMailNotification()
	{
		RestoreAdmin.restoreEmailNotfication();
	}

	/**
	 * @description: restore AlgConfiguration
	 */
	public static boolean restoreAlgConfiguration()
	{
		return RestoreConfigSecurity.restoreAlgConfiguration();
	}

	/**
	 * @description: restore Active Directory and OpenLDAP
	 */
	public static boolean restoreActiveDirOrLdap()
	{
		return RestoreUsersAndAccess.restoreActiveDirOrLdap();
	}

	/**
	 * @description: restore Vlan Dhcp Server
	 */
	public static boolean restoreVlanDhcpServer()
	{
		return RestoreConfigNetwork.restoreVlanDhcpServer();
	}

	/**
	 * @description: restore OsObject
	 */
	public static boolean restoreOsObject()
	{
		return RestoreConfigNetwork.restoreOsObject();
	}

	/**
	 * @description: restore Application
	 */
	public static boolean restoreApplication() {
		return RestoreConfigNetwork.restoreApplication();
	}

	/**
	 * @description: restore ApplicationProfile
	 */
	public static boolean restoreApplicationProfile() {
		return RestoreConfigNetwork.restoreApplicationProfile();
	}


	/**
	 * @description: restore OsVersion
	 */
	public static boolean restoreOsVersion()
	{
		return RestoreConfigNetwork.restoreOsVersion();
	}

	/**
	 * @description: restore DomainObject
	 */
	public static boolean restoreDomainObject()
	{
		return RestoreConfigNetwork.restoreDomainObject();
	}

	// -----the first functiones to restore end---

	// -----the second functiones to restore begin---

	/**
	 * @description: restore the Mac Filters
	 */
	public static void restoreMacFilters()
	{
		RestoreMacFilter filter = new RestoreMacFilter();
		filter.saveToDabatase();
	}

	/**
	 * @description: restore Access Console
	 */
	public static boolean restoreAccessConsole()
	{
		return RestoreConfigNetwork.restoreAccessConsole();
	}

	public static boolean restoreLLDPCDPProfile()
	{
		return RestoreConfigNetwork.restoreLLDPCDPProfile();
	}

	/**
	 * @description: restore PPPoE
	 */
	public static boolean restorePPPoE()
	{
		return RestoreConfigNetwork.restorePPPoE();
	}

	/**
	 * @description: restore PseProfile
	 */
	public static boolean restorePseProfile()
	{
		return RestoreConfigNetwork.restorePseProfile();
	}

	/**
	 * @description: restore Bonjour Service Category
	 */
	public static boolean restoreBonjourServiceCategory()
	{
		return RestoreConfigNetwork.restoreBonjourServiceCategory();
	}

	/**
	 * @description: restore Bonjour Service
	 */
	public static boolean restoreBonjourService()
	{
		return RestoreConfigNetwork.restoreBonjourService();
	}

	/**
	 * @description: restore Vlan Group
	 */
	public static boolean restoreVlanGroup()
	{
		return RestoreConfigNetwork.restoreVlanGroup();
	}

	/**
	 * @description: restore Bonjour Gateway Profile
	 */
	public static boolean restoreBonjourGatewayProfile()
	{
		return RestoreConfigNetwork.restoreBonjourGatewayProfile();
	}

	/**
	 * @description: restore the usr group
	 */
	public static void restoreUsrGroup()
	{
		RestoreAdmin.restoreUserGroup();
	}

	/**
	 * @description: restore Location Server
	 */
	public static boolean restoreLocationServer()
	{
		return RestoreConfigSecurity.restoreLocationServer();
	}

	/**
	 * @description: restore Ethernet Access
	 */
	public static boolean restoreEthernetAccess()
	{
		return RestoreConfigSecurity.restoreEthernetAccess();
	}

	/**
	 * @description: restore SLA Mappings
	 */
	public static boolean restoreSlaMappings(){
		return RestoreConfigSecurity.restoreSlaMappings();
	}

	/**
	 * @description: restore radius server
	 */
	public static boolean restoreRadiusServer()
	{
		return RestoreUsersAndAccess.restoreRadiusAssignment();
	}

	/**
	 * @description: restore Ethernet Access
	 */
	public static void restoreEventAndAlarm()
	{
		 RestoreEventAndAlarm event_alarm = new RestoreEventAndAlarm();
		 event_alarm.restoreEvent();
		 event_alarm.restoreAlarm();

	}

	// -----the second functiones to restore end---

	// -----the third functiones to restore begin---

	/**
	 * @description: restore the ssid
	 */
	public static boolean restoreSsid()
	{
		return RestoreConfigTemplate.restoreSsidProfile();
	}

//	/**
//	 * @description: restore the private PSK
//	 */
//	public static boolean restorePrivatePsk()
//	{
//		return RestoreConfigTemplate.restorePersonalizedPsk();
//	}
//
//	/**
//	 * @description: restore the private PSK Group
//	 */
//	public static boolean restorePrivatePskGroup()
//	{
//		return RestoreConfigTemplate.restorePersonalizedPskGroup();
//	}

	/**
	 * @description: restore the identity based tunnels
	 */
	public static boolean restoreIdentityBasedTunnel()
	{
		return RestoreConfigTemplate.restoreTunnelSetting();
	}

	/**
	 * @description: restore Ip policy
	 */
	public static boolean restoreIPolicy()
	{
		return RestoreConfigSecurity.restoreIpPolicy();
	}

	/**
	 * @desription: restore Mac policies
	 */
	public static boolean restoreMacPolicy()
	{
		return RestoreConfigSecurity.restoreMacPolicy();
	}

	/**
	 * @description: restore mgmt service operation
	 */
	public static void restoreMgmtServiceOption()
	{
		RestoreMgmtService mgmt = new RestoreMgmtService();
		mgmt.saveOperationToDataBase();
	}

	/**
	 * @description: restore the user
	 */
	public static void restoreHmUsr()
	{
		RestoreAdmin.restoreUser();
	}

	/**
	 * @description: restore radius proxy
	 */
	public static boolean restoreRadiusProxy()
	{
		return RestoreUsersAndAccess.restoreRadiusProxy();
	}

	// -----the third functiones to restore end---

	// -----the forth functiones to restore begin---
	/**
	 * @description: restore Mgt Ip Filters
	 */
	public static boolean restoreMgtIpFilter()
	{
		return RestoreConfigTemplate.restoreIpFilter();
	}

	/**
	 * @description: restore syslog
	 */
	public static void restoreMgmtService()
	{
		RestoreMgmtService mgmt = new RestoreMgmtService();
		mgmt.saveToDatabase();
	}

	/**
	 * @description: restore Hives
	 */
	public static boolean restoreHives()
	{
		return RestoreConfigTemplate.restoreHiveProfile();
	}

	/**
	 * @description: restore VpnNetwork
	 */
	public static boolean restoreVpnNetwork()
	{
		return RestoreConfigNetwork.restoreAllVpnNetworks();
	}

	/**
	 * @description: restore SubNetworkResource
	 */
	public static boolean restoreSubNetworkResource()
	{
		return RestoreConfigNetwork.restoreAllSubNetworkResource();
	}

	/**
	 * @description: restore IDS policy
	 */
	public static void restoreIdsPolicy()
	{
		RestoreIdsPolicy.restoreIdsPolicy();
	}

	/**
	 * @description: restore user
	 */
	public static void restoreUserProfile()
	{
		RestoreUserProfile profile = new RestoreUserProfile();
		profile.saveToDatabase();
	}
	
	public static void restoreOpenDNS(){
		RestoreAdmin.restoreOpenDNSAccounts();
		RestoreAdmin.restoreOpenDNSDevices();
		RestoreAdmin.restoreOpenDNSMappings();
	}

	public static boolean restoreRadiusUserProfileRule()
	{
		return RestoreRadiusUserProfileRule.restoreRadiusUserProfileRule();
	}

	/**
	 * @description: restore classification
	 */
	public static void restoreQoS()
	{
		RestoreQoS qos = new RestoreQoS();
		qos.saveToDatabase();
	}

	/**
	 * @description: restore QoSMarking
	 */
	public static void restoreQoSMarking()
	{
		RestoreQoS qos = new RestoreQoS();
		qos.saveToDatabaseForMarking();
	}
	
	/**
	 * @description: restore mgt service filters
	 */
	public static boolean restoreMgtServiceFilter()
	{
		return RestoreConfigTemplate.restoreServiceFilter();
	}

	/**
	 * @description: restore the audit log
	 */
	public static void restoreAuditLog()
	{
		RestoreAdmin.restoreAuditLog();
	}

	public static void restoreSystemLog(){
//		RestoreAdmin.restoreSystemLog();
		RestorePerformance.restoreSystemLog();
	}
	
	public static void restoreUpgradeLog()
	{
		RestorePerformance.restoreUpgradeLog();
	}

	/**
	 * @description: restore local user groups
	 */
	public static boolean restoreLocalUserGroup()
	{
		return RestoreUsersAndAccess.restoreLocalUserGroup();
	}
	// -----the forth functiones to restore end---

	// -----the fifth functiones to restore begin---
	/**
	 * @description: restore local user
	 */
	public static boolean restoreLocalUser()
	{
		return RestoreUsersAndAccess.restoreLocalUser();
	}

	public static boolean restoreMacAuth(){
		return RestoreUsersAndAccess.restoreMACAuth();
	}

	public static boolean restorePrintTemplate()
	{
		return RestoreUsersAndAccess.restorePrintTemplate();
	}

	public static boolean restoreRadiusLibrarySip()
	{
		return RestoreUsersAndAccess.restoreRadiusLibrarySip();
	}

	/**
	 * @description: restore Firewall Policy
	 */
	public static boolean restoreFirewallPolicy()
	{
		return RestoreConfigSecurity.restoreFirewallPolicy();
	}

//	public static boolean restoreDeviceGroupPolicy()
//	{
//		return RestoreConfigSecurity.restoreDeviceGroupPolicy();
//	}

	// -----the fifth functiones to restore end---

	// -----the sixth functiones to restore begin---

	/**
	 * @description: restore HiveAp Radius
	 */
	public static boolean restoreHiveApRadius()
	{
		return RestoreUsersAndAccess.restoreHiveAPRadius();
	}

	public static boolean restoreVpnServices(){
		return RestoreVpnService.restoreVpnServices();
	}

	/**
	 * @description: restore configuration template
	 */
	public static void restoreConfigurateTepmlate()
	{
		RestoreConfigTemplate.restoreTemplates();
	}

	public static void restoreHiveAP()
	{
		RestoreHiveAp.restoreHiveAps();
		BeLogTools.info(HmLogConst.M_COMMON, "update Topology Map data .........");
		RestoreHiveAp.updateTopologyMapInfoGlobal();
		BeLogTools.info(HmLogConst.M_COMMON, "restore Igmp Policy data .........");
    	RestoreIgmp.restoreIgmpPolicy();
    	BeLogTools.info(HmLogConst.M_COMMON, "restore Multicast Group data .........");
    	RestoreIgmp.restoreMulticastGroup();
    	BeLogTools.info(HmLogConst.M_COMMON, "restore Multicast Group Interface data .........");
    	RestoreIgmp.restoreMulticastGroupInterface();
	}

	public static void restoreAutoProvisionConfig()
	{
		RestoreHiveApAutoProvision.restoreHiveApAutoProvision();
	}

	public static void restoreIdpSettings(){
		RestoreIdpSettings.restoreIdpSettings();
	}

	public static void restoreMapSettings()
	{
		RestoreMapSettings.restoreMapSettings();
	}

	public static void restoreHiveApUpdateSettings()
	{
		RestoreHiveApUpdateSettings.restoreHiveApUpdateSettings();
	}

	public static void restorePresenceAnalyticsCustomers()
	{
		RestorePresenceAnalyticsCustomer.restorePresenceAnalyticsCustomers();
	}

	public static void restoreDashboard()
	{
		AhRestoreDBTools.logRestoreMsg("restore AhDashboard");
		RestoreDashboard.restoreAhDashboard();
		AhRestoreDBTools.logRestoreMsg("restore AhDashboardMetric");
		RestoreDashboard.restoreAhDashboardMetric();
		AhRestoreDBTools.logRestoreMsg("restore AhDashboardComponent");
		RestoreDashboard.restoreAhDashboardComponent();
		AhRestoreDBTools.logRestoreMsg("restore AhDashboardLayout");
		RestoreDashboard.restoreAhDashboardLayout();
		AhRestoreDBTools.logRestoreMsg("restore AhDashboardWidget");
		RestoreDashboard.restoreAhDashboardWidget();

	}

	// -----the sixth functiones to restore end---

	// -----restore the performance begin---
	public static void restorePerformance()
	{
		AhRestoreDBTools.logRestoreMsg("Start restore performance data");
		long start = System.currentTimeMillis();

		RestorePerformance.restoreClientEditResults();

		AhRestoreDBTools.logRestoreMsg("Restore xif data");
//		RestorePerformance.restoreAhXIf();
		RestorePerformance.restoreAhXIfExt();

		AhRestoreDBTools.logRestoreMsg("Restore radio attributes");
//		RestorePerformance.restoreRadioAttribute();
		RestorePerformance.restoreRadioAttributeExt();

		AhRestoreDBTools.logRestoreMsg("Restore vif statistics data");
//		RestorePerformance.restoreVifStats();
		RestorePerformance.restoreVifStatsExt();

		AhRestoreDBTools.logRestoreMsg("Restore radio statistics data");
//		RestorePerformance.restoreRadioStats();
		RestorePerformance.restoreRadioStatsExt();

		AhRestoreDBTools.logRestoreMsg("Restore association data");
//		RestorePerformance.restoreAssociation();
		RestorePerformance.restoreAssociationExt();

		AhRestoreDBTools.logRestoreMsg("Restore history client data");
//		RestorePerformance.restoreHistoryClientSession();
		RestorePerformance.restoreHistoryClientExt();

		AhRestoreDBTools.logRestoreMsg("Restore neighbor data");
//		RestorePerformance.restoreNeighbor();
		RestorePerformance.restoreNeighborExt();

		AhRestoreDBTools.logRestoreMsg("Restore ah summary page data");
		RestorePerformance.restoreAhSummaryPage();

		AhRestoreDBTools.logRestoreMsg("Restore report data");
		RestorePerformance.restoreAhReport();

		AhRestoreDBTools.logRestoreMsg("Restore custom report data");
		RestorePerformance.restoreAhCustomReport();

		AhRestoreDBTools.logRestoreMsg("Restore user report data");
		RestorePerformance.restoreAhUserReport();

		AhRestoreDBTools.logRestoreMsg("Restore new network usage report data");
		RestorePerformance.restoreAhNewReport();

		RestorePerformance.restoreActiveClientFilter();

		AhRestoreDBTools.logRestoreMsg("Restore interference statistics data");
		RestorePerformance.restoreInterferenceStats();

		AhRestoreDBTools.logRestoreMsg("Restore acsp neighbor data");
		RestorePerformance.restoreACSPNeighbor();

		AhRestoreDBTools.logRestoreMsg("Restore bandwidth history data");
		RestorePerformance.restoreBandWidthHistory();

		AhRestoreDBTools.logRestoreMsg("Restore pci data");
		RestorePerformance.restorePCIData();

		AhRestoreDBTools.logRestoreMsg("Restore admin login session data");
		RestorePerformance.restoreAdminLoginSession();

		AhRestoreDBTools.logRestoreMsg("Restore max client history data");
		RestorePerformance.restoreAhMaxClientCountData();

		AhRestoreDBTools.logRestoreMsg("Restore ssid client count history data");
		RestorePerformance.restoreAhSsidClientCountData();

		AhRestoreDBTools.logRestoreMsg("Restore osinfo count history data");
		RestorePerformance.restoreAhClientOsInfoCountData();
		
		AhRestoreDBTools.logRestoreMsg("Restore reboot count history data");
		RestorePerformance.restoreAhDeviceRebootHistoryCountData();

		AhRestoreDBTools.logRestoreMsg("Restore SLA stats data");
		RestorePerformance.restoreAhSLAStats();

		AhRestoreDBTools.logRestoreMsg("Restore ap connection history data");
		RestorePerformance.restoreApConnectionHistory();

		AhRestoreDBTools.logRestoreMsg("Restore interface statistics data");
		RestorePerformance.restoreInterfaceStats();

		AhRestoreDBTools.logRestoreMsg("Restore clients statistics data");
		RestorePerformance.restoreClientStats();

		AhRestoreDBTools.logRestoreMsg("Restore new sla statistics data");
		RestorePerformance.restoreNewSLAStats();

		AhRestoreDBTools.logRestoreMsg("Restore application report data");
		RestorePerformance.restoreAppData();

		AhRestoreDBTools.logRestoreMsg("Restore application flow data");
		RestorePerformance.restoreAppFlow();

		AhRestoreDBTools.logRestoreMsg("Restore device statistics data");
		RestorePerformance.restoreDeviceStats();

		AhRestoreDBTools.logRestoreMsg("Restore Switch Port Period Stats data");
		RestorePerformance.restoreAhSwitchPortPeriodStats();

		AhRestoreDBTools.logRestoreMsg("Restore Report BackEnd Rollup data");
		RestorePerformance.restoreReportRollupTables( );

		AhRestoreDBTools.logRestoreMsg("Finish restore performance data");
		AhRestoreDBTools.logRestoreMsg("Restore performance cost time(s):"
			+ (System.currentTimeMillis() - start) / 1000);
	}

	// ------restore the performance end----
	// ------restore the admin begin--------
	public static void restoreAdmin()
	{
		RestoreAdmin.restoreHMServicesSettings();
		// move only happen when in home
		//RestoreAdmin.restoreCapwapSettings();
		//RestoreAdmin.restoreLogSettings();
		RestoreAdmin.restoreLoginAuth();
		//RestoreAdmin.restoreNTPInfo();
		//RestoreAdmin.restoreHmAcl();
		//RestoreAdmin.restoreCapwapClient();
		//move finished
		//RestoreAdmin.restoreLicenseHistoryInfo();
		
		// user register info in license server for entitlement key
		RestoreOrderKey.restoreUserRegisterInfo();
	}

	public static void restoreAdminOnlyForHome()
	{
		RestoreAdmin.restoreUpdateSoftwareInfo();
		RestoreAdmin.restoreCapwapSettings();
		RestoreAdmin.restoreLogSettings();
		//RestoreAdmin.restoreLoginAuth();
		RestoreAdmin.restoreNTPInfo();
		RestoreAdmin.restoreHmAcl();
		RestoreAdmin.restoreCapwapClient();

		// hiveap image download info
		AhRestoreDBTools.logRestoreMsg("restore HiveAp Image Info begin");
		BeLogTools.info(HmLogConst.M_COMMON, "restore HiveAp Image Info data .........");
		RestoreHiveApUpdateSettings.restoreHiveApImageInfo();

		AhRestoreDBTools.logRestoreMsg("restore Hm Express Mode Enable begin");
	    BeLogTools.info(HmLogConst.M_COMMON, "restore Hm Express Mode Enable data .........");
	    RestoreAdmin.restoreHmExpressModeEnable();

	    AhRestoreDBTools.logRestoreMsg("restore SGE settings begin");
	    BeLogTools.info(HmLogConst.M_COMMON, "restore SGE settings data .........");
	    RestoreAdmin.restoreSGESettings();

	    AhRestoreDBTools.logRestoreMsg("restore RPC settings begin");
	    BeLogTools.info(HmLogConst.M_COMMON, "restore RPC settings data .........");
	    RestoreAdmin.restoreRPCSettings();
	}
	// ------restore the admin end---------

	public static void restoreHiveApFilter()
	{
		RestoreConfigTemplate.restoreHiveApFilter();
	}

	public static void restoreUSBModem()
	{
		RestoreConfigTemplate.restoreUSBModem();
	}

	public static void adjustSettingOfSsidProfile()
	{
		RestoreConfigTemplate.adjustPpskServerOfSsidProfile();
	}

	public static void restoreAhAlarmsFilter()
	{
		RestoreConfigTemplate.restoreAhAlarmsFilter();
	}

	public static void restoreAhEventsFilter()
	{
		RestoreConfigTemplate.restoreAhEventsFilter();
	}

	public static void restoreDeviceResetConfig()
	{
		RestoreAdditionalBo.restoreDeviceResetConfig();
	}
	
	public static void restoreDeviceInventory()
	{
		RestoreAdditionalBo.restoreDeviceInventory();
	}
	
	public static void restoreCompliancePolicy()
	{
		RestoreCompliancePolicy.restoreCompliancePolicy();
	}

    // restore license server setting
	public static void restoreLicenseServerSet()
	{
		RestoreLicenseAndActivation.restoreLicenseServerSetting();
	}

	//restore default
	public static void restoreDefaultCert(HmDomain oDomain)
	{
		BeOperateHMCentOSImpl.createDefaultDomainCERT(oDomain.getDomainName());
	}

	//resotre teacher view
	public static void restoreTvStudentRoster(){
		RestoreTeacherView.restoreTvStudentRoster();
	}
	public static void restoreTvComputerCart(){
		RestoreTeacherView.restoreTvComputerCart();
	}
	public static void restoreTvClass(){
		RestoreTeacherView.restoreTvClass();
	}
	public static void restoreTvResourceMap(){
		RestoreTeacherView.restoreTvResourceMap();
	}

	/*
	 * restore table column customization
	 */
	public static void restoreTableColumnCustom() {
		RestoreConfigTemplate.restoreTableColumnCustom();
	}

	// cchen DONE
	// restore table column (from 6.1r3) customization
	public static void restoreTableColumnCustomNew() {
		RestoreConfigTemplate.restoreTableColumnCustomNew();
	}

	/*
	 * restore table size (from 6.1r3) customization
	 * fix bug.
	 */
	public static void restoreTableSize() {
		RestoreConfigTemplate.restoreTableSize();
	}

	/*
	 * restore table size (from 6.1r3) customization
	 */
	public static void restoreTableSizeNew() {
		RestoreConfigTemplate.restoreTableSizeNew();
	}

	/*
	 * restore table DNS services
	 */
	public static void restoreDNSServices() {
		RestoreConfigNetwork.restoreDNSServices();
	}

	/*
	 * restore table Routing Profiles
	 */
	public static void restoreRoutingProfiles() {
		RestoreConfigNetwork.restoreRoutingProfiles();
	}

	/*
	 * restore table LAN Profiles
	 */
	public static void restoreLANProfiles() {
		//RestoreConfigTemplate.restoreLANProfile();
	}

	/*
	 * restore setting of hm user
	 */
	public static void restoreHmUserAboutSettings() {
		RestoreAdmin.restoreHmUserAboutSettings();
	}

	/*
	 * restore setting of hm user (new)
	 */
	public static void restoreHmUserAboutSettingsNew() {
		RestoreAdmin.restoreHmUserAboutSettingsNew();
	}

	/*
	 * restore local user group (new)
	 */
	public static void restoreLocalUserGroupNew() {
		RestoreAdmin.restoreLocalUserGroupNew();
	}

	/*
	 * restore user ssid profile (new)
	 */
	public static void restoreUserSsidProfileNew() {
		RestoreAdmin.restoreUserSsidProfileNew();
	}

	/*
	 * restore hm user settings (new)
	 */
	public static void restoreHmUserSettings() {
		RestoreAdmin.restoreHmUserSettings();
	}

	public static void restoreOneTimePasswords(){
		RestoreOneTimePassword.restoreOneTimePasswords();
	}

	public static void restoreRoutingPolicies(){
		RestoreRoutingPolicy.restoreRoutingPolicies();
	}

	public static void restoreRoutingProfilePolicies() {
		RestoreRoutingProfilePolicy.restore();
	}

	public static void restoreRoutingPolicieByVpnService(){
		RestoreRoutingPolicy.RestoreRoutingPolicyByVpnService();
	}

    public static void restoreIDMCustomer() {
        RestoreIDMCustomer.restoreCustomers();
    }

	public static boolean restoreWifiClientPreferred(){
		return RestoreConfigNetwork.restoreWifiClientPreferredSsid();
	}

    public static void restoreDataRetention(){
    	AhRestoreDBTools.logRestoreMsg("restore ClientDeviceInfo begin");
		BeLogTools.info(HmLogConst.M_COMMON, "restore ClientDeviceInfo data .........");
//    	RestoreUserClientDevice.restoreClientDeviceInfos();
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
        xmlParser.convertXMLfile("client_device_info");
        String finalTableName = "convert_client_device_info";
		AhDBRestoreTool.restoreConvertTable(finalTableName, "client_device_info");
    	AhRestoreDBTools.logRestoreMsg("restore NetworkDeviceHistory begin");
		BeLogTools.info(HmLogConst.M_COMMON, "restore NetworkDeviceHistory data .........");
//    	RestoreApTopAndProfileChangeHistory.restoreNetworkDeviceHistory();
    	RestorePerformance.restoreNetworkDeviceHistory();
    }

    public static void restoreCpuMemUsage(){
    	AhRestoreDBTools.logRestoreMsg("restore CpuMemoryUsage begin");
		BeLogTools.info(HmLogConst.M_COMMON, "restore CpuMemoryUsage data .........");
    	RestoreCpuMemoryUsage.restoreCpuMemoryUsage();
    }

    public static void restoreTCAAlarms(){
    	AhRestoreDBTools.logRestoreMsg("restore TCAAlarms begin");
		BeLogTools.info(HmLogConst.M_COMMON, "restore TCAAlarms data .........");
		RestoreTCAAlarms.restoreTCAAlarms();
    }

    public static void restoreClientProfiles(){
    	AhRestoreDBTools.logRestoreMsg("restore Client Profiles begin");
		BeLogTools.info(HmLogConst.M_COMMON, "restore Client Profiles data .........");
    	RestoreClientProfiles.restoreClientProfiles();
    }

    public static void restoreConfigTemplateMDM(){
    	AhRestoreDBTools.logRestoreMsg("restore ConfigTemplateMDM begin");
		BeLogTools.info(HmLogConst.M_COMMON, "restore ConfigTemplateMDM data .........");
    	RestoreConfigTemplateMDM.restoreTCAAlarms();
    }

    public static boolean restoreForwardingDB(){
		return RestoreForwardingDB.restoreForwardingDB();
	}

    public static void restorePortTemplateAndAccess() {
        BeLogTools.info(HmLogConst.M_COMMON, "restore Old LAN data .........");
        RestoreWiredPortTemplate.restoreLANProfile();
        BeLogTools.info(HmLogConst.M_COMMON, "restore Port Access data .........");
        RestoreWiredPortTemplate.restorePortAccessProfile();
        BeLogTools.info(HmLogConst.M_COMMON, "restore Port Template data .........");
        RestoreWiredPortTemplate.restorePortTemplate();
    }

    public static void restoreMstpRegion() throws Exception {
        BeLogTools.info(HmLogConst.M_COMMON, "restore Mstp Region data .........");
        RestoreConfigNetwork.restoreMstpRegion();
    }

    public static void restoreAhRouterLTEVZInfo(){
    	BeLogTools.info(HmLogConst.M_COMMON, "restore AhRouterLTEVZInfo data .........");
    	RestoreAhRouterLTEVZInfo.restoreAhRouterLTEVZInfo();
    }
    
    public static void restoreCustomApplication(){
    	BeLogTools.info(HmLogConst.M_COMMON, "restore CustomApplication data .........");
    	RestoreCustomApplication.restoreCustomApplication();
    }
    
    private static VHMCustomerInfo getVhmInfos(String vhmId) {
    	
    	String username = "hmol@portal";
		String password = "";
    	try {
			PropertiesConfiguration config = new PropertiesConfiguration("/etc/secretkey");
			password = config.getString("portal.rest.key.hmol@portal");
		} catch (ConfigurationException e1) {
			AhRestoreDBTools.logRestoreErrorMsg("Retrieve VHM ("+vhmId+") info from Portal, get REST API cretential failed." + e1.getMessage());
			password = "aerohive";
		}
		PortalResUtils portalResUtils = PortalResUtils.getInstance(
				username, password.getBytes(BasicAuthFilter.CHARACTER_SET));
		VHMCustomerInfo vhminfo = null;
		try {
			vhminfo = portalResUtils.getVHMCustomerInfo(vhmId);
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreErrorMsg("Retrieve VHM ("+vhmId+") info from Portal (call API "+ PathConstant.POR_VHM_CUSTOMERINFORMATION_PATH+") error." + e.getMessage());
		}
		return vhminfo;
	}
    
    public static void getVhmEmails() {
    	// only needed for HMOL and single VHM move/upgrade, fix bug 32249
		/*
		 * before restore user customization settings, check if need to retrieve VHM user emails from Portal
		 * customization settings include data in below 4 tables:
		 * hm_table_column_new
		 * hm_table_size_new
		 * hm_autorefresh_settings_new
		 * hm_user_settings
		 */
    	// get VHM BO
		HmDomain domain = AhRestoreNewMapTools.getHmDomainMap().isEmpty() ? null : (AhRestoreNewMapTools.getHmDomainMap().values()).iterator().next();
		if (domain != null) {
			String vhmId = domain.getVhmID();
			VHMCustomerInfo vhminfo = getVhmInfos(vhmId);
			AhRestoreDBTools.logRestoreMsg("Retrieve VHM ("+vhmId+") info from Portal " + (vhminfo == null ? "failed." : "suceed.") + " (first time retrieval)");
			
			// 3 times retry
			for (int i = 0; vhminfo == null && i < 3; i++) {
				vhminfo = getVhmInfos(vhmId);
				AhRestoreDBTools.logRestoreMsg("Retrieve VHM ("+vhmId+") info from Portal " + (vhminfo == null ? "failed." : "suceed.") + "(retry retrieval, times:" + (i+1) + ")");
			}
			
			AhRestoreDBTools.logRestoreMsg("VHM info : " + ToStringBuilder.reflectionToString(vhminfo, ToStringStyle .MULTI_LINE_STYLE));
			
			if (vhminfo != null) {
				// if VHM got CID, use user get from Portal as filter
				if (!StringUtils.isEmpty(vhminfo.getCustomerId())) {
					// clear user email
					AhRestoreNewMapTools.clearVhmEmails();
					
					AhRestoreNewMapTools.addVhmEmails(vhminfo.getPrimaryUsers());
					AhRestoreNewMapTools.addVhmEmails(vhminfo.getNonPrimaryUsers());
				} else {
					// got no CID, use email list saved when restore hm_user on HMOL
				}
			} else {
				// make sure restore all customization settings, clear email list saved when restore hm_user
				AhRestoreNewMapTools.clearVhmEmails();
				AhRestoreDBTools.logRestoreErrorMsg("Retrieve VHM ("+vhmId+") info from Portal failed after 3 times retry. Restore all customization settings from source HMOL.");
			}
		} else {
			// make sure restore all customization settings, clear email list saved when restore hm_user
			AhRestoreNewMapTools.clearVhmEmails();
			AhRestoreDBTools.logRestoreErrorMsg("Cannot start Retrieving VHM info from Portal due to get VHM record from HMOL DB failed. Restore all customization settings from source HMOL.");
		}
	}

    public static void restoreGuestAnalytics() {
        RestoreGuestAnalytics.restoreGuestAnalytics();
    }
    
    public static void restoreCLIBlobs(){
    	RestoreCLIBlob cliBlob = new RestoreCLIBlob();
    	cliBlob.saveToDatabase();
    }
}
