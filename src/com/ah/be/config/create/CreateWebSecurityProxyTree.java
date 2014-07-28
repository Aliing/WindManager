package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.WebSecurityProxyInt;
import com.ah.be.config.create.source.WebSecurityProxyInt.ProxyType;
import com.ah.be.config.create.source.impl.UserProfileImpl;
import com.ah.bo.useraccess.UserProfile;
import com.ah.xml.be.config.AhAllowBlockValue;
import com.ah.xml.be.config.OpendnsV1DnsServer;
import com.ah.xml.be.config.OpendnsV1UserProfile;
import com.ah.xml.be.config.WebSecurityHttpProxyPort;
import com.ah.xml.be.config.WebSecurityHttpsProxyPort;
import com.ah.xml.be.config.WebSecurityProxyAction;
import com.ah.xml.be.config.WebSecurityProxyBarracuda;
import com.ah.xml.be.config.WebSecurityProxyObj;
import com.ah.xml.be.config.WebSecurityProxyOpendns;
import com.ah.xml.be.config.WebSecurityProxySubnet;
import com.ah.xml.be.config.WebSecurityProxyWebsense;

public class CreateWebSecurityProxyTree {
	
	private WebSecurityProxyInt webProxyImpl;
	private GenerateXMLDebug oDebug;
	
	private WebSecurityProxyObj webProxyObj;
	
	private List<UserProfileImpl> userProfileImplList;
	
	private List<Object> webProxyChildList_1 = new ArrayList<Object>();

	public CreateWebSecurityProxyTree(WebSecurityProxyInt webProxyImpl, List<UserProfileImpl> userProfileImplList, GenerateXMLDebug oDebug){
		this.webProxyImpl = webProxyImpl;
		this.userProfileImplList = userProfileImplList;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		
		if(webProxyImpl.isConfigWebProxy()){
			webProxyObj = new WebSecurityProxyObj();
			generateWebProxyLevel_1();
		}
	}
	
	public WebSecurityProxyObj getWebSecurityProxyObj(){
		return webProxyObj;
	}
	
	private void generateWebProxyLevel_1() throws Exception{

		/**element: <web-security-proxy>.<websense-v1> */
		if(webProxyImpl.isConfigWebProxy(ProxyType.websense)){
			WebSecurityProxyWebsense webObj = new WebSecurityProxyWebsense();
			webProxyChildList_1.add(webObj);
			webProxyObj.setWebsenseV1(webObj);
		}
		
		/**element: <web-security-proxy>.<barracuda-v1> */
		if(webProxyImpl.isConfigWebProxy(ProxyType.barracuda)){
			WebSecurityProxyBarracuda barrObj = new WebSecurityProxyBarracuda();
			webProxyChildList_1.add(barrObj);
			webProxyObj.setBarracudaV1(barrObj);
		}
		
		/**element: <web-security-proxy>.<opendns-v1> */
		WebSecurityProxyOpendns openDNSObj = new WebSecurityProxyOpendns();
		webProxyChildList_1.add(openDNSObj);
		webProxyObj.setOpendnsV1(openDNSObj);
		
		generateWebProxyLevel_2();
	}
	
	private void generateWebProxyLevel_2() throws Exception{
		/**
		 * <web-security-proxy>.<websense-v1>				WebSecurityProxyWebsense
		 * <web-security-proxy>.<barracuda-v1>				WebSecurityProxyBarracuda
		 */
		for(Object childObj : webProxyChildList_1){
			
			/** element: <web-security-proxy>.<websense-v1> */
			if(childObj instanceof WebSecurityProxyWebsense){
				WebSecurityProxyWebsense webObj = (WebSecurityProxyWebsense)childObj;
				
				/** element: <web-security-proxy>.<websense-v1>.<http-proxy-host> */
				if(webProxyImpl.isConfigHttpHost(ProxyType.websense)){
					webObj.setHttpProxyHost(CLICommonFunc.createAhStringActObj(
							webProxyImpl.getHttpHost(ProxyType.websense), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <web-security-proxy>.<websense-v1>.<http-proxy-port> */
				if(webProxyImpl.isConfigHttpPort(ProxyType.websense)){
					Object[][] portParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, webProxyImpl.getHttpPort(ProxyType.websense)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					webObj.setHttpProxyPort((WebSecurityHttpProxyPort)CLICommonFunc.createObjectWithName(
							WebSecurityHttpProxyPort.class, portParm));
				}
				
				/** element: <web-security-proxy>.<websense-v1>.<https-proxy-host> */
				if(webProxyImpl.isConfigHttpsHost(ProxyType.websense)){
					webObj.setHttpsProxyHost(CLICommonFunc.createAhStringActObj(
							webProxyImpl.getHttpsHost(ProxyType.websense), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <web-security-proxy>.<websense-v1>.<https-proxy-port> */
				if(webProxyImpl.isConfigHttpsPort(ProxyType.websense)){
					Object[][] httpsPortParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, webProxyImpl.getHttpsPort(ProxyType.websense)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					webObj.setHttpsProxyPort((WebSecurityHttpsProxyPort)CLICommonFunc.createObjectWithName(
							WebSecurityHttpsProxyPort.class, httpsPortParm));
				}
				
				/** element: <web-security-proxy>.<websense-v1>.<account-id> */
				if(webProxyImpl.isConfigAccountId(ProxyType.websense)){
					webObj.setAccountId(CLICommonFunc.createAhStringActObj(
							webProxyImpl.getAccountId(ProxyType.websense), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <web-security-proxy>.<websense-v1>.<default-username> */
				if(webProxyImpl.isConfigDefaultUsername(ProxyType.websense)){
					webObj.setDefaultUsername(CLICommonFunc.createAhStringActObj(
							webProxyImpl.getDefaultUsername(ProxyType.websense), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <web-security-proxy>.<websense-v1>.<account-key> */
				if(webProxyImpl.isConfigAccountKey(ProxyType.websense)){
					webObj.setAccountKey(CLICommonFunc.createAhEncryptedStringAct(
							webProxyImpl.getAccountKey(ProxyType.websense), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <web-security-proxy>.<websense-v1>.<subnet> */
				for(int index=0; index < webProxyImpl.getSubnetSize(ProxyType.websense); index++){
					webObj.getSubnet().add(createWebSecurityProxySubnet(webProxyImpl.getSubnetValue(ProxyType.websense, index), 
							webProxyImpl.getSubnetAction(ProxyType.websense, index)));
				}
				
				/** element: <web-security-proxy>.<websense-v1>.<default-domain> */
				if(webProxyImpl.isConfigDefaultDomain(ProxyType.websense)){
					webObj.setDefaultDomain(CLICommonFunc.createAhStringActObj(
							webProxyImpl.getDefaultDomain(ProxyType.websense), CLICommonFunc.getYesDefault())
					);
				}
				
				/** element: <web-security-proxy>.<websense-v1>.<whitelist> */
				for(int index=0; index < webProxyImpl.getWhitelistSize(ProxyType.websense); index++){
					webObj.getWhitelist().add(CLICommonFunc.createAhNameActValue(
							webProxyImpl.getWhitelistName(ProxyType.websense, index), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <web-security-proxy>.<websense-v1>.<enable> */
				webObj.setEnable(CLICommonFunc.getAhOnlyAct(webProxyImpl.isEnableWebProxy(ProxyType.websense)));
			}
			
			/** element: <web-security-proxy>.<barracuda-v1> */
			if(childObj instanceof WebSecurityProxyBarracuda){
				WebSecurityProxyBarracuda barObj = (WebSecurityProxyBarracuda)childObj;
				
				/** element: <web-security-proxy>.<barracuda-v1>.<http-proxy-host> */
				if(webProxyImpl.isConfigHttpHost(ProxyType.barracuda)){
					barObj.setHttpProxyHost(CLICommonFunc.createAhStringActObj(
							webProxyImpl.getHttpHost(ProxyType.barracuda), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <web-security-proxy>.<barracuda-v1>.<http-proxy-port> */
				if(webProxyImpl.isConfigHttpPort(ProxyType.barracuda)){
					Object[][] portParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, webProxyImpl.getHttpPort(ProxyType.barracuda)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					barObj.setHttpProxyPort((WebSecurityHttpProxyPort)CLICommonFunc.createObjectWithName(
							WebSecurityHttpProxyPort.class, portParm));
				}
				
				/** element: <web-security-proxy>.<barracuda-v1>.<https-proxy-host> */
				if(webProxyImpl.isConfigHttpsHost(ProxyType.barracuda)){
					barObj.setHttpsProxyHost(CLICommonFunc.createAhStringActObj(
							webProxyImpl.getHttpsHost(ProxyType.barracuda), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <web-security-proxy>.<barracuda-v1>.<https-proxy-port> */
				if(webProxyImpl.isConfigHttpsPort(ProxyType.barracuda)){
					Object[][] httpsPortParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, webProxyImpl.getHttpsPort(ProxyType.barracuda)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					barObj.setHttpsProxyPort((WebSecurityHttpsProxyPort)CLICommonFunc.createObjectWithName(
							WebSecurityHttpsProxyPort.class, httpsPortParm));
				}
				
				/** element: <web-security-proxy>.<barracuda-v1>.<account-id> */
				if(webProxyImpl.isConfigAccountId(ProxyType.barracuda)){
					barObj.setAccountId(CLICommonFunc.createAhStringActObj(
							webProxyImpl.getAccountId(ProxyType.barracuda), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <web-security-proxy>.<barracuda-v1>.<default-username> */
				if(webProxyImpl.isConfigDefaultUsername(ProxyType.barracuda)){
					barObj.setDefaultUsername(CLICommonFunc.createAhStringActObj(
							webProxyImpl.getDefaultUsername(ProxyType.barracuda), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <web-security-proxy>.<barracuda-v1>.<default-domain> */
				if(webProxyImpl.isConfigDefaultDomain(ProxyType.barracuda)){
					barObj.setDefaultDomain(CLICommonFunc.createAhStringActObj(
							webProxyImpl.getDefaultDomain(ProxyType.barracuda), CLICommonFunc.getYesDefault())
					);
				}
				
				/** element: <web-security-proxy>.<barracuda-v1>.<subnet> */
				for(int index=0; index < webProxyImpl.getSubnetSize(ProxyType.barracuda); index++){
					barObj.getSubnet().add(createWebSecurityProxySubnet(webProxyImpl.getSubnetValue(ProxyType.barracuda, index), 
							webProxyImpl.getSubnetAction(ProxyType.barracuda, index)));
				}
				
				/** element: <web-security-proxy>.<barracuda-v1>.<whitelist> */
				for(int index=0; index < webProxyImpl.getWhitelistSize(ProxyType.barracuda); index++){
					barObj.getWhitelist().add(CLICommonFunc.createAhNameActValue(
							webProxyImpl.getWhitelistName(ProxyType.barracuda, index), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <web-security-proxy>.<barracuda-v1>.<enable> */
				barObj.setEnable(CLICommonFunc.getAhOnlyAct(webProxyImpl.isEnableWebProxy(ProxyType.barracuda)));
			}
			
			/** element: <web-security-proxy>.<opendns-v1> */
			if(childObj instanceof WebSecurityProxyOpendns){
				WebSecurityProxyOpendns openDNSObj = (WebSecurityProxyOpendns)childObj;
					
				/** element: <web-security-proxy>.<opendns-v1> [options] <string> <secure-id> <string> */
				if(webProxyImpl.isEnableOpenDNS()){
					for(UserProfileImpl userProfileImpl : userProfileImplList){
						UserProfile userProfile = userProfileImpl.getUserProfile();
						String openDNSDID = webProxyImpl.getOpenDNSDID(userProfile.getId());
						if(openDNSDID != null){
							OpendnsV1UserProfile openDNSUserProfile = new OpendnsV1UserProfile();
							openDNSUserProfile.setName(userProfile.getUserProfileName());					
							openDNSUserProfile.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
							openDNSUserProfile.setSecureId(CLICommonFunc.createAhName(openDNSDID));
							openDNSObj.getUserProfile().add(openDNSUserProfile);
						}
					}
					/** element: <web-security-proxy>.<opendns-v1>.<dns-server> [options] <ip_addr> */
					openDNSObj.setDnsServer(createOpendnsV1DnsServer());
				}
				
				/** element: <web-security-proxy>.<opendns-v1>.<enable> */
				openDNSObj.setEnable(CLICommonFunc.getAhOnlyAct(webProxyImpl.isEnableOpenDNS()));
			}
		}
		webProxyChildList_1.clear();
	}
	
	private WebSecurityProxySubnet createWebSecurityProxySubnet(String subnet, AhAllowBlockValue type){
		WebSecurityProxySubnet subnetObj = new WebSecurityProxySubnet();
		
		/** attribute: name */
		subnetObj.setName(subnet);
		
		/** attribute: operation */
		subnetObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <action-if-unreachable> */
		WebSecurityProxyAction actionObj = new WebSecurityProxyAction();
		actionObj.setValue(type);
		actionObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		subnetObj.setActionIfUnreachable(actionObj);
		
		return subnetObj;
	}
	
	private OpendnsV1DnsServer createOpendnsV1DnsServer(){
		OpendnsV1DnsServer dnsServer = new OpendnsV1DnsServer();
		dnsServer.setDns1(CLICommonFunc.createAhStringActObj(webProxyImpl.getOpenDNSServer1(), CLICommonFunc.getYesDefault()));
		dnsServer.setDns2(CLICommonFunc.createAhStringActObj(webProxyImpl.getOpenDNSServer2(), CLICommonFunc.getYesDefault()));
		return dnsServer;
	}
}
