/**
 *@filename		AhClientRequest.java
 *@version
 *@author		Long
 *@createtime	2007-8-4 12:42:47 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.capwap.event.request.client;

// java import
import com.ah.be.capwap.event.AhCapwapEvent;
import com.ah.be.capwap.AhCapwapDecodeException;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * @author Long
 * @version V1.0.0.0
 */
public class AhCapwapClientRequest extends AhCapwapEvent {

	private static final long serialVersionUID = 1L;

	public enum RequestType {
		CLIENT_REQUEST, TIMEOUT_REQUEST
	}

	private RequestType reqType;
	private SocketAddress reqSocket;
	private ByteBuffer reqPacket;

	public AhCapwapClientRequest(RequestType reqType) {
		this.reqType = reqType;
	}

	public AhCapwapClientRequest(RequestType reqType, SocketAddress reqSocket,
			ByteBuffer reqPacket) {
		this(reqType);
		this.reqSocket = reqSocket;
		this.reqPacket = reqPacket;
	}

	public RequestType getReqType() {
		return reqType;
	}

	public SocketAddress getReqSocket() {
		return reqSocket;
	}

	public void setReqSocket(SocketAddress reqSocket) {
		this.reqSocket = reqSocket;
	}

	public ByteBuffer getReqPacket() {
		return reqPacket;
	}

	public void setReqPacket(ByteBuffer reqPacket) {
		this.reqPacket = reqPacket;
	}

	public void parsePacket() throws AhCapwapDecodeException {

	}

}