package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.HiveProfileInt;
import com.ah.be.config.create.source.SsidProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.network.DosParams;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.IdsPolicy;
import com.ah.bo.network.ServiceFilter;
import com.ah.util.MgrUtil;

public class HiveProfileImpl implements HiveProfileInt {

	private final HiveAp hiveAp;
	private HiveProfile hiveProfile;
	private final ServiceFilter manageStatus;
	private final DosPrevention hiveDos;
	private final DosPrevention stationDos;
	private IdsPolicy idsPolicy;

	public HiveProfileImpl(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
		hiveProfile = hiveAp.getConfigTemplate().getHiveProfile();
		stationDos = hiveProfile.getStationDos();
		hiveDos = hiveProfile.getHiveDos();

		manageStatus = hiveAp.getConfigTemplate().getWireServiceFilter();
		idsPolicy = hiveAp.getConfigTemplate().getIdsPolicy();
	}
	
	public String getHiveGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.hiveProfiles");
	}
	
	public String getServiceFilterGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.managementFilters");
	}
	
	public String getServiceFilterName(){
		if(manageStatus != null){
			return manageStatus.getFilterName();
		}else{
			return null;
		}
	}

	public String getApVersion() {
		return hiveAp.getSoftVer();
	}

	public String getHiveName() {
		return hiveProfile.getHiveName();
	}

	public String getHiveUpdateTime() {
		List<Object> hiveTimeObj = new ArrayList<Object>();
		hiveTimeObj.add(hiveAp);
		hiveTimeObj.add(hiveProfile);
		hiveTimeObj.add(manageStatus);
		hiveTimeObj.add(hiveDos);
		hiveTimeObj.add(stationDos);
		if (hiveProfile.getMacFilters() != null) {
			hiveTimeObj.addAll(hiveProfile.getMacFilters());
		}
		return CLICommonFunc.getLastUpdateTime(hiveTimeObj);
	}

	public String getHiveSecurityUpdateTime() {
		List<Object> securityTime = new ArrayList<Object>();
		securityTime.add(hiveDos);
		securityTime.add(stationDos);
		if (hiveProfile.getMacFilters() != null) {
			securityTime.addAll(hiveProfile.getMacFilters());
		}
		return CLICommonFunc.getLastUpdateTime(securityTime);
	}

	public String getHiveWlanUpdateTime() {
		List<Object> wlanTime = new ArrayList<Object>();
		wlanTime.add(hiveDos);
		wlanTime.add(stationDos);
		return CLICommonFunc.getLastUpdateTime(wlanTime);
	}

	public String getHiveLevelUpdateTime() {
		List<Object> hiveLevel = new ArrayList<Object>();
		hiveLevel.add(hiveDos);
		return CLICommonFunc.getLastUpdateTime(hiveLevel);
	}

	public String getStationLevelUpdateTime() {
		List<Object> stationLevel = new ArrayList<Object>();
		stationLevel.add(stationDos);
		return CLICommonFunc.getLastUpdateTime(stationLevel);
	}

	public boolean isConfigHiveProfile() {
		return hiveProfile != null
				&& !hiveProfile.getDefaultFlag();
	}

	public int getHiveFragThreshold() {
		return hiveProfile.getFragThreshold();
	}

//	public boolean isConfigNativeVlan() {
//		if (CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())) {
//			return true;
//		} else if (CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())) {
//			return false;
//		} else {
//			return false;
//		}
//	}

	public int getNativeVlanId() throws CreateXMLException {
		int nativeVlan = hiveAp.getNativeVlan();
		if(nativeVlan > 0){
			return nativeVlan;
		}else{
			return CLICommonFunc.getVlan(hiveAp.getConfigTemplate().getVlanNative(), hiveAp).getVlanId();
		}
	}

	public int getRtsThreshold() {
		return hiveProfile.getRtsThreshold();
	}

	public boolean isConfigurePassword() {
		return hiveProfile.getHivePassword() != null
				&& !hiveProfile.getHivePassword().equals("");
	}

	public String getPassword() {
		return hiveProfile.getHivePassword();
	}

	public boolean isConfigureManage() {
		return manageStatus != null;
	}

//	public boolean isConfigureSecurity() {
//		return this.isConfigureMacFilter() || this.isConfigureWlan();
//	}

	public boolean isEnableManageWithType(String manageType) {
		boolean status = false;
		if (HiveProfileInt.MANAGE_PING.equals(manageType)) {
			status = manageStatus.getEnablePing();
		} else if (HiveProfileInt.MANAGE_SNMP.equals(manageType)) {
			status = manageStatus.getEnableSNMP();
		} else if (HiveProfileInt.MANAGE_SSH.equals(manageType)) {
			status = manageStatus.getEnableSSH();
		} else if (HiveProfileInt.MANAGE_TELNET.equals(manageType)) {
			status = manageStatus.getEnableTelnet();
		}
		return status;
	}

//	public boolean isConfigureMacFilter() {
//		return hiveProfile.getMacFilters() != null
//				&& hiveProfile.getMacFilters().size() > 0;
//	}

	public String getMacFilterName() {
		String hiveName = hiveProfile.getHiveName();
//		hiveName.replaceAll(" ", "");
//		hiveName.replaceAll("\\?", "");
		return hiveName;
	}

	public boolean isConfigureWlan() {
		return this.isConfigureHiveLevel() || this.isConfigureStationLevel();
	}

	public boolean isConfigureHiveLevel() {
		return hiveDos != null;
	}

	public boolean isConfigureStationLevel() {
		return stationDos != null;
	}

	private String getFrameTypeKey(SsidProfileInt.FrameType type) {
		String key = "";
		if (SsidProfileInt.FrameType.assoc_req == type) {
			key = DosParams.FrameType.ASSOC_REQ.name();
		} else if (SsidProfileInt.FrameType.assoc_resp == type) {
			key = DosParams.FrameType.ASSOC_RESP.name();
		} else if (SsidProfileInt.FrameType.auth == type) {
			key = DosParams.FrameType.AUTH.name();
		} else if (SsidProfileInt.FrameType.deauth == type) {
			key = DosParams.FrameType.DEAUTH.name();
		} else if (SsidProfileInt.FrameType.disassoc == type) {
			key = DosParams.FrameType.DISASSOC.name();
		} else if (SsidProfileInt.FrameType.eapol == type) {
			key = DosParams.FrameType.EAPOL.name();
		} else if (SsidProfileInt.FrameType.probe_req == type) {
			key = DosParams.FrameType.PROBE_REQ.name();
		} else if (SsidProfileInt.FrameType.probe_resp == type) {
			key = DosParams.FrameType.PROBE_RESP.name();
		}
		return key;
	}

	public boolean isEnableHiveLevelWithType(SsidProfileInt.FrameType hiveType) {
		String key = this.getFrameTypeKey(hiveType);
		return hiveDos.getDosParamsMap().get(key).isEnabled();
	}

	public int getThresholdHiveLevelWithType(SsidProfileInt.FrameType hiveType) {
		String key = this.getFrameTypeKey(hiveType);
		return hiveDos.getDosParamsMap().get(key).getAlarmThreshold();
	}

	public int getAlarmHiveLevelWithType(SsidProfileInt.FrameType hiveType) {
		String key = this.getFrameTypeKey(hiveType);
		return hiveDos.getDosParamsMap().get(key).getAlarmInterval();
	}

	public boolean isEnableHiveStationWithType(SsidProfileInt.FrameType hiveType) {
		String key = this.getFrameTypeKey(hiveType);
		return stationDos.getDosParamsMap().get(key).isEnabled();
	}

	public int getThresholdHiveStationWithType(SsidProfileInt.FrameType hiveType) {
		String key = this.getFrameTypeKey(hiveType);
		return stationDos.getDosParamsMap().get(key).getAlarmThreshold();
	}

	public int getAlarmHiveStationWithType(SsidProfileInt.FrameType hiveType) {
		String key = this.getFrameTypeKey(hiveType);
		return stationDos.getDosParamsMap().get(key).getAlarmInterval();
	}

	public String getBanHiveStationWithType(SsidProfileInt.FrameType hiveType) {
		String key = this.getFrameTypeKey(hiveType);
		int actionTime = stationDos.getDosParamsMap().get(key).getDosActionTime();
		if(actionTime < 0){
			return "forever";
		}else{
			return String.valueOf(actionTime);
		}
	}
	
	public boolean isConfigNeighbor(){
		return hiveProfile.getEnabledThreshold();
//		if(CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())){
//			return false;
//		}else if(CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())){
//			return hiveProfile.getEnabledThreshold();
//		}else{
//			return false;
//		}
	}
	
	public String getConnectingThreshold(){
		short connectionThreshold = hiveProfile.getConnectionThreshold();
		String resValue;
		
		switch(connectionThreshold){
			case -85 :
				resValue = ConnectingThreshold.low.name();
				break;
			case -80 :
				resValue = ConnectingThreshold.medium.name();
				break;
			case -75 :
				resValue = ConnectingThreshold.high.name();
				break;
			default:
				resValue = String.valueOf(connectionThreshold);
		}
		return resValue;
	}
	
	public int getPollingInterval(){
		return hiveProfile.getPollingInterval();
	}
	
	public boolean isConfigWlanIdp(){
		return idsPolicy != null;
	}
	
	public int getMaxMitigatorNum(){
		return idsPolicy.getDetectorAps();
	}
	
	public boolean isMitigationModeAuto(){
		return idsPolicy.getMitigationMode() == IdsPolicy.MITIGATION_MODE_AUTO;
	}
	
	public boolean isMitigationModeSemiAuto(){
		return idsPolicy.getMitigationMode() == IdsPolicy.MITIGATION_MODE_SEMIAUTO;
	}
	
	public boolean isMitigationModeManual(){
		return idsPolicy.getMitigationMode() == IdsPolicy.MITIGATION_MODE_MANUAL;
	}
	
	public boolean isConfigIdpInNetAp(){
		return idsPolicy.getMitigationMode() == IdsPolicy.MITIGATION_MODE_AUTO;
	}
	
	public boolean isEnableIdpInNetAp(){
		return idsPolicy.isInSameNetwork();
	}
}