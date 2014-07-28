package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.ForwardingEngineInt;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.HiveAPVirtualConnection;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApMultipleVlan;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.util.MgrUtil;
import com.ah.xml.be.config.ForwardingActionValue;

/**
 * @author zhang
 * @version 2007-12-20 10:35:35
 */

public class ForwardingEngineImpl implements ForwardingEngineInt {
	
	private HiveAp hiveAp;
	private MgmtServiceOption serviceOption;
	private List<HiveAPVirtualConnection> rules;
	
	private List<String> multiVlanList;
	
	public ForwardingEngineImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
		serviceOption = hiveAp.getConfigTemplate().getMgmtServiceOption();
	}
	
	public String getMgmtServiceGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.mgmtServiceOption");
	}
	
	public String getMgmtServiceName(){
		if(serviceOption != null){
			return serviceOption.getMgmtName();
		}else{
			return null;
		}
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}
	
	public boolean isConfigForwardingEngine(){
		return serviceOption != null;
//		if(CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())){
//			return false;
//		}else if(CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())){
//			return serviceOption != null;
//		}else{
//			return false;
//		}
	}
	
	public String getUpdateTime(){
		List<Object> forwardList = new ArrayList<Object>();
		forwardList.add(serviceOption);
		return CLICommonFunc.getLastUpdateTime(forwardList);
	}
	
	public boolean isEnableInterSsidFlood(){
		return !serviceOption.getDisableSsid();
	}
	
	public boolean isEnableProxyArp(){
		return !serviceOption.getDisableProxyArp();
	}
	
	public int getIpSession(){
		return serviceOption.getForwardMaxIp();
	}
	
	public int getMacSession(){
		return serviceOption.getForwardMaxMac();
	}
	
	public boolean isConfigIpSession(){
		return serviceOption.isEnableForwardMaxIp();
	}
	
	public boolean isConfigMacSession(){
		return serviceOption.isEnableForwardMaxMac();
	}
	
	public boolean isEnableLogDroppedPackets(){
		return serviceOption.isLogDroppedPackets();
	}
	
	public boolean isEnableLogSessionsEnable(){
		return serviceOption.isLogFirstPackets();
	}
	
	public boolean isEnableDropFragmentedPackets(){
		return serviceOption.isDropFragmentedIpPackets();
	}
	
	public boolean isEnableDropManagementTraffic(){
		return serviceOption.isDropNonMgtTraffic();
	}
	
	public boolean isConfigTunnel(){
		return serviceOption != null;
	}
	
	public boolean isEnableTcpMss(){
		return serviceOption.isEnableTcpMss();
	}
	
	public boolean isConfigTcpMssThresholdSize(){
		return serviceOption.isEnableTcpMss() && serviceOption.getTcpMssThreshold() > 0;
	}
	
	public int getTcpMssThresholdSize(){
		return serviceOption.getTcpMssThreshold();
	}
	
	public boolean isConfigStaticRule(){
		int size = getStaticRuleSize();
		return size > 0;
	}
	
	public int getStaticRuleSize(){
		rules = hiveAp.getVirtualConnections();
		if(rules == null){
			return 0;
		}else{
			return rules.size();
		}
	}
	
	public String getStaticRuleName(int index){
		return rules.get(index).getForwardName();
	}
	
	public ForwardingActionValue getRuleActionValue(int index){
		byte forwardAction = rules.get(index).getForwardAction();
		if(forwardAction == HiveAPVirtualConnection.ACTION_PASS){
			return ForwardingActionValue.PASS;
		}else{
			return ForwardingActionValue.DROP;
		}
	}
	
	public String getRuleInifValue(int index){
		byte interface_in = rules.get(index).getInterface_in();
		if(interface_in == HiveAPVirtualConnection.INTERFACE_ETH0){
			return "eth0";
		}else if(interface_in == HiveAPVirtualConnection.INTERFACE_ETH1){
			return "eth1";
		}else if(interface_in == HiveAPVirtualConnection.INTERFACE_AGG0){
			return "agg0";
		}else if(interface_in == HiveAPVirtualConnection.INTERFACE_RED0){
			return "red0";
		}else if(interface_in == HiveAPVirtualConnection.INTERFACE_WIFI0){
			return "wifi0.1";
		}else if(interface_in == HiveAPVirtualConnection.INTERFACE_WIFI1){
			return "wifi1.1";
		}else{
			return "";
		}
	}
	
	public boolean isConfigRuleSrcOui(int index){
		String srcoui = rules.get(index).getSourceMac();
		if(srcoui != null && srcoui.length() == 6){
			return true;
		}else{
			return false;
		}
	}
	
	public String getRuleSrcValue(int index){
		String srcoui = rules.get(index).getSourceMac();
		if(srcoui == null || "".equals(srcoui)){
			return "";
		}else if(srcoui.contains("-")){
			return srcoui;
		}else{
			return CLICommonFunc.transFormMacAddrOrOui(srcoui);
		}
	}
	
	public String getRuleDstMacValue(int index){
		String dstMac = rules.get(index).getDestMac();
		if(dstMac == null || "".equals(dstMac)){
			return "";
		}else if(dstMac.contains("-")){
			return dstMac;
		}else{
			return CLICommonFunc.transFormMacAddrOrOui(dstMac);
		}
	}
	
	public String getRuleTxMacValue(int index){
		byte interface_in = rules.get(index).getInterface_in();
		if(interface_in == HiveAPVirtualConnection.INTERFACE_ETH0){
			return "";
		}else if(interface_in == HiveAPVirtualConnection.INTERFACE_ETH1){
			return "";
		}else if(interface_in == HiveAPVirtualConnection.INTERFACE_AGG0){
			return "";
		}else if(interface_in == HiveAPVirtualConnection.INTERFACE_RED0){
			return "";
		}
		
		String txMac = rules.get(index).getTxMac();
		if(txMac == null || "".equals(txMac)){
			return "";
		}else if(txMac.contains("-")){
			return txMac;
		}else{
			return CLICommonFunc.transFormMacAddrOrOui(txMac);
		}
	}
	
	public String getRuleOutIfValue(int index){
		byte outif = rules.get(index).getInterface_out();
		if(outif == HiveAPVirtualConnection.INTERFACE_ETH0){
			return "eth0";
		}else if(outif == HiveAPVirtualConnection.INTERFACE_ETH1){
			return "eth1";
		}else if(outif == HiveAPVirtualConnection.INTERFACE_AGG0){
			return "agg0";
		}else if(outif == HiveAPVirtualConnection.INTERFACE_RED0){
			return "red0";
		}else if(outif == HiveAPVirtualConnection.INTERFACE_WIFI0){
			return "wifi0.1";
		}else if(outif == HiveAPVirtualConnection.INTERFACE_WIFI1){
			return "wifi1.1";
		}else{
			return "";
		}
	}
	
	public String getRuleRxMacValue(int index){
		byte outif = rules.get(index).getInterface_out();
		if(outif == HiveAPVirtualConnection.INTERFACE_ETH0){
			return "";
		}else if(outif == HiveAPVirtualConnection.INTERFACE_ETH1){
			return "";
		}else if(outif == HiveAPVirtualConnection.INTERFACE_AGG0){
			return "";
		}else if(outif == HiveAPVirtualConnection.INTERFACE_RED0){
			return "";
		}
		
		String rxMac = rules.get(index).getRxMac();
		if(rxMac == null || "".equals(rxMac)){
			return "";
		}else if(rxMac.contains("-")){
			return rxMac;
		}else{
			return CLICommonFunc.transFormMacAddrOrOui(rxMac);
		}
	}
	
	public int getMultiNativeVlanSize(){
		if(multiVlanList == null){
			multiVlanList = new ArrayList<String>();
			if(hiveAp.getEthConfigType() == HiveAp.USE_ETHERNET_BOTH 
					&& hiveAp.getEth1().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL
					&& hiveAp.getMultipleVlan() != null){
				String strVlan = "";
				for(HiveApMultipleVlan vlan : hiveAp.getMultipleVlan()){
					if("".equals(strVlan)){
						strVlan += vlan.getVlanid();
					}else{
						strVlan += "," + vlan.getVlanid();
					}
				}
				if(!"".equals(strVlan)){
					strVlan = strVlan.replace(" ", "");
					strVlan = CLICommonFunc.mergeRange(strVlan);
					String[] argVlan = strVlan.split(",");
					for(int index=0; index<argVlan.length; index++){
						multiVlanList.add(argVlan[index]);
					}
				}
			}
		}
		return multiVlanList.size();
	}
	
	public String getMultiNativeVlanName(int index){
		return multiVlanList.get(index);
	}
	
	// waiting bo change
	public boolean isConfigForwardAllowAll(){
		return serviceOption.getMulticastselect() == MgmtServiceOption.MULTICAST_ALLOW;
	}
	
	public boolean isConfigForwardBlockAll(){
		return serviceOption.getMulticastselect() == MgmtServiceOption.MULTICAST_BLOCK;
	}
	
	public int getForwardExceptSize(){
		if(serviceOption.getMultipleVlan() == null){
			return 0;
		}else{
			return serviceOption.getMultipleVlan().size();
		}
	}
	
	public String getForwardExceptValue(int index){
		String ip = serviceOption.getMultipleVlan().get(index).getIp();
		String newmask = serviceOption.getMultipleVlan().get(index).getNetmask();
		if(newmask == null || "255.255.255.255".equals(newmask)){
			return ip;
		}else{
			ip = CLICommonFunc.countIpAndMask(ip, newmask);
			int intMask = CLICommonFunc.turnNetMaskToNum(newmask);
			return ip + "/" + String.valueOf(intMask);
		}
	}
	
	public boolean isConfigForwardsyncVlan(){
		return serviceOption.isEnableSyncVlanId();
	}
}
