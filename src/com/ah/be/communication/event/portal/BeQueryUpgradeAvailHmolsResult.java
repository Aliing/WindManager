package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

@SuppressWarnings("serial")
public class BeQueryUpgradeAvailHmolsResult extends BePortalHMPayloadResultEvent {

	private Map<String, String>	destHmols;

	public BeQueryUpgradeAvailHmolsResult() {
		super();
		payloadResultType = RESULTTYPE_VHMUPGRADE;
	}

	protected byte[] buildOperationResult() throws Exception {
		if (destHmols == null) {
			throw new BeCommunicationEncodeException("destHmols is a necessary field!");
		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		buf.put(super.buildOperationResult());

		buf.putInt(destHmols.size());

		for (String version : destHmols.keySet()) {
			String ip = destHmols.get(version);
			AhEncoder.putString(buf, version);
			AhEncoder.putString(buf, ip);
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

			destHmols = new HashMap<String, String>();
			int size = buf.getInt();

			for (int i = 0; i < size; i++) {
				destHmols.put(AhDecoder.getString(buf), AhDecoder.getString(buf));
			}

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeVhmUpgradeResultEvent.parsePacket() catch exception", e);
		}
	}

	public Map<String, String> getDestHmols() {
		return destHmols;
	}

	public void setDestHmols(Map<String, String> destHmols) {
		this.destHmols = destHmols;
	}

}
