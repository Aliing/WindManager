package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

/**
 * 
 *@filename		BeUpdateApplicationInfoEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-3-1 11:28:52
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
@SuppressWarnings("serial")
public class BeUpdateApplicationInfoEvent extends BePortalHMPayloadEvent {

	private int		apCount;

	private int		vhmCount;
	
	private String 	vhmName;
	
	public BeUpdateApplicationInfoEvent() {
		super();
		operationType = OPERATIONTYPE_UPDATE_APPLICATION_INFO;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		buf.putInt(apCount);
		buf.putInt(vhmCount);
		AhEncoder.putString(buf, vhmName);

		buf.flip();
		byte[] array = new byte[buf.limit()];
		buf.get(array);
		
		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		apCount = buf.getInt();
		vhmCount = buf.getInt();
		vhmName = AhDecoder.getString(buf);

		return null;
	}

	public int getApCount() {
		return apCount;
	}

	public void setApCount(int apCount) {
		this.apCount = apCount;
	}

	public int getVhmCount() {
		return vhmCount;
	}

	public void setVhmCount(int vhmCount) {
		this.vhmCount = vhmCount;
	}
	
	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		this.vhmName = vhmName;
	}
}
