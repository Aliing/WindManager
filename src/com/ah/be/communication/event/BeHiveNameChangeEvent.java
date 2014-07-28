package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;

@SuppressWarnings("serial")
public class BeHiveNameChangeEvent extends BeAPWTPEvent {

	private String	hiveName;

	public BeHiveNameChangeEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGEELEMENTTYPE_HIVENAMECHANGE;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data
	 *            -
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);

			ByteBuffer buf = ByteBuffer.wrap(getWtpMsgData());

			//sequenceNum = buf.getInt();
			hiveName = AhDecoder.bytes2String(buf, buf.remaining());
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeHiveNameChangeEvent() catch exception", e);
		}
	}

	public String getHiveName() {
		return hiveName;
	}

	public void setHiveName(String hiveName) {
		this.hiveName = hiveName;
	}
}
