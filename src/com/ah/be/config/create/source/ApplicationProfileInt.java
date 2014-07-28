package com.ah.be.config.create.source;

/**
 * @author llchen
 * @version 2012-09-25 9:36:43 AM
 */

public interface ApplicationProfileInt {
	
	public static final int MAX_APPID_LENGTH = 39;
	
	public static final String DEFAULT_APPID_SEPARATOR = ",";

	public String getApplicationGuiName();
	
	public String getApplicationName();
	
	public boolean isConfigApplicationReporting();
	
	public int getReportingAppSize();
	
	public String getReportAppId(int index);
	
	public boolean isEnableL7Switch();
	
	public int getCustomAppSize();
	
	public String getCustomAppCode(int index);
	
	public String getCustomAppName(int index);
	
	public int getCustomAppRuleSize(int appIndex);
	
	public String getCustomAppRuleValue(int appIndex, int ruleIndex);
	
}