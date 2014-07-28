package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.mo.CredentialInfo;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class BeSendCredentialEvent extends BePortalHMPayloadEvent {

	private static final long	serialVersionUID	= 1L;

	public BeSendCredentialEvent() {
		super();
		operationType = OPERATIONTYPE_SEND_CREDENTIAL;
	}

	private CredentialInfo	credInfo;

	public CredentialInfo getCredInfo() {
		return credInfo;
	}

	public void setCredInfo(CredentialInfo credInfo) {
		this.credInfo = credInfo;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		AhEncoder.putString(buf, credInfo.getVhmName());
		AhEncoder.putString(buf, credInfo.getVhmId());
		AhEncoder.putString(buf, credInfo.getUserName());
		AhEncoder.putString(buf, credInfo.getDnsUrl());
		AhEncoder.putString(buf, credInfo.getClearPassword());
		buf.putShort(credInfo.getVhmType());
		buf.putInt(credInfo.getValidDays());

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		credInfo = new CredentialInfo();
		credInfo.setVhmName(AhDecoder.getString(buf));
		credInfo.setVhmId(AhDecoder.getString(buf));
		credInfo.setUserName(AhDecoder.getString(buf));
		credInfo.setDnsUrl(AhDecoder.getString(buf));
		credInfo.setClearPassword(AhDecoder.getString(buf));
		credInfo.setVhmType(buf.getShort());
		credInfo.setValidDays(buf.getInt());

		return null;
	}
}
