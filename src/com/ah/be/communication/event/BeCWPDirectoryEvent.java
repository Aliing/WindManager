package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeCWPDirectoryEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-8-11 05:23:31
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeCWPDirectoryEvent extends BeCommunicationEvent {

//	private int	cwpSerialNumber	= 0;

	public BeCWPDirectoryEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_SHOWCWPDIRECTORYREQ;
	}

	/**
	 * build event data to packet message
	 * 
	 * @return BeCommunicationMessageData
	 * @throws BeCommunicationEncodeException -
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		try {
			if (simpleHiveAp == null && ap == null) {
				throw new BeCommunicationEncodeException("AP is a necessary field!");
			}

			/**
			 * AP identifier 's length = 6 +1 + apMac.length()<br>
			 * CWP query 's length = 6 + 4<br>
			 */
			int apIdentifierLen = 7 + apMac.length();
			int cwpLen = 10;
			int bufLength = apIdentifierLen + cwpLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CWPDIRECTORYQUERY);
			buf.putInt(cwpLen - 6);
			buf.putInt(sequenceNum);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeCWPDirectoryEvent.buildPacket() catch exception", e);
		}
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 * @throws BeCommunicationDecodeException -
	 */
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			ByteBuffer buf = ByteBuffer.wrap(data);
			while (buf.hasRemaining()) {
				short msgType = buf.getShort();
				int msgLen = buf.getInt();

				if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER) {
					byte macLen = buf.get();
					apMac = AhDecoder.bytes2String(buf, AhDecoder.byte2int(macLen)).toUpperCase();
				} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_RESULTDESCRIPTOR) {
					// check length valid
					if (msgLen != 1) {
						throw new BeCommunicationDecodeException(
								"Invalid messge length in BeScriptConfigEvent");
					}

					result = buf.get();
				} else {
					throw new BeCommunicationDecodeException(
							"Invalid messge element type in BeCWPDirectoryEvent, type value = " + msgType);
					// DebugUtil
					// .commonDebugWarn("Invalid messge type in BeCWPDirectoryEvent,
					// type value = "
					// + msgType);
					//
					// return;
				}
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeCWPDirectoryEvent.parsePacket() catch exception", e);
		}
	}
//
//	public int getCwpSerialNumber() {
//		return cwpSerialNumber;
//	}
//
//	public void setCwpSerialNumber(int cwpSerialNumber) {
//		this.cwpSerialNumber = cwpSerialNumber;
//	}
}
