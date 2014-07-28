/**
 *@filename		AhConfigComparedException.java
 *@version
 *@author		Francis
 *@createtime	Nov 7, 2007 8:34:56 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.config.xml.compare;

import com.ah.be.config.AhConfigException;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhConfigComparedException extends AhConfigException {

	private static final long serialVersionUID = 1L;

	public AhConfigComparedException() {
		super();
	}

	public AhConfigComparedException(String message) {
		super(message);
	}

	public AhConfigComparedException(Throwable cause) {
		super(cause);
	}

	public AhConfigComparedException(String message, Throwable cause) {
		super(message, cause);
	}

}