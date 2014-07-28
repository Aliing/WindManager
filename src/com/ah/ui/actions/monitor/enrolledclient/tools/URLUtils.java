package com.ah.ui.actions.monitor.enrolledclient.tools;

import com.ah.be.common.ConfigUtil;

public class URLUtils {
	
	public static final String REST_URL = "/api/common/device/";
	
	public static final String REST_CM_HM_URL = "/api/hm/devices/";
	
	public static final String DEVICE_CLIENTS_ENROLLEDLIST_APPENDER = "enrollmentstatus";
	
	public static final String DEVICE_CLIENT_DETAIL_APPENDER = "detail";
	
	public static final String DEVICE_CLIENT_NETWORK_APPENDER = "networkinfo";
	
	public static final String DEVICE_CLIENT_SCANRESULT_APPENDER = "scanresults";
	
	public static final String DEVICE_CLIENT_CERT_APPENDER = "certs";
	
	public static final String DEVICE_CLIENT_PROFILE_APPENDER = "profiles";
	
	public static final String DEVICE_CLIENT_LOGS_APPENDER = "logs";

	public static final String DEVICE_CLIENTS_LIST_APPENDER = "list";
	
	public static final String DEVICE_DETAILS_INFO_APPENDER = "info/";
	
	public static final String DEVICE_APP_LIST_APPENDER = "appList/";
	
	public static final String DEVICE_CERT_LIST_APPENDER = "certList/";
	
	public static final String DEVICE_NETWORK_INFO_APPENDER = "networkInfo/";
	
	public static final String DEVICE_RESTRICTIONS_INFO_APPENDER = "restrictionsInfo/";
	
	public static final String DEVICE_SECURITY_INFO_APPENDER = "securityInfo/";
	
	public static final String DEVICE_OPERATION_VALUE_STR = "operate";
	
	public static final String ACTIVE_CLIENT_ENROLLED_APPENDER = "macaddress";
	
	public static final String REST_URL_ENTITLE_KEY = "/api/hm/entitlement";
	
	public static final String VIEW_CLIENT_PROFILE_APPENDER = "/configuration/profile/main/list";
	
	public static final String VIEW_CLIENT_LIST_APPENDER = "/monitor/device/main/list";
	
	public static String getViewClientProfileURL(){
		return  ConfigUtil.getACMConfigServerViewUrl() + VIEW_CLIENT_PROFILE_APPENDER;
	}
	
	public static String getViewClientListURL(){
		return ConfigUtil.getACMConfigServerViewUrl() + VIEW_CLIENT_LIST_APPENDER;
	}
	
	public static String getViewCMURL(){
		return ConfigUtil.getACMConfigServerViewUrl() + "/home";
	}
}
