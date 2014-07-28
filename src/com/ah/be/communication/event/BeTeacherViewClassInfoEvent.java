package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;

/**
 * 
 *@filename		BeTeacherViewClassInfoEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-5-27 02:16:15
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
@SuppressWarnings("serial")
public class BeTeacherViewClassInfoEvent extends BeCapwapClientInfoEvent {

	public static final Byte FLAG_REMOVE = 1;
	
	private List<String> remove_classIDList;
	
	public BeTeacherViewClassInfoEvent() {
		super();
		queryType = BeCapwapClientInfoEvent.TYPE_TEACHERVIEW_CLASSINFO;
	}
	
	/**
	 * build event data to packet message
	 * 
	 * @return BeCommunicationMessageData
	 * @throws BeCommunicationEncodeException -
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		if (apMac == null) {
			throw new BeCommunicationEncodeException("ApMac is a necessary field!");
		}

		try {
			byte[] requestData = prepareRequestData();
			
			/**
			 * AP identifier 's length = 6 + 1 + apSerialNum.length()<br>
			 * query's length = 6 + 12
			 */
			int apIdentifierLen = 7 + apMac.length();
			int queryLen = 12 + requestData.length;
			int bufLength = apIdentifierLen + queryLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_INFORMATIONQUERY);
			buf.putInt(6 + requestData.length);
			buf.putShort(queryType);
			buf.putInt(requestData.length); // data length
			buf.put(requestData);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeTeacherViewClassInfoEvent.buildPacket() catch exception", e);
		}
	}
	
	private byte[] prepareRequestData() throws Exception
	{
		if (remove_classIDList == null) {
			throw new BeCommunicationEncodeException("Invalid argument.");
		}
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		buf.put((byte)remove_classIDList.size());
		for (String classID : remove_classIDList) {
			buf.put((byte)classID.length());
			buf.put(classID.getBytes());
			buf.put(FLAG_REMOVE);
		}
		
		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 * @throws BeCommunicationDecodeException -
	 */
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		super.parsePacket(data);
		
		// it's infoData is empty
	}
	
	public List<String> getRemove_classIDList() {
		return remove_classIDList;
	}

	public void setRemove_classIDList(List<String> removeClassIDList) {
		remove_classIDList = removeClassIDList;
	}
}
