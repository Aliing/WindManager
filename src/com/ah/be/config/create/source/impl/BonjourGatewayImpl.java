package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.BonjourGatewayInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.BonjourFilterRule;
import com.ah.bo.network.BonjourGatewaySettings;
import com.ah.bo.network.BonjourService;
import com.ah.bo.network.VlanGroup;
import com.ah.util.MgrUtil;

public class BonjourGatewayImpl implements BonjourGatewayInt {
	
	private HiveAp hiveAp;
	private BonjourGatewaySettings bonjouGateway;
	private List<String> bonjourVlans = new ArrayList<String>();
	private List<BonjourFilterRule> ruleList = new ArrayList<BonjourFilterRule>();
	
	public BonjourGatewayImpl(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
		this.bonjouGateway = hiveAp.getConfigTemplate().getBonjourGw();
		this.loadBonjourFilterRuleVlan();
	}
	
	private void loadBonjourFilterRuleVlan(){
		if(bonjouGateway == null || bonjouGateway.getVlans() == null){
			return;
		}
		List<String> list = CLICommonFunc.mergeRangeList(bonjouGateway.getVlans());
		for(String vlan : list){
			if(vlan.indexOf("-")>0){
				vlan = vlan.replaceAll("\\s*\\-\\s*"," ");
			}
			bonjourVlans.add(vlan);
		}
		String realmName = (hiveAp.getRealmName() == null) ? "" : hiveAp.getRealmName();
		for(BonjourFilterRule rule : bonjouGateway.getRules()) {
			if (rule.getRealmName() == null || rule.getRealmName().equals("") || rule.getRealmName().equalsIgnoreCase("null") 
			    || rule.getRealmName().equalsIgnoreCase("[-any-]") || realmName.equalsIgnoreCase(rule.getRealmName())) {
				ruleList.add(rule);
			}
		}
	}
	
	public String getWlanName() {
		return hiveAp.getConfigTemplate().getConfigName();
	}

	public String getBonjourGatewayGuiName() {
		return MgrUtil.getUserMessage("config.upload.debug.bonjourGateWayConfiguration");
	}

	public boolean isEnableBonjourGateway() {
		return bonjouGateway != null /*&& !ruleList.isEmpty()*/;
	}

	public int getBonjourGatewayFiletrRulesize() {
		//return bonjouGateway.getRules().size();		
		return ruleList.size();
	}
	
	public String getBonjourGatewayFiletrRuleName(int index){
		index = getReverseIndex(index);
		BonjourService bonjourService = ruleList.get(index).getBonjourService();
		return bonjourService.getType();
	}
	
	public String getBonjourGatewayFiletrRuleId(int index){
		index = getReverseIndex(index);
		return String.valueOf(ruleList.get(index).getRuleId());
	}

	public boolean isConfigBonjouGateway() {
		return NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"6.0.1.0") < 0;
	}

	public int getBonjourVlanSize() {
		return bonjourVlans.size();
	}

	public String getBonjourValn(int index) {
		return bonjourVlans.get(index);
	}

	public boolean isConfigPriority() {
		return hiveAp.getPriority() != null && !hiveAp.getPriority().equals("");
	}
	
	public int getPriority() throws CreateXMLException {
		return Integer.parseInt(hiveAp.getPriority());
	}
	
	public boolean isConfigBefore(int index){
		return index != 0;
	}
	
	public String getBonjourBeforeIdValue(int index){
		index = ruleList.size()-index;
		return String.valueOf(ruleList.get(index).getRuleId());
	}
	
	public String getBeforeValue(int index){
		index = ruleList.size() - index;
		StringBuffer strB = new StringBuffer("");
		for(int i=0; i<index; i++){
			strB.append(" ");
		}
		return strB.toString();
	}

	public String getFromGroupName(int index) {
		index = getReverseIndex(index);
		VlanGroup vlanGroup = ruleList.get(index).getFromVlanGroup();
		return vlanGroup != null ? vlanGroup.getVlanGroupName() : "";
	}

	public String getToGroupName(int index) {
		index = getReverseIndex(index);
		VlanGroup vlanGroup = ruleList.get(index).getToVlanGroup();
		return vlanGroup != null ? vlanGroup.getVlanGroupName() : "";
	}
	
	public boolean isConfigMetricValue(int index) {
		index = getReverseIndex(index);
		String metric = ruleList.get(index).getMetric();
		return /*ruleList.get(index).getFilterAction() == 
				MacFilter.FILTER_ACTION_PERMIT && */metric != null && !metric.equals("");
	}

	public String getMetricValue(int index) {
		index = getReverseIndex(index);
		String metric = ruleList.get(index).getMetric();
		if(metric != null && !metric.equals("")){
			return metric;
		}
		return "";
	}
	
//	public boolean isConfigRuleAction(){
//		return NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.2.1.0") < 0;
//	}
//	public boolean isCOnfigRuleActionPermit(int index) {
//		index = getReverseIndex(index);
////		return ruleList.get(index).getFilterAction() == 
////				MacFilter.FILTER_ACTION_PERMIT;
//		return true;
//	}
//
//	public boolean isCOnfigRuleActionDeny(int index) {
//		index = getReverseIndex(index);
////		return ruleList.get(index).getFilterAction() == 
////				MacFilter.FILTER_ACTION_DENY;
//		return false;
//	}
	
	public int getReverseIndex(int index){
		return ruleList.size() - index -1;
	}
	
	public String getRealmName() {
		return hiveAp.getRealmName();
	}
}
