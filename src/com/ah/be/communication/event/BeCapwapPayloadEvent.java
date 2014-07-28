package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeCapwapPayloadEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-11-18 02:07:50
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeCapwapPayloadEvent extends BeCommunicationEvent {

	public static final short	QUERYTYPE_PORTALANDHM	= 1;

	protected short				queryType				= QUERYTYPE_PORTALANDHM;

	public BeCapwapPayloadEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_CAPWAPPAYLOADREQ;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 * @throws BeCommunicationDecodeException -
	 */
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		ByteBuffer buf = ByteBuffer.wrap(data);
		while (buf.hasRemaining()) {
			short msgType = buf.getShort();
			buf.getInt();

			if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER) {
				byte macLen = buf.get();
				apMac = AhDecoder.bytes2String(buf, AhDecoder.byte2int(macLen)).toUpperCase();
			} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_RESULTDESCRIPTOR) {
				result = buf.get();
			} else {
				throw new BeCommunicationDecodeException(
						"Invalid messge element type in BeCapwapPayloadEvent, type value = "
								+ msgType);
			}
		}
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getShort(); // element type
		buf.getInt(); //element length
		byte len = buf.get(); // mac length
		apMac = AhDecoder.bytes2String(buf, len).toUpperCase();
		buf.getShort(); // element type
		buf.getInt(); //element length
		queryType = buf.getShort(); // query type
		
		byte[] remaining = new byte[buf.remaining()];
		buf.get(remaining, 0, buf.remaining());
		return remaining;
	}
	
	public short getQueryType() {
		return queryType;
	}

	public void setQueryType(short queryType) {
		this.queryType = queryType;
	}
}
