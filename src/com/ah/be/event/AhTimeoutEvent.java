/**
 *@filename		AhTimeoutEvent.java
 *@version
 *@author		Facncis
 *@createtime	Nov 13, 2007 6:14:04 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.event;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhTimeoutEvent extends BeBaseEvent {

	private static final long serialVersionUID = 1L;

	public AhTimeoutEvent() {
		super.setEventType(BeEventConst.AH_TIMEOUT_EVENT);
	}

}