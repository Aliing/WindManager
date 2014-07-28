package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.ApplicationProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.Application;
import com.ah.bo.network.ApplicationProfile;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.CustomApplicationRule;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.ApplicationUtil;
/**
 * @author llchen
 * @version 2012-09-24 9:36:43 AM
 */
@SuppressWarnings("static-access")
public class ApplicationProfileImpl implements ApplicationProfileInt {
	
	private HiveAp hiveAp;
	private boolean view;
	private List<String> applicationList = new ArrayList<String>();
	private List<CustomApplication> caList = new ArrayList<CustomApplication>();
	
	public ApplicationProfileImpl(HiveAp hiveAp, boolean view) throws CreateXMLException {
		this.hiveAp = hiveAp;
		this.view = view;
		loadApplicationList();
	}
	
	private void loadApplicationList() throws CreateXMLException{
		//ApplicationProfile appProfile = hiveAp.getConfigTemplate().getAppProfile();
		ApplicationProfile appProfile = MgrUtil.getQueryEntity().findBoByAttribute(ApplicationProfile.class, "owner.id", hiveAp.getOwner().getId(), new ConfigLazyQueryBo());
		caList = MgrUtil.getQueryEntity().executeQuery(CustomApplication.class, new SortParams("appCode"), 
				new FilterParams("deletedFlag = :s1", new Object[] {false}), hiveAp.getOwner().getId(), new ConfigLazyQueryBo());
		if (appProfile == null) {
			return;
		}
		
		int maxAppId = ApplicationUtil.getMaxSupportedAppCode(hiveAp);	
		List<Integer> appIdList = new ArrayList<Integer>();
		for (Application app : appProfile.getApplicationList()) {
			if (app.getAppCode().intValue() > maxAppId) {
				continue;
			}
			appIdList.add(app.getAppCode());
		}
		for (CustomApplication customApp : appProfile.getCustomApplicationList()) {
			appIdList.add(customApp.getAppCode());
		}
		
		if (appIdList.size() > ApplicationUtil.getWatchlistLimitation()) {
			if (NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.2.0") >= 0){
				String errorMsg;
				if (view) {
					errorMsg = NmsUtil.getUserMessage("error.config.L7.watchlist.limitation.view", new String[]{"reportSetting.action"});
				} else {
					errorMsg = NmsUtil.getUserMessage("error.config.L7.watchlist.limitation");
				}
				throw new CreateXMLException(errorMsg);
			}
		}
		Collections.sort(appIdList);

		StringBuffer bufAppId = new StringBuffer();		
		for(Integer appCode : appIdList){
			bufAppId.append(appCode).append(DEFAULT_APPID_SEPARATOR);
			if(bufAppId.length() > MAX_APPID_LENGTH){
				String appid = appCode + DEFAULT_APPID_SEPARATOR;
				applicationList.add(bufAppId.substring(0, bufAppId.lastIndexOf(appid)-1));
				bufAppId = new StringBuffer();
				bufAppId.append(appCode).append(DEFAULT_APPID_SEPARATOR);
			}
		}
		if(bufAppId.lastIndexOf(DEFAULT_APPID_SEPARATOR) > 0){
			applicationList.add(bufAppId.substring(0, bufAppId.lastIndexOf(DEFAULT_APPID_SEPARATOR)));
		}	
	}
	
	public boolean isEnableL7Switch() {
		return hiveAp.getConfigTemplate().isEnableL7Switch();
	}
	
	public String getApplicationGuiName() {
		return MgrUtil.getUserMessage("config.upload.debug.appProfile");
	}
	
	public String getApplicationName() {
		return hiveAp.getConfigTemplate().getConfigName();
	}
	
	public boolean isConfigApplicationReporting() {
		return !applicationList.isEmpty();
	}
	
	public int getReportingAppSize() {
		return applicationList.size();
	}
	
	public String getReportAppId(int index) {
		return applicationList.get(index);
	}
	
	public int getCustomAppSize() {
		return caList.size();
	}

	public String getCustomAppCode(int index) {
		return String.valueOf(caList.get(index).getAppCode());
	}

	public String getCustomAppName(int index) {
		return caList.get(index).getCustomAppShortName();
	}

	public int getCustomAppRuleSize(int appIndex) {
		List<CustomApplicationRule> rules = caList.get(appIndex).getRules();
		if (rules == null) {
			return 0;
		}
		return rules.size();
		
	}

	public String getCustomAppRuleValue(int appIndex, int ruleIndex) {
		CustomApplicationRule rule = caList.get(appIndex).getRules().get(ruleIndex);
		String ruleValue = "";
		if (rule.getDetectionType() == CustomApplicationRule.DETECTION_TYPE_PORT) {
			ruleValue = ":" + rule.getPortNumber();
		}
		else if (rule.getDetectionType() == CustomApplicationRule.DETECTION_TYPE_IPADDRESS) {
			if (rule.getPortNumber() > 0) {
				ruleValue = rule.getRuleValue() + ":" + rule.getPortNumber();
			}
			else {
				ruleValue = rule.getRuleValue();
			}
		}
		else if (rule.getDetectionType() == CustomApplicationRule.DETECTION_TYPE_HOSTNAME) {
			ruleValue = "host=" + rule.getRuleValue();
		}
		ruleValue += " cdp-module " + rule.getCdpModule();
		return ruleValue;
	}	
	
	public static void main(String[] args) {
		List<Application> appIdList = new ArrayList<Application>();
		appIdList.add(new Application(110, "", "", "", "", null));
		appIdList.add(new Application(101, "", "", "", "", null));
		appIdList.add(new Application(102, "", "", "", "", null));
		appIdList.add(new Application(103, "", "", "", "", null));
		appIdList.add(new Application(104, "", "", "", "", null));
		appIdList.add(new Application(105, "", "", "", "", null));
		appIdList.add(new Application(106, "", "", "", "", null));
		appIdList.add(new Application(107, "", "", "", "", null));
		appIdList.add(new Application(108, "", "", "", "", null));
		appIdList.add(new Application(109, "", "", "", "", null));
		
		List<String> applicationList = new ArrayList<String>();
		StringBuffer bufAppId = new StringBuffer();
		for(Application application : appIdList){
			bufAppId.append(application.getAppCode()).append(DEFAULT_APPID_SEPARATOR);
			if(bufAppId.length() > MAX_APPID_LENGTH){
				String appid = application.getAppCode() + DEFAULT_APPID_SEPARATOR;
				applicationList.add(bufAppId.substring(0, bufAppId.lastIndexOf(appid)-1));
				bufAppId = new StringBuffer();
				bufAppId.append(application.getAppCode()).append(DEFAULT_APPID_SEPARATOR);
			}
		}
		if(bufAppId.lastIndexOf(DEFAULT_APPID_SEPARATOR) > 0){
			applicationList.add(bufAppId.substring(0, bufAppId.lastIndexOf(DEFAULT_APPID_SEPARATOR)));
		}
		for (int i = 0; i < applicationList.size(); i++) {
			System.out.println(applicationList.get(i));
		}
		
	}

	
}
