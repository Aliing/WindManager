package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.mo.HmolInfo;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class BeQueryHmolResultEvent extends BePortalHMPayloadResultEvent {

	private static final long	serialVersionUID	= 1L;

	private HmolInfo			info;

	public BeQueryHmolResultEvent() {
		super();
		payloadResultType = RESULTTYPE_QUERYHHM;
	}

	protected byte[] buildOperationResult() throws Exception {
		if (info == null) {
			throw new BeCommunicationEncodeException("HmolInfo is a necessary field!");
		}
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		buf.put(super.buildOperationResult());

		AhEncoder.putString(buf, info.getHmolVersion());
		buf.putShort(info.getHmolType());
		buf.putInt(info.getMaxApNum());
		buf.putInt(info.getMaxVhmNum());
		buf.putInt(info.getLeftApCount());
		buf.putInt(info.getLeftVhmCount());

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

			info = new HmolInfo();
			info.setHmolVersion(AhDecoder.getString(buf));
			info.setHmolType(buf.getShort());
			info.setMaxApNum(buf.getInt());
			info.setMaxVhmNum(buf.getInt());
			info.setLeftApCount(buf.getInt());
			info.setLeftVhmCount(buf.getInt());
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeQueryHmolResultEvent.parsePacket() catch exception", e);
		}
	}

	public HmolInfo getInfo() {
		return info;
	}

	public void setInfo(HmolInfo info) {
		this.info = info;
	}
}
