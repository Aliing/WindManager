package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.ConsoleProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.util.MgrUtil;

/**
 * @author zhang
 * @version 2008-4-10 10:59:18
 */

public class ConsoleProfileImpl implements ConsoleProfileInt {
	
	private HiveAp hiveAp;
	private MgmtServiceOption mgmtService;
	
	public ConsoleProfileImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
		mgmtService = hiveAp.getConfigTemplate().getMgmtServiceOption();
	}
	
	public String getApVersion() {
		return hiveAp.getSoftVer();
	}
	
	public String getMgmtServiceGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.mgmtServiceOption");
	}
	
	public String getMgmtServiceName(){
		if(mgmtService != null){
			return mgmtService.getMgmtName();
		}else{
			return null;
		}
	}

	public boolean isConfigConsoleTree(){
		return mgmtService != null;
//		if(CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(this.getApVersion())){
//			return false;
//		}else if(CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(this.getApVersion())){
//			return mgmtService != null;
//		}else{
//			return false;
//		}
	}
	
	public boolean isEnableSerialPort(){
		return !mgmtService.getDisableConsolePort();
	}
}
