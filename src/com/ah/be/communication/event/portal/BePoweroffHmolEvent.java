package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;

public class BePoweroffHmolEvent extends BePortalHMPayloadEvent {
	private static final long	serialVersionUID	= 1L;

	public BePoweroffHmolEvent() {
		super();
		operationType = OPERATIONTYPE_HMOL_POWEROFF;
	}
	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		return null;
	}
}
