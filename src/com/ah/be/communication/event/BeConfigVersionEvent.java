package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;

@SuppressWarnings("serial")
public class BeConfigVersionEvent extends BeAPWTPEvent {
	private int	versionNumber;

	public BeConfigVersionEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGEELEMENTTYPE_CONFIGVERSIONEVENT;
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
			byte[] buffer = getWtpMsgData();
			versionNumber = ByteBuffer.wrap(buffer).getInt();
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeConfigVersionEvent.parsePacket catch exception", e);
		}
	}

	public int getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}
}
