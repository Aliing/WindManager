/* 
 * $RCSfile: CwpPageMultiLanguageRes.java,v $ 
 * $Revision: 1.3.16.1 $ 
 * $Date: 2013/07/26 03:41:44 $ 
 * 
 * Copyright (C) 2012 Aerohive, Inc. All rights reserved. 
 * 
 * This software is the proprietary information of Aerohive, Inc. 
 * Use is subject to license terms. 
 */  
package com.ah.bo.wlan;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/** 
 * <p>Title: CwpPageMultiLanguageRes</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2012</p>  
 * @author xxu
 * @mail xxu@aerohive.com 
 * @version 1.0 
 */

@Embeddable
public class CwpPageMultiLanguageRes implements Serializable {
	
	public static final int[] languages		= {1,2,3,4,5,6,7,8,9};
	
	private static final long	serialVersionUID	= 1L;
	
	@Column(length = 256)
	private String successNotice;
	
	@Column(length = 256)
	private String successLibrarySIPStatus;
	
	@Column(length = 256)
	private String successLibrarySIPFines;
	
	@Column(length = 256)
	private String failureLibrarySIPFines;
	
	
	@Column(length = 20000)
	private String userPolicy;
	
	private int resLanguage;

	public String getSuccessNotice() {
		return successNotice;
	}

	public void setSuccessNotice(String successNotice) {
		this.successNotice = successNotice;
	}

	public String getSuccessLibrarySIPStatus() {
		return successLibrarySIPStatus;
	}

	public void setSuccessLibrarySIPStatus(String successLibrarySIPStatus) {
		this.successLibrarySIPStatus = successLibrarySIPStatus;
	}

	public String getSuccessLibrarySIPFines() {
		return successLibrarySIPFines;
	}

	public void setSuccessLibrarySIPFines(String successLibrarySIPFines) {
		this.successLibrarySIPFines = successLibrarySIPFines;
	}

	public String getFailureLibrarySIPFines() {
		return failureLibrarySIPFines;
	}

	public void setFailureLibrarySIPFines(String failureLibrarySIPFines) {
		this.failureLibrarySIPFines = failureLibrarySIPFines;
	}

	public String getUserPolicy() {
		return userPolicy;
	}

	public void setUserPolicy(String userPolicy) {
		this.userPolicy = userPolicy;
	}

	public int getResLanguage() {
		return resLanguage;
	}

	public void setResLanguage(int resLanguage) {
		this.resLanguage = resLanguage;
	}
	
	@Transient
	private String restoreId;

	public String getRestoreId()
	{
		return restoreId;
	}
	
	public void setRestoreId(String restoreId)
	{
		this.restoreId = restoreId;
	}

}
