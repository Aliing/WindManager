package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeAAATestResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-3-29 03:23:07
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeAAATestResultEvent extends BeCapwapClientResultEvent {

	public static final byte	RESULTCODE_SUCCESS	= 0;
	public static final byte	RESULTCODE_FAILURE	= 1;

	private byte				testType;

	private byte				resultCode;

	private String				message;
	
	private int					errorCode;
	
	public BeAAATestResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_AAATEST;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);

			testType = buf.get();
			resultCode = buf.get();
			message = AhDecoder.getString(buf);
			if (buf.hasRemaining())errorCode = buf.getInt();

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeAAATestResultEvent.parsePacket() catch exception", e);
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public byte getResultCode() {
		return resultCode;
	}

	public void setResultCode(byte resultCode) {
		this.resultCode = resultCode;
	}

	public byte getTestType() {
		return testType;
	}

	public void setTestType(byte testType) {
		this.testType = testType;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
