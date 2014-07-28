package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhDecoder;

/**
 * AP WTP event message type
 *@filename		BeAPWTPEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-10 02:24:53
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BeAPWTPEvent extends BeCommunicationEvent {

	private static final long			serialVersionUID	= 1L;

	/**
	 * one of the follows:<br>
	 * 6001.IDP STATISTICS<br>
	 * 6101.AP TYPE CHANGE<br>
	 * 6102.CLI RESULT<br>
	 * 6103.FILE DOWNLOAD PROGRESS
	 */
	private int							wtpMsgType;

	private byte[]	wtpMsgData;

	public byte[] getWtpMsgData() {
		return wtpMsgData;
	}

	public void setWtpMsgData(byte[] wtpMsgData) {
		this.wtpMsgData = wtpMsgData;
	}

	public int getWtpMsgType() {
		return wtpMsgType;
	}

	public void setWtpMsgType(int wtpMsgType) {
		this.wtpMsgType = wtpMsgType;
	}

	public BeAPWTPEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_APWTPEVENT;
	}

	/**
	 * build event data to packet message
	 * 
	 * @return BeCommunicationMessageData
	 * @throws BeCommunicationEncodeException -
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		try {
			if (apMac == null) {
				throw new BeCommunicationEncodeException("ApMac is a necessary field!");
			}

			/**
			 * AP identifier 's length = 6 + 1 + apSerialNum.length()<br>
			 * another 's length = 6+ ....
			 */
			int apIdentifierLen = 7 + apMac.length();
			int wtpLen = 6 + wtpMsgData.length;
			int bufLength = apIdentifierLen + wtpLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			byte[] msgType = AhDecoder.toByteArray(
					BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER, 2);
			buf.put(msgType);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			msgType = AhDecoder.toByteArray(wtpMsgType, 2);
			buf.put(msgType);
			buf.putInt(wtpLen - 6);
			buf.put(wtpMsgData);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException("BeAPWTPEvent.buildPacket() catch exception",
					e);
		}
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
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
				} else {
					wtpMsgType = msgType;

					wtpMsgData = new byte[msgLen];
					buf.get(wtpMsgData, 0, msgLen);
				}
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException("BeAPWTPEvent.parsePacket() catch exception",
					e);
		}
//		SimpleHiveAp simpleHiveAp = getSimpleHiveAp();
//		if(simpleHiveAp == null) {
//			throw new BeCommunicationDecodeException("BeAPWTPEvent.parsePacket(): receive CAPWAP WTP message["+
//		wtpMsgType+"] from ap["+apMac+"] which is not existed in HiveAP cache");
//		}
	}

}