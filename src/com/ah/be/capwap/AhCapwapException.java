/**
 *@filename		AhCapwapException.java
 *@version
 *@author		Francis
 *@createtime	2007-10-10 02:43:13 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Inc.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.capwap;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhCapwapException extends Exception {

	private static final long serialVersionUID = 1L;

	public AhCapwapException() {
		super();
	}

	public AhCapwapException(String errorMsg) {
		super(errorMsg);
	}

	public AhCapwapException(String errorMsg, Throwable e) {
		super(errorMsg, e);
	}

}