/**
 * Search the LDAP tree info result event
 */
package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.mo.LDAPTreeInfo;
import com.ah.util.coder.AhDecoder;

/**
 * @author cchen
 *
 */
@SuppressWarnings("serial")
public class BeLDAPTreeInfoResultEvent extends BeCapwapClientResultEvent {
	// 0: success -1: failure
	public static final byte	RESULTCODE_SUCCESS	= 0;
	public static final byte	RESULTCODE_FAILURE	= -1;

	private byte				resultCode;
	
	private String				message = "";
	
	private LDAPTreeInfo 		treeInfo;
	
	public BeLDAPTreeInfoResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LDAPTREEINFO;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @see com.ah.be.communication.event.BeCapwapClientResultEvent#parsePacket(byte[])
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);

			resultCode = buf.get();
			
			// parse one-level tree info
			if (resultCode == RESULTCODE_SUCCESS){
				message = AhDecoder.getString(buf);
				parseLDAPtreeInfo(buf);
			} else {
				message = AhDecoder.getString(buf);
				treeInfo = null;
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeLDAPTreeInfoResultEvent.parsePacket() catch exception", e);
		}
	}
	
	private void parseLDAPtreeInfo(ByteBuffer buf) throws BeCommunicationDecodeException {
		treeInfo = new LDAPTreeInfo();
		// Number of DN
		short dns = buf.getShort();
		byte len = 0;
		short sLenCount = 0;
		short sLen = 0;
		
		List<String> rdnList  = treeInfo.getrDns();
		while(buf.hasRemaining()){
			// Length of RDN
			len = buf.get();
			// RDN
			String rdn = AhDecoder.bytes2String(buf, len);
			rdnList.add(rdn);
			// Number of attributes
			sLenCount = buf.getShort();
			
			for(short i = 0; i < sLenCount; i++) {
				// Length of attribute name
				sLen = buf.getShort();
				
				// Attribute name
				String name = AhDecoder.bytes2String(buf, sLen);
				
				// Length of attribute value
				sLen = buf.getShort();
				
				// Attribute values
				String values = AhDecoder.bytes2String(buf, sLen);
				
				treeInfo.setAttrNameValue(rdn, name, values);
			}
		}
		
		if (dns != rdnList.size()){
			throw new BeCommunicationDecodeException("BeLDAPTreeInfoResultEvent.parseLDAPtreeInfo format error");
		}
	}

	public byte getResultCode() {
		return resultCode;
	}

	public void setResultCode(byte resultCode) {
		this.resultCode = resultCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LDAPTreeInfo getTreeInfo() {
		return treeInfo;
	}

	public void setTreeInfo(LDAPTreeInfo treeInfo) {
		this.treeInfo = treeInfo;
	}
}
