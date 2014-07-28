package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.app.DebugUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.bo.teacherView.TvClass;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeTeacherViewStudentNotFoundEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-5-26 01:29:15
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 * 2013/01/06 Modified by HeHui
 */
@SuppressWarnings("serial")
public class BeTeacherViewStudentNotFoundEvent extends BeCapwapClientResultEvent {

	private String classID;
	
	private byte classTypeByte;
	
	private int  msgVersion;
	
	private int  classType;
	
	private Map<String,Integer> studentList;
	
	public BeTeacherViewStudentNotFoundEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_TEACHERVIEW_STUDENTNOTFOUND;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);

			byte len = buf.get();
			classID = AhDecoder.bytes2String(buf, len);
			classTypeByte = buf.get();
			msgVersion = classTypeByte >> 4;
			classType = classTypeByte << 28 >>28;
			short count = buf.getShort();
			studentList = new HashMap<String, Integer>(count);
			if(msgVersion == TvClass.TV_MSG_VERSION_BASE){
				for (int i = 0; i < count; i++) {
					len = buf.get();			
					if(classType == TvClass.TV_ROSTER_TYPE_STUDENT){
						studentList.put(AhDecoder.bytes2String(buf, len), TvClass.TV_STUNAME_TYPE_ID);
					}else if(classType == TvClass.TV_ROSTER_TYPE_COMPUTERCART){
						studentList.put(AhDecoder.bytes2hex(buf, len),TvClass.TV_STUNAME_TYPE_MACADDRESS);	
					}else{
						DebugUtil.commonDebugError("The class type is error, class type " + classType);
					}				
				}
			}else if(msgVersion == TvClass.TV_MSG_VERSION_MIXEDTYPE){
				for (int i = 0; i < count; i++) {
					byte itemLength = buf.get();
					int start = buf.position();
					byte studentType = buf.get();
					byte stuIdLength = buf.get();
					
					if ((buf.position() - start) < itemLength) {
						if(studentType == TvClass.TV_STUNAME_TYPE_ID){
							studentList.put(AhDecoder.bytes2String(buf, stuIdLength),TvClass.TV_STUNAME_TYPE_ID);
						}else if(studentType == TvClass.TV_STUNAME_TYPE_MACADDRESS){
							studentList.put(AhDecoder.bytes2hex(buf, stuIdLength),TvClass.TV_STUNAME_TYPE_MACADDRESS);	
						}	
					}

					buf.position(start+itemLength);
				}
			}else{
				DebugUtil.commonDebugError("The message version is error, version number " + msgVersion);
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeTeacherViewStudentNotFoundEvent.parsePacket() catch exception", e);
		}
	}
	
	public String getClassID() {
		return classID;
	}

	public void setClassID(String classID) {
		this.classID = classID;
	}

	public Map<String, Integer> getStudentList() {
		return studentList;
	}

	public void setStudentList(Map<String, Integer> studentList) {
		this.studentList = studentList;
	}

	public void setClassType(int classType) {
		this.classType = classType;
	}

	public int getClassType() {
		return classType;
	}

	public void setClassType(byte classType) {
		this.classType = classType;
	}

	public int getMsgVersion() {
		return msgVersion;
	}

	public void setMsgVersion(int msgVersion) {
		this.msgVersion = msgVersion;
	}

	public byte getClassTypeByte() {
		return classTypeByte;
	}

	public void setClassTypeByte(byte classTypeByte) {
		this.classTypeByte = classTypeByte;
	}
}
