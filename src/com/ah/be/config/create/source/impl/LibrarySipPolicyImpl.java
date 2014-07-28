package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.LibrarySipPolicyInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.useraccess.RadiusLibrarySip;
import com.ah.bo.useraccess.RadiusLibrarySipRule;
import com.ah.util.MgrUtil;

public class LibrarySipPolicyImpl implements LibrarySipPolicyInt {
	
	private HiveAp hiveAp;
	private RadiusLibrarySip sipPolicy;

	public LibrarySipPolicyImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
		this.sipPolicy = hiveAp.getRadiusServerProfile().getSipPolicy();
	}
	
	public String getIpPolicyGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.librarySip");
	}
	
	public String getIpPolicyName(){
		return sipPolicy.getPolicyName();
	}
	
	public String getDefaultGroupName(){
		return sipPolicy.getDefUserGroup().getGroupName();
	}
	
	public int getLibrarySipPolicySize(){
		return sipPolicy.getRules().size();
	}
	
	public int getLibrarySipPolicyId(int index){
		index = getReverseIndex(index);
		return sipPolicy.getRules().get(index).getRuleId();
	}
	
	private int getReverseIndex(int index){
		return sipPolicy.getRules().size() - index -1;
	}
	
	public String getBeforeValue(int index){
		index = sipPolicy.getRules().size() - index;
		StringBuffer strB = new StringBuffer("");
		for(int i=0; i<index; i++){
			strB.append(" ");
		}
		return strB.toString();
	}
	
	public boolean isConfigBefore(int index){
		return index != 0;
	}
	
	public int getPolicyBeforeIdValue(int index){
		index = sipPolicy.getRules().size()-index;
		return sipPolicy.getRules().get(index).getRuleId();
	}
	
	public String getFieldValue(int index){
		index = getReverseIndex(index);
		return sipPolicy.getRules().get(index).getField();
	}
	
	public boolean isConfigContains(int index){
		index = getReverseIndex(index);
		return sipPolicy.getRules().get(index).getOperator() == RadiusLibrarySipRule.SIP_OPERATOR_CONTAIN;
	}
	
	public boolean isConfigDiffersFrom(int index){
		index = getReverseIndex(index);
		return sipPolicy.getRules().get(index).getOperator() == RadiusLibrarySipRule.SIP_OPERATOR_DIFFER;
	}
	
	public boolean isConfigMatches(int index){
		index = getReverseIndex(index);
		return sipPolicy.getRules().get(index).getOperator() == RadiusLibrarySipRule.SIP_OPERATOR_MATCH;
	}
	
	public boolean isConfigOccursAfter(int index){
		index = getReverseIndex(index);
		return sipPolicy.getRules().get(index).getOperator() == RadiusLibrarySipRule.SIP_OPERATOR_OCCUR_AFTER;
	}
	
	public boolean isConfigOccursBefore(int index){
		index = getReverseIndex(index);
		return sipPolicy.getRules().get(index).getOperator() == RadiusLibrarySipRule.SIP_OPERATOR_OCCUR_BEFORE;
	}
	
	public boolean isConfigStartsWith(int index){
		index = getReverseIndex(index);
		return sipPolicy.getRules().get(index).getOperator() == RadiusLibrarySipRule.SIP_OPERATOR_START;
	}
	
	public boolean isConfigEqual(int index){
		index = getReverseIndex(index);
		return sipPolicy.getRules().get(index).getOperator() == RadiusLibrarySipRule.SIP_OPERATOR_EQUAL;
	}
	
	public boolean isConfigGreaterThan(int index){
		index = getReverseIndex(index);
		return sipPolicy.getRules().get(index).getOperator() == RadiusLibrarySipRule.SIP_OPERATOR_GREATER_THAN;
	}
	
	public boolean isConfigLessThan(int index){
		index = getReverseIndex(index);
		return sipPolicy.getRules().get(index).getOperator() == RadiusLibrarySipRule.SIP_OPERATOR_LESS_THAN;
	}
	
	public String getLibrarySipPolicyValue(int index){
		index = getReverseIndex(index);
		return sipPolicy.getRules().get(index).getValueStr();
	}
	
	public String getLibrarySipPolicyGroup(int index){
		index = getReverseIndex(index);
		return sipPolicy.getRules().get(index).getUserGroup().getGroupName();
	}
	
	public String getUserGroupAction(int index){
		index = getReverseIndex(index);
		short action = sipPolicy.getRules().get(index).getAction();
		if(action == RadiusLibrarySipRule.SIP_RULE_ACTION_PERMIT){
			return "permit";
		}else if(action == RadiusLibrarySipRule.SIP_RULE_ACTION_DENY){
			return "deny";
		}else if(action == RadiusLibrarySipRule.SIP_RULE_ACTION_RESTRICTED){
			return "restricted";
		}else {
			return "";
		}
	}
	
	public String getUserGroupMessage(int index){
		index = getReverseIndex(index);
		return sipPolicy.getRules().get(index).getMessage();
	}
	
	public String getDefUserGroupAction(){
		short action = sipPolicy.getDefAction();
		if(action == RadiusLibrarySipRule.SIP_RULE_ACTION_PERMIT){
			return "permit";
		}else if(action == RadiusLibrarySipRule.SIP_RULE_ACTION_DENY){
			return "deny";
		}else if(action == RadiusLibrarySipRule.SIP_RULE_ACTION_RESTRICTED){
			return "restricted";
		}else {
			return "";
		}
	}
	
	public String getDefUserGroupMessage(){
		return sipPolicy.getDefMessage();
	}
	
}
