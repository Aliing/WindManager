/**
 *
 */
package com.ah.be.admin.restoredb;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.common.NmsUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmDomain;

/**
 * @author root
 *
 */
public class AhRestoreDBTools
{
	public static String HM_VERSION_FLAG = "webversion";

	public static boolean isNewFrameData = true;

	//public static HmDomain HM_RESTORE_DOMAIN = null;

	public static String   HM_NEW_BACKUP_STRUCT_FLAG = "new_struct_flag";

	public static String HM_XML_TABLE_PATH = BeAdminCentOSTools.ahBackupdir	+ File.separatorChar;

	public static void logRestoreMsg(String strMsg)
	{
		BeLogTools.debug(HmLogConst.M_RESTORE, strMsg);
	}

	public static void logRestoreErrorMsg(String strMsg)
	{
		BeLogTools.restoreLog(BeLogTools.ERROR, strMsg);
	}

	public static void logRestoreMsg(String strMsg,Throwable e)
	{
		BeLogTools.restoreLog(BeLogTools.ERROR, strMsg, e);
	}

	public static boolean isNeedDefaultOrderkey()
	{
		BeVersionInfo oInfo = NmsUtil.getVersionInfo(AhRestoreDBTools.HM_XML_TABLE_PATH+File.separatorChar+".."+File.separatorChar+"hivemanager.ver");

		String strMainVersion = oInfo.getMainVersion();

		if(null == strMainVersion || "".equalsIgnoreCase(strMainVersion))
		{
			//error
			//add log
			BeLogTools.debug(HmLogConst.M_RESTORE, "could not find main version in restore file");

			return false;
		}

		try
		{
			float f = Float.parseFloat(strMainVersion);

			if( f < 3.5)
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "need dafalut orderkey");

				return true;
			}
			else
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "donot need dafalut orderkey");

				return false;
			}

		}
		catch(Exception ex)
		{
			logRestoreMsg("could not parse to float from string", ex);

			return false;
		}
	}

	public void restoreDB()
	{
		File oXmlDir = new File(BeAdminCentOSTools.ahBackupdir);

		if(!oXmlDir.isDirectory())
		{
			return;
		}

		if( !oXmlDir.exists() )
		{
			return;
		}

		String strVersionFlag = BeAdminCentOSTools.ahBackupdir + File.separator + HM_VERSION_FLAG;

		File oFileFlag = new  File(strVersionFlag);

		if(oFileFlag.exists())
		{
			logRestoreMsg("restore new framework begin");

			isNewFrameData = true;

			restoreNewFramework(true);

			return;
		}

		isNewFrameData = false;

		logRestoreMsg("restore old framework begin");

		restoreOldFramework();

		//dispose cache
		AhRestoreNewMapTools.resetCache();
	}

	public void restoreTest()
	{
		File oXmlDir = new File(BeAdminCentOSTools.ahBackupdir);

		if(!oXmlDir.isDirectory())
		{
			return;
		}

		if( !oXmlDir.exists() )
		{
			return;
		}

		String strVersionFlag = BeAdminCentOSTools.ahBackupdir + File.separator + HM_VERSION_FLAG;

		File oFileFlag = new  File(strVersionFlag);

		if(oFileFlag.exists())
		{
			AhRestoreDBImpl.restoreIpAdress();

			return;
		}


	}

	public void restoreNewFramework(boolean bIsRestoreDomain)
	{
		try
		{
			if(bIsRestoreDomain)
			{
			    logRestoreMsg("restore hm_domain begin");
			    BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore hm domain data .........");

			    RestoreAdmin.restoreDomain();
			}
//			} else {
//				// update target domain values
//				RestoreAdmin.restoreDomainExt(null);
//			}

			logRestoreMsg("restore CompliancePolicy begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore CompliancePolicy data .........");
			AhRestoreDBImpl.restoreCompliancePolicy();

			logRestoreMsg("restore EventAndAlarm begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Event and Alarm data .........");
			AhRestoreDBImpl.restoreEventAndAlarm();

			logRestoreMsg("restore map begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore HM Map data .........");
			AhRestoreDBImpl.restoreTopoMap();

			logRestoreMsg("restore Table Routing Profile begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Table Routing Profile data .........");
			AhRestoreDBImpl.restoreRoutingProfiles();

			logRestoreMsg("restore IpAdress begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore IP Adress configuration .........");
			AhRestoreDBImpl.restoreIpAdress();

			logRestoreMsg("restore cwp cert begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Cap Web Portal certificate .........");
			AhRestoreDBImpl.restoreCwpCert();

			logRestoreMsg("restore Vlans begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Vlan configuration .........");
			AhRestoreDBImpl.restoreVlans();

			logRestoreMsg("restore RADIUS Operator-Name Attribute begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore  RADIUS Operator-Name Attribute configuration .........");
			AhRestoreDBImpl.restoreRadiusOperatorNameAttr();

			logRestoreMsg("restore CapWebPortal begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Cap Web Portal configuration .........");
			AhRestoreDBImpl.restoreCapWebPortal();

			logRestoreMsg("restore DosPrevention begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Dos Prevention configuration .........");
			AhRestoreDBImpl.restoreDosPrevention();

			logRestoreMsg("restore Layer3Roaming begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Global Roaming Setting configuration .........");
			AhRestoreDBImpl.restoreLayer3Roaming();

			logRestoreMsg("restore Schedules begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Schedule configuration .........");
			AhRestoreDBImpl.restoreSchedules();

			logRestoreMsg("restore MacAddress begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Mac Address configuration .........");
			AhRestoreDBImpl.restoreMacAddress();
			
			logRestoreMsg("restore RadioProf begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Radio profile configuration .........");
			AhRestoreDBImpl.restoreRadioProf();

			logRestoreMsg("restore OsObject begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore OS Object configuration .........");
			AhRestoreDBImpl.restoreOsObject();

			logRestoreMsg("restore OsVersion begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore OS Version configuration .........");
			AhRestoreDBImpl.restoreOsVersion();

			logRestoreMsg("restore DomainObject begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Device Domain Object configuration .........");
			AhRestoreDBImpl.restoreDomainObject();

			logRestoreMsg("restore Location Client Watch begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Location Client Watch configuration .........");
			AhRestoreDBImpl.restoreLocationClientWatch();

			logRestoreMsg("restore NetworkService begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Network Service configuration .........");
			AhRestoreDBImpl.restoreNetworkService();

			logRestoreMsg("restore UserAttribute begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore User Attribute Group configuration .........");
			AhRestoreDBImpl.restoreUserAttribute();

			logRestoreMsg("restore RateControlAndQueuing begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Rate Control and Queuing configuration .........");
			AhRestoreDBImpl.restoreRateControlAndQueuing();

			logRestoreMsg("restore schedule backup data begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore schedule backup data .........");
			AhRestoreDBImpl.restoreScheduleBackupData();

			logRestoreMsg("restore mail notification begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore mail notification data .........");
			AhRestoreDBImpl.restoreMailNotification();

			logRestoreMsg("restore AlgConfiguration begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore ALG Service configuration .........");
			AhRestoreDBImpl.restoreAlgConfiguration();

			logRestoreMsg("restore RadiusServer begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore AAA Client Setting configuration .........");
			AhRestoreDBImpl.restoreRadiusServer();

			logRestoreMsg("restore RadiusProxy begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore RADIUS Proxy configuration .........");
			AhRestoreDBImpl.restoreRadiusProxy();

			logRestoreMsg("restore ActiveDirOrLdap begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore AD/LDAP Setting configuration .........");
			AhRestoreDBImpl.restoreActiveDirOrLdap();

			logRestoreMsg("restore VlanDhcpServer begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore DHCP Server Setting configuration .........");
			AhRestoreDBImpl.restoreVlanDhcpServer();

			logRestoreMsg("restore MacFilters begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Mac Filter configuration .........");
			AhRestoreDBImpl.restoreMacFilters();

			logRestoreMsg("restore AccessConsole begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Access Console configuration .........");
			AhRestoreDBImpl.restoreAccessConsole();

			logRestoreMsg("restore LLDP/CDP Profiles begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore LLDP/CDP profile configuration .........");
			AhRestoreDBImpl.restoreLLDPCDPProfile();

			logRestoreMsg("restore PPPoE begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore PPPoE configuration .........");
			AhRestoreDBImpl.restorePPPoE();

			logRestoreMsg("restore PseProfile begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore PseProfile configuration .........");
			AhRestoreDBImpl.restorePseProfile();

			logRestoreMsg("restore BonjourServiceCategory begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore BonjourServiceCategory configuration .........");
			AhRestoreDBImpl.restoreBonjourServiceCategory();

			logRestoreMsg("restore BonjourService begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore BonjourService configuration .........");
			AhRestoreDBImpl.restoreBonjourService();

			logRestoreMsg("restore VlanGroup begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore VlanGroup configuration .........");
			AhRestoreDBImpl.restoreVlanGroup();

			logRestoreMsg("restore BonjourGatewayProfile begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore BonjourGatewayProfile configuration .........");
			AhRestoreDBImpl.restoreBonjourGatewayProfile();

			logRestoreMsg("restore user group begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore User Group data .........");
			AhRestoreDBImpl.restoreUsrGroup();

			logRestoreMsg("restore LocationServer begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Location Server configuration .........");
			AhRestoreDBImpl.restoreLocationServer();

			logRestoreMsg("restore IdentityBasedTunnel begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Mobility Policy configuration .........");
			AhRestoreDBImpl.restoreIdentityBasedTunnel();
			
			logRestoreMsg("restore Custom Application begin...");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Custom Application data .........");
			AhRestoreDBImpl.restoreCustomApplication();

			logRestoreMsg("restore restoreIPolicy begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Ip Policy configuration .........");
			AhRestoreDBImpl.restoreIPolicy();

			logRestoreMsg("restore MacPolicy begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Mac Policy configuration .........");
			AhRestoreDBImpl.restoreMacPolicy();

			logRestoreMsg("restore HiveMgtIpFilter begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Mgt Ip Filter configuration .........");
			AhRestoreDBImpl.restoreMgtIpFilter();

			logRestoreMsg("restore HiveMgmtService begin");
			AhRestoreDBImpl.restoreMgmtService();

			logRestoreMsg("restore Hives begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Hive configuration .........");
			AhRestoreDBImpl.restoreHives();

			logRestoreMsg("restore Table DNS Serivces begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Table DNS Serivces data .........");
			AhRestoreDBImpl.restoreDNSServices();

			logRestoreMsg("restore VpnNetwork begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore VpnNetwork configuration .........");
			AhRestoreDBImpl.restoreVpnNetwork();

			logRestoreMsg("restore SubNetworkResource begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore SubNetworkResource configuration .........");
			AhRestoreDBImpl.restoreSubNetworkResource();

			logRestoreMsg("restore restoreQoSMarking begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore QoSMarking mappings .........");
			AhRestoreDBImpl.restoreQoSMarking();
			
			logRestoreMsg("restore UserProfile begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore User Profile configuration .........");
			AhRestoreDBImpl.restoreUserProfile();
			
			logRestoreMsg("restore OpenDNSSettings begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore OpenDNS Settings configuration .........");
			AhRestoreDBImpl.restoreOpenDNS();

			logRestoreMsg("restore VPN Services begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore VPN Services configuration .........");
			AhRestoreDBImpl.restoreVpnServices();

			logRestoreMsg("restore Routing Policies begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Routing Policies configuration .........");
			AhRestoreDBImpl.restoreRoutingPolicies();

			logRestoreMsg("restore Routing Profile Policies begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Routing Profile Policies configuration .........");
			AhRestoreDBImpl.restoreRoutingProfilePolicies();

//			logRestoreMsg("restore DeviceGroupPolicy begin");
//			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Device Group Policy configuration .........");
//			AhRestoreDBImpl.restoreDeviceGroupPolicy();

			logRestoreMsg("restore RadiusUserProfileRule begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore RADIUS User Profile Rule configuration .........");
			AhRestoreDBImpl.restoreRadiusUserProfileRule();

			logRestoreMsg("restore MgmtServiceOption begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Management Option configuration .........");
			AhRestoreDBImpl.restoreMgmtServiceOption();

			logRestoreMsg("restore MgtServiceFilter begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Mgt Service Filter configuration .........");
			AhRestoreDBImpl.restoreMgtServiceFilter();

//			logRestoreMsg("restore restorePrivatePSKGroup begin");
//			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore PrivatePSKGroup configuration .........");
//			AhRestoreDBImpl.restorePrivatePskGroup();
//
//			logRestoreMsg("restore restorePrivatePSK begin");
//			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore PrivatePSK configuration .........");
//			AhRestoreDBImpl.restorePrivatePsk();

			logRestoreMsg("restore LocalUserGroup begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Local User Group configuration .........");
			AhRestoreDBImpl.restoreLocalUserGroup();

			logRestoreMsg("restore LocalUser begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Local user configuration .........");
			AhRestoreDBImpl.restoreLocalUser();

			logRestoreMsg("restore MACAuth begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore MAC Auth configuration .........");
			AhRestoreDBImpl.restoreMacAuth();

			logRestoreMsg("restore RadiusLibrarySip begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Radius Library Sip Policy configuration .........");
			AhRestoreDBImpl.restoreRadiusLibrarySip();

			logRestoreMsg("restore FirewallPolicy begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Firewall Policy configuration .........");
			AhRestoreDBImpl.restoreFirewallPolicy();

			logRestoreMsg("restore PrintTemplate begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Print Template configuration .........");
			AhRestoreDBImpl.restorePrintTemplate();

			logRestoreMsg("restore configtemplatemdm");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore configtemplatemdm data .........");
			AhRestoreDBImpl.restoreConfigTemplateMDM();

			logRestoreMsg("restore Table Port Template&Access Profile begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Port Template&Access Profile data .........");
			AhRestoreDBImpl.restorePortTemplateAndAccess();

			logRestoreMsg("restore restoreSsid begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore SSID configuration .........");
			AhRestoreDBImpl.restoreSsid();
			
			logRestoreMsg("restore restoreQoS begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore QoS mappings .........");
			AhRestoreDBImpl.restoreQoS();

			logRestoreMsg("restore IdsPolicy begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Ids Policy configuration .........");
			AhRestoreDBImpl.restoreIdsPolicy();

			logRestoreMsg("restore EthernetAccess begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Ethernet Access configuration .........");
			AhRestoreDBImpl.restoreEthernetAccess();

			logRestoreMsg("restore SLA Mappings begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore SLA Customized mappings .........");
			AhRestoreDBImpl.restoreSlaMappings();

			logRestoreMsg("restore audit log begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Audit Log data .........");
			AhRestoreDBImpl.restoreAuditLog();
			

			logRestoreMsg("restore system log begin...");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore System Log data .........");
			AhRestoreDBImpl.restoreSystemLog();
			logRestoreMsg("restore system log end.");
			
			logRestoreMsg("restore upgrade log begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Upgrade Log data .........");
			AhRestoreDBImpl.restoreUpgradeLog();
			logRestoreMsg("restore upgrade log end.");
			
			logRestoreMsg("restore HiveApRadius begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore HiveAp AAA Server Setting configuration .........");
			AhRestoreDBImpl.restoreHiveApRadius();

			//restore mstp region
			logRestoreMsg("restore mstp region begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore mstp region .........");
			AhRestoreDBImpl.restoreMstpRegion();

			logRestoreMsg("restore CLIBlob begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore CLIBlob configuration .........");
			AhRestoreDBImpl.restoreCLIBlobs();
			
			logRestoreMsg("restore ConfigurateTepmlate begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Network Policy configuration .........");
			AhRestoreDBImpl.restoreConfigurateTepmlate();

			logRestoreMsg("restore Planned AP begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Planned AP data .........");
			AhRestoreDBImpl.restorePlannedAP();

			/*Added from Chesapeake*/
			logRestoreMsg("restore Forwarding DB begin");
			AhRestoreDBImpl.restoreForwardingDB();

			logRestoreMsg("restore wifi Client preferred SSID");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore wificleint preferred ssid usage data .........");
			AhRestoreDBImpl.restoreWifiClientPreferred();

			logRestoreMsg("restore HiveAP begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore HiveAP data .........");
			AhRestoreDBImpl.restoreHiveAP();

			logRestoreMsg("update VpnService HiveAP VPN Server 1 and Server 2 begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "update VpnService HiveAP VPN Server data .........");
			RestoreVpnService.updateVpnServiceHiveApVpnServer();

			logRestoreMsg("update VpnGatewaySetting HiveApId begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "update VpnGatewaySetting data .........");
			RestoreVpnService.updateVpnGateWaySetting();

			logRestoreMsg("restore Auto Provisioning begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Auto Provisioning data .........");
			AhRestoreDBImpl.restoreAutoProvisionConfig();

			logRestoreMsg("restore OneTime Passwords begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore OneTime Password data .........");
			AhRestoreDBImpl.restoreOneTimePasswords();

			logRestoreMsg("restore HiveAP Update Settings begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore HiveAP Update Setting data .........");
			AhRestoreDBImpl.restoreHiveApUpdateSettings();

			logRestoreMsg("restore Map Settings begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Map Setting data .........");
			AhRestoreDBImpl.restoreMapSettings();

			logRestoreMsg("restore IDP Settings begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore IDP Setting data .........");
			AhRestoreDBImpl.restoreIdpSettings();

			logRestoreMsg("restore Admin Settings begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore HiveManager Setting data .........");
			AhRestoreDBImpl.restoreAdmin();

			logRestoreMsg("restore users begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore HM User data .........");
			AhRestoreDBImpl.restoreHmUsr();

			/*added from dakar_r6*/
			logRestoreMsg("update HM Settings begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "update HM Settings enableRadarDetect data .........");
			RestoreConfigSecurity.updateHmSettingsRadarDetect();

			logRestoreMsg("restore License Server Settings begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore License Server Setting data .........");
			AhRestoreDBImpl.restoreLicenseServerSet();

			logRestoreMsg("restore HiveAp Filter begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore HiveAp Filter data .........");
			AhRestoreDBImpl.restoreHiveApFilter();

			logRestoreMsg("restore USB Modem begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore USB Modem data .........");
			AhRestoreDBImpl.restoreUSBModem();

			logRestoreMsg("adjust ppsk server of ssid profile begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "adjust ppsk server of ssid profile .........");
			AhRestoreDBImpl.adjustSettingOfSsidProfile();

			logRestoreMsg("restore Alarm Filter begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Alarm Filter data .........");
			AhRestoreDBImpl.restoreAhAlarmsFilter();

			logRestoreMsg("restore Event Filter begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Event Filter data .........");
			AhRestoreDBImpl.restoreAhEventsFilter();

			logRestoreMsg("restore Device Reset config begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Device Reset config data .........");
			AhRestoreDBImpl.restoreDeviceResetConfig();
			
			logRestoreMsg("restore Device Inventory begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Device Inventory data .........");
			AhRestoreDBImpl.restoreDeviceInventory();
			
			// only for home domain
//			logRestoreMsg("restore HiveAp Image Info begin");
//			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore HiveAp Image Info data .........");
//			AhRestoreDBImpl.restoreHiveApImageInfo();

			logRestoreMsg("restore Report begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Report data .........");
			AhRestoreDBImpl.restorePerformance();

			//restore default cert
			logRestoreMsg("restore Default Cert File begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Default Cert File data .........");
			Iterator<Map.Entry<Long, HmDomain>> it = AhRestoreNewMapTools.getHmDomainMap().entrySet().iterator();

			while(it.hasNext())
			{
				Map.Entry<Long, HmDomain> entry = (Map.Entry<Long, HmDomain>)it.next();
				HmDomain hmDomain = entry.getValue();
				AhRestoreDBImpl.restoreDefaultCert(hmDomain);
			}

//			logRestoreMsg("restore Hm Express Mode Enable begin");
//		    BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Hm Express Mode Enable data .........");
//		    RestoreAdmin.restoreHmExpressModeEnable();

			logRestoreMsg("restore HM_start config begin");
		    BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore HM start config data .........");
		    RestoreAdmin.restoreHmStartConfig();

			logRestoreMsg("restore Application begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Application configuration .........");
			AhRestoreDBImpl.restoreApplication();

			logRestoreMsg("restore ApplicationProfile begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore ApplicationProfile configuration .........");
			AhRestoreDBImpl.restoreApplicationProfile();

		    logRestoreMsg("update management option of configTemplate begin");
		    BeLogTools.info(HmLogConst.M_SHOWSHELL, "update management option of configTemplate data .........");
		    RestoreConfigTemplate.updateMgmtService();

//		    logRestoreMsg("restore SGE settings begin");
//		    BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore SGE settings data .........");
//		    RestoreAdmin.restoreSGESettings();
//
//		    logRestoreMsg("restore RPC settings begin");
//		    BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore RPC settings data .........");
//		    RestoreAdmin.restoreRPCSettings();

		    // restore Teacher view
		    logRestoreMsg("restore TV Computer Cart begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore TV Computer Cart data .........");
			AhRestoreDBImpl.restoreTvComputerCart();

			logRestoreMsg("restore TV Class begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore TV Class data .........");
			AhRestoreDBImpl.restoreTvClass();

			logRestoreMsg("restore TV Resource Map begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore TV Resource Map data .........");
			AhRestoreDBImpl.restoreTvResourceMap();

			logRestoreMsg("restore TV Student Roster begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore TV Student Roster data .........");
			AhRestoreDBImpl.restoreTvStudentRoster();

			logRestoreMsg("restore Table Column Customization begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Table Column Customization data .........");
			AhRestoreDBImpl.restoreTableColumnCustom();

			// only needed for HMOL and single VHM move/upgrade, fix bug 32249
			if (NmsUtil.isHostedHMApplication() && AhRestoreNewMapTools.isSingleVhmRestore()) {
				AhRestoreDBImpl.getVhmEmails();
			}

			// cchen DONE
			logRestoreMsg("restore Table Column Customization (hm_table_column_new) begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Table Column Customization (new) data .........");
			AhRestoreDBImpl.restoreTableColumnCustomNew();

			logRestoreMsg("restore Table Size Customization (old) begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Table Size Customization (old) data .........");
			AhRestoreDBImpl.restoreTableSize();

			logRestoreMsg("restore Table Size Customization (hm_table_size_new) begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Table Size Customization (new) data .........");
			AhRestoreDBImpl.restoreTableSizeNew();

			logRestoreMsg("restore hm user Customization settings begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore hm user Customization settings data .........");
			AhRestoreDBImpl.restoreHmUserAboutSettings();

			logRestoreMsg("restore hm user Customization settings (hm_autorefresh_settings_new) begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore hm user Customization settings (new) data .........");
			AhRestoreDBImpl.restoreHmUserAboutSettingsNew();

			logRestoreMsg("restore local user group settings (user_localusergroup_new) begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore local user group settings (new) data .........");
			AhRestoreDBImpl.restoreLocalUserGroupNew();

			logRestoreMsg("restore user ssid profile (user_ssidprofile_new) begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore user ssid profile settings (new) data .........");
			AhRestoreDBImpl.restoreUserSsidProfileNew();

			logRestoreMsg("restore hm user settings (hm_user_settings) begin");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore hm user settings (new) data .........");
			AhRestoreDBImpl.restoreHmUserSettings();

			logRestoreMsg("update networks in configTemplate");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "update networks used by Ap .........");
			RestoreConfigTemplate.updateNetworks();

			logRestoreMsg("create default Routing Policy according Vpn service");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "create default Routing Policy data .........");
			RestoreRoutingPolicy.RestoreRoutingPolicyByVpnService();

			logRestoreMsg("restore IDM CustomerID");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore IDM CustomerIDs data .........");
			AhRestoreDBImpl.restoreIDMCustomer();

			logRestoreMsg("restore Dashboard");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Dashboard data .........");
			AhRestoreDBImpl.restoreDashboard();

			logRestoreMsg("restore data retention");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore data retention table data .........");
			AhRestoreDBImpl.restoreDataRetention();

			logRestoreMsg("restore cpu memory usage");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore cpu memory usage data .........");
			AhRestoreDBImpl.restoreCpuMemUsage();

			logRestoreMsg("restore tca alarm");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore tca alarm data .........");
			AhRestoreDBImpl.restoreTCAAlarms();

			logRestoreMsg("restore client profiles");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore client profiles data .........");
			AhRestoreDBImpl.restoreClientProfiles();

			logRestoreMsg("restore AhRouterLTEVZInfo begin...");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore AhRouterLTEVZInfo data .........");
			AhRestoreDBImpl.restoreAhRouterLTEVZInfo();
			logRestoreMsg("restore AhRouterLTEVZInfo end");

			logRestoreMsg("restore Presence Analytics Cusotmer begin...");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Presence Analytics Cusotmer data .........");
			AhRestoreDBImpl.restorePresenceAnalyticsCustomers();
			
			logRestoreMsg("restore Social Analytics begin...");
			BeLogTools.info(HmLogConst.M_SHOWSHELL, "restore Social Analytics data .........");
			AhRestoreDBImpl.restoreGuestAnalytics();
			
		}
		catch(Exception ex)
		{
			AhRestoreDBTools.logRestoreMsg(ex.getMessage());
		}
	}

	public void restoreOldFramework()
	{
		return ;
	}

	public static void main(String[] args)
	{
		AhRestoreDBTools oRestoreTools = new AhRestoreDBTools();

		oRestoreTools.restoreDB();

		//oRestoreTools.restoreTest();
	}
}
