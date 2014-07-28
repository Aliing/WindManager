package com.ah.ui.actions.hiveap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeEventUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBeParaUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.PresenceUtil;
import com.ah.be.common.SensorModeUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.db.configuration.ConfigurationChangedEvent;
import com.ah.be.parameter.BeParaModule;
import com.ah.be.parameter.constant.parser.AhDeviceStyleParser.DeviceKey;
import com.ah.be.parameter.constant.parser.AhDeviceStyleParser.DeviceStyle;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.parameter.device.DevicePropertyManage;
import com.ah.be.performance.dataretention.NetworkDeviceConfigTracking;
import com.ah.bo.HmBo;
import com.ah.bo.HmBoBase;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.ConfigTemplateStormControl;
import com.ah.bo.hiveap.ConfigTemplateType;
import com.ah.bo.hiveap.ConfigTemplateVlanNetwork;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.DeviceInventory;
import com.ah.bo.hiveap.DeviceMstpInstancePriority;
import com.ah.bo.hiveap.DeviceStpSettings;
import com.ah.bo.hiveap.ForwardingDB;
import com.ah.bo.hiveap.HiveAPVirtualConnection;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApDynamicRoute;
import com.ah.bo.hiveap.HiveApFilter;
import com.ah.bo.hiveap.HiveApInternalNetwork;
import com.ah.bo.hiveap.HiveApIpRoute;
import com.ah.bo.hiveap.HiveApL3cfgNeighbor;
import com.ah.bo.hiveap.HiveApLearningMac;
import com.ah.bo.hiveap.HiveApMultipleVlan;
import com.ah.bo.hiveap.HiveApPreferredSsid;
import com.ah.bo.hiveap.HiveApSsidAllocation;
import com.ah.bo.hiveap.HiveApStaticRoute;
import com.ah.bo.hiveap.HiveApUpdateResult;
import com.ah.bo.hiveap.InterfaceMstpSettings;
import com.ah.bo.hiveap.InterfaceStpSettings;
import com.ah.bo.hiveap.MacAddressLearningEntry;
import com.ah.bo.hiveap.USBModemProfile;
import com.ah.bo.hiveap.WifiClientPreferredSsid;
import com.ah.bo.igmp.IgmpPolicy;
import com.ah.bo.igmp.MulticastGroup;
import com.ah.bo.igmp.MulticastGroupInterface;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.bo.mgmt.AhDataTableColumn;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.FilterParamsFactory;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryLazyBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.network.CLIBlob;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.MstpRegion;
import com.ah.bo.network.MstpRegionPriority;
import com.ah.bo.network.NeighborsNameItem;
import com.ah.bo.network.PPPoE;
import com.ah.bo.network.RoutingProfile;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.bo.network.StpSettings;
import com.ah.bo.network.SubNetworkResource;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VlanDhcpServer;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnNetworkSub;
import com.ah.bo.network.VpnService;
import com.ah.bo.performance.AhLatestRadioAttribute;
import com.ah.bo.performance.AhLatestXif;
import com.ah.bo.performance.AhPortAvailability;
import com.ah.bo.performance.AhVPNStatus;
import com.ah.bo.performance.AhVPNStatus.VpnStatus;
import com.ah.bo.performance.AhXIf;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.MgmtServiceSyslog;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SsidProfile;
import com.ah.integration.airtight.SyncProgressEvent;
import com.ah.integration.airtight.SyncStage;
import com.ah.integration.airtight.SyncStage.Status;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.SessionKeys;
import com.ah.ui.actions.admin.DeviceTagUtil;
import com.ah.ui.actions.config.ImportCsvFileAction;
import com.ah.ui.actions.config.RadioProfileAction;
import com.ah.ui.actions.config.RadiusOnHiveApAction;
import com.ah.ui.actions.config.VlanDhcpServerAction;
import com.ah.ui.actions.config.VpnServiceAction;
import com.ah.ui.actions.monitor.BonjourGatewayMonitoringAction;
import com.ah.ui.actions.monitor.MapNodeAction;
import com.ah.ui.tags.OptionsTransfer;
import com.ah.util.CheckItem;
import com.ah.util.CountryCode;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.HiveApUtils;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.bo.device.DeviceInterfaceAdapter;
import com.ah.util.bo.device.DeviceInterfaceBundle;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;
import com.ah.util.devices.impl.Device;
import com.ah.ws.rest.client.utils.DeviceImpUtils;
import com.ah.ws.rest.client.utils.DeviceUtils;
import com.sun.jersey.api.client.ClientHandlerException;

/*
 * modification history
 *
 * add function for returning EnumItem of 'bindInterface' and 'bindRole'
 * joseph chen, 04/10/2008
 *
 * change the radio mode and profile of wifi0 and wifi1
 * joseph chen, 04/11/2008
 *wifi0Channels
 * add method 'getRadioChannels(long radioProfileId)','getEnumChannelNaType', 'getEnumChannelNgType'
 * joseph chen, 04/23/2008
 *
 * add method 'verifyBindInterface'
 * joseph chen, 05/27/2008
 */

public class HiveApAction extends BaseAction implements QueryBo {

	private static final String NEWLINE_REGEX = "\n";

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(HiveApAction.class
			.getSimpleName());

	public static final String HM_LIST_TYPE = "hmListType";

	public static final String MANAGED_LIST_VIEW = "managedListView";

	public static final String LIST_VIEW_COLLAPSE_STATUS = "listViewCollapseStatus";

	public static final int MAX_SSID_FOR_AG20 = 7;

	public static final String DEFAULT_FILTER_CURRENT = "Current Policy";

	public static final String DEFAULT_FILTER_CURRENT_DEFAULT = "Current/Default Policies";
	
	public static final int MULTI_MODIFY = 0;

	public static final int SINGLE_MODIFY = 1;
	/*
	 * Table Column IDs
	 */
	public static final int COLUMN_HOSTNAME = 1;

	public static final int COLUMN_SEVERITY = 2;

	public static final int COLUMN_IPADDRESS = 3;

	public static final int COLUMN_MACADDRESS = 4;

	public static final int COLUMN_CONNECTED = 5;

	public static final int COLUMN_APTYPE = 6;

	public static final int COLUMN_CLIENTS = 7;

	public static final int COLUMN_CONNECTIONTIME = 8;

	public static final int COLUMN_PRODUCTNAME = 9;

	public static final int COLUMN_SOFTWAREVERSION = 10;

	public static final int COLUMN_DISCOVERYTIME = 11;

	public static final int COLUMN_NODEID = 12;

	public static final int COLUMN_WLANPOLICY = 13;

	public static final int COLUMN_TOPOMAP = 14;

	public static final int COLUMN_DHCP = 15;

	public static final int COLUMN_NETMASK = 16;

	public static final int COLUMN_GATEWAY = 17;

	public static final int COLUMN_LOCATION = 18;

	public static final int COLUMN_CURRENTADMIN = 19;

	public static final int COLUMN_DISCOVERYMETHOD = 20;

	public static final int COLUMN_SERIALNUMBER = 21;

	public static final int COLUMN_CAPWAPSTATUS = 22;

	public static final int COLUMN_WIFI0_CHANNEL = 23;

	public static final int COLUMN_WIFI0_POWER = 24;

	public static final int COLUMN_WIFI1_CHANNEL = 25;

	public static final int COLUMN_WIFI1_POWER = 26;

	public static final int COLUMN_COUNTRY_CODE = 27;

	public static final int COLUMN_AUDIT = 28;

	public static final int COLUMN_HIVE = 29;

	public static final int COLUMN_ETH0_DEVICE_ID = 30;

	public static final int COLUMN_ETH0_PORT_ID = 31;

	public static final int COLUMN_ETH1_DEVICE_ID = 32;

	public static final int COLUMN_ETH1_PORT_ID = 33;

	public static final int COLUMN_MGT0_VLAN = 34;

	public static final int COLUMN_ETH0_SYSTEM_ID = 35;

	public static final int COLUMN_ETH1_SYSTEM_ID = 36;

	public static final int COLUMN_NATIVE_VLAN = 37;

	public static final int COLUMN_CAPWAP_CLIENT_IP = 38;

	public static final int COLUMN_UPDATE_STATUS = 39;

	public static final int COLUMN_3GUSB_STATUS = 40;

	public static final int COLUMN_ISOUTDOOR = 41;
	// fix bug 14678 add 'Device category'
	public static final int COLUMN_DEVICE_CATEGORY = 42;

	public static final int COLUMN_WIFI0_RADIOPROFILE=43;

	public static final int COLUMN_WIFI1_RADIOPROFILE=44;

	public static final int COLUMN_L7_SIGNATURE_VERSION = 45;
	
	public static final int COLUMN_SUPPLEMENTAL_CLI = 46;

	public static final short SELECTED_COLUMNS = 1;

	public static final short AVAILABLE_COLUMNS = 2;
	
	public static final short PREFERRED_SSID_MAX_NUMBER = 4;

	public static List<USBModemProfile> defaultUsbModemList;
	
	private boolean mstpEnable = false;

	@Override
	public String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_HOSTNAME:
			if (isJsonMode()) {
				code = "guided.configuration.hiveap.column.hostName";
			} else {
				code = "hiveAp.hostName";
			}
			break;
		case COLUMN_SEVERITY:
			code = "monitor.hiveAp.severity.status";
			break;
		case COLUMN_IPADDRESS:
			code = "hiveAp.interface.ipAddress";
			break;
		case COLUMN_MACADDRESS:
			if (isJsonMode()) {
				code = "guided.configuration.hiveap.column.macaddress";
			} else {
				code = "hiveAp.macaddress";
			}
			break;
		case COLUMN_CONNECTED:
			if (isJsonMode()) {
				code = "guided.configuration.hiveap.column.online";
			} else {
				code = "monitor.hiveAp.capwap.status";
			}
			break;
		case COLUMN_APTYPE:
			code = "hiveAp.apType";
			break;
		case COLUMN_CLIENTS:
			code = "monitor.hiveAp.numberOfClient";
			break;
		case COLUMN_CONNECTIONTIME:
			code = "monitor.hiveAp.connectionTime";
			break;
		case COLUMN_PRODUCTNAME:
			code = "monitor.hiveAp.model";
			break;
		case COLUMN_SOFTWAREVERSION:
			if (isJsonMode()) {
				code = "guided.configuration.hiveap.column.version";
			} else {
				code = "monitor.hiveAp.sw";
			}
			break;
		case COLUMN_DISCOVERYTIME:
			code = "hiveAp.discoveryTime";
			break;
		case COLUMN_NODEID:
			if (isJsonMode()) {
				code = "guided.configuration.hiveap.column.macaddress";
			} else {
				code = "hiveAp.macaddress";
			}
			break;
		case COLUMN_WLANPOLICY:
			code = "hiveAp.template";
			break;
		case COLUMN_TOPOMAP:
			code = "hiveAp.topology";
			break;
		case COLUMN_DHCP:
			code = "hiveAp.head.dhcp";
			break;
		case COLUMN_NETMASK:
			code = "hiveAp.netmask";
			break;
		case COLUMN_GATEWAY:
			code = "hiveAp.gateway";
			break;
		case COLUMN_LOCATION:
			code = "hiveAp.location";
			break;
		case COLUMN_CURRENTADMIN:
			code = "hiveAp.currentUser";
			break;
		case COLUMN_DISCOVERYMETHOD:
			code = "hiveAp.origin";
			break;
		case COLUMN_SERIALNUMBER:
			code = "hiveAp.serialNumber";
			break;
		case COLUMN_CAPWAPSTATUS:
			code = "hiveAp.capwapStatus";
			break;
		case COLUMN_WIFI0_CHANNEL:
			code = "hiveAp.wifi0.channel";
			break;
		case COLUMN_WIFI0_POWER:
			code = "hiveAp.wifi0.power";
			break;
		case COLUMN_WIFI1_CHANNEL:
			code = "hiveAp.wifi1.channel";
			break;
		case COLUMN_WIFI1_POWER:
			code = "hiveAp.wifi1.power";
			break;
		case COLUMN_COUNTRY_CODE:
			code = "hiveAp.countryCode";
			break;
		case COLUMN_AUDIT:
			if (isJsonMode()) {
				code = "guided.configuration.hiveap.column.Updated";
			} else {
				code = "hiveAp.configuration.audit";
			}
			break;
		case COLUMN_HIVE:
			code = "hiveAp.hiveProfile";
			break;
		case COLUMN_ETH0_DEVICE_ID:
			code = "hiveAp.lldpCdp.eth0.deviceId";
			break;
		case COLUMN_ETH0_PORT_ID:
			code = "hiveAp.lldpCdp.eth0.portId";
			break;
		case COLUMN_ETH1_DEVICE_ID:
			code = "hiveAp.lldpCdp.eth1.deviceId";
			break;
		case COLUMN_ETH1_PORT_ID:
			code = "hiveAp.lldpCdp.eth1.portId";
			break;
		case COLUMN_MGT0_VLAN:
			code = "config.configTemplate.vlan";
			break;
		case COLUMN_NATIVE_VLAN:
			code = "config.configTemplate.vlanNative";
			break;
		case COLUMN_ETH0_SYSTEM_ID:
			code = "hiveAp.lldpCdp.eth0.systemId";
			break;
		case COLUMN_ETH1_SYSTEM_ID:
			code = "hiveAp.lldpCdp.eth1.systemId";
			break;
		case COLUMN_CAPWAP_CLIENT_IP:
			code = "hiveAp.capwapIpAddress";
			break;
		case COLUMN_UPDATE_STATUS:
			code = "guided.configuration.hiveap.column.uploadStatus";
			break;
		case COLUMN_3GUSB_STATUS:
			code = "hiveAp.brRouter.usb.3G";
			break;
		case COLUMN_ISOUTDOOR:
			code = "hiveAp.isOutdoor";
			break;
		// fix bug 14678 add 'Device category'
		case COLUMN_DEVICE_CATEGORY:
			code = "hiveAp.deviceCategory";
			break;
		case COLUMN_WIFI0_RADIOPROFILE:
			code = "hiveAp.head.wifi0.radioProfile";
			break;
		case COLUMN_WIFI1_RADIOPROFILE:
			code = "hiveAp.head.wifi1.radioProfile";
			break;
		case COLUMN_L7_SIGNATURE_VERSION:
			code = "hiveAp.head.l7.signature.ver";
			break;
		case COLUMN_SUPPLEMENTAL_CLI:
			if (isFullMode() && getEnableSupplementalCLI()){
				code = "hollywood_02.supp_cli_apList.title";
			}else{
				code = null;
			}
		    break;
		}

		return MgrUtil.getUserMessage(code);
	}

	@Override
	public final void setColumnDescription(List<HmTableColumn> columns) {
		for (HmTableColumn column : columns) {
			column.setColumnDescription(getColumnDescription(column
					.getColumnId()));
			column.setTableId(tableId);
		}
	}

	private String hmListType;

	public void setHmListType(String hmListType) {
		this.hmListType = hmListType;
	}

	public String getHmListType() {
		return this.hmListType;
	}
	
	private Long configmdmId;
	public Long getConfigmdmId() {
		return configmdmId;
	}

	public void setConfigmdmId(Long configmdmId) {
		this.configmdmId = configmdmId;
	}

	// default list position
	private String listType = "";

	private final String strNoChange = "[-No Change-]";

	private List<HiveAp> hiveApList = new ArrayList<HiveAp>();

	private List<HiveAp> routerList = new ArrayList<HiveAp>();

	private List<HiveAp> switchList = new ArrayList<HiveAp>();

	private List<HiveAp> l2_vpnGatewayList = new ArrayList<HiveAp>();

	private List<HiveAp> l3_vpnGatewayList = new ArrayList<HiveAp>();
	
	private List<Short> cvgList;
	
	private List<Short> brList;

	private String customTag1;
	private String customTag2;
	private String customTag3;
	
	public List<Short> getCVGList(){
		if(cvgList == null){
			cvgList = new ArrayList<Short>();
			cvgList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
			cvgList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY);
			return cvgList;
		}
		
		return cvgList;
	}
	
	public List<Short> getBRList(){
		if(brList == null){
			brList = new ArrayList<Short>();
			brList.add(HiveAp.HIVEAP_MODEL_BR100);
			brList.add(HiveAp.HIVEAP_MODEL_BR200);
			brList.add(HiveAp.HIVEAP_MODEL_BR200_WP);
			brList.add(HiveAp.HIVEAP_MODEL_BR200_LTE_VZ);
			return brList;
		}
		
		return brList;
	}

	public String getListType() {
		return listType;
	}

	public void setListType(String listType) {
		this.listType = listType;
	}

	public List<HiveAp> getHiveApList() {
		return hiveApList;
	}

	public void setHiveApList(List<HiveAp> hiveApList) {
		this.hiveApList = hiveApList;
	}

	public List<HiveAp> getL2_vpnGatewayList() {
		return this.l2_vpnGatewayList;
	}

	public void setL2_vpnGatewayList(List<HiveAp> l2_vpnGatewayList) {
		this.l2_vpnGatewayList = l2_vpnGatewayList;
	}

	public List<HiveAp> getL3_vpnGatewayList() {
		return this.l3_vpnGatewayList;
	}

	public void setL3_vpnGatewayList(List<HiveAp> l3_vpnGatewayList) {
		this.l3_vpnGatewayList = l3_vpnGatewayList;
	}

	public List<HiveAp> getRouterList() {
		return routerList;
	}

	public void setRouterList(List<HiveAp> routerList) {
		this.routerList = routerList;
	}

	@Override
	public String execute() throws Exception {
		try {
			if ("newHiveAps".equals(hmListType)
					|| "manuallyProvisioned".equals(hmListType)
					|| "autoDiscovered".equals(hmListType)
					|| "managedHiveAps".equals(hmListType)
					|| "manageAPEx".equals(hmListType)
					|| "manageAPGuid".equals(hmListType)
					|| "managedVPNGateways".equals(hmListType)
					|| "managedRouters".equals(hmListType)
					|| "managedSwitches".equals(hmListType)
					|| "managedDeviceAPs".equals(hmListType)) {
				MgrUtil.setSessionAttribute(HM_LIST_TYPE, hmListType);
			}

			String listTypeFromSession = (String) MgrUtil
					.getSessionAttribute(HM_LIST_TYPE);
			if ("newHiveAps".equals(listTypeFromSession)
					|| "manuallyProvisioned".equals(listTypeFromSession)
					|| "autoDiscovered".equals(listTypeFromSession)
					|| "managedHiveAps".equals(listTypeFromSession)
					|| "manageAPEx".equals(listTypeFromSession)
					|| "manageAPGuid".equals(listTypeFromSession)
					|| "managedVPNGateways".equals(listTypeFromSession)
					|| "managedRouters".equals(listTypeFromSession)
					|| "managedSwitches".equals(listTypeFromSession)
					|| "managedDeviceAPs".equals(listTypeFromSession)) {
				listType = listTypeFromSession;
			} else {
				listType = "managedHiveAps";
			}

			if(viewType != null && !viewType.equals("")){
				MgrUtil.setSessionAttribute(MANAGED_LIST_VIEW, "config"
						.equals(viewType) ? "config" : "monitor");
			}

			if ("manuallyProvisioned".equals(listType)) {
				setSelectedL2Feature(L2_FEATURE_MANUALLY_PROVISIONED);
				String where = "manageStatus = :s1 AND origin = :s2";
				Object[] values = new Object[2];
				values[0] = HiveAp.STATUS_NEW;
				values[1] = HiveAp.ORIGIN_CREATE;
				filterParams = new FilterParams(where, values);
			} else if ("autoDiscovered".equals(listType)) {
				setSelectedL2Feature(L2_FEATURE_AUTO_DISCOVERED);
				String where = "manageStatus = :s1 AND origin = :s2";
				Object[] values = new Object[2];
				values[0] = HiveAp.STATUS_NEW;
				values[1] = HiveAp.ORIGIN_DISCOVERED;
				filterParams = new FilterParams(where, values);
			} else if ("newHiveAps".equals(listType)) {
				setSelectedL2Feature(L2_FEATURE_NEW_HIVE_APS);
				filterParams = new FilterParams("manageStatus",
						HiveAp.STATUS_NEW);
			} else if ("managedHiveAps".equals(listType)) {
				String viewType = (String) MgrUtil
				.getSessionAttribute(MANAGED_LIST_VIEW);
				if("config".equals(viewType)){
					setSelectedL2Feature(L2_FEATURE_CONFIG_HIVE_APS);
				}else{
					setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
				}

				List<Short> status = new ArrayList<Short>();
				status.add(HiveAp.STATUS_MANAGED);
				status.add(HiveAp.STATUS_NEW);
				filterParams = new FilterParams("manageStatus", status);
			} else if("manageAPEx".equals(listType)) {
				setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
				List<Short> status = new ArrayList<Short>();
				status.add(HiveAp.STATUS_MANAGED);
				status.add(HiveAp.STATUS_NEW);

				//express model not support BR100, BR200, BR200-WP, CVG
				List<Short> modelList = new ArrayList<Short>();
				modelList.addAll(getCVGList());
				modelList.addAll(getBRList());
				// TA1917
				if(getUserContext().isSourceFromIdm()) {
					filterParams = new FilterParams("deviceType = :s1 AND manageStatus in :s2 AND hiveApModel not in (:s3) and softVer>:s4", new Object[]{HiveAp.Device_TYPE_HIVEAP, status, modelList, "5.1.2.0"});
				} else {
					filterParams = new FilterParams("deviceType = :s1 AND manageStatus in :s2 AND hiveApModel not in (:s3)", new Object[]{HiveAp.Device_TYPE_HIVEAP, status, modelList});
				}
			} else if("manageAPGuid".equals(listType)) {
				setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
				List<Short> status = new ArrayList<Short>();
				status.add(HiveAp.STATUS_MANAGED);
				status.add(HiveAp.STATUS_NEW);
				List<ConfigTemplate> tempList = getNetworkPolicyFilterList();
				if (tempList == null || tempList.isEmpty()) {
					filterParams = new FilterParams("manageStatus", status);
				} else {
					ConfigTemplate selectedPolicy = tempList.get(0);
					String whereSql;
					if(selectedPolicy.getId() != null){
						if(selectedPolicy.getConfigType().isBonjourOnly()){
							whereSql = "((deviceType = :s1 AND hiveApModel in (:s2) AND configTemplate in (:s3) AND manageStatus in (:s4)) "+
							"OR id in (select distinct bo3.hiveApId from "+ConfigTemplate.class.getSimpleName()+
							" as bo1 join bo1.vpnService as bo2 join bo2.vpnGateWaysSetting as bo3 where bo1.id = :s5))";
							
							List<Short> bonjourSptList = DevicePropertyManage.getInstance().getSupportDeviceList(DeviceInfo.SPT_BONJOUR_SERVICE);
							if(getUserContext().isSourceFromIdm()) {
								whereSql = whereSql + " and softVer>:s6";
								filterParams = new FilterParams(whereSql,
										new Object[]{HiveAp.Device_TYPE_HIVEAP, bonjourSptList, tempList, status, selectedPolicy.getId(),"5.1.2.0"});
							} else {
								filterParams = new FilterParams(whereSql,
										new Object[]{HiveAp.Device_TYPE_HIVEAP, bonjourSptList, tempList, status, selectedPolicy.getId()});
							}
						}else{
							whereSql = "((deviceType != :s1 AND configTemplate in (:s2) AND manageStatus in (:s3)) "+
							"OR id in (select distinct bo3.hiveApId from "+ConfigTemplate.class.getSimpleName()+
							" as bo1 join bo1.vpnService as bo2 join bo2.vpnGateWaysSetting as bo3 where bo1.id = :s4))";
							if(getUserContext().isSourceFromIdm()) {
								whereSql = whereSql + " and softVer>:s5";
								filterParams = new FilterParams(whereSql,
										new Object[]{HiveAp.Device_TYPE_VPN_GATEWAY, tempList, status, selectedPolicy.getId(),"5.1.2.0"});
							} else {
								filterParams = new FilterParams(whereSql,
										new Object[]{HiveAp.Device_TYPE_VPN_GATEWAY, tempList, status, selectedPolicy.getId()});
							}

						}
					}else{
						 whereSql = "deviceType != :s1 AND configTemplate in (:s2) AND manageStatus in (:s3)";
						 if(getUserContext().isSourceFromIdm()) {
								whereSql = whereSql + " and softVer>:s4";
								filterParams = new FilterParams(whereSql,
										new Object[]{HiveAp.Device_TYPE_VPN_GATEWAY, tempList, status,"5.1.2.0"});
						} else {
							filterParams = new FilterParams(whereSql,
									new Object[]{HiveAp.Device_TYPE_VPN_GATEWAY, tempList, status});
						}
					}

//					if (isJsonMode()){
//						setFilter("Current Policy");
//					}
				}
			} else if("managedVPNGateways".equals(listType)){
				String viewType = (String) MgrUtil.getSessionAttribute(MANAGED_LIST_VIEW);
				if("config".equals(viewType)){
					setSelectedL2Feature(L2_FEATURE_CONFIG_VPN_GATEWAYS);
				}else{
					setSelectedL2Feature(L2_FEATURE_VPN_GATEWAYS);
				}

				List<Short> status = new ArrayList<Short>();
				status.add(HiveAp.STATUS_MANAGED);
				status.add(HiveAp.STATUS_NEW);
				
				List<Short> deviceTypes = new ArrayList<Short>();
				deviceTypes.add(HiveAp.Device_TYPE_VPN_GATEWAY);
				deviceTypes.add(HiveAp.Device_TYPE_VPN_BR);
				
				filterParams = new FilterParams("(deviceType in (:s1) or hiveApModel in (:s2)) AND manageStatus in (:s3)",
						new Object[]{deviceTypes, getCVGList(), status});
			} else if("managedRouters".equals(listType)){
				String viewType = (String) MgrUtil
				.getSessionAttribute(MANAGED_LIST_VIEW);
				if("config".equals(viewType)){
					setSelectedL2Feature(L2_FEATURE_CONFIG_BRANCH_ROUTERS);
				}else{
					setSelectedL2Feature(L2_FEATURE_BRANCH_ROUTERS);
				}

				List<Short> status = new ArrayList<Short>();
				status.add(HiveAp.STATUS_MANAGED);
				status.add(HiveAp.STATUS_NEW);
				filterParams = new FilterParams("deviceType = :s1 AND manageStatus in :s2", new Object[]{HiveAp.Device_TYPE_BRANCH_ROUTER, status});
			} else if("managedSwitches".equals(listType)){
				String viewType = (String) MgrUtil
				.getSessionAttribute(MANAGED_LIST_VIEW);
				if("config".equals(viewType)){
					setSelectedL2Feature(L2_FEATURE_CONFIG_SWITCHES);
				}else{
					setSelectedL2Feature(L2_FEATURE_SWITCHES);
				}

				List<Short> status = new ArrayList<Short>();
				status.add(HiveAp.STATUS_MANAGED);
				status.add(HiveAp.STATUS_NEW);
				filterParams = new FilterParams("deviceType = :s1 AND manageStatus in :s2", new Object[]{HiveAp.Device_TYPE_SWITCH, status});
			} else if("managedDeviceAPs".equals(listType)){
				String viewType = (String) MgrUtil
				.getSessionAttribute(MANAGED_LIST_VIEW);
				if("config".equals(viewType)){
					setSelectedL2Feature(L2_FEATURE_CONFIG_DEVICE_HIVEAPS);
				}else{
					setSelectedL2Feature(L2_FEATURE_DEVICE_HIVEAPS);
				}
				List<Short> status = new ArrayList<Short>();
				status.add(HiveAp.STATUS_MANAGED);
				status.add(HiveAp.STATUS_NEW);
				if(this.isEasyMode()){
					//express model not support BR100, BR200, BR200-WP, CVG
					List<Short> modelList = new ArrayList<Short>();					
					modelList.addAll(getCVGList());
					modelList.addAll(getBRList());
					filterParams = new FilterParams("deviceType = :s1 AND hiveApModel not in (:s2) AND manageStatus in :s3",
							new Object[]{HiveAp.Device_TYPE_HIVEAP, modelList, status});
				}else{
					filterParams = new FilterParams("deviceType = :s1 AND hiveApModel not in (:s2) AND manageStatus in :s3",
							new Object[]{HiveAp.Device_TYPE_HIVEAP, getCVGList(), status});
				}
			}
			resetPermission();
			setCurrentTableId();

			String forward = globalForward();
			if (forward != null) {
				return forward;
			}

			Map<String,String> cusMap=DeviceTagUtil.getInstance().getClassifierCustomTag(this.getDomainId());
			customTag1=cusMap.get(DeviceTagUtil.CUSTOM_TAG1);
			customTag2=cusMap.get(DeviceTagUtil.CUSTOM_TAG2);
			customTag3=cusMap.get(DeviceTagUtil.CUSTOM_TAG3);



			if (null == operation || "managedHiveAps".equals(operation)
					|| "manageAPEx".equals(operation)
					|| "manageAPGuid".equals(operation)) {
				// the initial come into list view.
				saveFilter();
				if (!"manageAPEx".equals(operation)){
					removeLastExConfigGuide();
					removeExConfigGuideFeature();
				}
				return prepareHiveApList(true);
			} else if ("new2".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("hiveAp.title.hiveAp"))) {
					return getLstForward();
				}
				setTabId(0);
				HiveAp hiveAp = new HiveAp(hiveApModel);
				if (getDiMenuTypeKey()!=null) {
					hiveAp.setDiMenuTypeKey(getDiMenuTypeKey());
				}
				if (getPre_serialNumber()!=null) {
					hiveAp.setSerialNumber(getPre_serialNumber());
				}
				hiveAp.setOwner(this.getDomain());
				setSessionDataSource(hiveAp);
				getDataSource().setVpnIpTrack(QueryUtil.findBoByAttribute(MgmtServiceIPTrack.class, "trackName",
					BeParaModule.DEFAULT_IP_TRACKING_VPN_GATEWAY_NAME_NEW));
				prepareInitialParameters();
				prepareDependentObjects();
				return getDevicePage();
			} else if ("newGuid".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("hiveAp.title.hiveAp"))) {
					return getLstForward();
				}
				setTabId(0);
				HiveAp hiveAp = new HiveAp(hiveApModel);
				hiveAp.setOwner(this.getDomain());
				setSessionDataSource(hiveAp);
				getDataSource().setVpnIpTrack(QueryUtil.findBoByAttribute(MgmtServiceIPTrack.class, "trackName",
					BeParaModule.DEFAULT_IP_TRACKING_VPN_GATEWAY_NAME_NEW));
				prepareInitialParameters();
				prepareDependentObjects();
				return getDevicePageJson();
			} else if ("edit2".equals(operation)) {
				setTabId(0);
				// findBoById(boClass, id, this);
				if(this.getExConfigGuideFeature() != null && null != dataSource
						&& dataSource.getId() != null && dataSource.getId().equals(this.id)){
					getSessionDataSource();
				}else{
					setSessionDataSource(findBoById(boClass, id, this));
				}
				if (getDiMenuTypeKey()!=null) {
					getDataSource().setDiMenuTypeKey(getDiMenuTypeKey());
				}
				if(this.getDataSource().getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY){
					synchronizeCVGInterfaceState(this.getDataSource());
				}
				if (dataSource == null) {
					if(isJsonMode()){
						if(isVpnGateWayDlg()){
							return "hiveapDlg";
						}
						return getDevicePageJson();
					}
					return prepareHiveApList(true);
				} else {
					addLstTitle(getText("hiveAp.title.hiveAp.edit") + " '"
							+ getChangedHiveApName() + "'");
					prepareConfigTemplateForTag();
					prepareDependentObjects();
					prepareAhDataTableDataForForwardingDB();
					prepareAhIgmpTableData();
//					PortGroupProfile portProfile= this.getDataSource().getPortGroup(getDataSource().getConfigTemplate());
//				    prepareSwitchDeviceInterface(portProfile);
					prepareAhMulticastGroupTableData();
					if(!isVpnGateWayDlg()){
						if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
							return getDevicePageJson();
						} else {
							return getDevicePage();
						}
					}else{
						return "hiveapDlg";
					}
				}
			} else if ("editHiveApGuid".equals(operation)) {
				setTabId(0);
				setSessionDataSource(findBoById(boClass, id, this));
				if (dataSource == null) {
					return prepareHiveApList(true);
				} else {
					synchronizeCVGInterfaceState(this.getDataSource());
					prepareConfigTemplateForTag();
					prepareDependentObjects();
					prepareAhIgmpTableData();
					prepareAhMulticastGroupTableData();
					prepareAhDataTableDataForForwardingDB();
					return getDevicePageJson();
				}
			}else if ("create2".equals(operation)) {
				log.info("execute", "operation:" + operation);
				jsonObject = new JSONObject();
				if (null == dataSource) {
					log.error("execute", "dataSource is null");
					return prepareHiveApList(true);
				}
				setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setSelectedPreferredSsids();
				setWifiRadioMode(getDataSource());
				updateStaticRoutes();
				setDeviceInterface();
				setSelectedStormControl();
				setCustomizedNasIdentifier();
				updateDeviceStpSettings();
				//Set forwarding db
				if(!checkAndSetForwardingDB()){
					prepareDependentObjects();
					prepareForForwardingDBError();
					if ((this.getExConfigGuideFeature() != null && "hiveapEx"
							.equals(this.getExConfigGuideFeature()))
							|| this.isJsonMode()) {
						return getDevicePageJson();
					} else {
						// setSelectedObjects();
						return getDevicePage();
					}
				}

				//Validate the preferred SSID
				if(!checkPreferredSsids()){
					prepareDependentObjects();
					if ((this.getExConfigGuideFeature() != null && "hiveapEx"
							.equals(this.getExConfigGuideFeature()))
							|| this.isJsonMode()) {
						return getDevicePageJson();
					} else {
						// setSelectedObjects();
						return getDevicePage();
					}
				}

				//Set igmp policy
				if(!checkIgmpPolicyVlanId()){
					prepareDependentObjects();
					if ((this.getExConfigGuideFeature() != null && "hiveapEx"
							.equals(this.getExConfigGuideFeature()))
							|| this.isJsonMode()) {
						return getDevicePageJson();
					} else {
						// setSelectedObjects();
						return getDevicePage();
					}
				}
				if(!checkMulticastGroupVlanIDandIp()){
					prepareDependentObjects();
					if ((this.getExConfigGuideFeature() != null && "hiveapEx"
							.equals(this.getExConfigGuideFeature()))
							|| this.isJsonMode()) {
						return getDevicePageJson();
					} else {
						// setSelectedObjects();
						return getDevicePage();
					}
				}

				if (!checkMstpTable()){
					prepareDependentObjects();
					if ((this.getExConfigGuideFeature() != null && "hiveapEx"
							.equals(this.getExConfigGuideFeature()))
							|| this.isJsonMode()) {
						return getDevicePageJson();
					} else {
						return getDevicePage();
					}
				}

				// upper case macAddress
				getDataSource().setMacAddress(
						getDataSource().getMacAddress().toUpperCase());
				if (isHiveApMacAddressExist(domainId, getDataSource()
						.getMacAddress())) {
					addActionError(MgrUtil.getUserMessage(
							"error.macAddressExists", getDataSource()
									.getMacAddress()));
					setTabId(0);
					prepareDependentObjects();
					if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
						return getDevicePageJson();
					} else {
						// setSelectedObjects();
					return getDevicePage();
					}
				}
				if (isHMOnline() && getDataSource().getDiMenuTypeKey()!=null) {
					if (isHiveApSerialNumberExist(domainId, getDataSource()
							.getSerialNumber())) {
						addActionError(MgrUtil.getUserMessage(
								"error.serialNumberExists", getDataSource()
										.getSerialNumber()));
						setTabId(0);
						prepareDependentObjects();
						if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
							return getDevicePageJson();
						} else {
							// setSelectedObjects();
						return getDevicePage();
						}
					}
				}
				if (!checkAllUpdateOrCreate()) {
					prepareDependentObjects();
					if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
						return getDevicePageJson();
					} else {
					// setSelectedObjects();
					return getDevicePage();
					}
				}
				if (!checkIsNetworkPolicyProper4Device(getDataSource())) {
					prepareDependentObjects();
					if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
						return getDevicePageJson();
					} else {
						return getDevicePage();
					}
				}
				if(!checkBrStaticRoute()){
					if(isJsonMode() && isVpnGateWayDlg()){
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", errMsgTmp);
						return "json";
					}
					prepareDependentObjects();
					if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
						return getDevicePageJson();
					} else {
					// setSelectedObjects();
						return getDevicePage();
					}
				}
				
				try{
					SensorModeUtil.checkSensorFeatures(getDataSource());
					//check WIPS configuration in network policy for radio profile check-box andn Sensor mode configuration
					SensorModeUtil.checkWIPSConfiguration(getDataSource());
				}catch(Exception ex){
					 addActionError(ex.getMessage());
					 prepareDependentObjects();
					  if ((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature()))
								|| this.isJsonMode()) {
							return getDevicePageJson();
						} else {
							return getDevicePage();
					 }
				}
				
				String cfgIp = getDataSource().getCfgIpAddress();
				String cfgNm = getDataSource().getCfgNetmask();
				String cfgGw = getDataSource().getCfgGateway();
				if (!getDataSource().isDhcp()) {
					getDataSource().setIpAddress(cfgIp);
					getDataSource().setNetmask(cfgNm);
					getDataSource().setGateway(cfgGw);
				}
				if (!overrideVlan) {
					getDataSource().setNativeVlan(0);
				}
				if (!overrideMgtVlan) {
					getDataSource().setMgtVlan(0);
				}

				updateIpPrefix(getDataSource());
				// create capwapIp object if needed;
				IpAddress capwapIp = autoCreateIpAddress();
				if (null != capwapIp) {
					getDataSource().setCapwapIpBind(capwapIp);
				}
				IpAddress capwapBackupIp = autoCreateBackupIpAddress();
				if (null != capwapBackupIp) {
					getDataSource().setCapwapBackupIpBind(capwapBackupIp);
				}

				updateRoutingProfile(getDataSource());

				// ip track only for VPN Gateway
				if (HiveAp.Device_TYPE_VPN_GATEWAY != getDataSource().getDeviceType()) {
					getDataSource().setVpnIpTrack(null);
				}

				updateForwardingDB(getDataSource());

				updateStormContorl(getDataSource());

				if(getDataSource().getDeviceInfo().isSptEthernetMore_24()){
					prepareSaveIgmpPolicy();
					prepareSaveMulticastGroup();
					
					if(getDataSource().getManagementType() == HiveAp.MANAGERMENT_TYPE_DYNAMIC){
						getDataSource().setEnablePoeLldp(false);
					}
				}
				
				//update the switch of CAPWAP delay alarm
				if(isFullMode() && !getDataSource().isOverrideEnableDelayAlarm() && getDataSource().getDeviceType() != HiveAp.Device_TYPE_VPN_GATEWAY){
					getDataSource().setEnableDelayAlarm(getDataSource().getConfigTemplate().isEnableDelayAlarm());
				}
				
				createBo(dataSource);
				final HiveAp hiveAP = getDataSource();
				final Long vHMdomain = hiveAP.getOwner().getId();
				String[] tags = null;
				List<String> tagsStr = new ArrayList<>();
				
				if(null != hiveAP.getClassificationTag1() && !"".equals(hiveAP.getClassificationTag1())){
					tagsStr.add(hiveAP.getClassificationTag1());
				}
				if(null != hiveAP.getClassificationTag2() && !"".equals(hiveAP.getClassificationTag2())){
					tagsStr.add(hiveAP.getClassificationTag2());
				}
				if(null != hiveAP.getClassificationTag3() && !"".equals(hiveAP.getClassificationTag3())){
					tagsStr.add(hiveAP.getClassificationTag3());
				}
				if(null != tagsStr && tagsStr.size() > 0){
					tags = new String[tagsStr.size()];
					tagsStr.toArray(tags);
				}
				if(hiveAP.getMapContainer() == null){
					String sql = "select id from map_node where parent_map_id = " +
							"(select id from map_node where parent_map_id is null) and owner="+vHMdomain;
					List<?> list = QueryUtil.executeNativeQuery(sql, 1);
					if(!list.isEmpty()){
						NetworkDeviceConfigTracking.topologyChanged(Calendar.getInstance(),
								vHMdomain, hiveAP.getMacAddress(), hiveAP.getTimeZoneOffset(), new long[]{Long.parseLong(list.get(0).toString())}, tags);
					}
				}else{
					NetworkDeviceConfigTracking.topologyChanged(Calendar.getInstance(),
							vHMdomain, hiveAP.getMacAddress(), hiveAP.getTimeZoneOffset(), new long[]{hiveAP.getMapContainer().getId()}, tags);   //TODO  topologyGroupPkFromTop(exclusive)ToBottom(inclusive)
				}

				if(this.isJsonMode()){
					jsonObject = new JSONObject();
					jsonObject.put("t", true);
					return "json";
				}else{
					return prepareHiveApList(true);
				}
			} else if ("createHiveApGuid".equals(operation)) {
				log.info("execute", "operation:" + operation);
				if (null == dataSource) {
					log.error("execute", "dataSource is null");
					return prepareHiveApList(true);
				}
				setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setWifiRadioMode(getDataSource());
				updateStaticRoutes();
				setDeviceInterface();
				setSelectedStormControl();
				updateDeviceStpSettings();
				prepareMstpInstanceDataTable();
				prepareMstpInstanceToDataTable();
				prepareAhIgmpTableData();
				prepareAhMulticastGroupDataTableColumnDefs(getDataSource());
				prepareAhMulticastGroupTableData();
				
				// upper case macAddress
				getDataSource().setMacAddress(
						getDataSource().getMacAddress().toUpperCase());
				if (isHiveApMacAddressExist(domainId, getDataSource()
						.getMacAddress())) {
					addActionError(MgrUtil.getUserMessage(
							"error.macAddressExists", getDataSource()
									.getMacAddress()));
					setTabId(0);
					prepareDependentObjects();
					return getDevicePageJson();
				}
				if (!checkAllUpdateOrCreate()) {
					prepareDependentObjects();
					return getDevicePageJson();
				}
				if (!checkIsNetworkPolicyProper4Device(getDataSource())) {
					prepareDependentObjects();
					return getDevicePageJson();
				}
				String cfgIp = getDataSource().getCfgIpAddress();
				String cfgNm = getDataSource().getCfgNetmask();
				String cfgGw = getDataSource().getCfgGateway();
				if (!getDataSource().isDhcp()) {
					getDataSource().setIpAddress(cfgIp);
					getDataSource().setNetmask(cfgNm);
					getDataSource().setGateway(cfgGw);
				}
				if (!overrideVlan) {
					getDataSource().setNativeVlan(0);
				}
				if (!overrideMgtVlan) {
					getDataSource().setMgtVlan(0);
				}
				updateIpPrefix(getDataSource());
				// create capwapIp object if needed;
				IpAddress capwapIp = autoCreateIpAddress();
				if (null != capwapIp) {
					getDataSource().setCapwapIpBind(capwapIp);
				}
				IpAddress capwapBackupIp = autoCreateBackupIpAddress();
				if (null != capwapBackupIp) {
					getDataSource().setCapwapBackupIpBind(capwapBackupIp);
				}
				// ip track only for VPN Gateway
				if (HiveAp.Device_TYPE_VPN_GATEWAY != getDataSource().getDeviceType()) {
					getDataSource().setVpnIpTrack(null);
				}
				createBo(dataSource);

				final HiveAp hiveAP = getDataSource();
				final Long vHMdomain = hiveAP.getOwner().getId();
				String[] tags = null;
				List<String> tagsStr = new ArrayList<>();
				
				if(null != hiveAP.getClassificationTag1() && !"".equals(hiveAP.getClassificationTag1())){
					tagsStr.add(hiveAP.getClassificationTag1());
				}
				if(null != hiveAP.getClassificationTag2() && !"".equals(hiveAP.getClassificationTag2())){
					tagsStr.add(hiveAP.getClassificationTag2());
				}
				if(null != hiveAP.getClassificationTag3() && !"".equals(hiveAP.getClassificationTag3())){
					tagsStr.add(hiveAP.getClassificationTag3());
				}
				if(null != tagsStr && tagsStr.size() > 0){
					tags = new String[tagsStr.size()];
					tagsStr.toArray(tags);
				}
				if(hiveAP.getMapContainer() == null){
					String sql = "select id from map_node where parent_map_id = " +
							"(select id from map_node where parent_map_id is null) and owner="+vHMdomain;
					List<?> list = QueryUtil.executeNativeQuery(sql, 1);
					if(!list.isEmpty()){
						NetworkDeviceConfigTracking.topologyChanged(Calendar.getInstance(),
								vHMdomain, hiveAP.getMacAddress(), hiveAP.getTimeZoneOffset(), new long[]{Long.parseLong(list.get(0).toString())}, tags);
					}
				}else{
					NetworkDeviceConfigTracking.topologyChanged(Calendar.getInstance(),
							vHMdomain, hiveAP.getMacAddress(), hiveAP.getTimeZoneOffset(), new long[]{hiveAP.getMapContainer().getId()}, tags);   //TODO  topologyGroupPkFromTop(exclusive)ToBottom(inclusive)
				}
				return prepareHiveApList(true);
			}else if (("update2"+ getLstForward()).equals(operation) || "update2".equals(operation)) {
				jsonObject = new JSONObject();
				log.info("execute", "operation:" + operation);
				if (null == dataSource) {
					if(isJsonMode() && isVpnGateWayDlg()){
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", "dataSource is null");
						return "json";
					}else{
						log.error("execute", "dataSource is null");
						return prepareHiveApList(true);
					}
				}
				if (getDataSource().isDhcp()
						&& !getDataSource().isDhcpFallback()) {
					getDataSource().setCfgGateway(null);
				}
				setDependentObjects();
				//Set forwarding db
				if(!checkAndSetForwardingDB()){
					prepareDependentObjects();
					if ((this.getExConfigGuideFeature() != null && "hiveapEx"
							.equals(this.getExConfigGuideFeature()))
							|| this.isJsonMode()) {
						return getDevicePageJson();
					} else {
						// setSelectedObjects();
						return getDevicePage();
					}
				}

				//Validate storm control
				if(!checkStormControl()){
					prepareDependentObjects();
					if ((this.getExConfigGuideFeature() != null && "hiveapEx"
							.equals(this.getExConfigGuideFeature()))
							|| this.isJsonMode()) {
						return getDevicePageJson();
					} else {
						// setSelectedObjects();
						return getDevicePage();
					}
				}
				
				//Validate the preferred SSID
				if(!checkPreferredSsids()){
					prepareDependentObjects();
					if ((this.getExConfigGuideFeature() != null && "hiveapEx"
							.equals(this.getExConfigGuideFeature()))
							|| this.isJsonMode()) {
						return getDevicePageJson();
					} else {
						// setSelectedObjects();
						return getDevicePage();
					}
				}

				//Set igmp policy
				if(!checkIgmpPolicyVlanId()){
					prepareDependentObjects();
					if ((this.getExConfigGuideFeature() != null && "hiveapEx"
							.equals(this.getExConfigGuideFeature()))
							|| this.isJsonMode()) {
						return getDevicePageJson();
					} else {
						// setSelectedObjects();
						return getDevicePage();
					}
				}
				if(!checkMulticastGroupVlanIDandIp()){
					prepareDependentObjects();
					if ((this.getExConfigGuideFeature() != null && "hiveapEx"
							.equals(this.getExConfigGuideFeature()))
							|| this.isJsonMode()) {
						return getDevicePageJson();
					} else {
						// setSelectedObjects();
						return getDevicePage();
					}
				}

				if (!checkMstpTable()){
					prepareDependentObjects();
					if ((this.getExConfigGuideFeature() != null && "hiveapEx"
							.equals(this.getExConfigGuideFeature()))
							|| this.isJsonMode()) {
						return getDevicePageJson();
					} else {
						return getDevicePage();
					}
				}

				if (!checkAllUpdateOrCreate()) {
					if(isJsonMode() && isVpnGateWayDlg()){
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", errMsgTmp);
						return "json";
					}
					prepareDependentObjects();
					if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
						return getDevicePageJson();
					} else {
					// setSelectedObjects();
						return getDevicePage();
					}
				}
				if (!checkIsNetworkPolicyProper4Device(getDataSource())) {
					if(isJsonMode() && isVpnGateWayDlg()){
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", errMsgTmp);
						return "json";
					}
					prepareDependentObjects();
					if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
						return getDevicePageJson();
					} else {
					// setSelectedObjects();
						return getDevicePage();
					}
				}
				if(!checkBrStaticRoute()){
					if(isJsonMode() && isVpnGateWayDlg()){
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", errMsgTmp);
						return "json";
					}
					prepareDependentObjects();
					if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
						return getDevicePageJson();
					} else {
					// setSelectedObjects();
						return getDevicePage();
					}
				}
				
				try{
					SensorModeUtil.checkSensorFeatures(getDataSource());
					//check WIPS configuration in network policy for radio profile check-box andn Sensor mode configuration
					SensorModeUtil.checkWIPSConfiguration(getDataSource());
				}catch(Exception ex){
					 addActionError(ex.getMessage());
					 prepareDependentObjects();
					  if ((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature()))
								|| this.isJsonMode()) {
							return getDevicePageJson();
						} else {
							return getDevicePage();
					 }
				}
				
				// if the Pass phrase is not match the current value,
				// update the keyId also;
				updateKeyId(getDataSource());
				if (!overrideVlan) {
					getDataSource().setNativeVlan(0);
				}
				if (!overrideMgtVlan) {
					getDataSource().setMgtVlan(0);
				}
				updateIpPrefix(getDataSource());
				IpAddress capwapIp = autoCreateIpAddress();
				if (null != capwapIp) {
					getDataSource().setCapwapIpBind(capwapIp);
				}
				IpAddress capwapBackupIp = autoCreateBackupIpAddress();
				if (null != capwapBackupIp) {
					getDataSource().setCapwapBackupIpBind(capwapBackupIp);
				}

				updateRoutingProfile(getDataSource());
				updateForwardingDB(getDataSource());
				updateStormContorl(getDataSource());
				if(getDataSource().getDeviceInfo().isSptEthernetMore_24()){
					removeExistIgmpPolicy(getDataSource());
					prepareSaveIgmpPolicy();
					removeExistMulticastGroup(getDataSource());
					prepareSaveMulticastGroup();
					
					if(getDataSource().getManagementType() == HiveAp.MANAGERMENT_TYPE_DYNAMIC){
						getDataSource().setEnablePoeLldp(false);
					}
				}
				//getDataSource().setId(this.id);
				//update the switch of CAPWAP delay alarm
				if(isFullMode() && !getDataSource().isOverrideEnableDelayAlarm() && getDataSource().getDeviceType() != HiveAp.Device_TYPE_VPN_GATEWAY){
					getDataSource().setEnableDelayAlarm(getDataSource().getConfigTemplate().isEnableDelayAlarm());
				}
				try{
					updateBo(getDataSource());
				}catch(Exception e){
					errMsgTmp = MgrUtil.getUserMessage(e);
					addActionError(errMsgTmp);
					if(isJsonMode() && isVpnGateWayDlg()){
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", errMsgTmp);
						return "json";
					}
					prepareDependentObjects();
					if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
						return getDevicePageJson();
					} else {
					// setSelectedObjects();
						return getDevicePage();
					}
				}

				final HiveAp hiveAP = getDataSource();
				final Long vHMdomain = hiveAP.getOwner().getId();
				String[] tags = null;
				List<String> tagsStr = new ArrayList<>();
				
				if(null != hiveAP.getClassificationTag1() && !"".equals(hiveAP.getClassificationTag1())){
					tagsStr.add(hiveAP.getClassificationTag1());
				}
				if(null != hiveAP.getClassificationTag2() && !"".equals(hiveAP.getClassificationTag2())){
					tagsStr.add(hiveAP.getClassificationTag2());
				}
				if(null != hiveAP.getClassificationTag3() && !"".equals(hiveAP.getClassificationTag3())){
					tagsStr.add(hiveAP.getClassificationTag3());
				}
				if(null != tagsStr && tagsStr.size() > 0){
					tags = new String[tagsStr.size()];
					tagsStr.toArray(tags);
				}
				if(hiveAP.getMapContainer() == null){
					String sql = "select id from map_node where parent_map_id = " +
							"(select id from map_node where parent_map_id is null) and owner="+vHMdomain;
					List<?> list = QueryUtil.executeNativeQuery(sql, 1);
					if(!list.isEmpty()){
						NetworkDeviceConfigTracking.topologyChanged(Calendar.getInstance(),
								vHMdomain, hiveAP.getMacAddress(), hiveAP.getTimeZoneOffset(), new long[]{Long.parseLong(list.get(0).toString())}, tags);
					}
				}else{
					NetworkDeviceConfigTracking.topologyChanged(Calendar.getInstance(),
							vHMdomain, hiveAP.getMacAddress(), hiveAP.getTimeZoneOffset(), new long[]{hiveAP.getMapContainer().getId()}, tags);   //TODO  topologyGroupPkFromTop(exclusive)ToBottom(inclusive)
				}

				if(isEasyMode()) {// prompt the warning message after successful update in express mode
					MgrUtil.setSessionAttribute(GUIDED_CONFIG_WARNING_MSG, true);
				}
				if(this.isJsonMode()){
					jsonObject.put("t", true);
					jsonObject.put("resultStatus", true);
					jsonObject.put("id",getDataSource().getId());
					jsonObject.put("name", getDataSource().getHostName());
					jsonObject.put("parentDomID", getParentDomID());
					return "json";
				} else if (getLstForward() != null && !"".equals(getLstForward())){
					setUpdateContext(true);
					return getLstForward();
				} else {
					return prepareHiveApList(true);
				}
			} else if ("clone2".equals(operation)) {
				setTabId(0);
				long cloneId = getSelectedIds().get(0);
				HiveAp cloneAp = (HiveAp) findBoById(boClass, cloneId, this);
				cloneAp.setId(null);
				cloneAp.setVersion(null);
				cloneAp.setHostName("");
				cloneAp.setMacAddress("");
				// As the cloned AP not configured, so set status to New
				cloneAp.setManageStatus(HiveAp.STATUS_NEW);
				//
				List<HiveApStaticRoute> staticRoutes = new ArrayList<HiveApStaticRoute>();
				for (HiveApStaticRoute staticRoute : cloneAp.getStaticRoutes()) {
					staticRoutes.add(staticRoute);
				}
				cloneAp.setStaticRoutes(staticRoutes);

				List<HiveApDynamicRoute> dynamicRoutes = new ArrayList<HiveApDynamicRoute>();
				for (HiveApDynamicRoute dynamicRoute : cloneAp
						.getDynamicRoutes()) {
					dynamicRoutes.add(dynamicRoute);
				}
				cloneAp.setDynamicRoutes(dynamicRoutes);
				//add by nxma
				List<HiveApMultipleVlan> multipleVlans = new ArrayList<HiveApMultipleVlan>();
				for (HiveApMultipleVlan multipleVlan : cloneAp
						.getMultipleVlan()) {
					multipleVlans.add(multipleVlan);
				}
				cloneAp.setMultipleVlan(multipleVlans);

				List<HiveApIpRoute> ipRoutes = new ArrayList<HiveApIpRoute>();
				for (HiveApIpRoute ipRoute : cloneAp.getIpRoutes()) {
					ipRoutes.add(ipRoute);
				}
				cloneAp.setIpRoutes(ipRoutes);

				List<HiveAPVirtualConnection> virtualConnects = new ArrayList<HiveAPVirtualConnection>();
				for (HiveAPVirtualConnection virtualConnection : cloneAp.getVirtualConnections()) {
					virtualConnects.add(virtualConnection);
				}
				cloneAp.setVirtualConnections(virtualConnects);

				List<HiveApL3cfgNeighbor> l3Neighbors = new ArrayList<HiveApL3cfgNeighbor>();
				for (HiveApL3cfgNeighbor l3Neighbor : cloneAp.getL3Neighbors()) {
					l3Neighbors.add(l3Neighbor);
				}
				cloneAp.setL3Neighbors(l3Neighbors);
				Set<VlanDhcpServer> dhcpServers = new HashSet<VlanDhcpServer>();
				for (VlanDhcpServer dhcpServer : cloneAp.getDhcpServers()) {
					dhcpServers.add(dhcpServer);
				}
				cloneAp.setDhcpServers(dhcpServers);
				List<HiveApSsidAllocation> ssids = new ArrayList<HiveApSsidAllocation>();
				for (HiveApSsidAllocation ssid : cloneAp.getDisabledSsids()) {
					ssids.add(ssid);
				}
				cloneAp.setDisabledSsids(ssids);
				List<HiveApLearningMac> macs = new ArrayList<HiveApLearningMac>();
				for (HiveApLearningMac mac : cloneAp.getLearningMacs()) {
					macs.add(mac);
				}
				cloneAp.setLearningMacs(macs);
				Set<UserProfile> userProfiles = new HashSet<UserProfile>();
				for (UserProfile userProfile : cloneAp
						.getEthCwpRadiusUserProfiles()) {
					userProfiles.add(userProfile);
				}
				cloneAp.setEthCwpRadiusUserProfiles(userProfiles);

				Map<Long, DeviceInterface> deviceInterfaces = new HashMap<Long, DeviceInterface>();
				for(Long key : cloneAp.getDeviceInterfaces().keySet()){
					deviceInterfaces.put(key, cloneAp.getDeviceInterfaces().get(key));
				}
				cloneAp.setDeviceInterfaces(deviceInterfaces);

				List<USBModemProfile> usbModemList = new ArrayList<USBModemProfile>();
				for(USBModemProfile usbProfile : cloneAp.getUsbModemList()){
					usbModemList.add(usbProfile);
				}
				cloneAp.setUsbModemList(usbModemList);


				List<HiveApMultipleVlan> multipleVlan = new ArrayList<HiveApMultipleVlan>();
				for(HiveApMultipleVlan mVlan : cloneAp.getMultipleVlan()){
					multipleVlan.add(mVlan);
				}
				cloneAp.setMultipleVlan(multipleVlan);

				List<HiveApInternalNetwork> internalNetworks = new ArrayList<HiveApInternalNetwork>();
				for (HiveApInternalNetwork intNetwork : cloneAp.getInternalNetworks()) {
					internalNetworks.add(intNetwork);
				}
				cloneAp.setInternalNetworks(internalNetworks);

				List<HiveApPreferredSsid> preferredSsids = new ArrayList<HiveApPreferredSsid>();
				for(HiveApPreferredSsid preferredSsid : cloneAp.getWifiClientPreferredSsids()){
					preferredSsids.add(preferredSsid);
				}
				cloneAp.setWifiClientPreferredSsids(preferredSsids);

				setSessionDataSource(cloneAp);
				prepareDependentObjects();
				addLstTitle(getText("hiveAp.title.hiveAp"));
				return getDevicePage();
			} else if ("multiEdit".equals(operation)) {
				setTabId(0);
				setSessionDataSource(new HiveAp());
				if (getDiMenuTypeKey()!=null) {
					getDataSource().setDiMenuTypeKey(getDiMenuTypeKey());
				}
				List<Long> lstSelectIds = getSelectedHiveApIds();
				if (null == lstSelectIds || lstSelectIds.isEmpty()) {
					log.error("execute", "lstSelectIds is null");
					return prepareHiveApList(true);
				}
				// hide by fnr
//				String editError = checkForMultiEdit(lstSelectIds);
//				if(editError != null && !"".equals(editError)){
//					this.addActionError(editError);
//					return prepareHiveApList(true);
//				}
				MgrUtil.setSessionAttribute("selectHiveAPs", lstSelectIds);
				addLstTitle(getText("hiveAp.title.hiveAp.modify.multi"));

				prepareDataSourceMultiple(lstSelectIds);

				prepareDependentObjectsMultiple(lstSelectIds);

				capwapIp = -3L;// set to no change by default
				capwapBackupIp = -3L;
				getDataSource().setCapwapText(strNoChange);
				getDataSource().setCapwapBackupText(strNoChange);
				getDataSource().setDistributedPriority((short)-3);
				getDataSource().setPriority(strNoChange);
				getDataSource().setRealmName(strNoChange);
				return isJsonMode()?"multiEditJson" :"multiEdit";
			} else if ("multiUpdate".equals(operation)) {
				if (dataSource != null) {
					List<HiveAp> list = getMultipleEditHiveAPs();
					if (list.isEmpty()) {
						log.error("execute", "selected HiveAP is null");
						return prepareHiveApList(true);
					}
					//fnr hide, no more need check it.
//					if (!verify11nInterfaces(getDataSource())) {
//						prepareDependentObjectsMultiple();
//						return "multiEdit";
//					}
					setSelectedObjects();
					setDeviceInterface();
					//fnr hide, no more need check it.
//					if (getDataSource().isWifi1Available()) {
//						if (null != wifi0RadioProfile
//								&& null != wifi1RadioProfile
//								&& wifi0RadioProfile > 0
//								&& wifi1RadioProfile > 0) {
//							// both changed, verify with current data source
//							// setting;
//							if (!verifyRadioFailover(getDataSource()
//									.getWifi0RadioProfile(), getDataSource()
//									.getWifi1RadioProfile(), null, getDataSource().getSoftVer())) {
//								prepareDependentObjectsMultiple();
//								return "multiEdit";
//							}
//						} else if (null != wifi0RadioProfile
//								&& wifi0RadioProfile > 0) {
//							// wifi0 changed, wifi1 not change, and wifi0 to
//							// fail
//							// over
//							if (null != getDataSource().getWifi0RadioProfile()
//									&& getDataSource().getWifi0RadioProfile()
//											.getBackhaulFailover()) {
//								for (HiveAp hiveAp : list) {
//									if (!verifyRadioFailover(getDataSource()
//											.getWifi0RadioProfile(), hiveAp
//											.getWifi1RadioProfile(), hiveAp
//											.getHostName(), hiveAp.getSoftVer())) {
//										prepareDependentObjectsMultiple();
//										return "multiEdit";
//									}
//								}
//							}
//						} else if (null != wifi1RadioProfile
//								&& wifi1RadioProfile > 0) {
//							// wifi1 changed, wifi0 not change, and wifi1 to
//							// fail
//							// over
//							if (null != getDataSource().getWifi1RadioProfile()
//									&& getDataSource().getWifi1RadioProfile()
//											.getBackhaulFailover()) {
//								for (HiveAp hiveAp : list) {
//									if (!verifyRadioFailover(hiveAp
//											.getWifi0RadioProfile(),
//											getDataSource()
//													.getWifi1RadioProfile(),
//											hiveAp.getHostName(), hiveAp.getSoftVer())) {
//										prepareDependentObjectsMultiple();
//										return "multiEdit";
//									}
//								}
//							}
//						}
//					}
					if (null != wifi0RadioProfile && wifi0RadioProfile != -1
							&& wifi0RadioProfile != -3) {
						int currentChannelNum = getDataSource().getWifi0()
								.getChannel();
						for (HiveAp hiveAp : list) {
							int channelNum = currentChannelNum > 0 ? currentChannelNum
									: hiveAp.getWifi0().getChannel();
							if (!verifyRadioTurboMode(getDataSource()
									.getWifi0RadioProfile(), channelNum, hiveAp
									.getCountryCode(), hiveAp.getHostName())) {
								prepareDependentObjectsMultiple(getMultipleEditIds());
								return isJsonMode()?"multiEditJson" :"multiEdit";
							}
						}
					}
					//for ap370 validation 11ac mode
					if (null != wifi1RadioProfile && wifi1RadioProfile != -1
							&& wifi1RadioProfile != -3) {
						RadioProfile radioProfile = QueryUtil.findBoById(RadioProfile.class,wifi1RadioProfile);
						if (null != radioProfile
								&& radioProfile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC) {
							if(!verify11acRadioProfile(list)){
								   prepareDependentObjectsMultiple(getMultipleEditIds());
								   return isJsonMode()?"multiEditJson" : "multiEdit";
								}
						}
					}
					
					if (null != wifi1RadioProfile && wifi1RadioProfile != -1
							&& wifi1RadioProfile != -3) {
						int currentChannelNum = getDataSource().getWifi1()
								.getChannel();
						for (HiveAp hiveAp : list) {
							int channelNum = currentChannelNum > 0 ? currentChannelNum
									: hiveAp.getWifi1().getChannel();
							if (!verifyRadioTurboMode(getDataSource()
									.getWifi1RadioProfile(), channelNum, hiveAp
									.getCountryCode(), hiveAp.getHostName())) {
								prepareDependentObjectsMultiple(getMultipleEditIds());
								return isJsonMode()?"multiEditJson" :"multiEdit";
							}
						}
					}
					short vpnMark = getDataSource().getVpnMark();
					if (null != configTemplate && configTemplate != -1
							&& configTemplate != -3) {
						ConfigTemplate configTemplateTmp = QueryUtil.findBoById(ConfigTemplate.class, configTemplate, this);
						for (HiveAp hiveAp : list) {
							// verify user setting
							if (!verifyUserUserGroupCount(hiveAp,
									configTemplateTmp, hiveAp
											.getRadiusServerProfile(), hiveAp
											.getHostName())) {
								prepareDependentObjectsMultiple(getMultipleEditIds());
								return isJsonMode()?"multiEditJson" :"multiEdit";
							}
							if (configTemplateTmp != null && !checkIsNetworkPolicyProper4DeviceMulti(hiveAp, configTemplateTmp)) {
								prepareDependentObjectsMultiple(getMultipleEditIds());
								return isJsonMode()?"multiEditJson" :"multiEdit";
							}
						}
						if (!verifyMutipleVpnClients(getDataSource()
								.getConfigTemplate(), vpnMark)) {
							prepareDependentObjectsMultiple(getMultipleEditIds());
							return isJsonMode()?"multiEditJson" :"multiEdit";
						}
					} else if (vpnMark == HiveAp.VPN_MARK_CLIENT
							&& configTemplate == -3) {
						if (!verifyMutipleVpnClients()) {
							prepareDependentObjectsMultiple(getMultipleEditIds());
							return isJsonMode()?"multiEditJson" :"multiEdit";
						}
					}
					if (null != configTemplate && configTemplate != -1
							&& configTemplate != -3) {
						for (HiveAp hiveAp : list) {
							// verify config template settings
							if (!verifyConfigTemplate(hiveAp, getDataSource()
									.getConfigTemplate(), hiveAp.getHostName())) {
								prepareDependentObjectsMultiple(getMultipleEditIds());
								return isJsonMode()?"multiEditJson" :"multiEdit";
							}
						}
					}
					if (!verifyRealSimulateHiveAp(getDataSource()
							.getMapContainer(), list)) {
						prepareDependentObjectsMultiple(getMultipleEditIds());
						return isJsonMode()?"multiEditJson" :"multiEdit";
					}
					if (!verifyPresenceEnabledDeviceCount(list, getDataSource()
							.getWifi0RadioProfile(), getDataSource()
							.getWifi1RadioProfile())) {
						prepareDependentObjectsMultiple(getMultipleEditIds());
						return isJsonMode() ? "multiEditJson" : "multiEdit";
					}
					setModifyValues(list);
				}
				if(this.isJsonMode()){
					jsonObject = new JSONObject();
					jsonObject.put("t", true);
					return "json";
				}else{
					return prepareHiveApList(true);
				}
			} else if ("newTemplate".equals(operation)
					|| "newWifi0RadioProfile".equals(operation)
					|| "newWifi1RadioProfile".equals(operation)
					|| "newCapwapIp".equals(operation)
					|| "newCapwapBackupIp".equals(operation)
					|| "newIpTrack".equals(operation)
					|| "newScheduler".equals(operation)
					|| "newRadius".equals(operation)
					|| "newRadiusProxy".equals(operation)
					|| "newVpn".equals(operation)
					|| "newDhcpServer".equals(operation)
					|| "newUserProfileEth0".equals(operation)
					|| "newUserProfileEth1".equals(operation)
					|| "newUserProfileAgg0".equals(operation)
					|| "newUserProfileRed0".equals(operation)
					|| "newMacAddressEth0".equals(operation)
					|| "newMacAddressEth1".equals(operation)
					|| "newMacAddressAgg0".equals(operation)
					|| "newMacAddressRed0".equals(operation)
					|| "newEthCwpUserprofile".equals(operation)
					|| "newEthCwpDefaultAuthUserProfile".equals(operation)
					|| "newEthCwpDefaultRegUserProfile".equals(operation)
					|| "newEthCwpCwpProfile".equals(operation)
					|| "newEthCwpRadiusClient".equals(operation)
					|| "newRouting".equals(operation)
					|| "newMgtDns".equals(operation)
					|| "newMgtTime".equals(operation)
					|| "newMgtSyslog".equals(operation)
					|| "newMgtSnmp".equals(operation)
					|| "newWifiClientPreferredSsid".equals(operation)
					|| "editTemplate".equals(operation)
					|| "editWifi0RadioProfile".equals(operation)
					|| "editWifi1RadioProfile".equals(operation)
					|| "editCapwapIp".equals(operation)
					|| "editCapwapBackupIp".equals(operation)
					|| "editIpTrack".equals(operation)
					|| "editScheduler".equals(operation)
					|| "editRadius".equals(operation)
					|| "editRadiusProxy".equals(operation)
					|| "editVpn".equals(operation)
					|| "editDhcpServer".equals(operation)
					|| "editUserProfileEth0".equals(operation)
					|| "editUserProfileEth1".equals(operation)
					|| "editUserProfileAgg0".equals(operation)
					|| "editUserProfileRed0".equals(operation)
					|| "editMacAddressEth0".equals(operation)
					|| "editMacAddressEth1".equals(operation)
					|| "editMacAddressAgg0".equals(operation)
					|| "editMacAddressRed0".equals(operation)
					|| "editEthCwpUserprofile".equals(operation)
					|| "editEthCwpDefaultAuthUserProfile".equals(operation)
					|| "editEthCwpDefaultRegUserProfile".equals(operation)
					|| "editEthCwpCwpProfile".equals(operation)
					|| "editEthCwpRadiusClient".equals(operation)
					|| "editRouting".equals(operation)
					|| "editMgtDns".equals(operation)
					|| "editMgtTime".equals(operation)
					|| "editMgtSyslog".equals(operation)
					|| "editMgtSnmp".equals(operation)
					|| "editWifiClientPreferredSsid".equals(operation)
					|| "newPPPoE".equals(operation)
					|| "editPPPoE".equals(operation)
					|| "newRoutingPbrPolicy".equals(operation)
					|| "editRoutingPbrPolicy".equals(operation)
					|| "newLldpCdp".equals(operation)
					|| "editLldpCdp".equals(operation)
					|| "newMstpRegion".equals(operation)
					|| "editMstpRegion".equals(operation)
					|| "newConfigmdmPolicy".equals(operation)
					|| "editConfigmdmPolicy".equals(operation)
					|| "newSuppCLI".equals(operation)
					|| "editSuppCLI".equals(operation)) {
				setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setSelectedPreferredSsids();
				clearErrorsAndMessages();
				setDeviceInterface();
				setSelectedStormControl();
				updateDeviceStpSettings();
				prepareMstpInstanceDataTable();
				prepareMstpInstanceToDataTable();
				prepareAhDaTableTransientDataForForwardingDB();
				if (operation.toLowerCase().indexOf("userprofile") > 0){
					prepareNetworkPolicyWirelessRouting();
				}
				if ("newWifi1RadioProfile".equals(operation)
						|| "editWifi1RadioProfile".equals(operation)
						|| "newUserProfileEth1".equals(operation)
						//|| "editUserProfileEth1".equals(operation)) {
						|| "editUserProfileEth1".equals(operation)
						//|| "newCapwapIp".equals(operation)|| "editCapwapIp".equals(operation)
						|| "newCapwapBackupIp".equals(operation)|| "editCapwapBackupIp".equals(operation)
						|| "newMgtDns".equals(operation)|| "editMgtDns".equals(operation)
						|| "newMgtTime".equals(operation)|| "editMgtTime".equals(operation)
						|| "newMgtSyslog".equals(operation)|| "editMgtSyslog".equals(operation)
						|| "newMgtSnmp".equals(operation)|| "editMgtSnmp".equals(operation)) {
					addLstForward("hiveAp2");
				} else if ("newUserProfileAgg0".equals(operation)
						|| "editUserProfileAgg0".equals(operation)) {
					addLstForward("hiveAp3");
				} else if ("newUserProfileRed0".equals(operation)
						|| "editUserProfileRed0".equals(operation)) {
					addLstForward("hiveAp4");
				} else if ("newEthCwpUserprofile".equals(operation)
						|| "editEthCwpUserprofile".equals(operation)) {
					addLstForward("hiveAp5");
				} else if ("newEthCwpDefaultAuthUserProfile".equals(operation)
						|| "editEthCwpDefaultAuthUserProfile".equals(operation)) {
					addLstForward("hiveAp6");
				} else if ("newEthCwpDefaultRegUserProfile".equals(operation)
						|| "editEthCwpDefaultRegUserProfile".equals(operation)) {
					addLstForward("hiveAp7");
				} else {
					addLstForward("hiveAp");
				}
// hide for this fix bug 16059
//				if ("newRadius".equals(operation)
//					|| "newRadiusProxy".equals(operation)
//					|| "editRadius".equals(operation)
//					|| "editRadiusProxy".equals(operation)){
//					if (!isNewEditRadiusForBrFlg() && getDataSource().getDeviceType()==HiveAp.Device_TYPE_BRANCH_ROUTER) {
//						setNewEditRadiusForBrFlg(true);
//					}
//				}
				addLstTabId(tabId);
				return operation;
			} else if ("newTemplateMulti".equals(operation)
					|| "newWifi0RadioProfileMulti".equals(operation)
					|| "newWifi1RadioProfileMulti".equals(operation)
					|| "newCapwapIpMulti".equals(operation)
					|| "newCapwapBackupIpMulti".equals(operation)
					|| "newSchedulerMulti".equals(operation)
					|| "editTemplateMulti".equals(operation)
					|| "editWifi0RadioProfileMulti".equals(operation)
					|| "editWifi1RadioProfileMulti".equals(operation)
					|| "editCapwapIpMulti".equals(operation)
					|| "editCapwapBackupIpMulti".equals(operation)
					|| "editSchedulerMulti".equals(operation)
					|| "newSuppCLIMulti".equals(operation)
					|| "editSuppCLIMulti".equals(operation)) {
				setSelectedObjects();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setDeviceInterface();
				setSelectedStormControl();
				clearErrorsAndMessages();
				if ("newCapwapBackupIpMulti".equals(operation)
						|| "editCapwapBackupIpMulti".equals(operation)
						|| "newWifi1RadioProfileMulti".equals(operation)
						|| "editWifi1RadioProfileMulti".equals(operation)) {
					addLstForward("hiveApMulti2");
				} else {
					addLstForward("hiveApMulti");
				}
				addLstTabId(tabId);
				return operation;
			} else if ("continue".equals(operation)
					|| "continueMulti".equals(operation)) {
				if (null == dataSource) {
					return prepareHiveApList(true);
				} else {
					setId(dataSource.getId());
					setSelectedObjects();
					setTabId(getLstTabId());
					// clear temp radio profile
					resetTempRadioProfile();

					if (getUpdateContext()) {
						removeLstTitle();
						MgrUtil
								.setSessionAttribute("CURRENT_TABID",
										getTabId());
						removeLstForward();
						removeLstTabId();
						setUpdateContext(false);
					} else {
						setTabId(Integer.parseInt(MgrUtil.getSessionAttribute(
								"CURRENT_TABID").toString()));
					}
					if ("continueMulti".equals(operation)) {
						Set<Long> tempSelectIDs = new HashSet<Long>();
						tempSelectIDs.addAll(getMultipleEditIds());
						prepareDependentObjectsMultiple(tempSelectIDs);
						setAllSelectedIds(tempSelectIDs);
						return "multiEdit";
					} else {
						prepareDependentObjects();
						prepareAhDataTableDataAfterEditForForwardingDB();
						if(this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) {
							return "guidedConfiguration";
						} else if(isJsonMode()&& isVpnGateWayDlg()){
							return "hiveapDlg";
						} else {
							return getDevicePage();
						}
					}
				}
			} else if ("addStaticRoute2".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				updateStaticRoutes();
				addNewStaticRoute();
				/*setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				prepareDependentObjects();
				setDeviceInterface();
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return "hiveapEx";
				} else {
				return getDevicePage();
				}*/
				return "json";
			} else if ("removeStaticRoutes2".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				updateStaticRoutes();
				removeSelectedStaticRoutes();
				/*setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				prepareDependentObjects();
				setDeviceInterface();
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return "hiveapEx";
				} else {
				return getDevicePage();
				}*/
				return "json";
			} else if ("addDynamicRoute2".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				addNewDynamicRoute();
				/*setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setDeviceInterface();
				prepareDependentObjects();
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return "hiveapEx";
				} else {
				return getDevicePage();
				}*/
				return "json";
			} else if ("removeDynamicRoutes2".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				removeSelectedDynamicRoutes();
				/*setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setDeviceInterface();
				prepareDependentObjects();
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return "hiveapEx";
				} else {
				return getDevicePage();
				}*/
				return "json";
			} else if("removeUSBConnection2".equals(operation)){
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				removeSelectedUsbConnection();
				setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setDeviceInterface();
				prepareDependentObjects();
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return getDevicePageJson();
				} else {
					return getDevicePage();
				}
			} else if ("addStaticRoute".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				addNewRoutingProfileStaticRoutes();

				return "json";
			} else if ("removeStaticRouteNone".equals(operation) || "removeStaticRoute".equals(operation)) {
				removeSelectedRoutingProfileStaticRoutes();
				jsonObject = new JSONObject();
				jsonObject.put("t",true);
				jsonObject.put("operation", "removeStaticRoute");
				jsonObject.put("staticRoute", getStaticRouteInfoForHtml());
				jsonObject.put("gridCount", getDataSource().getIpRoutes().size());
				return "json";
			} else if ("addIntNetwork".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				addNewCVGIntNetwork();
				return "json";
			} else if ("addBrStaticRouting".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				addNewBrStaticRouting();
				return "json";
			} else if ("removeIntNetwork".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				removeSelectedIntNetwork();
				return "json";
			} else if("removeBrStaticRouting".equals(operation)){
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				removeSelectedBrStaticRouting();
				return "json";
			}  else if ("addIpRoute2".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				addNewIpRoute();
				return "json";
			} else if ("removeIpRoutes2".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				removeSelectedIpRoutes();
				/*setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setDeviceInterface();
				prepareDependentObjects();
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return "hiveapEx";
				} else {
				return getDevicePage();
				}*/
				return "json";
 			} else if ("addMultipleVlan".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				addNewMultipleVlan();
				/*setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setDeviceInterface();
				prepareDependentObjects();
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return "hiveapEx";
				} else {
				return getDevicePage();
				}*/
				return "json";
			} else if ("removeMultipleVlan".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				removeSelectedMultipleVlan();//removeSelectedIpRoutes();
				/*setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setDeviceInterface();
				prepareDependentObjects();
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return "hiveapEx";
				} else {
				return getDevicePage();
				}*/
				return "json";
			}else if ("addIpRouteMulti".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				addNewIpRoute();
				/*setSelectedObjects();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				//prepareDependentObjects();
				prepareDependentObjectsMultiple();
				return "multiEdit";*/
				return "json";
			} else if ("removeIpRouteMulti".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				removeSelectedIpRoutes();
				/*setSelectedObjects();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				//prepareDependentObjects();
				prepareDependentObjectsMultiple();
				return "multiEdit";*/
				return "json";
			} else if ("addVirtualConnect".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				addNewVirtualConnect();
				/*setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setDeviceInterface();
				prepareDependentObjects();
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return getDevicePageJson();
				} else {
				return getDevicePage();
				}*/
				return "json";
			} else if ("removeVirtualConnect".equals(operation)) {
				if (dataSource == null) {
					return prepareHiveApList(true);
				}
				removeSelectedVirtualConnect();
				/*setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setDeviceInterface();
				prepareDependentObjects();
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return getDevicePageJson();
				} else {
					return getDevicePage();
				}*/
				return "json";
			} else if ("createWifi0RadioProfile".equals(operation)) {
				if (this.isJsonMode()) {
					clearErrorsAndMessages();
					createWifi0RadioProfile();
					if (!getActionErrors().isEmpty()) {
						if (jsonObject ==null) {
							jsonObject = new JSONObject();
						}
						jsonObject.put("errbg", getActionErrors().toArray()[0]);
					}
					return "json";
				} else {
					setSelectedInterfaces();
					setSelectedObjects();
					setSelectedLearningMac();
					setSelectedNeighbor();
					setSelectedDhcpServer();
					setDeviceInterface();
					createWifi0RadioProfile();
					prepareDependentObjects();
				}
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return getDevicePageJson();
				} else {
					return getDevicePage();
				}
			} else if ("updateWifi0RadioProfile".equals(operation)) {
				setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setDeviceInterface();
				updateWifi0RadioProfile();
				prepareDependentObjects();
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return getDevicePageJson();
				} else {
					return getDevicePage();
				}
			} else if ("clearWifi0RadioProfile".equals(operation)) {
				setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setDeviceInterface();
				clearWifi0RadioProfile();
				prepareDependentObjects();
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return getDevicePageJson();
				} else {
					return getDevicePage();
				}
			} else if ("createWifi1RadioProfile".equals(operation)) {
				if (this.isJsonMode()) {
					clearErrorsAndMessages();
					createWifi1RadioProfile();
					if (!getActionErrors().isEmpty()) {
						if (jsonObject ==null) {
							jsonObject = new JSONObject();
						}
						jsonObject.put("erra", getActionErrors().toArray()[0]);
					}
					return "json";
				} else {
					setSelectedInterfaces();
					setSelectedObjects();
					setSelectedLearningMac();
					setSelectedNeighbor();
					setSelectedDhcpServer();
					setDeviceInterface();
					createWifi1RadioProfile();
					prepareDependentObjects();
				}
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return getDevicePageJson();
				} else {
					return getDevicePage();
				}
			} else if ("updateWifi1RadioProfile".equals(operation)) {
				setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setDeviceInterface();
				updateWifi1RadioProfile();
				prepareDependentObjects();
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return getDevicePageJson();
				} else {
					return getDevicePage();
				}
			} else if ("clearWifi1RadioProfile".equals(operation)) {
				setSelectedInterfaces();
				setSelectedObjects();
				setSelectedLearningMac();
				setSelectedNeighbor();
				setSelectedDhcpServer();
				setDeviceInterface();
				clearWifi1RadioProfile();
				prepareDependentObjects();
				if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
					return getDevicePageJson();
				} else {
					return getDevicePage();
				}
			} else if ("accept".equals(operation)) {
				acceptOperation();
				return prepareHiveApList(true);
			} else if ("upgradeConfiguration".equals(operation)
					|| "upgradeImage".equals(operation)
					|| "upgradeSignature".equals(operation)
					|| "upgradeBootstrap".equals(operation)
					|| "upgradeCountryCode".equals(operation)
					|| "upgradeCwp".equals(operation)
					|| "upgradeCert".equals(operation)
					|| "upgradeConfigurationS".equals(operation)
					|| "upgradeLocalUserS".equals(operation)
					|| "upgradePskS".equals(operation)
					|| "upgradePoe".equals(operation)
					|| "upgradeVpn".equals(operation)
					|| "upgradeNetdump".equals(operation)
					|| "upgradeOutdoor".equals(operation)) {
				log.debug("execute", "update hiveAP :" + operation);
				setupInitialUpdateList();
				return operation;
			} else if ("updates".equals(operation)) {
				log.debug("execute", "Updates from cache: " + cacheId);
				HiveApPagingCache hiveApPagingCache = getHiveApListCache();
				jsonArray = new JSONArray(hiveApPagingCache.getUpdates(cacheId));
				return "json";
			} else if ("refreshFromCache".equals(operation)) {
				log.debug("execute", "Refresh from cache: " + cacheId);
				return prepareHiveApList(false);
			} else if ("refreshAfterResetConfig".equals(operation)) {
				//log.debug("execute", "Refresh from cache: " + cacheId);
				
				filter = (String)MgrUtil.getSessionAttribute(MANAGED_HIVEAP_CURRENT_FILTER);
				setSelectedIds(null);
				setAllSelectedIds(null);
				return prepareHiveApList(true);
			} else if ("view".equals(operation)) {
				saveFilter();
				if (isJsonMode()) {
					MgrUtil.setSessionAttribute(MANAGED_HIVEAP_CURRENT_FILTER, filter);
				}
				return prepareHiveApList(true);
			} else if ("import".equals(operation)
					|| "importNew".equals(operation)) {
				addLstForward("import".equals(operation) ? "hiveAp"
						: "hiveApNew");
				clearErrorsAndMessages();
				return operation;
			} else if ("switch".equals(operation)) {
				log.info("execute", "switch to view:" + viewType + ", list view: " + listType);
				/*- fix bug when switch views 'config' vs 'monitor', show same node selected
				MgrUtil.setSessionAttribute(MANAGED_LIST_VIEW, "config"
						.equals(viewType) ? "config" : "monitor");
				if("config".equals(viewType)){
					setSelectedL2Feature(L2_FEATURE_CONFIG_HIVE_APS );
				}else{
					setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS  );
				}*/
				return prepareHiveApList(true);
			} else if ("showNeighborDetails".equals(operation)) {
				log.info("execute", "Neighbor HiveAP macAddress:" + macAddress
						+ ", domainId:" + domainId);
				jsonObject = showNeighborDetails();
				return "json";
			} else if ("showSsidDetails".equals(operation)) {
				log.info("execute", "SSID name:" + ssidName + ", domainId:"
						+ domainId);
				jsonObject = getSsidsBySsidName();
				return "json";
			} else if ("showHiveApDetails".equals(operation)) {
				log.info("execute", "show HiveAP details, id:" + id);
				return "hiveApDetails";
			} else if ("search".equals(operation)) {
				log.info("execute", "search operation");
				prepareSearchOperation();
				saveFilter();
				saveToSessionFilterList();
				return prepareHiveApList(true);
			} else if ("removeFilter".equals(operation)) {
				log.info("execute", "removeFilter operation");
				saveFilter();
				removeHiveApFilter();
				return prepareHiveApList(true);
			} else if ("requestFilterValues".equals(operation)) {
				log.info("execute", "requestFilterValues operation");
				jsonObject = prepareFilterValues();
				return "json";
			} else if ("fetchRadioProfiles".equals(operation)) {
				int countryCode = getDataSource().getCountryCode();
				if(apModelType == 0){
					apModelType = getDataSource().getHiveApModel();
				}
				log.info("execute", "operation:" + operation + ", apModelType:"
						+ apModelType + ", countryCode:" + countryCode
						+ ", wifi0OperationMode:" + wifi0OperationMode
						+ ", wifi1OperationMode:" + wifi1OperationMode);
				if (HiveAp.is11nHiveAP(apModelType)) {
					if ((AhConstantUtil.isTrueAll(Device.IS_SINGLERADIO, apModelType)
							&& !AhConstantUtil.isTrueAll(Device.IS_DUALBAND, apModelType))
							|| !AhConstantUtil.isTrueAll(Device.IS_SINGLERADIO, apModelType)) {
						jsonObject = get11nDualRadioProfiles(countryCode,
								wifi0OperationMode, wifi1OperationMode, apModelType);
					} else {
						jsonObject = get11nSingleRadioProfiles(countryCode,
								wifi0OperationMode);
					}
				} else {
					jsonObject = getAg20RadioProfiles(countryCode,
							wifi0OperationMode, wifi1OperationMode);
				}
				return "json";
			}else if ("wifi0Channels".equals(operation)) {
				int countryCode = getDataSource().getCountryCode();
				short hiveApModel = getDataSource().getHiveApModel();
				log.info("execute", "operation:" + operation
						+ ", wifi0 operation mode:" + wifi0OperationMode
						+ ", profile id:" + wifi0RadioProfile
						+ ", countryCode:" + countryCode);
				jsonObject = getRadioInfos(hiveApModel, wifi0RadioProfile,
						countryCode, wifi0OperationMode);
				return "json";
			} else if ("wifi1Channels".equals(operation)) {
				int countryCode = getDataSource().getCountryCode();
				short hiveApModel = getDataSource().getHiveApModel();
				log.info("execute", "operation:" + operation
						+ ", wifi1 operation mode:" + wifi1OperationMode
						+ ", profile id:" + wifi1RadioProfile
						+ ", countryCode:" + countryCode);
				jsonObject = getRadioInfos(hiveApModel, wifi1RadioProfile,
						countryCode, wifi1OperationMode);
				return "json";
			}else if ("wifi0ChannelsMulti".equals(operation)) {
				int countryCode = getDataSource().getCountryCode();
//				short hiveApModel = getDataSource().getHiveApModel();
				log.info("execute", "operation:" + operation
						+ ", wifi0 operation mode:" + wifi0OperationMode
						+ ", profile id:" + wifi0RadioProfile
						+ ", countryCode:" + countryCode);
				jsonObject = getRadioInfosMulti(wifi0RadioProfile, wifi0OperationMode, 0);
				return "json";
			} else if ("wifi1ChannelsMulti".equals(operation)) {
				int countryCode = getDataSource().getCountryCode();
//				short hiveApModel = getDataSource().getHiveApModel();
				log.info("execute", "operation:" + operation
						+ ", wifi1 operation mode:" + wifi1OperationMode
						+ ", profile id:" + wifi1RadioProfile
						+ ", countryCode:" + countryCode);
				jsonObject = getRadioInfosMulti(wifi1RadioProfile,wifi1OperationMode, 1);
				return "json";
			} else if("radioConfigTypeChanged".equals(operation)) {
				jsonObject = radioConfigTypeChanged(tempConfigTemplate);
				return "json";
			}
			else if ("requestTemplate".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", configtemplate id:" + configTemplate);
				
				
				if(this.getDataSource().isSwitchProduct()){
					ConfigTemplate	 template = QueryUtil.findBoById(
								ConfigTemplate.class, configTemplate, this);
					this.getDataSource().setConfigTemplate(template);
					 PortGroupProfile portProfile=	this.getDataSource().getPortGroup(template);
					setDependentObjects();
					 if(null!= switchWanPortSettings){
							switchWanPortSettings.clear();
						}
					prepareDependentObjects();
					revokeOverrideLLDPCDP();
					//generate realm name when change network policy
					if(null != getDataSource()){
						boolean lockRealmName = getDataSource().isLockRealmName();
						String oldRealmName = getDataSource().getRealmName();
						getDataSource().setRealmName(generateRealmName(configTemplate,topology,lockRealmName,oldRealmName));
						if(portProfile!=null){
							getDataSource().setPortTemplate(portProfile.getName());
						}else{
							getDataSource().setPortTemplate("");
						}
						
					}

					if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
						return getDevicePageJson();
					} else {
						return getDevicePage();
					}
				}else{
					revokeOverrideLLDPCDP();
					jsonObject = getTemplateInfo(configTemplate);
					return "json";
				}
			}else if ("requestCwp".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", cwp profile id:" + cwpProfile);
				jsonObject = getCwpInfo(cwpProfile);
				return "json";
			} else if ("reassignDomain".equals(operation)) {
				log.info("execute", "reassignDomain operation, domain name:"
						+ reassignDomainName);
				reassignOperation(reassignDomainName);
				return prepareHiveApList(true);
			} else if ("getTechBack".equals(operation)) {
				log.info("execute", "Get Tech back, message:" + message);
				if (null != message) {
					addActionError(message);
				}
				return prepareHiveApList(true);
			} else if ("rebootHiveAPs".equals(operation) || 
					"uploadWizard".equals(operation) || 
					"getDeviceCounts".equals(operation)) {
				log.info("execute", "Reboot HiveAPs");
				prepareSelectedDeviceIdStr();
				return operation;
			} else if ("invokeImage".equals(operation)) {
				log.info("execute", "Invoke HiveAP image, image type:"
						+ imageType);
				jsonObject = invokeHiveApImage(imageType);
				return "json";
			} else if ("collapseNewHiveAps".equals(operation)) {
				log.info("execute", "operation:" + operation);
				MgrUtil.setSessionAttribute(LIST_VIEW_COLLAPSE_STATUS,
						collapsed);
				return null;
			} else if ("syncSGE".equals(operation)) {
				log.info("execute", "operation:" + operation);
				jsonObject = syncSGE();
				return "json";
			} else if ("fetchSGEProgress".equals(operation)) {
				log.info("execute", "operation:" + operation);
				jsonObject = fetchSGEProgress();
				return "json";
			} else if ("uploadResultRefresh".equals(operation)) {
				JSONArray jsonList = new JSONArray(getRefreshGuidedHiveApListExpress());
				jsonObject = new JSONObject();
//				jsonObject.put("index", getPageIndex());
//				jsonObject.put("count", getPageCount());
				jsonObject.put("list", jsonList);
				return "json";
			} else if ("uploadResultRefreshGuid".equals(operation)) {
				prepareHiveApList(true);
				JSONArray jsonList = new JSONArray(getRefreshGuidedHiveApList());
				jsonObject = new JSONObject();
				jsonObject.put("list", jsonList);
				return "json";
			} else if("secondVPNGatewayChanged".equals(operation)) {
				jsonObject = new JSONObject();
				secondVpnGatewayRefresh(jsonObject);
				return "json";
			}else if("showPortDetails".equals(operation)){
				if (isConfigured()) {
					Map<String,String> wanIfInfoMap = new HashMap<>();//if-ip
					if(null != wanIfNums){
						for(int i=0;i<wanIfNums.length;i++){
							if("2".equals(wanIfConnTypes[i])){//static 
								String ifName = getIfName(wanIfNums[i]);
								if(ifName != null && !ifName.isEmpty()){
									wanIfInfoMap.put(getIfName(wanIfNums[i]), wanIfIpAndNetmasks[i]);
								}
							}
						}
					}
					prepareBrPortDetails(getDataSource(),wanIfInfoMap);
				}

				return "showPortDetails";
			}else if("cancel".equals(operation)){
				//fix bug 15058 start
				if (!isJsonMode()) {
					if (getLstForward() != null && !"".equals(getLstForward())){
						setUpdateContext(true);
						return getLstForward();
					}
				}
				return prepareHiveApList(true);
			}else if("deviceModeChange".equals(operation)){
				prepareDeviceTypeChange();
				if(this.getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_SR24 ||
						this.getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_SR2024P ||
						this.getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_SR2124P ||
						this.getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_SR2148P ||
						this.getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_SR48){
					ConfigTemplate template = QueryUtil.findBoById(ConfigTemplate.class, configTemplate, this);
				    PortGroupProfile portProfile=this.getDataSource().getPortGroup(template);
					if(portProfile!=null){
						getDataSource().setPortTemplate(portProfile.getName());
					}else{
						getDataSource().setPortTemplate("");
					}
					
					setDependentObjects();
					prepareDependentObjects();
					revokeOverrideLLDPCDP();
					
					if((this.getExConfigGuideFeature() != null && "hiveapEx".equals(this.getExConfigGuideFeature())) || this.isJsonMode()) {
						return getDevicePageJson();
					} else {
						return getDevicePage();
					}
				}else{
					jsonObject = deviceModeChange(this.getDataSource(),tempConfigTemplate);
					return "json";
				}
			}else if ("uploadConfigSucc4IDM".equals(operation)) {
                MgrUtil.setSessionAttribute(UPLOAD_CONFIG_IDM_KEY, true);
                jsonObject = new JSONObject();
                jsonObject.put("succ", true);
                return "json";
			} else if("retriveNetworkPolicySetting".equals(operation)){
				jsonObject = new JSONObject();
				JSONArray ja =  getOverrideNetworkPolicySetting();
				if(ja.length() > 0){
					jsonObject.put("succ", true);
					jsonObject.put("networkPlicyConfig",ja);
				}else{
					jsonObject.put("succ", false);
				}
                return "json";
			}else if ("generateRealmName".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", configtemplate id:" + configTemplate
						+", topologyMap id:" + topologyMapId);
				jsonObject = requestRealmName(configTemplate,topologyMapId,lockRealmName,oldRealmName);
				return "json";
			} else if ("exportDeviceInventory".equals(operation)) {
				return this.exportDeviceInventories(null);
			} else {
				// auto refresh setting start
				baseCustomizationOperationJson();
				if (jsonObject != null && jsonObject.length() > 0) {
					return "json";
				}
				// auto refresh setting end
				baseOperation();
				return prepareHiveApList(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setL3Features(null);
			return prepareActionError(e);
		}
	}

	private String getDevicePage(){
		if(this.getDataSource().getDeviceInfo().isSptEthernetMore_24()){
			return "devicePage";
		}else{
			return "input2";
		}
	}

	private String getDevicePageJson(){
		if(isEasyMode()){
			return "hiveapEx";
		}
		if(this.getDataSource().getDeviceInfo().isSptEthernetMore_24()){
			return "devicePageJsonOnly";
		}else{
			return "hiveapJsonOnly";
		}
	}

	/*start: br_port details*/
	private List<DeviceInterfaceAdapter> deviceInterfaceAdapters;

	public List<DeviceInterfaceAdapter> getDeviceInterfaceAdapters() {
		return deviceInterfaceAdapters;
	}

	public void setDeviceInterfaceAdapters(
			List<DeviceInterfaceAdapter> deviceInterfaceAdapters) {
		this.deviceInterfaceAdapters = deviceInterfaceAdapters;
	}

	private void prepareBrPortDetails(HiveAp hiveAp,Map<String,String> wanIfInfoMap) {
		DeviceInterfaceBundle portsBundle = new DeviceInterfaceBundle(hiveAp.getDeviceInterfaces(), hiveAp);
		portsBundle.initializeAccessModeString();
		portsBundle.preparePortsLinkStatus();
		portsBundle.prepareEthNetwork(wanIfInfoMap);
		
		if(hiveAp.isSwitchProduct()){
			if(!hiveAp.isOverrideNetworkPolicySetting()){
				ConfigTemplate template = hiveAp.getConfigTemplate();
				if(null != template){
					overridePortAdminStateByConfigTemplate(template,portsBundle.getDeviceInterfaceAdapters());
				}
			} else {
				overridePortAdminStateByAgg(portsBundle.getDeviceInterfaceAdapters(),hiveAp.getDeviceInterfaces());
			}
		}
		deviceInterfaceAdapters = portsBundle.getSortedDeviceInterfaces();
	}
	
	private void overridePortAdminStateByAgg(Map<String, DeviceInterfaceAdapter> deviceInterfaceAdapters,Map<Long, DeviceInterface> deviceInterfaces){
		if(deviceInterfaces != null && deviceInterfaceAdapters != null){
			for(DeviceInterface deviceInterface : deviceInterfaces.values()){
				if(deviceInterface != null && deviceInterface.isPortChannel()){
					for(short ifNo : deviceInterface.getMembers()){
						String portName = StringUtils.lowerCase(MgrUtil
								.getEnumString("enum.switch.interface."+ifNo));
						for(String key:deviceInterfaceAdapters.keySet()){
							if(key.equals(portName)){
								DeviceInterface dInt = deviceInterfaceAdapters.get(key).getDeviceInterface();
								dInt.setAdminState(deviceInterface.getAdminState());
							}
						}
					}
				}
			}
		}
	} 

	public boolean isConfigured(){
		String mac = getDataSource().getMacAddress();
		if(mac == null || mac.isEmpty()){
			return false;
		} else {
			List<SubNetworkResource> subResources = QueryUtil.executeQuery(SubNetworkResource.class, null, new FilterParams("lower(hiveapmac)",mac.toLowerCase()));
			if (subResources.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/*end: br_port details*/


	@Override
	protected String prepareActionError(Exception e) throws Exception {
		log.error("prepareHiveApActionError", MgrUtil.getUserMessage(e), e);
		addActionError(MgrUtil.getUserMessage(e));
		try {
			return prepareHiveApList(true);
		} catch (Exception ne) {
			clearDataSource();
			page = new ArrayList<>();
			return listType;
		}
	}

	public String getChangedHiveApName() {
		return getDataSource().getHostName().replace("\\", "\\\\").replace("'",
				"\\'");
	}

	private List<String> getStaticRouteInfoForHtml() {
		List<String> strList = new ArrayList<String>();
		int checkboxValue = 0;
		for (HiveApIpRoute ipRoute : getDataSource().getIpRoutes()) {

			String td0str = "<input id='hiveAp_routingProfilesStaticRoutesIndices' type='checkbox' value='" +checkboxValue + "' name='routingProfilesStaticRoutesIndices'>"
				+ " <input type='hidden' value='"+checkboxValue+"' name='__checkbox_routingProfilesStaticRoutesIndices'> ";
			String td1str = ipRoute.getSourceIp();
			String td2str = ipRoute.getNetmask();
			String td3str = ipRoute.getGateway();
			String strCheck = ipRoute.isDistributeBR()? "checked" : "";
			String td4str = "<input type='checkbox' name='distributeBR' disabled='true' "
					+ strCheck + " />";

			String trstr = td0str+","+td1str+","+td2str+","+td3str+","+td4str;
			strList.add(trstr);
			checkboxValue ++;
		}

		return strList;
	}

	private boolean verifyInterfaceOperationMode(HiveAp hiveAp,
			boolean withHostname) {
		boolean isAnyBackhaul = false;
		if (hiveAp.isEth1Available()) {
			short ethSetup = hiveAp.getEthConfigType();
			if (ethSetup == HiveAp.USE_ETHERNET_AGG0) {
				if (hiveAp.getAgg0().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL) {
					isAnyBackhaul = true;
				}
			} else if (ethSetup == HiveAp.USE_ETHERNET_RED0) {
				if (hiveAp.getRed0().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL) {
					isAnyBackhaul = true;
				}
			} else {
				if (hiveAp.getEth0().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL) {
					isAnyBackhaul = true;
				}
				if (hiveAp.getEth1().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL) {
					isAnyBackhaul = true;
				}
			}
		} else {
			if (hiveAp.getEth0().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL) {
				isAnyBackhaul = true;
			}
		}

		if (hiveAp.isWifi1Available()) {
			if (hiveAp.getWifi1().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL ||
					hiveAp.getWifi1().getOperationMode() == AhInterface.OPERATION_MODE_DUAL) {
				isAnyBackhaul = true;
			}
		}
		if (hiveAp.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL ||
				hiveAp.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_DUAL) {
			isAnyBackhaul = true;
		}

		if (!isAnyBackhaul) {
			String msg = withHostname ? getText(
					"error.hiveAp.operationMode.backhaul.withHostname",
					new String[] { hiveAp.getHostName() })
					: getText("error.hiveAp.operationMode.backhaul");
			addActionError(msg);
			return false;
		}
		return true;
	}
	//verify the 11ac radio profile
	private boolean verify11acRadioProfile(List<HiveAp> list) {
		   List<HiveAp> noAp370List = new ArrayList<HiveAp>();
		   if(null != list && !list.isEmpty()){
			for(HiveAp hap : list){
			   if(!HiveAp.is11acHiveAP(hap.getHiveApModel())){
				noAp370List.add(hap);	
			   }	
			}
		   }
		   if(null != noAp370List && !noAp370List.isEmpty()){
		       StringBuffer sbf = new StringBuffer();
		       for(HiveAp ha : noAp370List){
		           sbf.append(ha.getLabel());
			   sbf.append(", ");
		       }
		       addActionError(getText("error.hiveAp.11ac.radio.profile",
						new String[] { sbf.toString().substring(0,(sbf.toString().length()-2)) }));
		       return false;
		   }
		   return true;
		}
	
	// verify the radio A mode profile and its channel
	private boolean verifyRadioTurboMode(RadioProfile radioProfile,
			int channelNum, int countryCode, String hostName) {
		if (null != radioProfile
				&& radioProfile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A) {
			if (!radioProfile.isTurboMode()) {
				// not support channel 42, 152, 160 when turbo mode disabled
				if (channelNum == 42 || channelNum == 152 || channelNum == 160) {
					String params[] = { hostName, String.valueOf(channelNum) };
					addActionError(getText("error.hiveAp.turbo.channel", params));
					return false;
				}
			} else {
				// validate country code support turbo mode
				if (!CountryCode.isAllowTurboMode(countryCode)) {
					String params[] = { hostName };
					addActionError(getText("error.hiveAp.turbo.countryCode",
							params));
					return false;
				}
			}
		}
		return true;
	}

	private boolean verifyRadioFailover(RadioProfile wifi0Profile,
			RadioProfile wifi1Profile, String withHostname, String softver) {
		List<String> errorMsg = new ArrayList<String>();
		boolean result = verifyRadioFailoverPublic(wifi0Profile, wifi1Profile,
				withHostname, errorMsg, softver);
		if (!errorMsg.isEmpty()) {
			addActionError(errorMsg.get(0));
		}
		return result;
	}

	public boolean verifyRadioFailoverPublic(RadioProfile wifi0Profile,
			RadioProfile wifi1Profile, String withHostname,
			List<String> errorMsg, String softver) {
		if (null != wifi0Profile && null != wifi1Profile
				&& wifi0Profile.getBackhaulFailover()
				&& wifi1Profile.getBackhaulFailover()
				&& softver != null && !"".equals(softver)
				&& NmsUtil.compareSoftwareVersion("4.0.1.0", softver) > 0
				&& !wifi0Profile.getDefaultFlag()
				&& !wifi1Profile.getDefaultFlag()) {
			if (null == errorMsg) {
				errorMsg = new ArrayList<String>();
			}
			String error;
			if (null != withHostname) {
				error = getText("error.hiveAp.backhaul.failover.withHostname",
						new String[] { withHostname });
			} else {
				error = getText("error.hiveAp.backhaul.failover");
			}
			// save error message;
			errorMsg.add(error);
			return false;
		}
		return true;
	}

	private boolean verifyConfigTemplate(HiveAp hiveAp,
			ConfigTemplate configTemp, String withHostname) throws Exception {
		List<String> errorMsg = new ArrayList<String>();
		boolean result = verifyConfigTemplatePublic(hiveAp, configTemp,
				errorMsg, withHostname);
		if (!errorMsg.isEmpty()) {
			addActionError(errorMsg.get(0));
		}
		return result;
	}

	public boolean verifyConfigTemplatePublic(HiveAp hiveAp,
			ConfigTemplate configTemp, List<String> errorMsg,
			String withHostname) throws Exception {
		if (null == errorMsg) {
			errorMsg = new ArrayList<String>();
		}
		if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_20 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_28) {
			if (!verifyBindingSsidCountForAg20(configTemp, errorMsg,
					withHostname)) {
				return false;
			}
		}
		return true;
	}

	private boolean verifyBindingSsidCountForAg20(
			ConfigTemplate configTemplate, List<String> errorMsg,
			String withHostname) {
		if (null != configTemplate) {
			int countA = 0;
			int countBg = 0;
			Map<Long, ConfigTemplateSsid> ssidInterfaces = configTemplate
					.getSsidInterfaces();
			if (null != ssidInterfaces) {
				for (ConfigTemplateSsid ctSsid : ssidInterfaces.values()) {
					SsidProfile ssid = ctSsid.getSsidProfile();
					if (null == ssid) {
						continue;
					}
					if (ssid.getRadioMode() == SsidProfile.RADIOMODE_BG) {
						countBg++;
					} else if (ssid.getRadioMode() == SsidProfile.RADIOMODE_A) {
						countA++;
					} else {
						countBg++;
						countA++;
					}
				}
			}
			if (countBg > MAX_SSID_FOR_AG20 || countA > MAX_SSID_FOR_AG20) {
				if (withHostname != null) {
					errorMsg.add(getText(
							"error.assignSsid.rangeForAg20.withHostname",
							new String[] { configTemplate.getConfigName(),
									withHostname }));
				} else {
					errorMsg.add(getText("error.assignSsid.rangeForAg20",
							new String[] { configTemplate.getConfigName() }));
				}
				return false;
			}
		}
		return true;
	}

	private boolean verify11nInterfaces(HiveAp hiveAp) throws Exception {
		List<String> errorMsg = new ArrayList<String>();
		boolean result = verify11nInterfacesPublic(hiveAp, errorMsg);

		if (!errorMsg.isEmpty()) {
			addActionError(errorMsg.get(0));
		}
		return result;
	}

	public boolean verify11nInterfacesPublic(HiveAp hiveAp,
			List<String> errorMsg) {
		if (hiveAp.isEth1Available()) {
			if (null == errorMsg) {
				errorMsg = new ArrayList<String>();
			}
			/*
			 * bind interface of eth0 and eth1 should be of the same type or
			 * none
			 */
			if (hiveAp.getEth0().getBindInterface() == AhInterface.ETH_BIND_IF_NULL
					|| hiveAp.getEth1().getBindInterface() == AhInterface.ETH_BIND_IF_NULL)
				return true;

			if (hiveAp.getEth0().getBindInterface() != hiveAp.getEth1()
					.getBindInterface()) {
				errorMsg.add(getText("error.bind.differentInterface"));
				return false;
			}

			/*
			 * bind role of eth0 and eth1 couldn't be primary at the same time
			 */
			if (hiveAp.getEth0().getBindInterface() == AhInterface.ETH_BIND_IF_RED0
					&& hiveAp.getEth1().getBindInterface() == AhInterface.ETH_BIND_IF_RED0) {
				if (hiveAp.getEth0().getBindRole() == AhInterface.ETH_BIND_ROLE_PRI
						&& hiveAp.getEth1().getBindRole() == AhInterface.ETH_BIND_ROLE_PRI) {
					errorMsg.add(getText("error.bind.sameRole"));
					return false;
				}
			}
		}
		return true;
	}

	private boolean verifyVpnServer(HiveAp hiveAp) {
		if (null != hiveAp && hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP) {
			String ipAddress = hiveAp.getCfgIpAddress();
			boolean isDhcp = hiveAp.isDhcp();
			if (null != hiveAp.getConfigTemplate()
					&& null != hiveAp.getConfigTemplate().getVpnService()) {
				VpnService service = hiveAp.getConfigTemplate().getVpnService();
				if(service.getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_2){
					String privateIp1 = service.getServerPrivateIp1();
					String privateIp2 = service.getServerPrivateIp2();
					if (hiveAp.getVpnMark() == HiveAp.VPN_MARK_SERVER) {
						if (isDhcp || null == ipAddress || "".equals(ipAddress)) {
							addActionError(MgrUtil
									.getUserMessage("error.hiveAp.vpnServer.term"));
							return false;
						}
						if (!ipAddress.equals(privateIp1)
								&& !ipAddress.equals(privateIp2)) {
							String params = privateIp1
									+ ((null == privateIp2 || "".equals(privateIp2)) ? ""
											: " or " + privateIp2);
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.vpnServer.binding", params));
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private boolean verifyVpnClient(ConfigTemplate wlan, HiveAp hiveAp) {
		if (null != hiveAp && hiveAp.getVpnMark() == HiveAp.VPN_MARK_CLIENT && hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP) {
			Set<Long> newClients = new HashSet<Long>(1);
			newClients.add(hiveAp.getId());
			String errorMsg = VpnServiceAction.verifyVpnClient(wlan.getId(),
					newClients);
			if (null != errorMsg && !"".equals(errorMsg)) {
				addActionError(errorMsg);
				return false;
			}
		}
		return true;
	}

	private boolean verifyMutipleVpnClients(ConfigTemplate wlan, short vpnMark) {
		List<Long> updateAps = getMultipleEditIds();
		if (null != wlan && null != updateAps) {
			Set<Long> newClients = null;
			if (vpnMark == -3) {
				// no change option, need to fetch the VPN clients
				String where = "vpnMark = :s1 and id in (:s2)";
				Object[] values = new Object[2];
				values[0] = HiveAp.VPN_MARK_CLIENT;
				values[1] = updateAps;
				FilterParams filterParams = new FilterParams(where, values);
				List<Long> list = (List<Long>) QueryUtil.executeQuery(
						"select id from " + HiveAp.class.getSimpleName(), null,
						filterParams);
				if (!list.isEmpty()) {
					newClients = new HashSet<Long>(list);
				}
			} else if (vpnMark == HiveAp.VPN_MARK_CLIENT) {
				newClients = new HashSet<Long>(updateAps);
			}

			if (null != newClients) {
				String errorMsg = VpnServiceAction.verifyVpnClient(
						wlan.getId(), newClients);
				if (null != errorMsg && !"".equals(errorMsg)) {
					addActionError(errorMsg);
					return false;
				}
			}
		}
		return true;
	}

	private boolean verifyMutipleVpnClients() {
		List<Long> updateAps = getMultipleEditIds();
		if (null != updateAps) {
			String query = "select id, configTemplate.id from "
					+ HiveAp.class.getSimpleName();
			List<?> list = QueryUtil.executeQuery(query, null,
					new FilterParams("id", updateAps));
			Map<Long, Set<Long>> mapping = new HashMap<Long, Set<Long>>();
			for (Object object : list) {
				Object[] attrs = (Object[]) object;
				Long apId = (Long) attrs[0];
				Long templateId = (Long) attrs[1];
				Set<Long> apIds = mapping.get(templateId);
				if (null == apIds) {
					apIds = new HashSet<Long>();
					mapping.put(templateId, apIds);
				}
				apIds.add(apId);
			}
			for (Long wlanId : mapping.keySet()) {
				String errorMsg = VpnServiceAction.verifyVpnClient(wlanId,
						mapping.get(wlanId));
				if (null != errorMsg && !"".equals(errorMsg)) {
					addActionError(errorMsg);
					return false;
				}
			}
		}
		return true;
	}

	private boolean verifyPresenceEnabledDeviceCount(List<HiveAp> devices,
			RadioProfile wifi0, RadioProfile wifi1) {
		if (!PresenceUtil.isPresenceSettingEnabled()) {
			// ignore if global switch off
			return true;
		}
		if (null == devices || devices.isEmpty()) {
			// not impact the count
			return true;
		}
		boolean presenceEnabled = false;
		if (null != wifi0 && wifi0.isEnabledPresence()) {
			presenceEnabled = true;
		}
		if (null != wifi1 && wifi1.isEnabledPresence()) {
			presenceEnabled = true;
		}
		if (!presenceEnabled) {
			// not impact the count
			return true;
		}

		Set<String> configMacs = new HashSet<String>();
		for (HiveAp device : devices) {
			configMacs.add(device.getMacAddress());
		}

		// search the whole instance, not special customer
		String query = "select bo.macAddress from "
				+ HiveAp.class.getCanonicalName() + " bo";
		String where = "bo.hiveApModel in (:s1) and (bo.wifi0RadioProfile.enabledPresence = :s2 or bo.wifi1RadioProfile.enabledPresence = :s2)";
		Object[] values = new Object[] {
				HiveApUtils.getPresenceSupportDeviceFilter(), true };
		List<?> macs = QueryUtil.executeQuery(query, null, new FilterParams(
				where, values));
		Set<String> totalMacs = new HashSet<String>(macs.size());
		for (Object mac : macs) {
			totalMacs.add((String) mac);
		}
		totalMacs.addAll(configMacs);
		if (totalMacs.size() > PresenceUtil.PRESENCE_MAX_DEVICE_COUNT) {
			addActionError(MgrUtil
					.getUserMessage("info.presence.reach.maximum"));
			return false;
		}
		return true;
	}

	private boolean verifyUserUserGroupCount(HiveAp hiveAp,
			ConfigTemplate wlan, RadiusOnHiveap radiusOnHiveAp,
			String withHostname) {
		if (null == wlan) {
			return true;
		}
		int maxUserGroupCount = LocalUserGroup.MAX_COUNT_AP_USERGROUPPERAP;
		int maxPmkUserCount = hiveAp.isDevicePPSK9999Support() ? LocalUser.MAX_COUNT_AP30_USERPERAP
				: LocalUser.MAX_COUNT_AP10_USERPERAP;
		int maxUserCount = hiveAp.isDevicePPSK9999Support() ? LocalUser.MAX_COUNT_AP30_USERCOUNT_PERAP
				: LocalUser.MAX_COUNT_AP10_USERCOUNT_PERAP;
		int maxUserCountPerGroup = hiveAp.isDevicePPSK9999Support() ? LocalUser.MAX_COUNT_AP30_LOCALUSER
				: LocalUser.MAX_COUNT_AP10_LOCALUSER;

		int[] radiusSizes = new int[] { 0, 0, 0 };
		Long radiusOnApId = radiusOnHiveAp==null? null:radiusOnHiveAp.getId();
		if (hiveAp.isBranchRouter() &&
				hiveAp.getHiveApModel()!=HiveAp.HIVEAP_MODEL_BR100) {
			if (hiveAp.isEnabledBrAsRadiusServer()) {
				radiusOnApId = wlan.getRadiusServerProfile()==null? null:wlan.getRadiusServerProfile().getId();
				radiusOnApId = radiusOnHiveAp==null? radiusOnApId:radiusOnHiveAp.getId();
			} else {
				radiusOnApId=null;
			}
		}

		if (null != radiusOnApId) {
			radiusSizes = RadiusOnHiveApAction
					.getNumberOfGroupAndUser(radiusOnApId);
		}
		int[] pskSizes = ConfigTemplateAction.getNumberOfGroupAndUser(wlan
				.getId());
		// validate user group count;
		long radiusUserGroupCount = radiusSizes[0];
		long pskUserGroupCount = ConfigTemplateAction.getTotalPskGroupId(
				wlan.getId()).size();
		if (radiusUserGroupCount + pskUserGroupCount > maxUserGroupCount) {
			if (null == withHostname) {
				addActionError(MgrUtil.getUserMessage(
						"error.be.config.create.MaxUserGroup", String
								.valueOf(maxUserGroupCount)));
			} else {
				addActionError(MgrUtil.getUserMessage(
						"error.be.config.create.withHostname.MaxUserGroup",
						new String[] { withHostname,
								String.valueOf(maxUserGroupCount) }));
			}
			return false;
		}

		long radiusUserCount = radiusSizes[1];
		// validate user count
		long pskUserCount = pskSizes[0];
		if (radiusUserCount + pskUserCount > maxUserCount) {
			if (null == withHostname) {
				addActionError(MgrUtil.getUserMessage(
						"error.be.config.create.MaxUser", String
								.valueOf(maxUserCount)));
			} else {
				addActionError(MgrUtil.getUserMessage(
						"error.be.config.create.withHostname.MaxUser",
						new String[] { withHostname,
								String.valueOf(maxUserCount) }));
			}
			return false;
		}
		// validate PMK user count
		long pmkUserCount = pskSizes[1];
		if (radiusUserCount + pmkUserCount > maxPmkUserCount) {
			if (null == withHostname) {
				addActionError(MgrUtil.getUserMessage(
						"error.be.config.create.MaxPmkUser", String
								.valueOf(maxPmkUserCount)));
			} else {
				addActionError(MgrUtil.getUserMessage(
						"error.be.config.create.withHostname.MaxPmkUser",
						new String[] { withHostname,
								String.valueOf(maxPmkUserCount) }));
			}
			return false;
		}
		// validate max user count per group for special AP
		if (!hiveAp.isDevicePPSK9999Support()) {
			long maxRadiusUserPerGroup = radiusSizes[2];
			long maxPskUserPerGroup = pskSizes[2];
			if (maxRadiusUserPerGroup + maxPskUserPerGroup > maxUserCountPerGroup) {
				if (null == withHostname) {
					addActionError(MgrUtil.getUserMessage(
							"error.be.config.create.MaxUserPerGroup", String
									.valueOf(maxUserCountPerGroup)));
				} else {
					addActionError(MgrUtil
							.getUserMessage(
									"error.be.config.create.withHostname.MaxUserPerGroup",
									new String[] { withHostname,
											String.valueOf(maxUserCountPerGroup) }));
				}
				return false;
			}
		}
		return true;
	}

	private boolean verifyRealSimulateHiveAp(MapContainerNode mapContainer,
			List<HiveAp> hiveAps) {
		if (!hiveAps.isEmpty() && null != mapContainer) {
			int realAPCount = 0;
			for (HiveAp hiveAp : hiveAps) {
				if (!hiveAp.isSimulated()) {
					realAPCount++;
				}
			}
			if (realAPCount > 0 && realAPCount != hiveAps.size()) {
				// part of simulated HiveAPs.
				addActionError(MgrUtil
						.getUserMessage("error.hiveAp.map.locate.mixed"));
				return false;
			}
			if (mapContainer.isAnyPlannedAP()) {
				addActionError(MgrUtil.getUserMessage(
						"error.hiveAp.real.locate.planned",
						new String[] { mapContainer.getMapName() }));
				return false;
			} else if (mapContainer.isAnyRealHiveAP() && (realAPCount == 0)) {
				addActionError(MgrUtil.getUserMessage(
						"error.hiveAp.real.locate.simulated",
						new String[] { mapContainer.getMapName() }));
				return false;
			} else if (mapContainer.isAnySimulatedHiveAP()
					&& (realAPCount == hiveAps.size())) {
				addActionError(MgrUtil.getUserMessage(
						"error.hiveAp.simulated.locate.real",
						new String[] { mapContainer.getMapName() }));
				return false;
			}
		}
		return true;
	}

	private boolean verifyRealSimulateHiveAp(MapContainerNode mapContainer,
			HiveAp hiveAp) {
		if (null != hiveAp && null != mapContainer) {
			if (mapContainer.isAnyPlannedAP()) {
				addActionError(MgrUtil.getUserMessage(
						"error.hiveAp.real.locate.planned",
						new String[] { mapContainer.getMapName() }));
				return false;
			} else if (mapContainer.isAnyRealHiveAP() && hiveAp.isSimulated()) {
				addActionError(MgrUtil.getUserMessage(
						"error.hiveAp.real.locate.simulated",
						new String[] { mapContainer.getMapName() }));
				return false;
			} else if (mapContainer.isAnySimulatedHiveAP()
					&& !hiveAp.isSimulated()) {
				addActionError(MgrUtil.getUserMessage(
						"error.hiveAp.simulated.locate.real",
						new String[] { mapContainer.getMapName() }));
				return false;
			}
		}
		return true;
	}

	private void updateKeyId(HiveAp hiveAp) {
		if (null == hiveAp) {
			return;
		}
		if (hiveAp.getPassPhrase() != null
				&& !(hiveAp.getPassPhrase().equals(hiveAp
						.getCurrentPassPhrase()))) {
			hiveAp.setKeyId(NmsUtil.getNewDtlsKeyId(hiveAp.getCurrentKeyId()));
		} else {
			hiveAp.setKeyId(hiveAp.getCurrentKeyId());
		}
	}

	private String macAddress;

	private String ssidName;

	private boolean configView;

	private String viewType;

	private int cacheId;

	private String reassignDomainName;

	private boolean hasNewHiveAP;
	
	private long newHiveApCount=0;

	private boolean collapsed;

	private void updateConfigViewParam() {
		if ("manageAPGuid".equals(listType)) {
			configView = false;
			return;
		}
		String viewType = (String) MgrUtil
				.getSessionAttribute(MANAGED_LIST_VIEW);
		configView = "config".equals(viewType);
	}

	public String getDeviceTypesJsonString() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		if (null != page) {
			for (Object obj : page) {
				HiveAp device = (HiveAp) obj;
				jsonObject.put("_" + device.getId(), device.getDeviceType());
				jsonObject.put("__" + device.getId(), device.getHiveApModel());
			}
			log.info("device models on page in json string: "
					+ jsonObject.toString());
		}
		return jsonObject.toString();
	}

	public String prepareHiveApList(boolean initCache) {
		setEnablePageAutoRefreshSetting();
		MgrUtil.removeSessionAttribute("lstTitle");
		MgrUtil.removeSessionAttribute("lstTabId");
		MgrUtil.removeSessionAttribute("lstForward");
		MgrUtil.removeSessionAttribute(HiveApUpdateAction.UPDATE_CONFIG_SELECTED_IDS_EX);
		if (getDataSource()!=null) {
			diMenuTypeKey=getDataSource().getDiMenuTypeKey();
			clearDataSource();
			if (diMenuTypeKey!=null) {
				return "di_list";
			}
		} else {
			clearDataSource();
		}
		String exGuide = (String)MgrUtil.getSessionAttribute("exConfigGuideFeature");
		if(exGuide != null && exGuide.equals("hiveapEx")){
			MgrUtil.setSessionAttribute("exConfigGuideFeature", "manageAPEx");
		}
		HiveApPagingCache hiveApPagingCache = getHiveApListCache();
		if (initCache) {
			enablePaging();
			cacheId = hiveApPagingCache.init();
			page = hiveApPagingCache.getBos(cacheId);
		} else {
			paging = (Paging<? extends HmBo>) MgrUtil.getSessionAttribute(boClass
					.getSimpleName()
					+ "Paging");
			page = hiveApPagingCache.getBos(cacheId);
		}
		// Need to fill in LAZY relationship info
		HiveApPagingCache.queryLazyInfo(page);

		hiveApList.clear();
		routerList.clear();
		switchList.clear();
		l2_vpnGatewayList.clear();
		l3_vpnGatewayList.clear();
		for(Object obj : page){
			HiveAp apPage = (HiveAp)obj;
			HiveApUtils.DeviceListType dTypeEnum = HiveApUtils.getDeviceTypeEnum(apPage.getHiveApModel(), apPage.getDeviceType());
			
			if(dTypeEnum == HiveApUtils.DeviceListType.AP){
				hiveApList.add(apPage);
			}else if(dTypeEnum == HiveApUtils.DeviceListType.BR){
				routerList.add(apPage);
			}else if(dTypeEnum == HiveApUtils.DeviceListType.L3_VPN){
				l3_vpnGatewayList.add(apPage);
			}else if(dTypeEnum == HiveApUtils.DeviceListType.L2_VPN){
				l2_vpnGatewayList.add(apPage);
			}else if(dTypeEnum == HiveApUtils.DeviceListType.Switch){
				switchList.add(apPage);
			}
		}
		
		if("manageAPGuid".equals(listType)){
			prepareSelectAllForGuidList();
		}

		setUserTimeZone();
		iteratePageValue();
		setTableColumns();
		return listType;
	}

	private void setUserTimeZone() {
		if (null == page) {
			return;
		}
		String userTimeZone = userContext.getTimeZone();
		for (Object obj : page) {
			HiveAp hiveAp = (HiveAp) obj;
			hiveAp.setUserTimeZone(userTimeZone);
		}
	}
	private List<HmTableColumn> getManagedDefaultSelectedColumns_config(
			short columnType) {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		if (SELECTED_COLUMNS == columnType) {
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			if (isFullMode()) {// show WLAN only in full mode
				columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			}
			columns.add(new HmTableColumn(COLUMN_HIVE));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_GATEWAY));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_LOCATION));
//			columns.add(new HmTableColumn(COLUMN_3GUSB_STATUS));
			// fix bug 14678 add 'Device category'
			columns.add(new HmTableColumn(COLUMN_DEVICE_CATEGORY));
		} else {
			if (isFullMode()) {// show WLAN only in full mode
				columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			}
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_SEVERITY));
			columns.add(new HmTableColumn(COLUMN_HIVE));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_GATEWAY));
			columns.add(new HmTableColumn(COLUMN_LOCATION));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_APTYPE));
			columns.add(new HmTableColumn(COLUMN_CLIENTS));
			columns.add(new HmTableColumn(COLUMN_CONNECTIONTIME));
			columns.add(new HmTableColumn(COLUMN_PRODUCTNAME));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
			columns.add(new HmTableColumn(COLUMN_SERIALNUMBER));
			columns.add(new HmTableColumn(COLUMN_DISCOVERYTIME));
			columns.add(new HmTableColumn(COLUMN_COUNTRY_CODE));
			columns.add(new HmTableColumn(COLUMN_ISOUTDOOR));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_MGT0_VLAN));
			columns.add(new HmTableColumn(COLUMN_NATIVE_VLAN));
			columns.add(new HmTableColumn(COLUMN_L7_SIGNATURE_VERSION));
			// fix bug 14678 add 'Device category'
			columns.add(new HmTableColumn(COLUMN_DEVICE_CATEGORY));
			columns.add(new HmTableColumn(COLUMN_WIFI0_RADIOPROFILE));
			columns.add(new HmTableColumn(COLUMN_WIFI1_RADIOPROFILE));
			
			columns.add(new HmTableColumn(COLUMN_WIFI0_CHANNEL));
			columns.add(new HmTableColumn(COLUMN_WIFI0_POWER));
			columns.add(new HmTableColumn(COLUMN_WIFI1_CHANNEL));
			columns.add(new HmTableColumn(COLUMN_WIFI1_POWER));
			columns.add(new HmTableColumn(COLUMN_ETH0_DEVICE_ID));
			columns.add(new HmTableColumn(COLUMN_ETH0_PORT_ID));
			columns.add(new HmTableColumn(COLUMN_ETH0_SYSTEM_ID));
			columns.add(new HmTableColumn(COLUMN_ETH1_DEVICE_ID));
			columns.add(new HmTableColumn(COLUMN_ETH1_PORT_ID));
			columns.add(new HmTableColumn(COLUMN_ETH1_SYSTEM_ID));
			
			if (isFullMode() && getEnableSupplementalCLI()){
				columns.add(new HmTableColumn(COLUMN_SUPPLEMENTAL_CLI));
			}
			
		}
		return columns;
	}

	private List<HmTableColumn> getManagedDefaultSelectedColums_monitor(
			short columnType) {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		if (SELECTED_COLUMNS == columnType) {
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_SEVERITY));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_APTYPE));
			columns.add(new HmTableColumn(COLUMN_CLIENTS));
			columns.add(new HmTableColumn(COLUMN_CONNECTIONTIME));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
//			columns.add(new HmTableColumn(COLUMN_3GUSB_STATUS));
			// fix bug 14678 add 'Device category'
			columns.add(new HmTableColumn(COLUMN_DEVICE_CATEGORY));
			columns.add(new HmTableColumn(COLUMN_L7_SIGNATURE_VERSION));
		} else {
			if (isFullMode()) {// show WLAN only in full mode
				columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			}
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_SEVERITY));
			columns.add(new HmTableColumn(COLUMN_HIVE));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_GATEWAY));
			columns.add(new HmTableColumn(COLUMN_LOCATION));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_APTYPE));
			columns.add(new HmTableColumn(COLUMN_CLIENTS));
			columns.add(new HmTableColumn(COLUMN_CONNECTIONTIME));
			columns.add(new HmTableColumn(COLUMN_PRODUCTNAME));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
			columns.add(new HmTableColumn(COLUMN_SERIALNUMBER));
			columns.add(new HmTableColumn(COLUMN_DISCOVERYTIME));
			columns.add(new HmTableColumn(COLUMN_COUNTRY_CODE));
			columns.add(new HmTableColumn(COLUMN_ISOUTDOOR));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_MGT0_VLAN));
			columns.add(new HmTableColumn(COLUMN_NATIVE_VLAN));
			columns.add(new HmTableColumn(COLUMN_L7_SIGNATURE_VERSION));
			// fix bug 14678 add 'Device category'
			columns.add(new HmTableColumn(COLUMN_DEVICE_CATEGORY));
			columns.add(new HmTableColumn(COLUMN_WIFI0_RADIOPROFILE));
			columns.add(new HmTableColumn(COLUMN_WIFI1_RADIOPROFILE));
			columns.add(new HmTableColumn(COLUMN_WIFI0_CHANNEL));
			columns.add(new HmTableColumn(COLUMN_WIFI0_POWER));
			columns.add(new HmTableColumn(COLUMN_WIFI1_CHANNEL));
			columns.add(new HmTableColumn(COLUMN_WIFI1_POWER));
			columns.add(new HmTableColumn(COLUMN_ETH0_DEVICE_ID));
			columns.add(new HmTableColumn(COLUMN_ETH0_PORT_ID));
			columns.add(new HmTableColumn(COLUMN_ETH0_SYSTEM_ID));
			columns.add(new HmTableColumn(COLUMN_ETH1_DEVICE_ID));
			columns.add(new HmTableColumn(COLUMN_ETH1_PORT_ID));
			columns.add(new HmTableColumn(COLUMN_ETH1_SYSTEM_ID));
			
			if (isFullMode() && getEnableSupplementalCLI()){
				columns.add(new HmTableColumn(COLUMN_SUPPLEMENTAL_CLI));
			}
		}
		return columns;
	}

	private List<HmTableColumn> getVPNGatewaysDefaultSelectedColumns_config(
			short columnType) {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		if (SELECTED_COLUMNS == columnType) {
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_GATEWAY));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_DEVICE_CATEGORY));
			columns.add(new HmTableColumn(COLUMN_LOCATION));
		} else {
			if (isFullMode()) {// show WLAN only in full mode
				columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			}
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_SEVERITY));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_GATEWAY));
			columns.add(new HmTableColumn(COLUMN_LOCATION));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_DEVICE_CATEGORY));
			columns.add(new HmTableColumn(COLUMN_CONNECTIONTIME));
			columns.add(new HmTableColumn(COLUMN_PRODUCTNAME));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
			columns.add(new HmTableColumn(COLUMN_SERIALNUMBER));
			columns.add(new HmTableColumn(COLUMN_DISCOVERYTIME));
			columns.add(new HmTableColumn(COLUMN_COUNTRY_CODE));
			columns.add(new HmTableColumn(COLUMN_MGT0_VLAN));
			columns.add(new HmTableColumn(COLUMN_NATIVE_VLAN));
			if (isFullMode() && getEnableSupplementalCLI()){
				columns.add(new HmTableColumn(COLUMN_SUPPLEMENTAL_CLI));
			}

		}
		return columns;
	}

	private List<HmTableColumn> getVPNGatewaysDefaultSelectedColums_monitor(
			short columnType) {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		if (SELECTED_COLUMNS == columnType) {
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_SEVERITY));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_DEVICE_CATEGORY));
			columns.add(new HmTableColumn(COLUMN_CONNECTIONTIME));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
		} else {
			if (isFullMode()) {// show WLAN only in full mode
				columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			}
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_SEVERITY));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_GATEWAY));
			columns.add(new HmTableColumn(COLUMN_LOCATION));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_DEVICE_CATEGORY));
			columns.add(new HmTableColumn(COLUMN_CONNECTIONTIME));
			columns.add(new HmTableColumn(COLUMN_PRODUCTNAME));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
			columns.add(new HmTableColumn(COLUMN_SERIALNUMBER));
			columns.add(new HmTableColumn(COLUMN_DISCOVERYTIME));
			columns.add(new HmTableColumn(COLUMN_COUNTRY_CODE));
			columns.add(new HmTableColumn(COLUMN_MGT0_VLAN));
			columns.add(new HmTableColumn(COLUMN_NATIVE_VLAN));
			if (isFullMode() && getEnableSupplementalCLI()){
				columns.add(new HmTableColumn(COLUMN_SUPPLEMENTAL_CLI));
			}
		}
		return columns;
	}

	private List<HmTableColumn> getRoutersDefaultSelectedColumns_config(
			short columnType) {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		if (SELECTED_COLUMNS == columnType) {
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			if (isFullMode()) {// show WLAN only in full mode
				columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			}
			columns.add(new HmTableColumn(COLUMN_HIVE));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_GATEWAY));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_LOCATION));
//			columns.add(new HmTableColumn(COLUMN_3GUSB_STATUS));
		} else {
			if (isFullMode()) {// show WLAN only in full mode
				columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			}
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_SEVERITY));
			columns.add(new HmTableColumn(COLUMN_HIVE));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_GATEWAY));
			columns.add(new HmTableColumn(COLUMN_LOCATION));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_APTYPE));
			columns.add(new HmTableColumn(COLUMN_CLIENTS));
			columns.add(new HmTableColumn(COLUMN_CONNECTIONTIME));
			columns.add(new HmTableColumn(COLUMN_PRODUCTNAME));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
			columns.add(new HmTableColumn(COLUMN_SERIALNUMBER));
			columns.add(new HmTableColumn(COLUMN_DISCOVERYTIME));
			columns.add(new HmTableColumn(COLUMN_COUNTRY_CODE));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_MGT0_VLAN));
			columns.add(new HmTableColumn(COLUMN_NATIVE_VLAN));
			columns.add(new HmTableColumn(COLUMN_L7_SIGNATURE_VERSION));
			columns.add(new HmTableColumn(COLUMN_WIFI0_RADIOPROFILE));
			columns.add(new HmTableColumn(COLUMN_WIFI1_RADIOPROFILE));
			columns.add(new HmTableColumn(COLUMN_WIFI0_CHANNEL));
			columns.add(new HmTableColumn(COLUMN_WIFI0_POWER));
			columns.add(new HmTableColumn(COLUMN_WIFI1_CHANNEL));
			columns.add(new HmTableColumn(COLUMN_WIFI1_POWER));
			if (isFullMode() && getEnableSupplementalCLI()){
				columns.add(new HmTableColumn(COLUMN_SUPPLEMENTAL_CLI));
			}
		
		}
		return columns;
	}

	private List<HmTableColumn> getRoutersDefaultSelectedColums_monitor(
			short columnType) {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		if (SELECTED_COLUMNS == columnType) {
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_SEVERITY));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_APTYPE));
			columns.add(new HmTableColumn(COLUMN_CLIENTS));
			columns.add(new HmTableColumn(COLUMN_CONNECTIONTIME));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
//			columns.add(new HmTableColumn(COLUMN_3GUSB_STATUS));
			columns.add(new HmTableColumn(COLUMN_L7_SIGNATURE_VERSION));
		} else {
			if (isFullMode()) {// show WLAN only in full mode
				columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			}
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_SEVERITY));
			columns.add(new HmTableColumn(COLUMN_HIVE));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_GATEWAY));
			columns.add(new HmTableColumn(COLUMN_LOCATION));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_APTYPE));
			columns.add(new HmTableColumn(COLUMN_CLIENTS));
			columns.add(new HmTableColumn(COLUMN_CONNECTIONTIME));
			columns.add(new HmTableColumn(COLUMN_PRODUCTNAME));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
			columns.add(new HmTableColumn(COLUMN_SERIALNUMBER));
			columns.add(new HmTableColumn(COLUMN_DISCOVERYTIME));
			columns.add(new HmTableColumn(COLUMN_COUNTRY_CODE));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_MGT0_VLAN));
			columns.add(new HmTableColumn(COLUMN_NATIVE_VLAN));
			columns.add(new HmTableColumn(COLUMN_L7_SIGNATURE_VERSION));
			columns.add(new HmTableColumn(COLUMN_WIFI0_RADIOPROFILE));
			columns.add(new HmTableColumn(COLUMN_WIFI1_RADIOPROFILE));
			columns.add(new HmTableColumn(COLUMN_WIFI0_CHANNEL));
			columns.add(new HmTableColumn(COLUMN_WIFI0_POWER));
			columns.add(new HmTableColumn(COLUMN_WIFI1_CHANNEL));
			columns.add(new HmTableColumn(COLUMN_WIFI1_POWER));
			if (isFullMode() && getEnableSupplementalCLI()){
				columns.add(new HmTableColumn(COLUMN_SUPPLEMENTAL_CLI));
			}
		}
		return columns;
	}

	private List<HmTableColumn> getSwitchesDefaultSelectedColumns_config(
			short columnType) {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		if (SELECTED_COLUMNS == columnType) {
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			if (isFullMode()) {// show WLAN only in full mode
				columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			}
			columns.add(new HmTableColumn(COLUMN_HIVE));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_GATEWAY));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_LOCATION));
//			columns.add(new HmTableColumn(COLUMN_3GUSB_STATUS));
		} else {
			if (isFullMode()) {// show WLAN only in full mode
				columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			}
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_SEVERITY));
			columns.add(new HmTableColumn(COLUMN_HIVE));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_GATEWAY));
			columns.add(new HmTableColumn(COLUMN_LOCATION));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_APTYPE));
			columns.add(new HmTableColumn(COLUMN_CLIENTS));
			columns.add(new HmTableColumn(COLUMN_CONNECTIONTIME));
			columns.add(new HmTableColumn(COLUMN_PRODUCTNAME));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
			columns.add(new HmTableColumn(COLUMN_SERIALNUMBER));
			columns.add(new HmTableColumn(COLUMN_DISCOVERYTIME));
			columns.add(new HmTableColumn(COLUMN_COUNTRY_CODE));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_MGT0_VLAN));
			columns.add(new HmTableColumn(COLUMN_NATIVE_VLAN));
			if (isFullMode() && getEnableSupplementalCLI()){
				columns.add(new HmTableColumn(COLUMN_SUPPLEMENTAL_CLI));
			}
		}
		return columns;
	}

	private List<HmTableColumn> getSwitchesDefaultSelectedColums_monitor(
			short columnType) {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		if (SELECTED_COLUMNS == columnType) {
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_SEVERITY));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_APTYPE));
			columns.add(new HmTableColumn(COLUMN_CLIENTS));
			columns.add(new HmTableColumn(COLUMN_CONNECTIONTIME));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
//			columns.add(new HmTableColumn(COLUMN_3GUSB_STATUS));
		} else {
			if (isFullMode()) {// show WLAN only in full mode
				columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			}
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_SEVERITY));
			columns.add(new HmTableColumn(COLUMN_HIVE));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_GATEWAY));
			columns.add(new HmTableColumn(COLUMN_LOCATION));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_APTYPE));
			columns.add(new HmTableColumn(COLUMN_CLIENTS));
			columns.add(new HmTableColumn(COLUMN_CONNECTIONTIME));
			columns.add(new HmTableColumn(COLUMN_PRODUCTNAME));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
			columns.add(new HmTableColumn(COLUMN_SERIALNUMBER));
			columns.add(new HmTableColumn(COLUMN_DISCOVERYTIME));
			columns.add(new HmTableColumn(COLUMN_COUNTRY_CODE));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_MGT0_VLAN));
			columns.add(new HmTableColumn(COLUMN_NATIVE_VLAN));
			
			if (isFullMode() && getEnableSupplementalCLI()){
				columns.add(new HmTableColumn(COLUMN_SUPPLEMENTAL_CLI));
			}
		}
		return columns;
	}

	private List<HmTableColumn> getDeviceAPsDefaultSelectedColumns_config(
			short columnType) {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		if (SELECTED_COLUMNS == columnType) {
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			if (isFullMode()) {// show WLAN only in full mode
				columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			}
			columns.add(new HmTableColumn(COLUMN_HIVE));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_GATEWAY));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_LOCATION));
		} else {
			if (isFullMode()) {// show WLAN only in full mode
				columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			}
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_SEVERITY));
			columns.add(new HmTableColumn(COLUMN_HIVE));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_GATEWAY));
			columns.add(new HmTableColumn(COLUMN_LOCATION));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_APTYPE));
			columns.add(new HmTableColumn(COLUMN_CLIENTS));
			columns.add(new HmTableColumn(COLUMN_CONNECTIONTIME));
			columns.add(new HmTableColumn(COLUMN_PRODUCTNAME));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
			columns.add(new HmTableColumn(COLUMN_SERIALNUMBER));
			columns.add(new HmTableColumn(COLUMN_DISCOVERYTIME));
			columns.add(new HmTableColumn(COLUMN_COUNTRY_CODE));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_MGT0_VLAN));
			columns.add(new HmTableColumn(COLUMN_NATIVE_VLAN));
			columns.add(new HmTableColumn(COLUMN_L7_SIGNATURE_VERSION));
			columns.add(new HmTableColumn(COLUMN_WIFI0_RADIOPROFILE));
			columns.add(new HmTableColumn(COLUMN_WIFI1_RADIOPROFILE));
			columns.add(new HmTableColumn(COLUMN_WIFI0_CHANNEL));
			columns.add(new HmTableColumn(COLUMN_WIFI0_POWER));
			columns.add(new HmTableColumn(COLUMN_WIFI1_CHANNEL));
			columns.add(new HmTableColumn(COLUMN_WIFI1_POWER));
			columns.add(new HmTableColumn(COLUMN_ETH0_DEVICE_ID));
			columns.add(new HmTableColumn(COLUMN_ETH0_PORT_ID));
			columns.add(new HmTableColumn(COLUMN_ETH0_SYSTEM_ID));
			columns.add(new HmTableColumn(COLUMN_ETH1_DEVICE_ID));
			columns.add(new HmTableColumn(COLUMN_ETH1_PORT_ID));
			columns.add(new HmTableColumn(COLUMN_ETH1_SYSTEM_ID));
			
			if (isFullMode() && getEnableSupplementalCLI()){
				columns.add(new HmTableColumn(COLUMN_SUPPLEMENTAL_CLI));
			}

		}
		return columns;
	}

	private List<HmTableColumn> getDeviceAPsDefaultSelectedColums_monitor(
			short columnType) {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		if (SELECTED_COLUMNS == columnType) {
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_SEVERITY));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_APTYPE));
			columns.add(new HmTableColumn(COLUMN_CLIENTS));
			columns.add(new HmTableColumn(COLUMN_CONNECTIONTIME));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
			columns.add(new HmTableColumn(COLUMN_L7_SIGNATURE_VERSION));
		} else {
			if (isFullMode()) {// show WLAN only in full mode
				columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			}
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_SEVERITY));
			columns.add(new HmTableColumn(COLUMN_HIVE));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_GATEWAY));
			columns.add(new HmTableColumn(COLUMN_LOCATION));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_CAPWAP_CLIENT_IP));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_APTYPE));
			columns.add(new HmTableColumn(COLUMN_CLIENTS));
			columns.add(new HmTableColumn(COLUMN_CONNECTIONTIME));
			columns.add(new HmTableColumn(COLUMN_PRODUCTNAME));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
			columns.add(new HmTableColumn(COLUMN_SERIALNUMBER));
			columns.add(new HmTableColumn(COLUMN_DISCOVERYTIME));
			columns.add(new HmTableColumn(COLUMN_COUNTRY_CODE));
			columns.add(new HmTableColumn(COLUMN_TOPOMAP));
			columns.add(new HmTableColumn(COLUMN_MGT0_VLAN));
			columns.add(new HmTableColumn(COLUMN_NATIVE_VLAN));
			columns.add(new HmTableColumn(COLUMN_L7_SIGNATURE_VERSION));
			columns.add(new HmTableColumn(COLUMN_WIFI0_RADIOPROFILE));
			columns.add(new HmTableColumn(COLUMN_WIFI1_RADIOPROFILE));
			columns.add(new HmTableColumn(COLUMN_WIFI0_CHANNEL));
			columns.add(new HmTableColumn(COLUMN_WIFI0_POWER));
			columns.add(new HmTableColumn(COLUMN_WIFI1_CHANNEL));
			columns.add(new HmTableColumn(COLUMN_WIFI1_POWER));
			columns.add(new HmTableColumn(COLUMN_ETH0_DEVICE_ID));
			columns.add(new HmTableColumn(COLUMN_ETH0_PORT_ID));
			columns.add(new HmTableColumn(COLUMN_ETH0_SYSTEM_ID));
			columns.add(new HmTableColumn(COLUMN_ETH1_DEVICE_ID));
			columns.add(new HmTableColumn(COLUMN_ETH1_PORT_ID));
			columns.add(new HmTableColumn(COLUMN_ETH1_SYSTEM_ID));
			
			if (isFullMode() && getEnableSupplementalCLI()){
				columns.add(new HmTableColumn(COLUMN_SUPPLEMENTAL_CLI));
			}
		}
		return columns;
	}

	private List<HmTableColumn> getManageAPExSelectedColumns(short columnType) {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		if (SELECTED_COLUMNS == columnType) {
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
		} else {
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_NETMASK));
			columns.add(new HmTableColumn(COLUMN_DHCP));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
			
			if (isFullMode() && getEnableSupplementalCLI()){
				columns.add(new HmTableColumn(COLUMN_SUPPLEMENTAL_CLI));
			}
		}
		return columns;
	}

	private List<HmTableColumn> getManageAPGuidSelectedColumns(short columnType){
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		if (SELECTED_COLUMNS == columnType) {
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_UPDATE_STATUS));
		} else {
			columns.add(new HmTableColumn(COLUMN_CONNECTED));
			columns.add(new HmTableColumn(COLUMN_HOSTNAME));
			columns.add(new HmTableColumn(COLUMN_MACADDRESS));
			columns.add(new HmTableColumn(COLUMN_IPADDRESS));
			columns.add(new HmTableColumn(COLUMN_WLANPOLICY));
			columns.add(new HmTableColumn(COLUMN_SOFTWAREVERSION));
			columns.add(new HmTableColumn(COLUMN_AUDIT));
			columns.add(new HmTableColumn(COLUMN_UPDATE_STATUS));
			
			if (isFullMode() && getEnableSupplementalCLI()){
				columns.add(new HmTableColumn(COLUMN_SUPPLEMENTAL_CLI));
			}
		}
		return columns;
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		return getSelectedColumns(AVAILABLE_COLUMNS);
	}

	protected List<HmTableColumn> getInitSelectedColumns() {
		return getSelectedColumns(SELECTED_COLUMNS);
	}

	private List<HmTableColumn> getSelectedColumns(short columnType) {
		if ("managedHiveAps".equals(listType)) {
			if (configView) {
				return getManagedDefaultSelectedColumns_config(columnType);
			} else {
				return getManagedDefaultSelectedColums_monitor(columnType);
			}
		} else if("managedVPNGateways".equals(listType)) {
			if (configView) {
				return getVPNGatewaysDefaultSelectedColumns_config(columnType);
			} else {
				return getVPNGatewaysDefaultSelectedColums_monitor(columnType);
			}
		} else if("managedRouters".equals(listType)) {
			if (configView) {
				return getRoutersDefaultSelectedColumns_config(columnType);
			} else {
				return getRoutersDefaultSelectedColums_monitor(columnType);
			}
		} else if("managedSwitches".equals(listType)) {
			if (configView) {
				return getSwitchesDefaultSelectedColumns_config(columnType);
			} else {
				return getSwitchesDefaultSelectedColums_monitor(columnType);
			}
		} else if ("managedDeviceAPs".equals(listType)) {
			if (configView) {
				return getDeviceAPsDefaultSelectedColumns_config(columnType);
			} else {
				return getDeviceAPsDefaultSelectedColums_monitor(columnType);
			}
		}else if ("manageAPEx".equals(listType)) {
			return getManageAPExSelectedColumns(columnType);
		} else if ("manageAPGuid".equals(listType)) {
			return getManageAPGuidSelectedColumns(columnType);
		}
		return new ArrayList<HmTableColumn>();
	}

	private void setCurrentTableId() {
		String type = viewType;
		if (null == type || "".equals(type.trim())) {
			type = (String) MgrUtil.getSessionAttribute(MANAGED_LIST_VIEW);
		}
		if ("managedHiveAps".equals(listType)) {
			if ("config".equals(type)) {
				tableId = HmTableColumn.TABLE_MANAGED_APS_CONFIG;
			} else {
				tableId = HmTableColumn.TABLE_MANAGED_APS_MONITOR;
			}
		} else if ("managedVPNGateways".equals(listType)) {
			if ("config".equals(type)) {
				tableId = HmTableColumn.TABLE_MANAGED_VPN_GATEWAYS_CONFIG;
			} else {
				tableId = HmTableColumn.TABLE_MANAGED_VPN_GATEWAYS_MONITOR;
			}
		} else if ("managedRouters".equals(listType)) {
			if ("config".equals(type)) {
				tableId = HmTableColumn.TABLE_MANAGED_ROUTERS_CONFIG;
			} else {
				tableId = HmTableColumn.TABLE_MANAGED_ROUTERS_MONITOR;
			}
		} else if ("managedSwitches".equals(listType)) {
			if ("config".equals(type)) {
				tableId = HmTableColumn.TABLE_MANAGED_SWITCHES_CONFIG;
			} else {
				tableId = HmTableColumn.TABLE_MANAGED_SWITCHES_MONITOR;
			}
		} else if ("managedDeviceAPs".equals(listType)) {
			if ("config".equals(type)) {
				tableId = HmTableColumn.TABLE_MANAGED_DEVICEAPS_CONFIG;
			} else {
				tableId = HmTableColumn.TABLE_MANAGED_DEVICEAPS_MONITOR;
			}
		} else if ("newHiveAps".equals(listType)) {
			tableId = HmTableColumn.TABLE_NEW_APS;
		} else if ("autoDiscovered".equals(listType)) {
			tableId = HmTableColumn.TABLE_AUTO_DISCOVER_APS;
		} else if ("manuallyProvisioned".equals(listType)) {
			tableId = HmTableColumn.TABLE_MANU_PROVISION_APS;
		} else if ("manageAPEx".equals(listType)) {
			tableId = HmTableColumn.TABLE_MANAGED_APS_CONFIG;
		} else if ("manageAPGuid".equals(listType)) {
			tableId = HmTableColumn.TABLE_MANAGED_APS_CONFIG_GUID;
		} else {
			tableId = 0;
		}
	}

	private void iteratePageValue() {
		if ("managedHiveAps".equals(listType)
				|| "manageAPEx".equals(listType)
				|| "manageAPGuid".equals(listType)
				|| "managedVPNGateways".equals(listType)
				|| "managedRouters".equals(listType)
				|| "managedSwitches".equals(listType)
				|| "managedDeviceAPs".equals(listType)) {
			updateConfigViewParam();
			customizedManagedListCols();
			getNewHiveAPTag();
		}
	}

	private void getNewHiveAPTag() {
		if (null == page) {
			return;
		}
		for (Object obj : page) {
			HiveAp hiveAp = (HiveAp) obj;
			if (hiveAp.isNewHiveAP()) {
				hasNewHiveAP = true;
				break;
			}
		}
		if (hasNewHiveAP){
			String sessionKey = boClass.getSimpleName() + "Filtering";
			FilterParams oldfilterParams = (FilterParams) MgrUtil.getSessionAttribute(sessionKey);
			FilterParams fp = fetchNewfilterParamsForNewApCount(oldfilterParams);
			List<?> searchObject=null;
			if (getShowDomain()){
				searchObject = QueryUtil.executeQuery("select count(*) from " + HiveAp.class.getSimpleName(),
					null, fp);
			} else {
				searchObject = QueryUtil.executeQuery("select count(*) from " + HiveAp.class.getSimpleName(),
						null, fp, getDomain().getId());
			}
			if (searchObject!=null && !searchObject.isEmpty()) {
				newHiveApCount = Long.parseLong(searchObject.get(0).toString());
			} else {
				newHiveApCount=0;
			}
		}
	}
	
	public long getManagementHiveApCount(){
		if (getRowCount()==0) {
			return 0;
		}
		return getRowCount()-newHiveApCount;
	}
	
	private FilterParams fetchNewfilterParamsForNewApCount(FilterParams filterParams) {
		FilterParams fp = null;
		if (filterParams==null) {
			fp = new FilterParams("manageStatus", HiveAp.STATUS_NEW);
			return fp;
		} else {
			StringBuilder sqlF = new StringBuilder();
			if (filterParams.getName() != null
					&& null == filterParams.getValue()
					&& null == filterParams.getValues()) {
				sqlF.append(filterParams.getName()).append(" is null and manageStatus = :s1");
				fp = new FilterParams(sqlF.toString(),  new Object[]{HiveAp.STATUS_NEW});
			} else if (filterParams.getValue() != null) {
				sqlF.append(filterParams.getName()).append(" =:s1 and manageStatus = :s2");
				fp = new FilterParams(sqlF.toString(),  new Object[]{filterParams.getValue(),HiveAp.STATUS_NEW});
			} else if (filterParams.getValues() != null) {
				sqlF.append(filterParams.getName()).append(" in (:s1) and manageStatus = :s2");
				fp = new FilterParams(sqlF.toString(),  new Object[]{filterParams.getValues(),HiveAp.STATUS_NEW});
			} else if (filterParams.getWhere() != null) {
				int ff = filterParams.getBindings().length + 1;
				sqlF.append(filterParams.getWhere() + " and manageStatus =:s" + ff);
				Object[] obF = new Object[filterParams.getBindings().length + 1];
				for(int i=0; i< filterParams.getBindings().length; i++){
					obF[i] = filterParams.getBindings()[i];
				}
				obF[ff-1] = HiveAp.STATUS_NEW;
				fp = new FilterParams(sqlF.toString(),  obF);
			} else {
				fp = new FilterParams("manageStatus = :s1",  new Object[]{HiveAp.STATUS_NEW});
			}
			
			return fp;
		}
	}

	private void customizedManagedListCols() {
		if (null == page) {
			return;
		}
		String contextPath = request.getContextPath();
		// set common columns
		allDisconnected = true;
		for (Object object : page) {
			HiveAp hiveAp = (HiveAp) object;
			// set connection icon;
			if (hiveAp.isConnected()) {
				allDisconnected = false;
			}
			hiveAp.setConnectionIcon(HiveApMonitor.getConnectionIcon(
					hiveAp.getConnectStatus(), contextPath,
					hiveAp.getDelayTime(), hiveAp.getDisconnChangedTimeStr()));
			hiveAp.setConnectionIconEx(HiveApMonitor.getConnectionIconEx(
					hiveAp.isConnected(), contextPath));
			// set dtls icon;
			hiveAp.setDtlsIcon(hiveAp.isConnected() ? HiveApMonitor
					.getDtlsIcon(hiveAp.isCurrentDtlsEnable(), contextPath,
							isJsonMode())
					: HiveApMonitor.getDtlsBlankIcon(contextPath));
			// set audit icon;
			hiveAp.setConfigIndicationIcon(HiveApMonitor
					.getConfigIndicationIcon(hiveAp.isPending(),
							hiveAp.getPendingIndex(), hiveAp.getPendingMsg(),
							hiveAp.isPending_user(),
							hiveAp.getPendingIndex_user(),
							hiveAp.getPendingMsg_user(), contextPath,
							hiveAp.getId()));
			// set status icon;
			// BR CVG not support in express mode.
			if (this.isEasyMode() && hiveAp.isDisableExpress()) {
				hiveAp.setSeverityIcon(HiveApMonitor.getStatusIcon(
						(short) -1, contextPath));
			} else {
				hiveAp.setSeverityIcon(HiveApMonitor.getStatusIcon(
						hiveAp.getSeverity(), contextPath));
			}
			// set active client count;
			SimpleHiveAp s_hiveAp = CacheMgmt.getInstance()
					.getSimpleHiveAp(hiveAp.getMacAddress());
			if (null != s_hiveAp) {
				hiveAp.setActiveClientCount(s_hiveAp.getActiveClientCount());
			}
		}
		// set columns NOT in config view
//		if (!configView) { //display all column both config and monitor
		if (true) {
			anyRadius = false;
			anyRadiusProxy = false;
			anyVpns = false;
			anyDhcps = false;
			anyPPSK = false;
			iconMaxCount = 0;
			Map<String, HiveAp> hiveAps = new HashMap<String, HiveAp>(
					page.size());
			Map<String, List<String>> apIcons = new HashMap<String, List<String>>(
					page.size());
			Map<Long, List<SsidProfile>> ssidProfiles4Page = new HashMap<Long, List<SsidProfile>>();
			Map<Long, ConfigTemplate> hiveApConfigTemplateMap = this
					.getConfigTemplateMapOfPage(ssidProfiles4Page);
			boolean vhmIDMEnable = NmsUtil.isVhmEnableIdm(super.domainId);
			for (Object object : page) {
				HiveAp hiveAp = (HiveAp) object;
				int roleCount = 0; // indicate how many role it acts
				List<String> icons = new ArrayList<String>();
				// set AAA radius server icon;
				boolean isRadiusServer = false;
				if (hiveAp.isBranchRouter()
						&& hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR100) {
					if (hiveAp.isEnabledBrAsRadiusServer()) {
						if (hiveAp.getRadiusServerProfile() != null
								|| (hiveAp.getConfigTemplate() != null && hiveAp
										.getConfigTemplate()
										.getRadiusServerProfile() != null)) {
							isRadiusServer = true;
						}
					}
				} else {
					isRadiusServer = null != hiveAp.getRadiusServerProfile();
				}

				if (isRadiusServer) {
					anyRadius = true;
					roleCount++;
					String icon = HiveApMonitor.getRADIUSStatusIcon(
							isRadiusServer, contextPath);
					icons.add(icon);
				}
				// set IDM proxy server icon;
				boolean isIdmProxy = hiveAp.isIDMProxy();
				if (isIdmProxy) {
					anyRadiusProxy = true;
					roleCount++;
					String icon = HiveApMonitor.getIdmProxyStatusIcon(isIdmProxy, contextPath);
					icons.add(icon);
				}
				
				// set IDM Auth proxy server icon;
				boolean isIdmAuthProxy = vhmIDMEnable && isRadiusServer && 
						hiveAp.isEnableIDMAuthProxy();
				if(isIdmAuthProxy){
					roleCount++;
					String icon = HiveApMonitor.getIdmAuthProxyStatusIcon(isIdmAuthProxy, contextPath);
					icons.add(icon);
				}
				
				// set RADIUS proxy server icon;
				boolean isProxyServer = false;
				if (hiveAp.isBranchRouter()
						&& hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR100) {
					if (hiveAp.isEnabledBrAsRadiusServer()) {
						if (hiveAp.getRadiusProxyProfile() != null
								|| (hiveAp.getConfigTemplate() != null && hiveAp
										.getConfigTemplate()
										.getRadiusProxyProfile() != null)) {
							isProxyServer = true;
						}
					}
				} else {
					isProxyServer = null != hiveAp.getRadiusProxyProfile();
				}
				if (isProxyServer) {
					anyRadiusProxy = true;
					roleCount++;
					String icon = HiveApMonitor.getProxyStatusIcon(isProxyServer, contextPath);
					icons.add(icon);
				}
				// set DHCP server icon;
				boolean isDhcpServer = hiveAp.getDhcpServerCount() > 0;
				if (isDhcpServer) {
					anyDhcps = true;
					roleCount++;
					String icon = HiveApMonitor.getDHCPStatusIcon(isDhcpServer,
							contextPath);
					icons.add(icon);
				}
				// set VPN icon;
				boolean isVpnServer = hiveAp.isVpnServer();
				boolean isVpnClient = hiveAp.isVpnClient();
				if (isVpnServer) {
					anyVpns = true;
					roleCount++;
					VpnStatus isUp = AhVPNStatus.isVpnServerUp(hiveAp
							.getMacAddress());
					String icon = HiveApMonitor.getVPNStatusIcon(
							HiveAp.VPN_MARK_SERVER, isUp, contextPath);
					icons.add(icon);
				} else if (isVpnClient) {
					anyVpns = true;
					roleCount++;
					Long vpnId = null;
					if (null != hiveAp.getConfigTemplate()
							&& null != hiveAp.getConfigTemplate()
									.getVpnService()) {
						vpnId = hiveAp.getConfigTemplate().getVpnService()
								.getId();
					}
					VpnStatus isUp = AhVPNStatus.isVpnClientUp(
							hiveAp.getMacAddress(), vpnId);
					String icon = HiveApMonitor.getVPNStatusIcon(
							HiveAp.VPN_MARK_CLIENT, isUp, contextPath);
					icons.add(icon);
				}
				// PPSK server
				ConfigTemplate configTemplateTmp = null;
				List<SsidProfile> ssidProfiles = null;
				if (hiveApConfigTemplateMap != null) {
					configTemplateTmp = hiveApConfigTemplateMap.get(hiveAp
							.getId());
					if (configTemplateTmp != null && ssidProfiles4Page != null) {
						ssidProfiles = ssidProfiles4Page.get(configTemplateTmp
								.getId());
					}
				}
				boolean isPpskServer = this.isPpskServer(hiveAp,
						configTemplateTmp, ssidProfiles);
				if (isPpskServer) {
					anyPPSK = true;
					roleCount++;
					String icon = HiveApMonitor.getPPSKStatusIcon(isPpskServer,
							contextPath);
					icons.add(icon);
				}
				if (iconMaxCount < roleCount) {
					iconMaxCount = roleCount;
				}
				apIcons.put(hiveAp.getMacAddress(), icons);
				hiveAps.put(hiveAp.getMacAddress(), hiveAp);
			}

			if (ssidProfiles4Page != null) {
				ssidProfiles4Page.clear();
			}
			if (hiveApConfigTemplateMap != null) {
				hiveApConfigTemplateMap.clear();
			}

			String blankIcon = HiveApMonitor.getBlankIcon(contextPath);
			for (String mac : hiveAps.keySet()) {
				HiveAp hiveAp = hiveAps.get(mac);
				List<String> icons = apIcons.get(mac);
				for (int i = 0; i < iconMaxCount; i++) {
					if (i == 0) {
						if (icons.size() > 0) {
							hiveAp.setIconItem1(icons.get(0));
						} else {
							hiveAp.setIconItem1(blankIcon);
						}
					} else if (i == 1) {
						if (icons.size() > 1) {
							hiveAp.setIconItem2(icons.get(1));
						} else {
							hiveAp.setIconItem2(blankIcon);
						}
					} else if (i == 2) {
						if (icons.size() > 2) {
							hiveAp.setIconItem3(icons.get(2));
						} else {
							hiveAp.setIconItem3(blankIcon);
						}
					} else if (i == 3) {
						if (icons.size() > 3) {
							hiveAp.setIconItem4(icons.get(3));
						} else {
							hiveAp.setIconItem4(blankIcon);
						}
					} else if (i == 4) {
						if (icons.size() > 4) {
							hiveAp.setIconItem5(icons.get(4));
						} else {
							hiveAp.setIconItem5(blankIcon);
						}
					} else if (i == 5) {
						if (icons.size() > 5) {
							hiveAp.setIconItem6(icons.get(5));
						} else {
							hiveAp.setIconItem6(blankIcon);
						}
					}
				}
				//set hiveap hostname
				hiveAp.setHostnameHtml(HiveApMonitor.getHostHtmlStr(hiveAp, iconMaxCount, this.getShowDomain()));
			}
			// set up channel&power values;
			filledChannelPowers(hiveAps);
		}
	}

	private void filledChannelPowers(Map<String, HiveAp> hiveAps) {
		// long start = System.currentTimeMillis();
		if (!hiveAps.isEmpty()) {
			List<AhLatestXif> radioList = QueryUtil.executeQuery(
					AhLatestXif.class, null, new FilterParams("apMac", hiveAps
							.keySet()));
			List<AhLatestRadioAttribute> attributeList = QueryUtil
					.executeQuery(AhLatestRadioAttribute.class, null,
							new FilterParams("apMac", hiveAps.keySet()));
			Map<String, String> indexNameMapping = new HashMap<String, String>(
					radioList.size());
			for (AhLatestXif xif : radioList) {
				String mac = xif.getApMac();
				indexNameMapping.put(mac + xif.getIfIndex(), xif.getIfName());
			}
			for (AhLatestRadioAttribute attributes : attributeList) {
				String mac = attributes.getApMac();
				String wifiName = indexNameMapping.get(mac
						+ attributes.getIfIndex());
				if ("wifi0".equalsIgnoreCase(wifiName)) {
					hiveAps.get(mac).getWifi0().setRunningChannel(
							String.valueOf(attributes.getRadioChannel()));
					hiveAps.get(mac).getWifi0().setRunningPower(
							String.valueOf(attributes.getRadioTxPower())
									+ " dBm");
				} else if ("wifi1".equalsIgnoreCase(wifiName)) {
					hiveAps.get(mac).getWifi1().setRunningChannel(
							String.valueOf(attributes.getRadioChannel()));
					hiveAps.get(mac).getWifi1().setRunningPower(
							String.valueOf(attributes.getRadioTxPower())
									+ " dBm");
				}
			}
			
			for (HiveAp ap : hiveAps.values()){
				if (ap.getWifi0()!=null) {
					if (!ap.getWifi0().getRunningChannel().equals("-")) {
						if (ap.getWifi0().getChannel()!=0) {
							ap.getWifi0().setRunningChannel(ap.getWifi0().getRunningChannel() + "*");
						}
					}
					if (!ap.getWifi0().getRunningPower().equals("-")) {
						if (ap.getWifi0().getRunningPower().equals("0 dBm")) {
							ap.getWifi0().setRunningPower("Down");
							ap.getWifi0().setRunningChannel("N/A");
						} else {
							if (ap.getWifi0().getPower()!=0) {
								ap.getWifi0().setRunningPower(ap.getWifi0().getRunningPower() + "*");
							}
						}
					}
				}
				if (ap.getWifi1()!=null) {
					if (!ap.getWifi1().getRunningChannel().equals("-")) {
						if (ap.getWifi1().getChannel()!=0) {
							ap.getWifi1().setRunningChannel(ap.getWifi1().getRunningChannel() + "*");
						}
					}
					if (!ap.getWifi1().getRunningPower().equals("-")) {
						if (ap.getWifi1().getRunningPower().equals("0 dBm")) {
							ap.getWifi1().setRunningPower("Down");
							ap.getWifi1().setRunningChannel("N/A");
						} else {
							if (ap.getWifi1().getPower()!=0) {
								ap.getWifi1().setRunningPower(ap.getWifi1().getRunningPower() + "*");
							}
						}
					}
				}
			}
		}
		// long end = System.currentTimeMillis();
		// log.info("filledChannelPowers", "cost time:"+(end-start)+"ms");
	}

	private boolean anyRadius;
	private boolean anyRadiusProxy;
	private boolean anyVpns;
	private boolean anyDhcps;
	private boolean anyPPSK;
	private int iconMaxCount;

	public int getHostnamIndent() {
		return 3 + iconMaxCount * 21;
	}

	public int getHostnamIndentInDraw() {
		return 28 + iconMaxCount * 21;
	}

	public int getIconMaxCount() {
		return iconMaxCount;
	}

	public boolean isAnyVpns() {
		return anyVpns;
	}

	public boolean isAnyDhcps() {
		return anyDhcps;
	}

	public boolean isAnyRadius() {
		return anyRadius;
	}

	public boolean isAnyPPSK(){
		return anyPPSK;
	}

	public boolean isAnyRadiusProxy() {
		return anyRadiusProxy;
	}

	private boolean allDisconnected;

	public boolean isAllDisconnected() {
		return allDisconnected;
	}

	private JSONObject showNeighborDetails() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		HiveAp hiveAp = null;
		if (null != macAddress) {
			List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class, null,
					new FilterParams("macAddress", macAddress), domainId, this);
			if (!list.isEmpty()) {
				hiveAp = list.get(0);
			}
		}
		if (null == hiveAp) {
			jsonObject.put("msg", MgrUtil
					.getUserMessage("error.apMonitor.entry.notExist"));
		} else if (hiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
			jsonObject.put("msg", MgrUtil
					.getUserMessage("error.monitor.notManaged"));
		} else {
			jsonObject.put("success", true);
			jsonObject.put("id", hiveAp.getId());
		}
		return jsonObject;
	}

	private JSONObject getSsidsBySsidName() throws Exception {
		List<SsidProfile> ssidList = QueryUtil.executeQuery(SsidProfile.class,
				null, new FilterParams("ssid", ssidName), domainId);
		JSONObject jsonObject = new JSONObject();
		if (ssidList.isEmpty()) {
			jsonObject.put("msg", MgrUtil
					.getUserMessage("error.apMonitor.ssid.notExist"));
		} else {
			JSONArray jsonArray = new JSONArray();
			for (SsidProfile ssid : ssidList) {
				ssid = QueryUtil.findBoById(SsidProfile.class, ssid.getId(),
						this);
				JSONObject obj = new JSONObject();
				obj.put("id", ssid.getId());
				obj.put("name", ssid.getSsidName());
				obj.put("domainId", ssid.getOwner().getId());
				jsonArray.put(obj);
			}
			jsonObject.put("ssids", jsonArray);
		}
		return jsonObject;
	}

	/*
	 * Only create 1 map events cache per session.
	 */
	protected HiveApPagingCache getHiveApListCache() {
		HiveApPagingCache hiveApPagingCache = (HiveApPagingCache) MgrUtil
				.getSessionAttribute(SessionKeys.HIVEAP_PAGING_CACHE);
		if (hiveApPagingCache == null) {
			hiveApPagingCache = new HiveApPagingCache(userContext);
			MgrUtil.setSessionAttribute(SessionKeys.HIVEAP_PAGING_CACHE,
					hiveApPagingCache);
		}
		return hiveApPagingCache;
	}

	private void saveFilter() throws Exception {
		FilterParams f_params = null;
		if (null != mapId && null != isConnected && null != isNew) {
			// for invoked from topology map network summary.
			Long mapContainerId = Long.parseLong(mapId);
			Boolean con = Boolean.parseBoolean(isConnected);
			Boolean isNewHiveAp = Boolean.parseBoolean(isNew);
			Set<Long> mapIds = BoMgmt.getMapMgmt().getContainerDownIds(
					mapContainerId);
			String where = "manageStatus = :s1 AND mapContainer.id in(:s2) AND connected = :s3";
			Object[] values = new Object[3];
			if (isNewHiveAp) {
				values[0] = HiveAp.STATUS_NEW;
			} else {
				values[0] = HiveAp.STATUS_MANAGED;
			}
			values[1] = mapIds;
			values[2] = con;
			f_params = new FilterParams(where, values);
		} else if (dashCondition != null) {
			List<Short> status = new ArrayList<Short>();
			status.add(HiveAp.STATUS_MANAGED);
			// for invoked from report Dashboard.
			String where = "manageStatus in (:s1)";
			Object[] values;
			if (dashCondition.equalsIgnoreCase("alarm")) {
				where = where + " and severity >:s2";
				values = new Object[2];
				values[0] = status;
				values[1] = AhAlarm.AH_SEVERITY_UNDETERMINED;
			} else if (dashCondition.equalsIgnoreCase("outofData")) {
				where = where + " and (pending =:s2 or pending_user=:s3)";
				values = new Object[3];
				values[0] = status;
				values[1] = true;
				values[2] = true;
			} else if (dashCondition.equalsIgnoreCase("autoDiscoverHiveAps")) {
				// where = where + " and origin = :s2";
				values = new Object[1];
				status.clear();
				status.add(HiveAp.STATUS_NEW);
				values[0] = status;
				// values[1] = HiveAp.ORIGIN_DISCOVERED;
			} else if (dashCondition.equalsIgnoreCase("manuallyHiveAps")) {
				where = where + " and origin = :s2";
				values = new Object[2];
				status.add(HiveAp.STATUS_NEW);
				values[0] = status;
				values[1] = HiveAp.ORIGIN_CREATE;

			} else if (dashCondition.equalsIgnoreCase("apHthUp")) {
				where = where + " and connected = :s2";
				values = new Object[2];
				values[0] = status;
				values[1] = true;
			} else if (dashCondition.equalsIgnoreCase("apHthDown")) {
				where = where + " and connected = :s2";
				values = new Object[2];
				values[0] = status;
				values[1] = false;
			} else if (dashCondition.equalsIgnoreCase("nonComplianceAp")) {
				where = where + " and hostName =:s2";
				values = new Object[2];
				status.add(HiveAp.STATUS_MANAGED);
				values[0] = status;
				values[1] = filterMac;
			} else {
				where = where + " and hostName =:s2";
				values = new Object[2];
				status.add(HiveAp.STATUS_NEW);
				values[0] = status;
				values[1] = filterMac;
			}
			f_params = new FilterParams(where, values);
		} else if (null != isNew) {
			// for invoked from status item.
			Boolean isNewHiveAp = Boolean.parseBoolean(isNew);
			f_params = new FilterParams("manageStatus=:s1",
					new Object[]{isNewHiveAp ? HiveAp.STATUS_NEW : HiveAp.STATUS_MANAGED});
		}
		if (null != f_params) {
			if(getUserContext().isSourceFromIdm()){
				int fCount = f_params.getBindings().length + 1;
				String sWhere = f_params.getWhere() + " and softVer>:s" + fCount;
				Object[] sBind = new Object[fCount-1];
				for(int i=0; i<fCount-1; i++){
					sBind[i]=f_params.getBindings()[i];
				}
				sBind[fCount-1] = "5.1.2.0";
				filterParams = new FilterParams(sWhere,sBind) ;
			} else {
				filterParams = f_params;
			}
		}
		setSessionFiltering();
		// clear current filter on the left page
		MgrUtil.removeSessionAttribute(MANAGED_HIVEAP_CURRENT_FILTER);
	}

	@Override
	protected int removeBos(Class<? extends HmBo> boClass, Collection<Long> ids)
			throws Exception {
		log.info("hiveApAction", "Customized remove.");
		AccessControl.checkUserAccess(getUserContext(),
				getSelectedL2FeatureKey(), CrudOperation.DELETE);
		boolean urlErrorMsg=false;
		int  successRemovedCount=0;
		
		List<HiveAp> removedAps = QueryUtil.executeQuery(HiveAp.class, null,
				new FilterParams("id", ids),getDomain().getId());
		
		try {
			Collection<Long> successLst = BoMgmt.getMapMgmt().removeHiveApsReturnRemovedIds(ids, true, true, null, resetDeviceFlag);
			successRemovedCount = successLst.size();
			// propagate to other section;
			removeApPropagated(removedAps, successLst);
			DeviceUtils diu = DeviceImpUtils.getInstance();
			diu.removeSerialNumberFromHm(removedAps, successLst, false, getDomain());
		} catch (ClientHandlerException ex) {
			log.error(ex);
			urlErrorMsg=true;
		} catch (Exception e) {
			log.error(e);
		}
		int excepIds = ids.size()-successRemovedCount;
		if (excepIds>0) {
			String msg = "";
			if (excepIds==1) {
				msg="1 item";
			} else {  
				msg=excepIds + " items";
			}
			if (urlErrorMsg) {
				addActionError(MgrUtil.getUserMessage("error.remove.exception.urlerror",msg));
			} else {
				addActionError(MgrUtil.getUserMessage("error.remove.exception",msg));
			}
		}
		
		return successRemovedCount;
	}

	public static FilterParams fetchNewfilterParams(FilterParams filterParams, Set<Long> exIds) {
		FilterParams fp = null;
		if (exIds==null || exIds.isEmpty()) {
			return filterParams;
		}
		
		if (filterParams==null) {
			fp = new FilterParams("id not in (:s1)", exIds);
			return fp;
		} else {
			StringBuilder sqlF = new StringBuilder();
			if (filterParams.getName() != null
					&& null == filterParams.getValue()
					&& null == filterParams.getValues()) {
				sqlF.append(filterParams.getName()).append(" is null and id not in (:s1)");
				fp = new FilterParams(sqlF.toString(),  new Object[]{exIds});
			} else if (filterParams.getValue() != null) {
				sqlF.append(filterParams.getName()).append(" =:s1 and id not in (:s2)");
				fp = new FilterParams(sqlF.toString(),  new Object[]{filterParams.getValue(),exIds});
			} else if (filterParams.getValues() != null) {
				sqlF.append(filterParams.getName()).append(" in (:s1) and id not in (:s2)");
				fp = new FilterParams(sqlF.toString(),  new Object[]{filterParams.getValues(),exIds});
			} else if (filterParams.getWhere() != null) {
				int ff = filterParams.getBindings().length + 1;
				sqlF.append(filterParams.getWhere() + " and id not in (:s" + ff + ")");
				Object[] obF = new Object[filterParams.getBindings().length + 1];
				for(int i=0; i< filterParams.getBindings().length; i++){
					obF[i] = filterParams.getBindings()[i];
				}
				obF[ff-1] = exIds;
				fp = new FilterParams(sqlF.toString(),  obF);
			} else {
				fp = new FilterParams("id not in (:s1)",  new Object[]{exIds});
			}
			
			return fp;
		}
	}
	
	private int removeAllBos(Class<? extends HmBo> boClass,
			Collection<Long> defaultIds) throws Exception {
		log.info("hiveApAction", "Customized remove.");
		AccessControl.checkUserAccess(getUserContext(),
				getSelectedL2FeatureKey(), CrudOperation.DELETE);

		int count = 0;
		int batchSize = 20;
		getSessionFiltering();
		boolean urlErrorMsg=false;

		Set<Long> exceptionIds = new HashSet<Long>();
		List<Long> removeIds = (List<Long>) QueryUtil.executeQuery("select id from "
				+ boClass.getSimpleName(), null, filterParams, domainId,
				batchSize);
		try {
			while (!removeIds.isEmpty()) {
				List<HiveAp> removedAps = QueryUtil.executeQuery(HiveAp.class,
						null, new FilterParams("id", removeIds));
				Collection<Long> successLst =  BoMgmt.getMapMgmt().removeHiveApsReturnRemovedIds(removeIds, true, true, null, resetDeviceFlag);
				
				count= count + successLst.size();
				
				removeIds.removeAll(successLst);
				exceptionIds.addAll(removeIds);
				// propagate to other section;
				removeApPropagated(removedAps,successLst);
				DeviceUtils diu = DeviceImpUtils.getInstance();
				diu.removeSerialNumberFromHm(removedAps, successLst, false, getDomain());
				
				removeIds = (List<Long>) QueryUtil.executeQuery("select id from "
						+ boClass.getSimpleName(), null, fetchNewfilterParams(filterParams, exceptionIds), domainId,
						batchSize);
			}
		} catch (ClientHandlerException ex) {
			log.error(ex);
			urlErrorMsg=true;
		} catch (Exception e) {
			log.error(e);
		}

		if (!exceptionIds.isEmpty()) {
			String msg = "";
			if (exceptionIds.size()==1) {
				msg="1 item";
			} else {  
				msg=exceptionIds.size() + " items";
			}
			if (urlErrorMsg) {
				addActionError(MgrUtil.getUserMessage("error.remove.exception.urlerror",msg));
			} else {
				addActionError(MgrUtil.getUserMessage("error.remove.exception",msg));
			}
		}
		
		return count;
	}

	@Override
	protected int removeAllBos(Class<? extends HmBo> boClass,
			FilterParams filterParams, Collection<Long> defaultIds)
			throws Exception {
		return removeAllBos(boClass, defaultIds);
	}

	private void removeApPropagated(List<HiveAp> removedAps, Collection<Long> successIds) {
		if (null == removedAps || removedAps.isEmpty() || successIds==null || successIds.isEmpty()) {
			return;
		}

		for (HiveAp hiveAp : removedAps) {
			if (!successIds.contains(hiveAp.getId())) {
				continue;
			}
			short managedStatus = hiveAp.getManageStatus();
			if (StringUtils.isBlank(hiveAp.getSerialNumber())) {
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, 
						MgrUtil.getUserMessage("hm.audit.log.remove.ap.from.hiveaplist"
									, new String[]{hiveAp.getHostName()
											, getHiveApListName(managedStatus)}));
			} else {
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, 
						MgrUtil.getUserMessage("hm.audit.log.remove.ap.from.hiveaplist.withserialnumber"
									, new String[]{hiveAp.getHostName()
											, hiveAp.getSerialNumber()
											, getHiveApListName(managedStatus)}));
			}
		}
	}

	// while accept, update, multi-modify, must using this method to get
	// the real selected hiveAps.
	private List<Long> getSelectedHiveApIds() {
		if(selectedDeviceIdStr != null && !"".equals(selectedDeviceIdStr)){
			List<Long> s_ids = new ArrayList<Long>();
			String[] ids = selectedDeviceIdStr.split(",");
			for(int i=0; i<ids.length; i++){
				s_ids.add(Long.valueOf(ids[i]));
			}
			return s_ids;
		}else if (allItemsSelected) {
			getSessionFiltering();
			if (domainId == null) {
				domainId = QueryUtil.getDependentDomainFilter(userContext);
			}
			List<?> ids = QueryUtil.executeQuery("select id from "
					+ boClass.getSimpleName(), null, filterParams, domainId);
			List<Long> selectedIds = new ArrayList<Long>(ids.size());

			for (Object obj : ids) {
				selectedIds.add((Long) obj);
			}

			return selectedIds;
		} else {
			Set<Long> allSelectedIds = getAllSelectedIds();
			List<Long> sIds = new ArrayList<Long>();
			if (null != allSelectedIds) {
				sIds.addAll(allSelectedIds);
			}
			return sIds;
		}
	}

	protected HiveAp updateHiveAp(HiveAp hiveAp, MapContainerNode newParent,
			short oldManagedStatus, boolean oldConnection) throws Exception {
		log.info("hiveApAction", "Customized update.");
		if (hiveAp == null || hiveAp.getId() == null
				|| !hiveAp.getId().equals(id)) {
			throw new HmException(
					"Update object failed, session must have been shared by another browser window.",
					HmMessageCodes.STALE_SESSION_OBJECT,
					new String[] { "Update" });
		}
		AccessControl.checkUserAccess(getUserContext(),
				getSelectedL2FeatureKey(), CrudOperation.UPDATE);
		
		// user couldn't update across objects
		if(null != getUserContext()){
			// load owner first
			HmBo bo = QueryUtil.findBoById(HiveAp.class, hiveAp.getId());
			// user can edit default ip tracking
			if (null != bo && null != bo.getOwner()
					&& !HmDomain.GLOBAL_DOMAIN.equals(bo.getOwner().getDomainName())
					&& !bo.getOwner().getId().equals(
							QueryUtil.getDependentDomainFilter(getUserContext()))) {
				throw new HmException("User '" + getUserContext().getUserName()
						+ "' does not have WRITE access to object '"
						+ bo.getLabel() + "'.",
						HmMessageCodes.PERMISSION_DENIED_OBJECT_OPERATION,
						new String[] { getUserContext().getUserName(), bo.getLabel() });
			}
		}
		
		return BoMgmt.getMapMgmt().updateHiveApWithPropagation(hiveAp,
				newParent, oldConnection, oldManagedStatus);
	}

	/*-
	 *This function is forbidden in HiveAP update section, since HiveAp is related
	 *with its map leafNode, also the various count of HiveAp in cache.
	 *Pls use updateHiveAp function above!
	 */
	/*-
	@Override
	protected HmBo updateBo(HmBo hmBo) throws Exception {
		log.info("hiveApAction", "Customized update.");
		if (hmBo == null || hmBo.getId() == null || !hmBo.getId().equals(id)) {
			throw new HmException(
					"Update object failed, session must have been shared by another browser window.",
					HmMessageCodes.STALE_SESSION_OBJECT,
					new String[] { "Update" });
		}
		AccessControl.checkUserAccess(getUserContext(),
				getSelectedL2FeatureKey(), CrudOperation.UPDATE);
		MapContainerNode mapContainer = getSelectedTopology();
		HiveAp hiveAp = (HiveAp) hmBo;
		MapLeafNode mapLeafNode = hiveAp.getMapLeafNode();

		try {
			if (null == mapContainer && null == mapLeafNode) {
				// not assign a map now and before. just update HiveAp self.
				hiveAp = BoMgmt.getMapMgmt().updateHiveAp(hiveAp);
			} else if (null == mapContainer && null != mapLeafNode) {
				// not assign a map now, but before assigned a map.
				hiveAp = BoMgmt.getMapMgmt().removeMapLeafNode(hiveAp);
			} else if (null != mapContainer && null == mapLeafNode) {
				// assigned a map now, but before not assigned.
				MapLeafNode leafNode = new MapLeafNode();
				BoMgmt.getMapMgmt().placeIcon(mapContainer, leafNode);
				leafNode.setIconName(MapMgmt.BASE_LEAFNODE_ICON);
				leafNode.setSeverity(hiveAp.getSeverity());
				hiveAp = BoMgmt.getMapMgmt().createMapLeafNode(hiveAp,
						leafNode, mapContainer);
			} else if (!(mapContainer.getId().equals(mapLeafNode.getParentMap()
					.getId()))) {
				// both has a map now and before, but map is changed.
				MapLeafNode leafNode = new MapLeafNode();
				BoMgmt.getMapMgmt().placeIcon(mapContainer, leafNode);
				leafNode.setIconName(MapMgmt.BASE_LEAFNODE_ICON);
				leafNode.setSeverity(hiveAp.getSeverity());
				hiveAp = BoMgmt.getMapMgmt().replaceMapLeafNode(hiveAp,
						leafNode, mapContainer);
			} else if (mapContainer.getId().equals(
					mapLeafNode.getParentMap().getId())
					&& !(hiveAp.getHostName().equals(mapLeafNode.getApName()))) {
				// both has a map now and before, and map is the same, but ap
				// host name changed.
				hiveAp = BoMgmt.getMapMgmt().updateHiveAp(hiveAp);
			} else {
				hiveAp = BoMgmt.getMapMgmt().updateHiveAp(hiveAp);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		return hiveAp;
	}
	 */

	@Override
	protected Long createBo(HmBo hmBo) throws Exception {
		log.info("hiveApAction", "Customized create.");
		if (hmBo == null || hmBo.getId() != null) {
			throw new HmException(
					"Create object failed, session must have been shared by another browser window.",
					HmMessageCodes.STALE_SESSION_OBJECT,
					new String[] { "Create" });
		}
		AccessControl.checkUserAccess(getUserContext(),
				getSelectedL2FeatureKey(), CrudOperation.CREATE);
		if (getUserContext() != null && hmBo.getOwner() == null) {
			if (getUserContext().getSwitchDomain() != null) {
				hmBo.setOwner(getUserContext().getSwitchDomain());
			} else {
				hmBo.setOwner(getUserContext().getDomain());
			}
		}
		HiveAp hiveAp = (HiveAp) hmBo;
		HiveAp createdHiveAp;
		try {
			MapContainerNode mapContainer = getSelectedTopology();
			createdHiveAp = BoMgmt.getMapMgmt().createHiveApWithPropagation(
					hiveAp, mapContainer);
			updateClassifierTags(hiveAp);

			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.base.operation.create",new String[]{NmsUtil.getOEMCustomer().getAccessPonitName(),dataSource.getLabel()}));
		} catch (RuntimeException e) {
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.base.operation.create",new String[]{NmsUtil.getOEMCustomer().getAccessPonitName(),dataSource.getLabel()}));
			throw new RuntimeException(e);
		}
		return null == createdHiveAp ? null : createdHiveAp.getId();
	}

	@Override
	protected <E extends HmBo> E updateBo(E hmBo) throws Exception {
		HiveAp updatedHiveAp;
		try {
			HiveAp hiveAp = (HiveAp) hmBo;
			// get the previous HiveAP;
			HiveAp previous = QueryUtil
					.findBoById(HiveAp.class, hiveAp.getId());
			updatedHiveAp = updateHiveAp(hiveAp, getSelectedTopology(), hiveAp
					.getManageStatus(), hiveAp.isConnected());
			addActionMessage(MgrUtil.getUserMessage(OBJECT_UPDATED, dataSource
					.getLabel()));
			updateClassifierTags(hiveAp);
			// showMixHiveApInfo(getSelectedTopology(), hiveAp);
			// generate an event to configuration indication process
			HmBeEventUtil
					.eventGenerated(new ConfigurationChangedEvent(previous,
							ConfigurationChangedEvent.Operation.UPDATE, null));
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, "Update "+NmsUtil.getOEMCustomer().getAccessPonitName()+" ("
					+ dataSource.getLabel() + ")");
		} catch (RuntimeException e) {
			generateAuditLog(HmAuditLog.STATUS_FAILURE, "Update "+NmsUtil.getOEMCustomer().getAccessPonitName()+" ("
					+ dataSource.getLabel() + ")");
			throw new RuntimeException(e);
		}
		return (E) updatedHiveAp;
	}

	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}
		if (bo instanceof MapLeafNode) {
			((MapLeafNode) bo).getHiveAp().getId();
			return null;
		} else if (bo instanceof MapContainerNode) {
			MapContainerNode container = (MapContainerNode) bo;
			Set<MapNode> children = container.getChildNodes();
			for (MapNode node : children) {
				if (node.isLeafNode()) {
					MapLeafNode leafNode = (MapLeafNode) node;
					if (null != leafNode.getHiveAp()) {
						leafNode.getHiveAp().getId();
					}
				}
			}
			container.getPlannedAPs().size();
		} else if (bo instanceof ConfigTemplate) {
			// Just to trigger load from database
			ConfigTemplate wlan = (ConfigTemplate) bo;
			((ConfigTemplate) bo).getSsidInterfaces().values();
			if (null != wlan.getSsidInterfaces()) {
				wlan.getSsidInterfaces().values();
			}
			if (null != wlan.getEth0BackServiceFilter()) {
				wlan.getEth0BackServiceFilter().getId();
			}
			if (null != wlan.getEth1BackServiceFilter()) {
				wlan.getEth1BackServiceFilter().getId();
			}
			if (null != wlan.getVpnService()) {
				wlan.getVpnService().getId();
			}
			if (null != wlan.getPortProfiles()){
				for(PortGroupProfile pgp: wlan.getPortProfiles()){
					if(pgp.getBasicProfiles() != null){
						pgp.getBasicProfiles().size();
						for(PortBasicProfile basicPort : pgp.getBasicProfiles()){
							if(basicPort.getAccessProfile() != null){
								basicPort.getAccessProfile().getId();
								if(null != basicPort.getAccessProfile().getCwp()){
									basicPort.getAccessProfile().getCwp().getId();
								}
							}
						}
					}
				}
			}
			if (null != wlan.getRadiusServerProfile()){
				wlan.getRadiusServerProfile().getId();
			}
			if (wlan.getVpnService() != null){
				wlan.getVpnService().getId();
			}
			if(wlan.getHiveProfile() != null){
				wlan.getHiveProfile().getId();
			}
			if(wlan.getStormControlList() != null){
				wlan.getStormControlList().size();
			}
			
			if(wlan.getLldpCdp() != null){
				wlan.getLldpCdp().getId();
			}
			
			if(null != wlan.getSwitchSettings()){
				if(null != wlan.getSwitchSettings().getStpSettings()){
					if(null != wlan.getSwitchSettings().getStpSettings().getMstpRegion()){
						if(null != wlan.getSwitchSettings().getStpSettings().getMstpRegion().getMstpRegionPriorityList()){
							wlan.getSwitchSettings().getStpSettings().getMstpRegion().getMstpRegionPriorityList().size();
						}
					}
				}
			}
			
			if(null != wlan.getSupplementalCLI()){
				wlan.getSupplementalCLI().getId();
			}
			return null;
		} else if (bo instanceof HiveAp) {
			HiveAp hiveAp = (HiveAp) bo;
			// Just calling the get method will fetch the LAZY attributes
			// Call additional LAZY methods
			if (hiveAp.getConfigTemplate().getSsidInterfaces() != null){
				hiveAp.getConfigTemplate().getSsidInterfaces().size();
			}
			
			Collection<ConfigTemplateSsid> cfSsids = hiveAp
					.getConfigTemplate().getSsidInterfaces().values();
			for (ConfigTemplateSsid cfSsid : cfSsids) {
				SsidProfile ssid = cfSsid.getSsidProfile();
				if (null != ssid && null != ssid.getNRateSets()) {
					ssid.getNRateSets().size();
				}
				if (null != ssid && null != ssid.getAcRateSets()) {
					ssid.getAcRateSets().size();
				}
			}
			
			if (hiveAp.getWifi0RadioProfile() != null)
				hiveAp.getWifi0RadioProfile().getId();
			if (hiveAp.getWifi1RadioProfile() != null)
				hiveAp.getWifi1RadioProfile().getId();
			if (hiveAp.getStaticRoutes() != null) {
				for (int i = 0; i < hiveAp.getStaticRoutes().size(); i++) {
					hiveAp.getStaticRoutes().get(i).getDestinationMac();
					hiveAp.getStaticRoutes().get(i).getInterfaceType();
					hiveAp.getStaticRoutes().get(i).getNextHopMac();
				}
			}
			if (hiveAp.getDynamicRoutes() != null) {
				for (int i = 0; i < hiveAp.getDynamicRoutes().size(); i++) {
					hiveAp.getDynamicRoutes().get(i).getNeighborMac();
					hiveAp.getDynamicRoutes().get(i).getRouteMaximun();
					hiveAp.getDynamicRoutes().get(i).getRouteMinimun();
				}
			}
			if (hiveAp.getMultipleVlan() != null) {
				for (int i = 0; i < hiveAp.getMultipleVlan().size(); i++) {
					hiveAp.getMultipleVlan().get(i).getVlanid();
				}
			}
			if (hiveAp.getIpRoutes() != null) {
				for (int i = 0; i < hiveAp.getIpRoutes().size(); i++) {
					hiveAp.getIpRoutes().get(i).getGateway();
					hiveAp.getIpRoutes().get(i).getNetmask();
					hiveAp.getIpRoutes().get(i).getSourceIp();
				}
			}
			if (hiveAp.getVirtualConnections() != null) {
				for (int i = 0; i < hiveAp.getVirtualConnections().size(); i++) {
					hiveAp.getVirtualConnections().get(i).getDestMac();
					hiveAp.getVirtualConnections().get(i).getForwardAction();
					hiveAp.getVirtualConnections().get(i).getForwardName();
					hiveAp.getVirtualConnections().get(i).getInterface_in();
					hiveAp.getVirtualConnections().get(i).getInterface_out();
					hiveAp.getVirtualConnections().get(i).getRxMac();
					hiveAp.getVirtualConnections().get(i).getSourceMac();
					hiveAp.getVirtualConnections().get(i).getTxMac();
				}
			}
			if (hiveAp.getLearningMacs() != null) {
				for (int i = 0; i < hiveAp.getLearningMacs().size(); i++) {
					hiveAp.getLearningMacs().get(i).getMac().getId();
				}
			}
			if(hiveAp.getRoutingProfilePolicy()!=null){
				hiveAp.getRoutingProfilePolicy().getId();
				if(hiveAp.getRoutingProfilePolicy().getRoutingProfilePolicyRuleList()!=null){
					hiveAp.getRoutingProfilePolicy().getRoutingProfilePolicyRuleList().size();
				}
			}
			if (hiveAp.getConfigTemplate() != null) {
				hiveAp.getConfigTemplate().getId();
				if (hiveAp.getConfigTemplate().getRadiusServerProfile()!=null) {
					hiveAp.getConfigTemplate().getRadiusServerProfile().getId();
				}
				if (hiveAp.getConfigTemplate().getVlan()!=null) {
					hiveAp.getConfigTemplate().getVlan().getId();
				}
				if (hiveAp.getConfigTemplate().getHiveProfile()!=null) {
					hiveAp.getConfigTemplate().getHiveProfile().getId();
				}
				
				if(hiveAp.getConfigTemplate().getRoutingProfilePolicy()!=null){
					hiveAp.getConfigTemplate().getRoutingProfilePolicy().getId();
					if(hiveAp.getConfigTemplate().getRoutingProfilePolicy().getRoutingProfilePolicyRuleList()!=null){
						hiveAp.getConfigTemplate().getRoutingProfilePolicy().getRoutingProfilePolicyRuleList().size();
					}
				}

				if (hiveAp.getConfigTemplate().getVlanNetwork()!=null) {
					hiveAp.getConfigTemplate().getVlanNetwork().size();
					for(ConfigTemplateVlanNetwork cvn: hiveAp.getConfigTemplate().getVlanNetwork()){
						if (cvn.getVlan()!=null) {
							cvn.getVlan().getId();
						}
						if (cvn.getNetworkObj()!=null) {
							cvn.getNetworkObj().getId();
						}
					}
				}
				if(hiveAp.getConfigTemplate().getPortProfiles() != null){
					hiveAp.getConfigTemplate().getPortProfiles().size();
					for(PortGroupProfile pgp: hiveAp.getConfigTemplate().getPortProfiles()){
						pgp.getId();
						if(pgp.getBasicProfiles() != null){
							pgp.getBasicProfiles().size();
							for (PortBasicProfile basic : pgp.getBasicProfiles()) {
								basic.getAccessProfile().getId();
								if(null != basic.getAccessProfile().getCwp()){
									basic.getAccessProfile().getCwp().getId();
								}
								if(null != basic.getAccessProfile().getVoiceVlan()){
								    basic.getAccessProfile().getVoiceVlan().getId();
								}
								if(null != basic.getAccessProfile().getDataVlan()){
								    basic.getAccessProfile().getDataVlan().getId();
								}
								if(null != basic.getAccessProfile().getDefUserProfile()){
									basic.getAccessProfile().getDefUserProfile().getId();
									if(null != basic.getAccessProfile().getDefUserProfile().getVlan()){
										basic.getAccessProfile().getDefUserProfile().getVlan().getId();
									}
								}
								if(null != basic.getAccessProfile().getSelfRegUserProfile()){
									basic.getAccessProfile().getSelfRegUserProfile().getId();
									if(null != basic.getAccessProfile().getSelfRegUserProfile().getVlan()){
										basic.getAccessProfile().getSelfRegUserProfile().getVlan().getId();
									}
								}
								if(null != basic.getAccessProfile().getAuthOkUserProfile()){
									basic.getAccessProfile().getAuthOkUserProfile().size();
									for(UserProfile userProfile : basic.getAccessProfile().getAuthOkUserProfile()){
										userProfile.getVlan().getId();
									}
								}
								if(null != basic.getAccessProfile().getAuthOkDataUserProfile()){
									basic.getAccessProfile().getAuthOkDataUserProfile().size();
									for(UserProfile userProfile : basic.getAccessProfile().getAuthOkDataUserProfile()){
										userProfile.getVlan().getId();
									}
								}
								if(null != basic.getAccessProfile().getAuthFailUserProfile()){
									basic.getAccessProfile().getAuthFailUserProfile().size();
									for(UserProfile userProfile : basic.getAccessProfile().getAuthFailUserProfile()){
										userProfile.getVlan().getId();
									}
								}
							}
						}
					}
				}
				if(hiveAp.getConfigTemplate().getStormControlList() != null){
					hiveAp.getConfigTemplate().getStormControlList().size();
				}
				
				if(null != hiveAp.getConfigTemplate().getLldpCdp()){
					hiveAp.getConfigTemplate().getLldpCdp().getId();
				}
				
				if (hiveAp.getConfigTemplate().getSwitchSettings() != null) {
					if (hiveAp.getConfigTemplate().getSwitchSettings()
							.getStpSettings() != null) {
						if (hiveAp.getConfigTemplate().getSwitchSettings()
								.getStpSettings().getMstpRegion() != null) {
							if (hiveAp.getConfigTemplate().getSwitchSettings()
									.getStpSettings().getMstpRegion()
									.getMstpRegionPriorityList() != null) {
								hiveAp.getConfigTemplate().getSwitchSettings()
										.getStpSettings().getMstpRegion()
										.getMstpRegionPriorityList().size();
							}
						}
					}
				}
				
				if(null != hiveAp.getConfigTemplate().getSupplementalCLI()){
					hiveAp.getConfigTemplate().getSupplementalCLI().getId();
				}
			}
			if (hiveAp.getRadiusServerProfile() != null)
				hiveAp.getRadiusServerProfile().getId();
			if (hiveAp.getRadiusProxyProfile() != null)
				hiveAp.getRadiusProxyProfile().getId();
			if (hiveAp.getRoutingProfilePolicy() != null)
				hiveAp.getRoutingProfilePolicy().getId();
			if (hiveAp.getDhcpServers() != null) {
				hiveAp.getDhcpServers().size();
			}
			if (hiveAp.getDisabledSsids() != null) {
				hiveAp.getDisabledSsids().size();
			}
			if (hiveAp.getL3Neighbors() != null) {
				hiveAp.getL3Neighbors().size();
			}
			if (hiveAp.getEth0UserProfile() != null) {
				hiveAp.getEth0UserProfile().getId();
			}
			if (hiveAp.getEth1UserProfile() != null) {
				hiveAp.getEth1UserProfile().getId();
			}
			if (hiveAp.getAgg0UserProfile() != null) {
				hiveAp.getAgg0UserProfile().getId();
			}
			if (hiveAp.getRed0UserProfile() != null) {
				hiveAp.getRed0UserProfile().getId();
			}
			if (hiveAp.getEthCwpCwpProfile() != null) {
				hiveAp.getEthCwpCwpProfile().getId();
			}
			if (hiveAp.getEthCwpDefaultAuthUserProfile() != null) {
				hiveAp.getEthCwpDefaultAuthUserProfile().getId();
			}
			//add by nxma
			if (hiveAp.getMultipleVlan() != null) {
				hiveAp.getMultipleVlan().size();
			}
			if (hiveAp.getEthCwpDefaultRegUserProfile() != null) {
				hiveAp.getEthCwpDefaultRegUserProfile().getId();
			}
			if (hiveAp.getEthCwpRadiusClient() != null) {
				hiveAp.getEthCwpRadiusClient().getId();
			}
			if (hiveAp.getEthCwpRadiusUserProfiles() != null) {
				hiveAp.getEthCwpRadiusUserProfiles().size();
			}
			if (hiveAp.getSecondVPNGateway() != null) {
				hiveAp.getSecondVPNGateway().getId();
				if(hiveAp.getSecondVPNGateway().getDeviceInterfaces() != null){
					for(DeviceInterface dInt : hiveAp.getSecondVPNGateway().getDeviceInterfaces().values()){
						dInt.getDeviceIfType();
					}
				}
			}
			if (hiveAp.getRoutingProfile() != null){
				hiveAp.getRoutingProfile().getId();

				if (null != hiveAp.getRoutingProfile().getItems()) {
					hiveAp.getRoutingProfile().getItems().size();
				}

			}
			if (hiveAp.getDeviceInterfaces() != null){
				hiveAp.getDeviceInterfaces().values();
			}
			if (hiveAp.getUsbModemList() != null){
				hiveAp.getUsbModemList().size();
			}
			if (hiveAp.getMultipleVlan() != null){
				hiveAp.getMultipleVlan().size();
			}
			if (hiveAp.getConfigTemplate() != null) {
				hiveAp.getConfigTemplate().getId();
			}
			if (hiveAp.getInternalNetworks() != null){
				hiveAp.getInternalNetworks().size();
			}
			if(hiveAp.getCvgDPD() != null){
				if(hiveAp.getCvgDPD().getDnsForCVG() != null)
					hiveAp.getCvgDPD().getDnsForCVG().getId();
				if(hiveAp.getCvgDPD().getMgtNetwork() != null)
					hiveAp.getCvgDPD().getMgtNetwork().getId();
				if(hiveAp.getCvgDPD().getNtpForCVG() != null)
					hiveAp.getCvgDPD().getNtpForCVG().getId();
				if(hiveAp.getCvgDPD().getMgtVlan() != null)
					hiveAp.getCvgDPD().getMgtVlan().getId();
				if(hiveAp.getCvgDPD().getMgmtServiceSyslog() != null)
					hiveAp.getCvgDPD().getMgmtServiceSyslog().getId();
				if(hiveAp.getCvgDPD().getMgmtServiceSnmp() != null)
					hiveAp.getCvgDPD().getMgmtServiceSnmp().getId();
			}

			if (hiveAp.getPppoeAuthProfile() != null){
				hiveAp.getPppoeAuthProfile().getId();
			}

			if(hiveAp.getMapContainer() != null && null != hiveAp.getMapContainer().getParentMap()) {
			    hiveAp.getMapContainer().getParentMap().getId();
			}

			if(hiveAp.getWifiClientPreferredSsids() != null){
				hiveAp.getWifiClientPreferredSsids().size();
			}
			if(hiveAp.getIgmpPolicys() != null){
				hiveAp.getIgmpPolicys().size();
			}
			if(hiveAp.getMulticastGroups() != null){
				hiveAp.getMulticastGroups().size();
				for(MulticastGroup mg : hiveAp.getMulticastGroups()){
					mg.getId();
					if(null != mg.getInterfaces()){
						mg.getInterfaces().size();
					}
				}
			}
			if(null != hiveAp.getForwardingDB()){
				hiveAp.getForwardingDB().getId();
				if(null != hiveAp.getForwardingDB().getMacAddressEntries()){
					hiveAp.getForwardingDB().getMacAddressEntries().size();
				}
			}
			if(null != hiveAp.getStormControlList()){
				hiveAp.getStormControlList().size();
			}

			//load stp settings
			if(hiveAp.getDeviceStpSettings() != null){
				if(hiveAp.getDeviceStpSettings().getInstancePriority() != null){
					hiveAp.getDeviceStpSettings().getInstancePriority().size();
				}
				if(hiveAp.getDeviceStpSettings().getInterfaceMstpSettings() != null){
					hiveAp.getDeviceStpSettings().getInterfaceMstpSettings().size();
				}
				if(hiveAp.getDeviceStpSettings().getInterfaceStpSettings() != null){
					hiveAp.getDeviceStpSettings().getInterfaceStpSettings().size();
				}
			}
			
			if(null != hiveAp.getSupplementalCLI()){
				hiveAp.getSupplementalCLI().getId();
			}
		} else if (bo instanceof SsidProfile) {
			bo.getOwner().getId();
			if (((SsidProfile) bo).getLocalUserGroups() != null) {
				((SsidProfile) bo).getLocalUserGroups().size();
			}
		} else if(bo instanceof SubNetworkResource) {
			SubNetworkResource subNetworkResource = (SubNetworkResource) bo;
			if(subNetworkResource.getVpnNetwork() != null){
				subNetworkResource.getVpnNetwork().getId();
				if(subNetworkResource.getVpnNetwork().getSubItems() != null){
					subNetworkResource.getVpnNetwork().getSubItems().size();
				}
			}
		} else if (bo instanceof RadiusOnHiveap) {
			/** RadiusOnHiveap */
			RadiusOnHiveap radiusObj = (RadiusOnHiveap) bo;
			if (radiusObj.getDirectoryOrLdap() != null){
				radiusObj.getDirectoryOrLdap().size();
			}
		} else if (bo instanceof MstpRegion){
			MstpRegion mstpObj = (MstpRegion)bo;
			if(mstpObj.getMstpRegionPriorityList() != null){
				mstpObj.getMstpRegionPriorityList().size();
			}
		} else if (bo instanceof StpSettings){
			StpSettings stpSettings = (StpSettings) bo;
			if(stpSettings.getMstpRegion() != null){
				if(stpSettings.getMstpRegion().getMstpRegionPriorityList() != null){
					stpSettings.getMstpRegion().getMstpRegionPriorityList().size();
				}
			}
		} else if (bo instanceof DeviceStpSettings){
			DeviceStpSettings settings = (DeviceStpSettings) bo;
			if(settings.getInstancePriority() != null){
				settings.getInstancePriority().size();
			}
			if(settings.getInterfaceMstpSettings() != null){
				settings.getInterfaceMstpSettings().size();
			}
			if(settings.getInterfaceStpSettings() != null){
				settings.getInterfaceStpSettings().size();
			}
		}else if(bo instanceof PortGroupProfile){
			PortGroupProfile pgp = (PortGroupProfile)bo;
			if(pgp.getBasicProfiles() != null){
				pgp.getBasicProfiles().size();
				for(PortBasicProfile basicPort : pgp.getBasicProfiles()){
					if(basicPort.getAccessProfile() != null){
						basicPort.getAccessProfile().getId();
					}
				}
			}
		}
		return null;
	}

	private List<HiveAp> getMultipleEditHiveAPs() throws Exception {
		List<Long> selectIds = getMultipleEditIds();
		List<HiveAp> list = new ArrayList<HiveAp>();
		if (null != selectIds) {
			list = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams(
					"id", selectIds), domainId, new QueryLazyBo());
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private List<Long> getMultipleEditIds() {
		List<Long> list = (List<Long>) MgrUtil
				.getSessionAttribute("selectHiveAPs");
		if (null == list) {
			list = new ArrayList<Long>();
		}
		return list;
	}

	private boolean isMutipleEditHiveAPWithVPN() {
		List<Long> editIds = getMultipleEditIds();
		if (null != editIds && !editIds.isEmpty()) {
			String query = "select bo.configTemplate.vpnService.id from "
					+ HiveAp.class.getSimpleName() + " bo";
			List<?> list = QueryUtil.executeQuery(query, null,
					new FilterParams("id", editIds));
			boolean allConfigured = true;
			for (Object object : list) {
				if (null == object) {
					allConfigured = false;
					break;
				}
			}
			return allConfigured;
		}
		return false;
	}

	public void setModifyValues(List<HiveAp> selectHiveAPs) throws Exception {
		HiveAp currentHiveAp = getDataSource();

		boolean mapChanged = null != topology && topology != -3;
		MapContainerNode settingMap = mapChanged ? getSelectedTopology() : null;
		
		//update classifier tags
		updateClassifierTags(currentHiveAp);

		// create capwapIp object if needed;
		IpAddress createdIp = autoCreateIpAddress();
		IpAddress createBackupIp = autoCreateBackupIpAddress();
		int updateCount = 0;
        
		for (HiveAp hiveAp : selectHiveAPs) {
			if (null != configTemplate && configTemplate != -3) {
				ConfigTemplate template = currentHiveAp.getConfigTemplate();
				if (template != null) {
					hiveAp.setConfigTemplate(template);
				}
			}
			if (wifi0RadioProfile != null && wifi0RadioProfile != -3) {
				RadioProfile radioProfile = currentHiveAp
						.getWifi0RadioProfile();
				if (radioProfile != null) {
					hiveAp.setWifi0RadioProfile(radioProfile);
				}
			}
			if (wifi1RadioProfile != null && wifi1RadioProfile != -3) {
				RadioProfile radioProfile1 = currentHiveAp
						.getWifi1RadioProfile();
				if (radioProfile1 != null) {
					hiveAp.setWifi1RadioProfile(radioProfile1);
				}
			}
			if (null != capwapIp && capwapIp != -3) {
				IpAddress ipaddress = currentHiveAp.getCapwapIpBind();
				hiveAp.setCapwapIpBind(ipaddress);
				if (null != createdIp) {
					hiveAp.setCapwapIpBind(createdIp);
				}
			}
			if (null != capwapBackupIp && capwapBackupIp != -3) {
				IpAddress ipaddress = currentHiveAp.getCapwapBackupIpBind();
				hiveAp.setCapwapBackupIpBind(ipaddress);
				if (null != createBackupIp) {
					hiveAp.setCapwapBackupIpBind(createBackupIp);
				}
			}
			if (null != scheduler && scheduler != -3) {
				Scheduler sd = currentHiveAp.getScheduler();
				hiveAp.setScheduler(sd);
			}
			if(null != supplementalCLIId && supplementalCLIId != -3){
				CLIBlob cliBlob = currentHiveAp.getSupplementalCLI();
				hiveAp.setSupplementalCLI(cliBlob);
			}
			
			if (!currentHiveAp.getGateway().trim().equals(strNoChange)) {
				hiveAp.setCfgGateway(currentHiveAp.getGateway().trim());
			}
			// Only change these attribute when device is not BR100 platform.
			if (hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR100) {
				if (null != insertTopologyInfo && insertTopologyInfo != -3) {
					boolean isInclude = insertTopologyInfo == 1;
					hiveAp.setIncludeTopologyInfo(isInclude);
				}
				if (!currentHiveAp.getLocation().trim().equals(strNoChange)) {
					hiveAp.setLocation(currentHiveAp.getLocation());
				}
			}
			if (currentHiveAp.getNativeVlan() != 0) {
				hiveAp.setNativeVlan(currentHiveAp.getNativeVlan());
			}
			if (currentHiveAp.getMgtVlan() != 0) {
				hiveAp.setMgtVlan(currentHiveAp.getMgtVlan());
			}
			if (currentHiveAp.getEth0().getAdminState() != -3) {
				hiveAp.getEth0().setAdminState(
						currentHiveAp.getEth0().getAdminState());
			}
			if (!currentHiveAp.getEth0().getAllowedVlan().equals(strNoChange)) {
				hiveAp.getEth0().setAllowedVlan(
						currentHiveAp.getEth0().getAllowedVlan());
			}

			if (currentHiveAp.getEth0().getMultiNativeVlan() != null && currentHiveAp.getEth0().getMultiNativeVlan()!=1) {
				hiveAp.getEth0().setMultiNativeVlan(
						currentHiveAp.getEth0().getMultiNativeVlan());
			}

			if (currentHiveAp.getEth0().getOperationMode() != -3) {
				hiveAp.getEth0().setOperationMode(
						currentHiveAp.getEth0().getOperationMode());
			}
			if (currentHiveAp.getEth0().getDuplex() != -3) {
				hiveAp.getEth0().setDuplex(currentHiveAp.getEth0().getDuplex());
			}
			if (currentHiveAp.getEth0().getSpeed() != -3) {
				hiveAp.getEth0().setSpeed(currentHiveAp.getEth0().getSpeed());
			}

			if (currentHiveAp.getWifi0().getOperationMode() != -3) {
				hiveAp.getWifi0().setOperationMode(
						currentHiveAp.getWifi0().getOperationMode());
			}
			if (currentHiveAp.getWifi0().getChannel() != -3) {
				hiveAp.getWifi0().setChannel(
						currentHiveAp.getWifi0().getChannel());
			}
			if (currentHiveAp.getWifi0().getPower() != -3) {
				hiveAp.getWifi0().setPower(currentHiveAp.getWifi0().getPower());
			}
			if (currentHiveAp.getWifi0().getAdminState() != -3) {
				hiveAp.getWifi0().setAdminState(
						currentHiveAp.getWifi0().getAdminState());
			}
			if (currentHiveAp.getWifi1().getOperationMode() != -3) {
				hiveAp.getWifi1().setOperationMode(
						currentHiveAp.getWifi1().getOperationMode());
			}
			if (currentHiveAp.getWifi1().getChannel() != -3) {
				hiveAp.getWifi1().setChannel(
						currentHiveAp.getWifi1().getChannel());
			}
			if (currentHiveAp.getWifi1().getPower() != -3) {
				hiveAp.getWifi1().setPower(currentHiveAp.getWifi1().getPower());
			}
			if (currentHiveAp.getWifi1().getAdminState() != -3) {
				hiveAp.getWifi1().setAdminState(
						currentHiveAp.getWifi1().getAdminState());
			}
			
			if (currentHiveAp.getDeviceTxRetry() != -1) {
				hiveAp.setDeviceTxRetry(currentHiveAp.getDeviceTxRetry());
			}
			
			if (currentHiveAp.getClientTxRetry() != -1) {
				hiveAp.setClientTxRetry(currentHiveAp.getClientTxRetry());
			}
			
			boolean tagChangeFlg=false;
			if (!currentHiveAp.getClassificationTag1().trim().equals(
					strNoChange)) {
				hiveAp.setClassificationTag1(currentHiveAp
						.getClassificationTag1().trim());
				tagChangeFlg=true;
			}
			if (!currentHiveAp.getClassificationTag2().trim().equals(
					strNoChange)) {
				hiveAp.setClassificationTag2(currentHiveAp
						.getClassificationTag2().trim());
				tagChangeFlg=true;
			}
			if (!currentHiveAp.getClassificationTag3().trim().equals(
					strNoChange)) {
				hiveAp.setClassificationTag3(currentHiveAp
						.getClassificationTag3().trim());
				tagChangeFlg=true;
			}
			if (tagChangeFlg){
				String[] tags = null;
				List<String> tagsStr = new ArrayList<>();
				
				if(null != hiveAp.getClassificationTag1() && !"".equals(hiveAp.getClassificationTag1())){
					tagsStr.add(hiveAp.getClassificationTag1());
				}
				if(null != hiveAp.getClassificationTag2() && !"".equals(hiveAp.getClassificationTag2())){
					tagsStr.add(hiveAp.getClassificationTag2());
				}
				if(null != hiveAp.getClassificationTag3() && !"".equals(hiveAp.getClassificationTag3())){
					tagsStr.add(hiveAp.getClassificationTag3());
				}
				if(null != tagsStr && tagsStr.size() > 0){
					tags = new String[tagsStr.size()];
					tagsStr.toArray(tags);
				}
				NetworkDeviceConfigTracking.tagsChanged(tags,hiveAp.getMacAddress());
			}
			if (currentHiveAp.getDistributedPriority() != -3){
				hiveAp.setDistributedPriority(currentHiveAp.getDistributedPriority());
			}
			if (!currentHiveAp.getCfgAdminUser().trim().equals(strNoChange)) {
				hiveAp.setCfgAdminUser(currentHiveAp.getCfgAdminUser().trim());
				hiveAp.setCfgPassword(currentHiveAp.getCfgPassword().trim());
			}
			if (!currentHiveAp.getCfgReadOnlyUser().trim().equals(strNoChange)) {
				hiveAp.setCfgReadOnlyUser(currentHiveAp.getCfgReadOnlyUser()
						.trim());
				hiveAp.setCfgReadOnlyPassword(currentHiveAp
						.getCfgReadOnlyPassword().trim());
			}
			if (currentHiveAp.getVpnMark() != -3) {
				hiveAp.setVpnMark(currentHiveAp.getVpnMark());
			}
			if (currentHiveAp.isChangeIDMAuthProxy()) {
				hiveAp.setEnableIDMAuthProxy(currentHiveAp.isEnableIDMAuthProxy());
			}

			if (currentHiveAp.getMultiDisplayType()==HiveAp.MULTI_DISPLAY_BR) {
				boolean breth0Flg = false;
				boolean breth1Flg = false;
				boolean breth2Flg = false;
				boolean breth3Flg = false;
				boolean breth4Flg = false;
				boolean brusbFlg = false;
				boolean brwifi0Flg = false;
				boolean brwifi1Flg = false;
				DeviceInterface breth0 = hiveAp.getEth0Interface();
				if (branchRouterEth0.getAdminState()!=-3) {
					breth0.setAdminState(branchRouterEth0.getAdminState());
					breth0Flg = true;
				}
//				if (branchRouterEth0.getDuplex()!=-3) {
//					breth0.setDuplex(branchRouterEth0.getDuplex());
//					breth0Flg = true;
//				}
//				if (branchRouterEth0.getSpeed()!=-3) {
//					breth0.setSpeed(branchRouterEth0.getSpeed());
//					breth0Flg = true;
//				}
				if (breth0Flg){
					hiveAp.setEth0Interface(breth0);
				}

				DeviceInterface breth1 = hiveAp.getEth1Interface();
				if (branchRouterEth1.getAdminState()!=-3) {
					breth1.setAdminState(branchRouterEth1.getAdminState());
					breth1Flg = true;
				}
//				if (branchRouterEth1.getDuplex()!=-3) {
//					breth1.setDuplex(branchRouterEth1.getDuplex());
//					breth1Flg = true;
//				}
//				if (branchRouterEth1.getSpeed()!=-3) {
//					breth1.setSpeed(branchRouterEth1.getSpeed());
//					breth1Flg = true;
//				}
				if (breth1Flg){
					hiveAp.setEth1Interface(breth1);
				}

				DeviceInterface breth2 = hiveAp.getEth2Interface();
				if (branchRouterEth2.getAdminState()!=-3) {
					breth2.setAdminState(branchRouterEth2.getAdminState());
					breth2Flg = true;
				}
//				if (branchRouterEth2.getDuplex()!=-3) {
//					breth2.setDuplex(branchRouterEth2.getDuplex());
//					breth2Flg = true;
//				}
//				if (branchRouterEth2.getSpeed()!=-3) {
//					breth2.setSpeed(branchRouterEth2.getSpeed());
//					breth2Flg = true;
//				}
				if (breth2Flg){
					hiveAp.setEth2Interface(breth2);
				}

				DeviceInterface breth3 = hiveAp.getEth3Interface();
				if (branchRouterEth3.getAdminState()!=-3) {
					breth3.setAdminState(branchRouterEth3.getAdminState());
					breth3Flg = true;
				}
//				if (branchRouterEth3.getDuplex()!=-3) {
//					breth3.setDuplex(branchRouterEth3.getDuplex());
//					breth3Flg = true;
//				}
//				if (branchRouterEth3.getSpeed()!=-3) {
//					breth3.setSpeed(branchRouterEth3.getSpeed());
//					breth3Flg = true;
//				}
				if (breth3Flg){
					hiveAp.setEth3Interface(breth3);
				}

				DeviceInterface breth4 = hiveAp.getEth4Interface();
				if (branchRouterEth4.getAdminState()!=-3) {
					breth4.setAdminState(branchRouterEth4.getAdminState());
					breth4Flg = true;
				}
//				if (branchRouterEth4.getDuplex()!=-3) {
//					breth4.setDuplex(branchRouterEth4.getDuplex());
//					breth4Flg = true;
//				}
//				if (branchRouterEth4.getSpeed()!=-3) {
//					breth4.setSpeed(branchRouterEth4.getSpeed());
//					breth4Flg = true;
//				}
				if (breth4Flg){
					hiveAp.setEth4Interface(breth4);
				}

				DeviceInterface brusb = hiveAp.getUSBInterface();
				if (branchRouterUSB.getAdminState()!=-3) {
					brusb.setAdminState(branchRouterUSB.getAdminState());
					brusbFlg = true;
				}
//				if (branchRouterUSB.getDuplex()!=-3) {
//					brusb.setDuplex(branchRouterUSB.getDuplex());
//					brusbFlg = true;
//				}
//				if (branchRouterUSB.getSpeed()!=-3) {
//					brusb.setSpeed(branchRouterUSB.getSpeed());
//					brusbFlg = true;
//				}
				if (brusbFlg){
					hiveAp.setUSBInterface(brusb);
				}

				//for fix bug cannot tag multi device
//				DeviceInterface brwifi0 = hiveAp.getWifi0Interface();
//				if (branchRouterWifi0.getAdminState()!=-3) {
//					brwifi0.setAdminState(branchRouterWifi0.getAdminState());
//					brwifi0Flg = true;
//				}
//				if (brwifi0Flg){
//					hiveAp.setWifi0Interface(brwifi0);
//				}
//
//				DeviceInterface brwifi1 = hiveAp.getWifi1Interface();
//				if (branchRouterWifi1.getAdminState()!=-3) {
//					brwifi1.setAdminState(branchRouterWifi1.getAdminState());
//					brwifi1Flg = true;
//				}
//				if (brwifi1Flg){
//					hiveAp.setWifi1Interface(brwifi1);
//				}
			}

			if (strManageUponContact.equals("1")) {
				hiveAp.setManageUponContact(true);
			} else if (strManageUponContact.equals("2")) {
				hiveAp.setManageUponContact(false);
			}
			if (null != strEnableDas && !"".equals(strEnableDas)) {
				if (strEnableDas.equals("1")) {
					hiveAp.setEnableDas(true);
				} else if (strEnableDas.equals("2")) {
					hiveAp.setEnableDas(false);
				}
			}
			if (changePassPhrase) {
				hiveAp.setPassPhrase(currentHiveAp.getPassPhrase());
			}

			// reset radioConfigType if needed
			if (hiveAp.getRadioConfigType() != HiveAp.RADIO_MODE_CUSTOMIZE) {
				HiveAp.setHiveApRadioConfigType(hiveAp);
			}

			MapContainerNode node = mapChanged ? settingMap : hiveAp
					.getMapContainer();

			// reset realm name and priority
			if (hiveAp.getHiveApModel()!=HiveAp.HIVEAP_MODEL_BR100 && 
					hiveAp.getHiveApModel()!=HiveAp.HIVEAP_MODEL_20&& 
					hiveAp.getHiveApModel()!=HiveAp.HIVEAP_MODEL_28) {
				
				if (!currentHiveAp.getPriority().trim().equals(strNoChange)) {
					hiveAp.setPriority(currentHiveAp.getPriority().trim());
				}
				
				if (currentHiveAp.isMultiChangeLockRealmName()) {
					hiveAp.setLockRealmName(currentHiveAp.isLockRealmName());
				}
				
				if (!currentHiveAp.getRealmName().trim().equals(strNoChange)) {
					hiveAp.setRealmName(currentHiveAp.getRealmName().trim());
				} else {
					if (!hiveAp.isLockRealmName()) {
						if ((null != configTemplate && configTemplate != -3) || mapChanged) {
							if (node==null || hiveAp.getConfigTemplate()==null || hiveAp.getConfigTemplate().getHiveProfile()==null) {
								hiveAp.setRealmName("");
							} else {
								MapContainerNode mapContainerNode = QueryUtil.findBoById(
										MapContainerNode.class, node.getId(), new BonjourGatewayMonitoringAction());
								if(null != mapContainerNode){
									if(mapContainerNode.getMapType() == MapContainerNode.MAP_TYPE_FLOOR){
										if(mapContainerNode.getParentMap() != null){
											MapContainerNode parentMap = mapContainerNode.getParentMap();
											hiveAp.setRealmName(parentMap.getLabel() + "_" + hiveAp.getConfigTemplate().getHiveProfile().getHiveName());
										}
									} else {
										hiveAp.setRealmName(mapContainerNode.getLabel() + "_" + hiveAp.getConfigTemplate().getHiveProfile().getHiveName());
									}
								}
							}
						}
					}
				}
			}

			setId(hiveAp.getId());
			setWifiRadioMode(hiveAp);
			// update layer3 route if needed.
			updateLayer3Routes(hiveAp, currentHiveAp);
			//add the control for CAPWAP delay alarm from Guadalupe
			updateDelayAlarm(hiveAp,currentHiveAp);
			
			// if the Pass phrase is not match the current value, update the
			// keyId also;
			updateKeyId(hiveAp);
			try {
				boolean needSaveAp =true;
				if (hiveAp.isWifi1Available() &&
						hiveAp.getWifi0().getOperationMode()==AhInterface.OPERATION_MODE_ACCESS &&
						hiveAp.getWifi1().getOperationMode()==AhInterface.OPERATION_MODE_ACCESS ){
					if (hiveAp.isEth1Available() && hiveAp.getEthConfigType()== HiveAp.USE_ETHERNET_BOTH) {
						if (hiveAp.getEth0().getOperationMode()!= AhInterface.OPERATION_MODE_BACKHAUL
								&& hiveAp.getEth1().getOperationMode()!= AhInterface.OPERATION_MODE_BACKHAUL) {
							needSaveAp = false;
						}
					}
					if ( hiveAp.getEthConfigType()== HiveAp.USE_ETHERNET_AGG0) {
						if (hiveAp.getAgg0()!=null && hiveAp.getAgg0().getOperationMode()!= AhInterface.OPERATION_MODE_BACKHAUL) {
							needSaveAp = false;
						}
					}
					if (hiveAp.getEthConfigType()== HiveAp.USE_ETHERNET_RED0) {
						if (hiveAp.getRed0()!=null && hiveAp.getRed0().getOperationMode()!= AhInterface.OPERATION_MODE_BACKHAUL) {
							needSaveAp = false;
						}
					}
				}
				if (needSaveAp) {
					Date oldVer = hiveAp.getVersion();
					hiveAp = updateHiveAp(hiveAp, node, hiveAp.getManageStatus(),
							hiveAp.isConnected());
					addActionMessage(MgrUtil.getUserMessage(OBJECT_UPDATED, hiveAp
							.getLabel()));
					// generate an event to configuration indication process
					HmBeEventUtil.eventGenerated(new ConfigurationChangedEvent(
							hiveAp, ConfigurationChangedEvent.Operation.UPDATE,
							oldVer));
					updateCount++;

					//add by yang zhong fs
					if(mapChanged){
						String[] tags = null;
						List<String> tagsStr = new ArrayList<>();
						
						if(null != hiveAp.getClassificationTag1() && !"".equals(hiveAp.getClassificationTag1())){
							tagsStr.add(hiveAp.getClassificationTag1());
						}
						if(null != hiveAp.getClassificationTag2() && !"".equals(hiveAp.getClassificationTag2())){
							tagsStr.add(hiveAp.getClassificationTag2());
						}
						if(null != hiveAp.getClassificationTag3() && !"".equals(hiveAp.getClassificationTag3())){
							tagsStr.add(hiveAp.getClassificationTag3());
						}
						if(null != tagsStr && tagsStr.size() > 0){
							tags = new String[tagsStr.size()];
							tagsStr.toArray(tags);
						}
//						final HiveAp hiveAP = getDataSource();
						final Long vHMdomain = hiveAp.getOwner().getId();
						if(hiveAp.getMapContainer() == null){
							String sql = "select id from map_node where parent_map_id = " +
									"(select id from map_node where parent_map_id is null) and owner="+vHMdomain;
							List<?> list = QueryUtil.executeNativeQuery(sql, 1);
							if(!list.isEmpty()){
								NetworkDeviceConfigTracking.topologyChanged(Calendar.getInstance(),
										vHMdomain, hiveAp.getMacAddress(), hiveAp.getTimeZoneOffset(), new long[]{Long.parseLong(list.get(0).toString())}, tags);
							}
						}else{
							NetworkDeviceConfigTracking.topologyChanged(Calendar.getInstance(),
									vHMdomain, hiveAp.getMacAddress(), hiveAp.getTimeZoneOffset(), new long[]{hiveAp.getMapContainer().getId()}, tags);   //TODO  topologyGroupPkFromTop(exclusive)ToBottom(inclusive)
						}
					}
				} else {
					addActionError(MgrUtil.getUserMessage("action.error.item.update.fail", hiveAp .getLabel())
							+ MgrUtil.getUserMessage("error.hiveAp.operationMode.backhaul"));
				}
			} catch (RuntimeException e) {
				throw new RuntimeException(e);
			}
		}
		if (updateCount > 0) {
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.hiveap.count",new String[]{String.valueOf(updateCount),NmsUtil.getOEMCustomer().getAccessPonitName()}));
		}
	}

    private void updateClassifierTags(HiveAp hiveAp) {
        if (!(StringUtils.isNotBlank(hiveAp.getClassificationTag1()) 
                && hiveAp.getClassificationTag1().equals(strNoChange))) {
            DeviceTagUtil.getInstance().updateClassifierTag(
                    hiveAp.getClassificationTag1(), 1, this.getDomain());
        }
        if(!(StringUtils.isNotBlank(hiveAp.getClassificationTag2()) 
                && hiveAp.getClassificationTag2().equals(strNoChange))) {
            DeviceTagUtil.getInstance().updateClassifierTag(
                    hiveAp.getClassificationTag2(), 2, this.getDomain());
        }
        if(!(StringUtils.isNotBlank(hiveAp.getClassificationTag3()) 
                && hiveAp.getClassificationTag3().equals(strNoChange))) {
            DeviceTagUtil.getInstance().updateClassifierTag(
                    hiveAp.getClassificationTag3(), 3, this.getDomain());
        }
    }

	private void updateLayer3Routes(HiveAp hiveAp, HiveAp dataSource) {
		if (null == hiveAp || null == dataSource
				|| !dataSource.isChangeLayer3Route()) {
			return;
		}
		hiveAp.setIpRoutes(dataSource.getIpRoutes());
	}

//	private void updateMultiNativeVlan(HiveAp hiveAp, HiveAp dataSource) {
//		if (null == hiveAp || null == dataSource
//				|| !dataSource.isChangeMultiNativeVlan()) {
//			return;
//		}
//		hiveAp.setMultipleVlan(dataSource.getMultipleVlan());
//	}

	private String radioType;

	public String getRadioType() {
		return radioType;
	}

	public void setRadioType(String radioType) {
		this.radioType = radioType;
	}

	private String radioMode;

	public String getRadioMode() {
		return radioMode;
	}

	public void setRadioMode(String radioMode) {
		this.radioMode = radioMode;
	}

	private String profileId;

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	private String boxValue;

	public String getBoxValue() {
		return boxValue;
	}

	public void setBoxValue(String boxValue) {
		this.boxValue = boxValue;
	}

	private String bindInterface;

	public String getBindInterface() {
		return bindInterface;
	}

	public void setBindInterface(String bindInterface) {
		this.bindInterface = bindInterface;
	}

	private void prepareDataSourceMultiple(List<Long> lstSelectIds) {
		// determine the value of property 'apModelType'
		String query = "select hiveApModel,deviceType from "
				+ HiveAp.class.getSimpleName();
		List<?> models = QueryUtil.executeQuery(query, null, new FilterParams(
				"id", lstSelectIds));
		// following fields is used for display WLAN when multi modify
		List<Short> ap110 = new ArrayList<Short>();
		List<Short> ap300 = new ArrayList<Short>();
		List<Short> ap11ac = new ArrayList<Short>();
		List<Short> ap20 = new ArrayList<Short>();
		List<Short> br100 = new ArrayList<Short>();
		List<Short> br200cvg = new ArrayList<Short>();

		// following fields is used for display Lan interface and port setting when multi modify
		List<Short> lanIf = new ArrayList<Short>();
		List<Short> usbPortIf = new ArrayList<Short>();
		List<Short> ltePortIf = new ArrayList<Short>();

		// to indicate is all AP or include other device
		getDataSource().setMultiDisplayApOnly(true);
		getDataSource().setMultiIncludeCvg(false);
		getDataSource().setMultiDisplayRealm(true);

		for (Object object : models) {
			Object[] oneObj = (Object[]) object;
			Short model = (Short)oneObj[0];
			Short type = (Short)oneObj[1];
			if (null == model) {
				continue;
			}
			switch (model) {
			case HiveAp.HIVEAP_MODEL_BR100:
				br100.add(model);
				usbPortIf.add(model);
				if (type==HiveAp.Device_TYPE_HIVEAP) {
					getDataSource().setMultiIncludeBRAsAp(true);
				} else {
					getDataSource().setMultiDisplayApOnly(false);
				}
				getDataSource().setMultiDisplayRealm(false);
				getDataSource().setMultiDisplayLocation(false);
				break;
			case HiveAp.HIVEAP_MODEL_BR200:
				br200cvg.add(model);
				usbPortIf.add(model);
				getDataSource().setMultiDisplayApOnly(false);
				break;
			case HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA:
			case HiveAp.HIVEAP_MODEL_VPN_GATEWAY:
				br200cvg.add(model);
				getDataSource().setMultiDisplayApOnly(false);
				getDataSource().setMultiIncludeCvg(true);
				getDataSource().setMultiDisplayLocation(false);
				break;
			case HiveAp.HIVEAP_MODEL_SR24:
			case HiveAp.HIVEAP_MODEL_SR2124P:
			case HiveAp.HIVEAP_MODEL_SR2024P:
			case HiveAp.HIVEAP_MODEL_SR2148P:
			case HiveAp.HIVEAP_MODEL_SR48:
				br200cvg.add(model);
				getDataSource().setMultiDisplayApOnly(false);
				break;
			case HiveAp.HIVEAP_MODEL_110:
				lanIf.add(model);
				ap110.add(model);
				break;
			case HiveAp.HIVEAP_MODEL_BR200_WP:
				if (type==HiveAp.Device_TYPE_HIVEAP) {
					lanIf.add(model);
				} else if (type==HiveAp.Device_TYPE_BRANCH_ROUTER) {
					usbPortIf.add(model);
					getDataSource().setMultiDisplayApOnly(false);
				}
				ap110.add(model);
				break;
			case HiveAp.HIVEAP_MODEL_BR200_LTE_VZ:
				if (type==HiveAp.Device_TYPE_HIVEAP) {
					lanIf.add(model);
				} else if (type==HiveAp.Device_TYPE_BRANCH_ROUTER) {
					ltePortIf.add(model);
					getDataSource().setMultiDisplayApOnly(false);
				}
				ap110.add(model);
				break;
			case HiveAp.HIVEAP_MODEL_120:
			case HiveAp.HIVEAP_MODEL_121:
			case HiveAp.HIVEAP_MODEL_141:
				lanIf.add(model);
				ap300.add(model);
				break;
			case HiveAp.HIVEAP_MODEL_170:
			case HiveAp.HIVEAP_MODEL_320:
			case HiveAp.HIVEAP_MODEL_340:
			case HiveAp.HIVEAP_MODEL_330:
			case HiveAp.HIVEAP_MODEL_350:
			case HiveAp.HIVEAP_MODEL_380:
				if (type==HiveAp.Device_TYPE_BRANCH_ROUTER) {
					getDataSource().setMultiDisplayApOnly(false);
				}
				ap300.add(model);
				break;
			case HiveAp.HIVEAP_MODEL_370:
			case HiveAp.HIVEAP_MODEL_390:
			case HiveAp.HIVEAP_MODEL_230:
				ap300.add(model);
				ap11ac.add(model);
				break;
			case HiveAp.HIVEAP_MODEL_20:
			case HiveAp.HIVEAP_MODEL_28:
				lanIf.add(model);
				ap20.add(model);
				getDataSource().setMultiDisplayRealm(false);
				break;
			}
		}

		if (ap110.size() == models.size()) {
			getDataSource().setHiveApModel(HiveAp.HIVEAP_MODEL_110);
		} else if (br100.size() == models.size()) {
			getDataSource().setHiveApModel(HiveAp.HIVEAP_MODEL_BR100);
		} else if (br200cvg.size() == models.size()) {
			getDataSource().setHiveApModel(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
		} else if (ap11ac.size() == models.size()) {
			getDataSource().setHiveApModel(HiveAp.HIVEAP_MODEL_370);
		} else if (ap300.size() == models.size()) {
			getDataSource().setHiveApModel(HiveAp.HIVEAP_MODEL_340);
		} else if (ap20.size() == models.size()) {
			getDataSource().setHiveApModel(HiveAp.HIVEAP_MODEL_20);
		} else {
			getDataSource().setHiveApModel((short) -1);
		}

		if (lanIf.size() == models.size()) {
			getDataSource().setMultiDisplayType(HiveAp.MULTI_DISPLAY_LAN);
		} else if(usbPortIf.size() + ltePortIf.size() == models.size()) {
			getDataSource().setMultiDisplayType(HiveAp.MULTI_DISPLAY_BR);
			//if (!getDataSource().isMultiIncludeBRAsAp()) {
			getDataSource().setDeviceType(HiveAp.Device_TYPE_BRANCH_ROUTER);
			if(usbPortIf.size() == models.size()) {
				getDataSource().setMultiBRPortUSBDisplay(true);
				getDataSource().setMultiBRPortLTEDisplay(false);
			}
			
			if(ltePortIf.size() == models.size()) {
				getDataSource().setMultiBRPortUSBDisplay(false);
				getDataSource().setMultiBRPortLTEDisplay(true);
			}
			//}
		} else {
			getDataSource().setMultiDisplayType(-1);
		}
		
		if(ap11ac.size() == models.size()){
			getDataSource().setMultiDisplayTxRetry(true);
		}

		int countryCode = getSelectedHiveAPsCountryCode(lstSelectIds);
		getDataSource().setCountryCode(countryCode);
		// set these field to [no change] in dataSource
		getDataSource().setVpnMark((short) -3);
		getDataSource().getEth0().setAdminState((short) -3);
		getDataSource().getEth0().setOperationMode((short) -3);
		getDataSource().getEth0().setDuplex((short) -3);
		getDataSource().getEth0().setSpeed((short) -3);
		getDataSource().getWifi0().setChannel(-3);
		getDataSource().getWifi0().setOperationMode((short) -3);
		getDataSource().getWifi0().setPower(-3);
		getDataSource().getWifi0().setAdminState((short) -3);
		getDataSource().getWifi1().setChannel(-3);
		getDataSource().getWifi1().setOperationMode((short) -3);
		getDataSource().getWifi1().setPower(-3);
		getDataSource().getWifi1().setAdminState((short) -3);
		getDataSource().setChangeIDMAuthProxy(false);	//set to [no change]
	}

	protected void prepareDependentObjectsMultiple(Collection<Long> lstSelectIds) throws Exception {
		// default select wlan policy
		if (isEasyMode()) {
			ConfigTemplate defaultTemplate = HmBeParaUtil
					.getEasyModeDefaultTemplate(domainId);
			if (null == defaultTemplate) {
				defaultTemplate = HmBeParaUtil.getDefaultTemplate();
			}
			ConfigTemplate tmp = QueryUtil.findBoById(ConfigTemplate.class, defaultTemplate.getId(), this);
			getDataSource().setConfigTemplate(tmp);
			configTemplate = defaultTemplate.getId();
		}
		prepareConfigTemplatesMulti();
		prepareCapwapIpForMultiple();
		prepareSchedulers();
		prepareTopologys(true);
		prepareRadioProfiles(MULTI_MODIFY, lstSelectIds);
		prepareManagedUponContact();
		//prepareDeviceInterface();
		preparePreferredSsidOptions();
		removeNoneAvailableItem();
		prepareSupplentalCLI();
		if(this.getDataSource().getHiveApModel()==HiveAp.HIVEAP_MODEL_SR24 
				|| this.getDataSource().getHiveApModel()==HiveAp.HIVEAP_MODEL_SR2124P
				|| this.getDataSource().getHiveApModel()==HiveAp.HIVEAP_MODEL_SR2024P
				|| this.getDataSource().getHiveApModel()==HiveAp.HIVEAP_MODEL_SR2148P
				|| this.getDataSource().getHiveApModel()==HiveAp.HIVEAP_MODEL_SR48){
			prepareSwitchDeviceInterface();
		}else{
			prepareDeviceInterface();
		}

		if (getDataSource().getWifi0RadioProfile() != null
				&& (wifi0RadioProfile == null || wifi0RadioProfile != -3)) {
			wifi0RadioProfile = getDataSource().getWifi0RadioProfile().getId();
		}
		if (getDataSource().getWifi1RadioProfile() != null
				&& (wifi1RadioProfile == null || wifi1RadioProfile != -3)) {
			wifi1RadioProfile = getDataSource().getWifi1RadioProfile().getId();
		}
		if (getDataSource().getConfigTemplate() != null
				&& (configTemplate == null || configTemplate != -3)) {
			configTemplate = getDataSource().getConfigTemplate().getId();
		}
		if (getDataSource().getMapContainer() != null
				&& (topology == null || topology != -3)) {
			topology = getDataSource().getMapContainer().getId();
		}
		if (getDataSource().getCapwapIpBind() != null
				&& (capwapIp == null || capwapIp != -3)) {
			capwapIp = getDataSource().getCapwapIpBind().getId();
			getDataSource().setCapwapText(
					getDataSource().getCapwapIpBind().getAddressName());
		} else {
			if (!strNoChange.equals(getDataSource().getCapwapText())) {
				capwapIp = -1L;
			}
		}
		if (getDataSource().getCapwapBackupIpBind() != null
				&& (capwapBackupIp == null || capwapBackupIp != -3)) {
			capwapBackupIp = getDataSource().getCapwapBackupIpBind().getId();
			getDataSource().setCapwapBackupText(
					getDataSource().getCapwapBackupIpBind().getAddressName());
		} else {
			if (!strNoChange.equals(getDataSource().getCapwapBackupText())) {
				capwapBackupIp = -1L;
			}
		}
		if (getDataSource().getScheduler() != null
				&& (scheduler == null || scheduler != -3)) {
			scheduler = getDataSource().getScheduler().getId();
		}
		
		if(null != getDataSource().getSupplementalCLI()
				&& (supplementalCLIId == null || supplementalCLIId !=-3)){
			supplementalCLIId = getDataSource().getSupplementalCLI().getId();
		}
	}
	
	protected void prepareDeviceTypeChange() throws Exception{
		//device type change from router to others need clear static IP address
		if(this.oldDeviceType == HiveAp.Device_TYPE_BRANCH_ROUTER && 
				this.getDataSource().getDeviceType() != HiveAp.Device_TYPE_BRANCH_ROUTER){
			this.getDataSource().setDhcp(true);
			this.getDataSource().setCfgIpAddress(null);
			this.getDataSource().setCfgNetmask(null);
			this.getDataSource().setCfgGateway(null);
		}
		
		// fix bug 32378
		// don't need clear static route when device type don't chagne
		if(this.oldDeviceType != this.getDataSource().getDeviceType()){
			//device type change between AP and BR, need clear static route.
			this.getDataSource().getIpRoutes().clear();
		}

	}

	private void setDependentObjects() throws Exception{
		setSelectedInterfaces();
		setSelectedObjects();
		setSelectedLearningMac();
		setSelectedNeighbor();
		setSelectedDhcpServer();
		setSelectedPreferredSsids();
		setWifiRadioMode(getDataSource());
		updateStaticRoutes();
		setDeviceInterface();
		setCustomizedNasIdentifier();
		setSelectedStormControl();
		updateDeviceStpSettings();
		SensorModeUtil.setSensorWifiChannel(getDataSource());
	}
	
	private boolean needRefreshWanOrderOnStart = false;
	
	
	public boolean isNeedRefreshWanOrderOnStart() {
		return needRefreshWanOrderOnStart;
	}

	public void setNeedRefreshWanOrderOnStart(boolean needRefreshWanOrderOnStart) {
		this.needRefreshWanOrderOnStart = needRefreshWanOrderOnStart;
	}

	protected void prepareConfigTemplateForTag(){
//		if(getDataSource().getPortTemplate() != null 
//				&& getDataSource().getPortGroup()!=null 
//				&& !getDataSource().getPortTemplate().equals(getDataSource().getPortGroup().getName())){
//			getDataSource().setPortTemplate(getDataSource().getPortGroup().getName());
//			setNeedRefreshWanOrderOnStart(true);
//		}else
			if(getDataSource().isWanPortChanged()){
				setNeedRefreshWanOrderOnStart(true);
			
		}
		
	}

	protected void prepareDependentObjects() throws Exception {
		// default select wlan policy
		if (isEasyMode()) {
			ConfigTemplate defaultTemplate = HmBeParaUtil
					.getEasyModeDefaultTemplate(domainId);
			if (null == defaultTemplate) {
				defaultTemplate = HmBeParaUtil.getDefaultTemplate();
			}
			ConfigTemplate tmp = QueryUtil.findBoById(ConfigTemplate.class, defaultTemplate.getId(), this);
			getDataSource().setConfigTemplate(tmp);
			configTemplate = defaultTemplate.getId();
		}
		if (getDataSource().getConfigTemplateMdm()!= null) {
			configmdmId = getDataSource().getConfigTemplateMdm().getId();
		}
		
		prepareConfigTemplates();
		prepareDefaultDisplayNetworkPolicy();
		prepareCapwapIps();
		preparePppoeAuthProfile();
		prepareSchedulers();
		prepareSupplentalCLI();
		prepareTopologys(true);
		prepareRadioProfiles(SINGLE_MODIFY, null);
		prepareAvailableHiveAps();
		prepareRADIUSServers();
		prepareConfigMdm();
		prepareRADIUSProxys();
		prepareRoutingPolicy();
		prepareUserProfiles();
		prepareDhcpServers();
		prepareCwps();
		prepareRadiusClientProfiles();
		prepareEthMacAddresses();
		prepareEthUserprofileOptions();
		prepareManagedUponContact();
		prepareSsidAllocations();
		prepareSecondVPNGateways();
		prepareRoutingProfiles();
		preparePrimaryVpnGatewayProfiles();
		if(this.getDataSource().getHiveApModel()==HiveAp.HIVEAP_MODEL_SR24 
				|| this.getDataSource().getHiveApModel()==HiveAp.HIVEAP_MODEL_SR2124P
				|| this.getDataSource().getHiveApModel()==HiveAp.HIVEAP_MODEL_SR2024P
				|| this.getDataSource().getHiveApModel()==HiveAp.HIVEAP_MODEL_SR2148P
				|| this.getDataSource().getHiveApModel()==HiveAp.HIVEAP_MODEL_SR48){
			prepareSwitchDeviceInterface();
		}else{
			prepareDeviceInterface();
		}
		prepareUSBSettings();
		prepareDependentSelectObjects();
		prepareMgt0InterfaceSettings();
		preparePreferredSsidOptions();
		prepareForForwardingDB();
		prepareStormControls();
		preparePortChannel();
		prepareSwitchInterface();
		prepareDeviceStpSettings();
		prepareMstpInstanceDataTable();
		prepareMstpInstanceToDataTable();
		prepareAhMulticastGroupDataTableColumnDefs(getDataSource());
		prepareForIgmpError();
		prepareForMulticastGroupError();
		prepareCopyHiveApModeToDeviceInterface();
		
		if (getDataSource().getWifi0RadioProfile() != null
				&& (wifi0RadioProfile == null || wifi0RadioProfile != -3)) {
			wifi0RadioProfile = getDataSource().getWifi0RadioProfile().getId();
		}
		if (getDataSource().getWifi1RadioProfile() != null
				&& (wifi1RadioProfile == null || wifi1RadioProfile != -3)) {
			wifi1RadioProfile = getDataSource().getWifi1RadioProfile().getId();
		}
		if (getDataSource().getConfigTemplate() != null
				&& (configTemplate == null || configTemplate != -3)) {
			configTemplate = getDataSource().getConfigTemplate().getId();
		}
		if (getDataSource().getMapContainer() != null
				&& (topology == null || topology != -3)) {
			topology = getDataSource().getMapContainer().getId();
		}
		if (getDataSource().getRadiusServerProfile() != null) {
			radiusServer = getDataSource().getRadiusServerProfile().getId();
		}
		if (getDataSource().getRadiusProxyProfile() != null) {
			radiusProxy = getDataSource().getRadiusProxyProfile().getId();
		}
		if (getDataSource().getRoutingProfilePolicy() != null) {
			routingPolicyId = getDataSource().getRoutingProfilePolicy().getId();
		}
		if (getDataSource().getCapwapIpBind() != null
				&& (capwapIp == null || capwapIp != -3)) {
			capwapIp = getDataSource().getCapwapIpBind().getId();
			getDataSource().setCapwapText(
					getDataSource().getCapwapIpBind().getAddressName());
		}
		if (getDataSource().getCapwapBackupIpBind() != null
				&& (capwapBackupIp == null || capwapBackupIp != -3)) {
			capwapBackupIp = getDataSource().getCapwapBackupIpBind().getId();
			getDataSource().setCapwapBackupText(
					getDataSource().getCapwapBackupIpBind().getAddressName());
		}
		if (getDataSource().getScheduler() != null
				&& (scheduler == null || scheduler != -3)) {
			scheduler = getDataSource().getScheduler().getId();
		}
		if (getDataSource().getPppoeAuthProfile() != null) {
			pppoeAuthProfile = getDataSource().getPppoeAuthProfile().getId();
		}

		if (null == vpnIpTrackId && getDataSource().getVpnIpTrack() != null) {
			vpnIpTrackId = getDataSource().getVpnIpTrack().getId();
		}
		if (getDataSource().getEth0UserProfile() != null) {
			userProfileEth0 = getDataSource().getEth0UserProfile().getId();
		}
		if (getDataSource().getEth1UserProfile() != null) {
			userProfileEth1 = getDataSource().getEth1UserProfile().getId();
		}
		if (getDataSource().getAgg0UserProfile() != null) {
			userProfileAgg0 = getDataSource().getAgg0UserProfile().getId();
		}
		if (getDataSource().getRed0UserProfile() != null) {
			userProfileRed0 = getDataSource().getRed0UserProfile().getId();
		}
		if (getDataSource().getEthCwpCwpProfile() != null) {
			cwpProfile = getDataSource().getEthCwpCwpProfile().getId();
		}
		if (getDataSource().getEthCwpRadiusClient() != null) {
			ethCwpRadiusClient = getDataSource().getEthCwpRadiusClient()
					.getId();
		}
		if (getDataSource().getEthCwpDefaultAuthUserProfile() != null) {
			ethDefaultAuthUserprofile = getDataSource()
					.getEthCwpDefaultAuthUserProfile().getId();
		}
		if (getDataSource().getEthCwpDefaultRegUserProfile() != null) {
			ethDefaultRegUserprofile = getDataSource()
					.getEthCwpDefaultRegUserProfile().getId();
		}
		if (getDataSource().getSecondVPNGateway() != null){
			this.secondVPNGateway = getDataSource().getSecondVPNGateway().getId();
		}
		if (getDataSource().getRoutingProfile() != null){
			this.routingProfile = getDataSource().getRoutingProfile().getId();
		}
		if(getDataSource().getCvgDPD() != null && getDataSource().getCvgDPD().getMgtNetwork() != null){
			if(getDataSource().getCvgDPD().getMgtNetwork() != null){
				this.cvgMgtNetwork = getDataSource().getCvgDPD().getMgtNetwork().getId();
			}
			if(getDataSource().getCvgDPD().getDnsForCVG() != null){
				this.dnsForCVGId = getDataSource().getCvgDPD().getDnsForCVG().getId();
			}
			if(getDataSource().getCvgDPD().getNtpForCVG() != null){
				this.ntpForCVGId = getDataSource().getCvgDPD().getNtpForCVG().getId();
			}
			if(getDataSource().getCvgDPD().getMgtVlan() != null){
				this.cvgMgtVlan = getDataSource().getCvgDPD().getMgtVlan().getId();
			}
			if(getDataSource().getCvgDPD().getMgmtServiceSyslog() != null){
				this.syslogForCVGId = getDataSource().getCvgDPD().getMgmtServiceSyslog().getId();
			}
			if(getDataSource().getCvgDPD().getMgmtServiceSnmp() != null){
				this.snmpForCVGId = getDataSource().getCvgDPD().getMgmtServiceSnmp().getId();
			}
		}

		vpnGatewayVrrpEnable = getDataSource().isEnableVRRP();
		branchRouterVrrpEnable = getDataSource().isEnableVRRP();
		vpnGatewayVirtualWanIp = getDataSource().getVirtualWanIp();
		branchRouterVirtualWanIp = getDataSource().getVirtualWanIp();
		vpnGatewayVirtualLanIp = getDataSource().getVirtualLanIp();
		branchRouterVirtualLanIp = getDataSource().getVirtualLanIp();
		vpnGatewayPreemptEnable = getDataSource().isEnablePreempt();
		branchRouterPreemptEnable = getDataSource().isEnablePreempt();
		wifi0ModeBR = getDataSource().getWifi0().getOperationMode();
		wifi1ModeBR = getDataSource().getWifi1().getOperationMode();

		getDataSource().setEnabledOverrideRadiusServer(false);
		if (getDataSource().isBranchRouter()) {
			if (getDataSource().isEnabledBrAsRadiusServer()) {
				if (getDataSource().getRadiusServerProfile()!=null || getDataSource().getRadiusProxyProfile()!=null) {
					getDataSource().setEnabledOverrideRadiusServer(true);
				}
			}
		}
		
		if (null != getDataSource().getSupplementalCLI() && (supplementalCLIId == null || supplementalCLIId != -3)){
			supplementalCLIId = getDataSource().getSupplementalCLI().getId();
		}
	}

	private boolean overrideVlan;
	private boolean overrideMgtVlan;

	private boolean overrideVlanId;

	public void setOverrideVlan(boolean overrideVlan) {
		this.overrideVlan = overrideVlan;
	}

	public void setOverrideMgtVlan(boolean overrideMgtVlan) {
		this.overrideMgtVlan = overrideMgtVlan;
	}

	public void setOverrideVlanId(boolean overrideVlanId) {
		this.overrideVlanId = overrideVlanId;
	}

	public boolean getNativeVlanOverride() {
		return null != getDataSource() && getDataSource().getNativeVlan() > 0;
	}

	public String getNativeVlanDisabled() {
		return String.valueOf(!getNativeVlanOverride());
	}

	public boolean getMgtVlanOverride() {
		return null != getDataSource() && getDataSource().getMgtVlan() > 0;
	}

	public String getMgtVlanDisabled() {
		return String.valueOf(!getMgtVlanOverride());
	}

	/* SSID Allocation section */
	private List<HiveApSsidAllocation> wifi0Ssids;
	private List<HiveApSsidAllocation> wifi1Ssids;
	private Set<Long> ssid0Indices;
	private Set<Long> ssid1Indices;

	public List<HiveApSsidAllocation> getWifi0Ssids() {
		return wifi0Ssids;
	}

	public List<HiveApSsidAllocation> getWifi1Ssids() {
		return wifi1Ssids;
	}

	public void setSsid0Indices(Set<Long> ssid0Indices) {
		this.ssid0Indices = ssid0Indices;
	}

	public void setSsid1Indices(Set<Long> ssid1Indices) {
		this.ssid1Indices = ssid1Indices;
	}

	private void prepareSsidAllocations() {
		Long templateId = null;
		if (getDataSource().getConfigTemplate() == null) {
			// try to get the first template from the list;
			if (null != configTemplates && !configTemplates.isEmpty()) {
				templateId = configTemplates.get(0).getId();
			}
		} else {
			templateId = getDataSource().getConfigTemplate().getId();
		}
		if (null != templateId && templateId > 0) {
			ConfigTemplate template = QueryUtil.findBoById(
					ConfigTemplate.class, templateId, this);
			List<HiveApSsidAllocation> disabledSsids = getDataSource()
					.getDisabledSsids();
			wifi0Ssids = new ArrayList<HiveApSsidAllocation>();
			wifi1Ssids = new ArrayList<HiveApSsidAllocation>();
			List<SsidProfile> ssidList_bg = getSsidProfileList(template,
					HiveApSsidAllocation.WIFI2G);
			for (SsidProfile ssidProfile : ssidList_bg) {
				HiveApSsidAllocation hiveApSsid = getSsidAllocation(
						HiveApSsidAllocation.WIFI2G, ssidProfile, disabledSsids);
				wifi0Ssids.add(hiveApSsid);
			}
			List<SsidProfile> ssidList_a = getSsidProfileList(template,
					HiveApSsidAllocation.WIFI5G);
			for (SsidProfile ssidProfile : ssidList_a) {
				HiveApSsidAllocation hiveApSsid = getSsidAllocation(
						HiveApSsidAllocation.WIFI5G, ssidProfile, disabledSsids);
				wifi1Ssids.add(hiveApSsid);
			}
		}
	}

	private List<SsidProfile> getSsidProfileList(ConfigTemplate template,
			short type) {
		List<SsidProfile> list = new ArrayList<SsidProfile>();
		if (null != template) {
			Map<Long, ConfigTemplateSsid> config_ssids = template
					.getSsidInterfaces();
			if (null == config_ssids) {
				return list;
			}
			for (ConfigTemplateSsid config_ssid : config_ssids.values()) {
				SsidProfile ssidProfile = config_ssid.getSsidProfile();
				if (null == ssidProfile) {
					continue;
				}
				int mode = ssidProfile.getRadioMode();
				if (mode == SsidProfile.RADIOMODE_BG) {
					if (type == HiveApSsidAllocation.WIFI2G) {
						list.add(ssidProfile);
					}
				} else if (mode == SsidProfile.RADIOMODE_A) {
					if (type == HiveApSsidAllocation.WIFI5G) {
						list.add(ssidProfile);
					}
				} else {
					list.add(ssidProfile);
				}
			}
		}
		return list;
	}

	private HiveApSsidAllocation getSsidAllocation(short type,
			SsidProfile ssid, List<HiveApSsidAllocation> disabledSsids) {
		HiveApSsidAllocation hiveApSsid = new HiveApSsidAllocation();
		hiveApSsid.setInterType(type);
		hiveApSsid.setSsid(ssid.getId());
		hiveApSsid.setChecked(disabledSsids == null
				|| !disabledSsids.contains(hiveApSsid));
		String ssidName = ssid.getSsidName();
//		if (null != ssidName && ssidName.length() > 12) {
//			hiveApSsid.setSsidName(ssidName.substring(0, 12) + "...");
//			hiveApSsid.setTooltip(ssidName);
//		} else {
//			hiveApSsid.setSsidName(ssidName);
//		}

//		repair the SSID Allocation doesn't display in device page
		if (null != ssidName) {
			hiveApSsid.setSsidName(ssidName);
			hiveApSsid.setTooltip(ssidName);
		} else {
			hiveApSsid.setSsidName(ssidName);
		}
		return hiveApSsid;
	}

	private void setDisabledSsids() {
		if (null == ssid0Indices && null == ssid1Indices) {
			return;
		}
		if (null == getDataSource().getConfigTemplate()) {
			// reset to all ssid are enabled.
			getDataSource().setDisabledSsids(null);
			return;
		}
		ConfigTemplate template = QueryUtil.findBoById(ConfigTemplate.class,
				getDataSource().getConfigTemplate().getId(), this);
		List<HiveApSsidAllocation> list = new ArrayList<HiveApSsidAllocation>();
		// only the admin status is up and operation mode is access will disable
		// specify ssid.
		if (getDataSource().getWifi0().getAdminState() == AhInterface.ADMIN_STATE_UP
				&& (getDataSource().getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS
						|| getDataSource().getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_WAN_ACCESS
						|| getDataSource().getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_DUAL)) {
			List<SsidProfile> ssidList_bg = getSsidProfileList(template,
					HiveApSsidAllocation.WIFI2G);
			for (SsidProfile ssidProfile : ssidList_bg) {
				if (null == ssid0Indices
						|| !ssid0Indices.contains(ssidProfile.getId())) {
					HiveApSsidAllocation hsa = new HiveApSsidAllocation();
					hsa.setSsid(ssidProfile.getId());
					hsa.setInterType(HiveApSsidAllocation.WIFI2G);
					list.add(hsa);
				}
			}
		}
		if (getDataSource().getWifi1().getAdminState() == AhInterface.ADMIN_STATE_UP
				&& (getDataSource().getWifi1().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS
						|| getDataSource().getWifi1().getOperationMode() == AhInterface.OPERATION_MODE_DUAL)) {
			List<SsidProfile> ssidList_a = getSsidProfileList(template,
					HiveApSsidAllocation.WIFI5G);
			for (SsidProfile ssidProfile : ssidList_a) {
				if (null == ssid1Indices
						|| !ssid1Indices.contains(ssidProfile.getId())) {
					HiveApSsidAllocation hsa = new HiveApSsidAllocation();
					hsa.setSsid(ssidProfile.getId());
					hsa.setInterType(HiveApSsidAllocation.WIFI5G);
					list.add(hsa);
				}
			}
		}
		getDataSource().setDisabledSsids(list);
	}

	public static String generateRealmName(Long templateId,Long topologyMapId,boolean lockRealmName,String oldRealmName){
		String realmName = "";
		if(lockRealmName){
			realmName = oldRealmName;
		} else {
			String buildName = "";
			String hiveName = "";
			if(null != topologyMapId){
				MapContainerNode mapContainerNode = QueryUtil.findBoById(
						MapContainerNode.class, topologyMapId, new BonjourGatewayMonitoringAction());
				if(null != mapContainerNode){
					if(mapContainerNode.getMapType() == MapContainerNode.MAP_TYPE_FLOOR){
						if(mapContainerNode.getParentMap() != null){
							MapContainerNode parentMap = mapContainerNode.getParentMap();
							buildName = parentMap.getLabel();
						}
					} else {
						buildName = mapContainerNode.getLabel();
					}
				}
			}

			if (null != templateId) {
				if (templateId > 0) {
					ConfigTemplate template = QueryUtil.findBoById(
							ConfigTemplate.class, templateId, new BonjourGatewayMonitoringAction());
					hiveName = template == null ? "" : template.getHiveProfile().getHiveName();
				}
			}

			if(!"".equals(buildName) && !"".equals(hiveName)){
				realmName = buildName+"_"+hiveName;
			}
		}
		
		return realmName;
	}

	private JSONObject requestRealmName(Long templateId,Long topologyMapId,boolean lockRealmName,String oldRealmName) throws JSONException {
		String realmName = "";
		JSONObject jsonObject = new JSONObject();
		realmName = generateRealmName(templateId,topologyMapId,lockRealmName,oldRealmName);
		ConfigTemplate	 template = QueryUtil.findBoById(ConfigTemplate.class, templateId, this);
	    PortGroupProfile portProfile = this.getDataSource().getPortGroup(template);
	    if(portProfile != null){
	    	jsonObject.put("tmplateId", portProfile.getName());
	    }
		jsonObject.put("realmName", realmName);
		
		return jsonObject;
	}

	private JSONObject getTemplateInfo(Long templateId) throws JSONException {
		boolean isVpn = false;
		JSONObject jsonObject = new JSONObject();
		ConfigTemplate template = null;
		if (null != templateId) {
			if (templateId > 0) {
				 template = QueryUtil.findBoById(
						ConfigTemplate.class, templateId, this);
				JSONArray ssids = getSsidAllocations(template);
				jsonObject.put("ssids", ssids);
				//added for ForwardingDB
				JSONArray interfaces = getForwardingDBInterfaceOptionJson(template);
				jsonObject.put("interfaces", interfaces);
				jsonObject.put("wirelessEnabled", template.getConfigType().isWirelessEnabled());
				jsonObject.put("routerEnabled", template.getConfigType().isRouterEnabled());
				isVpn = template.getVpnService() != null;
			} else if (templateId == -3) {
				isVpn = isMutipleEditHiveAPWithVPN();
			}
		}

		//prepare user profiles filtered by wireless only or wireless+routing
		//TODO: check it for network policy type re-design
		//TODO: please check how to set values here for hiveAp2.jsp
		/*jsonObject.put("wirelessRoutingChged", false);*/
		if (null != templateId && templateId > 0) {
			ConfigTemplate config = isNetworkPolicyModeChanged(templateId, getPreviousNPId());
			if (config != null) {
				/*JSONArray ups = getAllUserProfiles(config.isBlnWirelessRouter());*/
				JSONArray ups = getAllUserProfiles();
				jsonObject.put("userProfiles", ups);
				/*jsonObject.put("toWirelessRouting", config.isBlnWirelessRouter());
				jsonObject.put("wirelessRoutingChged", true);*/
			}
		}
		if (getDataSource().isMultiDisplayApOnly()) {
			jsonObject.put("vpn", isVpn);
		}
		if (null != template ) {
			this.getDataSource().setClassificationTag1(classificationTag1);
			this.getDataSource().setClassificationTag2(classificationTag2);
			this.getDataSource().setClassificationTag3(classificationTag3);
		PortGroupProfile pgp=	this.getDataSource().getPortGroup(template);
		//getIfwanPortTypeMap(pgp);
		//setChangeWans(pgp);
			if(null!=pgp){
				List<Short> ethList=pgp.getPortFinalValuesByPortType(DeviceInfType.Gigabit, PortAccessProfile.PORT_TYPE_WAN);
				if(this.getDataSource().isBRSupportMultiWan() && ethList!=null && ethList.size()>0){
					
					//wanPortNum . change portgroup thould get it through configtemplate +1 is usb
					jsonObject.put("wanPortNum",ethList.size()+2);
//					if(!isBR200WPAsRouter()){
						jsonObject.put("wifi0str", "LAN");
//					}
					if(ethList.contains(Short.valueOf("4"))){
							jsonObject.put("eth0str", "WAN");
						}
						if(ethList.contains(Short.valueOf("5"))){
							jsonObject.put("eth1str", "WAN");
						}else {
							jsonObject.put("eth1str", "LAN");
						}
						if(ethList.contains(Short.valueOf("6"))){
							jsonObject.put("eth2str", "WAN");
						}else {
							jsonObject.put("eth2str", "LAN");
						}
						if(ethList.contains(Short.valueOf("7"))){
							jsonObject.put("eth3str", "WAN");	
						}else {
							jsonObject.put("eth3str", "LAN");	
						}
						if(ethList.contains(Short.valueOf("8"))){
							jsonObject.put("eth4str", "WAN");
						}else{
							jsonObject.put("eth4str", "LAN");
						}
						
				}else{
					jsonObject.put("eth0str", "WAN");
					jsonObject.put("eth1str", "LAN");
					jsonObject.put("eth2str", "LAN");
					jsonObject.put("eth3str", "LAN");	
					jsonObject.put("eth4str", "LAN");
					if((isBR200WPAsRouter() || isBR200LTE()) && getDataSource().getRouterWanInterfaceNum(template)< MAX_WAN_NUM_4AP ){
						jsonObject.put("wifi0str", ROUTER_PORT_WAN);
						jsonObject.put("wanPortNum",this.getDataSource().getRouterWanInterfaceNum(template)+1);
					}else{
						jsonObject.put("wifi0str", ROUTER_PORT_LAN);
						jsonObject.put("wanPortNum",this.getDataSource().getRouterWanInterfaceNum(template));
					}
					
				}
				jsonObject.put("portTemplate", pgp.getName());
				
			}else{
				jsonObject.put("portTemplate", "");
				jsonObject.put("eth0str", "WAN");
				jsonObject.put("eth1str", "LAN");
				jsonObject.put("eth2str", "LAN");
				jsonObject.put("eth3str", "LAN");
				jsonObject.put("eth4str", "LAN");
				jsonObject.put("usbstr", ROUTER_PORT_WAN);
								//wanPortNum 
								if((isBR200WPAsRouter() || isBR200LTE()) && getDataSource().getRouterWanInterfaceNum(template) < MAX_WAN_NUM_4AP){
									jsonObject.put("wifi0str", ROUTER_PORT_WAN);
									jsonObject.put("wanPortNum",this.getDataSource().getRouterWanInterfaceNum(template)+1);
								}else{
									jsonObject.put("wifi0str", ROUTER_PORT_LAN);
									jsonObject.put("wanPortNum",this.getDataSource().getRouterWanInterfaceNum(template));
								}
				//wanPortNum 
				
				
			}
		
		}
		
		return jsonObject;
	}

	private boolean checkPreferredSsids() throws Exception{
		if(!validatePreferredSsids(configTemplate)){
			jsonObject = new JSONObject();
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
			return false;
		}
		return true;
	}

	private boolean  validatePreferredSsids(Long templateId) throws JSONException {
		if(getPreferredSsids() != null && getPreferredSsids().size() > 0){
			if (null != templateId) {
				ConfigTemplate template = QueryUtil.findBoById(
						ConfigTemplate.class, templateId, this);
				Map<Long, ConfigTemplateSsid> config_ssids = template.getSsidInterfaces();
				if (null == config_ssids) {
					return true;
				}

				for (ConfigTemplateSsid config_ssid : config_ssids.values()) {
					SsidProfile ssidProfile = config_ssid.getSsidProfile();
					if (null == ssidProfile) {
						continue;
					}

					for(Long preferredSsid : getPreferredSsids()){
						WifiClientPreferredSsid wpreferredSsid = QueryUtil.findBoById(WifiClientPreferredSsid.class, preferredSsid);
						if(wpreferredSsid.getSsid().equals(ssidProfile.getSsidName())){
							addActionError(MgrUtil.getUserMessage("info.perferredssid.warning.duplicate", wpreferredSsid.getSsid()));
							return false;
						}
					}
				}
			}
		}

		return true;

	}

	private ConfigTemplate isNetworkPolicyModeChanged(Long curNpId, Long preNpId) {
		List<Long> npIds = new ArrayList<Long>();
		npIds.add(curNpId);
		npIds.add(preNpId);
		List<ConfigTemplate> templates = QueryUtil.executeQuery(
				ConfigTemplate.class, null, new FilterParams("id", npIds), domainId);
		if (templates.size() != 2) {
			if (templates.size() == 1) {
				return templates.get(0);
			}
			return null;
		}
		/*if (templates.get(0).isBlnWirelessRouter() != templates.get(1).isBlnWirelessRouter()) {*/
		//TODO: check it for network policy type re-design
		if (!templates.get(0).getConfigType().equals(templates.get(1).getConfigType())) {
			return templates.get(0).getId().equals(curNpId) ? templates.get(0) : templates.get(1);
		}
		return null;
	}

	private JSONArray getAllUserProfiles() throws JSONException {
		JSONArray jsons = new JSONArray();
//		Object objNull = null;
		List<UserProfile> items;
//		if (blnNpWirelessRouting) {
			items = QueryUtil.executeQuery(UserProfile.class, new SortParams("id"),null, domainId);
//		} else {
//			items = QueryUtil.executeQuery(UserProfile.class, new SortParams("id"), new FilterParams("networkObj", objNull), domainId);
//		}
		for (UserProfile userProfile : items) {
			JSONObject object = new JSONObject();
			object.put("id", userProfile.getId());
			object.put("name", userProfile.getUserProfileName());
			jsons.put(object);
		}
		return jsons;
	}

	private JSONObject getCwpInfo(Long cwpId) throws JSONException {
		boolean isEthCwpRadiusClient = false;
		boolean isEthCwpDefaultRegUserProfile = false;
		boolean isEthCwpDefaultAuthUserProfile = false;
		JSONObject jsonObject = new JSONObject();
		if (null != cwpId) {
			if (cwpId > 0) {
				Cwp cwp = QueryUtil.findBoById(Cwp.class, cwpId);
				if (null != cwp) {
					isEthCwpRadiusClient = isConfigureEthCwpRadiusClient(cwp);
					isEthCwpDefaultRegUserProfile = isConfigureEthCwpDefaultRegUserProfile(cwp);
					isEthCwpDefaultAuthUserProfile = isConfigureEthCwpDefaultAuthUserProfile(cwp);
				}
			}
		}
		jsonObject.put("isEthCwpRadiusClient", isEthCwpRadiusClient);
		jsonObject.put("isEthCwpDefaultRegUserProfile",
				isEthCwpDefaultRegUserProfile);
		jsonObject.put("isEthCwpDefaultAuthUserProfile",
				isEthCwpDefaultAuthUserProfile);
		return jsonObject;
	}

	private JSONArray getSsidAllocations(ConfigTemplate template)
			throws JSONException {
		JSONArray jsonArray = new JSONArray();
		List<HiveApSsidAllocation> disabledSsids = null;
		if (null != getDataSource()
				&& null != getDataSource().getConfigTemplate()) {
			Long hiveApTemplateId = getDataSource().getConfigTemplate().getId();
			if (hiveApTemplateId.equals(template.getId())) {
				disabledSsids = getDataSource().getDisabledSsids();
			}
		}
		List<SsidProfile> ssidList_bg = getSsidProfileList(template,
				HiveApSsidAllocation.WIFI2G);
		for (SsidProfile ssidProfile : ssidList_bg) {
			HiveApSsidAllocation ssid = getSsidAllocation(
					HiveApSsidAllocation.WIFI2G, ssidProfile, disabledSsids);
			JSONObject object = new JSONObject();
			object.put("ssid", ssid.getSsid());
			object.put("type", "wifi0");
			object.put("ssidName", ssid.getSsidName());
			object.put("checked", ssid.isChecked());
			object.put("tooltip", ssid.getTooltip() == null ? "" : ssid
					.getTooltip());
			jsonArray.put(object);
		}
		List<SsidProfile> ssidList_a = getSsidProfileList(template,
				HiveApSsidAllocation.WIFI5G);
		for (SsidProfile ssidProfile : ssidList_a) {
			HiveApSsidAllocation ssid = getSsidAllocation(
					HiveApSsidAllocation.WIFI5G, ssidProfile, disabledSsids);
			JSONObject object = new JSONObject();
			object.put("ssid", ssid.getSsid());
			object.put("type", "wifi1");
			object.put("ssidName", ssid.getSsidName());
			object.put("checked", ssid.isChecked());
			object.put("tooltip", ssid.getTooltip() == null ? "" : ssid
					.getTooltip());
			jsonArray.put(object);
		}
		return jsonArray;
	}

	private void prepareSelectedDeviceIdStr() throws Exception {
		List<Long> ids = getSelectedHiveApIds();
		Set<Long> idsSet = new HashSet<Long>();
		idsSet.addAll(ids);
		MgrUtil.setSessionAttribute(HiveApUpdateAction.SIMPLIFIED_UPDATE_SELECTED_IDs, idsSet);
	}

	private JSONObject invokeHiveApImage(String imageType) throws Exception {
		Set<Long> ids = new HashSet<Long>(getSelectedHiveApIds());
		return new MapNodeAction().getMultipleHiveApCliInfo(ids,
				getText("topology.menu.hiveAp.invokeBackup"), imageType);
	}

	private JSONObject syncSGE() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		try {
			HiveApPagingCache hiveApPagingCache = getHiveApListCache();
			hiveApPagingCache.startSyncProgress();
		} catch (Exception e) {
			log.error("syncSGE", "syncSGE error.", e);
			jsonObject.put("error", "<div class='error'>" + e.getMessage()
					+ "</div>");
		}
		return jsonObject;
	}

	private JSONObject fetchSGEProgress() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		HiveApPagingCache hiveApPagingCache = getHiveApListCache();
		SyncProgressEvent event = hiveApPagingCache.getSyncProgressEvent();
		if (null != event) {
			Collection<SyncStage> stages = event.getSyncStages();
			String description = event.getDescription();
			if (null != stages) {
				JSONArray array = new JSONArray();
				for (SyncStage stage : stages) {
					String state;
					if (Status.FINISHED.equals(stage.getStatus())) {
						state = "<span class='finished'>Finished</span>";
					} else if (Status.RUNNING.equals(stage.getStatus())) {
						String icon = "<img align='absmiddle' width='16' src='"
								+ request.getContextPath()
								+ "/images/waitingSquare.gif"
								+ "' class='dinl'>";
						state = "<span class='running'>Running " + icon
								+ "</span>";
					} else {
						state = "<span>&nbsp;</span>";
					}
					array.put(state);
				}
				jsonObject.put("data", array);
			}
			if (null != description) {
				if (event.isSuccDesc()) {
					jsonObject.put("msg", "<div class='normal'>" + description
							+ "</div>");
				} else {
					jsonObject.put("msg", "<div class='error'>" + description
							+ "</div>");
				}
			}
		}
		return jsonObject;
	}

	private String mucDisplay;

	public String getMucDisplay() {
		return mucDisplay;
	}

	private void prepareManagedUponContact() throws Exception {
		/* Always not show it, the attribute in HiveAP is not used. */
		/*-
		if ("manuallyProvisioned".equals(listType)) {
			mucDisplay = "";
		} else {
			mucDisplay = "none";
		}
		 */
		mucDisplay = "none";
	}

	private boolean checkAllUpdateOrCreate() throws Exception {
		if (isHiveApHostNameExist(domainId, getDataSource().getHostName(),
				getDataSource().getId())) {
			errMsgTmp = MgrUtil.getUserMessage("error.hostnameExists",
					getDataSource().getHostName());
			addActionError(errMsgTmp);
			setTabId(0);
			return false;
		}
		if (getDataSource().isWifi1Available()) {
			if (!verifyRadioFailover(getDataSource().getWifi0RadioProfile(),
					getDataSource().getWifi1RadioProfile(), null, getDataSource().getSoftVer())) {
				return false;
			}
		}
		if (!verifyRadioTurboMode(getDataSource().getWifi0RadioProfile(),
				getDataSource().getWifi1().getChannel(), getDataSource()
						.getCountryCode(), getDataSource().getHostName())) {
			return false;
		}
		if (!verifyRadioTurboMode(getDataSource().getWifi1RadioProfile(),
				getDataSource().getWifi1().getChannel(), getDataSource()
						.getCountryCode(), getDataSource().getHostName())) {
			return false;
		}
		if (!verifyInterfaceOperationMode(getDataSource(), false)) {
			return false;
		}
		if (!verifyConfigTemplate(getDataSource(), getDataSource()
				.getConfigTemplate(), null)) {
			return false;
		}
		if (!verify11nInterfaces(getDataSource())) {
			return false;
		}
		if (!verifyVpnServer(getDataSource())) {
			return false;
		}
		if (!verifyVpnClient(getDataSource().getConfigTemplate(),
				getDataSource())) {
			return false;
		}
		if (!verifyUserUserGroupCount(getDataSource(), getDataSource()
				.getConfigTemplate(), getDataSource().getRadiusServerProfile(),
				null)) {
			return false;
		}
		if (!verifyDhcpServerSettings()) {
			return false;
		}
		if (!verifyRealSimulateHiveAp(getDataSource().getMapContainer(),
				getDataSource())) {
			return false;
		}
		if (!verifyPresenceEnabledDeviceCount(Arrays.asList(getDataSource()),
				getDataSource().getWifi0RadioProfile(), getDataSource()
						.getWifi1RadioProfile())) {
			return false;
		}
		return true;
	}

	private void setSelectedInterfaces() throws Exception {
		if (null == dataSource) {
			return;
		}
		switch (getDataSource().getRadioConfigType()) {
		case HiveAp.RADIO_MODE_ACCESS_ALL:
			getDataSource().getWifi0().setOperationMode(
					AhInterface.OPERATION_MODE_ACCESS);
			getDataSource().getWifi1().setOperationMode(
					AhInterface.OPERATION_MODE_ACCESS);
			break;
		case HiveAp.RADIO_MODE_ACCESS_ONE:
			if (getDataSource().isWifi1Available()) {
				getDataSource().getWifi0().setOperationMode(
						AhInterface.OPERATION_MODE_ACCESS);
				getDataSource().getWifi1().setOperationMode(
						AhInterface.OPERATION_MODE_BACKHAUL);
			}else{
				getDataSource().getWifi0().setOperationMode(
						AhInterface.OPERATION_MODE_BACKHAUL);
				getDataSource().getWifi1().setOperationMode(
						AhInterface.OPERATION_MODE_BACKHAUL);
			}
			break;
		case HiveAp.RADIO_MODE_ACCESS_DUAL:
			if (getDataSource().isWifi1Available()) {
				getDataSource().getWifi0().setOperationMode(
						AhInterface.OPERATION_MODE_ACCESS);
				getDataSource().getWifi1().setOperationMode(
						AhInterface.OPERATION_MODE_DUAL);
			}else{
				getDataSource().getWifi0().setOperationMode(
						AhInterface.OPERATION_MODE_DUAL);
				getDataSource().getWifi1().setOperationMode(
						AhInterface.OPERATION_MODE_DUAL);
			}

//			if(!getDataSource().isWifi1Available()){
//				RadioProfile profile = QueryUtil.findBoById(RadioProfile.class,
//						wifi0RadioProfile);
//				if(profile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
//						|| profile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA){
//					getDataSource().getWifi0().setOperationMode(
//							AhInterface.OPERATION_MODE_DUAL);
//				}else{
//					getDataSource().getWifi0().setOperationMode(
//							AhInterface.OPERATION_MODE_ACCESS);
//				}
//			}else{
//				getDataSource().getWifi0().setOperationMode(
//						AhInterface.OPERATION_MODE_ACCESS);
//				getDataSource().getWifi1().setOperationMode(
//						AhInterface.OPERATION_MODE_DUAL);
//			}
			break;
		case HiveAp.RADIO_MODE_BRIDGE:
			if (getDataSource().isWifi1Available()) {
				getDataSource().getWifi0().setOperationMode(
						AhInterface.OPERATION_MODE_ACCESS);
				getDataSource().getWifi1().setOperationMode(
						AhInterface.OPERATION_MODE_BACKHAUL);
				getDataSource().getEth0().setBindInterface(
						AhInterface.ETH_BIND_IF_NULL);
				getDataSource().getEth1().setBindInterface(
						AhInterface.ETH_BIND_IF_NULL);
			} else {
				getDataSource().getWifi0().setOperationMode(
						AhInterface.OPERATION_MODE_BACKHAUL);
				getDataSource().getWifi1().setOperationMode(
						AhInterface.OPERATION_MODE_BACKHAUL);
				getDataSource().getEth0().setBindInterface(
						AhInterface.ETH_BIND_IF_NULL);
				getDataSource().getEth1().setBindInterface(
						AhInterface.ETH_BIND_IF_NULL);
			}
			break;
		case HiveAp.RADIO_MODE_ACCESS_WAN:
			getDataSource().getWifi0().setOperationMode(
					AhInterface.OPERATION_MODE_WAN_ACCESS);
			break;
		case HiveAp.RADIO_MODE_CUSTOMIZE:
			//deal with BR100 as AP
			if(getDataSource().isBranchRouter() || getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
				if (getDataSource().isWifi1Available()) {
					getDataSource().getWifi0().setOperationMode(
							this.wifi0ModeBR);
					getDataSource().getWifi1().setOperationMode(
							this.wifi1ModeBR);
				} else {
					getDataSource().getWifi0().setOperationMode(
							this.wifi0ModeBR);
				}
			}
		}
		switch (getDataSource().getEthConfigType()) {
		case HiveAp.USE_ETHERNET_BOTH:
			getDataSource().getEth0().setBindInterface(
					AhInterface.ETH_BIND_IF_NULL);
			getDataSource().getEth1().setBindInterface(
					AhInterface.ETH_BIND_IF_NULL);
			break;
		case HiveAp.USE_ETHERNET_AGG0:
			getDataSource().getEth0().setBindInterface(
					AhInterface.ETH_BIND_IF_AGG0);
			getDataSource().getEth1().setBindInterface(
					AhInterface.ETH_BIND_IF_AGG0);
			break;
		case HiveAp.USE_ETHERNET_RED0:
			getDataSource().getEth0().setBindInterface(
					AhInterface.ETH_BIND_IF_RED0);
			getDataSource().getEth1().setBindInterface(
					AhInterface.ETH_BIND_IF_RED0);
			break;
		}

		//CVG BR100 use for AP eth0 mode always backhaul.
		if(getDataSource().getDeviceType() == HiveAp.Device_TYPE_HIVEAP &&
				(getDataSource().isCVGAppliance() ||
				getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100)){
			getDataSource().getEth0().setOperationMode(AhInterface.OPERATION_MODE_BACKHAUL);
		}
	}

	private void setSelectedLearningMac() throws Exception {
		if (null == dataSource) {
			return;
		}
		List<HiveApLearningMac> list = getDataSource().getLearningMacs();
		list.clear();
		if (null != eth0Maces) {// eth0
			if (getDataSource().getEthConfigType() == HiveAp.USE_ETHERNET_BOTH) {
				list.addAll(getLearningObjects(eth0Maces,
						HiveApLearningMac.LEARNING_MAC_ETH0));
			}
		}
		if (null != eth1Maces) {// eth1
			if (getDataSource().isWifi1Available()
					&& getDataSource().getEthConfigType() == HiveAp.USE_ETHERNET_BOTH) {
				list.addAll(getLearningObjects(eth1Maces,
						HiveApLearningMac.LEARNING_MAC_ETH1));
			}
		}
		if (null != agg0Maces) {// agg0
			if (getDataSource().getEthConfigType() == HiveAp.USE_ETHERNET_AGG0) {
				list.addAll(getLearningObjects(agg0Maces,
						HiveApLearningMac.LEARNING_MAC_AGG0));
			}
		}
		if (null != red0Maces) {// red0
			if (getDataSource().getEthConfigType() == HiveAp.USE_ETHERNET_RED0) {
				list.addAll(getLearningObjects(red0Maces,
						HiveApLearningMac.LEARNING_MAC_RED0));
			}
		}
	}

	private List<HiveApLearningMac> getLearningObjects(List<Long> selectedMacs,
			short learningType) throws Exception {
		List<HiveApLearningMac> list = new ArrayList<HiveApLearningMac>();
		if (null != selectedMacs) {
			for (Long macId : selectedMacs) {
				MacOrOui bo = QueryUtil.findBoById(MacOrOui.class, macId);
				if (null != bo) {
					HiveApLearningMac lm = new HiveApLearningMac();
					lm.setLearningMacType(learningType);
					lm.setMac(bo);
					list.add(lm);
				}
			}
		}
		return list;
	}

	protected void setSelectedObjects() throws Exception {
		setSelectedWifi0RadioProfile();
		setSelectedWifi1RadioProfile();
		setSelectedConfigTemplate();
		setSelectedTopology();
		setSelectedRADIUSServer();
		setSelectedMdm();
		setSelectedRADIUSProxy();
		setSelectedRoutingPolicy();
		setSelectedCapwapIp();
		setSelectedScheduler();
		setSelectedPppoeAuthProfile();
		setSelectedIpTrack();
		setSelectedMacLearningObjects();
		setDisabledSsids();
		setSelectedEthCwpSettings();
		setSelectedRoutingProfile();
		setSelectedSecondVPNGateway();
		setUSBSettings();
		setDataSourceVPNMark();
		setSelectedCvgDepend();
		setSelectedSuppCLI();
	}

	private void setupInitialUpdateList() throws Exception {
		Set<Long> initialList = new HashSet<Long>();
		if (null != leafNodeId) {
			// For selection from map canvas
			MapLeafNode leafNode = QueryUtil.findBoById(MapLeafNode.class,
					leafNodeId, this);
			if (null != leafNode) {
				HiveAp hiveAp = leafNode.getHiveAp();
				if (null != hiveAp) {
					initialList.add(hiveAp.getId());
				}
			}
		} else {
			// For selection from HiveAP ManagedList
			List<Long> selection = getSelectedHiveApIds();
			if (null != selection) {
				initialList.addAll(selection);
			}
		}
		MgrUtil.setSessionAttribute(HiveApUpdateAction.UPDATE_INITIAL_IDs,
				initialList);
	}

	// private int[] multiEditCountryCode = { -1, -1 };

	/*
	 * set country code to the common dataSource when multiple edit
	 */
	private int getSelectedHiveAPsCountryCode(List<Long> selectedIds) {
		List<?> countryCodeList = QueryUtil.executeQuery(
				"select countryCode,hiveApModel from " + boClass.getSimpleName(), null,
				new FilterParams("id", selectedIds));
		boolean isSame5GChannels = true;
		boolean isSame24GChannels = true;
		int previousCode = -1;
		for (Object object : countryCodeList) {
			Object[] oneObj = (Object[]) object;
			Integer code = Integer.parseInt(oneObj[0].toString());
			short apModel = Short.parseShort(oneObj[1].toString());
			if (previousCode > 0) {
				boolean sameChannel = CountryCode.isSameChannelList_5GHz(
						previousCode, code,apModel);
				if (!sameChannel) {
					// countryCodeList has different values for a/na mode;
					isSame5GChannels = false;
					break;
				}
			}
			previousCode = code;
		}
		previousCode = -1;
		for (Object obj : countryCodeList) {
			Object[] oneObj = (Object[]) obj;
			Integer code = Integer.parseInt(oneObj[0].toString());
//			Integer code = (Integer) obj;
			if (previousCode > 0) {
				boolean sameChannel = CountryCode.isSameChannelList_2_4GHz(
						previousCode, code);
				if (!sameChannel) {
					// countryCodeList has different values for bg/ng mode;
					isSame24GChannels = false;
					break;
				}
			}
			previousCode = code;
		}
		return (isSame24GChannels && isSame5GChannels) ? previousCode : -1;
	}

	@Override
	public HiveAp getDataSource() {
		if (null != dataSource && dataSource instanceof HiveAp) {
			return (HiveAp) dataSource;
		}
		return null;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setDataSource(HiveAp.class);
		enableSorting();
		// default sorting
		if (sortParams.getOrderBy().equals("id")) {
			sortParams.setPrimaryOrderBy("manageStatus");
			sortParams.setPrimaryAscending(true);
		}
		keyColumnId = COLUMN_HOSTNAME;
	}

	protected void updateSortParams() {
		super.updateSortParams();
		sortParams.setPrimaryOrderBy("manageStatus");
		sortParams.setPrimaryAscending(true);
	}
	
	public String getWifi0Style() {
		if (this.getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_WP
				&& HiveAp.Device_TYPE_BRANCH_ROUTER == getDataSource()
						.getDeviceType()) {
			return "";
		} else {
			return "none";
		}
	}

	public EnumItem[] getEnumAdminStateType() {
		return AhInterface.ADMIN_STATE_TYPE;
	}

	public EnumItem[] getEnumConnectionType1() {
		return AhInterface.CONNECTION_TYPE1;
	}
	
	public EnumItem[] getEnumConnectionType2() {
		return AhInterface.CONNECTION_TYPE2;
	}
	
	public EnumItem[] getEnumUsbRoleType() {
		return AhInterface.ROLE_TYPE;
	}

	public EnumItem[] getEnumRoleType() {
		return AhInterface.ROLE_TYPE_WAN_LAN;
	}
	
	private Collection<DeviceInterface> prepareInterfaceList(){
		Collection<DeviceInterface> dInfSet=new ArrayList<DeviceInterface>();
		dInfSet.add(branchRouterEth0);
		dInfSet.add(branchRouterEth1);
		dInfSet.add(branchRouterEth2);
		dInfSet.add(branchRouterEth3);
		dInfSet.add(branchRouterEth4);
		dInfSet.add(branchRouterUSB);
		dInfSet.add(branchRouterWifi0);
		dInfSet.add(branchRouterWifi1);
		return dInfSet;
	}
	
	Collection<DeviceInterface> myDInfSet=null;
	
	private Collection<DeviceInterface> prepareMyDInfList(){
		
		if(myDInfSet!=null){
			return myDInfSet;
		}
		Collection<DeviceInterface> dInfSet=new ArrayList<DeviceInterface>();
		
		DeviceInterface	myEth0 = new DeviceInterface();
		myEth0.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth0"));
		myEth0.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH0);
//			branchRouterEth0.setRole(AhInterface.ROLE_PRIMARY);

		DeviceInterface	myEth1 = new DeviceInterface();
		myEth1.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth1"));
		myEth1.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH1);
		myEth1.setPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH1);

		DeviceInterface	myEth2 = new DeviceInterface();
		myEth2.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth2"));
		myEth2.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH2);


		DeviceInterface	myEth3 = new DeviceInterface();
		myEth3.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth3"));
		myEth3.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH3);

		DeviceInterface myEth4 = new DeviceInterface();
		myEth4.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth4"));
		myEth4.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH4);

		DeviceInterface	myUSB = new DeviceInterface();
		myUSB.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.usb"));
		myUSB.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_USB);

			DeviceInterface	myWifi0 = new DeviceInterface();
			myWifi0.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.wifi0"));
			myWifi0.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_WIFI0);

			DeviceInterface	myWifi1 = new DeviceInterface();
			myWifi1.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.wifi1"));
			myWifi1.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_WIFI1);
			
			dInfSet.add(myEth0);
			dInfSet.add(myEth1);
			dInfSet.add(myEth2);
			dInfSet.add(myEth3);
			dInfSet.add(myEth4);
			dInfSet.add(myUSB);
			dInfSet.add(myWifi0);
			dInfSet.add(myWifi1);
			return dInfSet;
	}
	
	public JSONObject getWanPortNumJSON(HiveAp hiveAp)
	throws JSONException {
		Collection<JSONObject> resList = new ArrayList<JSONObject>();
		JSONObject jsonObj;
		EnumItem[] wanEnumList=getEnumPriorityLevelType();
			jsonObj = new JSONObject();
			jsonObj.put("wanNum", wanEnumList.length);
		return jsonObj;
		}

	public EnumItem[] getEnumPriorityLevelType(){
		int wanNums;
		if(isBR200WPAsRouter() && getDataSource().getRouterWanInterfaceNum()< MAX_WAN_NUM_4AP && getDataSource().getRadioConfigType() == 6){
			wanNums=getDataSource().getRouterWanInterfaceNum()+1;
		}else{
			wanNums=getDataSource().getRouterWanInterfaceNum();
		}
//		int wanNums=getDataSource().getRouterWanInterfaceNum();
		int[] wanArrays=new int[wanNums];
		for(int i=0;i<wanNums;i++){
			wanArrays[i]=i+1;
		}
//		getDataSource().setWanOrder();
		return MgrUtil.enumItems(
				"enum.interface.priority.", wanArrays);
//		return AhInterface.PRIORITY_LEVEL_TYPE;
	}
	public EnumItem[] getEnumPrioritySwitchType(){
		if(null != switchWanPortSettings && !switchWanPortSettings.isEmpty()){
			int[] wanArrays=new int[switchWanPortSettings.size()];
			for(int i=0;i<switchWanPortSettings.size();i++){
				wanArrays[i]=i+1;
			}
			return MgrUtil.enumItems(
					"enum.interface.priority.", wanArrays);
		}else{
			return null;
		}
		
	}
	
	public EnumItem[] getEnumPriorityUsbLevelType(){
		return getEnumPriorityLevelType();
//		int wanNums=getDataSource().getWanInterfaceNum(prepareInterfaceList());
//		int[] wanArrays=new int[wanNums];
//		for(int i=0;i<wanNums;i++){
//			wanArrays[i]=i+1;
//		}
////		getDataSource().setWanOrder();
//		return MgrUtil.enumItems(
//				"enum.interface.priority.", wanArrays);
////		return AhInterface.PRIORITY_USB_LEVEL_TYPE;
	}
	
	public EnumItem[] getEnumPriorityEth0LevelType(){
		return getEnumPriorityLevelType();
//		int wanNums=getDataSource().getWanInterfaceNum(prepareInterfaceList());
//		int[] wanArrays=new int[wanNums];
//		for(int i=0;i<wanNums;i++){
//			wanArrays[i]=i+1;
//		}
////		getDataSource().setWanOrder();
//		return MgrUtil.enumItems(
//				"enum.interface.priority.", wanArrays);
////		return AhInterface.PRIORITY_ETH0_LEVEL_TYPE;
	}
	
	/**
	 * get channel list for wifi0
	 *
	 * @return an array of EnumItems
	 * @author Joseph Chen
	 * @since 05/23/2008
	 */
	public EnumItem[] getWifi0Channel() {
		if (null == wifi0ChannelList) {
			wifi0ChannelList = CountryCode.getChannelList_2_4GHz(
					getDataSource().getCountryCode(),
					RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20);
		}
		return MgrUtil.enumItems("enum.interface.channel.", wifi0ChannelList);
	}

	/**
	 * get channel list for wifi1
	 *
	 * @return an array of EnumItems
	 * @author Joseph Chen
	 * @since 05/23/2008
	 */
	public EnumItem[] getWifi1Channel() {
		if (null == wifi1ChannelList) {
			wifi1ChannelList = CountryCode.getChannelList_5GHz(getDataSource()
					.getCountryCode(),
					RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20, false, false,getDataSource().getHiveApModel());
		}
		return MgrUtil.enumItems("enum.interface.channel.", wifi1ChannelList);
	}

	public EnumItem[] getEnumWifiOperationMode() {
		String softver = this.getDataSource().getSoftVer();
		if (softver != null && !"".equals(softver)
				&& NmsUtil.compareSoftwareVersion("4.0.1.0", softver) > 0) {
			return MgrUtil.enumItems("enum.interface.operationMode.", new int[] {
					AhInterface.OPERATION_MODE_ACCESS,
					AhInterface.OPERATION_MODE_BACKHAUL,
					AhInterface.OPERATION_MODE_WAN});
		} else {
			if (softver != null && !"".equals(softver)
					&& NmsUtil.compareSoftwareVersion("5.1.2.0", softver) > 0) {
				return MgrUtil.enumItems("enum.interface.operationMode.",
						new int[] { AhInterface.OPERATION_MODE_ACCESS,
								AhInterface.OPERATION_MODE_BACKHAUL,
								AhInterface.OPERATION_MODE_DUAL });
			} else {
				short apMode=getDataSource().getHiveApModel();
				if(SensorModeUtil.isSupportSensorAp(apMode)){
					return MgrUtil.enumItems("enum.interface.operationMode.",
							new int[] { AhInterface.OPERATION_MODE_ACCESS,
									AhInterface.OPERATION_MODE_BACKHAUL,
									AhInterface.OPERATION_MODE_DUAL,
									AhInterface.OPERATION_MODE_SENSOR });
				}else if(SensorModeUtil.isSupportSensorBr(apMode)){
				    return MgrUtil.enumItems("enum.interface.operationMode.",
							new int[] { AhInterface.OPERATION_MODE_ACCESS,
									AhInterface.OPERATION_MODE_SENSOR });
				 }else{
					return MgrUtil.enumItems("enum.interface.operationMode.",
							new int[] { AhInterface.OPERATION_MODE_ACCESS,
									AhInterface.OPERATION_MODE_BACKHAUL,
									AhInterface.OPERATION_MODE_DUAL});
				}
				
			}
		}
	}

	public EnumItem[] getEnumEthOperationMode() {
		return MgrUtil.enumItems("enum.interface.eth.operationMode.",
				new int[] { AhInterface.OPERATION_MODE_ACCESS,
						AhInterface.OPERATION_MODE_BACKHAUL,
						AhInterface.OPERATION_MODE_BRIDGE });
	}

	public EnumItem[] getEnumRedOperationMode() {
//		return MgrUtil.enumItems("enum.interface.eth.operationMode.",
//				new int[] { AhInterface.OPERATION_MODE_ACCESS,
//						AhInterface.OPERATION_MODE_BACKHAUL,
//						AhInterface.OPERATION_MODE_BRIDGE });
		return MgrUtil.enumItems("enum.interface.eth.operationMode.",
				new int[] { AhInterface.OPERATION_MODE_BACKHAUL });
	}

	public EnumItem[] getEnumPowerType() {
		return AhInterface.POWER_TYPE;
	}

	public EnumItem[] getEnumSpeedType() {
		return AhInterface.ETH_SPEED_TYPE;
	}

	public EnumItem[] getEnumDuplexType() {
		return AhInterface.ETH_DUPLEX_TYPE;
	}

	public EnumItem[] getEnumPseType() {
		//Fix bug 27293, BR200_LTE_VZ not support the 802.3at
		if(getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ){
			return AhInterface.ETH_PSE_WITHOUT_AT_TYPE_ONLY_TYPE;
		}
		
		return AhInterface.ETH_PSE_TYPE_ONLY_TYPE;
	}

//	public EnumItem[] getEnumPsePowerThreshold() {
//		return MgrUtil.enumItems(
//				"enum.interface.eth.pse.power.", new int[] {
//						AhInterface.PSE_POWER_THRESHOLD_CLASSBASE, AhInterface.PSE_POWER_THRESHOLD_USERDEFINE });
//	}

	public EnumItem[] getEnumBindInterface() {
		return AhInterface.ETH_BIND_IF;
	}

	public EnumItem[] getEnumBindRole() {
		return AhInterface.ETH_BIND_ROLE;
	}

	public EnumItem[] getEnumFlowCtlType(){
		return AhInterface.FLOW_CONTROL_TYPE;
	}

	public EnumItem[] getEnumStaticRouteIfType() {
		if (getDataSource().is11nHiveAP()) {
			int ethCounts = this.getDataSource().getDeviceInfo().getIntegerValue(DeviceInfo.SPT_ETHERNET_COUNTS);
			
			if (getDataSource().isWifi1Available()) {
				if(ethCounts > 1){
					return HiveApStaticRoute.STATIC_ROUTE_IF_TYPE_DUAL;
				}else{
					return HiveApStaticRoute.STATIC_ROUTE_IF_TYPE;
				}
			} else {
				return HiveApStaticRoute.STATIC_ROUTE_IF_TYPE_SINGLE;
			}
		} else {
			return HiveApStaticRoute.STATIC_ROUTE_IF_TYPE;
		}
	}

	public EnumItem[] getEnumStaticRoute() {
		return HiveApStaticRoute.STATIC_ROUTE_IF_TYPE;
	}

	public EnumItem[] getEnumStaticRouteDual() {
		return HiveApStaticRoute.STATIC_ROUTE_IF_TYPE_DUAL;
	}

	public EnumItem[] getEnumStaticRouteSingle() {
		return HiveApStaticRoute.STATIC_ROUTE_IF_TYPE_SINGLE;
	}

	public EnumItem[] getEnumTunnelThresholdType() {
		return HiveAp.TUNNEL_THRESHOLD_TYPE;
	}

	public EnumItem[] getEnumMetricType() {
		return HiveAp.METRIC_TYPE;
	}

	public EnumItem[] getEnumVPNMarkType() {
		return HiveAp.VPN_MARK;
	}

	public EnumItem[] getEnumVPNMarkWithoutServerType() {
		return HiveAp.VPN_MARK_WITHOUT_SERVER;
	}

	public EnumItem[] getEnumInterfaceType() {
		return EnumConstUtil.ENUM_INTERFACE_TYPE;
	}

	public EnumItem[] getEnumFilters() {
		return new EnumItem[1];
	}

	public EnumItem[] getEnumRadioChannelWidth() {
		return RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH;
	}

	public EnumItem[] getEnumRadioMode() {
		return RadioProfile.ENUM_RADIO_PROFILE_MODE;
	}

	public EnumItem[] getEnumHiveApType() {
		return HiveAp.HIVEAP_TYPE;
	}

	public EnumItem[] getEnumHiveApModel() {
		return NmsUtil.filterHiveAPModel(HiveAp.HIVEAP_MODEL, this.isEasyMode());
	}

	public EnumItem[] getEnumHiveDeviceType() {
		return HiveAp.DEVICE_TYPE;
	}
	
	public EnumItem[] getEnumHiveFilterDeviceType() {
		return HiveAp.DEVICE_TYPE_USED_4_FILTER;
	}

	public EnumItem[] getEnumAuthMethod() {
		return Cwp.ENUM_AUTH_METHOD;
	}

	public EnumItem[] getEnumDenyAction() {
		return SsidProfile.DENY_ACTION;
	}

	public EnumItem[] getEnumDistributedPriority(){
		return HiveAp.DISTRIBUTED_PRIORITY;
	}

	public int getGridCount() {
		return getDataSource().getStaticRoutes().size() < 3 ? (3 - getDataSource()
				.getStaticRoutes().size())
				: 0;
	}

	public int getVirtualConnectGridCount() {
		return getDataSource().getVirtualConnections().size() < 3 ? (3 - getDataSource()
				.getVirtualConnections().size())
				: 0;
	}

	public int getIpRouteCount(){
		return getDataSource().getIpRoutes().size() < 3 ? (3 - getDataSource()
				.getIpRoutes().size())
				: 0;
	}

	public int getHostNameLength() {
		return getAttributeLength("hostName");
	}

	public int getMacAddressLength() {
		return getAttributeLength("macAddress");
	}

	public int getCfgIpAddressLength() {
		return getAttributeLength("cfgIpAddress");
	}

	public int getCfgNetmaskLength() {
		return getAttributeLength("cfgNetmask");
	}

	public int getCfgGatewayLength() {
		return getAttributeLength("cfgGateway");
	}

	public int getAdminUserLength() {
		return getAttributeLength("adminUser");
	}

	public int getReadOnlyUserLength() {
		return getAttributeLength("readOnlyUser");
	}

	public int getCfgAdminUserLength() {
		return getAttributeLength("cfgAdminUser");
	}

	public int getCfgReadOnlyUserLength() {
		return getAttributeLength("cfgReadOnlyUser");
	}

	public Range getMetricIntervalRange() {
		return getAttributeRange("metricInteval");
	}

	public int getClassificationTag1Length() {
		return getAttributeLength("classificationTag1");
	}

	public int getClassificationTag2Length() {
		return getAttributeLength("classificationTag2");
	}

	public int getClassificationTag3Length() {
		return getAttributeLength("classificationTag3");
	}

	public int getRadioProfileNameLength() {
		return HmBoBase.DEFAULT_STRING_LENGTH;
	}

	public int getRadioProfileCommentLength() {
		return HmBoBase.DEFAULT_DESCRIPTION_LENGTH;
	}

	public String getMGListWriteDisabled(){
		if(this.isEasyMode() && ("managedHiveAps".equals(hmListType) || "managedHiveAps".equals(listType))){
			return "disabled";
		}else{
			return this.getWriteDisabled();
		}
	}

	public String getWifi2GSsidAllocationStyle() {
		if (getDataSource().isWifi1Available()) {
			return "";
		}
		RadioProfile radioProfile = getDataSource().getWifi0RadioProfile();
		if (null != radioProfile
				&& (radioProfile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_BG || radioProfile
						.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG)
				&& getDataSource().getRadioConfigType() != HiveAp.RADIO_MODE_ACCESS_ONE) {
			return "";
		}
		return "none";
	}

	public String getWifi5GSsidAllocationStyle() {
		if (getDataSource().isWifi1Available()) {
			return "";
		}
		RadioProfile radioProfile = getDataSource().getWifi0RadioProfile();
		if (null != radioProfile
				&& (radioProfile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A || radioProfile
						.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA || radioProfile
						.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC)
				&& getDataSource().getRadioConfigType() != HiveAp.RADIO_MODE_ACCESS_ONE) {
			return "";
		}
		return "none";
	}

	public String getCustomizeWifi1VisibilityStyle() {
		if (getDataSource().isWifi1Available()) {
			return "visible";
		} else {
			return "hidden";
		}
	}

//	public String getRadioConfigType2Style() {
//		if (getDataSource().isWifi1Available()) {
//			return "";
//		} else {
//			return "none";
//		}
//	}

	public String getRadioConfigType2NoteStyle() {
		if (getDataSource().getRadioConfigType() == HiveAp.RADIO_MODE_ACCESS_ONE) {
			return "";
		} else {
			return "none";
		}
	}

	public String getNetworkSettingStyle() {
		if (getDataSource().isDhcp() && !getDataSource().isDhcpFallback()) {
			return "true";
		} else {
			return "false";
		}
	}

	public String getGatewayVisibilityStyle() {
		if (getMgt0NetworkType() != HiveAp.USE_DHCP_WITHOUTFALLBACK) {
			return "";
		} else {
			return "none";
		}
	}

	public String getDhcpTimeoutVisibilityStyle() {
		if (getMgt0NetworkType() != HiveAp.USE_STATIC_IP) {
			return "";
		} else {
			return "none";
		}
	}

	public String getAddressOnlyVisibilityStyle() {
		if (getMgt0NetworkType() != HiveAp.USE_STATIC_IP) {
			return "";
		} else {
			return "none";
		}
	}

	public String getDhcpFallbackStyle() {
		if (getDataSource().isDhcp()) {
			return "false";
		} else {
			return "true";
		}
	}

	public String getEth1StuffStyle() {
		if (getDataSource().isEth1Available()) {
			return "";
		} else {
			return "none";
		}
	}

	public String getWifi1StuffStyle() {
		if (getDataSource().isWifi1Available()) {
			return "";
		} else {
			return "none";
		}
	}

	public String getVpnRuleStyle() {
		if (null != getDataSource().getConfigTemplate()
				&& getDataSource().getDeviceType() == HiveAp.Device_TYPE_HIVEAP
				&& null != getDataSource().getConfigTemplate().getVpnService()
				&& getDataSource().getConfigTemplate().getConfigType().isWirelessEnabled()
				&& !getDataSource().getConfigTemplate().getConfigType().isRouterEnabled()) {
			return "";
		} else {
			return "none";
		}
	}

	public String getStormControlDetailTrStyle(){
		if(null != getDataSource() && getDataSource().isEnableOverrideStormControl()){
			return "";
		}
		return "none";
	}

	/* for HiveAP multiple edit page */
	public String getIpRouteStyleOfMulti() {
		if (getDataSource().isChangeLayer3Route()) {
			return "";
		}
		return "none";
	}

	public String getVpnRuleStyleOfMulti() {
		if (getDataSource().isMultiIncludeCvg()) {
			return "none";
		}
		if (!getDataSource().isMultiDisplayApOnly()){
			return "none";
		}
		if (null == getDataSource().getConfigTemplate()) {
			// without no selection, check if all select HiveAP
			// configured a WLAN policy with VPN settings
			boolean allConfigured = isMutipleEditHiveAPWithVPN();
			return allConfigured ? "" : "none";
		} else {
			return getVpnRuleStyle();
		}
	}

	public String getDisplayUsbPortInterface(){
		if (getDataSource().isMultiIncludeBRAsAp() || !getDataSource().isMultiBRPortUSBDisplay()) {
			return "none";
		}
		return "";
	}
	
	public String getDisplayLTEPortInterface(){
		if (getDataSource().isMultiIncludeBRAsAp() || !getDataSource().isMultiBRPortLTEDisplay()) {
			return "none";
		}
		return "";
	}

	public String getFullModeConfigStyle() {
		if (isFullMode()) {
			return "";
		} else {
			return "none";
		}
	}

	public String getDisplayConfigTemplate(){
		if (isFullMode()) {
			if (getDataSource().isMultiIncludeCvg()) {
				return "none";
			}
			return "";
		} else {
			return "none";
		}
	}

	public String getDisplayVlan(){
		if (getDataSource().isMultiDisplayApOnly()) {
			return "";
		}
		return "none";
	}
	public String getDisplayGateWay(){
		if (getDataSource().isMultiDisplayApOnly()) {
			return "";
		}
		return "none";
	}
	public String getDisplayRoutering(){
		if (getDataSource().isMultiDisplayApOnly()) {
			return getFullModeConfigStyle();
		}
		return "none";
	}

	public String getDisplayDistributedPriority(){
		if (getDataSource().isMultiIncludeBRAsAp()) {
			return "none";
		}
		if (getDataSource().isMultiDisplayApOnly()) {
			return getFullModeConfigStyle();
		}
		return "none";
	}

	public String getDisplayAdvanceSetting(){
		if (getDataSource().isMultiIncludeCvg()) {
			return "none";
		}
		return "";
	}

	public String getDisplayDeviceBonjourGatewayConfig(){
		if (getDataSource().isMultiDisplayRealm()) {
			return "";
		}
		return "none";
	}
	
	public String getDisplayTxRetryRateConfig(){
		if (getDataSource().isMultiDisplayTxRetry()) {
			return "";
		}
		return "none";
	}
	
	
	public String getTxRetryShowStyle(){
		if (getTxRetryShowFlag()) {
			return "";
		}
		return "none";
	}
	
	public boolean getTxRetryShowFlag(){
		if (getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_370
				|| getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_390) {
			return true;
		}
		
		return false;
	}


	public String getVirtualConnectDisplay() {
		if (this.getDataSource().getDeviceType() != HiveAp.Device_TYPE_VPN_GATEWAY && isFullMode() && !getIs20HiveAP()) {
			return "";
		} else {
			return "none";
		}
	}

	//add by nxma
	public String getMultipleVlanDisplay() {
		if(getDataSource()!=null){
			if (this.getDataSource().getDeviceType() == HiveAp.Device_TYPE_HIVEAP && isFullMode()
					&& getDataSource().getHiveApModel()!=HiveAp.HIVEAP_MODEL_20
					&& getDataSource().getHiveApModel()!=HiveAp.HIVEAP_MODEL_28
					&& getDataSource().getHiveApModel()!=HiveAp.HIVEAP_MODEL_110
					&& getDataSource().getHiveApModel()!=HiveAp.HIVEAP_MODEL_120
					&& getDataSource().getHiveApModel()!=HiveAp.HIVEAP_MODEL_121
					&& getDataSource().getHiveApModel()!=HiveAp.HIVEAP_MODEL_141
					&& getDataSource().getHiveApModel()!=HiveAp.HIVEAP_MODEL_170
					&& getDataSource().getEth1().getOperationMode()==AhInterface.OPERATION_MODE_BACKHAUL) {
				return "";
			} else {
				return "none";
			}
		}else{
			if (isFullMode() && !getIs20HiveAP()) {
				return "";
			} else {
				return "none";
			}
		}
	}
	public String getEth0MacLearningStyle() {
		if (null != getDataSource().getEth0() &&
				getDataSource().getEth0().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL) {
			return "";
		}
		return "none";
	}

	public String getEth0MacLearningEnabledStyle() {
		if (null != getDataSource().getEth0()
				&& !getDataSource().getEth0().isMacLearningEnabled()) {
			return "none";
		}
		return "";
	}

	public String getEth1MacLearningStyle() {
		if (null != getDataSource().getEth1() &&
				getDataSource().getEth1().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL) {
			return "";
		}
		return "none";
	}

	public String getEth1MacLearningEnabledStyle() {
		if (null != getDataSource().getEth1()
				&& !getDataSource().getEth1().isMacLearningEnabled()) {
			return "none";
		}
		return "";
	}

	public String getAgg0MacLearningStyle() {
		if (null != getDataSource().getAgg0() &&
				getDataSource().getAgg0().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL) {
			return "";
		}
		return "none";
	}

	public String getAgg0MacLearningEnabledStyle() {
		if (null != getDataSource().getAgg0()
				&& !getDataSource().getAgg0().isMacLearningEnabled()) {
			return "none";
		}
		return "";
	}

	public String getRed0MacLearningStyle() {
		if (null != getDataSource().getRed0() &&
				getDataSource().getRed0().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL) {
			return "";
		}
		return "none";
	}

	public String getRed0MacLearningEnabledStyle() {
		if (null != getDataSource().getRed0()
				&& !getDataSource().getRed0().isMacLearningEnabled()) {
			return "none";
		}
		return "";
	}

	public String getAuditSchedulerStyle() {
		return isHMOnline() ? "none" : "";
	}

	public String getEthCwpActionTimeDisabled() {
		if (null != getDataSource()
				&& getDataSource().getEthCwpDenyAction() == SsidProfile.DENY_ACTION_BAN) {
			return "false";
		}
		return "true";
	}

	public boolean getConfigureEthCwpRadiusClient() {
		return null != getDataSource() && isConfigureEthCwpRadiusClient(getDataSource()
				.getEthCwpCwpProfile());
	}

	public boolean getConfigureEthCwpDefaultRegUserProfile() {
		return null != getDataSource() && isConfigureEthCwpDefaultRegUserProfile(getDataSource()
				.getEthCwpCwpProfile());
	}

	public boolean getConfigureEthCwpDefaultAuthUserProfile() {
		return null != getDataSource() && isConfigureEthCwpDefaultAuthUserProfile(getDataSource()
				.getEthCwpCwpProfile());
	}

	// indicate the wifi0 profile section is under edit, not create
	public boolean getWifi0RadioProfileNameDisabled() {
		return getDataSource() != null
				&& getDataSource().getTempWifi0RadioProfile().getId() != null;
	}

	// indicate the wifi1 profile section is under edit, not create
	public boolean getWifi1RadioProfileNameDisabled() {
		return getDataSource() != null
				&& getDataSource().getTempWifi1RadioProfile().getId() != null;
	}

	public String getWifi0RadioProfileApplyDisabled() {
		return getDataSource() != null
				&& getDataSource().getTempWifi0RadioProfile().getId() != null
				&& getDataSource().getTempWifi0RadioProfile().getDefaultFlag() ? "disabled"
				: "";
	}

	public String getWifi1RadioProfileApplyDisabled() {
		return getDataSource() != null
				&& getDataSource().getTempWifi1RadioProfile().getId() != null
				&& getDataSource().getTempWifi1RadioProfile().getDefaultFlag() ? "disabled"
				: "";
	}

	public String getWifi0RadioProfileChannelStyle() {
		return getDataSource() != null
				&& (getDataSource().getTempWifi0RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG || getDataSource()
						.getTempWifi0RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA || getDataSource()
						.getTempWifi0RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC) ? ""
				: "none";
	}

	public String getWifi1RadioProfileChannelStyle() {
		return getDataSource() != null
				&& (getDataSource().getTempWifi1RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG || getDataSource()
						.getTempWifi1RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA || getDataSource()
						.getTempWifi1RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC) ? ""
				: "none";
	}
	
	public boolean isMacAuthDependOnCWP(){
		//Note that this is only for AP330/AP350 and version > 6.1.1.0.
		short apModel = getDataSource().getHiveApModel();
		boolean noDepend = false;
		
		if(apModel == HiveAp.HIVEAP_MODEL_330 || apModel == HiveAp.HIVEAP_MODEL_350){
			noDepend = NmsUtil.compareSoftwareVersion(getDataSource().getSoftVer(), "6.1.1.0") >= 0;
		}else if(apModel == HiveAp.HIVEAP_MODEL_230 ){
			noDepend = true;
		}
		
		return !noDepend;
	}

	public boolean isHostBasedDependOnAuthEnabled(){
		//Note that this is only for AP330/AP350 and version > 6.1.3.0.
		boolean support = getDataSource().getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_MULTIPLE_HOST);
		return !support;
	}
	public String getEthCwpMacAuthSettingDisplayStyle() {
		if(isMacAuthDependOnCWP()){
			if (null != getDataSource() && getDataSource().isEthCwpEnableEthCwp()) {
				return "";
			}
			return "none";
		}else{
			return "";
		}
	}

	public String getEthCwpRadiusContentSettingDisplayStyle() {
		if (null != getDataSource()
				&& getDataSource().isEthCwpEnableEthCwp()
				&& (getDataSource().getEthCwpCwpProfile() != null || getDataSource()
						.isEthCwpEnableMacAuth())) {
			return "";
		}
		if(null != getDataSource() && getDataSource().isEthCwpEnableMacAuth()){
			return "";
		}
		return "none";
	}

	public String getEthCwpRadiusSelectionDisplayStyle() {
		if (null != getDataSource()
				&& (getDataSource().isEthCwpEnableMacAuth() || isConfigureEthCwpRadiusClient(getDataSource()
						.getEthCwpCwpProfile()))) {
			return "";
		}
		return "none";
	}

	public String getEthCwpUserProfileSelectionDisplayStyle() {
		return getEthCwpMacAuthSettingDisplayStyle();
	}

	public String getEthCwpUserProfileAuthLabelDisplayStyle() {
		if (null != getDataSource()
				&& (getDataSource().isEthCwpEnableMacAuth() || isConfigureEthCwpRadiusClient(getDataSource()
						.getEthCwpCwpProfile()))) {
			return "";
		}
		return "none";
	}

	public String getEthCwpUserProfileDefaultLabelDisplayStyle() {
		return getEthCwpUserProfileAuthLabelDisplayStyle().equals("") ? "none" : "";
	}

	public String getEthCwpUserProfileRegDisplayStyle() {
		if (null != getDataSource()
				&& isConfigureEthCwpDefaultRegUserProfile(getDataSource()
						.getEthCwpCwpProfile())) {
			return "";
		}
		return "none";
	}

	public String getEthCwpUserProfileAuthDisplayStyle() {
		if (null != getDataSource()
				&& (getDataSource().isEthCwpEnableMacAuth() || isConfigureEthCwpDefaultAuthUserProfile(getDataSource()
						.getEthCwpCwpProfile()))) {
			return "";
		}
		return "none";
	}

	public String getEthCwpUserProfilesDisplayStyle() {
		return getEthCwpRadiusSelectionDisplayStyle();
	}

	public String getEthCwpUserProifleParamsDisplayStyle() {
		return getEthCwpRadiusSelectionDisplayStyle();
	}

	public String getHostnameRange() {
		return MgrUtil.getUserMessage("hiveAp.hostName.range");
	}

	public String getAllowedVlanTitle() {
		return MgrUtil.getUserMessage("hiveAp.if.allowedVlan.note");
	}

	public String getMultiNativeVlanTitle() {
		return MgrUtil.getUserMessage("hiveAp.if.multinativeVlan.note");
	}

	public String getNativeVlanTitle() {
		return MgrUtil.getUserMessage("hiveAp.if.nativeVlan.note");
	}

	public String getLocationRange() {
		return MgrUtil.getUserMessage("hiveAp.location.range");
	}

	public String getVlanRange() {
		String tip = MgrUtil.getUserMessage("hiveAp.nativeVlan.tip.express");
		return isEasyMode() ? tip : "";
	}

	public String getMgtVlanRange() {
		String tip = MgrUtil.getUserMessage("hiveAp.mgtVlan.tip.express");
		return isEasyMode() ? tip : "";
	}

	public String getDhcpTimeoutRange() {
		return MgrUtil.getUserMessage("hiveAp.dhcpTimeout.range");
	}

	public String getDefaultIpPrefixFormat() {
		return MgrUtil.getUserMessage("hiveAp.default.ipAddress.note");
	}

	public String getDefaultNetmaskFormat() {
		return MgrUtil.getUserMessage("hiveAp.default.netmask.note");
	}

	public String getDebounceTimerRange() {
		return MgrUtil
				.getUserMessage("hiveAp.switch.port.settings.column.debounceTimer.note");
	}

	private Long wifi0RadioProfile;

	private Long wifi1RadioProfile;

	private List<CheckItem> wifi0RadioProfiles;

	private List<CheckItem> wifi1RadioProfiles;

	private List<CheckItem> configTemplates;

	private Long configTemplate;
	
	//this id is only used for device function change
	private Long tempConfigTemplate;
	
	public Long getTempConfigTemplate() {
		return tempConfigTemplate;
	}

	public void setTempConfigTemplate(Long tempConfigTemplate) {
		this.tempConfigTemplate = tempConfigTemplate;
	}

	private String portTemplate;

	public String getPortTemplate() {
		return portTemplate;
	}

	public void setPortTemplate(String portTemplate) {
		this.portTemplate = portTemplate;
	}

	private Long topologyMapId;

	private List<CheckItem> cwps;

	private List<CheckItem> capwapIps;

	private List<CheckItem> capwapIpsWithNoChange;

	private Long capwapIp;

	private Long capwapBackupIp;

	private List<CheckItem> schedulers;

	private Long scheduler;

	private List<CheckItem> pppoeAuthProfiles;

	private Long pppoeAuthProfile;

	private List<CheckItem> topologys;

	private Long topology;

	private List<String> includedNeighbors;

	private List<String> excludedNeighbors;

	private List<CheckItem> radiusServers;
	
	private List<CheckItem> configMdmList;
	
	private Long supplementalCLIId;

	public List<CheckItem> getConfigMdmList() {
		return configMdmList;
	}

	public void setConfigMdmList(List<CheckItem> configMdmList) {
		this.configMdmList = configMdmList;
	}

	private Long radiusServer;

	private List<CheckItem> radiusProxys;

	private Long radiusProxy;

	private List<CheckItem> list_routingPolicy;

	private Long routingPolicyId;

	private List<CheckItem> userProfiles;

	private Long userProfileEth0;

	private Long userProfileEth1;

	private Long userProfileAgg0;

	private Long userProfileRed0;

	private OptionsTransfer dhcpServerOptions;

	private List<Long> dhcpServers;

	private Long dhcpServer;

	private OptionsTransfer eth0MacOptions;

	private OptionsTransfer eth1MacOptions;

	private OptionsTransfer agg0MacOptions;

	private OptionsTransfer red0MacOptions;

	private List<Long> eth0Maces;

	private List<Long> eth1Maces;

	private List<Long> agg0Maces;

	private List<Long> red0Maces;

	private Long learningMacId;

	private boolean expanding_dynamic;

	private boolean expanding_static;

	private boolean expanding_ip;

	private boolean expanding_vlanid;

	private boolean expanding_virtualConnect;

	private boolean expanding_staticRoutes;

	private boolean expanding_intNetwork;

	private String wifi0RadioModeLabel;

	private String wifi1RadioModeLabel;

	private String wifi0Label = MgrUtil.getUserMessage("hiveAp.if.24G");

	private String wifi1Label = MgrUtil.getUserMessage("hiveAp.if.5G");

	private int[] wifi0ChannelList;

	private int[] wifi1ChannelList;

	private short apModelType;

	private short wifi0OperationMode;

	private short wifi1OperationMode;

	private Long cwpProfile;

	private Long ethCwpRadiusClient;

	private List<CheckItem> radiusClientProfiles;

	private Long ethDefaultAuthUserprofile;

	private Long ethDefaultRegUserprofile;

	private Long ethUserProfileId;

	private List<Long> ethUserProfiles;

	private OptionsTransfer ethUserprofileOptions;

	private OptionsTransfer preferredSsidOptions;

	private Long preferredSsid;

	private List<Long> preferredSsids;

	private Long secondVPNGateway;

	private List<CheckItem> secondVPNGateways;

	private Long routingProfile;

	private List<CheckItem> routingProfiles;

	private short mgt0NetworkType = HiveAp.USE_STATIC_IP;

	// new or edit radius profile and radius proxy
	// check if the device type is BR
	private boolean newEditRadiusForBrFlg;

	private String radioPsePriority;
	
	private boolean lockRealmName;
	
	private String oldRealmName;
	
	private Integer insertTopologyInfo;
	
	private String enableMdmTag;
	
	public String getEnableMdmTag() {
		return enableMdmTag;
	}

	public void setEnableMdmTag(String enableMdmTag) {
		this.enableMdmTag = enableMdmTag;
	}

	public boolean isLockRealmName() {
		return lockRealmName;
	}

	public String getOldRealmName() {
		return oldRealmName;
	}

	public void setLockRealmName(boolean lockRealmName) {
		this.lockRealmName = lockRealmName;
	}

	public void setOldRealmName(String oldRealmName) {
		this.oldRealmName = oldRealmName;
	}

	public void setInsertTopologyInfo(Integer insertTopologyInfo) {
		this.insertTopologyInfo = insertTopologyInfo;
	}

	public Integer getInsertTopologyInfo() {
		return insertTopologyInfo;
	}

	public boolean isExpanding_virtualConnect() {
		return expanding_virtualConnect;
	}

	public void setExpanding_virtualConnect(boolean expandingVirtualConnect) {
		expanding_virtualConnect = expandingVirtualConnect;
	}

	public boolean isExpanding_staticRoutes() {
		return expanding_staticRoutes;
	}

	public void setExpanding_staticRoutes(boolean expanding_staticRoutes) {
		this.expanding_staticRoutes = expanding_staticRoutes;
	}

	public boolean isExpanding_intNetwork() {
		return expanding_intNetwork;
	}

	public void setExpanding_intNetwork(boolean expanding_intNetwork) {
		this.expanding_intNetwork = expanding_intNetwork;
	}

	public OptionsTransfer getEthUserprofileOptions() {
		return ethUserprofileOptions;
	}

	public List<CheckItem> getRadiusClientProfiles() {
		return radiusClientProfiles;
	}

	public List<Long> getEthUserProfiles() {
		return ethUserProfiles;
	}

	public void setEthUserProfiles(List<Long> ethUserProfiles) {
		this.ethUserProfiles = ethUserProfiles;
	}

	public Long getEthDefaultAuthUserprofile() {
		return ethDefaultAuthUserprofile;
	}

	public void setEthDefaultAuthUserprofile(Long ethDefaultAuthUserprofile) {
		this.ethDefaultAuthUserprofile = ethDefaultAuthUserprofile;
	}

	public Long getEthDefaultRegUserprofile() {
		return ethDefaultRegUserprofile;
	}

	public void setEthDefaultRegUserprofile(Long ethDefaultRegUserprofile) {
		this.ethDefaultRegUserprofile = ethDefaultRegUserprofile;
	}

	public Long getEthCwpRadiusClient() {
		return ethCwpRadiusClient;
	}

	public void setEthCwpRadiusClient(Long ethCwpRadiusClient) {
		this.ethCwpRadiusClient = ethCwpRadiusClient;
	}

	public Long getCwpProfile() {
		return cwpProfile;
	}

	public void setCwpProfile(Long cwpProfile) {
		this.cwpProfile = cwpProfile;
	}

	public void setWifi0OperationMode(short wifi0OperationMode) {
		this.wifi0OperationMode = wifi0OperationMode;
	}

	public void setWifi1OperationMode(short wifi1OperationMode) {
		this.wifi1OperationMode = wifi1OperationMode;
	}

	public void setApModelType(short apModelType) {
		this.apModelType = apModelType;
	}

	public String getWifi0RadioModeLabel() {
		if (null == wifi0RadioModeLabel) {
			wifi0RadioModeLabel = MgrUtil
					.getEnumString("enum.radioProfileMode."
							+ RadioProfile.RADIO_PROFILE_MODE_BG);
		}
		return wifi0RadioModeLabel;
	}

	public String getWifi1RadioModeLabel() {
		if (null == wifi1RadioModeLabel) {
			wifi1RadioModeLabel = MgrUtil
					.getEnumString("enum.radioProfileMode."
							+ RadioProfile.RADIO_PROFILE_MODE_A);
		}
		return wifi1RadioModeLabel;
	}

	public String getWifi0RadioModeLabel_multiple() {
		if (null == wifi0RadioModeLabel) {
			wifi0RadioModeLabel = strNoChange;
		}
		return wifi0RadioModeLabel;
	}

	public String getWifi1RadioModeLabel_multiple() {
		if (null == wifi1RadioModeLabel) {
			wifi1RadioModeLabel = strNoChange;
		}
		return wifi1RadioModeLabel;
	}

	public String getWifi0Label() {
		return wifi0Label;
	}

	public String getWifi1Label() {
		return wifi1Label;
	}

	public Long getDhcpServer() {
		return dhcpServer;
	}

	public void setDhcpServer(Long dhcpServer) {
		this.dhcpServer = dhcpServer;
	}

	public Long getWifi0RadioProfile() {
		return wifi0RadioProfile;
	}

	public void setWifi0RadioProfile(Long wifi0RadioProfile) {
		this.wifi0RadioProfile = wifi0RadioProfile;
	}

	public Long getWifi1RadioProfile() {
		return wifi1RadioProfile;
	}

	public void setWifi1RadioProfile(Long wifi1RadioProfile) {
		this.wifi1RadioProfile = wifi1RadioProfile;
	}

	public List<CheckItem> getWifi0RadioProfiles() {
		return wifi0RadioProfiles;
	}

	public List<CheckItem> getWifi1RadioProfiles() {
		return wifi1RadioProfiles;
	}

	public List<CheckItem> getConfigTemplates() {
		return configTemplates;
	}

	public Long getTopologyMapId() {
		return topologyMapId;
	}

	public void setTopologyMapId(Long topologyMapId) {
		this.topologyMapId = topologyMapId;
	}

	public List<CheckItem> getCwps() {
		return cwps;
	}

	public List<CheckItem> getTopologys() {
		return topologys;
	}

	public List<CheckItem> getCapwapIps() {
		return capwapIps;
	}

	public List<CheckItem> getCapwapIpsWithNoChange() {
		return capwapIpsWithNoChange;
	}

	public List<CheckItem> getSchedulers() {
		return schedulers;
	}

	public Long getConfigTemplate() {
		return configTemplate;
	}

	public void setConfigTemplate(Long configTemplate) {
		this.configTemplate = configTemplate;
	}

	public Long getTopology() {
		return topology;
	}

	public void setTopology(Long topology) {
		this.topology = topology;
	}

	public void setIncludedNeighbors(List<String> includedNeighbors) {
		this.includedNeighbors = includedNeighbors;
	}

	public void setExcludedNeighbors(List<String> excludedNeighbors) {
		this.excludedNeighbors = excludedNeighbors;
	}

	public Long getRadiusServer() {
		return radiusServer;
	}

	public void setRadiusServer(Long radiusServer) {
		this.radiusServer = radiusServer;
	}

	public Long getRadiusProxy() {
		return radiusProxy;
	}

	public void setRadiusProxy(Long radiusProxy) {
		this.radiusProxy = radiusProxy;
	}

	public Long getUserProfileEth0() {
		return userProfileEth0;
	}

	public void setUserProfileEth0(Long userProfileEth0) {
		this.userProfileEth0 = userProfileEth0;
	}

	public Long getUserProfileEth1() {
		return userProfileEth1;
	}

	public void setUserProfileEth1(Long userProfileEth1) {
		this.userProfileEth1 = userProfileEth1;
	}

	public Long getUserProfileAgg0() {
		return userProfileAgg0;
	}

	public void setUserProfileAgg0(Long userProfileAgg0) {
		this.userProfileAgg0 = userProfileAgg0;
	}

	public Long getUserProfileRed0() {
		return userProfileRed0;
	}

	public void setUserProfileRed0(Long userProfileRed0) {
		this.userProfileRed0 = userProfileRed0;
	}

	public boolean isExpanding_dynamic() {
		return expanding_dynamic;
	}

	public void setExpanding_dynamic(boolean expanding_dynamic) {
		this.expanding_dynamic = expanding_dynamic;
	}

	public boolean isExpanding_static() {
		return expanding_static;
	}

	public void setExpanding_static(boolean expanding_static) {
		this.expanding_static = expanding_static;
	}

	public boolean isExpanding_ip() {
		return expanding_ip;
	}

	public void setExpanding_ip(boolean expanding_ip) {
		this.expanding_ip = expanding_ip;
	}

	public boolean isExpanding_vlanid() {
		return expanding_vlanid;
	}

	public void setExpanding_vlanid(boolean expanding_vlanid) {
		this.expanding_vlanid = expanding_vlanid;
	}

	public List<CheckItem> getRadiusServers() {
		return radiusServers;
	}

	public List<CheckItem> getRadiusProxys() {
		return radiusProxys;
	}

	public List<CheckItem> getUserProfiles() {
		return userProfiles;
	}

	public OptionsTransfer getDhcpServerOptions() {
		return dhcpServerOptions;
	}

	public List<Long> getDhcpServers() {
		return dhcpServers;
	}

	public void setDhcpServers(List<Long> dhcpServers) {
		this.dhcpServers = dhcpServers;
	}

	public Long getCapwapIp() {
		return capwapIp;
	}

	public void setCapwapIp(Long capwapIp) {
		this.capwapIp = capwapIp;
	}

	public Long getCapwapBackupIp() {
		return capwapBackupIp;
	}

	public void setCapwapBackupIp(Long capwapBackupIp) {
		this.capwapBackupIp = capwapBackupIp;
	}

	public Long getScheduler() {
		return scheduler;
	}

	public void setScheduler(Long scheduler) {
		this.scheduler = scheduler;
	}

	public List<Long> getEth0Maces() {
		return eth0Maces;
	}

	public void setEth0Maces(List<Long> eth0Maces) {
		this.eth0Maces = eth0Maces;
	}

	public List<Long> getEth1Maces() {
		return eth1Maces;
	}

	public void setEth1Maces(List<Long> eth1Maces) {
		this.eth1Maces = eth1Maces;
	}

	public List<Long> getAgg0Maces() {
		return agg0Maces;
	}

	public void setAgg0Maces(List<Long> agg0Maces) {
		this.agg0Maces = agg0Maces;
	}

	public List<Long> getRed0Maces() {
		return red0Maces;
	}

	public void setRed0Maces(List<Long> red0Maces) {
		this.red0Maces = red0Maces;
	}

	public Long getLearningMacId() {
		return learningMacId;
	}

	public void setLearningMacId(Long learningMacId) {
		this.learningMacId = learningMacId;
	}

	public OptionsTransfer getEth0MacOptions() {
		return eth0MacOptions;
	}

	public OptionsTransfer getEth1MacOptions() {
		return eth1MacOptions;
	}

	public OptionsTransfer getAgg0MacOptions() {
		return agg0MacOptions;
	}

	public OptionsTransfer getRed0MacOptions() {
		return red0MacOptions;
	}

	public EnumItem[] getApModel() {
		return NmsUtil.filterHiveAPModel(HiveAp.HIVEAP_MODEL, this.isEasyMode());
	}

	public EnumItem[] getDeviceTypeList(){
		return HiveAp.DEVICE_TYPE;
	}

	public EnumItem[] getVirtualConnectActions() {
		return HiveAPVirtualConnection.VIRTUALCONNECT_ACTIONS;
	}

//	public EnumItem[] getMultipleVlanActions() {
//		return HiveApMultipleVlan.MultipleVlan_ACTIONS;
//	}

	public EnumItem[] getVirtualConnectInterfaces() {
		return HiveAPVirtualConnection.VIRTUALCONNECT_INTERFACES;
	}

	public String getEnableModel() {
		if (null != getDataSource() && null != getDataSource().getId()) {
			return "true";
		} else {
			return "false";
		}
	}

	protected void prepareInitialParameters() {
		if (null != getDataSource()) {
			String softVer = NmsUtil.getHiveOSVersion(versionInfo);
			getDataSource().setManageStatus(HiveAp.STATUS_PRECONFIG);
			getDataSource().setOrigin(HiveAp.ORIGIN_CREATE);
			getDataSource().setSoftVer(softVer);
			getDataSource().setDisplayVer(getText("monitor.hiveAp.DisplayVer", 
					new String[]{getDataSource().getSoftVerString()}));
			String listTypeFromSession = (String) MgrUtil
					.getSessionAttribute(HM_LIST_TYPE);
			if("managedVPNGateways".equals(listTypeFromSession)){
				getDataSource().setDeviceType(HiveAp.Device_TYPE_VPN_GATEWAY);
			}else if("managedRouters".equals(listTypeFromSession)){
				getDataSource().setDeviceType(HiveAp.Device_TYPE_BRANCH_ROUTER);
			}else if("managedSwitches".equals(listTypeFromSession)){
				getDataSource().setDeviceType(HiveAp.Device_TYPE_SWITCH);
			}else if("managedDeviceAPs".equals(listTypeFromSession)){
				getDataSource().setDeviceType(HiveAp.Device_TYPE_HIVEAP);
			}

			//init bonjour priority
			getDataSource().setPriority(HiveAp.getDefaultBonjourPriority(getDataSource().getHiveApModel()));

			//init pse maxpowerbudget
			getDataSource().setMaxpowerBudget(HiveAp.getDefaultMaxPowerBudget(getDataSource().getHiveApModel()));
		}
	}

	protected void prepareCapwapIps() {
		this.capwapIps = getIpObjectsByIpAndName();
	}

	protected void prepareCapwapIpForMultiple() {
		List<CheckItem> capwapIps = getIpObjectsByIpAndName();
		capwapIps.add(0, new CheckItem(-3L, strNoChange));
		this.capwapIpsWithNoChange = capwapIps;
	}

	protected void preparePppoeAuthProfile() {
		this.pppoeAuthProfiles = getBoCheckItems("pppoeName", PPPoE.class,
				null, CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	protected void prepareSchedulers() {
		this.schedulers = getBoCheckItems("schedulerName", Scheduler.class,
				null, CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	private void removeNoneAvailableItem() {
		if (null != schedulers) {
			for (CheckItem item : schedulers) {
				if (item.getId() == CHECK_ITEM_ID_NONE
						&& item
								.getValue()
								.equals(
										MgrUtil
												.getUserMessage("config.optionsTransfer.none"))) {
					schedulers.remove(item);
					break;
				}
			}
		}
		if (null != topologys) {
			for (CheckItem item : topologys) {
				if (item.getId() == CHECK_ITEM_ID_NONE
						&& item
								.getValue()
								.equals(
										MgrUtil
												.getUserMessage("config.optionsTransfer.none"))) {
					topologys.remove(item);
					break;
				}
			}
		}
	}

	protected void prepareCwps() {
		this.cwps = getBoCheckItems("cwpName", Cwp.class, new FilterParams("idmSelfReg", false),
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	protected void prepareRadiusClientProfiles() {
		this.radiusClientProfiles = getBoCheckItems("radiusName",
				RadiusAssignment.class, null);
	}

	/*
	 * The list is dynamic base on device function
	 */
	protected void prepareConfigTemplates() {
		this.configTemplates = getConfigTemplatesByDeviceFunction(getDataSource());
	}
	
	/*
	 * The list is dynamic base on device function for multi
	 */
	protected void prepareConfigTemplatesMulti() {
		SortParams sortParams = new SortParams("configName");
//		sortParams.setPrimaryOrderBy("defaultFlag");
		sortParams.setPrimaryAscending(false);
		
		// fix bug 27541
		String where = "defaultFlag != :s1";

		configTemplates = getBoCheckItems("configName",
					ConfigTemplate.class, new FilterParams(where, new Object[]{true}),
					sortParams);
		if (configTemplates==null) {
			configTemplates = new ArrayList<CheckItem>();
		}
		if (configTemplates.isEmpty()) {
			configTemplates.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
	}

	protected void prepareDefaultDisplayNetworkPolicy() {
		if (getDataSource() == null
				|| getDataSource().getConfigTemplate() == null) {
			if (configTemplates != null && !configTemplates.isEmpty()) {
				defaultDisplayNetworkPolicyId = configTemplates.get(0).getId();
			}
		} else {
			Long selectedId = getDataSource().getConfigTemplate().getId();
			// check whether the selected id inside the available template list
			boolean contains = false;
			for (CheckItem item : configTemplates) {
				if (selectedId.equals(item.getId())) {
					contains = true;
					break;
				}
			}
			if (contains) {
				defaultDisplayNetworkPolicyId = getDataSource()
						.getConfigTemplate().getId();
			} else {
				getDataSource().setConfigTemplate(null);
				if (configTemplates != null && !configTemplates.isEmpty()) {
					defaultDisplayNetworkPolicyId = configTemplates.get(0)
							.getId();
				}
			}
		}
	}

	private List<CheckItem> getConfigTemplatesByDeviceFunction(HiveAp device){
		List<CheckItem> configTemplates = null;
		if (null ==device) {
			configTemplates = new ArrayList<CheckItem>();
			configTemplates.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
			return configTemplates;
		}
		SortParams sortParams = new SortParams("configName");
		sortParams.setPrimaryOrderBy("defaultFlag");
		sortParams.setPrimaryAscending(false);
		if (device.getDeviceType() == HiveAp.Device_TYPE_HIVEAP) {
			if(device.getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_BONJOUR_SERVICE)){
				String where = "(defaultFlag = :s1 or configType.wirelessEnabled = :s2 or "
						+ "(configType.bonjourEnabled = :s3 and configType.wirelessEnabled = :s4 and configType.routerEnabled = :s4 and configType.switchEnabled = :s4))";
				Object[] values = new Object[] { true, true, true, false };
				configTemplates = getBoCheckItems("configName",
						ConfigTemplate.class, new FilterParams(where, values),
						sortParams);
			}else{
				String where = "defaultFlag = :s1 or configType.wirelessEnabled = :s2";
				Object[] values = new Object[] { true, true};
				configTemplates = getBoCheckItems("configName",
						ConfigTemplate.class, new FilterParams(where, values),
						sortParams);
			}
			
		} else if (device.isBranchRouter()) {
			String where = "(defaultFlag = :s1 or configType.routerEnabled = :s2 )";
			Object[] values = new Object[] { true, true};
			configTemplates = getBoCheckItems("configName",
					ConfigTemplate.class, new FilterParams(where, values),
					sortParams);
		} else if (device.isSwitch()) {
			String where = "(defaultFlag = :s1 or configType.switchEnabled = :s2 or "
					+ "(configType.bonjourEnabled = :s3 and configType.wirelessEnabled = :s4 and configType.routerEnabled = :s4 and configType.switchEnabled = :s4))";
			Object[] values = new Object[] { true, true, true, false };
			configTemplates = getBoCheckItems("configName",
					ConfigTemplate.class, new FilterParams(where, values),
					sortParams);
		} else if (device.isVpnGateway()) {
			String where = "defaultFlag = :s1";
			Object[] values = new Object[] { true };
			configTemplates = getBoCheckItems("configName",
					ConfigTemplate.class, new FilterParams(where, values),
					sortParams);
		} else {
			configTemplates = new ArrayList<CheckItem>();
		}
		if (configTemplates.isEmpty()) {
			configTemplates.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		return configTemplates;
	}

	private List<CheckItem> getUserProfilesFilterByNpType(){
		Long npId = defaultDisplayNetworkPolicyId;
		ConfigTemplate configTemplateTmp = QueryUtil.findBoById(ConfigTemplate.class, npId);
		if (configTemplateTmp == null) {
			return null;
		}
//		Object objNull = null;
//		if (configTemplateTmp.isBlnWirelessRouter()) {
			return getBoCheckItems("userProfileName", UserProfile.class, null);
//		} else {
//			return getBoCheckItems("userProfileName", UserProfile.class, new FilterParams("networkObj", objNull));
//		}
	}

	private List<CheckItem> getWifiClientPreferredSsids(){
		return getBoCheckItems("ssid", WifiClientPreferredSsid.class, null);
	}

	private void prepareTopologys(boolean autoFillNone) {
		List<CheckItem> maps = getMapListView();
		List<CheckItem> topologys = new ArrayList<CheckItem>();
		if (maps.isEmpty()) {
			if (autoFillNone) {
				topologys.add(new CheckItem((long) -1, MgrUtil
						.getUserMessage("config.optionsTransfer.none")));
			}
		} else {
			topologys.add(new CheckItem((long) -1, ""));
		}
		topologys.addAll(maps);

		this.topologys = topologys;
	}

	protected void prepareRADIUSServers() {
/*		List<CheckItem> items = new ArrayList<CheckItem>();
		List<RadiusOnHiveap> radiusOnHiveapList =
			QueryUtil.executeQuery(RadiusOnHiveap.class, new SortParams("id"), null, domainId, this);
		for (RadiusOnHiveap radiusOnHiveap : radiusOnHiveapList) {
			if(RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE == radiusOnHiveap.getDatabaseType() ||
				RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_ACTIVE == radiusOnHiveap.getDatabaseType()) {
				if ("edit2".equals(operation)) {
					// exclude AD server not configured for current HiveAP
					List<ActiveDirectoryOrLdapInfo> directoryOrLdap = radiusOnHiveap.getDirectoryOrLdap();
					if (directoryOrLdap != null &&
						!directoryOrLdap.isEmpty() &&
						!getDataSource().getMacAddress().equals(directoryOrLdap.get(0).getDirectoryOrLdap().getApMac())) {
						continue;
					}
				} else {
					continue;
				}
			}

			CheckItem checkItem = new CheckItem(radiusOnHiveap.getId(), radiusOnHiveap.getRadiusName());
			items.add(checkItem);
		}
		if (items.isEmpty()) {
			items.add(new CheckItem((long) CHECK_ITEM_ID_NONE, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		} else {
			// add the special item
			items.add(0, new CheckItem((long) CHECK_ITEM_ID_BLANK, ""));
		}
		this.radiusServers = items;*/

		this.radiusServers = getBoCheckItems("radiusName",
				RadiusOnHiveap.class, null, BaseAction.CHECK_ITEM_BEGIN_BLANK,
				BaseAction.CHECK_ITEM_END_NO);
	}
	
	protected void prepareConfigMdm() {
		this.configMdmList = getBoCheckItems("policyname",
				ConfigTemplateMdm.class, null, BaseAction.CHECK_ITEM_BEGIN_BLANK,
				BaseAction.CHECK_ITEM_END_NO);
	}

	protected void prepareRADIUSProxys() {
		this.radiusProxys = getBoCheckItems("proxyName", RadiusProxy.class,
				null, BaseAction.CHECK_ITEM_BEGIN_BLANK,
				BaseAction.CHECK_ITEM_END_NO);
	}

	protected void prepareRoutingPolicy() {
		list_routingPolicy = getBoCheckItemsSort("profileName", RoutingProfilePolicy.class, null,
				new SortParams("profileName"),
				CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	protected void prepareUserProfiles() {
		//this.userProfiles = getBoCheckItems("userProfileName",
		//		UserProfile.class, null);
		this.userProfiles = getUserProfilesFilterByNpType();
	}

	protected void prepareSecondVPNGateways(){
		secondVPNGateways = new ArrayList<CheckItem>();
		secondVPNGateways.add(new CheckItem((long) CHECK_ITEM_ID_NONE, MgrUtil
				.getUserMessage("config.optionsTransfer.none")));
		if(this.getDataSource().getId() != null && this.getDataSource().getSecondVPNGateway() != null){
			secondVPNGateways.add(new CheckItem(this.getDataSource().getSecondVPNGateway().getId(),
					this.getDataSource().getSecondVPNGateway().getHostName()));
		}

		String strSql = "select id, hostname from hive_ap ap1 where ap1.SECOND_VPN_GATEWAY_ID is null and deviceType="+HiveAp.Device_TYPE_VPN_GATEWAY+
							" and not exists(select 1 from hive_ap ap2 where ap1.id = ap2.SECOND_VPN_GATEWAY_ID)";
		if(this.getDataSource().getId() != null){
			strSql += " and id != "+this.getDataSource().getId();
		}
		List<?> bos = QueryUtil.executeNativeQuery(strSql);
		for(Object obj : bos){
			Object[] objArg = (Object[])obj;
			BigInteger bid = (BigInteger)objArg[0];
			secondVPNGateways.add(new CheckItem(bid.longValue(), (String)objArg[1]));
		}
	}

	protected void prepareRoutingProfiles(){
		if ("newGuid".equals(operation) || "new2".equals(operation)) {
			keepalive = 60;
			area = "0.0.0.0";
		} else if("editHiveApGuid".equals(operation) || "edit2".equals(operation)) {
			StringBuilder sb = new StringBuilder();
			if (getDataSource().getRoutingProfile() != null) {

				List<NeighborsNameItem> items = getDataSource().getRoutingProfile().getItems();
				if (!items.isEmpty()) {
					for (NeighborsNameItem item : items) {
						sb.append(item.getNeighborsName());
						sb.append(NEWLINE_REGEX);
					}
					bgpNeighbors = sb.substring(0, sb.length() - 1);
				}
				keepalive = getDataSource().getRoutingProfile().getKeepalive();
				routerId = getDataSource().getRoutingProfile().getRouterId();
				bgpRouterId = getDataSource().getRoutingProfile().getBgpRouterId();
				area = getDataSource().getRoutingProfile().getArea();
			} else {
				keepalive = 60;
				area = "0.0.0.0";
			}

		}
	}

	protected void preparePrimaryVpnGatewayProfiles(){
		if(this.getDataSource() != null && this.getDataSource().isSecondVpnGateway()){
			HiveAp primaryGateway = QueryUtil.findBoByAttribute(HiveAp.class, "secondVPNGateway", this.getDataSource(),
					this.getDataSource().getOwner().getId(), this);
			this.getDataSource().setPrimaryVPNGateway(primaryGateway);
		}
	}

	protected void prepareDhcpServers() {
		List<CheckItem> dhcpServers = getBoCheckItems("profileName",
				VlanDhcpServer.class, null);
		List<CheckItem> selected = new ArrayList<CheckItem>();
		if (getDataSource().getDhcpServers() != null) {
			for (VlanDhcpServer server : getDataSource().getDhcpServers()) {
				CheckItem item = new CheckItem(server.getId(), server
						.getProfileName());
				selected.add(item);
			}
			dhcpServers.removeAll(selected);
		}
		// For the OptionsTransfer component
		dhcpServerOptions = new OptionsTransfer(MgrUtil
				.getUserMessage("hiveAp.dhcpServer.availableServer"), MgrUtil
				.getUserMessage("hiveAp.dhcpServer.selectedServer"),
				dhcpServers, selected, "id", "value", "dhcpServers",
				"DhcpServer");
	}

	protected void prepareEthMacAddresses() {
		List<CheckItem> macAddresses = getBoCheckItems("macOrOuiName",
				MacOrOui.class, new FilterParams("typeFlag",
						MacOrOui.TYPE_MAC_ADDRESS));
		List<HiveApLearningMac> macs = getDataSource().getLearningMacs();
		List<CheckItem> eth0List = new ArrayList<CheckItem>();
		List<CheckItem> eth1List = new ArrayList<CheckItem>();
		List<CheckItem> agg0List = new ArrayList<CheckItem>();
		List<CheckItem> red0List = new ArrayList<CheckItem>();
		if (null != macs) {
			for (HiveApLearningMac mac : macs) {
				MacOrOui item = mac.getMac();
				short itemType = mac.getLearningMacType();
				switch (itemType) {
				case HiveApLearningMac.LEARNING_MAC_AGG0:
					agg0List.add(new CheckItem(item.getId(), item
							.getMacOrOuiName()));
					break;
				case HiveApLearningMac.LEARNING_MAC_RED0:
					red0List.add(new CheckItem(item.getId(), item
							.getMacOrOuiName()));
					break;
				case HiveApLearningMac.LEARNING_MAC_ETH0:
					eth0List.add(new CheckItem(item.getId(), item
							.getMacOrOuiName()));
					break;
				case HiveApLearningMac.LEARNING_MAC_ETH1:
					eth1List.add(new CheckItem(item.getId(), item
							.getMacOrOuiName()));
					break;
				}
			}
		}
		List<CheckItem> availableEth0 = new ArrayList<CheckItem>(macAddresses);
		List<CheckItem> availableEth1 = new ArrayList<CheckItem>(macAddresses);
		List<CheckItem> availableAgg0 = new ArrayList<CheckItem>(macAddresses);
		List<CheckItem> availableRed0 = new ArrayList<CheckItem>(macAddresses);
		availableEth0.removeAll(eth0List);
		availableEth1.removeAll(eth1List);
		availableAgg0.removeAll(agg0List);
		availableRed0.removeAll(red0List);

		String left = MgrUtil
				.getUserMessage("hiveAp.ethernet.macLearning.available.mac");
		String right = MgrUtil
				.getUserMessage("hiveAp.ethernet.macLearning.selected.mac");
		// For the OptionsTransfer component
		eth0MacOptions = new OptionsTransfer(left, right, availableEth0,
				eth0List, "id", "value", "eth0Maces", "MacAddressEth0",
				SIMPLE_OBJECT_MAC, MAC_SUB_OBJECT_MAC, "macOuiEth0ListChanged",
				domainId);
		eth1MacOptions = new OptionsTransfer(left, right, availableEth1,
				eth1List, "id", "value", "eth1Maces", "MacAddressEth1",
				SIMPLE_OBJECT_MAC, MAC_SUB_OBJECT_MAC, "macOuiEth1ListChanged",
				domainId);
		agg0MacOptions = new OptionsTransfer(left, right, availableAgg0,
				agg0List, "id", "value", "agg0Maces", "MacAddressAgg0",
				SIMPLE_OBJECT_MAC, MAC_SUB_OBJECT_MAC, "macOuiAgg0ListChanged",
				domainId);
		red0MacOptions = new OptionsTransfer(left, right, availableRed0,
				red0List, "id", "value", "red0Maces", "MacAddressRed0",
				SIMPLE_OBJECT_MAC, MAC_SUB_OBJECT_MAC, "macOuiRed0ListChanged",
				domainId);
	}

	protected void prepareEthUserprofileOptions() {
		//List<CheckItem> userprofiles = getBoCheckItems("userProfileName",
		//		UserProfile.class, null);
		List<CheckItem> userprofiles = getUserProfilesFilterByNpType();
		List<CheckItem> selected = new ArrayList<CheckItem>();
		if (getDataSource().getEthCwpRadiusUserProfiles() != null
				&& !getDataSource().getEthCwpRadiusUserProfiles().isEmpty()) {
			for (UserProfile up : getDataSource().getEthCwpRadiusUserProfiles()) {
				CheckItem item = new CheckItem(up.getId(), up
						.getUserProfileName());
				selected.add(item);
			}
			userprofiles.removeAll(selected);
		}
		// For the OptionsTransfer component
		ethUserprofileOptions = new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.ethCwp.userprofile.available.title"),
				MgrUtil
						.getUserMessage("hiveAp.ethCwp.userprofile.selected.title"),
				userprofiles, selected, "id", "value", "ethUserProfiles",
				"EthCwpUserprofile");
	}

	protected void preparePreferredSsidOptions() {
		List<CheckItem> preferredSSIDs = getWifiClientPreferredSsids();
		List<CheckItem> selected = new ArrayList<CheckItem>();
		Map<Integer, WifiClientPreferredSsid> preferredSsidMap = new TreeMap<Integer, WifiClientPreferredSsid>(new Comparator<Integer>(){
			public int compare(Integer priority1, Integer priority2) {
				return  -priority1.compareTo(priority2);
			}

		});
		if (getDataSource().getWifiClientPreferredSsids() != null) {
			for (HiveApPreferredSsid hiveApPreferredSsid : getDataSource().getWifiClientPreferredSsids()) {
				WifiClientPreferredSsid ssid = QueryUtil.findBoById(WifiClientPreferredSsid.class, hiveApPreferredSsid.getPreferredId());
				if(ssid != null){
					preferredSsidMap.put(hiveApPreferredSsid.getPriority(), ssid);
				}
			}
		}

		if(preferredSsidMap.size() > 0){
			for(WifiClientPreferredSsid ssid : preferredSsidMap.values()){
				CheckItem item = new CheckItem(ssid.getId(), ssid.getSsid());
				selected.add(item);
			}
			preferredSSIDs.removeAll(selected);
		}
		// For the OptionsTransfer component
		preferredSsidOptions = new OptionsTransfer(
				MgrUtil
						.getUserMessage("hiveAp.wifiClientMode.available.title"),
				MgrUtil
						.getUserMessage("hiveAp.wifiClientMode.selected.title"),
						preferredSSIDs, selected, "id", "value", "preferredSsids",
				"WifiClientPreferredSsid", true);
	}

	protected void prepareRadioProfiles(int singleOrMulti, Collection<Long> itemSelectIds) {
		if (null != getDataSource()) {
			if (getDataSource().is11nHiveAP()) {
				if (!AhConstantUtil.isTrueAll(Device.IS_SINGLERADIO, getDataSource().getHiveApModel())
						|| !AhConstantUtil.isTrueAll(Device.IS_DUALBAND, getDataSource().getHiveApModel())) {
					//for AP370 support 11ac radio mode
					if(getDataSource().is11acHiveAP()){
						wifi1RadioProfiles = getRadioProfile(new Short[] {
								RadioProfile.RADIO_PROFILE_MODE_A,
								RadioProfile.RADIO_PROFILE_MODE_NA,
								RadioProfile.RADIO_PROFILE_MODE_AC});
						if(null == getDataSource().getWifi1RadioProfile() && singleOrMulti == SINGLE_MODIFY){
							RadioProfile defaultWifi1Profile = QueryUtil.findBoByAttribute(RadioProfile.class, "radioName",
									BeParaModule.DEFAULT_RADIO_PROFILE_NAME_AC);
							wifi1RadioProfile = defaultWifi1Profile.getId();
							wifi1RadioModeLabel = defaultWifi1Profile.getRadioModeString();
						}
					}else{
						wifi1RadioProfiles = getRadioProfile(new Short[] {
								RadioProfile.RADIO_PROFILE_MODE_A,
								RadioProfile.RADIO_PROFILE_MODE_NA });
					}
					wifi0RadioProfiles = getRadioProfile(new Short[] {
							RadioProfile.RADIO_PROFILE_MODE_BG,
							RadioProfile.RADIO_PROFILE_MODE_NG });
				} else {
					wifi0RadioProfiles = getRadioProfile(new Short[] {
							RadioProfile.RADIO_PROFILE_MODE_BG,
							RadioProfile.RADIO_PROFILE_MODE_NG,
							RadioProfile.RADIO_PROFILE_MODE_A,
							RadioProfile.RADIO_PROFILE_MODE_NA });
					// avoid exception
					wifi1RadioProfiles = wifi0RadioProfiles;
				}
			} else {
				wifi1RadioProfiles = getRadioProfile(new Short[] { RadioProfile.RADIO_PROFILE_MODE_A });
				wifi0RadioProfiles = getRadioProfile(new Short[] { RadioProfile.RADIO_PROFILE_MODE_BG });
			}

			// update radio mode label && radio label && channel list
			RadioProfile wifi0Profile = getDataSource().getWifi0RadioProfile();
			RadioProfile wifi1Profile = getDataSource().getWifi1RadioProfile();
			if (wifi0Profile != null) {
				wifi0RadioProfile = wifi0Profile.getId();
				wifi0RadioModeLabel = wifi0Profile.getRadioModeString();
				if (wifi0Profile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_BG
						|| wifi0Profile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG) {
					wifi0Label = MgrUtil.getUserMessage("hiveAp.if.24G");
				} else {
					wifi0Label = MgrUtil.getUserMessage("hiveAp.if.5G");
				}
				//only for wp200
				if(getDynamicBandSwitchStyle().equals("none")){
					wifi0RadioModeLabel="2.4 GHz/5 GHz";
					wifi0Label="11ng/11na";
				}
			}
			
			if (wifi1Profile != null) {
				wifi1RadioProfile = wifi1Profile.getId();
				wifi1RadioModeLabel = wifi1Profile.getRadioModeString();
				if (wifi1Profile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
						|| wifi1Profile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA
						|| wifi1Profile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC) {
					wifi1Label = MgrUtil.getUserMessage("hiveAp.if.5G");
				} else {
					wifi1Label = MgrUtil.getUserMessage("hiveAp.if.24G");
				}
			}
			
			initChannelList(singleOrMulti, itemSelectIds);
		}
	}
	
	private int[] fetchOneChannelList(List<?> mList, RadioProfile rp, int radioNumber) {
		List<Integer> resultArray=null;
		List<Integer> resultArraytmp=null;
		int[] ret=null;
		for (Object mo: mList) {
			Object[] objects = (Object[]) mo;
			short a_model = Short.valueOf(objects[0].toString());
			int a_countryCode = Integer.valueOf(objects[1].toString());
			long a_w0Id= Long.parseLong(objects[2] == null? "-1" : objects[2].toString());
			long a_w1Id= Long.parseLong(objects[3] == null? "-1" : objects[3].toString());
			int[] retArray = null;
			long c_radioId = radioNumber==0? a_w0Id: a_w1Id;
			// wifi0
			if (rp != null) {
				retArray = getChannelList(a_model, a_countryCode, rp, (short)0);
			} else {
				if (c_radioId !=-1) {
					RadioProfile rProfile = QueryUtil.findBoById(RadioProfile.class, c_radioId);
					if (rProfile!=null) {
						retArray = getChannelList(a_model, a_countryCode,
								rProfile, (short)0);
					}
				}
			}
			if (retArray!=null) {
				if (resultArray ==null) {
					resultArray = new ArrayList<Integer>();
					for(int a : retArray) {
						resultArray.add(a);
					}
				} else {
					resultArraytmp = new ArrayList<Integer>();
					for(int a : retArray) {
						if (resultArray.contains(a)) {
							resultArraytmp.add(a);
						}
					}
					resultArray.clear();
					resultArray.addAll(resultArraytmp);
				}
			}
		}
		
		if (resultArray!=null) {
			ret = new int[resultArray.size()];
			int i=0;
			for (Integer a: resultArray) {
				ret[i++] =a;
			}
		}
		return ret;
	}
	
	protected void initChannelList (int singleOrMulti, Collection<Long> itemSelectIds) {
		if (singleOrMulti == SINGLE_MODIFY) {
			if ( getDataSource().getWifi0RadioProfile() != null) {
				wifi0ChannelList = getChannelList(getDataSource()
					.getHiveApModel(), getDataSource().getCountryCode(),
					 getDataSource().getWifi0RadioProfile(), getDataSource().getWifi0()
							.getOperationMode());
			}
			if ( getDataSource().getWifi1RadioProfile() != null) {
				wifi1ChannelList = getChannelList(getDataSource()
						.getHiveApModel(), getDataSource().getCountryCode(),
						getDataSource().getWifi1RadioProfile(), getDataSource().getWifi1()
								.getOperationMode());
			}
		} else {
			String query = "select hiveApModel,countryCode, wifi0RadioProfile.id,wifi1RadioProfile.id from "
					+ HiveAp.class.getSimpleName();
			List<?> models = QueryUtil.executeQuery(query, null, new FilterParams(
					"id", itemSelectIds));
			
			wifi0ChannelList = fetchOneChannelList(models,getDataSource().getWifi0RadioProfile(), 0);
			wifi1ChannelList = fetchOneChannelList(models,getDataSource().getWifi1RadioProfile(), 1);
		}
	}

	protected List<CheckItem> getRadioProfile(Short[] radioModes) {
		List<CheckItem> radioProfiles = getBoCheckItems("radioName",
				RadioProfile.class, new FilterParams("radioMode", Arrays
						.asList(radioModes)));
		if (radioProfiles.isEmpty()) {
			radioProfiles.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		return radioProfiles;
	}

	private Collection<JSONObject> getRadioProfiles(String radioMode)
			throws Exception {
		Collection<JSONObject> profiles = new ArrayList<JSONObject>();
		List<CheckItem> profList = new ArrayList<CheckItem>();

		if ("bg".equals(radioMode)) {
			profList = getRadioProfile(new Short[] { RadioProfile.RADIO_PROFILE_MODE_BG });
		} else if ("a".equals(radioMode)) {
			profList = getRadioProfile(new Short[] { RadioProfile.RADIO_PROFILE_MODE_A });
		} else if ("ng".equals(radioMode)) {
			profList = getRadioProfile(new Short[] {
					RadioProfile.RADIO_PROFILE_MODE_BG,
					RadioProfile.RADIO_PROFILE_MODE_NG });
		} else if ("na".equals(radioMode)) {
			profList = getRadioProfile(new Short[] {
					RadioProfile.RADIO_PROFILE_MODE_A,
					RadioProfile.RADIO_PROFILE_MODE_NA });
		} else if ("abgn".equals(radioMode)) {
			profList = getRadioProfile(new Short[] {
					RadioProfile.RADIO_PROFILE_MODE_A,
					RadioProfile.RADIO_PROFILE_MODE_NA,
					RadioProfile.RADIO_PROFILE_MODE_BG,
					RadioProfile.RADIO_PROFILE_MODE_NG });
		} else if ("ac".equals(radioMode)){
			profList = getRadioProfile(new Short[] {
					RadioProfile.RADIO_PROFILE_MODE_A,
					RadioProfile.RADIO_PROFILE_MODE_NA,
					RadioProfile.RADIO_PROFILE_MODE_AC});
		}
		JSONObject attribute;
		for (CheckItem prof : profList) {
			attribute = new JSONObject();
			attribute.put("id", prof.getId());
			attribute.put("v", prof.getValue());
			profiles.add(attribute);
		}
		return profiles;
	}

	private JSONObject getAg20RadioProfiles(int countryCode,
			short wifi0OperationMode, short wifi1OperationMode)
			throws Exception {
		JSONObject object = new JSONObject();
		RadioProfile bg = HmBeParaUtil.getDefaultRadioBGProfile();
		if (null != getDataSource().getWifi0RadioProfile() && RadioProfile.RADIO_PROFILE_MODE_BG ==
			getDataSource().getWifi0RadioProfile().getRadioMode()) {
			bg = getDataSource().getWifi0RadioProfile();
		}
		RadioProfile a = HmBeParaUtil.getDefaultRadioAProfile();
		if (null != getDataSource().getWifi1RadioProfile() && RadioProfile.RADIO_PROFILE_MODE_A ==
			getDataSource().getWifi1RadioProfile().getRadioMode()) {
			a = getDataSource().getWifi1RadioProfile();
		}
		Collection<JSONObject> wifi0Profiles = getRadioProfiles("bg");
		Collection<JSONObject> wifi1Profiles = getRadioProfiles("a");
		object.put("wifi0", wifi0Profiles);
		object.put("wifi1", wifi1Profiles);
		object.put("wifi0d", bg.getId());
		object.put("wifi1d", a.getId());
		object.put("wifi0c", getChannelJSONList(HiveAp.HIVEAP_MODEL_20,
				countryCode, bg, wifi0OperationMode));
		object.put("wifi1c", getChannelJSONList(HiveAp.HIVEAP_MODEL_20,
				countryCode, a, wifi1OperationMode));
		object.put("wifi0dl", MgrUtil.getEnumString("enum.radioProfileMode."
				+ RadioProfile.RADIO_PROFILE_MODE_BG));
		object.put("wifi1dl", MgrUtil.getEnumString("enum.radioProfileMode."
				+ RadioProfile.RADIO_PROFILE_MODE_A));
		object.put("wifi0label", getWifiInterfaceLabel(bg));
		object.put("wifi1label", getWifiInterfaceLabel(a));
		return object;
	}

	private JSONObject get11nDualRadioProfiles(int countryCode,
			short wifi0OperationMode, short wifi1OperationMode, short model)
			throws Exception {
		RadioProfile ng = HmBeParaUtil.getDefaultRadioNGProfile();
		if (null != getDataSource().getWifi0RadioProfile() && (RadioProfile.RADIO_PROFILE_MODE_BG ==
			getDataSource().getWifi0RadioProfile().getRadioMode() || RadioProfile.RADIO_PROFILE_MODE_NG ==
				getDataSource().getWifi0RadioProfile().getRadioMode())) {
			ng = getDataSource().getWifi0RadioProfile();
		}
		RadioProfile na = HmBeParaUtil.getDefaultRadioNAProfile();
		if (null != getDataSource().getWifi1RadioProfile() && 
				(RadioProfile.RADIO_PROFILE_MODE_A == getDataSource().getWifi1RadioProfile().getRadioMode() || 
				RadioProfile.RADIO_PROFILE_MODE_NA == getDataSource().getWifi1RadioProfile().getRadioMode() || 
				RadioProfile.RADIO_PROFILE_MODE_AC == getDataSource().getWifi1RadioProfile().getRadioMode())
				) {
			na = getDataSource().getWifi1RadioProfile();
		}
		JSONObject object = new JSONObject();
		Collection<JSONObject> wifi0Profiles = getRadioProfiles("ng");
		Collection<JSONObject> wifi1Profiles = getRadioProfiles("na");
		//for 11ac mode
		if(HiveAp.is11acHiveAP(model)){
			wifi1Profiles = getRadioProfiles("ac");
			if(null == getDataSource().getWifi1RadioProfile()){
				na = QueryUtil.findBoByAttribute(RadioProfile.class, "radioName",
						BeParaModule.DEFAULT_RADIO_PROFILE_NAME_AC);
			}
		}
		object.put("wifi0", wifi0Profiles);
		object.put("wifi1", wifi1Profiles);
		object.put("wifi0d", ng.getId());
		object.put("wifi1d", na.getId());
		object.put("wifi0c", getChannelJSONList(model,
				countryCode, ng, wifi0OperationMode));
		object.put("wifi1c", getChannelJSONList(model,
				countryCode, na, wifi1OperationMode));
		object.put("wifi0dl", MgrUtil.getEnumString("enum.radioProfileMode."
				+ RadioProfile.RADIO_PROFILE_MODE_NG));
		if(HiveAp.is11acHiveAP(model)){
			object.put("wifi1dl", MgrUtil.getEnumString("enum.radioProfileMode."
					+ RadioProfile.RADIO_PROFILE_MODE_AC));
		}else{
			object.put("wifi1dl", MgrUtil.getEnumString("enum.radioProfileMode."
					+ RadioProfile.RADIO_PROFILE_MODE_NA));
		}
		object.put("wifi0label", getWifiInterfaceLabel(ng));
		object.put("wifi1label", getWifiInterfaceLabel(na));
		return object;
	}

	private JSONObject get11nSingleRadioProfiles(int countryCode,
			short wifi0OperationMode) throws Exception {
		RadioProfile ng = HmBeParaUtil.getDefaultRadioNGProfile();
		if (null != getDataSource().getWifi0RadioProfile()) {
			ng = getDataSource().getWifi0RadioProfile();
		}
		JSONObject object = new JSONObject();
		Collection<JSONObject> wifi0Profiles = getRadioProfiles("abgn");
		object.put("wifi0", wifi0Profiles);
		object.put("wifi0d", ng.getId());
		object.put("wifi0c", getChannelJSONList(HiveAp.HIVEAP_MODEL_110,
				countryCode, ng, wifi0OperationMode));
		object.put("wifi0dl", MgrUtil.getEnumString("enum.radioProfileMode."
				+ RadioProfile.RADIO_PROFILE_MODE_NG));
		object.put("wifi0label", getWifiInterfaceLabel(ng));
		return object;
	}

	private int[] getChannelList(short hiveApModel, int countryCode,
			RadioProfile radioProfile, short operationMode) {
		if (null != radioProfile) {
			short radioMode = radioProfile.getRadioMode();
			short channelWidth = radioProfile.getChannelWidth();
			boolean dfsEnabled = radioProfile.isEnableDfs();
			boolean turboEnable = radioProfile.isTurboMode();
			Boolean isOutdoor = AhConstantUtil.isTrueAll(Device.IS_OUTDOOR, hiveApModel);

			boolean isDfsChannel = dfsEnabled;
					// support all mode access, dual, backhaul
//					&& (operationMode == AhInterface.OPERATION_MODE_ACCESS
//							|| operationMode == AhInterface.OPERATION_MODE_DUAL);
			boolean isTurboChannel = turboEnable
					&& !HiveAp.is11nHiveAP(hiveApModel);
			switch (radioMode) {
			case RadioProfile.RADIO_PROFILE_MODE_A:
			case RadioProfile.RADIO_PROFILE_MODE_NA:
			case RadioProfile.RADIO_PROFILE_MODE_AC:
				return CountryCode.getChannelList_5GHz(countryCode,
						channelWidth, isDfsChannel, isTurboChannel, hiveApModel, isOutdoor);
			case RadioProfile.RADIO_PROFILE_MODE_BG:
			case RadioProfile.RADIO_PROFILE_MODE_NG:
				return CountryCode.getChannelList_2_4GHz(countryCode,
						channelWidth);
			}
		}
		return null;
	}

	private JSONArray getChannelJSONList(short hiveApModel, int countryCode,
			RadioProfile radioProfile, short operationMode)
			throws JSONException {
		int[] list = getChannelList(hiveApModel, countryCode, radioProfile,
				operationMode);
		if (null != list) {
			JSONArray array = new JSONArray();
			for (int channel : list) {
				JSONObject object = new JSONObject();
				if (channel == 0) {
					object.put("key", "0");
					object.put("value", "Auto");
				} else {
					object.put("key", channel);
					object.put("value", channel);
				}
				array.put(object);
			}
			return array;
		}
		return null;
	}

	private String getWifiInterfaceLabel(RadioProfile radioProfile) {
		if (null != radioProfile) {
			short radioMode = radioProfile.getRadioMode();
			switch (radioMode) {
			case RadioProfile.RADIO_PROFILE_MODE_A:
			case RadioProfile.RADIO_PROFILE_MODE_NA:
			case RadioProfile.RADIO_PROFILE_MODE_AC:
				return MgrUtil.getUserMessage("hiveAp.if.5G");
			case RadioProfile.RADIO_PROFILE_MODE_BG:
			case RadioProfile.RADIO_PROFILE_MODE_NG:
				return MgrUtil.getUserMessage("hiveAp.if.24G");
			}
		}
		return "";
	}

	private JSONObject getRadioInfos(short hiveApModel, Long profileId,
			int countryCode, short operationMode) throws Exception {
		JSONObject jsonObject = new JSONObject();
		if (null != profileId) {
			RadioProfile profile = QueryUtil.findBoById(RadioProfile.class,
					profileId);
			if (null == profile) {
				return jsonObject;
			}
			short mode = profile.getRadioMode();
			jsonObject.put("m", profile.getRadioModeString());
			jsonObject.put("l", getWifiInterfaceLabel(profile));
			jsonObject.put("c", getChannelJSONList(hiveApModel, countryCode,
					profile, operationMode));
			jsonObject.put("o", operationMode);
			jsonObject.put("enable5G",
					mode == RadioProfile.RADIO_PROFILE_MODE_A
							|| mode == RadioProfile.RADIO_PROFILE_MODE_NA
							|| mode == RadioProfile.RADIO_PROFILE_MODE_AC);
		}
		return jsonObject;
	}
	
	private JSONObject getRadioInfosMulti(Long profileId, short operationMode, int radioNumber) throws Exception {
		JSONObject jsonObject = new JSONObject();
		if (null != profileId) {
			RadioProfile profile = QueryUtil.findBoById(RadioProfile.class,
					profileId);
			if (null == profile) {
				jsonObject.put("c", getChannelJSONListMulti(profile, operationMode, radioNumber));
			} else {
				short mode = profile.getRadioMode();
				jsonObject.put("m", profile.getRadioModeString());
				jsonObject.put("l", getWifiInterfaceLabel(profile));
				jsonObject.put("c", getChannelJSONListMulti(profile, operationMode, radioNumber));
				jsonObject.put("o", operationMode);
				jsonObject.put("enable5G",
						mode == RadioProfile.RADIO_PROFILE_MODE_A
								|| mode == RadioProfile.RADIO_PROFILE_MODE_NA
								|| mode == RadioProfile.RADIO_PROFILE_MODE_AC);
			}
		}
		return jsonObject;
	}
	
	private JSONArray getChannelJSONListMulti(RadioProfile radioProfile, short operationMode, int radioNumber)
			throws JSONException {
		
		String query = "select hiveApModel,countryCode, wifi0RadioProfile.id,wifi1RadioProfile.id from "
				+ HiveAp.class.getSimpleName();
		List<?> models = QueryUtil.executeQuery(query, null, new FilterParams(
				"id", getMultipleEditIds()));
		int[] list = fetchOneChannelList(models, radioProfile, radioNumber);
		
//		int[] list = getChannelList(hiveApModel, countryCode, radioProfile,
//				operationMode);
		if (null != list) {
			JSONArray array = new JSONArray();
			for (int channel : list) {
				JSONObject object = new JSONObject();
				if (channel == 0) {
					object.put("key", "0");
					object.put("value", "Auto");
				} else {
					object.put("key", channel);
					object.put("value", channel);
				}
				array.put(object);
			}
			return array;
		}
		return null;
	}

	// define for change CAPWAP pass phrase
	private boolean changePassPhrase;

	public boolean isChangePassPhrase() {
		return changePassPhrase;
	}

	public void setChangePassPhrase(boolean changePassPhrase) {
		this.changePassPhrase = changePassPhrase;
	}

	public String getPassPhraseDisabled() {
		if (changePassPhrase) {
			return "false";
		} else {
			return "true";
		}
	}
	//add by nxma
	private String multiplevlanInput;
	public void setMultiplevlanInput(String multiplevlanInput) {
		this.multiplevlanInput = multiplevlanInput;
	}

	// define for routingProfile staticRoutes
	private String staticRouteIpInput;

	private String staticRouteMaskInput;

	private String staticRouteGwInput;

	//define for BR static routing
	private String brStaticRouteIpInput;

	private String  brStaticRouteMaskInput;

	private String  brStaticRouteGwInput;

	private boolean advertiseCvg;
	
	private String[] wanIfNums;
	
	private String[] wanIfConnTypes;
	
	private String[] wanIfIpAndNetmasks;

	//define for CVG Internal Network

	private String interNetIpInput;

	private String interNetMaskInput;

	private boolean distributeNet;

	// define for ip route;
	private String ipRouteIpInput;

	private String ipRouteMaskInput;

	private String ipRouteGwInput;

	// define for dynamic route;
	private String neighborMac;

	private int routeMinimun = 67;

	private int routeMaximun = 67;

	// define for static route;
	private String destinationMac;

	private short interfaceType;

	private String nextHopMac;

	private OptionsTransfer includedNeighborOptions;

	private OptionsTransfer excludedNeighborOptions;

	private String			virtualConnectName;

	private byte			virtualConnectAction;

	private byte			virtualConnectInterface_in;

	private byte			virtualConnectInterface_out;

	private String			virtualConnectSourceMac;

	private String			virtualConnectDestMac;

	private String			virtualConnectTxMac;

	private String			virtualConnectRxMac;

	private short hiveApModel;

	public short getHiveApModel(){
		return this.hiveApModel;
	}

	public void setHiveApModel(short hiveApModel){
		this.hiveApModel = hiveApModel;
	}

	public void setVirtualConnectName(String virtualConnectName) {
		this.virtualConnectName = virtualConnectName;
	}

	public void setVirtualConnectAction(byte virtualConnectAction) {
		this.virtualConnectAction = virtualConnectAction;
	}

	public void setVirtualConnectInterface_in(byte virtualConnectInterfaceIn) {
		virtualConnectInterface_in = virtualConnectInterfaceIn;
	}

	public void setVirtualConnectInterface_out(byte virtualConnectInterfaceOut) {
		virtualConnectInterface_out = virtualConnectInterfaceOut;
	}

	public void setVirtualConnectSourceMac(String virtualConnectSourceMac) {
		this.virtualConnectSourceMac = virtualConnectSourceMac;
	}

	public void setVirtualConnectDestMac(String virtualConnectDestMac) {
		this.virtualConnectDestMac = virtualConnectDestMac;
	}

	public void setVirtualConnectTxMac(String virtualConnectTxMac) {
		this.virtualConnectTxMac = virtualConnectTxMac;
	}

	public void setVirtualConnectRxMac(String virtualConnectRxMac) {
		this.virtualConnectRxMac = virtualConnectRxMac;
	}

	public String getStaticRouteIpInput() {
		return staticRouteIpInput;
	}

	public void setStaticRouteIpInput(String staticRouteIpInput) {
		this.staticRouteIpInput = staticRouteIpInput;
	}

	public String getStaticRouteMaskInput() {
		return staticRouteMaskInput;
	}

	public void setStaticRouteMaskInput(String staticRouteMaskInput) {
		this.staticRouteMaskInput = staticRouteMaskInput;
	}

	public String getStaticRouteGwInput() {
		return staticRouteGwInput;
	}

	public void setStaticRouteGwInput(String staticRouteGwInput) {
		this.staticRouteGwInput = staticRouteGwInput;
	}

	public void setIpRouteIpInput(String ipRouteIpInput) {
		this.ipRouteIpInput = ipRouteIpInput;
	}

	public void setIpRouteMaskInput(String ipRouteMaskInput) {
		this.ipRouteMaskInput = ipRouteMaskInput;
	}

	public void setIpRouteGwInput(String ipRouteGwInput) {
		this.ipRouteGwInput = ipRouteGwInput;
	}

	public String getBrStaticRouteIpInput() {
		return brStaticRouteIpInput;
	}

	public void setBrStaticRouteIpInput(String brStaticRouteIpInput) {
		this.brStaticRouteIpInput = brStaticRouteIpInput;
	}

	public String getBrStaticRouteMaskInput() {
		return brStaticRouteMaskInput;
	}

	public void setBrStaticRouteMaskInput(String brStaticRouteMaskInput) {
		this.brStaticRouteMaskInput = brStaticRouteMaskInput;
	}

	public String getBrStaticRouteGwInput() {
		return brStaticRouteGwInput;
	}

	public void setBrStaticRouteGwInput(String brStaticRouteGwInput) {
		this.brStaticRouteGwInput = brStaticRouteGwInput;
	}

	public boolean isAdvertiseCvg() {
		return advertiseCvg;
	}

	public void setAdvertiseCvg(boolean advertiseCvg) {
		this.advertiseCvg = advertiseCvg;
	}

	public String[] getWanIfNums() {
		return wanIfNums;
	}

	public void setWanIfNums(String[] wanIfNums) {
		this.wanIfNums = wanIfNums;
	}

	public String[] getWanIfConnTypes() {
		return wanIfConnTypes;
	}

	public String[] getWanIfIpAndNetmasks() {
		return wanIfIpAndNetmasks;
	}

	public void setWanIfConnTypes(String[] wanIfConnTypes) {
		this.wanIfConnTypes = wanIfConnTypes;
	}

	public void setWanIfIpAndNetmasks(String[] wanIfIpAndNetmasks) {
		this.wanIfIpAndNetmasks = wanIfIpAndNetmasks;
	}

	public String getNeighborMac() {
		return neighborMac;
	}

	public void setNeighborMac(String neighborMac) {
		this.neighborMac = neighborMac;
	}

	public int getRouteMinimun() {
		return routeMinimun;
	}

	public void setRouteMinimun(int routeMinimun) {
		this.routeMinimun = routeMinimun;
	}

	public int getRouteMaximun() {
		return routeMaximun;
	}

	public void setRouteMaximun(int routeMaximun) {
		this.routeMaximun = routeMaximun;
	}

	public void setDestinationMac(String destinationMac) {
		this.destinationMac = destinationMac;
	}

	public void setInterfaceType(short interfaceType) {
		this.interfaceType = interfaceType;
	}

	public void setNextHopMac(String nextHopMac) {
		this.nextHopMac = nextHopMac;
	}

	public OptionsTransfer getIncludedNeighborOptions() {
		return includedNeighborOptions;
	}

	public void setIncludedNeighborOptions(
			OptionsTransfer includedNeighborOptions) {
		this.includedNeighborOptions = includedNeighborOptions;
	}

	public OptionsTransfer getExcludedNeighborOptions() {
		return excludedNeighborOptions;
	}

	public void setExcludedNeighborOptions(
			OptionsTransfer excludedNeighborOptions) {
		this.excludedNeighborOptions = excludedNeighborOptions;
	}

	private Collection<String>	staticRouteIndices;

	private Collection<String>	dynamicRouteIndices;

	private Collection<String>	ipRouteIndices;

	private Collection<String>	multiplevlanIndices;

	private Collection<String>	virtualConnectIndices;

	private Collection<String> routingProfilesStaticRoutesIndices;

	private Collection<String> intNetworkIndices;

	private Collection<String> brStaticRouteingIndices;

	private short[]				interfaceTypes;

	public void setVirtualConnectIndices(Collection<String> virtualConnectIndices) {
		this.virtualConnectIndices = virtualConnectIndices;
	}

	public void setStaticRouteIndices(Collection<String> staticRouteIndices) {
		this.staticRouteIndices = staticRouteIndices;
	}

	public void setDynamicRouteIndices(Collection<String> dynamicRouteIndices) {
		this.dynamicRouteIndices = dynamicRouteIndices;
	}

	public void setIpRouteIndices(Collection<String> ipRouteIndices) {
		this.ipRouteIndices = ipRouteIndices;
	}

	public void setMultiplevlanIndices(Collection<String> multiplevlanIndices) {
		this.multiplevlanIndices = multiplevlanIndices;
	}

	public void setInterfaceTypes(short[] interfaceTypes) {
		this.interfaceTypes = interfaceTypes;
	}

	public Collection<String> getRoutingProfilesStaticRoutesIndices() {
		return routingProfilesStaticRoutesIndices;
	}

	public void setRoutingProfilesStaticRoutesIndices(
			Collection<String> routingProfilesStaticRoutesIndices) {
		this.routingProfilesStaticRoutesIndices = routingProfilesStaticRoutesIndices;
	}

	private void prepareAvailableHiveAps() throws Exception {
		if (domainId == null) {
			domainId = QueryUtil.getDependentDomainFilter(userContext);
		}
		List<?> hiveApFields = QueryUtil.executeQuery(
				"select bo.macAddress, bo.hostName, bo.ipAddress from "
						+ HiveAp.class.getSimpleName() + " bo", null,
				new FilterParams("manageStatus", HiveAp.STATUS_MANAGED),
				domainId);
		Collection<TextItem> totalList = new ArrayList<TextItem>();
		Collection<TextItem> includedList = new ArrayList<TextItem>();
		Collection<TextItem> excludedList = new ArrayList<TextItem>();
		for (Object obj : hiveApFields) {
			Object[] fields = (Object[]) obj;
			if (null == fields[2] || "".equals(fields[2])) {
				// HiveAP with none IP, will not appear in the neighbor list.
				continue;
			}
			String macAddress = (String) fields[0];
			String hostName = (String) fields[1];
			TextItem item = new TextItem(macAddress, hostName);

			totalList.add(item);
			for (HiveApL3cfgNeighbor tmp_neighbor : getDataSource()
					.getL3Neighbors()) {
				if (macAddress.equals(tmp_neighbor.getNeighborMac())) {
					if (tmp_neighbor.getNeighborType() == HiveApL3cfgNeighbor.NEIGHBOR_TYPE_EXCLUDED) {
						excludedList.add(item);
					} else {
						includedList.add(item);
					}
				}
			}
		}
		List<TextItem> availableInHiveAps = new ArrayList<TextItem>(totalList);
		availableInHiveAps.removeAll(includedList);
		List<TextItem> availableExHiveAps = new ArrayList<TextItem>(totalList);
		availableExHiveAps.removeAll(excludedList);
		// remove itself while doing update operation
		if (null != getDataSource().getId()) {
			availableInHiveAps.remove(new TextItem(getDataSource()
					.getMacAddress(), getDataSource().getHostName()));
			availableExHiveAps.remove(new TextItem(getDataSource()
					.getMacAddress(), getDataSource().getHostName()));
		}
		// For the OptionsTransfer component
		includedNeighborOptions = new OptionsTransfer(
				MgrUtil.getUserMessage("hiveAp.cfg.l3Roaming.availableNeihbor"),
				MgrUtil.getUserMessage("hiveAp.cfg.l3Roaming.selectedNeighbor"),
				availableInHiveAps, includedList, "key", "value",
				"includedNeighbors");

		excludedNeighborOptions = new OptionsTransfer(
				MgrUtil.getUserMessage("hiveAp.cfg.l3Roaming.availableNeihbor"),
				MgrUtil.getUserMessage("hiveAp.cfg.l3Roaming.selectedNeighbor"),
				availableExHiveAps, excludedList, "key", "value",
				"excludedNeighbors");
	}

	private boolean setSelectedNeighbor() throws Exception {
		List<HiveApL3cfgNeighbor> neighbors = getDataSource().getL3Neighbors();
		neighbors.clear();
		if (null == includedNeighbors && null == excludedNeighbors) {
			return true;
		}
		boolean result = true;
		short include = HiveApL3cfgNeighbor.NEIGHBOR_TYPE_INCLUDED;
		short exclude = HiveApL3cfgNeighbor.NEIGHBOR_TYPE_EXCLUDED;
		if (includedNeighbors != null) {
			for (String includedNeighbor : includedNeighbors) {
				if (includedNeighbor != null) {
					HiveApL3cfgNeighbor hn = new HiveApL3cfgNeighbor();
					hn.setNeighborMac(includedNeighbor);
					hn.setNeighborType(include);
					neighbors.add(hn);
				}
			}
		}
		if (excludedNeighbors != null) {
			for (String excludedNeighbor : excludedNeighbors) {
				if (excludedNeighbor != null) {
					HiveApL3cfgNeighbor hn = new HiveApL3cfgNeighbor();
					hn.setNeighborMac(excludedNeighbor);
					hn.setNeighborType(exclude);
					neighbors.add(hn);
				}
			}
		}
		getDataSource().setL3Neighbors(neighbors);
		log.debug("setSelectedNeighbor", "HiveAp "
				+ getDataSource().getHostName() + " has " + neighbors.size()
				+ " Neighbors.");
		return result;
	}

	private void setSelectedWifi0RadioProfile() throws Exception {
		if (null != wifi0RadioProfile) {
			RadioProfile radioProfile = QueryUtil.findBoById(
					RadioProfile.class, wifi0RadioProfile);
			if (radioProfile == null && wifi0RadioProfile != -1
					&& wifi0RadioProfile != -2 && wifi0RadioProfile != -3) {
				String tempStr[] = { getText("hiveAp.head.wifi0.radioProfile") };
				addActionError(getText("info.ssid.warning", tempStr));
			}
			if (null != getDataSource() && null != radioProfile) {
				getDataSource().setWifi0RadioProfile(radioProfile);
			}
		}
	}

	private void setSelectedWifi1RadioProfile() throws Exception {
		if (null != wifi1RadioProfile) {
			RadioProfile radioProfile = QueryUtil.findBoById(
					RadioProfile.class, wifi1RadioProfile);
			if (radioProfile == null && wifi1RadioProfile != -1
					&& wifi1RadioProfile != -2 && wifi1RadioProfile != -3) {
				String tempStr[] = { getText("hiveAp.head.wifi1.radioProfile") };
				addActionError(getText("info.ssid.warning", tempStr));
			}
			if (null != getDataSource() && null != radioProfile) {
				getDataSource().setWifi1RadioProfile(radioProfile);
			}
		}
		if (!getDataSource().isWifi1Available()) {
			// set a default value, avoid NPE
			getDataSource().setWifi1RadioProfile(
					HmBeParaUtil.getDefaultRadioNAProfile());
		}
	}

	protected void setSelectedCapwapIp() throws Exception {
		if (null != capwapIp) {
			IpAddress ipaddress = QueryUtil.findBoById(IpAddress.class,
					capwapIp);
			if (null != getDataSource()) {
				getDataSource().setCapwapIpBind(ipaddress);
			}
		}
		if (null != capwapBackupIp) {
			IpAddress ipaddress = QueryUtil.findBoById(IpAddress.class,
					capwapBackupIp);
			if (null != getDataSource()) {
				getDataSource().setCapwapBackupIpBind(ipaddress);
			}
		}
	}

	private void updateRoutingProfile(HiveAp hiveAp) throws Exception {
		if (null != hiveAp) {
			RoutingProfile routingProfile = hiveAp.getRoutingProfile();
			if (null != routingProfile && routingProfile.isEnableDynamicRouting()) {

				switch (routingProfile.getTypeFlag()) {
					case RoutingProfile.ENABLE_DRP_BGP:
						removeOspfData(routingProfile);
						List<NeighborsNameItem> dItems = new ArrayList<NeighborsNameItem>();
						for (String dItemName :  bgpNeighbors.split(NEWLINE_REGEX)) {
							if (StringUtils.isNotBlank(dItemName)) {
								NeighborsNameItem dItem = new NeighborsNameItem();
								dItem.setNeighborsName(dItemName);
								dItems.add(dItem);
							}
						}
						routingProfile.setItems(dItems);
						routingProfile.setKeepalive(keepalive);
						routingProfile.setBgpRouterId(bgpRouterId);
						break;
					case RoutingProfile.ENABLE_DRP_OSPF:
						removeBgpData(routingProfile);
						routingProfile.setRouterId(routerId);
						routingProfile.setArea(area);
						break;
					default:
						removeOspfData(routingProfile);
						removeBgpData(routingProfile);
						break;
				}

				if (hiveAp.getRoutingProfile().getId() == null) {
					routingProfile.setOwner(getDomain());
					QueryUtil.createBo(routingProfile);
				} else {
					QueryUtil.updateBo(routingProfile);

				}

				hiveAp.setRoutingProfile(routingProfile);
			} else {
				hiveAp.setRoutingProfile(null);
			}
		}
	}

	private void removeBgpData(RoutingProfile routingProfile) {
		routingProfile.setAutonmousSysNm(null);
		routingProfile.setKeepalive(null);
		routingProfile.setBgpRouterId(null);
		routingProfile.setItems(new ArrayList<NeighborsNameItem>());
	}

	private void removeOspfData(RoutingProfile routingProfile) {
		routingProfile.setArea(null);
		routingProfile.setRouterId(null);
	}

	private void updateIpPrefix(HiveAp hiveAp) {
		if (null != hiveAp) {
			if (hiveAp.isDhcp() && !hiveAp.isDhcpFallback()) {
				String ipPrefix = hiveAp.getCfgIpAddress();
				String mask = hiveAp.getCfgNetmask();
				mask = (null == mask || "".equals(mask)) ? "255.255.0.0" : mask;
				hiveAp.setCfgIpAddress(getIpPrefix(ipPrefix, mask));
			}
		}
	}

	private String getIpPrefix(String ip, String netmask) {
		String ipPrefix = ip;
		if (null != ip && null != netmask) {
			try {
				String[] ipArray = ip.split("\\.");
				String[] maskArray = netmask.split("\\.");
				ipPrefix = "";
				for (int i = 0; i < ipArray.length; i++) {
					String part = String.valueOf(Integer.valueOf(ipArray[i])
							& Integer.valueOf(maskArray[i]));
					ipPrefix += i == 0 ? part : "." + part;
				}
			} catch (NumberFormatException e) {
				ipPrefix = ip;
			}
		}
		return ipPrefix;
	}

	private IpAddress autoCreateIpAddress() {
		if ((null == capwapIp || capwapIp == -1)
				&& null != getDataSource().getCapwapText()
				&& !"".equals(getDataSource().getCapwapText())) {
			short ipType = ImportCsvFileAction
					.getIpAddressWrongFlag(getDataSource().getCapwapText()) ? IpAddress.TYPE_HOST_NAME
					: IpAddress.TYPE_IP_ADDRESS;
			return CreateObjectAuto.createNewIP(
					getDataSource().getCapwapText(), ipType, getDomain(),
					MgrUtil.getUserMessage("hiveAp.capwap.server")
							+ " for "+NmsUtil.getOEMCustomer().getAccessPonitName()+":" + getDataSource().getHostName());
		}
		return null;
	}

	private IpAddress autoCreateBackupIpAddress() {
		if ((null == capwapBackupIp || capwapBackupIp == -1)
				&& null != getDataSource().getCapwapBackupText()
				&& !"".equals(getDataSource().getCapwapBackupText())) {
			short ipType = ImportCsvFileAction
					.getIpAddressWrongFlag(getDataSource()
							.getCapwapBackupText()) ? IpAddress.TYPE_HOST_NAME
					: IpAddress.TYPE_IP_ADDRESS;
			return CreateObjectAuto.createNewIP(getDataSource()
					.getCapwapBackupText(), ipType, getDomain(), MgrUtil
					.getUserMessage("hiveAp.capwap.server.backup")
					+ " for "+NmsUtil.getOEMCustomer().getAccessPonitName()+":" + getDataSource().getHostName());
		}
		return null;
	}

	protected void setSelectedScheduler() throws Exception {
		if (null != scheduler) {
			Scheduler sd = QueryUtil.findBoById(Scheduler.class, scheduler);
			if (null != getDataSource()) {
				getDataSource().setScheduler(sd);
			}
		}
	}

	protected void setSelectedPppoeAuthProfile() throws Exception {
		if (null != pppoeAuthProfile) {
			if (branchRouterEth0!=null && ("3".equals(branchRouterEth0.getConnectionType())) && getDataSource().getDeviceType()==HiveAp.Device_TYPE_BRANCH_ROUTER) {
				PPPoE sd = QueryUtil.findBoById(PPPoE.class, pppoeAuthProfile);
				if (null != getDataSource()) {
					getDataSource().setPppoeAuthProfile(sd);
				}
			} else {
				getDataSource().setPppoeAuthProfile(null);
				getDataSource().setEnablePppoe(false);
			}
		}
	}

	protected void setSelectedIpTrack() throws Exception {
		if (null != vpnIpTrackId && null != getDataSource() && HiveAp.Device_TYPE_VPN_GATEWAY == getDataSource().getDeviceType()) {
			getDataSource().setVpnIpTrack(QueryUtil.findBoById(MgmtServiceIPTrack.class, vpnIpTrackId));
		}
	}

	protected boolean setSelectedDhcpServer() throws Exception {
		boolean result = true;
		Set<VlanDhcpServer> dhcpServerObjects = getDataSource()
				.getDhcpServers();
		dhcpServerObjects.clear();
		int dhcpServerCount = 0;
		if (null != dhcpServers) {
			for (Long serverId : dhcpServers) {
				VlanDhcpServer server = QueryUtil.findBoById(
						VlanDhcpServer.class, serverId);
				if (null != server) {
					dhcpServerObjects.add(server);
					if (server.getTypeFlag() == VlanDhcpServer.ENABLE_DHCP_SERVER) {
						dhcpServerCount++;
					}
				}
			}
			getDataSource().setDhcpServers(dhcpServerObjects);
		}
		getDataSource().setDhcpServerCount(dhcpServerCount);
		return result;
	}

	protected boolean setSelectedPreferredSsids() throws Exception {
		boolean result = true;
		short maxPriority = PREFERRED_SSID_MAX_NUMBER;
		List<HiveApPreferredSsid> preferredSsidObjects = getDataSource().getWifiClientPreferredSsids();
		preferredSsidObjects.clear();
		if (null != preferredSsids) {
			for (Long ssidId : preferredSsids) {
				WifiClientPreferredSsid wpreferredSsid = QueryUtil.findBoById(
						WifiClientPreferredSsid.class, ssidId);
				if (null != wpreferredSsid) {
					HiveApPreferredSsid preferredSsid = new HiveApPreferredSsid();
					preferredSsid.setPreferredId(wpreferredSsid.getId());
					preferredSsid.setPriority(maxPriority);
					--maxPriority;
					preferredSsidObjects.add(preferredSsid);
				}
			}
			getDataSource().setWifiClientPreferredSsids(preferredSsidObjects);
		}
		return result;
	}

	protected void setSelectedConfigTemplate() throws Exception {
		if (null != configTemplate) {
			ConfigTemplate template = QueryUtil.findBoById(
					ConfigTemplate.class, configTemplate, this);
			if (template == null && configTemplate != -1
					&& configTemplate != -3) {
				String tempStr[] = { getText("hiveAp.template") };
				addActionError(getText("info.ssid.warning", tempStr));
			}
			if (null != getDataSource()) {
				getDataSource().setConfigTemplate(template);
			}
		}
	}

	protected void setSelectedTopology() throws Exception {
		if (null != topology) {
			MapContainerNode container = getSelectedTopology();
			if (null != getDataSource()) {
				getDataSource().setMapContainer(container);
			}
		}
	}

	protected MapContainerNode getSelectedTopology() throws Exception {
		if (null != topology) {
			MapContainerNode container = QueryUtil.findBoById(
					MapContainerNode.class, topology, new QueryLazyBo());
			if (container == null && topology != -1 && topology != -3) {
				String tempStr[] = { getText("hiveAp.topology") };
				addActionError(getText("info.ssid.warning", tempStr));
			}
			return container;
		}
		return null;
	}

	protected void setSelectedRADIUSServer() throws Exception {
		if (null != getDataSource()) {
			if (null != radiusServer) {
				if (getDataSource().isBranchRouter()
						&& (!getDataSource().isEnabledBrAsRadiusServer() ||
								!getDataSource().isEnabledOverrideRadiusServer())) {
					getDataSource().setRadiusServerProfile(null);
				} else {
					RadiusOnHiveap radiusServerProfile = QueryUtil.findBoById(
							RadiusOnHiveap.class, radiusServer);
					if (radiusServerProfile == null && radiusServer != -1) {
						String tempStr[] = { getText("hiveAp.radiusServerLabel") };
						addActionError(getText("info.ssid.warning", tempStr));
					}
					getDataSource().setRadiusServerProfile(radiusServerProfile);
				}
			}

		}
	}
	protected void setSelectedMdm()throws Exception{
		if(enableMdmTag != null){
			if(enableMdmTag.equals("enable") && null != getDataSource()){
				if(null!=configmdmId && configmdmId>0 && getDataSource().getEnableForMDMItem() ){
					ConfigTemplateMdm configTemplateMdm = QueryUtil.findBoById(
							ConfigTemplateMdm.class, configmdmId);
					getDataSource().setConfigTemplateMdm(configTemplateMdm);
				}
				return;
			}
		}else{
			if(null != getDataSource()) {
				if(null!=configmdmId && configmdmId>0 && getDataSource().isEnableMDM() ){
					ConfigTemplateMdm configTemplateMdm = QueryUtil.findBoById(
							ConfigTemplateMdm.class, configmdmId);
					getDataSource().setConfigTemplateMdm(configTemplateMdm);
				}
			}
		}
	}

	protected void setSelectedRADIUSProxy() throws Exception {
		if (null != getDataSource()) {
			if (null != radiusProxy) {
				if (getDataSource().isBranchRouter()
					&& (!getDataSource().isEnabledBrAsRadiusServer() ||
							!getDataSource().isEnabledOverrideRadiusServer())) {
						getDataSource().setRadiusProxyProfile(null);
				} else {
					RadiusProxy radiusProxyProfile = QueryUtil.findBoById(
							RadiusProxy.class, radiusProxy);
					if (radiusProxyProfile == null && radiusProxy != -1) {
						String tempStr[] = { getText("hiveAp.radiusProxyLabel") };
						addActionError(getText("info.ssid.warning", tempStr));
					}
					getDataSource().setRadiusProxyProfile(radiusProxyProfile);
				}
			}
		}
	}

	protected void setSelectedRoutingPolicy() throws Exception {
		if (null != getDataSource()) {
			if (null != routingPolicyId) {
				if (getDataSource().getDeviceType()!=HiveAp.Device_TYPE_BRANCH_ROUTER || routingPolicyId<0) {
						getDataSource().setRoutingProfilePolicy(null);
				} else {
					RoutingProfilePolicy routingProfilePolicy = QueryUtil.findBoById(
							RoutingProfilePolicy.class, routingPolicyId);
					getDataSource().setRoutingProfilePolicy(routingProfilePolicy);
				}
			}
		}
	}

	protected void setSelectedRoutingProfile() throws Exception {
		if(null != getDataSource()){
			if(this.routingProfile != null){
				RoutingProfile rtProfile = QueryUtil.findBoById(RoutingProfile.class, routingProfile);
				if(rtProfile == null && routingProfile != -1) {
					String tempStr[] = { getText("hiveAp.vpnGateway.routing.profile") };
					addActionError(getText("info.ssid.warning", tempStr));
				}
				getDataSource().setRoutingProfile(rtProfile);
			}
		}
	}

	protected void setSelectedSecondVPNGateway() throws Exception {
		if(null != getDataSource()){
			if(this.secondVPNGateway != null){
				HiveAp secVPNGateway = QueryUtil.findBoById(HiveAp.class, secondVPNGateway);
				if(secVPNGateway == null && secondVPNGateway != -1){
					String tempStr[] = { getText("hiveAp.vpnGateway.secondary.gateway") };
					addActionError(getText("info.ssid.warning", tempStr));
				}
				getDataSource().setSecondVPNGateway(secVPNGateway);
			}
		}
	}

	protected void setUSBSettings(){
		if(modemName != null){
			List<USBModemProfile> usbModemProfileList = new ArrayList<>();
			USBModemProfile modemSource = null;
			for(int i=0; i<modemName.length; i++){
				for(USBModemProfile modem : getDataSource().getUsbModemList()){
					if(modem.getModemName().equals(modemName[i])){
						modemSource = new USBModemProfile();
						modemSource.setApn(apn[i]);
						modemSource.setDialupNum(dialupNum[i]);
						modemSource.setUserId(userId[i]);
						modemSource.setPassword(password[i]);
						modemSource.setCellularMode(cellularMode[i]);
						modemSource.setModemName(modem.getModemName());
						modemSource.setOsVersion(modem.getOsVersion());
						modemSource.setDisplayName(modem.getDisplayName());
						usbModemProfileList.add(modemSource);
						break;
					}
				}
			}
			getDataSource().setUsbModemList(usbModemProfileList);
		}
	}

	private void setDataSourceVPNMark(){
		if(this.getDataSource().isVpnGateway()){
			this.getDataSource().setVpnMark(HiveAp.VPN_MARK_SERVER);
			this.getDataSource().setDhcp(false);
		}else if(this.getDataSource().getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER){
			this.getDataSource().setVpnMark(HiveAp.VPN_MARK_CLIENT);
			this.getDataSource().setDhcp(false);
		} else if(this.getDataSource().getDeviceType() == HiveAp.Device_TYPE_SWITCH){
			this.getDataSource().setVpnMark(HiveAp.VPN_MARK_NONE);
		}
	}

	protected void setSelectedMacLearningObjects() throws Exception {
		// default user profile
		setSelectedUserProfile();
	}

	private void setSelectedUserProfile() throws Exception {
		if (null != getDataSource()) {
			if (isEasyMode()) {
				// don't set user profiles
				getDataSource().setEth0UserProfile(null);
				getDataSource().setEth1UserProfile(null);
				getDataSource().setAgg0UserProfile(null);
				getDataSource().setRed0UserProfile(null);
				return;
			}
			if (null != userProfileEth0) {// eth0
				if (getDataSource().getEthConfigType() == HiveAp.USE_ETHERNET_BOTH) {
					UserProfile userProfile = QueryUtil.findBoById(
							UserProfile.class, userProfileEth0);
					if (userProfile == null && userProfileEth0 != -1) {
						String tempStr[] = { getText("hiveAp.ethernet.macLearning.userProfile") };
						addActionError(getText("info.ssid.warning", tempStr));
					}
					getDataSource().setEth0UserProfile(userProfile);
				} else {
					getDataSource().setEth0UserProfile(null);
				}
			}
			if (null != userProfileEth1) {// eth1
				if (getDataSource().isWifi1Available()
						&& getDataSource().getEthConfigType() == HiveAp.USE_ETHERNET_BOTH) {
					UserProfile userProfile = QueryUtil.findBoById(
							UserProfile.class, userProfileEth1);
					if (userProfile == null && userProfileEth1 != -1) {
						String tempStr[] = { getText("hiveAp.ethernet.macLearning.userProfile") };
						addActionError(getText("info.ssid.warning", tempStr));
					}
					getDataSource().setEth1UserProfile(userProfile);
				} else {
					getDataSource().setEth1UserProfile(null);
				}
			}
			if (null != userProfileAgg0) {// agg0
				if (getDataSource().getEthConfigType() == HiveAp.USE_ETHERNET_AGG0) {
					UserProfile userProfile = QueryUtil.findBoById(
							UserProfile.class, userProfileAgg0);
					if (userProfile == null && userProfileAgg0 != -1) {
						String tempStr[] = { getText("hiveAp.ethernet.macLearning.userProfile") };
						addActionError(getText("info.ssid.warning", tempStr));
					}
					getDataSource().setAgg0UserProfile(userProfile);
				} else {
					getDataSource().setAgg0UserProfile(null);
				}
			}
			if (null != userProfileRed0) {// red0
				if (getDataSource().getEthConfigType() == HiveAp.USE_ETHERNET_RED0) {
					UserProfile userProfile = QueryUtil.findBoById(
							UserProfile.class, userProfileRed0);
					if (userProfile == null && userProfileRed0 != -1) {
						String tempStr[] = { getText("hiveAp.ethernet.macLearning.userProfile") };
						addActionError(getText("info.ssid.warning", tempStr));
					}
					getDataSource().setRed0UserProfile(userProfile);
				} else {
					getDataSource().setRed0UserProfile(null);
				}
			}
		}
	}

	private boolean isConfigureEthCwpRadiusClient(Cwp cwp) {
		byte registrationType = -1;
		if (null != cwp) {
			registrationType = cwp.getRegistrationType();
		}
		if (registrationType == Cwp.REGISTRATION_TYPE_AUTHENTICATED
				|| registrationType == Cwp.REGISTRATION_TYPE_EXTERNAL
				|| registrationType == Cwp.REGISTRATION_TYPE_BOTH) {
			return true;
		}
		return false;
	}

	private boolean isConfigureEthCwpDefaultAuthUserProfile(Cwp cwp) {
		byte registrationType = -1;
		if (null != cwp) {
			registrationType = cwp.getRegistrationType();
		}
		if (registrationType == Cwp.REGISTRATION_TYPE_AUTHENTICATED
				|| registrationType == Cwp.REGISTRATION_TYPE_EXTERNAL
				|| registrationType == Cwp.REGISTRATION_TYPE_BOTH
				|| registrationType == Cwp.REGISTRATION_TYPE_EULA) {
			return true;
		}
		return false;
	}

	private boolean isConfigureEthCwpDefaultRegUserProfile(Cwp cwp) {
		byte registrationType = -1;
		if (null != cwp) {
			registrationType = cwp.getRegistrationType();
		}
		if (registrationType == Cwp.REGISTRATION_TYPE_REGISTERED
				|| registrationType == Cwp.REGISTRATION_TYPE_BOTH) {
			return true;
		}
		return false;
	}

	protected void setSelectedEthCwpSettings() throws Exception {
		
		//fix bug 26769, version before 6.1.1.0 enable mac auth must enable ethernet cwp first.
		if(!getDataSource().isEthCwpEnableEthCwp() && 
				NmsUtil.compareSoftwareVersion("6.1.1.0", getDataSource().getSoftVer()) > 0) {
			getDataSource().setEthCwpEnableMacAuth(false);
		}
		
		if (getDataSource().getDeviceType() == HiveAp.Device_TYPE_HIVEAP && 
				(getDataSource().isEthCwpEnableEthCwp() || getDataSource().isEthCwpEnableMacAuth()) ) {
			if(getDataSource().isEthCwpEnableEthCwp()){
				setSelectedEthCwpProfile();
			}else{
				getDataSource().setEthCwpCwpProfile(null);
			}
			
			Cwp cwp = getDataSource().getEthCwpCwpProfile();
			if (getDataSource().isEthCwpEnableMacAuth()
					|| isConfigureEthCwpRadiusClient(cwp)) {
				setSelectedEthCwpRadiusClient();
				if (!"continue".equals(operation)
						&& !"continueMulti".equals(operation)) {
					setSelectedEthCwpRadiusUserProfiles();
				}
			} else {
				getDataSource().setEthCwpRadiusClient(null);
				getDataSource().setEthCwpRadiusUserProfiles(
						new HashSet<UserProfile>());
			}
			
			if (getDataSource().isEthCwpEnableMacAuth()
					|| isConfigureEthCwpDefaultAuthUserProfile(cwp)) {
				setSelectedEthCwpDefaultAuthUserProfile();
			} else {
				getDataSource().setEthCwpDefaultAuthUserProfile(null);
			}
			
			if (getDataSource().isEthCwpEnableMacAuth()
					|| isConfigureEthCwpDefaultRegUserProfile(cwp)) {
				setSelectedEthCwpDefaultRegUserProfile();
			} else {
				getDataSource().setEthCwpDefaultRegUserProfile(null);
			}
		} else {
			getDataSource().setEthCwpCwpProfile(null);
			getDataSource().setEthCwpDefaultAuthUserProfile(null);
			getDataSource().setEthCwpDefaultRegUserProfile(null);
			getDataSource().setEthCwpRadiusClient(null);
			getDataSource().setEthCwpRadiusUserProfiles(
					new HashSet<UserProfile>());
		}
	}

	protected void setSelectedEthCwpRadiusClient() {
		if (null != getDataSource()) {
			if (null != ethCwpRadiusClient) {
				RadiusAssignment bo = QueryUtil.findBoById(
						RadiusAssignment.class, ethCwpRadiusClient);
				if (bo == null && ethCwpRadiusClient != -1) {
					String tempStr[] = { getText("hiveAp.ethCwp.radius.profile") };
					addActionError(getText("info.ssid.warning", tempStr));
				}
				getDataSource().setEthCwpRadiusClient(bo);
			}
		}
	}

	protected void setSelectedEthCwpDefaultAuthUserProfile() {
		if (null != getDataSource()) {
			if (null != ethDefaultAuthUserprofile) {
				UserProfile bo = QueryUtil.findBoById(UserProfile.class,
						ethDefaultAuthUserprofile);
				if (bo == null && ethDefaultAuthUserprofile != -1) {
					String tempStr[] = { getText("hiveAp.ethCwp.userprofile.label") };
					addActionError(getText("info.ssid.warning", tempStr));
				}
				getDataSource().setEthCwpDefaultAuthUserProfile(bo);
			}
		}
	}

	protected void setSelectedEthCwpDefaultRegUserProfile() {
		if (null != getDataSource()) {
			if (null != ethDefaultRegUserprofile) {
				UserProfile bo = QueryUtil.findBoById(UserProfile.class,
						ethDefaultRegUserprofile);
				if (bo == null && ethDefaultRegUserprofile != -1) {
					String tempStr[] = { getText("hiveAp.ethCwp.userprofile.label") };
					addActionError(getText("info.ssid.warning", tempStr));
				}
				getDataSource().setEthCwpDefaultRegUserProfile(bo);
			}
		}
	}

	protected void setSelectedEthCwpRadiusUserProfiles() {
		Set<UserProfile> userProfileObjects = getDataSource()
				.getEthCwpRadiusUserProfiles();
		userProfileObjects.clear();
		if (null != ethUserProfiles) {
			for (Long userProfileId : ethUserProfiles) {
				UserProfile profile = QueryUtil.findBoById(UserProfile.class,
						userProfileId);
				if (null != profile) {
					userProfileObjects.add(profile);
				}
			}
			getDataSource().setEthCwpRadiusUserProfiles(userProfileObjects);
		}
	}

	protected void setSelectedEthCwpProfile() throws Exception {
		if (null != getDataSource()) {
			if (null != cwpProfile) {
				Cwp cwp = QueryUtil.findBoById(Cwp.class, cwpProfile);
				if (cwp == null && cwpProfile != -1) {
					String tempStr[] = { getText("hiveAp.ethCwp.profile.label") };
					addActionError(getText("info.ssid.warning", tempStr));
				}
				getDataSource().setEthCwpCwpProfile(cwp);
			}
		}
	}

	private void createWifi0RadioProfile() throws Exception {
		if (checkNameExists("radioName", getDataSource()
				.getTempWifi0RadioProfile().getRadioName(), RadioProfile.class)) {
			return;
		}
		RadioProfile profile = getDataSource().getTempWifi0RadioProfile();
		profile.setWmmItems(RadioProfileAction.getDefaultWmmInfo(profile));
		Long newId = BoMgmt.createBo(getDataSource().getTempWifi0RadioProfile(),
				getUserContext(), getSelectedL2FeatureKey());
		getDataSource().setWifi0RadioProfile(
				getDataSource().getTempWifi0RadioProfile());
		getDataSource().setTempWifi0RadioProfile(new RadioProfile());
		getDataSource().setTempWifi0RadioProfileCreateDisplayStyle("none");

		if (this.isJsonMode()) {
			prepareJsonObjForRadioProfile(newId, profile.getRadioName(), 0, profile.getRadioMode());
		}
	}

	private void prepareJsonObjForRadioProfile(Long newId, String rdName, int wifi, short radioType) throws JSONException {
		jsonObject = new JSONObject();
		jsonObject.put("newId", newId);
		jsonObject.put("name", rdName);
		jsonObject.put("wifi", wifi);

		if (HiveAp.is11nHiveAP(apModelType)) {
			if (!AhConstantUtil.isTrueAll(Device.IS_SINGLERADIO, apModelType)
					|| !AhConstantUtil.isTrueAll(Device.IS_DUALBAND, apModelType)) {
				if (RadioProfile.RADIO_PROFILE_MODE_A == radioType || RadioProfile.RADIO_PROFILE_MODE_NA == radioType
						 || RadioProfile.RADIO_PROFILE_MODE_AC == radioType) {
					jsonObject.put("listId", "hiveAp_wifi1RadioProfile");
				} else {
					jsonObject.put("listId", "hiveAp_wifi0RadioProfile");
				}
			} else {
				jsonObject.put("listId", "hiveAp_wifi0RadioProfile");
			}
		} else {
			if (RadioProfile.RADIO_PROFILE_MODE_A == radioType) {
				jsonObject.put("listId", "hiveAp_wifi1RadioProfile");
			} else if (RadioProfile.RADIO_PROFILE_MODE_BG == radioType){
				jsonObject.put("listId", "hiveAp_wifi0RadioProfile");
			}
		}
	}

	private void updateWifi0RadioProfile() throws Exception {
		Long id = getDataSource().getTempWifi0RadioProfile().getId();
		boolean defaultFlag = getDataSource().getTempWifi0RadioProfile()
				.getDefaultFlag();
		if (null != id && !defaultFlag) {
			BoMgmt.updateBo(getDataSource().getTempWifi0RadioProfile(),
					getUserContext(), getSelectedL2FeatureKey());
			short mode = getDataSource().getTempWifi0RadioProfile()
					.getRadioMode();
			if (mode == RadioProfile.RADIO_PROFILE_MODE_BG
					|| mode == RadioProfile.RADIO_PROFILE_MODE_NG) {
				getDataSource().setWifi0RadioProfile(
						getDataSource().getTempWifi0RadioProfile());
			}
		}
		getDataSource().setTempWifi0RadioProfile(new RadioProfile());
		getDataSource().setTempWifi0RadioProfileCreateDisplayStyle("none");
	}

	private void clearWifi0RadioProfile() {
		getDataSource().setTempWifi0RadioProfile(new RadioProfile());
		getDataSource().setTempWifi0RadioProfileCreateDisplayStyle("none");
	}

	private void createWifi1RadioProfile() throws Exception {
		if (checkNameExists("radioName", getDataSource()
				.getTempWifi1RadioProfile().getRadioName(), RadioProfile.class)) {
			return;
		}
		RadioProfile profile = getDataSource().getTempWifi1RadioProfile();
		profile.setWmmItems(RadioProfileAction.getDefaultWmmInfo(profile));
		Long newId = BoMgmt.createBo(getDataSource().getTempWifi1RadioProfile(),
				getUserContext(), getSelectedL2FeatureKey());
		getDataSource().setWifi1RadioProfile(
				getDataSource().getTempWifi1RadioProfile());
		getDataSource().setTempWifi1RadioProfile(new RadioProfile());
		getDataSource().setTempWifi1RadioProfileCreateDisplayStyle("none");

		if (this.isJsonMode()) {
			prepareJsonObjForRadioProfile(newId, profile.getRadioName(), 1, profile.getRadioMode());
		}
	}

	private void updateWifi1RadioProfile() throws Exception {
		Long id = getDataSource().getTempWifi1RadioProfile().getId();
		boolean defaultFlag = getDataSource().getTempWifi1RadioProfile()
				.getDefaultFlag();
		if (null != id && !defaultFlag) {
			BoMgmt.updateBo(getDataSource().getTempWifi1RadioProfile(),
					getUserContext(), getSelectedL2FeatureKey());
			short mode = getDataSource().getTempWifi1RadioProfile()
					.getRadioMode();
			if (mode == RadioProfile.RADIO_PROFILE_MODE_A
					|| mode == RadioProfile.RADIO_PROFILE_MODE_NA
					|| mode == RadioProfile.RADIO_PROFILE_MODE_AC) {
				getDataSource().setWifi1RadioProfile(
						getDataSource().getTempWifi1RadioProfile());
			}
		}
		getDataSource().setTempWifi1RadioProfile(new RadioProfile());
		getDataSource().setTempWifi1RadioProfileCreateDisplayStyle("none");
	}

	private void clearWifi1RadioProfile() {
		getDataSource().setTempWifi1RadioProfile(new RadioProfile());
		getDataSource().setTempWifi1RadioProfileCreateDisplayStyle("none");
	}

	private void resetTempRadioProfile() {
		getDataSource().setTempWifi0RadioProfile(new RadioProfile());
		getDataSource().setTempWifi0RadioProfileCreateDisplayStyle("none");
		getDataSource().setTempWifi1RadioProfile(new RadioProfile());
		getDataSource().setTempWifi1RadioProfileCreateDisplayStyle("none");
	}

	protected void updateStaticRoutes() {
		if (null != getDataSource() && null != interfaceTypes) {
			for (int i = 0; i < interfaceTypes.length; i++) {
				if (i < getDataSource().getStaticRoutes().size()) {
					getDataSource().getStaticRoutes().get(i).setInterfaceType(
							interfaceTypes[i]);
				}
			}
		}
	}

	protected void setWifiRadioMode(HiveAp hiveAp) {
		HiveAp.setHiveApRadioModes(hiveAp);
	}

	protected void addNewStaticRoute() throws Exception {
		jsonObject = new JSONObject();
		if (destinationMac == null || nextHopMac == null) {
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", "Please fill in \"Destination MAC\" and \"Next Hop MAC\".");
			return;
		}
		HiveApStaticRoute staticRoute = new HiveApStaticRoute();
		staticRoute.setDestinationMac(destinationMac);
		staticRoute.setNextHopMac(nextHopMac);
		staticRoute.setInterfaceType(interfaceType);
		getDataSource().getStaticRoutes().add(staticRoute);
		jsonObject.put("resultStatus", true);
		jsonObject.put("itemId", getDataSource().getStaticRoutes().size() - 1);
		jsonObject.put("destinationMac", staticRoute.getDestinationMac());
		jsonObject.put("nextHopMac", staticRoute.getNextHopMac());
		jsonObject.put("interfaceType", staticRoute.getInterfaceType());
		jsonObject.put("gridCount", getDataSource().getStaticRoutes().size());
	}

	protected void addNewDynamicRoute() throws Exception {
		jsonObject = new JSONObject();
		if (neighborMac == null) {
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", "Please fill in \"Neighbor MAC\".");
			return;
		}
		HiveApDynamicRoute dynamicRoute = new HiveApDynamicRoute();
		dynamicRoute.setNeighborMac(neighborMac);
		dynamicRoute.setRouteMaximun(routeMaximun);
		dynamicRoute.setRouteMinimun(routeMinimun);
		getDataSource().getDynamicRoutes().add(dynamicRoute);
		neighborMac = "";
		routeMaximun = 67;
		routeMinimun = 67;
		jsonObject.put("resultStatus", true);
		jsonObject.put("itemId", getDataSource().getDynamicRoutes().size() - 1);
		jsonObject.put("neighborMac", dynamicRoute.getNeighborMac());
		jsonObject.put("routeMaximun", dynamicRoute.getRouteMaximun());
		jsonObject.put("routeMinimun", dynamicRoute.getRouteMinimun());
		jsonObject.put("gridCount", getDataSource().getDynamicRoutes().size());
	}

	protected void addNewRoutingProfileStaticRoutes() throws JSONException{
		jsonObject = new JSONObject();
		if (staticRouteIpInput == null || staticRouteMaskInput == null
				|| staticRouteGwInput == null) {
			jsonObject.put("t", false);
			jsonObject.put("errMsg", "Please fill in \"Destination IP\" and \"Netmask\" and \"Gateway\".");
			return;
		}

		String start = CLICommonFunc.countIpAndMask(staticRouteIpInput,
				staticRouteMaskInput);
		List<HiveApIpRoute> current = getDataSource().getIpRoutes();
		if (null != current && !current.isEmpty()) {
			for (HiveApIpRoute route : current) {
				if (start.equals(route.getSourceIp())) {
					route.setGateway(staticRouteGwInput);
					jsonObject.put("t", false);
					jsonObject.put("errMsg", "Duplicate IP.");
					return;
				}
			}
		}

		HiveApIpRoute ipRoute = new HiveApIpRoute();
		ipRoute.setSourceIp(start);
		ipRoute.setNetmask(staticRouteMaskInput);
		ipRoute.setGateway(staticRouteGwInput);
		ipRoute.setDistributeBR(this.distributeNet);

		getDataSource().getIpRoutes().add(ipRoute);

		jsonObject = new JSONObject();
		jsonObject.put("t",true);
		jsonObject.put("operation", "addStaticRoute");
		jsonObject.put("staticRoute", getStaticRouteInfoForHtml());
		jsonObject.put("gridCount", getDataSource().getIpRoutes().size());

	}

	private boolean validateBrGateway(String gateway,int index,boolean brSameSubnet) throws JSONException{
		if(isConfigured()){
			List<SubNetworkResource> subResources ;
			String mac = getDataSource().getMacAddress();
			if(mac == null || mac.isEmpty()){
				jsonObject.put("resultStatus", false);
				jsonObject.put("errMsg", "Mac address is empty.");
				return false || brSameSubnet;
			} else {
				subResources = QueryUtil.executeQuery(SubNetworkResource.class, null, new FilterParams("lower(hiveapmac)",mac.toLowerCase()),getDomainId(),new HiveApAction());
				if (subResources.isEmpty()) {
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg", "Sub network is not find.");
					return false || brSameSubnet;
				}
			}
			long lGateway = AhEncoder.ip2Long(gateway);
			boolean isInIpNetwork = false;
			boolean isInDhcpPoll = false;
			for(SubNetworkResource sResource :subResources){
				
				String localNetwork = sResource.getLocalNetwork();
				int mask = Integer.valueOf(localNetwork.split("/")[1]);
				long allNum = (long) Math.pow(2, 32 - mask);
				long startip = AhEncoder.ip2Long(getIpFromNetwork(localNetwork, 0));
				long endip = AhEncoder.ip2Long(getIpFromNetwork(localNetwork, allNum)) - 1;
				 
				if(!isInIpNetwork){
					if(startip<lGateway && lGateway<=endip){
						isInIpNetwork = true;
					}
				}
				
				boolean enableDhcp = false;
				if(sResource.getVpnNetwork() != null){
					List<VpnNetworkSub> vpnNetworkSubList= sResource.getVpnNetwork().getSubItems();
					for(VpnNetworkSub sub : vpnNetworkSubList){
						String ipNetywork = sub.getIpNetwork();
						String parentNetwork = sResource.getParentNetwork();
						String localIpNetwork = sub.getLocalIpNetwork();
						String parentLoaclNetwork = sResource.getParentLocalNetwork();
						if(ipNetywork != null && parentNetwork != null
							&& localIpNetwork != null && parentLoaclNetwork != null
							&& ipNetywork.equals(parentNetwork) && localIpNetwork.equals(parentLoaclNetwork)){
								enableDhcp = sub.isEnableDhcp();
								break;
						}
					}
				}
				
				if(enableDhcp){
					if (!isInDhcpPoll) {
						if(AhEncoder.ip2Long(sResource.getIpPoolStart())<=lGateway && lGateway<=AhEncoder.ip2Long(sResource.getIpPoolEnd())){
							isInDhcpPoll = true;
						}
					}
				}
			}

			if(!isInIpNetwork){
				jsonObject.put("resultStatus", false);
				jsonObject.put("focusName", "brStaticRouteGwInput");
				jsonObject.put("errMsg", "Gateway IP address is invalid on this router.");
				jsonObject.put("errFlag", 1);
				return false || brSameSubnet;
			}

			if(isInDhcpPoll){
				jsonObject.put("resultStatus", false);
				jsonObject.put("focusName", "brStaticRouteGwInput");
				jsonObject.put("errMsg", "Gateway IP address cannot be in the DHCP pool of the interfaces on the router.");
				jsonObject.put("errFlag", 2);
				return false || brSameSubnet;
			}
		}
		return true;
	}
	
	private String getIpFromNetwork(String network, long index){
		int flagIndex = network.indexOf("/");
		String ipAddr = network.substring(0, flagIndex);
		//  String mask = network.substring(flagIndex+1);
		long ipNum = AhEncoder.ip2Long(ipAddr);
		long resIpNum = ipNum + index;
		return AhDecoder.long2Ip(resIpNum);
	}

	private String getIpAddressAndNetmaskFromDeviceInterface(DeviceInterface deviceInterface){
		String ipAddress = null;
		if(null != deviceInterface){
			if("1".equals(deviceInterface.getConnectionType())){// for DHCP
				// fix bug 22690
				String searchSQL = "lower(mac)=:s1 AND interfType=:s2 ";
				Object values[] = new Object[2];
				values[0] = getDataSource().getMacAddress().toLowerCase();
				values[1] = AhPortAvailability.INTERFACE_TYPE_WAN;
				List<AhPortAvailability> lstPortInfo = QueryUtil.executeQuery(AhPortAvailability.class, null, new FilterParams(searchSQL, values), getDomainId());
				if(lstPortInfo != null){
					for(AhPortAvailability port :lstPortInfo){
						String interfName = StringUtils.lowerCase(port.getInterfName());
						if(getDataSource().isSwitchProduct()){
							if(deviceInterface.getInterfaceName() != null && deviceInterface.getInterfaceName().equalsIgnoreCase(interfName)){
								if(port.getWanipaddress() != 0 && port.getWannetmask() != 0){
									ipAddress = AhDecoder.int2IP(port.getWanipaddress()) + "/" + AhEncoder.netmask2int(AhDecoder.int2IP(port.getWannetmask()));
									break;
								}
							}
						} else {
							String portInterName = "";
							if(deviceInterface.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_ETH0){
								portInterName = MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth0");
							} else if(deviceInterface.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_ETH1){
								portInterName = MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth1");
							} else if(deviceInterface.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_ETH2){
								portInterName = MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth2");
							} else if(deviceInterface.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_ETH3){
								portInterName = MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth3");
							} else if(deviceInterface.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_ETH4){
								portInterName = MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth4");
							} else if(deviceInterface.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_USB){
								portInterName = MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.usb");
							} 
							if(portInterName != null && portInterName.equalsIgnoreCase(interfName)){
								if(port.getWanipaddress() != 0 && port.getWannetmask() != 0){
									ipAddress = AhDecoder.int2IP(port.getWanipaddress()) + "/" + AhEncoder.netmask2int(AhDecoder.int2IP(port.getWannetmask()));
									break;
								}
							}
						}
					}
				}
				
			} else if("2".equals(deviceInterface.getConnectionType())){ // Static
				ipAddress =  deviceInterface.getIpAddress() + "/" + AhEncoder.netmask2int(deviceInterface.getNetMask());
			}
		}
		return ipAddress;
	}
	
	// return x.x.x.x/x
	private List<String> getWanIpAddressAndNetmask(){
		List<String> wanIpAddressList = new ArrayList<>();
		if(getDataSource().isBranchRouter()){
			if(getDataSource().isSwitchProduct()){
				if(null != switchWanPortSettings){
					for(DeviceInterface deviceInterface : switchWanPortSettings){
						String wanIp = getIpAddressAndNetmaskFromDeviceInterface(deviceInterface);
						if(null != wanIp){
							wanIpAddressList.add(wanIp);
						}
					}
				}
			} else {
				if(true){ // flageth0
					String wanIp = getIpAddressAndNetmaskFromDeviceInterface(branchRouterEth0);
					if(null != wanIp){
						wanIpAddressList.add(wanIp);
					}
				}
				if(flageth1){
					String wanIp = getIpAddressAndNetmaskFromDeviceInterface(branchRouterEth1);
					if(null != wanIp){
						wanIpAddressList.add(wanIp);
					}
				}
				if(flageth2){
					String wanIp = getIpAddressAndNetmaskFromDeviceInterface(branchRouterEth2);
					if(null != wanIp){
						wanIpAddressList.add(wanIp);
					}
				}
				if(flageth3){
					String wanIp = getIpAddressAndNetmaskFromDeviceInterface(branchRouterEth3);
					if(null != wanIp){
						wanIpAddressList.add(wanIp);
					}
				}
				if(flageth4){
					String wanIp = getIpAddressAndNetmaskFromDeviceInterface(branchRouterEth4);
					if(null != wanIp){
						wanIpAddressList.add(wanIp);
					}
				}
				if(flagusb0){
					String wanIp = getIpAddressAndNetmaskFromDeviceInterface(branchRouterUSB);
					if(null != wanIp){
						wanIpAddressList.add(wanIp);
					}
				}
				if(getDataSource().getRadioConfigType() == HiveAp.RADIO_MODE_ACCESS_WAN){ //wifi0
					String wanIp = getIpAddressAndNetmaskFromDeviceInterface(branchRouterWifi0);
					if(null != wanIp){
						wanIpAddressList.add(wanIp);
					}
				}
			}
		}
		
		return wanIpAddressList;
	}
	
	private boolean checkWanIpAndGatewayInSameNetwork(String wanIpAndNetmask,String gateway){
		if(wanIpAndNetmask == null || wanIpAndNetmask.isEmpty()
				|| gateway == null || gateway.isEmpty()){
			return false;
		}
		try{
			String ipStr = wanIpAndNetmask.substring(0, wanIpAndNetmask.indexOf("/"));
			String maskInt = wanIpAndNetmask.substring(wanIpAndNetmask.indexOf("/")+1);
			String mask = AhDecoder.int2Netmask(Integer.valueOf(maskInt));
			return MgrUtil.checkIpInSameSubnet(ipStr,gateway,mask);
		} catch(Exception e){
			return false;
		}
	}
	
	private String getIfName(String ifNum){
		String ifName = "";
		if(getDataSource().isSwitchProduct()){
			ifName = StringUtils.lowerCase(MgrUtil
					.getEnumString("enum.switch.interface."+ifNum));
		} else {
			ifName = StringUtils.lowerCase(MgrUtil
					.getUserMessage("hiveAp.autoProvisioning.device.if.port."+ifNum));
		}
		return ifName;
	}
	
	private List<String> getWanIpAddressAndNetmask(String[] wanIfConnTypes, String[] wanIfIpAndNetmasks){
		List<String> wanIpAddressList = new ArrayList<>();
		if(wanIfConnTypes != null){
			for(int i=0; i< wanIfConnTypes.length;i++){
				if("1".equals(wanIfConnTypes[i])){//dhcp
					// fix bug 22690
					String searchSQL = "lower(mac)=:s1 AND interfType=:s2 ";
					Object values[] = new Object[2];
					values[0] = getDataSource().getMacAddress().toLowerCase();
					values[1] = AhPortAvailability.INTERFACE_TYPE_WAN;
					List<AhPortAvailability> lstPortInfo = QueryUtil.executeQuery(AhPortAvailability.class, null, new FilterParams(searchSQL, values), getDomainId());
					if(lstPortInfo != null){
						for(AhPortAvailability port :lstPortInfo){
							String interfName = StringUtils.lowerCase(port.getInterfName());
							String wanIfName = getIfName(wanIfNums[i]);
							if(wanIfName != null && wanIfName.equalsIgnoreCase(interfName)){
								if(port.getWanipaddress() != 0 && port.getWannetmask() != 0){
									wanIpAddressList.add(AhDecoder.int2IP(port.getWanipaddress()) + "/" + AhEncoder.netmask2int(AhDecoder.int2IP(port.getWannetmask())));
									break;
								}
							}
						}
					}
				} else if("2".equals(wanIfConnTypes[i])){
					wanIpAddressList.add(wanIfIpAndNetmasks[i]);
				}
			}
		}
		
		return wanIpAddressList;
	}
	
	protected void addNewBrStaticRouting() throws JSONException{
		jsonObject = new JSONObject();
		if (brStaticRouteIpInput == null || brStaticRouteMaskInput == null || brStaticRouteGwInput == null) {
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", "Please fill in \"Destination IP\" and \"Netmask\" and \"Gateway\".");
			return;
		}

		//fix bug 25486
		if(!isConfigured()){
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", "Static Routes could not be configured becuase a network policy has not yet been uploaded to this device.");
			return;
		}
		
		List<String> wanIpAddressList = getWanIpAddressAndNetmask(wanIfConnTypes,wanIfIpAndNetmasks);

		boolean isSameSubnet = false;
		for(String ipAndNetmask : wanIpAddressList){
			isSameSubnet = checkWanIpAndGatewayInSameNetwork(ipAndNetmask,brStaticRouteGwInput);
			if(isSameSubnet){
				break;
			}
		}
		// validate gateway
		if(!validateBrGateway(brStaticRouteGwInput,0,isSameSubnet)){
			return;
		}

		//fix bug 16790
		String start = CLICommonFunc.countIpAndMask(brStaticRouteIpInput,
				brStaticRouteMaskInput);
		List<HiveApIpRoute> current = getDataSource().getIpRoutes();
		if (null != current && !current.isEmpty()) {
			for (HiveApIpRoute route : current) {
				if (start.equals(route.getSourceIp())) {
					route.setGateway(brStaticRouteGwInput);
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg", "Duplicate IP.");
					return;
				}
			}
		}

		HiveApIpRoute ipRoute = new HiveApIpRoute();
		ipRoute.setSourceIp(start);
		ipRoute.setNetmask(brStaticRouteMaskInput);
		ipRoute.setGateway(brStaticRouteGwInput);
		ipRoute.setAdvertiseCvg(advertiseCvg);

		getDataSource().getIpRoutes().add(ipRoute);

		jsonObject.put("resultStatus", true);
		jsonObject.put("itemId", getDataSource().getIpRoutes().size() - 1);
		jsonObject.put("ip", ipRoute.getSourceIp());
		jsonObject.put("netmask", ipRoute.getNetmask());
		jsonObject.put("gateway", ipRoute.getGateway());
		jsonObject.put("advertiseCvg", ipRoute.isAdvertiseCvg()? "checked='checked' " : "");
		jsonObject.put("gridCount", getDataSource().getIpRoutes().size());
	}

	protected void addNewCVGIntNetwork() throws JSONException{
		jsonObject = new JSONObject();
		if (interNetIpInput == null || interNetMaskInput == null) {
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", "Please fill in \"Network\" and \"Netmask\".");
			return;
		}
		String network = MgrUtil.getStartIpAddressValue(interNetIpInput, interNetMaskInput);

		HiveApInternalNetwork intNetwork = new HiveApInternalNetwork();
		intNetwork.setInternalNetwork(network);
		intNetwork.setNetmask(interNetMaskInput);
		for(HiveApInternalNetwork item : getDataSource().getInternalNetworks()){
			if(intNetwork.getInternalNetwork().equals(item.getInternalNetwork()) &&
					intNetwork.getNetmask().equals(item.getNetmask())){
				jsonObject.put("resultStatus", false);
				jsonObject.put("errMsg", "Network \"" + network + "\" and Netmask \""+interNetMaskInput+"\" has exists.");
				return;
			}
		}

		getDataSource().getInternalNetworks().add(intNetwork);
		jsonObject.put("resultStatus", true);
		jsonObject.put("itemId", getDataSource().getInternalNetworks().size() - 1);
		jsonObject.put("network", intNetwork.getInternalNetwork());
		jsonObject.put("netmask", intNetwork.getNetmask());
		jsonObject.put("gridCount", getDataSource().getInternalNetworks().size());
	}

	protected void removeSelectedRoutingProfileStaticRoutes(){
		if (routingProfilesStaticRoutesIndices != null) {
			Collection<HiveApIpRoute> removeList = new Vector<HiveApIpRoute>();
			for (String ipRouteIndex : routingProfilesStaticRoutesIndices) {
				try {
					int index = Integer.parseInt(ipRouteIndex);
					if (index < getDataSource().getIpRoutes().size()) {
						removeList
								.add(getDataSource().getIpRoutes().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getIpRoutes().removeAll(removeList);
		}
	}

	protected void removeSelectedBrStaticRouting(){
		if (brStaticRouteingIndices != null) {
			Collection<HiveApIpRoute> removeList = new Vector<HiveApIpRoute>();
			for (String brStaticRouteingIndex : brStaticRouteingIndices) {
				try {
					int index = Integer.parseInt(brStaticRouteingIndex);
					if (index < getDataSource().getIpRoutes().size()) {
						removeList.add(getDataSource().getIpRoutes().get(
								index));
					}
				} catch (NumberFormatException e) {
					return;
				}
			}
			getDataSource().getIpRoutes().removeAll(removeList);
		}
		jsonArray = new JSONArray();
		int itemId = 0;
		int recCount = getDataSource().getIpRoutes().size();
		try {
			for(HiveApIpRoute item : getDataSource().getIpRoutes()) {
				jsonObject = new JSONObject();

				jsonObject.put("itemId", itemId++);
				jsonObject.put("ip", item.getSourceIp());
				jsonObject.put("netmask", item.getNetmask());
				jsonObject.put("gateway", item.getGateway());
				jsonObject.put("advertiseCvg", item.isAdvertiseCvg()? "checked='checked' " : "");
				jsonObject.put("gridCount", recCount);

				jsonArray.put(jsonObject);
			}
		} catch (JSONException e) {
		}
	}

	protected void removeSelectedIntNetwork(){
		if (intNetworkIndices != null) {
			Collection<HiveApInternalNetwork> removeList = new Vector<HiveApInternalNetwork>();
			for (String intNetworkIndex : intNetworkIndices) {
				try {
					int index = Integer.parseInt(intNetworkIndex);
					if (index < getDataSource().getInternalNetworks().size()) {
						removeList.add(getDataSource().getInternalNetworks().get(
								index));
					}
				} catch (NumberFormatException e) {
					return;
				}
			}
			getDataSource().getInternalNetworks().removeAll(removeList);
		}

		jsonArray = new JSONArray();
		int itemId = 0;
		int recCount = getDataSource().getInternalNetworks().size();
		try {
			for(HiveApInternalNetwork item : getDataSource().getInternalNetworks()) {
				jsonObject = new JSONObject();

				jsonObject.put("itemId", itemId++);
				jsonObject.put("network", item.getInternalNetwork());
				jsonObject.put("netmask", item.getNetmask());
				jsonObject.put("gridCount", recCount);

				jsonArray.put(jsonObject);
			}
		} catch (JSONException e) {
		}
	}

	protected void addNewIpRoute() throws Exception {
		jsonObject = new JSONObject();
		if (ipRouteIpInput == null || ipRouteMaskInput == null
				|| ipRouteGwInput == null) {
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", "Please fill in \"Destination IP\", \"Netmask\" and \"Gateway\".");
			return;
		}
		String start = CLICommonFunc.countIpAndMask(ipRouteIpInput,
				ipRouteMaskInput);
		List<HiveApIpRoute> current = getDataSource().getIpRoutes();
		if (null != current && !current.isEmpty()) {
			for (HiveApIpRoute route : current) {
				if (start.equals(route.getSourceIp())) {
					route.setGateway(ipRouteGwInput);
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg", "Duplicate IP.");
					return;
				}
			}
		}
		HiveApIpRoute ipRoute = new HiveApIpRoute();
		ipRoute.setSourceIp(start);
		ipRoute.setNetmask(ipRouteMaskInput);
		ipRoute.setGateway(ipRouteGwInput);
		getDataSource().getIpRoutes().add(ipRoute);
		jsonObject.put("resultStatus", true);
		jsonObject.put("itemId", getDataSource().getIpRoutes().size() - 1);
		jsonObject.put("sourceIp", ipRoute.getSourceIp());
		jsonObject.put("netmask", ipRoute.getNetmask());
		jsonObject.put("gateway", ipRoute.getGateway());
		jsonObject.put("gridCount", getDataSource().getIpRoutes().size());
	}

	protected void removeSelectedIpRoutes() {
		if (ipRouteIndices != null) {
			Collection<HiveApIpRoute> removeList = new Vector<HiveApIpRoute>();
			for (String ipRouteIndex : ipRouteIndices) {
				try {
					int index = Integer.parseInt(ipRouteIndex);
					if (index < getDataSource().getIpRoutes().size()) {
						removeList
								.add(getDataSource().getIpRoutes().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getIpRoutes().removeAll(removeList);
		}

		jsonArray = new JSONArray();
		int itemId = 0;
		int recCount = getDataSource().getIpRoutes().size();
		try {
			for(HiveApIpRoute item : getDataSource().getIpRoutes()) {
				jsonObject = new JSONObject();

				jsonObject.put("itemId", itemId++);
				jsonObject.put("gridCount", recCount);
				jsonObject.put("sourceIp", item.getSourceIp());
				jsonObject.put("netmask", item.getNetmask());
				jsonObject.put("gateway", item.getGateway());

				jsonArray.put(jsonObject);
			}
		} catch (JSONException e) {
		}
	}

	protected void addNewMultipleVlan() throws Exception {
		jsonObject = new JSONObject();
		if (multiplevlanInput == null) {
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", "Please fill in \"VLAN ID\".");
			return;
		}
		List<HiveApMultipleVlan> current = getDataSource().getMultipleVlan();
		if (null != current && !current.isEmpty()) {
			for (HiveApMultipleVlan multivlanId : current) {
				if (multiplevlanInput.equals(multivlanId.getVlanid())) {
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg", "Duplicate VLAN ID.");
					return;
				}
			}
		}
		HiveApMultipleVlan multivlanId = new HiveApMultipleVlan();
		multivlanId.setVlanid(multiplevlanInput);
		getDataSource().getMultipleVlan().add(multivlanId);
		jsonObject.put("resultStatus", true);
		jsonObject.put("itemId", getDataSource().getMultipleVlan().size() - 1);
		jsonObject.put("vlanId", multivlanId.getVlanid());
		jsonObject.put("gridCount", getDataSource().getMultipleVlan().size());
	}

	protected void removeSelectedMultipleVlan() {
		if (multiplevlanIndices != null) {
			Collection<HiveApMultipleVlan> removeList = new Vector<HiveApMultipleVlan>();
			for (String multiplevlanIndex : multiplevlanIndices) {
				try {
					int index = Integer.parseInt(multiplevlanIndex);
					if (index < getDataSource().getMultipleVlan().size()) {
						removeList.add(getDataSource().getMultipleVlan().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getMultipleVlan().removeAll(removeList);
		}

		jsonArray = new JSONArray();
		int itemId = 0;
		int recCount = getDataSource().getMultipleVlan().size();
		try {
			for(HiveApMultipleVlan item : getDataSource().getMultipleVlan()) {
				jsonObject = new JSONObject();

				jsonObject.put("itemId", itemId++);
				jsonObject.put("gridCount", recCount);
				jsonObject.put("vlanId", item.getVlanid());

				jsonArray.put(jsonObject);
			}
		} catch (JSONException e) {
		}
	}

	protected void addNewVirtualConnect() throws Exception {
		jsonObject = new JSONObject();
		if (virtualConnectName == null) {
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", "Please fill in \"Name\".");
			return;
		}
		List<HiveAPVirtualConnection> current = getDataSource().getVirtualConnections();
		if (current != null && current.size() >= 50) {
			//addActionError("You have reached the maximum number (50) of static packet-forwarding rules that you can create.");
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg", "You have reached the maximum number (50) of static packet-forwarding rules that you can create.");
			return;
		}

		if (HiveAp.is110HiveAP(getDataSource().getHiveApModel())) {
			if (virtualConnectInterface_in == HiveAPVirtualConnection.INTERFACE_AGG0
					|| virtualConnectInterface_in == HiveAPVirtualConnection.INTERFACE_RED0
					|| virtualConnectInterface_in == HiveAPVirtualConnection.INTERFACE_WIFI1
					|| virtualConnectInterface_in == HiveAPVirtualConnection.INTERFACE_ETH1
					|| virtualConnectInterface_out == HiveAPVirtualConnection.INTERFACE_AGG0
					|| virtualConnectInterface_out == HiveAPVirtualConnection.INTERFACE_RED0
					|| virtualConnectInterface_out == HiveAPVirtualConnection.INTERFACE_WIFI1
					|| virtualConnectInterface_out == HiveAPVirtualConnection.INTERFACE_ETH1) {
				//addActionError("The eth1, wifi1, agg0, and red0 interfaces are not available on "+NmsUtil.getOEMCustomer().getAccessPonitName()+" 110 series devices.");
				jsonObject.put("resultStatus", false);
				jsonObject.put("errMsg", "The eth1, wifi1, agg0, and red0 interfaces are not available on "+NmsUtil.getOEMCustomer().getAccessPonitName()+" 110 series devices.");
				return;
			}
		}

		if (HiveAp.is120HiveAP(getDataSource().getHiveApModel())) {
			if (virtualConnectInterface_in == HiveAPVirtualConnection.INTERFACE_AGG0
					|| virtualConnectInterface_in == HiveAPVirtualConnection.INTERFACE_RED0
					|| virtualConnectInterface_in == HiveAPVirtualConnection.INTERFACE_ETH1
					|| virtualConnectInterface_out == HiveAPVirtualConnection.INTERFACE_AGG0
					|| virtualConnectInterface_out == HiveAPVirtualConnection.INTERFACE_RED0
					|| virtualConnectInterface_out == HiveAPVirtualConnection.INTERFACE_ETH1) {
				//addActionError("The eth1, agg0, and red0 interfaces are not available on "+NmsUtil.getOEMCustomer().getAccessPonitName()+" 120 series devices.");
				jsonObject.put("resultStatus", false);
				jsonObject.put("errMsg", "The eth1, agg0, and red0 interfaces are not available on "+NmsUtil.getOEMCustomer().getAccessPonitName()+" 120 series devices.");
				return;
			}
		}

		if (null != current && !current.isEmpty()) {
			for (HiveAPVirtualConnection connection : current) {
				if (virtualConnectName.equalsIgnoreCase(connection.getForwardName())) {
					//addActionError("The same entry name(" + virtualConnectName + ") exists.");
					jsonObject.put("resultStatus", false);
					jsonObject.put("errMsg", "The same entry name (" + virtualConnectName + ") exists.");
					return;
				}
			}
		}
		HiveAPVirtualConnection connection = new HiveAPVirtualConnection();
		connection.setDestMac(virtualConnectDestMac);
		connection.setForwardAction(virtualConnectAction);
		connection.setForwardName(virtualConnectName);
		connection.setInterface_in(virtualConnectInterface_in);
		connection.setInterface_out(virtualConnectInterface_out);
		connection.setRxMac(virtualConnectRxMac);
		connection.setSourceMac(virtualConnectSourceMac);
		connection.setTxMac(virtualConnectTxMac);
		getDataSource().getVirtualConnections().add(connection);
		jsonObject.put("resultStatus", true);
		jsonObject.put("itemId", getDataSource().getVirtualConnections().size() - 1);
		jsonObject.put("virtualConnectDestMac",virtualConnectDestMac);
		jsonObject.put("virtualConnectAction",virtualConnectAction);
		jsonObject.put("virtualConnectName",virtualConnectName);
		jsonObject.put("virtualConnectInterface_in",virtualConnectInterface_in);
		jsonObject.put("virtualConnectInterface_out",virtualConnectInterface_out);
		jsonObject.put("virtualConnectAction_str",connection.getForwardAction4Display());
		jsonObject.put("virtualConnectInterface_in_str",connection.getInterface_in4Display());
		jsonObject.put("virtualConnectInterface_out_str",connection.getInterface_out4Display());
		if(null != virtualConnectRxMac){
			jsonObject.put("virtualConnectRxMac",virtualConnectRxMac);
		}else{
			jsonObject.put("virtualConnectRxMac","");
		}
		jsonObject.put("virtualConnectSourceMac",virtualConnectSourceMac);
		if(null != virtualConnectTxMac){
			jsonObject.put("virtualConnectTxMac",virtualConnectTxMac);
		}else{
			jsonObject.put("virtualConnectTxMac","");
		}
	}

	protected void removeSelectedVirtualConnect() {
		if (virtualConnectIndices != null) {
			Collection<HiveAPVirtualConnection> removeList = new Vector<HiveAPVirtualConnection>();
			for (String virtualConnectIndex : virtualConnectIndices) {
				try {
					int index = Integer.parseInt(virtualConnectIndex);
					if (index < getDataSource().getVirtualConnections().size()) {
						removeList.add(getDataSource().getVirtualConnections().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getVirtualConnections().removeAll(removeList);
		}
		jsonArray = new JSONArray();
		int itemId = 0;
		int recCount = getDataSource().getVirtualConnections().size();
		try {
			for(HiveAPVirtualConnection item : getDataSource().getVirtualConnections()) {
				jsonObject = new JSONObject();
				jsonObject.put("itemId", itemId++);
				jsonObject.put("gridCount", recCount);
				/*connection.setDestMac(virtualConnectDestMac);
				connection.setForwardAction(virtualConnectAction);
				connection.setForwardName(virtualConnectName);
				connection.setInterface_in(virtualConnectInterface_in);
				connection.setInterface_out(virtualConnectInterface_out);
				connection.setRxMac(virtualConnectRxMac);
				connection.setSourceMac(virtualConnectSourceMac);
				connection.setTxMac(virtualConnectTxMac);*/
				jsonObject.put("virtualConnectDestMac",item.getDestMac());
				jsonObject.put("virtualConnectAction",item.getForwardAction());
				jsonObject.put("virtualConnectName",item.getForwardName());
				jsonObject.put("virtualConnectInterface_in",item.getInterface_in());
				jsonObject.put("virtualConnectInterface_out",item.getInterface_out());
				jsonObject.put("virtualConnectAction_str",item.getForwardAction4Display());
				jsonObject.put("virtualConnectInterface_in_str",item.getInterface_in4Display());
				jsonObject.put("virtualConnectInterface_out_str",item.getInterface_out4Display());
				
				jsonObject.put("virtualConnectRxMac",item.getRxMac());
				
				jsonObject.put("virtualConnectSourceMac",item.getSourceMac());
				
				jsonObject.put("virtualConnectTxMac",item.getTxMac());

				jsonArray.put(jsonObject);
			}
		} catch (JSONException e) {
		}
	}

	protected void removeSelectedStaticRoutes() {
		if (staticRouteIndices != null) {
			Collection<HiveApStaticRoute> removeList = new Vector<HiveApStaticRoute>();
			for (String staticRouteIndex : staticRouteIndices) {
				try {
					int index = Integer.parseInt(staticRouteIndex);
					if (index < getDataSource().getStaticRoutes().size()) {
						removeList.add(getDataSource().getStaticRoutes().get(
								index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getStaticRoutes().removeAll(removeList);
		}

		jsonArray = new JSONArray();
		int itemId = 0;
		int recCount = getDataSource().getStaticRoutes().size();
		try {
			for(HiveApStaticRoute item : getDataSource().getStaticRoutes()) {
				jsonObject = new JSONObject();

				jsonObject.put("itemId", itemId++);
				jsonObject.put("gridCount", recCount);
				jsonObject.put("destinationMac", item.getDestinationMac());
				jsonObject.put("nextHopMac", item.getNextHopMac());
				jsonObject.put("interfaceType", item.getInterfaceType());

				jsonArray.put(jsonObject);
			}
		} catch (JSONException e) {
		}
	}

	protected void removeSelectedDynamicRoutes() {
		if (dynamicRouteIndices != null) {
			Collection<HiveApDynamicRoute> removeList = new Vector<HiveApDynamicRoute>();
			for (String dynamicRouteIndex : dynamicRouteIndices) {
				try {
					int index = Integer.parseInt(dynamicRouteIndex);
					if (index < getDataSource().getDynamicRoutes().size()) {
						removeList.add(getDataSource().getDynamicRoutes().get(
								index));
					}
				} catch (NumberFormatException e) {
					return;
				}
			}
			getDataSource().getDynamicRoutes().removeAll(removeList);
		}

		jsonArray = new JSONArray();
		int itemId = 0;
		int recCount = getDataSource().getDynamicRoutes().size();
		try {
			for(HiveApDynamicRoute item : getDataSource().getDynamicRoutes()) {
				jsonObject = new JSONObject();

				jsonObject.put("itemId", itemId++);
				jsonObject.put("neighborMac", item.getNeighborMac());
				jsonObject.put("routeMaximun", item.getRouteMaximun());
				jsonObject.put("routeMinimun", item.getRouteMinimun());
				jsonObject.put("gridCount", recCount);

				jsonArray.put(jsonObject);
			}
		} catch (JSONException e) {
		}
	}

	public static String getHiveApListName(short type) {
		if (HiveAp.STATUS_MANAGED == type) {
			return "Configured "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s";
		} else if (HiveAp.STATUS_NEW == type) {
			return "Unconfigured "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s";
		} else if (HiveAp.STATUS_PRECONFIG == type) {
			return "UnManaged "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s";
		} else {
			return "Unknown "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s";
		}
	}

	private ConfigTemplate getDefaultConfigTemplate(HmDomain domain) {
		if (null != domain) {
			HmStartConfig startConfig = domain.getStartConfig();
			if (null != startConfig) {
				short type = startConfig.getModeType();
				if (type == HmStartConfig.HM_MODE_EASY) {
					return HmBeParaUtil.getEasyModeDefaultTemplate(domain
							.getId());
				} else {
					return HmBeParaUtil.getDefaultTemplate();
				}
			} else {
				return HmBeParaUtil.getDefaultTemplate();
			}
		}
		return null;
	}

	private ConfigTemplate getEasyModeDefaultTemplate(HmDomain domain) {
		if (null != domain) {
			HmStartConfig startConfig = domain.getStartConfig();

			if (null != startConfig) {
				short type = startConfig.getModeType();

				if (type == HmStartConfig.HM_MODE_EASY) {
					return HmBeParaUtil.getEasyModeDefaultTemplate(domain
							.getId());
				}
			}
		}

		return null;
	}

	protected void reassignOperation(String reassignDomain) throws Exception {
		if (null != reassignDomain && !reassignDomain.isEmpty()) {
			List<Long> selectedHiveAps = getSelectedHiveApIds();
			HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class,
					"domainName", reassignDomain);
			if (null != domain && selectedHiveAps != null
					&& !selectedHiveAps.isEmpty()) {
				ConfigTemplate currentDefault = getEasyModeDefaultTemplate(getDomain());
				ConfigTemplate destDefault = getDefaultConfigTemplate(domain);
				
				HmStartConfig startConfig = domain.getStartConfig();
				if (null == startConfig || destDefault==null) {
					addActionError(MgrUtil.getUserMessage("error.admin.login.vhm.reassignDevice"));
					return;
				} else {
					short type = startConfig.getModeType();
					if (type == HmStartConfig.HM_MODE_EASY)  {
						if (destDefault.isDefaultFlag()) {
							addActionError(MgrUtil.getUserMessage("error.admin.login.vhm.reassignDevice"));
							return;
						}
					}
				}

				for (Long selectedId : selectedHiveAps) {
					HiveAp hiveAp = findBoById(HiveAp.class, selectedId, this);
					if (null != hiveAp) {
						// validate HiveAP status
						if (hiveAp.getManageStatus() != HiveAp.STATUS_NEW) {
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.reassign.noNew",
									new String[] { hiveAp.getLabel() }));
							continue;
						}
						if (hiveAp.getOrigin() != HiveAp.ORIGIN_DISCOVERED) {
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.reassign.noDiscovery",
									new String[] { hiveAp.getLabel() }));
							continue;
						}
						// validate repeated HiveAP
						HiveAp hmBo = QueryUtil.findBoByAttribute(HiveAp.class,
								"macAddress", hiveAp.getMacAddress(), domain
										.getId());
						if (null != hmBo) {
							short managedStatus = hmBo
									.getManageStatus();
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.reassign.existed",
									new String[] { hiveAp.getLabel(),
											getHiveApListName(managedStatus) }));
							continue;
						}
						// validate license(needn't check license when reassign)
						// if (!checkLicensePass(domain.getId())) {
						// break;
						// }
						if (null != hiveAp.getConfigTemplate()
								&& null != currentDefault) {
							Long apConfigTempId = hiveAp.getConfigTemplate().getId();

							if (!apConfigTempId.equals(currentDefault.getId())
									&& !apConfigTempId.equals(HmBeParaUtil.getDefaultTemplate().getId())) {
								addActionError(MgrUtil.getUserMessage(
										"error.hiveAp.reassign", hiveAp
												.getLabel()));
								continue;
							}
						}
						if (null != hiveAp.getWifi0RadioProfile()
								&& !hiveAp
										.getWifi0RadioProfile()
										.getId()
										.equals(
												HmBeParaUtil
														.getDefaultRadioBGProfile()
														.getId())
								&& !hiveAp
										.getWifi0RadioProfile()
										.getId()
										.equals(
												HmBeParaUtil
														.getDefaultRadioNGProfile()
														.getId())) {
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.reassign", hiveAp.getLabel()));
							continue;
						}
						if (null != hiveAp.getWifi1RadioProfile()
								&& !hiveAp
										.getWifi1RadioProfile()
										.getId()
										.equals(
												HmBeParaUtil
														.getDefaultRadioAProfile()
														.getId())
								&& !hiveAp
										.getWifi1RadioProfile()
										.getId()
										.equals(
												HmBeParaUtil
														.getDefaultRadioACProfile()
														.getId())														
								&& !hiveAp
										.getWifi1RadioProfile()
										.getId()
										.equals(
												HmBeParaUtil
														.getDefaultRadioNAProfile()
														.getId())) {
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.reassign", hiveAp.getLabel()));
							continue;
						}
						if (null != hiveAp.getRadiusServerProfile()) {
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.reassign", hiveAp.getLabel()));
							continue;
						}
						if (null != hiveAp.getCapwapIpBind()) {
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.reassign", hiveAp.getLabel()));
							continue;
						}
						if (null != hiveAp.getScheduler()) {
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.reassign", hiveAp.getLabel()));
							continue;
						}
						if (null != hiveAp.getDhcpServers()
								&& !hiveAp.getDhcpServers().isEmpty()) {
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.reassign", hiveAp.getLabel()));
							continue;
						}
						if (null != hiveAp.getL3Neighbors()
								&& !hiveAp.getL3Neighbors().isEmpty()) {
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.reassign", hiveAp.getLabel()));
							continue;
						}
						if (null != hiveAp.getEth0UserProfile()) {
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.reassign", hiveAp.getLabel()));
							continue;
						}
						if (null != hiveAp.getEth1UserProfile()) {
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.reassign", hiveAp.getLabel()));
							continue;
						}
						if (null != hiveAp.getAgg0UserProfile()) {
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.reassign", hiveAp.getLabel()));
							continue;
						}
						if (null != hiveAp.getRed0UserProfile()) {
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.reassign", hiveAp.getLabel()));
							continue;
						}
						if (null != hiveAp.getLearningMacs()
								&& !hiveAp.getLearningMacs().isEmpty()) {
							addActionError(MgrUtil.getUserMessage(
									"error.hiveAp.reassign", hiveAp.getLabel()));
							continue;
						}
						try {
							if (null != hiveAp.getMapContainer()) {
								BoMgmt.getMapMgmt()
										.updateHiveApWithPropagation(hiveAp,
												null, hiveAp.isConnected(),
												hiveAp.getManageStatus());
							}
							// reassign process
							BoMgmt.getMapMgmt().reassignDomain(hiveAp,
									destDefault, domain);
							// add audit log
							generateAuditLog(HmAuditLog.STATUS_SUCCESS,
									MgrUtil.getUserMessage("hm.audit.log.reassign.hiveap.between.home.and.domain",new String[]{
											NmsUtil.getOEMCustomer().getAccessPonitName(), hiveAp.getLabel(),reassignDomain}));
							// add action message
							addActionMessage(MgrUtil.getUserMessage(
									HiveAP_REASSIGN, hiveAp.getLabel()));
						} catch (RuntimeException e) {
							generateAuditLog(HmAuditLog.STATUS_FAILURE,
									MgrUtil.getUserMessage("hm.audit.log.reassign.hiveap.between.home.and.domain",new String[]{
											NmsUtil.getOEMCustomer().getAccessPonitName(), hiveAp.getLabel(),reassignDomain}));
						}
					}
				}
				// clear selection or the previous selected id is still in the
				// list.
				setAllSelectedIds(null);
			}
		}
	}

	protected void acceptOperation() throws Exception {
		/*-
		List<Long> selectedHiveAps = getSelectedHiveApIds();
		if (selectedHiveAps != null && !selectedHiveAps.isEmpty()) {
			// check for license
			if (!checkLicensePass(domainId, selectedHiveAps.size())) {
				return;
			}
			ConfigTemplate defaultTemplate = HmBeParaUtil.getDefaultTemplate();
			RadioProfile defaultARadioProfile = HmBeParaUtil
					.getDefaultRadioAProfile();
			RadioProfile defaultBGRadioProfile = HmBeParaUtil
					.getDefaultRadioBGProfile();
			RadioProfile defaultNGRadioProfile = HmBeParaUtil
					.getDefaultRadioNGProfile();
			RadioProfile defaultNARadioProfile = HmBeParaUtil
					.getDefaultRadioNAProfile();

			for (Long selectedId : selectedHiveAps) {
				HiveAp hiveAp = findBoById(HiveAp.class, selectedId);
				if (null != hiveAp) {
					boolean oldConnection = hiveAp.isConnected();
					short oldManageStatus = hiveAp.getManageStatus();

					// set hiveAp current Map Container.
					if (hiveAp.getMapContainer() != null) {
						topology = hiveAp.getMapContainer().getId();
					} else {
						topology = null;
					}
					hiveAp.setManageStatus(HiveAp.STATUS_MANAGED);
					// assign a default value if there is none when accept it;
					if (null == hiveAp.getConfigTemplate()) {
						hiveAp.setConfigTemplate(defaultTemplate);
					}
					if (null == hiveAp.getWifi0RadioProfile()) {
						if (!hiveAp.is11nHiveAP()) {
							hiveAp.setWifi0RadioProfile(defaultBGRadioProfile);
						} else {
							hiveAp.setWifi0RadioProfile(defaultNGRadioProfile);
						}
					}
					if (null == hiveAp.getWifi1RadioProfile()) {
						if (!hiveAp.is11nHiveAP()) {
							hiveAp.setWifi1RadioProfile(defaultARadioProfile);
						} else {
							hiveAp.setWifi1RadioProfile(defaultNARadioProfile);
						}
					}
					try {
						setId(hiveAp.getId());
						HiveAp updatedHiveAp = updateHiveAp(hiveAp,
								getSelectedTopology(), oldManageStatus,
								oldConnection);
						// send ap manage status changed event.
						HmBeEventUtil
								.eventGenerated(new HiveApManageStatusChangedEvent(
										updatedHiveAp, oldManageStatus));
						// add audit log;
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, "Move AP ("
								+ hiveAp.getLabel() + ") from '"
								+ getHiveApListName(oldManageStatus) + "' to '"
								+ getHiveApListName(HiveAp.STATUS_MANAGED)
								+ "'");
						// add action message;
						addActionMessage(MgrUtil.getUserMessage(OBJECT_MOVED,
								hiveAp.getLabel()));
					} catch (RuntimeException e) {
						generateAuditLog(HmAuditLog.STATUS_FAILURE, "Move AP ("
								+ hiveAp.getLabel() + ") from '"
								+ getHiveApListName(oldManageStatus) + "' to '"
								+ getHiveApListName(HiveAp.STATUS_MANAGED)
								+ "'");
					}
				}
			}
			// clear selection or the previous selected id is still in the list.
			setAllSelectedIds(null);
		} else {
			addActionMessage(MgrUtil.getUserMessage(SELECT_OBJECT));
		}
		 */
	}

	/*-
	 private boolean checkLicensePass(Long domainId, int newCount)
	 throws Exception {
	 if (domainId != null) {
	 HmDomain domain = QueryUtil.findBoById(HmDomain.class, domainId);
	 domain.computeManagedApNum();
	 if (domain.getRunStatus() == HmDomain.DOMAIN_DEFAULT_STATUS) {
	 int supportNum = domain.getMaxApSupportNum();
	 int existNum = domain.getManagedApNum();
	 if (domain.isManagedApNumFull()) {
	 if (HmDomain.HOME_DOMAIN.equals(domain.getDomainName())) {
	 addActionError(MgrUtil
	 .getUserMessage("error.hiveApAccepted.outofLincense.maximum"));
	 } else {
	 addActionError(MgrUtil
	 .getUserMessage(
	 "error.vhm.hiveApAccepted.outofLincense.maximum",
	 domain.getDomainName()));
	 }
	 return false;
	 } else if (supportNum < existNum + newCount) {
	 addActionError(MgrUtil.getUserMessage(
	 "info.license.hiveApCount.approachMax", String
	 .valueOf(supportNum - existNum)));
	 return false;
	 } else if (supportNum - existNum - newCount <= BeLicenseModule.AH_LICENSE_EXCESS_SUPPORT_AP_COUNT) {
	 addActionMessage(MgrUtil.getUserMessage(
	 "info.license.hiveApCount.approachMax", String
	 .valueOf(supportNum - existNum - newCount)));
	 return true;
	 } else {
	 return true;
	 }
	 } else {
	 log.error("checkLicensePass", "vhm:" + domain.getDomainName()
	 + "restoring, cannot accept HiveAP.");
	 }
	 }
	 return false;
	 }
	 */

	private boolean verifyDhcpServerSettings() {
		try {
			Set<VlanDhcpServer> dhcpServers = getDataSource().getDhcpServers();
			boolean isDhcp = getDataSource().isDhcp();
			if (null != dhcpServers && !dhcpServers.isEmpty()) {
				Map<Short, String> interfaces = new HashMap<Short, String>();
				Map<String, String> subnets = new HashMap<String, String>();
				Map<Integer, String> vlans = new HashMap<Integer, String>();
				for (VlanDhcpServer dhcpServer : dhcpServers) {
					short inter = dhcpServer.getDhcpMgt();
					String name = dhcpServer.getProfileName();
					short type = dhcpServer.getTypeFlag();
					String profileName = interfaces.put(inter, name);
					if (null != profileName) {// 1).repeating interfaces binds
						addActionError(getText(
								"error.hiveAp.DHCPServer.repeating.interfaces",
								new String[] {
										profileName,
										name,
										MgrUtil
												.getEnumString("enum.interface.dhcp.server."
														+ inter) }));
						setTabId(3);
						return false;
					}
					if (inter == 0) {// interface is Mgt0
						if (isDhcp) {
							if (type == VlanDhcpServer.ENABLE_DHCP_SERVER) {
								// 2).DHCP server must use static IP.
								addActionError(getText("error.hiveAp.DHCPServer.term"));
								setTabId(0);
								return false;
							}
						} else {
							String ip = getDataSource().getCfgIpAddress();
							String mask = getDataSource().getCfgNetmask();
							String msg = VlanDhcpServerAction
									.checkInterfaceNetwork(ip, mask, dhcpServer);
							if (null != msg && !"".equals(msg.trim())) {
								// 3).checking error.
								addActionError(msg);
								setTabId(3);
								return false;
							}
							// 4).repeating subnet
							String subnet = HmBeOsUtil.parseSubnet(ip, mask);
							String profileName1 = subnets.put(subnet, name);
							if (null != profileName1) {
								addActionError(getText(
										"error.hiveAp.DHCPServer.repeating.subnet",
										new String[] { profileName1, name,
												subnet }));
								setTabId(3);
								return false;
							}

						}
						/*
						 * ignore mgt0 vlan checking for the mgt vlan is in
						 * template.
						 */
					} else {
						// 4).repeating subnet
						String ip = dhcpServer.getInterfaceIp();
						String mask = dhcpServer.getInterfaceNet();
						String subnet = HmBeOsUtil.parseSubnet(ip, mask);
						String profileName1 = subnets.put(subnet, name);
						if (null != profileName1) {
							addActionError(getText(
									"error.hiveAp.DHCPServer.repeating.subnet",
									new String[] { profileName1, name, subnet }));
							setTabId(3);
							return false;
						}
						// 5).repeating vlan
						int vlan = dhcpServer.getInterVlan();
						String profileName2 = vlans.put(vlan, name);
						if (null != profileName2) {
							addActionError(getText(
									"error.hiveAp.DHCPServer.repeating.vlan",
									new String[] { profileName2, name,
											String.valueOf(vlan) }));
							setTabId(3);
							return false;
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("verifyDhcpServerSettings",
					"verify dhcp server setting error.", e);
		}
		return true;
	}

	public String getStrNoChange() {
		return strNoChange;
	}

	public int getCacheId() {
		return cacheId;
	}

	public void setCacheId(int cacheId) {
		this.cacheId = cacheId;
	}

	public void setReassignDomainName(String reassignDomainName) {
		this.reassignDomainName = reassignDomainName;
	}

	private String filterMac;

	private String dashCondition;

	public String getDashCondition() {
		return dashCondition;
	}

	public void setDashCondition(String dashCondition) {
		this.dashCondition = dashCondition;
	}

	public String getFilterMac() {
		return filterMac;
	}

	public void setFilterMac(String filterMac) {
		this.filterMac = filterMac;
	}

	private String mapId;

	private String isConnected;

	private String isNew;

	private Long leafNodeId;

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public void setIsConnected(String isConnected) {
		this.isConnected = isConnected;
	}

	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}

	public void setLeafNodeId(Long leafNodeId) {
		this.leafNodeId = leafNodeId;
	}

	private String strManageUponContact;

	private String strDtls;

	private String strEnableDas;

	public String getStrDtls() {
		return strDtls;
	}

	public void setStrDtls(String strDtls) {
		this.strDtls = strDtls;
	}

	public String getStrManageUponContact() {
		return strManageUponContact;
	}

	public void setStrManageUponContact(String strManageUponContact) {
		this.strManageUponContact = strManageUponContact;
	}

	public String getStrEnableDas() {
		return strEnableDas;
	}

	public void setStrEnableDas(String strEnableDas) {
		this.strEnableDas = strEnableDas;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	public boolean isConfigView() {
		return configView;
	}
	
	public String returnConfigOrMonitorViewL2FeatureKey(String featureKey){
		if (featureKey.equals(L2_FEATURE_CONFIG_SWITCHES)) {
			return L2_FEATURE_SWITCHES;
		} else if (featureKey.equals(L2_FEATURE_SWITCHES)) {
			return L2_FEATURE_CONFIG_SWITCHES;
		} else if (featureKey.equals(L2_FEATURE_CONFIG_VPN_GATEWAYS)) {
			return L2_FEATURE_VPN_GATEWAYS;
		} else if (featureKey.equals(L2_FEATURE_VPN_GATEWAYS)) {
			return L2_FEATURE_CONFIG_VPN_GATEWAYS;
		} else if (featureKey.equals(L2_FEATURE_CONFIG_DEVICE_HIVEAPS)) {
			return L2_FEATURE_DEVICE_HIVEAPS;
		} else if (featureKey.equals(L2_FEATURE_DEVICE_HIVEAPS)) {
			return L2_FEATURE_CONFIG_DEVICE_HIVEAPS;
		} else if (featureKey.equals(L2_FEATURE_CONFIG_BRANCH_ROUTERS)) {
			return L2_FEATURE_BRANCH_ROUTERS;
		} else if (featureKey.equals(L2_FEATURE_BRANCH_ROUTERS)) {
			return L2_FEATURE_CONFIG_BRANCH_ROUTERS;
			
		} else if (featureKey.equals(L2_FEATURE_CONFIG_HIVE_APS)) {
			return L2_FEATURE_MANAGED_HIVE_APS;
		} else if (featureKey.equals(L2_FEATURE_MANAGED_HIVE_APS)) {
			return L2_FEATURE_CONFIG_HIVE_APS;
		}
		return null;
	}
	
	public boolean isDisplayMonitorView() {
		boolean hasReadAccess=true;
		String l2FeatureKey = returnConfigOrMonitorViewL2FeatureKey(getSelectedL2FeatureKey());
		if (l2FeatureKey==null) {
			return false;
		}
		if (!hasAccessPermission(l2FeatureKey, HmPermission.OPERATION_READ)) {
			hasReadAccess= false;
		}
		if (isConfigView() && hasReadAccess) {
			return true;
		}
		return false;
	}
	public boolean isDisplayConfigView() {
		boolean hasReadAccess=true;
		String l2FeatureKey = returnConfigOrMonitorViewL2FeatureKey(getSelectedL2FeatureKey());
		if (l2FeatureKey==null) {
			return false;
		}
		if (!hasAccessPermission(l2FeatureKey, HmPermission.OPERATION_READ)) {
			hasReadAccess= false;
		}
		
		if (!isConfigView() && hasReadAccess) {
			return true;
		}
		return false;
	}

	public boolean isHasNewHiveAP() {
		return hasNewHiveAP;
	}

	public boolean isCollapsed() {
		Boolean r = (Boolean) MgrUtil
				.getSessionAttribute(LIST_VIEW_COLLAPSE_STATUS);
		return r == null ? false : r;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public void setSsidName(String ssidName) {
		this.ssidName = ssidName;
	}

	public String getShowRadioProfiles() {
		return "showRadioProfiles";
	}

	public String getRadioChannels() {
		return "radioChannels";
	}

	public String getStaticRouteInterface() {
		return "staticRouteInterface";
	}

	public String getEth0AdminState() {
		return "eth0AdminState";
	}

	public String getEth1AdminState() {
		return "eth1AdminState";
	}

	public String getEth0OperationMode() {
		return "eth0OperationMode";
	}

	public String getEth1OperationMode() {
		return "eth1OperationMode";
	}

	public static boolean isHiveApMacAddressExist(Long domainId,
			String macAddress) {
		if (null != macAddress) {
			macAddress = macAddress.toUpperCase();
		}
		List<?> boIds = QueryUtil.executeQuery("select id from "
				+ HiveAp.class.getSimpleName(), null, new FilterParams(
				"macAddress", macAddress));
		return !boIds.isEmpty();
	}
	
	public static boolean isHiveApSerialNumberExist(Long domainId,
			String serialNumber) {
		List<?> boIds = QueryUtil.executeQuery("select id from "
				+ HiveAp.class.getSimpleName(), null, new FilterParams(
				"serialNumber", serialNumber));
		return !boIds.isEmpty();
	}

	public static boolean isHiveApHostNameExist(Long domainId, String hostname,
			Long currentId) {
		List<?> boIds = QueryUtil.executeQuery("select id from "
				+ HiveAp.class.getSimpleName(), null, new FilterParams(
				"hostname", hostname), domainId);
		if (!boIds.isEmpty()) {
			if (null == currentId) {
				return true;
			} else {
				for (Object object : boIds) {
					Long otherId = (Long) object;
					if (otherId.longValue() != currentId.longValue()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/* functions for checking in multiple-edit page */

	public boolean getIs20HiveAP() {
		return null != getDataSource()
				&& HiveAp.is20HiveAP(getDataSource().getHiveApModel());
	}

	public boolean getIs110HiveAP() {
		return null != getDataSource()
				&& HiveAp.is110HiveAP(getDataSource().getHiveApModel());
	}

	public boolean getIs120HiveAP() {
		return null != getDataSource()
				&& HiveAp.is120HiveAP(getDataSource().getHiveApModel());
	}

	public boolean getIs300HiveAP() {
		return null != getDataSource()
				&& (HiveAp.is340HiveAP(getDataSource().getHiveApModel())
				|| HiveAp.is11acHiveAP(getDataSource().getHiveApModel()));
	}

	public boolean getIsBr100() {
		return null != getDataSource()
				&& HiveAp.isBR100LikeHiveAP(getDataSource().getHiveApModel());
	}
	
	public boolean getIs2024Switch() {
		return null != getDataSource()
				&& HiveAp.is2024Switch(getDataSource().getHiveApModel());
	}
	
	public boolean getIs2124PSwitch() {
		return null != getDataSource()
				&& HiveAp.is2124PSwitch(getDataSource().getHiveApModel());
	}
	
	public boolean getIs2024PSwitch() {
		return null != getDataSource()
				&& HiveAp.is2024PSwitch(getDataSource().getHiveApModel());
	}
	
	public boolean getIs2148PSwitch() {
		return null != getDataSource()
				&& HiveAp.is2148PSwitch(getDataSource().getHiveApModel());
	}
	
	public boolean getIsBRLTEMode() {
		return null != getDataSource()
				&& HiveAp.isBRLTEMode(getDataSource().getHiveApModel());
	}

	public String getDisplayLanInterface(){
		if ( null != getDataSource()
				&& getDataSource().getMultiDisplayType()==HiveAp.MULTI_DISPLAY_LAN) {
			return "";
		}
		return "none";
	}

	public String getDisplayPortInterface(){
		if (isEasyMode()) {
			return "none";
		}
		if (null != getDataSource()
				&& getDataSource().getMultiDisplayType()==HiveAp.MULTI_DISPLAY_BR) {
			return "";
		}
		return "none";
	}

	public String getDisplayMultiOperationModeForWifi(){
		if (getIs110HiveAP() && getDataSource().getMultiDisplayType()==-1) {
			return "none";
		}
		return "";
	}

	/* end */

	private String imageType;

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	private String message;

	public void setMessage(String message) {
		this.message = message;
	}

	/* HiveAP New Layout */

	public EnumItem[] getRadioModeType1() {
		if (getDataSource().isWifi1Available()) {
			return new EnumItem[] { new EnumItem(HiveAp.RADIO_MODE_ACCESS_ALL,
					getText("hiveAp.radioMode.allAccess")) };
		} else {
			return new EnumItem[] { new EnumItem(HiveAp.RADIO_MODE_ACCESS_ALL,
					getText("hiveAp.radioMode.access")) };
		}
	}

	public EnumItem[] getRadioModeType2() {
		if (getDataSource().isWifi1Available()) {
			return new EnumItem[] { new EnumItem(HiveAp.RADIO_MODE_ACCESS_ONE,
					getText("hiveAp.radioMode.oneAccess")) };
		}else{
			return new EnumItem[] { new EnumItem(HiveAp.RADIO_MODE_ACCESS_ONE,
					getText("hiveAp.radioMode.mesh")) };
		}
	}

	public EnumItem[] getRadioModeType3() {
		return new EnumItem[] { new EnumItem(HiveAp.RADIO_MODE_BRIDGE,
				getText("hiveAp.radioMode.bridge")) };
	}

	public EnumItem[] getRadioModeType4() {
		return new EnumItem[] { new EnumItem(HiveAp.RADIO_MODE_CUSTOMIZE,
				getText("hiveAp.radioMode.customize")) };
	}

	public EnumItem[] getRadioModeType5() {
		if (getDataSource().isWifi1Available()) {
			return new EnumItem[] { new EnumItem(HiveAp.RADIO_MODE_ACCESS_DUAL,
					getText("hiveAp.radioMode.accessDual")) };
		}else{
			return new EnumItem[] { new EnumItem(HiveAp.RADIO_MODE_ACCESS_DUAL,
					getText("hiveAp.radioMode.dual")) };
		}

	}
	
	public EnumItem[] getRadioModeType6() {
		return new EnumItem[] { new EnumItem(HiveAp.RADIO_MODE_ACCESS_WAN,
				getText("hiveAp.radioMode.accessWan")) };
	}
	public String getRadioTypeAccessWanStyle2(){
		if (NmsUtil.compareSoftwareVersion("6.0.0.0", this.getDataSource()
				.getSoftVer()) > 0
				|| (this.getDataSource().getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200_WP
				&& this.getDataSource().getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200_LTE_VZ)
				|| HiveAp.Device_TYPE_BRANCH_ROUTER != getDataSource()
						.getDeviceType()) {
			return "none";
		} else {
				return "";
		}
	}
	
	public String getRadioTypeAccessWanStyle(){
		if (NmsUtil.compareSoftwareVersion("6.0.0.0", this.getDataSource()
				.getSoftVer()) > 0
				|| (this.getDataSource().getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200_WP
				&& this.getDataSource().getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200_LTE_VZ)
				|| HiveAp.Device_TYPE_BRANCH_ROUTER != getDataSource()
						.getDeviceType()) {
			return "none";
		} else {
			if (getDataSource().getRouterWanInterfaceNum() < 3) {
				return "";
			}
		}
		return "none";
	}
	
	public String getRadioWanBR200WPStyle(){
		if (NmsUtil.compareSoftwareVersion("6.0.0.0", this.getDataSource().getSoftVer()) > 0 
				|| (this.getDataSource().getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200_WP && this.getDataSource().getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200_LTE_VZ) 
				|| HiveAp.Device_TYPE_BRANCH_ROUTER != getDataSource().getDeviceType()){
			return "";
		} else {
			return "none";
		}
	}
	
	public String getDynamicBandSwitchStyle() {
		if (NmsUtil.compareSoftwareVersion("6.0.0.0", this.getDataSource().getSoftVer()) <= 0 
				&& this.getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_WP 
				&& HiveAp.Device_TYPE_BRANCH_ROUTER == getDataSource().getDeviceType()
				&& this.getDataSource().getRadioConfigType() == HiveAp.RADIO_MODE_ACCESS_WAN
				&& this.getDataSource().isEnableDynamicBandSwitch()){
			return "none";
		} else {
			return "";
		}
	}

	public EnumItem[] getEnumEthernetSetups() {
		return MgrUtil.enumItems("enum.interface.eth.setup.", new int[] {
				HiveAp.USE_ETHERNET_BOTH, HiveAp.USE_ETHERNET_AGG0,
				HiveAp.USE_ETHERNET_RED0 });
	}

	public EnumItem[] getMgt0NetworkType1() {
		return new EnumItem[] { new EnumItem(HiveAp.USE_STATIC_IP,
				getText("hiveAp.mgt0.network.staticip")) };
	}

	public EnumItem[] getMgt0NetworkType2() {
		return new EnumItem[] { new EnumItem(HiveAp.USE_DHCP_FALLBACK,
				getText("hiveAp.mgt0.network.fallback")) };
	}

	public EnumItem[] getMgt0NetworkType3() {
		return new EnumItem[] { new EnumItem(HiveAp.USE_DHCP_WITHOUTFALLBACK,
				getText("hiveAp.mgt0.network.nofallback")) };
	}

	/* end */

	/* HiveAP Filter feature */
	public static final String MANAGED_HIVEAP_FILTERS = "managed_hiveAp_filters";
	public static final String MANAGED_HIVEAP_CURRENT_FILTER = "managed_hiveAp_current_filter";

	private String filter = DEFAULT_FILTER_CURRENT_DEFAULT;
	private String filterName;
	private Long filterTemplate;
	private Long filterTopology;
	private Long filterHive;
	private String filterIp;
	private int filterProvision;
	private boolean filterProvisionFlag;
	private int filterConfig;
	private boolean filterVpnServer;
	private boolean filterRadiusServer;
	private boolean filterDhcpServer;
	private boolean filterRadiusProxy;
	private boolean filterVpnClient;
	private short filterApType;
	private short filterApModel;
	private short filterDeviceType=-2;
	private String filterVersion;
	private String filterHostname;
	private boolean filterEth0Bridge;
	private boolean filterEth1Bridge;
	private boolean filterRed0Bridge;
	private boolean filterAgg0Bridge;
	private String classificationTag1;
	private String classificationTag2;
	private String classificationTag3;
	private String filterSerialNumber;

	public String getFilter() {
		if (null == filter) {
			filter = (String) MgrUtil.getSessionAttribute(MANAGED_HIVEAP_CURRENT_FILTER);
		}
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public void setFilterTemplate(Long filterTemplate) {
		this.filterTemplate = filterTemplate;
	}

	public void setFilterTopology(Long filterTopology) {
		this.filterTopology = filterTopology;
	}

	public void setFilterHive(Long filterHive) {
		this.filterHive = filterHive;
	}

	public void setFilterProvision(int filterProvision) {
		this.filterProvision = filterProvision;
	}

	public void setFilterProvisionFlag(boolean filterProvisionFlag) {
		this.filterProvisionFlag = filterProvisionFlag;
	}

	public void setFilterVpnServer(boolean filterVpnServer) {
		this.filterVpnServer = filterVpnServer;
	}

	public void setFilterVpnClient(boolean filterVpnClient) {
		this.filterVpnClient = filterVpnClient;
	}

	public void setFilterRadiusServer(boolean filterRadiusServer) {
		this.filterRadiusServer = filterRadiusServer;
	}

	public void setFilterDhcpServer(boolean filterDhcpServer) {
		this.filterDhcpServer = filterDhcpServer;
	}

	public void setFilterRadiusProxy(boolean filterRadiusProxy) {
		this.filterRadiusProxy = filterRadiusProxy;
	}

	public void setFilterHostname(String filterHostname) {
		this.filterHostname = filterHostname;
	}

	public String getFilterName() {
		return filterName;
	}

	public Long getFilterTemplate() {
		return filterTemplate;
	}

	public Long getFilterTopology() {
		return filterTopology;
	}

	public Long getFilterHive() {
		return filterHive;
	}

	public int getFilterProvision() {
		return filterProvision;
	}

	public boolean isFilterProvisionFlag() {
		return filterProvisionFlag;
	}

	public String getFilterIp() {
		return filterIp;
	}

	public void setFilterIp(String filterIp) {
		this.filterIp = filterIp;
	}

	public int getFilterConfig() {
		return filterConfig;
	}

	public void setFilterConfig(int filterConfig) {
		this.filterConfig = filterConfig;
	}

	public boolean getFilterSelectionDisabled() {
		return !filterProvisionFlag;
	}

	public short getFilterApType() {
		return this.filterApType;
	}

	public void setFilterApType(short filterApType) {
		this.filterApType = filterApType;
	}

	public short getFilterApModel() {
		return this.filterApModel;
	}

	public void setFilterApModel(short filterApModel) {
		this.filterApModel = filterApModel;
	}

	public short getFilterDeviceType() {
		return filterDeviceType;
	}

	public void setFilterDeviceType(short filterDeviceType) {
		this.filterDeviceType = filterDeviceType;
	}

	public String getFilterVersion() {
		return this.filterVersion;
	}

	public void setFilterVersion(String filterVersion) {
		this.filterVersion = filterVersion;
	}

	public boolean isFilterEth0Bridge() {
		return this.filterEth0Bridge;
	}

	public void setFilterEth0Bridge(boolean filterEth0Bridge) {
		this.filterEth0Bridge = filterEth0Bridge;
	}

	public boolean isFilterEth1Bridge() {
		return this.filterEth1Bridge;
	}

	public void setFilterEth1Bridge(boolean filterEth1Bridge) {
		this.filterEth1Bridge = filterEth1Bridge;
	}

	public boolean isFilterRed0Bridge() {
		return this.filterRed0Bridge;
	}

	public void setFilterRed0Bridge(boolean filterRed0Bridge) {
		this.filterRed0Bridge = filterRed0Bridge;
	}

	public boolean isFilterAgg0Bridge() {
		return this.filterAgg0Bridge;
	}

	public void setFilterAgg0Bridge(boolean filterAgg0Bridge) {
		this.filterAgg0Bridge = filterAgg0Bridge;
	}

	public String getClassificationTag1() {
		return this.classificationTag1;
	}

	public void setClassificationTag1(String classificationTag1) {
		this.classificationTag1 = classificationTag1;
	}

	public String getClassificationTag2() {
		return this.classificationTag2;
	}

	public void setClassificationTag2(String classificationTag2) {
		this.classificationTag2 = classificationTag2;
	}

	public String getClassificationTag3() {
		return this.classificationTag3;
	}

	public void setClassificationTag3(String classificationTag3) {
		this.classificationTag3 = classificationTag3;
	}

	// bug fix for 22978, all network policies should be listed for filter parameter
	public List<CheckItem> getFilterTemplates() {
		return getBoCheckItems("configName", ConfigTemplate.class, null, new SortParams("configName"));
	}

	public List<CheckItem> getFilterTopologys() {
		prepareTopologys(false);
		return this.topologys;
	}

	public List<CheckItem> getFilterHives() {
		List<CheckItem> hiveProfiles = getBoCheckItems("hiveName",
				HiveProfile.class, null,new SortParams("hiveName"));
		if (hiveProfiles.isEmpty()) {
			hiveProfiles.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		return hiveProfiles;
	}

	public List<?> getFilterList() {
		List<?> list = QueryUtil.executeQuery("select filterName from "
				+ HiveApFilter.class.getSimpleName(), null, new FilterParams(
				"userName=:s1 AND typeOfThisFilter=:s2", 
					new Object[] {getUserContext().getUserName(), HiveApFilter.FILTER_TYPE_MANAGED_DEVICE}), domainId);
		List<String> filters = new ArrayList<String>();
		for (Object o : list) {
			filters.add((String) o);
		}
		// order by name
		Collections.sort(filters, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
		});
		if (isJsonMode()==true){
			filters.add(0, DEFAULT_FILTER_CURRENT);
			filters.add(0, DEFAULT_FILTER_CURRENT_DEFAULT);
		}
		return filters;
	}

	public static final int AUTO_PROVISION_ONE_HOUR = 1;
	public static final int AUTO_PROVISION_ONE_DAY = 2;
	public static final int AUTO_PROVISION_THREE_DAYS = 3;
	public static final int AUTO_PROVISION_ONE_WEEK = 4;
	public static final int AUTO_PROVISION_ONE_MONTH = 5;
	public static final int AUTO_PROVISION_ALL_TIME = 6;

	public EnumItem[] getFilterProvisions() {
		return MgrUtil.enumItems("enum.hiveAp.autoProvision.time.", new int[] {
				AUTO_PROVISION_ONE_HOUR, AUTO_PROVISION_ONE_DAY,
				AUTO_PROVISION_THREE_DAYS, AUTO_PROVISION_ONE_WEEK,
				AUTO_PROVISION_ONE_MONTH, AUTO_PROVISION_ALL_TIME });
	}

	public static final int CONFIGURATION_MATCHED = 1;
	public static final int CONFIGURATION_MISMATCHED = 2;

	public EnumItem[] getFilterConfigs() {
		return MgrUtil.enumItems("enum.hiveAp.configuration.indication.",
				new int[] { CONFIGURATION_MATCHED, CONFIGURATION_MISMATCHED });
	}

	private void prepareSearchOperation() {
		List<Short> status = new ArrayList<Short>();
		status.add(HiveAp.STATUS_MANAGED);
		status.add(HiveAp.STATUS_NEW);

		List<Object> values = new ArrayList<Object>();
		List<Short> deviceTypes = new ArrayList<Short>();
		String where = "manageStatus in (:s1)";
		values.add(status);
		if("managedHiveAps".equals(listType)){
			where = where + " AND deviceType in (:s" + (values.size() + 1) + ")";
			deviceTypes.add(HiveAp.Device_TYPE_HIVEAP);
			deviceTypes.add(HiveAp.Device_TYPE_BRANCH_ROUTER);
			deviceTypes.add(HiveAp.Device_TYPE_SWITCH);
			deviceTypes.add(HiveAp.Device_TYPE_VPN_GATEWAY);
			deviceTypes.add(HiveAp.Device_TYPE_VPN_BR);
			values.add(deviceTypes);
		}else if("managedVPNGateways".equals(listType)){
			where = where + " AND (deviceType in :s" + (values.size() + 1);
			deviceTypes.add(HiveAp.Device_TYPE_VPN_GATEWAY);
			deviceTypes.add(HiveAp.Device_TYPE_VPN_BR);
			values.add(deviceTypes);
			
			where = where + " OR hiveApModel in (:s" + (values.size() + 1) + ")" + ")";
			values.add(getCVGList());
		}else if("managedRouters".equals(listType)){
			where = where + " AND deviceType = :s" + (values.size() + 1);
			values.add(HiveAp.Device_TYPE_BRANCH_ROUTER);
		}else if("managedSwitches".equals(listType)){
			where = where + " AND deviceType = :s" + (values.size() + 1);
			values.add(HiveAp.Device_TYPE_SWITCH);
		}else if("managedDeviceAPs".equals(listType)){
			where = where + " AND deviceType = :s" + (values.size() + 1);
			values.add(HiveAp.Device_TYPE_HIVEAP);

			List<Short> modelList = new ArrayList<Short>();
			if(this.isEasyMode()){
				modelList.addAll(getCVGList());
				modelList.addAll(getBRList());
			}else{
				modelList.addAll(getCVGList());
			}
			where = where + " AND hiveApModel not in (:s" + (values.size() + 1) + ")";
			values.add(modelList);
		}

		if (null != filterTemplate && filterTemplate > 0) {
			where = where + " AND configTemplate.id = :s" + (values.size() + 1);
			values.add(filterTemplate);
			//fix bug 18894
			if(!deviceTypes.isEmpty() && deviceTypes.
					contains(HiveAp.Device_TYPE_VPN_GATEWAY)){
				deviceTypes.remove((Object)HiveAp.Device_TYPE_VPN_GATEWAY);
			}
		}
		if (null != filterHive && filterHive > 0) {
			where = where + " AND configTemplate.hiveProfile.id = :s"
					+ (values.size() + 1);
			values.add(filterHive);
			//fix bug 18894
			if(!deviceTypes.isEmpty() && deviceTypes.
					contains(HiveAp.Device_TYPE_VPN_GATEWAY)){
				deviceTypes.remove((Object)HiveAp.Device_TYPE_VPN_GATEWAY);
			}
		}
		if (null != filterTopology && filterTopology > 0) {
			where = where + " AND mapContainer.id = :s" + (values.size() + 1);
			values.add(filterTopology);
		} else if (null != filterTopology && filterTopology == -1) {
			where = where + " AND mapContainer.id is null";
		}
		if (filterConfig > 0) {
			where = where + " AND pending = :s" + (values.size() + 1);
			if (filterConfig == CONFIGURATION_MATCHED) {
				values.add(false);
			} else {
				values.add(true);
			}
		}
		if (null != filterIp && !("".equals(filterIp.trim()))) {
			where = where + " AND ipAddress like :s" + (values.size() + 1);
			values.add(filterIp.trim() + '%');
		}
		if (null != filterHostname && !("".equals(filterHostname.trim()))) {
			where = where + " AND lower(hostName) like :s"
					+ (values.size() + 1);
			values.add('%' + filterHostname.trim().toLowerCase() + '%');
		}
		if (filterProvisionFlag) {
			long currentMs = System.currentTimeMillis();
			Date date = new Date(0);
			switch (filterProvision) {
			case AUTO_PROVISION_ONE_HOUR:
				date = new Date(currentMs - 3600 * 1000);
				break;
			case AUTO_PROVISION_ONE_DAY:
				date = new Date(currentMs - 24 * 3600 * 1000);
				break;
			case AUTO_PROVISION_THREE_DAYS:
				date = new Date(currentMs - 3 * 24 * 3600 * 1000);
				break;
			case AUTO_PROVISION_ONE_WEEK:
				date = new Date(currentMs - 7 * 24 * 3600 * 1000);
				break;
			case AUTO_PROVISION_ONE_MONTH:
				date = new Date(currentMs - 30 * 24 * 3600 * 1000L);
				break;
			case AUTO_PROVISION_ALL_TIME:
				break;
			}
			where = where + " AND provision != :s" + (values.size() + 1)
					+ " AND discoveryTime >= :s" + (values.size() + 2);
			values.add((int) HiveAp.HIVEAP_NO_PROVISION);
			values.add(date.getTime());
		}
		if (filterRadiusServer || filterVpnServer || filterVpnClient
				|| filterDhcpServer || filterRadiusProxy) {
			String filter = "";
			if (filterRadiusServer) {
				filter += "".equals(filter) ? "radiusServerProfile != null"
						: " or radiusServerProfile != null";
			}
			if (filterRadiusProxy) {
				filter += "".equals(filter) ? "radiusProxyProfile != null"
						: " or radiusProxyProfile != null";
			}
			if (filterVpnServer) {
				filter += "".equals(filter) ? "(configTemplate.vpnService != null AND vpnMark = "
						+ HiveAp.VPN_MARK_SERVER + ")"
						: " or (configTemplate.vpnService != null AND vpnMark = "
								+ HiveAp.VPN_MARK_SERVER + ")";
			}
			if (filterVpnClient) {
				filter += "".equals(filter) ? "(configTemplate.vpnService != null AND vpnMark = "
						+ HiveAp.VPN_MARK_CLIENT + ")"
						: " or (configTemplate.vpnService != null AND vpnMark = "
								+ HiveAp.VPN_MARK_CLIENT + ")";
			}
			if (filterDhcpServer) {
				filter += "".equals(filter) ? "dhcpServerCount > 0"
						: " or dhcpServerCount > 0";
			}
			where = where + " AND (" + filter + ")";
		}

		if (filterApType > 0) {
			where = where + " AND hiveApType = :s" + (values.size() + 1);
			values.add(filterApType);
		}
		if (filterApModel >= 0) {
			where = where + " AND hiveApModel = :s" + (values.size() + 1);
			values.add(filterApModel);
		}
		if (filterDeviceType >=0) {
			if (filterDeviceType==2){
				where = where + " AND (deviceType = :s" + (values.size() + 1);
				values.add(HiveAp.Device_TYPE_VPN_GATEWAY);
				where = where + " OR deviceType = :s" + (values.size() + 1);
				values.add(HiveAp.Device_TYPE_VPN_BR);
				where = where + " OR hiveApModel in (:s" + (values.size() + 1) + "))";
				values.add(getCVGList());
			} else {
				if (filterDeviceType==0) {
					where = where + " AND hiveApModel not in (:s" + (values.size() + 1) + ")";
					values.add(getCVGList());
				}
				where = where + " AND deviceType = :s" + (values.size() + 1);
				values.add(filterDeviceType);
			}
		}
		if (filterVersion != null && !"".equals(filterVersion.trim())) {
			where = where + " AND displayVer like :s" + (values.size() + 1);
			values.add("%" + filterVersion + "%");
		}
		if (filterEth0Bridge || filterEth1Bridge || filterRed0Bridge
				|| filterAgg0Bridge) {
			String filter = "";
			if (filterEth0Bridge) {
				String query = "(eth0.operationMode = "
						+ AhInterface.OPERATION_MODE_ACCESS
						+ " OR eth0.operationMode = "
						+ AhInterface.OPERATION_MODE_BRIDGE + ")";

				filter += "".equals(filter) ? query : " or " + query;
			}

			if (filterEth1Bridge) {
				String query = "(eth1.operationMode = "
						+ AhInterface.OPERATION_MODE_ACCESS
						+ " OR eth1.operationMode = "
						+ AhInterface.OPERATION_MODE_BRIDGE + ")";
				filter += "".equals(filter) ? query : " or " + query;
			}

			if (filterRed0Bridge) {
				String query = "(red0.operationMode = "
						+ AhInterface.OPERATION_MODE_ACCESS
						+ " OR red0.operationMode = "
						+ AhInterface.OPERATION_MODE_BRIDGE + ")";
				filter += "".equals(filter) ? query : " or " + query;
			}

			if (filterAgg0Bridge) {
				String query = "(agg0.operationMode = "
						+ AhInterface.OPERATION_MODE_ACCESS
						+ " OR agg0.operationMode = "
						+ AhInterface.OPERATION_MODE_BRIDGE + ")";
				filter += "".equals(filter) ? query : " or " + query;
			}
			where = where + " AND (" + filter + ")";
		}

		if (classificationTag1 != null && !"".equals(classificationTag1)) {
			where = where + " AND classificationTag1 = :s" + (values.size() + 1);
			values.add(classificationTag1);
		}
		if (classificationTag2 != null && !"".equals(classificationTag2)) {
			where = where + " AND classificationTag2 = :s" + (values.size() + 1);
			values.add(classificationTag2);
		}
		if (classificationTag3 != null && !"".equals(classificationTag3)) {
			where = where + " AND classificationTag3 = :s" + (values.size() + 1);
			values.add(classificationTag3);
		}
		
		if (StringUtils.isNotBlank(filterSerialNumber)) {
			if (filterSerialNumber.length() == 14) {
				where = where + " AND serialNumber = :s" + (values.size() + 1);
				values.add(filterSerialNumber);
			} else {
				where = where + " AND serialNumber like :s" + (values.size() + 1);
				values.add("%" + filterSerialNumber + "%");
			}
		}
		
		if (!values.isEmpty()) {
			filterParams = new FilterParams(where, values.toArray());
		}
	}

	private void removeHiveApFilter() {
		if (null == filterName || "".equals(filterName.trim())) {
			return;
		}
		try {
			List<HiveApFilter> hiveApFilterList = QueryUtil
					.executeQuery(HiveApFilter.class, null, new FilterParams(
							"filterName=:s1 AND userName=:s2 AND typeOfThisFilter=:s3",
							new Object[] { filterName,
									getUserContext().getUserName(), HiveApFilter.FILTER_TYPE_MANAGED_DEVICE }), domainId);
			if (!hiveApFilterList.isEmpty()) {
				HiveApFilter hiveApFilter = hiveApFilterList.get(0);
				HiveApFilter rmbos = findBoById(HiveApFilter.class, hiveApFilter
						.getId());
				QueryUtil.removeBoBase(rmbos);
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage("action.error.remove.filter.fail",NmsUtil.getOEMCustomer().getAccessPonitName()));
		}
	}

	private void saveToSessionFilterList() {
		if (null == filterName || "".equals(filterName.trim())) {
			return;
		}

		try {
			List<HiveApFilter> hiveApFilterList = QueryUtil
					.executeQuery(HiveApFilter.class, null, new FilterParams(
							"filterName=:s1 AND userName=:s2 AND typeOfThisFilter=:s3",
							new Object[] { filterName,
									getUserContext().getUserName(), HiveApFilter.FILTER_TYPE_MANAGED_DEVICE }), domainId);
			if (!hiveApFilterList.isEmpty()) {
				HiveApFilter hiveApFilter = hiveApFilterList.get(0);
				hiveApFilter.setFilterTemplate(filterTemplate);
				hiveApFilter.setFilterHive(filterHive);
				hiveApFilter.setFilterTopology(filterTopology);
				hiveApFilter.setFilterIp(filterIp);
				hiveApFilter.setFilterProvisionFlag(filterProvisionFlag);
				hiveApFilter.setFilterProvision(filterProvision);
				hiveApFilter.setFilterConfiguration(filterConfig);
				hiveApFilter.setFilterDhcpServer(filterDhcpServer);
				hiveApFilter.setFilterVpnServer(filterVpnServer);
				hiveApFilter.setFilterRadiusServer(filterRadiusServer);
				hiveApFilter.setFilterRadiusProxy(filterRadiusProxy);
				hiveApFilter.setFilterVpnClient(filterVpnClient);
				hiveApFilter.setHiveApType(filterApType);
				hiveApFilter.setHiveApModel(filterApModel);
				hiveApFilter.setFilterDeviceType(filterDeviceType);
				hiveApFilter.setDisplayVer(filterVersion);
				hiveApFilter.setHostname(filterHostname);
				hiveApFilter.setClassificationTag1(classificationTag1);
				hiveApFilter.setClassificationTag2(classificationTag2);
				hiveApFilter.setClassificationTag3(classificationTag3);
				hiveApFilter.setEth0Bridge(filterEth0Bridge);
				hiveApFilter.setEth1Bridge(filterEth1Bridge);
				hiveApFilter.setRed0Bridge(filterRed0Bridge);
				hiveApFilter.setAgg0Bridge(filterAgg0Bridge);
				hiveApFilter.setSerialNumber(filterSerialNumber);
				setId(hiveApFilter.getId());
				QueryUtil.updateBo(hiveApFilter);
			} else {
				HiveApFilter hiveApFilter = new HiveApFilter();
				hiveApFilter.setFilterName(filterName);
				hiveApFilter.setUserName(getUserContext().getUserName());
				hiveApFilter.setFilterTemplate(filterTemplate);
				hiveApFilter.setFilterHive(filterHive);
				hiveApFilter.setFilterTopology(filterTopology);
				hiveApFilter.setFilterIp(filterIp);
				hiveApFilter.setFilterProvisionFlag(filterProvisionFlag);
				hiveApFilter.setFilterProvision(filterProvision);
				hiveApFilter.setFilterConfiguration(filterConfig);
				hiveApFilter.setFilterDhcpServer(filterDhcpServer);
				hiveApFilter.setFilterVpnServer(filterVpnServer);
				hiveApFilter.setFilterRadiusServer(filterRadiusServer);
				hiveApFilter.setFilterRadiusProxy(filterRadiusProxy);
				hiveApFilter.setFilterVpnClient(filterVpnClient);
				hiveApFilter.setHiveApType(filterApType);
				hiveApFilter.setHiveApModel(filterApModel);
				hiveApFilter.setFilterDeviceType(filterDeviceType);
				hiveApFilter.setDisplayVer(filterVersion);
				hiveApFilter.setHostname(filterHostname);
				hiveApFilter.setClassificationTag1(classificationTag1);
				hiveApFilter.setClassificationTag2(classificationTag2);
				hiveApFilter.setClassificationTag3(classificationTag3);
				hiveApFilter.setEth0Bridge(filterEth0Bridge);
				hiveApFilter.setEth1Bridge(filterEth1Bridge);
				hiveApFilter.setRed0Bridge(filterRed0Bridge);
				hiveApFilter.setAgg0Bridge(filterAgg0Bridge);
				hiveApFilter.setSerialNumber(filterSerialNumber);
				hiveApFilter.setOwner(getDomain());
				QueryUtil.createBo(hiveApFilter);
			}

			MgrUtil.setSessionAttribute(MANAGED_HIVEAP_CURRENT_FILTER,
					filterName);
			filter = filterName;

		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage("action.error.add.filter.fail",NmsUtil.getOEMCustomer().getAccessPonitName()));
		}
	}

	private JSONObject prepareFilterValues() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		if (null == filter) {
			return jsonObject;
		}
		jsonObject.put("fname", filter);

		List<HiveApFilter> filterMap = QueryUtil.executeQuery(
				HiveApFilter.class, null, new FilterParams(
						"filterName=:s1 and userName=:s2 and typeOfThisFilter=:s3", new Object[] {
								filter, getUserContext().getUserName(), HiveApFilter.FILTER_TYPE_MANAGED_DEVICE }),
				domainId);

		if (filterMap.isEmpty()) {
			return jsonObject;
		}
		HiveApFilter flt = filterMap.get(0);
		if (null != flt.getFilterTemplate()) {
			jsonObject.put("ftemp", flt.getFilterTemplate());
		}
		if (null != flt.getFilterHive()) {
			jsonObject.put("fhive", flt.getFilterHive());
		}
		if (null != flt.getFilterTopology()) {
			jsonObject.put("ftopo", flt.getFilterTopology());
		}
		if (null != flt.getFilterIp()) {
			jsonObject.put("fip", flt.getFilterIp());
		}
		if (null != flt.getDisplayVer()) {
			jsonObject.put("fDisplayVer", flt.getDisplayVer());
		}
		if (null != flt.getHostname()) {
			jsonObject.put("fHostname", flt.getHostname());
		}
		if (null != flt.getClassificationTag1()) {
			jsonObject.put("fTag1", flt.getClassificationTag1());
		}
		if (null != flt.getClassificationTag2()) {
			jsonObject.put("fTag2", flt.getClassificationTag2());
		}
		if (null != flt.getClassificationTag3()) {
			jsonObject.put("fTag3", flt.getClassificationTag3());
		}

		jsonObject.put("fprovisionf", flt.getFilterProvisionFlag());
		jsonObject.put("fprovision", flt.getFilterProvision());
		jsonObject.put("fconfig", flt.getFilterConfiguration());
		jsonObject.put("fvpn", flt.isFilterVpnServer());
		jsonObject.put("fvpnClient", flt.isFilterVpnClient());
		jsonObject.put("fradius", flt.isFilterRadiusServer());
		jsonObject.put("fradiusProxy", flt.isFilterRadiusProxy());
		jsonObject.put("fdhcp", flt.isFilterDhcpServer());
		jsonObject.put("fApType", flt.getHiveApType());
		jsonObject.put("fApModel", flt.getHiveApModel());
		jsonObject.put("fDeType", flt.getFilterDeviceType());
		jsonObject.put("fBEth0", flt.isEth0Bridge());
		jsonObject.put("fBEth1", flt.isEth1Bridge());
		jsonObject.put("fBRed0", flt.isRed0Bridge());
		jsonObject.put("fBAgg0", flt.isAgg0Bridge());
		jsonObject.put("fSerialNumber", flt.getSerialNumber());

		return jsonObject;
	}

	/* HiveAP Filter feature end */

	/*
	 * whether show cli window menu
	 */
	public boolean getVisibleCliWindow() {
		String showFlag = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_GUI,
				ConfigUtil.KEY_SHOW_CLIWINDOW, "0");
		return showFlag.equals("1");

	}

	@SuppressWarnings("unchecked")
	protected Collection<JSONObject> getRefreshGuidedHiveApListExpress()
			throws Exception {
		Collection<JSONObject> listResults = new Vector<JSONObject>();
		enableSorting();
		enablePaging();
		getSessionFiltering();
		page = findBos();
		if (null == page) {
			return listResults;
		}
		Object idsObj = MgrUtil
				.getSessionAttribute(HiveApUpdateAction.UPDATE_CONFIG_SELECTED_IDS_EX);
		Set<Long> uploadApsEx = null;
		if(idsObj != null){
			uploadApsEx = (Set<Long>)idsObj;
		}
		for (Object obj : page) {
			HiveAp hiveAp = (HiveAp) obj;
			JSONObject result = new JSONObject();
			result.put("id", hiveAp.getId());
			HiveAp resHiveAp = QueryUtil.findBoById(HiveAp.class,
					hiveAp.getId());
			resHiveAp.setConnectionIcon(HiveApMonitor.getConnectionIcon(
					resHiveAp.getConnectStatus(), request.getContextPath(),
					resHiveAp.getDelayTime(),
					resHiveAp.getDisconnChangedTimeStr()));

			resHiveAp.setConfigIndicationIcon(HiveApMonitor
					.getConfigIndicationIcon(resHiveAp.isPending(),
							resHiveAp.getPendingIndex(),
							resHiveAp.getPendingMsg(),
							resHiveAp.isPending_user(),
							resHiveAp.getPendingIndex_user(),
							resHiveAp.getPendingMsg_user(),
							request.getContextPath(), resHiveAp.getId()));

			result.put("connected", resHiveAp.getConnectionIcon()+"&nbsp;");
			result.put("ipAddress", resHiveAp.getIpAddress()+"&nbsp;");
			result.put("apType", resHiveAp.getHiveApTypeString()+"&nbsp;");
			result.put("softVer", resHiveAp.getSoftVerString()+"&nbsp;");
			result.put("configIndicationIcon",
					resHiveAp.getConfigIndicationIcon());
			if(uploadApsEx != null && uploadApsEx.contains(hiveAp.getId())){
				JSONObject resultAttributes = getHiveApUpdateResultInfo(hiveAp);
				if (null != resultAttributes) {
					result.put("result", resultAttributes);
				}
			}
			listResults.add(result);
		}
		return listResults;
	}

	protected Collection<JSONObject> getRefreshGuidedHiveApList() throws Exception {
		Collection<JSONObject> listResults = new Vector<JSONObject>();
//		enableSorting();
//		enablePaging();
//		getSessionFiltering();
//		page = findBos();
		if (null == page) {
			return listResults;
		}
//		String ids = "";
//		Map<Long, String> configTempMap = new HashMap<Long, String>();
//		for (Object obj : page) {
//			HiveAp resHiveAp = (HiveAp) obj;
//			if("".equals(ids)){
//				ids += String.valueOf(resHiveAp.getId());
//			}else{
//				ids += "," + String.valueOf(resHiveAp.getId());
//			}
//		}
//		if(!"".equals(ids)){
//			String query = "select ap.id, c.configName from hive_ap as ap left join CONFIG_TEMPLATE as c on ap.TEMPLATE_ID = c.id and ap.id in ("+ids+")";
//			List<?> apList = QueryUtil.executeNativeQuery(query);
//			for (Object obj : apList) {
//				Object[] template = (Object[]) obj;
//				Long id = Long.valueOf(String.valueOf(template[0]));
//				String tempName = (String) template[1];
//				configTempMap.put(id, tempName);
//			}
//		}

		for (Object obj : page) {
			HiveAp resHiveAp = (HiveAp) obj;
			JSONObject result = new JSONObject();
			result.put("id", resHiveAp.getId());
//			HiveAp resHiveAp = (HiveAp)QueryUtil.findBoById(HiveAp.class, hiveAp.getId(),this);
			resHiveAp.setConnectionIcon(HiveApMonitor.getConnectionIcon(resHiveAp.getConnectStatus(), request.getContextPath(),
					resHiveAp.getDelayTime(), resHiveAp.getDisconnChangedTimeStr()));

			resHiveAp.setConfigIndicationIcon(HiveApMonitor
					.getConfigIndicationIcon(resHiveAp.isPending(), resHiveAp.getPendingIndex(),
							resHiveAp.getPendingMsg(), resHiveAp.isPending_user(),
							resHiveAp.getPendingIndex_user(), resHiveAp.getPendingMsg_user(),
							request.getContextPath(), resHiveAp.getId()));

			result.put("d"+COLUMN_CONNECTED, resHiveAp.getConnectionIcon()+"&nbsp;");
			//result.put("d"+COLUMN_HOSTNAME, resHiveAp.getHostName()+"&nbsp;");
			result.put("d"+COLUMN_MACADDRESS, resHiveAp.getMacAddressFormat()+"&nbsp;");
			result.put("d"+COLUMN_IPADDRESS, resHiveAp.getIpAddress()+"&nbsp;");
			result.put("d"+COLUMN_HOSTNAME, resHiveAp.getHostnameHtml());
			if(resHiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY){
				result.put("d"+COLUMN_WLANPOLICY, "N/A &nbsp;");
			}else{
				ConfigTemplate temp = new ConfigTemplate(ConfigTemplateType.WIRELESS);
				temp.setConfigName(resHiveAp.getConfigTemplateName());
				resHiveAp.setConfigTemplate(temp);
				result.put("d"+COLUMN_WLANPOLICY, resHiveAp.getConfigTemplate().getConfigNameSubstr()+"&nbsp;");
				result.put("d"+COLUMN_WLANPOLICY + "title", resHiveAp.getConfigTemplate().getConfigName());
			}

//			if(resHiveAp.getMapContainer() != null){
//				result.put("d"+COLUMN_LOCATION, resHiveAp.getMapContainer().getMapNameEx()+"&nbsp;");
//			}else{
//				result.put("d"+COLUMN_LOCATION, "&nbsp;");
//			}
			result.put("d"+COLUMN_SOFTWAREVERSION, resHiveAp.getSoftVerString()+"&nbsp;");
			result.put("d"+COLUMN_AUDIT, resHiveAp.getConfigIndicationIcon());
			JSONObject resultAttributes = getHiveApUpdateResultInfo(resHiveAp);
			if (null != resultAttributes) {
				result.put("result", resultAttributes);
			}
			listResults.add(result);
		}
		return listResults;
	}

	private JSONObject getHiveApUpdateResultInfo(HiveAp hiveAp) throws JSONException{
		List<HiveApUpdateResult> updateResultList = QueryUtil.executeQuery(HiveApUpdateResult.class,
					new SortParams("id", false), new FilterParams("nodeId", hiveAp.getMacAddress()), domainId);
		if(!updateResultList.isEmpty()){
			HiveApUpdateResult updateResult = updateResultList.get(0);
			JSONObject resultAttributes = new JSONObject();
			resultAttributes.put("d" + HiveApUpdateResultAction.COLUMN_UPDATE_RATE,
						updateResult.getDownloadRateString());
			resultAttributes.put("d" + HiveApUpdateResultAction.COLUMN_RESULT +"_key", updateResult
					.getResult());
			resultAttributes.put("d" + HiveApUpdateResultAction.COLUMN_RESULT +"_value", updateResult
					.getResultString());
			resultAttributes.put("d" + HiveApUpdateResultAction.COLUMN_DESC, updateResult.getDescriptionTitle());
			boolean hideAction = getShowDomain()
					&& !HmDomain.HOME_DOMAIN.equals(updateResult.getOwner()
							.getDomainName());
			if(!hideAction && updateResult.getActionType() == UpdateParameters.ACTION_REBOOT){
				updateResult.setActionTypeString(hideAction);
				resultAttributes.put("d" + HiveApUpdateResultAction.COLUMN_ACTION + "_key", updateResult.getActionType());
			}
			resultAttributes.put("updateTime", updateResult.getFinishTimeString());
			return resultAttributes;
		}
		return null;
	}

	private boolean isPpskServer(HiveAp hiveAp, ConfigTemplate configTemplate, List<SsidProfile> ssidProfiles){
		if(hiveAp.getDeviceType()==HiveAp.Device_TYPE_HIVEAP && (hiveAp.isDhcp() || hiveAp.getId() == null)){
			return false;
		}
		ConfigTemplate wlan = configTemplate;
		if (wlan == null) {
			hiveAp = QueryUtil.findBoById(HiveAp.class, hiveAp.getId(), this);
			wlan = hiveAp.getConfigTemplate();
			wlan = QueryUtil.findBoById(ConfigTemplate.class, wlan.getId(), this);
		}
		if(wlan == null){
			return false;
		}
		for(ConfigTemplateSsid ssidTemp : wlan.getSsidInterfaces().values()){
			if(ssidTemp != null && ssidTemp.getSsidProfile() != null && ssidTemp.getSsidProfile().getId() != null){
				SsidProfile ssid = null;
				if (ssidProfiles != null) {
					for (SsidProfile ssidProfileTmp : ssidProfiles) {
						if (ssidProfileTmp.getId().equals(ssidTemp.getSsidProfile().getId())) {
							ssid = ssidProfileTmp;
							break;
						}
					}
				}
				if (ssid == null) {
					ssid = QueryUtil.findBoById(SsidProfile.class, ssidTemp.getSsidProfile().getId(), this);
				}
				if(ssid.getAccessMode() == SsidProfile.ACCESS_MODE_PSK && ssid.isEnablePpskSelfReg()
						&& ssid.getPpskServer() != null && ssid.getPpskServer().getId().equals(hiveAp.getId())){
					return true;
				}
				if(ssid.getAccessMode() == SsidProfile.ACCESS_MODE_PSK && ssid.getSsidSecurity() != null &&
						ssid.getSsidSecurity().isBlnMacBindingEnable() &&
						ssid.getPpskServer() != null && ssid.getPpskServer().getId().equals(hiveAp.getId())){
					return true;
				}
				if (hiveAp.isBranchRouter()
						&& ssid.getAccessMode() == SsidProfile.ACCESS_MODE_PSK
						&& ssid.isEnablePpskSelfReg()
						&& ssid.isBlnBrAsPpskServer()){
					return true;
				}
			}
		}
		return false;
	}

	public String getRadioTypeAccessDualStyle(){
		if(NmsUtil.compareSoftwareVersion("4.0.1.0", this.getDataSource().getSoftVer()) > 0){
			return "none";
		}else{
			return "";
		}
	}
	public String getRadioConfigDualNoteStyle(){
		if(NmsUtil.compareSoftwareVersion(this.getDataSource().getSoftVer(), "4.0.1.0") > 0
				&& this.getDataSource().getRadioConfigType() == HiveAp.RADIO_MODE_ACCESS_DUAL){
			return "";
		}else{
			return "none";
		}
	}

	public String getRadioConfigCustomerStyle(){
		if (isFullMode() && getDataSource().isWifi1Available()) {
			return "";
		} else {
			return "none";
		}
	}

	public boolean isEnableEthBridgeDisabled(){
		if(this.getDataSource().getRadioConfigType() == HiveAp.RADIO_MODE_ACCESS_DUAL
				|| this.getDataSource().getRadioConfigType() == HiveAp.RADIO_MODE_ACCESS_ONE){
			return false;
		}else{
			return true;
		}
	}

	public String getEnableEthBridgeNodeStyle(){
		if(this.getDataSource().isEnableEthBridge()){
			return "";
		}else{
			return "none";
		}
	}

	/** support for VPN Gateway
	 * @throws JSONException */

	private DeviceInterface wanInterface;

	private DeviceInterface lanInterface;

	private DeviceInterface branchRouterEth0;
	private DeviceInterface branchRouterEth1;
	private DeviceInterface branchRouterEth2;
	private DeviceInterface branchRouterEth3;
	private DeviceInterface branchRouterEth4;
	private DeviceInterface branchRouterUSB;
	private DeviceInterface branchRouterWifi0;
	private DeviceInterface branchRouterWifi1;

	private int havePPPoE = -1;
	
	private boolean vpnGatewayVrrpEnable;

	private boolean branchRouterVrrpEnable;

	private String vpnGatewayVirtualWanIp;

	private String branchRouterVirtualWanIp;

	private String vpnGatewayVirtualLanIp;

	private String branchRouterVirtualLanIp;

	private boolean branchRouterPreemptEnable;

	private boolean vpnGatewayPreemptEnable;

	private Collection<String>	usbConnectionIndices;
	
	private String[] modemName;
	private String[] displayName;
	private String[] apn;
	private String[] dialupNum;
	private String[] userId;
	private String[] password;
	private Short[] cellularMode;
			
	public EnumItem[] getEnumCellularMode() {
		return MgrUtil.enumItems(
				"enum.usb.medem.cellular.mode.", new int[] { USBModemProfile.CELLULAR_MODE_AUTO,
						USBModemProfile.CELLULAR_MODE_2G,
						USBModemProfile.CELLULAR_MODE_3G,
						USBModemProfile.CELLULAR_MODE_4G});
	}

	public String getModemUsb4Br200LteStyle(){
		if(getDataSource()!=null && getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ){
			return "";
		}
		
		return "none";
	}
	
	public String getModemUsb2OtherStyle(){
		if(getDataSource()!=null && getDataSource().getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200_LTE_VZ){
			return "";
		}
		
		return "none";
	}
	
	private void setDeviceInterface(){
		if(this.getDataSource().getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY){
			if (wanInterface != null) {
				wanInterface.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH0);
			}
			if (lanInterface != null) {
				lanInterface.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH1);
			}
			this.getDataSource().setEth0Interface(this.wanInterface);
			this.getDataSource().setEth1Interface(this.lanInterface);
			this.getDataSource().setEnableVRRP(vpnGatewayVrrpEnable);
			this.getDataSource().setVirtualWanIp(vpnGatewayVirtualWanIp);
			this.getDataSource().setVirtualLanIp(vpnGatewayVirtualLanIp);
			this.getDataSource().setEnablePreempt(vpnGatewayPreemptEnable);

		}
		getIfwanPortTypeMap();
		// BR100 use for AP also can config eth0-eth4
		if((this.getDataSource().isBranchRouter() ||
				this.getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100) &&
				       this.getDataSource().getHiveApModel()!=HiveAp.HIVEAP_MODEL_SR24 && 
				    		 this.getDataSource().getHiveApModel()!=HiveAp.HIVEAP_MODEL_SR2124P && 
				    		 this.getDataSource().getHiveApModel()!=HiveAp.HIVEAP_MODEL_SR2024P && 
				    		 this.getDataSource().getHiveApModel()!=HiveAp.HIVEAP_MODEL_SR2148P && 
				             this.getDataSource().getHiveApModel()!=HiveAp.HIVEAP_MODEL_SR48){
			if (branchRouterEth0 != null && !getDataSource().isSwitchProduct()) {
				this.getDataSource().setEnablePppoe(("3".equals(branchRouterEth0.getConnectionType())));
				branchRouterEth0.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH0);
				if(this.getDataSource().isBr100WorkAsAp()){
					branchRouterEth0.setWanOrder(0);
				}
				this.getDataSource().setEth0Interface(this.branchRouterEth0);
			}
			if (branchRouterEth1 != null) {
				branchRouterEth1.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH1);
				if(this.getDataSource().getRole(branchRouterEth1) != AhInterface.ROLE_WAN){
					branchRouterEth1.setWanOrder(0);
				}
				if(getRadioPsePriority()!=null && getRadioPsePriority().equals(AhInterface.ETH_PSE_PRIORITY_ETH1)) {
					branchRouterEth1.setPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH1);
				} else {
					branchRouterEth1.setPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH2);
				}
				this.getDataSource().setEth1Interface(this.branchRouterEth1);
			}
			if (branchRouterEth2 != null) {
				branchRouterEth2.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH2);
				if(this.getDataSource().getRole(branchRouterEth2) != AhInterface.ROLE_WAN){
					branchRouterEth2.setWanOrder(0);
				}
				if(getRadioPsePriority()!=null && getRadioPsePriority().equals(AhInterface.ETH_PSE_PRIORITY_ETH1)) {
					branchRouterEth2.setPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH2);
				} else {
					branchRouterEth2.setPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH1);
				}
				this.getDataSource().setEth2Interface(this.branchRouterEth2);
			}
			if (branchRouterEth3 != null) {
				branchRouterEth3.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH3);
				if(this.getDataSource().getRole(branchRouterEth3) != AhInterface.ROLE_WAN){
					branchRouterEth3.setWanOrder(0);
				}
				this.getDataSource().setEth3Interface(this.branchRouterEth3);
			}
			if (branchRouterEth4 != null) {
				branchRouterEth4.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH4);
				if(this.getDataSource().getRole(branchRouterEth4) != AhInterface.ROLE_WAN){
					branchRouterEth4.setWanOrder(0);
				}
				this.getDataSource().setEth4Interface(this.branchRouterEth4);
			}
			if (branchRouterUSB != null) {
				branchRouterUSB.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_USB);
				if(this.getDataSource().getRole(branchRouterUSB) != AhInterface.ROLE_WAN){
					branchRouterUSB.setWanOrder(0);
				}
				if (this.getDataSource().isUsbAsCellularModem() && !this.getDataSource().isEnableCellularModem()) {
					branchRouterUSB.setWanOrder(0);
					//branchRouterUSB.setPriority(0);
				}
				
				if(this.getDataSource().isBr100WorkAsAp()){
					branchRouterUSB.setWanOrder(0);
				}
				this.getDataSource().setUSBInterface(this.branchRouterUSB);
			}
			
			if (branchRouterWifi0 != null) {
				branchRouterWifi0.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_WIFI0);
				if(this.getDataSource().getRole(branchRouterWifi0) != AhInterface.ROLE_WAN){
					branchRouterWifi0.setWanOrder(0);
				}
				this.getDataSource().setWifi0Interface(this.branchRouterWifi0);
			}
			if (branchRouterWifi1 != null) {
				branchRouterWifi1.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_WIFI1);
				if(this.getDataSource().getRole(branchRouterWifi1) != AhInterface.ROLE_WAN){
					branchRouterWifi1.setWanOrder(0);
				}
				this.getDataSource().setWifi1Interface(this.branchRouterWifi1);
			}
			
			this.getDataSource().setEnableVRRP(branchRouterVrrpEnable);
			this.getDataSource().setVirtualWanIp(branchRouterVirtualWanIp);
			this.getDataSource().setVirtualLanIp(branchRouterVirtualLanIp);
			this.getDataSource().setEnablePreempt(branchRouterPreemptEnable);
		}

		if(this.getDataSource().getDeviceType() == HiveAp.Device_TYPE_VPN_BR){
			if (wanInterface != null) {
				wanInterface.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH0);
			}
			wanInterface.setEnableDhcp(false);
			this.getDataSource().setEth0Interface(this.wanInterface);
		}
		
		if(deviceIfType != null && deviceIfType.length > 0){
			getDataSource().setPppoeAuthProfile(null);
			getDataSource().setEnablePppoe(false);
			for(int i=0; i<deviceIfType.length; i++){
				DeviceInterface deviceInterface = this.getDataSource().getDeviceInterfaces().get((long)deviceIfType[i]);
				deviceInterface.setWanOrder(0);
				//for switch as router
				if(null != switchWanPortSettings && !switchWanPortSettings.isEmpty()){
					for(DeviceInterface dIntItem : switchWanPortSettings){
						if(dIntItem.getDeviceIfType() == deviceIfType[i]){
							deviceInterface.setWanOrder(dIntItem.getWanOrder());
							//deviceInterface.setAdminState(dIntItem.getAdminState());
							//deviceInterface.setDuplex(dIntItem.getDuplex());
							//deviceInterface.setSpeed(dIntItem.getSpeed());
							deviceInterface.setEnableNat(dIntItem.isEnableNat());
							deviceInterface.setDisablePortForwarding(dIntItem.isDisablePortForwarding());
							deviceInterface.setEnableMaxDownload(dIntItem.isEnableMaxDownload());
							deviceInterface.setEnableMaxUpload(dIntItem.isEnableMaxUpload());
							deviceInterface.setMaxDownload(dIntItem.getMaxDownload());
							deviceInterface.setMaxUpload(dIntItem.getMaxUpload());
							deviceInterface.setConnectionType(dIntItem.getConnectionType());
							switch(Short.parseShort(dIntItem.getConnectionType())){
							   case AhInterface.CONNECTION_STATICIP:
								    deviceInterface.setIpAndNetmask(dIntItem.getIpAndNetmask());
								    deviceInterface.setGateway(dIntItem.getGateway());
								    break;
							   case AhInterface.CONNECTION_PPPOE:
								    if (dIntItem.getPppoeID() != 0 || dIntItem.getPppoeID() != -1) {
								    	deviceInterface.setPppoeID(dIntItem.getPppoeID());
								    	getDataSource().setEnablePppoe(true);
								    	pppoeAuthProfile = dIntItem.getPppoeID();
										PPPoE sd = QueryUtil.findBoById(PPPoE.class, dIntItem.getPppoeID());
										if (null != getDataSource() && null != sd) {
											getDataSource().setPppoeAuthProfile(sd);
										}
									} else {
										getDataSource().setPppoeAuthProfile(null);
										getDataSource().setEnablePppoe(false);
									}
								    break;
								   
							}
							
						}
					}
				}
					deviceInterface.setAdminState(adminState[i]);
//					dInt.setNativeVlan(nativeVlan[i]);
//					dInt.setAllowedVlan(allowedVlan[i]);
					deviceInterface.setDuplex(duplex[i]);
					deviceInterface.setSpeed(speed[i]);
					deviceInterface.setFlowControlStatus(flowControlStatus[i]);
					deviceInterface.setAutoMdix(autoMdix[i]);
//					dInt.setMtu(mtu[i]);
					deviceInterface.setDebounceTimer(debounceTimer[i]);
					deviceInterface.setLldpTransmit(lldpTransmit[i]);
					deviceInterface.setLldpReceive(lldpReceive[i]);
					deviceInterface.setCdpReceive(cdpReceive[i]);
					deviceInterface.setClientReporting(clientReporting[i]);
					deviceInterface.setPortDescription(portDescription[i]);
				
			}
		}
	
	}

	private List<DeviceInterface> dInterfaces;
	private short[] deviceIfType;
	private short[] adminState;
//	private int[] nativeVlan;
//	private String[] allowedVlan;
	private short[] duplex;
	private short[] speed;
	private short[] flowControlStatus;
	private boolean[] autoMdix;
//	private int[] mtu;
	private int[] debounceTimer;
	private boolean[] lldpTransmit;
	private boolean[] lldpReceive;
	private boolean[] cdpReceive;
	private boolean[] clientReporting;
	private String[] portDescription;

	private short oldDeviceType;

	private void prepareSwitchInterface(){
		dInterfaces = new ArrayList<DeviceInterface>();

		for(DeviceInterface dInf : getDataSource().getDeviceInterfaces().values()){
			//switch mode no need dispaly interface usb.
			if(dInf.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_USB && !getDataSource().isBranchRouter()){
				continue;
			}
			if(dInf.getDeviceIfType() >= AhInterface.DEVICE_IF_TYPE_ETH1){
				dInterfaces.add(dInf);
			}
		}

		Collections.sort(dInterfaces, new Comparator<DeviceInterface>(){
			@Override
			public int compare(DeviceInterface o1, DeviceInterface o2) {
				if(o1 == null || o2 == null){
					return -1;
				}
				return o1.getDeviceIfType() - o2.getDeviceIfType();
			}
		});
	}
	private List<DeviceInterface> switchWanPortSettings;

	private boolean isUsbPriorityPrimary;

	public List<DeviceInterface> getSwitchWanPortSettings() {
		return switchWanPortSettings;
	}

	public void setSwitchWanPortSettings(List<DeviceInterface> switchWanPortSettings) {
		this.switchWanPortSettings = switchWanPortSettings;
	}
	

	private void prepareSwitchDeviceInterface(){
		
		if( this.getDataSource().getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER){
			PortGroupProfile  portGroup= this.getDataSource().getPortGroup();
			switchWanPortSettings=new ArrayList<DeviceInterface>();
			
			
			if(null!=portGroup){
			List<Short> ethList=portGroup.getPortFinalValuesByPortType(DeviceInfType.Gigabit, PortAccessProfile.PORT_TYPE_WAN);
			if(ethList!=null && ethList.size()>0){	
				for(Short eth:ethList){
				DeviceInterface  deviceInterface=this.getDataSource().getDeviceInterfaces().get((long)eth);
				deviceInterface.setInterfaceName(deviceInterface.getInterfaceNameEnum());
				switchWanPortSettings.add(deviceInterface);
				}
			}
			List<Short> sfpList=portGroup.getPortFinalValuesByPortType(DeviceInfType.SFP, PortAccessProfile.PORT_TYPE_WAN);	
			if(sfpList!=null && sfpList.size()>0){
					for(Short sfp:sfpList){
						DeviceInterface  sfpdeviceInterface=this.getDataSource().getDeviceInterfaces().get((long)sfp);
						sfpdeviceInterface.setInterfaceName(sfpdeviceInterface.getInterfaceNameEnum());
						switchWanPortSettings.add(sfpdeviceInterface);
					}
				}
			
			}
			
			DeviceInterface  usbdeviceInterface=this.getDataSource().getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_USB);
			usbdeviceInterface.setInterfaceName(usbdeviceInterface.getInterfaceNameEnum());
			switchWanPortSettings.add(usbdeviceInterface);
			
			
//			List<Short> usbList=portGroup.getPortFinalValuesByPortType(DeviceInfType.USB, PortAccessProfile.PORT_TYPE_WAN);	
//			if(usbList!=null && usbList.size()>0){
//				for(Short usb:usbList)
//				{
//					DeviceInterface  usbdeviceInterface=this.getDataSource().getDeviceInterfaces().get((long)usb);
//					usbdeviceInterface.setInterfaceName(usbdeviceInterface.getInterfaceNameEnum());
//					switchWanPortSettings.add(usbdeviceInterface);
//				}
//			}
			
			if(!switchWanPortSettings.isEmpty()){
			
				Collections.sort(switchWanPortSettings,  new Comparator<DeviceInterface>(){
					  public int compare(DeviceInterface obj1, DeviceInterface obj2) {
							  return obj1.getPriority() - obj2.getPriority();
			            }
				});
	
				for(DeviceInterface  deviceInterface : switchWanPortSettings){
					
					//according to init priority to set wanorder
					deviceInterface.setWanOrder(switchWanPortSettings.indexOf(deviceInterface) + 1);
					
					//to determin PPPoE is exist!
					if(null != deviceInterface.getConnectionType() && 
							deviceInterface.getConnectionType().equals(String.valueOf(AhInterface.CONNECTION_PPPOE))){
						havePPPoE = switchWanPortSettings.indexOf(deviceInterface);
						if( null != this.getDataSource().getPppoeAuthProfile()){
							deviceInterface.setPppoeID(this.getDataSource().getPppoeAuthProfile().getId());
						}
					}
				}
				//if usb's priority is primary,hide usb connect states
				DeviceInterface firstDeviceIF = switchWanPortSettings.get(0);
				if(firstDeviceIF.isPortUSB()){
					isUsbPriorityPrimary = true;
				}
			}
			
		}
	}
	
	private void prepareDeviceInterface(){

		getIfwanPortTypeMap();
		// for vpn gateway;
		wanInterface = this.getDataSource().getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_ETH0);
		if(wanInterface == null){
			wanInterface = new DeviceInterface();
			wanInterface.setInterfaceName(MgrUtil.getUserMessage("hiveAp.vpnGateway.if.wan"));
			wanInterface.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH0);
		}

		lanInterface = this.getDataSource().getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_ETH1);
		if(lanInterface == null){
			lanInterface = new DeviceInterface();
			lanInterface.setInterfaceName(MgrUtil.getUserMessage("hiveAp.vpnGateway.if.lan"));
			lanInterface.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH1);
		}

		//for branch router
		branchRouterEth0 = this.getDataSource().getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_ETH0);
		if(branchRouterEth0 == null){
			branchRouterEth0 = new DeviceInterface();
			branchRouterEth0.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth0"));
			branchRouterEth0.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH0);
//			branchRouterEth0.setRole(AhInterface.ROLE_PRIMARY);
		}

		branchRouterEth1 = this.getDataSource().getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_ETH1);
		if(branchRouterEth1 == null){
			branchRouterEth1 = new DeviceInterface();
			branchRouterEth1.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth1"));
			branchRouterEth1.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH1);
			branchRouterEth1.setPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH1);
		}

		branchRouterEth2 = this.getDataSource().getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_ETH2);
		if(branchRouterEth2 == null){
			branchRouterEth2 = new DeviceInterface();
			branchRouterEth2.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth2"));
			branchRouterEth2.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH2);
		}

		if (branchRouterEth1.getPsePriority().equals(AhInterface.ETH_PSE_PRIORITY_ETH1)){
			setRadioPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH1);
		} else {
			setRadioPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH2);
		}


		branchRouterEth3 = this.getDataSource().getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_ETH3);
		if(branchRouterEth3 == null){
			branchRouterEth3 = new DeviceInterface();
			branchRouterEth3.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth3"));
			branchRouterEth3.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH3);
		}

		branchRouterEth4 = this.getDataSource().getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_ETH4);
		if(branchRouterEth4 == null){
			branchRouterEth4 = new DeviceInterface();
			branchRouterEth4.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth4"));
			branchRouterEth4.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH4);
		}

		branchRouterUSB = this.getDataSource().getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_USB);
		if(branchRouterUSB == null){
			branchRouterUSB = new DeviceInterface();
			branchRouterUSB.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.usb"));
			branchRouterUSB.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_USB);
//			branchRouterUSB.setRole(AhInterface.ROLE_BACKUP);
		}

		branchRouterWifi0 = this.getDataSource().getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_WIFI0);
		if(branchRouterWifi0 == null){
			branchRouterWifi0 = new DeviceInterface();
			branchRouterWifi0.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.wifi0"));
			branchRouterWifi0.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_WIFI0);
//			branchRouterWifi0.setRole(AhInterface.ROLE_LAN);
		} else {
			if (branchRouterWifi0.getWanOrder() > 0 && getDataSource().isUsbAsCellularModem()) {
				this.wifi0str = "WAN";
			}
		}
		

		branchRouterWifi1 = this.getDataSource().getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_WIFI1);
		if(branchRouterWifi1 == null){
			branchRouterWifi1 = new DeviceInterface();
			branchRouterWifi1.setInterfaceName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.wifi1"));
			branchRouterWifi1.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_WIFI1);
//			branchRouterWifi1.setRole(AhInterface.ROLE_LAN);
		}
	}

	private void prepareUSBSettings(){

		if(this.getDataSource().getUsbModemList() == null){
			this.getDataSource().setUsbModemList(new ArrayList<USBModemProfile>());
		}
		
		if(defaultUsbModemList == null){
			defaultUsbModemList = NmsUtil.getUSBModemInfo();
		}

		//add new usb modem for Device OS version upgrade
		if(this.getDataSource().getUsbModemList().size() != defaultUsbModemList.size()){
			for(USBModemProfile usbModem : defaultUsbModemList){
				boolean exists = false;
				for(USBModemProfile usbModemSource : this.getDataSource().getUsbModemList()){
					if(usbModem.getModemName().equals(usbModemSource.getModemName())){
						exists = true;
						break;
					}
				}
				if(!exists && NmsUtil.compareSoftwareVersion(this.getDataSource().getSoftVer(), usbModem.getOsVersion()) >= 0){
					this.getDataSource().getUsbModemList().add(usbModem);
				}
			}
		}

		//add new usb modem for Device OS version reduce
		List<USBModemProfile> rmUsbModemList = new ArrayList<USBModemProfile>();
		for(USBModemProfile usbModem : this.getDataSource().getUsbModemList()){
			if(NmsUtil.compareSoftwareVersion(usbModem.getOsVersion(), this.getDataSource().getSoftVer()) > 0){
				rmUsbModemList.add(usbModem);
			} else if("novatel_E362".equals(usbModem.getModemName()) && this.getDataSource().getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200_LTE_VZ){
				rmUsbModemList.add(usbModem);
			} else if(!"novatel_E362".equals(usbModem.getModemName()) && this.getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ){
				rmUsbModemList.add(usbModem);
			}
		}
		if(!rmUsbModemList.isEmpty()){
			this.getDataSource().getUsbModemList().removeAll(rmUsbModemList);
			rmUsbModemList = null;
		}

		//add USB Modem data to GUI array
		if(!this.getDataSource().getUsbModemList().isEmpty()){
			int size = this.getDataSource().getUsbModemList().size();
			this.modemName = new String[size];
			this.displayName = new String[size];
			this.apn = new String[size];
			this.dialupNum = new String[size];
			this.userId = new String[size];
			this.password = new String[size];
			this.cellularMode = new Short[size];
			int index=0;
			for(USBModemProfile usb : this.getDataSource().getUsbModemList()){
				modemName[index] = usb.getModemName();
				displayName[index] = usb.getDisplayName();
				apn[index] = usb.getApn();
				dialupNum[index] = usb.getDialupNum();
				userId[index] = usb.getUserId();
				password[index] = usb.getPassword();
				cellularMode[index] = usb.getCellularMode();
				index++;
			}
		}
	}

	public DeviceInterface getWanInterface(){
		return wanInterface;
	}

	public void setWanInterface(DeviceInterface wanInterface){
		this.wanInterface = wanInterface;
	}

	public DeviceInterface getLanInterface(){
		return lanInterface;
	}

	public void setLanInterface(DeviceInterface lanInterface){
		this.lanInterface = lanInterface;
	}

	private void secondVpnGatewayRefresh(JSONObject jsonObj) throws JSONException{
		jsonObj.put("wanIp", "");
		jsonObj.put("lanIp", "");
		if(this.getSecondVPNGateway() != -1){
			HiveAp secGateway = QueryUtil.findBoById(HiveAp.class, this.getSecondVPNGateway(), this);
			if(secGateway != null && secGateway.getEth0Interface() != null){
				jsonObj.put("wanIp", secGateway.getEth1Interface().getIpAddress());
			}
			if(secGateway != null && secGateway.getEth1Interface() != null){
				jsonObj.put("lanIp", secGateway.getEth1Interface().getIpAddress());
			}
		}
	}

	public Long getSecondVPNGateway(){
		return this.secondVPNGateway;
	}

	public void setSecondVPNGateway(Long secondVPNGateway){
		this.secondVPNGateway = secondVPNGateway;
	}

	public Long getRoutingProfile(){
		return this.routingProfile;
	}

	public void setRoutingProfile(Long routingProfile){
		this.routingProfile = routingProfile;
	}

	public List<CheckItem> getSecondVPNGateways(){
		return this.secondVPNGateways;
	}

	public List<CheckItem> getRoutingProfiles(){
		return this.routingProfiles;
	}

	public String getRadioConfigStyle(){
		return "";
	}

	public String getWifiConfigStyle(){
		return "";
	}

//	public String getVpnGatewayIntStyle(){
//		return "none";
//	}

	public String getEthConfigCwpStyle(){
		return this.getFullModeConfigStyle();
	}

	public String getBrAsRadiusServerDisplayStyle() {
		if (isEasyMode()) {
			return "none";
		}
		if(!getDataSource().isOverWriteRadiusServer()){
			return "none";
		}
		return "";
	}

	public String getRadiusServerLabelValue() {
		if (getBrAsRadiusServerDisplayStyle().equals("")) {
			return getText("hiveAp.radiusServerLabel.br");
		}
		return getText("hiveAp.radiusServerLabel");
	}

	public String getRadiusProxyLabelValue() {
		if (getBrAsRadiusServerDisplayStyle().equals("")) {
			return getText("hiveAp.radiusProxyLabel.br");
		}
		return getText("hiveAp.radiusProxyLabel");
	}

	public String getBrAsPpskServerDisplayStyle() {
//		if (isEasyMode()) {
//			return "none";
//		}
//		if (getDataSource().getDeviceType()!=HiveAp.Device_TYPE_BRANCH_ROUTER) {
//			return "none";
//		}
//		if (getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200) {
//			return "none";
//		}
		return "none";
	}

	public String getBrAsRadiusServerOverrideDisplayDiv() {
		if (isEasyMode()) {
			return "none";
		}
		if (getDataSource().getDeviceType()!=HiveAp.Device_TYPE_BRANCH_ROUTER) {
			return "none";
		}
		if (!getDataSource().isEnabledBrAsRadiusServer()) {
			return "none";
		}
		return "";
	}

	public String getRadiusServerRowDivStyle() {
		if (getDataSource().getHiveApModel()==HiveAp.HIVEAP_MODEL_BR100) {
			return "none";
		}
		if (getDataSource().isBranchRouter()) {
			if (!getDataSource().isEnabledBrAsRadiusServer() || !getDataSource().isEnabledOverrideRadiusServer()) {
				return "none";
			}
		}
		return "";
	}

	public String getRadiusProxyRowDivStyle() {
		if (isFullMode()) {
			if (getDataSource().getHiveApModel()!=HiveAp.HIVEAP_MODEL_BR100) {
				if (getDataSource().isBranchRouter()) {
					if (!getDataSource().isEnabledBrAsRadiusServer() || !getDataSource().isEnabledOverrideRadiusServer()) {
						return "none";
					}
				}
				return "";
			}
		}
		return "none";
	}

	public String getL3RoamingThresholdStyle() {
		return this.getFullModeConfigStyle();
	}

	public String getVPPRSettingStyle(){
		if(this.getDataSource().isEnableVRRP()){
			return "";
		}else{
			return "none";
		}
	}

	public String getVlanSettingStyle(){
		return "";
	}
	
	public String getPoeSettingStyle(){
		if(this.getDataSource().isSupportPoEMode()){
			return "";
		}
		return "none";
	}
	
	//HiveAP 230 not support the Primary ethX settings.
	public String getPoePrimaryEthSectionStyle(){
		if(this.getDataSource().isSupportPoEMode() 
				&& this.getDataSource().getPoeMode() != HiveAp.POE_802_3_AT
				&& this.getDataSource().getHiveApModel() != HiveAp.HIVEAP_MODEL_230){
			return "";
		}
		return "none";
	}

	public String getHiveApTagStyle(){
		return getFullModeConfigStyle();
	}

	public String getDistributedPriorityStyle(){
		return "";
	}

	public String getPppoeAuthProfileStyle(){
		if (getDataSource().isEnablePppoe()){
			return "";
		}
		return "none";
	}
	public String getEth0StaticIpFlag(){
		
		if("2".equals(branchRouterEth0.getConnectionType()))
		{
			return "";
		}
		return "none";
	}
	public String getPppoeAuthFlag(){
		
		if("3".equals(branchRouterEth0.getConnectionType()))
		{
			return "";
		}
		return "none";
	}
	public String getEth1StaticIpFlag(){
		if(this.getDataSource().getRole(branchRouterEth1) == AhInterface.ROLE_WAN && "2".equals(branchRouterEth1.getConnectionType()))
		{
			return "";
		}
		return "none";
	}
	public String getEth2StaticIpFlag(){
		
		if(this.getDataSource().getRole(branchRouterEth2) == AhInterface.ROLE_WAN && "2".equals(branchRouterEth2.getConnectionType()))
		{
			return "";
		}
		return "none";
	}
	public String getEth3StaticIpFlag(){
		
		if(this.getDataSource().getRole(branchRouterEth3) == AhInterface.ROLE_WAN && "2".equals(branchRouterEth3.getConnectionType()))
		{
			return "";
		}
		return "none";
	}
	public String getEth4StaticIpFlag(){
		
		if(this.getDataSource().getRole(branchRouterEth4) == AhInterface.ROLE_WAN && "2".equals(branchRouterEth4.getConnectionType()))
		{
			return "";
		}
		return "none";
	}
	public String getUsbStaticIpFlag(){
		if(this.getDataSource().getRole(branchRouterUSB) == AhInterface.ROLE_WAN && "2".equals(branchRouterUSB.getConnectionType()))
		
		{
			return "";
		}
		return "none";
	}
	public String getWifi0StaticIpFlag(){
		
		if("2".equals(branchRouterWifi0.getConnectionType()))
		{
			return "";
		}
		return "none";
	}
	public String getWifi1StaticIpFlag(){
		if("2".equals(branchRouterWifi1.getConnectionType()))
		
		{
			return "";
		}
		return "none";
	}

	public String getVoipDetailDivStyle() {
		if (getDataSource().isEnabledOverrideVoipSetting()) {
			return "";
		}
		return "none";

	}

	public boolean getVoipEth0DnReadOnly() {
		if (branchRouterEth0!=null) {
			if (branchRouterEth0.isEnableMaxDownload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}
	public boolean getVoipEth0UpReadOnly() {
		if (branchRouterEth0!=null) {
			if (branchRouterEth0.isEnableMaxUpload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}

	public boolean getVoipUsbDnReadOnly() {
		if (branchRouterUSB!=null) {
			if (branchRouterUSB.isEnableMaxDownload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}
	public boolean getVoipUsbUpReadOnly() {
		if (branchRouterUSB!=null) {
			if (branchRouterUSB.isEnableMaxUpload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}

	public boolean getVoipEth1DnReadOnly() {
		if (branchRouterEth1!=null) {
			if (branchRouterEth1.isEnableMaxDownload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}
	public boolean getVoipEth1UpReadOnly() {
		if (branchRouterEth1!=null) {
			if (branchRouterEth1.isEnableMaxUpload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}

	public boolean getVoipEth2DnReadOnly() {
		if (branchRouterEth2!=null) {
			if (branchRouterEth2.isEnableMaxDownload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}
	public boolean getVoipEth2UpReadOnly() {
		if (branchRouterEth2!=null) {
			if (branchRouterEth2.isEnableMaxUpload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}

	public boolean getVoipEth3DnReadOnly() {
		if (branchRouterEth3!=null) {
			if (branchRouterEth3.isEnableMaxDownload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}
	public boolean getVoipEth3UpReadOnly() {
		if (branchRouterEth3!=null) {
			if (branchRouterEth3.isEnableMaxUpload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}

	public boolean getVoipEth4DnReadOnly() {
		if (branchRouterEth4!=null) {
			if (branchRouterEth4.isEnableMaxDownload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}
	public boolean getVoipEth4UpReadOnly() {
		if (branchRouterEth4!=null) {
			if (branchRouterEth4.isEnableMaxUpload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}

	public boolean getVoipWifi0DnReadOnly() {
		if (branchRouterWifi0!=null) {
			if (branchRouterWifi0.isEnableMaxDownload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}
	public boolean getVoipWifi0UpReadOnly() {
		if (branchRouterWifi0!=null) {
			if (branchRouterWifi0.isEnableMaxUpload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}

	public boolean getVoipWifi1DnReadOnly() {
		if (branchRouterWifi1!=null) {
			if (branchRouterWifi1.isEnableMaxDownload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}
	public boolean getVoipWifi1UpReadOnly() {
		if (branchRouterWifi1!=null) {
			if (branchRouterWifi1.isEnableMaxUpload() && getDataSource().isEnabledOverrideVoipSetting()) {
				return false;
			}
		}
		return true;
	}

	public DeviceInterface getBranchRouterEth0(){
		return this.branchRouterEth0;
	}

	public void setBranchRouterEth0(DeviceInterface branchRouterEth0){
		this.branchRouterEth0 = branchRouterEth0;
	}

	public DeviceInterface getBranchRouterEth1(){
		return this.branchRouterEth1;
	}

	public void setBranchRouterEth1(DeviceInterface branchRouterEth1){
		this.branchRouterEth1 = branchRouterEth1;
	}

	public DeviceInterface getBranchRouterEth2(){
		return this.branchRouterEth2;
	}

	public void setBranchRouterEth2(DeviceInterface branchRouterEth2){
		this.branchRouterEth2 = branchRouterEth2;
	}

	public DeviceInterface getBranchRouterEth3(){
		return this.branchRouterEth3;
	}

	public void setBranchRouterEth3(DeviceInterface branchRouterEth3){
		this.branchRouterEth3 = branchRouterEth3;
	}

	public DeviceInterface getBranchRouterEth4(){
		return this.branchRouterEth4;
	}

	public void setBranchRouterEth4(DeviceInterface branchRouterEth4){
		this.branchRouterEth4 = branchRouterEth4;
	}

	public DeviceInterface getBranchRouterUSB(){
		return this.branchRouterUSB;
	}

	public DeviceInterface getBranchRouterWifi0() {
		return branchRouterWifi0;
	}

	public void setBranchRouterUSB(DeviceInterface branchRouterUSB){
		this.branchRouterUSB = branchRouterUSB;
	}

	public void setBranchRouterWifi0(DeviceInterface branchRouterWifi0) {
		this.branchRouterWifi0 = branchRouterWifi0;
	}

	public DeviceInterface getBranchRouterWifi1() {
		return branchRouterWifi1;
	}

	public void setBranchRouterWifi1(DeviceInterface branchRouterWifi1) {
		this.branchRouterWifi1 = branchRouterWifi1;
	}

	public boolean isVpnGatewayVrrpEnable(){
		return this.vpnGatewayVrrpEnable;
	}

	public void setVpnGatewayVrrpEnable(boolean vpnGatewayVrrpEnable){
		this.vpnGatewayVrrpEnable = vpnGatewayVrrpEnable;
	}

	public boolean isBranchRouterVrrpEnable(){
		return this.branchRouterVrrpEnable;
	}

	public void setBranchRouterVrrpEnable(boolean branchRouterVrrpEnable){
		this.branchRouterVrrpEnable = branchRouterVrrpEnable;
	}

	public String getVpnGatewayVirtualWanIp(){
		return this.vpnGatewayVirtualWanIp;
	}

	public void setVpnGatewayVirtualWanIp(String vpnGatewayVirtualWanIp){
		this.vpnGatewayVirtualWanIp = vpnGatewayVirtualWanIp;
	}

	public String getBranchRouterVirtualWanIp(){
		return this.branchRouterVirtualWanIp;
	}

	public void setBranchRouterVirtualWanIp(String branchRouterVirtualWanIp){
		this.branchRouterVirtualWanIp = branchRouterVirtualWanIp;
	}

	public String getVpnGatewayVirtualLanIp(){
		return this.vpnGatewayVirtualLanIp;
	}

	public void setVpnGatewayVirtualLanIp(String vpnGatewayVirtualLanIp){
		this.vpnGatewayVirtualLanIp = vpnGatewayVirtualLanIp;
	}

	public String getBranchRouterVirtualLanIp(){
		return this.branchRouterVirtualLanIp;
	}

	public void setBranchRouterVirtualLanIp(String branchRouterVirtualLanIp){
		this.branchRouterVirtualLanIp = branchRouterVirtualLanIp;
	}

	public boolean isVpnGatewayPreemptEnable(){
		return this.vpnGatewayPreemptEnable;
	}

	public void setVpnGatewayPreemptEnable(boolean vpnGatewayPreemptEnable){
		this.vpnGatewayPreemptEnable = vpnGatewayPreemptEnable;
	}

	public boolean isBranchRouterPreemptEnable(){
		return this.branchRouterPreemptEnable;
	}

	public void setBranchRouterPreemptEnable(boolean branchRouterPreemptEnable){
		this.branchRouterPreemptEnable = branchRouterPreemptEnable;
	}

	public EnumItem[] getUsbConnectNeeded() {
		return new EnumItem[] { new EnumItem(HiveAp.USB_CONNECTION_MODEL_NEEDED,
				getText("hiveAp.brRouter.usb.connect.needed")) };
	}

	public EnumItem[] getUsbConnectAlways() {
		return new EnumItem[] { new EnumItem(HiveAp.USB_CONNECTION_MODEL_ALWAYS,
				getText("hiveAp.brRouter.usb.connect.always")) };
	}

	public void setUsbConnectionIndices(Collection<String>	usbConnectionIndices){
		this.usbConnectionIndices = usbConnectionIndices;
	}

	public String getVrrpId(){
		if(this.getDataSource().getVrrpId() > 0){
			return String.valueOf(this.getDataSource().getVrrpId());
		}else{
			return "";
		}
	}

	public void setVrrpId(String vrrpId){
		try{
			int intVrrpId = Integer.valueOf(vrrpId);
			this.getDataSource().setVrrpId(intVrrpId);
		}catch(Exception ex){

		}
	}

	public String getVrrpPriority(){
		if(this.getDataSource().getVrrpPriority() > 0){
			return String.valueOf(this.getDataSource().getVrrpPriority());
		}else{
			return "";
		}
	}

	public void setVrrpPriority(String vrrpPriority){
		try{
			int intVrrpPriority = Integer.valueOf(vrrpPriority);
			this.getDataSource().setVrrpPriority(intVrrpPriority);
		}catch(Exception ex){

		}
	}

	protected void removeSelectedUsbConnection() {
		if (usbConnectionIndices != null) {
			Collection<USBModemProfile> removeList = new Vector<USBModemProfile>();
			for (String usbConnectionIndex : dynamicRouteIndices) {
				try {
					int index = Integer.parseInt(usbConnectionIndex);
					if (index < getDataSource().getUsbModemList().size()) {
						removeList.add(getDataSource().getUsbModemList().get(
								index));
					}
				} catch (NumberFormatException e) {
					return;
				}
			}
			getDataSource().getUsbModemList().removeAll(removeList);
		}
	}

	public int getUsbGridCount() {
		return getDataSource().getUsbModemList().size() < 3 ? (3 - getDataSource()
				.getUsbModemList().size())
				: 0;
	}

//	public String getRouterWanRowStyle(){
//		return "none";
//	}

//	public String getRouterNetworkSettingStyle(){
//		return "none";
//	}

	public String getDhcpServerOptionsRowStyle(){
		if(this.getDataSource().getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
			return "";
		}else{
			return "none";
		}
	}

	public String getHiveApNetworkSettingsStyle(){
		if(this.getDataSource().getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
			return "";
		}else{
			return "none";
		}
	}

	public String getBrRouterVrrpSettingStyle(){
		if(this.getDataSource().isEnableVRRP()){
			return "";
		}else{
			return "none";
		}
	}

	public String getBrRouterEth0DhcpSettingsStyle(){
		if(this.getDataSource().getEth0Interface() != null && !this.getDataSource().getEth0Interface().isEnableDhcp()){
			return "";
		} else {
			return "none";
		}
	}

	public String getLanBindConfigStyle(){
		return "none";
	}

	public String getBrRouteIntervalStyle(){
		if(this.getDataSource().isEnableOverrideBrPMTUD()){
			return "";
		}else {
			return "none";
		}
	}

//	public String getLan1Style(){
//		return "none";
//	}
//
//	public String getLan2Style(){
//		return "none";
//	}
//
//	public String getLan3Style(){
//		return "none";
//	}
//
//	public String getLan4Style(){
//		return "none";
//	}


	public String getEth1RoleStyle(){
		if(getDataSource().getRole(this.getBranchRouterEth1()) == AhInterface.ROLE_WAN){
			return "";
		}
		return "none";
	}

	public String getEth2RoleStyle(){
		if(getDataSource().getRole(this.getBranchRouterEth2()) == AhInterface.ROLE_WAN){
			return "";
		}
		return "none";
	}

	public String getEth3RoleStyle(){
		if(getDataSource().getRole(this.getBranchRouterEth3()) == AhInterface.ROLE_WAN){
			return "";
		}
		return "none";
	}

	public String getEth4RoleStyle(){
		if(getDataSource().getRole(this.getBranchRouterEth4()) == AhInterface.ROLE_WAN){
			return "";
		}
		return "none";
	}

	public String getWifi0RoleStyle(){
		if(getDataSource().getRole(this.getBranchRouterWifi0()) == AhInterface.ROLE_WAN){
			return "";
		}
		return "none";
	}

	public String getWifi1RoleStyle(){
		if(getDataSource().getRole(this.getBranchRouterWifi1()) == AhInterface.ROLE_WAN){
			return "";
		}
		return "none";
	}
	
	public String getMultiLockRealmNameDisplayStyle(){
		if (getDataSource()!=null && getDataSource().isMultiChangeLockRealmName()) {
			return "";
		}
		
		return "none";
	}

	// for scheduler dialog
	private String schedulerListName;

	public String getSchedulerListName() {
		return schedulerListName;
	}

	public void setSchedulerListName(String schedulerListName) {
		this.schedulerListName = schedulerListName;
	}

	public String getHiveApName() {
		return MgrUtil.getEnumString("enum.hiveAp.deviceType." + HiveAp.Device_TYPE_HIVEAP);
	}

	public String getBRName() {
		return MgrUtil.getEnumString("enum.hiveAp.deviceType." + HiveAp.Device_TYPE_BRANCH_ROUTER);
	}

	public String getCVGName() {
		return MgrUtil.getEnumString("enum.hiveAp.deviceType." + HiveAp.Device_TYPE_VPN_GATEWAY);
	}

//	public boolean isEnableDyRouting(){
//		if(this.getDataSource().getRoutingProfile() == null){
//			return false;
//		}else{
//			return this.getDataSource().getRoutingProfile().isEnableRouting();
//		}
//	}
//
//	public EnumItem[] getRoutingTypeList(){
//		return RoutingProfile.ROUTING_TYPE;
//	}

	//for vpnGateway dialog in the vpnservice.jsp
	private boolean vpnGateWayDlg;

	public boolean isVpnGateWayDlg() {
		return vpnGateWayDlg;
	}

	public void setVpnGateWayDlg(boolean vpnGateWayDlg) {
		this.vpnGateWayDlg = vpnGateWayDlg;
	}

	//for routingProfiles
	private String bgpNeighbors;

	private Integer keepalive;

	private String bgpRouterId;

	private String routerId;

	private String area;

	public String getBgpRouterId() {
		return bgpRouterId;
	}

	public void setBgpRouterId(String bgpRouterId) {
		this.bgpRouterId = bgpRouterId;
	}

	public String getRouterId() {
		return routerId;
	}

	public void setRouterId(String routerId) {
		this.routerId = routerId;
	}

	public Integer getKeepalive() {
		return keepalive;
	}

	public void setKeepalive(Integer keepalive) {
		this.keepalive = keepalive;
	}

	public String getBgpNeighbors() {
		return bgpNeighbors;
	}

	public void setBgpNeighbors(String bgpNeighbors) {
		this.bgpNeighbors = bgpNeighbors;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getEnableDynamicRoutingStyle(){
		if (getDataSource().getRoutingProfile()!=null && getDataSource().getRoutingProfile().isEnableDynamicRouting()) {
			return "";
		}
		return "none";
	}

	public String getUseMD5Style(){
		if (getDataSource().getRoutingProfile()!=null && getDataSource().getRoutingProfile().isUseMD5()) {
			return "";
		}
		return "none";
	}

	public String getBgpStyle(){
		if (getDataSource().getRoutingProfile()!=null && getDataSource().getRoutingProfile().getTypeFlag() == 3) {
			return "";
		}
		return "none";
	}

	public String getOspfStyle(){
		if (getDataSource().getRoutingProfile()==null || getDataSource().getRoutingProfile().getTypeFlag() == 2) {
			return "";
		}
		return "none";
	}

	private List<CheckItem> dynamicRoutingList;

	public List<CheckItem> getDynamicRoutingList() {
		if (dynamicRoutingList == null) {
			dynamicRoutingList = new ArrayList<CheckItem>();
		}
		dynamicRoutingList.clear();
		CheckItem item;
		item = new CheckItem((long)RoutingProfile.ENABLE_DRP_RIPV2, "RIPv2");
		dynamicRoutingList.add(item);
		item = new CheckItem((long)RoutingProfile.ENABLE_DRP_OSPF, "OSPF");
		dynamicRoutingList.add(item);
		//item = new CheckItem((long)RoutingProfile.ENABLE_DRP_BGP, "BGP");
		//dynamicRoutingList.add(item);
		///item = new CheckItem((long)RoutingProfile.ENABLE_DRP_NONE, "NONE");
		//dynamicRoutingList.add(item);
		return dynamicRoutingList;
	}

	public short getDefaultDynamicRouting(){
		if (getDataSource().getRoutingProfile()!=null) {
			return getDataSource().getRoutingProfile().getTypeFlag();
		}
		return RoutingProfile.ENABLE_DRP_OSPF;
	}

	public void setDynamicRoutingList(List<CheckItem> dynamicRoutingList) {
		this.dynamicRoutingList = dynamicRoutingList;
	}

	public String[] getPassword() {
		return password;
	}

	public void setPassword(String[] password) {
		this.password = password;
	}

	public String[] getApn() {
		return apn;
	}

	public void setApn(String[] apn) {
		this.apn = apn;
	}

	public String[] getDialupNum() {
		return dialupNum;
	}

	public void setDialupNum(String[] dialupNum) {
		this.dialupNum = dialupNum;
	}

	public String[] getUserId() {
		return userId;
	}

	public void setUserId(String[] userId) {
		this.userId = userId;
	}

	public String[] getModemName() {
		return modemName;
	}

	public void setModemName(String[] modemName) {
		this.modemName = modemName;
	}

	public String[] getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String[] displayName) {
		this.displayName = displayName;
	}

	public Short[] getCellularMode() {
		return cellularMode;
	}

	public void setCellularMode(Short[] cellularMode) {
		this.cellularMode = cellularMode;
	}

	public List<CheckItem> getAvailableIpTracks() {
		return getBoCheckItems("trackName", MgmtServiceIPTrack.class,
				null, CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	private Long vpnIpTrackId;

	public Long getVpnIpTrackId()
	{
		return vpnIpTrackId;
	}

	public void setVpnIpTrackId(Long vpnIpTrackId)
	{
		this.vpnIpTrackId = vpnIpTrackId;
	}

	public String getEnableEthSetting() {
//		if (this.getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100) {
//			return "true";
//		} else {
			return "false";
//		}
	}

	public Long getEthUserProfileId() {
		return ethUserProfileId;
	}

	public void setEthUserProfileId(Long ethUserProfileId) {
		this.ethUserProfileId = ethUserProfileId;
	}

	private Long previousNPId = -1L;

	public Long getPreviousNPId() {
		return previousNPId;
	}

	public void setPreviousNPId(Long previousNPId) {
		this.previousNPId = previousNPId;
	}

	private Long defaultDisplayNetworkPolicyId = -1L;

	public Long getDefaultDisplayNetworkPolicyId() {
		return defaultDisplayNetworkPolicyId;
	}

	private boolean defaultDisplayNpWirelessRoutingEnabled;

	public boolean isDefaultDisplayNpWirelessRoutingEnabled() {
		return defaultDisplayNpWirelessRoutingEnabled;
	}

	private Long selectNetworkPolicyId;

	public Long getSelectNetworkPolicyId() {
		return selectNetworkPolicyId;
	}

	public void setSelectNetworkPolicyId(Long selectNetworkPolicyId) {
		this.selectNetworkPolicyId = selectNetworkPolicyId;
	}

	private List<ConfigTemplate> getNetworkPolicyFilterList() {
		List<ConfigTemplate> resList = new ArrayList<ConfigTemplate>();
		if (selectNetworkPolicyId == null || selectNetworkPolicyId.compareTo(0L) <= 0) {
			return resList;
		}
		ConfigTemplate config = QueryUtil.findBoById(ConfigTemplate.class, selectNetworkPolicyId);
		ConfigTemplate defConfig = QueryUtil.findBoByAttribute(ConfigTemplate.class, "configName",
				BeParaModule.DEFAULT_DEVICE_GROUP_NAME, this.getUserContext().getOwner().getId());
		if (config != null && (DEFAULT_FILTER_CURRENT.equals(this.filter) || DEFAULT_FILTER_CURRENT_DEFAULT.equals(this.filter))) {
			resList.add(config);
		}
		if(defConfig != null && DEFAULT_FILTER_CURRENT_DEFAULT.equals(this.filter)){
			resList.add(defConfig);
		}
		return resList;
	}
	
	private boolean checkBrStaticRoute() throws Exception{
		if(HiveAp.Device_TYPE_BRANCH_ROUTER == getDataSource().getDeviceType()){
			if(getDataSource().getIpRoutes() != null){
				for(int i=0;i<getDataSource().getIpRoutes().size();i++){
					List<String> wanIpAddressList = getWanIpAddressAndNetmask();
					boolean isSameSubnet = false;
					for(String ipAndNetmask : wanIpAddressList){
						isSameSubnet = checkWanIpAndGatewayInSameNetwork(ipAndNetmask,getDataSource().getIpRoutes().get(i).getGateway());
						if(isSameSubnet){
							break;
						}
					}
					
					if(!validateBrGateway(getDataSource().getIpRoutes().get(i).getGateway(), 0, isSameSubnet)){
						if("1".equals(jsonObject.get("errFlag").toString())){
							errMsgTmp = "Gateway IP address is invalid on this router or "+MgrUtil.getUserMessage(
									"error.hiveap.gateway.sameSubnet", new String[]{"Port WAN IP Address","Gateway"}) +" in "+(i+1)+" row of Static Routes.";
						} else {
							errMsgTmp = "Gateway IP address cannot be in the DHCP pool of the interfaces on the router or "+MgrUtil.getUserMessage(
									"error.hiveap.gateway.sameSubnet", new String[]{"WAN IP Address","Gateway"}) +" in "+(i+1)+" row of Static Routes.";
						}
						addActionError(errMsgTmp);
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private boolean checkIsNetworkPolicyProper4Device(HiveAp hiveAp) {
		// only work in fullMode
		if (!isFullMode()) {
			return true;
		}
		if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY) {
			return true;
		}

		if (hiveAp.isBranchRouter() &&
				(!hiveAp.getConfigTemplate().getConfigType().isRouterContained()
				|| isDefaultTemplate(hiveAp))) {
			errMsgTmp = MgrUtil.getUserMessage("error.config.hiveAp.networkpolicy.type.notmatch",
					new String[]{hiveAp.getConfigTemplate().getConfigName(), "routers", "routing"});
			addActionError(errMsgTmp);
			return false;
		}
		if (hiveAp.isSwitch() &&
				((!hiveAp.getConfigTemplate().getConfigType().isSwitchContained() && 
				!hiveAp.getConfigTemplate().getConfigType().isBonjourOnly())
				|| isDefaultTemplate(hiveAp))) {
			errMsgTmp = MgrUtil.getUserMessage("error.config.hiveAp.networkpolicy.type.notmatch",
					new String[]{hiveAp.getConfigTemplate().getConfigName(), "switches", "switching"});
			addActionError(errMsgTmp);
			return false;
		}
		
		if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
			boolean checkPass = false;
			if(isDefaultTemplate(hiveAp)){
				checkPass = false;
			}else if(hiveAp.getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_BONJOUR_SERVICE)){
				checkPass = hiveAp.getConfigTemplate().getConfigType().isWirelessContained() || 
						hiveAp.getConfigTemplate().getConfigType().isBonjourOnly();
			}else{
				checkPass = hiveAp.getConfigTemplate().getConfigType().isWirelessContained();
			}
			
			if(!checkPass){
				errMsgTmp = MgrUtil.getUserMessage("error.config.hiveAp.networkpolicy.type.notmatch",
						new String[]{hiveAp.getConfigTemplate().getConfigName(), "APs", "wireless access"});
				addActionError(errMsgTmp);
				return false;
			}
		}

		/*boolean isWirelessRouterNp = hiveAp.getConfigTemplate().isBlnWirelessRouter();*/
//		boolean isWirelessRouterNp = hiveAp.getConfigTemplate().getConfigType().isTypeSupportOr(ConfigTemplateType.ROUTER|ConfigTemplateType.SWITCH);
//		boolean isDeviceHiveAp = true;
//		if (hiveAp.getDeviceType() != HiveAp.Device_TYPE_HIVEAP) {
//			isDeviceHiveAp = false;
//		}
//
//		// can not assign wireless only network policy to BR & CVG.
//		if (!isDeviceHiveAp && !isWirelessRouterNp) {
//			errMsgTmp = MgrUtil.getUserMessage("error.config.hiveAp.wireless.only.for.hiveAp", hiveAp.getConfigTemplate().getConfigName());
//			addActionError(errMsgTmp);
//			return false;
//		}

		return true;
	}

	private boolean checkIsNetworkPolicyProper4DeviceMulti(HiveAp hiveAp, ConfigTemplate npTmp) {
		// only work in fullMode
		if (!isFullMode()) {
			return true;
		}
		/*boolean isWirelessRouterNp = npTmp.isBlnWirelessRouter();*/
//		boolean isWirelessRouterNp = npTmp.getConfigType().isTypeSupportOr(ConfigTemplateType.ROUTER|ConfigTemplateType.SWITCH);
//		boolean isDeviceHiveAp = true;
//		if (hiveAp.getDeviceType() != HiveAp.Device_TYPE_HIVEAP) {
//			isDeviceHiveAp = false;
//		}
//
//		// can not assign wireless only network policy to BR & CVG.
//		if (!isDeviceHiveAp && !isWirelessRouterNp) {
//			addActionError(MgrUtil.getUserMessage("error.config.hiveAp.wireless.only.for.hiveAp", npTmp.getConfigName()));
//			return false;
//		}

		if (hiveAp.isBranchRouter() &&
				!npTmp.getConfigType().isRouterContained()) {
			addActionError(MgrUtil.getUserMessage("error.config.hiveAp.networkpolicy.type.notmatch",
					new String[]{npTmp.getConfigName(), "routers", "routing"}));
			return false;
		}
		if (hiveAp.isSwitch() &&
				!npTmp.getConfigType().isSwitchContained() && 
				!npTmp.getConfigType().isBonjourOnly()) {
			addActionError(MgrUtil.getUserMessage("error.config.hiveAp.networkpolicy.type.notmatch",
					new String[]{npTmp.getConfigName(), "switches", "switching"}));
			return false;
		}
		if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP &&
				!npTmp.getConfigType().isWirelessContained() &&
				!npTmp.getConfigType().isBonjourOnly()) {
			addActionError(MgrUtil.getUserMessage("error.config.hiveAp.networkpolicy.type.notmatch",
					new String[]{hiveAp.getConfigTemplate().getConfigName(), "APs", "wireless access"}));
			return false;
		}

		return true;
	}

	private String errMsgTmp;

	private short wifi0ModeBR;

	private short wifi1ModeBR;

	public void setWifi0ModeBR(short wifi0ModeBR) {
		this.wifi0ModeBR = wifi0ModeBR;
	}

	public short getWifi0ModeBR(){
		return this.wifi0ModeBR;
	}

	public short getWifi1ModeBR() {
		return wifi1ModeBR;
	}

	public void setWifi1ModeBR(short wifi1ModeBR) {
		this.wifi1ModeBR = wifi1ModeBR;
	}

	/*-
	private String checkForMultiEdit(List<Long> lstSelectIds){
		String errorMsg = null;
		boolean existsBR=false, existsCVG=false;
		List<HiveAp> hiveApList = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams("id", lstSelectIds));
		for(HiveAp hiveAp : hiveApList){
			if(hiveAp.isVpnGateway()){
				existsCVG = true;
			}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER){
				existsBR = true;
			}
		}
		if(existsBR){
			errorMsg = MgrUtil.getUserMessage("error.hiveap.multiEdit.br");
		}
		if(existsCVG){
			errorMsg = MgrUtil.getUserMessage("error.hiveap.multiEdit.cvg");
		}

		return errorMsg;
	}*/

	/**
	 * TA2086: Concatenates the VA image link for VPN Gateways
	 *
	 * @author Yunzhi Lin
	 * - Time: Nov 1, 2011 4:44:02 PM
	 * @return the image link or empty string
	 */
	public String getVaImageURL() {
		String helpPath = NmsUtil.getOEMCustomer().getHelpLink();

		String urlSuffix = MgrUtil.getUserMessage("hiveAp.update.cvg.url.suffix");
		String imageUrl;
		if(StringUtils.isBlank(urlSuffix)) {
			String folder = "ref";
			String imageName = "cvg.ova";
			String pathSeparator = "/";
			imageUrl = helpPath + pathSeparator + folder + pathSeparator + imageName;
		} else {
			imageUrl = helpPath + urlSuffix;
		}
		return StringUtils.isBlank(helpPath) ? "" : imageUrl;
	}

	private void prepareNetworkPolicyWirelessRouting() {
		if (getDataSource() != null) {
			if (getDataSource().getConfigTemplate() != null) {
				//TODO: check it for network policy type re-design
				/*setWirelessRoutingEnable(getDataSource().getConfigTemplate().isBlnWirelessRouter());*/
			}
		}
	}

	public boolean isBlnForceUsingWREnable() {
		return true;
	}

	public String getInterNetIpInput() {
		return interNetIpInput;
	}

	public void setInterNetIpInput(String interNetIpInput) {
		this.interNetIpInput = interNetIpInput;
	}

	public String getInterNetMaskInput() {
		return interNetMaskInput;
	}

	public void setInterNetMaskInput(String interNetMaskInput) {
		this.interNetMaskInput = interNetMaskInput;
	}

	public boolean getDistributeNet() {
		return distributeNet;
	}

	public void setDistributeNet(boolean distributeNet) {
		this.distributeNet = distributeNet;
	}

	public Collection<String> getIntNetworkIndices() {
		return intNetworkIndices;
	}

	public void setIntNetworkIndices(Collection<String> intNetworkIndices) {
		this.intNetworkIndices = intNetworkIndices;
	}

	public Collection<String> getBrStaticRouteingIndices() {
		return brStaticRouteingIndices;
	}

	public void setBrStaticRouteingIndices(
			Collection<String> brStaticRouteingIndices) {
		this.brStaticRouteingIndices = brStaticRouteingIndices;
	}

	/*
	 * only used when you prepare hiveap list
	 */
	private void setEnablePageAutoRefreshSetting() {
		enablePageAutoRefreshSetting = !"manageAPGuid".equals(listType);
	}

	public boolean isBlnResetColumnItems() {
		return "manageAPGuid".equals(listType)
				&& "resetColumns".equals(operation);
	}

	public EnumItem[] getRadioNasType1() {
		return new EnumItem[] { new EnumItem(HiveAp.USE_AP_HOSTNAME_AS_NAS_IDE,
				getText("hiveAp.nasIdentifier.ap.hostname")) };
	}

	public EnumItem[] getRadioNasType2() {
		return new EnumItem[] { new EnumItem(HiveAp.USE_CUSTOMIZED_NAS_IDE,
				getText("hiveAp.nasIdentifier.customized")) };
	}

	public String getCustomizedNasIdenReadonly() {
		if (getDataSource().getNasIdentifierType() == HiveAp.USE_CUSTOMIZED_NAS_IDE) {
			return "false";
		} else {
			return "true";
		}
	}

	private void setCustomizedNasIdentifier() {
		if (getDataSource().getNasIdentifierType() != HiveAp.USE_CUSTOMIZED_NAS_IDE) {
			getDataSource().setCustomizedNasIdentifier(null);
		}
	}

	/**
	 * Criterion: AP330/AP350 can be changed from AP to BR only when HiveOS is newer than 5.0.1.0
	 *
	 * @return boolean
	 */
	public boolean isDeviceUseForBr() {
		if (getDataSource() != null
				&& getDataSource().getId() != null
				&& (getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_330
						|| getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_350)
				&& NmsUtil.compareSoftwareVersion(getDataSource().getSoftVer(), "5.0.1.0") < 0) {
			return false;
		}
		return true;
	}

	public boolean isDeviceUseForAp() {
		if (getDataSource() != null
				&& getDataSource().getId() != null
				&& (getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100 ||
					    HiveAp.isCVGAppliance(getDataSource().getHiveApModel()))
				&& NmsUtil.compareSoftwareVersion(getDataSource().getSoftVer(), "5.1.1.0") < 0) {
			return false;
		}
		return true;
	}

	//*********** Storm Control ******************//
	protected void prepareStormControls(){
		if(null != getDataSource().getStormControlList() && !getDataSource().getStormControlList().isEmpty()){
			PortGroupProfile portGroup = getDataSource().getPortGroup();

			//set storm control data from network policy when don't checked Override
			if(!getDataSource().isEnableOverrideStormControl()){
				//if network don't set port config, load default value for storm control (BYTE + BPS)
				if(portGroup == null){
					short stormControlMode = ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_BYTE;
					getDataSource().setSwitchStormControlMode(stormControlMode);
					for(ConfigTemplateStormControl stormControl : getDataSource().getStormControlList()){
						stormControl.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID);
						prepareSingleStormControl(true,stormControlMode,stormControl);
					}
				} else {
					Map<Short,Short> interface2PortTypeMap = getIfPortTypeMap(portGroup);
					setStormControlFromNP(interface2PortTypeMap);
				}

			} else {
				short stormControlMode = getDataSource().getSwitchStormControlMode();
				for(ConfigTemplateStormControl stormControl : getDataSource().getStormControlList()){
					prepareSingleStormControl(!isPortStromEnable(stormControl),stormControlMode,stormControl);
				}
			}
		}
	}

	private ConfigTemplateStormControl getStormControlFromNP(String interfaceType){
		ConfigTemplateStormControl stormControl = null;
		if(interfaceType == null || "".equals(interfaceType)){
			return stormControl;
		}
		TreeMap<String, ConfigTemplateStormControl> stormControlMap = getDataSource().getConfigTemplate().getStormControlTreeMap();

		if(stormControlMap == null){
			return stormControl;
		}
		stormControl = stormControlMap.get(interfaceType);

		return stormControl;
	}
	private boolean flageth0=false;
	private boolean flageth1=false;
	private boolean flageth2=false;
	private boolean flageth3=false;
	private boolean flageth4=false;
	private boolean flagusb0=true;
	
	private String eth0str ="WAN";
	private String eth1str ="LAN";
	private String eth2str ="LAN ";
	private String eth3str ="LAN";
	private String eth4str ="LAN";
	private String usbstr  ="WAN";
	private String wifi0str = "LAN";
	public final int MAX_WAN_NUM_4AP = 3;
	public final String ROUTER_PORT_WAN = "WAN";
	public final String ROUTER_PORT_LAN = "LAN";
	

	public String getWifi0str() {
		return wifi0str;
	}

	public void setWifi0str(String wifi0str) {
		this.wifi0str = wifi0str;
	}
	
	public String getEth1str() {
		return eth1str;
	}

	public void setEth1str(String eth1str) {
		this.eth1str = eth1str;
	}

	public String getEth2str() {
		return eth2str;
	}

	public void setEth2str(String eth2str) {
		this.eth2str = eth2str;
	}

	public String getEth3str() {
		return eth3str;
	}

	public void setEth3str(String eth3str) {
		this.eth3str = eth3str;
	}

	public String getEth4str() {
		return eth4str;
	}

	public void setEth4str(String eth4str) {
		this.eth4str = eth4str;
	}

	public String getUsbstr() {
		return usbstr;
	}

	public void setUsbstr(String usbstr) {
		this.usbstr = usbstr;
	}

	
	public void getIfwanPortTypeMap() {

		PortGroupProfile portGroup = getDataSource().getPortGroup();
		int wanPortNum = 0;
		if (null != portGroup) {
			// first add Eth0.It's not in list.
			wanPortNum += 1;
			List<Short> ethList = portGroup.getPortFinalValuesByPortType(
					DeviceInfType.Gigabit, PortAccessProfile.PORT_TYPE_WAN);

			if (getDataSource().isBRSupportMultiWan() && ethList != null && ethList.size() > 0) {
				// second add eth ports
				wanPortNum += ethList.size();
				if (ethList.contains(Short.valueOf("4"))) {
					flageth0 = true;
					eth0str = "WAN";
				}
				if (ethList.contains(Short.valueOf("5"))) {
					flageth1 = true;
					eth1str = "WAN";
				} else {
					flageth1 = false;
					eth1str = "LAN";
				}
				if (ethList.contains(Short.valueOf("6"))) {
					flageth2 = true;
					eth2str = "WAN";
				} else {
					flageth2 = false;
					eth2str = "LAN";
				}
				if (ethList.contains(Short.valueOf("7"))) {
					flageth3 = true;
					eth3str = "WAN";
				} else {
					flageth3 = false;
					eth3str = "LAN";
				}
				if (ethList.contains(Short.valueOf("8"))) {
					flageth4 = true;
					eth4str = "WAN";
				} else {
					flageth4 = false;
					eth4str = "LAN";
				}
				List usbList = portGroup.getPortFinalValuesByPortType(
						DeviceInfType.USB, PortAccessProfile.PORT_TYPE_WAN);
				if (usbList != null && usbList.size() > 0) {
					if (usbList.contains(Short.valueOf("3000"))) {
						flagusb0 = true;
					}
				}
				// TODO add usb
				wanPortNum += 1;

			}
		}

		if ((getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_WP || getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ)
				&& HiveAp.Device_TYPE_BRANCH_ROUTER != getDataSource()
						.getDeviceType() && wanPortNum < MAX_WAN_NUM_4AP) {
			wifi0str = ROUTER_PORT_WAN;
		} else {
			wifi0str = ROUTER_PORT_LAN;
		}
	}
	
	private boolean isBR200WPAsRouter(){
		if (NmsUtil.compareSoftwareVersion("6.0.0.0", this.getDataSource()
				.getSoftVer()) > 0
				|| this.getDataSource().getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200_WP
				|| HiveAp.Device_TYPE_BRANCH_ROUTER != getDataSource()
						.getDeviceType()) {
			return false;
		}
		return true;
	}
	
	private boolean isBR200LTE(){
		if (NmsUtil.compareSoftwareVersion("6.0.0.0", this.getDataSource()
				.getSoftVer()) > 0
				|| this.getDataSource().getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200_LTE_VZ
				|| HiveAp.Device_TYPE_BRANCH_ROUTER != getDataSource()
						.getDeviceType()) {
			return false;
		}
		return true;
	}
	
	private JSONObject radioConfigTypeChanged(Long tempTemplateId)
			throws JSONException {
		ConfigTemplate template = null;

		JSONObject jsonObj = new JSONObject();
		jsonObj.put("t", true);
		if (null != tempTemplateId && tempTemplateId > 0) {
			
				template = QueryUtil.findBoById(ConfigTemplate.class,
						tempTemplateId, this);
			
		}else{
			template = this.getDataSource().getConfigTemplate();
		}
		if (null != template) {
			PortGroupProfile pgp = this.getDataSource().getPortGroup(template);
			// getIfwanPortTypeMap(pgp);
			// setChangeWans(pgp);
			
			if (null != pgp) {
				List<Short> ethList = pgp.getPortFinalValuesByPortType(
						DeviceInfType.Gigabit, PortAccessProfile.PORT_TYPE_WAN);
				if (this.getDataSource().isBRSupportMultiWan() && ethList != null && ethList.size() > 0) {

					// wanPortNum . change portgroup thould get it through
					// configtemplate +1 is usb
					jsonObj.put("wanPortNum", ethList.size() + 2);

					//add wifi0
					//if(!isBR200WPAsRouter() || ){
						jsonObj.put("wifi0str", "LAN");
					//}
					
					if (ethList.contains(Short.valueOf("4"))) {
						jsonObj.put("eth0str", "WAN");
					}
					if (ethList.contains(Short.valueOf("5"))) {
						jsonObj.put("eth1str", "WAN");
					} else {
						jsonObj.put("eth1str", "LAN");
					}
					if (ethList.contains(Short.valueOf("6"))) {
						jsonObj.put("eth2str", "WAN");
					} else {
						jsonObj.put("eth2str", "LAN");
					}
					if (ethList.contains(Short.valueOf("7"))) {
						jsonObj.put("eth3str", "WAN");
					} else {
						jsonObj.put("eth3str", "LAN");
					}
					if (ethList.contains(Short.valueOf("8"))) {
						jsonObj.put("eth4str", "WAN");
					} else {
						jsonObj.put("eth4str", "LAN");
					}

				} else {
//					jsonObj.put("wanPortNum", this.getDataSource()
//							.getRouterWanInterfaceNum());
//					if(isBR200WPAsRouter()){
//						jsonObj.put("wifi0str", ROUTER_PORT_WAN);
//					}
					if((isBR200WPAsRouter() || isBR200LTE()) && getDataSource().getRouterWanInterfaceNum(template)< MAX_WAN_NUM_4AP && getDataSource().getRadioConfigType() == 6){
						jsonObj.put("wifi0str", ROUTER_PORT_WAN);
						jsonObj.put("wanPortNum",this.getDataSource().getRouterWanInterfaceNum(template)+1);
					}else{
						jsonObj.put("wifi0str", ROUTER_PORT_LAN);
						jsonObj.put("wanPortNum",this.getDataSource().getRouterWanInterfaceNum(template));
					}
				}
				// jsonObj.put("portTemplate", pgp.getName());

			} else {
				jsonObj.put("portTemplate", "");
				jsonObj.put("eth0str", "WAN");
				jsonObj.put("eth1str", "LAN");
				jsonObj.put("eth2str", "LAN");
				jsonObj.put("eth3str", "LAN");
				jsonObj.put("eth4str", "LAN");
/*				if(isBR200WPAsRouter()){
					jsonObj.put("wifi0str", ROUTER_PORT_WAN);
				}*/
				if((isBR200WPAsRouter() || isBR200LTE()) && getDataSource().getRouterWanInterfaceNum(template)< MAX_WAN_NUM_4AP && getDataSource().getRadioConfigType() == 6){
					jsonObj.put("wifi0str", ROUTER_PORT_WAN);
					jsonObj.put("wanPortNum",this.getDataSource().getRouterWanInterfaceNum(template)+1);
				}
                else{
					jsonObj.put("wifi0str", ROUTER_PORT_LAN);
					jsonObj.put("wanPortNum",this.getDataSource().getRouterWanInterfaceNum(template));
				}
				// wanPortNum
//				jsonObj.put("wanPortNum", this.getDataSource()
//						.getWanInterfaceNum(prepareMyDInfList()));
			}
		} else {
//			jsonObj.put("wanPortNum",
//					getDataSource().getWanInterfaceNum(prepareMyDInfList()));
			if((isBR200WPAsRouter() || isBR200LTE()) && getDataSource().getRouterWanInterfaceNum(template)< MAX_WAN_NUM_4AP && getDataSource().getRadioConfigType() == 6){
				jsonObj.put("wifi0str", ROUTER_PORT_WAN);
				jsonObj.put("wanPortNum",this.getDataSource().getRouterWanInterfaceNum(template)+1);
			}else{
				jsonObj.put("wifi0str", ROUTER_PORT_LAN);
				jsonObj.put("wanPortNum",this.getDataSource().getRouterWanInterfaceNum(template));
			}
		}

		return jsonObj;
		
		
	}
	 
	
	
	public boolean isFlageth0() {
		return flageth0;
	}

	public void setFlageth0(boolean flageth0) {
		this.flageth0 = flageth0;
	}

	public boolean isFlageth1() {
		return flageth1;
	}
	public String getFlageth1Str(){
		if(flageth1){
			return "";
		}
		return "none";
	}

	public void setFlageth1(boolean flageth1) {
		this.flageth1 = flageth1;
	}

	public boolean isFlageth2() {
		return flageth2;
	}
	public String getFlageth2Str(){
		if(flageth2){
			return "";
		}
		return "none";
	}
	public void setFlageth2(boolean flageth2) {
		this.flageth2 = flageth2;
	}

	public boolean isFlageth3() {
		return flageth3;
	}
	public String getFlageth3Str(){
		if(flageth3){
			return "";
		}
		return "none";
	}
	public void setFlageth3(boolean flageth3) {
		this.flageth3 = flageth3;
	}

	public boolean isFlageth4() {
		return flageth4;
	}
	public String getFlageth4Str(){
		if(flageth4){
			return "";
		}
		return "none";
	}
	public void setFlageth4(boolean flageth4) {
		this.flageth4 = flageth4;
	}

	public boolean isFlagusb0() {
		return flagusb0;
	}
	
	public String getFlageUsbStr(){
		if(flagusb0){
			return "";
		}
		return "none";
	}
	public void setFlagusb0(boolean flagusb0) {
		this.flagusb0 = flagusb0;
	}

	

	private Map<Short,Short> getIfPortTypeMap(PortGroupProfile portGroup){
		Map<Short,Short> interface2PortTypeMap = new HashMap<>();
		if(portGroup != null){
			List<PortBasicProfile> basicProfiles = portGroup.getBasicProfiles();
			if(basicProfiles != null){
				for(PortBasicProfile basicProfile : basicProfiles){
					String[] eths = basicProfile.getETHs();
					String[] sfps = basicProfile.getSFPs();
					String[] usbs = basicProfile.getUSBs();
					PortAccessProfile accessProfile = basicProfile.getAccessProfile();
					if(accessProfile != null){
						short portType = accessProfile.getPortType();
						if(eths != null){
							for(String eth : eths){
								short sEth = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(eth), getDataSource().getHiveApModel());
								interface2PortTypeMap.put(sEth, portType);
							}
						}
						if(sfps != null){
							for(String sfp : sfps){
								short sSfp = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfp), getDataSource().getHiveApModel());
								interface2PortTypeMap.put(sSfp, portType);
							}
						}
						if(usbs != null){
							for(String usb : usbs){
								short sUsb = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(usb), getDataSource().getHiveApModel());
								interface2PortTypeMap.put(sUsb, portType);
							}
						}
					}
				}
			}
		}

		return interface2PortTypeMap;
	}

	private boolean isPortAccessModel(short portType) {
	    return portType == PortAccessProfile.PORT_TYPE_MONITOR
	        || portType == PortAccessProfile.PORT_TYPE_ACCESS;
	}

	private boolean isPortTrunkModel(short portType) {
	    return portType == PortAccessProfile.PORT_TYPE_PHONEDATA
	        || portType == PortAccessProfile.PORT_TYPE_AP
	        || portType == PortAccessProfile.PORT_TYPE_8021Q;
	}

	private void setStormControlFromNP(Map<Short,Short> interface2PortTypeMap){

		//set storm control mode
		short switchStormControlMode = getDataSource().getConfigTemplate().getSwitchStormControlMode();
		getDataSource().setSwitchStormControlMode(switchStormControlMode);

		//set storm control list
		for(ConfigTemplateStormControl stormControl : getDataSource().getStormControlList()){
			short interfaceNum = stormControl.getInterfaceNum();
			if(interface2PortTypeMap.keySet().contains(interfaceNum)){
				short portType = interface2PortTypeMap.get(interfaceNum);
				ConfigTemplateStormControl stormControlFromNP = getUsedStormControl4PortFromNP(portType,switchStormControlMode);

				stormControl.setInterfaceNum(stormControl.getInterfaceNum());
				if(stormControlFromNP != null){
					stormControl.setAllTrafficType(stormControlFromNP.isAllTrafficType());
					stormControl.setBroadcast(stormControlFromNP.isBroadcast());
					stormControl.setTcpsyn(stormControlFromNP.isTcpsyn());
					stormControl.setUnknownUnicast(stormControlFromNP.isUnknownUnicast());
					stormControl.setMulticast(stormControlFromNP.isMulticast());
					stormControl.setRateLimitType(stormControlFromNP.getRateLimitType());
					stormControl.setRateLimitValue(stormControlFromNP.getRateLimitValue());
				}
				prepareSingleStormControl(false,switchStormControlMode,stormControl);
			} else {
				//if network don't set port config, load default value for storm control
				if(switchStormControlMode == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_BYTE){
					// (BYTE + BPS)
					stormControl.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID);
				} else {
					// (PACKET + PPS)
					stormControl.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_ID);
				}

				prepareSingleStormControl(true,switchStormControlMode,stormControl);
			}
		}
	}

	private ConfigTemplateStormControl getUsedStormControl4PortFromNP(short portType,short switchStormControlMode){
		ConfigTemplateStormControl stormControl4Access = getStormControlFromNP(MgrUtil.getResourceString("config.configTemplate.access"));
		ConfigTemplateStormControl stormControl4Trunk = getStormControlFromNP(MgrUtil.getResourceString("config.configTemplate.8021Q"));

		ConfigTemplateStormControl stormControlFromNP = new ConfigTemplateStormControl();
		if(isPortAccessModel(portType)){
			stormControlFromNP = stormControl4Access;
		} else if(isPortTrunkModel(portType)){
			stormControlFromNP = stormControl4Trunk;
		} else {
			stormControlFromNP.setAllTrafficType(false);
			stormControlFromNP.setBroadcast(false);
			stormControlFromNP.setTcpsyn(false);
			stormControlFromNP.setUnknownUnicast(false);
			stormControlFromNP.setMulticast(false);
			if(switchStormControlMode == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_BYTE){
				stormControlFromNP.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID);
				stormControlFromNP.setRateLimitValue(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_DEFULT_VALUE);
			} else {
				stormControlFromNP.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_ID);
				stormControlFromNP.setRateLimitValue(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_DEFULT_VALUE);
			}

		}

		return stormControlFromNP;
	}

	private void prepareSingleStormControl(boolean resetDefaultValue,short switchStormControlMode,ConfigTemplateStormControl stormControl){
		if(switchStormControlMode == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_BYTE){
			if(stormControl.getRateLimitType() == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID){
				if(stormControl.isSFP(getDataSource().getHiveApModel())){
					stormControl.setRateLimitRange(MgrUtil.getResourceString("config.configTemplate.switchSettings.stormControl.ratelimittype.range.bps.sfp"));
					stormControl.setRateLimitValueLength(8);
				} else {
					stormControl.setRateLimitRange(MgrUtil.getResourceString("config.configTemplate.switchSettings.stormControl.ratelimittype.range.bps"));
					stormControl.setRateLimitValueLength(7);
				}
				
				if(resetDefaultValue){
					setDefaultValue4StormControl(stormControl,ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID,
							ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_DEFULT_VALUE);
				}

			} else {
				stormControl.setRateLimitRange(MgrUtil.getResourceString("config.configTemplate.switchSettings.stormControl.ratelimittype.range.percentage"));
				stormControl.setRateLimitValueLength(3);
				if(resetDefaultValue){
					setDefaultValue4StormControl(stormControl,ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE_ID,
							ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE_DEFULT_VALUE);
				}
			}
		} else {
			if(stormControl.isSFP(getDataSource().getHiveApModel())){
				stormControl.setRateLimitRange(MgrUtil.getResourceString("config.configTemplate.switchSettings.stormControl.ratelimittype.range.pps.sfp"));
				stormControl.setRateLimitValueLength(10);
			} else {
				stormControl.setRateLimitRange(MgrUtil.getResourceString("config.configTemplate.switchSettings.stormControl.ratelimittype.range.pps"));
				stormControl.setRateLimitValueLength(9);
			}
			if(resetDefaultValue){
				setDefaultValue4StormControl(stormControl,ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_ID,
						ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_DEFULT_VALUE);
			}
		}
	}

	private void setDefaultValue4StormControl(ConfigTemplateStormControl stormControl, long rateLimitType, long rateLimitValue){
		stormControl.setRateLimitType(rateLimitType);
		stormControl.setRateLimitValue(rateLimitValue);
		stormControl.setAllTrafficType(false);
		stormControl.setBroadcast(false);
		stormControl.setTcpsyn(false);
		stormControl.setUnknownUnicast(false);
		stormControl.setMulticast(false);
	}

	private void updateStormContorl(HiveAp hiveAp){
		if (hiveAp == null || hiveAp.getStormControlList() == null) {
			return;
		}
		if(!hiveAp.getDeviceInfo().isSptEthernetMore_24()){
			hiveAp.setStormControlList(null);
		} else {
			short stormControlMode = hiveAp.getSwitchStormControlMode();
			Set<Short> interfaceNumSet = getEanbleStormIfNum();
			for(ConfigTemplateStormControl stormControl : hiveAp.getStormControlList()){
				boolean blnAllTrafficType = false;
				boolean blnBroadcast = false;
				boolean blnUnknownUnicast = false;
				boolean blnMulticast = false;
				boolean blnTcpsyn = false;

				if(!interfaceNumSet.contains(stormControl.getInterfaceNum())){
					if(stormControlMode == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_BYTE){
						if(stormControl.getRateLimitType() == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID){
							stormControl.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID);
							stormControl.setRateLimitValue(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_DEFULT_VALUE);
						} else {
							stormControl.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_ID);
							stormControl.setRateLimitValue(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_DEFULT_VALUE);
						}

					} else {
						stormControl.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_ID);
						stormControl.setRateLimitValue(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_DEFULT_VALUE);
					}
				}

				if (arrayInterfaceNum!=null){
					for(int i =0;i<arrayInterfaceNum.length;i++){
						if (stormControl.getInterfaceNum()==arrayInterfaceNum[i]){
							if(stormControlMode == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_BYTE){
								if(arrayRateLimitType != null && i < arrayRateLimitType.length){
									stormControl.setRateLimitType(arrayRateLimitType[i]);
								}
							} else {
								stormControl.setRateLimitType(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_ID);
							}
							if(arrayRateLimitValue != null && i < arrayRateLimitValue.length){
								stormControl.setRateLimitValue(arrayRateLimitValue[i]);
							}
							break;
						}
					}
				}

				if (arrayAllTrafficType!=null){
					for(short sAllTrafficType:arrayAllTrafficType){
						if (stormControl.getInterfaceNum()==sAllTrafficType){
							blnAllTrafficType = true;
							break;
						}
					}
				}
				if (blnAllTrafficType) {
					stormControl.setAllTrafficType(blnAllTrafficType);
					stormControl.setBroadcast(blnAllTrafficType);
					stormControl.setUnknownUnicast(blnAllTrafficType);
					stormControl.setMulticast(blnAllTrafficType);
					stormControl.setTcpsyn(blnAllTrafficType);
					continue;
				}
				if (arrayBroadcast!=null){
					for(short sBroadcast:arrayBroadcast){
						if (stormControl.getInterfaceNum() == sBroadcast){
							blnBroadcast = true;
							break;
						}
					}
				}
				if (arrayUnknownUnicast!=null){
					for(short sUnknownUnicast:arrayUnknownUnicast){
						if (stormControl.getInterfaceNum() == sUnknownUnicast){
							blnUnknownUnicast = true;
							break;
						}
					}
				}
				if (arrayMulticast!=null){
					for(short sMulticast:arrayMulticast){
						if (stormControl.getInterfaceNum() == sMulticast){
							blnMulticast = true;
							break;
						}
					}
				}

				if (arrayTcpsyn!=null){
					for(short sTcpsyn:arrayTcpsyn){
						if (stormControl.getInterfaceNum() == sTcpsyn){
							blnTcpsyn = true;
							break;
						}
					}
				}

				stormControl.setAllTrafficType(blnAllTrafficType);
				stormControl.setBroadcast(blnBroadcast);
				stormControl.setUnknownUnicast(blnUnknownUnicast);
				stormControl.setMulticast(blnMulticast);
				stormControl.setTcpsyn(blnTcpsyn);
			}
		}

	}

	private boolean isPortStromEnable(ConfigTemplateStormControl stormControl){
		if(stormControl == null){
			return false;
		}

		if(stormControl.isBroadcast()
				|| stormControl.isMulticast()
				|| stormControl.isTcpsyn()
				|| stormControl.isUnknownUnicast()){
			return true;
		}

		return false;
	}

	private Set<Short> getEanbleStormIfNum(){
		Set<Short> result = new HashSet<>();
		if(arrayBroadcast != null){
			result.addAll(Arrays.asList(arrayBroadcast));
		}
		if(arrayUnknownUnicast != null){
			result.addAll(Arrays.asList(arrayUnknownUnicast));
		}
		if(arrayMulticast != null){
			result.addAll(Arrays.asList(arrayMulticast));
		}
		if(arrayTcpsyn != null){
			result.addAll(Arrays.asList(arrayTcpsyn));
		}
		return result;
	}
	
	private boolean checkStormControl() throws Exception{
		String softVer = getDataSource().getSoftVer();
		boolean before6_0r3 = softVer != null
				&& (NmsUtil.compareSoftwareVersion("6.1.1.0", softVer) > 0);
		if(before6_0r3){
			if(!validateStormControls()){
				jsonObject = new JSONObject();
				jsonObject.put("resultStatus", false);
				jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
				return false;
			}
			
		}
		return true;
	}
	
	private boolean  validateStormControls() throws JSONException {
		if(getDataSource() == null || getDataSource().getStormControlList() == null){
			return true;
		}
		List<ConfigTemplateStormControl> stormControlList = getDataSource().getStormControlList();

		for(ConfigTemplateStormControl sc : stormControlList){
			if(sc.isSFP(getDataSource().getHiveApModel())){
				if(getDataSource().getSwitchStormControlMode() == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_BYTE){
					if(sc.getRateLimitValue() > 1000000){//kbps
						addActionError(MgrUtil.getUserMessage("error.hiveap.storm.control.version.limitvalue.validate", new String[]{"KBPS","1000000"}));
						return false;
					}
				} else {
					if(sc.getRateLimitValue() > 100000000){//pps
						addActionError(MgrUtil.getUserMessage("error.hiveap.storm.control.version.limitvalue.validate", new String[]{"PPS","100000000"}));
						return false;
					}
				}
			}
		}
		
		return true;
	}

	public String getShowStormControlDiv() {
		return "";
	}

	public String getHideStormControlDiv() {
		return "none";
	}

	public List<CheckItem> getList_stormLimitType() {
		List<CheckItem> items = new ArrayList<CheckItem>();
		CheckItem item = new CheckItem(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID,
				ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS);
		items.add(item);
//		item = new CheckItem(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_ID,
//				ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS);
//		items.add(item);
		item = new CheckItem(ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE_ID,
				ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE);
		items.add(item);
		return items;
	}

    public EnumItem[] getStormRateLimitByte() {
			return new EnumItem[] { new EnumItem(0,
					getText("config.configTemplate.switchSettings.stormControl.byteBased")) };
	}

    public EnumItem[] getStormRateLimitPacket() {
			return new EnumItem[] { new EnumItem(1,
					getText("config.configTemplate.switchSettings.stormControl.packetBased")) };
	}

	private Short[] arrayAllTrafficType;
	private Short[] arrayBroadcast;
	private Short[] arrayUnknownUnicast;
	private Short[] arrayMulticast;
	private Short[] arrayTcpsyn;
	private Long[] arrayRateLimitType;
	private Long[] arrayRateLimitValue;
	private Short[] arrayInterfaceNum;

	public Short[] getArrayAllTrafficType() {
		return arrayAllTrafficType;
	}

	public Short[] getArrayBroadcast() {
		return arrayBroadcast;
	}

	public Short[] getArrayUnknownUnicast() {
		return arrayUnknownUnicast;
	}

	public Short[] getArrayMulticast() {
		return arrayMulticast;
	}

	public Short[] getArrayTcpsyn() {
		return arrayTcpsyn;
	}

	public void setArrayAllTrafficType(Short[] arrayAllTrafficType) {
		this.arrayAllTrafficType = arrayAllTrafficType;
	}

	public void setArrayBroadcast(Short[] arrayBroadcast) {
		this.arrayBroadcast = arrayBroadcast;
	}

	public void setArrayUnknownUnicast(Short[] arrayUnknownUnicast) {
		this.arrayUnknownUnicast = arrayUnknownUnicast;
	}

	public void setArrayMulticast(Short[] arrayMulticast) {
		this.arrayMulticast = arrayMulticast;
	}

	public void setArrayTcpsyn(Short[] arrayTcpsyn) {
		this.arrayTcpsyn = arrayTcpsyn;
	}

	public Long[] getArrayRateLimitValue() {
		return arrayRateLimitValue;
	}

	public Short[] getArrayInterfaceNum() {
		return arrayInterfaceNum;
	}

	public void setArrayRateLimitValue(Long[] arrayRateLimitValue) {
		this.arrayRateLimitValue = arrayRateLimitValue;
	}

	public void setArrayInterfaceNum(Short[] arrayInterfaceNum) {
		this.arrayInterfaceNum = arrayInterfaceNum;
	}

	public Long[] getArrayRateLimitType() {
		return arrayRateLimitType;
	}

	public void setArrayRateLimitType(Long[] arrayRateLimitType) {
		this.arrayRateLimitType = arrayRateLimitType;
	}

	// add for Switch PSE start
	public EnumItem[] getEnumManagementType() {
		return HiveAp.ENUM_MANAGERMENT_TYPE;
	}
	
	public boolean isPoeLegacyDisplay(){
		if (NmsUtil.compareSoftwareVersion("6.1.2.0", this.getDataSource()
				.getSoftVer()) > 0) {
			return false;
		}
		return true;
	}
	
	public boolean isPoeLldpDisplay(){
//		if (NmsUtil.compareSoftwareVersion("6.1.3.0", this.getDataSource()
//				.getSoftVer()) > 0) {
//			return false;
//		}
//		return true;
		return false;
	}

	// add for remove Network Policy from CVG  start **************************

	private List<CheckItem> cvgMgtNetworkList;

	private List<CheckItem> dnsForCVGList;

	private List<CheckItem> ntpForCVGList;

	private List<CheckItem> cvgMgtVlanList;
	
	private List<CheckItem> syslogForCVGList;
	
	private List<CheckItem> snmpForCVGList;

	private Long cvgMgtNetwork;

	private Long dnsForCVGId;

	private Long ntpForCVGId;

	private Long cvgMgtVlan;
	
	private Long syslogForCVGId;
	
	private Long snmpForCVGId;

	protected void setSelectedStormControl(){
		updateStormContorl(getDataSource());
	}

	public List<CheckItem> getCvgMgtNetworkList() {
		return this.cvgMgtNetworkList;
	}

	public List<CheckItem> getDnsForCVGList() {
		return this.dnsForCVGList;
	}

	public List<CheckItem> getNtpForCVGList() {
		return this.ntpForCVGList;
	}

	public List<CheckItem> getCvgMgtVlanList(){
		return this.cvgMgtVlanList;
	}

	public List<CheckItem> getSyslogForCVGList() {
		return syslogForCVGList;
	}

	public List<CheckItem> getSnmpForCVGList() {
		return snmpForCVGList;
	}

	protected void prepareDependentSelectObjects() {
        this.cvgMgtNetworkList = getBoCheckItems("networkName", VpnNetwork.class,
                new FilterParams("networkType", VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT));
		if (cvgMgtNetworkList.isEmpty()) {
			cvgMgtNetworkList.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}

		this.dnsForCVGList = getBoCheckItems("mgmtName",
				MgmtServiceDns.class, null, CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);

		this.ntpForCVGList = getBoCheckItems("mgmtName",
				MgmtServiceTime.class, null, CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		this.cvgMgtVlanList =  getBoCheckItems("vlanName",
				Vlan.class, null, CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		this.syslogForCVGList = getBoCheckItems("mgmtName",
				MgmtServiceSyslog.class, null, CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
		this.snmpForCVGList = getBoCheckItems("mgmtName",
				MgmtServiceSnmp.class, null, CHECK_ITEM_BEGIN_BLANK, CHECK_ITEM_END_NO);
	}

	public Long getCvgMgtNetwork() {
		return cvgMgtNetwork;
	}

	public void setCvgMgtNetwork(Long cvgMgtNetwork) {
		this.cvgMgtNetwork = cvgMgtNetwork;
	}

	public Long getCvgMgtVlan() {
		return cvgMgtVlan;
	}

	public void setCvgMgtVlan(Long cvgMgtVlan) {
		this.cvgMgtVlan = cvgMgtVlan;
	}

	public Long getDnsForCVGId() {
		return dnsForCVGId;
	}

	public void setDnsForCVGId(Long dnsForCVGId) {
		this.dnsForCVGId = dnsForCVGId;
	}

	public Long getNtpForCVGId() {
		return ntpForCVGId;
	}

	public void setNtpForCVGId(Long ntpForCVGId) {
		this.ntpForCVGId = ntpForCVGId;
	}

	public Long getSyslogForCVGId() {
		return syslogForCVGId;
	}

	public void setSyslogForCVGId(Long syslogForCVGId) {
		this.syslogForCVGId = syslogForCVGId;
	}

	public Long getSnmpForCVGId() {
		return snmpForCVGId;
	}

	public void setSnmpForCVGId(Long snmpForCVGId) {
		this.snmpForCVGId = snmpForCVGId;
	}

	protected void setSelectedCvgDepend() throws Exception {
		if(HiveAp.isCVGAppliance(getDataSource().getHiveApModel()) &&
				getDataSource().getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY){
			if (null != cvgMgtNetwork) {
				VpnNetwork vpnNetwork = QueryUtil.findBoById(
						VpnNetwork.class, cvgMgtNetwork, this);
				if (vpnNetwork == null && cvgMgtNetwork > 0) {
					String tempStr[] = { getText("hiveAp.cvg.mgt.network") };
					addActionError(getText("info.ssid.warning", tempStr));
				}
				if (null != getDataSource()) {
					getDataSource().getOrCreateCvgDPD().setMgtNetwork(vpnNetwork);
				}
			}

			if (null != cvgMgtVlan) {
				Vlan mgtVlan = QueryUtil.findBoById(
						Vlan.class, cvgMgtVlan, this);
				if (mgtVlan == null && cvgMgtVlan > 0) {
					String tempStr[] = { getText("hiveAp.cvg.mgt.vlan") };
					addActionError(getText("info.ssid.warning", tempStr));
				}
				if (null != getDataSource()) {
					getDataSource().getOrCreateCvgDPD().setMgtVlan(mgtVlan);
				}
			}

			if (null != dnsForCVGId) {
				MgmtServiceDns dnsForCVG = QueryUtil.findBoById(
						MgmtServiceDns.class, dnsForCVGId, this);
				if (dnsForCVG == null && dnsForCVGId > 0) {
					String tempStr[] = { getText("config.configTemplate.mgtDns") };
					addActionError(getText("info.ssid.warning", tempStr));
				}
				if (null != getDataSource()) {
					getDataSource().getOrCreateCvgDPD().setDnsForCVG(dnsForCVG);
				}
			}

			if (null != ntpForCVGId) {
				MgmtServiceTime ntpForCVG = QueryUtil.findBoById(
						MgmtServiceTime.class, ntpForCVGId, this);
				if (ntpForCVG == null && ntpForCVGId > 0) {
					String tempStr[] = { getText("config.configTemplate.mgtTime") };
					addActionError(getText("info.ssid.warning", tempStr));
				}
				if (null != getDataSource()) {
					getDataSource().getOrCreateCvgDPD().setNtpForCVG(ntpForCVG);
				}
			}
			
			if (null != syslogForCVGId) {
				MgmtServiceSyslog syslogForCVG = QueryUtil.findBoById(
						MgmtServiceSyslog.class, syslogForCVGId, this);
				if (syslogForCVG == null && syslogForCVGId > 0) {
					String tempStr[] = { getText("config.configTemplate.mgtSyslog") };
					addActionError(getText("info.ssid.warning", tempStr));
				}
				if (null != getDataSource()) {
					getDataSource().getOrCreateCvgDPD().setMgmtServiceSyslog(syslogForCVG);
				}
			}
			
			if (null != snmpForCVGId) {
				MgmtServiceSnmp snmpForCVG = QueryUtil.findBoById(
						MgmtServiceSnmp.class, snmpForCVGId, this);
				if (snmpForCVG == null && snmpForCVGId > 0) {
					String tempStr[] = { getText("config.configTemplate.mgtSnmp") };
					addActionError(getText("info.ssid.warning", tempStr));
				}
				if (null != getDataSource()) {
					getDataSource().getOrCreateCvgDPD().setMgmtServiceSnmp(snmpForCVG);
				}
			}
		}else{
			getDataSource().getOrCreateCvgDPD().setMgtNetwork(null);
			getDataSource().getOrCreateCvgDPD().setDnsForCVG(null);
			getDataSource().getOrCreateCvgDPD().setNtpForCVG(null);
			getDataSource().getOrCreateCvgDPD().setMgmtServiceSyslog(null);
			getDataSource().getOrCreateCvgDPD().setMgmtServiceSnmp(null);
		}

	}

	public String getConfigTemplateStyle(){
		return this.getFullModeConfigStyle();
	}

	public String getCvgDpdStyle(){
		return "";
	}

	public boolean isNewEditRadiusForBrFlg() {
		return newEditRadiusForBrFlg;
	}

	public void setNewEditRadiusForBrFlg(boolean newEditRadiusForBrFlg) {
		this.newEditRadiusForBrFlg = newEditRadiusForBrFlg;
	}

	public boolean getDeviceTypeDisabled(){
		if(NmsUtil.compareSoftwareVersion("5.0.0.0", this.getDataSource().getSoftVer()) > 0){
			return true;
		}else if (!isFullMode()) {
			return true;
		}else{
			return false;
		}
	}

	// end for remove Network Policy from CVG  end ****************************

	private Map<Long, ConfigTemplate> getConfigTemplateMapOfPage(Map<Long, List<SsidProfile>> ssidProfiles) {
		if (page == null) {
			return null;
		}
		if (ssidProfiles == null) {
			ssidProfiles = new HashMap<Long, List<SsidProfile>>();
		} else {
			ssidProfiles.clear();
		}
		List<Long> deviceIds = new ArrayList<Long>(page.size());
		for (Object obj : page) {
			deviceIds.add(((HiveAp)obj).getId());
		}
		if (deviceIds.isEmpty()) {
			return null;
		}
		Map<Long, List<Long>> mapOfConfigAndDevice = new HashMap<Long, List<Long>>();
		List<?> list =
			QueryUtil.executeQuery("select id, configTemplate.id from " + HiveAp.class.getSimpleName(),
					null, new FilterParams("id", deviceIds));
		for (Object obj : list) {
			Object[] tmp = (Object[])obj;
			if(tmp[0] == null || tmp[1] == null){
				continue;
			}
			Long idTmp = (Long)tmp[0];
			Long configIdTmp = (Long)tmp[1];
			
			if (mapOfConfigAndDevice.get(configIdTmp) != null) {
				mapOfConfigAndDevice.get(configIdTmp).add(idTmp);
			} else {
				List<Long> listTmp = new ArrayList<Long>();
				listTmp.add(idTmp);
				mapOfConfigAndDevice.put(configIdTmp, listTmp);
			}
		}

		List<Long> configIds = new ArrayList<Long>(mapOfConfigAndDevice.keySet());
		if (configIds.isEmpty()) {
			return null;
		}
		Map<Long, ConfigTemplate> resultMap = new HashMap<Long, ConfigTemplate>(page.size());

		List<ConfigTemplate> configTemplates =
			QueryUtil.executeQuery(ConfigTemplate.class, null, new FilterParams("id", configIds), null, this);
		if (!configTemplates.isEmpty()) {
			Map<Long, List<Long>> ssidConfigIdsTmp = new HashMap<Long, List<Long>>();
			for (ConfigTemplate configTemplate : configTemplates) {
				List<Long> deviceIdsTmp = mapOfConfigAndDevice.get(configTemplate.getId());
				if (deviceIdsTmp != null && !deviceIdsTmp.isEmpty()) {
					for (Long idTmp : deviceIdsTmp) {
						resultMap.put(idTmp, configTemplate);
					}
				}

				for(ConfigTemplateSsid ssidTemp : configTemplate.getSsidInterfaces().values()){
					if(ssidTemp != null && ssidTemp.getSsidProfile() != null && ssidTemp.getSsidProfile().getId() != null){
						Long idTmp = ssidTemp.getSsidProfile().getId();
						if (ssidConfigIdsTmp.get(idTmp) != null) {
							ssidConfigIdsTmp.get(idTmp).add(configTemplate.getId());
						} else {
							List<Long> lstTmp = new ArrayList<Long>();
							lstTmp.add(configTemplate.getId());
							ssidConfigIdsTmp.put(idTmp, lstTmp);
						}
					}
				}
			}

			List<Long> ssidIdsTmp = new ArrayList<Long>(ssidConfigIdsTmp.keySet());
			if (!ssidIdsTmp.isEmpty()) {
				List<SsidProfile> ssidProfileLst =
					QueryUtil.executeQuery(SsidProfile.class, null, new FilterParams("id", ssidIdsTmp), null, this);
				for (SsidProfile ssidProfileTmp : ssidProfileLst) {
					List<Long> idsTmp = ssidConfigIdsTmp.get(ssidProfileTmp.getId());
					if (idsTmp != null && !idsTmp.isEmpty()) {
						for (Long idTmp : idsTmp) {
							if (ssidProfiles.get(idTmp) != null) {
								ssidProfiles.get(idTmp).add(ssidProfileTmp);
							} else {
								List<SsidProfile> ssidLst = new ArrayList<SsidProfile>();
								ssidLst.add(ssidProfileTmp);
								ssidProfiles.put(idTmp, ssidLst);
							}
						}
					}
				}
			}
		}

		return resultMap;
	}

	public Long getPppoeAuthProfile() {
		return pppoeAuthProfile;
	}

	public void setPppoeAuthProfile(Long pppoeAuthProfile) {
		this.pppoeAuthProfile = pppoeAuthProfile;
	}

	public List<CheckItem> getPppoeAuthProfiles() {
		return pppoeAuthProfiles;
	}

	public String getRadioPsePriority() {
		return radioPsePriority;
	}

	public void setRadioPsePriority(String radioPsePriority) {
		this.radioPsePriority = radioPsePriority;
	}

	public String getImportButtonStyle(){
		if("managedVPNGateways".equals(hmListType)){
			return "none";
		}else{
			return "";
		}
	}

	public static void synchronizeCVGInterfaceState(HiveAp hiveAp){
		if(hiveAp.getDeviceType() != HiveAp.Device_TYPE_VPN_GATEWAY){
			return;
		}
		FilterParams filter = new FilterParams("apMac = :s1 and ifName = :s2", new Object[]{hiveAp.getMacAddress(), "eth0"});
		List<AhXIf> results = QueryUtil.executeQuery(AhXIf.class,
				new SortParams("xifpk.statTimeStamp", false), filter, hiveAp.getOwner().getId(), 1);
		if(results.isEmpty() || results.get(0) == null){
			return;
		}
		long statTimeStamp = results.get(0).getXifpk().getStatTimeValue();
		filter = new FilterParams("apMac = :s1 and ifName = :s2 and xifpk.statTimeStamp = :s3", new Object[]{hiveAp.getMacAddress(), "eth1", statTimeStamp});
		results = QueryUtil.executeQuery(AhXIf.class,
				null, filter, hiveAp.getOwner().getId(), 1);

		DeviceInterface eth1Interface = hiveAp.getEth1Interface();
		if(results.isEmpty()){
			eth1Interface.setIfActive(false);
		}else{
			eth1Interface.setIfActive(true);
		}
		hiveAp.setEth1Interface(eth1Interface);
	}

	public String getCVGLanPortStyle(){
		if(this.getDataSource().getEth1Interface().isIfActive()){
			return "";
		}else{
			return "None";
		}
	}

	public short getMgt0NetworkType() {
		return mgt0NetworkType;
	}

	public void setMgt0NetworkType(short mgt0NetworkType) {
		this.mgt0NetworkType = mgt0NetworkType;
	}

	private JSONObject deviceModeChange(HiveAp hiveAp,Long tempTemplateId) throws JSONException{
		ConfigTemplate template = null;
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("t", true);
		jsonObj.put("deviceTypeList", getDeviceTypeList(hiveAp));
		jsonObj.put("vpnServiceRoleList", getVpnServiceRoleList(hiveAp));
		jsonObj.put("devicePageStyleList", getDevicePageStyleList(hiveAp));
		jsonObj.put("templateList", getConfigTemplateList(hiveAp));
		if (null != tempTemplateId ) {
			if (tempTemplateId > 0) {
				 template = QueryUtil.findBoById(
						ConfigTemplate.class, tempTemplateId, this);
			}
		}
		if (null != template ) {
		PortGroupProfile pgp=	this.getDataSource().getPortGroup(template);
		//getIfwanPortTypeMap(pgp);
		//setChangeWans(pgp);
			if(null!=pgp){
				List<Short> ethList=pgp.getPortFinalValuesByPortType(DeviceInfType.Gigabit, PortAccessProfile.PORT_TYPE_WAN);
				if(this.getDataSource().isBRSupportMultiWan() && ethList!=null && ethList.size()>0){
					
					//wanPortNum . change portgroup thould get it through configtemplate +1 is usb
					jsonObj.put("wanPortNum",ethList.size()+2);
					if(ethList.contains(Short.valueOf("4"))){
						jsonObj.put("eth0str", "WAN");
						}
						if(ethList.contains(Short.valueOf("5"))){
							jsonObj.put("eth1str", "WAN");
						}else {
							jsonObj.put("eth1str", "LAN");
						}
						if(ethList.contains(Short.valueOf("6"))){
							jsonObj.put("eth2str", "WAN");
						}else {
							jsonObj.put("eth2str", "LAN");
						}
						if(ethList.contains(Short.valueOf("7"))){
							jsonObj.put("eth3str", "WAN");	
						}else {
							jsonObj.put("eth3str", "LAN");	
						}
						if(ethList.contains(Short.valueOf("8"))){
							jsonObj.put("eth4str", "WAN");
						}else{
							jsonObj.put("eth4str", "LAN");
						}
						
				}else{
					jsonObj.put("wanPortNum",this.getDataSource().getRouterWanInterfaceNum());
				}
//				jsonObj.put("portTemplate", pgp.getName());
				
			}else{
				jsonObj.put("portTemplate", "");
				jsonObj.put("eth0str", "WAN");
				jsonObj.put("eth1str", "LAN");
				jsonObj.put("eth2str", "LAN");
				jsonObj.put("eth3str", "LAN");
				jsonObj.put("eth4str", "LAN");
				//wanPortNum 
				if(isBR200WPAsRouter() && getDataSource().getRouterWanInterfaceNum(template)< MAX_WAN_NUM_4AP){
					jsonObj.put("wifi0str", ROUTER_PORT_WAN);
					jsonObj.put("wanPortNum",this.getDataSource().getRouterWanInterfaceNum(template)+1);
				}else{
					jsonObj.put("wifi0str", ROUTER_PORT_LAN);
					jsonObj.put("wanPortNum",this.getDataSource().getRouterWanInterfaceNum(template));
				}
			}
		}else {
//			jsonObj.put("wanPortNum",hiveAp.getWanInterfaceNum(prepareMyDInfList()));
			if(isBR200WPAsRouter() && getDataSource().getRouterWanInterfaceNum(template)< MAX_WAN_NUM_4AP && getDataSource().getRadioConfigType() == 6){
				jsonObj.put("wifi0str", ROUTER_PORT_WAN);
				jsonObj.put("wanPortNum",this.getDataSource().getRouterWanInterfaceNum(template)+1);
			}else{
				jsonObj.put("wifi0str", ROUTER_PORT_LAN);
				jsonObj.put("wanPortNum",this.getDataSource().getRouterWanInterfaceNum(template));
			}
		}
		
		return jsonObj;
	}


	private Collection<JSONObject> getConfigTemplateList(HiveAp hiveAp)
			throws JSONException {
		Collection<JSONObject> resList = new ArrayList<JSONObject>();
		List<CheckItem> templates = getConfigTemplatesByDeviceFunction(hiveAp);
		JSONObject jsonObj;
		for (CheckItem template : templates) {
			jsonObj = new JSONObject();
			jsonObj.put("id", String.valueOf(template.getId()));
			jsonObj.put("name", template.getValue());
			resList.add(jsonObj);
		}
		return resList;
	}

	private Collection<JSONObject> getVpnServiceRoleList(HiveAp hiveAp) throws JSONException{
		Collection<JSONObject> resList = new ArrayList<JSONObject>();
		JSONObject jsonObj;
		if(hiveAp.isCVGAppliance() && hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
			jsonObj = new JSONObject();
			jsonObj.put("vpnKey",HiveAp.VPN_MARK_NONE);
			jsonObj.put("vpnValue", MgrUtil.getEnumString("enum.vpn.mark."+HiveAp.VPN_MARK_NONE));
			resList.add(jsonObj);

			jsonObj = new JSONObject();
			jsonObj.put("vpnKey",HiveAp.VPN_MARK_SERVER);
			jsonObj.put("vpnValue", MgrUtil.getEnumString("enum.vpn.mark."+HiveAp.VPN_MARK_SERVER));
			resList.add(jsonObj);
		}else{
			jsonObj = new JSONObject();
			jsonObj.put("vpnKey",HiveAp.VPN_MARK_NONE);
			jsonObj.put("vpnValue", MgrUtil.getEnumString("enum.vpn.mark."+HiveAp.VPN_MARK_NONE));
			resList.add(jsonObj);

			jsonObj = new JSONObject();
			jsonObj.put("vpnKey",HiveAp.VPN_MARK_CLIENT);
			jsonObj.put("vpnValue", MgrUtil.getEnumString("enum.vpn.mark."+HiveAp.VPN_MARK_CLIENT));
			resList.add(jsonObj);

			jsonObj = new JSONObject();
			jsonObj.put("vpnKey",HiveAp.VPN_MARK_SERVER);
			jsonObj.put("vpnValue", MgrUtil.getEnumString("enum.vpn.mark."+HiveAp.VPN_MARK_SERVER));
			resList.add(jsonObj);
		}
		return resList;
	}

	private Collection<JSONObject> getDeviceTypeList(HiveAp hiveAp) throws JSONException{
		Collection<JSONObject> resList = new ArrayList<JSONObject>();
		boolean isAp = false, isBr= false, isCvg=false, isCvgBr=false, isSwitch=false;
		switch(hiveAp.getHiveApModel()){
			case HiveAp.HIVEAP_MODEL_28:
			case HiveAp.HIVEAP_MODEL_20:
			case HiveAp.HIVEAP_MODEL_320:
			case HiveAp.HIVEAP_MODEL_340:
			case HiveAp.HIVEAP_MODEL_370:
			case HiveAp.HIVEAP_MODEL_390:
			case HiveAp.HIVEAP_MODEL_380:
			case HiveAp.HIVEAP_MODEL_120:
			case HiveAp.HIVEAP_MODEL_110:
			case HiveAp.HIVEAP_MODEL_170:
			case HiveAp.HIVEAP_MODEL_121:
			case HiveAp.HIVEAP_MODEL_141:
			case HiveAp.HIVEAP_MODEL_230:
				isAp = true;
				break;
			case HiveAp.HIVEAP_MODEL_330:
			case HiveAp.HIVEAP_MODEL_350:
				isAp = true;
				if(hiveAp.getSoftVer() == null || "".equals(hiveAp.getSoftVer()) ||
						NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "5.0.0.0") >= 0){
					isBr = true;
				}
//				if(hiveAp.getSoftVer() == null || "".equals(hiveAp.getSoftVer()) ||
//						NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "5.1.1.0") >= 0){
//					isCvgBr = true;
//				}
				break;
			case HiveAp.HIVEAP_MODEL_BR100:
				isBr = true;
				if(hiveAp.getSoftVer() == null || "".equals(hiveAp.getSoftVer()) ||
						NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "5.1.1.0") >= 0){
					isAp = true;
				}
				break;
			case HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA:
			case HiveAp.HIVEAP_MODEL_VPN_GATEWAY:
				isCvg = true;
				if(hiveAp.getSoftVer() == null || "".equals(hiveAp.getSoftVer()) ||
						NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "5.1.1.0") >= 0){
					isAp = true;
				}
				break;
			case HiveAp.HIVEAP_MODEL_BR200:
			case HiveAp.HIVEAP_MODEL_BR200_WP:
			case HiveAp.HIVEAP_MODEL_BR200_LTE_VZ:
				isBr = true;
//				if(hiveAp.getSoftVer() == null || "".equals(hiveAp.getSoftVer()) ||
//						NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "5.1.1.0") >= 0){
//					isCvgBr = true;
//				}
				break;
			case HiveAp.HIVEAP_MODEL_SR24:
			case HiveAp.HIVEAP_MODEL_SR2124P:
			case HiveAp.HIVEAP_MODEL_SR2024P:
			case HiveAp.HIVEAP_MODEL_SR2148P:
			case HiveAp.HIVEAP_MODEL_SR48:
				isSwitch = true;
				break;
			default:
		}
		if(isAp){
			JSONObject jsonObjAp = new JSONObject();
			jsonObjAp.put("deviceKey", HiveAp.Device_TYPE_HIVEAP);
			if(hiveAp.isCVGAppliance()){
				jsonObjAp.put("deviceValue", MgrUtil.getEnumString("enum.cvg.deviceType."+HiveAp.Device_TYPE_HIVEAP));
			}else{
				jsonObjAp.put("deviceValue", MgrUtil.getEnumString("enum.hiveAp.deviceType."+HiveAp.Device_TYPE_HIVEAP));
			}
			resList.add(jsonObjAp);
		}
		if(isBr){
			JSONObject jsonObjBr = new JSONObject();
			jsonObjBr.put("deviceKey", HiveAp.Device_TYPE_BRANCH_ROUTER);
			jsonObjBr.put("deviceValue", MgrUtil.getEnumString("enum.hiveAp.deviceType."+HiveAp.Device_TYPE_BRANCH_ROUTER));
			resList.add(jsonObjBr);
		}
		if(isCvg){
			JSONObject jsonObjCvg = new JSONObject();
			jsonObjCvg.put("deviceKey", HiveAp.Device_TYPE_VPN_GATEWAY);
			jsonObjCvg.put("deviceValue", MgrUtil.getEnumString("enum.hiveAp.deviceType."+HiveAp.Device_TYPE_VPN_GATEWAY));
			resList.add(jsonObjCvg);
		}
		if(isCvgBr){
			JSONObject jsonObjCvg = new JSONObject();
			jsonObjCvg.put("deviceKey", HiveAp.Device_TYPE_VPN_BR);
			jsonObjCvg.put("deviceValue", MgrUtil.getEnumString("enum.hiveAp.deviceType."+HiveAp.Device_TYPE_VPN_BR));
			resList.add(jsonObjCvg);
		}
		if(isSwitch){
			JSONObject jsonObjCvg = new JSONObject();
			jsonObjCvg.put("deviceKey", HiveAp.Device_TYPE_SWITCH);
			jsonObjCvg.put("deviceValue", MgrUtil.getEnumString("enum.hiveAp.deviceType."+HiveAp.Device_TYPE_SWITCH));
			resList.add(jsonObjCvg);
		}

		//set device type
		for(JSONObject obj : resList){
			if(obj.get("deviceKey").toString().equals(String.valueOf(hiveAp.getDeviceType()))){
				return resList;
			}
		}
		hiveAp.setDeviceType(Short.valueOf(resList.iterator().next().get("deviceKey").toString()));

		return resList;
	}

	private Collection<JSONObject> getDevicePageStyleList(HiveAp hiveAp) throws JSONException{

		Collection<JSONObject> resList = new ArrayList<JSONObject>();
		DeviceKey deviceKey = new DeviceKey(hiveAp.getHiveApModel(), hiveAp.getDeviceType());
		Collection<DeviceStyle> mapList = AhConstantUtil.getDevicePageStyleMap().get(deviceKey.getKey());
		if(mapList == null){
			return resList;
		}
		for(DeviceStyle deviceStyle : mapList){
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("id", deviceStyle.getId());
			jsonObj.put("hide", deviceStyle.isHide());
			resList.add(jsonObj);
		}
		return resList;
	}

	protected void prepareMgt0InterfaceSettings() {
		if(this.getDataSource().isDhcp() && !this.getDataSource().isDhcpFallback()){
			mgt0NetworkType = HiveAp.USE_DHCP_WITHOUTFALLBACK;
		}else if(this.getDataSource().isDhcp() && this.getDataSource().isDhcpFallback()){
			mgt0NetworkType = HiveAp.USE_DHCP_FALLBACK;
		}else{
			mgt0NetworkType = HiveAp.USE_STATIC_IP;
		}
	}

	public Long getRoutingPolicyId() {
		return routingPolicyId;
	}

	public void setRoutingPolicyId(Long routingPolicyId) {
		this.routingPolicyId = routingPolicyId;
	}

	public List<CheckItem> getList_routingPolicy() {
		return list_routingPolicy;
	}

	public String getRoutingPolicyDetailDivStyle(){
		if (isEnabledOverrideRoutingPolicy()) {
			return "";
		}
		return "none";
	}

	public boolean isEnabledOverrideRoutingPolicy() {
		return getDataSource().getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER && getDataSource().getRoutingProfilePolicy() != null;
	}

	public String getCustomTag1() {
		return customTag1;
	}

	public void setCustomTag1(String customTag1) {
		this.customTag1 = customTag1;
	}

	public String getCustomTag2() {
		return customTag2;
	}

	public void setCustomTag2(String customTag2) {
		this.customTag2 = customTag2;
	}

	public String getCustomTag3() {
		return customTag3;
	}

	public void setCustomTag3(String customTag3) {
		this.customTag3 = customTag3;
	}

    public boolean isDoneBtnDisplayFlag() {
        Object flag = MgrUtil.getSessionAttribute(UPLOAD_CONFIG_IDM_KEY);
        return null == flag ? false : Boolean.parseBoolean(flag.toString());
    }

	public OptionsTransfer getPreferredSsidOptions() {
		return preferredSsidOptions;
	}

	public void setPreferredSsidOptions(OptionsTransfer preferredSsidOptions) {
		this.preferredSsidOptions = preferredSsidOptions;
	}

	public Long getPreferredSsid() {
		return preferredSsid;
	}

	public void setPreferredSsid(Long preferredSsid) {
		this.preferredSsid = preferredSsid;
	}

	public List<Long> getPreferredSsids() {
		return preferredSsids;
	}

	public void setPreferredSsids(List<Long> preferredSsids) {
		this.preferredSsids = preferredSsids;
	}

	//Added from Chesapeake For LLDP Start*************
	private void revokeOverrideLLDPCDP(){
		if(getDataSource().isOverrideNetworkPolicySetting()){
			getDataSource().setOverrideNetworkPolicySetting(false);
		}
		for(Long key:this.getDataSource().getDeviceInterfaces().keySet()){
			DeviceInterface dInt = this.getDataSource().getDeviceInterfaces().get(key);
			dInt.setLldpTransmit(true);
			dInt.setLldpReceive(true);
			dInt.setCdpReceive(true);
			dInt.setClientReporting(true);
			dInt.setPortDescription("");
		}
	}
	
	private List<Short> getNonHostPorts(){
		List<Short> nonHostPorts = new ArrayList<Short>();
		if(null != getDataSource().getConfigTemplate() && null != getDataSource().getConfigTemplate().getPortProfiles()){
			//Set<PortGroupProfile> pgps = getDataSource().getConfigTemplate().getPortProfiles();
			// find non-host ports
		    PortGroupProfile portProfile=getDataSource().getPortGroup(getDataSource().getConfigTemplate());
			//for(PortGroupProfile pgp :pgps){
				if(null != portProfile && null != portProfile.getBasicProfiles()){
					for(PortBasicProfile pbp : portProfile.getBasicProfiles()){
						if(null != pbp.getAccessProfile() && (pbp.getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_8021Q
								|| pbp.getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA)){
							String[] ethPorts = pbp.getETHs();
							String[] sfpPorts = pbp.getSFPs();
							
							if(null != ethPorts){
								for(int i=0;i<ethPorts.length;i++){
									short tempConst = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(ethPorts[i]), getDataSource().getHiveApModel());
									nonHostPorts.add(tempConst);
								}
							}
							if(null != sfpPorts){
								for(int i=0;i<sfpPorts.length;i++){
									short tempConst = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfpPorts[i]), getDataSource().getHiveApModel());
									nonHostPorts.add(tempConst);
								}
							}

						}
					}
				}
			//}

	}
		return nonHostPorts;
	}
	
	private void overrideLLDPCDPSetting(ConfigTemplate template) {
		if (null != getDataSource()
				&& null != template
				&& null != template.getLldpCdp()
				&& null != template.getPortProfiles()
				&& template.getPortProfiles().size() > 0) {

			LLDPCDPProfile lldpcdp = QueryUtil.findBoById(LLDPCDPProfile.class, template.getLldpCdp().getId());
			if (lldpcdp == null) {
				String tempStr[] = { getText("hiveAp.discoveryProtocolLabel") };
				addActionError(getText("info.ssid.warning", tempStr));
			}

			if(null != lldpcdp && (lldpcdp.isEnableLLDPNonHostPorts() 
					|| lldpcdp.isEnableLLDPHostPorts()
					|| lldpcdp.isEnableCDPHostPorts()
					|| lldpcdp.isEnableCDPNonHostPorts())){
				List<Short> nonHostPorts = getNonHostPorts();
				for(Long key:this.getDataSource().getDeviceInterfaces().keySet()){
					DeviceInterface dInt = this.getDataSource().getDeviceInterfaces().get(key);
					if(dInt.isPortChannel()){
						dInt.setLldpTransmit(true);
						dInt.setLldpReceive(true);
						dInt.setCdpReceive(true);
					}else{
						if(!nonHostPorts.isEmpty()){
							if(nonHostPorts.contains(dInt.getDeviceIfType())){
								if(lldpcdp.isEnableLLDPNonHostPorts() || lldpcdp.isEnableCDPNonHostPorts()){
									if(lldpcdp.isEnableLLDPNonHostPorts()){
										dInt.setLldpEnable(true);
										if(lldpcdp.isLldpReceiveOnly()){
											dInt.setLldpTransmit(false);
										}
									}else{
										dInt.setLldpEnable(false);
									}
									
									if(lldpcdp.isEnableCDPNonHostPorts()){
										dInt.setCdpEnable(true);
									}else{
										dInt.setCdpEnable(false);
									}
								}else {
									dInt.setLldpEnable(false);
									dInt.setCdpEnable(false);
								}
							}
							if(!nonHostPorts.contains(dInt.getDeviceIfType())){
								if(lldpcdp.isEnableLLDPHostPorts() || lldpcdp.isEnableCDPHostPorts()){
									if(lldpcdp.isEnableLLDPHostPorts()){
										dInt.setLldpEnable(true);
										if(lldpcdp.isLldpReceiveOnly()){
											dInt.setLldpTransmit(false);
										}
									}else{
										dInt.setLldpEnable(false);
									}
									
									if(lldpcdp.isEnableCDPHostPorts()){
										dInt.setCdpEnable(true);
									}else{
										dInt.setCdpEnable(false);
									}
								}else{
									dInt.setLldpEnable(false);
									dInt.setCdpEnable(false);
								}
							}
		
						}else{
							if(lldpcdp.isEnableLLDPHostPorts()){
								dInt.setLldpEnable(true);
								if(lldpcdp.isLldpReceiveOnly()){
									dInt.setLldpTransmit(false);
								}
							}else{
								dInt.setLldpEnable(false);
							}
							if(lldpcdp.isEnableCDPHostPorts()){
								dInt.setCdpEnable(true);
							}else{
								dInt.setCdpEnable(false);
							}
						}
					}
				}
				for(Long key:this.getDataSource().getDeviceInterfaces().keySet()){
					DeviceInterface dInt = this.getDataSource().getDeviceInterfaces().get(key);
					if(!dInt.isPortChannel()){
						if(!dInt.isLldpEnable()){
							dInt.setLldpTransmit(false);
							dInt.setLldpReceive(false);
						}else{
							dInt.setLldpTransmit(true);
							dInt.setLldpReceive(true);
						}
						if(!dInt.isCdpEnable()){
							dInt.setCdpReceive(false);
						}else{
							dInt.setCdpReceive(true);
						}
					}
				}

			}else{
				for(Long key:this.getDataSource().getDeviceInterfaces().keySet()){
					DeviceInterface dInt = this.getDataSource().getDeviceInterfaces().get(key);
					dInt.setLldpTransmit(true);
					dInt.setLldpReceive(true);
					dInt.setCdpReceive(true);
					dInt.setLldpEnable(false);
					dInt.setCdpEnable(false);
				}
			}
		}else{
			for(Long key:this.getDataSource().getDeviceInterfaces().keySet()){
				DeviceInterface dInt = this.getDataSource().getDeviceInterfaces().get(key);
				dInt.setLldpTransmit(true);
				dInt.setLldpReceive(true);
				dInt.setCdpReceive(true);
				dInt.setLldpEnable(false);
				dInt.setCdpEnable(false);
			}
		}
	}
	
	private Map<Short,Boolean> getConfiguredPortsInNetworkPolicy(ConfigTemplate template){
		if(null == template){
			return null;
		}
		
		Map<Short,Boolean> enabledClientReportingPortsMap = new HashMap<Short,Boolean>();;
		// find non-host ports
		PortGroupProfile portProfile = getDataSource().getPortGroup(template);
		if(null != portProfile && null != portProfile.getBasicProfiles()){
			for(PortBasicProfile pbp : portProfile.getBasicProfiles()){
				if(null != pbp.getAccessProfile() && 
						(pbp.getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_ACCESS 
						|| pbp.getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA 
						|| pbp.getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_8021Q)){
					String[] ethPorts = pbp.getETHs();
					String[] sfpPorts = pbp.getSFPs();
					
					if(pbp.isEnabledlinkAggregation()){
						short portChannel = DeviceInfType.PortChannel.getFinalValue(pbp.getPortChannel(), getDataSource().getHiveApModel());
						enabledClientReportingPortsMap.put(portChannel, pbp.getAccessProfile().isEnabledClientReport());
						continue;
					}
					
					if(null != ethPorts){
						for(int i=0;i<ethPorts.length;i++){
							short tempConst = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(ethPorts[i]), getDataSource().getHiveApModel());
							enabledClientReportingPortsMap.put(tempConst, pbp.getAccessProfile().isEnabledClientReport());
						}
					}
					if(null != sfpPorts){
						for(int i=0;i<sfpPorts.length;i++){
							short tempConst = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfpPorts[i]), getDataSource().getHiveApModel());
							enabledClientReportingPortsMap.put(tempConst, pbp.getAccessProfile().isEnabledClientReport());
						}
					}

				}
			}
		}
		return enabledClientReportingPortsMap;
	}
	
	private void overrideClientReporting(ConfigTemplate template){
		if (null != getDataSource()
				&& null != template
				&& null != template.getPortProfiles()
				&& template.getPortProfiles().size() > 0){
			Map<Short,Boolean> configuredPortsInNetworkPolicy = getConfiguredPortsInNetworkPolicy(template);
			List<Short> deviceInterfaceTypeList = new ArrayList<Short>();
			for(Short deviceIfType: configuredPortsInNetworkPolicy.keySet()){
				deviceInterfaceTypeList.add(deviceIfType);
			}
			if(!deviceInterfaceTypeList.isEmpty()){
				for(Long key:this.getDataSource().getDeviceInterfaces().keySet()){
					DeviceInterface dInt = this.getDataSource().getDeviceInterfaces().get(key);
					
					if(deviceInterfaceTypeList.contains(dInt.getDeviceIfType())){
						dInt.setClientReporting(configuredPortsInNetworkPolicy.get(dInt.getDeviceIfType()));
						dInt.setEnableClientReporting(true);
					}else{
						dInt.setClientReporting(true);
						dInt.setEnableClientReporting(false);
					}
					
				}
			}else{
				for(Long key:this.getDataSource().getDeviceInterfaces().keySet()){
					DeviceInterface dInt = this.getDataSource().getDeviceInterfaces().get(key);
					dInt.setClientReporting(true);
					dInt.setEnableClientReporting(false);
				}
			}
		}else{
			for(Long key:this.getDataSource().getDeviceInterfaces().keySet()){
				DeviceInterface dInt = this.getDataSource().getDeviceInterfaces().get(key);
				dInt.setClientReporting(true);
				dInt.setEnableClientReporting(false);
			}
		}
	}
	
	
	private Map<Short,Map<String,String>> getPortsDesAndShutDownInNetworkPolicy(ConfigTemplate template){
		if(null == template){
			return null;
		}
		Map<Short,Map<String,String>> PortsDesAndShutDownMap2 = new HashMap<Short,Map<String,String>>();
		// find non-host ports
		PortGroupProfile portProfile = getDataSource().getPortGroup(template);
		if(null != portProfile && null != portProfile.getBasicProfiles()){
			for(PortBasicProfile pbp : portProfile.getBasicProfiles()){
				if(null != pbp.getAccessProfile()){
					String[] ethPorts = pbp.getETHs();
					String[] sfpPorts = pbp.getSFPs();
					
					if(pbp.isEnabledlinkAggregation()){
						short portChannel = DeviceInfType.PortChannel.getFinalValue(pbp.getPortChannel(), getDataSource().getHiveApModel());
						
						Map<String, String> PortsDesAndShutDownValue = new HashMap<String,String>();
						PortsDesAndShutDownValue.put("adminState", pbp.getAccessProfile().isShutDownPorts()? "1":"0");
						PortsDesAndShutDownValue.put("portDes", pbp.getAccessProfile().getPortDescription()== null ? "": pbp.getAccessProfile().getPortDescription());
						PortsDesAndShutDownMap2.put(portChannel, PortsDesAndShutDownValue);
					}
					
					if(null != ethPorts){
						for(int i=0;i<ethPorts.length;i++){
							short tempConst = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(ethPorts[i]), getDataSource().getHiveApModel());
						
							Map<String, String> PortsDesAndShutDownValue = new HashMap<String,String>();
							PortsDesAndShutDownValue.put("adminState", pbp.getAccessProfile().isShutDownPorts()? "1":"0");
							PortsDesAndShutDownValue.put("portDes", pbp.getAccessProfile().getPortDescription()== null ? "": pbp.getAccessProfile().getPortDescription());
							PortsDesAndShutDownMap2.put(tempConst, PortsDesAndShutDownValue);
						}
					}
					if(null != sfpPorts){
						for(int i=0;i<sfpPorts.length;i++){
							short tempConst = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfpPorts[i]), getDataSource().getHiveApModel());
						
							Map<String, String> PortsDesAndShutDownValue = new HashMap<String,String>();
							PortsDesAndShutDownValue.put("adminState", pbp.getAccessProfile().isShutDownPorts()? "1":"0");
							PortsDesAndShutDownValue.put("portDes", pbp.getAccessProfile().getPortDescription()== null ? "": pbp.getAccessProfile().getPortDescription());
							PortsDesAndShutDownMap2.put(tempConst, PortsDesAndShutDownValue);
						}
					}

				}
			}
		}
		return PortsDesAndShutDownMap2;
	}
	
	private void overridePortAdminStateByConfigTemplate(ConfigTemplate template,Map<String, DeviceInterfaceAdapter> deviceInterfaceAdapters){
		if (null != deviceInterfaceAdapters
				&& null != template
				&& null != template.getPortProfiles()
				&& template.getPortProfiles().size() > 0){
			Map<Short,Map<String,String>> portsDesAndShutDownMap = getPortsDesAndShutDownInNetworkPolicy(template);
			List<Short> deviceInterfaceTypeList = new ArrayList<>();
			for(short deviceIfType: portsDesAndShutDownMap.keySet()){
				deviceInterfaceTypeList.add(deviceIfType); // MgrUtil.getEnumString("enum.switch.interface."+deviceIfType)
			}
			if(!deviceInterfaceTypeList.isEmpty()){
				for(String key:deviceInterfaceAdapters.keySet()){
					DeviceInterface dInt = deviceInterfaceAdapters.get(key).getDeviceInterface();
					
					if(deviceInterfaceTypeList.contains(dInt.getDeviceIfType())){
						dInt.setAdminState(Short.parseShort(portsDesAndShutDownMap.get(dInt.getDeviceIfType()).get("adminState")));
					}else{
						dInt.setAdminState((short)0);
					}
					
				}
			}else{
				for(String key:deviceInterfaceAdapters.keySet()){
					DeviceInterface dInt = deviceInterfaceAdapters.get(key).getDeviceInterface();
					dInt.setAdminState((short)0);
				}
			}
		}else{
			for(String key:deviceInterfaceAdapters.keySet()){
				DeviceInterface dInt = deviceInterfaceAdapters.get(key).getDeviceInterface();
				dInt.setAdminState((short)0);
			}
		}
	}
	
	private void overridePortDesAndShutDown(ConfigTemplate template){
		if (null != getDataSource()
				&& null != template
				&& null != template.getPortProfiles()
				&& template.getPortProfiles().size() > 0){
			Map<Short,Map<String,String>> portsDesAndShutDownMap = getPortsDesAndShutDownInNetworkPolicy(template);
			List<Short> deviceInterfaceTypeList = new ArrayList<Short>();
			for(Short deviceIfType: portsDesAndShutDownMap.keySet()){
				deviceInterfaceTypeList.add(deviceIfType);
			}
			if(!deviceInterfaceTypeList.isEmpty()){
				for(Long key:this.getDataSource().getDeviceInterfaces().keySet()){
					DeviceInterface dInt = this.getDataSource().getDeviceInterfaces().get(key);
					
					if(deviceInterfaceTypeList.contains(dInt.getDeviceIfType())){
						dInt.setAdminState(Short.parseShort(portsDesAndShutDownMap.get(dInt.getDeviceIfType()).get("adminState")));
						dInt.setPortDescription(portsDesAndShutDownMap.get(dInt.getDeviceIfType()).get("portDes"));
						dInt.setEnableOverridePortDescription(true);
					}else{
						dInt.setAdminState((short)0);
						dInt.setPortDescription("");
						dInt.setEnableOverridePortDescription(false);
					}
					
				}
			}else{
				for(Long key:this.getDataSource().getDeviceInterfaces().keySet()){
					DeviceInterface dInt = this.getDataSource().getDeviceInterfaces().get(key);
					dInt.setAdminState((short)0);
					dInt.setPortDescription("");
					dInt.setEnableOverridePortDescription(false);
				}
			}
		}else{
			for(Long key:this.getDataSource().getDeviceInterfaces().keySet()){
				DeviceInterface dInt = this.getDataSource().getDeviceInterfaces().get(key);
				dInt.setAdminState((short)0);
				dInt.setPortDescription("");
				dInt.setEnableOverridePortDescription(false);
			}
		}
	}
	
	private JSONArray getOverrideNetworkPolicySetting() throws JSONException{
		JSONArray jsonArray = new JSONArray();
		
		ConfigTemplate template = QueryUtil.findBoById(
				ConfigTemplate.class, configTemplate, this);
		if(null != template){
		    overrideLLDPCDPSetting(template);
			overrideClientReporting(template);
			overridePortDesAndShutDown(template);
			
			List<DeviceInterface> dInterfacesTemp = new ArrayList<DeviceInterface>();
			for(DeviceInterface dInf : getDataSource().getDeviceInterfaces().values()){
				if(dInf.getDeviceIfType() >= AhInterface.DEVICE_IF_TYPE_ETH1){
					dInterfacesTemp.add(dInf);
				}
			}
			Collections.sort(dInterfacesTemp, new Comparator<DeviceInterface>(){
				@Override
				public int compare(DeviceInterface o1, DeviceInterface o2) {
					if(o1 == null || o2 == null){
						return -1;
					}
					return o1.getDeviceIfType() - o2.getDeviceIfType();
				}
			});
			
			for(DeviceInterface dInt:dInterfacesTemp){
				JSONObject object = new JSONObject();
				object.put("deviceIfType", dInt.getDeviceIfType());
				object.put("lldpenable", dInt.isLldpEnable());
				object.put("cdpEnable", dInt.isCdpEnable());
				object.put("lldpTransmitValue",dInt.isLldpTransmit());
				object.put("lldpReceiveValue", dInt.isLldpReceive());
				object.put("cdpReceiveValue", dInt.isCdpReceive());
				object.put("clientReporting", dInt.isClientReporting());
				object.put("enableClientReporting", dInt.isEnableClientReporting());
				object.put("adminState", dInt.getAdminState());
				object.put("portDescription", dInt.getPortDescription());
				object.put("enableOverridePortDes", dInt.isEnableOverridePortDescription());
				jsonArray.put(object);
			}
			
		}

		return jsonArray;
	}
	//Added from Chesapeake For LLDP end***************

	//Added from Chesapeake for forwarding db start****************

	private String fdb_ahDtClumnDefs;
	private String fdb_ahDtDatas;
	private String fdb_editInfo;

	private String[] fdb_vlans;
	private String[] fdb_interfaces;
	private String[] fdb_macaddress;
	//private String[] ruleIds;

	private JSONArray getForwardingDBInterfaceOptionJson(ConfigTemplate template)
			throws JSONException {
		JSONArray jsonArray = new JSONArray();

		if(null != template){
			List<CheckItem> options = getForwardingDBInterfaceOption(template);
			for (CheckItem cItem : options) {
				JSONObject object = new JSONObject();
				object.put("value", cItem.getId());
				object.put("label", cItem.getValue());
				jsonArray.put(object);
			}
		}

		return jsonArray;
	}

	private List<CheckItem> getForwardingDBInterfaceOption(ConfigTemplate ct){
		List<CheckItem> interfaceOptions = new ArrayList<CheckItem>();
		List<Short> interfaceListTempList = new ArrayList<Short>();
		if(null != getDataSource() && null != ct && null != ct.getPortProfiles()){
			PortGroupProfile pgp = getDataSource().getPortGroup(ct);
			if(null != pgp && null != pgp.getBasicProfiles()){
				for(PortBasicProfile pbp : pgp.getBasicProfiles()){
					if(null != pbp.getAccessProfile() && pbp.getAccessProfile().getPortType()!= PortAccessProfile.PORT_TYPE_WAN){
						String[] ethPorts = pbp.getETHs();
						String[] sfpPorts = pbp.getSFPs();

						if(pbp.isEnabledlinkAggregation()){
							short portChannel = DeviceInfType.PortChannel.getFinalValue(pbp.getPortChannel(), getDataSource().getHiveApModel());
							if(!interfaceListTempList.contains(portChannel)){
								interfaceListTempList.add(portChannel);
								CheckItem item = new CheckItem((long) portChannel, MgrUtil
										.getEnumString("enum.switch.interface." + portChannel));
								interfaceOptions.add(item);
								//when the port type is portChannel, the Aggregate ports no need to list.
								continue;
							}
						}
						
						if(null != ethPorts){
							for(int i=0;i<ethPorts.length;i++){
								short tempConst = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(ethPorts[i]), getDataSource().getHiveApModel());
								if(!interfaceListTempList.contains(tempConst)){
									interfaceListTempList.add(tempConst);
									CheckItem item = new CheckItem((long)tempConst, MgrUtil
											.getEnumString("enum.switch.interface."+tempConst));
									interfaceOptions.add(item);
								}
							}
						}
						if(null != sfpPorts){
							for(int i=0;i<sfpPorts.length;i++){
								short tempConst = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfpPorts[i]), getDataSource().getHiveApModel());
								if(!interfaceListTempList.contains(tempConst)){
									interfaceListTempList.add(tempConst);
									CheckItem item = new CheckItem((long)tempConst, MgrUtil
											.getEnumString("enum.switch.interface."+tempConst));
									interfaceOptions.add(item);
								}
							}
						}
					}
				}
			}
			Collections.sort(interfaceOptions, new Comparator<CheckItem>() {
				public int compare(CheckItem o1, CheckItem o2) {
					return (int) (o1.getId() - o2.getId());
				}
			});
		}

		return interfaceOptions;
	}

	private void prepareAhDataTableColumnDefsForForwardingDB() throws JSONException{
		List<AhDataTableColumn> ahDataTableColumns = new ArrayList<AhDataTableColumn>();
		AhDataTableColumn column = new AhDataTableColumn();
		column.setMark("interfaces");
		if(null != getDataSource() && null != getDataSource().getConfigTemplate()
				&& null != getDataSource().getConfigTemplate().getPortProfiles()){
			column.setOptions(getForwardingDBInterfaceOption(QueryUtil.findBoById(ConfigTemplate.class, getDataSource().getConfigTemplate().getId(),this)));
		}else{
			column.setOptions(getForwardingDBInterfaceOption(QueryUtil.findBoById(ConfigTemplate.class, configTemplates.get(0).getId(),this)));
		}

		ahDataTableColumns.add(column);

		fdb_ahDtClumnDefs = generateAhDataTableColumnJsonString(ahDataTableColumns);
	}

	private void prepareAhDaTableTransientDataForForwardingDB(){
		if(null != getDataSource() && null != getDataSource().getForwardingDB()){
			getDataSource().getForwardingDB().setEditInfo(fdb_editInfo);
			getDataSource().getForwardingDB().setFdb_vlans(fdb_vlans);
			getDataSource().getForwardingDB().setFdb_interfaces(fdb_interfaces);
			getDataSource().getForwardingDB().setFdb_macaddress(fdb_macaddress);
		}
	}

	private void prepareAhDataTableDataAfterEditForForwardingDB() throws JSONException{
		if (getDataSource() == null
				|| getDataSource().getForwardingDB() == null
				|| getDataSource().getForwardingDB().getFdb_vlans() == null) {
			fdb_ahDtDatas = "";
			return ;
		}
		JSONArray jsonArray = new JSONArray();
		for(int i =0;i<getDataSource().getForwardingDB().getFdb_vlans().length;i++){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("vlans", getDataSource().getForwardingDB().getFdb_vlans()[i]);
			jsonObject.put("interfaces", getDataSource().getForwardingDB().getFdb_interfaces()[i]);
			jsonObject.put("macAddress", getDataSource().getForwardingDB().getFdb_macaddress()[i]);
			jsonArray.put(jsonObject);
		}

		fdb_ahDtDatas = jsonArray.toString();
	}

	private void prepareAhDataTableDataForForwardingDB() throws JSONException{
		if (getDataSource() == null
				|| getDataSource().getForwardingDB() == null
				|| getDataSource().getForwardingDB().getMacAddressEntries().isEmpty() ) {
			fdb_ahDtDatas = "";
			return ;
		}

		JSONArray jsonArray = new JSONArray();
		for(MacAddressLearningEntry mae :getDataSource().getForwardingDB().getMacAddressEntries()){
			if(null != mae && mae.getDeviceInfoConstant() != 0){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("vlans", mae.getVlanId());
				jsonObject.put("interfaces", mae.getDeviceInfoConstant());
				jsonObject.put("macAddress", mae.getMacAddress());
				jsonArray.put(jsonObject);
			}
		}
		fdb_ahDtDatas = jsonArray.toString();
	}

	private void prepareForForwardingDB(){
		if(null == getDataSource().getForwardingDB()){
			getDataSource().setForwardingDB(new ForwardingDB());
		}
		try {
			prepareAhDataTableColumnDefsForForwardingDB();
			//prepareAhDataTableDataForForwardingDB();
		} catch (JSONException e) {
			log.error("prepareForForwardingDB", e);
		}
	}

	private void prepareForForwardingDBError(){
		try {
			prepareAhDaTableTransientDataForForwardingDB();
			prepareAhDataTableDataAfterEditForForwardingDB();
		} catch (JSONException e) {
			log.error("prepareForForwardingDBError", e);
		}

	}

	public Range getForwardingDBIdletimeoutRange() {
		return getAttributeRange("idleTimeout", ForwardingDB.class);
	}

	private void updateForwardingDB(HiveAp hiveAp) throws Exception {
		if (null != hiveAp) {
			ForwardingDB fdb = hiveAp.getForwardingDB();
			if (null != fdb) {
				if (hiveAp.getForwardingDB().getId() == null) {
					fdb.setOwner(getDomain());
					QueryUtil.createBo(fdb);
				} else {
					QueryUtil.updateBo(fdb);

				}
				hiveAp.setForwardingDB(fdb);
			} else {
				hiveAp.setForwardingDB(null);
			}
		}
	}
	
	private String getMacAddressNoLink(String macAddress){
		StringBuffer sBuffer =new StringBuffer();
		if(null != macAddress && !"".equals(macAddress)){
			for(int i=0;i<macAddress.length();i++){
				char temp = macAddress.charAt(i);
				if(temp != '.' &&  temp != ':' && temp != '-'){
					sBuffer.append(temp);
				}
			}
		}
		return sBuffer.toString();
	}

	protected boolean checkForwardingDBStatickEntery(){
		if(null != fdb_vlans && null != fdb_interfaces && null != fdb_macaddress){
			//the max length is 8k(8*1024)
			if(fdb_vlans.length >8192){
				addActionError(MgrUtil.getUserMessage("error.objectReachLimit"));
				return false;
			}

			List<Integer> list = new ArrayList<>();
			for(int i=0;i<fdb_vlans.length;i++){
				String vlan = fdb_vlans[i];
				String interfaces = fdb_interfaces[i];
				String macAddress =getMacAddressNoLink(fdb_macaddress[i]);
				
				for(int j=i+1;j<fdb_vlans.length;j++){
					if((vlan != null && fdb_vlans[j] != null && vlan.equals(fdb_vlans[j]))
						&&(interfaces != null && fdb_interfaces[j] != null && interfaces.equals(fdb_interfaces[j]))
						&&(macAddress != null && fdb_macaddress[j] != null && macAddress.equals(getMacAddressNoLink(fdb_macaddress[j])))){
						list.add(i+1);
						break;
					}
				}
			}

			if(list.size() > 1){
				addActionError("Some rules in ("+list.toString()+") rows cannot be added because the same ones already exist.");
				return false;
			} else if(list.size() == 1){
				addActionError("The rule in ("+list.toString()+") row cannot be added because the same one already exist.");
				return false;
			}

		}
		return true;
	}

	protected void setForwardingDBStaticEntry() {
		if (null != fdb_vlans && null != fdb_interfaces && null != fdb_macaddress) {
			List<MacAddressLearningEntry> rules = new ArrayList<MacAddressLearningEntry>();
			for (int i = 0; i < fdb_vlans.length; i++) {
				if(null != fdb_vlans[i] && !"".equals(fdb_vlans[i])
						&& null != fdb_interfaces[i] && !"".equals(fdb_interfaces[i])
						&& null != fdb_macaddress[i] && !"".equals(fdb_macaddress[i])){
					MacAddressLearningEntry male = new MacAddressLearningEntry();
				//	male.setVlanProfile(QueryUtil.findBoById(Vlan.class, Long.parseLong(fdb_vlans[i]), new VlanAction()));
					male.setVlanId(Integer.parseInt(fdb_vlans[i]));
					male.setDeviceInfoConstant(Short.valueOf(fdb_interfaces[i]));
					male.setMacAddress(fdb_macaddress[i]);
					rules.add(male);
				}

			}
			getDataSource().getForwardingDB().setMacAddressEntries(rules);
		}else{
			if(null != getDataSource().getForwardingDB()
					&& null != getDataSource().getForwardingDB().getMacAddressEntries()
					&& !getDataSource().getForwardingDB().getMacAddressEntries().isEmpty()){
				getDataSource().getForwardingDB().getMacAddressEntries().clear();
			}

		}
	}

	private boolean checkAndSetForwardingDB() throws Exception{
		if(!checkForwardingDBStatickEntery()){
			prepareForForwardingDBError();
			jsonObject = new JSONObject();
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
			return false;
		}
		setForwardingDBStaticEntry();
		return true;
	}


	public String getFdbPartVlansStyle(){
		if(null != getDataSource() && null != getDataSource().getForwardingDB()){
			return  getDataSource().getForwardingDB().isDisableMacLearnForPartVlans() ? "" :"none";
		}
		return "none";
	}

	//Added from Chesapeake for forwarding db start****************

	private boolean checkIgmpPolicyVlanId() throws Exception{
		if(!getDataSource().isOverrideIgmpSnooping()){
				return true;
			}
		if(!checkDupltiIgmpPolicyVlanId()){
			jsonObject = new JSONObject();
			jsonObject.put("resultStatus", false);
			jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
			return false;
		}
		return true;
	}

	private boolean checkDupltiIgmpPolicyVlanId(){
		List<Integer> list = new ArrayList<>();
		if(igmpVlanIds != null && igmpVlanIds.length !=0){
			for(int i=0;i<igmpVlanIds.length;i++){
				String vlan = igmpVlanIds[i];
				for(int j=i+1;j<igmpVlanIds.length;j++){
					if((vlan != null && igmpVlanIds[j] != null && vlan.equals(igmpVlanIds[j]))){
						list.add(i+1);
						break;
					}
				}
			}
		}

		if(list.size() > 1){
			addActionError(MgrUtil.getUserMessage("config.switchSettings.igmp.vlanid.more.exist", list.toString()));
			return false;
		} else if(list.size() == 1){
			addActionError(MgrUtil.getUserMessage("config.switchSettings.igmp.vlanid.one.exist", list.toString()));
			return false;
		}

	 return true;
   }
	//for switch igmp settings
		private void prepareSaveIgmpPolicy(){
			if (igmpVlanIds!=null && igmpVlanIds.length != 0){
				Set<IgmpPolicy> tempIgmpSet = new HashSet<IgmpPolicy>();
				for(int i =0;i<igmpVlanIds.length;i++){
					if(null != igmpVlanIds[i] && !"".equals(igmpVlanIds[i])){
						IgmpPolicy igmp = new IgmpPolicy();
						igmp.setIgmpSnooping(igmpSnoopings[i].equalsIgnoreCase("false") ? false : true);
						igmp.setImmediateLeave(immediateLeaves[i].equalsIgnoreCase("false") ? false : true);
						igmp.setDelayLeaveQueryInterval(delayLeaveQueryIntervals[i].equals("") ? null : Integer.parseInt(delayLeaveQueryIntervals[i]));
						igmp.setDelayLeaveQueryCount(delayLeaveQueryCounts[i].equals("") ? null : Integer.parseInt(delayLeaveQueryCounts[i]));
						igmp.setRouterPortAginTime(routerPortAginTimes[i].equals("") ? null : Integer.parseInt(routerPortAginTimes[i]));
						igmp.setRobustnessCount(robustnessCounts[i].equals("") ? null : Integer.parseInt(robustnessCounts[i]));
						igmp.setVlanId(igmpVlanIds[i].equalsIgnoreCase("") ? null : Integer.parseInt(igmpVlanIds[i]));
						igmp.setHiveAp(getDataSource());
						igmp.setOwner(getDomain());
						tempIgmpSet.add(igmp);
					}
				}
				getDataSource().setIgmpPolicys(tempIgmpSet);
			}else{
				getDataSource().setIgmpPolicys(null);
			}
		}

		private void prepareForIgmpError(){
			try {
				if(null != getDataSource() && null != getDataSource().getIgmpPolicys()){
					getDataSource().setIgmpEditInfo(igmpEditInfo);
					getDataSource().setIgmpSnoopings(igmpSnoopings);
					getDataSource().setImmediateLeaves(immediateLeaves);
					getDataSource().setDelayLeaveQueryIntervals(delayLeaveQueryIntervals);
					getDataSource().setDelayLeaveQueryCounts(delayLeaveQueryCounts);
					getDataSource().setRouterPortAginTimes(routerPortAginTimes);
					getDataSource().setRobustnessCounts(robustnessCounts);
					getDataSource().setIgmpVlanIds(igmpVlanIds);
				}

				if (getDataSource() == null
						|| getDataSource().getIgmpPolicys() == null ) {
					ahIgmpDtDatas = "";
					return ;
				}

				if(null != getDataSource().getIgmpVlanIds()){
					JSONArray jsonArray = new JSONArray();
					for(int i =0;i<getDataSource().getIgmpVlanIds().length;i++){
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("igmpSnoopings", Boolean.valueOf(getDataSource().getIgmpSnoopings()[i]));
						jsonObject.put("immediateLeaves", Boolean.valueOf(getDataSource().getImmediateLeaves()[i]));
						jsonObject.put("delayLeaveQueryIntervals", getDataSource().getDelayLeaveQueryIntervals()[i]);
						jsonObject.put("delayLeaveQueryCounts", getDataSource().getDelayLeaveQueryCounts()[i]);
						jsonObject.put("routerPortAginTimes", getDataSource().getRouterPortAginTimes()[i]);
						jsonObject.put("robustnessCounts", getDataSource().getRobustnessCounts()[i]);
						jsonObject.put("igmpVlanIds", ("".equals(getDataSource().getIgmpVlanIds()[i]) ||
										getDataSource().getIgmpVlanIds()[i] == null) ? "" : getDataSource().getIgmpVlanIds()[i]
								);
						jsonArray.put(jsonObject);
					}

					ahIgmpDtDatas = jsonArray.toString();
				}else{
					ahIgmpDtDatas = "";
				}
			} catch (JSONException e) {
				log.error("prepareForIgmpError", e);
			}

		}

		private void prepareAhIgmpTableData() throws JSONException{
			if (getDataSource() == null) {
				ahIgmpDtDatas = "";
				return ;
			}
			if(getDataSource().getDeviceInfo().isSptEthernetMore_24()){
				JSONArray jsonArray = new JSONArray();
				List<IgmpPolicy> list = new ArrayList<IgmpPolicy>(getDataSource().getIgmpPolicys());
				Collections.sort(list);
				for(IgmpPolicy igmp : list){
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("igmpSnoopings", igmp.isIgmpSnooping());
					jsonObject.put("immediateLeaves", igmp.isImmediateLeave());
					jsonObject.put("delayLeaveQueryIntervals", igmp.getDelayLeaveQueryInterval());
					jsonObject.put("delayLeaveQueryCounts", igmp.getDelayLeaveQueryCount());
					jsonObject.put("routerPortAginTimes", igmp.getRouterPortAginTime());
					jsonObject.put("robustnessCounts", igmp.getRobustnessCount());
					jsonObject.put("igmpVlanIds", (igmp.getVlanId() == null || igmp.getVlanId() == 0) ? "" : igmp.getVlanId());
					jsonArray.put(jsonObject);
				}

				ahIgmpDtDatas = jsonArray.toString();
			}

		}

		private void removeExistIgmpPolicy(HiveAp config){
			if(config != null){
				try {
					QueryUtil.removeBos(IgmpPolicy.class, new FilterParams("hive_ap_id = :s1",new Object[]{config}));
				} catch (Exception e) {
					log.error("remove exist igmp policy failure.");
				}
			}
		}

		public String getEnabledOverrideIgmpMode(){
			if(getDataSource().isOverrideIgmpSnooping()){
				return "";
			}else{
				return "none";
			}
		}
		//for switch multicast group settings
		private boolean checkMulticastGroupVlanIDandIp() throws Exception{
			if(!getDataSource().isOverrideIgmpSnooping()){
					return true;
				}
			if(!checkDupltiMulticastGroupVlanIDandIp()){
				jsonObject = new JSONObject();
				jsonObject.put("resultStatus", false);
				jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
				return false;
			}
			
			if(!checkMulticastGroupWanInterface()){
				jsonObject = new JSONObject();
				jsonObject.put("resultStatus", false);
				jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
				return false;
			}
			return true;
		}

		private boolean checkDupltiMulticastGroupVlanIDandIp(){
			List<Integer> list = new ArrayList<>();
			if(multicastGroupVlanIds != null && multicastGroupVlanIds.length !=0){
				if(multicastGroupVlanIds.length >1024){
					addActionError(MgrUtil.getUserMessage("error.objectReachLimit"));
					return false;
				}
				for(int i=0;i<multicastGroupVlanIds.length;i++){
					String vlan = multicastGroupVlanIds[i];
					String ip = multicastGroupIpAddresses[i];
					for(int j=i+1;j<multicastGroupVlanIds.length;j++){
						if((vlan != null && multicastGroupVlanIds[j] != null && vlan.equals(multicastGroupVlanIds[j]) &&
								ip != null && multicastGroupIpAddresses[j] != null && ip.equals(multicastGroupIpAddresses[j]))){
							list.add(i+1);
							break;
						}
					}
				}
			}

			if(list.size() > 1){
				addActionError(MgrUtil.getUserMessage("config.switchSettings.igmp.multicastgroup.vlanid.ipaddress.more.exist", list.toString()));
				return false;
			} else if(list.size() == 1){
				addActionError(MgrUtil.getUserMessage("config.switchSettings.igmp.multicastgroup.vlanid.ipaddress.one.exist", list.toString()));
				return false;
			}

		 return true;
	   }

		private boolean checkMulticastGroupWanInterface(){
			   if(getDataSource() == null){
				   return true;
			   }
			   ConfigTemplate config = getDataSource().getConfigTemplate();
			   if (config != null
						&& getDataSource().getDeviceInfo().isSptEthernetMore_24()) {
				   StringBuffer sbf = new StringBuffer();
				   List<Integer> wanGth = new ArrayList<Integer>();
				   List<Integer> wanSfp = new ArrayList<Integer>();
					PortGroupProfile portGroup = getDataSource().getPortGroup();
					if(portGroup != null && portGroup.getBasicProfiles() != null){
						for(PortBasicProfile baseProfile : portGroup.getBasicProfiles()){
							if(!baseProfile.isEnabledlinkAggregation() && baseProfile.getAccessProfile() != null &&
									baseProfile.getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_WAN){
								String[] eths = baseProfile.getETHs();
								String[] sfps = baseProfile.getSFPs();
								if(eths != null && eths.length > 0){
									for(int i=0; i<eths.length; i++){
										int gth = (int)DeviceInfType.Gigabit.getFinalValue(Integer.parseInt(eths[i]), getDataSource().getHiveApModel());
										if(!wanGth.contains(gth)){
											wanGth.add(gth);
										}
									}
								}
								if(sfps != null && sfps.length > 0){
									for(int i=0; i<sfps.length; i++){
										int sfp = (int)DeviceInfType.SFP.getFinalValue(Integer.parseInt(sfps[i]), getDataSource().getHiveApModel());
										if(!wanSfp.contains(sfp)){
											wanSfp.add(sfp);
										}
									}
								}
							}
						}
					}
				   if((!wanGth.isEmpty() || !wanSfp.isEmpty()) && multicastGroupInterfaces != null){
					    List<CheckItem> items = allPortsList();
					   for(int i=0; i<multicastGroupInterfaces.length; i++){
						   String[] interfaces =  multicastGroupInterfaces[i].split(",");
						   for(String s: interfaces){
							   if(null != items){
									for(CheckItem item : items){
										if(Long.parseLong(s) == item.getId()){
												if(wanGth.contains(Integer.parseInt(s))){
													sbf.append(MgrUtil.getEnumString("enum.switch.interface." + Integer.parseInt(s)));
													sbf.append(",");
													break;
												}
												if(wanSfp.contains(Integer.parseInt(s))){
													sbf.append(MgrUtil.getEnumString("enum.switch.interface." + Integer.parseInt(s)));
													sbf.append(",");
													break;
												}
											}
										}
									}
								}
						   }
					   }
				   if(!sbf.toString().equals("")){
					   String[] strs = sbf.toString().substring(0, sbf.toString().length()-1).split(",");
					   if(strs.length > 1){
						   addActionError(MgrUtil.getUserMessage("config.switchSettings.igmp.multicastgroup.interface.wans.exist", sbf.toString().substring(0, sbf.toString().length()-1)));
						   return false;
					   }else{
						   addActionError(MgrUtil.getUserMessage("config.switchSettings.igmp.multicastgroup.interface.wan.exist", sbf.toString().substring(0, sbf.toString().length()-1)));
						   return false;
					   }
				   }
			   }

			   return true;
		   }

			private void prepareSaveMulticastGroup(){
				if (multicastGroupVlanIds!=null){
					Set<MulticastGroup> tempIgmpSet = new HashSet<MulticastGroup>();
					List<CheckItem> items = allPortsList();
					for(int i =0;i<multicastGroupVlanIds.length;i++){
						if(null != multicastGroupVlanIds[i] && !"".equals(multicastGroupVlanIds[i])){
							MulticastGroup mg = new MulticastGroup();
							mg.setVlanId(multicastGroupVlanIds[i].equalsIgnoreCase("") ? null : Integer.parseInt(multicastGroupVlanIds[i]));
							mg.setIpAddress(multicastGroupIpAddresses[i]);
							mg.setHiveAp(getDataSource());
							mg.setOwner(getDomain());
							String[] interfaces =  multicastGroupInterfaces[i].split(",");
							mg.setInterfaces(getMgInterface(mg,interfaces,items));
							tempIgmpSet.add(mg);
						}
					}
					getDataSource().setMulticastGroups(tempIgmpSet);
				}else{
					getDataSource().setMulticastGroups(null);
				}
			}

			private Set<MulticastGroupInterface> getMgInterface(MulticastGroup mg, String[] interfaces, List<CheckItem> items){
				Set<MulticastGroupInterface> set = new HashSet<MulticastGroupInterface>();
				for(String s : interfaces){
					MulticastGroupInterface mgi = new MulticastGroupInterface();
					if(null != items){
						for(CheckItem item : items){
							if(Long.parseLong(s) == item.getId()){
								int finalVal = DeviceInfType.getInstance(Short.parseShort(s), getDataSource().getHiveApModel()).getIndex();

								if( Short.parseShort(s) < 1000){
									mgi.setInterfaceType(MulticastGroup.INTERFACE_TYPE_ETH);
									mgi.setInterfacePort(finalVal);
								}else if(Short.parseShort(s) > 1000 && Short.parseShort(s) < 2000){
									mgi.setInterfaceType(MulticastGroup.INTERFACE_TYPE_SFP);
									mgi.setInterfacePort(finalVal);
								}else if(Short.parseShort(s) >2000){
									mgi.setInterfaceType(MulticastGroup.INTERFACE_TYPE_PORTCHANNEL);
									mgi.setInterfacePort(finalVal);
								}

								mgi.setOwner(getDomain());
								mgi.setMulticastGroup(mg);
								break;
							}
						}
						set.add(mgi);
					}
				}
				return set;
			}

			private void prepareForMulticastGroupError(){
					try {
						prepareAhDaTableTransientDataForMulticastGroup();
						prepareAhDataTableDataAfterEditForMulticastGroup();
					} catch (JSONException e) {
						log.error("prepareForMulticastGroupError", e);
					}

				}

			private void prepareAhDaTableTransientDataForMulticastGroup(){
					if(null != getDataSource() && null != getDataSource().getMulticastGroups()){
						getDataSource().setMulticastGroupVlanIds(multicastGroupVlanIds);
						getDataSource().setMulticastGroupIpAddresses(multicastGroupIpAddresses);
						getDataSource().setMulticastGroupInterfaces(multicastGroupInterfaces);
					}
				}

			private void prepareAhDataTableDataAfterEditForMulticastGroup() throws JSONException{
					if (getDataSource() == null
							|| getDataSource().getMulticastGroups() == null ) {
						ahMulticastGroupDtDatas = "";
						return ;
					}

					if(null != getDataSource().getMulticastGroupVlanIds()){
						jsonArray = new JSONArray();
						for(int i =0;i<getDataSource().getMulticastGroupVlanIds().length;i++){
							jsonObject = new JSONObject();
							jsonObject.put("multicastGroupVlanIds", getDataSource().getMulticastGroupVlanIds()[i]);
							jsonObject.put("multicastGroupIpAddresses", getDataSource().getMulticastGroupIpAddresses()[i]);
							jsonObject.put("multicastGroupInterfaces", getDataSource().getMulticastGroupInterfaces()[i]);
							jsonArray.put(jsonObject);
						}

						ahMulticastGroupDtDatas = jsonArray.toString();
					}else{
						ahMulticastGroupDtDatas = "";
					}

				}

		private void preparePortChannel(){
			if(!getDataSource().getDeviceInfo().isSptEthernetMore_24()){
				return;
			}
			PortGroupProfile portGroup = getDataSource().getPortGroup();

			//get all port channel from hiveap DeviceInterfaces tables.
			Map<Short, DeviceInterface> portChannels = new HashMap<Short, DeviceInterface>();
			Iterator<Long> keyItes = getDataSource().getDeviceInterfaces().keySet().iterator();
			while(keyItes.hasNext()){
				Long key = keyItes.next();
				DeviceInterface dInf = getDataSource().getDeviceInterfaces().get(key);
				if(DeviceInfType.getInstance(dInf.getDeviceIfType(), getDataSource().getHiveApModel()).getDeviceInfType() != DeviceInfType.PortChannel){
					continue;
				}
				portChannels.put(dInf.getDeviceIfType(), dInf);
			}
			for(Short rmKey : portChannels.keySet()){
				getDataSource().getDeviceInterfaces().remove(Long.valueOf(rmKey.toString()));
			}

			//get all port channel from port group.
			Map<Short, DeviceInterface> currentPortChannels = new HashMap<Short, DeviceInterface>();
			if(portGroup != null && portGroup.getBasicProfiles() != null){
				for(PortBasicProfile baseProfile : portGroup.getBasicProfiles()){
					if(!baseProfile.isEnabledlinkAggregation()){
						continue;
					}
					short protChannel = DeviceInfType.PortChannel.getFinalValue(baseProfile.getPortChannel(), getDataSource().getHiveApModel());
					DeviceInterface dInf = new DeviceInterface();
					dInf.setDeviceIfType(protChannel);
					dInf.setHiveApModel(this.getDataSource().getHiveApModel());
					//init members
					dInf.initMembers(baseProfile);
					
					currentPortChannels.put(protChannel, dInf);
				}
			}
			
			//remove members and parent
			if(getDataSource().getDeviceInterfaces() != null){
				for(DeviceInterface dInf : getDataSource().getDeviceInterfaces().values()){
					if(dInf.getMembers() != null){
						dInf.getMembers().clear();
					}
					dInf.setParent(DeviceInterface.DEFAULT_PARENT_VALUE);
				}
			}

			//remove port channel not exists.
			Iterator<Short> keyShortItes = currentPortChannels.keySet().iterator();
			while(keyShortItes.hasNext()){
				Short key = keyShortItes.next();
				if(portChannels.containsKey(key)){
					portChannels.get(key).setMembers(currentPortChannels.get(key).getMembers());
					currentPortChannels.put(key, portChannels.get(key));
				}
			}

			//merge to deviceInterfaces
			for(Short key : currentPortChannels.keySet()){
				getDataSource().getDeviceInterfaces().put(Long.valueOf(key.toString()), currentPortChannels.get(key));
			}

			//set port channel to member interface
			for(DeviceInterface dInf : currentPortChannels.values()){
				short parent = dInf.getDeviceIfType();
				for(Short keyShort : dInf.getMembers()){
					getDataSource().getDeviceInterfaces().get(Long.valueOf(keyShort.toString())).setParent(parent);
				}
			}
		}

		private void prepareAhMulticastGroupTableData() throws JSONException{
			if (getDataSource() == null
					|| getDataSource().getMulticastGroups() == null ) {
				ahMulticastGroupDtDatas = "";
				return ;
			}
			List<CheckItem> items = allPortsList();
			if(getDataSource().getDeviceInfo().isSptEthernetMore_24()){
				JSONArray jsonArray = new JSONArray();
				List<MulticastGroup> list = new ArrayList<MulticastGroup>(getDataSource().getMulticastGroups());
				Collections.sort(list);
				for(MulticastGroup mg : list){
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("multicastGroupVlanIds", mg.getVlanId());
					jsonObject.put("multicastGroupIpAddresses", mg.getIpAddress());
					jsonObject.put("multicastGroupInterfaces", getInterfaces(mg,items));
					jsonArray.put(jsonObject);
				}

				ahMulticastGroupDtDatas = jsonArray.toString();
			}

		}
		
	private void prepareCopyHiveApModeToDeviceInterface(){
		for(DeviceInterface dinf : this.getDataSource().getDeviceInterfaces().values()){
			if(dinf != null){
				dinf.setHiveApModel(this.getDataSource().getHiveApModel());
			}
		}
	}

	private String getInterfaces(MulticastGroup mg,List<CheckItem> items ){
			StringBuffer sf = new StringBuffer("");
			if(null != mg){
				for(MulticastGroupInterface mgi : mg.getInterfaces()){
					if(null != mgi){
						if(mgi.getInterfaceType() == MulticastGroup.INTERFACE_TYPE_ETH){
							if(null != items){
								for(CheckItem item : items){
									if(item.getId().equals((long)DeviceInfType.Gigabit.getFinalValue(mgi.getInterfacePort(), getDataSource().getHiveApModel()))){
										sf.append(item.getId());
										sf.append(",");
										break;
									}
								}
							}
						}else if(mgi.getInterfaceType() == MulticastGroup.INTERFACE_TYPE_SFP){
							if(null != items){
								for(CheckItem item : items){
									if(item.getId().equals((long)DeviceInfType.SFP.getFinalValue(mgi.getInterfacePort(), getDataSource().getHiveApModel()))){
										sf.append(item.getId());
										sf.append(",");
										break;
									}
								}
							}
						}else if(mgi.getInterfaceType() == MulticastGroup.INTERFACE_TYPE_PORTCHANNEL){
							if(null != items){
								for(CheckItem item : items){
									if(item.getId().equals((long)DeviceInfType.PortChannel.getFinalValue(mgi.getInterfacePort(), getDataSource().getHiveApModel()))){
										sf.append(item.getId());
										sf.append(",");
										break;
									}
								}
							}
						}
					}
				}
			}
			return sf.toString().equals("") ? "" : sf.toString().substring(0, sf.toString().length()-1);
		}

	private void removeExistMulticastGroup(HiveAp config){
			if(config != null){
				try {
					for(MulticastGroup mg : config.getMulticastGroups()){
						if(null != mg){
							QueryUtil.removeBos(MulticastGroupInterface.class, new FilterParams("multicastGroupId = :s1",new Object[]{mg}));
							QueryUtil.removeBos(MulticastGroup.class, new FilterParams("HIVE_AP_ID = :s1",new Object[]{config}));
						}
					}

				} catch (Exception e) {
					log.error("remove exist multicast group failure.");
				}
			}
		}

	private void prepareAhMulticastGroupDataTableColumnDefs(HiveAp hiveAp) throws JSONException{
		List<AhDataTableColumn> ahDataTableColumns = new ArrayList<AhDataTableColumn>();
		AhDataTableColumn column = new AhDataTableColumn();
		column.setMark("multicastGroupInterfaces");
		column.setOptions(allPortsList());
		column.setDefaultValue(null);
		column.setChangeValue(allPortsList());
		ahDataTableColumns.add(column);

		ahMulticastGroupDtClumnDefs = generateAhDataTableColumnJsonString(ahDataTableColumns);
	}

	private String igmpEditInfo;
	private String ahIgmpDtClumnDefs;
	private String ahIgmpDtDatas;

	private String[] igmpVlanIds;
	private String[] igmpSnoopings;
	private String[] immediateLeaves;
	private String[] delayLeaveQueryIntervals;
	private String[] delayLeaveQueryCounts;
	private String[] routerPortAginTimes;
	private String[] robustnessCounts;

	private String multicastGroupEditInfo;
	private String ahMulticastGroupDtClumnDefs;
	private String ahMulticastGroupDtDatas;

	private String[] multicastGroupVlanIds;
	private String[] multicastGroupIpAddresses;
	private String[] multicastGroupInterfaces;


	public String getIgmpEditInfo() {
		return igmpEditInfo;
	}

	public void setIgmpEditInfo(String igmpEditInfo) {
		this.igmpEditInfo = igmpEditInfo;
	}

	public String getMulticastGroupEditInfo() {
		return multicastGroupEditInfo;
	}

	public void setMulticastGroupEditInfo(String multicastGroupEditInfo) {
		this.multicastGroupEditInfo = multicastGroupEditInfo;
	}

	public String getAhMulticastGroupDtClumnDefs() {
		return ahMulticastGroupDtClumnDefs;
	}

	public void setAhMulticastGroupDtClumnDefs(String ahMulticastGroupDtClumnDefs) {
		this.ahMulticastGroupDtClumnDefs = ahMulticastGroupDtClumnDefs;
	}

	public String getAhMulticastGroupDtDatas() {
		return ahMulticastGroupDtDatas;
	}

	public void setAhMulticastGroupDtDatas(String ahMulticastGroupDtDatas) {
		this.ahMulticastGroupDtDatas = ahMulticastGroupDtDatas;
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

	public String getAhIgmpDtClumnDefs() {
		return ahIgmpDtClumnDefs;
	}

	public void setAhIgmpDtClumnDefs(String ahIgmpDtClumnDefs) {
		this.ahIgmpDtClumnDefs = ahIgmpDtClumnDefs;
	}

	public String getAhIgmpDtDatas() {
		return ahIgmpDtDatas;
	}

	public void setAhIgmpDtDatas(String ahIgmpDtDatas) {
		this.ahIgmpDtDatas = ahIgmpDtDatas;
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

	public String getFdb_ahDtClumnDefs() {
		return fdb_ahDtClumnDefs;
	}

	public void setFdb_ahDtClumnDefs(String fdb_ahDtClumnDefs) {
		this.fdb_ahDtClumnDefs = fdb_ahDtClumnDefs;
	}

	public String getFdb_ahDtDatas() {
		return fdb_ahDtDatas;
	}

	public void setFdb_ahDtDatas(String fdb_ahDtDatas) {
		this.fdb_ahDtDatas = fdb_ahDtDatas;
	}

	public String getFdb_editInfo() {
		return fdb_editInfo;
	}

	public void setFdb_editInfo(String fdb_editInfo) {
		this.fdb_editInfo = fdb_editInfo;
	}

	public String[] getFdb_vlans() {
		return fdb_vlans;
	}

	public void setFdb_vlans(String[] fdb_vlans) {
		this.fdb_vlans = fdb_vlans;
	}

	public String[] getFdb_interfaces() {
		return fdb_interfaces;
	}

	public void setFdb_interfaces(String[] fdb_interfaces) {
		this.fdb_interfaces = fdb_interfaces;
	}

	public String[] getFdb_macaddress() {
		return fdb_macaddress;
	}

	public void setFdb_macaddress(String[] fdb_macaddress) {
		this.fdb_macaddress = fdb_macaddress;
	}

	public List<DeviceInterface> getDInterfaces() {
		return dInterfaces;
	}

	public void setDInterfaces(List<DeviceInterface> dInterfaces) {
		this.dInterfaces = dInterfaces;
	}

	public short[] getAdminState() {
		return adminState;
	}

	public void setAdminState(short[] adminState) {
		this.adminState = adminState;
	}

//	public int[] getNativeVlan() {
//		return nativeVlan;
//	}
//
//	public void setNativeVlan(int[] nativeVlan) {
//		this.nativeVlan = nativeVlan;
//	}
//
//	public String[] getAllowedVlan() {
//		return allowedVlan;
//	}
//
//	public void setAllowedVlan(String[] allowedVlan) {
//		this.allowedVlan = allowedVlan;
//	}

	public short[] getDuplex() {
		return duplex;
	}

	public void setDuplex(short[] duplex) {
		this.duplex = duplex;
	}

	public boolean[] getAutoMdix() {
		return autoMdix;
	}

	public void setAutoMdix(boolean[] autoMdix) {
		this.autoMdix = autoMdix;
	}

//	public int[] getMtu() {
//		return mtu;
//	}
//
//	public void setMtu(int[] mtu) {
//		this.mtu = mtu;
//	}

	public int[] getDebounceTimer() {
		return debounceTimer;
	}

	public void setDebounceTimer(int[] debounceTimer) {
		this.debounceTimer = debounceTimer;
	}

	public boolean[] getLldpTransmit() {
		return lldpTransmit;
	}

	public void setLldpTransmit(boolean[] lldpTransmit) {
		this.lldpTransmit = lldpTransmit;
	}

	public boolean[] getLldpReceive() {
		return lldpReceive;
	}

	public void setLldpReceive(boolean[] lldpReceive) {
		this.lldpReceive = lldpReceive;
	}

	public boolean[] getCdpReceive() {
		return cdpReceive;
	}

	public void setCdpReceive(boolean[] cdpReceive) {
		this.cdpReceive = cdpReceive;
	}

	public short[] getSpeed() {
		return speed;
	}

	public void setSpeed(short[] speed) {
		this.speed = speed;
	}

	public short[] getFlowControlStatus() {
		return flowControlStatus;
	}

	public void setFlowControlStatus(short[] flowControlStatus) {
		this.flowControlStatus = flowControlStatus;
	}

	public short[] getDeviceIfType() {
		return deviceIfType;
	}

	public void setDeviceIfType(short[] deviceIfType) {
		this.deviceIfType = deviceIfType;
	}

	public List<HiveAp> getSwitchList() {
		return switchList;
	}

	public void setSwitchList(List<HiveAp> switchList) {
		this.switchList = switchList;
	}

	// STP Settings

	private List<InterfaceStpSettings> allPortLevelSettings;
	private List<InterfaceMstpSettings> allPortPrioritySettings;
	private List<DeviceMstpInstancePriority> allInstancePrioritySettings;
	private short[] interfaceNums;
	private String[] devicePortNames;
	private String[] hidden_bpduModes;
	private boolean[] edgePorts;
	private String[] device_path_cost;
	private boolean[] enableStpStatus;
	private short[] device_priority;
	private String mstpi_ahDtClumnDefs;
	private String mstpi_ahDtDatas;
	private Short[] mstp_interface;
	private Short[] mstp_instance;
	private String[] mstp_path_cost;
	private short[] mstp_priority;
	private short[] device_instance_priority;
	private short[] device_instance_id;

	public short[] getDevice_instance_id() {
		return device_instance_id;
	}

	public void setDevice_instance_id(short[] device_instance_id) {
		this.device_instance_id = device_instance_id;
	}

	public short[] getDevice_instance_priority() {
		return device_instance_priority;
	}

	public void setDevice_instance_priority(short[] device_instance_priority) {
		this.device_instance_priority = device_instance_priority;
	}

	public List<InterfaceStpSettings> getAllPortLevelSettings() {
		return allPortLevelSettings;
	}

	public void setAllPortLevelSettings(
			List<InterfaceStpSettings> allPortLevelSettings) {
		this.allPortLevelSettings = allPortLevelSettings;
	}

	public List<InterfaceMstpSettings> getAllPortPrioritySettings() {
		return allPortPrioritySettings;
	}

	public void setAllPortPrioritySettings(
			List<InterfaceMstpSettings> allPortPrioritySettings) {
		this.allPortPrioritySettings = allPortPrioritySettings;
	}

	public List<DeviceMstpInstancePriority> getAllInstancePrioritySettings() {
		return allInstancePrioritySettings;
	}

	public void setAllInstancePrioritySettings(
			List<DeviceMstpInstancePriority> allInstancePrioritySettings) {
		this.allInstancePrioritySettings = allInstancePrioritySettings;
	}

	public String getShowDeviceSettingsDiv() {
		return "";
	}

	public String getHideDeviceSettingsDiv() {
		return "none";
	}

	public String getShowSwitchSettingsDiv() {
		return "";
	}

	public String getHideSwitchSettingsDiv() {
		return "none";
	}

	public EnumItem[] getStpModeStp() {
		return new EnumItem[] { new EnumItem(StpSettings.STP_MODE_STP,
				getText("config.switchSettings.STPmode.stp")) };
	}

	public EnumItem[] getStpModeRstp() {
		return new EnumItem[] { new EnumItem(StpSettings.STP_MODE_RSTP,
				getText("config.switchSettings.STPmode.rstp")) };
	}

	public EnumItem[] getStpModeMstp() {
		return new EnumItem[] { new EnumItem(StpSettings.STP_MODE_MSTP,
				getText("config.switchSettings.STPmode.mstp")) };
	}

	public String getEnabledStpMode() {
		if (getDataSource().getDeviceInfo().isSptEthernetMore_24()
				&& getDataSource().getConfigTemplate().getSwitchSettings()
						.getStpSettings().isEnableStp()) {
			return "";
		} else {
			return "none";
		}
	}

	public String getShowPortLevelSettingsDiv() {
		return "";
	}

	public String getHidePortLevelSettingsDiv() {
		return "none";
	}

	public String getShowMstPortPrioritySettingsDiv() {
		if (getDataSource().getDeviceInfo().isSptEthernetMore_24()
				&& getDataSource().getConfigTemplate() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().getStp_mode() == StpSettings.STP_MODE_MSTP
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().getMstpRegion() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().getMstpRegion().getMstpRegionPriorityList() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().getMstpRegion().getMstpRegionPriorityList().size() > 0) {
			
			return "";
		} else {
			return "none";
		}
	}

	public String getHideMstPortPrioritySettingsDiv() {
		return "none";
	}

	protected List<CheckItem> allPortsList() {
		List<CheckItem> list = new ArrayList<CheckItem>();
		int ethCounts = getDataSource().getDeviceInfo().getIntegerValue(
				DeviceInfo.SPT_ETHERNET_COUNTS);
		int sfpCounts = getDataSource().getDeviceInfo().getIntegerValue(
				DeviceInfo.SPT_SFP_COUNTS);
		int interType;
		if (ethCounts > 1) {
			interType = AhInterface.DEVICE_IF_TYPE_ETH1;
			while (ethCounts > 0) {
				if(!isPortChannelMemberPort((short)interType)){
					list.add(new CheckItem((long) interType, MgrUtil
							.getEnumString("enum.switch.interface." + interType)));
				}

				ethCounts--;
				interType++;
			}
		}
		if (sfpCounts > 0) {
			for(int i=0; i<sfpCounts; i++) {
				interType = DeviceInfType.SFP.getFinalValue(i+1, getDataSource().getHiveApModel());
				if(!isPortChannelMemberPort((short)interType)){
					list.add(new CheckItem((long) interType, MgrUtil
							.getEnumString("enum.switch.interface." + interType)));
				}
				interType++;
			}
		}
		// add port channel
		PortGroupProfile portGroup = getDataSource().getPortGroup();
		if(portGroup != null && portGroup.getBasicProfiles() != null){
			for(PortBasicProfile baseProfile : portGroup.getBasicProfiles()){
				if(!baseProfile.isEnabledlinkAggregation()){
					continue;
				}
				short protChannel = DeviceInfType.PortChannel.getFinalValue(baseProfile.getPortChannel(), getDataSource().getHiveApModel());

				list.add(new CheckItem((long) protChannel, MgrUtil
						.getEnumString("enum.switch.interface." + protChannel)));

			}
		}

		Collections.sort(list, new Comparator<CheckItem>() {
			public int compare(CheckItem o1, CheckItem o2) {
				return (int) (o1.getId() - o2.getId());
			}
		});


		return list;
	}

	public EnumItem[] getBpduModeItem() {
		return new EnumItem[] {
				new EnumItem(
						InterfaceStpSettings.BPDU_GUARD_MODE,
						getText("config.switchSettings.deviceSettings.allports.bpdu.guard")),
				new EnumItem(
						InterfaceStpSettings.BPDU_FILTER_MODE,
						getText("config.switchSettings.deviceSettings.allports.bpdu.filter")),
				new EnumItem(
						InterfaceStpSettings.BPDU_DEFAULT_MODE,
						getText("config.switchSettings.deviceSettings.allports.bpdu.default")) };
	}
	
	public EnumItem[] getHelloTimeItem() {
		return new EnumItem[] {
				new EnumItem(DeviceStpSettings.DEFAULT_HELLO_TIME,Short.toString(DeviceStpSettings.DEFAULT_HELLO_TIME))
		};
	}
	
	public EnumItem[] getForwardingDelayItem() {
		return new EnumItem[] {
				new EnumItem(1,"7"),
				new EnumItem(2,"9"),
				new EnumItem(3,"10"),
				new EnumItem(4,"12"),
				new EnumItem(5,"13"),
				new EnumItem(6, Short.toString(DeviceStpSettings.DEFAULT_FORWARD_DELAY))
		};
	}
	
	public EnumItem[] getMaxAgeItem(){
		return new EnumItem[] {
				new EnumItem(1,"10"),
				new EnumItem(2,"12"),
				new EnumItem(3,"14"),
				new EnumItem(4,"16"),
				new EnumItem(5,"18"),
				new EnumItem(6, Short.toString(DeviceStpSettings.DEFAULT_MAX_AGE))
		};
	}

	public EnumItem[] getForceVersionItem(){
		EnumItem[] forceVersionItem = new EnumItem[]{};
		if(getDataSource() != null 
				&& getDataSource().getConfigTemplate() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().isEnableStp()){
			short stpMode = getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().getStp_mode();
			if(stpMode == StpSettings.STP_MODE_RSTP){
				forceVersionItem = new EnumItem[]{new EnumItem(0,"STP Only"),new EnumItem(StpSettings.STP_MODE_RSTP,"RSTP Only")};
			}
			if(stpMode == StpSettings.STP_MODE_MSTP){
				forceVersionItem = new EnumItem[]{new EnumItem(0,"STP Only"),new EnumItem(StpSettings.STP_MODE_RSTP,"RSTP Only"),new EnumItem(StpSettings.STP_MODE_MSTP,"MSTP Only")};
			}
		}
		return forceVersionItem;
	}
	
	private void prepareDeviceStpSettings() {
		if(getDataSource().getDeviceInfo().isSptEthernetMore_24()){
			if(getDataSource().getConfigTemplate() != null
					&& getDataSource().getConfigTemplate().getSwitchSettings() != null
					&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings() != null
					&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().isEnableStp()){
				StpSettings stpSettings = getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings();
				if(getDataSource().getDeviceStpSettings() == null){
					DeviceStpSettings deviceStpSettings = new DeviceStpSettings();
					deviceStpSettings.setStp_mode(stpSettings.getStp_mode());
					deviceStpSettings.setEnableStp(stpSettings.isEnableStp());
					getDataSource().setDeviceStpSettings(deviceStpSettings);
				}else{
					DeviceStpSettings deviceStpSettings = getDataSource().getDeviceStpSettings();
					if(!stpSettings.isEnableStp()){
						deviceStpSettings.setOverrideStp(stpSettings.isEnableStp());
					}
					deviceStpSettings.setEnableStp(stpSettings.isEnableStp());
					deviceStpSettings.setStp_mode(stpSettings.getStp_mode());
					getDataSource().setDeviceStpSettings(deviceStpSettings);
				}
				if(stpSettings.isEnableStp() && stpSettings.getStp_mode() == StpSettings.STP_MODE_MSTP){
					mstpEnable = true;
				}
				initAllPortLevelSettings();
				initAllInstancePrioritySettings();
			}
		}
	}

	private void updateDeviceStpSettings() {
		if(getDataSource() != null && getDataSource().getDeviceStpSettings() != null){
			getDataSource().getDeviceStpSettings().setOwner(getDomain());
		}
		if(getDataSource() != null &&  getDataSource().getDeviceInfo().isSptEthernetMore_24()
				&& getDataSource().getConfigTemplate() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().isEnableStp()
				&& getDataSource().getDeviceStpSettings() != null
				&& getDataSource().getDeviceStpSettings().isOverrideStp()
				&& getDataSource().getDeviceStpSettings().isEnableStp()){
			updateAllPortLevelSettings();
			if(getDataSource().getDeviceStpSettings().getStp_mode() == StpSettings.STP_MODE_MSTP){
				updateAllInterfaceMstpInstancePriority();
				updateAllInstancePrioritySettings();
			}
		}
	}

	public String getShowHideStpCheckbox(){
		if(getDataSource().getDeviceInfo().isSptEthernetMore_24()
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings() != null){
			if(getDataSource().getDeviceStpSettings().isOverrideStp()
					&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().isEnableStp()){
				return "";
			}else {
				return "none";
			}
		}
		return "none";
	}

	public String[] getHidden_bpduModes() {
		return hidden_bpduModes;
	}

	public void setHidden_bpduModes(String[] hidden_bpduModes) {
		this.hidden_bpduModes = hidden_bpduModes;
	}

	public short[] getDevice_priority() {
		return device_priority;
	}

	public void setDevice_priority(short[] device_priority) {
		this.device_priority = device_priority;
	}

	public String[] getDevice_path_cost() {
		return device_path_cost;
	}

	public void setDevice_path_cost(String[] device_path_cost) {
		this.device_path_cost = device_path_cost;
	}

	public boolean[] getEnableStpStatus() {
		return enableStpStatus;
	}

	public void setEnableStpStatus(boolean[] enableStpStatus) {
		this.enableStpStatus = enableStpStatus;
	}

	public boolean[] getEdgePorts() {
		return edgePorts;
	}

	public void setEdgePorts(boolean[] edgePorts) {
		this.edgePorts = edgePorts;
	}

	public short[] getInterfaceNums() {
		return interfaceNums;
	}

	public void setInterfaceNums(short[] interfaceNums) {
		this.interfaceNums = interfaceNums;
	}

	public String[] getDevicePortNames() {
		return devicePortNames;
	}

	public void setDevicePortNames(String[] devicePortNames) {
		this.devicePortNames = devicePortNames;
	}

	public Short[] getMstp_interface() {
		return mstp_interface;
	}

	public void setMstp_interface(Short[] mstp_interface) {
		this.mstp_interface = mstp_interface;
	}

	public Short[] getMstp_instance() {
		return mstp_instance;
	}

	public void setMstp_instance(Short[] mstp_instance) {
		this.mstp_instance = mstp_instance;
	}

	public String[] getMstp_path_cost() {
		return mstp_path_cost;
	}

	public void setMstp_path_cost(String[] mstp_path_cost) {
		this.mstp_path_cost = mstp_path_cost;
	}

	public short[] getMstp_priority() {
		return mstp_priority;
	}

	public void setMstp_priority(short[] mstp_priority) {
		this.mstp_priority = mstp_priority;
	}

	public String getMstpi_ahDtClumnDefs() {
		return mstpi_ahDtClumnDefs;
	}

	public void setMstpi_ahDtClumnDefs(String mstpi_ahDtClumnDefs) {
		this.mstpi_ahDtClumnDefs = mstpi_ahDtClumnDefs;
	}

	public String getMstpi_ahDtDatas() {
		return mstpi_ahDtDatas;
	}

	public void setMstpi_ahDtDatas(String mstpi_ahDtDatas) {
		this.mstpi_ahDtDatas = mstpi_ahDtDatas;
	}

	private void prepareMstpInstanceDataTable() throws JSONException {
		if (null != getDataSource() && null != getDataSource().getDeviceStpSettings()
				&& getDataSource().getConfigTemplate() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().getStp_mode() == StpSettings.STP_MODE_MSTP
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().getMstpRegion() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().getMstpRegion().getMstpRegionPriorityList() != null){
			long mstpRegionId = getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().getMstpRegion().getId();
			if (mstpRegionId != 0 && mstpRegionId != -1) {
				List<CheckItem> allPorts = allPortsList();
				List<AhDataTableColumn> ahDataTableColumns = new ArrayList<AhDataTableColumn>();
				MstpRegion region = QueryUtil.findBoById(MstpRegion.class, mstpRegionId, this);
				List<MstpRegionPriority> priority = region.getMstpRegionPriorityList();
				List<CheckItem> mstpRegionPriorityList = prepareMstpRegionPriority(priority);

				for(int i = 0; i < allPorts.size(); i ++){
					if(!this.supportedStp(allPorts.get(i).getId().shortValue())){
						allPorts.remove(i);
						i --;
					}
				}

				if(!priority.isEmpty() || priority.size() > 0){
					AhDataTableColumn column = new AhDataTableColumn();
					column.setMark("mstp_interface");
					column.setOptions(allPorts);
					ahDataTableColumns.add(column);

					column = new AhDataTableColumn();
					column.setMark("mstp_instance");
					column.setOptions(mstpRegionPriorityList);
					ahDataTableColumns.add(column);

					column = new AhDataTableColumn();
					column.setMark("mstp_priority");
					column.setOptions(getMstiPriorityList());
					ahDataTableColumns.add(column);

					mstpi_ahDtClumnDefs = generateAhDataTableColumnJsonString(ahDataTableColumns);
				} else {
					mstpi_ahDtClumnDefs = "";
				}


			} else {
				mstpi_ahDtClumnDefs = "";
			}
		}
	}

	private List<CheckItem> prepareMstpRegionPriority(
			List<MstpRegionPriority> mstpRegionPriorityList) {
		List<CheckItem> list = new ArrayList<CheckItem>();
		for (MstpRegionPriority priority : mstpRegionPriorityList) {
			list.add(new CheckItem((long) priority.getInstance(), Integer
					.toString(priority.getInstance())));
		}
		return list;
	}

	private void prepareMstpInstanceToDataTable() throws JSONException {
		if (getDataSource() != null
				&& getDataSource().getDeviceStpSettings() != null
				&& getDataSource().getConfigTemplate() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings()
						.getStpSettings() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings()
						.getStpSettings().getStp_mode() == StpSettings.STP_MODE_MSTP
				&& getDataSource().getConfigTemplate().getSwitchSettings()
						.getStpSettings().getMstpRegion() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings()
						.getStpSettings().getMstpRegion()
						.getMstpRegionPriorityList() != null) {
			JSONArray jsonArray = new JSONArray();
			Map<Short, List<CheckItem>> map = getPortaChannelMemberPorts();
			allPortPrioritySettings = getDataSource().getDeviceStpSettings()
					.getInterfaceMstpSettings();
			if (!allPortPrioritySettings.isEmpty()) {
				List<MstpRegionPriority> priority = getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().getMstpRegion().getMstpRegionPriorityList();
				Map<Short, Integer> priorityMap = new HashMap<Short, Integer>();
				for (MstpRegionPriority tmpObj : priority){
					priorityMap.put(tmpObj.getInstance(), tmpObj.getPriority());
				}
				
				for(int i = 0; i < allPortPrioritySettings.size(); i ++){
					InterfaceMstpSettings instancePriority = allPortPrioritySettings.get(i);
					if(!priorityMap.keySet().contains(instancePriority.getInstance())){
						allPortPrioritySettings.remove(i);
						i --;
					}
				}
				
				for (InterfaceMstpSettings settings : allPortPrioritySettings) {
					if (settings.getInstance() > 0
							&& settings.getInterfaceNum() > 0) {
						if (isPortChannelMemberPort(settings.getInterfaceNum())) {
							continue;
						}

						if (!this.supportedStp(settings.getInterfaceNum())) {
							continue;
						}
						JSONObject dataObject = new JSONObject();
						dataObject.put("mstp_interface",
								settings.getInterfaceNum());
						dataObject.put("mstp_instance", settings.getInstance());
						dataObject.put("mstp_priority", settings.getTimes());
						dataObject.put("mstp_path_cost",
								settings.getDefaultPathCost());
						if (DeviceInfType.getInstance(settings
								.getInterfaceNum(), getDataSource().getHiveApModel()).getDeviceInfType() != DeviceInfType.PortChannel) {
							jsonArray.put(dataObject);
						} else {
							if (map.keySet().contains(
									settings.getInterfaceNum())) {
								jsonArray.put(dataObject);
							}
						}
					}
				}
			}
			if (jsonArray != null) {
				mstpi_ahDtDatas = jsonArray.toString();
			}
		}else{
			mstpi_ahDtDatas = "";
		}
	}

	private Map<Short,List<CheckItem>> getPortaChannelMemberPorts(){
		Map<Short,List<CheckItem>> mapPortChannel = new HashMap<Short,List<CheckItem>>();

		PortGroupProfile portGroup = getDataSource().getPortGroup();
		if(portGroup != null && portGroup.getBasicProfiles() != null){
			for(PortBasicProfile baseProfile : portGroup.getBasicProfiles()){
				List<CheckItem> list = new ArrayList<CheckItem>();
				if(!baseProfile.isEnabledlinkAggregation()){
					continue;
				}
				short portChannel = DeviceInfType.PortChannel.getFinalValue(baseProfile.getPortChannel(), getDataSource().getHiveApModel());
				String[] eths = baseProfile.getETHs();
				String[] sfps = baseProfile.getSFPs();
				int interType;
				if(eths != null && eths.length > 0){
					for(int i=0; i<eths.length; i++){
						interType = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(eths[i]), getDataSource().getHiveApModel());
						list.add(new CheckItem((long) interType, MgrUtil
								.getEnumString("enum.switch.interface." + interType)));
					}
				}
				if(sfps != null && sfps.length > 0){
					for(int i=0; i<sfps.length; i++){
						interType = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfps[i]), getDataSource().getHiveApModel());
						list.add(new CheckItem((long) interType, MgrUtil
								.getEnumString("enum.switch.interface." + interType)));
					}
				}
				mapPortChannel.put(portChannel, list);
			}
		}
		return mapPortChannel;

	}

	private boolean isPortChannelMemberPort(short interfaceNum){
		PortGroupProfile portGroup = getDataSource().getPortGroup();
		if(portGroup != null && portGroup.getBasicProfiles() != null){
			for(PortBasicProfile baseProfile : portGroup.getBasicProfiles()){
				if(!baseProfile.isEnabledlinkAggregation()){
					continue;
				}
				String[] eths = baseProfile.getETHs();
				String[] sfps = baseProfile.getSFPs();
				int interType;
				if(eths != null && eths.length > 0){
					for(int i=0; i<eths.length; i++){
						interType = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(eths[i]), getDataSource().getHiveApModel());
						if(interType == interfaceNum){
							return true;
						}
					}
				}
				if(sfps != null && sfps.length > 0){
					for(int i=0; i<sfps.length; i++){
						interType = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfps[i]), getDataSource().getHiveApModel());
						if(interType == interfaceNum){
							return true;
						}
					}
				}
			}
		}
		return false;

	}

	public List<EnumItem> getPriorityList (){
		List<EnumItem> list = new ArrayList<EnumItem>();
		short i = DeviceStpSettings.MIN_TIMES;
		while (i < DeviceStpSettings.MAX_TIMES + 1){
			list.add(new EnumItem(i,Integer.toString(DeviceStpSettings.BASE_PRIORITY * i)));
			i ++;
		}
		return  list;
	}

	public List<EnumItem> getDevicePriorityList (){
		List<EnumItem> list = new ArrayList<EnumItem>();
		short i = InterfaceStpSettings.MIN_TIMES;
		while (i < InterfaceStpSettings.MAX_TIMES + 1){
			list.add(new EnumItem(i,Integer.toString(InterfaceStpSettings.BASE_PRIORITY * i)));
			i ++;
		}
		return  list;
	}

	public List<CheckItem> getMstiPriorityList (){
		List<CheckItem> list = new ArrayList<CheckItem>();
		short i = InterfaceMstpSettings.MIN_TIMES;
		while (i < InterfaceMstpSettings.MAX_TIMES + 1){
			list.add(new CheckItem((long)i,Integer.toString(InterfaceMstpSettings.BASE_PRIORITY * i)));
			i ++;
		}
		return  list;
	}
	
	public List<CheckItem> getInstancePriorityList (){
		List<CheckItem> list = new ArrayList<CheckItem>();
		short i = DeviceMstpInstancePriority.MIN_TIMES;
		while (i < DeviceMstpInstancePriority.MAX_TIMES + 1){
			list.add(new CheckItem((long)i,Integer.toString(DeviceMstpInstancePriority.BASE_PRIORITY * i)));
			i ++;
		}
		return  list;
	}

	public List<CheckItem> getNotSupportStpPorts(){
		List<CheckItem> list = new ArrayList<CheckItem>();
		ConfigTemplate config = getDataSource().getConfigTemplate();
		if (config != null
				&& getDataSource().getDeviceInfo().isSptEthernetMore_24()) {
			PortGroupProfile portGroup = getDataSource().getPortGroup();
			if(null == portGroup){
				log.info("bonjourGatewayConfig() PortGroupProfile is null.");
				return list;
			}
			for (PortBasicProfile basicProfile : portGroup.getBasicProfiles()){
				PortAccessProfile accessProfile = basicProfile.getAccessProfile();
				if(accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_WAN
						&& !basicProfile.isEnabledlinkAggregation()){
					String[] eths = basicProfile.getETHs();
					String[] sfps = basicProfile.getSFPs();
//					String[] usbs = basicProfile.getUSBs();
					int interType;
					if(eths != null && eths.length > 0){
						for(int i=0; i<eths.length; i++){
							interType = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(eths[i]), getDataSource().getHiveApModel());
							list.add(new CheckItem((long) interType, MgrUtil
									.getEnumString("enum.switch.interface." + interType)));
						}
					}
					if(sfps != null && sfps.length > 0){
						for(int i=0; i<sfps.length; i++){
							interType = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfps[i]), getDataSource().getHiveApModel());
							list.add(new CheckItem((long) interType, MgrUtil
									.getEnumString("enum.switch.interface." + interType)));
						}
					}
					/*if(usbs != null && usbs.length > 0){
						for(int i=0; i<usbs.length; i++){
							interType = DeviceInfType.USB.getFinalValue(Integer.valueOf(usbs[i]));
							list.add(new CheckItem((long) interType, MgrUtil
									.getEnumString("enum.switch.interface." + interType)));
						}
					}*/
				}else if(accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_WAN
						&& basicProfile.isEnabledlinkAggregation()){
					short protChannel = DeviceInfType.PortChannel.getFinalValue(basicProfile.getPortChannel(), getDataSource().getHiveApModel());

					list.add(new CheckItem((long) protChannel, MgrUtil
							.getEnumString("enum.switch.interface." + protChannel)));
				}
			}

		}

		return list;
	}

	public boolean supportedStp(short portNum){
		List<CheckItem> list = getNotSupportStpPorts();
		if(list == null){
			return true;
		}
		for (CheckItem tmpObj : list){
			if (portNum == tmpObj.getId()){
				return false;
			}
		}
		return true;
	}

	public boolean checkMstpTable(){
		Map<Short,Set<Short>> map = new HashMap<Short, Set<Short>>();

		if(mstp_interface != null && mstp_interface.length > 0)
		for (int i = 0; i < mstp_interface.length; i ++) {
			Set<Short> list = new HashSet<Short>();
			if(map.isEmpty() || map == null ){
				if(!list.add(mstp_instance[i])){
					errMsgTmp = MgrUtil.getUserMessage("error.stp.mstp.instance",
							new String[]{mstp_instance[i].toString(), MgrUtil.getEnumString("enum.switch.interface." + mstp_interface[i])});
					addActionError(errMsgTmp);
					return false;
				}else{
					map.put(mstp_interface[i], list);
				}
			}else{
				if(map.get(mstp_interface[i]) != null){
					if(!map.get(mstp_interface[i]).add(mstp_instance[i])){
						errMsgTmp = MgrUtil.getUserMessage("error.stp.mstp.instance",
								new String[]{mstp_instance[i].toString(), MgrUtil.getEnumString("enum.switch.interface." + mstp_interface[i])});
						addActionError(errMsgTmp);
						return false;
					}
				}
			}
		}

		return true;
	}

	public boolean isInArray(String substring, String[] source) {
		if (source == null || source.length == 0) {
			return false;
		}
		for (int i = 0; i < source.length; i++) {
			String aSource = source[i];
			if (aSource.equals(substring)) {
				return true;
			}
		}
		return false;
	}

	public short getOldDeviceType(){
		return this.oldDeviceType;
	}

	public void setOldDeviceType(short oldDeviceType) {
		this.oldDeviceType = oldDeviceType;
	}
	
	public List<DeviceMstpInstancePriority> instanceList;
	
	public List<DeviceMstpInstancePriority> getInstanceList() {
		return instanceList;
	}

	public void setInstanceList(List<DeviceMstpInstancePriority> instanceList) {
		this.instanceList = instanceList;
	}

	private List<DeviceMstpInstancePriority> initInstancePriority(List<MstpRegionPriority> list){
		List<DeviceMstpInstancePriority> instanceList = new ArrayList<DeviceMstpInstancePriority>();
		for(MstpRegionPriority priority : list){
			DeviceMstpInstancePriority devicePriority = new DeviceMstpInstancePriority();
			devicePriority.setInstance(priority.getInstance());
			devicePriority.setPriority(priority.getPriority());
			instanceList.add(devicePriority);
		}
		return instanceList;
	}
	
	private void initAllPortLevelSettings(){
		Map<Short,List<CheckItem>>  map = getPortaChannelMemberPorts();
		List<CheckItem> allPortsList = allPortsList();
		allPortLevelSettings = getDataSource().getDeviceStpSettings().getInterfaceStpSettings();
		if (allPortLevelSettings.isEmpty()) {

			allPortLevelSettings = new ArrayList<InterfaceStpSettings>();

			for(CheckItem settings : allPortsList){
				InterfaceStpSettings deviceStpSettings = new InterfaceStpSettings();
				deviceStpSettings.setInterfaceNum(settings.getId().shortValue());
				deviceStpSettings.setDevicePortName(settings.getValue());
				if(this.supportedStp(settings.getId().shortValue()) && !isPortChannelMemberPort(settings.getId().shortValue())){
					deviceStpSettings.setPortChannelMemberPort(false);
				}else{
					deviceStpSettings.setPortChannelMemberPort(true);
				}

				//check port enabled authentication
				if(isEnabledAuth(settings.getId().shortValue())){
					deviceStpSettings.setEnableAuth(true);
					deviceStpSettings.setEdgePort(true);
				}
				
				if(isEnableSpanningEdgePort(settings.getId().shortValue())){
					deviceStpSettings.setEdgePort(true);
					deviceStpSettings.setBpduMode(InterfaceStpSettings.BPDU_GUARD_MODE);
				}
				allPortLevelSettings.add(deviceStpSettings);
			}
			getDataSource().getDeviceStpSettings().setInterfaceStpSettings(allPortLevelSettings);
		} else {
			List<Short> array = new ArrayList<Short>();
			for (int i = 0; i < allPortLevelSettings.size(); i ++){
				InterfaceStpSettings deviceStpSettings = allPortLevelSettings.get(i);
				DeviceInfType type = DeviceInfType.getInstance(deviceStpSettings.getInterfaceNum(), getDataSource().getHiveApModel()).getDeviceInfType();
				deviceStpSettings.setDevicePortName(MgrUtil.getEnumString("enum.switch.interface." + deviceStpSettings.getInterfaceNum()));

				if(isEnabledAuth(deviceStpSettings.getInterfaceNum())){
					deviceStpSettings.setEnableAuth(true);
					deviceStpSettings.setEdgePort(true);
				}
				
				if(this.supportedStp(deviceStpSettings.getInterfaceNum()) && !isPortChannelMemberPort(deviceStpSettings.getInterfaceNum())){
					deviceStpSettings.setPortChannelMemberPort(false);
				}else{
					deviceStpSettings.setPortChannelMemberPort(true);
				}

				if(!getDataSource().getDeviceStpSettings().isOverrideStp()){
					if(isEnableSpanningEdgePort(deviceStpSettings.getInterfaceNum())){
						deviceStpSettings.setEdgePort(true);
						deviceStpSettings.setBpduMode(InterfaceStpSettings.BPDU_GUARD_MODE);
					}else{
						deviceStpSettings.setEdgePort(false);
						deviceStpSettings.setBpduMode(InterfaceStpSettings.BPDU_DEFAULT_MODE);
					}
				}
				
				if(!map.keySet().contains(deviceStpSettings.getInterfaceNum())
						&& type == DeviceInfType.PortChannel){
					allPortLevelSettings.remove(i);
					i --;
				}else{
					array.add(deviceStpSettings.getInterfaceNum());
				}
			}
			for (int i = 0; i < allPortsList.size(); i ++){
				if(!array.contains(allPortsList.get(i).getId().shortValue())
						&& !isPortChannelMemberPort(allPortsList.get(i).getId().shortValue())){
					InterfaceStpSettings tmpObj = new InterfaceStpSettings();
					tmpObj.setInterfaceNum(allPortsList.get(i).getId().shortValue());
					if(!this.supportedStp(allPortsList.get(i).getId().shortValue())){
						tmpObj.setPortChannelMemberPort(true);
					}else{
						tmpObj.setPortChannelMemberPort(false);
					}

					tmpObj.setDevicePortName(MgrUtil.getEnumString("enum.switch.interface." + allPortsList.get(i).getId()));
					allPortLevelSettings.add(tmpObj);
				}
			}
		}
		
		Collections.sort(allPortLevelSettings, new Comparator<InterfaceStpSettings>() {
			public int compare(InterfaceStpSettings o1, InterfaceStpSettings o2) {
				return (int) (o1.getInterfaceNum() - o2.getInterfaceNum());
			}
		});

		getDataSource().getDeviceStpSettings().setInterfaceStpSettings(allPortLevelSettings);
	}
	
	private void initAllInstancePrioritySettings(){
		if(getDataSource().getConfigTemplate() != null 
				&& getDataSource().getConfigTemplate().getSwitchSettings() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().getStp_mode() == StpSettings.STP_MODE_MSTP
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().getMstpRegion() != null
				&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().getMstpRegion().getMstpRegionPriorityList() != null){
			if(getDataSource().getDeviceStpSettings() != null
					&& getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().isEnableStp()){
				List<MstpRegionPriority> priority = getDataSource().getConfigTemplate().getSwitchSettings().getStpSettings().getMstpRegion().getMstpRegionPriorityList();
				allInstancePrioritySettings = getDataSource().getDeviceStpSettings().getInstancePriority();
				Map<Short, Integer> map = new HashMap<Short, Integer>();
				for (MstpRegionPriority tmpObj : priority){
					map.put(tmpObj.getInstance(), tmpObj.getPriority());
				}
				
				if("requestTemplate".equalsIgnoreCase(operation)){
					allInstancePrioritySettings.clear();
				}
				
				if(allInstancePrioritySettings.isEmpty()){
					allInstancePrioritySettings = initInstancePriority(priority);
				}else{
					Map<Short, Integer> instanceMap = new HashMap<Short, Integer>();
					for(int i = 0; i < allInstancePrioritySettings.size(); i ++){
						DeviceMstpInstancePriority instancePriority = allInstancePrioritySettings.get(i);
						if(!map.keySet().contains(instancePriority.getInstance())){
							allInstancePrioritySettings.remove(i);
							i --;
						}else{
							instanceMap.put(instancePriority.getInstance(), instancePriority.getPriority());
						}
					}
					
					Set<Short> key = map.keySet();
				    for (Iterator<Short> it = key.iterator(); it.hasNext();) {
				    	short s = it.next();
				        if(!instanceMap.keySet().contains(s)){
				        	DeviceMstpInstancePriority tmpObj = new DeviceMstpInstancePriority();
				        	tmpObj.setInstance(s);
				        	tmpObj.setPriority(map.get(s));
				        	allInstancePrioritySettings.add(tmpObj);
				        }
				    }
				}
				
				Collections.sort(allInstancePrioritySettings, new Comparator<DeviceMstpInstancePriority>() {
					public int compare(DeviceMstpInstancePriority o1, DeviceMstpInstancePriority o2) {
						return (int) (o1.getInstance() - o2.getInstance());
					}
				});
			}
			getDataSource().getDeviceStpSettings().setInstancePriority(allInstancePrioritySettings);
		}
	}
	
	private void updateAllPortLevelSettings(){
		List<InterfaceStpSettings> settingsList = getDataSource().getDeviceStpSettings().getInterfaceStpSettings();
		for(int j = 0; j < settingsList.size(); j ++){
			InterfaceStpSettings settings = settingsList.get(j);
			if (interfaceNums != null && interfaceNums.length > 0) {
				for (int i = 0; i < interfaceNums.length; i++) {
					if (settings.getInterfaceNum() == interfaceNums[i]) {
						settings.setInterfaceNum(interfaceNums[i]);
						settings.setDevicePortName(MgrUtil.getEnumString("enum.switch.interface." + interfaceNums[i]));
						settings.setTimes(device_priority[i]);
						settings.setDefaultPathCost(device_path_cost[i]);
						settings.setBpduMode(Short
								.parseShort(hidden_bpduModes[i]));
						settings.setEnableStp(enableStpStatus[i]);
						if(!isEnabledAuth(settings.getInterfaceNum())){
							settings.setEdgePort(edgePorts[i]);
						}
						if(!this.supportedStp(settings.getInterfaceNum())
								|| isPortChannelMemberPort(settings.getInterfaceNum())){
							settings.setPortChannelMemberPort(true);
						}else{
							settings.setPortChannelMemberPort(false);
						}
						continue;
					}
				}
			} else if (settings.getInstance() > 0){
				settingsList.remove(j);
				j --;
			}
		}
	}
	
	private void updateAllInstancePrioritySettings(){
		if(getDataSource().getDeviceStpSettings().getStp_mode() == StpSettings.STP_MODE_MSTP
				&& device_instance_priority != null && device_instance_priority.length > 0){
			List<DeviceMstpInstancePriority> settingsList = getDataSource().getDeviceStpSettings().getInstancePriority();
			if(device_instance_id.length == settingsList.size()){
				for(int i = 0; i < settingsList.size(); i ++){
					if(device_instance_id[i] == settingsList.get(i).getInstance()){
						settingsList.get(i).setTimes(device_instance_priority[i]);
					}
				}
			}
			getDataSource().getDeviceStpSettings().setInstancePriority(settingsList);
		}
	}
	private void updateAllInterfaceMstpInstancePriority(){
		allPortPrioritySettings = getDataSource().getDeviceStpSettings().getInterfaceMstpSettings();
		if(mstp_interface != null && mstp_interface.length > 0){
			List<InterfaceMstpSettings> newDataList = new ArrayList<InterfaceMstpSettings>();
			for (int i = 0; i < mstp_interface.length; i++) {
				if ( mstp_interface[i] != null && mstp_interface[i] > 0){
					if (!this.supportedStp(mstp_interface[i])){
						continue;
					}
				}
				InterfaceMstpSettings settings = new InterfaceMstpSettings();
				settings.setInterfaceNum(mstp_interface[i]);
				settings.setDevicePortName(MgrUtil.getEnumString("enum.switch.interface." + mstp_interface[i]));
				settings.setInstance(mstp_instance[i]);
				settings.setDefaultPathCost(mstp_path_cost[i]);
				settings.setTimes(mstp_priority[i]);
				if(!this.supportedStp(mstp_interface[i])
						|| isPortChannelMemberPort(mstp_interface[i])){
					settings.setPortChannelMemberPort(true);
				}else{
					settings.setPortChannelMemberPort(false);
				}
				newDataList.add(settings);
			}
			getDataSource().getDeviceStpSettings().setInterfaceMstpSettings(newDataList);
		}else{
			allPortPrioritySettings.clear();
			getDataSource().getDeviceStpSettings().setInterfaceMstpSettings(allPortPrioritySettings);
		}
	}

	public int getHavePPPoE() {
		return havePPPoE;
	}

	public void setHavePPPoE(int havePPPoE) {
		this.havePPPoE = havePPPoE;
	}

	public boolean[] getClientReporting() {
		return clientReporting;
	}

	public void setClientReporting(boolean[] clientReporting) {
		this.clientReporting = clientReporting;
	}
	public boolean isUsbPriorityPrimary() {
		return isUsbPriorityPrimary;
	}

	public void setUsbPriorityPrimary(boolean isUsbPriorityPrimary) {
		this.isUsbPriorityPrimary = isUsbPriorityPrimary;
	}
	
	public boolean isEnabledAuth(short interfaceNum){
		if(getDataSource().getPortGroup() != null){
			for (PortBasicProfile basicProfile : getDataSource().getPortGroup().getBasicProfiles()){
				PortAccessProfile accessProfile = basicProfile.getAccessProfile();
				if(accessProfile.isRadiusAuthEnable()){
					String[] eths = basicProfile.getETHs();
					String[] sfps = basicProfile.getSFPs();
//					String[] usbs = basicProfile.getUSBs();
					int interType;
					if(eths != null && eths.length > 0){
						for(int i=0; i<eths.length; i++){
							interType = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(eths[i]), getDataSource().getHiveApModel());
							if(interfaceNum == interType){
								return true;
							}
						}
					}
					if(sfps != null && sfps.length > 0){
						for(int i=0; i<sfps.length; i++){
							interType = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfps[i]), getDataSource().getHiveApModel());
							if(interfaceNum == interType){
								return true;
							}
						}
					}
//					if(usbs != null && usbs.length > 0){
//						for(int i=0; i<usbs.length; i++){
//							interType = DeviceInfType.USB.getFinalValue(Integer.valueOf(usbs[i]), getDataSource().getHiveApModel());
//							if(interfaceNum == interType){
//								return true;
//							}
//						}
//					}
				}
			}
		}
		return false;
	}

	public String[] getPortDescription() {
		return portDescription;
	}

	public void setPortDescription(String[] portDescription) {
		this.portDescription = portDescription;
	}
	
	
	public boolean isEnableSpanningEdgePort(short interfaceNum){
		if(getDataSource().getPortGroup() != null){
			for (PortBasicProfile basicProfile : getDataSource().getPortGroup().getBasicProfiles()){
				PortAccessProfile accessProfile = basicProfile.getAccessProfile();
				if(accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_ACCESS
						|| accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA
						|| accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_MONITOR){
					String[] eths = basicProfile.getETHs();
					String[] sfps = basicProfile.getSFPs();
//					String[] usbs = basicProfile.getUSBs();
					int interType;
					if(eths != null && eths.length > 0){
						for(int i=0; i<eths.length; i++){
							interType = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(eths[i]), getDataSource().getHiveApModel());
							if(interfaceNum == interType){
								return true;
							}
						}
					}
					if(sfps != null && sfps.length > 0){
						for(int i=0; i<sfps.length; i++){
							interType = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfps[i]), getDataSource().getHiveApModel());
							if(interfaceNum == interType){
								return true;
							}
						}
					}
//					if(usbs != null && usbs.length > 0){
//						for(int i=0; i<usbs.length; i++){
//							interType = DeviceInfType.USB.getFinalValue(Integer.valueOf(usbs[i]), getDataSource().getHiveApModel());
//							if(interfaceNum == interType){
//								return true;
//							}
//						}
//					}
				}
			}
		}
		return false;
	}
	
	private String selectedAllStr;
	private String selectedAllApStr;
	private String selectedAllBrStr;
	private String selectedAllSwitchStr;
	private String selectedAllL3VPNStr;
	private String selectedAllL2VPNStr;
	
	private String selectedDeviceIdStr;
	
	private void prepareSelectAllForGuidList(){
		if(filterParams == null){
			return;
		}
		
		String idStr;
		short hiveApModel, deviceType;
		HiveApUtils.DeviceListType enumType;
		StringBuffer selectedAllStrB = new StringBuffer();
		StringBuffer selectedAllApStrB = new StringBuffer();
		StringBuffer selectedAllBrStrB = new StringBuffer();
		StringBuffer selectedAllSwitchStrB = new StringBuffer();
		StringBuffer selectedAllL3VPNStrB = new StringBuffer();
		StringBuffer selectedAllL2VPNStrB = new StringBuffer();
		
		String queryStr = "select id, hiveApModel, deviceType from "+boClass.getSimpleName();
		List<?> resList = QueryUtil.executeQuery(queryStr, null, filterParams, this.getDomainId(), null);
		if(resList != null && !resList.isEmpty()){
			for(Object obj : resList){
				Object[] arryObj = (Object[])obj;
				idStr = String.valueOf(arryObj[0]);
				hiveApModel = Short.valueOf(String.valueOf(arryObj[1]));
				deviceType = Short.valueOf(String.valueOf(arryObj[2]));
				enumType = HiveApUtils.getDeviceTypeEnum(hiveApModel, deviceType);

				idStr = "," + idStr;
				selectedAllStrB.append(idStr);
				if(enumType == HiveApUtils.DeviceListType.AP){
					selectedAllApStrB.append(idStr);
				}else if(enumType == HiveApUtils.DeviceListType.BR){
					selectedAllBrStrB.append(idStr);
				}else if(enumType == HiveApUtils.DeviceListType.Switch){
					selectedAllSwitchStrB.append(idStr);
				}else if(enumType == HiveApUtils.DeviceListType.L3_VPN){
					selectedAllL3VPNStrB.append(idStr);
				}else if(enumType == HiveApUtils.DeviceListType.L2_VPN){
					selectedAllL2VPNStrB.append(idStr);
				}
			}
		}
		if(selectedAllStrB.length() > 0){
			selectedAllStr = selectedAllStrB.substring(1);
		}
		if(selectedAllApStrB.length() > 0){
			selectedAllApStr = selectedAllApStrB.substring(1);
		}
		if(selectedAllBrStrB.length() > 0){
			selectedAllBrStr = selectedAllBrStrB.substring(1);
		}
		if(selectedAllSwitchStrB.length() > 0){
			selectedAllSwitchStr = selectedAllSwitchStrB.substring(1);
		}
		if(selectedAllL3VPNStrB.length() > 0){
			selectedAllL3VPNStr = selectedAllL3VPNStrB.substring(1);
		}
		if(selectedAllL2VPNStrB.length() > 0){
			selectedAllL2VPNStr = selectedAllL2VPNStrB.substring(1);
		}
	}
	
	public int getSelectedAllApCounts(){
		if(selectedAllApStr == null || "".equals(selectedAllApStr)){
			return 0;
		}
		return selectedAllApStr.split(",").length;
	}
	
	public int getSelectedAllBrCounts(){
		if(selectedAllBrStr == null || "".equals(selectedAllBrStr)){
			return 0;
		}
		return selectedAllBrStr.split(",").length;
	}
	
	public EnumItem[] getPoEModeList(){
		return HiveAp.ENUM_SYSTEM_POE_POWER_MODE;
	}
	
	public EnumItem[] getPoEPrimaryEthList(){
		return HiveAp.ENUM_SYSTEM_POE_PRIMARY_ETH;
	}
	
	public int getSelectedAllSwitchCounts(){
		if(selectedAllSwitchStr == null || "".equals(selectedAllSwitchStr)){
			return 0;
		}
		return selectedAllSwitchStr.split(",").length;
	}
	
	public int getSelectedAllL3VPNCounts(){
		if(selectedAllL3VPNStr == null || "".equals(selectedAllL3VPNStr)){
			return 0;
		}
		return selectedAllL3VPNStr.split(",").length;
	}
	
	public int getSelectedAllL2VPNCounts(){
		if(selectedAllL2VPNStr == null || "".equals(selectedAllL2VPNStr)){
			return 0;
		}
		return selectedAllL2VPNStr.split(",").length;
	}

	public String getSelectedAllStr() {
		return selectedAllStr;
	}

	public void setSelectedAllStr(String selectedAllStr) {
		this.selectedAllStr = selectedAllStr;
	}

	public String getSelectedAllApStr() {
		return selectedAllApStr;
	}

	public void setSelectedAllApStr(String selectedAllApStr) {
		this.selectedAllApStr = selectedAllApStr;
	}

	public String getSelectedAllBrStr() {
		return selectedAllBrStr;
	}

	public void setSelectedAllBrStr(String selectedAllBrStr) {
		this.selectedAllBrStr = selectedAllBrStr;
	}

	public String getSelectedAllSwitchStr() {
		return selectedAllSwitchStr;
	}

	public void setSelectedAllSwitchStr(String selectedAllSwitchStr) {
		this.selectedAllSwitchStr = selectedAllSwitchStr;
	}

	public String getSelectedAllL3VPNStr() {
		return selectedAllL3VPNStr;
	}

	public void setSelectedAllL3VPNStr(String selectedAllL3VPNStr) {
		this.selectedAllL3VPNStr = selectedAllL3VPNStr;
	}

	public String getSelectedAllL2VPNStr() {
		return selectedAllL2VPNStr;
	}

	public void setSelectedAllL2VPNStr(String selectedAllL2VPNStr) {
		this.selectedAllL2VPNStr = selectedAllL2VPNStr;
	}

	public String getSelectedDeviceIdStr() {
		return selectedDeviceIdStr;
	}

	public void setSelectedDeviceIdStr(String selectedDeviceIdStr) {
		this.selectedDeviceIdStr = selectedDeviceIdStr;
	}

	public String getFilterSerialNumber() {
		return filterSerialNumber;
	}

	public void setFilterSerialNumber(String filterSerialNumber) {
		this.filterSerialNumber = filterSerialNumber;
	}
	private String radioProfileName;
	private Short radioProfileMode;
	private Short radioProfileChannelWidth;

	public String getRadioProfileName() {
		return radioProfileName;
	}

	public void setRadioProfileName(String radioProfileName) {
		this.radioProfileName = radioProfileName;
	}

	public Short getRadioProfileMode() {
		return radioProfileMode;
	}

	public void setRadioProfileMode(Short radioProfileMode) {
		this.radioProfileMode = radioProfileMode;
	}

	public Short getRadioProfileChannelWidth() {
		return radioProfileChannelWidth;
	}

	public void setRadioProfileChannelWidth(Short radioProfileChannelWidth) {
		this.radioProfileChannelWidth = radioProfileChannelWidth;
	}
	
	public boolean isEth1Available(){
		return getDataSource().getDeviceInfo().getIntegerValue(DeviceInfo.SPT_ETHERNET_COUNTS) == 2;
	}
	
	public String getEnabledOverrideCaptureDataMode(){
		if(getDataSource().isOverrideCaptureDataByCWP()){
			return "";
		}else{
			return "none";
		}
	}
	
	public String getOverrideConfigCaptureDataStyle(){
		return this.getFullModeConfigStyle();
	}
	
	public String getCustomTag1String() {
		Map<String,String> cusMap=DeviceTagUtil.getInstance().getClassifierCustomTag(this.getDomainId());
		return cusMap.get(DeviceTagUtil.CUSTOM_TAG1);
	}
	public String getCustomTag2String() {
		Map<String,String> cusMap=DeviceTagUtil.getInstance().getClassifierCustomTag(this.getDomainId());
		return cusMap.get(DeviceTagUtil.CUSTOM_TAG2);
	}
	public String getCustomTag3String() {
		Map<String,String> cusMap=DeviceTagUtil.getInstance().getClassifierCustomTag(this.getDomainId());
		return cusMap.get(DeviceTagUtil.CUSTOM_TAG3);
	}
	
	public List<TextItem> getClassificationTag1List(){
		List<TextItem> retLst = new ArrayList<TextItem>();
		retLst.add(new TextItem("","All"));
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct classificationTag1 from hive_ap a");
		sql.append(" where a.owner=").append(getDomain().getId());
		sql.append(" and a.classificationTag1!='' and a.classificationTag1 is not null");
		sql.append(" order by classificationTag1");
		List<?> lst = QueryUtil.executeNativeQuery(sql.toString());
		if (!lst.isEmpty()) {
			for(Object onb: lst) {
				retLst.add(new TextItem(onb.toString(),onb.toString()));
			}
		}
		return retLst;
	}
	public List<TextItem> getClassificationTag2List(){
		List<TextItem> retLst = new ArrayList<TextItem>();
		retLst.add(new TextItem("","All"));
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct classificationTag2 from hive_ap a");
		sql.append(" where a.owner=").append(getDomain().getId());
		sql.append(" and a.classificationTag2!='' and a.classificationTag2 is not null");
		sql.append(" order by classificationTag2");
		List<?> lst = QueryUtil.executeNativeQuery(sql.toString());
		if (!lst.isEmpty()) {
			for(Object onb: lst) {
				retLst.add(new TextItem(onb.toString(),onb.toString()));
			}
		}
		return retLst;
	}
	public List<TextItem> getClassificationTag3List(){
		List<TextItem> retLst = new ArrayList<TextItem>();
		retLst.add(new TextItem("","All"));
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct classificationTag3 from hive_ap a");
		sql.append(" where a.owner=").append(getDomain().getId());
		sql.append(" and a.classificationTag3!='' and a.classificationTag3 is not null");
		sql.append(" order by classificationTag3");
		List<?> lst = QueryUtil.executeNativeQuery(sql.toString());
		if (!lst.isEmpty()) {
			for(Object onb: lst) {
				retLst.add(new TextItem(onb.toString(),onb.toString()));
			}
		}
		return retLst;
	}
	
	//Mission UX update support.
	private boolean completeCfgUpdate;
	private boolean imageUpgrade;
	private boolean forceImageUpgrade;
	private short simplifiedRebootType;

	public boolean isCompleteCfgUpdate() {
		return completeCfgUpdate;
	}

	public void setCompleteCfgUpdate(boolean completeCfgUpdate) {
		this.completeCfgUpdate = completeCfgUpdate;
	}

	public boolean isImageUpgrade() {
		return imageUpgrade;
	}

	public void setImageUpgrade(boolean imageUpgrade) {
		this.imageUpgrade = imageUpgrade;
	}

	public boolean isForceImageUpgrade() {
		return forceImageUpgrade;
	}

	public void setForceImageUpgrade(boolean forceImageUpgrade) {
		this.forceImageUpgrade = forceImageUpgrade;
	}

	public short getSimplifiedRebootType() {
		return simplifiedRebootType;
	}

	public void setSimplifiedRebootType(short simplifiedRebootType) {
		this.simplifiedRebootType = simplifiedRebootType;
	}

	private InputStream inputStream;
	public InputStream getInputStream() throws Exception {
		return inputStream;
	}
	public String getLocalCSVFileName() {
		return "Device Inventory of " + this.getDomain().getDomainName() + ".csv";
	}
	private String exportDeviceInventories(FilterParams filterArg) {
		getSessionFiltering();
		List<DeviceInventory> deviceInventories  = null;
		List<HiveAp> devices = null;
		if (allItemsSelected) {
			devices = QueryUtil.executeQuery(HiveAp.class, 
						sortParams, 
						filterParams, 
						this.getDomain().getId(),
						this);
			
		} else {
			devices = QueryUtil.executeQuery(HiveAp.class, 
						sortParams, 
						FilterParamsFactory.getInstance().fieldIsIn("id", this.getAllSelectedIds()), 
						this.getDomain().getId(),
						this);
		}
		
		if (devices != null
				&& !devices.isEmpty()) {
			deviceInventories = new ArrayList<>();
			for (HiveAp device : devices) {
				if (device.isSimulated()) {
					continue;
				}
				DeviceInventory di = new DeviceInventory();
				di.setHiveAp(device);
				di.setOwner(device.getOwner());
				di.setSerialNumber(device.getSerialNumber());
				deviceInventories.add(di);
			}
		}
		
		
		String csvString = DeviceImpUtils.getInstance()
				.getDeviceInventoryCSVString(deviceInventories, DeviceUtils.EXPORT_CSV_TYPE_CONFIGURATION, isEasyMode());
		inputStream = new ByteArrayInputStream(csvString.getBytes());
		return "exportDeviceInventory";
	}
	
	//fix bug 27541
	private boolean isDefaultTemplate(HiveAp hiveAp){
		if(hiveAp.getConfigTemplate() != null){
			return hiveAp.getConfigTemplate().getConfigName().trim().equalsIgnoreCase(BeParaModule.DEFAULT_DEVICE_GROUP_NAME);
		}
		return false;
	}
	
	public String getDisplayLocation(){
		return getDataSource().isMultiDisplayLocation() ? "" : "none";
	}

	public long getNewHiveApCount() {
		return newHiveApCount;
	}

	public void setNewHiveApCount(long newHiveApCount) {
		this.newHiveApCount = newHiveApCount;
	}

	public boolean isMstpEnable() {
		return mstpEnable;
	}

	public void setMstpEnable(boolean mstpEnable) {
		this.mstpEnable = mstpEnable;
	}
	
	public boolean isDeviceSptRadiusServer(){
		return this.getDataSource() != null && 
				this.getDataSource().getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_RADIUS_SERVER);
	}
	
	private String diMenuTypeKey=null;
	private String pre_serialNumber=null;

	public String getDiMenuTypeKey() {
		return diMenuTypeKey;
	}

	public void setDiMenuTypeKey(String diMenuTypeKey) {
		this.diMenuTypeKey = diMenuTypeKey;
	}

	public String getPre_serialNumber() {
		return pre_serialNumber;
	}

	public void setPre_serialNumber(String pre_serialNumber) {
		this.pre_serialNumber = pre_serialNumber;
	}
	
	public boolean isBR100PlatformDevice(){
		return this.getDataSource().getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100 ? true : false;
	}
	
	public String getDelayAlarmEnableStyle(){
		if(null == getDataSource()){
			return "none";
		}
		
		return getDataSource().isOverrideEnableDelayAlarm() || getDataSource().getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY ? "": "none";
	}
	
	public String getDelayAlarmEnableMultyStyle(){
		if(null == getDataSource()){
			return "none";
		}
		
		return getDataSource().isMultiChangeDelayAlarm()? "": "none";
	}
	
	private void updateDelayAlarm(HiveAp hiveAp, HiveAp dataSource) {
		if (null == hiveAp || null == dataSource
				|| !dataSource.isMultiChangeDelayAlarm()) {
			return;
		}
		hiveAp.setOverrideEnableDelayAlarm(true);
		hiveAp.setEnableDelayAlarm(dataSource.isEnableDelayAlarm());
	}
	
	public String getDeviceInfoJSONStr() throws JSONException{
		JSONObject jsonObj = new JSONObject();
		
		for(Short apModel : DevicePropertyManage.getInstance().getAllDeviceKey()){
			DeviceInfo dInfo = NmsUtil.getDeviceInfo(apModel);
			JSONObject cldObj = new JSONObject();
			jsonObj.put(apModel.toString(), cldObj);
			
			//get spt_device_image_counts attribute
			cldObj.put(DeviceInfo.SPT_DEVICE_IMAGE_COUNTS, dInfo.getIntegerValue(DeviceInfo.SPT_DEVICE_IMAGE_COUNTS));
		}
		
		return jsonObj.toString();
	}
	
//	public EnumItem[] getIDMAuthProxyTypeAuto() {
//		return new EnumItem[] { new EnumItem(HiveAp.IDM_AUTH_PROXY_AUTO,
//				getText("hiveAp.server.idmAuthProxy.type.auto")) };
//	}
//	
//	public EnumItem[] getIDMAuthProxyTypeDisable() {
//		return new EnumItem[] { new EnumItem(HiveAp.IDM_AUTH_PROXY_DISABLE,
//				getText("hiveAp.server.idmAuthProxy.type.disable")) };
//	}
//	
//	public EnumItem[] getStringNoChange() {
//		return new EnumItem[] { new EnumItem(-3, this.strNoChange) };
//	}
//	
	public String getMultiServiceSettingsStyle(){
		if(this.isIDMEnableForCurrentVHM()){
			return "";
		}else{
			return "none";
		}
	}
	
	public boolean isIDMEnableForCurrentVHM(){
		return NmsUtil.isVhmEnableIdm(super.getDomainId());
	}
	
	private List<CheckItem> list_cliBlob;
	
	private void prepareSupplentalCLI(){
		list_cliBlob = getBoCheckItems("supplementalName", CLIBlob.class, null,
				CHECK_ITEM_BEGIN_BLANK,CHECK_ITEM_END_NO);
	}

	public List<CheckItem> getList_cliBlob() {
		return list_cliBlob;
	}

	public void setList_cliBlob(List<CheckItem> list_cliBlob) {
		this.list_cliBlob = list_cliBlob;
	}

	public Long getSupplementalCLIId() {
		return supplementalCLIId;
	}

	public void setSupplementalCLIId(Long supplementalCLIId) {
		this.supplementalCLIId = supplementalCLIId;
	}
	
	protected void setSelectedSuppCLI() throws Exception {
		if (null != supplementalCLIId) {
			CLIBlob cb = QueryUtil.findBoById(CLIBlob.class, supplementalCLIId);
			if (null != getDataSource()) {
				getDataSource().setSupplementalCLI(cb);
			}
		}
	}
	
	public String getSupplementalCLIStyle(){
		HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner",getDomain());
		if(null != bo && bo.isEnableSupplementalCLI()){
			return "";
		}
		return "none";
	}
	
	public boolean getEnableSupplementalCLI(){
		HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner",getDomain());
		return null != bo && bo.isEnableSupplementalCLI()? true : false;
	}
	
	private boolean resetDeviceFlag = true;

	public boolean isResetDeviceFlag() {
		return resetDeviceFlag;
	}

	public void setResetDeviceFlag(boolean resetDeviceFlag) {
		this.resetDeviceFlag = resetDeviceFlag;
	}
}
