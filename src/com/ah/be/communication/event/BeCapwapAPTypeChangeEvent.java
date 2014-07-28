package com.ah.be.communication.event;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;

/**
 * AP TYPE CHANGE message element Type
 *@filename		BeCapwapAPTypeChangeEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-10 02:25:33
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BeCapwapAPTypeChangeEvent extends BeAPWTPEvent
{

	private static final long	serialVersionUID	= 1L;

	/**
	 * Ap Type (Defined by Aerohive, msg type : 5006)
	 * <p>
	 * +-+-+-+-+-+-+-+-+ | Portal | +-+-+-+-+-+-+-+-+
	 * <p>
	 * Portal: An 8-bit boolean stating whether the Ap is Portal or MP. A value
	 * of zero indicates MP and one indicates Portal.
	 * <p>
	 */

	private byte	apType;

	public BeCapwapAPTypeChangeEvent()
	{
		super();
		msgType = BeCommunicationConstant.MESSAGEELEMENTTYPE_APTYPECHANGE;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	@Override
	protected void parsePacket(byte[] data)
		throws BeCommunicationDecodeException
	{
		try
		{
			super.parsePacket(data);
			apType = getWtpMsgData()[0];
		}
		catch (Exception e)
		{
			throw new BeCommunicationDecodeException(
				"BeCapwapAPTypeChangeEvent.parsePacket catch exception", e);
		}
	}

	public byte getApType()
	{
		return apType;
	}

	public void setApType(byte apType)
	{
		this.apType = apType;
	}

}