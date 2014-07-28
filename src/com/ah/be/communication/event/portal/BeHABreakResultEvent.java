package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationDecodeException;

public class BeHABreakResultEvent extends BePortalHMPayloadResultEvent {

	private static final long serialVersionUID = -2328809795767686873L;

	private int exitValue = 0;

	public BeHABreakResultEvent() {
		super();
		payloadResultType = RESULTTYPE_BREAKUP;
	}

	protected byte[] buildOperationResult() throws Exception {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		buf.put(super.buildOperationResult());

		buf.putInt(exitValue);

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

			exitValue = buf.getInt();
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeHABreakResultEvent.parsePacket() catch exception", e);
		}
	}

	public int getExitValue() {
		return exitValue;
	}

	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}
}
