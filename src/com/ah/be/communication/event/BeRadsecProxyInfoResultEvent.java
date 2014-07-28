package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;

public class BeRadsecProxyInfoResultEvent extends BeCapwapClientResultEvent {

	private static final long serialVersionUID = 1L;
	
	public static final byte OPERATION_PROXY_QUERY 		= 0;
	public static final byte OPERATION_PROXY_ADD 		= 1;
	public static final byte OPERATION_PROXY_REMOVE 	= 2;
	
	private Set<String> addProxy = new HashSet<String>();
	private Set<String> removeProxy = new HashSet<String>();
	
	public BeRadsecProxyInfoResultEvent(){
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RADSEC_PROXY_INFO;
	}
	
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try{
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);
			
			int itemCount = buf.getShort();
			for (int i = 0; i < itemCount; i++) {
				int itemLength = buf.getShort();
				int start = buf.position();
				byte operation = buf.get();
				String macAddress = AhDecoder.bytes2hex(buf, 6).toUpperCase();
				buf.position(start+itemLength);
				
				if(operation == OPERATION_PROXY_ADD){
					addProxy.add(macAddress);
				}else if(operation == OPERATION_PROXY_REMOVE){
					removeProxy.add(macAddress);
				}
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeRadsecProxyInfoResultEvent.parsePacket() catch exception", e);
		}
	}

	public Set<String> getAddProxy() {
		return addProxy;
	}

	public Set<String> getRemoveProxy() {
		return removeProxy;
	}
}
