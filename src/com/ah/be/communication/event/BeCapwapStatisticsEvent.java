package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeCapwapStatisticsEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-11-30 11:25:16
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeCapwapStatisticsEvent extends BeCommunicationEvent {

	private long	totalRX;

	private long	totalTX;

	private long	errorRX;

	private long	errorTX;

	private long	dropRX;

	private long	dropTX;

	public BeCapwapStatisticsEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_CAPWAPSTATREQ;
	}

	@Override
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		try {
			int bufLength = 6;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_HASTATUS);
			buf.putInt(bufLength - 6);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeCapwapStatisticsEvent.buildPacket() catch exception", e);
		}
	}

	@Override
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			ByteBuffer buf = ByteBuffer.wrap(data);
			short msgType = buf.getShort();
			buf.getInt();

			if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPSTATISTICS) {
				totalRX = AhDecoder.int2long(buf.getInt());
				totalTX = AhDecoder.int2long(buf.getInt());
				errorRX = AhDecoder.int2long(buf.getInt());
				errorTX = AhDecoder.int2long(buf.getInt());
				dropRX = AhDecoder.int2long(buf.getInt());
				dropTX = AhDecoder.int2long(buf.getInt());
			} else {
				throw new BeCommunicationDecodeException(
						"Invalid messge element type in BeCapwapStatisticsEvent, type value = "
								+ msgType);
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeCapwapStatisticsEvent.parsePacket() catch exception", e);
		}
	}

	public long getDropRX() {
		return dropRX;
	}

	public void setDropRX(long dropRX) {
		this.dropRX = dropRX;
	}

	public long getDropTX() {
		return dropTX;
	}

	public void setDropTX(long dropTX) {
		this.dropTX = dropTX;
	}

	public long getErrorRX() {
		return errorRX;
	}

	public void setErrorRX(long errorRX) {
		this.errorRX = errorRX;
	}

	public long getErrorTX() {
		return errorTX;
	}

	public void setErrorTX(long errorTX) {
		this.errorTX = errorTX;
	}

	public long getTotalRX() {
		return totalRX;
	}

	public void setTotalRX(long totalRX) {
		this.totalRX = totalRX;
	}

	public long getTotalTX() {
		return totalTX;
	}

	public void setTotalTX(long totalTX) {
		this.totalTX = totalTX;
	}
}
