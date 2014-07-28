package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhDecoder;

/**
 * delete ap connect message type
 *@filename		BeDeleteAPConnectEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-12 03:02:30
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
@SuppressWarnings("serial")
public class BeDeleteAPConnectEvent extends BeCommunicationEvent {
	/**
	 * if u want just disconnect ap, set this flag to false.
	 */
	private boolean	deleteAP	= true;

	public BeDeleteAPConnectEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_DELETEAPCONNECTREQ;
	}

	/**
	 * build event data to packet message
	 * 
	 * @return BeCommunicationMessageData
	 * @throws BeCommunicationEncodeException -
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		if (apMac == null) {
			throw new BeCommunicationEncodeException("ApMac is a necessary field!");
		}

		try {
			/**
			 * AP identifier 's length = 6 + 1 + apSerialNum.length()<br>
			 */
			int apIdentifierLen = 7 + apMac.length();
			int apFlagLen = 6 + 1;
			ByteBuffer buf = ByteBuffer.allocate(apIdentifierLen + apFlagLen);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APFLAG);
			buf.putInt(1);
			buf.put((byte)(deleteAP ? 1 : 0));
			
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeDeleteAPConnectEvent.buildPacket() catch exception", e);
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
								"Invalid messge length in BeDeleteAPConnectEvent");
					}

					result = buf.get();
				} else {
					throw new BeCommunicationDecodeException(
							"Invalid messge element type in BeDeleteAPConnectEvent, type value = "
									+ msgType);
					// DebugUtil
					// .commonDebugWarn("Invalid messge type in
					// BeDeleteAPConnectEvent, type value = "
					// + msgType);
					//
					// return;
				}
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeDeleteAPConnectEvent.parsePacket() catch exception", e);
		}
	}

	public boolean isDeleteAP() {
		return deleteAP;
	}

	public void setDeleteAP(boolean deleteAP) {
		this.deleteAP = deleteAP;
	}

}