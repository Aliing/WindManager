package com.ah.ws.rest.server.bussiness;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.admin.HmAutoRefresh;
import com.ah.bo.admin.HmLocalUserGroup;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmTableSize;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserSettings;
import com.ah.bo.admin.HmUserSsidProfile;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.notificationmsg.NotificationMessageStatus;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.wlan.SsidProfile;
import com.ah.util.Tracer;

public class UsersBussiness {
	
	private static final Tracer log = new Tracer(UsersBussiness.class
			.getSimpleName());
	/**
	 * remove users from table hm_user
	 * 
	 * @param userEmails
	 * @throws Exception
	 */
	public static void removeUsersFromHmUser(List<String> userEmails) throws Exception {
		HmUser user;
		List<Long> removeObj = new ArrayList<>();
		for (String userEmail : userEmails) {
			user = QueryUtil.findBoByAttribute(HmUser.class, "lower(emailAddress)", StringUtils.lowerCase(userEmail));
			if (user != null) {
				removeObj.add(user.getId());
			}
		}
		
		if (!removeObj.isEmpty()) {
			QueryUtil.removeBos(HmUser.class, removeObj);
		}
	}
	
	/**
	 * remove users' settings from table:
	 *  1.hm_table_column_new
	 *  2.hm_table_size_new
	 *  3.hm_autorefresh_settings_new
	 *  4.USER_LOCALUSERGROUP
	 *  5.USER_SSIDPROFILE
	 * 
	 * 
	 * @param userEmails
	 */
	public static void removeUserSettings(List<String> userEmails) {
		
		for (String userEmail : userEmails) {
			
			FilterParams filterParams = new FilterParams("lower(useremail)", StringUtils.lowerCase(userEmail));
			
			// hm_table_column_new
			try {
				QueryUtil.removeBos(HmTableColumn.class, filterParams);
			} catch (Exception e) {
				log.error("removeUserSettings", "remove user[" + userEmail + "]'s settings from table hm_table_column_new failed", e);
			}
			// hm_table_size_new
			try {
				QueryUtil.removeBos(HmTableSize.class, filterParams);
			} catch (Exception e) {
				log.error("removeUserSettings", "remove user[" + userEmail + "]'s settings from table hm_table_size_new failed", e);
			}
			// hm_autorefresh_settings_new
			try {
				QueryUtil.removeBos(HmAutoRefresh.class, filterParams);
			} catch (Exception e) {
				log.error("removeUserSettings", "remove user[" + userEmail + "]'s settings from table hm_autorefresh_settings_new failed", e);
			}
			// user_localusergroup_new
			try {
				QueryUtil.removeBos(HmLocalUserGroup.class, filterParams);
			} catch (Exception e) {
				log.error("removeUserSettings", "remove user[" + userEmail + "]'s settings from table user_localusergroup_new failed", e);
			}
			// user_ssidprofile_new
			try {
				QueryUtil.removeBos(HmUserSsidProfile.class, filterParams);
			} catch (Exception e) {
				log.error("removeUserSettings", "remove user[" + userEmail + "]'s settings from table user_ssidprofile_new failed", e);
			}
			// user_ssidprofile_new
			try {
				QueryUtil.removeBos(HmUserSettings.class, filterParams);
			} catch (Exception e) {
				log.error("removeUserSettings", "remove user[" + userEmail + "]'s settings from table hm_user_settings failed", e);
			}
			// user message notification
			try {
				QueryUtil.removeBos(NotificationMessageStatus.class, filterParams);
			} catch (Exception e) {
				log.error("removeUserSettings", "remove user[" + userEmail + "]'s settings from table notification_message_status failed", e);
			}
		}
	}
	
	/**
	 * get all SSID profile name under specified VHM
	 * 
	 * @param vhmId
	 * @return
	 */
	public static List<String> getVhmAvaliableSsids(String vhmId) {
		List<?> ssids = QueryUtil.executeQuery("select ssidName from " + SsidProfile.class.getSimpleName(), new SortParams(
				"id"), new FilterParams("owner.vhmID", vhmId));
		return (List<String>)ssids;
	}
	
	/**
	 * get all local user group name under specified VHM
	 * 
	 * @param vhmId
	 * @return
	 */
	public static List<String> getVhmAvaliableLocalUserGroups(String vhmId) {
		List<?> localUserGroups = QueryUtil.executeQuery("select groupName from " + LocalUserGroup.class.getSimpleName(), new SortParams(
				"id"), new FilterParams("owner.vhmID = :s1 and userType = :s2", new Object[]{vhmId, LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK}));
		return (List<String>)localUserGroups;
	}
	
	public static List<String> getUserSelectedSsids(String vhmId, String userEmail) {

		List<HmUserSsidProfile> ssidProfilesIds = QueryUtil.executeQuery(HmUserSsidProfile.class, null , new FilterParams("useremail", userEmail));
		List<String> selectedItems = new ArrayList<String>();
		for (HmUserSsidProfile userSsidProfile : ssidProfilesIds) {
			FilterParams filterParams = new FilterParams(
					"owner.vhmID = :s1 and id = :s2", new Object[] { vhmId,
							userSsidProfile.getSsidprofile_id() });
			List<SsidProfile> ssidProfiles = QueryUtil.executeQuery(SsidProfile.class, null, filterParams);
			if (ssidProfiles != null && !ssidProfiles.isEmpty()) {
				selectedItems.add(ssidProfiles.get(0).getSsidName());
			}
		}
		return selectedItems;
	}
	
	public static List<String> getUserSelectedLocalUserGroups(String vhmId, String userEmail) {

		List<HmLocalUserGroup> localUserGroupsIds = QueryUtil.executeQuery(HmLocalUserGroup.class, null , new FilterParams("useremail", userEmail));
		List<String> selectedItems = new ArrayList<String>();
		for (HmLocalUserGroup hmLocalUserGroup : localUserGroupsIds) {
			FilterParams filterParams = new FilterParams(
					"owner.vhmID = :s1 and id = :s2", new Object[] { vhmId,
							hmLocalUserGroup.getLocalusergroup_id() });
			List<LocalUserGroup> localUserGroups = QueryUtil.executeQuery(LocalUserGroup.class, null, filterParams);
			if (localUserGroups != null && !localUserGroups.isEmpty()) {
				selectedItems.add(localUserGroups.get(0).getGroupName());
			}
		}
		return selectedItems;
	}
}
