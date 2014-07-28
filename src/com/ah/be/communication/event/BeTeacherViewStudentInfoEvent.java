package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.bo.teacherView.TvClass;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

/**
 * 
 *@filename		BeTeacherViewStudentInfoEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-5-26 10:40:08
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
@SuppressWarnings("serial")
public class BeTeacherViewStudentInfoEvent extends BeCapwapClientInfoEvent {

	private String classID;
	
	private int classType;
	
	private int msgVersion;
	
	private byte classTypeByte;


	/**
	 * AP ip address - student name list
	 */
	private Map<String, List<Object[]>> apStuduentMap;
	

	public BeTeacherViewStudentInfoEvent() {
		super();
		queryType = BeCapwapClientInfoEvent.TYPE_TEACHERVIEW_STUDUENTINFO;
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
					"BeTeacherViewStudentInfoEvent.buildPacket() catch exception", e);
		}
	}
	
	private byte[] prepareRequestData() throws Exception
	{
		if (classID == null || apStuduentMap == null) {
			throw new BeCommunicationEncodeException("Invalid argument.");
		}
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		buf.put((byte)classID.length());
		buf.put(classID.getBytes());
		buf.put(classTypeByte);
		buf.put((byte)apStuduentMap.size());
		for (String ip : apStuduentMap.keySet()) {
			buf.putInt(AhEncoder.ip2Int(ip));
			List<Object[]> studentList = apStuduentMap.get(ip);
			studentList = (studentList == null) ? new ArrayList<Object[]>(0) : studentList;
			buf.putShort((short)studentList.size());
			if(msgVersion == TvClass.TV_MSG_VERSION_BASE){
				if(classType == TvClass.TV_ROSTER_TYPE_STUDENT){
					String name;
					for (Object[] stuInfo : studentList) {
						name = (String)stuInfo[0];
						buf.put((byte)name.length());
						buf.put(name.getBytes());
					}
				}else if(classType == TvClass.TV_ROSTER_TYPE_COMPUTERCART){
					String macAddress;
					for (Object[] stuInfo : studentList) {
						macAddress = (String)stuInfo[0];
						byte[] macBytes = AhEncoder.hex2bytes(macAddress);
						buf.put((byte)macBytes.length);
						buf.put(macBytes);
					}
				}
			}else if(msgVersion == TvClass.TV_MSG_VERSION_MIXEDTYPE){
				Integer stuType;
				String  stuIdentity;
				for (Object[] stuInfo : studentList) {					
					stuIdentity = (String)stuInfo[0];
					stuType = (Integer)stuInfo[1];	
					int stuItemLen = 2;
					if(stuType == TvClass.TV_STUNAME_TYPE_ID){
						stuItemLen += stuIdentity.length();
						buf.put((byte)stuItemLen);
						buf.put(stuType.byteValue());
						buf.put((byte)stuIdentity.length());
						buf.put(stuIdentity.getBytes());
					}else if(stuType == TvClass.TV_STUNAME_TYPE_MACADDRESS){
						byte[] macBytes = AhEncoder.hex2bytes(stuIdentity);
						stuItemLen += macBytes.length;
						buf.put((byte)stuItemLen);
						buf.put(stuType.byteValue());						
						buf.put((byte)macBytes.length);
						buf.put(macBytes);
					}
				}
			}


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
	
	public String getClassID() {
		return classID;
	}

	public void setClassID(String classID) {
		this.classID = classID;
	}

	public Map<String, List<Object[]>> getApStuduentMap() {
		return apStuduentMap;
	}

	public void setApStuduentMap(Map<String, List<Object[]>> apStuduentMap) {
		this.apStuduentMap = apStuduentMap;
	}

	public int getClassType() {
		return classType;
	}

	public void setClassType(int classType) {
		this.classType = classType;
	}

	public byte getClassTypeByte() {
		return classTypeByte;
	}

	public void setClassTypeByte(byte classTypeByte) {
		this.classTypeByte = classTypeByte;
	}

	public int getMsgVersion() {
		return msgVersion;
	}

	public void setMsgVersion(int msgVersion) {
		this.msgVersion = msgVersion;
	}
}
