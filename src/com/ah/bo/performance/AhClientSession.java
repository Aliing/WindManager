package com.ah.bo.performance;

import java.sql.Timestamp;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import org.hibernate.annotations.Index;

import com.ah.be.common.ConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.coder.AhDecoder;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "AH_CLIENTSESSION")
@org.hibernate.annotations.Table(appliesTo = "AH_CLIENTSESSION", indexes = {
		@Index(name = "CLIENT_SESSION_OWNER", columnNames = { "OWNER" }),
		@Index(name = "CLIENT_SESSION_CLIENT_MAC", columnNames = { "CLIENTMAC" }),
		@Index(name = "CLIENT_SESSION_AP_MAC", columnNames = { "APMAC" })
		})
public class AhClientSession implements HmBo {

	public static final String SSID_SEND_UP_FROM_DEVICE_FOR_AP_WIRED_CLIENT = "default security-obj";
	
	private static final long	serialVersionUID		= 1L;
	private static final String	SEPARATOR				= "+";
	private static final String	SPACE					= " ";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long				id;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String				apName;

	@Column(length = MAC_ADDRESS_LENGTH)
	private String				apMac;

	@Column(length = 14)
	private String				apSerialNumber;

	private int					ifIndex;

	private String				ifName;
	
	private String				clientOsInfo;


	// @Column(length = 48, unique = true, nullable = false)
	@Column(length = MAC_ADDRESS_LENGTH, nullable = false)
	private String				clientMac;

	public static final byte	CONNECT_STATE_DOWN		= 0;
	public static final byte	CONNECT_STATE_UP		= 1;

	private byte				connectstate			= CONNECT_STATE_UP;

	// private int clientRSSI;

	private byte				clientAuthMethod;

	private byte				clientEncryptionMethod;

	// default "802.11b"
	private byte				clientMACProtocol		= 1;

	private int					clientVLAN;

	private int					clientUserProfId;

	private int					clientChannel;

	// private int clientLastTxRate;
	//
	// private int clientLastRxRate;

	@Column(length = 32)
	private String				clientHostname			= "";

	@Column(length = 32)
	private String				clientSSID				= "";

	private String				clientIP;
	@Transient
    private String              clientNatIP="";
	
	@Column(length = 255)
	private String				clientUsername;

	private long				startTimeStamp;

	private long				endTimeStamp;

	private String				startTimeZone			= TimeZone.getDefault().getID();

	private String				endTimeZone				= TimeZone.getDefault().getID();

	private String				memo;

	private byte				clientCWPUsed;

	private Long				mapId;

	@Column(length = 32)
	private String				comment1;

	@Column(length = 32)
	private String				comment2;

	// mac address
	private String				clientBSSID				= "";
	
	private int					clientRssi;

	private int					bandWidthSentinelStatus	= AhBandWidthSentinelHistory.STATUS_CLEAR;

	private boolean				simulated;

	public static byte			CLIENT_SCORE_RED		= 25;
	public static byte			CLIENT_SCORE_YELLOW		= 50;
	private byte				slaConnectScore			= 100;
	
	private byte				ipNetworkConnectivityScore=100;

	private byte				applicationHealthScore=100;
	
	private byte				overallClientHealthScore=100;
	
	private String				email;
	
	private String				companyName;
	
	private boolean				wirelessClient;

	@Transient
	private int					rssiCount;

	@Transient
	private String				mapName;

	// indicate under client monitoring
	@Transient
	private boolean				monitoring;

	@Transient
	private long				last2HourData;
	
	@Column(length = 256)
	private String				os_option55;
	
	private String				userProfileName;
	
	private short				SNR;
	
	private byte				clientMacBasedAuthUsed;
	
	private short 				managedStatus;
	
	@Transient
	private boolean				clientEnrolled;
	
	@Transient
	private String				clientEnrolledURL;
	
	@Transient
	private String				customerID;
	@Transient
	private String				enrolledCMURL;
	@Transient
	private String				enrolledSLURL;
	@Transient
	private String				enrolledIDMURL;
	
	public boolean getDisplayEnrolledCMIcon(){
		if (((managedStatus & 0x01) ==1) || clientEnrolled) {
			return true;
		}
		return false;
	}
	
	public boolean getDisplayEnrolledSLIcon(){
		if (((managedStatus >>>1 & 0x01) ==1)) {
			return true;
		}
		return false;
	}
	
	public boolean getDisplayEnrolledIDMIcon(){
		if (((managedStatus >>>2 & 0x01) ==1)) {
			return true;
		}
		return false;
	}
	
	public String getEnrolledCMURL() {
		return enrolledCMURL==null? "": enrolledCMURL + "/monitor/device/view?customerId=" + customerID +"&clientMac=" + getClientMac();
	}
	
	public String getEnrolledSLURL() {
		return enrolledSLURL==null? "" : enrolledSLURL + "/guests/viewClients?customerId=" + customerID +"&clientMac=" + getClientMac();
	}
	
	public String getEnrolledIDMURL() {
		return enrolledIDMURL==null ? "" : enrolledIDMURL + "/monitor/logs/viewClients?customerId=" + customerID +"&clientMac=" + getClientMac();
	}
	
	@Transient
	public boolean isOsOption55Exist() {
		return StringUtils.isNotBlank(os_option55);
	}

	public String getOs_option55() {
		return os_option55;
	}

	public void setOs_option55(String os_option55) {
		this.os_option55 = os_option55;
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
	
	@Transient
	public String getClientOsInfoInDb() {
		if (null != clientOsInfo && !"".equals(clientOsInfo)) {
			List<?> detailInfo = QueryUtil.executeNativeQuery("select description from os_object_version where upper(osversion) = '"
				+ NmsUtil.convertSqlStr(clientOsInfo.toUpperCase())+"' order by os_object_id");
			for (Object obj : detailInfo) {
				String osInfo = (String)obj;
				if (!"".equals(osInfo)) {
					return osInfo;
				}
			}
		}
		return clientOsInfo;
	}

	public void setClientOsInfo(String clientOsInfo) {
		this.clientOsInfo = clientOsInfo;
	}

	public boolean isMonitoring() {
		return monitoring;
	}

	public void setMonitoring(boolean monitoring) {
		this.monitoring = monitoring;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public int getRssiCount() {
		return rssiCount;
	}

	public void setRssiCount(int rssiCount) {
		this.rssiCount = rssiCount;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public String getClientMac() {
		return clientMac;
	}

	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}

	public String getClientUsername() {
		return clientUsername;
	}

	public String getClientNatIP() {
		return clientNatIP;
	}
	public void setClientNatIP(String clientNatIP) {
		this.clientNatIP = clientNatIP;
	}
	public void setClientUsername(String clientUsername) {
		this.clientUsername = clientUsername;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	// public String getStartTimeShow() {
	// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// return formatter.format(startTime);
	// }

	// public Date getStartTime() {
	// return startTime;
	// }
	//
	// public void setStartTime(Date startTime) {
	// this.startTime = startTime;
	// }

	// --------implement interface function--------
	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return "currentClientSession";
	}

	// For multi page selection
	@Transient
	private boolean	selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/*
	 * statistic objects have no owner or version,because it created by system, it can't be updated
	 * and 'statTime' keep track of when create it.
	 */
	/**
	 * modify mark: add owner field for VHM
	 */

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public String getApSerialNumber() {
		return apSerialNumber;
	}

	public void setApSerialNumber(String apSerialNumber) {
		this.apSerialNumber = apSerialNumber;
	}

	public byte getClientAuthMethod() {
		return clientAuthMethod;
	}

	public void setClientAuthMethod(byte clientAuthMethod) {
		this.clientAuthMethod = clientAuthMethod;
	}

	public int getClientChannel() {
		return clientChannel;
	}

	public String getClientChannelString() {
		if(wirelessClient){
			if (clientChannel == 0) {
	//			return "Unknown";
				return "";
			}
			return String.valueOf(AhDecoder.int2long(clientChannel));
		} else {
			return "";
		}
	}

	public String getClientChannel4Show() {
		return String.valueOf(AhDecoder.int2long(clientChannel));
	}

	public void setClientChannel(int clientChannel) {
		this.clientChannel = clientChannel;
		wirelessClient = this.clientChannel != 0;
	}
	
	public boolean isWirelessClient() {
		return wirelessClient;
	}

	public void setWirelessClient(boolean wirelessClient) {
		this.wirelessClient = wirelessClient;
	}

	public byte getClientEncryptionMethod() {
		return clientEncryptionMethod;
	}

	public void setClientEncryptionMethod(byte clientEncryptionMethod) {
		this.clientEncryptionMethod = clientEncryptionMethod;
	}

	public String getClientHostname() {
		return clientHostname;
	}

	public void setClientHostname(String clientHostname) {
		this.clientHostname = clientHostname;
	}

	// public int getClientLastTxRate() {
	// return clientLastTxRate;
	// }
	//
	// public String getClientLastTxRate4Show() {
	// return String.valueOf(AhDecoder.int2long(clientLastTxRate));
	// }
	//
	// public void setClientLastTxRate(int clientLastTxRate) {
	// this.clientLastTxRate = clientLastTxRate;
	// }

	public byte getClientMACProtocol() {
		return clientMACProtocol;
	}

	public void setClientMACProtocol(byte clientMACProtocol) {
		this.clientMACProtocol = clientMACProtocol;
	}

	// public int getClientRSSI() {
	// return clientRSSI;
	// }
	//
	// public String getClientRSSI4Show() {
	// // return String.valueOf(AhDecoder.int2long(clientRSSI));
	// // rssi is a negative number (dbm)
	// return String.valueOf(clientRSSI);
	// }
	//
	// public void setClientRSSI(int clientRSSI) {
	// this.clientRSSI = clientRSSI;
	// }

	public String getClientSSID() {
		return clientSSID;
	}

	public String getClientSSIDForUi() {
		if (SSID_SEND_UP_FROM_DEVICE_FOR_AP_WIRED_CLIENT.equalsIgnoreCase(clientSSID)) {
			// fix bug 33531, if the client is AP wired client, should not show SSID like 'default security-obj', but blank.
			return "";
		}
		return clientSSID;
	}

	public void setClientSSID(String clientSSID) {
		this.clientSSID = clientSSID;
	}

	public int getClientUserProfId() {
		return clientUserProfId;
	}

	public String getClientUserProfId4Show() {
		return String.valueOf(AhDecoder.int2long(clientUserProfId));
	}

	public void setClientUserProfId(int clientUserProfId) {
		this.clientUserProfId = clientUserProfId;
	}

	public int getClientVLAN() {
		return clientVLAN;
	}

	public String getClientVLANString() {
		if (clientVLAN == 0) {
			return "Unknown";
		}
		return String.valueOf(clientVLAN);
	}

	// public String getClientVLAN4Show() {
	// return String.valueOf(AhDecoder.int2long(clientVLAN));
	// }

	public void setClientVLAN(int clientVLAN) {
		this.clientVLAN = clientVLAN;
	}

	@Transient
	public String getStartTimeString() {
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			return AhDateTimeUtil.getSpecifyDateTime(startTimeStamp, TimeZone
					.getTimeZone(startTimeZone), loginUser != null ? loginUser : owner);
		}else{
			return AhDateTimeUtil.getSpecifyDateTime(startTimeStamp, TimeZone
					.getTimeZone(startTimeZone));
		}
	}

	@Transient
	public String getEndTimeShow() {
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			return AhDateTimeUtil.getSpecifyDateTime(endTimeStamp, TimeZone
					.getTimeZone(endTimeZone), loginUser != null ? loginUser : owner);
		}else{
			return AhDateTimeUtil.getSpecifyDateTime(endTimeStamp, TimeZone
					.getTimeZone(endTimeZone));
		}
	}

	@Transient
	public String getDurationString() {
		if (startTimeStamp > 0) {
			long duration;
			if (endTimeStamp == 0) {
				duration = System.currentTimeMillis() - startTimeStamp;
			} else {
				duration = endTimeStamp - startTimeStamp;
			}

			return NmsUtil.transformTime((int) (duration / 1000));
		}

		return "N/A";
	}

	@Transient
	public String getClientMacPtlString() {
		if(wirelessClient){
			if (ifName != null && ifName.toLowerCase().contains("eth")) {
				return "Ethernet";
			}
	
			switch (clientMACProtocol) {
			case AhAssociation.CLIENTMACPROTOCOL_AMODE:
			case AhAssociation.CLIENTMACPROTOCOL_BMODE:
			case AhAssociation.CLIENTMACPROTOCOL_GMODE:
			case AhAssociation.CLIENTMACPROTOCOL_NAMODE:
			case AhAssociation.CLIENTMACPROTOCOL_NGMODE:
			case AhAssociation.CLIENTMACPROTOCOL_ACMODE:
			case AhAssociation.CLIENT_MAC_PROTOCOL_8023MODE:
				return MgrUtil.getEnumString("enum.snmp.association.macProtocol." + clientMACProtocol);
			default:
				return "Unknown";
			}
		} else {
			return "";
		}
	}

	@Transient
	public static byte getclientRadioTypeByString(String PltString) {
		if("2.4G".equals(PltString)){
			return AhInterfaceStats.RADIOTYPE_24G;
		}
		return AhInterfaceStats.RADIOTYPE_5G;
	}
	
	@Transient
	public String getClientAuthMethodString() {
		String authMethod = "";
		switch (clientAuthMethod) {
		case AhAssociation.CLIENTAUTHMETHOD_OPEN:
		case AhAssociation.CLIENTAUTHMETHOD_WEPOPEN:
		case AhAssociation.CLIENTAUTHMETHOD_WEPSHARED:
		case AhAssociation.CLIENTAUTHMETHOD_WPAPSK:
		case AhAssociation.CLIENTAUTHMETHOD_WPA2PSK:
		case AhAssociation.CLIENTAUTHMETHOD_WPA8021X:
		case AhAssociation.CLIENTAUTHMETHOD_WPA28021X:
		case AhAssociation.CLIENTAUTHMETHOD_WPAAUTOPSK:
		case AhAssociation.CLIENTAUTHMETHOD_WPAAUTO8021X:
		case AhAssociation.CLIENTAUTHMETHOD_DYNAMICWEP:
		case AhAssociation.CLIENTAUTHMETHOD_8021X:
			authMethod = MgrUtil
					.getEnumString("enum.snmp.association.authentication." + clientAuthMethod);
			break;
		default:
			authMethod = "Unknown";
		}
		
		// CWP
		if (AhAssociation.CLIENT_CWP_USED == clientCWPUsed) {
			if (!StringUtils.isEmpty(authMethod)) {
				authMethod += SPACE + SEPARATOR + SPACE;
			}
			authMethod += "CWP";
		}
		
		// macAuth
		if (AhAssociation.CLIENT_MAC_BASED_AUTH_USED == clientMacBasedAuthUsed) {
			if (!StringUtils.isEmpty(authMethod)) {
				authMethod += SPACE + SEPARATOR + SPACE;
			}
			authMethod += "MAC";
		}
		
		return authMethod;
	}

	@Transient
	public String getClientEncryptionMethodString() {
		if(wirelessClient){
			switch (clientEncryptionMethod) {
			case AhAssociation.CLIENTENCRYMETHOD_AES:
			case AhAssociation.CLIENTENCRYMETHOD_TKIP:
			case AhAssociation.CLIENTENCRYMETHOD_WEP:
			case AhAssociation.CLIENTENCRYMETHOD_NON:
				return MgrUtil.getEnumString("enum.snmp.association.encryption."
						+ clientEncryptionMethod);
			default:
				return "Unknown";
			}
		} else {
			return "";
		}
	}

	@Transient
	public String getConnectStateString() {
		return MgrUtil.getEnumString("enum.clientConnectstate."
				+ connectstate);
	}

	public byte getClientCWPUsed() {
		return clientCWPUsed;
	}

	public void setClientCWPUsed(byte clientCWPUsed) {
		this.clientCWPUsed = clientCWPUsed;
	}

	public String getClientCWPUsedString() {
		if (clientCWPUsed == 0) {
			return "";
		} else {
			return MgrUtil.getEnumString("enum.snmp.association.cwp." + clientCWPUsed);
		}
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
	}

	public byte getConnectstate() {
		return connectstate;
	}

	public void setConnectstate(byte connectstate) {
		this.connectstate = connectstate;
	}

	// public Date getEndTime() {
	// return endTime;
	// }
	//
	//
	// public void setEndTime(Date endTime) {
	// this.endTime = endTime;
	// }

	public Long getMapId() {
		return mapId;
	}

	public void setMapId(Long mapId) {
		this.mapId = mapId;
	}

	public String getComment1() {
		return comment1;
	}

	public void setComment1(String comment1) {
		this.comment1 = comment1;
	}

	public String getComment2() {
		return comment2;
	}

	public void setComment2(String comment2) {
		this.comment2 = comment2;
	}

	public String getClientBSSID() {
		if(wirelessClient){
			return clientBSSID;
		} else {
			return "";
		}
	}

	public void setClientBSSID(String clientBSSID) {
		this.clientBSSID = clientBSSID;
	}

	public int getClientRssi() {
		// if HiveOS version is older than 5.1r4, use SNR and noiseFloor&SNR to calculate RSSI value
		SimpleHiveAp hiveAp = CacheMgmt.getInstance().getSimpleHiveAp(this.getApMac());
		if (hiveAp != null
				&& NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "5.1.4.0") < 0) {
			Long noiseFloor = -95L;
			String where = "radioChannel = :s1 and apMac = :s2";
			Object[] values = new Object[2];
			values[0] = Long.valueOf(this.getClientChannel()) ;
			values[1] = this.getApMac() ;
			
			List<?> radioNoiseFloors = QueryUtil.executeQuery("select radioNoiseFloor from "
				+ AhLatestRadioAttribute.class.getSimpleName(), new SortParams("time", false),
				new FilterParams(where, values),owner.getId());
			if(radioNoiseFloors != null
					&& !radioNoiseFloors.isEmpty()){
				noiseFloor = (Long)radioNoiseFloors.get(0);
			}
			
			return this.SNR + noiseFloor.intValue();
		}
		
		// can use RSSI value directly from HiveOS version 5.1r4
		return clientRssi;
	}

	public void setClientRssi(int clientRssi) {
		this.clientRssi = clientRssi;
	}
	
	//bug fix for 20142, use rssi&snr value sent by HiveOS 
	public String getClientRSSI4Show() {
		if(wirelessClient){
			if (ifName != null && ifName.toLowerCase().contains("eth")) {
				return "N/A";
			}
			
			if (this.getClientRssi()==0) {
				if (this.getClientIP()==null || this.getClientIP().isEmpty()
						|| this.getClientIP().equals("0.0.0.0")) {
					return "";
				}
			}
			
			return this.getClientRssi() + " dBm";
		} else {
			return "";
		}
	}
	
	public String getVendorName() {
		return AhConstantUtil.getMacOuiComName(clientMac.substring(0, 6));
	}

	public long getEndTimeStamp() {
		return endTimeStamp;
	}

	public void setEndTimeStamp(long endTime) {
		this.endTimeStamp = endTime;
	}

	public long getStartTimeStamp() {
		return startTimeStamp;
	}

	public void setStartTimeStamp(long startTime) {
		this.startTimeStamp = startTime;
	}

	public String getEndTimeZone() {
		return endTimeZone;
	}

	public void setEndTimeZone(String endTimeZone) {
		this.endTimeZone = endTimeZone;
	}

	public String getStartTimeZone() {
		return startTimeZone;
	}

	public void setStartTimeZone(String startTimeZone) {
		this.startTimeZone = startTimeZone;
	}

	public int getBandWidthSentinelStatus() {
		return bandWidthSentinelStatus;
	}

	public void setBandWidthSentinelStatus(int bandWidthSentinelStatus) {
		this.bandWidthSentinelStatus = bandWidthSentinelStatus;
	}

	public Long getBelongAPID() {
		HiveAp ap = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", apMac);
		if (ap == null) {
			return null;
		}

		return ap.getId();
	}

	public boolean isSimulated() {
		return simulated;
	}

	public void setSimulated(boolean simulated) {
		this.simulated = simulated;
	}

	public byte getSlaConnectScore() {
		return slaConnectScore;
	}

	public void setSlaConnectScore(byte slaConnectScore) {
		this.slaConnectScore = slaConnectScore;
	}

	public byte getIpNetworkConnectivityScore() {
		return ipNetworkConnectivityScore;
	}

	public void setIpNetworkConnectivityScore(byte ipNetworkConnectivityScore) {
		this.ipNetworkConnectivityScore = ipNetworkConnectivityScore;
	}

	public byte getApplicationHealthScore() {
		return applicationHealthScore;
	}

	public void setApplicationHealthScore(byte applicationHealthScore) {
		this.applicationHealthScore = applicationHealthScore;
	}

	public byte getOverallClientHealthScore() {
		return overallClientHealthScore;
	}

	public void setOverallClientHealthScore(byte overallClientHealthScore) {
		this.overallClientHealthScore = overallClientHealthScore;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Override
	public String toString() {
		return "Client: " + clientMac + "; BSSID: " + clientBSSID + "; SSID: " + clientSSID
				+ "; Auth User Name: " + clientUsername + "; Host Name: " + clientHostname;
	}

	/**
	 * @return the last2HourData
	 */
	public long getLast2HourData() {
		return last2HourData;
	}
	
	public String getLast2HourDataString() {
        if (last2HourData > 500000000) {
    		return (float)(Math.round(((float)last2HourData)/1000000000*100))/100  + " GB";
  		} else if (last2HourData > 500000) {
  			return (float)(Math.round(((float)last2HourData)/1000000*100))/100 + " MB";
		} else if (last2HourData > 500) {
			return (float)(Math.round(((float)last2HourData)/1000*100))/100 + " KB";
		} else if (last2HourData ==0) {
			return "";
  		} else {
  			return last2HourData + " Bytes";
  		}
	}

	/**
	 * @param last2HourData the last2HourData to set
	 */
	public void setLast2HourData(long last2HourData) {
		this.last2HourData = last2HourData;
	}
	
	//bug fix for 20142, use rssi&snr value sent by HiveOS
	public String getClientSNRShow(){
		if(wirelessClient){
			if (this.getClientRssi()==0) {
				if (this.getClientIP()==null || this.getClientIP().isEmpty()
						|| this.getClientIP().equals("0.0.0.0")) {
					return "";
				}
			}
			
			return String.valueOf(SNR) + " dB";
		} else {
			return "";
		}
	}
	
	@Transient
	public String getActiveClientType() {
		if (wirelessClient) {
			return "wireless";
		} else {
			return "wired";
		}
	}

	public String getUserProfileName() {
		return userProfileName;
	}
	
	public void setUserProfileName(String userProfileName) {
		this.userProfileName = userProfileName;
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

	public String getClientMacBasedAuthUsedString() {
		if (clientMacBasedAuthUsed == 0) {
			return "";
		} else {
			return MgrUtil.getEnumString("enum.snmp.association.mac.auth." + clientMacBasedAuthUsed);
		}
	}
	
	@Transient
	private HmDomain loginUser;
	
	public void setLoginUser(HmDomain loginUser){
		this.loginUser = loginUser;
	}

	public boolean isClientEnrolled() {
		return clientEnrolled;
	}

	public void setClientEnrolled(boolean clientEnrolled) {
		this.clientEnrolled = clientEnrolled;
	}
	
	public String getClientEnrolledString() {
		if (clientEnrolled) {
			return "Yes";
		}
		return "No";
	}

	public String getClientEnrolledURL() {
		return clientEnrolledURL;
	}

	public void setClientEnrolledURL(String clientEnrolledURL) {
		this.clientEnrolledURL = clientEnrolledURL;
	}

	public short getManagedStatus() {
		return managedStatus;
	}

	public void setManagedStatus(short managedStatus) {
		this.managedStatus = managedStatus;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public void setEnrolledCMURL(String enrolledCMURL) {
		this.enrolledCMURL = enrolledCMURL;
	}

	public void setEnrolledSLURL(String enrolledSLURL) {
		this.enrolledSLURL = enrolledSLURL;
	}

	public void setEnrolledIDMURL(String enrolledIDMURL) {
		this.enrolledIDMURL = enrolledIDMURL;
	}
}