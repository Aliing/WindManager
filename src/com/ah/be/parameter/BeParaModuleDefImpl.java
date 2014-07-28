/**
 *@filename		BeParaModuleDefImpl.java
 *@version		v1.85
 *@author		Fiona
 *@createtime	2007-9-3 PM 02:01:54
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.parameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeOperateHMCentOSImpl;
import com.ah.be.admin.restoredb.AhRestoreDBTools;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.BaseModule;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.hiveap.L7SignatureMng;
import com.ah.be.license.BeLicenseModuleCentOsImpl;
import com.ah.be.license.LicenseInfo;
import com.ah.be.os.LinuxNetConfigImpl;
import com.ah.be.os.NetConfigImplInterface;
import com.ah.be.os.WindowsNetConfigImpl;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.parameter.constant.util.AhWebUtil;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.admin.CapwapClient;
import com.ah.bo.admin.CapwapSettings;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmExpressModeEnable;
import com.ah.bo.admin.HmLoginAuthentication;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.gml.PrintTemplate;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.ConfigTemplateType;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.impl.DomainMgmtImpl;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.mobility.QosRateLimit;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.network.AlgConfiguration;
import com.ah.bo.network.AlgConfigurationInfo;
import com.ah.bo.network.AlgConfigurationInfo.GatewayType;
import com.ah.bo.network.Application;
import com.ah.bo.network.ApplicationProfile;
import com.ah.bo.network.BonjourService;
import com.ah.bo.network.BonjourServiceCategory;
import com.ah.bo.network.DomainNameItem;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.DosParams;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.DosPrevention.DosType;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.IpPolicy;
import com.ah.bo.network.IpPolicyRule;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.OsObjectVersion;
import com.ah.bo.network.OsVersion;
import com.ah.bo.network.PseProfile;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.Vlan;
import com.ah.bo.performance.AhCustomReportField;
import com.ah.bo.performance.AhNewReport;
import com.ah.bo.performance.AhReport;
import com.ah.bo.report.AhReportContainer;
import com.ah.bo.tca.TCAAlarm;
import com.ah.bo.tca.TCAUtils;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.RadiusUserProfileRule;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.CwpCertificate;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.SlaMappingCustomize;
import com.ah.bo.wlan.SsidProfile;
import com.ah.bo.wlan.SsidSecurity;
import com.ah.bo.wlan.TX11aOr11gRateSetting;
import com.ah.bo.wlan.TX11aOr11gRateSetting.ARateType;
import com.ah.bo.wlan.TX11aOr11gRateSetting.GRateType;
import com.ah.bo.wlan.TX11aOr11gRateSetting.NRateType;
import com.ah.bo.wlan.Tx11acRateSettings;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.config.ImportTextFileAction;
import com.ah.ui.actions.config.RadioProfileAction;
import com.ah.ui.actions.home.HmServicesAction;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumConstUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.bo.BoGenerationUtil;
import com.ah.xml.navigation.XmlNavigationNode;
import com.ah.xml.navigation.XmlNavigationTree;

/**
 * @author Fiona
 * @version v1.85
 */
public class BeParaModuleDefImpl extends BaseModule implements BeParaModule {

	private static final Tracer	log	= new Tracer(BeParaModuleDefImpl.class
															.getSimpleName());
	
	//facebook, zynga, DropBox, WINUPDAT, RTP, RTCP, skype, NetFlix, YouTube, BitTorrent, GnuTella, KaZaa, eDonkey, 
    //FaceTime,QuickTime, iTunes, iCloud, CiTrix, gmail,hotmail
	final int[] default_app_data = new int[] { 191, 711, 166, 681, 534, 531, 562, 414, 706, 81, 230, 326, 176,
            192, 504, 320, 284, 665, 229, 271};

	public final boolean ifRestore = null == AhAppContainer.HmBe;

	private HmDomain globalDomain;

	private HmDomain homeDomain;
	
	/**
	 * Construct method
	 */
	public BeParaModuleDefImpl() {
		setModuleId(BaseModule.ModuleID_Parameter);
		setModuleName("BeParameterModule");
		getDebuger().setModuleId(BaseModule.ModuleID_Parameter);
		getDebuger().setModuleName("BeParameterModule");
	}

	@Override
	public boolean init() {
		try {
			log.info("init", "Loading constant configs.");
			AhConstantUtil.loadAllConstantConfigs();
			log.info("init", "Constant configs have been loaded.");

			log.info("init", "Loading mac oui dictionary.");
			AhConstantUtil.loadMacOuiDictionary();
			log.info("init", "Mac oui dictionary has been loaded.");

			log.info("init", "Loading Device settings for web.");
			AhWebUtil.getAllDevicesJs();
			log.info("init", "Device settings for web have been loaded.");

			return true;
		} catch (Exception e) {
			log.error("init", "Loading constant configs failed.", e);
			return false;
		}
	}

	/**
	 * @see com.ah.be.app.BaseModule#run()
	 */
	@Override
	public boolean run() {
		constructDefaultProfile();
//		new AhRestoreDBData().restoreFullData();
		// init the status for domain
		BeAdminCentOSTools.initDomainStatus(HmDomain.DOMAIN_DEFAULT_STATUS);
		// call synchronize L7 signature meta information
		new L7SignatureMng().callSyncSignatureInfo();

		// Initialize map hierarchy cache
		BoMgmt.getMapHierarchyCache().init();
		BoMgmt.getMapHierarchyCache().register();

		// Loading all the caches while launching the backend.
		CacheMgmt.getInstance().init();

		return true;
	}

	@Override
	public boolean shutdown() {
		BoMgmt.getMapHierarchyCache().destroy();

		return true;
	}

	/**
	 * construct default profile in db when server start
	 */
	@Override
	public void constructDefaultProfile() {
		try {
			insertDefaultDomains();
		} catch (Exception e) {
			setDebugMessage("Set domain default value catch exception", e);
		}

		try {
			insertDefaultUserGroups();
		} catch (Exception e) {
			setDebugMessage("Set usergroups default value catch exception", e);
		}

		try {
			insertDefaultUsers();
		} catch (Exception e) {
			setDebugMessage("Set users default value catch exception", e);
		}

		try {
			insertEmailNotificationDefault();
		} catch (Exception e) {
			setDebugMessage("Set mail notification default value catch exception", e);
		}

		try {
			insertDefaultCapwapSettings();
		} catch (Exception e) {
			setDebugMessage("Set capwap settings default value catch exception", e);
		}

		try {
			insertDefaultLogSettings();
		} catch (Exception e) {
			setDebugMessage("Set log settings default value catch exception", e);
		}

		try {
			insertDefaultHMServiceSettings();
		} catch (Exception e) {
			setDebugMessage("Set active clients settings default value catch exception", e);
		}

		try {
			insertDefaultExpressModeEnable();
		} catch (Exception e) {
			setDebugMessage("Set default express mode enable value catch exception", e);
		}

		try {
			insertDefaultLoginAuth();
		} catch (Exception e) {
			setDebugMessage("Set login auth settings default value catch exception", e);
		}

		try {
			insertDefaultLserverSettings();
		} catch (Exception e) {
			setDebugMessage("Set license server settings default value catch exception", e);
		}

		try {
			insertDefaultTCASetting();
		} catch (Exception e) {
			setDebugMessage("Set TCA Alarm settings default value catch exception", e);
		}
		
		// Set ip address default value
		insertDefaultIPAddress();

		// Set mac address default value
		insertDefaultMACAddress();

		// Set os object default value
		insertDefaultOsObject();

		// Set vlan default value
		insertDefaultVLAN();

		// Set IP Tracking default value
		insertDefaultIPTracking();

		// Set service filter default value
		insertDefaultServiceFilter();

		// Set mgmt snmp default value
		insertDefaultMgmtSnmp();

		// Set net service default value
		insertDefaultNetService();

		// Set ALG service default value
		insertDefaultALGService();

		// Set RADIUS UP rule default value
		insertDefaultRADIUSUPRule();

		// Set mac dos default value
		insertDefaultMACDos(0);

		// Set mac station default value
		insertDefaultMACDos(1);

		// Set ip dos default value
		insertDefaultMACDos(2);

		// Set radio profile default value
		insertDefaultRadioProfile();

		// Set IP Policy
		insertDefaultIpPolicy();

		// Set qos rate control default value
		insertDefaultQosRateControl();

		// Set role default value
		insertDefaultUserProfile();

		// Set hive profile default value
		insertDefaultHiveId();

		// Set ssid profile default value
		insertDefaultSSIDProfile();

		// Set WIPS policy default value
		// insertDefaultWipsPolicy();

		try {
			insertDefaultApplication();
		} catch (Exception e) {
			setDebugMessage("insertDefaultApplication catch exception", e);
		}
		
		// Set WLAN policy default value
		insertDefaultConfigTemplate();

		// Set Report default value
		insertDefaultReport();
		
		// Set network usage Report default value
		insertDefaultNetworkUsageReport();

		// ha_settings
		insertDefaultHASettings();

		// location client watch
		insertDefaultLocationClientWatch();

		// cwp certificate
		insertDefaultCWPCertificate();

		//
		insertDefaultBonjour();
		
		// clear Bonjour Gateway Monitor
		clearBonjourGatewayMonitor();
		
		/*
		 * GML
		 */
		// print template
		insertDefaultGMLPrintTemplate();

		// Device Domain Objects
		insertDefaultDeviceDomainObjects();
		
		//OS Version
		insertDefaultOSVersion();
		
		// default PSE profile
		insertDefaultPseProfile();
		
		// init report configs
		AhReportContainer.reScanAhReportConfigs();
		
		CreateDefaultWidget.insertDefaultWidget();

		try {
			insertDefaultCapwapClient();
		} catch (Exception e) {
			setDebugMessage("insertDefaultCapwapClient catch exception", e);
		}
		
	}
	
	private List<Application> getApplicationList() {
		List<Application> list = new ArrayList<>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("reporting/ah_appdata.txt")));
		    String line;
		    while((line = (br.readLine())) != null) {
		    	String array[] = line.split("\t");
				 if (array.length < 5) {
					 continue;
				 }
				 Integer appCode = Integer.parseInt(array[0]);
				 String shortName = array[1];
				 String appName = array[2];
				 String description = array[3].trim();
				 String groupName = array[4].trim();
				 list.add(new Application(appCode, appName, shortName, description, groupName, globalDomain));
		    }
		    list.add(new Application(0, "Others", "Others", "Others", "Networking", globalDomain));
		} catch(Exception e) {
			setDebugMessage("reading ah_appdata.txt catch exception", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
		}
		return list;
	}
	
	private boolean isDefaultApp(int appCode) {
		for (int data : default_app_data) {
			if (data == appCode) {
				return true;
			}
		}
		return false;
	}
	
	private void insertDefaultApplication() throws Exception {
		long rowCount = QueryUtil.findRowCount(Application.class, null);
		if (rowCount < 1) {
			List<Application> list = getApplicationList();
			QueryUtil.bulkCreateBos(list);
		}
		// for new application config, there is no default app profile
//		rowCount = QueryUtil.findRowCount(ApplicationProfile.class, null);
//		if (rowCount < 1) {
//			ApplicationProfile profile = new ApplicationProfile();
//			Set<Application> appList = new HashSet<>();
//			List<Application> allList = QueryUtil.executeQuery(Application.class, null, null);
//			for (Application app : allList) {
//				if (isDefaultApp(app.getAppCode())) {
//					appList.add(app);
//				}
//			}	
//			profile.setApplicationList(appList);
//			profile.setProfileName("Others");
//			profile.setDefaultFlag(true);
//			profile.setOwner(globalDomain);
//			QueryUtil.createBo(profile);
//		}
	}

	/**
	 * set default value of email notification settings.
	 *
	 * @throws Exception -
	 */
	private void insertEmailNotificationDefault() throws Exception {
		long rowCount = QueryUtil.findRowCount(MailNotification.class, null);

		if (rowCount > 0) {
			return;
		}

		MailNotification defaultSettings = new MailNotification();
		defaultSettings.setOwner(homeDomain);
		defaultSettings.setSendMailFlag(false);
		defaultSettings.setServerName("");
		defaultSettings.setMailFrom("");
		defaultSettings.setMailTo("");
		defaultSettings.setHdRadio((byte) 16);
		defaultSettings.setCapWap((byte) 29);
		defaultSettings.setConfig((byte) 8);
		defaultSettings.setSecurity((byte) 8);
		defaultSettings.setTimeBomb((byte) 28);
		defaultSettings.setAd((byte) 9);
		defaultSettings.setClient((byte) 5);
		defaultSettings.setTca((byte) 17);
		defaultSettings.setSystem((byte) 9);

		QueryUtil.createBo(defaultSettings);
	}

	/**
	 * set default value of capwap settings
	 *
	 * @throws Exception -
	 */
	private void insertDefaultCapwapSettings() throws Exception {
		long rowCount = QueryUtil.findRowCount(CapwapSettings.class, null);

		if (rowCount > 0) {
			return;
		}

		CapwapSettings defaultSettings = new CapwapSettings();
		defaultSettings.setOwner(homeDomain);
		defaultSettings.setDtlsCapability(CapwapSettings.DTLS_DTLSONLY);
		defaultSettings.setUdpPort(12222);
		defaultSettings.setTimeOut((short) 30);
		defaultSettings.setNeighborDeadInterval((short) 105);
		defaultSettings.setTrapFilterInterval((short)3);
		defaultSettings.setBootStrap("");
		defaultSettings.setPrimaryCapwapIP("");
		defaultSettings.setBackupCapwapIP("");

		QueryUtil.createBo(defaultSettings);
	}

	private void insertDefaultCapwapClient() throws Exception {
//		CapwapClient bo = QueryUtil.findBoByAttribute(CapwapClient.class, "serverType",
//				CapwapClient.SERVERTYPE_PORTAL);
//
//		if (bo != null) {
//			return;
//		}

		long rowCount = QueryUtil.findRowCount(CapwapClient.class, new FilterParams("serverType", CapwapClient.SERVERTYPE_PORTAL));

		if (rowCount > 0) {
			return;
		}

		CapwapClient bo = new CapwapClient();
		bo.setServerType(CapwapClient.SERVERTYPE_PORTAL);
		bo.setCapwapEnable(true);
		bo.setUdpPort(12223);
		bo.setTimeOut((short)30);
		bo.setNeighborDeadInterval((short)105);
		bo.setPrimaryCapwapIP("");

		QueryUtil.createBo(bo);
	}

	/**
	 * set default value of log settings
	 *
	 * @throws Exception -
	 */
	private void insertDefaultLogSettings() throws Exception {
		long rowCount = QueryUtil.findRowCount(LogSettings.class, null);

		if (rowCount > 0) {
			return;
		}

		LogSettings defaultSettings = new LogSettings();
		defaultSettings.setOwner(homeDomain);
		defaultSettings.setAlarmInterval(LogSettings.DEFAULT_ALARM_INTERVAL);
		defaultSettings.setEventInterval(7);
		defaultSettings.setMaxPerfRecord(500000);
		defaultSettings.setMaxHistoryClientRecord(2000000);
		defaultSettings.setInterfaceStatsInterval(30);
		defaultSettings.setStatsStartMinute(0);
		QueryUtil.createBo(defaultSettings);
	}

	/**
	 * ha_settings
	 */
	private void insertDefaultHASettings()
	{
		long rowCount = QueryUtil.findRowCount(HASettings.class, null);

		if (rowCount > 0) {
			return;
		}

		HASettings defaultSettings = new HASettings();
		defaultSettings.setOwner(homeDomain);
		defaultSettings.setHaStatus(HASettings.HASTATUS_DIABLE);
		defaultSettings.setHaNotifyEmail(HASettings.DEFAULT_HA_NOTIFY_EMAIL);
		defaultSettings.setEnableExternalDb(HASettings.EXTERNALDB_DISABLEHA_INITIAL);
		try {
			QueryUtil.createBo(defaultSettings);
		} catch (Exception e) {
			setDebugMessage("insert default ha_settings : ", e);
		}
	}

	/**
	 * location client watch
	 */
	private void insertDefaultCWPCertificate() {
		List<?> list = QueryUtil.executeQuery("select id from " + CwpCertificate.class.getSimpleName(), null, new FilterParams(
				"defaultFlag=:s1 and owner.domainName=:s2", new Object[] { true,
						HmDomain.HOME_DOMAIN }), 1);

		if (!list.isEmpty()) {
			return;
		}

		try {
			// CWP Server Key
			String serverKeyDir = AhDirTools.getCwpServerKeyDir(HmDomain.HOME_DOMAIN);
			AhDirTools.checkDir(serverKeyDir);

			// create default cwp cert
			boolean isSucc;
			//inrestore it will happen null poing, change to other function---lanbao
			//isSucc = HmBeAdminUtil.createDefaultDomainCwp(HmDomain.HOME_DOMAIN);
			isSucc = BeOperateHMCentOSImpl.createDefaultDomainCwp(HmDomain.HOME_DOMAIN);

			if (!isSucc) {
				String strMsg = "Create default cert for domain " + HmDomain.HOME_DOMAIN+ " failed.";
				setDebugMessage(strMsg , new Exception(strMsg));
				return;
			}

			// create bo for relation
			CwpCertificate cwpCert = new CwpCertificate();
			cwpCert.setCertName("Default-CWPCert");
			cwpCert.setDescription("Default cwp key file.");
			cwpCert.setEncrypted(false);
			cwpCert.setIndex(0);
			cwpCert.setOwner(homeDomain);
			cwpCert.setSrcCertName("Default-CWPCert");
			cwpCert.setSrcKeyName("Default-CWPCert");
			cwpCert.setDefaultFlag(true);

			QueryUtil.createBo(cwpCert);
		} catch (Exception e) {
			setDebugMessage("insert default cwp certificate : ", e);
		}
	}
	
	/**
	 * Clear Bonjour Gataway Monitor
	 */
	private void clearBonjourGatewayMonitor(){
		try {
			String sql = " TRUNCATE table BONJOUR_SERVICE_DETAIL CASCADE ";
			QueryUtil.executeNativeUpdate(sql);
			sql = " TRUNCATE table bonjour_gateway_monitoring CASCADE ";
			QueryUtil.executeNativeUpdate(sql);
			
			//QueryUtil.bulkRemoveBos(BonjourRealm.class, null);
			//QueryUtil.removeBos(BonjourGatewayMonitoring.class, null,null,null);
		} catch (Exception e) {
			setDebugMessage("Clear Bonjour Gataway Monitor  : ", e);
		}
	}
	
	/**
	 * Device Bonjour Gataway
	 */
	private void insertDefaultBonjour() {
		String[] serviceCategory = BonjourServiceCategory.getServiceCategory();
		String[] services = BonjourService.getDefaultBonjouServiceName();
				
		Map<String, String[]> serviceNameMap = new HashMap<>();
		Map<String, String[]> serviceTypeMap = new HashMap<>();
		Map<String, Integer> serviceTypeIdMap = new HashMap<>();
		
		for(int i =0;i<services.length;i++){
			serviceTypeIdMap.put(services[i], i+1);
		}
		
		serviceNameMap.put(BonjourServiceCategory.SERVICE_CATEGORY_ALL,new String[]{
				""
			});
		serviceTypeMap.put(BonjourServiceCategory.SERVICE_CATEGORY_ALL,new String[]{
				""
			});
		
		serviceNameMap.put(BonjourServiceCategory.SERVICE_CATEGORY_WILDCARD_SERVICES,new String[]{
				BonjourService.ALL_TCP_SERVICES_NAME,BonjourService.ALL_UDP_SERVICES_NAME,BonjourService.ALL_SERVICES_NAME
			});
		serviceTypeMap.put(BonjourServiceCategory.SERVICE_CATEGORY_WILDCARD_SERVICES,new String[]{
				BonjourService.ALL_TCP_SERVICES_TYPE,BonjourService.ALL_UDP_SERVICES_TYPE,BonjourService.ALL_SERVICES_TYPE
			});
		
		serviceNameMap.put(BonjourServiceCategory.SERVICE_CATEGORY_PRINTING,new String[]{
				BonjourService.INTERNET_PRINTING_PROTOCOL_NAME,BonjourService.JETDIRECT_NAME,BonjourService.LPR_NAME
		});
		serviceTypeMap.put(BonjourServiceCategory.SERVICE_CATEGORY_PRINTING,new String[]{
				BonjourService.INTERNET_PRINTING_PROTOCOL_TYPE,BonjourService.JETDIRECT_TYPE,BonjourService.LPR_TYPE
		});
		
		serviceNameMap.put(BonjourServiceCategory.SERVICE_CATEGORY_FILE_SHARING,new String[]{
				BonjourService.AFP_NAME,BonjourService.SAMBA_NAME,BonjourService.FTP_NAME
			});
		serviceTypeMap.put(BonjourServiceCategory.SERVICE_CATEGORY_FILE_SHARING,new String[]{
				BonjourService.AFP_TYPE,BonjourService.SAMBA_TYPE,BonjourService.FTP_TYPE
		});

		serviceNameMap.put(BonjourServiceCategory.SERVICE_CATEGORY_MEDIA,new String[]{
				BonjourService.ITUNES_NAME,BonjourService.BITTORRENT_NAME,BonjourService.AIRPLAY_NAME,
				BonjourService.REMOTE_AUDIO_OUTPUT_SERVICES_NAME,BonjourService.APPLE_TV_SERVICES_NAME,
				BonjourService.HOME_SHARING_SERVICES_NAME,BonjourService.SLEEP_PROXY_NAME
			});
		serviceTypeMap.put(BonjourServiceCategory.SERVICE_CATEGORY_MEDIA,new String[]{
				BonjourService.ITUNES_TYPE,BonjourService.BITTORRENT_TYPE,BonjourService.AIRPLAY_TYPE,
				BonjourService.REMOTE_AUDIO_OUTPUT_SERVICES_TYPE,BonjourService.APPLE_TV_SERVICES_TYPE,
				BonjourService.HOME_SHARING_SERVICES_TYPE,BonjourService.SLEEP_PROXY_TYPE
		});
		
		serviceNameMap.put(BonjourServiceCategory.SERVICE_CATEGORY_INSTANT_MESSAGEING,new String[]{
				BonjourService.ICHAT_NAME
			});
		serviceTypeMap.put(BonjourServiceCategory.SERVICE_CATEGORY_INSTANT_MESSAGEING,new String[]{
				BonjourService.ICHAT_TYPE
		});
		
		serviceNameMap.put(BonjourServiceCategory.SERVICE_CATEGORY_LOGIN,new String[]{
				BonjourService.SSH_NAME,BonjourService.TELNET_NAME,BonjourService.SHELL_NAME
			});
		serviceTypeMap.put(BonjourServiceCategory.SERVICE_CATEGORY_LOGIN,new String[]{
				BonjourService.SSH_TYPE,BonjourService.TELNET_TYPE,BonjourService.SHELL_TYPE
		});
		
		serviceNameMap.put(BonjourServiceCategory.SERVICE_CATEGORY_AEROHIVE,new String[]{
				BonjourService.AEROHIVE_SERVICES_UDP_NAME,BonjourService.AEROHIVE_SERVICES_TCP_NAME,BonjourService.AEROHIVE_HTTP_PROXY_CONFIGURATION_NAME
			});
		serviceTypeMap.put(BonjourServiceCategory.SERVICE_CATEGORY_AEROHIVE,new String[]{
				BonjourService.AEROHIVE_SERVICES_UDP_TYPE,BonjourService.AEROHIVE_SERVICES_TCP_TYPE,BonjourService.AEROHIVE_HTTP_PROXY_CONFIGURATION_TYPE
		});
		
		serviceNameMap.put(BonjourServiceCategory.SERVICE_CATEGORY_CUSTIOM,new String[]{
		});
		serviceTypeMap.put(BonjourServiceCategory.SERVICE_CATEGORY_CUSTIOM,new String[]{
		});

		for (String sc : serviceCategory) {
			long rowCount = QueryUtil.findRowCount(BonjourServiceCategory.class, new FilterParams("serviceCategoryName", sc));
			if (rowCount > 0) {
				if (BonjourServiceCategory.SERVICE_CATEGORY_MEDIA.equals(sc)) {
					long count = QueryUtil.findRowCount(BonjourService.class, new FilterParams("type", BonjourService.REMOTE_AUDIO_OUTPUT_SERVICES_TYPE));
					if (count > 0) {
						continue;
					} else {
						BonjourServiceCategory bonjourServiceCategory = QueryUtil.findBoByAttribute(BonjourServiceCategory.class, "serviceCategoryName", sc);
						for (int j = 0; j < serviceNameMap.get(sc).length; j++) {
							if ("".equals(serviceNameMap.get(sc)[j])) {
								continue;
							}
							if (BonjourService.ITUNES_NAME.equals(serviceNameMap.get(sc)[j])
									|| BonjourService.BITTORRENT_NAME.equals(serviceNameMap.get(sc)[j])
									|| BonjourService.AIRPLAY_NAME.equals(serviceNameMap.get(sc)[j])) {
								continue;
							}
							BonjourService bonjourService = new BonjourService();
							bonjourService.setOwner(globalDomain);
							bonjourService.setServiceName(serviceNameMap.get(sc)[j]);
							bonjourService.setType(serviceTypeMap.get(sc)[j]);
							bonjourService.setBonjourServiceCategory(bonjourServiceCategory);
							bonjourService.setTypeId(serviceTypeIdMap.get(serviceNameMap.get(sc)[j]));
							try {
								QueryUtil.createBo(bonjourService);
							} catch (Exception e) {
								setDebugMessage("insert default bonjourService  : ", e);
							}
						}
					}
				}

				continue;
			}
			BonjourServiceCategory defaultSettings = new BonjourServiceCategory();
			defaultSettings.setOwner(globalDomain);
			defaultSettings.setServiceCategoryName(sc);
			try {
				QueryUtil.createBo(defaultSettings);
			} catch (Exception e) {
				setDebugMessage("insert default serviceCategory  : ", e);
			}
			BonjourServiceCategory bonjourServiceCategory = QueryUtil.findBoByAttribute(BonjourServiceCategory.class, "serviceCategoryName", sc);

			for (int j = 0; j < serviceNameMap.get(sc).length; j++) {
				if ("".equals(serviceNameMap.get(sc)[j])) {
					continue;
				}
				BonjourService bonjourService = new BonjourService();
				bonjourService.setOwner(globalDomain);
				bonjourService.setServiceName(serviceNameMap.get(sc)[j]);
				bonjourService.setType(serviceTypeMap.get(sc)[j]);
				bonjourService.setBonjourServiceCategory(bonjourServiceCategory);
				bonjourService.setTypeId(serviceTypeIdMap.get(serviceNameMap.get(sc)[j]));
				try {
					QueryUtil.createBo(bonjourService);
				} catch (Exception e) {
					setDebugMessage("insert default bonjourService  : ", e);
				}
			}
		}
	}

	/**
	 * Device Domain Objects
	 */
	private void insertDefaultDeviceDomainObjects() {
		List<DomainObject> list = QueryUtil.executeQuery( DomainObject.class, null, new FilterParams(
			"objName=:s1 OR objName=:s2 OR objName=:s3 OR objName=:s4", new Object[] {HmServicesAction.WEBSENSEQUICKSTART,
			HmServicesAction.BARRACUDAQUICKSTART, BeParaModule.DEVICE_DOMAIN_OBJECT_KEY_WORD_KNOWN, BeParaModule.DEVICE_DOMAIN_OBJECT_KEY_WORD_UNKNOWN}));
		boolean hasWebSense = false;
		boolean hasBarracuda = false;
		boolean hasKnown = false;
		boolean hasUnknown = false;
		for (DomainObject domainObject : list) {
			if (HmServicesAction.WEBSENSEQUICKSTART.equals(domainObject.getObjName())) {
				hasWebSense = true;
			}
			if (HmServicesAction.BARRACUDAQUICKSTART.equals(domainObject.getObjName())) {
				hasBarracuda = true;
			}
			if (BeParaModule.DEVICE_DOMAIN_OBJECT_KEY_WORD_KNOWN.equals(domainObject.getObjName())) {
				hasKnown = true;
			}
			if (BeParaModule.DEVICE_DOMAIN_OBJECT_KEY_WORD_UNKNOWN.equals(domainObject.getObjName())) {
				hasUnknown = true;
			}
		}
		List<DomainObject> domainObjList = new ArrayList<>();
		if (!hasWebSense) {
			String[] webSenseWhitelist = new String[]{
					".mailcontrol.com",
					"download.microsoft.com",
					"ntservicepack.microsoft.com",
					"cdm.microsoft.com",
					"wustat.windows.com",
					"windowsupdate.microsoft.com",
					".windowsupdate.microsoft.com",
					"update.microsoft.com",
					".update.microsoft.com"
			};
			String[] webSenseWhiteListDefault = new String[]{
					"Do not proxy content from Web Security service",
					"Do not proxy Windows update content",
					"Do not proxy Windows update content",
					"Do not proxy Windows update content",
					"Do not proxy Windows update content",
					"Do not proxy Windows update content",
					"Do not proxy Windows update content",
					"Do not proxy Windows update content",
					"Do not proxy Windows update content"
			};
			DomainObject domainObj = new DomainObject();
			domainObj.setObjName(HmServicesAction.WEBSENSEQUICKSTART);
			List<DomainNameItem> items = new ArrayList<>();
			for (int i=0;i<webSenseWhitelist.length;i++) {
				DomainNameItem item = new DomainNameItem();
				item.setDomainName(webSenseWhitelist[i]);
				item.setDescription(webSenseWhiteListDefault[i]);
				items.add(item);
			}
			domainObj.setItems(items);
			domainObj.setAutoGenerateFlag(false);
			domainObj.setObjType(DomainObject.WEB_SECURITY);
			domainObj.setOwner(globalDomain);
			domainObjList.add(domainObj);
		}
		if (!hasBarracuda) {
			String[] barracudaWhitelist = new String[]{
					".purewire.com",
					".barracudanetworks.com",
					"download.microsoft.com",
					"ntservicepack.microsoft.com",
					"cdm.microsoft.com",
					"wustat.windows.com",
					"windowsupdate.microsoft.com",
					".windowsupdate.microsoft.com",
					"update.microsoft.com",
					".update.microsoft.com"
			};
			String[] barracudaWhitelistDefault = new String[]{
					"Do not proxy content from Web Security service",
					"Do not proxy content from Web Security service",
					"Do not proxy Windows update content",
					"Do not proxy Windows update content",
					"Do not proxy Windows update content",
					"Do not proxy Windows update content",
					"Do not proxy Windows update content",
					"Do not proxy Windows update content",
					"Do not proxy Windows update content",
					"Do not proxy Windows update content"
			};
			DomainObject domainObj = new DomainObject();
			domainObj.setObjName(HmServicesAction.BARRACUDAQUICKSTART);
			List<DomainNameItem> items = new ArrayList<>();
			for (int i=0;i<barracudaWhitelist.length;i++) {
				DomainNameItem item = new DomainNameItem();
				item.setDomainName(barracudaWhitelist[i]);
				item.setDescription(barracudaWhitelistDefault[i]);
				items.add(item);
			}
			domainObj.setItems(items);
			domainObj.setAutoGenerateFlag(false);
			domainObj.setObjType(DomainObject.WEB_SECURITY);
			domainObj.setOwner(globalDomain);
			domainObjList.add(domainObj);
		}
		if (!hasKnown) {
			DomainObject domainObj = new DomainObject();
			domainObj.setObjName(BeParaModule.DEVICE_DOMAIN_OBJECT_KEY_WORD_KNOWN);
			List<DomainNameItem> items = new ArrayList<>();
			DomainNameItem item = new DomainNameItem();
			item.setDomainName(BeParaModule.DEVICE_DOMAIN_OBJECT_KEY_WORD_KNOWN);
			item.setDescription("key word");
			items.add(item);
			domainObj.setItems(items);
			domainObj.setAutoGenerateFlag(false);
			domainObj.setOwner(globalDomain);
			domainObjList.add(domainObj);
		}
		if (!hasUnknown) {
			DomainObject domainObj = new DomainObject();
			domainObj.setObjName(BeParaModule.DEVICE_DOMAIN_OBJECT_KEY_WORD_UNKNOWN);
			List<DomainNameItem> items = new ArrayList<>();
			DomainNameItem item = new DomainNameItem();
			item.setDomainName(BeParaModule.DEVICE_DOMAIN_OBJECT_KEY_WORD_UNKNOWN);
			item.setDescription("key word");
			items.add(item);
			domainObj.setItems(items);
			domainObj.setAutoGenerateFlag(false);
			domainObj.setOwner(globalDomain);
			domainObjList.add(domainObj);
		}

		try {
			for (DomainObject domainObject : domainObjList) {
				QueryUtil.createBo(domainObject);
			}
		} catch (Exception e) {
			setDebugMessage("insert default Device Domain object  : ", e);
		}
	}
	
	private void changeOsVersionFile(){
		String fingerprints = ImportTextFileAction.OS_VERSION_FILE_PATH +ImportTextFileAction.OS_VERSION_FILE_NAME;
		String fingerprintsChg = ImportTextFileAction.OS_VERSION_FILE_PATH +ImportTextFileAction.OS_VERSION_FILE_NAME_CHG;
		FileWriter fWriter = null;
		try{
			if(new File(fingerprints).exists() && new File(fingerprintsChg).exists()){
				List<String> lines = NmsUtil.readFileByLines(fingerprints);
				List<String> replaceOsName = new ArrayList<>();
				List<String> replaceOption55 = new ArrayList<>();
				String preHmVer = NmsUtil.getHiveOSVersion(NmsUtil.getVersionInfo(BeAdminCentOSTools.ahBackupdir 
	                    + File.separatorChar + "hivemanager.ver"));
				
				// parse os_dhcp_fingerprints_changes.xml
				SAXReader reader = new SAXReader();
				Document document = reader.read(new File(fingerprintsChg));
				Element root = document.getRootElement();
				List<?> fingerprintElems = root.elements();
				for (Object obj : fingerprintElems) {
					Element fingerprintElem = (Element) obj;
					String osName = fingerprintElem.attributeValue("osname");
					for(Iterator<?> iterator = fingerprintElem.elementIterator();iterator.hasNext();){
						Element option55Elem = (Element) iterator.next();
						String node_option55_text = option55Elem.getText();
						Attribute version = option55Elem.attribute("version");
						String version_text = version.getText();
						if(NmsUtil.compareSoftwareVersion(preHmVer,version_text) <= 0){
							if(!replaceOption55.contains(node_option55_text)){
								replaceOsName.add(osName);
								replaceOption55.add(node_option55_text);
							}
						}
					}
				}
				
				if(replaceOption55.isEmpty()){
					log.debug("No need to modify os_dhcp_fingerprints.txt.");
					FileUtils.deleteQuietly(new File(fingerprintsChg));
					return;
				}
				
				for(String option55 : replaceOption55) {
					int size = lines.size();
					boolean remove =false;
					for(int i=size-1;i>=0;i--){
						if(remove){
							lines.remove(i);
							remove=false;
						} else {
							if(option55.equals(lines.get(i))){
								if(i<size-1 && i>0 && lines.get(i-1).startsWith(ImportTextFileAction.OS_STR) && lines.get(i+1).equals(ImportTextFileAction.END_STR)){
									lines.remove(i+1);
									lines.remove(i);
									remove = true;
								} else {
									lines.remove(i);
								}
							}
						}
					}
				}
				
				//insert
				for(int i=0;i<replaceOption55.size();i++){
					String option55 = replaceOption55.get(i);
					String osName = ImportTextFileAction.OS_STR +replaceOsName.get(i);
					
					if(!lines.contains(option55)){
						if(lines.contains(osName)){
							List<String> temp = lines.subList(lines.indexOf(osName),lines.size());
							int index = lines.indexOf(osName) + temp.indexOf(ImportTextFileAction.END_STR);
							lines.add(index, option55);
							
						} else {
							lines.add(osName);
							lines.add(option55);
							lines.add(ImportTextFileAction.END_STR);
						}
					}
				}
				
				fWriter = new FileWriter(fingerprints, false);
				for(String line : lines){
					if (line != null && line.startsWith(ImportTextFileAction.VERSION_STR)) {
						String version = line.substring(line.indexOf(ImportTextFileAction.VERSION_STR)
								+ ImportTextFileAction.VERSION_STR.length());
						BigDecimal b1 = new BigDecimal(version);
						BigDecimal b2 = new BigDecimal("0.1");
						float fVer = b1.add(b2).floatValue();
						String versionStr = ImportTextFileAction.VERSION_STR + String.valueOf(fVer) + "\r\n";
						fWriter.write(versionStr);
					} else {
						fWriter.write(line+"\r\n");
					}
				}
				fWriter.close();
				
				//compress file
				String strCmd = "";
				StringBuffer strCmdBuf = new StringBuffer();
				strCmdBuf.append("tar zcvf ");
				strCmdBuf.append(ImportTextFileAction.OS_VERSION_FILE_PATH + ImportTextFileAction.OS_VERSION_FILE_NAME_TAR);
				strCmdBuf.append(" -C ");
				strCmdBuf.append(ImportTextFileAction.OS_VERSION_FILE_PATH);
				strCmdBuf.append(" " + ImportTextFileAction.OS_VERSION_FILE_NAME);
				strCmd = strCmdBuf.toString();
				boolean compressResult = BeAdminCentOSTools.exeSysCmd(strCmd);
				if(!compressResult){
					log.error("compress os_dhcp_fingerprints.txt error.");
					return;
				}
				
				FileUtils.deleteQuietly(new File(fingerprintsChg));
			} else {
				if(new File(fingerprintsChg).exists()){
					FileUtils.deleteQuietly(new File(fingerprintsChg));
				}
			}
		} catch(Exception e){
			setDebugMessage("change OsVersionFile error: ", e);
		} finally {
			if(fWriter != null){
				try {
					fWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	private void insertDefaultOSVersion(){
		
		changeOsVersionFile();
		
		long rowCount = QueryUtil.findRowCount(OsVersion.class, new FilterParams(
					"owner.domainName", HmDomain.GLOBAL_DOMAIN));
		if (rowCount > 0) {
			return;
		}	
		
		List<String> lines = null;
		boolean fileExisted = true;
		try {
			lines = NmsUtil.readFileByLines(ImportTextFileAction.OS_VERSION_FILE_PATH + File.separator
					+ ImportTextFileAction.OS_VERSION_FILE_NAME);
		} catch (IOException e) {
			fileExisted = false;
		}
		if(lines == null){
			fileExisted = false;
		}
		if(!fileExisted){
			try {
				lines = NmsUtil.readFileByLines(ImportTextFileAction.OS_VERSION_FILE_PATH + File.separator
						+ ImportTextFileAction.OS_VERSION_FILE_NAME_DEF);
			} catch (IOException e) {
				setDebugMessage("insert default OS Version -> read file: ", e);
			}
		}
		
		if(lines != null && !lines.isEmpty()){
			try {
				List<OsVersion> osVersions = ImportTextFileAction.getOsVersions(lines);
				for (OsVersion osVersion : osVersions) {
					OsVersion osVer = new OsVersion();
					osVer.setOption55(osVersion.getOption55());
					osVer.setOsVersion(osVersion.getOsVersion());
					osVer.setOwner(globalDomain);
					QueryUtil.createBo(osVer);
				}
			} catch (Exception e) {
				setDebugMessage("insert default Device Domain object  : ", e);
			}
		}
	}
	
	/**
	 * location client watch
	 */
	private void insertDefaultLocationClientWatch()
	{
		long rowCount = QueryUtil.findRowCount(LocationClientWatch.class, null);

		if (rowCount > 0) {
			return;
		}

		LocationClientWatch defaultBo = new LocationClientWatch();
		defaultBo.setOwner(homeDomain);
		defaultBo.setDefaultFlag(true);
		defaultBo.setDescription("Default location client watch list, which includes all clients.");
		defaultBo.setName("All Clients");

		try {
			QueryUtil.createBo(defaultBo);
		} catch (Exception e) {
			setDebugMessage("insert default location client watch : ", e);
		}
	}

	/**
	 * set default value of license server settings
	 *
	 * @throws Exception -
	 */
	private void insertDefaultLserverSettings() throws Exception {
//		List<LicenseServerSetting> list = QueryUtil.executeQuery(LicenseServerSetting.class, null, null);
//
//		if (!list.isEmpty()) {
//			return;
//		}

		long rowCount = QueryUtil.findRowCount(LicenseServerSetting.class, null);

		if (rowCount > 0) {
			return;
		}

		LicenseServerSetting defaultSettings = new LicenseServerSetting();
		defaultSettings.setOwner(homeDomain);
		QueryUtil.createBo(defaultSettings);
	}

	/**
	 * set default value of log settings
	 *
	 * @throws Exception -
	 */
	private void insertDefaultExpressModeEnable() throws Exception {
		long rowCount = QueryUtil.findRowCount(HmExpressModeEnable.class, null);

		if (rowCount > 0) {
			return;
		}

		HmExpressModeEnable defaultSettings = new HmExpressModeEnable();
		QueryUtil.createBo(defaultSettings);
	}

	/**
	 * set default value of hm service settings
	 *
	 * @throws Exception -
	 */
	private void insertDefaultHMServiceSettings() throws Exception {
	//	HMServicesSettings hmService = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner.domainName", HmDomain.HOME_DOMAIN);
		List<?> list = QueryUtil.executeQuery("select enableTeacher from " + HMServicesSettings.class.getSimpleName(), null, new FilterParams(
				"owner.domainName", HmDomain.HOME_DOMAIN), 1);

	//	if (null == hmService) {
		if (list.isEmpty()) {
			HMServicesSettings defaultSettings = new HMServicesSettings();
			defaultSettings.setOwner(homeDomain);
			defaultSettings.setEnableClientRefresh(false);
			defaultSettings.setRefreshInterval(60);
			defaultSettings.setRefreshFilterName("");
			defaultSettings.setSessionExpiration(15);
			defaultSettings.setInfiniteSession(false);
            defaultSettings.setEnabledBetaIDM(false);
            defaultSettings.setApiUserName(homeDomain.getDomainName());
			QueryUtil.createBo(defaultSettings);
		} else {
//			Object[] attrs = (Object[]) list.get(0);
//			short hmStatus = (Short) attrs[0];
			boolean enableTeacher = (Boolean) list.get(0);

//			if (NmsUtil.isHostedHMApplication())
//			{
//			//	HmBeOsUtil.HIVEMANAGER_IN_MAINTENANCE = HMServicesSettings.HM_OLINE_STATUS_MAINT == hmService.getHmStatus();
//				HmBeOsUtil.HIVEMANAGER_IN_MAINTENANCE = HMServicesSettings.HM_OLINE_STATUS_MAINT == hmStatus;
//			}

		//	HmBeLicenseUtil.TEACHER_LICENSE_VALID = hmService.isEnableTeacher();
			NmsUtil.TEACHER_VIEW_GLOBAL_ENABLED = enableTeacher;
		}
	}

	private void insertDefaultLoginAuth() throws Exception {
		long rowCount = QueryUtil.findRowCount(HmLoginAuthentication.class, null);

		if (rowCount > 0) {
			return;
		}

		HmLoginAuthentication defaultSettings = new HmLoginAuthentication();
		defaultSettings.setOwner(homeDomain);
		defaultSettings.setHmAdminAuth(EnumConstUtil.ADMIN_USER_AUTHENTICATION_LOCAL);
		defaultSettings.setAuthType(Cwp.AUTH_METHOD_PAP);

		QueryUtil.createBo(defaultSettings);
	}

	private void insertDefaultUsers() throws Exception {
		FilterParams filterParams = new FilterParams("userName", HmUser.ADMIN_USER);

		List<?> boIds = QueryUtil.executeQuery("select id from " + HmUser.class.getSimpleName(), null, filterParams, homeDomain.getId(), 1);

		if (boIds.isEmpty()) {
			filterParams = new FilterParams("groupName", HmUserGroup.ADMINISTRATOR);
			List<HmUserGroup> hmUserGroups = QueryUtil.executeQuery(HmUserGroup.class, null, filterParams, 1);

			if (hmUserGroups.isEmpty()) {
				return;
			}

			HmUserGroup admin = hmUserGroups.get(0);

			HmUser user = new HmUser();
			user.setDefaultFlag(true);
			user.setUserName(HmUser.ADMIN_USER);
			user.setPassword(MgrUtil.digest(NmsUtil.getOEMCustomer().getDefaultHMPassword()));
			user.setUserFullName(NmsUtil.getOEMCustomer().getCompanyName() + " Administrator");
			user.setEmailAddress(NmsUtil.getOEMCustomer().getSupportMail());
			user.setUserGroup(admin);
			user.setOwner(homeDomain);
			BoMgmt.createBo(user, null, null);
		}
	}
	
	private void insertDefaultTCASetting() throws Exception {
		List<TCAAlarm> list = QueryUtil.executeQuery(TCAAlarm.class, null, null);

		if (list.isEmpty()) {
			TCAAlarm alarm = new TCAAlarm();
			alarm.setOwner(homeDomain);
			alarm.setMeatureItem(TCAUtils.DISKUSAGE);
			alarm.setHighThreshold((long) 90);
			alarm.setLowThreshold((long) 70);
			alarm.setDescription(TCAUtils.DISKUSAGE);
			alarm.setInterval((long) 10);
			BoMgmt.createBo(alarm, null, null);
		}
	}

	public void insertDefaultDomains() throws Exception {
		FilterParams filterParams = new FilterParams("domainName", HmDomain.GLOBAL_DOMAIN);
		List<HmDomain> list = QueryUtil.executeQuery(HmDomain.class, null, filterParams);
		if (list.isEmpty()) {
			globalDomain = new HmDomain();
			globalDomain.setDomainName(HmDomain.GLOBAL_DOMAIN);
			QueryUtil.createBo(globalDomain);
		} else {
			globalDomain = list.get(0);
		}
		BoMgmt.getDomainMgmt().setGlobalDomain(globalDomain);
		filterParams = new FilterParams("domainName", HmDomain.HOME_DOMAIN);
		list = QueryUtil.executeQuery(HmDomain.class, null, filterParams);

		
		//String l7TmpDir = "/HiveManager/l7_signatures";
		
		if (list.isEmpty()) { 
			homeDomain = new HmDomain();
			LicenseInfo licenseInfo = ifRestore ? (new BeLicenseModuleCentOsImpl())
					.getLicenseInfo() : HmBeLicenseUtil.getLicenseInfo();
			homeDomain.setMaxApNum(licenseInfo == null ? 0 : licenseInfo.getHiveAps());
			homeDomain.setMaxSimuAp(100);
			homeDomain.setMaxSimuClient(100);
			homeDomain.setDomainName(HmDomain.HOME_DOMAIN);
			homeDomain.setSupportFullMode(true);
			homeDomain.setSupportGM(true);
			Long id = QueryUtil.createBo(homeDomain);
			homeDomain.setId(id);
			// invoke to copy the default files to the home domain.
			BeTopoModuleUtil.copyDefaultMapImages(HmDomain.HOME_DOMAIN);
			// create downloads dir add by lanbao
			String domainDir = AhDirTools.getDownloadsDir() + HmDomain.HOME_DOMAIN + File.separator;
			File fDomainDir = new File(domainDir);
//			if (!fDomainDir.exists()) {
				BoMgmt.getDomainMgmt().createDomainRelevantDirs(HmDomain.HOME_DOMAIN, false);
//			}else{
				//this.copyL7File(l7Dir);
//			}

			BeOperateHMCentOSImpl.createDefaultDomainCERT("home");
		} else {
			homeDomain = list.get(0);
			/*File l7File = new File(l7Dir);
			File l7TmpFIle =new File(l7TmpDir);
			if(l7TmpFIle.isDirectory() && !l7File.isDirectory()){
				//this.copyL7File(l7Dir);
			}*/
		}
		String l7Dir = AhDirTools.getL7SignatureDir(HmDomain.HOME_DOMAIN);
		File updateFile = new File(L7SignatureMng.FLAG_UPDATE);
		if(updateFile.exists()){
			this.copyL7File(l7Dir);
		}
		BoMgmt.getDomainMgmt().setHomeDomain(homeDomain);
	}

	private static void copyL7File(String l7Dir){
		DomainMgmtImpl.createDir(l7Dir);
		boolean boo = DomainMgmtImpl.l7signatureCopyFiles(HmDomain.HOME_DOMAIN);
		if(!boo){
			log.error(MgrUtil.getUserMessage("error.l7.signature.file.upgrade.fail"));
		}
		
		File flagFile = new File(L7SignatureMng.FLAG_FILE);
		File updateFile = new File(L7SignatureMng.FLAG_UPDATE);
		try {
			//touch new flag
			if(!flagFile.exists()){
				flagFile.createNewFile();
			}
			//rm update flag
			if(updateFile.exists()){
				updateFile.delete();
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
	private XmlNavigationTree xmlTree = null;

	private final HmPermission readWritePermission = new HmPermission();

	private final HmPermission readOnlyPermission = new HmPermission();

	public void insertDefaultUserGroups() throws Exception {
		// init permission object
		readWritePermission.addOperation(HmPermission.OPERATION_READ);
		readWritePermission.addOperation(HmPermission.OPERATION_WRITE);

		readOnlyPermission.setOperations(HmPermission.OPERATION_READ);

		// root map
		FilterParams filterParams = new FilterParams("mapName", MapMgmt.ROOT_MAP_NAME);
		List<?> list = QueryUtil.executeQuery(MapNode.class, null, filterParams);
		MapContainerNode rootMap;
		if (list.isEmpty()) {
			rootMap = new MapContainerNode();
			rootMap.setMapName(MapMgmt.ROOT_MAP_NAME);
			rootMap.setOwner(globalDomain);
			rootMap.setId(QueryUtil.createBo(rootMap));
		} else {
			rootMap = (MapContainerNode) list.get(0);
		}
		BoMgmt.getMapMgmt().setRootMapId(rootMap.getId());
		Long homeGlobalMapId = BoMgmt.getMapMgmt().createWorldMap(homeDomain);

		// user group
		List<HmUserGroup> list_group = new ArrayList<>(3);
		filterParams = new FilterParams("groupName", HmUserGroup.ADMINISTRATOR);
		list = QueryUtil.executeQuery("select id from " + HmUserGroup.class.getSimpleName(), null, filterParams, homeDomain.getId(), 1);
		if (list.isEmpty()) {
			xmlTree = loadXmlNavigationTree();

			HmUserGroup adminGroup = new HmUserGroup();
			adminGroup.setGroupName(HmUserGroup.ADMINISTRATOR);
			adminGroup.setDefaultFlag(true);
			adminGroup.setOwner(homeDomain);
			adminGroup.setFeaturePermissions(getSuperUserPermission());
			adminGroup.setGroupAttribute(HmUserGroup.ADMINISTRATOR_ATTRIBUTE);
			list_group.add(adminGroup);

			HmUserGroup monitorGroup = new HmUserGroup();
			monitorGroup.setGroupName(HmUserGroup.MONITOR);
			monitorGroup.setDefaultFlag(true);
			monitorGroup.setOwner(homeDomain);
			monitorGroup.setGroupAttribute(HmUserGroup.MONITOR_ATTRIBUTE);
			monitorGroup.setFeaturePermissions(getPermission(HmUserGroup.MONITOR));
			Map<Long, HmPermission> mapMonitorPermissions = new HashMap<>();
			mapMonitorPermissions.put(homeGlobalMapId, readOnlyPermission);
			monitorGroup.setInstancePermissions(mapMonitorPermissions);
			list_group.add(monitorGroup);

			HmUserGroup configGroup = new HmUserGroup();
			configGroup.setGroupName(HmUserGroup.CONFIG);
			configGroup.setDefaultFlag(true);
			configGroup.setOwner(homeDomain);
			configGroup.setGroupAttribute(HmUserGroup.CONFIG_ATTRIBUTE);
			configGroup.setFeaturePermissions(getPermission(HmUserGroup.CONFIG));
			Map<Long, HmPermission> mapConfigPermissions = new HashMap<>();
			mapConfigPermissions.put(homeGlobalMapId, readWritePermission);
			configGroup.setInstancePermissions(mapConfigPermissions);
			list_group.add(configGroup);
		}

		QueryUtil.bulkCreateBos(list_group);

		// create GM user groups if license valid
		if (HmBeLicenseUtil.GM_LITE_LICENSE_VALID) {
			insertDefaultGMUserGroup();
		}

		insertDefaultTeacherUserGroup();

		insertDefaultVADAndStandAloneHMGroup();
	}

	public void insertDefaultGMUserGroup() throws Exception
	{
		List<?> list = QueryUtil.executeQuery("select id from " + HmUserGroup.class.getSimpleName(), null, new FilterParams("groupName", HmUserGroup.GM_ADMIN), homeDomain.getId(), 1);

		if (list.isEmpty()) {
			List<HmUserGroup> list_group = new ArrayList<>(2);

			HmUserGroup adminGroup = new HmUserGroup();
			adminGroup.setGroupName(HmUserGroup.GM_ADMIN);
			adminGroup.setDefaultFlag(true);
			adminGroup.setOwner(homeDomain);
			adminGroup.setGroupAttribute(HmUserGroup.GM_ADMIN_ATTRIBUTE);
			adminGroup.setFeaturePermissions(getGMPermission(HmUserGroup.GM_ADMIN));
			list_group.add(adminGroup);

			HmUserGroup operatorGroup = new HmUserGroup();
			operatorGroup.setGroupName(HmUserGroup.GM_OPERATOR);
			operatorGroup.setDefaultFlag(true);
			operatorGroup.setOwner(homeDomain);
			operatorGroup.setGroupAttribute(HmUserGroup.GM_OPERATOR_ATTRIBUTE);
			operatorGroup.setFeaturePermissions(getGMPermission(HmUserGroup.GM_OPERATOR));
			list_group.add(operatorGroup);

			QueryUtil.bulkCreateBos(list_group);
		}
	}

	public void insertDefaultTeacherUserGroup() throws Exception
	{
		List<?> list = QueryUtil.executeQuery("select id from " + HmUserGroup.class.getSimpleName(), null, new FilterParams("groupName", HmUserGroup.TEACHER), homeDomain.getId(), 1);

		if (list.isEmpty()) {
			HmUserGroup teacherGroup = new HmUserGroup();
			teacherGroup.setGroupName(HmUserGroup.TEACHER);
			teacherGroup.setDefaultFlag(true);
			teacherGroup.setOwner(homeDomain);
			teacherGroup.setGroupAttribute(HmUserGroup.TEACHER_ATTRIBUTE);
			teacherGroup.setFeaturePermissions(getTeacherPermission());

			QueryUtil.createBo(teacherGroup);
		}
	}

	public void insertDefaultVADAndStandAloneHMGroup() throws Exception
	{
		if (!NmsUtil.isHostedHMApplication()) {
			return;
		}

		List<?> list = QueryUtil.executeQuery("select id from " + HmUserGroup.class.getSimpleName(), null, new FilterParams("groupName", HmUserGroup.VAD), homeDomain.getId(), 1);

		if (list.isEmpty()) {
			List<HmUserGroup> list_group = new ArrayList<>(2);

			HmUserGroup vadGroup = new HmUserGroup();
			vadGroup.setGroupName(HmUserGroup.VAD);
			vadGroup.setDefaultFlag(true);
			vadGroup.setOwner(homeDomain);
			vadGroup.setGroupAttribute(HmUserGroup.VAD_ATTRIBUTE);
			vadGroup.setFeaturePermissions(getPermission(HmUserGroup.CONFIG));
			list_group.add(vadGroup);

			HmUserGroup standaloneGroup = new HmUserGroup();
			standaloneGroup.setGroupName(HmUserGroup.STANDALONE_HM);
			standaloneGroup.setDefaultFlag(true);
			standaloneGroup.setOwner(homeDomain);
			standaloneGroup.setGroupAttribute(HmUserGroup.STANDALONE_HM_ATTRIBUTE);
			standaloneGroup.setFeaturePermissions(getPermission(HmUserGroup.MONITOR));
			list_group.add(standaloneGroup);

			QueryUtil.bulkCreateBos(list_group);
		}
	}

	final List<String> keys = new ArrayList<>();

	/*
	 * XML navigation tree is loaded upon tomcat startup.
	 */
	public XmlNavigationTree loadXmlNavigationTree() {
		XmlNavigationTree xmlNavigationTree = null;
		try {
			String hmRoot = System.getenv("HM_ROOT");
			String xmlNavTreePath = hmRoot != null ? hmRoot + File.separator + "WEB-INF"
					+ File.separator + "navigation.xml" : "webapps" + File.separator + "hm"
					+ File.separator + "WEB-INF" + File.separator + "navigation.xml";
			xmlNavigationTree = MgrUtil.unmarshal(xmlNavTreePath);
		} catch (JAXBException e) {
			setDebugMessage("loadNavigationTree::Load navigation tree failed!", e);
		}
		return xmlNavigationTree;
	}

	protected void createSuperUserPermissions(XmlNavigationNode xmlNode) {
		if (xmlNode == null) {
			return;
		}
		for (XmlNavigationNode xmlChildNode : xmlNode.getNode()) {
			if (xmlChildNode != null) {
				String key = xmlChildNode.getKey();

				// super user can't cover user manager features also.
				if (key != null
						&& (key.equals(Navigation.L1_FEATURE_USER_MGR) || key
								.equals(Navigation.L1_FEATURE_USER_REPORTS))) {
					continue;
				}

				keys.add(key);
				createSuperUserPermissions(xmlChildNode);
			}
		}
	}

	/**
	 * get super user permission
	 *
	 * @return -
	 */
	private Map<String, HmPermission> getSuperUserPermission() {
		Map<String, HmPermission> map = new HashMap<>();

		if (xmlTree != null) {
			keys.clear();
			createSuperUserPermissions(xmlTree.getTree());
			for (String key : keys) {
				if (key == null) {
						continue;
				}
				map.put(key, readWritePermission);
			}
		}
		return map;
	}

	/**
	 * grouptype define in HmUserGroup.class, include: monitor, config, administrator
	 *
	 * @param groupType -
	 * @return -
	 */
	private Map<String, HmPermission> getPermission(String groupType) {
		Map<String, HmPermission> map = new HashMap<>();

		if (xmlTree == null) {
			log.error("getPermission", "create permissions for group type(" + groupType
					+ "), but xmlTree is null.");

			return map;
		}

		keys.clear();
		createPermissions(xmlTree.getTree());

		for (String key : keys) {
			if (key == null) {
				continue;
			}
			HmPermission permission = new HmPermission();
			permission.setOperations(HmPermission.OPERATION_READ);

			if (groupType.equals(HmUserGroup.CONFIG)) {
				if (!(key.equals(Navigation.L2_FEATURE_LICENSEMGR)
						|| key.equals(Navigation.L2_FEATURE_HM_SERVICES) || key
						.equals(Navigation.L2_FEATURE_HM_SETTINGS))) {
					permission.addOperation(HmPermission.OPERATION_WRITE);
				}
			} else if (groupType.equals(HmUserGroup.MONITOR)) {
				if (key.equals(Navigation.L2_FEATURE_USER_PASSWORD_MODIFY)) {
					permission.addOperation(HmPermission.OPERATION_WRITE);
				}
			}

			map.put(key, permission);
		}
		return map;
	}

	protected void createPermissions(XmlNavigationNode xmlNode) {
		if (xmlNode == null) {
			return;
		}
		for (XmlNavigationNode xmlChildNode : xmlNode.getNode()) {
			if (xmlChildNode != null) {
				String key = xmlChildNode.getKey();

				if (key != null
						&& (key.equals(Navigation.L1_FEATURE_USER_MGR) || key
								.equals(Navigation.L1_FEATURE_USER_REPORTS))) {
					continue;
				}

				if (xmlNode.getKey() != null && xmlNode.getKey().equals(Navigation.L1_FEATURE_HOME)) {
					if (!(key.equals(Navigation.L2_FEATURE_USER_PASSWORD_MODIFY)
							|| key.equals(Navigation.L2_FEATURE_ADMINISTRATION)
							|| key.equals(Navigation.L2_FEATURE_SUMMARY)
							)) {
						continue;
					}
				}
				if (xmlNode.getKey() != null && xmlNode.getKey().equals(Navigation.L1_FEATURE_DASH)) {
					if (!(key.equals(Navigation.L2_FEATURE_DASHBOARD))) {
						continue;
					}
				}

				if (xmlNode.getKey() != null && xmlNode.getKey().equals(Navigation.L2_FEATURE_ADMINISTRATION)) {
					if (!(key.equals(Navigation.L2_FEATURE_LICENSEMGR)
							|| key.equals(Navigation.L2_FEATURE_HM_SETTINGS) || key
							.equals(Navigation.L2_FEATURE_HM_SERVICES))) {
						continue;
					}
				}

//				if (xmlNode.getKey() != null && xmlNode.getKey().equals("hiveMgrConfig")) {
//					if (!key.equals(Navigation.L2_FEATURE_DATETIMECONFIG)) {
//						continue;
//					}
//				}

				keys.add(key);
				createPermissions(xmlChildNode);
			}
		}
	}

	private Map<String, HmPermission> getTeacherPermission() {
		Map<String, HmPermission> map = new HashMap<>();
		map.put(Navigation.L2_FEATURE_USER_PASSWORD_MODIFY, readWritePermission);
		return map;
	}

	private Map<String, HmPermission> getGMPermission(String groupType) {
		Map<String, HmPermission> map = new HashMap<>();
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
	private void createPermissionsPure(XmlNavigationNode xmlNode)
	{
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

	private void insertDefaultIPAddress() {
		try {
			NetConfigImplInterface networkService;

			if (ifRestore) {
				String os = System.getProperty("os.name");
				networkService = os.toLowerCase().contains("windows") ? new WindowsNetConfigImpl() : new LinuxNetConfigImpl();
			} else {
				networkService = AhAppContainer.HmBe.getOsModule().getNetworkService();
			}

			String hmIp = networkService.getHiveManagerIPAddr();
			IpAddress ipBo = HmBeParaUtil.getDefaultProfile(IpAddress.class, null);
			if (null == ipBo) {
				IpAddress dto_ip = new IpAddress();
				dto_ip.setAddressName(BeParaModule.DEFAULT_IP_ADDRESS_NAME);
				dto_ip.setOwner(globalDomain);
				List<SingleTableItem> items = new ArrayList<>();
				SingleTableItem single = new SingleTableItem();
				single.setDescription("Default System IP address");
				single.setIpAddress(hmIp);
				single.setNetmask(IpAddress.NETMASK_OF_SINGLE_IP);
				single.setType(SingleTableItem.TYPE_GLOBAL);
				items.add(single);
				dto_ip.setItems(items);
				dto_ip.setDefaultFlag(true);

				QueryUtil.createBo(dto_ip);
			} else {
				// always update default HiveManager IP for HA
				List<SingleTableItem> items = new ArrayList<>();
				SingleTableItem single = new SingleTableItem();
				single.setDescription("Default System IP address");
				single.setIpAddress(hmIp);
				single.setNetmask(IpAddress.NETMASK_OF_SINGLE_IP);
				single.setType(SingleTableItem.TYPE_GLOBAL);
				items.add(single);

				ipBo.setItems(items);

				QueryUtil.updateBo(ipBo);
			}
		} catch (Exception e) {
			setDebugMessage("insertDefaultIPAddress", e);
		}
	}

	private void insertDefaultMACAddress() {
		Map<String, Object> map = new HashMap<>();
		try {
			for (String[] macInfo : BeParaModule.DEFAULT_MAC_OUIS) {
				String[] macs = macInfo[1].split(",");
				for (String mac : macs) {
				map.put("macOrOuiName", BeParaModule.DEFAULT_MAC_ADDRESS_NAME);
			//	MacOrOui macList = HmBeParaUtil.getDefaultProfile(MacOrOui.class, map);
				Long macOuiId = HmBeParaUtil.getDefaultProfileId(MacOrOui.class, map);
			//	if (!(null != macList && BeParaModule.DEFAULT_MAC_OUI_NAME.equals(macInfo[0]))) {
				if (!(macOuiId != null && BeParaModule.DEFAULT_MAC_OUI_NAME.equals(macInfo[0]))) {
						map.put("macOrOuiName", BeParaModule.DEFAULT_MAC_OUI_NAME.equals(macInfo[0]) ? macInfo[0]+"-"+mac : macInfo[0]);
			//		macList = HmBeParaUtil.getDefaultProfile(MacOrOui.class, map);
					macOuiId = HmBeParaUtil.getDefaultProfileId(MacOrOui.class, map);
				}
			//	if (null == macList) {
				if (macOuiId == null) {
					MacOrOui dto_MacAddr = new MacOrOui();
						dto_MacAddr.setMacOrOuiName(BeParaModule.DEFAULT_MAC_OUI_NAME.equals(macInfo[0]) ? macInfo[0]+"-"+mac : macInfo[0]);
					dto_MacAddr.setOwner(globalDomain);
					List<SingleTableItem> items = new ArrayList<>();
					SingleTableItem single = new SingleTableItem();
					single.setDescription(macInfo[2]);
						single.setMacEntry(mac);
					single.setType(SingleTableItem.TYPE_GLOBAL);
					items.add(single);
					dto_MacAddr.setItems(items);
					dto_MacAddr.setTypeFlag(MacOrOui.TYPE_MAC_OUI);
					dto_MacAddr.setDefaultFlag(true);
					QueryUtil.createBo(dto_MacAddr);
				} else if(NmsUtil.getHiveApMacOui()[0].equalsIgnoreCase(mac)){
					MacOrOui macOui = QueryUtil.findBoById(MacOrOui.class, macOuiId);
					if (macOui != null && BeParaModule.DEFAULT_MAC_ADDRESS_NAME.equals(macOui.getMacOrOuiName())) {
					macOui.setMacOrOuiName(DEFAULT_MAC_OUI_NAME + "-" + mac);
					QueryUtil.updateBo(macOui);
					}
				}
				}
			}
		} catch (Exception e) {
			setDebugMessage("insert default MAC ADDRESS : ", e);
		}
	}

	private void insertDefaultOsObject() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			for (int i=0; i<BeParaModule.DEFAULT_OS_OBJECTS_NAMES.length; i++) {
				map.put("osName", BeParaModule.DEFAULT_OS_OBJECTS_NAMES[i]);
				Long osId = HmBeParaUtil.getDefaultProfileId(OsObject.class, map);
				if (osId == null) {
					OsObject dto_os = new OsObject();
					dto_os.setOsName(BeParaModule.DEFAULT_OS_OBJECTS_NAMES[i]);
					dto_os.setOwner(globalDomain);
					List<OsObjectVersion> items = new ArrayList<OsObjectVersion>();
					List<OsObjectVersion> dhcpItems = new ArrayList<OsObjectVersion>();
					
					String[][] itemValues = DEFAULT_OS_OBJECTS_CONFIGURATION[i][0];
					String[][] dhcpItemValues = DEFAULT_OS_OBJECTS_CONFIGURATION[i][1];

					for (String[] versions : itemValues) {
						OsObjectVersion single = new OsObjectVersion();
						single.setOsVersion(versions[0]);
						single.setDescription(versions[1]);
						items.add(single);
					}
					for (String[] versions : dhcpItemValues) {
						OsObjectVersion single = new OsObjectVersion();
						single.setOsVersion(versions[0]);
						dhcpItems.add(single);
					}
					
					dto_os.setItems(items);
					dto_os.setDhcpItems(dhcpItems);
					dto_os.setDefaultFlag(true);
					QueryUtil.createBo(dto_os);
				}
			}
		} catch (Exception e) {
			setDebugMessage("insert default OS Object : ", e);
		}
	}

	private void insertDefaultVLAN() {
	//	Vlan bo = HmBeParaUtil.getDefaultProfile(Vlan.class, null);
		Long vlanId = HmBeParaUtil.getDefaultProfileId(Vlan.class, null);

	//	if (null == bo) {
		if (vlanId == null) {
			Vlan dto_Vlan = new Vlan();
			dto_Vlan.setVlanName("1");
			dto_Vlan.setOwner(globalDomain);
			List<SingleTableItem> items = new ArrayList<>();
			SingleTableItem single = new SingleTableItem();
			single.setDescription("Default VLAN");
			single.setVlanId(1);
			single.setType(SingleTableItem.TYPE_GLOBAL);
			items.add(single);
			dto_Vlan.setItems(items);
			dto_Vlan.setDefaultFlag(true);
			try {
				QueryUtil.createBo(dto_Vlan);
			} catch (Exception e) {
				setDebugMessage("insert default VLAN : ", e);
			}
		}
	}

	private void insertDefaultIPTracking() {
		// For HiveAP
		List<?> boIds = QueryUtil.executeQuery("select id from " + MgmtServiceIPTrack.class.getSimpleName(), null, new FilterParams("trackName",
			BeParaModule.DEFAULT_IP_TRACKING_AP_NAME_NEW), globalDomain.getId());

		if (boIds.isEmpty()) {
			MgmtServiceIPTrack ipTrack = new MgmtServiceIPTrack();
			ipTrack.setTrackName(BeParaModule.DEFAULT_IP_TRACKING_AP_NAME_NEW);
			ipTrack.setOwner(globalDomain);
			ipTrack.setUseGateway(true);
//			ipTrack.setInterval((short)10);
//			ipTrack.setTimeout((short)2);
			ipTrack.setInterval((short)2);
			ipTrack.setRetryTime((short)2);
			ipTrack.setDescription(MgrUtil.getResourceString("policy.predefined.description.qs.iptrack.ap"));
			try {
				QueryUtil.createBo(ipTrack);
			} catch (Exception e) {
				setDebugMessage("insert default IP Tracking : ", e);
			}
		}

		// For BR 100
		boIds = QueryUtil.executeQuery("select id from " + MgmtServiceIPTrack.class.getSimpleName(), null, new FilterParams("trackName",
			BeParaModule.DEFAULT_IP_TRACKING_BR_NAME_NEW), globalDomain.getId());

		if (boIds.isEmpty()) {
			MgmtServiceIPTrack ipTrack = new MgmtServiceIPTrack();
			ipTrack.setTrackName(BeParaModule.DEFAULT_IP_TRACKING_BR_NAME_NEW);
			ipTrack.setOwner(globalDomain);
			ipTrack.setIpAddresses(HmStartConfig.EXPRESS_MODE_DEFAULT_NTP_IP1+","+HmStartConfig.EXPRESS_MODE_DEFAULT_NTP_IP2);
			ipTrack.setUseGateway(true);
//			ipTrack.setInterval((short)30);
//			ipTrack.setTimeout((short)5);
			ipTrack.setInterval((short)5);
			ipTrack.setRetryTime((short)2);
			ipTrack.setDescription(MgrUtil.getResourceString("policy.predefined.description.qs.iptrack.router"));
			try {
				QueryUtil.createBo(ipTrack);
			} catch (Exception e) {
				setDebugMessage("insert default IP Tracking : ", e);
			}
		}

		// For VPN Gateway
		boIds = QueryUtil.executeQuery("select id from " + MgmtServiceIPTrack.class.getSimpleName(), null, new FilterParams("trackName",
			BeParaModule.DEFAULT_IP_TRACKING_VPN_GATEWAY_NAME_NEW), globalDomain.getId());

		if (boIds.isEmpty()) {
			MgmtServiceIPTrack ipTrack = new MgmtServiceIPTrack();
			ipTrack.setTrackName(BeParaModule.DEFAULT_IP_TRACKING_VPN_GATEWAY_NAME_NEW);
			ipTrack.setOwner(globalDomain);
			ipTrack.setUseGateway(true);
//			ipTrack.setInterval((short)30);
//			ipTrack.setTimeout((short)5);
			ipTrack.setInterval((short)5);
			ipTrack.setRetryTime((short)2);
			ipTrack.setDescription(MgrUtil.getResourceString("policy.predefined.description.qs.iptrack.vpn.gateway"));
			try {
				QueryUtil.createBo(ipTrack);
			} catch (Exception e) {
				setDebugMessage("insert default IP Tracking : ", e);
			}
		}
		
		// For Track Wan
			boIds = QueryUtil.executeQuery("select id from " + MgmtServiceIPTrack.class.getSimpleName(), null, new FilterParams("trackName",
				BeParaModule.DEFAULT_IP_TRACKING_WAN_BR_NAME_NEW), globalDomain.getId());

			if (boIds.isEmpty()) {
				MgmtServiceIPTrack ipTrack = new MgmtServiceIPTrack();
				ipTrack.setTrackName(BeParaModule.DEFAULT_IP_TRACKING_WAN_BR_NAME_NEW);
				ipTrack.setOwner(globalDomain);
				ipTrack.setGroupType(1);
				ipTrack.setIpAddresses(HmStartConfig.EXPRESS_MODE_DEFAULT_NTP_IP1+","+HmStartConfig.EXPRESS_MODE_DEFAULT_NTP_IP2);
				ipTrack.setUseGateway(true);
				ipTrack.setInterval((short)6);
				ipTrack.setRetryTime((short)2);
				ipTrack.setDescription("Default track IP group for router WAN ports");
				try {
					QueryUtil.createBo(ipTrack);
				} catch (Exception e) {
					setDebugMessage("insert default Tracking WAN: ", e);
				}
			}		
	}

	private void insertDefaultServiceFilter() {
	//	ServiceFilter bo = HmBeParaUtil.getDefaultProfile(ServiceFilter.class, null);
		Long serviceFilterId = HmBeParaUtil.getDefaultProfileId(ServiceFilter.class, null);
	//	if (null == bo) {
		if (serviceFilterId == null) {
			ServiceFilter dto_Filter = new ServiceFilter();
			dto_Filter.setOwner(globalDomain);
			dto_Filter.setFilterName(BeParaModule.DEFAULT_SERVICE_FILTER_NAME);
			dto_Filter.setDescription("Default service filter");
			dto_Filter.setEnablePing(true);
			dto_Filter.setEnableSNMP(false);
			dto_Filter.setEnableSSH(true);
			dto_Filter.setEnableTelnet(false);
			dto_Filter.setDefaultFlag(true);
			try {
				QueryUtil.createBo(dto_Filter);
			} catch (Exception e) {
				setDebugMessage("insert Service Filter : ", e);
			}
		}
	}

	private void insertDefaultMgmtSnmp() {
	//	MgmtServiceSnmp bo = HmBeParaUtil.getDefaultProfile(MgmtServiceSnmp.class, null);
		Long snmpId = HmBeParaUtil.getDefaultProfileId(MgmtServiceSnmp.class, null);
	//	if (null == bo) {
		if (snmpId == null) {
			MgmtServiceSnmp dto_Snmp = new MgmtServiceSnmp();
			dto_Snmp.setOwner(globalDomain);
			dto_Snmp.setMgmtName(BeParaModule.DEFAULT_SERVICE_SNMP_NAME);
			dto_Snmp.setDescription("Default service SNMP");
			dto_Snmp.setContact("");
			dto_Snmp.setEnableSnmp(false);
			dto_Snmp.setEnableCapwap(true);
			dto_Snmp.setDefaultFlag(true);
			try {
				QueryUtil.createBo(dto_Snmp);
			} catch (Exception e) {
				setDebugMessage("insert default Mgmt SNMP : ", e);
			}
		}
	}

	private void insertDefaultMACDos(int dosType) {
		Map<String, Object> map = new HashMap<>();
		switch (dosType) {
		case 0:
			map.put("dosType", DosType.MAC);
			break;
		case 1:
			map.put("dosType", DosType.MAC_STATION);
			break;
		case 2:
			map.put("dosType", DosType.IP);
			break;
		default:
			break;
		}
	//	DosPrevention dosList = HmBeParaUtil.getDefaultProfile(DosPrevention.class, map);
		Long dosPreventionId = HmBeParaUtil.getDefaultProfileId(DosPrevention.class, map);
	//	if (null == dosList) {
		if (dosPreventionId == null) {
			DosPrevention dto_MacDos = new DosPrevention();
			switch (dosType) {
			case 0:
				dto_MacDos.setDosPreventionName(BeParaModule.DEFAULT_MAC_DOS_NAME);
				dto_MacDos.setDosType(DosType.MAC);
				dto_MacDos.setDescription("Default MAC DoS");
				break;
			case 1:
				dto_MacDos.setDosPreventionName(BeParaModule.DEFAULT_MAC_DOS_STATION_NAME);
				dto_MacDos.setDosType(DosType.MAC_STATION);
				dto_MacDos.setDescription("Default MAC DoS Station");
				break;
			case 2:
				dto_MacDos.setDosPreventionName(BeParaModule.DEFAULT_IP_DOS_NAME);
				dto_MacDos.setEnabledSynCheck(false);
				dto_MacDos.setDosType(DosType.IP);
				dto_MacDos.setDescription("Default IP DoS");
				break;
			default:
				break;
			}
			dto_MacDos.setDefaultFlag(true);
			dto_MacDos.setDosParamsMap(getDosMap(dosType));
			dto_MacDos.setOwner(globalDomain);

			try {
				QueryUtil.createBo(dto_MacDos);
			} catch (Exception e) {
				setDebugMessage("insert default MAC Dos(" + dosType + "): ", e);
			}
		}
	}

	private void insertDefaultNetService() {
		Map<String, Object> map = new HashMap<>();
		try {
			for (String[] preServiceInfo : BeParaModule.NETWORK_PRE_DEFIND_SERVICES) {
				map.put("serviceName", preServiceInfo[0]);
				map.put("servicetype", (short)Integer.parseInt(preServiceInfo[6]));
				Long networkServiceId = HmBeParaUtil.getDefaultProfileId(NetworkService.class, map);
				if (networkServiceId == null) {
					NetworkService serviceDto = new NetworkService();
					serviceDto.setServiceName(preServiceInfo[0]);
					if(null != preServiceInfo[1] && !"".equals(preServiceInfo[1])){
						int protocol = Integer.parseInt(preServiceInfo[1]);
						serviceDto.setProtocolNumber(protocol);
						short protocolId = NetworkService.PROTOCOL_ID_CUSTOM;
						switch (protocol) {
						case 6:
							protocolId = NetworkService.PROTOCOL_ID_TCP;
							break;
						case 17:
							protocolId = NetworkService.PROTOCOL_ID_UDP;
							break;
						case 119:
							protocolId = NetworkService.PROTOCOL_ID_SVP;
							break;
						default:
							break;
						}
						serviceDto.setProtocolId(protocolId);
					}
					if(null != preServiceInfo[2] && !"".equals(preServiceInfo[2])){
						serviceDto.setPortNumber(Integer.parseInt(preServiceInfo[2]));				
					}
					serviceDto.setIdleTimeout(Integer.parseInt(preServiceInfo[3]));
					serviceDto.setDescription(preServiceInfo[4]);
					if(null != preServiceInfo[5] && !"".equals(preServiceInfo[5])){
						serviceDto.setAlgType((short) Integer.parseInt(preServiceInfo[5]));
					}else{
						serviceDto.setAlgType((short)0);
					}
					serviceDto.setServiceType((short)Integer.parseInt(preServiceInfo[6]));
					if(null != preServiceInfo[7] && !"".equals(preServiceInfo[7])){
						serviceDto.setAppId(Integer.parseInt(preServiceInfo[7]));
					}
				
					serviceDto.setDefaultFlag(true);
					serviceDto.setOwner(globalDomain);
					boolean boolCli = false;
					for (String cliDefault : BeParaModule.CLI_DEFAULT_NETWORK_SERVICE) {
						if (cliDefault.equalsIgnoreCase(preServiceInfo[0])) {
							boolCli = true;
							break;
						}
					}
					serviceDto.setCliDefaultFlag(boolCli);
					QueryUtil.createBo(serviceDto);
				}
			}
			
			long homeDomain_AppNum = QueryUtil.findRowCount(NetworkService.class, new FilterParams("serviceType = :s1 and owner.domainName = :s2",
					new Object[]{NetworkService.SERVICE_TYPE_L7, HmDomain.HOME_DOMAIN}));
			if(homeDomain_AppNum == 0){
				List<Application> list = getApplicationList();
				List<NetworkService> initList = initAllL7AppServiceByDomain(list,homeDomain);
				if(!initList.isEmpty() && null != initList){
					QueryUtil.bulkCreateBos(initList);
				}
			}
			
		} catch (Exception e) {
			setDebugMessage("insert default Network Service : ", e);
		}
	}

	private List<NetworkService> initAllL7AppServiceByDomain(List<Application> list, HmDomain hmDomain){
		List<NetworkService> initList = new ArrayList<NetworkService>();
		if(!list.isEmpty()){
			for(Application app : list){
				if(app.getAppCode() != 0){
						NetworkService serviceDto = new NetworkService();
						String appName = NetworkService.L7_SERVICE_NAME_PREFIX+app.getShortName();
						if(appName.length() > 32){
							appName = appName.substring(0, 32);
						}
						serviceDto.setServiceName(appName);
						serviceDto.setProtocolNumber(0);
						serviceDto.setPortNumber(0);				
						serviceDto.setIdleTimeout(300);
						serviceDto.setDescription(app.getAppName());
						serviceDto.setAlgType((short)0);
						serviceDto.setServiceType(NetworkService.SERVICE_TYPE_L7);
						serviceDto.setAppId(app.getAppCode());
						serviceDto.setDefaultFlag(false);
						serviceDto.setOwner(hmDomain);
						serviceDto.setCliDefaultFlag(false);
						initList.add(serviceDto);
				}
			}
		}
		return initList;
	}
	
	private void insertDefaultALGService() {
		try {
		//	AlgConfiguration algList = HmBeParaUtil.getDefaultProfile(AlgConfiguration.class, null);
			Long algId = HmBeParaUtil.getDefaultProfileId(AlgConfiguration.class, null);
		//	if (null == algList) {
			if (algId == null) {
				AlgConfiguration dto_Alg = new AlgConfiguration();
				dto_Alg.setConfigName(BeParaModule.DEFAULT_SERVICE_ALG_NAME);
				dto_Alg.setDescription("Default ALG service");
				dto_Alg.setDefaultFlag(true);
				dto_Alg.setOwner(globalDomain);
				Map<String, AlgConfigurationInfo> items = new LinkedHashMap<>();
				for (GatewayType gatewayType : AlgConfigurationInfo.GatewayType.values()) {
					AlgConfigurationInfo oneItem = dto_Alg.getAlgInfo(gatewayType);
					if (oneItem == null) {
						oneItem = new AlgConfigurationInfo();
						if (GatewayType.SIP.equals(gatewayType)) {
							oneItem.setQosClass(EnumConstUtil.QOS_CLASS_VOICE);
							oneItem.setTimeout(60);
							oneItem.setDuration(720);
						}
					}
					oneItem.setGatewayType(gatewayType);
					items.put(oneItem.getkey(), oneItem);
				}
				dto_Alg.setItems(items);
				QueryUtil.createBo(dto_Alg);
			}
		} catch (Exception e) {
			setDebugMessage("insert default ALG Service : ", e);
		}
	}

	private void insertDefaultRADIUSUPRule() {
		try {
		//	RadiusUserProfileRule ruleList = HmBeParaUtil.getDefaultProfile(RadiusUserProfileRule.class, null);
			Long radiusUserProfileRuleId = HmBeParaUtil.getDefaultProfileId(RadiusUserProfileRule.class, null);
		//	if (null == ruleList) {
			if (radiusUserProfileRuleId == null) {
				RadiusUserProfileRule dtoRule = new RadiusUserProfileRule();
				dtoRule.setRadiusUserProfileRuleName(BeParaModule.DEFAULT_RADIUS_UP_RULE_NAME);
				dtoRule.setDescription("Default RADIUS user profile rule");
				dtoRule.setAllUserProfilesPermitted(true);
				dtoRule.setDenyAction(RadiusUserProfileRule.DENY_ACTION_DISCONNECT);
				dtoRule.setActionTime(RadiusUserProfileRule.ACTION_TIME_DEFAULT);
				dtoRule.setStrict(false);
				dtoRule.setDefaultFlag(true);
				dtoRule.setOwner(globalDomain);
				QueryUtil.createBo(dtoRule);
			}
		} catch (Exception e) {
			setDebugMessage("insert default RADIUS User Profile Rule : ", e);
		}
	}

	private void insertDefaultRadioProfile() {
		try {
			List<RadioProfile> allRadios = getDefaultRadioProfile(RadioProfile.RADIO_PROFILE_MODE_A);
			if (!allRadios.isEmpty()) {
				QueryUtil.bulkCreateBos(allRadios);
			}
			allRadios = getDefaultRadioProfile(RadioProfile.RADIO_PROFILE_MODE_BG);
			if (!allRadios.isEmpty()) {
				QueryUtil.bulkCreateBos(allRadios);
			}
			allRadios = getDefaultRadioProfile(RadioProfile.RADIO_PROFILE_MODE_NA);
			if (!allRadios.isEmpty()) {
				QueryUtil.bulkCreateBos(allRadios);
			}
			allRadios = getDefaultRadioProfile(RadioProfile.RADIO_PROFILE_MODE_NG);
			if (!allRadios.isEmpty()) {
				QueryUtil.bulkCreateBos(allRadios);
			}
			/**
			 * Millau_Merge_Start:engineer=zhoushaohua
			 */
			allRadios = getDefaultRadioProfile(RadioProfile.RADIO_PROFILE_MODE_AC);
			if (!allRadios.isEmpty()) {
				QueryUtil.bulkCreateBos(allRadios);
			}
			/**
			 * Millau_Merge_End:engineer=zhoushaohua
			 */
		} catch (Exception e) {
			setDebugMessage("insert default Radio Profile : ", e);
		}
	}

	private void insertDefaultIpPolicy() {
	//	IpPolicy policyList = HmBeParaUtil.getDefaultProfile(IpPolicy.class, null);
		Long ipPolicyId = HmBeParaUtil.getDefaultProfileId(IpPolicy.class, null);

	//	if (null == policyList) {
		if (ipPolicyId == null) {
			IpAddress ip10 = CreateObjectAuto.createNewIP("10.0.0.0", IpAddress.TYPE_IP_NETWORK, globalDomain, "For default IP policy",
				"255.0.0.0");
			IpAddress ip172 = CreateObjectAuto.createNewIP("172.16.0.0", IpAddress.TYPE_IP_NETWORK, globalDomain, "For default IP policy",
				"255.240.0.0");
			IpAddress ip192 = CreateObjectAuto.createNewIP("192.168.0.0", IpAddress.TYPE_IP_NETWORK, globalDomain, "For default IP policy",
				"255.255.0.0");

			IpPolicy ipPolicyFrom = new IpPolicy();
			ipPolicyFrom.setPolicyName(BeParaModule.DEFAULT_IPPOLICY_NAME);
			ipPolicyFrom.setDescription("Default IP policy that allows Internet access only");
			ipPolicyFrom.setDefaultFlag(true);
			ipPolicyFrom.setOwner(globalDomain);
			List<IpPolicyRule> lstRules = new ArrayList<>();
			NetworkService dhcp_server = null;
			NetworkService dns = null;
			List<NetworkService> netWorkList = QueryUtil.executeQuery(NetworkService.class,
					null, new FilterParams("(serviceName=:s1 or serviceName=:s2) and defaultFlag=:s3",
							new Object[]{"DHCP-Server", "DNS",true}));
			for(NetworkService oneObj:netWorkList){
				if (oneObj.getServiceName().equalsIgnoreCase("DHCP-Server")){
					dhcp_server = oneObj;
				} else {
					dns = oneObj;
				}
			}

			IpPolicyRule rule = new IpPolicyRule();
			rule.setRuleId((short)1);
			rule.setSourceIp(null);
			rule.setDesctinationIp(null);
			rule.setNetworkService(dhcp_server);
			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			lstRules.add(rule);

			rule = new IpPolicyRule();
			rule.setRuleId((short)2);
			rule.setSourceIp(null);
			rule.setDesctinationIp(null);
			rule.setNetworkService(dns);
			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			lstRules.add(rule);

			rule = new IpPolicyRule();
			rule.setRuleId((short)3);
			rule.setSourceIp(null);
			rule.setDesctinationIp(ip10);
			rule.setNetworkService(null);
			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_DENY);
			lstRules.add(rule);

			rule = new IpPolicyRule();
			rule.setRuleId((short)4);
			rule.setSourceIp(null);
			rule.setDesctinationIp(ip172);
			rule.setNetworkService(null);
			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_DENY);
			lstRules.add(rule);

			rule = new IpPolicyRule();
			rule.setRuleId((short)5);
			rule.setSourceIp(null);
			rule.setDesctinationIp(ip192);
			rule.setNetworkService(null);
			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_DENY);
			lstRules.add(rule);

			rule = new IpPolicyRule();
			rule.setRuleId((short)6);
			rule.setSourceIp(null);
			rule.setDesctinationIp(null);
			rule.setNetworkService(null);
			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			lstRules.add(rule);

			ipPolicyFrom.setRules(lstRules);

			try {
				QueryUtil.createBo(ipPolicyFrom);
			} catch (Exception e) {
				setDebugMessage("insert default Ip Policy : ", e);
			}
		}
	}

	private void insertDefaultQosRateControl() {
	//	QosRateControl rateList = HmBeParaUtil.getDefaultProfile(QosRateControl.class, null);
		Long qosRateControlId = HmBeParaUtil.getDefaultProfileId(QosRateControl.class, null);
	//	if (null == rateList) {
		if (qosRateControlId == null) {
			QosRateControl dto_Rate = new QosRateControl();
			dto_Rate.setQosName(BeParaModule.DEFAULT_QOS_RATE_CONTROL_NAME);
			dto_Rate.setOwner(globalDomain);
			dto_Rate.setDescription("Default user profile QoS setting");
			dto_Rate.setDefaultFlag(true);
			dto_Rate.setRateLimit(54000);
			dto_Rate.setRateLimit11n(1000000);
			dto_Rate.setRateLimit11ac(1000000);
			List<QosRateLimit> vector_QosC = new ArrayList<>();
			for (int j = 7; j > -1; j--) {
				QosRateLimit dto_QosC0 = new QosRateLimit();
				if (j == 6 || j == 7) {
					dto_QosC0.setSchedulingType(QosRateLimit.STRICT);
				} else {
					dto_QosC0.setSchedulingType(QosRateLimit.WEIGHTED_ROUND_ROBIN);
				}
				dto_QosC0.setSchedulingWeight(BeParaModule.DEFAULT_QOS_RATE_CONTROL_WEIGHT[j]);
				dto_QosC0.setQosClass((short) j);
				if (j == 6 || j == 7) {
					dto_QosC0.setPolicingRateLimit(512);
					dto_QosC0.setPolicing11nRateLimit(20000);
					dto_QosC0.setPolicing11acRateLimit(20000);
				} else if (j == 5) {
					dto_QosC0.setPolicingRateLimit(10000);
					dto_QosC0.setPolicing11nRateLimit(1000000);
					dto_QosC0.setPolicing11acRateLimit(1000000);
				} else {
					dto_QosC0.setPolicingRateLimit(54000);
					dto_QosC0.setPolicing11nRateLimit(1000000);
					dto_QosC0.setPolicing11acRateLimit(1000000);
				}
				vector_QosC.add(dto_QosC0);
			}
			dto_Rate.setQosRateLimit(vector_QosC);
			try {
				QueryUtil.createBo(dto_Rate);
			} catch (Exception e) {
				setDebugMessage("insert default QoS Rate Control : ", e);
			}
		}
	}

	private void insertDefaultUserProfile() {
	//	UserProfile userList = HmBeParaUtil.getDefaultProfile(UserProfile.class, null);
		Long userProfileId = HmBeParaUtil.getDefaultProfileId(UserProfile.class, null);
	//	if (null == userList) {
		if (userProfileId == null) {
			UserProfile dto_User = new UserProfile();
			dto_User.setUserProfileName(BeParaModule.DEFAULT_USER_PROFILE_NAME);
			dto_User.setOwner(globalDomain);
			dto_User.setDescription("Default user profile");
			dto_User.setAttributeValue((short) 0);
			dto_User.setDefaultFlag(true);
			dto_User.setVlan(HmBeParaUtil.getDefaultProfile(Vlan.class, null));
			dto_User.setQosRateControl(HmBeParaUtil.getDefaultProfile(QosRateControl.class,
					null));
			try {
				QueryUtil.createBo(dto_User);
			} catch (Exception e) {
				setDebugMessage("insert default User Profile : ", e);
			}
		}
	}

	private void insertDefaultHiveId() {
	//	HiveProfile hiveList = HmBeParaUtil.getDefaultProfile(HiveProfile.class, null);
		Long hiveProfileId = HmBeParaUtil.getDefaultProfileId(HiveProfile.class, null);
	//	if (null == hiveList) {
		if (hiveProfileId == null) {
			HiveProfile dto_Hiveid = new HiveProfile();
			dto_Hiveid.setHiveName(BeParaModule.DEFAULT_HIVEID_PROFILE_NAME);
			dto_Hiveid.setOwner(globalDomain);
			dto_Hiveid.setDescription("Default " + NmsUtil.getOEMCustomer().getWirelessUnitName() + " profile");
			dto_Hiveid.setRtsThreshold(2346);
			dto_Hiveid.setFragThreshold(2346);
			dto_Hiveid.setEnabledPassword(false);
			dto_Hiveid.setL3TrafficPort(3000);
			dto_Hiveid.setEnabledThreshold(false);
//			dto_Hiveid.setConnectionThreshold(HiveProfile.CONNECTION_THRESHOLD_LOW);
			dto_Hiveid.setPollingInterval(1);
			dto_Hiveid.setEnabledL3Setting(false);
			dto_Hiveid.setKeepAliveInterval(10);
			dto_Hiveid.setKeepAliveAgeout(5);
//			dto_Hiveid.setUpdateInterval(60);
//			dto_Hiveid.setUpdateAgeout(60);
			dto_Hiveid.setDefaultFlag(true);
			Map<String, Object> map = new HashMap<>();
			map.put("dosType", DosType.MAC);
			dto_Hiveid.setHiveDos(HmBeParaUtil.getDefaultProfile(DosPrevention.class, map));
			map.remove("dosType");
			map.put("dosType", DosType.MAC_STATION);
			dto_Hiveid.setStationDos(HmBeParaUtil.getDefaultProfile(DosPrevention.class, map));
			try {
				QueryUtil.createBo(dto_Hiveid);
			} catch (Exception e) {
				setDebugMessage("insert default Hive Profile : ", e);
			}
		}
	}

	private void insertDefaultSSIDProfile() {
		SsidProfile dto_Ssid;
		List<SsidProfile> allSsids = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		for (String name : BeParaModule.SSID_PROFILE_NAMES) {
			map.clear();
			map.put("ssidName", name);
		//	SsidProfile ssidList = HmBeParaUtil.getDefaultProfile(SsidProfile.class, map);
			Long ssidProfileId = HmBeParaUtil.getDefaultProfileId(SsidProfile.class, map);
		//	if (null == ssidList) {
			if (ssidProfileId == null) {
				dto_Ssid = new SsidProfile();
				dto_Ssid.setSsidName(name);
				dto_Ssid.setSsid(name);
				dto_Ssid.setOwner(globalDomain);
				if (BeParaModule.DEFAULT_SSID_PROFILE_NAME.equals(name)) {
					dto_Ssid.setComment("Default SSID profile");
				} else {
					dto_Ssid.setComment("SSID profile template");
				}
				dto_Ssid.setDefaultFlag(true);
				dto_Ssid.setHide(false);
				dto_Ssid.setBroadcase(false);
				if (BeParaModule.SSID_PROFILE_TEMPLATE_SYMBOL_SCANNER.equals(name)) {
					dto_Ssid.setDtimSetting(10);
				} else if (BeParaModule.SSID_PROFILE_TEMPLATE_BLACK_BERRY.equals(name)) {
					dto_Ssid.setDtimSetting(5);
				} else {
					dto_Ssid.setDtimSetting(1);
				}
				dto_Ssid.setRtsThreshold(2346);
				dto_Ssid.setFragThreshold(2346);
				dto_Ssid.setEncryption(SsidProfile.KEY_MGMT_OPEN);
				dto_Ssid.setMgmtKey(SsidProfile.KEY_MGMT_OPEN);
				dto_Ssid.setAuthentication(SsidProfile.KEY_MGMT_OPEN);
				dto_Ssid.setPreauthenticationEnabled(false);
				dto_Ssid.setMacAuthEnabled(false);
				dto_Ssid.setEnabledUnscheduled(false);
				dto_Ssid.setEnabledwmm(!(BeParaModule.SSID_PROFILE_TEMPLATE_BLACK_BERRY.equals(name) ||
					BeParaModule.SSID_PROFILE_TEMPLATE_SPECTRA_LINK.equals(name)));
				dto_Ssid.setMaxClient(100);
				dto_Ssid.setUpdateInterval(60);
				dto_Ssid.setAgeOut(60);
				dto_Ssid.setAuthentication(0);
				map.clear();
				map.put("dosType", DosType.MAC);
				dto_Ssid.setSsidDos(HmBeParaUtil.getDefaultProfile(DosPrevention.class, map));
				map.remove("dosType");
				map.put("dosType", DosType.MAC_STATION);
				dto_Ssid.setStationDos(HmBeParaUtil.getDefaultProfile(DosPrevention.class, map));
				map.remove("dosType");
				map.put("dosType", DosType.IP);
				dto_Ssid.setIpDos(HmBeParaUtil.getDefaultProfile(DosPrevention.class, map));
				ServiceFilter serviceFilter = HmBeParaUtil.getDefaultProfile(ServiceFilter.class,
						null);
				dto_Ssid.setServiceFilter(serviceFilter);
				UserProfile userProfile = HmBeParaUtil.getDefaultProfile(UserProfile.class, null);
				dto_Ssid.setUserProfileDefault(userProfile);
				SsidSecurity security = new SsidSecurity();
				dto_Ssid.setSsidSecurity(security);
				dto_Ssid.setEnableGRateSet(true);
				dto_Ssid.setGRateSets(getSsidGrateSettings(name));
				dto_Ssid.setEnableARateSet(true);
				dto_Ssid.setARateSets(getSsidArateSettings(name));
				dto_Ssid.setEnableNRateSet(true);
				dto_Ssid.setNRateSets(getSsidNrateSettings(name));
				dto_Ssid.setEnableACRateSet(true);
				dto_Ssid.setAcRateSets(getSsidAcRateSettings());
				allSsids.add(dto_Ssid);
			}
		}
		try {
			if (!allSsids.isEmpty()) {
				QueryUtil.bulkCreateBos(allSsids);
			}
		} catch (Exception e) {
			setDebugMessage("insert default SSID Profile : ", e);
		}
	}

//	private void insertDefaultWipsPolicy() {
//		Long idsId = HmBeParaUtil.getDefaultProfileId(IdsPolicy.class, null);
//		if (idsId == null) {
//			IdsPolicy dto_ids = new IdsPolicy();
//			dto_ids.setPolicyName(BeParaModule.DEFAULT_WIPS_POLICY_NAME);
//			dto_ids.setOwner(globalDomain);
//			dto_ids.setDescription("default rogue detection wips policy");
//			dto_ids.setDefaultFlag(true);
//
//			// mac oui
//			Set<MacOrOui> def_macOui = new HashSet<MacOrOui>();
//			Map<String, Object> parameter = new HashMap<String, Object>();
//			parameter.put("macOrOuiName", BeParaModule.DEFAULT_MAC_OUI_NAME);
//			def_macOui.add(HmBeParaUtil.getDefaultProfile(MacOrOui.class, parameter));
//			dto_ids.setMacOrOuis(def_macOui);
//
//			// vlan
//			Set<Vlan> def_vlan = new HashSet<Vlan>();
//			def_vlan.add(HmBeParaUtil.getDefaultProfile(Vlan.class, null));
//			dto_ids.setVlans(def_vlan);
//			try {
//				QueryUtil.createBo(dto_ids);
//			} catch (Exception e) {
//				setDebugMessage("insert default WIPS Policy : ", e);
//			}
//		}
//	}

	public void insertDefaultReport() {
		Map<String, Object> map = new HashMap<>();
		map.put("reportType", Navigation.L2_FEATURE_CHANNELPOWERNOISE);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_CHANNELPOWERNOISE,"def-channel-power-noise_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_RADIOAIRTIME);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_RADIOAIRTIME, "def-AP-airtime-usage_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_RADIOTRAFFICMETRICS);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_RADIOTRAFFICMETRICS,"def-AP-traffic-metric_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_RADIOTROUBLESHOOTING);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_RADIOTROUBLESHOOTING,"def-AP-troubleshoot_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_RADIOINTERFERENCE);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_RADIOINTERFERENCE,"def-interference_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_SSIDAIRTIME);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_SSIDAIRTIME,"def-ssid-airtime-usage_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_SSIDTRAFFICMETRICS);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_SSIDTRAFFICMETRICS,"def-ssid-traffic-metric_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_SSIDTROUBLESHOOTING);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_SSIDTROUBLESHOOTING,"def-ssid-troubleshoot_report");

//		map.clear();
//		map.put("reportType", Navigation.L2_FEATURE_MOSTCLIENTSAPS);
//		insertDeferentReportType(map,
//				Navigation.L2_FEATURE_MOSTCLIENTSAPS,"def");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_CLIENTSESSION);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_CLIENTSESSION,"def-client-session_report");

//		map.clear();
//		map.put("reportType", Navigation.L2_FEATURE_CLIENTCOUNT);
//		insertDeferentReportType(map,
//				Navigation.L2_FEATURE_CLIENTCOUNT,"def-");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_CLIENTAIRTIME);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_CLIENTAIRTIME, "def-client-airtime-usage_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_UNIQUECLIENTCOUNT);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_UNIQUECLIENTCOUNT,"def-unique-client-count_report");

//		map.clear();
//		map.put("reportType", Navigation.L2_FEATURE_CLIENTAUTH);
//		insertDeferentReportType(map,
//				Navigation.L2_FEATURE_CLIENTAUTH,"def");

//		map.clear();
//		map.put("reportType", Navigation.L2_FEATURE_CLIENTVENDOR);
//		insertDeferentReportType(map,Navigation.L2_FEATURE_CLIENTVENDOR);

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_SECURITYROGUEAPS);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_SECURITYROGUEAPS,"def-rogue-aps_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_SECURITYROGUECLIENTS);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_SECURITYROGUECLIENTS,"def-rogue-clients_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_SECURITYCOMPLIANCE);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_SECURITYCOMPLIANCE,"def-compliance_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_SECURITY_NONHIVEAP);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_SECURITY_NONHIVEAP,"def-noncompliance-device_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_SECURITY_NONCLIENT);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_SECURITY_NONCLIENT,"def-noncompliance-client_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_MESHNEIGHBORS);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_MESHNEIGHBORS,"def-mesh-neighbors_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_INVENTORY);
		insertDeferentReportType(map,Navigation.L2_FEATURE_INVENTORY, "def-inventory_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_HIVEAPSLA);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_HIVEAPSLA,"def-AP-SLA_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_CLIENTSLA);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_CLIENTSLA,"def-Client-SLA_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_MAXCLIENTREPORT);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_MAXCLIENTREPORT,"def-Max-Client_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_HIVEAPCONNECTION);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_HIVEAPCONNECTION,"def-AP-Connection_report");

		// vpn report
		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_REPORT_VPNAVAILABILITY);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_REPORT_VPNAVAILABILITY,"def-vpn-availability_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_REPORT_VPNTHROUGHPUT);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_REPORT_VPNTHROUGHPUT,"def-vpn-throughput_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_REPORT_VPNLATENCY);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_REPORT_VPNLATENCY,"def-vpn-latency_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_REPORT_WANAVAILABILITY);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_REPORT_WANAVAILABILITY,"def-wan-availability_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_REPORT_WANTHROUGHPUT);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_REPORT_WANTHROUGHPUT,"def-wan-throughput_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_REPORT_GWVPNAVAILABILITY);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_REPORT_GWVPNAVAILABILITY,"def-gw-vpn-availability_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_REPORT_GWWANAVAILABILITY);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_REPORT_GWWANAVAILABILITY,"def-gw-wan-availability_report");

		map.clear();
		map.put("reportType", Navigation.L2_FEATURE_REPORT_GWWANTHROUGHPUT);
		insertDeferentReportType(map,
				Navigation.L2_FEATURE_REPORT_GWWANTHROUGHPUT,"def-gw-wan-throughput_report");

		insertDefaultCustomReportField();
	}
	
	public void insertDefaultNetworkUsageReport(){
		Map<String, Object> map = new HashMap<>();
		map.put("name", BeParaModule.DEFAULT_NETWORK_REPORT);
		Long reportId = HmBeParaUtil.getDefaultProfileId(AhNewReport.class, map);
		if (reportId == null) {
			AhNewReport dto_report = new AhNewReport();
			dto_report.setName(BeParaModule.DEFAULT_NETWORK_REPORT);
			dto_report.setOwner(globalDomain);
			dto_report.setDefaultFlag(true);
			dto_report.setReportType(1);
			dto_report.setExcuteType(AhNewReport.NEW_REPORT_EXCUTETYPE_IMME);
			dto_report.setReportPeriod(AhNewReport.NEW_REPORT_PERIOD_LASTCLOCKHOUR);
			dto_report.setSsidName("All");
			dto_report.setEmailAddress("");
			dto_report.setDescription("Clone this template to create your custom report");
			
			try {
				QueryUtil.createBo(dto_report);
			} catch (Exception e) {
				setDebugMessage("insert network default Report : ", e);
			}
		}
		
		map.clear();
		map.put("name", BeParaModule.DEFAULT_NETWORK_REPORT_SAMPLE);
		reportId = HmBeParaUtil.getDefaultProfileId(AhNewReport.class, map);
		if (reportId == null) {
			AhNewReport dto_report = new AhNewReport();
			dto_report.setName(BeParaModule.DEFAULT_NETWORK_REPORT_SAMPLE);
			dto_report.setOwner(globalDomain);
			dto_report.setDefaultFlag(true);
			dto_report.setReportType(1);
			dto_report.setExcuteType(AhNewReport.NEW_REPORT_EXCUTETYPE_IMME);
			dto_report.setReportPeriod(AhNewReport.NEW_REPORT_PERIOD_LASTCLOCKHOUR);
			dto_report.setSsidName("All");
			dto_report.setEmailAddress("");
			dto_report.setDescription("Report with sample data");
			
			try {
				QueryUtil.createBo(dto_report);
			} catch (Exception e) {
				setDebugMessage("insert network demo Report : ", e);
			}
		}
	}

	public void insertDefaultCustomReportField(){
		try {
			long rowCount = QueryUtil.findRowCount(AhCustomReportField.class, null);

			if (rowCount>0){
				return;
			}

			List<AhCustomReportField> reportFields = new ArrayList<>();

			SAXReader reader = new SAXReader();
			String docName = AhDirTools.getHmRoot() +
				"resources" + File.separator +
				"customReport" + File.separator + "custom_report_table.xml";
			Document doc = reader.read(new File(docName));
			Element root = doc.getRootElement();
			List<?> rootLst = root.elements();

			for (Object obj : rootLst) {
				List<?> rowlst = ((Element) obj).elements();
				AhCustomReportField reportField = new AhCustomReportField();
				for (int j = 0; j < rowlst.size(); j++) {
					Element elm = (Element) rowlst.get(j);
//					String name = elm.attributeValue("name");
					String value = elm.attributeValue("value");
					if (j == 0) {
						reportField.setId(Long.parseLong(value));
					} else if (j == 1) {
						reportField.setType(Integer.parseInt(value));
					} else if (j == 2) {
						reportField.setDetailType(Integer.parseInt(value));
					} else if (j == 3) {
						reportField.setTableName(value);
					} else if (j == 4) {
						reportField.setTableField(value);
					} else if (j == 5) {
						reportField.setFieldString(value);
					} else if (j == 6) {
						reportField.setStrUnit(value);
					} else if (j == 7) {
						reportField.setDescription(value);
					}
				}
				reportFields.add(reportField);
			}

			root.clearContent();
			doc.clearContent();

			QueryUtil.bulkCreateBos(reportFields);
		} catch (Exception e) {
			setDebugMessage("insert default custom report field: ", e);
		}
	}

	public void insertDeferentReportType(Map<String, Object> map,String reportType, String reportName){
	//	AhReport reportList = HmBeParaUtil.getDefaultProfile(AhReport.class, map);
		Long reportId = HmBeParaUtil.getDefaultProfileId(AhReport.class, map);
	//	if(null == reportList){
		if (reportId == null) {
			AhReport dto_report = new AhReport();
			dto_report.setName(reportName);
			dto_report.setOwner(globalDomain);
			dto_report.setDefaultFlag(true);
			dto_report.setReportType(reportType);
			dto_report.setApName("");
			dto_report.setNewOldFlg(AhReport.REPORT_NEWOLDTYEP_OLD);
			dto_report.setSsidName("");
			dto_report.setEmailAddress("");
			dto_report.setAuthHostName("");
			dto_report.setAuthIp("");
			dto_report.setAuthMac("");
			dto_report.setAuthUserName("");
			try {
				QueryUtil.createBo(dto_report);
			} catch (Exception e) {
				setDebugMessage("insert default Report : ", e);
			}
		}
	}

	public void insertDefaultConfigTemplate() {
	//	ConfigTemplate templeList = HmBeParaUtil.getDefaultProfile(ConfigTemplate.class, null);
		Long configTemplateId = HmBeParaUtil.getDefaultProfileId(ConfigTemplate.class, null);
	//	if (null == templeList) {
		if (configTemplateId == null) {
			ConfigTemplate dto_Device = new ConfigTemplate(ConfigTemplateType.WIRELESS);
			dto_Device.setConfigName(BeParaModule.DEFAULT_DEVICE_GROUP_NAME);
			dto_Device.setOwner(globalDomain);
			dto_Device.setDescription("Default network policy");
			dto_Device.setDefaultFlag(true);
			dto_Device.setHiveProfile(HmBeParaUtil.getDefaultProfile(HiveProfile.class, null));
			dto_Device.setMgmtServiceSnmp(HmBeParaUtil.getDefaultProfile(
					MgmtServiceSnmp.class, null));
			Vlan defaultVlan = HmBeParaUtil.getDefaultProfile(Vlan.class, null);
			dto_Device.setVlan(defaultVlan);
			dto_Device.setVlanNative(defaultVlan);
			dto_Device.setAlgConfiguration(HmBeParaUtil.getDefaultProfile(
					AlgConfiguration.class, null));
			dto_Device.setEnableAirTime(false);
//			Map<String, ConfigTemplateSsidUserProfile> ssidUserProfiles = new HashMap<String, ConfigTemplateSsidUserProfile>();
//			ConfigTemplateSsidUserProfile configSsid = new ConfigTemplateSsidUserProfile();
			SsidProfile ssidSingle = HmBeParaUtil.getDefaultProfile(SsidProfile.class, null);
//			configSsid.setSsidProfile(ssidSingle);
//			configSsid.setUpType(ConfigTemplateSsidUserProfile.USERPORFILE_TYPE_AUTHENTICATED);
//			UserProfile userSingle = getDefaultProfile(UserProfile.class, null);
//			configSsid.setUserProfile(userSingle);
//			ssidUserProfiles.put(configSsid.getKey(), configSsid);
//			dto_Device.setSsidUserProfiles(ssidUserProfiles);
			Map<Long, ConfigTemplateSsid> ssidInterfaces;
			ServiceFilter serviceFilter = HmBeParaUtil.getDefaultProfile(ServiceFilter.class,
					null);
			dto_Device.setDeviceServiceFilter(serviceFilter);
			dto_Device.setEth0ServiceFilter(serviceFilter);
			dto_Device.setEth1ServiceFilter(serviceFilter);
			dto_Device.setRed0ServiceFilter(serviceFilter);
			dto_Device.setAgg0ServiceFilter(serviceFilter);
			dto_Device.setWireServiceFilter(serviceFilter);
			dto_Device.setEth0BackServiceFilter(serviceFilter);
			dto_Device.setEth1BackServiceFilter(serviceFilter);
			dto_Device.setRed0BackServiceFilter(serviceFilter);
			dto_Device.setAgg0BackServiceFilter(serviceFilter);
			/*
			ConfigTemplateSsid eth0 = new ConfigTemplateSsid();
			eth0.setInterfaceName("eth0");
			ssidInterfaces.put((long) -1, eth0);
			// add for dual port
			ConfigTemplateSsid eth1 = new ConfigTemplateSsid();
			eth1.setInterfaceName("eth1");
			ssidInterfaces.put((long) -2, eth1);
			ConfigTemplateSsid red0 = new ConfigTemplateSsid();
			red0.setInterfaceName("red0");
			ssidInterfaces.put((long) -3, red0);
			ConfigTemplateSsid agg0 = new ConfigTemplateSsid();
			agg0.setInterfaceName("agg0");
			ssidInterfaces.put((long) -4, agg0);
			ConfigTemplateSsid eth2 = new ConfigTemplateSsid();
			eth2.setInterfaceName("eth2");
			ssidInterfaces.put((long) -5, eth2);
			ConfigTemplateSsid eth3 = new ConfigTemplateSsid();
			eth3.setInterfaceName("eth3");
			ssidInterfaces.put((long) -6, eth3);
			ConfigTemplateSsid eth4 = new ConfigTemplateSsid();
			eth4.setInterfaceName("eth4");
			ssidInterfaces.put((long) -7, eth4);
			*/
			ssidInterfaces = BoGenerationUtil.genDefaultSsidInterfaces();

			// add end
			ConfigTemplateSsid ssid = new ConfigTemplateSsid();
			ssid.setInterfaceName(ssidSingle.getSsidName());
			ssid.setSsidProfile(ssidSingle);
//			ssid.setServiceFilter(serviceFilter);
//			RadiusUserProfileRule radiusRule = getDefaultProfile(
//					RadiusUserProfileRule.class, null);
//			ssid.setUserProfileRule(radiusRule);
//			ssid.setRadioMode(ConfigTemplateSsid.RADIOMODE_BOTH);
			ssidInterfaces.put(ssidSingle.getId(), ssid);
			dto_Device.setSsidInterfaces(ssidInterfaces);

			ApplicationProfile profile = QueryUtil.findBoByAttribute(ApplicationProfile.class, "defaultFlag", true);
			if (profile != null) {
				dto_Device.setAppProfile(profile);
			}
			
//			Map<String, ConfigTemplateQos> qosPolicies = new HashMap<String, ConfigTemplateQos>();
//			ConfigTemplateQos qosPolicy;
//			for (int i = 0; i < 2; i++) {
//				qosPolicy = new ConfigTemplateQos();
//				qosPolicy.setUserProfile(userSingle);
//				qosPolicy.setPolicingRate(54000);
//				qosPolicy.setPolicingRate11n(1000000);
//				qosPolicy.setSchedulingWeight(10);
//				qosPolicy.setWeightPercent(100);
//				switch (i) {
//				case 0:
//					qosPolicy.setRadioMode(SsidProfile.RADIOMODE_A);
//					break;
//				case 1:
//					qosPolicy.setRadioMode(SsidProfile.RADIOMODE_BG);
//					break;
//				default:
//					break;
//				}
//				qosPolicies.put(qosPolicy.getKey(), qosPolicy);
//			}
//			dto_Device.setQosPolicies(qosPolicies);
			try {
				QueryUtil.createBo(dto_Device);
			} catch (Exception e) {
				setDebugMessage("insert default network Policy : ", e);
			}
		}
	}

	private void insertDefaultGMLPrintTemplate() {
	//	PrintTemplate bo = HmBeParaUtil.getDefaultProfile(PrintTemplate.class, null);
		Long printTemplateId = HmBeParaUtil.getDefaultProfileId(PrintTemplate.class, null);
	//	if (null == bo) {
		if (printTemplateId == null) {
			List<PrintTemplate> boList = new ArrayList<>();

			PrintTemplate template1 = new PrintTemplate();

			template1.setName("One-account-per-page_label");
			template1.setOwner(globalDomain);
			template1.setAsDefault(true);
			template1.setEnabled(true);
			template1.setDefaultFlag(true);

			boList.add(template1);

			PrintTemplate template2 = new PrintTemplate();

			template2.setName("One-account-per-page_regular");
			template2.setOwner(globalDomain);
			template2.setAsDefault(false);
			template2.setEnabled(true);
			template2.setDefaultFlag(true);

			StringBuilder headerHTML = new StringBuilder();

			headerHTML.append("<table width=\"500px\"><tr><td colspan=\"2\">");
			headerHTML.append("Your guest account has been created and is now ready to use!");
			headerHTML.append("</td></tr><tr><td height=\"10px\"></td></tr><tr><td width=\"20\" valign=\"top\">");
			headerHTML.append("1.");
			headerHTML.append("</td><td>");
			headerHTML.append("Make sure your network adapter is set to \"DHCP - Obtain an IP address automatically\".");
			headerHTML.append("</td></tr><tr><td width=\"20px\" valign=\"top\">");
			headerHTML.append("2.");
			headerHTML.append("</td><td>");
			headerHTML.append("Open the wireless network client on your computer and select the wireless network name, or SSID, listed below.");
			headerHTML.append("</td></tr><tr><td width=\"20px\" valign=\"top\">");
			headerHTML.append("3.");
			headerHTML.append("</td><td>");
			headerHTML.append("Open your web browser.");
			headerHTML.append("</td></tr><tr><td width=\"20px\" valign=\"top\">");
			headerHTML.append("4.");
			headerHTML.append("</td><td>");
			headerHTML.append("Enter your user name and password in the spaces provided.");
			headerHTML.append("</td></tr></table>");
			template2.setHeaderHTML(headerHTML.toString());

			boList.add(template2);

			try {
				QueryUtil.bulkCreateBos(boList);
			} catch (Exception e) {
				setDebugMessage("insert default GML print template: ", e);
			}
		}
	}
	
	private void insertDefaultPseProfile(){
		Long pseId = HmBeParaUtil.getDefaultProfileId(PseProfile.class, null);
		if (pseId == null) {
			PseProfile pseProfile = new PseProfile();
			pseProfile.setDefaultFlag(true);
			pseProfile.setName(BeParaModule.DEFAULT_PSE_PROFILE_NAME);
			pseProfile.setOwner(globalDomain);
			pseProfile.setDescription("");
			pseProfile.setPowerMode(AhInterface.PSE_PDTYPE_8023AT);
			pseProfile.setThresholdPower(PseProfile.THRESHOLD_POWER_AT);
			pseProfile.setPriority(PseProfile.PRIORITY_LOW);
			try {
				QueryUtil.createBo(pseProfile);
			} catch (Exception e) {
				setDebugMessage("insert default pse profile : ", e);
			}
		}
	}

	/**
	 * Get the MAC Dos, Station or IP Dos map values.
	 *
	 * @param dosType -
	 *            0 : MAC Dos; 1 : MAC Station; 2 : IP Dos
	 * @return Map<String, DosParams>
	 */
	private Map<String, DosParams> getDosMap(int dosType) {
		Map<String, DosParams> dosParamsMap = new HashMap<>();
		DosParams dto_Dos;
		switch (dosType) {
		case 0:
			for (int i = 0; i < 8; i++) {
				dto_Dos = new DosParams();
				dto_Dos.setFrameType(DosParams.FrameType.values()[i]);
				if (i == 0) {
					dto_Dos.setAlarmThreshold(12000);
				} else if (i == 1) {
					dto_Dos.setAlarmThreshold(24000);
				} else if (i == 3) {
					dto_Dos.setAlarmThreshold(2400);
				} else if (i == 4 || i == 6) {
					dto_Dos.setAlarmThreshold(1200);
				} else {
					dto_Dos.setAlarmThreshold(6000);
				}
				dto_Dos.setAlarmInterval(60);
				dto_Dos.setEnabled(true);
				dosParamsMap.put(dto_Dos.getkey(), dto_Dos);
			}
			break;
		case 1:
			for (int i = 0; i < 8; i++) {
				dto_Dos = new DosParams();
				dto_Dos.setFrameType(DosParams.FrameType.values()[i]);
				if (i == 0) {
					dto_Dos.setAlarmThreshold(1200);
				} else if (i == 1) {
					dto_Dos.setAlarmThreshold(2400);
				} else if (i == 3) {
					dto_Dos.setAlarmThreshold(240);
				} else if (i == 4 || i == 6) {
					dto_Dos.setAlarmThreshold(120);
				} else {
					dto_Dos.setAlarmThreshold(600);
				}
				if (i == 2 || i == 7 || i == 5)
					dto_Dos.setDosActionTime(60);
				dto_Dos.setAlarmInterval(60);
				dto_Dos.setEnabled(true);
				dosParamsMap.put(dto_Dos.getkey(), dto_Dos);
			}
			break;
		case 2:
			for (int i = 0; i < 8; i++) {
				dto_Dos = new DosParams();
				dto_Dos.setScreeningType(DosParams.ScreeningType.values()[i]);
				switch (i) {
				case 0:
					dto_Dos.setAlarmThreshold(20);
					break;
				case 1:
					dto_Dos.setAlarmThreshold(50);
					break;
				case 2:
					dto_Dos.setAlarmThreshold(1000);
					break;
				case 3:
					dto_Dos.setAlarmThreshold(100);
					break;
				case 4:
					dto_Dos.setAlarmThreshold(100);
					break;
				case 5:
					dto_Dos.setAlarmThreshold(100);
					break;
				case 6:
					dto_Dos.setAlarmThreshold(3);
					break;
				case 7:
					dto_Dos.setAlarmThreshold(5);
					break;
				default:
					break;
				}
				dto_Dos.setDosAction(DosParams.DosAction.values()[0]);
				dto_Dos.setDosActionTime(10);
				dto_Dos.setEnabled(false);
				dosParamsMap.put(dto_Dos.getkey(), dto_Dos);
			}
			break;
		default:
			break;
		}
		return dosParamsMap;
	}

	/**
	 * Get the default radio profile bo by mode.
	 *
	 * @param arg_RadioMode -
	 * @return List<RadioProfile>
	 */
	private List<RadioProfile> getDefaultRadioProfile(short arg_RadioMode) {
		String[] radioNames = BeParaModule.RADIO_PROFILE_NAME_BG;
		String description = "";
		String defaultName = "";
		String hignName = "";
		switch (arg_RadioMode) {
			case RadioProfile.RADIO_PROFILE_MODE_BG:
				radioNames = BeParaModule.RADIO_PROFILE_NAME_BG;
				defaultName = BeParaModule.DEFAULT_RADIO_PROFILE_NAME_BG;
				hignName = BeParaModule.RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_BG;
				description = "Default 11b/g mode radio profile";
				break;
			case RadioProfile.RADIO_PROFILE_MODE_A:
				radioNames = BeParaModule.RADIO_PROFILE_NAME_A;
				defaultName = BeParaModule.DEFAULT_RADIO_PROFILE_NAME_A;
				description = "Default 11a mode radio profile";
				break;
			case RadioProfile.RADIO_PROFILE_MODE_NG:
				radioNames = BeParaModule.RADIO_PROFILE_NAME_NG;
				defaultName = BeParaModule.DEFAULT_RADIO_PROFILE_NAME_NG;
				hignName = BeParaModule.RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_NG;
				description = "Default 11ng mode radio profile";
				break;
			case RadioProfile.RADIO_PROFILE_MODE_NA:
				radioNames = BeParaModule.RADIO_PROFILE_NAME_NA;
				defaultName = BeParaModule.DEFAULT_RADIO_PROFILE_NAME_NA;
				description = "Default 11na mode radio profile";
				break;
			/**
			 * Millau_Merge_Start:engineer=zhoushaohua
			 */	
			case RadioProfile.RADIO_PROFILE_MODE_AC:
				radioNames = BeParaModule.RADIO_PROFILE_NAME_AC;
				defaultName = BeParaModule.DEFAULT_RADIO_PROFILE_NAME_AC;
				description = "Default 11ac mode radio profile";
				break;
			/**
			 * Millau_Merge_End:engineer=zhoushaohua
			 */
			default:
				break;
		}
		Map<String, Object> map = new HashMap<>();
		List<RadioProfile> allRadios = new ArrayList<>();
		RadioProfile dto_Radio;
		for (String name : radioNames) {
			map.put("radioMode", arg_RadioMode);
			map.put("radioName", name);
		//	RadioProfile radioList = HmBeParaUtil.getDefaultProfile(RadioProfile.class, map);
			Long radioProfileId = HmBeParaUtil.getDefaultProfileId(RadioProfile.class, map);
		//	if (null == radioList) {
			if (radioProfileId == null) {
				dto_Radio = new RadioProfile();
				dto_Radio.setRadioName(name);
				dto_Radio.setOwner(globalDomain);
				dto_Radio.setRadioMode(arg_RadioMode);
				dto_Radio.setDefaultFlag(true);
				if (defaultName.equals(name)) {
					dto_Radio.setDescription(description);
					dto_Radio.setCliDefaultFlag(true);
				} else {
					dto_Radio.setDescription("radio profile template");
					if (RadioProfile.RADIO_PROFILE_MODE_BG == arg_RadioMode || RadioProfile.RADIO_PROFILE_MODE_NG == arg_RadioMode) {
						if (!hignName.equals(name)) {
							dto_Radio.setShortPreamble(RadioProfile.RADIO_PROFILE_PREAMBLE_LONG);
						}
					}
				}
				// High-Capacity-40MHz-11na-Profile
				if (BeParaModule.RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_NA.equals(name)) {
					dto_Radio.setChannelWidth(RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A);
					dto_Radio.setSlaThoughput(SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
					dto_Radio.setGuardInterval(true);

				// High-Capacity-20MHz-11ng-Profile
				} else if (BeParaModule.RADIO_PROFILE_TEMPLATE_HIGH_CAPACITY_NG.equals(name)) {
					dto_Radio.setSlaThoughput(SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
				/**
				 * Millau_Merge_Start:engineer=zhoushaohua
				 */	
			    // radio_ac0
				} else if (BeParaModule.DEFAULT_RADIO_PROFILE_NAME_AC.equals(name)){
					dto_Radio.setChannelWidth(RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80);
				// High-Capacity-80MHz-11ac-Profile
				} else if (BeParaModule.RADIO_PROFILE_NAME_HIGH_CAPACITY_AC.equals(name)){
					dto_Radio.setChannelWidth(RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80);
					dto_Radio.setSlaThoughput(SlaMappingCustomize.SLA_THROUGHPUT_HIGH);
					dto_Radio.setGuardInterval(true);
				}
				/**
				 * Millau_Merge_End:engineer=zhoushaohua
				 */
				dto_Radio.setWmmItems(RadioProfileAction.getDefaultWmmInfo(dto_Radio));
				dto_Radio.setSupressBprOUIs(RadioProfileAction.getDefaultSupressPBROuis(dto_Radio));
				allRadios.add(dto_Radio);
			}
		}
		return allRadios;
	}

	public static Map<String, TX11aOr11gRateSetting> getSsidGrateSettings(String ssidName) {
		Map<String, TX11aOr11gRateSetting> gRateSet = new LinkedHashMap<>();
		for (GRateType gType : TX11aOr11gRateSetting.GRateType.values()) {
			TX11aOr11gRateSetting rateSet = new TX11aOr11gRateSetting();
			if (BeParaModule.DEFAULT_SSID_PROFILE_NAME.equals(ssidName)) {
				if (GRateType.one.equals(gType) || GRateType.two.equals(gType)
					|| GRateType.five.equals(gType) || GRateType.eleven.equals(gType)) {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
				} else {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
				}
			} else if (BeParaModule.SSID_PROFILE_TEMPLATE_HIGH_CAPACITY.equals(ssidName)) {
				if (GRateType.eighteen.equals(gType)) {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
				} else if (GRateType.twenty_four.equals(gType) || GRateType.thirty_six.equals(gType)
					|| GRateType.forty_eight.equals(gType) || GRateType.fifty_four.equals(gType)) {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
				} else {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_NEI);
				}
			} else if (BeParaModule.SSID_PROFILE_TEMPLATE_SPECTRA_LINK.equals(ssidName)) {
				if (GRateType.one.equals(gType) || GRateType.six.equals(gType)
					|| GRateType.five.equals(gType) || GRateType.nine.equals(gType)) {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_NEI);
				} else if (GRateType.eleven.equals(gType) || GRateType.twelve.equals(gType)) {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
				} else {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
				}
			} else {
				if (GRateType.six.equals(gType) || GRateType.nine.equals(gType)) {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_NEI);
				} else if (GRateType.one.equals(gType) || GRateType.two.equals(gType)) {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
				} else {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
				}
			}
			rateSet.setGRateType(gType);
			gRateSet.put(rateSet.getkey(), rateSet);
		}
		return gRateSet;
	}

	public static Map<String, TX11aOr11gRateSetting> getSsidArateSettings(String ssidName) {
		Map<String, TX11aOr11gRateSetting> aRateSets = new LinkedHashMap<>();
		for (ARateType aType : TX11aOr11gRateSetting.ARateType.values()) {
			TX11aOr11gRateSetting rateSet = new TX11aOr11gRateSetting();
			if (BeParaModule.SSID_PROFILE_TEMPLATE_HIGH_CAPACITY.equals(ssidName)) {
				if (ARateType.eighteen.equals(aType)) {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
				} else if (ARateType.six.equals(aType) || ARateType.nine.equals(aType) || ARateType.twelve.equals(aType)) {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_NEI);
				} else {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
				}
			} else {
				if (ARateType.six.equals(aType) || ARateType.twelve.equals(aType) || ARateType.twenty_four.equals(aType)) {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC);
				} else {
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
				}
			}
			rateSet.setARateType(aType);
			aRateSets.put(rateSet.getkey(), rateSet);
		}
		return aRateSets;
	}

	public static Map<String, TX11aOr11gRateSetting> getSsidNrateSettings(String ssidName) {
		Map<String, TX11aOr11gRateSetting> nRateSets = new LinkedHashMap<>();
		for (NRateType nType : TX11aOr11gRateSetting.NRateType.values()) {
			TX11aOr11gRateSetting rateSet = new TX11aOr11gRateSetting();
			rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
			rateSet.setNRateType(nType);
			nRateSets.put(rateSet.getkey(), rateSet);
		}
		return nRateSets;
	}
	
	
	public static List<Tx11acRateSettings> getSsidAcRateSettings() {
		List<Tx11acRateSettings> acRateList = new ArrayList<Tx11acRateSettings>();
		for (short i = Tx11acRateSettings.STREAM_TYPE_SINGLE; i <= Tx11acRateSettings.STREAM_TYPE_THREE; i ++){
			Tx11acRateSettings acRateSet = new Tx11acRateSettings();
			acRateSet.setStreamType(i);
			acRateSet.setMcsValue(Tx11acRateSettings.MAX_MCS_VALUE);
			acRateList.add(acRateSet);
		}
		return acRateList;
	}

	/**
	 * @see com.ah.be.parameter.BeParaModule#getWatchDogReportInterval()
	 */
	public int getWatchDogReportInterval() {
		return 10000;
	}

	/**
	 * set the message to restore log when restore 2.1 to 3.0, else set to parameter log
	 *
	 * @param str_Message -
	 * @param e -
	 */
	private void setDebugMessage(String str_Message, Exception e) {
		if (ifRestore) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
		} else {
			DebugUtil.parameterDebugWarn(str_Message, e);
		}
	}

}