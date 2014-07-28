package com.ah.util;

/*
 * @author Chris Scheers
 */

import java.util.Arrays;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.jmx.StatisticsService;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import com.ah.be.admin.adminOperateImpl.BeOperateHMCentOSImpl;
import com.ah.be.search.ColumnTarget;
import com.ah.be.search.EntityTarget;
import com.ah.be.search.FieldTarget;
import com.ah.be.search.Target;
import com.ah.bo.admin.AcmEntitleKeyHistoryInfo;
import com.ah.bo.admin.ActivationKeyInfo;
import com.ah.bo.admin.AhScheduleBackupData;
import com.ah.bo.admin.AirtightSettings;
import com.ah.bo.admin.CapwapClient;
import com.ah.bo.admin.CapwapSettings;
import com.ah.bo.admin.DomainOrderKeyInfo;
import com.ah.bo.admin.GuestAnalyticsInfo;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAccessControl;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmAutoRefresh;
import com.ah.bo.admin.HmClassifierTag;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmExpressModeEnable;
import com.ah.bo.admin.HmL3FirewallLog;
import com.ah.bo.admin.HmLocalUserGroup;
import com.ah.bo.admin.HmLoginAuthentication;
import com.ah.bo.admin.HmNtpServerAndInterval;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmTableSize;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.admin.HmUserSettings;
import com.ah.bo.admin.HmUserSsidProfile;
import com.ah.bo.admin.LicenseHistoryInfo;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.admin.MailNotification4VHM;
import com.ah.bo.admin.OpenDNSAccount;
import com.ah.bo.admin.OpenDNSDevice;
import com.ah.bo.admin.OpenDNSMapping;
import com.ah.bo.admin.OrderHistoryInfo;
import com.ah.bo.admin.PlanToolConfig;
import com.ah.bo.admin.RemoteProcessCallSettings;
import com.ah.bo.admin.UserRegInfoForLs;
import com.ah.bo.cloudauth.CloudAuthCustomer;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.dashboard.AhDashboardAppAp;
import com.ah.bo.dashboard.AhDashboardLayout;
import com.ah.bo.dashboard.AhDashboardWidget;
import com.ah.bo.dashboard.DashboardComponent;
import com.ah.bo.dashboard.DashboardComponentMetric;
import com.ah.bo.gml.PrintTemplate;
import com.ah.bo.hhm.DenyUpgradeEmailSuffix;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.bo.hhm.HhmUpgradeVersionInfo;
import com.ah.bo.hhm.HmolUpgradeServerInfo;
import com.ah.bo.hhm.SyncTaskOnHmol;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.DeviceIPSubNetwork;
import com.ah.bo.hiveap.DeviceInventory;
import com.ah.bo.hiveap.DeviceResetConfig;
import com.ah.bo.hiveap.DeviceStpSettings;
import com.ah.bo.hiveap.DownloadInfo;
import com.ah.bo.hiveap.ForwardingDB;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.hiveap.HiveApFilter;
import com.ah.bo.hiveap.HiveApImageInfo;
import com.ah.bo.hiveap.HiveApSerialNumber;
import com.ah.bo.hiveap.HiveApUpdateResult;
import com.ah.bo.hiveap.HiveApUpdateSettings;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.hiveap.IdpSettings;
import com.ah.bo.hiveap.LSevenSignatures;
import com.ah.bo.hiveap.MdmProfiles;
import com.ah.bo.hiveap.WifiClientPreferredSsid;
import com.ah.bo.igmp.IgmpPolicy;
import com.ah.bo.igmp.MulticastGroup;
import com.ah.bo.igmp.MulticastGroupInterface;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.InterRoaming;
import com.ah.bo.mobility.QosClassfierAndMarker;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.mobility.TunnelSetting;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.monitor.ClientDeviceInfo;
import com.ah.bo.monitor.CpuMemoryUsage;
import com.ah.bo.monitor.DeviceDaInfo;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.monitor.LocationRssiReport;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.monitor.MapSettings;
import com.ah.bo.monitor.NetworkDeviceHistory;
import com.ah.bo.monitor.NetworkDeviceReport;
import com.ah.bo.monitor.OneTimePassword;
import com.ah.bo.monitor.PlannedAP;
import com.ah.bo.monitor.Trex;
import com.ah.bo.network.AccessConsole;
import com.ah.bo.network.AirScreenAction;
import com.ah.bo.network.AirScreenBehavior;
import com.ah.bo.network.AirScreenRule;
import com.ah.bo.network.AirScreenRuleGroup;
import com.ah.bo.network.AirScreenSource;
import com.ah.bo.network.AlgConfiguration;
import com.ah.bo.network.Application;
import com.ah.bo.network.ApplicationProfile;
import com.ah.bo.network.BonjourGatewayMonitoring;
import com.ah.bo.network.BonjourGatewaySettings;
import com.ah.bo.network.BonjourRealm;
import com.ah.bo.network.BonjourService;
import com.ah.bo.network.BonjourServiceCategory;
import com.ah.bo.network.CLIBlob;
import com.ah.bo.network.CompliancePolicy;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.DnsServiceProfile;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.FirewallPolicy;
import com.ah.bo.network.IdsPolicy;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.IpFilter;
import com.ah.bo.network.IpPolicy;
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.MacPolicy;
import com.ah.bo.network.MstpRegion;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.OsVersion;
import com.ah.bo.network.PPPoE;
import com.ah.bo.network.PseProfile;
import com.ah.bo.network.RadiusAttrs;
import com.ah.bo.network.RoutingPolicy;
import com.ah.bo.network.RoutingProfile;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.StpSettings;
import com.ah.bo.network.SubNetworkResource;
import com.ah.bo.network.SwitchSettings;
import com.ah.bo.network.USBModem;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VlanDhcpServer;
import com.ah.bo.network.VlanGroup;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnService;
import com.ah.bo.notificationmsg.NotificationMessageStatus;
import com.ah.bo.performance.APConnectHistoryInfo;
import com.ah.bo.performance.ActiveClientFilter;
import com.ah.bo.performance.AhACSPNeighbor;
import com.ah.bo.performance.AhAdminLoginSession;
import com.ah.bo.performance.AhAlarmsFilter;
import com.ah.bo.performance.AhAppDataHour;
import com.ah.bo.performance.AhAppDataSeconds;
import com.ah.bo.performance.AhAppFlowDay;
import com.ah.bo.performance.AhAppFlowLog;
import com.ah.bo.performance.AhAppFlowMonth;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhBandWidthSentinelHistory;
import com.ah.bo.performance.AhClientEditValues;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhClientSessionHistory;
import com.ah.bo.performance.AhClientStats;
import com.ah.bo.performance.AhClientStatsDay;
import com.ah.bo.performance.AhClientStatsHour;
import com.ah.bo.performance.AhClientStatsWeek;
import com.ah.bo.performance.AhClientsOsInfoCount;
import com.ah.bo.performance.AhClientsOsInfoCountDay;
import com.ah.bo.performance.AhClientsOsInfoCountHour;
import com.ah.bo.performance.AhClientsOsInfoCountWeek;
import com.ah.bo.performance.AhCustomReport;
import com.ah.bo.performance.AhCustomReportField;
import com.ah.bo.performance.AhDevicePSEPower;
import com.ah.bo.performance.AhDeviceRebootHistory;
import com.ah.bo.performance.AhDeviceStats;
import com.ah.bo.performance.AhEventsFilter;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.AhInterfaceStatsDay;
import com.ah.bo.performance.AhInterfaceStatsHour;
import com.ah.bo.performance.AhInterfaceStatsWeek;
import com.ah.bo.performance.AhInterferenceStats;
import com.ah.bo.performance.AhLLDPInformation;
import com.ah.bo.performance.AhLatestACSPNeighbor;
import com.ah.bo.performance.AhLatestInterferenceStats;
import com.ah.bo.performance.AhLatestNeighbor;
import com.ah.bo.performance.AhLatestRadioAttribute;
import com.ah.bo.performance.AhLatestXif;
import com.ah.bo.performance.AhMaxClientsCount;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhNewReport;
import com.ah.bo.performance.AhNewSLAStats;
import com.ah.bo.performance.AhNewSLAStatsDay;
import com.ah.bo.performance.AhNewSLAStatsHour;
import com.ah.bo.performance.AhNewSLAStatsWeek;
import com.ah.bo.performance.AhPCIData;
import com.ah.bo.performance.AhPSEStatus;
import com.ah.bo.performance.AhPortAvailability;
import com.ah.bo.performance.AhRadioAttribute;
import com.ah.bo.performance.AhRadioStats;
import com.ah.bo.performance.AhReport;
import com.ah.bo.performance.AhReportAppDataHour;
import com.ah.bo.performance.AhReportAppDataSeconds;
import com.ah.bo.performance.AhReportCompliance;
import com.ah.bo.performance.AhRouterLTEVZInfo;
import com.ah.bo.performance.AhSLAStats;
import com.ah.bo.performance.AhSpectralAnalysis;
import com.ah.bo.performance.AhSsidClientsCount;
import com.ah.bo.performance.AhSsidClientsCountDay;
import com.ah.bo.performance.AhSsidClientsCountHour;
import com.ah.bo.performance.AhSsidClientsCountWeek;
import com.ah.bo.performance.AhStatsAvailabilityHigh;
import com.ah.bo.performance.AhStatsAvailabilityLow;
import com.ah.bo.performance.AhStatsLatencyHigh;
import com.ah.bo.performance.AhStatsLatencyLow;
import com.ah.bo.performance.AhStatsThroughputHigh;
import com.ah.bo.performance.AhStatsThroughputLow;
import com.ah.bo.performance.AhStatsVpnStatusHigh;
import com.ah.bo.performance.AhStatsVpnStatusLow;
import com.ah.bo.performance.AhSummaryPage;
import com.ah.bo.performance.AhSwitchPortInfo;
import com.ah.bo.performance.AhSwitchPortPeriodStats;
import com.ah.bo.performance.AhSwitchPortStats;
import com.ah.bo.performance.AhUserLoginSession;
import com.ah.bo.performance.AhUserReport;
import com.ah.bo.performance.AhVIfStats;
import com.ah.bo.performance.AhVPNStatus;
import com.ah.bo.performance.AhXIf;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.report.PresenceAnalyticsCustomer;
import com.ah.bo.tca.TCAAlarm;
import com.ah.bo.teacherView.TvClass;
import com.ah.bo.teacherView.TvComputerCart;
import com.ah.bo.teacherView.TvResourceMap;
import com.ah.bo.teacherView.TvScheduleMap;
import com.ah.bo.teacherView.TvStudentRoster;
import com.ah.bo.teacherView.ViewingClass;
import com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.LocationServer;
import com.ah.bo.useraccess.MACAuth;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.MgmtServiceSyslog;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusLibrarySip;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.useraccess.RadiusUserProfileRule;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.bo.useraccess.UserProfileVlanMapping;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.CwpCertificate;
import com.ah.bo.wlan.CwpPageCustomization;
import com.ah.bo.wlan.EthernetAccess;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SlaMappingCustomize;
import com.ah.bo.wlan.SsidProfile;
import com.ah.common.bo.ReportRollupRecord;
import com.ah.common.bo.apcpumemory.ReportCpuMemoryDate;
import com.ah.common.bo.apcpumemory.ReportCpuMemoryHour;
import com.ah.common.bo.apcpumemory.ReportCpuMemoryMonth;
import com.ah.common.bo.apcpumemory.ReportCpuMemoryWeek;
import com.ah.common.bo.appdata.ReportAllAppLastWeek;
import com.ah.common.bo.appdata.ReportAppDataDate;
import com.ah.common.bo.appdata.ReportAppDataMonth;
import com.ah.common.bo.appdata.ReportAppDataWeek;
import com.ah.common.bo.clientcount.ReportClientCountDate;
import com.ah.common.bo.clientcount.ReportClientCountHour;
import com.ah.common.bo.clientcount.ReportClientCountMonth;
import com.ah.common.bo.clientcount.ReportClientCountWeek;
import com.ah.common.bo.clientdata.ReportClientDataDate;
import com.ah.common.bo.clientdata.ReportClientDataHour;
import com.ah.common.bo.clientdata.ReportClientDataMonth;
import com.ah.common.bo.clientdata.ReportClientDataWeek;
import com.ah.common.bo.hmcpumemory.ReportHMCpuMemoryDate;
import com.ah.common.bo.hmcpumemory.ReportHMCpuMemoryHour;
import com.ah.common.bo.hmcpumemory.ReportHMCpuMemoryMonth;
import com.ah.common.bo.hmcpumemory.ReportHMCpuMemoryWeek;
import com.ah.common.bo.network.ReportNetworkErrorDate;
import com.ah.common.bo.network.ReportNetworkErrorHour;
import com.ah.common.bo.network.ReportNetworkErrorMonth;
import com.ah.common.bo.network.ReportNetworkErrorWeek;
import com.ah.common.bo.newslastats.ReportNewSLAStatsDate;
import com.ah.common.bo.newslastats.ReportNewSLAStatsHour;
import com.ah.common.bo.newslastats.ReportNewSLAStatsMonth;
import com.ah.common.bo.newslastats.ReportNewSLAStatsWeek;
import com.ah.common.bo.portnetwork.ReportPortNetworkDate;
import com.ah.common.bo.portnetwork.ReportPortNetworkHour;
import com.ah.common.bo.portnetwork.ReportPortNetworkMonth;
import com.ah.common.bo.portnetwork.ReportPortNetworkWeek;
import com.ah.common.bo.ssid.ReportSSIDCountDate;
import com.ah.common.bo.ssid.ReportSSIDCountMonth;
import com.ah.common.bo.ssid.ReportSSIDCountWeek;
import com.ah.common.bo.switchport.ReportSwitchPortPeriodDate;
import com.ah.common.bo.switchport.ReportSwitchPortPeriodHour;
import com.ah.common.bo.switchport.ReportSwitchPortPeriodMonth;
import com.ah.common.bo.switchport.ReportSwitchPortPeriodWeek;
import com.ah.common.schedule.v2.RowupJob;
import com.ah.common.schedule.v2.RowupLog;
import com.ah.common.schedule.v2.RowupStatus;

public class HibernateUtil {

	private static final Tracer log = new Tracer(HibernateUtil.class.getSimpleName());

	private static final Configuration	configuration;

	private static EntityManagerFactory	entityManagerFactory;

	private static final boolean		useEntityManagerFactory	= true;

	private static SessionFactory		sessionFactory;

	private static final boolean		useSessionFactory		= false;

	static {
		try {
			configuration = new Configuration();
			/*
			 * Only Entity objects need to be in this list.
			 */
			configuration.addAnnotatedClass(HiveProfile.class);
			configuration.addAnnotatedClass(MacFilter.class);
			configuration.addAnnotatedClass(MacOrOui.class);
			configuration.addAnnotatedClass(OsObject.class);
			configuration.addAnnotatedClass(OsVersion.class);
			configuration.addAnnotatedClass(RoutingProfile.class);
			configuration.addAnnotatedClass(DomainObject.class);
			configuration.addAnnotatedClass(ConfigTemplateMdm.class);
			configuration.addAnnotatedClass(MapContainerNode.class);
			configuration.addAnnotatedClass(MapLeafNode.class);
			configuration.addAnnotatedClass(MapNode.class);
			configuration.addAnnotatedClass(PlannedAP.class);
			configuration.addAnnotatedClass(HmUser.class);
			configuration.addAnnotatedClass(HmUserGroup.class);
			configuration.addAnnotatedClass(QosClassification.class);
			configuration.addAnnotatedClass(QosMarking.class);
			configuration.addAnnotatedClass(QosRateControl.class);
			configuration.addAnnotatedClass(NetworkService.class);
			configuration.addAnnotatedClass(Vlan.class);
			configuration.addAnnotatedClass(VlanGroup.class);
			configuration.addAnnotatedClass(RadiusAssignment.class);
			configuration.addAnnotatedClass(LocalUserGroup.class);
			configuration.addAnnotatedClass(LocalUser.class);
			configuration.addAnnotatedClass(MACAuth.class);
			configuration.addAnnotatedClass(OpenDNSDevice.class);
			configuration.addAnnotatedClass(UserProfileAttribute.class);
			configuration.addAnnotatedClass(UserProfile.class);
			configuration.addAnnotatedClass(OpenDNSAccount.class);
			configuration.addAnnotatedClass(OpenDNSMapping.class);
			configuration.addAnnotatedClass(RadiusUserProfileRule.class);
			configuration.addAnnotatedClass(HiveAp.class);
			configuration.addAnnotatedClass(IpAddress.class);
			configuration.addAnnotatedClass(RadiusAttrs.class);
			configuration.addAnnotatedClass(IpPolicy.class);
			configuration.addAnnotatedClass(MacPolicy.class);
		//	configuration.addAnnotatedClass(DevicePolicy.class);
			configuration.addAnnotatedClass(SsidProfile.class);
			configuration.addAnnotatedClass(Scheduler.class);
			configuration.addAnnotatedClass(Cwp.class);
			configuration.addAnnotatedClass(CwpPageCustomization.class);
			configuration.addAnnotatedClass(RadioProfile.class);
			configuration.addAnnotatedClass(DosPrevention.class);
			configuration.addAnnotatedClass(AhAlarm.class);
			configuration.addAnnotatedClass(AhEvent.class);
			configuration.addAnnotatedClass(InterRoaming.class);
			configuration.addAnnotatedClass(IdsPolicy.class);
			configuration.addAnnotatedClass(TunnelSetting.class);
			configuration.addAnnotatedClass(IpFilter.class);
			configuration.addAnnotatedClass(ServiceFilter.class);
			configuration.addAnnotatedClass(MgmtServiceDns.class);
			configuration.addAnnotatedClass(MgmtServiceSyslog.class);
			configuration.addAnnotatedClass(MgmtServiceSnmp.class);
			configuration.addAnnotatedClass(MgmtServiceTime.class);
			configuration.addAnnotatedClass(MgmtServiceOption.class);
			configuration.addAnnotatedClass(ConfigTemplate.class);
			configuration.addAnnotatedClass(RadiusLibrarySip.class);
			configuration.addAnnotatedClass(RadiusOnHiveap.class);
			configuration.addAnnotatedClass(AhScheduleBackupData.class);
			configuration.addAnnotatedClass(HmAuditLog.class);
			configuration.addAnnotatedClass(HmSystemLog.class);
			configuration.addAnnotatedClass(HmUpgradeLog.class);
			configuration.addAnnotatedClass(HmL3FirewallLog.class);
			configuration.addAnnotatedClass(HiveApUpdateSettings.class);
			configuration.addAnnotatedClass(HiveApUpdateResult.class);
			configuration.addAnnotatedClass(MailNotification.class);
			configuration.addAnnotatedClass(MailNotification4VHM.class);
			configuration.addAnnotatedClass(AhAssociation.class);
			configuration.addAnnotatedClass(AhNeighbor.class);
			configuration.addAnnotatedClass(AhRadioStats.class);
			configuration.addAnnotatedClass(AhVIfStats.class);
			configuration.addAnnotatedClass(AhXIf.class);
			configuration.addAnnotatedClass(AhRadioAttribute.class);
			configuration.addAnnotatedClass(AhClientSession.class);
			configuration.addAnnotatedClass(AhClientEditValues.class);
			configuration.addAnnotatedClass(AhClientSessionHistory.class);
			configuration.addAnnotatedClass(AhACSPNeighbor.class);
			configuration.addAnnotatedClass(AhInterferenceStats.class);
			configuration.addAnnotatedClass(CapwapSettings.class);
			configuration.addAnnotatedClass(AlgConfiguration.class);
			configuration.addAnnotatedClass(EthernetAccess.class);
			configuration.addAnnotatedClass(LocationServer.class);
			configuration.addAnnotatedClass(LogSettings.class);
			configuration.addAnnotatedClass(HMServicesSettings.class);
			configuration.addAnnotatedClass(QosClassfierAndMarker.class);
			configuration.addAnnotatedClass(ActiveDirectoryOrOpenLdap.class);
			configuration.addAnnotatedClass(HmDomain.class);
			configuration.addAnnotatedClass(HiveApAutoProvision.class);
			configuration.addAnnotatedClass(HmLoginAuthentication.class);
			configuration.addAnnotatedClass(MapSettings.class);
			configuration.addAnnotatedClass(AhReport.class);
			configuration.addAnnotatedClass(AhUserReport.class);
			configuration.addAnnotatedClass(AhNewReport.class);
			configuration.addAnnotatedClass(AhAdminLoginSession.class);
			configuration.addAnnotatedClass(AhUserLoginSession.class);
			configuration.addAnnotatedClass(HmNtpServerAndInterval.class);
			configuration.addAnnotatedClass(AhSummaryPage.class);
			configuration.addAnnotatedClass(RadiusProxy.class);
			configuration.addAnnotatedClass(HmolUpgradeServerInfo.class);
			configuration.addAnnotatedClass(BonjourServiceCategory.class);
			configuration.addAnnotatedClass(BonjourService.class);
			configuration.addAnnotatedClass(BonjourGatewaySettings.class);
			configuration.addAnnotatedClass(BonjourGatewayMonitoring.class);
			configuration.addAnnotatedClass(BonjourRealm.class);
			configuration.addAnnotatedClass(TCAAlarm.class);
			configuration.addAnnotatedClass(MstpRegion.class);
			configuration.addAnnotatedClass(StpSettings.class);
			configuration.addAnnotatedClass(DeviceStpSettings.class);
			configuration.addAnnotatedClass(SwitchSettings.class);

			// add license information
			configuration.addAnnotatedClass(LicenseHistoryInfo.class);
			// add activation key information
			configuration.addAnnotatedClass(ActivationKeyInfo.class);
			// add license server information
			configuration.addAnnotatedClass(LicenseServerSetting.class);
			configuration.addAnnotatedClass(HiveApFilter.class);
			configuration.addAnnotatedClass(HiveApSerialNumber.class);
			// add IP Tracking
			configuration.addAnnotatedClass(MgmtServiceIPTrack.class);
			// add Access Console
			configuration.addAnnotatedClass(AccessConsole.class);
			// add PPPoE
			configuration.addAnnotatedClass(PPPoE.class);
			configuration.addAnnotatedClass(PseProfile.class);
			configuration.addAnnotatedClass(ActiveClientFilter.class);
			configuration.addAnnotatedClass(Idp.class);
			configuration.addAnnotatedClass(IdpSettings.class);
			configuration.addAnnotatedClass(AhLatestNeighbor.class);
			configuration.addAnnotatedClass(AhLatestXif.class);
			configuration.addAnnotatedClass(AhLatestRadioAttribute.class);
			configuration.addAnnotatedClass(AhLatestACSPNeighbor.class);
			configuration.addAnnotatedClass(AhLatestInterferenceStats.class);
			configuration.addAnnotatedClass(AhBandWidthSentinelHistory.class);
			configuration.addAnnotatedClass(VlanDhcpServer.class);
			configuration.addAnnotatedClass(LLDPCDPProfile.class);
			configuration.addAnnotatedClass(HASettings.class);
			configuration.addAnnotatedClass(AirtightSettings.class);
			configuration.addAnnotatedClass(RemoteProcessCallSettings.class);
			configuration.addAnnotatedClass(CwpCertificate.class);
			configuration.addAnnotatedClass(Trex.class);
			configuration.addAnnotatedClass(VpnService.class);
			configuration.addAnnotatedClass(AhVPNStatus.class);
			configuration.addAnnotatedClass(AhClientStats.class);
			configuration.addAnnotatedClass(AhInterfaceStats.class);
			configuration.addAnnotatedClass(AhDeviceStats.class);
			configuration.addAnnotatedClass(CompliancePolicy.class);

			configuration.addAnnotatedClass(AirScreenAction.class);
			configuration.addAnnotatedClass(AirScreenBehavior.class);
			configuration.addAnnotatedClass(AirScreenSource.class);
			configuration.addAnnotatedClass(AirScreenRule.class);
			configuration.addAnnotatedClass(AirScreenRuleGroup.class);

			configuration.addAnnotatedClass(HmAccessControl.class);

			configuration.addAnnotatedClass(AhCustomReportField.class);
			configuration.addAnnotatedClass(AhCustomReport.class);
			configuration.addAnnotatedClass(AhEventsFilter.class);
			configuration.addAnnotatedClass(AhAlarmsFilter.class);

			// location
			configuration.addAnnotatedClass(LocationRssiReport.class);
			configuration.addAnnotatedClass(LocationClientWatch.class);

			// HM search
			configuration.addAnnotatedClass(Target.class);
			configuration.addAnnotatedClass(FieldTarget.class);
			configuration.addAnnotatedClass(ColumnTarget.class);
			configuration.addAnnotatedClass(EntityTarget.class);

			// Teacher view
			configuration.addAnnotatedClass(TvClass.class);
			configuration.addAnnotatedClass(TvComputerCart.class);
			configuration.addAnnotatedClass(TvResourceMap.class);
			configuration.addAnnotatedClass(TvStudentRoster.class);
			configuration.addAnnotatedClass(TvScheduleMap.class);
			configuration.addAnnotatedClass(ViewingClass.class);

			// GML
			configuration.addAnnotatedClass(PrintTemplate.class);

			// for HiveAP image information
			configuration.addAnnotatedClass(HiveApImageInfo.class);

			// for hhm update software information
			configuration.addAnnotatedClass(HMUpdateSoftwareInfo.class);
			// for hhm upgrade version information
			configuration.addAnnotatedClass(HhmUpgradeVersionInfo.class);

			// for sync task
			configuration.addAnnotatedClass(SyncTaskOnHmol.class);

			// for planner upgrade to demo
			configuration.addAnnotatedClass(DenyUpgradeEmailSuffix.class);

			// HM Start Config Page
			configuration.addAnnotatedClass(HmStartConfig.class);
			// HM SLA Customize Mapping
			configuration.addAnnotatedClass(SlaMappingCustomize.class);
			// HM Planning Tool
			configuration.addAnnotatedClass(PlanToolConfig.class);

			configuration.addAnnotatedClass(AhPCIData.class);
			configuration.addAnnotatedClass(CapwapClient.class);
			configuration.addAnnotatedClass(AhMaxClientsCount.class);

			configuration.addAnnotatedClass(AhSLAStats.class);

			// Order key history info
			configuration.addAnnotatedClass(OrderHistoryInfo.class);
			configuration.addAnnotatedClass(DomainOrderKeyInfo.class);
			// ACM entitlement key info
			configuration.addAnnotatedClass(AcmEntitleKeyHistoryInfo.class);

			// AP connect history info
			configuration.addAnnotatedClass(APConnectHistoryInfo.class);

			configuration.addAnnotatedClass(HmExpressModeEnable.class);

			// for spectrum analysis
			configuration.addAnnotatedClass(AhSpectralAnalysis.class);

			// send user register info to license server
			configuration.addAnnotatedClass(UserRegInfoForLs.class);

			// vpn network
			configuration.addAnnotatedClass(VpnNetwork.class);

			//SubNetworkResource
			configuration.addAnnotatedClass(SubNetworkResource.class);

			// DNS Service
			configuration.addAnnotatedClass(DnsServiceProfile.class);

			//for storing IP subNetwork settings
			configuration.addAnnotatedClass(DeviceIPSubNetwork.class);

			// Firewall Policy
			configuration.addAnnotatedClass(FirewallPolicy.class);

			// USB Modem configuration
			configuration.addAnnotatedClass(USBModem.class);

			configuration.addAnnotatedClass(UserProfileVlanMapping.class);

			// WAN & VPN report data
			configuration.addAnnotatedClass(AhPortAvailability.class);
			configuration.addAnnotatedClass(AhStatsLatencyHigh.class);
			configuration.addAnnotatedClass(AhStatsLatencyLow.class);
			configuration.addAnnotatedClass(AhStatsThroughputHigh.class);
			configuration.addAnnotatedClass(AhStatsThroughputLow.class);
			configuration.addAnnotatedClass(AhStatsAvailabilityHigh.class);
			configuration.addAnnotatedClass(AhStatsAvailabilityLow.class);
			configuration.addAnnotatedClass(AhStatsVpnStatusHigh.class);
			configuration.addAnnotatedClass(AhStatsVpnStatusLow.class);

			configuration.addAnnotatedClass(RadiusAttrs.class);

			configuration.addAnnotatedClass(NotificationMessageStatus.class);

			// pse status
			configuration.addAnnotatedClass(AhPSEStatus.class);
			configuration.addAnnotatedClass(AhDevicePSEPower.class);

			configuration.addAnnotatedClass(AhClientStatsHour.class);
			configuration.addAnnotatedClass(AhClientStatsDay.class);
			configuration.addAnnotatedClass(AhClientStatsWeek.class);

			configuration.addAnnotatedClass(AhInterfaceStatsHour.class);
			configuration.addAnnotatedClass(AhInterfaceStatsDay.class);
			configuration.addAnnotatedClass(AhInterfaceStatsWeek.class);

			configuration.addAnnotatedClass(AhSsidClientsCount.class);
			configuration.addAnnotatedClass(AhSsidClientsCountHour.class);
			configuration.addAnnotatedClass(AhSsidClientsCountDay.class);
			configuration.addAnnotatedClass(AhSsidClientsCountWeek.class);

			configuration.addAnnotatedClass(AhClientsOsInfoCount.class);
			configuration.addAnnotatedClass(AhClientsOsInfoCountHour.class);
			configuration.addAnnotatedClass(AhClientsOsInfoCountDay.class);
			configuration.addAnnotatedClass(AhClientsOsInfoCountWeek.class);

			configuration.addAnnotatedClass(AhNewSLAStats.class);
			configuration.addAnnotatedClass(AhNewSLAStatsHour.class);
			configuration.addAnnotatedClass(AhNewSLAStatsDay.class);
			configuration.addAnnotatedClass(AhNewSLAStatsWeek.class);

			configuration.addAnnotatedClass(AhDashboardWidget.class);
			configuration.addAnnotatedClass(AhDashboardLayout.class);
			configuration.addAnnotatedClass(AhDashboard.class);
			configuration.addAnnotatedClass(AhDashboardAppAp.class);

			configuration.addAnnotatedClass(AhLLDPInformation.class);

			//one time password
			configuration.addAnnotatedClass(OneTimePassword.class);

			//routing policy
			configuration.addAnnotatedClass(RoutingPolicy.class);
			//routingProfilepolicy
			configuration.addAnnotatedClass(RoutingProfilePolicy.class);

			configuration.addAnnotatedClass(CloudAuthCustomer.class);

			configuration.addAnnotatedClass(DashboardComponent.class);

			configuration.addAnnotatedClass(DashboardComponentMetric.class);

			configuration.addAnnotatedClass(DeviceDaInfo.class);

			configuration.addAnnotatedClass(AhSwitchPortPeriodStats.class);

			//Application Report
			configuration.addAnnotatedClass(AhAppDataHour.class);
			configuration.addAnnotatedClass(AhAppDataSeconds.class);



			configuration.addAnnotatedClass(Application.class);
			configuration.addAnnotatedClass(ApplicationProfile.class);

			configuration.addAnnotatedClass(AhAppFlowDay.class);
			configuration.addAnnotatedClass(AhAppFlowMonth.class);
			configuration.addAnnotatedClass(AhAppFlowLog.class);

			// data retention
			configuration.addAnnotatedClass(ClientDeviceInfo.class);
			configuration.addAnnotatedClass(NetworkDeviceHistory.class);
			// report data
			configuration.addAnnotatedClass(AhReportCompliance.class);
			configuration.addAnnotatedClass(ReportRollupRecord.class);
			configuration.addAnnotatedClass(NetworkDeviceReport.class);
			//cpu memory
			configuration.addAnnotatedClass(ReportCpuMemoryDate.class);
			configuration.addAnnotatedClass(ReportCpuMemoryHour.class);
			configuration.addAnnotatedClass(ReportCpuMemoryMonth.class);
			configuration.addAnnotatedClass(ReportCpuMemoryWeek.class);
			//hm memory
			configuration.addAnnotatedClass(ReportHMCpuMemoryHour.class);
			configuration.addAnnotatedClass(ReportHMCpuMemoryDate.class);
			configuration.addAnnotatedClass(ReportHMCpuMemoryWeek.class);
			configuration.addAnnotatedClass(ReportHMCpuMemoryMonth.class);
			//app data
			configuration.addAnnotatedClass(ReportAllAppLastWeek.class);
			configuration.addAnnotatedClass(ReportAppDataDate.class);
			configuration.addAnnotatedClass(AhReportAppDataHour.class);
			configuration.addAnnotatedClass(AhReportAppDataSeconds.class);
			configuration.addAnnotatedClass(ReportAppDataMonth.class);
			configuration.addAnnotatedClass(ReportAppDataWeek.class);
			//client data
			configuration.addAnnotatedClass(ReportClientDataDate.class);
			configuration.addAnnotatedClass(ReportClientDataHour.class);
			configuration.addAnnotatedClass(ReportClientDataMonth.class);
			configuration.addAnnotatedClass(ReportClientDataWeek.class);
			//network
			configuration.addAnnotatedClass(ReportNetworkErrorDate.class);
			configuration.addAnnotatedClass(ReportNetworkErrorHour.class);
			configuration.addAnnotatedClass(ReportNetworkErrorMonth.class);
			configuration.addAnnotatedClass(ReportNetworkErrorWeek.class);
			//SLA
			configuration.addAnnotatedClass(ReportNewSLAStatsDate.class);
			configuration.addAnnotatedClass(ReportNewSLAStatsHour.class);
			configuration.addAnnotatedClass(ReportNewSLAStatsMonth.class);
			configuration.addAnnotatedClass(ReportNewSLAStatsWeek.class);
			//port
			configuration.addAnnotatedClass(ReportPortNetworkHour.class);
			configuration.addAnnotatedClass(ReportPortNetworkDate.class);
			configuration.addAnnotatedClass(ReportPortNetworkWeek.class);
			configuration.addAnnotatedClass(ReportPortNetworkMonth.class);

			//ssid
			configuration.addAnnotatedClass(ReportSSIDCountDate.class);
			configuration.addAnnotatedClass(ReportSSIDCountWeek.class);
			configuration.addAnnotatedClass(ReportSSIDCountMonth.class);
			//switch port period
			configuration.addAnnotatedClass(ReportSwitchPortPeriodHour.class);
			configuration.addAnnotatedClass(ReportSwitchPortPeriodDate.class);
			configuration.addAnnotatedClass(ReportSwitchPortPeriodWeek.class);
			configuration.addAnnotatedClass(ReportSwitchPortPeriodMonth.class);

			//VHM level concurrent client count
			configuration.addAnnotatedClass(ReportClientCountHour.class);
			configuration.addAnnotatedClass(ReportClientCountDate.class);
			configuration.addAnnotatedClass(ReportClientCountWeek.class);
			configuration.addAnnotatedClass(ReportClientCountMonth.class);

			// following 3 tables for report back-end row-up.
			configuration.addAnnotatedClass(RowupJob.class);
			configuration.addAnnotatedClass(RowupLog.class);
			configuration.addAnnotatedClass(RowupStatus.class);


			//WIFI Client Mode
			configuration.addAnnotatedClass(WifiClientPreferredSsid.class);

			//cpu memory usage
			configuration.addAnnotatedClass(CpuMemoryUsage.class);

			//ClassifierTag
			configuration.addAnnotatedClass(HmClassifierTag.class);

			// Ports
			configuration.addAnnotatedClass(PortGroupProfile.class);
			configuration.addAnnotatedClass(PortAccessProfile.class);

			//Forwarding db
			configuration.addAnnotatedClass(ForwardingDB.class);

			// IGMP
			configuration.addAnnotatedClass(IgmpPolicy.class);
			configuration.addAnnotatedClass(MulticastGroup.class);
			configuration.addAnnotatedClass(MulticastGroupInterface.class);

			//switch port
			configuration.addAnnotatedClass(AhSwitchPortInfo.class);
			configuration.addAnnotatedClass(AhSwitchPortStats.class);

			//L7 signatures
			configuration.addAnnotatedClass(LSevenSignatures.class);

			//download server: DownloadInfo
			configuration.addAnnotatedClass(DownloadInfo.class);

			//Router LTE_VZ
			configuration.addAnnotatedClass(AhRouterLTEVZInfo.class);

			//Presence Analytics Customer
			configuration.addAnnotatedClass(PresenceAnalyticsCustomer.class);

			// Device Inventory
			configuration.addAnnotatedClass(DeviceInventory.class);
			
			// Device Reset config
			configuration.addAnnotatedClass(DeviceResetConfig.class);

			// Separate HM_USER from CollectionTable/JoinTable
			configuration.addAnnotatedClass(HmUserSettings.class); // Newly added
			configuration.addAnnotatedClass(HmLocalUserGroup.class);
			configuration.addAnnotatedClass(HmUserSsidProfile.class);
			configuration.addAnnotatedClass(HmAutoRefresh.class);
			configuration.addAnnotatedClass(HmTableColumn.class);
			configuration.addAnnotatedClass(HmTableSize.class);

//MDM Profiles
			configuration.addAnnotatedClass(MdmProfiles.class);
			//Capture web portal data
			configuration.addAnnotatedClass(CustomApplication.class);
			
			//device reboot history
			configuration.addAnnotatedClass(AhDeviceRebootHistory.class);
			
			// social analytics
			configuration.addAnnotatedClass(GuestAnalyticsInfo.class);
			
			configuration.addAnnotatedClass(CLIBlob.class);
			
			log.info("HibernateUtil", "Building mappings...");
			configuration.buildMappings();

			ResourceBundle bundle = ResourceBundle.getBundle("resources.hmConfig");
			for (String key : bundle.keySet()) {
				Object value = bundle.getObject(key);
				System.getProperties().put(key, value);
			}

			configuration.configure("hibernate.cfg.xml");

			String username = System.getProperty("hm.connection.username");
			configuration.setProperty("hibernate.connection.username", username);
			String password = System.getProperty("hm.connection.password");
			configuration.setProperty("hibernate.connection.password", password);
		} catch (Throwable e) {
			log.error("HibernateUtil", "Load configuration error.", e);
			throw new ExceptionInInitializerError(e);
		}
	}

	public synchronized static void init(boolean enablingCaching) {
		configuration.setProperty("hibernate.cache.use_query_cache", String.valueOf(enablingCaching));
		configuration.setProperty("hibernate.cache.use_second_level_cache", String.valueOf(enablingCaching));
		Properties configurationProperties = configuration.getProperties();

		/*
		 * Java Persistence style API. Create EntityManagerFactory from persistence.xml
		 */
		if (useEntityManagerFactory) {
			log.info("init", "Creating entity manager factory...");
			entityManagerFactory = Persistence.createEntityManagerFactory("hm-unit", configurationProperties);
			log.info("init", "Entity manager factory created.");
		} else {
			entityManagerFactory = null;
		}

		/*
		 * Hibernate native interfaces. Create SessionFactory from hibernate.cfg.xml
		 */
		if (useSessionFactory) {
			log.info("init", "Creating session factory...");
			ServiceRegistryBuilder serviceRegistryBuilder = new ServiceRegistryBuilder();
			serviceRegistryBuilder.applySettings(configurationProperties);
			ServiceRegistry serviceRegistry = serviceRegistryBuilder.buildServiceRegistry();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			log.info("init", "Session factory created.");
		} else {
			sessionFactory = null;
		}
	}

	public synchronized static void close() {
		closeEntityManagerFactory();
		closeSessionFactory();
	}

	public synchronized static void closeEntityManagerFactory() {
		if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
			try {
				log.info("closeEntityManagerFactory", "Closing entity manager factory...");
				entityManagerFactory.close();
				log.info("closeEntityManagerFactory", "Entity manager factory closed.");
			} catch (IllegalStateException ise) {
				log.error("closeEntityManagerFactory", "Close entity manager factory error.", ise);
			}
		}
	}

	public synchronized static void closeSessionFactory() {
		if (sessionFactory != null && !sessionFactory.isClosed()) {
			try {
				log.info("closeSessionFactory", "Closing session factory...");
				sessionFactory.close();
				log.info("closeSessionFactory", "Session factory closed.");
			} catch (HibernateException he) {
				log.error("closeSessionFactory", "Close session factory error.", he);
			}
		}
	}

	public synchronized static EntityManagerFactory getEntityManagerFactory() {
		if (entityManagerFactory == null) {
			init(false);
		}

		return entityManagerFactory;
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static PersistentClass getPersistentClass(Class<?> boClass) {
		return configuration.getClassMapping(boClass.getName());
	}

	public static Configuration	getConfiguration(){
		return configuration;
	}

	public static void main(String[] args) {
		init(false);
		System.out.println("Entered HibernateUtil.main");
		System.out.println("# arguments: " + Arrays.asList(args));
		System.out.println("Getting configuration.");

		if ("create".equals(args[0])) {
			if (args.length >= 2) {
				String newUrl = "jdbc:postgresql://localhost/" + args[1];
				configuration.setProperty("hibernate.connection.url", newUrl);

				if (args.length >= 3) {
					try {
						int i = Integer.parseInt(args[2]);

						if ((1 == i) && (BeOperateHMCentOSImpl.isExistHomeDomain())) {
							System.out.println("have tables! need not recreate");

							return;
						}
					} catch (Exception ex) {
						System.out.println(ex);
					}
				}
			}

			SchemaExport schemaExport = new SchemaExport(configuration);

			System.out.println("Creating schema ...");
			schemaExport.create(true, true);
		//	BeSqlProcedure.insertSqlProcedure();
			DBFunction.createHex2Int();
			DBFunction.createDBRollUp();
			DBFunction.createRepoRollUp();
			System.out.println("Create schema finished.");
		} else if ("export".equals(args[0])) {
			SchemaExport schemaExport = new SchemaExport(configuration);

			schemaExport.setOutputFile("schema.ddl");
			schemaExport.setDelimiter(";");
			System.out.println("Exporting schema ...");
			schemaExport.create(true, false);
			System.out.println("Export finished.");
		} else if ("drop".equals(args[0])) {
			SchemaExport schemaExport = new SchemaExport(configuration);

			System.out.println("Dropping schema ...");
			schemaExport.drop(true, true);
			System.out.println("Drop schema finished.");
		} else if ("reset".equals(args[0])) {
			//java HibernateUtil reset jdbc:postgresql://ip_address/db_name
			if (args.length >= 2) {
				configuration.setProperty("hibernate.connection.url", args[1]);
			}
			SchemaExport schemaExport = new SchemaExport(configuration);
			schemaExport.create(true, true);
			DBFunction.createHex2Int();
			DBFunction.createDBRollUp();
			DBFunction.createRepoRollUp();
			System.out.println("execute reset finished.");
		}

		close();
	}

}