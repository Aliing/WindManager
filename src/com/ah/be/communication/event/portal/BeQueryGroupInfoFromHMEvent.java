/**
 *@filename		BeQueryGroupInfoFromHMEvent.java
 *@version
 *@author		Fiona
 *@createtime	Aug 16, 2012 3:52:41 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class BeQueryGroupInfoFromHMEvent extends BePortalHMPayloadEvent {
	private static final long	serialVersionUID	= 1L;

	private String				vhmId;

	public BeQueryGroupInfoFromHMEvent() {
		super();
		operationType = OPERATIONTYPE_VHM_GET_USER_GROUPS;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		if (vhmId == null) {
			throw new BeCommunicationEncodeException("vhmId is a necessary field!");
		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		AhEncoder.putString(buf, vhmId);

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		vhmId = AhDecoder.getString(buf);

		return null;
	}

	public String getVhmId() {
		return vhmId;
	}

	public void setVhmId(String vhmId) {
		this.vhmId = vhmId;
	}

}
