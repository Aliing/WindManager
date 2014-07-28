package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;

@SuppressWarnings("serial")
public class BeHostIdentificationResultEvent extends BeAPWTPEvent {

	public static final int	RESULT_SUCCESS	= 0;

	public static final int	RESULT_FAILURE	= 1;

	private int				hostIdenti_result;

	public BeHostIdentificationResultEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGEELEMENTTYPE_HOSTIDENTIFICATIONKEYRESULT;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	@Override
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);

			ByteBuffer buf = ByteBuffer.wrap(getWtpMsgData());

			sequenceNum = buf.getInt();
			hostIdenti_result = buf.getInt();
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeCapwapHostIdentificationResultEvent.parsePacket() catch exception", e);
		}
	}

	public int getHostIdenti_result() {
		return hostIdenti_result;
	}

	public void setHostIdenti_result(int hostIdenti_result) {
		this.hostIdenti_result = hostIdenti_result;
	}
}
