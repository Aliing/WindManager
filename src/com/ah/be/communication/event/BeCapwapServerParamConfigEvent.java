package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;

/**
 * Capwap server parameter config message type
 *@filename		BeCapwapServerParamConfigEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-10 02:30:48
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BeCapwapServerParamConfigEvent extends BeCommunicationEvent {

	private static final long	serialVersionUID	= 1L;

	private short				udpPort;

	private short				echoTimeout;

	private short				neighborDeadInterval;

	private byte				dtlsCapability;

	private String				passPhrase;

	private boolean				supportSimulator;

	/**
	 * Construct method
	 */
	public BeCapwapServerParamConfigEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_CAPWAPSERVERCONFIGREQ;
	}

	/**
	 * build event data to packet message
	 * 
	 * @return BeCommunicationMessageData
	 * @throws BeCommunicationEncodeException -
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		try {
			if (passPhrase == null) {
				passPhrase = "";
			}

			/**
			 * capwap server descriptor 's length = 6 + 9 + passphrase.length()<br>
			 */
			int bufLength = 15 + passPhrase.length();
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPSERVERDESCRIPTOR);
			buf.putInt(bufLength - 6);
			buf.putShort(udpPort);
			buf.putShort(echoTimeout);
			buf.putShort(neighborDeadInterval);
			buf.put(dtlsCapability);
			buf.put((byte) passPhrase.length());
			if (passPhrase.length() > 0) {
				buf.put(passPhrase.getBytes());
			}
			buf.put(supportSimulator ? (byte) 1 : (byte) 0);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeCapwapServerParamConfigEvent.buildPacket() catch exception", e);
		}
	}

	public String getPassPhrase() {
		return passPhrase;
	}

	public void setPassPhrase(String bootStrap) {
		this.passPhrase = bootStrap;
	}

	public short getEchoTimeout() {
		return echoTimeout;
	}

	public void setEchoTimeout(short echoTimeout) {
		this.echoTimeout = echoTimeout;
	}

	public short getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(short udpPort) {
		this.udpPort = udpPort;
	}

	public short getNeighborDeadInterval() {
		return neighborDeadInterval;
	}

	public void setNeighborDeadInterval(short neighborDeadInterval) {
		this.neighborDeadInterval = neighborDeadInterval;
	}

	public byte getDtlsCapability() {
		return dtlsCapability;
	}

	public void setDtlsCapability(byte dtlsCapability) {
		this.dtlsCapability = dtlsCapability;
	}

	public boolean isSupportSimulator() {
		return supportSimulator;
	}

	public void setSupportSimulator(boolean supportSimulator) {
		this.supportSimulator = supportSimulator;
	}

}