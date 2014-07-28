package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeEventUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.fault.BeFaultConst;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.performance.AhAssociation;
import com.ah.util.coder.AhDecoder;
import com.ah.util.datetime.AhDateTimeUtil;

/**
 *
 *@filename		BeTrapEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-7-9 03:15:19
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
@SuppressWarnings("serial")
public class BeTrapEvent extends BeAPWTPEvent implements Comparable<BeTrapEvent> {
	public static final byte	TYPE_FAILURE				= 1;
	public static final byte	TYPE_THRESHOLDCROSSING		= 2;
	public static final byte	TYPE_STATECHANGE			= 3;
	public static final byte	TYPE_CONNECTCHANGE			= 4;
	public static final byte	TYPE_CLIENTINFOMATION		= 6;
	public static final byte	TYPE_POE					= 7;
	public static final byte	TYPE_CHANNELPOWERCHANGE		= 8;
	public static final byte	TYPE_INTERFERENCEALERT		= 10;
	public static final byte	TYPE_BANDWIDTHSENTINELEVENT	= 11;
	public static final byte	TYPE_INTERFACECLIENTTRAP	= 12;
	public static final byte	TYPE_TIMEBOMBWARNING		= 100;
	public static final byte	TYPE_AIRSCREENREPORT		= 101;
	public static final byte	TYPE_VPN					= 102;
	public static final byte	TYPE_SSID_STATECHANGE		= 103;
	public static final byte	TYPE_CLIENT_CONNECTDOWN		= 104;
	public static final byte	TYPE_SECURITY_ALARM			= 105;
	public static final byte	TYPE_CLIENTOSINFOMATION		= 106;
	public static final byte	TYPE_AD_ALARM				= 107;
	public static final byte	TYPE_CAPWAP_DELAY			= 108;
	public static final byte	TYPE_CLIENT_SELF_REGISTER_INFO	= 109;
	public static final byte	TYPE_RADAR_CHANNEL			= 110;
	public static final byte	TYPE_PSE_ERROR				= 111;
	public static final byte	TYPE_VoIP_QoS_POLICING		= 112;
	public static final byte    TYPE_ALARM					= 113;
	public static final byte	TYPE_CWP_INFO				= 114;
	public static final byte    TYPE_POWER_MODE_CHANGE		= 115;

	public static final byte    TYPE_TCA_ALARM              = 118;

	private int					requestSequenceNumber;

	// add by CCHEN
	public static final byte    TYPE_KERNEL_DUMP_EVENT			= -127;
	public static final byte	TYPE_CAPWAP_EVENT				= -126;
	public static final byte	TYPE_DTLS_HANDSHAKE_FAIL 		= -125;
	public static final byte	TYPE_DOWNLOAD_CONFIG_EVENT		= -124;


	private long				timeStamp;

	private String				timeZone;

	private String				apName;

	private String				domainName			= null;

	private String					eventTag1;

	private int						alarmTag1 = 0;

	private int						alarmTag2 = 0;

	private String					alarmTag3 = "";

	//cwp info event
	private String				cwpKeyValues = "";

	public String getAhTimeDisplay2() {
		if (timeStamp <= 0) {
			return "-";
		}

		return AhDateTimeUtil.getFormatDateTime(new Date(timeStamp), AhDateTimeUtil.DEFAULT_DATE_TIME_FORMAT, TimeZone
				.getTimeZone(timeZone));
	}

	public String getTrapTypeString() {
		String trapType = "";
		if (getTrapType() == BeTrapEvent.TYPE_CONNECTCHANGE) {
			trapType = "Connection Change";
		} else if (getTrapType() == BeTrapEvent.TYPE_STATECHANGE) {
			trapType = "State Change";
		} else if (getTrapType() == BeTrapEvent.TYPE_THRESHOLDCROSSING) {
			trapType = "Threshold Crossing";
		} else if (getTrapType() == BeTrapEvent.TYPE_POE) {
			trapType = "POE";
		} else if (getTrapType() == BeTrapEvent.TYPE_CHANNELPOWERCHANGE) {
			trapType = "Channel Power Change";
		} else if (getTrapType() == BeTrapEvent.TYPE_AIRSCREENREPORT) {
			trapType = "Air Screen";
		} else if (getTrapType() == BeTrapEvent.TYPE_VPN) {
			trapType = "VPN Service";
		} else if (getTrapType() == BeTrapEvent.TYPE_ALARM) {
			trapType = "Alarm";
		} else if (getTrapType() == BeTrapEvent.TYPE_CWP_INFO) {
			trapType = "CWP info";
		} else if (getTrapType() == BeTrapEvent.TYPE_POWER_MODE_CHANGE) {
			trapType = "Power Mode Change";
		}
		return trapType;
	}

	public boolean isClientMonitorEvent() {
		return getTrapType() == BeTrapEvent.TYPE_INTERFACECLIENTTRAP
				&& getSourceType() == BeTrapEvent.SOURCE_CLIENT;
	}

	public boolean isInterfaceStatTrapEvent() {
		return getTrapType() == BeTrapEvent.TYPE_INTERFACECLIENTTRAP
				&& getSourceType() == BeTrapEvent.SOURCE_INTERFACE;
	}

	public boolean isStateChangeEvent() {
		return getTrapType() == BeTrapEvent.TYPE_STATECHANGE;
	}

	public boolean isPOEEvent() {
		return getTrapType() == BeTrapEvent.TYPE_POE;
	}

	public boolean isChannelPowerChangeEvent() {
		return getTrapType() == BeTrapEvent.TYPE_CHANNELPOWERCHANGE;
	}

	public boolean isHDCPUEvent() {
		return getObjectName().equalsIgnoreCase(BeFaultConst.TRAP_SEND_MAIL_TYPEX[3]);
	}

	public boolean isHDMemoryEvent() {
		return getObjectName().equalsIgnoreCase(BeFaultConst.TRAP_SEND_MAIL_TYPEX[4]);
	}

	public boolean isAuthEvent() {
		//return getObjectName().equalsIgnoreCase(BeFaultConst.TRAP_SEND_MAIL_TYPEX[5]);
		if(getObjectName().equalsIgnoreCase(BeFaultConst.TRAP_SEND_MAIL_TYPEX[5]))
			return true;
		if( getTrapType() == BeTrapEvent.TYPE_CWP_INFO )
			return true;
		return false;
	}

//	public boolean isClientRegisterEvent(){
//		return getObjectName().equalsIgnoreCase(BeFaultConst.TRAP_SEND_MAIL_TYPEX[21]);
//	}

	public boolean isInterfaceEvent() {
		if (getObjectName().toLowerCase().contains("wifi")
				|| getObjectName().toLowerCase().contains("eth")
				|| getObjectName().toLowerCase().contains("adap")) {
			if (isStateChangeEvent() || isPOEEvent() || isChannelPowerChangeEvent()
					|| isInterfaceStatTrapEvent()|| isPSEErrorEvent() || isPowerModeChangeEvent()) {
				return true;
			}
		}

		return false;
	}

	public boolean isPSEErrorEvent(){
		return getTrapType() == BeTrapEvent.TYPE_PSE_ERROR;
	}

	public boolean isPowerModeChangeEvent(){
		return getTrapType() == BeTrapEvent.TYPE_POWER_MODE_CHANGE;
	}

	public boolean isL2DOSEvent() {
		return getObjectName().equalsIgnoreCase(BeFaultConst.TRAP_SEND_MAIL_TYPEX[7]);
	}

	public boolean isScreenEvent() {
		return getObjectName().equalsIgnoreCase(BeFaultConst.TRAP_SEND_MAIL_TYPEX[11]);
	}

	public boolean isAirScreenEvent() {
		return getObjectName().equalsIgnoreCase(BeFaultConst.TRAP_SEND_MAIL_TYPEX[14]);
	}

	public boolean isVPNEvent() {
		return getObjectName().equalsIgnoreCase(BeFaultConst.TRAP_SEND_MAIL_TYPEX[15]);
	}

	// add end

	// // increase by degrees.From 1 to 0X7FFFFFFF
	// private int sequenceNumber;

	private byte				trapType;

	private String				objectName;

	private String				describe;

	private int					code;

	private byte				severity;

	private byte				probableCause;

	// indicate whether this is an alert or clear alert
	private boolean				failureFlag					= true;

	private int					currentValue;

	private int					thresholdhigh;

	private int					thresholdlow;

	private byte				previousState;

	private byte				currentState;

	private int					ifIndex;

	private String				remoteID;

	private byte				objectType;

	private String				clientSSID;

	private String				clientIP;

	private String				clientHostName;

	private String				clientUserName;

	private int					powerSource;

	private byte				poEEth0On;

	private int					poEEth0Pwr;

	private byte				poEEth1On;

	private int					poEEth1Pwr;

	private byte				poEEth0MaxSpeed;

	private byte				poEEth1MaxSpeed;

	private byte				poEWifi0Setting;

	private byte				poEWifi1Setting;

	private byte				poEWifi2Setting;

	/**
	 * add for connection change event trap
	 */
	public static final int TRAP_STATE_UP_INDEX = 1;
	public static final int TRAP_STATE_DOWN_INDEX = 2;

	public static final int TRAP_OBJECT_TYPE_CLIENT_INDEX = 1;
	public static final int TRAP_OBJECT_TYPE_NEIGHBOR_INDEX = 2;

	private byte				clientCWPUsed;

	private byte				clientAuthMethod;

	private byte				clientEncryptionMethod;

	private byte				clientMacProtocol;

	private int					clientVLAN;

	private int					clientUserProfId;

	private int					clientChannel;

	private String				clientBSSID;

	// mark: initilize value, prevent unfriendly GUI display
	private long				associationTime				= System.currentTimeMillis() / 1000;

	private String				ifName;

	private int					clientRssi;

	private String  			userProfileName;

	private short				SNR;

	private byte				clientMacBasedAuthUsed;
	
	private short               managedStatus;

	/**
	 * channel power change event
	 */
	private int					radioChannel;

	private int					radioTxPower;

	private int					beaconInterval;

	// The configured interference CU threshold
	private byte				interferenceCUThreshold;

	private byte				averageInterferenceCU;

	private byte				shortTermInterferenceCU;

	private byte				snapShotInterferenceCU;

	// The configured CRC error rate threshold
	private byte				crcErrorRateThreshold;

	private byte				crcErrorRate;

	// for air screen trap
	private byte				reportType;

	private byte				nameType;

	private String				name;

	private byte				sourceType;

	private String				sourceID;

	private long				airScreenTime;

	private String				ruleName;

	private int					instanceID;

	// bandwidht sentinel event
	private int					bandwidthSentinelStatus;

	private int					guaranteedBandWidth;

	private int					actualBandWidth;

	private int					bandWidthAction;

	private byte 				channelUltil;

	private byte				interferenceUltil;

	private byte				txUltil;

	private byte				rxUltil;

	// 0 down, 1 up
	private byte				state;

	// interface client trap
	// use source type indicate interface or client
	public static final byte	SOURCE_INTERFACE			= 1;
	public static final byte	SOURCE_CLIENT				= 2;

	public static final byte	ALERTTYPE_CRCERROR			= 0;

	public static final byte	ALERTTYPE_TXDROP			= 1;

	public static final byte	ALERTTYPE_TXRETRY			= 2;

	public static final byte	ALERTTYPE_RXDROP			= 3;

	public static final byte	ALERTTYPE_AIRTIMECONSUME	= 4;

	private byte				alertType;

	private int					thresholdValue;

	private int					shorttermValue;

	private int					snapshotValue;

	//client os information
	private String				clientOsInfo;

	// Directory Alarm trap
	private byte				reasonType;// 1:net join,2:bind dn

	// CAPWAP delay trap
	private long avgDelay;

	private long curDelay;

	private long minorThreshold;

	private long majorThreshold;

	private String				os_lenght;

	private String				os_option55;

	// Client self register info
	long	expirationTime;

	String	email;

	String	companyName;

	// power mode change event
	private byte				powerMode;

	public static final byte	POWERMODE_AT			= 0;
	public static final byte	POWERMODE_AF			= 1;

	private List<AhAlarm> alarmList;

	/**
	 * Construct method
	 *
	 * @param
	 * @throws
	 */
	public BeTrapEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGEELEMENTTYPE_TRAPEVENT;
		alarmList = new ArrayList<AhAlarm>();
	}

	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);

			simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			if (simpleHiveAp == null) {
				throw new BeCommunicationDecodeException("Invalid apMac:(" + apMac
						+ "), Can't find corresponding data in cache.");
			}

			byte[] buffer = getWtpMsgData();
			ByteBuffer buf = ByteBuffer.wrap(buffer);

			requestSequenceNumber = buf.getInt();
			sequenceNum = buf.getInt();

			trapType = buf.get();

			switch (trapType) {
			case TYPE_FAILURE: {
				parseFailureTrap(buf);
				break;
			}

			case TYPE_THRESHOLDCROSSING: {
				parseThresholdCrossingEvent(buf);
				break;
			}

			case TYPE_STATECHANGE: {
				parseStateChangeEvent(buf);
				break;
			}

			case TYPE_CONNECTCHANGE: {
				parseConnectChange(buf);
				break;
			}

			case TYPE_CLIENTINFOMATION: {
				parseClientInformationTrap(buf);
				break;
			}

			case TYPE_POE: {
				parsePowerInformationTrap(buf);
				break;
			}

			case TYPE_CHANNELPOWERCHANGE: {
				parseChannelPowerChangeTrap(buf);
				break;
			}

			case TYPE_TIMEBOMBWARNING: {
				parseTimeBombWarningEvent(buf);
				break;
			}

			case TYPE_INTERFERENCEALERT: {
				parseInterferenceAlertTrap(buf);
				break;
			}

			case TYPE_AIRSCREENREPORT: {
				parseAirScreenReport(buf);
				break;
			}

			case TYPE_BANDWIDTHSENTINELEVENT: {
				parseBandWidthSentinelEvent(buf);
				break;
			}

			case TYPE_VPN: {
				parseVPNEvent(buf);
				break;
			}

			case TYPE_SSID_STATECHANGE: {
				parseSSIDStateChangeEvent(buf);
				break;
			}

			case TYPE_INTERFACECLIENTTRAP: {
				parseInterfaceClientTrap(buf);
				break;
			}

			case TYPE_CLIENT_CONNECTDOWN: {
				parseClientConnectDownTrap(buf);
				break;
			}

			case TYPE_SECURITY_ALARM: {
				parseSecurityAlarmTrap(buf);
				break;
			}

			case TYPE_CLIENTOSINFOMATION: {
				parseClientOSInfoTrap(buf);
				break;
			}

			case TYPE_AD_ALARM: {
				parseDirectoryAlarmTrap(buf);
				break;
			}

			case TYPE_CAPWAP_DELAY: {
				parseCapwapDelayTrap(buf);
				break;
			}

			case TYPE_CLIENT_SELF_REGISTER_INFO: {
				parseClientSelfRegisterInfo(buf);
				break;
			}

			case TYPE_RADAR_CHANNEL: {
				parseRadarChannel(buf);
				break;
			}

			case TYPE_PSE_ERROR: {
				parsePSEError(buf);
				break;
			}

			case TYPE_VoIP_QoS_POLICING: {
				parseVoIP_QoS_Policing(buf);
				break;
			}

			case TYPE_ALARM: {
				parseAlarmTrap(buf);
				break;
			}

			case TYPE_CWP_INFO: {
				parseCwpInfo(buf);
				break;
			}

			case TYPE_POWER_MODE_CHANGE: {
				parsePowerModeChange(buf);
				break;
			}

			default: {
				DebugUtil.commonDebugWarn("invalid trap type when parse trap event. type="
						+ trapType);

				break;
			}
			}

			if (describe != null) {
				describe = describe.replace("^M", "\n");// for display
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException("BeTrapEvent.parsePacket() catch exception", e);
		}
	}

	private void parsePowerModeChange(ByteBuffer buf) {
		buf.getShort(); // data length

		byte len = buf.get();// Object name length
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));// Object name

		len = buf.get();// Describe length
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));// Describe

		powerMode = buf.get();
	}

	private void parseCapwapDelayTrap(ByteBuffer buf) {
		buf.getShort(); // data length

		byte len = buf.get();// Describe length
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));// Descripe details

		severity = buf.get();

		avgDelay = buf.getLong();
		curDelay = buf.getLong();
		minorThreshold = buf.getLong();
		majorThreshold = buf.getLong();
	}

	private void parseClientSelfRegisterInfo(ByteBuffer buf) {
		buf.getShort(); // data length

		remoteID = AhDecoder.bytes2hex(buf, 6);
		int time = buf.getInt();
		if(time == -1)
			expirationTime = 0;
		else
			expirationTime = AhDecoder.int2long(time)*1000;
		byte len = buf.get();
		clientUserName = AhDecoder.bytes2String(buf, len);
		len = buf.get();
		email = AhDecoder.bytes2String(buf, len);
		len = buf.get();
		companyName = AhDecoder.bytes2String(buf, len);
		len = buf.get();
		clientSSID = AhDecoder.bytes2String(buf, len);
	}

	private void parseRadarChannel(ByteBuffer buf) {
		buf.getShort(); // data length

		byte objectNameLen = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(objectNameLen));

		byte describeLen = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(describeLen));

		eventTag1 = objectName;
	}


	private void parsePSEError(ByteBuffer buf) {
		buf.getShort(); // data length

		byte objectNameLen = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(objectNameLen));

		byte describeLen = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(describeLen));

		eventTag1 = objectName;
	}

	private void parseVoIP_QoS_Policing(ByteBuffer buf) {
		buf.getShort(); // data length

		byte objectNameLen = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(objectNameLen));

		byte describeLen = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(describeLen));

		eventTag1 = objectName;
	}

	/**
	 * parse failure trap
	 *
	 * @param
	 * @return
	 */
	private void parseFailureTrap(ByteBuffer buf) {
		buf.getShort(); // data length

		byte objectNameLen = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(objectNameLen));

		byte describeLen = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(describeLen));

		code = buf.getInt();
		severity = buf.get();
		probableCause = buf.get();
		if (buf.hasRemaining()) {
			failureFlag = (buf.get() == 1);
		}
	}

	/**
	 * parse threshold crossing event
	 *
	 * @param
	 * @return
	 */
	private void parseThresholdCrossingEvent(ByteBuffer buf) {
		buf.getShort(); // data length

		byte objectNameLen = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(objectNameLen));
		eventTag1 = objectName + "-";

		byte describeLen = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(describeLen));

		code = buf.getInt();
		eventTag1 += code + "-";

		currentValue = buf.getInt();
		eventTag1 += currentValue + "-";

		thresholdhigh = buf.getInt();
		eventTag1 += thresholdhigh + "-";

		thresholdlow = buf.getInt();
		eventTag1 += thresholdlow;
	}

	/**
	 * parse state change event
	 *
	 * @param
	 * @return
	 */
	private void parseStateChangeEvent(ByteBuffer buf) {
		buf.getShort(); // data length

		byte objectNameLen = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(objectNameLen));
		eventTag1 = objectName + "-";

		byte describeLen = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(describeLen));

		code = buf.getInt();
		eventTag1 += code + "-";

		previousState = buf.get();
		eventTag1 += previousState + "-";

		currentState = buf.get();
		eventTag1 += currentState;
	}

	/**
	 * parse connect change event
	 *
	 * @param
	 * @return
	 */
	private void parseConnectChange(ByteBuffer buf) {
		buf.getShort(); // data length

		byte length = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(length));

		length = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(length));

		code = buf.getInt();
		eventTag1 = code + "-";

		ifIndex = buf.getInt();
		eventTag1 += ifIndex + "-";

		remoteID = AhDecoder.bytes2hex(buf, 6).toUpperCase();
		eventTag1 += remoteID + "-";

		currentState = buf.get();
		objectType = buf.get();
		eventTag1 += objectType + "-";

		if (buf.hasRemaining()) {
			clientIP = AhDecoder.int2IP(buf.getInt());
			eventTag1 += clientIP + "-";

			length = buf.get();
			clientHostName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(length));
			length = buf.get();
			clientUserName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(length));
			length = buf.get();
			clientSSID = AhDecoder.bytes2String(buf, AhDecoder.byte2int(length));
			eventTag1 += clientSSID + "-";

			if (buf.hasRemaining()) {
				clientCWPUsed = buf.get();
				clientAuthMethod = buf.get();
				clientEncryptionMethod = buf.get();
				clientMacProtocol = buf.get();
				clientVLAN = buf.getInt();
				clientUserProfId = buf.getInt();
				clientChannel = buf.getInt();
				clientBSSID = AhDecoder.bytes2hex(buf, 6).toUpperCase();
				associationTime = AhDecoder.int2long(buf.getInt());
			}
			if (buf.hasRemaining()) {
				length = buf.get();
				ifName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(length));
				eventTag1 += ifName;
			}
			if (buf.hasRemaining()) {
				SNR = (short)buf.getInt();
			}
			if (buf.hasRemaining()) {
				length = buf.get();
				userProfileName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(length));
			}
			if (buf.hasRemaining()) {
				clientRssi = buf.getShort();
			} else {
				//for compatibility
				clientRssi = SNR - 95;
			}
			if (buf.hasRemaining()) {
				clientMacBasedAuthUsed = buf.get();
			}
			if (buf.hasRemaining()) {
				length = buf.get();
				clientOsInfo = AhDecoder.bytes2String(buf, AhDecoder.byte2int(length));
				length = buf.get();
				os_option55 = AhDecoder.bytes2String(buf, AhDecoder.byte2int(length));
			}
			if (buf.hasRemaining()) {
				managedStatus = buf.getShort();
			}
			
		}

		// special code for client mac protocol
		if (NmsUtil.compareSoftwareVersion("3.5.0.0", simpleHiveAp.getSoftVer()) <= 0) {
			if (clientMacProtocol == 3) {
				clientMacProtocol = AhAssociation.CLIENTMACPROTOCOL_NAMODE;
			} else if (clientMacProtocol == 4) {
				clientMacProtocol = AhAssociation.CLIENTMACPROTOCOL_NGMODE;
			}
		}
	}

	/**
	 * parse client information trap
	 *
	 * @param
	 * @return
	 */
	private void parseClientInformationTrap(ByteBuffer buf) {
		buf.getShort(); // data length

		byte objectNameLen = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(objectNameLen));
		eventTag1 = objectName + "-";

		byte describeLen = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(describeLen));

		code = buf.getInt();
		eventTag1 += code + "-";

		byte ssidLen = buf.get();
		clientSSID = AhDecoder.bytes2String(buf, AhDecoder.byte2int(ssidLen));
		eventTag1 += clientSSID + "-";

		remoteID = AhDecoder.bytes2hex(buf, 6).toUpperCase();
		eventTag1 += remoteID + "-";

		clientIP = AhDecoder.int2IP(buf.getInt());
		eventTag1 += clientIP;

		byte hostNameLen = buf.get();
		clientHostName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(hostNameLen));

		byte userNameLen = buf.get();
		clientUserName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(userNameLen));
		
		if (buf.hasRemaining()) {
			managedStatus = buf.getShort();
		}
	}

	/**
	 * parse power information trap
	 *
	 * @param
	 * @return
	 */
	private void parsePowerInformationTrap(ByteBuffer buf) {
		buf.getShort(); // data length

		byte objectNameLen = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(objectNameLen));
		eventTag1 = objectName + "-";

		byte describeLen = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(describeLen));

		code = buf.getInt();
		eventTag1 += code + "-";

		powerSource = buf.getInt();
		eventTag1 += powerSource;

		poEEth0On = buf.get();
		poEEth0Pwr = buf.getInt();
		poEEth1On = buf.get();
		poEEth1Pwr = buf.getInt();
		if (buf.hasRemaining()) {
			poEEth0MaxSpeed = buf.get();
			poEEth1MaxSpeed = buf.get();
			poEWifi0Setting = buf.get();
			poEWifi1Setting = buf.get();
			poEWifi2Setting = buf.get();
		}
	}

	/**
	 * parse channel power change event
	 *
	 * @param
	 *
	 * @return
	 */
	private void parseChannelPowerChangeTrap(ByteBuffer buf) {
		buf.getShort(); // data length

		byte objectNameLen = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(objectNameLen));
		eventTag1 = objectName + "-";

		byte describeLen = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(describeLen));

		code = buf.getInt();
		eventTag1 += code + "-";

		ifIndex = buf.getInt();
		eventTag1 += ifIndex;

		radioChannel = buf.getInt();
		radioTxPower = buf.getInt();
		if (buf.hasRemaining()) {
			beaconInterval = buf.getInt();
		}
	}

	/**
	 * parse time bomb warning event
	 *
	 * @param
	 * @return
	 */
	private void parseTimeBombWarningEvent(ByteBuffer buf) {
		buf.getShort(); // data length
		severity = buf.get();

		byte describeLen = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(describeLen));
	}

	/**
	 * parse interference alert
	 *
	 * @param
	 * @return
	 */
	private void parseInterferenceAlertTrap(ByteBuffer buf) {
		buf.getShort(); // data length

		byte len = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));

		len = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));

		code = buf.getInt();
		ifIndex = buf.getInt();
		failureFlag = (buf.get() == 1);
		severity = buf.get();
		interferenceCUThreshold = buf.get();
		averageInterferenceCU = buf.get();
		shortTermInterferenceCU = buf.get();
		snapShotInterferenceCU = buf.get();
		crcErrorRateThreshold = buf.get();
		crcErrorRate = buf.get();
	}

	/**
	 * parse air screen report trap
	 *
	 * @param
	 *
	 * @return
	 */
	private void parseAirScreenReport(ByteBuffer buf) {
		buf.getShort(); // data length

		byte len = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
		eventTag1 = objectName + "-";

		len = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));

		reportType = buf.get();
		eventTag1 += reportType + "-";

		nameType = buf.get();
		eventTag1 += nameType + "-";

		len = buf.get();
		name = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
		sourceType = buf.get();
		eventTag1 += sourceType;

		len = buf.get();
		sourceID = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
		airScreenTime = AhDecoder.int2long(buf.getInt()) * 1000;
		len = buf.get();
		ruleName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
		instanceID = buf.getInt();
	}

	/**
	 * parse bandwidth sentinel event
	 *
	 * @param
	 *
	 * @return
	 */
	private void parseBandWidthSentinelEvent(ByteBuffer buf) {
		buf.getShort(); // data length

		byte len = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));

		len = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));

		code = buf.getInt();
		ifIndex = buf.getInt();
		remoteID = AhDecoder.bytes2hex(buf, 6).toUpperCase();
		bandwidthSentinelStatus = buf.getInt();
		guaranteedBandWidth = buf.getInt();
		actualBandWidth = buf.getInt();
		bandWidthAction = buf.getInt();

		if(buf.hasRemaining()){
			channelUltil = buf.get();
			interferenceUltil = buf.get();
			txUltil = buf.get();
			rxUltil = buf.get();
		}
	}

	/**
	 * parse vpn service event
	 *
	 * @param
	 *
	 * @return
	 */
	private void parseVPNEvent(ByteBuffer buf) {
		buf.getShort(); // data length

		byte len = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));

		len = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
	}

	private void parseSSIDStateChangeEvent(ByteBuffer buf) {
		buf.getShort(); // data length

		byte len = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));

		len = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));

		ifIndex = buf.getInt();
		clientBSSID = AhDecoder.bytes2hex(buf, 6).toUpperCase();

		len = buf.get();
		clientSSID = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));

		state = buf.get();
	}

	private void parseInterfaceClientTrap(ByteBuffer buf) {
		buf.getShort(); // data length

		byte len = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
		eventTag1 = objectName + "-";

		len = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));

		code = buf.getInt();
		eventTag1 += code + "-";

		sourceType = buf.get();
		eventTag1 += sourceType + "-";

		ifIndex = buf.getInt();
		eventTag1 += ifIndex + "-";

		remoteID = AhDecoder.bytes2hex(buf, 6).toUpperCase(); // client mac
		eventTag1 += remoteID + "-";

		len = buf.get();
		clientSSID = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
		eventTag1 += clientSSID + "-";

		alertType = buf.get();
		eventTag1 += alertType;

		thresholdValue = buf.getInt();
		shorttermValue = buf.getInt();
		snapshotValue = buf.getInt();
		severity = buf.get();
		failureFlag = (buf.get() == 1);
	}

	/**
	 * convert one client connect down trap to one connect change trap and one statistics result
	 * event.
	 *
	 *@param
	 *
	 *@return
	 */
	private void parseClientConnectDownTrap(ByteBuffer buf) {
		buf.getShort(); // data length

		byte len = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));

		code = buf.getInt();

		len = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));

		associationTime = AhDecoder.int2long(buf.getInt());
		ifIndex = buf.getInt();
		remoteID = AhDecoder.bytes2hex(buf, 6).toUpperCase(); // client mac

		// change trap type
		trapType = TYPE_CONNECTCHANGE;
		currentState = TRAP_STATE_DOWN_INDEX;
		objectType = TRAP_OBJECT_TYPE_CLIENT_INDEX;

		// create association bo
		AhAssociation associateBo = new AhAssociation();
		associateBo
				.setOwner(CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId()));
		associateBo.setTimeStamp(new HmTimeStamp(getMessageTimeStamp(), getMessageTimeZone()));
		associateBo.setApMac(apMac);
		associateBo.setApName(simpleHiveAp.getHostname());
		associateBo.setApSerialNumber(simpleHiveAp.getSerialNumber());
		associateBo.setIfIndex(ifIndex);
		associateBo.setClientMac(remoteID);

		// continue parse
		//special for client rssi
		associateBo.setSNR((short)buf.getInt());
		associateBo.setClientRSSI(associateBo.getSNR()-95);

		associateBo.setClientLinkUptime(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientAuthMethod(buf.get());
		associateBo.setClientEncryptionMethod(buf.get());
		associateBo.setClientMACProtocol(buf.get());
		associateBo.setClientCWPUsed(buf.get());
		associateBo.setClientVLAN(buf.getInt());
		associateBo.setClientUserProfId(buf.getInt());
		associateBo.setClientChannel(buf.getInt());
		associateBo.setClientLastTxRate(buf.getInt());
		associateBo.setClientLastRxRate(buf.getInt());
		associateBo.setClientRxDataFrames(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientRxDataOctets(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientRxMgtFrames(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientRxUnicastFrames(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientRxMulticastFrames(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientRxBroadcastFrames(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientRxMICFailures(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientTxDataFrames(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientTxMgtFrames(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientTxDataOctets(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientTxUnicastFrames(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientTxMulticastFrames(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientTxBroadcastFrames(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientIP(AhDecoder.int2IP(buf.getInt()));
		len = buf.get();
		associateBo.setClientHostname(AhDecoder.bytes2String(buf, AhDecoder.byte2int(len)).trim());
		len = buf.get();
		associateBo.setClientSSID(AhDecoder.bytes2String(buf, AhDecoder.byte2int(len)).trim());
		len = buf.get();
		associateBo.setClientUsername(AhDecoder.bytes2String(buf, AhDecoder.byte2int(len)).trim());
		associateBo.setClientTxBeDataFrames(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientTxBgDataFrames(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientTxViDataFrames(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientTxVoDataFrames(AhDecoder.int2long(buf.getInt()));
		associateBo.setClientRxAirtime(AhDecoder.long2double(buf.getLong()) / 1000);
		associateBo.setClientTxAirtime(AhDecoder.long2double(buf.getLong()) / 1000);
		associateBo.setClientBSSID(AhDecoder.bytes2hex(buf, 6));
		associateBo.setClientAssociateTime(AhDecoder.int2long(buf.getInt()));
		len = buf.get();
		associateBo.setIfName(AhDecoder.bytes2String(buf, AhDecoder.byte2int(len)).trim());

		// create statistic result event
		Map<Byte, List<HmBo>> statsRowData = new HashMap<Byte, List<HmBo>>();
		List<HmBo> associateRow = new ArrayList<HmBo>();
		associateRow.add(associateBo);
		statsRowData.put(BeCommunicationConstant.STATTABLE_AHASSOCIATION, associateRow);

		BeStatisticResultEvent statResultEvent = new BeStatisticResultEvent();
		statResultEvent.setApMac(apMac);
		statResultEvent.setSimpleHiveAp(simpleHiveAp);
		statResultEvent.setMessageTimeZone(getMessageTimeZone());
		statResultEvent.setMessageTimeStamp(getMessageTimeStamp());
		statResultEvent.setSequenceNum(sequenceNum);
		statResultEvent.setSerialNum(serialNum);
		statResultEvent.setStatsRowData(statsRowData);
		statResultEvent.setParsed(true);

		HmBeEventUtil.eventGenerated(statResultEvent);

		clientHostName = associateBo.getClientHostname();
		clientIP = associateBo.getClientIP();
		clientUserName = associateBo.getClientUsername();
		clientSSID  = associateBo.getClientSSID();
		clientCWPUsed = associateBo.getClientCWPUsed();
		clientAuthMethod = associateBo.getClientAuthMethod();
		clientEncryptionMethod = associateBo.getClientEncryptionMethod();
		clientMacProtocol = associateBo.getClientMACProtocol();
		clientVLAN = associateBo.getClientVLAN();
		clientUserProfId = associateBo.getClientUserProfId();
		clientChannel = associateBo.getClientChannel();
		clientBSSID = associateBo.getClientBSSID();
	}

	private void parseSecurityAlarmTrap(ByteBuffer buf) {
		buf.getShort(); // data length

		byte len = buf.get();
		objectName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));

		len = buf.get();
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));

		ifIndex = buf.getInt();
		AhDecoder.bytes2hex(buf, 6).toUpperCase(); // bssid
		AhDecoder.bytes2hex(buf, 6).toUpperCase(); // attack mac
		buf.getInt(); // attack count
		buf.getShort(); // protocol
		AhDecoder.int2IP(buf.getInt()); // target ip
		AhDecoder.int2IP(buf.getInt()); // source ip
		severity = buf.get();

		// special code
		probableCause = (byte) ifIndex;
	}

	private void parseClientOSInfoTrap(ByteBuffer buf) {
		buf.getShort(); //data length

		remoteID = AhDecoder.bytes2hex(buf, 6).toUpperCase(); // client mac
		byte len = buf.get();
		clientOsInfo = AhDecoder.bytes2StringForUtf8(buf, AhDecoder.byte2int(len));

		if(buf.hasRemaining()){
			len = buf.get();
			os_option55 = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
		}

	}

	private void parseDirectoryAlarmTrap(ByteBuffer buf) {
		buf.getShort(); // data length

		byte len = buf.get();// Describe length
		describe = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));// Descripe details

		reasonType = buf.get(); // 1:net join,2:bind dn

		severity = buf.get();
	}

	private  void parseAlarmTrap(ByteBuffer buf) {
		SimpleHiveAp hiveAp = getSimpleHiveAp();
		if(null == hiveAp) {
			DebugUtil.faultDebugWarn("BeTrapEvent::parseAlarmTrap:AP " + getApMac() + " not exist in chche!");
			return;
		}
		buf.getShort(); // data length

		short itemCount = buf.getShort();
		for(int i =0; i < itemCount; i++) {
			short itemLength = buf.getShort();
			int start = buf.position();

			AhAlarm alarm = new AhAlarm();
			short alarmId = buf.getShort();
			alarm.setSeverity(buf.get());
			byte descLen = buf.get();
			alarm.setTrapDesc(AhDecoder.bytes2String(buf, AhDecoder.byte2int(descLen)));
			alarm.setTag1(buf.getInt());
			alarm.setTag2(buf.getInt());
			byte tag3Len = buf.get();
			if(alarmId > 0 && alarmId <100) {
				alarm.setTag3(AhDecoder.bytes2hex(buf, tag3Len).toUpperCase());
			} else {
				alarm.setTag3(AhDecoder.bytes2String(buf, AhDecoder.byte2int(tag3Len)));
			}
			if(alarmId>0 && alarmId<=6){
				alarm.setAlarmType(BeFaultConst.ALARM_TYPE_CLIENT);
				alarm.setAlarmSubType(alarmId);
				alarm.setObjectName(BeFaultConst.TRAP_SEND_MAIL_TYPEX[18]);
				//setObjectName(BeFaultConst.TRAP_SEND_MAIL_TYPEX[18]);//for TrapToMailProcessThread
			} else if (BeFaultConst.ALARM_SUBTYPE_SYSTEM_POWER == alarmId
					|| BeFaultConst.ALARM_SUBTYPE_SYSTEM_SIM == alarmId
					|| BeFaultConst.ALARM_SUBTYPE_SYSTEM_IDM_PROXY == alarmId){
				alarm.setAlarmType(BeFaultConst.ALARM_TYPE_SYSTEM);
				alarm.setAlarmSubType(alarmId);
				alarm.setObjectName(BeFaultConst.TRAP_SEND_MAIL_TYPEX[20]);
			}

			alarm.setApId(hiveAp.getMacAddress());
			alarm.setApName(hiveAp.getHostname());
			alarm.setOwner(CacheMgmt.getInstance().getCacheDomainById(hiveAp.getDomainId()));
			alarm.setTrapTimeStamp(new HmTimeStamp(System.currentTimeMillis(), alarm.getOwner().getTimeZoneString()));



			alarmList.add(alarm);
			buf.position(start+ itemLength);
		}
	}

	private void parseCwpInfo(ByteBuffer buf) {
		buf.getShort(); // data length

		remoteID = AhDecoder.bytes2hex(buf, 6);

		int len = AhDecoder.byte2int(buf.get());
		objectName = AhDecoder.bytes2String(buf, len);

		len = AhDecoder.short2int(buf.getShort());
		describe = AhDecoder.bytes2String(buf, len);

		int itemCount = AhDecoder.byte2int(buf.get());
		StringBuffer strBuf = new StringBuffer();
		for(int i =0; i < itemCount; i++) {
			len = AhDecoder.byte2int(buf.get());
			if(i != 0)
				strBuf.append(";");
			strBuf.append(AhDecoder.bytes2String(buf,len));
		}
		cwpKeyValues = strBuf.toString();

		eventTag1 = objectName+"-"+remoteID;
	}

	public byte getReasonType() {
		return reasonType;
	}

	public void setReasonType(byte reasonType) {
		this.reasonType = reasonType;
	}

	public String getClientHostName() {
		return clientHostName;
	}

	public void setClientHostName(String clientHostName) {
		this.clientHostName = clientHostName;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public String getClientSSID() {
		return clientSSID;
	}

	public void setClientSSID(String clientSSID) {
		this.clientSSID = clientSSID;
	}

	public String getClientUserName() {
		return clientUserName;
	}

	public void setClientUserName(String clientUserName) {
		this.clientUserName = clientUserName;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public byte getCurrentState() {
		return currentState;
	}

	public void setCurrentState(byte currentState) {
		this.currentState = currentState;
	}

	public int getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public byte getObjectType() {
		return objectType;
	}

	public void setObjectType(byte objectType) {
		this.objectType = objectType;
	}

	public byte getPoEEth0On() {
		return poEEth0On;
	}

	public void setPoEEth0On(byte poEEth0On) {
		this.poEEth0On = poEEth0On;
	}

	public int getPoEEth0Pwr() {
		return poEEth0Pwr;
	}

	public void setPoEEth0Pwr(int poEEth0Pwr) {
		this.poEEth0Pwr = poEEth0Pwr;
	}

	public byte getPoEEth1On() {
		return poEEth1On;
	}

	public void setPoEEth1On(byte poEEth1On) {
		this.poEEth1On = poEEth1On;
	}

	public int getPoEEth1Pwr() {
		return poEEth1Pwr;
	}

	public void setPoEEth1Pwr(int poEEth1Pwr) {
		this.poEEth1Pwr = poEEth1Pwr;
	}

	public int getPowerSource() {
		return powerSource;
	}

	public void setPowerSource(int powerSource) {
		this.powerSource = powerSource;
	}

	public byte getPreviousState() {
		return previousState;
	}

	public void setPreviousState(byte previousState) {
		this.previousState = previousState;
	}

	public byte getProbableCause() {
		return probableCause;
	}

	public void setProbableCause(byte probableCause) {
		this.probableCause = probableCause;
	}

	public String getRemoteID() {
		return remoteID;
	}

	public void setRemoteID(String remoteID) {
		this.remoteID = remoteID;
	}

	public int getRequestSequenceNumber() {
		return requestSequenceNumber;
	}

	public void setRequestSequenceNumber(int requestSequenceNumber) {
		this.requestSequenceNumber = requestSequenceNumber;
	}

	// public int getSequenceNumber() {
	// return sequenceNumber;
	// }
	//
	// public void setSequenceNumber(int sequenceNumber) {
	// this.sequenceNumber = sequenceNumber;
	// }

	public byte getSeverity() {
		return severity;
	}

	public void setSeverity(byte severity) {
		this.severity = severity;
	}

	public int getThresholdhigh() {
		return thresholdhigh;
	}

	public void setThresholdhigh(int thresholdhigh) {
		this.thresholdhigh = thresholdhigh;
	}

	public int getThresholdlow() {
		return thresholdlow;
	}

	public void setThresholdlow(int thresholdlow) {
		this.thresholdlow = thresholdlow;
	}

	public byte getTrapType() {
		return trapType;
	}

	public void setTrapType(byte trapType) {
		this.trapType = trapType;
	}

	public int getRadioChannel() {
		return radioChannel;
	}

	public void setRadioChannel(int radioChannel) {
		this.radioChannel = radioChannel;
	}

	public int getRadioTxPower() {
		return radioTxPower;
	}

	public void setRadioTxPower(int radioTxPower) {
		this.radioTxPower = radioTxPower;
	}

	public byte getClientCWPUsed() {
		return clientCWPUsed;
	}

	public void setClientCWPUsed(byte clientCWPUsed) {
		this.clientCWPUsed = clientCWPUsed;
	}

	public byte getClientAuthMethod() {
		return clientAuthMethod;
	}

	public void setClientAuthMethod(byte clientAuthMethod) {
		this.clientAuthMethod = clientAuthMethod;
	}

	public byte getClientEncryptionMethod() {
		return clientEncryptionMethod;
	}

	public void setClientEncryptionMethod(byte clientEncryptionMethod) {
		this.clientEncryptionMethod = clientEncryptionMethod;
	}

	public byte getClientMacProtocol() {
		return clientMacProtocol;
	}

	public void setClientMacProtocol(byte clientMacProtocol) {
		this.clientMacProtocol = clientMacProtocol;
	}

	public int getClientVLAN() {
		return clientVLAN;
	}

	public void setClientVLAN(int clientVLAN) {
		this.clientVLAN = clientVLAN;
	}

	public int getClientUserProfId() {
		return clientUserProfId;
	}

	public void setClientUserProfId(int clientUserProfId) {
		this.clientUserProfId = clientUserProfId;
	}

	public int getClientChannel() {
		return clientChannel;
	}

	public void setClientChannel(int clientChannel) {
		this.clientChannel = clientChannel;
	}

	public String getClientBSSID() {
		return clientBSSID;
	}

	public void setClientBSSID(String clientBSSID) {
		this.clientBSSID = clientBSSID;
	}

	public byte getPoEEth0MaxSpeed() {
		return poEEth0MaxSpeed;
	}

	public void setPoEEth0MaxSpeed(byte poEEth0MaxSpeed) {
		this.poEEth0MaxSpeed = poEEth0MaxSpeed;
	}

	public byte getPoEEth1MaxSpeed() {
		return poEEth1MaxSpeed;
	}

	public void setPoEEth1MaxSpeed(byte poEEth1MaxSpeed) {
		this.poEEth1MaxSpeed = poEEth1MaxSpeed;
	}

	public byte getPoEWifi0Setting() {
		return poEWifi0Setting;
	}

	public void setPoEWifi0Setting(byte poEWifi0Setting) {
		this.poEWifi0Setting = poEWifi0Setting;
	}

	public byte getPoEWifi1Setting() {
		return poEWifi1Setting;
	}

	public void setPoEWifi1Setting(byte poEWifi1Setting) {
		this.poEWifi1Setting = poEWifi1Setting;
	}

	public byte getPoEWifi2Setting() {
		return poEWifi2Setting;
	}

	public void setPoEWifi2Setting(byte poEWifi2Setting) {
		this.poEWifi2Setting = poEWifi2Setting;
	}

	public long getAssociationTime() {
		//add protection, use current system time if association time is invalid
		if(associationTime < (System.currentTimeMillis()/1000 - 86400*365))
				associationTime = System.currentTimeMillis()/1000;
		return associationTime;
	}

	public void setAssociationTime(long associationTime) {
		this.associationTime = associationTime;
	}


	public int getClientRssi() {
		return clientRssi;
	}

	public void setClientRssi(int clientRssi) {
		this.clientRssi = clientRssi;
	}

	public short getSNR() {
		return SNR;
	}

	public void setSNR(short sNR) {
		SNR = sNR;
	}

	public byte getClientMacBasedAuthUsed() {
		return clientMacBasedAuthUsed;
	}

	public void setClientMacBasedAuthUsed(byte clientMacBasedAuthUsed) {
		this.clientMacBasedAuthUsed = clientMacBasedAuthUsed;
	}

	public String getUserProfileName() {
		return userProfileName;
	}

	public void setUserProfileName(String userProfileName) {
		this.userProfileName = userProfileName;
	}

	public boolean isFailureFlag() {
		return failureFlag;
	}

	public void setFailureFlag(boolean failureFlag) {
		this.failureFlag = failureFlag;
	}

	public byte getAverageInterferenceCU() {
		return averageInterferenceCU;
	}

	public void setAverageInterferenceCU(byte averageInterferenceCU) {
		this.averageInterferenceCU = averageInterferenceCU;
	}

	public byte getCrcErrorRate() {
		return crcErrorRate;
	}

	public void setCrcErrorRate(byte crcErrorRate) {
		this.crcErrorRate = crcErrorRate;
	}

	public byte getCrcErrorRateThreshold() {
		return crcErrorRateThreshold;
	}

	public void setCrcErrorRateThreshold(byte crcErrorRateThreshold) {
		this.crcErrorRateThreshold = crcErrorRateThreshold;
	}

	public byte getInterferenceCUThreshold() {
		return interferenceCUThreshold;
	}

	public void setInterferenceCUThreshold(byte interferenceCUThreshold) {
		this.interferenceCUThreshold = interferenceCUThreshold;
	}

	public byte getShortTermInterferenceCU() {
		return shortTermInterferenceCU;
	}

	public void setShortTermInterferenceCU(byte shortTermInterferenceCU) {
		this.shortTermInterferenceCU = shortTermInterferenceCU;
	}

	public byte getSnapShotInterferenceCU() {
		return snapShotInterferenceCU;
	}

	public void setSnapShotInterferenceCU(byte snapShotInterferenceCU) {
		this.snapShotInterferenceCU = snapShotInterferenceCU;
	}

	public int getInstanceID() {
		return instanceID;
	}

	public void setInstanceID(int instanceID) {
		this.instanceID = instanceID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getNameType() {
		return nameType;
	}

	public void setNameType(byte nameType) {
		this.nameType = nameType;
	}

	public byte getReportType() {
		return reportType;
	}

	public void setReportType(byte reportType) {
		this.reportType = reportType;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getSourceID() {
		return sourceID;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	public byte getSourceType() {
		return sourceType;
	}

	public void setSourceType(byte sourceType) {
		this.sourceType = sourceType;
	}

	public int getBandwidthSentinelStatus() {
		return bandwidthSentinelStatus;
	}

	public void setBandwidthSentinelStatus(int bandwidthSentinelStatus) {
		this.bandwidthSentinelStatus = bandwidthSentinelStatus;
	}

	public int getActualBandWidth() {
		return actualBandWidth;
	}

	public void setActualBandWidth(int actualBandWidth) {
		this.actualBandWidth = actualBandWidth;
	}

	public int getGuaranteedBandWidth() {
		return guaranteedBandWidth;
	}

	public void setGuaranteedBandWidth(int guaranteedBandWidth) {
		this.guaranteedBandWidth = guaranteedBandWidth;
	}

	public long getAirScreenTime() {
		return airScreenTime;
	}

	public void setAirScreenTime(long airScreenTime) {
		this.airScreenTime = airScreenTime;
	}

	public int getBandWidthAction() {
		return bandWidthAction;
	}

	public void setBandWidthAction(int bandWidthAction) {
		this.bandWidthAction = bandWidthAction;
	}

	public byte getChannelUltil() {
		return channelUltil;
	}

	public void setChannelUltil(byte channelUltil) {
		this.channelUltil = channelUltil;
	}

	public byte getInterferenceUltil() {
		return interferenceUltil;
	}

	public void setInterferenceUltil(byte interferenceUltil) {
		this.interferenceUltil = interferenceUltil;
	}

	public byte getTxUltil() {
		return txUltil;
	}

	public void setTxUltil(byte txUltil) {
		this.txUltil = txUltil;
	}

	public byte getRxUltil() {
		return rxUltil;
	}

	public void setRxUltil(byte rxUltil) {
		this.rxUltil = rxUltil;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public int getBeaconInterval() {
		return beaconInterval;
	}

	public void setBeaconInterval(int beaconInterval) {
		this.beaconInterval = beaconInterval;
	}

	public byte getAlertType() {
		return alertType;
	}

	public void setAlertType(byte alertType) {
		this.alertType = alertType;
	}

	public int getShorttermValue() {
		return shorttermValue;
	}

	public void setShorttermValue(int shorttermValue) {
		this.shorttermValue = shorttermValue;
	}

	public int getSnapshotValue() {
		return snapshotValue;
	}

	public void setSnapshotValue(int snapshotValue) {
		this.snapshotValue = snapshotValue;
	}

	public int getThresholdValue() {
		return thresholdValue;
	}

	public void setThresholdValue(int thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	public String getIfName() {
		return ifName;
	}

	public void setIfName(String ifName) {
		this.ifName = ifName;
	}

	public String getClientOsInfo() {
		return clientOsInfo;
	}

	public void setClientOsInfo(String clientOsInfo) {
		this.clientOsInfo = clientOsInfo;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	@Override
	public int compareTo(BeTrapEvent o) {
		if (this.remoteID == null || o.remoteID == null) {
			return (int)(this.timeStamp-o.timeStamp);
		}
		return this.remoteID.compareTo(o.getRemoteID());
	}
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof BeTrapEvent))
			return false;

		final BeTrapEvent event = (BeTrapEvent) o;

		if (remoteID != null ? !remoteID.equals(event.remoteID) : event.remoteID != null)
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result;
		result = (remoteID != null ? remoteID.hashCode() : 0);
		return result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		String TrapInfo = "";
		TrapInfo = TrapInfo + "ApId=" + this.apMac + " ";
		TrapInfo = TrapInfo + "ApName=" + this.apName + " ";
		TrapInfo = TrapInfo + "Severity=" + this.severity + " ";
		return TrapInfo;
		// return super.toString();
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public String getEventTag1() {
		return eventTag1;
	}

	public void setEventTag1(String eventTag1) {
		this.eventTag1 = eventTag1;
	}

	public int getAlarmTag1() {
		return alarmTag1;
	}

	public void setAlarmTag1(int alarmTag1) {
		this.alarmTag1 = alarmTag1;
	}

	public int getAlarmTag2() {
		return alarmTag2;
	}

	public void setAlarmTag2(int alarmTag2) {
		this.alarmTag2 = alarmTag2;
	}

	public String getAlarmTag3() {
		return alarmTag3;
	}

	public void setAlarmTag3(String alarmTag3) {
		this.alarmTag3 = alarmTag3;
	}

	public long getAvgDelay() {
		return avgDelay;
	}

	public void setAvgDelay(long avgDelay) {
		this.avgDelay = avgDelay;
	}

	public long getCurDelay() {
		return curDelay;
	}

	public void setCurDelay(long curDelay) {
		this.curDelay = curDelay;
	}

	public long getMinorThreshold() {
		return minorThreshold;
	}

	public void setMinorThreshold(long minorThreshold) {
		this.minorThreshold = minorThreshold;
	}

	public long getMajorThreshold() {
		return majorThreshold;
	}

	public void setMajorThreshold(long majorThreshold) {
		this.majorThreshold = majorThreshold;
	}

	public long getExpirationTime() {
		return expirationTime;
	}

	public String getEmail() {
		return email;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getOs_lenght() {
		return os_lenght;
	}

	public void setOs_lenght(String os_lenght) {
		this.os_lenght = os_lenght;
	}

	public String getOs_option55() {
		return os_option55;
	}

	public void setOs_option55(String os_option55) {
		this.os_option55 = os_option55;
	}

	public List<AhAlarm> getAlarmList() {
		return alarmList;
	}

	public void setAlarmList(List<AhAlarm> alarmList) {
		this.alarmList = alarmList;
	}

	public String getCwpKeyValues() {
		return cwpKeyValues;
	}

	public short getManagedStatus() {
		return managedStatus;
	}

	public void setManagedStatus(short managedStatus) {
		this.managedStatus = managedStatus;
	}

}
