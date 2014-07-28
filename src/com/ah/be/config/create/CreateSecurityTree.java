package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.SecurityProfileInt;
import com.ah.xml.be.config.MacFilterAction;
import com.ah.xml.be.config.MacFilterType;
import com.ah.xml.be.config.SecurityObj;
import com.ah.xml.be.config.WlanIdpMitigate;
import com.ah.xml.be.config.WlanIdpMitigateDeauthTime;
import com.ah.xml.be.config.WlanIdpMitigateDuration;
import com.ah.xml.be.config.WlanIdpMitigatePeriod;
import com.ah.xml.be.config.WlanIdpMitigateQuietTime;
import com.ah.xml.be.config.WlanIdpStaReport;
import com.ah.xml.be.config.WlanIdpStaReportAgeout;
import com.ah.xml.be.config.WlanIdpStaReportDuration;
import com.ah.xml.be.config.WlanIdpStaReportInterval;

/**
 * 
 * @author zhang
 *
 */
public class CreateSecurityTree {
	
	private SecurityProfileInt securityImpl;
	private SecurityObj securityObj;
	
	private GenerateXMLDebug oDebug;
	
	private List<Object> securityChildList_1 = new ArrayList<Object>();
	private List<Object> securityChildList_2 = new ArrayList<Object>();
	private List<Object> securityChildList_3 = new ArrayList<Object>();
	private List<Object> securityChildList_4 = new ArrayList<Object>();
	private List<Object> securityChildList_5 = new ArrayList<Object>();

	public CreateSecurityTree(SecurityProfileInt securityImpl, GenerateXMLDebug oDebug) {
		this.securityImpl = securityImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		if(securityImpl.isConfigureSecurity()){
			securityObj = new SecurityObj();
			generateSecurityLevel_1();
		}
	}
	
	public SecurityObj getSecurityObj(){
		return this.securityObj;
	}
	
	private void generateSecurityLevel_1() throws Exception {
		
//		/** attribute: updateTime*/
//		securityObj.setUpdateTime(securityObj.getUpdateTime());
		
		/** element: <mac-filter>*/
		for(int i=0; i<securityImpl.getMacFilterBindSize(); i++){
			SecurityObj.MacFilter macFilterObj = new SecurityObj.MacFilter();
			setMacFilter(macFilterObj, i);
			securityObj.getMacFilter().add(macFilterObj);
		}
		
		/** element: <security>.<wlan-idp> */
		if(securityImpl.isConfigureWlanIdp()){
			SecurityObj.WlanIdp wlanIdpObj = new SecurityObj.WlanIdp();
			securityChildList_1.add(wlanIdpObj);
			securityObj.setWlanIdp(wlanIdpObj);
		}
		
		generateSecurityLevel_2();
	}
	
	private void generateSecurityLevel_2() throws Exception {
		/**
		 * <security>.<wlan-idp>		SecurityObj.WlanIdp
		 */
		for(Object childObj : securityChildList_1){
			
			/** element: <security>.<wlan-idp> */
			if(childObj instanceof SecurityObj.WlanIdp){
				SecurityObj.WlanIdp wlanIdpObj = (SecurityObj.WlanIdp)childObj;
				
				/** attribute: updateTime */
				wlanIdpObj.setUpdateTime(securityImpl.getWlanIdpUpdateTime());
				
				/** element: <security>.<wlan-idp>.<profile> */
				SecurityObj.WlanIdp.Profile profileObj = new SecurityObj.WlanIdp.Profile();
				securityChildList_2.add(profileObj);
				wlanIdpObj.getProfile().add(profileObj);
			}
		}
		generateSecurityLevel_3();
	}
	
	private void generateSecurityLevel_3() throws Exception {
		/***
		 * <security>.<wlan-idp>.<profile>		SecurityObj.WlanIdp.Profile
		 */
		for(Object childObj : securityChildList_2){
			
			/** element: <security>.<wlan-idp>.<profile> */
			if(childObj instanceof SecurityObj.WlanIdp.Profile){
				SecurityObj.WlanIdp.Profile profileObj = (SecurityObj.WlanIdp.Profile)childObj;
				
				/** attribute: name */
				oDebug.debug("/configuration/security/wlan-idp",
						"profile", GenerateXMLDebug.SET_NAME,
						securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
				profileObj.setName(securityImpl.getWlanIdpProfileName());
				
				/** attribute: operation */
				profileObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
				
				/** element: <security>.<wlan-idp>.<profile>.<cr> */
				profileObj.setCr("");
				
				/** element: <security>.<wlan-idp>.<profile>.<adhoc> */
				oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']",
						"adhoc", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
				profileObj.setAdhoc(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableWlanIdpAdhoc()));
				
				/** element: <security>.<wlan-idp>.<profile>.<ap-detection> */
//				if(securityImpl.isConfigureApDetection()){
				SecurityObj.WlanIdp.Profile.ApDetection apDetectionObj = new SecurityObj.WlanIdp.Profile.ApDetection();
				securityChildList_3.add(apDetectionObj);
				profileObj.setApDetection(apDetectionObj);
//				}
				
				/** element: <security>.<wlan-idp>.<profile>.<ap-policy> */
				SecurityObj.WlanIdp.Profile.ApPolicy apPolicyObj = new SecurityObj.WlanIdp.Profile.ApPolicy();
				securityChildList_3.add(apPolicyObj);
				profileObj.setApPolicy(apPolicyObj);
				
				/** element: <security>.<wlan-idp>.<profile>.<old-mitigate> */
				SecurityObj.WlanIdp.Profile.OldMitigate oldMitigateObj = new SecurityObj.WlanIdp.Profile.OldMitigate();
				securityChildList_3.add(oldMitigateObj);
				profileObj.setOldMitigate(oldMitigateObj);
				
				/** element: <security>.<wlan-idp>.<profile>.<mitigate> */
				WlanIdpMitigate mitigateObj = new WlanIdpMitigate();
				securityChildList_3.add(mitigateObj);
				profileObj.setMitigate(mitigateObj);
				
				/** element: <security>.<wlan-idp>.<profile>.<sta-report> */
				WlanIdpStaReport staReportObj = new WlanIdpStaReport();
				securityChildList_3.add(staReportObj);
				profileObj.setStaReport(staReportObj);
			}
		}
		generateSecurityLevel_4();
	}
	
	private void generateSecurityLevel_4() throws Exception {
		/**
		 * <security>.<wlan-idp>.<profile>.<ap-detection>		SecurityObj.WlanIdp.Profile.ApDetection
		 * <security>.<wlan-idp>.<profile>.<ap-policy>			SecurityObj.WlanIdp.Profile.ApPolicy
		 * <security>.<wlan-idp>.<profile>.<old-mitigate>		SecurityObj.WlanIdp.Profile.OldMitigate
		 * <security>.<wlan-idp>.<profile>.<mitigate>			WlanIdpMitigate
		 * <security>.<wlan-idp>.<profile>.<sta-report>			WlanIdpStaReport
		 */
		for(Object childObj : securityChildList_3 ){
			
			/** element: <security>.<wlan-idp>.<profile>.<ap-detection> */
			if(childObj instanceof SecurityObj.WlanIdp.Profile.ApDetection){
				SecurityObj.WlanIdp.Profile.ApDetection apDetectionObj = (SecurityObj.WlanIdp.Profile.ApDetection)childObj;
				
				/** element: <security>.<wlan-idp>.<profile>.<ap-detection>.<connected> */
				SecurityObj.WlanIdp.Profile.ApDetection.Connected connectedObj = new SecurityObj.WlanIdp.Profile.ApDetection.Connected();
				securityChildList_4.add(connectedObj);
				apDetectionObj.setConnected(connectedObj);
				/** element: <security>.<wlan-idp>.<profile>.<ap-detection>.<client-mac-in-net> */
				apDetectionObj.setClientMacInNet(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableConnected()));
				
			}
			
			/** element: <security>.<wlan-idp>.<profile>.<ap-policy> */
			if(childObj instanceof SecurityObj.WlanIdp.Profile.ApPolicy){
				SecurityObj.WlanIdp.Profile.ApPolicy apPolicyObj = (SecurityObj.WlanIdp.Profile.ApPolicy)childObj;
				
//				/** attribute: operation */
//				apPolicyObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security>.<wlan-idp>.<profile>.<ap-policy>.<cr> */
				oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-policy",
						"cr", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
				apPolicyObj.setCr(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableApPolicy()));
				
				/** element: <security>.<wlan-idp>.<profile>.<ap-policy>.<short-beacon> */
				oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-policy",
						"short-beacon", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
				apPolicyObj.setShortBeacon(
						CLICommonFunc.getAhOnlyAct(securityImpl.isEnableShortBeacon())
				);
				
				/** element: <security>.<wlan-idp>.<profile>.<ap-policy>.<short-preamble> */
				oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-policy",
						"short-preamble", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
				apPolicyObj.setShortPreamble(
						CLICommonFunc.getAhOnlyAct(securityImpl.isEnableShortPreamble())
				);
				
				/** element: <security>.<wlan-idp>.<profile>.<ap-policy>.<wmm> */
				oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-policy",
						"wmm", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
				apPolicyObj.setWmm(
						CLICommonFunc.getAhOnlyAct(securityImpl.isEnableWmm())
				);
				
				/** element: <security>.<wlan-idp>.<profile>.<ap-policy>.<ap-oui> */
				SecurityObj.WlanIdp.Profile.ApPolicy.ApOui apOuiObj = new SecurityObj.WlanIdp.Profile.ApPolicy.ApOui();
				securityChildList_4.add(apOuiObj);
				apPolicyObj.setApOui(apOuiObj);
				
				/** element: <security>.<wlan-idp>.<profile>.<ap-policy>.<ssid> */
				SecurityObj.WlanIdp.Profile.ApPolicy.Ssid ssidObj = new SecurityObj.WlanIdp.Profile.ApPolicy.Ssid();
				securityChildList_4.add(ssidObj);
				apPolicyObj.setSsid(ssidObj);
			}
			
			/** element: <security>.<wlan-idp>.<profile>.<old-mitigate>	*/
			if(childObj instanceof SecurityObj.WlanIdp.Profile.OldMitigate){
				SecurityObj.WlanIdp.Profile.OldMitigate oldMitigateObj = (SecurityObj.WlanIdp.Profile.OldMitigate)childObj;
				
				/** attribute: operation */
				oldMitigateObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security>.<wlan-idp>.<profile>.<old-mitigate>.<period> */
				SecurityObj.WlanIdp.Profile.OldMitigate.Period periodObj = new SecurityObj.WlanIdp.Profile.OldMitigate.Period();
				securityChildList_4.add(periodObj);
				oldMitigateObj.setPeriod(periodObj);
			}
			
			/** element: <security>.<wlan-idp>.<profile>.<mitigate>	*/
			if(childObj instanceof WlanIdpMitigate){
				WlanIdpMitigate mitigateObj = (WlanIdpMitigate)childObj;
				
				/** element: <security>.<wlan-idp>.<profile>.<mitigate>.<period> */
				Object[][] periodParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getIDSPolicyPeriod()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				mitigateObj.setPeriod(
						(WlanIdpMitigatePeriod)CLICommonFunc.createObjectWithName(WlanIdpMitigatePeriod.class, periodParm)
				);
				
				/** element: <security>.<wlan-idp>.<profile>.<mitigate>.<duration> */
				WlanIdpMitigateDuration durationObj = new WlanIdpMitigateDuration();
				securityChildList_4.add(durationObj);
				mitigateObj.setDuration(durationObj);
				
				/** element: <security>.<wlan-idp>.<profile>.<mitigate>.<deauth-time> */
				Object[][] deauthTimeParam = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getMitigateDeauthTime()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				mitigateObj.setDeauthTime((WlanIdpMitigateDeauthTime)CLICommonFunc.createObjectWithName(
						WlanIdpMitigateDeauthTime.class, deauthTimeParam));
			}
			
			/** element: <security>.<wlan-idp>.<profile>.<sta-report> */
			if(childObj instanceof WlanIdpStaReport){
				WlanIdpStaReport staReportObj = (WlanIdpStaReport)childObj;
				
				/** element: <security>.<wlan-idp>.<profile>.<sta-report>.<cr> */
				staReportObj.setCr(CLICommonFunc.getAhOnlyAct(securityImpl.isStaReportEnable()));
				
				/** element: <security>.<wlan-idp>.<profile>.<sta-report>.<duration> */
				if(securityImpl.isStaReportEnable()){
					WlanIdpStaReportDuration durationObj = new WlanIdpStaReportDuration();
					securityChildList_4.add(durationObj);
					staReportObj.setDuration(durationObj);
				}
				/** element: <security>.<wlan-idp>.<profile>.<sta-report>.<age-time> */
				if(securityImpl.isStaReportEnable()){
					staReportObj.setAgeTime(CLICommonFunc.createAhIntActObj(securityImpl.getStaReportAgeTime(), true));
				}
			}
		}
		generateSecurityLevel_5();
	}
	
	private void generateSecurityLevel_5() throws Exception {
		/**
		 * <security>.<wlan-idp>.<profile>.<ap-detection>.<connected>		SecurityObj.WlanIdp.Profile.ApDetection.Connected
		 * <security>.<wlan-idp>.<profile>.<ap-policy>.<apOui>				SecurityObj.WlanIdp.Profile.ApPolicy.ApOui
		 * <security>.<wlan-idp>.<profile>.<ap-policy>.<ssid>				SecurityObj.WlanIdp.Profile.ApPolicy.Ssid
		 * <security>.<wlan-idp>.<profile>.<old-mitigate>.<period>			SecurityObj.WlanIdp.Profile.OldMitigate.Period
		 * <security>.<wlan-idp>.<profile>.<mitigate>.<duration>			WlanIdpMitigateDuration
		 * <security>.<wlan-idp>.<profile>.<sta-report>.<duration>			WlanIdpStaReportDuration
		 */
		for(Object childObj : securityChildList_4){
			
			/** element: <security>.<wlan-idp>.<profile>.<ap-detection>.<connected> */
			if(childObj instanceof SecurityObj.WlanIdp.Profile.ApDetection.Connected){
				SecurityObj.WlanIdp.Profile.ApDetection.Connected connectedObj = (SecurityObj.WlanIdp.Profile.ApDetection.Connected)childObj;
				
				/** element: <security>.<wlan-idp>.<profile>.<ap-detection>.<connected>.<cr> */
				oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-detection/connected",
						"cr", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
				connectedObj.setCr(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableConnected()));
			}
			
			/** element: <security>.<wlan-idp>.<profile>.<ap-policy>.<apOui> */
			if(childObj instanceof SecurityObj.WlanIdp.Profile.ApPolicy.ApOui){
				SecurityObj.WlanIdp.Profile.ApPolicy.ApOui apOutObj = (SecurityObj.WlanIdp.Profile.ApPolicy.ApOui)childObj;
				
				/** element: <security>.<wlan-idp>.<profile>.<ap-policy>.<ap-oui>.<cr> */
				oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-policy/ap-oui",
						"cr", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
				apOutObj.setCr(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableApOui()));
				
				oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-policy/ap-oui",
						"entry", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
				if(securityImpl.isEnableApOui()){
					
					/** element: <security>.<wlan-idp>.<profile>.<ap-policy>.<apOui>.<entry> */
					for(int i=0; i<securityImpl.getApOuiSize(); i++){
						oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-policy/ap-oui",
								"entry", GenerateXMLDebug.SET_NAME,
								securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
						apOutObj.getEntry().add(
								CLICommonFunc.createAhNameActValue(securityImpl.getApOuiAddress(i), CLICommonFunc.getYesDefault())
						);
					}
				}
			}
			
			/** element: <security>.<wlan-idp>.<profile>.<ap-policy>.<ssid> */
			if(childObj instanceof SecurityObj.WlanIdp.Profile.ApPolicy.Ssid){
				SecurityObj.WlanIdp.Profile.ApPolicy.Ssid ssidObj = (SecurityObj.WlanIdp.Profile.ApPolicy.Ssid)childObj;
				
				/** element: <security>.<wlan-idp>.<profile>.<ap-policy>.<ssid>.<cr> */
				oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-policy/ssid",
						"cr", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
				ssidObj.setCr(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableApPolicySsid()));
				
				oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-policy/ssid",
						"entry", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
				if(securityImpl.isEnableApPolicySsid()){
					
					/** element: <security>.<wlan-idp>.<profile>.<ap-policy>.<ssid>.<entry> */
					for(int i=0; i<securityImpl.getApPolicySsidSize(); i++){
						ssidObj.getEntry().add(createSsidEntry(i));
					}
				}
			}
			
			/** element: <security>.<wlan-idp>.<profile>.<old-mitigate>.<period> */
			if(childObj instanceof SecurityObj.WlanIdp.Profile.OldMitigate.Period){
				SecurityObj.WlanIdp.Profile.OldMitigate.Period periodObj = (SecurityObj.WlanIdp.Profile.OldMitigate.Period)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/mitigate",
						"period", GenerateXMLDebug.SET_VALUE,
						securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
				periodObj.setValue(securityImpl.getIDSPolicyPeriod());
				
				/** element: <security>.<wlan-idp>.<profile>.<old-mitigate>.<period>.<duration> */
				SecurityObj.WlanIdp.Profile.OldMitigate.Period.Duration durationObj = new SecurityObj.WlanIdp.Profile.OldMitigate.Period.Duration();
				securityChildList_5.add(durationObj);
				periodObj.setDuration(durationObj);
			}
			
			/** element: <security>.<wlan-idp>.<profile>.<mitigate>.<duration> */
			if(childObj instanceof WlanIdpMitigateDuration){
				WlanIdpMitigateDuration durationObj = (WlanIdpMitigateDuration)childObj;
				
				/** attribute: operation */
				durationObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				durationObj.setValue(securityImpl.getIDSPolicyDuration());
				
				/** element: <security>.<wlan-idp>.<profile>.<mitigate>.<duration>.<quiet-time> */
				Object[][] timeParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getIDSPolicyQuietTime()}
				};
				durationObj.setQuietTime(
						(WlanIdpMitigateQuietTime)CLICommonFunc.createObjectWithName(WlanIdpMitigateQuietTime.class, timeParm)
				);
			}
			
			/** element: <security>.<wlan-idp>.<profile>.<sta-report>.<duration> */
			if(childObj instanceof WlanIdpStaReportDuration){
				WlanIdpStaReportDuration durationObj = (WlanIdpStaReportDuration)childObj;
				
				/** attribute: value */
				durationObj.setValue(securityImpl.getStaReportDuration());
				
				/** attribute: operation */
				durationObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security>.<wlan-idp>.<profile>.<sta-report>.<duration>.<interval> */
				WlanIdpStaReportInterval intervalObj = new WlanIdpStaReportInterval();
				securityChildList_5.add(intervalObj);
				durationObj.setInterval(intervalObj);
			}
		}
		securityChildList_4.clear();
		generateSecurityLevel_6();
	}
	
	private void generateSecurityLevel_6() throws Exception {
		/**
		 * <security>.<wlan-idp>.<profile>.<old-mitigate>.<period>.<duration>	SecurityObj.WlanIdp.Profile.OldMitigate.Period.Duration
		 * <security>.<wlan-idp>.<profile>.<sta-report>.<duration>.<interval>	WlanIdpStaReportInterval
		 */
		for(Object childObj : securityChildList_5){
			
			/** element: <security>.<wlan-idp>.<profile>.<old-mitigate>.<period>.<duration> */
			if(childObj instanceof SecurityObj.WlanIdp.Profile.OldMitigate.Period.Duration){
				SecurityObj.WlanIdp.Profile.OldMitigate.Period.Duration durationObj = (SecurityObj.WlanIdp.Profile.OldMitigate.Period.Duration)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/mitigate/period",
						"duration", GenerateXMLDebug.SET_VALUE,
						securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
				durationObj.setValue(securityImpl.getIDSPolicyDuration());
				
				/** element: <security>.<wlan-idp>.<profile>.<old-mitigate>.<period>.<duration>.<quiet-time> */
				oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/mitigate/period/duration",
						"quiet-time", GenerateXMLDebug.SET_VALUE,
						securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
				Object[][] quietTimeParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getIDSPolicyQuietTime()}
				};
				durationObj.setQuietTime(
						(SecurityObj.WlanIdp.Profile.OldMitigate.Period.Duration.QuietTime)CLICommonFunc.createObjectWithName(
								SecurityObj.WlanIdp.Profile.OldMitigate.Period.Duration.QuietTime.class, quietTimeParm)
				);
			}
			
			/** element: <security>.<wlan-idp>.<profile>.<sta-report>.<duration>.<interval>	*/
			if(childObj instanceof WlanIdpStaReportInterval){
				WlanIdpStaReportInterval intervalObj = (WlanIdpStaReportInterval)childObj;
				
				/** attribute: value */
				intervalObj.setValue(securityImpl.getStaReportInterval());
				
				/** element: <security>.<wlan-idp>.<profile>.<sta-report>.<duration>.<interval>.<ageout> */
				Object[][] ageoutParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getStaReportAgeout()}
				};
				intervalObj.setAgeout(
						(WlanIdpStaReportAgeout)CLICommonFunc.createObjectWithName(WlanIdpStaReportAgeout.class, ageoutParm)
				);
			}
		}
		securityChildList_5.clear();
	}
	
	private void setMacFilter(SecurityObj.MacFilter macFilterObj, int i) throws Exception {
		
		/** attribute: name */
		oDebug.debug("/configuration/security", 
				"mac-filter", GenerateXMLDebug.SET_NAME,
				securityImpl.getMacFilterGuiName(), securityImpl.getMacFilterName(i));
		macFilterObj.setName(securityImpl.getMacFilterName(i));
		
		/** attribute: operation */
		macFilterObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <security>.<mac-filter>.<default> */
		SecurityObj.MacFilter.Default macDefault = new SecurityObj.MacFilter.Default();
		
		oDebug.debug("/configuration/security/mac-filter[@name='"+securityImpl.getMacFilterName(i)+"']", 
				"default", GenerateXMLDebug.SET_VALUE,
				securityImpl.getMacFilterGuiName(), securityImpl.getMacFilterName(i));
		macDefault.setValue(securityImpl.getDefaultActionValue(i));

		macFilterObj.setDefault(macDefault);
		
		/** element: <security>.<mac-filter>.<address> */
		for(int j=0; j<securityImpl.getMacFilterAddressSize(i); j++){
			MacFilterType addressType = new MacFilterType();
			macFilterObj.getAddress().add(addressType);
			
			/** attribute: name */
			oDebug.debug("/configuration/security/mac-filter[@name='"+securityImpl.getMacFilterName(i)+"']", 
					"address", GenerateXMLDebug.SET_NAME,
					securityImpl.getMacFilterGuiName(), securityImpl.getMacFilterName(i));
			addressType.setName(
					securityImpl.getMacAddress(i,j)
			);
			
			/** attribute: <address>.operation */
			addressType.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));

			if(securityImpl.isAddressActionIsPermit(i,j) ){
				/** element: <address>.<permit> */
				addressType.setPermit(new MacFilterAction() );
			}else{
				/** element: <address>.<deny> */
				addressType.setDeny(new MacFilterAction() );
			}
		}
		
		/** element: <security>.<mac-filter>.<oui> */
		for(int j=0; j<securityImpl.getMacFilterOuiSize(i); j++){
			MacFilterType ouiType = new MacFilterType();
			macFilterObj.getOui().add(ouiType);
			
			/** attribute: name */
			oDebug.debug("/configuration/security/mac-filter[@name='"+securityImpl.getMacFilterName(i)+"']", 
					"oui", GenerateXMLDebug.SET_NAME,
					securityImpl.getMacFilterGuiName(), securityImpl.getMacFilterName(i));
			ouiType.setName(
					securityImpl.getMacOui(i,j)
			);
			
			/** attribute: <oui>.operation */
			ouiType.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
			
			if(securityImpl.isOuiActionIsPermit(i,j)){
				/** element: <oui>.<permit> */
				ouiType.setPermit(new MacFilterAction() );
			}else{
				/** element: <oui>.<deny> */
				ouiType.setDeny(new MacFilterAction() );
			}
		}

	}
	
	private SecurityObj.WlanIdp.Profile.ApDetection.Connected.Vlan createConnectedVlan(int index) throws Exception {
		SecurityObj.WlanIdp.Profile.ApDetection.Connected.Vlan vlanObj = new SecurityObj.WlanIdp.Profile.ApDetection.Connected.Vlan();
		
		/** attribute: name */
		oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-detection/connected",
				"vlan", GenerateXMLDebug.SET_NAME,
				securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
		vlanObj.setName(securityImpl.getConnectedVlanName(index));
		
		/** attribute: operation */
		vlanObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		return vlanObj;
	}
	
	private SecurityObj.WlanIdp.Profile.ApPolicy.Ssid.Entry createSsidEntry(int index){
		SecurityObj.WlanIdp.Profile.ApPolicy.Ssid.Entry ssidEntryObj = new SecurityObj.WlanIdp.Profile.ApPolicy.Ssid.Entry();
		
		/** attribute: name */
		oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-policy/ssid",
				"entry", GenerateXMLDebug.SET_NAME,
				securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
		ssidEntryObj.setName(securityImpl.getApPolicySsidName(index));
		
		/** attribute: operation */
		ssidEntryObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <cr> */
		ssidEntryObj.setCr("");
		
		/** element: encryption */
		{
			SecurityObj.WlanIdp.Profile.ApPolicy.Ssid.Entry.Encryption encryptionObj = 
				new SecurityObj.WlanIdp.Profile.ApPolicy.Ssid.Entry.Encryption();
			ssidEntryObj.setEncryption(encryptionObj);
			
			/** element: <cr> */
			oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-policy/ssid/entry[@name='"+securityImpl.getApPolicySsidName(index)+"']",
					"cr", GenerateXMLDebug.SET_OPERATION,
					securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
			encryptionObj.setCr(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableEncryption(index)));
			
			/** element: <open> */
			oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-policy/ssid/entry[@name='"+securityImpl.getApPolicySsidName(index)+"']",
					"open", GenerateXMLDebug.CONFIG_ELEMENT,
					securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
			if(securityImpl.isConfigureEncryptionType(index, SecurityProfileInt.ENCRYPTION_TYPE_OPEN)){
				encryptionObj.setOpen(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <wep> */
			oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-policy/ssid/entry[@name='"+securityImpl.getApPolicySsidName(index)+"']",
					"wep", GenerateXMLDebug.CONFIG_ELEMENT,
					securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
			if(securityImpl.isConfigureEncryptionType(index, SecurityProfileInt.ENCRYPTION_TYPE_WEP)){
				encryptionObj.setWep(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <wpa> */
			oDebug.debug("/configuration/security/wlan-idp/profile[@name='"+securityImpl.getWlanIdpProfileName()+"']/ap-policy/ssid/entry[@name='"+securityImpl.getApPolicySsidName(index)+"']",
					"wpa", GenerateXMLDebug.CONFIG_ELEMENT,
					securityImpl.getIdsGuiName(), securityImpl.getWlanIdpProfileName());
			if(securityImpl.isConfigureEncryptionType(index, SecurityProfileInt.ENCRYPTION_TYPE_WPA)){
				encryptionObj.setWpa(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
		}
		
		return ssidEntryObj;
	}
}
