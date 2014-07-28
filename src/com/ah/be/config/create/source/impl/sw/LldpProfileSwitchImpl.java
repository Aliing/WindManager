package com.ah.be.config.create.source.impl.sw;

import com.ah.be.config.create.source.impl.baseImpl.LldpProfileBaseImpl;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.util.MgrUtil;

public class LldpProfileSwitchImpl extends LldpProfileBaseImpl {

	private LLDPCDPProfile LLDPProfile;
	
	private boolean isConfigLLDP;
	
	private HiveAp hiveAp;
	
	public LldpProfileSwitchImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
		LLDPProfile = hiveAp.getConfigTemplate().getLldpCdp();
		if (this.isConfigLLDPInNetWorkPolicy()) {
			isConfigLLDP = true;
		}
	}
	
	private boolean isConfigLLDPInNetWorkPolicy() {
		return (LLDPProfile != null) && (LLDPProfile.isEnableLLDPHostPorts() || LLDPProfile.isEnableLLDPNonHostPorts());
	}
	
//	private boolean isConfigLLDPInDevicePage() {
//		return hiveAp.isOverrideLldpCdp() && (LLDPProfile != null) && LLDPProfile.isEnableLLDP();
//	}
	
	public boolean isConfigLldp(){
		return isConfigLLDP;
	}
	
	public boolean isEnableLldp(){
		return isConfigLLDP;
	}
	
	public boolean isOverrideConfig() {
		return LLDPProfile != null;
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
	
	public int getLldpMaxPower(){
		return LLDPProfile.getLldpMaxPower();
	}
}
