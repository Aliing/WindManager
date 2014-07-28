package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.bo.hiveap.HiveAp;

/**
 * dtls config
 *@filename		BeCapwapDTLSConfigEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-12-3 19:14:24
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BeCapwapDTLSConfigEvent extends BeCommunicationEvent {

	private static final long	serialVersionUID	= 1L;

	private List<HiveAp>		apList;

	private byte				operationType		= BeCommunicationConstant.DTLSOPERTYPE_REMOVEAPCONNECTION;

	/**
	 * Construct method
	 */
	public BeCapwapDTLSConfigEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_CAPWAPDTLSCONFIGREQ;
	}

	/**
	 * build event data to packet message
	 * 
	 * @return BeCommunicationMessageData
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		if (apList == null) {
			throw new BeCommunicationEncodeException("apList is a required field!");
		}

		if (apList.size() == 0) {
			return new byte[0];
		}

		try {
			/**
			 * AP dtls descriptor 's length = 6 + apList.size() * ( 1 + apMac.length() +1 + 1 + 1 +
			 * passPhrase.length)
			 */
			int bufLen = 6;
			for (HiveAp ap : apList) {
				int macAddressLength = ap.getMacAddress().length();
				int passPhraseLength = ap.getCurrentPassPhrase() == null ? 0 : ap
						.getCurrentPassPhrase().length();

				int apDtlsDescriptorLen = 4 + macAddressLength + passPhraseLength;
				bufLen += apDtlsDescriptorLen;
			}
			ByteBuffer buf = ByteBuffer.allocate(bufLen);

			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APDTLSDESCRIPTOR);
			buf.putInt(bufLen - 6);
			for (HiveAp ap : apList) {
				buf.put((byte) ap.getMacAddress().length());
				buf.put(ap.getMacAddress().getBytes());
				buf.put(operationType);
				buf.put((byte) ap.getCurrentKeyId());
				byte len = (byte) (ap.getCurrentPassPhrase() == null ? 0 : ap
						.getCurrentPassPhrase().length());
				buf.put(len);
				if (len > 0) {
					buf.put(ap.getCurrentPassPhrase().getBytes());
				}
			}

			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeCapwapDTLSConfigEvent.buildPacket() catch exception", e);
		}
	}

	public List<HiveAp> getApList() {
		return apList;
	}

	public void setApList(List<HiveAp> apList) {
		this.apList = apList;
	}

	public byte getOperationType() {
		return operationType;
	}

	public void setOperationType(byte operationType) {
		this.operationType = operationType;
	}

}
