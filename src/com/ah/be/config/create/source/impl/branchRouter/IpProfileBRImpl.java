package com.ah.be.config.create.source.impl.branchRouter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.ah.be.config.create.source.InterfaceProfileInt;
import com.ah.be.config.create.source.impl.ConfigureProfileFunction;
import com.ah.be.config.create.source.impl.baseImpl.IpProfileBaseImpl;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.util.MgrUtil;

public class IpProfileBRImpl extends IpProfileBaseImpl {
	
	private HiveAp hiveAp;
	private MgmtServiceOption mgmtService;
	private InterfaceProfileInt interfaceProfileInt;

	public IpProfileBRImpl(HiveAp hiveAp, ConfigureProfileFunction function){
		super(hiveAp);
		this.hiveAp = hiveAp;
		mgmtService=hiveAp.getConfigTemplate().getMgmtServiceOption();
		if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER){
			if(hiveAp.isSwitchProduct()){
				Map<Long, DeviceInterface> switchDInfs=hiveAp.getDeviceInterfaces();
				for(DeviceInterface dInf:switchDInfs.values()){
					if (hiveAp.getRole(dInf) == AhInterface.ROLE_WAN 
							&& dInf.getConnectionType().equals("2") 
							&& dInf.getGateway()!= null 
							&& !dInf.getGateway().isEmpty() ) {
							ipNetList.add("0.0.0.0 0.0.0.0 gateway " + dInf.getGateway());
					}
				}
			}else{
				Collection<DeviceInterface> dInfArray = new ArrayList<DeviceInterface>();
				dInfArray.add(hiveAp.getEth0Interface());
				dInfArray.add(hiveAp.getEth1Interface());
				dInfArray.add(hiveAp.getEth2Interface());
				dInfArray.add(hiveAp.getEth3Interface());
				dInfArray.add(hiveAp.getEth4Interface());
				dInfArray.add(hiveAp.getUSBInterface());
//				dInfArray.add(hiveAp.getWifi0Interface());
				
				for(DeviceInterface inf:dInfArray){
					if (hiveAp.getRole(inf) == AhInterface.ROLE_WAN 
							&& inf.getConnectionType().equals("2") 
							&& inf.getGateway()!= null 
							&& !inf.getGateway().isEmpty() ) {
							ipNetList.add("0.0.0.0 0.0.0.0 gateway " + inf.getGateway());
					}
			}
			
		}
		}else if(this.isConfigureIp()){
			ipNetList.add("0.0.0.0 0.0.0.0 gateway " + hiveAp.getEth0Interface().getGateway());
		}
		this.interfaceProfileInt = function.getInterfaceProfileImpl();
	}
	
	public String getHiveApGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.configuration");
	}
	
	public String getHiveApName(){
		return hiveAp.getHostName();
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}
	
	private boolean isConfigureIp(){
		return hiveAp.getEth0Interface() != null && !hiveAp.getEth0Interface().isEnableDhcp() && 
				!hiveAp.isPppoeEnableCurrent() && hiveAp.getEth0Interface().getGateway() != null && 
				!"".equals(hiveAp.getEth0Interface().getGateway());
	}
	
	public boolean isConfigPathAndTcpMss() {
		return hiveAp.isEnableOverrideBrPMTUD() || mgmtService != null;
	}
	
	public boolean isIpPathMtuDiscoveryEnable() {
		if(hiveAp.isEnableOverrideBrPMTUD()){
			return hiveAp.isEnableBrPMTUD();
		}else{
			return mgmtService.isEnablePMTUD();
		}
	}

	public boolean isIpTcpMssThresholdEnable() {
		if(hiveAp.isEnableOverrideBrPMTUD()){
			return hiveAp.isMonitorBrMSS();
		}else{
			return mgmtService.isMonitorMSS();
		}
	}

	public boolean isConfigThresholdSize() {
		if(hiveAp.isEnableOverrideBrPMTUD()){
			return hiveAp.getThresholdBrForAllTCP() > 0;
		}else{
			return mgmtService.getThresholdForAllTCP() > 0;	
		}
	}

	public int getThresholdSize() {
		if(hiveAp.isEnableOverrideBrPMTUD()){
			return hiveAp.getThresholdBrForAllTCP();
		}else{
			return mgmtService.getThresholdForAllTCP();
		}
	}

	public boolean isConfigL3VpnThresholdSize() {
		if(hiveAp.isEnableOverrideBrPMTUD()){
			return hiveAp.getThresholdBrThroughVPNTunnel() > 0;
		}else{
			return mgmtService.getThresholdThroughVPNTunnel() > 0;	
		}
	}
	
	public int getL3VpnThresholdSize() {
		if(hiveAp.isEnableOverrideBrPMTUD()){
			return hiveAp.getThresholdBrThroughVPNTunnel();
		}else{
			return mgmtService.getThresholdThroughVPNTunnel();
		}
		
	}
	
	public boolean isConfigNatPolicy() {
		return true;
	}
	
	@Override
	public int getNatPolicySize() {
		return interfaceProfileInt.getNatPolicySize();
	}

	@Override
	public String getNatPolicyName(int index) {
		return interfaceProfileInt.getNatPolicyName(index);
	}

	@Override
	public boolean isNatPolicyConfigMatch(int index) {
		return interfaceProfileInt.isNatPolicyConfigMatch(index);
	}

	@Override
	public boolean isNatPolicyConfigVirtualHost(int index) {
		return interfaceProfileInt.isNatPolicyConfigVirtualHost(index);
	}

	@Override
	public String getNatPolicyMatchInsideValue(int index) {
		return interfaceProfileInt.getNatPolicyMatchInsideValue(index);
	}

	@Override
	public String getNatPolicyMatchOutsideValue(int index) {
		return interfaceProfileInt.getNatPolicyMatchOutsideValue(index);
	}
	
	@Override
	public String getNatPolicyVhostInsideHostValue(int index) {
		return interfaceProfileInt.getNatPolicyVhostInsideHostValue(index);
	}

	@Override
	public String getNatPolicyVhostInsidePortValue(int index) {
		return interfaceProfileInt.getNatPolicyVhostInsidePortValue(index);
	}

	@Override
	public String getNatPolicyVhostOutsidePortValue(int index) {
		return interfaceProfileInt.getNatPolicyVhostOutsidePortValue(index);
	}

	@Override
	public String getNatPolicyVhostProtocolValue(int index) {
		return interfaceProfileInt.getNatPolicyVhostProtocolValue(index);
	}
}
