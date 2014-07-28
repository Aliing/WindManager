/**
 *@filename		AhCapwapDecodeException.java
 *@version
 *@author		Francis
 *@createtime	2007-8-4 01:27:25 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
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
public class AhCapwapDecodeException extends AhCapwapException {

	private static final long serialVersionUID = 1L;

	public AhCapwapDecodeException() {
		super();
	}

	public AhCapwapDecodeException(String errorMsg) {
		super(errorMsg);
	}

	public AhCapwapDecodeException(String errorMsg, Throwable e) {
		super(errorMsg, e);
	}

}