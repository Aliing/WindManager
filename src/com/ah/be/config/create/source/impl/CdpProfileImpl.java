package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.CdpProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.LLDPCDPProfile;

public class CdpProfileImpl implements CdpProfileInt {
	
	private LLDPCDPProfile profile;
	
	private boolean isConfigCDP;
	
	private HiveAp hiveAp;
	
	public CdpProfileImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
		profile = hiveAp.getConfigTemplate().getLldpCdp();
		if (this.isConfigCDPInNetWorkPolicy()) {
			isConfigCDP = true;
		}
	}
	
	private boolean isConfigCDPInNetWorkPolicy() {
		return (profile != null) && (profile.isEnableCDPHostPorts() || profile.isEnableCDPNonHostPorts());
	}
	
//	private boolean isConfigCDPInDevicePage() {
//		return hiveAp.isOverrideLldpCdp() && (profile != null) && profile.isEnableCDP();
//	}
	
	public boolean isConfigCdp() {
		return isConfigCDP;
	}
	
	public boolean isEnableCdp(){
		return isConfigCDP;
	}
	
	public boolean isOverrideConfig() {
		return profile != null;
	}
	
	public int getCdpMaxEntries(){
		return profile.getCdpMaxEntries();
	}

}
