package com.ah.be.config.create;

import java.util.List;
import java.util.ArrayList;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.HiveProfileInt;
import com.ah.be.config.create.source.SsidProfileInt;
import com.ah.xml.be.config.*;

/**
 * 
 * @author zhang
 *
 */
public class CreateHiveTree {
	
	private HiveProfileInt hiveObjImpl;
	private HiveObj hiveObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> hiveChildList_1 = new ArrayList<Object>();
	private List<Object> hiveChildList_2 = new ArrayList<Object>();
	private List<Object> hiveChildList_3 = new ArrayList<Object>();
	private List<Object> hiveChildList_4 = new ArrayList<Object>();
	private List<Object> hiveChildList_5 = new ArrayList<Object>();
	private List<Object> hiveChildList_6 = new ArrayList<Object>();
	
	public CreateHiveTree (HiveProfileInt hiveObjImpl, GenerateXMLDebug oDebug) {
		this.hiveObjImpl = hiveObjImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		oDebug.debug("/configuration", 
				"hive", GenerateXMLDebug.CONFIG_ELEMENT,
				null, null);
		if(hiveObjImpl.isConfigHiveProfile()){
			hiveObj = new HiveObj();
			generateHiveLevel_1();
		}
	}
	
	public HiveObj getHiveObj(){
		return hiveObj;
	}

	public void generateHiveLevel_1() throws Exception{
		
		/** attribute: name */
		oDebug.debug("/configuration", 
				"hive", GenerateXMLDebug.SET_NAME,
				hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
		hiveObj.setName(hiveObjImpl.getHiveName());
		
		/** attribute: operation */
		hiveObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: updateTime */
		hiveObj.setUpdateTime(hiveObjImpl.getHiveUpdateTime());
		
		/** element: <cr> */
		hiveObj.setCr("");
		
		/** element: <hive>.<frag-threshold> */
		oDebug.debug("/configuration/hive", 
				"frag-threshold", GenerateXMLDebug.SET_VALUE,
				hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
		Object[][] fragThresholdParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getHiveFragThreshold()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		hiveObj.setFragThreshold(
				(HiveObj.FragThreshold)CLICommonFunc.createObjectWithName(HiveObj.FragThreshold.class, fragThresholdParm)
		);
		
		/** element: <hive>.<native-vlan> */
		oDebug.debug("/configuration/hive", 
				"native-vlan", GenerateXMLDebug.SET_VALUE,
				hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
		Object[][] nativeVlanParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getNativeVlanId()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		hiveObj.setNativeVlan(
				(HiveObj.NativeVlan)CLICommonFunc.createObjectWithName(HiveObj.NativeVlan.class, nativeVlanParm)
		);
		
		/** element: <hive>.<rts-threshold> */
		oDebug.debug("/configuration/hive", 
				"rts-threshold", GenerateXMLDebug.SET_VALUE,
				hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
		Object[][] rtsThresholdParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getRtsThreshold()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		hiveObj.setRtsThreshold(
				(HiveObj.RtsThreshold)CLICommonFunc.createObjectWithName(HiveObj.RtsThreshold.class, rtsThresholdParm)
		);
		
		/** element: <hive>.<password> */
		oDebug.debug("/configuration/hive", 
				"password", GenerateXMLDebug.CONFIG_ELEMENT,
				hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
		if(hiveObjImpl.isConfigurePassword()){
			
			oDebug.debug("/configuration/hive", 
					"password", GenerateXMLDebug.SET_VALUE,
					hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
			hiveObj.setPassword(
					CLICommonFunc.createAhEncryptedStringAct(hiveObjImpl.getPassword(), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <hive>.<manage> */
		oDebug.debug("/configuration/hive", 
				"manage", GenerateXMLDebug.CONFIG_ELEMENT,
				hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
		if(hiveObjImpl.isConfigureManage()){
			AhManage manageObj = new AhManage();
			hiveChildList_1.add(manageObj);
			hiveObj.setManage(manageObj);
		}
		
		/** element: <hive>.<security> */
		oDebug.debug("/configuration/hive", 
				"security", GenerateXMLDebug.CONFIG_ELEMENT,
				hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
//		if(hiveObjImpl.isConfigureSecurity()){
			HiveObj.Security securityObj = new HiveObj.Security();
			hiveChildList_1.add(securityObj);
			hiveObj.setSecurity(securityObj);
//		}
		
		/** element: <hive>.<neighbor> */
		oDebug.debug("/configuration/hive", 
				"neighbor", GenerateXMLDebug.CONFIG_ELEMENT,
				hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
		if(hiveObjImpl.isConfigNeighbor()){
			HiveObj.Neighbor neighborObj = new HiveObj.Neighbor();
			hiveChildList_1.add(neighborObj);
			hiveObj.setNeighbor(neighborObj);
		}
		
		/** element: <hive>.<wlan-idp> */
		oDebug.debug("/configuration/hive", 
				"wlan-idp", GenerateXMLDebug.CONFIG_ELEMENT,
				hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
		if(hiveObjImpl.isConfigWlanIdp()){
			HiveWlanIdp wlanIdpObj = new HiveWlanIdp();
			hiveChildList_1.add(wlanIdpObj);
			hiveObj.setWlanIdp(wlanIdpObj);
		}
		
		generateHiveLevel_2();
	}
	
	private void generateHiveLevel_2() throws Exception {
		/**
		 * <hive>.<manage>		AhManage
		 * <hive>.<security>	HiveObj.Security
		 * <hive>.<neighbor>	HiveObj.Neighbor
		 * <hive>.<wlan-idp>	HiveWlanIdp
		 */
		
		for(Object childObj : hiveChildList_1){
			
			/** element: <hive>.<manage> */
			if(childObj instanceof AhManage){
				AhManage manageObj = (AhManage)childObj;
				
				/** element: <hive>.<manage>.<SNMP> */
				oDebug.debug("/configuration/hive/manage", 
						"SNMP", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getServiceFilterGuiName(), hiveObjImpl.getServiceFilterName());
				manageObj.setSNMP(
						CLICommonFunc.getAhOnlyAct(hiveObjImpl.isEnableManageWithType(HiveProfileInt.MANAGE_SNMP))
				);
				
				/** element: <hive>.<manage>.<SSH> */
				oDebug.debug("/configuration/hive/manage", 
						"SSH", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getServiceFilterGuiName(), hiveObjImpl.getServiceFilterName());
				manageObj.setSSH(
						CLICommonFunc.getAhOnlyAct(hiveObjImpl.isEnableManageWithType(HiveProfileInt.MANAGE_SSH))
				);
				
				/** element: <hive>.<manage>.<Telnet> */
				oDebug.debug("/configuration/hive/manage", 
						"Telnet", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getServiceFilterGuiName(), hiveObjImpl.getServiceFilterName());
				manageObj.setTelnet(
						CLICommonFunc.getAhOnlyAct(hiveObjImpl.isEnableManageWithType(HiveProfileInt.MANAGE_TELNET))
				);
				
				/** element: <hive>.<manage>.<ping> */
				oDebug.debug("/configuration/hive/manage", 
						"ping", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getServiceFilterGuiName(), hiveObjImpl.getServiceFilterName());
				manageObj.setPing(
						CLICommonFunc.getAhOnlyAct(hiveObjImpl.isEnableManageWithType(HiveProfileInt.MANAGE_PING))
				);
			}
			
			/** element: <hive>.<security> */
			if(childObj instanceof HiveObj.Security){
				HiveObj.Security securityObj = (HiveObj.Security)childObj;
				
				/** attribute: updateTime */
				securityObj.setUpdateTime(hiveObjImpl.getHiveSecurityUpdateTime());
				
				/** element: <hive>.<security>.<mac-filter> */
				oDebug.debug("/configuration/hive/security", 
						"mac-filter", GenerateXMLDebug.CONFIG_ELEMENT,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
//				if(hiveObjImpl.isConfigureMacFilter()){
					
				oDebug.debug("/configuration/hive/security", 
						"mac-filter", GenerateXMLDebug.SET_NAME,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				securityObj.setMacFilter(
						CLICommonFunc.createAhNameActValue(hiveObjImpl.getMacFilterName(), CLICommonFunc.getYesDefault())
				);
//				}
				
				/** element: <hive>.<security>.<wlan> */
				oDebug.debug("/configuration/hive/security", 
						"wlan", GenerateXMLDebug.CONFIG_ELEMENT,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				if(hiveObjImpl.isConfigureWlan()){
					HiveObj.Security.Wlan wlanObj = new HiveObj.Security.Wlan();
					hiveChildList_2.add(wlanObj);
					securityObj.setWlan(wlanObj);
				}
			}
			
			/** element: <hive>.<neighbor> */
			if(childObj instanceof HiveObj.Neighbor){
				HiveObj.Neighbor neighborObj = (HiveObj.Neighbor)childObj;
				
				/** element: <hive>.<neighbor>.<connecting-threshold> */
				HiveObj.Neighbor.ConnectingThreshold connectingThresholdObj = new HiveObj.Neighbor.ConnectingThreshold();
				hiveChildList_2.add(connectingThresholdObj);
				neighborObj.setConnectingThreshold(connectingThresholdObj);
			}
			
			/** element: <hive>.<wlan-idp> */
			if(childObj instanceof HiveWlanIdp){
				HiveWlanIdp wlanIdpObj = (HiveWlanIdp)childObj;
				
				/** element: <hive>.<wlan-idp>.<max-mitigator-num> */
				Object[][] maxNumParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getMaxMitigatorNum()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				wlanIdpObj.setMaxMitigatorNum((WlanIdpMaxMitigatorNum)CLICommonFunc.createObjectWithName(
						WlanIdpMaxMitigatorNum.class, maxNumParm));
				
				/** element: <hive>.<wlan-idp>.<mitigation-mode> */
				WlanIdpMitigationMode mitiMode = new WlanIdpMitigationMode();
				hiveChildList_2.add(mitiMode);
				wlanIdpObj.setMitigationMode(mitiMode);
				
				/** element: <hive>.<wlan-idp>.<in-net-ap> */
				if(hiveObjImpl.isConfigIdpInNetAp()){
					oDebug.debug("/configuration/hive/wlan-idp", 
							"in-net-ap", GenerateXMLDebug.SET_OPERATION,
							hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
					wlanIdpObj.setInNetAp(CLICommonFunc.getAhOnlyAct(hiveObjImpl.isEnableIdpInNetAp()));
				}
			}
		}
		
		generateHiveLevel_3();
	}
	
	private void generateHiveLevel_3() throws Exception {
		
		/**
		 * <hive>.<security>.<wlan>							HiveObj.Security.Wlan
		 * <hive>.<neighbor>.<connecting-threshold>			HiveObj.Neighbor.ConnectingThreshold
		 * <hive>.<wlan-idp>.<mitigation-mode>				WlanIdpMitigationMode
		 */
		
		for(Object childObj : hiveChildList_2){
			
			/** element: <hive>.<security>.<wlan> */
			if(childObj instanceof HiveObj.Security.Wlan){
				HiveObj.Security.Wlan wlanObj = (HiveObj.Security.Wlan)childObj;
				
				/** attribute: updateTime */
				wlanObj.setUpdateTime(hiveObjImpl.getHiveWlanUpdateTime());
				
				HiveWlanDos dosObj = new HiveWlanDos();
				hiveChildList_3.add(dosObj);
				wlanObj.setDos(dosObj);
			}
			
			/** element: <hive>.<neighbor>.<connecting-threshold> */
			if(childObj instanceof HiveObj.Neighbor.ConnectingThreshold){
				HiveObj.Neighbor.ConnectingThreshold connectingThresholdObj = (HiveObj.Neighbor.ConnectingThreshold)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/hive/neighbor", 
						"connecting-threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				connectingThresholdObj.setValue(hiveObjImpl.getConnectingThreshold());
				
				/** attribute: operation */
				connectingThresholdObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <hive>.<neighbor>.<connecting-threshold>.<pollingInterval> */
				oDebug.debug("/configuration/hive/neighbor/connecting-threshold", 
						"pollingInterval", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] intervalParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getPollingInterval()}
				};
				connectingThresholdObj.setPollingInterval(
						(ConnectPollingInterval)CLICommonFunc.createObjectWithName(ConnectPollingInterval.class, intervalParm)
				);
				
			}
			
			/** element: <hive>.<wlan-idp>.<mitigation-mode> */
			if(childObj instanceof WlanIdpMitigationMode){
				WlanIdpMitigationMode mitiMode = (WlanIdpMitigationMode)childObj;
				
				/** attribute: operation */
				mitiMode.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <hive>.<wlan-idp>.<mitigation-mode>.<automatic> */
				oDebug.debug("/configuration/hive/wlan-idp/mitigation-mode", 
						"automatic", GenerateXMLDebug.CONFIG_ELEMENT,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				if(hiveObjImpl.isMitigationModeAuto()){
					mitiMode.setAutomatic(new MitigationModeAuto());
				}
				
				/** element: <hive>.<wlan-idp>.<mitigation-mode>.<semi-automatic> */
				oDebug.debug("/configuration/hive/wlan-idp/mitigation-mode", 
						"semi-automatic", GenerateXMLDebug.CONFIG_ELEMENT,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				if(hiveObjImpl.isMitigationModeSemiAuto()){
					MitigationModeAuto mgtigationObj = new MitigationModeAuto();
//					hiveChildList_3.add(mgtigationObj);
					mitiMode.setSemiAutomatic(mgtigationObj);
				}
				
				/** element: <hive>.<wlan-idp>.<mitigation-mode>.<manual> */
				oDebug.debug("/configuration/hive/wlan-idp/mitigation-mode", 
						"manual", GenerateXMLDebug.CONFIG_ELEMENT,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				if(hiveObjImpl.isMitigationModeManual()){
					mitiMode.setManual("");
				}
			}
		}
		generateHiveLevel_4();
	}
	
	private void generateHiveLevel_4() throws Exception {
		
		/**
		 * <hive>.<security>.<wlan>.<dos>		HiveWlanDos
		 */
		
		for(Object childObj : hiveChildList_3){
			
			/** element: <hive>.<security>.<wlan>.<dos> */
			if(childObj instanceof HiveWlanDos){
				HiveWlanDos dosObj = (HiveWlanDos)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level> */
				oDebug.debug("/configuration/hive/security/wlan/dos", 
						"hive-level", GenerateXMLDebug.CONFIG_ELEMENT,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				if(hiveObjImpl.isConfigureHiveLevel()){
					DosSsidLevel hiveLevelObj = new DosSsidLevel();
					hiveChildList_4.add(hiveLevelObj);
					dosObj.setHiveLevel(hiveLevelObj);
				}
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level> */
				oDebug.debug("/configuration/hive/security/wlan/dos", 
						"station-level", GenerateXMLDebug.CONFIG_ELEMENT,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				if(hiveObjImpl.isConfigureStationLevel()){
					DosStationLevel stationLevelObj = new DosStationLevel();
					hiveChildList_4.add(stationLevelObj);
					dosObj.setStationLevel(stationLevelObj);
				}
				
			}
			
//			/** element: <hive>.<wlan-idp>.<mitigation-mode>.<semi-automatic> */
//			if(childObj instanceof MitigationModeAuto){
//				MitigationModeAuto mitigationObj = (MitigationModeAuto)childObj;
//				
//				/** element: <hive>.<wlan-idp>.<mitigation-mode>.<semi-automatic>.<action> */
//				MitigationModeAutoAction actionObj = new MitigationModeAutoAction();
//				hiveChildList_4.add(actionObj);
//				mitigationObj.setAction(actionObj);
//				
//			}
		}
		generateHiveLevel_5();
	}
	
	private void generateHiveLevel_5() throws Exception {
		/**
		 * <hive>.<security>.<wlan>.<dos>.<hive-level>			DosSsidLevel
		 * <hive>.<security>.<wlan>.<dos>.<station-level>		DosStationLevel
		 */
		for(Object childObj : hiveChildList_4){
			
			/** element: <hive>.<security>.<wlan>.<dos>.<hive-level> */
			if(childObj instanceof DosSsidLevel){
				DosSsidLevel hiveLevelObj = (DosSsidLevel)childObj;
				
				/** attribute: updatTime */
				hiveLevelObj.setUpdateTime(hiveObjImpl.getHiveLevelUpdateTime());
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type> */
				DosSsidLevel.FrameType hiveFrameObj = new DosSsidLevel.FrameType();
				hiveChildList_5.add(hiveFrameObj);
				hiveLevelObj.setFrameType(hiveFrameObj);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<station-level> */
			if(childObj instanceof DosStationLevel){
				DosStationLevel stationLevelObj = (DosStationLevel)childObj;
				
				/** attribute: updatTime */
				stationLevelObj.setUpdateTime(hiveObjImpl.getStationLevelUpdateTime());
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type> */
				DosStationLevel.FrameType stationFrameObj = new DosStationLevel.FrameType();
				hiveChildList_5.add(stationFrameObj);
				stationLevelObj.setFrameType(stationFrameObj);
			}
			
//			/** element: <hive>.<wlan-idp>.<mitigation-mode>.<semi-automatic>.<action> */
//			if(childObj instanceof MitigationModeAutoAction){
//				MitigationModeAutoAction actionObj = (MitigationModeAutoAction)childObj;
//				
//				/** element: <hive>.<wlan-idp>.<mitigation-mode>.<semi-automatic>.<action>.<mitigate> */
//				if(hiveObjImpl.isConfigSemiAutoMitigate()){
//					oDebug.debug("/configuration/hive/wlan-idp/mitigation-mode/semi-automatic/action", 
//							"mitigate", GenerateXMLDebug.CONFIG_ELEMENT,
//							hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
//					actionObj.setMitigate("");
//				}
//				
//				/** element: <hive>.<wlan-idp>.<mitigation-mode>.<semi-automatic>.<action>.<report> */
//				if(hiveObjImpl.isConfigSemiAutoReport()){
//					oDebug.debug("/configuration/hive/wlan-idp/mitigation-mode/semi-automatic/action", 
//							"report", GenerateXMLDebug.CONFIG_ELEMENT,
//							hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
//					actionObj.setReport("");
//				}
//				
//				/** attribute: operation */
//				actionObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
//			}
			
		}
		generateHiveLevel_6();
	}
	
	private void generateHiveLevel_6() throws Exception {
		/**
		 * <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>		DosSsidLevel.FrameType
		 * <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>		DosStationLevel.FrameType
		 */
		
		for(Object childObj : hiveChildList_5){
			
			/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type> */
			if(childObj instanceof DosSsidLevel.FrameType){
				DosSsidLevel.FrameType hiveFrameObj = (DosSsidLevel.FrameType)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<probe-req> */
				DosSsidLevelProbeReq probeReqObj = new DosSsidLevelProbeReq();
				hiveChildList_6.add(probeReqObj);
				hiveFrameObj.setProbeReq(probeReqObj);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<probe-resp> */
				DosSsidLevelProbeResp probeRespObj = new DosSsidLevelProbeResp();
				hiveChildList_6.add(probeRespObj);
				hiveFrameObj.setProbeResp(probeRespObj);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<assoc-req> */
				DosSsidLevelAssocReq assocReqObj = new DosSsidLevelAssocReq();
				hiveChildList_6.add(assocReqObj);
				hiveFrameObj.setAssocReq(assocReqObj);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<assoc-resp> */
				DosSsidLevelAssocResp assocRespObj = new DosSsidLevelAssocResp();
				hiveChildList_6.add(assocRespObj);
				hiveFrameObj.setAssocResp(assocRespObj);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<auth> */
				DosSsidLevelAuth authObj = new DosSsidLevelAuth();
				hiveChildList_6.add(authObj);
				hiveFrameObj.setAuth(authObj);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<deauth> */
				DosSsidLevelDeauth deauthObj = new DosSsidLevelDeauth();
				hiveChildList_6.add(deauthObj);
				hiveFrameObj.setDeauth(deauthObj);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<disassoc> */
				DosSsidLevelDisassoc disassocObj = new DosSsidLevelDisassoc();
				hiveChildList_6.add(disassocObj);
				hiveFrameObj.setDisassoc(disassocObj);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<eapol> */
				DosSsidLevelEapol eapolObj = new DosSsidLevelEapol();
				hiveChildList_6.add(eapolObj);
				hiveFrameObj.setEapol(eapolObj);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type> */
			if(childObj instanceof DosStationLevel.FrameType){
				DosStationLevel.FrameType stationFrameObj = (DosStationLevel.FrameType)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-req> */
				DosStationLevelProbeReq probeReqObj = new DosStationLevelProbeReq();
				hiveChildList_6.add(probeReqObj);
				stationFrameObj.setProbeReq(probeReqObj);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-resp> */
				DosStationLevelProbeResp probeRespObj = new DosStationLevelProbeResp();
				hiveChildList_6.add(probeRespObj);
				stationFrameObj.setProbeResp(probeRespObj);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-req> */
				DosStationLevelAssocReq assocReqObj = new DosStationLevelAssocReq();
				hiveChildList_6.add(assocReqObj);
				stationFrameObj.setAssocReq(assocReqObj);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-resp> */
				DosStationLevelAssocResp assocRespObj = new DosStationLevelAssocResp();
				hiveChildList_6.add(assocRespObj);
				stationFrameObj.setAssocResp(assocRespObj);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<auth> */
				DosStationLevelAuth authObj = new DosStationLevelAuth();
				hiveChildList_6.add(authObj);
				stationFrameObj.setAuth(authObj);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<deauth> */
				DosStationLevelDeauth deauthObj = new DosStationLevelDeauth();
				hiveChildList_6.add(deauthObj);
				stationFrameObj.setDeauth(deauthObj);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<disassoc> */
				DosStationLevelDisassoc disassocObj = new DosStationLevelDisassoc();
				hiveChildList_6.add(disassocObj);
				stationFrameObj.setDisassoc(disassocObj);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<eapol> */
				DosStationLevelEapol eapolObj = new DosStationLevelEapol();
				hiveChildList_6.add(eapolObj);
				stationFrameObj.setEapol(eapolObj);
			}
		}
		hiveChildList_5.clear();
		generateHiveLevel_7();
	}
	
	private void generateHiveLevel_7() throws Exception{
		/**
		 * <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<probe-req>			DosSsidLevelProbeReq
		 * <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<probe-resp>			DosSsidLevelProbeResp
		 * <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<assoc-req>			DosSsidLevelAssocReq
		 * <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<assoc-resp>			DosSsidLevelAssocResp
		 * <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<auth>					DosSsidLevelAuth
		 * <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<deauth>				DosSsidLevelDeauth
		 * <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<disassoc>				DosSsidLevelDisassoc
		 * <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<eapol>				DosSsidLevelEapol
		 * 
		 * <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-req>			DosStationLevelProbeReq
		 * <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-resp>		DosStationLevelProbeResp
		 * <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-req>			DosStationLevelAssocReq
		 * <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-resp>		DosStationLevelAssocResp
		 * <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<auth>				DosStationLevelAuth
		 * <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<deauth>			DosStationLevelDeauth
		 * <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<disassoc>			DosStationLevelDisassoc
		 * <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<eapol>				DosStationLevelEapol
		 */
		
		for(Object childObj : hiveChildList_6){
			
			/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<probe-req> */
			if(childObj instanceof DosSsidLevelProbeReq){
				DosSsidLevelProbeReq probeReqObj = (DosSsidLevelProbeReq)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<probe-req>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/probe-req", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				probeReqObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveLevelWithType(SsidProfileInt.FrameType.probe_req)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<probe-req>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/probe-req", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveLevelWithType(SsidProfileInt.FrameType.probe_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				probeReqObj.setThreshold(
						(DosSsidLevelProbeReq.Threshold)CLICommonFunc.createObjectWithName(DosSsidLevelProbeReq.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<probe-req>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/probe-req", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveLevelWithType(SsidProfileInt.FrameType.probe_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				probeReqObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<probe-resp> */
			if(childObj instanceof DosSsidLevelProbeResp){
				DosSsidLevelProbeResp probeRespObj = (DosSsidLevelProbeResp)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<probe-resp>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/probe-resp", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				probeRespObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveLevelWithType(SsidProfileInt.FrameType.probe_resp)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<probe-resp>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/probe-resp", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveLevelWithType(SsidProfileInt.FrameType.probe_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				probeRespObj.setThreshold(
						(DosSsidLevelProbeResp.Threshold)CLICommonFunc.createObjectWithName(
								DosSsidLevelProbeResp.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<probe-resp>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/probe-resp", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveLevelWithType(SsidProfileInt.FrameType.probe_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				probeRespObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<assoc-req> */
			if(childObj instanceof DosSsidLevelAssocReq){
				DosSsidLevelAssocReq assocReqObj = (DosSsidLevelAssocReq)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<assoc-req>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/assoc-req", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				assocReqObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveLevelWithType(SsidProfileInt.FrameType.assoc_req)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<assoc-req>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/assoc-req", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveLevelWithType(SsidProfileInt.FrameType.assoc_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocReqObj.setThreshold(
						(DosSsidLevelAssocReq.Threshold)CLICommonFunc.createObjectWithName(DosSsidLevelAssocReq.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<assoc-req>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/assoc-req", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveLevelWithType(SsidProfileInt.FrameType.assoc_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocReqObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<assoc-resp> */
			if(childObj instanceof DosSsidLevelAssocResp){
				DosSsidLevelAssocResp assocRespObj = (DosSsidLevelAssocResp)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<assoc-resp>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/assoc-resp", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				assocRespObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveLevelWithType(SsidProfileInt.FrameType.assoc_resp)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<assoc-resp>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/assoc-resp", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveLevelWithType(SsidProfileInt.FrameType.assoc_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocRespObj.setThreshold(
						(DosSsidLevelAssocResp.Threshold)CLICommonFunc.createObjectWithName(DosSsidLevelAssocResp.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<assoc-resp>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/assoc-resp", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveLevelWithType(SsidProfileInt.FrameType.assoc_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocRespObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<auth> */
			if(childObj instanceof DosSsidLevelAuth){
				DosSsidLevelAuth authObj = (DosSsidLevelAuth)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<auth>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/auth", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				authObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveLevelWithType(SsidProfileInt.FrameType.auth)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<auth>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/auth", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveLevelWithType(SsidProfileInt.FrameType.auth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				authObj.setThreshold(
						(DosSsidLevelAuth.Threshold)CLICommonFunc.createObjectWithName(DosSsidLevelAuth.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<auth>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/auth", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveLevelWithType(SsidProfileInt.FrameType.auth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				authObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<deauth> */
			if(childObj instanceof DosSsidLevelDeauth){
				DosSsidLevelDeauth deauthObj = (DosSsidLevelDeauth)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<deauth>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/deauth", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				deauthObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveLevelWithType(SsidProfileInt.FrameType.deauth)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<deauth>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/deauth", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveLevelWithType(SsidProfileInt.FrameType.deauth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				deauthObj.setThreshold(
						(DosSsidLevelDeauth.Threshold)CLICommonFunc.createObjectWithName(DosSsidLevelDeauth.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<deauth>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/deauth", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveLevelWithType(SsidProfileInt.FrameType.deauth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				deauthObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<disassoc> */
			if(childObj instanceof DosSsidLevelDisassoc){
				DosSsidLevelDisassoc disassocObj = (DosSsidLevelDisassoc)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<disassoc>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/disassoc", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				disassocObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveLevelWithType(SsidProfileInt.FrameType.disassoc)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<disassoc>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/disassoc", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveLevelWithType(SsidProfileInt.FrameType.disassoc)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				disassocObj.setThreshold(
						(DosSsidLevelDisassoc.Threshold)CLICommonFunc.createObjectWithName(DosSsidLevelDisassoc.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<disassoc>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/disassoc", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveLevelWithType(SsidProfileInt.FrameType.disassoc)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				disassocObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<eapol> */
			if(childObj instanceof DosSsidLevelEapol){
				DosSsidLevelEapol eapolObj = (DosSsidLevelEapol)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<eapol>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/eapol", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				eapolObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveLevelWithType(SsidProfileInt.FrameType.eapol)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<eapol>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/eapol", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveLevelWithType(SsidProfileInt.FrameType.eapol)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				eapolObj.setThreshold(
						(DosSsidLevelEapol.Threshold)CLICommonFunc.createObjectWithName(DosSsidLevelEapol.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<hive-level>.<frame-type>.<eapol>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/hive-level/frame-type/eapol", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveLevelWithType(SsidProfileInt.FrameType.eapol)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				eapolObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-req> */
			if(childObj instanceof DosStationLevelProbeReq){
				DosStationLevelProbeReq probeReqObj = (DosStationLevelProbeReq)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-req>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/probe-req", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				probeReqObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveStationWithType(SsidProfileInt.FrameType.probe_req)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-req>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/probe-req", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveStationWithType(SsidProfileInt.FrameType.probe_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				probeReqObj.setThreshold(
						(DosStationLevelProbeReq.Threshold)CLICommonFunc.createObjectWithName(DosStationLevelProbeReq.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-req>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/probe-req", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveStationWithType(SsidProfileInt.FrameType.probe_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				probeReqObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-resp> */
			if(childObj instanceof DosStationLevelProbeResp){
				DosStationLevelProbeResp probeRespObj = (DosStationLevelProbeResp)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-resp>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/probe-req", 
						"cr", GenerateXMLDebug.CONFIG_ELEMENT,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				probeRespObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveStationWithType(SsidProfileInt.FrameType.probe_resp)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-resp>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/probe-req", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveStationWithType(SsidProfileInt.FrameType.probe_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				probeRespObj.setThreshold(
						(DosStationLevelProbeResp.Threshold)CLICommonFunc.createObjectWithName(DosStationLevelProbeResp.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-resp>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/probe-req", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveStationWithType(SsidProfileInt.FrameType.probe_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				probeRespObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-req> */
			if(childObj instanceof DosStationLevelAssocReq){
				DosStationLevelAssocReq assocReqObj = (DosStationLevelAssocReq)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-req>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/assoc-req", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				assocReqObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveStationWithType(SsidProfileInt.FrameType.assoc_req)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-req>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/assoc-req", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveStationWithType(SsidProfileInt.FrameType.assoc_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocReqObj.setThreshold(
						(DosStationLevelAssocReq.Threshold)CLICommonFunc.createObjectWithName(DosStationLevelAssocReq.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-req>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/assoc-req", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveStationWithType(SsidProfileInt.FrameType.assoc_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocReqObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-req>.<ban> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/assoc-req", 
						"ban", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] banParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getBanHiveStationWithType(SsidProfileInt.FrameType.assoc_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocReqObj.setBan(
						(DosBan)CLICommonFunc.createObjectWithName(DosBan.class, banParm)
				);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-resp> */
			if(childObj instanceof DosStationLevelAssocResp){
				DosStationLevelAssocResp assocRespObj = (DosStationLevelAssocResp)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-resp>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/assoc-req", 
						"cr", GenerateXMLDebug.CONFIG_ELEMENT,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				assocRespObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveStationWithType(SsidProfileInt.FrameType.assoc_resp)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-resp>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/assoc-req", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveStationWithType(SsidProfileInt.FrameType.assoc_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocRespObj.setThreshold(
						(DosStationLevelAssocResp.Threshold)CLICommonFunc.createObjectWithName(DosStationLevelAssocResp.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-resp>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/assoc-req", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveStationWithType(SsidProfileInt.FrameType.assoc_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocRespObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<auth> */
			if(childObj instanceof DosStationLevelAuth){
				DosStationLevelAuth authObj = (DosStationLevelAuth)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<auth>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/auth", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				authObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveStationWithType(SsidProfileInt.FrameType.auth)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<auth>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/auth", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveStationWithType(SsidProfileInt.FrameType.auth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				authObj.setThreshold(
						(DosStationLevelAuth.Threshold)CLICommonFunc.createObjectWithName(DosStationLevelAuth.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<auth>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/auth", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveStationWithType(SsidProfileInt.FrameType.auth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				authObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<auth>.<ban> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/auth", 
						"ban", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] banParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getBanHiveStationWithType(SsidProfileInt.FrameType.auth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				authObj.setBan(
						(DosBan)CLICommonFunc.createObjectWithName(DosBan.class, banParm)
				);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<deauth> */
			if(childObj instanceof DosStationLevelDeauth){
				DosStationLevelDeauth deauthObj = (DosStationLevelDeauth)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<deauth>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/deauth", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				deauthObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveStationWithType(SsidProfileInt.FrameType.deauth)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<deauth>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/deauth", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveStationWithType(SsidProfileInt.FrameType.deauth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				deauthObj.setThreshold(
						(DosStationLevelDeauth.Threshold)CLICommonFunc.createObjectWithName(DosStationLevelDeauth.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<deauth>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/deauth", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveStationWithType(SsidProfileInt.FrameType.deauth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				deauthObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<disassoc> */
			if(childObj instanceof DosStationLevelDisassoc){
				DosStationLevelDisassoc disassocObj = (DosStationLevelDisassoc)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<disassoc>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/disassoc", 
						"cr", GenerateXMLDebug.CONFIG_ELEMENT,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				disassocObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveStationWithType(SsidProfileInt.FrameType.disassoc)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<disassoc>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/disassoc", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveStationWithType(SsidProfileInt.FrameType.disassoc)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				disassocObj.setThreshold(
						(DosStationLevelDisassoc.Threshold)CLICommonFunc.createObjectWithName(DosStationLevelDisassoc.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<disassoc>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/disassoc", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveStationWithType(SsidProfileInt.FrameType.disassoc)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				disassocObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<eapol> */
			if(childObj instanceof DosStationLevelEapol){
				DosStationLevelEapol eapolObj = (DosStationLevelEapol)childObj;
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<eapol>.<cr> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/eapol", 
						"cr", GenerateXMLDebug.CONFIG_ELEMENT,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				eapolObj.setCr(CLICommonFunc.getAhOnlyAct(
						hiveObjImpl.isEnableHiveStationWithType(SsidProfileInt.FrameType.eapol)));
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<eapol>.<threshold> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/eapol", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getThresholdHiveStationWithType(SsidProfileInt.FrameType.eapol)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				eapolObj.setThreshold(
						(DosStationLevelEapol.Threshold)CLICommonFunc.createObjectWithName(DosStationLevelEapol.Threshold.class, thresholdParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<eapol>.<alarm> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/eapol", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getAlarmHiveStationWithType(SsidProfileInt.FrameType.eapol)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				eapolObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
				
				/** element: <hive>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<eapol>.<ban> */
				oDebug.debug("/configuration/hive/security/wlan/dos/station-level/frame-type/eapol", 
						"ban", GenerateXMLDebug.SET_VALUE,
						hiveObjImpl.getHiveGuiName(), hiveObjImpl.getHiveName());
				Object[][] banParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, hiveObjImpl.getBanHiveStationWithType(SsidProfileInt.FrameType.eapol)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				eapolObj.setBan(
						(DosBan)CLICommonFunc.createObjectWithName(DosBan.class, banParm)
				);
			}
		}
		hiveChildList_6.clear();
	}
	
}
