package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;

/**
 *
 *@filename		BeCWPDirectoryResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-8-11 05:32:23
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 *
 */
@SuppressWarnings("serial")
public class BeCWPDirectoryResultEvent extends BeAPWTPEvent {

//	private int							cwpSerialNum;

	private Map<String, List<String>>	cwpDirectoryMap;

	public BeCWPDirectoryResultEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGEELEMENTTYPE_CWPDIRECTORYRESULT;
	}

	/**
	 * parse packet message to event data
	 *
	 * @param data -
	 */
	@Override
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);

			byte[] buffer = getWtpMsgData();
			ByteBuffer buf = ByteBuffer.wrap(buffer);

			cwpDirectoryMap = new HashMap<String, List<String>>();

			sequenceNum = buf.getInt();
			while (buf.hasRemaining()) {
				short cwpDirLen = buf.getShort();
				String dirName = AhDecoder.bytes2String(buf, AhDecoder.short2int(cwpDirLen));
				short ssidNumber = buf.getShort();
				List<String> ssidList = new ArrayList<String>(ssidNumber);
				for (int i = 1; i <= ssidNumber; i++) {
					byte ssidLen = buf.get();
					String ssidName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(ssidLen));
					ssidList.add(ssidName);
				}
				cwpDirectoryMap.put(dirName, ssidList);
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeCapwapCliResultEvent.parsePacket() catch exception", e);
		}
	}

//	public int getCwpSerialNum() {
//		return cwpSerialNum;
//	}
//
//	public void setCwpSerialNum(int cwpSerialNum) {
//		this.cwpSerialNum = cwpSerialNum;
//	}

	public Map<String, List<String>> getCwpDirectoryMap() {
		return cwpDirectoryMap;
	}

	public void setCwpDirectoryMap(Map<String, List<String>> cwpDirectoryMap) {
		this.cwpDirectoryMap = cwpDirectoryMap;
	}

	public Set<String> getCwpDirectories(){
		if(null == cwpDirectoryMap){
			return new HashSet<String>();
		}
		return cwpDirectoryMap.keySet();
	}
}
