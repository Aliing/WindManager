package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeRemoveVHMEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-1-25 03:38:51
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeRemoveVHMEvent extends BePortalHMPayloadEvent {

	private String	vhmName;

	public BeRemoveVHMEvent() {
		super();
		operationType = OPERATIONTYPE_VHM_REMOVE;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		if (vhmName == null || vhmName.length() == 0) {
			throw new BeCommunicationEncodeException("vhmName is a necessary field!");
		}
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		buf.putShort((short) vhmName.length());
		buf.put(vhmName.getBytes());

		buf.flip();
		byte[] array = new byte[buf.limit()];
		buf.get(array);
		
		return array;
	}
	
	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length
		
		short len = buf.getShort();
		vhmName = AhDecoder.bytes2String(buf, len);
		
		return null;
	}


	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		this.vhmName = vhmName;
	}
}
