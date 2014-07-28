package com.ah.be.config.cli.generate.impl;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.cli.generate.CLIConfig;
import com.ah.be.config.cli.generate.CLIGenResult;
import com.ah.be.config.cli.generate.AbstractAutoAdaptiveCLIGenerate;
import com.ah.be.config.cli.generate.CLIGenerateException;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.wlan.SsidProfile;

public class SecurityObjectImpl_Ssid extends AbstractAutoAdaptiveCLIGenerate {
	
	private SsidProfile ssidObj;
	
	private String profileName;
	
	public SecurityObjectImpl_Ssid(SsidProfile ssidObj){
		this.ssidObj = ssidObj;
	}

	@Override
	public void init() throws CLIGenerateException {
		profileName = ssidObj.getSsid();
	}
	
	@Override
	public boolean isValid(){
		return hiveAp.getDeviceInfo().getIntegerValue(DeviceInfo.SPT_RADIO_COUNTS) > 0;
	}
	
	@CLIConfig
	public CLIGenResult getUserProfileDenyAction(){
		if(!ssidObj.getChkUserOnly()){
			return null;
		}
		
		Object[] resArgs = new Object[]{profileName, ssidObj.getDenyAction(), ssidObj.getChkDeauthenticate()}; 
		
		CLIGenResult resObj = new CLIGenResult();
		resObj.add(SECURITY_OBJECT_USER_PROFILE_DENY_ACTION_STRICT, resArgs);
		resObj.add(SSID_USER_PROFILE_DENY_ACTION_STRICT, resArgs);
		return resObj;
	}
	
	@CLIConfig
	public CLIGenResult getUserProfileDenyBanTime(){
		if(!ssidObj.getChkUserOnly() || ssidObj.getDenyAction() != SsidProfile.DENY_ACTION_BAN){
			return null;
		}
		
		Object[] resArgs = new Object[]{profileName, ssidObj.getActionTime()};
		
		CLIGenResult resObj = new CLIGenResult();
		resObj.add(SECURITY_OBJECT_USER_PROFILE_DENY_ACTION_BAN_TIME, resArgs);
		resObj.add(SSID_USER_PROFILE_DENY_ACTION_BAN_TIME, resArgs);
		return resObj;
	}
	
	@CLIConfig(version=">=6.1.6.0")
	public CLIGenResult getCloudCWPCli(){
		if(!ssidObj.isEnabledSocialLogin()){
			return null;
		}
		
		String apiKey = hiveAp.getDownloadInfo().getAPIKey();
		String apiNonce = hiveAp.getDownloadInfo().getAPINonce();
		String url = hiveAp.getDownloadInfo().getRootURL();
		String customerId =  hiveAp.getDownloadInfo().getCustomerId();
		int serviceId = hiveAp.getDownloadInfo().getServiceId();
		
		CLIGenResult resObj = new CLIGenResult();
		resObj.add(SECURITY_OBJECT_CLOUD_CWP_ENABLE, new Object[]{true, profileName});
		if(!StringUtils.isEmpty(apiKey) && !StringUtils.isEmpty(apiNonce)){
			resObj.add(SECURITY_OBJECT_CLOUD_CWP_API_KEY_API_NONCE, new Object[]{profileName, apiKey, apiNonce});
		}
		if(!StringUtils.isEmpty(url)){
			resObj.add(SECURITY_OBJECT_CLOUD_CWP_URL_ROOT_PATH, new Object[]{profileName, url});
		}
		if(!StringUtils.isEmpty(customerId)){
			resObj.add(SECURITY_OBJECT_CLOUD_CWP_CUSTOMER_ID, new Object[]{profileName, customerId});
		}
		if(serviceId > 0){
			resObj.add(SECURITY_OBJECT_CLOUD_CWP_SERVICE_ID, new Object[]{profileName, serviceId});
		}
		//need enable alg dns automatically.
		resObj.add(ALG_PROTOCOL_ENABLE, new Object[]{true, "dns"});
		
		return resObj;
	}
	
	@CLIConfig(version=">=5.1.1.0")
	public CLIGenResult getWallGardenForCM(){
		if(!ssidObj.isEnabledCM()){
			return null;
		}
		
		CLIGenResult resObj = new CLIGenResult();
		String[] wallGardens = {
				"clients1.google.com",
				"clients2.google.com",
				"clients3.google.com",
				"clients4.google.com",
				"dl.google.com",
				"dl-ssl.google.com",
				"m.google.com",
				"tools.google.com",
				"pack.google.com",
				"storage.googleapis.com",
				"commondatastorage.googleapis.com",
				"accounts.youtube.com",
				"googleusercontent.com",
				"omahaproxy.appspot.com",
				"cros-omahaproxy.appspot.com",
				"gweb-gettingstartedguide.appspot.com",
				"safebrowsing-cache.google.com",
				"m.safebrowsing-cache.google.com",
				"safebrowsing.google.com"
		};
		
		for(String wallGarden : wallGardens){
			resObj.add(SECURITY_OBJECT_WALLED_GARDEN_HOSTNAME_SERVICE, new Object[]{profileName, wallGarden, "all"});
		}
		
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
		return NmsUtil.isVhmEnableIdm(hiveAp.getOwner().getId()) && 
				(ssidObj.isEnabledIDM() || 
						(ssidObj.getParentPpskSsid() != null && ssidObj.getParentPpskSsid().isEnabledIDM())
				);
	}

}
