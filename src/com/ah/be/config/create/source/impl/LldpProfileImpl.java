package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.impl.baseImpl.LldpProfileBaseImpl;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.util.MgrUtil;

/**
 * @author zhang
 * @version 2008-10-16 09:48:16
 */

public class LldpProfileImpl extends LldpProfileBaseImpl {
	
	private LLDPCDPProfile LLDPProfile;
	
	public LldpProfileImpl(HiveAp hiveAp){
		LLDPProfile = hiveAp.getConfigTemplate().getLldpCdp();
	}
	
	public int getRepeatCount() {
		return LLDPProfile.getRepeatCount();
	}
	
	public int getDelayTime() {
		return LLDPProfile.getDelayTime();
	}
	
	public String getLLDPGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.lldpcdpProfiles");
	}
	
	public String getLLDPName(){
		if(LLDPProfile != null){
			return LLDPProfile.getProfileName();
		}else{
			return null;
		}
	}
	
	public boolean isConfigLldp(){
		return LLDPProfile != null;
	}
	
	public boolean isEnableLldp(){
		return LLDPProfile.isEnableLLDP();
	}
	
	public boolean isOverrideConfig() {
		return true;
	}
	
	public boolean isEnableCdp(){
		return LLDPProfile.isEnableCDP();
	}
	
	public int getHoldTime(){
		return LLDPProfile.getLldpHoldTime();
	}
	
	public int getTimer(){
		return LLDPProfile.getLldpTimer();
	}
	
	public int getLldpMaxEntries(){
		return LLDPProfile.getLldpMaxEntries();
	}
	
	public int getCdpMaxEntries(){
		return LLDPProfile.getCdpMaxEntries();
	}
	
	public boolean isConfigCdp(){
		return LLDPProfile.isEnableCDP();
	}
	
	public boolean isEnableReceiveOnly(){
		return LLDPProfile.isLldpReceiveOnly();
	}
	
	public int getLldpMaxPower(){
		return LLDPProfile.getLldpMaxPower();
	}

}
