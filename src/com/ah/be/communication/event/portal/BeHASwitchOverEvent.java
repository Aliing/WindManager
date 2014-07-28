package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;

@SuppressWarnings("serial")
public class BeHASwitchOverEvent extends BePortalHMPayloadEvent {
	
	public BeHASwitchOverEvent() {
		super();
		operationType = OPERATIONTYPE_HA_SWITCHOVER;
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
