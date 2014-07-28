package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.Map;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;

/**
 * 
 *@filename		BeInterferenceMapEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-5-4 03:05:48
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeInterferenceMapEvent extends BeCapwapClientEvent {

	public static final byte	REQUESTCODE_ACSPLIST			= 1;

	public static final byte	REQUESTCODE_INTERFERENCEDATA	= 2;

	public static final byte	REQUESTCODE_BOTH				= 3;

	/**
	 * key: ifindex<br>
	 * value: request code
	 */
	private Map<Integer, Byte>	ifindexRequestMap;

	/**
	 * if requestAll = true, ap will report all interfaces' Interference Data and ACSP list.
	 */
	private boolean				requestAll						= false;

	public BeInterferenceMapEvent() {
		super();
		queryType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_INTERFERENCEMAP;
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

		if (sequenceNum <= 0) {
			throw new BeCommunicationEncodeException("sequenceNum is a necessary field!");
		}

		try {
			byte[] requestData = getRequestData();

			/**
			 * AP identifier 's length = 6 + 1 + apSerialNum.length()<br>
			 * query's length = 6 + 11 + requestData.length
			 */
			int apIdentifierLen = 7 + apMac.length();
			int queryLen = 17 + requestData.length;
			int bufLength = apIdentifierLen + queryLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTQUERY);
			buf.putInt(11 + requestData.length); // 2+4+1+4+reqestData.length
			buf.putShort(queryType);
			buf.putInt(sequenceNum);
			buf.put(flag);
			buf.putInt(requestData.length); // data length
			buf.put(requestData);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeInterferenceMapEvent.buildPacket() catch exception", e);
		}
	}

	/**
	 * get request data
	 * 
	 * @param
	 * 
	 * @return
	 */
	private byte[] getRequestData() throws BeCommunicationEncodeException {
		if (!requestAll && (ifindexRequestMap == null || ifindexRequestMap.size() == 0)) {
			throw new BeCommunicationEncodeException("ifindexRequestMap is a request field!");
		}

		int dataLen = 0;
		if (requestAll) {
			dataLen = 1;
		} else {
			dataLen = 1 + ifindexRequestMap.size() * 5;
		}

		ByteBuffer buf = ByteBuffer.allocate(dataLen);

		if (requestAll) {
			buf.put((byte) 255);
		} else {
			buf.put((byte) ifindexRequestMap.size());
			for (Integer ifindex : ifindexRequestMap.keySet()) {
				byte requestCode = ifindexRequestMap.get(ifindex);
				buf.putInt(ifindex);
				buf.put(requestCode);
			}
		}

		return buf.array();
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
	}

	public Map<Integer, Byte> getIfindexRequestMap() {
		return ifindexRequestMap;
	}

	public void setIfindexRequestMap(Map<Integer, Byte> ifindexRequestMap) {
		this.ifindexRequestMap = ifindexRequestMap;
	}

	public boolean isRequestAll() {
		return requestAll;
	}

	public void setRequestAll(boolean requestAll) {
		this.requestAll = requestAll;
	}
}
