/**
 * 
 */
package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;

/**
 * @author cchen
 *
 */
@SuppressWarnings("serial")
public class BeRetrieveLDAPInfoResultEvent extends BeCapwapClientResultEvent {
	// 0: success -1: failure
	public static final int		RESULTCODE_SUCCESS	= 0;
	public static final int		RESULTCODE_FAILURE	= -1;
	
	private int						resultCode;
	
	private String					message = "";
	
	private String 					realm = "";		// Full name 
	
	private String 					domain = "";	// name
	
	private String 					serverIp = "";	// LDAP Server ip
	
	private String 					baseDn = "";	// Bind path
	
	/**
	 * 
	 */
	public BeRetrieveLDAPInfoResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RETRIEVELDAPINFO;
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
			
			resultCode = buf.getInt();
			
			if (resultCode == RESULTCODE_SUCCESS) {
				buf.getShort();
				
				byte len = buf.get();
				realm = AhDecoder.bytes2String(buf, len);

				len = buf.get();
				domain = AhDecoder.bytes2String(buf, len);

				len = buf.get();
				serverIp = AhDecoder.bytes2String(buf, len);

				len = buf.get();
				baseDn = AhDecoder.bytes2String(buf, len);
			} else {
				message = AhDecoder.getString(buf);
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeRetrieveLDAPInfoResultEvent.parsePacket() catch exception", e);
		}
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
