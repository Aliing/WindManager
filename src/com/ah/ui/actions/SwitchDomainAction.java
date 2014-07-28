package com.ah.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.rest.CustomerInfoUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.home.StartHereAction;
import com.ah.ui.actions.monitor.CurrentUserCache;
import com.ah.util.HmProxyUtil;
import com.ah.util.MgrUtil;
import com.ah.util.notificationmsg.AhNotificationMsgUtil;

/*
 * @author Chris Scheers
 */

public class SwitchDomainAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	@Override
	public String execute() throws Exception {
		try {
			if ("switchDomain".equals(operation)) {
				// TechOPs switch domain with link 'Menu' on HM top panel
				return switchDomain();
			} else if ("redirectToVHM".equals(operation)) {
				// from landing page(VHM user with CID) / portal VHM list(TechOPs or Partners click 'GoToVHM' button)
				return redirectVHM();
			} else if ("switchDomainPage".equals(operation)) {
				// TechOPs click page link on HM top panel switch domain menu 
				return switchDomainpage();
			}
			
			return SUCCESS;
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	
	private String switchDomainpage() throws Exception{
		jsonObject = new JSONObject();
		List<String> domainData = new ArrayList<>();
		List<HmDomain> lstDomain= getSwitchDomainData();
		if (!filterPara.equals("") && !filterPara.equals(getFilterVHMText())){
			for (Iterator<HmDomain> iterator = lstDomain.iterator(); iterator.hasNext();) {
				HmDomain oneDomain = iterator.next();
				if (!oneDomain.getDomainNameESC().toLowerCase().contains(filterPara.toLowerCase())) {
					iterator.remove();
				}
			}
		}
		for(int i=domanCurrentPage*ONEPAGE_SIZE; i<(domanCurrentPage*ONEPAGE_SIZE +ONEPAGE_SIZE) && i<lstDomain.size(); i++){
			domainData.add(lstDomain.get(i).getDomainNameESC());
		}
		jsonObject.put("v", domainData);
		
		if (lstDomain.size()==0) {
			jsonObject.put("s", 1);
		} else {
			if (lstDomain.size()%ONEPAGE_SIZE==0) {
				jsonObject.put("s", lstDomain.size()/ONEPAGE_SIZE);
			} else {
				jsonObject.put("s", lstDomain.size()/ONEPAGE_SIZE +1);
			}
		}
		return "json";
	}
	
	
	public static final int ONEPAGE_SIZE=25;
	private int domanCurrentPage;
	private String filterPara="";
	
	private String switchDomain() throws Exception
	{
		if (switchDomainID != null) {
			HmDomain domain = findBoById(HmDomain.class, switchDomainID);
			domainName = domain != null ? domain.getDomainName() : domainName;
		}
		
		jsonObject = new JSONObject();
		if (userContext == null || !userContext.isSuperUser()) {
			jsonObject.put("success", false);
		} else {
			// reset super user group
			userContext.setUserGroup(getUserGroupByName(HmUserGroup.ADMINISTRATOR));
			userContext.setShowLeftAccessTimeForSwitchDomain(false);
			if (userContext.isSuperUser()) {
				// after switched domain, user group will be override, so need save this flag to another attribute
				userContext.setSuperUserForSwitchDomain(true);
			}
			if (userContext.isVadAdmin()) {
				// after switched domain, user group will be override, so need save this flag to another attribute
				userContext.setVadForSwitchDomain(true);
			}
			
			jsonObject.put("success", true);
			if (getAllVHMsText().equals(domainName)) {
				userContext.setSwitchDomain(null);
				userContext.setMode((short) 0);//reset mode value;
				NavigationNode tree = userContext.getNavigationTree();
				createNavigationTree4VHM(userContext);
				setTreeVisibility(tree);
				jsonObject.put("dn", userContext.getUserName());
			} else {
				HmDomain hmDomain = findDomain(domainName);
				if (hmDomain == null) {
					jsonObject.put("success", false);
				} else {
					// access control
					if (!NmsUtil.hasReadAccessPermission(hmDomain.getAccessMode(), hmDomain.getAuthorizationEndDate())) {
						jsonObject.put("success", false);
						jsonObject.put("msg",
								MgrUtil.getUserMessage("error.no.vhm.access.permisssion", "read"));
						return "json";
					}
					
					if (hmDomain.getRunStatus() == HmDomain.DOMAIN_RESTORE_STATUS) {
						jsonObject.put("success", false);
						jsonObject.put("msg",
								getText("error.auth.vhm.restoring"));
					} else if (hmDomain.getRunStatus() == HmDomain.DOMAIN_BACKUP_STATUS) {
						jsonObject.put("success", false);
						jsonObject.put("msg",
								getText("error.auth.vhm.backuping"));
					} else if (hmDomain.getRunStatus() == HmDomain.DOMAIN_UPDATE_STATUS) {
						jsonObject.put("success", false);
						jsonObject.put("msg",
								getText("error.auth.vhm.updating"));
					} else if (hmDomain.getRunStatus() == HmDomain.DOMAIN_DISABLE_STATUS) {
						jsonObject.put("success", false);
						jsonObject.put("msg",
								getText("error.auth.vhm.disabled"));
					} else if (!StartHereAction.isStartHereConfigured(hmDomain
							.getId())) {
						jsonObject.put("success", false);
						jsonObject.put("msg",
								getText("error.auth.vhm.uninitialize"));
					} else {
						userContext.setSwitchDomain(hmDomain);
						userContext.setMode((short) 0);//reset mode value;
						jsonObject.put("dn", domainName + "//"
								+ userContext.getUserName());

						if (HmDomain.HOME_DOMAIN.equals(domainName)) {
							NavigationNode tree = userContext
									.getNavigationTree();
							createNavigationTree(userContext);
							setTreeVisibility(tree);
						} else {
							// create navigation tree for vhm
/*							String where = "owner = :s1 AND groupName = :s2";
							Object[] values = new Object[2];
							values[0] = hmDomain;
//							values[1] = HmUserGroup.CONFIG;
							values[1] = MgrUtil.getUserGroupName(hmDomain);
							FilterParams filterParams = new FilterParams(where,
									values);
							List<HmUserGroup> list = QueryUtil.executeQuery(
									HmUserGroup.class, null, filterParams, null, this);
							if (!list.isEmpty()) {*/
							setDependentVaulesForAccessMode();
							if (userContext.getUserGroup() != null) {
								HmUser user = new HmUser();
								user.setUserGroup(userContext.getUserGroup());
								user.setId(Long.valueOf(1));
								user.setOwner(hmDomain);
								NavigationNode tree = userContext
										.getNavigationTree();
								createNavigationTree4VHM(user);
								setTreeVisibility(tree);
								
//								userContext.setUserGroup(userGroup);
							}
						}
					}
				}
			}
		}
		if(jsonObject.getBoolean("success")) {
           AhNotificationMsgUtil.refreshNotificationMsgs(this);
           if(NmsUtil.isHostedHMApplication()){
               MgrUtil.setSessionAttribute(SessionKeys.VHM_CUSTOMER_INFO_KEY, null);
           }
		}
		return "json";
	}
	
	// code for Techops/VAD Admin/VHM user with CID, redirect to vHM scope with CAS auth
	private String redirectVHM() throws Exception
	{
		// get user from cas server
		if (switchFromMyHive) {
			// request from button 'GoTo VHM' click on portal VHM list. (TechOps/Partners)
			String userName = request.getRemoteUser();
			if (null != userName) {
				userContext = CustomerInfoUtil.getUserInfoFromMyhive(userName, domainName, switchFromMyHive);
			}
			
			// for access mode(replace current user's group with switch domain's user group, group name according to access mode)
			setDependentVaulesForAccessMode();
		} else {
			// VHM end user(with CID) click one VHM on landing page
			String errorCode = getUserInfoForSSO(domainName);
			if(null == errorCode){
				return "recreateUser";
			}else{
				String authServerURL = NmsUtil.getAuthServiceURL();

				if (!authServerURL.endsWith("/")) {
					authServerURL += "/";
				}

				redirectUrl = authServerURL + "logout";

				// need go to master
				if ("slave".equals(errorCode)) {
					redirectUrl = getMasterURL();
				} else if ("exception".equals(errorCode)) {
					redirectUrl = NmsUtil.getMyHiveServiceURL()+"/loginError.action?loginErrorMsg=error.authentication.credentials.bad";
				} else {
					redirectUrl = NmsUtil.getMyHiveServiceURL()+"/loginError.action?loginErrorMsg="+errorCode;
				}
				return "redirect"; 
			}
		}
		
		if (userContext == null) {
			return SUCCESS;
		}
		
//		HmDomain hmDomain = findDomain(domainName);
//		if (hmDomain == null) {
//			return SUCCESS;
//		}
//		
//		// check owner user of domain
//		if (getUserContext().isVadAdmin()
//				&& (hmDomain.getOwnerUser() == null || (hmDomain.getOwnerUser() != null && !hmDomain
//						.getOwnerUser().getId().equals(getUserContext().getId())))) {
//			redirectUrl = getSingleSignOutURL();
//			return "redirect";
//		}
		
		setSessionUserContext(userContext);
		userContext.setUserIpAddress(HmProxyUtil.getClientIp(request));
		request.getSession().setAttribute(userContext.getId().toString(),
				CurrentUserCache.getInstance());
		initSessionExpiration();
		userContext.createTableViews();
		userContext.createTableSizeMappings();
		userContext.setMode((short) 0);//reset mode value;
		
		// create navigation tree for vhm
		if ((null == userContext.getCustomerId() || userContext.getCustomerId().isEmpty()) && null != userContext.getSwitchDomain()) {
			String where = "owner = :s1 AND groupName = :s2";
			Object[] values = new Object[2];
			values[0] = userContext.getSwitchDomain();
			values[1] = NmsUtil.isPlanner() ? HmUserGroup.PLANNING : HmUserGroup.CONFIG;
			FilterParams filterParams = new FilterParams(where, values);
			List<HmUserGroup> list = QueryUtil.executeQuery(
					HmUserGroup.class, null, filterParams, null, this);
			if (!list.isEmpty()) {
				HmUser user = new HmUser();
				user.setUserGroup(list.get(0));
				user.setId(Long.valueOf(1));
				user.setOwner(userContext.getSwitchDomain());
				createNavigationTree4VHM(user);
				setTreeVisibility(userContext.getNavigationTree());
				userContext.setUserGroup(list.get(0));
			}
		} else {
			if (null != userContext.getSwitchDomain()) {
				// fix bug 24891 : techop/partner goto express VHM, but show enterprise VHM's menu
				// switch domain is not null, means is case techop/partner goto VHM, need use switch domain to create menu tree
				HmUser user = new HmUser();
				user.setUserGroup(userContext.getUserGroup());
				user.setId(Long.valueOf(1));
				user.setOwner(userContext.getSwitchDomain());
				createNavigationTree4VHM(user);
			} else {
				createNavigationTree4VHM(userContext);
			}
			setTreeVisibility(userContext.getNavigationTree());
		}
		
		redirectUrl = request.getScheme() + "://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath() + "/";
		
		String result = "recreateUser";
		if(!NmsUtil.isPlanner()){
			injectCSRFToken();
			result = SUCCESS;
		}
		
		return result;
	}

	private void setTreeVisibility(NavigationNode tree) {
		for (NavigationNode l1feature : tree.getChildNodes()) {
			for (NavigationNode l2feature : l1feature.getChildNodes()) {
				if (l2feature.isSummary() && l2feature.isExpanded()) {
					expandTree(l2feature.getKey(), true);
				}
			}
		}
	}

	private HmDomain findDomain(String domainName) throws Exception {
		for (HmDomain hmDomain : CacheMgmt.getInstance().getCacheDomains()) {
			if (domainName.equals(hmDomain.getDomainName())) {
				return hmDomain;
			}
		}
		return null;
	}

	private String domainName;

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	private Long switchDomainID;

	public void setSwitchDomainID(Long switchDomainID) {
		this.switchDomainID = switchDomainID;
	}
	
	private String redirectUrl;

	public String getRedirectUrl()
	{
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl)
	{
		this.redirectUrl = redirectUrl;
	}
	
	private boolean switchFromMyHive;

	public boolean isSwitchFromMyHive() {
		return switchFromMyHive;
	}

	public void setSwitchFromMyHive(boolean switchFromMyHive) {
		this.switchFromMyHive = switchFromMyHive;
	}

	@Override
	public Collection<HmBo> load(HmBo bo)
	{
		if (bo instanceof HmUser) {
			HmUser user = (HmUser) bo;
// cchen DONE			
//			if (user.getTableColumns() != null)
//				user.getTableColumns().size();
//			if (user.getTableSizes() != null)
//				user.getTableSizes().size();
//			if (user.getAutoRefreshs() != null)
//				user.getAutoRefreshs().size();
			if (user.getUserGroup().getInstancePermissions() != null)
				user.getUserGroup().getInstancePermissions().size();
			if (user.getUserGroup().getFeaturePermissions() != null)
				user.getUserGroup().getFeaturePermissions().size();
		} else if (bo instanceof HmUserGroup) {
			HmUserGroup group = (HmUserGroup) bo;
			if (group.getInstancePermissions() != null)
				group.getInstancePermissions().size();
			if (group.getFeaturePermissions() != null)
				group.getFeaturePermissions().size();
		}
		return null;
	}

	/**
	 * @return the domanCurrentPage
	 */
	public int getDomanCurrentPage() {
		return domanCurrentPage;
	}

	/**
	 * @param domanCurrentPage the domanCurrentPage to set
	 */
	public void setDomanCurrentPage(int domanCurrentPage) {
		this.domanCurrentPage = domanCurrentPage;
	}

	/**
	 * @return the filterPara
	 */
	public String getFilterPara() {
		return filterPara;
	}

	/**
	 * @param filterPara the filterPara to set
	 */
	public void setFilterPara(String filterPara) {
		this.filterPara = filterPara;
	}
	
	private HmUserGroup getUserGroupByName(String groupName) {
		return QueryUtil.findBoByAttribute(HmUserGroup.class, "groupName", groupName, this);
	}
	
	private HmUserGroup getUserGroupForAccessMode(HmDomain switchedDomain) {
		String groupName = MgrUtil.getUserGroupName(switchedDomain);
		return QueryUtil.findBoByAttribute(HmUserGroup.class,
				"groupName", groupName, switchedDomain.getId(), this);
	}
	
	private void setDependentVaulesForAccessMode() {
		if (userContext == null) {
			return;
		}
		if (userContext.isSuperUser()) {
			// after switched domain, user group will be override, so need save this flag to another attribute
			userContext.setSuperUserForSwitchDomain(true);
		} else {
			userContext.setSuperUserForSwitchDomain(false);
		}
		if (userContext.isVadAdmin()) {
			// after switched domain, user group will be override, so need save this flag to another attribute
			userContext.setVadForSwitchDomain(true);
		} else {
			userContext.setVadForSwitchDomain(false);
		}
		
		HmDomain vhmDomain = userContext.getSwitchDomain();
		MgrUtil.setShowLeftTimeFlag(vhmDomain, userContext);
		
		HmUserGroup userGroup = getUserGroupForAccessMode(vhmDomain);
		// set VHM's user group(according to access mode) to session user group 
		userContext.setUserGroup(userGroup);
	}

}