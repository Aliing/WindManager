package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationDecodeException;

@SuppressWarnings("serial")
public class BeModifyVhmResult extends BePortalHMPayloadResultEvent {

	private int	numberOfAP	= -1;

	public BeModifyVhmResult() {
		super();
		payloadResultType = RESULTTYPE_VHM_MODIFY;
	}

	public void setNumberOfAP(int numberOfAP) {
		this.numberOfAP = numberOfAP;
	}

	public int getNumberOfAP() {
		return numberOfAP;
	}

	protected byte[] buildOperationResult() throws Exception {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		buf.put(super.buildOperationResult());

		buf.putInt(numberOfAP);

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

			numberOfAP = buf.getInt();

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeModifyVhmResult parsePacket() catch exception", e);
		}
	}

}
