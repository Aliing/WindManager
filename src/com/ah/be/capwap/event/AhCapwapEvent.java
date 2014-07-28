/**
 *@filename		AhCapwapEvent.java
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
import com.ah.be.capwap.AhCapwapConstants;
import com.ah.be.capwap.AhCapwapDecodeException;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public abstract class AhCapwapEvent implements AhCapwapConstants {

	private static final long	serialVersionUID	= 1L;

	protected byte[] packet;
	protected int type;
	protected String serialNum;

	protected AhCapwapEvent() {
		super();
	}

	protected AhCapwapEvent(int type) {
		this.type = type;
	}

	protected AhCapwapEvent(String serialNum) {
		this.serialNum = serialNum;
	}

	protected AhCapwapEvent(int type, String serialNum) {
		this.type = type;
		this.serialNum = serialNum;
	}

	public byte[] getPacket() {
		return packet;
	}

	public void setPacket(byte[] packet) {
		this.packet = packet;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public abstract void parsePacket() throws AhCapwapDecodeException;

}