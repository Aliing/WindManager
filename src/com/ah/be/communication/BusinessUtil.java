package com.ah.be.communication;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ah.apiengine.element.MvResponseInfo;
import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeOperateHMCentOSImpl;
import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.admin.hhmoperate.APSwitchCenter;
import com.ah.be.admin.hhmoperate.HHMUserMgmt;
import com.ah.be.admin.hhmoperate.HHMmove;
import com.ah.be.admin.restoredb.RestoreAdmin;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.communication.event.portal.BeDeleteAPFromHMEvent;
import com.ah.be.communication.event.portal.BeDeleteAPFromHMResult;
import com.ah.be.communication.event.portal.BeHABreakEvent;
import com.ah.be.communication.event.portal.BeHABreakResultEvent;
import com.ah.be.communication.event.portal.BeHAEnableEvent;
import com.ah.be.communication.event.portal.BeHAJoinEvent;
import com.ah.be.communication.event.portal.BeHAMaintenanceEvent;
import com.ah.be.communication.event.portal.BeHAMaintenanceResultEvent;
import com.ah.be.communication.event.portal.BeHAStatusInfoEvent;
import com.ah.be.communication.event.portal.BeHASwitchOverEvent;
import com.ah.be.communication.event.portal.BePortalHMPayloadEvent;
import com.ah.be.communication.event.portal.BePortalHMPayloadResultEvent;
import com.ah.be.communication.event.portal.BePoweroffHmolEvent;
import com.ah.be.communication.event.portal.BeQueryGroupInfoFromHMEvent;
import com.ah.be.communication.event.portal.BeQueryGroupInfoFromHMResult;
import com.ah.be.communication.event.portal.BeQueryHADBStatusResult;
import com.ah.be.communication.event.portal.BeQueryHAStatusResult;
import com.ah.be.communication.event.portal.BeSearchAPFromHMEvent;
import com.ah.be.communication.event.portal.BeSearchAPFromHMResult;
import com.ah.be.communication.event.portal.BeVersionUpdateInfoEvent;
import com.ah.be.communication.mo.CredentialInfo;
import com.ah.be.communication.mo.HmolInfo;
import com.ah.be.communication.mo.UserInfo;
import com.ah.be.communication.mo.VhmInfo;
import com.ah.be.communication.mo.VhmRumStatus;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.LicenseInfo;
import com.ah.be.license.OrderKeyManagement;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.os.NetConfigImplInterface;
import com.ah.be.sn.operation.Ap;
import com.ah.be.sn.operation.ApDeleteResponse;
import com.ah.be.sn.operation.ApSearchResponse;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.DomainOrderKeyInfo;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.admin.OrderHistoryInfo;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;
import com.ah.bo.hhm.HhmUpgradeVersionInfo;
import com.ah.bo.hiveap.DeviceInventory;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.hiveap.HiveApSerialNumber;
import com.ah.bo.hiveap.HiveApUpdateResult;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.Application;
import com.ah.bo.network.NetworkService;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.ha.HAMonitor;
import com.ah.ha.HAStatus;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.admin.LicenseMgrAction;
import com.ah.ui.actions.home.HmSettingsAction;
import com.ah.ui.actions.home.StartHereAction;
import com.ah.util.MgrUtil;
import com.ah.util.UserSettingsUtil;
import com.ah.util.bo.DeviceFreevalUtil;

/**
 * Modify Note:
 * <br>
 * 1. Jan 13, 2011 6:14:47 PM, Yunzhi Lin<br>**add** -- send an email to notify the user(default is 'techops_mon@aerohive.com') when doing HA switch over
 */
public class BusinessUtil {

	private final static Log	log	= LogFactory.getLog("commonlog.BusinessUtil");
	
	public static final String PRODUCT_FREEVAL_AP_REGISTER_FLAG = "registerForFreevalAP";

	public static void createHomeUser(UserInfo userInfo) throws Exception {
		String logInfo;

		// create user
		if (userInfo.getUsername() == null || userInfo.getUsername().trim().length() == 0) {
			throw new Exception("'userName' is necessary.");
		}

		HmDomain domain = BoMgmt.getDomainMgmt().getHomeDomain();
		HmUserGroup userGroup = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupAttribute",
				(int) userInfo.getGroupAttribute(), domain.getId());
		if (userGroup == null) {
			int groupAttr = HmUserGroup.MONITOR_ATTRIBUTE;

			userGroup = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupAttribute", groupAttr,
					domain.getId());
			if (userGroup == null) {
				throw new Exception("User group(domain=" + domain.getDomainName() + ", attribute="
						+ userInfo.getGroupAttribute() + "->" + HmUserGroup.MONITOR_ATTRIBUTE
						+ ") is not exists.");
			}
		}

		HmUser user = new HmUser();

		boolean userExisted = false;
		// check email address
		List<HmUser> list = QueryUtil.executeQuery(HmUser.class, null, new FilterParams(
				"lower(emailAddress)", userInfo.getEmailAddress().toLowerCase()));
		if (!list.isEmpty()) {
			// if exists user with same name, update it.
			user = list.get(0);
			userExisted = true;
			if (user.getDomain().getId().longValue() != domain.getId().longValue()) {
				logInfo = MgrUtil.getUserMessage("hm.system.log.business.util.create.host.user.domain.error",new String[]{user.getUserName(),user.getEmailAddress(), user.getDomain().getDomainName()});
				log.error(logInfo);
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_CRITICAL,
						HmSystemLog.FEATURE_ADMINISTRATION, logInfo);

				throw new Exception(logInfo);
			} else if (!user.getUserName().equals(userInfo.getUsername())) {
				logInfo = MgrUtil.getUserMessage("hm.system.log.business.util.create.host.user.domain.error",new String[]{user.getUserName(),user.getEmailAddress(), user.getUserName()});
				log.error(logInfo);
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_CRITICAL,
						HmSystemLog.FEATURE_ADMINISTRATION, logInfo);
				throw new Exception(logInfo);
			}
		} else {
			// check user name
			list = QueryUtil.executeQuery(HmUser.class, null, new FilterParams("lower(userName)",
					userInfo.getUsername().toLowerCase()), domain.getId());
			if (!list.isEmpty()) {
				// if exists user with same name, update it.
				user = list.get(0);
				userExisted = true;
			}
		}

		user.setEmailAddress(userInfo.getEmailAddress());
		user.setOwner(domain);
		user.setPassword(userInfo.getPassword());
		user.setUserFullName(userInfo.getFullname());
		user.setUserGroup(userGroup);
		user.setUserName(userInfo.getUsername());
//		user.setMaxAPNum(userInfo.getMaxAPNum());
		user.setTimeZone(userInfo.getTimeZone());

		try {
			// changed in Geneva, for user setting columns separated from hm_user
			UserSettingsUtil.updateMaxAPNum(userInfo.getEmailAddress(), userInfo.getMaxAPNum());
			if (userExisted) {
				QueryUtil.updateBo(user);
			} else {
				QueryUtil.createBo(user);
			}
		} catch (Exception e) {
			logInfo = "Create home user [" + user.getUserName() + "," + user.getEmailAddress()
					+ "] error. ";
			log.error(logInfo, e);
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_CRITICAL,
					HmSystemLog.FEATURE_ADMINISTRATION, logInfo + e.getMessage());

			throw new Exception(logInfo + e.getMessage());
		}
	}

	public static void modifyVhmUser(UserInfo userInfo) throws Exception {
		// modify user
		if (userInfo.getUsername() == null || userInfo.getUsername().trim().length() == 0) {
			throw new Exception("'userName' is necessary.");
		}

		HmDomain domain = BoMgmt.getDomainMgmt().getHomeDomain();
		if (userInfo.getVhmName() != null && userInfo.getVhmName().length() > 0) {
			// maybe modify vhm user name
			domain = CacheMgmt.getInstance().getCacheDomainByName(userInfo.getVhmName());
			if (domain == null) {
				throw new Exception("Domain(name=" + userInfo.getVhmName() + ") is not exists.");
			}
		}

		// check user name
		List<HmUser> list = QueryUtil.executeQuery(HmUser.class, null, new FilterParams(
				"lower(userName)", userInfo.getUsername().toLowerCase()), domain.getId());
		if (list.isEmpty()) {
			DebugUtil.commonDebugWarn("User (" + userInfo.getUsername()
					+ ") is not exists. ignore modify user");
			return;
		}

		HmUser user = list.get(0);
		String oldPassword = user.getPassword();

		user.setEmailAddress(userInfo.getEmailAddress());
		user.setOwner(domain);
		user.setPassword(userInfo.getPassword());
		user.setUserFullName(userInfo.getFullname());

		HmUserGroup userGroup = QueryUtil.findBoByAttribute(HmUserGroup.class, "groupAttribute",
				(int) userInfo.getGroupAttribute(), domain.getId());

		// the users belong to portal
		if (domain.isHomeDomain()) {
			if (userGroup == null) {
				throw new Exception("User group(attribute=" + userInfo.getGroupAttribute()
						+ ") is not exists.");
			}
			user.setUserGroup(userGroup);
			// the users belong to hivemanager
		} else {
			// only planner default user need to change user group
			if (user.getUserGroup().isPlUserGroup() && user.getDefaultFlag()) {
				user.setUserGroup(userGroup);
			}
		}
		user.setUserName(userInfo.getUsername());
//		user.setMaxAPNum(userInfo.getMaxAPNum());
		user.setDefapplication(userInfo.getDefaultApp());
		user.setTimeZone(userInfo.getTimeZone());

		try {
			// changed in Geneva, for user setting columns separated from hm_user
			UserSettingsUtil.updateMaxAPNum(userInfo.getEmailAddress(), userInfo.getMaxAPNum());
			QueryUtil.updateBo(user);
		} catch (Exception e) {
			DebugUtil.commonDebugError("BusinessUtil.modifyUser(): catch exception", e);

			// throw exception
			throw new Exception("Modify user error. " + e.getMessage());
		}

		// if modify password of admin user, sync to shell password
		if (domain.isHomeDomain() && user.getUserName().equals(HmUser.ADMIN_USER)) {
			if (!oldPassword.equals(user.getPassword())) {
				String clearPassword = userInfo.getClearPassword();
				if (clearPassword != null && clearPassword.trim().length() > 0) {
					try {
						String[] cmd = { "bash", "-c", "passwd admin" };
						Process proc = Runtime.getRuntime().exec(cmd);

						PrintWriter out = new PrintWriter(new OutputStreamWriter(proc
								.getOutputStream()));
						// new password
						out.println(clearPassword);
						out.flush();

						// confirm password
						out.println(clearPassword);
						out.flush();
					} catch (Exception e) {
						DebugUtil.commonDebugError("BusinessUtil.modifyUser(): catch exception", e);
					}
				} else {
					DebugUtil
							.commonDebugError("BusinessUtil.modifyUser(): password not same with old password, but clear password in message is empty.");
				}
			}
		}
	}

	public static void resetVhmAdminPassword(String vhmName, String clearPassword) throws Exception {
		log.info("resetVhmAdminPassword() in");

		HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", vhmName);
		if (domain == null) {
			DebugUtil.commonDebugWarn("VHM '" + vhmName + "' is not exists. ignore it!");
			return;
		}

		HmUser defaultUser = QueryUtil.findBoByAttribute(HmUser.class, "defaultFlag", true, domain
				.getId());
		if (defaultUser == null) {
			DebugUtil.commonDebugWarn("Unable to find default user of vhm '" + vhmName
					+ "'. ignore it!");
			return;
		}

		// reset password of default user

		try {
			HHMUserMgmt.getInstance().resetUserPassword(defaultUser, clearPassword);
		} catch (Exception e) {
			DebugUtil.commonDebugError("reset user password catch exception", e);

			// throw exception
			throw new Exception("Reset user password error. " + e.getMessage());
		}

		log.info("resetVhmAdminPassword() out");
	}

	public static void sendCredential(CredentialInfo credInfo) throws Exception {
		log.info("sendCredential() in");

		HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", credInfo
				.getVhmName());
		if (domain == null) {
			throw new Exception("VHM '" + credInfo.getVhmName() + "' is not exists.");
		}

		HmUser defaultUser = QueryUtil.findBoByAttribute(HmUser.class, "defaultFlag", true, domain
				.getId());
		if (defaultUser == null) {
			throw new Exception("Unable to find default user of vhm '" + credInfo.getVhmName()
					+ "'");
		}

		try {
			String password_clear = credInfo.getClearPassword();
			defaultUser.setPassword(MgrUtil.digest(password_clear));
			QueryUtil.updateBo(defaultUser);

			// String toEmail = defaultUser.getEmailAddress();
			// if (toEmail == null || toEmail.trim().isEmpty()) {
			// throw new Exception("Unable to send notify email, user '"
			// + defaultUser.getUserName() + "' email address is empty.");
			// }
			//
			// String subject = MgrUtil.getUserMessage("email.account.created.title");
			// String mailContent = getHtmlContent4createAccount(credInfo, password_clear);
			//
			// sendEmail(mailContent, subject, toEmail);
		} catch (Exception e) {
			DebugUtil.commonDebugError(
					"VhmAgentImpl.execute(): send credent to user catch exception", e);

			// throw exception
			throw new Exception("Send credent error. " + e.getMessage());
		}

		log.info("sendCredential() out");
	}

	// private static void sendEmail(String mailContent, String subject, String toEmail) {
	// EmailElement email = new EmailElement();
	// email.setMustBeSent(true);
	// email.setMailContent(mailContent);
	// email.setSubject(subject);
	// email.setToEmail(toEmail);
	// email.setContentType("text/html");
	// email.addShowfile(AhDirTools.getHmRoot() + "images" + File.separator + "company_logo.png");
	//
	// HmBeAdminUtil.sendEmail(email);
	// }

	// private static String getHtmlContent4createAccount(CredentialInfo credInfo, String password)
	// {
	// String accountType = "";
	// if (credInfo.getVhmType() == CredentialInfo.USER_TYPE_PLAN_EVAL) {
	// accountType = " Planner";
	// } else {
	// accountType = " Demo";
	// }
	//
	// String validDaysStr = "";
	// if (credInfo.getValidDays() > 0) {
	// validDaysStr = ", and this account is valid for " + credInfo.getValidDays() + " days";
	// }
	//
	// StringBuffer content = new StringBuffer();
	// content.append("<html><body><table border=\"0\">");
	// content.append("<tr><td>");
	// content.append(MgrUtil.getUserMessage("email.account.created.para1",
	// new String[] { accountType, validDaysStr }));
	// content.append("</td></tr><br><tr><td>");
	// content.append(MgrUtil.getUserMessage("email.account.created.para2"));
	// content.append("</td></tr><br><tr><td>");
	// content.append(MgrUtil.getUserMessage("email.account.created.para3"));
	// content.append("</td></tr><br>");
	// content.append("<tr><td><table border=\"0\">");
	// content.append("<tr><td width=\"150px\">");
	// content.append("Login URL:");
	// content.append("</td><td>");
	// content.append(credInfo.getDnsUrl());
	// content.append("</td></tr><tr><td width=\"150px\">");
	// content.append("Admin Name:");
	// content.append("</td><td>");
	// content.append(credInfo.getUserName());
	// content.append("</td></tr><tr><td width=\"150px\">");
	// content.append("Password:");
	// content.append("</td><td>");
	// content.append(password);
	// content.append("</td></tr><tr><td width=\"150px\">");
	// content.append("Domain Name:");
	// content.append("</td><td>");
	// content.append(credInfo.getVhmName());
	// content.append("</td></tr><tr><td width=\"150px\">");
	// content.append("VHM ID:");
	// content.append("</td><td>");
	// content.append(credInfo.getVhmId());
	// content.append("</td></tr></table></td></tr><br><br>");
	//
	// /*
	// * copyright
	// */
	// content.append("<tr><td>");
	// content.append("Copyright ");
	//
	// /*
	// * year
	// */
	// content.append(Calendar.getInstance().get(Calendar.YEAR));
	// content.append(" Aerohive Networks Inc");
	// content.append("</td></tr>");
	//
	// /*
	// * logo
	// */
	// content.append("<tr><td>");
	// content.append("<img src=\"cid:company_logo.png\" />");
	// content.append("</td></tr>");
	//
	// content.append("</table></body><html>");
	//
	// return content.toString();
	// }

	public static int modifyVHM(VhmInfo info) throws Exception {
		if (info == null) {
			throw new Exception("VHM information object is necessary.");
		}

		String vhmName = info.getVhmName();
		if (vhmName == null || vhmName.trim().length() == 0) {
			throw new Exception("'vhmName' is necessary.");
		}

		HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "lower(domainName)", vhmName
				.toLowerCase());
		if (domain == null) {
			DebugUtil.commonDebugWarn("VHM '" + vhmName + "' is not exists, ignore modify vHM!");
			return -1;
		}

		// check email address
		if (null != info.getVhmAdminEmailAddress() && !info.getVhmAdminEmailAddress().isEmpty()) {
			List<?> boIds = QueryUtil
					.executeQuery(
							"select id from " + HmUser.class.getSimpleName(),
							null,
							new FilterParams(
									"lower(emailAddress) = :s1 AND (lower(owner.domainName) != :s2 OR defaultFlag = :s3)",
									new Object[] { info.getVhmAdminEmailAddress().toLowerCase(),
											vhmName.toLowerCase(), false }));

			if (!boIds.isEmpty()) {
				throw new Exception("E-mail address '" + info.getVhmAdminEmailAddress()
						+ "' already exists.");
			}
		}

		// // check remaining ap number
		// int remaining = BoMgmt.getDomainMgmt().getRemainingMaxAPNum() + domain.getMaxApNum();
		// if (remaining < info.getMaxApNum()) {
		// throw new Exception(MgrUtil.getUserMessage("error.vhm.create.noremainingap"));
		// }

		// check gm support
		if (info.isGmLightEnable() && !HmBeLicenseUtil.GM_LITE_LICENSE_VALID) {
			throw new Exception("User manager not be supported.");
		}

//		HmUser ownerUser = QueryUtil.findBoByAttribute(HmUser.class, "userName", info
//				.getOwnerUserName(), BoMgmt.getDomainMgmt().getHomeDomain().getId());
//		if (info.getOwnerUserName() != null && info.getOwnerUserName().length() > 0
//				&& ownerUser == null) {
//			throw new Exception("Cannot find Partner admin '" + info.getOwnerUserName() + "'.");
//		}

		domain.setSupportGM(info.isGmLightEnable());
		domain.setVhmID(info.getVhmId());
		domain.setSupportFullMode(info.isEnterpriseEnableFlag());
		domain.setBccEmail(info.getCcEmailAddress());
		domain.setPartnerId(info.getOwnerUserName());
		domain.setMaxSimuAp(info.getMaxSimuApNum());
		domain.setMaxSimuClient(info.getMaxSimuClientNum());

		try {
			BoMgmt.getDomainMgmt().updateDomain(domain);
		} catch (Exception e) {
			DebugUtil.commonDebugError("BusinessUtil.modifyVHM():: modify domain catch exception",
					e);

			// throw exception
			throw new Exception("Modify domain error. " + e.getMessage());
		}

		// create or update default user
		// check user name unique
		if (null != info.getVhmAdminEmailAddress() && !info.getVhmAdminEmailAddress().isEmpty()) {
			List<HmUser> userList = QueryUtil.executeQuery(HmUser.class, null, new FilterParams(
					"defaultFlag", true), domain.getId());
			if (userList.isEmpty()) {
				// create default user
				// try {
				// HHMUserMgmt.getInstance().createDefaultUserAccount(info, domain);
				// } catch (Exception e) {
				// DebugUtil.commonDebugError(
				// "BusinessUtil.modifyVHM(): create default user for domain catch exception",
				// e);
				//
				// // throw exception
				// throw new Exception("Create default user error. " + e.getMessage());
				// }
				throw new Exception("Default user not exists.");
			}
			HmUser user = userList.get(0);
			user.setEmailAddress(info.getVhmAdminEmailAddress());
			user.setUserFullName(info.getVhmAdminFullName());
			user.setUserName(info.getVhmAdminName());
			if (!StringUtils.isEmpty(info.getVhmAdminClearPassword())) {
				// if VHM default admin be changed, need reset password for new VHM admin
				user.setPassword(MgrUtil.digest(info.getVhmAdminClearPassword()));
			}

			QueryUtil.updateBo(user);
		}

		// for order key
		String orderKey = info.getOrderKey();
		if (null != orderKey && !"".equals(orderKey)) {
			String checkRes = LicenseMgrAction.checkKeyExistsIgnoreDomain(orderKey);
			if (null == checkRes) {
				try {
					OrderKeyManagement.activateOrderKey(orderKey, vhmName, info.getVhmId());
				} catch (Exception e) {
					DebugUtil.commonDebugError("BusinessUtil.modifyVHM(): catch exception", e);

					// throw exception
					DebugUtil.commonDebugError("The domain information has updated successfully, but the entitlement key is invalid. "
							+ e.getMessage());
					return -2;
				}
			} else {
				DebugUtil.commonDebugError("The domain information has updated successfully, but the entitlement key has been used.");
				return -3;
			}
		}
		// return the ap number of this vhm support
		if (HmolInfo.HHMTYPE_PRODUCT != info.getVhmType()) {
			if (HmolInfo.HHMTYPE_DEMO == info.getVhmType())
				RestoreAdmin.changeDomainOrderKeyInfo(vhmName, info.getVhmId(), info.getMaxApNum(), info.getValidDays());
			return info.getMaxApNum();
		} else {
			LicenseInfo lsInfo = HmBeLicenseUtil.VHM_ORDERKEY_INFO.get(vhmName);
			if (null != lsInfo) {
				if (BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(lsInfo.getLicenseType())) {
					return 0;
				} else {
					return lsInfo.getHiveAps();
				}
			} else {
				DomainOrderKeyInfo domOrder = QueryUtil.findBoByAttribute(DomainOrderKeyInfo.class,
						"domainName", vhmName);
				if (null != domOrder) {
					int[] orderInfo = domOrder.getOrderInfo();
					return orderInfo[1];
				}
			}
		}
		return -1;
	}

	public static int createVHM(VhmInfo info) throws Exception {
		if (info == null) {
			throw new Exception("VHM information object is necessary.");
		}

		String vhmName = info.getVhmName();
		if (vhmName == null || vhmName.trim().length() == 0) {
			throw new Exception("'vhmName' is necessary.");
		}

		HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "lower(domainName)", vhmName
				.toLowerCase());

		if (null != domain) {
			throw new Exception(MgrUtil.getUserMessage("error.objectExists", vhmName));
		}

		// check max VHM number
//		int maxVHMCount = HmBeLicenseUtil.getLicenseInfo().getVhmNumber();
//		if (CacheMgmt.getInstance().getCacheDomainCount() >= maxVHMCount) {
//			throw new Exception(MgrUtil.getUserMessage("error.vhm.outofLincense.maximum", String
//					.valueOf(maxVHMCount)));
//		}

		// there is no ap number when create
		// check remaining ap number
		// int remaining = BoMgmt.getDomainMgmt().getRemainingMaxAPNum();
		// if (remaining < info.getMaxApNum()) {
		// throw new Exception(MgrUtil.getUserMessage("error.vhm.create.noremainingap"));
		// }

		// check email address
		if (null != info.getVhmAdminEmailAddress() && !info.getVhmAdminEmailAddress().isEmpty()) {
			List<?> boIds = QueryUtil.executeQuery("select id from " + HmUser.class.getSimpleName(),
					null, new FilterParams("lower(emailAddress)", info.getVhmAdminEmailAddress()
					.toLowerCase()));

			if (!boIds.isEmpty()) {
				throw new Exception("E-mail address '" + info.getVhmAdminEmailAddress()
						+ "' already exists.");
			}
		}
		
		// check gm support
		if (info.isGmLightEnable() && !HmBeLicenseUtil.GM_LITE_LICENSE_VALID) {
			throw new Exception("User manager not be supported.");
		}

//		HmUser ownerUser = QueryUtil.findBoByAttribute(HmUser.class, "userName", info
//				.getOwnerUserName(), BoMgmt.getDomainMgmt().getHomeDomain().getId());
//		if (info.getOwnerUserName() != null && info.getOwnerUserName().length() > 0
//				&& ownerUser == null) {
//			throw new Exception("Cannot find Partner admin '" + info.getOwnerUserName() + "'.");
//		}

		domain = new HmDomain();

		domain.setDomainName(vhmName);
		domain.setSupportGM(info.isGmLightEnable());
		domain.setVhmID(info.getVhmId());
		domain.setSupportFullMode(info.isEnterpriseEnableFlag());
		domain.setBccEmail(info.getCcEmailAddress());
		domain.setPartnerId(info.getOwnerUserName());
		domain.setMaxSimuAp(info.getMaxSimuApNum());
		domain.setMaxSimuClient(info.getMaxSimuClientNum());
		domain.setAccessMode(info.getAccessMode());
		domain.setAuthorizationEndDate(info.getAuthorizationEndDate());
		domain.setAuthorizedTime(info.getAuthorizedTime()); // fix Bug 28274 - when set vhm using Temp access,time left is -1 in Device Management Settings page

		try {
			if (DeviceFreevalUtil.isHMForDeviceFreeval() || (null != info.getUrl() && PRODUCT_FREEVAL_AP_REGISTER_FLAG.equals(info.getUrl()))) {
				// create VHM for Free BR/Free AP
				
				// copy home domain's data to new VHM
				Long domId = BoMgmt.getDomainMgmt().cloneDomain(BoMgmt.getDomainMgmt().getHomeDomain(), domain, true);

				// set this vhm to enterprise mode
				HmStartConfig startConf = new HmStartConfig();
				startConf.setNetworkName(vhmName);
				startConf.setAsciiKey("aerohive");
				startConf.setQuickStartPwd("aerohive");
				startConf.setHiveApPassword(null);
				startConf.setUseAccessConsole(true);
				
				// FreeVal AP register
				if (null != info.getUrl() && PRODUCT_FREEVAL_AP_REGISTER_FLAG.equals(info.getUrl())) {
					startConf.setModeType(HmStartConfig.HM_MODE_EASY);
					startConf.setAdminUserLogin(true);
				// BR100
				} else {
					startConf.setModeType(HmStartConfig.HM_MODE_FULL);
				}

				BusinessUtil bUtil = new BusinessUtil();
				StartHereAction.saveObject(startConf, null, domId, null, false, HmStartConfig.EXPRESS_MODE_DEFAULT_NTP_SERVER,
						HmBeOsUtil.getServerTimeZoneIndex(domain.getTimeZoneString()), bUtil.new QueryBoForStartHere(), HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP1,
						HmStartConfig.EXPRESS_MODE_DEFAULT_DNS_IP2, QueryUtil.findBoById(HmDomain.class, domId), startConf.getModeType(),false,true);
				
				//Bug 25589 fix
				StartHereAction.updateAccessConsoleForQSWirelessOnlyNetworkPolicy(domId,startConf.getNetworkName());
				
			} else {
				// create VHM for other VHM (demo/planner/product VHM/Free BJGW)
				BoMgmt.getDomainMgmt().createDomain(domain);
				//for new VHM initialize L7 application service 
				initVhmApplicationService(domain);
			}
		} catch (Exception e) {
			DebugUtil.commonDebugError("BusinessUtil.createVHM(): catch exception", e);

			// throw exception
			throw new Exception("Create domain error. " + e.getMessage());
		}

		// create default user
		if (null != info.getVhmAdminEmailAddress() && !info.getVhmAdminEmailAddress().isEmpty()) {
			try {
				HHMUserMgmt.getInstance().createDefaultUserAccount(info, domain);
			} catch (Exception e) {
				DebugUtil.commonDebugError(
						"BusinessUtil.createVHM(): create default user for domain catch exception", e);

				// throw exception
				throw new Exception("Create default user error. " + e.getMessage());
			}
		}

		// for order key
		if (null != info.getOrderKey() && !"".equals(info.getOrderKey())) {
			// check the key in database
			String checkRes = LicenseMgrAction.checkKeyExistsIgnoreDomain(info.getOrderKey());
			if (null == checkRes) {
				try {
					OrderKeyManagement.activateOrderKey(info.getOrderKey(), vhmName, info.getVhmId());
					LicenseInfo lsInfo = HmBeLicenseUtil.VHM_ORDERKEY_INFO.get(vhmName);
					if (null != lsInfo && !BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(lsInfo.getLicenseType())) {
						return lsInfo.getHiveAps();
					}
				} catch (Exception e) {
					DebugUtil.commonDebugError("BusinessUtil.createVHM(): catch exception", e);

					// create vhm from ERP
					//if (null != info.getUrl() && "infoFromERP".equals(info.getUrl())) {
					OrderHistoryInfo orderInfo = new OrderHistoryInfo();
					orderInfo.setOrderKey(info.getOrderKey());
					orderInfo.setLicenseType(BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY);
					orderInfo.setActiveTime(System.currentTimeMillis());
					orderInfo.setDomainName(vhmName);

					// save this entitlement key to database
					QueryUtil.createBo(orderInfo);
					//}
					throw new Exception(
							"The domain has created successfully, but the entitlement key is invalid. "
									+ e.getMessage());
				}
			} else {
				throw new Exception("The domain has created successfully, but " + checkRes);
			}
			// ap number is assigned by portal
		} else if (HmolInfo.HHMTYPE_PRODUCT != info.getVhmType()) {
			// planner ap number is 0, no need to create
			if (HmolInfo.HHMTYPE_DEMO == info.getVhmType())
				RestoreAdmin.changeDomainOrderKeyInfo(vhmName, info.getVhmId(), info.getMaxApNum(), info.getValidDays());
			return info.getMaxApNum();
		}
		return -1;
	}
	
	private static void initVhmApplicationService(HmDomain domain){
		List<Application> allAppList = QueryUtil.executeQuery(Application.class, null, 
				new FilterParams("appCode > :s1", new Object[] {0}));
		List<NetworkService> insertAppServices = new ArrayList<NetworkService>();
		if(!allAppList.isEmpty()){
			for(Application app : allAppList){
				if(app.getAppCode() != 0){
						NetworkService serviceDto = new NetworkService();
						serviceDto.setServiceName(NetworkService.L7_SERVICE_NAME_PREFIX+app.getAppName());
						serviceDto.setProtocolNumber(0);
						serviceDto.setPortNumber(0);				
						serviceDto.setIdleTimeout(300);
						serviceDto.setDescription(app.getAppName());
						serviceDto.setAlgType((short)0);
						serviceDto.setServiceType(NetworkService.SERVICE_TYPE_L7);
						serviceDto.setAppId(app.getAppCode());
						serviceDto.setDefaultFlag(false);
						serviceDto.setOwner(domain);
						serviceDto.setCliDefaultFlag(false);
						insertAppServices.add(serviceDto);
				}
			}
		}
		try {
			QueryUtil.bulkCreateBos(insertAppServices);
		} catch (Exception e) {
			BeLogTools.debug(HmLogConst.M_TRACER, "HMOL Initialize new vhm L7 application service failure, the vhm is: "+domain.getDomainName());
		}
	}
	
	public static void removeHomeUser(String username) throws Exception {
		// remove user
		if (username == null || username.trim().length() == 0) {
			throw new Exception("'userName' is necessary.");
		}

		HmDomain domain = BoMgmt.getDomainMgmt().getHomeDomain();

		// check user name
		List<HmUser> list = QueryUtil.executeQuery(HmUser.class, null, new FilterParams(
				"lower(userName)", username.toLowerCase()), domain.getId());
		if (list.isEmpty()) {
			DebugUtil.commonDebugWarn("User (" + username + ") is not exists. ignore remove user");
			return;
		}

		HmUser user = list.get(0);

		try {
			QueryUtil.removeBo(HmUser.class, user.getId());
		} catch (Exception e) {
			DebugUtil.commonDebugError("removeUser() catch exception", e);

			// throw exception
			throw new Exception("Remove user error. " + e.getMessage());
		}
	}

	public static void syncAllHomeUser(List<UserInfo> userList) throws Exception {
		log.info("test..syncAllHomeUser()..in");
		// sync user
		if (userList == null || userList.isEmpty()) {
			log.warn("no any user need sync!");
			return;
		}

		boolean allSuccessFlag = true;
		StringBuilder str = new StringBuilder();
		for (UserInfo userInfo : userList) {
			log.info("syncing " + userInfo.getUsername());
			try {
				createHomeUser(userInfo);
			} catch (Exception e) {
				log.error("createHomeUser failed! " + e.getMessage());
				allSuccessFlag = false;
				str.append(userInfo.getUsername()).append(", ");
			}
		}

		if (!allSuccessFlag) {
			throw new Exception("syncAllHomeUser failed! part user failed! ["
					+ str.substring(0, str.length() - 2) + "]");
		}

		log.info("test..syncAllHomeUser()..out");
	}

	public static void removeVHM(String vhmName) throws Exception {
		// modify domain change parameter to vhmId from Glasgow
		if (vhmName == null || vhmName.trim().length() == 0) {
			throw new Exception("'vhmId' is necessary.");
		}

		HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "vhmID", vhmName);
		if (domain == null) {
			DebugUtil.commonDebugError("VHM '" + vhmName + "' is not exists, ignore remove");
			return;
		}

		try {
			BoMgmt.getDomainMgmt().removeDomain(domain.getId(), true);
		} catch (Exception e) {
			DebugUtil.commonDebugError("removeVHM(): catch exception", e);

			// throw exception
			throw new Exception("Remove domain error. " + e.getMessage());
		}
	}

	static boolean	restartHM;

	public static void modifyHMOL(HmolInfo hmolInfo) throws Exception {
		restartHM = false;

		// 1. process hhm type
		short hhmType = hmolInfo.getHmolType();
		String appType;
		switch (hhmType) {
			case HmolInfo.HHMTYPE_DEMO:
				appType = ConfigUtil.VALUE_APPLICATION_TYPE_DEMO;
				break;

			case HmolInfo.HHMTYPE_PLANNER:
				appType = ConfigUtil.VALUE_APPLICATION_TYPE_PLANNER;
				break;

			case HmolInfo.HHMTYPE_PRODUCT:
				appType = ConfigUtil.VALUE_APPLICATION_TYPE_HHM;
				break;

			default:
				throw new Exception("HMOL type value (" + hhmType + ") is invalid");
		}

		String currentAppType = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_APPLICATION,
				ConfigUtil.KEY_APPLICATION_TYPE);
		if (!appType.equals(currentAppType)) {
			ConfigUtil.setConfigInfo(ConfigUtil.SECTION_APPLICATION,
					ConfigUtil.KEY_APPLICATION_TYPE, appType);

			restartHM = true;
		}

		// 2. hm online contact support email address
		String supportEmail = hmolInfo.getSystemId();
		if (null != supportEmail) {
			if (!supportEmail.equalsIgnoreCase(NmsUtil.getSupportMail())) {
				ConfigUtil.setConfigInfo(ConfigUtil.SECTION_PORTAL,
						ConfigUtil.SUPPORT_MAIL_ADDRESS, supportEmail);

				restartHM = true;
			}
		}

		// 3. process hmol status
		short hmolStatus = hmolInfo.getHmolStatus();
		String vhn = hmolInfo.getVirtualHostName();
		boolean isSlave = HAUtil.isSlave();

		HMServicesSettings hmService = QueryUtil.findBoByAttribute(HMServicesSettings.class,
				"owner.domainName", HmDomain.HOME_DOMAIN);

		boolean update = false;
		if (!vhn.equals(hmService.getVirtualHostName())) {
			hmService.setVirtualHostName(vhn);
			update = true;
		}
		if (hmolStatus != hmService.getHmStatus()) {
			hmService.setHmStatus(hmolStatus);
			update = true;
		}
		if (update && !isSlave)QueryUtil.updateBo(hmService);
	}

	public static void changeVHMStatus(String vhmName, byte status) throws Exception {
		// change domain status
		if (vhmName == null || vhmName.trim().length() == 0) {
			throw new Exception("'vhmName' is necessary.");
		}

		HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "lower(domainName)", vhmName.toLowerCase());
		if (domain == null) {
			DebugUtil.commonDebugWarn("VHM '" + vhmName
					+ "' is not exists, ignore change VHM Status");
			return;
		}

		try {
			domain.setRunStatus(status);
			BoMgmt.getDomainMgmt().updateDomain(domain);
			
			// fix customer issue CFD-267 AP reconnect to standby VHM
			// active vhm or disable vhm
			if (HmDomain.DOMAIN_DEFAULT_STATUS == status || HmDomain.DOMAIN_DISABLE_STATUS == status) {
				
				// get the ap switch server info from cache
				APSwitchCenter deviceSwitchCenter = AhAppContainer.getBeAdminModule().getDeviceSwitchCenter();
				String activeIpAddress = deviceSwitchCenter.getSwitchInfo(vhmName);
				
				// no ap switch server
				if (null == activeIpAddress) {
					
					// diable vhm
					if (HmDomain.DOMAIN_DISABLE_STATUS == status) {
						
						// get the active ap switch server info
						List<?> activeIps = QueryUtil.executeQuery("SELECT ipAddress FROM " + HMUpdateSoftwareInfo.class.getSimpleName(), 
								new SortParams("id", false), new FilterParams("status", HMUpdateSoftwareInfo.STATUS_ACTIVE));
						
						// there is only one record
						if (activeIps.size() == 1) {
							String activeIp = (String) activeIps.get(0);
							
							// change the flag of ap switch server
							QueryUtil.updateBos(HMUpdateSoftwareInfo.class, "apSwithStatus = :s1", "domainName = :s2 AND ipAddress = :s3", 
									new Object[]{HMUpdateSoftwareInfo.NEED_AP_SWITH, vhmName, activeIp});
							
							// add this info to cache
							deviceSwitchCenter.addSwitchInfo(vhmName, activeIp);
							
							// transfer ap to the active server
							BeTopoModuleUtil.transferHiveAPs(domain.getId(), activeIp);
						}
					}
				// has ap switch server, but active this vhm
				} else if (HmDomain.DOMAIN_DEFAULT_STATUS == status) {
					
					// change the flag of ap switch server
					QueryUtil.updateBos(HMUpdateSoftwareInfo.class, "apSwithStatus = :s1", "domainName = :s2", 
							new Object[]{HMUpdateSoftwareInfo.NOT_NEED_AP_SWITCH, vhmName});
					
					// remove this ap switch server from cache
					deviceSwitchCenter.removeSwitchInfo(vhmName);
				}
			}
		} catch (Exception e) {
			DebugUtil.commonDebugError("changeVHMStatus(): catch exception", e);

			// throw exception
			throw new Exception("Change domain status error. " + e.getMessage());
		}
	}

	public static HmolInfo queryHmolInfo() {
		HmolInfo info = new HmolInfo();

		BeVersionInfo versionInfo = NmsUtil.getVersionInfo();
		String version = versionInfo.getMainVersion() + "r" + versionInfo.getSubVersion();

		LicenseInfo licInfo = HmBeLicenseUtil.getLicenseInfo();

		info.setHmolVersion(version);
		if (null == licInfo || BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(licInfo.getLicenseType())) {
			info.setMaxApNum(0);
			info.setMaxVhmNum((short) 0);
			info.setLeftApCount(0);
			info.setLeftVhmCount(0);
		} else {
			info.setMaxApNum(licInfo.getHiveAps());
			info.setMaxVhmNum(licInfo.getVhmNumber());

			List<HiveAp> aps = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams(
					"manageStatus", HiveAp.STATUS_MANAGED));
			int leftApCount = licInfo.getHiveAps() - aps.size();
			info.setLeftApCount(leftApCount);

			List<HmDomain> domains = QueryUtil.executeQuery(HmDomain.class, null, null);
			int leftVhmCount = licInfo.getVhmNumber() - domains.size();
			info.setLeftVhmCount(leftVhmCount);
		}
		return info;
	}

	public static void moveVHM(String destIPAddr, String destVersion, List<String> vhmNameList)
			throws Exception {
		HHMmove.initList_2(destIPAddr, vhmNameList, destVersion);
	}

	public static List<VhmRumStatus> queryVhmMovingStatus() throws Exception {
		List<VhmRumStatus> oReturn = new ArrayList<>();

		for (MvResponseInfo oReponInfo : HHMmove.lStatus) {
			VhmRumStatus oItem = new VhmRumStatus();

			oItem.setDestHmolAddress(oReponInfo.getDestIp());
			oItem.setFailureInfo(oReponInfo.getMsg());
			oItem.setProcessStatus(oReponInfo.getProcessStatus());
			oItem.setSrcHmolAddress(oReponInfo.getSrcIp());
			oItem.setStatus(oReponInfo.getMVStatus());
			oItem.setSuccess(oReponInfo.getResult());
			oItem.setVhmName(oReponInfo.getDomainName());

			oReturn.add(oItem);
		}

		return oReturn;
	}

	public static void clearVhmMovingStatus() throws Exception {
		HHMmove.cleanStatusInfo();
	}

	public static void postProcessForMoveVHM(String destIPAddr, String destVersion,
											 List<String> vhmNameList) {
		// post process for move vhm
		HHMmove.moveHHM();
	}

	public static List<UserInfo> queryVhmUsers(String vhmName) {
		List<HmUser> users = QueryUtil
				.findBosByCondition(HmUser.class, "owner.domainName", vhmName);

		List<UserInfo> users2 = new ArrayList<>();
		for (HmUser user : users) {
			users2.add(UserInfo.getUserInfo(user));
		}
		return users2;
	}

	public static synchronized void haEnable(BeHAEnableEvent event) {
		log.info("haEnable() in");

		sendRecieveRequestResponse(event);

		String secondaryHostname = event.getSecondaryHostname();
		String haSecret = event.getHaSecret();
		boolean useExternalIPHostname = event.isUseExternalIPHostname();
		String primaryExternalIPHostname = event.getPrimaryExternalIPHostname();
		String secondaryExternalIPHostname = event.getSecondaryExternalIPHostname();
		boolean enableFailBack = event.isEnableFailBack();
		String secondaryIP = event.getSecondaryIP();
		int heartbeatTimeOutValue = event.getHeartbeatTimeOutValue();

		String exceptionMessage = "";
		String[] dbSettings = new String[]{"","","","",""};
		try {
			dbSettings = getDBSettings();
			// ha settings bo
			boolean isCreate = false;
			List<HASettings> list = QueryUtil.executeQuery(HASettings.class, null, null);
			if (list.isEmpty()) {
				isCreate = true;
			}

			NetConfigImplInterface networkService = AhAppContainer.getBeOsLayerModule().getNetworkService();
			HASettings haSettings = (!list.isEmpty() ? list.get(0) : new HASettings());
			haSettings.setHaPort(HASettings.HAPORT_MGT);
			haSettings.setHaStatus(HASettings.HASTATUS_INITIAL);
			haSettings.setDomainName(NmsUtil.getOEMCustomer().getDnsSuffix());
			haSettings.setPrimaryHostName(networkService.getHostName());
			haSettings.setSecondaryHostName(secondaryHostname);
			haSettings.setEnableFailBack(enableFailBack);
			haSettings.setPrimaryMGTIP(networkService.getIP_eth0());
			haSettings.setSecondaryMGTIP(secondaryIP);
			haSettings.setHaSecret(haSecret);
			haSettings.setUseExternalIPHostname(useExternalIPHostname);
			haSettings.setPrimaryExternalIPHostname(primaryExternalIPHostname);
			haSettings.setSecondaryExternalIPHostname(secondaryExternalIPHostname);
			haSettings.setHeartbeatTimeOutValue(heartbeatTimeOutValue);

			if (isCreate) {
				QueryUtil.createBo(haSettings);
			} else {
				QueryUtil.updateBo(haSettings);
			}
		} catch (Exception e) {
			log.error("enableHA, update database failed", e);
			exceptionMessage = "Unable to enable HA, update HA settings error. " + e.getMessage();
		}

		if (exceptionMessage.isEmpty()) {
			try {
				// send message to portal
				BeHAStatusInfoEvent resultEvent = new BeHAStatusInfoEvent();
				resultEvent.setApMac(HmBeCommunicationUtil.getPortalMac());
				resultEvent.setSuccess(exceptionMessage.isEmpty());
				resultEvent.setExceptionMessage(exceptionMessage);
				resultEvent.setHaInfoType(BeHAStatusInfoEvent.TYPE_HAENABLE_RESULT);
				resultEvent.setHaSecret(haSecret);
				resultEvent.setForce(true);
				resultEvent.setPrimaryIP(primaryExternalIPHostname);
				resultEvent.setSecondaryIP(secondaryExternalIPHostname);
				resultEvent.setDbSettings(dbSettings);
				resultEvent.buildPacket();
				HmBeCommunicationUtil.sendResponse(resultEvent);
			} catch (BeCommunicationEncodeException e) {
				log.error("enableHA, send response failed", e);
			}

			// invoke script
			int exitValue = execCommand(HmBeOsUtil.getHAScriptsPath()
					+ "ha_enable.sh" + " -h " + dbSettings[0] + " -p "
					+ dbSettings[1] + " -d " + dbSettings[2] + " -U "
					+ dbSettings[3] + " -w " + dbSettings[4]
					+ " >/HiveManager/ha/logs/ha_enable"
					+ new SimpleDateFormat("yyyy-MM-dd").format(new Date())
					+ ".log 2>&1");
			log.info("enableHA, execute ha_enable.sh, exit value is "
					+ exitValue);
			if (exitValue != 0) {
				log.error("enableHA, execute ha_enable.sh failed, exit value is "
						+ exitValue);
				exceptionMessage = "Enable HA failed! "
						+ HmSettingsAction.getHAOperationExitMessage(exitValue);
			}
		}

		try {
			if (exceptionMessage.isEmpty()) {
				QueryUtil.updateBo(HASettings.class, "hastatus = "
						+ HASettings.HASTATUS_ENABLE, null);
			}
			BeHAStatusInfoEvent resultEvent = new BeHAStatusInfoEvent();
			resultEvent.setApMac(HmBeCommunicationUtil.getPortalMac());
			resultEvent.setSuccess(exceptionMessage.isEmpty());
			resultEvent.setExceptionMessage(exceptionMessage);
			resultEvent.setHaInfoType(BeHAStatusInfoEvent.TYPE_HAENABLE_RESULT);
			resultEvent.setHaSecret(haSecret);
			resultEvent.setPrimaryIP(primaryExternalIPHostname);
			resultEvent.setSecondaryIP(secondaryExternalIPHostname);
			resultEvent.setDbSettings(dbSettings);
			resultEvent.buildPacket();
			HmBeCommunicationUtil.sendResponse(resultEvent);
		} catch (Exception e) {
			DebugUtil.commonDebugError(
					"haEnable, send result event catch exception", e);
		}

		log.info("haEnable() out");
	}

	private static String[] getDBSettings() throws SQLException {
		String[] dbSettings = new String[5];
		Connection con = null;

		try {
			con = QueryUtil.getConnection();
			DatabaseMetaData mData = con.getMetaData();
			String[] urls = DBOperationUtil.parseJDBCUrl(mData.getURL());
			if (urls != null && urls.length == 3) {
				dbSettings[0] = urls[0];
				dbSettings[1] = urls[1];
				dbSettings[2] = urls[2];
				dbSettings[3] = System.getProperty("hm.connection.username");
				dbSettings[4] = System.getProperty("hm.connection.password");
			}
			return dbSettings;
		} finally {
			if (con != null && !con.isClosed()) {
				try {
					con.close();
				} catch (SQLException e) {
					log.error("Connection close error.", e);
				}
			}
		}
	}

	public static synchronized void haJoin(BeHAJoinEvent event) {
		log.info("hajoin() in");

		sendRecieveRequestResponse(event);

		String exceptionMessage = "";

		String[] dbSettings = event.getDbSettings();

		HAMonitor haMonitor = HAUtil.getHAMonitor();

		// Suspend HA monitor before joining.
		haMonitor.setSuspending(true);

		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String cmd = HmBeOsUtil.getHAScriptsPath() + "ha_join.sh"
					+ " -h " + dbSettings[0] + " -p " + dbSettings[1] + " -d " + dbSettings[2]
					+ " -U " + dbSettings[3] + " -w " + dbSettings[4]
					+ " >/HiveManager/ha/logs/ha_join" + formatter.format(new Date()) + ".log 2>&1";
			int exitValue = execCommand(cmd);
			if (exitValue != 0) {
				log.error("HA join failed. Execute ha_join.sh failed, exit value is " + exitValue);
				throw new Exception(HmBeResUtil.getString("ha.join.error") + " "
						+ HmSettingsAction.getHAOperationExitMessage(exitValue));
			} else {
				log.info("HA join succeeded.");
			}
		} catch (Exception e) {
			exceptionMessage = e.getMessage();
		}

		try {
			BeHAStatusInfoEvent resultEvent = new BeHAStatusInfoEvent();
			resultEvent.setApMac(HmBeCommunicationUtil.getPortalMac());
			resultEvent.setHaInfoType(BeHAStatusInfoEvent.TYPE_HAJOIN_RESULT);
			resultEvent.setSuccess(exceptionMessage.isEmpty());
			resultEvent.setExceptionMessage(exceptionMessage);
			resultEvent.buildPacket();
			HmBeCommunicationUtil.sendRequest(resultEvent);

		} catch (Exception e) {
			log.error("Send HA join result event error.", e);
		}

		// restart passive node
		if (exceptionMessage.isEmpty()) {
			log.info("Restarting software processes...");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			StringBuilder cmd = new StringBuilder(BeAdminCentOSTools.ahShellRoot + "/haRebootPassive.sh");
			for (String item : dbSettings) {
				cmd.append(" ").append(item);
			}
			cmd.append(" >>/HiveManager/ha/logs/ha_join").append(formatter.format(new Date())).append(".log 2>&1");
			execCommand(cmd.toString());
		} else {
			// Resume HA monitor once join fails.
			haMonitor.setSuspending(false);
		}

		log.info("hajoin() out");
	}

	public static synchronized void haBreak(BeHABreakEvent event) {
		log.info("haBreak() in");
		int exitValue = -1;

		while (true) {
			try {
				HAStatus haStatus = HAUtil.queryHANodeStatus();
				if (haStatus.getStatus() != HAStatus.STATUS_HA_MASTER
						&& haStatus.getStatus() != HAStatus.STATUS_HA_SLAVE) {
					log.info("the ha status is " + haStatus.getStatus() + ", break ignored");
					break;
				}

				// execute script
				exitValue = execCommand(HmBeOsUtil.getHAScriptsPath() + "ha_disable.sh"
						+ " >/HiveManager/ha/logs/ha_disable"
						+ new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log 2>&1");
				log.info("execute ha_disable.sh, exit value is " + exitValue);
				if (exitValue != 0) {
					log.error("execute ha_disable.sh failed, exit value is " + exitValue);
				} else {
					// do nothing just logging
					log.info("HA breakup succeeded.");
				}
			} catch (Exception e) {
				log.error("ha break catch exception.", e);
				exitValue = Integer.MAX_VALUE;
			}
			break;
		}

		BeHABreakResultEvent resultEvent = new BeHABreakResultEvent();
		resultEvent.setApMac(HmBeCommunicationUtil.getPortalMac());
		resultEvent.setSequenceNum(event.getSequenceNum());
		resultEvent.setExitValue(exitValue);
		try {
			resultEvent.buildPacket();
			HmBeCommunicationUtil.sendRequest(resultEvent);
		} catch (Exception e) {
			log.error("ha break catch exception.", e);
		}

		log.info("haBreak() out");
	}

	public static synchronized void haMaintenance(BeHAMaintenanceEvent event) {
		short hmolStatus = event.getHmStatus();
		boolean maintenance = hmolStatus == HmolInfo.HHMSTATUS_MAINTAIN;
		short result = -3;

		while(true) {
			// invoke check maintenance script
			int exitValue = execCommand(HmBeOsUtil.getHAScriptsPath()
					+ "check_ha_maintenance.sh"
					+ " >/HiveManager/ha/logs/ha_maintenance"
					+ new SimpleDateFormat("yyyy-MM-dd").format(new Date())
					+ ".log 2>&1");

			log.info("ha check maintenance, execute check_ha_maintenance.sh, exit value is "
					+ exitValue);
			if (maintenance && exitValue == 0 ||
					!maintenance && exitValue != 0) {
				log.info("current maintenance status is : " + maintenance + " and check maintenance is : " + exitValue + ", processing ignored.");
				result = -1;
			}

			if (result != -1) {
				String runScript = !maintenance ? "disable_ha_maintenance.sh" : "enable_ha_maintenance.sh";

				// invoke maintenance script
				exitValue = execCommand(HmBeOsUtil.getHAScriptsPath()
						+ runScript
						+ " >>/HiveManager/ha/logs/ha_maintenance"
						+ new SimpleDateFormat("yyyy-MM-dd").format(new Date())
						+ ".log 2>&1");

				log.info("ha maintenance, execute " + runScript + ", exit value is "
						+ exitValue);

				if (exitValue != 0) {
					log.error("ha maintenance, execute " + runScript + " failed, exit value is "
							+ exitValue + ", processing ignored.");
					result = -2;
				} else {
					result = event.getHmStatus();
				}
			}

			boolean isSlave = HAUtil.isSlave();
			if (!isSlave && result != -2) {
				try {
					HmDomain homeDomain = BoMgmt.getDomainMgmt().getHomeDomain();
					QueryUtil.updateBos(HMServicesSettings.class,
							"hmStatus = :s1",
							"hmStatus != :s2 and owner.id = :s3",
							new Object[] { hmolStatus, hmolStatus,
											homeDomain.getId() });

				} catch (Exception e) {
					log.error("ha maintenance update db catch exception.", e);
					result = -2;
					break;
				}
			}
			break;
		}

		BeHAMaintenanceResultEvent resultEvent = new BeHAMaintenanceResultEvent();
		resultEvent.setApMac(HmBeCommunicationUtil.getPortalMac());
		resultEvent.setSequenceNum(event.getSequenceNum());
		resultEvent.setHmStatus(result);
		try {
			resultEvent.buildPacket();
			HmBeCommunicationUtil.sendRequest(resultEvent);
		} catch (Exception e) {
			log.error("ha maintenance catch exception.", e);
		}
	}

	public static void handleHAStatusInfoEvent(BeHAStatusInfoEvent event) {
		try {
			switch (event.getHaInfoType()) {
				case BeHAStatusInfoEvent.TYPE_HASTATUS_QUERY: {
					BeQueryHAStatusResult result = new BeQueryHAStatusResult();
					result.setApMac(event.getApMac());
					result.setHmolHaStatus((short) HAUtil.getHAMonitor().getCurrentStatus().getStatus());
					result.setSequenceNum(event.getSequenceNum());
					result.buildPacket();
					HmBeCommunicationUtil.sendResponse(result);
					break;
				}

				case BeHAStatusInfoEvent.TYPE_HADBSTATUS_QUERY: {
					BeQueryHADBStatusResult result = new BeQueryHADBStatusResult();
					result.setApMac(event.getApMac());
					result.setDbSettings(getDBSettings());
					result.setSequenceNum(event.getSequenceNum());
					result.buildPacket();
					HmBeCommunicationUtil.sendResponse(result);
					break;
				}

				default:
					break;
			}
		} catch (Exception e) {
			log.error("handleHAStatusInfoEvent catch exception.", e);
		}
	}

	public static void haSwitchOver(BeHASwitchOverEvent event) throws Exception {
		log.info("haSwitchOver() in");

		sendRecieveRequestResponse(event);

		String exceptionMessage = "";

		// send a notification email to super user
		NmsUtil.sendMailToAdminUser(HmBeResUtil.getString("ha.email.subject"),
				HmBeResUtil.getString("ha.email.content.passive",
						new String[]{HmBeOsUtil.getHostName(),HmBeOsUtil.getHiveManagerIPAddr()}));

		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String cmd = HmBeOsUtil.getHAScriptsPath() + "ha_switch_over.sh"
					+ " >/HiveManager/ha/logs/ha_switchover" + formatter.format(new Date())
					+ ".log 2>&1";
			int exitValue = execCommand(cmd);
			log.info("execute ha_switch_over.sh, exit value is " + exitValue);
			if (exitValue != 0) {
				throw new Exception(HmBeResUtil.getString("ha.switchover.error") + " "
						+ HmSettingsAction.getHAOperationExitMessage(exitValue));
			}
		} catch (Exception e) {
			exceptionMessage = e.getMessage();
		}

		// if HA switch over failure, re-send a error information email
		if(!exceptionMessage.isEmpty()){
			NmsUtil.sendMailToAdminUser(HmBeResUtil.getString("ha.email.subject"),
					HmBeResUtil.getString("ha.email.content.passive.error",
							new String[]{HmBeOsUtil.getHostName(),HmBeOsUtil.getHiveManagerIPAddr()})+
							"\n\nThe error message see below:\n\n"+exceptionMessage);
		}

		try {
			BeHAStatusInfoEvent resultEvent = new BeHAStatusInfoEvent();
			resultEvent.setApMac(HmBeCommunicationUtil.getPortalMac());
			resultEvent.setHaInfoType(BeHAStatusInfoEvent.TYPE_HASWITCH_RESULT);
			resultEvent.setSuccess(exceptionMessage.isEmpty());
			resultEvent.setExceptionMessage(exceptionMessage);
			resultEvent.buildPacket();
			HmBeCommunicationUtil.sendResponse(resultEvent);
		} catch (Exception e) {
			DebugUtil.commonDebugError("haSwitchOver, send result event catch exception", e);
		}

		log.info("haSwitchOver() out");
	}

	public static List<ApSearchResponse> fetchAPInfoWithSerialNum(String serialNum) {
		List<ApSearchResponse> apSearchResponseLst= new ArrayList<>();

		//please fill searchAPInfo with searched result, just fill hmolResult
		//if error occurs, please set value to errorMsg
		if (null != serialNum && !"".equals(serialNum)) {
			HiveAp apInfo = QueryUtil.findBoByAttribute(HiveAp.class, "serialNumber", serialNum);
			if (null != apInfo) {
				HmDomain hmDom = apInfo.getOwner();
				if (null != hmDom) {
					Ap newAp = new Ap();
					newAp.setSerialNumber(serialNum);
					newAp.setInternalId(apInfo.getId());
					if (null == hmDom.getVhmID() || "".equals(hmDom.getVhmID())) {
						newAp.setVhmId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
					} else {
						newAp.setVhmId(hmDom.getVhmID());
					}
					apSearchResponseLst.add(new ApSearchResponse(newAp));
				}
			}
			// HiveAP serial number table
			HiveApSerialNumber apNum = QueryUtil.findBoByAttribute(HiveApSerialNumber.class, "serialNumber", serialNum);
			if (null != apNum) {
				HmDomain hmDom = apNum.getOwner();
				if (null != hmDom) {
					Ap newAp = new Ap();
					newAp.setSerialNumber(serialNum);
					newAp.setInternalId(apNum.getId());
					if (null == hmDom.getVhmID() || "".equals(hmDom.getVhmID())) {
						newAp.setVhmId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
					} else {
						newAp.setVhmId(hmDom.getVhmID());
					}
					apSearchResponseLst.add(new ApSearchResponse(newAp));
				}
			}
			// HiveAP auto provision table
			List<?> vhmIds = QueryUtil.executeNativeQuery("SELECT id, owner FROM hive_ap_auto_provision WHERE id in (SELECT hive_ap_auto_provision_id FROM hive_ap_auto_provision_maces WHERE macaddress ='"
					+serialNum+"')");
			if (!vhmIds.isEmpty()) {
				for (Object obj : vhmIds) {
					Object[] objValues = (Object[])obj;
					HmDomain hmDom = QueryUtil.findBoById(HmDomain.class, ((BigInteger)objValues[1]).longValue());
					if (null != hmDom) {
						Ap newAp = new Ap();
						newAp.setSerialNumber(serialNum);
						newAp.setInternalId(((BigInteger)objValues[0]).longValue());
						if (null == hmDom.getVhmID() || "".equals(hmDom.getVhmID())) {
							newAp.setVhmId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
						} else {
							newAp.setVhmId(hmDom.getVhmID());
						}
						apSearchResponseLst.add(new ApSearchResponse(newAp));
					}
				}
			}
			
			// device inventory table
			DeviceInventory deviceInven = QueryUtil.findBoByAttribute(DeviceInventory.class, "serialNumber", serialNum);
			if (null != deviceInven) {
				HmDomain hmDom = deviceInven.getOwner();
				if (null != hmDom) {
					Ap newAp = new Ap();
					newAp.setSerialNumber(serialNum);
					newAp.setInternalId(deviceInven.getId());
					if (null == hmDom.getVhmID() || "".equals(hmDom.getVhmID())) {
						newAp.setVhmId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
					} else {
						newAp.setVhmId(hmDom.getVhmID());
					}
					apSearchResponseLst.add(new ApSearchResponse(newAp));
				}
			}
		}
		return apSearchResponseLst;
	}

	public static void handleSearchAPwithSerialNum(BeSearchAPFromHMEvent event) throws Exception {
		log.info("handleSearchAPwithSerialNum() in:"+event.getApSearchRequest().getAp().getSerialNumber());

		List<ApSearchResponse> apSearchResponseLst = fetchAPInfoWithSerialNum(event.getApSearchRequest().getAp().getSerialNumber());
		String exceptionMessage = "";

		try {
			BeSearchAPFromHMResult resultEvent = new BeSearchAPFromHMResult();
			resultEvent.setApMac(HmBeCommunicationUtil.getPortalMac());
			resultEvent.setSequenceNum(event.getSequenceNum());
			resultEvent.setSuccess(isNullString(exceptionMessage));
			for (ApSearchResponse apSearchResponse : apSearchResponseLst) {
				apSearchResponse.getAp().setSerialNumber(event.getApSearchRequest().getAp().getSerialNumber());
				apSearchResponse.getStatus().setSuccess(isNullString(apSearchResponse.getStatus().getErrorMsg()));
			}
			resultEvent.setApSearchResponseLst(apSearchResponseLst);
			resultEvent.buildPacket();
			HmBeCommunicationUtil.sendResponse(resultEvent);
		} catch (Exception e) {
			DebugUtil.commonDebugError("handleSearchAPwithSerialNum, send result event catch exception", e);
		}

		log.info("handleSearchAPwithSerialNum() out");
	}

	public static List<ApDeleteResponse> deleteAPInfoWithSerialNum(String serialNum) {
		List<ApDeleteResponse> apDeleteResponseLst= new ArrayList<>();

		if (null != serialNum && !"".equals(serialNum)) {
			try {
				// ap serial number table
				HiveApSerialNumber apNum = QueryUtil.findBoByAttribute(HiveApSerialNumber.class, "serialNumber", serialNum);
				if (null != apNum) {
					QueryUtil.removeBoBase(apNum);

					HmDomain hmDom = apNum.getOwner();
					if (null != hmDom) {
						Ap newAp = new Ap();
						newAp.setSerialNumber(serialNum);
						newAp.setInternalId(apNum.getId());
						if (null == hmDom.getVhmID() || "".equals(hmDom.getVhmID())) {
							newAp.setVhmId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
						} else {
							newAp.setVhmId(hmDom.getVhmID());
						}
						apDeleteResponseLst.add(new ApDeleteResponse(newAp));
					}
				}
			} catch (Exception e) {
				//if error occurs, please set value to errorMsg, else, nothing to do with the object
				ApDeleteResponse apDeleteResponse = new ApDeleteResponse(new Ap());
				apDeleteResponse.getStatus().setErrorMsg(e.getMessage());
				apDeleteResponseLst.add(apDeleteResponse);
			}
			try {
				// device inventory table
				DeviceInventory deviceInven = QueryUtil.findBoByAttribute(DeviceInventory.class, "serialNumber", serialNum);
				if (null != deviceInven) {
					QueryUtil.removeBoBase(deviceInven);

					HmDomain hmDom = deviceInven.getOwner();
					if (null != hmDom) {
						Ap newAp = new Ap();
						newAp.setSerialNumber(serialNum);
						newAp.setInternalId(deviceInven.getId());
						if (null == hmDom.getVhmID() || "".equals(hmDom.getVhmID())) {
							newAp.setVhmId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
						} else {
							newAp.setVhmId(hmDom.getVhmID());
						}
						apDeleteResponseLst.add(new ApDeleteResponse(newAp));
					}
				}
			} catch (Exception e) {
				//if error occurs, please set value to errorMsg, else, nothing to do with the object
				ApDeleteResponse apDeleteResponse = new ApDeleteResponse(new Ap());
				apDeleteResponse.getStatus().setErrorMsg(e.getMessage());
				apDeleteResponseLst.add(apDeleteResponse);
			}
			try {
				// ap auto provision table
				// HiveAP auto provision table
				List<?> vhmIds = QueryUtil.executeNativeQuery("SELECT id, owner FROM hive_ap_auto_provision WHERE id in (SELECT hive_ap_auto_provision_id FROM hive_ap_auto_provision_maces WHERE macaddress ='"
						+serialNum+"')");
				if (!vhmIds.isEmpty()) {
					BusinessUtil bUtil = new BusinessUtil();
					List<HiveApAutoProvision> autoProList = (List<HiveApAutoProvision>) QueryUtil.executeQuery("select distinct bo from "
							+ HiveApAutoProvision.class.getSimpleName() + " as bo join bo.macAddresses as macinfo", null,
							new FilterParams("macinfo", serialNum), null, bUtil.new QueryBoForHiveAP());
					if (!autoProList.isEmpty()) {
						for (HiveApAutoProvision apAuto : autoProList) {
							apAuto.getMacAddresses().remove(serialNum);
						}
						QueryUtil.bulkUpdateBos(autoProList);
					}

					for (Object obj : vhmIds) {
						Object[] objValues = (Object[])obj;
						HmDomain hmDom = QueryUtil.findBoById(HmDomain.class, ((BigInteger)objValues[1]).longValue());
						if (null != hmDom) {
							Ap newAp = new Ap();
							newAp.setSerialNumber(serialNum);
							newAp.setInternalId(((BigInteger)objValues[0]).longValue());
							if (null == hmDom.getVhmID() || "".equals(hmDom.getVhmID())) {
								newAp.setVhmId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
							} else {
								newAp.setVhmId(hmDom.getVhmID());
							}
							apDeleteResponseLst.add(new ApDeleteResponse(newAp));
						}
					}
				}
			} catch (Exception e) {
				//if error occurs, please set value to errorMsg, else, nothing to do with the object
				ApDeleteResponse apDeleteResponse = new ApDeleteResponse(new Ap());
				apDeleteResponse.getStatus().setErrorMsg(e.getMessage());
				apDeleteResponseLst.add(apDeleteResponse);
			}
			try {
				// ap info
				HiveAp apInfo = QueryUtil.findBoByAttribute(HiveAp.class, "serialNumber", serialNum);
				if (null != apInfo) {
					List<Long> ids = new ArrayList<>(1);
					ids.add(apInfo.getId());
					BoMgmt.getMapMgmt().removeHiveAps(ids);
					BeTopoModuleUtil.sendBeDeleteAPConnectRequest(apInfo, true);

					HmDomain hmDom = apInfo.getOwner();
					if (null != hmDom) {
						Ap newAp = new Ap();
						newAp.setSerialNumber(serialNum);
						newAp.setInternalId(apInfo.getId());
						if (null == hmDom.getVhmID() || "".equals(hmDom.getVhmID())) {
							newAp.setVhmId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
						} else {
							newAp.setVhmId(hmDom.getVhmID());
						}
						apDeleteResponseLst.add(new ApDeleteResponse(newAp));
					}
				}

				// ap update image info
				QueryUtil.bulkRemoveBos(HiveApUpdateResult.class, new FilterParams("owner.id = :s1 AND hostname = :s2",
						new Object[]{apInfo.getOwner().getId(), apInfo.getHostName()}), null, null);
			} catch (Exception e) {
				//if error occurs, please set value to errorMsg, else, nothing to do with the object
				ApDeleteResponse apDeleteResponse = new ApDeleteResponse(new Ap());
				apDeleteResponse.getStatus().setErrorMsg(e.getMessage());
				apDeleteResponseLst.add(apDeleteResponse);
			}
		}
		return apDeleteResponseLst;
	}

	public static void handleDeleteAPwithSerialNum(BeDeleteAPFromHMEvent event) throws Exception {
		log.info("handleDeleteAPwithSerialNum() in:"+event.getApDeleteRequest().getAp().getSerialNumber());

		List<ApDeleteResponse> apDeleteResponseLst = deleteAPInfoWithSerialNum(event.getApDeleteRequest().getAp().getSerialNumber());
		String exceptionMessage = "";

		try {
			BeDeleteAPFromHMResult resultEvent = new BeDeleteAPFromHMResult();
			resultEvent.setApMac(HmBeCommunicationUtil.getPortalMac());
			resultEvent.setSequenceNum(event.getSequenceNum());
			resultEvent.setSuccess(isNullString(exceptionMessage));
			for (ApDeleteResponse apDeleteResponse : apDeleteResponseLst) {
				apDeleteResponse.getAp().setSerialNumber(event.getApDeleteRequest().getAp().getSerialNumber());
				apDeleteResponse.getStatus().setSuccess(isNullString(apDeleteResponse.getStatus().getErrorMsg()));
			}
			resultEvent.setApDeleteResponseLst(apDeleteResponseLst);
			resultEvent.buildPacket();
			HmBeCommunicationUtil.sendResponse(resultEvent);
		} catch (Exception e) {
			DebugUtil.commonDebugError("handleDeleteAPwithSerialNum, send result event catch exception", e);
		}

		log.info("handleDeleteAPwithSerialNum() out");
	}
	
	public static void handleUserGroupInfoByVhmId(BeQueryGroupInfoFromHMEvent event) throws Exception {
		log.info("handleUserGroupInfoByVhmId() vhmId is :"+event.getVhmId());
		
		try {
			// all user groups
			List<?> groups = QueryUtil.executeQuery("select groupName from " + HmUserGroup.class.getSimpleName(), null, new FilterParams("owner.vhmID", event.getVhmId()));
			List<String> groupNames = new ArrayList<>();
			for (Object obj : groups) {
				groupNames.add((String)obj);
			}
			
			// all user and its user groups
			 Map<String, String> userInfos = new HashMap<>();
			 List<HmUser> users = QueryUtil.executeQuery(HmUser.class, null, new FilterParams("owner.vhmID", event.getVhmId()));
			 for (HmUser user : users) {
				 userInfos.put(user.getEmailAddress(), user.getUserGroup().getGroupName());
			 }
			BeQueryGroupInfoFromHMResult resultEvent = new BeQueryGroupInfoFromHMResult();
			resultEvent.setApMac(HmBeCommunicationUtil.getPortalMac());
			resultEvent.setSequenceNum(event.getSequenceNum());
			resultEvent.setUserInfos(userInfos);
			resultEvent.setGroupNames(groupNames);
			resultEvent.buildPacket();
			HmBeCommunicationUtil.sendResponse(resultEvent);
		} catch (Exception e) {
			log.error("handleUserGroupInfoByVhmId, send result event catch exception", e);
		}

		log.info("handleUserGroupInfoByVhmId() out");
	}

	private static boolean isNullString(String str) {
		return str == null || "".equals(str);
	}

	private static void sendRecieveRequestResponse(BePortalHMPayloadEvent event)
	{
		try {
			BePortalHMPayloadResultEvent response = new BePortalHMPayloadResultEvent();
			response.setApMac(event.getApMac());
			response.setSequenceNum(event.getSequenceNum());
			response.setSuccess(true);
			response.buildPacket();
			HmBeCommunicationUtil.sendResponse(response);
		} catch (Exception e) {
			DebugUtil.commonDebugError("sendRecieveRequestResponse() send response event catch exception", e);
		}
	}

	public static int execCommand(String cmd) {
		try {
			String string_Path_Array[] = new String[3];
			string_Path_Array[0] = "bash";
			string_Path_Array[1] = "-c";
			string_Path_Array[2] = cmd;

			Process p = Runtime.getRuntime().exec(string_Path_Array);

			p.waitFor();

			return p.exitValue();
		} catch (Exception e) {
			log.error("execCommand", e);
			return 255;
		}
	}

	/*
	 * VHM if can upgrade to higher version
	 */
	public static void handleVersionInfo(BeVersionUpdateInfoEvent event) {
		if (null != event) {
			try {
				// update the info in database
				QueryUtil.bulkRemoveBos(HhmUpgradeVersionInfo.class, null);

				List<HhmUpgradeVersionInfo> versionInfoList = event.getVersionInfoList();

				if (null != versionInfoList && !versionInfoList.isEmpty()) {
					QueryUtil.bulkCreateBos(versionInfoList);
				}
			} catch (Exception e) {
				DebugUtil.commonDebugError("handleVersionInfo() save version info in database catch exception", e);
			}
		}
	}

	public class QueryBoForHiveAP implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (bo instanceof HiveApAutoProvision) {
				HiveApAutoProvision autoProvision = (HiveApAutoProvision) bo;

				if (autoProvision.getMacAddresses() != null) {
					autoProvision.getMacAddresses().size();
				}
			}

			return null;
		}
	}

	public class QueryBoForStartHere implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			/*
			 * For welcome page
			 */
			if(bo instanceof MgmtServiceDns) {
				MgmtServiceDns dns = (MgmtServiceDns)bo;

				if(dns.getDnsInfo() != null) {
					dns.getDnsInfo().size();
				}
			} else if(bo instanceof MgmtServiceTime) {
				MgmtServiceTime time = (MgmtServiceTime)bo;

				if(time.getTimeInfo() != null) {
					time.getTimeInfo().size();
				}
			}

			return null;
		}
	}

	public static void powerOffFromPortal(BePoweroffHmolEvent event) {
		log.debug("Poweroff from portal's request.");

		int status = HAUtil.getHAMonitor().getCurrentStatus().getStatus();
		if (HAStatus.STATUS_HA_MASTER == status
				|| HAStatus.STATUS_HA_SLAVE == status) {
			// execute script
			int exitValue = execCommand(HmBeOsUtil.getHAScriptsPath() + "ha_stop.sh"
					+ " >/HiveManager/ha/logs/ha_stop"
					+ new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log 2>&1");
			log.info("execute ha_stop.sh, exit value is " + exitValue);
			if (exitValue != 0) {
				log.error("execute ha_stop.sh failed, exit value is " + exitValue);
			} else {
				// do nothing just logging
				log.info("HA stopped success.");
			}
		}

		BeOperateHMCentOSImpl.execShutdownSoft();
	}

}