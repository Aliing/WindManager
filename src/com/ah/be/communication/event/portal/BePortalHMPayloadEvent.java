package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.event.BeCapwapPayloadEvent;

/**
 * the parent class for operations between portal and hm
 *@filename		BePortalHMPayloadEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-1-25 03:08:57
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 *
 */
@SuppressWarnings("serial")
public class BePortalHMPayloadEvent extends BeCapwapPayloadEvent {

	// vhm
	public static final short	OPERATIONTYPE_VHM_CREATE				= 1;

	public static final short	OPERATIONTYPE_VHM_MODIFY				= 2;

	public static final short	OPERATIONTYPE_VHM_REMOVE				= 3;

	public static final short	OPERATIONTYPE_VHM_STATUSCHANGE			= 4;

	public static final short	OPERATIONTYPE_VHM_MOVE					= 5;

	public static final short	OPERATIONTYPE_VHM_UPGRADE				= 6;

	public static final short	OPERATIONTYPE_VHM_REVERT				= 7;

	public static final short	OPERATIONTYPE_VHM_REPORT_UPGRADE_STATUS	= 8;

	public static final short	OPERATIONTYPE_VHM_REPORT_MOVING_STATUS	= 9;

	public static final short	OPERATIONTYPE_VHM_REPORT_REVERT_STATUS	= 10;

	public static final short	OPERATIONTYPE_VHM_QUERY_MOVING_STATUS	= 11;

	public static final short	OPERATIONTYPE_VHM_CLEAR_MOVING_STATUS	= 12;
	
	// query user group info
    public static final short   OPERATIONTYPE_VHM_GET_USER_GROUPS       = 13;

	// user
	public static final short	OPERATIONTYPE_USER_CREATE				= 100;

	public static final short	OPERATIONTYPE_USER_MODIFY				= 101;

	public static final short	OPERATIONTYPE_USER_REMOVE				= 102;

	public static final short	OPERATIONTYPE_USER_SYNC					= 103;

	public static final short	OPERATIONTYPE_RESET_VHM_ADMIN_PASSWORD	= 104;

	public static final short	OPERATIONTYPE_SEND_CREDENTIAL			= 105;

	public static final short	OPERATIONTYPE_QUERY_VHMUSERS			= 106;

	// hmol
	public static final short	OPERATIONTYPE_UPDATE_APPLICATION_INFO	= 200;

	public static final short	OPERATIONTYPE_HMOL_CREATE				= 201;

	public static final short	OPERATIONTYPE_HMOL_REMOVE				= 202;

	public static final short	OPERATIONTYPE_HMOL_MODIFY				= 203;

	public static final short	OPERATIONTYPE_HMOL_QUERYINFO			= 204;

	public static final short	OPERATIONTYPE_HMOL_VERSIONUPDATEINFO	= 205;

	public static final short	OPERATIONTYPE_HMOL_POWEROFF				= 206;

	// HA
	public static final short	OPERATIONTYPE_HA_ENABLE					= 300;

	public static final short	OPERATIONTYPE_HA_JOIN					= 301;

	public static final short	OPERATIONTYPE_HA_BREAK					= 302;

	public static final short	OPERATIONTYPE_HA_SWITCHOVER				= 303;

	public static final short	OPERATIONTYPE_HA_STATUSINFO				= 304;

	public static final short	OPERATIONTYPE_HA_MAINTENANCE			= 305;

    // Search or delete AP
    public static final short   OPERATIONTYPE_HM_SEARCH_AP              = 400;

    public static final short   OPERATIONTYPE_HM_DELETE_AP              = 401;

    public static final short   OPERATIONTYPE_SS_SEARCH_AP              = 402;

    public static final short   OPERATIONTYPE_SS_DELETE_AP              = 403;

    public static final short   OPERATIONTYPE_LS_SEARCH_AP              = 404;

    public static final short   OPERATIONTYPE_LS_DELETE_AP              = 405;


	/**
	 * the operation type between portal and HM.
	 */
	protected short				operationType;

	/**
	 * enable and disable
	 */
	public static final byte	ENABLE									= 1;

	public static final byte	DISABLE									= 0;

	public BePortalHMPayloadEvent() {
		super();
		queryType = QUERYTYPE_PORTALANDHM;
	}

	/**
	 * build event data to packet message
	 *
	 * @return byte[]
	 * @throws BeCommunicationEncodeException
	 *             -
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		if (apMac == null) {
			throw new BeCommunicationEncodeException("apMac is a necessary field!");
		}

		byte[] requestData = getRequestData();

		try {
			ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(1 + apMac.length());
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPPAYLOADQUERY);
			buf.putInt(15 + requestData.length); // payload query length

			buf.putShort(queryType);
			buf.putInt(sequenceNum);
			buf.put(BeCommunicationConstant.NOTCOMPRESS);
			buf.putInt(requestData.length);
			buf.putInt(requestData.length);
			buf.put(requestData);

			buf.flip();
			byte[] array = new byte[buf.limit()];
			buf.get(array);
			setPacket(array);

			return array;
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BePortalHMPayloadEvent.buildPacket() catch exception", e);
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
		byte[] data = buildOperationData();
		if (data.length == 0) {
			throw new BeCommunicationEncodeException("request data is necessary!");
		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		buf.putShort(operationType);
		buf.putInt(data.length);

		buf.put(data);

		buf.flip();
		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	/**
	 * this api should override by clild class, put into concrete data
	 *
	 * @param
	 *
	 * @return
	 */
	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		return new byte[0];
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		sequenceNum = buf.getInt(); // sequence number
		buf.get(); // compress flag
		buf.getInt(); // original length
		buf.getInt(); // data length
		operationType = buf.getShort(); // operation type

		byte[] remaining = new byte[buf.remaining()];
		buf.get(remaining, 0, buf.remaining());
		return remaining;
	}

	public short getOperationType() {
		return operationType;
	}

	public void setOperationType(short operationType) {
		this.operationType = operationType;
	}
}
