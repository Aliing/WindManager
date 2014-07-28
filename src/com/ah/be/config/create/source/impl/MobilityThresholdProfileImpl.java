package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.hiveap.HiveAp;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.MobilityThresholdProfileInt;
import com.ah.util.MgrUtil;

/**
 * 
 * @author zhang
 *
 */
public class MobilityThresholdProfileImpl implements MobilityThresholdProfileInt {
	
	private HiveAp hiveAp;
	
	public MobilityThresholdProfileImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
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
	
	public String getUpdateTime(){
		List<Object> mobilityList = new ArrayList<Object>();
		mobilityList.add(hiveAp);
		return CLICommonFunc.getLastUpdateTime(mobilityList);
	}
	
	private short turnThresholdType(String type){
		if(MobilityThresholdProfileInt.ROAMING_THRESHOLD_HIGH.equals(type)){
			return HiveAp.TUNNEL_THRESHOLD_HIGH;
		}else if(MobilityThresholdProfileInt.ROAMING_THRESHOLD_MEDIUM.equals(type)){
			return HiveAp.TUNNEL_THRESHOLD_MEDIUM;
		}else{
			return HiveAp.TUNNEL_THRESHOLD_LOW;
		}
	}
	
	public boolean isConfigureThresholdType(String type){
		short tunnelThreshold = turnThresholdType(type);
		return tunnelThreshold == hiveAp.getTunnelThreshold();
	}

}
