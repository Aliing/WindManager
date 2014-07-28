package com.ah.be.communication.event;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;

/**
 * 
 *@filename		BeCapwapConnectEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-12-5 11:20:55
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
public class BeCapwapConnectEvent extends BeCommunicationEvent
{

	private static final long	serialVersionUID	= 1L;

	public static final boolean	CONNECTSTATE_CONNECT	= true;

	public static final boolean	CONNECTSTATE_DISCONNECT	= false;
	
	private boolean				connectState;
	

	/**
	 * Construct method
	 */
	public BeCapwapConnectEvent()
	{
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_CAPWAPCONNECT;
	}


	public boolean isConnectState()
	{
		return connectState;
	}


	public void setConnectState(boolean connectState)
	{
		this.connectState = connectState;
	}

}