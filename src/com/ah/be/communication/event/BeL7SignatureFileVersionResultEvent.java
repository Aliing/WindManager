package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;

@SuppressWarnings("serial")
public class BeL7SignatureFileVersionResultEvent extends BeCapwapClientResultEvent {
	
	private int	l7SignatureFileVersion = 0;
	
	public BeL7SignatureFileVersionResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_L7_SIGNATURE_FILE_VERSION;
	}

	/** 
	 * parse packet message to event data
	 * 
	 * @see com.ah.be.communication.event.BeCapwapClientResultEvent#parsePacket(byte[])
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);
			
			l7SignatureFileVersion = buf.getInt();
			
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeL7SignatureFileVersionResultEvent.parsePacket() catch exception", e);
		}
	}

	public int getL7SignatureFileVersion() {
		return l7SignatureFileVersion;
	}
}
