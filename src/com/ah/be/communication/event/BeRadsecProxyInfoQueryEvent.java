package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhEncoder;

public class BeRadsecProxyInfoQueryEvent extends BeCapwapClientEvent {
	
	private static final long serialVersionUID = 1L;

	public BeRadsecProxyInfoQueryEvent(){
		super();
		queryType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RADSEC_PROXY_INFO;
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
			 * query's length = 6 + 11 + 11
			 */
			int apIdentifierLen = 7 + apMac.length();
			int queryLen = 17;
			int queryBodyLen = 2 + 2 + 1 +6;
			int bufLength = apIdentifierLen + queryLen + queryBodyLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTQUERY);
			buf.putInt(11+(2+2+1+6)); 
			buf.putShort(queryType);
			buf.putInt(sequenceNum);
			buf.put(flag);
			buf.putInt(queryBodyLen); // data length
			
			buf.putShort((short)1);
			buf.putShort((short)7);
			buf.put(BeRadsecProxyInfoResultEvent.OPERATION_PROXY_QUERY);
			buf.put(AhEncoder.hex2bytes(apMac));
			
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeLLDPCDPInfoEvent.buildPacket() catch exception", e);
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
}
