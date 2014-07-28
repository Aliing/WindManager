package com.ah.be.config.cli.generate.impl;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.cli.generate.CLIConfig;
import com.ah.be.config.cli.generate.CLIGenResult;
import com.ah.be.config.cli.generate.AbstractAutoAdaptiveCLIGenerate;
import com.ah.be.config.cli.generate.CLIGenerateException;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.wlan.SsidProfile;

public class SecurityObjectImpl_Port extends AbstractAutoAdaptiveCLIGenerate {
	
	private PortAccessProfile accessProfile;
	private String profileName;
	
	public SecurityObjectImpl_Port(PortAccessProfile accessProfile){
		this.accessProfile = accessProfile;
	}

	@Override
	public void init() throws CLIGenerateException {
		profileName = accessProfile.getName();
	}
	
	@CLIConfig
	public CLIGenResult getUserProfileDenyAction(){
		if(!accessProfile.isChkUserOnly()){
			return null;
		}
		
		Object[] resArgs = new Object[]{profileName, accessProfile.getDenyAction(), accessProfile.isChkDeauthenticate()};
		
		CLIGenResult resObj = new CLIGenResult();
		resObj.add(SECURITY_OBJECT_USER_PROFILE_DENY_ACTION_STRICT, resArgs);
		resObj.add(SSID_USER_PROFILE_DENY_ACTION_STRICT, resArgs);
		return resObj;
	}
	
	@CLIConfig
	public CLIGenResult getUserProfileDenyBanTime(){
		if(!accessProfile.isChkUserOnly() || accessProfile.getDenyAction() != SsidProfile.DENY_ACTION_BAN){
			return null;
		}
		
		Object[] resArgs = new Object[]{profileName, accessProfile.getActionTime()};
		
		CLIGenResult resObj = new CLIGenResult();
		resObj.add(SECURITY_OBJECT_USER_PROFILE_DENY_ACTION_BAN_TIME, resArgs);
		resObj.add(SSID_USER_PROFILE_DENY_ACTION_BAN_TIME, resArgs);
		return resObj;
	}
	
	@CLIConfig(SECURITY_OBJECT_SECURITY_AAA_RADIUS_SERVER_IDM)
	public CLIGenResult getIDMClientEnableClis() {
		CLIGenResult resObj = new CLIGenResult();
		boolean enabled = isIDMEnabled();
		resObj.add(new Object[]{enabled, profileName});
		return resObj;
	}
	
	
	/*******************************************************************************************************************/
	
	private boolean isIDMEnabled(){
		return NmsUtil.isVhmEnableIdm(hiveAp.getOwner().getId()) && accessProfile.isEnabledIDM();
	}

}
