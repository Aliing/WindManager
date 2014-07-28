/**
 *@filename		AcmResultModel.java
 *@version
 *@author		Fiona
 *@createtime	Sep 17, 2013 3:33:37 PM
 *Copyright (c) 2006-2013 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.rest.ahmdm.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@XStreamAlias("content")
public class AcmResultModel {
	
	public static final int ACM_RESULT_STATUS_SUCCESS = 1;
	
	public static final int ACM_RESULT_STATUS_FAILED = 0;
	
	@XStreamAlias("StatusCode")
	private String statusCode;
	
	@XStreamAlias("Message")
	private String message;

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
