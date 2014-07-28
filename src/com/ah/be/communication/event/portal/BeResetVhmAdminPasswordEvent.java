package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class BeResetVhmAdminPasswordEvent extends BePortalHMPayloadEvent {

	private static final long	serialVersionUID	= 1L;

	public BeResetVhmAdminPasswordEvent() {
		super();
		operationType = OPERATIONTYPE_RESET_VHM_ADMIN_PASSWORD;
	}

	private String	vhmName;

	private String	clearPassword;

	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		this.vhmName = vhmName;
	}

	public String getClearPassword() {
		return clearPassword;
	}

	public void setClearPassword(String clearPassword) {
		this.clearPassword = clearPassword;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		AhEncoder.putString(buf, vhmName);
		AhEncoder.putString(buf, clearPassword);

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		this.vhmName = AhDecoder.getString(buf);
		this.clearPassword = AhDecoder.getString(buf);

		return null;
	}
}
