/**
 *@filename		AhDiscoveryException.java
 *@version
 *@author		Francis
 *@createtime	2007-8-5 12:48:51 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.db.discovery;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhDiscoveryException extends Exception {

	private static final long serialVersionUID = 1L;

	public AhDiscoveryException() {
		super();
	}

	public AhDiscoveryException(String message) {
		super(message);
	}

	public AhDiscoveryException(Throwable cause) {
		super(cause);
	}

	public AhDiscoveryException(String message, Throwable cause) {
		super(message, cause);
	}

}