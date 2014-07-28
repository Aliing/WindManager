package com.ah.ui.interceptors;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.LicenseInfo;
import com.ah.be.license.LicenseOperationTool;
import com.ah.be.search.PageIndex;
import com.ah.be.search.SearchEngineImpl;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.HmMenuAction;
import com.ah.ui.actions.SessionKeys;
import com.ah.ui.actions.admin.LicenseMgrAction;
import com.ah.ui.actions.admin.RestoreDBAction;
import com.ah.ui.actions.config.ConfigGuideAction;
import com.ah.ui.actions.hiveap.HiveApAction;
import com.ah.ui.actions.monitor.AhTrapsAction;
import com.ah.ui.actions.monitor.CurrentUserCache;
import com.ah.ui.actions.monitor.MapSettingsAction;
import com.ah.ui.actions.monitor.MapsAction;
import com.ah.ui.actions.teacherView.StudentRegisterAction;
import com.ah.ui.actions.tools.PlanToolAction;
import com.ah.util.MgrUtil;
import com.ah.util.notificationmsg.AhNotificationMsgUtil;

public class GlobalForwardInterceptor implements Interceptor, QueryBo {

	private static final long serialVersionUID = 1L;
	
	private static Long homeDomain;

	@Override
	public void destroy() {
		
	}

	@Override
	public void init() {
		
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		HttpServletRequest request = (HttpServletRequest) invocation
				.getInvocationContext().get(ServletActionContext.HTTP_REQUEST);

		Object action = invocation.getAction();
		
		/*
		 * to support HM Search - Index, since HM3.5R2
		 */
		if(PageIndex.needBuildIndex() && PageIndex.isLocalRequest(request)) {
			if(action instanceof BaseAction) {
				BaseAction baseAction = (BaseAction)action;
				baseAction.setDomainId(getHomeDomain());
				baseAction.setMode(HmStartConfig.HM_MODE_FULL);
                baseAction.setUserContext(((SearchEngineImpl)AhAppContainer.getBeMiscModule().getSearchEngine())
                        .getPageIndex().getUserContext());
			}
			
			return invocation.invoke();
		}
		
		if (isPenetrateInterceptorAction(action)) {
			// swf upload do not support https, use http instead
			return invocation.invoke();
		} else if (action instanceof BaseAction) {
			if (!NmsUtil.isHTTPEnable() && !"https".equals(request.getScheme())) {
				// SSL redirect (check this only in case of HTTP not enabled)
				return "ssl";
			}

			BaseAction myAction = (BaseAction) action;
			HmUser vhmUser = myAction.getUserContext();
			
			// Authentication.
			if (null == vhmUser && !(action instanceof StudentRegisterAction)) {
				// Need to redirect to login.
				return "login";
			}
			
			// check database connection
			try {
				HmBeOsUtil.getMaintenanceModeFromDb();
			} catch (Exception ex) {
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
						HmSystemLog.FEATURE_ADMINISTRATION, MgrUtil.getUserMessage("error.global.database.connection"));
				return "error503";
			}

			// this hivemanager is hm online
			if (NmsUtil.isHostedHMApplication()) {
				if (HmBeOsUtil.getMaintenanceModeFromDb() && !vhmUser.isSuperUser()) {
					// Need to redirect to login.
					return "login";
				}
				
				// redirect to landing page if temp access has been expired
				HmDomain switchDomain = vhmUser.getSwitchDomain();
				if (switchDomain != null) {
					HmDomain newSwitchDomain = findDomain(switchDomain.getDomainName());
					if (null == newSwitchDomain) {
						MgrUtil.removeSessionAttribute(BaseAction.USER_CONTEXT);
						return "login";
					} else {
						vhmUser.setSwitchDomain(newSwitchDomain);
						if (!NmsUtil.hasReadAccessPermission(newSwitchDomain.getAccessMode(), newSwitchDomain.getAuthorizationEndDate())) {
							myAction.setBaseRedirectUrl(myAction.getMyHivePage());
							return "baseRedirect";
						}
					}
				}
			} else {
				boolean shouldLogin = true;

				for (HttpSession activeUser : CurrentUserCache.getInstance()
						.getActiveSessions()) {
					HmUser hmUser;
					try {
						hmUser = (HmUser) activeUser.getAttribute(BaseAction.USER_CONTEXT);
					} catch (Exception e) {
						continue;
					}
					if (null != hmUser
							&& hmUser.getId().longValue() == vhmUser.getId()) {
						shouldLogin = false;
						break;
					}
				}

				if (shouldLogin) {
					return "login";
				}
			}
			
			// check if this user has eula
			if (vhmUser.ifShowEualPage() && !(action instanceof StudentRegisterAction) && !HAUtil.isSlave()) {
				return "eula";
			}
			
			String groupName = vhmUser.getUserGroup().getGroupName();

			if (!HmUserGroup.PLANNING.equals(groupName) && !HmUserGroup.TEACHER.equals(groupName) && !(action instanceof LicenseMgrAction)) {
				// check HM mode has configured
				// use user's domain for switch to has not been initialized vhm
				//HmDomain domObj = null == vhmUser.getSwitchDomain() ? vhmUser.getOwner() : vhmUser.getSwitchDomain(); 
				List<?> list = QueryUtil.executeQuery("select adminUserLogin from "+HmStartConfig.class.getSimpleName(), null, 
						new FilterParams("owner.id", vhmUser.getOwner().getId()));
				
				if ((list.isEmpty() || !((Boolean)list.get(0))) && null == vhmUser.getSwitchDomain()) {
					if (vhmUser.getDefaultFlag()) {
						return "startHere";
					} else {
						HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
								HmSystemLog.FEATURE_ADMINISTRATION,
								MgrUtil.getUserMessage("error.admin.login.vhm.login"));
						return "login";
					}
				}
			}

			// check license and activation key
			if (!(action instanceof LicenseMgrAction) && HmDomain.DOMAIN_DEFAULT_STATUS == vhmUser.getOwner().getRunStatus()) {
				if (!HmBeActivationUtil.ACTIVATION_KEY_VALID) {
					return "startHere";
				} else {					
					// check vhm order key if valid
					String domainName = (vhmUser.getSwitchDomain() == null ? vhmUser
						.getDomain() : vhmUser.getSwitchDomain()).getDomainName();
					
					LicenseInfo orderInfo = null;
					if (!HmDomain.HOME_DOMAIN.equals(domainName) && NmsUtil.isHostedHMApplication()) {
						orderInfo = HmBeLicenseUtil.VHM_ORDERKEY_INFO.get(domainName);
						if (null == orderInfo) {
							orderInfo = LicenseOperationTool.getOrderKeyInfoFromDatabase(domainName, false);
						}
					}
					
					// whole HM
					if (null == orderInfo) {
						if (HmBeLicenseUtil.NO_LICENSE_MUST_INPUT == HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID) {
							HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
									HmSystemLog.FEATURE_ADMINISTRATION, MgrUtil.getUserMessage("hm.system.log.global.forward.license.input"));
							return "startHere";
						} else if (HmBeLicenseUtil.LICENSE_INVALID == HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID) {
							HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
									HmSystemLog.FEATURE_ADMINISTRATION, MgrUtil.getUserMessage("hm.system.log.global.forward.new.license.input"));
							return "startHere";
						}
					// vhm in HM Online
					} else {
						// there is no order key
						if (BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(orderInfo.getLicenseType()) && orderInfo.getLeftHours() <= 0) {
							HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
									HmSystemLog.FEATURE_ADMINISTRATION, MgrUtil.getUserMessage("error.admin.login.entitlementKey", new String[] {domainName}));
							return "startHere";
						}
						// for evaluation or permanent order key
						if (orderInfo.getHiveAps() <= 0 && orderInfo.getCvgNumber() <= 0 && !orderInfo.isZeroDeviceKeyValid()) {
							HmBeLicenseUtil.VHM_ORDERKEY_INFO.put(domainName, orderInfo);
							HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
									HmSystemLog.FEATURE_ADMINISTRATION, MgrUtil.getUserMessage("error.admin.login.entitlementKey.invalid", new String[] {domainName}));
							return "startHere";
						}
					}
				}
			}

			// if (myAction.getUserContext().getUserGroup().isAdministrator()) {
			// myAction.setReadPermission(true);
			// myAction.setWritePermission(true);
			// } else {
			if (myAction.getSelectedL2Feature() != null) {
				HmPermission featurePermission = vhmUser.getUserGroup().getFeaturePermissions().get(myAction.getSelectedL2Feature().getKey());
				if (featurePermission != null) {
					if (featurePermission.hasAccess(HmPermission.OPERATION_READ)) {
						myAction.setReadPermission(true);
					}
					if (HAUtil.isSlave()) {
						myAction.setWritePermission(false);
					} else if (featurePermission.hasAccess(HmPermission.OPERATION_WRITE)) {
						myAction.setWritePermission(true);
					}
				}
			}
			// }

			// Remove unselected items on the submitted page.
			if (myAction.getPageIds() != null
					&& myAction.getAllSelectedIds() != null) {
				myAction.getAllSelectedIds().removeAll(myAction.getPageIds());
			}

			// Append items selected on the submitted page.
			if (myAction.getSelectedIds() != null) {
				if (myAction.getAllSelectedIds() == null) {
					myAction.setAllSelectedIds(new HashSet<Long>());
				}
				myAction.getAllSelectedIds().addAll(myAction.getSelectedIds());
			}

			if (MgrUtil.getSessionAttribute("errorMessage") != null) {
				myAction.addActionError(MgrUtil.getSessionAttribute(
						"errorMessage").toString());
				MgrUtil.removeSessionAttribute("errorMessage");
			}

			if ("paintbrushSourceInfo".equals(myAction.getOperation())) {
				myAction.getPaintbrushSourceInfo();
				return "json";
			}
			
			// return null;
			if (myAction.getSearchResult() != null) {
				if (myAction.isSearchFlg()){
					myAction.removeSessionAttributes();
				} else {
					if(!(myAction instanceof AhTrapsAction
			                || null != request.getHeader("X-Requested-With")))
					    // ignore the specific action and the AJAX requests
						myAction.clearSearchResult();
				}
			}
			
			refreshNotificationPool(request, action);
		}

		return invocation.invoke();
	}

    /**
     * Refresh the notification message pool.
     * 
     * @author Yunzhi Lin
     * - Time: Feb 2, 2012 2:10:37 PM
     * @param request -
     * @param action -
     */
    private void refreshNotificationPool(HttpServletRequest request, Object action) {
        // refresh the notification messages when it is NOT an asynchronization request 
        if (null == request.getHeader("X-Requested-With")) {
            if (isNeedRefreshNotificationMsgs(action)) {
                AhNotificationMsgUtil.refreshNotificationMsgs(action);
            } else if("GET".equalsIgnoreCase(request.getMethod())) {
                // handle the GET method
                if(action instanceof ConfigGuideAction) {
                    // enforce refresh pool when jump from the message banner
                    AhNotificationMsgUtil.refreshNotificationMsgs(action);
                } else if(action instanceof HiveApAction) {
                    // enforce refresh pool when jump from the message banner
                    if("managedHiveAps".equals(((HiveApAction)action).getHmListType())
                            && "managedHiveAps".equals(((HiveApAction)action).getOperation())) {
                        AhNotificationMsgUtil.refreshNotificationMsgs(action);
                    }
                }
            }
        }
    }
	
	/**
	 * Adjust the refresh status - Only refresh the message pool when request from menu action.<br>
	 * (Need to filter request for the menu action will redirect to specific action, 
	 * it means that the server side will receive two requests when click the menu action.)
	 * @author Yunzhi Lin
	 * - Time: Dec 7, 2011 5:06:35 PM
	 * @param action ({@link BaseAction})
	 * @return <code>True</code> or <code>False</code>
	 */
    private boolean isNeedRefreshNotificationMsgs(Object action) {
        boolean flag = false;
        if (action instanceof HmMenuAction) {
            String prevOperation = (String) MgrUtil
                    .getSessionAttribute(SessionKeys.PREV_MENU_OPERATION);
            String currentOperation = ((BaseAction) action).getOperation();
            if (null != prevOperation) {
                // 'home' && 'configuration' DON't have operation when redirect
                if ((prevOperation.equals("home") || prevOperation.equals("configuration"))
                        && null == currentOperation) {
                    flag = false;
                } else if (!prevOperation.equals(currentOperation)) {
                    flag = true;
                }
            } else {
                flag = true;
            }
            MgrUtil.setSessionAttribute(SessionKeys.PREV_MENU_OPERATION, currentOperation);
        }
        return flag;
    }
	/**
	 * Specify the action which need to go through this interceptor directly 
	 * @author Yunzhi Lin
	 * - Time: Jun 2, 2011 4:59:12 PM
	 * @param action inherited from {@link BaseAction}
	 * @return <code>true</code> or <code>false</code> 
	 */
	private boolean isPenetrateInterceptorAction(Object action) {
		if (action instanceof PlanToolAction
				&& "upload".equals(((BaseAction) action).getOperation())) {
			return true;
		} else if (action instanceof MapSettingsAction
				&& "uploadImage".equals(((BaseAction) action).getOperation())) {
			return true;
		} else if (action instanceof MapsAction
		        && "uploadPlanningData".equals(((BaseAction) action).getOperation())) {
		    return true;
		} else if (action instanceof RestoreDBAction
				&& "pollRestoreStatus".equals(((RestoreDBAction) action).getOperation())) {
			return true;
		} else {
			return false;
		}
	}
	
	private Long getHomeDomain() {
		if(homeDomain != null) {
			return homeDomain;
		}
		
		HmDomain home = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", HmDomain.HOME_DOMAIN);

		return home != null ? home.getId() : null;
	}

	private HmDomain findDomain(String domainName) throws Exception {
		for (HmDomain hmDomain : CacheMgmt.getInstance().getCacheDomains()) {
			if (domainName.equals(hmDomain.getDomainName())) {
				return hmDomain;
			}
		}
		return null;
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
		}
		return null;
	}

}