package com.ah.bo.mgmt.impl;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeOperateHMCentOSImpl;
import com.ah.be.admin.hhmoperate.APSwitchCenter;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.LicenseInfo;
import com.ah.be.os.FileManager;
import com.ah.be.performance.BeOsInfoProcessor;
import com.ah.be.sync.VhmUserSync;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.AcmEntitleKeyHistoryInfo;
import com.ah.bo.admin.AhScheduleBackupData;
import com.ah.bo.admin.AirtightSettings;
import com.ah.bo.admin.DomainOrderKeyInfo;
import com.ah.bo.admin.GuestAnalyticsInfo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmClassifierTag;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmExpressModeEnable;
import com.ah.bo.admin.HmLoginAuthentication;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.admin.MailNotification4VHM;
import com.ah.bo.admin.OpenDNSAccount;
import com.ah.bo.admin.OpenDNSDevice;
import com.ah.bo.admin.OpenDNSMapping;
import com.ah.bo.admin.OrderHistoryInfo;
import com.ah.bo.admin.PlanToolConfig;
import com.ah.bo.admin.UserRegInfoForLs;
import com.ah.bo.cloudauth.CloudAuthCustomer;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.dashboard.AhDashboardAppAp;
import com.ah.bo.dashboard.AhDashboardLayout;
import com.ah.bo.dashboard.AhDashboardWidget;
import com.ah.bo.dashboard.DashboardComponent;
import com.ah.bo.dashboard.DashboardComponentMetric;
import com.ah.bo.gml.PrintTemplate;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.bo.hhm.HmolUpgradeServerInfo;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.DeviceIPSubNetwork;
import com.ah.bo.hiveap.DeviceInventory;
import com.ah.bo.hiveap.DeviceResetConfig;
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
import com.ah.bo.hiveap.MdmProfiles;
import com.ah.bo.hiveap.WifiClientPreferredSsid;
import com.ah.bo.igmp.IgmpPolicy;
import com.ah.bo.igmp.MulticastGroup;
import com.ah.bo.igmp.MulticastGroupInterface;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.DomainMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.InterRoaming;
import com.ah.bo.mobility.QosClassfierAndMarker;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.mobility.TunnelSetting;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.monitor.ClientDeviceInfo;
import com.ah.bo.monitor.CpuMemoryUsage;
import com.ah.bo.monitor.DeviceDaInfo;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.monitor.LocationRssiReport;
import com.ah.bo.monitor.MapSettings;
import com.ah.bo.monitor.NetworkDeviceHistory;
import com.ah.bo.monitor.OneTimePassword;
import com.ah.bo.monitor.PlannedAP;
import com.ah.bo.monitor.Trex;
import com.ah.bo.network.AccessConsole;
import com.ah.bo.network.AlgConfiguration;
import com.ah.bo.network.AlgConfigurationInfo;
import com.ah.bo.network.AlgConfigurationInfo.GatewayType;
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
import com.ah.bo.performance.AhAppFlowDay;
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
import com.ah.bo.performance.AhReportCompliance;
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
import com.ah.bo.teacherView.TvStudentRoster;
import com.ah.bo.teacherView.ViewingClass;
import com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.LocationServer;
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
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.CwpCertificate;
import com.ah.bo.wlan.EthernetAccess;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SlaMappingCustomize;
import com.ah.bo.wlan.SsidProfile;
import com.ah.events.BoEvent;
import com.ah.events.BoEvent.BoEventType;
import com.ah.events.impl.BoObserver;
import com.ah.mdm.core.profile.impl.ProfileMgrServiceImpl;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.home.clientManagement.service.CertificateGenSV;
import com.ah.util.EnumConstUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.ws.rest.client.utils.DeviceImpUtils;
import com.ah.ws.rest.server.bussiness.UsersBussiness;
import com.ah.xml.navigation.XmlNavigationNode;
import com.ah.xml.navigation.XmlNavigationTree;

public class DomainMgmtImpl implements DomainMgmt {

	private static DomainMgmtImpl instance;

	private String gdc;

	private static final Tracer log = new Tracer(DomainMgmtImpl.class.getSimpleName());

	private final HmPermission readWritePermission = new HmPermission();

	private final HmPermission readOnlyPermission = new HmPermission();

	public synchronized static DomainMgmtImpl getInstance() {
		if (instance == null) {
			instance = new DomainMgmtImpl();
		}

		return instance;
	}

	private DomainMgmtImpl() {
		readWritePermission.addOperation(HmPermission.OPERATION_READ);
		readWritePermission.addOperation(HmPermission.OPERATION_WRITE);
		readOnlyPermission.setOperations(HmPermission.OPERATION_READ);
	}

	private HmDomain homeDomain;

	private HmDomain globalDomain;

	public HmDomain getHomeDomain() {
		return homeDomain;
	}

	public void setHomeDomain(HmDomain homeDomain) {
		this.homeDomain = homeDomain;

		// update cache domain
		CacheMgmt.getInstance().updateHmDomainCache(homeDomain);
	}

	public void setGlobalDomain(HmDomain globalDomain) {
		this.globalDomain = globalDomain;
		gdc = "owner.id = " + globalDomain.getId();
	}

	public HmDomain getGlobalDomain() {
		return globalDomain;
	}

	public String getGlobalDomainCondition() {
		return gdc;
	}

	/**
	 * @see com.ah.bo.mgmt.DomainMgmt#createDomain(com.ah.bo.admin.HmDomain)
	 */
	public Long createDomain(HmDomain hmDomain) throws Exception {
		return createDomain(hmDomain, true);
	}

	/**
	 * Create domain by different use
	 *
	 * @param hmDomain
	 *            -
	 * @param containStart
	 *            -
	 * @return Long
	 * @throws Exception
	 *             -
	 */
	public Long createDomain(HmDomain hmDomain, boolean containStart) throws Exception {
		Long domainId = QueryUtil.createBo(hmDomain);

		// update cache domain
		CacheMgmt.getInstance().initCacheValues(hmDomain.getId());

		// Create relevant directories as soon as the creation of destination
		// domain is successful.
		createDomainRelevantDirs(hmDomain.getDomainName(), true);

		// invoke to copy the default files to the domain.
		BeTopoModuleUtil.copyDefaultMapImages(hmDomain.getDomainName());

		// create bo for new domain
		createDefaultBo4Domain(hmDomain);

		if (containStart) {
			// create start here page
			createStartHerePage(hmDomain);
		}

		// sync device inventory with Redirector, only work for HMOL VHM customers
		this.syncDeviceInventoryWithRedirector(hmDomain);
		
		return domainId;
	}
	
	private void syncDeviceInventoryWithRedirector(HmDomain hmDomain) {
		if (hmDomain == null
				|| hmDomain.isHomeDomain()) {
			return;
		}
		DeviceImpUtils.getInstance().syncDeviceInventoriesWithRedirector(hmDomain);
	}

	private void createDefaultBo4Domain(HmDomain hmDomain) throws Exception {
		Long domainWorldMapId = BoMgmt.getMapMgmt().createWorldMap(hmDomain);

		// create default groups
		createDefaultUserGroups(hmDomain, domainWorldMapId);

		// create mail notification record
		createMailNotifyRecord(hmDomain);

		// create default cwp certificate
		createDomainCwpCert(hmDomain);

		// create default cert
		BeOperateHMCentOSImpl.createDefaultDomainCERT(hmDomain.getDomainName());

		// create default location client watch
		createDomainClientWatch(hmDomain);

		// create hmservicessettings bo
		createHMServicesSettings(hmDomain);

		// insert a default update software info bo
		createUpdateSoftwareInfoBo(hmDomain);
		
		//copy certificate from home domain to new domain if HM is in the mode HM
		if(!NmsUtil.isHostedHMApplication()){
			CertificateGenSV.copyCrt4VHM("home",hmDomain.getDomainName());
		}
	}

	private void createStartHerePage(HmDomain hmDomain) throws Exception {
		// set start here data
		HmStartConfig startConf = new HmStartConfig();
		startConf.setOwner(hmDomain);

		boolean showExpress;
		List<HmExpressModeEnable> settings = QueryUtil.executeQuery(HmExpressModeEnable.class,
				null, null);
		if (settings == null || settings.isEmpty()) {
			showExpress = NmsUtil.getOEMCustomer().getExpressModeEnable();
		} else {
			showExpress = settings.get(0).isExpressModeEnable();
		}

		if (NmsUtil.isHMForOEM() && !showExpress){
			startConf.setModeType(HmStartConfig.HM_MODE_FULL);
		} else {
			startConf.setNetworkName(hmDomain.getDomainName());
			
			startConf.setModeType(HmStartConfig.HM_MODE_EASY);
			
			// the following settings in start here 2013/12/27
			// network name
//			ConfigTemplate wlanPolicy = createWlan(hmDomain);
//
//			// HiveAP
//			StartHereAction.assignHiveApInfos(wlanPolicy, hmDomain.getId());
		}
		// create the start here config in database
		QueryUtil.createBo(startConf);
	}

	// remove quick start policies
	// only create some sub profiles here, no wireless only configtemplate, use the default one with defaultFlag==true
	private ConfigTemplate createWlan(HmDomain hmDomain) throws Exception {
		// new wlan name
		String wlanName = hmDomain.getDomainName();

		// create hive object
		HiveProfile hive = new HiveProfile();
		hive.setHiveName(wlanName);
		hive.setHivePassword(MgrUtil.getRandomString(63, 7));
		hive.setOwner(hmDomain);
		hive.setDescription(MgrUtil.getUserMessage("policy.predefined.description.hive.domain"));
		Long hiveId = QueryUtil.createBo(hive);
		hive = QueryUtil.findBoById(HiveProfile.class, hiveId);

		// create ntp server object
		MgmtServiceTime ntpServer = new MgmtServiceTime();
		ntpServer.setMgmtName(wlanName);
		ntpServer.setEnableClock(true);
		ntpServer.setTimeZoneStr("America/Los_Angeles");
		ntpServer.setDescription("For all "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s in express mode");
		ntpServer.setOwner(hmDomain);
		QueryUtil.createBo(ntpServer);

		// create management option
		MgmtServiceOption mgmtOption = new MgmtServiceOption();
		mgmtOption.setMgmtName(wlanName);
		mgmtOption.setSystemLedBrightness(MgmtServiceOption.SYSTEM_LED_BRIGHT);
		mgmtOption.setOwner(hmDomain);
		mgmtOption.setDescription("For all "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s in express mode");
		QueryUtil.createBo(mgmtOption);

		// create alg service
		AlgConfiguration defAlg = new AlgConfiguration();
		defAlg.setConfigName(wlanName);
		Map<String, AlgConfigurationInfo> items = new LinkedHashMap<String, AlgConfigurationInfo>();
		for (GatewayType gatewayType : AlgConfigurationInfo.GatewayType.values()) {
			AlgConfigurationInfo oneItem = defAlg.getAlgInfo(gatewayType);
			if (oneItem == null) {
				oneItem = new AlgConfigurationInfo();
				oneItem.setIfEnable(!(GatewayType.DNS.equals(gatewayType)|| GatewayType.HTTP.equals(gatewayType)));
				if (GatewayType.SIP.equals(gatewayType)) {
					oneItem.setQosClass(EnumConstUtil.QOS_CLASS_VOICE);
					oneItem.setTimeout(60);
					oneItem.setDuration(720);
				}
			}
			oneItem.setGatewayType(gatewayType);
			items.put(oneItem.getkey(), oneItem);
		}
		defAlg.setItems(items);
		defAlg.setOwner(hmDomain);
		defAlg.setDescription("For all "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s in express mode");
		QueryUtil.createBo(defAlg);

		// create LLDPCDPProfile
		LLDPCDPProfile lldpOrCdp = new LLDPCDPProfile();
		lldpOrCdp.setProfileName(wlanName);
		lldpOrCdp.setOwner(hmDomain);
		lldpOrCdp.setDescription("For all "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s in express mode");
		QueryUtil.createBo(lldpOrCdp);

		return HmBeParaUtil.getDefaultProfile(ConfigTemplate.class, null);
	}

	/**
	 * @param hmDomain
	 *            -
	 * @throws Exception
	 *             -
	 */
	private void createUpdateSoftwareInfoBo(HmDomain hmDomain) throws Exception {
		List<HMUpdateSoftwareInfo> list = QueryUtil.executeQuery(HMUpdateSoftwareInfo.class, null,
				new FilterParams("domainName", hmDomain.getDomainName()));
		if (!list.isEmpty()) {
			// remove exists records
			bulkRemoveBos(HMUpdateSoftwareInfo.class, new FilterParams("domainName", hmDomain
					.getDomainName()));
		}

		HMUpdateSoftwareInfo bo = new HMUpdateSoftwareInfo();
		bo.setDomainName(hmDomain.getDomainName());
		bo.setIpAddress(HmBeOsUtil.getHiveManagerIPAddr());
		bo.setStatus(HMUpdateSoftwareInfo.STATUS_ACTIVE);
		bo.setHmVersion(NmsUtil.getVersionInfo().getMainVersion());

		QueryUtil.createBo(bo);
	}

	/**
	 * create MailNotification4VHM record for vhm
	 *
	 * @param hmDomain
	 *            -
	 * @throws Exception
	 *             -
	 */
	private void createMailNotifyRecord(HmDomain hmDomain) throws Exception {
		MailNotification defaultSettings = new MailNotification();
		defaultSettings.setOwner(hmDomain);
		defaultSettings.setSendMailFlag(false);
		defaultSettings.setServerName("");
		defaultSettings.setMailFrom("");
		defaultSettings.setMailTo("");
		defaultSettings.setHdRadio((byte) 16);
		defaultSettings.setCapWap((byte) 17);
		defaultSettings.setConfig((byte) 8);
		defaultSettings.setSecurity((byte) 8);
		defaultSettings.setTimeBomb((byte) 28);
		defaultSettings.setClient((byte) 5);
		defaultSettings.setTca((byte) 17);
		QueryUtil.createBo(defaultSettings);
	}

	/**
	 * Create {@link HMServicesSettings} bo for new VHM
	 *
	 * @param hmDomain
	 *            -
	 * @throws Exception
	 *             -
	 */
	private void createHMServicesSettings(HmDomain hmDomain) throws Exception {
		HMServicesSettings settings = new HMServicesSettings();
		settings.setOwner(hmDomain);
		settings.setEnableClientRefresh(false);
		settings.setRefreshInterval(60);
		settings.setRefreshFilterName("");
		settings.setSessionExpiration(15);
		settings.setInfiniteSession(false);
		settings.setApiUserName(hmDomain.getDomainName());
		if(!NmsUtil.isHostedHMApplication()){
			try{
				List<HmDomain> home = QueryUtil.executeQuery(HmDomain.class,null, new FilterParams("domainname",new String("home")));
				if(!home.isEmpty()){
					HMServicesSettings homeSettings = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", home.get(0));
					if(homeSettings != null){
						settings.setEnableCidPolicyEnforcement(homeSettings.isEnableCidPolicyEnforcement());
						settings.setEnableClientManagement(homeSettings.isEnableClientManagement());
						settings.setApiKey(homeSettings.getApiKey());
					}
				}
			}catch(Exception e){
				log.error(e.getMessage());
			}
		}
		QueryUtil.createBo(settings);
	}

	/**
	 * create default location client watch for vhm
	 *
	 * @param hmDomain
	 *            -
	 * @throws Exception
	 *             -
	 */
	private void createDomainClientWatch(HmDomain hmDomain) throws Exception {
		LocationClientWatch defaultBo = new LocationClientWatch();
		defaultBo.setOwner(hmDomain);
		defaultBo.setDefaultFlag(true);
		defaultBo.setDescription("Default location client watch list, which includes all clients.");
		defaultBo.setName("All Clients");
		QueryUtil.createBo(defaultBo);
	}

	/**
	 * update Domain API
	 *
	 * @param hmDomain
	 *            -
	 * @throws Exception
	 *             -
	 */
	public void updateDomain(HmDomain hmDomain) throws Exception {
		// get old object
		HmDomain bo = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", hmDomain
				.getDomainName());
		if (bo == null) {
			log.error("updateDomain",
					"Update domain, but can't get domain object by given domain name.");
			return;
		}

		// if call from VHM edit page, update attribute of default group
		if (!NmsUtil.isHostedHMApplication()
				&& hmDomain.getMonitoringConfigId() != 0) {
			updateDefaultGroupAttribute(hmDomain);
		}

		// update bo
		QueryUtil.updateBo(hmDomain);

		// Note: notification of update for HmDomain should be on the back of database operation.
		BoObserver.notifyListeners(new BoEvent<HmDomain>(hmDomain, BoEventType.UPDATED));

		// update cache domain
		CacheMgmt.getInstance().updateHmDomainCache(hmDomain);
	}

	/**
	 * get remaining ap number(for real HiveAP)
	 *
	 * @return -
	 */
	public int getRemainingMaxAPNum() {
		// get max ap number supported by license file
		LicenseInfo licenseInfo = HmBeLicenseUtil.getLicenseInfo();
		int licenseAPCount = null == licenseInfo ? 0 : licenseInfo.getHiveAps();

		// mark: not include new status ap from 3.3r1
		List<Short> notInList = new ArrayList<>();
		notInList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
		//notInList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY);
		int realAPCountOfHomeDomain = (int) QueryUtil.findRowCount(HiveAp.class, new FilterParams(
				"manageStatus=:s1 AND simulated=:s2 AND owner=:s3 AND hiveApModel not in :s4",
				new Object[] { HiveAp.STATUS_MANAGED, false, homeDomain, notInList }));
		int maxAPCountOfOtherDomains = getNonHomeDomainAPNum();
		int maxAPCountOfHomeDomain = licenseAPCount - maxAPCountOfOtherDomains;

		// mark: real ap count maybe plus than max ap number(5)
		if (realAPCountOfHomeDomain > maxAPCountOfHomeDomain) {
			realAPCountOfHomeDomain = maxAPCountOfHomeDomain;
		}

		int usedApNum = realAPCountOfHomeDomain + maxAPCountOfOtherDomains;

		return licenseAPCount - usedApNum;
	}

	/**
	 * Get the total number of HiveAP which can be supported in non-home domain .
	 *
	 * @return int
	 */
	public int getNonHomeDomainAPNum() {
		int result = 0;
		// get ap number from order key
		if (NmsUtil.isHostedHMApplication()) {
			List<?> allNames = QueryUtil.executeQuery("SELECT domainName FROM " + HmDomain.class.getSimpleName(), new SortParams("id"),
				new FilterParams("domainName != :s1 AND domainName != :s2",
					new Object[]{HmDomain.HOME_DOMAIN, HmDomain.GLOBAL_DOMAIN}));

			for (Object obj : allNames) {
				String domName = (String)obj;
				LicenseInfo lsInfo = HmBeLicenseUtil.VHM_ORDERKEY_INFO.get(domName);
				if (null != lsInfo) {
					if (!BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(lsInfo.getLicenseType())) {
						result += lsInfo.getHiveAps();
					}
				} else {
					DomainOrderKeyInfo domOrder = QueryUtil.findBoByAttribute(
							DomainOrderKeyInfo.class, "domainName", domName);
					if (null != domOrder) {
						int[] orderInfo = domOrder.getOrderInfo();
						if (0 != orderInfo[0]) {
							result += orderInfo[1];
						}
					}
				}
			}
		} else {
			String query = "SELECT SUM(maxApNum) FROM " + HmDomain.class.getSimpleName() + " WHERE domainName != '"
				+ HmDomain.GLOBAL_DOMAIN + "' AND domainName != '" + HmDomain.HOME_DOMAIN + "'";
			List<?> list = QueryUtil.executeQuery(query, 1);
			result += (null == list.get(0) ? 0 : Integer.valueOf(list.get(0).toString()));
		}

		return result;
	}

	/**
	 * check user group, if exists return true;
	 *
	 * @param groupName
	 *            -
	 * @param domain
	 *            -
	 * @return -
	 */
	private boolean checkUserGroupExists(String groupName, HmDomain domain) {
		HmUserGroup group = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupName", groupName,
				domain.getId());
		return group != null;
	}

	/**
	 * create default user groups for domain<br>
	 * including: readonly group and read-write group
	 *
	 * @param hmDomain
	 *            -
	 * @param domainWorldMapId
	 *            -
	 * @throws Exception
	 *             -
	 */
	private void createDefaultUserGroups(HmDomain hmDomain, Long domainWorldMapId) throws Exception {

		// check default user group exists
		if (checkUserGroupExists(HmUserGroup.MONITOR, hmDomain)) {
			// update instance permission
			List<HmUserGroup> groupList = new ArrayList<HmUserGroup>();

			HmUserGroup monitorGroup = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupName",
					HmUserGroup.MONITOR, hmDomain.getId());
			if (monitorGroup != null) {
				Map<Long, HmPermission> mapPermissions = new HashMap<Long, HmPermission>();
				mapPermissions.put(domainWorldMapId, readOnlyPermission);
				monitorGroup.setInstancePermissions(mapPermissions);
				groupList.add(monitorGroup);
			}

			HmUserGroup configGroup = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupName",
					HmUserGroup.CONFIG, hmDomain.getId());
			if (configGroup != null) {
				Map<Long, HmPermission> mapPermissions = new HashMap<Long, HmPermission>();
				mapPermissions.put(domainWorldMapId, readWritePermission);
				configGroup.setInstancePermissions(mapPermissions);
				groupList.add(configGroup);
			}

			HmUserGroup planGroup = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupName",
					HmUserGroup.PLANNING, hmDomain.getId());
			if (planGroup != null) {
				Map<Long, HmPermission> mapPermissions = new HashMap<Long, HmPermission>();
				mapPermissions.put(domainWorldMapId, readWritePermission);
				planGroup.setInstancePermissions(mapPermissions);
				groupList.add(planGroup);
			}

			QueryUtil.bulkUpdateBos(groupList);

			return;
		}

		List<HmUserGroup> list_group = new ArrayList<HmUserGroup>();

		// create feature keys
		createFeatureKeys();

		if (NmsUtil.isHostedHMApplication()) {//HMOL
			hmDomain.setMonitoringId(HmUserGroup.MONITOR_ATTRIBUTE);
			hmDomain.setMonitoringConfigId(HmUserGroup.CONFIG_ATTRIBUTE);
			hmDomain.setRfPlanningId(HmUserGroup.PLANNING_ATTRIBUTE);
			hmDomain.setUserMngAdminId(HmUserGroup.GM_ADMIN_ATTRIBUTE);
			hmDomain.setUserMngOperatorId(HmUserGroup.GM_OPERATOR_ATTRIBUTE);
			hmDomain.setTeacherId(HmUserGroup.TEACHER_ATTRIBUTE);
		} else {//stand alone
			if (hmDomain.getMonitoringConfigId() == 0
					&& !HmDomain.HOME_DOMAIN.equals(hmDomain.getDomainName())
					&& !HmDomain.GLOBAL_DOMAIN.equals(hmDomain.getDomainName())) {
				//if it is not be called from VHM edit page, init group attribute of VHM(exclude home and global) user group
				int[] newIds = generateGroupAttribute(6);
				hmDomain.setMonitoringId(newIds[0]);
				hmDomain.setMonitoringConfigId(newIds[1]);
				hmDomain.setRfPlanningId(newIds[2]);
				hmDomain.setUserMngAdminId(newIds[3]);
				hmDomain.setUserMngOperatorId(newIds[4]);
				hmDomain.setTeacherId(newIds[5]);
			}
		}

		// create readonly group
		HmUserGroup group = new HmUserGroup();
		group.setGroupName(HmUserGroup.MONITOR);
		group.setDefaultFlag(true);
		group.setOwner(hmDomain);
//		group.setGroupAttribute(HmUserGroup.MONITOR_ATTRIBUTE);
		group.setGroupAttribute(hmDomain.getMonitoringId());
		group.setFeaturePermissions(getPermissionReadOnly());
		Map<Long, HmPermission> mapPermissions = new HashMap<Long, HmPermission>();
		mapPermissions.put(domainWorldMapId, readOnlyPermission);
		group.setInstancePermissions(mapPermissions);
		list_group.add(group);

		// read-write
		group = new HmUserGroup();
		group.setGroupName(HmUserGroup.CONFIG);
		group.setDefaultFlag(true);
		group.setOwner(hmDomain);
//		group.setGroupAttribute(HmUserGroup.CONFIG_ATTRIBUTE);
		group.setGroupAttribute(hmDomain.getMonitoringConfigId());
		group.setFeaturePermissions(getPermissionWrite());
		mapPermissions = new HashMap<Long, HmPermission>();
		mapPermissions.put(domainWorldMapId, readWritePermission);
		group.setInstancePermissions(mapPermissions);
		list_group.add(group);

		// planning tool only
		group = new HmUserGroup();
		group.setGroupName(HmUserGroup.PLANNING);
		group.setDefaultFlag(true);
		group.setOwner(hmDomain);
//		group.setGroupAttribute(HmUserGroup.PLANNING_ATTRIBUTE);
		group.setGroupAttribute(hmDomain.getRfPlanningId());

		Map<String, HmPermission> map = new HashMap<String, HmPermission>();
		map.put(Navigation.L2_FEATURE_MAP_VIEW, readWritePermission);
		group.setFeaturePermissions(map);

		mapPermissions = new HashMap<Long, HmPermission>();
		mapPermissions.put(domainWorldMapId, readWritePermission);
		group.setInstancePermissions(mapPermissions);
		list_group.add(group);

		// gm admin
		HmUserGroup adminGroup = new HmUserGroup();
		adminGroup.setGroupName(HmUserGroup.GM_ADMIN);
		adminGroup.setDefaultFlag(true);
		adminGroup.setOwner(hmDomain);
//		adminGroup.setGroupAttribute(HmUserGroup.GM_ADMIN_ATTRIBUTE);
		adminGroup.setGroupAttribute(hmDomain.getUserMngAdminId());
		adminGroup.setFeaturePermissions(getGMPermission(HmUserGroup.GM_ADMIN));
		list_group.add(adminGroup);

		// gm operator
		HmUserGroup operatorGroup = new HmUserGroup();
		operatorGroup.setGroupName(HmUserGroup.GM_OPERATOR);
		operatorGroup.setDefaultFlag(true);
		operatorGroup.setOwner(hmDomain);
//		operatorGroup.setGroupAttribute(HmUserGroup.GM_OPERATOR_ATTRIBUTE);
		operatorGroup.setGroupAttribute(hmDomain.getUserMngOperatorId());
		operatorGroup.setFeaturePermissions(getGMPermission(HmUserGroup.GM_OPERATOR));
		list_group.add(operatorGroup);

		HmUserGroup teacherGroup = new HmUserGroup();
		teacherGroup.setGroupName(HmUserGroup.TEACHER);
		teacherGroup.setDefaultFlag(true);
		teacherGroup.setOwner(hmDomain);
//		teacherGroup.setGroupAttribute(HmUserGroup.TEACHER_ATTRIBUTE);
		teacherGroup.setGroupAttribute(hmDomain.getTeacherId());
		teacherGroup.setFeaturePermissions(getTeacherPermission());
		list_group.add(teacherGroup);

		QueryUtil.bulkCreateBos(list_group);
	}

	private final List<String> keys = new ArrayList<String>();

	private boolean isHHM = false;

	/**
	 * read navigation.xml content, cache permit feature key
	 */
	public void createFeatureKeys() {
		XmlNavigationTree xmlTree = loadXmlNavigationTree();

		if (xmlTree == null) {
			log.error("createFeatureKeys", "xml tree is null.");
		}

		isHHM = NmsUtil.isHostedHMApplication();

		keys.clear();

		if (xmlTree != null) {
			createPermissions(xmlTree.getTree());
		}
	}

	/**
	 * get feature permissions map for read-write group
	 *
	 * @return -
	 */
	public Map<String, HmPermission> getPermissionWrite() {
		Map<String, HmPermission> map = new HashMap<String, HmPermission>();

		if (keys != null) {
			for (String key : keys) {
				if (key == null) {
					continue;
				}

				HmPermission permission = new HmPermission();
				permission.setOperations(HmPermission.OPERATION_READ);
				if (!key.equals(Navigation.L2_FEATURE_LICENSEMGR)) {
					permission.addOperation(HmPermission.OPERATION_WRITE);
				}

				map.put(key, permission);
			}
		}
		return map;
	}

	/**
	 * get feature permissions map for monitor group
	 *
	 * @return -
	 */
	public Map<String, HmPermission> getPermissionReadOnly() {
		Map<String, HmPermission> map = new HashMap<String, HmPermission>();

		if (keys != null) {
			for (String key : keys) {
				if (key == null) {
					continue;
				}

				HmPermission permission = new HmPermission();
				permission.setOperations(HmPermission.OPERATION_READ);
				if (key.equals(Navigation.L1_FEATURE_HOME)
						|| key.equals(Navigation.L2_FEATURE_USER_PASSWORD_MODIFY)) {
					permission.addOperation(HmPermission.OPERATION_WRITE);
				}

				map.put(key, permission);
			}
		}
		return map;
	}

	/**
	 * get feature permissions map for planning group
	 *
	 * @return -
	 */
	public Map<String, HmPermission> getPermissionPlanning() {
		Map<String, HmPermission> map = new HashMap<String, HmPermission>();
		map.put(Navigation.L2_FEATURE_MAP_VIEW, readWritePermission);
		return map;
	}

	/*
	 * XML navigation tree is loaded upon tomcat startup.
	 */
	private XmlNavigationTree loadXmlNavigationTree() {
		XmlNavigationTree xmlNavigationTree = null;

		try {
			String hmRoot = System.getenv("HM_ROOT");
			String xmlNavTreePath = hmRoot != null ? hmRoot + File.separator + "WEB-INF"
					+ File.separator + "navigation.xml" : "webapps" + File.separator + "ROOT"
					+ File.separator + "WEB-INF" + File.separator + "navigation.xml";
			xmlNavigationTree = MgrUtil.unmarshal(xmlNavTreePath);
		} catch (JAXBException e) {
			log.error("loadXmlNavigationTree::Load navigation tree failed!", e.getMessage());
		}

		return xmlNavigationTree;
	}

	/**
	 * recur function, read navigation tree node
	 *
	 * @param xmlNode
	 *            -
	 */
	private void createPermissions(XmlNavigationNode xmlNode) {
		if (xmlNode == null) {
			log.error("createPermissions", "xml node is null.");
			return;
		}

		for (XmlNavigationNode xmlChildNode : xmlNode.getNode()) {
			if (xmlChildNode == null) {
				continue;
			}

			if (xmlChildNode.isHomeOnly() != null && xmlChildNode.isHomeOnly()) {
				// not support home only features
				continue;
			}

			String key = xmlChildNode.getKey();
			if (key != null) {
				if (key.equals(Navigation.L1_FEATURE_USER_MGR)
						|| key.equals(Navigation.L1_FEATURE_USER_REPORTS)) {
					// not covert gml features
					continue;
				}

				if (!isHHM
						&& (key.equals(Navigation.L2_FEATURE_REBOOTAPP) || key
								.equals(Navigation.L2_FEATURE_UPDATE_SOFTWARE))) {
					// for regular hm, not support update software and revert version features.
					continue;
				}
			}

			keys.add(key);
			createPermissions(xmlChildNode);
		}
	}

	public Map<String, HmPermission> getTeacherPermission() {
		Map<String, HmPermission> map = new HashMap<String, HmPermission>();
		map.put(Navigation.L2_FEATURE_USER_PASSWORD_MODIFY, readWritePermission);
		return map;
	}

	public Map<String, HmPermission> getGMPermission(String groupType) {
		Map<String, HmPermission> map = new HashMap<String, HmPermission>();
		XmlNavigationTree xmlTree = loadXmlNavigationTree();
		if (xmlTree != null) {
			keys.clear();

			if (groupType.equals(HmUserGroup.GM_ADMIN)) {
				createGMAdminPermissions(xmlTree.getTree());
			} else if (groupType.equals(HmUserGroup.GM_OPERATOR)) {
				createGMOperatorPermissions(xmlTree.getTree());
			}

			for (String key : keys) {
				if (key == null) {
					continue;
				}

				map.put(key, readWritePermission);
			}
		}
		return map;
	}

	private void createGMAdminPermissions(XmlNavigationNode xmlNode) {
		if (xmlNode == null) {
			return;
		}

		for (XmlNavigationNode xmlChildNode : xmlNode.getNode()) {
			if (xmlChildNode != null) {
				String key = xmlChildNode.getKey();
				if (key != null
						&& (key.equals(Navigation.L1_FEATURE_USER_MGR) || key
								.equals(Navigation.L1_FEATURE_USER_REPORTS))) {
					keys.add(key);
					createPermissionsPure(xmlChildNode);
				}
			}
		}
	}

	// no special check
	private void createPermissionsPure(XmlNavigationNode xmlNode) {
		for (XmlNavigationNode xmlChildNode : xmlNode.getNode()) {
			if (xmlChildNode != null) {
				String key = xmlChildNode.getKey();
				if (key != null) {
					keys.add(key);
					createPermissionsPure(xmlChildNode);
				}
			}
		}
	}

	private void createGMOperatorPermissions(XmlNavigationNode xmlNode) {
		if (xmlNode == null) {
			return;
		}

		keys.add(Navigation.L1_FEATURE_USER_MGR);
		keys.add("tempAccount");
		keys.add(Navigation.L2_FEATURE_UM_TEMP_CREATE);
	}

	public Long cloneDomain(HmDomain srcDomain, HmDomain destDomain) throws Exception {
		return cloneDomain(srcDomain, destDomain, false);
	}

	/**
	 * clone Domain API
	 *
	 * @param srcDomain
	 *            -
	 * @param destDomain
	 *            -
	 * @return -
	 * @throws Exception
	 *             -
	 */
	public Long cloneDomain(HmDomain srcDomain, HmDomain destDomain, boolean blnCloneFromCreate) throws Exception {
		// hmonline need order key to support ap number
		if (NmsUtil.isHostedHMApplication()) {
			destDomain.setMaxApNum(0);
		}
		Long id = createDomain(destDomain, false);
		destDomain = QueryUtil.findBoById(HmDomain.class, id);

		DomainMaintenance cloneUtil = DomainMaintenance.getInstance();

		/**
		 * OpenDNS Feature
		 */
		cloneUtil.cloneOpenDNSAccount(srcDomain, destDomain);
		/*
		 * admin section
		 */
		cloneUtil.cloneHMSerivceSettings(srcDomain, destDomain);
		if (!blnCloneFromCreate) {
			cloneUtil.cloneMailNotify(srcDomain, destDomain);

			cloneUtil.cloneSingleTableInfo(AhClientEditValues.class, srcDomain, destDomain);

			// cloneUtil.cloneStartConfig(srcDomain,destDomain);
			cloneUtil.cloneSingleTableInfo(HmStartConfig.class, srcDomain, destDomain);

			cloneUtil.cloneSingleTableInfo(PlanToolConfig.class, srcDomain, destDomain);
		}

		/*
		 * topology section
		 */
		cloneUtil.cloneSingleTableInfo(MapSettings.class, srcDomain, destDomain);
		// Disable the map container clone
		//cloneUtil.cloneMapContainer(srcDomain, destDomain);

		/*
		 * Configuration section
		 */
		cloneUtil.cloneCustomApplication(srcDomain, destDomain);
		cloneUtil.cloneSingleTableInfo(CompliancePolicy.class, srcDomain, destDomain);
		cloneUtil.cloneLocationClientWatch(srcDomain, destDomain);
		cloneUtil.cloneSingleTableInfo(LLDPCDPProfile.class, srcDomain, destDomain);
		cloneUtil.cloneIPAddress(srcDomain, destDomain);
		cloneUtil.cloneMACOrOUI(srcDomain, destDomain);
		cloneUtil.cloneOsObject(srcDomain, destDomain);
		cloneUtil.cloneDomainObject(srcDomain, destDomain);
		cloneUtil.cloneVlan(srcDomain, destDomain);
		cloneUtil.cloneUserProfileAttribute(srcDomain, destDomain);
		cloneUtil.cloneDosPrevention(srcDomain, destDomain);
		cloneUtil.cloneSingleTableInfo(InterRoaming.class, srcDomain, destDomain);
		cloneUtil.cloneSingleTableInfo(Scheduler.class, srcDomain, destDomain);
		cloneUtil.cloneRadioProfile(srcDomain, destDomain);
		cloneUtil.cloneSingleTableInfo(NetworkService.class, srcDomain, destDomain);
		cloneUtil.cloneQosRateControl(srcDomain, destDomain);
		cloneUtil.cloneAlgConfiguration(srcDomain, destDomain);
		cloneUtil.cloneSingleTableInfo(MgmtServiceIPTrack.class, srcDomain, destDomain);
		cloneUtil.cloneActiveDirectoryOrLdap(srcDomain, destDomain);
		cloneUtil.cloneMacFilter(srcDomain, destDomain);
		cloneUtil.cloneCwpCertficate(srcDomain, destDomain);
		cloneUtil.cloneCwp(srcDomain, destDomain);
		// add access console which bind mac filter
		cloneUtil.cloneAccessConsole(srcDomain, destDomain);
		cloneUtil.cloneLocationServer(srcDomain, destDomain);
		cloneUtil.cloneTunnelSetting(srcDomain, destDomain);
		cloneUtil.cloneIpPolicy(srcDomain, destDomain);
		cloneUtil.cloneMacPolicy(srcDomain, destDomain);
		cloneUtil.cloneLocalUserGroup(srcDomain, destDomain);
		cloneUtil.cloneLocalUser(srcDomain, destDomain);
		cloneUtil.cloneRadiusLibrarySip(srcDomain, destDomain);
		cloneUtil.clonePrintTemplate(srcDomain, destDomain);
//		cloneUtil.cloneRadiusOnHiveap(srcDomain, destDomain);
		cloneUtil.cloneIpFilter(srcDomain, destDomain);
		cloneUtil.cloneMgmtServiceDNS(srcDomain, destDomain);
		cloneUtil.cloneMgmtServiceSNMP(srcDomain, destDomain);
		cloneUtil.cloneMgmtServiceSyslog(srcDomain, destDomain);
		cloneUtil.cloneMgmtServiceTime(srcDomain, destDomain);
		cloneUtil.cloneRadiusAssignment(srcDomain, destDomain);
		cloneUtil.cloneMgmtServiceOption(srcDomain, destDomain);
		cloneUtil.cloneHiveProfile(srcDomain, destDomain);
		cloneUtil.cloneRadiusAttri(srcDomain, destDomain);
		/*
		 * DNS Services Profile
		 */
		cloneUtil.cloneDNSServices(srcDomain, destDomain);
		cloneUtil.cloneVpnNetwork(srcDomain, destDomain);
		cloneUtil.cloneSingleTableInfo(QosMarking.class, srcDomain, destDomain);//clone QosMarking must before UserProfile
		cloneUtil.cloneUserProfile(srcDomain, destDomain);
		cloneUtil.cloneRadiusOnHiveap(srcDomain, destDomain);
		cloneUtil.cloneRoutingProfiles(srcDomain, destDomain);

		cloneUtil.cloneRoutingPolicies(srcDomain, destDomain);
		cloneUtil.cloneRoutingProfilePolicies(srcDomain, destDomain);
		cloneUtil.cloneConfigMdmPolicies(srcDomain, destDomain);
		
		/**
		 * OpenDNS Feature
		 */
		cloneUtil.cloneOpenDNSDevice(srcDomain, destDomain);
		cloneUtil.cloneOpenDNSMapping(srcDomain, destDomain);
		
		//Clone the Wifi Client Preferred SSIDs.
		cloneUtil.cloneWifiClientPreSSID(srcDomain, destDomain);
		/*
		 * MDM Profile
		 */
		HMServicesSettings clientSetting = QueryUtil.findBoByAttribute(HMServicesSettings.class,"owner",srcDomain);
		if(clientSetting != null){
			if(clientSetting.isEnableClientManagement()){
				cloneUtil.cloneMdmProfilesPolicies(srcDomain, destDomain);
				cloneUtil.cloneMdmProfilesPoliciesViaApi(srcDomain, destDomain);
			}
		}
		
		//=======================================================
		// firewall policy base on user profile
		cloneUtil.cloneFirewallPolicy(srcDomain, destDomain);

		// add bonjour gateway profile
		cloneUtil.cloneSingleTableInfo(BonjourServiceCategory.class, srcDomain, destDomain);
		cloneUtil.cloneService(srcDomain, destDomain);
		cloneUtil.cloneSingleTableInfo(VlanGroup.class,srcDomain, destDomain);
		cloneUtil.cloneBonjourGatewaySetting(srcDomain, destDomain);

		//cloneUtil.cloneDeviceGroupPolicy(srcDomain, destDomain);
		cloneUtil.cloneRadiusUserProfileRule(srcDomain, destDomain);
		cloneUtil.cloneSingleTableInfo(ServiceFilter.class, srcDomain, destDomain);
		// cloneInfo.cloneSingleTableInfo(PersonalizedPskGroup.class, srcDomain,
		// destDomain);
		// cloneInfo.clonePersonalizedPsk(srcDomain, destDomain);
		cloneUtil.cloneSsidProfile(srcDomain, destDomain);

		// add PseProfile 
		cloneUtil.cloneSingleTableInfo(PseProfile.class, srcDomain, destDomain);
		//add Mstp Region Profile
		cloneUtil.cloneMstpRegion(srcDomain, destDomain);
	    /*
         * Port Access Profile
         */
        cloneUtil.clonePortAccessProfile(srcDomain, destDomain);
        cloneUtil.clonePortTemplateProfile(srcDomain, destDomain);

		cloneUtil.cloneIdsPolicy(srcDomain, destDomain);
		cloneUtil.cloneEthernetAccess(srcDomain, destDomain);
		cloneUtil.cloneSingleTableInfo(QosClassfierAndMarker.class, srcDomain, destDomain);
		cloneUtil.cloneQosClassification(srcDomain, destDomain);
		cloneUtil.cloneApplicationProfile(srcDomain, destDomain);		

		// add Radius Proxy base on radius assignment
		cloneUtil.cloneRadiusProxy(srcDomain, destDomain);

		// add Vlan Dhcp Server
		cloneUtil.cloneVlanDhcpServer(srcDomain, destDomain);
		// add VPN Services
		cloneUtil.cloneVpnService(srcDomain, destDomain);
		cloneUtil.cloneConfigTemplate(srcDomain, destDomain);
		//====================================
		cloneUtil.updatePortTemplateProfileItem(srcDomain, destDomain);
		//====================================		
		cloneUtil.cloneSingleTableInfo(SlaMappingCustomize.class, srcDomain, destDomain);

		/*
		 * HiveAP section
		 */
		cloneUtil.cloneAutoProvisioning(srcDomain, destDomain);
		cloneUtil.cloneHiveApUpdateSetting(srcDomain, destDomain);

		/*
		 * IDP section
		 */
		cloneUtil.cloneIdpSettings(srcDomain, destDomain);

		// add pppoe
		cloneUtil.cloneSingleTableInfo(PPPoE.class, srcDomain, destDomain);

		// add osVersion
		cloneUtil.cloneSingleTableInfo(OsVersion.class, srcDomain, destDomain);

		/*
		 * usb modem
		 */
		cloneUtil.cloneSingleTableInfo(USBModem.class, srcDomain, destDomain);

		/*
		 * device interface ipsubnetwork
		 */
		cloneUtil.cloneSingleTableInfo(DeviceIPSubNetwork.class, srcDomain, destDomain);

		/**
		 * OneTime-password
		 */
		cloneUtil.cloneSingleTableInfo(OneTimePassword.class, srcDomain, destDomain);

		/*
		 * Notification Status Message
		 */
		//cloneUtil.cloneSingleTableInfo(NotificationMessageStatus.class, srcDomain, destDomain);

		/*
		 * IDM Customer
		 */
		cloneUtil.cloneSingleTableInfo(CloudAuthCustomer.class, srcDomain, destDomain);
		
		cloneUtil.cloneSingleTableInfo(GuestAnalyticsInfo.class, srcDomain, destDomain);
		
		/*
		 * Copy related files from source domain to destination domain;
		 */
		cloneDomainFiles(srcDomain.getDomainName(), destDomain.getDomainName());

		// re-initialize Cache values;
		CacheMgmt.getInstance().initCacheValues(destDomain.getId());

		return id;
	}

	/**
	 * remove Domain API
	 *
	 * @param isRemoveDomainSelf
	 *            : when erase db by domain itself, we should remain domain data and remove other
	 *            data belonging to this domain
	 */
	public synchronized void removeDomain(Long domainId, boolean isRemoveDomainSelf) throws Exception {
		removeDomain(domainId, isRemoveDomainSelf, false);
	}
	
	/**
	 * remove Domain API
	 *
	 * @param isRemoveDomainSelf
	 *            : when erase db by domain itself, we should remain domain data and remove other
	 *            data belonging to this domain
	 * @param resetDeviceDefault : reset default to default configuration
	 */
	public synchronized void removeDomain(Long domainId, boolean isRemoveDomainSelf, boolean resetDeviceDefault) 
			throws Exception {
		DomainMaintenance maintenance = DomainMaintenance.getInstance();
		HmDomain deleteDomain = QueryUtil.findBoById(HmDomain.class, domainId);
		FilterParams deleteFilterParams = new FilterParams("owner.id", domainId);

		/**-This params used to remove report tables-**/
		//FilterParams reportDeleteFilterParams = new FilterParams("owner", domainId);

		HmDomain cDomain= CacheMgmt.getInstance().getCacheDomainById(domainId);
		int prevStatus = HmDomain.DOMAIN_DEFAULT_STATUS;
		if (cDomain != null){
			prevStatus = cDomain.getRunStatus();
			cDomain.setRunStatus(HmDomain.DOMAIN_DELETING_STATUS);
		}
		boolean domainDeleted = false;

		try {
			if (deleteDomain == null) {
				// remove cache domain
				CacheMgmt.getInstance().removeCacheValues(domainId);
				domainDeleted = true;

				return;
			}

			if (deleteDomain.isHomeDomain()) {
				QueryUtil.updateBo(HmDomain.class, "partnerId=null",null);
			}

			// user
			List<String> userEmails = null;
			if (isRemoveDomainSelf) {
				userEmails = (List<String>)QueryUtil.executeQuery("select emailAddress from "+HmUser.class.getSimpleName(), null, deleteFilterParams);
				QueryUtil.removeBos(HmUser.class, deleteFilterParams);
				
				// fix bug 28187 Issues with removing old VHM (TO go to VHM use vhm user group)
				if (NmsUtil.isHostedHMApplication() && !deleteDomain.isHomeDomain()) {
					updateTechOpUserWithVhmGroup(domainId);
				}
				
				QueryUtil.removeBos(HmUserGroup.class, deleteFilterParams);
			} else {
				// not remove default user.
				if (NmsUtil.isHostedHMApplication()) {
					// not home domain
					if (!deleteDomain.isHomeDomain()) {
						List<HmUser> users = QueryUtil.executeQuery(HmUser.class, null, new FilterParams(
								"owner.id = :s1 and defaultFlag = :s2", new Object[] { domainId, false }));
						userEmails = new ArrayList<String>();
						for (HmUser user : users) {
							VhmUserSync
									.syncForRemoveVhmUser(deleteDomain.getDomainName(), user.getUserName());
							userEmails.add(user.getEmailAddress());
						}
						QueryUtil.removeBos(HmUser.class, new FilterParams(
							"owner.id = :s1 and defaultFlag = :s2", new Object[] { domainId, false }));
						QueryUtil.removeBos(HmUserGroup.class, new FilterParams(
							"owner.id = :s1 and defaultFlag = :s2", new Object[] { domainId, false }));
					}
				} else {
					userEmails = (List<String>)QueryUtil.executeQuery("select emailAddress from "+HmUser.class.getSimpleName(), null, new FilterParams(
							"owner.id = :s1 and defaultFlag = :s2", new Object[] { domainId, false }));
					QueryUtil.removeBos(HmUser.class, new FilterParams(
						"owner.id = :s1 and defaultFlag = :s2", new Object[] { domainId, false }));
					QueryUtil.removeBos(HmUserGroup.class, new FilterParams(
						"owner.id = :s1 and defaultFlag = :s2", new Object[] { domainId, false }));
				}
			}
			
			// remove user relation settings
			if (null != userEmails && !userEmails.isEmpty()) {
				UsersBussiness.removeUserSettings(userEmails);
			}
			
			//OpenDNS Feature - OpenDNSAccount

			//IGMP 
			bulkRemoveBos(MulticastGroupInterface.class, deleteFilterParams);
			bulkRemoveBos(MulticastGroup.class, deleteFilterParams);
			bulkRemoveBos(IgmpPolicy.class, deleteFilterParams);
			// HiveAp section
			QueryUtil.removeBos(HiveApAutoProvision.class, deleteFilterParams);
			bulkRemoveBos(HiveApSerialNumber.class, deleteFilterParams);
			maintenance.removeHiveAp(domainId, resetDeviceDefault);
			QueryUtil.removeBos(HiveApUpdateResult.class, deleteFilterParams);
			QueryUtil.removeBos(HiveApUpdateSettings.class, deleteFilterParams);
			//forwarding db section
			QueryUtil.removeBos(ForwardingDB.class, deleteFilterParams);
			//clientdeviceinfo
			bulkRemoveBos(ClientDeviceInfo.class, deleteFilterParams);
			//CpuMemoryUsage
			bulkRemoveBos(CpuMemoryUsage.class, deleteFilterParams);
	
			// admin section
			bulkRemoveBos(AhScheduleBackupData.class, deleteFilterParams);
			bulkRemoveBos(HmAuditLog.class, deleteFilterParams);
			bulkRemoveBos(HmUpgradeLog.class, deleteFilterParams);
			bulkRemoveBos(MailNotification4VHM.class, deleteFilterParams);
			bulkRemoveBos(MailNotification.class, deleteFilterParams);
			bulkRemoveBos(HMServicesSettings.class, deleteFilterParams);
			bulkRemoveBos(PlanToolConfig.class, deleteFilterParams);

			//Dashboard
			QueryUtil.removeBos(AhDashboardWidget.class, deleteFilterParams);
			QueryUtil.removeBos(AhDashboardLayout.class, deleteFilterParams);
			QueryUtil.removeBos(DashboardComponent.class, deleteFilterParams);
			QueryUtil.removeBos(DashboardComponentMetric.class, deleteFilterParams);
			QueryUtil.removeBos(AhDashboard.class, deleteFilterParams);

			// reports section
			bulkRemoveBos(AhSummaryPage.class, deleteFilterParams);
			bulkRemoveBos(AhAdminLoginSession.class, deleteFilterParams);
			bulkRemoveBos(AhUserLoginSession.class, deleteFilterParams);
			bulkRemoveBos(AhClientSession.class, new FilterParams("owner.id = :s1",
					new Object[] { domainId }));
			bulkRemoveBos(AhClientSessionHistory.class, deleteFilterParams);
			bulkRemoveBos(AhVIfStats.class, deleteFilterParams);
			bulkRemoveBos(AhRadioAttribute.class, deleteFilterParams);
			bulkRemoveBos(AhRadioStats.class, deleteFilterParams);
			bulkRemoveBos(AhAssociation.class, deleteFilterParams);
			bulkRemoveBos(AhXIf.class, deleteFilterParams);
			bulkRemoveBos(AhNeighbor.class, deleteFilterParams);
			bulkRemoveBos(AhReport.class, deleteFilterParams);
			bulkRemoveBos(AhNewReport.class, deleteFilterParams);
			QueryUtil.removeBos(AhCustomReport.class, deleteFilterParams);
			bulkRemoveBos(AhUserReport.class, deleteFilterParams);
			bulkRemoveBos(AhClientEditValues.class, deleteFilterParams);
			bulkRemoveBos(AhLatestNeighbor.class, deleteFilterParams);
			bulkRemoveBos(AhLatestRadioAttribute.class, deleteFilterParams);
			bulkRemoveBos(AhLatestXif.class, deleteFilterParams);
			bulkRemoveBos(AhACSPNeighbor.class, deleteFilterParams);
			bulkRemoveBos(AhBandWidthSentinelHistory.class, deleteFilterParams);
			bulkRemoveBos(AhInterferenceStats.class, deleteFilterParams);
			bulkRemoveBos(AhLatestACSPNeighbor.class, deleteFilterParams);
			bulkRemoveBos(AhLatestInterferenceStats.class, deleteFilterParams);
			bulkRemoveBos(AhVPNStatus.class, deleteFilterParams);
			
			bulkRemoveBos(LocationRssiReport.class, deleteFilterParams);

			bulkRemoveBos(APConnectHistoryInfo.class, deleteFilterParams);
			bulkRemoveBos(AhClientStats.class, deleteFilterParams);
			bulkRemoveBos(AhClientStatsHour.class, deleteFilterParams);
			bulkRemoveBos(AhClientStatsDay.class, deleteFilterParams);
			bulkRemoveBos(AhClientStatsWeek.class, deleteFilterParams);
			bulkRemoveBos(AhInterfaceStats.class, deleteFilterParams);
			bulkRemoveBos(AhInterfaceStatsHour.class, deleteFilterParams);
			bulkRemoveBos(AhInterfaceStatsDay.class, deleteFilterParams);
			bulkRemoveBos(AhInterfaceStatsWeek.class, deleteFilterParams);
			bulkRemoveBos(AhDeviceStats.class, deleteFilterParams);
			bulkRemoveBos(AhLLDPInformation.class, deleteFilterParams);
			bulkRemoveBos(AhReportCompliance.class, deleteFilterParams);
			bulkRemoveBos(AhDeviceRebootHistory.class, deleteFilterParams);

			// Switch port infos
			bulkRemoveBos(AhSwitchPortInfo.class, deleteFilterParams);
			bulkRemoveBos(AhSwitchPortPeriodStats.class, deleteFilterParams);
			bulkRemoveBos(AhSwitchPortStats.class, deleteFilterParams);
			
			// configuration section
			bulkRemoveBos(CompliancePolicy.class, deleteFilterParams);
			QueryUtil.removeBos(ConfigTemplate.class, deleteFilterParams);
			// Vlan Dhcp Server which is bound by HiveAP
			QueryUtil.removeBos(VlanDhcpServer.class, deleteFilterParams);
			// VPN Service which is bound by WLAN policy
			QueryUtil.removeBos(VpnService.class, deleteFilterParams);
			QueryUtil.removeBos(RoutingProfilePolicy.class, deleteFilterParams);
			// which is bound by WLAN policy
			bulkRemoveBos(MgmtServiceIPTrack.class, deleteFilterParams);
			bulkRemoveBos(LLDPCDPProfile.class, deleteFilterParams);
			QueryUtil.removeBos(QosClassification.class, deleteFilterParams);
			bulkRemoveBos(QosClassfierAndMarker.class, deleteFilterParams);
			// which is unused
			QueryUtil.removeBos(RadiusUserProfileRule.class, deleteFilterParams);
			// which is unused
			QueryUtil.removeBos(EthernetAccess.class, deleteFilterParams);
			QueryUtil.removeBos(IdsPolicy.class, deleteFilterParams);
			QueryUtil.removeBos(HiveProfile.class, deleteFilterParams);
			QueryUtil.removeBos(MgmtServiceTime.class, deleteFilterParams);
			QueryUtil.removeBos(MgmtServiceSnmp.class, deleteFilterParams);
			QueryUtil.removeBos(MgmtServiceSyslog.class, deleteFilterParams);
			QueryUtil.removeBos(MgmtServiceDns.class, deleteFilterParams);
			QueryUtil.removeBos(MgmtServiceOption.class, deleteFilterParams);
			QueryUtil.removeBos(IpFilter.class, deleteFilterParams);
			QueryUtil.removeBos(RadiusOnHiveap.class, deleteFilterParams);
			QueryUtil.removeBos(SsidProfile.class, deleteFilterParams);
			
			QueryUtil.removeBos(ApplicationProfile.class, deleteFilterParams);
			QueryUtil.removeBos(AhAppFlowDay.class, deleteFilterParams);
			QueryUtil.removeBos(AhAppFlowMonth.class, deleteFilterParams);

            // Port profile
            QueryUtil.removeBos(PortGroupProfile.class, deleteFilterParams);
            QueryUtil.removeBos(PortAccessProfile.class, deleteFilterParams);

			// remove PseProfile
			bulkRemoveBos(PseProfile.class,deleteFilterParams);
			// remove Mstp Region
			QueryUtil.removeBos(MstpRegion.class, deleteFilterParams);
			//remove StpSettings
			QueryUtil.removeBos(StpSettings.class, deleteFilterParams);
			//remove SwitchSettings
			QueryUtil.removeBos(SwitchSettings.class, deleteFilterParams);
			
			// radius library sip base on local user group
			QueryUtil.removeBos(RadiusLibrarySip.class, deleteFilterParams);

			QueryUtil.removeBos(LocalUser.class, deleteFilterParams);
			QueryUtil.removeBos(LocalUserGroup.class, deleteFilterParams);

			// Radius Proxy base on radius assignment
			QueryUtil.removeBos(RadiusProxy.class, deleteFilterParams);

			// by standby
			QueryUtil.removeBos(PrintTemplate.class, deleteFilterParams);

			// device login auth base on RadiusAssignment
			QueryUtil.removeBos(HmLoginAuthentication.class, deleteFilterParams);

			QueryUtil.removeBos(RadiusAssignment.class, deleteFilterParams);

			// base on user profile
			QueryUtil.removeBos(FirewallPolicy.class, deleteFilterParams);
			
			// base on device group policy
			QueryUtil.removeBos(UserProfile.class, deleteFilterParams);
			
			//remove QosMarking must after UserProfile
			bulkRemoveBos(QosMarking.class, deleteFilterParams);
			
			// base on device group policy
			QueryUtil.removeBos(SubNetworkResource.class, deleteFilterParams);

			// base on device group policy
			QueryUtil.removeBos(VpnNetwork.class, deleteFilterParams);

			QueryUtil.removeBos(RoutingProfile.class, deleteFilterParams);

			// DNS service
			QueryUtil.removeBos(DnsServiceProfile.class, deleteFilterParams);

			QueryUtil.removeBos(MacPolicy.class, deleteFilterParams);
			QueryUtil.removeBos(IpPolicy.class, deleteFilterParams);
			QueryUtil.removeBos(TunnelSetting.class, deleteFilterParams);
			bulkRemoveBos(ServiceFilter.class, deleteFilterParams);
			QueryUtil.removeBos(LocationServer.class, deleteFilterParams);
			QueryUtil.removeBos(AccessConsole.class, deleteFilterParams);
			QueryUtil.removeBos(MacFilter.class, deleteFilterParams);
			QueryUtil.removeBos(ActiveDirectoryOrOpenLdap.class, deleteFilterParams);
			QueryUtil.removeBos(AlgConfiguration.class, deleteFilterParams);
			QueryUtil.removeBos(QosRateControl.class, deleteFilterParams);
			QueryUtil.removeBos(UserProfileAttribute.class, deleteFilterParams);
			bulkRemoveBos(NetworkService.class, deleteFilterParams);
			QueryUtil.removeBos(RadioProfile.class, deleteFilterParams);
			QueryUtil.removeBos(MacOrOui.class, deleteFilterParams);
			QueryUtil.removeBos(OsObject.class, deleteFilterParams);
			BeOsInfoProcessor.getInstance().removeVhmOsName(domainId);
			QueryUtil.removeBos(DomainObject.class, deleteFilterParams);
			bulkRemoveBos(Scheduler.class, deleteFilterParams);
			bulkRemoveBos(InterRoaming.class, deleteFilterParams);
			QueryUtil.removeBos(DosPrevention.class, deleteFilterParams);
			QueryUtil.removeBos(Cwp.class, deleteFilterParams);
			QueryUtil.removeBos(IpAddress.class, deleteFilterParams);
			QueryUtil.removeBos(LocationClientWatch.class, deleteFilterParams);
			QueryUtil.removeBos(SlaMappingCustomize.class, deleteFilterParams);
			QueryUtil.removeBos(Vlan.class, deleteFilterParams);
			QueryUtil.removeBos(RadiusAttrs.class, deleteFilterParams);

			// Teacher view
			bulkRemoveBos(TvStudentRoster.class, deleteFilterParams);
			bulkRemoveBos(TvResourceMap.class, deleteFilterParams);
			QueryUtil.removeBos(TvClass.class, deleteFilterParams);
			QueryUtil.removeBos(TvComputerCart.class, deleteFilterParams);
			bulkRemoveBos(ViewingClass.class, deleteFilterParams);

			// topology section
			bulkRemoveBos(MapSettings.class, deleteFilterParams);
			bulkRemoveBos(PlannedAP.class, deleteFilterParams);
			bulkRemoveBos(Trex.class, deleteFilterParams);
			maintenance.removeMapContainer(domainId);

			// fault section
			BoMgmt.getTrapMgmt().bulkRemoveAlarms(deleteFilterParams);
			bulkRemoveBos(AhEvent.class, deleteFilterParams);
			bulkRemoveBos(HiveApFilter.class, deleteFilterParams);
			bulkRemoveBos(ActiveClientFilter.class, deleteFilterParams);
			bulkRemoveBos(AhAlarmsFilter.class, deleteFilterParams);
			bulkRemoveBos(AhEventsFilter.class, deleteFilterParams);

			// IDP section
			bulkRemoveBos(Idp.class, deleteFilterParams);
			QueryUtil.removeBos(IdpSettings.class, deleteFilterParams);

			bulkRemoveBos(CwpCertificate.class, deleteFilterParams);

			// start here page
			bulkRemoveBos(HmStartConfig.class, deleteFilterParams);

			bulkRemoveBos(AhMaxClientsCount.class, deleteFilterParams);
			bulkRemoveBos(AhSsidClientsCount.class, deleteFilterParams);
			bulkRemoveBos(AhSsidClientsCountHour.class, deleteFilterParams);
			bulkRemoveBos(AhSsidClientsCountDay.class, deleteFilterParams);
			bulkRemoveBos(AhSsidClientsCountWeek.class, deleteFilterParams);

			bulkRemoveBos(AhClientsOsInfoCount.class, deleteFilterParams);
			bulkRemoveBos(AhClientsOsInfoCountHour.class, deleteFilterParams);
			bulkRemoveBos(AhClientsOsInfoCountDay.class, deleteFilterParams);
			bulkRemoveBos(AhClientsOsInfoCountWeek.class, deleteFilterParams);

			bulkRemoveBos(AhSLAStats.class, deleteFilterParams);

			bulkRemoveBos(AhNewSLAStats.class, deleteFilterParams);
			bulkRemoveBos(AhNewSLAStatsHour.class, deleteFilterParams);
			bulkRemoveBos(AhNewSLAStatsDay.class, deleteFilterParams);
			bulkRemoveBos(AhNewSLAStatsWeek.class, deleteFilterParams);

			// spectral analysis
			bulkRemoveBos(AhSpectralAnalysis.class, deleteFilterParams);

			// HiveAP image info
			bulkRemoveBos(HiveApImageInfo.class, deleteFilterParams);

			// Airtight Setting
			bulkRemoveBos(AirtightSettings.class, deleteFilterParams);

			// WAN & VPN report data
			bulkRemoveBos(AhPortAvailability.class, deleteFilterParams);
			bulkRemoveBos(AhStatsLatencyHigh.class, deleteFilterParams);
			bulkRemoveBos(AhStatsLatencyLow.class, deleteFilterParams);
			bulkRemoveBos(AhStatsThroughputHigh.class, deleteFilterParams);
			bulkRemoveBos(AhStatsThroughputLow.class, deleteFilterParams);
			bulkRemoveBos(AhStatsAvailabilityHigh.class, deleteFilterParams);
			bulkRemoveBos(AhStatsAvailabilityLow.class, deleteFilterParams);
			bulkRemoveBos(AhStatsVpnStatusHigh.class, deleteFilterParams);
			bulkRemoveBos(AhStatsVpnStatusLow.class, deleteFilterParams);

			// device interface ipsubnetwork
			bulkRemoveBos(DeviceIPSubNetwork.class, deleteFilterParams);
			// usb modem
			bulkRemoveBos(USBModem.class, deleteFilterParams);

			// Notification Status Message
			bulkRemoveBos(NotificationMessageStatus.class, deleteFilterParams);

			// pse status
			bulkRemoveBos(AhPSEStatus.class, deleteFilterParams);
			bulkRemoveBos(AhDevicePSEPower.class, deleteFilterParams);

			// remove HMOL_UPGRADE_SERVER_INFO
			bulkRemoveBos(HmolUpgradeServerInfo.class,new FilterParams("vhmId", deleteDomain.getVhmID()));

			// remove pppoe
			bulkRemoveBos(PPPoE.class,deleteFilterParams);

			// remove OsVersion
			bulkRemoveBos(OsVersion.class,deleteFilterParams);

			//remove onetime password
			bulkRemoveBos(OneTimePassword.class, deleteFilterParams);

			//Bonjour Gateway
			QueryUtil.removeBos(BonjourGatewaySettings.class, deleteFilterParams);
			bulkRemoveBos(VlanGroup.class, deleteFilterParams);
			bulkRemoveBos(BonjourService.class,deleteFilterParams);
			bulkRemoveBos(BonjourServiceCategory.class,deleteFilterParams);
			QueryUtil.removeBos(BonjourGatewayMonitoring.class, deleteFilterParams);
			bulkRemoveBos(BonjourRealm.class,deleteFilterParams);

			//DeviceDaInfo
			bulkRemoveBos(DeviceDaInfo.class, deleteFilterParams);
			

			// IDM Customer
			bulkRemoveBos(CloudAuthCustomer.class, deleteFilterParams);
			
			bulkRemoveBos(GuestAnalyticsInfo.class, deleteFilterParams);
			
			// remove PCI Data for fix bug 24103
			bulkRemoveBos(AhPCIData.class, deleteFilterParams);
						
			// AhDashboardAppAp
			bulkRemoveBos(AhDashboardAppAp.class,deleteFilterParams);
			
			//HmClassifierTag
			bulkRemoveBos(HmClassifierTag.class, deleteFilterParams);
			
			//NetworkDeviceHistory
			bulkRemoveBos(NetworkDeviceHistory.class, deleteFilterParams);
			
			//DownloadInfo
			bulkRemoveBos(DownloadInfo.class, deleteFilterParams);
			
			// remove device inventory and reset config 
			bulkRemoveBos(DeviceInventory.class, deleteFilterParams);
			bulkRemoveBos(DeviceResetConfig.class, deleteFilterParams);
			
			//Presence Analytics Customer
			bulkRemoveBos(PresenceAnalyticsCustomer.class, deleteFilterParams);
			
			// OpenDNS Feature - huihe@aerohive.com
			QueryUtil.removeBos(OpenDNSMapping.class, deleteFilterParams);
			QueryUtil.removeBos(OpenDNSDevice.class, deleteFilterParams);
			QueryUtil.removeBos(OpenDNSAccount.class, deleteFilterParams);
			
			//Wifi Client Preferred SSID
			QueryUtil.removeBos(WifiClientPreferredSsid.class, deleteFilterParams);
			
			//Custom Application
			QueryUtil.removeBos(CustomApplication.class, deleteFilterParams);
			
			//CLIBlob
			QueryUtil.removeBos(CLIBlob.class, deleteFilterParams);
			
			/**-This section used to remove report tables-**/
			/**
        		 * @author zdu 
        		 * @mailto zdu@aerohive.com
        		 */
        		cleanReportRowupDataWithDomain( domainId );

			/**-End of section ---------- remove report tables-**/

            // remove MDM configuration
            QueryUtil.removeBos(ConfigTemplateMdm.class, deleteFilterParams);
            // remove MDM Profiles
            //====================================================
            try{
	    		List<MdmProfiles> profiles = QueryUtil.executeQuery(MdmProfiles.class, null,deleteFilterParams);
	    		Set<String> profileNames = new HashSet<String>();
	    		for (MdmProfiles profile : profiles) {
	    			profileNames.add(profile.getMdmProfilesName());
	    		}
	    		ProfileMgrServiceImpl impl =new ProfileMgrServiceImpl();
	    		impl.delMdmProfile(profileNames, domainId.toString());
			} catch (Exception e) {
				log.error("Failed to clone mdmProfiles policies between domains.", e);
			}
            QueryUtil.removeBos(MdmProfiles.class, deleteFilterParams);
            //====================================================
            String domainName = deleteDomain.getDomainName();
            
            //remove the email settings cache
            HmBeAdminUtil.removeMailNotification(domainName);
            
			// remove domain
			if (isRemoveDomainSelf) {

				// remove order key
				log.warn("remove all entitlement key information for the domain ("+domainName+") when remove this domain begin");
				QueryUtil.removeBos(OrderHistoryInfo.class, new FilterParams("domainName", domainName));
				bulkRemoveBos(DomainOrderKeyInfo.class, new FilterParams("domainName", domainName));
				QueryUtil.removeBos(AcmEntitleKeyHistoryInfo.class, new FilterParams("domainName", domainName));
				
				HmBeLicenseUtil.VHM_ORDERKEY_INFO.remove(domainName);
				log.warn("remove all entitlement key information for the domain ("+domainName+") when remove this domain end");
				
				// user register info base on domain id
				bulkRemoveBos(UserRegInfoForLs.class, deleteFilterParams);
				
				//remove tca alarm.
				QueryUtil.removeBos(TCAAlarm.class, deleteFilterParams);
				//remove system log related to this domain.
				QueryUtil.removeBos(HmSystemLog.class, deleteFilterParams);
				
				QueryUtil.bulkRemoveBos(HmDomain.class, new FilterParams("id", domainId), null, null);
				// remove cache domain
				CacheMgmt.getInstance().removeCacheValues(domainId);
				domainDeleted = true;

				// Note: notification of removal for HmDomain should be on the back of database
				// operation.
				BoObserver.notifyListeners(new BoEvent<HmDomain>(deleteDomain, BoEventType.REMOVED));

				// Delete relevant directories to the domain after the specified
				// 'HmDomain' persistence
				// was removed out of database.
				deleteRelevantDirs(domainName);

				bulkRemoveBos(HMUpdateSoftwareInfo.class, new FilterParams("domainName", deleteDomain
						.getDomainName()));

				APSwitchCenter deviceSwitchCenter = AhAppContainer.getBeAdminModule().getDeviceSwitchCenter();
				deviceSwitchCenter.removeSwitchInfo(deleteDomain.getDomainName());
			} else {
				// Delete relevant directories to the domain after the specified
				// 'HmDomain' persistence
				// was removed out of database.
				deleteRelevantDirs(domainName);

				// Create relevant directories as soon as the creation of
				// destination domain is
				// successful.
				createDomainRelevantDirs(deleteDomain.getDomainName(), true);

				createDefaultBo4Domain(deleteDomain);

				// re-initialize Cache values;
				CacheMgmt.getInstance().initCacheValues(deleteDomain.getId());


				// invoke to copy the default files to the domain.
				BeTopoModuleUtil.copyDefaultMapImages(domainName);
			}
		} finally {
			if(!domainDeleted && cDomain != null){
				cDomain.setRunStatus(prevStatus);
			}
		}
	}
	
	/**
	 * TechOP or Partner user go to VHM from Portal will set vhm user group, then this vhm cannot be removed
	 *
	 *@param vhm record db id
	 *
	 */
	private void updateTechOpUserWithVhmGroup(Long vhmId) {
		List<HmUserGroup> groupObj = QueryUtil.executeQuery(HmUserGroup.class, null, new FilterParams(
				"owner.domainName = :s1 and groupName = :s2", new Object[] { HmDomain.HOME_DOMAIN, HmUserGroup.CONFIG }), 1);
		if (!groupObj.isEmpty()) {
			try {
				List<HmUser> homeUsers = QueryUtil.executeQuery(HmUser.class, null, new FilterParams(
						"userGroup.owner.id = :s1 and owner.id != :s2", new Object[] { vhmId, vhmId }));
				if (!homeUsers.isEmpty()) {
					for (HmUser singleUser : homeUsers) {
						singleUser.setUserGroup(groupObj.get(0));
					}
					QueryUtil.bulkUpdateBos(homeUsers);
				}
			} catch (Exception e) {
				log.error("updateTechOpUserWithVhmGroup", "update TO user with vhm group " + vhmId + " failed.", e);
			}
		}
	}

	private void bulkRemoveBos(Class<? extends HmBo> class0, FilterParams filterParams) {
		try {
			QueryUtil.bulkRemoveBos(class0, filterParams, null, null);
		} catch (Exception e) {
			log.error("bulkRemoveBos", "remove " + class0.getName() + " failed.", e);
		}
	}

//	private void bulkRemoveReportsBos(Class<?> class0, FilterParams filterParams) {
//		try {
//			QueryUtil.removeReportsBos(class0, filterParams, null, null);
//		} catch (Exception e) {
//			log.error("bulkRemoveBos", "remove " + class0.getName() + " failed.", e);
//		}
//	}
	/*
	 * this function for create directories for domain<br> Steps:<br> 1. get domain directory<br> 2.
	 * delete directory content if it exists<br> 3. create directory<br>
	 */
	public void createDomainRelevantDirs(String domainName, boolean deleteFirst) {
		String domainDir = AhDirTools.getDomainDir(domainName);

		// Delete the relevant directories first if presence.
		if (deleteFirst) {
		    deleteDir(domainDir);
		}

		// Create the top directory to the domain.
		createDir(domainDir);

		// aerohiveca
		String aerohivecaDir = AhDirTools.getCertificateDir(domainName);
		createDir(aerohivecaDir);

		// CWP
		String cwpDir = AhDirTools.getCwpDir(domainName);
		createDir(cwpDir);

		// CWP Server Key
		String serverKeyDir = AhDirTools.getCwpServerKeyDir(domainName);
		createDir(serverKeyDir);

		// CWP Web Page
		String webPageDir = AhDirTools.getCwpWebDir(domainName);
		createDir(webPageDir);

		// HiveAP Image
		String imageDir = AhDirTools.getImageDir(domainName);
		createDir(imageDir);

		if ("home".equalsIgnoreCase(domainName)) {
			String strSrcPath = "/image";

			File oImage = new File(strSrcPath);
			if (oImage.exists() && oImage.isDirectory()) {
				try {
					FileManager.getInstance().moveDirectory(strSrcPath, imageDir);
				} catch (Exception ex) {
					log.error(ex.getMessage(), ex);
				}
			}
		}

		// L7 signature
		/*if("home".equalsIgnoreCase(domainName)){
			String l7Dir = AhDirTools.getL7SignatureDir(domainName);
			createDir(l7Dir);
			boolean boo = true;
			// copy files to dir
			if(!NmsUtil.isHostedHMApplication()){
				boo = l7signatureCopyFiles(domainName);
				if(!boo){
					log.error(MgrUtil.getUserMessage("error.l7.signature.file.upgrade.fail"));
				}
			}else{
			boo = l7signatureCopyFiles(domainName);
			if(!boo){
				log.error(MgrUtil.getUserMessage("error.l7.signature.file.upgrade.fail"));
			}
			//}
			
			//touch new flag
			File file = new File(L7SignatureMng.FLAG_FILE);
			try {
				if(!file.exists())
					file.createNewFile();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}*/
		
		// Config
		String configDir = AhDirTools.getConfigDir(domainName);
		createDir(configDir);

		// Bootstrap Config
		String bootstrapConfigDir = AhDirTools.getBootstrapConfigDir(domainName);
		createDir(bootstrapConfigDir);

		// New Config
		String newConfigDir = AhDirTools.getNewConfigDir(domainName);
		createDir(newConfigDir);

		// Running Config
		String runningConfigDir = AhDirTools.getRunConfigDir(domainName);
		createDir(runningConfigDir);

		// XML Config
		String xmlConfigDir = AhDirTools.getXmlConfigDir(domainName);
		createDir(xmlConfigDir);

		// Bootstrap XML Config
		String bootstrapXmlConfigDir = AhDirTools.getBootstrapXmlConfigDir(domainName);
		createDir(bootstrapXmlConfigDir);

		// New XML Config
		String newXmlConfigDir = AhDirTools.getNewXmlConfigDir(domainName);
		createDir(newXmlConfigDir);

		// Old XML Config
		String oldXmlConfigDir = AhDirTools.getOldXmlConfigDir(domainName);
		createDir(oldXmlConfigDir);

		// Running XML Config
		String runningXmlConfigDir = AhDirTools.getRunXmlConfigDir(domainName);
		createDir(runningXmlConfigDir);

		// View-based XML Config
		String viewXmlConfigDir = AhDirTools.getViewBasedXmlConfigDir(domainName);
		createDir(viewXmlConfigDir);
	}

	public static boolean l7signatureCopyFiles(String domainName){
		//copy old tar files
		boolean boo = true;
		String cmd;
		
		String upDir = "/HiveManager/l7_signatures";
		cmd = "/bin/cp -rf " + upDir + "/*.tar.gz " +  AhDirTools.getDomainDir(domainName) + "signature";
		String[] cmdS = {"/bin/bash","-c", cmd};
		boo = BeAdminCentOSTools.exeSysCmd(cmdS);
		
		return boo;
	}
	
	/*private boolean l7signaturecopyHMOLFiles(String domainName){
		boolean boo = true;
		String cmd;
		
		String upDir = "/HiveManager/l7_signatures";
		cmd = "/bin/cp -rf " + upDir + "/* " +  AhDirTools.getDomainDir(domainName) + "/signature";
		boo = BeAdminCentOSTools.exeSysCmd(cmd);
		
		return boo;
	}*/
	
	private void deleteRelevantDirs(String domainName) {
		log.info("deleteRelevantDirs", "Deleting relevant directories to the specified domain["
				+ domainName + "].");

		String domainDir = AhDirTools.getDomainDir(domainName);
		boolean isRemoved = deleteDir(domainDir);

		// invoke to delete the map files of this domain.
		BeTopoModuleUtil.deleteDomainDirectory(domainName);

		if (isRemoved) {
			log.info("deleteRelevantDirs", "The relevant directories to the specified domain["
					+ domainName + "] were deleted.");
		}
	}

	private boolean deleteDir(String dirPath) {
		File dir = new File(dirPath);

		if (!dir.exists()) {
			return true;
		}

		if (!dir.isDirectory()) {
			return dir.delete();
		}

		if (dir.list().length == 0) {
			return dir.delete();
		}

		File[] files = dir.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				deleteDir(file.getAbsolutePath());
			} else {
				file.delete();
			}
		}

		return dir.delete();
	}

	public static boolean createDir(String dirPath) {
		File dir = new File(dirPath);

		return !dir.exists() && dir.mkdirs();
	}

	/**
	 * create default cwp certificate for new domain
	 *
	 * @param hmDomain
	 *            -
	 */
	private void createDomainCwpCert(HmDomain hmDomain) {
		// check dir
		String domainName = hmDomain.getDomainName();

		// CWP Server Key
		String serverKeyDir = AhDirTools.getCwpServerKeyDir(domainName);
		AhDirTools.checkDir(serverKeyDir);

		// create default cwp cert
		boolean isSucc;
		try {
			isSucc = HmBeAdminUtil.createDefaultDomainCwp(domainName);
		} catch (Exception e) {
			DebugUtil.adminDebugWarn("DomainMgmtImpl.createDomainCwpCert():catch exception", e);
			isSucc = false;
		}

		if (!isSucc) {
			DebugUtil.adminDebugWarn("Create default cert for domain " + domainName + " failed.");
			return;
		}

		// create bo for relation
		CwpCertificate cwpCert = new CwpCertificate();
		cwpCert.setCertName("Default-CWPCert");
		cwpCert.setDescription("Default cwp key file.");
		cwpCert.setEncrypted(false);
		cwpCert.setIndex(0);
		cwpCert.setOwner(hmDomain);
		cwpCert.setSrcCertName("Default-CWPCert");
		cwpCert.setSrcKeyName("Default-CWPCert");
		cwpCert.setDefaultFlag(true);

		try {
			QueryUtil.createBo(cwpCert);
			isSucc = true;
		} catch (Exception e) {
			DebugUtil.adminDebugWarn("DomainMgmtImpl.createDomainCwpCert():catch exception", e);
			isSucc = false;
		}

		if (!isSucc) {
			DebugUtil.adminDebugWarn("Create CwpCertificate bo for domain " + domainName
					+ " failed.");
		}
	}

	/**
	 * clone the domain's file including downloads and map
	 *
	 * @param strSrcDomainName
	 *            the source domain name
	 * @param strDestDomainName
	 *            the destination domain name
	 * @return failure and success
	 */
	private boolean cloneDomainFiles(String strSrcDomainName, String strDestDomainName) {
		if (null == strSrcDomainName || "".equals(strSrcDomainName.trim())) {
			// add debug log
			DebugUtil.adminDebugInfo("the src domain name is null or ''");

			return false;
		}

		if (null == strDestDomainName || "".equals(strDestDomainName.trim())) {
			// add debug log
			DebugUtil.adminDebugInfo("the dest domain name is null or ''");

			return false;
		}

		String strErrMsg = "clone_error";

		// String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
		// + "/cloneDomainFile.sh " + "\"" + strSrcDomainName + "\"" + " "
		// + "\"" + strDestDomainName + "\"";

		String[] strCmds = { "sh",
				BeAdminCentOSTools.ahShellRoot + "/cloneDomainFile.sh",
				strSrcDomainName,
				strDestDomainName };

		String strReturn = BeAdminCentOSTools.execCmdWithErr(strCmds, strErrMsg);

		if (strReturn.equals(strErrMsg)) {
			// add debug log
			DebugUtil
					.adminDebugInfo("DomainMgmtImpl.cloneDomainFiles(): clone domain files is error");

			return false;
		}

		return true;
	}

	// ------------------------- API for HHM API Engine -----------------------------------

	/**
	 * disable domain api
	 *
	 * @param domainName
	 *            -
	 * @return -
	 */
	public String disableDomain(String domainName) {
		try {
			List<HmDomain> list = QueryUtil.executeQuery(HmDomain.class, null, new FilterParams(
					"domainName", domainName));
			if (list.isEmpty()) {
				return "Can't find VHM (" + domainName + ").";
			}

			HmDomain hmDomain = list.get(0);

			hmDomain.setRunStatus(HmDomain.DOMAIN_DISABLE_STATUS);
			updateDomain(hmDomain);

			return "Disable VHM (" + domainName + ") successfully.";
		} catch (Exception e) {
			log.error("disableDomain", "catch exception", e);
			return "Unable to disable VHM (" + domainName + "). Cause is " + e.getMessage();
		}
	}

	/**
	 * enable domain api
	 *
	 * @param domainName
	 *            -
	 * @return -
	 */
	public String enableDomain(String domainName) {
		try {
			List<HmDomain> list = QueryUtil.executeQuery(HmDomain.class, null, new FilterParams(
					"domainName", domainName));
			if (list.isEmpty()) {
				return "Can't find VHM (" + domainName + ").";
			}

			HmDomain hmDomain = list.get(0);

			hmDomain.setRunStatus(HmDomain.DOMAIN_DEFAULT_STATUS);
			updateDomain(hmDomain);

			return "Enable VHM (" + domainName + ") successfully.";
		} catch (Exception e) {
			log.error("enableDomain", "catch exception", e);
			return "Unable to enable VHM (" + domainName + "). Cause is " + e.getMessage();
		}
	}

	public int[] generateGroupAttribute(int idCount) {

		// set 10 as the start group id
		Integer initId = 10;
		int[] newIds = new int[idCount];
		List<HmUserGroup> attributes = QueryUtil.executeQuery(HmUserGroup.class, null, null);
		Set<Integer> groupIDSet = new HashSet<Integer>(attributes.size());
		for (HmUserGroup o : attributes) {
			groupIDSet.add(o.getGroupAttribute());
		}

		for (int i = 0; i < idCount; i++) {
			while (groupIDSet.contains(initId)) {
				initId++;
			}
			newIds[i] = initId;
			groupIDSet.add(initId);
		}
		return newIds;
	}

	private void updateDefaultGroupAttribute(HmDomain hmDomain) throws Exception {

		String[] names = HmUserGroup.defaultVhmGroupNames;

		List<HmUserGroup> list_group = new ArrayList<HmUserGroup>();
		List<Integer> attributes = new ArrayList<Integer>();
		attributes.add(hmDomain.getMonitoringId());
		attributes.add(hmDomain.getMonitoringConfigId());
		attributes.add(hmDomain.getRfPlanningId());
		attributes.add(hmDomain.getUserMngAdminId());
		attributes.add(hmDomain.getUserMngOperatorId());
		attributes.add(hmDomain.getTeacherId());

		int i = 0;
		HmUserGroup userGroup;
		for (Integer integer : attributes) {
			List<HmUserGroup> bos = QueryUtil.executeQuery(HmUserGroup.class, null, new FilterParams("groupname", names[i]),
					hmDomain.getId());
			if (bos != null && !bos.isEmpty()) {
				userGroup = bos.get(0);
				userGroup.setGroupAttribute(integer);
				list_group.add(userGroup);
			}

			i++;
		}

		// do update
		QueryUtil.bulkUpdateBos(list_group);
	}

	public boolean checkGroupAttributeExist(int groupId, HmDomain owner, String updateGroupName) {

		String sql = "groupattribute = :s1";
		Object[] param = new Object[]{groupId};

		// exclude self when do update
		if (owner != null && owner.getId() != null) {
			sql += " and (owner != :s2 or owner = :s3 and defaultflag = :s4)";
			param = new Object[]{groupId, owner, owner, false};
		}
		List<HmUserGroup> hmUserGroups = QueryUtil.executeQuery(
				HmUserGroup.class, null, new FilterParams(sql, param));
		if (hmUserGroups != null && !hmUserGroups.isEmpty()) {
			// exclude self when do user group update
			if (updateGroupName != null
					&& hmUserGroups.size() == 1
					&& updateGroupName.equals(hmUserGroups.get(0).getGroupName())) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * clean report back end row-up data with one domain id
	 * 
	 * @param domainId
	 * @author zdu
	 * @mailto  zdu@aerohive.com
	 */
    private static void cleanReportRowupDataWithDomain( Long domainId ) {
	try {
	    Class< ? > clazz = Class
		    .forName( "com.ah.nms.worker.report.rowup.migration.ReportRowupDataClean" );
	    Method method = clazz.getDeclaredMethod( "cleanAllWithDomain",
		    new Class[ ] { Long.class } );

	    method.invoke( null, new Object[ ] { domainId } );
	} catch ( Exception e ) {
	    log.error( "cleanReportRowupDataWithDomain()", "catch exception", e );
	}
    }	

}
