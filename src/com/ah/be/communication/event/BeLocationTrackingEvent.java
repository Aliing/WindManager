package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhEncoder;

/**
 * 
 *@filename		BeLocationTrackingEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-2-25 03:19:24
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeLocationTrackingEvent extends BeCapwapClientEvent {

	public static final short	OPCODE_START				= 1;

	public static final short	OPCODE_STOP					= 2;

	public static final short	OPCODE_MODIFY				= 3;

	public static final short	OPCODE_QUERY				= 4;

	public static final short	OPCODE_RSSIREPORT			= 5;

	public static final short	OPCODE_ACK					= 6;

	public static final short	TLVTYPE_REPORTINTERVAL		= 1;

	public static final short	TLVTYPE_RSSIUPDATETHRESHOLD	= 2;

	public static final short	TLVTYPE_RSSIVALIDPERIOD		= 3;

	public static final short	TLVTYPE_CLIENTMAC			= 4;

	public static final short	TLVTYPE_CLIENTOUI			= 5;

	public static final short	TLVTYPE_RSSIREADING			= 6;

	public static final short	TLVTYPE_ACKCODE				= 7;

	private static final short	LEN_OPCODE					= 2;

	private static final short	LEN_TOTALLENGTH				= 2;

	private static final short	LEN_TLVNUMBER				= 2;

	private static final short	LEN_TLV_REPORTINTERVAL		= 6;

	private static final short	LEN_TLV_RSSIUPDATETHRESHOLD	= 5;

	private static final short	LEN_TLV_RSSIVALIDPERIOD		= 6;

	private static final byte	VALUE_TRACKALLCLIENT		= (byte) 0xFF;

	private short				opCode;

	// Indicate the RSSI report interval to HM in seconds
	private short				reportInterval;

	// Indicate the RSSI delta threshold for AP to trigger a change update
	private byte				rssiChangeUpdateThreshold;

	// Indicate RSSI valid time period in seconds
	private short				rssiValidPeriod;

	// List one or multiple client MAC addresses HM wanting AP to track. If the value 0xFF is
	// assigned in the 'Num of MACs' field, it indicates that HM wants to track all the clients, no
	// MAC address is included in the TLV
	private List<String>		clientMacList;

	// is start/stop track all the clients
	private boolean				isAllClients;

	// List one or multiple OUIs HM wanting AP to track
	private List<String>		clientOuiList;

	public BeLocationTrackingEvent() {
		super();
		queryType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LOCATIONTRACK;
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
			// get message data
			byte[] trackData = null;
			switch (opCode) {
			case OPCODE_START:
				trackData = buildStartMessage();
				break;

			case OPCODE_STOP:
				trackData = buildStopMessage();
				break;

			case OPCODE_MODIFY:
				trackData = buildModifyMessage();
				break;

			case OPCODE_QUERY:
				trackData = buildQueryMessage();
				break;

			default:
				throw new BeCommunicationEncodeException("opCode (" + opCode + ") is invalid!");
			}

			/**
			 * AP identifier 's length = 6 + 1 + apSerialNum.length()<br>
			 * query's length = 6 + 11 + trackData.length
			 */
			int apIdentifierLen = 7 + apMac.length();
			int queryLen = 17 + trackData.length;
			int bufLength = apIdentifierLen + queryLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTQUERY);
			buf.putInt(11 + trackData.length); // 2+4+1+4+..
			buf.putShort(queryType);
			buf.putInt(sequenceNum);
			buf.put(flag);
			buf.putInt(trackData.length); // data length
			buf.put(trackData);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeLocationTrackingEvent.buildPacket() catch exception", e);
		}
	}

	private byte[] buildStartMessage() throws BeCommunicationEncodeException {
		// 2+4+2
		short dataLen = LEN_OPCODE + LEN_TOTALLENGTH + LEN_TLVNUMBER;
		short tlvNumber = 0;

		if (reportInterval > 0) {
			dataLen += 6;
			tlvNumber++;
		}
		if (rssiChangeUpdateThreshold > 0) {
			dataLen += 5;
			tlvNumber++;
		}
		if (rssiValidPeriod > 0) {
			dataLen += 6;
			tlvNumber++;
		}
		if (isAllClients) {
			dataLen += 5;
			tlvNumber++;
		} else if (clientMacList != null && clientMacList.size() > 0) {
			dataLen += 5 + clientMacList.size() * 6;
			tlvNumber++;
		}
		if (clientOuiList != null && clientOuiList.size() > 0) {
			dataLen += 5 + clientOuiList.size() * 3;
			tlvNumber++;
		}

		if (tlvNumber == 0) {
			throw new BeCommunicationEncodeException(
					"Start location tracking message tlv is empty.");
		}

		ByteBuffer buf = ByteBuffer.allocate(dataLen);
		buf.putShort(OPCODE_START);
		buf.putShort(dataLen);
		buf.putShort(tlvNumber);
		if (reportInterval > 0) {
			buf.putShort(TLVTYPE_REPORTINTERVAL);
			buf.putShort(LEN_TLV_REPORTINTERVAL);
			buf.putShort(reportInterval);
		}
		if (rssiChangeUpdateThreshold > 0) {
			buf.putShort(TLVTYPE_RSSIUPDATETHRESHOLD);
			buf.putShort(LEN_TLV_RSSIUPDATETHRESHOLD);
			buf.put(rssiChangeUpdateThreshold);
		}
		if (rssiValidPeriod > 0) {
			buf.putShort(TLVTYPE_RSSIVALIDPERIOD);
			buf.putShort(LEN_TLV_RSSIVALIDPERIOD);
			buf.putShort(rssiValidPeriod);
		}
		if (isAllClients) {
			buf.putShort(TLVTYPE_CLIENTMAC);
			buf.putShort((short) 5);
			buf.put(VALUE_TRACKALLCLIENT);
		} else if (clientMacList != null && clientMacList.size() > 0) {
			buf.putShort(TLVTYPE_CLIENTMAC);
			buf.putShort((short) (5 + clientMacList.size() * 6));
			buf.put((byte)clientMacList.size());
			for (String macAddr : clientMacList) {
				buf.put(AhEncoder.hex2bytes(macAddr));
			}
		}
		if (clientOuiList != null && clientOuiList.size() > 0) {
			buf.putShort(TLVTYPE_CLIENTOUI);
			buf.putShort((short) (5 + clientOuiList.size() * 3));
			buf.put((byte)clientOuiList.size());
			for (String oui : clientOuiList) {
				buf.put(AhEncoder.hex2bytes(oui));
			}
		}

		return buf.array();
	}

	private byte[] buildStopMessage() throws BeCommunicationEncodeException {
		// 2+4+2
		short dataLen = LEN_OPCODE + LEN_TOTALLENGTH + LEN_TLVNUMBER;
		short tlvNumber = 0;

		if (isAllClients) {
			dataLen += 5;
			tlvNumber++;
		} else if (clientMacList != null && clientMacList.size() > 0) {
			dataLen += 5 + clientMacList.size() * 6;
			tlvNumber++;
		}
		if (clientOuiList != null && clientOuiList.size() > 0) {
			dataLen += 5 + clientOuiList.size() * 3;
			tlvNumber++;
		}

		if (tlvNumber == 0) {
			throw new BeCommunicationEncodeException("Stop location tracking message tlv is empty.");
		}

		ByteBuffer buf = ByteBuffer.allocate(dataLen);
		buf.putShort(OPCODE_STOP);
		buf.putShort(dataLen);
		buf.putShort(tlvNumber);
		if (isAllClients) {
			buf.putShort(TLVTYPE_CLIENTMAC);
			buf.putShort((short) 5);
			buf.put(VALUE_TRACKALLCLIENT);
		} else if (clientMacList != null && clientMacList.size() > 0) {
			buf.putShort(TLVTYPE_CLIENTMAC);
			buf.putShort((short) (5 + clientMacList.size() * 6));
			buf.put((byte)clientMacList.size());
			for (String macAddr : clientMacList) {
				buf.put(AhEncoder.hex2bytes(macAddr));
			}
		}
		if (clientOuiList != null && clientOuiList.size() > 0) {
			buf.putShort(TLVTYPE_CLIENTOUI);
			buf.putShort((short) (5 + clientOuiList.size() * 3));
			buf.put((byte)clientOuiList.size());
			for (String oui : clientOuiList) {
				buf.put(AhEncoder.hex2bytes(oui));
			}
		}

		return buf.array();
	}

	private byte[] buildModifyMessage() throws BeCommunicationEncodeException {
		// 2+4+2
		short dataLen = LEN_OPCODE + LEN_TOTALLENGTH + LEN_TLVNUMBER;
		short tlvNumber = 0;

		if (reportInterval > 0) {
			dataLen += 6;
			tlvNumber++;
		}
		if (rssiChangeUpdateThreshold > 0) {
			dataLen += 5;
			tlvNumber++;
		}
		if (rssiValidPeriod > 0) {
			dataLen += 6;
			tlvNumber++;
		}

		if (tlvNumber == 0) {
			throw new BeCommunicationEncodeException(
					"Modify location tracking message tlv is empty.");
		}

		ByteBuffer buf = ByteBuffer.allocate(dataLen);
		buf.putShort(OPCODE_MODIFY);
		buf.putShort(dataLen);
		buf.putShort(tlvNumber);
		if (reportInterval > 0) {
			buf.putShort(TLVTYPE_REPORTINTERVAL);
			buf.putShort(LEN_TLV_REPORTINTERVAL);
			buf.putShort(reportInterval);
		}
		if (rssiChangeUpdateThreshold > 0) {
			buf.putShort(TLVTYPE_RSSIUPDATETHRESHOLD);
			buf.putShort(LEN_TLV_RSSIUPDATETHRESHOLD);
			buf.put(rssiChangeUpdateThreshold);
		}
		if (rssiValidPeriod > 0) {
			buf.putShort(TLVTYPE_RSSIVALIDPERIOD);
			buf.putShort(LEN_TLV_RSSIVALIDPERIOD);
			buf.putShort(rssiValidPeriod);
		}

		return buf.array();
	}

	private byte[] buildQueryMessage() throws BeCommunicationEncodeException {
		// 2+4+2
		short dataLen = LEN_OPCODE + LEN_TOTALLENGTH + LEN_TLVNUMBER;
		short tlvNumber = 0;

		if (isAllClients) {
			dataLen += 5;
			tlvNumber++;
		} else if (clientMacList != null && clientMacList.size() > 0) {
			dataLen += 5 + clientMacList.size() * 6;
			tlvNumber++;
		}

		if (tlvNumber == 0) {
			throw new BeCommunicationEncodeException(
					"Query location tracking message tlv is empty.");
		}

		ByteBuffer buf = ByteBuffer.allocate(dataLen);
		buf.putShort(OPCODE_QUERY);
		buf.putShort(dataLen);
		buf.putShort(tlvNumber);
		if (isAllClients) {
			buf.putShort(TLVTYPE_CLIENTMAC);
			buf.putShort((short) 5);
			buf.put(VALUE_TRACKALLCLIENT);
		} else if (clientMacList != null && clientMacList.size() > 0) {
			buf.putShort(TLVTYPE_CLIENTMAC);
			buf.putShort((short) (5 + clientMacList.size() * 6));
			buf.put((byte)clientMacList.size());
			for (String macAddr : clientMacList) {
				buf.put(AhEncoder.hex2bytes(macAddr));
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
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		super.parsePacket(data);
	}

	public List<String> getClientMacList() {
		return clientMacList;
	}

	public void setClientMacList(List<String> clientMacList) {
		this.clientMacList = clientMacList;
	}

	public List<String> getClientOuiList() {
		return clientOuiList;
	}

	public void setClientOuiList(List<String> clientOuiList) {
		this.clientOuiList = clientOuiList;
	}

	public boolean isAllClients() {
		return isAllClients;
	}

	public void setAllClients(boolean isTrackAllClients) {
		this.isAllClients = isTrackAllClients;
	}

	public short getOpCode() {
		return opCode;
	}

	public void setOpCode(short opCode) {
		this.opCode = opCode;
	}

	public short getReportInterval() {
		return reportInterval;
	}

	public void setReportInterval(short reportInterval) {
		this.reportInterval = reportInterval;
	}

	public byte getRssiChangeUpdateThreshold() {
		return rssiChangeUpdateThreshold;
	}

	public void setRssiChangeUpdateThreshold(byte rssiChangeUpdateThreshold) {
		this.rssiChangeUpdateThreshold = rssiChangeUpdateThreshold;
	}

	public short getRssiValidPeriod() {
		return rssiValidPeriod;
	}

	public void setRssiValidPeriod(short rssiValidPeriod) {
		this.rssiValidPeriod = rssiValidPeriod;
	}
}
