package com.ah.util;

import org.apache.commons.lang3.StringUtils;

import com.ah.bo.admin.HmUserSettings;
import com.ah.bo.mgmt.QueryUtil;

public class UserSettingsUtil {

	private static final Tracer log = new Tracer(UserSettingsUtil.class
			.getSimpleName());
	
	public static HmUserSettings getUserSettings(String userEmail) {
		HmUserSettings userSettings = QueryUtil.findBoByAttribute(HmUserSettings.class, "lower(useremail)", StringUtils.lowerCase(userEmail));
		if (userSettings == null) {
			userSettings = new HmUserSettings();
			userSettings.setUseremail(userEmail);
			try {
				QueryUtil.createBo(userSettings);
			} catch (Exception e) {
				log.error("getUserSettings", "create user settings failed.");
			}
		}
		return userSettings;
	}
	
	public static void updateUserSettings(HmUserSettings userSettings) throws Exception {
		QueryUtil.updateBo(userSettings);
	}
	
	// dontshow
	public static void updateDontShowMessageInDashboard(String userEmail, boolean dontshow) throws Exception {
		HmUserSettings userSettings = getUserSettings(userEmail);
		userSettings.setDontShowMessageInDashboard(dontshow);
		updateUserSettings(userSettings);
	}
	
	// endUserLicAgree
	public static void updateEndUserLicAgree(String userEmail, boolean endUserLicAgree) throws Exception {
		HmUserSettings userSettings = getUserSettings(userEmail);
		userSettings.setEndUserLicAgree(endUserLicAgree);
		updateUserSettings(userSettings);
	}
	
	// maxAPNum
	public static void updateMaxAPNum(String userEmail, int maxAPNum) throws Exception {
		HmUserSettings userSettings = getUserSettings(userEmail);
		userSettings.setMaxAPNum(maxAPNum);
		updateUserSettings(userSettings);
	}
	
	// navCustomization
	public static void updateNavCustomization(String userEmail, int navCustomization) throws Exception {
		HmUserSettings userSettings = getUserSettings(userEmail);
		userSettings.setNavCustomization(navCustomization);
		updateUserSettings(userSettings);
	}
	
	// orderFolders
	public static void updateOrderFolders(String userEmail, boolean orderFolders) throws Exception {
		HmUserSettings userSettings = getUserSettings(userEmail);
		userSettings.setOrderFolders(orderFolders);
		updateUserSettings(userSettings);
	}
	
	// promptChanges
	public static void updatePromptChanges(String userEmail, boolean promptChanges) throws Exception {
		HmUserSettings userSettings = getUserSettings(userEmail);
		userSettings.setPromptChanges(promptChanges);
		updateUserSettings(userSettings);
	}
	
	// syncResult
	public static void updateSyncResult(String userEmail, short syncResult) throws Exception {
		HmUserSettings userSettings = getUserSettings(userEmail);
		userSettings.setSyncResult(syncResult);
		updateUserSettings(userSettings);
	}
	
	// treeWidth
	public static void updateTreeWidth(String userEmail, short treeWidth) throws Exception {
		HmUserSettings userSettings = getUserSettings(userEmail);
		userSettings.setTreeWidth(treeWidth);
		updateUserSettings(userSettings);
	}
}
