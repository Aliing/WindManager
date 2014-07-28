/**
 *@filename		AhApTypeChangeEvent.java
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
package com.ah.be.capwap.event;

// aerohive import
import com.ah.be.capwap.AhCapwapDecodeException;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhApTypeChangeEvent extends AhWtpEvent {

	private static final long	serialVersionUID	= 1L;

	private int apType;

	public AhApTypeChangeEvent() {
		super(AP_TYPE_CHANGE);
	}

	public AhApTypeChangeEvent(byte[] packet) {
		super(AP_TYPE_CHANGE, packet);
	}

	public int getApType() {
		return apType;
	}

	/**
	 * Ap Type (Defined by Aerohive, msg type : 5006)
	 * <p>
	 * +-+-+-+-+-+-+-+-+
	 * |    Portal     |
	 * +-+-+-+-+-+-+-+-+
	 * <p>
	 * Portal:   An 8-bit boolean stating whether the Ap is Portal or MP.
	 *           A value of zero indicates MP and one indicates Portal.
	 * <p>
	 * @throws AhCapwapDecodeException  if error occurs in parsing capwap option message.
	 */
	@Override
	public void parsePacket() throws AhCapwapDecodeException {
		if (packet == null) {
			throw new AhCapwapDecodeException("Event packet is required.");
		}

		if (fsm == null) {
			throw new AhCapwapDecodeException("Fsm is required.");
		}

		apType = packet[0];
	}

}