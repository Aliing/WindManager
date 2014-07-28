package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.mo.HmolInfo;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class BeModifyHMOLEvent extends BePortalHMPayloadEvent {

	private static final long serialVersionUID = 1L;

	private HmolInfo hmolInfo;

	public BeModifyHMOLEvent() {
		super();
		operationType = OPERATIONTYPE_HMOL_MODIFY;
	}

	public HmolInfo getHmolInfo() {
		return hmolInfo;
	}

	public void setHmolInfo(HmolInfo hmolInfo) {
		this.hmolInfo = hmolInfo;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		AhEncoder.putString(buf, hmolInfo.getHmolName());
		AhEncoder.putString(buf, hmolInfo.getHmolIpAddress());
		AhEncoder.putString(buf, hmolInfo.getHmolVersion());
		AhEncoder.putString(buf, hmolInfo.getMacAddress());
		AhEncoder.putString(buf, hmolInfo.getSystemId());
		buf.putShort(hmolInfo.getHmolType());
		buf.putInt(hmolInfo.getMaxApNum());
		buf.putInt(hmolInfo.getMaxVhmNum());
		buf.putShort(hmolInfo.getHmolStatus());
		AhEncoder.putString(buf, hmolInfo.getVirtualHostName());
		buf.putInt(hmolInfo.getHeartbeatTimeOutValue());

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		hmolInfo = new HmolInfo();
		hmolInfo.setHmolName(AhDecoder.getString(buf));
		hmolInfo.setHmolIpAddress(AhDecoder.getString(buf));
		hmolInfo.setHmolVersion(AhDecoder.getString(buf));
		hmolInfo.setMacAddress(AhDecoder.getString(buf));
		hmolInfo.setSystemId(AhDecoder.getString(buf));
		hmolInfo.setHmolType(buf.getShort());
		hmolInfo.setMaxApNum(buf.getInt());
		hmolInfo.setMaxVhmNum(buf.getInt());
		hmolInfo.setHmolStatus(buf.getShort());
		if (buf.hasRemaining()) {
			hmolInfo.setVirtualHostName(AhDecoder.getString(buf));
			hmolInfo.setHeartbeatTimeOutValue(buf.getInt());
		}
		return null;
	}

}