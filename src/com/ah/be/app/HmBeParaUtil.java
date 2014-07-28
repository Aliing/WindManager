/**
 *@filename		HmBeParaUtil.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3  08:57:34
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.parameter.BeParaModule;
import com.ah.be.parameter.device.DevicePropertyManage;
import com.ah.be.watchdog.WatchDogImpl;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.wlan.RadioProfile;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class HmBeParaUtil
{

	public static int getWatchDogReportInterval()
	{
		if (AhAppContainer.HmBe == null
			|| AhAppContainer.HmBe.getBeParaModule() == null)
		{
			return WatchDogImpl.DefaultReportInterval;
		}
		else
		{
			return AhAppContainer.HmBe.getBeParaModule()
				.getWatchDogReportInterval();
		}
	}
	
	/**
	 * Insert default profile.
	 */
	public static void insertDefaultProfile()
	{
		AhAppContainer.HmBe.getBeParaModule().constructDefaultProfile();
	}
	
	/**
	 * Get default profile.
	 * @param boClass : the profile bo class
	 * @param parameter : except "defaultFlag = true", Map<fieldName, fieldValue>, set null when there is no other parameter
	 * @return -
	 */
	public static <T extends HmBo> T getDefaultProfile(Class<T> boClass, Map<String, Object> parameter) {
		try {
			boClass.getDeclaredField("defaultFlag");
		} catch (NoSuchFieldException e) {
			return null;
		}
		List<T> boList;
		if (null == parameter) {
			boList = QueryUtil.executeQuery(boClass, null, new FilterParams("defaultFlag", true), 1);
		} else {
			int size = parameter.size();
			int index = 1;
			int i = 0;
			StringBuilder where = new StringBuilder();
			Object[] values = new Object[size];
			for (String key : parameter.keySet()) {
				where.append(key);
				where.append(" = :s");
				where.append(index++);
				where.append(" AND ");
				values[i++] = parameter.get(key);
			}
			where.append("defaultFlag = true");
			boList = QueryUtil.executeQuery(boClass, null, new FilterParams(where.toString(),
					values));
		}
		return !boList.isEmpty() ? boList.get(0) : null;
	}
	
	/**
	 * Get profile.
	 * @param boClass : the profile bo class
	 * @param parameter :Map<fieldName, fieldValue>, set null when there is no other parameter
	 * @return -
	 */
	public static <T extends HmBo> T getProfile(Class<T> boClass, Map<String, Object> parameter) {

		List<T> boList;
		if (null == parameter) {
			boList = QueryUtil.executeQuery(boClass, null, null, 1);
		} else {
			int size = parameter.size();
			int index = 1;
			int i = 0;
			StringBuilder where = new StringBuilder();
			Object[] values = new Object[size];
			for (String key : parameter.keySet()) {
				where.append(key);
				where.append(" = :s");
				where.append(index++);
				values[i++] = parameter.get(key);
			}
			boList = QueryUtil.executeQuery(boClass, null, new FilterParams(where.toString(),
					values));
		}
		return !boList.isEmpty() ? boList.get(0) : null;
	}

	public static Long getDefaultProfileId(Class<? extends HmBo> boClass, Map<String, Object> parameter) {
		try {
			boClass.getDeclaredField("defaultFlag");
		} catch (NoSuchFieldException e) {
			return null;
		}
		List<?> boIds;
		if (null == parameter) {
			boIds = QueryUtil.executeQuery("select id from " + boClass.getSimpleName(), null, new FilterParams("defaultFlag", true), 1);
		} else {
			int size = parameter.size();
			int index = 1;
			int i = 0;
			StringBuilder where = new StringBuilder();
			Object[] values = new Object[size];
			for (String key : parameter.keySet()) {
				where.append(key);
				where.append(" = :s");
				where.append(index++);
				where.append(" AND ");
				values[i++] = parameter.get(key);
			}
			where.append("defaultFlag = true");
			boIds = QueryUtil.executeQuery("select id from " + boClass.getSimpleName(), null, new FilterParams(where.toString(), values), 1);
		}
		return !boIds.isEmpty() ? (Long) boIds.get(0) : null;
	}
	
	public static ConfigTemplate getDefaultTemplate() {
		return getDefaultProfile(ConfigTemplate.class, null);
	}

	public static RadioProfile getDefaultRadioAProfile() {
		Map<String, Object> term = new HashMap<String, Object>(1);
		term.put("radioMode", RadioProfile.RADIO_PROFILE_MODE_A);
		return getDefaultProfile(
				RadioProfile.class, term);
	}

	public static RadioProfile getDefaultRadioBGProfile() {
		Map<String, Object> term = new HashMap<String, Object>(1);
		term.put("radioMode", RadioProfile.RADIO_PROFILE_MODE_BG);
		return getDefaultProfile(
				RadioProfile.class, term);
	}

	public static RadioProfile getDefaultRadioNGProfile() {
		Map<String, Object> term = new HashMap<String, Object>(1);
		term.put("radioMode", RadioProfile.RADIO_PROFILE_MODE_NG);
		return getDefaultProfile(
				RadioProfile.class, term);
	}

	public static RadioProfile getDefaultRadioNAProfile() {
		Map<String, Object> term = new HashMap<String, Object>(1);
		term.put("radioMode", RadioProfile.RADIO_PROFILE_MODE_NA);
		return getDefaultProfile(
				RadioProfile.class, term);
	}
	
	public static RadioProfile getDefaultRadioACProfile() {
		Map<String, Object> term = new HashMap<String, Object>(1);
		term.put("radioMode", RadioProfile.RADIO_PROFILE_MODE_AC);
		term.put("radioName", BeParaModule.DEFAULT_RADIO_PROFILE_NAME_AC);
		return getDefaultProfile(
				RadioProfile.class, term);
	}
	
	public static ConfigTemplate getEasyModeDefaultTemplate(Long domainId){
		HmStartConfig startConf = QueryUtil.findBoByAttribute(HmStartConfig.class, "owner.id", domainId);
		if(null != startConf && HmStartConfig.HM_MODE_EASY == startConf.getModeType()){
			return QueryUtil.findBoByAttribute(ConfigTemplate.class, "defaultFlag", false, domainId);
		} else {
			return null;
		}
	}
	
	/**
	 * insert default user group for user management
	 *
	 * @throws Exception -
	 */
	public static void insertDefaultGMUserGroup() throws Exception
	{
		AhAppContainer.HmBe.getBeParaModule().insertDefaultGMUserGroup(); 
	}

}