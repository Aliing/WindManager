package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mobility.TunnelSetting;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.SingleTableItem;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.source.MobilityPolicyProfileInt;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * 
 * @author zhang
 *
 */
public class MobilityPolicyProfileImpl implements MobilityPolicyProfileInt {
	
	private final HiveAp hiveAp;
	private final TunnelSetting tunnelSet;
	private final List<IpAddress> fromIpList;
	private static final Tracer log = new Tracer(MobilityPolicyProfileImpl.class
			.getSimpleName());
	
	public MobilityPolicyProfileImpl(TunnelSetting tunnelSet,HiveAp hiveAp){
		this.tunnelSet = tunnelSet;
		this.hiveAp = hiveAp;
		fromIpList = new ArrayList<IpAddress>(this.tunnelSet.getIpAddressList());
	}
	
	public boolean isConfigMobilityPolicy(){
		return hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP && tunnelSet != null;
	}
	
	public String getMobilityPolicyGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.identityBasedTunnels");
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}

	public String getUpdateTime(){
//		List<Object> mobPolicyTime = new ArrayList<Object>();
//		mobPolicyTime.add(hiveAp);
//		mobPolicyTime.add(tunnelSet);
//		if(tunnelSet != null){
//			mobPolicyTime.add(tunnelSet.getIpAddress());
//			if(tunnelSet.getIpAddressList() != null){
//				for(TunnelSettingIPAddress ipInfoObj : tunnelSet.getIpAddressList()){
//					if(ipInfoObj != null){
//						mobPolicyTime.add(ipInfoObj.getIpAddress());
//					}
//				}
//			}
//		}
//		return CLICommonFunc.getLastUpdateTime(mobPolicyTime);
		return CLICommonFunc.getLastUpdateTime(null);
	}
	
	public String getMobilityPolicyName(){
		return tunnelSet.getTunnelName();
	}
	
	public boolean isConfigureDnxp() throws CreateXMLException{
		return this.isConfigNomadicRoaming() || this.isConfigUnroamThreshold();
	}
	
	public boolean isConfigNomadicRoaming(){
		return tunnelSet.getEnableType() == TunnelSetting.TUNNELSETTING_DYNAMIC_TUNNELING;
	}
	
	public boolean isConfigureInxp() throws CreateXMLException{
		if(tunnelSet.getEnableType() == TunnelSetting.TUNNELSETTING_DYNAMIC_TUNNELING){
			return false;
		}
		boolean isConfigFrom = isMgtIpInFrom();
		boolean isConfigTo = isMgtIpInTo();
		String hiveIp = hiveAp.getCfgIpAddress();
		if(hiveIp == null || "".equals(hiveIp)){
			hiveIp = hiveAp.getIpAddress();
		}
//		if(isConfigFrom && isConfigTo){
//			String[] errParams = {tunnelSet.getTunnelName(), hiveIp};
//			String errMsg = NmsUtil.getUserMessage(
//					"error.be.config.create.tunnelPolicyContainIp_1", errParams);
//			log.error("isConfigureInxp", errMsg);
//			throw new CreateXMLException(errMsg);
//		}
		if(!isConfigFrom && !isConfigTo){
			String[] errParams = {tunnelSet.getTunnelName(), hiveIp};
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.tunnelPolicyContainIp_2", errParams);
			log.error("isConfigureInxp", errMsg);
			throw new CreateXMLException(errMsg);
		}
		return isConfigFrom || isConfigTo;
	}
	
	public boolean isMgtIpInFrom() throws CreateXMLException{
		//check ip address exists
		String hiveIp = hiveAp.getCfgIpAddress();
		if(hiveIp == null || "".equals(hiveIp)){
			hiveIp = hiveAp.getIpAddress();
		}
		if(hiveIp == null || "".equals(hiveIp)){
			String[] errParams = {hiveAp.getHostName()};
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.cannotFoundIp", errParams);
			log.error("isConfigureInxpFrom", errMsg);
			throw new CreateXMLException(errMsg);
		}
		
		long[][] ipArg = new long[tunnelSet.getIpAddressList().size()][2];
		int i=0;
		for(IpAddress ipAddr : tunnelSet.getIpAddressList()){
			SingleTableItem ipTableItem = CLICommonFunc.getIpAddress(ipAddr, hiveAp);
			String startIp = CLICommonFunc.getStartIpAddress(ipTableItem.getIpAddress(), ipTableItem.getNetmask());
			long startIndex = CLICommonFunc.getIpIndex(startIp);
			long endIndex = CLICommonFunc.getIpIndex("255.255.255.255") - CLICommonFunc.getIpIndex(ipTableItem.getNetmask()) + startIndex;
			ipArg[i][0] = startIndex;
			ipArg[i][1] = endIndex;
			i++;
		}
		
		long hiveApIndex = CLICommonFunc.getIpIndex(hiveIp);
		for (long[] anIpArg : ipArg) {
			if (hiveApIndex >= anIpArg[0] && hiveApIndex <= anIpArg[1]) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isMgtIpInTo() throws CreateXMLException{
		//check ip address exists
		String hiveIp = hiveAp.getCfgIpAddress();
		if(hiveIp == null || "".equals(hiveIp)){
			hiveIp = hiveAp.getIpAddress();
		}
		if(hiveIp == null || "".equals(hiveIp)){
			String[] errParams = {hiveAp.getHostName()};
			String errMsg = NmsUtil.getUserMessage(
					"error.be.config.create.cannotFoundIp", errParams);
			log.error("isConfigureInxpTo", errMsg);
			throw new CreateXMLException(errMsg);
		}
		
		long hiveApIpIndex = CLICommonFunc.getIpIndex(hiveIp);
		int tunnelToType = tunnelSet.getTunnelToType();
		if(tunnelToType == TunnelSetting.TUNNELSETTING_TUNNELTYPE_IPADDRESS){
			SingleTableItem ipTableItem = CLICommonFunc.getIpAddress(tunnelSet.getIpAddress(), hiveAp);
			return hiveIp.equals(ipTableItem.getIpAddress());
		}else if(tunnelToType == TunnelSetting.TUNNELSETTING_TUNNELTYPE_RANGEIP){
			long startIpIndex = CLICommonFunc.getIpIndex(tunnelSet.getIpRangeStart());
			long endIpIndex = CLICommonFunc.getIpIndex(tunnelSet.getIpRangeEnd());
			return hiveApIpIndex >= startIpIndex && hiveApIpIndex <= endIpIndex;
		}else{
			return false;
		}
	}
	
//	public boolean isEnableDnxpNomadicRoaming(){
//		return tunnelSet.isRoamingEnable();
//	}
	
	public int getMobilityPolicyFromSize(){
		return tunnelSet.getIpAddressList().size();
	}
	
	public String getMobInxpToAddress() throws CreateXMLException {
		int tunnelToType = tunnelSet.getTunnelToType() ;
		if(tunnelToType == TunnelSetting.TUNNELSETTING_TUNNELTYPE_IPADDRESS ){
			return CLICommonFunc.getIpAddress(tunnelSet.getIpAddress(), hiveAp).getIpAddress();
		}else if(tunnelToType == TunnelSetting.TUNNELSETTING_TUNNELTYPE_RANGEIP){
			return tunnelSet.getIpRangeStart() + " " + tunnelSet.getIpRangeEnd() ;
		}else {
			return "";
		}
	}
	
	public String getMobInxpToPassword(){
		return tunnelSet.getPassword();
	}
	
	public String getMobInxpFromAddress(int index) throws CreateXMLException{
		SingleTableItem ipItem = CLICommonFunc.getIpAddress(fromIpList.get(index), hiveAp);
		return ipItem.getIpAddress()+"/"+CLICommonFunc.turnNetMaskToNum(ipItem.getNetmask());
	}
	
	public String getMobInxpFromPassword(int index){
		return tunnelSet.getPassword();
	}
	
	public boolean isConfigUnroamThreshold() {
		return tunnelSet.getEnableType() == TunnelSetting.TUNNELSETTING_DYNAMIC_TUNNELING;
//		if(CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())){
//			return tunnelSet.getEnableType() == TunnelSetting.TUNNELSETTING_DYNAMIC_TUNNELING;
//		}else if(CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())){
//			return false;
//		}else{
//			return false;
//		}
	}
	
	public String getUnroamThresholdValue(){
		return tunnelSet.getUnroamingAgeout() + " " + tunnelSet.getUnroamingInterval();
	}

}