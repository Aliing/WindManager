package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.SsidProfileInt;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.network.AirScreenRule;
import com.ah.bo.network.DosParams;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SsidProfile;
import com.ah.bo.wlan.TX11aOr11gRateSetting;
import com.ah.bo.wlan.Tx11acRateSettings;
import com.ah.util.MgrUtil;
import com.ah.xml.be.config.SsidMulticastConversionToUnicastValue;

/**
 * @author jzhang
 * @version V1.0.0.0
 */

public class SsidProfileImpl implements SsidProfileInt {

	private final boolean DEFAULT_OPERATION_TRUE = true;
	private final boolean DEFAULT_OPERATION_FALSE = false;
	private final HiveAp hiveAp;
	private Cwp cwp;
	private final ConfigTemplateSsid templateSsid;
	private final DosPrevention ssidDos;
	private final DosPrevention stationDos;
	private final DosPrevention ipDos;
	private final SsidProfile ssidProfile;
	private final ServiceFilter serviceFilter;
	private final Set<Scheduler> schedules;
	private final Set<MacFilter> macFilters;
	private RadiusAssignment radiusAssignment;
	private Iterator<Scheduler> iteSchedules;
	
	private List<LocalUserGroup> pskUserGroupList;
	private List<AirScreenRule> airScreenRules = null;
	
	public SsidProfileImpl(SsidProfile ssidProfile, HiveAp hiveAp) {
		this.hiveAp = hiveAp;
		this.ssidProfile = ssidProfile;
		schedules = ssidProfile.getSchedulers();
		macFilters = ssidProfile.getMacFilters();
		ssidDos = ssidProfile.getSsidDos();
		stationDos = ssidProfile.getStationDos();
		ipDos = ssidProfile.getIpDos();
		templateSsid = hiveAp.getConfigTemplate().getSsidInterfaces().get(
				ssidProfile.getId());
		serviceFilter = ssidProfile.getServiceFilter();

		//load airscreen rule
		if(ssidProfile.getAsRuleGroup() != null){
			if(ssidProfile.getAsRuleGroup().getRules() != null){
				airScreenRules = new ArrayList<AirScreenRule>(ssidProfile.getAsRuleGroup().getRules());
			}
		}
	}
	
	public SsidProfile getSsidProfile(){
		return this.ssidProfile;
	}
	
	public String getSsidGuiKey(){
		return MgrUtil.getUserMessage("config.upload.debug.ssidProfiles");
	}
	
	public String getIpDosGuiKey(){
		return MgrUtil.getUserMessage("config.upload.debug.ipDos");
	}
	
	public String getIpDosName(){
		return ipDos.getDosPreventionName();
	}
	
	public String getWlanGuiKey(){
		return MgrUtil.getUserMessage("config.upload.debug.networkPolicy");
	}
	
	public String getWlanName(){
		return hiveAp.getConfigTemplate().getConfigName();
	}
	
	public String getServiceFilterGuikey() {
		return MgrUtil.getUserMessage("config.upload.debug.managementFilters");
	}
	
	public String getServiceFilterName() {
		return serviceFilter.getFilterName();
	}
	
	public String getRadiusAssGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.radiusServerAssignments");
	}
	
	public String getRadiusAssName(){
		return radiusAssignment.getRadiusName();
	}

	public String getApVersion() {
		return hiveAp.getSoftVer();
	}

	public boolean isConfigureSsid() {
		return ssidProfile != null;
	}

	public boolean isYesDefault() {
		return DEFAULT_OPERATION_TRUE;
	}

	public boolean isNoDefault() {
		return DEFAULT_OPERATION_FALSE;
	}

	public String getSsidName() {
		return ssidProfile.getSsid();
	}

	public String getSsidUpdateTime() {
//		List<Object> ssidTimeList = new ArrayList<Object>();
//		ssidTimeList.add(hiveAp);
//		ssidTimeList.add(ssidProfile);
//		ssidTimeList.add(serviceFilter);
//		ssidTimeList.add(cwp);
//		ssidTimeList.addAll(schedules);
//		ssidTimeList.add(ssidDos);
//		ssidTimeList.add(stationDos);
//		ssidTimeList.add(ipDos);
//		ssidTimeList.add(defUserProfile);
//		ssidTimeList.add(regUserProfile);
//		ssidTimeList.add(authUserProfile);
//		ssidTimeList.add(radiusAssignment);
//		if (backup1Radius != null) {
//			ssidTimeList.add(backup1Radius.getIpAddress());
//		}
//		if (backup2Radius != null) {
//			ssidTimeList.add(backup2Radius.getIpAddress());
//		}
//		if (backup3Radius != null) {
//			ssidTimeList.add(backup3Radius.getIpAddress());
//		}
//		ssidTimeList.add(templateSsid.getClassfierAndMarker());
//		// ssidTimeList.add(templateSsid.getQosMarking());
//		return CLICommonFunc.getLastUpdateTime(ssidTimeList);
		return CLICommonFunc.getLastUpdateTime(null);
	}

	public int getDtimPeriod(){
		return ssidProfile.getDtimSetting();
	}

	public int getFragThreshold() {
		return ssidProfile.getFragThreshold();
	}

	public boolean isHideSsidEnable(){
		return ssidProfile.isHide();
	}

	public boolean isIgnoreBroadcastProbeEnable() {
		return ssidProfile.isBroadcase();
	}

	public int getRtsThreshold() {
		return ssidProfile.getRtsThreshold();
	}

	public int getSsidScheduleSize() {
		if (schedules != null) {
			return schedules.size();
		} else {
			return 0;
		}
	}

	public String getSsidScheduleNextName() {
		if (iteSchedules == null) {
			iteSchedules = schedules.iterator();
		}
		return iteSchedules.next().getSchedulerName();
	}

	// public boolean isDnsServerEnable() {
	// // later
	// return true;
	// }

	public boolean isSsidManageEnable(String manageType) {
		boolean manageStatus = false;
		if (serviceFilter == null) {
			return manageStatus;
		}
		if (SsidProfileInt.MANAGE_SNMP.equals(manageType)) {
			manageStatus = serviceFilter.getEnableSNMP();
		} else if (SsidProfileInt.MANAGE_PING.equals(manageType)) {
			manageStatus = serviceFilter.getEnablePing();
		} else if (SsidProfileInt.MANAGE_SSH.equals(manageType)) {
			manageStatus = serviceFilter.getEnableSSH();
		} else if (SsidProfileInt.MANAGE_TELNET.equals(manageType)) {
			manageStatus = serviceFilter.getEnableTelnet();
		}
		return manageStatus;
	}

//	public boolean isConfigureMacFilter() {
//		return macFilters != null && macFilters.size() > 0;
//	}

	public String getMacFilter() {
		return ssidProfile.getSsid();
	}

	private String turnSsidScreenType(String screenType) {
		String key = "";
		if (SsidProfileInt.SCREENING_ADDRESS_SWEEP.equals(screenType)) {
			key = DosParams.ScreeningType.ADDRESS_SWEEP.name();
		} else if (SsidProfileInt.SCREENING_ICMP_FLOOD.equals(screenType)) {
			key = DosParams.ScreeningType.ICMP_FLOOD.name();
		} else if (SsidProfileInt.SCREENING_IP_SPOOF.equals(screenType)) {
			key = DosParams.ScreeningType.IP_SPOOF.name();
		} else if (SsidProfileInt.SCREENING_PORT_SCAN.equals(screenType)) {
			key = DosParams.ScreeningType.PORT_SCAN.name();
		} else if (SsidProfileInt.SCREENING_RADIUS_ATTACK.equals(screenType)) {
			key = DosParams.ScreeningType.RADIUS_ATTACK.name();
		} else if (SsidProfileInt.SCREENING_SYN_FLOOD.equals(screenType)) {
			key = DosParams.ScreeningType.SYN_FLOOD.name();
		} else if (SsidProfileInt.SCREENING_UDP_FLOOD.equals(screenType)) {
			key = DosParams.ScreeningType.UDP_FLOOD.name();
		} else if (SsidProfileInt.SCREENING_ARP_FLOOD.equals(screenType)) {
			key = DosParams.ScreeningType.ARP_FLOOD.name();
		}
		return key;
	}

	public boolean isConfigureScreenElement(String screenType) {
		String key = turnSsidScreenType(screenType);
		return ipDos.getDosParamsMap().get(key).isEnabled();
	}

	public int getScreenThresholdValue(String screenType) {
		String key = turnSsidScreenType(screenType);
		return ipDos.getDosParamsMap().get(key).getAlarmThreshold();
	}

	private String turnScreenActonType(String actionType) {
		String actionValue = "";
		if (SsidProfileInt.SCREENING_ACTION_ALARM.equals(actionType)) {
			actionValue = DosParams.DosAction.ALARM.name();
		} else if (SsidProfileInt.SCREENING_ACTION_BAN.equals(actionType)) {
			actionValue = DosParams.DosAction.BAN.name();
		} else if (SsidProfileInt.SCREENING_ACTION_BAN_FOREVER
				.equals(actionType)) {
			actionValue = DosParams.DosAction.BAN_FOREVER.name();
		} else if (SsidProfileInt.SCREENING_ACTION_DISCONNECT
				.equals(actionType)) {
			actionValue = DosParams.DosAction.DISCONNECT.name();
		} else if (SsidProfileInt.SCREENING_ACTION_DROP.equals(actionType)) {
			actionValue = DosParams.DosAction.DROP.name();
		}
		return actionValue;
	}

	public boolean isConfigureActionWithScreen(String screenType,
			String actionType) {
		String key = turnSsidScreenType(screenType);

		String actionValue = turnScreenActonType(actionType);

		return actionValue.equals(ipDos.getDosParamsMap().get(key)
				.getDosAction().name());
	}

	public int getScreenActionValue(String screenType, String actionType) {
		String key = turnSsidScreenType(screenType);
		return ipDos.getDosParamsMap().get(key).getDosActionTime();
	}
	
	public boolean isEnableWithStationType(FrameType stationType){
		String key = getStationLevelKey(stationType);
		return stationDos.getDosParamsMap().get(key).isEnabled();
	}

	public int getAlarmValueWithStationType(FrameType stationType) {
		String key = getStationLevelKey(stationType);
		return stationDos.getDosParamsMap().get(key).getAlarmInterval();
	}

	private String getStationLevelKey(FrameType stationType) {
		String key = "";
		if (SsidProfileInt.FrameType.assoc_req == stationType) {
			key = DosParams.FrameType.ASSOC_REQ.name();
		} else if (SsidProfileInt.FrameType.assoc_resp == stationType) {
			key = DosParams.FrameType.ASSOC_RESP.name();
		} else if (SsidProfileInt.FrameType.auth == stationType) {
			key = DosParams.FrameType.AUTH.name();
		} else if (SsidProfileInt.FrameType.deauth == stationType) {
			key = DosParams.FrameType.DEAUTH.name();
		} else if (SsidProfileInt.FrameType.disassoc == stationType) {
			key = DosParams.FrameType.DISASSOC.name();
		} else if (SsidProfileInt.FrameType.eapol == stationType) {
			key = DosParams.FrameType.EAPOL.name();
		} else if (SsidProfileInt.FrameType.probe_req == stationType) {
			key = DosParams.FrameType.PROBE_REQ.name();
		} else if (SsidProfileInt.FrameType.probe_resp == stationType) {
			key = DosParams.FrameType.PROBE_RESP.name();
		}
		return key;
	}
	
	public String getBanValueWithStationType(FrameType stationType) {
		String key = getStationLevelKey(stationType);
		int actionTime = stationDos.getDosParamsMap().get(key).getDosActionTime();
		if(actionTime < 0){
			return "forever";
		}else{
			return String.valueOf(actionTime);
		}
	}

	public int getThresholdValueWithStationType(FrameType stationType) {
		String key = getStationLevelKey(stationType);
		return stationDos.getDosParamsMap().get(key).getAlarmThreshold();
	}

	public boolean isEnableWithSsidType(FrameType stationType) {
		String key = getStationLevelKey(stationType);
		return ssidDos.getDosParamsMap().get(key).isEnabled();
	}

	public int getAlarmValueWithSsidType(FrameType stationType) {
		String key = getStationLevelKey(stationType);
		return ssidDos.getDosParamsMap().get(key).getAlarmInterval();
	}

	public int getThresholdValueWithSsidType(FrameType stationType) {
		String key = getStationLevelKey(stationType);
		return ssidDos.getDosParamsMap().get(key).getAlarmThreshold();
	}

	public boolean isConfigureQosClass() {
		boolean manualApply = hiveAp.getConfigTemplate().getEnabledMapOverride() && templateSsid != null && (
				templateSsid.getNetworkServicesEnabled() || templateSsid.getMacOuisEnabled() || 
				templateSsid.getSsidEnabled() || templateSsid.getCheckE() ||
				templateSsid.getCheckP() || templateSsid.getCheckD() || templateSsid.getSsidOnlyEnabled()
				);
		
		QosClassification classMap = hiveAp.getConfigTemplate().getClassifierMap();
		boolean autoApply =  classMap != null && !hiveAp.getConfigTemplate().getEnabledMapOverride() && (
				classMap.getMacOuisEnabled() || classMap.getNetworkServicesEnabled() || classMap.getSsidEnabled() ||
				(classMap.getGeneralEnabled() && classMap.getPrtclE() != null && !"".equals(classMap.getPrtclE())) ||
				(classMap.getGeneralEnabled() && classMap.getPrtclD() != null && !"".equals(classMap.getPrtclD()))
		);
		
		return manualApply || autoApply;
		
//		return hiveAp.getConfigTemplate().getClassifierMap() != null;
	}

	public String getQosClassifierName() {
//		return templateSsid.getInterfaceName();
		return ssidProfile.getSsidName();
	}

	public boolean isConfigureQosMarker() {
		boolean manualApply = hiveAp.getConfigTemplate().getEnabledMapOverride() && templateSsid != null && (
				templateSsid.getCheckET() || templateSsid.getCheckPT() || templateSsid.getCheckDT()
				);
		QosMarking markMap = hiveAp.getConfigTemplate().getMarkerMap();
		boolean autoApply = markMap != null && !hiveAp.getConfigTemplate().getEnabledMapOverride() && (
				(markMap.getPrtclD() != null && !"".equals(markMap.getPrtclD())) ||
				(markMap.getPrtclE() != null && !"".equals(markMap.getPrtclE()))
		);
		
		return manualApply || autoApply;
		
//		return hiveAp.getConfigTemplate().getMarkerMap() != null;
	}

	public boolean isEnableUapsd() {
		return ssidProfile.getEnabledUnscheduled();
	}

	public String getQosMarkerName() {
//		return templateSsid.getInterfaceName();
		return ssidProfile.getSsidName();
	}

	// public boolean isConfigureProtocolSuite(){
	// return cwp == null;
	// }

	public boolean isConfigureScreen() {
		return ipDos != null;
	}

	public boolean isConfigureWlan() {
		return (ssidDos != null) || (stationDos != null);
	}

	public boolean isConfigureStationLevel() {
		return stationDos != null;
	}

	public boolean isConfigureSsidLevel() {
		return ssidDos != null;
	}

	public boolean isEnableSsidWmm() {
		return ssidProfile.getEnabledwmm();
	}
	
	public boolean isEnableSsidWnm(){
		return ssidProfile.isEnabled80211v();
	}

	public boolean isEnableScreenTcpSynCheck() throws Exception {
		return ipDos.getEnabledSynCheck();
	}
	
	public boolean isConfig11aRateSet(){
		String rate11a = get11aRateSetValue();
		return ssidProfile.isEnableARateSet() && rate11a != null && !"".equals(rate11a);
//		if (CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())) {
//			return false;
//		}else {
//			return ssidProfile.isEnableARateSet();
//		}
	}
	
	public boolean isConfig11gRateSet(){
		String rate11g = get11gRateSetValue();
		return ssidProfile.isEnableGRateSet() && rate11g != null && !"".equals(rate11g);
//		if (CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())) {
//			return false;
//		}else {
//			return ssidProfile.isEnableGRateSet();
//		}
	}
	
	public boolean isConfig11nRateSet(){
		String rate11n = get11nRateSetValue();
		return ssidProfile.getEnableNRateSet() && rate11n != null &&  !"".equals(rate11n);
	}
	
	public boolean isConfig11ngRateSet()throws CreateXMLException {
		return ssidProfile.getEnableNRateSet()&&(!"".equals(getExpand_11nRateSetValue()));
	}
	
	public String get11aRateSetValue(){
		Map<String, TX11aOr11gRateSetting> aRateSets = ssidProfile.getARateSets();
		StringBuffer restValue = new StringBuffer("");
		TX11aOr11gRateSetting tx11aRateObj;
		
		/** 6 */
		tx11aRateObj = aRateSets.get(TX11aOr11gRateSetting.ARateType.six.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.SIX.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 9 */
		tx11aRateObj = aRateSets.get(TX11aOr11gRateSetting.ARateType.nine.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.NINE.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 12 */
		tx11aRateObj = aRateSets.get(TX11aOr11gRateSetting.ARateType.twelve.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.TWELVE.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 18 */
		tx11aRateObj = aRateSets.get(TX11aOr11gRateSetting.ARateType.eighteen.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.EIGHTEEN.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 24 */
		tx11aRateObj = aRateSets.get(TX11aOr11gRateSetting.ARateType.twenty_four.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.TWENTY_FOUR.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 36 */
		tx11aRateObj = aRateSets.get(TX11aOr11gRateSetting.ARateType.thirty_six.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.THIRTY_SIX.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 48 */
		tx11aRateObj = aRateSets.get(TX11aOr11gRateSetting.ARateType.forty_eight.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.FORTY_EIGHT.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 54 */
		tx11aRateObj = aRateSets.get(TX11aOr11gRateSetting.ARateType.fifty_four.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.FIFTY_FOUR.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		return restValue.toString();
	}
	
	public String get11gRateSetValue(){
		Map<String, TX11aOr11gRateSetting> gRateSets = ssidProfile.getGRateSets();
		StringBuffer restValue = new StringBuffer("");
		TX11aOr11gRateSetting tx11aRateObj;
		
		/** 1 */
		tx11aRateObj = gRateSets.get(TX11aOr11gRateSetting.GRateType.one.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.ONE.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 2 */
		tx11aRateObj = gRateSets.get(TX11aOr11gRateSetting.GRateType.two.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.TWO.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 5.5 */
		tx11aRateObj = gRateSets.get(TX11aOr11gRateSetting.GRateType.five.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.FIVE.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 11 */
		tx11aRateObj = gRateSets.get(TX11aOr11gRateSetting.GRateType.eleven.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.ELEVEN.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 6 */
		tx11aRateObj = gRateSets.get(TX11aOr11gRateSetting.GRateType.six.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.SIX.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 9 */
		tx11aRateObj = gRateSets.get(TX11aOr11gRateSetting.GRateType.nine.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.NINE.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 12 */
		tx11aRateObj = gRateSets.get(TX11aOr11gRateSetting.GRateType.twelve.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.TWELVE.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 18 */
		tx11aRateObj = gRateSets.get(TX11aOr11gRateSetting.GRateType.eighteen.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.EIGHTEEN.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 24 */
		tx11aRateObj = gRateSets.get(TX11aOr11gRateSetting.GRateType.twenty_four.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.TWENTY_FOUR.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 36 */
		tx11aRateObj = gRateSets.get(TX11aOr11gRateSetting.GRateType.thirty_six.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.THIRTY_SIX.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 48 */
		tx11aRateObj = gRateSets.get(TX11aOr11gRateSetting.GRateType.forty_eight.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.FORTY_EIGHT.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		/** 54 */
		tx11aRateObj = gRateSets.get(TX11aOr11gRateSetting.GRateType.fifty_four.name());
		if(tx11aRateObj != null && tx11aRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append(RateSet11aOr11g.FIFTY_FOUR.getValue());
			if(tx11aRateObj.getRateSet() == TX11aOr11gRateSetting.RATE_SET_TYPE_BASIC){
				restValue.append(RateSet11aOr11g.BASIC.getValue());
			}
		}
		
		return restValue.toString();
	}
	
	public String get11nRateSetValue(){
		Map<String, TX11aOr11gRateSetting> nRateSets = ssidProfile.getNRateSets();
		StringBuffer restValue = new StringBuffer("");
		TX11aOr11gRateSetting tx11nRateObj;
		
		/** 0 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.zero.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("0");
		}
		
		/** 1 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.one.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("1");
		}
		
		/** 2 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.two.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("2");
		}
		
		/** 3 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.three.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("3");
		}
		
		/** 4 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.four.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("4");
		}
		
		/** 5 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.five.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("5");
		}
		
		/** 6 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.six.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("6");
		}
		
		/** 7 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.seven.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("7");
		}
		
		/** 8 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.eight.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("8");
		}
		
		/** 9 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.nine.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("9");
		}
		
		/** 10 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.ten.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("10");
		}
		
		/** 11 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.eleven.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("11");
		}
		
		/** 12 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.twelve.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("12");
		}
		
		/** 13 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.thirteen.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("13");
		}
		
		/** 14 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.fourteen.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("14");
		}
		
		/** 15 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.fifteen.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(" ");
			}
			restValue.append("15");
		}
		
		return restValue.toString();
	}
	
	public String getExpand_11nRateSetValue() throws CreateXMLException{
		Map<String, TX11aOr11gRateSetting> nRateSets = ssidProfile.getNRateSets();
		StringBuffer restValue = new StringBuffer("");
		TX11aOr11gRateSetting tx11nRateObj;
		String prefix = "mcs";
		boolean versionFlag = false;
		// TODO AP330/350 need to support new mcs format in millau
		
		if (!hiveAp.is11acHiveAP() && NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.6.0") < 0 ) {
			versionFlag = true;
		}
		
		/** 0 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.zero.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("0");
			}else{
				restValue.append(prefix + "0/1");
			}
			
		}
		
		/** 1 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.one.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("1");
			}else{
				restValue.append(prefix + "1/1");
			}
		}
		
		/** 2 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.two.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("2");
			}else{
				restValue.append(prefix + "2/1");
			}
		}
		
		/** 3 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.three.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("3");
			}else{
				restValue.append(prefix + "3/1");
			}
		}
		
		/** 4 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.four.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("4");
			}else{
				restValue.append(prefix + "4/1");
			}
		}
		
		/** 5 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.five.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("5");
			}else{
				restValue.append(prefix + "5/1");
			}
		}
		
		/** 6 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.six.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("6");
			}else{
				restValue.append(prefix + "6/1");
			}
		}
		
		/** 7 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.seven.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("7");
			}else{
				restValue.append(prefix + "7/1");
			}
		}
		
		if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
			return restValue.toString();
		}
		
		/** 8 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.eight.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("8");
			}else{
				restValue.append(prefix + "0/2");
			}
		}
		
		/** 9 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.nine.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("9");
			}else{
				restValue.append(prefix + "1/2");
			}
		}
		
		/** 10 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.ten.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("10");
			}else{
				restValue.append(prefix + "2/2");
			}
		}
		
		/** 11 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.eleven.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("11");
			}else{
				restValue.append(prefix + "3/2");
			}
		}
		
		/** 12 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.twelve.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("12");
			}else{
				restValue.append(prefix + "4/2");
			}
		}
		
		/** 13 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.thirteen.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("13");
			}else{
				restValue.append(prefix + "5/2");
			}
		}
		
		/** 14 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.fourteen.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("14");
			}else{
				restValue.append(prefix + "6/2");
			}
		}
		
		/** 15 */
		tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.fifteen.name());
		if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
			if(!"".equals(restValue.toString())){
				restValue.append(",");
			}
			if(versionFlag){
				restValue.append("15");
			}else{
				restValue.append(prefix + "7/2");
			}
		}
		
		//radio 3*3
		if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_330
				|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_350
				|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_WP
				|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ
				|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_370
				|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_390
				|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_230) {
			
			/** 16 */
			tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.sixteen.name());
			if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
				if(!"".equals(restValue.toString())){
					restValue.append(",");
				}
				if(versionFlag){
					restValue.append("16");
				}else{
					restValue.append(prefix + "0/3");
				}
			}
			
			/** 17 */
			tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.seventeen.name());
			if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
				if(!"".equals(restValue.toString())){
					restValue.append(",");
				}
				if(versionFlag){
					restValue.append("17");
				}else{
					restValue.append(prefix + "1/3");
				}
			}
			
			/** 18 */
			tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.eighteen.name());
			if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
				if(!"".equals(restValue.toString())){
					restValue.append(",");
				}
				if(versionFlag){
					restValue.append("18");
				}else{
					restValue.append(prefix + "2/3");
				}
			}
			
			/** 19 */
			tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.nineteen.name());
			if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
				if(!"".equals(restValue.toString())){
					restValue.append(",");
				}
				if(versionFlag){
					restValue.append("19");
				}else{
					restValue.append(prefix + "3/3");
				}
			}
			
			/** 20 */
			tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.twenty.name());
			if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
				if(!"".equals(restValue.toString())){
					restValue.append(",");
				}
				if(versionFlag){
					restValue.append("20");
				}else{
					restValue.append(prefix + "4/3");
				}
			}
			
			/** 21 */
			tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.twenty_one.name());
			if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
				if(!"".equals(restValue.toString())){
					restValue.append(",");
				}
				if(versionFlag){
					restValue.append("21");
				}else{
					restValue.append(prefix + "5/3");
				}
			}
			
			/** 22 */
			tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.twenty_two.name());
			if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
				if(!"".equals(restValue.toString())){
					restValue.append(",");
				}
				if(versionFlag){
					restValue.append("22");
				}else{
					restValue.append(prefix + "6/3");
				}
			}
			
			/** 23 */
			tx11nRateObj = nRateSets.get(TX11aOr11gRateSetting.NRateType.twenty_three.name());
			if(tx11nRateObj != null && tx11nRateObj.getRateSet() != TX11aOr11gRateSetting.RATE_SET_TYPE_NEI){
				if(!"".equals(restValue.toString())){
					restValue.append(",");
				}
				if(versionFlag){
					restValue.append("23");
				}else{
					restValue.append(prefix + "7/3");
				}
			}
		}else{
			if("".equals(restValue.toString())){
				String[] errParams = { ssidProfile.getSsidName()};
				String errMsg = NmsUtil.getUserMessage(
						"error.be.config.create.11nMCS", errParams);
				throw new CreateXMLException(errMsg);
			}
		}
		
		return restValue.toString();
	}
	
	public boolean isEnableInternalServers(){
		return cwp != null && cwp.getServerType() == Cwp.CWP_INTERNAL;
	}
	
	public int getRoamingAgeout(){
		return ssidProfile.getAgeOut();
	}
	
	public boolean isConfigInterStationTraffic(){
		return serviceFilter != null;
	}
	
	public boolean isEnableInterStationTraffic(){
		return serviceFilter.getInterTraffic();
	}
	
	public int getMaxClient(){
		int maxClient = ssidProfile.getMaxClient();
		if(maxClient > 64 && ((NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.4.3.0")) < 0 ||
				hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_28 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_20)){
			return 64;
		}else if(maxClient > 32 && hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
			return 32;
		}else{
			return ssidProfile.getMaxClient();
		}
	}
	
	public int getPskUserGroupSize(){
		if(pskUserGroupList == null){
			pskUserGroupList = new ArrayList<LocalUserGroup>();
			if(ssidProfile.getLocalUserGroups() != null){
				pskUserGroupList.addAll(ssidProfile.getLocalUserGroups());
			}
		}
		return pskUserGroupList.size();
	}
	
	public String getPskUserGroupName(int index){
		return pskUserGroupList.get(index).getGroupName();
	}
	
	public boolean isEnableLegacy(){
		return ssidProfile.getEnabledLegacy();
	}
	
	public int getRoamingUpdateInterval(){
		return ssidProfile.getUpdateInterval();
	}
	
	public boolean isConfigAirScreen(){
		return airScreenRules != null && airScreenRules.size() > 0;
	}
	
	public int getAirScreenSize(){
		return airScreenRules.size();
	}
	
	public String getAirScreenRuleName(int index){
		return airScreenRules.get(index).getProfileName();
	}
	
	public int getClientAgeOut(){
		return ssidProfile.getClientAgeOut();
	}
	
	public String getSecurityObjectName(){
		return ssidProfile.getSsid();
	}
	
	public int getCuThresholdValue(){
		return ssidProfile.getCuthreshold();
	}
	
	public int getMemberThresholdValue(){
		return ssidProfile.getMemberthreshold();
	}
	
	public SsidMulticastConversionToUnicastValue getMulticastConversionValue(){
		if(ssidProfile.getConvtounicast() == SsidProfile.CONVTOUNICAST_AUTO){
			return SsidMulticastConversionToUnicastValue.AUTO;
		}else if(ssidProfile.getConvtounicast() == SsidProfile.CONVTOUNICAST_ALWAYS){
			return SsidMulticastConversionToUnicastValue.ALWAYS;
		}else if(ssidProfile.getConvtounicast() == SsidProfile.CONVTOUNICAST_DISABLE){
			return SsidMulticastConversionToUnicastValue.DISABLE;
		}else{
			return SsidMulticastConversionToUnicastValue.AUTO;
		}
	}
	
	public boolean isEnableRrm(){
		return ssidProfile.isEnabled80211k();
	}

	public boolean isEnableAdmctl(){
		return ssidProfile.isEnabledAcBesteffort() || ssidProfile.isEnabledAcBackground() 
				|| ssidProfile.isEnabledAcVideo() || ssidProfile.isEnabledAcVoice();
	}

	public boolean isConfigAcNumber(int index) {
		if(index == WMM_AC_BESTEFFORT){
			return ssidProfile.isEnabledAcBesteffort();
		}else if(index == WMM_AC_BACKGROUND){
			return ssidProfile.isEnabledAcBackground();
		}else if(index == WMM_AC_VIDEO){
			return ssidProfile.isEnabledAcVideo();
		}else if(index == WMM_AC_VOICE){
			return ssidProfile.isEnabledAcVoice();
		}else{
			return false;
		}
	}
	
	public class WalledGarden{
		private String address;
		
		private boolean all = false;
		
		private boolean web = false;
		
		private final List<Protocol> protocolList = new ArrayList<Protocol>();
		
		public void setAddress(String address){
			this.address = address;
		}
		
		public String getAddress(){
			return this.address;
		}
		
		public void setAll(boolean isAll){
			this.all = isAll;
		}
		
		public boolean isAll(){
			return this.all;
		}
		
		public void setWeb(boolean isWeb){
			this.web = isWeb;
		}
		
		public boolean isWeb(){
			return this.web;
		}
		
		public List<Protocol> getProtocolList(){
			return this.protocolList;
		}
		
		public Protocol newProtocol(){
			return new Protocol();
		}

		public class Protocol{
			private int protocolValue;
			
			private final List<Integer> portList = new ArrayList<Integer>();
			
			public void setProtocolValue(int value){
				this.protocolValue = value;
			}
			
			public int getProtocolValue(){
				return this.protocolValue;
			}
			
			public List<Integer> getPortList(){
				return this.portList;
			}
		}
	}
	
	public int getEgressMulticastThreshold() {
		return hiveAp.getConfigTemplate().getEgressMulticastThreshold();
	}
	
	public int getEgressMulticastInterval() {
		return hiveAp.getConfigTemplate().getEgressMulticastInterval();
	}
	
	public boolean isEnableConnectionAlarm() {
		return hiveAp.getConfigTemplate().isEnableConnectionAlarm();
	}
	
	public boolean isConfigPriority(){
		return this.ssidProfile.getWifiPriority() > 0;
	}
	
	public int getPriority(){
		return this.ssidProfile.getWifiPriority();
	}

	@Override
	public String get11acRageSets() {
		
		if(NmsUtil.compareSoftwareVersion("6.0.2.0",hiveAp.getSoftVer()) > 0){
			return null;
		}

		List<Tx11acRateSettings> acRateSets = ssidProfile.getAcRateSets();
		
		if(acRateSets == null || acRateSets.size() == 0){
			return null;
		}
		
		StringBuffer restValue = new StringBuffer("");
		
		String singlePrefix = "mcs0/1,mcs1/1,mcs2/1,mcs3/1,mcs4/1,mcs5/1,mcs6/1";
		String twoPrefix = "mcs0/2,mcs1/2,mcs2/2,mcs3/2,mcs4/2,mcs5/2,mcs6/2";
		String threePrefix = "mcs0/3,mcs1/3,mcs2/3,mcs3/3,mcs4/3,mcs5/3,mcs6/3";
		
		for(Tx11acRateSettings tx11acRateObj : acRateSets){
			if(tx11acRateObj.isStreamEnable() && tx11acRateObj.getStreamType() == Tx11acRateSettings.STREAM_TYPE_SINGLE){
				if(!"".equals(restValue.toString())){
					restValue.append(",");
				}
				restValue.append(singlePrefix);
				restValue.append(",");
				restValue.append("mcs" + Tx11acRateSettings.MIN_MCS_VALUE + "/1");
			}
			
			if(tx11acRateObj.isStreamEnable() && tx11acRateObj.getStreamType() == Tx11acRateSettings.STREAM_TYPE_TWO){
				if(!"".equals(restValue.toString())){
					restValue.append(",");
				}
				restValue.append(twoPrefix);
				restValue.append(",");
				restValue.append("mcs" + Tx11acRateSettings.MIN_MCS_VALUE + "/2");
			}
			
			if(tx11acRateObj.isStreamEnable() && tx11acRateObj.getStreamType() == Tx11acRateSettings.STREAM_TYPE_THREE){
				if(!"".equals(restValue.toString())){
					restValue.append(",");
				}
				restValue.append(threePrefix);
				restValue.append(",");
				restValue.append("mcs" + Tx11acRateSettings.MIN_MCS_VALUE + "/3");
			}
		}
		
		for(Tx11acRateSettings tx11acRateObj : acRateSets){
			if(tx11acRateObj.isStreamEnable() && tx11acRateObj.getStreamType() == Tx11acRateSettings.STREAM_TYPE_SINGLE){
				if(tx11acRateObj.getMcsValue() > Tx11acRateSettings.MEDIAN_MCS_VALUE){
					restValue.append(",");
					restValue.append("mcs" + Tx11acRateSettings.MEDIAN_MCS_VALUE + "/1");
					restValue.append(",");
					restValue.append("mcs" + Tx11acRateSettings.MAX_MCS_VALUE + "/1");
				}else if(tx11acRateObj.getMcsValue() == Tx11acRateSettings.MEDIAN_MCS_VALUE){
					restValue.append(",");
					restValue.append("mcs" + Tx11acRateSettings.MEDIAN_MCS_VALUE + "/1");
				}
			}
			
			if(tx11acRateObj.isStreamEnable() && tx11acRateObj.getStreamType() == Tx11acRateSettings.STREAM_TYPE_TWO){
				if(tx11acRateObj.getMcsValue() > Tx11acRateSettings.MEDIAN_MCS_VALUE){
					restValue.append(",");
					restValue.append("mcs" + Tx11acRateSettings.MEDIAN_MCS_VALUE + "/2");
					restValue.append(",");
					restValue.append("mcs" + Tx11acRateSettings.MAX_MCS_VALUE + "/2");
				}else if(tx11acRateObj.getMcsValue() == Tx11acRateSettings.MEDIAN_MCS_VALUE){
					restValue.append(",");
					restValue.append("mcs" + Tx11acRateSettings.MEDIAN_MCS_VALUE + "/2");
				}
			}
			
			if(tx11acRateObj.isStreamEnable() && tx11acRateObj.getStreamType() == Tx11acRateSettings.STREAM_TYPE_THREE){
				if(tx11acRateObj.getMcsValue() > Tx11acRateSettings.MEDIAN_MCS_VALUE){
					restValue.append(",");
					restValue.append("mcs" + Tx11acRateSettings.MEDIAN_MCS_VALUE + "/3");
					restValue.append(",");
					restValue.append("mcs" + Tx11acRateSettings.MAX_MCS_VALUE + "/3");
				}else if(tx11acRateObj.getMcsValue() == Tx11acRateSettings.MEDIAN_MCS_VALUE){
					restValue.append(",");
					restValue.append("mcs" + Tx11acRateSettings.MEDIAN_MCS_VALUE + "/3");
				}
			}
		}
		return restValue.toString();
	}

	@Override
	public boolean isConfig11acRateSet() {
		// TODO Auto-generated method stub
		String rate11ac = get11acRageSets();
		return ssidProfile.isEnableACRateSet() && rate11ac != null && !"".equals(rate11ac);
	}
}