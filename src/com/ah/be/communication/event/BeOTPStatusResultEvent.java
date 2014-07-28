package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;

@SuppressWarnings("serial")
public class BeOTPStatusResultEvent extends BeCapwapClientResultEvent{
	
	private byte lengthOfPassword;
	private String password;
	
	public BeOTPStatusResultEvent(){
		super();
		resultType=BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_OTP;
	}
	
	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	@Override
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);
			
			lengthOfPassword = buf.get();
			password = AhDecoder.bytes2String(buf, lengthOfPassword);

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeVLANProbeResultEvent.parsePacket() catch exception", e);
		}
	}

	public byte getLengthOfPassword() {
		return lengthOfPassword;
	}

	public void setLengthOfPassword(byte lengthOfPassword) {
		this.lengthOfPassword = lengthOfPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
