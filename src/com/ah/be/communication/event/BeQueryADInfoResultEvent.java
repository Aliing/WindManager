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
public class BeQueryADInfoResultEvent extends BeCapwapClientResultEvent {
	// 0: success -1: failure
	public static final byte	RESULTCODE_SUCCESS	= 0;
	public static final byte	RESULTCODE_FAILURE	= -1;
	
	// 0: success -1: failure
	public static final byte	QUERYRESULT_SUCCESS	= 0;
	public static final byte	QUERYRESULT_FAILURE	= -1;
	
	private byte					resultCode;
	
	private String					message = "";
	
	private byte					queryResult;
	
	private String 					domain = "";	// domain name
	
	/**
	 * 
	 */
	public BeQueryADInfoResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_QUERYADINFO;
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
			
			if (resultCode == RESULTCODE_SUCCESS) {
				buf.getShort();
				
				queryResult = buf.get();
				
				if (queryResult == QUERYRESULT_SUCCESS) {
					byte len = buf.get();
					domain = AhDecoder.bytes2String(buf, len);
				}
			} else {
				message = AhDecoder.getString(buf);
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeQueryADInfoResultEvent.parsePacket() catch exception", e);
		}
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
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

	public byte getQueryResult() {
		return queryResult;
	}

	public void setQueryResult(byte queryResult) {
		this.queryResult = queryResult;
	}
}
