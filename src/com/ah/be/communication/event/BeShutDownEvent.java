package com.ah.be.communication.event;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;

/**
 * shutdown message type
 *@filename		BeShutDownEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-10 02:39:38 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
public class BeShutDownEvent extends BeCommunicationEvent
{

	private static final long	serialVersionUID	= 1L;
	
	public BeShutDownEvent()
	{
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_SHUTDOWNREQ;
	}

}