package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.impl.baseImpl.IpProfileBaseImpl;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.MgrUtil;

/**
 * 
 * @author zhang
 *
 */
public class IpProfileImpl extends IpProfileBaseImpl {
	
	protected HiveAp hiveAp;

	public IpProfileImpl(HiveAp hiveAp){
		super(hiveAp);
		this.hiveAp = hiveAp;
		if(this.isConfigureIp()){
			ipNetList.add("0.0.0.0 0.0.0.0 gateway " + hiveAp.getCfgGateway());
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
		return (!hiveAp.isDhcp() && hiveAp.getCfgGateway() != null && !hiveAp.getCfgGateway().equals("") ) ||
			(hiveAp.isDhcp() && hiveAp.isDhcpFallback() && hiveAp.getCfgGateway() != null && !hiveAp.getCfgGateway().equals("") );
	}
	
	public boolean isConfigPathAndTcpMss() {
		return false;
	}

	public boolean isIpPathMtuDiscoveryEnable() {
		return false;
	}

	public boolean isIpTcpMssThresholdEnable() {
		return false;
	}
	
	public boolean isConfigThresholdSize() {
		return false;
	}

	public int getThresholdSize() {
		return 0;
	}

	public boolean isConfigL3VpnThresholdSize() {
		return false;
	}
	
	public int getL3VpnThresholdSize() {
		return 0;
	}

}
