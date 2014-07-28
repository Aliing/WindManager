package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhDecoder;

/**
 * Layer3 roaming config message type
 *@filename		BeL3RoamingConfigEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-10 02:36:59
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BeL3RoamingConfigEvent extends BeCommunicationEvent
{

	private static final long			serialVersionUID	= 1L;

	private byte[]	l3RoamingMsgData;

	public byte[] getL3RoamingMsgData()
	{
		return l3RoamingMsgData;
	}

	public void setL3RoamingMsgData(byte[] l3RoamingMsgData)
	{
		this.l3RoamingMsgData = l3RoamingMsgData;
	}

	public BeL3RoamingConfigEvent()
	{
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_L3ROAMINGCONFIGREQ;
	}

	/**
	 * build event data to packet message
	 * 
	 * @return BeCommunicationMessageData
	 * @throws BeCommunicationEncodeException -
	 */
	public byte[] buildPacket()
		throws BeCommunicationEncodeException
	{
		if (apMac == null)
		{
			throw new BeCommunicationEncodeException("ApMac is a necessary field!");
		}

		try
		{
			/**
			 * AP identifier 's length = 6 + 1 + apSerialNum.length()<br>
			 * l3 roaming 's length = 6+ ....<br>
			 */
			int apIdentifierLen = 7 + apMac.length();
			int l3RoamLen = 6 + l3RoamingMsgData.length;
			int bufLength = apIdentifierLen + l3RoamLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_LAYER3ROAMING);
			buf.putInt(l3RoamLen - 6);
			buf.put(l3RoamingMsgData);
			setPacket(buf.array());
			return buf.array();
		}
		catch (Exception e)
		{
			throw new BeCommunicationEncodeException(
				"BeL3RoamingConfigEvent.buildPacket() catch exception", e);
		}
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 * @throws BeCommunicationDecodeException -
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

				if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER)
				{
					byte macLen = buf.get();
					apMac = AhDecoder.bytes2String(buf, AhDecoder
						.byte2int(macLen)).toUpperCase();
				}
				// else
				// if (msgType ==
				// BeCommunicationConstant.MESSAGEELEMENTTYPE_LAYER3ROAMING)
				// {
				// byte[] tmpBytes = new byte[msgLen];
				// buf.get(tmpBytes, 0, msgLen);
				// l3RoamingMsgData = BeCommunicationMessageData
				// .wrap(tmpBytes);
				// }
				else
					if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_RESULTDESCRIPTOR)
					{
						// check length valid
						if (msgLen != 1)
						{
							throw new BeCommunicationDecodeException(
								"Invalid messge length in BeL3RoamingConfigEvent");
						}

						result = buf.get();
					}
					else
					{
						throw new BeCommunicationDecodeException(
							"Invalid messge element type in BeL3RoamingConfigEvent, type value = "
								+ msgType);
						// DebugUtil
						// .commonDebugWarn("Invalid messge type in
						// BeL3RoamingConfigEvent, type value = "
						// + msgType);

						// return;
					}
			}
		}
		catch (Exception e)
		{
			throw new BeCommunicationDecodeException(
				"BeL3RoamingConfigEvent.parsePacket() catch exception", e);
		}
	}

}