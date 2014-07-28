package com.ah.ui.actions;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.Ostermiller.util.Base64;
import com.ah.be.admin.auth.AhAuthException;
import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.SendMailUtil;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.HM_License;
import com.ah.be.license.LicenseInfo;
import com.ah.be.ls.ClientSenderCenter;
import com.ah.be.rest.client.models.SettingsModel;
import com.ah.be.rest.client.services.SettingsService;
import com.ah.bo.HmBo;
import com.ah.bo.admin.ActivationKeyInfo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAccessControl;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmExpressModeEnable;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.admin.LicenseHistoryInfo;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.admin.OrderHistoryInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.GroupByParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.admin.LicenseMgrAction;
import com.ah.ui.actions.home.StartHereAction;
import com.ah.ui.actions.monitor.CurrentUserCache;
import com.ah.util.EnumItem;
import com.ah.util.HmProxyUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.UserSettingsUtil;
import com.ah.util.bo.starthere.NewStartHereForm;
import com.ah.ws.rest.client.utils.ClientUtils;
import com.ah.ws.rest.client.utils.RedirectorResUtils;
import com.ah.ws.rest.models.DeviceCounts;
import com.ah.ws.rest.models.idm.VHMCustomerInfo;

/*
 * @author Chris Scheers
 */

public class LoginAction extends LicenseMgrAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(LoginAction.class
			.getSimpleName());

	private String display = "";
	
	private boolean vmwareConnectLsCheck = false;
	
	private String vmwareConnectLsInfo = null;

	@Override
	public void prepare() throws Exception {
//		if (HAMonitorUtil.isSlave()) {
//			log.info("prepare", "Currently HM is in HA Slave mode.");
//			return;
//		}
		try {
			versionInfo = getSessionVersionInfo();
			if (null == versionInfo) {
				versionInfo = NmsUtil.getVersionInfo();
				if (null != versionInfo) {
					setSessionVersionInfo(versionInfo);
				}
			}
		} catch (IllegalStateException ise) {
			log.error("prepare()", ise.getMessage());
		}
	}

	public String ssl() throws Exception {
		return "ssl";
	}
	
	public String blank() throws Exception {
		return "blankPage";
	}

	public String deny() throws Exception {
		try {
			List<HmAccessControl> list = QueryUtil.executeQuery(HmAccessControl.class, null,
					null);
			if (!list.isEmpty()) {
				HmAccessControl acl = list.get(0);
				if (acl.getDenyBehavior() == HmAccessControl.BEHAVIOR_TYPE_BLANK) {
					return "blankPage";
				} else {
					return "denyPage";
				}
			}
		} catch (Exception e) {
			log.error("deny", e.getMessage(), e);
		}
		return "blankPage";
	}

	/**
	 * TechOps/VHM(with no CID) user/ do login
	 * 
	 * @return
	 * @throws Exception
	 */
	public String login() throws Exception {
		long startTime = System.nanoTime();

		try {
			if (!NmsUtil.isHTTPEnable() && !"https".equals(request.getScheme())) {
				// SSL redirect (check this only in case of HTTP not enabled)
				return "ssl";
			} else {
				// get user from portal
				String errorCode = getUserInfoForSSO(HmDomain.PRODUCT_ID_VHM);
				
				// user info is valid
				if (null == errorCode) {
					return checkUserAndRedirect();
				} else {
					// need go to master
					if ("slave".equals(errorCode)) {
						redirectUrl = getMasterURL();
						return "redirect";
					} else if ("exception".equals(errorCode)) {
						redirectUrl = NmsUtil.getMyHiveServiceURL()+"/loginError.action?loginErrorMsg=error.authentication.credentials.bad";
						return "redirect";
					} else if ("login".equals(errorCode)) {
						return "login";
					} else {
						redirectUrl = NmsUtil.getMyHiveServiceURL()+"/loginError.action?loginErrorMsg="+errorCode;
						return "redirect";
					}
				}
			}
		} finally {
			long endTime = System.nanoTime();
			log.info("login", "Login completed. Time Elapsed: " + ((endTime - startTime) / 1000000) + " ms.");
		}
	}
	
	/*
	 * only used for cas login user
	 */
	public String checkUserAndRedirect() {
		redirectUrl = getSingleSignOutURL();
		userContext = getSessionUserContext();
		if (null == userContext) {
			return "redirect";
		}
		try {
			injectCSRFToken();
			
			initLicenseAndActKeyInfo();
			
			getLicenseMessage();

			// initial the message pool for current user
			setSessionNotificationMessagePool();

			// only developer can use windows system
			if (licenseInfo == null && os.toLowerCase().contains("windows")) {
				addActionError(MgrUtil
						.getUserMessage("error.licenseFailed.system.error"));
				userContext = null;
				return "redirect";
			}
			if (userContext.ifShowEualPage()) {
				versionInfo = getSessionVersionInfo();
				licTile = MgrUtil.getUserMessage("feature.end.user.license.agreement");

				// passive node cannot change any data
				if (HAUtil.isSlave()) {
					redirectUrl = getMasterURL();
					return "redirect";
				}
				return "eula";
			}

			String welcomePage = getContinuePage();

			if (null != welcomePage) {
				return welcomePage;
			}

			return landingPage();
		} catch (Exception e) {
			log.error("checkUserAndRedirect", e.getMessage(), e);
			return "redirect";
		}
	}
	
	public String logout() throws Exception {
		request.getSession().invalidate();

		// switch MyHive by click MyHive button
		if (NmsUtil.isHostedHMApplication() && null != operation && "switchMyHive".equals(operation)) {
			redirectUrl = getMyHivePage();
			return "redirect";
		} else {
			if (null != request.getRemoteUser()) {
				redirectUrl = getSingleSignOutURL();
				return "redirect";
			}
			return "login";
		}
	}

	public String authenticate() {
		try {
			userContext = HmBeAdminUtil.authenticate(userName, password);
			
			if (null == userContext) {
				addActionError(MgrUtil.getUserMessage(AUTH_FAILED));
				return "login";
			}
			
			injectCSRFToken();

			// only supper admin can access passive node
			if (HAUtil.isSlave() && !userContext.isSuperUser()) {
				redirectUrl = getMasterURL();
				return "redirect";
			}
			
			// not permit vad admin
			if (userContext.isVadAdmin()) {
				addActionError(MgrUtil.getUserMessage("error.authFailed.partnerUser"));
				userContext = null;
				return "login";
			}
			
			/*
			 * keep password in clear text for possible usage
			 */
			userContext.setPasswordInClearText(password);
			//createNavigationTree(userContext);
			userContext.createTableViews();
			userContext.createTableSizeMappings();
			userContext.createAutoRefreshMappings();
			setSessionUserContext(userContext);
			
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.user.login",userContext.getUserName()));
			userContext.setUserIpAddress(HmProxyUtil.getClientIp(request));
			request.getSession().setAttribute(userContext.getId().toString(),
				CurrentUserCache.getInstance());
			initSessionExpiration();
			
			mode = userContext.getMode();
			
			initLicenseAndActKeyInfo();
			
			String groupName = userContext.getUserGroup().getGroupName();
			
			// user manager license
			if (groupName.equals(HmUserGroup.GM_ADMIN) || groupName.equals(HmUserGroup.GM_OPERATOR)) {
				// check license and activation key
				if (HmBeLicenseUtil.GM_LITE_LICENSE_VALID) {
					// system license is invalid
					if (!getLicenseValidFlag()) {
						addActionError(MgrUtil.getUserMessage("error.authFailed.licenseInvalid"));
						userContext = null;
						return "login";
					} else if (!getIsInHomeDomain()) {
						// vhm does not support user manager
						if (!userContext.getOwner().isSupportGM()) {
							addActionError(MgrUtil.getUserMessage("error.authFailed.userManager"));
							userContext = null;
							return "login";
						}
					}
					
				// system does not support user manager
				} else {
					addActionError(MgrUtil.getUserMessage("error.authFailed.userManager"));
					userContext = null;
					return "login";
				}
			}
			
			// teacher view user
			if (HmUserGroup.TEACHER.equals(groupName)) {
				if (!NmsUtil.isTeacherViewEnabled(userContext)) {
					addActionError(MgrUtil.getUserMessage("error.authFailed.teacherView"));
					userContext = null;
					return "login";
				}
			}
			
			getLicenseMessage();
			
			refreshNavigationTree();
			
			// initial the message pool for current user
			setSessionNotificationMessagePool();
			
			// only developer can use windows system
			if (licenseInfo == null && os.toLowerCase().contains("windows")) {
				addActionError(MgrUtil
						.getUserMessage("error.licenseFailed.system.error"));
				userContext = null;
				return "login";
			}
			
			if (userContext.ifShowEualPage()) {
				versionInfo = getSessionVersionInfo();
				licTile = MgrUtil.getUserMessage("feature.end.user.license.agreement");
				
				// passive node cannot change any data
				if (HAUtil.isSlave()) {
					redirectUrl = getMasterURL();
					return "redirect";
				}
				return "eula";
			}
			
			String welcomePage = getContinuePage();
			
			if (null != welcomePage) {
				return welcomePage;
			}

			return landingPage();
		} catch (AhAuthException e) {
			addActionError(MgrUtil.getUserMessage(e.getMessage()));
			return "login";
		}
	}

	private String getContinuePage() {
		String groupName = userContext.getUserGroup().getGroupName();
		
		// GM light user
		if (!userContext.isPlannerUser() && !(groupName.equals(HmUserGroup.GM_ADMIN) 
			|| groupName.equals(HmUserGroup.GM_OPERATOR))) {
			
//			if (NmsUtil.isHostedHMApplication() && null != userContext.getCustomerId() && !userContext.getCustomerId().isEmpty() && !getIsInHomeDomain()) {
//				// passive node cannot change any data
//				if (HAUtil.isSlave()) {
//					redirectUrl = getMasterURL();
//					return "redirect";
//				}
//				return "startHere";
				// check HM mode has configured
//				HmStartConfig startConf = new HmStartConfig();
//				
//				List<HmStartConfig> list = QueryUtil.executeQuery(HmStartConfig.class, null, null, getDomain().getId());
//				if (!list.isEmpty()) {
//					startConf = list.get(0);
//				}
//				
//				if (list.isEmpty() || !startConf.isAdminUserLogin()) {
//					SettingsModel sm = new SettingsService().getStartSettings(userContext.getCustomerId());
//					if(null != sm && sm.getReturnCode() == 0){
//						networkName = getDomain().getDomainName();
//						hmModeType = sm.getModeType();
//						timezone = HmBeOsUtil.getServerTimeZoneIndex(sm.getTimeZone());	
//						startConf.setNetworkName(networkName);
//						startConf.setUseAccessConsole(true);
//						startConf.setModeType(hmModeType);
//						if (HmStartConfig.HM_MODE_FULL == hmModeType) {
//							quickPassword = sm.getSsidPwd();
//							startConf.setQuickStartPwd(quickPassword);
//							startConf.setAsciiKey(quickPassword);
//						} else {
//							startConf.setAsciiKey(sm.getAsciiKey());
//						}
//						
//						try {
//							boolean flag = StartHereAction.saveObject(startConf, adminPassword, getDomain().getId(), userContext, getIsInHomeDomain(), 
//								HmStartConfig.EXPRESS_MODE_DEFAULT_NTP_SERVER, timezone, this, HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP1,
//								HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP2, getDomain(), hmModeType, true, false);
//							
//							//Bug 25589 fix
//							if(!StartHereAction.updateAccessConsoleForQSWirelessOnlyNetworkPolicy(getDomain().getId(),startConf.getNetworkName())){
//								log.error("getContinuePage", " updateAccessConsoleForQSWirelessOnlyNetworkPolicy error");
//							}
//							
//							// generate log info
//							generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.create.global.setting", getDomain().getDomainName()));
//							addActionMessage(MgrUtil.getUserMessage(OBJECT_CREATED, "global settings of "+getDomain().getDomainName()));
//							
//							return flag ? null : "startHere";
//						} catch (Exception ex) {
//							generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.create.global.setting", getDomain().getDomainName()));
//							addActionError(MgrUtil.getUserMessage(ex));
//							return "startHere";
//						}
//					}
//				}
//			}
			// first configure start here page
			// fix bug 29327 clear database but has entitlement key, hm_start_config is empty in db but ignore in GlobalForwardInterceptor
			try {
				if (isNeedStartHereConfigure()) {
					// passive node cannot change any data
					if (HAUtil.isSlave()) {
						redirectUrl = getMasterURL();
						return "redirect";
					}
					return "startHere";
				}
			} catch (Exception ex) {
				addActionError(ex.getMessage());
				return "login";
			}
			// check license and activation key
			if (null != licenseInfo) {
				if (!vmwareConnectLsCheck) {
					setVmwareSystemWarnMsg();
				}
				if ((!BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM.equals(licenseInfo.getLicenseType()) 
						&& !BeLicenseModule.LICENSE_TYPE_DEVELOP_NUM.equals(licenseInfo.getLicenseType())
						&& !BeLicenseModule.LICENSE_TYPE_SIMPLE_VHM.equals(licenseInfo.getLicenseType()))
						|| null != vmwareConnectLsInfo || !"".equals(getSupportEvalSubsInfo())
						|| (licenseInfo.isUseActiveCheck() && ("".equals(primaryActKey) || !HmBeActivationUtil.ACTIVATION_KEY_VALID))) {
					versionInfo = getSessionVersionInfo();
					return "startHere";
				}
			}
		}
		return null;
	}

	public String licenseOverdue() {
		return "license";
	}

	public String license() throws Exception {
		userContext = getSessionUserContext();
		versionInfo = getSessionVersionInfo();
		
		if (!NmsUtil.isHTTPEnable() && !"https".equals(request.getScheme())) {
			// SSL redirect (check this only in case of HTTP not enabled)
			return "ssl";
		}
		// Authentication.
		if (null == userContext) {
			// Need to redirect to login.
			return logout();
		} else {
			mode = userContext.getMode();
		}
		// check if the user exist in user list
		boolean shouldLogin = true;
		for (HttpSession activeUser : CurrentUserCache.getInstance()
				.getActiveSessions()) {
			HmUser hmuser;
			try {
				hmuser=(HmUser)activeUser.getAttribute(USER_CONTEXT);
			} catch (Exception e) {
				log.error("license", e);
				continue;
			}
			if (hmuser!=null && hmuser.getId().longValue() == userContext.getId()) {
				shouldLogin = false;
				break;
			}
		}

		if (shouldLogin) {
			userContext = null;
			return logout();
		}
		if ("notAgree".equals(operation)) {
			if (NmsUtil.isHostedHMApplication() && null != userContext.getCustomerId() && !userContext.getCustomerId().isEmpty()) {
				redirectUrl = getMyHivePage();
				return "redirect";
			} else {
				userContext = null;
				return logout();
			}
		}
		initLicenseAndActKeyInfo();
		if ("import".equals(operation)) {
			if (importLicenseFile()) {
				/*
				 * check activation key if valid
				 */
				if (getLicenseValidFlag() && (!licenseInfo.isUseActiveCheck() || 
						HmBeActivationUtil.ACTIVATION_KEY_VALID)) {								
					//licTile = MgrUtil.getUserMessage("feature.end.user.license.agreement");
					return landingPage();
				} 
			}		
			return "license";
		} else if ("install".equals(operation)) {
			setDataSource(OrderHistoryInfo.class);
			if (installOrderKey()) {
				if (getLicenseValidFlag()) {								
					//licTile = MgrUtil.getUserMessage("feature.end.user.license.agreement");				
					return landingPage();
				} 
			}
			return "license";
		} else if ("activate".equals(operation)) {
			if (getShowTwoSystemId()) {
				if (!getShowPrimaryActKey()) {
					primaryActKey = "";
				}
				if (!getShowSecondaryActKey()) {
					secondaryActKey = "";
				}
			}
			
			if (activateActivationKey()) {
				/*
				 * check the license if valid
				 */
				if (getLicenseValidFlag()) {
					//licTile = MgrUtil.getUserMessage("feature.end.user.license.agreement");
					return landingPage();
				}
			}
			return "license";
			// the eula page
		} else if ("agree".equals(operation)) {
			userContext.setEndUserLicAgree(true);
			/*HmUser userObj = QueryUtil.findBoById(HmUser.class, userContext.getId());
			userObj.setEndUserLicAgree(true);
			QueryUtil.updateBo(userObj);*/
			// changed in Geneva, for user setting columns separated from hm_user
			UserSettingsUtil.updateEndUserLicAgree(getUserContext().getEmailAddress(), true);
			
			String welcomePage = getContinuePage();
			if (null != welcomePage) {
				return welcomePage;
			}
			return landingPage();
		} else if ("continue".equals(operation)) {
			//licTile = MgrUtil.getUserMessage("feature.end.user.license.agreement");
			return landingPage();
		} else if ("eula".equals(operation)) {
			licTile = MgrUtil.getUserMessage("feature.end.user.license.agreement");
			return "eula";
		} else if ("sendEmail".equals(operation)) {
			MailNotification mailNotify = QueryUtil.findBoByAttribute(
					MailNotification.class, "owner.domainName", HmDomain.HOME_DOMAIN);
			String keyInfo = ifVmware ? MgrUtil.getUserMessage("mail.entitlement.key") : MgrUtil.getUserMessage("mail.license.entitlement.key");
			String mailContent = MgrUtil.getUserMessage("mail.content.begin") + keyInfo + "."
				+ "\n\n"+MgrUtil.getUserMessage("mail.content.order.number")+"\t\t"
				+ orderId
				+ (getIsInHomeDomain() ? "\n"+MgrUtil.getUserMessage("mail.content.system.id")+"\t\t"
				+ BeLicenseModule.HIVEMANAGER_SYSTEM_ID : "\n" + getText("config.login.vhmid") + "\t\t"
					+ getDomain().getVhmID());
			if (!ifVmware && null != twoSystemId && twoSystemId.length == 2) {
				mailContent += "\n"+MgrUtil.getUserMessage("mail.content.secondary.system.id")+"\t"
				+ twoSystemId[1];
			}
			mailContent += "\n\n"+MgrUtil.getUserMessage("mail.content.license.info")+"\t"
				+ getLicenseMessage().replaceAll("<br>", "\n");
			if (null != activationMessage && !"".equals(activationMessage)) {
				mailContent += activationMessage;
			}

			SendMailUtil mailUtil = new SendMailUtil(mailNotify);
			mailUtil.setMailTo(NmsUtil.getOEMCustomer().getOrdersMail());
			mailUtil.setSubject(MgrUtil.getUserMessage("mail.subject",keyInfo)+NmsUtil.getOEMCustomer().getNmsName());
			mailUtil.setText(mailContent);
	
			try {
				mailUtil.startSend();
				addActionMessage(MgrUtil.getUserMessage("message.license.send.email.success"));
			} catch (Exception e) {
				addActionError(e.getMessage());
			}
			return "license";
		} else {
			return "license";
		}
	}

	private String userName, password;
	
	private String activationMessage;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLicenseMessage() {
		String feedDay = "";
		String feedtype = "a license";
		String feedAp;
		String sessionInfo = null;
		String resultStr = null;
		
		if (null != licenseInfo && null != userContext) {
			String lsType = "license";
			// the information of evaluation or vmware license
			if ("".equals(licenseInfo.getOrderKey()) && (licenseInfo.getLicenseType().equals(BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM)
					|| licenseInfo.getLicenseType().equals(BeLicenseModule.LICENSE_TYPE_VMWARE_NUM))) {
				int hours = licenseInfo.getLeftHours();		
				//int hours = 50;
				if (hours > 0) {
					int days = hours / 24;
					if (days < 31) {
						String timeLeft = days > 1 ? (days + " days remain") : "1 day remains";
						sessionInfo = MgrUtil.getUserMessage("info.license.entitlement.key.evaluation.or.support", new String[]{
							timeLeft, "evaluation "+lsType});
					}
					feedDay = MgrUtil.getUserMessage("info.license.permanent.entitlement.key.will.expired", new String[] { 
						"evaluation", (days > 1 ? (days + " days") : "1 day")});
				} else {
					sessionInfo = MgrUtil.getUserMessage("info.license.entitlement.key.expired", "evaluation "+lsType);
					feedDay = MgrUtil.getUserMessage("info.license.evaluation.entitlement.key.expired");
					display = "none";
				}
				feedtype = (licenseInfo.getLicenseType().equals(BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM) ?
						"an evaluation " : "a VMWare ") +lsType;	
			// no license or order key
			} else if (licenseInfo.getLicenseType().equals(BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY)){
				int period = licenseInfo.getLeftHours();
				
				if (period > 0) {
					String timeLeft = getDayLeftStr(period);
					resultStr = MgrUtil.getUserMessage("info.license.activation.key.input", new String[] { getTitleParam(), timeLeft});
					sessionInfo = MgrUtil.getUserMessage("info.license.entitlement.key.not.input", new String[] { getTitleParam(), timeLeft});
				} else {
					display = "none";
					resultStr = MgrUtil.getUserMessage("error.license.activation.key.input", getTitleParam());	
				}
			} else {
				if (licenseInfo.getHiveAps() <= 0 && licenseInfo.getCvgNumber() <= 0 && !licenseInfo.isZeroDeviceKeyValid()) {
					resultStr = MgrUtil.getUserMessage("error.license.activation.key.input", getTitleParam());
					display = "none";
				}
				
				feedDay = getSupportEvalSubsInfo();
				sessionInfo = getSupportEvalSubsInfo();
			}
			// check if connect with license server
			if (!vmwareConnectLsCheck) {
				setVmwareSystemWarnMsg();
			}
			if (null != vmwareConnectLsInfo)
				resultStr = vmwareConnectLsInfo;
			if (null != sessionInfo && !NmsUtil.isDemoHHM() && !NmsUtil.isPlanner()) {
				MgrUtil.setSessionAttribute(LICENSE_INFO_IN_TITLE_AREA, sessionInfo);
			} else {
				MgrUtil.removeSessionAttribute(LICENSE_INFO_IN_TITLE_AREA);
			}
			
			if (null == resultStr) {
				int hiveApNumber = licenseInfo.getHiveAps();
				int vhmNum = licenseInfo.getVhmNumber();
				int cvgNum = licenseInfo.getCvgNumber();
				
				if (vhmNum > 1) {
					if (cvgNum > 0) {
						if(hiveApNumber >1 && cvgNum >1 && vhmNum >1){
							feedAp = MgrUtil.getUserMessage("info.license.hiveApCount.cvgCount.vhmCount",
									new String[] { feedtype, String.valueOf(hiveApNumber),
											String.valueOf(cvgNum), String.valueOf(vhmNum) });
						}else if(hiveApNumber <= 1 && cvgNum <= 1 && vhmNum <= 1){
							feedAp = MgrUtil.getUserMessage("info.license.hiveApCount.cvgCount.vhmCount.single.all",
									new String[] { feedtype, String.valueOf(hiveApNumber),
											String.valueOf(cvgNum), String.valueOf(vhmNum) });
						}else if(hiveApNumber >1 && cvgNum <= 1 && vhmNum <= 1){
							feedAp = MgrUtil.getUserMessage("info.license.hiveApCount.cvgCount.vhmCount.single.cvg.vhm",
									new String[] { feedtype, String.valueOf(hiveApNumber),
											String.valueOf(cvgNum), String.valueOf(vhmNum) });
						}else if(hiveApNumber <= 1 && cvgNum > 1 && vhmNum <= 1){
							feedAp = MgrUtil.getUserMessage("info.license.hiveApCount.cvgCount.vhmCount.single.device.vhm",
									new String[] { feedtype, String.valueOf(hiveApNumber),
											String.valueOf(cvgNum), String.valueOf(vhmNum) });
						}else if(hiveApNumber <= 1 && cvgNum <= 1 && vhmNum > 1){
							feedAp = MgrUtil.getUserMessage("info.license.hiveApCount.cvgCount.vhmCount.single.device.cvg",
									new String[] { feedtype, String.valueOf(hiveApNumber),
											String.valueOf(cvgNum), String.valueOf(vhmNum) });
						}else if(hiveApNumber <= 1 && cvgNum > 1 && vhmNum > 1){
							feedAp = MgrUtil.getUserMessage("info.license.hiveApCount.cvgCount.vhmCount.single.device",
									new String[] { feedtype, String.valueOf(hiveApNumber),
											String.valueOf(cvgNum), String.valueOf(vhmNum) });
						}else if(hiveApNumber > 1 && cvgNum <= 1 && vhmNum > 1){
							feedAp = MgrUtil.getUserMessage("info.license.hiveApCount.cvgCount.vhmCount.single.cvg",
									new String[] { feedtype, String.valueOf(hiveApNumber),
											String.valueOf(cvgNum), String.valueOf(vhmNum) });
						}else{
							feedAp = MgrUtil.getUserMessage("info.license.hiveApCount.cvgCount.vhmCount.single.vhm",
									new String[] { feedtype, String.valueOf(hiveApNumber),
											String.valueOf(cvgNum), String.valueOf(vhmNum) });
						}
						
					} else {
						if(hiveApNumber >1 && vhmNum >1){
							feedAp = MgrUtil.getUserMessage("info.license.vhmCount",
									new String[] { feedtype, String.valueOf(hiveApNumber),
											String.valueOf(vhmNum) });
						}else if(hiveApNumber >1 && vhmNum <=1){
							feedAp = MgrUtil.getUserMessage("info.license.vhmCount.single.vhm",
									new String[] { feedtype, String.valueOf(hiveApNumber),
											String.valueOf(vhmNum) });
						}else if(hiveApNumber <= 1 && vhmNum > 1){
							feedAp = MgrUtil.getUserMessage("info.license.vhmCount.single.device",
									new String[] { feedtype, String.valueOf(hiveApNumber),
											String.valueOf(vhmNum) });
						}else{
							feedAp = MgrUtil.getUserMessage("info.license.vhmCount.single.all",
									new String[] { feedtype, String.valueOf(hiveApNumber),
											String.valueOf(vhmNum) });
						}
					}
				} else {
					if (cvgNum > 0) {
						if(hiveApNumber >1 && cvgNum > 1){
							feedAp = MgrUtil.getUserMessage("info.license.hiveApCount.cvgCount",
									new String[] { feedtype, String.valueOf(hiveApNumber),
										String.valueOf(cvgNum)});
						}else if(hiveApNumber <=1 && cvgNum > 1){
							feedAp = MgrUtil.getUserMessage("info.license.hiveApCount.cvgCount.single.device",
									new String[] { feedtype, String.valueOf(hiveApNumber),
										String.valueOf(cvgNum)});
						}else if(hiveApNumber >1 && cvgNum <= 1){
							feedAp = MgrUtil.getUserMessage("info.license.hiveApCount.cvgCount.single.cvg",
									new String[] { feedtype, String.valueOf(hiveApNumber),
										String.valueOf(cvgNum)});
						}else{
							feedAp = MgrUtil.getUserMessage("info.license.hiveApCount.cvgCount.single.device.cvg",
									new String[] { feedtype, String.valueOf(hiveApNumber),
										String.valueOf(cvgNum)});
						}
						
					} else {
						if(hiveApNumber > 1){
							feedAp = MgrUtil.getUserMessage("info.license.hiveApCount",
									new String[] { feedtype, String.valueOf(hiveApNumber) });
						}else{
							feedAp = MgrUtil.getUserMessage("info.license.hiveApCount.single",
									new String[] { feedtype, String.valueOf(hiveApNumber) });
						}
					}
				}
				if (licenseInfo.isUseActiveCheck()) {
					if (null != activationInfo) {
						// there is no activation key information
						if ("".equals(activationInfo.getActivationKey())) {
							int period = activationInfo.getQueryPeriodLeft();
							if (period > 0) {
								String timeLeft = getDayLeftStr(period);
								activationMessage = MgrUtil.getUserMessage("info.license.activation.key.input", new String[] { "activation key", timeLeft});
							} else {
								display = "none";
								activationMessage = MgrUtil.getUserMessage("error.license.activation.key.input", "activation key");	
							}
						// show the warning message
						} else {
							if (!HmBeActivationUtil.ACTIVATION_KEY_VALID) {
								display = "none";
								activationMessage = MgrUtil.getUserMessage("error.license.activation.key.input", "activation key");
							} else {
								activationMessage = MgrUtil.getUserMessage("info.license.activation.key.query");
							}
						}
						if (ifVmware) {
							activationMessage = activationMessage.replaceAll("activation key", "entitlement key");
						}
					}
				}
				if ("".equals(feedAp)) {
					resultStr = ("".equals(feedDay) ? "" : feedDay) + "<br>";
				} else {
					resultStr = ("".equals(feedDay) ? feedAp : (feedAp + "<br>" + feedDay)) + "<br>";
				}
				if (ifVmware && getIsInHomeDomain() && null != activationMessage && !"".equals(activationMessage)) {
					resultStr += activationMessage;
				}
			}
		}
		return resultStr;
	}
	
	private void setVmwareSystemWarnMsg() {
		vmwareConnectLsCheck = true;
		vmwareConnectLsInfo = null;
		// check if connect with license server
		if (ifVmware && !NmsUtil.isHostedHMApplication() && !getNoLicense()) {
			List<?> bos = QueryUtil.executeQuery("select enableProxy, proxyServer, proxyPort, proxyUserName, proxyPassword from "+HMServicesSettings.class.getSimpleName(), null,
					new FilterParams("owner.domainName", HmDomain.HOME_DOMAIN), 1);
			boolean enableProxy = false;
			String errMsg = MgrUtil.getUserMessage("info.license.orderkey.from.license.server", HmBeActivationUtil.getLicenseServerInfo().getLserverUrl());
			String proServer = null;
			int proPort = 0;
			String proUser = null;
			String proPwd = null;
			if (!bos.isEmpty()) {
				// get proxy information
				Object[] settings = (Object[])bos.get(0);
				enableProxy = (Boolean)settings[0];
				proServer = (String)settings[1];
				proPort = (Integer)settings[2];
				proUser = (String)settings[3];
				proPwd = (String)settings[4];
			}
			// check license server connection
			String result = ClientSenderCenter.testForConnectingToLS(enableProxy, proServer,
					proPort, proUser, proPwd);
			if (!HmBeResUtil.getString("connecttest.tols.success").equals(result)) {
				vmwareConnectLsInfo = errMsg;
			}
		}
	}
	
	public String getActivationMessage() {
		return activationMessage;
	}

	public String getInforDisplay() {
		return display;
	}
	
	public String getActivationDisplay() {
		if (null != licenseInfo && null != userContext && !ifVmware) {
			if (licenseInfo.isUseActiveCheck() && null != activationInfo) {
				if ("".equals(activationInfo.getActivationKey())) {
					return "";
				} else if (!HmBeActivationUtil.ACTIVATION_KEY_VALID) {
					return "";
				}
			}
		} 
		return "none";
	}
	
	public boolean getShowPrimaryActKey() {
		if (null != allActiveInfo && getShowTwoSystemId()) {
			for (ActivationKeyInfo actInfo : allActiveInfo) {
				if (BeLicenseModule.HIVEMANAGER_SYSTEM_ID.equals(actInfo.getSystemId())) {
					return !actInfo.isStartRetryTimer() && !actInfo.isActivateSuccess()
							&& HM_License.getInstance().isVirtualMachineSystem();
				}
			}
		}
		return false;
	}
	
	public boolean getShowSecondaryActKey() {
		if (null != allActiveInfo && getShowTwoSystemId()) {
			for (ActivationKeyInfo actInfo : allActiveInfo) {
				if (!BeLicenseModule.HIVEMANAGER_SYSTEM_ID.equals(actInfo.getSystemId())) {
					return !actInfo.isStartRetryTimer() && !actInfo.isActivateSuccess()
							&& HM_License.getInstance().isVirtualMachineSystem();
				}
			}
		}
		return false;
	}

	private String landingPage() {
		return landingPage(null);
	}
	
	private String landingPage(String defaultOper) {
		List<NavigationNode> accessibleL1Feature = userContext
			.getNavigationTree().getChildNodes();

		for (NavigationNode l1Node : accessibleL1Feature) {
			if (null != defaultOper) {
				if (defaultOper.equals(l1Node.getKey())){
					operation = defaultOper;
					break;
				} else {
					operation = l1Node.getKey();
				}
			} else {
				operation = l1Node.getKey();// set the accessible L1 feature
				// fnr change it default is dashboard when not hmol
				if (isHMOnline()) {
					if (Navigation.L1_FEATURE_CONFIGURATION.equals(l1Node.getKey())) {
						// while contain the default L1 feature, select this one
						break;
					}
				} else {
					if (Navigation.L1_FEATURE_DASH.equals(l1Node.getKey())) {
						// while contain the default L1 feature, select this one
						break;
					}
				}
			}
		}
		if (HmUpgradeLog.isNeedRedirectPage(userContext)) {
			return L2_FEATURE_UPDATE_SOFTWARE;
		}
		return SUCCESS;
	}
	
	public boolean getHasConfigEmail() {
		MailNotification mailNotify = QueryUtil.findBoByAttribute(
				MailNotification.class, "owner.domainName", HmDomain.HOME_DOMAIN);
		return !(mailNotify == null || "".equals(mailNotify.getServerName()) || "".equals(mailNotify.getMailFrom()));
	}
	
	public String getEmailMessage() {
		return HmDomain.HOME_DOMAIN.equals(userContext.getDomain().getDomainName()) ? "four- or five-digit sales order number and system ID" : "four- or five-digit sales order number and V"+NmsUtil.getOEMCustomer().getNmsNameAbbreviation()+" ID";
	}
	
	private String orderId;
	
	public boolean getNoLicense() {
		return null != licenseInfo && BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(licenseInfo.getLicenseType());
	}
	
	public String licTile = MgrUtil.getUserMessage("feature.license");

	public String getLicTile() {
		return licTile;
	}

	public void setLicTile(String licTile) {
		this.licTile = licTile;
	}
	
	public String redirectUrl;

	public String getRedirectUrl()
	{
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl)
	{
		this.redirectUrl = redirectUrl;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	public boolean getLicenseValidFlag() {
		if (null == userContext || getIsInHomeDomain() || !NmsUtil.isHostedHMApplication()) {
			return HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID == HmBeLicenseUtil.LICENSE_VALID
			|| HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID == HmBeLicenseUtil.NO_LICENSE_HAS_PERIOD;
		// for vhm in hm online
		} else {
			if (licenseInfo.getTotalDays() > 0 && licenseInfo.getLeftHours() <= 0) {
				return false;
			} else if (licenseInfo.getHiveAps() <= 0 && licenseInfo.getCvgNumber() <= 0 && !licenseInfo.isZeroDeviceKeyValid()) {
				return false;
			}
			return true;
		}
	}
	
	public String getHmIdInfo() {
		if (!getIsInHomeDomain() && NmsUtil.isHostedHMApplication()) {
			return getText("config.login.vhmid") + " : "+getDomain().getVhmID();
		}
		return NmsUtil.getOEMCustomer().getNmsName()+" System ID : "+getSystemId();
	}
	
	public String getOrderEmail() {
		return NmsUtil.getOEMCustomer().getOrdersMail();
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
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
		if (bo instanceof HmUserGroup) {
			HmUserGroup group = (HmUserGroup) bo;
			if (group.getFeaturePermissions() != null) {
				group.getFeaturePermissions().size();
			}
			if (group.getInstancePermissions() != null) {
				group.getInstancePermissions().size();
			}
		}
		/*
		 * For welcome page
		 */
		if(bo instanceof MgmtServiceDns) {
			MgmtServiceDns dns = (MgmtServiceDns)bo;

			if(dns.getDnsInfo() != null) {
				dns.getDnsInfo().size();
			}
		}

		if(bo instanceof MgmtServiceTime) {
			MgmtServiceTime time = (MgmtServiceTime)bo;

			if(time.getTimeInfo() != null) {
				time.getTimeInfo().size();
			}
		}
		return null;
	}

	/**
	 * Welcome Page
	 */
	private String networkName;
	
	private String adminPassword;
	
	private String hiveApPassword;
	
	private String quickPassword;
	
	public String getQuickPassword()
	{
		return quickPassword;
	}

	public void setQuickPassword(String quickPassword)
	{
		this.quickPassword = quickPassword;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}
	
	private int timezone;
	
	public int getTimezone()
	{
		return timezone;
	}

	public void setTimezone(int timezone)
	{
		this.timezone = timezone;
	}

	public String getNetworkName()
	{
		return networkName;
	}

	public void setNetworkName(String networkName)
	{
		this.networkName = networkName;
	}
	
	public EnumItem[] getEnumTimeZone() {
		return HmBeOsUtil.getEnumsTimeZone();
	}
	
	public String getShowUpConfig() throws Exception {
		return isNeedStartHereConfigure() ? "" : "none";
	}
	
	public String startHere() throws Exception {
		userContext = getSessionUserContext();
		versionInfo = getSessionVersionInfo();
		
		if (!NmsUtil.isHTTPEnable() && !"https".equals(request.getScheme())) {
			// SSL redirect (check this only in case of HTTP not enabled)
			return "ssl";
		}
		// Authentication.
		if (null == userContext) {
			// Need to redirect to login.
			return logout();
		} else {
			mode = userContext.getMode();
		}
		// check if the user exist in user list
		boolean shouldLogin = true;
		for (HttpSession activeUser : CurrentUserCache.getInstance()
				.getActiveSessions()) {
			HmUser hmuser;
			try {
				hmuser=(HmUser)activeUser.getAttribute(USER_CONTEXT);
			} catch (Exception e) {
				log.error("startHere", e);
				continue;
			}
			if (hmuser!=null && hmuser.getId().longValue() == userContext.getId()) {
				shouldLogin = false;
				break;
			}
		}

		if (shouldLogin) {
			userContext = null;
			return logout();
		}
		initLicenseAndActKeyInfo();
		
		if (isNeedStartHereConfigure() && !"continue".equals(operation)) {
			initStartHereValues();
		}
		
		licTile = MgrUtil.getUserMessage("feature.welcome.page.title");
		
		if ("continueKey".equals(operation)) {
			// remove the expired entitle key info in session
			MgrUtil.removeSessionAttribute(SessionKeys.LICENSE_INFO_IN_TITLE_AREA);
			// fnr change it default is dashboard when not hmol
			if (isHMOnline()) {
				return landingPage(L1_FEATURE_CONFIGURATION);
			} else {
				return landingPage(L1_FEATURE_DASH);
			}
		} else if ("continue".equals(operation)) {
			if (isNeedStartHereConfigure()) {	
				HmStartConfig startConf = new HmStartConfig();
				
				List<HmStartConfig> list = QueryUtil.executeQuery(HmStartConfig.class,null, null, getDomain().getId());
				if (!list.isEmpty()) {
					startConf = list.get(0);
				}
				
				if (getDisplayEnterprise()) {
					startConf.setModeType(HmStartConfig.HM_MODE_FULL);
					startConf.setHiveApPassword(hiveApPassword);
					hmModeType = HmStartConfig.HM_MODE_FULL;
				} else {
					startConf.setNetworkName(networkName);
					startConf.setAsciiKey(adminPassword);
					startConf.setHiveApPassword(adminPassword);
					startConf.setUseAccessConsole(true);
					startConf.setModeType(hmModeType);
					if (HmStartConfig.HM_MODE_FULL == hmModeType) {
						if (StringUtils.isBlank(quickPassword)) {
							quickPassword = adminPassword;
						}
						startConf.setQuickStartPwd(quickPassword);
						startConf.setAsciiKey(quickPassword);
					}
				}
				
				try {
					boolean flag = StartHereAction.saveObject(startConf, adminPassword, getDomain().getId(), userContext, getIsInHomeDomain(), 
						HmStartConfig.EXPRESS_MODE_DEFAULT_NTP_SERVER, timezone, this, HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP1,
						HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP2, getDomain(), hmModeType,rebootFlag,false);
					
					//bug 25589 fix.
					if(!StartHereAction.updateAccessConsoleForQSWirelessOnlyNetworkPolicy(getDomain().getId(),startConf.getNetworkName())){
						log.error("execute", "operation:" + operation + " updateAccessConsoleForQSWirelessOnlyNetworkPolicy error");
					}
					
					// generate log info
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.create.global.setting", getDomain().getDomainName()));
					addActionMessage(MgrUtil.getUserMessage(OBJECT_CREATED, "global settings of "+getDomain().getDomainName()));
					
					if(getIsInHomeDomain() && rebootFlag){
						jsonObject = new JSONObject();
						if(flag){
							jsonObject.put("succ", true);
							jsonObject.put("message", HmBeResUtil.getString("datetime.update.success"));
							jsonObject.put("restart", true);
						}else{
							jsonObject.put("succ", false);
							jsonObject.put("message", HmBeResUtil.getString("datetime.update.error"));
						}
						return "json";
					}else{
						if(NmsUtil.isHostedHMApplication() 
								&& null != userContext.getCustomerId() 
								&& !userContext.getCustomerId().isEmpty() 
								&& !getIsInHomeDomain()){
							return landingPage(L1_FEATURE_CONFIGURATION);
						}
						return SUCCESS;
					}
					
				} catch (Exception ex) {
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.create.global.setting", getDomain().getDomainName()));
					addActionError(MgrUtil.getUserMessage(ex));
					// check if use new welcome page
					return getWelcomePageRedirectStr();
				}
			}
			// fnr change it default is dashboard when not hmol
			if (isHMOnline()) {
				return landingPage(L1_FEATURE_CONFIGURATION);
			} else {
				return landingPage(L1_FEATURE_DASH);
			}
		}else if ("restartHM".equals(operation)) {
			Thread.sleep(5000);
			boolean isSucc = HmBeAdminUtil.restartSoft();

			jsonObject = new JSONObject();
			jsonObject.put("succ", isSucc);

			return "json";
		}else {
			//skip the welcompage for MyHive
			// fix bug 29327
			Boolean restoreFlag = false;
			if (null != HmBeAdminUtil.VHM_RESTORE_STATUS_INFO.get(getDomain().getId())) {
				restoreFlag = HmBeAdminUtil.VHM_RESTORE_STATUS_INFO.get(getDomain().getId());
			}
			if(NmsUtil.isHostedHMApplication() 
					&& null != userContext.getCustomerId() 
					&& !userContext.getCustomerId().isEmpty() 
					&& !getIsInHomeDomain() && !restoreFlag){
				if (isNeedStartHereConfigure()) {	
					HmStartConfig startConf = new HmStartConfig();
					
					List<HmStartConfig> list = QueryUtil.executeQuery(HmStartConfig.class,null, null, getDomain().getId());
					if (!list.isEmpty()) {
						startConf = list.get(0);
					}
					
					if (getDisplayEnterprise()) {
						startConf.setModeType(HmStartConfig.HM_MODE_FULL);
						startConf.setHiveApPassword(hiveApPassword);
						hmModeType = HmStartConfig.HM_MODE_FULL;
					} else {
//						SettingsModel sm = new SettingsService().getStartSettings(userContext.getCustomerId());
//						if(null != sm  && sm.getReturnCode() == 0){
//							networkName = getDomain().getDomainName();
//							hmModeType = sm.getModeType();
//							timezone = HmBeOsUtil.getServerTimeZoneIndex(sm.getTimeZone());	
//							startConf.setNetworkName(networkName);
//							startConf.setUseAccessConsole(true);
//							startConf.setModeType(hmModeType);
//							if (HmStartConfig.HM_MODE_EASY == hmModeType) {
//								startConf.setAsciiKey(sm.getAsciiKey());
//							} else {
//								quickPassword = sm.getSsidPwd();
//								startConf.setQuickStartPwd(quickPassword);
//								startConf.setAsciiKey(quickPassword);
//							}
//							//set the flag for timezone change
//							rebootFlag = true;
//						} else {
							// check if use new welcome page
							return getWelcomePageRedirectStr();
//						}
					}
					
					try {
						StartHereAction.saveObject(startConf, adminPassword, getDomain().getId(), userContext, getIsInHomeDomain(), 
							HmStartConfig.EXPRESS_MODE_DEFAULT_NTP_SERVER, timezone, this, HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP1,
							HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP2, getDomain(), hmModeType,rebootFlag,false);
						// generate log info
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.create.global.setting" , getDomain().getDomainName() ));
						addActionMessage(MgrUtil.getUserMessage(OBJECT_CREATED, "get settings from "+getDomain().getDomainName()));
						return landingPage(L1_FEATURE_CONFIGURATION);
					} catch (Exception ex) {
						generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.create.global.setting" , getDomain().getDomainName() ));
						addActionError(MgrUtil.getUserMessage(ex));
						// check if use new welcome page
						return getWelcomePageRedirectStr();
					}
				}
			}
			// check if use new welcome page
			return getWelcomePageRedirectStr();
		}
	}

	private NewStartHereForm newStart;
	public NewStartHereForm getNewStart() {
		return newStart;
	}

	public void setNewStart(NewStartHereForm newStart) {
		this.newStart = newStart;
	}

	public String getWelcomePageRedirectStr() throws Exception{
		if (isHMOnline() && getIsInHomeDomain()) {
			return "welcomePage";
		} else {
			if (isNeedStartHereConfigure()) {
				//return "welcomePage";
				return "newWelcomePageAction";
			} else {
				return "welcomePage";
			}
		}
	}

	public EnumItem[] getModeTypeNew1() {
		return new EnumItem[] { new EnumItem(HmStartConfig.HM_MODE_EASY,
				MgrUtil.getEnumString("enum.hm.welcome.page.hm.mode.new."
						+ HmStartConfig.HM_MODE_EASY)) };
	}

	public EnumItem[] getModeTypeNew2() {
		return new EnumItem[] { new EnumItem(HmStartConfig.HM_MODE_FULL,
				MgrUtil.getEnumString("enum.hm.welcome.page.hm.mode.new."
						+ HmStartConfig.HM_MODE_FULL)) };
	}
	public String newStartHere() throws Exception {
		userContext = getSessionUserContext();
		versionInfo = getSessionVersionInfo();

		if (!NmsUtil.isHTTPEnable() && !"https".equals(request.getScheme())) {
			// SSL redirect (check this only in case of HTTP not enabled)
			return "ssl";
		}
		// Authentication.
		if (null == userContext) {
			// Need to redirect to login.
			return logout();
		} else {
			mode = userContext.getMode();
		}
		// check if the user exist in user list
		boolean shouldLogin = true;
		for (HttpSession activeUser : CurrentUserCache.getInstance()
				.getActiveSessions()) {
			HmUser hmuser;
			try {
				hmuser=(HmUser)activeUser.getAttribute(USER_CONTEXT);
			} catch (Exception e) {
				log.error("startHere", e);
				continue;
			}
			if (hmuser!=null && hmuser.getId().longValue() == userContext.getId()) {
				shouldLogin = false;
				break;
			}
		}

		if (shouldLogin) {
			userContext = null;
			return logout();
		}

		licTile = MgrUtil.getUserMessage("hm.missionux.wecomle.title.welcome", getSystemNmsName());

		if ("fetchDeviceNumber".equals(operation)) {
			jsonObject = new JSONObject();
			try {
				if (isHMOnline()) {
					RedirectorResUtils ru = ClientUtils.getRedirectorResUtils();
					DeviceCounts dc= new DeviceCounts();
					if (getDomain().getVhmID()!=null) {
						dc= ru.getDeviceCounts(getDomain().getVhmID());
					}
					jsonObject.put("n_total", dc.getApCounts() + dc.getBrCounts() + dc.getSrCounts()+ dc.getCvgCounts());
					jsonObject.put("n_ap", dc.getApCounts());
					jsonObject.put("n_br", dc.getBrCounts());
					jsonObject.put("n_sw", dc.getSrCounts());
					jsonObject.put("n_cvg", dc.getCvgCounts());
				} else {
					initDeviceNumberInPage();
					jsonObject.put("n_total", conTotalNum);
					jsonObject.put("n_ap", conApNum);
					jsonObject.put("n_br", conBrNum);
					jsonObject.put("n_sw", conSwNum);
					jsonObject.put("n_cvg", conCvgNum);
				}
				jsonObject.put("t", true);
			} catch (Exception e) {
				log.error(e);
				jsonObject.put("t", false);
			}
			return "json";
		 } else if ("activate_license".equals(operation)) {
			initLicenseAndActKeyInfo();
			if (StringUtils.isNotBlank(this.primaryOrderKey)) {
				primaryOrderKey = this.primaryOrderKey.trim();
				final String enteredEntitlementKey = this.primaryOrderKey;
				installEntitleKeyInTop(new LicenseReturnedInfoEncap() {
					@Override
					public void encap(LicenseInfo licenseInfo, JSONObject jsonObject)
							throws JSONException {
						jsonObject.put("welmsg", getOrderedEntitlementKeyInfo(enteredEntitlementKey));
						jsonObject.put("key", enteredEntitlementKey);
						
						// this method is called when a valid license is entered, avoid to show message on top of page at first time
						MgrUtil.removeSessionAttribute(SessionKeys.LICENSE_INFO_IN_TITLE_AREA);
					}
				});
			} else {
				primaryLicense = this.primaryLicense.trim();
				final String enteredLicenseKey = this.primaryLicense;
				installEntitleKeyInTop(new LicenseReturnedInfoEncap() {
					@Override
					public void encap(LicenseInfo licenseInfo, JSONObject jsonObject)
							throws JSONException {
						jsonObject.put("welmsg", NewStartHereForm.getOrderedLicenseKeyInfo(enteredLicenseKey, licenseInfo));
						jsonObject.put("key", enteredLicenseKey);
						
						// this method is called when a valid license is entered, avoid to show message on top of page at first time
						MgrUtil.removeSessionAttribute(SessionKeys.LICENSE_INFO_IN_TITLE_AREA);
					}
				});
			}
			return "json";
		} else if ("getTimezoneTime".equals(operation)) {
			jsonObject = new JSONObject();
			jsonObject.put("result", true);
			jsonObject.put("time", this.getNewStart().getCurrentTimeWithTimezone());
			return "json";
		} else if ("configDone".equals(operation)) {
			if (isNeedStartHereConfigure()) {	
				HmStartConfig startConf = new HmStartConfig();
				
				List<HmStartConfig> list = QueryUtil.executeQuery(HmStartConfig.class,null, null, getDomain().getId());
				if (!list.isEmpty()) {
					startConf = list.get(0);
				}
				
				if (getDisplayEnterprise()) {
					startConf.setModeType(HmStartConfig.HM_MODE_FULL);
					//startConf.setHiveApPassword(hiveApPassword);
					startConf.setHiveApPassword(adminPassword);
					hmModeType = HmStartConfig.HM_MODE_FULL;
				} else {
					startConf.setNetworkName(networkName);
					startConf.setModeType(hmModeType);
					startConf.setUseAccessConsole(true);
					 
					// vhm with customer id no need to set hivemanager password
					// cannot get the password need input manually
					if (NmsUtil.isHostedHMApplication() && !StringUtils.isBlank(userContext.getCustomerId()) && StringUtils.isBlank(adminPassword)) {
						if (StringUtils.isBlank(quickPassword)) {
							addActionError(MgrUtil.getUserMessage("error.new.welcome.management.settings.access.pwd"));
							return "welcomePage";
						} else {
							startConf.setAsciiKey(quickPassword);
							if (HmStartConfig.HM_MODE_FULL == hmModeType) {
								startConf.setQuickStartPwd(quickPassword);
							}
						}
					} else {
						startConf.setAsciiKey(adminPassword);
						startConf.setHiveApPassword(adminPassword);
						if (HmStartConfig.HM_MODE_FULL == hmModeType) {
							if (StringUtils.isBlank(quickPassword)) {
								quickPassword = adminPassword;
							}
							startConf.setQuickStartPwd(quickPassword);
							startConf.setAsciiKey(quickPassword);
						}
					}
				}
				
				try {
					boolean flag = StartHereAction.saveObject(startConf, adminPassword, getDomain().getId(), userContext, getIsInHomeDomain(), 
						HmStartConfig.EXPRESS_MODE_DEFAULT_NTP_SERVER, this.getNewStart().getTimeZoneIdx(), this, 
						HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP1,
						HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP2, getDomain(), hmModeType,rebootFlag,false);
					
					// generate log info
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.create.global.setting", getDomain().getDomainName()));
					addActionMessage(MgrUtil.getUserMessage(OBJECT_CREATED, "global settings of "+getDomain().getDomainName()));
					
					if(getIsInHomeDomain() && rebootFlag){
						jsonObject = new JSONObject();
						if(flag){
							jsonObject.put("succ", true);
							jsonObject.put("message", HmBeResUtil.getString("datetime.update.success"));
							jsonObject.put("restart", true);
						}else{
							jsonObject.put("succ", false);
							jsonObject.put("message", HmBeResUtil.getString("datetime.update.error"));
						}
						return "json";
					}else{
						return "welcomePageDone";
					}
					
				} catch (Exception ex) {
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.create.global.setting", getDomain().getDomainName()));
					addActionError(MgrUtil.getUserMessage(ex));
					return "welcomePage";
				}
			}
		} else if ("startUsingHM".equals(operation)) {
			return landingPage(L1_FEATURE_CONFIGURATION);
		} else if ("restartHM".equals(operation)) {
			Thread.sleep(5000);
			boolean isSucc = HmBeAdminUtil.restartSoft();

			jsonObject = new JSONObject();
			jsonObject.put("succ", isSucc);

			return "json";
		} else if ("checkRestart".equals(operation)) {
			jsonObject = new JSONObject();
			jsonObject.put("succ", true);
			return "json";
		} else {
			boolean blnNeedStartHereConfigure = isNeedStartHereConfigure();
			if (!blnNeedStartHereConfigure && !viewConfigPage) {
				// return to configuration done page
				return "welcomePageDone";
			}
			newStart = new NewStartHereForm();
			newStart.initializeArgs();
			
			initDeviceNumberInPage();
			initStartHereValues();
			getTimezoneInfoForNewStartHere();
			
			newStart.setOrderedKeys(getOrderedEntitlementKeyHistories());
			
			if (!HM_License.getInstance().isVirtualMachineSystem()) {
				newStart.setLicenseInfo(this.getOrderedLicenseKeyHistory());
			}
			
			if (this.hmModeType == HmStartConfig.HM_MODE_EASY) {
				newStart.setQuickStartSsidPwdDisplay("hidden");
			}
			
			// only fetch country info for hmol end user
			if (this.isHMOnline()) {
				try {
					VHMCustomerInfo vhmCustomer = ClientUtils.getPortalResUtils().getVHMCustomerInfo(getDomain().getVhmID());
					if (vhmCustomer != null && !StringUtils.isBlank(vhmCustomer.getCountry())) {
						newStart.setCountry(vhmCustomer.getCountry());
					}
				} catch (Exception e1) {
					log.error("FAILED to fetch country info from Portal", e1.getMessage(), e1);
				}
			}
			
			if(NmsUtil.isHostedHMApplication() && !StringUtils.isBlank(userContext.getCustomerId())) {
				newStart.setHivemanagerPwdDisplay("hidden");
				
				// vhm with customer id no need to set hivemanager password
				// get this password from MyHive
				SettingsModel sm = new SettingsService().getStartSettings(userContext.getCustomerId());
				if(null != sm  && sm.getReturnCode() == 0){
					adminPassword = Base64.decode(sm.getSsidPwd());
				}
				// cannot get the password need input manually
				newStart.setNoPwdFromMyHive(StringUtils.isBlank(adminPassword));
			}
			
			if(NmsUtil.isHostedHMApplication() 
					&& null != userContext.getCustomerId() 
					&& !userContext.getCustomerId().isEmpty() 
					&& !getIsInHomeDomain()){
				if (blnNeedStartHereConfigure) {	
					HmStartConfig startConf = new HmStartConfig();
					
					List<HmStartConfig> list = QueryUtil.executeQuery(HmStartConfig.class,null, null, getDomain().getId());
					if (!list.isEmpty()) {
						startConf = list.get(0);
					}
					
					if (getDisplayEnterprise()) {
						startConf.setModeType(HmStartConfig.HM_MODE_FULL);
						startConf.setHiveApPassword(hiveApPassword);
						hmModeType = HmStartConfig.HM_MODE_FULL;
					} else {
//						SettingsModel sm = new SettingsService().getStartSettings(userContext.getCustomerId());
//						if(null != sm  && sm.getReturnCode() == 0){
//							networkName = getDomain().getDomainName();
//							hmModeType = sm.getModeType();
//							this.getNewStart().setTimeZone(sm.getTimeZone());
//							startConf.setNetworkName(networkName);
//							startConf.setUseAccessConsole(true);
//							startConf.setModeType(hmModeType);
//							if (HmStartConfig.HM_MODE_EASY == hmModeType) {
//								startConf.setAsciiKey(sm.getAsciiKey());
//							} else {
//								quickPassword = sm.getSsidPwd();
//								startConf.setQuickStartPwd(quickPassword);
//								startConf.setAsciiKey(quickPassword);
//							}
//							//set the flag for timezone change
//							rebootFlag = true;
//						} else {
							return "welcomePage";
//						}
					}
					
					try {
						StartHereAction.saveObject(startConf, adminPassword, getDomain().getId(), userContext, getIsInHomeDomain(), 
							HmStartConfig.EXPRESS_MODE_DEFAULT_NTP_SERVER, timezone, this, HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP1,
							HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP2, getDomain(), hmModeType,rebootFlag,false);
						// generate log info
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.create.global.setting" , getDomain().getDomainName() ));
						addActionMessage(MgrUtil.getUserMessage(OBJECT_CREATED, "get settings from "+getDomain().getDomainName()));
						return landingPage(L1_FEATURE_CONFIGURATION);
					} catch (Exception ex) {
						generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.create.global.setting" , getDomain().getDomainName() ));
						addActionError(MgrUtil.getUserMessage(ex));
						return "welcomePage";
					}
				}
			}
		}

		return "welcomePage";
	}
	private boolean viewConfigPage; 
	
	public boolean isViewConfigPage() {
		return viewConfigPage;
	}

	public void setViewConfigPage(boolean viewConfigPage) {
		this.viewConfigPage = viewConfigPage;
	}
	
	private List<OrderHistoryInfo> getOrderedEntitlementKeyHistories() {
		return QueryUtil.executeQuery(OrderHistoryInfo.class, new SortParams("activetime"), 
				new FilterParams("licenseType != :s1 AND domainName = :s2 AND (statusFlag = :s3 OR cvgStatusFlag = :s4)", 
						new Object[]{BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY, 
										getDomain().getDomainName(),
										OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL,
										OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL
										}));
	}
	
	private LicenseInfo getOrderedLicenseKeyHistory() {
		HM_License hm_l = HM_License.getInstance();
		String systemId = hm_l.get_system_id();
		String where = "active is true AND systemId = :s1 AND type = :s2";
		Object[] values = new Object[2];
		values[0] = systemId;
		values[1] = LicenseHistoryInfo.LICENSE_TYPE_HIVEMANAGER;
		List<LicenseHistoryInfo> licenseHistories = QueryUtil.executeQuery(LicenseHistoryInfo.class, null,
				new FilterParams(where, values));
		if (licenseHistories != null
				&& licenseHistories.size() > 0) {
			LicenseHistoryInfo licenseHistory = licenseHistories.get(0);
			String licenseKey = licenseHistory.getLicenseString();
			
			licenseKey = hm_l.decrypt_from_string(systemId, licenseKey);
			String type = licenseKey.substring(0, BeLicenseModule.LICENSE_TYPE_INDEX);
			int hiveAPs = Integer.parseInt(licenseKey.substring(BeLicenseModule.LICENSE_TYPE_INDEX,
					BeLicenseModule.LICENSE_HIVEAP_NUM_INDEX));
			int hours = Integer.parseInt(licenseKey.substring(BeLicenseModule.LICENSE_HIVEAP_NUM_INDEX,
					BeLicenseModule.LICENSE_KEY_LENGTH));
			
			LicenseInfo licenseInfo = new LicenseInfo();
			licenseInfo.setLicenseType(type);
			licenseInfo.setOrderKey(licenseHistory.getLicenseString());
			licenseInfo.setHiveAps(hiveAPs);
			licenseInfo.setTotalDays(hours / 24);
			licenseInfo.setLeftHours(hours);
			
			return licenseInfo;
		}
		
		return null;
	}
	
	private String getOrderedEntitlementKeyInfo(String primaryKey) {
		String result = "";
		if (StringUtils.isNotBlank(primaryKey)) {
			OrderHistoryInfo curOrder = QueryUtil.findBoByAttribute(OrderHistoryInfo.class, "orderKey", primaryKey);
			if (curOrder != null) {
				result = MgrUtil.getUserMessage("geneva_31.hm.missionux.wecomle.license.succ.license.single.info"
							, String.valueOf(curOrder.getNumberOfAps()));
				if (curOrder.getNumberOfEvalValidDays() > 1) {
					result += " for <span class='key_total_days'>" + curOrder.getNumberOfEvalValidDays() + " days</span>";
				} else if (curOrder.getSubEndDate() == 0 && curOrder.getSupportEndDate() == 0) {
					result += " for <span class='key_total_days'>" + curOrder.getNumberOfEvalValidDays() + " day</span>";
				} else if (curOrder.getSupportEndDate() > 0) {
					result += " to <span class='key_total_days'>" + curOrder.getSupportEndTimeStr() + "</span>";
				} else {
					result += " to <span class='key_total_days'>" + curOrder.getSubEndTimeStr() + "</span>";
				}
			}
		}
		
		return result;
	}
	
	private void getTimezoneInfoForNewStartHere() {
		//for HMOL customer, get country info from MyHive
		if (NmsUtil.isHostedHMApplication()) {
			if (!StringUtils.isBlank(userContext.getTimeZone())) {
				this.getNewStart().setTimeZone(userContext.getTimeZone());
			}
		} else {
			//for on-premise customer, use the timezone, and list countries contain this timezone
			this.getNewStart().setCountry("");
			MgmtServiceTime ntpService = QueryUtil.findBoByAttribute(MgmtServiceTime.class, "mgmtName", networkName, getDomain().getId(), this);
			if (null != ntpService) {
				this.getNewStart().setTimeZone(ntpService.getTimeZoneStr());
			} else {
				this.getNewStart().setTimeZone(getDomain().getTimeZoneString());
			}
		}
	}

	private int conApNum,conBrNum,conSwNum,conCvgNum,conTotalNum;

	private void initDeviceNumberInPage() {
		List<?> lst = QueryUtil.executeQuery("select hiveApModel, count(*) from " + HiveAp.class.getSimpleName(),
				null, new FilterParams("simulated", false), new GroupByParams(new String[] { "hiveApModel"}), getDomain().getId());
		if (lst.isEmpty()) {
			conApNum=conBrNum=conSwNum=conCvgNum=conTotalNum=0;
		} else {
			for(Object obj: lst){
				Object[] oneObj = (Object[]) obj;
				int model = Integer.valueOf(oneObj[0].toString());
				int devices = Integer.valueOf(oneObj[1].toString());

				switch (model) {
					case HiveAp.HIVEAP_MODEL_28:
					case HiveAp.HIVEAP_MODEL_20:
					case HiveAp.HIVEAP_MODEL_320:
					case HiveAp.HIVEAP_MODEL_340:
					case HiveAp.HIVEAP_MODEL_380:
					case HiveAp.HIVEAP_MODEL_120:
					case HiveAp.HIVEAP_MODEL_110:
					case HiveAp.HIVEAP_MODEL_330:
					case HiveAp.HIVEAP_MODEL_350:
					case HiveAp.HIVEAP_MODEL_370:
					case HiveAp.HIVEAP_MODEL_390:
					case HiveAp.HIVEAP_MODEL_170:
					case HiveAp.HIVEAP_MODEL_121:
					case HiveAp.HIVEAP_MODEL_141:
					case HiveAp.HIVEAP_MODEL_230:
						conApNum  = devices + conApNum;
						break;

					case HiveAp.HIVEAP_MODEL_BR100:
					case HiveAp.HIVEAP_MODEL_BR200:
					case HiveAp.HIVEAP_MODEL_BR200_WP:
					case HiveAp.HIVEAP_MODEL_BR200_LTE_VZ:
						conBrNum  = devices + conBrNum;
						break;
					case HiveAp.HIVEAP_MODEL_SR24:
					case HiveAp.HIVEAP_MODEL_SR48:
					case HiveAp.HIVEAP_MODEL_SR2024P:
					case HiveAp.HIVEAP_MODEL_SR2124P:
					case HiveAp.HIVEAP_MODEL_SR2148P:
						conSwNum  = devices + conSwNum;
						break;
					case HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA:
					case HiveAp.HIVEAP_MODEL_VPN_GATEWAY:
						conCvgNum = devices + conCvgNum;
						break;
					default: break;
				}
			}

			conTotalNum = conSwNum + conBrNum + conApNum + conCvgNum;
		}

	}
	
	private void initStartHereValues() {
		HmStartConfig startConf = QueryUtil.findBoByAttribute(HmStartConfig.class, "owner.id", getDomain().getId());
		
		networkName = NmsUtil.getOEMCustomer().getCompanyNameWithoutBlank();
		
		if (null != startConf) {
			networkName = startConf.getNetworkName();
			hmModeType = startConf.getModeType();
		} else {
			// set default network name
			if (null != getDomain() && !HmDomain.HOME_DOMAIN.equals(getDomain().getDomainName())){
				networkName = getDomain().getDomainName();
			}
		}
		
		// set time zone
		MgmtServiceTime ntpService = QueryUtil.findBoByAttribute(MgmtServiceTime.class, "mgmtName", networkName, getDomain().getId(), this);
		if (null != ntpService) {
			timezone = HmBeOsUtil.getServerTimeZoneIndex(ntpService.getTimeZoneStr());
		} else {
			timezone = HmBeOsUtil.getServerTimeZoneIndex(getDomain().getTimeZoneString());
		}
	}
	
	public boolean getDisplayEnterprise() {
		boolean showExpress;
		List<HmExpressModeEnable> settings = QueryUtil.executeQuery(HmExpressModeEnable.class,
				null, null);
		if (settings.isEmpty()) {
			showExpress = NmsUtil.getOEMCustomer().getExpressModeEnable();
		} else {
			showExpress = settings.get(0).isExpressModeEnable();
		}

		return isOEMSystem() && !showExpress;
	}
	
	private boolean isNeedStartHereConfigure() throws Exception {
		String groupName = userContext.getUserGroup().getGroupName();

		if (!HmUserGroup.PLANNING.equals(groupName) && !HmUserGroup.TEACHER.equals(groupName)) {
			// check HM mode has configured
			List<?> list = QueryUtil.executeQuery("select adminUserLogin from "+HmStartConfig.class.getSimpleName(), null, 
					new FilterParams("owner.id", getDomain().getId()));
			
			if (list.isEmpty() || !((Boolean)list.get(0))) {
				if (userContext.getDefaultFlag()) {
					return true;
				} else {
					throw new Exception(MgrUtil.getUserMessage("error.auth.vhm.uninitialize"));
				}
			}
		}
		return false;
	}
	
	public String getEnterKeyPannel() {
		if (!vmwareConnectLsCheck) {
			setVmwareSystemWarnMsg();
		}
		if (null != vmwareConnectLsInfo)
			return "none";
		if (getIsInHomeDomain()) {
			if (!NmsUtil.isHostedHMApplication()) {
				return userContext.isSuperUser() ? "" : "none";
			}
			return "none";
		} else {
			if (NmsUtil.isProduction()) {
				return userContext.getDefaultFlag() ? "" : "none";
			} else {
				return "none";
			}
		}
	}
	
	public String getHiveApPassword() {
		return hiveApPassword;
	}

	public void setHiveApPassword(String hiveApPassword) {
		this.hiveApPassword = hiveApPassword;
	}
	
	private short hmModeType = HmStartConfig.HM_MODE_EASY;
	
	public short getHmModeType()
	{
		return hmModeType;
	}

	public void setHmModeType(short hmModeType)
	{
		this.hmModeType = hmModeType;
	}

	public EnumItem[] getModeType1() {
		return new EnumItem[] { new EnumItem(HmStartConfig.HM_MODE_EASY,
				MgrUtil.getEnumString("enum.hm.welcome.page.hm.mode."
						+ HmStartConfig.HM_MODE_EASY)) };
	}

	public EnumItem[] getModeType2() {
		return new EnumItem[] { new EnumItem(HmStartConfig.HM_MODE_FULL,
				MgrUtil.getEnumString("enum.hm.welcome.page.hm.mode."
						+ HmStartConfig.HM_MODE_FULL)) };
	}
	
	public boolean getDisableMode() {
		return !getDomain().isSupportFullMode();
	}
	
	public boolean getDisableExpressMode() {
		List<?> startConf = QueryUtil.executeQuery("select modeType from "+HmStartConfig.class.getSimpleName(), null, 
				new FilterParams("owner.id", getDomain().getId()));
		return !startConf.isEmpty() && HmStartConfig.HM_MODE_FULL == (Short)startConf.get(0);
	}
	
	private boolean rebootFlag;

	public boolean isRebootFlag() {
		return rebootFlag;
	}

	public void setRebootFlag(boolean rebootFlag) {
		this.rebootFlag = rebootFlag;
	}
	
	public EnumItem[] getTimeZoneRebootNow() {
		return new EnumItem[] { new EnumItem(1,
				getText("hm.config.start.welcome.setting.timezone.reboot.now")) };
	}

	public EnumItem[] getTimeZoneRebootCancel() {
		return new EnumItem[] { new EnumItem(2,
				getText("hm.config.start.welcome.setting.timezone.reboot.cancel")) };
	}
	
	public int getConApNum() {
		return conApNum;
	}

	public void setConApNum(int conApNum) {
		this.conApNum = conApNum;
	}

	public int getConBrNum() {
		return conBrNum;
	}

	public void setConBrNum(int conBrNum) {
		this.conBrNum = conBrNum;
	}

	public int getConSwNum() {
		return conSwNum;
	}

	public void setConSwNum(int conSwNum) {
		this.conSwNum = conSwNum;
	}

	public int getConTotalNum() {
		return conTotalNum;
	}

	public void setConTotalNum(int conTotalNum) {
		this.conTotalNum = conTotalNum;
	}

	public int getConCvgNum() {
		return conCvgNum;
	}

	public void setConCvgNum(int conCvgNum) {
		this.conCvgNum = conCvgNum;
	}

}