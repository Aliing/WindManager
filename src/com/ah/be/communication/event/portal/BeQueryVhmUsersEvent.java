package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class BeQueryVhmUsersEvent extends BePortalHMPayloadEvent {

	private static final long	serialVersionUID	= 1L;

	private String				vhmName;

	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		this.vhmName = vhmName;
	}

	public BeQueryVhmUsersEvent() {
		super();
		operationType = OPERATIONTYPE_QUERY_VHMUSERS;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		AhEncoder.putString(buf, vhmName);

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		vhmName = AhDecoder.getString(buf);

		return null;
	}
}
