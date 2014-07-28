package com.ah.bo.hiveap;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.network.AccessConsole;
import com.ah.bo.network.AlgConfiguration;
import com.ah.bo.network.ApplicationProfile;
import com.ah.bo.network.BonjourGatewaySettings;
import com.ah.bo.network.CLIBlob;
import com.ah.bo.network.FirewallPolicy;
import com.ah.bo.network.IdsPolicy;
import com.ah.bo.network.IpFilter;
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.RadiusAttrs;
import com.ah.bo.network.RoutingPolicy;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.SwitchSettings;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnService;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.LocationServer;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.MgmtServiceSyslog;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.useraccess.UserProfileVlanMapping;
import com.ah.ui.actions.BaseAction;

@Entity
@Table(name = "CONFIG_TEMPLATE" , uniqueConstraints = { @UniqueConstraint(columnNames = {
		"OWNER", "CONFIGNAME" }) })
@org.hibernate.annotations.Table(appliesTo = "CONFIG_TEMPLATE", indexes = {
		@Index(name = "CONFIG_TEMPLATE_OWNER", columnNames = { "OWNER" }),
		@Index(name = "CONFIG_TEMPLATE_OWNER_DEFAULTFLAG", columnNames = { "OWNER", "DEFAULTFLAG" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class ConfigTemplate implements HmBo {

	private static final long serialVersionUID = 1L;
	
	public static final long SSID_INTERFACES_MAPKEY_ETH0 = -1;
	public static final long SSID_INTERFACES_MAPKEY_ETH1 = -2;
	public static final long SSID_INTERFACES_MAPKEY_RED0 = -3;
	public static final long SSID_INTERFACES_MAPKEY_AGG0 = -4;
	public static final long SSID_INTERFACES_MAPKEY_ETH2 = -5;
	public static final long SSID_INTERFACES_MAPKEY_ETH3 = -6;
	public static final long SSID_INTERFACES_MAPKEY_ETH4 = -7;

	/**
	 * please <b><font color='red'>Do Not</font></b> use this constructor only if you really know what you are doing!
	 */
	public ConfigTemplate() {
	}
	
	public ConfigTemplate(int configTemplatType) {
		this.setConfigType(configTemplatType);
	}
	
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp version;

	private boolean defaultFlag;
	
	private boolean enableAirTime;
	
	private boolean enableReportCollection=true;
	private int collectionInterval=10;
	private int collectionIfCrc=30;
	private int collectionIfTxDrop=40;
	private int collectionIfRxDrop=40;
	private int collectionIfTxRetry=40;
	private int collectionIfAirtime=50;
	
	private int collectionClientTxDrop=40;
	private int collectionClientRxDrop=40;
	private int collectionClientTxRetry=40;
	private int collectionClientAirtime=30;
	
	private boolean enabledMapOverride;
	
	//WLAN connection alarm 
	private boolean enableConnectionAlarm;
	private int txRetryThreshold = 20;
	@Transient
	private int txRetryInterval = 5;
	private int txFrameErrorThreshold = 10;
	@Transient
	private int txFrameErrorInterval = 5;
	private int probRequestThreshold = 5;
	@Transient
	private int probRequestInterval = 5;
	private int egressMulticastThreshold = 5000;
	@Transient
	private int egressMulticastInterval = 5;
	private int ingressMulticastThreshold = 5000;
	@Transient
	private int ingressMulticastInterval = 5;
	private int channelUtilizationThreshold = 70;
	@Transient
	private int channelUtilizationInterval = 5;
	
	private boolean enableL7Switch = true;
	
	private boolean enableKddr;

	public static final int SLA_DEFAULT_NOTIFICATION_INTERVAL = 600;	
	@Range(min = 30, max = 1800)
	private int slaInterval = SLA_DEFAULT_NOTIFICATION_INTERVAL;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String configName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "HIVE_PROFILE_ID")
	private HiveProfile hiveProfile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MGMT_SERVICE_DNS_ID")
	private MgmtServiceDns mgmtServiceDns;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MGMT_SERVICE_SYSLOG_ID")
	private MgmtServiceSyslog mgmtServiceSyslog;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MGMT_SERVICE_SNMP_ID")
	private MgmtServiceSnmp mgmtServiceSnmp;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MGMT_SERVICE_TIME_ID")
	private MgmtServiceTime mgmtServiceTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MGMT_SERVICE_OPTION_ID")
	private MgmtServiceOption mgmtServiceOption;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CLIENT_WATCH_ID")
	private LocationClientWatch clientWatch;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IDS_POLICY_ID")
	private IdsPolicy idsPolicy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VLAN_ID")
	private Vlan vlan;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NATIVE_VLAN_ID")
	private Vlan vlanNative;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IP_FILTER_ID")
	private IpFilter ipFilter;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACCESS_CONSOLE_ID")
	private AccessConsole accessConsole;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEVICE_SERVICE_FILTER_ID")
	private ServiceFilter deviceServiceFilter;
	
	private boolean overrideTF4IndividualAPs;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ETH0_SERVICE_FILTER_ID")
	private ServiceFilter eth0ServiceFilter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WIRE_SERVICE_FILTER_ID")
	private ServiceFilter wireServiceFilter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ETH0BACK_SERVICE_FILTER_ID")
	private ServiceFilter eth0BackServiceFilter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ETH1BACK_SERVICE_FILTER_ID")
	private ServiceFilter eth1BackServiceFilter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RED0BACK_SERVICE_FILTER_ID")
	private ServiceFilter red0BackServiceFilter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGG0BACK_SERVICE_FILTER_ID")
	private ServiceFilter agg0BackServiceFilter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOCATION_SERVER_ID")
	private LocationServer locationServer;

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ETHERNET_ACCESS_ID")
//	private EthernetAccess ethernetAccess;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ETHERNET_BRIDGE_ID")
//	private EthernetAccess ethernetBridge;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALG_CONFIGURATION_ID")
	private AlgConfiguration algConfiguration;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QOS_CLASSIFICATION_ID")
	private QosClassification classifierMap;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QOS_MARKING_ID")
	private QosMarking markerMap;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="mapkey")
	@CollectionTable(name = "CONFIG_TEMPLATE_SSID", joinColumns = @JoinColumn(name = "CONFIG_TEMPLATE_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Map<Long, ConfigTemplateSsid> ssidInterfaces = new HashMap<>();

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "CONFIG_TEMPLATE_VLANNETWORK", joinColumns = @JoinColumn(name = "CONFIG_TEMPLATE_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<ConfigTemplateVlanNetwork> vlanNetwork = new ArrayList<>();
	
	 @ManyToMany(fetch = FetchType.LAZY)
	 @JoinTable(name = "CONFIG_TEMPLATE_IP_TRACK", joinColumns = @JoinColumn(name ="CONFIG_TEMPLATE_ID", nullable = true))
	 @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	 private Set<MgmtServiceIPTrack> ipTracks = new HashSet<>();
	 
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "IP_TRACK_ID")
//	private MgmtServiceIPTrack routerIpTrack;
	
	@ManyToOne
	@JoinColumn(name = "PRIMARY_IP_TRACK_ID")
	private MgmtServiceIPTrack primaryIpTrack;

	@ManyToOne
	@JoinColumn(name = "BACKUP1_IP_TRACK_ID")
	private MgmtServiceIPTrack backup1IpTrack;

	@ManyToOne
	@JoinColumn(name = "BACKUP2_IP_TRACK_ID")
	private MgmtServiceIPTrack backup2IpTrack;

	private Long thirdPort;
	/**
	 * add fields for dual port
	 * @see com.ah.bo.HmBo#getId()
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ETH1_SERVICE_FILTER_ID")
	private ServiceFilter eth1ServiceFilter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RED0_SERVICE_FILTER_ID")
	private ServiceFilter red0ServiceFilter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGG0_SERVICE_FILTER_ID")
	private ServiceFilter agg0ServiceFilter;

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ETHERNET_ACCESS_ID_ETH1")
//	private EthernetAccess ethernetAccessEth1;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ETHERNET_BRIDGE_ID_ETH1")
//	private EthernetAccess ethernetBridgeEth1;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ETHERNET_ACCESS_ID_RED")
//	private EthernetAccess ethernetAccessRed;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ETHERNET_BRIDGE_ID_RED")
//	private EthernetAccess ethernetBridgeRed;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ETHERNET_ACCESS_ID_AGG")
//	private EthernetAccess ethernetAccessAgg;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ETHERNET_BRIDGE_ID_AGG")
//	private EthernetAccess ethernetBridgeAgg;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VPN_SERVICE_ID")
	private VpnService vpnService;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LLDPCDP_ID", nullable = true)
	private LLDPCDPProfile lldpCdp;

	private boolean enableProbe;
	@Range(min = 60, max = 600)
	private int probeInterval = 60;
	@Range(min = 1, max = 10)
	private int probeRetryCount=3;
	@Range(min = 1, max = 60)
	private int probeRetryInterval=10;
	@Column(length = DEFAULT_STRING_LENGTH)
	private String probeUsername="";
	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String probePassword="";
	
	private boolean enableOSDURL=true;
	private boolean enableTVService;
	
	private boolean enableHttpServer=true;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CONFIG_TEMPLATE_TV_SERVICE", joinColumns = @JoinColumn(name ="CONFIG_TEMPLATE_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<NetworkService> tvNetworkService = new HashSet<>();
	
	// add from Congo
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FIREWALL_POLICY_ID")
	private FirewallPolicy fwPolicy;
	
	// add from Dakar
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BONJOUR_GATEWAY_ID")
	private BonjourGatewaySettings bonjourGw;

	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE })
	@JoinTable(name = "CONFIG_TEMPLATE_PORT", joinColumns = { @JoinColumn(name = "CONFIG_TEMPLATE_ID") }, inverseJoinColumns = { @JoinColumn(name = "PORTPROFILES_ID") })
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<PortGroupProfile> portProfiles = new HashSet<>();

	@OneToMany(mappedBy = "networkPolicy", cascade = { CascadeType.PERSIST,
			CascadeType.MERGE, CascadeType.REMOVE })
	private Set<UserProfileVlanMapping> upVlanMapping = new HashSet<>();
	
//	private String managementNetwork="1.3.0.0/16";
	
	private boolean enabledWanConfiguration;
	
	public boolean isEnabledWanConfiguration() {
		return enabledWanConfiguration;
	}

	public void setEnabledWanConfiguration(boolean enabledWanConfiguration) {
		this.enabledWanConfiguration = enabledWanConfiguration;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RADIUS_SERVER_ID")
	private RadiusOnHiveap radiusServerProfile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RADIUS_PROXY_ID")
	private RadiusProxy radiusProxyProfile;
	
//	private boolean enabledRouterPpskServer;
	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "mgt_network_id")
//	private VpnNetwork mgtNetwork;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RADIUS_ATTRS_ID")
	private RadiusAttrs radiusAttrs;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUPPLEMENTAL_CLI_ID")
	private CLIBlob supplementalCLI;
	
	// for 802.1X client settings
    public static final int DEFAULT_CLIENT_EXPIRE_TIME = 300;
    public static final int DEFAULT_CLIENT_SUPPRESS_INTERVAL = 0;
    private int clientExpireTime8021X = DEFAULT_CLIENT_EXPIRE_TIME;
    private int clientSuppressInterval8021X = DEFAULT_CLIENT_SUPPRESS_INTERVAL;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "CONFIG_TEMPLATE_STORM_CONTROL", joinColumns = @JoinColumn(name = "CONFIG_TEMPLATE_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<ConfigTemplateStormControl> stormControlList = new ArrayList<>();

	public List<ConfigTemplateStormControl> getStormControlList() {
		return stormControlList;
	}

	public void setStormControlList(
			List<ConfigTemplateStormControl> stormControlList) {
		this.stormControlList = stormControlList;
	}
	
	private short switchStormControlMode = ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_BYTE;

	public short getSwitchStormControlMode() {
		return switchStormControlMode;
	}

	public void setSwitchStormControlMode(short switchStormControlMode) {
		this.switchStormControlMode = switchStormControlMode;
	}
	
	public String getShowStormLimitTypeBased(){
		if(switchStormControlMode == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_BYTE){
			return "";
		} else {
			return "none";
		}
	}
	public String getShowStormLimitTypePacket(){
		if(switchStormControlMode == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_PACKET){
			return "";
		} else {
			return "none";
		}
	}
	
	//************ VoIP Bandwidth Limiting Setting start **********//
	private boolean enableEth0LimitDownloadBandwidth;
	
	private boolean enableEth0LimitUploadBandwidth;
	
	private short eth0LimitDownloadRate=100;
	
	private short eth0LimitUploadRate=100;
		
	private boolean enableUSBLimitDownloadBandwidth;
	
	private boolean enableUSBLimitUploadBandwidth;
	
	private short usbLimitDownloadRate=100;
	
	private short usbLimitUploadRate=100;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROUTING_PBR_POLICY_ID")
	private RoutingProfilePolicy routingProfilePolicy;

	public RoutingProfilePolicy getRoutingProfilePolicy() {
		return routingProfilePolicy;
	}

	public void setRoutingProfilePolicy(RoutingProfilePolicy routingProfilePolicy) {
		this.routingProfilePolicy = routingProfilePolicy;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROUTING_POLICY_ID")
	private RoutingPolicy routingPolicy;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "appProfileId", nullable = true)
	private ApplicationProfile appProfile;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column = @Column(name="enable_wireless"), name = "wirelessEnabled"),
		@AttributeOverride(column = @Column(name="enable_switch"), name = "switchEnabled"),
		@AttributeOverride(column = @Column(name="enable_router"), name = "routerEnabled"),
		@AttributeOverride(column = @Column(name="enable_bonjour"), name = "bonjourEnabled")})
	private ConfigTemplateType configType = new ConfigTemplateType();
	
	public ApplicationProfile getAppProfile() {
		return appProfile;
	}

	public void setAppProfile(ApplicationProfile appProfile) {
		this.appProfile = appProfile;
	}

	public short getEth0LimitDownloadRate() {
		return eth0LimitDownloadRate;
	}

	public void setEth0LimitDownloadRate(short eth0LimitDownloadRate) {
		this.eth0LimitDownloadRate = eth0LimitDownloadRate;
	}

	public short getEth0LimitUploadRate() {
		return eth0LimitUploadRate;
	}

	public void setEth0LimitUploadRate(short eth0LimitUploadRate) {
		this.eth0LimitUploadRate = eth0LimitUploadRate;
	}

	public short getUsbLimitDownloadRate() {
		return usbLimitDownloadRate;
	}

	public void setUsbLimitDownloadRate(short usbLimitDownloadRate) {
		this.usbLimitDownloadRate = usbLimitDownloadRate;
	}

	public short getUsbLimitUploadRate() {
		return usbLimitUploadRate;
	}

	public void setUsbLimitUploadRate(short usbLimitUploadRate) {
		this.usbLimitUploadRate = usbLimitUploadRate;
	}
	
	public boolean isEnableEth0LimitDownloadBandwidth() {
		return enableEth0LimitDownloadBandwidth;
	}

	public void setEnableEth0LimitDownloadBandwidth(
			boolean enableEth0LimitDownloadBandwidth) {
		this.enableEth0LimitDownloadBandwidth = enableEth0LimitDownloadBandwidth;
	}

	public boolean isEnableEth0LimitUploadBandwidth() {
		return enableEth0LimitUploadBandwidth;
	}

	public void setEnableEth0LimitUploadBandwidth(
			boolean enableEth0LimitUploadBandwidth) {
		this.enableEth0LimitUploadBandwidth = enableEth0LimitUploadBandwidth;
	}

	public boolean isEnableUSBLimitDownloadBandwidth() {
		return enableUSBLimitDownloadBandwidth;
	}

	public void setEnableUSBLimitDownloadBandwidth(
			boolean enableUSBLimitDownloadBandwidth) {
		this.enableUSBLimitDownloadBandwidth = enableUSBLimitDownloadBandwidth;
	}

	public boolean isEnableUSBLimitUploadBandwidth() {
		return enableUSBLimitUploadBandwidth;
	}

	public void setEnableUSBLimitUploadBandwidth(
			boolean enableUSBLimitUploadBandwidth) {
		this.enableUSBLimitUploadBandwidth = enableUSBLimitUploadBandwidth;
	}

	//************ VoIP Bandwidth Limiting Setting end **********//

	public RadiusAttrs getRadiusAttrs() {
		return radiusAttrs;
	}

	public void setRadiusAttrs(RadiusAttrs radiusAttrs) {
		this.radiusAttrs = radiusAttrs;
	}

	public FirewallPolicy getFwPolicy()
	{
		return fwPolicy;
	}

	public void setFwPolicy(FirewallPolicy fwPolicy)
	{
		this.fwPolicy = fwPolicy;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return configName;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getConfigName() {
		return configName;
	}
	
	@OneToOne(fetch = FetchType.LAZY,cascade = {CascadeType.ALL})
	@JoinColumn(name = "SWITCH_SETTINGS_ID", nullable = true)
	private SwitchSettings switchSettings;
	
	public SwitchSettings getSwitchSettings() {
		return switchSettings;
	}

	public void setSwitchSettings(SwitchSettings switchSettings) {
		this.switchSettings = switchSettings;
	}

	public String getConfigNameSubstr(){
		if (configName==null) {
			return "";
		}
		if (configName.length()> (BaseAction.DISPLAY_LENGTH_IN_GUI_OK)) {
			return configName.substring(0, BaseAction.DISPLAY_LENGTH_IN_GUI) + "...";
		}
		
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public HiveProfile getHiveProfile() {
		return hiveProfile;
	}

	public void setHiveProfile(HiveProfile hiveProfile) {
		this.hiveProfile = hiveProfile;
	}

	public Vlan getVlan() {
		return vlan;
	}

	public void setVlan(Vlan vlan) {
		this.vlan = vlan;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<Long, ConfigTemplateSsid> getSsidInterfaces() {
		return ssidInterfaces;
	}
	
	public TreeMap<Long, ConfigTemplateSsid> getSsidInterfacesTreeMap() {
		if (ssidInterfaces==null) return null;
		List<Map.Entry<Long, ConfigTemplateSsid>> info = new ArrayList<>(ssidInterfaces.entrySet());
        Collections.sort(info, new Comparator<Map.Entry<Long, ConfigTemplateSsid>>() {
            public int compare(Map.Entry<Long, ConfigTemplateSsid> obj1, Map.Entry<Long, ConfigTemplateSsid> obj2) {
                return obj2.getValue().getInterfaceName().compareToIgnoreCase(obj1.getValue().getInterfaceName());
            }
        });
        TreeMap<Long, ConfigTemplateSsid> retValue = new TreeMap<>();
		for (Map.Entry<Long, ConfigTemplateSsid> anInfo : info) {
			retValue.put(anInfo.getKey(), anInfo.getValue());
		}
		return retValue;
	}
	
	public TreeMap<String, ConfigTemplateStormControl> getStormControlTreeMap() {
		if (stormControlList==null) return null;
		 
        TreeMap<String, ConfigTemplateStormControl> retValue = new TreeMap<>();
		for (ConfigTemplateStormControl anInfo : stormControlList) {
			retValue.put(anInfo.getInterfaceType(), anInfo);
		}
		return retValue;
	}

	public void setSsidInterfaces(Map<Long, ConfigTemplateSsid> ssidInterfaces) {
		this.ssidInterfaces = ssidInterfaces;
	}

	public Set<MgmtServiceIPTrack> getIpTracks() {
		return ipTracks;
	}

	public void setIpTracks(Set<MgmtServiceIPTrack> ipTracks) {
		this.ipTracks = ipTracks;
	}

	public MgmtServiceDns getMgmtServiceDns() {
		return mgmtServiceDns;
	}

	public void setMgmtServiceDns(MgmtServiceDns mgmtServiceDns) {
		this.mgmtServiceDns = mgmtServiceDns;
	}

	public MgmtServiceSyslog getMgmtServiceSyslog() {
		return mgmtServiceSyslog;
	}

	public void setMgmtServiceSyslog(MgmtServiceSyslog mgmtServiceSyslog) {
		this.mgmtServiceSyslog = mgmtServiceSyslog;
	}

	public MgmtServiceSnmp getMgmtServiceSnmp() {
		return mgmtServiceSnmp;
	}

	public void setMgmtServiceSnmp(MgmtServiceSnmp mgmtServiceSnmp) {
		this.mgmtServiceSnmp = mgmtServiceSnmp;
	}

	public MgmtServiceTime getMgmtServiceTime() {
		return mgmtServiceTime;
	}

	public void setMgmtServiceTime(MgmtServiceTime mgmtServiceTime) {
		this.mgmtServiceTime = mgmtServiceTime;
	}

	public IdsPolicy getIdsPolicy() {
		return idsPolicy;
	}

	public void setIdsPolicy(IdsPolicy idsPolicy) {
		this.idsPolicy = idsPolicy;
	}

	public IpFilter getIpFilter() {
		return ipFilter;
	}

	public void setIpFilter(IpFilter ipFilter) {
		this.ipFilter = ipFilter;
	}

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public MgmtServiceOption getMgmtServiceOption() {
		return mgmtServiceOption;
	}

	public void setMgmtServiceOption(MgmtServiceOption mgmtServiceOption) {
		this.mgmtServiceOption = mgmtServiceOption;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	public ServiceFilter getDeviceServiceFilter() {
		return deviceServiceFilter;
	}

	public boolean isOverrideTF4IndividualAPs() {
		return overrideTF4IndividualAPs;
	}

	public void setDeviceServiceFilter(ServiceFilter deviceServiceFilter) {
		this.deviceServiceFilter = deviceServiceFilter;
	}

	public void setOverrideTF4IndividualAPs(boolean overrideTF4IndividualAPs) {
		this.overrideTF4IndividualAPs = overrideTF4IndividualAPs;
	}

	public ServiceFilter getEth0ServiceFilter() {
		return eth0ServiceFilter;
	}

	public void setEth0ServiceFilter(ServiceFilter eth0ServiceFilter) {
		this.eth0ServiceFilter = eth0ServiceFilter;
	}

	public ServiceFilter getWireServiceFilter() {
		return wireServiceFilter;
	}

	public void setWireServiceFilter(ServiceFilter wireServiceFilter) {
		this.wireServiceFilter = wireServiceFilter;
	}

	public ServiceFilter getEth0BackServiceFilter() {
		return eth0BackServiceFilter;
	}

	public void setEth0BackServiceFilter(ServiceFilter eth0BackServiceFilter) {
		this.eth0BackServiceFilter = eth0BackServiceFilter;
	}

	public LocationServer getLocationServer() {
		return locationServer;
	}

	public void setLocationServer(LocationServer locationServer) {
		this.locationServer = locationServer;
	}

//	public EthernetAccess getEthernetAccess() {
//		return ethernetAccess;
//	}
//
//	public void setEthernetAccess(EthernetAccess ethernetAccess) {
//		this.ethernetAccess = ethernetAccess;
//	}

	public AlgConfiguration getAlgConfiguration() {
		return algConfiguration;
	}

	public void setAlgConfiguration(AlgConfiguration algConfiguration) {
		this.algConfiguration = algConfiguration;
	}

	public QosClassification getClassifierMap() {
		return classifierMap;
	}

	public void setClassifierMap(QosClassification classifierMap) {
		this.classifierMap = classifierMap;
	}

	public QosMarking getMarkerMap() {
		return markerMap;
	}

	public void setMarkerMap(QosMarking markerMap) {
		this.markerMap = markerMap;
	}

//	public EthernetAccess getEthernetBridge() {
//		return ethernetBridge;
//	}

	public ServiceFilter getEth1BackServiceFilter()
	{
		return eth1BackServiceFilter;
	}

	public void setEth1BackServiceFilter(ServiceFilter eth1BackServiceFilter)
	{
		this.eth1BackServiceFilter = eth1BackServiceFilter;
	}

	public ServiceFilter getRed0BackServiceFilter()
	{
		return red0BackServiceFilter;
	}

	public void setRed0BackServiceFilter(ServiceFilter red0BackServiceFilter)
	{
		this.red0BackServiceFilter = red0BackServiceFilter;
	}

	public ServiceFilter getAgg0BackServiceFilter()
	{
		return agg0BackServiceFilter;
	}

	public void setAgg0BackServiceFilter(ServiceFilter agg0BackServiceFilter)
	{
		this.agg0BackServiceFilter = agg0BackServiceFilter;
	}

	public ServiceFilter getEth1ServiceFilter()
	{
		return eth1ServiceFilter;
	}

	public void setEth1ServiceFilter(ServiceFilter eth1ServiceFilter)
	{
		this.eth1ServiceFilter = eth1ServiceFilter;
	}

	public ServiceFilter getRed0ServiceFilter()
	{
		return red0ServiceFilter;
	}

	public void setRed0ServiceFilter(ServiceFilter red0ServiceFilter)
	{
		this.red0ServiceFilter = red0ServiceFilter;
	}

	public ServiceFilter getAgg0ServiceFilter()
	{
		return agg0ServiceFilter;
	}

	public void setAgg0ServiceFilter(ServiceFilter agg0ServiceFilter)
	{
		this.agg0ServiceFilter = agg0ServiceFilter;
	}

//	public EthernetAccess getEthernetAccessEth1()
//	{
//		return ethernetAccessEth1;
//	}
//
//	public void setEthernetAccessEth1(EthernetAccess ethernetAccessEth1)
//	{
//		this.ethernetAccessEth1 = ethernetAccessEth1;
//	}
//
//	public EthernetAccess getEthernetBridgeEth1()
//	{
//		return ethernetBridgeEth1;
//	}

//	public void setEthernetBridgeEth1(EthernetAccess ethernetBridgeEth1)
//	{
//		this.ethernetBridgeEth1 = ethernetBridgeEth1;
//	}
//
//	public EthernetAccess getEthernetAccessRed()
//	{
//		return ethernetAccessRed;
//	}

//	public void setEthernetAccessRed(EthernetAccess ethernetAccessRed)
//	{
//		this.ethernetAccessRed = ethernetAccessRed;
//	}
//
//	public EthernetAccess getEthernetBridgeRed()
//	{
//		return ethernetBridgeRed;
//	}

//	public void setEthernetBridgeRed(EthernetAccess ethernetBridgeRed)
//	{
//		this.ethernetBridgeRed = ethernetBridgeRed;
//	}
//
//	public EthernetAccess getEthernetAccessAgg()
//	{
//		return ethernetAccessAgg;
//	}

//	public void setEthernetAccessAgg(EthernetAccess ethernetAccessAgg)
//	{
//		this.ethernetAccessAgg = ethernetAccessAgg;
//	}
//
//	public EthernetAccess getEthernetBridgeAgg()
//	{
//		return ethernetBridgeAgg;
//	}

//	public void setEthernetBridgeAgg(EthernetAccess ethernetBridgeAgg)
//	{
//		this.ethernetBridgeAgg = ethernetBridgeAgg;
//	}
//
//	public void setEthernetBridge(EthernetAccess ethernetBridge) {
//		this.ethernetBridge = ethernetBridge;
//	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public boolean getEnableAirTime() {
		return enableAirTime;
	}

	public void setEnableAirTime(boolean enableAirTime) {
		this.enableAirTime = enableAirTime;
	}

	public int getSlaInterval() {
		return slaInterval;
	}

	public void setSlaInterval(int slaInterval) {
		this.slaInterval = slaInterval;
	}

	public AccessConsole getAccessConsole() {
		return accessConsole;
	}

	public void setAccessConsole(AccessConsole accessConsole) {
		this.accessConsole = accessConsole;
	}

	public Vlan getVlanNative() {
		return vlanNative;
	}

	public void setVlanNative(Vlan vlanNative) {
		this.vlanNative = vlanNative;
	}

	public VpnService getVpnService() {
		return vpnService;
	}

	public void setVpnService(VpnService vpnService) {
		this.vpnService = vpnService;
	}

	public LLDPCDPProfile getLldpCdp() {
		return lldpCdp;
	}

	public void setLldpCdp(LLDPCDPProfile lldpCdp) {
		this.lldpCdp = lldpCdp;
	}

	public LocationClientWatch getClientWatch() {
		return clientWatch;
	}

	public void setClientWatch(LocationClientWatch clientWatch) {
		this.clientWatch = clientWatch;
	}
	
	@Override
    public ConfigTemplate clone() {
       try {
           return (ConfigTemplate) super.clone();
       } catch (CloneNotSupportedException e) {
           return null;
       }
    }

	public boolean getEnabledMapOverride() {
		return enabledMapOverride;
	}

	public void setEnabledMapOverride(boolean enabledMapOverride) {
		this.enabledMapOverride = enabledMapOverride;
	}

	public boolean getEnableReportCollection() {
		return enableReportCollection;
	}

	public void setEnableReportCollection(boolean enableReportCollection) {
		this.enableReportCollection = enableReportCollection;
	}

	public int getCollectionInterval() {
		return collectionInterval;
	}

	public void setCollectionInterval(int collectionInterval) {
		this.collectionInterval = collectionInterval;
	}

	/**
	 * @return the enableProbe
	 */
	public boolean isEnableProbe() {
		return enableProbe;
	}

	/**
	 * @param enableProbe the enableProbe to set
	 */
	public void setEnableProbe(boolean enableProbe) {
		this.enableProbe = enableProbe;
	}

	/**
	 * @return the probeInterval
	 */
	public int getProbeInterval() {
		return probeInterval;
	}

	/**
	 * @param probeInterval the probeInterval to set
	 */
	public void setProbeInterval(int probeInterval) {
		this.probeInterval = probeInterval;
	}

	/**
	 * @return the probeRetryCount
	 */
	public int getProbeRetryCount() {
		return probeRetryCount;
	}

	/**
	 * @param probeRetryCount the probeRetryCount to set
	 */
	public void setProbeRetryCount(int probeRetryCount) {
		this.probeRetryCount = probeRetryCount;
	}

	/**
	 * @return the probeRetryInterval
	 */
	public int getProbeRetryInterval() {
		return probeRetryInterval;
	}

	/**
	 * @param probeRetryInterval the probeRetryInterval to set
	 */
	public void setProbeRetryInterval(int probeRetryInterval) {
		this.probeRetryInterval = probeRetryInterval;
	}

	/**
	 * @return the probeUsername
	 */
	public String getProbeUsername() {
		return probeUsername;
	}

	/**
	 * @param probeUsername the probeUsername to set
	 */
	public void setProbeUsername(String probeUsername) {
		this.probeUsername = probeUsername;
	}

	/**
	 * @return the probePassword
	 */
	public String getProbePassword() {
		return probePassword;
	}

	/**
	 * @param probePassword the probePassword to set
	 */
	public void setProbePassword(String probePassword) {
		this.probePassword = probePassword;
	}

	/**
	 * @return the collectionIfCrc
	 */
	public int getCollectionIfCrc() {
		return collectionIfCrc;
	}

	/**
	 * @param collectionIfCrc the collectionIfCrc to set
	 */
	public void setCollectionIfCrc(int collectionIfCrc) {
		this.collectionIfCrc = collectionIfCrc;
	}

	/**
	 * @return the collectionIfTxDrop
	 */
	public int getCollectionIfTxDrop() {
		return collectionIfTxDrop;
	}

	/**
	 * @param collectionIfTxDrop the collectionIfTxDrop to set
	 */
	public void setCollectionIfTxDrop(int collectionIfTxDrop) {
		this.collectionIfTxDrop = collectionIfTxDrop;
	}

	/**
	 * @return the collectionIfRxDrop
	 */
	public int getCollectionIfRxDrop() {
		return collectionIfRxDrop;
	}

	/**
	 * @param collectionIfRxDrop the collectionIfRxDrop to set
	 */
	public void setCollectionIfRxDrop(int collectionIfRxDrop) {
		this.collectionIfRxDrop = collectionIfRxDrop;
	}

	/**
	 * @return the collectionIfTxRetry
	 */
	public int getCollectionIfTxRetry() {
		return collectionIfTxRetry;
	}

	/**
	 * @param collectionIfTxRetry the collectionIfTxRetry to set
	 */
	public void setCollectionIfTxRetry(int collectionIfTxRetry) {
		this.collectionIfTxRetry = collectionIfTxRetry;
	}

	/**
	 * @return the collectionIfAirtime
	 */
	public int getCollectionIfAirtime() {
		return collectionIfAirtime;
	}

	/**
	 * @param collectionIfAirtime the collectionIfAirtime to set
	 */
	public void setCollectionIfAirtime(int collectionIfAirtime) {
		this.collectionIfAirtime = collectionIfAirtime;
	}

	/**
	 * @return the collectionClientTxDrop
	 */
	public int getCollectionClientTxDrop() {
		return collectionClientTxDrop;
	}

	/**
	 * @param collectionClientTxDrop the collectionClientTxDrop to set
	 */
	public void setCollectionClientTxDrop(int collectionClientTxDrop) {
		this.collectionClientTxDrop = collectionClientTxDrop;
	}

	/**
	 * @return the collectionClientRxDrop
	 */
	public int getCollectionClientRxDrop() {
		return collectionClientRxDrop;
	}

	/**
	 * @param collectionClientRxDrop the collectionClientRxDrop to set
	 */
	public void setCollectionClientRxDrop(int collectionClientRxDrop) {
		this.collectionClientRxDrop = collectionClientRxDrop;
	}

	/**
	 * @return the collectionClientTxRetry
	 */
	public int getCollectionClientTxRetry() {
		return collectionClientTxRetry;
	}

	/**
	 * @param collectionClientTxRetry the collectionClientTxRetry to set
	 */
	public void setCollectionClientTxRetry(int collectionClientTxRetry) {
		this.collectionClientTxRetry = collectionClientTxRetry;
	}

	/**
	 * @return the collectionClientAirtime
	 */
	public int getCollectionClientAirtime() {
		return collectionClientAirtime;
	}

	/**
	 * @param collectionClientAirtime the collectionClientAirtime to set
	 */
	public void setCollectionClientAirtime(int collectionClientAirtime) {
		this.collectionClientAirtime = collectionClientAirtime;
	}

	/**
	 * @return the enableOSDURL
	 */
	public boolean isEnableOSDURL() {
		return enableOSDURL;
	}

	/**
	 * @param enableOSDURL the enableOSDURL to set
	 */
	public void setEnableOSDURL(boolean enableOSDURL) {
		this.enableOSDURL = enableOSDURL;
	}

	/**
	 * @return the enableTVService
	 */
	public boolean isEnableTVService() {
		return enableTVService;
	}

	/**
	 * @param enableTVService the enableTVService to set
	 */
	public void setEnableTVService(boolean enableTVService) {
		this.enableTVService = enableTVService;
	}

	/**
	 * @return the tvNetworkService
	 */
	public Set<NetworkService> getTvNetworkService() {
		return tvNetworkService;
	}

	/**
	 * @param tvNetworkService the tvNetworkService to set
	 */
	public void setTvNetworkService(Set<NetworkService> tvNetworkService) {
		this.tvNetworkService = tvNetworkService;
	}

	public boolean isEnableHttpServer() {
		return enableHttpServer;
	}

	public void setEnableHttpServer(boolean enableHttpServer) {
		this.enableHttpServer = enableHttpServer;
	}

	public Set<PortGroupProfile> getPortProfiles() {
		if(portProfiles != null && !portProfiles.isEmpty()){
			Set<PortGroupProfile> defaults = new TreeSet<PortGroupProfile>();
			defaults.addAll(portProfiles);
	        return defaults;
		}else{
			return portProfiles;
		}
	}

    public void setPortProfiles(Set<PortGroupProfile> portProfiles) {
        this.portProfiles = portProfiles;
    }

//	public MgmtServiceIPTrack getRouterIpTrack() {
//		return routerIpTrack;
//	}

//	public void setRouterIpTrack(MgmtServiceIPTrack routerIpTrack) {
//		this.routerIpTrack = routerIpTrack;
//	}

	/**
	 * this method is not suggested to be used, please use below method for replacement:
	 * 1, configType.isWirelessAndRouterOnly : if you want only wireless only and router are enabled
	 * 2, configType.isWirelessAndRouterContained : if you want wireless only and router are enabled, but no matter other types are enabled
	 * 3, configType.isTypeSupport : if you want check whether certain type(s) are contained, no matter other types
	 * 4, configType.isTypeSupportStrict : if you want check only certain type(s) are contained, without other types enabled
	 *
	 * @return -
	 */
	@Deprecated
	public boolean isBlnWirelessRouter() {
		return this.getConfigType().isTypeSupportStrict(ConfigTemplateType.WIRELESS|ConfigTemplateType.ROUTER);
	}

//	public VpnNetwork getMgtNetwork() {
//	return mgtNetwork;
//}
//
//public void setMgtNetwork(VpnNetwork mgtNetwork) {
//	this.mgtNetwork = mgtNetwork;
//}

	public RadiusOnHiveap getRadiusServerProfile() {
		return radiusServerProfile;
	}

	public void setRadiusServerProfile(RadiusOnHiveap radiusServerProfile) {
		this.radiusServerProfile = radiusServerProfile;
	}

	public RadiusProxy getRadiusProxyProfile() {
		return radiusProxyProfile;
	}

	public void setRadiusProxyProfile(RadiusProxy radiusProxyProfile) {
		this.radiusProxyProfile = radiusProxyProfile;
	}

    public int getClientExpireTime8021X() {
        return clientExpireTime8021X;
    }

    public void setClientExpireTime8021X(int clientExpireTime8021X) {
        this.clientExpireTime8021X = clientExpireTime8021X;
    }

    public int getClientSuppressInterval8021X() {
        return clientSuppressInterval8021X;
    }

    public void setClientSuppressInterval8021X(int clientSuppressInterval8021X) {
        this.clientSuppressInterval8021X = clientSuppressInterval8021X;
    }

//	public boolean isEnabledRouterPpskServer() {
//		return enabledRouterPpskServer;
//	}
//
//	public void setEnabledRouterPpskServer(boolean enabledRouterPpskServer) {
//		this.enabledRouterPpskServer = enabledRouterPpskServer;
//	}

//	public String getManagementNetwork() {
//		return managementNetwork;
//	}
//
//	public void setManagementNetwork(String managementNetwork) {
//		this.managementNetwork = managementNetwork;
//	}

    public BonjourGatewaySettings getBonjourGw() {
        return bonjourGw;
    }

    public void setBonjourGw(BonjourGatewaySettings bonjourGw) {
        this.bonjourGw = bonjourGw;
    }
    
	public RoutingPolicy getRoutingPolicy() {
		return routingPolicy;
	}

	public void setRoutingPolicy(RoutingPolicy routingPolicy) {
		this.routingPolicy = routingPolicy;
	}
	
	public boolean isEnableL7Switch() {
		return enableL7Switch;
	}

	public void setEnableL7Switch(boolean enableL7Switch) {
		this.enableL7Switch = enableL7Switch;
	}
	 

	public boolean isEnableKddr() {
		return enableKddr;
	}

	public void setEnableKddr(boolean enableKddr) {
		this.enableKddr = enableKddr;
	}

	public boolean isEnableConnectionAlarm() {
		return enableConnectionAlarm;
	}

	public void setEnableConnectionAlarm(boolean enableConnectionAlarm) {
		this.enableConnectionAlarm = enableConnectionAlarm;
	}

	public int getTxRetryThreshold() {
		return txRetryThreshold;
	}

	public void setTxRetryThreshold(int txRetryThreshold) {
		this.txRetryThreshold = txRetryThreshold;
	}

	public int getTxRetryInterval() {
		return txRetryInterval;
	}

	public void setTxRetryInterval(int txRetryInterval) {
		this.txRetryInterval = txRetryInterval;
	}

	public int getTxFrameErrorThreshold() {
		return txFrameErrorThreshold;
	}

	public void setTxFrameErrorThreshold(int txFrameErrorThreshold) {
		this.txFrameErrorThreshold = txFrameErrorThreshold;
	}

	public int getTxFrameErrorInterval() {
		return txFrameErrorInterval;
	}

	public void setTxFrameErrorInterval(int txFrameErrorInterval) {
		this.txFrameErrorInterval = txFrameErrorInterval;
	}

	public int getProbRequestThreshold() {
		return probRequestThreshold;
	}

	public void setProbRequestThreshold(int probRequestThreshold) {
		this.probRequestThreshold = probRequestThreshold;
	}

	public int getProbRequestInterval() {
		return probRequestInterval;
	}

	public void setProbRequestInterval(int probRequestInterval) {
		this.probRequestInterval = probRequestInterval;
	}

	public int getEgressMulticastThreshold() {
		return egressMulticastThreshold;
	}

	public void setEgressMulticastThreshold(int egressMulticastThreshold) {
		this.egressMulticastThreshold = egressMulticastThreshold;
	}

	public int getEgressMulticastInterval() {
		return egressMulticastInterval;
	}

	public void setEgressMulticastInterval(int egressMulticastInterval) {
		this.egressMulticastInterval = egressMulticastInterval;
	}

	public int getIngressMulticastThreshold() {
		return ingressMulticastThreshold;
	}

	public void setIngressMulticastThreshold(int ingressMulticastThreshold) {
		this.ingressMulticastThreshold = ingressMulticastThreshold;
	}

	public int getIngressMulticastInterval() {
		return ingressMulticastInterval;
	}

	public void setIngressMulticastInterval(int ingressMulticastInterval) {
		this.ingressMulticastInterval = ingressMulticastInterval;
	}

	public int getChannelUtilizationThreshold() {
		return channelUtilizationThreshold;
	}

	public void setChannelUtilizationThreshold(int channelUtilizationThreshold) {
		this.channelUtilizationThreshold = channelUtilizationThreshold;
	}

	public int getChannelUtilizationInterval() {
		return channelUtilizationInterval;
	}

	public void setChannelUtilizationInterval(int channelUtilizationInterval) {
		this.channelUtilizationInterval = channelUtilizationInterval;
	}

	public ConfigTemplateType getConfigType() {
		return configType;
	}

	public void setConfigType(ConfigTemplateType configType) {
		this.configType = configType;
	}

	public void setConfigType(int configTemplatType) {
		if ((ConfigTemplateType.WIRELESS & configTemplatType) == ConfigTemplateType.WIRELESS) {
			this.getConfigType().setWirelessEnabled(true);
		}
		if ((ConfigTemplateType.SWITCH & configTemplatType) == ConfigTemplateType.SWITCH) {
			this.getConfigType().setSwitchEnabled(true);
		}
		if ((ConfigTemplateType.ROUTER & configTemplatType) == ConfigTemplateType.ROUTER) {
			this.getConfigType().setRouterEnabled(true);
		}
		if ((ConfigTemplateType.BONJOUR & configTemplatType) == ConfigTemplateType.BONJOUR) {
			this.getConfigType().setBonjourEnabled(true);
		}
	}
	
	public List<ConfigTemplateVlanNetwork> getVlanNetwork() {
		if (vlanNetwork==null) vlanNetwork = new ArrayList<>();
		return vlanNetwork;
	}

	public void setVlanNetwork(List<ConfigTemplateVlanNetwork> vlanNetwork) {
		this.vlanNetwork = vlanNetwork;
	}
	
	public List<ConfigTemplateVlanNetwork> getVlanNetworkTreeMap() {
		if (vlanNetwork==null) return null;
		//List<ConfigTemplateVlanNetwork> info = new ArrayList<ConfigTemplateVlanNetwork>();
        Collections.sort(vlanNetwork, new Comparator<ConfigTemplateVlanNetwork>() {
			@Override
            public int compare(ConfigTemplateVlanNetwork obj1, ConfigTemplateVlanNetwork obj2) {
                return obj1.getVlan().getVlanName().compareToIgnoreCase(obj2.getVlan().getVlanName());
            }
        });
		return vlanNetwork;
	}
	
	public VpnNetwork getNetworkByVlan(Vlan vlan){
		if(vlan == null){
			return null;
		}
		if(vlanNetwork == null || vlanNetwork.isEmpty()){
			return null;
		}
		for(ConfigTemplateVlanNetwork vlanNet : vlanNetwork){
			if(vlan.getId().equals(vlanNet.getVlan().getId())){
				return vlanNet.getNetworkObj();
			}
		}
		return null;
	}
	
	public Vlan getVlanByNetwork(VpnNetwork network){
		if(network == null){
			return null;
		}
		if(vlanNetwork == null || vlanNetwork.isEmpty()){
			return null;
		}
		for(ConfigTemplateVlanNetwork vlanNet : vlanNetwork){
			if(vlanNet == null || vlanNet.getNetworkObj() == null) {
				continue;
			}
			if(network.getId().equals(vlanNet.getNetworkObj().getId())){
				return vlanNet.getVlan();
			}
		}
		return null;
	}
	
	public VpnNetwork getMgtNetwork(){
		if(vlan == null){
			return null;
		}
		return getNetworkByVlan(vlan);
	}

	public Set<UserProfileVlanMapping> getUpVlanMapping() {
		return upVlanMapping;
	}

	public void setUpVlanMapping(Set<UserProfileVlanMapping> upVlanMapping) {
		this.upVlanMapping = upVlanMapping;
	}
	
	@Transient
	private Long preVlanId;
	
	@Transient
	private Long preNetworkId;

	public Long getPreVlanId() {
		return preVlanId;
	}

	public void setPreVlanId(Long preVlanId) {
		this.preVlanId = preVlanId;
	}

	public Long getPreNetworkId() {
		return preNetworkId;
	}

	public void setPreNetworkId(Long preNetworkId) {
		this.preNetworkId = preNetworkId;
	}
	
	private static final int INSERT_AFTERT = 1;
	private static final int INSERT_BEFORE = -1;
	@Transient
	public Set<PortGroupProfile> getSortedPortProfiles() {
	    if(null == this.portProfiles || this.portProfiles.isEmpty()) {
	        return this.portProfiles;
	    } else {
	        Set<PortGroupProfile> sortedPorts = new TreeSet<>(new Comparator<PortGroupProfile>() {
	            @Override
	            public int compare(PortGroupProfile insert, PortGroupProfile compare) {
	                if(insert.getDeviceType() == compare.getDeviceType()) {
	                    return insert.getName().compareTo(compare.getName());
	                } else {
	                    return insert.getDeviceType() > compare.getDeviceType() ? INSERT_BEFORE : INSERT_AFTERT;
	                }
	            }
	        });
	        sortedPorts.addAll(this.portProfiles);
	        return sortedPorts;
	    }
	}
	@Transient
	private Set<PortAccessProfile> accessProfiles = new TreeSet<>(new Comparator<PortAccessProfile>() {
        @Override
        public int compare(PortAccessProfile insertAcc, PortAccessProfile compareAcc) {
            int result = 0;
            if(insertAcc.getPortType() == compareAcc.getPortType()) {
                result = insertAcc.getName().compareTo(compareAcc.getName());
            } else {
                // access, phone&data, 802.1q, mirror, wan
                if(insertAcc.getPortType() == PortAccessProfile.PORT_TYPE_ACCESS) {
                    return INSERT_BEFORE;
                } else if(insertAcc.getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA) {
                    if(compareAcc.getPortType() == PortAccessProfile.PORT_TYPE_ACCESS) {
                        return INSERT_AFTERT;
                    } else {
                        return INSERT_BEFORE;
                    }
                } else if (insertAcc.getPortType() == PortAccessProfile.PORT_TYPE_8021Q) {
                    if(compareAcc.getPortType() == PortAccessProfile.PORT_TYPE_ACCESS
                            ||compareAcc.getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA) {
                        return INSERT_AFTERT;
                    } else {
                        return INSERT_BEFORE;
                    }
                } else if (insertAcc.getPortType() == PortAccessProfile.PORT_TYPE_MONITOR) {
                    if(compareAcc.getPortType() == PortAccessProfile.PORT_TYPE_ACCESS
                            ||compareAcc.getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA
                            ||compareAcc.getPortType() == PortAccessProfile.PORT_TYPE_8021Q) {
                        return INSERT_AFTERT;
                    } else {
                        return INSERT_BEFORE;
                    }
                } else {
                    return INSERT_AFTERT;
                }
            }
            return result;
        }
    });

    public Set<PortAccessProfile> getAccessProfiles() {
        accessProfiles.clear();
        if(!this.getPortProfiles().isEmpty()) {
            for (PortGroupProfile portGroup : this.getPortProfiles()) {
            	//non default templates
            	if(!portGroup.getItems().isEmpty()){
            		for(SingleTableItem nonItem : portGroup.getItems()){
            			if(null != nonItem && nonItem.getConfigTemplateId() == this.getId().longValue()){
            				if (nonItem.getNonDefault() == null)
            					continue;
            				 for (PortBasicProfile nonBasic : nonItem.getNonDefault().getBasicProfiles()) {
                				 final PortAccessProfile nonAccessProfile = nonBasic.getAccessProfile();
                				 accessProfiles.add(nonAccessProfile);
                			 }
            			}
            		}
            	}
                if(!portGroup.getBasicProfiles().isEmpty()) {
                    for (PortBasicProfile basic : portGroup.getBasicProfiles()) {
                        final PortAccessProfile accessProfile = basic.getAccessProfile();
                        if(null != accessProfile) {
                            accessProfiles.add(accessProfile);
                        }
                    }
                }
            }
        }
        return accessProfiles;
    }

    public void setAccessProfiles(Set<PortAccessProfile> accessProfiles) {
        this.accessProfiles = accessProfiles;
    }

	public MgmtServiceIPTrack getPrimaryIpTrack() {
		return primaryIpTrack;
	}

	public void setPrimaryIpTrack(MgmtServiceIPTrack primaryIpTrack) {
		this.primaryIpTrack = primaryIpTrack;
	}

	public MgmtServiceIPTrack getBackup1IpTrack() {
		return backup1IpTrack;
	}

	public void setBackup1IpTrack(MgmtServiceIPTrack backup1IpTrack) {
		this.backup1IpTrack = backup1IpTrack;
	}

	public MgmtServiceIPTrack getBackup2IpTrack() {
		return backup2IpTrack;
	}

	public void setBackup2IpTrack(MgmtServiceIPTrack backup2IpTrack) {
		this.backup2IpTrack = backup2IpTrack;
	}

	public Long getThirdPort() {
		return thirdPort;
	}

	public void setThirdPort(Long thirdPort) {
		this.thirdPort = thirdPort;
	}

	/*Add control for CAPWAP delay alarm from Guadalupe*/
  	private boolean enableDelayAlarm = true;

	public boolean isEnableDelayAlarm() {
		return enableDelayAlarm;
	}

	public void setEnableDelayAlarm(boolean enableDelayAlarm) {
		this.enableDelayAlarm = enableDelayAlarm;
	}

	public CLIBlob getSupplementalCLI() {
		return supplementalCLI;
	}

	public void setSupplementalCLI(CLIBlob supplementalCLI) {
		this.supplementalCLI = supplementalCLI;
	}
}