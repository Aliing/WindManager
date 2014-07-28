package com.ah.apiengine.agent.subagent.impl;

import java.util.List;

import com.ah.apiengine.agent.HmApiEngineException;
import com.ah.apiengine.agent.subagent.VhmAgent;
import com.ah.apiengine.element.VhmOperation;
import com.ah.be.admin.hhmoperate.HHMUserMgmt;
import com.ah.be.admin.hhmoperate.HHMngineOperate;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.DomainMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;

public class VhmAgentImpl implements VhmAgent {

	private final DomainMgmt domainMgmt	= BoMgmt.getDomainMgmt();

	@Override
	public void execute(VhmOperation operation) throws HmApiEngineException {
		if (operation == null) {
			throw new HmApiEngineException("Parameter 'operation' is null.");
		}

		if (operation.getVhmName() == null || operation.getVhmName().trim().length() == 0) {
			throw new HmApiEngineException("'vhmName' is necessary.");
		}

		switch (operation.getOperType()) {
		case VhmOperation.VHM_OPER_TYPE_CREATE: {
			// create domain

			// check domain name
			List<?> boIds = QueryUtil.executeQuery("select id from "
					+ HmDomain.class.getSimpleName(), null, new FilterParams("lower(domainName)",
					operation.getVhmName().toLowerCase()));
			if (!boIds.isEmpty()) {
				throw new HmApiEngineException(MgrUtil.getUserMessage("error.objectExists",
						operation.getVhmName()));
			}

//			// check max VHM number
//			int maxVHMCount = HmBeLicenseUtil.getLicenseInfo().getVhmNumber();
//			if (CacheMgmt.getInstance().getCacheDomainCount() >= maxVHMCount) {
//				throw new HmApiEngineException(MgrUtil.getUserMessage(
//						"error.vhm.outofLincense.maximum", String.valueOf(maxVHMCount)));
//			}

			// check remaining ap number
			int remaining = BoMgmt.getDomainMgmt().getRemainingMaxAPNum();
			if (remaining < operation.getApNum()) {
				throw new HmApiEngineException(MgrUtil
						.getUserMessage("error.vhm.create.noremainingap"));
			}

			// check email address
			boIds = QueryUtil
					.executeQuery("select id from " + HmUser.class.getSimpleName(), null,
							new FilterParams("lower(emailAddress)", operation.getEmailAddr()
									.toLowerCase()));

			if (!boIds.isEmpty()) {
				throw new HmApiEngineException("E-mail address '" + operation.getEmailAddr()
						+ "' already exists.");
			}
			
			// check gm support
			if (operation.getGmLight() == VhmOperation.GM_LIGHT_ENABLED && !HmBeLicenseUtil.GM_LITE_LICENSE_VALID) {
				throw new HmApiEngineException("User manager not be supported.");
			}

			HmDomain domain = new HmDomain();
			domain.setDomainName(operation.getVhmName());
			domain.setMaxApNum(operation.getApNum());
			domain.setSupportGM(operation.getGmLight() == VhmOperation.GM_LIGHT_ENABLED);
			domain.setVhmID(operation.getVhmId());
			domain.setSupportFullMode(operation.isEnableEnterprise());
			domain.setBccEmail(operation.getCcEmailAddr());

			try {
				domainMgmt.createDomain(domain);

				HmBeLogUtil.generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.vhm.create.success",
						domain.getDomainName()));
			} catch (Exception e) {

				HmBeLogUtil.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.vhm.unable.create",
						domain.getDomainName()));
				DebugUtil.commonDebugError("VhmAgentImpl.execute(): create domain catch exception",
						e);

				// throw exception
				throw new HmApiEngineException("Create domain error. " + e.getMessage());
			}

			// create default user
			try {
				HHMUserMgmt.getInstance().createDefaultUserAccount(operation, domain);
				
				HmBeLogUtil.generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.user.create.success",
						operation.getUserName()));
			} catch (Exception e) {
				HmBeLogUtil.generateAuditLog(HmAuditLog.STATUS_FAILURE, 
						 MgrUtil.getUserMessage("hm.audit.log.user.unable.create",
									operation.getUserName()));
				DebugUtil
						.commonDebugError(
								"VhmAgentImpl.execute(): create default user for domain catch exception",
								e);

				// throw exception
				throw new HmApiEngineException("Create default user error. " + e.getMessage());
			}

			break;
		}

		case VhmOperation.VHM_OPER_TYPE_REMOVE: {
			// remove domain

			// check home domain
			if (operation.getVhmName().trim().equals(HmDomain.HOME_DOMAIN)) {
				throw new HmApiEngineException("VHM 'home' is not able to be removed.");
			}

			HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "lower(domainName)",
					operation.getVhmName().toLowerCase());
			if (domain == null) {
				throw new HmApiEngineException("VHM '" + operation.getVhmName()
						+ "' is not exists.");
			}

			try {
				domainMgmt.removeDomain(domain.getId(), true);
				
				HmBeLogUtil.generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.vhm.remove.success",
						domain.getDomainName()));
			} catch (Exception e) {
				HmBeLogUtil.generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.vhm.unable.remove",
						domain.getDomainName()));
				DebugUtil.commonDebugError("VhmAgentImpl.execute(): remove domain catch exception",
						e);

				// throw exception
				throw new HmApiEngineException("Remove domain error. " + e.getMessage());
			}

			break;
		}

		case VhmOperation.VHM_OPER_TYPE_STATUSCHANGE: {
			// disable domain

			// check home domain
			if (operation.getVhmName().trim().equals(HmDomain.HOME_DOMAIN)) {
				throw new HmApiEngineException("VHM 'home' is not able to be disabled.");
			}

			HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "lower(domainName)",
					operation.getVhmName().toLowerCase());
			if (domain == null) {
				throw new HmApiEngineException("VHM '" + operation.getVhmName()
						+ "' is not exists.");
			}

			try {
				// domain.setRunStatus(HmDomain.DOMAIN_DISABLE_STATUS);
				domain.setRunStatus(operation.getStatus());
				domainMgmt.updateDomain(domain);
			} catch (Exception e) {
				DebugUtil.commonDebugError(
						"VhmAgentImpl.execute(): update domain status catch exception", e);

				// throw exception
				throw new HmApiEngineException("Update domain error. " + e.getMessage());
			}

			break;
		}

		case VhmOperation.VHM_OPER_TYPE_MODIFY: {
			// modify domain

			// query domain
			HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "lower(domainName)",
					operation.getVhmName().toLowerCase());
			if (domain == null) {
				throw new HmApiEngineException("VHM '" + operation.getVhmName()
						+ "' is not exists.");
			}

			// check remaining ap number
			int remaining = BoMgmt.getDomainMgmt().getRemainingMaxAPNum() + domain.getMaxApNum();
			if (remaining < operation.getApNum()) {
				throw new HmApiEngineException(MgrUtil
						.getUserMessage("error.vhm.create.noremainingap"));
			}
			
			// check gm support
			if (operation.getGmLight() == VhmOperation.GM_LIGHT_ENABLED && !HmBeLicenseUtil.GM_LITE_LICENSE_VALID) {
				throw new HmApiEngineException("User manager not be supported.");
			}

			domain.setMaxApNum(operation.getApNum());
			domain.setSupportGM(operation.getGmLight() == VhmOperation.GM_LIGHT_ENABLED);
			domain.setRunStatus(operation.getStatus());
			domain.setVhmID(operation.getVhmId());
			domain.setSupportFullMode(operation.isEnableEnterprise());
			domain.setBccEmail(operation.getCcEmailAddr());

			try {
				domainMgmt.updateDomain(domain);
			} catch (Exception e) {
				DebugUtil.commonDebugError("VhmAgentImpl.execute(): modify domain catch exception",
						e);

				// throw exception
				throw new HmApiEngineException("Modify domain error. " + e.getMessage());
			}

			// create or update default user
			// check user name unique
			List<HmUser> userList = QueryUtil.executeQuery(HmUser.class, null, new FilterParams(
					"lower(userName)", operation.getUserName().toLowerCase()), domain.getId());
			if (!userList.isEmpty()) {
				for (HmUser user : userList) {
					if (!user.getDefaultFlag()) {
						// not itself
						throw new HmApiEngineException(MgrUtil.getUserMessage("error.objectExists",
								operation.getUserName()));
					}
				}
			}
			
			// check email address unique
			List<HmUser> userList_ = QueryUtil.executeQuery(HmUser.class, null, new FilterParams(
					"lower(emailAddress)", operation.getEmailAddr().toLowerCase()));
			if (!userList_.isEmpty()) {
				for (HmUser user : userList_) {
					if (!(user.getDefaultFlag() && user.getDomain().getDomainName().equals(operation.getVhmName()))) {
						// not itself
						throw new HmApiEngineException(MgrUtil.getUserMessage("error.objectExists",
								operation.getEmailAddr()));
					}
				}
			}
			
			try {
				HHMUserMgmt.getInstance().updateDefaultUserAccount(operation, domain);
			} catch (Exception e) {
				DebugUtil
						.commonDebugError(
								"VhmAgentImpl.execute(): create or update default user for domain catch exception",
								e);

				// throw exception
				throw new HmApiEngineException("Create or update default user error. "
						+ e.getMessage());
			}

			break;
		}

		case VhmOperation.VHM_OPER_TYPE_BACKUP: {
			HHMngineOperate.backupForEgine(operation.getVhmName(), operation.getSshServer(),
					operation.getSshPort(), operation.getSshUserName(), operation.getSshPwd(),
					operation.getSshPath());

			break;
		}

		case VhmOperation.VHM_OPER_TYPE_RESTORE: {
			HHMngineOperate.restoreForEgine(operation.getVhmName(), operation.getSshServer(),
					operation.getSshPort(), operation.getSshUserName(), operation.getSshPwd(),
					operation.getSshPath(), operation.getFileName());

			break;
		}

		case VhmOperation.VHM_OPER_TYPE_SEND_CREDENT: {
			// send credent
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
			
			try {
				HHMUserMgmt.getInstance().notifyEmail2DefaultUser(operation, domain, defaultUser);
			} catch (Exception e) {
				DebugUtil.commonDebugError(
						"VhmAgentImpl.execute(): send credent to user catch exception", e);

				// throw exception
				throw new HmApiEngineException("Send credent error. " + e.getMessage());
			}
			
			break;
		}

		default:
			break;
		}
	}

	public void test() {
//		VhmOperation operation = new VhmOperation();
//		operation.setVhmName("tt");
//		operation.setVhmId("VHM-DD1122");
//		operation.setApNum(1);
//		operation.setDnsUrl("https://10.16.0.67");
//		operation.setEmailAddr("jzhou@aerohive.com");
//		operation.setNotifyFlag(true);
//		operation.setOperType(VhmOperation.VHM_OPER_TYPE_SEND_CREDENT);
//		operation.setUserAccountType(VhmOperation.USER_TYPE_PLAN_EVAL);
//		operation.setUserName("juju");
//		operation.setValidDays(45);
		
		VhmOperation operation = new VhmOperation();
		operation.setVhmName("tt");
		operation.setVhmId("VHM-DD1122");
		operation.setApNum(1);
		operation.setEmailAddr("jzhou@aerohive.com");
		operation.setOperType(VhmOperation.VHM_OPER_TYPE_CREATE);
		operation.setUserName("juju");
		operation.setNotifyFlag(true);
		operation.setCcEmailAddr("xxia@aerohive.com");
		operation.setUserAccountType(VhmOperation.USER_TYPE_PLAN_EVAL);
		operation.setValidDays(45);
		
		try {
			execute(operation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}