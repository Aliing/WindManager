package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.useraccess.MgmtServiceOption;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.ResetButtonProfileInt;
import com.ah.util.MgrUtil;

/**
 * @author zhang
 * @version 2007-12-3 04:12:41
 */

public class ResetButtonProfileImpl implements ResetButtonProfileInt {
	
	private MgmtServiceOption mgmtServiceOption;
	private HiveAp hiveAp;

	public ResetButtonProfileImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
		mgmtServiceOption = hiveAp.getConfigTemplate().getMgmtServiceOption();
	}
	
	public String getMgmtServiceGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.mgmtServiceOption");
	}
	
	public String getMgmtServiceName(){
		if(mgmtServiceOption != null){
			return mgmtServiceOption.getMgmtName();
		}else{
			return null;
		}
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}
	
	public boolean isConfigureResetButton(){
		return mgmtServiceOption != null;
	}
	
	public String getUpdateTime() {
		List<Object> resetButtonList = new ArrayList<Object>();
		resetButtonList.add(mgmtServiceOption);
		return CLICommonFunc.getLastUpdateTime(resetButtonList);
	}
	
	public boolean isEnableResetConfig(){
		return !mgmtServiceOption.getDisableResetButton();
	}
}
