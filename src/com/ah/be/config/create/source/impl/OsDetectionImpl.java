package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.OsDetectionInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.util.MgrUtil;

public class OsDetectionImpl implements OsDetectionInt {
	
	private MgmtServiceOption mgmtService;
	private HiveAp hiveAp;
	public OsDetectionImpl(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
		mgmtService=hiveAp.getConfigTemplate().getMgmtServiceOption();
	}
	
	public boolean isConfigOsDetection(){
		return mgmtService != null;
	}
	
	public boolean isEnableOsDetection() {
		return mgmtService.isEnableOsdetection();
	}

	public boolean isConfigOsDetectionMethod(int type) {
		if(type == MgmtServiceOption.OS_DETECTION_METHOD_DHCP){
			return type == mgmtService.getOsDetectionMethod();
		}else if(type == MgmtServiceOption.OS_DETECTION_METHOD_HTTP){
			return type == mgmtService.getOsDetectionMethod();
		}else if(type == MgmtServiceOption.OS_DETECTION_METHOD_BOTH){
			return type == mgmtService.getOsDetectionMethod();
		}else{
			return false;
		}
	}
	
	public String getHiveApGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.OSDETECTION");
	}
	
	public String getHiveApName(){
		return hiveAp.getHostName();
	}
}
