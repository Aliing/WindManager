package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.CacProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.util.MgrUtil;

/**
 * @author zhang
 * @version 2008-8-7 10:25:22
 */

public class CacProfileImpl implements CacProfileInt {
	
	private MgmtServiceOption mgmtOpt;
	
	public CacProfileImpl(HiveAp hiveAp){
		mgmtOpt = hiveAp.getConfigTemplate().getMgmtServiceOption();
	}
	
	public String getMgmtServiceGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.mgmtServiceOption");
	}
	
	public String getMgmtServiceName(){
		if(mgmtOpt != null){
			return mgmtOpt.getMgmtName();
		}else{
			return null;
		}
	}

	public boolean isConfigCac(){
		return mgmtOpt != null;
	}
	
	public boolean isEnableCac(){
		return !mgmtOpt.getDisableCallAdmissionControl();
	}
	
	public int getCacAirtimePerSecond(){
		return mgmtOpt.getAirtimePerSecond();
	}
	
	public int getRoamingAirtime(){
		return mgmtOpt.getRoamingGuaranteedAirtime();
	}
}
