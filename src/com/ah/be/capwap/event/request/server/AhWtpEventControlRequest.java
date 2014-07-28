/**
 *@filename		AhWtpEventControlRequest.java
 *@version
 *@author		Francis
 *@createtime	2007-10-10 02:43:13 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Inc.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.capwap.event.request.server;

// aerohive import
import com.ah.be.capwap.AhCapwapDecodeException;
import com.ah.be.capwap.AhCapwapEncodeException;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhWtpEventControlRequest extends AhCapwapServerRequest {

	private static final long	serialVersionUID	= 1L;

	private boolean				eventEnabling;

	public AhWtpEventControlRequest() {
		super(WTP_EVENT_CONTROL_REQUEST);
	}

	public AhWtpEventControlRequest(boolean eventEnabling) {
		this();
		this.eventEnabling = eventEnabling;
	}

	public AhWtpEventControlRequest(String serialNum) {
		super(WTP_EVENT_CONTROL_REQUEST, serialNum);
	}

	public AhWtpEventControlRequest(boolean eventEnabling, String serialNum) {
		this(serialNum);
		this.eventEnabling = eventEnabling;
	}

	public boolean isEventEnabling() {
		return eventEnabling;
	}

	public void setEventEnabling(boolean eventEnabling) {
		this.eventEnabling = eventEnabling;
	}

	@Override
	public void parsePacket() throws AhCapwapDecodeException {

	}

	/**
	 * WTP Event Control.
	 * <p>
     * 0 1 2 3 4 5 6 7 8
     * +-+-+-+-+-+-+-+-+
     * |    Enabling   |
     * +-+-+-+-+-+-+-+-+
	 * <p>
	 * Enabling : 0 - Enable.
	 *            1 - Disable.
	 * <p>
	 * @return  The packet of request option.
	 * @throws AhCapwapEncodeException  If error occurs in encoding capwap option packet.
	 */
	@Override
	public byte[] buildPacket() throws AhCapwapEncodeException {
		reqPacket = new byte[1];
		reqPacket[0] = eventEnabling ? (byte)WTP_EVENT_ENABLE : (byte)WTP_EVENT_DISABLE;

		return reqPacket;
	}

	@Override
	public String getRequestName() {
		return "WTP Event Control Request";
	}

}