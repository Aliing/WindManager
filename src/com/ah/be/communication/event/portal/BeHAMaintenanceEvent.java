package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.mo.HmolInfo;

@SuppressWarnings("serial")
public class BeHAMaintenanceEvent extends BePortalHMPayloadEvent {

	private short hmStatus = HmolInfo.HHMSTATUS_NORMAL;

	public BeHAMaintenanceEvent() {
		super();
		operationType = OPERATIONTYPE_HA_MAINTENANCE;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		buf.putShort(hmStatus);

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		hmStatus = buf.getShort();

		return null;
	}

	public short getHmStatus() {
		return hmStatus;
	}

	public void setHmStatus(short hmStatus) {
		this.hmStatus = hmStatus;
	}
}
