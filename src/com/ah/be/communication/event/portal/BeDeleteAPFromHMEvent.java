package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.sn.operation.Ap;
import com.ah.be.sn.operation.ApDeleteRequest;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class BeDeleteAPFromHMEvent extends BePortalHMPayloadEvent {
	private static final long serialVersionUID = -4433475735162441542L;

	private ApDeleteRequest apDeleteRequest = new ApDeleteRequest(new Ap());

	public BeDeleteAPFromHMEvent() {
		super();
		operationType = OPERATIONTYPE_HM_DELETE_AP;
	}
	
	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		
		AhEncoder.putString(buf, apDeleteRequest.getAp().getSerialNumber());
		
		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}
	
	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length
		
		apDeleteRequest.getAp().setSerialNumber(AhDecoder.getString(buf));
		
		return null;
	}

	public ApDeleteRequest getApDeleteRequest() {
		return apDeleteRequest;
	}

	public void setApDeleteRequest(ApDeleteRequest apDeleteRequest) {
		this.apDeleteRequest = apDeleteRequest;
	}
	
}
