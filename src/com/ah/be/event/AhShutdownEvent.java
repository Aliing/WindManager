/**
 *@filename		AhShutdownEvent.java
 *@version
 *@author		Francis
 *@createtime	Dec 18, 2007 8:53:18 AM
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
public class AhShutdownEvent extends BeBaseEvent {

	private static final long serialVersionUID = 1L;

	public AhShutdownEvent() {
		super.setEventType(BeEventConst.AH_SHUTDOWN_EVENT);
	}

}