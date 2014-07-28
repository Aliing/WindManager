package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhDecoder;

/**
 * IDP query message type
 *@filename		BeIDPQueryEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-10 02:35:18
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BeIDPQueryEvent extends BeCommunicationEvent
{

	private static final long	serialVersionUID	= 1L;

//	private int					idpSequenceNumber;

	public BeIDPQueryEvent()
	{
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_IDPQUERYREQ;
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

		if (sequenceNum <= 0)
		{
			throw new BeCommunicationEncodeException(
				"sequenceNum is a necessary field!");
		}

		try
		{
			/**
			 * AP identifier 's length = 6 + 1 + apSerialNum.length()<br>
			 * idp's length = 6 + 4
			 */
			int apIdentifierLen = 7 + apMac.length();
			int idpLen = 10;
			int bufLength = apIdentifierLen + idpLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_IDPQUERY);
			buf.putInt(4);
			buf.putInt(sequenceNum);
			setPacket(buf.array());
			return buf.array();
		}
		catch (Exception e)
		{
			throw new BeCommunicationEncodeException(
				"BeIDPQueryEvent.buildPacket() catch exception", e);
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
					apMac = AhDecoder.bytes2String(buf,
						AhDecoder.byte2int(macLen)).toUpperCase();
				}
				else
					if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_RESULTDESCRIPTOR)
					{
						// check length valid
						if (msgLen != 1)
						{
							throw new BeCommunicationDecodeException(
								"Invalid messge length in BeIDPQueryEvent");
						}

						result = buf.get();
					}
					else
					{
						throw new BeCommunicationDecodeException(
							"Invalid messge element type in BeIDPQueryEvent, type value = "
								+ msgType);
						// DebugUtil
						// .commonDebugWarn("Invalid messge type in
						// BeIDPQueryEvent, type value = "
						// + msgType);
						//
						// return;
					}
			}
		}
		catch (Exception e)
		{
			throw new BeCommunicationDecodeException(
				"BeIDPQueryEvent.parsePacket() catch exception", e);
		}
	}

//	public int getIdpSequenceNumber()
//	{
//		return idpSequenceNumber;
//	}
//
//	public void setIdpSequenceNumber(int idpSequenceNumber)
//	{
//		this.idpSequenceNumber = idpSequenceNumber;
//	}

}