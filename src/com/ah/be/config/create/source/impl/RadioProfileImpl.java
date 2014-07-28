package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.RadioProfileInt;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.RadioProfileWmmInfo;
import com.ah.bo.wlan.SlaMappingCustomize.ClientPhyMode;
import com.ah.util.CountryCode;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.xml.be.config.BandSteeringModeValue;
import com.ah.xml.be.config.ClientLoadBalanceModeValue;

/**
 * 
 * @author zhang
 *
 */
public class RadioProfileImpl implements RadioProfileInt {
	
	private static final Tracer log = new Tracer(RadioProfileImpl.class
			.getSimpleName());
	
	private List<RadioProfile> radioProfileList;
	private HiveAp hiveAp;
	private Long wifi0RadId;
	private Long wifi1RadId;
	private RadioProfile radiowifi0;
	
	private boolean presenceEnable;
    private HMServicesSettings hmServicesSettings;

	public RadioProfileImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
		radioProfileList = new ArrayList<RadioProfile>();
		RadioProfile radioProfile;
		
		if(hiveAp.getWifi0RadioProfile() != null ){
			radioProfile = hiveAp.getWifi0RadioProfile();
			hiveAp.setWifi0RadioProfile(radioProfile);
			radioProfileList.add(radioProfile);
			wifi0RadId = radioProfile.getId();
			radiowifi0=radioProfile;
		}
		if(hiveAp.getWifi1RadioProfile() != null ){
			radioProfile = hiveAp.getWifi1RadioProfile();
			hiveAp.setWifi1RadioProfile(radioProfile);
			radioProfileList.add(radioProfile);
			wifi1RadId = radioProfile.getId();
		}
		//hmServicesSettings = new HmServiceSettingService().getServiceSetting(hiveAp.getOwner().getId());
		presenceEnable = queryPresenceState();
	}
	
	private static Boolean queryPresenceState() {
		String query = "select presenceEnable from "
				+ HMServicesSettings.class.getCanonicalName();
		List<?> list = MgrUtil.getQueryEntity().executeQuery(query, null, new FilterParams("owner.domainName", HmDomain.HOME_DOMAIN));
		if (null == list) {
			return false;
		}
		return (Boolean) list.get(0);
	}
	
	public boolean isConfigureRadioTree(){
		return hiveAp.getDeviceInfo().getIntegerValue(DeviceInfo.SPT_RADIO_COUNTS) > 0;
	}
	
	public String getRadioProfileGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.radioProfiles");
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}
	
	public String getUpdateTime(){
		List<Object> radioList = new ArrayList<Object>();
		if(radioProfileList != null){
			radioList.addAll(radioProfileList);
		}
		return CLICommonFunc.getLastUpdateTime(radioList);
	}
	
	public String getProfileUpdatTime(int index){
		List<Object> radioList = new ArrayList<Object>();
		if(radioProfileList != null){
			radioList.add(radioProfileList.get(index));
		}
		return CLICommonFunc.getLastUpdateTime(radioList);
	}
	
//	public boolean isConfigureRadioTree(){
//		for(RadioProfile radioObj : radioProfileList){
//			if(!radioObj.isCliDefaultFlag()){
//				return true;
//			}
//		}
//		return false;
//	}
	
	public boolean isConfigureRadioProfile(int index){
//		return !CLICommonFunc.isDefaultProfile(radioProfileList.get(index));
		RadioProfile radProf = radioProfileList.get(index);
		if(!radProf.isCliDefaultFlag()){
			return true;
		}else{
			String radName = radProf.getRadioName();
			if (hiveAp.is11acHiveAP()) {
				//for 11ac ap, wifi1 default radio profile is radio_ac0 not radio_na0
				if ("radio_ac0".equals(radName)) {
					return false;
				}
				else if("radio_na0".equals(radName)) {
					return true;
				}
			}
			if(hiveAp.is11nHiveAP() && ("radio_na0".equals(radName) || "radio_ng0".equals(radName))){
				return false;
			}else if(!hiveAp.is11nHiveAP() && ("radio_a0".equals(radName) || "radio_g0".equals(radName))){
				return false;
			}else{
				return true;
			}
		}
	}
	
	public int getRadioProfileSize(){
		return radioProfileList.size();
	}
	
	public String getName(int index){
		return radioProfileList.get(index).getRadioName();
	}
	
	public int getBeaconPeriod(int index){
		return radioProfileList.get(index).getBeaconPeriod();
	}
	
	public boolean isEnableBackhaulFailover(int index){
		RadioProfile radProfile = radioProfileList.get(index);
		return radProfile.getBackhaulFailover();
	}
	
	public int getMaxClient(int index){
//		return radioProfileList.get(index).getMaxClients();
		int maxClient = radioProfileList.get(index).getMaxClients();
		if(maxClient > 64 && ((NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.4.3.0")) < 0 ||
				hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_28 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_20)){
			return 64;
		}else if(maxClient > 32 && hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
			return 32;
		}else{
			return maxClient;
		}
	}
	
	public String getRadioPhyMode(int index){
		short mode = radioProfileList.get(index).getRadioMode();
		if(mode == RadioProfile.RADIO_PROFILE_MODE_A){
			return RadioProfileInt.RadioPhyMode.M11a.getValue();
		}else if(mode == RadioProfile.RADIO_PROFILE_MODE_BG){
			return RadioProfileInt.RadioPhyMode.M11bg.getValue();
		}else if(mode == RadioProfile.RADIO_PROFILE_MODE_NA){
			return RadioProfileInt.RadioPhyMode.M11na.getValue();
		}else if(mode == RadioProfile.RADIO_PROFILE_MODE_NG){
			return RadioProfileInt.RadioPhyMode.M11ng.getValue();
		}else if(mode == RadioProfile.RADIO_PROFILE_MODE_AC){
			return RadioProfileInt.RadioPhyMode.M11AC.getValue();
		}else{
			return null;
		}
	}
	
	public boolean isConfigureRadioScan(int index){
		return radioProfileList.get(index).getBackgroundScan();
	}
	
	public boolean isShortPreamble(int index){
		short preamble = radioProfileList.get(index).getShortPreamble();
		return !(preamble != RadioProfile.RADIO_PROFILE_PREAMBLE_SHORT &&
				 (getRadioPhyMode(index).equals(RadioProfileInt.RadioPhyMode.M11bg.getValue()) ||
						 getRadioPhyMode(index).equals(RadioProfileInt.RadioPhyMode.M11ng.getValue())
				 )
				);
	}
	
	public int getBackHaulTriggerTime(int index){
		return radioProfileList.get(index).getTriggerTime();
	}
	
	public int getBackHaulHoldTime(int index){
		return radioProfileList.get(index).getHoldTime();
	}
	
	public int getScanInterval(int index){
		return radioProfileList.get(index).getInterval();
	}
	
	public boolean isEnableScanVoice(int index){
		return radioProfileList.get(index).getTrafficVoice();
	}
	
//	public boolean isConfigRadiuAcsp(int index){
//		RadioProfile radioProfile = radioProfileList.get(index);
//		return radioProfile.isEnableChannel() || radioProfile.isEnablePower();
//	}
	
	public boolean isConfigAcspMaxPower(int index){
		return radioProfileList.get(index).isEnablePower();
	}
	
	public int getAcspMaxPower(int index){
		return radioProfileList.get(index).getTransmitPower();
	}
	
//	public boolean isConfigAcspAccess(int index){
//		short radioMode = radioProfileList.get(index).getRadioMode();
//		return radioProfileList.get(index).isEnableChannel() || 
//			radioMode == RadioProfile.RADIO_PROFILE_MODE_NA ||
//			radioMode == RadioProfile.RADIO_PROFILE_MODE_NG;
//	}
	
	public boolean isConfigAcspAccessChannel(int index){
		RadioProfile radio = radioProfileList.get(index);
		if(radio.getId().equals(this.wifi0RadId)){
			return radio.isEnableChannel() 
					&& this.hiveAp.getWifi0().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL 
					&& this.hiveAp.getWifi0().getOperationMode() != AhInterface.OPERATION_MODE_DUAL;
		}else if(radio.getId().equals(this.wifi1RadId)){
			return radio.isEnableChannel() 
					&& this.hiveAp.getWifi1().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL 
					&& this.hiveAp.getWifi1().getOperationMode() != AhInterface.OPERATION_MODE_DUAL;
		}else{
			return radioProfileList.get(index).isEnableChannel();
		}
	}
	
	public boolean isConfigAcspDfs(int index){
		return CountryCode.isSupportDfs(hiveAp.getCountryCode(), hiveAp.getHiveApModel()) &&
//			m_hm_list.containsKey((Integer)hiveAp.getCountryCode()) &&
			(radioProfileList.get(index).getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA || 
			radioProfileList.get(index).getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A ||
			radioProfileList.get(index).getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC);
	}
	
	public boolean isEnableAcspDfs(int index){
		return radioProfileList.get(index).isEnableDfs();
	}
	
	public boolean isRadarDetectOnly(int index){
		if(this.hiveAp.getIsOutdoor() != null && this.hiveAp.getIsOutdoor()){
			return true;
		}
		return radioProfileList.get(index).isEnableRadarDetect();
	}
	
	public String getChannelAutoSelectTimeRange(int index){
		RadioProfile radioProfile = radioProfileList.get(index);
		String fromHour = String.valueOf(radioProfile.getFromHour());
		String fromMin = String.valueOf(radioProfile.getFromMinute());
		String toHour = String.valueOf(radioProfile.getToHour());
		String toMin = String.valueOf(radioProfile.getToMinute());
		if(fromHour.length() == 1){
			fromHour = "0" + fromHour;
		}
		if(fromMin.length() == 1){
			fromMin = "0" + fromMin;
		}
		if(toHour.length() == 1){
			toHour = "0" + toHour;
		}
		if(toMin.length() == 1){
			toMin = "0" + toMin;
		}
		return fromHour+":"+fromMin+" "+toHour+":"+toMin;
	}
	
	public int getChannelAutoSelectStation(int index){
//		return radioProfileList.get(index).getChannelClient();
		int maxClient = radioProfileList.get(index).getChannelClient();
		if(maxClient > 64 && ((NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.4.3.0")) < 0 ||
				hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_28 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_20)){
			return 64;
		}else{
			return maxClient;
		}
	}
	
//	public boolean isEnableRadioTurbo(int index){
//		//when a mode, enable turbo, backhaul; return ture
//		RadioProfile radioProfile = radioProfileList.get(index);
//		boolean isBackhaul = false;
//		if(radioProfile.getId().longValue() == hiveAp.getWifi0RadioProfile().getId().longValue()){
//			isBackhaul = hiveAp.getWifi0().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL;
//		}
//		if(radioProfile.getId().longValue() == hiveAp.getWifi1RadioProfile().getId().longValue()){
//			isBackhaul = hiveAp.getWifi1().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL;
//		}
//		return radioProfile.isTurboMode() && radioProfile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A
//				&& isBackhaul;
//	}
	
	public boolean isHiveAp11n(){
		return hiveAp.is11nHiveAP();
	}
	
	private boolean isConfigChannelWidth(int index){
		RadioProfile radioProfile = radioProfileList.get(index);

		if (radioProfile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC) {
			return true;
		}
		
		// only AP120/110/170/320/340 2.4G radio support 40M.
		return isHiveAp11n() && (radioProfile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA ||
				(radioProfile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG 
					&& hiveAp.getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_40M_FOR_24G)) );
	}
	
//	public String getRadioChannelWidth(int index){
//		short channelWidth = radioProfileList.get(index).getChannelWidth();
//		switch(channelWidth){
//			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20 :
//				return RadioProfileInt.RadioChannelWidth._20.getValue();
//			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A :
//				return sptPrimaryOffset() ? "40" : RadioProfileInt.RadioChannelWidth._40_above.getValue();
//			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40B :
//				return sptPrimaryOffset() ? "40" : RadioProfileInt.RadioChannelWidth._40_below.getValue();
//			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40 :
//				return "40";
//			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80 :
//				return "80";	
//			default:
//				return null;
//		}
//	}
	
//	private boolean sptPrimaryOffset(){
//		return hiveAp.is11acHiveAP() && 
//			   hiveAp.getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_40M_FOR_24G);
//	}
	
	public boolean isEnableAllow_11b_clients(int index){
//		return radioProfileList.get(index).isAllow11b();
		return !radioProfileList.get(index).isDeny11b();
	}
	
	public boolean isEnableOnly_11n_clients(int index){
//		return radioProfileList.get(index).isAllow11n();
		return !radioProfileList.get(index).isDeny11abg();
	}
	
	public boolean isConfigShortGuardInterval(int index){
		RadioProfile radio = radioProfileList.get(index);
		
		short hiveApModel = this.hiveAp.getHiveApModel();
		short radioMode = radio.getRadioMode();
		short channelWidth = radio.getChannelWidth();
		
		//change for fix bug 20387
		switch(hiveApModel){
			case HiveAp.HIVEAP_MODEL_110:
			case HiveAp.HIVEAP_MODEL_120:
			case HiveAp.HIVEAP_MODEL_170:
			case HiveAp.HIVEAP_MODEL_320:
			case HiveAp.HIVEAP_MODEL_340:
			case HiveAp.HIVEAP_MODEL_380:
				return channelWidth != RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20;
			case HiveAp.HIVEAP_MODEL_330:
			case HiveAp.HIVEAP_MODEL_350:
			case HiveAp.HIVEAP_MODEL_121:
			case HiveAp.HIVEAP_MODEL_141:
			case HiveAp.HIVEAP_MODEL_BR200_WP:
			case HiveAp.HIVEAP_MODEL_BR200_LTE_VZ:
				return radioMode == RadioProfile.RADIO_PROFILE_MODE_NA || 
					(radioMode == RadioProfile.RADIO_PROFILE_MODE_NG && 
						channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20) || 
					// !isConfigChannelWidth(index) means channel 20
					(radioMode == RadioProfile.RADIO_PROFILE_MODE_NG && 
						!isConfigChannelWidth(index));
			case HiveAp.HIVEAP_MODEL_370:
			case HiveAp.HIVEAP_MODEL_390:
			case HiveAp.HIVEAP_MODEL_230:
				return radioMode == RadioProfile.RADIO_PROFILE_MODE_NA || 
					   radioMode == RadioProfile.RADIO_PROFILE_MODE_AC ||
					(radioMode == RadioProfile.RADIO_PROFILE_MODE_NG && 
						channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20) || 
					(radioMode == RadioProfile.RADIO_PROFILE_MODE_NG && 
						!isConfigChannelWidth(index));
			case HiveAp.HIVEAP_MODEL_BR100:
				return (radioMode == RadioProfile.RADIO_PROFILE_MODE_NG && 
							channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20) || 
						(radioMode == RadioProfile.RADIO_PROFILE_MODE_NG && 
							!isConfigChannelWidth(index));
			default:
				return false;
		}
	}
	
	public boolean isEnableShortGuardInterval(int index){
		return radioProfileList.get(index).isGuardInterval();
	}
	
	public boolean isEnableAmpdu(int index){
		return radioProfileList.get(index).isAggregateMPDU();
	}
	
	public int getTransmitChain(int index) throws CreateXMLException{
		if(radioProfileList.get(index).isUseDefaultChain()){
			if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_110 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_120 ||
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_121 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_141 ||
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_170 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_WP
					|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ){
				return 2;
			}else if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_320 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_340 ||
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_380){
				return 3;
			}else if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_330 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_350 ||
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_370 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_390 ||
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_230){
				return 3;
			}else{
				return 1;
			}
		}
		int key = radioProfileList.get(index).getTransmitChain();
		if(key == RadioProfile.RADIO_PROFILE_CHAIN_3){
			if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_120 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_110 ||
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_121 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_141 ||
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_170 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
				String[] errParm = {radioProfileList.get(index).getRadioName()};
				String errMsg = NmsUtil.getUserMessage("error.be.config.create.errorChain", errParm);
				log.error("getTransmitChain", errMsg);
				throw new CreateXMLException(errMsg);
			}else{
				return 3;
			}
		}else if(key == RadioProfile.RADIO_PROFILE_CHAIN_2){
			if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
				String[] errParm = {radioProfileList.get(index).getRadioName()};
				String errMsg = NmsUtil.getUserMessage("error.be.config.create.errorChain", errParm);
				log.error("getTransmitChain", errMsg);
				throw new CreateXMLException(errMsg);
			}else{
				return 2;
			}
		}else {
			return 1;
		}
	}
	
	public int getReceiveChain(int index) throws CreateXMLException{
		if(radioProfileList.get(index).isUseDefaultChain()){
			if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_110 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_120 || 
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_121 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_141 || 
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_170 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_WP || 
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ){
				return 2;
			}else if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_320 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_340 ||
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_380){
				return 2;
			}else if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_330 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_350 ||
					 hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_370 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_390 ||
					 hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_230){
				return 3;
			}else{
				return 1;
			}
		}
		int key = radioProfileList.get(index).getReceiveChain();
		if(key == RadioProfile.RADIO_PROFILE_CHAIN_3){
			if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_120 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_110 || 
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_121 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_141 || 
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_170 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
				String[] errParm = {radioProfileList.get(index).getRadioName()};
				String errMsg = NmsUtil.getUserMessage("error.be.config.create.errorChain", errParm);
				log.error("getReceiveChain", errMsg);
				throw new CreateXMLException(errMsg);
			}else{
				return 3;
			}
		}else if(key == RadioProfile.RADIO_PROFILE_CHAIN_2){
			if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
				String[] errParm = {radioProfileList.get(index).getRadioName()};
				String errMsg = NmsUtil.getUserMessage("error.be.config.create.errorChain", errParm);
				log.error("getReceiveChain", errMsg);
				throw new CreateXMLException(errMsg);
			}else{
				return 2;
			}
		}else{
			return 1;
		}
	}
	
	public boolean isConfigReceiveChain(int index){
		RadioProfile radioProfile = radioProfileList.get(index);
		return !radioProfile.isUseDefaultChain() && (radioProfile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA ||
				radioProfile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG ||
				radioProfile.getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC);
	}
	
	public boolean isConfigDenyClient(int index){
		RadioProfile radio = radioProfileList.get(index);
		return this.isHiveAp11n() && (radio.isDeny11abg() || radio.isDeny11b());
	}
	
	public String getDenyClientValue(int index){
		if(radioProfileList.get(index).isDeny11b()){
			return RadioClientDenyType._11b.getValue();
		}else if(radioProfileList.get(index).isDeny11abg()){
			return RadioClientDenyType._11abg.getValue();
		}else{
			return null;
		}
	}
	
	public boolean isEnableScanAccessClient(int index){
		return radioProfileList.get(index).isClientConnect();
	}
	
	public boolean isEnablePowerSave(int index){
		return radioProfileList.get(index).isPowerSave();
	}
	
	public boolean isConfigChannelMode(int index){
		short radioMode = radioProfileList.get(index).getRadioMode();
		if(radioMode == RadioProfile.RADIO_PROFILE_MODE_A || radioMode == RadioProfile.RADIO_PROFILE_MODE_NA){
			return false;
		}
		
		if(radioProfileList.get(index).isUseDefaultChannelModel()){
			return false;
		}
		
		
		int countryCode = hiveAp.getCountryCode();
		short continent = radioProfileList.get(index).getChannelRegion();
		if(!this.isContryCodeVestContinent(countryCode, continent)){
			return false;
		}
		
		short channelModel = radioProfileList.get(index).getChannelModel();
		String channelValue = radioProfileList.get(index).getChannelValue();
		if(continent == RadioProfile.RADIO_PROFILE_CHANNEL_REGION_US 
				&& channelModel == RadioProfile.RADIO_PROFILE_CHANNEL_MODEL_3
				&& "01-06-11".equals(channelValue)){
			return false;
		}
		if(continent == RadioProfile.RADIO_PROFILE_CHANNEL_REGION_EUR
				&& channelModel == RadioProfile.RADIO_PROFILE_CHANNEL_MODEL_4
				&& "01-05-09-13".equals(channelValue)){
			return false;
		}
		
		return true;
	}
	
	private boolean isContryCodeVestContinent(int countryCode, short Continent){
		int[] channelList = CountryCode.getChannelList_2_4GHz(
				countryCode,
				RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20);
		short count = 0;
		for(int i=0; i<channelList.length; i++){
			if		(channelList[i] == AhInterface.CHANNEL_BG_1){
				count++;
			}else if(channelList[i] == AhInterface.CHANNEL_BG_2){
				count++;
			}else if(channelList[i] == AhInterface.CHANNEL_BG_3){
				count++;
			}else if(channelList[i] == AhInterface.CHANNEL_BG_4){
				count++;
			}else if(channelList[i] == AhInterface.CHANNEL_BG_5){
				count++;
			}else if(channelList[i] == AhInterface.CHANNEL_BG_6){
				count++;
			}else if(channelList[i] == AhInterface.CHANNEL_BG_7){
				count++;
			}else if(channelList[i] == AhInterface.CHANNEL_BG_8){
				count++;
			}else if(channelList[i] == AhInterface.CHANNEL_BG_9){
				count++;
			}else if(channelList[i] == AhInterface.CHANNEL_BG_10){
				count++;
			}else if(channelList[i] == AhInterface.CHANNEL_BG_11){
				count++;
			}else if(channelList[i] == AhInterface.CHANNEL_BG_12){
				count++;
			}else if(channelList[i] == AhInterface.CHANNEL_BG_13){
				count++;
			}
		}
		
		if(count == 11){
			return Continent == RadioProfile.RADIO_PROFILE_CHANNEL_REGION_US;
		}else if(count == 13){
			return Continent == RadioProfile.RADIO_PROFILE_CHANNEL_REGION_EUR;
		}else{
			return true;
		}
	}
	
	public boolean isConfigChannel_3(int index){
		int countryCode = hiveAp.getCountryCode();
		short continent = radioProfileList.get(index).getChannelRegion();
		if(this.isContryCodeVestContinent(countryCode, continent)){
			return radioProfileList.get(index).getChannelModel() == RadioProfile.RADIO_PROFILE_CHANNEL_MODEL_3;
		}else{
			if(CountryCode.isEuropeCountry(hiveAp.getCountryCode()) || 
					CountryCode.isJapan(hiveAp.getCountryCode())){
				return false;
			}
			if(CountryCode.isUSA(hiveAp.getCountryCode())){
				return true;
			}
			return radioProfileList.get(index).getChannelModel() == RadioProfile.RADIO_PROFILE_CHANNEL_MODEL_3;
		}
	}
	
	public boolean isConfigChannel_4(int index){
		int countryCode = hiveAp.getCountryCode();
		short continent = radioProfileList.get(index).getChannelRegion();
		if(this.isContryCodeVestContinent(countryCode, continent)){
			return radioProfileList.get(index).getChannelModel() == RadioProfile.RADIO_PROFILE_CHANNEL_MODEL_4;
		}else{
			if(CountryCode.isEuropeCountry(hiveAp.getCountryCode()) || 
					CountryCode.isJapan(hiveAp.getCountryCode())){
				return true;
			}
			if(CountryCode.isUSA(hiveAp.getCountryCode())){
				return false;
			}
			return radioProfileList.get(index).getChannelModel() == RadioProfile.RADIO_PROFILE_CHANNEL_MODEL_4;
		}
	}
	
	public boolean isConfigChannelCr(int index) throws CreateXMLException{
		String channelStr = radioProfileList.get(index).getChannelValue();
		int countryCode = hiveAp.getCountryCode();
		short continent = radioProfileList.get(index).getChannelRegion();
		if(this.isContryCodeVestContinent(countryCode, continent)){
			if(channelStr != null && !"".equals(channelStr)){
				if(continent == RadioProfile.RADIO_PROFILE_CHANNEL_REGION_US){
					for(String channel : channelStr.split("-")){
						if(Integer.parseInt(channel) > 11){
							String[] errParm = {radioProfileList.get(index).getChannelValue()};
							String errMsg = NmsUtil.getUserMessage("error.be.config.create.errorChannel", errParm);
							log.error("isConfigChannelCr", errMsg);
							throw new CreateXMLException(errMsg);
						}
					}
				}
				return true;
			}else{
				return false;
			}
			
		}else{
			return false;
		}
		
//		if(CountryCode.isEuropeCountry(hiveAp.getCountryCode()) || 
//				CountryCode.isJapan(hiveAp.getCountryCode())){
//			return radioProfileList.get(index).getChannelModel() == RadioProfile.RADIO_PROFILE_CHANNEL_MODEL_4;
//		}
//		if(CountryCode.isUSA(hiveAp.getCountryCode())){
//			return radioProfileList.get(index).getChannelModel() == RadioProfile.RADIO_PROFILE_CHANNEL_MODEL_3;
//		}
//		
//		String channelStr = radioProfileList.get(index).getChannelValue();
//		return channelStr != null && !"".equals(channelStr);
	}
	
	public String getChannelCr(int index){
		StringBuffer buf = new StringBuffer();
		String[] channelStr = radioProfileList.get(index).getChannelValue().split("-");
		int i=0;
		for(String channel : channelStr){
			i++;
			if(channel.length() < 2){
				if(i == channelStr.length){
					buf.append("0").append(channel);
				}else{
					buf.append("0").append(channel).append("-");
				}
			}else{
				if(i == channelStr.length){
					buf.append(channel);
				}else{
					buf.append(channel).append("-");
				}
				
			}
		}
		return buf.toString();
	}
	
	public int getAifsValue(int index, WmmType wmmType){
		if(wmmType == WmmType.background){
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.BG.name()).getAifs();
		}else if(wmmType == WmmType.best_effort){
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.BE.name()).getAifs();
		}else if(wmmType == WmmType.video){
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.VI.name()).getAifs();
		}else{
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.VO.name()).getAifs();
		}
	}
	
	public int getCwmaxValue(int index, WmmType wmmType){
		if(wmmType == WmmType.background){
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.BG.name()).getMaximum();
		}else if(wmmType == WmmType.best_effort){
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.BE.name()).getMaximum();
		}else if(wmmType == WmmType.video){
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.VI.name()).getMaximum();
		}else{
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.VO.name()).getMaximum();
		}
	}
	
	public int getCwminValue(int index, WmmType wmmType){
		if(wmmType == WmmType.background){
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.BG.name()).getMinimum();
		}else if(wmmType == WmmType.best_effort){
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.BE.name()).getMinimum();
		}else if(wmmType == WmmType.video){
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.VI.name()).getMinimum();
		}else{
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.VO.name()).getMinimum();
		}
	}
	
	public int getTxoplimitValue(int index, WmmType wmmType){
		if(wmmType == WmmType.background){
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.BG.name()).getTxoplimit();
		}else if(wmmType == WmmType.best_effort){
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.BE.name()).getTxoplimit();
		}else if(wmmType == WmmType.video){
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.VI.name()).getTxoplimit();
		}else{
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.VO.name()).getTxoplimit();
		}
	}
	
	public boolean isNoack(int index, WmmType wmmType){
		if(wmmType == WmmType.background){
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.BG.name()).isNoAck();
		}else if(wmmType == WmmType.best_effort){
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.BE.name()).isNoAck();
		}else if(wmmType == WmmType.video){
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.VI.name()).isNoAck();
		}else{
			return radioProfileList.get(index).getWmmItems().get(RadioProfileWmmInfo.AccessCategory.VO.name()).isNoAck();
		}
	}
	
	public boolean isConfigInterMap(int index){
		short radioMod = radioProfileList.get(index).getRadioMode();
		return radioMod == RadioProfile.RADIO_PROFILE_MODE_NA || radioMod == RadioProfile.RADIO_PROFILE_MODE_NG || radioMod == RadioProfile.RADIO_PROFILE_MODE_AC;
	}
	
	public boolean isEnableInterMap(int index){
		return radioProfileList.get(index).isEnableInterfernce();
	}
	
	public int getCrcThreshold(int index){
		return radioProfileList.get(index).getCrcThreshold();
	}
	
	public int getCuThreshold(int index){
		return radioProfileList.get(index).getChannelThreshold();
	}
	
	public int getShortInterval(int index){
		return radioProfileList.get(index).getAverageInterval();
	}
	
	public int getRateSize(int index){
		return 2;
	}
	
	public String getRateName(ClientPhyMode type, int indexRad, int indexRate){
		RadioProfile radio = radioProfileList.get(indexRad);
		String rate = null;
		switch(indexRate){
			case 0:
				rate =  radio.getSLATopRate(type);
				break;
			case 1:
				rate =  radio.getSLABottomRate(type);
				break;
			default:
				rate =  radio.getSLATopRate(type);
				break;
		}
		
		if(StringUtils.isEmpty(rate)){
			return null;
		}
		
		int max_11n_mcs_rate = hiveAp.getDeviceInfo().getIntegerValue(DeviceInfo.SLA_MAX_11N_MCS_RATE);
		
		switch (type) {
		case _11n:
			if(rate.contains("mcs")){
				String rateNew = rate.replace("mcs", "");
				int rateNewInt = Integer.valueOf(rateNew);
				if(rateNewInt > max_11n_mcs_rate){
					return null;
				}
				
				if(hiveAp.is11acHiveAP() || 
						NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.6.0") >= 0){
					//from 6.1r6 all 11n and 11ac device use same format.
					return NmsUtil.mcsFormatConvert(rateNewInt, 8);
				}else{
					return rate;
				}
			}else{
				return rate;
			}
		case _11ac:
		case _11b:
		case _11a:
		case _11g:
		default:
			return rate;
		}
	}
	
	public int getSuccessValue(ClientPhyMode type, int indexRad, int indexRate){
		RadioProfile radio = radioProfileList.get(indexRad);
		switch(indexRate){
			case 0:
				return radio.getSLATopSuccessPercent(type);
			case 1:
				return radio.getSLABottomSuccessPercent(type);
			default:
				return radio.getSLATopSuccessPercent(type);
		}
	}
	
	public int getUsageValue(ClientPhyMode type, int indexRad, int indexRate){
		RadioProfile radio = radioProfileList.get(indexRad);
		switch(indexRate){
			case 0:
				return radio.getSLATopUsagePercent(type);
			case 1:
				return radio.getSLABottomUsagePercent(type);
			default:
				return radio.getSLATopUsagePercent(type);
		}
	}
	
	public boolean isHighDensityEnable(int index){
		return radioProfileList.get(index).isEnableHighDensity();
	}
	
	public boolean isTxRateHigh(int index){
		return radioProfileList.get(index).getHighDensityTransmitRate() == RadioProfile.HIGH_DENSITY_TRANSMIT_RATE_HIGH;
	}
	
	public boolean isTxRateLow(int index){
		return radioProfileList.get(index).getHighDensityTransmitRate() == RadioProfile.HIGH_DENSITY_TRANSMIT_RATE_LOW;
	}
	
	public boolean isContinuousSuppressEnable(int index){
		return radioProfileList.get(index).isEnableContinuousProbe();
	}
	
	public boolean isWeakSnrSuppressEnable(int index){
		return radioProfileList.get(index).isEnableSuppress();
	}
	
	public int getWeakSnrSuppressThreshold(int index){
		return radioProfileList.get(index).getSuppressThreshold();
	}
	
	public boolean isClientLoadBalanceEnable(int index){
		return radioProfileList.get(index).isEnableClientLoadBalance();
	}
	
	public int getLoadBalanceHoldTime(int index){
		return radioProfileList.get(index).getClientHoldTime();
	}
	
	public int getLoadBalanceCuLimit(int index){
		return radioProfileList.get(index).getCuLimit();
	}
	
	public int getLoadBalanceIntLimit(int index){
		return radioProfileList.get(index).getMaxInterference();
	}
	
	public int getLoadBalanceErrorLimit(int index){
		return radioProfileList.get(index).getCrcErrorLimit();
	}
	
	public boolean isSafetyNetEnable(int index){
		return radioProfileList.get(index).isEnableSafetyNet();
	}
	
	public int getSafetyNetTimeout(int index){
		return radioProfileList.get(index).getSafetyNetTimeout();
	}
	
	public boolean isBroadcastProbeEnable(int index){
		return radioProfileList.get(index).isEnableBroadcastProbe();
	}
	
	public boolean isBandSteeringEnable(int index){
		//return radioProfileList.get(index).isEnableBandSteering();
		return radiowifi0.isEnableBandSteering();
	}
	
	public boolean isBroadcastOuiEnable(int index) {
		return radioProfileList.get(index).isEnableSupressBPRByOUI();
	}
	
	public List<String> getBroadcastOuis(int index) {
		List<String> list = new ArrayList<String>();
		Set<MacOrOui> set = radioProfileList.get(index).getSupressBprOUIs();
		if (set != null) {
			for (MacOrOui single : set) {
				String oui = null;
				try {
					oui = CLICommonFunc.getMacAddressOrOui(single, hiveAp).getMacEntry();
					oui = CLICommonFunc.transFormMacAddrOrOui(oui);
				} catch (CreateXMLException e) {
					log.error("RadioProfileImpl.getBroadcastOuis() error", e);
				}
				list.add(oui);
			}
		}
		return list;
	}
	
	public boolean isAllChannelsModelEnable(int index){
		return radioProfileList.get(index).isUseDefaultChannelModel();
	}
	
//	public boolean isCofigInterSwitch(int index){
//		short radioMode = radioProfileList.get(index).getRadioMode();
//		return radioMode == RadioProfile.RADIO_PROFILE_MODE_NG || 
//			radioMode == RadioProfile.RADIO_PROFILE_MODE_BG;
//	}
	
//	public int getSwitchCuThreshold(int index){
//		return radioProfileList.get(index).getCuThreshold();
//	}
	
	public int getSwitchIuThreshold(int index){
		return radioProfileList.get(index).getIuThreshold();
	}
	
	public int getSwitchCrcErrThreshold(int index){
		return radioProfileList.get(index).getCrcChannelThr();
	}
	
	public boolean isConfigInterSwitchEnable(int index){
		return radioProfileList.get(index).isChannelSwitch() && radioProfileList.get(index).isStationConnect();
	}
	
	public boolean isConfigInterSwitchDisable(int index){
		return !radioProfileList.get(index).isChannelSwitch();
	}
	
	public boolean isConfigInterSwitchNoStation(int index){
		return radioProfileList.get(index).isChannelSwitch() && !radioProfileList.get(index).isStationConnect();
	}
	
	public boolean isConfigAllChannelsModel(int index){
		short mode = radioProfileList.get(index).getRadioMode();
		return mode == RadioProfile.RADIO_PROFILE_MODE_NG ||  mode == RadioProfile.RADIO_PROFILE_MODE_BG;
	}
	
	public boolean isEnableDetectBssidSpoofing(int index){
		return radioProfileList.get(index).getEnabledBssidSpoof();
	}

	public BandSteeringModeValue getBandSteeringModeVlaue(int index) {
		BandSteeringModeValue modevalue = null;
		switch(radiowifi0.getBandSteeringMode()){
			case RadioProfile.BAND_STEERING_MODE_PREFER5G:
				modevalue = BandSteeringModeValue.PREFER_5_G;
				break;
			case RadioProfile.BAND_STEERING_MODE_BALANCEBAND:
				modevalue = BandSteeringModeValue.BALANCE_BAND;
				break;
			case RadioProfile.BAND_STEERING_MODE_FORCE5G:
				modevalue = BandSteeringModeValue.FORCE_5_G;
				break;
		}
		return modevalue;
		
	}

	public boolean isConfigBandSteeringPrefer5G(int index) {
		short mode = radiowifi0.getBandSteeringMode();
		return mode == RadioProfile.BAND_STEERING_MODE_PREFER5G;
	}

	public boolean isConfigBandSteeringBalanceBand(int index) {
		short mode = radiowifi0.getBandSteeringMode();
		return mode == RadioProfile.BAND_STEERING_MODE_BALANCEBAND;
	}

	public int getBandSteeringMinimumRatio(int index) {
		return radiowifi0.getMinimumRatio();
	}

	public int getBandSteeringLimitNumber(int index) {
		return radiowifi0.getLimitNumber();
	}

	public ClientLoadBalanceModeValue getClientLoadBalanceModeValue(int index) {
		ClientLoadBalanceModeValue modevalue = null;
		switch(radioProfileList.get(index).getLoadBalancingMode()){
			case RadioProfile.LOAD_BALANCE_MODE_AIRTIME_BASED:
				modevalue = ClientLoadBalanceModeValue.AIRTIME;
				break;
			case RadioProfile.LOAD_BALANCE_MODE_STATION_NUMBER:
				modevalue = ClientLoadBalanceModeValue.STA_NUM;
				break;
		}
		return modevalue;
	}

	public boolean isConfigClientLoadBalanceMode(int index) {
		short mode = radioProfileList.get(index).getLoadBalancingMode();
		return mode == RadioProfile.LOAD_BALANCE_MODE_AIRTIME_BASED;
	}
	
	public boolean isConfigBenchmark(int index){
		short mode = radioProfileList.get(index).getRadioMode();
		return mode != RadioProfile.RADIO_PROFILE_MODE_NG;
	}
	
	public int getNeighborLoadQueryInterval(int index){
		return radioProfileList.get(index).getQueryInterval();
	}
	
	public boolean isEnablePresence(int index){
		return (presenceEnable && radioProfileList.get(index).isEnabledPresence());
	}
	
	public int getPresenceTrapInterval(int index){
		return radioProfileList.get(index).getTrapInterval();
	}
	
	public int getPresenceAgingTime(int index){
		return radioProfileList.get(index).getAgingTime();
	}
	
	public int getPresenceAggrInterval(int index) {
		return radioProfileList.get(index).getAggrInterval();
	}
	
	public String getSensorChannelListValue(int index) {
		if (radioProfileList.get(index).isScanAllChannel()) {
			return "all";
		}
		return radioProfileList.get(index).getScanChannels();
	}
	
	public int getSensorDwellTime(int index) {
		return radioProfileList.get(index).getDellTime();
	}
	
	
	public boolean isEnableConnectionAlarm() {
		return hiveAp.getConfigTemplate().isEnableConnectionAlarm();
	}
	
	public int getChannelUtilizatioThreshold() {
		return hiveAp.getConfigTemplate().getChannelUtilizationThreshold();
	}
	
	public int getChannelUtilizatioInterval() {
		return hiveAp.getConfigTemplate().getChannelUtilizationInterval();
	}
	
//	public String getChannelOffset(int index){
//		short channelWidth = radioProfileList.get(index).getChannelWidth();
//		switch(channelWidth){
//			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A :
//				return "0";
//			case RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40B :
//				return "1";
//			default:
//				return "auto";
//		}
//	}

	public boolean isFrameburstEnabled(int index) {
		return radioProfileList.get(index).isEnableFrameburst();
	}

	public boolean isVHTEnabled(int index) {
		short mode = radioProfileList.get(index).getRadioMode();
	    if(mode == RadioProfile.RADIO_PROFILE_MODE_NG){
	    	return radioProfileList.get(index).isEnableVHT();
	    }
	    
	    return false;
	}
	
	public boolean isTxBeamformingEnabled(int index){
		return radioProfileList.get(index).getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC 
				&& radioProfileList.get(index).isEnabledTxbeamforming();
	}

	public boolean isTxBeamformingExplicitMode(int index) {
		short mode = radioProfileList.get(index).getTxBeamformingMode();
		if(RadioProfile.TXBEAMFORMING_MODE_EXPLICIT == mode){
			return true;
		}
		
		return false;
	}
}
