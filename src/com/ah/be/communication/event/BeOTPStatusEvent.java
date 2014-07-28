package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhEncoder;

@SuppressWarnings("serial")
public class BeOTPStatusEvent  extends BeCapwapClientEvent{
	
	public static final	byte		OTP_MODE_QUERY	= 0;
	public static final	byte		OTP_MODE_CORRECT	= 1;
	public static final	byte		OTP_MODE_WRONG= 2;
	
	private byte mode;
	private String password;

	public BeOTPStatusEvent(){
		super();
		queryType=BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_OTP;
	}
	
	/**
	 * build event data to packet message
	 * 
	 * @return BeCommunicationMessageData
	 * @throws BeCommunicationEncodeException -
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		if (apMac == null) {
			throw new BeCommunicationEncodeException("ApMac is a necessary field!");
		}

		if (sequenceNum <= 0) {
			throw new BeCommunicationEncodeException("sequenceNum is a necessary field!");
		}

		try {
			/**
			 * AP identifier 's length = 6 + 1 + apSerialNum.length()<br>
			 * query's length = 6 + 11 + 1 + 1 + 12
			 */
			byte lengthOfPassword = 0;
			if(null != password && !"".equals(password)){
				lengthOfPassword = (byte)password.length();
			}
			int apIdentifierLen = 7 + apMac.length();
			int queryLen = 17+1+1+lengthOfPassword;
			int bufLength = apIdentifierLen + queryLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTQUERY);
			buf.putInt(7+4+1+1+lengthOfPassword); 
			buf.putShort(queryType);
			buf.putInt(sequenceNum);
			buf.put(flag);
			buf.putInt(1+1+lengthOfPassword); // data length
			buf.put(mode);
			buf.put(lengthOfPassword);
			
			if(null != password && !"".equals(password)){
				buf.put(password.getBytes());
			}
			
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeClientMonitoringEvent.buildPacket() catch exception", e);
		}
	}

	 /**
     * parse packet message to event data
     * 
     * @param data -
     * @throws BeCommunicationDecodeException -
     */
    protected void parsePacket(byte[] data)
            throws BeCommunicationDecodeException {
        super.parsePacket(data);
    }

	public byte getMode() {
		return mode;
	}

	public void setMode(byte mode) {
		this.mode = mode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
