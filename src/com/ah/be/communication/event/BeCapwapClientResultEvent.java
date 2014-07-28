package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;

/**
 * 
 *@filename		BeCapwapClientResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-12-5 03:49:55
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeCapwapClientResultEvent extends BeAPWTPEvent {

	/**
	 * definination see as BeCommunicationConstant
	 */
	protected short				resultType;

	protected byte[]			resultData;

	public BeCapwapClientResultEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	@Override
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);

			ByteBuffer buf = ByteBuffer.wrap(getWtpMsgData());

			resultType = buf.getShort();
			// if it is report auto, fill 0
			sequenceNum = buf.getInt();
			int dataLen = buf.getInt();
			resultData = new byte[dataLen];
			buf.get(resultData, 0, dataLen);

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeCapwapCliResultEvent.parsePacket() catch exception", e);
		}
	}

	public short getResultType() {
		return resultType;
	}

	public void setResultType(short resultType) {
		this.resultType = resultType;
	}
}
