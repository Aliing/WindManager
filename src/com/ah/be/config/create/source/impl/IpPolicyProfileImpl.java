package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.IpPolicyProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.IpPolicy;
import com.ah.bo.network.IpPolicyRule;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.SingleTableItem;
import com.ah.util.MgrUtil;

/**
 * 
 * @author zhang
 *
 */
public class IpPolicyProfileImpl implements IpPolicyProfileInt {
	
	private final IpPolicy ipPolicy;
	private final HiveAp hiveAp;
	
	public static final String IP_POLICY_FOR_VPN_AUTO = "_for_vpn";
	
	public static final String IP_POLICY_FOR_PPSK_AUTO = "_for_ppsk";
	public static final String IP_POLICY_FOR_WPA_AUTO = "_for_wpa";
	public static final String IP_POLICY_FOR_PPSK_AUTO_BR = "ip-policy4ppsk-server-from-dhcp";
	
	private List<NetworkService> serviceList;
	private List<CustomApplication> customAppServiceList;

	public IpPolicyProfileImpl(HiveAp hiveAp, IpPolicy ipPolicy) {
		this.hiveAp = hiveAp;
		this.ipPolicy = ipPolicy;
	}
	
	public boolean isConfigIpPolicy(){
		if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
			return ipPolicy != null;
		}else{
			return ipPolicy != null && ipPolicy.isAutoGenerate();
		}
	}
	
	public String getIpPolicyGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.ipPolicies");
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}
	
	public List<NetworkService> getServiceList(){
		if(serviceList == null){
			serviceList = new ArrayList<NetworkService>();
		}
		for(IpPolicyRule policyRule : ipPolicy.getRules()){
			if (policyRule.getServiceType() == IpPolicyRule.RULE_NETWORKSERVICE_TYPE) {
				serviceList.add(policyRule.getNetworkService());
			}
		}
		return serviceList;
	}
	
	public List<CustomApplication> getCustomAppServiceList() {
		if (customAppServiceList == null) {
			customAppServiceList = new ArrayList<CustomApplication>();
		}
		for(IpPolicyRule policyRule : ipPolicy.getRules()){
			if (policyRule.getServiceType() == IpPolicyRule.RULE_CUSTOMSERVICE_TYPE && policyRule.getCustomApp() != null) {
				customAppServiceList.add(policyRule.getCustomApp());
			}
		}
		return customAppServiceList;
	}
	
	public String getIpPolicyName(){
		return ipPolicy.getPolicyName();
	}
	
//	public String getUpdateTime(){
//		List<Object> ipPolicyList = new ArrayList<Object>();
//		ipPolicyList.add(ipPolicy);
//		if(ipPolicy.getRules() != null){
//			for(IpPolicyRule ruleInfo : ipPolicy.getRules()){
//				if(ruleInfo != null){
//					ipPolicyList.add(ruleInfo.getSourceIp());
//					ipPolicyList.add(ruleInfo.getDesctinationIp());
//					ipPolicyList.add(ruleInfo.getNetworkService());
//				}
//			}
//		}
//		return String.valueOf(CLICommonFunc.getLastUpdateTime(ipPolicyList));
//	}
	
	public int getIpPolicyIdSize(){
		return ipPolicy.getRules().size();
	}
	
	public int getIpPolicyIdName(int index){
		index = getReverseIndex(index);
		return ipPolicy.getRules().get(index).getRuleId();
	}
	
	public String getPolicyFromValue(int index) throws Exception {
		index = getReverseIndex(index);
		if(ipPolicy.getRules().get(index).getSourceIp() == null){
			return "0.0.0.0 0.0.0.0";
		}else{
			SingleTableItem ipFrom = CLICommonFunc.getIpAddress(ipPolicy.getRules().get(index).getSourceIp(), hiveAp);
			String ipAddress = ConfigureProfileFunction.BR_AS_PPSK_SERVER_IP.equals(ipFrom.getIpAddress()) ? 
					hiveAp.getCfgIpAddress() : ipFrom.getIpAddress();
			String netmask = ipFrom.getNetmask();
			short ipType = ipPolicy.getRules().get(index).getSourceIp().getTypeFlag();
			if(ipType == IpAddress.TYPE_IP_NETWORK || ipType == IpAddress.TYPE_IP_WILDCARD){
				return CLICommonFunc.countIpAndMask(ipAddress, netmask) + " " + netmask;
			}else if(ipType == IpAddress.TYPE_IP_ADDRESS){
				return ipAddress + " 255.255.255.255";
			}else if(ipType == IpAddress.TYPE_HOST_NAME){
				return ipAddress;
			}else{
				return ipAddress;
			}
			
		}
	}
	
	public String getPolicyToValue(int index) throws Exception{
		index = getReverseIndex(index);
		if(ipPolicy.getRules().get(index).getDesctinationIp() == null){
			return "0.0.0.0 0.0.0.0";
		}else{
			IpAddress desctinationIp = ipPolicy.getRules().get(index).getDesctinationIp();
			SingleTableItem ipItem = CLICommonFunc.getIpAddress(desctinationIp, this.hiveAp);
			String ipAddress = ConfigureProfileFunction.BR_AS_PPSK_SERVER_IP.equals(ipItem.getIpAddress()) ? 
					hiveAp.getCfgIpAddress() : ipItem.getIpAddress();
			String netmask = ipItem.getNetmask();
			if(desctinationIp.getTypeFlag() == IpAddress.TYPE_HOST_NAME){
				return ipAddress;
			}else if(desctinationIp.getTypeFlag() == IpAddress.TYPE_IP_ADDRESS){
				return ipAddress + " 255.255.255.255";
			}else if(desctinationIp.getTypeFlag() == IpAddress.TYPE_IP_NETWORK 
					|| desctinationIp.getTypeFlag() == IpAddress.TYPE_IP_WILDCARD){
				return CLICommonFunc.countIpAndMask(ipAddress, netmask) + " " + netmask;
			}else{
				return ipAddress;
			}
		}
	}
	
	public String getPolicyServiceName(int index){
		index = getReverseIndex(index);
		if(ipPolicy.getRules().get(index).getNetworkService() == null){
			if (ipPolicy.getRules().get(index).getCustomApp() != null) {
				return ipPolicy.getRules().get(index).getCustomApp().getCustomAppShortName();
			}
			else {
				return "any";
			}
		}else{
			NetworkService serviceObj = ipPolicy.getRules().get(index).getNetworkService();
			return serviceObj.getServiceName();
		}
	}
	
	public boolean isConfigPolicyAction(int index, IpPolicyActionValue actionType){
		index = getReverseIndex(index);
		short action = ipPolicy.getRules().get(index).getFilterAction();
		if(actionType == IpPolicyActionValue.permit){
			return action == IpPolicyRule.POLICY_ACTION_PERMIT;
		}else if(actionType == IpPolicyActionValue.deny){
			return action == IpPolicyRule.POLICY_ACTION_DENY;
		}else if(actionType == IpPolicyActionValue.inter_station_traffic_drop){
			return action == IpPolicyRule.POLICY_ACTION_TRAFFIC_DROP;
		}else if(actionType == IpPolicyActionValue.nat){
			return action == IpPolicyRule.POLICY_ACTION_NAT;
		}else{
			return false;
		}
	}
	
	public boolean isConfigPolicyLog(int index) {
		index = getReverseIndex(index);
		return ipPolicy.getRules().get(index).getActionLog() != IpPolicyRule.POLICY_LOGGING_OFF;
	}
	
	public boolean isConfigPolicyLogValue(int index, IpPolicyLog logType){
		index = getReverseIndex(index);
		short logAction = ipPolicy.getRules().get(index).getActionLog();
		
		if(logType == IpPolicyLog.initiate_session){
			return logAction == IpPolicyRule.POLICY_LOGGING_INITIATE;
		}else if(logType == IpPolicyLog.terminate_session){
			return logAction == IpPolicyRule.POLICY_LOGGING_TERMINATE;
		}else if(logType == IpPolicyLog.packet_drop){
			return logAction == IpPolicyRule.POLICY_LOGGING_DROP;
		}else if(logType == IpPolicyLog.cr){
			return logAction == IpPolicyRule.POLICY_LOGGING_BOTH;
		}else{
			return false;
		}
	}
	
	public boolean isConfigBefore(int index){
		return index != 0;
	}
	
	public int getPolicyBeforeIdValue(int index){
		index = ipPolicy.getRules().size()-index;
		return ipPolicy.getRules().get(index).getRuleId();
	}
	
	public String getBeforeValue(int index){
		index = ipPolicy.getRules().size() - index;
		StringBuffer strB = new StringBuffer("");
		for(int i=0; i<index; i++){
			strB.append(" ");
		}
		return strB.toString();
	}
	
	private int getReverseIndex(int index){
		return ipPolicy.getRules().size() - index -1;
	}
	
}