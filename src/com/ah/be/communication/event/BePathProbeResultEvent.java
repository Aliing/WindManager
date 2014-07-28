package com.ah.be.communication.event;

import com.ah.be.communication.BeCommunicationConstant;

/**
 * 
 *@filename		BePathProbeResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-3-30 11:16:56
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BePathProbeResultEvent extends BeCapwapClientResultEvent {

	public BePathProbeResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PATHPROBE;
	}
}
