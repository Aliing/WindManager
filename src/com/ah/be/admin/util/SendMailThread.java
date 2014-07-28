package com.ah.be.admin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.ah.be.app.DebugUtil;
import com.ah.be.common.SendMailUtil;
import com.ah.be.debug.DebugConstant;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;

/**
 * 
 *@filename		SendMailThread.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-11-17 05:13:16
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 * 
 */
public class SendMailThread implements Runnable {

	private Thread								emailThread;

	private final BlockingQueue<EmailElement>	mailQueue;

	private boolean								isContinue			= true;

	/**
	 * cache for email settings <br>
	 * key is vhm name, value is email setting bo
	 */
	private final Map<String, MailNotification>	mailSettings;

	private ScheduledExecutorService			scheduler4FailureEmail;

	private final List<EmailElement>			failureEmailList;

	private static final int					REFRESH_INTERVAL	= 5;

	/**
	 * Construct method
	 */
	public SendMailThread() {
		mailSettings = new HashMap<String, MailNotification>();
		failureEmailList = new ArrayList<EmailElement>();
		mailQueue = new LinkedBlockingQueue<EmailElement>(1000);
		List<MailNotification> list = QueryUtil.executeQuery(MailNotification.class, null, null);
		if (list != null && list.size() > 0) {
			for (Object object : list) {
				MailNotification bo = (MailNotification) object;
				mailSettings.put(bo.getOwner().getDomainName(), bo);
			}
		}
	}

	public void startTask() {
		emailThread = new Thread(this);
		emailThread.setName("Send Email Thread");
		emailThread.start();

		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_ADMIN,
				"<BE Thread> SendMailProxy - thread for send email is running...");
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(getClass().getSimpleName());

		while (isContinue) {
			// take() method blocks
			EmailElement email = getEmail();
			if (null == email)
				continue;

			try {
				MailNotification notification;
				String tempDomainName=null;
				if (email.getDomainName() != null && email.getDomainName().trim().length() > 0) {
					notification = mailSettings.get(email.getDomainName());
					tempDomainName = email.getDomainName();
				} else {
					notification = mailSettings.get(HmDomain.HOME_DOMAIN);
					tempDomainName=HmDomain.HOME_DOMAIN;
				}
				
				// fix CFD 409. when restore a VHM to server, the mail cannot work.
				if (notification == null && tempDomainName!=null) {
					MailNotification destBo = QueryUtil.findBoByAttribute(MailNotification.class, "owner.domainName",
							tempDomainName);
					if (destBo!=null) {
						updateMailNotification(destBo);
						notification= destBo;
					}
				}

				if (notification == null) {
					DebugUtil
							.adminDebugError("SendEmailThread.run(): Unable to send email, there are no mail notify setting bo for domain "
									+ email.getDomainName());
					continue;
				}
				
				if (!notification.getSendMailFlag() && !email.isMustBeSent()) {
					DebugUtil
							.adminDebugError("SendEmailThread.run(): Unable to send email, email notification has been disabled for domain "
									+ email.getDomainName());
					continue;
				}
				
				SendMailUtil mailUtil = new SendMailUtil(notification);
				mailUtil.setSubject(email.getSubject());
				mailUtil.setText(email.getMailContent());
				mailUtil.setMailContentType(email.getContentType());
				
				if (email.getToEmail() == null || email.getToEmail().length()  == 0) {
					continue;
				}
				mailUtil.setMailTo(email.getToEmail());
				mailUtil.setCCEmail(email.getCcEmail());
				mailUtil.setBCCEmail(email.getBccEmail());
				
				if (email.getShowFileList() != null && email.getShowFileList().size() > 0) {
					mailUtil.setShowFileList(email.getShowFileList());
				}
				if (email.getDetachedFileList() != null && email.getDetachedFileList().size() > 0) {
					mailUtil.setDetachedFileList(email.getDetachedFileList());
				}

				mailUtil.startSend();

			} catch (Exception e) {
				DebugUtil.adminDebugError("SendEmailThread.run() catch exception", e);

				// if must be sent, put it into scheduler.
				if (email.isMustBeSent()) {
					putMailIntoScheduler(email);
				}
			}
		}
	}

	/**
	 * put the email into cache, be sent again after an interval.
	 * 
	 * @param email -
	 */
	public void putMailIntoScheduler(EmailElement email) {
		synchronized (failureEmailList) {
			failureEmailList.add(email);
		}

		DebugUtil
				.adminDebugInfo("SendMailThread.putMailIntoScheduler(): put send mail task into schedule. Mail Subject ("
						+ email.getSubject() + "); Mail content (" + email.getMailContent() + ").");

		if (scheduler4FailureEmail == null || scheduler4FailureEmail.isShutdown()) {
			scheduler4FailureEmail = Executors.newSingleThreadScheduledExecutor();
			scheduler4FailureEmail.scheduleWithFixedDelay(new RefreshFailureEmailCache(),
					REFRESH_INTERVAL, REFRESH_INTERVAL, TimeUnit.MINUTES);

			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_ADMIN,
					"<BE Thread> SendMailProxy - thread for refresh email cache is running...");
		}
	}

	/**
	 * get element from queue
	 * 
	 * @return EmailElement or null
	 */
	private EmailElement getEmail() {
		try {
			return mailQueue.take();
		} catch (Exception e) {
			DebugUtil.adminDebugError(
					"SendMailProxy.getEvent(): Exception while get element from queue", e);
			return null;
		}
	}

	/**
	 * add element to queue
	 * 
	 * @param email -
	 */
	public void sendEmail(EmailElement email) {
		try {
			mailQueue.add(email);
		} catch (Exception e) {
			DebugUtil.adminDebugError(
					"SendMailProxy.sendEmail(): Exception while add element to queue", e);
		}
	}
	
	/**
	 * get cached mail setting bo
	 *
	 * @param domainName -
	 * @return -
	 */
	public MailNotification getCacheMailNotification(String domainName)
	{
		return mailSettings.get(domainName);
	}

	/**
	 * Update email settings
	 * 
	 * @param mailNotification -
	 */
	public void updateMailNotification(MailNotification mailNotification) {
		mailSettings.put(mailNotification.getOwner().getDomainName(), mailNotification);
	}
	
	public void removeMailNotification(String domainName) {
		if (!StringUtils.isEmpty(domainName)) {
			mailSettings.remove(domainName);
		}
	}

	public boolean shutdown() {
		isContinue = false;

		EmailElement element = new EmailElement();
		mailQueue.add(element);

		if (scheduler4FailureEmail != null && !scheduler4FailureEmail.isShutdown()) {
			scheduler4FailureEmail.shutdown();
		}

		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_ADMIN,
				"<BE Thread> SendMailProxy - thread for send email is shutdown");

		return true;
	}

	public MailNotification getMailNotification(String domainName) {
		return mailSettings.get(domainName);
	}

	class RefreshFailureEmailCache implements Runnable {
		@Override
		public void run() {
			MgrUtil.setTimerName(getClass().getSimpleName());

			synchronized (failureEmailList) {
				if (failureEmailList.isEmpty()) {
					DebugUtil
							.adminDebugInfo("RefreshMailGenerator.run(): no mail waiting to be sent.");
					return;
				}

				for (EmailElement email : failureEmailList) {
					sendEmail(email);
				}
				
				failureEmailList.clear();
			}
		}
	}

}
