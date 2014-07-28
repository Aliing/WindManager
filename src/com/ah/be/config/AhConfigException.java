/**
 *@filename		AhConfigException.java
 *@version
 *@author		Francis
 *@createtime	Nov 21, 2007 5:12:15 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.config;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhConfigException extends Exception {

	private static final long serialVersionUID = 1L;

	public AhConfigException() {
		super();
	}

	public AhConfigException(String message) {
		super(message);
	}

	public AhConfigException(Throwable cause) {
		super(cause);
	}

	public AhConfigException(String message, Throwable cause) {
		super(message, cause);
	}

}