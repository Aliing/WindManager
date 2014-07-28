package com.ah.bo.hiveap;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
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
import javax.persistence.Version;
import javax.validation.constraints.Min;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.be.app.AhAppContainer;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.be.db.configuration.ConfigurationResources;
import com.ah.be.parameter.BeParaModule;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.parameter.device.DevicePropertyManage;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.igmp.IgmpPolicy;
import com.ah.bo.igmp.MulticastGroup;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryLazyBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.network.CLIBlob;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.PPPoE;
import com.ah.bo.network.RoutingPolicy;
import com.ah.bo.network.RoutingProfile;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.VlanDhcpServer;
import com.ah.bo.network.VpnService;
import com.ah.bo.performance.AhXIf;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CountryCode;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.coder.AhDecoder;
import com.ah.util.datetime.AhDateTimeUtil;
import com.ah.util.devices.impl.Device;
import com.ah.xml.deviceProperties.DeviceObj;

/*
 * modification history
 *
 * add field 'eth1','red0','agg0'
 * joseph chen , 04/10/2008
 *
 * add field 'hiveApModel' and related functions
 * joseph chen, 04/14/2008
 */
@Entity
@Table(name = "HIVE_AP")
@org.hibernate.annotations.Table(appliesTo = "HIVE_AP", indexes = {
		@Index(name = "HIVE_AP_OWNER", columnNames = { "OWNER" }),
		@Index(name = "HIVE_AP_OWNER_MANAGE_STATUS", columnNames = { "OWNER", "MANAGESTATUS" })
		})
public class HiveAp implements HmBo {

	private static final long serialVersionUID = 1L;

	public HiveAp(short hiveApModel){
		this.hiveApModel = hiveApModel;
		this.deviceType = (short)this.getDeviceInfo().getDeviceTypeEnum()[0].getKey();
		init();
		initInterface();
		initDeviceStpSettings();
	}

	// initial some fields
	public HiveAp() {
		init();
	}

	public void init(){
		this.getEth0().setOperationMode(AhInterface.OPERATION_MODE_BACKHAUL);
		this.getEth1().setOperationMode(AhInterface.OPERATION_MODE_BACKHAUL);
		this.getRed0().setOperationMode(AhInterface.OPERATION_MODE_BACKHAUL);
		this.getAgg0().setOperationMode(AhInterface.OPERATION_MODE_BACKHAUL);
		this.getWifi0().setOperationMode(AhInterface.OPERATION_MODE_ACCESS);
		this.getWifi0().setRadioMode(AhInterface.RADIO_MODE_BG);
		this.getWifi1().setOperationMode(AhInterface.OPERATION_MODE_ACCESS);
		this.getWifi1().setRadioMode(AhInterface.RADIO_MODE_A);
		this.getWifi0().setPower(AhInterface.POWER_AUTO);
		this.getWifi1().setPower(AhInterface.POWER_AUTO);
		this.setPassPhrase(NmsUtil.generatePassphrase());
		this.setKeyId(NmsUtil.getNewDtlsKeyId(this.getCurrentKeyId()));
		this.setSeverity(AhAlarm.AH_SEVERITY_UNDETERMINED);
		// set pending default value
		this.setPending(true);
		this.setPendingIndex(ConfigurationResources.CONFIG_HIVEAP_INITIAL);
		// set pending user database default value
		this.setPending_user(false);
		//set out door flag
		this.setIsOutdoor(this.hiveApModel == HiveAp.HIVEAP_MODEL_170 ? true : false);
	}

	public void initInterface(){
		int ethCounts = this.getDeviceInfo().getIntegerValue(DeviceInfo.SPT_ETHERNET_COUNTS);
		int sfpCounts = this.getDeviceInfo().getIntegerValue(DeviceInfo.SPT_SFP_COUNTS);
		int usbCounts = this.getDeviceInfo().getIntegerValue(DeviceInfo.SPT_USB_COUNTS);
		int interType;
		if(ethCounts > 1){

			//switch ethernet interface start from ETH1/1
			if(this.getDeviceInfo().isSptEthernetMore_24()){
				interType = AhInterface.DEVICE_IF_TYPE_ETH1;
			}else{
				interType = AhInterface.DEVICE_IF_TYPE_ETH0;
			}

			while(ethCounts > 0){
				DeviceInterface dInf = new DeviceInterface();
				dInf.setDeviceIfType((short)interType);
				//any router has eth0.Eth0 work as wan by default and has primary priority
				if(interType==AhInterface.DEVICE_IF_TYPE_ETH0){
					dInf.setWanOrder(1);
				}
				this.getDeviceInterfaces().put((long)interType, dInf);


				ConfigTemplateStormControl stormControl = new ConfigTemplateStormControl();
				stormControl.setInterfaceNum((short)interType);
				stormControl.setRateLimitValue(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_DEFULT_VALUE);
				this.getStormControlList().add(stormControl);

				ethCounts--;
				interType++;
			}
		}
		if(sfpCounts > 0){
			int sfpIndex = 1;
			while(sfpCounts > 0){
				DeviceInterface dInf = new DeviceInterface();

				short sfpIfType = DeviceInfType.SFP.getFinalValue(sfpIndex++, this.hiveApModel);
				dInf.setDeviceIfType(sfpIfType);

				this.getDeviceInterfaces().put((long)sfpIfType, dInf);

				ConfigTemplateStormControl stormControl = new ConfigTemplateStormControl();
				stormControl.setInterfaceNum((short)sfpIfType);
				stormControl.setRateLimitValue(200000000);
				this.getStormControlList().add(stormControl);

				sfpCounts--;
			}
		}

		if(usbCounts > 0){
			interType = AhInterface.DEVICE_IF_TYPE_USB;
			while(usbCounts > 0){
				DeviceInterface dInf = new DeviceInterface();
				dInf.setDeviceIfType((short)interType);
				//any router has usb.Usb work as wan by default and has bacuk1 priority
				if(interType==AhInterface.DEVICE_IF_TYPE_USB){
					if(this.getDeviceInfo().isSptEthernetMore_24()){
						//dInf.setWanOrder(1);
					}else{
						dInf.setWanOrder(2);
					}
				}
				this.getDeviceInterfaces().put((long)interType, dInf);
				usbCounts--;
				interType++;
			}
		}
	}

	@Transient
	private DeviceInfo deviceInfo;
	private String portTemplate;


	public String getPortTemplate() {
		return this.getPortGroup().getName();
	}

	public void setPortTemplate(String portTemplate) {
		this.portTemplate = portTemplate;
	}

	public DeviceInfo getDeviceInfo() {
		if(deviceInfo == null){
			List<?> vhmModeList = QueryUtil.executeQuery("select modeType from "+HmStartConfig.class.getSimpleName(), null,
					new FilterParams("owner", this.getOwner()));
			short vhmMode;
			if(vhmModeList != null && !vhmModeList.isEmpty()){
				vhmMode = (Short)vhmModeList.get(0);
			}else{
				vhmMode = HmStartConfig.HM_MODE_FULL;
			}
			deviceInfo = new DeviceInfo(this.hiveApModel, this.deviceType, this.softVer, vhmMode);
			DeviceObj property = DevicePropertyManage.getInstance().getDeviceProperty(this.hiveApModel);
			DevicePropertyManage.getInstance().clone(property, deviceInfo);
			deviceInfo.init();
		}else if(deviceInfo.getHiveApModel() != this.hiveApModel
				|| deviceInfo.getDeviceType() != this.deviceType){
			deviceInfo.setHiveApModel(this.hiveApModel);
			deviceInfo.setDeviceType(this.deviceType);
		}
		return deviceInfo;
	}

	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public static final String DEFAULT_LOCATION = "change_me";

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
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

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String hostName;

	private boolean dhcp = true;

	@Column(length = IP_ADDRESS_LENGTH)
	private String ipAddress;

	@Column(length = IP_ADDRESS_LENGTH)
	private String netmask;

	@Column(length = IP_ADDRESS_LENGTH)
	private String gateway;

	@Column(length = IP_ADDRESS_LENGTH)
	private String cfgIpAddress;

	@Column(length = IP_ADDRESS_LENGTH)
	private String cfgNetmask;

	@Column(length = IP_ADDRESS_LENGTH)
	private String cfgGateway;

	@Column(length = IP_ADDRESS_LENGTH)
	private String capwapLinkIp;

	@Column(length = IP_ADDRESS_LENGTH)
	private String capwapClientIp;

	private int nativeVlan = 0;

	private int mgtVlan = 0;

	private String location;

	// indicate if include topology info in sysLocation
	private boolean includeTopologyInfo = true;

	@Column(length = 14)
	private String serialNumber;

	@Column(length = 12, nullable = false, unique = true)
	private String macAddress;

	public static final short STATUS_NEW = 0;

	public static final short STATUS_MANAGED = 1;

//  public static final short STATUS_FRIENDLY = 2;

//  public static final short STATUS_ROGUE = 3;

	public static final short STATUS_PRECONFIG = 5;

	public static EnumItem[] MANAGED_STATUS_TYPE = MgrUtil.enumItems(
			"enum.managed.status.", new int[] { STATUS_NEW, STATUS_MANAGED });

	private short manageStatus;

	private boolean connected;

	public static final short	CONNECT_DOWN = 0;
	public static final short	CONNECT_UP = 1;
	public static final short	CONNECT_UP_MINOR = 2;
	public static final short	CONNECT_UP_MAJOR = 3;

	private short connectStatus = CONNECT_DOWN;

	//CAPWAP delay time
	private int		delayTime = -1;

	private long disconnChangedTime = -1;

	public static final short ORIGIN_CREATE = 0;

	public static final short ORIGIN_DISCOVERED = 1;

	public static EnumItem[] ORIGIN_TYPE = MgrUtil.enumItems(
			"enum.hiveAp.origin.",
			new int[] { ORIGIN_CREATE, ORIGIN_DISCOVERED });

	private short origin;

	private int signatureVer;

	private int switchChipVersion;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String hardwareRevision;

	@Column(length = 64)
	private String productName;

	@Column(length = 16)
	private String softVer;

	@Column(length = 64)
	private String displayVer;

	@Column(length = 64)
	private String runningHive;

	@Column(length = 20)
	private String adminUser = "admin";

	@Column(length = DEFAULT_STRING_LENGTH)
	private String adminPassword = NmsUtil.getOEMCustomer().getDefaultAPPassword();

	@Column(length = 20)
	private String readOnlyUser;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String readOnlyPassword;

	@Column(length = 20)
	private String cfgAdminUser;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String cfgPassword;

	@Column(length = 20)
	private String cfgReadOnlyUser;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String cfgReadOnlyPassword;

	private long discoveryTime;

	private long connChangedTime;

	private long lastImageTime;

	private long lastCfgTime;

	private long lastAuditTime;

	private long lastSignatureTime;

	private long upTime;

	private byte timeZoneOffset;

	private int countryCode;

	private int regionCode;

	private short severity;

	@Transient
	private int activeClientCount;

	private int configVer;

	//if null or current value not match with value in DB, need do complete update.
	private String completeUpdateTag;

	private int dhcpTimeout = 20;

	private boolean dhcpFallback;

	private boolean addressOnly;

	public static final short HIVEAP_NO_PROVISION = 0;

	public static final short HIVEAP_PROVISION = 1;

	private int provision;

	public static final short HIVEAP_TYPE_MP = 1;

	public static final short HIVEAP_TYPE_PORTAL = 2;

	public static EnumItem[] HIVEAP_TYPE = MgrUtil.enumItems(
			"enum.hiveAp.type.",
			new int[] { HIVEAP_TYPE_MP, HIVEAP_TYPE_PORTAL });

	private short hiveApType;

	public static final short HIVEAP_MODEL_28 = 0;

	public static final short HIVEAP_MODEL_20 = 1;

	public static final short HIVEAP_MODEL_320 = 2;

	public static final short HIVEAP_MODEL_340 = 3;

	public static final short HIVEAP_MODEL_380 = 4;

	public static final short HIVEAP_MODEL_120 = 5;

	public static final short HIVEAP_MODEL_110 = 6;

//	public static final short HIVEAP_MODEL_VI = 7;

	public static final short HIVEAP_MODEL_330 = 8;

	public static final short HIVEAP_MODEL_350 = 9;

	public static final short HIVEAP_MODEL_370 = 20;

	public static final short HIVEAP_MODEL_390 = 21;

	public static final short HIVEAP_MODEL_VPN_GATEWAY_VA = 10;

	public static final short HIVEAP_MODEL_BR100 = 11;

	public static final short HIVEAP_MODEL_170 = 12;

	public static final short HIVEAP_MODEL_BR200 = 13;

	public static final short HIVEAP_MODEL_BR200_WP = 14;

	// public static final short HIVEAP_MODEL_11N = 2;

	public static final short HIVEAP_MODEL_121 = 15;

	public static final short HIVEAP_MODEL_141 = 16;

	public static final short HIVEAP_MODEL_SR24 = 17;

	public static final short HIVEAP_MODEL_SR48 = 18;

	public static final short HIVEAP_MODEL_BR200_LTE_VZ = 19;

	public static final short HIVEAP_MODEL_SR2124P = 22;

	public static final short HIVEAP_MODEL_SR2148P = 23;

	public static final short HIVEAP_MODEL_SR2024P = 24;
	
	public static final short HIVEAP_MODEL_230 = 25;
	
	public static final short HIVEAP_MODEL_VPN_GATEWAY = 26;

	public static EnumItem[] HIVEAP_MODEL = MgrUtil.enumItems(
								Device.NAME, 
								//HiveAP
								HIVEAP_MODEL_20, 
								HIVEAP_MODEL_28,
								HIVEAP_MODEL_120, 
								HIVEAP_MODEL_110, 
								HIVEAP_MODEL_170,
								HIVEAP_MODEL_121, 
								HIVEAP_MODEL_141,
								HIVEAP_MODEL_230,
								HIVEAP_MODEL_320, 
								HIVEAP_MODEL_340,
								HIVEAP_MODEL_330, 
								HIVEAP_MODEL_350,
								HIVEAP_MODEL_370,
								HIVEAP_MODEL_390,
								//CVG
								HIVEAP_MODEL_VPN_GATEWAY_VA, 
								HIVEAP_MODEL_VPN_GATEWAY,
								//BR
								HIVEAP_MODEL_BR100, 
								HIVEAP_MODEL_BR200, 
								HIVEAP_MODEL_BR200_WP,
								HIVEAP_MODEL_BR200_LTE_VZ,
								//Switch
								HIVEAP_MODEL_SR24,
								HIVEAP_MODEL_SR2124P,
								HIVEAP_MODEL_SR2148P,
								HIVEAP_MODEL_SR2024P);

	/* used in auto provision only, please be careful when you need using this one */
	public static EnumItem[] HIVEAP_MODEL_NO_VPN = MgrUtil.enumItems(
					Device.NAME, 
					//HiveAP
					HIVEAP_MODEL_20, 
					HIVEAP_MODEL_28,
					HIVEAP_MODEL_121, 
					HIVEAP_MODEL_141, 
					HIVEAP_MODEL_120, 
					HIVEAP_MODEL_110, 
					HIVEAP_MODEL_170,
					HIVEAP_MODEL_230,
					HIVEAP_MODEL_320, 
					HIVEAP_MODEL_340, 
					HIVEAP_MODEL_330, 
					HIVEAP_MODEL_350,
					HIVEAP_MODEL_370,
					HIVEAP_MODEL_390,
					//BR
					HIVEAP_MODEL_BR100, 
					HIVEAP_MODEL_BR200, 
					HIVEAP_MODEL_BR200_WP,
					HIVEAP_MODEL_BR200_LTE_VZ,
					//Switch
					HIVEAP_MODEL_SR24, 
					HIVEAP_MODEL_SR2124P, 
					HIVEAP_MODEL_SR2148P,
					HIVEAP_MODEL_SR2024P);

	/**
	 * used when wireless+routing is not enabled
	 */
	public static EnumItem[] HIVEAP_MODEL_ONLY_HIVEAP_TYPE = MgrUtil.enumItems(
			"enum.hiveAp.model.", new int[] { 
					HIVEAP_MODEL_20, 
					HIVEAP_MODEL_28,
					HIVEAP_MODEL_110, 
					HIVEAP_MODEL_120, 
					HIVEAP_MODEL_121, 
					HIVEAP_MODEL_141,
					HIVEAP_MODEL_170,
					HIVEAP_MODEL_230,
					HIVEAP_MODEL_320, 
					HIVEAP_MODEL_340,
					HIVEAP_MODEL_330, 
					HIVEAP_MODEL_350,
					HIVEAP_MODEL_370,
					HIVEAP_MODEL_390});
	
	public static EnumItem[] HIVEAP_MODEL_TYPE_BRANCH_ROUTER = MgrUtil.enumItems(
			"enum.hiveAp.model.", new int[] {
					HIVEAP_MODEL_330, 
					HIVEAP_MODEL_350,
					HIVEAP_MODEL_BR100});
	
	public static EnumItem[] HIVEAP_MODEL_TYPE_VPN_GATEWAY = MgrUtil.enumItems(
			"enum.hiveAp.model.", new int[] {
					HIVEAP_MODEL_VPN_GATEWAY_VA,
					HIVEAP_MODEL_VPN_GATEWAY});

	public static EnumItem[] HIVEAP_MODEL_PLANNING = MgrUtil.enumItems(
			"enum.hiveAp.planned.model.", new int[] { 
					//HiveAP
					HIVEAP_MODEL_20, 
					HIVEAP_MODEL_110, 
					HIVEAP_MODEL_120, 
					HIVEAP_MODEL_121, 
					HIVEAP_MODEL_141, 
					HIVEAP_MODEL_170,
					HIVEAP_MODEL_230,
					HIVEAP_MODEL_320, 
					HIVEAP_MODEL_340, 
					HIVEAP_MODEL_330, 
					HIVEAP_MODEL_350,
					HIVEAP_MODEL_370, 
					HIVEAP_MODEL_390,
					//BR
					HIVEAP_MODEL_BR100,
					HIVEAP_MODEL_BR200_WP, 
					HIVEAP_MODEL_BR200_LTE_VZ});

	private short hiveApModel = HIVEAP_MODEL_20;

	public static final short Device_TYPE_HIVEAP = 0;

	public static final short Device_TYPE_BRANCH_ROUTER = 1;

	public static final short Device_TYPE_VPN_GATEWAY = 2;

	public static final short Device_TYPE_VPN_BR = 3;

	public static final short Device_TYPE_SWITCH = 4;

	public static final short DEVICE_CONNECT_DHCP=1;

	public static final short DEVICE_CONNECT_STATIC=2;

	public static final short DEVICE_CONNECT_PPPOE=3;


	public static EnumItem[] DEVICE_TYPE = MgrUtil.enumItems(
			"enum.hiveAp.deviceType.", new int[]{ 
					Device_TYPE_HIVEAP, 
					Device_TYPE_BRANCH_ROUTER,
					Device_TYPE_VPN_GATEWAY, 
					Device_TYPE_VPN_BR, 
					Device_TYPE_SWITCH });

	public static EnumItem[] DEVICE_TYPE_USED_4_FILTER = MgrUtil.enumItems(
			"enum.hiveAp.filter.deviceType.", new int[]{ 
					Device_TYPE_HIVEAP, 
					Device_TYPE_BRANCH_ROUTER,
					Device_TYPE_VPN_GATEWAY,
					Device_TYPE_SWITCH });

	private short deviceType = Device_TYPE_HIVEAP;

	public static final short TUNNEL_THRESHOLD_LOW = 1;

	public static final short TUNNEL_THRESHOLD_MEDIUM = 2;

	public static final short TUNNEL_THRESHOLD_HIGH = 3;

	public static EnumItem[] TUNNEL_THRESHOLD_TYPE = MgrUtil.enumItems(
			"enum.tunnel.threshold.", new int[] { 
					TUNNEL_THRESHOLD_LOW,
					TUNNEL_THRESHOLD_MEDIUM, 
					TUNNEL_THRESHOLD_HIGH });

	private short tunnelThreshold = TUNNEL_THRESHOLD_HIGH;

	public static final short METRIC_TYPE_AGGRESSIVE = 1;

	public static final short METRIC_TYPE_CONSERVATIVE = 2;

	public static final short METRIC_TYPE_NORMAL = 3;

	public static EnumItem[] METRIC_TYPE = MgrUtil.enumItems("enum.metric.",
			new int[] { 
				METRIC_TYPE_AGGRESSIVE, 
				METRIC_TYPE_CONSERVATIVE,
				METRIC_TYPE_NORMAL });

	private short metric = METRIC_TYPE_NORMAL;

	@Range(min = 10, max = 300)
	private int metricInteval = 60;

	private boolean manageUponContact;

	private boolean pending;

	private int pendingIndex;

	private String pendingMsg;

	private boolean pending_user;

	private int pendingIndex_user;

	private String pendingMsg_user;

	private boolean currentDtlsEnable;

	private int currentKeyId;

	//fix bug 28254
	private boolean enabledSameVlan;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String currentPassPhrase;

	private int keyId;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String passPhrase;

	/* indicate how many VlanDhcpServer profiles binds on */
	private int dhcpServerCount;

	public static final short VPN_MARK_NONE = 0;

	public static final short VPN_MARK_SERVER = 1;

	public static final short VPN_MARK_CLIENT = 2;

	public static EnumItem[] VPN_MARK = MgrUtil.enumItems("enum.vpn.mark.",
			new int[] { VPN_MARK_NONE, VPN_MARK_SERVER, VPN_MARK_CLIENT });

	public static EnumItem[] VPN_MARK_WITHOUT_SERVER = MgrUtil.enumItems(
			"enum.vpn.mark.", new int[] { VPN_MARK_NONE, VPN_MARK_CLIENT });

	private short vpnMark = VPN_MARK_NONE;

	private String eth0DeviceId;

	private String eth0PortId;

	private String eth0SystemId;

	private String eth1DeviceId;

	private String eth1PortId;

	private String eth1SystemId;

	private short transferProtocol = BeAPConnectEvent.TRANSFERMODE_UDP;

	private int transferPort;

	@Column(length = 64)
	private String proxyName;

	private int proxyPort;

	@Column(length = 64)
	private String proxyUsername;

	@Column(length = 64)
	private String proxyPassword;

	@Column(length = 64)
	private String classificationTag1;

	@Column(length = 64)
	private String classificationTag2;

	@Column(length = 64)
	private String classificationTag3;

	public static final short DEVICE_OPERATIONMODE_ACCESS = 1;

	public static final short DEVICE_OPERATIONMODE_BACKHAUL = 2;

	public static final short DEVICE_OPERATIONMODE_802Q = 3;


	public static final short DISTRIBUTED_PRIORITY_HIGH = 1;

	public static final short DISTRIBUTED_PRIORITY_DEFAULT = 2;

	public static final short DISTRIBUTED_PRIORITY_DISABLED = 3;

	private short distributedPriority = DISTRIBUTED_PRIORITY_DEFAULT;

	public static EnumItem[] DISTRIBUTED_PRIORITY = MgrUtil.enumItems(
			"enum.hiveAp.distributed.priority.",
			 new int[] {DISTRIBUTED_PRIORITY_HIGH, DISTRIBUTED_PRIORITY_DEFAULT, DISTRIBUTED_PRIORITY_DISABLED});

	public static final short RADIO_MODE_ACCESS_ALL = 1;

	public static final short RADIO_MODE_ACCESS_ONE = 2;

	public static final short RADIO_MODE_BRIDGE = 3;

	public static final short RADIO_MODE_CUSTOMIZE = 4;

	public static final short RADIO_MODE_ACCESS_DUAL = 5;

	public static final short RADIO_MODE_ACCESS_WAN = 6;

	private short radioConfigType = RADIO_MODE_ACCESS_ALL;

	public static final short USE_ETHERNET_BOTH = 0;

	public static final short USE_ETHERNET_AGG0 = 1;

	public static final short USE_ETHERNET_RED0 = 2;

	private short ethConfigType = USE_ETHERNET_BOTH;

	private boolean simulated;

	private boolean enableEthBridge;

	private boolean enableDynamicBandSwitch;


	private int simulateCode;

	private String simulateClientInfo;

	//cvg as ap
	public static final short USE_STATIC_IP = 1;

	public static final short USE_DHCP_FALLBACK = 2;

	public static final short USE_DHCP_WITHOUTFALLBACK = 3;


	// this filed just used for express mode, in enterprise, it's in WLAN policy
	private boolean enableDas; // dynamic airtime schedule

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TEMPLATE_ID")
	private ConfigTemplate configTemplate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RADIUS_SERVER_ID")
	private RadiusOnHiveap radiusServerProfile;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUPPLEMENTAL_CLI_ID")
	private CLIBlob supplementalCLI;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RADIUS_PROXY_ID")
	private RadiusProxy radiusProxyProfile;

	private boolean enabledBrAsRadiusServer=true;
	private boolean enabledBrAsPpskServer=true;
	private boolean enabledOverrideVoipSetting=false;
	private boolean enablePppoe=false;

	// columen for pppoe current status synchronization with BR
	private boolean pppoeEnableCurrent = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PPPOE_AUTH_ID")
	private PPPoE pppoeAuthProfile;

	@Transient
	private boolean enabledOverrideRadiusServer=false;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "VPN_SERVER_ID")
	// private VpnService vpnServerProfile;

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "adminState", column = @Column(name = "ETH0_ADMIN_STATE")),
			@AttributeOverride(name = "operationMode", column = @Column(name = "ETH0_OPERATION_MODE")),
			@AttributeOverride(name = "speed", column = @Column(name = "ETH0_SPEED")),
			@AttributeOverride(name = "duplex", column = @Column(name = "ETH0_DUPLEX")),
			@AttributeOverride(name = "bindInterface", column = @Column(name = "ETH0_BIND_INTERFACE")),
			@AttributeOverride(name = "bindRole", column = @Column(name = "ETH0_BIND_ROLE")),
//			@AttributeOverride(name = "useDefaultSettings", column = @Column(name = "ETH0_USE_DEFAULT_SETTINGS")),
			@AttributeOverride(name = "macLearningEnabled", column = @Column(name = "ETH0_LEARNING_ENABLED")),
			@AttributeOverride(name = "idelTimeout", column = @Column(name = "ETH0_IDEL_TIMEOUT")),
			@AttributeOverride(name = "allowedVlan", column = @Column(name = "ETH0_ALLOWED_VLAN")),
			@AttributeOverride(name = "multiNativeVlan", column = @Column(name = "ETH0_MULTINATIVE_VLAN"))})
	private HiveApEth eth0 = new HiveApEth();

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "adminState", column = @Column(name = "ETH1_ADMIN_STATE")),
			@AttributeOverride(name = "operationMode", column = @Column(name = "ETH1_OPERATION_MODE")),
			@AttributeOverride(name = "speed", column = @Column(name = "ETH1_SPEED")),
			@AttributeOverride(name = "duplex", column = @Column(name = "ETH1_DUPLEX")),
			@AttributeOverride(name = "bindInterface", column = @Column(name = "ETH1_BIND_INTERFACE")),
			@AttributeOverride(name = "bindRole", column = @Column(name = "ETH1_BIND_ROLE")),
//			@AttributeOverride(name = "useDefaultSettings", column = @Column(name = "ETH1_USE_DEFAULT_SETTINGS")),
			@AttributeOverride(name = "macLearningEnabled", column = @Column(name = "ETH1_LEARNING_ENABLED")),
			@AttributeOverride(name = "idelTimeout", column = @Column(name = "ETH1_IDEL_TIMEOUT")),
			@AttributeOverride(name = "allowedVlan", column = @Column(name = "ETH1_ALLOWED_VLAN")),
			@AttributeOverride(name = "multiNativeVlan", column = @Column(name = "ETH1_MULTINATIVE_VLAN")) })
	private HiveApEth eth1 = new HiveApEth();

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "adminState", column = @Column(name = "RED0_ADMIN_STATE")),
			@AttributeOverride(name = "operationMode", column = @Column(name = "RED0_OPERATION_MODE")),
			@AttributeOverride(name = "speed", column = @Column(name = "RED0_SPEED")),
			@AttributeOverride(name = "duplex", column = @Column(name = "RED0_DUPLEX")),
			@AttributeOverride(name = "bindInterface", column = @Column(name = "RED0_BIND_INTERFACE")),
			@AttributeOverride(name = "bindRole", column = @Column(name = "RED0_BIND_ROLE")),
//			@AttributeOverride(name = "useDefaultSettings", column = @Column(name = "RED0_USE_DEFAULT_SETTINGS")),
			@AttributeOverride(name = "macLearningEnabled", column = @Column(name = "RED0_LEARNING_ENABLED")),
			@AttributeOverride(name = "idelTimeout", column = @Column(name = "RED0_IDEL_TIMEOUT")),
			@AttributeOverride(name = "allowedVlan", column = @Column(name = "RED0_ALLOWED_VLAN")),
			@AttributeOverride(name = "multiNativeVlan", column = @Column(name = "RED0_MULTINATIVE_VLAN"))})
	private HiveApEth red0 = new HiveApEth();

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "adminState", column = @Column(name = "AGG0_ADMIN_STATE")),
			@AttributeOverride(name = "operationMode", column = @Column(name = "AGG0_OPERATION_MODE")),
			@AttributeOverride(name = "speed", column = @Column(name = "AGG0_SPEED")),
			@AttributeOverride(name = "duplex", column = @Column(name = "AGG0_DUPLEX")),
			@AttributeOverride(name = "bindInterface", column = @Column(name = "AGG0_BIND_INTERFACE")),
			@AttributeOverride(name = "bindRole", column = @Column(name = "AGG0_BIND_ROLE")),
//			@AttributeOverride(name = "useDefaultSettings", column = @Column(name = "AGG0_USE_DEFAULT_SETTINGS")),
			@AttributeOverride(name = "macLearningEnabled", column = @Column(name = "AGG0_LEARNING_ENABLED")),
			@AttributeOverride(name = "idelTimeout", column = @Column(name = "AGG0_IDEL_TIMEOUT")),
			@AttributeOverride(name = "allowedVlan", column = @Column(name = "AGG0_ALLOWED_VLAN")),
			@AttributeOverride(name = "multiNativeVlan", column = @Column(name = "AGG0_MULTINATIVE_VLAN"))})
	private HiveApEth agg0 = new HiveApEth();

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "adminState", column = @Column(name = "WIFI0_ADMIN_STATE")),
			@AttributeOverride(name = "operationMode", column = @Column(name = "WIFI0_OPERATION_MODE")),
			@AttributeOverride(name = "radioMode", column = @Column(name = "WIFI0_RADIO_MODE")),
			@AttributeOverride(name = "channel", column = @Column(name = "WIFI0_RADIO_CHANNEL")),
			@AttributeOverride(name = "power", column = @Column(name = "WIFI0_RADIO_POWER")) })
	private HiveApWifi wifi0 = new HiveApWifi();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WIFI0_RADIO_PROFILE_ID", nullable = true)
	private RadioProfile wifi0RadioProfile;

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "adminState", column = @Column(name = "WIFI1_ADMIN_STATE")),
			@AttributeOverride(name = "operationMode", column = @Column(name = "WIFI1_OPERATION_MODE")),
			@AttributeOverride(name = "radioMode", column = @Column(name = "WIFI1_RADIO_MODE")),
			@AttributeOverride(name = "channel", column = @Column(name = "WIFI1_RADIO_CHANNEL")),
			@AttributeOverride(name = "power", column = @Column(name = "WIFI1_RADIO_POWER")) })
	private HiveApWifi wifi1 = new HiveApWifi();

	// add from 4.1r1, only used for 330/350 HiveAP 5 GHz
	//private boolean powerSafeMode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WIFI1_RADIO_PROFILE_ID", nullable = true)
	private RadioProfile wifi1RadioProfile;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MAP_CONTAINER_ID", nullable = true)
	private MapContainerNode mapContainer;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "HIVE_AP_STATIC_ROUTE", joinColumns = @JoinColumn(name = "HIVE_AP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<HiveApStaticRoute> staticRoutes = new ArrayList<HiveApStaticRoute>();

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "HIVE_AP_DYNAMIC_ROUTE", joinColumns = @JoinColumn(name = "HIVE_AP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<HiveApDynamicRoute> dynamicRoutes = new ArrayList<HiveApDynamicRoute>();

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "HIVE_AP_IP_ROUTE", joinColumns = @JoinColumn(name = "HIVE_AP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<HiveApIpRoute> ipRoutes = new ArrayList<HiveApIpRoute>();

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "HIVE_AP_INTERNAL_NETWORK", joinColumns = @JoinColumn(name = "HIVE_AP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<HiveApInternalNetwork> internalNetworks = new ArrayList<HiveApInternalNetwork>();

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "HIVE_AP_MULTIPLE_VLAN", joinColumns = @JoinColumn(name = "HIVE_AP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<HiveApMultipleVlan> multipleVlan = new ArrayList<HiveApMultipleVlan>();

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "HIVE_AP_L3CFG_NEIGHBOR", joinColumns = @JoinColumn(name = "HIVE_AP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<HiveApL3cfgNeighbor> l3Neighbors = new ArrayList<HiveApL3cfgNeighbor>();

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "HIVE_AP_LEARNING_MAC", joinColumns = @JoinColumn(name = "HIVE_AP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<HiveApLearningMac> learningMacs = new ArrayList<HiveApLearningMac>();

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "HIVEAP_PREFERRED_SSID", joinColumns = @JoinColumn(name = "HIVE_AP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<HiveApPreferredSsid> wifiClientPreferredSsids = new ArrayList<HiveApPreferredSsid>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CAPWAP_IP_ID", nullable = true)
	private IpAddress capwapIpBind;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CAPWAP_BACKUP_IP_ID", nullable = true)
	private IpAddress capwapBackupIpBind;

	// @ManyToMany(fetch = FetchType.LAZY)
	// @JoinTable(name = "HIVE_AP_IP_TRACK", joinColumns = @JoinColumn(name =
	// "HIVE_AP_ID", nullable = true))
	// private Set<MgmtServiceIPTrack> ipTracks = new
	// HashSet<MgmtServiceIPTrack>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "HIVE_AP_DHCP_SERVER", joinColumns = @JoinColumn(name = "HIVE_AP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<VlanDhcpServer> dhcpServers = new HashSet<VlanDhcpServer>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SCHEDULER_ID", nullable = true)
	private Scheduler scheduler;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "VPN_IP_TRACK_ID", nullable = true)
	private MgmtServiceIPTrack vpnIpTrack;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "LLDPCDP_ID", nullable = true)
	// private LLDPCDPProfile lldpCdp;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ETH0_USER_PROFILE_ID", nullable = true)
	private UserProfile eth0UserProfile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ETH1_USER_PROFILE_ID", nullable = true)
	private UserProfile eth1UserProfile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGG0_USER_PROFILE_ID", nullable = true)
	private UserProfile agg0UserProfile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RED0_USER_PROFILE_ID", nullable = true)
	private UserProfile red0UserProfile;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CONFIGMDM_ID")
    private ConfigTemplateMdm configTemplateMdm;
    private boolean enableMDM =false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "download_info_id")
    private DownloadInfo downloadInfo;

	public boolean isEnableMDM() {
		if(getDeviceType() == Device_TYPE_HIVEAP && getEthConfigType() == HiveAp.USE_ETHERNET_BOTH &&  (getEth0().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS
				|| getEth1().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS) && configTemplateMdm != null){

			if(NmsUtil.compareSoftwareVersion("6.0.0.0",this.getSoftVer())>0 && configTemplateMdm.getMdmType() !=0 ){
				return false;
			}else{
				return enableMDM;
			}

		}
		return false ;
	}

	//fix bug 26357 beginning
	public boolean getEnableForMDMItem(){
		if(getDeviceType() == Device_TYPE_HIVEAP && getEthConfigType() == HiveAp.USE_ETHERNET_BOTH &&  (getEth0().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS
				|| getEth1().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS)){

			if(NmsUtil.compareSoftwareVersion("6.0.0.0",this.getSoftVer())>0 && configTemplateMdm.getMdmType() !=0 ){
				return false;
			}else{
				return enableMDM;
			}

		}
		return false ;
	}
	//fix bug 26357 end

	public void setEnableMDM(boolean enableMDM) {
		this.enableMDM = enableMDM;
	}


	public ConfigTemplateMdm getConfigTemplateMdm() {
		return configTemplateMdm;
	}

	public void setConfigTemplateMdm(ConfigTemplateMdm configTemplateMdm) {
		this.configTemplateMdm = configTemplateMdm;
	}

	// ethernet cwp feature
	private boolean ethCwpEnableEthCwp;

	private boolean ethCwpEnableMacAuth;

	private short ethCwpAuthMethod = Cwp.AUTH_METHOD_PAP;

	private boolean ethCwpLimitUserProfiles;

	private short ethCwpDenyAction = SsidProfile.DENY_ACTION_DISCONNECT;

	private int ethCwpActiveTime = SsidProfile.DEFAULT_ACTION_TIME;

	private boolean ethCwpEnableStriction;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ETHERNET_CWP_ID", nullable = true)
	private Cwp ethCwpCwpProfile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RADIUS_CLIENT_ID", nullable = true)
	private RadiusAssignment ethCwpRadiusClient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEFAULT_ETH_AUTH_USER_PROFILE_ID", nullable = true)
	private UserProfile ethCwpDefaultAuthUserProfile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEFAULT_ETH_REG_USER_PROFILE_ID", nullable = true)
	private UserProfile ethCwpDefaultRegUserProfile;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "HIVE_AP_USER_PROFILE", joinColumns = { @JoinColumn(name = "HIVE_AP_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_PROFILE_ID") })
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<UserProfile> ethCwpRadiusUserProfiles = new HashSet<UserProfile>();
	// end
	// disabled SSID Objects stored
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "HIVE_AP_SSID_ALLOCATION", joinColumns = @JoinColumn(name = "HIVE_AP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<HiveApSsidAllocation> disabledSsids = new ArrayList<HiveApSsidAllocation>();

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "HIVE_AP_VIRTUAL_CONNECTION", joinColumns = @JoinColumn(name = "HIVE_AP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<HiveAPVirtualConnection> virtualConnections = new ArrayList<HiveAPVirtualConnection>();

	private boolean IDMProxy = false;
	
	private boolean enableIDMAuthProxy = true;
	
	@Transient
	private boolean changeIDMAuthProxy;			//this column used to device multi-edit

	/* Total time connected with HM */
	@Min(0)
	private long totalConnectTime;

	/* Total times connected to HM */
	@Min(0)
	private long totalConnectTimes;

	/* Indicates if accomplish the reporting to the License Server on the first discovery */
	private boolean discoveryReported;

	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="mapkey")
	@CollectionTable(name = "HIVEAP_DEVICE_INTERFACE", joinColumns = @JoinColumn(name = "HIVEAP_ID", nullable = false))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Map<Long, DeviceInterface> deviceInterfaces = new HashMap<Long, DeviceInterface>();

	private boolean enableVRRP;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SECOND_VPN_GATEWAY_ID", nullable = true)
	private HiveAp secondVPNGateway;

	private boolean enablePreempt = true;

	@Column(length = IP_ADDRESS_LENGTH)
	private String virtualWanIp;

	@Column(length = IP_ADDRESS_LENGTH)
	private String virtualLanIp;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROUTING_PROFILE_ID")
	private RoutingProfile routingProfile;

	private int routeInterval = 60;

	private int maxPowerSource = 44;

	@Transient
	private HiveAp primaryVPNGateway;

	private int vrrpId = -1;

	private int vrrpPriority = -1;

	private int vrrpDelay = 1;

	public static final short USB_CONNECTION_MODEL_NEEDED = 0;
	public static final short USB_CONNECTION_MODEL_ALWAYS = 1;
	public static final short USB_CONNECTION_MODEL_PRIMAYR_WAN = 2;

	private short usbConnectionModel = USB_CONNECTION_MODEL_NEEDED;

	private String ethLanStatus = "1111";

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "HIVE_AP_USB_MODEM", joinColumns = @JoinColumn(name = "HIVE_AP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<USBModemProfile> usbModemList = new ArrayList<USBModemProfile>();

	public static final short USE_AP_HOSTNAME_AS_NAS_IDE = 1;

	public static final short USE_CUSTOMIZED_NAS_IDE = 2;

	private short nasIdentifierType = USE_AP_HOSTNAME_AS_NAS_IDE;

	private String customizedNasIdentifier;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROUTING_POLICY_ID")
	private RoutingPolicy routingPolicy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROUTING_PBR_POLICY_ID")
	private RoutingProfilePolicy routingProfilePolicy;

	public RoutingProfilePolicy getRoutingProfilePolicy() {
		return routingProfilePolicy;
	}

	public void setRoutingProfilePolicy(RoutingProfilePolicy routingProfilePolicy) {
		this.routingProfilePolicy = routingProfilePolicy;
	}

	//qos enable for Chesapeake
	private boolean enableSwitchQosSettings = true;

	//Added from Chesapeake
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FORWARDING_DB_ID", nullable = true)
	private ForwardingDB forwardingDB;

	/*Changed from enable/disable client reporting on Switch*/
	private boolean overrideNetworkPolicySetting;
	//private boolean overrideLldpCdp;


	@Embedded
	private DeviceCVGDepend cvgDPD = new DeviceCVGDepend();

	@Transient
	private String vpnServerName;

	@Transient
	private DeviceInfo deviceProperty;
	public DeviceInfo getDeviceProperty(){
		return deviceProperty;
	}

	// bonjour gatway
	private String priority="";
	@Column(length = 256)
	private String realmName;
	private boolean lockRealmName;

	@Transient
	public boolean isSupportBonjour() {
		return isSupportBonjour(hiveApModel);
	}

	@Transient
	private static boolean isSupportBonjour(short hiveApModel){
		Boolean supportBonjour =AhConstantUtil.isTrueAll(Device.SUPPORTED_BONJOUR, hiveApModel);

		return supportBonjour == null ? false : supportBonjour;
	}

	@Transient
	public static String getDefaultBonjourPriority(short hiveApModel){
		String priority = "";
		if(isSupportBonjour(hiveApModel)){
			//switch (50), br200 (40),CVG (25),AP330/350 (20),AP340/320 (15),AP120/121/141/170 (10),AP110 (5)
			priority = AhConstantUtil.getString(Device.BONJOUR_PRIORITY, hiveApModel);
		}

		return priority;
	}

	@Transient
	private boolean multiChangeLockRealmName=false;

	public boolean isLockRealmName() {
		return lockRealmName;
	}

	public void setLockRealmName(boolean lockRealmName) {
		this.lockRealmName = lockRealmName;
	}

	public String getRealmName() {
		return realmName;
	}

	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	//The PoE mode and PoE parimary is only for AP370/AP390
	public static final short POE_802_3_AUTO = 0;
	public static final short POE_802_3_AF = 1;
	public static final short POE_802_3_AT = 2;

	public static final short POE_PRIMARY_ETH0 = 0;
	public static final short POE_PRIMARY_ETH1 = 1;

	public static EnumItem[] ENUM_SYSTEM_POE_POWER_MODE = MgrUtil.enumItems(
			"enum.system.poe.mode.", new int[] { POE_802_3_AUTO, POE_802_3_AF, POE_802_3_AT });

	public static EnumItem[] ENUM_SYSTEM_POE_PRIMARY_ETH = MgrUtil.enumItems(
			"enum.system.poe.primary.eth.", new int[] { POE_PRIMARY_ETH0, POE_PRIMARY_ETH1 });

	private short poeMode = POE_802_3_AUTO;
	private short poePrimaryEth = POE_PRIMARY_ETH0;

	public short getPoeMode() {
		return poeMode;
	}

	public void setPoeMode(short poeMode) {
		this.poeMode = poeMode;
	}

	public short getPoePrimaryEth() {
		return poePrimaryEth;
	}

	public void setPoePrimaryEth(short poePrimaryEth) {
		this.poePrimaryEth = poePrimaryEth;
	}

	public String getPoePrimaryEthName(){
		if(poePrimaryEth == POE_PRIMARY_ETH1){
			return "eth1";
		}else{
			return "eth0";
		}
	}

	// swich pse
	private boolean enableSwitchPse = true;
	private short maxpowerBudget;
	public static final short MANAGERMENT_TYPE_STATIC=0;
	public static final short MANAGERMENT_TYPE_DYNAMIC=1;
	private short managementType = MANAGERMENT_TYPE_DYNAMIC;
	public static EnumItem[] ENUM_MANAGERMENT_TYPE = MgrUtil.enumItems("enum.pse.management.type.",
			new int[] { MANAGERMENT_TYPE_STATIC, MANAGERMENT_TYPE_DYNAMIC});
	private boolean enableSwitchPriority = true;
	private short powerGuardBand=22;
	private boolean enablePoeLegacy=false;
	private boolean enablePoeLldp=false;

	@Transient
	public static short getDefaultMaxPowerBudget(short hiveApModel){
		short maxPowerBudget = 195;
		if(HiveAp.HIVEAP_MODEL_SR24 == hiveApModel || HiveAp.HIVEAP_MODEL_SR2024P == hiveApModel){
			maxPowerBudget = (short) 195;
		} else if(HiveAp.HIVEAP_MODEL_SR2124P == hiveApModel){
			maxPowerBudget = (short) 408;
		} else if(HiveAp.HIVEAP_MODEL_SR2148P == hiveApModel){
			maxPowerBudget = (short) 779;
		}

		return maxPowerBudget;
	}

	public boolean isEnableSwitchPse() {
		return enableSwitchPse;
	}

	public short getMaxpowerBudget() {
		return maxpowerBudget;
	}

	public short getManagementType() {
		return managementType;
	}

	public boolean isEnableSwitchPriority() {
		return enableSwitchPriority;
	}

	public short getPowerGuardBand() {
		return powerGuardBand;
	}

	public boolean isEnablePoeLegacy() {
		return enablePoeLegacy;
	}

	public void setEnablePoeLegacy(boolean enablePoeLegacy) {
		this.enablePoeLegacy = enablePoeLegacy;
	}

	public boolean isEnablePoeLldp() {
		return enablePoeLldp;
	}

	public void setEnablePoeLldp(boolean enablePoeLldp) {
		this.enablePoeLldp = enablePoeLldp;
	}

	public void setEnableSwitchPse(boolean enableSwitchPse) {
		this.enableSwitchPse = enableSwitchPse;
	}

	public void setMaxpowerBudget(short maxpowerBudget) {
		this.maxpowerBudget = maxpowerBudget;
	}

	public void setManagementType(short managementType) {
		this.managementType = managementType;
	}

	public void setEnableSwitchPriority(boolean enableSwitchPriority) {
		this.enableSwitchPriority = enableSwitchPriority;
	}

	public void setPowerGuardBand(short powerGuardBand) {
		this.powerGuardBand = powerGuardBand;
	}

	public short getNasIdentifierType() {
		return nasIdentifierType;
	}

	public void setNasIdentifierType(short nasIdentifierType) {
		this.nasIdentifierType = nasIdentifierType;
	}

	public String getCustomizedNasIdentifier() {
		return customizedNasIdentifier;
	}

	public void setCustomizedNasIdentifier(String customizedNasIdentifier) {
		this.customizedNasIdentifier = customizedNasIdentifier;
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "HIVE_AP_STORM_CONTROL", joinColumns = @JoinColumn(name = "HIVE_AP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<ConfigTemplateStormControl> stormControlList = new ArrayList<ConfigTemplateStormControl>();

	public List<ConfigTemplateStormControl> getStormControlList() {
		return stormControlList;
	}

	public void setStormControlList(
			List<ConfigTemplateStormControl> stormControlList) {
		this.stormControlList = stormControlList;
	}


	private short switchStormControlMode = ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_BYTE;
	private boolean enableOverrideStormControl = false;

	public boolean isEnableOverrideStormControl() {
		return enableOverrideStormControl;
	}

	public void setEnableOverrideStormControl(boolean enableOverrideStormControl) {
		this.enableOverrideStormControl = enableOverrideStormControl;
	}

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

//	public List<ConfigTemplateStormControl> getStormControlList() {
//		if (stormControls==null) return null;
//		List<ConfigTemplateStormControl> info = new ArrayList<ConfigTemplateStormControl>(stormControls);
//
//		Collections.sort(info, new Comparator<ConfigTemplateStormControl>() {
//	            public int compare(ConfigTemplateStormControl obj1, ConfigTemplateStormControl obj2) {
//	                 return obj1.getInterfaceNum() - obj2.getInterfaceNum();
//	            }
//	        });
//
//		return info;
//	}

	private int interfaceMtu4Mgt0 = 1500;

	private int interfaceMtu4Ethernet = 1500;

	public int getInterfaceMtu4Mgt0() {
		return interfaceMtu4Mgt0;
	}

	public int getInterfaceMtu4Ethernet() {
		return interfaceMtu4Ethernet;
	}

	public void setInterfaceMtu4Mgt0(int interfaceMtu4Mgt0) {
		this.interfaceMtu4Mgt0 = interfaceMtu4Mgt0;
	}

	public void setInterfaceMtu4Ethernet(int interfaceMtu4Ethernet) {
		this.interfaceMtu4Ethernet = interfaceMtu4Ethernet;
	}

	@OneToMany(mappedBy = "hiveAp", cascade={CascadeType.ALL})
	private Set<IgmpPolicy> igmpPolicys = new TreeSet<IgmpPolicy>();


	@OneToMany(mappedBy = "hiveAp", cascade={CascadeType.ALL})
	private Set<MulticastGroup> multicastGroups = new TreeSet<MulticastGroup>();


	public Set<IgmpPolicy> getIgmpPolicys() {
		return igmpPolicys;
	}

	public void setIgmpPolicys(Set<IgmpPolicy> igmpPolicys) {
		this.igmpPolicys = igmpPolicys;
	}

	public Set<MulticastGroup> getMulticastGroups() {
		return multicastGroups;
	}

	public void setMulticastGroups(Set<MulticastGroup> multicastGroups) {
		this.multicastGroups = multicastGroups;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getLabel() {
		return hostName;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Transient
	private boolean selected;

	// 1:display lan Interface, 2: display br port setting
	public static final int MULTI_DISPLAY_LAN=1;
	public static final int MULTI_DISPLAY_BR=2;

	@Transient
	private int multiDisplayType=-1;
	@Transient
	private boolean multiBRPortUSBDisplay=false;
	@Transient
	private boolean multiBRPortLTEDisplay=false;
	@Transient
	private boolean multiDisplayApOnly=true;
	@Transient
	private boolean multiIncludeCvg=false;
	@Transient
	private boolean multiDisplayRealm=true;
	@Transient
	private boolean multiDisplayLocation=true;
	@Transient
	private boolean multiDisplayTxRetry=false;

	@Transient
	private boolean multiIncludeBRAsAp=false;

	public boolean isMultiBRPortUSBDisplay() {
		return multiBRPortUSBDisplay;
	}

	public void setMultiBRPortUSBDisplay(boolean multiBRPortUSBDisplay) {
		this.multiBRPortUSBDisplay = multiBRPortUSBDisplay;
	}

	public boolean isMultiBRPortLTEDisplay() {
		return multiBRPortLTEDisplay;
	}

	public void setMultiBRPortLTEDisplay(boolean multiBRPortLTEDisplay) {
		this.multiBRPortLTEDisplay = multiBRPortLTEDisplay;
	}

	public boolean isMultiIncludeBRAsAp() {
		return multiIncludeBRAsAp;
	}

	public void setMultiIncludeBRAsAp(boolean multiIncludeBRAsAp) {
		this.multiIncludeBRAsAp = multiIncludeBRAsAp;
	}

	public boolean isMultiDisplayTxRetry() {
		return multiDisplayTxRetry;
	}

	public void setMultiDisplayTxRetry(boolean multiDisplayTxRetry) {
		this.multiDisplayTxRetry = multiDisplayTxRetry;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getHostName() {
		return hostName;
	}

	public String getHostNameSubstr() {
		if (hostName==null) {
			return "";
		}
		if (hostName.length()> BaseAction.DISPLAY_LENGTH_IN_GUI_OK) {
			return hostName.substring(0, BaseAction.DISPLAY_LENGTH_IN_GUI) + "...";
		}

		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public boolean isDhcp() {
		return dhcp;
	}

	public void setDhcp(boolean dhcp) {
		this.dhcp = dhcp;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		if ("".equals(ipAddress)) {
			// avoid error when sort by inet function, fix bug 27305
			ipAddress = null;
		}
		this.ipAddress = ipAddress;
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		if ("".equals(netmask)) {
			// avoid error when sort by inet function, fix bug 27305
			netmask = null;
		}
		this.netmask = netmask;
	}

	public List<HiveAPVirtualConnection> getVirtualConnections() {
		return virtualConnections;
	}

	public void setVirtualConnections(List<HiveAPVirtualConnection> virtualConnections) {
		this.virtualConnections = virtualConnections;
	}

	public boolean isIDMProxy() {
		return IDMProxy;
	}

	public void setIDMProxy(boolean iDMProxy) {
		IDMProxy = iDMProxy;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		if ("".equals(gateway)) {
			// avoid error when sort by inet function, fix bug 27305
			gateway = null;
		}
		this.gateway = gateway;
	}

	public String getCfgIpAddress() {
		return cfgIpAddress;
	}

	public void setCfgIpAddress(String cfgIpAddress) {
		if (cfgIpAddress!=null && cfgIpAddress.isEmpty()) {
			this.cfgIpAddress = null;
		} else {
			this.cfgIpAddress = cfgIpAddress;
		}
	}

	public String getCfgNetmask() {
		return cfgNetmask;
	}

	public void setCfgNetmask(String cfgNetmask) {
		if (cfgNetmask!=null && cfgNetmask.isEmpty()) {
			this.cfgNetmask = null;
		} else {
			this.cfgNetmask = cfgNetmask;
		}
	}

	public String getCfgGateway() {
		return cfgGateway;
	}

	public void setCfgGateway(String cfgGateway) {
		if (cfgGateway!=null && cfgGateway.isEmpty()) {
			this.cfgGateway = null;
		} else {
			this.cfgGateway = cfgGateway;
		}
	}

	public int getNativeVlan() {
		return nativeVlan;
	}

	public void setNativeVlan(int nativeVlan) {
		this.nativeVlan = nativeVlan;
	}

	public int getMgtVlan() {
		return mgtVlan;
	}

	public void setMgtVlan(int mgtVlan) {
		this.mgtVlan = mgtVlan;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isIncludeTopologyInfo() {
		return includeTopologyInfo;
	}

	public void setIncludeTopologyInfo(boolean includeTopologyInfo) {
		this.includeTopologyInfo = includeTopologyInfo;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getMacAddressFormat(){
		if(macAddress == null || "".equals(macAddress)){
			return "";
		}else{
			String resStr = "";
			for(int i=0; i<macAddress.length(); i++){
				if(i+2 >= macAddress.length()){
					resStr += macAddress.substring(i);
				}else{
					resStr += macAddress.substring(i, i+2);
					resStr += ":";
				}
				i++;
			}
			return resStr;
		}
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public short getManageStatus() {
		return manageStatus;
	}

	public void setManageStatus(short manageStatus) {
		this.manageStatus = manageStatus;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
		if(this.connected)
			this.connectStatus = CONNECT_UP;
		else
			this.connectStatus = CONNECT_DOWN;
	}

	public short getConnectStatus() {
		return connectStatus;
	}

	public void setConnectStatus(short connectStatus) {
		this.connectStatus = connectStatus;
		connected = (this.connectStatus != CONNECT_DOWN);
		if(connected){
			disconnChangedTime = -1;
		}
	}

	public int getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}

	public long getDisconnChangedTime() {
		return disconnChangedTime;
	}

	public void setDisconnChangedTime(long disconnChangedTime) {
		this.disconnChangedTime = disconnChangedTime;
	}

	public String getDisconnChangedTimeStr(){
		if(disconnChangedTime <= 0){
			return "";
		}else{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return formatter.format(new Date(disconnChangedTime));
		}
	}

	public short getOrigin() {
		return origin;
	}

	public void setOrigin(short origin) {
		this.origin = origin;
	}

	public int getSignatureVer() {
		return signatureVer;
	}

	public void setSignatureVer(int signatureVer) {
		this.signatureVer = signatureVer;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getSoftVer() {
		return softVer;
	}

	public void setSoftVer(String softVer) {
		this.softVer = softVer;
	}

	public String getDisplayVer() {
		return displayVer;
	}

	@Transient
	public String getSoftVerString(){
		if(softVer == null || "".equals(softVer)){
			return "";
		}else if(softVer.length() <7){
			return "";
		}else{
			return softVer.substring(0, 3) + "r" + softVer.substring(4, 5);
		}
	}

	public String getDisplayVerNoBuild(){
		if (displayVer==null) return "";
		return displayVer.replace(" release", "").replace(" build", ".");
	}

	public void setDisplayVer(String displayVer) {
		if (displayVer==null) {
			this.displayVer="";
		} else {
			this.displayVer = displayVer.replace(" release", "").replace(" build", ".");
		}
	}

	public String getRunningHive() {
		return runningHive;
	}

	public void setRunningHive(String runningHive) {
		this.runningHive = runningHive;
	}

	public String getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public String getReadOnlyUser() {
		return readOnlyUser;
	}

	public void setReadOnlyUser(String readOnlyUser) {
		this.readOnlyUser = readOnlyUser;
	}

	public String getReadOnlyPassword() {
		return readOnlyPassword;
	}

	public void setReadOnlyPassword(String readOnlyPassword) {
		this.readOnlyPassword = readOnlyPassword;
	}

	public long getDiscoveryTime() {
		return discoveryTime;
	}

	public void setDiscoveryTime(long discoveryTime) {
		this.discoveryTime = discoveryTime;
	}

	public long getConnChangedTime() {
		return connChangedTime;
	}

	public void setConnChangedTime(long connChangedTime) {
		this.connChangedTime = connChangedTime;
	}

	public long getLastImageTime() {
		return lastImageTime;
	}

	public void setLastImageTime(long lastImageTime) {
		this.lastImageTime = lastImageTime;
	}

	public long getLastCfgTime() {
		return lastCfgTime;
	}

	public void setLastCfgTime(long lastCfgTime) {
		this.lastCfgTime = lastCfgTime;
	}

	public long getLastSignatureTime() {
		return lastSignatureTime;
	}

	public void setLastSignatureTime(long lastSignatureTime) {
		this.lastSignatureTime = lastSignatureTime;
	}

	public int getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(int countryCode) {
		this.countryCode = countryCode;
	}

	public int getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(int regionCode) {
		this.regionCode = regionCode;
	}

	public short getTunnelThreshold() {
		return tunnelThreshold;
	}

	public short getHiveApType() {
		return hiveApType;
	}

	public void setHiveApType(short hiveApType) {
		this.hiveApType = hiveApType;
	}

	public short getHiveApModel() {
		return hiveApModel;
	}

	public String getDeviceModelName(){
		for(int i=0; i<HIVEAP_MODEL.length; i++){
			if(HIVEAP_MODEL[i].getKey() == hiveApModel){
				return HIVEAP_MODEL[i].getValue();
			}
		}
		return "None";
	}

	public static String getDeviceModelName(short apModel){
		for(int i=0; i<HIVEAP_MODEL.length; i++){
			if(HIVEAP_MODEL[i].getKey() == apModel){
				return HIVEAP_MODEL[i].getValue();
			}
		}
		return "None";
	}

	public String getDeviceTypeName(){
		for(int i=0; i<DEVICE_TYPE.length; i++){
			if(DEVICE_TYPE[i].getKey() == this.deviceType){
				return DEVICE_TYPE[i].getValue();
			}
		}
		return "None";
	}

	public void setHiveApModel(short hiveApModel) {
		this.hiveApModel = hiveApModel;
	}

	public short getDeviceType(){
		return this.deviceType;
	}

	public void setDeviceType(short deviceType){
		this.deviceType = deviceType;
	}

	public String getHiveApModelString(){
		return MgrUtil.getEnumString("enum.hiveAp.model." + hiveApModel);
	}

	public void setTunnelThreshold(short tunnelThreshold) {
		this.tunnelThreshold = tunnelThreshold;
	}

	public boolean isManageUponContact() {
		return manageUponContact;
	}

	public void setManageUponContact(boolean manageUponContact) {
		this.manageUponContact = manageUponContact;
	}

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	public int getKeyId() {
		return keyId;
	}

	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}

	public boolean isCurrentDtlsEnable() {
		return currentDtlsEnable;
	}

	public void setCurrentDtlsEnable(boolean currentDtlsEnable) {
		this.currentDtlsEnable = currentDtlsEnable;
	}

	public int getCurrentKeyId() {
		return currentKeyId;
	}

	public void setCurrentKeyId(int currentKeyId) {
		this.currentKeyId = currentKeyId;
	}

	public String getCurrentPassPhrase() {
		return currentPassPhrase;
	}

	public void setCurrentPassPhrase(String currentPassPhrase) {
		this.currentPassPhrase = currentPassPhrase;
	}

	public String getPassPhrase() {
		return passPhrase;
	}

	public void setPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase;
	}

	public HiveApEth getEth0() {
		return eth0;
	}

	public void setEth0(HiveApEth eth0) {
		this.eth0 = eth0;
	}

	public HiveApEth getEth1() {
		return eth1;
	}

	public void setEth1(HiveApEth eth1) {
		this.eth1 = eth1;
	}

	public HiveApEth getRed0() {
		return red0;
	}

	public void setRed0(HiveApEth red0) {
		this.red0 = red0;
	}

	public HiveApEth getAgg0() {
		return agg0;
	}

	public void setAgg0(HiveApEth agg0) {
		this.agg0 = agg0;
	}

	public HiveApWifi getWifi0() {
		return wifi0;
	}

	public void setWifi0(HiveApWifi wifi0) {
		this.wifi0 = wifi0;
	}

	public HiveApWifi getWifi1() {
		return wifi1;
	}

	public void setWifi1(HiveApWifi wifi1) {
		this.wifi1 = wifi1;
	}

	public ConfigTemplate getConfigTemplate() {
		return configTemplate;
	}

	public void setConfigTemplate(ConfigTemplate configTemplate) {
		this.configTemplate = configTemplate;
	}

	public MapContainerNode getMapContainer() {
		return mapContainer;
	}

	public void setMapContainer(MapContainerNode mapContainer) {
		this.mapContainer = mapContainer;
	}

	public RadiusProxy getRadiusProxyProfile() {
		return radiusProxyProfile;
	}

	public void setRadiusProxyProfile(RadiusProxy radiusProxyProfile) {
		this.radiusProxyProfile = radiusProxyProfile;
	}

	public RadiusOnHiveap getRadiusServerProfile() {
		return radiusServerProfile;
	}

	public void setRadiusServerProfile(RadiusOnHiveap radiusServerProfile) {
		this.radiusServerProfile = radiusServerProfile;
	}

	public List<HiveApStaticRoute> getStaticRoutes() {
		return staticRoutes;
	}

	public void setStaticRoutes(List<HiveApStaticRoute> staticRoutes) {
		this.staticRoutes = staticRoutes;
	}

	public List<HiveApL3cfgNeighbor> getL3Neighbors() {
		return l3Neighbors;
	}

	public void setL3Neighbors(List<HiveApL3cfgNeighbor> neighbors) {
		l3Neighbors = neighbors;
	}

	public List<HiveApLearningMac> getLearningMacs() {
		return learningMacs;
	}

	public void setLearningMacs(List<HiveApLearningMac> learningMacs) {
		this.learningMacs = learningMacs;
	}

	public RadioProfile getWifi0RadioProfile() {
		return wifi0RadioProfile;
	}

	public void setWifi0RadioProfile(RadioProfile wifi0RadioProfile) {
		this.wifi0RadioProfile = wifi0RadioProfile;
	}

	public RadioProfile getWifi1RadioProfile() {
		return wifi1RadioProfile;
	}

	public void setWifi1RadioProfile(RadioProfile wifi1RadioProfile) {
		this.wifi1RadioProfile = wifi1RadioProfile;
	}

	public short getTransferProtocol() {
		return transferProtocol;
	}

	public void setTransferProtocol(short transferProtocol) {
		this.transferProtocol = transferProtocol;
	}

	public int getTransferPort() {
		return transferPort;
	}

	public void setTransferPort(int transferPort) {
		this.transferPort = transferPort;
	}

	public String getProxyName() {
		return proxyName;
	}

	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public String getClassificationTag1() {
		return classificationTag1;
	}

	public void setClassificationTag1(String classificationTag1) {
		this.classificationTag1 = classificationTag1;
	}

	public String getClassificationTag2() {
		return classificationTag2;
	}

	public void setClassificationTag2(String classificationTag2) {
		this.classificationTag2 = classificationTag2;
	}

	public String getClassificationTag3() {
		return classificationTag3;
	}

	public void setClassificationTag3(String classificationTag3) {
		this.classificationTag3 = classificationTag3;
	}

	public short getDistributedPriority(){
		return this.distributedPriority;
	}

	public void setDistributedPriority(short distributedPriority){
		this.distributedPriority = distributedPriority;
	}

	public short getRadioConfigType() {
		if(radioConfigType == HiveAp.RADIO_MODE_ACCESS_DUAL
				&& NmsUtil.compareSoftwareVersion("4.0.1.0", this.getSoftVer()) > 0){
			return HiveAp.RADIO_MODE_ACCESS_ALL;
		}else{
			return radioConfigType;
		}
	}

	public void setRadioConfigType(short radioConfigType) {
		this.radioConfigType = radioConfigType;
	}

	public short getEthConfigType() {
		return ethConfigType;
	}

	public void setEthConfigType(short ethConfigType) {
		this.ethConfigType = ethConfigType;
	}

	public boolean isSimulated() {
		return simulated;
	}

	public void setSimulated(boolean simulated) {
		this.simulated = simulated;
	}

	public int getSimulateCode() {
		return simulateCode;
	}

	public void setSimulateCode(int simulateCode) {
		this.simulateCode = simulateCode;
	}

	public String getSimulateClientInfo() {
		return simulateClientInfo;
	}

	public void setSimulateClientInfo(String simulateClientInfo) {
		this.simulateClientInfo = simulateClientInfo;
	}

	public boolean isEnableDas() {
		return enableDas;
	}

	public void setEnableDas(boolean enableDas) {
		this.enableDas = enableDas;
	}

	public String getCfgAdminUser() {
		return cfgAdminUser;
	}

	public void setCfgAdminUser(String cfgAdminUser) {
		this.cfgAdminUser = cfgAdminUser;
	}

	public String getCfgPassword() {
		return cfgPassword;
	}

	public void setCfgPassword(String cfgPassword) {
		this.cfgPassword = cfgPassword;
	}

	public String getCfgReadOnlyUser() {
		return cfgReadOnlyUser;
	}

	public void setCfgReadOnlyUser(String cfgReadOnlyUser) {
		this.cfgReadOnlyUser = cfgReadOnlyUser;
	}

	public String getCfgReadOnlyPassword() {
		return cfgReadOnlyPassword;
	}

	public void setCfgReadOnlyPassword(String cfgReadOnlyPassword) {
		this.cfgReadOnlyPassword = cfgReadOnlyPassword;
	}

	public short getMetric() {
		return metric;
	}

	public void setMetric(short metric) {
		this.metric = metric;
	}

	public int getMetricInteval() {
		return metricInteval;
	}

	public void setMetricInteval(int metricInteval) {
		this.metricInteval = metricInteval;
	}

	public List<HiveApDynamicRoute> getDynamicRoutes() {
		return dynamicRoutes;
	}

	public void setDynamicRoutes(List<HiveApDynamicRoute> dynamicRoutes) {
		this.dynamicRoutes = dynamicRoutes;
	}

	public List<HiveApIpRoute> getIpRoutes() {
		return ipRoutes;
	}

	public void setIpRoutes(List<HiveApIpRoute> ipRoutes) {
		this.ipRoutes = ipRoutes;
	}

	public short getSeverity() {
		return severity;
	}

	public void setSeverity(short severity) {
		this.severity = severity;
	}

	public int getActiveClientCount() {
		return activeClientCount;
	}

	public void setActiveClientCount(int activeClientCount) {
		this.activeClientCount = activeClientCount;
	}

	public int getConfigVer() {
		return configVer;
	}

	public void setConfigVer(int configVer) {
		this.configVer = configVer;
	}

	public int getDhcpTimeout() {
		return dhcpTimeout;
	}

	public void setDhcpTimeout(int dhcpTimeout) {
		this.dhcpTimeout = dhcpTimeout;
	}

	public boolean isDhcpFallback() {
		return dhcpFallback;
	}

	public void setDhcpFallback(boolean dhcpFallback) {
		this.dhcpFallback = dhcpFallback;
	}

	public int getProvision() {
		return provision;
	}

	public void setProvision(int provision) {
		this.provision = provision;
	}

	public boolean isAddressOnly() {
		return addressOnly;
	}

	public void setAddressOnly(boolean addressOnly) {
		this.addressOnly = addressOnly;
	}

	public int getDhcpServerCount() {
		return dhcpServerCount;
	}

	public void setDhcpServerCount(int dhcpServerCount) {
		this.dhcpServerCount = dhcpServerCount;
	}

	public short getVpnMark() {
		return vpnMark;
	}

	public void setVpnMark(short vpnMark) {
		this.vpnMark = vpnMark;
	}

	public String getEth0DeviceId() {
		return eth0DeviceId;
	}

	public void setEth0DeviceId(String eth0DeviceId) {
		this.eth0DeviceId = eth0DeviceId;
	}

	public String getEth0PortId() {
		return eth0PortId;
	}

	public void setEth0PortId(String eth0PortId) {
		this.eth0PortId = eth0PortId;
	}

	public String getEth1DeviceId() {
		return eth1DeviceId;
	}

	public void setEth1DeviceId(String eth1DeviceId) {
		this.eth1DeviceId = eth1DeviceId;
	}

	public String getEth1PortId() {
		return eth1PortId;
	}

	public void setEth1PortId(String eth1PortId) {
		this.eth1PortId = eth1PortId;
	}

	public String getEth0SystemId() {
		return eth0SystemId;
	}

	public void setEth0SystemId(String eth0SystemId) {
		this.eth0SystemId = eth0SystemId;
	}

	public String getEth1SystemId() {
		return eth1SystemId;
	}

	public void setEth1SystemId(String eth1SystemId) {
		this.eth1SystemId = eth1SystemId;
	}

	public IpAddress getCapwapIpBind() {
		return capwapIpBind;
	}

	public void setCapwapIpBind(IpAddress capwapIpBind) {
		this.capwapIpBind = capwapIpBind;
	}

	public IpAddress getCapwapBackupIpBind() {
		return capwapBackupIpBind;
	}

	public void setCapwapBackupIpBind(IpAddress capwapBackupIpBind) {
		this.capwapBackupIpBind = capwapBackupIpBind;
	}

	public int getPendingIndex() {
		return pendingIndex;
	}

	public void setPendingIndex(int pendingIndex) {
		this.pendingIndex = pendingIndex;
	}

	public String getPendingMsg() {
		return pendingMsg;
	}

	public void setPendingMsg(String pendingMsg) {
		this.pendingMsg = pendingMsg;
	}

	public boolean isPending_user() {
		return pending_user;
	}

	public void setPending_user(boolean pending_user) {
		this.pending_user = pending_user;
	}

	public int getPendingIndex_user() {
		return pendingIndex_user;
	}

	public void setPendingIndex_user(int pendingIndex_user) {
		this.pendingIndex_user = pendingIndex_user;
	}

	public String getPendingMsg_user() {
		return pendingMsg_user;
	}

	public void setPendingMsg_user(String pendingMsg_user) {
		this.pendingMsg_user = pendingMsg_user;
	}

	// public Set<MgmtServiceIPTrack> getIpTracks() {
	// return ipTracks;
	// }
	//
	// public void setIpTracks(Set<MgmtServiceIPTrack> ipTracks) {
	// this.ipTracks = ipTracks;
	// }

	public Set<VlanDhcpServer> getDhcpServers() {
		return dhcpServers;
	}

	public void setDhcpServers(Set<VlanDhcpServer> dhcpServers) {
		this.dhcpServers = dhcpServers;
	}

	public String getCapwapLinkIp() {
		return capwapLinkIp;
	}

	public void setCapwapLinkIp(String capwapLinkIp) {
		this.capwapLinkIp = capwapLinkIp;
	}

	public String getCapwapClientIp() {
		return capwapClientIp;
	}

	public void setCapwapClientIp(String capwapClientIp) {
		if ("".equals(capwapClientIp)) {
			// avoid error when sort by inet function, fix bug 27305
			capwapClientIp = "0.0.0.0";
		}
		this.capwapClientIp = capwapClientIp;
	}

	public long getLastAuditTime() {
		return lastAuditTime;
	}

	public void setLastAuditTime(long lastAuditTime) {
		this.lastAuditTime = lastAuditTime;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public long getUpTime() {
		return upTime;
	}

	public void setUpTime(long upTime) {
		this.upTime = upTime;
	}

	public byte getTimeZoneOffset() {
		return timeZoneOffset;
	}

	public void setTimeZoneOffset(byte timeZoneOffset) {
		this.timeZoneOffset = timeZoneOffset;
	}

	// public LLDPCDPProfile getLldpCdp() {
	// return lldpCdp;
	// }
	//
	// public void setLldpCdp(LLDPCDPProfile lldpCdp) {
	// this.lldpCdp = lldpCdp;
	// }

	public UserProfile getEth0UserProfile() {
		return eth0UserProfile;
	}

	public void setEth0UserProfile(UserProfile eth0UserProfile) {
		this.eth0UserProfile = eth0UserProfile;
	}

	public UserProfile getEth1UserProfile() {
		return eth1UserProfile;
	}

	public void setEth1UserProfile(UserProfile eth1UserProfile) {
		this.eth1UserProfile = eth1UserProfile;
	}

	public UserProfile getAgg0UserProfile() {
		return agg0UserProfile;
	}

	public void setAgg0UserProfile(UserProfile agg0UserProfile) {
		this.agg0UserProfile = agg0UserProfile;
	}

	public UserProfile getRed0UserProfile() {
		return red0UserProfile;
	}

	public void setRed0UserProfile(UserProfile red0UserProfile) {
		this.red0UserProfile = red0UserProfile;
	}

	public boolean isEthCwpEnableEthCwp() {
		return ethCwpEnableEthCwp;
	}

	public void setEthCwpEnableEthCwp(boolean ethCwpEnableEthCwp) {
		this.ethCwpEnableEthCwp = ethCwpEnableEthCwp;
	}

	public boolean isEthCwpEnableMacAuth() {
		return ethCwpEnableMacAuth;
	}

	public void setEthCwpEnableMacAuth(boolean ethCwpEnableMacAuth) {
		this.ethCwpEnableMacAuth = ethCwpEnableMacAuth;
	}

	public short getEthCwpAuthMethod() {
		return ethCwpAuthMethod;
	}

	public void setEthCwpAuthMethod(short ethCwpAuthMethod) {
		this.ethCwpAuthMethod = ethCwpAuthMethod;
	}

	public boolean isEthCwpLimitUserProfiles() {
		return ethCwpLimitUserProfiles;
	}

	public void setEthCwpLimitUserProfiles(boolean ethCwpLimitUserProfiles) {
		this.ethCwpLimitUserProfiles = ethCwpLimitUserProfiles;
	}

	public short getEthCwpDenyAction() {
		return ethCwpDenyAction;
	}

	public void setEthCwpDenyAction(short ethCwpDenyAction) {
		this.ethCwpDenyAction = ethCwpDenyAction;
	}

	public int getEthCwpActiveTime() {
		return ethCwpActiveTime;
	}

	public void setEthCwpActiveTime(int ethCwpActiveTime) {
		this.ethCwpActiveTime = ethCwpActiveTime;
	}

	public boolean isEthCwpEnableStriction() {
		return ethCwpEnableStriction;
	}

	public void setEthCwpEnableStriction(boolean ethCwpEnableStriction) {
		this.ethCwpEnableStriction = ethCwpEnableStriction;
	}

	public Cwp getEthCwpCwpProfile() {
		return ethCwpCwpProfile;
	}

	public void setEthCwpCwpProfile(Cwp ethCwpCwpProfile) {
		this.ethCwpCwpProfile = ethCwpCwpProfile;
	}

	public RadiusAssignment getEthCwpRadiusClient() {
		return ethCwpRadiusClient;
	}

	public void setEthCwpRadiusClient(RadiusAssignment ethCwpRadiusClient) {
		this.ethCwpRadiusClient = ethCwpRadiusClient;
	}

	public UserProfile getEthCwpDefaultAuthUserProfile() {
		return ethCwpDefaultAuthUserProfile;
	}

	public void setEthCwpDefaultAuthUserProfile(
			UserProfile ethCwpDefaultAuthUserProfile) {
		this.ethCwpDefaultAuthUserProfile = ethCwpDefaultAuthUserProfile;
	}

	public UserProfile getEthCwpDefaultRegUserProfile() {
		return ethCwpDefaultRegUserProfile;
	}

	public void setEthCwpDefaultRegUserProfile(
			UserProfile ethCwpDefaultRegUserProfile) {
		this.ethCwpDefaultRegUserProfile = ethCwpDefaultRegUserProfile;
	}

	public Set<UserProfile> getEthCwpRadiusUserProfiles() {
		return ethCwpRadiusUserProfiles;
	}

	public void setEthCwpRadiusUserProfiles(
			Set<UserProfile> ethCwpRadiusUserProfiles) {
		this.ethCwpRadiusUserProfiles = ethCwpRadiusUserProfiles;
	}

	public List<HiveApSsidAllocation> getDisabledSsids() {
		return disabledSsids;
	}

	public void setDisabledSsids(List<HiveApSsidAllocation> disabledSsids) {
		this.disabledSsids = disabledSsids;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof HiveAp
				&& (null == id ? super.equals(other) : id
						.equals(((HiveAp) other).getId()));
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}

	/*
	 * Tools functions
	 */
	@Transient
	private MapLeafNode mapLeafNode;

	@Transient
	private boolean isQueried;

	public MapLeafNode findMapLeafNode() {
		if (!isQueried) {
			mapLeafNode = QueryUtil.findBoByAttribute(MapLeafNode.class,
					"hiveAp.id", this.getId(), new QueryLazyBo());
			isQueried = true;
		}
		return mapLeafNode;
	}

	@Transient
	public String getConfigTemplateName() {
		return configTemplate == null ? "" : configTemplate.getConfigName();
	}

	@Transient
	public long getConfigTemplateId() {
		return configTemplate == null ? 0 : configTemplate.getId();
	}

	@Transient
	public boolean isDefConfigTemplate() {
		return configTemplate == null ? false : configTemplate.getConfigName().equals(BeParaModule.DEFAULT_DEVICE_GROUP_NAME);
	}

	@Transient
	public String getWifi0RadioProfileName() {
		if (isWifi0Available(hiveApModel)) {
			return wifi0RadioProfile == null ? "" : wifi0RadioProfile.getRadioName();
		}
		return "";
	}

	@Transient
	public String getWifi1RadioProfileName() {
		if (isWifi1Available(hiveApModel)) {
			return wifi1RadioProfile == null ? "" : wifi1RadioProfile.getRadioName();
		}
		return "";
	}

	@Transient
	public String getTopologyName() {
		String result="-";
		if(mapContainer != null){
			if(mapContainer.getParentMap() == null || mapContainer.getMapType() == 1){
				result = mapContainer.getMapName();
			} else {
				result = mapContainer.getParentMap().getLabel()+"_"+mapContainer.getMapName();
			}
		}
		return result;
	}

	@Transient
	public String getMapContainerName() {
		String result="";
		if(mapContainer != null){
			result = mapContainer.getMapName();
		}
		return result;
	}

	@Transient
	public String getHiveName() {
		return configTemplate == null ? "" : configTemplate.getHiveProfile()
				.getHiveName();
	}

	@Transient
	public String getVlanName() {
		if(this.deviceType == HiveAp.Device_TYPE_HIVEAP || this.isBranchRouter()){
			if (mgtVlan > 0) {
				return String.valueOf(mgtVlan);
			}
			return configTemplate == null ? "" : configTemplate.getVlan()
					.getVlanName();
		}else if(this.deviceType == HiveAp.Device_TYPE_VPN_GATEWAY){
			// TODO for remove network object in user profile
//			if(this.cvgDPD == null || this.cvgDPD.getMgtNetwork() == null || this.cvgDPD.getMgtNetwork().getVlan() == null){
				return "";
//			}else{
//				return this.cvgDPD.getMgtNetwork().getVlan().getVlanName();
//			}
		}else{
			return configTemplate == null ? "" : configTemplate.getVlan()
					.getVlanName();
		}

	}

	@Transient
	public String getNativeVlanName() {
		if(this.deviceType == HiveAp.Device_TYPE_HIVEAP){
			if (nativeVlan > 0) {
				return String.valueOf(nativeVlan);
			}
			return configTemplate == null ? "" : configTemplate.getVlanNative()
					.getVlanName();
		}else if(this.isBranchRouter()){
			return configTemplate == null ? "" : configTemplate.getVlanNative()
					.getVlanName();
		}else if(this.deviceType == HiveAp.Device_TYPE_VPN_GATEWAY){
			return "N/A";
		}else{
			if (nativeVlan > 0) {
				return String.valueOf(nativeVlan);
			}
			return configTemplate == null ? "" : configTemplate.getVlanNative()
					.getVlanName();
		}

	}

	@Transient
	public boolean isNewHiveAP() {
		return manageStatus == HiveAp.STATUS_NEW;
	}

	@Transient
	public boolean isVpnServer() {
		if(!"None".equals(getVpnServerName())){
			return true;
		}else{
			return configTemplate != null && configTemplate.getVpnService() != null
			&& vpnMark == VPN_MARK_SERVER;
		}
	}

	@Transient
	public boolean isVpnClient() {
		return configTemplate != null && configTemplate.getVpnService() != null
				&& vpnMark == VPN_MARK_CLIENT;
	}

	@Transient
	public String getConnectedString() {
		if (connected) {
			return "<FONT color='green'>"
					+ MgrUtil.getUserMessage("hiveAp.capwapStatus.connected")
					+ "</FONT>";
		} else {
			return "<FONT color='red'><b>"
					+ MgrUtil
							.getUserMessage("hiveAp.capwapStatus.disconnected")
					+ "</FONT>";
		}
	}

	@Transient
	public String getConnectedStringNoColor() {
		if (connected) {
			return MgrUtil.getUserMessage("hiveAp.capwapStatus.connected");
		} else {
			return MgrUtil.getUserMessage("hiveAp.capwapStatus.disconnected");
		}
	}

	@Transient
	public String getOriginString() {
		switch (origin) {
		case ORIGIN_CREATE:
			return "<FONT color='green'>"
					+ MgrUtil.getEnumString("enum.hiveAp.origin." + origin)
					+ "</FONT>";
		case ORIGIN_DISCOVERED:
			return "<FONT color='blue'>"
					+ MgrUtil.getEnumString("enum.hiveAp.origin." + origin)
					+ "</FONT>";
		default:
			return "Unknown";
		}
	}

	@Transient
	public String getSignatureVerString() {
		String signatureVersion = "";
		if (signatureVer != 0) {
			String ipformat = AhDecoder.int2IP(signatureVer);
			signatureVersion = ipformat.substring(0, ipformat.lastIndexOf("."));
		}
		return signatureVersion;
	}

	@Transient
	public String getSignatureVerStringWithPrefix() {
		String signatureVersion = "";
		if (signatureVer != 0) {
			String ipformat = AhDecoder.int2IP(signatureVer);
			signatureVersion = "ver " + ipformat.substring(0, ipformat.lastIndexOf("."));
		}
		return signatureVersion;
	}

	@Transient
	public String getSignatureDPIVerString(){
		String signatureVersion = "";
		if (signatureVer != 0) {
			String ipformat = AhDecoder.int2IP(signatureVer);
			signatureVersion = "v" + ipformat.substring(0, ipformat.indexOf("."));
		}
		return signatureVersion;
	}

	@Transient
	public String getDhcpString() {
		return dhcp ? "Enabled" : "Disabled";
	}

	@Transient
	public String getIpTypeString(){
		return dhcp ? "DHCP" : "Static";
	}

	@Transient
	public String getManageStatusString() {
		switch (manageStatus) {
		case STATUS_MANAGED:
		case STATUS_NEW:
			return MgrUtil.getEnumString("enum.managed.status." + manageStatus);
		default:
			return "Unknown";
		}
	}

	@Transient
	public static String getModelEnumString(short model) {
		String modelString = AhConstantUtil.getString(Device.NAME, model);

		return (modelString == null ? "" : modelString);
	}

	@Transient
	public static String getDeviceEnumString(short deviceModel, short deviceType) {
		switch(deviceModel){
			case HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA:
			case HiveAp.HIVEAP_MODEL_VPN_GATEWAY:	
				return (deviceType == 0 || deviceType == 2) ? MgrUtil.getEnumString("enum.cvg.deviceType." + deviceType) : "";
			default:
				return MgrUtil.getEnumString("enum.hiveAp.deviceType." + deviceType);
		}
	}

	@Transient
	public static String getModelProtocalString(short modelType) {
		switch (modelType) {
		case HIVEAP_MODEL_20:
		case HIVEAP_MODEL_28:
			return "802.11abg";
		case HIVEAP_MODEL_SR24:
		case HIVEAP_MODEL_SR2124P:
		case HIVEAP_MODEL_SR2024P:
		case HIVEAP_MODEL_SR48:
		case HIVEAP_MODEL_SR2148P:
			return "N/A";
		case HIVEAP_MODEL_370:
		case HIVEAP_MODEL_390:
		case HIVEAP_MODEL_230:
			return "802.11ac";
		case HIVEAP_MODEL_110:
		case HIVEAP_MODEL_120:
		case HIVEAP_MODEL_121:
		case HIVEAP_MODEL_141:
		case HIVEAP_MODEL_320:
		case HIVEAP_MODEL_340:
		case HIVEAP_MODEL_380:
		case HIVEAP_MODEL_330:
		case HIVEAP_MODEL_350:
		case HIVEAP_MODEL_BR100:
		case HIVEAP_MODEL_BR200_WP:
		case HIVEAP_MODEL_BR200_LTE_VZ:
		case HIVEAP_MODEL_170:
		default:
			return "802.11n";
		}
	}

	@Transient
	public static void setHiveApRadioModes(HiveAp hiveAp) {
		if (hiveAp == null) {
			return;
		}
		if (hiveAp.is11nHiveAP()) {
			if (null != hiveAp.getWifi0RadioProfile()) {
				short radioMode = hiveAp.getWifi0RadioProfile().getRadioMode();
				if (radioMode == RadioProfile.RADIO_PROFILE_MODE_A) {
					hiveAp.getWifi0().setRadioMode(AhInterface.RADIO_MODE_A);
				} else if (radioMode == RadioProfile.RADIO_PROFILE_MODE_NA) {
					hiveAp.getWifi0().setRadioMode(AhInterface.RADIO_MODE_NA);
				} else if (radioMode == RadioProfile.RADIO_PROFILE_MODE_BG) {
					hiveAp.getWifi0().setRadioMode(AhInterface.RADIO_MODE_BG);
				} else if (radioMode == RadioProfile.RADIO_PROFILE_MODE_NG) {
					hiveAp.getWifi0().setRadioMode(AhInterface.RADIO_MODE_NG);
				}
			} else {
				hiveAp.getWifi0().setRadioMode(AhInterface.RADIO_MODE_NG);
			}
			if (null != hiveAp.getWifi1RadioProfile()) {
				short radioMode = hiveAp.getWifi1RadioProfile().getRadioMode();
				if (radioMode == RadioProfile.RADIO_PROFILE_MODE_A) {
					hiveAp.getWifi1().setRadioMode(AhInterface.RADIO_MODE_A);
				} else if (radioMode == RadioProfile.RADIO_PROFILE_MODE_NA) {
					hiveAp.getWifi1().setRadioMode(AhInterface.RADIO_MODE_NA);
				} else if (radioMode == RadioProfile.RADIO_PROFILE_MODE_BG) {
					hiveAp.getWifi1().setRadioMode(AhInterface.RADIO_MODE_BG);
				} else if (radioMode == RadioProfile.RADIO_PROFILE_MODE_NG) {
					hiveAp.getWifi1().setRadioMode(AhInterface.RADIO_MODE_NG);
				} else if (hiveAp.is11acHiveAP() && radioMode == RadioProfile.RADIO_PROFILE_MODE_AC) {
					hiveAp.getWifi1().setRadioMode(AhInterface.RADIO_MODE_AC);
				}
			} else {
				hiveAp.getWifi1().setRadioMode(AhInterface.RADIO_MODE_NA);
			}
		} else {
			hiveAp.getWifi0().setRadioMode(AhInterface.RADIO_MODE_BG);
			hiveAp.getWifi1().setRadioMode(AhInterface.RADIO_MODE_A);
		}
	}

	@Transient
	public static void setHiveApRadioConfigType(HiveAp hiveAp) {
		if (!hiveAp.isWifi1Available()) {
			if (hiveAp.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS){
				hiveAp.setRadioConfigType(HiveAp.RADIO_MODE_ACCESS_ALL);
			} else if(hiveAp.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL){
				hiveAp.setRadioConfigType(HiveAp.RADIO_MODE_ACCESS_ONE);
			} else if(hiveAp.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_DUAL){
				hiveAp.setRadioConfigType(HiveAp.RADIO_MODE_ACCESS_DUAL);
			}else if(hiveAp.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_SENSOR){
				hiveAp.setRadioConfigType(HiveAp.RADIO_MODE_CUSTOMIZE);
			}
//			if (hiveAp.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS) {
//				hiveAp.setRadioConfigType(HiveAp.RADIO_MODE_ACCESS_DUAL);
//			} else if (hiveAp.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL) {
//				if (hiveAp.getEth0().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL) {
//					hiveAp.setRadioConfigType(HiveAp.RADIO_MODE_CUSTOMIZE);
//				} else {
//					hiveAp.setRadioConfigType(HiveAp.RADIO_MODE_BRIDGE);
//				}
//			}
		} else {
			if (hiveAp.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS
					&& hiveAp.getWifi1().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS) {
				hiveAp.setRadioConfigType(HiveAp.RADIO_MODE_ACCESS_ALL);
			} else if (hiveAp.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS
					&& hiveAp.getWifi1().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL) {
				hiveAp.setRadioConfigType(HiveAp.RADIO_MODE_ACCESS_ONE);
			} else if(hiveAp.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS
					&& hiveAp.getWifi1().getOperationMode() == AhInterface.OPERATION_MODE_DUAL){
				hiveAp.setRadioConfigType(HiveAp.RADIO_MODE_ACCESS_DUAL);
			} else if ((hiveAp.getWifi1().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS && hiveAp
					.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL)
					|| (hiveAp.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL && hiveAp
							.getWifi1().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL)) {
				hiveAp.setRadioConfigType(HiveAp.RADIO_MODE_CUSTOMIZE);
			} else if(hiveAp.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_WAN_ACCESS) {
				hiveAp.setRadioConfigType(HiveAp.RADIO_MODE_ACCESS_WAN);
			}
			else {
				hiveAp.setRadioConfigType(HiveAp.RADIO_MODE_CUSTOMIZE);
			}
		}
	}

	@Transient
	public static void setHiveApEthConfigType(HiveAp hiveAp) {
		short ethconfigtype;
		if (hiveAp.getEth0().getBindInterface() == AhInterface.ETH_BIND_IF_AGG0
				&& hiveAp.getEth1().getBindInterface() == AhInterface.ETH_BIND_IF_AGG0) {
			ethconfigtype = HiveAp.USE_ETHERNET_AGG0;
		} else if (hiveAp.getEth0().getBindInterface() == AhInterface.ETH_BIND_IF_RED0
				&& hiveAp.getEth1().getBindInterface() == AhInterface.ETH_BIND_IF_RED0) {
			ethconfigtype = HiveAp.USE_ETHERNET_RED0;
		} else {
			ethconfigtype = HiveAp.USE_ETHERNET_BOTH;
		}
		hiveAp.setEthConfigType(ethconfigtype);
	}

	@Transient
	private String userTimeZone;

	@Transient
	private HmDomain loginUser;

	public void setLoginUser(HmDomain loginUser){
		this.loginUser = loginUser;
	}

	@Transient
	public String getDiscoveryTimeString() {
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			if (discoveryTime > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(discoveryTime,
							TimeZone.getTimeZone(userTimeZone), loginUser != null ? loginUser : owner);
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(discoveryTime, owner
							.getTimeZone(), loginUser != null ? loginUser : owner);
				}
			}
		}else{
			if (discoveryTime > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(discoveryTime,
							TimeZone.getTimeZone(userTimeZone));
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(discoveryTime, owner
							.getTimeZone());
				}
			}
		}
		return "-------------------";
	}

	@Transient
	public String getLastConfigurationTimeString() {
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			if (lastCfgTime > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(lastCfgTime, TimeZone
							.getTimeZone(userTimeZone), loginUser != null ? loginUser : owner);
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(lastCfgTime, owner
							.getTimeZone(), loginUser != null ? loginUser : owner);
				}
			}
		}else{
			if (lastCfgTime > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(lastCfgTime, TimeZone
							.getTimeZone(userTimeZone));
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(lastCfgTime, owner
							.getTimeZone());
				}
			}
		}
		return "-----";
	}

	@Transient
	public String getLastImageTimeString() {
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			if (lastImageTime > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(lastImageTime,
							TimeZone.getTimeZone(userTimeZone), loginUser != null ? loginUser : owner);
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(lastImageTime, owner
							.getTimeZone(), loginUser != null ? loginUser : owner);
				}
			}
		}else{
			if (lastImageTime > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(lastImageTime,
							TimeZone.getTimeZone(userTimeZone));
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(lastImageTime, owner
							.getTimeZone());
				}
			}
		}
		return "-----";
	}

	@Transient
	public String getLastSignatureTimeString() {
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			if (lastSignatureTime > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(lastSignatureTime,
							TimeZone.getTimeZone(userTimeZone), loginUser != null ? loginUser : owner);
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(lastSignatureTime,
							owner.getTimeZone(), loginUser != null ? loginUser : owner);
				}
			}
		}else{
			if (lastSignatureTime > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(lastSignatureTime,
							TimeZone.getTimeZone(userTimeZone));
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(lastSignatureTime,
							owner.getTimeZone());
				}
			}
		}
		return "-----";
	}

	@Transient
	public String getConnectionTimeString() {
		if (!isConnected()) {
			return "";
		}
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			if (connChangedTime > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(connChangedTime,
							TimeZone.getTimeZone(userTimeZone), loginUser != null ? loginUser : owner);
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(connChangedTime,
							owner.getTimeZone(), loginUser != null ? loginUser : owner);
				}
			}
		}else{
			if (connChangedTime > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(connChangedTime,
							TimeZone.getTimeZone(userTimeZone));
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(connChangedTime,
							owner.getTimeZone());
				}
			}
		}
		return "-----";
	}

	public void setUserTimeZone(String userTimeZone) {
		this.userTimeZone = userTimeZone;
	}

	@Transient
	public String getUpTimeString() {
		if (!isConnected()) {
			return "";
		} else {
			if (upTime > 0) {
				long upDuration = System.currentTimeMillis() - upTime;
				return NmsUtil.transformTime((int) (upDuration / 1000));
			} else {
				return "Unknown";
			}
		}
	}

	@Transient
	public String getHiveApTypeString() {
		switch (hiveApType) {
		case HIVEAP_TYPE_MP:
		case HIVEAP_TYPE_PORTAL:
			if ((deviceType==Device_TYPE_HIVEAP && !isCVGAppliance())
					|| (deviceType == Device_TYPE_BRANCH_ROUTER &&
							hiveApModel==HIVEAP_MODEL_BR100 ||
							hiveApModel==HIVEAP_MODEL_BR200_WP ||
							hiveApModel==HIVEAP_MODEL_BR200_LTE_VZ))  {
				return MgrUtil.getEnumString("enum.hiveAp.type." + hiveApType);
			} else {
				return "";
			}
		default:
			return "Unknown";
		}
	}

	@Transient
	public String getEth0DeviceIdString() {
		if (null == eth0DeviceId || "".equals(eth0DeviceId)) {
			return "-";
		}
		return eth0DeviceId;
	}

	@Transient
	public String getEth0PortIdString() {
		if (null == eth0PortId || "".equals(eth0PortId)) {
			return "-";
		}
		return eth0PortId;
	}

	@Transient
	public String getEth1DeviceIdString() {
		if (null == eth1DeviceId || "".equals(eth1DeviceId)) {
			return "-";
		}
		return eth1DeviceId;
	}

	@Transient
	public String getEth1PortIdString() {
		if (null == eth1PortId || "".equals(eth1PortId)) {
			return "-";
		}
		return eth1PortId;
	}

	@Transient
	public String getEth0SystemIdString() {
		if (null == eth0SystemId || "".equals(eth0SystemId)) {
			return "-";
		}
		return eth0SystemId;
	}

	@Transient
	public String getEth1SystemIdString() {
		if (null == eth1SystemId || "".equals(eth1SystemId)) {
			return "-";
		}
		return eth1SystemId;
	}

	@Transient
	private String severityIcon;

	public String getSeverityIcon() {
		return severityIcon;
	}

	public void setSeverityIcon(String severityIcon) {
		this.severityIcon = severityIcon;
	}

	@Transient
	private String connectionIcon;

	public String getConnectionIcon() {
		return connectionIcon;
	}

	public void setConnectionIcon(String connectionIcon) {
		this.connectionIcon = connectionIcon;
	}

	@Transient
	private String connectionIconEx;

	public String getConnectionIconEx(){
		return this.connectionIconEx;
	}

	public void setConnectionIconEx(String connectionIconEx){
		this.connectionIconEx = connectionIconEx;
	}

	@Transient
	private String dtlsIcon;

	public String getDtlsIcon() {
		return dtlsIcon;
	}

	public void setDtlsIcon(String dtlsIcon) {
		this.dtlsIcon = dtlsIcon;
	}

	@Transient
	private String iconItem1, iconItem2, iconItem3, iconItem4, iconItem5, iconItem6;

	/* In order to show these icons one by one */

	public String getIconItem1() {
		return iconItem1;
	}

	public void setIconItem1(String iconItem1) {
		this.iconItem1 = iconItem1;
	}

	public String getIconItem2() {
		return iconItem2;
	}

	public void setIconItem2(String iconItem2) {
		this.iconItem2 = iconItem2;
	}

	public String getIconItem3() {
		return iconItem3;
	}

	public void setIconItem3(String iconItem3) {
		this.iconItem3 = iconItem3;
	}

	public String getIconItem4() {
		return iconItem4;
	}

	public void setIconItem4(String iconItem4) {
		this.iconItem4 = iconItem4;
	}

	public String getIconItem5() {
		return iconItem5;
	}

	public void setIconItem5(String iconItem5) {
		this.iconItem5 = iconItem5;
	}

	public String getIconItem6() {
		return iconItem6;
	}

	public void setIconItem6(String iconItem6) {
		this.iconItem6 = iconItem6;
	}

	@Transient
	private int clientCount;

	public int getClientCount() {
		return clientCount;
	}

	public void setClientCount(int clientCount) {
		this.clientCount = clientCount;
	}

	@Transient
	private String configIndicationIcon;

	public void setConfigIndicationIcon(String configIndicationIcon) {
		this.configIndicationIcon = configIndicationIcon;
	}

	public String getConfigIndicationIcon() {
		return configIndicationIcon;
	}

	/**
	 * Get the new config version based on current config version.
	 *
	 * @return -
	 */
	@Transient
	public int getNewConfigVerNum() {
		return AhConfigUtil.computeNewConfigVerNum(configVer);
	}

	@Transient
	private HiveApAutoProvision autoProvisioningConfig;

	public HiveApAutoProvision getAutoProvisioningConfig() {
		return autoProvisioningConfig;
	}

	public void setAutoProvisioningConfig(
			HiveApAutoProvision autoProvisioningConfig) {
		this.autoProvisioningConfig = autoProvisioningConfig;
	}

	@Transient
	private Object[] oldHiveApInfos;

	public Object[] getOldHiveApInfos() {
		return oldHiveApInfos;
	}

	public void setOldHiveApInfos(Object[] oldHiveApInfos) {
		this.oldHiveApInfos = oldHiveApInfos;
	}

	/*
	 * Object[]: [<oldMapContainerId>, <oldConnection>, <oldManagedStatus>]
	 */
	@Transient
	public void saveOldHiveApInfo() {
		Long mapContainerId = mapContainer != null ? mapContainer.getId()
				: null;

		oldHiveApInfos = new Object[] { mapContainerId, connected, manageStatus };
		previousConfigVer = configVer;
	}

	@Transient
	public int previousConfigVer;

	public int getPreviousConfigVer() {
		return previousConfigVer;
	}

	public void setPreviousConfigVer(int previousConfigVer) {
		this.previousConfigVer = previousConfigVer;
	}

	@Transient
	private BeAPConnectEvent capwapConnectEvent;

	public BeAPConnectEvent getCapwapConnectEvent() {
		return capwapConnectEvent;
	}

	public void setCapwapConnectEvent(BeAPConnectEvent capwapConnectEvent) {
		this.capwapConnectEvent = capwapConnectEvent;
	}

	@Transient
	private boolean reConnectedByReboot;

	public boolean isReConnectedByReboot() {
		return reConnectedByReboot;
	}

	public void setReConnectedByReboot(boolean reConnectedByReboot) {
		this.reConnectedByReboot = reConnectedByReboot;
	}

	@Transient
	private String simulateNeighborInfo;

	public String getSimulateNeighborInfo() {
		return simulateNeighborInfo;
	}

	public void setSimulateNeighborInfo(String simulateNeighborInfo) {
		this.simulateNeighborInfo = simulateNeighborInfo;
	}

	@Transient
	private String newTransferCapwap;

	public String getNewTransferCapwap() {
		return newTransferCapwap;
	}

	public void setNewTransferCapwap(String newTransferCapwap) {
		this.newTransferCapwap = newTransferCapwap;
	}

	@Transient
	public String getCountryName() {
		return CountryCode.getCountryName(countryCode);
	}

	/**
	 * sensor mode doesn't support ACSP neighbor in fuji
	 * @param isA 2.4G or 5G channel
	 * @return
	 */
	public boolean isSupportAcsp(boolean isA) {
		if (HiveAp.isWifi0Available(this.hiveApModel)) {
			RadioProfile wifi0Profile = this.getWifi0RadioProfile();
			int wifi0Operation = this.getWifi0().getOperationMode();
			short wifi0Mode = this.getWifi0().getRadioMode();
			if (null != wifi0Profile
					&& wifi0Operation == AhInterface.OPERATION_MODE_SENSOR) {
				if (isA
						&& (wifi0Mode == RadioProfile.RADIO_PROFILE_MODE_A
								|| wifi0Mode == RadioProfile.RADIO_PROFILE_MODE_NA
								|| wifi0Mode == RadioProfile.RADIO_PROFILE_MODE_AC)) {
					// 5G
					return false;
				} else if (!isA
						&& (wifi0Mode == RadioProfile.RADIO_PROFILE_MODE_BG || wifi0Mode == RadioProfile.RADIO_PROFILE_MODE_NG)) {
					// 2.4G
					return false;
				}
			}
		}
		if (HiveAp.isWifi1Available(this.hiveApModel)) {
			RadioProfile wifi1Profile = this.getWifi1RadioProfile();
			int wifi1Operation = this.getWifi1().getOperationMode();
			short wifi1Mode = this.getWifi1().getRadioMode();
			if (null != wifi1Profile
					&& wifi1Operation == AhInterface.OPERATION_MODE_SENSOR) {
				if (isA
						&& (wifi1Mode == RadioProfile.RADIO_PROFILE_MODE_A
								|| wifi1Mode == RadioProfile.RADIO_PROFILE_MODE_NA
								|| wifi1Mode == RadioProfile.RADIO_PROFILE_MODE_AC)) {
					// 5G
					return false;
				} else if (!isA
						&& (wifi1Mode == RadioProfile.RADIO_PROFILE_MODE_BG || wifi1Mode == RadioProfile.RADIO_PROFILE_MODE_NG)) {
					// 2.4G
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * static method check for hiveAP is 11n box or not.
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean is11nHiveAP(short hiveApModel) {
		Boolean is11n = AhConstantUtil.isTrueAll(Device.IS_11n, hiveApModel);

		return is11n == null ? false : is11n;
	}

	/*
	 * method check for itself is 11n box or not.
	 */
	public boolean is11nHiveAP() {
		return is11nHiveAP(hiveApModel);
	}
	
	//Only AP370/AP390/AP230 support PoE mode
	public boolean isSupportPoEMode(){
		return 	this.getHiveApModel() == HiveAp.HIVEAP_MODEL_370 ||
				this.getHiveApModel() == HiveAp.HIVEAP_MODEL_390 ||
				this.getHiveApModel() == HiveAp.HIVEAP_MODEL_230;
	}

	/**
	 * static method check for hiveAP is 11ac box or not.
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean is11acHiveAP(short hiveApModel) {
		return new HiveAp(hiveApModel).getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_11AC);
	}

	/*
	 * method check for itself is 11n box or not.
	 */
	public boolean is11acHiveAP() {
		return this.getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_11AC);
	}

	/**
	 * @description By the end of Glasgow, Millau product still use specific cli_parser. 
	 * Need be removed after the HOS Millau code merge back to mainline
	 * @author huihe
	 * @date 11/22/2013
	 * @return
	 */
	public boolean isMillauImage() {
		return this.hiveApModel == HIVEAP_MODEL_370 || this.hiveApModel == HIVEAP_MODEL_390;
	}
	/*
	 * method check for itself is 300 box or not.
	 */
	public boolean is300HiveAP() {
		switch (hiveApModel) {
		case HIVEAP_MODEL_320:
		case HIVEAP_MODEL_340:
		case HIVEAP_MODEL_380:
		case HIVEAP_MODEL_330:
		case HIVEAP_MODEL_350:
		case HIVEAP_MODEL_370:
		case HIVEAP_MODEL_390:
			return true;
		}
		return false;
	}

	public boolean isBR200() {
		switch (hiveApModel) {
		case HIVEAP_MODEL_BR200:
		case HIVEAP_MODEL_BR200_WP:
		case HIVEAP_MODEL_BR200_LTE_VZ:
			return true;
		}
		return false;
	}
	
	public boolean isDevicePPSK9999Support() {
		return is300HiveAP()|| isBR200() || isSwitchProduct()  || isCVGAppliance() || hiveApModel==HIVEAP_MODEL_230;
	}

	/**
	 * Eth1 is available for HiveAPs except ag20,ag28, HiveAP120, HiveAP110
	 *
	 * @return -
	 */
	public boolean isEth1Available() {
		return isEth1Available(this.hiveApModel);
	}

	/**
	 * Eth1 is available for HiveAPs except ag20,ag28, HiveAP120, HiveAP110
	 * (Static method)
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean isEth1Available(short hiveApModel) {
		String[] eths = AhConstantUtil.getEnumValues(Device.ETH_PORTS, hiveApModel);
		if (eths != null && eths.length > 0) {
			for (String string : eths) {
				if (string.equalsIgnoreCase("eth1")) return true;
			}
			return false;
		}

		return false;
	}

	/**
	 * Wifi1 is available for HiveAPs except HiveAP110
	 *
	 * @return -
	 */
	public boolean isWifi1Available() {
		return isWifi1Available(hiveApModel);
//		return hiveApModel != HIVEAP_MODEL_110 && hiveApModel != HIVEAP_MODEL_BR100 && hiveApModel != HIVEAP_MODEL_BR200_WP;
	}

	/**
	 * Wifi1 is available for HiveAPs except HiveAP110 (Static method)
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean isWifi1Available(short hiveApModel) {
		Boolean isTrue = AhConstantUtil.isTrueAll(Device.SUPPORTED_WIFI1, hiveApModel);

		return isTrue == null ? false : isTrue;
	}

	public static boolean isWifi0Available(short hiveApModel) {
		Boolean isTrue = AhConstantUtil.isTrueAll(Device.SUPPORTED_WIFI0, hiveApModel);

		return isTrue == null ? false : isTrue;
	}

	/**
	 * PoE is available for HiveAPs except ag20,ag28, HiveAP120, HiveAP110
	 *
	 * @return -
	 */
	public boolean isPoEAvailable() {
		return is11nHiveAP() && !is120HiveAP() && !is110HiveAP();
	}

	/**
	 * Dual image is available for HiveAPs except ag20,ag28, HiveAP120,
	 * HiveAP110
	 *
	 * @return -
	 */
	public boolean isDualImageAvailable() {
		return is11nHiveAP() && !is120HiveAP() && !is110HiveAP() || isCVGAppliance();
	}
	
	public boolean isCVGAppliance(){
		return hiveApModel == HIVEAP_MODEL_VPN_GATEWAY_VA || hiveApModel == HIVEAP_MODEL_VPN_GATEWAY;
	}
	
	public static boolean isCVGAppliance(short hiveApModel){
		return hiveApModel == HIVEAP_MODEL_VPN_GATEWAY_VA || hiveApModel == HIVEAP_MODEL_VPN_GATEWAY;
	}

	/**
	 * VPN Server is available for HiveAPs except ag20,ag28, HiveAP120,
	 * HiveAP110
	 *
	 * @return -
	 */
	public boolean isVpnServerAvailable() {
		return is11nHiveAP() && !is120HiveAP() && !is110HiveAP();
	}

	private boolean is120HiveAP() {
		return hiveApModel == HIVEAP_MODEL_120;
	}

	private boolean is110HiveAP() {
		return hiveApModel == HIVEAP_MODEL_110;
	}

	/**
	 * static method check for HiveAP is HiveAP110 box or not
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean is110HiveAP(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_110;
	}

	/**
	 * static method check for HiveAP is HiveAP120 box or not
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean is120HiveAP(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_120;
	}

	/**
	 * static method check for HiveAP is HiveAP320 box or not
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean is320HiveAP(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_320;
	}

	public static boolean is330HiveAP(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_330;
	}

	public static boolean is350HiveAP(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_350;
	}

	public static boolean is370HiveAP(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_370;
	}

	public static boolean is390HiveAP(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_390;
	}

	/**
	 * static method check for HiveAP is HiveAP340 box or not
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean is340HiveAP(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_340;
	}

	/**
	 * static method check for HiveAP is HiveAP380 box or not
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean is380HiveAP(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_380;
	}

	/**
	 * static method check for HiveAP is HiveAP20 box or not
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean is20HiveAP(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_20;
	}

	/**
	 * static method check for Switich is 2024 box or not
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean is2024Switch(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_SR24;
	}

	/**
	 * static method check for Switich is 2124P box or not
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean is2124PSwitch(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_SR2124P;
	}

	/**
	 * static method check for Switich is 2148P box or not
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean is2148PSwitch(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_SR2148P;
	}

	/**
	 * static method check for BR has LTE Modem
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean isBRLTEMode(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_BR200_LTE_VZ;
	}

	/**
	 * static method check for Switich is 2024P box or not
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean is2024PSwitch(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_SR2024P;
	}

	/**
	 * static method check for HiveAP is HiveAP28 box or not
	 *
	 * @param hiveApModel
	 *            -
	 * @return -
	 */
	public static boolean is28HiveAP(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_28;
	}

	public static boolean isBR100LikeHiveAP(short hiveApModel) {
		return hiveApModel == HIVEAP_MODEL_BR100 ||
			   hiveApModel == HIVEAP_MODEL_BR200 ||
			   hiveApModel == HIVEAP_MODEL_BR200_WP ||
			   hiveApModel == HIVEAP_MODEL_BR200_LTE_VZ;
	}

	public static boolean is330HiveAPAsRouter(short hiveApModel, short deviceType) {
		return hiveApModel == HIVEAP_MODEL_330 && deviceType == Device_TYPE_BRANCH_ROUTER;
	}

	public static boolean is350HiveAPAsRouter(short hiveApModel, short deviceType) {
		return hiveApModel == HIVEAP_MODEL_350 && deviceType == Device_TYPE_BRANCH_ROUTER;
	}

	public boolean isBr200LteVZ(){
		return hiveApModel == HIVEAP_MODEL_BR200_LTE_VZ;
	}

	public boolean isAp370(){
		return hiveApModel == HIVEAP_MODEL_370;
	}

	@Transient
	private boolean changeLayer3Route;

	public boolean isChangeLayer3Route() {
		return changeLayer3Route;
	}

	public void setChangeLayer3Route(boolean changeLayer3Route) {
		this.changeLayer3Route = changeLayer3Route;
	}

	@Transient
	private String capwapText;

	@Transient
	private String capwapBackupText;

	public String getCapwapText() {
		return capwapText;
	}

	public void setCapwapText(String capwapText) {
		this.capwapText = capwapText;
	}

	public String getCapwapBackupText() {
		return capwapBackupText;
	}

	public void setCapwapBackupText(String capwapBackupText) {
		this.capwapBackupText = capwapBackupText;
	}

	@Transient
	private RadioProfile tempWifi0RadioProfile = new RadioProfile();

	@Transient
	private RadioProfile tempWifi1RadioProfile = new RadioProfile();

	public RadioProfile getTempWifi0RadioProfile() {
		return tempWifi0RadioProfile;
	}

	public void setTempWifi0RadioProfile(RadioProfile tempWifi0RadioProfile) {
		this.tempWifi0RadioProfile = tempWifi0RadioProfile;
	}

	public RadioProfile getTempWifi1RadioProfile() {
		return tempWifi1RadioProfile;
	}

	public void setTempWifi1RadioProfile(RadioProfile tempWifi1RadioProfile) {
		this.tempWifi1RadioProfile = tempWifi1RadioProfile;
	}

	@Transient
	private String tempWifi0RadioProfileCreateDisplayStyle = "none";

	@Transient
	private String tempWifi1RadioProfileCreateDisplayStyle = "none";

	public String getTempWifi0RadioProfileCreateDisplayStyle() {
		return tempWifi0RadioProfileCreateDisplayStyle;
	}

	public void setTempWifi0RadioProfileCreateDisplayStyle(
			String tempWifi0RadioProfileCreateDisplayStyle) {
		this.tempWifi0RadioProfileCreateDisplayStyle = tempWifi0RadioProfileCreateDisplayStyle;
	}

	public String getTempWifi1RadioProfileCreateDisplayStyle() {
		return tempWifi1RadioProfileCreateDisplayStyle;
	}

	public void setTempWifi1RadioProfileCreateDisplayStyle(
			String tempWifi1RadioProfileCreateDisplayStyle) {
		this.tempWifi1RadioProfileCreateDisplayStyle = tempWifi1RadioProfileCreateDisplayStyle;
	}

	@Transient
	private String networkSettingsDisplayStyle = "none"; // by default

	@Transient
	private String serviceSettingsDisplayStyle = "none";

	@Transient
	private String wifiClientModeDisplayStyle = "none";

	@Transient
	private String ssidAllocationDisplayStyle = "none";

	@Transient
	private String lanAllocationDisplayStyle = "none";

	@Transient
	private String pseSettingsDisplayStyle = "none";

	@Transient
	private String advEthSettingsDisplayStyle = "none";

	@Transient
	private String advSettingsDisplayStyle = "none";

	@Transient
	private String bjgwConfigDisplayStyle = "none";

	@Transient
	private String cvgMgtServerDisplayStyle = "none";

	@Transient
	private String credentialsDisplayStyle = "none";

	@Transient
	private String l3RoamingDisplayStyle = "none";

	@Transient
	private String routingDisplayStyle = "none";

	@Transient
	private String eth0BridgeAdvDisplayStyle = "none";

	@Transient
	private String eth1BridgeAdvDisplayStyle = "none";

	@Transient
	private String agg0BridgeAdvDisplayStyle = "none";

	@Transient
	private String red0BridgeAdvDisplayStyle = "none";

	@Transient
	private String ethCwpSettingDisplayStyle = "none";

	@Transient
	private String staticRoutesDisplayStyle = "none";

	@Transient
	private String internalNetworksDisplayStyle = "none";

	@Transient
	private String brStaticRoutingDisplayStyle = "none";

	@Transient
	private String mgt0DhcpSettingsStyle = "none";
	@Transient
	private String configMdmContentDisplayStyle = "none";

	public String getConfigMdmContentDisplayStyle() {
		return configMdmContentDisplayStyle;
	}

	public void setConfigMdmContentDisplayStyle(String configMdmContentDisplayStyle) {
		this.configMdmContentDisplayStyle = configMdmContentDisplayStyle;
	}

	@Transient
	private String forwardingDBSettingStyle = "none";

	@Transient
	private String lldpcdpSettingStyle = "none";

	@Transient
	private String switchPortSettingsStyle = "none";

	@Transient
	private String stormControlDivStyle = "none";

	@Transient
	private String switchQosSettingDivStyle = "none";

	@Transient
	private String igmpDivStyle = "none";


	public String getIgmpDivStyle() {
		return igmpDivStyle;
	}

	public void setIgmpDivStyle(String igmpDivStyle) {
		this.igmpDivStyle = igmpDivStyle;
	}

	public String getMgt0DhcpSettingsStyle() {
		return mgt0DhcpSettingsStyle;
	}

	public void setMgt0DhcpSettingsStyle(String mgt0DhcpSettingsStyle) {
		this.mgt0DhcpSettingsStyle = mgt0DhcpSettingsStyle;
	}

	public String getBrStaticRoutingDisplayStyle() {
		return brStaticRoutingDisplayStyle;
	}

	public void setBrStaticRoutingDisplayStyle(String brStaticRoutingDisplayStyle) {
		this.brStaticRoutingDisplayStyle = brStaticRoutingDisplayStyle;
	}

	public String getInternalNetworksDisplayStyle() {
		return internalNetworksDisplayStyle;
	}

	public void setInternalNetworksDisplayStyle(String internalNetworksDisplayStyle) {
		this.internalNetworksDisplayStyle = internalNetworksDisplayStyle;
	}

	public String getStaticRoutesDisplayStyle() {
		return staticRoutesDisplayStyle;
	}

	public void setStaticRoutesDisplayStyle(String staticRoutesDisplayStyle) {
		this.staticRoutesDisplayStyle = staticRoutesDisplayStyle;
	}

	public String getNetworkSettingsDisplayStyle() {
		return networkSettingsDisplayStyle;
	}

	public void setNetworkSettingsDisplayStyle(
			String networkSettingsDisplayStyle) {
		this.networkSettingsDisplayStyle = networkSettingsDisplayStyle;
	}

	public String getServiceSettingsDisplayStyle() {
		return serviceSettingsDisplayStyle;
	}

	public void setServiceSettingsDisplayStyle(
			String serviceSettingsDisplayStyle) {
		this.serviceSettingsDisplayStyle = serviceSettingsDisplayStyle;
	}

	public String getSsidAllocationDisplayStyle() {
		return ssidAllocationDisplayStyle;
	}

	public void setSsidAllocationDisplayStyle(String ssidAllocationDisplayStyle) {
		this.ssidAllocationDisplayStyle = ssidAllocationDisplayStyle;
	}

	public String getLanAllocationDisplayStyle(){
		return this.lanAllocationDisplayStyle;
	}

	public void setLanAllocationDisplayStyle(String lanAllocationDisplayStyle){
		this.lanAllocationDisplayStyle = lanAllocationDisplayStyle;
	}

	public String getAdvEthSettingsDisplayStyle() {
		return advEthSettingsDisplayStyle;
	}

	public void setAdvEthSettingsDisplayStyle(String advEthSettingsDisplayStyle) {
		this.advEthSettingsDisplayStyle = advEthSettingsDisplayStyle;
	}

	public String getAdvSettingsDisplayStyle() {
		return advSettingsDisplayStyle;
	}

	public void setAdvSettingsDisplayStyle(String advSettingsDisplayStyle) {
		this.advSettingsDisplayStyle = advSettingsDisplayStyle;
	}

	public String getBjgwConfigDisplayStyle() {
		return bjgwConfigDisplayStyle;
	}

	public void setBjgwConfigDisplayStyle(String bjgwConfigDisplayStyle) {
		this.bjgwConfigDisplayStyle = bjgwConfigDisplayStyle;
	}

	public String getCvgMgtServerDisplayStyle() {
		return cvgMgtServerDisplayStyle;
	}

	public void setCvgMgtServerDisplayStyle(String cvgMgtServerDisplayStyle) {
		this.cvgMgtServerDisplayStyle = cvgMgtServerDisplayStyle;
	}

	public String getCredentialsDisplayStyle() {
		return credentialsDisplayStyle;
	}

	public void setCredentialsDisplayStyle(String credentialsDisplayStyle) {
		this.credentialsDisplayStyle = credentialsDisplayStyle;
	}

	public String getL3RoamingDisplayStyle() {
		return l3RoamingDisplayStyle;
	}

	public void setL3RoamingDisplayStyle(String roamingDisplayStyle) {
		l3RoamingDisplayStyle = roamingDisplayStyle;
	}

	public String getRoutingDisplayStyle() {
		return routingDisplayStyle;
	}

	public void setRoutingDisplayStyle(String routingDisplayStyle) {
		this.routingDisplayStyle = routingDisplayStyle;
	}

	public String getEth0BridgeAdvDisplayStyle() {
		return eth0BridgeAdvDisplayStyle;
	}

	public void setEth0BridgeAdvDisplayStyle(String eth0BridgeAdvDisplayStyle) {
		this.eth0BridgeAdvDisplayStyle = eth0BridgeAdvDisplayStyle;
	}

	public String getEth1BridgeAdvDisplayStyle() {
		return eth1BridgeAdvDisplayStyle;
	}

	public void setEth1BridgeAdvDisplayStyle(String eth1BridgeAdvDisplayStyle) {
		this.eth1BridgeAdvDisplayStyle = eth1BridgeAdvDisplayStyle;
	}

	public String getAgg0BridgeAdvDisplayStyle() {
		return agg0BridgeAdvDisplayStyle;
	}

	public void setAgg0BridgeAdvDisplayStyle(String agg0BridgeAdvDisplayStyle) {
		this.agg0BridgeAdvDisplayStyle = agg0BridgeAdvDisplayStyle;
	}

	public String getRed0BridgeAdvDisplayStyle() {
		return red0BridgeAdvDisplayStyle;
	}

	public void setRed0BridgeAdvDisplayStyle(String red0BridgeAdvDisplayStyle) {
		this.red0BridgeAdvDisplayStyle = red0BridgeAdvDisplayStyle;
	}

	public String getEthCwpSettingDisplayStyle() {
		return ethCwpSettingDisplayStyle;
	}

	public void setEthCwpSettingDisplayStyle(String ethCwpSettingDisplayStyle) {
		this.ethCwpSettingDisplayStyle = ethCwpSettingDisplayStyle;
	}

	public long getTotalConnectTime() {
		return totalConnectTime;
	}

	public void setTotalConnectTime(long totalConnectTime) {
		this.totalConnectTime = totalConnectTime;
	}

	public long getTotalConnectTimes() {
		return totalConnectTimes;
	}

	public void setTotalConnectTimes(long totalConnectTimes) {
		this.totalConnectTimes = totalConnectTimes;
	}

	public boolean isDiscoveryReported() {
		return discoveryReported;
	}

	public void setDiscoveryReported(boolean discoveryReported) {
		this.discoveryReported = discoveryReported;
	}

	@Override
	public String toString() {
		return "Node ID: " + macAddress + "; Product Name: " + productName
				+ "; Software Version: " + softVer + "; Managed Status: "
				+ manageStatus + "; CAPWAP Connected: " + connected + "; VHM: "
				+ owner;
	}

	public boolean getSpnSupportBln() {
		return AhAppContainer.HmBe.getPerformModule().getBeSpectralAnalysisProcessor().isRunningAp(id);
	}

	public boolean isEnableEthBridge(){
		return this.enableEthBridge;
	}

	public void setEnableEthBridge(boolean enableEthBridge){
		this.enableEthBridge = enableEthBridge;
	}

	public boolean isEnableDynamicBandSwitch() {
		return enableDynamicBandSwitch;
	}

	public void setEnableDynamicBandSwitch(boolean enableDynamicBandSwitch) {
		this.enableDynamicBandSwitch = enableDynamicBandSwitch;
	}

	public Map<Long, DeviceInterface> getDeviceInterfaces(){
		return deviceInterfaces;
	}

	public void setDeviceInterfaces(Map<Long, DeviceInterface> deviceInterfaces){
		this.deviceInterfaces = deviceInterfaces;
	}
	/**
	 *
	 * @param order 0~2 0 primary 1 backup1 2 backup2
	 * @return primary or backup1 or backup2 deviceinterface
	 */
	@Transient
	public DeviceInterface getOrderWanInterface(int order){
		//this int array could provide 10 wan ports
		int[] priorityList={1500,1500,1500,1500,1500,1500,1500,1500,1500,1500};
		Collection<DeviceInterface> set = deviceInterfaces.values();
		Map<Integer,DeviceInterface> portMap=new HashMap<Integer,DeviceInterface>();
		int i=0;
		for (DeviceInterface intf : set) {
			if (getRole(intf) != AhInterface.ROLE_LAN) {
				priorityList[i]=intf.getPriority();
				portMap.put(intf.getPriority(), intf);
				i++;
			}
		}
		Arrays.sort(priorityList);
		return portMap.get(priorityList[order]);
	}

	@Transient
	public Map<Long, Integer> getWanInfoByDevice(){
		Map<Long, Integer> map = new HashMap<Long, Integer>();
		Map<Long, DeviceInterface> interfaceMap = getDeviceInterfaces();
		Set<Long> set = interfaceMap.keySet();
		for (Iterator<Long> iter = set.iterator(); iter.hasNext();) {
			Long key = iter.next();
			DeviceInterface deviceIntf = interfaceMap.get(key);
			if (deviceIntf != null && deviceIntf.getWanOrder() > 0) {
				map.put(key, deviceIntf.getWanOrder());
			}
		}
		return map;
	}


	@Transient
	public Map<Long, Integer> getWanInfoByNetworkPolicy(){
		Map<Long, Integer> map = new HashMap<Long, Integer>();
		PortGroupProfile portGroup = getPortGroup(configTemplate);
		if (null != portGroup) {
			List<Short> ethList = portGroup.getPortFinalValuesByPortType(
					DeviceInfType.Gigabit, PortAccessProfile.PORT_TYPE_WAN);
			if (ethList != null && ethList.size() > 0) {
				for (Short s : ethList) {
					map.put(s.longValue(), 0);
				}
			}
			List<Short> sfpList = portGroup.getPortFinalValuesByPortType(
					DeviceInfType.SFP, PortAccessProfile.PORT_TYPE_WAN);
			if (sfpList != null && sfpList.size() > 0) {
				for (Short s : sfpList) {
					map.put(s.longValue(), 0);
				}
			}
			List<Short> usbList = portGroup.getPortFinalValuesByPortType(
					DeviceInfType.USB, PortAccessProfile.PORT_TYPE_WAN);
			if (usbList != null && usbList.size() > 0) {
				for (Short s : usbList) {
					map.put(s.longValue(), 0);
				}
			}
		}
		return map;
	}


	/**
	 * get all wan ports of Router(not including Switch)
	 * @return how many wan ports of a hiveap
	 */
	@Transient
	public int getRouterWanInterfaceNum(ConfigTemplate tempConfig){
		if(tempConfig ==null){
			tempConfig = this.getConfigTemplate();
		}
		int usedWanPortNum = 0;
		if (getPortGroup(tempConfig) != null) {
			PortGroupProfile portGroup = getPortGroup(tempConfig);
			if (null != portGroup) {
				//add eth0 first
				usedWanPortNum += 1;
				List<Short> ethList = portGroup.getPortFinalValuesByPortType(
						DeviceInfType.Gigabit, PortAccessProfile.PORT_TYPE_WAN);
				if (isBRSupportMultiWan() && ethList != null && ethList.size() > 0) {
					usedWanPortNum+=ethList.size();
				}
				List<Short> usbList = portGroup.getPortFinalValuesByPortType(
						DeviceInfType.USB, PortAccessProfile.PORT_TYPE_WAN);
				if (usbList != null && usbList.size() > 0) {
					usedWanPortNum += usbList.size();
				}
				//TODO usb should be configable
				else {
					usedWanPortNum += 1;
				}
			}else {
				usedWanPortNum = 2;
			}
		} else {
			usedWanPortNum = 2;
		}
		return usedWanPortNum;
	}

	@Transient
	public boolean isWanPortChanged(){
		Collection<DeviceInterface> dInfArray = new ArrayList<DeviceInterface>();
		dInfArray.add(this.getEth0Interface());
		dInfArray.add(this.getEth1Interface());
		dInfArray.add(this.getEth2Interface());
		dInfArray.add(this.getEth3Interface());
		dInfArray.add(this.getEth4Interface());
		dInfArray.add(this.getWifi0Interface());

		for(DeviceInterface inf:dInfArray){
			if (getRole(inf) == AhInterface.ROLE_WAN) {
				if(inf.getPriority() == 2 || inf.getPriority() == 4 || inf.getPriority() == 6 || inf.getPriority() == 1 || inf.getPriority() == 8 || inf.getWanOrder() !=0){
					continue;
				}else{
					return true;
				}
			}else if(inf.getPriority() == 2 || inf.getPriority() == 4 || inf.getPriority() == 6 || inf.getPriority() == 1 || inf.getPriority() == 8 || inf.getWanOrder()!=0){
						return true;
			}

		}
		return false;
	}
		/**
	 * get all wan ports of Router(not including Switch)
	 * @return how many wan ports of a hiveap
	 */
	@Transient
	public int getRouterWanInterfaceNum(){
		int usedWanPortNum = 0;
		if (getPortGroup() != null) {
			PortGroupProfile portGroup = getPortGroup();
			if (null != portGroup) {
				//add eth0 first
				usedWanPortNum += 1;
				List<Short> ethList = portGroup.getPortFinalValuesByPortType(
						DeviceInfType.Gigabit, PortAccessProfile.PORT_TYPE_WAN);
				if (isBRSupportMultiWan() && ethList != null && ethList.size() > 0) {
					usedWanPortNum+=ethList.size();
				}
				List<Short> usbList = portGroup.getPortFinalValuesByPortType(
						DeviceInfType.USB, PortAccessProfile.PORT_TYPE_WAN);
				if (usbList != null && usbList.size() > 0) {
					usedWanPortNum += usbList.size();
				}
				//TODO usb should be configable
				else {
					usedWanPortNum += 1;
				}
			}else {
				usedWanPortNum = 2;
			}
		} else {
			usedWanPortNum = 2;
		}
		return usedWanPortNum;
	}

//	@Transient
//	public int getWanInterfaceNum(Collection<DeviceInterface> set){
//		int wanNums=0;
//		for (DeviceInterface intf : set) {
//			if (getRole(intf) != AhInterface.ROLE_LAN) {
//				wanNums++;
//			}
//		}
//		setWanOrder(set);
//		return wanNums;
//	}


	@Transient
	public void setWanOrder(Collection<DeviceInterface> set){
		//this int array could provide 10 wan ports
		int[] priorityList={1500,1500,1500,1500,1500,1500,1500,1500,1500,1500};
		Map<Integer,DeviceInterface> portMap=new HashMap<Integer,DeviceInterface>();
		int i=0;
		for (DeviceInterface intf : set) {
			if (getRole(intf) != AhInterface.ROLE_LAN) {
				priorityList[i]=intf.getPriority();
				portMap.put(intf.getPriority(), intf);
				i++;
			}
		}
		Arrays.sort(priorityList);
		for(int k=0;k<i;k++){
			portMap.get(priorityList[k]).setWanOrder(k+1);
		}
	}



	public boolean isEnableVRRP(){
		return this.enableVRRP;
	}

	public void setEnableVRRP(boolean enableVRRP){
		this.enableVRRP = enableVRRP;
	}

	public HiveAp getSecondVPNGateway(){
		return this.secondVPNGateway;
	}

	public void setSecondVPNGateway(HiveAp secondVPNGateway){
		this.secondVPNGateway = secondVPNGateway;
	}

	public boolean isEnablePreempt(){
		return this.enablePreempt;
	}

	public void setEnablePreempt(boolean enablePreempt){
		this.enablePreempt = enablePreempt;
	}

	public String getVirtualWanIp(){
		return this.virtualWanIp;
	}

	public void setVirtualWanIp(String virtualWanIp){
		this.virtualWanIp = virtualWanIp;
	}

	public String getVirtualLanIp(){
		return this.virtualLanIp;
	}

	public void setVirtualLanIp(String virtualLanIp){
		this.virtualLanIp = virtualLanIp;
	}

	public RoutingProfile getRoutingProfile(){
		return this.routingProfile;
	}

	public void setRoutingProfile(RoutingProfile routingProfile){
		this.routingProfile = routingProfile;
	}

	public int getRouteInterval(){
		return this.routeInterval;
	}

	public void setRouteInterval(int routeInterval){
		this.routeInterval = routeInterval;
	}

	public int getVrrpId(){
		return this.vrrpId;
	}

	public void setVrrpId(int vrrpId){
		this.vrrpId = vrrpId;
	}

	public int getVrrpPriority(){
		return this.vrrpPriority;
	}

	public void setVrrpPriority(int vrrpPriority){
		this.vrrpPriority = vrrpPriority;
	}

	public int getVrrpDelay(){
		return this.vrrpDelay;
	}

	public void setVrrpDelay(int vrrpDelay){
		this.vrrpDelay = vrrpDelay;
	}

	public List<USBModemProfile> getUsbModemList(){
		return this.usbModemList;
	}

	public void setUsbModemList(List<USBModemProfile> usbModemList){
		this.usbModemList = usbModemList;
	}

	public short getUsbConnectionModel(){
		return this.usbConnectionModel;
	}

	public void setUsbConnectionModel(short usbConnectionModel){
		this.usbConnectionModel = usbConnectionModel;
	}

	public String getEthLanStatus(){
		return this.ethLanStatus;
	}

	public void setEthLanStatus(String ethLanStatus){
		this.ethLanStatus = ethLanStatus;
	}

	@Transient
	public boolean isEnableLan_1(){
		int index = 1;
		return "1".equals(ethLanStatus.substring(index-1, index));
	}

	@Transient
	public void setEnableLan_1(boolean isEnable){
		int index = 1;
		if(isEnable){
			ethLanStatus = ethLanStatus.substring(0, index-1) + "1" + ethLanStatus.substring(index);
		}else{
			ethLanStatus = ethLanStatus.substring(0, index-1) + "0" + ethLanStatus.substring(index);
		}
	}

	@Transient
	public boolean isEnableLan_2(){
		int index = 2;
		return "1".equals(ethLanStatus.substring(index-1, index));
	}

	@Transient
	public void setEnableLan_2(boolean isEnable){
		int index = 2;
		if(isEnable){
			ethLanStatus = ethLanStatus.substring(0, index-1) + "1" + ethLanStatus.substring(index);
		}else{
			ethLanStatus = ethLanStatus.substring(0, index-1) + "0" + ethLanStatus.substring(index);
		}
	}

	@Transient
	public boolean isEnableLan_3(){
		int index = 3;
		return "1".equals(ethLanStatus.substring(index-1, index));
	}

	@Transient
	public void setEnableLan_3(boolean isEnable){
		int index = 3;
		if(isEnable){
			ethLanStatus = ethLanStatus.substring(0, index-1) + "1" + ethLanStatus.substring(index);
		}else{
			ethLanStatus = ethLanStatus.substring(0, index-1) + "0" + ethLanStatus.substring(index);
		}
	}

	@Transient
	public boolean isEnableLan_4(){
		int index = 4;
		return "1".equals(ethLanStatus.substring(index-1));
	}

	@Transient
	public void setEnableLan_4(boolean isEnable){
		int index = 4;
		if(isEnable){
			ethLanStatus = ethLanStatus.substring(0, index-1) + "1";
		}else{
			ethLanStatus = ethLanStatus.substring(0, index-1) + "0";
		}
	}

	@Transient
	public boolean isSecondVpnGateway(){
		if(id != null && this.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY){
			String strSql = "select 1 from hive_ap where SECOND_VPN_GATEWAY_ID="+this.id;
			List<?> boIds = QueryUtil.executeNativeQuery(strSql);
			return !boIds.isEmpty();
		}else{
			return false;
		}
	}

	@Transient
	public HiveAp getPrimaryVPNGateway(){
		return this.primaryVPNGateway;
	}

	@Transient
	public void setPrimaryVPNGateway(HiveAp primaryVPNGateway){
		this.primaryVPNGateway = primaryVPNGateway;
	}

	@Transient
	public DeviceInterface getEth0Interface(){
		DeviceInterface eth0Interface = this.getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_ETH0);
		if(eth0Interface == null){
			eth0Interface = new DeviceInterface();
			eth0Interface.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth0"));
			eth0Interface.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH0);
//			eth0Interface.setRole(AhInterface.ROLE_PRIMARY);
            if (!isSwitchProduct())
			    this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_ETH0, eth0Interface);
		}
		return eth0Interface;
	}

	@Transient
	public void setEth0Interface(DeviceInterface eth0Interface){
		this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_ETH0, eth0Interface);
	}

	@Transient
	public DeviceInterface getEth1Interface(){
		DeviceInterface eth1Interface = this.getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_ETH1);
		if(eth1Interface == null){
			eth1Interface = new DeviceInterface();
			eth1Interface.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth1"));
			eth1Interface.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH1);
			eth1Interface.setPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH1);
			this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_ETH1, eth1Interface);
		}
		return eth1Interface;
	}

	@Transient
	public void setEth1Interface(DeviceInterface eth1Interface){
		this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_ETH1, eth1Interface);
	}

	@Transient
	public DeviceInterface getEth2Interface(){
		DeviceInterface eth2Interface = this.getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_ETH2);
		if(eth2Interface == null){
			eth2Interface = new DeviceInterface();
			eth2Interface.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth2"));
			eth2Interface.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH2);
			this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_ETH2, eth2Interface);
		}
		return eth2Interface;
	}

	@Transient
	public void setEth2Interface(DeviceInterface eth2Interface){
		this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_ETH2, eth2Interface);
	}

	@Transient
	public DeviceInterface getEth3Interface(){
		DeviceInterface eth3Interface = this.getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_ETH3);
		if(eth3Interface == null){
			eth3Interface = new DeviceInterface();
			eth3Interface.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth3"));
			eth3Interface.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH3);
			this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_ETH3, eth3Interface);
		}
		return eth3Interface;
	}

	@Transient
	public void setEth3Interface(DeviceInterface eth3Interface){
		this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_ETH3, eth3Interface);
	}

	@Transient
	public DeviceInterface getEth4Interface(){
		DeviceInterface eth4Interface = this.getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_ETH4);
		if(eth4Interface == null){
			eth4Interface = new DeviceInterface();
			eth4Interface.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth4"));
			eth4Interface.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH4);
			this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_ETH4, eth4Interface);
		}
		return eth4Interface;
	}

	@Transient
	public void setEth4Interface(DeviceInterface eth4Interface){
		this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_ETH4, eth4Interface);
	}

	@Transient
	public DeviceInterface getSwitchInterface(long index){
		DeviceInterface swInterface = this.getDeviceInterfaces().get(index);
		return swInterface;
	}


	@Transient
	public DeviceInterface getUSBInterface(){
		DeviceInterface usbInterface = this.getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_USB);
		if(usbInterface == null){
			usbInterface = new DeviceInterface();
			usbInterface.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.usb"));
			usbInterface.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_USB);
//			usbInterface.setRole(AhInterface.ROLE_BACKUP);
			this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_USB, usbInterface);
		}
		return usbInterface;
	}

	@Transient
	public void setUSBInterface(DeviceInterface usbInterface){
		this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_USB, usbInterface);
	}

	@Transient
	public DeviceInterface getWifi0Interface(){
		DeviceInterface wifi0Interface = this.getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_WIFI0);
		if(wifi0Interface == null){
			wifi0Interface = new DeviceInterface();
			wifi0Interface.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.wifi0"));
			wifi0Interface.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_WIFI0);
			this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_WIFI0, wifi0Interface);
		}
		return wifi0Interface;
	}

	@Transient
	public void setWifi0Interface(DeviceInterface wifi0Interface){
		this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_WIFI0, wifi0Interface);
	}

	@Transient
	public DeviceInterface getWifi1Interface(){
		DeviceInterface wifi1Interface = this.getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_WIFI1);
		if(wifi1Interface == null){
			wifi1Interface = new DeviceInterface();
			wifi1Interface.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.wifi1"));
			wifi1Interface.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_WIFI1);
			this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_WIFI1, wifi1Interface);
		}
		return wifi1Interface;
	}

	@Transient
	public void setWifi1Interface(DeviceInterface wifi1Interface){
		this.getDeviceInterfaces().put((long)AhInterface.DEVICE_IF_TYPE_WIFI1, wifi1Interface);
	}

	//fix bug 14678 start
	@Transient
	public String getDeviceCategory(){
		return getDeviceEnumString(hiveApModel, deviceType);
	}
	//end

	public void setMultipleVlan(List<HiveApMultipleVlan> multipleVlan) {
		this.multipleVlan = multipleVlan;
	}

	public List<HiveApMultipleVlan> getMultipleVlan() {
		return multipleVlan;
	}

	@Column(nullable = true)
	private Boolean isOutdoor;

	public Boolean getIsOutdoor() {
		return isOutdoor;
	}

	public void setIsOutdoor(Boolean isOutdoor) {
		this.isOutdoor = isOutdoor;
	}

	public MgmtServiceIPTrack getVpnIpTrack() {
		return vpnIpTrack;
	}

	public void setVpnIpTrack(MgmtServiceIPTrack vpnIpTrack) {
		this.vpnIpTrack = vpnIpTrack;
	}

	public List<HiveApInternalNetwork> getInternalNetworks() {
		return internalNetworks;
	}

	public void setInternalNetworks(List<HiveApInternalNetwork> internalNetworks) {
		this.internalNetworks = internalNetworks;
	}

	public boolean isDisableExpress(){
		return this.deviceType == Device_TYPE_BRANCH_ROUTER || this.deviceType == Device_TYPE_VPN_GATEWAY || this.deviceType == Device_TYPE_VPN_BR ||
				this.hiveApModel == HIVEAP_MODEL_BR100 || isCVGAppliance() || this.deviceType == Device_TYPE_SWITCH;
	}

	private boolean enableCvgPMTUD = true;

	private boolean monitorCvgMSS = true;

	private int thresholdCvgForAllTCP = 0;

	private int thresholdCvgThroughVPNTunnel = 0;

	private boolean enableOverrideBrPMTUD = false;

	private boolean enableBrPMTUD = true;

	private boolean monitorBrMSS = true;

	private int thresholdBrForAllTCP = 0;

	private int thresholdBrThroughVPNTunnel = 0;


	public int getMultiDisplayType() {
		return multiDisplayType;
	}

	public void setMultiDisplayType(int multiDisplayType) {
		this.multiDisplayType = multiDisplayType;
	}

	public boolean isMultiDisplayApOnly() {
		return multiDisplayApOnly;
	}

	public void setMultiDisplayApOnly(boolean multiDisplayApOnly) {
		this.multiDisplayApOnly = multiDisplayApOnly;
	}

	public boolean isMultiIncludeCvg() {
		return multiIncludeCvg;
	}

	public void setMultiIncludeCvg(boolean multiIncludeCvg) {
		this.multiIncludeCvg = multiIncludeCvg;
	}

	// add for remove Network Policy from CVG  start **************************
	public DeviceCVGDepend getCvgDPD() {
		return cvgDPD;
	}

	public DeviceCVGDepend getOrCreateCvgDPD() {
		if(cvgDPD == null){
			cvgDPD = new DeviceCVGDepend();
		}
		return cvgDPD;
	}

	public void setCvgDPD(DeviceCVGDepend cvgDPD) {
		this.cvgDPD = cvgDPD;
	}

	public String getVpnServerName() {
		if(this.id == null){
			return "None";
		}
		if(this.vpnServerName == null){
			List<?> vpnServerNameList = QueryUtil.executeQuery(
							"select distinct bo.profileName from "
									+ VpnService.class.getSimpleName()
									+ " as bo join bo.vpnGateWaysSetting as joined",
							null,
							new FilterParams("joined.hiveApId = :s1",new Object[] { this.id }));
			if(vpnServerNameList != null && !vpnServerNameList.isEmpty()){
				this.vpnServerName = (String)vpnServerNameList.get(0);
			}
		}
		if(vpnServerName == null){
			return "None";
		}else{
			return vpnServerName;
		}
	}

	// end for remove Network Policy from CVG  end ****************************

	public boolean isEnableCvgPMTUD() {
		return enableCvgPMTUD;
	}

	public void setEnableCvgPMTUD(boolean enableCvgPMTUD) {
		this.enableCvgPMTUD = enableCvgPMTUD;
	}

	public boolean isMonitorCvgMSS() {
		return monitorCvgMSS;
	}

	public void setMonitorCvgMSS(boolean monitorCvgMSS) {
		this.monitorCvgMSS = monitorCvgMSS;
	}

	public int getThresholdCvgForAllTCP() {
		return thresholdCvgForAllTCP;
	}

	public void setThresholdCvgForAllTCP(int thresholdCvgForAllTCP) {
		this.thresholdCvgForAllTCP = thresholdCvgForAllTCP;
	}

	public int getThresholdCvgThroughVPNTunnel() {
		return thresholdCvgThroughVPNTunnel;
	}

	public void setThresholdCvgThroughVPNTunnel(int thresholdCvgThroughVPNTunnel) {
		this.thresholdCvgThroughVPNTunnel = thresholdCvgThroughVPNTunnel;
	}

	public boolean isEnableBrPMTUD() {
		return enableBrPMTUD;
	}

	public void setEnableBrPMTUD(boolean enableBrPMTUD) {
		this.enableBrPMTUD = enableBrPMTUD;
	}

	public boolean isMonitorBrMSS() {
		return monitorBrMSS;
	}

	public void setMonitorBrMSS(boolean monitorBrMSS) {
		this.monitorBrMSS = monitorBrMSS;
	}

	public int getThresholdBrForAllTCP() {
		return thresholdBrForAllTCP;
	}

	public void setThresholdBrForAllTCP(int thresholdBrForAllTCP) {
		this.thresholdBrForAllTCP = thresholdBrForAllTCP;
	}

	public int getThresholdBrThroughVPNTunnel() {
		return thresholdBrThroughVPNTunnel;
	}

	public void setThresholdBrThroughVPNTunnel(int thresholdBrThroughVPNTunnel) {
		this.thresholdBrThroughVPNTunnel = thresholdBrThroughVPNTunnel;
	}

	public boolean isEnableOverrideBrPMTUD() {
		return enableOverrideBrPMTUD;
	}

	public void setEnableOverrideBrPMTUD(boolean enableOverrideBrPMTUD) {
		this.enableOverrideBrPMTUD = enableOverrideBrPMTUD;
	}

	public boolean isEnabledBrAsRadiusServer() {
		return enabledBrAsRadiusServer;
	}

	public void setEnabledBrAsRadiusServer(boolean enabledBrAsRadiusServer) {
		this.enabledBrAsRadiusServer = enabledBrAsRadiusServer;
	}

	public boolean isEnabledBrAsPpskServer() {
		return enabledBrAsPpskServer;
	}

	public void setEnabledBrAsPpskServer(boolean enabledBrAsPpskServer) {
		this.enabledBrAsPpskServer = enabledBrAsPpskServer;
	}

	public int getMaxPowerSource() {
		return maxPowerSource;
	}

	public void setMaxPowerSource(int maxPowerSource) {
		this.maxPowerSource = maxPowerSource;
	}

	public boolean isOverWriteRadiusServer(){
		if (!this.isBranchRouter() || this.hiveApModel == HiveAp.HIVEAP_MODEL_BR100) {
			return false;
		}else{
			return true;
		}
	}

	public boolean isEnabledOverrideRadiusServer() {
		return enabledOverrideRadiusServer;
	}

	public void setEnabledOverrideRadiusServer(boolean enabledOverrideRadiusServer) {
		this.enabledOverrideRadiusServer = enabledOverrideRadiusServer;
	}

	public boolean isEnabledOverrideVoipSetting() {
		return enabledOverrideVoipSetting;
	}

	public void setEnabledOverrideVoipSetting(boolean enabledOverrideVoipSetting) {
		this.enabledOverrideVoipSetting = enabledOverrideVoipSetting;
	}

	public boolean isEnablePppoe() {
		return enablePppoe;
	}

	public void setEnablePppoe(boolean enablePppoe) {
		this.enablePppoe = enablePppoe;
	}

	public PPPoE getPppoeAuthProfile() {
		return pppoeAuthProfile;
	}

	public void setPppoeAuthProfile(PPPoE pppoeAuthProfile) {
		this.pppoeAuthProfile = pppoeAuthProfile;
	}

	public String getPseSettingsDisplayStyle() {
		return pseSettingsDisplayStyle;
	}

	public void setPseSettingsDisplayStyle(String pseSettingsDisplayStyle) {
		this.pseSettingsDisplayStyle = pseSettingsDisplayStyle;
	}

	public boolean isPppoeEnableCurrent() {
		return pppoeEnableCurrent;
	}

	public void setPppoeEnableCurrent(boolean pppoeEnableCurrent) {
		this.pppoeEnableCurrent = pppoeEnableCurrent;
	}

	public RoutingPolicy getRoutingPolicy() {
		return routingPolicy;
	}

	public void setRoutingPolicy(RoutingPolicy routingPolicy) {
		this.routingPolicy = routingPolicy;
	}

	@Transient
	public boolean isVpnGateway(){
		return this.deviceType == HiveAp.Device_TYPE_VPN_GATEWAY ||
				this.deviceType == HiveAp.Device_TYPE_VPN_BR;
	}

	@Transient
	public boolean isBranchRouter(){
		return this.deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER ||
				this.deviceType == HiveAp.Device_TYPE_VPN_BR;
	}

	@Transient
	public boolean isSwitch(){
		return this.deviceType == HiveAp.Device_TYPE_SWITCH;
	}

	@Transient
	public boolean isSwitchProduct(){
		return this.getDeviceInfo().isSptEthernetMore_24();
	}

	@Transient
	public static boolean isSwitchProduct(short hiveApModel){
		return NmsUtil.getDeviceInfo(hiveApModel).isSptEthernetMore_24();
	}
	@Transient
	public static boolean isBranchRouterProduct(short hiveApModel){
	    return hiveApModel == HiveAp.HIVEAP_MODEL_BR100
	            || hiveApModel == HiveAp.HIVEAP_MODEL_BR200
	            || hiveApModel == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ
	            || hiveApModel == HiveAp.HIVEAP_MODEL_BR200_WP;
	}

	@Transient
	public static int getPortRange(short hiveApModel){
		DeviceInfo dInfo = NmsUtil.getDeviceInfo(hiveApModel);
		int ethCounts = dInfo.getIntegerValue(DeviceInfo.SPT_ETHERNET_COUNTS);
		int sfpCounts = dInfo.getIntegerValue(DeviceInfo.SPT_SFP_COUNTS);
		ethCounts = ethCounts < 0 ? 0 : ethCounts;
		sfpCounts = sfpCounts < 0 ? 0 : sfpCounts;
		return ethCounts + sfpCounts;
	}

	public String getWifiClientModeDisplayStyle() {
		return wifiClientModeDisplayStyle;
	}

	public void setWifiClientModeDisplayStyle(String wifiClientModeDisplayStyle) {
		this.wifiClientModeDisplayStyle = wifiClientModeDisplayStyle;
	}

	public List<HiveApPreferredSsid> getWifiClientPreferredSsids() {
		return wifiClientPreferredSsids;
	}

	public void setWifiClientPreferredSsids(
			List<HiveApPreferredSsid> wifiClientPreferredSsids) {
		this.wifiClientPreferredSsids = wifiClientPreferredSsids;
	}

	public ForwardingDB getForwardingDB() {
		return forwardingDB;
	}

	public void setForwardingDB(ForwardingDB forwardingDB) {
		this.forwardingDB = forwardingDB;
	}

	public boolean isEnableSwitchQosSettings() {
		return enableSwitchQosSettings;
	}

	public void setEnableSwitchQosSettings(boolean enableSwitchQosSettings) {
		this.enableSwitchQosSettings = enableSwitchQosSettings;
	}

	public String getForwardingDBSettingStyle() {
		return forwardingDBSettingStyle;
	}

	public void setForwardingDBSettingStyle(String forwardingDBSettingStyle) {
		this.forwardingDBSettingStyle = forwardingDBSettingStyle;
	}

	public String getLldpcdpSettingStyle() {
		return lldpcdpSettingStyle;
	}

	public void setLldpcdpSettingStyle(String lldpcdpSettingStyle) {
		this.lldpcdpSettingStyle = lldpcdpSettingStyle;
	}

	public String getSwitchPortSettingsStyle() {
		return switchPortSettingsStyle;
	}

	public void setSwitchPortSettingsStyle(String switchPortSettingsStyle) {
		this.switchPortSettingsStyle = switchPortSettingsStyle;
	}

	public String getStormControlDivStyle() {
		return stormControlDivStyle;
	}

	public void setStormControlDivStyle(String stormControlDivStyle) {
		this.stormControlDivStyle = stormControlDivStyle;
	}

	public String getSwitchQosSettingDivStyle() {
		return switchQosSettingDivStyle;
	}

	public void setSwitchQosSettingDivStyle(String switchQosSettingDivStyle) {
		this.switchQosSettingDivStyle = switchQosSettingDivStyle;
	}

	//Added from Chesapeake
	public static final int RESERVED_VLANS_DEFAULT = 3967;

	@Range(min = 2, max = 3967)
	private int resrvedVlans = RESERVED_VLANS_DEFAULT;

	@Transient
	private String resrvedVlansSettingStyle = "none";

	public int getResrvedVlans() {
		return resrvedVlans;
	}

	public void setResrvedVlans(int resrvedVlans) {
		this.resrvedVlans = resrvedVlans;
	}

	public String getResrvedVlansSettingStyle() {
		return resrvedVlansSettingStyle;
	}

	public void setResrvedVlansSettingStyle(String resrvedVlansSettingStyle) {
		this.resrvedVlansSettingStyle = resrvedVlansSettingStyle;
	}

	@Transient
	private PortGroupProfile portGroup;



	public PortGroupProfile getPortGroup(ConfigTemplate tempConfig) {
		PortGroupProfile tempProfile=null;
			String hiveApModelStr = String.valueOf(this.hiveApModel);
		//	ConfigTemplate tempConfig=QueryUtil.findBoById(ConfigTemplate.class, configTemplateId);
			if(tempConfig == null || tempConfig.getPortProfiles() == null ||tempConfig.getPortProfiles().isEmpty()){
				return null;
			}
			for(PortGroupProfile group : tempConfig.getPortProfiles()){
				String[] groupArg = group.getDeviceModelStrs();
				if(groupArg == null){
					continue;
				}
				if(group.getDeviceType() != deviceType){
					continue;
				}
				for(int index=0; index<groupArg.length; index++){
					if(hiveApModelStr.equals(groupArg[index])){

						SingleTableItem	portGroupItem = CLICommonFunc.getPortGroupProfile(group, this);
							if(portGroupItem!=null){
								PortGroupProfile tagPortGroup=QueryUtil.findBoById(PortGroupProfile.class, portGroupItem.getNonGlobalId(),new ConfigLazyQueryBo());
								if(tagPortGroup!=null){
									return tagPortGroup;
								}
							}
						return group;
					}
				}
			}
		return tempProfile;
	}

	public PortGroupProfile getPortGroup() {
			String hiveApModelStr = String.valueOf(this.hiveApModel);
			if(this.configTemplate == null || this.configTemplate.getPortProfiles() == null ||this.configTemplate.getPortProfiles().isEmpty()){
				return null;
			}
			for(PortGroupProfile group : this.configTemplate.getPortProfiles()){
				String[] groupArg = group.getDeviceModelStrs();
				if(groupArg == null){
					continue;
				}
				if(group.getDeviceType() != deviceType){
					continue;
				}
				for(int index=0; index<groupArg.length; index++){
					if(hiveApModelStr.equals(groupArg[index])){
						SingleTableItem	portGroupItem = CLICommonFunc.getPortGroupProfile(group, this);
						if(portGroupItem!=null){
						PortGroupProfile tagPortGroup=QueryUtil.findBoById(PortGroupProfile.class, portGroupItem.getNonGlobalId(),new ConfigLazyQueryBo());
						if(tagPortGroup!=null){
							this.portGroup=tagPortGroup;
							return this.portGroup;
						}
						}
						this.portGroup = group;
						return this.portGroup;
					}
				}
			}
		return portGroup;
	}

//	@Transient
//	public short getRole(DeviceInterface dInf) {
//		int[] priorityList={1500,1500,1500};
//		Collection<DeviceInterface> set = deviceInterfaces.values();
//		Map<Integer,DeviceInterface> portMap=new HashMap<Integer,DeviceInterface>();
//		int i=0;
//		for (DeviceInterface intf : set) {
//			if (getWanLanRole(intf) != AhInterface.ROLE_LAN) {
//				priorityList[i]=intf.getPriority();
//				portMap.put(intf.getPriority(), intf);
//				if(i>1){
//					break;
//				}else{
//					i++;
//				}
//			}
//		}
//		Arrays.sort(priorityList);
//		if(dInf==priorityList[])
//		return portMap.get(priorityList[order]);
//	}

	@Transient
	public boolean isBRSupportMultiWan(){
		if(getDeviceType()!=Device_TYPE_BRANCH_ROUTER){
			return false;
		}
//		if(this.hiveApModel == HiveAp.HIVEAP_MODEL_SR24 || this.hiveApModel == HiveAp.HIVEAP_MODEL_SR2124P
//			     || this.hiveApModel == HiveAp.HIVEAP_MODEL_SR48){
//			return false;
//		}
		return NmsUtil.compareSoftwareVersion("6.0.2.0",getSoftVer()) <= 0;
	}

	@Transient
	public short getRole(DeviceInterface dInf) {
		if(!isBRSupportMultiWan()){
				if(dInf.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_ETH1 || dInf.getDeviceIfType()  == AhInterface.DEVICE_IF_TYPE_ETH3
						|| dInf.getDeviceIfType()  == AhInterface.DEVICE_IF_TYPE_ETH3 || dInf.getDeviceIfType()  == AhInterface.DEVICE_IF_TYPE_ETH4){
					return AhInterface.ROLE_LAN;
				}
		}

		// first check whether it's wifi 0/1
//		if (dInf.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_WIFI0
//				|| dInf.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_WIFI1) {
//			if (dInf.getPriority() == 100) {
//				return AhInterface.ROLE_WAN;
//			} else {
//				return AhInterface.ROLE_LAN;
//			}
//
//		}
		if(getDeviceType()==Device_TYPE_BRANCH_ROUTER &&
				(dInf.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_ETH0 || dInf.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_USB)) {
			return AhInterface.ROLE_WAN;
		}
		if (NmsUtil.compareSoftwareVersion("6.0.2.0", getSoftVer()) > 0
				&& (getDeviceType() == Device_TYPE_HIVEAP || getDeviceType() == Device_TYPE_BRANCH_ROUTER)
				&& dInf.getDeviceIfType() != AhInterface.DEVICE_IF_TYPE_USB ) {
			return AhInterface.ROLE_LAN;
		}
		int usedWanPortNum = 1;
		if (getPortGroup() != null) {
			PortGroupProfile portGroup = getPortGroup();
			if (null != portGroup) {
				List<Short> ethList = portGroup.getPortFinalValuesByPortType(
						DeviceInfType.Gigabit, PortAccessProfile.PORT_TYPE_WAN);
				if (ethList != null && ethList.size() > 0) {
					usedWanPortNum+=ethList.size();
					for (Short s : ethList) {
						if (dInf.getDeviceIfType() == s) {
							return AhInterface.ROLE_WAN;
						}
					}
				} else if (dInf.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_ETH0) {
					return AhInterface.ROLE_WAN;
				}
				List<Short> usbList = portGroup.getPortFinalValuesByPortType(
						DeviceInfType.USB, PortAccessProfile.PORT_TYPE_WAN);
				if (usbList != null && usbList.size() > 0) {
					usedWanPortNum += usbList.size();
					for (Short s : usbList) {
						if (dInf.getDeviceIfType() == s) {
							return AhInterface.ROLE_WAN;
						}
					}
				} else if (dInf.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_USB) {
					if (this.hiveApModel == HiveAp.HIVEAP_MODEL_SR24 || this.hiveApModel == HiveAp.HIVEAP_MODEL_SR2124P
						     || this.hiveApModel == HiveAp.HIVEAP_MODEL_SR48 || this.hiveApModel == HiveAp.HIVEAP_MODEL_SR2148P
						     || this.hiveApModel == HiveAp.HIVEAP_MODEL_SR2024P) {
						return AhInterface.ROLE_WAN;
					}else{
					return AhInterface.ROLE_WAN;
					}

				}

				if(usbList == null || (usbList!=null && usbList.size() == 0)){
					usedWanPortNum += 1;
				}

				List<Short> sfpList = portGroup.getPortFinalValuesByPortType(
						DeviceInfType.SFP, PortAccessProfile.PORT_TYPE_WAN);
				if (sfpList != null && sfpList.size() > 0) {
					for (Short s : sfpList) {
						if (dInf.getDeviceIfType() == s) {
							return AhInterface.ROLE_WAN;
						}
					}
				}
			}
		} else {
			if (dInf.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_USB) {
				if (this.hiveApModel == HiveAp.HIVEAP_MODEL_SR24
						 || this.hiveApModel == HiveAp.HIVEAP_MODEL_SR2124P
					     || this.hiveApModel == HiveAp.HIVEAP_MODEL_SR48 ) {
					return AhInterface.ROLE_WAN;
				}else{
				return AhInterface.ROLE_WAN;
				}
			}
		}

		if(dInf.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_WIFI0 && getDeviceType()==Device_TYPE_BRANCH_ROUTER) {
			if (hiveApModel == HIVEAP_MODEL_BR200_WP || hiveApModel == HIVEAP_MODEL_BR200_LTE_VZ) {
				if(this.getRadioConfigType() == RADIO_MODE_ACCESS_WAN && usedWanPortNum < 3){
					return AhInterface.ROLE_WAN;
				}
			}
		}
		return AhInterface.ROLE_LAN;

	}

	@Transient
	private String igmpEditInfo;
	@Transient
	private String[] igmpVlanIds;
	@Transient
	private String[] igmpSnoopings;
	@Transient
	private String[] immediateLeaves;
	@Transient
	private String[] delayLeaveQueryIntervals;
	@Transient
	private String[] delayLeaveQueryCounts;
	@Transient
	private String[] routerPortAginTimes;
	@Transient
	private String[] robustnessCounts;

	@Transient
	private String multicastGroupEditInfo;
	@Transient
	private String[] multicastGroupVlanIds;
	@Transient
	private String[] multicastGroupIpAddresses;
	@Transient
	private String[] multicastGroupInterfaces;

	private boolean overrideIgmpSnooping = false;
	private boolean enableImmediateLeave = false;
	private boolean enableReportSuppression = true;
	@Range(min = 1, max = 25)
	private Integer globalDelayLeaveQueryInterval = 1;
	@Range(min = 1, max = 7)
	private Integer globalDelayLeaveQueryCount = 2;
	@Range(min = 30, max = 1000)
	private Integer globalRouterPortAginTime = 250;
	@Range(min = 1, max = 3)
	private Integer globalRobustnessCount = 2;

	private boolean enableCellularModem = true;

	public boolean isEnableCellularModem() {
		return enableCellularModem;
	}

	public void setEnableCellularModem(boolean enableCellularModem) {
		this.enableCellularModem = enableCellularModem;
	}


	public boolean isEnableImmediateLeave() {
		return enableImmediateLeave;
	}

	public void setEnableImmediateLeave(boolean enableImmediateLeave) {
		this.enableImmediateLeave = enableImmediateLeave;
	}

	public boolean isEnableReportSuppression() {
		return enableReportSuppression;
	}

	public void setEnableReportSuppression(boolean enableReportSuppression) {
		this.enableReportSuppression = enableReportSuppression;
	}

	public Integer getGlobalDelayLeaveQueryInterval() {
		return globalDelayLeaveQueryInterval;
	}

	public void setGlobalDelayLeaveQueryInterval(
			Integer globalDelayLeaveQueryInterval) {
		this.globalDelayLeaveQueryInterval = globalDelayLeaveQueryInterval;
	}

	public Integer getGlobalDelayLeaveQueryCount() {
		return globalDelayLeaveQueryCount;
	}

	public void setGlobalDelayLeaveQueryCount(Integer globalDelayLeaveQueryCount) {
		this.globalDelayLeaveQueryCount = globalDelayLeaveQueryCount;
	}

	public Integer getGlobalRouterPortAginTime() {
		return globalRouterPortAginTime;
	}

	public void setGlobalRouterPortAginTime(Integer globalRouterPortAginTime) {
		this.globalRouterPortAginTime = globalRouterPortAginTime;
	}

	public Integer getGlobalRobustnessCount() {
		return globalRobustnessCount;
	}

	public void setGlobalRobustnessCount(Integer globalRobustnessCount) {
		this.globalRobustnessCount = globalRobustnessCount;
	}

	public boolean isOverrideIgmpSnooping() {
		return overrideIgmpSnooping;
	}

	public void setOverrideIgmpSnooping(boolean overrideIgmpSnooping) {
		this.overrideIgmpSnooping = overrideIgmpSnooping;
	}

	public String getMulticastGroupEditInfo() {
		return multicastGroupEditInfo;
	}

	public void setMulticastGroupEditInfo(String multicastGroupEditInfo) {
		this.multicastGroupEditInfo = multicastGroupEditInfo;
	}

	public String[] getMulticastGroupVlanIds() {
		return multicastGroupVlanIds;
	}

	public void setMulticastGroupVlanIds(String[] multicastGroupVlanIds) {
		this.multicastGroupVlanIds = multicastGroupVlanIds;
	}

	public String[] getMulticastGroupIpAddresses() {
		return multicastGroupIpAddresses;
	}

	public void setMulticastGroupIpAddresses(String[] multicastGroupIpAddresses) {
		this.multicastGroupIpAddresses = multicastGroupIpAddresses;
	}

	public String[] getMulticastGroupInterfaces() {
		return multicastGroupInterfaces;
	}

	public void setMulticastGroupInterfaces(String[] multicastGroupInterfaces) {
		this.multicastGroupInterfaces = multicastGroupInterfaces;
	}

	public String getIgmpEditInfo() {
		return igmpEditInfo;
	}

	public void setIgmpEditInfo(String igmpEditInfo) {
		this.igmpEditInfo = igmpEditInfo;
	}

	public String[] getIgmpVlanIds() {
		return igmpVlanIds;
	}

	public void setIgmpVlanIds(String[] igmpVlanIds) {
		this.igmpVlanIds = igmpVlanIds;
	}

	public String[] getIgmpSnoopings() {
		return igmpSnoopings;
	}

	public void setIgmpSnoopings(String[] igmpSnoopings) {
		this.igmpSnoopings = igmpSnoopings;
	}

	public String[] getImmediateLeaves() {
		return immediateLeaves;
	}

	public void setImmediateLeaves(String[] immediateLeaves) {
		this.immediateLeaves = immediateLeaves;
	}

	public String[] getDelayLeaveQueryIntervals() {
		return delayLeaveQueryIntervals;
	}

	public void setDelayLeaveQueryIntervals(String[] delayLeaveQueryIntervals) {
		this.delayLeaveQueryIntervals = delayLeaveQueryIntervals;
	}

	public String[] getDelayLeaveQueryCounts() {
		return delayLeaveQueryCounts;
	}

	public void setDelayLeaveQueryCounts(String[] delayLeaveQueryCounts) {
		this.delayLeaveQueryCounts = delayLeaveQueryCounts;
	}

	public String[] getRouterPortAginTimes() {
		return routerPortAginTimes;
	}

	public void setRouterPortAginTimes(String[] routerPortAginTimes) {
		this.routerPortAginTimes = routerPortAginTimes;
	}

	public String[] getRobustnessCounts() {
		return robustnessCounts;
	}

	public void setRobustnessCounts(String[] robustnessCounts) {
		this.robustnessCounts = robustnessCounts;
	}

	@OneToOne(fetch = FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinColumn(name = "device_stp_settings_id", nullable = true)
	private DeviceStpSettings deviceStpSettings;

	public boolean isMultiDisplayRealm() {
		return multiDisplayRealm;
	}

	public void setMultiDisplayRealm(boolean multiDisplayRealm) {
		this.multiDisplayRealm = multiDisplayRealm;
	}

	public DeviceStpSettings getDeviceStpSettings() {
		return deviceStpSettings;
	}

	public void setDeviceStpSettings(DeviceStpSettings deviceStpSettings) {
		this.deviceStpSettings = deviceStpSettings;
	}

	public DownloadInfo getDownloadInfo() {
		return downloadInfo;
	}

	public void setDownloadInfo(DownloadInfo downloadInfo) {
		this.downloadInfo = downloadInfo;
	}

	@Transient
	private DownloadInfo downloadInfoView;

	public DownloadInfo getDownloadInfoView() {
		return downloadInfoView;
	}

	public void setDownloadInfoView(DownloadInfo downloadInfoView) {
		this.downloadInfoView = downloadInfoView;
	}

	@Transient
	@SuppressWarnings("static-access")
	public void synchronizeCVGInterfaceState(){
		if(getDeviceType() != HiveAp.Device_TYPE_VPN_GATEWAY){
			return;
		}
		FilterParams filter = new FilterParams("apMac = :s1 and ifName = :s2", new Object[]{getMacAddress(), "eth0"});
		List<AhXIf> results = MgrUtil.getQueryEntity().executeQuery(AhXIf.class,
				new SortParams("xifpk.statTimeStamp", false), filter, getOwner().getId(), 1);
		if(results.isEmpty() || results.get(0) == null){
			return;
		}
		long statTimeStamp = results.get(0).getXifpk().getStatTimeValue();
		filter = new FilterParams("apMac = :s1 and ifName = :s2 and xifpk.statTimeStamp = :s3", new Object[]{getMacAddress(), "eth1", statTimeStamp});
		results = MgrUtil.getQueryEntity().executeQuery(AhXIf.class,
				null, filter, owner.getId(), 1);

		DeviceInterface eth1Interface = getEth1Interface();
		if(results.isEmpty()){
			eth1Interface.setIfActive(false);
		}else{
			eth1Interface.setIfActive(true);
		}
		setEth1Interface(eth1Interface);
	}

	public boolean isMultiChangeLockRealmName() {
		return multiChangeLockRealmName;
	}

	public void setMultiChangeLockRealmName(boolean multiChangeLockRealmName) {
		this.multiChangeLockRealmName = multiChangeLockRealmName;
	}

	public boolean isOverrideNetworkPolicySetting() {
		return overrideNetworkPolicySetting;
	}

	public void setOverrideNetworkPolicySetting(boolean overrideNetworkPolicySetting) {
		this.overrideNetworkPolicySetting = overrideNetworkPolicySetting;
	}

	public int getSwitchChipVersion() {
		return switchChipVersion;
	}

	public void setSwitchChipVersion(int switchChipVersion) {
		this.switchChipVersion = switchChipVersion;
	}

	public void initDeviceStpSettings(){
		if(this.deviceType == HiveAp.Device_TYPE_SWITCH){
			DeviceStpSettings deviceStpSettings = new DeviceStpSettings();
			deviceStpSettings.setOwner(this.owner);
			List<InterfaceStpSettings> allPortLevelSettings = new ArrayList<InterfaceStpSettings>();
			for(Long key:this.getDeviceInterfaces().keySet()){
				if(key.shortValue() != AhInterface.DEVICE_IF_TYPE_USB){
					InterfaceStpSettings interfaceStpSettings = new InterfaceStpSettings();
					interfaceStpSettings.setInterfaceNum(key.shortValue());
					allPortLevelSettings.add(interfaceStpSettings);
				}
			}
			deviceStpSettings.setInterfaceStpSettings(allPortLevelSettings);
			this.setDeviceStpSettings(deviceStpSettings);
		}
	}

	@Transient
	public boolean isBr100WorkAsAp(){
		return this.getHiveApModel() == HIVEAP_MODEL_BR100 && this.getDeviceType()== Device_TYPE_HIVEAP;
	}

	@Transient
	//private boolean usbAsCellularModem;

	public boolean isUsbAsCellularModem() {
		return HIVEAP_MODEL_BR200_LTE_VZ == hiveApModel;
	}
	//for override capture data input into cwp
	private boolean overrideCaptureDataByCWP = false;
	private boolean enableCaptureDataByCWP = false;

	public boolean isOverrideCaptureDataByCWP() {
		return overrideCaptureDataByCWP;
	}

	public void setOverrideCaptureDataByCWP(boolean overrideCaptureDataByCWP) {
		this.overrideCaptureDataByCWP = overrideCaptureDataByCWP;
	}

	public boolean isEnableCaptureDataByCWP() {
		return enableCaptureDataByCWP;
	}

	public void setEnableCaptureDataByCWP(boolean enableCaptureDataByCWP) {
		this.enableCaptureDataByCWP = enableCaptureDataByCWP;
	}

	@Transient
	private String captureDataCwpDivStyle = "none";


	public String getCaptureDataCwpDivStyle() {
		return captureDataCwpDivStyle;
	}

	public void setCaptureDataCwpDivStyle(String captureDataCwpDivStyle) {
		this.captureDataCwpDivStyle = captureDataCwpDivStyle;
	}

	public String getCompleteUpdateTag() {
		return completeUpdateTag;
	}

	public void setCompleteUpdateTag(String completeUpdateTag) {
		this.completeUpdateTag = completeUpdateTag;
	}

	public String generateCompleteUpdateTag(){
		return "deviceType=" + this.deviceType;
	}

	public boolean isNeedCompleteUpdate(){
		if(completeUpdateTag == null){
			return true;
		}
		if(isSimulated()){
			return true;
		}
		return !completeUpdateTag.equals(generateCompleteUpdateTag());
	}

	@Transient
	private short rebootResult;

	public short getRebootResult() {
		return rebootResult;
	}

	public void setRebootResult(short rebootResult) {
		this.rebootResult = rebootResult;
	}

	public boolean isMultiDisplayLocation() {
		return multiDisplayLocation;
	}

	public void setMultiDisplayLocation(boolean multiDisplayLocation) {
		this.multiDisplayLocation = multiDisplayLocation;
	}

	@Transient
	private String diMenuTypeKey=null;

	public String getDiMenuTypeKey() {
		return diMenuTypeKey;
	}

	public void setDiMenuTypeKey(String diMenuTypeKey) {
		this.diMenuTypeKey = diMenuTypeKey;
	}

	/**
	 * @author huihe@aerohive.com
	 * @description fix Bug28914, only for 11ac Device, it will overwrite the config in network policy
	 */
	public static final int TX_RETRY_RATE = 60;
	private int deviceTxRetry = TX_RETRY_RATE;

	private int clientTxRetry = TX_RETRY_RATE;

	public int getDeviceTxRetry() {
		return deviceTxRetry;
	}

	public void setDeviceTxRetry(int deviceTxRetry) {
		this.deviceTxRetry = deviceTxRetry;
	}

	public int getClientTxRetry() {
		return clientTxRetry;
	}

	public void setClientTxRetry(int clientTxRetry) {
		this.clientTxRetry = clientTxRetry;
	}

	public boolean isEnabledSameVlan() {
		return enabledSameVlan;
	}

	public void setEnabledSameVlan(boolean enabledSameVlan) {
		this.enabledSameVlan = enabledSameVlan;
	}

    public String getHardwareRevision() {
        return hardwareRevision;
    }

    public void setHardwareRevision(String hardwareRevision) {
        this.hardwareRevision = hardwareRevision;
    }
    
    /*Add control for CAPWAP delay alarm from Guadalupe*/
	
    private boolean overrideEnableDelayAlarm;
    
 	private boolean enableDelayAlarm = true;
 	
 	@Transient
	private boolean multiChangeDelayAlarm=false;

	public boolean isEnableDelayAlarm() {
		return enableDelayAlarm;
	}

	public void setEnableDelayAlarm(boolean enableDelayAlarm) {
		this.enableDelayAlarm = enableDelayAlarm;
	}

	public boolean isOverrideEnableDelayAlarm() {
		return overrideEnableDelayAlarm;
	}

	public void setOverrideEnableDelayAlarm(boolean overrideEnableDelayAlarm) {
		this.overrideEnableDelayAlarm = overrideEnableDelayAlarm;
	}

	public boolean isMultiChangeDelayAlarm() {
		return multiChangeDelayAlarm;
	}

	public void setMultiChangeDelayAlarm(boolean multiChangeDelayAlarm) {
		this.multiChangeDelayAlarm = multiChangeDelayAlarm;
	}

	public CLIBlob getSupplementalCLI() {
		return supplementalCLI;
	}

	public void setSupplementalCLI(CLIBlob supplementalCLI) {
		this.supplementalCLI = supplementalCLI;
	}

	public boolean isEnableIDMAuthProxy() {
		return enableIDMAuthProxy;
	}

	public void setEnableIDMAuthProxy(boolean enableIDMAuthProxy) {
		this.enableIDMAuthProxy = enableIDMAuthProxy;
	}

	public boolean isChangeIDMAuthProxy() {
		return changeIDMAuthProxy;
	}

	public void setChangeIDMAuthProxy(boolean changeIDMAuthProxy) {
		this.changeIDMAuthProxy = changeIDMAuthProxy;
	}
	
	@Transient
	public String getSupplementalCLIName(){
		String result="";
		if(supplementalCLI != null){
			result = supplementalCLI.getSupplementalName();
			if(null == result){
				if(null != configTemplate && null != configTemplate.getSupplementalCLI()){
					result = configTemplate.getSupplementalCLI().getSupplementalName();
				}
			}
		}else{
			if(null != configTemplate && null != configTemplate.getSupplementalCLI()){
				result = configTemplate.getSupplementalCLI().getSupplementalName();
			}
		}
		return result;
	}
	
	@Transient
	private String hostnameHtml;

	public String getHostnameHtml() {
		return hostnameHtml;
	}

	public void setHostnameHtml(String hostnameHtml) {
		this.hostnameHtml = hostnameHtml;
	}
	
}