package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhDecoder;

/**
 * WTP event control message type
 *@filename		BeWTPEventControlEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-10 02:41:10
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
@SuppressWarnings("serial")
public class BeWTPEventControlEvent extends BeCommunicationEvent {

	// private BeCommunicationMessageData wtpControlMsgtData;
	//
	// public BeCommunicationMessageData getWtpControlMsgtData()
	// {
	// return wtpControlMsgtData;
	// }
	//
	// public void setWtpControlMsgtData(
	// BeCommunicationMessageData wtpControlMsgtData)
	// {
	// this.wtpControlMsgtData = wtpControlMsgtData;
	// }

	/**
	 * WTP Event Control.
	 * <p>
	 * 0 1 2 3 4 5 6 7 8<br>
	 * +-+-+-+-+-+-+-+-+<br>
	 * | Enabling 	   |<br>
	 * +-+-+-+-+-+-+-+-+<br>
	 * <p>
	 * Enabling : 0 - Enable. 1 - Disable.
	 * <p>
	 */

	private boolean	enabling;

	public BeWTPEventControlEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_WTPCONTROLREQ;
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
			 * wtp control 's length = 6+ 1<br>
			 */
			int apIdentifierLen = 7 + apMac.length();
			int wtpLen = 7;
			int bufLength = apIdentifierLen + wtpLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_WTPEVENTCONTROL);
			buf.putInt(wtpLen - 6);
			buf.put(enabling ? (byte) BeCommunicationConstant.WTP_EVENT_ENABLE
					: (byte) BeCommunicationConstant.WTP_EVENT_DISABLE);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeWTPEventControlEvent.buildPacket() catch exception", e);
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
				}
				// else
				// if (msgType ==
				// BeCommunicationConstant.MESSAGEELEMENTTYPE_WTPEVENTCONTROL)
				// {
				// byte[] tmpBytes = new byte[msgLen];
				// buf.get(tmpBytes, 0, msgLen);
				// wtpControlMsgtData = BeCommunicationMessageData
				// .wrap(tmpBytes);
				// }
				else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_RESULTDESCRIPTOR) {
					// check length valid
					if (msgLen != 1) {
						throw new BeCommunicationDecodeException(
								"Invalid messge length in BeWTPEventControlEvent");
					}

					result = buf.get();
				} else {
					throw new BeCommunicationDecodeException(
							"Invalid messge element type in BeWTPEventControlEvent, type value = "
									+ msgType);
					// DebugUtil
					// .commonDebugWarn("Invalid messge type in
					// BeWTPEventControlEvent, type value = "
					// + msgType);
					//
					// return;
				}
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeWTPEventControlEvent.parsePacket() catch exception", e);
		}
	}

	public boolean isEnabling() {
		return enabling;
	}

	public void setEnabling(boolean enabling) {
		this.enabling = enabling;
	}

}