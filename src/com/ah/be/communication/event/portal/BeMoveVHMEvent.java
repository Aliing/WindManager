package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

/**
 * 
 *@filename		BeMoveVHMEvent.java
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
public class BeMoveVHMEvent extends BePortalHMPayloadEvent {

	private String			destIPAddr;

	private String			destVersion;

	private String			destLoginPasswd;

	private List<String>	vhmNameList;

	public BeMoveVHMEvent() {
		super();
		operationType = OPERATIONTYPE_VHM_MOVE;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		if (destIPAddr == null || destIPAddr.length() == 0) {
			throw new BeCommunicationEncodeException("destIPAddr is a necessary field!");
		}

		if (vhmNameList == null || vhmNameList.size() == 0) {
			throw new BeCommunicationEncodeException("vhmNameList is a necessary field!");
		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		AhEncoder.putString(buf, destIPAddr);
		AhEncoder.putString(buf, destVersion);
		AhEncoder.putString(buf, destLoginPasswd);

		buf.putInt(vhmNameList.size());
		for (String vhmName : vhmNameList) {
			AhEncoder.putString(buf, vhmName);
		}

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		destIPAddr = AhDecoder.getString(buf);
		destVersion = AhDecoder.getString(buf);
		destLoginPasswd = AhDecoder.getString(buf);

		int size = buf.getInt();
		vhmNameList = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			String vhmName = AhDecoder.getString(buf);
			vhmNameList.add(vhmName);
		}

		return null;
	}

	public String getDestIPAddr() {
		return destIPAddr;
	}

	public void setDestIPAddr(String destIPAddr) {
		this.destIPAddr = destIPAddr;
	}

	public String getDestVersion() {
		return destVersion;
	}

	public void setDestVersion(String destVersion) {
		this.destVersion = destVersion;
	}

	public String getDestLoginPasswd() {
		return destLoginPasswd;
	}

	public void setDestLoginPasswd(String destLoginPasswd) {
		this.destLoginPasswd = destLoginPasswd;
	}

	public List<String> getVhmNameList() {
		return vhmNameList;
	}

	public void setVhmNameList(List<String> vhmNameList) {
		this.vhmNameList = vhmNameList;
	}
}
