package com.ah.bo.network;
/**
 * 
 * Description: application data type object
 * ApplicationDTO.java Create on Jul 30, 2013 2:33:02 AM
 * @author Shaohua Zhou
 * @version 1.0
 * Copyright (c) 2013 Aerohive Networks Inc. All Rights Reserved.
 */
public class ApplicationDTO {
	
	private Long id;
	
	private String appName;
	
	private String description;
	
	private String appGroupName;
	
	private Integer appCode; 
	
	private Long lastDayUsage = 0L;
	
	private Long lastMonthUsage = 0L; 
	
	private String lastDayUsageStr;
	
	private String lastMonthUsageStr; 
	
	private int appType; //0: system, 1: custom

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAppGroupName() {
		return appGroupName;
	}

	public void setAppGroupName(String appGroupName) {
		this.appGroupName = appGroupName;
	}

	public Integer getAppCode() {
		return appCode;
	}

	public void setAppCode(Integer appCode) {
		this.appCode = appCode;
	}

	public Long getLastDayUsage() {
		return lastDayUsage;
	}

	public void setLastDayUsage(Long lastDayUsage) {
		this.lastDayUsage = lastDayUsage;
	}

	public Long getLastMonthUsage() {
		return lastMonthUsage;
	}

	public void setLastMonthUsage(Long lastMonthUsage) {
		this.lastMonthUsage = lastMonthUsage;
	}

	public String getLastDayUsageStr() {
		return lastDayUsageStr;
	}

	public void setLastDayUsageStr(String lastDayUsageStr) {
		this.lastDayUsageStr = lastDayUsageStr;
	}

	public String getLastMonthUsageStr() {
		return lastMonthUsageStr;
	}

	public void setLastMonthUsageStr(String lastMonthUsageStr) {
		this.lastMonthUsageStr = lastMonthUsageStr;
	}

	public int getAppType() {
		return appType;
	}

	public void setAppType(int appType) {
		this.appType = appType;
	}
	
	
}
