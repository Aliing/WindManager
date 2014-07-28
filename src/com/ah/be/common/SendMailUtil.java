package com.ah.be.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;

import com.ah.be.app.DebugUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.MailNotification;

public class SendMailUtil {

	private List<Address>	toEmailList			= new ArrayList<Address>();

	private String			fromEmail			= "";

	private String			smtpServer			= "";

	private String			subject				= "";

	private String			text				= "";

	List<String>			detachedFileList	= new ArrayList<String>();

	List<String>			showFileList		= new ArrayList<String>();

	private boolean			supportSSL			= false;

	private boolean			supportTLS			= false;

	private boolean			supportPwdAuth		= false;

	private String			emailUserName;

	private String			emailPassword;

	private String			mailContentType		= "text/plain";

	private int				port				= 25;

	private List<Address>	ccEmailList			= new ArrayList<Address>();

	private List<Address>	bccEmailList		= new ArrayList<Address>();

	/**
	 * 
	 * Construct method
	 * 
	 * @param
	 * 
	 * @throws
	 */
	public SendMailUtil() {

	}

	/**
	 * 
	 * Construct method
	 * 
	 * @param mailSetting:
	 *            email notify setting bo
	 * 
	 * @throws
	 */
	public SendMailUtil(MailNotification mailSetting) {
		try {
			smtpServer = mailSetting.getServerName();
			fromEmail = mailSetting.getMailFrom();
			supportPwdAuth = mailSetting.isSupportPwdAuth();
			emailUserName = mailSetting.getEmailUserName();
			emailPassword = mailSetting.getEmailPassword();
			supportSSL = mailSetting.isSupportSSL();
			supportTLS = mailSetting.isSupportTLS();
			port = mailSetting.getPort();
			if (!StringUtils.isBlank(mailSetting.getMailTo())) {
				String[] array_to = mailSetting.getMailTo().trim().split(";");
				for (String mailTo : array_to) {
					if (mailTo != null && !mailTo.trim().equals("")) {
						toEmailList.add(new InternetAddress(mailTo));
					}
				}
			}
		} catch (Exception e) {
			DebugUtil.commonDebugError("Create send mail instance catch exception.", e);
		}
	}

	public void attachfile(String filePath) {
		detachedFileList.add(filePath);
	}

	public void addShowfile(String filePath) {
		showFileList.add(filePath);
	}

	public void addMailToAddr(String mailAddress) {
		try {
			String[] array_to = mailAddress.trim().split(";");
			for (String mailTo : array_to) {
				if (mailTo != null && !mailTo.trim().equals("")) {
					toEmailList.add(new InternetAddress(mailTo.trim()));
				}
			}
		} catch (Exception e) {
			DebugUtil.commonDebugError(
					"Add mail to address for send mail instance catch exception.", e);
		}
	}

	public void setMailTo(String mailAddress) {
		try {
			toEmailList.clear();
			addMailToAddr(mailAddress);
		} catch (Exception e) {
			DebugUtil.commonDebugError("setMailTo catch exception.", e);
		}
	}
	
	public void addCCEmail(String mailAddress) {
		try {
			if (mailAddress == null) {
				return;
			}
			
			String[] array_to = mailAddress.trim().split(";");
			for (String mailTo : array_to) {
				if (mailTo != null && !mailTo.trim().equals("")) {
					ccEmailList.add(new InternetAddress(mailTo.trim()));
				}
			}
		} catch (Exception e) {
			DebugUtil.commonDebugError(
					"addCCEmail() catch exception.", e);
		}
	}
	
	public void setCCEmail(String mailAddress) {
		try {
			ccEmailList.clear();
			addCCEmail(mailAddress);
		} catch (Exception e) {
			DebugUtil.commonDebugError("setCCEmail catch exception.", e);
		}
	}
	
	public void addBCCEmail(String mailAddress) {
		try {
			if (mailAddress == null) {
				return;
			}
			
			String[] array_to = mailAddress.trim().split(";");
			for (String mailTo : array_to) {
				if (mailTo != null && !mailTo.trim().equals("")) {
					bccEmailList.add(new InternetAddress(mailTo.trim()));
				}
			}
		} catch (Exception e) {
			DebugUtil.commonDebugError(
					"addBCCEmail() catch exception.", e);
		}
	}
	
	public void setBCCEmail(String mailAddress) {
		try {
			bccEmailList.clear();
			addBCCEmail(mailAddress);
		} catch (Exception e) {
			DebugUtil.commonDebugError("setBCCEmail catch exception.", e);
		}
	}

	public void startSend() throws Exception {
		if (smtpServer == null || smtpServer.length() == 0) {
			throw new Exception("SMTP server configuration is necessary.");
		}

		if (fromEmail == null || fromEmail.length() == 0) {
			throw new Exception("From email configuration is necessary.");
		}

		if (toEmailList == null || toEmailList.size() == 0) {
			throw new Exception("To email configuration is necessary.");
		}

		// set api properties
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", smtpServer);
		props.setProperty("mail.smtp.timeout", "180000");

		props.remove("mail.smtp.socketFactory.class");
		props.remove("mail.smtp.socketFactory.fallback");
		props.setProperty("mail.smtp.ssl", "false");
		props.setProperty("mail.smtp.starttls.enable", "false");
		props.setProperty("mail.smtp.port", String.valueOf(port));
		props.setProperty("mail.smtp.socketFactory.port ", String.valueOf(port));
		
		props.setProperty("mail.smtp.ssl.trust", "*");

		if (supportSSL) {
			props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.smtp.socketFactory.fallback", "false");
			props.setProperty("mail.smtp.ssl", "true");
		} else if (supportTLS) {
			props.setProperty("mail.smtp.starttls.enable", "true");
		}

		Session session = null;
		if (supportPwdAuth) {
			props.setProperty("mail.smtp.auth", "true");
			session = Session.getInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(emailUserName, emailPassword);
				}
			});
		} else {
			props.setProperty("mail.smtp.auth", "false");
			session = Session.getDefaultInstance(props, null);
		}
		
		// start send
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(fromEmail));
		msg.setSubject(subject);

		msg.setRecipients(Message.RecipientType.TO, toEmailList.toArray(new Address[toEmailList
				.size()]));
		if (ccEmailList != null && ccEmailList.size() > 0) {
			msg.setRecipients(Message.RecipientType.CC, ccEmailList.toArray(new Address[ccEmailList
					.size()]));
		}
		if (bccEmailList != null && bccEmailList.size() > 0) {
			msg.setRecipients(Message.RecipientType.BCC, bccEmailList
					.toArray(new Address[bccEmailList.size()]));
		}

		Multipart mp = new MimeMultipart();
		MimeBodyPart mbp = new MimeBodyPart();
		// mbp.setContent(text, "text/html");
		mbp.setContent(text, mailContentType);
		mp.addBodyPart(mbp);

		if (detachedFileList != null && detachedFileList.size() > 0) {
			for (String filePath : detachedFileList) {
				FileDataSource fds = new FileDataSource(filePath);
				if (!fds.getFile().exists()) {
					continue;
				}
				mbp = new MimeBodyPart();
				mbp.setDataHandler(new DataHandler(fds));
				mbp.setFileName(fds.getName());
				mp.addBodyPart(mbp);
			}
		}
		if (showFileList != null && showFileList.size() > 0) {
			for (String filePath : showFileList) {
				FileDataSource fds = new FileDataSource(filePath);
				if (!fds.getFile().exists()) {
					continue;
				}
				mbp = new MimeBodyPart();
				mbp.setDataHandler(new DataHandler(fds));
				mbp.setFileName(fds.getName());
				mbp.setHeader("Content-ID", fds.getName());
				mp.addBodyPart(mbp);
			}
		}
		msg.setContent(mp);
		msg.setSentDate(new Date());

		Transport.send(msg);

		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_ADMIN, "SendMailUtil: send mail successfully. Subject: " + subject
				+ ", Content: " + text);
	}

	public String getEmailPassword() {
		return emailPassword;
	}

	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}

	public String getEmailUserName() {
		return emailUserName;
	}

	public void setEmailUserName(String emailUserName) {
		this.emailUserName = emailUserName;
	}

	public boolean isSupportPwdAuth() {
		return supportPwdAuth;
	}

	public void setSupportPwdAuth(boolean supportPwdAuth) {
		this.supportPwdAuth = supportPwdAuth;
	}

	public boolean isSupportSSL() {
		return supportSSL;
	}

	public void setSupportSSL(boolean supportSSL) {
		this.supportSSL = supportSSL;
	}

	public List<String> getDetachedFileList() {
		return detachedFileList;
	}

	public void setDetachedFileList(List<String> detachedFileList) {
		this.detachedFileList = detachedFileList;
	}

	public List<String> getShowFileList() {
		return showFileList;
	}

	public void setShowFileList(List<String> showFileList) {
		this.showFileList = showFileList;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Address> getToEmailList() {
		return toEmailList;
	}

	public void setToEmailList(List<Address> toEmailList) {
		this.toEmailList = toEmailList;
	}

	public String getMailContentType() {
		return mailContentType;
	}

	public void setMailContentType(String mailContentType) {
		this.mailContentType = mailContentType;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isSupportTLS() {
		return supportTLS;
	}

	public void setSupportTLS(boolean supportTLS) {
		this.supportTLS = supportTLS;
	}

	public List<Address> getBccEmailList() {
		return bccEmailList;
	}

	public void setBccEmailList(List<Address> bccEmailList) {
		this.bccEmailList = bccEmailList;
	}

	public List<Address> getCcEmailList() {
		return ccEmailList;
	}

	public void setCcEmailList(List<Address> ccEmailList) {
		this.ccEmailList = ccEmailList;
	}
	
	public static String addHeadAndFoot(String text) {
		// head
		text = "<html><body><table border=\"0\">" + text;
		
		text += "<br><br><br><br>";
		
		/*
		 * copyright
		 */
		text += "<tr><td>";
		text += "Copyright ";
		
		/*
		 * year
		 */
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		text += c.get(Calendar.YEAR);
		text += " Aerohive Networks, Inc.";
		text += "</td></tr>";
		
		/*
		 * logo
		 */
		text += "<tr><td align=\"right\">";
		text += "<img src=\"cid:company_logo.png\" />";
		text += "</td></tr>";
		
		text += "</table></body><html>";
		
		return text;
	}

}
