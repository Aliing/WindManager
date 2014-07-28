package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeAPDTLSAuthorizeEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-12-17 01:39:32
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BeAPDTLSAuthorizeEvent extends BeCommunicationEvent
{

	private static final long	serialVersionUID	= 1L;

	/**
	 * authState const define
	 */
	public static final byte	AUTHSTATE_FAIL		= 0;
	public static final byte	AUTHSTATE_SUCCESS	= 1;

	private byte				authState;

	private String				apSerialNum;

	/**
	 * Construct method
	 */
	public BeAPDTLSAuthorizeEvent()
	{
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_APDTLSAUTHORIZEEVENT;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	protected void parsePacket(byte[] data)
		throws BeCommunicationDecodeException
	{
		try
		{
			ByteBuffer buf = ByteBuffer.wrap(data);

			while (buf.hasRemaining())
			{
				short msgType = buf.getShort();
				int msgLen = buf.getInt();

				// AP dtls authorize event's data field
				// 1. AP Identifier
				// 2. AP dtls authorize descriptor
				if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER)
				{
					byte macLen = buf.get();
					apMac = AhDecoder.bytes2String(buf, AhDecoder
						.byte2int(macLen)).toUpperCase();
				}
				else
					if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_APDTLSAUTHORIZEDESCRIPTOR)
					{
						authState = buf.get();
						byte apSerialNumLen = buf.get();
						apSerialNum = AhDecoder.bytes2String(buf, AhDecoder
							.byte2int(apSerialNumLen));

						// check length valid
						if (msgLen != 2 + apSerialNumLen)
						{
							throw new BeCommunicationDecodeException(
								"Invalid messge length in BeAPDTLSAuthorizeEvent");
						}
					}
					else
					{
						throw new BeCommunicationDecodeException(
							"Invalid messge element type in BeAPDTLSAuthorizeEvent, type value = "
								+ msgType);
						// DebugUtil
						// .commonDebugWarn("Invalid messge type in
						// BeAPDTLSAuthorizeEvent, type value = "
						// + msgType);
						//
						// return;
					}
			}
		}
		catch (Exception e)
		{
			throw new BeCommunicationDecodeException(
				"BeAPDTLSAuthorizeEvent.parsePacket() catch exception", e);
		}
	}

	public byte getAuthState()
	{
		return authState;
	}

	public void setAuthState(byte authState)
	{
		this.authState = authState;
	}

	public String getApSerialNum()
	{
		return apSerialNum;
	}

	public void setApSerialNum(String macAddr)
	{
		this.apSerialNum = macAddr;
	}

	public String getDescription()
	{
		if (authState == AUTHSTATE_FAIL)
			return "dtls handshake fail";
		else
			return "dtls handshake success";
	}

}