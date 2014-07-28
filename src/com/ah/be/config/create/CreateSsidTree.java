package com.ah.be.config.create;

import java.util.List;
import java.util.ArrayList;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.SsidProfileInt;
import com.ah.xml.be.config.*;

/**
 * @author zhang
 *
 */
public class CreateSsidTree {
	
	private SsidProfileInt ssidProfile;
	private SsidObj treeSsid;
	private NetworkAccessSecurityObj security;
	private List<Object> ssidChildList_1 = new ArrayList<Object>();
	private List<Object> ssidChildList_2 = new ArrayList<Object>();
	private List<Object> ssidChildList_3 = new ArrayList<Object>();
	private List<Object> ssidChildList_4 = new ArrayList<Object>();
	private List<Object> ssidChildList_5 = new ArrayList<Object>();
	private List<Object> ssidChildList_6 = new ArrayList<Object>();
	
	private GenerateXMLDebug oDebug;

	/**
	 * Construct method
	 * 
	 * @param arg_ssidProfile
	 *            ssid profile object
	 * @throws Exception -
	 */
	public CreateSsidTree(SsidProfileInt arg_ssidProfile, NetworkAccessSecurityObj security, GenerateXMLDebug oDebug) throws Exception {
		this.oDebug = oDebug;
		this.ssidProfile = arg_ssidProfile;
		this.security =security;
	}
	public void generate() throws Exception{
		/** element: <ssid> */
		oDebug.debug("/configuration", "ssid", GenerateXMLDebug.CONFIG_ELEMENT, ssidProfile.getSsidGuiKey(), null);
		if(ssidProfile.isConfigureSsid()){
			treeSsid = new SsidObj();
			generateSsidLevel_1();
		}
	}
	
	public SsidObj getSsidObj(){
		return treeSsid;
	}

	public void generateSsidLevel_1() throws Exception{
		/**
		 * <ssid>				SsidObj
		 */
		
		/** attribute: name */
		oDebug.debug("/configuration", "ssid", GenerateXMLDebug.SET_NAME, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		treeSsid.setName(ssidProfile.getSsidName());
		treeSsid.setOperation(CLICommonFunc.getAhEnumActValue(ssidProfile.isYesDefault()));
		treeSsid.setUpdateTime(ssidProfile.getSsidUpdateTime());
		
		/** element: <ssid>.<cr> */
		treeSsid.setCr("");
		
		/** element: <ssid>.<default-user-profile-attr> */
		treeSsid.setDefaultUserProfileAttr(security.getDefaultUserProfileAttr());
		
		/** element: <ssid>.<dtim-period> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "dtim-period", GenerateXMLDebug.SET_VALUE, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		Object[][] paramDtimPeriod = {
				{CLICommonFunc.ATTRIBUTE_VALUE,ssidProfile.getDtimPeriod()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		treeSsid.setDtimPeriod(
				(SsidObj.DtimPeriod)CLICommonFunc.createObjectWithName(SsidObj.DtimPeriod.class, paramDtimPeriod)
		);
		
		/** element: <ssid>.<frag-threshold> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "frag-threshold", GenerateXMLDebug.SET_VALUE, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		Object[][] paramFragThreshold = {
				{CLICommonFunc.ATTRIBUTE_VALUE,ssidProfile.getFragThreshold()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		treeSsid.setFragThreshold(
				(SsidObj.FragThreshold)CLICommonFunc.createObjectWithName(SsidObj.FragThreshold.class, paramFragThreshold)
		);
		
		/** element: <ssid>.<hide-ssid> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "hide-ssid", GenerateXMLDebug.SET_OPERATION, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		treeSsid.setHideSsid(
				CLICommonFunc.getAhOnlyAct(
						ssidProfile.isHideSsidEnable()
				)
		);
		
		/** element: <ssid>.<wmm> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "wmm", GenerateXMLDebug.SET_OPERATION, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		treeSsid.setWmm(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableSsidWmm()));
		
		/** element: <ssid>.<wmm> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "wnm", GenerateXMLDebug.SET_OPERATION, 
				ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		treeSsid.setWnm(CLICommonFunc.createAhEnable(ssidProfile.isEnableSsidWnm()));
		
		/** element: <ssid>.<ignore-broadcast-probe> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "ignore-broadcast-probe", GenerateXMLDebug.SET_OPERATION, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		treeSsid.setIgnoreBroadcastProbe(
				CLICommonFunc.getAhOnlyAct(
						ssidProfile.isIgnoreBroadcastProbeEnable()
				)
		);
		
		/** element: <ssid>.<rts-threshold> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "rts-threshold", GenerateXMLDebug.SET_VALUE, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		Object[][] paramRtsThreshold = {
				{CLICommonFunc.ATTRIBUTE_VALUE,ssidProfile.getRtsThreshold()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, ssidProfile.isYesDefault()}
		};
		treeSsid.setRtsThreshold(
				(SsidObj.RtsThreshold)CLICommonFunc.createObjectWithName(SsidObj.RtsThreshold.class, paramRtsThreshold)
		);
		
		/** element: <ssid>.<manage> */
		if(ssidProfile.isConfigInterStationTraffic()){
			AhManage manageObj = new AhManage();
			ssidChildList_1.add(manageObj);
			treeSsid.setManage(manageObj);
		}
		
		/** element: <ssid>.<security> */
		SsidObj.Security securityObj = new SsidObj.Security();
		ssidChildList_1.add(securityObj);
		treeSsid.setSecurity(securityObj);
		
		/** element: <ssid>.<web-server> */
		treeSsid.setWebServer(security.getWebServer());
		
		/** element: <ssid>.<web-directory> */
		treeSsid.setWebDirectory(security.getWebDirectory());
		
		/** element: <ssid>.<schedule> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "schedule", GenerateXMLDebug.CONFIG_ELEMENT, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		int size = ssidProfile.getSsidScheduleSize();
		for(int i=0; i< size; i++){
			
			oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "schedule", GenerateXMLDebug.SET_NAME, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
			treeSsid.getSchedule().add(
					CLICommonFunc.createAhNameActValue(
							ssidProfile.getSsidScheduleNextName(), ssidProfile.isYesDefault()
					)
			);
		}
		
		/** element: <ssid>.<dns-server> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "dns-server", GenerateXMLDebug.CONFIG_ELEMENT, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		if(ssidProfile.isEnableInternalServers()){
			
			oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "dns-server", GenerateXMLDebug.SET_OPERATION, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
			treeSsid.setDnsServer(
					CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableInternalServers())
			);
		}
		
		/** element: <ssid>.<dhcp-server> */
		treeSsid.setDhcpServer(security.getDhcpServer());
		
		/** element: <ssid>.<qos-classifier> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "qos-classifier", GenerateXMLDebug.CONFIG_ELEMENT, ssidProfile.getWlanGuiKey(), ssidProfile.getWlanName());
		if(ssidProfile.isConfigureQosClass()){
			
			oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "qos-classifier", GenerateXMLDebug.SET_NAME, ssidProfile.getWlanGuiKey(), ssidProfile.getWlanName());
			treeSsid.setQosClassifier(
					CLICommonFunc.createAhNameActObj(ssidProfile.getQosClassifierName(), ssidProfile.isYesDefault())
			);
		}
		
		/** element: <ssid>.<qos-marker> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "qos-marker", GenerateXMLDebug.CONFIG_ELEMENT, ssidProfile.getWlanGuiKey(), ssidProfile.getWlanName());
		if(ssidProfile.isConfigureQosMarker()){
			
			oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "qos-marker", GenerateXMLDebug.SET_NAME, ssidProfile.getWlanGuiKey(), ssidProfile.getWlanName());
			treeSsid.setQosMarker(
					CLICommonFunc.createAhNameActObj(ssidProfile.getQosMarkerName(), ssidProfile.isYesDefault())
			);
		}
		
		/** element: <ssid>.<uapsd> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "uapsd", GenerateXMLDebug.SET_OPERATION, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		treeSsid.setUapsd(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableUapsd()));
		
		/** element: <ssid>.<11a-rate-set> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "_11a-rate-set", GenerateXMLDebug.CONFIG_ELEMENT, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		if(ssidProfile.isConfig11aRateSet()){
			
			oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "_11a-rate-set", GenerateXMLDebug.SET_VALUE, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
			treeSsid.set11ARateSet(
					CLICommonFunc.createAhStringActQuoteProhibited(ssidProfile.get11aRateSetValue(), 
							CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault())
			);
		}
		
		/** element: <ssid>.<11g-rate-set> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "_11g-rate-set", GenerateXMLDebug.CONFIG_ELEMENT, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		if(ssidProfile.isConfig11gRateSet()){
			
			oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "_11g-rate-set", GenerateXMLDebug.SET_VALUE, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
			treeSsid.set11GRateSet(
					CLICommonFunc.createAhStringActQuoteProhibited(ssidProfile.get11gRateSetValue(), 
							CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault())
			);
		}
		
		/** element: <ssid>.<_11n-mcs-rate-set> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", 
				"_11n-mcs-rate-set", GenerateXMLDebug.CONFIG_ELEMENT, 
				ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		if(ssidProfile.isConfig11nRateSet()){
			treeSsid.set11NMcsRateSet(
					CLICommonFunc.createAhStringActQuoteProhibited(ssidProfile.get11nRateSetValue(), true, true)
			);
		}
		
		/** element: <ssid>.<_11n-mcs-expand-rate-set> */
		if(ssidProfile.isConfig11ngRateSet()){
			treeSsid.set11NMcsExpandRateSet(CLICommonFunc.createAhStringActQuoteProhibited(
					ssidProfile.getExpand_11nRateSetValue(), true, true));
		}
		
		/** element: <ssid>.<_11ac-mcs-rate-set> */
		if(ssidProfile.isConfig11acRateSet()){
			treeSsid.set11AcMcsRateSet(CLICommonFunc.createAhStringActQuoteProhibited(ssidProfile.get11acRageSets(), true, true));
		}
		
		/** element: <ssid>.<user-profile-allowed> */
		treeSsid.setUserProfileAllowed(security.getUserProfileAllowed());
		
//		/** element: <ssid>.<user-profile-deny> */
//		treeSsid.setUserProfileDeny(security.getUserProfileDeny());
		
		/** element: <ssid>.<roaming> */
		SsidObj.Roaming roamingObj = new SsidObj.Roaming();
		ssidChildList_1.add(roamingObj);
		treeSsid.setRoaming(roamingObj);
		
		/** element: <ssid>.<inter-station-traffic> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "inter-station-traffic", GenerateXMLDebug.CONFIG_ELEMENT, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		if(ssidProfile.isConfigInterStationTraffic()){
			
			oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "inter-station-traffic", GenerateXMLDebug.SET_OPERATION, ssidProfile.getServiceFilterGuikey(), ssidProfile.getServiceFilterName());
			treeSsid.setInterStationTraffic(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableInterStationTraffic()));
		}
		
		/** element: <ssid>.<max-client> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "max-client", GenerateXMLDebug.SET_VALUE, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		Object[][] maxClientParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getMaxClient()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		treeSsid.setMaxClient(
				(SsidObj.MaxClient)CLICommonFunc.createObjectWithName(SsidObj.MaxClient.class, maxClientParm)
		);
		
		/** element: <ssid>.<user-group> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "user-group", GenerateXMLDebug.CONFIG_ELEMENT, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		for(int i=0; i<ssidProfile.getPskUserGroupSize(); i++){
			
			oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", "user-group", GenerateXMLDebug.SET_NAME, ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
			treeSsid.getUserGroup().add(CLICommonFunc.createAhNameActValue(
					ssidProfile.getPskUserGroupName(i), CLICommonFunc.getYesDefault())
			);
		}
		
		/** element: <ssid>.<mode> */
		SsidObj.Mode modeObj = new SsidObj.Mode();
		ssidChildList_1.add(modeObj);
		treeSsid.setMode(modeObj);
		
		/** element: <ssid>.<airscreen> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']", 
				"airscreen", GenerateXMLDebug.CONFIG_ELEMENT, 
				ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		if(ssidProfile.isConfigAirScreen()){
			SsidObj.Airscreen airscreenObj = new SsidObj.Airscreen();
			ssidChildList_1.add(airscreenObj);
			treeSsid.setAirscreen(airscreenObj);
		}
		
		/** element: <ssid>.<client-age-out> */
		Object[][] clientOutArgs = {
				{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getClientAgeOut()},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		treeSsid.setClientAgeOut(
				(SsidObj.ClientAgeOut)CLICommonFunc.createObjectWithName(SsidObj.ClientAgeOut.class, clientOutArgs)
		);
		
		/** element: <ssid>.<security-object> */
		treeSsid.setSecurityObject(CLICommonFunc.createAhNameActValue(
				ssidProfile.getSecurityObjectName(), CLICommonFunc.getYesDefault()));
		
		/** element: <ssid>.<multicast> */
		SsidMulticast multicast = new SsidMulticast();
		ssidChildList_1.add(multicast);
		treeSsid.setMulticast(multicast);
		
		/** element: <ssid>.<rrm> */
		SsidRrm rrm = new SsidRrm();
		ssidChildList_1.add(rrm);
		treeSsid.setRrm(rrm);
		
		
		/** element: <ssid>.<admctl> */
		SsidAdmctl admctl = new SsidAdmctl();
		ssidChildList_1.add(admctl);
		treeSsid.setAdmctl(admctl);
		
		/** element: <ssid>.<priority> */
//		if(ssidProfile.isConfigPriority()){
//			treeSsid.setPriority(CLICommonFunc.createAhIntActObj(ssidProfile.getPriority(), CLICommonFunc.getYesDefault()));
//		}
		
		if (ssidProfile.isEnableConnectionAlarm()) {
			SsidConnectionAlarming alarm = new SsidConnectionAlarming();
			ssidChildList_1.add(alarm);
			treeSsid.setConnectionAlarming(alarm);
		}
		
		generateSsidLevel_2(ssidChildList_1);
	}
	
	private void generateSsidLevel_2(List<Object> list) throws Exception{
		/**
		 * <ssid>.<manage>						AhManage
		 * <ssid>.<security>					SsidObj.Security
		 * <ssid>.<roaming>						SsidObj.Roaming
		 * <ssid>.<mode>						SsidObj.Mode
		 * <ssid>.<airscreen>					SsidObj.Airscreen
		 * <ssid>.<multicast>					SsidMulticast
		 */
		if(list.isEmpty()){
			return;
		}

		for(Object childObj : list){
			
			/** element: <ssid>.<manage> */
			if(childObj instanceof AhManage){
				AhManage manageObj = (AhManage)childObj;
				
				/** element: <ssid>.<manage>.<SNMP> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/manage", "SNMP", GenerateXMLDebug.SET_OPERATION, 
						ssidProfile.getServiceFilterGuikey(), ssidProfile.getServiceFilterName());
				manageObj.setSNMP(
						CLICommonFunc.getAhOnlyAct(ssidProfile.isSsidManageEnable(SsidProfileInt.MANAGE_SNMP))
				);
				
				/** element: <ssid>.<manage>.<SSH> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/manage", "SSH", GenerateXMLDebug.SET_OPERATION, 
						ssidProfile.getServiceFilterGuikey(), ssidProfile.getServiceFilterName());
				manageObj.setSSH(
						CLICommonFunc.getAhOnlyAct(ssidProfile.isSsidManageEnable(SsidProfileInt.MANAGE_SSH))
				);
				
				/** element: <ssid>.<manage>.<Telnet> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/manage", "Telnet", GenerateXMLDebug.SET_OPERATION, 
						ssidProfile.getServiceFilterGuikey(), ssidProfile.getServiceFilterName());
				manageObj.setTelnet(
						CLICommonFunc.getAhOnlyAct(ssidProfile.isSsidManageEnable(SsidProfileInt.MANAGE_TELNET))
				);
				
				/** element: <ssid>.<manage>.<ping> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/manage", "ping", GenerateXMLDebug.SET_OPERATION, 
						ssidProfile.getServiceFilterGuikey(), ssidProfile.getServiceFilterName());
				manageObj.setPing(
						CLICommonFunc.getAhOnlyAct(ssidProfile.isSsidManageEnable(SsidProfileInt.MANAGE_PING))
				);
			}
			
			/** element: <ssid>.<security> */
			if(childObj instanceof SsidObj.Security){
				SsidObj.Security securityObj = (SsidObj.Security)childObj;
				
				/** element: <ssid>.<security>.<preauth> */
				securityObj.setPreauth(security.getSecurity().getPreauth());
				
				/** element: <ssid>.<security>.<mac-filter> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security", "mac-filter", GenerateXMLDebug.SET_VALUE, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
//				if(ssidProfile.isConfigureMacFilter()){
				securityObj.setMacFilter(
						CLICommonFunc.createAhNameActValue(ssidProfile.getMacFilter(), ssidProfile.isYesDefault())
				);
//				}
				
				/** element: <ssid>.<security>.<protocol-suite> */
				securityObj.setProtocolSuite(security.getSecurity().getProtocolSuite());
				
				/** element: <ssid>.<security>.<screening> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security", "screening", GenerateXMLDebug.CONFIG_ELEMENT, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				if(ssidProfile.isConfigureScreen()){
					SsidObj.Security.Screening screenObj = new SsidObj.Security.Screening();
					ssidChildList_2.add(screenObj);
					securityObj.setScreening(screenObj);
				}
				
				/** element: <ssid>.<security>.<wlan> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security", "wlan", GenerateXMLDebug.CONFIG_ELEMENT, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				if(ssidProfile.isConfigureWlan()){
					SsidObj.Security.Wlan wlanObj = new SsidObj.Security.Wlan();
					ssidChildList_2.add(wlanObj);
					securityObj.setWlan(wlanObj);
				}
				
				/** element: <ssid>.<security>.<aaa> */
				securityObj.setAaa(security.getSecurity().getAaa());
				
				/** element: <ssid>.<security>.<additional-auth-method> */
				securityObj.setAdditionalAuthMethod(security.getSecurity().getAdditionalAuthMethod());
				
				/** element: <ssid>.<security>.<private-psk> */
				securityObj.setPrivatePsk(security.getSecurity().getPrivatePsk());
				
				/** element: <ssid>.<security>.<local-cache> */
				securityObj.setLocalCache(security.getSecurity().getLocalCache());
				
				/** element: <ssid>.<security>.<roaming> */
				securityObj.setRoaming(security.getSecurity().getRoaming());
				
				/** element: <ssid>.<security>.<eap> */
				securityObj.setEap(security.getSecurity().getEap());
			}
			
			/** element: <ssid>.<roaming> */
			if(childObj instanceof SsidObj.Roaming){
				SsidObj.Roaming roamingObj = (SsidObj.Roaming)childObj;
				
				/** element: <ssid>.<roaming>.<cache> */
				SsidObj.Roaming.Cache cacheObj = new SsidObj.Roaming.Cache();
				ssidChildList_2.add(cacheObj);
				roamingObj.setCache(cacheObj);
			}
			
			/** element: <ssid>.<mode> */
			if(childObj instanceof SsidObj.Mode){
				SsidObj.Mode modeObj = (SsidObj.Mode)childObj;
				
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/mode", 
						"legacy", GenerateXMLDebug.CONFIG_ELEMENT, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				/** element: <ssid>.<mode>.<legacy> */
				modeObj.setLegacy(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableLegacy()));
			}
			
			/** element: <ssid>.<airscreen> */
			if(childObj instanceof SsidObj.Airscreen){
				SsidObj.Airscreen airScreenObj = (SsidObj.Airscreen)childObj;
				
				/** element: <ssid>.<airscreen>.<rule> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/airscreen",
						"rule", GenerateXMLDebug.SET_NAME,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				for(int i=0; i<ssidProfile.getAirScreenSize(); i++){
					airScreenObj.getRule().add(CLICommonFunc.createAhNameActObj(ssidProfile.getAirScreenRuleName(i), CLICommonFunc.getYesDefault()));
				}
			}
			
			/** element: <ssid>.<multicast> */
			if(childObj instanceof SsidMulticast){
				SsidMulticast multicast = (SsidMulticast)childObj;
				
				/** element: <ssid>.<multicast>.<conversion-to-unicast> */
				SsidMulticastConversionToUnicast conversion = new SsidMulticastConversionToUnicast();
				ssidChildList_2.add(conversion);
				multicast.setConversionToUnicast(conversion);
				
				if(ssidProfile.getMulticastConversionValue() == SsidMulticastConversionToUnicastValue.AUTO){
					
					/** element: <ssid>.<multicast>.<cu-threshold> */
					Object[][] cuParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getCuThresholdValue()},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					multicast.setCuThreshold(
							(SsidMulticastCuThreshold)CLICommonFunc.createObjectWithName(SsidMulticastCuThreshold.class, cuParm));
					
					/** element: <ssid>.<multicast>.<member-threshold> */
					Object[][] memberParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getMemberThresholdValue()},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					multicast.setMemberThreshold(
							(SsidMulticastMemberThreshold)CLICommonFunc.createObjectWithName(SsidMulticastMemberThreshold.class, memberParm));
				}
			}
			
			/** element: <ssid>.<rrm> */
			if(childObj instanceof SsidRrm){
				SsidRrm rrm = (SsidRrm)childObj;
				/** element: <ssid>.<rrm>.<enable> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/rrm",
						"enable", GenerateXMLDebug.CONFIG_ELEMENT,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				rrm.setEnable(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableRrm()));
			}
			
			/** element: <ssid>.<admctl> */
			if(childObj instanceof SsidAdmctl){
				SsidAdmctl admctl = (SsidAdmctl)childObj;
				/** element: <ssid>.<admctl>.<ac> */
				admctl.getAc().add(createSsidAdmctlAc(SsidProfileInt.WMM_AC_BESTEFFORT));
				/** element: <ssid>.<admctl>.<ac> */
				admctl.getAc().add(createSsidAdmctlAc(SsidProfileInt.WMM_AC_BACKGROUND));
				/** element: <ssid>.<admctl>.<ac> */
				admctl.getAc().add(createSsidAdmctlAc(SsidProfileInt.WMM_AC_VIDEO));
				/** element: <ssid>.<admctl>.<ac> */
				admctl.getAc().add(createSsidAdmctlAc(SsidProfileInt.WMM_AC_VOICE));
				
			}
			
			if(childObj instanceof SsidConnectionAlarming){
				SsidConnectionAlarming alarm = (SsidConnectionAlarming)childObj;
				ConnectionAlarmingEgressMulticast egressMulticast = new ConnectionAlarmingEgressMulticast();
				alarm.setEgressMulticast(egressMulticast);
				ssidChildList_2.add(egressMulticast);
			}
			
		}
		generateSsidLevel_3(ssidChildList_2);
	}
	
	public void generateSsidLevel_3(List<Object> list) throws Exception{
		/**
		 * <ssid>.<security>.<screening>					SsidObj.Security.Screening
		 * <ssid>.<security>.<wlan>							SsidObj.Security.Wlan
		 * <ssid>.<roaming>.<cache>							SsidObj.Roaming.Cache
		 * <ssid>.<multicast>.<conversion-to-unicast>		SsidMulticastConversionToUnicast
		 */
		for(Object childObj : list){
			
			/** element: <ssid>.<security>.<screening> */
			if(childObj instanceof SsidObj.Security.Screening){
				SsidObj.Security.Screening screenProfile = (SsidObj.Security.Screening)childObj;
				
				/** element: <ssid>.<security>.<screening>.<arp-flood> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening", "arp-flood", GenerateXMLDebug.CONFIG_ELEMENT, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				if(ssidProfile.isConfigureScreenElement(SsidProfileInt.SCREENING_ARP_FLOOD)){
					SsidObj.Security.Screening.ArpFlood arpFlood = new SsidObj.Security.Screening.ArpFlood();
					ssidChildList_3.add(arpFlood);
					screenProfile.setArpFlood(arpFlood);
				}
				
				/** element: <ssid>.<security>.<screening>.<icmp-flood> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening", "icmp-flood", GenerateXMLDebug.CONFIG_ELEMENT, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				if(ssidProfile.isConfigureScreenElement(SsidProfileInt.SCREENING_ICMP_FLOOD)){
					SsidObj.Security.Screening.IcmpFlood icmpFlood = new SsidObj.Security.Screening.IcmpFlood();
					ssidChildList_3.add(icmpFlood);
					screenProfile.setIcmpFlood(icmpFlood);
				}
				
				/** element: <ssid>.<security>.<screening>.<udp-flood> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening", "udp-flood", GenerateXMLDebug.CONFIG_ELEMENT, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				if(ssidProfile.isConfigureScreenElement(SsidProfileInt.SCREENING_UDP_FLOOD)){
					SsidObj.Security.Screening.UdpFlood udpFlood = new SsidObj.Security.Screening.UdpFlood();
					ssidChildList_3.add(udpFlood);
					screenProfile.setUdpFlood(udpFlood);
				}
				
				/** element: <ssid>.<security>.<screening>.<syn-flood> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening", "syn-flood", GenerateXMLDebug.CONFIG_ELEMENT, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				if(ssidProfile.isConfigureScreenElement(SsidProfileInt.SCREENING_SYN_FLOOD)){
					SsidObj.Security.Screening.SynFlood synFlood = new SsidObj.Security.Screening.SynFlood();
					ssidChildList_3.add(synFlood);
					screenProfile.setSynFlood(synFlood);
				}
				
				/** element: <ssid>.<security>.<screening>.<address-sweep> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening", "address-sweep", GenerateXMLDebug.CONFIG_ELEMENT, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				if(ssidProfile.isConfigureScreenElement(SsidProfileInt.SCREENING_ADDRESS_SWEEP)){
					SsidObj.Security.Screening.AddressSweep addressSweep = new SsidObj.Security.Screening.AddressSweep();
					ssidChildList_3.add(addressSweep);
					screenProfile.setAddressSweep(addressSweep);
				}
				
				/** element: <ssid>.<security>.<screening>.<port-scan> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening", "port-scan", GenerateXMLDebug.CONFIG_ELEMENT, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				if(ssidProfile.isConfigureScreenElement(SsidProfileInt.SCREENING_PORT_SCAN)){
					SsidObj.Security.Screening.PortScan portScan = new SsidObj.Security.Screening.PortScan();
					ssidChildList_3.add(portScan);
					screenProfile.setPortScan(portScan);
				}
				
				/** element: <ssid>.<security>.<screening>.<ip-spoof> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening", "ip-spoof", GenerateXMLDebug.CONFIG_ELEMENT, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				if(ssidProfile.isConfigureScreenElement(SsidProfileInt.SCREENING_IP_SPOOF)){
					SsidObj.Security.Screening.IpSpoof ipSpoof = new SsidObj.Security.Screening.IpSpoof();
					ssidChildList_3.add(ipSpoof);
					screenProfile.setIpSpoof(ipSpoof);
				}
				
				/** element: <ssid>.<security>.<screening>.<radius-attack> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening", "radius-attack", GenerateXMLDebug.CONFIG_ELEMENT, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				if(ssidProfile.isConfigureScreenElement(SsidProfileInt.SCREENING_RADIUS_ATTACK)){
					SsidObj.Security.Screening.RadiusAttack radiusAttack = new SsidObj.Security.Screening.RadiusAttack();
					ssidChildList_3.add(radiusAttack);
					screenProfile.setRadiusAttack(radiusAttack);
				}
				
				/** element: <ssid>.<security>.<screening>.<tcp-syn-check> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening", "tcp-syn-check", GenerateXMLDebug.SET_OPERATION, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				screenProfile.setTcpSynCheck(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableScreenTcpSynCheck()));
			}
			
			/** element: <ssid>.<security>.<wlan> */
			if(childObj instanceof SsidObj.Security.Wlan){
				SsidObj.Security.Wlan wlanObj = (SsidObj.Security.Wlan)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos> */
				SsidWlanDos wlanDosObj = new SsidWlanDos();
				ssidChildList_3.add(wlanDosObj);
				wlanObj.setDos(wlanDosObj);
			}
			
			/** element: <ssid>.<roaming>.<cache> */
			if(childObj instanceof SsidObj.Roaming.Cache){
				SsidObj.Roaming.Cache cacheObj = (SsidObj.Roaming.Cache)childObj;
				
				/** element: <ssid>.<roaming>.<cache>.<update-interval> */
				SsidObj.Roaming.Cache.UpdateInterval updateIntervalObj = new SsidObj.Roaming.Cache.UpdateInterval();
				ssidChildList_3.add(updateIntervalObj);
				cacheObj.setUpdateInterval(updateIntervalObj);
			}
			
			/** element: <ssid>.<multicast>.<conversion-to-unicast> */
			if(childObj instanceof SsidMulticastConversionToUnicast){
				SsidMulticastConversionToUnicast conversion = (SsidMulticastConversionToUnicast)childObj;
				
				/** attribute: value */
				conversion.setValue(ssidProfile.getMulticastConversionValue());
				
				/** attribute: operation */
				conversion.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			}
			
			if(childObj instanceof ConnectionAlarmingEgressMulticast){
				ConnectionAlarmingEgressMulticast egressMulticast = (ConnectionAlarmingEgressMulticast)childObj;
				ConnectionAlarmingEgressMulticastThreshold egressMulticastthreshold = new ConnectionAlarmingEgressMulticastThreshold();
				egressMulticast.setThreshold(egressMulticastthreshold);
				ssidChildList_3.add(egressMulticastthreshold);
			}
			
		}
		generateSsidLevel_4(ssidChildList_3);
	}
	
	private void generateSsidLevel_4(List<Object> list) throws Exception{
		
		/**
		 * <ssid>.<security>.<screening>.<icmp-flood>							SsidObj.Security.Screening.IcmpFlood
		 * <ssid>.<security>.<screening>.<udp-flood>							SsidObj.Security.Screening.UdpFlood
		 * <ssid>.<security>.<screening>.<syn-flood>							SsidObj.Security.Screening.SynFlood
		 * <ssid>.<security>.<screening>.<address-sweep>						SsidObj.Security.Screening.AddressSweep
		 * <ssid>.<security>.<screening>.<port-scan>							SsidObj.Security.Screening.PortScan
		 * <ssid>.<security>.<screening>.<ip-spoof>								SsidObj.Security.Screening.IpSpoof
		 * <ssid>.<security>.<screening>.<radius-attack>						SsidObj.Security.Screening.RadiusAttack
		 * <ssid>.<security>.<wlan>.<dos>										SsidWlanDos
		 * <ssid>.<security>.<screening>.<arp-flood>							SsidObj.Security.Screening.ArpFlood
		 * <ssid>.<roaming>.<cache>.<update-interval>							SsidObj.Roaming.Cache.UpdateInterval
		 */	
		for(Object childObj : list){
			
			/** element: <ssid>.<security>.<screening>.<icmp-flood> */
			if(childObj instanceof SsidObj.Security.Screening.IcmpFlood){
				SsidObj.Security.Screening.IcmpFlood icmpFloodObj = (SsidObj.Security.Screening.IcmpFlood)childObj;
				
				/** attribute: operation */
				icmpFloodObj.setOperation(
						CLICommonFunc.getAhEnumAct(ssidProfile.isYesDefault())
				);
				
				/** element: <ssid>.<security>.<screening>.<icmp-flood>.<cr> */
				icmpFloodObj.setCr("");
				
				/** element: <ssid>.<security>.<screening>.<icmp-flood>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/icmp-flood", 
						"threshold", GenerateXMLDebug.SET_VALUE, 
						ssidProfile.getIpDosGuiKey(), ssidProfile.getIpDosName());
				Object[][] parmThreshold = {
					{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getScreenThresholdValue(SsidProfileInt.SCREENING_ICMP_FLOOD)},
					{CLICommonFunc.ATTRIBUTE_OPERATION,ssidProfile.isYesDefault()}
				};
				icmpFloodObj.setThreshold(
						(SsidObj.Security.Screening.IcmpFlood.Threshold)
						CLICommonFunc.createObjectWithName(SsidObj.Security.Screening.IcmpFlood.Threshold.class, parmThreshold)
				);
				
				/** element: <ssid>.<security>.<screening>.<icmp-flood>.<action> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/icmp-flood", 
						"action", GenerateXMLDebug.NULL, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				icmpFloodObj.setAction(createScreeningAction(SsidProfileInt.SCREENING_ICMP_FLOOD));
			}
			
			/** element: <ssid>.<security>.<screening>.<udp-flood> */
			if(childObj instanceof SsidObj.Security.Screening.UdpFlood){
				SsidObj.Security.Screening.UdpFlood udpFloodObj = (SsidObj.Security.Screening.UdpFlood)childObj;
				
				/** attribute: operation */
				udpFloodObj.setOperation(
						CLICommonFunc.getAhEnumAct(ssidProfile.isYesDefault())
				);
				
				/** element: <ssid>.<security>.<screening>.<udp-flood>.<cr> */
				udpFloodObj.setCr("");
				
				/** element: <ssid>.<security>.<screening>.<udp-flood>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/udp-flood", 
						"threshold", GenerateXMLDebug.SET_VALUE, 
						ssidProfile.getIpDosGuiKey(), ssidProfile.getIpDosName());
				Object[][] parmThreshold = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getScreenThresholdValue(SsidProfileInt.SCREENING_UDP_FLOOD)},
						{CLICommonFunc.ATTRIBUTE_OPERATION,ssidProfile.isYesDefault()}
					};
				udpFloodObj.setThreshold(
						(SsidObj.Security.Screening.UdpFlood.Threshold)
						CLICommonFunc.createObjectWithName(SsidObj.Security.Screening.UdpFlood.Threshold.class, parmThreshold)
				);
				
				/** element: <ssid>.<security>.<screening>.<udp-flood>.<action> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/udp-flood", 
						"action", GenerateXMLDebug.NULL, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				udpFloodObj.setAction(createScreeningAction(SsidProfileInt.SCREENING_UDP_FLOOD));
			}
			
			/** element: <ssid>.<security>.<screening>.<syn-flood> */
			if(childObj instanceof SsidObj.Security.Screening.SynFlood){
				SsidObj.Security.Screening.SynFlood synFloodObj = (SsidObj.Security.Screening.SynFlood)childObj;
				
				/** attribute: operation */
				synFloodObj.setOperation(
						CLICommonFunc.getAhEnumAct(ssidProfile.isYesDefault())
				);
				
				/** element: <ssid>.<security>.<screening>.<syn-flood>.<cr> */
				synFloodObj.setCr("");
				
				/** element: <ssid>.<security>.<screening>.<syn-flood>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/syn-flood", 
						"threshold", GenerateXMLDebug.SET_VALUE, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] parmThreshold = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getScreenThresholdValue(SsidProfileInt.SCREENING_SYN_FLOOD)},
						{CLICommonFunc.ATTRIBUTE_OPERATION,ssidProfile.isYesDefault()}
				};
				synFloodObj.setThreshold(
						(SsidObj.Security.Screening.SynFlood.Threshold)
						CLICommonFunc.createObjectWithName(SsidObj.Security.Screening.SynFlood.Threshold.class, parmThreshold)
				);
				
				/** element: <ssid>.<security>.<screening>.<syn-flood>.<action> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/syn-flood", 
						"action", GenerateXMLDebug.NULL, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				synFloodObj.setAction(createScreeningAction(SsidProfileInt.SCREENING_SYN_FLOOD));
			}
			
			/** element: <ssid>.<security>.<screening>.<arp-flood> */
			if(childObj instanceof SsidObj.Security.Screening.ArpFlood ){
				SsidObj.Security.Screening.ArpFlood arpFloodObj = (SsidObj.Security.Screening.ArpFlood)childObj;
				
				/** attribute: operation */
				arpFloodObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <ssid>.<security>.<screening>.<arp-flood>.<cr> */
				arpFloodObj.setCr("");
				
				/** element: <ssid>.<security>.<screening>.<arp-flood>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/arp-flood", 
						"threshold", GenerateXMLDebug.SET_VALUE, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] arpThreParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getScreenThresholdValue(SsidProfileInt.SCREENING_ARP_FLOOD)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				arpFloodObj.setThreshold(
						(SsidObj.Security.Screening.ArpFlood.Threshold)CLICommonFunc.createObjectWithName(
								SsidObj.Security.Screening.ArpFlood.Threshold.class, arpThreParm)
				);
				
				/** element: <ssid>.<security>.<screening>.<arp-flood>.<action> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/arp-flood", 
						"action", GenerateXMLDebug.NULL, 
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				arpFloodObj.setAction(
						createScreeningAction(SsidProfileInt.SCREENING_ARP_FLOOD)
				);
				
			}
			
			/** element: <ssid>.<security>.<screening>.<address-sweep> */
			if(childObj instanceof SsidObj.Security.Screening.AddressSweep){
				SsidObj.Security.Screening.AddressSweep addressSweepObj = (SsidObj.Security.Screening.AddressSweep)childObj;
				
				/** attribute: operation */
				addressSweepObj.setOperation(
						CLICommonFunc.getAhEnumAct(ssidProfile.isYesDefault())
				);
				
				/** element: <ssid>.<security>.<screening>.<address-sweep>.<cr> */
				addressSweepObj.setCr("");
				
				/** element: <ssid>.<security>.<screening>.<address-sweep>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/address-sweep", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] parmThreshold = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getScreenThresholdValue(SsidProfileInt.SCREENING_ADDRESS_SWEEP)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, ssidProfile.isYesDefault()}
				};
				addressSweepObj.setThreshold(
						(SsidObj.Security.Screening.AddressSweep.Threshold)CLICommonFunc.createObjectWithName(
								SsidObj.Security.Screening.AddressSweep.Threshold.class, parmThreshold)
				);
				
				/** element: <ssid>.<security>.<screening>.<address-sweep>.<action> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/address-sweep", 
						"action", GenerateXMLDebug.NULL,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				addressSweepObj.setAction(
						createScreeningAction(SsidProfileInt.SCREENING_ADDRESS_SWEEP)
				);
			}
			
			/** element: <ssid>.<security>.<screening>.<port-scan> */
			if(childObj instanceof SsidObj.Security.Screening.PortScan){
				SsidObj.Security.Screening.PortScan portScanObj = (SsidObj.Security.Screening.PortScan)childObj;
				
				/** attribute: operation */
				portScanObj.setOperation(
						CLICommonFunc.getAhEnumAct(ssidProfile.isYesDefault())
				);
				
				/** element: <ssid>.<security>.<screening>.<port-scan>.<cr> */
				portScanObj.setCr("");
				
				/** element: <ssid>.<security>.<screening>.<port-scan>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/port-scan", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] parmThreshold = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getScreenThresholdValue(SsidProfileInt.SCREENING_PORT_SCAN)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, ssidProfile.isYesDefault()}
				};
				portScanObj.setThreshold(
						(SsidObj.Security.Screening.PortScan.Threshold)CLICommonFunc.createObjectWithName(
								SsidObj.Security.Screening.PortScan.Threshold.class, parmThreshold)
				);
				
				/** element: <ssid>.<security>.<screening>.<port-scan>.<action> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/port-scan", 
						"action", GenerateXMLDebug.NULL,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				portScanObj.setAction(
						createScreeningAction(SsidProfileInt.SCREENING_PORT_SCAN)
				);
			}
			
			/** element: <ssid>.<security>.<screening>.<ip-spoof> */
			if(childObj instanceof SsidObj.Security.Screening.IpSpoof){
				SsidObj.Security.Screening.IpSpoof ipSpoofObj = (SsidObj.Security.Screening.IpSpoof)childObj;
				
				/** attribute: operation */
				ipSpoofObj.setOperation(
						CLICommonFunc.getAhEnumAct(ssidProfile.isYesDefault())
				);
				
				/** element: <ssid>.<security>.<screening>.<ip-spoof>.<cr> */
				ipSpoofObj.setCr("");
				
				/** element: <ssid>.<security>.<screening>.<ip-spoof>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/ip-spoof", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] parmThreshold = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getScreenThresholdValue(SsidProfileInt.SCREENING_IP_SPOOF)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, ssidProfile.isYesDefault()}
				};
				ipSpoofObj.setThreshold(
						(SsidObj.Security.Screening.IpSpoof.Threshold)
						CLICommonFunc.createObjectWithName(SsidObj.Security.Screening.IpSpoof.Threshold.class, parmThreshold)
				);
				
				/** element: <ssid>.<security>.<screening>.<ip-spoof>.<action> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/ip-spoof", 
						"threshold", GenerateXMLDebug.NULL,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				ipSpoofObj.setAction(
						createScreeningAction(SsidProfileInt.SCREENING_IP_SPOOF)
				);
			}
			
			/** element: <ssid>.<security>.<screening>.<radius-attack> */
			if(childObj instanceof SsidObj.Security.Screening.RadiusAttack){
				SsidObj.Security.Screening.RadiusAttack radiusAttackObj = (SsidObj.Security.Screening.RadiusAttack)childObj;
				
				/** attribute: operation */
				radiusAttackObj.setOperation(
						CLICommonFunc.getAhEnumAct(ssidProfile.isYesDefault())
				);
				
				/** element: <ssid>.<security>.<screening>.<radius-attack>.<cr> */
				radiusAttackObj.setCr("");
				
				/** element: <ssid>.<security>.<screening>.<radius-attack>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/radius-attack", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] parmThreshold = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getScreenThresholdValue(SsidProfileInt.SCREENING_RADIUS_ATTACK)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, ssidProfile.isYesDefault()}
				};
				radiusAttackObj.setThreshold(
						(SsidObj.Security.Screening.RadiusAttack.Threshold)
						CLICommonFunc.createObjectWithName(SsidObj.Security.Screening.RadiusAttack.Threshold.class, parmThreshold)
				);
				
				/** element: <ssid>.<security>.<screening>.<radius-attack>.<action> */
				ScreeningRadiusAttackAction radiusActionObj = new ScreeningRadiusAttackAction();
				
				/** attribute: operation */
				radiusActionObj.setOperation(CLICommonFunc.getAhEnumAct(ssidProfile.isYesDefault()));
				
				/** childElement: <ssid>.<security>.<screening>.<radius-attack>.<action>.<ban-forever> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/radius-attack/action", 
						"ban-forever", GenerateXMLDebug.CONFIG_ELEMENT,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				if(ssidProfile.isConfigureActionWithScreen(SsidProfileInt.SCREENING_RADIUS_ATTACK, SsidProfileInt.SCREENING_ACTION_BAN_FOREVER)){
					radiusActionObj.setBanForever("");
				}
				
				/** childElement: <ssid>.<security>.<screening>.<radius-attack>.<action>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/radius-attack/action", 
						"alarm", GenerateXMLDebug.CONFIG_ELEMENT,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				if(ssidProfile.isConfigureActionWithScreen(SsidProfileInt.SCREENING_RADIUS_ATTACK, SsidProfileInt.SCREENING_ACTION_ALARM)){
					
					oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/radius-attack/action", 
							"alarm", GenerateXMLDebug.SET_VALUE,
							ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
					Object[][] parmAlarm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getScreenActionValue(SsidProfileInt.SCREENING_RADIUS_ATTACK, SsidProfileInt.SCREENING_ACTION_ALARM)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, ssidProfile.isYesDefault()}
					};
					radiusActionObj.setAlarm(
							(ScreeningRadiusAttackAction.Alarm)
							CLICommonFunc.createObjectWithName(ScreeningRadiusAttackAction.Alarm.class, parmAlarm)
					);
				}
				/** childElement: <ssid>.<security>.<screening>.<radius-attack>.<action>.<ban> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/radius-attack/action", 
						"ban", GenerateXMLDebug.CONFIG_ELEMENT,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				if(ssidProfile.isConfigureActionWithScreen(SsidProfileInt.SCREENING_RADIUS_ATTACK, SsidProfileInt.SCREENING_ACTION_BAN)){
					
					oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/screening/radius-attack/action", 
							"ban", GenerateXMLDebug.SET_VALUE,
							ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
					Object[][] parmBan = {
							{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getScreenActionValue(SsidProfileInt.SCREENING_RADIUS_ATTACK, SsidProfileInt.SCREENING_ACTION_BAN)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, ssidProfile.isYesDefault()}
					};
					radiusActionObj.setBan(
							(ScreeningRadiusAttackAction.Ban)
							CLICommonFunc.createObjectWithName(ScreeningRadiusAttackAction.Ban.class, parmBan)
					);
				}
				
				radiusAttackObj.setAction(radiusActionObj);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos> */
			if(childObj instanceof SsidWlanDos){
				SsidWlanDos wlanObj = (SsidWlanDos)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos", 
						"station-level", GenerateXMLDebug.CONFIG_ELEMENT,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				if(ssidProfile.isConfigureStationLevel()){
					DosStationLevel stationLevelObj = new DosStationLevel();
					ssidChildList_4.add(stationLevelObj);
					wlanObj.setStationLevel(stationLevelObj);
				}
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos", 
						"ssid-level", GenerateXMLDebug.CONFIG_ELEMENT,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				if(ssidProfile.isConfigureSsidLevel()){
					DosSsidLevel ssidLevelObj = new DosSsidLevel();
					ssidChildList_4.add(ssidLevelObj);
					wlanObj.setSsidLevel(ssidLevelObj);
				}
			}
			
			/** element: <ssid>.<roaming>.<cache>.<update-interval>	*/
			if(childObj instanceof SsidObj.Roaming.Cache.UpdateInterval){
				SsidObj.Roaming.Cache.UpdateInterval updateIntervalObj = (SsidObj.Roaming.Cache.UpdateInterval)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/roaming/cache", 
						"update-interval", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				updateIntervalObj.setValue(ssidProfile.getRoamingUpdateInterval());
				
				/** attribute: operation */
				updateIntervalObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <ssid>.<roaming>.<cache>.<update-interval>.<ageout> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/roaming/cache/update-interval", 
						"ageout", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] ageOutParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getRoamingAgeout()}
				};
				updateIntervalObj.setAgeout(
						(SsidObj.Roaming.Cache.UpdateInterval.Ageout)CLICommonFunc.createObjectWithName(SsidObj.Roaming.Cache.UpdateInterval.Ageout.class, ageOutParm)
				);
			}
			
			if(childObj instanceof ConnectionAlarmingEgressMulticastThreshold){
				ConnectionAlarmingEgressMulticastThreshold egressMulticastThreshold = new  ConnectionAlarmingEgressMulticastThreshold();
				egressMulticastThreshold.setValue(ssidProfile.getEgressMulticastThreshold());
				//egressMulticastThreshold.setTimeInterval(ssidProfile.getEgressMulticastInterval());
				egressMulticastThreshold.setOperation(AhEnumAct.YES);
			}
			
		}
		generateSsidLevel_5();
	}
	
	private void generateSsidLevel_5() throws Exception {
		/**
		 * <ssid>.<security>.<wlan>.<dos>.<station-level>			DosStationLevel
		 * <ssid>.<security>.<wlan>.<dos>.<ssid-level>				DosSsidLevel
		 */
		for(Object childObj : ssidChildList_4){
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<station-level> */
			if(childObj instanceof DosStationLevel){
				DosStationLevel stationObj = (DosStationLevel)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type> */
				DosStationLevel.FrameType frameTypeObj = new DosStationLevel.FrameType();
				ssidChildList_5.add(frameTypeObj);
				stationObj.setFrameType(frameTypeObj);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level> */
			if(childObj instanceof DosSsidLevel){
				DosSsidLevel ssidLevelObj = (DosSsidLevel)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type> */
				DosSsidLevel.FrameType frameTypeObj = new DosSsidLevel.FrameType();
				ssidChildList_5.add(frameTypeObj);
				ssidLevelObj.setFrameType(frameTypeObj);
			}
		}
		ssidChildList_4.clear();
		generateSsidLevel_6();
	}
	
	private void generateSsidLevel_6() throws Exception{
		/**
		 * <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>			DosStationLevel.FrameType
		 * <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>				DosSsidLevel.FrameType
		 */
		for(Object childObj : ssidChildList_5){
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type> */
			if(childObj instanceof DosStationLevel.FrameType){
				DosStationLevel.FrameType frameTypeObj = (DosStationLevel.FrameType)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-req> */
				DosStationLevelProbeReq probeReqObj = new DosStationLevelProbeReq();
				ssidChildList_6.add(probeReqObj);
				frameTypeObj.setProbeReq(probeReqObj);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-resp> */
				DosStationLevelProbeResp probeRespObj = new DosStationLevelProbeResp();
				ssidChildList_6.add(probeRespObj);
				frameTypeObj.setProbeResp(probeRespObj);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-req> */
				DosStationLevelAssocReq assocReqObj = new DosStationLevelAssocReq();
				ssidChildList_6.add(assocReqObj);
				frameTypeObj.setAssocReq(assocReqObj);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-resp> */
				DosStationLevelAssocResp assocRespObj = new DosStationLevelAssocResp();
				ssidChildList_6.add(assocRespObj);
				frameTypeObj.setAssocResp(assocRespObj);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<auth> */
				DosStationLevelAuth authObj = new DosStationLevelAuth();
				ssidChildList_6.add(authObj);
				frameTypeObj.setAuth(authObj);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<deauth> */
				DosStationLevelDeauth deauthObj = new DosStationLevelDeauth();
				ssidChildList_6.add(deauthObj);
				frameTypeObj.setDeauth(deauthObj);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<disassoc> */
				DosStationLevelDisassoc disassocObj = new DosStationLevelDisassoc();
				ssidChildList_6.add(disassocObj);
				frameTypeObj.setDisassoc(disassocObj);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<eapol> */
				DosStationLevelEapol eapolObj = new DosStationLevelEapol();
				ssidChildList_6.add(eapolObj);
				frameTypeObj.setEapol(eapolObj);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type> */
			if(childObj instanceof DosSsidLevel.FrameType){
				DosSsidLevel.FrameType frameTypeObj = (DosSsidLevel.FrameType)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<probe-req> */
				DosSsidLevelProbeReq probeReqObj = new DosSsidLevelProbeReq();
				ssidChildList_6.add(probeReqObj);
				frameTypeObj.setProbeReq(probeReqObj);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<probe-resp> */
				DosSsidLevelProbeResp probeRespObj = new DosSsidLevelProbeResp();
				ssidChildList_6.add(probeRespObj);
				frameTypeObj.setProbeResp(probeRespObj);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<assoc-req> */
				DosSsidLevelAssocReq assocReqObj = new DosSsidLevelAssocReq();
				ssidChildList_6.add(assocReqObj);
				frameTypeObj.setAssocReq(assocReqObj);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<assoc-resp> */
				DosSsidLevelAssocResp assocRespObj = new DosSsidLevelAssocResp();
				ssidChildList_6.add(assocRespObj);
				frameTypeObj.setAssocResp(assocRespObj);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<auth> */
				DosSsidLevelAuth authObj = new DosSsidLevelAuth();
				ssidChildList_6.add(authObj);
				frameTypeObj.setAuth(authObj);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<deauth> */
				DosSsidLevelDeauth deauthObj = new DosSsidLevelDeauth();
				ssidChildList_6.add(deauthObj);
				frameTypeObj.setDeauth(deauthObj);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<disassoc> */
				DosSsidLevelDisassoc disassocObj = new DosSsidLevelDisassoc();
				ssidChildList_6.add(disassocObj);
				frameTypeObj.setDisassoc(disassocObj);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<eapol> */
				DosSsidLevelEapol eapolObj = new DosSsidLevelEapol();
				ssidChildList_6.add(eapolObj);
				frameTypeObj.setEapol(eapolObj);
			}
		}
		ssidChildList_5.clear();
		generateSsidLevel_7();
	}
	
	private void generateSsidLevel_7() throws Exception{
		/**
		 * <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-req>		DosStationLevelProbeReq
		 * <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-resp>		DosStationLevelProbeResp
		 * <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-req>		DosStationLevelAssocReq
		 * <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-resp>		DosStationLevelAssocResp
		 * <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<auth>			DosStationLevelAuth
		 * <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<deauth>			DosStationLevelDeauth
		 * <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<disassoc>		DosStationLevelDisassoc
		 * <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<eapol>			DosStationLevelEapol
		 * 
		 * <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<probe-req>			DosSsidLevelProbeReq
		 * <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<probe-resp>		DosSsidLevelProbeResp
		 * <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<assoc-req>			DosSsidLevelAssocReq
		 * <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<assoc-resp>		DosSsidLevelAssocResp
		 * <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<auth>				DosSsidLevelAuth
		 * <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<deauth>			DosSsidLevelDeauth
		 * <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<disassoc>			DosSsidLevelDisassoc
		 * <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<eapol>				DosSsidLevelEapol
		 */
		
		for(Object childObj : ssidChildList_6){
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-req> */
			if(childObj instanceof DosStationLevelProbeReq){
				DosStationLevelProbeReq probeReqObj = (DosStationLevelProbeReq)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-req>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/probe-req", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				probeReqObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithStationType(SsidProfileInt.FrameType.probe_req)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-req>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/probe-req", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithStationType(SsidProfileInt.FrameType.probe_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, ssidProfile.isYesDefault()}
				};
				probeReqObj.setThreshold(
						(DosStationLevelProbeReq.Threshold)CLICommonFunc.createObjectWithName(DosStationLevelProbeReq.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-req>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/probe-req", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithStationType(SsidProfileInt.FrameType.probe_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				probeReqObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-resp> */
			if(childObj instanceof DosStationLevelProbeResp){
				DosStationLevelProbeResp probeRespObj = (DosStationLevelProbeResp)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-resp>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/probe-resp", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				probeRespObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithStationType(SsidProfileInt.FrameType.probe_resp)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-resp>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/probe-resp", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithStationType(SsidProfileInt.FrameType.probe_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, ssidProfile.isYesDefault()}
				};
				probeRespObj.setThreshold(
						(DosStationLevelProbeResp.Threshold)CLICommonFunc.createObjectWithName(DosStationLevelProbeResp.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<probe-resp>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/probe-resp", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithStationType(SsidProfileInt.FrameType.probe_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, ssidProfile.isYesDefault()}
				};
				probeRespObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-req> */
			if(childObj instanceof DosStationLevelAssocReq){
				DosStationLevelAssocReq assocReqObj = (DosStationLevelAssocReq)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-req>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/assoc-req", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				assocReqObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithStationType(SsidProfileInt.FrameType.assoc_req)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-req>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/assoc-req", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithStationType(SsidProfileInt.FrameType.assoc_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocReqObj.setThreshold(
						(DosStationLevelAssocReq.Threshold)CLICommonFunc.createObjectWithName(DosStationLevelAssocReq.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-req>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/assoc-req", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithStationType(SsidProfileInt.FrameType.assoc_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocReqObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-req>.<ban> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/assoc-req", 
						"ban", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] banParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getBanValueWithStationType(SsidProfileInt.FrameType.assoc_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocReqObj.setBan(
						(DosBan)CLICommonFunc.createObjectWithName(DosBan.class, banParm)
				);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-resp> */
			if(childObj instanceof DosStationLevelAssocResp){
				DosStationLevelAssocResp assocRespObj = (DosStationLevelAssocResp)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-resp>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/assoc-resp", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				assocRespObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithStationType(SsidProfileInt.FrameType.assoc_resp)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-resp>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/assoc-resp", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithStationType(SsidProfileInt.FrameType.assoc_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocRespObj.setThreshold(
						(DosStationLevelAssocResp.Threshold)CLICommonFunc.createObjectWithName(
								DosStationLevelAssocResp.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<assoc-resp>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/assoc-resp", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithStationType(SsidProfileInt.FrameType.assoc_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocRespObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<auth> */
			if(childObj instanceof DosStationLevelAuth){
				DosStationLevelAuth authObj = (DosStationLevelAuth)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<auth>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/auth", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				authObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithStationType(SsidProfileInt.FrameType.auth)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<auth>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/auth", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithStationType(SsidProfileInt.FrameType.auth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				authObj.setThreshold(
						(DosStationLevelAuth.Threshold)CLICommonFunc.createObjectWithName(
								DosStationLevelAuth.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<auth>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/auth", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmObj = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithStationType(SsidProfileInt.FrameType.auth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				authObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmObj)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<auth>.<ban> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/auth", 
						"ban", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] banParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getBanValueWithStationType(SsidProfileInt.FrameType.auth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				authObj.setBan(
						(DosBan)CLICommonFunc.createObjectWithName(DosBan.class, banParm)
				);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<deauth> */
			if(childObj instanceof DosStationLevelDeauth){
				DosStationLevelDeauth deauthObj = (DosStationLevelDeauth)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<deauth>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/deauth", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				deauthObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithStationType(SsidProfileInt.FrameType.deauth)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<deauth>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/deauth", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithStationType(SsidProfileInt.FrameType.deauth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				deauthObj.setThreshold(
						(DosStationLevelDeauth.Threshold)CLICommonFunc.createObjectWithName(
								DosStationLevelDeauth.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<deauth>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/deauth", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithStationType(SsidProfileInt.FrameType.deauth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				deauthObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<disassoc> */
			if(childObj instanceof DosStationLevelDisassoc){
				DosStationLevelDisassoc disassocObj = (DosStationLevelDisassoc)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<disassoc>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/disassoc", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				disassocObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithStationType(SsidProfileInt.FrameType.disassoc)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<disassoc>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/disassoc", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithStationType(SsidProfileInt.FrameType.disassoc)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				disassocObj.setThreshold(
						(DosStationLevelDisassoc.Threshold)CLICommonFunc.createObjectWithName(DosStationLevelDisassoc.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<disassoc>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/disassoc", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithStationType(SsidProfileInt.FrameType.disassoc)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				disassocObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<eapol> */
			if(childObj instanceof DosStationLevelEapol){
				DosStationLevelEapol eapolObj = (DosStationLevelEapol)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<eapol>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/eapol", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				eapolObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithStationType(SsidProfileInt.FrameType.eapol)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<eapol>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/eapol", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithStationType(SsidProfileInt.FrameType.eapol)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				eapolObj.setThreshold(
						(DosStationLevelEapol.Threshold)CLICommonFunc.createObjectWithName(DosStationLevelEapol.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<eapol>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/eapol", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmObj = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithStationType(SsidProfileInt.FrameType.eapol)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				eapolObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmObj)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<station-level>.<frame-type>.<eapol>.<ban> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/station-level/frame-type/eapol", 
						"ban", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] banParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getBanValueWithStationType(SsidProfileInt.FrameType.eapol)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				eapolObj.setBan(
						(DosBan)CLICommonFunc.createObjectWithName(DosBan.class, banParm)
				);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<probe-req> */
			if(childObj instanceof DosSsidLevelProbeReq){
				DosSsidLevelProbeReq probeReqObj = (DosSsidLevelProbeReq)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<probe-req>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/probe-req", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				probeReqObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithSsidType(SsidProfileInt.FrameType.probe_req)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<probe-req>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/probe-req", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithSsidType(SsidProfileInt.FrameType.probe_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				probeReqObj.setThreshold(
						(DosSsidLevelProbeReq.Threshold)CLICommonFunc.createObjectWithName(DosSsidLevelProbeReq.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<probe-req>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/probe-req", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmObj = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithSsidType(SsidProfileInt.FrameType.probe_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				probeReqObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmObj)
				);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<probe-resp> */
			if(childObj instanceof DosSsidLevelProbeResp){
				DosSsidLevelProbeResp probeRespObj = (DosSsidLevelProbeResp)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<probe-resp>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/probe-resp", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				probeRespObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithSsidType(SsidProfileInt.FrameType.probe_resp)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<probe-resp>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/probe-resp", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithSsidType(SsidProfileInt.FrameType.probe_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				probeRespObj.setThreshold(
						(DosSsidLevelProbeResp.Threshold)CLICommonFunc.createObjectWithName(DosSsidLevelProbeResp.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<probe-resp>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/probe-resp", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithSsidType(SsidProfileInt.FrameType.probe_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				probeRespObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<assoc-req> */
			if(childObj instanceof DosSsidLevelAssocReq){
				DosSsidLevelAssocReq assocReqObj = (DosSsidLevelAssocReq)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<assoc-req>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/assoc-req", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				assocReqObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithSsidType(SsidProfileInt.FrameType.assoc_req)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<assoc-req>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/assoc-req", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithSsidType(SsidProfileInt.FrameType.assoc_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocReqObj.setThreshold(
						(DosSsidLevelAssocReq.Threshold)CLICommonFunc.createObjectWithName(DosSsidLevelAssocReq.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<assoc-req>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/assoc-req", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithSsidType(SsidProfileInt.FrameType.assoc_req)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocReqObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<assoc-resp> */
			if(childObj instanceof DosSsidLevelAssocResp){
				DosSsidLevelAssocResp assocRespObj = (DosSsidLevelAssocResp)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<assoc-resp>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/assoc-resp", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				assocRespObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithSsidType(SsidProfileInt.FrameType.assoc_resp)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<assoc-resp>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/assoc-resp", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithSsidType(SsidProfileInt.FrameType.assoc_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocRespObj.setThreshold(
						(DosSsidLevelAssocResp.Threshold)CLICommonFunc.createObjectWithName(DosSsidLevelAssocResp.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<assoc-resp>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/assoc-resp", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithSsidType(SsidProfileInt.FrameType.assoc_resp)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				assocRespObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<auth> */
			if(childObj instanceof DosSsidLevelAuth){
				DosSsidLevelAuth authObj = (DosSsidLevelAuth)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<auth>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/auth", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				authObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithSsidType(SsidProfileInt.FrameType.auth)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<auth>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/auth", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithSsidType(SsidProfileInt.FrameType.auth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				authObj.setThreshold(
						(DosSsidLevelAuth.Threshold)CLICommonFunc.createObjectWithName(DosSsidLevelAuth.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<auth>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/auth", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithSsidType(SsidProfileInt.FrameType.auth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				authObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<deauth> */
			if(childObj instanceof DosSsidLevelDeauth){
				DosSsidLevelDeauth deauthObj = (DosSsidLevelDeauth)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<deauth>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/deauth", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				deauthObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithSsidType(SsidProfileInt.FrameType.deauth)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<deauth>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/deauth", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithSsidType(SsidProfileInt.FrameType.deauth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				deauthObj.setThreshold(
						(DosSsidLevelDeauth.Threshold)CLICommonFunc.createObjectWithName(DosSsidLevelDeauth.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<deauth>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/deauth", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmObj = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithSsidType(SsidProfileInt.FrameType.deauth)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				deauthObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmObj)
				);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<disassoc> */
			if(childObj instanceof DosSsidLevelDisassoc){
				DosSsidLevelDisassoc disassocObj = (DosSsidLevelDisassoc)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<disassoc>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/disassoc", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				disassocObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithSsidType(SsidProfileInt.FrameType.disassoc)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<disassoc>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/disassoc", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithSsidType(SsidProfileInt.FrameType.disassoc)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				disassocObj.setThreshold(
						(DosSsidLevelDisassoc.Threshold)CLICommonFunc.createObjectWithName(DosSsidLevelDisassoc.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<disassoc>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/disassoc", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithSsidType(SsidProfileInt.FrameType.disassoc)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				disassocObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
			
			/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<eapol> */
			if(childObj instanceof DosSsidLevelEapol){
				DosSsidLevelEapol eapolObj = (DosSsidLevelEapol)childObj;
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<eapol>.<cr> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/eapol", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				eapolObj.setCr(CLICommonFunc.getAhOnlyAct(ssidProfile.isEnableWithSsidType(SsidProfileInt.FrameType.eapol)));
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<eapol>.<threshold> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/eapol", 
						"threshold", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getThresholdValueWithSsidType(SsidProfileInt.FrameType.eapol)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				eapolObj.setThreshold(
						(DosSsidLevelEapol.Threshold)CLICommonFunc.createObjectWithName(DosSsidLevelEapol.Threshold.class, thresholdParm)
				);
				
				/** element: <ssid>.<security>.<wlan>.<dos>.<ssid-level>.<frame-type>.<eapol>.<alarm> */
				oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/security/wlan/dos/ssid-level/frame-type/eapol", 
						"alarm", GenerateXMLDebug.SET_VALUE,
						ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
				Object[][] alarmParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getAlarmValueWithSsidType(SsidProfileInt.FrameType.eapol)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				eapolObj.setAlarm(
						(DosAlarm)CLICommonFunc.createObjectWithName(DosAlarm.class, alarmParm)
				);
			}
		
		}
	}
	
	private ScreeningAction createScreeningAction(String screeningType) throws Exception{
		ScreeningAction screenObj = new ScreeningAction();
		
		/** attribute: operation */
		screenObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <ban-forever> */
		if(ssidProfile.isConfigureActionWithScreen(screeningType, SsidProfileInt.SCREENING_ACTION_BAN_FOREVER)){
			screenObj.setBanForever(CLICommonFunc.getAhOnlyAct(ssidProfile.isYesDefault()));
		}
		
		/** element: <disconnect> */
		if(ssidProfile.isConfigureActionWithScreen(screeningType, SsidProfileInt.SCREENING_ACTION_DISCONNECT)){
			screenObj.setDisconnect(CLICommonFunc.getAhOnlyAct(ssidProfile.isYesDefault()));
		}
		
		/** element: <alarm> */
		if(ssidProfile.isConfigureActionWithScreen(screeningType, SsidProfileInt.SCREENING_ACTION_ALARM)){
			Object[][] parmAlarm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getScreenActionValue(screeningType, SsidProfileInt.SCREENING_ACTION_ALARM)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, ssidProfile.isYesDefault()}
			};
			screenObj.setAlarm(
					(ScreeningAction.Alarm)CLICommonFunc.createObjectWithName(ScreeningAction.Alarm.class, parmAlarm)
			);
		}
		
		/** element: <ban> */
		if(ssidProfile.isConfigureActionWithScreen(screeningType, SsidProfileInt.SCREENING_ACTION_BAN)){
			Object[][] parmBan = {
					{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getScreenActionValue(screeningType, SsidProfileInt.SCREENING_ACTION_BAN)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, ssidProfile.isYesDefault()}
			};
			screenObj.setBan(
					(ScreeningAction.Ban)CLICommonFunc.createObjectWithName(ScreeningAction.Ban.class, parmBan)
			);
		}
		
		/** element: <drop> */
		if(ssidProfile.isConfigureActionWithScreen(screeningType, SsidProfileInt.SCREENING_ACTION_DROP)){
			Object[][] parmDrop = {
					{CLICommonFunc.ATTRIBUTE_VALUE, ssidProfile.getScreenActionValue(screeningType, SsidProfileInt.SCREENING_ACTION_DROP)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, ssidProfile.isYesDefault()}
			};
			screenObj.setDrop(
					(ScreeningAction.Drop)CLICommonFunc.createObjectWithName(ScreeningAction.Drop.class, parmDrop)
			);
		}
		
		return screenObj;
	}
	
	/**
	 * create ssid admctl ac number
	 * @param index
	 * @return
	 */
	private SsidAdmctlAc createSsidAdmctlAc(int index){
		SsidAdmctlAc admctlAc = new SsidAdmctlAc();
		/** attribute: value*/
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/admctl",
				"ac", GenerateXMLDebug.SET_VALUE,
				ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		admctlAc.setName(index);
		
		/** element: <ssid>.<admctl>.<ac>.<enable> */
		oDebug.debug("/configuration/ssid[@name='"+treeSsid.getName()+"']/admctl/ac",
				"enable", GenerateXMLDebug.CONFIG_ELEMENT,
				ssidProfile.getSsidGuiKey(), ssidProfile.getSsidName());
		admctlAc.setEnable(CLICommonFunc.getAhOnlyAct(ssidProfile.isConfigAcNumber(index)));
		return admctlAc;
	}
}
