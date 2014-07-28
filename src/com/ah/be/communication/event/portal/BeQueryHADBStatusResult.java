package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

@SuppressWarnings("serial")
public class BeQueryHADBStatusResult extends BePortalHMPayloadResultEvent {

	// [host][port][db][username][password]
	private String[]			dbSettings = new String[5];

	public BeQueryHADBStatusResult() {
		super();
		payloadResultType = RESULTTYPE_QUERY_HADBSTATUS;
	}

	@Override
	protected byte[] buildOperationResult() throws Exception {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		buf.put(super.buildOperationResult());

		AhEncoder.putString(buf, dbSettings[0]);
		AhEncoder.putString(buf, dbSettings[1]);
		AhEncoder.putString(buf, dbSettings[2]);
		AhEncoder.putString(buf, dbSettings[3]);
		AhEncoder.putString(buf, dbSettings[4]);

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

			dbSettings[0] = AhDecoder.getString(buf);
			dbSettings[1] = AhDecoder.getString(buf);
			dbSettings[2] = AhDecoder.getString(buf);
			dbSettings[3] = AhDecoder.getString(buf);
			dbSettings[4] = AhDecoder.getString(buf);
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeQueryHAStatusResult parsePacket() catch exception", e);
		}
	}

	public String[] getDbSettings() {
		return dbSettings;
	}

	public void setDbSettings(String[] dbSettings) {
		this.dbSettings = dbSettings;
	}
}
