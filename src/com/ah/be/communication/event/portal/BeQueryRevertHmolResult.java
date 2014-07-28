package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.mo.HmolInfo;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class BeQueryRevertHmolResult extends BePortalHMPayloadResultEvent {
	private static final long	serialVersionUID	= 1L;

	private HmolInfo			destHmol;

	public BeQueryRevertHmolResult() {
		super();
		payloadResultType = RESULTTYPE_QUERY_VHMREVERT_DESTHMOL;
	}

	protected byte[] buildOperationResult() throws Exception {
//		if (destHmol == null) {
//			throw new BeCommunicationEncodeException("destHmol is a necessary field!");
//		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		buf.put(super.buildOperationResult());

		if (destHmol == null) {
			AhEncoder.putString(buf, "");
			AhEncoder.putString(buf, "");
		} else {
			AhEncoder.putString(buf, destHmol.getHmolVersion());
			AhEncoder.putString(buf, destHmol.getHmolIpAddress());
		}

		buf.flip();
		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
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

			ByteBuffer buf = ByteBuffer.wrap(getResultData());

			destHmol = new HmolInfo();
			destHmol.setHmolVersion(AhDecoder.getString(buf));
			destHmol.setHmolIpAddress(AhDecoder.getString(buf));
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeVhmUpgradeResultEvent.parsePacket() catch exception", e);
		}
	}

	public HmolInfo getDestHmol() {
		return destHmol;
	}

	public void setDestHmol(HmolInfo destHmol) {
		this.destHmol = destHmol;
	}


}
