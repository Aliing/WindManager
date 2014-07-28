package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.KddrInt;
import com.ah.bo.hiveap.HiveAp;

public class KddrImpl implements KddrInt {
	
	private HiveAp hiveAp;

	public KddrImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
	}
	
	public boolean isKddrEnable() {
		return hiveAp.getConfigTemplate().isEnableKddr();
	}
	
}
