package com.ah.be.config.create.source.impl.cvg;

import com.ah.be.config.create.source.impl.baseImpl.IpProfileBaseImpl;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.MgrUtil;

public class IpProfileCVGImpl extends IpProfileBaseImpl {

	private HiveAp hiveAp;

	public IpProfileCVGImpl(HiveAp hiveAp){
		super(hiveAp);
		this.hiveAp = hiveAp;
		if(this.isConfigureIp()){
			ipNetList.add("0.0.0.0 0.0.0.0 gateway " + hiveAp.getEth0Interface().getGateway());
		}
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
		String gatewayStr = hiveAp.getEth0Interface().getGateway();
		return gatewayStr != null && !"".equals(gatewayStr);
	}
	
	public boolean isConfigPathAndTcpMss() {
		return true;
	}

	public boolean isIpPathMtuDiscoveryEnable() {
		return hiveAp.isEnableCvgPMTUD();
	}

	public boolean isIpTcpMssThresholdEnable() {
		return hiveAp.isMonitorCvgMSS();
	}

	public boolean isConfigThresholdSize() {
		return hiveAp.getThresholdCvgForAllTCP() > 0;
	}

	public int getThresholdSize() {
		return hiveAp.getThresholdCvgForAllTCP();
	}

	public boolean isConfigL3VpnThresholdSize() {
		return hiveAp.getThresholdCvgThroughVPNTunnel() > 0;
	}
	
	public int getL3VpnThresholdSize() {
		return hiveAp.getThresholdCvgThroughVPNTunnel();
	}

}
