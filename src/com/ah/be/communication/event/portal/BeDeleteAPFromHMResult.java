package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.sn.operation.Ap;
import com.ah.be.sn.operation.ApDeleteResponse;
import com.ah.be.sn.operation.Status;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class BeDeleteAPFromHMResult extends BePortalHMPayloadResultEvent {
	private static final long serialVersionUID = -8971316836371627902L;

	private List<ApDeleteResponse> apDeleteResponseLst = new ArrayList<ApDeleteResponse>();
	
	public BeDeleteAPFromHMResult() {
		super();
		payloadResultType = RESULTTYPE_HM_DELETE_AP;
	}
	
	protected byte[] buildOperationResult() throws Exception {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		buf.put(super.buildOperationResult());
		
		buf.putInt(apDeleteResponseLst.size());
		for (ApDeleteResponse apDeleteResponse : apDeleteResponseLst) {
			AhEncoder.putString(buf, apDeleteResponse.getAp().getSerialNumber());
			buf.put(apDeleteResponse.getStatus().isSuccess()?(byte)1:(byte)0);
			AhEncoder.putString(buf, apDeleteResponse.getAp().getVhmId());
			buf.putLong(apDeleteResponse.getAp().getInternalId());
			AhEncoder.putString(buf, apDeleteResponse.getStatus().getErrorMsg());
		}
		
		buf.flip();
		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}
	
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);

			ByteBuffer buf = ByteBuffer.wrap(getResultData());

			int recordCount = buf.getInt();
			for (int i = 0; i < recordCount; i++) {
				ApDeleteResponse apDeleteResponse = new ApDeleteResponse(new Ap());
				if (apDeleteResponse.getStatus() == null) {
					apDeleteResponse.setStatus(new Status());
				}
				apDeleteResponse.getAp().setSerialNumber(AhDecoder.getString(buf));
				apDeleteResponse.getStatus().setSuccess(buf.get()==(byte)1?true:false);
				apDeleteResponse.getAp().setVhmId(AhDecoder.getString(buf));
				apDeleteResponse.getAp().setInternalId(buf.getLong());
				apDeleteResponse.getStatus().setErrorMsg(AhDecoder.getString(buf));
				apDeleteResponseLst.add(apDeleteResponse);
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeDeleteAPFromHMResult parsePacket() catch exception", e);
		}
	}

	public List<ApDeleteResponse> getApDeleteResponseLst() {
		return apDeleteResponseLst;
	}

	public void setApDeleteResponseLst(List<ApDeleteResponse> apDeleteResponseLst) {
		this.apDeleteResponseLst = apDeleteResponseLst;
	}
	
}
