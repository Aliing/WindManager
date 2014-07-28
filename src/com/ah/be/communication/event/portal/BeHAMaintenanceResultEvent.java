package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.mo.HmolInfo;

public class BeHAMaintenanceResultEvent extends BePortalHMPayloadResultEvent {

	private static final long serialVersionUID = 1L;

	private short hmStatus = HmolInfo.HHMSTATUS_NORMAL;

	public BeHAMaintenanceResultEvent() {
		super();
		payloadResultType = RESULTTYPE_MAINTENANCE;
	}

	protected byte[] buildOperationResult() throws Exception {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		buf.put(super.buildOperationResult());

		buf.putShort(hmStatus);

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

			hmStatus = buf.getShort();
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeHAMaintenanceResultEvent.parsePacket() catch exception", e);
		}
	}

	public short getHmStatus() {
		return hmStatus;
	}

	public void setHmStatus(short hmStatus) {
		this.hmStatus = hmStatus;
	}
}
