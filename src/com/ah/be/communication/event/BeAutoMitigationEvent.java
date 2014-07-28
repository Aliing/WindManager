package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.mo.classifyap.ClassifyAps;

/**
 * used to hold data sent to DA or APs
 */
public class BeAutoMitigationEvent extends BeCapwapClientEvent {
	
	private static final long serialVersionUID = 1L;
	
	private List<ClassifyAps> classifyAps = new ArrayList<ClassifyAps>();
	
	public BeAutoMitigationEvent() {
		super();
		queryType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_AUTO_MITIGATION;
	}
	
	private int getRequestDataLength() {
		int apsLength = 0;
		for (ClassifyAps ap : classifyAps) {
			apsLength += ap.getLength();
		}
		return apsLength;
	}
	
	private byte[] getRequestData() throws BeCommunicationEncodeException {
		ByteBuffer buf = ByteBuffer.allocate(getRequestDataLength());
		
		for (ClassifyAps ap : classifyAps) {
			buf.put(ap.getBytesOfObject());
		}
	
		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}
	
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
			throw new BeCommunicationEncodeException("BeAutoMitigationEvent.buildPacket() catch exception", e);
		}
	}

	public List<ClassifyAps> getClassifyAps() {
		return classifyAps;
	}

	public void setClassifyAps(List<ClassifyAps> classifyAps) {
		this.classifyAps = classifyAps;
	}

}
