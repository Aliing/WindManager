package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;

/**
 * delete cookie request/response class
 *@filename		BeDeleteCookieEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-12-5 01:48:01
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeDeleteCookieEvent extends BeCapwapClientInfoEvent {

	/**
	 * 0 means delete all cookies
	 */
	private int		cookie;

	/**
	 * event query type of cookie, definiation see as {@link BeCapwapClientInfoEvent}
	 */
	private short	cookieType;

	public BeDeleteCookieEvent() {
		super();
		queryType = BeCapwapClientInfoEvent.TYPE_DELETECOOKIE;
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
			 * query's length = 6 + 12
			 */
			int apIdentifierLen = 7 + apMac.length();
			int queryLen = 18;
			int bufLength = apIdentifierLen + queryLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_INFORMATIONQUERY);
			buf.putInt(12);
			buf.putShort(queryType);
			buf.putInt(6); // data length
			buf.putShort(cookieType);
			buf.putInt(cookie);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeDeleteCookieEvent.buildPacket() catch exception", e);
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
		super.parsePacket(data);
		
		// it's infoData is empty
	}

	public int getCookie() {
		return cookie;
	}

	public void setCookie(int cookie) {
		this.cookie = cookie;
	}

	public short getCookieType() {
		return cookieType;
	}

	public void setCookieType(short cookieType) {
		this.cookieType = cookieType;
	}
}
