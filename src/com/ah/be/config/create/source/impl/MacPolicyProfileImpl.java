package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.IpPolicyRule;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacPolicy;
import com.ah.bo.network.MacPolicyRule;

import com.ah.be.config.create.source.IpPolicyProfileInt;
import com.ah.be.config.create.source.MacPolicyProfileInt;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.util.MgrUtil;

/**
 * 
 * @author zhang
 *
 */
public class MacPolicyProfileImpl implements MacPolicyProfileInt {
	
	private final HiveAp hiveAp;
	private final MacPolicy macPolicy;

	public MacPolicyProfileImpl(MacPolicy macPolicy, HiveAp hiveAp) {
		this.hiveAp = hiveAp;
		this.macPolicy = macPolicy;
	}
	
	public String getMacPolicyGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.macPolicies");
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}
	
	public String getUpdateTime(){
		List<Object> macPolicyTimeList = new ArrayList<Object>();
		macPolicyTimeList.add(hiveAp);
		macPolicyTimeList.add(macPolicy);
		if(macPolicy.getRules() != null){
			for(MacPolicyRule macRuleObj : macPolicy.getRules()){
				if(macRuleObj != null){
					macPolicyTimeList.add(macRuleObj.getSourceMac());
					macPolicyTimeList.add(macRuleObj.getDestinationMac());
				}
			}
		}
		return CLICommonFunc.getLastUpdateTime(macPolicyTimeList);
	}
	
	public String getMacPolicyName(){
		return macPolicy.getPolicyName();
	}
	
	public int getMacPolicyIdSize(){
		return macPolicy.getRules().size();
	}
	
	public int getMacPolicyIdName(int index){
		index = getReverseIndex(index);
		return macPolicy.getRules().get(index).getRuleId();
	}
	
	public String getPolicyFromValue(int index) throws Exception {
		index = getReverseIndex(index);
		if(macPolicy.getRules().get(index).getSourceMac() == null){
			return "00-00-00-00-00-00 0";
		}else{
			String sAddress = CLICommonFunc.transFormMacAddrOrOui(CLICommonFunc.getMacAddressOrOui(macPolicy.getRules().get(index).getSourceMac(), hiveAp).getMacEntry());
			int mask;
			if(sAddress.length() == 8){
				sAddress = sAddress + "-00-00-00";
				mask = 24;
			}else{
				mask = 48;
			}
			return sAddress + " " + mask;
		}
	}
	
	public String getPolicyToValue(int index) throws Exception{
		index = getReverseIndex(index);
		if(macPolicy.getRules().get(index).getDestinationMac() == null){
			return "00-00-00-00-00-00 0";
		}else{
			String sAddress = CLICommonFunc.transFormMacAddrOrOui(CLICommonFunc.getMacAddressOrOui(macPolicy.getRules().get(index).getDestinationMac(), hiveAp).getMacEntry());
			int iMask;
			if(sAddress.length() == 8){
				sAddress = sAddress + "-00-00-00";
				iMask = 24;
			}else{
				iMask = 48;
			}
			return sAddress + " " + iMask;
		}
	}
	
	public boolean isConfigPolicyAction(int index, IpPolicyProfileInt.IpPolicyActionValue actionType){
		index = getReverseIndex(index);
		short action = macPolicy.getRules().get(index).getFilterAction();
		if(actionType == IpPolicyProfileInt.IpPolicyActionValue.permit){
			return action == MacFilter.FILTER_ACTION_PERMIT;
		}else if(actionType == IpPolicyProfileInt.IpPolicyActionValue.deny){
			return action == MacFilter.FILTER_ACTION_DENY;
		}else{
			return false;
		}
	}
	
	public boolean isConfigPolicyLog(int index) {
		index = getReverseIndex(index);
		return macPolicy.getRules().get(index).getActionLog() != IpPolicyRule.POLICY_LOGGING_OFF;
//		if(CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())){
//			return false;
//		}else if(CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())){
//			index = getReverseIndex(index);
//			return macPolicy.getRules().get(index).getActionLog() != IpPolicyRule.POLICY_LOGGING_OFF;
//		}else{
//			return false;
//		}
	}
	
	public boolean isConfigPolicyLogType(int index, IpPolicyProfileInt.IpPolicyLog logType){
		int ruleIndex = getReverseIndex(index);
		short logvalue = macPolicy.getRules().get(ruleIndex).getActionLog();
		if(logType == IpPolicyProfileInt.IpPolicyLog.initiate_session){
			return logvalue == IpPolicyRule.POLICY_LOGGING_INITIATE;
		}else if(logType == IpPolicyProfileInt.IpPolicyLog.terminate_session){
			return logvalue == IpPolicyRule.POLICY_LOGGING_TERMINATE;
		}else if(logType == IpPolicyProfileInt.IpPolicyLog.packet_drop){
			return logvalue == IpPolicyRule.POLICY_LOGGING_DROP;
		}else if(logType == IpPolicyProfileInt.IpPolicyLog.cr){
			return logvalue == IpPolicyRule.POLICY_LOGGING_BOTH;
		}else{
			return false;
		}
	}
	
	public boolean isConfigBefore(int index){
		return index != 0;
	}
	
	public int getPolicyBeforeIdValue(int index){
		index = macPolicy.getRules().size() - index;
		return macPolicy.getRules().get(index).getRuleId();
	}
	
	public String getBeforeValue(int index){
		index = macPolicy.getRules().size() - index;
		StringBuffer strB = new StringBuffer("");
		for(int i=0; i<index; i++){
			strB.append(" ");
		}
		return strB.toString();
	}
	
	private int getReverseIndex(int index){
		return macPolicy.getRules().size() - index -1;
	}
	
}
