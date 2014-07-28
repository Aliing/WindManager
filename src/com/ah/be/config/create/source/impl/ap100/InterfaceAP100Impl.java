package com.ah.be.config.create.source.impl.ap100;

import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.InterfaceProfileInt;
import com.ah.be.config.create.source.impl.InterfaceProfileImpl;
import com.ah.be.config.create.source.impl.branchRouter.InterfaceBRImpl;
import com.ah.bo.hiveap.HiveAp;
import com.ah.xml.be.config.EthDuplex;


public class InterfaceAP100Impl extends InterfaceProfileImpl {
	
	private InterfaceProfileInt lanImpl;
	
	public InterfaceAP100Impl(HiveAp hiveAp) throws Exception{
		super(hiveAp);
		lanImpl = new InterfaceBRImpl(hiveAp, true);
	}
	
	public boolean isConfigEthx(InterType ethType){
		return lanImpl.isConfigEthx(ethType);
	}
	
	public boolean isConfigInterBind(InterType type) {
		return lanImpl.isConfigInterBind(type);
	}
	
	public boolean isConfigInterManage(InterType type) {
		if(InterType.eth0.equals(type)){
			return super.isConfigInterManage(type);
		}else{
			return lanImpl.isConfigInterManage(type);
		}
	}
	
	public String getInterSpeed(InterType type) {
		return lanImpl.getInterSpeed(type);
	}
	
	public boolean isInterShutdown(InterType type){
		return lanImpl.isInterShutdown(type);
	}
	
	public boolean isConfigInterStationTraffic(InterType type) throws CreateXMLException {
		return lanImpl.isConfigInterStationTraffic(type);
	}
	
	public boolean isEnableInterStationTraffic(InterType type) {
		return lanImpl.isEnableInterStationTraffic(type);
	}
	
	public boolean isConfigEthAllowedVlan(InterType type){
		return lanImpl.isConfigEthAllowedVlan(type);
	}
	
	public boolean isConfigEthSecurity(InterType type){
		return lanImpl.isConfigEthSecurity(type);
	}
	
	public String getEthSecurityObjName(InterType type){
		return lanImpl.getEthSecurityObjName(type);
	}
	
	public boolean isConfigEthNativeVlan(InterType type){
		return lanImpl.isConfigEthNativeVlan(type);
	}
	
	public int getEthNativeVlan(InterType type) throws CreateXMLException{
		return lanImpl.getEthNativeVlan(type);
	}
	
	public boolean isConfigInterMode(InterfaceMode mode, InterType type)
			throws CreateXMLException {
		if(InterType.eth0.equals(type)){
			return super.isConfigInterMode(mode, type);
		}else{
			return lanImpl.isConfigInterMode(mode, type);
		}
	}
	
	public boolean isEnableInterManage(ManageType type, InterType type1) {
		if(InterType.eth0.equals(type1)){
			return super.isEnableInterManage(type, type1);
		}else{
			return lanImpl.isEnableInterManage(type, type1);
		}
	}
	
	public EthDuplex getInterDuplex(InterType type) {
		return lanImpl.getInterDuplex(type);
	}
	
	public boolean isConfigAllowedVlanAll(InterType type){
		return lanImpl.isConfigAllowedVlanAll(type);
	}
	
	public boolean isConfigAllowedVlanAuto(InterType type){
		return lanImpl.isConfigAllowedVlanAuto(type);
	}
	
	public boolean isConfigAllowedVlanNum(InterType type){
		return lanImpl.isConfigAllowedVlanNum(type);
	}
	
	public String getAllowedVlanStr(InterType type, int i){
		return lanImpl.getAllowedVlanStr(type, i);
	}
	
	public int getInterAccessUserProfileAttr(InterType type) {
		return lanImpl.getInterAccessUserProfileAttr(type);
	}
	
	public boolean isConfigBridgeUserProfile(InterType type){
		return lanImpl.isConfigBridgeUserProfile(type);
	}
	
	public boolean isEnableInterMacLearning(InterType type) {
		return lanImpl.isEnableInterMacLearning(type);
	}
	
	public int getInterMacLearningStaticSize(InterType type) {
		return lanImpl.getInterMacLearningStaticSize(type);
	}
	
	public int getAllowedVlanSize(InterType type) {
		return lanImpl.getAllowedVlanSize(type);
	}

}
