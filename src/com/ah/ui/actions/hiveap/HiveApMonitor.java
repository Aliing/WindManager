package com.ah.ui.actions.hiveap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCPUMemInfoEvent;
import com.ah.be.communication.event.BeCapwapClientEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.communication.event.BePOEStatusEvent;
import com.ah.be.communication.event.BePOEStatusResultEvent;
import com.ah.be.communication.event.BeSwitchSystemInfoEvent;
import com.ah.be.communication.event.BeSwitchSystemInfoResultEvent;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.config.hiveap.UpdateUtil;
import com.ah.be.db.configuration.ConfigurationProcessor.ConfigurationType;
import com.ah.be.db.configuration.ConfigurationResources;
import com.ah.be.db.configuration.ConfigurationUtils;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.performance.BeInterfaceReportProcessor;
import com.ah.be.topo.BeTopoModuleParameters;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.ConfigTemplateVlanNetwork;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApUpdateResult;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.lan.LanProfile;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnService;
import com.ah.bo.network.VpnServiceCredential;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhClientSessionHistory;
import com.ah.bo.performance.AhInterferenceStats;
import com.ah.bo.performance.AhLLDPInformation;
import com.ah.bo.performance.AhLatestACSPNeighbor;
import com.ah.bo.performance.AhLatestNeighbor;
import com.ah.bo.performance.AhLatestRadioAttribute;
import com.ah.bo.performance.AhLatestXif;
import com.ah.bo.performance.AhPSEStatus;
import com.ah.bo.performance.AhPortAvailability;
import com.ah.bo.performance.AhRadioAttribute;
import com.ah.bo.performance.AhReport;
import com.ah.bo.performance.AhRouterLTEVZInfo;
import com.ah.bo.performance.AhStatsAvailabilityHigh;
import com.ah.bo.performance.AhStatsLatencyHigh;
import com.ah.bo.performance.AhStatsThroughputHigh;
import com.ah.bo.performance.AhStatsVpnStatusHigh;
import com.ah.bo.performance.AhSwitchPortInfo;
import com.ah.bo.performance.AhSwitchPortStats;
import com.ah.bo.performance.AhVPNStatus;
import com.ah.bo.performance.AhVPNStatus.VpnStatus;
import com.ah.bo.performance.AhXIf;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.SsidProfile;
import com.ah.bo.wlan.TX11aOr11gRateSetting;
import com.ah.bo.wlan.Tx11acRateSettings;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.monitor.AhEventsAction;
import com.ah.ui.actions.monitor.ClientMonitorAction;
import com.ah.ui.actions.monitor.MapNodeAction;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.bo.device.DeviceInterfaceAdapter;
import com.ah.util.bo.device.DeviceInterfaceBundle;
import com.ah.util.bo.device.DeviceInterfaceUtil;
import com.ah.util.bo.device.pse.PSEStatusBundle;
import com.ah.util.coder.AhDecoder;
import com.ah.util.datetime.AhDateTimeUtil;

public class HiveApMonitor extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(HiveApMonitor.class
			.getSimpleName());
	
	// gigabitEthernet1
	private static final String SWITCH_PORT_GIGABIT_NAME_PREFIX = "eth1";
	// sfp1
	private static final String SWITCH_PORT_SFP_NAME_PREFIX = "eth1";
	
	private static final int SWITCH_PORT_NUM_24 = 24;
	private static final int SWITCH_PORT_NUM_48 = 48;
	
	private static final String USB_STATUS_DISCONNECTED = "Disconnected";
	private static final String USB_STATUS_CONNECTED = "Connected";
	private static final String USB_STATUS_ACTIVE = "Active";
	private static final String PORT_ETH = "ETH";
	private static final String PORT_SFP = "SFP";
	private static final String PORT_USB = "USB";
	private static final String SPEARATOR_COMMA = ",";
	private static final String WANSTATUS_GREEN="/images/wanStatus_Green.png";
	private static final String WANSTATUS_GRAY="/images/wanStatus_Gray.png";
	public static final String RESULT_CODE_SUCCESS = "0";
	public static final String RESULT_CODE_FAILURE = "1";
	
	public AhRouterLTEVZInfo ahRouter;
	public List<DeviceInterface> wanDeviceInterfaceList=new ArrayList<DeviceInterface>();
	public double dwThroughput=0;
	public double upThroughput=0;
	
	public static TimeZone tz;

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}

		tz = getUserTimeZone();

		try {
			if ("hiveApDetails".equals(operation)) {
				long startTime = System.currentTimeMillis();
				long timeTmp = printTime(0, "");
				log.info("execute", "operation:" + operation + ", id:" + id);

				if(id == null) {
					return SUCCESS;
				}

				HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class, id,
						new HiveApAction());
				setSessionDataSource(hiveAp);
				prepareDeviceType(hiveAp);
				preparePageLayouts(hiveAp);
				timeTmp = printTime(timeTmp, "preparePageLayouts");
				getReportDataFromDevice(hiveAp);
				timeTmp = printTime(timeTmp, "getReportDataFromDevice");
				prepareDetails(hiveAp);
				if (null != hiveAp)
					prepareFlash(hiveAp.is11nHiveAP(), hiveAp.getSoftVer(), hiveAp.getDeviceType(), hiveAp);
				removeCachedStats();
				MgrUtil.setSessionAttribute("calculateBrFlashData_done", "");
				MgrUtil.setSessionAttribute("calculateCVGFlashData_done", "");
				
				long endTime = System.currentTimeMillis();
				log.info("method hiveApDetails ====>: cost time : " + (endTime-startTime)/1000 + "s " + (endTime-startTime)%1000 +"ms");
				return SUCCESS;
			} else if ("getFlashData".equals(operation)) {
				log.info("execute", "operation:" + operation);
				getSessionDataSource();
				prepareDeviceType(getDataSource());
				fetchHiveApInfo(getDataSource());
				return getFlashDataReturnPath(getDataSource());
			} else if ("fetchPoePowerStatus".equals(operation)) {
				log.info("execute", "operation:" + operation);
				jsonObject = fetchPoeStatus();
				return "json";
			} else if ("fetchSwPortInfo".equals(operation)) {
				log.info("execute", "operation:" + operation);
				jsonObject = fetchSwPortInfo();
				return "json";
			} else if("recyclePower".equals(operation)){
				jsonObject = recyclePower();
				return "json";
			} else if("clearErrorCounters".equals(operation)){
				log.info("execute", "clearErrorCounters on device");
				jsonObject = clearErrorCounters();
				return "json";
			}
			return SUCCESS;
		} catch (Exception ex) {
			log.error("execute", "exception", ex);
			addActionError(MgrUtil.getUserMessage(ex));
			return SUCCESS;
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		//setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
		setDataSource(HiveAp.class);
		enableSorting();
		String listTypeFromSession = (String) MgrUtil
				.getSessionAttribute(HiveApAction.HM_LIST_TYPE);
		if("managedVPNGateways".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_VPN_GATEWAYS);
		}else if( "managedRouters".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_BRANCH_ROUTERS);
		}else if( "managedSwitches".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_SWITCHES);
		}else if("managedDeviceAPs".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_DEVICE_HIVEAPS);
		}else if("managedHiveAps".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
		}else{
			setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
		}
	}

	@Override
	public HiveAp getDataSource() {
		if(dataSource != null && dataSource instanceof HiveAp){
			return (HiveAp) dataSource;
		}else{
			return null;
		}
	}

	public String getChangedHiveApName() {
		if (null != name) {
			return name.replace("\\", "\\\\").replace("'", "\\'");
		}
		return "";
	}

	protected JSONObject fetchPoeStatus() throws Exception {
		JSONObject jsonObject = new JSONObject();
		try {
			HiveAp hiveAp = getDataSource();
			String errorMsg = MapNodeAction.validateSelectedHiveAp(hiveAp,
					"3.4.1.0");
			if (null != errorMsg) {
				jsonObject.put("msg", errorMsg);
			} else {
				BePOEStatusEvent request = BeTopoModuleUtil
						.getPoERequestEvent(hiveAp);
				BeCommunicationEvent event = HmBeCommunicationUtil
						.sendSyncRequest(
								request,
								BeTopoModuleParameters.POLLING_VIA_CAPWAP_TIMEOUT);
				if (null == event) {
					jsonObject.put("msg", "fetch PoE status data error.");
				} else {
					BePOEStatusResultEvent result = BeTopoModuleUtil
							.getPoEEventResult(event);
					if (null == result) {
						jsonObject.put("msg", MgrUtil
								.getUserMessage("error.capwap.poe.timeout"));
					} else {
						String content = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">";
						String srcLabel = MgrUtil
								.getUserMessage("monitor.hiveAp.poe.power.src");
						String wifi0Label = MgrUtil
								.getUserMessage("monitor.hiveAp.poe.wifi0.setting");
						String wifi1Label = MgrUtil
								.getUserMessage("monitor.hiveAp.poe.wifi1.setting");
						// String wifi2Label = MgrUtil
						// .getUserMessage("monitor.hiveAp.poe.wifi2.setting");
						String eth0StateLabel = MgrUtil
								.getUserMessage("monitor.hiveAp.poe.eth0.state");
						String eth0PowerLabel = MgrUtil
								.getUserMessage("monitor.hiveAp.poe.eth0.power");
						String eth0SpeedLabel = MgrUtil
								.getUserMessage("monitor.hiveAp.poe.eth0.speed");
						String eth1StateLabel = MgrUtil
								.getUserMessage("monitor.hiveAp.poe.eth1.state");
						String eth1PowerLabel = MgrUtil
								.getUserMessage("monitor.hiveAp.poe.eth1.power");
						String eth1SpeedLabel = MgrUtil
								.getUserMessage("monitor.hiveAp.poe.eth1.speed");
						String srcValue = result.getPowerSourceString();
						String wifi0Setting = result
								.getWifiSettingString(result.getWifi0Setting());
						String wifi1Setting = result
								.getWifiSettingString(result.getWifi1Setting());
						// String wifi2Setting = result
						// .getWifiSettingString(result.getWifi2Setting());
						String eth0State = result
								.getEthState(result.isEth0Up());
						String eth1State = result
								.getEthState(result.isEth1Up());
						String eth0Power = result.getEthPowerString(result
								.getEth0PowerLevel());
						String eth1Power = result.getEthPowerString(result
								.getEth1PowerLevel());
						String eth0Speed = result.getEthSpeedString(result
								.getEth0MaxSpeed());
						String eth1Speed = result.getEthSpeedString(result
								.getEth1MaxSpeed());

						content += "<tr><td class=\"panelLabel\">" + srcLabel
								+ "</td><td colspan=\"3\">" + srcValue
								+ "</td>";
						content += "<tr><td class=\"panelLabel\">" + wifi0Label
								+ "</td><td colspan=\"3\">" + wifi0Setting
								+ "</td>";
						content += "<tr><td class=\"panelLabel\">" + wifi1Label
								+ "</td><td colspan=\"3\">" + wifi1Setting
								+ "</td>";
						content += "<tr><td width=\"165px\" class=\"panelLabel\">"
								+ eth0StateLabel
								+ "</td><td width=\"260px\">"
								+ eth0State
								+ "</td><td width=\"180px\" class=\"panelLabel\">"
								+ eth1StateLabel
								+ "</td><td>"
								+ eth1State
								+ "</td</tr>";
						content += "<tr><td class=\"panelLabel\">"
								+ eth0PowerLabel + "</td><td>" + eth0Power
								+ "</td><td class=\"panelLabel\">"
								+ eth1PowerLabel + "</td><td>" + eth1Power
								+ "</td</tr>";
						content += "<tr><td class=\"panelLabel\">"
								+ eth0SpeedLabel + "</td><td>" + eth0Speed
								+ "</td><td class=\"panelLabel\">"
								+ eth1SpeedLabel + "</td><td>" + eth1Speed
								+ "</td</tr>";
						content += "</table>";
						jsonObject.put("msg", content);
					}
				}
			}
		} catch (Exception e) {
			log.error("fetchPoeStatus", "error occored.", e);
			jsonObject.put("msg", "fetch PoE status data error.");
		}
		return jsonObject;
	}

	private void removeCachedStats() {
		MgrUtil.removeSessionAttribute(HIVE_AP_CLIENT_INFOS);
		MgrUtil.removeSessionAttribute(HIVE_AP_CLIENT_OUI_INFOS);
		MgrUtil.removeSessionAttribute(HIVE_AP_INTERFERENCE_WIFI0_INFOS);
		MgrUtil.removeSessionAttribute(HIVE_AP_INTERFERENCE_WIFI1_INFOS);
		removeBrSessionFlashData();
		removeCVGSessionFlashData();
	}

	public void fetchHiveApInfo(HiveAp hiveAp) {
		if (hiveAp == null) {
			return;
		}
		queryHiveApCpuMenInfo(hiveAp);
		prepareHiveApCpuMenStats(hiveAp);
		if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER) {
			prepareHiveApClientCountStats(hiveAp);
			if (this.isBlnSwitchAsBr()) {
				prepareHiveApClientOui(hiveAp);
			} else {
				prepareHiveApAcspNeighbor(hiveAp);
				prepareHiveApInterference(hiveAp);
			}
			if (!hasCalculateBrFlashData()) {
				prepareVpnAvailablity(hiveAp);
				prepareVpnThroughput(hiveAp);
				prepareVpnLatency(hiveAp);
				setCalculateBrFlashData2Session();
			}
		} else if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY) {
			if (!hasCalculateCVGFlashData()) {
				prepareCVGWanAvailability(hiveAp);
				prepareCVGWanThroughput(hiveAp);
				prepareCVGVpnAvailability(hiveAp);
				setCalculateCVGFlashData2Session();
			}
		} else if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_SWITCH) {
			prepareHiveApClientCountStats(hiveAp);
			prepareHiveApClientOui(hiveAp);
		} else {
			if (hiveAp.isCVGAppliance()) {
				return;
			}
			prepareHiveApClientCountStats(hiveAp);
			prepareHiveApAcspNeighbor(hiveAp);
			prepareHiveApInterference(hiveAp);
			prepareHiveApClientOui(hiveAp);
		}
	}
	
	private long printTime(long startTime, String method) {
		long endTime = System.currentTimeMillis();
		if (startTime > 0) {
			int seconds = (int)((endTime-startTime)/1000);
			int mSeconds = (int)((endTime-startTime)%1000);
			log.info("after " +  method+ ", cost time ====> " + seconds + "s " + mSeconds +"ms");
		}
		return endTime;
	}

	public void prepareDetails(HiveAp hiveAp) throws Exception {
		if (null == hiveAp) {
			return;
		}
		Long apId = hiveAp.getId();
		String apName = hiveAp.getHostName();
		String apMac = hiveAp.getMacAddress();
		String apIp = hiveAp.getIpAddress();
		String apModel = hiveAp.getProductName();
		String apSw = hiveAp.getDisplayVer();
		long apUpTime = hiveAp.isConnected() ? hiveAp.getUpTime() : 0;
		String apTopology = hiveAp.getTopologyName();
		String apStatus = getStatusIcon(hiveAp.getSeverity(), request
				.getContextPath());
		String apOrigin = hiveAp.getOriginString();
		String apManageStatus = hiveAp.getManageStatusString();
		String apType = HiveAp.getDeviceEnumString(hiveAp.getHiveApModel(), hiveAp.getDeviceType());
		String apWanIp = hiveAp.getCapwapClientIp();

		// for bug: 17625. use AP instead of HiveAP
		if (!StringUtils.isBlank(apModel)) {
			apModel = apModel.replaceFirst("HiveAP", "AP");
		}
		
		id = apId;
		domainId = hiveAp.getOwner().getId();
		domainName = hiveAp.getOwner().getDomainName();
		name = apName;
		macAddress = apMac == null ? "N/A" : apMac;
		ipAddress = apIp == null ? "N/A" : apIp;
		externalIpAddress = apWanIp == null ? "N/A" : apWanIp;
		model = apModel == null ? "N/A" : apModel;
		sw = apSw == null ? "N/A" : apSw;
		origin = apOrigin == null ? "N/A" : apOrigin;
		status = apStatus;
		manageStatus = apManageStatus;
		deviceType = apType == null ? "N/A" : apType;
		if (apUpTime > 0) {
			upTime = NmsUtil
					.transformTime((int) ((System.currentTimeMillis() - apUpTime) / 1000));
		}
		topologyMap = apTopology;
		if (null != topologyMap && !"".equals(topologyMap) 
				 && null!=hiveAp.getMapContainer()) {
			mapId = hiveAp.getMapContainer().getId();
			inTopology = true;
		}

		eth0DeviceId = hiveAp.getEth0DeviceIdString();
		eth0PortId = hiveAp.getEth0PortIdString();
		eth1DeviceId = hiveAp.getEth1DeviceIdString();
		eth1PortId = hiveAp.getEth1PortIdString();
		eth0SystemId = hiveAp.getEth0SystemIdString();
		eth1SystemId = hiveAp.getEth1SystemIdString();
		// get BR200-LTE-VZ by macAddress
		if(!StringUtils.isBlank(apMac) && hiveAp.isUsbAsCellularModem() 
				&& hiveAp.getConnectStatus() == HiveAp.CONNECT_UP){
			ahRouter=QueryUtil.findBoByAttribute(AhRouterLTEVZInfo.class, "mac", apMac);
		}
		if(!hiveAp.isSwitch()){
			prepareWanDeviceData(hiveAp);
		}
		prepareRadioInfo(hiveAp);
		prepareNeighborInfo(apMac);
		prepareClientInfo(apMac);
		//just use user profile name sent by HiveOS
		/*if (blnBranchRouter || blnSwitch || blnSwitchAsBr) {
			updateClientListInfo();
		}*/
		prepareIdpInfo(hiveAp);
		prepareEventInfo(apMac);
		prepareLldpInformation(hiveAp);
		
		if (blnSwitchAsBr || hiveAp.getDeviceType() == HiveAp.Device_TYPE_SWITCH) {
			// must put before normal BR
			long timeTmp = printTime(0, "");
			
			if (blnSwitchAsBr) {
				prepareNetworkDetails4Br(hiveAp, blnSwitchAsBr);
			}
			prepareBrMoreDetails(hiveAp);
			timeTmp = printTime(timeTmp, "prepareBrMoreDetails");
			// for SW only
			prepareSystemInfos(hiveAp);
			timeTmp = printTime(timeTmp, "prepareSystemInfos");
			
			prepareSwPortDetails(hiveAp);
			setPortGroup(hiveAp.getPortGroup());
			prepareDataForUpdatePortStatus(hiveAp);
		} else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER) {
			prepareNetworkDetails4Br(hiveAp, false);
			prepareBrPortDetails(hiveAp);
			prepareBrMoreDetails(hiveAp);
		} else if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY) {
			prepareVpnGatewayDeviceInterface(hiveAp);
			prepareVpnGatewayBranchClients(hiveAp);
			prepareVpnGatewayTopologyMap(hiveAp);
		}
	}


	private void prepareWanDeviceData(HiveAp hiveAp){
		String searchSQL = "lower(mac)=:s1 AND interfType=:s2 ";
		Object values[] = new Object[2];
		values[0] = hiveAp.getMacAddress().toLowerCase();
		values[1] = AhPortAvailability.INTERFACE_TYPE_WAN;
		List<?> lstPortInfo = QueryUtil.executeQuery(AhPortAvailability.class, null,
				new FilterParams(searchSQL, values));
		DeviceInterface pInf=hiveAp.getOrderWanInterface(0);
		String wanStatusImg="";
		String wanPortName="";
		if(null!=pInf){
			wanPortName=getWanPortName(pInf,hiveAp);
			pInf.setWanPortName(wanPortName);
			String pInfIpAddress = getWanIpAddress(hiveAp,pInf,lstPortInfo);
			pInf.setIpAddress(pInfIpAddress);
			pInf.setWanOrder(1);
			wanStatusImg=getWanStatusImg(pInf,lstPortInfo);
			pInf.setWanStatusImg(wanStatusImg);
			pInf.setWanStatusImgAlt(getImageAltInfo(wanStatusImg));
			wanDeviceInterfaceList.add(pInf);
		}
		DeviceInterface backup1Inf=hiveAp.getOrderWanInterface(1);
		if(null!=backup1Inf){
			wanPortName=getWanPortName(backup1Inf,hiveAp);
			backup1Inf.setWanPortName(wanPortName);
			String b1InfIpAddress = getWanIpAddress(hiveAp,backup1Inf,lstPortInfo);
			backup1Inf.setIpAddress(b1InfIpAddress);
			backup1Inf.setWanOrder(2);
			wanStatusImg=getWanStatusImg(backup1Inf,lstPortInfo);
			backup1Inf.setWanStatusImg(wanStatusImg);
			backup1Inf.setWanStatusImgAlt(getImageAltInfo(wanStatusImg));
			wanDeviceInterfaceList.add(backup1Inf);
		}
		DeviceInterface backup2Inf=hiveAp.getOrderWanInterface(2);
		if(null!=backup2Inf){
			wanPortName=getWanPortName(backup2Inf,hiveAp);
			backup2Inf.setWanPortName(wanPortName);
			String b2InfIpAddress = getWanIpAddress(hiveAp,backup2Inf,lstPortInfo);
			backup2Inf.setIpAddress(b2InfIpAddress);
			backup2Inf.setWanOrder(3);
			wanStatusImg=getWanStatusImg(backup2Inf,lstPortInfo);
			backup2Inf.setWanStatusImg(wanStatusImg);
			backup2Inf.setWanStatusImgAlt(getImageAltInfo(wanStatusImg));
			wanDeviceInterfaceList.add(backup2Inf);
		}
		for(DeviceInterface dInf:wanDeviceInterfaceList){
			if(AhInterface.DEVICE_IF_TYPE_USB==dInf.getDeviceIfType() 
				    && hiveAp.isUsbAsCellularModem()){
				dInf.setWanPortName(MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.port.cellularmodem"));
				cellularDevice=dInf;
	     	}
		}
		//set cellular Wan in the last
		if(null!=cellularDevice){
			wanDeviceInterfaceList.remove(cellularDevice);
			if(hiveAp.isEnableCellularModem()){
				wanDeviceInterfaceList.add(cellularDevice);
				//if device is BR200_LTE_VZ and usb as CellularDevice
				prepareWanDataRate(hiveAp);
			}else{
				cellularDevice=null;
			}
		}
	}
	
  private void prepareWanDataRate(HiveAp hiveAp){
	    String searchSQL = "lower(mac)=:s1 and interfType=:s2 and (interfName=:s3 or interfName=:s4) order by time desc";
		Object values[] = new Object[4];
		values[0] = hiveAp.getMacAddress().toLowerCase();
		values[1] = AhPortAvailability.INTERFACE_TYPE_WAN;
		values[2] = DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_USB_3G;
		values[3] = DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_USB_4G;
		List<AhStatsThroughputHigh> lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputHigh.class, null,
				new FilterParams(searchSQL, values),60);
	   long dwCount=0;
	   long upCount=0;
		for(AhStatsThroughputHigh OneRec:lstInterfaceInfo){
			dwCount+=OneRec.getRxBytes();
			upCount+=OneRec.getTxBytes();
		}
		this.dwThroughput=dwCount;
		this.upThroughput=upCount;
  }
	private String getWanPortName(DeviceInterface dInterface,HiveAp hiveAp){
		String wanPortName="";
		if(HiveAp.isSwitchProduct(hiveAp.getHiveApModel())){
			wanPortName=DeviceInterface.getPSwitchDisplayLabel(dInterface.getDeviceIfType());
		}else{
			wanPortName=DeviceInterface.getPbrDisplayLabel(dInterface.getDeviceIfType());
		}
		return wanPortName;
	}
	
	private String getWanIpAddress(HiveAp hiveAp,DeviceInterface dInterface,List<?> lstPortInfo) {
		String ipAddress = "0.0.0.0";
		for(Object oneRec : lstPortInfo) {
			AhPortAvailability ahPort = (AhPortAvailability)oneRec;
			String wanPortName=dInterface.getWanPortName();
			if(AhInterface.DEVICE_IF_TYPE_USB==dInterface.getDeviceIfType()){
				wanPortName=MgrUtil.getUserMessage("hiveAp.router.br.deviceInterface.devicePort.name.usb");
			}
			if (wanPortName.equalsIgnoreCase(ahPort.getInterfName())) {
				ipAddress = AhDecoder.int2IP(ahPort.getWanipaddress());
				break;
			}
		}
		return ipAddress;
	}
	
	public static String getWanPortLabel(HiveAp hiveAp,short deviceIfType){
		if(hiveAp.getDeviceType() != HiveAp.Device_TYPE_BRANCH_ROUTER){
			return "";
		}
		if(hiveAp.isSwitchProduct()){
			if(deviceIfType == AhInterface.DEVICE_IF_TYPE_USB){
				return MgrUtil.getUserMessage("hiveAp.router.br.deviceInterface.devicePort.name.usb");
			}
			String searchWord = "enum.switch.interface."+ deviceIfType;
			return MgrUtil.getEnumString(searchWord);
		}else {
			switch(deviceIfType){
		case AhInterface.DEVICE_IF_TYPE_USB:
			return MgrUtil.getUserMessage("hiveAp.router.br.deviceInterface.devicePort.name.usb");
		case AhInterface.DEVICE_IF_TYPE_WIFI0:
			return MgrUtil.getUserMessage("hiveAp.router.br.deviceInterface.devicePort.name.wifi0");
		case AhInterface.DEVICE_IF_TYPE_WIFI1:
			return MgrUtil.getUserMessage("hiveAp.router.br.deviceInterface.devicePort.name.wifi1");
		case AhInterface.DEVICE_IF_TYPE_ETH0:
			return MgrUtil.getUserMessage("hiveAp.router.br.deviceInterface.devicePort.name.eth0");
		case AhInterface.DEVICE_IF_TYPE_ETH1:
			return MgrUtil.getUserMessage("hiveAp.router.br.deviceInterface.devicePort.name.eth1");
		case AhInterface.DEVICE_IF_TYPE_ETH2:
			return MgrUtil.getUserMessage("hiveAp.router.br.deviceInterface.devicePort.name.eth2");
		case AhInterface.DEVICE_IF_TYPE_ETH3:
			return MgrUtil.getUserMessage("hiveAp.router.br.deviceInterface.devicePort.name.eth3");
		case AhInterface.DEVICE_IF_TYPE_ETH4:
			return MgrUtil.getUserMessage("hiveAp.router.br.deviceInterface.devicePort.name.eth4");
			}
			
		}
		return "";
	}

	private boolean inTopology;

	public boolean isInTopology() {
		return inTopology;
	}

	private void prepareIdpInfo(HiveAp hiveAp) {
		if (null == hiveAp) {
			return;
		}
		idpList = QueryUtil.executeQuery(Idp.class, null, new FilterParams(
				"reportNodeId", hiveAp.getMacAddress()));
		IdpPagingCache.queryLocationInfo(hiveAp.getOwner(), idpList);
		String userTimeZone = userContext.getTimeZone();
		IdpAction.setUserTimeZone(idpList, userTimeZone);
	}

	private void prepareEventInfo(String hostMac) {
		if (null == hostMac) {
			return;
		}
		HmUser hmUser = null;
		eventList = QueryUtil.executeQuery(AhEvent.class, new SortParams("id",
				false), new FilterParams("apId", hostMac), hmUser, 5);
		String userTimeZone = userContext.getTimeZone();
		AhEventsAction.setUserTimeZone(eventList, userTimeZone);
	}

	private void prepareRadioInfo(HiveAp hiveAp) {
		if (null == hiveAp) {
			return;
		}
		String hostMac = hiveAp.getMacAddress();
		List<AhLatestXif> radioList = QueryUtil.executeQuery(AhLatestXif.class,
				null, new FilterParams("apMac", hostMac));
		List<AhLatestRadioAttribute> attributeList = QueryUtil.executeQuery(
				AhLatestRadioAttribute.class, null, new FilterParams("apMac",
						hostMac));

		wifi0_ssids = new ArrayList<String>();
		wifi1_ssids = new ArrayList<String>();
		for (AhLatestXif xif : radioList) {
			String ifName = xif.getIfName();
			if (null == ifName || "".equals(ifName.trim())) {
				continue;
			}
			ifName = ifName.toLowerCase();
			if ("wifi0".equals(ifName)) {
				// wifi0 interface
				setWifi0_index(xif.getIfIndex());
				setWifi0_name(xif.getIfName());
				setWifi0_mode(xif.getIfModeString());
				//wifi0_ifMode
				setWifi0_ifMode(xif.getIfMode());
				// setWifi0_type("802.11g");
			} else if ("wifi1".equals(ifName)) {
				// wifi1 interface
				setWifi1_index(xif.getIfIndex());
				setWifi1_name(xif.getIfName());
				setWifi1_mode(xif.getIfModeString());
				//wifi1ifMode
				setWifi1_ifMode(xif.getIfMode());
				// setWifi1_type("802.11a");
			} else if (ifName.startsWith("wifi0.")) {
				// wifi0 virtual interface
				String ssid = xif.getSsidName();
				if (null != ssid && !ssid.trim().isEmpty()
						&& !"N/A".equals(ssid.trim())) {
					wifi0_ssids.add(ssid);
				}
			} else if (ifName.startsWith("wifi1.")) {
				// wifi1 virtual interface
				String ssid = xif.getSsidName();
				if (null != ssid && !ssid.trim().isEmpty()
						&& !"N/A".equals(ssid.trim())) {
					wifi1_ssids.add(ssid);
				}
			}
		}

		for (AhLatestRadioAttribute attribute : attributeList) {
			int index = attribute.getIfIndex();
			if (index == getWifi0_index()) {
				if(wifi0_ifMode==AhXIf.IFMODE_SENSOR){
					setWifi0_channel("N/A");
				}else{
					setWifi0_channel(String.valueOf(attribute.getRadioChannel()));
				}
				setWifi0_power(String.valueOf(attribute.getRadioTxPower())
						+ " dBm");
				setWifi0_noise(String.valueOf(attribute.getRadioNoiseFloor())
						+ " dBm");
				setWifi0_eirp(String.valueOf(attribute.getEirp())
						+ " dBm");
				setWifi0_name("wifi0");
				// wifi name based on binding radio profile
				RadioProfile wifi0Profile = hiveAp.getWifi0RadioProfile();
				setWifi0_type(this.getCertainRadioType(attribute, hiveAp, wifi0Profile));
				this.setWifi0_type_tip(this.getCertainRadioTypeTip(attribute, hiveAp));
				if (null != wifi0Profile) {
					setWifi0RateStr(getDataRatePerSSID_Wifi0(wifi0Profile, hiveAp));
				}
			} else if (index == getWifi1_index()) {
				if(wifi1_ifMode==AhXIf.IFMODE_SENSOR){
					setWifi1_channel("N/A");
				}else{
					setWifi1_channel(String.valueOf(attribute.getRadioChannel()));
				}
				setWifi1_power(String.valueOf(attribute.getRadioTxPower())
						+ " dBm");
				setWifi1_noise(String.valueOf(attribute.getRadioNoiseFloor())
						+ " dBm");
				setWifi1_eirp(String.valueOf(attribute.getEirp())
						+ " dBm");
				setWifi1_name("wifi1");
				// wifi name based on binding radio profile
				RadioProfile wifi1Profile = hiveAp.getWifi1RadioProfile();
				setWifi1_type(this.getCertainRadioType(attribute, hiveAp, wifi1Profile));
				this.setWifi1_type_tip(this.getCertainRadioTypeTip(attribute, hiveAp));
				if (null != wifi1Profile) {
					setWifi1RateStr(getDataRatePerSSID_Wifi1(wifi1Profile, hiveAp));
				}
			}
		}
	}
	
	private String getRadioTypeString(short radioType) {
		return MgrUtil.getEnumString("enum.radioProfileMode." + radioType);
	}
	private String getCertainRadioType(AhLatestRadioAttribute attribute, HiveAp hiveAp, RadioProfile wifiProfile) {
		String result = MgrUtil.getUserMessage("monitor.hiveAp.radio.attribute.type.not.available.text.unreported");
		
		if (attribute.getRadioType() != AhRadioAttribute.RADIO_TYPE_INVALID) {
			result = this.getRadioTypeString(attribute.getRadioType());
		} else {
			if (!hiveAp.isEnableDynamicBandSwitch()
					&& wifiProfile != null) {
				result = this.getRadioTypeString(wifiProfile.getRadioMode());
			}
		}
		
		return result;
	}
	private String getCertainRadioTypeTip(AhLatestRadioAttribute attribute, HiveAp hiveAp) {
		String result = "";
		
		if (attribute.getRadioType() == AhRadioAttribute.RADIO_TYPE_INVALID
				&& hiveAp.isEnableDynamicBandSwitch()) {
			result = MgrUtil.getUserMessage("monitor.hiveAp.radio.attribute.type.not.available.tip");;
		}
		
		return result;
	}
	
	
	private String getDataRatePerSSID_Wifi0(RadioProfile wifi0Profile, HiveAp hiveAp){
		boolean dupRateFlag = false;
		double singleRate = 0,ssidRate = 0;
		StringBuffer sbf = new StringBuffer();
		for(int i = 0; i < wifi0_ssids.size(); i++){
			ssidRate = getRadioDateRate(wifi0_ssids.get(i), hiveAp, wifi0Profile, false);
			if(ssidRate != 0){
				sbf.append(ssidRate + " Mbps" +" ("+wifi0_ssids.get(i)+")");
				if(i != wifi0_ssids.size() -1){
					sbf.append(",");
				}
			}
				
			if(i == 0){				
				singleRate = ssidRate;
			}else{
				if(singleRate != ssidRate){
					dupRateFlag = true; 
				}
			}	
		}
		return dupRateFlag ? sbf.toString() : singleRate + " Mbps";
	}
	
	private String getDataRatePerSSID_Wifi1(RadioProfile wifi1Profile, HiveAp hiveAp){
		boolean dupRateFlag = false;
		double singleRate = 0,ssidRate = 0;
		StringBuffer sbf = new StringBuffer();
		for(int i = 0; i < wifi1_ssids.size(); i++){
			ssidRate = getRadioDateRate(wifi1_ssids.get(i), hiveAp, wifi1Profile, true);
			if(ssidRate != 0){
				sbf.append(ssidRate + " Mbps" +" ("+wifi1_ssids.get(i)+")");
				if(i != wifi1_ssids.size() -1){
					sbf.append(",");
				}
			}
				
			if(i == 0){				
				singleRate = ssidRate;
			}else{
				if(singleRate != ssidRate){
					dupRateFlag = true; 
				}
			}	
		}
		return dupRateFlag ? sbf.toString() : singleRate + " Mbps";
	}
	
	private double getRadioDateRate(String ssidName, HiveAp hiveap, RadioProfile radioProfile, boolean isWifi1){
		short radioMode = radioProfile.getRadioMode();
		short channelWidth = isOnlySpt20MUnder24G(hiveap, radioProfile) ? RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20 : radioProfile.getChannelWidth();
		boolean guardInterval = radioProfile.isGuardInterval();
		boolean vhtEnable = radioProfile.isEnableVHT();
		int steramType = 0;
		switch(radioMode){
			case RadioProfile.RADIO_PROFILE_MODE_BG:
			case RadioProfile.RADIO_PROFILE_MODE_A:
				return 54;
			case RadioProfile.RADIO_PROFILE_MODE_NA:
			case RadioProfile.RADIO_PROFILE_MODE_NG:
				steramType = getMCSStreamType(ssidName, hiveap, isWifi1, radioMode);
				switch(channelWidth){
				case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20:
					if (vhtEnable) {
						switch (steramType) {
						case 1:return guardInterval ? 78 : 86.7;
						case 2:return guardInterval ? 156 : 173.3;
						case 3:return guardInterval ? 260 : 288.9;
						default:return 0;
						}
					} else {
						switch (steramType) {
						case 1:return guardInterval ? 72.2 : 65;
						case 2:return guardInterval ? 144.4 : 130;
						case 3:return guardInterval ? 216.6 : 195;
						default:return 0;
						}
					}
				case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40:
				case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A:
				case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40B:
					 switch(steramType){
						 case 1: return guardInterval ? 150 : 135;								 	
						 case 2: return guardInterval ? 300 : 270;
						 case 3: return guardInterval ? 450 : 405;
						 default: return 0;
					 }
				default: return 0;
			}
			case RadioProfile.RADIO_PROFILE_MODE_AC:
				steramType = getMCSStreamType(ssidName, hiveap, isWifi1, radioMode);
				switch(channelWidth){
					case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20:
						 switch(steramType){
							 case 1: return guardInterval ? 86.7 : 78;								 	
							 case 2: return guardInterval ? 173.3 : 156;
							 case 3: return guardInterval ? 288.9 : 260;
							 default: return 0;
						 }
					case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40:
						 switch(steramType){
							 case 1: return guardInterval ? 200 : 180;								 	
							 case 2: return guardInterval ? 400 : 360;
							 case 3: return guardInterval ? 600 : 540;
							 default: return 0;
						 }
					case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80:
						 switch(steramType){
							 case 1: return guardInterval ? 433.3 : 390;								 	
							 case 2: return guardInterval ? 866.7 : 780;
							 case 3: return guardInterval ? 1170 : 1300;
							 default: return 0;
						 }
					default: return 0;
				}
			default: return 0;
		}
	}
	
	// only AP120/110/170/320/340 2.4G radio support 40M.
	private boolean isOnlySpt20MUnder24G(HiveAp hiveAp, RadioProfile radioProfile){
		return hiveAp.is11nHiveAP() && 
				(radioProfile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA ||
				(radioProfile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG 
				&& !hiveAp.getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_40M_FOR_24G)));
	}
	
	private int getMCSStreamType(String ssidName, HiveAp hiveap, boolean isWifi1, short radioMode){
		if(hiveap.getConfigTemplate() == null) return 0;
		Map<Long, ConfigTemplateSsid> configSSIDMap = hiveap.getConfigTemplate().getSsidInterfaces();
		int maxStreamType = 0;
		SsidProfile ssidProfile = null;
		for(ConfigTemplateSsid ssid : configSSIDMap.values()){
			ssidProfile = ssid.getSsidProfile();
			if(ssidProfile != null && ssidName.trim().equalsIgnoreCase(ssid.getSsidProfile().getSsid().trim())){
				break;
			}
		}
		if(ssidProfile == null 
				|| (!isWifi1 && (ssidProfile.getRadioMode() != SsidProfile.RADIOMODE_BG && ssidProfile.getRadioMode() != SsidProfile.RADIOMODE_BOTH))
				|| (isWifi1 && (ssidProfile.getRadioMode() != SsidProfile.RADIOMODE_A && ssidProfile.getRadioMode() != SsidProfile.RADIOMODE_BOTH))) 
			return maxStreamType;
		
		switch(radioMode){
			case RadioProfile.RADIO_PROFILE_MODE_NA:
			case RadioProfile.RADIO_PROFILE_MODE_NG:
				 int nRateStreamType = get11nStreamType(ssidProfile.getNRateSets());
				 maxStreamType = maxStreamType < nRateStreamType ? nRateStreamType : maxStreamType;
				 break;
			case RadioProfile.RADIO_PROFILE_MODE_AC:
				for(Tx11acRateSettings rateSetting : ssidProfile.getAcRateSets()){
					if(!rateSetting.isStreamEnable()) continue;
					switch(rateSetting.getStreamType()){
						case Tx11acRateSettings.STREAM_TYPE_THREE:
							 return 3;
						case Tx11acRateSettings.STREAM_TYPE_TWO:
							 maxStreamType = 2;
							 break;
						case Tx11acRateSettings.STREAM_TYPE_SINGLE:
							 if(maxStreamType < 1) maxStreamType = 1;
							 break;
						default: continue;
					}
				}
				break;
			default: return maxStreamType;
		}
		return maxStreamType;
	}
	
	private int get11nStreamType(Map<String, TX11aOr11gRateSetting> nRateSets){
		TX11aOr11gRateSetting tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.twenty_three.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 3;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.twenty_two.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 3;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.twenty_one.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 3;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.twenty.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 3;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.nineteen.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 3;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.eighteen.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 3;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.seventeen.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 3;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.sixteen.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 3;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.fifteen.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 2;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.fourteen.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 2;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.thirteen.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 2;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.twelve.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 2;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.eleven.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 2;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.ten.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 2;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.nine.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 2;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.eight.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 2;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.seven.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 1;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.six.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 1;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.five.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 1;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.four.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 1;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.three.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 1;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.two.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 1;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.one.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 1;
		}
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.zero.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			return 1;
		}
		return 0;
	}

	private void prepareNeighborInfo(String hostMac) {
		if (null == hostMac) {
			return;
		}
		neighborList = QueryUtil.executeQuery(AhLatestNeighbor.class, null,
				new FilterParams("apMac", hostMac));
		
		// add ifName of neighbor;
		if(!neighborList.isEmpty()) {
            List<?> ifIndexNames = QueryUtil.executeQuery(
                    "SELECT bo.ifIndex, bo.ifName FROM " + AhLatestXif.class.getSimpleName() + " bo", null,
                    new FilterParams("apMac", hostMac));
            for(AhLatestNeighbor neighbor:neighborList) {
                for(Object object:ifIndexNames) {
                    Object[] attributes = (Object[]) object;
                    if(Integer.valueOf(String.valueOf(attributes[0])).equals(neighbor.getIfIndex())) {
                        neighbor.setIfName(String.valueOf(attributes[1]));
                        break;
                    }
                }
            }
		}
		
		
		// add hostname of neighbor;
		if (!neighborList.isEmpty()) {
			Set<String> macAddresses = new HashSet<String>();
			for (AhLatestNeighbor neighbor : neighborList) {
				macAddresses.add(neighbor.getNeighborAPID());
			}
			List<?> hostNames = QueryUtil.executeQuery(
					"select bo.macAddress, bo.hostName from " + HiveAp.class.getSimpleName() + " bo", null,
					new FilterParams("macAddress", macAddresses));
			Map<String, String> map = new HashMap<String, String>();
			for (Object object : hostNames) {
				Object[] attributes = (Object[]) object;
				map.put((String) attributes[0], (String) attributes[1]);
			}

			for (AhLatestNeighbor neighbor : neighborList) {
				String hostname = map.get(neighbor.getNeighborAPID());
				if (null == hostname) {
					neighbor.setHostName("-");
				} else {
					neighbor.setHostName(hostname);
				}
			}
		}
	}

	private void prepareClientInfo(String hostMac) {
		if (null == hostMac) {
			return;
		}
		String where = "apMac = ? AND connectstate= ?";
		Object values[] = new Object[2];
		values[0] = hostMac;
		values[1] = AhClientSession.CONNECT_STATE_UP;
		FilterParams filterParams = new FilterParams(where, values);
//		clientList = QueryUtil.executeQuery(AhClientSession.class, null,
//				filterParams);
		clientList = DBOperationUtil.executeQuery(AhClientSession.class, null,
				filterParams);
		String userTimeZone = userContext.getTimeZone();
		ClientMonitorAction.setUserTimeZone(clientList, userTimeZone);
	}

	//just use user profile name sent by HiveOS
	/*private void updateClientListInfo() {
		if (clientList != null && clientList.size() > 0) {
			for(AhClientSession client : clientList) {
				client.setClientUserProfileName("");
				if (client.getClientUserProfId() > 0) {
					UserProfile userProfile = QueryUtil.findBoById(UserProfile.class, AhDecoder.int2long(client.getClientUserProfId()));
					if (userProfile != null) {
						client.setClientUserProfileName(userProfile.getUserProfileName());
					}
				}
			}
		}
	}*/

	public boolean getIs11nAp() {
		HiveAp hiveAp = getDataSource();
		return null != hiveAp && hiveAp.is11nHiveAP();
	}

	public boolean getHasPoEFeature() {
		HiveAp hiveAp = getDataSource();
		//now, only specially support AP320&AP340
		//return null != hiveAp && hiveAp.isPoEAvailable();
		if (hiveAp != null) {
			if (HiveAp.is320HiveAP(hiveAp.getHiveApModel())
					||HiveAp.is340HiveAP(hiveAp.getHiveApModel())) {
				return true;
			}
		}
		return false;
	}

	public boolean getHasLldpCdpFeature() {
		HiveAp hiveAp = getDataSource();
		return null != hiveAp
				&& NmsUtil.compareSoftwareVersion("3.4.0.0", hiveAp
						.getSoftVer()) < 0;
	}

	// radio detail table grid count
	public int getGridCount_r() {
		return 1;
	}

	// neighbor detail table grid count
	public int getGridCount_n() {
		if (null == neighborList || neighborList.isEmpty()) {
			return 3;
		} else {
			return 1;
		}
	}

	// neighbor detail table grid count (lldp show in neighbor table)
	public int getGridCount_lldp() {
		if (null == lldpInformationList || lldpInformationList.isEmpty()) {
			return 3;
		} else {
			return 1;
		}
	}

	// network details
	public int getGridCount_d() {
		if (null == networkList || networkList.isEmpty()) {
			return 3;
		} else {
			return 1;
		}
	}

	// client detail table grid count
	public int getGridCount_c() {
		if (null == clientList || clientList.isEmpty()) {
			return 3;
		} else {
			return 1;
		}
	}

	// idp detail table grid count
	public int getGridCount_i() {
		if (null == idpList || idpList.isEmpty()) {
			return 3;
		} else {
			return 1;
		}
	}

	public int getGridCount_e() {
		if (null == eventList || eventList.isEmpty()) {
			return 3;
		} else {
			return 1;
		}
	}

	// SW port details
	public int getGridCount_p() {
		if (null == swPortStatsList || swPortStatsList.isEmpty()) {
			return 3;
		} else {
			return 1;
		}
	}

	public static String getConnectionIcon(short connectStatus, String contextPath, int delay, String disDate) {
		String delayStr = null;
		String disDateStr = null;
		if(delay < 0){
			delayStr = "Connected";
		}else if(delay == 0){
			delayStr = "Delay 0 ms";
		}else{
			delayStr = "Delay " + String.valueOf((float)delay/1000) + " ms";
		}
		if(disDate == null || "".equals(disDate)){
			disDateStr = "Disconnected";
		}else{
			disDateStr = "Disconnected at "+disDate;
		}
		if (connectStatus == HiveAp.CONNECT_UP) {
			return "<img title='"+delayStr+"' src='" + contextPath
					+ "/images/HM-capwap-up.png" + "' hspace=2 class='dinl'/>";
		} else if(connectStatus == HiveAp.CONNECT_UP_MINOR) {
			return "<img title='"+delayStr+"' src='" + contextPath
					+ "/images/hm-capwap-warn1.png" + "' hspace=2 class='dinl'/>";
		} else if(connectStatus == HiveAp.CONNECT_UP_MAJOR) {
			return "<img title='"+delayStr+"' src='" + contextPath
			+ "/images/hm-capwap-warn2.png" + "' hspace=2 class='dinl'/>";
		} else {
			return "<img title='"+disDateStr+"' src='" + contextPath
					+ "/images/HM-capwap-down.png" + "' hspace=2 class='dinl'/>";
		}
	}

	public static String getConnectionIconEx(boolean conn, String contextPath) {
		if (conn) {
			return "<img title='Connected' src='" + contextPath
					+ "/images/hm/express/hm-capwap-up.png" + "' hspace=2 class='dinl'/>";
		} else {
			return "<img title='Disconnected' src='" + contextPath
					+ "/images/hm/express/hm-capwap-down.png" + "' hspace=2 class='dinl'/>";
		}
	}

	public static String getDtlsIcon(boolean dtls, String contextPath, boolean jsonMode) {
		if (dtls) {
			if (jsonMode) {
				return "<img title='DTLS enabled' src='" + contextPath
					+ "/images/dtls_enable_json.png" + "' hspace=2 class='dinl'/>";
			} else {
				return "<img title='DTLS enabled' src='" + contextPath
				+ "/images/dtls_enable.png" + "' hspace=2 class='dinl'/>";
			}
		} else {
			if (jsonMode) {
				return "<img title='DTLS disabled' src='" + contextPath
				+ "/images/dtls_disable_json.png" + "' hspace=2 class='dinl'/>";
			} else {
				return "<img title='DTLS disabled' src='" + contextPath
					+ "/images/dtls_disable.png" + "' hspace=2 class='dinl'/>";
			}
		}
	}
	
	public static String getDtlsBlankIcon(String contextPath) {
		return "<img width='10' src='" + contextPath
				+ "/images/spacer.gif" + "' hspace=2 class='dinl'/>";
	}

	public static String getStatusIcon(short severity, String contextPath) {
		if (severity == AhAlarm.AH_SEVERITY_UNDETERMINED) {
			return "<img title='Clear' src='" + contextPath
					+ "/images/status1.png" + "' border=0/>";
		} else if (severity == AhAlarm.AH_SEVERITY_MINOR) {
			return "<img title='Minor' src='" + contextPath
					+ "/images/status3.png" + "' border=0/>";
		} else if (severity == AhAlarm.AH_SEVERITY_MAJOR) {
			return "<img title='Major' src='" + contextPath
					+ "/images/status4.png" + "' border=0/>";
		} else if (severity == AhAlarm.AH_SEVERITY_CRITICAL) {
			return "<img title='Critical' src='" + contextPath
					+ "/images/status5.png" + "' border=0/>";
		} else {
			return "<img title='Unknown' src='" + contextPath
					+ "/images/status0.png" + "' border=0>";
		}
	}

	public static String getRADIUSStatusIcon(boolean isRadius,
			String contextPath) {
		if (isRadius) {
			return "<img title='"
					+ MgrUtil.getUserMessage("hiveAp.server.label.radius")
					+ "' src='" + contextPath + "/images/radius_server.png"
					+ "' class='dblk serverIcon'/>";
		} else {
			return getBlankIcon(contextPath);
		}
	}

	public static String getPPSKStatusIcon(boolean isPPSK, String contextPath){
		if(isPPSK){
			return "<img title='"
				+ MgrUtil.getUserMessage("hiveAp.server.label.ppsk")
				+ "' src='" + contextPath + "/images/ppsk_server.png"
				+ "' class='dblk serverIcon'/>";
		}else{
			return getBlankIcon(contextPath);
		}
	}

	public static String getIdmProxyStatusIcon(boolean isIdmProxy, String contextPath) {
		if(isIdmProxy){
			return "<img title='"
					+ MgrUtil
							.getUserMessage("hiveAp.server.label.idm.proxy")
					+ "' src='" + contextPath
					+ "/images/HM-icon-RADIUS_Proxy.png"
					+ "' class='dblk serverIcon'/>";
		}else{
			return getBlankIcon(contextPath);
		}
		
	}
	
	public static String getIdmAuthProxyStatusIcon(boolean isAuthProxy, String contextPath) {
		if(isAuthProxy){
			return "<img title='"
					+ MgrUtil
							.getUserMessage("hiveAp.server.label.idm.auth.proxy")
					+ "' src='" + contextPath
					+ "/images/hm-idm-auth-proxy.png"
					+ "' class='dblk serverIcon'/>";
		}else{
			return getBlankIcon(contextPath);
		}
		
	}
	
	public static String getProxyStatusIcon(boolean isProxy, String contextPath) {
		if (isProxy) {
			return "<img title='"
					+ MgrUtil
							.getUserMessage("hiveAp.server.label.radius.proxy")
					+ "' src='" + contextPath
					+ "/images/radius_proxy_server.png"
					+ "' class='dblk serverIcon'/>";
		} else {
			return getBlankIcon(contextPath);
		}
	}

	public static String getVPNStatusIcon(short vpnMark, VpnStatus vpnStatus,
			String contextPath) {
		if (vpnMark == HiveAp.VPN_MARK_SERVER) {
			if (VpnStatus.Up.equals(vpnStatus)) {
				return "<img title='"
						+ MgrUtil.getUserMessage("hiveAp.server.label.vpn")
						+ "' src='" + contextPath + "/images/vpn_server_up.png"
						+ "' class='dblk serverIcon'/>";
			} else {
				return "<img title='"
						+ MgrUtil.getUserMessage("hiveAp.server.label.vpn")
						+ "' src='" + contextPath + "/images/vpn_server.png"
						+ "' class='dblk serverIcon'/>";
			}
		} else if (vpnMark == HiveAp.VPN_MARK_CLIENT) {
			if (VpnStatus.Up.equals(vpnStatus)) {
				return "<img title='"
						+ MgrUtil.getUserMessage("hiveAp.client.label.vpn")
						+ "' src='" + contextPath + "/images/vpn_client_up.png"
						+ "' class='dblk serverIcon'/>";
			} else if (VpnStatus.Half.equals(vpnStatus)) {
				return "<img title='"
						+ MgrUtil.getUserMessage("hiveAp.client.label.vpn")
						+ "' src='" + contextPath
						+ "/images/vpn_client_half.png"
						+ "' class='dblk serverIcon'/>";
			} else {
				return "<img title='"
						+ MgrUtil.getUserMessage("hiveAp.client.label.vpn")
						+ "' src='" + contextPath + "/images/vpn_client.png"
						+ "' class='dblk serverIcon'/>";
			}
		} else {
			return getBlankIcon(contextPath);
		}
	}
	
	public static String getHostHtmlStr(HiveAp hiveAp, int iconCounts, boolean isShowDomain) {
		String htmlFormat = "<table border='0' cellspacing='0' cellpadding='0'>" + 
								"<tbody>" + 
									"<tr>" + 
										"%s" + 
										"%s" + 
									"</tr>" + 
								"</tbody>" + 
							"</table>";
		String iconFormat = "<td style='padding-right: 3px;'>%s</td>";
		String hostReadFormat = "<td class='textLink'>%s<span title='%s'>%s</span></td>";
		String hostFormat = "<td class='textLink'>" +
								"%s" + 
								"<a class='npcLinkA' onclick='editHiveApAction(%s);' href='javascript:void(0);'>" +
									"<span title='%s'>%s</span>" + 
								"</a>" + 
							"</td>";
		
		StringBuilder iconSb = new StringBuilder();
		if(iconCounts >= 1){
			iconSb.append(String.format(iconFormat, hiveAp.getIconItem1()));
		}
		if(iconCounts >= 2){
			iconSb.append(String.format(iconFormat, hiveAp.getIconItem2()));
		}
		if(iconCounts >= 3){
			iconSb.append(String.format(iconFormat, hiveAp.getIconItem3()));
		}
		if(iconCounts >= 4){
			iconSb.append(String.format(iconFormat, hiveAp.getIconItem4()));
		}
		if(iconCounts >= 5){
			iconSb.append(String.format(iconFormat, hiveAp.getIconItem5()));
		}
		if(iconCounts >= 6){
			iconSb.append(String.format(iconFormat, hiveAp.getIconItem6()));
		}
		
		StringBuilder hostnameSb = new StringBuilder();
		if(isShowDomain && !"home".equals(hiveAp.getOwner().getDomainName()) && 
				!"global".equals(hiveAp.getOwner().getDomainName()) ){
			hostnameSb.append(String.format(hostReadFormat, hiveAp.getDtlsIcon(), 
					hiveAp.getHostName(), hiveAp.getHostName()));
		}else{
			hostnameSb.append(String.format(hostFormat, hiveAp.getDtlsIcon(), hiveAp.getId(), 
					hiveAp.getHostName(), hiveAp.getHostName()));
		}
		
		return String.format(htmlFormat, iconSb.toString(), hostnameSb.toString());
	}

	public static String getDHCPStatusIcon(boolean isDhcp, String contextPath) {
		if (isDhcp) {
			return "<img title='"
					+ MgrUtil.getUserMessage("hiveAp.server.label.dhcp")
					+ "' src='" + contextPath + "/images/dhcp_server.png"
					+ "' class='dblk serverIcon'/>";
		} else {
			return getBlankIcon(contextPath);
		}
	}
	
	public static String getBlankIcon(String contextPath) {
		return "<img width='16' src='" + contextPath + "/images/spacer.gif"
				+ "' class='dblk'/>";
	}

	public static String getConfigIndicationIcon(boolean pending,
			int pendingIndex, String pendingMsg, boolean pending_user,
			int pendingIndex_user, String pendingMsg_user, String contextPath,
			Long id) {
		String stagSql = "select id from "+HiveAp.class.getSimpleName()+" as ap";
		String stagWhere = "ap.id = :s1 and exists(select id from " + HiveApUpdateResult.class.getSimpleName() + 
				" as rs where ap.macAddress = rs.nodeId and rs.result = :s2)";
		List<?> stageList = QueryUtil.executeQuery(stagSql, null, 
				new FilterParams(stagWhere, new Object[]{id, UpdateParameters.UPDATE_STAGED}));
		if(stageList != null && !stageList.isEmpty()){
			return "<img width='16' name='indication' alt='Staged' title='Staged' src='"
					+ contextPath + "/images/config-pending.png" + "' class='dblk'>";
		}else if (pending) {
			String desc = ConfigurationResources.getMismatchMessage(
					pendingIndex, pendingMsg, ConfigurationType.Configuration);
			desc = desc.replace("<", "\"").replace(">", "\"").replace("'",
					"&#39;");
			return "<img width='16' name='indication' alt='Mismatch' title='"
					+ desc
					+ "' src='"
					+ contextPath
					+ "/images/config-mismatch.png"
					+ "' class='dblk' style='cursor: pointer;' onclick='requestMismatchAudit("
					+ id + ")'>";
		} else if (pending_user) {
			String desc = ConfigurationResources.getMismatchMessage(
					pendingIndex_user, pendingMsg_user,
					ConfigurationType.UserDatabase);
			desc = desc.replace("<", "\"").replace(">", "\"").replace("'",
					"&#39;");
			return "<img width='16' name='indication' alt='Mismatch' title='"
					+ desc
					+ "' src='"
					+ contextPath
					+ "/images/config-mismatch.png"
					+ "' class='dblk' style='cursor: pointer;' onclick='requestMismatchAudit("
					+ id + ")'>";
		} else {
			return "<img width='16' name='indication' alt='Match' title='Matched' src='"
					+ contextPath + "/images/config-match.png" + "' class='dblk'>";
		}
	}

	/**
	 * get the active client count on the specify HiveAp.
	 *
	 * @param hiveAp
	 *            -
	 * @return -
	 */
	public static long getActiveClientCount(HiveAp hiveAp) {
		if (null == hiveAp) {
			return 0;
		}
//		String where = "apMac = :s1 AND owner.id = :s2 AND connectstate=:s3";
//		Object values[] = new Object[3];
//		values[0] = hiveAp.getMacAddress();
//		values[1] = hiveAp.getOwner().getId();
//		values[2] = AhClientSession.CONNECT_STATE_UP;
//		FilterParams f_params = new FilterParams(where, values);
//		return QueryUtil.findRowCount(AhClientSession.class, f_params);
		String where = "apMac = ? AND owner = ? AND connectstate=?";
		Object values[] = new Object[3];
		values[0] = hiveAp.getMacAddress();
		values[1] = hiveAp.getOwner().getId();
		values[2] = AhClientSession.CONNECT_STATE_UP;
		FilterParams f_params = new FilterParams(where, values);
		return DBOperationUtil.findRowCount(AhClientSession.class, f_params);
	}

	/* CPU and Memory static section */
	private List<String> cpuUsage;
	private List<String> memUsage;
	private String totalMem;
	private String freeMem;
	private String usedMem;
	private List<CheckItem> uniqueClients;
	private List<CheckItem> clientOuis;

	public List<CheckItem> getClientOuis() {
		return clientOuis;
	}

	public List<CheckItem> getUniqueClients() {
		return uniqueClients;
	}

	public List<String> getCpuUsage() {
		return cpuUsage;
	}

	public List<String> getMemUsage() {
		return memUsage;
	}

	public String getTotalMem() {
		if (totalMem == null) {
			return "0";
		}
		return totalMem;
	}

	public String getFreeMem() {
		if (freeMem == null) {
			return "0";
		}
		return freeMem;
	}

	public String getUsedMem() {
		if (usedMem == null) {
			return "0";
		}
		return usedMem;
	}

	private static final String HIVE_AP_INTERFERENCE_WIFI0_INFOS = "hive_ap_interference_stats_wifi0_infos";
	private static final String HIVE_AP_INTERFERENCE_WIFI1_INFOS = "hive_ap_interference_stats_wifi1_infos";
	private static final String HIVE_AP_CLIENT_INFOS = "hive_ap_client_stats_infos";
	private static final String HIVE_AP_CLIENT_OUI_INFOS = "hive_ap_client_oui_infos";
	private static final String HIVE_AP_CPU_MEM_INFOS = "hive_ap_cpu_mem_infos";

	private void prepareHiveApCpuMenStats(HiveAp hiveAp) {
		log.info("prepareHiveApCpuMenStats", "Preparing CPU and memory statistics for HiveAP " + hiveAp);
		Map<String, List<BeCPUMemInfoEvent>> infos = (Map<String, List<BeCPUMemInfoEvent>>) MgrUtil
				.getSessionAttribute(HIVE_AP_CPU_MEM_INFOS);
		cpuUsage = new ArrayList<String>(10);
		memUsage = new ArrayList<String>(10);
		if (null != infos && null != hiveAp) {
			List<BeCPUMemInfoEvent> list = infos.get(hiveAp.getMacAddress());
			if (null != list) {
				for (BeCPUMemInfoEvent event : list) {
					totalMem = String.valueOf(event.getTotalMem());
					freeMem = String.valueOf(event.getFreeMem());
					usedMem = String.valueOf(event.getUsedMem());
					float cpuPercentage = event.getCpuUsage1()
							+ event.getCpuUsage2() / 1000f;
					float memPercentage = event.getTotalMem() > 0 ? event
							.getUsedMem()
							* 100f / (event.getTotalMem()) : 0.0f;
					cpuUsage.add(String.valueOf(cpuPercentage));
					memUsage.add(String.valueOf(memPercentage));
				}
			}
		}
		while (cpuUsage.size() < 10) {
			cpuUsage.add(0, String.valueOf("0"));
		}
		while (memUsage.size() < 10) {
			memUsage.add(0, String.valueOf("0"));
		}
	}

	private void queryHiveApCpuMenInfo(HiveAp hiveAp) {
		log.info("queryHiveApCpuMenInfo", "Preparing CPU and memory information for HiveAP " + hiveAp);
		if (null != hiveAp) {
			try {
				String errorMsg = MapNodeAction.validateSelectedHiveAp(hiveAp,
						"3.4.1.0");
				if (null != errorMsg) {
					log.info("queryHiveApCpuMenInfo", errorMsg);
				} else {
					BeCPUMemInfoEvent event = new BeCPUMemInfoEvent();
					event.setAp(hiveAp);
					event.buildPacket();
					BeCPUMemInfoEvent result = (BeCPUMemInfoEvent) HmBeCommunicationUtil
							.sendSyncRequest(event, 30);

					if (null != result) {
						if (result.getResult() != BeCommunicationConstant.RESULTTYPE_SUCCESS) {
							log.error("queryHiveApCpuMenInfo",
									"query cpu memory info failed. result type:"
											+ result.getResult());
							return;
						}
						Map<String, List<BeCPUMemInfoEvent>> infos = (Map<String, List<BeCPUMemInfoEvent>>) MgrUtil
								.getSessionAttribute(HIVE_AP_CPU_MEM_INFOS);
						if (null == infos) {
							infos = new HashMap<String, List<BeCPUMemInfoEvent>>();
							MgrUtil.setSessionAttribute(HIVE_AP_CPU_MEM_INFOS,
									infos);
						}
						List<BeCPUMemInfoEvent> list = infos.get(hiveAp
								.getMacAddress());
						if (null == list) {
							list = new ArrayList<BeCPUMemInfoEvent>();
							infos.put(hiveAp.getMacAddress(), list);
						}
						list.add(result);
						while (list.size() > 10) {
							list.remove(0);
						}
					}
				}
			} catch (Exception e) {
				log.error("queryHiveApCpuMenInfo", "error, e:" + e);
			}
		}
	}

	private void prepareHiveApClientCountStats(HiveAp hiveAp) {
		if(hiveAp == null){
			return;
		}
		log.info("prepareHiveApClientCountStats", "Preparing client count for HiveAP " + hiveAp);
		Object clientStats = MgrUtil.getSessionAttribute(HIVE_AP_CLIENT_INFOS);
		if (null != clientStats) {
			uniqueClients = (List<CheckItem>) clientStats;
		} else {
			long systemTimeInMillis = System.currentTimeMillis();
			int aggregationInterval = 1;// hour unit


			Calendar reportDateTime = Calendar.getInstance(getDomain()
					.getTimeZone());
			reportDateTime.add(Calendar.HOUR_OF_DAY, -8);

			String searchSQL = "select clientMac,startTimeStamp,endTimeStamp from "
					+ AhClientSessionHistory.class.getSimpleName()
					+ " where "
					+ " endTimeStamp >='"
					+ reportDateTime.getTimeInMillis()
					+ "'";
			searchSQL = searchSQL + " and apMac='" + hiveAp.getMacAddress()
					+ "'";

			List<?> list = QueryUtil.executeQuery(searchSQL, null, null);

			Map<Long, Set<String>> cleintCountMap = new HashMap<Long, Set<String>>();

			Calendar historyTime = Calendar.getInstance(getDomain()
					.getTimeZone());
			for (Object obj : list) {
				historyTime.setTimeInMillis(reportDateTime.getTimeInMillis());
				historyTime.add(Calendar.HOUR_OF_DAY, aggregationInterval);

				Object[] tmp = (Object[]) obj;
				String clientMac = (String) tmp[0];
				Long clientStart = (Long) tmp[1];
				Long clientEnd = (Long) tmp[2];

				while (historyTime.getTimeInMillis() <= systemTimeInMillis) {
					if (cleintCountMap.get(historyTime.getTimeInMillis()) == null) {
						Set<String> setClientMac = new HashSet<String>();
						cleintCountMap.put(historyTime.getTimeInMillis(),
								setClientMac);
					}
					if (clientStart <= historyTime.getTimeInMillis()
							&& clientEnd > historyTime.getTimeInMillis()
									- aggregationInterval * 3600 * 1000) {
						cleintCountMap.get(historyTime.getTimeInMillis()).add(
								clientMac);
					}
					historyTime.add(Calendar.HOUR_OF_DAY, aggregationInterval);
				}
			}

//			String searchSQLCurrent = "select clientMac,startTimeStamp from "
//					+ AhClientSession.class.getSimpleName() + " where ";
//			searchSQLCurrent = searchSQLCurrent + " apMac='"
//					+ hiveAp.getMacAddress() + "'";
//
//			List<?> profilesCurrent = QueryUtil.executeQuery(searchSQLCurrent,
//					null, null);
			String searchSQLCurrent = "select clientMac,startTimeStamp from "
				+ "ah_clientsession where ";
			searchSQLCurrent = searchSQLCurrent + " apMac='"
					+ hiveAp.getMacAddress() + "'";

			List<?> profilesCurrent = DBOperationUtil.executeQuery(searchSQLCurrent,
					null, null);

			Calendar currentTime = Calendar.getInstance(getDomain()
					.getTimeZone());

			for (Object obj : profilesCurrent) {
				currentTime.setTimeInMillis(reportDateTime.getTimeInMillis());
				currentTime.add(Calendar.HOUR_OF_DAY, aggregationInterval);

				Object[] tmp = (Object[]) obj;
				String clientMac = (String) tmp[0];
				Long clientStart = (Long) tmp[1];
				while (currentTime.getTimeInMillis() <= systemTimeInMillis) {
					if (cleintCountMap.get(currentTime.getTimeInMillis()) == null) {
						Set<String> setClientMac = new HashSet<String>();
						cleintCountMap.put(currentTime.getTimeInMillis(),
								setClientMac);
					}
					if (clientStart <= currentTime.getTimeInMillis()) {
						cleintCountMap.get(currentTime.getTimeInMillis()).add(
								clientMac);
					}
					currentTime.add(Calendar.HOUR_OF_DAY, aggregationInterval);
				}
			}

			uniqueClients = new ArrayList<CheckItem>();
			TimeZone userTimeZone = getUserTimeZone();
			for (Long tmpReportTime : cleintCountMap.keySet()) {
				long tmpSize = cleintCountMap.get(tmpReportTime).size();
				String time = AhDateTimeUtil.getSpecifyDateTimeReport(
						tmpReportTime, userTimeZone);
				uniqueClients.add(new CheckItem(tmpSize, time));
			}

			Collections.sort(uniqueClients, new Comparator<CheckItem>() {
				@Override
				public int compare(CheckItem o1, CheckItem o2) {
					try {
						SimpleDateFormat sf = new SimpleDateFormat(
								AhDateTimeUtil.REPORT_DATE_TIME_FORMAT);
						Date reportTime1 = sf.parse(o1.getValue());
						Date reportTime2 = sf.parse(o2.getValue());
						return new Long((reportTime1.getTime() - reportTime2
								.getTime()) / 100000).intValue();
					} catch (Exception e) {
						return 0;
					}
				}
			});
			MgrUtil.setSessionAttribute(HIVE_AP_CLIENT_INFOS, uniqueClients);
		}
	}

	private void prepareHiveApClientOui(HiveAp hiveAp) {
		log.info("prepareHiveApClientOui", "Preparing client OUI for HiveAP " + hiveAp);
		Object clientStats = MgrUtil
				.getSessionAttribute(HIVE_AP_CLIENT_OUI_INFOS);
		if (null != clientStats) {
			clientOuis = (List<CheckItem>) clientStats;
		} else {
//			String query = "select clientMac,startTimeStamp from "
//					+ AhClientSession.class.getSimpleName();
//			String where = "apMac = :s1 AND connectstate = :s2";
//			Object[] values = new Object[] { hiveAp.getMacAddress(),
//					AhClientSession.CONNECT_STATE_UP };
//			List<?> profilesCurrent = QueryUtil.executeQuery(query, null,
//					new FilterParams(where, values));
			if (null == hiveAp) {
				return;
			}

			String query = "select clientMac,startTimeStamp from ah_clientsession";
			String where = "apMac = ? AND connectstate = ?";
			Object[] values = new Object[] { hiveAp.getMacAddress(),
					AhClientSession.CONNECT_STATE_UP };
			List<?> profilesCurrent = DBOperationUtil.executeQuery(query, null,
					new FilterParams(where, values));

			clientOuis = new ArrayList<CheckItem>();
			Map<String, Integer> clientCount = new HashMap<String, Integer>();
			for (Object obj : profilesCurrent) {
				Object[] tmp = (Object[]) obj;
				String clientMac = (String) tmp[0];
				if (clientMac != null) {
					String strOui = clientMac.substring(0, 6).toUpperCase();
					String vender = AhConstantUtil.getMacOuiComName(strOui) == null ? strOui
							: AhConstantUtil.getMacOuiComName(strOui);
					if (null == clientCount.get(vender)) {
						clientCount.put(vender, 0);
					}
					Integer value = clientCount.get(vender);
					clientCount.put(vender, value + 1);
				}
			}
			
			// fetch top 15 items to show, for bug 23883
			List<Map.Entry<String, Integer>> clientCountLst = new ArrayList<>(clientCount.entrySet());
			Collections.sort(clientCountLst, new Comparator<Map.Entry<String, Integer>>(){
				@Override
				public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
					return entry2.getValue().compareTo(entry1.getValue());
				}
			});
			int curPos = 0;
			for (Map.Entry<String, Integer> oui : clientCountLst) {
				if (curPos >= 15) {
					break;
				}
				clientOuis.add(new CheckItem(oui.getValue().longValue(), oui.getKey()));
				curPos++;
			}
			MgrUtil.setSessionAttribute(HIVE_AP_CLIENT_OUI_INFOS, clientOuis);
		}
	}

	private void prepareHiveApAcspNeighbor(HiveAp hiveAp) {
		log.info("prepareHiveApAcspNeighbor", "Preparing ACSP neighbor for HiveAP " + hiveAp);
		if (null != hiveAp) {
			acspNeighborList = QueryUtil.executeQuery(
					AhLatestACSPNeighbor.class, null, new FilterParams("apMac",
							hiveAp.getMacAddress()));
			String userTimeZone = userContext.getTimeZone();
			for (AhLatestACSPNeighbor acspNeibor : acspNeighborList) {
				acspNeibor.getTimeStamp().setTimeZone(userTimeZone);
			}
		}
	}

	private void prepareHiveApInterference(HiveAp hiveAp) {
		log.info("prepareHiveApInterference", "Preparing interference for HiveAP " + hiveAp);
		Object interference0Stats = MgrUtil
				.getSessionAttribute(HIVE_AP_INTERFERENCE_WIFI0_INFOS);
		Object interference1Stats = MgrUtil
				.getSessionAttribute(HIVE_AP_INTERFERENCE_WIFI1_INFOS);
		List<AhInterferenceStats> interferenceList;
		if (null != interference0Stats && null != interference1Stats) {
			interferenceWifi0List = (List<AhInterferenceStats>) interference0Stats;
			interferenceWifi1List = (List<AhInterferenceStats>) interference1Stats;
		} else {
			if (null == hiveAp) {
				return;
			}

			int aggregationInterval = 1;// hour unit
			long systemTimeInMillis = System.currentTimeMillis();
			Calendar reportDateTime = Calendar.getInstance(getDomain()
					.getTimeZone());

			reportDateTime.add(Calendar.HOUR_OF_DAY, -8);
			String where = "apMac = :s1 and timeStamp.time >= :s2";
			Object[] values = new Object[] { hiveAp.getMacAddress(),
					reportDateTime.getTimeInMillis() };
			FilterParams filter = new FilterParams(where, values);
			interferenceList = QueryUtil.executeQuery(
					AhInterferenceStats.class, null, filter);

			Map<Long, Set<AhInterferenceStats>> statsMap = new HashMap<Long, Set<AhInterferenceStats>>();
			// String timeZoneString = "";
			Calendar historyTime = Calendar.getInstance(getDomain()
					.getTimeZone());
			historyTime.setTimeInMillis(reportDateTime.getTimeInMillis());
			historyTime.add(Calendar.HOUR_OF_DAY, aggregationInterval);

			while (historyTime.getTimeInMillis() <= systemTimeInMillis) {
				if (statsMap.get(historyTime.getTimeInMillis()) == null) {
					Set<AhInterferenceStats> stats = new HashSet<AhInterferenceStats>();
					statsMap.put(historyTime.getTimeInMillis(), stats);
				}
				for (AhInterferenceStats stats : interferenceList) {
					// if ("".equals(timeZoneString)) {
					// timeZoneString = stats.getTimeStamp().getTimeZone();
					// }
					if (stats.getTimeStamp().getTime() <= historyTime
							.getTimeInMillis()
							&& stats.getTimeStamp().getTime() > historyTime
									.getTimeInMillis()
									- aggregationInterval * 3600 * 1000) {
						statsMap.get(historyTime.getTimeInMillis()).add(stats);
					}
				}
				historyTime.add(Calendar.HOUR_OF_DAY, aggregationInterval);
			}
			interferenceWifi0List = new ArrayList<AhInterferenceStats>();
			interferenceWifi1List = new ArrayList<AhInterferenceStats>();
			for (Long time : statsMap.keySet()) {
				Set<AhInterferenceStats> infos = statsMap.get(time);
				AhInterferenceStats wifi0Stats = new AhInterferenceStats();
				wifi0Stats.setTimeStamp(new HmTimeStamp(time, userContext
						.getTimeZone()));
				wifi0Stats.setOwner(getDomain());
				AhInterferenceStats wifi1Stats = new AhInterferenceStats();
				wifi1Stats.setTimeStamp(new HmTimeStamp(time, userContext
						.getTimeZone()));
				wifi1Stats.setOwner(getDomain());
				interferenceWifi0List.add(wifi0Stats);
				interferenceWifi1List.add(wifi1Stats);
				if (null != infos) {
					AhInterferenceStats item_wifi0 = null;
					AhInterferenceStats item_wifi1 = null;
					for (AhInterferenceStats item : statsMap.get(time)) {
						if ("wifi0".equalsIgnoreCase(item.getIfName())) {
							if (null == item_wifi0) {
								item_wifi0 = item;
								continue;
							}
							item_wifi0
									.setAverageInterferenceCU((byte) ((item_wifi0
											.getAverageInterferenceCU() + item
											.getAverageInterferenceCU()) / 2));
							item_wifi0
									.setShortTermInterferenceCU((byte) ((item_wifi0
											.getShortTermInterferenceCU() + item
											.getShortTermInterferenceCU()) / 2));
							item_wifi0
									.setSnapShotInterferenceCU((byte) ((item_wifi0
											.getSnapShotInterferenceCU() + item
											.getSnapShotInterferenceCU()) / 2));
							item_wifi0.setCrcError((byte) ((item_wifi0
									.getCrcError() + item.getCrcError()) / 2));
							item_wifi0
									.setInterferenceCUThreshold((byte) ((item_wifi0
											.getInterferenceCUThreshold() + item
											.getInterferenceCUThreshold()) / 2));
							item_wifi0
									.setCrcErrorRateThreshold((byte) ((item_wifi0
											.getCrcErrorRateThreshold() + item
											.getCrcErrorRateThreshold()) / 2));
						} else if ("wifi1".equalsIgnoreCase(item.getIfName())) {
							if (null == item_wifi1) {
								item_wifi1 = item;
								continue;
							}
							item_wifi1
									.setAverageInterferenceCU((byte) ((item_wifi1
											.getAverageInterferenceCU() + item
											.getAverageInterferenceCU()) / 2));
							item_wifi1
									.setShortTermInterferenceCU((byte) ((item_wifi1
											.getShortTermInterferenceCU() + item
											.getShortTermInterferenceCU()) / 2));
							item_wifi1
									.setSnapShotInterferenceCU((byte) ((item_wifi1
											.getSnapShotInterferenceCU() + item
											.getSnapShotInterferenceCU()) / 2));
							item_wifi1.setCrcError((byte) ((item_wifi1
									.getCrcError() + item.getCrcError()) / 2));
							item_wifi1
									.setInterferenceCUThreshold((byte) ((item_wifi1
											.getInterferenceCUThreshold() + item
											.getInterferenceCUThreshold()) / 2));
							item_wifi1
									.setCrcErrorRateThreshold((byte) ((item_wifi1
											.getCrcErrorRateThreshold() + item
											.getCrcErrorRateThreshold()) / 2));
						}
					}
					if (null != item_wifi0) {
						wifi0Stats.setAverageInterferenceCU(item_wifi0
								.getAverageInterferenceCU());
						wifi0Stats.setShortTermInterferenceCU(item_wifi0
								.getShortTermInterferenceCU());
						wifi0Stats.setSnapShotInterferenceCU(item_wifi0
								.getSnapShotInterferenceCU());
						wifi0Stats.setCrcError(item_wifi0.getCrcError());
						wifi0Stats.setInterferenceCUThreshold(item_wifi0
								.getInterferenceCUThreshold());
						wifi0Stats.setCrcErrorRateThreshold(item_wifi0
								.getCrcErrorRateThreshold());
					}

					if (null != item_wifi1) {
						wifi1Stats.setAverageInterferenceCU(item_wifi1
								.getAverageInterferenceCU());
						wifi1Stats.setShortTermInterferenceCU(item_wifi1
								.getShortTermInterferenceCU());
						wifi1Stats.setSnapShotInterferenceCU(item_wifi1
								.getSnapShotInterferenceCU());
						wifi1Stats.setCrcError(item_wifi1.getCrcError());
						wifi1Stats.setInterferenceCUThreshold(item_wifi1
								.getInterferenceCUThreshold());
						wifi1Stats.setCrcErrorRateThreshold(item_wifi1
								.getCrcErrorRateThreshold());
					}
				}
			}
			Collections.sort(interferenceWifi0List,
					new Comparator<AhInterferenceStats>() {
						@Override
						public int compare(AhInterferenceStats o1,
								AhInterferenceStats o2) {
							try {
								long reportTime1 = o1.getTimeStamp().getTime();
								long reportTime2 = o2.getTimeStamp().getTime();
								return new Long(
										(reportTime1 - reportTime2) / 100000)
										.intValue();
							} catch (Exception e) {
								return 0;
							}
						}
					});
			Collections.sort(interferenceWifi1List,
					new Comparator<AhInterferenceStats>() {
						@Override
						public int compare(AhInterferenceStats o1,
								AhInterferenceStats o2) {
							try {
								long reportTime1 = o1.getTimeStamp().getTime();
								long reportTime2 = o2.getTimeStamp().getTime();
								return new Long(
										(reportTime1 - reportTime2) / 100000)
										.intValue();
							} catch (Exception e) {
								return 0;
							}
						}
					});
			MgrUtil.setSessionAttribute(HIVE_AP_INTERFERENCE_WIFI0_INFOS,
					interferenceWifi0List);
			MgrUtil.setSessionAttribute(HIVE_AP_INTERFERENCE_WIFI1_INFOS,
					interferenceWifi1List);
		}
	}

	private void prepareFlash(boolean is11n, String softVer, short deviceType, HiveAp hiveAp) {
		width = "100%";
		height = "525";
		bgcolor = "ffffff";
		if (isFlashUsingRouter(hiveAp)) {
			swf = "brDashBoard";
			application = "brDashBoard";
		} else if (isFlashUsingVpnGateway(hiveAp)) {
			swf = "vpnGatewayDashBoard";
			application = "vpnGatewayDashBoard";
			height = "225";
		} else if (isFlashUsingVpnAsAp(hiveAp)) {
			swf = "vpnAsApDashBoard";
			application = "vpnAsApDashBoard";
			height = "225";
		} else if (this.isFlashUsingSwitch(hiveAp)) {
			swf = "switchDashBoard";
			application = "switchDashBoard";
			if (this.isBlnSwitchAsBr()) {
				height = "540";
			} else {
				height = "270";
			}
		} else {
			swf = "apDashBoard";
			if (NmsUtil.compareSoftwareVersion("3.4.1.0", softVer) > 0) {
				// only show client count, client verdor info
				height = "225";
			} else if (NmsUtil.compareSoftwareVersion("3.4.1.0", softVer) == 0) {
				// 11n ap show all chart, ap20 only show cpu, memory, client count,
				// client verdor info
				height = is11n ? "525" : "226";
			}
			application = "apDashBoard";
		}
	}

	private String swf, width, height, application = "apDashBoard", bgcolor;

	public String getApplication() {
		return application;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public String getHeight() {
		return height;
	}

	public String getSwf() {
		return swf;
	}

	public String getWidth() {
		return width;
	}

	//

	private String domainName;

	private Long mapId;

	private String name;

	private String macAddress;

	private String ipAddress;

	private String model;

	private String sw;

	private String status;

	private String upTime;

	private String topologyMap;

	private String origin;

	private String manageStatus;

	private String eth0DeviceId;

	private String eth0PortId;

	private String eth0SystemId;

	private String eth1DeviceId;

	private String eth1PortId;

	private String eth1SystemId;

	private List<AhLatestNeighbor> neighborList;

	private List<AhClientSession> clientList;

	private List<Idp> idpList;

	private List<AhEvent> eventList;

	private List<AhLatestACSPNeighbor> acspNeighborList;

	private List<AhInterferenceStats> interferenceWifi0List;

	private List<AhInterferenceStats> interferenceWifi1List;

	private String getWanStatusImg(DeviceInterface dInf,List<?> lstPortInfo) {
		for(Object oneRec : lstPortInfo) {
			AhPortAvailability ahPort = (AhPortAvailability)oneRec;
			String wanPortName=dInf.getWanPortName();
			if(AhInterface.DEVICE_IF_TYPE_USB==dInf.getDeviceIfType()){
				wanPortName=MgrUtil.getUserMessage("hiveAp.router.br.deviceInterface.devicePort.name.usb");
			}
			if (wanPortName.equalsIgnoreCase(ahPort.getInterfName())) {
				if(AhPortAvailability.WAN_ACTIVE == ahPort.getWanactive() ){
					return WANSTATUS_GREEN;
				}
			}
		}
		return WANSTATUS_GRAY;
	}
	
	private String getImageAltInfo(String imageSrc){
		if(WANSTATUS_GREEN.equals(imageSrc)){
			return "Active WAN";
		}else{
			return "Inactive WAN";
		}
	}

	// for radio details
	private int wifi0_index;

	private String wifi0_name;

	private String wifi0_type;

	private byte wifi0_ifMode;
	
	private String wifi0_mode;

	private String wifi0_channel;

	private String wifi0_power;

	private String wifi0_noise;

	private String wifi0_eirp;
	
	private String wifi0_type_tip;

	private List<String> wifi0_ssids;

	private int wifi1_index;

	private String wifi1_name;

	private String wifi1_type;
	
	private byte wifi1_ifMode;

	private String wifi1_mode;

	private String wifi1_channel;

	private String wifi1_power;

	private String wifi1_noise;

	private String wifi1_eirp;
	
	private String wifi1_type_tip;

	private List<String> wifi1_ssids;
	
	private String wifi0RateStr;
	
	private String wifi1RateStr;
	
	private PortGroupProfile portGroup;

	private String swUtilization;

	private String stpMode;

	private String stpState;

	private String sysTemparture;

	private String fanStatus;

	private String powerStatus;

	private boolean supportPowerStatus;
	
	List<AhSwitchPortStats> swPortStatsList;
	
	List<AhLLDPInformation> lldpInformationList;
	
	private int portNo;
	
	private String portMode;

	public boolean isSupportPowerStatus() {
		return supportPowerStatus;
	}
	
	public String getPowerStatus() {
		return powerStatus;
	}
	
	public List<AhLLDPInformation> getLldpInformationList() {
		return lldpInformationList;
	}

	public void setLldpInformationList(List<AhLLDPInformation> lldpInformationList) {
		this.lldpInformationList = lldpInformationList;
	}

	public int getPortNo() {
		return portNo;
	}

	public void setPortNo(int portNo) {
		this.portNo = portNo;
	}

	public String getPortMode() {
		return portMode;
	}

	public void setPortMode(String portMode) {
		this.portMode = portMode;
	}

	public List<AhSwitchPortStats> getSwPortStatsList() {
		return swPortStatsList;
	}

	public String getSwUtilization() {
		return swUtilization;
	}

	public String getStpMode() {
		return stpMode;
	}

	public String getStpState() {
		return stpState;
	}

	public String getSysTemparture() {
		return sysTemparture;
	}

	public String getFanStatus() {
		return fanStatus;
	}

	public PortGroupProfile getPortGroup() {
		return portGroup;
	}

	public void setPortGroup(PortGroupProfile portGroup) {
		this.portGroup = portGroup;
	}

	public List<AhLatestACSPNeighbor> getAcspNeighborList() {
		return acspNeighborList;
	}

	public List<AhInterferenceStats> getInterferenceWifi0List() {
		return interferenceWifi0List;
	}

	public List<AhInterferenceStats> getInterferenceWifi1List() {
		return interferenceWifi1List;
	}

	public String getDomainName() {
		return domainName;
	}

	public Long getMapId() {
		return mapId;
	}

	public String getName() {
		return name;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getModel() {
		if ("HiveAP-VI".equals(model)) {
			return "HiveOS-VA";
		}
		return model;
	}
	
	private String externalIpAddress;
	
	public String getExternalIpAddress() {
		return externalIpAddress;
	}

	public String getSw() {
		return sw;
	}

	public String getOrigin() {
		return origin;
	}

	public String getManageStatus() {
		return manageStatus;
	}

	public String getEth0DeviceId() {
		return eth0DeviceId;
	}

	public String getEth0PortId() {
		return eth0PortId;
	}

	public String getEth1DeviceId() {
		return eth1DeviceId;
	}

	public String getEth1PortId() {
		return eth1PortId;
	}

	public String getEth0SystemId() {
		return eth0SystemId;
	}

	public String getEth1SystemId() {
		return eth1SystemId;
	}

	public String getStatus() {
		return status;
	}

	public String getUpTime() {
		return upTime;
	}

	public String getTopologyMap() {
		return topologyMap;
	}

	public List<AhLatestNeighbor> getNeighborList() {
		return neighborList;
	}

	public List<AhClientSession> getClientList() {
		return clientList;
	}

	public List<Idp> getIdpList() {
		return idpList;
	}

	public List<AhEvent> getEventList() {
		return eventList;
	}

	public String getWifi0_name() {
		return wifi0_name;
	}

	public void setWifi0_name(String wifi0_name) {
		this.wifi0_name = wifi0_name;
	}

	public String getWifi0_type() {
		return wifi0_type;
	}

	public void setWifi0_type(String wifi0_type) {
		this.wifi0_type = wifi0_type;
	}

	public String getWifi0_mode() {
		return wifi0_mode;
	}

	public void setWifi0_mode(String wifi0_mode) {
		this.wifi0_mode = wifi0_mode;
	}

	public String getWifi0_channel() {
		return wifi0_channel;
	}

	public void setWifi0_channel(String wifi0_channel) {
		this.wifi0_channel = wifi0_channel;
	}

	public String getWifi0_power() {
		return wifi0_power;
	}

	public void setWifi0_power(String wifi0_power) {
		this.wifi0_power = wifi0_power;
	}

	public String getWifi0_noise() {
		return wifi0_noise;
	}

	public void setWifi0_noise(String wifi0_noise) {
		this.wifi0_noise = wifi0_noise;
	}

	public String getWifi1_name() {
		return wifi1_name;
	}

	public void setWifi1_name(String wifi1_name) {
		this.wifi1_name = wifi1_name;
	}

	public String getWifi1_type() {
		return wifi1_type;
	}

	public void setWifi1_type(String wifi1_type) {
		this.wifi1_type = wifi1_type;
	}
   
	public byte getWifi0_ifMode() {
		return wifi0_ifMode;
	}

	public void setWifi0_ifMode(byte wifi0_ifMode) {
		this.wifi0_ifMode = wifi0_ifMode;
	}

	public byte getWifi1_ifMode() {
		return wifi1_ifMode;
	}

	public void setWifi1_ifMode(byte wifi1_ifMode) {
		this.wifi1_ifMode = wifi1_ifMode;
	}

	public String getWifi1_mode() {
		return wifi1_mode;
	}

	public void setWifi1_mode(String wifi1_mode) {
		this.wifi1_mode = wifi1_mode;
	}

	public String getWifi1_channel() {
		return wifi1_channel;
	}

	public void setWifi1_channel(String wifi1_channel) {
		this.wifi1_channel = wifi1_channel;
	}

	public String getWifi1_power() {
		return wifi1_power;
	}

	public void setWifi1_power(String wifi1_power) {
		this.wifi1_power = wifi1_power;
	}

	public String getWifi1_noise() {
		return wifi1_noise;
	}

	public void setWifi1_noise(String wifi1_noise) {
		this.wifi1_noise = wifi1_noise;
	}

	public int getWifi0_index() {
		return wifi0_index;
	}

	public void setWifi0_index(int wifi0_index) {
		this.wifi0_index = wifi0_index;
	}

	public int getWifi1_index() {
		return wifi1_index;
	}

	public void setWifi1_index(int wifi1_index) {
		this.wifi1_index = wifi1_index;
	}

	public List<String> getWifi0_ssids() {
		return wifi0_ssids;
	}

	public List<String> getWifi1_ssids() {
		return wifi1_ssids;
	}

	public String getWifi0_eirp() {
		return wifi0_eirp;
	}

	public void setWifi0_eirp(String wifi0_eirp) {
		this.wifi0_eirp = wifi0_eirp;
	}

	public String getWifi1_eirp() {
		return wifi1_eirp;
	}

	public void setWifi1_eirp(String wifi1_eirp) {
		this.wifi1_eirp = wifi1_eirp;
	}

	protected JSONArray jsonArray = null;

	protected JSONObject jsonObject = null;

	@Override
	public String getJSONString() {
		if (jsonArray == null) {
			log.debug("getJSONString", "JSON string: " + jsonObject.toString());
			return jsonObject.toString();
		} else {
			log.debug("getJSONString", "JSON string: " + jsonArray.toString());
			return jsonArray.toString();
		}
	}

	private void prepareDeviceType(HiveAp hiveAp) {
		if (hiveAp == null) {
			return;
		}
		if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER) {
			blnBranchRouter = true;
			if (hiveAp.getDeviceInfo().isDeviceModelInitSwitch()) {
				this.blnSwitchAsBr = true;
			}
		} else if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY || hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_BR) {
			blnVpnGateway = true;
		} else if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_SWITCH) {
			blnSwitch = true;
		}
		if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100) {
			blnBr100Like = true;
		}
		
		if (HiveAp.Device_TYPE_HIVEAP == hiveAp.getDeviceType()
				&& hiveAp.isCVGAppliance()) {
			blnVpnGatewayAsAp = true;
		}

		swEth48 = hiveAp.getDeviceInfo().isSptEthernetMore_48();
		
		if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_SR2124P
				|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_SR2148P) {
			// only 2124p & 2148p support Redundancy Power Supply
			supportPowerStatus = true;
		} else {
			supportPowerStatus = false;
		}
	}

	private boolean blnBranchRouter;
	private boolean blnVpnGateway;
	private boolean blnBr100Like;
	private boolean blnVpnGatewayAsAp;
	private boolean blnSwitch;
	private boolean blnSwitchAsBr;
    private DeviceInterface cellularDevice;
	public boolean isBlnSwitchAsBr() {
		return blnSwitchAsBr;
	}

	public void setBlnSwitchAsBr(boolean blnSwitchAsBr) {
		this.blnSwitchAsBr = blnSwitchAsBr;
	}
	
	public boolean isBlnSwitch() {
		return blnSwitch;
	}

	public void setBlnSwitch(boolean blnSwitch) {
		this.blnSwitch = blnSwitch;
	}

	public boolean isBlnVpnGatewayAsAp() {
		return blnVpnGatewayAsAp;
	}

	public boolean isBlnBr100Like() {
		return blnBr100Like;
	}

	public void setBlnBr100Like(boolean blnBr100Like) {
		this.blnBr100Like = blnBr100Like;
	}

	public boolean isBlnBranchRouter() {
		return blnBranchRouter;
	}

	public void setBlnBranchRouter(boolean blnBranchRouter) {
		this.blnBranchRouter = blnBranchRouter;
	}

	public boolean isBlnVpnGateway() {
		return blnVpnGateway;
	}

	public void setBlnVpnGateway(boolean blnVpnGateway) {
		this.blnVpnGateway = blnVpnGateway;
	}

	public boolean isDisplaySIMCardDetails() {
		if(null!=cellularDevice){
			return true;
		}
		return false;
	}

	private String getFlashDataReturnPath(HiveAp hiveAp) {
		if (isFlashUsingRouter(hiveAp)) {
			return "brDashBoardData";
		} else if (isFlashUsingVpnGateway(hiveAp)) {
			return "vpnGatewayDashBoardData";
		} else if (isFlashUsingVpnAsAp(hiveAp)) {
			return "vpnAsApDashBoardData";
		} else if (isFlashUsingSwitch(hiveAp)) {
			return "switchDashBoardData";
		}
		return "dashBoardhData";
	}
	
	private boolean isFlashUsingVpnGateway(HiveAp hiveAp) {
		if (hiveAp != null) {
			if (HiveAp.Device_TYPE_VPN_GATEWAY == hiveAp.getDeviceType()) {
				return true;
			}
		}
		return false;
	}
	private boolean isFlashUsingRouter(HiveAp hiveAp) {
		if (hiveAp != null) {
			if (HiveAp.Device_TYPE_BRANCH_ROUTER == hiveAp.getDeviceType()
					&& !this.isBlnSwitchAsBr()) {
				return true;
			}
		}
		return false;
	}
	private boolean isFlashUsingVpnAsAp(HiveAp hiveAp) {
		if (hiveAp != null) {
			if (hiveAp.isCVGAppliance()
					&& HiveAp.Device_TYPE_HIVEAP == hiveAp.getDeviceType()) {
				return true;
			}
		}
		return false;
	}
	private boolean isFlashUsingSwitch(HiveAp hiveAp) {
		if (hiveAp != null) {
			if (hiveAp.getDeviceInfo().isDeviceModelInitSwitch()) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	private boolean isFlashUsingHiveAp(HiveAp hiveAp) {
		if (hiveAp != null) {
			if (HiveAp.Device_TYPE_HIVEAP == hiveAp.getDeviceType()) {
				return true;
			}
		}
		return false;
	}

	/*start: used for vpn availability report.*/
	private List<TextItem> vpn_availability=null;
	private List<TextItem> vpn_uptime=null;

	public List<TextItem> getVpn_availability() {
		return vpn_availability;
	}

	public void setVpn_availability(List<TextItem> vpn_availability) {
		this.vpn_availability = vpn_availability;
	}

	public List<TextItem> getVpn_uptime() {
		return vpn_uptime;
	}

	public void setVpn_uptime(List<TextItem> vpn_uptime) {
		this.vpn_uptime = vpn_uptime;
	}

	private int calTunnelCountForCVGReport(HiveAp hiveAp) {
		String vpnCountSql =
				"select c.hiveapid, count(c.hiveapid) from vpn_service a, vpn_service_credential b, " +
				"vpn_gateway_setting c where a.id=b.vpn_service_id and a.id=c.vpn_gateway_setting_id " +
				"and a.ipsecvpntype=4 and b.allocatedStatus=" + VpnServiceCredential.ALLOCATED_STATUS_USED +
				" and c.hiveapid = " + hiveAp.getId() +
				" group by c.hiveapid";
		List<?> profilesIds = QueryUtil.executeNativeQuery(vpnCountSql);
		if (profilesIds != null && profilesIds.size() > 0) {
			Object[] singleObj = (Object[])profilesIds.get(0);
			return Integer.parseInt(singleObj[1].toString());
		}
		return 0;
	}

	private Map<String, List<String>> calAllTunnelsForReport(HiveAp hiveAp) {
		String searchSQL = "select a.hostname, a.macAddress, d.tunnelName, d.externalIpAddress from hive_ap a, " +
					" config_template b, " +
					" VPN_SERVICE c, " +
					"(select e.hostname as tunnelName, f.VPN_GATEWAY_SETTING_ID, f.externalIpAddress " +
					"from hive_ap e,VPN_GATEWAY_SETTING f where e.id=f.hiveApId) d " +
					"where a.template_id=b.id and b.VPN_SERVICE_ID=c.id " +
					"and c.id= d.VPN_GATEWAY_SETTING_ID " +
					"and lower(a.macAddress)='" + hiveAp.getMacAddress().toLowerCase() + "'" +
					" and a.deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER;

		List<?> profiles = QueryUtil.executeNativeQuery(searchSQL);
		Map<String, List<String>> tunnelNameMap = new HashMap<String, List<String>>();
		if (profiles == null || profiles.size() < 1) {

		} else {
			for (Object profile : profiles) {
				Object[] tmp = (Object[]) profile;

				if (tmp[0] != null && !tmp[0].toString().equals("")) {
					if (tmp[2] != null && !tmp[2].toString().equals("") && tmp[3] != null && !tmp[3].toString().equals("")) {
						if (tunnelNameMap.get(tmp[0].toString().toLowerCase()) == null) {
							List<String> tmpList = new ArrayList<String>();
							tmpList.add(tmp[2].toString());
							tunnelNameMap.put(tmp[0].toString().toLowerCase(), tmpList);
						} else {
							tunnelNameMap.get(tmp[0].toString().toLowerCase()).add(tmp[2].toString());
						}
					}
				}
			}
		}
		return tunnelNameMap;
	}

	/**
	 * whether tunnel more than current configured should be counted
	 *
	 * @param isConfigOnlyOneTunnel :if 2 tunnels are configured, nothing to worry about
	 * @param lastTunnelOnlyIp :indicate which tunnel is configured at the last time
	 * @param curTunnelIp :the counting tunnel, if it is same as lastTunnelOnlyIp, it will be counted
	 *
	 * @return boolean :whether to be counted
	 */
	private boolean isTunnelShouldCount4Br(boolean isConfigOnlyOneTunnel, String lastTunnelOnlyIp, String curTunnelIp) {
		if (!isConfigOnlyOneTunnel) {
			return true;
		}
		if (lastTunnelOnlyIp == null || "".equals(lastTunnelOnlyIp)) {
			return false;
		}
		if (lastTunnelOnlyIp.equals(curTunnelIp)) {
			return true;
		}
		return false;
	}

	private void prepareVpnAvailablity(HiveAp hiveAp) {
		DecimalFormat df = new DecimalFormat("0.00");
		String searchSQL = "lower(mac)=:s1 and interfType=:s2";

		Object values[] = new Object[2];
		values[0] = hiveAp.getMacAddress().toLowerCase();
		values[1] = AhPortAvailability.INTERFACE_TYPE_VPN;

		List<?> lstInterfaceInfo = QueryUtil.executeQuery(AhStatsLatencyHigh.class, new SortParams("time"),
				new FilterParams(searchSQL, values));

		int totalBarCount = 60;
		long timeTigg=1;
		int[] totalCount= new int[totalBarCount];
		int[] upCount= new int[totalBarCount];
		int index=0;
		long currentTime=0;
		long nextTime=0;
		long oneTimeRecordCount=0;
		long totalTimeRecordtime=0;
		long totalUptime=0;

		vpn_uptime= new ArrayList<TextItem>();
		vpn_availability= new ArrayList<TextItem>();

		Map<String, List<String>> tunnelsNameMap = calAllTunnelsForReport(hiveAp);
		int allTunnelCount = 1;
		if (tunnelsNameMap != null && tunnelsNameMap.size() > 0
				&& tunnelsNameMap.get(hiveAp.getHostName().toLowerCase()) != null
				&& tunnelsNameMap.get(hiveAp.getHostName().toLowerCase()).size() > 0) {
			allTunnelCount = tunnelsNameMap.get(hiveAp.getHostName().toLowerCase()).size();
		}
		boolean isConfigOnlyOneTunnel = false;
		if (allTunnelCount < 2) {
			isConfigOnlyOneTunnel = true;
		}
		String lastTunnelOnlyIp = null;

		long lastRecordTime=System.currentTimeMillis();
		if (!lstInterfaceInfo.isEmpty()) {
			lastTunnelOnlyIp = ((AhStatsLatencyHigh)lstInterfaceInfo.get(lstInterfaceInfo.size()-1)).getInterfServer();
		}
		List<AhStatsAvailabilityHigh> oneTimeRec = QueryUtil.executeQuery(AhStatsAvailabilityHigh.class, new SortParams("time", false),
					new FilterParams("lower(mac)", values[0].toString()), 1);
		if (!oneTimeRec.isEmpty()) {
				lastRecordTime = oneTimeRec.get(0).getTime();
		}
		lastRecordTime = lastRecordTime - timeTigg * 60 *1000L * totalBarCount;

		for(Object OneRec : lstInterfaceInfo) {
			long recTime;
			byte recStatus;
			String recServerIp;

			recTime = ((AhStatsLatencyHigh)OneRec).getTime();
			recStatus = ((AhStatsLatencyHigh)OneRec).getTargetStatus();
			recServerIp = ((AhStatsLatencyHigh)OneRec).getInterfServer();
			if (recTime<=lastRecordTime) {
				continue;
			} else if(currentTime==0 && recTime>lastRecordTime + timeTigg * 60 *1000L) {
				currentTime = lastRecordTime + (index) * timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;
				oneTimeRecordCount = 1;
				totalCount[index]= allTunnelCount * ((int)oneTimeRecordCount);
				totalTimeRecordtime = totalTimeRecordtime + totalCount[index];

				while (recTime>lastRecordTime + (index +1) * timeTigg * 60 *1000L){
					long reportTimeConvert=nextTime;
					vpn_availability.add(new TextItem(
							df.format(((float)upCount[index] *100)/(totalCount[index])),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

					vpn_uptime.add(new TextItem(
							df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					if (index <(totalBarCount-1)){
						index++;
						currentTime = currentTime + timeTigg * 60 *1000L;
						nextTime=currentTime + timeTigg * 60 *1000L;
						oneTimeRecordCount = 1;
						totalCount[index]= allTunnelCount * ((int)oneTimeRecordCount);
						totalTimeRecordtime = totalTimeRecordtime + totalCount[index];
					} else {
						break;
					}
				}
			}

			if (currentTime==0) {
				currentTime = lastRecordTime;
				nextTime=currentTime + timeTigg * 60 *1000L;
				oneTimeRecordCount = 1;
				totalCount[index]= allTunnelCount * ((int)oneTimeRecordCount);
				totalTimeRecordtime = totalTimeRecordtime + totalCount[index];
			}

			if (recTime <= nextTime) {
				if (isTunnelShouldCount4Br(isConfigOnlyOneTunnel, lastTunnelOnlyIp, recServerIp)) {
					if (recStatus==AhStatsLatencyHigh.TARGET_STATUS_UP){
						upCount[index]++;
						totalUptime++;
					}
				}
			} else {
				long reportTimeConvert=nextTime;
				vpn_availability.add(new TextItem(
						df.format(((float)upCount[index] *100)/(totalCount[index])),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert, tz)));
				vpn_uptime.add(new TextItem(
						df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				if (index<(totalBarCount-1)){
					index++;
					currentTime = currentTime + timeTigg * 60 *1000L;
					nextTime=currentTime + timeTigg * 60 *1000L;
					oneTimeRecordCount = 1;
					totalCount[index]= allTunnelCount * ((int)oneTimeRecordCount);
					totalTimeRecordtime = totalTimeRecordtime + totalCount[index];

					while (recTime> currentTime && index <totalBarCount){
						if (recTime<=nextTime) {
							if (isTunnelShouldCount4Br(isConfigOnlyOneTunnel, lastTunnelOnlyIp, recServerIp)) {
								if (recStatus==AhStatsLatencyHigh.TARGET_STATUS_UP){
									upCount[index]++;
									totalUptime++;
								}
							}
							break;
						} else {
							reportTimeConvert=nextTime;
							vpn_availability.add(new TextItem(
									df.format(((float)upCount[index] *100)/(totalCount[index])),
									AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
							vpn_uptime.add(new TextItem(
									df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
									AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
							if (index <(totalBarCount-1)){
								index++;
								currentTime = currentTime + timeTigg * 60 *1000L;
								nextTime=currentTime + timeTigg * 60 *1000L;
								oneTimeRecordCount = 1;
								totalCount[index]= allTunnelCount * ((int)oneTimeRecordCount);
								totalTimeRecordtime = totalTimeRecordtime + totalCount[index];
							} else {
								break;
							}
						}
					}
				}
			}
		}
		if (vpn_availability.size()<totalBarCount && index!=0) {
			long reportTimeConvert=nextTime;
			vpn_availability.add(new TextItem(
					df.format(((float)upCount[index] *100)/(totalCount[index])),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

			vpn_uptime.add(new TextItem(
					df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

			index++;
		}

		while (index <totalBarCount){
			currentTime = lastRecordTime + (index) * timeTigg * 60 *1000L;
			nextTime=currentTime + timeTigg * 60 *1000L;
			oneTimeRecordCount = 1;
			totalCount[index]= allTunnelCount * ((int)oneTimeRecordCount);
			totalTimeRecordtime = totalTimeRecordtime + totalCount[index];

			long reportTimeConvert=nextTime;
			vpn_availability.add(new TextItem(
					df.format(((float)upCount[index] *100)/(totalCount[index])),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

			vpn_uptime.add(new TextItem(
					df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			index++;
		}

	}

	/*end: used for vpn availability report.*/

	/*start: used for vpn throughput report.*/
	private List<TextItem> vpn_throughput_tunnel1_in = null;
	private List<TextItem> vpn_throughput_tunnel1_out = null;
	private List<TextItem> vpn_throughput_tunnel2_in = null;
	private List<TextItem> vpn_throughput_tunnel2_out = null;
	private String vpn_tunnel1_name;
	private String vpn_tunnel2_name;

	public List<TextItem> getVpn_throughput_tunnel1_in() {
		return vpn_throughput_tunnel1_in;
	}

	public void setVpn_throughput_tunnel1_in(
			List<TextItem> vpn_throughput_tunnel1_in) {
		this.vpn_throughput_tunnel1_in = vpn_throughput_tunnel1_in;
	}

	public List<TextItem> getVpn_throughput_tunnel1_out() {
		return vpn_throughput_tunnel1_out;
	}

	public void setVpn_throughput_tunnel1_out(
			List<TextItem> vpn_throughput_tunnel1_out) {
		this.vpn_throughput_tunnel1_out = vpn_throughput_tunnel1_out;
	}

	public List<TextItem> getVpn_throughput_tunnel2_in() {
		return vpn_throughput_tunnel2_in;
	}

	public void setVpn_throughput_tunnel2_in(
			List<TextItem> vpn_throughput_tunnel2_in) {
		this.vpn_throughput_tunnel2_in = vpn_throughput_tunnel2_in;
	}

	public List<TextItem> getVpn_throughput_tunnel2_out() {
		return vpn_throughput_tunnel2_out;
	}

	public void setVpn_throughput_tunnel2_out(
			List<TextItem> vpn_throughput_tunnel2_out) {
		this.vpn_throughput_tunnel2_out = vpn_throughput_tunnel2_out;
	}

	public String getVpn_tunnel1_name() {
		return vpn_tunnel1_name;
	}

	public void setVpn_tunnel1_name(String vpn_tunnel1_name) {
		this.vpn_tunnel1_name = vpn_tunnel1_name;
	}

	public String getVpn_tunnel2_name() {
		return vpn_tunnel2_name;
	}

	public void setVpn_tunnel2_name(String vpn_tunnel2_name) {
		this.vpn_tunnel2_name = vpn_tunnel2_name;
	}

	private void prepareVpnThroughput(HiveAp hiveAp) {
		DecimalFormat df = new DecimalFormat("0.00");
		String searchSQL = "lower(mac)=:s1 and interfType=:s2";

		Object values[] = new Object[2];
		values[0] = hiveAp.getMacAddress().toLowerCase();
		values[1] = AhPortAvailability.INTERFACE_TYPE_VPN;

		SortParams sortParams = new SortParams("interfName");
		sortParams.setPrimaryOrderBy("time");
		List<?> lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputHigh.class, sortParams,
				new FilterParams(searchSQL, values));
		long timeTigg=1;
		long hourTigg=60;
		int index=0;
		long currentTime=0;
		long nextTime=0;

		vpn_throughput_tunnel1_in = new ArrayList<TextItem>();
		vpn_throughput_tunnel1_out = new ArrayList<TextItem>();
		vpn_throughput_tunnel2_in = new ArrayList<TextItem>();
		vpn_throughput_tunnel2_out = new ArrayList<TextItem>();
		vpn_tunnel1_name = "";
		vpn_tunnel2_name = "";

		long throughput_tunnel1_in=0;
		long throughput_tunnel1_out=0;
		long throughput_tunnel2_in=0;
		long throughput_tunnel2_out=0;

		for(Object OneRec : lstInterfaceInfo) {
			String vTunnelName = ((AhStatsThroughputHigh)OneRec).getInterfName();
			if (!isNullString(vTunnelName)) {
				if (isNullString(vpn_tunnel1_name)) {
					vpn_tunnel1_name = vTunnelName;
				} else if (isNullString(vpn_tunnel2_name) && !vTunnelName.equals(vpn_tunnel1_name)) {
					vpn_tunnel2_name = vTunnelName;
					break;
				}
			}
		}

		long lastRecordTime=System.currentTimeMillis();
		List<AhStatsAvailabilityHigh> oneTimeRec = QueryUtil.executeQuery(AhStatsAvailabilityHigh.class, new SortParams("time", false),
					new FilterParams("lower(mac)", values[0].toString()), 1);
		if (!oneTimeRec.isEmpty()) {
			lastRecordTime = oneTimeRec.get(0).getTime();
		}
		lastRecordTime = lastRecordTime - timeTigg * 60 *1000L * hourTigg;
		
		long timePerPoint=timeTigg * 60;

		for(Object OneRec : lstInterfaceInfo) {
			long recTime;
			long vPutIn = 0;
			long vPutOut = 0;
			String vTunnelName = "";
			recTime = ((AhStatsThroughputHigh)OneRec).getTime();
			vPutIn = ((AhStatsThroughputHigh)OneRec).getRxBytes();
			vPutOut = ((AhStatsThroughputHigh)OneRec).getTxBytes();
			vTunnelName = ((AhStatsThroughputHigh)OneRec).getInterfName();

			if (recTime<=lastRecordTime) {
				continue;
			} else if(currentTime==0 && recTime>lastRecordTime + timeTigg * 60 *1000L) {
				currentTime = lastRecordTime + (index) * timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;
				while (recTime>lastRecordTime + (index +1) * timeTigg * 60 *1000L){
					long reportTimeConvert=nextTime;
					vpn_throughput_tunnel1_in.add(new TextItem(
							df.format(((float)throughput_tunnel1_in)*8/1024/timePerPoint),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					vpn_throughput_tunnel1_out.add(new TextItem(
							df.format(((float)throughput_tunnel1_out*8/1024/timePerPoint)),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					vpn_throughput_tunnel2_in.add(new TextItem(
							df.format(((float)throughput_tunnel2_in)*8/1024/timePerPoint),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					vpn_throughput_tunnel2_out.add(new TextItem(
							df.format(((float)throughput_tunnel2_out*8/1024/timePerPoint)),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

					throughput_tunnel1_in=0;
					throughput_tunnel1_out=0;
					throughput_tunnel2_in=0;
					throughput_tunnel2_out=0;
					index++;
					currentTime = currentTime + timeTigg * 60 *1000L;
					nextTime=currentTime + timeTigg * 60 *1000L;
				}
			}
			if (currentTime==0) {
				currentTime = lastRecordTime;
				nextTime=currentTime + timeTigg * 60 *1000L;
			}

			if (recTime <= nextTime) {
				if (vTunnelName.equals(vpn_tunnel1_name)) {
					throughput_tunnel1_in = throughput_tunnel1_in + vPutIn;
					throughput_tunnel1_out = throughput_tunnel1_out + vPutOut;
				} else if (vTunnelName.equals(vpn_tunnel2_name)) {
					throughput_tunnel2_in = throughput_tunnel2_in + vPutIn;
					throughput_tunnel2_out = throughput_tunnel2_out + vPutOut;
				}
			}else {
				long reportTimeConvert=nextTime;
				vpn_throughput_tunnel1_in.add(new TextItem(
						df.format(((float)throughput_tunnel1_in)*8/1024/timePerPoint),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				vpn_throughput_tunnel1_out.add(new TextItem(
						df.format(((float)throughput_tunnel1_out*8/1024/timePerPoint)),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				vpn_throughput_tunnel2_in.add(new TextItem(
						df.format(((float)throughput_tunnel2_in)*8/1024/timePerPoint),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				vpn_throughput_tunnel2_out.add(new TextItem(
						df.format(((float)throughput_tunnel2_out*8/1024/timePerPoint)),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

				throughput_tunnel1_in=0;
				throughput_tunnel1_out=0;
				throughput_tunnel2_in=0;
				throughput_tunnel2_out=0;
				index++;
				currentTime = currentTime + timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;

				while (recTime> currentTime){
					if (recTime<=nextTime) {
						if (vTunnelName.equals(vpn_tunnel1_name)) {
							throughput_tunnel1_in = throughput_tunnel1_in + vPutIn;
							throughput_tunnel1_out = throughput_tunnel1_out + vPutOut;
						} else if (vTunnelName.equals(vpn_tunnel2_name)) {
							throughput_tunnel2_in = throughput_tunnel2_in + vPutIn;
							throughput_tunnel2_out = throughput_tunnel2_out + vPutOut;
						}
						break;
					} else {
						reportTimeConvert=nextTime;
						vpn_throughput_tunnel1_in.add(new TextItem(
								df.format(((float)throughput_tunnel1_in)*8/1024/timePerPoint),
								AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
						vpn_throughput_tunnel1_out.add(new TextItem(
								df.format(((float)throughput_tunnel1_out*8/1024/timePerPoint)),
								AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
						vpn_throughput_tunnel2_in.add(new TextItem(
								df.format(((float)throughput_tunnel2_in)*8/1024/timePerPoint),
								AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
						vpn_throughput_tunnel2_out.add(new TextItem(
								df.format(((float)throughput_tunnel2_out*8/1024/timePerPoint)),
								AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

						throughput_tunnel1_in=0;
						throughput_tunnel1_out=0;
						throughput_tunnel2_in=0;
						throughput_tunnel2_out=0;
						index++;
						currentTime = currentTime + timeTigg * 60 *1000L;
						nextTime=currentTime + timeTigg * 60 *1000L;
					}
				}
			}

		}

		if (index!=0) {
			long reportTimeConvert=nextTime;
			vpn_throughput_tunnel1_in.add(new TextItem(
					df.format(((float)throughput_tunnel1_in)*8/1024/timePerPoint),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			vpn_throughput_tunnel1_out.add(new TextItem(
					df.format(((float)throughput_tunnel1_out*8/1024/timePerPoint)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

			vpn_throughput_tunnel2_in.add(new TextItem(
					df.format(((float)throughput_tunnel2_in)*8/1024/timePerPoint),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			vpn_throughput_tunnel2_out.add(new TextItem(
					df.format(((float)throughput_tunnel2_out*8/1024/timePerPoint)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

			index++;
			currentTime = currentTime + timeTigg * 60 *1000L;
			nextTime=currentTime + timeTigg * 60 *1000L;
			throughput_tunnel1_in=0;
			throughput_tunnel1_out=0;
			throughput_tunnel2_in=0;
			throughput_tunnel2_out=0;
		}

		if (currentTime==0) {
			currentTime = lastRecordTime;
			nextTime=currentTime + timeTigg * 60 *1000L;
			throughput_tunnel1_in=0;
			throughput_tunnel1_out=0;
			throughput_tunnel2_in=0;
			throughput_tunnel2_out=0;
		}
		while (nextTime <=lastRecordTime + timeTigg * 60 *1000L * hourTigg) {
			long reportTimeConvert=nextTime;
			vpn_throughput_tunnel1_in.add(new TextItem(
					df.format(((float)throughput_tunnel1_in)*8/1024/timePerPoint),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			vpn_throughput_tunnel1_out.add(new TextItem(
					df.format(((float)throughput_tunnel1_out*8/1024/timePerPoint)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			vpn_throughput_tunnel2_in.add(new TextItem(
					df.format(((float)throughput_tunnel2_in)*8/1024/timePerPoint),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			vpn_throughput_tunnel2_out.add(new TextItem(
					df.format(((float)throughput_tunnel2_out*8/1024/timePerPoint)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

			throughput_tunnel1_in=0;
			throughput_tunnel1_out=0;
			throughput_tunnel2_in=0;
			throughput_tunnel2_out=0;

			index++;
			currentTime = currentTime + timeTigg * 60 *1000L;
			nextTime=currentTime + timeTigg * 60 *1000L;
		}

	}
	/*end: used for vpn throughput report.*/

	/*start: used for vpn latency report.*/
	private List<TextItem> vpn_latency_tunnel1 = null;
	private List<TextItem> vpn_latency_tunnel2 = null;
	private String vpn_latency_tunnel1_name;
	private String vpn_latency_tunnel2_name;
	public List<TextItem> getVpn_latency_tunnel1() {
		return vpn_latency_tunnel1;
	}

	public void setVpn_latency_tunnel1(List<TextItem> vpn_latency_tunnel1) {
		this.vpn_latency_tunnel1 = vpn_latency_tunnel1;
	}

	public List<TextItem> getVpn_latency_tunnel2() {
		return vpn_latency_tunnel2;
	}

	public void setVpn_latency_tunnel2(List<TextItem> vpn_latency_tunnel2) {
		this.vpn_latency_tunnel2 = vpn_latency_tunnel2;
	}

	public String getVpn_latency_tunnel1_name() {
		return vpn_latency_tunnel1_name;
	}

	public void setVpn_latency_tunnel1_name(String vpn_latency_tunnel1_name) {
		this.vpn_latency_tunnel1_name = vpn_latency_tunnel1_name;
	}

	public String getVpn_latency_tunnel2_name() {
		return vpn_latency_tunnel2_name;
	}

	public void setVpn_latency_tunnel2_name(String vpn_latency_tunnel2_name) {
		this.vpn_latency_tunnel2_name = vpn_latency_tunnel2_name;
	}

	private void prepareVpnLatency(HiveAp hiveAp) {
		DecimalFormat df = new DecimalFormat("0.00");
		String searchSQL = "lower(mac)=:s1 and interfType=:s2";

		Object values[] = new Object[2];
		values[0] = hiveAp.getMacAddress().toLowerCase();
		values[1] = AhPortAvailability.INTERFACE_TYPE_VPN;

		SortParams sortParams = new SortParams("interfName");
		sortParams.setPrimaryOrderBy("time");
		List<?> lstInterfaceInfo = QueryUtil.executeQuery(AhStatsLatencyHigh.class, sortParams,
				new FilterParams(searchSQL, values));
		long timeTigg=1;
		long hourTigg=60;
		int index=0;
		long currentTime=0;
		long nextTime=0;

		vpn_latency_tunnel1 = new ArrayList<TextItem>();
		vpn_latency_tunnel2 = new ArrayList<TextItem>();
		vpn_latency_tunnel1_name = "";
		vpn_latency_tunnel2_name = "";

		for(Object OneRec : lstInterfaceInfo) {
			String vTunnelName = ((AhStatsLatencyHigh)OneRec).getInterfName();
			if (!isNullString(vTunnelName)) {
				if (isNullString(vpn_latency_tunnel1_name)) {
					vpn_latency_tunnel1_name = vTunnelName;
				} else if (isNullString(vpn_latency_tunnel2_name) && !vTunnelName.equals(vpn_latency_tunnel1_name)) {
					vpn_latency_tunnel2_name = vTunnelName;
					break;
				}
			}
		}

		long lastRecordTime=System.currentTimeMillis();
		List<AhStatsAvailabilityHigh> oneTimeRec = QueryUtil.executeQuery(AhStatsAvailabilityHigh.class, new SortParams("time", false),
					new FilterParams("lower(mac)", values[0].toString()), 1);
		if (!oneTimeRec.isEmpty()) {
			lastRecordTime = oneTimeRec.get(0).getTime();
		}
		lastRecordTime = lastRecordTime - timeTigg * 60 *1000L * hourTigg;

		double latecny_rrt1=0;
		double latecny_rrt2=0;
		long latecny_rrt_count1=0;
		long latecny_rrt_count2=0;
		long latencyCount1=0;
		long latencyCount2=0;

		for(Object OneRec : lstInterfaceInfo) {
			long recTime;
			double vRrt1=0;
			double vRrt2=0;
			String vTunnelName = "";

			recTime = ((AhStatsLatencyHigh)OneRec).getTime();
			vTunnelName = ((AhStatsLatencyHigh)OneRec).getInterfName();
			if (vTunnelName.equals(vpn_latency_tunnel1_name)) {
				if (((AhStatsLatencyHigh)OneRec).getTargetStatus()==AhStatsLatencyHigh.TARGET_STATUS_UP){
					vRrt1 = ((AhStatsLatencyHigh)OneRec).getRtt();
				} else {
					vRrt1 = 0;
				}
			} else if (vTunnelName.equals(vpn_latency_tunnel2_name)) {
				if (((AhStatsLatencyHigh)OneRec).getTargetStatus()==AhStatsLatencyHigh.TARGET_STATUS_UP){
					vRrt2 = ((AhStatsLatencyHigh)OneRec).getRtt();
				} else {
					vRrt2 = 0;
				}
			}

			if (recTime<=lastRecordTime) {
				continue;
			} else if(currentTime==0 && recTime>lastRecordTime + timeTigg * 60 *1000L) {
				currentTime = lastRecordTime + (index) * timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;

				while (recTime>lastRecordTime + (index +1) * timeTigg * 60 *1000L){
					long reportTimeConvert=nextTime;
					vpn_latency_tunnel1.add(new TextItem(
							df.format((latecny_rrt1>0 && latecny_rrt_count1>0)?latecny_rrt1/latecny_rrt_count1:0),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					vpn_latency_tunnel2.add(new TextItem(
							df.format((latecny_rrt2>0 && latecny_rrt_count2>0)?latecny_rrt2/latecny_rrt_count2:0),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

					latecny_rrt1=0;
					latecny_rrt_count1=0;
					latecny_rrt2=0;
					latecny_rrt_count2=0;
					index++;
					currentTime = currentTime + timeTigg * 60 *1000L;
					nextTime=currentTime + timeTigg * 60 *1000L;

				}
			}
			if (currentTime==0) {
				currentTime = lastRecordTime;
				nextTime=currentTime + timeTigg * 60 *1000L;
			}

			if (recTime <= nextTime) {
				if (vTunnelName.equals(vpn_latency_tunnel1_name)) {
					latecny_rrt1 = latecny_rrt1 + vRrt1;
					if (vRrt1!=0){
						latecny_rrt_count1++;
						latencyCount1++;
					}
				} else if (vTunnelName.equals(vpn_latency_tunnel2_name)) {
					latecny_rrt2 = latecny_rrt2 + vRrt2;
					if (vRrt2!=0){
						latecny_rrt_count2++;
						latencyCount2++;
					}
				}
			} else {
				long reportTimeConvert=nextTime;
				vpn_latency_tunnel1.add(new TextItem(
						df.format((latecny_rrt1>0 && latecny_rrt_count1>0)?latecny_rrt1/latecny_rrt_count1:0),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				vpn_latency_tunnel2.add(new TextItem(
						df.format((latecny_rrt2>0 && latecny_rrt_count2>0)?latecny_rrt2/latecny_rrt_count2:0),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				latecny_rrt1=0;
				latecny_rrt_count1=0;
				latecny_rrt2=0;
				latecny_rrt_count2=0;
				index++;
				currentTime = currentTime + timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;

				while (recTime> currentTime){
					if (recTime<=nextTime) {
						if (vTunnelName.equals(vpn_latency_tunnel1_name)) {
							latecny_rrt1 = latecny_rrt1 + vRrt1;
							if (vRrt1!=0){
								latecny_rrt_count1++;
								latencyCount1++;
							}
						} else if (vTunnelName.equals(vpn_latency_tunnel2_name)) {
							latecny_rrt2 = latecny_rrt2 + vRrt2;
							if (vRrt2!=0){
								latecny_rrt_count2++;
								latencyCount2++;
							}
						}
						break;
					} else {
						reportTimeConvert=nextTime;
						vpn_latency_tunnel1.add(new TextItem(
								df.format((latecny_rrt1>0 && latecny_rrt_count1>0)?latecny_rrt1/latecny_rrt_count1:0),
								AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
						vpn_latency_tunnel2.add(new TextItem(
								df.format((latecny_rrt2>0 && latecny_rrt_count2>0)?latecny_rrt2/latecny_rrt_count2:0),
								AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

						latecny_rrt1=0;
						latecny_rrt_count1=0;
						latecny_rrt2=0;
						latecny_rrt_count2=0;
						index++;
						currentTime = currentTime + timeTigg * 60 *1000L;
						nextTime=currentTime + timeTigg * 60 *1000L;
					}
				}
			}

		}

		if (index!=0) {
			long reportTimeConvert=nextTime;
			vpn_latency_tunnel1.add(new TextItem(
					df.format((latecny_rrt1>0 && latecny_rrt_count1>0)?latecny_rrt1/latecny_rrt_count1:0),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			vpn_latency_tunnel2.add(new TextItem(
					df.format((latecny_rrt2>0 && latecny_rrt_count2>0)?latecny_rrt2/latecny_rrt_count2:0),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

			latecny_rrt1=0;
			latecny_rrt_count1=0;
			latecny_rrt2=0;
			latecny_rrt_count2=0;
			index++;
			currentTime = currentTime + timeTigg * 60 *1000L;
			nextTime=currentTime + timeTigg * 60 *1000L;

		}
		if (currentTime==0) {
			currentTime = lastRecordTime;
			nextTime=currentTime + timeTigg * 60 *1000L;
			latecny_rrt1=0;
			latecny_rrt_count1=0;
			latecny_rrt2=0;
			latecny_rrt_count2=0;
		}
		while (nextTime<=lastRecordTime + timeTigg * 60 *1000L * hourTigg){
			long reportTimeConvert=nextTime;
			vpn_latency_tunnel1.add(new TextItem(
					df.format((latecny_rrt1>0 && latecny_rrt_count1>0)?latecny_rrt1/latecny_rrt_count1:0),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			vpn_latency_tunnel2.add(new TextItem(
					df.format((latecny_rrt2>0 && latecny_rrt_count2>0)?latecny_rrt2/latecny_rrt_count2:0),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

			latecny_rrt1=0;
			latecny_rrt_count1=0;
			latecny_rrt2=0;
			latecny_rrt_count2=0;
			index++;
			currentTime = currentTime + timeTigg * 60 *1000L;
			nextTime=currentTime + timeTigg * 60 *1000L;
		}
	}

	/*end: used for vpn latency report.*/

	private boolean isNullString(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		return false;
	}

	private String deviceType;

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	/*start: br_network details*/
	public Collection<HmBo> load(HmBo bo) {
		if (null != bo) {
			if (bo instanceof VpnNetwork) {
				VpnNetwork network = (VpnNetwork) bo;
				if(network.getSubItems()!=null) network.getSubItems().size();
			}
			if(bo instanceof LanProfile) {
				LanProfile lanProfile = (LanProfile)bo;
				if(null != lanProfile.getUserProfileDefault()) {
					lanProfile.getUserProfileDefault().getId();
				}
				if(null != lanProfile.getUserProfileSelfReg()) {
					lanProfile.getUserProfileSelfReg().getId();
				}
				if(null != lanProfile.getRadiusUserProfile()) {
					lanProfile.getRadiusUserProfile().size();
					if (lanProfile.getRadiusUserProfile().size() > 0) {
						for (UserProfile userProfileTmp : lanProfile.getRadiusUserProfile()) {
							if (userProfileTmp != null) {
								userProfileTmp.getId();
							}
						}
					}
				}
			}
			if(bo instanceof ConfigTemplate) {
				ConfigTemplate config = (ConfigTemplate)bo;
				if (null != config.getVlanNetwork()) {
					config.getVlanNetwork().size();
					if (config.getVlanNetwork().size() > 0) {
						for (ConfigTemplateVlanNetwork oneItem : config.getVlanNetwork()) {
							VpnNetwork networkObj = oneItem.getNetworkObj();
							if (networkObj != null) {
								networkObj.getId();
							}
							Vlan vlan = oneItem.getVlan();
							if (vlan != null) {
								vlan.getId();
							}
						}
					}
				}
			}
		}
		return null;
	}

	private List<Map.Entry<String, List<MgtInterface4BrReport>>> networkList = null;

	public List<Map.Entry<String, List<MgtInterface4BrReport>>> getNetworkList() {
		return networkList;
	}

	public void setNetworkList(List<Map.Entry<String, List<MgtInterface4BrReport>>> networkList) {
		this.networkList = networkList;
	}

	public class MgtInterface4BrReport {
		private String mgtName;
		private String networkName;
		private String networkType;
		private String subnet;
		private String webSecurity;
		private String exactIP;

		public MgtInterface4BrReport() {
			this.mgtName = "";
			this.networkName = "";
			this.networkType = "";
			this.subnet = "";
			this.webSecurity = "";
			this.exactIP = "";
		}

		public MgtInterface4BrReport(String mgtName, String subnet, VpnNetwork vpnNetwork, HiveAp hiveAp) {
			this.mgtName = mgtName;
			this.subnet = subnet;
			this.networkName = vpnNetwork.getNetworkName();
			this.webSecurity = vpnNetwork.getWebSecurityString();
			if (hiveAp.getConfigTemplate() != null
					&& hiveAp.getConfigTemplate().getMgtNetwork() != null
					&& hiveAp.getConfigTemplate().getMgtNetwork().getId().equals(vpnNetwork.getId())) {
				this.networkType = "Management";
			} else {
				this.networkType = vpnNetwork.getNetworkTypeString();
			}
		}

		public String getMgtName() {
			return mgtName;
		}
		public void setMgtName(String mgtName) {
			this.mgtName = mgtName;
		}
		public String getNetworkName() {
			return networkName;
		}
		public void setNetworkName(String networkName) {
			this.networkName = networkName;
		}
		public String getNetworkType() {
			return networkType;
		}
		public void setNetworkType(String networkType) {
			this.networkType = networkType;
		}
		public String getSubnet() {
			return subnet;
		}
		public void setSubnet(String subnet) {
			this.subnet = subnet;
		}
		public String getWebSecurity() {
			return webSecurity;
		}
		public void setWebSecurity(String webSecurity) {
			this.webSecurity = webSecurity;
		}
		public String getExactIP() {
			return exactIP;
		}
		public void setExactIP(String exactIP) {
			this.exactIP = exactIP;
		}
		
	}

	private void prepareNetworkDetails4Br(HiveAp hiveAp, boolean blnSwitchAsBr) {
		List<?> lstMgts = QueryUtil.executeNativeQuery("select distinct networkid, hiveapmgtx, network from sub_network_resource where lower(hiveapmac)='"+hiveAp.getMacAddress().toLowerCase()+"'");
		if (lstMgts == null || lstMgts.size() < 1) {
			return;
		}
		
		// get networkid<-->Vlan mapping for SW works as BR.
		Map<Long, Vlan> netWorkVlan = new HashMap<Long, Vlan>(); // key: network ID, value: vlan object
		if (blnSwitchAsBr) {
			getSwAsBrNetworkVlanMapping(netWorkVlan, hiveAp);
		}

		Map<String, List<MgtInterface4BrReport>> networkMap = new HashMap<String, List<MgtInterface4BrReport>>();
		for (Object obj : lstMgts) {
			Object[] objs = (Object[]) obj;
			Long networkId = ((BigInteger) objs[0]).longValue();
			Short mgtId = ((Short) objs[1]).shortValue();
			String subnet = (String)objs[2];
			// if SwitchAsBr not ignore record with column value hiveapmgtx = -1
			if (!blnSwitchAsBr && mgtId == -1) {
				continue;
			}
			VpnNetwork vpnNetwork = QueryUtil.findBoById(VpnNetwork.class, networkId, this);
			if (vpnNetwork == null) {
				continue;
			}
			String mgtName = "";
			if (blnSwitchAsBr) {
				// if is SwitchAsBr, network not mapping to MGTX, so get network's VLAN name as interface name.
				Vlan vlan = netWorkVlan.get(networkId);
				if (vlan != null) {
					mgtName = vlan.getVlanName();
				} else {
					continue;
				}
			} else {
				mgtName = getMgtxName(mgtId/*, blnSwitchAsBr*/);
			}
			if (networkMap.containsKey(mgtName)) {
				List<MgtInterface4BrReport> vpnList2 = networkMap.get(mgtName);
				vpnList2.add(new MgtInterface4BrReport(mgtName, subnet, vpnNetwork, hiveAp));
				networkMap.put(mgtName, vpnList2);
			} else {
				List<MgtInterface4BrReport> vpnList = new ArrayList<MgtInterface4BrReport>();
				vpnList.add(new MgtInterface4BrReport(mgtName, subnet, vpnNetwork, hiveAp));
				networkMap.put(mgtName, vpnList);
			}
		}
		if (networkMap != null && networkMap.size() > 0) {
			networkList = new ArrayList<Map.Entry<String, List<MgtInterface4BrReport>>>(networkMap.entrySet());
			Collections.sort(networkList, new Comparator<Map.Entry<String, List<MgtInterface4BrReport>>>(){
				public int compare(Map.Entry<String, List<MgtInterface4BrReport>> o1, Map.Entry<String, List<MgtInterface4BrReport>> o2) {
					String str1 = (String)o1.getKey();
					String str2 = (String)o2.getKey();
					if (str1.length() > str2.length()) {
						return 1;
					} else if (str1.length() < str2.length()) {
						return -1;
					} else {
						return str1.compareTo(str2);
					}
				}
			});
		}
	}

	private String getMgtxName(Short mgtx/*, boolean blnSwitchAsBr*/) {
/*		if (blnSwitchAsBr) {
			// TODO mgtx value for SW(as BR) in DB still has problem, always -1, need zhangjie help to fix.
			return "";
		} else {*/
			if (mgtx == 0) {
				return "mgt0";
			} else if (mgtx > 0) {
				return "mgt0."+mgtx.toString();
			}
			return "";
//		}
	}
	
	private void getSwAsBrNetworkVlanMapping(Map<Long, Vlan> netWorkVlan, HiveAp hiveAp) {
		ConfigTemplate configTemplate = QueryUtil.findBoById(ConfigTemplate.class, hiveAp.getConfigTemplate().getId(), this);
		List<ConfigTemplateVlanNetwork> vlanNetwork = configTemplate.getVlanNetwork();
		if (vlanNetwork != null && !vlanNetwork.isEmpty()) {
			for (ConfigTemplateVlanNetwork oneItem : vlanNetwork) {
				if (oneItem.getNetworkObj() == null
						|| oneItem.getVlan() == null) {
					continue;
				}
				Vlan vlan = QueryUtil.findBoById(Vlan.class, oneItem.getVlan().getId());
				if (vlan != null) {
					netWorkVlan.put(oneItem.getNetworkObj().getId(), vlan);
				}
			}
		}
	}

	/*end: br_network details*/

	/*start: br_port details*/
	private List<DeviceInterfaceAdapter> deviceInterfaceAdapters = null;

	public List<DeviceInterfaceAdapter> getDeviceInterfaceAdapters() {
		return deviceInterfaceAdapters;
	}

	public void setDeviceInterfaceAdapters(
			List<DeviceInterfaceAdapter> deviceInterfaceAdapters) {
		this.deviceInterfaceAdapters = deviceInterfaceAdapters;
	}

	private void prepareBrPortDetails(HiveAp hiveAp) {
		DeviceInterfaceBundle portsBundle = new DeviceInterfaceBundle(hiveAp.getDeviceInterfaces(), hiveAp);
		portsBundle.initializeAccessModeString();
		portsBundle.preparePortsLinkStatus();
		deviceInterfaceAdapters = portsBundle.getSortedDeviceInterfaces();
	}
	
	/*end: br_port details*/

	/*start: br_detail information*/
	private String usbStatusString;
	private String wanAvailability;
	private String wanThroughput;

	public String getUsbStatusString() {
		return usbStatusString;
	}

	public void setUsbStatusString(String usbStatusString) {
		this.usbStatusString = usbStatusString;
	}

	public String getWanAvailability() {
		return wanAvailability;
	}

	public void setWanAvailability(String wanAvailability) {
		this.wanAvailability = wanAvailability;
	}

	public String getWanThroughput() {
		return wanThroughput;
	}

	public void setWanThroughput(String wanThroughput) {
		this.wanThroughput = wanThroughput;
	}

	private void prepareBrMoreDetails(HiveAp hiveAp) {
		prepareUsbStatus(hiveAp);
		prepareWanAvailability(hiveAp);
		prepareWanThroughput(hiveAp);
	}
	private String getWanUpStatus(HiveAp hiveAp,DeviceInterface dInterface,List<?> lstPortInfo) {
		String wanUpStatus = DeviceInterfaceUtil.DEVICE_INTERFACE_LINK_STATUS_FAILED;
		for(Object oneRec : lstPortInfo) {
			AhPortAvailability ahPort = (AhPortAvailability)oneRec;
			if (dInterface.getWanOrderName().equalsIgnoreCase(ahPort.getInterfName())) {
				if(AhPortAvailability.WAN_ACTIVE == ahPort.getWanactive() ){
					wanUpStatus = DeviceInterfaceUtil.DEVICE_INTERFACE_LINK_STATUS_OK;
					break;
				}
			}
		}
		return wanUpStatus;
	}

	private void prepareUsbStatus(HiveAp hiveAp) {
		String searchSQL = "lower(mac)=:s1 AND interfType=:s2";
		Object values[] = new Object[2];
		values[0] = hiveAp.getMacAddress().toLowerCase();
		values[1] = AhPortAvailability.INTERFACE_TYPE_WAN;
		List<?> lstPortInfo = QueryUtil.executeQuery(AhPortAvailability.class, null,
				new FilterParams(searchSQL, values));
		boolean usbPrimary = false;
		Map<Long, DeviceInterface> interfs = hiveAp.getDeviceInterfaces();
		if (interfs != null && !interfs.isEmpty()) {
			for (Long key : interfs.keySet()) {
				DeviceInterface interTmp = interfs.get(key);
				if (interTmp != null
						&& interTmp.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_USB
						&& hiveAp.getRole(interTmp) == AhInterface.ROLE_PRIMARY) {
					usbPrimary = true;
					break;
				}
			}
		}
		boolean eth0Succ = false;
		for(Object oneRec : lstPortInfo) {
			AhPortAvailability ahPort = (AhPortAvailability)oneRec;
			if (DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_ETH0.equalsIgnoreCase(ahPort.getInterfName())) {
				if (AhPortAvailability.INTERFACE_STATUS_UP == ahPort.getInterfStatus()) {
					eth0Succ = true;
				}
				break;
			}
		}
		setUsbStatusString(USB_STATUS_DISCONNECTED);
		for(Object oneRec : lstPortInfo) {
			AhPortAvailability ahPort = (AhPortAvailability)oneRec;
			if (DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_USB.equalsIgnoreCase(ahPort.getInterfName())) {
				if (AhPortAvailability.INTERFACE_STATUS_UP == ahPort.getInterfStatus()) {
					if (eth0Succ && usbPrimary == false) {
						setUsbStatusString(USB_STATUS_ACTIVE);
					} else {
						setUsbStatusString(USB_STATUS_CONNECTED);
					}
				} else {
					setUsbStatusString(USB_STATUS_DISCONNECTED);
				}
				break;
			}
		}
	}
	
	private byte prepareUsbStatusSw(HiveAp hiveAp) {
		String searchSQL = "lower(mac)=:s1 AND interfName=:s2";
		Object values[] = new Object[2];
		values[0] = StringUtils.lowerCase(hiveAp.getMacAddress());
		values[1] = DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_USB;
		
		List<?> lstUsbPortInfo = QueryUtil.executeQuery(AhPortAvailability.class, null,
				new FilterParams(searchSQL, values));
		if (!lstUsbPortInfo.isEmpty()) {
			AhPortAvailability usbPort = (AhPortAvailability)lstUsbPortInfo.get(0);
			return usbPort.getInterfStatus();
/*			if (AhPortAvailability.INTERFACE_STATUS_UP == usbPort.getInterfStatus()) {
				// connected and up
			} else if (AhPortAvailability.INTERFACE_STATUS_DOWN == usbPort.getInterfStatus()) {
				// connected but down
			} else if (AhPortAvailability.INTERFACE_STATUS_NOT_CONNECTED == usbPort.getInterfStatus()) {
				// not connected
			}*/
		} else {
			// can not get USB port info, treat as USB not connected
			return AhPortAvailability.INTERFACE_STATUS_NOT_CONNECTED;
		}
	}
	
	private void prepareWanAvailability(HiveAp hiveAp) {
		DecimalFormat df = new DecimalFormat("0.00");
		String searchSQL = "time >= :s1 AND lower(mac)=:s2 and interfType=:s3";

		Calendar reportDateTime = Calendar.getInstance(getDomain()
				.getTimeZone());
		//last one hour
		reportDateTime.add(Calendar.HOUR_OF_DAY, -1);
		Object values[] = new Object[3];
		values[0] = reportDateTime.getTimeInMillis();
		values[1] = hiveAp.getMacAddress().toLowerCase();
		values[2] = AhPortAvailability.INTERFACE_TYPE_WAN;

		List<?> lstInterfaceInfo = QueryUtil.executeQuery(AhStatsLatencyHigh.class, new SortParams("time"),
				new FilterParams(searchSQL, values));

		int totalUptime=0;
		int totalDowntime=0;

		for(Object OneRec : lstInterfaceInfo) {
			byte recStatus;

			recStatus = ((AhStatsLatencyHigh)OneRec).getTargetStatus();

			if (recStatus==AhStatsLatencyHigh.TARGET_STATUS_UP){
				totalUptime++;
			} else {
				totalDowntime++;
			}
		}
		if (totalUptime + totalDowntime > 0) {
			setWanAvailability(df.format(((float)totalUptime *100)/(totalUptime + totalDowntime)) + "%");
		} else {
			setWanAvailability("0.00%");
		}

	}

	private void prepareWanThroughput(HiveAp hiveAp) {
		DecimalFormat df = new DecimalFormat("0.00");
		String searchSQL = "time >= :s1 AND lower(mac)=:s2 and interfType=:s3";

		Calendar reportDateTime = Calendar.getInstance(getDomain()
				.getTimeZone());
		//last one hour
		reportDateTime.add(Calendar.HOUR_OF_DAY, -1);
		Object values[] = new Object[3];
		values[0] = reportDateTime.getTimeInMillis();
		values[1] = hiveAp.getMacAddress().toLowerCase();
		values[2] = AhPortAvailability.INTERFACE_TYPE_WAN;

		List<?> lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputHigh.class, new SortParams("time"),
				new FilterParams(searchSQL, values));

		long throughput_in=0;
		long throughput_out=0;

		for(Object OneRec : lstInterfaceInfo) {
			long vPutIn = 0;
			long vPutOut = 0;
			vPutIn = ((AhStatsThroughputHigh)OneRec).getRxBytes();
			vPutOut = ((AhStatsThroughputHigh)OneRec).getTxBytes();

			throughput_in = throughput_in + vPutIn;
			throughput_out = throughput_out + vPutOut;
		}

		setWanThroughput(df.format(((float)throughput_in + throughput_out)*8/1024/60));
	}

	/*end: br_detail information*/

	/*start: vpnGateway system details*/
	private DeviceInterface wanInterface;
	private DeviceInterface lanInterface;

	public DeviceInterface getWanInterface() {
		return wanInterface;
	}

	public void setWanInterface(DeviceInterface wanInterface) {
		this.wanInterface = wanInterface;
	}

	public DeviceInterface getLanInterface() {
		return lanInterface;
	}

	public void setLanInterface(DeviceInterface lanInterface) {
		this.lanInterface = lanInterface;
	}

	private void prepareVpnGatewayDeviceInterface(HiveAp hiveAp) {
		wanInterface = hiveAp.getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_ETH0);
		if(wanInterface == null){
			wanInterface = new DeviceInterface();
			wanInterface.setInterfaceName(MgrUtil.getUserMessage("hiveAp.vpnGateway.if.wan"));
			wanInterface.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH0);
		}

		lanInterface = hiveAp.getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_ETH1);
		if(lanInterface == null){
			lanInterface = new DeviceInterface();
			lanInterface.setInterfaceName(MgrUtil.getUserMessage("hiveAp.vpnGateway.if.lan"));
			lanInterface.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH1);
		}
	}

	/*end: vpnGateway system details*/

	/*start: vpnGateway_branch tunnel details*/
	public class AhVPNStatusReport {
		private String clientName;
		private String clientStatus;
		private String upTime;
		private String clientMac;
		private Long clientId;

		public AhVPNStatusReport() {
			clientStatus = "Up";
			clientId = null;
		}

		public String getClientName() {
			return clientName;
		}
		public void setClientName(String clientName) {
			this.clientName = clientName;
		}
		public String getClientStatus() {
			return clientStatus;
		}
		public void setClientStatus(String clientStatus) {
			this.clientStatus = clientStatus;
		}

		public String getUpTime() {
			return upTime;
		}

		public void setUpTime(String upTime) {
			this.upTime = upTime;
		}

		public String getClientMac() {
			return clientMac;
		}

		public void setClientMac(String clientMac) {
			this.clientMac = clientMac;
		}

		public Long getClientId() {
			return clientId;
		}

		public void setClientId(Long clientId) {
			this.clientId = clientId;
		}
	}

	private List<AhVPNStatusReport> vpnBranchTunnelList = null;

	private boolean inVpnTopology;

	private String vpnTopologyMap = null;

	private Long vpnServiceOfVpnGateway = null;

	public Long getVpnServiceOfVpnGateway() {
		return vpnServiceOfVpnGateway;
	}

	public void setVpnServiceOfVpnGateway(Long vpnServiceOfVpnGateway) {
		this.vpnServiceOfVpnGateway = vpnServiceOfVpnGateway;
	}

	public boolean isInVpnTopology() {
		return inVpnTopology;
	}

	public void setInVpnTopology(boolean inVpnTopology) {
		this.inVpnTopology = inVpnTopology;
	}

	public String getVpnTopologyMap() {
		return vpnTopologyMap;
	}

	public void setVpnTopologyMap(String vpnTopologyMap) {
		this.vpnTopologyMap = vpnTopologyMap;
	}

	public List<AhVPNStatusReport> getVpnBranchTunnelList() {
		return vpnBranchTunnelList;
	}

	public void setVpnBranchTunnelList(List<AhVPNStatusReport> vpnBranchTunnelList) {
		this.vpnBranchTunnelList = vpnBranchTunnelList;
	}

	private void prepareVpnGatewayBranchClients(HiveAp hiveAp) {
		List<AhVPNStatus> vpnBranches = QueryUtil.executeQuery(AhVPNStatus.class, new SortParams("connectTimeStamp"),
				new FilterParams("serverID", hiveAp.getMacAddress()));
		List<Long> connectedBrClient = new ArrayList<Long>();

		vpnBranchTunnelList = new ArrayList<AhVPNStatusReport>();
		if (vpnBranches != null && vpnBranches.size() > 0) {
			for (Iterator<AhVPNStatus> iter = vpnBranches.iterator(); iter.hasNext();) {
				AhVPNStatus item = (AhVPNStatus)iter.next();
				if (item.getClientID() == null || "".equals(item.getClientID())) {
					continue;
				}
				HiveAp hiveApBr = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", item.getClientID().trim(), new HiveApAction());
				if (hiveApBr == null) {
					continue;
				}
				connectedBrClient.add(hiveApBr.getId());
				AhVPNStatusReport reportItem = new AhVPNStatusReport();
				reportItem.setClientName(hiveApBr.getHostName());
				reportItem.setClientMac(item.getClientID().trim());
				reportItem.setClientId(hiveApBr.getId());
				if (item.getConnectTimeStamp() > 0) {
					reportItem.setUpTime(NmsUtil
							.transformTime((int) ((System.currentTimeMillis() - item.getConnectTimeStamp()) / 1000)));
				}
				vpnBranchTunnelList.add(reportItem);
			}
		}

		//then, get all branch routers with the same network policy as vpn gateway
		List<AhVPNStatusReport> unconnectBrLst = getUnconnectBrOfVpnGateway(hiveAp, connectedBrClient);
		if (unconnectBrLst != null && unconnectBrLst.size() > 0) {
			vpnBranchTunnelList.addAll(unconnectBrLst);
		}
	}

	private List<AhVPNStatusReport> getUnconnectBrOfVpnGateway(HiveAp hiveAp, List<Long> connectedBrs) {
		List<?> tmpIds = QueryUtil.executeNativeQuery(
				"select distinct a.id from vpn_service a, vpn_gateway_setting b where a.id = b.vpn_gateway_setting_id and b.hiveapid="+hiveAp.getId());
		if (tmpIds == null
				|| tmpIds.isEmpty()) {
			return null;
		}
		List<Long> vpnIds = new ArrayList<Long>(tmpIds.size());
		for (Object obj : tmpIds) {
			vpnIds.add(Long.valueOf(obj.toString()));
		}
		@SuppressWarnings("unchecked")
		List<Long> npIds = (List<Long>) QueryUtil.executeQuery(
				"select id from " + ConfigTemplate.class.getSimpleName(), 
				null, 
				new FilterParams("vpnService.id", vpnIds),
				hiveAp.getOwner().getId());
		if (npIds == null
				|| npIds.isEmpty()) {
			return null;
		}
		
		List<AhVPNStatusReport> rtnList = new ArrayList<AhVPNStatusReport>();

		String filterStr = "configTemplate.id in (:s1) and deviceType = :s2";
		Object[] filterArgs = new Object[2];
		filterArgs[0] = npIds;
		filterArgs[1] = HiveAp.Device_TYPE_BRANCH_ROUTER;
		List<HiveAp> brs = QueryUtil.executeQuery(HiveAp.class, 
				new SortParams("hostname"), 
				new FilterParams(filterStr, filterArgs),
				hiveAp.getOwner().getId());
		if (brs != null && brs.size() > 0) {
			for (HiveAp hiveApTmp : brs) {
				if (connectedBrs.contains(hiveApTmp.getId())) {
					continue;
				}
				AhVPNStatusReport reportItem = new AhVPNStatusReport();
				reportItem.setClientName(hiveApTmp.getHostName());
				reportItem.setClientMac(hiveApTmp.getMacAddress());
				reportItem.setClientId(hiveApTmp.getId());
				reportItem.setClientStatus("Down");
				reportItem.setUpTime("0");
				rtnList.add(reportItem);
			}
		}

		return rtnList;
	}

	private void prepareVpnGatewayTopologyMap(HiveAp hiveAp) {
		vpnServiceOfVpnGateway = getVpnServiceOfVpnGateway(hiveAp);
		if (vpnServiceOfVpnGateway == null) {
			inVpnTopology = false;
			return;
		}
		inVpnTopology = getHasVpnTopology(vpnServiceOfVpnGateway);
	}

	private Long getVpnServiceOfVpnGateway(HiveAp hiveAp) {
		Long vpnServiceId = null;
		if (hiveAp == null) return null;

		String sql = "select a.id from vpn_service a, vpn_gateway_setting b where a.id = b.vpn_gateway_setting_id and b.hiveapid = " + hiveAp.getId();

		List<?> objs = QueryUtil.executeNativeQuery(sql);

		if (objs != null && objs.size() > 0) {
			BigInteger obj = (BigInteger)objs.get(0);
			vpnServiceId = obj.longValue();
		}

		return vpnServiceId;
	}

	private boolean getHasVpnTopology(Long vpnServiceId) {
		VpnService vpn = QueryUtil.findBoById(VpnService.class, vpnServiceId);

		if(vpn == null) {
			return false;
		}

		/*
		 * get the VPN servers and clients which use this VPN service
		 * here, they are Long object id
		 */
		Set<Long> vpnServers = ConfigurationUtils.getRelevantVpnServers(vpn);

		if(vpnServers == null || vpnServers.isEmpty()) {
			return false;
		}

		/*
		 * get MAC address of servers
		 */
		List<?> servers = QueryUtil.executeQuery("select bo.macAddress from "
						+ HiveAp.class.getSimpleName() + " bo",
				null, new FilterParams("id", vpnServers));

		if(servers == null || servers.isEmpty()) {
			return false;
		}

		/*
		 * get count of VPN status
		 */
		String where = "serverID in (:s1)";
		long count = QueryUtil.findRowCount(AhVPNStatus.class,new FilterParams(where, new Object[] {
						servers}));

		return count > 0;
	}
	/*end: vpnGateway_branch tunnel details*/

	/*start: vpnGateway reports*/
	private List<TextItem> cvg_vpn_availability = null;
	private List<TextItem> cvg_vpn_uptime = null;
	private List<TextItem> cvg_wan_availability = null;
	private List<TextItem> cvg_wan_uptime = null;
	private List<TextItem> cvg_wan_throughput_in = null;
	private List<TextItem> cvg_wan_throughput_out = null;

	public List<TextItem> getCvg_vpn_availability() {
		return cvg_vpn_availability;
	}

	public void setCvg_vpn_availability(List<TextItem> cvg_vpn_availability) {
		this.cvg_vpn_availability = cvg_vpn_availability;
	}

	public List<TextItem> getCvg_vpn_uptime() {
		return cvg_vpn_uptime;
	}

	public void setCvg_vpn_uptime(List<TextItem> cvg_vpn_uptime) {
		this.cvg_vpn_uptime = cvg_vpn_uptime;
	}

	public List<TextItem> getCvg_wan_availability() {
		return cvg_wan_availability;
	}

	public void setCvg_wan_availability(List<TextItem> cvg_wan_availability) {
		this.cvg_wan_availability = cvg_wan_availability;
	}

	public List<TextItem> getCvg_wan_uptime() {
		return cvg_wan_uptime;
	}

	public void setCvg_wan_uptime(List<TextItem> cvg_wan_uptime) {
		this.cvg_wan_uptime = cvg_wan_uptime;
	}

	public List<TextItem> getCvg_wan_throughput_in() {
		return cvg_wan_throughput_in;
	}

	public void setCvg_wan_throughput_in(List<TextItem> cvg_wan_throughput_in) {
		this.cvg_wan_throughput_in = cvg_wan_throughput_in;
	}

	public List<TextItem> getCvg_wan_throughput_out() {
		return cvg_wan_throughput_out;
	}

	public void setCvg_wan_throughput_out(List<TextItem> cvg_wan_throughput_out) {
		this.cvg_wan_throughput_out = cvg_wan_throughput_out;
	}

	private void prepareCVGWanAvailability(HiveAp hiveAp) {
		DecimalFormat df = new DecimalFormat("0.00");
		String searchSQL = "lower(mac)=:s1";

		Object values[] = new Object[1];
		values[0] = hiveAp.getMacAddress().toLowerCase();

		List<?> lstInterfaceInfo = QueryUtil.executeQuery(AhStatsAvailabilityHigh.class, new SortParams("time"),
				new FilterParams(searchSQL, values));

		long timeTigg=1;
		int totalBarCount=60;
		int totalUptime=0;
		int[] upCount= new int[totalBarCount];
		int index=0;
		long currentTime=0;
		long nextTime=0;
		long oneTimeRecordCount=0;
		long totalTimeRecordtime=0;

		cvg_wan_availability = new ArrayList<TextItem>();
		cvg_wan_uptime = new ArrayList<TextItem>();

		long lastRecordTime=System.currentTimeMillis();
		List<AhStatsAvailabilityHigh> oneTimeRec = QueryUtil.executeQuery(AhStatsAvailabilityHigh.class, new SortParams("time", false),
				new FilterParams("lower(mac)", values[0].toString()), 1);
		if (!oneTimeRec.isEmpty()) {
			lastRecordTime = oneTimeRec.get(0).getTime();
		}
		lastRecordTime = lastRecordTime - timeTigg * 60 *1000L * totalBarCount;

		for(Object OneRec : lstInterfaceInfo) {
			long recTime;
			byte recStatus;
			int activeStatus=0;

			recTime = ((AhStatsAvailabilityHigh)OneRec).getTime();
			recStatus = ((AhStatsAvailabilityHigh)OneRec).getInterfStatus();
			activeStatus = ((AhStatsAvailabilityHigh)OneRec).getInterfActive();
			if (activeStatus==0) {
				continue;
			}

			if (recTime<=lastRecordTime) {
				continue;
			} else if(currentTime==0 && recTime>lastRecordTime + timeTigg * 60 *1000L) {
				currentTime = lastRecordTime + (index) * timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;
				oneTimeRecordCount = 1;
				totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;

				while (recTime>lastRecordTime + (index +1) * timeTigg * 60 *1000L){
					long reportTimeConvert=nextTime;
					cvg_wan_availability.add(new TextItem(
							df.format(0),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					cvg_wan_uptime.add(new TextItem(
							df.format(0),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					if (index <(totalBarCount-1)){
						index++;
						currentTime = currentTime + timeTigg * 60 *1000L;
						nextTime=currentTime + timeTigg * 60 *1000L;
						oneTimeRecordCount = 1;
						totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
					} else {
						break;
					}
				}
			}

			if (currentTime==0) {
				currentTime = lastRecordTime;
				nextTime=currentTime + timeTigg * 60 *1000L;
				oneTimeRecordCount = 1;
				totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
			}

			if (recTime <= nextTime) {
				if (recStatus==AhPortAvailability.INTERFACE_STATUS_UP){
					upCount[index]++;
					totalUptime++;
				}
			} else {
				long reportTimeConvert=nextTime;
				cvg_wan_availability.add(new TextItem(
						df.format(((float)upCount[index] *100)/(oneTimeRecordCount)),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				cvg_wan_uptime.add(new TextItem(
						df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

				if (index<(totalBarCount-1)){
					index++;
					currentTime = currentTime + timeTigg * 60 *1000L;
					nextTime=currentTime + timeTigg * 60 *1000L;
					oneTimeRecordCount = 1;
					totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;

					while (recTime> currentTime && index <totalBarCount){
						if (recTime<=nextTime) {
							if (recStatus==AhPortAvailability.INTERFACE_STATUS_UP){
								upCount[index]++;
								totalUptime++;
							}
							break;
						} else {
							reportTimeConvert=nextTime;
							cvg_wan_availability.add(new TextItem(
									df.format(((float)upCount[index] *100)/(oneTimeRecordCount)),
									AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

							cvg_wan_uptime.add(new TextItem(
									df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
									AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
							if (index <(totalBarCount-1)){
								index++;
								currentTime = currentTime + timeTigg * 60 *1000L;
								nextTime=currentTime + timeTigg * 60 *1000L;
								oneTimeRecordCount = 1;
								totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
							} else {
								break;
							}
						}
					}
				}
			}
		}
		if (cvg_wan_availability.size()<totalBarCount && index!=0) {
			long reportTimeConvert=nextTime;
			cvg_wan_availability.add(new TextItem(
					df.format(((float)upCount[index] *100)/(oneTimeRecordCount)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

			cvg_wan_uptime.add(new TextItem(
					df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
		}
	}

	private void prepareCVGWanThroughput(HiveAp hiveAp) {
		DecimalFormat df = new DecimalFormat("0.00");
		String searchSQL = "lower(mac)=:s1 and interfType=:s2";

		Object values[] = new Object[2];
		values[0] = hiveAp.getMacAddress().toLowerCase();
		values[1] = AhPortAvailability.INTERFACE_TYPE_WAN;

		List<?> lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputHigh.class, new SortParams("time"),
				new FilterParams(searchSQL, values));

		long timeTigg=1;
		long hourTigg=60;
		int index=0;
		long currentTime=0;
		long nextTime=0;

		long throughput_in=0;
		long throughput_out=0;
		cvg_wan_throughput_in = new ArrayList<TextItem>();
		cvg_wan_throughput_out = new ArrayList<TextItem>();

		long lastRecordTime=System.currentTimeMillis();
		List<AhStatsAvailabilityHigh> oneTimeRec = QueryUtil.executeQuery(AhStatsAvailabilityHigh.class, new SortParams("time", false),
				new FilterParams("lower(mac)", values[0].toString()), 1);
		if (!oneTimeRec.isEmpty()) {
			lastRecordTime = oneTimeRec.get(0).getTime();
		}
		lastRecordTime = lastRecordTime - timeTigg * 60 *1000L * hourTigg;
		long timePerPoint = timeTigg * 60;
		
		for(Object OneRec : lstInterfaceInfo) {
			long recTime;
			long vPutIn=0;
			long vPutOut=0;

			recTime = ((AhStatsThroughputHigh)OneRec).getTime();
			vPutIn = ((AhStatsThroughputHigh)OneRec).getRxBytes();
			vPutOut = ((AhStatsThroughputHigh)OneRec).getTxBytes();

			if (recTime<=lastRecordTime) {
				continue;
			} else if(currentTime==0 && recTime>lastRecordTime + timeTigg * 60 *1000L) {
				currentTime = lastRecordTime + (index) * timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;

				while (recTime>lastRecordTime + (index +1) * timeTigg * 60 *1000L){
					long reportTimeConvert=nextTime;
					cvg_wan_throughput_in.add(new TextItem(
							df.format(((float)throughput_in)*8/1024/timePerPoint),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					cvg_wan_throughput_out.add(new TextItem(
							df.format(((float)throughput_out*8/1024/timePerPoint)),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

					throughput_in=0;
					throughput_out=0;
					index++;
					currentTime = currentTime + timeTigg * 60 *1000L;
					nextTime=currentTime + timeTigg * 60 *1000L;
				}
			}
			if (currentTime==0) {
				currentTime = lastRecordTime;
				nextTime=currentTime + timeTigg * 60 *1000L;
			}

			if (recTime <= nextTime) {
				throughput_in = throughput_in + vPutIn;
				throughput_out = throughput_out + vPutOut;
			} else {
				long reportTimeConvert=nextTime;
				cvg_wan_throughput_in.add(new TextItem(
						df.format(((float)throughput_in)*8/1024/timePerPoint),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				cvg_wan_throughput_out.add(new TextItem(
						df.format(((float)throughput_out*8/1024/timePerPoint)),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

				throughput_in=0;
				throughput_out=0;

				index++;
				currentTime = currentTime + timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;

				while (recTime> currentTime){
					if (recTime<=nextTime) {
						throughput_in = throughput_in + vPutIn;
						throughput_out = throughput_out + vPutOut;
						break;
					} else {
						reportTimeConvert=nextTime;
						cvg_wan_throughput_in.add(new TextItem(
								df.format(((float)throughput_in)*8/1024/timePerPoint),
								AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
						cvg_wan_throughput_out.add(new TextItem(
								df.format(((float)throughput_out*8/1024/timePerPoint)),
								AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

						throughput_in=0;
						throughput_out=0;

						index++;
						currentTime = currentTime + timeTigg * 60 *1000L;
						nextTime=currentTime + timeTigg * 60 *1000L;
					}
				}
			}
		}
		if (index!=0) {
			long reportTimeConvert=nextTime;
			cvg_wan_throughput_in.add(new TextItem(
					df.format(((float)throughput_in)*8/1024/timePerPoint),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			cvg_wan_throughput_out.add(new TextItem(
					df.format(((float)throughput_out*8/1024/timePerPoint)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			throughput_in=0;
			throughput_out=0;

			index++;
			currentTime = currentTime + timeTigg * 60 *1000L;
			nextTime=currentTime + timeTigg * 60 *1000L;
		}
		if (currentTime==0) {
			currentTime = lastRecordTime;
			nextTime=currentTime + timeTigg * 60 *1000L;
			throughput_in=0;
			throughput_out=0;
		}

		while (nextTime <=lastRecordTime + timeTigg * 60 *1000L * hourTigg) {
			long reportTimeConvert=nextTime;
			cvg_wan_throughput_in.add(new TextItem(
					df.format(((float)throughput_in)*8/1024/timePerPoint),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			cvg_wan_throughput_out.add(new TextItem(
					df.format(((float)throughput_out*8/1024/timePerPoint)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			throughput_in=0;
			throughput_out=0;
			index++;
			currentTime = currentTime + timeTigg * 60 *1000L;
			nextTime=currentTime + timeTigg * 60 *1000L;
		}
	}

	private void prepareCVGVpnAvailability(HiveAp hiveAp) {
		long timeTigg=1;

		DecimalFormat df = new DecimalFormat("0.00");
		String searchSQL = "lower(mac)=:s1";

		Object values[] = new Object[1];
		values[0] = hiveAp.getMacAddress().toLowerCase();

		List<?> lstInterfaceInfo = QueryUtil.executeQuery(AhStatsVpnStatusHigh.class, new SortParams("time"),
				new FilterParams(searchSQL, values));

		int totalBarCount=60;
		int[] upCount= new int[totalBarCount];

		int index=0;
		long currentTime=0;
		long nextTime=0;
		long oneTimeRecordCount=0;
		long totalTimeRecordtime=0;
		long totalUptime=0;
		long tunnelCount = calTunnelCountForCVGReport(hiveAp);
		if (tunnelCount <= 0) {
			return;
		}
		cvg_vpn_uptime= new ArrayList<TextItem>();
		cvg_vpn_availability= new ArrayList<TextItem>();

		long lastRecordTime=System.currentTimeMillis();
		if (!lstInterfaceInfo.isEmpty()) {
			lastRecordTime = ((AhStatsVpnStatusHigh)lstInterfaceInfo.get(lstInterfaceInfo.size()-1)).getTime();
		}
		lastRecordTime = lastRecordTime - timeTigg * 60 *1000L * totalBarCount;

		for(Object OneRec : lstInterfaceInfo) {
			long recTime;
			int recTunnelCount;
			recTime = ((AhStatsVpnStatusHigh)OneRec).getTime();
			recTunnelCount = ((AhStatsVpnStatusHigh)OneRec).getTunnelCount();

			if (recTime<=lastRecordTime) {
				continue;
			} else if(currentTime==0 && recTime>lastRecordTime + timeTigg * 60 *1000L) {
				currentTime = lastRecordTime + (index) * timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;
				oneTimeRecordCount = 1;
				totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;

				while (recTime>lastRecordTime + (index +1) * timeTigg * 60 *1000L){
					long reportTimeConvert=nextTime;
					cvg_vpn_availability.add(new TextItem(
							df.format(((float)upCount[index] *100)/(oneTimeRecordCount * tunnelCount)),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

					cvg_vpn_uptime.add(new TextItem(
							df.format(((float)totalUptime *100)/(totalTimeRecordtime * tunnelCount)),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

					if (index <(totalBarCount-1)){
						index++;
						currentTime = currentTime + timeTigg * 60 *1000L;
						nextTime=currentTime + timeTigg * 60 *1000L;
						oneTimeRecordCount = 1;
						totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
					} else {
						break;
					}
				}
			}
			if (currentTime==0) {
				currentTime = lastRecordTime;
				nextTime=currentTime + timeTigg * 60 *1000L;
				oneTimeRecordCount = 1;
				totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
			}

			if (recTime <= nextTime) {
				upCount[index] = upCount[index] + recTunnelCount;
				totalUptime = totalUptime + recTunnelCount;

			} else {
				long reportTimeConvert=nextTime;
				cvg_vpn_availability.add(new TextItem(
						df.format(((float)upCount[index] *100)/(oneTimeRecordCount * tunnelCount)),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

				cvg_vpn_uptime.add(new TextItem(
						df.format(((float)totalUptime *100)/(totalTimeRecordtime * tunnelCount)),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				if (index<(totalBarCount-1)){
					index++;
					currentTime = currentTime + timeTigg * 60 *1000L;
					nextTime=currentTime + timeTigg * 60 *1000L;
					oneTimeRecordCount = 1;
					totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;

					while (recTime> currentTime && index <totalBarCount){
						if (recTime<=nextTime) {
							upCount[index] = upCount[index] + recTunnelCount;
							totalUptime = totalUptime + recTunnelCount;
							break;
						} else {
							reportTimeConvert=nextTime;
							cvg_vpn_availability.add(new TextItem(
									df.format(((float)upCount[index] *100)/(oneTimeRecordCount * tunnelCount)),
									AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

							cvg_vpn_uptime.add(new TextItem(
									df.format(((float)totalUptime *100)/(totalTimeRecordtime * tunnelCount)),
									AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
							if (index <(totalBarCount-1)){
								index++;
								currentTime = currentTime + timeTigg * 60 *1000L;
								nextTime=currentTime + timeTigg * 60 *1000L;
								oneTimeRecordCount = 1;
								totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
							} else {
								break;
							}
						}
					}
				}
			}
		}
		if (cvg_vpn_availability.size()<totalBarCount && index!=0) {
			long reportTimeConvert=nextTime;
			cvg_vpn_availability.add(new TextItem(
					df.format(((float)upCount[index] *100)/(oneTimeRecordCount * tunnelCount)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));

			cvg_vpn_uptime.add(new TextItem(
					df.format(((float)totalUptime *100)/(totalTimeRecordtime * tunnelCount)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
		}
	}

	/*end: vpnGateway reports*/

	private boolean hasCalculateBrFlashData() {
		String sessionKey = "calculateBrFlashData_done";
		String hasDone = (String) MgrUtil.getSessionAttribute(sessionKey);
		if ("done".equals(hasDone)) {
			String preFix = "br_flash_data_";
			vpn_latency_tunnel1 = (List<TextItem>)MgrUtil.getSessionAttribute(preFix+"vpn_latency_tunnel1");
			vpn_latency_tunnel2 = (List<TextItem>)MgrUtil.getSessionAttribute(preFix+"vpn_latency_tunnel2");
			vpn_latency_tunnel1_name = (String)MgrUtil.getSessionAttribute(preFix+"vpn_latency_tunnel1_name");
			vpn_latency_tunnel2_name = (String)MgrUtil.getSessionAttribute(preFix+"vpn_latency_tunnel2_name");
			vpn_throughput_tunnel1_in = (List<TextItem>)MgrUtil.getSessionAttribute(preFix+"vpn_throughput_tunnel1_in");
			vpn_throughput_tunnel1_out = (List<TextItem>)MgrUtil.getSessionAttribute(preFix+"vpn_throughput_tunnel1_out");
			vpn_throughput_tunnel2_in = (List<TextItem>)MgrUtil.getSessionAttribute(preFix+"vpn_throughput_tunnel2_in");
			vpn_throughput_tunnel2_out = (List<TextItem>)MgrUtil.getSessionAttribute(preFix+"vpn_throughput_tunnel2_out");
			vpn_tunnel1_name = (String)MgrUtil.getSessionAttribute(preFix+"vpn_tunnel1_name");
			vpn_tunnel2_name = (String)MgrUtil.getSessionAttribute(preFix+"vpn_tunnel2_name");
			vpn_availability = (List<TextItem>)MgrUtil.getSessionAttribute(preFix+"vpn_availability");
			vpn_uptime = (List<TextItem>)MgrUtil.getSessionAttribute(preFix+"vpn_uptime");
			return true;
		}
		return false;
	}

	private void setCalculateBrFlashData2Session() {
		String preFix = "br_flash_data_";
		MgrUtil.setSessionAttribute(preFix+"vpn_latency_tunnel1", vpn_latency_tunnel1);
		MgrUtil.setSessionAttribute(preFix+"vpn_latency_tunnel2", vpn_latency_tunnel2);
		MgrUtil.setSessionAttribute(preFix+"vpn_latency_tunnel1_name", vpn_latency_tunnel1_name);
		MgrUtil.setSessionAttribute(preFix+"vpn_latency_tunnel2_name", vpn_latency_tunnel2_name);
		MgrUtil.setSessionAttribute(preFix+"vpn_throughput_tunnel1_in", vpn_throughput_tunnel1_in);
		MgrUtil.setSessionAttribute(preFix+"vpn_throughput_tunnel1_out", vpn_throughput_tunnel1_out);
		MgrUtil.setSessionAttribute(preFix+"vpn_throughput_tunnel2_in", vpn_throughput_tunnel2_in);
		MgrUtil.setSessionAttribute(preFix+"vpn_throughput_tunnel2_out", vpn_throughput_tunnel2_out);
		MgrUtil.setSessionAttribute(preFix+"vpn_tunnel1_name", vpn_tunnel1_name);
		MgrUtil.setSessionAttribute(preFix+"vpn_tunnel2_name", vpn_tunnel2_name);
		MgrUtil.setSessionAttribute(preFix+"vpn_availability", vpn_availability);
		MgrUtil.setSessionAttribute(preFix+"vpn_uptime", vpn_uptime);
		MgrUtil.setSessionAttribute("calculateBrFlashData_done", "done");
	}

	private void removeBrSessionFlashData() {
		String preFix = "br_flash_data_";
		MgrUtil.removeSessionAttribute(preFix+"vpn_latency_tunnel1");
		MgrUtil.removeSessionAttribute(preFix+"vpn_latency_tunnel2");
		MgrUtil.removeSessionAttribute(preFix+"vpn_latency_tunnel1_name");
		MgrUtil.removeSessionAttribute(preFix+"vpn_latency_tunnel2_name");
		MgrUtil.removeSessionAttribute(preFix+"vpn_throughput_tunnel1_in");
		MgrUtil.removeSessionAttribute(preFix+"vpn_throughput_tunnel1_out");
		MgrUtil.removeSessionAttribute(preFix+"vpn_throughput_tunnel2_in");
		MgrUtil.removeSessionAttribute(preFix+"vpn_throughput_tunnel2_out");
		MgrUtil.removeSessionAttribute(preFix+"vpn_tunnel1_name");
		MgrUtil.removeSessionAttribute(preFix+"vpn_tunnel2_name");
		MgrUtil.removeSessionAttribute(preFix+"vpn_availability");
		MgrUtil.removeSessionAttribute(preFix+"vpn_uptime");
		MgrUtil.removeSessionAttribute("calculateBrFlashData_done");
	}

	private boolean hasCalculateCVGFlashData() {
		String sessionKey = "calculateCVGFlashData_done";
		String hasDone = (String) MgrUtil.getSessionAttribute(sessionKey);
		if ("done".equals(hasDone)) {
			String preFix = "cvg_flash_data_";
			cvg_vpn_availability = (List<TextItem>)MgrUtil.getSessionAttribute(preFix+"cvg_vpn_availability");
			cvg_vpn_uptime = (List<TextItem>)MgrUtil.getSessionAttribute(preFix+"cvg_vpn_uptime");
			cvg_wan_availability = (List<TextItem>)MgrUtil.getSessionAttribute(preFix+"cvg_wan_availability");
			cvg_wan_uptime = (List<TextItem>)MgrUtil.getSessionAttribute(preFix+"cvg_wan_uptime");
			cvg_wan_throughput_in = (List<TextItem>)MgrUtil.getSessionAttribute(preFix+"cvg_wan_throughput_in");
			cvg_wan_throughput_out = (List<TextItem>)MgrUtil.getSessionAttribute(preFix+"cvg_wan_throughput_out");
			return true;
		}
		return false;
	}

	private void setCalculateCVGFlashData2Session() {
		String preFix = "cvg_flash_data_";
		MgrUtil.setSessionAttribute(preFix+"cvg_vpn_availability", cvg_vpn_availability);
		MgrUtil.setSessionAttribute(preFix+"cvg_vpn_uptime", cvg_vpn_uptime);
		MgrUtil.setSessionAttribute(preFix+"cvg_wan_availability", cvg_wan_availability);
		MgrUtil.setSessionAttribute(preFix+"cvg_wan_uptime", cvg_wan_uptime);
		MgrUtil.setSessionAttribute(preFix+"cvg_wan_throughput_in", cvg_wan_throughput_in);
		MgrUtil.setSessionAttribute(preFix+"cvg_wan_throughput_out", cvg_wan_throughput_out);
		MgrUtil.setSessionAttribute("calculateCVGFlashData_done", "done");
	}

	private void removeCVGSessionFlashData() {
		String preFix = "cvg_flash_data_";
		MgrUtil.removeSessionAttribute(preFix+"cvg_vpn_availability");
		MgrUtil.removeSessionAttribute(preFix+"cvg_vpn_uptime");
		MgrUtil.removeSessionAttribute(preFix+"cvg_wan_availability");
		MgrUtil.removeSessionAttribute(preFix+"cvg_wan_uptime");
		MgrUtil.removeSessionAttribute(preFix+"cvg_wan_throughput_in");
		MgrUtil.removeSessionAttribute(preFix+"cvg_wan_throughput_out");
		MgrUtil.removeSessionAttribute("calculateCVGFlashData_done");
	}

	private boolean getReportDataFromDevice(HiveAp hiveAp) {
		long  retLong = AhAppContainer.HmBe.getPerformModule().getBeInterfaceReportProcessor().fetchReportData(hiveAp.getMacAddress(), AhReport.REPORT_PERIOD_VPN_ONEHOUR);
		if (retLong!=BeInterfaceReportProcessor.QUERY_OK && retLong!=BeInterfaceReportProcessor.QUERY_DONE){
			return false;
		}
		return true;
	}
	
	public class PageLayouts {
		public PageLayouts(HiveAp hiveAp) {
			if (hiveAp != null) {
				prepareLayoutValues(hiveAp);
			}
		}
		private void prepareLayoutValues(HiveAp hiveAp) {
			if (blnSwitchAsBr) {
				// must put before normal BR
				blnNetworkDetails = true;
				blnPortDetails = true;
				blnNeighborDetails = true;
				blnClientDetails = true;
				blnEventDetails = true;
			} else if (HiveAp.Device_TYPE_HIVEAP == hiveAp.getDeviceType()) {
				blnEventDetails = true;
				if (hiveAp.isCVGAppliance()) {
					//blnTopoMap = false;
					return;
				}
				blnRadioDetails = true;
				blnNeighborDetails = true;
				blnClientDetails = true;
				if (hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR100
						&& hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200) {
					blnIdpReportDetails = true;
				}
			} else if (HiveAp.Device_TYPE_VPN_GATEWAY == hiveAp.getDeviceType()) {
				blnVpnTunnelDetails = true;
				blnEventDetails = true;
			} else if (HiveAp.Device_TYPE_BRANCH_ROUTER == hiveAp.getDeviceType()) {
				blnNetworkDetails = true;
				blnPortDetails = true;
				if (HiveAp.HIVEAP_MODEL_BR200 != hiveAp.getHiveApModel()) {
					blnRadioDetails = true;
				}
				if (hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR100
						&& hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200) {
					blnIdpReportDetails = true;
				}
				blnNeighborDetails = true;
				blnClientDetails = true;
				blnEventDetails = true;
			} else if (HiveAp.Device_TYPE_SWITCH == hiveAp.getDeviceType()) {
				blnNetworkDetails = false;
				blnPortDetails = true;
				blnNeighborDetails = true;
				blnClientDetails = true;
				blnEventDetails = true;
			}
		}
		
		private boolean blnTopoMap = true;
		private boolean blnVpnTunnelDetails;
		private boolean blnNetworkDetails;
		private boolean blnPortDetails;
		private boolean blnRadioDetails;
		private boolean blnNeighborDetails;
		private boolean blnClientDetails;
		private boolean blnIdpReportDetails;
		private boolean blnEventDetails;
		
		public boolean isBlnNetworkDetails() {
			return blnNetworkDetails;
		}
		public boolean isBlnPortDetails() {
			return blnPortDetails;
		}
		public boolean isBlnRadioDetails() {
			return blnRadioDetails;
		}
		public boolean isBlnNeighborDetails() {
			return blnNeighborDetails;
		}
		public boolean isBlnClientDetails() {
			return blnClientDetails;
		}
		public boolean isBlnIdpReportDetails() {
			return blnIdpReportDetails;
		}
		public boolean isBlnEventDetails() {
			return blnEventDetails;
		}
		public boolean isBlnVpnTunnelDetails() {
			return blnVpnTunnelDetails;
		}
		public boolean isBlnTopoMap() {
			return blnTopoMap;
		}
		
	}
	
	private PageLayouts pageLayouts;
	
	public PageLayouts getPageLayouts() {
		return pageLayouts;
	}
	private void preparePageLayouts(HiveAp hiveAp) {
		pageLayouts = new PageLayouts(hiveAp);
	}

	/**
	 * section for PSE status start
	 */
	private PSEStatusBundle pseBundle = null;
	
	public PSEStatusBundle getPseBundle() {
		if (this.pseBundle == null) {
			this.pseBundle = new PSEStatusBundle(getDataSource());
		}
		return this.pseBundle;
	}
	
	/**
	 * section for PSE status start
	 */
	
	/**
	 * for switch
	 */
	private void prepareSwPortDetails(HiveAp hiveAp) {
		
		String macAddress = hiveAp.getMacAddress();
		macAddress = (macAddress == null ? macAddress : macAddress.toLowerCase());
		FilterParams filterParams = new FilterParams(
				"owner.id = :s1 and lower(mac) = :s2", new Object[] {
						hiveAp.getOwner().getId(), macAddress });
		swPortStatsList = QueryUtil.executeQuery(AhSwitchPortStats.class, new SortParams("id"), filterParams);
	}
	
	private AhSwitchPortStats getSwPortStats(String macAddress, String portName) {
		
		FilterParams filterParams = new FilterParams(
				"lower(mac) = :s1 and lower(portName) = :s2", new Object[] {
						StringUtils.lowerCase(macAddress),  StringUtils.lowerCase(portName)});
		List<AhSwitchPortStats> swPortStatsList = QueryUtil.executeQuery(AhSwitchPortStats.class, null, filterParams);
		if (swPortStatsList != null && swPortStatsList.size() > 0) {
			return swPortStatsList.get(0);
		} else {
			return null;
		}
	}
	
	private void prepareLldpInformation(HiveAp hiveAp) {
		
		String macAddress = hiveAp.getMacAddress();
		macAddress = (macAddress == null ? macAddress : macAddress.toLowerCase());
		FilterParams filterParams = new FilterParams(
				"owner.id = :s1 and lower(reporter) = :s2", new Object[] {
						hiveAp.getOwner().getId(), macAddress });
		lldpInformationList = QueryUtil.executeQuery(AhLLDPInformation.class, new SortParams("id"), filterParams);
	}
	
	private void prepareSystemInfos(HiveAp hiveAp) {
		try {
			BeSwitchSystemInfoEvent event = new BeSwitchSystemInfoEvent();
			int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
			event.setAp(hiveAp);
			event.setSequenceNum(sequenceNum);
			event.buildPacket();
//			HmBeCommunicationUtil.sendRequest(event);
			BeCommunicationEvent response = HmBeCommunicationUtil
					.sendSyncRequest(event, 10);
			String msg = parseSwitchSystemInfoResult(response);
			log.info("prepareSystemInfos", msg);
		} catch (Exception e) {
			DebugUtil.topoDebugError("Device Mac:" + hiveAp.getMacAddress()
					+ " send Switch system info query request Failed.", e);
		}
	}

	private String parseSwitchSystemInfoResult(BeCommunicationEvent event) {
		String msg = "";
		if (null == event) {
			// error.
			log.error("parseSwitchSystemInfoResult", "the parameter event is null!!");
			return msg;
		}
		int msgType = event.getMsgType();
		if (msgType == BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP) {
			// failure
			BeCapwapClientEvent response = (BeCapwapClientEvent) event;
			short queryType = response.getQueryType();
			if (queryType == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SWITCH_SYSTEM_INFO) {
				BeSwitchSystemInfoEvent res = (BeSwitchSystemInfoEvent) response;
				byte r = res.getResult();
				if (BeCommunicationConstant.RESULTTYPE_NOFSM == r
						|| BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT == r
						|| BeCommunicationConstant.RESULTTYPE_TIMEOUT == r) {
					msg = MgrUtil.getUserMessage("error.capwap.server.nofsm.ap.disconnected");
				} else {
					msg = UpdateUtil.getCommonResponseMessage(r);
					log.info("parseSwitchSystemInfoResult",
							"receive switch system info response, result:" + r);
					if (null == msg || "".equals(msg)) {
						// The request was unsuccessful, but no error message was received.
						return MgrUtil
								.getUserMessage("info.aaa.test.failed.noMessage");
					}
					msg = "The request is failure, error message: " + msg;
				}
			}
		} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
			BeCapwapClientResultEvent result = (BeCapwapClientResultEvent) event;
			short resultType = result.getResultType();
			if (resultType == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SWITCH_SYSTEM_INFO) {
				BeSwitchSystemInfoResultEvent rlt = (BeSwitchSystemInfoResultEvent) result;
				if (rlt != null) {
					// success
					msg = "Retrieve system info successfully.";
					stpMode = getStpModeString(rlt.getSTPMode());
					stpState = getStpStateString(rlt.getSTPState());
					byte[] fanStateArray = rlt.getFanStateArray();
					if (fanStateArray != null) {
						fanStatus = "";
						for(int i = 0; i < fanStateArray.length; i++) {
							if (i > 0) {
								fanStatus += ", ";
							}
							fanStatus += "Fan" + i + " : " + getFanStatusString(fanStateArray[i]);
						}
					}
					
					// 33500 means 33.5 degree C
					float sysTemp = rlt.getSystemTemperature();
					sysTemparture = String.valueOf(sysTemp) + " degree C";
					
					// switch Utilization
					swUtilization = String.valueOf(rlt.getUtilization()) + "%";
					
					// power status
					powerStatus = getPowerStatusString(rlt.getPowerStatus());
				}
			}
		}
		return msg;
	}
	
	private String getStpStateString (byte stpState) {
		return MgrUtil.getEnumString("enum.monitor.hiveAp.switch.stp.state." + stpState);
	}
	
	private String getFanStatusString (byte fanStatus) {
		return MgrUtil.getEnumString("enum.monitor.hiveAp.switch.fan.status." + fanStatus);
	}
	
	private String getPortTypeString (byte portType) {
		return MgrUtil.getEnumString("enum.monitor.hiveAp.switch.port.type." + portType);
	}
	
	public String getStpModeString (byte stpMode) {
		return MgrUtil.getEnumString("enum.monitor.hiveAp.switch.stp.mode." + stpMode);
	}
	
	public String getPowerStatusString (byte powerStatus) {
		String ret = "";
		if (powerStatus >= 0) {
			// sw2024 have will not send up power status, so value of status should be -1, but -1 will cause error when get name from enum property file
			ret = MgrUtil.getEnumString("enum.monitor.hiveAp.switch.power.status." + powerStatus);
		}
		return ret;
	}
	
	/*
	 * port type string, used by jsp
	 */
	public String getPortTypePhoneDataString () {
		return MgrUtil.getEnumString("enum.portConfig.port.type.1");
	}
	
	public String getPortTypeAerohiveApString () {
		return MgrUtil.getEnumString("enum.portConfig.port.type.2");
	}
	
	public String getPortTypeMonitorString () {
		return MgrUtil.getEnumString("enum.portConfig.port.type.3");
	}
	
	public String getPortTypeAccessString () {
		return MgrUtil.getEnumString("enum.portConfig.port.type.4");
	}
	
	public String getPortType8021QString () {
		return MgrUtil.getEnumString("enum.portConfig.port.type.5");
	}
	
	public String getPortTypeWanString () {
		return MgrUtil.getEnumString("enum.portConfig.port.type.6");
	}
	/*
	 * port type string, end
	 */
	
	/**
	 * need two parameters: MAC of switch, port No., port type(ETH,SFP)
	 * 
	 * @return
	 * @throws Exception
	 */
	protected JSONObject fetchSwPortInfo() throws Exception {
		JSONObject jsonObject = new JSONObject();
		try {

			String mac = (macAddress == null ? macAddress : macAddress.toLowerCase());
			// check if device is connected or not
			Boolean isDeviceConnected = false;
			if (!StringUtils.isEmpty(mac)) {
				List<?> devices = QueryUtil.executeQuery("SELECT connected FROM "
						+ HiveAp.class.getSimpleName()
						+ " bo", null, new FilterParams("lower(bo.macAddress) = :s1", new Object[] { mac}));
				if (!devices.isEmpty()) {
					isDeviceConnected = (Boolean)devices.get(0);
				}
			}
			
			// get port name prefix 
			String portNamePrefix = "";
			if (PORT_ETH.equals(portMode)) {
				// ETH port
				portNamePrefix = SWITCH_PORT_GIGABIT_NAME_PREFIX;
			} else if (PORT_SFP.equals(portMode)) {
				// SFP port
				portNamePrefix = SWITCH_PORT_SFP_NAME_PREFIX;
				
				// port No. for SFP from port element is start from 0, not base on ETH port NO.
				if (swEth48) {
					portNo = portNo + SWITCH_PORT_NUM_48;
				} else {
					portNo = portNo + SWITCH_PORT_NUM_24;
				}
			} else if (PORT_USB.equals(portMode)) {
				// USB port
				byte retUsbStatus = AhSwitchPortInfo.SWITCH_LINK_STATE_USB_DISCONNECTED;
				if (isDeviceConnected != null && isDeviceConnected) {
					// if Switch is connected from HM, return get USB port info success
					try {
						byte usbStatus = prepareUsbStatusSw(getDataSource());
						jsonObject.put("success", "success");
						jsonObject.put("portName", "usb");
						jsonObject.put("portPoe", false);
						if (AhPortAvailability.INTERFACE_STATUS_UP == usbStatus) {
							// connected and up
							retUsbStatus = AhSwitchPortInfo.SWITCH_LINK_STATE_UP;
						} else if (AhPortAvailability.INTERFACE_STATUS_DOWN == usbStatus) {
							// connected but down
							retUsbStatus = AhSwitchPortInfo.SWITCH_LINK_STATE_DOWN;
						} else {
							retUsbStatus = AhSwitchPortInfo.SWITCH_LINK_STATE_USB_DISCONNECTED;
						}
					} catch (Exception e) {
						// if error happened, treate as USB not connected
						log.error("prepareUsbStatusSw", "error occored.", e);
						retUsbStatus = AhSwitchPortInfo.SWITCH_LINK_STATE_USB_DISCONNECTED;
					}
				} else {
					// if Switch is disconnected from HM, treate as USB not connected
					retUsbStatus = AhSwitchPortInfo.SWITCH_LINK_STATE_USB_DISCONNECTED;
				}
				jsonObject.put("linkState", retUsbStatus);
				return jsonObject;
			}
			String portName = portNamePrefix.toLowerCase() + "/" + portNo;
			jsonObject.put("portName", portName);
			
			// query port info
			FilterParams filterParams = new FilterParams(
					"lower(mac) = :s1 and portName like :s2", new Object[] {
							mac, "%" + portName});
			
			List<AhSwitchPortInfo> swPortInfoList = QueryUtil.executeQuery(AhSwitchPortInfo.class, null, filterParams);
			if (swPortInfoList != null && !swPortInfoList.isEmpty()) {
				AhSwitchPortInfo swPortInfo = swPortInfoList.get(0);
				jsonObject.put("success", "success");
				
				jsonObject.put("upTimePort", swPortInfo.getPortUpTimeString());
				jsonObject.put("linkState", swPortInfo.getState());
				jsonObject.put("linkStateString", swPortInfo.getLinkStateString());
				jsonObject.put("lineProtocol", ""); // TODO not support yet
				jsonObject.put("portPoe", checkPortSupplyPower(mac, portName));
				jsonObject.put("destMirrorPort", swPortInfo.getMirrorPort());
				
				/*
				 * below attributes need use port channel's info if current port belongs to port channel
				 */
				// portType show on page will use portType of port element(init with HM config), not use this real time value send up from device
				jsonObject.put("portType", swPortInfo.getPortType());
				jsonObject.put("portTypeString", getPortTypeString(swPortInfo.getPortType()));
				jsonObject.put("voiceVLANs", swPortInfo.getVoiceVLANs());
				jsonObject.put("dataVLANs", swPortInfo.getDataVLANs());
				jsonObject.put("pvid", swPortInfo.getPvid()); // portType is (1:Trunk): native vlan, is (0:Access): access vlan, is (2:WAN):wan pvid,will not used
				jsonObject.put("authenticationState", swPortInfo.getAuthenticationState());
				jsonObject.put("authenticationStateString", swPortInfo.getAuthStateString());
				jsonObject.put("stpMode", swPortInfo.getStpModeString()); 
				jsonObject.put("stpRole", swPortInfo.getSTPRole());
				jsonObject.put("stpRoleString", swPortInfo.getStpRoleString());
				jsonObject.put("stpState", swPortInfo.getStpStateDetailString());
				
				/*
				 *  check current port belongs to port channel or not 
				 *  (physicalPorts is a string includes all member ports joined by comma, e.g.: eth1/1,eth1/11)
				 */
				filterParams = new FilterParams(
						"lower(mac) = :s1 and (physicalPorts like :s2 or physicalPorts like :s3) ",
						new Object[] { mac,
								"%" + portName + ",%", // current port in the middle of physicalPorts string.
								"%" + portName }); // current port at the end of physicalPorts string.
				List<AhSwitchPortInfo> swPortChannelInfoList = QueryUtil.executeQuery(AhSwitchPortInfo.class, null, filterParams);
				if (swPortChannelInfoList != null && !swPortChannelInfoList.isEmpty()) {
					swPortInfo = swPortChannelInfoList.get(0);
					jsonObject.put("linkStatePortChannel", swPortInfo.getState());
					jsonObject.put("upTimePortChannel", swPortInfo.getPortUpTimeString());
					jsonObject.put("portChannel", swPortInfo.getPortName()); // like string "agg2"
					jsonObject.put("portChannelMembers", swPortInfo.getPhysicalPorts()); // like string "eth1/1,eth1/11"
					
					// over write member port's info with port channel's info
					jsonObject.put("portType", swPortInfo.getPortType());
					jsonObject.put("portTypeString", getPortTypeString(swPortInfo.getPortType()));
					jsonObject.put("voiceVLANs", swPortInfo.getVoiceVLANs());
					jsonObject.put("dataVLANs", swPortInfo.getDataVLANs());
					jsonObject.put("pvid", swPortInfo.getPvid()); // portType is (1:Trunk): native vlan, is (0:Access): access vlan, is (2:WAN):wan pvid,will not used
					jsonObject.put("authenticationState", swPortInfo.getAuthStateString());
					jsonObject.put("stpMode", swPortInfo.getStpModeString()); 
					jsonObject.put("stpRole", swPortInfo.getSTPRole());
					jsonObject.put("stpRoleString", swPortInfo.getStpRoleString());
					jsonObject.put("stpState", swPortInfo.getStpStateDetailString());
				}
				
				// get port error count infos
				AhSwitchPortStats portStats = getSwPortStats(mac, portName);
				if (portStats != null) {
					if (!StringUtils.isEmpty(portStats.getRxRatioOutOfLimits())) {
						jsonObject.put("receivingError", portStats.getRxRatioOutOfLimits());
					}
					if (!StringUtils.isEmpty(portStats.getTxRatioOutOfLimits())) {
						jsonObject.put("transmissionError", portStats.getTxRatioOutOfLimits());
					}
				}
			} else {
				jsonObject.put("msg", MgrUtil.getUserMessage("error.switch.port.info.not.fetched"));
			}
			
		} catch (Exception e) {
			log.error("fetchSwPortInfo", "error occored.", e);
			jsonObject.put("msg", "fetch switch port info error.");
		}
		return jsonObject;
	}
	
	/**
	 * prepare port status data for port style update on monitor page
	 */
	private String portStatusData;
	
	private boolean swEth48;
	
	private String contextPathStr;
	
	public String getPortStatusData() {
		return portStatusData;
	}

	public void setPortStatusData(String portStatusData) {
		this.portStatusData = portStatusData;
	}

	public boolean isSwEth48() {
		return swEth48;
	}

	public void setSwEth48(boolean swEth48) {
		this.swEth48 = swEth48;
	}

	public String getContextPathStr() {
		return contextPathStr;
	}

	public void setContextPathStr(String contextPathStr) {
		this.contextPathStr = contextPathStr;
	}

	/**
	 * get port id from port interface name, e.g.: Eth1/11, portId = 11.
	 */
	private String getPortNoFromPortName(String portName) {
		String portId = null;
		if (!StringUtils.isEmpty(portName) && portName.indexOf("/") > 0) {
			portId = portName.substring(portName.indexOf("/") + 1);
		}
		return portId;
	}
	
	private String joinStringWithSeparator(List<String> strList, String separator) {
		String result = "";
		if (strList != null && !strList.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < strList.size(); i++) {
				if (i > 0){
					builder.append(",");
				}
				builder.append(strList.get(i));
			}
			result = builder.toString();
		}
		return result;
	}
	
	private boolean checkPortSupplyPower(String mac, String interfName) {
		mac = (mac == null ? mac : mac.toLowerCase());
		FilterParams pseFilterParams = new FilterParams(
				"lower(mac) = :s1 and power > :s2 and interfName = :s3", new Object[] {mac, 0.0f, interfName});
		List<?> pseDatas = QueryUtil.executeQuery("select bo.id from " + AhPSEStatus.class.getSimpleName() + " bo", null, pseFilterParams);
		if (pseDatas != null && !pseDatas.isEmpty()) {
			return true;
		}
		return false;
	}
	
	private List<?> queryPseData(FilterParams pseFilterParams) {
		return QueryUtil.executeQuery("select bo.interfName, bo.power from " + AhPSEStatus.class.getSimpleName() + " bo", null, pseFilterParams);
	}
	
	private List<AhSwitchPortInfo> querySwPortInfoData(FilterParams filterParams) {
		return QueryUtil.executeQuery(AhSwitchPortInfo.class, null, filterParams);
	}
	
	/*
	 * prepare pse data(key,value:portName,power), when do refresh, 
	 * HM will first remove all pse data and insert new data from device, so here may be need retry to get pse data from DB.
	 */
	private void preparePseData(String mac, Map<String, String> portPseMap) {
		
		// check current port is supply power or not 
		FilterParams pseFilterParams = new FilterParams(
				"lower(mac) = :s1 and power > :s2", new Object[] {mac, 0.0f});
		List<?> pseDatas = null;
		
		// try 5 times max
		short tryTimes = 1;
		while (tryTimes <= 5 && (pseDatas == null || pseDatas.isEmpty())) {
			log.info("preparePseData, sleep 1 second, try times:===============" + tryTimes);
			pseDatas = queryPseData(pseFilterParams);
			
			// if get data, finish loop
			if (pseDatas != null && !pseDatas.isEmpty()) {
				break;
			}
			
			if (tryTimes > 1) {
				// wait for 1 second
				MgrUtil.sleepTime(1);
			}
			tryTimes ++;
		}
		for (Object object : pseDatas) {
			Object[] objects = (Object[]) object;
			log.info("preparePseData, power > 0===============" + (String)objects[0] +"," +String.valueOf(objects[1]));
			portPseMap.put((String)objects[0], String.valueOf(objects[1]));
		}
	}
	
	private List<AhSwitchPortInfo> prepareSwPortInfoData(FilterParams filterParams) {
		
		List<AhSwitchPortInfo> swPortInfoList = null;
		
		short tryTimes = 1;
		while (tryTimes <= 5 && (swPortInfoList == null || swPortInfoList.isEmpty())) {
			log.info("prepareSwPortInfoData, sleep 1 second, try times:===============" + tryTimes);
			swPortInfoList = querySwPortInfoData(filterParams);
			
			// if get data, finish loop
			if (swPortInfoList != null && !swPortInfoList.isEmpty()) {
				break;
			}
			
			if (tryTimes > 1) {
				
				// wait for 1 second
				MgrUtil.sleepTime(1);
			}
			tryTimes ++;
		}
		
		return swPortInfoList;
	}


	/**
	 * result like below:
	 * {
	 * 		ports: {
	 * 			ETH: [1,2,3],
	 * 			SFP: [1,2,3,4],
	 * 			USB: [0]
	 * 		},
	 * 		className: 'claxxName'
	 * }
	 */
	private void prepareDataForUpdatePortStatus(HiveAp hiveAp) {
		List<String> upEthPorts = new ArrayList<String>();
		List<String> upSfpPorts = new ArrayList<String>();
		List<String> upUsbPorts = new ArrayList<String>();

		List<String> downEthPorts = new ArrayList<String>();
		List<String> downSfpPorts = new ArrayList<String>();
		List<String> downUsbPorts = new ArrayList<String>();

		List<String> adminDisableEthPorts = new ArrayList<String>(); // must be down, show black color
		List<String> adminDisableSfpPorts = new ArrayList<String>(); // must be down, show black color
		List<String> notConnectedUsbPorts = new ArrayList<String>(); // usb not conntected, show black color
		
		List<String> errorEthPorts = new ArrayList<String>();
		List<String> errorSfpPorts = new ArrayList<String>();
		
		List<String> poePorts = new ArrayList<String>(); // POE on means port really supply power for PD, fix bug 23221
		
		String mac = hiveAp.getMacAddress();
		mac = (mac == null ? mac : mac.toLowerCase());
		FilterParams filterParams = new FilterParams(
				"lower(mac) = :s1", new Object[] {mac});
		
		Map<String, String> portPseMap = new HashMap<String, String>();
		preparePseData(mac, portPseMap);
		
		List<AhSwitchPortInfo> swPortInfoList = prepareSwPortInfoData(filterParams);
		AhSwitchPortStats portStats;
		boolean isPortError = false;;
		if (swPortInfoList != null && !swPortInfoList.isEmpty()) {
			for (AhSwitchPortInfo ahSwitchPortInfo : swPortInfoList) {
				String portName = ahSwitchPortInfo.getPortName(); // port name(SR24: Eth1/1..28, SR48: Eth1/1..52)
				String portNo = getPortNoFromPortName(portName);
				if (portNo == null) {
					continue;
				}
				
				int portIntNo = 0;
				try {
					portIntNo = Integer.parseInt(portNo);
				} catch (Exception e) {
					continue;
				}
				
				boolean isEthPort = false;
				if ((portIntNo >=1 && portIntNo <= SWITCH_PORT_NUM_24)
						|| (swEth48 && portIntNo >=1 && portIntNo <= SWITCH_PORT_NUM_48)) {
					isEthPort = true;
				} else {
					// SFP port need do portNo.-24/48 for display 
					if (swEth48) {
						portIntNo -= SWITCH_PORT_NUM_48;
					} else {
						portIntNo -= SWITCH_PORT_NUM_24;
					}
				}
				
				portStats = getSwPortStats(mac, portName);
				if (portStats != null && portStats.isPortError()) {
					isPortError = true;
				} else {
					isPortError = false;
				}
				
				// ETH
				if (isEthPort) {
					if (isPortError) {
						// error port no poe
						errorEthPorts.add(String.valueOf(portIntNo));
					} else {
						if (AhSwitchPortInfo.SWITCH_LINK_STATE_ADMIN_DISABLE == ahSwitchPortInfo.getState()) {
							// admin disable, must be down
							adminDisableEthPorts.add(String.valueOf(portIntNo));
						} else if (AhSwitchPortInfo.SWITCH_LINK_STATE_DOWN == ahSwitchPortInfo.getState()
								|| AhSwitchPortInfo.SWITCH_LINK_STATE_DOWN_STPD == ahSwitchPortInfo.getState()) {
							// admin enable, but down ETH port
							downEthPorts.add(String.valueOf(portIntNo));
						} else {
							// up ETH port
							upEthPorts.add(String.valueOf(portIntNo));
						}
					}
					
					// EHT(SR24 1-24, SR48 1-48)
					if (!StringUtils.isEmpty(portPseMap.get(portName))) {
						poePorts.add(String.valueOf(portIntNo));
					}
				} else {
					// SFP
					if (isPortError) {
						// error port
						errorSfpPorts.add(String.valueOf(portIntNo));
					} else {
						if (AhSwitchPortInfo.SWITCH_LINK_STATE_ADMIN_DISABLE == ahSwitchPortInfo.getState()) {
							// admin disable, must be down
							adminDisableSfpPorts.add(String.valueOf(portIntNo));
						} else if (AhSwitchPortInfo.SWITCH_LINK_STATE_DOWN == ahSwitchPortInfo.getState()
								|| AhSwitchPortInfo.SWITCH_LINK_STATE_DOWN_STPD == ahSwitchPortInfo.getState()) {
							// admin enable, but down SFP port
							downSfpPorts.add(String.valueOf(portIntNo));
						} else {
							// up SFP port
							upSfpPorts.add(String.valueOf(portIntNo));
						}
					}
				}
			}
			
			// USB port
			try {
				byte usbStatus = prepareUsbStatusSw(hiveAp);
				if (AhPortAvailability.INTERFACE_STATUS_UP == usbStatus) {
					// connected and up
					// only got one USB port, portNo is 0
					upUsbPorts.add("0");
				} else if (AhPortAvailability.INTERFACE_STATUS_DOWN == usbStatus) {
					// connected but down
					downUsbPorts.add("0");
				} else {
					// not connected (INTERFACE_STATUS_NOT_CONNECTED)
					notConnectedUsbPorts.add("0");
				}
			} catch (Exception e) {
				// if error happened, do nothing(need not to refresh USB port color)
				log.error("prepareUsbStatusSw", "error occored.", e);
				// error happened, treat as not connected
				notConnectedUsbPorts.add("0");
			}
			
			/*
			 * generate port status JSON string.
			 */
			StringBuilder builder = new StringBuilder("[");
			
			// up ports
			builder.append("{");
			builder.append("ports: {");
	        if(!upEthPorts.isEmpty()) {
	        	builder.append(PORT_ETH + ": [" + joinStringWithSeparator(upEthPorts, SPEARATOR_COMMA) +"]");
	        }
	        if(!upSfpPorts.isEmpty()) {
	        	if (!upEthPorts.isEmpty()) {
	        		builder.append(",");
	        	}
	        	builder.append(PORT_SFP + ": [" + joinStringWithSeparator(upSfpPorts, SPEARATOR_COMMA) +"]");
	        }
			if (!upUsbPorts.isEmpty()) {
	        	if (!upEthPorts.isEmpty()
	        			|| !upSfpPorts.isEmpty()) {
	        		builder.append(",");
	        	}
	        	builder.append(PORT_USB + ": [" + joinStringWithSeparator(upUsbPorts, SPEARATOR_COMMA) +"]");
			}
	        builder.append("} ,");
	        builder.append("className:\"up\""); // must match CSS name on portMonitorPage.css
	        builder.append("}");
	        
	        // down ports
			builder.append(", {");
			builder.append("ports: {");
	        if(!downEthPorts.isEmpty()) {
	        	builder.append(PORT_ETH + ": [" + joinStringWithSeparator(downEthPorts, SPEARATOR_COMMA) +"]");
	        }
	        if(!downSfpPorts.isEmpty()) {
	        	if (!downEthPorts.isEmpty()) {
	        		builder.append(",");
	        	}
	        	builder.append(PORT_SFP + ": [" + joinStringWithSeparator(downSfpPorts, SPEARATOR_COMMA) +"]");
	        }
			if (!downUsbPorts.isEmpty()) {
	        	if (!downEthPorts.isEmpty()
	        			|| !downSfpPorts.isEmpty()) {
	        		builder.append(",");
	        	}
	        	builder.append(PORT_USB + ": [" + joinStringWithSeparator(downUsbPorts, SPEARATOR_COMMA) +"]");
			}
	        builder.append("} ,");
	        builder.append("className:\"down\""); // must match CSS name on portMonitorPage.css
	        builder.append("}");
	        
	        // not connected USB/admin disable eth & sfp
			builder.append(", {");
			builder.append("ports: {");
	        if(!adminDisableEthPorts.isEmpty()) {
	        	builder.append(PORT_ETH + ": [" + joinStringWithSeparator(adminDisableEthPorts, SPEARATOR_COMMA) +"]");
	        }
			if (!adminDisableSfpPorts.isEmpty()) {
	        	if (!adminDisableEthPorts.isEmpty()) {
	        		builder.append(",");
	        	}
	        	builder.append(PORT_SFP + ": [" + joinStringWithSeparator(adminDisableSfpPorts, SPEARATOR_COMMA) +"]");
			}
 			if (!notConnectedUsbPorts.isEmpty()) {
 				if (!adminDisableEthPorts.isEmpty()
	        			|| !adminDisableSfpPorts.isEmpty()) {
	        		builder.append(",");
	        	}
 	        	builder.append(PORT_USB + ": [" + joinStringWithSeparator(notConnectedUsbPorts, SPEARATOR_COMMA) +"]");
 			}
 	        builder.append("} ,");
 	        builder.append("className:\"disabled\""); // must match CSS name on portMonitorPage.css
 	        builder.append("}");
	        
	        // error port
			builder.append(", {");
			builder.append("ports: {");
	        if(!errorEthPorts.isEmpty()) {
	        	builder.append(PORT_ETH + ": [" + joinStringWithSeparator(errorEthPorts, SPEARATOR_COMMA) +"]");
	        }
	        if(!errorSfpPorts.isEmpty()) {
	        	if (!errorEthPorts.isEmpty()) {
	        		builder.append(",");
	        	}
	        	builder.append(PORT_SFP + ": [" + joinStringWithSeparator(errorSfpPorts, SPEARATOR_COMMA) +"]");
	        }
	        builder.append("} ,");
	        builder.append("className:\"error\""); // must match CSS name on portMonitorPage.css
	        builder.append("}");
	        
	        // up & POE port
	        if (!poePorts.isEmpty()) {
	        	builder.append(", {");
	    		builder.append("ports: {");
	    		builder.append(PORT_ETH + ": [" + joinStringWithSeparator(poePorts, SPEARATOR_COMMA) +"]");
	            builder.append("} ,");
	            builder.append("className:\"poe\""); // must match CSS name on portMonitorPage.css
	            builder.append("}");
	        }
	        
	        builder.append("]");
			this.portStatusData = builder.toString();
		}
		log.info("prepareDataForUpdatePortStatus()", "portStatusData = " + portStatusData);
		
		// set context path for resource reference like icon, css
		contextPathStr = request.getContextPath();
	}

	public String getWifi0RateStr() {
		return wifi0RateStr;
	}

	public void setWifi0RateStr(String wifi0RateStr) {
		this.wifi0RateStr = wifi0RateStr;
	}

	public String getWifi1RateStr() {
		return wifi1RateStr;
	}

	public void setWifi1RateStr(String wifi1RateStr) {
		this.wifi1RateStr = wifi1RateStr;
	}

	public AhRouterLTEVZInfo getAhRouter() {
		return null==ahRouter?new AhRouterLTEVZInfo():ahRouter;
	}

	public List<DeviceInterface> getWanDeviceInterfaceList() {
		return wanDeviceInterfaceList;
	}

	public String getDwThroughput() {
		return convertValue(this.dwThroughput);
	}

	public String getUpThroughput() {
		return convertValue(this.upThroughput);
	}
	
	private static String convertValue(double throughtOut) {
		String vThroughtOut = "";
		throughtOut = new BigDecimal(throughtOut*8/3600).setScale(2,
				BigDecimal.ROUND_HALF_UP).doubleValue();
		vThroughtOut = throughtOut + " bps";
		if (throughtOut > 1024) {
			throughtOut = new BigDecimal(throughtOut/1024).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			vThroughtOut = throughtOut + " kbps";
		}
		if (throughtOut > 1024) {
			throughtOut = new BigDecimal(throughtOut/1024).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			vThroughtOut = throughtOut + " mbps";
		}
		return vThroughtOut;
	}

	public String getWifi0_type_tip() {
		return wifi0_type_tip;
	}

	public void setWifi0_type_tip(String wifi0_type_tip) {
		this.wifi0_type_tip = wifi0_type_tip;
	}

	public String getWifi1_type_tip() {
		return wifi1_type_tip;
	}

	public void setWifi1_type_tip(String wifi1_type_tip) {
		this.wifi1_type_tip = wifi1_type_tip;
	}
	
	/**
	 * for switch
	 */
	
	private String[] arrayPseBounce;

	public String[] getArrayPseBounce() {
		return arrayPseBounce;
	}

	public void setArrayPseBounce(String[] arrayPseBounce) {
		this.arrayPseBounce = arrayPseBounce;
	}
	
	public String getSupportPoeBounce(){
		if (NmsUtil.compareSoftwareVersion("6.1.3.0", this.getDataSource()
				.getSoftVer()) > 0) {
			return "none";
		}
		return "";
	}
	
	private String generateBlankSpaces(int count){
		String result = "";
		for(int i=0;i<count;i++){
			result += "&nbsp;";
		}
		return result;
	}
	
	private JSONObject recyclePower() throws Exception{
		JSONObject jsonObject = new JSONObject();
		List<String> succPorts = new ArrayList<>();
		List<String> failPorts = new ArrayList<>();
		int len = arrayPseBounce.length;
		String[] cli = new String[len*2];
		for(int i=0;i<len;i++){
			if(i==len-1){
				cli[i] = AhCliFactory.getPseShutdownCLi(arrayPseBounce[i].toLowerCase(), true, false);
				cli[i+len] = AhCliFactory.getPseShutdownCLi(arrayPseBounce[i].toLowerCase(), false, true);
			} else {
				cli[i] = AhCliFactory.getPseShutdownCLi(arrayPseBounce[i].toLowerCase(), false, false);
				cli[i+len] = AhCliFactory.getPseShutdownCLi(arrayPseBounce[i].toLowerCase(), false, true);
			}
		}
		HiveAp hiveAp = getDataSource();
		int timeout = BeTopoModuleParameters.DEFAULT_CLI_TIMEOUT_MAX / 1000;
		BeCommunicationEvent result = BeTopoModuleUtil
				.sendSyncCliRequest(hiveAp, cli,
						BeCliEvent.CLITYPE_NORMAL,
						timeout);
		String msg = BeTopoModuleUtil.parseCliRequestResult(result);
		boolean isSuccess = BeTopoModuleUtil.isCliExeSuccess(result);
		if(isSuccess){
			for(String port : arrayPseBounce){
				succPorts.add(port);
			}
		} else {
			boolean cliError = false;
			for(String port : arrayPseBounce){
				if(msg == null || msg.isEmpty() || msg.contains(port)){
					cliError = true;
					break;
				}
			}
			if(cliError){
				for(String port : arrayPseBounce){
					String[] _cli = new String[2];
					_cli[0] = AhCliFactory.getPseShutdownCLi(port.toLowerCase(), true, false);
					_cli[1] = AhCliFactory.getPseShutdownCLi(port.toLowerCase(), false, true);
					BeCommunicationEvent _result = BeTopoModuleUtil
							.sendSyncCliRequest(hiveAp, _cli,
									BeCliEvent.CLITYPE_NORMAL,
									timeout);
					boolean _isSuccess = BeTopoModuleUtil.isCliExeSuccess(_result);
					if(_isSuccess){
						succPorts.add(port);
					} else {
						failPorts.add(port);
					}
				}
			} 
		}
		
		jsonObject = new JSONObject();
		if(succPorts.isEmpty() && failPorts.isEmpty()){
			jsonObject.put("v", msg);
		} else {
			String succMsg = "";
			if(succPorts.isEmpty()){
				succMsg = succMsg+"None";
			} else {
				int i=1;
				for(String port : succPorts){
					if(i%7 == 0){
						if(i==succPorts.size()){
							succMsg = succMsg+port;
						} else {
							succMsg = succMsg+port+","+"<br>"+generateBlankSpaces(40);
						}
						
					} else {
						if(i==succPorts.size()){
							succMsg = succMsg+port;
						} else {
							succMsg = succMsg+port+",";
						}
					}
					i++;
				}
			}
			
			String failMsg = "";
			if(failPorts.isEmpty()){
				failMsg = failMsg+"None";
			} else {
				int i=1;
				for(String port : failPorts){
					if(i%7 == 0){
						if(i==failPorts.size()){
							failMsg = failMsg+port;
						} else {
							failMsg = failMsg+port+","+"<br>"+generateBlankSpaces(36);
						}
						
					} else {
						if(i==failPorts.size()){
							failMsg = failMsg+port;
						} else {
							failMsg = failMsg+port+",";
						}
					}
					i++;
				}
			}
			msg = MgrUtil.getUserMessage("glasgow_07.info.port.pse.bouce.successful",succMsg)+"<br><br>"+
					MgrUtil.getUserMessage("glasgow_07.info.port.pse.bouce.unsuccessful",failMsg);
			jsonObject.put("v", msg);
		}
		
		return jsonObject;
	}
	
	/**
	 * push CLI down to device to clear interface counters
	 * 
	 * @return
	 * @throws Exception
	 */
	private JSONObject clearErrorCounters() throws Exception {
		if(jsonObject == null) {
			jsonObject = new JSONObject();
		}
		
		String[] cli = new String[]{AhCliFactory.clearInterfaceCounters()};
		HiveAp hiveAp = getDataSource();
		int timeout = BeTopoModuleParameters.DEFAULT_CLI_TIMEOUT_MAX / 1000;
		BeCommunicationEvent result = BeTopoModuleUtil
				.sendSyncCliRequest(hiveAp, cli,
						BeCliEvent.CLITYPE_NORMAL,
						timeout);
		boolean isSuccess = BeTopoModuleUtil.isCliExeSuccess(result);
		if(isSuccess){
			jsonObject.put("success", "success");
			jsonObject.put("msg", MgrUtil.getUserMessage("info.clear.error.counters.success"));
		} else {
			jsonObject.put("msg", MgrUtil.getUserMessage("info.clear.error.counters.failed"));
		}
		
		return jsonObject;
	}
}
