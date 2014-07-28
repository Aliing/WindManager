package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.PerformanceSentinelInt;
import com.ah.bo.hiveap.HiveAp;

/**
 * @author zhang
 * @version 2009-9-4 10:58:03
 */

public class PerformanceSentinelImpl implements PerformanceSentinelInt {
	
	private HiveAp hiveAp;
	
	public PerformanceSentinelImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
	}
	
	public int getSlaInterval(){
		return hiveAp.getConfigTemplate().getSlaInterval();
	}
}
