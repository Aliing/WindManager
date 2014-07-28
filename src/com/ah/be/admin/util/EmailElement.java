package com.ah.be.admin.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *@filename		EmailElement.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-11-17 05:13:07
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
public class EmailElement {

	private String			mailContent;

	private String			subject;

	private String			domainName;

	private String			toEmail;

	private String			ccEmail;

	private String			bccEmail;

	private List<String>	showFileList	= new ArrayList<String>();
	
	List<String>			detachedFileList	= new ArrayList<String>();

	/**
	 * the email must be sent if this flag is true
	 */
	private boolean			mustBeSent		= false;

	private String			contentType		= "text/plain";

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public EmailElement() {
	}

	public String getMailContent() {
		return mailContent;
	}

	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public void addShowfile(String filePath) {
		showFileList.add(filePath);
	}

	public boolean isMustBeSent() {
		return mustBeSent;
	}

	public void setMustBeSent(boolean mustBeSent) {
		this.mustBeSent = mustBeSent;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String mailContentType) {
		this.contentType = mailContentType;
	}

	public List<String> getShowFileList() {
		return showFileList;
	}

	public void setShowFileList(List<String> showFileList) {
		this.showFileList = showFileList;
	}

	public String getBccEmail() {
		return bccEmail;
	}

	public void setBccEmail(String bccEmail) {
		this.bccEmail = bccEmail;
	}

	public String getCcEmail() {
		return ccEmail;
	}

	public void setCcEmail(String ccEmail) {
		this.ccEmail = ccEmail;
	}
	
	public List<String> getDetachedFileList() {
		return detachedFileList;
	}

	public void setDetachedFileList(List<String> detachedFileList) {
		this.detachedFileList = detachedFileList;
	}
}
