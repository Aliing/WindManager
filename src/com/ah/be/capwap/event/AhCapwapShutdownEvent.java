/**
 *@filename		AhCapwapShutdownEvent.java
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
package com.ah.be.capwap.event;

// aerohive import
import com.ah.be.capwap.AhCapwapDecodeException;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhCapwapShutdownEvent extends AhCapwapEvent {

	private static final long	serialVersionUID	= 1L;
	
	public AhCapwapShutdownEvent() {
		super();
	}

	@Override
	public void parsePacket() throws AhCapwapDecodeException {

	}

}