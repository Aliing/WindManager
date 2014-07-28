/**
 * Retrieve the LDAP info event
 */
package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;

/**
 * @author cchen
 * 
 */
@SuppressWarnings("serial")
public class BeRetrieveLDAPInfoEvent extends BeCapwapClientEvent {

	private 	String			fullName = "";	// full name
	
	public BeRetrieveLDAPInfoEvent() {
		super();
		queryType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RETRIEVELDAPINFO;
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
			/**
			 * AP identifier 's length = 6 + 1 + apMac.length()<br>
			 * query's length = 6 + 11 + (1 + fullName.length())
			 */
			int apIdentifierLen = 6 + 1 + apMac.length();
			int queryLen = 6 + 11 + (1 + fullName.length());
			ByteBuffer buf = ByteBuffer.allocate(apIdentifierLen + queryLen);
			
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(1 + apMac.length());
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTQUERY);
			buf.putInt(11 + (1 + fullName.length()));
			buf.putShort(queryType);
			buf.putInt(sequenceNum);
			buf.put(flag);
			buf.putInt(fullName.length() + 1);
			
			// set event data
			buf.put((byte) fullName.length());
			buf.put(fullName.getBytes());
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException("BeRetrieveLDAPInfoEvent.buildPacket() catch exception", e);
		}
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
