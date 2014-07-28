package com.ah.ui.actions.tools;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.ah.be.common.ConfigUtil;
import com.ah.be.common.cache.ClientInfoBean;
import com.ah.be.common.cache.ReportCacheMgmt;
import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.be.performance.BeClientSessionProcessor;
import com.ah.be.performance.appreport.BeApplicationFlowGatherProcessor;
import com.ah.be.performance.appreport.BeCopServerSendDataProcessor;
import com.ah.be.performance.messagehandle.timer.TimerQueryApplicationFlow;
import com.ah.be.search.FilterParam;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.Application;
import com.ah.bo.network.ApplicationProfile;
import com.ah.ui.actions.BaseAction;

public class SchedulerDebugAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	
	private String clientMac;
	
	private boolean debugSwitch = false;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		if (StringUtils.isBlank(operation)) {
			return init();
		}	
		try {
			Method method = getClass().getDeclaredMethod(operation);
			return (String) method.invoke(this);
		} catch (Exception e) {
			reportActionError(e);
			return init();
		}
	} 
	
	public String init() throws Exception {
		String schedulerDebug = ConfigUtil.getConfigInfo("performance", "scheduler_debug", "0"); 
		if ("1".equals(schedulerDebug)) {
			debugSwitch = true;
		}
		return INPUT;
	}
	
	public String appFlowSendRequest() throws Exception {
		TimerQueryApplicationFlow appFlow = new TimerQueryApplicationFlow();
		appFlow.setDebugFlag(true);
		Thread thread = new Thread(appFlow);
		thread.start();
		return init();
	}
	
	public String appFlowRollUp() throws Exception {
		BeApplicationFlowGatherProcessor processor = new BeApplicationFlowGatherProcessor();
		processor.startTaskForDebug();
		return init();
	}
	
	public String openLogDoubtfulClientInfo() throws Exception {
		BeClientSessionProcessor.logDoubtfulClientInfo = true;
		return init();
	}
	
	public String closeLogDoubtfulClientInfo() throws Exception {
		BeClientSessionProcessor.logDoubtfulClientInfo = false;
		return init();
	}
	
	public String sendDataToCopServer() throws Exception {
		BeCopServerSendDataProcessor processor = new BeCopServerSendDataProcessor();
		processor.startTaskForDebug();
		return init();
	}
	
	public String mockClientInfoData() throws Exception {
		List<ClientInfoBean> list = new ArrayList<ClientInfoBean>();
		for (int i = 0; i < 200; i++) {
			list.add(new ClientInfoBean("111111111111" + i, 2L, "username1", "profilename1", 1, "ssid1", "hostname1", "ios6", true, 0, 1));
		}
		
		//list.add(new ClientInfoBean("222222222222", 2L, "username2", "profilename2", 1, "ssid2", "hostname2", "ios7", true, 0, 1));
		for (ClientInfoBean bean : list) {
			ReportCacheMgmt.getInstance().saveClientInfo(bean.getClientMac(), bean);
		}
		return init();
	}
	
	public String findTop200ClientCache() throws Exception {
		List<ClientInfoBean> list = ReportCacheMgmt.getInstance().getClientInfoListForDebug(200);
		StringBuffer message = new StringBuffer();
		jsonObject = new JSONObject();
		if (list != null && list.size() > 0) {
			for (ClientInfoBean bean : list) {
				String single = "clientMac:" + bean.getClientMac() + "  userName:" + bean.getUserName() + "  hostName:" + bean.getHostName() 
						+ "  ssid:" + bean.getSsid() + "  profileName:" + bean.getProfileName() + "  osInfo:" + bean.getOsInfo()
						+ "  domainId:" + bean.getDomainId() + "  online:" + bean.isOnline() 
						+ "  timeout:" + bean.getTimeout() + "   datasource:" + bean.getDataSource();
				message.append(single + "\r\n\r\n");
			}
			
		}
		else {
			message.append("can not find client cache data");
		}
		jsonObject.put("message", message.toString());
		return "json";
	}
	
	public String findClientCache() throws Exception {
		ClientInfoBean bean = ReportCacheMgmt.getInstance().getClientInfoBean(clientMac , getDomain().getId());
		HmDomain hd = null;
		if(null != bean && null != bean.getDomainId()){
			hd = QueryUtil.findBoById(HmDomain.class, bean.getDomainId());
		}
		String message = null;
		jsonObject = new JSONObject();
		if (bean != null) {
			message = "userName:" + bean.getUserName() + "  hostName:" + bean.getHostName() + "  ssid:" + bean.getSsid()
					+ "  profileName:" + bean.getProfileName() + "  osInfo:" + bean.getOsInfo()
					+ "  domainName:" + (null == hd?  "" : hd.getDomainName()) + "  online:" + bean.isOnline() 
					+ "  timeout:" + bean.getTimeout() + "   datasource:" + bean.getDataSource();
		}
		else {
			message = "can not find client cache data";
		}
		jsonObject.put("message", message);
		return "json";
	}
	
	public String openWatchlistCleanNotification() throws Exception {
		HMServicesSettings serviceSetting = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner.id", userContext.getDomain().getId());
		serviceSetting.setNotifyCleanWatchList(false);
		serviceSetting.setNotifyUpdateWatchList(true);
		QueryUtil.updateBo(serviceSetting);
		
//		List<Application> fixedAppList = QueryUtil.executeQuery(Application.class, new SortParams("appName"), new FilterParams("appCode > :s1", new Object[] {0}));
//		fixedAppList = fixedAppList.subList(0, 8);
//		Set<Application> appList = new HashSet<>();
//		appList.addAll(fixedAppList);
//		ApplicationProfile profile = QueryUtil.findBoByAttribute(ApplicationProfile.class, "owner.id", userContext.getDomain().getId(), new ConfigLazyQueryBo());
//		if (profile == null) {
//			profile = new ApplicationProfile();
//			profile.setOwner(userContext.getDomain());
//			profile.setProfileName("default" + userContext.getDomain().getId());
//			profile.setApplicationList(appList);
//			QueryUtil.createBo(profile);
//		}
//		else {
//			profile.setApplicationList(appList);
//			QueryUtil.updateBo(profile);
//		}

		jsonObject = new JSONObject();
		jsonObject.put("message", "ready for display need clean watchlist notification.");
		return "json";
	}
	
	public String openWatchlistUpdateNotification() throws Exception {
		HMServicesSettings serviceSetting = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner.id", userContext.getDomain().getId());
		serviceSetting.setNotifyCleanWatchList(true);
		serviceSetting.setNotifyUpdateWatchList(false);
		QueryUtil.updateBo(serviceSetting);
		
		List<Application> fixedAppList = QueryUtil.executeQuery(Application.class, new SortParams("appName"), new FilterParams("appCode > :s1", new Object[] {0}));
		fixedAppList = fixedAppList.subList(0, 8);
		Set<Application> appList = new HashSet<>();
		appList.addAll(fixedAppList);
		ApplicationProfile profile = QueryUtil.findBoByAttribute(ApplicationProfile.class, "owner.id", userContext.getDomain().getId(), new ConfigLazyQueryBo());
		if (profile == null) {
			profile = new ApplicationProfile();
			profile.setOwner(userContext.getDomain());
			profile.setProfileName("default" + userContext.getDomain().getId());
			profile.setApplicationList(appList);
			QueryUtil.createBo(profile);
		}
		else {
			Set<Application> existsAppList = profile.getApplicationList();
			appList.addAll(existsAppList);
			profile.setApplicationList(appList);
			QueryUtil.updateBo(profile);
		}
		jsonObject = new JSONObject();
		jsonObject.put("message", "ready for display update watchlist notification.");
		return "json";
	}

	public String getClientMac() {
		return clientMac;
	}

	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}

	public boolean isDebugSwitch() {
		return debugSwitch;
	}

	public void setDebugSwitch(boolean debugSwitch) {
		this.debugSwitch = debugSwitch;
	}
	
	
}