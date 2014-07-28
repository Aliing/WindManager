package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ah.be.app.HmBeLogUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.BeConfigModule.ConfigType;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.common.CVGAndBRIpResourceManage;
import com.ah.be.config.create.source.AdminConnectionAlarmInt;
import com.ah.be.config.create.source.ApplicationProfileInt;
import com.ah.be.config.create.source.BonjourGatewayInt;
import com.ah.be.config.create.source.CdpProfileInt;
import com.ah.be.config.create.source.ClientModeInt;
import com.ah.be.config.create.source.DesignatedServerInt;
import com.ah.be.config.create.source.InterfaceProfileInt;
import com.ah.be.config.create.source.IpProfileInt;
import com.ah.be.config.create.source.KddrInt;
import com.ah.be.config.create.source.LanPortInt;
import com.ah.be.config.create.source.LldpProfileInt;
import com.ah.be.config.create.source.MacAddressTableInt;
import com.ah.be.config.create.source.MacTableProfileInt;
import com.ah.be.config.create.source.MonitorProfileInt;
import com.ah.be.config.create.source.NetworkFirewallInt;
import com.ah.be.config.create.source.OsDetectionInt;
import com.ah.be.config.create.source.PortChannelInt;
import com.ah.be.config.create.source.PseProfileInt;
import com.ah.be.config.create.source.RoutingProfileInt;
import com.ah.be.config.create.source.SecurityObjectProfileInt;
import com.ah.be.config.create.source.ServiceProfileInt;
import com.ah.be.config.create.source.SpanningTreeInt;
import com.ah.be.config.create.source.StromControlInt;
import com.ah.be.config.create.source.TrackProfileInt;
import com.ah.be.config.create.source.UsbmodemInt;
import com.ah.be.config.create.source.VPNProfileInt;
import com.ah.be.config.create.source.VlanProfileInt;
import com.ah.be.config.create.source.VlanReserveInt;
import com.ah.be.config.create.source.WebSecurityProxyInt;
import com.ah.be.config.create.source.common.AcmOsObject;
import com.ah.be.config.create.source.impl.ap100.InterfaceAP100Impl;
import com.ah.be.config.create.source.impl.brAndCVG.InterfaceBRAndCVGImpl;
import com.ah.be.config.create.source.impl.branchRouter.InterfaceBRImpl;
import com.ah.be.config.create.source.impl.branchRouter.IpProfileBRImpl;
import com.ah.be.config.create.source.impl.branchRouter.LanPortImpl;
import com.ah.be.config.create.source.impl.branchRouter.MacTableProfileBRImpl;
import com.ah.be.config.create.source.impl.branchRouter.PseProfileBRImpl;
import com.ah.be.config.create.source.impl.branchRouter.VPNProfileBRImpl;
import com.ah.be.config.create.source.impl.cvg.InterfaceCVGImpl;
import com.ah.be.config.create.source.impl.cvg.IpProfileCVGImpl;
import com.ah.be.config.create.source.impl.cvg.VPNProfileCVGImpl;
import com.ah.be.config.create.source.impl.sw.InterfaceProfileSwitchImpl;
import com.ah.be.config.create.source.impl.sw.IpProfileSwitchImpl;
import com.ah.be.config.create.source.impl.sw.LldpProfileSwitchImpl;
import com.ah.be.config.create.source.impl.sw.PseProfileSwitchImpl;
import com.ah.be.config.create.source.impl.sw.SecurityObjectSwitchImpl;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.ConfigTemplateVlanNetwork;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.DownloadInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApStaticRoute;
import com.ah.bo.hiveap.MacAddressLearningEntry;
import com.ah.bo.igmp.IgmpPolicy;
import com.ah.bo.igmp.MulticastGroup;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mobility.QosNetworkService;
import com.ah.bo.mobility.TunnelSetting;
import com.ah.bo.network.AirScreenRule;
import com.ah.bo.network.AirScreenRuleGroup;
import com.ah.bo.network.BonjourFilterRule;
import com.ah.bo.network.BonjourGatewaySettings;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.DevicePolicyRule;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.FirewallPolicyRule;
import com.ah.bo.network.IpPolicy;
import com.ah.bo.network.IpPolicyRule;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.MacPolicy;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.OsObjectVersion;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.UserProfileForTrafficL2;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnNetworkSub;
import com.ah.bo.network.VpnService;
import com.ah.bo.network.VpnServiceCredential;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.port.PortMonitorProfile;
import com.ah.bo.useraccess.LdapServerOuUserProfile;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusLibrarySipRule;
import com.ah.bo.useraccess.RadiusServer;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SsidProfile;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.bo.report.ApplicationUtil;

/**
 * 
 * @author zhang
 * 
 */

@SuppressWarnings("static-access")
public class ConfigureProfileFunction {

	private static final Tracer log = new Tracer(ConfigureProfileFunction.class
			.getSimpleName());
	public static String synLock = "";
	public static final String INTERFACE_WIRELESS_MESH = "wireless mesh";
	public static final String BR_AS_PPSK_SERVER_IP = "br_as_ppsk_server_ipaddress";
	
	public static final short BR100_SSID_LIMIT = 4;
	public static final short MAX_IP_POLICY = 32;
	
	private long index_for_ppsk = -99;
	private long index_for_wpa = -199;

	private boolean view = false;
	private HiveAp hiveAp;
	private final List<SsidProfileImpl> ssidProfileList = new ArrayList<SsidProfileImpl>();
	private final List<ScheduleProfileImpl> scheduleList = new ArrayList<ScheduleProfileImpl>();
	private final List<ScheduleProfileImpl> ppskScheduleList = new ArrayList<ScheduleProfileImpl>();
	private final List<UserProfileImpl> userProfileList = new ArrayList<UserProfileImpl>();
	private final Map<Long, UserProfileImpl> userProfileMap = new HashMap<Long, UserProfileImpl>();
	private List<UserProfileForTrafficL2> l2TrafficList;
	private HiveProfileImpl hiveProfile;
	private SecurityProfileImpl securityProfile;
	private RadioProfileImpl radiProfileImpl;
	private AAAProfileImpl aaaProfileImpl;
	private DnsProfileImpl dnsProfileImpl;
	private AdminProfileImpl adminProfileImpl;
	private AmrpProfileImpl amrpProfileImpl;
	private InterfaceProfileInt interfaceProfileImpl;
	private List<IpPolicyProfileImpl> ipPolicyProfileImplList;
	private IpProfileInt ipProfileImpl;
	private LogingProfileImpl logingProfileImpl;
	private List<MacPolicyProfileImpl> macPolicyProfileImplList;
	private List<MobilityPolicyProfileImpl> mobilityPolicyProfileImplList;
	private MobilityThresholdProfileImpl mobilityThresholdProfileImpl;
	private NtpProfileImpl ntpProfileImpl;
	private RoamingProfileImpl roamingProfileImpl;
	private List<RouteProfileImpl> routeProfileImplList;
	private SnmpProfileImpl snmpProfileImpl;
	private ClockProfileImpl clockProfileImpl;
	private QosProfileImpl qosProfileImpl;
	private List<ServiceProfileImpl> serviceProfileList;
	private ResetButtonProfileImpl resetButtonImpl;
	private ForwardingEngineImpl forwardingEngineImpl;
	private AlgProfileImpl algProfileImpl;
	private LocationProfileImpl locationProfileImpl;
	private CapwapProfileImpl capwapProfileImpl;
	private HivemanagerProfileImpl hivemanagerProfileImpl;
	private HostnameProfileImpl hostnameProfileImpl;
	private ConsoleProfileImpl consoleProfileImp;
	private SystemProfileImpl systemProfileImpl;
	private List<TrackProfileInt> trackProfileImpl;
	private List<TrackProfileInt> trackWanProfileImpl;
	private CacProfileImpl cacProfileImpl;
	private AccessConsoleImpl accessConsoleImpl;
	private LldpProfileInt lldpProfileImpl;
	private ConfigType configType;
	private final List<UserGroupProfileImpl> userGroupList = new ArrayList<UserGroupProfileImpl>();
	private final List<UserImpl> userList = new ArrayList<UserImpl>();
	private final List<SsidBindUserGroupImpl> ssidUserGroupBindList = new ArrayList<SsidBindUserGroupImpl>();
	private RadiusBindGroupImpl radiusBindGroupImpl;
	private final List<PskAutoUserGroupImpl> autoUserGroupList = new ArrayList<PskAutoUserGroupImpl>();
	private VPNProfileInt vpnImpl;
	private AirScreenProfileImpl airScreenImpl;
	private PerformanceSentinelImpl performanceImpl;
	private final List<SecurityObjectProfileInt> securityObjList = new ArrayList<SecurityObjectProfileInt>();
	private final List<LibrarySipPolicyImpl> librarySipPolicyList = new ArrayList<LibrarySipPolicyImpl>();
	private ReportProfileImpl reportImpl;
	private final List<MobileDevicePolicyImpl> devicePolicyList = new ArrayList<MobileDevicePolicyImpl>();
	private final List<DeviceGroupImpl> deviceGroupList = new ArrayList<DeviceGroupImpl>();
	private final List<OsObjectImpl> osObjectList = new ArrayList<OsObjectImpl>();
	private final List<MacObjectImpl> macObjectList = new ArrayList<MacObjectImpl>();
	private final List<DomainObjectImpl> domainObjectList = new ArrayList<DomainObjectImpl>();
	private DataCollectionImpl collectionImpl;
	private ConfigProfileImpl configProfileImpl;
	private NetworkFirewallInt networkFirewallImpl;
	private WebSecurityProxyInt webProxyImpl;
	private UsbmodemInt usbmodemImpl;
	private RoutingProfileInt routingImpl;
	private LanPortInt LanProfileImpl;
	private PseProfileInt pseProfileImpl;
	private MacTableProfileInt macTableProfileImpl;
	private final List<OsVersionImpl> osVersionList = new ArrayList<OsVersionImpl>();
	private OsDetectionInt osDetectionImpl;
	private BonjourGatewayInt bonjourGatewayImpl;
	private DesignatedServerInt DesignatedServerImpl;
	private ApplicationProfileInt applicationProfileImpl;
	private AdminConnectionAlarmInt adminConnectionAlarmImpl;
	private final List<VlanGroupProfileImpl> vlanGroupList = new ArrayList<VlanGroupProfileImpl>();
	private PortChannelInt portChannelImpl;
	private MacAddressTableInt macAddressTableImpl;
	private CdpProfileInt cdpProfileImpl;
	private ClientModeInt clientModeImpl;
	private List<VlanProfileInt> vlanImplList;
	private MonitorProfileInt monitorImpl;
	private VlanReserveInt vlanReserveImpl;
	private SpanningTreeInt spanningTreeImpl;
	private StromControlInt stormImpl;
	private KddrInt kddrImpl;
//	private ConfigMdmService			configmdmservice		= new ConfigMdmServiceImpl();

	public ConfigureProfileFunction(HiveAp hiveAp, ConfigType configType, boolean view) {
		this.configType = configType;
		this.view = view;
		this.hiveAp = hiveAp;
	}
	
	public static void br100SsidLimit(HiveAp hiveAp){
		if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
			short maxCount = BR100_SSID_LIMIT;
			if(hiveAp.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_DUAL){
				maxCount --;
			}
			short counts = 1;
			List<Long> rmSsidLists = new ArrayList<Long>();
			for (Long key : hiveAp.getConfigTemplate().getSsidInterfacesTreeMap().keySet()) {
				ConfigTemplateSsid ssidRelation = hiveAp.getConfigTemplate().getSsidInterfacesTreeMap().get(key);
				if (!ssidRelation.getInterfaceName().equalsIgnoreCase(
						InterfaceProfileInt.InterType.eth0.name())
						&& !ssidRelation.getInterfaceName().equalsIgnoreCase(
								INTERFACE_WIRELESS_MESH)
						&& !ssidRelation.getInterfaceName().equalsIgnoreCase(
								InterfaceProfileInt.InterType.eth1.name())
						&& !ssidRelation.getInterfaceName().equalsIgnoreCase(
								InterfaceProfileInt.InterType.eth2.name())
						&& !ssidRelation.getInterfaceName().equalsIgnoreCase(
								InterfaceProfileInt.InterType.eth3.name())
						&& !ssidRelation.getInterfaceName().equalsIgnoreCase(
								InterfaceProfileInt.InterType.eth4.name())
						&& !ssidRelation.getInterfaceName().equalsIgnoreCase(
								InterfaceProfileInt.InterType.agg0.name())
						&& !ssidRelation.getInterfaceName().equalsIgnoreCase(
								InterfaceProfileInt.InterType.red0.name())) {

					SsidProfile ssidProfileObj = ssidRelation.getSsidProfile();
					if(ssidProfileObj != null){
						if(counts > maxCount){
							rmSsidLists.add(key);
						}
						counts ++;
					}
					if(ssidProfileObj != null && ssidProfileObj.isEnablePpskSelfReg()){
						if(counts > maxCount){
							rmSsidLists.add(key);
						}
						counts ++;
					}
				}
			}
			if(!rmSsidLists.isEmpty()){
				for(Long key : rmSsidLists){
					hiveAp.getConfigTemplate().getSsidInterfaces().remove(key);
				}
			}
		}
	}

	public void loadPrfile() throws Exception {
		
		//reload hiveap from db
		String softver = hiveAp.getSoftVer();
		DownloadInfo dInfoView = hiveAp.getDownloadInfoView();
		this.hiveAp = MgrUtil.getQueryEntity().findBoById(HiveAp.class, hiveAp
				.getId(), new ConfigLazyQueryBo());
		if(view){
			this.hiveAp.setDownloadInfo(dInfoView);
		}
		this.hiveAp.setSoftVer(softver);
		
		//load wifi client mode ssid from HiveAp
//		prepareWifiClientMode();
		
		//for BR100 limit 8 SSIDs
		br100SsidLimit(hiveAp);
		
		hiveAp.synchronizeCVGInterfaceState();
		
		//fix bug 25496
		prepareBrAsRadiusPpskServer();
		cloneSsidForPPSK(hiveAp);
		
		if(isDeviceNeedAllUserProfile(hiveAp)){
			loadUserProfileList();
		}
		
		prepareDeviceDataSource(view);
		cloneSsidForWPA(hiveAp);
		
		if(!isDeviceNeedAllUserProfile(hiveAp)){
			loadUserProfileList();
		}
		
		//this must after user profile be loaded.
		filterNetworkObject();
		
		String profileKey = null;
		
		//CLI update check
		configUpdateCheck();
		
		try{
			if (this.configType == ConfigType.USER_FULL) {
				
				profileKey = "Ssid Profile";
				loadSsidProfileList();
				
				profileKey = "Radius Server";
				loadAAAProfile();
				loadUserGroupList();		//for check max user-group and users
				
				profileKey = "Check Max User";
				checkMaxUser();
				
				profileKey = "PSK Auto User Group";
				loadAutoUserGroupLis();
				
				profileKey = "Radius Server Local User";
				loadLocalUserImpl();
				
				profileKey = "PpskSchedule";
				loadPpskScheduleList();
			} else {
				loadVlanImplList();
				
				profileKey = "Interface";
				loadInterfaceProfile(this.view);
				
//				StringBuilder routeMsg = new StringBuilder();
//				if(!checkBRStaticRoute(this.hiveAp, this.view, routeMsg)){
//					throw new CreateXMLException(routeMsg.toString());
//				}
				
				loadAirScreenProfile();

				profileKey = "Ssid Profile";
				loadSsidProfileList();
				
				profileKey = "Security Obj";
				loadSecurityObjProfile();
				
				loadLibrarySipPolicyList();
				
				loadDevicePolicyList();
				
				profileKey = "Schedule";
				loadScheduleList();
				
				profileKey = "Hive Profile";
				loadHiveProfile();
				
				profileKey = "Security Profile";
				loadSecurityProfile();
				
				profileKey = "Radius Server";
				loadAAAProfile();
				
				profileKey = "Dns Server";
				loadDnsProfileImpl();
				
				profileKey = "Admin User";
				loadAdminProfile();
				
				profileKey = "Amrp";
				loadAmrpProfile();
				
				profileKey = "Ip Policy";
				loadIpPolicyProfileList();
				
				profileKey = "Ip Profile";
				loadIpProfile();
				
				profileKey = "Loging Server";
				loadLogingProfile();
				
				profileKey = "Mac Policy";
				loadMacPolicyProfileImplList();
				
				profileKey = "Mobility Policy";
				loadMobilityPolicyProfileImplList();
				
				profileKey = "Mobility Threshold";
				loadMobilityThresholdProfile();
				
				profileKey = "Ntp Server";
				loadNtpProfile();
				
				profileKey = "Radio Profile";
				loadRadiProfileImplList();
				
				profileKey = "Roaming";
				loadRoamingProfileImpl();
				
				profileKey = "Route";
				loadRouteProfileImplList();
				
				profileKey = "Snmp Server";
				loadSnmpProfileImpl();
				
				profileKey = "Clock";
				loadClockProfileImpl();
				
				profileKey = "Qos Setting";
				loadQosProfileImpl();
				
				profileKey = "Service";
				loadServiceProfileList();
				
				profileKey = "Reset Button";
				loadResetButtonImpl();
				
				profileKey = "Forwarding Engine";
				loadForwardingEngineImpl();
				
				profileKey = "Alg Setting";
				loadAlgProfileImpl();
				
				profileKey = "Location Setting";
				loadLocationProfileImpl();
				
				profileKey = "Capwap Setting";
				loadCapwapProfileImpl();
				
				loadHivemanagerProfileImpl();
				
				profileKey = "Host Name";
				loadHostNameProfileImpl();
				
				profileKey = "Console Setting";
				loadConsoleProfileImp();
				
				profileKey = "System Setting";
				loadSystemProfileImpl();
				
				profileKey = "Track Setting";
				loadTrackProfileImpl();
				
				profileKey = "TrackWan Setting";
				loadTrackWanProfileImpl();

				profileKey = "Cac Setting";
				loadCacProfileImpl();
				
				profileKey = "Access Console";
				loadAccessConsoleImpl();
				
				profileKey = "Lldp Setting";
				loadLldpProfileImpl();
				
				profileKey = "User Group";
				loadUserGroupList();
				
				profileKey = "VPN Services";
				loadVpnImpl();
				
				profileKey = "WLAN Policies";
				loadPerformanceSentinelImpl();
				
				profileKey = "WLAN Policies";
				loadReportProfileImpl();
				
				loadDeviceGroupList();
				
				loadOsObjectList();
				
				loadMacObjectList();
				
				loadDomainObjectList();
				
				loadCollectionImpl();
				
				loadConfigProfileImpl();
				
				loadNetworkFirewallImpl();
				
				loadWebProxyImpl();
				
				loadUsbmodemImpl();
				
				loadRoutingImpl();
				
				loadLanProfileImpl();
				
				loadPseProfileImpl();
				
				loadMacTableProfileImpl();
				
				loadOsDetection();
				
				loadOsVersionList();
				
				loadBonjourGateway();
				
				loadDesignatedServerImpl();
				
				loadApplicationProfileImpl(this.view);
				
				loadVlanGroupProfileImpl();
				
				loadAdminConnectionAlarmImpl();
				
				loadPortChannelImpl();
				
				loadMacAddressTableImpl();
				
				loadCdpProfileImpl();
				
				loadClientModeImpl();
				
				loadMonitorProfileImpl();
				
				loadVlanReserveImpl();
				
				loadSpanningTreeImpl();
				
				loadStromControlImpl();
				
				loadKddrImpl();
			}
		}catch(CreateXMLException cxe){
			throw cxe;
		}catch(Exception e){
			String errMsg = NmsUtil.getUserMessage("error.be.config.create.loadprofileError", new String[]{profileKey});
			log.error("ConfigureProfileFunction", e.getMessage(), e);
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_CRITICAL,
					HmSystemLog.FEATURE_CONFIGURATION, e.getMessage());
			throw new CreateXMLException(errMsg);
		}
	}
	
	private void prepareBrAsRadiusPpskServer(){
//		IpAddress cfgIpAddress = CLICommonFunc.getGlobalIpAddress(hiveAp.getCfgIpAddress(), "255.255.255.0");
		RadiusServer radServer = new RadiusServer();
		radServer.setUseSelfAsServer(true);
		radServer.setServerPriority(RadiusServer.RADIUS_PRIORITY_PRIMARY);
		
		if(hiveAp.isOverWriteRadiusServer() && hiveAp.isEnabledBrAsRadiusServer()){
			for(ConfigTemplateSsid ssidTemp : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
				SsidProfile ssidProfile = ssidTemp.getSsidProfile();
				if(ssidProfile != null){
					RadiusAssignment radiusClient =  ssidProfile.getRadiusAssignment();
					RadiusAssignment radiusPpsk = ssidProfile.getRadiusAssignmentPpsk();
					if(radiusClient != null){
						if(radiusClient.getServices() == null){
							radiusClient.setServices(new ArrayList<RadiusServer>());
						}
						if(radiusClient.getServices().isEmpty()){
							radiusClient.getServices().add(radServer);
						}
					}
					
					if(radiusPpsk != null){
						if(radiusPpsk.getServices() == null){
							radiusPpsk.setServices(new ArrayList<RadiusServer>());
						}
						if(radiusPpsk.getServices().isEmpty()){
							radiusPpsk.getServices().add(radServer);
						}
					}
				}
			}
			
			//fix bug 24704
			PortGroupProfile portGroup = hiveAp.getPortGroup();
			if(portGroup != null && portGroup.getBasicProfiles() != null){
				for(PortBasicProfile baseProfile : portGroup.getBasicProfiles()){
					if(baseProfile.getAccessProfile() == null){
						continue;
					}
					RadiusAssignment radiusClient = baseProfile.getAccessProfile().getRadiusAssignment();
					if(radiusClient != null){
						if(radiusClient.getServices() == null){
							radiusClient.setServices(new ArrayList<RadiusServer>());
						}
						if(radiusClient.getServices().isEmpty()){
							radiusClient.getServices().add(radServer);
						}
					}
				}
			}
		}
		
//		if(hiveAp.isBranchRouter()){
//			for(ConfigTemplateSsid ssidTemp : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
//				SsidProfile ssidProfile = ssidTemp.getSsidProfile();
//				if(ssidProfile != null && ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_PSK 
//						&& (ssidProfile.isEnablePpskSelfReg() || ssidProfile.getSsidSecurity().isBlnMacBindingEnable()) 
//						&& ssidProfile.isBlnBrAsPpskServer()){
////					ssidProfile.setBlnBrAsPpskServer(false);
//					ssidProfile.setPpskServer(hiveAp);
//				}
//			}
//		}
	}

	public String getXMLFileName() {
		return hiveAp.getMacAddress();
	}

	public List<SsidProfileImpl> getSsidProfileList() {
		return this.ssidProfileList;
	}

	public List<ScheduleProfileImpl> getScheduleList() {
		return this.scheduleList;
	}
	
	public List<ScheduleProfileImpl> getPpskScheduleList() {
		return this.ppskScheduleList;
	}

	public List<UserProfileImpl> getUserProfileList() {
		return this.userProfileList;
	}

	public HiveProfileImpl getHiveProfile() {
		return this.hiveProfile;
	}

	public SecurityProfileImpl getSecurityProfile() {
		return this.securityProfile;
	}

	public AAAProfileImpl getAAAProfileImpl() {
		return this.aaaProfileImpl;
	}

	public DnsProfileImpl getDnsProfileImpl() {
		return this.dnsProfileImpl;
	}

	public AdminProfileImpl getAdminProfileImpl() {
		return this.adminProfileImpl;
	}

	public AmrpProfileImpl getAmrpProfileImpl() {
		return this.amrpProfileImpl;
	}

	public InterfaceProfileInt getInterfaceProfileImpl() {
		return this.interfaceProfileImpl;
	}

	public List<IpPolicyProfileImpl> getIpPolicyProfileImplList() {
		return this.ipPolicyProfileImplList;
	}

	public IpProfileInt getIpProfileImpl() {
		return this.ipProfileImpl;
	}

	public LogingProfileImpl getLogingProfileImpl() {
		return this.logingProfileImpl;
	}

	public List<MacPolicyProfileImpl> getMacPolicyProfileImplList() {
		return this.macPolicyProfileImplList;
	}

	public List<MobilityPolicyProfileImpl> getMobilityPolicyProfileImplList() {
		return this.mobilityPolicyProfileImplList;
	}

	public MobilityThresholdProfileImpl getMobilityThresholdProfileImpl() {
		return this.mobilityThresholdProfileImpl;
	}

	public NtpProfileImpl getNtpProfileImpl() {
		return this.ntpProfileImpl;
	}

	public RadioProfileImpl getRadioProfileImpl() {
		return this.radiProfileImpl;
	}

	public RoamingProfileImpl getRoamingProfileImpl() {
		return this.roamingProfileImpl;
	}

	public List<RouteProfileImpl> getRouteProfileImplList() {
		return this.routeProfileImplList;
	}

	public SnmpProfileImpl getSnmpProfileImpl() {
		return this.snmpProfileImpl;
	}

	public ClockProfileImpl getClockProfileImpl() {
		return this.clockProfileImpl;
	}

	public QosProfileImpl getQosProfileImpl() {
		return this.qosProfileImpl;
	}

	public List<ServiceProfileImpl> getServiceProfileImplList() {
		return this.serviceProfileList;
	}

	public ResetButtonProfileImpl getResetButtonImpl() {
		return this.resetButtonImpl;
	}

	public ForwardingEngineImpl getForwardingEngineImpl() {
		return this.forwardingEngineImpl;
	}

	public AlgProfileImpl getAlgProfileImpl() {
		return this.algProfileImpl;
	}
	
	public PseProfileInt getPseProfileImpl() {
		return pseProfileImpl;
	}
	
	public MacTableProfileInt getMacTableProfileImpl() {
		return macTableProfileImpl;
	}
	
	public LocationProfileImpl getLocationProfileImpl() {
		return this.locationProfileImpl;
	}

	public CapwapProfileImpl getCapwapProfileImpl() {
		return this.capwapProfileImpl;
	}

	public HivemanagerProfileImpl getHivemanagerProfileImpl() {
		return this.hivemanagerProfileImpl;
	}

	public HostnameProfileImpl getHostnameProfileImpl() {
		return this.hostnameProfileImpl;
	}

	public ConsoleProfileImpl getConsoleProfileImp() {
		return this.consoleProfileImp;
	}

	public SystemProfileImpl getsystemProfileImpl() {
		return this.systemProfileImpl;
	}

	public List<TrackProfileInt> getTrackProfileImplList() {
		return this.trackProfileImpl;
	}

	public List<TrackProfileInt> getTrackWanProfileImplList() {
		return this.trackWanProfileImpl;
	}

	public CacProfileImpl getCacProfileImpl() {
		return this.cacProfileImpl;
	}

	public AccessConsoleImpl getAccessConsoleImpl() {
		return this.accessConsoleImpl;
	}

	public LldpProfileInt getLldpProfileImpl() {
		return this.lldpProfileImpl;
	}
	
	public List<UserGroupProfileImpl> getUserGroupList(){
		return this.userGroupList;
	}
	
	public List<UserImpl> getUserList(){
		return this.userList;
	}
	
	public List<SsidBindUserGroupImpl> getSsidUserGroupBindList() {
		return this.ssidUserGroupBindList;
	}
	
	public RadiusBindGroupImpl getRadiusBindGroupImpl(){
		return this.radiusBindGroupImpl;
	}
	
	public List<PskAutoUserGroupImpl> getPskAutoUserGroupList(){
		return this.autoUserGroupList;
	}
	
	public VPNProfileInt getVpnProfileImpl(){
		return this.vpnImpl;
	}
	
	public AirScreenProfileImpl getAirScreenProfileImpl(){
		return this.airScreenImpl;
	}
	
	public PerformanceSentinelImpl getPerformanceSentinelImpl(){
		return this.performanceImpl;
	}
	
	public List<SecurityObjectProfileInt> getSecurityObjList(){
		return this.securityObjList;
	}
	
	public List<LibrarySipPolicyImpl> getLibrarySipPolicyList(){
		return this.librarySipPolicyList;
	}
	
	public List<MobileDevicePolicyImpl> getDevicePolicyList(){
		return this.devicePolicyList;
	}
	
	public List<DeviceGroupImpl> getDeviceGroupList(){
		return this.deviceGroupList;
	}
	
	public List<OsObjectImpl> getOsObjectList(){
		return this.osObjectList;
	}
	
	public List<OsVersionImpl> getOsVersionList() {
		return osVersionList;
	}

	public OsDetectionInt getOsDetectionImpl() {
		return osDetectionImpl;
	}

	public List<MacObjectImpl> getMacObjectList(){
		return this.macObjectList;
	}
	
	public List<DomainObjectImpl> getDomainObjectList(){
		return this.domainObjectList;
	}
	
	public ReportProfileImpl getReportProfileImpl(){
		return this.reportImpl;
	}
	
	public DataCollectionImpl getCollectionImpl(){
		return this.collectionImpl;
	}
	
	public ConfigProfileImpl getConfigProfileImpl(){
		return this.configProfileImpl;
	}
	
	public NetworkFirewallInt getNetworkFirewallImpl(){
		return this.networkFirewallImpl;
	}
	
	public WebSecurityProxyInt getWebProxyImpl(){
		return this.webProxyImpl;
	}
	
	public UsbmodemInt getUsbmodemImpl(){
		return this.usbmodemImpl;
	}
	
	public RoutingProfileInt getRoutingImpl(){
		return this.routingImpl;
	}
	
	public BonjourGatewayInt getBonjourGatewayImpl(){
		return this.bonjourGatewayImpl;
	}
	
	public LanPortInt getLanPortImpl(){
		return this.LanProfileImpl;
	}
	
	public DesignatedServerInt getDesignatedServerImpl(){
		return this.DesignatedServerImpl;
	}

	public ApplicationProfileInt getApplicationProfileImpl() {
		return applicationProfileImpl;
	}
	
	public AdminConnectionAlarmInt getAdminConnectionAlarmImpl() {
		return adminConnectionAlarmImpl;
	}
	
	public PortChannelInt getPortChannelImpl() {
		return portChannelImpl;
	}

	public MacAddressTableInt getMacAddressTableImpl() {
		return macAddressTableImpl;
	}
	
	public CdpProfileInt getCdpProfileImpl() {
		return cdpProfileImpl;
	}
	
	public ClientModeInt getClientModeImpl(){
		return clientModeImpl;
	}

	public List<VlanGroupProfileImpl> getVlanGroupList() {
		return vlanGroupList;
	}
	
	public List<VlanProfileInt> getVlanImplList(){
		return this.vlanImplList;
	}
	
	public MonitorProfileInt getMonitorImpl(){
		return this.monitorImpl;
	}
	
	public VlanReserveInt getVlanReserveImpl(){
		return this.vlanReserveImpl;
	}
	
	public SpanningTreeInt getSpanningTreeImpl(){
		return this.spanningTreeImpl;
	}
	
	public StromControlInt getStromControlImpl(){
		return this.stormImpl;
	}
	
	public KddrInt getKddrImpl(){
		return this.kddrImpl;
	}

	private void loadSsidProfileList() {
		Map<Long, ConfigTemplateSsid> ssidInterfaces = hiveAp.getConfigTemplate().getSsidInterfaces();

		for (ConfigTemplateSsid ssidRelation : ssidInterfaces.values()) {
			if (!ssidRelation.getInterfaceName().equalsIgnoreCase(
					InterfaceProfileInt.InterType.eth0.name())
					&& !ssidRelation.getInterfaceName().equalsIgnoreCase(
							INTERFACE_WIRELESS_MESH)
					&& !ssidRelation.getInterfaceName().equalsIgnoreCase(
							InterfaceProfileInt.InterType.eth1.name())
					&& !ssidRelation.getInterfaceName().equalsIgnoreCase(
							InterfaceProfileInt.InterType.eth2.name())
					&& !ssidRelation.getInterfaceName().equalsIgnoreCase(
							InterfaceProfileInt.InterType.eth3.name())
					&& !ssidRelation.getInterfaceName().equalsIgnoreCase(
							InterfaceProfileInt.InterType.eth4.name())
					&& !ssidRelation.getInterfaceName().equalsIgnoreCase(
							InterfaceProfileInt.InterType.agg0.name())
					&& !ssidRelation.getInterfaceName().equalsIgnoreCase(
							InterfaceProfileInt.InterType.red0.name())) {

				SsidProfile ssidProfileObj = ssidRelation.getSsidProfile();
				SsidProfileImpl ssidProfileImpl = new SsidProfileImpl(
						ssidProfileObj, hiveAp);
				ssidProfileList.add(ssidProfileImpl);
			}
		}
	}
	
	private void loadSecurityObjProfile() throws Exception{
		for(SsidProfileImpl ssidImpl : ssidProfileList){
			securityObjList.add(new SecurityObjectProfileImpl(ssidImpl.getSsidProfile(),this.hiveAp));
		}
		
		boolean isSwitch = hiveAp.getDeviceInfo().isSptEthernetMore_24();
		//security object for switch
		if(hiveAp.getPortGroup() != null && hiveAp.getPortGroup().getBasicProfiles() != null){
			for(PortBasicProfile baseProfile : hiveAp.getPortGroup().getBasicProfiles()){
				if(baseProfile.getAccessProfile() != null && 
						baseProfile.getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_WAN){
					continue;
				}
				if(isSwitch){
					if(baseProfile.isExistsAuthMode(hiveAp.getDeviceType()) && 
							!baseProfile.isEnabledlinkAggregation()){
						securityObjList.add(new SecurityObjectSwitchImpl(baseProfile.getAccessProfile(), this.hiveAp));
					}
				}else{
					securityObjList.add(new SecurityObjectSwitchImpl(baseProfile.getAccessProfile(), this.hiveAp));
				}
			}
		}
		
		if(hiveAp.getEthConfigType() == HiveAp.USE_ETHERNET_BOTH){
			if(hiveAp.getEth0().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL 
					&& (hiveAp.isEthCwpEnableEthCwp() || hiveAp.isEthCwpEnableMacAuth() || hiveAp.isEnableMDM())){
				securityObjList.add(new SecurityObjectProfileImpl(null, this.hiveAp));
			}
			if(hiveAp.getEth1().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL 
					&& (hiveAp.isEthCwpEnableEthCwp() || hiveAp.isEthCwpEnableMacAuth() || hiveAp.isEnableMDM())){
				securityObjList.add(new SecurityObjectProfileImpl(null, this.hiveAp));
			}
		}
	}
	
	private void loadLibrarySipPolicyList() {
		if(hiveAp.getRadiusServerProfile() != null && hiveAp.getRadiusServerProfile().getServerEnable() && 
				hiveAp.getRadiusServerProfile().getSipPolicy() != null){
			librarySipPolicyList.add(new LibrarySipPolicyImpl(hiveAp));
		}
	}

	private void loadScheduleList() {
		
//		List<UserProfile> userProfileList = ConfigureProfileFunction
//				.getAllUserProfileList(hiveAp);
		List<Scheduler> tempList = new ArrayList<Scheduler>();
		
		Map<Long, ConfigTemplateSsid> ssidTemplateList = this.hiveAp.getConfigTemplate().getSsidInterfaces();
		List<SsidProfile> ssidList = new ArrayList<SsidProfile>();

		// schedule from ssid
		for (ConfigTemplateSsid confTempSsid : ssidTemplateList.values()) {
			if (confTempSsid.getSsidProfile() != null) {
				ssidList.add(confTempSsid.getSsidProfile());
				tempList.addAll(confTempSsid.getSsidProfile().getSchedulers());
			}
		}

		// schedule from user-profile
		for (UserProfileImpl userProfileImp : this.userProfileList) {
			if (userProfileImp.getUserProfile().getUserProfileSchedulers() != null) {
				tempList.addAll(userProfileImp.getUserProfile().getUserProfileSchedulers());
			}
		}
		
		// schedule from PersonalizedPsk
		if(NmsUtil.compareSoftwareVersion("3.5.0.0", hiveAp.getSoftVer()) > 0){
			for(SsidProfile ssidObj : ssidList){
				if(ssidObj.getLocalUserGroups() != null){
					for(LocalUserGroup userGroup : ssidObj.getLocalUserGroups()){
						if(userGroup.getSchedule() != null){
							tempList.add(userGroup.getSchedule());
						}
					}
				}
			}
		}

		String scheduleName;
		boolean isFind;

		for (Scheduler scheduleObj : tempList) {
			scheduleName = scheduleObj.getSchedulerName();
			isFind = false;

			for (ScheduleProfileImpl scheduleObj2 : scheduleList) {
				if (scheduleObj2.getScheduleName().equals(scheduleName)) {
					isFind = true;
					break;
				}
			}

			if (!isFind) {
				scheduleList.add(new ScheduleProfileImpl(scheduleObj, hiveAp));
			}
		}
	}
	
	private void loadPpskScheduleList() {

		List<Scheduler> tempList = new ArrayList<Scheduler>();
		
		Map<Long, ConfigTemplateSsid> ssidTemplateList = this.hiveAp.getConfigTemplate().getSsidInterfaces();
		List<SsidProfile> ssidList = new ArrayList<SsidProfile>();

		// schedule from ssid
		for (ConfigTemplateSsid confTempSsid : ssidTemplateList.values()) {
			if (confTempSsid.getSsidProfile() != null) {
				ssidList.add(confTempSsid.getSsidProfile());
			}
		}
		
		// schedule from PersonalizedPsk
		for(SsidProfile ssidObj : ssidList){
			if(ssidObj.getLocalUserGroups() != null){
				for(LocalUserGroup userGroup : ssidObj.getLocalUserGroups()){
					if(userGroup.getSchedule() != null){
						tempList.add(userGroup.getSchedule());
					}
				}
			}
		}

		String scheduleName;
		boolean isFind;

		for (Scheduler scheduleObj : tempList) {
			scheduleName = scheduleObj.getSchedulerName();
			isFind = false;

			for (ScheduleProfileImpl scheduleObj2 : ppskScheduleList) {
				if (scheduleObj2.getScheduleName().equals(scheduleName)) {
					isFind = true;
					break;
				}
			}

			if (!isFind) {
				ppskScheduleList.add(new ScheduleProfileImpl(scheduleObj, hiveAp));
			}
		}
	}

	/* create for loadUserProfileList */
	private UserProfile addUserProfileToList(UserProfile userProfileObj)
			throws CreateXMLException {
		if(userProfileObj.isDefaultFlag()){
			return userProfileObj;
		}
		UserProfile userProfileRes = userProfileObj;
		long id = userProfileObj.getId();
		
		if(userProfileMap.containsKey(id)){
			userProfileRes = userProfileMap.get(id).getUserProfile();
		}else{
			if(id > 0){
				userProfileRes = MgrUtil.getQueryEntity().findBoById(
						UserProfile.class, id,
						new ConfigLazyQueryBo());
			}
			//default user profile no need upload.
			if(userProfileRes.isDefaultFlag()){
				return userProfileRes;
			}
			setL2VpnServiceTunnel(userProfileRes, this.hiveAp);
			userProfileMap.put(id, new UserProfileImpl(userProfileRes, hiveAp, this));
		}
		return userProfileRes;
	}
	
	private void setL2VpnServiceTunnel(UserProfile userProfileRes, HiveAp hiveAp){
		if(l2TrafficList == null){
			l2TrafficList = new ArrayList<UserProfileForTrafficL2>();
			VpnService l2_VpnServer = hiveAp.getConfigTemplate().getVpnService();
			if(l2_VpnServer != null && l2_VpnServer.getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_2 && 
					l2_VpnServer.getUserProfileTrafficL2() != null && !l2_VpnServer.getUserProfileTrafficL2().isEmpty()){
				l2TrafficList = l2_VpnServer.getUserProfileTrafficL2();
			}
		}
		for(UserProfileForTrafficL2 traffic : l2TrafficList){
			if(traffic.getUserProfile() == null){
				continue;
			}
			if(userProfileRes.getId().equals(traffic.getUserProfile().getId()) 
					&& traffic.getVpnTunnelModeL2() == UserProfileForTrafficL2.VPNTUNNEL_MODE_ENABLED){
				if("tunnelAll".equals(traffic.getTunnelSelected())){
					userProfileRes.setTunnelTraffic(UserProfile.VPN_TUNNEL_TRAFFIC_ALL);
				}else if("splitTunnel".equals(traffic.getTunnelSelected())){
					userProfileRes.setTunnelTraffic(UserProfile.VPN_TUNNEL_TRAFFIC_NOT_LOCAL_INTERNET);
				}
				break;
			}
		}
	}

	private void loadUserProfileList() throws CreateXMLException {
		
		/** aaa radius server */
		List<Long> priorUserProfile = new ArrayList<Long>();
		Set<Long> priorUserProfileSet = new HashSet<Long>();
		if(hiveAp.getRadiusServerProfile() != null){
			List<LdapServerOuUserProfile> ldapOuUserProfiles = hiveAp.getRadiusServerProfile().getLdapOuUserProfiles();
			if(ldapOuUserProfiles != null && !ldapOuUserProfiles.isEmpty()){
				for(LdapServerOuUserProfile upItem : ldapOuUserProfiles){
					if(upItem == null){
						continue;
					}
					UserProfile upObj = MgrUtil.getQueryEntity().findBoById(UserProfile.class, upItem.getUserProfileId());
					if(!priorUserProfileSet.contains(upItem.getUserProfileId()) && !upObj.isDefaultFlag()){
						priorUserProfile.add(upItem.getUserProfileId());
						priorUserProfileSet.add(upItem.getUserProfileId());
						this.addUserProfileToList(upObj);
					}
				}
			}
		}
		
		/** configTemplate */
		for (ConfigTemplateSsid userProfileBind : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
			SsidProfile ssidProfileObj = userProfileBind.getSsidProfile();
			if(ssidProfileObj != null){
				if(ssidProfileObj.getUserProfileDefault() != null){
					UserProfile userProfileObj = ssidProfileObj.getUserProfileDefault();
					userProfileObj = this.addUserProfileToList(userProfileObj);
					ssidProfileObj.setUserProfileDefault(userProfileObj);
				}
				if(ssidProfileObj.getUserProfileSelfReg() != null){
					UserProfile userProfileObj = ssidProfileObj.getUserProfileSelfReg();
					userProfileObj = this.addUserProfileToList(userProfileObj);
					ssidProfileObj.setUserProfileSelfReg(userProfileObj);
				}
				if(ssidProfileObj.getRadiusUserProfile() != null){
					Set<UserProfile> userProfileSet = new HashSet<UserProfile>();
					for(UserProfile userObj : ssidProfileObj.getRadiusUserProfile()){
						userObj = this.addUserProfileToList(userObj);
						userProfileSet.add(userObj);
					}
					ssidProfileObj.setRadiusUserProfile(userProfileSet);
				}
				if(ssidProfileObj.getUserProfileGuest() != null){
					UserProfile userProfileObj = ssidProfileObj.getUserProfileGuest();
					userProfileObj = this.addUserProfileToList(userProfileObj);
					ssidProfileObj.setUserProfileGuest(userProfileObj);
				}
				
			}
		}
		
		/** UserProfile from port profile */
		if(hiveAp.getPortGroup() != null && hiveAp.getPortGroup().getBasicProfiles() != null){
			for(PortBasicProfile baseProfile : hiveAp.getPortGroup().getBasicProfiles()){
				
				PortAccessProfile accessProfile = baseProfile.getAccessProfile();
				if(accessProfile.getDefUserProfile() != null){
					this.addUserProfileToList(accessProfile.getDefUserProfile());
				}
				if(accessProfile.getSelfRegUserProfile() != null){
					this.addUserProfileToList(accessProfile.getSelfRegUserProfile());
				}
//				if(accessProfile.getDataUserProfile() != null){
//					this.addUserProfileToList(accessProfile.getDataUserProfile());
//				}
				if(accessProfile.getAuthOkUserProfile() != null){
					for(UserProfile up : accessProfile.getAuthOkUserProfile()){
						this.addUserProfileToList(up);
					}
				}
				if(accessProfile.getAuthFailUserProfile() != null){
					for(UserProfile up : accessProfile.getAuthFailUserProfile()){
						this.addUserProfileToList(up);
					}
				}
				if(accessProfile.getAuthOkDataUserProfile() != null){
					for(UserProfile up : accessProfile.getAuthOkDataUserProfile()){
						this.addUserProfileToList(up);
					}
				}
				if (accessProfile.getGuestUserProfile() != null) {
					this.addUserProfileToList(accessProfile.getGuestUserProfile());
				}
			}
		}
		
		/** UserProfile from Network Firewall */
		if(hiveAp.getConfigTemplate().getFwPolicy() != null && hiveAp.getConfigTemplate().getFwPolicy().getRules() != null){
			for(FirewallPolicyRule rule : hiveAp.getConfigTemplate().getFwPolicy().getRules()){
				if(rule != null && !rule.isDisableRule() && rule.getSourceUp() != null){
					this.addUserProfileToList(rule.getSourceUp());
				}
			}
		}
		
		/** interface eth0 */
		if(hiveAp.getEthConfigType() == HiveAp.USE_ETHERNET_BOTH && 
				hiveAp.getEth0().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL &&
				hiveAp.getEth0UserProfile() != null){
			this.addUserProfileToList(hiveAp.getEth0UserProfile());
		}
		
		/** interface eth1 */
		if(hiveAp.getEthConfigType() == HiveAp.USE_ETHERNET_BOTH && 
				hiveAp.getEth1().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL &&
				hiveAp.getEth1UserProfile() != null){
			this.addUserProfileToList(hiveAp.getEth1UserProfile());
		}
		
		/** interface agg0 */
		if(hiveAp.getEthConfigType() == HiveAp.USE_ETHERNET_AGG0 && 
				hiveAp.getAgg0().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL &&
				hiveAp.getAgg0UserProfile() != null){
			this.addUserProfileToList(hiveAp.getAgg0UserProfile());
		}
		
		/** interface red0 */
		if(hiveAp.getEthConfigType() == HiveAp.USE_ETHERNET_RED0 && 
				hiveAp.getRed0().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL &&
				hiveAp.getRed0UserProfile() != null){
			this.addUserProfileToList(hiveAp.getRed0UserProfile());
		}
		
		/** interface eth0|eth1 cwp */
		if(hiveAp.isEthCwpEnableEthCwp() || hiveAp.isEthCwpEnableMacAuth()){
			
			if(hiveAp.getEthCwpDefaultAuthUserProfile() != null){
				this.addUserProfileToList(hiveAp.getEthCwpDefaultAuthUserProfile());
			}
			
			if(hiveAp.getEthCwpDefaultRegUserProfile() != null){
				this.addUserProfileToList(hiveAp.getEthCwpDefaultRegUserProfile());
			}
			
			if(hiveAp.getEthCwpRadiusUserProfiles() != null){
				for(UserProfile up : hiveAp.getEthCwpRadiusUserProfiles()){
					this.addUserProfileToList(up);
				}
			}
		}
		
		/** user profile from device policy rules must at last */
		if(hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR100){
			loadAllUserProfileFromDevicePolicyRule();
		}

		for(Long upId : priorUserProfile){
			userProfileList.add(userProfileMap.get(upId));
			userProfileMap.remove(upId);
		}
		if(!userProfileMap.isEmpty()){
			Long[] keyArray = userProfileMap.keySet().toArray(new Long[userProfileMap.size()]);
			Arrays.sort(keyArray);
			for (Long key : keyArray) {
				userProfileList.add(userProfileMap.get(key));
			}
		}
		
		List<UserProfileImpl> userProfileListTemp = new ArrayList<UserProfileImpl>();
		for(int i=userProfileList.size()-1; i>=0; i--){
			userProfileListTemp.add(userProfileList.get(i));
		}
		userProfileList.clear();
		userProfileList.addAll(userProfileListTemp);
		for(UserProfileImpl upImpl : userProfileList){
			if(upImpl.getUserProfile().getId() < 0){
				upImpl.getUserProfile().setAttributeValue(this.getFreeUserProfileAttribute(userProfileListTemp));
			}
		}
	}

	private void loadHiveProfile() {
		hiveProfile = new HiveProfileImpl(hiveAp);
	}

	private void loadSecurityProfile() throws CreateXMLException {
		securityProfile = new SecurityProfileImpl(hiveAp);
	}

	private void loadAAAProfile() {
		aaaProfileImpl = new AAAProfileImpl(hiveAp);
	}

	//Added by HeHui to prevent build fail for temp
	private void loadDnsProfileImpl() throws CreateXMLException {
		dnsProfileImpl = new DnsProfileImpl(hiveAp);
	}

	private void loadAdminProfile() {
		adminProfileImpl = new AdminProfileImpl(hiveAp);
	}

	private void loadAmrpProfile() {
		amrpProfileImpl = new AmrpProfileImpl(hiveAp);
	}

	private void loadInterfaceProfile(boolean view) throws Exception {
		if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
			if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
				interfaceProfileImpl = new InterfaceAP100Impl(hiveAp);
			}else{
				interfaceProfileImpl = new InterfaceProfileImpl(hiveAp);
			}
		}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY){
			interfaceProfileImpl = new InterfaceCVGImpl(hiveAp, view);
		}else if(hiveAp.getDeviceInfo().getIntegerValue(DeviceInfo.SPT_ETHERNET_COUNTS) >=24){
			interfaceProfileImpl = new InterfaceProfileSwitchImpl(hiveAp, view);
		}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER){
			interfaceProfileImpl = new InterfaceBRImpl(hiveAp, view);
		}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_BR){
			interfaceProfileImpl = new InterfaceBRAndCVGImpl(hiveAp, view);
		}
		
	}

	private void loadIpPolicyProfileList() throws CreateXMLException {
		ipPolicyProfileImplList = new ArrayList<IpPolicyProfileImpl>();
		
		boolean isSupportL7Service = this.isSupportL7Service(hiveAp);
		int maxAppId = ApplicationUtil.getMaxSupportedAppCode(hiveAp);

		for (UserProfileImpl userProfileImpl : userProfileList) {
			IpPolicy ipPolicyFrom = userProfileImpl.getUserProfile()
					.getIpPolicyFrom();
			IpPolicy ipPolicyTo = userProfileImpl.getUserProfile()
					.getIpPolicyTo();
			
			//if not support L7 service filter all policy rules that use L3 service.
			if(!isSupportL7Service){
				if(ipPolicyFrom != null && ipPolicyFrom.getRules() != null){
					Iterator<IpPolicyRule> fromRuleIterator = ipPolicyFrom.getRules().iterator();
					while(fromRuleIterator.hasNext()){
						IpPolicyRule rule = fromRuleIterator.next();
						if (rule.getServiceType() == IpPolicyRule.RULE_NETWORKSERVICE_TYPE) { //may be networkservice or system application
							if(rule.getNetworkService() != null && 
									rule.getNetworkService().getServiceType() == NetworkService.SERVICE_TYPE_L7){
								fromRuleIterator.remove();
							}
						}
						if (rule.getServiceType() == IpPolicyRule.RULE_CUSTOMSERVICE_TYPE) { //custom application
							fromRuleIterator.remove();
						}
						
					}
				}
				
				if(ipPolicyTo != null && ipPolicyTo.getRules() != null){
					Iterator<IpPolicyRule> toRuleIterator = ipPolicyTo.getRules().iterator();
					while(toRuleIterator.hasNext()){
						IpPolicyRule rule = toRuleIterator.next();
						if (rule.getServiceType() == IpPolicyRule.RULE_NETWORKSERVICE_TYPE) { //may be networkservice or system application
							if(rule.getNetworkService() != null && 
									rule.getNetworkService().getServiceType() == NetworkService.SERVICE_TYPE_L7){
								toRuleIterator.remove();
							}
						}
						if (rule.getServiceType() == IpPolicyRule.RULE_CUSTOMSERVICE_TYPE) { //custom application
							toRuleIterator.remove();
						}
						
					}
				}
			}
			
			//filter not supported app-id
			if(ipPolicyFrom != null && ipPolicyFrom.getRules() != null){
				Iterator<IpPolicyRule> fromRuleIterator = ipPolicyFrom.getRules().iterator();
				while(fromRuleIterator.hasNext()){
					IpPolicyRule rule = fromRuleIterator.next();
					if(rule.getNetworkService() != null && 
							rule.getNetworkService().getServiceType() == NetworkService.SERVICE_TYPE_L7){
						if (rule.getNetworkService().getAppId() >= ApplicationUtil.getMinCustomAppCode()) {
							continue;
						}
						if (rule.getNetworkService().getAppId() > maxAppId) {
							fromRuleIterator.remove();
						}
					}
				}
			}
			
			if(ipPolicyTo != null && ipPolicyTo.getRules() != null){
				Iterator<IpPolicyRule> toRuleIterator = ipPolicyTo.getRules().iterator();
				while(toRuleIterator.hasNext()){
					IpPolicyRule rule = toRuleIterator.next();
					if(rule.getNetworkService() != null && 
							rule.getNetworkService().getServiceType() == NetworkService.SERVICE_TYPE_L7){
						if (rule.getNetworkService().getAppId() >= ApplicationUtil.getMinCustomAppCode()) {
							continue;
						}
						if (rule.getNetworkService().getAppId() > maxAppId) {
							toRuleIterator.remove();
						}
					}
				}
			}
			
			if(hiveAp.getVpnMark() == HiveAp.VPN_MARK_CLIENT && (userProfileImpl.getUserProfile().getTunnelTraffic() == UserProfile.VPN_TUNNEL_TRAFFIC_NOT_LOCAL || 
					userProfileImpl.getUserProfile().getTunnelTraffic() == UserProfile.VPN_TUNNEL_TRAFFIC_NOT_LOCAL_INTERNET)){
				ipPolicyFrom = this.cloneIpPolicyForVPN(ipPolicyFrom, userProfileImpl.getUserProfile().getTunnelTraffic());
				userProfileImpl.getUserProfile().setIpPolicyFrom(ipPolicyFrom);
				
				ipPolicyTo = this.cloneIpPolicyForVPN(ipPolicyTo, userProfileImpl.getUserProfile().getTunnelTraffic());
				userProfileImpl.getUserProfile().setIpPolicyTo(ipPolicyTo);
			}
			boolean isFoundFrom = false;
			boolean isFoundTo = false;

			if (ipPolicyFrom != null) {
				for (IpPolicyProfileImpl ipPolicyImpl : ipPolicyProfileImplList) {
					if (ipPolicyImpl.getIpPolicyName().equals(
							ipPolicyFrom.getPolicyName())) {
						isFoundFrom = true;
					}
				}

				if (!isFoundFrom) {
					ipPolicyProfileImplList.add(new IpPolicyProfileImpl(hiveAp,
							ipPolicyFrom));
				}
			}

			if (ipPolicyTo != null) {
				for (IpPolicyProfileImpl ipPolicyImpl : ipPolicyProfileImplList) {
					if (ipPolicyImpl.getIpPolicyName().equals(
							ipPolicyTo.getPolicyName())) {
						isFoundTo = true;
					}
				}

				if (!isFoundTo) {
					ipPolicyProfileImplList.add(new IpPolicyProfileImpl(hiveAp,
							ipPolicyTo));
				}
			}
		}
		
		if(ipPolicyProfileImplList.size() > MAX_IP_POLICY){
			String[] errParams = {
					String.valueOf(ipPolicyProfileImplList.size()),
					String.valueOf(MAX_IP_POLICY)};
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.ippolicy.maxAllow", errParams);
			throw new CreateXMLException(errMsg);
		}
	}

	private void loadIpProfile() {
		if(hiveAp.getDeviceInfo().isSptEthernetMore_24()) {
			ipProfileImpl = new IpProfileSwitchImpl(hiveAp, this);
		} 
		else {
			if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
				ipProfileImpl = new IpProfileImpl(hiveAp);
			}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY){
				ipProfileImpl =  new IpProfileCVGImpl(hiveAp);
			}else if(hiveAp.isBranchRouter()){
				ipProfileImpl =  new IpProfileBRImpl(hiveAp, this);
			}else{
				ipProfileImpl = new IpProfileImpl(hiveAp);
			}
		}
	}

	private void loadLogingProfile() {
		logingProfileImpl = new LogingProfileImpl(hiveAp);
	}

	private void loadMacPolicyProfileImplList() {
		macPolicyProfileImplList = new ArrayList<MacPolicyProfileImpl>();

		for (UserProfileImpl userProfileImpl : userProfileList) {
			MacPolicy macPolicyFrom, macPolicyTo;
			boolean isFoundFrom = false, isFoundTo = false;
			macPolicyFrom = userProfileImpl.getUserProfile().getMacPolicyFrom();
			macPolicyTo = userProfileImpl.getUserProfile().getMacPolicyTo();

			if (macPolicyFrom != null) {
				for (MacPolicyProfileImpl macPolicyImpl : macPolicyProfileImplList) {
					if (macPolicyImpl.getMacPolicyName().equals(
							macPolicyFrom.getPolicyName())) {
						isFoundFrom = true;
					}
				}

				if (!isFoundFrom) {
					macPolicyProfileImplList.add(new MacPolicyProfileImpl(
							macPolicyFrom, hiveAp));
				}
			}

			if (macPolicyTo != null) {
				for (MacPolicyProfileImpl macPolicyImpl : macPolicyProfileImplList) {
					if (macPolicyImpl.getMacPolicyName().equals(
							macPolicyTo.getPolicyName())) {
						isFoundTo = true;
					}
				}

				if (!isFoundTo) {
					macPolicyProfileImplList.add(new MacPolicyProfileImpl(
							macPolicyTo, hiveAp));
				}
			}
		}
	}

	private void loadMobilityPolicyProfileImplList() {
		mobilityPolicyProfileImplList = new ArrayList<MobilityPolicyProfileImpl>();

		for (UserProfileImpl userProfileImpl : userProfileList) {
			TunnelSetting tunnelSet;
			boolean isFoundPolicy = false;
			tunnelSet = userProfileImpl.getUserProfile().getTunnelSetting();

			if (tunnelSet != null) {
				for (MobilityPolicyProfileImpl mobilityImpl : mobilityPolicyProfileImplList) {
					if (mobilityImpl.getMobilityPolicyName().equals(
							tunnelSet.getTunnelName())) {
						isFoundPolicy = true;
					}
				}

				if (!isFoundPolicy) {
					mobilityPolicyProfileImplList
							.add(new MobilityPolicyProfileImpl(tunnelSet,
									hiveAp));
				}
			}
		}
	}

	private void loadMobilityThresholdProfile() {
		mobilityThresholdProfileImpl = new MobilityThresholdProfileImpl(hiveAp);
	}

	private void loadNtpProfile() throws CreateXMLException {
		ntpProfileImpl = new NtpProfileImpl(hiveAp);
	}

	private void loadRadiProfileImplList() {
		radiProfileImpl = new RadioProfileImpl(hiveAp);
	}

	private void loadRoamingProfileImpl() {
		roamingProfileImpl = new RoamingProfileImpl(hiveAp);
	}

	private void loadRouteProfileImplList() {
		routeProfileImplList = new ArrayList<RouteProfileImpl>();

		for (HiveApStaticRoute route : hiveAp.getStaticRoutes()) {
			routeProfileImplList.add(new RouteProfileImpl(hiveAp, route));
		}
	}

	private void loadSnmpProfileImpl() throws CreateXMLException {
		snmpProfileImpl = new SnmpProfileImpl(hiveAp);
	}

	private void loadClockProfileImpl() {
		clockProfileImpl = new ClockProfileImpl(hiveAp);
	}

	private void loadQosProfileImpl() {
//		for (ConfigTemplateQos qosTemp : hiveAp.getConfigTemplate()
//				.getQosPolicies().values()) {
//			String userProfileName = qosTemp.getUserProfile().getUserProfileName();
//			if(userProfileName != null){
//				for(UserProfileImpl userImpl : userProfileList){
//					if(userProfileName.equals(userImpl.getUserProfileName())){
//						qosTemp.setUserProfile(userImpl.getUserProfile());
//					}
//				}
//			}
//		}
		List<UserProfile> allUserProfileList = new ArrayList<UserProfile>();
		for(UserProfileImpl userImpl : userProfileList){
			allUserProfileList.add(userImpl.getUserProfile());
		}
		qosProfileImpl = new QosProfileImpl(hiveAp, allUserProfileList);
	}

	private void loadServiceProfileList() throws CreateXMLException {
		List<NetworkService> serviceList = new ArrayList<NetworkService>();
		List<CustomApplication> customAppServiceList = new ArrayList<CustomApplication>();
		// service from ip-policy
		for (IpPolicyProfileImpl ipPolicyImpl : ipPolicyProfileImplList) {
			serviceList.addAll(ipPolicyImpl.getServiceList());
			customAppServiceList.addAll(ipPolicyImpl.getCustomAppServiceList());
		}
		
		//service from Network Firewall Policy
		if(this.hiveAp.getConfigTemplate().getFwPolicy() != null){
			for(FirewallPolicyRule rulesItem : hiveAp.getConfigTemplate().getFwPolicy().getRules() ){
				if(rulesItem.getNetworkService() != null && !rulesItem.isDisableRule()){
					serviceList.add(rulesItem.getNetworkService());
				}
			}
		}
		
		// service from qos-map
		serviceList.addAll(qosProfileImpl.getServiceList());
		customAppServiceList.addAll(qosProfileImpl.getCustomAppServiceList());
		
		// service from teacher view
		if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.5.3.0") >= 0 && 
				hiveAp.getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_TEACHER_VIEW)){
			String query = "select enableTeacher from " + HMServicesSettings.class.getSimpleName() + " bo where bo.owner.id = "+hiveAp.getOwner().getId();
			List<?> hmSettingList = MgrUtil.getQueryEntity().executeQuery(query, 1);
			boolean isEnableTv = false;
			if(hmSettingList != null && hmSettingList.size() > 0){
				isEnableTv = (Boolean)hmSettingList.get(0);
			}
			if(isEnableTv){
//				if(this.hiveAp.getConfigTemplate().isEnableOSDURL()){
//					NetworkService serTv = QueryUtil.findBoByAttribute(NetworkService.class, "serviceName", "TeacherView-HTTP");
//					serviceList.add(serTv);
//				}
				if(this.hiveAp.getConfigTemplate().isEnableTVService()){
					serviceList.addAll(this.hiveAp.getConfigTemplate().getTvNetworkService());
				}
			}
		}

		if (serviceProfileList == null) {
			serviceProfileList = new ArrayList<ServiceProfileImpl>();
		}

		boolean isFound;

		for (NetworkService serviceObj : serviceList) {
			isFound = false;

			for (ServiceProfileImpl serviceImpl : serviceProfileList) {
				if (serviceObj != null
						&& serviceObj.getServiceName().equals(
								serviceImpl.getServiceName())) {
					isFound = true;
					break;
				}
			}

			if (!isFound && serviceObj != null
					&& !serviceObj.isCliDefaultFlag()) {
				serviceProfileList.add(new ServiceProfileImpl(serviceObj,
						hiveAp));
			}
		}
		
		for (CustomApplication app : customAppServiceList) {
			isFound = false;

			for (ServiceProfileImpl serviceImpl : serviceProfileList) {
				if (app != null
						&& app.getCustomAppName().equals(
								serviceImpl.getServiceName())) {
					isFound = true;
					break;
				}
			}

			if (!isFound && app != null) {
				serviceProfileList.add(new ServiceProfileImpl(new NetworkService(app), hiveAp));
			}
		}

		if (serviceProfileList.size() > ServiceProfileInt.MAX_SERVER) {
			String[] errParams = {
					String.valueOf(serviceProfileList.size()),
					String.valueOf(ServiceProfileInt.MAX_SERVER)};
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.maxNetWorkServer", errParams);
			log.error("loadServiceProfileList", errMsg);
			throw new CreateXMLException(errMsg);
		}
	}

	private void loadResetButtonImpl() {
		resetButtonImpl = new ResetButtonProfileImpl(hiveAp);
	}

	private void loadForwardingEngineImpl() {
		forwardingEngineImpl = new ForwardingEngineImpl(hiveAp);
	}

	private void loadAlgProfileImpl() {
		boolean isEnableOSDetection = !devicePolicyList.isEmpty();
		algProfileImpl = new AlgProfileImpl(hiveAp, isEnableOSDetection);
	}
	
	private void loadPseProfileImpl() {
		if (hiveAp.getDeviceInfo().isSptEthernetMore_24()) {
			pseProfileImpl = new PseProfileSwitchImpl(hiveAp);
		} else {
			pseProfileImpl = new PseProfileBRImpl(hiveAp);
		}
		
	}
	
	private void loadMacTableProfileImpl() {
		macTableProfileImpl = new MacTableProfileBRImpl(hiveAp);
	}

	private void loadLocationProfileImpl() throws CreateXMLException {
		locationProfileImpl = new LocationProfileImpl(hiveAp);
	}

	private void loadCapwapProfileImpl() {
		capwapProfileImpl = new CapwapProfileImpl(hiveAp);
	}

	private void loadHivemanagerProfileImpl() {
		hivemanagerProfileImpl = new HivemanagerProfileImpl(hiveAp);
	}

	private void loadHostNameProfileImpl() {
		hostnameProfileImpl = new HostnameProfileImpl(hiveAp);
	}

	private void loadConsoleProfileImp() {
		consoleProfileImp = new ConsoleProfileImpl(hiveAp);
	}

	private void loadSystemProfileImpl() {
		systemProfileImpl = new SystemProfileImpl(hiveAp);
	}

	private void loadTrackProfileImpl() {
		trackProfileImpl = new ArrayList<TrackProfileInt>();
		if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY){
			if(this.hiveAp.getVpnIpTrack() != null){
				this.hiveAp.getVpnIpTrack().setWanTesting(true);
				trackProfileImpl.add(new TrackProfileImpl(this.hiveAp.getVpnIpTrack()));
//			}else if(this.hiveAp.getConfigTemplate().getRouterIpTrack() != null){
//				this.hiveAp.getConfigTemplate().getRouterIpTrack().setWanTesting(true);
//				trackProfileImpl.add(new TrackProfileImpl(this.hiveAp.getConfigTemplate().getRouterIpTrack()));
			}
		}else if (hiveAp.isBranchRouter() && NmsUtil.compareSoftwareVersion("6.0.2.0", hiveAp.getSoftVer()) > 0){
			MgmtServiceIPTrack ipTrack = hiveAp.getConfigTemplate().getPrimaryIpTrack();
			
			if (ipTrack == null) {
				ipTrack = hiveAp.getConfigTemplate().getBackup1IpTrack();
				if (ipTrack == null)
					ipTrack = hiveAp.getConfigTemplate().getBackup2IpTrack();
			}
			if (ipTrack != null) {
				ipTrack.setWanTesting(true);
				trackProfileImpl.add(new TrackProfileImpl(ipTrack));
			}
		}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
			for (MgmtServiceIPTrack ipTrackObj : hiveAp.getConfigTemplate().getIpTracks()) {
				trackProfileImpl.add(new TrackProfileImpl(ipTrackObj));
			}
		}
	}
	
	private void loadTrackWanProfileImpl() {
		trackWanProfileImpl = new ArrayList<TrackProfileInt>();
		if (hiveAp.isBranchRouter()){
			if (hiveAp.getConfigTemplate().getPrimaryIpTrack() != null ) {
				DeviceInterface pInf=hiveAp.getOrderWanInterface(0);
				if(pInf==null){
					return;
				}
				TrackWanProfileImpl track = new TrackWanProfileImpl(hiveAp.getConfigTemplate().getPrimaryIpTrack());
				track.addInterfaceType(pInf.getDeviceIfType());
				trackWanProfileImpl.add(track);
				
			}
			if (hiveAp.getConfigTemplate().getBackup1IpTrack() != null) {
				DeviceInterface b1Inf=hiveAp.getOrderWanInterface(1);
				if(b1Inf==null){
					return;
				}
				TrackWanProfileImpl track = new TrackWanProfileImpl(hiveAp.getConfigTemplate().getBackup1IpTrack());
				track.addInterfaceType(b1Inf.getDeviceIfType());
				trackWanProfileImpl.add(track);
			}
			if (hiveAp.getConfigTemplate().getBackup2IpTrack() != null) {
				DeviceInterface b2Inf=hiveAp.getOrderWanInterface(2);
				if(b2Inf==null){
					return;
				}
				TrackWanProfileImpl track = new TrackWanProfileImpl(hiveAp.getConfigTemplate().getBackup2IpTrack());
				track.addInterfaceType(b2Inf.getDeviceIfType());
				trackWanProfileImpl.add(track);
			}
		    if((trackWanProfileImpl != null) && (trackWanProfileImpl.size() > 1)){
		    	for(int i = (trackWanProfileImpl.size()-1);i > 0;i--){
		    		TrackWanProfileImpl last = (TrackWanProfileImpl)trackWanProfileImpl.get(i);
		    			for(int j = 0;j < i;j++){
		    				if(last.getTrackName().equals(trackWanProfileImpl.get(j).getTrackName())){
		    					if((NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.0.2.0") == 0)){
		    						trackWanProfileImpl.remove(i);
		    					}else{
		    						((TrackWanProfileImpl)(trackWanProfileImpl.get(j))).addInterfaceType(last.getInterfaceType(0));
		    						trackWanProfileImpl.remove(i);
		    					}
		    					break;
		    				}
		    			}
		    		}
		    	}
		}
	}

	private void loadCacProfileImpl() {
		cacProfileImpl = new CacProfileImpl(hiveAp);
	}

	private void loadAccessConsoleImpl(){
		accessConsoleImpl = new AccessConsoleImpl(hiveAp);
	}

	private void loadLocalUserImpl() {
		//User Group from radius server
		List<LocalUser> users = new ArrayList<LocalUser>();
		Map<String, LocalUserGroup> userGroupMap = new HashMap<String, LocalUserGroup>();
		if(hiveAp.getRadiusServerProfile() != null){
			if(hiveAp.getRadiusServerProfile().getLdapOuUserProfiles() != null){
				for(LdapServerOuUserProfile ldapServerOu : hiveAp.getRadiusServerProfile().getLdapOuUserProfiles()){
					if(!userGroupMap.containsKey(ldapServerOu.getLocalUserGroup().getGroupName()) && 
							ldapServerOu.getLocalUserGroup().getUserType() != LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
						userGroupMap.put(ldapServerOu.getLocalUserGroup().getGroupName(), ldapServerOu.getLocalUserGroup());
					}
				}
			}
			
			if(hiveAp.getRadiusServerProfile().getLocalUserGroup() != null){
				for(LocalUserGroup localGroup : hiveAp.getRadiusServerProfile().getLocalUserGroup()){
					if(!userGroupMap.containsKey(localGroup.getGroupName()) && 
							localGroup.getUserType() != LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
						userGroupMap.put(localGroup.getGroupName(), localGroup);
					}
				}
			}
		}
		
		//User Group from ssid
		for(ConfigTemplateSsid ssidMapping : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
			SsidProfile ssidProfile = ssidMapping.getSsidProfile();
			if(ssidProfile != null && 
					ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_PSK && 
					ssidProfile.getLocalUserGroups() != null){
				for(LocalUserGroup userGroup : ssidProfile.getLocalUserGroups()){
					if(userGroup.getUserType() != LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK && 
							!userGroupMap.containsKey(userGroup.getGroupName())){
						userGroupMap.put(userGroup.getGroupName(), userGroup);
					}
				}
			}
		}
		
		//local all manual user through user group
		if(userGroupMap.size() > 0){
			users.addAll(
					MgrUtil.getQueryEntity().executeQuery(LocalUser.class,
							null, 
							new FilterParams("localUserGroup", userGroupMap.values()), 
							hiveAp.getOwner().getId())
			);
		}
		
		for(LocalUser localUser : users){
			userList.add(new UserImpl(localUser));
		}
	}

	private void loadLldpProfileImpl() {
		if(hiveAp.getDeviceInfo().isSptEthernetMore_24()) {
			lldpProfileImpl = new LldpProfileSwitchImpl(hiveAp);
		} else {
			lldpProfileImpl = new LldpProfileImpl(hiveAp);
		}
		
	}
	
	private void loadUserGroupList() throws CreateXMLException{
		Map<String, UserGroupProfileImpl> userGroupMap = new HashMap<String, UserGroupProfileImpl>();
		
		//UserGroup from ssid
		for(ConfigTemplateSsid configSsid : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
			if(configSsid.getSsidProfile() != null && configSsid.getSsidProfile().getLocalUserGroups() != null){
				for(LocalUserGroup userGroup : configSsid.getSsidProfile().getLocalUserGroups()){
					if(!userGroupMap.containsKey(userGroup.getGroupName())){
						userGroupMap.put(userGroup.getGroupName(), new UserGroupProfileImpl(userGroup, this.hiveAp));
					}
				}
			}
			
			//enable Assign user profile based on RADIUS attribute
			if(configSsid.getSsidProfile() != null && configSsid.getSsidProfile().getRadiusUserGroups() != null){
				for(LocalUserGroup userGroup : configSsid.getSsidProfile().getRadiusUserGroups()){
					if(!userGroupMap.containsKey(userGroup.getGroupName())){
						userGroupMap.put(userGroup.getGroupName(), new UserGroupProfileImpl(userGroup, this.hiveAp));
					}
				}
			}
		}
		
		//UserGroup from lanprofile when enable Assign user profile based on RADIUS attribute
		//TODO Port Template Profiles
		if(hiveAp.getPortGroup() != null && hiveAp.getPortGroup().getBasicProfiles() != null){
			for(PortBasicProfile baseProfile : hiveAp.getPortGroup().getBasicProfiles()){
				PortAccessProfile accessProfile = baseProfile.getAccessProfile();
				if(accessProfile != null && accessProfile.getRadiusUserGroups() != null){
					for(LocalUserGroup userGroup : accessProfile.getRadiusUserGroups()){
						if(!userGroupMap.containsKey(userGroup.getGroupName())){
							userGroupMap.put(userGroup.getGroupName(), new UserGroupProfileImpl(userGroup, this.hiveAp));
						}
					}
				}
			}
		}
		
		if(hiveAp.getRadiusServerProfile() != null){
			//UserGroup from radius server ldap server
			if(hiveAp.getRadiusServerProfile().getLocalUserGroup() != null){
				for(LocalUserGroup userGroup : hiveAp.getRadiusServerProfile().getLocalUserGroup()){
					if(!userGroupMap.containsKey(userGroup.getGroupName())){
						userGroupMap.put(userGroup.getGroupName(), new UserGroupProfileImpl(userGroup, this.hiveAp));
					}
				}
			}
			//UserGroup from radius server local database
			if(hiveAp.getRadiusServerProfile().getLdapOuUserProfiles() != null){
				for(LdapServerOuUserProfile ldapServerOu : hiveAp.getRadiusServerProfile().getLdapOuUserProfiles()){
					if(!userGroupMap.containsKey(ldapServerOu.getLocalUserGroup().getGroupName())){
						userGroupMap.put(ldapServerOu.getLocalUserGroup().getGroupName(), new UserGroupProfileImpl(ldapServerOu.getLocalUserGroup(), this.hiveAp));
					}
				}
			}
			
			//UserGroup from radius server Library SIP Policy
			if(hiveAp.getRadiusServerProfile().isLibrarySipCheck()){
				if(hiveAp.getRadiusServerProfile().getSipPolicy() != null){
					if(hiveAp.getRadiusServerProfile().getSipPolicy().getDefUserGroup() != null){
						LocalUserGroup defUserGroup = hiveAp.getRadiusServerProfile().getSipPolicy().getDefUserGroup();
						userGroupMap.put(defUserGroup.getGroupName(), new UserGroupProfileImpl(defUserGroup, this.hiveAp));
					}
					if(hiveAp.getRadiusServerProfile().getSipPolicy().getRules() != null){
						for(RadiusLibrarySipRule sipRule : hiveAp.getRadiusServerProfile().getSipPolicy().getRules()){
							if(sipRule.getUserGroup() != null){
								userGroupMap.put(sipRule.getUserGroup().getGroupName(), new UserGroupProfileImpl(sipRule.getUserGroup(), this.hiveAp));
							}
						}
					}
				}
			}
		}
		userGroupList.addAll(userGroupMap.values());
		
		if (userGroupList.size() > 512) {
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.MaxUserGroup",
					new String[] { String.valueOf(512) });
			log.error("loadUserGroupList", errMsg);
			throw new CreateXMLException(errMsg);
		}
	}
	
	private void loadVpnImpl() throws Exception{
		generateVpnUsers(this.hiveAp);
		if(this.hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
			vpnImpl = new VPNProfileImpl(this.hiveAp, this.view);
		}else if(this.hiveAp.isVpnGateway()){
			vpnImpl = new VPNProfileCVGImpl(this.hiveAp, view);
		}else if(this.hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER){
			vpnImpl = new VPNProfileBRImpl(this.hiveAp, this, view);
		}else{
			vpnImpl = new VPNProfileBRImpl(this.hiveAp, this, view);
		}
	}
	
	public List<Integer> getAllDeviceVlans(HiveAp hiveAp) throws CreateXMLException{
		List<Integer> allVlans = new ArrayList<Integer>();
		
		boolean[] vlanArgs = new boolean[CLICommonFunc.MAX_MERGE_RANGE];
		List<Vlan> vlanObjList = new ArrayList<Vlan>();
		
		//mgt0 vlan
		Vlan mgt0Vlan = hiveAp.getConfigTemplate().getVlan();
		if(mgt0Vlan != null){
			vlanObjList.add(mgt0Vlan);
		}
		
		//mgt0 native vlan
		Vlan mgt0NativeVlan = hiveAp.getConfigTemplate().getVlanNative();
		if(mgt0NativeVlan != null){
			vlanObjList.add(mgt0NativeVlan);
		}
		
		//vlan from network policy Port Group.
		PortGroupProfile portGroup = hiveAp.getPortGroup();
		if(portGroup != null && portGroup.getBasicProfiles() != null){
			
			//vlan from monitor session
			if(portGroup.getMonitorProfiles() != null){
				for(PortMonitorProfile monitor : portGroup.getMonitorProfiles()){
					if(monitor.isEnableVlans() && monitor.getIngressVlan() != null && !"".equals(monitor.getIngressVlan())){
						CLICommonFunc.mergeRange(vlanArgs, monitor.getIngressVlan());
					}
				}
			}
			
			//vlan from access profile
			for(PortBasicProfile profile : portGroup.getBasicProfiles()){
				PortAccessProfile accessProfile = profile.getAccessProfile();
				if(accessProfile == null){
					continue;
				}
				
				//vlan from trunk mode native vlan.
				if(accessProfile.getNativeVlan() != null){
					vlanObjList.add(accessProfile.getNativeVlan());
				}
				
				//vlan from phone/data voice vlan.
				if(accessProfile.getVoiceVlan() != null){
					vlanObjList.add(accessProfile.getVoiceVlan());
				}
				
				//vlan from phone/data data vlan.
				if(accessProfile.getDataVlan() != null){
					vlanObjList.add(accessProfile.getDataVlan());
				}
				
				//vlan from trunk allowed vlan.
				if(accessProfile.getAllowedVlan() != null && !"all".equalsIgnoreCase(accessProfile.getAllowedVlan())){
					CLICommonFunc.mergeRange(vlanArgs, accessProfile.getAllowedVlan());
				}
				
				//vlan from cwp pass-through vlan
				if(accessProfile.getCwp() != null && accessProfile.getCwp().getVlan() != null){
					vlanObjList.add(accessProfile.getCwp().getVlan());
				}
				
			}
		}
		
		//For switch get vlan
		if(hiveAp.getDeviceInfo().isSptEthernetMore_24()){
			
			//vlan from igmp
			if (hiveAp.isOverrideIgmpSnooping()) {
				if (hiveAp.getIgmpPolicys() != null) {
					for (IgmpPolicy policy : hiveAp.getIgmpPolicys()) {
						vlanArgs[policy.getVlanId()] = true;
					}
				}
				
				if (hiveAp.getMulticastGroups() != null) {
					for (MulticastGroup group : hiveAp.getMulticastGroups()) {
						vlanArgs[group.getVlanId()] = true;
					}
				}
			}
			
			//vlan from forwarding DB
			if (hiveAp.getForwardingDB() != null) {
				
				//MAC Address Table, Disable MAC Learning for Part VLANs 
				if(hiveAp.getForwardingDB().isDisableMacLearnForPartVlans() && hiveAp.getForwardingDB().getVlans() != null){
					CLICommonFunc.mergeRange(vlanArgs, hiveAp.getForwardingDB().getVlans());
				}
				
				for (MacAddressLearningEntry entry : hiveAp.getForwardingDB().getMacAddressEntries()) {
					if (entry != null) {
						vlanArgs[entry.getVlanId()] = true;
					}
				}
			}
		}
		
		//vlan from UserProfile
		for(UserProfileImpl upImpl : userProfileList){
			UserProfile userPf = upImpl.getUserProfile();
			int vlan = InterfaceBRImpl.getUserProfileVlan(userPf, hiveAp);
			vlanArgs[vlan] = true;
		}
		
		
		//merge vlan to arrays
		for(Vlan vlan : vlanObjList){
			int vlanId = CLICommonFunc.getVlan(vlan, hiveAp).getVlanId();
			vlanArgs[vlanId] = true;
		}
		
		//merge to result list
		for(int index=0; index<vlanArgs.length; index++){
			if(vlanArgs[index]){
				allVlans.add(index);
			}
		}
		
		return allVlans;
	}
	
	private void loadVlanImplList() throws Exception {
		this.vlanImplList = new ArrayList<VlanProfileInt>();
		
		if(hiveAp.getDeviceInfo().isSptEthernetMore_24()){
			
			List<Integer> allVlans = getAllDeviceVlans(this.hiveAp);
			//create VlanProfileImpl vlan 1 no need create
			for(Integer vlan : allVlans){
				if(vlan > 1){
					vlanImplList.add(new VlanProfileImpl(vlan));
				}
			}

			//vlan max 255
			if(vlanImplList.size() > 254){
				String errMsg = NmsUtil.getUserMessage(
						"error.be.config.create.moreVlan", new String[]{"255"});
				throw new CreateXMLException(errMsg);
			}
			
			//check vlan not in reserve vlan
			int reserveStart = VlanReserveImpl.getVlanReserve(this.hiveAp);
			int reserveEnd = reserveStart + 127;
			String reserveVlans = "";
			for(VlanProfileInt vlanObj : vlanImplList){
				if(vlanObj.getVlanId() >= reserveStart && vlanObj.getVlanId() <= reserveEnd){
					if("".equals(reserveVlans)){
						reserveVlans += vlanObj.getVlanId();
					}else{
						reserveVlans += ","+vlanObj.getVlanId();
					}
				}
			}
			reserveVlans = CLICommonFunc.mergeRange(reserveVlans);
			if(reserveVlans != null && !"".equals(reserveVlans)){
//				String reserveRange = reserveStart + " - " + reserveEnd;
				String errMsg = NmsUtil.getUserMessage(
						"error.be.config.create.inReserveVlan", new String[]{reserveVlans});
				throw new CreateXMLException(errMsg);
			}
		}
	}
	
	private void generateVpnUsers(HiveAp hiveAp) {
		if(hiveAp == null){
			return;
		}
		VpnService vpnService = hiveAp.getConfigTemplate().getVpnService();
		if(vpnService == null || vpnService.getIpsecVpnType() != VpnService.IPSEC_VPN_LAYER_3){
			return;
		}
		List<ConfigTemplate> netPolicyList = MgrUtil.getQueryEntity().executeQuery(ConfigTemplate.class, null, 
				new FilterParams("vpnService", vpnService), hiveAp.getOwner().getId(), new ConfigLazyQueryBo());
		if(netPolicyList == null || netPolicyList.isEmpty()){
			return;
		}
		
		int count = 0;
		int userCount = vpnService.getVpnCredentials().size();
		for(ConfigTemplate netPolicy : netPolicyList){
			Vlan mgt0Vlan = netPolicy.getVlan();
			VpnNetwork mgtNetwork = netPolicy.getNetworkByVlan(mgt0Vlan);
			for(VpnNetworkSub ipSub : mgtNetwork.getSubItems()){
				count += ipSub.getIpBranches();
			}
		}
		if(HiveAp.HIVEAP_MODEL_VPN_GATEWAY == hiveAp.getHiveApModel()){
			count = (count > VpnService.MAX_IP_POOL_SIZE_VPN_CVG_DEVICE) ? VpnService.MAX_IP_POOL_SIZE_VPN_CVG_DEVICE : count;
		}else{
			count = (count > VpnService.MAX_IP_POOL_SIZE) ? VpnService.MAX_IP_POOL_SIZE : count;
		}
		
		if(userCount == count){
			return;
		}
		
		synchronized(synLock) {
			//recount xauth counts, for multi-thread.
			vpnService = MgrUtil.getQueryEntity().findBoById(VpnService.class, vpnService.getId(), new ConfigLazyQueryBo());
			hiveAp.getConfigTemplate().setVpnService(vpnService);
			userCount = vpnService.getVpnCredentials().size();
			if(userCount == count){
				return;
			}else if(userCount < count){
				int addCount = count - userCount;
				Set<VpnServiceCredential> addSet = generate(addCount,hiveAp.getHiveApModel());
				vpnService.getVpnCredentials().addAll(addSet);
			}else if(userCount > count){
				int removeCount = userCount - count;
				List<VpnServiceCredential> rmList = new ArrayList<VpnServiceCredential>();
				for(VpnServiceCredential user : vpnService.getVpnCredentials()){
					if(removeCount == 0){
						break;
					}
					if(user.isFree()){
						rmList.add(user);
						removeCount--;
					}
				}
				if(removeCount > 0){
					for(VpnServiceCredential user : vpnService.getVpnCredentials()){
						if(removeCount == 0){
							break;
						}
						rmList.add(user);
						removeCount--;
					}
				}
				vpnService.getVpnCredentials().removeAll(rmList);
			}
			try{
				MgrUtil.getQueryEntity().updateBo(vpnService);
			}catch(Exception ex){
				log.error("Update VpnService when upload configuration: ", ex);
			}
		}
	}
	
	private void loadAutoUserGroupLis(){
		Map<String, PskAutoUserGroupImpl> userGroupMap = new HashMap<String, PskAutoUserGroupImpl>();
		
		//UserGroup from ssid
		for(ConfigTemplateSsid configSsid : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
			if(configSsid.getSsidProfile() != null && configSsid.getSsidProfile().getLocalUserGroups() != null){
				for(LocalUserGroup userGroup : configSsid.getSsidProfile().getLocalUserGroups()){
					if(!userGroupMap.containsKey(userGroup.getGroupName())){
						userGroupMap.put(userGroup.getGroupName(), new PskAutoUserGroupImpl(userGroup, this.hiveAp));
					}
				}
			}
		}
		
		if(hiveAp.getRadiusServerProfile() != null){
			//UserGroup from radius server ldap server
			if(hiveAp.getRadiusServerProfile().getLocalUserGroup() != null){
				for(LocalUserGroup userGroup : hiveAp.getRadiusServerProfile().getLocalUserGroup()){
					if(!userGroupMap.containsKey(userGroup.getGroupName())){
						userGroupMap.put(userGroup.getGroupName(), new PskAutoUserGroupImpl(userGroup, this.hiveAp));
					}
				}
			}
			//UserGroup from radius server local database
			if(hiveAp.getRadiusServerProfile().getLdapOuUserProfiles() != null){
				for(LdapServerOuUserProfile ldapServerOu : hiveAp.getRadiusServerProfile().getLdapOuUserProfiles()){
					if(!userGroupMap.containsKey(ldapServerOu.getLocalUserGroup().getGroupName())){
						userGroupMap.put(ldapServerOu.getLocalUserGroup().getGroupName(), new PskAutoUserGroupImpl(ldapServerOu.getLocalUserGroup(), this.hiveAp));
					}
				}
			}
		}
		autoUserGroupList.addAll(userGroupMap.values());
	}
	
	
	
//	private void loadSsidUserGroupBind(){
//		for(ConfigTemplateSsid configSsid : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
//			if(configSsid.getSsidProfile() !=  null){
//				SsidProfile ssidProfile = configSsid.getSsidProfile();
//				ssidUserGroupBindList.add(new SsidBindUserGroupImpl(ssidProfile));
//			}
//		}
//		
//	}
	

	private void checkMaxUser() throws CreateXMLException{
		Map<String, LocalUserGroup> userGroupMap = new HashMap<String, LocalUserGroup>();
		
		//UserGroup from ssid
		for(ConfigTemplateSsid configSsid : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
			if(configSsid.getSsidProfile() != null && configSsid.getSsidProfile().getLocalUserGroups() != null){
				for(LocalUserGroup userGroup : configSsid.getSsidProfile().getLocalUserGroups()){
					if(!userGroupMap.containsKey(userGroup.getGroupName())){
						userGroupMap.put(userGroup.getGroupName(), userGroup);
					}
				}
			}
		}
		
		if(hiveAp.getRadiusServerProfile() != null){
			//UserGroup from radius server ldap server
			if(hiveAp.getRadiusServerProfile().getLocalUserGroup() != null){
				for(LocalUserGroup userGroup : hiveAp.getRadiusServerProfile().getLocalUserGroup()){
					if(!userGroupMap.containsKey(userGroup.getGroupName())){
						userGroupMap.put(userGroup.getGroupName(), userGroup);
					}
				}
			}
			//UserGroup from radius server local database
			if(hiveAp.getRadiusServerProfile().getLdapOuUserProfiles() != null){
				for(LdapServerOuUserProfile ldapServerOu : hiveAp.getRadiusServerProfile().getLdapOuUserProfiles()){
					if(!userGroupMap.containsKey(ldapServerOu.getLocalUserGroup().getGroupName())){
						userGroupMap.put(ldapServerOu.getLocalUserGroup().getGroupName(), ldapServerOu.getLocalUserGroup());
					}
				}
			}
		}
		
		if (userGroupMap.size() > 512) {
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.MaxUserGroup",
					new String[] { String.valueOf(512) });
			log.error("loadUserGroupList", errMsg);
			throw new CreateXMLException(errMsg);
		}
		
		long count = 0;
		if(!userGroupMap.values().isEmpty()){
			String sqlStr = "select count(id) from " + LocalUser.class.getSimpleName();
			String whereStr = "localUserGroup in (:s1) AND revoked = :s2";
			List<?> counts = MgrUtil.getQueryEntity().executeQuery(sqlStr, null, 
					new FilterParams(whereStr, new Object[]{userGroupMap.values(), false}), this.hiveAp.getOwner().getId());
			
			if(counts != null && !counts.isEmpty()){
				count = (Long)counts.get(0);
			}else{
				count = 0;
			}
		}
		
		if (count > 9999) {
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.MaxUser", new String[] { String
							.valueOf(9999) });
			log.error("loadUserGroupList", errMsg);
			throw new CreateXMLException(errMsg);
		}
	}
	
	private void loadAirScreenProfile(){
		Map<String, AirScreenRule> asRuleMap = new HashMap<String, AirScreenRule>();
		/** ariscreen rule from user profile */
		for (UserProfileImpl userProfileImpl : userProfileList){
			AirScreenRuleGroup asGroup = userProfileImpl.getUserProfile().getAsRuleGroup();
			if(asGroup == null){
				continue;
			}
			asGroup = MgrUtil.getQueryEntity().findBoById(AirScreenRuleGroup.class, userProfileImpl.getUserProfile().getAsRuleGroup().getId(), new ConfigLazyQueryBo());
			userProfileImpl.getUserProfile().setAsRuleGroup(asGroup);
			
			if(userProfileImpl.getUserProfile().getAsRuleGroup().getRules() != null){
				for(AirScreenRule asRule : asGroup.getRules()){
					if(!asRuleMap.containsKey(asRule.getProfileName())){
						asRule = MgrUtil.getQueryEntity().findBoById(AirScreenRule.class, asRule.getId(), new ConfigLazyQueryBo());
						asRuleMap.put(asRule.getProfileName(), asRule);
					}
				}
			}
		}
		
		/** airscreen rule from ssid profile */
		for(ConfigTemplateSsid cfgSsid : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
			SsidProfile ssidObj = cfgSsid.getSsidProfile();
			if(ssidObj == null || ssidObj.getAsRuleGroup() == null){
				continue;
			}
			AirScreenRuleGroup ruleGroup = MgrUtil.getQueryEntity().findBoById(AirScreenRuleGroup.class, ssidObj.getAsRuleGroup().getId(), new ConfigLazyQueryBo());
			ssidObj.setAsRuleGroup(ruleGroup);
			
			if(ssidObj.getAsRuleGroup().getRules() != null){
				for(AirScreenRule asRule : ssidObj.getAsRuleGroup().getRules()){
					if(!asRuleMap.containsKey(asRule.getProfileName())){
						asRule = MgrUtil.getQueryEntity().findBoById(AirScreenRule.class, asRule.getId(), new ConfigLazyQueryBo());
						asRuleMap.put(asRule.getProfileName(), asRule);
					}
				}
			}
		}
		
		airScreenImpl = new AirScreenProfileImpl(new ArrayList<AirScreenRule>(asRuleMap.values()), this.hiveAp);
	}
	
	private void loadPerformanceSentinelImpl(){
		this.performanceImpl = new PerformanceSentinelImpl(this.hiveAp);
	}
	
	private void loadReportProfileImpl(){
		this.reportImpl = new ReportProfileImpl(this.hiveAp);
	}
	
	private void loadDevicePolicyList() throws CreateXMLException{
		List<DevicePolicyRule> ruleList = new ArrayList<DevicePolicyRule>();
		List<UserProfile> upList = new ArrayList<UserProfile>();
		for(UserProfileImpl upImpl : userProfileList){
			UserProfile upObj = upImpl.getUserProfile();
			if(upObj.isEnableAssign() && upObj.getAssignRules() != null){
				upList.add(upObj);
				for(DevicePolicyRule rule: upObj.getAssignRules()){
					rule.setDescription(upObj.getUserProfileName());
					ruleList.add(rule);
				}
			}
		}
		if(!ruleList.isEmpty()){
			devicePolicyList.add(new MobileDevicePolicyImpl(ruleList, upList, this.hiveAp));
		}
	}
	
	private void loadDeviceGroupList(){
		Set<DeviceGroupImpl.DeviceGroup> deviceGroupSet = new HashSet<DeviceGroupImpl.DeviceGroup>();
		for(MobileDevicePolicyImpl userPolicyImpl : devicePolicyList){
			if(userPolicyImpl.getDevicePolicyRules() != null){
				for(DevicePolicyRule rule: userPolicyImpl.getDevicePolicyRules()){
					DeviceGroupImpl.DeviceGroup deviceGroup = new DeviceGroupImpl.DeviceGroup();
					deviceGroup.setMacObj(rule.getMacObj());
					deviceGroup.setOsObj(rule.getOsObj());
					deviceGroup.setDomainObj(rule.getDomObj());
					deviceGroup.setDeviceOwnership(rule.getOwnership());
					deviceGroupSet.add(deviceGroup);
				}
			}
		}
		for(DeviceGroupImpl.DeviceGroup deviceGroup : deviceGroupSet){
			DeviceGroupImpl dgImpl = new DeviceGroupImpl(deviceGroup,hiveAp);
			deviceGroupList.add(dgImpl);
		}
	}
	
	public boolean isSupportMDM() {
		if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"6.0.0.0") >= 0){
		if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100 || (hiveAp.isCVGAppliance() && hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200))
		{
			return false;
		}
		}
		else if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"5.1.1.0") >= 0){
			if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200
					|| hiveAp.isCVGAppliance()){
				return false;
			}
			
		}
		return true;
	}
	
	private void loadOsObjectList(){
		Map<String, OsObjectImpl> osObjectMap = new HashMap<String, OsObjectImpl>();
		for(DeviceGroupImpl dgImpl : deviceGroupList){
			OsObject osObj = dgImpl.getDeviceGroup().getOsObj();
			if(osObj != null && !osObjectMap.containsKey(osObj.getOsName())){
				OsObjectImpl osImpl = new OsObjectImpl(osObj,hiveAp);
				osObjectMap.put(osObj.getOsName(), osImpl);
			}
		}
		
		for(SsidProfileImpl ssidImpl : ssidProfileList){
			if (!isSupportMDM())
				continue;
			
			if (ssidImpl.getSsidProfile().isEnableMDM()){
				ConfigTemplateMdm configMdm=ssidImpl.getSsidProfile().getConfigmdmId();
				if(NmsUtil.compareSoftwareVersion("6.0.0.0",hiveAp.getSoftVer())>0 && configMdm.getMdmType() != ConfigTemplateMdm.MDM_ENROLL_TYPE_JSS ){
					continue;
				}
				
				addSupportedOsObject(osObjectMap, configMdm);
			}
					
			if (ssidImpl.getSsidProfile().isEnableAerohiveMdm()) {
				if (!osObjectMap.containsKey(SecurityObjectProfileImpl.MDM_OBJECT_IOS)){
					addOsObjectIntoList(osObjectMap, SecurityObjectProfileImpl.MDM_OBJECT_IOS);
				}
				if (!osObjectMap.containsKey(SecurityObjectProfileImpl.MDM_OBJECT_MACOS)){
					addOsObjectIntoList(osObjectMap, SecurityObjectProfileImpl.MDM_OBJECT_MACOS);
				}
				if (!osObjectMap.containsKey(AcmOsObject.ANDROID.getName())){
					addOsObjectIntoList(osObjectMap, AcmOsObject.ANDROID.getName());
				}
				if (!osObjectMap.containsKey(AcmOsObject.CHROME.getName())){
					addOsObjectIntoList(osObjectMap, AcmOsObject.CHROME.getName());
				}
			}
		}
		
		//os object for lan port
		if(hiveAp.getPortGroup() != null && hiveAp.getPortGroup().getBasicProfiles() != null){
			for(PortBasicProfile baseProfile : hiveAp.getPortGroup().getBasicProfiles()){
				if(baseProfile.getAccessProfile().isEnableMDM()  && isSupportMDM()){
					
					ConfigTemplateMdm configMdm=baseProfile.getAccessProfile().getConfigtempleMdm();
					if(NmsUtil.compareSoftwareVersion("6.0.0.0",hiveAp.getSoftVer())>0 && configMdm.getMdmType() !=0 ){
						continue;
					}
					
					addSupportedOsObject(osObjectMap, configMdm);					
				}
				
			}
		}
		
		//for AP's Ethernet port
		if(hiveAp.getDeviceType()==HiveAp.Device_TYPE_HIVEAP && hiveAp.isEnableMDM()){
			ConfigTemplateMdm configMdm=hiveAp.getConfigTemplateMdm();
			
			addSupportedOsObject(osObjectMap, configMdm);
		}
		
		osObjectList.addAll(osObjectMap.values());
	}
	
	private void addSupportedOsObject(Map<String, OsObjectImpl> osObjectMap, ConfigTemplateMdm configMdm) {
		if(configMdm!=null){
			if(configMdm.isEnableAppleOs() && !osObjectMap.containsKey(SecurityObjectProfileImpl.MDM_OBJECT_IOS)){
				addOsObjectIntoList(osObjectMap, SecurityObjectProfileImpl.MDM_OBJECT_IOS);
			}
			if(configMdm.isEnableMacOs() && !osObjectMap.containsKey(SecurityObjectProfileImpl.MDM_OBJECT_MACOS)){
				addOsObjectIntoList(osObjectMap, SecurityObjectProfileImpl.MDM_OBJECT_MACOS);
			}
			if(configMdm.isEnableSymbianOs()  && !osObjectMap.containsKey(SecurityObjectProfileImpl.MDM_OBJECT_SYMBIAN)){
				addOsObjectIntoList(osObjectMap, SecurityObjectProfileImpl.MDM_OBJECT_SYMBIAN);
			}
			if(configMdm.isEnableBlackberryOs() && !osObjectMap.containsKey(SecurityObjectProfileImpl.MDM_OBJECT_BLACKBERRY)){
				addOsObjectIntoList(osObjectMap, SecurityObjectProfileImpl.MDM_OBJECT_BLACKBERRY);
			}
			if(configMdm.isEnableAndroidOs() && !osObjectMap.containsKey(SecurityObjectProfileImpl.MDM_OBJECT_ANDROID)){
				addOsObjectIntoList(osObjectMap, SecurityObjectProfileImpl.MDM_OBJECT_ANDROID);
			}
			if(configMdm.isEnableWindowsphoneOs() && !osObjectMap.containsKey(SecurityObjectProfileImpl.MDM_OBJECT_WINDOWSPHONE)){
				addOsObjectIntoList(osObjectMap, SecurityObjectProfileImpl.MDM_OBJECT_WINDOWSPHONE);
			}
		}

	}
	
	private void addOsObjectIntoList(Map<String, OsObjectImpl> map, String osName) {
		OsObject osObj = MgrUtil.getQueryEntity().findBoByAttribute(OsObject.class, "osName", osName, new ConfigLazyQueryBo());
		if(osObj != null){
			OsObjectImpl osImpl = new OsObjectImpl(osObj,hiveAp);
			map.put(osName, osImpl);
		}
	}
	
	private void loadOsVersionList(){
		Map<String, OsVersionImpl> osVersionMap = new HashMap<String, OsVersionImpl>();
		for(DeviceGroupImpl dgImpl : deviceGroupList){
			OsObject osObj = dgImpl.getDeviceGroup().getOsObj();
			if(osObj != null && osObj.getDhcpItems().size() > 0){
				for(OsObjectVersion osVersion : osObj.getDhcpItems()){
					if(osVersion != null && osVersion.getOption55() != null && !osVersion.getOption55().trim().equals("")
						&& !osVersionMap.containsKey(osVersion.getOption55())){
						OsVersionImpl osVersionImpl = new OsVersionImpl(osVersion);
						osVersionMap.put(osVersion.getOption55(),osVersionImpl);
					}
				}
			}
		}
		osVersionList.addAll(osVersionMap.values());
	}
	
	private void loadOsDetection(){
		osDetectionImpl = new OsDetectionImpl(hiveAp);
	}
	
	private void loadBonjourGateway(){
		bonjourGatewayImpl = new BonjourGatewayImpl(hiveAp);
	}
	
	private void loadDesignatedServerImpl(){
//		DesignatedServerImpl = new DesignatedServerImpl(hiveAp);
	}
	
	private void loadApplicationProfileImpl(boolean view) throws CreateXMLException {
		applicationProfileImpl = new ApplicationProfileImpl(hiveAp, view);
	}
	
	private void loadAdminConnectionAlarmImpl() {
		adminConnectionAlarmImpl = new AdminConnectionAlarmImpl(hiveAp);
	}
	
	private void loadPortChannelImpl() {
		portChannelImpl = new PortChannelImpl(hiveAp);
	}
	
	private void loadMacAddressTableImpl() {
		macAddressTableImpl = new MacAddressTableImpl(hiveAp);
	}
	
	private void loadCdpProfileImpl() {
		cdpProfileImpl = new CdpProfileImpl(hiveAp);
	}
	
	private void loadClientModeImpl(){
		clientModeImpl = new ClientModeImpl(hiveAp);
	}
	
	private void loadMonitorProfileImpl() {
		monitorImpl = new MonitorProfileImpl(hiveAp);
	}
	
	private void loadVlanGroupProfileImpl(){
		BonjourGatewaySettings bonjouGateway = hiveAp.getConfigTemplate().getBonjourGw();
		if(bonjouGateway != null && !bonjouGateway.getRules().isEmpty()){
			Map<String, VlanGroupProfileImpl> vlanGroupMap = new HashMap<String, VlanGroupProfileImpl>();
			for(BonjourFilterRule rule:bonjouGateway.getRules()){
				if(rule.getFromVlanGroup() != null){
					VlanGroupProfileImpl vlanGroupProfileImpl = new VlanGroupProfileImpl(rule.getFromVlanGroup(),this.hiveAp);
					vlanGroupMap.put(rule.getFromVlanGroup().getVlanGroupName(),vlanGroupProfileImpl);
				}
				if(rule.getToVlanGroup() != null){
					VlanGroupProfileImpl vlanGroupProfileImpl = new VlanGroupProfileImpl(rule.getToVlanGroup(),this.hiveAp);
					vlanGroupMap.put(rule.getToVlanGroup().getVlanGroupName(),vlanGroupProfileImpl);
				}
			}
			vlanGroupList.addAll(vlanGroupMap.values());
		}
	}
	
	private void loadMacObjectList(){
		Map<String, MacObjectImpl> macObjectMap = new HashMap<String, MacObjectImpl>();
		for(DeviceGroupImpl dgImpl : deviceGroupList){
			MacOrOui macObj = dgImpl.getDeviceGroup().getMacObj();
			if(macObj != null && !macObjectMap.containsKey(macObj.getMacOrOuiName())){
				MacObjectImpl macImpl = new MacObjectImpl(macObj, this.hiveAp);
				macObjectMap.put(macObj.getMacOrOuiName(), macImpl);
			}
		}
		macObjectList.addAll(macObjectMap.values());
	}
	
	private void loadDomainObjectList(){
		Map<String, DomainObjectImpl> domainObjectMap = new HashMap<String, DomainObjectImpl>();
		for(DeviceGroupImpl dgImpl : deviceGroupList){
			DomainObject domainObject = dgImpl.getDeviceGroup().getDomainObj();
			if(domainObject != null && !domainObjectMap.containsKey(domainObject.getObjName())){
				DomainObjectImpl domainImpl = new DomainObjectImpl(domainObject);
				domainObjectMap.put(domainObject.getObjName(), domainImpl);
			}
		}
		domainObjectList.addAll(domainObjectMap.values());
	}
	
	private void loadVlanReserveImpl(){
		this.vlanReserveImpl = new VlanReserveImpl(this.hiveAp);
	}
	
	private void loadCollectionImpl(){
		this.collectionImpl = new DataCollectionImpl();
	}
	
	private void loadConfigProfileImpl(){
		this.configProfileImpl = new ConfigProfileImpl(hiveAp);
	}
	
	private void loadNetworkFirewallImpl() throws CreateXMLException{
		this.networkFirewallImpl = new NetworkFirewallImpl(this.hiveAp);
	}
	
	private void loadWebProxyImpl(){
		this.webProxyImpl = new WebSecurityProxyImpl(this.hiveAp, this.interfaceProfileImpl);
	}
	
	private void loadUsbmodemImpl(){
		this.usbmodemImpl = new UsbmodemImpl(this.hiveAp);
	}
	
	private void loadRoutingImpl() throws CreateXMLException{
		this.routingImpl = new RoutingProfileImpl(this.hiveAp, this);
	}
	
	private void loadLanProfileImpl() throws CreateXMLException{
		this.LanProfileImpl = new LanPortImpl(this.hiveAp);
	}
	
	private void loadSpanningTreeImpl(){
		this.spanningTreeImpl = new SpanningTreeImpl(this.hiveAp);
	}
	
	private void loadStromControlImpl(){
		this.stormImpl = new StromControlImpl(this.hiveAp);
	}
	
	private void loadKddrImpl(){
		this.kddrImpl = new KddrImpl(this.hiveAp);
	}
	
	private IpPolicy cloneIpPolicyForWpa(SsidProfile cloneSsid, HiveAp hiveAp){
		IpPolicy policyPpsk = new IpPolicy();
		policyPpsk.setAutoGenerate(true);
		
		List<IpPolicyRule> ruleList = new ArrayList<IpPolicyRule>();
		String policyName = null;

//		if((!cloneSsid.isBlnBrAsPpskServer() || hiveAp.isBranchRouter())){
			policyName=cloneSsid.getSsid().trim();
			policyName=policyName.substring(0,policyName.length() > 23 ? 23 : policyName.length());
			policyPpsk.setPolicyName(policyName + IpPolicyProfileImpl.IP_POLICY_FOR_WPA_AUTO);
			
			
			short rule_index = 0;
//			String ppskServer = cloneSsid.getPpskServer().getCfgIpAddress();
//			if(ppskServer == null || "".equals(ppskServer)){
//				ppskServer = BR_AS_PPSK_SERVER_IP;
//			}
			
//     		IpPolicyRule rule = new IpPolicyRule();
//			rule.setRuleId(++rule_index);
//			rule.setDesctinationIp(CLICommonFunc.getGlobalIpAddress(ppskServer, "255.255.255.255"));
//			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
//			ruleList.add(rule);
			
     		IpPolicyRule rule = new IpPolicyRule();
			rule.setRuleId(++rule_index);
			rule.setDesctinationIp(CLICommonFunc.getGlobalIpAddress("1.1.0.0", "255.255.0.0"));
			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			ruleList.add(rule);
			
			rule = new IpPolicyRule();
			rule.setRuleId(++rule_index);
			rule.setSourceIp(CLICommonFunc.getGlobalIpAddress("1.1.0.0", "255.255.0.0"));
			rule.setDesctinationIp(CLICommonFunc.getGlobalHost("local-subnet"));
			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			ruleList.add(rule);
			
			rule = new IpPolicyRule();
			rule.setRuleId(++rule_index);
			rule.setSourceIp(CLICommonFunc.getGlobalIpAddress("0.0.0.0","0.0.0.0"));
			rule.setDesctinationIp(CLICommonFunc.getGlobalIpAddress("0.0.0.0","0.0.0.0"));
			NetworkService networkService = (NetworkService)MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "servicename", "DHCP-Server");
			rule.setNetworkService(networkService);
			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			ruleList.add(rule);
			
			rule = new IpPolicyRule();
			rule.setRuleId(++rule_index);
			rule.setSourceIp(CLICommonFunc.getGlobalIpAddress("0.0.0.0","0.0.0.0"));
			rule.setDesctinationIp(CLICommonFunc.getGlobalIpAddress("0.0.0.0","0.0.0.0"));
			networkService = (NetworkService)MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "servicename", "DNS");
			rule.setNetworkService(networkService);
			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			ruleList.add(rule);
//			rule = new IpPolicyRule();
//			rule.setRuleId(++rule_index);
//			rule.setSourceIp(CLICommonFunc.getGlobalIpAddress(ppskServer, "255.255.255.255"));
//			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
//			ruleList.add(rule);
			
//		}else{
//			policyName=IpPolicyProfileImpl.IP_POLICY_FOR_PPSK_AUTO_BR;
//			policyPpsk.setPolicyName(policyName);
//		}
		
		policyPpsk.setRules(ruleList);
		
		return policyPpsk;
	}
	
	private IpPolicy cloneIpPolicyForPPSK(SsidProfile cloneSsid, HiveAp hiveAp){
		IpPolicy policyPpsk = new IpPolicy();
		policyPpsk.setAutoGenerate(true);
		
		List<IpPolicyRule> ruleList = new ArrayList<IpPolicyRule>();
		String policyName = null;

		if((cloneSsid.isBlnBrAsPpskServer() && hiveAp.isBranchRouter()) || cloneSsid.getPpskServer() != null ||
				(cloneSsid.getParentPpskSsid() != null && cloneSsid.getParentPpskSsid().isEnabledIDM())
			){
			policyName=cloneSsid.getSsid().trim();
			policyName=policyName.substring(0,policyName.length() > 23 ? 23 : policyName.length());
			policyPpsk.setPolicyName(policyName + IpPolicyProfileImpl.IP_POLICY_FOR_PPSK_AUTO);
			
			
			short rule_index = 0;
			String ppskServer;
			if(cloneSsid.isEnabledIDM()) {
				ppskServer = hiveAp.getDownloadInfo().getIdmRadSecConfig().getIdmGatewayServer();
			}else if(cloneSsid.isBlnBrAsPpskServer()){
				ppskServer = BR_AS_PPSK_SERVER_IP;
			}else if(cloneSsid.getPpskServer() != null){
				ppskServer = cloneSsid.getPpskServer().getCfgIpAddress();
			}else{
				return null;
			}
			boolean isIpAddr =CLICommonFunc.isIpAddress(ppskServer);
			
			IpPolicyRule rule = new IpPolicyRule();
			rule.setRuleId(++rule_index);
			if(isIpAddr){
				rule.setDesctinationIp(CLICommonFunc.getGlobalIpAddress(ppskServer, "255.255.255.255"));
			}else{
				rule.setDesctinationIp(CLICommonFunc.getGlobalHost(ppskServer));
			}
			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			ruleList.add(rule);
			
			rule = new IpPolicyRule();
			rule.setRuleId(++rule_index);
			rule.setDesctinationIp(CLICommonFunc.getGlobalIpAddress("1.1.0.0", "255.255.0.0"));
			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			ruleList.add(rule);
			
			rule = new IpPolicyRule();
			rule.setRuleId(++rule_index);
			rule.setSourceIp(CLICommonFunc.getGlobalIpAddress("1.1.0.0", "255.255.0.0"));
			rule.setDesctinationIp(CLICommonFunc.getGlobalHost("local-subnet"));
			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			ruleList.add(rule);
			
			rule = new IpPolicyRule();
			rule.setRuleId(++rule_index);
			rule.setSourceIp(CLICommonFunc.getGlobalIpAddress(ppskServer, "255.255.255.255"));
			rule.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			ruleList.add(rule);
			
		}else{
			policyName=IpPolicyProfileImpl.IP_POLICY_FOR_PPSK_AUTO_BR;
			policyPpsk.setPolicyName(policyName);
		}
		
		policyPpsk.setRules(ruleList);
		
		return policyPpsk;
	}
	
	private UserProfile cloneUserProfileForPpsk(SsidProfile cloneSsid, HiveAp hiveAp, Vlan vlanObj){
		UserProfile upPpsk = MgrUtil.getQueryEntity().findBoByAttribute(UserProfile.class, "userProfileName", "default-profile", new ConfigLazyQueryBo());
		
		upPpsk.setId(index_for_ppsk--);
		String policyName=cloneSsid.getSsid();
		policyName=policyName.substring(0,policyName.length() > 23 ? 23 : policyName.length());
		upPpsk.setUserProfileName(policyName + IpPolicyProfileImpl.IP_POLICY_FOR_PPSK_AUTO);
		upPpsk.setDefaultFlag(false);
		upPpsk.setVlan(vlanObj);
		
		if(cloneSsid.getParentPpskSsid() != null && cloneSsid.getParentPpskSsid().isEnabledIDM()){
			return upPpsk;
		}
		
		if(cloneSsid.getPpskServer() != null || cloneSsid.isBlnBrAsPpskServer()){
			upPpsk.setIpPolicyFrom(cloneIpPolicyForPPSK(cloneSsid, hiveAp));
			upPpsk.setIpPolicyTo(cloneIpPolicyForPPSK(cloneSsid, hiveAp));
			upPpsk.setActionIp(IpPolicyRule.POLICY_ACTION_DENY);
		}
		return upPpsk;
	}
	
	private short getFreeUserProfileAttribute(List<UserProfileImpl> userProfileList){
		if(userGroupList == null){
			return -1;
		}
		
		try{
			boolean[] attrMerge = new boolean[UserProfileImpl.MAX_ATTRIBUTE + 2];
			for(UserProfileImpl userImpl : userProfileList){
				UserProfile upObj = userImpl.getUserProfile();
				String attrStr = String.valueOf(upObj.getAttributeValue());
				if(upObj.getUserProfileAttribute() != null){
					attrStr += ",";
					attrStr += CLICommonFunc.getUserProfileAttr(upObj.getUserProfileAttribute(), this.hiveAp).getAttributeValue();
				}
				String[] attrArr;
				if(attrStr.indexOf(",") > 0){
					attrArr = attrStr.split(",");
				}else{
					attrArr = new String[1];
					attrArr[0] = attrStr;
				}
				for (String attr : attrArr) {
					if (attr.indexOf("-") > 0) {
						int frome, to;
						frome = Integer.valueOf(attr.substring(0, attr.indexOf("-")));
						to = Integer.valueOf(attr.substring(attr.indexOf("-") + 1));
						for (int j = frome; j <= to; j++) {
							attrMerge[j] = true;
						}
					} else {
						try {
							attrMerge[Integer.valueOf(attr)] = true;
						} catch (Exception ex) {
						}
					}
				}
			}
			for(short index = 4095; index >=0; index--){
				if(!attrMerge[index]){
					return index;
				}
			}
		}catch(Exception ex){
			log.info("getFreeUserProfileAttribute", ex);
		}
		
		return -1;
	}
	
	private IpPolicy cloneIpPolicyForVPN(IpPolicy ipPolicy, short vpnType){
		if(ipPolicy == null){
			ipPolicy = new IpPolicy();
			ipPolicy.setPolicyName(hiveAp.getSerialNumber() + IpPolicyProfileImpl.IP_POLICY_FOR_VPN_AUTO);
			ipPolicy.setAutoGenerate(true);
		}
		
		if(ipPolicy.getRules() == null){
			ipPolicy.setRules(new ArrayList<IpPolicyRule>());
		}
		
		int rule_index = 0;
		for(IpPolicyRule denyRule : ipPolicy.getRules()){
			rule_index = Math.max(rule_index, denyRule.getRuleId());
		}
		
		IpPolicyRule rule = new IpPolicyRule();
		rule.setRuleId((short)(++rule_index));
		rule.setDesctinationIp(CLICommonFunc.getGlobalHost("local-subnet"));
		rule.setFilterAction(IpPolicyRule.POLICY_ACTION_NAT);
		ipPolicy.getRules().add(rule);
		
		if(vpnType == UserProfile.VPN_TUNNEL_TRAFFIC_NOT_LOCAL){
			IpPolicyRule rulePAll = new IpPolicyRule();
			rulePAll.setRuleId((short)(++rule_index));
			rulePAll.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			ipPolicy.getRules().add(rulePAll);
		}else if(vpnType == UserProfile.VPN_TUNNEL_TRAFFIC_NOT_LOCAL_INTERNET){
			IpPolicyRule ruleDhcp = new IpPolicyRule();
			ruleDhcp.setRuleId((short)(++rule_index));
			NetworkService dhcpService = MgrUtil.getQueryEntity().findBoByAttribute(NetworkService.class, "serviceName", "DHCP-Server");
			dhcpService.setServiceName(dhcpService.getServiceName().toLowerCase());
			ruleDhcp.setNetworkService(dhcpService);
			ruleDhcp.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			ipPolicy.getRules().add(ruleDhcp);
			
			IpPolicyRule rule1_10 = new IpPolicyRule();
			rule1_10.setRuleId((short)(++rule_index));
			rule1_10.setDesctinationIp(CLICommonFunc.getGlobalIpAddress("10.0.0.0", "255.0.0.0"));
			rule1_10.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			ipPolicy.getRules().add(rule1_10);
			
			IpPolicyRule rule1_192 = new IpPolicyRule();
			rule1_192.setRuleId((short)(++rule_index));
			rule1_192.setDesctinationIp(CLICommonFunc.getGlobalIpAddress("192.168.0.0", "255.255.0.0"));
			rule1_192.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			ipPolicy.getRules().add(rule1_192);
			
			IpPolicyRule rule1_172 = new IpPolicyRule();
			rule1_172.setRuleId((short)(++rule_index));
			rule1_172.setDesctinationIp(CLICommonFunc.getGlobalIpAddress("172.16.0.0", "255.240.0.0"));
			rule1_172.setFilterAction(IpPolicyRule.POLICY_ACTION_PERMIT);
			ipPolicy.getRules().add(rule1_172);
			
			IpPolicyRule rule_nat = new IpPolicyRule();
			rule_nat.setRuleId((short)(++rule_index));
			rule_nat.setFilterAction(IpPolicyRule.POLICY_ACTION_NAT);
			ipPolicy.getRules().add(rule_nat);
		}
		
		return ipPolicy;
	}
	
	private void loadAllUserProfileFromDevicePolicyRule() throws CreateXMLException{
		List<UserProfileImpl> userImplList = new ArrayList<UserProfileImpl>();
		userImplList.addAll(userProfileMap.values());
		for(UserProfileImpl userImpl : userImplList){
			UserProfile upObj = userImpl.getUserProfile();
			if(upObj.isEnableAssign()){
				for(DevicePolicyRule rule : upObj.getAssignRules()){
					loadUserProfileFromDPRule(rule.getUserProfileId());
				}
			}
		}
	}
	
	private void loadUserProfileFromDPRule(long upId) throws CreateXMLException{
		if(userProfileMap.containsKey(upId)){
			return;
		}
		UserProfile upObj = new UserProfile();
		upObj.setId(upId);
		upObj = this.addUserProfileToList(upObj);
		if(upObj.isEnableAssign()){
			for(DevicePolicyRule rule : upObj.getAssignRules()){
				loadUserProfileFromDPRule(rule.getUserProfileId());
			}
		}
	}
	
	private SsidProfile loadPpskSelfReg(SsidProfile ssidProfileObj, HiveAp hiveAp){
		SsidProfile cloneSsid = null;
		if(ssidProfileObj != null && ssidProfileObj.isEnablePpskSelfReg() && !ssidProfileObj.isEnableSingleSsid()){ 
			cloneSsid = MgrUtil.getQueryEntity().findBoByAttribute(SsidProfile.class, "ssidName", "ssid0", new ConfigLazyQueryBo());
			cloneSsid.setSsidName(ssidProfileObj.getPpskOpenSsid());
			cloneSsid.setSsid(ssidProfileObj.getPpskOpenSsid());
			cloneSsid.setRadioMode(ssidProfileObj.getRadioMode());
			cloneSsid.setEnablePpskSelfReg(ssidProfileObj.isEnablePpskSelfReg());
			cloneSsid.setPpskServer(ssidProfileObj.getPpskServer());
			cloneSsid.setPpskOpenSsid(ssidProfileObj.getSsid());
			cloneSsid.setCwp(ssidProfileObj.getPpskECwp());
			cloneSsid.setPpskECwp(ssidProfileObj.getPpskECwp());
			cloneSsid.setRadiusAssignmentPpsk(ssidProfileObj.getRadiusAssignmentPpsk());
			cloneSsid.setBlnBrAsPpskServer(ssidProfileObj.isBlnBrAsPpskServer());
			//open ssid no need clone mac auth enable from ppsk ssid, that from wulitian.
//			cloneSsid.setMacAuthEnabled(ssidProfileObj.getMacAuthEnabled());
			cloneSsid.setPersonPskRadiusAuth(ssidProfileObj.getPersonPskRadiusAuth());
			//fix bug 28904
			cloneSsid.setUserProfileDefault(ssidProfileObj.getUserProfileDefault());
			
			cloneSsid.setRadiusAssignment(ssidProfileObj.getRadiusAssignment());
			cloneSsid.setParentPpskSsid(ssidProfileObj);
			Vlan vlanObj = null;
			if(ssidProfileObj.getUserProfileDefault() != null && ssidProfileObj.getUserProfileDefault().getVlan() != null){
				vlanObj = ssidProfileObj.getUserProfileDefault().getVlan();
			}
			cloneSsid.setUserProfileSelfReg(this.cloneUserProfileForPpsk(cloneSsid, hiveAp, vlanObj));
			cloneSsid.setCloneFromPPSKServer(true);
			
			if (isEnableClientManagement(ssidProfileObj)) {
				cloneSsid.setWpaECwp(ssidProfileObj.getWpaECwp());
				cloneSsid.setEnableProvisionPrivate(ssidProfileObj.isEnableProvisionPrivate());			
				cloneSsid.setEnableProvisionPersonal(ssidProfileObj.isEnableProvisionPersonal());
				cloneSsid.setOnboardSsid(ssidProfileObj.getSsid());
				cloneSsid.setOwner(ssidProfileObj.getOwner());
			}
		}
		
		return cloneSsid;
	}
	
	private ConfigTemplateSsid cloneConfigTemplateSsid(ConfigTemplateSsid ssidTemp, SsidProfile ssid){
		ConfigTemplateSsid ssidClone = null;
		if(ssidTemp != null && ssid != null){
			ssidClone = new ConfigTemplateSsid();
			ssidClone.setInterfaceName(ssid.getSsidName());
			ssidClone.setSsidProfile(ssid);
		}
		return ssidClone;
	}
	
	private boolean isPpskServer(HiveAp hiveAp){
		if(hiveAp.getDeviceType()==HiveAp.Device_TYPE_HIVEAP && (hiveAp.isDhcp() || hiveAp.getId() == null)){
			return false;
		}
		for(ConfigTemplateSsid ssidTemp : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
			if(ssidTemp == null || ssidTemp.getSsidProfile() == null){
				continue;
			}
			SsidProfile ssid = ssidTemp.getSsidProfile();
			if(ssid.getAccessMode() == SsidProfile.ACCESS_MODE_PSK 
					&& (ssid.isEnablePpskSelfReg() || ssid.getSsidSecurity().isBlnMacBindingEnable())
					&& ssid.getPpskServer() != null && ssid.getPpskServer().getId().equals(hiveAp.getId())){
				return true;
			}
			if (hiveAp.isBranchRouter()
					&& ssid.getAccessMode() == SsidProfile.ACCESS_MODE_PSK
					&& (ssid.isEnablePpskSelfReg() || ssid.getSsidSecurity().isBlnMacBindingEnable())
					&& ssid.isBlnBrAsPpskServer()){
				return true;
			}
		}
		return false;
	}
	
	private boolean isSupportL7Service(HiveAp hiveAp){
		return !hiveAp.isBranchRouter() && !hiveAp.isVpnGateway() &&
				hiveAp.getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_L7_SERVICE);
	}
	
	private void prepareDeviceDataSource(boolean view) throws Exception{
		int radioCounts = hiveAp.getDeviceInfo().getIntegerValue(DeviceInfo.SPT_RADIO_COUNTS);
		int ethCounts = hiveAp.getDeviceInfo().getIntegerValue(DeviceInfo.SPT_ETHERNET_COUNTS);
		boolean isSwitch = hiveAp.getDeviceInfo().isSptEthernetMore_24();
		
		//fix bug 28097
		if(isSwitch){
			hiveAp.getConfigTemplate().setEnableEth0LimitDownloadBandwidth(false);
			hiveAp.getConfigTemplate().setEnableEth0LimitUploadBandwidth(false);
			if(hiveAp.isSwitch()){
				hiveAp.getConfigTemplate().setEnableUSBLimitDownloadBandwidth(false);
				hiveAp.getConfigTemplate().setEnableUSBLimitUploadBandwidth(false);
			}
		}
		
		//for SR2124P speed only support 10000M
		if(hiveAp.getDeviceInterfaces() != null){
			for(DeviceInterface dInf : hiveAp.getDeviceInterfaces().values()){
				dInf.setHiveApModel(hiveAp.getHiveApModel());
			}
		}
		
		if(radioCounts <= 0){		//br200, switch ppsk server ssid
			boolean isPpskServer = isPpskServer(hiveAp);
			Map<Long, ConfigTemplateSsid> ssidMap = hiveAp.getConfigTemplate().getSsidInterfaces();
			Set<Long> rmList = new HashSet<Long>();
			for(Long key : ssidMap.keySet()){
				ConfigTemplateSsid ssidTemp = ssidMap.get(key);
				SsidProfile ssidObj = ssidTemp.getSsidProfile();
				if(ssidObj == null){
					continue;
				}
				if(isPpskServer){
					if(ssidObj.getAccessMode() == SsidProfile.ACCESS_MODE_PSK && 
							(ssidObj.isEnablePpskSelfReg() || ssidObj.getSsidSecurity().isBlnMacBindingEnable()) && 
							NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "5.1.1.0") >= 0){
						continue;
					}else if(ssidObj.isCloneFromPPSKServer()){
						continue;
					}
				}
				rmList.add(key);
			}
			for(Long rmKey : rmList){
				hiveAp.getConfigTemplate().getSsidInterfaces().remove(rmKey);
			}
		}
		
		//Ethernet qos interface filter
		Iterator<Entry<Long, ConfigTemplateSsid>> templateIte = hiveAp.getConfigTemplate().getSsidInterfaces().entrySet().iterator();
		while(templateIte.hasNext()){
			Entry<Long, ConfigTemplateSsid> entryItem = templateIte.next();
			ConfigTemplateSsid tmpItem = entryItem.getValue();
			if(tmpItem == null){
				templateIte.remove();
				continue;
			}
			boolean needRemove = false;
			if("eth0".equalsIgnoreCase(tmpItem.getInterfaceName())){
				if(isSwitch || ethCounts < 1){
					needRemove = true;
				}
			}else if("eth1".equalsIgnoreCase(tmpItem.getInterfaceName()) ){
				if(isSwitch || ethCounts < 2){
					needRemove = true;
				}
			}else if("eth2".equalsIgnoreCase(tmpItem.getInterfaceName()) || 
					"eth3".equalsIgnoreCase(tmpItem.getInterfaceName()) || 
					"eth4".equalsIgnoreCase(tmpItem.getInterfaceName()) ){
				if(isSwitch || ethCounts < 5){
					needRemove = true;
				}
			}else if("red0".equalsIgnoreCase(tmpItem.getInterfaceName()) || 
					"agg0".equalsIgnoreCase(tmpItem.getInterfaceName()) ){
				if(isSwitch || ethCounts < 2 || hiveAp.getDeviceType() != HiveAp.Device_TYPE_HIVEAP){
					needRemove = true;
				}
			}
			if(needRemove){
				templateIte.remove();
			}
		}
		
		//clear invalid PortGroup
		PortGroupProfile portGroup = hiveAp.getPortGroup();
		hiveAp.getConfigTemplate().setPortProfiles(new HashSet<PortGroupProfile>());
		if(portGroup != null){
			Set<PortGroupProfile> portSet = new HashSet<>();
			portSet.add(portGroup);
			hiveAp.getConfigTemplate().setPortProfiles(portSet);
		}
		
		//clear port profiles
		if(ethCounts <= 2 && hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
			hiveAp.getConfigTemplate().getPortProfiles().clear();
		}
		
		//remove qos 2,3,4
		if(ethCounts <= 2 && hiveAp.getConfigTemplate().getSsidInterfaces() != null){
			hiveAp.getConfigTemplate().getSsidInterfaces().remove(ConfigTemplate.SSID_INTERFACES_MAPKEY_ETH2);
			hiveAp.getConfigTemplate().getSsidInterfaces().remove(ConfigTemplate.SSID_INTERFACES_MAPKEY_ETH3);
			hiveAp.getConfigTemplate().getSsidInterfaces().remove(ConfigTemplate.SSID_INTERFACES_MAPKEY_ETH4);
			if(hiveAp.getDeviceType() != HiveAp.Device_TYPE_HIVEAP){
				hiveAp.getConfigTemplate().getSsidInterfaces().remove(ConfigTemplate.SSID_INTERFACES_MAPKEY_RED0);
				hiveAp.getConfigTemplate().getSsidInterfaces().remove(ConfigTemplate.SSID_INTERFACES_MAPKEY_AGG0);
			}
		}
		
		for(ConfigTemplateSsid ssidTemp : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
			//if PPSK local user group attribute is null copy default user profile to group attribute id
			if(ssidTemp.getSsidProfile() != null && ssidTemp.getSsidProfile().getAccessMode() == SsidProfile.ACCESS_MODE_PSK){
				SsidProfile ppskSsid = ssidTemp.getSsidProfile();
				if(ppskSsid.getUserProfileDefault() != null){
					short attributeValue = ppskSsid.getUserProfileDefault().getAttributeValue();
					if(ppskSsid.getLocalUserGroups() != null){
						for(LocalUserGroup group : ppskSsid.getLocalUserGroups()){
							if(group.getUserProfileId() < 0){
								group.setUserProfileId(attributeValue);
							}
						}
					}
				}
			}
			
			//if IDM enable and use anonymous access function auto enable dynamic-auth-extension
			if(ssidTemp.getSsidProfile() != null && ssidTemp.getSsidProfile().isEnabledIDM()){
				if(ssidTemp.getSsidProfile().getRadiusAssignment() == null){
					ssidTemp.getSsidProfile().setRadiusAssignment(new RadiusAssignment());
				}
				
				if(ssidTemp.getSsidProfile().getCwp() != null && (
						ssidTemp.getSsidProfile().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA || 
						ssidTemp.getSsidProfile().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED || 
						ssidTemp.getSsidProfile().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH )){
					ssidTemp.getSsidProfile().getRadiusAssignment().setEnableExtensionRadius(true);
				}
				//PPSK/802.1X COA for IDM
				if(/** NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.3.0") >= 0 && **/
						(ssidTemp.getSsidProfile().getAccessMode() == SsidProfile.ACCESS_MODE_8021X || 
						ssidTemp.getSsidProfile().getAccessMode() == SsidProfile.ACCESS_MODE_PSK) ){
					ssidTemp.getSsidProfile().getRadiusAssignment().setEnableExtensionRadius(true);
				}
				//IDM CWP not support Chap model, if IDM enable change Chap to Pap
				if(ssidTemp.getSsidProfile().getCwp() != null 
						&& ssidTemp.getSsidProfile().getCwp().getAuthMethod() == Cwp.AUTH_METHOD_CHAP){
					ssidTemp.getSsidProfile().getCwp().setAuthMethod(Cwp.AUTH_METHOD_PAP);
				}
			}
		}
		
		//ethernet port enable CWP.
		if(hiveAp.getPortGroup() != null && hiveAp.getPortGroup().getBasicProfiles() != null){
			for(PortBasicProfile basicProfile : hiveAp.getPortGroup().getBasicProfiles()){
				if(basicProfile.getAccessProfile() == null){
					continue;
				}
				if(basicProfile.getAccessProfile().isEnabledIDM()){
					basicProfile.getAccessProfile().setRadiusAssignment(new RadiusAssignment());
					
					if(basicProfile.getAccessProfile().getCwp() != null && (
							basicProfile.getAccessProfile().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA || 
							basicProfile.getAccessProfile().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED || 
							basicProfile.getAccessProfile().getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH )){
						basicProfile.getAccessProfile().getRadiusAssignment().setEnableExtensionRadius(true);
					}
					//PPSK/802.1X COA for IDM
					if(basicProfile.getAccessProfile().isEnabled8021X()){
						basicProfile.getAccessProfile().getRadiusAssignment().setEnableExtensionRadius(true);
					}
					//IDM CWP not support Chap model, if IDM enable change Chap to Pap for wired port
					if(basicProfile.getAccessProfile().isEnabledIDM() && 
							basicProfile.getAccessProfile().getCwp() != null && 
							basicProfile.getAccessProfile().getCwp().getAuthMethod() == Cwp.AUTH_METHOD_CHAP){
						basicProfile.getAccessProfile().getCwp().setAuthMethod(Cwp.AUTH_METHOD_PAP);
					}
				}
				if(NmsUtil.compareSoftwareVersion("6.1.3.0", hiveAp.getSoftVer()) > 0 && !hiveAp.isSwitchProduct()){
					basicProfile.getAccessProfile().setEnabledSameVlan(false);
				}
			}
		}
		
		if(hiveAp.getConfigTemplate().getConfigType().isBonjourOnly()){
			hiveAp.getConfigTemplate().getSsidInterfaces().clear();
			hiveAp.getConfigTemplate().getPortProfiles().clear();
			hiveAp.getConfigTemplate().setFwPolicy(null);
			hiveAp.getConfigTemplate().setVpnService(null);
			hiveAp.getConfigTemplate().setClassifierMap(null);
			hiveAp.getConfigTemplate().setMarkerMap(null);
		}
		
		if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY){
			if(hiveAp.getCvgDPD() != null){
				if(hiveAp.getCvgDPD().getMgtNetwork() != null && hiveAp.getCvgDPD().getMgtVlan() != null){
					if(hiveAp.getConfigTemplate().getVlanNetwork() == null){
						hiveAp.getConfigTemplate().setVlanNetwork(new ArrayList<ConfigTemplateVlanNetwork>());
					}
					hiveAp.getConfigTemplate().getVlanNetwork().clear();
					ConfigTemplateVlanNetwork vlanNet = new ConfigTemplateVlanNetwork();
					vlanNet.setVlan(hiveAp.getCvgDPD().getMgtVlan());
					vlanNet.setNetworkObj(hiveAp.getCvgDPD().getMgtNetwork());
					hiveAp.getConfigTemplate().getVlanNetwork().add(vlanNet);
				}
				if(hiveAp.getCvgDPD().getDnsForCVG() != null){
					hiveAp.getConfigTemplate().setMgmtServiceDns(hiveAp.getCvgDPD().getDnsForCVG());
				}
				if(hiveAp.getCvgDPD().getNtpForCVG() != null){
					hiveAp.getConfigTemplate().setMgmtServiceTime(hiveAp.getCvgDPD().getNtpForCVG());
				}
				if(hiveAp.getCvgDPD().getMgmtServiceSyslog() != null){
					hiveAp.getConfigTemplate().setMgmtServiceSyslog(hiveAp.getCvgDPD().getMgmtServiceSyslog());
				}
				if(hiveAp.getCvgDPD().getMgmtServiceSnmp() != null){
					hiveAp.getConfigTemplate().setMgmtServiceSnmp(hiveAp.getCvgDPD().getMgmtServiceSnmp());
				}
			}
			String vpnSql = "select distinct vpn.id from "+VpnService.class.getSimpleName()+" as vpn join vpn.vpnGateWaysSetting as joined ";
			List<?> vpnIds = MgrUtil.getQueryEntity().executeQuery(vpnSql, null, new FilterParams("joined.hiveApId = :s1", new Object[]{hiveAp.getId()}));
			if(vpnIds != null && !vpnIds.isEmpty()){
				Long vpnId = (Long)vpnIds.get(0);
				VpnService vpnService = MgrUtil.getQueryEntity().findBoById(VpnService.class, vpnId, new ConfigLazyQueryBo());
				hiveAp.getConfigTemplate().setVpnService(vpnService);
			}
		}
		
		if(hiveAp.isOverWriteRadiusServer() && hiveAp.isEnabledBrAsRadiusServer() 
				&& !(hiveAp.getRadiusServerProfile()!=null || hiveAp.getRadiusProxyProfile()!=null)){
			if(hiveAp.getConfigTemplate().getRadiusServerProfile() != null && hiveAp.getRadiusServerProfile() == null){
				hiveAp.setRadiusServerProfile(hiveAp.getConfigTemplate().getRadiusServerProfile());
			}
			if(hiveAp.getConfigTemplate().getRadiusProxyProfile() != null && hiveAp.getRadiusProxyProfile() == null){
				hiveAp.setRadiusProxyProfile(hiveAp.getConfigTemplate().getRadiusProxyProfile());
			}
		}
		
//		if(hiveAp.isIDMProxy()){
//			if(hiveAp.getRadiusProxyProfile() == null){
//				RadiusProxy obj = new RadiusProxy();
//				obj.setProxyName("IDM_Proxy");
//				hiveAp.setRadiusProxyProfile(obj);
//			}
//			
//			if(hiveAp.getRadiusProxyProfile().getRadiusRealm() == null || hiveAp.getRadiusProxyProfile().getRadiusRealm().isEmpty()){
//				List<RadiusProxyRealm> radiusRealm = new ArrayList<RadiusProxyRealm>();
//				RadiusProxyRealm defRealm = new RadiusProxyRealm();
//				defRealm.setServerName(RadiusProxyRealm.DEFAULT_REALM_NAME);
//				defRealm.setUseIDM(true);
//				radiusRealm.add(defRealm);
//				hiveAp.getRadiusProxyProfile().setRadiusRealm(radiusRealm);
//			}else{
//				for(RadiusProxyRealm realm : hiveAp.getRadiusProxyProfile().getRadiusRealm()){
//					if(RadiusProxyRealm.DEFAULT_REALM_NAME.equals(realm.getServerName())){
//						realm.setUseIDM(true);
//						break;
//					}
//				}
//			}
//		}
		
//		//this is for IDM auth proxy, from version 6.1.2.0 auth proxy no need generate any CLI.
//		if(hiveAp.getDownloadInfo().isEnableIdm() && 
//				hiveAp.getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_IDM_PROXY) ){
//			String ipAddress = hiveAp.getCfgIpAddress();
//			if(ipAddress == null){
//				ipAddress = hiveAp.getIpAddress();
//			}
//			if(hiveAp.getRadiusServerProfile() != null && 
//					hiveAp.isEnableIDMAuthProxy() && 
//					ipAddress != null){
//				RadiusProxy proxy = hiveAp.getRadiusProxyProfile();
//				proxy = this.createIDMAuthProxy(ipAddress, proxy, hiveAp.getSoftVer());
//				if(hiveAp.getRadiusProxyProfile() == null){
//					hiveAp.setRadiusProxyProfile(proxy);
//				}
//			}
//		}
		
		//if device not BR or VPN model release all IP resource
		if(!hiveAp.isBranchRouter() && !hiveAp.isVpnGateway() && !view){
			CVGAndBRIpResourceManage.prepareReleaseResource(hiveAp.getOwner(), hiveAp.getMacAddress());
		}
		
		//only device type is AP support L3 service
		if(!isSupportL7Service(hiveAp) && hiveAp.getConfigTemplate().getClassifierMap() != null){
			if(hiveAp.getConfigTemplate().getClassifierMap().getNetworkServices() != null){
				Iterator<Entry<Long, QosNetworkService>> itemService = hiveAp.getConfigTemplate().getClassifierMap().getNetworkServices().entrySet().iterator();
				while(itemService.hasNext()){
					Entry<Long, QosNetworkService> entryService = itemService.next();
					QosNetworkService service = entryService.getValue();
					if(service.getNetworkService() != null && 
							service.getNetworkService().getServiceType() == NetworkService.SERVICE_TYPE_L7){
						itemService.remove();
					}
				}
			}
			hiveAp.getConfigTemplate().getClassifierMap().setCustomServices(null);
		}
		
		//filter not supported app-id
		int maxAppId = ApplicationUtil.getMaxSupportedAppCode(hiveAp);
		if(hiveAp.getConfigTemplate().getClassifierMap() != null && 
				hiveAp.getConfigTemplate().getClassifierMap().getNetworkServices() != null){
			Iterator<Entry<Long, QosNetworkService>> itemService = hiveAp.getConfigTemplate().getClassifierMap().getNetworkServices().entrySet().iterator();
			while (itemService.hasNext()) {
				Entry<Long, QosNetworkService> entryService = itemService.next();
			    NetworkService service = entryService.getValue().getNetworkService();
				if (service != null && service.getServiceType() == NetworkService.SERVICE_TYPE_L7) {
					if (service.getAppId() > maxAppId) {
						itemService.remove();
					}
				}
			}
		}
		if(hiveAp.getConfigTemplate().getFwPolicy() != null && 
				hiveAp.getConfigTemplate().getFwPolicy().getRules() != null){
			Iterator<FirewallPolicyRule> iter = hiveAp.getConfigTemplate().getFwPolicy().getRules().iterator();
			while (iter.hasNext()) {
				FirewallPolicyRule rule = iter.next();
				NetworkService service = rule.getNetworkService();
				if (service != null && service.getServiceType() == NetworkService.SERVICE_TYPE_L7) {
					if (service.getAppId() > maxAppId) {
						iter.remove();
					}
				}
			}
		}
		
		//set global Traffic Filter to per interface.
		if(!hiveAp.getConfigTemplate().isOverrideTF4IndividualAPs() && hiveAp.getConfigTemplate().getDeviceServiceFilter() != null){
			ServiceFilter defFilter = hiveAp.getConfigTemplate().getDeviceServiceFilter();
			
			//eth0
			hiveAp.getConfigTemplate().setEth0ServiceFilter(defFilter);
			hiveAp.getConfigTemplate().setEth0BackServiceFilter(defFilter);
			
			//eth1
			hiveAp.getConfigTemplate().setEth1ServiceFilter(defFilter);
			hiveAp.getConfigTemplate().setEth1BackServiceFilter(defFilter);
			
			//red0
			hiveAp.getConfigTemplate().setRed0ServiceFilter(defFilter);
			hiveAp.getConfigTemplate().setRed0BackServiceFilter(defFilter);
			
			//agg0
			hiveAp.getConfigTemplate().setAgg0ServiceFilter(defFilter);
			hiveAp.getConfigTemplate().setAgg0BackServiceFilter(defFilter);
			
			//wifi
			hiveAp.getConfigTemplate().setWireServiceFilter(defFilter);
		}
	}
	
	private void filterNetworkObject() throws CreateXMLException{
		//Keep valid Vpn Network for router
		if(hiveAp.isBranchRouter() && hiveAp.getConfigTemplate().getVlanNetwork() != null){
			Set<Integer> allVlanSet = new HashSet<Integer>(getAllDeviceVlans(this.hiveAp));
			Iterator<ConfigTemplateVlanNetwork> vlanNetworkItem = hiveAp.getConfigTemplate().getVlanNetwork().iterator();
			int vpnNetworkCounts = 0;
			while(vlanNetworkItem.hasNext()){
				ConfigTemplateVlanNetwork vlanNetwork = vlanNetworkItem.next();
				Integer vlan = CLICommonFunc.getVlan(vlanNetwork.getVlan(), hiveAp).getVlanId();
				if(!allVlanSet.contains(vlan)){
					vlanNetworkItem.remove();
				}else if(vlanNetwork.getNetworkObj() != null){
					vpnNetworkCounts ++;
				}
			}
			if(hiveAp.getDeviceInfo().isSptEthernetMore_24() && vpnNetworkCounts > 65){
				String[] errParams = {"65"};
				String errMsg = NmsUtil.getUserMessage("error.be.config.create.maxVlan.switch", errParams);
				throw new CreateXMLException(errMsg);
			}else if(!hiveAp.getDeviceInfo().isSptEthernetMore_24() && vpnNetworkCounts > 17){
				String[] errParams = {"17"};
				String errMsg = NmsUtil.getUserMessage("error.be.config.create.maxVlan.br", errParams);
				throw new CreateXMLException(errMsg);
			}
		}
	}
	
/*	private void prepareWifiClientMode() {
		if(hiveAp.getWifiClientPreferredSsids() == null || !hiveAp.getDeviceInfo().isContainsRouterFunc()){
			return;
		}
		
		//load bo HiveApPreferredSsid
		for(HiveApPreferredSsid simpleSsid : hiveAp.getWifiClientPreferredSsids()){
			if(simpleSsid.getPreferredSsid() == null) {
				WifiClientPreferredSsid ssid = MgrUtil.getQueryEntity(hiveAp.getOwner()).findBoById(WifiClientPreferredSsid.class, simpleSsid.getPreferredId());
				simpleSsid.setPreferredSsid(ssid);
			}
			if(simpleSsid.getPreferredSsid() == null){
				continue;
			}
			cloneWifiClientSsid(simpleSsid.getPreferredSsid(), simpleSsid.getPriority());
		}
	}
	
	private void cloneWifiClientSsid(WifiClientPreferredSsid wifiSsid, int priority) {
		if(wifiSsid == null){
			return;
		}
		//clone attribute from WifiClientPreferredSsid
		SsidProfile ssidObj = new SsidProfile();
		ssidObj.setSsid(wifiSsid.getSsid());
		ssidObj.setSsidName(wifiSsid.getSsid());
		ssidObj.setAccessMode(wifiSsid.getAccessMode());
		ssidObj.setEncryption(wifiSsid.getEncryption());
		ssidObj.setAuthentication(wifiSsid.getAuthentication());
		ssidObj.setMgmtKey(wifiSsid.getMgmtKey());
		ssidObj.setSsidSecurity(new SsidSecurity());
		ssidObj.getSsidSecurity().setKeyType(wifiSsid.getKeyType());
		ssidObj.getSsidSecurity().setFirstKeyValue(wifiSsid.getKeyValue());
		ssidObj.setWifiPriority(priority);
		DeviceInterface wifi0Interface = hiveAp.getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_WIFI0);
		DeviceInterface wifi1Interface = hiveAp.getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_WIFI1);

		if( hiveAp.getRole(wifi0Interface) == AhInterface.ROLE_WAN){
			if(hiveAp.getWifi0().getRadioMode() == AhInterface.RADIO_MODE_BG || 
					hiveAp.getWifi0().getRadioMode() == AhInterface.RADIO_MODE_NG){
				ssidObj.setRadioMode(SsidProfile.RADIOMODE_BG);
			}else{
				ssidObj.setRadioMode(SsidProfile.RADIOMODE_A);
			}
		}else if( hiveAp.getRole(wifi1Interface) == AhInterface.ROLE_WAN){
			if(hiveAp.getWifi1().getRadioMode() == AhInterface.RADIO_MODE_BG || 
					hiveAp.getWifi1().getRadioMode() == AhInterface.RADIO_MODE_NG){
				ssidObj.setRadioMode(SsidProfile.RADIOMODE_BG);
			}else{
				ssidObj.setRadioMode(SsidProfile.RADIOMODE_A);
			}
		}
		
		//bind ssid to network policy
		ConfigTemplateSsid ssidClone = new ConfigTemplateSsid();
		ssidClone.setInterfaceName(ssidObj.getSsidName());
		ssidClone.setSsidProfile(ssidObj);
		hiveAp.getConfigTemplate().getSsidInterfaces().put(index_for_ppsk--, ssidClone);
	}*/
	
//	private boolean checkBRStaticRoute(HiveAp hiveAp, boolean isView, StringBuilder routeMsg){
//		if( isView || 
//			hiveAp.getDeviceType() != HiveAp.Device_TYPE_BRANCH_ROUTER || 
//			hiveAp.getIpRoutes() == null || 
//			hiveAp.getIpRoutes().isEmpty()){
//			return true;
//		}
//		String checkWhere = "hiveApMac = :s1 and ipStartLong < :s2 and :s2 <= ipEndLong";
//		for(HiveApIpRoute route : hiveAp.getIpRoutes()){
//			String gwStr = route.getGateway();
//			long gwLong = AhEncoder.ip2Long(gwStr);
//			List<?> resList = MgrUtil.getQueryEntity(hiveAp.getOwner()).executeQuery(SubNetworkResource.class, null, new FilterParams(checkWhere, new Object[]{hiveAp.getMacAddress(), gwLong}));
//			if(resList == null || resList.isEmpty()){
////				routeMsg.append(NmsUtil.getUserMessage("error.be.config.create.checkBRStaticRoute", new String[] {gwStr}));
////				return false;
//				continue;
//			}
//			for(Object obj : resList){
//				if(obj instanceof SubNetworkResource){
//					SubNetworkResource source = (SubNetworkResource)obj;
//					String ipPoolStart = source.getIpPoolStart();
//					String ipPoolEnd = source.getIpPoolEnd();
//					long ipPoolStartLong = AhEncoder.ip2Long(ipPoolStart);
//					long ipPoolEndLong = AhEncoder.ip2Long(ipPoolEnd);
//					if(gwLong >= ipPoolStartLong && gwLong <= ipPoolEndLong){
//						routeMsg.append(NmsUtil.getUserMessage("error.be.config.create.checkBRStaticRoutePool", new String[] {gwStr, ipPoolStart+" ~ "+ipPoolEnd}));
//						return false;
//					}
//				}
//			}
//		}
//		return true;
//	}
	
	private boolean isDeviceNeedAllUserProfile(HiveAp hiveAp){
		if(hiveAp == null){
			return false;
		}
		return ( (hiveAp.isBranchRouter() || hiveAp.getDeviceInfo().isSptEthernetMore_24()) && isExistsTrunkPort(hiveAp) ) || 
				(hiveAp.isCVGAppliance() && hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP) ||
				(hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP && hiveAp.getVpnMark() == HiveAp.VPN_MARK_SERVER && 
				hiveAp.getConfigTemplate().getVpnService() != null && hiveAp.getConfigTemplate().getVpnService().getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_2);
	}
	
	private UserProfile cloneUserProfileForWpa(SsidProfile cloneSsid, HiveAp hiveAp){
UserProfile upPpsk = MgrUtil.getQueryEntity().findBoByAttribute(UserProfile.class, "userProfileName", "default-profile", new ConfigLazyQueryBo());
		
		upPpsk.setId(index_for_ppsk--);
		String policyName=cloneSsid.getSsid();
		policyName=policyName.substring(0,policyName.length() > 23 ? 23 : policyName.length());
		upPpsk.setUserProfileName(policyName + IpPolicyProfileImpl.IP_POLICY_FOR_WPA_AUTO);
		upPpsk.setDefaultFlag(false);
		
		//if(cloneSsid.getPpskServer() != null || cloneSsid.isBlnBrAsPpskServer()){
			upPpsk.setIpPolicyFrom(cloneIpPolicyForWpa(cloneSsid, hiveAp));
			upPpsk.setIpPolicyTo(cloneIpPolicyForWpa(cloneSsid, hiveAp));
		//}
		upPpsk.setActionIp(IpPolicyRule.POLICY_ACTION_DENY);
		return upPpsk;
		
//		UserProfile upPpsk = MgrUtil.getQueryEntity().findBoByAttribute(UserProfile.class, "userProfileName", "default-profile", new ConfigLazyQueryBo());
//		upPpsk.setId(index_for_wpa--);
//		String policyName=cloneSsid.getSsid();
//		policyName=policyName.substring(0,policyName.length() > 23 ? 23 : policyName.length());
//		upPpsk.setUserProfileName(policyName + IpPolicyProfileImpl.IP_POLICY_FOR_WPA_AUTO);
//		upPpsk.setDefaultFlag(false);
//		upPpsk.setActionIp(IpPolicyRule.POLICY_ACTION_DENY);
		//return upPpsk;
	}
	private boolean isExistsTrunkPort(HiveAp hiveAp){
		PortGroupProfile portGroup = hiveAp.getPortGroup();
		if(portGroup == null || portGroup.getBasicProfiles() == null){
			return false;
		}
		
		for(PortBasicProfile bsProfile : portGroup.getBasicProfiles()){
			if(bsProfile.getAccessProfile() != null && 
					bsProfile.getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_8021Q){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isEnableClientManagement(SsidProfile ssidProfileObj){
		HMServicesSettings bo = MgrUtil.getQueryEntity().findBoByAttribute(HMServicesSettings.class,"owner",ssidProfileObj.getOwner());
	     if(bo != null && bo.isEnableClientManagement()){
	    	 return true;
	     }
	     return false;
	}
	
		private SsidProfile loadWPAAuthCwp(SsidProfile ssidProfileObj, HiveAp hiveAp){
		SsidProfile cloneSsid = null;
		if(ssidProfileObj != null && isEnableClientManagement(ssidProfileObj) && ssidProfileObj.isEnableProvisionPersonal()){
			cloneSsid = MgrUtil.getQueryEntity().findBoByAttribute(SsidProfile.class, "ssidName", "ssid0", new ConfigLazyQueryBo());
			cloneSsid.setSsidName(ssidProfileObj.getWpaOpenSsid());
			cloneSsid.setSsid(ssidProfileObj.getWpaOpenSsid());
			cloneSsid.setRadioMode(ssidProfileObj.getRadioMode());
			cloneSsid.setEnablePpskSelfReg(ssidProfileObj.isEnablePpskSelfReg());
			cloneSsid.setPpskServer(ssidProfileObj.getPpskServer());
			cloneSsid.setPpskOpenSsid(ssidProfileObj.getSsid());
			cloneSsid.setCwp(ssidProfileObj.getWpaECwp());
			cloneSsid.setCwpSelectEnabled(true);
			cloneSsid.setPpskECwp(ssidProfileObj.getPpskECwp());
			cloneSsid.setWpaECwp(ssidProfileObj.getWpaECwp());
			cloneSsid.setRadiusAssignment(ssidProfileObj.getRadiusAssignment());
			cloneSsid.setRadiusAssignmentPpsk(ssidProfileObj.getRadiusAssignmentPpsk());
			cloneSsid.setBlnBrAsPpskServer(ssidProfileObj.isBlnBrAsPpskServer());
			//cloneSsid.setUserProfileSelfReg(this.cloneUserProfileForWpa(cloneSsid, hiveAp));
			cloneSsid.setUserProfileDefault(this.cloneUserProfileForWpa(cloneSsid, hiveAp));
			cloneSsid.setEnableProvisionPrivate(ssidProfileObj.isEnableProvisionPrivate());			
			cloneSsid.setEnableProvisionPersonal(ssidProfileObj.isEnableProvisionPersonal());
			cloneSsid.setOnboardSsid(ssidProfileObj.getSsid());
			cloneSsid.setOwner(ssidProfileObj.getOwner());
			cloneSsid.setMacAuthEnabled(ssidProfileObj.getMacAuthEnabled());
			cloneSsid.setPersonPskRadiusAuth(ssidProfileObj.getPersonPskRadiusAuth());
		}
		
		return cloneSsid;
	}
	
	private void cloneSsidForWPA(HiveAp hiveAp){

		Map<Long, ConfigTemplateSsid> ssidInterfaces = hiveAp.getConfigTemplate().getSsidInterfaces();
		
		List<ConfigTemplateSsid> ssidTempList = new ArrayList<ConfigTemplateSsid>();

		for (ConfigTemplateSsid ssidRelation : ssidInterfaces.values()) {
			if (ssidRelation.getSsidProfile() != null) {
				SsidProfile ssidProfileObj = ssidRelation.getSsidProfile();
				SsidProfile cloneOpen = this.loadWPAAuthCwp(ssidProfileObj, this.hiveAp);
				if(cloneOpen != null){
					ConfigTemplateSsid ssidTemp = cloneConfigTemplateSsid(ssidRelation, cloneOpen);
					ssidTempList.add(ssidTemp);
				}
			}
		}
		for(ConfigTemplateSsid ssidTemp : ssidTempList){
			hiveAp.getConfigTemplate().getSsidInterfaces().put(index_for_wpa--, ssidTemp);
		}
	
	}
	
	private void cloneSsidForPPSK(HiveAp hiveAp){

		Map<Long, ConfigTemplateSsid> ssidInterfaces = hiveAp.getConfigTemplate().getSsidInterfaces();
		
		List<ConfigTemplateSsid> ssidTempList = new ArrayList<ConfigTemplateSsid>();

		for (ConfigTemplateSsid ssidRelation : ssidInterfaces.values()) {
			if (ssidRelation.getSsidProfile() != null) {
				SsidProfile ssidProfileObj = ssidRelation.getSsidProfile();
				SsidProfile cloneOpen = this.loadPpskSelfReg(ssidProfileObj, this.hiveAp);
				if(cloneOpen != null){
					ConfigTemplateSsid ssidTemp = cloneConfigTemplateSsid(ssidRelation, cloneOpen);
					ssidTempList.add(ssidTemp);
				}
			}
		}
		for(ConfigTemplateSsid ssidTemp : ssidTempList){
			hiveAp.getConfigTemplate().getSsidInterfaces().put(index_for_ppsk--, ssidTemp);
		}
	
	}
	
//	private RadiusProxy createIDMAuthProxy(String ipAddress, RadiusProxy proxy, String softVer){
//		if(proxy == null){
//			proxy = new RadiusProxy();
//			proxy.setProxyName("idmAuthProxy");
//		}
//		
//		if(NmsUtil.compareSoftwareVersion("6.1.2.0", softVer) > 0){
//			RadiusProxyRealm defRealm = null;
//			for(RadiusProxyRealm realm : proxy.getRadiusRealm()){
//				if(RadiusProxyRealm.DEFAULT_REALM_NAME.equals(realm.getServerName())){
//					defRealm = realm;
//					break;
//				}
//			}
//			if(defRealm == null){
//				defRealm = new RadiusProxyRealm();
//				defRealm.setServerName(RadiusProxyRealm.DEFAULT_REALM_NAME);
//				defRealm.setIdmAuthProxy(true);
//				defRealm.setRadiusServer(createIDMAuthProxyRadClient(ipAddress));
//				proxy.getRadiusRealm().add(defRealm);
//			}else if(defRealm.getRadiusServer() == null){
//				defRealm.setRadiusServer(createIDMAuthProxyRadClient(ipAddress));
//				defRealm.setIdmAuthProxy(true);
//			}
//		}
//		
//		IDMConfig radSecConfig = hiveAp.getDownloadInfo().getIdmRadSecConfig();
//		if(radSecConfig != null){
//			String idmServer = radSecConfig.getIdmGatewayServer();
//			IpAddress oIp = CLICommonFunc.getGlobalHost(idmServer);
//			oIp.setTypeFlag(IpAddress.TYPE_HOST_NAME);
//			RadiusHiveapAuth oNas = new RadiusHiveapAuth();
//			oNas.setIpAddress(oIp);
//			oNas.setEnableTls(true);
//			if(proxy.getRadiusNas() == null){
//				proxy.setRadiusNas(new ArrayList<RadiusHiveapAuth>());
//			}
//			proxy.getRadiusNas().add(oNas);
//		}
//		
//		return proxy;
//	}
	
//	private RadiusAssignment createIDMAuthProxyRadClient(String ipAddress) {
//		if(ipAddress == null || "".equals(ipAddress)){
//			return null;
//		}
//		IpAddress oIp = CLICommonFunc.getGlobalHost(ipAddress);
//		RadiusAssignment radius = new RadiusAssignment();
//		radius.setRadiusName("IDMAuthProxy");
//		RadiusServer radServer = new RadiusServer();
//		radServer.setServerPriority(RadiusServer.RADIUS_PRIORITY_PRIMARY);
//		radServer.setIpAddress(oIp);
//		radServer.setSharedSecret("aerohive");
//		radius.getServices().add(radServer);
//		
//		return radius;
//	}
	
	public static Set<VpnServiceCredential> generate(int count,short modeType) {
		log.info("generate", "count:" + count);
		if(HiveAp.HIVEAP_MODEL_VPN_GATEWAY == modeType){
			count = count > VpnService.MAX_IP_POOL_SIZE_VPN_CVG_DEVICE ? VpnService.MAX_IP_POOL_SIZE_VPN_CVG_DEVICE
					: count;// make sure count doesn't beyond MAX_IP_POOL_SIZE_VPN_CVG_DEVICE
		}else{
			count = count > VpnService.MAX_IP_POOL_SIZE ? VpnService.MAX_IP_POOL_SIZE
					: count;// make sure count doesn't beyond MAX_IP_POOL_SIZE
		}
		
		Set<VpnServiceCredential> set = new HashSet<>(count);
		while (set.size() < count) {// make sure credentials are unique
			String username = MgrUtil.getRandomString(32, 3);
			String password = MgrUtil.getRandomString(32, 7);
			VpnServiceCredential sc = new VpnServiceCredential();
			sc.setClientName(username);
			sc.setCredential(password);
			sc.setAllocated(false);
			set.add(sc);
		}
		return set;
	}
	
	private void configUpdateCheck() throws CreateXMLException {
		
		//IDM low version check
		IdmSelfRegCheck();
		//IDM check on wired port
		checkIDMOnEthernet();
	}
	
	private void IdmSelfRegCheck() throws CreateXMLException {
		if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.1.0") >= 0){
			return;
		}
		
		int radioCounts = hiveAp.getDeviceInfo().getIntegerValue(DeviceInfo.SPT_RADIO_COUNTS);
		if(radioCounts <= 0){
			return;
		}
		
		if(hiveAp.getDownloadInfo().isEnableIdm()){
			return;
		}
		
		SsidProfile ssidProfile = null;
		for(ConfigTemplateSsid ssidTemp : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
			ssidProfile = ssidTemp.getSsidProfile();
			if(ssidProfile == null){
				continue;
			}
			if(ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_OPEN  && 
					ssidProfile.isEnabledIDM() && 
					ssidProfile.getCwp() != null && 
					ssidProfile.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH){
				String errorMsg = NmsUtil.getUserMessage("error.be.config.create.idm.self-cwp.check");
				throw new CreateXMLException(errorMsg);
			}
		}
	}
	
	private void checkIDMOnEthernet() throws CreateXMLException{
		if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.3.0") >= 0){
			return;
		}
		
		if(hiveAp.getPortGroup() != null && hiveAp.getPortGroup().getBasicProfiles() != null){
			for(PortBasicProfile basicProfile : hiveAp.getPortGroup().getBasicProfiles()){
				if(basicProfile.getAccessProfile() == null){
					continue;
				}
				if(basicProfile.getAccessProfile().isEnabledIDM()){
					String errorMsg = NmsUtil.getUserMessage("error.be.config.create.idm.wired.port.check");
					throw new CreateXMLException(errorMsg);
				}
			}
		}
	}
	
	public HiveAp getHiveAp(){
		return this.hiveAp;
	}
}