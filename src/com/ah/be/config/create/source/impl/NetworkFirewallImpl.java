package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.NetworkFirewallInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.FirewallPolicy;
import com.ah.bo.network.FirewallPolicyRule;
import com.ah.bo.network.IpPolicyRule;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnNetworkSub;
import com.ah.util.MgrUtil;
import com.ah.util.coder.AhDecoder;

public class NetworkFirewallImpl implements NetworkFirewallInt {
	
	private HiveAp hiveAp;
	private FirewallPolicy firewall;
	
	private List<FirewallRule> ruleList = new ArrayList<FirewallRule>();
	
	public static final String ANY = "any";
	public static final String IP_RANGE = "ip-range";
	public static final String NETWORK = "network";
	public static final String USER_PROFILE = "user-profile";
	public static final String VPN = "vpn";
	public static final String WILDCARD = "wildcard";
	public static final String ACTION_DENY = "deny";
	public static final String ACTION_PERMIT = "permit";
	public static final String LOGGING_ON = "on";
	public static final String LOGGING_OFF = "off";
	public static final String HOSTNAME = "hostname";
	
	public NetworkFirewallImpl(HiveAp hiveAp) throws CreateXMLException{
		this.hiveAp = hiveAp;
		this.firewall = hiveAp.getConfigTemplate().getFwPolicy();
		loadFirewallRuleList();
	}
	
	private void loadFirewallRuleList() throws CreateXMLException{
		if(ruleList == null){
			ruleList = new ArrayList<FirewallRule>();
		}
		if(hiveAp.getConfigTemplate() == null || hiveAp.getConfigTemplate().getFwPolicy() == null){
			return;
		}
		firewall = hiveAp.getConfigTemplate().getFwPolicy();
//		String policyName = firewall.getPolicyName();
		
		for(FirewallPolicyRule rule : firewall.getRules()){
			ruleList.addAll(this.getFirewallRuleList(rule));
		}
		//default rule
		FirewallRule defRule = new FirewallRule();
		defRule.setFrom(ANY);
		defRule.setTo(ANY);
		defRule.setService(ANY);
		if(firewall.getDefRuleAction() == IpPolicyRule.POLICY_ACTION_PERMIT){
			defRule.setAction(ACTION_PERMIT);
		}else{
			defRule.setAction(ACTION_DENY);
		}
		if(firewall.getDefRuleLog() == FirewallPolicyRule.POLICY_LOGGING_OFF){
			defRule.setLogging(LOGGING_OFF);
		}else{
			defRule.setLogging(LOGGING_ON);
		}
		ruleList.add(defRule);
		//end default rule
		
		for(int index=0; index<ruleList.size(); index++){
			ruleList.get(index).setName(String.valueOf(index+1));
			ruleList.get(index).setPosition(index+1);
		}
	}
	
	private List<FirewallRule> getFirewallRuleList(FirewallPolicyRule rule) throws CreateXMLException{
		List<FirewallRule> resList = new ArrayList<FirewallRule>();
		if(rule.isDisableRule()){
			return resList;
		}
		if(rule.getSourceType() == FirewallPolicy.FIREWALL_POLICY_TYPE_NETOBJ){
			FirewallPolicyRule cloneRule = this.cloneRule(rule);
			if(rule.getSourceNtObj() == null){
				cloneRule.setSourceType(FirewallPolicy.FIREWALL_POLICY_TYPE_ANY);
				resList.addAll(getFirewallRuleList(cloneRule));
			}else if(rule.getSourceNtObj().getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_GUEST){
				String cliValue = this.turnIpNetmask(rule.getSourceNtObj().getIpAddressSpace());
				if(cliValue == null){
					cloneRule.setSourceType(FirewallPolicy.FIREWALL_POLICY_TYPE_ANY);
				}else{
					cloneRule.setSourceType(FirewallPolicy.FIREWALL_POLICY_TYPE_CLINET);
					cloneRule.setSourceCliNetwork(cliValue);
				}
				resList.addAll(getFirewallRuleList(cloneRule));
			}else if(rule.getSourceNtObj().getSubItems() == null || rule.getSourceNtObj().getSubItems().isEmpty()){
				cloneRule.setSourceType(FirewallPolicy.FIREWALL_POLICY_TYPE_ANY);
				resList.addAll(getFirewallRuleList(cloneRule));
			}else{
				for(VpnNetworkSub subItem : rule.getSourceNtObj().getSubItems()){
					String cliValue = this.turnIpNetmask(subItem.getIpNetwork());
					if(cliValue == null){
						cloneRule.setSourceType(FirewallPolicy.FIREWALL_POLICY_TYPE_ANY);
					}else{
						cloneRule.setSourceType(FirewallPolicy.FIREWALL_POLICY_TYPE_CLINET);
						cloneRule.setSourceCliNetwork(cliValue);
					}
					resList.addAll(getFirewallRuleList(cloneRule));
					if (subItem.isEnableNat()) {
						String cliLocalValue = this.turnIpNetmask(subItem.getLocalIpNetwork());
						if(cliLocalValue == null){
							cloneRule.setSourceType(FirewallPolicy.FIREWALL_POLICY_TYPE_ANY);
						}else{
							cloneRule.setSourceType(FirewallPolicy.FIREWALL_POLICY_TYPE_CLINET);
							cloneRule.setSourceCliNetwork(cliLocalValue);
						}
						resList.addAll(getFirewallRuleList(cloneRule));
					}
				}
			}
		}else if(rule.getDestType() == FirewallPolicy.FIREWALL_POLICY_TYPE_NETOBJ){
			FirewallPolicyRule cloneRule = this.cloneRule(rule);
			if(rule.getDestinationNtObj() == null){
				cloneRule.setDestType(FirewallPolicy.FIREWALL_POLICY_TYPE_ANY);
				resList.addAll(getFirewallRuleList(cloneRule));
			}else if(rule.getDestinationNtObj().getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_GUEST){
				String cliValue = this.turnIpNetmask(rule.getDestinationNtObj().getIpAddressSpace());
				if(cliValue == null){
					cloneRule.setDestType(FirewallPolicy.FIREWALL_POLICY_TYPE_ANY);
				}else{
					cloneRule.setDestType(FirewallPolicy.FIREWALL_POLICY_TYPE_CLINET);
					cloneRule.setDestCliNetwork(cliValue);
				}
				resList.addAll(getFirewallRuleList(cloneRule));
			}else if(rule.getDestinationNtObj().getSubItems() == null || rule.getDestinationNtObj().getSubItems().isEmpty()){
				cloneRule.setDestType(FirewallPolicy.FIREWALL_POLICY_TYPE_ANY);
				resList.addAll(getFirewallRuleList(cloneRule));
			}else{
				for(VpnNetworkSub subItem : rule.getDestinationNtObj().getSubItems()){
					String cliValue = this.turnIpNetmask(subItem.getIpNetwork());
					if(cliValue == null){
						cloneRule.setDestType(FirewallPolicy.FIREWALL_POLICY_TYPE_ANY);
					}else{
						cloneRule.setDestType(FirewallPolicy.FIREWALL_POLICY_TYPE_CLINET);
						cloneRule.setDestCliNetwork(cliValue);
					}
					resList.addAll(getFirewallRuleList(cloneRule));
					if (subItem.isEnableNat()) {
						String cliLocalValue = this.turnIpNetmask(subItem.getLocalIpNetwork());
						if(cliLocalValue == null){
							cloneRule.setDestType(FirewallPolicy.FIREWALL_POLICY_TYPE_ANY);
						}else{
							cloneRule.setDestType(FirewallPolicy.FIREWALL_POLICY_TYPE_CLINET);
							cloneRule.setDestCliNetwork(cliLocalValue);
						}
						resList.addAll(getFirewallRuleList(cloneRule));
					}
				}
			}
		}else{
			FirewallRule ruleItem = new FirewallRule();
			// set from value
			if(rule.getSourceType() == FirewallPolicy.FIREWALL_POLICY_TYPE_ANY){
				ruleItem.setFrom(ANY);
			}else if(rule.getSourceType() == FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET){
				SingleTableItem ipItem = CLICommonFunc.getIpAddress(rule.getSourceIp(), this.hiveAp);
				String ipAddr = ipItem.getIpAddress();
				String maskStr = ipItem.getNetmask();
				String startIp = MgrUtil.getStartIpAddressValue(ipAddr, maskStr);
				ruleItem.setFrom(NETWORK + " " + startIp + " " + maskStr);
			}else if(rule.getSourceType() == FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE){
				SingleTableItem ipItem = CLICommonFunc.getIpAddress(rule.getSourceIp(), this.hiveAp);
				String ipFrom = ipItem.getIpAddress();
				String ipTo = ipItem.getNetmask();
				ruleItem.setFrom(IP_RANGE + " " + ipFrom + " " + ipTo);
			}else if(rule.getSourceType() == FirewallPolicy.FIREWALL_POLICY_TYPE_UPOBJ){
				String userName = rule.getSourceUp().getUserProfileName();
				ruleItem.setFrom(USER_PROFILE + " " + userName);
			}else if(rule.getSourceType() == FirewallPolicy.FIREWALL_POLICY_TYPE_VPN){
				ruleItem.setFrom(VPN);
			}else if(rule.getSourceType() == FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD){
				SingleTableItem ipItem = CLICommonFunc.getIpAddress(rule.getSourceIp(), this.hiveAp);
				String ipAddr = ipItem.getIpAddress();
				String maskStr = ipItem.getNetmask();
				String startIp = MgrUtil.getStartIpAddressValue(ipAddr, maskStr);
				ruleItem.setFrom(WILDCARD + " " + startIp + " " + maskStr);
			}else if(rule.getSourceType() == FirewallPolicy.FIREWALL_POLICY_TYPE_CLINET){
				ruleItem.setFrom(NETWORK + " " + rule.getSourceCliNetwork());
			}
			
			// set to value
			if(rule.getDestType() == FirewallPolicy.FIREWALL_POLICY_TYPE_ANY){
				ruleItem.setTo(ANY);
			}else if(rule.getDestType() == FirewallPolicy.FIREWALL_POLICY_TYPE_IPNET){
				SingleTableItem ipItem = CLICommonFunc.getIpAddress(rule.getDestinationIp(), this.hiveAp);
				String ipAddr = ipItem.getIpAddress();
				String maskStr = ipItem.getNetmask();
				String startIp = MgrUtil.getStartIpAddressValue(ipAddr, maskStr);
				ruleItem.setTo(NETWORK + " " + startIp + " " + maskStr);
			}else if(rule.getDestType() == FirewallPolicy.FIREWALL_POLICY_TYPE_IPRANGE){
				SingleTableItem ipItem = CLICommonFunc.getIpAddress(rule.getDestinationIp(), this.hiveAp);
				String ipFrom = ipItem.getIpAddress();
				String ipTo = ipItem.getNetmask();
				ruleItem.setTo(IP_RANGE + " " + ipFrom + " " + ipTo);
			}else if(rule.getDestType() == FirewallPolicy.FIREWALL_POLICY_TYPE_VPN){
				ruleItem.setTo(VPN);
			}else if(rule.getDestType() == FirewallPolicy.FIREWALL_POLICY_TYPE_WILDCARD){
				SingleTableItem ipItem = CLICommonFunc.getIpAddress(rule.getDestinationIp(), this.hiveAp);
				String ipAddr = ipItem.getIpAddress();
				String maskStr = ipItem.getNetmask();
				String startIp = MgrUtil.getStartIpAddressValue(ipAddr, maskStr);
				ruleItem.setTo(WILDCARD + " " + startIp + " " + maskStr);
			}else if(rule.getDestType() == FirewallPolicy.FIREWALL_POLICY_TYPE_HOST){
				SingleTableItem ipItem = CLICommonFunc.getIpAddress(rule.getDestinationIp(), this.hiveAp);
				String ipAddr = ipItem.getIpAddress();
				ruleItem.setTo(HOSTNAME + " " + ipAddr);
			}else if(rule.getDestType() == FirewallPolicy.FIREWALL_POLICY_TYPE_CLINET){
				ruleItem.setTo(NETWORK + " " + rule.getDestCliNetwork());
			}
			
			//set service value
			if(rule.getNetworkService() != null){
				String serviceStr = rule.getNetworkService().getServiceName();
				ruleItem.setService(serviceStr);
			}else{
				ruleItem.setService(ANY);
			}
			
			//set action value
			if(rule.getFilterAction() == IpPolicyRule.POLICY_ACTION_DENY){
				ruleItem.setAction(ACTION_DENY);
			}else{
				ruleItem.setAction(ACTION_PERMIT);
			}
			
			//set logging value
			if(rule.getActionLog() == FirewallPolicyRule.POLICY_LOGGING_OFF){
				ruleItem.setLogging(LOGGING_OFF);
			}else{
				ruleItem.setLogging(LOGGING_ON);
			}
			
			resList.add(ruleItem);
		}
		return resList;
	}
	
	private FirewallPolicyRule cloneRule(FirewallPolicyRule rule){
		if(rule == null){
			return null;
		}
		FirewallPolicyRule cloneRule = new FirewallPolicyRule();
		cloneRule.setActionLog(rule.getActionLog());
		cloneRule.setDestCliNetwork(rule.getDestCliNetwork());
		cloneRule.setDestinationIp(rule.getDestinationIp());
		cloneRule.setDestinationNtObj(rule.getDestinationNtObj());
		cloneRule.setDestType(rule.getDestType());
		cloneRule.setDisableRule(rule.isDisableRule());
		cloneRule.setFilterAction(rule.getFilterAction());
		cloneRule.setNetworkService(rule.getNetworkService());
		cloneRule.setReorder(rule.getReorder());
		cloneRule.setRuleId(rule.getRuleId());
		cloneRule.setSourceCliNetwork(rule.getSourceCliNetwork());
		cloneRule.setSourceIp(rule.getSourceIp());
		cloneRule.setSourceNtObj(rule.getSourceNtObj());
		cloneRule.setSourceType(rule.getSourceType());
		cloneRule.setSourceUp(rule.getSourceUp());
		return cloneRule;
	}
	
	private String turnIpNetmask(String ipMask){
		try{
			int index = ipMask.indexOf("/");
			String ip = ipMask.substring(0, index);
			int mask = Integer.valueOf(ipMask.substring(index+1));
			long maskLong = (long)(Math.pow(2,32) - Math.pow(2,(32-mask)));
			String maskStr = AhDecoder.long2Ip(maskLong);
			return ip + " " + maskStr;
		}catch(Exception ex){
			return null;
		}
	}

	public boolean isConfigNetFirewall() {
		return hiveAp.isBranchRouter() && ruleList != null && !ruleList.isEmpty();
	}

	public int getNetFirewallRullSize() {
		return ruleList.size();
	}

	public String getNetFirewallRullName(int index) {
		return ruleList.get(index).getName();
	}

	public int getNetFirewallRullPosition(int index) {
		return ruleList.get(index).getPosition();
	}

	public String getNetFirewallRullFrom(int index) {
		return ruleList.get(index).getFrom();
	}

	public String getNetFirewallRullTo(int index) {
		return ruleList.get(index).getTo();
	}

	public String getNetFirewallRullService(int index) {
		return ruleList.get(index).getService();
	}

	public String getNetFirewallRullAction(int index) {
		return ruleList.get(index).getAction();
	}

	public String getNetFirewallRullLogging(int index) {
		return ruleList.get(index).getLogging();
	}
	
	public static class FirewallRule{
		
		private String name;

		private String from;
		
		private String to;
		
		private String service;
		
		private String action;
		
		private String logging;
		
		private int position;
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		public String getService() {
			return service;
		}

		public void setService(String service) {
			this.service = service;
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public String getLogging() {
			return logging;
		}

		public void setLogging(String logging) {
			this.logging = logging;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}
	}

}
