/**
 *@filename		CustomerInfoUtil.java
 *@version
 *@author		Fiona
 *@createtime	Mar 31, 2012 4:29:08 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.rest;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.be.rest.client.models.UserModel;
import com.ah.be.rest.client.services.UserService;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.wlan.SsidProfile;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * @author		Fiona
 * @version		V1.0.0.0
 */
public class CustomerInfoUtil {

	private static final Tracer log = new Tracer(CustomerInfoUtil.class.getSimpleName());

	public static HmUser getUserInfoFromMyhive(String userEmail, String vhmId, boolean switchFromMyHive) {
		HmUser userInfo = new HmUser();
		if (null != userEmail && null != vhmId) {
			userInfo.setUserName(userEmail);
			userInfo.setEmailAddress(userEmail);

			UserService myhive = new UserService(NmsUtil.getMyHiveServiceURL());
			UserModel userModel = myhive.retrieveUserInfo(userEmail, vhmId);

			CustomerInfoUtil util = new CustomerInfoUtil();

			// error
			if (null == userModel || userModel.getReturnCode() > 0) {
				log.error("getUserInfoFromMyhive", "Cannot get user info from myhive ("+userEmail+") "+(null == userModel?" myhive.retrieveUserInfo is null":userModel.getMessage()));
				if (null != userModel && userModel.getReturnCode() == 1) {
					userInfo = QueryUtil.findBoByAttribute(HmUser.class, "lower(emailAddress)", userEmail.toLowerCase(), util.new GroupQuery());
					return userInfo;
				} else {
					return null;
				}
			} else {
				userInfo.setDefaultFlag(userModel.isDefaultFlg());
				userInfo.setCustomerId(userModel.getCustomerId());
				userInfo.setTimeZone(userModel.getTimeZone());

				vhmId = userModel.getProductId();

				HmDomain domainInfo;
				// home domain
				if (HmDomain.PRODUCT_ID_VHM.equals(vhmId)) {
					domainInfo = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", HmDomain.HOME_DOMAIN);
				} else {
					domainInfo = QueryUtil.findBoByAttribute(HmDomain.class, "vhmID", vhmId);
				}
				if (null == domainInfo) {
					log.error("getUserInfoFromMyhive", "Cannot get hm domain info by vhmid "+vhmId);
					return null;
				}
				userInfo.setOwner(domainInfo);

				if (switchFromMyHive) {
					userInfo.setOwner(QueryUtil.findBoByAttribute(HmDomain.class, "domainName", HmDomain.HOME_DOMAIN));
					userInfo.setSwitchDomain(domainInfo);
					userModel.setGroupName(MgrUtil.getUserGroupName(domainInfo));
//					MgrUtil.setShowLeftTimeFlag(domainInfo, userInfo);
					userInfo.setRedirectUser(true);
				}

				HmUserGroup group = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupName", userModel.getGroupName(), domainInfo.getId(), util.new GroupQuery());
				if (null == group) {
					log.error("getUserInfoFromMyhive", "Cannot get hm user group info by vhmid "+vhmId+", group name "+userModel.getGroupName());
					return null;
				}
				userInfo.setUserGroup(group);
				// add from 6.r3
				// actually it is the user name in MyHive
				userInfo.setUserFullName(userModel.getUserNameInMyhive());
				userInfo.setAccessMyhive(userModel.isAccessRedirector());

				/*
				 * add since Glasgow 6.1r3, update 'limit operator access' settings base on info retrieved from Myhive(if have ever configed on Myhive)
				 * 
				 * if is TechOPs/Partner switch into VHM by click 'Goto VHM' on Portal VHM list page, need not update such info.
				 * only User Management Operator user(belongs to home/VHM domain) self login HMOL need retrieve this info.
				 */
				if (!switchFromMyHive && HmUserGroup.GM_OPERATOR.equalsIgnoreCase(userModel.getGroupName())
						&& userModel.isConfigedLimitOperatorAccessOnMyhive()) {
					updateUserLimitOperatorAccessSettings(
							userModel.getUserSelectedSsids(),
							userModel.getUserSelectedLocalUserGroups(),
							userInfo);
				}
				
				// insert into database for user id
				HmUser userDbInfo = null;
				if (HmUser.ADMIN_USER.equals(userEmail)) {
					List<HmUser> allUsers = QueryUtil.executeQuery(HmUser.class, new SortParams("id"), new FilterParams("userName = :s1 AND owner.domainName = :s2",
						new Object[]{HmUser.ADMIN_USER, HmDomain.HOME_DOMAIN}), null, util.new GroupQuery());
					if (!allUsers.isEmpty()) {
						userDbInfo = allUsers.get(0);
					}
				} else {
					userDbInfo = QueryUtil.findBoByAttribute(HmUser.class, "lower(emailAddress)", userEmail.toLowerCase(), util.new GroupQuery());
				}
				if (null == userDbInfo) {
					userInfo.setId(Long.valueOf(1));
//					if (HAUtil.isSlave()) {
//						userInfo.setId(Long.valueOf(1));
//					} else {
//						userDbInfo = new HmUser();
//						userDbInfo.setUserName(userEmail);
//						userDbInfo.setEmailAddress(userEmail);
//						userDbInfo.setUserGroup(group);
//						userDbInfo.setOwner(userInfo.getOwner());
//						userDbInfo.setDefaultFlag(userInfo.getDefaultFlag());
//						userDbInfo.setAccessMyhive(userInfo.isAccessMyhive());
//						userDbInfo.setTimeZone(userInfo.getTimeZone());
//						userDbInfo.setUserFullName(userInfo.getUserName());
//						try {
//							userInfo.setId(QueryUtil.createBo(userDbInfo));
//						} catch (Exception ex) {
//							log.error("getUserInfoFromMyhive", "Create user ("+userEmail+") in database error : "+ex.getMessage());
//						}
//					}
				} else {
					userInfo.setId(userDbInfo.getId());
					userInfo.setEndUserLicAgree(userDbInfo.isEndUserLicAgree());
					userInfo.setTableColumns(userDbInfo.getTableColumns());
					userInfo.setTableSizes(userDbInfo.getTableSizes());
					userInfo.setAutoRefreshs(userDbInfo.getAutoRefreshs());
//					
//					userDbInfo.setAccessMyhive(userInfo.isAccessMyhive());
//					userDbInfo.setUserFullName(userInfo.getUserFullName());
//					userDbInfo.setUserGroup(group);
//					userDbInfo.setDefaultFlag(userInfo.getDefaultFlag());
//					userDbInfo.setTimeZone(userInfo.getTimeZone());
//					userDbInfo.setUserFullName(userInfo.getUserName());
//					try {
//						QueryUtil.updateBo(userDbInfo);
//					} catch (Exception ex) {
//						log.error("getUserInfoFromMyhive", "Update user ("+userEmail+") in database error : "+ex.getMessage());
//					}
				}
				
				// fix bug 19711, show user name on TopPanel if customer has CID
				userInfo.setUserNameInMyhive(userModel.getUserNameInMyhive());
				return userInfo;
			}
		}
		return null;
	}

	private class GroupQuery implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (bo instanceof HmUserGroup) {
				HmUserGroup group = (HmUserGroup) bo;
				if (group.getInstancePermissions() != null)
					group.getInstancePermissions().size();
				if (group.getFeaturePermissions() != null)
					group.getFeaturePermissions().size();
			}
			if (bo instanceof HmUser) {
				HmUser user = (HmUser) bo;
// cchen DONE
//				if (user.getTableColumns() != null)
//					user.getTableColumns().size();
//				if (user.getTableSizes() != null)
//					user.getTableSizes().size();
//				if (user.getAutoRefreshs() != null)
//					user.getAutoRefreshs().size();
				if (user.getUserGroup().getInstancePermissions() != null)
					user.getUserGroup().getInstancePermissions().size();
				if (user.getUserGroup().getFeaturePermissions() != null)
					user.getUserGroup().getFeaturePermissions().size();
			}
			return null;
		}
	}
	
	
	/**
	 * update 'limit operator access' settings base on info retrieved from Myhive(if have ever configed on Myhive)
	 * 
	 * @param userSelectedSsids
	 * @param userSelectedLocalUserGroups
	 * @param userInfo
	 */
	private static void updateUserLimitOperatorAccessSettings(
			String userSelectedSsids,
			String userSelectedLocalUserGroups, HmUser userInfo) {
		
		if (userInfo == null || userInfo.getOwner() == null) {
			return;
		}
		Long domainId = userInfo.getOwner().getId();
		
		// update selected SSID
		FilterParams  filterParams = new FilterParams("owner.id", domainId);
		List<?> ids = QueryUtil.executeQuery("select id from " + SsidProfile.class.getSimpleName(), null, filterParams);
		userInfo.removeSsidProfiles(ids); // clear old selected SSIDs for user under current VHM which now user login 
		String[] arraySelectedSsids = convertLimitOperatorItemsToList(userSelectedSsids);
		if (arraySelectedSsids != null && arraySelectedSsids.length > 0) {
			Set<SsidProfile> ssidProfiles = new HashSet<SsidProfile>();
			SsidProfile profile;
			for (String ssidName : arraySelectedSsids) {
				profile = QueryUtil.findBoByAttribute(SsidProfile.class, "ssidName", ssidName, domainId);
				if (profile != null) {
					ssidProfiles.add(profile);
				}
			}
			if (ssidProfiles.size() > 0) {
				userInfo.setSsidProfiles(ssidProfiles);
				userInfo.addSsidProfiles();
			}
		}
		
		// update selected local user group
		filterParams = new FilterParams("owner.id", domainId);
		ids = QueryUtil.executeQuery("select id from " + LocalUserGroup.class.getSimpleName(), null, filterParams);
		userInfo.removeLocalUserGroups(ids); //  clear old selected local user groups for user under current VHM which now user login
		String[] arraySelectedLocalUserGroups = convertLimitOperatorItemsToList(userSelectedLocalUserGroups);
		if (arraySelectedLocalUserGroups != null && arraySelectedLocalUserGroups.length > 0) {
			Set<LocalUserGroup> lupProfiles = new HashSet<LocalUserGroup>();
			LocalUserGroup profile;
			for (String localUserGroupNm : arraySelectedLocalUserGroups) {
				profile = QueryUtil.findBoByAttribute(LocalUserGroup.class, "groupName", localUserGroupNm, domainId);
				if (profile != null) {
					lupProfiles.add(profile);
				}
			}
			if (lupProfiles.size() > 0) {
				userInfo.setLocalUserGroups(lupProfiles);
				userInfo.addLocalUserGroups();
			}
		}
	}
	
	/**
	 * items is a string concatenate with '&NBSP'
	 * 
	 * @param items
	 * @return
	 */
	private static String[] convertLimitOperatorItemsToList(String items) {
		if (StringUtils.isEmpty(items)) {
			return null;
		}
		
		return items.split("&NBSP");
	}
}
