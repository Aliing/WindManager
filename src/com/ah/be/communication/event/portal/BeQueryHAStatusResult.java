package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.ha.HAStatus;

@SuppressWarnings("serial")
public class BeQueryHAStatusResult extends BePortalHMPayloadResultEvent {

	private short hmolHaStatus 				= HAStatus.STATUS_UNKNOWN;

	public BeQueryHAStatusResult() {
		super();
		payloadResultType = RESULTTYPE_QUERY_HASTATUS;
	}

	@Override
	protected byte[] buildOperationResult() throws Exception {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		buf.put(super.buildOperationResult());

		buf.putShort(hmolHaStatus);

		buf.flip();
		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);

			ByteBuffer buf = ByteBuffer.wrap(getResultData());

			hmolHaStatus = buf.getShort();
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeQueryHAStatusResult parsePacket() catch exception", e);
		}
	}

	public short getHmolHaStatus() {
		return hmolHaStatus;
	}

	public void setHmolHaStatus(short hmolHaStatus) {
		this.hmolHaStatus = hmolHaStatus;
	}
}
