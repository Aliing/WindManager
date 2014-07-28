package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

@SuppressWarnings("serial")
public class BeHAEnableEvent extends BePortalHMPayloadEvent {

	private String	secondaryHostname;
	
	private String secondaryIP;

	private String	haSecret;

	private boolean	useExternalIPHostname;

	private String	primaryExternalIPHostname;

	private String	secondaryExternalIPHostname;

	private boolean	enableFailBack;

	private int heartbeatTimeOutValue;

	public BeHAEnableEvent() {
		super();
		operationType = OPERATIONTYPE_HA_ENABLE;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		AhEncoder.putString(buf, secondaryHostname);
		AhEncoder.putString(buf, haSecret);

		buf.put(useExternalIPHostname ? (byte) 1 : (byte) 0);

		AhEncoder.putString(buf, primaryExternalIPHostname);
		AhEncoder.putString(buf, secondaryExternalIPHostname);

		buf.put(enableFailBack ? (byte) 1 : (byte) 0);
		
		AhEncoder.putString(buf, secondaryIP);

		buf.putInt(heartbeatTimeOutValue);

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		secondaryHostname = AhDecoder.getString(buf);
		haSecret = AhDecoder.getString(buf);

		useExternalIPHostname = buf.get() == 1;

		primaryExternalIPHostname = AhDecoder.getString(buf);
		secondaryExternalIPHostname = AhDecoder.getString(buf);

		enableFailBack = buf.get() == 1;
		
		secondaryIP = AhDecoder.getString(buf);

		if (buf.hasRemaining()) {
			heartbeatTimeOutValue = buf.getInt();
		}

		return null;
	}
	
	public String getSecondaryHostname() {
		return secondaryHostname;
	}

	public void setSecondaryHostname(String secondaryHostname) {
		this.secondaryHostname = secondaryHostname;
	}

	public String getHaSecret() {
		return haSecret;
	}

	public void setHaSecret(String haSecret) {
		this.haSecret = haSecret;
	}

	public boolean isUseExternalIPHostname() {
		return useExternalIPHostname;
	}

	public void setUseExternalIPHostname(boolean useExternalIPHostname) {
		this.useExternalIPHostname = useExternalIPHostname;
	}

	public String getPrimaryExternalIPHostname() {
		return primaryExternalIPHostname;
	}

	public void setPrimaryExternalIPHostname(String primaryExternalIPHostname) {
		this.primaryExternalIPHostname = primaryExternalIPHostname;
	}

	public String getSecondaryExternalIPHostname() {
		return secondaryExternalIPHostname;
	}

	public void setSecondaryExternalIPHostname(String secondaryExternalIPHostname) {
		this.secondaryExternalIPHostname = secondaryExternalIPHostname;
	}

	public boolean isEnableFailBack() {
		return enableFailBack;
	}

	public void setEnableFailBack(boolean enableFailBack) {
		this.enableFailBack = enableFailBack;
	}
	
	public String getSecondaryIP() {
		return secondaryIP;
	}

	public void setSecondaryIP(String secondaryIP) {
		this.secondaryIP = secondaryIP;
	}

	public int getHeartbeatTimeOutValue() {
		return heartbeatTimeOutValue;
}
	public void setHeartbeatTimeOutValue(int heartbeatTimeOutValue) {
		this.heartbeatTimeOutValue = heartbeatTimeOutValue;
	}
}
