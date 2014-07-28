package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;

/**
 * 
 *@filename		BeStudentReportEvent.java
 *@version		V1.0.0.0
 *@author		
 *@createtime	2011-1-11 11:20:24
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeStudentReportEvent extends BeCapwapClientEvent {

	public static final byte	TABLETYPE_STUDENT	= 1;

	private List<Byte>			tableTypeList;

	private boolean				queryAllTable;

	public BeStudentReportEvent() {
		super();
		queryType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_STUDENTREPORT;
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

		if (sequenceNum <= 0) {
			throw new BeCommunicationEncodeException("sequenceNum is a necessary field!");
		}

		try {
			byte[] requestData = getRequestData();

			/**
			 * AP identifier 's length = 6 + 1 + apSerialNum.length()<br>
			 * query's length = 6 + 11 + requestData.length
			 */
			int apIdentifierLen = 7 + apMac.length();
			int queryLen = 17 + requestData.length;
			int bufLength = apIdentifierLen + queryLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTQUERY);
			buf.putInt(11 + requestData.length); // 2+4+1+4+reqestData.length
			buf.putShort(queryType);
			buf.putInt(sequenceNum);
			buf.put(flag);
			buf.putInt(requestData.length); // data length
			buf.put(requestData);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeInterfaceClientEvent.buildPacket() catch exception", e);
		}
	}

	/**
	 * get request data
	 * 
	 * @param
	 * 
	 * @return
	 */
	private byte[] getRequestData() throws BeCommunicationEncodeException {
		if (!queryAllTable && (tableTypeList == null || tableTypeList.size() == 0)) {
			throw new BeCommunicationEncodeException("tableTypeList is a request field!");
		}

		int dataLen = 0;
		if (queryAllTable) {
			dataLen = 1;
		} else {
			dataLen = 1 + tableTypeList.size();
		}

		ByteBuffer buf = ByteBuffer.allocate(dataLen);

		if (queryAllTable) {
			buf.put((byte) 0);
		} else {
			buf.put((byte) tableTypeList.size());
			for (Byte type : tableTypeList) {
				buf.put(type);
			}
		}

		return buf.array();
	}
	
	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 * @throws BeCommunicationDecodeException -
	 */
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		super.parsePacket(data);
	}

	public List<Byte> getTableTypeList() {
		return tableTypeList;
	}

	public void setTableTypeList(List<Byte> tableTypeList) {
		this.tableTypeList = tableTypeList;
	}

	public boolean isQueryAllTable() {
		return queryAllTable;
	}

	public void setQueryAllTable(boolean queryAllTable) {
		this.queryAllTable = queryAllTable;
	}
}
