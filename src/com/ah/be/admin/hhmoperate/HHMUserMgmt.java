package com.ah.be.admin.hhmoperate;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import com.ah.apiengine.element.VhmOperation;
import com.ah.be.admin.util.EmailElement;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.mo.VhmInfo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;

public class HHMUserMgmt {

	private static HHMUserMgmt instance;

	/**
	 * Construct method
	 */
	private HHMUserMgmt() {
	}

	public synchronized static HHMUserMgmt getInstance() {
		if (instance == null) {
			instance = new HHMUserMgmt();
		}

		return instance;
	}

	private void sendEmail(String mailContent, String subject, String toEmail) {
		EmailElement email = new EmailElement();
		email.setMustBeSent(true);
		email.setMailContent(mailContent);
		email.setSubject(subject);
		email.setToEmail(toEmail);
		email.setContentType("text/html");
		email.addShowfile(AhDirTools.getHmRoot() + "images" + File.separator + "company_logo.png");

		HmBeAdminUtil.sendEmail(email);
	}

//	private void sendEmailWithCC(String mailContent, String subject, String toEmail,
//			String ccEmail, String bccEmail) {
//		EmailElement email = new EmailElement();
//		email.setMustBeSent(true);
//		email.setMailContent(mailContent);
//		email.setSubject(subject);
//		email.setToEmail(toEmail);
//		email.setCcEmail(ccEmail);
//		email.setBccEmail(bccEmail);
//		email.setContentType("text/html");
//		email.addShowfile(AhDirTools.getHmRoot() + "images" + File.separator + "company_logo.png");
//
//		HmBeAdminUtil.sendEmail(email);
//	}

	/**
	 * reset user account password, and email new password to user
	 * 
	 * @param user -
	 * @throws Exception -
	 */
	public void resetUserPassword(HmUser user) throws Exception {
		String password_clear = NmsUtil.genRandomString(8);
		user.setPassword(MgrUtil.digest(password_clear));
		QueryUtil.updateBo(user);

//		String toEmail = user.getEmailAddress();
//		if (toEmail == null || toEmail.trim().isEmpty()) {
//			throw new Exception("Unable to send notify email, user '" + user.getUserName()
//					+ "' email address is empty.");
//		}
//
//		String subject = MgrUtil.getUserMessage("email.account.resetPasswd.title");
//		String mailContent = getHtmlContent4ResetPasswd(user, password_clear);
//
//		sendEmail(mailContent, subject, toEmail);
	}

	public void resetUserPassword(HmUser user, String clearPassword) throws Exception {
		user.setPassword(MgrUtil.digest(clearPassword));
		QueryUtil.updateBo(user);

//		String toEmail = user.getEmailAddress();
//		if (toEmail == null || toEmail.trim().isEmpty()) {
//			throw new Exception("Unable to send notify email, user '" + user.getUserName()
//					+ "' email address is empty.");
//		}
//
//		String subject = MgrUtil.getUserMessage("email.account.resetPasswd.title");
//		String mailContent = getHtmlContent4ResetPasswd(user, clearPassword);
//
//		sendEmail(mailContent, subject, toEmail);
	}

//	private String getHtmlContent4ResetPasswd(HmUser user, String password) {
//		StringBuffer content = new StringBuffer();
//		content.append("<html><body><table border=\"0\">");
//		content.append("<tr><td>");
//		content.append(MgrUtil.getUserMessage("email.account.resetPasswd.para1"));
//		content.append("</td></tr><br>");
//		content.append("<tr><td><table border=\"0\">");
//		content.append("<tr><td width=\"150px\">");
//		content.append("Domain Name:");
//		content.append("</td><td>");
//		content.append(user.getOwner().getDomainName());
//		content.append("</td></tr><tr><td width=\"150px\">");
//		content.append("Admin Name:");
//		content.append("</td><td>");
//		content.append(user.getUserName());
//		content.append("</td></tr><tr><td width=\"150px\">");
//		content.append("New Password:");
//		content.append("</td><td>");
//		content.append(password);
//		content.append("</td></tr></table></td></tr><br><br>");
//
//		/*
//		 * copyright
//		 */
//		content.append("<tr><td>");
//		content.append("Copyright ");
//
//		/*
//		 * year
//		 */
//		content.append(Calendar.getInstance().get(Calendar.YEAR));
//		content.append(" Aerohive Networks Inc");
//		content.append("</td></tr>");
//
//		/*
//		 * logo
//		 */
//		content.append("<tr><td>");
//		content.append("<img src=\"cid:company_logo.png\" />");
//		content.append("</td></tr>");
//
//		content.append("</table></body><html>");
//
//		return content.toString();
//	}

	/**
	 * create a new user account with random password, and then email the information.
	 * 
	 * @param vhmOperation -
	 * @param domain -
	 * @throws Exception -
	 */
	public void createDefaultUserAccount(VhmOperation vhmOperation, HmDomain domain)
			throws Exception {
		String groupName = vhmOperation.getUserAccountType() == VhmOperation.USER_TYPE_PLAN_EVAL ? HmUserGroup.PLANNING
				: HmUserGroup.CONFIG;
		FilterParams filterParams = new FilterParams("groupName", groupName);
		List<HmUserGroup> list = QueryUtil.executeQuery(HmUserGroup.class, null, filterParams,
				domain.getId());
		if (list.isEmpty()) {
			throw new Exception("Unable to create default user of domain('"
					+ domain.getDomainName() + "'). " + groupName + " user group is not exists.");
		}
		HmUserGroup userGroup = list.get(0);

		String password_clear = NmsUtil.genRandomString(8);
		HmUser user = new HmUser();
		user.setDefaultFlag(true);
		user.setUserName(vhmOperation.getUserName());
		user.setUserFullName(vhmOperation.getUserFullName());
		user.setEmailAddress(vhmOperation.getEmailAddr());
		user.setPassword(MgrUtil.digest(password_clear));
		user.setUserGroup(userGroup);
		user.setAccessMyhive(true);
		user.setOwner(domain);
		QueryUtil.createBo(user);

//		// email new account to user
//		if (!vhmOperation.isNotifyFlag()) {
//			// not notify customer
//			return;
//		}
//
//		String subject = MgrUtil.getUserMessage("email.account.created.title");
//		String mailContent = getHtmlContent4createAccount(vhmOperation, password_clear);
//
//		/**
//		 * because the cc email in vhm operation is for sales, so we need send it as bcc email
//		 */
//		sendEmailWithCC(mailContent, subject, vhmOperation.getEmailAddr(), "", vhmOperation
//				.getCcEmailAddr());
	}

	public void createDefaultUserAccount(VhmInfo vhmInfo, HmDomain domain) throws Exception {
		String groupName = vhmInfo.getVhmType() == VhmOperation.USER_TYPE_PLAN_EVAL ? HmUserGroup.PLANNING
				: HmUserGroup.CONFIG;
		FilterParams filterParams = new FilterParams("groupName", groupName);
		List<HmUserGroup> list = QueryUtil.executeQuery(HmUserGroup.class, null, filterParams,
				domain.getId());
		if (list.isEmpty()) {
			throw new Exception("Unable to create default user of domain('"
					+ domain.getDomainName() + "'). " + groupName + " user group is not exists.");
		}
		HmUserGroup userGroup = list.get(0);

		String password_clear = vhmInfo.getVhmAdminClearPassword();
		HmUser user = new HmUser();
		user.setDefaultFlag(true);
		user.setUserName(vhmInfo.getVhmAdminName());
		user.setUserFullName(vhmInfo.getVhmAdminFullName());
		user.setEmailAddress(vhmInfo.getVhmAdminEmailAddress());
		user.setPassword(MgrUtil.digest(password_clear));
		user.setUserGroup(userGroup);
		user.setAccessMyhive(true);
		user.setOwner(domain);
		QueryUtil.createBo(user);

//		// email new account to user
//		if (!vhmInfo.isNeedNotifyFlag()) {
//			// not notify customer
//			return;
//		}
//
//		String subject = MgrUtil.getUserMessage("email.account.created.title");
//		String mailContent = getHtmlContent4createAccount(vhmInfo, password_clear);
//
//		/**
//		 * because the cc email in vhm operation is for sales, so we need send it as bcc email
//		 */
//		sendEmailWithCC(mailContent, subject, vhmInfo.getVhmAdminEmailAddress(), "", vhmInfo
//				.getCcEmailAddress());
	}

	private String getHtmlContent4createAccount(VhmOperation vhmOperation, String password) {
		String accountType;
		if (vhmOperation.getUserAccountType() == VhmOperation.USER_TYPE_PLAN_EVAL) {
			accountType = " Planner";
		} else {
			accountType = " Demo";
		}

		String validDaysStr = "";
		if (vhmOperation.getValidDays() > 0) {
			validDaysStr = ", and this account is valid for " + vhmOperation.getValidDays()
					+ " days";
		}

		StringBuilder content = new StringBuilder();
		content.append("<html><body><table border=\"0\">");
		content.append("<tr><td>");
		content.append(MgrUtil.getUserMessage("email.account.created.para1",
				new String[] { accountType, validDaysStr, 
						NmsUtil.getOEMCustomer().getRegisterUrl()}));
		content.append("</td></tr><br><tr><td>");
		content.append(MgrUtil.getUserMessage("email.account.created.para2",
				new String[] {NmsUtil.getOEMCustomer().getSalesMail()}));
		content.append("</td></tr><br><tr><td>");
		content.append(MgrUtil.getUserMessage("email.account.created.para3"));
		content.append("</td></tr><br>");
		content.append("<tr><td><table border=\"0\">");
		content.append("<tr><td width=\"150px\">");
		content.append("Login URL:");
		content.append("</td><td>");
		content.append(vhmOperation.getDnsUrl());
		content.append("</td></tr><tr><td width=\"150px\">");
		content.append("Admin Name:");
		content.append("</td><td>");
		content.append(vhmOperation.getUserName());
		content.append("</td></tr><tr><td width=\"150px\">");
		content.append("Password:");
		content.append("</td><td>");
		content.append(password);
		content.append("</td></tr><tr><td width=\"150px\">");
		content.append("Domain Name:");
		content.append("</td><td>");
		content.append(vhmOperation.getVhmName());
		content.append("</td></tr><tr><td width=\"150px\">");
		content.append("VHM ID:");
		content.append("</td><td>");
		content.append(vhmOperation.getVhmId());
		content.append("</td></tr></table></td></tr><br><br>");

		/*
		 * copyright
		 */
		content.append("<tr><td>");
		content.append("Copyright ");

		/*
		 * year
		 */
		content.append(Calendar.getInstance().get(Calendar.YEAR));
		content.append(NmsUtil.getOEMCustomer().getCompanyFullName());
		content.append("</td></tr>");

		/*
		 * logo
		 */
		content.append("<tr><td>");
		content.append("<img src=\"cid:company_logo.png\" />");
		content.append("</td></tr>");

		content.append("</table></body><html>");

		return content.toString();
	}

//	private String getHtmlContent4createAccount(VhmInfo vhmInfo, String password) {
//		String accountType = "";
//		if (vhmInfo.getVhmType() == VhmOperation.USER_TYPE_PLAN_EVAL) {
//			accountType = " Planner";
//		} else {
//			accountType = " Demo";
//		}
//
//		String validDaysStr = "";
//		if (vhmInfo.getValidDays() > 0) {
//			validDaysStr = ", and this account is valid for " + vhmInfo.getValidDays() + " days";
//		}
//
//		StringBuffer content = new StringBuffer();
//		content.append("<html><body><table border=\"0\">");
//		content.append("<tr><td>");
//		content.append(MgrUtil.getUserMessage("email.account.created.para1",
//				new String[] { accountType, validDaysStr }));
//		content.append("</td></tr><br><tr><td>");
//		content.append(MgrUtil.getUserMessage("email.account.created.para2"));
//		content.append("</td></tr><br><tr><td>");
//		content.append(MgrUtil.getUserMessage("email.account.created.para3"));
//		content.append("</td></tr><br>");
//		content.append("<tr><td><table border=\"0\">");
//		content.append("<tr><td width=\"150px\">");
//		content.append("Login URL:");
//		content.append("</td><td>");
//		content.append(vhmInfo.getUrl());
//		content.append("</td></tr><tr><td width=\"150px\">");
//		content.append("Admin Name:");
//		content.append("</td><td>");
//		content.append(vhmInfo.getVhmAdminName());
//		content.append("</td></tr><tr><td width=\"150px\">");
//		content.append("Password:");
//		content.append("</td><td>");
//		content.append(password);
//		content.append("</td></tr><tr><td width=\"150px\">");
//		content.append("Domain Name:");
//		content.append("</td><td>");
//		content.append(vhmInfo.getVhmName());
//		content.append("</td></tr><tr><td width=\"150px\">");
//		content.append("VHM ID:");
//		content.append("</td><td>");
//		content.append(vhmInfo.getVhmId());
//		content.append("</td></tr></table></td></tr><br><br>");
//
//		/*
//		 * copyright
//		 */
//		content.append("<tr><td>");
//		content.append("Copyright ");
//
//		/*
//		 * year
//		 */
//		content.append(Calendar.getInstance().get(Calendar.YEAR));
//		content.append(" Aerohive Networks Inc");
//		content.append("</td></tr>");
//
//		/*
//		 * logo
//		 */
//		content.append("<tr><td>");
//		content.append("<img src=\"cid:company_logo.png\" />");
//		content.append("</td></tr>");
//
//		content.append("</table></body><html>");
//
//		return content.toString();
//	}

	/**
	 * update user account with random password, and then email the information.
	 * 
	 * @param vhmOperation -
	 * @param domain -
	 * @throws Exception -
	 */
	public void updateDefaultUserAccount(VhmOperation vhmOperation, HmDomain domain)
			throws Exception {
		FilterParams filterParams = new FilterParams("defaultFlag", true);
		List<HmUser> list = QueryUtil
				.executeQuery(HmUser.class, null, filterParams, domain.getId());
		// need to create the default user
		if (list.isEmpty()) {
//			createDefaultUserAccount(vhmOperation, domain);
//			return;
			
			throw new Exception("Default user not exists.");
		}

		HmUser user = list.get(0);
		user.setUserFullName(vhmOperation.getUserFullName());
		user.setUserName(vhmOperation.getUserName());
		user.setEmailAddress(vhmOperation.getEmailAddr());
		QueryUtil.updateBo(user);
		
		
//		// if just update full name, not send email notification.
//		if (user.getUserName().equalsIgnoreCase(vhmOperation.getUserName())
//				&& !user.getUserFullName().equalsIgnoreCase(vhmOperation.getUserFullName())) {
//			user.setUserFullName(vhmOperation.getUserFullName());
//			QueryUtil.updateBo(user);
//			return;
//		}
//
//		// if update user name, we need email notification for this change.
//		if (!user.getUserName().equalsIgnoreCase(vhmOperation.getUserName())) {
//			user.setUserFullName(vhmOperation.getUserFullName());
//			user.setUserName(vhmOperation.getUserName());
//			user.setEmailAddress(vhmOperation.getEmailAddr());
//			QueryUtil.updateBo(user);
//
//			String subject = MgrUtil.getUserMessage("email.account.updateDefaultUser.title");
//			String mailContent = getHtmlContent4UpdateDefaultUser(vhmOperation);
//
//			sendEmail(mailContent, subject, vhmOperation.getEmailAddr());
//		}
	}

//	private String getHtmlContent4UpdateDefaultUser(VhmOperation vhmOperation) {
//		StringBuffer content = new StringBuffer();
//		content.append("<html><body><table border=\"0\">");
//		content.append("<tr><td>");
//		content.append(MgrUtil.getUserMessage("email.account.updateDefaultUser.para1"));
//		content.append("</td></tr><br>");
//		content.append("<tr><td><table border=\"0\">");
//		content.append("<tr><td width=\"150px\">");
//		content.append("Domain Name:");
//		content.append("</td><td>");
//		content.append(vhmOperation.getVhmName());
//		content.append("</td></tr><tr><td width=\"150px\">");
//		content.append("Admin Name:");
//		content.append("</td><td>");
//		content.append(vhmOperation.getUserName());
//		content.append("</td></tr><tr><td width=\"150px\">");
//		content.append("Email Address:");
//		content.append("</td><td>");
//		content.append(vhmOperation.getEmailAddr());
//		content.append("</td></tr><tr><td width=\"150px\">");
//		content.append("Full Name:");
//		content.append("</td><td>");
//		content.append(vhmOperation.getUserFullName());
//		content.append("</td></tr></table></td></tr><br><br>");
//
//		/*
//		 * copyright
//		 */
//		content.append("<tr><td>");
//		content.append("Copyright ");
//
//		/*
//		 * year
//		 */
//		content.append(Calendar.getInstance().get(Calendar.YEAR));
//		content.append(" Aerohive Networks Inc");
//		content.append("</td></tr>");
//
//		/*
//		 * logo
//		 */
//		content.append("<tr><td>");
//		content.append("<img src=\"cid:company_logo.png\" />");
//		content.append("</td></tr>");
//
//		content.append("</table></body><html>");
//
//		return content.toString();
//	}

//	private String getHtmlContent4UpdateDefaultUser(VhmInfo vhmInfo) {
//		StringBuffer content = new StringBuffer();
//		content.append("<html><body><table border=\"0\">");
//		content.append("<tr><td>");
//		content.append(MgrUtil.getUserMessage("email.account.updateDefaultUser.para1"));
//		content.append("</td></tr><br>");
//		content.append("<tr><td><table border=\"0\">");
//		content.append("<tr><td width=\"150px\">");
//		content.append("Domain Name:");
//		content.append("</td><td>");
//		content.append(vhmInfo.getVhmName());
//		content.append("</td></tr><tr><td width=\"150px\">");
//		content.append("Admin Name:");
//		content.append("</td><td>");
//		content.append(vhmInfo.getVhmAdminName());
//		content.append("</td></tr><tr><td width=\"150px\">");
//		content.append("Email Address:");
//		content.append("</td><td>");
//		content.append(vhmInfo.getVhmAdminEmailAddress());
//		content.append("</td></tr><tr><td width=\"150px\">");
//		content.append("Full Name:");
//		content.append("</td><td>");
//		content.append(vhmInfo.getVhmAdminFullName());
//		content.append("</td></tr></table></td></tr><br><br>");
//
//		/*
//		 * copyright
//		 */
//		content.append("<tr><td>");
//		content.append("Copyright ");
//
//		/*
//		 * year
//		 */
//		content.append(Calendar.getInstance().get(Calendar.YEAR));
//		content.append(" Aerohive Networks Inc");
//		content.append("</td></tr>");
//
//		/*
//		 * logo
//		 */
//		content.append("<tr><td>");
//		content.append("<img src=\"cid:company_logo.png\" />");
//		content.append("</td></tr>");
//
//		content.append("</table></body><html>");
//
//		return content.toString();
//	}

//	/**
//	 * report new url to default user
//	 * 
//	 * @param
//	 * 
//	 * @return
//	 */
//	public void notifyNewURL2DefaultUser(HmUser defaultUser, String loginURL, HmDomain domain)
//			throws Exception {
//		String toEmail = defaultUser.getEmailAddress();
//		if (toEmail == null || toEmail.trim().isEmpty()) {
//			throw new Exception("Unable to send notify email, user '" + defaultUser.getUserName()
//					+ "' email address is empty.");
//		}
//
//		String subject = MgrUtil.getUserMessage("email.account.newUrl.title");
//		String mailContent = getHtmlContent4NewUrl(domain.getDomainName(), defaultUser
//				.getUserName(), loginURL);
//
//		sendEmail(mailContent, subject, toEmail);
//	}

//	private String getHtmlContent4NewUrl(String domainName, String userName, String loginUrl) {
//		StringBuffer content = new StringBuffer();
//		content.append("<html><body><table border=\"0\">");
//		content.append("<tr><td>");
//		content.append(MgrUtil.getUserMessage("email.account.newUrl.para1"));
//		content.append("</td></tr><br>");
//		content.append("<tr><td><table border=\"0\">");
//		content.append("<tr><td width=\"150px\">");
//		content.append("Domain Name:");
//		content.append("</td><td>");
//		content.append(domainName);
//		content.append("</td></tr><tr><td width=\"150px\">");
//		content.append("Admin Name:");
//		content.append("</td><td>");
//		content.append(userName);
//		content.append("</td></tr><tr><td width=\"150px\">");
//		content.append("Login URL:");
//		content.append("</td><td>");
//		content.append(loginUrl);
//		content.append("</td></tr></table></td></tr><br><br>");
//
//		/*
//		 * copyright
//		 */
//		content.append("<tr><td>");
//		content.append("Copyright ");
//
//		/*
//		 * year
//		 */
//		content.append(Calendar.getInstance().get(Calendar.YEAR));
//		content.append(" Aerohive Networks Inc");
//		content.append("</td></tr>");
//
//		/*
//		 * logo
//		 */
//		content.append("<tr><td>");
//		content.append("<img src=\"cid:company_logo.png\" />");
//		content.append("</td></tr>");
//
//		content.append("</table></body><html>");
//
//		return content.toString();
//	}

	/**
	 * notify customer about new account created
	 * 
	 * @param vhmOperation -
	 * @param domain -
	 * @param defaultUser -
	 * @throws Exception -
	 */
	public void notifyEmail2DefaultUser(VhmOperation vhmOperation, HmDomain domain,
			HmUser defaultUser) throws Exception {
		// af first, reset password of default user
		String password_clear = NmsUtil.genRandomString(8);
		defaultUser.setPassword(MgrUtil.digest(password_clear));
		QueryUtil.updateBo(defaultUser);

		// keep this send email code because there are no clear password in vhm operation object.
		String toEmail = defaultUser.getEmailAddress();
		if (toEmail == null || toEmail.trim().isEmpty()) {
			throw new Exception("Unable to send notify email, user '" + defaultUser.getUserName()
					+ "' email address is empty.");
		}

		String subject = MgrUtil.getUserMessage("email.account.created.title");
		String mailContent = getHtmlContent4createAccount(vhmOperation, password_clear);

		sendEmail(mailContent, subject, toEmail);
	}

}