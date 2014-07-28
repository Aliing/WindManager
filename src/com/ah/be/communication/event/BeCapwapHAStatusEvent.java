/**
 * @filename			BeCapwapHAStatusEvent.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.3
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;

/**
 * CAPWAP event for HA status
 */
public class BeCapwapHAStatusEvent extends BeCommunicationEvent {
	private static final long	serialVersionUID	= 1L;
	
	private int haStatus;

	public BeCapwapHAStatusEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_HASETREQ;
	}
	
	@Override
	public byte[] buildPacket() throws BeCommunicationEncodeException
	{
		try
		{
			/**
			 * capwap ha status' length = 
			 * 	2(element type) + 4(element length) + 1(haStatus)<br>
			 */
			int bufLength = 7;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_HASTATUS);
			buf.putInt(bufLength - 6);
			buf.put((byte)haStatus);
			setPacket(buf.array());
			return buf.array();
		}
		catch (Exception e)
		{
			throw new BeCommunicationEncodeException(
				"BeCapwapServerLicenseEvent.buildPacket() catch exception",
				e);
		}
	}
	
	@Override
	public synchronized void parsePacket() throws BeCommunicationDecodeException
	{
		super.parsePacket();
	}

	/**
	 * getter of haStatus
	 * @return the haStatus
	 */
	public int getHaStatus() {
		return haStatus;
	}

	/**
	 * setter of haStatus
	 * @param haStatus the haStatus to set
	 */
	public void setHaStatus(int haStatus) {
		this.haStatus = haStatus;
	}
	
}
