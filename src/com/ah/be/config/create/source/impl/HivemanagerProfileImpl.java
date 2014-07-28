package com.ah.be.config.create.source.impl;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.HivemanagerProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.MgrUtil;

/**
 * @author zhang
 * @version 2008-1-4 10:10:28
 */

public class HivemanagerProfileImpl implements HivemanagerProfileInt {
	
	private HiveAp hiveAp;
	
	public HivemanagerProfileImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}
	
	public String getHiveApGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.configuration");
	}
	
	public String getHiveApName(){
		return hiveAp.getHostName();
	}

	public String getUpdateTime(){
		return CLICommonFunc.getLastUpdateTime(null);
	}
	
	public String getHiverManagerIp(){
		return NmsUtil.getCapwapServer(hiveAp, true);
	}
}
