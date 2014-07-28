package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

@SuppressWarnings("serial")
public class BeHAJoinEvent extends BePortalHMPayloadEvent {

	private String	primaryIP;

	private String	secret;

	// [host][port][db][username][password]
	private String[]			dbSettings = new String[5];

	private int heartbeatTimeOutValue;

	public BeHAJoinEvent() {
		super();
		operationType = OPERATIONTYPE_HA_JOIN;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {

		if (primaryIP == null || primaryIP.length() == 0) {
			throw new BeCommunicationEncodeException("primaryIP is a necessary field!");
		}

		if (secret == null || secret.length() == 0) {
			throw new BeCommunicationEncodeException("secret is a necessary field!");
		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		AhEncoder.putString(buf, primaryIP);
		AhEncoder.putString(buf, secret);
		AhEncoder.putString(buf, dbSettings[0]);
		AhEncoder.putString(buf, dbSettings[1]);
		AhEncoder.putString(buf, dbSettings[2]);
		AhEncoder.putString(buf, dbSettings[3]);
		AhEncoder.putString(buf, dbSettings[4]);

		buf.putInt(heartbeatTimeOutValue);

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		primaryIP = AhDecoder.getString(buf);
		secret = AhDecoder.getString(buf);
		if(buf.hasRemaining()) {
			dbSettings[0] = AhDecoder.getString(buf);
			dbSettings[1] = AhDecoder.getString(buf);
			dbSettings[2] = AhDecoder.getString(buf);
			dbSettings[3] = AhDecoder.getString(buf);
			dbSettings[4] = AhDecoder.getString(buf);
			heartbeatTimeOutValue = buf.getInt();
		}
		return null;
	}

	public String getPrimaryIP() {
		return primaryIP;
	}

	public void setPrimaryIP(String primaryIP) {
		this.primaryIP = primaryIP;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String[] getDbSettings() {
		return dbSettings;
	}

	public void setDbSettings(String[] dbSettings) {
		this.dbSettings = dbSettings;
	}

	public int getHeartbeatTimeOutValue() {
		return heartbeatTimeOutValue;
	}

	public void setHeartbeatTimeOutValue(int heartbeatTimeOutValue) {
		this.heartbeatTimeOutValue = heartbeatTimeOutValue;
	}
}
