package com.ah.apiengine.agent.subagent.impl;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.ah.apiengine.agent.HmApiEngineException;
import com.ah.apiengine.agent.subagent.UserAgent;
import com.ah.apiengine.element.UserOperation;
import com.ah.be.admin.hhmoperate.HHMUserMgmt;
import com.ah.be.app.DebugUtil;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.SessionKeys;
import com.ah.ui.actions.monitor.CurrentUserCache;
import com.ah.util.MgrUtil;

public class UserAgentImpl implements UserAgent {

	@Override
	public void execute(UserOperation operation) throws HmApiEngineException {
		if (operation == null) {
			throw new HmApiEngineException("Parameter 'operation' is null.");
		}

		if (operation.getVhmName() == null || operation.getVhmName().trim().length() == 0) {
			throw new HmApiEngineException("'vhmName' is necessary.");
		}

		switch (operation.getOperType()) {
		case UserOperation.USER_OPER_TYPE_CREATE: {
			// create user
			if (operation.getUserName() == null || operation.getUserName().trim().length() == 0) {
				throw new HmApiEngineException("'userName' is necessary.");
			}

			HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", operation
					.getVhmName());
			if (domain == null) {
				throw new HmApiEngineException("VHM '" + operation.getVhmName()
						+ "' is not exists.");
			}

			HmUserGroup userGroup = QueryUtil.findBoByAttribute(HmUserGroup.class,
					"groupAttribute", operation.getUserGroupAttr(), domain.getId());
			if (userGroup == null) {
				throw new HmApiEngineException("User group (attribute="
						+ operation.getUserGroupAttr() + ",owner='" + operation.getVhmName()
						+ "') is not exists.");
			}

			// check user name
			List<HmUser> list = QueryUtil.executeQuery(HmUser.class, null, new FilterParams(
					"lower(userName)", operation.getUserName().toLowerCase()), domain.getId());
			if (!list.isEmpty()) {
				throw new HmApiEngineException(MgrUtil.getUserMessage("error.objectExists",
						operation.getUserName()));
			}

			HmUser user = new HmUser();
			user.setEmailAddress(operation.getEmailAddr());
			user.setOwner(domain);
			user.setPassword(operation.getPassword());
			user.setUserFullName(operation.getUserFullName());
			user.setUserGroup(userGroup);
			user.setUserName(operation.getUserName());

			try {
				QueryUtil.createBo(user);
			} catch (Exception e) {
				DebugUtil.commonDebugError("UserAgentImpl.execute(): create user catch exception",
						e);

				// throw exception
				throw new HmApiEngineException("Create user error. " + e.getMessage());
			}

			break;
		}

		case UserOperation.USER_OPER_TYPE_REMOVE: {
			// remove user
			if (operation.getUserName() == null || operation.getUserName().trim().length() == 0) {
				throw new HmApiEngineException("'userName' is necessary.");
			}

			HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", operation
					.getVhmName());
			if (domain == null) {
				throw new HmApiEngineException("VHM '" + operation.getVhmName()
						+ "' is not exists.");
			}

			// check default user
			HmUser user = QueryUtil.findBoByAttribute(HmUser.class, "userName", operation
					.getUserName(), domain.getId());
			if (user == null) {
				throw new HmApiEngineException("User '" + operation.getUserName()
						+ "' is not exists.");
			}

			if (user.getDefaultFlag()) {
				throw new HmApiEngineException("Default user is not able to be removed.");
			}

			// check user login
			for (HttpSession activeUser : CurrentUserCache.getInstance().getActiveSessions()) {
				HmUser sessionUser=null;
				try{
					sessionUser = (HmUser) activeUser.getAttribute(SessionKeys.USER_CONTEXT);
				} catch (Exception e){
					DebugUtil.commonDebugError("UserAgentImpl.execute(): Session already invalidated",
							e);
					continue;
				}
				if (sessionUser.getUserName().equalsIgnoreCase(operation.getUserName())
						&& sessionUser.getDomain().getDomainName().equalsIgnoreCase(
								domain.getDomainName())) {
					throw new HmApiEngineException("Uable to remove user '"
							+ operation.getUserName()
							+ ", this user is currently logged in to the HM.");
				}
			}

			try {
				QueryUtil.removeBo(HmUser.class, user.getId());
			} catch (Exception e) {
				DebugUtil.commonDebugError("UserAgentImpl.execute(): remove user catch exception",
						e);

				// throw exception
				throw new HmApiEngineException("Remove user error. " + e.getMessage());
			}

			break;
		}

		case UserOperation.USER_OPER_TYPE_REINIT_PASSWORD: {
			// change user password
			HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", operation
					.getVhmName());
			if (domain == null) {
				throw new HmApiEngineException("VHM '" + operation.getVhmName()
						+ "' is not exists.");
			}

			HmUser defaultUser = QueryUtil.findBoByAttribute(HmUser.class, "defaultFlag", true,
					domain.getId());
			if (defaultUser == null) {
				throw new HmApiEngineException("Unable to find default user of vhm '"
						+ operation.getVhmName() + "'");
			}

			// reset password of default user

			try {
				HHMUserMgmt.getInstance().resetUserPassword(defaultUser);
			} catch (Exception e) {
				DebugUtil.commonDebugError(
						"UserAgentImpl.execute(): reset user password catch exception", e);

				// throw exception
				throw new HmApiEngineException("Reset user password error. " + e.getMessage());
			}

			break;
		}

		case UserOperation.USER_OPER_TYPE_SEND_CREDENT: {
//			// send credent
//			HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", operation
//					.getVhmName());
//			if (domain == null) {
//				throw new HmApiEngineException("VHM '" + operation.getVhmName()
//						+ "' is not exists.");
//			}
//
//			HmUser defaultUser = QueryUtil.findBoByAttribute(HmUser.class, "defaultFlag", true,
//					domain.getId());
//			if (defaultUser == null) {
//				throw new HmApiEngineException("Unable to find default user of vhm '"
//						+ operation.getVhmName() + "'");
//			}
//			
//			try {
//				HHMUserMgmt.getInstance().notifyEmail2DefaultUser(defaultUser, operation.getLoginUrl(), domain);
//			} catch (Exception e) {
//				DebugUtil.commonDebugError(
//						"UserAgentImpl.execute(): send credent to user catch exception", e);
//
//				// throw exception
//				throw new HmApiEngineException("Send credent error. " + e.getMessage());
//			}
//			
			break;
		}

//		case UserOperation.USER_OPER_TYPE_SEND_NEWURL: {
//			
//			HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", operation
//					.getVhmName());
//			if (domain == null) {
//				throw new HmApiEngineException("VHM '" + operation.getVhmName()
//						+ "' is not exists.");
//			}
//			
//			HmUser defaultUser = QueryUtil.findBoByAttribute(HmUser.class, "defaultFlag", true,
//					domain.getId());
//			if (defaultUser == null) {
//				throw new HmApiEngineException("Unable to find default user of vhm '"
//						+ operation.getVhmName() + "'");
//			}
//			
//			try {
//				HHMUserMgmt.getInstance().notifyNewURL2DefaultUser(defaultUser, operation.getLoginUrl(), domain);
//			} catch (Exception e) {
//				DebugUtil.commonDebugError(
//						"UserAgentImpl.execute(): send new url to user catch exception", e);
//
//				// throw exception
//				throw new HmApiEngineException("Send new url error. " + e.getMessage());
//			}
//			
//			break;
//		}
		default:
			break;
		}
	}

}