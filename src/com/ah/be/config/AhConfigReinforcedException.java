/**
 *@filename		AhConfigReinforcedException.java
 *@version
 *@author		Francis
 *@createtime	Apr 9, 2008 6:02:55 PM
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
public class AhConfigReinforcedException extends AhConfigException {

	private static final long serialVersionUID = 1L;

	public AhConfigReinforcedException() {
		super();
	}

	public AhConfigReinforcedException(String message) {
		super(message);
	}

	public AhConfigReinforcedException(Throwable cause) {
		super(cause);
	}

	public AhConfigReinforcedException(String message, Throwable cause) {
		super(message, cause);
	}

}