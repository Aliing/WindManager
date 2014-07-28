package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhDecoder;

/**
 * AP connect message type
 *@filename		BeAPConnectEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-10 02:21:01
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 */
public class BeAPConnectEvent extends BeCommunicationEvent {

	/*
	 * Reconnect reasons
	 */
	public static final int CLIENT_STATUS_CHANGED = 1000;
	public static final int CLIENT_DTLS_STATUS_CHANGED = 1001;
	public static final int CLIENT_SERVER_PORT_CHANGED = 1002;
	public static final int CLIENT_IP_CHANGED = 1003;
	public static final int CLIENT_SERVER_NAME_CHANGED = 1004;
	public static final int CLIENT_HIVE_AP_REBOOTED = 1005;
	public static final int CLIENT_SERVER_TIMED_OUT = 1006;
	public static final int CLIENT_UNKNOWN = 1007;
	public static final int CLIENT_UPGRADE = 1008;
	public static final int CLIENT_CONFIG_ROLLBACK = 1009;
	public static final int CLIENT_TRANSFER_MODE_CHANGE = 1010;
	public static final int CLIENT_PROXY_INFO_CHANGE = 1011;
	public static final int CLIENT_PROXY_AUTH_CHANGE = 1012;
	public static final int CLIENT_PROXY_CONTENT_LEN_CHANGE = 1013;
	public static final int CLIENT_IMAGE_ROLLBACK = 1014;

	public String getReconnectDescription() {
		switch (reconnectReason) {
		case CLIENT_STATUS_CHANGED:
			return HmBeResUtil.getString("alarm.reconnectReason.1000.apStatusChanged");
		case CLIENT_DTLS_STATUS_CHANGED:
			return HmBeResUtil.getString("alarm.reconnectReason.1001.apDtlsStatusChanged");
		case CLIENT_SERVER_PORT_CHANGED:
			return HmBeResUtil.getString("alarm.reconnectReason.1002.apServerPortChanged");
		case CLIENT_IP_CHANGED:
			return HmBeResUtil.getString("alarm.reconnectReason.1003.apIpAddressChanged");
		case CLIENT_SERVER_NAME_CHANGED:
			return HmBeResUtil.getString("alarm.reconnectReason.1004.apServerNameChanged");
		case CLIENT_HIVE_AP_REBOOTED:
			return HmBeResUtil.getString("alarm.reconnectReason.1005.apRebooted");
		case CLIENT_SERVER_TIMED_OUT:
			if (null != previousCapwapClientIP && (!previousCapwapClientIP.equals(""))
					&& (!previousCapwapClientIP.equals(capwapClientIP))) {
				return HmBeResUtil
						.getString("alarm.reconnectReason.1006.apServerTimedOut.publicIpChanged");
			} else {
				return HmBeResUtil.getString("alarm.reconnectReason.1006.apServerTimedOut");
			}
		case CLIENT_UNKNOWN:
			return HmBeResUtil.getString("alarm.reconnectReason.Unknown");
		case CLIENT_UPGRADE:
			return HmBeResUtil.getString("alarm.reconnectReason.1008.apImageUpgraded");
		case CLIENT_CONFIG_ROLLBACK:
			return HmBeResUtil.getString("alarm.reconnectReason.1009.apConfigRollbacked");
		case CLIENT_TRANSFER_MODE_CHANGE:
			return HmBeResUtil.getString("alarm.reconnectReason.1010.apTransferModeChanged");
		case CLIENT_PROXY_INFO_CHANGE:
			return HmBeResUtil.getString("alarm.reconnectReason.1011.apProxyInfoChanged");
		case CLIENT_PROXY_AUTH_CHANGE:
			return HmBeResUtil.getString("alarm.reconnectReason.1012.apProxyAuthChanged");
		default:
			return HmBeResUtil.getString("alarm.reconnectReason.Unknown");
		}
	}
	
	
	public static final int REGION_CODE_FCC = 0;
	
	public static final int REGION_CODE_WORLD = 1;

	private static final long serialVersionUID = 1L;

	private String ipAddr;

	private String netmask;

	private String gateway;

	private String apSerialNum = "";

	private byte ipType;

	private byte APType;

	private int countryCode;

	private int regionCode;

	private String wtpName = "";

	private String softVersion = "";

	private String productName = "";

	private String location = "";

	private boolean connectState;

	private byte dtlsState;

	private byte passPhraseType;

	private int configVersion;

	private String hivemanagerIP;

	private String capwapClientIP;

	private String previousCapwapClientIP;

	public static final boolean CONNECTSTATE_CONNECT = true;

	public static final boolean CONNECTSTATE_DISCONNECT = false;

	private int reconnectReason;

	private long upTime;

	public static final byte FLAG_NOCOREDUMPFILE = 0;

	public static final byte FLAG_EXISTSCOREDUMPFILE = 1;

	// 0:no core dump file, 1: have core dump files
	private byte coreDumpFlag;

	private String displayVersion;

	private String domainName;

	private boolean isSimulate;

	private int simulateCode;

	private String simulateClientInfo;

	private short wifi0Channel;

	private short wifi0Power;

	private short wifi1Channel;

	private short wifi1Power;

	private byte capwapClientType = BeCommunicationConstant.CAPWAPCLIENTTYPE_AP;

	private int apCount;

	private int vhmCount;

	private String hiveName;

	public static final byte TRANSFERMODE_TCP = 2;

	public static final byte TRANSFERMODE_UDP = 1;

	private byte transferMode;

	private int connectPort;

	private String proxyName;

	private int proxyPort;

	private String proxyUserName;

	private String proxyPassword;
	
	private byte  indoorFlag;
	
	public static final byte PPPOE_STATUS_DISABLE	= 0;
	public static final byte PPPOE_STATUS_ENABLE	= 1;
	
	private byte  ppp;
	
	private byte  pppoe = PPPOE_STATUS_DISABLE;
	
	public static final byte TYPE_FLAG_AP	= 0;
	public static final byte TYPE_FLAG_BR	= 1;
	public static final byte TYPE_FLAG_CVG	= 2;
	
	private byte  typeFlag = TYPE_FLAG_AP;
	
	private int		l7SignatureFileVersion;
	
	private int		switchChipVersion;
	
	private int     flag = 0;
	
	private byte	cliError = 0;
	
	private byte 	netdumpFlag = 0;
	
	private byte	rebootType = -1;
	
	private long	rebootTimestamp = 0;
	
	private String	hardwareRevision;
	
	public BeAPConnectEvent() {
		super();
		msgType = connectState ? BeCommunicationConstant.MESSAGETYPE_APCONNECT
				: BeCommunicationConstant.MESSAGETYPE_APDISCONNECT;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data
	 *            -
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			ByteBuffer buf = ByteBuffer.wrap(data);

			while (buf.hasRemaining()) {
				short msgType = buf.getShort();
				int length = buf.getInt();
				int position = buf.position();
				
				// Ap connect event's data field
				// 1. AP Identifier
				// 2. AP descriptor
				if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER) {
					byte macLen = buf.get();
					apMac = AhDecoder.bytes2String(buf, AhDecoder.byte2int(macLen)).toUpperCase();
				} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_APDESCRIPTOR) {
					ipAddr = AhDecoder.int2IP(buf.getInt());
					netmask = AhDecoder.int2IP(buf.getInt());
					gateway = AhDecoder.int2IP(buf.getInt());
					countryCode = buf.getInt();
					regionCode = buf.getInt();
					ipType = buf.get();
					APType = buf.get();
					byte apSerialNumLen = buf.get();
					apSerialNum = AhDecoder.bytes2String(buf, AhDecoder.byte2int(apSerialNumLen));
					byte wtpNameLength = buf.get();
					wtpName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(wtpNameLength));
					byte softVersionLength = buf.get();
					softVersion = AhDecoder
							.bytes2String(buf, AhDecoder.byte2int(softVersionLength));
					short locationLength = buf.getShort();
					location = AhDecoder.bytes2String(buf, AhDecoder.short2int(locationLength));
					dtlsState = buf.get();
					passPhraseType = buf.get();
					byte hdVersionLen = buf.get();
					productName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(hdVersionLen));
					configVersion = buf.getInt();
					hivemanagerIP = AhDecoder.int2IP(buf.getInt());
					capwapClientIP = AhDecoder.int2IP(buf.getInt());
					reconnectReason = buf.getInt();
					upTime = AhDecoder.int2long(buf.getInt());
					coreDumpFlag = buf.get();
					byte len = buf.get();
					displayVersion = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
					len = buf.get();
					domainName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
					len = buf.get();
					hiveName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
					transferMode = buf.get(); // 1: udp, 2: tcp
					connectPort = buf.getInt();
					len = buf.get();
					proxyName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
					proxyPort = buf.getInt();
					len = buf.get();
					proxyUserName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
					len = buf.get();
					proxyPassword = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
					isSimulate = (buf.get() == 1);
					simulateCode = AhDecoder.short2int(buf.getShort());
					short infoLen = buf.getShort();
					simulateClientInfo = AhDecoder.bytes2String(buf, AhDecoder.short2int(infoLen));
					wifi0Channel = buf.getShort();
					wifi0Power = buf.getShort();
					wifi1Channel = buf.getShort();
					wifi1Power = buf.getShort();
					capwapClientType = buf.get();
					apCount = buf.getInt();
					vhmCount = buf.getInt();
					if((buf.position()-position) < length)
						indoorFlag = buf.get();
					if((buf.position()-position) < length) {
						ppp = buf.get();
						pppoe = buf.get();
					}
					if((buf.position()-position) < length)
						typeFlag = buf.get();
					
					if((buf.position()-position) < length)
						//hastatus
						buf.get();
					if((buf.position()-position) < length) {
						l7SignatureFileVersion = buf.getInt();
					}
					if((buf.position()-position) < length) {
						switchChipVersion = buf.getInt();
					}
					if((buf.position()-position) < length) {
						flag = buf.getInt();
						cliError = (byte)(flag & 0x1);
						netdumpFlag = (byte)((flag & 0x2) != 0 ? 1 : 0);
					}
					if((buf.position()-position) < length) {
						rebootType = buf.get();
						rebootTimestamp = AhDecoder.int2long(buf.getInt()) * 1000;
					}
					if((buf.position()-position) < length) {
						len = buf.get();
						hardwareRevision = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
					}
					
				} else {
					throw new BeCommunicationDecodeException(
							"Invalid message element type in BeAPConnectEvent, type value = "
									+ msgType);
				}
				buf.position(position+length);
				// if ap exists in cache, set previous capwap client ip for fault module
				SimpleHiveAp hiveAP_cache = getSimpleHiveAp();
				if (hiveAP_cache != null) {
					previousCapwapClientIP = hiveAP_cache.getCapwapClientIp();
				}
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeAPConnectEvent.parsePacket() catch exception", e);
		}
	}

	public String getProxyName() {
		return proxyName;
	}

	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUserName() {
		return proxyUserName;
	}

	public void setProxyUserName(String proxyUserName) {
		this.proxyUserName = proxyUserName;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public boolean isConnectState() {
		return connectState;
	}

	public void setConnectState(boolean connectState) {
		this.connectState = connectState;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String hardwareVersion) {
		this.productName = hardwareVersion;
	}

	public byte getDtlsState() {
		return dtlsState;
	}

	public void setDtlsState(byte dtlsState) {
		this.dtlsState = dtlsState;
	}

	public byte getPassPhraseType() {
		return passPhraseType;
	}

	public void setPassPhraseType(byte passPhraseType) {
		this.passPhraseType = passPhraseType;
	}

	public byte getAPType() {
		return APType;
	}

	public void setAPType(byte type) {
		APType = type;
	}

	public int getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(int countryCode) {
		this.countryCode = countryCode;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public byte getIpType() {
		return ipType;
	}

	public void setIpType(byte ipType) {
		this.ipType = ipType;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getApSerialNum() {
		return apSerialNum;
	}

	public void setApSerialNum(String macAddr) {
		this.apSerialNum = macAddr;
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	public int getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(int regionCode) {
		this.regionCode = regionCode;
	}

	public String getSoftVersion() {
		return softVersion;
	}

	public void setSoftVersion(String softVersion) {
		this.softVersion = softVersion;
	}

	public String getWtpName() {
		return wtpName;
	}

	public void setWtpName(String wtpName) {
		this.wtpName = wtpName;
	}

	public int getConfigVersion() {
		return configVersion;
	}

	public void setConfigVersion(int configVersion) {
		this.configVersion = configVersion;
	}

	public String getHivemanagerIP() {
		return hivemanagerIP;
	}

	public void setHivemanagerIP(String hivemanagerIP) {
		this.hivemanagerIP = hivemanagerIP;
	}

	public int getReconnectReason() {
		return reconnectReason;
	}

	public void setReconnectReason(int reconnectReason) {
		this.reconnectReason = reconnectReason;
	}

	public long getUpTime() {
		return upTime;
	}

	public void setUpTime(long upTime) {
		this.upTime = upTime;
	}

	public byte getCoreDumpFlag() {
		return coreDumpFlag;
	}

	public void setCoreDumpFlag(byte coreDumpFlag) {
		this.coreDumpFlag = coreDumpFlag;
	}

	public String getCapwapClientIP() {
		return capwapClientIP;
	}

	public void setCapwapClientIP(String capwapClientIP) {
		this.capwapClientIP = capwapClientIP;
	}

	public String getPreviousCapwapClientIP() {
		return previousCapwapClientIP;
	}

	public void setPreviousCapwapClientIP(String previousCapwapClientIP) {
		this.previousCapwapClientIP = previousCapwapClientIP;
	}

	public String getDisplayVersion() {
		return displayVersion;
	}

	public void setDisplayVersion(String displayVersion) {
		this.displayVersion = displayVersion;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public boolean isSimulate() {
		return isSimulate;
	}

	public void setSimulate(boolean isSimulate) {
		this.isSimulate = isSimulate;
	}

	public String getSimulateClientInfo() {
		return simulateClientInfo;
	}

	public void setSimulateClientInfo(String simulateClientInfo) {
		this.simulateClientInfo = simulateClientInfo;
	}

	public int getSimulateCode() {
		return simulateCode;
	}

	public void setSimulateCode(int simulateCode) {
		this.simulateCode = simulateCode;
	}

	public short getWifi0Channel() {
		return wifi0Channel;
	}

	public void setWifi0Channel(short wifi0Channel) {
		this.wifi0Channel = wifi0Channel;
	}

	public short getWifi0Power() {
		return wifi0Power;
	}

	public void setWifi0Power(short wifi0Power) {
		this.wifi0Power = wifi0Power;
	}

	public short getWifi1Channel() {
		return wifi1Channel;
	}

	public void setWifi1Channel(short wifi1Channel) {
		this.wifi1Channel = wifi1Channel;
	}

	public short getWifi1Power() {
		return wifi1Power;
	}

	public void setWifi1Power(short wifi1Power) {
		this.wifi1Power = wifi1Power;
	}

	public byte getCapwapClientType() {
		return capwapClientType;
	}

	public void setCapwapClientType(byte capwapClientType) {
		this.capwapClientType = capwapClientType;
	}

	public int getApCount() {
		return apCount;
	}

	public void setApCount(int apCount) {
		this.apCount = apCount;
	}

	public int getVhmCount() {
		return vhmCount;
	}

	public void setVhmCount(int vhmCount) {
		this.vhmCount = vhmCount;
	}

	public int getConnectPort() {
		return connectPort;
	}

	public void setConnectPort(int connectPort) {
		this.connectPort = connectPort;
	}

	public String getHiveName() {
		return hiveName;
	}

	public void setHiveName(String hiveName) {
		this.hiveName = hiveName;
	}

	public byte getTransferMode() {
		return transferMode;
	}

	public void setTransferMode(byte transferMode) {
		this.transferMode = transferMode;
	}

	public byte getIndoorFlag() {
		return indoorFlag;
	}

	public void setIndoorFlag(byte indoorFlag) {
		this.indoorFlag = indoorFlag;
	}

	public byte getPpp() {
		return ppp;
	}

	public void setPpp(byte ppp) {
		this.ppp = ppp;
	}

	public byte getPppoe() {
		return pppoe;
	}

	public void setPppoe(byte pppoe) {
		this.pppoe = pppoe;
	}
	
	public boolean isEnablePppoe(){
		return this.pppoe == PPPOE_STATUS_ENABLE;
	}

	public byte getTypeFlag() {
		return typeFlag;
	}

	public void setTypeFlag(byte typeFlag) {
		this.typeFlag = typeFlag;
	}
	
	public boolean isTypeFlagBR(){
		return this.typeFlag == TYPE_FLAG_BR;
	}
	
	public int getL7SignatureFileVersion() {
		return l7SignatureFileVersion;
	}

	public int getSwitchChipVersion() {
		return switchChipVersion;
	}

	public void setSwitchChipVersion(int switchChipVersion) {
		this.switchChipVersion = switchChipVersion;
	}

	public byte getCliError() {
		return cliError;
	}
	
	public byte getNetdumpFlag() {
		return netdumpFlag;
	}

	public byte getRebootType() {
		return rebootType;
	}

	public long getRebootTimestamp() {
		return rebootTimestamp;
	}

	public String getHardwareRevision() {
		return hardwareRevision;
	}

	
}