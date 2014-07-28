package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeRebootFailEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-1-2 03:13:15
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
public class BeRebootFailEvent extends BeAPWTPEvent
{
	private static final long	serialVersionUID	= 1L;
	
	private String description;
	
	public BeRebootFailEvent()
	{
		super();
		msgType = BeCommunicationConstant.MESSAGEELEMENTTYPE_REBOOTFAILEVENT;
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
			byte[] buffer = getWtpMsgData();
			description = AhDecoder.bytes2String(ByteBuffer.wrap(buffer), buffer.length);
		}
		catch (Exception e)
		{
			throw new BeCommunicationDecodeException(
				"BeRebootFailEvent.parsePacket catch exception", e);
		}
	}
	
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
}
