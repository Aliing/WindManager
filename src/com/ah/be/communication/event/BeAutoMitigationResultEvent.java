package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;

public class BeAutoMitigationResultEvent extends BeCapwapClientResultEvent {

	private static final long serialVersionUID = 1L;
	
	public BeAutoMitigationResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_AUTO_MITIGATION;
	}

	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);
			
			/**
			 * only result status, 4 bytes, 0 stands for success, -1 stands for failure
			 */
			int resultMark = buf.getInt();
			if (resultMark == 0) {
				isSucc = true;
			} else {
				isSucc = false;
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeAutoMitigationResultEvent.parsePacket() catch exception", e);
		}
	}
	
	private boolean isSucc;

	public boolean isSucc() {
		return isSucc;
	}

	public void setSucc(boolean isSucc) {
		this.isSucc = isSucc;
	}
	
}
