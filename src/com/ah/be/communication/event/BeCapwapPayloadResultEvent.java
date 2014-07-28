package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;

/**
 * 
 *@filename		BeCapwapPayloadResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-1-26 10:22:52
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeCapwapPayloadResultEvent extends BeAPWTPEvent {

	protected short		queryType		= BeCapwapPayloadEvent.QUERYTYPE_PORTALANDHM;

	/**
	 * constant defined in {@link BeCommunicationConstant}
	 */
	private byte		compressFlag	= BeCommunicationConstant.NOTCOMPRESS;

	private int			originalDataLen;

	private int			dataLen;

	protected byte[]	resultData;

	protected byte		payloadResultType;

	public BeCapwapPayloadResultEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPPAYLOADRESULT;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);

			ByteBuffer buf = ByteBuffer.wrap(getWtpMsgData());

			queryType = buf.getShort();
			sequenceNum = buf.getInt();
			compressFlag = buf.get();
			payloadResultType = buf.get();
			originalDataLen = buf.getInt();
			dataLen = buf.getInt();

			// not consider about compress now.
			resultData = new byte[dataLen];
			buf.get(resultData, 0, dataLen);

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeCapwapPayloadResultEvent.parsePacket() catch exception", e);
		}
	}

	public byte getCompressFlag() {
		return compressFlag;
	}

	public void setCompressFlag(byte compressFlag) {
		this.compressFlag = compressFlag;
	}

	public int getDataLen() {
		return dataLen;
	}

	public void setDataLen(int dataLen) {
		this.dataLen = dataLen;
	}

	public int getOriginalDataLen() {
		return originalDataLen;
	}

	public void setOriginalDataLen(int originalDataLen) {
		this.originalDataLen = originalDataLen;
	}

	public short getQueryType() {
		return queryType;
	}

	public void setQueryType(short queryType) {
		this.queryType = queryType;
	}

	public byte[] getResultData() {
		return resultData;
	}

	public void setResultData(byte[] resultData) {
		this.resultData = resultData;
	}

	public byte getPayloadResultType() {
		return payloadResultType;
	}

	public void setPayloadResultType(byte payloadResultType) {
		this.payloadResultType = payloadResultType;
	}
}
