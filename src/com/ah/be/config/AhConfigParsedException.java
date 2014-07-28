/**
 *@filename		AhConfigParsedException.java
 *@version
 *@author		Francis
 *@createtime	Jan 5, 2008 12:52:06 PM
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
public class AhConfigParsedException extends AhConfigException {

	private static final long serialVersionUID = 1L;

	public AhConfigParsedException() {
		super();
	}

	public AhConfigParsedException(String message) {
		super(message);
	}

	public AhConfigParsedException(Throwable cause) {
		super(cause);
	}

	public AhConfigParsedException(String message, Throwable cause) {
		super(message, cause);
	}

}