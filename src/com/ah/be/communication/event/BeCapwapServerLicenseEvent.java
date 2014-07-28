package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;

/**
 * 
 *@filename		BeCapwapServerLicenseEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-4-9 10:37:30
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
public class BeCapwapServerLicenseEvent extends BeCommunicationEvent
{
	private static final long	serialVersionUID	= 1L;
	
	/**
	 * 0~5000
	 */
	private int apNum;
	
	public BeCapwapServerLicenseEvent()
	{
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_CAPWAPSERVERLICENSEREQ;
	}
	
	@Override
	public byte[] buildPacket() throws BeCommunicationEncodeException
	{
		try
		{
			/**
			 * capwap server license descriptor's length = 6 + 4<br>
			 */
			int bufLength = 10;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_LICENSEDESCRIPTOR);
			buf.putInt(bufLength - 6);
			buf.putInt(apNum);
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

	public int getApNum()
	{
		return apNum;
	}

	public void setApNum(int apNum)
	{
		this.apNum = apNum;
	}
}
