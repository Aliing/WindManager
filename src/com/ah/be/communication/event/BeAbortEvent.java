package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhDecoder;

/**
 *
 *@filename		BeAbortEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-7-31 02:21:46
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 * 
 */
@SuppressWarnings("serial")
public class BeAbortEvent extends BeCommunicationEvent {

	public static final byte	RESULT_SUCCESS					= 0;
	public static final byte	RESULT_FAILED					= 1;
	public static final byte	RESULT_WRITE_FLASH				= 2;
	public static final byte	RESULT_NO_PROCESS				= 3;
	public static final byte	RESULT_TYPE_MISMATCH			= 4;

	public static final int		ABORTTYPE_SCP_IMAGEDOWNLOAD		= 1;
	public static final int		ABORTTYPE_TFTP_IMAGEDOWNLOAD	= 2;

	private int					abortType;

	private int					abortResult;

	public BeAbortEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_ABORTREQ;
	}

	/**
	 * build event data to packet message
	 * 
	 * @return -
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		try {
			if (apMac == null) {
				throw new BeCommunicationEncodeException("AP is a necessary field!");
			}

			/**
			 * AP identifier 's length = 6 + 1 + apMac.length()<br>
			 * abort data 's length = 6 + 4
			 */
			int apIdentifierLen = 7 + apMac.length();
			int abortDataLen = 10;
			int bufLength = apIdentifierLen + abortDataLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);

			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_ABORTDATA);
			buf.putInt(abortDataLen - 6);
			buf.putInt(abortType);

			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException("BeAbortEvent.buildPacket() catch exception",
					e);
		}
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			ByteBuffer buf = ByteBuffer.wrap(data);

			while (buf.hasRemaining()) {
				short msgType = buf.getShort();
				int msgLen = buf.getInt();

				// Abort event's data field
				// 1. AP Identifier
				// 2. result descriptor
				// 3. abort result (may be)
				if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER) {
					byte macLen = buf.get();
					apMac = AhDecoder.bytes2String(buf, AhDecoder.byte2int(macLen)).toUpperCase();
				} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_RESULTDESCRIPTOR) {
					// check length valid
					if (msgLen != 1) {
						throw new BeCommunicationDecodeException(
								"Invalid messge length in BeAbortEvent");
					}

					result = buf.get();
				} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_ABORTRESULT) {
					// check length valid
					if (msgLen != 4) {
						throw new BeCommunicationDecodeException(
								"Invalid messge length in BeAbortEvent");
					}

					abortResult = buf.getInt();
				} else {
					throw new BeCommunicationDecodeException(
							"Invalid messge element type in BeAbortEvent, type value = " + msgType);
				}
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException("BeAbortEvent.parsePacket() catch exception",
					e);
		}
	}

	public int getAbortResult() {
		return abortResult;
	}

	public void setAbortResult(int abortResult) {
		this.abortResult = abortResult;
	}

	public int getAbortType() {
		return abortType;
	}

	public void setAbortType(int abortType) {
		this.abortType = abortType;
	}
}
