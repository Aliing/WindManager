package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.StromControlInt;
import com.ah.bo.hiveap.ConfigTemplateStormControl;
import com.ah.bo.hiveap.HiveAp;
import com.ah.xml.be.config.StormControlRateLimitModeValue;

public class StromControlImpl implements StromControlInt {
	
	private HiveAp hiveAp;

	public StromControlImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
	}
	
	public static StormControlRateLimitModeValue getStormControlMode(HiveAp hiveAp) {
		short mode;
		if(hiveAp.isEnableOverrideStormControl()){
			mode = hiveAp.getSwitchStormControlMode();
		}else {
			mode = hiveAp.getConfigTemplate().getSwitchStormControlMode();
		}
		switch(mode){
		case ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_BYTE:
			return StormControlRateLimitModeValue.BYTE_BASED;
		case ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_PACKET:
			return StormControlRateLimitModeValue.PACKET_BASED;
		default:
			return StormControlRateLimitModeValue.BYTE_BASED;
		}
	}
	
	public StormControlRateLimitModeValue getStormControlMode(){
		return getStormControlMode(this.hiveAp);
	}
	
}
