package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeCapwapClientConnectEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-11-23 03:21:05
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeCapwapClientConnectEvent extends BeCommunicationEvent {

	public static final byte SERVERTYPE_PORTAL = 1;
	
	private byte	serverType;

	private boolean	connected;

	private String	capwapServerIP;

	private String	capwapServerMac;

	public BeCapwapClientConnectEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTCONNECT;
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

			short msgType = buf.getShort();
			buf.getInt();

			if (msgType != BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTCONNECTDESCRIPTOR) {
				throw new BeCommunicationDecodeException(
						"Invalid messge element type in BeCapwapClientConnectEvent, type value = "
								+ msgType);
			}
			
			serverType = buf.get();
			connected = buf.get() == 1;
			capwapServerIP = AhDecoder.int2IP(buf.getInt());
			capwapServerMac = AhDecoder.bytes2hex(buf,6).toUpperCase();

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeCapwapClientConnectEvent.parsePacket() catch exception", e);
		}
	}

	public String getCapwapServerIP() {
		return capwapServerIP;
	}

	public void setCapwapServerIP(String capwapServerIP) {
		this.capwapServerIP = capwapServerIP;
	}

	public String getCapwapServerMac() {
		return capwapServerMac;
	}

	public void setCapwapServerMac(String capwapServerMac) {
		this.capwapServerMac = capwapServerMac;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public byte getServerType() {
		return serverType;
	}

	public void setServerType(byte serverType) {
		this.serverType = serverType;
	}
}
