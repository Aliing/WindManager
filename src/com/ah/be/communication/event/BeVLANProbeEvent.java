package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;

/**
 * 
 *@filename		BeVLANProbeEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-2-24 03:03:55
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeVLANProbeEvent extends BeCapwapClientEvent {

	private short	minVlanId;

	private short	maxVlanId;

	private short	retry;

	// seconds unit
	private short	probeTimeout;

	public BeVLANProbeEvent() {
		super();
		queryType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VLANPROBE;
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
			/**
			 * AP identifier 's length = 6 + 1 + apSerialNum.length()<br>
			 * query's length = 6 + 11 + 8
			 */
			int apIdentifierLen = 7 + apMac.length();
			int queryLen = 25;
			int bufLength = apIdentifierLen + queryLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTQUERY);
			buf.putInt(19); // 2+4+1+4+8
			buf.putShort(queryType);
			buf.putInt(sequenceNum);
			buf.put(flag);
			buf.putInt(8); // data length
			buf.putShort(minVlanId);
			buf.putShort(maxVlanId);
			buf.putShort(retry);
			buf.putShort(probeTimeout);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeVLANProbeEvent.buildPacket() catch exception", e);
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
	}

	public short getMaxVlanId() {
		return maxVlanId;
	}

	public void setMaxVlanId(short maxVlanId) {
		this.maxVlanId = maxVlanId;
	}

	public short getMinVlanId() {
		return minVlanId;
	}

	public void setMinVlanId(short minVlanId) {
		this.minVlanId = minVlanId;
	}

	public short getRetry() {
		return retry;
	}

	public void setRetry(short retry) {
		this.retry = retry;
	}

	public short getProbeTimeout() {
		return probeTimeout;
	}

	public void setProbeTimeout(short probeTimeout) {
		this.probeTimeout = probeTimeout;
	}

}
