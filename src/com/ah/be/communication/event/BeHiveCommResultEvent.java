package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeHiveCommResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-12-21 03:00:08
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeHiveCommResultEvent extends BeCapwapClientResultEvent {

	/**
	 * ap mac - return code (definition see in BeCapwapCliResultEvent)
	 */
	private Map<String, Integer>	hivecommResult	= new HashMap<String, Integer>();

	/**
	 * definition see in BeHiveCommEvent
	 */
	private int						hiveMessageType;

	public BeHiveCommResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_HIVECOMM;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	@Override
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);

			hiveMessageType = buf.getInt();
			short listSize = (short) (buf.getShort() / 12);
			for (int i = 0; i < listSize; i++) {
				buf.getShort();
				String macAddress = AhDecoder.bytes2hex(buf, 6).toUpperCase();
				int returnCode = buf.getInt();
				hivecommResult.put(macAddress, returnCode);
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeHiveCommResultEvent.parsePacket() catch exception", e);
		}
	}

	public Map<String, Integer> getHivecommResult() {
		return hivecommResult;
	}

	public void setHivecommResult(Map<String, Integer> hivecommResult) {
		this.hivecommResult = hivecommResult;
	}

	public int getHiveMessageType() {
		return hiveMessageType;
	}

	public void setHiveMessageType(int hiveMessageType) {
		this.hiveMessageType = hiveMessageType;
	}
}
