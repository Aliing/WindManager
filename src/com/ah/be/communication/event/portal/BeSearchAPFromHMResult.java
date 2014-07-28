package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.sn.operation.Ap;
import com.ah.be.sn.operation.ApSearchResponse;
import com.ah.be.sn.operation.Status;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class BeSearchAPFromHMResult extends BePortalHMPayloadResultEvent {
	private static final long serialVersionUID = -8971316836371627902L;

	private List<ApSearchResponse> apSearchResponseLst = new ArrayList<ApSearchResponse>();
	
	public BeSearchAPFromHMResult() {
		super();
		payloadResultType = RESULTTYPE_HM_SEARCH_AP;
	}
	
	protected byte[] buildOperationResult() throws Exception {
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		buf.put(super.buildOperationResult());
		
		buf.putInt(apSearchResponseLst.size());
		for (ApSearchResponse apSearchResponse : apSearchResponseLst) {
			AhEncoder.putString(buf, apSearchResponse.getAp().getSerialNumber());
			buf.put(apSearchResponse.getStatus().isSuccess()?(byte)1:(byte)0);
			AhEncoder.putString(buf, apSearchResponse.getAp().getVhmId());
			buf.putLong(apSearchResponse.getAp().getInternalId());
			AhEncoder.putString(buf, apSearchResponse.getStatus().getErrorMsg());
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
				ApSearchResponse apSearchResponse = new ApSearchResponse(new Ap());
				if (apSearchResponse.getStatus() == null) {
					apSearchResponse.setStatus(new Status());
				}
				apSearchResponse.getAp().setSerialNumber(AhDecoder.getString(buf));
				apSearchResponse.getStatus().setSuccess(buf.get()==(byte)1?true:false);
				apSearchResponse.getAp().setVhmId(AhDecoder.getString(buf));
				apSearchResponse.getAp().setInternalId(buf.getLong());
				apSearchResponse.getStatus().setErrorMsg(AhDecoder.getString(buf));
				apSearchResponseLst.add(apSearchResponse);
			}

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeSearchAPFromHMResult parsePacket() catch exception", e);
		}
	}

	public List<ApSearchResponse> getApSearchResponseLst() {
		return apSearchResponseLst;
	}

	public void setApSearchResponseLst(List<ApSearchResponse> apSearchResponseLst) {
		this.apSearchResponseLst = apSearchResponseLst;
	}

}
