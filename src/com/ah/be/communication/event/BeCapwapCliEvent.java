package com.ah.be.communication.event;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;

/**
 *
 *@filename		BeCapwapCliEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-23 10:52:23
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
@Deprecated
//move into BeCliEvent, let's not open detail to invoker.
public class BeCapwapCliEvent extends BeCommunicationEvent
{

	private static final long	serialVersionUID	= 1L;

	/**
	 * Construct method
	 * 
	 * @param clis -
	 * @param cliSerialNum -
	 */
	public BeCapwapCliEvent(String[] clis, int cliSerialNum)
	{
		this.clis = clis;
		this.cliSerialNum = cliSerialNum;
	}

	private int			cliSerialNum	= 0;	// cliSerialNum created by
												// communication module

	private String[]	clis;

	/**
	 * build event data to packet message
	 * 
	 * @return BeCommunicationMessageData
	 */
	public byte[] buildPacket()
		throws BeCommunicationEncodeException
	{
		try
		{
			if (clis == null || clis.length == 0)
			{
				throw new BeCommunicationEncodeException(
					"cli(s) is(are) required.");
			}

			if (cliSerialNum <= 0)
			{
				throw new BeCommunicationEncodeException(
					"CliSerialNum is required.");
			}

			String s = "";
			for (String cli : clis)
			{
				s += cli;
			}
			byte[] clisArray;
			try
			{
				clisArray = s.getBytes("iso-8859-1");
			}
			catch (UnsupportedEncodingException e)
			{
				clisArray = s.getBytes();
			}
			ByteBuffer buf = ByteBuffer.allocate(s.length() + 4);
			buf.putInt(cliSerialNum);
			buf.put(clisArray);
			setPacket(buf.array());
			return buf.array();
		}
		catch (Exception e)
		{
			throw new BeCommunicationEncodeException(
				"BeCapwapCliEvent.buildPacket() catch exception", e);
		}
	}

	public String[] getClis()
	{
		return clis;
	}

	public void setClis(String[] clis)
	{
		this.clis = clis;
	}

	public int getCliSerialNum()
	{
		return cliSerialNum;
	}

	public void setCliSerialNum(int cliSerialNum)
	{
		this.cliSerialNum = cliSerialNum;
	}

}