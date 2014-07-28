package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;

/**
 * modify history*
 * 
 */
public class BeNaasLicenseResultEvent extends BeCapwapClientResultEvent {

	private static final long serialVersionUID = 1L;
	
	private byte					optType = BeNaasLicenseEvent.NAAS_LICENSE_OPT_SYNC;
	
	private int						currentCounter = 0;
	private int						maxCounter = 0;

	public BeNaasLicenseResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_AAATEST;
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
			ByteBuffer buf = ByteBuffer.wrap(resultData);

			optType = buf.get();
			buf.getShort();
			byte infoType;
			while(buf.hasRemaining()) {
				infoType = buf.get();
				buf.getShort();
				switch(infoType) {
				case BeNaasLicenseEvent.NAAS_LICENSE_CURRENT_COUNTER:
					currentCounter = buf.getInt();
					break;
				case BeNaasLicenseEvent.NAAS_LICENSE_MAX_COUNTER:
					maxCounter = buf.getInt();
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeNaasLicenseResultEvent.parsePacket() catch exception", e);
		}
	}

	public byte getOptType() {
		return optType;
	}

	public int getCurrentCounter() {
		return currentCounter;
	}

	public int getMaxCounter() {
		return maxCounter;
	}

}