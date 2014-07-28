package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhDecoder;

/**
 * This event for request information from ap.<br>
 * Distinguish different query through query type, this way will simply later modifcation in capwap module<br>
 *@filename		BeCapwapClientInfoEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-12-5 11:05:10
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeCapwapClientInfoEvent extends BeCommunicationEvent {

	public static final short	TYPE_DELETECOOKIE				= 1;

	public static final short	TYPE_CPUMEMQUERY				= 2;

	public static final short	TYPE_TEACHERVIEW_STUDUENTINFO	= 3;

	public static final short	TYPE_TEACHERVIEW_CLASSINFO		= 4;

	protected short				queryType;

	protected byte[]			infoData;

	public BeCapwapClientInfoEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTINFOREQ;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data
	 *            -
	 * @throws BeCommunicationDecodeException
	 *             -
	 */
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			ByteBuffer buf = ByteBuffer.wrap(data);
			while (buf.hasRemaining()) {
				short msgType = buf.getShort();
				buf.getInt();

				if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER) {
					byte macLen = buf.get();
					apMac = AhDecoder.bytes2String(buf, AhDecoder.byte2int(macLen)).toUpperCase();
				} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_RESULTDESCRIPTOR) {
					result = buf.get();
				} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_INFORMATIONRESULT) {
					queryType = buf.getShort(); // result type
					int dataLen = buf.getInt();
					infoData = new byte[dataLen];
					buf.get(infoData, 0, dataLen);
				} else {
					throw new BeCommunicationDecodeException(
							"Invalid messge element type in BeCapwapClientInfoEvent, type value = "
									+ msgType);
				}
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeCapwapClientInfoEvent.parsePacket() catch exception", e);
		}
	}

	public short getQueryType() {
		return queryType;
	}

	public void setQueryType(short queryType) {
		this.queryType = queryType;
	}
}
