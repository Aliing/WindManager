package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeRemoveUserEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-1-26 10:04:42
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeRemoveUserEvent extends BePortalHMPayloadEvent {

	private String	vhmName = "";

	private String	userName;

	public BeRemoveUserEvent() {
		super();
		operationType = OPERATIONTYPE_USER_REMOVE;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		if (userName == null) {
			throw new BeCommunicationEncodeException("userName is a necessary field!");
		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		buf.putShort((short) userName.length());
		buf.put(userName.getBytes());

		buf.putShort((short) vhmName.length());
		buf.put(vhmName.getBytes());

		buf.flip();
		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		short len = buf.getShort();
		userName = AhDecoder.bytes2String(buf, len);

		len = buf.getShort();
		vhmName = AhDecoder.bytes2String(buf, len);

		return null;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		this.vhmName = vhmName;
	}
}
