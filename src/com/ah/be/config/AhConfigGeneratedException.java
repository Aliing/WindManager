/**
 *@filename		AhConfigGeneratedException.java
 *@version
 *@author		Francis
 *@createtime	Jan 4, 2008 8:45:26 AM
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
public class AhConfigGeneratedException extends AhConfigException {

	private static final long serialVersionUID = 1L;

	public AhConfigGeneratedException() {
		super();
	}

	public AhConfigGeneratedException(String message) {
		super(message);
	}

	public AhConfigGeneratedException(Throwable cause) {
		super(cause);
	}

	public AhConfigGeneratedException(String message, Throwable cause) {
		super(message, cause);
	}

}