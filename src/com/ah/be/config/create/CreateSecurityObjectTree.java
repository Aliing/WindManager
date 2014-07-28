package com.ah.be.config.create;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.AAAProfileInt;
import com.ah.be.config.create.source.SecurityObjectProfileInt;
import com.ah.be.config.create.source.SsidProfileInt;
import com.ah.be.config.create.source.SsidProfileInt.AuthMethodType;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.xml.be.config.AaaAcctPort;
import com.ah.xml.be.config.AaaAuthPort;
import com.ah.xml.be.config.AaaUserProfileMapping;
import com.ah.xml.be.config.AhAuthMethod;
import com.ah.xml.be.config.AuthMethodMobileDeviceManager;
import com.ah.xml.be.config.AuthModeHostBased;
import com.ah.xml.be.config.AuthModePortBased;
import com.ah.xml.be.config.CwpExternalServer;
import com.ah.xml.be.config.CwpExternalServerRank;
import com.ah.xml.be.config.CwpMultiLanguage;
import com.ah.xml.be.config.CwpProcessSipInfo;
import com.ah.xml.be.config.CwpRedirectDelay;
import com.ah.xml.be.config.CwpRedirectExternalPage;
import com.ah.xml.be.config.CwpRedirectLoginPage;
import com.ah.xml.be.config.CwpRedirectOriginalPage;
import com.ah.xml.be.config.CwpSelfRegViaIdm;
import com.ah.xml.be.config.CwpServerName;
import com.ah.xml.be.config.CwpWalledGarden;
import com.ah.xml.be.config.HttpAuthUserPassword;
import com.ah.xml.be.config.LocalCacheTimeout;
import com.ah.xml.be.config.MdmAerohiveApiKey;
import com.ah.xml.be.config.MdmAerohiveOnboard;
import com.ah.xml.be.config.MdmAirwatchNonCompliant;
import com.ah.xml.be.config.MdmAirwatchPollStatus;
import com.ah.xml.be.config.MdmAirwatchSendMessage;
import com.ah.xml.be.config.MdmAirwatchSendMessageType;
import com.ah.xml.be.config.MdmHttpAuth;
import com.ah.xml.be.config.MdmOsObject;
import com.ah.xml.be.config.MobileDeviceManagerAerohive;
import com.ah.xml.be.config.MobileDeviceManagerAirwatch;
import com.ah.xml.be.config.MobileDeviceManagerJss;
import com.ah.xml.be.config.NetworkAccessSecurityObj;
import com.ah.xml.be.config.PpskAuthMethod;
import com.ah.xml.be.config.Protocol802Dot1X;
import com.ah.xml.be.config.ProtocolGmkRekeyPeriod;
import com.ah.xml.be.config.ProtocolGtkRetry;
import com.ah.xml.be.config.ProtocolGtkTimeout;
import com.ah.xml.be.config.ProtocolMfpMethod;
import com.ah.xml.be.config.ProtocolPtkRekeyPeriod;
import com.ah.xml.be.config.ProtocolPtkRetry;
import com.ah.xml.be.config.ProtocolPtkTimeout;
import com.ah.xml.be.config.ProtocolReauthInterval;
import com.ah.xml.be.config.ProtocolRekeyPeriod;
import com.ah.xml.be.config.ProtocolReplayWindow;
import com.ah.xml.be.config.ProtocolRoaming;
import com.ah.xml.be.config.ProtocolSuiteType;
import com.ah.xml.be.config.ProtocolWep8021X;
import com.ah.xml.be.config.ProtocolWepOpen;
import com.ah.xml.be.config.ProtocolWepRekeyPeriod;
import com.ah.xml.be.config.ProtocolWpa2AesPsk;
import com.ah.xml.be.config.ProtocolWpaAes8021X;
import com.ah.xml.be.config.ProtocolWpaAes8021XRoaming;
import com.ah.xml.be.config.ProtocolWpaAesMfp;
import com.ah.xml.be.config.ProtocolWpaAesPsk;
import com.ah.xml.be.config.ProtocolWpaTkip8021X;
import com.ah.xml.be.config.ProtocolWpaTkip8021XRoaming;
import com.ah.xml.be.config.ProtocolWpaTkipPsk;
import com.ah.xml.be.config.RadiusAccountInterimInterval;
import com.ah.xml.be.config.RadiusAccountingAll;
import com.ah.xml.be.config.RadiusAccountingServer;
import com.ah.xml.be.config.RadiusNasInject;
import com.ah.xml.be.config.RadiusRetryInterval;
import com.ah.xml.be.config.RadiusServerType;
import com.ah.xml.be.config.ReauthIntervalCr;
import com.ah.xml.be.config.SecurityAaa;
import com.ah.xml.be.config.SecurityAdditionalAuthMethod;
import com.ah.xml.be.config.SecurityAuthMode;
import com.ah.xml.be.config.SecurityDefaultUserProfileAttr;
import com.ah.xml.be.config.SecurityDhcpServer;
import com.ah.xml.be.config.SecurityEap;
import com.ah.xml.be.config.SecurityInitialAuthMethod;
import com.ah.xml.be.config.SecurityLocalCache;
import com.ah.xml.be.config.SecurityObjectPpskWebServer;
import com.ah.xml.be.config.SecurityParameters;
import com.ah.xml.be.config.SecurityPreauth;
import com.ah.xml.be.config.SecurityPrivatePsk;
import com.ah.xml.be.config.SecurityUserProfileAllowed;
import com.ah.xml.be.config.SecurityUserProfileDeny;
import com.ah.xml.be.config.SecurityWebServer;
import com.ah.xml.be.config.SsidCacheAgeout;
import com.ah.xml.be.config.SsidCacheUpdateInterval;
import com.ah.xml.be.config.SsidEapRetries;
import com.ah.xml.be.config.SsidEapTimeout;
import com.ah.xml.be.config.SsidRoamingCache;
import com.ah.xml.be.config.SsidSecurityRoaming;
import com.ah.xml.be.config.TimerDisplayAlert;
import com.ah.xml.be.config.UpDenyBanDisconnect;
import com.ah.xml.be.config.UserProfileDenyAction;
import com.ah.xml.be.config.UserProfileMappingAttributeId;
import com.ah.xml.be.config.UserProfileMappingVendorAttributeId;
import com.ah.xml.be.config.UserProfileMappingVendorId;
import com.ah.xml.be.config.WalledGardenServer;
import com.ah.xml.be.config.WalledGardenService;
import com.ah.xml.be.config.WalledGardenServicePort;
import com.ah.xml.be.config.WalledGardenServiceProtocol;
import com.ah.xml.be.config.WpaPskKey;

/**
 * @author zhang
 * @version 2010-4-29 14:39:38
 */

public class CreateSecurityObjectTree {
	
	private SecurityObjectProfileInt securityImpl;
	private GenerateXMLDebug oDebug;
	private NetworkAccessSecurityObj securityObj;
	
	private List<Object> securityChildList_1 = new ArrayList<Object>();
	private List<Object> securityChildList_2 = new ArrayList<Object>();
	private List<Object> securityChildList_3 = new ArrayList<Object>();
	private List<Object> securityChildList_4 = new ArrayList<Object>();
	private List<Object> securityChildList_5 = new ArrayList<Object>();
	private List<Object> securityChildList_6 = new ArrayList<Object>();
	
	private List<Object> wallGardenList_1 = new ArrayList<Object>();

	public CreateSecurityObjectTree(SecurityObjectProfileInt securityImpl, GenerateXMLDebug oDebug){
		this.securityImpl = securityImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		this.securityObj = new NetworkAccessSecurityObj();
		generateSecurityObjectLevel_1();
	}
	
	public NetworkAccessSecurityObj getSecurityObj(){
		return this.securityObj;
	}
	
	private void generateSecurityObjectLevel_1() throws Exception{
		
		/** attribute: operation */
		securityObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		securityObj.setName(securityImpl.getSecurityObjectName());
		
		/** element: <security-object>.<cr> */
		securityObj.setCr("");
		
		/** element: <security-object>.<default-user-profile-attr> */
		oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+"']", 
				"default-user-profile-attr", GenerateXMLDebug.CONFIG_ELEMENT, 
				securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
		if(securityImpl.isConfigDefaultUserProfile()){
			
			oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+"']",
					"default-user-profile-attr", GenerateXMLDebug.SET_VALUE, 
					securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
			Object[][] parmDefaultUserProfileId = {
					{CLICommonFunc.ATTRIBUTE_VALUE,securityImpl.getDefaultUserProfileId()},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
			securityObj.setDefaultUserProfileAttr(
					(SecurityDefaultUserProfileAttr)CLICommonFunc.createObjectWithName(SecurityDefaultUserProfileAttr.class, parmDefaultUserProfileId)
			);
		}
		
		/** element: <security-object>.<web-server> */
		oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+"']",
				"web-server", GenerateXMLDebug.CONFIG_ELEMENT,
				securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
		if(securityImpl.isConfigureWebServer()){
			SecurityWebServer webServerObj = new SecurityWebServer();
			securityChildList_1.add(webServerObj);
			securityObj.setWebServer(webServerObj);
		}
		
		/** element: <security-object>.<web-directory> */
		oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+"']",
				"web-directory", GenerateXMLDebug.CONFIG_ELEMENT,
				securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
		if(securityImpl.isConfigureWebDirect()){
			
			oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+"']",
					"web-directory", GenerateXMLDebug.SET_VALUE,
					securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
			securityObj.setWebDirectory(
					CLICommonFunc.createAhStringActObj(
							securityImpl.getWebDirectory(), CLICommonFunc.getYesDefault()
					)
			);
		}
		
		/** element: <security-object>.<dhcp-server> */
		oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+"']",
				"dhcp-server", GenerateXMLDebug.CONFIG_ELEMENT,
				securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
		if(securityImpl.isEnableInternalServers()){
			SecurityDhcpServer dhcpServer = new SecurityDhcpServer();
			securityChildList_1.add(dhcpServer);
			securityObj.setDhcpServer(dhcpServer);
		}
		
		/** element: <security-object>.<user-profile-allowed> */
		SecurityUserProfileAllowed userProfileAllowedObj = new SecurityUserProfileAllowed();
		securityChildList_1.add(userProfileAllowedObj);
		securityObj.setUserProfileAllowed(userProfileAllowedObj);
		
//		/** element: <security-object>.<user-profile-deny> */
//		oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+"']",
//				"user-profile-deny", GenerateXMLDebug.CONFIG_ELEMENT,
//				securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
//		if(securityImpl.isConfigUserProfileDeny()){
//			SecurityUserProfileDeny userProfileDenyObj = new SecurityUserProfileDeny();
//			securityChildList_1.add(userProfileDenyObj);
//			securityObj.setUserProfileDeny(userProfileDenyObj);
//		}
		
		/** element: <security-object>.<walled-garden> */
		oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+"']",
				"walled-garden", GenerateXMLDebug.CONFIG_ELEMENT,
				securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
		if(securityImpl.isConfigWalledGarden()){
			CwpWalledGarden walledGardenObj = new CwpWalledGarden();
			securityChildList_1.add(walledGardenObj);
			securityObj.setWalledGarden(walledGardenObj);
		}
		
		/** element: <security-object>.<user-profile-sequence> */
		oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+"']",
				"user-profile-sequence", GenerateXMLDebug.SET_VALUE,
				securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
		securityObj.setUserProfileSequence(CLICommonFunc.createAhStringActObj(securityImpl.getUserProfileSequence(), CLICommonFunc.getYesDefault()));
		
		/** element: <security-object>.<security> */
		SecurityParameters security = new SecurityParameters();
		securityChildList_1.add(security);
		securityObj.setSecurity(security);
		
		/** element: <security-object>.<mobile-device-policy> */
		oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+"']",
				"mobile-device-policy", GenerateXMLDebug.CONFIG_ELEMENT,
				securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
		if(securityImpl.isConfigDevicePolicy()){
			
			oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+"']",
					"mobile-device-policy", GenerateXMLDebug.SET_VALUE,
					securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
			securityObj.setMobileDevicePolicy(CLICommonFunc.createAhNameActObj(securityImpl.getDevicePolicy(), true));
		}
		
		/** element: <security-object>.<ppsk-web-server> */
		oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+"']",
				"ppsk-web-server", GenerateXMLDebug.CONFIG_ELEMENT,
				securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
		if(securityImpl.isConfigPpskWebServer()){
			SecurityObjectPpskWebServer ppskWebServer = new SecurityObjectPpskWebServer();
			securityChildList_1.add(ppskWebServer);
			securityObj.setPpskWebServer(ppskWebServer);
		}
		
		generateSecurityObjectLevel_2();
	}
	
	private void generateSecurityObjectLevel_2() throws Exception{
		/**
		 * <security-object>.<web-server>				SecurityWebServer
		 * <security-object>.<dhcp-server>				SecurityDhcpServer
		 * <security-object>.<user-profile-allowed>		SecurityUserProfileAllowed
		 * <security-object>.<user-profile-deny>		SecurityUserProfileDeny
		 * <security-object>.<walled-garden>			CwpWalledGarden
		 * <security-object>.<security>					SecurityParameters
		 * <security-object>.<ppsk-web-server>			SecurityObjectPpskWebServer
		 */
		for(Object childObj : securityChildList_1){
			
			/** element: <security-object>.<web-server>	*/
			if(childObj instanceof SecurityWebServer){
				SecurityWebServer webServerObj = (SecurityWebServer)childObj;
				
				/** attribute: operation */
				webServerObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<web-server>.<cr> */
				webServerObj.setCr("");
				
				if(!securityImpl.isConfigExternalCwp()){
					
					/** element: <security-object>.<web-server>.<index-file> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/web-server",
							"index-file", GenerateXMLDebug.CONFIG_ELEMENT, 
							securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
					if(securityImpl.isConfigIndexFile()){
						
						oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/web-server",
								"index-file", GenerateXMLDebug.SET_VALUE, 
								securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
						Object[][] indexFileParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getWebServerIndexFile()}
						};
						webServerObj.setIndexFile(
								(SecurityWebServer.IndexFile)CLICommonFunc.createObjectWithName(SecurityWebServer.IndexFile.class, indexFileParm)
						);
					}
					
					/** element: <security-object>.<web-server>.<web-page> */
					if(securityImpl.isConfigSsidWebPage()){
						SecurityWebServer.WebPage webPageObj = new SecurityWebServer.WebPage();
						securityChildList_2.add(webPageObj);
						webServerObj.setWebPage(webPageObj);
					}
				}
				
				/** element: <security-object>.<web-server>.<success-file> */
				if(securityImpl.isConfigCwpSuccessFile()){
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/web-server",
							"success-file", GenerateXMLDebug.SET_VALUE, 
							securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
					Object[][] successParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getWebServerSuccessFile()}
					};
					webServerObj.setSuccessFile(
							(SecurityWebServer.SuccessFile)CLICommonFunc.createObjectWithName(SecurityWebServer.SuccessFile.class, successParm)
					);
				}
				
				/** element: <security-object>.<web-server>.<failure-file> */
				if(securityImpl.isConfigCwpFailureFile()){
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/web-server",
							"failure-file", GenerateXMLDebug.SET_VALUE,
							securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
					webServerObj.setFailureFile(CLICommonFunc.createAhStringObj(securityImpl.getCwpFailureFileName()));
				}
				
				/** element: <security-object>.<web-server>.<ssl> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/web-server",
						"ssl", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isConfigureWebServerSsl()){
					SecurityWebServer.Ssl sslObj = new SecurityWebServer.Ssl();
					securityChildList_2.add(sslObj);
					webServerObj.setSsl(sslObj);
				}
				
			}
			
			/** element: <security-object>.<dhcp-server> */
			if(childObj instanceof SecurityDhcpServer){
				SecurityDhcpServer dhcpServer = (SecurityDhcpServer)childObj;
				
				/** attribute: operation */
				dhcpServer.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<dhcp-server>.<cr> */
				dhcpServer.setCr("");
				
				/** element: <security-object>.<dhcp-server>.<lease-time> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/dhcp-server",
						"lease-time", GenerateXMLDebug.SET_VALUE, 
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				Object[][] paramLeaseTime = {
						{ CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getDhcpServerLeaseTime() },
						{ CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault() }
				};
				dhcpServer.setLeaseTime(
						(SecurityDhcpServer.LeaseTime)CLICommonFunc.createObjectWithName(SecurityDhcpServer.LeaseTime.class, paramLeaseTime)
				);
				
				/** element: <security-object>.<dhcp-server>.<renewal-response> */
				SecurityDhcpServer.RenewalResponse renewalResponseObj = new SecurityDhcpServer.RenewalResponse();
				securityChildList_2.add(renewalResponseObj);
				dhcpServer.setRenewalResponse(renewalResponseObj);
			}
			
			/** element: <security-object>.<user-profile-allowed> */
			if(childObj instanceof SecurityUserProfileAllowed){
				SecurityUserProfileAllowed userProfileAllowedObj = (SecurityUserProfileAllowed)childObj;
				
				/** attribute: operation */
				userProfileAllowedObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				if(securityImpl.isConfigUserProfileAllowed()){
					
					/** element: <security-object>.<user-profile-allowed>.<cr> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/user-profile-allowed",
							"cr", GenerateXMLDebug.CONFIG_ELEMENT, 
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					for(int i=0; i<securityImpl.getUserProfileAllowedSize(); i++){
						
						oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/user-profile-allowed",
								"cr", GenerateXMLDebug.SET_NAME, 
								securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
						userProfileAllowedObj.getCr().add(
								CLICommonFunc.createAhNameActValue(securityImpl.getUserProfileAllowedName(i), CLICommonFunc.getYesDefault())
						);
					}
				}else{
					/** element: <security-object>.<user-profile-allowed>.<all> */
					userProfileAllowedObj.setAll(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
			}
			
			/** element: <security-object>.<user-profile-deny> */
			if(childObj instanceof SecurityUserProfileDeny){
				SecurityUserProfileDeny userProfileDenyObj = (SecurityUserProfileDeny)childObj;
				
				/** element: <security-object>.<user-profile-deny>.<action> */
				UserProfileDenyAction actionObj = new UserProfileDenyAction();
				securityChildList_2.add(actionObj);
				userProfileDenyObj.setAction(actionObj);
			}
			
			/** element: <security-object>.<walled-garden> */
			if(childObj instanceof CwpWalledGarden){
				CwpWalledGarden walledGardenObj = (CwpWalledGarden)childObj;
				
				/** element: <security-object>.<walled-garden>.<ip-address> */
				for(int i=0; i<securityImpl.getWallGardenIpSize(); i++){
					walledGardenObj.getIpAddress().add(this.createWalledGardenServer(SecurityObjectProfileInt.WALL_GARDEN_IPADDRESS, i));
				}
				
				/** element: <security-object>.<walled-garden>.<hostname> */
				for(int i=0; i<securityImpl.getWallGardenHostSize(); i++){
					walledGardenObj.getHostname().add(this.createWalledGardenServer(SecurityObjectProfileInt.WALL_GARDEN_HOSTNAME, i));
				}
			}
			
			/** element: <security-object>.<security> */
			if(childObj instanceof SecurityParameters){
				SecurityParameters security = (SecurityParameters)childObj;
				
				/** element: <security-object>.<security>.<additional-auth-method> */
				SecurityAdditionalAuthMethod additionalAuthMethodObj = new SecurityAdditionalAuthMethod();
				securityChildList_2.add(additionalAuthMethodObj);
				security.setAdditionalAuthMethod(additionalAuthMethodObj);
				
				/** element: <security-object>.<security>.<aaa> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security",
						"aaa", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isConfigSsidAAARadius()){
					SecurityAaa aaaObj = new SecurityAaa();
					securityChildList_2.add(aaaObj);
					security.setAaa(aaaObj);
				}
				
				/** element: <security-object>.<security>.<auth-mode> */
				if(securityImpl.isConfigAuthMethod()){
					SecurityAuthMode authMode = new SecurityAuthMode();
					securityChildList_2.add(authMode);
					security.setAuthMode(authMode);
				}
				
				/** element: <security-object>.<security>.<initial-auth-method> */
				if(securityImpl.isConfigInitialAuthMethod()){
					SecurityInitialAuthMethod initialAuth = new SecurityInitialAuthMethod();
					securityChildList_2.add(initialAuth);
					security.setInitialAuthMethod(initialAuth);
				}
				
				/** element: <security-object>.<security>.<ft> */
				security.setFt(CLICommonFunc.getAhOnlyAct(securityImpl.isEnabled80211r()));
				
				if(securityImpl.isCwpFromSsid()){
					
					/** element: <security-object>.<security>.<eap> */
					if(securityImpl.isSupportThisDevice()){
						SecurityEap eapObj = new SecurityEap();
						securityChildList_2.add(eapObj);
						security.setEap(eapObj);
					}
					
					/** element: <security-object>.<security>.<local-cache> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security",
							"local-cache", GenerateXMLDebug.CONFIG_ELEMENT,
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					if(securityImpl.isConfigLocalCacheTimeOut()){
						SecurityLocalCache cacheObj = new SecurityLocalCache();
						securityChildList_2.add(cacheObj);
						security.setLocalCache(cacheObj);
					}
					
					/** element: <security-object>.<security>.<roaming> */
					if(securityImpl.isSupportThisDevice()){
						SsidSecurityRoaming roamingObj = new SsidSecurityRoaming();
						securityChildList_2.add(roamingObj);
						security.setRoaming(roamingObj);
					}
					
					/** element: <security-object>.<security>.<private-psk> */
					if(securityImpl.isSupportThisDevice()){
						oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security",
								"private-psk", GenerateXMLDebug.CONFIG_ELEMENT, 
								securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
						SecurityPrivatePsk privatePskObj = new SecurityPrivatePsk();
						securityChildList_2.add(privatePskObj);
						security.setPrivatePsk(privatePskObj);
					}
					
					/** element: <security-object>.<security>.<preauth> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security",
							"preauth", GenerateXMLDebug.CONFIG_ELEMENT, 
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					if(securityImpl.isConfigurePreauth() ){
						SecurityPreauth preauthObj = new SecurityPreauth();
						securityChildList_2.add(preauthObj);
						security.setPreauth(preauthObj);
					}
					
					/** element: <security-object>.<security>.<protocol-suite> */
					ProtocolSuiteType protocolObj = new ProtocolSuiteType();
					securityChildList_2.add(protocolObj);
					security.setProtocolSuite(protocolObj);
				}
				
			}
			
			/** element: <security-object>.<ppsk-web-server> */
			if(childObj instanceof SecurityObjectPpskWebServer){
				SecurityObjectPpskWebServer ppskWebServer = (SecurityObjectPpskWebServer)childObj;
				
				/** attribute: operation */
				ppskWebServer.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<ppsk-web-server>.<https> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/ppsk-web-server",
						"https", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				ppskWebServer.setHttps(CLICommonFunc.getAhOnlyAct(securityImpl.isPpskWebServerHttps()));
				
				/** element: <security-object>.<ppsk-web-server>.<bind-to-ppsk-ssid> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/ppsk-web-server",
						"bind-to-ppsk-ssid", GenerateXMLDebug.SET_VALUE,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				ppskWebServer.setBindToPpskSsid(CLICommonFunc.createAhStringActObj(securityImpl.getBindToPpskSsid(), true));
				
				/** element: <security-object>.<ppsk-web-server>.<auth-user> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/ppsk-web-server",
						"auth-user", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isConfigPpskAuthUser()){
					ppskWebServer.setAuthUser(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
				
				if(!securityImpl.isUseDefaultPpskPage()){
					
					/** element: <security-object>.<ppsk-web-server>.<web-directory> */
					if(securityImpl.isConfigPpskWebDir()){
						oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/ppsk-web-server",
								"web-directory", GenerateXMLDebug.SET_VALUE,
								securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
						ppskWebServer.setWebDirectory(CLICommonFunc.createAhStringActObj(
								securityImpl.getPpskWebDir(), CLICommonFunc.getYesDefault()));
					}
					
					/** element: <security-object>.<ppsk-web-server>.<login-page> */
					if(securityImpl.isConfigPpskLoginPage()){
						oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/ppsk-web-server",
								"login-page", GenerateXMLDebug.SET_VALUE,
								securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
						ppskWebServer.setLoginPage(CLICommonFunc.createAhStringActObj(
								securityImpl.getPpskLoginPage(), CLICommonFunc.getYesDefault()));
					}
					
//					/** element: <security-object>.<ppsk-web-server>.<login-script> */
//					if(securityImpl.isConfigPpskLoginScript()){
//						oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/ppsk-web-server",
//								"login-script", GenerateXMLDebug.SET_VALUE,
//								securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
//						ppskWebServer.setLoginScript(CLICommonFunc.createAhStringActObj(
//								securityImpl.getPpskLoginScript(), CLICommonFunc.getYesDefault()));
//					}
				}
			}
		}
		securityChildList_1.clear();
		generateSecurityObjectLevel_3();
	}
	
	private void generateSecurityObjectLevel_3() throws Exception{
		/**
		 * <security-object>.<web-server>.<web-page>					SecurityWebServer.WebPage
		 * <security-object>.<web-server>.<ssl>							SecurityWebServer.Ssl
		 * <security-object>.<dhcp-server>.<renewal-response>			SecurityDhcpServer.RenewalResponse
		 * <security-object>.<user-profile-deny>.<action>				UserProfileDenyAction
		 * <security-object>.<security>.<preauth>						SecurityPreauth
		 * <security-object>.<security>.<aaa>							SecurityAaa
		 * <security-object>.<security>.<private-psk>					SecurityPrivatePsk
		 * <security-object>.<security>.<roaming>						SsidSecurityRoaming
		 * <security-object>.<security>.<local-cache>					SecurityLocalCache
		 * <security-object>.<security>.<eap>							SecurityEap
		 * <security-object>.<security>.<additional-auth-method>		SecurityAdditionalAuthMethod
		 * <security-object>.<security>.<protocol-suite>				ProtocolSuiteType
		 * <security-object>.<security>.<auth-mode>						SecurityAuthMode
		 * <security-object>.<security>.<initial-auth-method>			SecurityInitialAuthMethod
		 */
		for(Object childObj : securityChildList_2){
			
			/** element: <security-object>.<web-server>.<web-page> */
			if(childObj instanceof SecurityWebServer.WebPage){
				SecurityWebServer.WebPage webPageObj = (SecurityWebServer.WebPage)childObj;
				
				/** attribute: operation */
				webPageObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<web-server>.<web-page>.<mandatory-field> */
				SecurityWebServer.WebPage.MandatoryField mandatoryField = new SecurityWebServer.WebPage.MandatoryField();
				securityChildList_3.add(mandatoryField);
				webPageObj.setMandatoryField(mandatoryField);
			}
			
			/** element: <security-object>.<web-server>.<ssl> */
			if(childObj instanceof SecurityWebServer.Ssl){
				SecurityWebServer.Ssl sslObj = (SecurityWebServer.Ssl)childObj;
				
				/** attribute: operation */
				sslObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<web-server>.<ssl>.<server-key> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/web-server/ssl",
						"server-key", GenerateXMLDebug.SET_VALUE, 
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				Object[][] serverKeyParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getWebServerKeyValue()}
				};
				sslObj.setServerKey(
						(SecurityWebServer.Ssl.ServerKey)CLICommonFunc.createObjectWithName(SecurityWebServer.Ssl.ServerKey.class, serverKeyParm)
				);
			}
			
			/** element: <security-object>.<dhcp-server>.<renewal-response> */
			if(childObj instanceof SecurityDhcpServer.RenewalResponse){
				SecurityDhcpServer.RenewalResponse renewalResponseObj = (SecurityDhcpServer.RenewalResponse)childObj;
				
				/** attribute: operation */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/dhcp-server",
						"renewal-response", GenerateXMLDebug.SET_OPERATION, 
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isDhcpServerBroadcast()){
					renewalResponseObj.setOperation(CLICommonFunc.getAhEnumAct(false));
				}else{
					renewalResponseObj.setOperation(CLICommonFunc.getAhEnumAct(true));
				}
				
				if(securityImpl.isDhcpServerUnicast() || securityImpl.isDhcpServerKeepSilent()){
					
					/** element: <security-object>.<dhcp-server>.<renewal-response>.<keep-silent> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/dhcp-server/renewal-response",
							"keep-silent", GenerateXMLDebug.SET_OPERATION, 
							securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
					if(securityImpl.isDhcpServerKeepSilent()){
						renewalResponseObj.setKeepSilent("");
					}
					
					/** element: <security-object>.<dhcp-server>.<renewal-response>.<renew-nak-unicast> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/dhcp-server/renewal-response",
							"renew-nak-unicast", GenerateXMLDebug.SET_OPERATION, 
							securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
					if(securityImpl.isDhcpServerUnicast()){
						renewalResponseObj.setRenewNakUnicast("");
					}
				}
			}
			
			/** element: <security-object>.<user-profile-deny>.<action>	*/
			if(childObj instanceof UserProfileDenyAction){
				UserProfileDenyAction actionObj = (UserProfileDenyAction)childObj;
				
				/** element: <security-object>.<user-profile-deny>.<action>.<ban> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/user-profile-deny/action",
						"ban", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isConfigUserProfileAction(SsidProfileInt.UserProfileDenyAction.ban)){
					UserProfileDenyAction.Ban banObj = new UserProfileDenyAction.Ban();
					securityChildList_3.add(banObj);
					actionObj.setBan(banObj);
				}
				
				/** element: <security-object>.<user-profile-deny>.<action>.<ban-forever> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/user-profile-deny/action",
						"ban-forever", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isConfigUserProfileAction(SsidProfileInt.UserProfileDenyAction.banForever)){
					UpDenyBanDisconnect banForeverObj = new UpDenyBanDisconnect();
					securityChildList_3.add(banForeverObj);
					actionObj.setBanForever(banForeverObj);
				}
				
				/** element: <security-object>.<user-profile-deny>.<action>.<disconnect> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/user-profile-deny/action",
						"disconnect", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isConfigUserProfileAction(SsidProfileInt.UserProfileDenyAction.disconnect)){
					UpDenyBanDisconnect disconnectObj = new UpDenyBanDisconnect();
					securityChildList_3.add(disconnectObj);
					actionObj.setDisconnect(disconnectObj);
				}
			}
			
			/** element: <security-object>.<security>.<preauth>	*/
			if(childObj instanceof SecurityPreauth){
				SecurityPreauth preauthObj = (SecurityPreauth)childObj;
				
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security",
						"preauth", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				preauthObj.setOperation(
						CLICommonFunc.getAhEnumAct(securityImpl.isSecurityPreauthEnable())
				);
			}
			
			/** element: <security-object>.<security>.<aaa>	*/
			if(childObj instanceof SecurityAaa){
				SecurityAaa aaaObj = (SecurityAaa)childObj;
				
				/** element: <security-object>.<security>.<aaa>.<radius-server> */
				SecurityAaa.RadiusServer radiusServerObj = new SecurityAaa.RadiusServer();
				securityChildList_3.add(radiusServerObj);
				aaaObj.setRadiusServer(radiusServerObj);
				
				/** element: <security-object>.<security>.<aaa>.<user-profile-mapping> */
				AaaUserProfileMapping userProfileMappingObj = new AaaUserProfileMapping();
				securityChildList_3.add(userProfileMappingObj);
				aaaObj.setUserProfileMapping(userProfileMappingObj);
			}
			
			/** element: <security-object>.<security>.<private-psk>	*/
			if(childObj instanceof SecurityPrivatePsk){
				SecurityPrivatePsk privatePskObj = (SecurityPrivatePsk)childObj;
				
				/** element: <security-object>.<security>.<private-psk>.<cr> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/private-psk",
						"cr", GenerateXMLDebug.SET_OPERATION, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				privatePskObj.setCr(CLICommonFunc.getAhOnlyAct(securityImpl.isConfigPrivatePsk()));
				
				if(securityImpl.isConfigPrivatePsk()){
					
					/** element: <security-object>.<security>.<private-psk>.<default-psk-disabled> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/private-psk",
							"default-psk-disabled", GenerateXMLDebug.SET_OPERATION,
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					privatePskObj.setDefaultPskDisabled(CLICommonFunc.getAhOnlyAct(securityImpl.isDisableDefPsk()));
					
					/** element: <security-object>.<security>.<private-psk>.<radius-auth> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/private-psk",
							"radius-auth", GenerateXMLDebug.CONFIG_ELEMENT,
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					if(securityImpl.isEnableRadiusAuth()){
						
						oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/private-psk",
								"radius-auth", GenerateXMLDebug.SET_VALUE,
								securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
						privatePskObj.setRadiusAuth(this.createAhPpskAuthMethod(securityImpl.getPskAuthMethod()));
					}
					
					/** element: <security-object>.<security>.<private-psk>.<same-user-limit> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/private-psk",
							"same-user-limit", GenerateXMLDebug.SET_VALUE,
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					Object[][] limitParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getPskUserLimit()},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					privatePskObj.setSameUserLimit(
							(SecurityPrivatePsk.SameUserLimit)CLICommonFunc.createObjectWithName(SecurityPrivatePsk.SameUserLimit.class, limitParm)
					);
					
					/** element: <security-object>.<security>.<private-psk>.<mac-binding-enable> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/private-psk",
							"mac-binding-enable", GenerateXMLDebug.SET_OPERATION,
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					privatePskObj.setMacBindingEnable(CLICommonFunc.getAhOnlyAct(securityImpl.isPPSKMacBindingEnable()));
					
					/** element: <security-object>.<security>.<private-psk>.<external-server> */
					privatePskObj.setExternalServer(CLICommonFunc.getAhOnlyAct(securityImpl.isPPSKExternalServerEnable()));
					
					/** element: <security-object>.<security>.<private-psk>.<self-reg-enable> */
					privatePskObj.setSelfRegEnable(CLICommonFunc.getAhOnlyAct(securityImpl.isEnablePPSKSelfRegister()));
				}
				
				/** element: <security-object>.<security>.<private-psk>.<ppsk-server> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/private-psk",
						"ppsk-server", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isConfigPpskServer()){
					
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/private-psk",
							"ppsk-server", GenerateXMLDebug.SET_VALUE,
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					privatePskObj.setPpskServer(CLICommonFunc.createAhStringActObj(securityImpl.getPpskServerIp(), true));
				}
			}
			
			/** element: <security-object>.<security>.<roaming> */
			if(childObj instanceof SsidSecurityRoaming){
				SsidSecurityRoaming roamingObj = (SsidSecurityRoaming)childObj;
				
				/** element: <security-object>.<security>.<roaming>.<cache> */
				SsidRoamingCache cacheObj = new SsidRoamingCache();
				securityChildList_3.add(cacheObj);
				roamingObj.setCache(cacheObj);
			}
			
			/** element: <security-object>.<security>.<local-cache>	*/
			if(childObj instanceof SecurityLocalCache){
				SecurityLocalCache cacheObj = (SecurityLocalCache)childObj;
				
				/** element: <security-object>.<security>.<local-cache>.<timeout> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/local-cache",
						"timeout", GenerateXMLDebug.SET_VALUE,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				Object[][] timeOutParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getLocalCacheTimeOut()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				cacheObj.setTimeout(
						(LocalCacheTimeout)CLICommonFunc.createObjectWithName(LocalCacheTimeout.class, timeOutParm)
				);
			}
			
			/** element: <security-object>.<security>.<eap>	*/
			if(childObj instanceof SecurityEap){
				SecurityEap eapObj = (SecurityEap)childObj;
				
				/** element: <security-object>.<security>.<eap>.<timeout> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/eap", 
						"timeout", GenerateXMLDebug.SET_VALUE, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				Object[][] timeOutArg = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getEapTimeOut()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				eapObj.setTimeout(
						(SsidEapTimeout)CLICommonFunc.createObjectWithName(
								SsidEapTimeout.class, timeOutArg)
				);
				
				/** element: <security-object>.<security>.<eap>.<retries> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/eap", 
						"retries", GenerateXMLDebug.SET_VALUE, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				Object[][] retriesArg = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getEapRetries()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				eapObj.setRetries(
						(SsidEapRetries)CLICommonFunc.createObjectWithName(SsidEapRetries.class, retriesArg)
				);
			}
			
			/** element: <security-object>.<security>.<additional-auth-method> */
			if(childObj instanceof SecurityAdditionalAuthMethod){
				SecurityAdditionalAuthMethod additionalAuthMethodObj = (SecurityAdditionalAuthMethod)childObj;
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method",
						"captive-web-portal", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isConfigureCWP()){
					SecurityAdditionalAuthMethod.CaptiveWebPortal captiveWebPortal = new SecurityAdditionalAuthMethod.CaptiveWebPortal();
					securityChildList_3.add(captiveWebPortal);
					additionalAuthMethodObj.setCaptiveWebPortal(captiveWebPortal);
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<mac-based-auth> */
				SecurityAdditionalAuthMethod.MacBasedAuth macAuthObj = new SecurityAdditionalAuthMethod.MacBasedAuth();
				securityChildList_3.add(macAuthObj);
				additionalAuthMethodObj.setMacBasedAuth(macAuthObj);
				
				/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager> */
				if (securityImpl.isMdmTypeEnabled(ConfigTemplateMdm.MDM_ENROLL_TYPE_JSS)
						|| securityImpl.isMdmTypeEnabled(ConfigTemplateMdm.MDM_ENROLL_TYPE_AIRWATCH)
						|| securityImpl.isMdmTypeEnabled(ConfigTemplateMdm.MDM_ENROLL_TYPE_AEROHIVE) ) {
					AuthMethodMobileDeviceManager authMobileObj = new AuthMethodMobileDeviceManager();
					securityChildList_3.add(authMobileObj);
					additionalAuthMethodObj.setMobileDeviceManager(authMobileObj);
				}
			}
			
			/** element: <security-object>.<security>.<protocol-suite> */
			if(childObj instanceof ProtocolSuiteType){
				ProtocolSuiteType protocolSuite = (ProtocolSuiteType)childObj;
				
				/** attribute: operation */
				protocolSuite.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<security>.<protocol-suite>.<open> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite",
						"open", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isProtocolOpen()){
					protocolSuite.setOpen(
							CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault())
					);
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<wep-open> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite",
						"wep-open", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isProtocolWepOpen()){
					for(int i=0; i<securityImpl.getSsidProtocolWepSize(); i++){
						if(securityImpl.isConfigureProtocolWep(i)){
							protocolSuite.getWepOpen().add(createProtocolWepOpen(i));
						}
					}
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<wep-shared> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite",
						"wep-shared", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isProtocolWepShared()){
					for(int i=0; i<securityImpl.getSsidProtocolWepSize(); i++){
						if(securityImpl.isConfigureProtocolWep(i)){
							protocolSuite.getWepShared().add(createProtocolWepOpen(i));
						}
					}
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<wpa-auto-psk> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite",
						"wpa-auto-psk", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isProtocolWpaAutoPsk()){
					protocolSuite.setWpaAutoPsk(createProtocolWpaTkipPsk());
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<wpa-tkip-psk> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite",
						"wpa-tkip-psk", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isProtocolWpaTkipPsk()){
					protocolSuite.setWpaTkipPsk(createProtocolWpaTkipPsk());
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<wpa-aes-psk> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite",
						"wpa-aes-psk", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isProtocolWpaAesPsk()){
					protocolSuite.setWpaAesPsk(createProtocolWpaAesPsk());
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<wpa2-tkip-psk> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite",
						"wpa2-tkip-psk", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isProtocolWpa2TkipPsk()){
					protocolSuite.setWpa2TkipPsk(createProtocolWpaTkipPsk());
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<wpa2-aes-psk> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite",
						"wpa2-aes-psk", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isProtocolWpa2AesPsk()){
					protocolSuite.setWpa2AesPsk(createProtocolWpa2AesPsk());
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<wpa-auto-8021x> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite",
						"wpa-auto-8021x", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isProtocolWpaAuto_8021x()){
					protocolSuite.setWpaAuto8021X(createProtocolWpaTkip8021XRoaming());
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<wpa-tkip-8021x> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite",
						"wpa-tkip-8021x", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isProtocolWpaTkip_8021x()){
					protocolSuite.setWpaTkip8021X(createProtocolWpaTkip8021X());
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<wpa-aes-8021> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite",
						"wpa-aes-8021x", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isProtocolWpaAes_8021x()){
					protocolSuite.setWpaAes8021X(createProtocolWpaAes8021X());
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<wpa2-tkip-8021x> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite",
						"wpa2-tkip-8021x", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isProtocolWpa2Tkip_8021x()){
					protocolSuite.setWpa2Tkip8021X(createProtocolWpaTkip8021XRoaming());
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<wep104-8021x> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite",
						"wep104-8021x", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isProtocolWep104_8021x()){
					protocolSuite.setWep1048021X(createProtocolWep8021X());
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<wep40-8021x> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite",
						"wep40-8021x", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isProtocolWep40_8021x()){
					protocolSuite.setWep408021X(createProtocolWep8021X());
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<wpa2-aes-8021x> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite",
						"wpa2-aes-8021x", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isProtocolWpa2Aes_8021x()){
					protocolSuite.setWpa2Aes8021X(createProtocolWpaAes8021XRoaming());
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<_802.1x> */
				if(securityImpl.isEnabled8021X()){
					Protocol802Dot1X protocol8021X=new Protocol802Dot1X();
					securityChildList_3.add(protocol8021X);
					protocolSuite.set8021X(protocol8021X);
				}
				
			}
			
			/** element: <security-object>.<security>.<auth-mode> */
			if(childObj instanceof SecurityAuthMode){
				SecurityAuthMode authMode = (SecurityAuthMode)childObj;
				
				/** attribute: operation */
				authMode.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<security>.<auth-method>.<port-based> */
				if(securityImpl.isConfigPortBased()){
					AuthModePortBased portBased = new AuthModePortBased();
					securityChildList_3.add(portBased);
					authMode.setPortBased(portBased);
				}
				
				/** element: <security-object>.<security>.<auth-method>.<host-based> */
				if(securityImpl.isConfigHostBased()){
					AuthModeHostBased hostBased = new AuthModeHostBased();
					securityChildList_3.add(hostBased);
					authMode.setHostBased(hostBased);
				}
			}
			
			/** element: <security-object>.<security>.<initial-auth-method>	*/
			if(childObj instanceof SecurityInitialAuthMethod){
				SecurityInitialAuthMethod authMethod = (SecurityInitialAuthMethod)childObj;
				
				/** attribute: operation */
				authMethod.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<security>.<initial-auth-method>.<macBasedAuth> */
				authMethod.setMacBasedAuth("");
			}
		}
		securityChildList_2.clear();
		generateSecurityObjectLevel_4();
	}
	
	private void generateSecurityObjectLevel_4() throws Exception{
		/**
		 * <security-object>.<web-server>.<web-page>.<mandatory-field>					SecurityWebServer.WebPage.MandatoryField
		 * <security-object>.<user-profile-deny>.<action>.<ban>							UserProfileDenyAction.Ban
		 * <security-object>.<user-profile-deny>.<action>.<ban-forever>					UpDenyBanDisconnect
		 * <security-object>.<user-profile-deny>.<action>.<disconnect>					UpDenyBanDisconnect
		 * <security-object>.<security>.<aaa>.<radius-server>							SecurityAaa.RadiusServer
		 *.<security-object>.<security>.<aaa>.<user-profile-mapping>					AaaUserProfileMapping
		 * <security-object>.<security>.<roaming>.<cache>								SsidRoamingCache
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>	SecurityAdditionalAuthMethod.CaptiveWebPortal
		 * <security-object>.<security>.<additional-auth-method>.<mac-based-auth>		SecurityAdditionalAuthMethod.MacBasedAuth
		 *<security-object>.<security>.<additional-auth-method>.<mobile-device-manager>	AuthMethodMobileDeviceManager	
		 *<security-object>.<security>.<auth-method>.<port-based>						AuthMethodPortBased
		 *<security-object>.<security>.<auth-method>.<host-based>						AuthMethodHostBased
		 */
		for(Object childObj : securityChildList_3){
			
			/** element: <security-object>.<web-server>.<web-page>.<mandatory-field> */
			if(childObj instanceof SecurityWebServer.WebPage.MandatoryField){
				SecurityWebServer.WebPage.MandatoryField mandatoryField = (SecurityWebServer.WebPage.MandatoryField)childObj;
				
				/** attribute: operation */
				mandatoryField.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/web-server/web-page", 
						"mandatory-field", GenerateXMLDebug.SET_VALUE,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				mandatoryField.setValue(securityImpl.getMandatoryFieldValue(mandatoryField));
				
				/** element: <security-object>.<web-server>.<web-page>.<mandatory-field>.<optional-field> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/web-server/web-page/mandatory-field", 
						"optional-field", GenerateXMLDebug.SET_VALUE,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				Object[][] parmOptionalField = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getOptionalFieldValue(new SecurityWebServer.WebPage.MandatoryField.OptionalField())},
				};
				mandatoryField.setOptionalField(
						(SecurityWebServer.WebPage.MandatoryField.OptionalField)
						CLICommonFunc.createObjectWithName(SecurityWebServer.WebPage.MandatoryField.OptionalField.class, parmOptionalField)
				);
			}
			
			/** element: <security-object>.<user-profile-deny>.<action>.<ban> */
			if(childObj instanceof UserProfileDenyAction.Ban){
				UserProfileDenyAction.Ban banObj = (UserProfileDenyAction.Ban)childObj;
				
				/** element: <security-object>.<user-profile-deny>.<action>.<ban>.<cr> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/user-profile-deny/action/ban", 
						"cr", GenerateXMLDebug.SET_VALUE,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				Object[][] crParm ={
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getBanValue()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				banObj.setCr(
						(UserProfileDenyAction.Ban.Cr)CLICommonFunc.createObjectWithName(UserProfileDenyAction.Ban.Cr.class, crParm)
				);
			}
			
			/** element: <security-object>.<user-profile-deny>.<action>.<ban-forever> 
			 * 			 <security-object>.<user-profile-deny>.<action>.<disconnect>
			 */
			if(childObj instanceof UpDenyBanDisconnect){
				UpDenyBanDisconnect banForeverObj = (UpDenyBanDisconnect)childObj;
				
				/** element: <ssid>.<user-profile-deny>.<action>.<ban-forever>|<disconnect>.<cr> */
				banForeverObj.setCr(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				
				/** element: <ssid>.<user-profile-deny>.<action>.<ban-forever>|<disconnect>.<strict> */
				banForeverObj.setStrict(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableStrict()));
			}
			
			/** element: <security-object>.<security>.<aaa>.<radius-server>	*/
			if(childObj instanceof SecurityAaa.RadiusServer){
				SecurityAaa.RadiusServer radiusServerObj = (SecurityAaa.RadiusServer)childObj;
				
				/** element: <security-object>.<security>.<aaa>.<radius-server>.<retry-interval> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/aaa/radius-server", 
						"retry-interval", GenerateXMLDebug.SET_VALUE,
						securityImpl.getRadiusAssGuiName(), securityImpl.getRadiusAssName());
				Object[][] retryParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getAAARadiusRetryInterval()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				radiusServerObj.setRetryInterval((RadiusRetryInterval)CLICommonFunc.createObjectWithName(RadiusRetryInterval.class, retryParm));
				
				/** element: <security-object>.<security>.<aaa>.<radius-server>.<account-interim-interval> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/aaa/radius-server", 
						"account-interim-interval", GenerateXMLDebug.SET_VALUE,
						securityImpl.getRadiusAssGuiName(), securityImpl.getRadiusAssName());
				Object[][] accountParam = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getAAARadiusAcctInterval()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				radiusServerObj.setAccountInterimInterval(
						(RadiusAccountInterimInterval)CLICommonFunc.createObjectWithName(RadiusAccountInterimInterval.class, accountParam)
				);
				
				/** element: <security-object>.<security>.<aaa>.<radius-server>.<dynamic-auth-extension> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/aaa/radius-server", 
						"dynamic-auth-extension", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getRadiusAssGuiName(), securityImpl.getRadiusAssName());
				radiusServerObj.setDynamicAuthExtension(CLICommonFunc.getAhOnlyAct(securityImpl.isDynamicAuthExtensionEnable()));
				
				/** element: <security-object>.<security>.<aaa>.<radius-server>.<primary> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/aaa/radius-server", 
						"primary", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getRadiusAssGuiName(), securityImpl.getRadiusAssName());
				if(securityImpl.isConfigRadiusServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary)){
					RadiusServerType primaryObj = new RadiusServerType();
					setRadiusServerType(primaryObj, AAAProfileInt.RADIUS_PRIORITY_TYPE.primary);
					radiusServerObj.setPrimary(primaryObj);
				}
				
				/** element: <security-object>.<security>.<aaa>.<radius-server>.<backup1> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/aaa/radius-server", 
						"backup1", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getRadiusAssGuiName(), securityImpl.getRadiusAssName());
				if(securityImpl.isConfigRadiusServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1)){
					RadiusServerType backup1Obj = new RadiusServerType();
					setRadiusServerType(backup1Obj, AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1);
					radiusServerObj.setBackup1(backup1Obj);
				}
				
				/** element: <security-object>.<security>.<aaa>.<radius-server>.<backup2> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/aaa/radius-server", 
						"backup2", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getRadiusAssGuiName(), securityImpl.getRadiusAssName());
				if(securityImpl.isConfigRadiusServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2)){
					RadiusServerType backup2Obj = new RadiusServerType();
					setRadiusServerType(backup2Obj, AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2);
					radiusServerObj.setBackup2(backup2Obj);
				}
				
				/** element: <security-object>.<security>.<aaa>.<radius-server>.<backup3> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/aaa/radius-server", 
						"backup3", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getRadiusAssGuiName(), securityImpl.getRadiusAssName());
				if(securityImpl.isConfigRadiusServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3)){
					RadiusServerType backup3Obj = new RadiusServerType();
					setRadiusServerType(backup3Obj, AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3);
					radiusServerObj.setBackup3(backup3Obj);
				}
				
				/** element: <security-object>.<security>.<aaa>.<radius-server>.<accounting> */
				RadiusAccountingAll accountObj = new RadiusAccountingAll();
				securityChildList_4.add(accountObj);
				radiusServerObj.setAccounting(accountObj);
				
				/** element: <security-object>.<security>.<aaa>.<radius-server>.<inject> */
				RadiusNasInject injectObj=new RadiusNasInject();
				securityChildList_4.add(injectObj);
				radiusServerObj.setInject(injectObj);
			}
			
			/** element: <security-object>.<security>.<aaa>.<user-profile-mapping> */
			if(childObj instanceof AaaUserProfileMapping){
				AaaUserProfileMapping userProfileMappingObj = (AaaUserProfileMapping)childObj;
				/** element: <security-object>.<security>.<aaa>.<user-profile-mapping><enable> */
				userProfileMappingObj.setEnable(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableAaaUserProfileMapping()));
				
				if(securityImpl.isEnableAaaUserProfileMapping()){
					/** element: <security-object>.<security>.<aaa>.<user-profile-mapping>.<vendor-id> */
					if(securityImpl.isConfigUserProfileMappingVendorId()){
						UserProfileMappingVendorId userVendorIdObj = new UserProfileMappingVendorId();
						userProfileMappingObj.setVendorId(userVendorIdObj);
						securityChildList_4.add(userVendorIdObj);
					}else{
						/** element: <security-object>.<security>.<aaa>.<user-profile-mapping>.<attribute-id> */
						oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/aaa/user-profile-mapping", 
								"attribute-id", GenerateXMLDebug.SET_VALUE,
								securityImpl.getRadiusAssGuiName(), securityImpl.getRadiusAssName());
						Object[][] attributeParam = {
								{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getUserProfileMappingAttributeId()},
								{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
						};
						userProfileMappingObj.setAttributeId((UserProfileMappingAttributeId)CLICommonFunc.createObjectWithName
								(UserProfileMappingAttributeId.class, attributeParam));
					}
				}
			}
			
			/** element: <security-object>.<security>.<roaming>.<cache>	*/
			if(childObj instanceof SsidRoamingCache){
				SsidRoamingCache cacheObj = (SsidRoamingCache)childObj;
				
				/** element: <security-object>.<security>.<roaming>.<cache>.<update-interval> */
				SsidCacheUpdateInterval updateIntervalObj = new SsidCacheUpdateInterval();
				securityChildList_4.add(updateIntervalObj);
				cacheObj.setUpdateInterval(updateIntervalObj);
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<mac-based-auth>	*/
			if(childObj instanceof SecurityAdditionalAuthMethod.MacBasedAuth){
				SecurityAdditionalAuthMethod.MacBasedAuth macAuthObj = (SecurityAdditionalAuthMethod.MacBasedAuth)childObj;
				
				/** element: <security-object>.<security>.<additional-auth-method>.<mac-based-auth>.<cr> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/mac-based-auth", 
						"cr", GenerateXMLDebug.SET_OPERATION, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				macAuthObj.setCr(CLICommonFunc.getAhOnlyAct(securityImpl.isMacBasedAuthEnable()));
				
				if(securityImpl.isMacBasedAuthEnable()){
					
					/** element: <security-object>.<security>.<additional-auth-method>.<mac-based-auth>.<auth-method> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/mac-based-auth", 
							"auth-method", GenerateXMLDebug.SET_VALUE,
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					macAuthObj.setAuthMethod(createAhAuthMethod(securityImpl.getMacAuthType()));
					
					/** element: <security-object>.<security>.<additional-auth-method>.<mac-based-auth>.<fallback-to-ecwp> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/mac-based-auth", 
							"fallback-to-ecwp", GenerateXMLDebug.SET_OPERATION,
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					if(securityImpl.isConfigFallbackToEcwp()){
						macAuthObj.setFallbackToEcwp(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableFallbackToEcwp()));
					}
				}
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<jss>*/
			/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>*/
			if(childObj instanceof AuthMethodMobileDeviceManager){
				AuthMethodMobileDeviceManager authMobileObj = (AuthMethodMobileDeviceManager)childObj;
				
				if (securityImpl.isMdmTypeEnabled(ConfigTemplateMdm.MDM_ENROLL_TYPE_JSS)) {
					MobileDeviceManagerJss jssObj = new MobileDeviceManagerJss();
					securityChildList_4.add(jssObj);
					authMobileObj.setJss(jssObj);
				}
				
				if (securityImpl.isMdmTypeEnabled(ConfigTemplateMdm.MDM_ENROLL_TYPE_AIRWATCH)) {
					MobileDeviceManagerAirwatch airwatchObj = new MobileDeviceManagerAirwatch();
					securityChildList_4.add(airwatchObj);
					authMobileObj.setAirwatch(airwatchObj);
				}

				if (securityImpl.isMdmTypeEnabled(ConfigTemplateMdm.MDM_ENROLL_TYPE_AEROHIVE)) {
					MobileDeviceManagerAerohive aerohiveObj = new MobileDeviceManagerAerohive();
					securityChildList_4.add(aerohiveObj);
					authMobileObj.setAerohive(aerohiveObj);
				}
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>	*/
			if(childObj instanceof SecurityAdditionalAuthMethod.CaptiveWebPortal){
				SecurityAdditionalAuthMethod.CaptiveWebPortal cwpObj = (SecurityAdditionalAuthMethod.CaptiveWebPortal)childObj;
				
				/** attribute: operation */
				cwpObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<reg-user-profile-attr> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal", 
						"reg-user-profile-attr", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isConfigCwpRegUserProfile()){
					
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal", 
							"reg-user-profile-attr", GenerateXMLDebug.SET_VALUE, 
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					Object[][] parmUserProfile = {
							{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getCwpRegUserProfileAttr()}
					};
					cwpObj.setRegUserProfileAttr(
							(SecurityAdditionalAuthMethod.CaptiveWebPortal.RegUserProfileAttr)CLICommonFunc.createObjectWithName(
									SecurityAdditionalAuthMethod.CaptiveWebPortal.RegUserProfileAttr.class, parmUserProfile)
					);
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<auth-user-profile-attr> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal",
						"auth-user-profile-attr", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isConfigCwpAuthUserProfile() ){
					
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal",
							"auth-user-profile-attr", GenerateXMLDebug.SET_VALUE,
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					Object[][] parmUserProfileAuth = {
							{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getCwpAuthUserProfileAttr()}
					};
					cwpObj.setAuthUserProfileAttr(
							(SecurityAdditionalAuthMethod.CaptiveWebPortal.AuthUserProfileAttr)
							CLICommonFunc.createObjectWithName(SecurityAdditionalAuthMethod.CaptiveWebPortal.AuthUserProfileAttr.class, parmUserProfileAuth)
					);
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<timeout> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal",
						"timeout", GenerateXMLDebug.SET_VALUE, 
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				Object[][] parmTimeOut = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getCWPTimeOutValue()}
				};
				cwpObj.setTimeout(
						(SecurityAdditionalAuthMethod.CaptiveWebPortal.Timeout)CLICommonFunc.createObjectWithName(
								SecurityAdditionalAuthMethod.CaptiveWebPortal.Timeout.class, parmTimeOut)
				);
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<auth-method> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal", 
						"auth-method", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				if(securityImpl.isConfigCWPAuthMethod()){
					
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal", 
							"auth-method", GenerateXMLDebug.SET_VALUE, 
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					cwpObj.setAuthMethod(this.createAhAuthMethod(securityImpl.getCwpAuthMethod()));
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<timer-display> */
				SecurityAdditionalAuthMethod.CaptiveWebPortal.TimerDisplay timerDisplayObj = new SecurityAdditionalAuthMethod.CaptiveWebPortal.TimerDisplay();
				securityChildList_4.add(timerDisplayObj);
				cwpObj.setTimerDisplay(timerDisplayObj);
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<internal-servers> */
				if(!securityImpl.isConfigExternalCwp()){
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal", 
							"internal-servers", GenerateXMLDebug.SET_OPERATION, 
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					cwpObj.setInternalServers(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableInternalServers()));
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<pass-through> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal", 
						"pass-through", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isConfigPasthrough()){
					SecurityAdditionalAuthMethod.CaptiveWebPortal.PassThrough pasThrough = new SecurityAdditionalAuthMethod.CaptiveWebPortal.PassThrough();
					securityChildList_4.add(pasThrough);
					cwpObj.setPassThrough(pasThrough);
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<redirect> */
				SecurityAdditionalAuthMethod.CaptiveWebPortal.Redirect redirectObj = new SecurityAdditionalAuthMethod.CaptiveWebPortal.Redirect();
				securityChildList_4.add(redirectObj);
				cwpObj.setRedirect(redirectObj);
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<external-server> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal",
						"external-server", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isConfigExternalCwp()){
					CwpExternalServer externalServerObj = new CwpExternalServer();
					securityChildList_4.add(externalServerObj);
					cwpObj.setExternalServer(externalServerObj);
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<internal-pages> */
				SecurityAdditionalAuthMethod.CaptiveWebPortal.InternalPages internalPageObj = new SecurityAdditionalAuthMethod.CaptiveWebPortal.InternalPages();
				securityChildList_4.add(internalPageObj);
				cwpObj.setInternalPages(internalPageObj);
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<success-redirect> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal",
						"success-redirect", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isSuccessRedirect()){
					SecurityAdditionalAuthMethod.CaptiveWebPortal.SuccessRedirect successRed = new SecurityAdditionalAuthMethod.CaptiveWebPortal.SuccessRedirect();
					securityChildList_4.add(successRed);
					cwpObj.setSuccessRedirect(successRed);
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<failure-redirect> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal", 
						"failure-redirect", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isFailureRedirect()){
					SecurityAdditionalAuthMethod.CaptiveWebPortal.FailureRedirect failureRed = new SecurityAdditionalAuthMethod.CaptiveWebPortal.FailureRedirect();
					securityChildList_4.add(failureRed);
					cwpObj.setFailureRedirect(failureRed);
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<login-page-method> */
				SecurityAdditionalAuthMethod.CaptiveWebPortal.LoginPageMethod loginMethod = new SecurityAdditionalAuthMethod.CaptiveWebPortal.LoginPageMethod();
				securityChildList_4.add(loginMethod);
				cwpObj.setLoginPageMethod(loginMethod);
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<walled-garden> */
				cwpObj.setWalledGarden(securityObj.getWalledGarden());
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<server-name> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal", 
						"server-name", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isConfigCwpServer()){
					CwpServerName serverObj = new CwpServerName();
					securityChildList_4.add(serverObj);
					cwpObj.setServerName(serverObj);
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<process-sip-info> */
				CwpProcessSipInfo sipInfo = new CwpProcessSipInfo();
				securityChildList_4.add(sipInfo);
				cwpObj.setProcessSipInfo(sipInfo);
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<check-use-policy> */
				if(securityImpl.isEnableUsePolicy()){
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal", 
							"check-use-policy", GenerateXMLDebug.CONFIG_ELEMENT,
							securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
					cwpObj.setCheckUsePolicy(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableUsePolicy()));
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<default-language> */
				CwpMultiLanguage defaultLanguage = new CwpMultiLanguage();
				securityChildList_4.add(defaultLanguage);
				cwpObj.setDefaultLanguage(defaultLanguage);
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<anonymous-access> */
				cwpObj.setAnonymousAccess(CLICommonFunc.getAhOnlyAct(securityImpl.isConfigCwpAnonymousAccess()));
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<self-reg-via-idm> */
				if(securityImpl.isConfigSelfRegViaIdm()){
					CwpSelfRegViaIdm selfIdm = new CwpSelfRegViaIdm();
					securityChildList_4.add(selfIdm);
					cwpObj.setSelfRegViaIdm(selfIdm);
				}
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<report-guest-info> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal", 
						"report-guest-info", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isEnableCWPAndSelfRegister()){
					cwpObj.setReportGuestInfo(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableReportedDataByCWP()));
				}
			}
			
			/** element: <security-object>.<security>.<protocol-suite>.<_802.1x> */
			if(childObj instanceof Protocol802Dot1X){
				Protocol802Dot1X protocol8021X = (Protocol802Dot1X)childObj;
				
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite/_802.1x",
						"cr", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				protocol8021X.setCr("");
				
//				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite/_802.1x",
//						"send-multicast-eap", GenerateXMLDebug.CONFIG_ELEMENT, 
//						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
//				protocol8021X.setSendMulticastEap(CLICommonFunc.getAhOnlyAct(false));
			}
			
			/** element: <security-object>.<security>.<auth-mode>.<port-based> */
			if(childObj instanceof AuthModePortBased){
				AuthModePortBased portBased = (AuthModePortBased)childObj;
				
				/** element: <security-object>.<security>.<auth-mode>.<port-based>.<cr> */
				portBased.setCr("");
				
				/** element: <security-object>.<security>.<auth-mode>.<port-based>.<failure-user-profile-attr> */
				if(securityImpl.isConfigPortBasedFailedVlan()){
					portBased.setFailureUserProfileAttr(CLICommonFunc.createAhIntActObj(
							securityImpl.getPortBasedFailedVlan(), CLICommonFunc.getYesDefault()));
				}
			}
			
			/** element: <security-object>.<security>.<auth-mode>.<host-based> */
			if(childObj instanceof AuthModeHostBased){
				AuthModeHostBased hostBased = (AuthModeHostBased)childObj;
				
				/** element: <security-object>.<security>.<auth-method>.<host-based>.<cr> */
				hostBased.setCr("");
				
				/** element: <security-object>.<security>.<auth-method>.<host-based>.<multiple-domain> */
				if(securityImpl.isConfigMultipleDomain()){
					hostBased.setMultipleDomain(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				}
			}
		}
		securityChildList_3.clear();
		generateSecurityObjectLevel_5();
	}
	
	private void generateSecurityObjectLevel_5() throws Exception{
		/**
		 * <security-object>.<security>.<aaa>.<radius-server>.<accounting>									RadiusAccountingAll
		 * <security-object>.<security>.<aaa>.<radius-server>.<inject>										RadiusNasInject
		 * <security-object>.<security>.<roaming>.<cache>.<update-interval>									SsidCacheUpdateInterval
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<timer-display>		SecurityAdditionalAuthMethod.CaptiveWebPortal.TimerDisplay
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<pass-through>		SecurityAdditionalAuthMethod.CaptiveWebPortal.PassThrough
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<redirect>			SecurityAdditionalAuthMethod.CaptiveWebPortal.Redirect
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<external-server>		CwpExternalServer
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<internal-pages>		SecurityAdditionalAuthMethod.CaptiveWebPortal.InternalPages
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<success-redirect>	SecurityAdditionalAuthMethod.CaptiveWebPortal.SuccessRedirect
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<failure-redirect>	SecurityAdditionalAuthMethod.CaptiveWebPortal.FailureRedirect
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<login-page-method>	SecurityAdditionalAuthMethod.CaptiveWebPortal.LoginPageMethod
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<server-name>			CwpServerName
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<process-sip-info>	CwpProcessSipInfo
		 * <security-object>.<security>.<aaa>.<user-profile-mapping>.<vendor-id> 							UserProfileMappingVendorId
		 * <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<jss>				MobileDeviceManagerJss
		 * <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>			MobileDeviceManagerAirwatch
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<self-reg-via-idm>	CwpSelfRegViaIdm
		 */
		for(Object childObj : securityChildList_4){
			
			/** element: <security-object>.<security>.<aaa>.<radius-server>.<accounting> */
			if(childObj instanceof RadiusAccountingAll){
				RadiusAccountingAll accountObj = (RadiusAccountingAll)childObj;
				
				/** element: <security-object>.<security>.<aaa>.<radius-server>.<accounting>.<primary> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/aaa/radius-server/accounting",
						"primary", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getRadiusAssGuiName(), securityImpl.getRadiusAssName());
				if(securityImpl.isConfigAcctRadiusServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary)){
					RadiusAccountingServer primaryObj = new RadiusAccountingServer();
					setRadiusServerTypeAcct(primaryObj, AAAProfileInt.RADIUS_PRIORITY_TYPE.primary);
					accountObj.setPrimary(primaryObj);
				}
				
				/** element: <security-object>.<security>.<aaa>.<radius-server>.<accounting>.<backup1> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/aaa/radius-server/accounting",
						"backup1", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getRadiusAssGuiName(), securityImpl.getRadiusAssName());
				if(securityImpl.isConfigAcctRadiusServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1)){
					RadiusAccountingServer backup1 = new RadiusAccountingServer();
					setRadiusServerTypeAcct(backup1, AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1);
					accountObj.setBackup1(backup1);
				}
				
				/** element: <security-object>.<security>.<aaa>.<radius-server>.<accounting>.<backup2> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/aaa/radius-server/accounting", 
						"backup2", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getRadiusAssGuiName(), securityImpl.getRadiusAssName());
				if(securityImpl.isConfigAcctRadiusServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2)){
					RadiusAccountingServer backup2 = new RadiusAccountingServer();
					setRadiusServerTypeAcct(backup2, AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2);
					accountObj.setBackup2(backup2);
				}
				
				/** element: <security-object>.<security>.<aaa>.<radius-server>.<accounting>.<backup3> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/aaa/radius-server/accounting", 
						"backup3", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getRadiusAssGuiName(), securityImpl.getRadiusAssName());
				if(securityImpl.isConfigAcctRadiusServer(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3)){
					RadiusAccountingServer backup3 = new RadiusAccountingServer();
					setRadiusServerTypeAcct(backup3, AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3);
					accountObj.setBackup3(backup3);
				}
			}
			
			/** element: <security-object>.<security>.<aaa>.<radius-server>.<inject> */
			if(childObj instanceof RadiusNasInject){
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/aaa/radius-server", 
						"inject", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getRadiusAssGuiName(), securityImpl.getRadiusAssName());
				RadiusNasInject injectObj = (RadiusNasInject)childObj;
				injectObj.setOperatorName(CLICommonFunc.getAhOnlyAct(securityImpl.isInjectOperatorNameEnable()));
			}
			
			/** element: <security-object>.<security>.<aaa>.<user-profile-mapping>.<vendor-id> */
			if(childObj instanceof UserProfileMappingVendorId){
				UserProfileMappingVendorId vendorIdObj = (UserProfileMappingVendorId)childObj;
				
				/** attribute: value */
				vendorIdObj.setValue(securityImpl.getUserProfileMappingVendorId());
				
				/** attribute: operation */
				vendorIdObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<security>.<aaa>.<user-profile-mapping>.<vendor-id>.<attribute-id> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/aaa/user-profile-mapping/vendor-id", 
						"attribute-id", GenerateXMLDebug.SET_VALUE,
						securityImpl.getRadiusAssGuiName(), securityImpl.getRadiusAssName());
				Object[][] attributeParam = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getUserProfileMappingAttributeId()}
				};
				vendorIdObj.setAttributeId(
						(UserProfileMappingVendorAttributeId)CLICommonFunc.createObjectWithName(
								UserProfileMappingVendorAttributeId.class, attributeParam)
				);
			}
			
			/** element: <security-object>.<security>.<roaming>.<cache>.<update-interval> */
			if(childObj instanceof SsidCacheUpdateInterval){
				SsidCacheUpdateInterval cacheObj = (SsidCacheUpdateInterval)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/roaming/cache",
						"update-interval", GenerateXMLDebug.SET_VALUE,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				cacheObj.setValue(securityImpl.getRoamingUpdateInterval());
				
				/** attribute: operation */
				cacheObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<security>.<roaming>.<cache>.<update-interval>.<ageout> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/roaming/cache/update-interval", 
						"ageout", GenerateXMLDebug.SET_VALUE,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				Object[][] ageoutParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getRoamingAgeout()}
				};
				cacheObj.setAgeout(
						(SsidCacheAgeout)CLICommonFunc.createObjectWithName(SsidCacheAgeout.class, ageoutParm)
				);
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<timer-display>	*/
			if(childObj instanceof SecurityAdditionalAuthMethod.CaptiveWebPortal.TimerDisplay){
				SecurityAdditionalAuthMethod.CaptiveWebPortal.TimerDisplay timerDisplayObj = (SecurityAdditionalAuthMethod.CaptiveWebPortal.TimerDisplay)childObj;
				
				/** attribute: operation */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal",
						"timer-display", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				timerDisplayObj.setOperation(CLICommonFunc.getAhEnumAct(securityImpl.isEnableCwpTimerDisplay()));
				
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/timer-display",
						"cr", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isEnableCwpTimerDisplay()){
					/** element: <ssid>.<security>.<additional-auth-method>.<captive-web-portal>.<timer-display>.<cr> */
					timerDisplayObj.setCr("");
				}
				
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/timer-display", 
						"new-window", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isEnableCwpTimerDisplay()){
					/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<timer-display>.<new-window> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/timer-display", 
							"new-window", GenerateXMLDebug.SET_OPERATION,
							securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
					timerDisplayObj.setNewWindow(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableNewWindow()));
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<timer-display>.<alert> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/timer-display", 
						"alert", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isEnableCwpTimerDisplay()){
					
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/timer-display", 
							"alert", GenerateXMLDebug.SET_VALUE,
							securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
					Object[][] alertObj = {
							{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getAlert()},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					timerDisplayObj.setAlert(
							(TimerDisplayAlert)CLICommonFunc.createObjectWithName(TimerDisplayAlert.class, alertObj)
					);
				}
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<pass-through> */
			if(childObj instanceof SecurityAdditionalAuthMethod.CaptiveWebPortal.PassThrough){
				SecurityAdditionalAuthMethod.CaptiveWebPortal.PassThrough pasThroughObj = (SecurityAdditionalAuthMethod.CaptiveWebPortal.PassThrough)childObj;
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<pass-through>.<vlan>	*/
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/pass-through", 
						"vlan", GenerateXMLDebug.SET_VALUE,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				Object[][] vlanParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getCwpExternalVlan()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				pasThroughObj.setVlan(
						(SecurityAdditionalAuthMethod.CaptiveWebPortal.PassThrough.Vlan)CLICommonFunc.createObjectWithName(
								SecurityAdditionalAuthMethod.CaptiveWebPortal.PassThrough.Vlan.class, vlanParm)
				);
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<redirect>	*/
			if(childObj instanceof SecurityAdditionalAuthMethod.CaptiveWebPortal.Redirect){
				SecurityAdditionalAuthMethod.CaptiveWebPortal.Redirect redirectObj = (SecurityAdditionalAuthMethod.CaptiveWebPortal.Redirect)childObj;
				
				/** attribute: operation */
				redirectObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<external-server> */
			if(childObj instanceof CwpExternalServer){
				CwpExternalServer externalObj = (CwpExternalServer)childObj;
				
				/** attribute: operation */
				externalObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<external-server>.<primary> */
				CwpExternalServerRank primaryObj = new CwpExternalServerRank();
				securityChildList_5.add(primaryObj);
				externalObj.setPrimary(primaryObj);
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<internal-pages> */
			if(childObj instanceof SecurityAdditionalAuthMethod.CaptiveWebPortal.InternalPages){
				SecurityAdditionalAuthMethod.CaptiveWebPortal.InternalPages internalPageObj = (SecurityAdditionalAuthMethod.CaptiveWebPortal.InternalPages)childObj;
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<internal-pages>.<no-failure-page> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/internal-pages", 
						"no-failure-page", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				internalPageObj.setNoFailurePage(CLICommonFunc.getAhOnlyAct(securityImpl.isCwpNoFailurePage()));
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<internal-pages>.<no-success-page> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/internal-pages", 
						"no-success-page", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				internalPageObj.setNoSuccessPage(CLICommonFunc.getAhOnlyAct(securityImpl.isCwpNoSuccessPage()));
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<success-redirect> */
			if(childObj instanceof SecurityAdditionalAuthMethod.CaptiveWebPortal.SuccessRedirect){
				SecurityAdditionalAuthMethod.CaptiveWebPortal.SuccessRedirect successRed = (SecurityAdditionalAuthMethod.CaptiveWebPortal.SuccessRedirect)childObj;
				
				/** attribute: operation */
				successRed.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<success-redirect>.<external-page>	*/
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/success-redirect",
						"external-page", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isSuccessRedirectExternal()){
					successRed.setExternalPage(this.createCwpRedirectExternalPage(
							securityImpl.getSuccessRedirectExternalURL(), securityImpl.getSuccessDelay()));
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<success-redirect>.<original-page>	*/
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/success-redirect", 
						"original-page", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isSuccessRedirectOriginal()){
					CwpRedirectOriginalPage originalObj = new CwpRedirectOriginalPage();
					securityChildList_5.add(originalObj);
					successRed.setOriginalPage(originalObj);
				}
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<failure-redirect> */
			if(childObj instanceof SecurityAdditionalAuthMethod.CaptiveWebPortal.FailureRedirect){
				SecurityAdditionalAuthMethod.CaptiveWebPortal.FailureRedirect failureObj = (SecurityAdditionalAuthMethod.CaptiveWebPortal.FailureRedirect)childObj;
				
				/** attribute: operation */
				failureObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<failure-redirect>.<external-page> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/failure-redirect", 
						"external-page", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isFailureRedirectExternal()){
					failureObj.setExternalPage(this.createCwpRedirectExternalPage(
							securityImpl.getFailureRedirectExternalURL(), securityImpl.getFailureDelay()));
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<failure-redirect>.<login-page> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/failure-redirect", 
						"login-page", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isFailureRedirectLogin()){
					CwpRedirectLoginPage loginObj = new CwpRedirectLoginPage();
					securityChildList_5.add(loginObj);
					failureObj.setLoginPage(loginObj);
				}
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<login-page-method> */
			if(childObj instanceof SecurityAdditionalAuthMethod.CaptiveWebPortal.LoginPageMethod){
				SecurityAdditionalAuthMethod.CaptiveWebPortal.LoginPageMethod loginMethod = (SecurityAdditionalAuthMethod.CaptiveWebPortal.LoginPageMethod)childObj;
				
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/login-page-method", 
						"http302", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<login-page-method>.<http302> */
				loginMethod.setHttp302(CLICommonFunc.getAhOnlyAct(securityImpl.isEnablehttp302()));
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<server-name> */
			if(childObj instanceof CwpServerName){
				CwpServerName serverObj = (CwpServerName)childObj;
				
				/** attribute: operation */
				serverObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<server-name>.<cr> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/server-name", 
						"cr", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isConfigServerName()){
					
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/server-name", 
							"cr", GenerateXMLDebug.SET_VALUE,
							securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
					serverObj.setCr(CLICommonFunc.createAhStringObj(securityImpl.getCwpServerName()));
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<server-name>.<cert-cn> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/server-name", 
						"cert-cn", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isConfigCertDN()){
					serverObj.setCertDn("");
				}
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<process-sip-info> */
			if(childObj instanceof CwpProcessSipInfo){
				CwpProcessSipInfo sipInfo = (CwpProcessSipInfo)childObj;
				
				/** attribute: operation */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal", 
						"process-sip-info", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				sipInfo.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));

				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<process-sip-info>.<cr> */
				sipInfo.setCr("");

				if(securityImpl.isEnableProcessSipInfo()){
					
					/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<process-sip-info>.<block-redirect> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/process-sip-info", 
							"block-redirect", GenerateXMLDebug.SET_VALUE,
							securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
					sipInfo.setBlockRedirect(CLICommonFunc.createAhStringActObj(securityImpl.getProcessSipInfo(), CLICommonFunc.getYesDefault()));
				}
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<jss> */
			if(childObj instanceof MobileDeviceManagerJss){
				MobileDeviceManagerJss jssObj = (MobileDeviceManagerJss)childObj;
				
				/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager><jss><enable> */
				jssObj.setEnable(CLICommonFunc.getAhOnlyAct(securityImpl.isMdmTypeEnabled(ConfigTemplateMdm.MDM_ENROLL_TYPE_JSS)));
				
				if(securityImpl.isMdmTypeEnabled(ConfigTemplateMdm.MDM_ENROLL_TYPE_JSS)){
					
					/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<jss>.<url-root-path> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/mobile-device-manager/jss",
							"url-root-path", GenerateXMLDebug.CONFIG_ELEMENT, 
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					jssObj.setUrlRootPath(CLICommonFunc.createAhStringActObj(securityImpl.getMdmRootURLPath(), CLICommonFunc.getYesDefault()));
					
					/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<jss>.<http-auth> */
					MdmHttpAuth authObj = new MdmHttpAuth();
					securityChildList_5.add(authObj);
					jssObj.setHttpAuth(authObj);
					
					/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<jss>.<os-object> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/mobile-device-manager/jss",
							"os-object", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					if(securityImpl.isConfigMdmOsObject(SecurityObjectProfileInt.MDM_OSOBJECT_IOS)){
						jssObj.getOsObject().add(createMdmOsObject(SecurityObjectProfileInt.MDM_OSOBJECT_IOS));
					}
					if(securityImpl.isConfigMdmOsObject(SecurityObjectProfileInt.MDM_OSOBJECT_MACOS)){
						jssObj.getOsObject().add(createMdmOsObject(SecurityObjectProfileInt.MDM_OSOBJECT_MACOS));
					}
				}	
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch> */
			if(childObj instanceof MobileDeviceManagerAirwatch){
				MobileDeviceManagerAirwatch airwatchObj = (MobileDeviceManagerAirwatch)childObj;
				
				/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager><airwatch><enable> */
				airwatchObj.setEnable(CLICommonFunc.getAhOnlyAct(securityImpl.isMdmTypeEnabled(ConfigTemplateMdm.MDM_ENROLL_TYPE_AIRWATCH)));
				
				if(securityImpl.isMdmTypeEnabled(ConfigTemplateMdm.MDM_ENROLL_TYPE_AIRWATCH)){
					
					/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<url-root-path> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/mobile-device-manager/airwatch",
							"url-root-path", GenerateXMLDebug.CONFIG_ELEMENT, 
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					airwatchObj.setUrlEnrollment(CLICommonFunc.createAhStringActObj(securityImpl.getMdmRootURLPath(), CLICommonFunc.getYesDefault()));
					airwatchObj.setUrlRestApi(CLICommonFunc.createAhStringActObj(securityImpl.getMdmApiURL(), CLICommonFunc.getYesDefault()));
					airwatchObj.setApiKey(CLICommonFunc.createAhStringActObj(securityImpl.getMdmApiKey(), CLICommonFunc.getYesDefault()));

					
					/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<http-auth> */
					MdmHttpAuth authObj = new MdmHttpAuth();
					securityChildList_5.add(authObj);
					airwatchObj.setHttpAuth(authObj);
					
					/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<os-object> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/mobile-device-manager/airwatch",
							"os-object", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					if(securityImpl.isConfigMdmOsObject(SecurityObjectProfileInt.MDM_OSOBJECT_IOS)){
						airwatchObj.getOsObject().add(CLICommonFunc.createAhNameActValue(SecurityObjectProfileInt.MDM_OBJECT_IOS, true));
					}
					if(securityImpl.isConfigMdmOsObject(SecurityObjectProfileInt.MDM_OSOBJECT_MACOS)){
						airwatchObj.getOsObject().add(CLICommonFunc.createAhNameActValue(SecurityObjectProfileInt.MDM_OBJECT_MACOS, true));
					}
					if(securityImpl.isConfigMdmOsObject(SecurityObjectProfileInt.MDM_OSOBJECT_SYMBIAN)){
						airwatchObj.getOsObject().add(CLICommonFunc.createAhNameActValue(SecurityObjectProfileInt.MDM_OBJECT_SYMBIAN, true));
					}
					if(securityImpl.isConfigMdmOsObject(SecurityObjectProfileInt.MDM_OSOBJECT_BLACKBERRY)){
						airwatchObj.getOsObject().add(CLICommonFunc.createAhNameActValue(SecurityObjectProfileInt.MDM_OBJECT_BLACKBERRY, true));
					}
					if(securityImpl.isConfigMdmOsObject(SecurityObjectProfileInt.MDM_OSOBJECT_ANDROID)){
						airwatchObj.getOsObject().add(CLICommonFunc.createAhNameActValue(SecurityObjectProfileInt.MDM_OBJECT_ANDROID, true));
					}
					if(securityImpl.isConfigMdmOsObject(SecurityObjectProfileInt.MDM_OSOBJECT_WINDOWSPHONE)){
						airwatchObj.getOsObject().add(CLICommonFunc.createAhNameActValue(SecurityObjectProfileInt.MDM_OBJECT_WINDOWSPHONE, true));
					}
					if(securityImpl.isConfigMdmOsObject(SecurityObjectProfileInt.MDM_OSOBJECT_CHROME)){
                        airwatchObj.getOsObject().add(CLICommonFunc.createAhNameActValue(SecurityObjectProfileInt.MDM_OBJECT_CHROME, true));
                    }
					
					if (securityImpl.isEnableMdmNonCompliance()) {
						/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<non-compliant> */
	                    MdmAirwatchNonCompliant nonCompliant = new MdmAirwatchNonCompliant();
	                    securityChildList_5.add(nonCompliant);
	                    airwatchObj.setNonCompliant(nonCompliant);
	                    
	                    /** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<poll-status> */
	                    MdmAirwatchPollStatus pollStatus = new MdmAirwatchPollStatus();
	                    securityChildList_5.add(pollStatus);
	                    airwatchObj.setPollStatus(pollStatus);
					}
				}
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<aerohive>.<enable> */
			/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<aerohive>.<url-root-path> */
			/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<aerohive>.<os-object> */
			/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<aerohive>.<api-key> */
			/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<aerohive>.<onboard> */
			if(childObj instanceof MobileDeviceManagerAerohive){
				MobileDeviceManagerAerohive aerohiveObj = (MobileDeviceManagerAerohive)childObj;
				
				/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager><aerohive><enable> */
				aerohiveObj.setEnable(CLICommonFunc.getAhOnlyAct(true));
				
				/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<aerohive>.<url-root-path> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/mobile-device-manager/aerohive",
						"url-root-path", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				aerohiveObj.setUrlRootPath(CLICommonFunc.createAhStringActObj(securityImpl.getMdmRootURLPath(), CLICommonFunc.getYesDefault()));
				
				MdmAerohiveApiKey apiKey = new MdmAerohiveApiKey();
				securityChildList_5.add(apiKey);
				aerohiveObj.setApiKey(apiKey);
				
				if (securityImpl.getOnboardSsid() != null) {
					MdmAerohiveOnboard onboardSsid = new MdmAerohiveOnboard();
					securityChildList_5.add(onboardSsid);
					aerohiveObj.setOnboard(onboardSsid);
				}

				/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<aerohive>.<os-object> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/mobile-device-manager/aerohive",
						"os-object", GenerateXMLDebug.CONFIG_ELEMENT, 
					securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				aerohiveObj.getOsObject().add(CLICommonFunc.createAhNameActValue(SecurityObjectProfileInt.MDM_OBJECT_IOS, true));
				aerohiveObj.getOsObject().add(CLICommonFunc.createAhNameActValue(SecurityObjectProfileInt.MDM_OBJECT_MACOS, true));
				aerohiveObj.getOsObject().add(CLICommonFunc.createAhNameActValue(SecurityObjectProfileInt.MDM_OBJECT_ANDROID, true));
				aerohiveObj.getOsObject().add(CLICommonFunc.createAhNameActValue(SecurityObjectProfileInt.MDM_OBJECT_CHROME, true));
			}	
			
			/** <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<default-language>	CwpMultiLanguage */
			if(childObj instanceof CwpMultiLanguage){
				CwpMultiLanguage language = (CwpMultiLanguage)childObj;
				language.setValue(securityImpl.getCWPLanguageValue());
				language.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<self-reg-via-idm> */
			if(childObj instanceof CwpSelfRegViaIdm){
				CwpSelfRegViaIdm selfIdmObj = (CwpSelfRegViaIdm)childObj;
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<self-reg-via-idm>.<cr> */
				selfIdmObj.setCr(CLICommonFunc.getAhOnlyAct(securityImpl.isConfigSelfRegViaIdm()));
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<self-reg-via-idm>.<api> */
				if(securityImpl.isConfigSelfRegViaIdmApi()){
					selfIdmObj.setApi(CLICommonFunc.createAhStringActObj(
							securityImpl.getSelfRegViaIdmApi(), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<self-reg-via-idm>.<crl-file> */
				if(securityImpl.isConfigRegViaIdmCrlFile()){
					selfIdmObj.setCrlFile(CLICommonFunc.createAhStringActObj(
							securityImpl.getSelfRegViaIdmCrlFile(), CLICommonFunc.getYesDefault()));
				}
			}
		}
		securityChildList_4.clear();
		generateSecurityObjectLevel_6();
	}
	
	private void generateSecurityObjectLevel_6() throws Exception{
		/**
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<external-server>.<primary>				CwpExternalServerRank
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<success-redirect>.<original-page>		CwpRedirectOriginalPage
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<failure-redirect>.<login-page>			CwpRedirectLoginPage
		 * <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<jss>.<http-auth>						HttpAuth
		 * <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<http-auth>					HttpAuth
		 */
		for(Object childObj : securityChildList_5){
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<external-server>.<primary> */
			if(childObj instanceof CwpExternalServerRank){
				CwpExternalServerRank primaryObj = (CwpExternalServerRank)childObj;
				
				/** attribute: operation */
				primaryObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<external-server>.<primary>.<login-page> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/external-server/primary", 
						"login-page", GenerateXMLDebug.SET_OPERATION,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isConfigExCwpUrl()){
					primaryObj.setLoginPage(CLICommonFunc.createAhStringObj(securityImpl.getExCwpUrl()));
				}
				
				if(!securityImpl.isUseForPpskServer()){
					
					/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<external-server>.<primary>.<password-encryption> */
					if(securityImpl.isEcwpDefault()){
						if(securityImpl.isConfigExCwpPassBasic() || securityImpl.isConfigExCwpPassShared()){
							CwpExternalServerRank.PasswordEncryption passwordEncryptionObj = new CwpExternalServerRank.PasswordEncryption();
							securityChildList_6.add(passwordEncryptionObj);
							primaryObj.setPasswordEncryption(passwordEncryptionObj);
						}
					}
					
					/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<external-server>.<primary>.<no-roaming-at-login> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/external-server/primary", 
							"no-roaming-at-login", GenerateXMLDebug.CONFIG_ELEMENT,
							securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
					if(securityImpl.isEcwpNnu()){
						primaryObj.setNoRoamingAtLogin(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableNoRoamingAtLogin()));
					}
					
					if(securityImpl.isEcwpDepaul()){
						
						/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<external-server>.<primary>.<no-radius-auth> */
						oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/external-server/primary", 
								"no-radius-auth", GenerateXMLDebug.SET_OPERATION,
								securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
						primaryObj.setNoRadiusAuth(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableNoRadiusAuth()));
						
						/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<external-server>.<primary>.<success-register> */
						oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/external-server/primary", 
								"success-register", GenerateXMLDebug.SET_OPERATION,
								securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
						primaryObj.setSuccessRegister(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableSuccessRegister()));
					}
				}
				
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<success-redirect>.<original-page> */
			if(childObj instanceof CwpRedirectOriginalPage){
				CwpRedirectOriginalPage originalObj = (CwpRedirectOriginalPage)childObj;
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<success-redirect>.<original-page>.<cr> */
				originalObj.setCr("");
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<success-redirect>.<original-page>.<delay> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/success-redirect/original-page", 
						"delay", GenerateXMLDebug.SET_VALUE,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				Object[][] delayParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getSuccessDelay()}
				};
				originalObj.setDelay(
						(CwpRedirectDelay)CLICommonFunc.createObjectWithName(CwpRedirectDelay.class, delayParm));
			}
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<failure-redirect>.<login-page>	*/
			if(childObj instanceof CwpRedirectLoginPage){
				CwpRedirectLoginPage loginObj = (CwpRedirectLoginPage)childObj;
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<failure-redirect>.<login-page>.<cr> */
				loginObj.setCr("");
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<failure-redirect>.<login-page>.<delay> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/failure-redirect/original-page", 
						"delay", GenerateXMLDebug.SET_VALUE,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				Object[][] delayParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getFailureDelay()}
				};
				loginObj.setDelay(
						(CwpRedirectDelay)CLICommonFunc.createObjectWithName(CwpRedirectDelay.class, delayParm));
			}
			/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<jss>.<http-auth> */
			/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<http-auth> */
			if(childObj instanceof MdmHttpAuth){
				MdmHttpAuth authObj = (MdmHttpAuth)childObj;
				
				/** attribute: operation */
				authObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<jss>.<http-auth>.<user> */
				/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<http-auth>.<user> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/mobile-device-manager/jss/http-auth",
						"user", GenerateXMLDebug.CONFIG_ELEMENT, 
						securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
				authObj.setUser(createHttpAuthUserPassword(securityImpl.getMdmHttpAuthUser(), securityImpl.getMdmHttpAuthPassword()));
			}
			if (childObj instanceof MdmAerohiveApiKey) {
				MdmAerohiveApiKey apiKey = (MdmAerohiveApiKey)childObj;
				apiKey.setValue(securityImpl.getMdmApiKey());
				apiKey.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				apiKey.setApiInstanceId(CLICommonFunc.createAhStringObj(securityImpl.getMdmApiInstanceId()));
			}
			if (childObj instanceof MdmAerohiveOnboard) {
				MdmAerohiveOnboard onboard = (MdmAerohiveOnboard)childObj;
				onboard.setAccessSsid(CLICommonFunc.createAhNameActObj(securityImpl.getOnboardSsid(), true));
			}
			
			
			if (childObj instanceof MdmAirwatchNonCompliant) {
			    MdmAirwatchNonCompliant nonCompliant = (MdmAirwatchNonCompliant) childObj;
			    
			    /** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<non-compliant>.<gust-upid> */
			    if (securityImpl.getMdmGuestUserProfileId() > 0) {
			        nonCompliant.setGuestUpid(CLICommonFunc.createAhIntActObj(securityImpl.getMdmGuestUserProfileId(), true));
			    }
			    
			    /** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<non-compliant>.<send-message> */
			    MdmAirwatchSendMessage sendMessage = new MdmAirwatchSendMessage();
			    nonCompliant.setSendMessage(sendMessage);
			    securityChildList_6.add(sendMessage);
			    
			    /** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<non-compliant>.<disconnect-for-vlan-change> */
			    if (securityImpl.isEnableMdmDisconnectForVlanChange()) {
			        nonCompliant.setDisconnectForVlanChange(CLICommonFunc.getAhOnlyAct(true));
			    }
			}
            
			if (childObj instanceof MdmAirwatchPollStatus) {
			    MdmAirwatchPollStatus pollStatus = (MdmAirwatchPollStatus) childObj;
			    
			    /** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<poll-status>.<cr> */
			    pollStatus.setCr("");
			    
			    /** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<poll-status>.<interval> */
			    pollStatus.setInterval(CLICommonFunc.createAhIntActObj(securityImpl.getMdmPollStatusInterval(), true));
			    pollStatus.setOperation(CLICommonFunc.getAhEnumAct(true));
			}
            
			
		}
		securityChildList_5.clear();
		generateSecurityObjectLevel_7();
	}
	
	private void generateSecurityObjectLevel_7() throws Exception{
		/**
		 * <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<external-server>.<primary>.<password-encryption>		CwpExternalServerRank.PasswordEncryption
		 */
		for(Object childObj : securityChildList_6){
			
			/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<external-server>.<primary>.<password-encryption> */
			if(childObj instanceof CwpExternalServerRank.PasswordEncryption){
				CwpExternalServerRank.PasswordEncryption passwrodObj = (CwpExternalServerRank.PasswordEncryption)childObj;
				
				/** attribute: operation */
				passwrodObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<external-server>.<primary>.<password-encryption>.<uam-basic> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/external-server/primary/password-encryption", 
						"uam-basic", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isConfigExCwpPassBasic()){
					passwrodObj.setUamBasic("");
				}
				
				/** element: <security-object>.<security>.<additional-auth-method>.<captive-web-portal>.<external-server>.<primary>.<password-encryption>.<uam-shared> */
				oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/external-server/primary/password-encryption", 
						"uam-shared", GenerateXMLDebug.CONFIG_ELEMENT,
						securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
				if(securityImpl.isConfigExCwpPassShared()){
					
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/additional-auth-method/captive-web-portal/external-server/primary/password-encryption", 
							"uam-shared", GenerateXMLDebug.SET_VALUE,
							securityImpl.getCwpGuiKey(), securityImpl.getCwpName());
					passwrodObj.setUamShared(CLICommonFunc.createAhEncryptedString(securityImpl.getExCwpPassSharedValue()));
				}
			}
			
			if(childObj instanceof MdmAirwatchSendMessage){
			    MdmAirwatchSendMessage sendMessage = (MdmAirwatchSendMessage) childObj;
			    
			    /** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<non-compliant>.<send-message>.<type> */
			    MdmAirwatchSendMessageType type = new MdmAirwatchSendMessageType();
			    if (securityImpl.isEnableMdmSendMessageViaEmail()) {
			        type.setEmail(CLICommonFunc.getAhOnlyAct(true));
			    }
			    if (securityImpl.isEnableMdmSendMessageViaPush()) {
			        type.setPush(CLICommonFunc.getAhOnlyAct(true));
			        
			    }
			    if (securityImpl.isEnableMdmSendMessageViaSms()) {
			        type.setSms(CLICommonFunc.getAhOnlyAct(true));
			    }
			    sendMessage.setType(type);
			    
			    /** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<non-compliant>.<send-message>.<title> */
			    if (StringUtils.isNotBlank(securityImpl.getMdmSendMessageTitle())) {
			    	sendMessage.setTitle(CLICommonFunc.createAhStringActObj(securityImpl.getMdmSendMessageTitle(), true));
			    }

			    /** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<non-compliant>.<send-message>.<content> */
	            sendMessage.setContent(CLICommonFunc.createAhStringActObj(securityImpl.getMdmSendMessageContent(), true));
	                
			}
		}
		securityChildList_6.clear();
	}
	
	private ProtocolWpaAes8021XRoaming createProtocolWpaAes8021XRoaming() throws Exception{
		ProtocolWpaAes8021XRoaming wpaAesRoamingObj = new ProtocolWpaAes8021XRoaming();
		
		List<Object> childLevel_1 = new ArrayList<Object>();
		
		/***
		 * generate child levle 1
		 */
		{
			/** attribute: operation */
			wpaAesRoamingObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			
			/** element: <cr> */
			wpaAesRoamingObj.setCr("");
			
			/** element: <strict> */
			if(securityImpl.isConfigureProtocolStrict()){
				wpaAesRoamingObj.setStrict(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <non-strict> */
			if(securityImpl.isConfigureProtocolNoStrict()){
				wpaAesRoamingObj.setNonStrict(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <gmk-rekey-period> */
			Object[][] gmkParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGmkRekeyPeriod()}
			};
			wpaAesRoamingObj.setGmkRekeyPeriod(
					(ProtocolGmkRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolGmkRekeyPeriod.class, gmkParm)
			);
			
			/** element: <old-gmk-rekey-period> */
			if(securityImpl.getProtocolGmkRekeyPeriod() != 0){
				Object[][] gmkParmOld = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGmkRekeyPeriod()}
				};
				wpaAesRoamingObj.setOldGmkRekeyPeriod(
						(ProtocolGmkRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolGmkRekeyPeriod.class, gmkParmOld)
				);
			}
			
			/** element: <rekey-period> */
			Object[][] rekeyParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolRekeyPeriod()}
			};
			wpaAesRoamingObj.setRekeyPeriod(
					(ProtocolRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolRekeyPeriod.class, rekeyParm)
			);
			
			/** element: <old-rekey-period> */
			if(securityImpl.getProtocolRekeyPeriod() != 0){
				Object[][] rekeyParmOld = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolRekeyPeriod()}
				};
				wpaAesRoamingObj.setOldRekeyPeriod(
						(ProtocolRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolRekeyPeriod.class, rekeyParmOld)
				);
			}
			
			/** element: <ptk-timeout> */
			Object[][] ptkTimeOutParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolPtkTimeout()}
			};
			wpaAesRoamingObj.setPtkTimeout(
					(ProtocolPtkTimeout)CLICommonFunc.createObjectWithName(ProtocolPtkTimeout.class, ptkTimeOutParm)
			);
			
			/** element: <ptk-retry> */
			Object[][] ptkRetryParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolPtkRetry()}
			};
			wpaAesRoamingObj.setPtkRetry(
					(ProtocolPtkRetry)CLICommonFunc.createObjectWithName(ProtocolPtkRetry.class, ptkRetryParm)
			);
			
			/** element: <gtk-timeout> */
			Object[][] gtkTimeOutParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGtkTimeout()}
			};
			wpaAesRoamingObj.setGtkTimeout(
					(ProtocolGtkTimeout)CLICommonFunc.createObjectWithName(ProtocolGtkTimeout.class, gtkTimeOutParm)
			);
			
			/** element: <gtk-retry> */
			Object[][] gtkRetryParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGtkRetry()}
			};
			wpaAesRoamingObj.setGtkRetry(
					(ProtocolGtkRetry)CLICommonFunc.createObjectWithName(ProtocolGtkRetry.class, gtkRetryParm)
			);
			
			/** element: <roaming> */
			ProtocolRoaming roamingObj = new ProtocolRoaming();
			childLevel_1.add(roamingObj);
			wpaAesRoamingObj.setRoaming(roamingObj);
			
			/** element: <replay-window> */
			Object[][] replayParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolReplayWindow()},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			wpaAesRoamingObj.setReplayWindow(
					(ProtocolReplayWindow)CLICommonFunc.createObjectWithName(ProtocolReplayWindow.class, replayParm)
			);

			/** element: <ptk-rekey-period> */
			Object[][] ptkRekeyParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getPtkRekeyPeriod()}
			};
			wpaAesRoamingObj.setPtkRekeyPeriod(
					(ProtocolPtkRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolPtkRekeyPeriod.class, ptkRekeyParm)
			);

			/** element: <reauth-interval> */
			ProtocolReauthInterval reauthObj = new ProtocolReauthInterval();
			childLevel_1.add(reauthObj);
			wpaAesRoamingObj.setReauthInterval(reauthObj);
			
			/** element: <pmf> */
			if(securityImpl.isEnable80211w()){
				ProtocolWpaAesMfp mfp = new ProtocolWpaAesMfp();
				childLevel_1.add(mfp);
				wpaAesRoamingObj.setMfp(mfp);
			}
		}
		
		/***
		 * generate level 2
		 * 
		 * <roaming>			ProtocolRoaming
		 * <reauth-interval>		ProtocolReauthInterval
		 */
		{
			for(Object childObj : childLevel_1){
				
				/** element: <roaming> */
				if(childObj instanceof ProtocolRoaming){
					ProtocolRoaming roamingObj = (ProtocolRoaming)childObj;
					
					/** element: <roaming>.<proactive-pmkid-response> */
					roamingObj.setProactivePmkidResponse(
							CLICommonFunc.getAhOnlyAct(securityImpl.isRoamingProactivePmkidResponse())
					);
				}
				
				/** element: <reauth-interval> */
				if(childObj instanceof ProtocolReauthInterval){
					ProtocolReauthInterval reauthObj = (ProtocolReauthInterval)childObj;
					
					/** attribute: operation */
					reauthObj.setOperation(CLICommonFunc.getAhEnumAct(securityImpl.isEnableReauthInterval()));
					
					/** element: <reauth-interval>.<cr> */
					if(securityImpl.isEnableReauthInterval()){
						Object[][] crParm = {
								{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getReauthIntervalValue()}
						};
						reauthObj.setCr(
								(ReauthIntervalCr)CLICommonFunc.createObjectWithName(ReauthIntervalCr.class, crParm)
						);
					}
				}
				
				if(childObj instanceof ProtocolWpaAesMfp){
					ProtocolWpaAesMfp mfpObj = (ProtocolWpaAesMfp)childObj;
					/** attribute: operation */
					mfpObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
					ProtocolMfpMethod mfpMethod = createProtocolMfpMethod(securityImpl.isEnableBip());
					if(securityImpl.isConfigmfpMandatory()){
						/** element: <pmf>.<mandatory> */
						mfpObj.setMandatory(mfpMethod);
					}
					
					if(securityImpl.isConfigmfpOptional()){
						/** element: <pmf>.<optional> */
						mfpObj.setOptional(mfpMethod);
					}
				}
			}
			childLevel_1.clear();
		}
		
		return wpaAesRoamingObj;
	}
	
	private ProtocolWep8021X createProtocolWep8021X() throws Exception {
		
		ProtocolWep8021X suiteWepObj = new ProtocolWep8021X();
		
		/***
		 * generate child level 1
		 */
		{
			/** attribute: operation */
			suiteWepObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			
			/** element: <cr> */
			suiteWepObj.setCr("");
			
			/** element: <rekey-period>	*/
			Object[][] rekeyParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolRekeyPeriod()}
			};
			suiteWepObj.setRekeyPeriod(
					(ProtocolWepRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolWepRekeyPeriod.class, rekeyParm)
			);
		}
		
		return suiteWepObj;
	}
	
	private ProtocolWpaAes8021X createProtocolWpaAes8021X() throws Exception{
		
		ProtocolWpaAes8021X wpaAes8021Obj = new ProtocolWpaAes8021X();
		
		List<Object> childLevel_1 = new ArrayList<Object>();
		
		/**
		 * generate level_1
		 */
		{
			/** attribute: operation */
			wpaAes8021Obj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			
			/** element: <cr> */
			wpaAes8021Obj.setCr("");
			
			/** element: <strict> */
			if(securityImpl.isConfigureProtocolStrict()){
				wpaAes8021Obj.setStrict(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <non-strict> */
			if(securityImpl.isConfigureProtocolNoStrict()){
				wpaAes8021Obj.setNonStrict(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <gmk-rekey-period> */
			Object[][] gmkParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGmkRekeyPeriod()}
			};
			wpaAes8021Obj.setGmkRekeyPeriod(
					(ProtocolGmkRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolGmkRekeyPeriod.class, gmkParm)
			);
			
			/** element: <old-gmk-rekey-period> */
			if(securityImpl.getProtocolGmkRekeyPeriod() != 0){
				Object[][] gmkParmOld = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGmkRekeyPeriod()}
				};
				wpaAes8021Obj.setOldGmkRekeyPeriod(
						(ProtocolGmkRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolGmkRekeyPeriod.class, gmkParmOld)
				);
			}
			
			/** element: <rekey-period> */
			Object[][] rekeyParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolRekeyPeriod()}
			};
			wpaAes8021Obj.setRekeyPeriod(
					(ProtocolRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolRekeyPeriod.class, rekeyParm)
			);
			
			/** element: <old-rekey-period> */
			if(securityImpl.getProtocolRekeyPeriod() != 0){
				Object[][] rekeyParmOld = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolRekeyPeriod()}
				};
				wpaAes8021Obj.setOldRekeyPeriod(
						(ProtocolRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolRekeyPeriod.class, rekeyParmOld)
				);
			}
			
			/** element: <ptk-timeout> */
			Object[][] ptkTimeOutParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolPtkTimeout()}
			};
			wpaAes8021Obj.setPtkTimeout(
					(ProtocolPtkTimeout)CLICommonFunc.createObjectWithName(ProtocolPtkTimeout.class, ptkTimeOutParm)
			);
			
			/** element: <ptk-retry> */
			Object[][] ptkRetryParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolPtkRetry()}
			};
			wpaAes8021Obj.setPtkRetry(
					(ProtocolPtkRetry)CLICommonFunc.createObjectWithName(ProtocolPtkRetry.class, ptkRetryParm)
			);
			
			/** element: <gtk-timeout> */
			Object[][] gtkTimeOutParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGtkTimeout()}
			};
			wpaAes8021Obj.setGtkTimeout(
					(ProtocolGtkTimeout)CLICommonFunc.createObjectWithName(ProtocolGtkTimeout.class, gtkTimeOutParm)
			);
			
			/** element: <gtk-retry> */
			Object[][] gtkRetryParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGtkRetry()}
			};
			wpaAes8021Obj.setGtkRetry(
					(ProtocolGtkRetry)CLICommonFunc.createObjectWithName(ProtocolGtkRetry.class, gtkRetryParm)
			);
			
			/** element: <replay-window> */
			Object[][] replayParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolReplayWindow()},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			wpaAes8021Obj.setReplayWindow(
					(ProtocolReplayWindow)CLICommonFunc.createObjectWithName(ProtocolReplayWindow.class, replayParm)
			);
			
			/** element: <ptk-rekey-period> */
			Object[][] ptkRekeyParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getPtkRekeyPeriod()}
			};
			wpaAes8021Obj.setPtkRekeyPeriod(
					(ProtocolPtkRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolPtkRekeyPeriod.class, ptkRekeyParm)
			);

			/** element: <reauth-interval> */
			ProtocolReauthInterval reauthObj = new ProtocolReauthInterval();
			childLevel_1.add(reauthObj);
			wpaAes8021Obj.setReauthInterval(reauthObj);

		}
		
		/**
		 * generate level_2
		 * 
		 * <reauth-interval>			ProtocolReauthInterval
		 */
		{
			for(Object childObj : childLevel_1){
				
				/** element: <reauth-interval> */
				if(childObj instanceof ProtocolReauthInterval){
					ProtocolReauthInterval reauthObj = (ProtocolReauthInterval)childObj;
					
					/** attribute: operation */
					reauthObj.setOperation(CLICommonFunc.getAhEnumAct(securityImpl.isEnableReauthInterval()));
					
					/** element: <reauth-interval>.<cr> */
					if(securityImpl.isEnableReauthInterval()){
						Object[][] crParm = {
								{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getReauthIntervalValue()}
						};
						reauthObj.setCr(
								(ReauthIntervalCr)CLICommonFunc.createObjectWithName(ReauthIntervalCr.class, crParm)
						);
					}
				}
			}
			childLevel_1.clear();
		}
		
		return wpaAes8021Obj;
	}
	
	private ProtocolWpaTkip8021X createProtocolWpaTkip8021X()throws Exception{
		
		ProtocolWpaTkip8021X wpa8021xObj = new ProtocolWpaTkip8021X();
		
		List<Object> childLevel_1 = new ArrayList<Object>();
		
		/**
		 * generate level_1
		 */
		{
			/** attribute: operation */
			wpa8021xObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			
			/** element: <cr> */
			wpa8021xObj.setCr("");
			
			/** element: <strict> */
			if(securityImpl.isConfigureProtocolStrict()){
				wpa8021xObj.setStrict(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <non-strict> */
			if(securityImpl.isConfigureProtocolNoStrict()){
				wpa8021xObj.setNonStrict(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <gmk-rekey-period> */
			Object[][] gmkParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGmkRekeyPeriod()}
			};
			wpa8021xObj.setGmkRekeyPeriod(
					(ProtocolGmkRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolGmkRekeyPeriod.class, gmkParm)
			);
			
			/** element: <old-gmk-rekey-period> */
			if(securityImpl.getProtocolGmkRekeyPeriod() != 0){
				Object[][] gmkParmOld = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGmkRekeyPeriod()}
				};
				wpa8021xObj.setOldGmkRekeyPeriod(
						(ProtocolGmkRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolGmkRekeyPeriod.class, gmkParmOld)
				);
			}
			
			/** element: <rekey-period> */
			Object[][] rekeyParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolRekeyPeriod()}
			};
			wpa8021xObj.setRekeyPeriod(
					(ProtocolRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolRekeyPeriod.class, rekeyParm)
			);
			
			/** element: <old-rekey-period> */
			if(securityImpl.getProtocolRekeyPeriod() != 0){
				Object[][] rekeyParmOld = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolRekeyPeriod()}
				};
				wpa8021xObj.setOldRekeyPeriod(
						(ProtocolRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolRekeyPeriod.class, rekeyParmOld)
				);
			}
			
			/** element: <ptk-timeout> */
			Object[][] ptkTimeOutParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolPtkTimeout()}
			};
			wpa8021xObj.setPtkTimeout(
					(ProtocolPtkTimeout)CLICommonFunc.createObjectWithName(ProtocolPtkTimeout.class, ptkTimeOutParm)
			);
			
			/** element: <ptk-retry> */
			Object[][] ptkRetryParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolPtkRetry()}
			};
			wpa8021xObj.setPtkRetry(
					(ProtocolPtkRetry)CLICommonFunc.createObjectWithName(ProtocolPtkRetry.class, ptkRetryParm)
			);
			
			/** element: <gtk-timeout> */
			Object[][] gtkTimeOutParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGtkTimeout()}
			};
			wpa8021xObj.setGtkTimeout(
					(ProtocolGtkTimeout)CLICommonFunc.createObjectWithName(ProtocolGtkTimeout.class, gtkTimeOutParm)
			);
			
			/** element: <gtk-retry> */
			Object[][] gtkRetryParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGtkRetry()}
			};
			wpa8021xObj.setGtkRetry(
					(ProtocolGtkRetry)CLICommonFunc.createObjectWithName(ProtocolGtkRetry.class, gtkRetryParm)
			);
			
			/** element: <replay-window> */
			Object[][] replayParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolReplayWindow()},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			wpa8021xObj.setReplayWindow(
					(ProtocolReplayWindow)CLICommonFunc.createObjectWithName(ProtocolReplayWindow.class, replayParm)
			);
			
			/** element: <local-tkip-counter-measure> */
			wpa8021xObj.setLocalTkipCounterMeasure(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableProtocolLocalTkip()));
			
			/** element: <remote-tkip-counter-measure> */
			wpa8021xObj.setRemoteTkipCounterMeasure(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableProtocolRemoteTkip()));
			
			/** element: <ptk-rekey-period> */
			Object[][] ptkRekeyParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getPtkRekeyPeriod()}
			};
			wpa8021xObj.setPtkRekeyPeriod(
					(ProtocolPtkRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolPtkRekeyPeriod.class, ptkRekeyParm)
			);
			
			/** element: <reauth-interval> */
			ProtocolReauthInterval reauthObj = new ProtocolReauthInterval();
			childLevel_1.add(reauthObj);
			wpa8021xObj.setReauthInterval(reauthObj);
		}
		
		/**
		 * generate level_2
		 * 
		 * <reauth-interval>			ProtocolReauthInterval
		 */
		{
			for(Object childObj : childLevel_1){
				
				/** element: <reauth-interval> */
				if(childObj instanceof ProtocolReauthInterval){
					ProtocolReauthInterval reauthObj = (ProtocolReauthInterval)childObj;
					
					/** attribute: operation */
					reauthObj.setOperation(CLICommonFunc.getAhEnumAct(securityImpl.isEnableReauthInterval()));
					
					/** element: <reauth-interval>.<cr> */
					if(securityImpl.isEnableReauthInterval()){
						Object[][] crParm = {
								{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getReauthIntervalValue()}
						};
						reauthObj.setCr(
								(ReauthIntervalCr)CLICommonFunc.createObjectWithName(ReauthIntervalCr.class, crParm)
						);
					}
				}
			}
			childLevel_1.clear();
		}
		
		return wpa8021xObj;
	}
	
	private ProtocolWpaTkip8021XRoaming createProtocolWpaTkip8021XRoaming() throws Exception{
		ProtocolWpaTkip8021XRoaming wpaRoamingObj = new ProtocolWpaTkip8021XRoaming();
		
		List<Object> childLevel_1 = new ArrayList<Object>();
		
		/**
		 * generate level_1
		 */
		{
			/** attribute: operation */
			wpaRoamingObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			
			/** element: <cr> */
			wpaRoamingObj.setCr("");
			
			/** element: <strict> */
			if(securityImpl.isConfigureProtocolStrict()){
				wpaRoamingObj.setStrict(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <non-strict> */
			if(securityImpl.isConfigureProtocolNoStrict()){
				wpaRoamingObj.setNonStrict(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <gmk-rekey-period> */
			Object[][] gmkParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGmkRekeyPeriod()}
			};
			wpaRoamingObj.setGmkRekeyPeriod(
					(ProtocolGmkRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolGmkRekeyPeriod.class, gmkParm)
			);
			
			/** element: <old-gmk-rekey-period> */
			if(securityImpl.getProtocolGmkRekeyPeriod() != 0){
				Object[][] gmkParmOld = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGmkRekeyPeriod()}
				};
				wpaRoamingObj.setOldGmkRekeyPeriod(
						(ProtocolGmkRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolGmkRekeyPeriod.class, gmkParmOld)
				);
			}
			
			/** element: <rekey-period> */
			Object[][] rekeyParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolRekeyPeriod()}
			};
			wpaRoamingObj.setRekeyPeriod(
					(ProtocolRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolRekeyPeriod.class, rekeyParm)
			);
			
			/** element: <old-rekey-period> */
			if(securityImpl.getProtocolRekeyPeriod() != 0){
				Object[][] rekeyParmOld = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolRekeyPeriod()}
				};
				wpaRoamingObj.setOldRekeyPeriod(
						(ProtocolRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolRekeyPeriod.class, rekeyParmOld)
				);
			}
			
			/** element: <ptk-timeout> */
			Object[][] ptkTimeOutParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolPtkTimeout()}
			};
			wpaRoamingObj.setPtkTimeout(
					(ProtocolPtkTimeout)CLICommonFunc.createObjectWithName(ProtocolPtkTimeout.class, ptkTimeOutParm)
			);
			
			/** element: <ptk-retry> */
			Object[][] ptkRetryParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolPtkRetry()}
			};
			wpaRoamingObj.setPtkRetry(
					(ProtocolPtkRetry)CLICommonFunc.createObjectWithName(ProtocolPtkRetry.class, ptkRetryParm)
			);
			
			/** element: <gtk-timeout> */
			Object[][] gtkTimeOutParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGtkTimeout()}
			};
			wpaRoamingObj.setGtkTimeout(
					(ProtocolGtkTimeout)CLICommonFunc.createObjectWithName(ProtocolGtkTimeout.class, gtkTimeOutParm)
			);
			
			/** element: <gtk-retry> */
			Object[][] gtkRetryParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGtkRetry()}
			};
			wpaRoamingObj.setGtkRetry(
					(ProtocolGtkRetry)CLICommonFunc.createObjectWithName(ProtocolGtkRetry.class, gtkRetryParm)
			);
			
			/** element: <roaming> */
			ProtocolRoaming roamingObj = new ProtocolRoaming();
			childLevel_1.add(roamingObj);
			wpaRoamingObj.setRoaming(roamingObj);
			
			/** element: <replay-window> */
			Object[][] replayParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolReplayWindow()},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			wpaRoamingObj.setReplayWindow(
					(ProtocolReplayWindow)CLICommonFunc.createObjectWithName(ProtocolReplayWindow.class, replayParm)
			);
			
			/** element: <local-tkip-counter-measure> */
			wpaRoamingObj.setLocalTkipCounterMeasure(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableProtocolLocalTkip()));
			
			/** element: <remote-tkip-counter-measure> */
			wpaRoamingObj.setRemoteTkipCounterMeasure(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableProtocolRemoteTkip()));
			
			/** element: <ptk-rekey-period> */
			Object[][] ptkRekeyParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getPtkRekeyPeriod()}
			};
			wpaRoamingObj.setPtkRekeyPeriod(
					(ProtocolPtkRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolPtkRekeyPeriod.class, ptkRekeyParm)
			);
			
			/** element: <reauth-interval> */
			ProtocolReauthInterval reauthObj = new ProtocolReauthInterval();
			childLevel_1.add(reauthObj);
			wpaRoamingObj.setReauthInterval(reauthObj);
		}
		
		/**
		 * generate level_2
		 * 
		 * <roaming>				ProtocolRoaming
		 * <reauth-interval>		ProtocolReauthInterval
		 */
		{
			for(Object childObj : childLevel_1){
				
				/** element: <roaming> */
				if(childObj instanceof ProtocolRoaming){
					ProtocolRoaming roamingObj = (ProtocolRoaming)childObj;
					
					/** element: <roaming>.<proactive-pmkid-response> */
					roamingObj.setProactivePmkidResponse(
							CLICommonFunc.getAhOnlyAct(securityImpl.isRoamingProactivePmkidResponse())
					);
				}
				
				/** element: <reauth-interval> */
				if(childObj instanceof ProtocolReauthInterval){
					ProtocolReauthInterval reauthObj = (ProtocolReauthInterval)childObj;
					
					/** attribute: operation */
					reauthObj.setOperation(CLICommonFunc.getAhEnumAct(securityImpl.isEnableReauthInterval()));
					
					/** element: <reauth-interval>.<cr> */
					if(securityImpl.isEnableReauthInterval()){
						Object[][] crParm = {
								{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getReauthIntervalValue()}
						};
						reauthObj.setCr(
								(ReauthIntervalCr)CLICommonFunc.createObjectWithName(ReauthIntervalCr.class, crParm)
						);
					}
				}
			}
			childLevel_1.clear();
		}
		
		return wpaRoamingObj;
	}
	
	private ProtocolWpaAesPsk createProtocolWpaAesPsk() throws Exception {
		ProtocolWpaAesPsk wpaAesPskObj = new ProtocolWpaAesPsk();
		
		/***
		 * generate child level 1
		 */
		{
			
			/** attribute: operation */
			wpaAesPskObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			
			/** element: <hex-key> */
			if(securityImpl.isConfigureProtocolHex()){
				wpaAesPskObj.setHexKey(createWpaPskKey());
			}
			
			/** element: <ascii-key> */
			if(securityImpl.isConfigureProtocolAscii()){
				wpaAesPskObj.setAsciiKey(createWpaPskKey());
			}
			
			/** element: <replay-window> */
			Object[][] replayParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolReplayWindow()},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			wpaAesPskObj.setReplayWindow(
					(ProtocolReplayWindow)CLICommonFunc.createObjectWithName(ProtocolReplayWindow.class, replayParm)
			);
		}
		
		return wpaAesPskObj;
	}
	
	private ProtocolWpa2AesPsk createProtocolWpa2AesPsk() throws Exception {
		ProtocolWpa2AesPsk wpa2AesPskObj = new ProtocolWpa2AesPsk();
		
		/***
		 * generate child level 1
		 */
		{
			
			/** attribute: operation */
			wpa2AesPskObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			
			/** element: <hex-key> */
			if(securityImpl.isConfigureProtocolHex()){
				wpa2AesPskObj.setHexKey(createWpaPskKey());
			}
			
			/** element: <ascii-key> */
			if(securityImpl.isConfigureProtocolAscii()){
				wpa2AesPskObj.setAsciiKey(createWpaPskKey());
			}
			
			/** element: <replay-window> */
			Object[][] replayParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolReplayWindow()},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			wpa2AesPskObj.setReplayWindow(
					(ProtocolReplayWindow)CLICommonFunc.createObjectWithName(ProtocolReplayWindow.class, replayParm)
			);
			
			/** element: <pmf> */
			if(securityImpl.isEnable80211w()){
				ProtocolWpaAesMfp mfp = new ProtocolWpaAesMfp();
				/** attribute: operation */
				mfp.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				ProtocolMfpMethod mfpMethod = createProtocolMfpMethod(securityImpl.isEnableBip());
				if(securityImpl.isConfigmfpMandatory()){
					/** element: <pmf>.<mandatory> */
					mfp.setMandatory(mfpMethod);
				}
				
				if(securityImpl.isConfigmfpOptional()){
					/** element: <pmf>.<optional> */
					mfp.setOptional(mfpMethod);
				}
				wpa2AesPskObj.setMfp(mfp);
			}
		}
		
		return wpa2AesPskObj;
	}
	
	private ProtocolWpaTkipPsk createProtocolWpaTkipPsk() throws Exception {
		
		ProtocolWpaTkipPsk suitePsk = new ProtocolWpaTkipPsk();
		
		/**
		 * generate level_1
		 */
		{
			
			/** attribute: operation */
			suitePsk.setOperation(
					CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault())
			);
			
			/** element: <ascii-key> */
			if(securityImpl.isConfigureProtocolAscii()){
				suitePsk.setAsciiKey(this.createWpaPskKey());
			}
			
			/** element: <hex-key> */
			if(securityImpl.isConfigureProtocolHex()){
				suitePsk.setHexKey(this.createWpaPskKey());
			}
			
			/** element: <replay-window> */
			Object[][] replayParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolReplayWindow()},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			suitePsk.setReplayWindow(
					(ProtocolReplayWindow)CLICommonFunc.createObjectWithName(ProtocolReplayWindow.class, replayParm)
			);
			
			/** element: <local-tkip-counter-measure> */
			suitePsk.setLocalTkipCounterMeasure(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableProtocolLocalTkip()));
			
			/** element: <remote-tkip-counter-measure> */
			suitePsk.setRemoteTkipCounterMeasure(CLICommonFunc.getAhOnlyAct(securityImpl.isEnableProtocolRemoteTkip()));
			
		}
		
		return suitePsk;
	}
	
	private WpaPskKey createWpaPskKey() throws Exception {
		WpaPskKey wpaPskKeyObj = new WpaPskKey();
		
		/**
		 * generate level_1
		 */
		{
			/** attribute: value */
			wpaPskKeyObj.setValue(securityImpl.getProtocolKeyValue());
			
			/** attribute: encrypted */
			wpaPskKeyObj.setEncrypted(wpaPskKeyObj.getEncrypted());
			
			/** element: <cr> */
			wpaPskKeyObj.setCr("");
			
			/** element: <strict> */
			if(securityImpl.isConfigureProtocolStrict()){
				wpaPskKeyObj.setStrict(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <non-strict> */
			if(securityImpl.isConfigureProtocolNoStrict()){
				wpaPskKeyObj.setNonStrict(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <gmk-rekey-period> */
			Object[][] gmkParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGmkRekeyPeriod()}
			};
			wpaPskKeyObj.setGmkRekeyPeriod(
					(ProtocolGmkRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolGmkRekeyPeriod.class, gmkParm)
			);
			
			/** element: <old-gmk-rekey-period> */
			if(securityImpl.getProtocolGmkRekeyPeriod() != 0){
				Object[][] gmkParmOld = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGmkRekeyPeriod()}
				};
				wpaPskKeyObj.setOldGmkRekeyPeriod(
						(ProtocolGmkRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolGmkRekeyPeriod.class, gmkParmOld)
				);
			}
			
			/** element: <rekey-period> */
			Object[][] rekeyParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolRekeyPeriod()}
			};
			wpaPskKeyObj.setRekeyPeriod(
					(ProtocolRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolRekeyPeriod.class, rekeyParm)
			);
			
			/** element: <old-rekey-period> */
			if(securityImpl.getProtocolRekeyPeriod() != 0){
				Object[][] rekeyParmOld = {
						{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolRekeyPeriod()}
				};
				wpaPskKeyObj.setOldRekeyPeriod(
						(ProtocolRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolRekeyPeriod.class, rekeyParmOld)
				);
			}
			
			/** element: <ptk-timeout> */
			Object[][] ptkTimeOutParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolPtkTimeout()}
			};
			wpaPskKeyObj.setPtkTimeout(
					(ProtocolPtkTimeout)CLICommonFunc.createObjectWithName(ProtocolPtkTimeout.class, ptkTimeOutParm)
			);
			
			/** element: <ptk-retry> */
			Object[][] ptkRetryParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolPtkRetry()}
			};
			wpaPskKeyObj.setPtkRetry(
					(ProtocolPtkRetry)CLICommonFunc.createObjectWithName(ProtocolPtkRetry.class, ptkRetryParm)
			);
			
			/** element: <gtk-timeout> */
			Object[][] gtkTimeOutParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGtkTimeout()}
			};
			wpaPskKeyObj.setGtkTimeout(
					(ProtocolGtkTimeout)CLICommonFunc.createObjectWithName(ProtocolGtkTimeout.class, gtkTimeOutParm)
			);
			
			/** element: <gtk-retry> */
			Object[][] gtkRetryParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getProtocolGtkRetry()}
			};
			wpaPskKeyObj.setGtkRetry(
					(ProtocolGtkRetry)CLICommonFunc.createObjectWithName(ProtocolGtkRetry.class, gtkRetryParm)
			);
			
			/** element: <ptk-rekey-period> */
			Object[][] ptkRekeyParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getPtkRekeyPeriod()}
			};
			wpaPskKeyObj.setPtkRekeyPeriod(
					(ProtocolPtkRekeyPeriod)CLICommonFunc.createObjectWithName(ProtocolPtkRekeyPeriod.class, ptkRekeyParm)
			);
		}
		
		return wpaPskKeyObj;
	}
	
	private ProtocolWepOpen createProtocolWepOpen(int index) throws IOException{
		
		ProtocolWepOpen suiteOpenObj = new ProtocolWepOpen();
		
		List<Object> wepOpenChildLevel_1 = new ArrayList<Object>();
		
		/** <security-object>.<security>.<protocol-suite>.<wep-open> level 1*/
		{
			
			/** attribute: name */
			suiteOpenObj.setName(index);
			
			/** attribute: operation */
			suiteOpenObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
			
			/** element: <security-object>.<security>.<protocol-suite>.<wep-open>.<ascii-key> */
			oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite/wep-open",
					"ascii-key", GenerateXMLDebug.CONFIG_ELEMENT, 
					securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
			if(securityImpl.isConfigureProtocolAscii()){
				ProtocolWepOpen.AsciiKey asciiKeyObj = new ProtocolWepOpen.AsciiKey();
				wepOpenChildLevel_1.add(asciiKeyObj);
				suiteOpenObj.setAsciiKey(asciiKeyObj);
			}
			
			/** element: <security-object>.<security>.<protocol-suite>.<wep-open>.<hex-key> */
			oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite/wep-open",
					"hex-key", GenerateXMLDebug.CONFIG_ELEMENT,
					securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
			if(securityImpl.isConfigureProtocolHex()){
				ProtocolWepOpen.HexKey hexKeyObj = new ProtocolWepOpen.HexKey();
				wepOpenChildLevel_1.add(hexKeyObj);
				suiteOpenObj.setHexKey(hexKeyObj);
			}
		}
		
		/** generate ProtocolWepOpen level 2 
		 * 
		 * <ascii-key>				ProtocolWepOpen.AsciiKey
		 * <hex-key>				ProtocolWepOpen.HexKey
		 */
		{
			for(Object childObj : wepOpenChildLevel_1){
				
				/** element: <security-object>.<security>.<protocol-suite>.<wep-open>.<ascii-key> */
				if(childObj instanceof ProtocolWepOpen.AsciiKey){
					ProtocolWepOpen.AsciiKey asciiKeyObj = (ProtocolWepOpen.AsciiKey)childObj;
					
					/** attribute: value */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite/wep-open",
							"ascii-key", GenerateXMLDebug.SET_VALUE,
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					asciiKeyObj.setValue(securityImpl.getProtocolWepValue(index));
					
					/** attribute: encrypted */
					asciiKeyObj.setEncrypted(asciiKeyObj.getEncrypted());
					
					/** element: <security-object>.<security>.<protocol-suite>.<wep-open>.<ascii-key>.<default> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite/wep-open/ascii-key",
							"default", GenerateXMLDebug.CONFIG_ELEMENT,
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					if(securityImpl.isConfigureWepDefault(index)){
						asciiKeyObj.setDefault("");
					}
				}
				
				/** element: <security-object>.<security>.<protocol-suite>.<wep-open>.<hex-key> */
				if(childObj instanceof ProtocolWepOpen.HexKey){
					ProtocolWepOpen.HexKey hexKeyObj = (ProtocolWepOpen.HexKey)childObj;
					
					/** attribute: value */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite/wep-open",
							"hex-key", GenerateXMLDebug.SET_VALUE, 
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					hexKeyObj.setValue(securityImpl.getProtocolWepValue(index));
					
					/** attribute: encrypted */
					hexKeyObj.setEncrypted(hexKeyObj.getEncrypted());
					
					/** element: <security-object>.<security>.<protocol-suite>.<wep-open>.<hex-key>.<default> */
					oDebug.debug("/configuration/security-object[@name='"+securityObj.getName()+ "']/security/protocol-suite/wep-open/hex-key",
							"default", GenerateXMLDebug.CONFIG_ELEMENT, 
							securityImpl.getSsidGuiKey(), securityImpl.getSsidName());
					if(securityImpl.isConfigureWepDefault(index)){
						hexKeyObj.setDefault("");
					}
				}
			}
			wepOpenChildLevel_1.clear();
		}
		
		return suiteOpenObj;
	}
	
	private CwpRedirectExternalPage createCwpRedirectExternalPage(String extUrl, int delay) throws Exception{
		CwpRedirectExternalPage externalPageObj = new CwpRedirectExternalPage();
		
		/** attribute: value */
		externalPageObj.setValue(extUrl);
		
		/** element: <delay> */
		Object[][] delayParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, delay}
		};
		externalPageObj.setDelay(
				(CwpRedirectDelay)CLICommonFunc.createObjectWithName(CwpRedirectDelay.class, delayParm)
		);
		
		return externalPageObj;
	}
	
	private PpskAuthMethod createAhPpskAuthMethod(AuthMethodType authType){
		PpskAuthMethod ppskAuthMethod = new PpskAuthMethod();
		
		/** element: cr */
		ppskAuthMethod.setCr(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		
		/** element: <pap> */
		if(authType == AuthMethodType.pap){
			ppskAuthMethod.setPap(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <chap> */
		if(authType == AuthMethodType.chap){
			ppskAuthMethod.setChap(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <ms-chap-v2> */
		if(authType == AuthMethodType.msChapV2){
			ppskAuthMethod.setMsChapV2(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
		}
		
		return ppskAuthMethod;
	}
	
	private AhAuthMethod createAhAuthMethod(AuthMethodType authType){
		AhAuthMethod oAuthMethod = new AhAuthMethod();
		
		/** attribute: operation */
		oAuthMethod.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <pap> */
		if(authType == AuthMethodType.pap){
			oAuthMethod.setPap(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <chap> */
		if(authType == AuthMethodType.chap){
			oAuthMethod.setChap(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <ms-chap-v2> */
		if(authType == AuthMethodType.msChapV2){
			oAuthMethod.setMsChapV2(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
		}
		
		return oAuthMethod;
	}
	
	private void setRadiusServerTypeAcct(RadiusAccountingServer primaryObj, AAAProfileInt.RADIUS_PRIORITY_TYPE type) throws Exception{
		
		/** attribute: value */
		primaryObj.setValue(securityImpl.getAcctAAARadiusServerIpOrHost(type));
		
		/** attribute operation */
		primaryObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <shared-secret> */
		if(securityImpl.isConfigAcctAAARadiusServerSharedSecret(type)){
			primaryObj.setSharedSecret(CLICommonFunc.createAhEncryptedStringAct(
					securityImpl.getAcctAAARadiusServerSharedSecret(type), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <acct-port> */
		if(securityImpl.isConfigAcctAAARadiusAcctPort(type)){
			Object[][] acctProtParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getAcctAAARadiusServerAcctPort(type)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			primaryObj.setAcctPort(
					(AaaAcctPort)CLICommonFunc.createObjectWithName(AaaAcctPort.class, acctProtParm)
			);
		}
		
		/** element: <via-vpn-tunnel> */
		if(securityImpl.isEnableVpnTunnel(primaryObj.getValue())){
			primaryObj.setViaVpnTunnel(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
	}
	
	private void setRadiusServerType(RadiusServerType primaryObj, AAAProfileInt.RADIUS_PRIORITY_TYPE type) throws Exception {
		
		/** attribute: value */
		primaryObj.setValue(securityImpl.getAAARadiusServerIpOrHost(type));
		
		/** attribute operation */
		primaryObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <old-shared-secret> */
		if(securityImpl.isConfigAAARadiusServerSharedSecretOld(type)){
			RadiusServerType.OldSharedSecret oldSharedSecretObj = new RadiusServerType.OldSharedSecret();
			setSharedSecret(oldSharedSecretObj, type);
			primaryObj.setOldSharedSecret(oldSharedSecretObj);
		}
		
		/** element: <shared-secret> */
		if(securityImpl.isConfigAAARadiusServerSharedSecret(type)){
			primaryObj.setSharedSecret(CLICommonFunc.createAhEncryptedStringAct(
					securityImpl.getAAARadiusServerSharedSecret(type), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <auth-port> */
		Object[][] authPortParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getAAARadiusAuthPort(type)},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		primaryObj.setAuthPort(
				(AaaAuthPort)CLICommonFunc.createObjectWithName(AaaAuthPort.class, authPortParm)
		);
		
		/** element: <acct-port> */
		if(securityImpl.isConfigAAARadiusAcctPort(type)){
			Object[][] acctProtParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getAAARadiusServerAcctPort(type)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			primaryObj.setAcctPort(
					(AaaAcctPort)CLICommonFunc.createObjectWithName(AaaAcctPort.class, acctProtParm)
			);
		}
		
		/** element: <via-vpn-tunnel> */
		if(securityImpl.isEnableVpnTunnel(primaryObj.getValue())){
			primaryObj.setViaVpnTunnel(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
	}

	private void setSharedSecret(RadiusServerType.OldSharedSecret oldSharedSecretObj, AAAProfileInt.RADIUS_PRIORITY_TYPE type) throws Exception {
		
		/** attribute: value */
		oldSharedSecretObj.setValue(AhConfigUtil.hiveApCommonEncrypt(securityImpl.getAAARadiusServerSharedSecret(type)));
		
		/** element: <auth-port> */
		Object[][] authPortParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getAAARadiusAuthPort(type)}
		};
		oldSharedSecretObj.setAuthPort(
				(RadiusServerType.OldSharedSecret.AuthPort)CLICommonFunc.createObjectWithName(
						RadiusServerType.OldSharedSecret.AuthPort.class, authPortParm)
		);
		
		/** element: <acct-port> */
		if(securityImpl.isConfigAAARadiusAcctPort(type)){
			Object[][] acctProtParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, securityImpl.getAAARadiusServerAcctPort(type)}
			};
			oldSharedSecretObj.setAcctPort(
					(RadiusServerType.OldSharedSecret.AcctPort)CLICommonFunc.createObjectWithName(
							RadiusServerType.OldSharedSecret.AcctPort.class, acctProtParm)
			);
		}
	}
	
	private WalledGardenServer createWalledGardenServer(short ipOrHost, int i) throws Exception{
		WalledGardenServer walledGardenServerObj = new WalledGardenServer();
		
		/** attribute: operation */
		walledGardenServerObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		walledGardenServerObj.setName(securityImpl.getWallGardenAddress(ipOrHost, i));
		
		/** element: <service> */
		WalledGardenService serviceObj = new WalledGardenService();
		wallGardenList_1.add(serviceObj);
		walledGardenServerObj.setService(serviceObj);
		
		generateWalledGardenServer_1(ipOrHost, i);
		
		return walledGardenServerObj;
	}
	
	private void generateWalledGardenServer_1(short ipOrHost, int i) throws Exception{
		/**
		 * <service>			WalledGardenService
		 */
		for(Object childObj : wallGardenList_1){
			
			/** element: <service> */
			if(childObj instanceof WalledGardenService){
				WalledGardenService serviceObj = (WalledGardenService)childObj;
				
				/** element: <service>.<all> */
				if(securityImpl.getWallGardenAll(ipOrHost, i)){
					serviceObj.setAll(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
				} else {
					/** element: <service>.<web> */
					if(securityImpl.getWallGardenWeb(ipOrHost, i)){
						serviceObj.setWeb(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
					}
					
					/** element: <service>.<protocol> */
					for(int j=0; j<securityImpl.getWallGardenProtocolSize(ipOrHost, i); j++){
						serviceObj.getProtocol().add(this.createWalledGardenServiceProtocol(ipOrHost, i, j));
					}
				}
			}
		}
		wallGardenList_1.clear();
	}
	
	private WalledGardenServiceProtocol createWalledGardenServiceProtocol(short ipOrHost, int i, int j) throws Exception{
		/**
		 * <service>.<protocol>			WalledGardenServiceProtocol
		 */
		WalledGardenServiceProtocol protocolObj = new WalledGardenServiceProtocol();
		
		/** attribute: name */
		protocolObj.setName(securityImpl.getWallGardenProtocolValue(ipOrHost, i, j));
		
		/** element: <service>.<protocol>.<port> */
		for(int k=0; k<securityImpl.getWallGardenPortSize(ipOrHost, i, j); k++){
			Object[][] portObj = {
					{CLICommonFunc.ATTRIBUTE_NAME, securityImpl.getWallGardenPortValue(ipOrHost, i, j, k)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			
			protocolObj.getPort().add(
					(WalledGardenServicePort)CLICommonFunc.createObjectWithName(WalledGardenServicePort.class, portObj)
			);
		}
		
		return protocolObj;
	}
	
	private HttpAuthUserPassword createHttpAuthUserPassword(String userName,String password) throws IOException{
		HttpAuthUserPassword ahUserObj = new HttpAuthUserPassword();
		ahUserObj.setValue(userName);
		ahUserObj.setPassword(CLICommonFunc.createAhEncryptedString(password));
		return ahUserObj;
	}
	
	/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<jss>.<os-object><ios|mac-os|symbian|blackberry> */
	/** element: <security-object>.<security>.<additional-auth-method>.<mobile-device-manager>.<airwatch>.<os-object><ios|mac-os|symbian|blackberry> */
	private MdmOsObject createMdmOsObject(short i){
		MdmOsObject osObject = new MdmOsObject();
		/** attribute name **/
		osObject.setName(securityImpl.getMdmOsObject(i));
		/** attribute operation **/
		osObject.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		switch (i) {
		case SecurityObjectProfileInt.MDM_OSOBJECT_IOS: 		
//			osObject.setIos("");
			break;
		case SecurityObjectProfileInt.MDM_OSOBJECT_MACOS:		
			osObject.setMacOs("");
			break;
		}

		return osObject;
	}
	
	private ProtocolMfpMethod createProtocolMfpMethod(boolean bip){
		ProtocolMfpMethod mfpMethod = new ProtocolMfpMethod();
		mfpMethod.setBip(CLICommonFunc.getAhOnlyAct(bip));
		mfpMethod.setCr("");
		return mfpMethod;
	}
}
