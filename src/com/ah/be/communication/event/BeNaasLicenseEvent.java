package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;

public class BeNaasLicenseEvent extends BeCapwapClientEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public static final	byte		NAAS_LICENSE_OPT_START	= 1;
	public static final	byte		NAAS_LICENSE_OPT_STOP	= 2;
	public static final	byte		NAAS_LICENSE_OPT_SYNC	= 3;
	public static final	byte		NAAS_LICENSE_OPT_UPDATE	= 4;
	public static final	byte		NAAS_LICENSE_OPT_GET	= 5;
	
	public static final byte		NAAS_LICENSE_CURRENT_COUNTER	= 1;
	public static final byte		NAAS_LICENSE_MAX_COUNTER		= 2;
	
	
	private byte					optType = NAAS_LICENSE_OPT_GET;
	
	private int						currentCounter = 0;
	private int						maxCounter = 0;
	
	public BeNaasLicenseEvent() {
		super();
		queryType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_NAAS_LICENSE;
	}

	/**
	 * get request data
	 *
	 * @param
	 *
	 * @return
	 */
	private byte[] getRequestData() throws BeCommunicationEncodeException {

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		buf.put(optType);
		buf.putShort((short)14);
		
		buf.put(NAAS_LICENSE_CURRENT_COUNTER);
		buf.putShort((short)4);
		buf.putInt(currentCounter);

		buf.put(NAAS_LICENSE_MAX_COUNTER);
		buf.putShort((short)4);
		buf.putInt(maxCounter);
		
		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}
	
	/**
	 * build event data to packet message
	 * 
	 * @see com.ah.be.communication.BeCommunicationEvent#buildPacket()
	 */
	@Override
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		if (apMac == null) {
			throw new BeCommunicationEncodeException("ApMac is a necessary field!");
		}

		if (sequenceNum <= 0) {
			throw new BeCommunicationEncodeException("sequenceNum is a necessary field!");
		}
		
		try {
			byte[] requestData = getRequestData();

			/**
			 * AP identifier 's length = 6 + 1 + apSerialNum.length()<br>
			 * query's length = 6 + 11 + requestData.length
			 */
			int apIdentifierLen = 7 + apMac.length();
			int queryLen = 17 + requestData.length;
			int bufLength = apIdentifierLen + queryLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTQUERY);
			buf.putInt(11 + requestData.length); // 2+4+1+4+reqestData.length
			buf.putShort(queryType);
			buf.putInt(sequenceNum);
			buf.put(flag);
			buf.putInt(requestData.length); // data length
			buf.put(requestData);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException("BeNaasLicenseEvent.buildPacket() catch exception", e);
		}
	}

	public byte getOptType() {
		return optType;
	}

	public void setOptType(byte optType) {
		this.optType = optType;
	}

	public int getCurrentCounter() {
		return currentCounter;
	}

	public void setCurrentCounter(int currentCounter) {
		this.currentCounter = currentCounter;
	}

	public int getMaxCounter() {
		return maxCounter;
	}

	public void setMaxCounter(int maxCounter) {
		this.maxCounter = maxCounter;
	}
}
