package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.AirScreenProfileInt;
import com.ah.xml.be.config.AirscreenAction;
import com.ah.xml.be.config.AirscreenActionType;
import com.ah.xml.be.config.AirscreenBehavior;
import com.ah.xml.be.config.AirscreenBehaviorType;
import com.ah.xml.be.config.AirscreenObj;
import com.ah.xml.be.config.AirscreenSource;
import com.ah.xml.be.config.AirscreenSourceType;
import com.ah.xml.be.config.AirscreenSourceTypeClient;
import com.ah.xml.be.config.LocalBanInterval;
import com.ah.xml.be.config.ReconnectionConnectionCase;
import com.ah.xml.be.config.ReconnectionInterval;
import com.ah.xml.be.config.ReconnectionThreshold;

/**
 * @author zhang
 * @version 2009-6-5 10:45:22
 */

public class CreateAirScreen {
	
	private AirScreenProfileInt airScreenImpl;
	private AirscreenObj airObj;
	private GenerateXMLDebug oDebug;
	
	private List<Object> ariChildLevel_1 = new ArrayList<Object>();
	private List<Object> ariChildLevel_2 = new ArrayList<Object>();
	private List<Object> ariChildLevel_3 = new ArrayList<Object>();

	public CreateAirScreen(AirScreenProfileInt airScreenImpl, GenerateXMLDebug oDebug){
		this.airScreenImpl = airScreenImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		airObj = new AirscreenObj();
		generateAirScreenLevel_1();
	}
	
	public AirscreenObj getAirScreenObj(){
		return this.airObj;
	}
	
	public void generateAirScreenLevel_1() throws Exception{
		/**
		 * <airscreen>.<action>				AirscreenAction
		 * <airscreen>.<source>				AirscreenSource
		 * <airscreen>.<behavior>			AirscreenBehavior
		 * <airscreen>.<rule>				AirscreenObj.Rule
		 */
		
		/** <airscreen>.<action> */
		oDebug.debug("/configuration/airscreen", 
				"action", GenerateXMLDebug.CONFIG_ELEMENT,
				null, null);
		for(int index=0; index < airScreenImpl.getActionSize(); index ++){
			airObj.getAction().add(this.createAirscreenAction(index));
		}
		
		/** <airscreen>.<source> */
		oDebug.debug("/configuration/airscreen", 
				"source", GenerateXMLDebug.CONFIG_ELEMENT,
				null, null);
		for(int index=0; index < airScreenImpl.getSourceSize(); index ++){
			airObj.getSource().add(this.createAirscreenSource(index));
		}
		
		/** <airscreen>.<behavior> */
		oDebug.debug("/configuration/airscreen", 
				"behavior", GenerateXMLDebug.CONFIG_ELEMENT,
				null, null);
		for(int index=0; index < airScreenImpl.getBehaviorSize(); index ++){
			airObj.getBehavior().add(this.createAirscreenBehavior(index));
		}
		
		/** <airscreen>.<rule> */
		oDebug.debug("/configuration/airscreen", 
				"rule", GenerateXMLDebug.CONFIG_ELEMENT,
				null, null);
		for(int index=0; index < airScreenImpl.getRuleSize(); index ++){
			airObj.getRule().add(this.createRule(index));
		}
	}
	
	private AirscreenAction createAirscreenAction(int index) throws Exception{
		
		/** <airscreen>.<action> */
		AirscreenAction actionObj = new AirscreenAction();
		
		/** attribute: name */
		oDebug.debug("/configuration/airscreen", 
				"action", GenerateXMLDebug.SET_NAME,
				null, null);
		actionObj.setName(airScreenImpl.getActionName(index));
		
		/** attribute: operation */
		actionObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** <airscreen>.<action>.<type> */
		AirscreenActionType typeObj = new AirscreenActionType();
		ariChildLevel_1.add(typeObj);
		actionObj.setType(typeObj);
		
		generateActionLevel_1(index);
		
		return actionObj;
	}
	
	private void generateActionLevel_1(int index) throws Exception{
		/**
		 * <airscreen>.<action>.<type>				AirscreenActionType
		 */
		for(Object childObj : ariChildLevel_1){
			
			if(childObj instanceof AirscreenActionType){
				AirscreenActionType actionObj  = (AirscreenActionType)childObj;
				
				/** element: <airscreen>.<action>.<type>.<de-auth> */
				oDebug.debug("/configuration/airscreen/action[@name='"+airScreenImpl.getActionName(index)+"']/type", 
						"de-auth", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(airScreenImpl.isConfigActionDeAuth(index)){
					actionObj.setDeAuth("");
				}
				
				/** element: <airscreen>.<action>.<type>.<report-to-hm> */
				oDebug.debug("/configuration/airscreen/action[@name='"+airScreenImpl.getActionName(index)+"']/type", 
						"report-to-hm", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(airScreenImpl.isConfigActionReportToHm(index)){
					actionObj.setReportToHm("");
				}
				
				/** element: <airscreen>.<action>.<type>.<local-ban> */
				oDebug.debug("/configuration/airscreen/action[@name='"+airScreenImpl.getActionName(index)+"']/type", 
						"local-ban", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(airScreenImpl.isConfigActionLocalBan(index)){
					AirscreenActionType.LocalBan localBanObj = new AirscreenActionType.LocalBan();
					ariChildLevel_2.add(localBanObj);
					actionObj.setLocalBan(localBanObj);
				}
			}
		}
		ariChildLevel_1.clear();
		generateActionLevel_2(index);
	}
	
	private void generateActionLevel_2(int index) throws Exception{
		/**
		 * <airscreen>.<action>.<type>.<local-ban>				AirscreenActionType.LocalBan
		 */
		for(Object childObj : ariChildLevel_2){
			
			/** elemnet: <airscreen>.<action>.<type>.<local-ban> */
			if(childObj instanceof AirscreenActionType.LocalBan){
				AirscreenActionType.LocalBan localBanObj = (AirscreenActionType.LocalBan)childObj;
				
				/** elemnet: <airscreen>.<action>.<type>.<local-ban>.<cr> */
				localBanObj.setCr("");
				
				/** elemnet: <airscreen>.<action>.<type>.<local-ban>.<interval> */
				oDebug.debug("/configuration/airscreen/action[@name='"+airScreenImpl.getActionName(index)+"']/type/local-ban", 
						"interval", GenerateXMLDebug.SET_VALUE,
						null, null);
				Object[][] intervalParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, airScreenImpl.getLocalBanInterval(index)}
				};
				localBanObj.setInterval(
						(LocalBanInterval)CLICommonFunc.createObjectWithName(LocalBanInterval.class, intervalParm)
				);
			}
		}
		ariChildLevel_2.clear();
	}
	
	private AirscreenSource createAirscreenSource(int index) throws CreateXMLException{
		/** <airscreen>.<source> */
		AirscreenSource sourceObj = new AirscreenSource();
		
		/** attribute: name */
		oDebug.debug("/configuration/airscreen",
				"source", GenerateXMLDebug.SET_NAME,
				null, null);
		sourceObj.setName(airScreenImpl.getSourceName(index));
		
		/** attribute: operation */
		sourceObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <airscreen>.<source>.<type> */
		AirscreenSourceType typeObj = new AirscreenSourceType();
		ariChildLevel_1.add(typeObj);
		sourceObj.setType(typeObj);
		
		generateSourceChildLevel_1(index);
		
		return sourceObj;
	}
	
	private void generateSourceChildLevel_1(int index) throws CreateXMLException{
		/**
		 * <airscreen>.<source>.<type>					AirscreenSourceType
		 */
		for(Object childObj : ariChildLevel_1){
			
			/** element: <airscreen>.<source>.<type> */
			if(childObj instanceof AirscreenSourceType){
				AirscreenSourceType typeObj = (AirscreenSourceType)childObj;
				
				/** element: <airscreen>.<source>.<type>.<client> */
				AirscreenSourceTypeClient clientObj = new AirscreenSourceTypeClient();
				ariChildLevel_2.add(clientObj);
				typeObj.setClient(clientObj);
				
			}
		}
		ariChildLevel_1.clear();
		generateSourceChildLevel_2(index);
	}
	
	private void generateSourceChildLevel_2(int index) throws CreateXMLException{
		/**
		 * <airscreen>.<source>.<type>.<client>					AirscreenSourceTypeClient
		 */
		for(Object childObj : ariChildLevel_2){
			
			/** element: <airscreen>.<source>.<type>.<client> */
			if(childObj instanceof AirscreenSourceTypeClient){
				AirscreenSourceTypeClient clientObj = (AirscreenSourceTypeClient)childObj;
				
				/** element: <airscreen>.<source>.<type>.<client>.<cr> */
				clientObj.setCr("");
				
				/** element: <airscreen>.<source>.<type>.<client>.<oui> */
				oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client",
						"oui", GenerateXMLDebug.SET_VALUE,
						null, null);
				clientObj.setOui(CLICommonFunc.createAhStringActObj(airScreenImpl.getSourceOui(index), CLICommonFunc.getYesDefault()));
				
				/** element: <airscreen>.<source>.<type>.<client>.<rssi> */
				oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client",
						"rssi", GenerateXMLDebug.SET_VALUE,
						null, null);
				clientObj.setRssi(CLICommonFunc.createAhStringActQuoteProhibited(
						airScreenImpl.getSourceRssi(index), CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault()));
				
				/** element: <airscreen>.<source>.<type>.<client>.<auth-mode> */
				AirscreenSourceTypeClient.AuthMode authModeObj = new AirscreenSourceTypeClient.AuthMode();
				ariChildLevel_3.add(authModeObj);
				clientObj.setAuthMode(authModeObj);
			}
		}
		ariChildLevel_2.clear();
		generateSourceChildLevel_3(index);
	}
	
	private void generateSourceChildLevel_3(int index){
		/**
		 * <airscreen>.<source>.<type>.<client>.<auth-mode>				AirscreenSourceTypeClient.AuthMode
		 */
		for(Object childObj : ariChildLevel_3){
			
			/** element: <airscreen>.<source>.<type>.<client>.<auth-mode> */
			if(childObj instanceof AirscreenSourceTypeClient.AuthMode){
				AirscreenSourceTypeClient.AuthMode authModeObj = (AirscreenSourceTypeClient.AuthMode)childObj;
				
				/** attribute: operation */
				authModeObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <airscreen>.<source>.<type>.<client>.<auth-mode>.<open> */
				oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client/auth-mode",
						"open", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(airScreenImpl.isConfigSourceOpen(index)){
					authModeObj.setOpen("");
				}
				
				/** element: <airscreen>.<source>.<type>.<client>.<auth-mode>.<wep> */
				oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client/auth-mode",
						"wep", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(airScreenImpl.isConfigSourceWep(index)){
					authModeObj.setWep("");
				}
				
				/** element: <airscreen>.<source>.<type>.<client>.<auth-mode>.<wep-open> */
				oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client/auth-mode",
						"wep-open", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(airScreenImpl.isConfigSourceWepOpen(index)){
					authModeObj.setWepOpen("");
				}
				
				/** element: <airscreen>.<source>.<type>.<client>.<auth-mode>.<wep-shared> */
				oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client/auth-mode",
						"wep-shared", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(airScreenImpl.isConfigSourceWepShared(index)){
					authModeObj.setWepShared("");
				}
				
				/** element: <airscreen>.<source>.<type>.<client>.<auth-mode>.<dynamic-wep> */
				oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client/auth-mode",
						"dynamic-wep", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(airScreenImpl.isConfigSourceDynamicWep(index)){
					authModeObj.setDynamicWep("");
				}
				
				/** element: <airscreen>.<source>.<type>.<client>.<auth-mode>.<wpa> */
				oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client/auth-mode",
						"wpa", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(airScreenImpl.isConfigSourceWpa(index)){
					
					oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client/auth-mode",
							"wpa", GenerateXMLDebug.SET_VALUE,
							null, null);
					authModeObj.setWpa(airScreenImpl.getSourceEncryptionMode(index));
				}
				
				/** element: <airscreen>.<source>.<type>.<client>.<auth-mode>.<wpa-psk> */
				oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client/auth-mode",
						"wpa-psk", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(airScreenImpl.isConfigSourceWpaPsk(index)){
					
					oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client/auth-mode",
							"wpa-psk", GenerateXMLDebug.SET_VALUE,
							null, null);
					authModeObj.setWpaPsk(airScreenImpl.getSourceEncryptionMode(index));
				}
				
				/** element: <airscreen>.<source>.<type>.<client>.<auth-mode>.<wpa-8021x> */
				oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client/auth-mode",
						"wpa-8021x", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(airScreenImpl.isConfigSourceWpa8021X(index)){
					
					oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client/auth-mode",
							"wpa-8021x", GenerateXMLDebug.SET_VALUE,
							null, null);
					authModeObj.setWpa8021X(airScreenImpl.getSourceEncryptionMode(index));
				}
				
				/** element: <airscreen>.<source>.<type>.<client>.<auth-mode>.<wpa2-psk> */
				oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client/auth-mode",
						"wpa2-psk", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(airScreenImpl.isConfigSourceWpa2Psk(index)){
					
					oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client/auth-mode",
							"wpa2-psk", GenerateXMLDebug.SET_VALUE,
							null, null);
					authModeObj.setWpa2Psk(airScreenImpl.getSourceEncryptionMode(index));
				}
				
				/** element: <airscreen>.<source>.<type>.<client>.<auth-mode>.<wpa2-8021x> */
				oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client/auth-mode",
						"wpa2-8021x", GenerateXMLDebug.CONFIG_ELEMENT,
						null, null);
				if(airScreenImpl.isConfigSourceWpa28021X(index)){
					
					oDebug.debug("/configuration/airscreen/source[@name='"+airScreenImpl.getSourceName(index)+"']/type/client/auth-mode",
							"wpa2-8021x", GenerateXMLDebug.CONFIG_ELEMENT,
							null, null);
					authModeObj.setWpa28021X(airScreenImpl.getSourceEncryptionMode(index));
				}
			}
		}
		ariChildLevel_3.clear();
	}
	
	private AirscreenBehavior createAirscreenBehavior(int index) throws Exception{
		
		/** <airscreen>.<behavior> */
		AirscreenBehavior behaviorObj = new AirscreenBehavior();
		
		/** attribute: name */
		oDebug.debug("/configuration/airscreen",
				"behavior", GenerateXMLDebug.SET_NAME,
				null, null);
		behaviorObj.setName(airScreenImpl.getBehaviorName(index));
		
		/** attribute: operation */
		behaviorObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <airscreen>.<behavior>.<type> */
		AirscreenBehaviorType typeObj = new AirscreenBehaviorType();
		ariChildLevel_1.add(typeObj);
		behaviorObj.setType(typeObj);
		
		generateBehaviorChildLevel_1(index);
		return behaviorObj;
	}
	
	private void generateBehaviorChildLevel_1(int index) throws Exception{
		/**
		 * <airscreen>.<behavior>.<type>					AirscreenBehaviorType
		 */
		for(Object childObj : ariChildLevel_1){
			
			/** element: <airscreen>.<behavior>.<type> */
			if(childObj instanceof AirscreenBehaviorType){
				AirscreenBehaviorType typeObj = (AirscreenBehaviorType)childObj;
				
				/** element: <airscreen>.<behavior>.<type>.<reconnection> */
				AirscreenBehaviorType.Reconnection reconnectionObj = new AirscreenBehaviorType.Reconnection();
				ariChildLevel_2.add(reconnectionObj);
				typeObj.setReconnection(reconnectionObj);
			}
		}
		ariChildLevel_1.clear();
		generateBehaviorChildLevel_2(index);
	}
	
	private void generateBehaviorChildLevel_2(int index) throws Exception{
		/**
		 * <airscreen>.<behavior>.<type>.<reconnection>				AirscreenBehaviorType.Reconnection
		 */
		for(Object childObj : ariChildLevel_2){
			
			/** element: <airscreen>.<behavior>.<type>.<reconnection> */
			if(childObj instanceof AirscreenBehaviorType.Reconnection){
				AirscreenBehaviorType.Reconnection reconnectionObj = (AirscreenBehaviorType.Reconnection)childObj;
				
				/** element: <airscreen>.<behavior>.<type>.<reconnection>.<connection-case> */
				oDebug.debug("/configuration/airscreen/behavior[@name='"+airScreenImpl.getBehaviorName(index)+"']/type/reconnection",
						"connection-case", GenerateXMLDebug.SET_VALUE,
						null, null);
				Object[][] connectParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, airScreenImpl.getBehaviorConnectionType(index)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				reconnectionObj.setConnectionCase(
						(ReconnectionConnectionCase)CLICommonFunc.createObjectWithName(ReconnectionConnectionCase.class, connectParm)
				);
				
				/** element: <airscreen>.<behavior>.<type>.<reconnection>.<interval> */
				oDebug.debug("/configuration/airscreen/behavior[@name='"+airScreenImpl.getBehaviorName(index)+"']/type/reconnection",
						"interval", GenerateXMLDebug.SET_VALUE,
						null, null);
				Object[][] intervalParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, airScreenImpl.getBehaviorInterval(index)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				reconnectionObj.setInterval(
						(ReconnectionInterval)CLICommonFunc.createObjectWithName(ReconnectionInterval.class, intervalParm)
				);
				
				/** element: <airscreen>.<behavior>.<type>.<reconnection>.<threshold> */
				oDebug.debug("/configuration/airscreen/behavior[@name='"+airScreenImpl.getBehaviorName(index)+"']/type/reconnection",
						"threshold", GenerateXMLDebug.SET_VALUE,
						null, null);
				Object[][] thresholdParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, airScreenImpl.getBehaviorThreshold(index)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				reconnectionObj.setThreshold(
						(ReconnectionThreshold)CLICommonFunc.createObjectWithName(ReconnectionThreshold.class, thresholdParm)
				);
			}
		}
		ariChildLevel_2.clear();
	}
	
	private AirscreenObj.Rule createRule(int ruleIndex){
		
		/** <airscreen>.<rule> */
		AirscreenObj.Rule ruleObj = new AirscreenObj.Rule();
		
		/** attribute: name */
		oDebug.debug("/configuration/airscreen",
				"rule", GenerateXMLDebug.SET_NAME,
				null, null);
		ruleObj.setName(airScreenImpl.getAirRuleName(ruleIndex));
		
		/** attribute: operation */
		ruleObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** elemnet: <airscreen>.<rule>.<cr> */
		ruleObj.setCr("");
		
		/** elemnet: <airscreen>.<rule>.<source> */
		oDebug.debug("/configuration/airscreen/rule[@name='"+airScreenImpl.getAirRuleName(ruleIndex)+"']",
				"source", GenerateXMLDebug.CONFIG_ELEMENT,
				null, null);
		if(airScreenImpl.isConfigRuleSource(ruleIndex)){
			
			oDebug.debug("/configuration/airscreen/rule[@name='"+airScreenImpl.getAirRuleName(ruleIndex)+"']",
					"source", GenerateXMLDebug.SET_NAME,
					null, null);
			ruleObj.setSource(CLICommonFunc.createAhNameActObj(airScreenImpl.getAirRuleSourceName(ruleIndex), CLICommonFunc.getYesDefault()));
		}
		
		/** elemnet: <airscreen>.<rule>.<action> */
		oDebug.debug("/configuration/airscreen/rule[@name='"+airScreenImpl.getAirRuleName(ruleIndex)+"']",
				"action", GenerateXMLDebug.CONFIG_ELEMENT,
				null, null);
		for(int actionIndex =0 ; actionIndex < airScreenImpl.getAirRuleActionSize(ruleIndex); actionIndex++){
			
			oDebug.debug("/configuration/airscreen/rule[@name='"+airScreenImpl.getAirRuleName(ruleIndex)+"']",
					"action", GenerateXMLDebug.SET_NAME,
					null, null);
			ruleObj.getAction().add(CLICommonFunc.createAhNameActObj(airScreenImpl.getAirRuleActionName(ruleIndex, actionIndex), CLICommonFunc.getYesDefault()));
		}
		
		/** elemnet: <airscreen>.<rule>.<behavior> */
		oDebug.debug("/configuration/airscreen/rule[@name='"+airScreenImpl.getAirRuleName(ruleIndex)+"']",
				"behavior", GenerateXMLDebug.CONFIG_ELEMENT,
				null, null);
		for(int behaviorIndex =0; behaviorIndex < airScreenImpl.getAirRuleBehaviorSize(ruleIndex); behaviorIndex++){
			
			oDebug.debug("/configuration/airscreen/rule[@name='"+airScreenImpl.getAirRuleName(ruleIndex)+"']",
					"behavior", GenerateXMLDebug.SET_NAME,
					null, null);
			ruleObj.getBehavior().add(CLICommonFunc.createAhNameActObj(airScreenImpl.getAirRuleBehaviorName(ruleIndex, behaviorIndex), CLICommonFunc.getYesDefault()));
		}
		
		return ruleObj;
	}
}
