package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.AirScreenProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.AirScreenAction;
import com.ah.bo.network.AirScreenBehavior;
import com.ah.bo.network.AirScreenRule;
import com.ah.bo.network.AirScreenSource;
import com.ah.bo.network.MacOrOui;
import com.ah.xml.be.config.AirscreenAuthModeWpa;
import com.ah.xml.be.config.ReconnectionConnectionCaseValue;
import com.ah.xml.be.config.WpaEncryptionValue;

/**
 * @author zhang
 * @version 2009-6-5 10:47:11
 */

public class AirScreenProfileImpl implements AirScreenProfileInt {
	
	private HiveAp hiveAp;
	private List<CLIRule> airRuleList;
	private List<AirScreenAction>  airActionList;
	private List<AirScreenSource>  airSourceList;
	private List<AirScreenBehavior>  airBehaviorList;
	
	public static class CLIRule{
		
		private String profileName;
		
		private AirScreenSource source;
		
		private List<AirScreenBehavior> behaviors;
		
		private List<AirScreenAction> actions;
		
		public String getProfileName(){
			return this.profileName;
		}
		
		public void setProfileName(String profileName){
			this.profileName = profileName;
		}
		
		public AirScreenSource getSource(){
			return this.source;
		}
		
		public void setSource(AirScreenSource source){
			this.source = source;
		}
		
		public List<AirScreenBehavior> getBehaviors(){
			return this.behaviors;
		}
		
		public void setBehaviors(List<AirScreenBehavior> behaviors){
			this.behaviors = behaviors;
		}
		
		public List<AirScreenAction> getActions(){
			return this.actions;
		}
		
		public void setActions(List<AirScreenAction> actions){
			this.actions = actions;
		}
	}

	public AirScreenProfileImpl(List<AirScreenRule> airRuleList, HiveAp hiveAp){
		this.hiveAp = hiveAp;
		
		Map<String, AirScreenAction>  airActionMap = new HashMap<String, AirScreenAction>();
		Map<String, AirScreenSource>  airSourceMap = new HashMap<String, AirScreenSource>();
		Map<String, AirScreenBehavior>  airBehaviorMap = new HashMap<String, AirScreenBehavior>();
		for(AirScreenRule airRule : airRuleList){
			
			CLIRule cliRule = new CLIRule();
			cliRule.setProfileName(airRule.getProfileName());
			cliRule.setSource(airRule.getSource());
			cliRule.setActions(new ArrayList<AirScreenAction>(airRule.getActions()));
			cliRule.setBehaviors(new ArrayList<AirScreenBehavior>(airRule.getBehaviors()));
			
			if(airRule.getSource() != null && !airSourceMap.containsKey(airRule.getSource().getProfileName())){
				airSourceMap.put(airRule.getSource().getProfileName(), airRule.getSource());
			}
			
			if(airRule.getActions() != null){
				for(AirScreenAction action : airRule.getActions()){
					if(!airActionMap.containsKey(action.getProfileName())){
						airActionMap.put(action.getProfileName(), action);
					}
				}
			}
			
			if(airRule.getBehaviors() != null){
				for(AirScreenBehavior behavior : airRule.getBehaviors()){
					if(!airBehaviorMap.containsKey(behavior.getProfileName())){
						airBehaviorMap.put(behavior.getProfileName(), behavior);
					}
				}
			}
		}
		
		airActionList = new ArrayList<AirScreenAction>(airActionMap.values());
		airSourceList = new ArrayList<AirScreenSource>(airSourceMap.values());
		airBehaviorList = new ArrayList<AirScreenBehavior>(airBehaviorMap.values());
	}

	public int getSourceSize() {
		return airSourceList.size();
	}

	public int getActionSize() {
		return airActionList.size();
	}

	public int getBehaviorSize() {
		return airBehaviorList.size();
	}

	public int getRuleSize() {
		if(airRuleList == null){
			return 0;
		}else{
			return airRuleList.size();
		}
	}
	
	public String getActionName(int index){
		return airActionList.get(index).getProfileName();
	}
	
	public boolean isConfigActionDeAuth(int index){
		return airActionList.get(index).getType() == AirScreenAction.TYPE_DE_AUTH;
	}
	
	public boolean isConfigActionLocalBan(int index){
		return airActionList.get(index).getType() == AirScreenAction.TYPE_LOCAL_BAN;
	}
	
	public boolean isConfigActionReportToHm(int index){
		return airActionList.get(index).getType() == AirScreenAction.TYPE_REPORT_TO_HM;
	}
	
	public int getLocalBanInterval(int index){
		return airActionList.get(index).getInterval();
	}
	
	public String getSourceName(int index){
		return airSourceList.get(index).getProfileName();
	}
	
	public String getSourceOui(int index) throws CreateXMLException{
		MacOrOui oui = airSourceList.get(index).getOui();
		if(oui == null){
			return null;
		}else{
			String ouiStr = CLICommonFunc.getMacAddressOrOui(oui, this.hiveAp).getMacEntry();
			return CLICommonFunc.transFormMacAddrOrOui(ouiStr);
		}
	}
	
	public String getSourceRssi(int index){
		return airSourceList.get(index).getMinRssi() + " - " + airSourceList.get(index).getMaxRssi();
	}
	
	public boolean isConfigSourceOpen(int index){
		return airSourceList.get(index).getAuthMode() == AirScreenSource.AUTH_MODE_OPEN;
	}
	
	public boolean isConfigSourceWep(int index){
		return airSourceList.get(index).getAuthMode() == AirScreenSource.AUTH_MODE_WEP;
	}
	
	public boolean isConfigSourceWepOpen(int index){
		return airSourceList.get(index).getAuthMode() == AirScreenSource.AUTH_MODE_WEP_OPEN;
	}
	
	public boolean isConfigSourceWepShared(int index){
		return airSourceList.get(index).getAuthMode() == AirScreenSource.AUTH_MODE_WEP_SHARED;
	}
	
	public boolean isConfigSourceDynamicWep(int index){
		return airSourceList.get(index).getAuthMode() == AirScreenSource.AUTH_MODE_DYNAMIC_WEP;
	}
	
	public boolean isConfigSourceWpa(int index){
		return airSourceList.get(index).getAuthMode() == AirScreenSource.AUTH_MODE_WPA;
	}
	
	public boolean isConfigSourceWpaPsk(int index){
		return airSourceList.get(index).getAuthMode() == AirScreenSource.AUTH_MODE_WPA_PSK;
	}
	
	public boolean isConfigSourceWpa8021X(int index){
		return airSourceList.get(index).getAuthMode() == AirScreenSource.AUTH_MODE_WPA_8021X;
	}
	
	public boolean isConfigSourceWpa2Psk(int index){
		return airSourceList.get(index).getAuthMode() == AirScreenSource.AUTH_MODE_WPA2_PSK;
	}
	
	public boolean isConfigSourceWpa28021X(int index){
		return airSourceList.get(index).getAuthMode() == AirScreenSource.AUTH_MODE_WPA2_8021X;
	}
	
	public AirscreenAuthModeWpa getSourceEncryptionMode(int index){
		AirscreenAuthModeWpa modeObj = new AirscreenAuthModeWpa();
		short mode = airSourceList.get(index).getEncryptionMode();
		if(mode == AirScreenSource.ENCRYPTION_MODE_TKIP){
			modeObj.setValue(WpaEncryptionValue.TKIP);
		}else if(mode == AirScreenSource.ENCRYPTION_MODE_AES){
			modeObj.setValue(WpaEncryptionValue.AES);
		}else{
			modeObj.setValue(WpaEncryptionValue.ANY);
		}
		
		return modeObj;
	}
	
	public String getBehaviorName(int index){
		return airBehaviorList.get(index).getProfileName();
	}
	
	public ReconnectionConnectionCaseValue getBehaviorConnectionType(int index){
		short connCase = airBehaviorList.get(index).getConnectionCase();
		if(connCase == AirScreenBehavior.CONNECTION_CASE_FAILURE){
			return ReconnectionConnectionCaseValue.FAILURE;
		}else if(connCase == AirScreenBehavior.CONNECTION_CASE_SUCCESS){
			return ReconnectionConnectionCaseValue.SUCCESS;
		}else{
			return ReconnectionConnectionCaseValue.ANY;
		}
	}
	
	public int getBehaviorInterval(int index){
		return airBehaviorList.get(index).getInterval();
	}
	
	public int getBehaviorThreshold(int index){
		return airBehaviorList.get(index).getThreshold();
	}
	
	public String getAirRuleName(int ruleIndex){
		return airRuleList.get(ruleIndex).getProfileName();
	}
	
	public boolean isConfigRuleSource(int ruleIndex){
		return airRuleList.get(ruleIndex).getSource() != null;
	}
	
	public String getAirRuleSourceName(int ruleIndex){
		return airRuleList.get(ruleIndex).getSource().getProfileName();
	}
	
	public int getAirRuleActionSize(int ruleIndex){
		return airRuleList.get(ruleIndex).getActions().size();
	}
	
	public String getAirRuleActionName(int ruleIndex, int actionIndex){
		return airRuleList.get(ruleIndex).getActions().get(actionIndex).getProfileName();
	}
	
	public int getAirRuleBehaviorSize(int ruleIndex){
		return airRuleList.get(ruleIndex).getBehaviors().size();
	}
	
	public String getAirRuleBehaviorName(int ruleIndex, int behaviorIndex){
		return airRuleList.get(ruleIndex).getBehaviors().get(behaviorIndex).getProfileName();
	}
}
