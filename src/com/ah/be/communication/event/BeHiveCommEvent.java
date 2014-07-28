package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.coder.AhEncoder;

/**
 * 
 *@filename		BeHiveCommEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-12-21 02:58:33
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeHiveCommEvent extends BeCapwapClientEvent {

	public static final int		HIVEMESSAGE_SAVEIMAGE	= 1;

	public static final int		HIVEMESSAGE_CLI			= 2;
	
	public static final int		HIVEMESSAGE_CANCELIMAGE = 3;

	/**
	 * return flag, 1 means collect all result, 0 means return result one by one.
	 */
	public static final byte	RETURNFLAG_NEEDALL		= 1;

	public static final byte	RETURNFLAG_ONEBYONE		= 0;

	private int					hiveMessageType;

	// seconds
	private int					hiveTimeout				= 120;

	private byte				hiveReturnFlag			= RETURNFLAG_NEEDALL;

	private String				cli;

	private List<HiveAp>		apList;

	public BeHiveCommEvent() {
		super();
		queryType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_HIVECOMM;
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
					"BeHiveCommEvent.buildPacket() catch exception", e);
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
		if (apList == null || apList.size() == 0) {
			throw new BeCommunicationEncodeException("AP list is empty!");
		}

		if (hiveMessageType == HIVEMESSAGE_CLI && (cli == null || cli.length() == 0)) {
			throw new BeCommunicationEncodeException("CLI is a necessary field!");
		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		buf.putInt(hiveMessageType);
		buf.putShort((short) (apList.size() * 12));
		for (HiveAp ap : apList) {
			buf.putShort((short) 10);
			buf.put(AhEncoder.hex2bytes(ap.getMacAddress()));
			buf.putInt(AhEncoder.ip2Int(ap.getIpAddress()));
		}
		buf.put(hiveReturnFlag);
		buf.putInt(hiveTimeout);
		if (hiveMessageType == HIVEMESSAGE_CLI) {
			buf.putInt(cli.length());
			buf.put(cli.getBytes());
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
	}

	public String getCli() {
		return cli;
	}

	public void setCli(String cli) {
		this.cli = cli;
	}

	public byte getHiveReturnFlag() {
		return hiveReturnFlag;
	}

	public void setHiveReturnFlag(byte hiveReturnFlag) {
		this.hiveReturnFlag = hiveReturnFlag;
	}

	public int getHiveTimeout() {
		return hiveTimeout;
	}

	public void setHiveTimeout(int hiveTimeout) {
		this.hiveTimeout = hiveTimeout;
	}

	public int getHiveMessageType() {
		return hiveMessageType;
	}

	public void setHiveMessageType(int hiveMessageType) {
		this.hiveMessageType = hiveMessageType;
	}

	public List<HiveAp> getApList() {
		return apList;
	}

	public void setApList(List<HiveAp> apList) {
		this.apList = apList;
	}
}
