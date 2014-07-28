package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.sn.operation.Ap;
import com.ah.be.sn.operation.ApSearchRequest;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class BeSearchAPFromHMEvent extends BePortalHMPayloadEvent {
	private static final long serialVersionUID = -4433475735162441542L;

	private ApSearchRequest apSearchRequest;

	public BeSearchAPFromHMEvent() {
		super();
		operationType = OPERATIONTYPE_HM_SEARCH_AP;
	}
	
	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		
		AhEncoder.putString(buf, apSearchRequest.getAp().getSerialNumber());
		
		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}
	
	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length
		
		apSearchRequest = new ApSearchRequest(new Ap());
		apSearchRequest.getAp().setSerialNumber(AhDecoder.getString(buf));
		
		return null;
	}

	public ApSearchRequest getApSearchRequest() {
		return apSearchRequest;
	}

	public void setApSearchRequest(ApSearchRequest apSearchRequest) {
		this.apSearchRequest = apSearchRequest;
	}
	
}
