package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.event.BeCapwapPayloadResultEvent;
import com.ah.util.coder.AhDecoder;

/**
 *
 *@filename		BePortalHMPayloadResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-1-26 10:22:52
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 *
 */
@SuppressWarnings("serial")
public class BePortalHMPayloadResultEvent extends BeCapwapPayloadResultEvent {

	private boolean				isSuccess							= true;

	public static final byte	SUCCESS								= 1;

	public static final byte	FAILURE								= 0;

	private String				errorMessage;

	public static final byte	RESULTTYPE_BASE						= 0;

	public static final byte	RESULTTYPE_QUERYHHM					= 1;

	public static final byte	RESULTTYPE_VHMUPGRADE				= 2;

	public static final byte	RESULTTYPE_QUERY_VHMREVERT_DESTHMOL	= 3;

	public static final byte	RESULTTYPE_QUERY_VHM_MOVING_STATUS	= 4;

	public static final byte	RESULTTYPE_VHM_CREATE				= 5;

	public static final byte	RESULTTYPE_VHM_MODIFY				= 6;
	
	public static final byte    RESULTTYPE_HM_SEARCH_AP             = 7;
	
	public static final byte    RESULTTYPE_HM_DELETE_AP             = 8;
	
	public static final byte    RESULTTYPE_SS_SEARCH_AP             = 9;
	
	public static final byte    RESULTTYPE_SS_DELETE_AP             = 10;
	
	public static final byte    RESULTTYPE_LS_SEARCH_AP             = 11;
	
	public static final byte    RESULTTYPE_LS_DELETE_AP             = 12;

	public static final byte	RESULTTYPE_QUERY_VHMUSERS			= 101;

	public static final byte	RESULTTYPE_QUERY_HASTATUS			= 102;

	public static final byte	RESULTTYPE_QUERY_HADBSTATUS			= 103;

	public static final byte	RESULTTYPE_MAINTENANCE				= 104;

	public static final byte	RESULTTYPE_BREAKUP					= 105;
	
	public static final byte	RESULTTYPE_VHM_USER_GROUPS			= 21;

	public BePortalHMPayloadResultEvent() {
		super();
		payloadResultType = RESULTTYPE_BASE;
	}

	/**
	 * build event data to packet message
	 * 
	 * @return byte[]
	 * @throws BeCommunicationEncodeException
	 *             -
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {

		if (apMac == null) {
			throw new BeCommunicationEncodeException("apMac is a necessary field!");
		}

		try {
			msgType = BeCommunicationConstant.MESSAGETYPE_APWTPEVENT;

			byte[] resultData = buildResultData();

			ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(1 + apMac.length());
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());

			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPPAYLOADRESULT);
			buf.putInt(resultData.length);
			buf.put(resultData);

			buf.flip();
			byte[] array = new byte[buf.limit()];
			buf.get(array);
			setPacket(array);

			return array;
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BePortalHMPayloadResultEvent.buildPacket() catch exception", e);
		}
	}

	private byte[] buildResultData() throws Exception {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		buf.putShort(queryType);
		buf.putInt(sequenceNum);
		buf.put(BeCommunicationConstant.NOTCOMPRESS);
		buf.put(payloadResultType);

		byte[] result = buildOperationResult();
		buf.putInt(result.length);
		buf.putInt(result.length);
		buf.put(result);

		buf.flip();
		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	protected byte[] buildOperationResult() throws Exception {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		buf.put(isSuccess ? SUCCESS : FAILURE);
		if (!isSuccess && errorMessage != null) {
			buf.putShort((short) errorMessage.length());
			buf.put(errorMessage.getBytes());
		}

		buf.flip();
		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data
	 *            -
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);

			ByteBuffer buf = ByteBuffer.wrap(getResultData());

			isSuccess = (buf.get() == 1);
			if (!isSuccess) {
				short len = buf.getShort();
				errorMessage = AhDecoder.bytes2String(buf, len);
			}

			int remaining = buf.remaining();
			resultData = new byte[remaining];
			buf.get(resultData, 0, remaining);

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BePortalHMPayloadResultEvent.parsePacket() catch exception", e);
		}
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

}
