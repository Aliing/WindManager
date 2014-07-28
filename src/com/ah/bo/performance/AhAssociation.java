package com.ah.bo.performance;

import java.sql.Timestamp;
import java.util.Date;

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

import org.hibernate.annotations.Index;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.coder.AhDecoder;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "HM_ASSOCIATION")
@org.hibernate.annotations.Table(appliesTo = "HM_ASSOCIATION", indexes = {
		@Index(name = "ASSOCIATION_OWNER", columnNames = { "OWNER" }),
		@Index(name = "ASSOCIATION_CLIENT_MAC_TIME", columnNames = { "CLIENTMAC", "TIME"})
		})
public class AhAssociation implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long				id;

	@Column(length = 64)
	private String				apName;

	private String				apMac;

	@Column(length = 14)
	private String				apSerialNumber;

	// private Date statTime;

	private int					ifIndex;

	private String				ifName;

	private String				clientMac;

	private int					clientRSSI;

	private long				clientLinkUptime;

	public static final byte	CLIENTAUTHMETHOD_CWP			= 0;

	public static final byte	CLIENTAUTHMETHOD_OPEN			= 1;

	public static final byte	CLIENTAUTHMETHOD_WEPOPEN		= 2;

	public static final byte	CLIENTAUTHMETHOD_WEPSHARED		= 3;

	public static final byte	CLIENTAUTHMETHOD_WPAPSK			= 4;

	public static final byte	CLIENTAUTHMETHOD_WPA2PSK		= 5;

	public static final byte	CLIENTAUTHMETHOD_WPA8021X		= 6;

	public static final byte	CLIENTAUTHMETHOD_WPA28021X		= 7;

	public static final byte	CLIENTAUTHMETHOD_WPAAUTOPSK		= 8;

	public static final byte	CLIENTAUTHMETHOD_WPAAUTO8021X	= 9;

	public static final byte	CLIENTAUTHMETHOD_DYNAMICWEP		= 10;
	
	public static final byte	CLIENTAUTHMETHOD_8021X		    = 11;

	public static EnumItem[]	CLIENTAUTHMETHOD_TYPE			= MgrUtil
																		.enumItems(
																				"enum.snmp.association.authentication.",
																				new int[] {
			CLIENTAUTHMETHOD_CWP, CLIENTAUTHMETHOD_OPEN, CLIENTAUTHMETHOD_WEPOPEN,
			CLIENTAUTHMETHOD_WEPSHARED, CLIENTAUTHMETHOD_WPAPSK, CLIENTAUTHMETHOD_WPA2PSK,
			CLIENTAUTHMETHOD_WPA8021X, CLIENTAUTHMETHOD_WPA28021X, CLIENTAUTHMETHOD_WPAAUTOPSK,
			CLIENTAUTHMETHOD_WPAAUTO8021X, CLIENTAUTHMETHOD_DYNAMICWEP,	CLIENTAUTHMETHOD_8021X,	});

	private byte				clientAuthMethod;

	public static final byte	CLIENTENCRYMETHOD_AES			= 0;

	public static final byte	CLIENTENCRYMETHOD_TKIP			= 1;

	public static final byte	CLIENTENCRYMETHOD_WEP			= 2;

	public static final byte	CLIENTENCRYMETHOD_NON			= 3;

	public static EnumItem[]	CLIENTENCRYMETHOD_TYPE			= MgrUtil
																		.enumItems(
																				"enum.snmp.association.encryption.",
																				new int[] {
			CLIENTENCRYMETHOD_AES, CLIENTENCRYMETHOD_TKIP, CLIENTENCRYMETHOD_WEP,
			CLIENTENCRYMETHOD_NON												});

	private byte				clientEncryptionMethod;

	public static final byte	CLIENTMACPROTOCOL_AMODE			= 0;

	public static final byte	CLIENTMACPROTOCOL_BMODE			= 1;

	public static final byte	CLIENTMACPROTOCOL_GMODE			= 2;

	public static final byte	CLIENTMACPROTOCOL_NAMODE		= 6;

	public static final byte	CLIENTMACPROTOCOL_NGMODE		= 7;
	
	public static final byte	CLIENTMACPROTOCOL_ACMODE		= 5;
	
	public static final byte	CLIENT_MAC_PROTOCOL_8023MODE	= 8;

	public static EnumItem[]	CLIENTMACPROTOCOL_TYPE			= MgrUtil
																		.enumItems(
																				"enum.snmp.association.macProtocol.",
																				new int[] {
			CLIENTMACPROTOCOL_AMODE, CLIENTMACPROTOCOL_BMODE, CLIENTMACPROTOCOL_GMODE,
			CLIENTMACPROTOCOL_NAMODE, CLIENTMACPROTOCOL_NGMODE, CLIENTMACPROTOCOL_ACMODE,
			CLIENT_MAC_PROTOCOL_8023MODE});

	private byte				clientMACProtocol;

	public static final byte	CLIENT_CWP_USED					= 1;

	public static final byte	CLIENT_CWP_NOT_USED				= 2;

	public static EnumItem[]	CLIENT_CWP_USED_TYPE			= MgrUtil
																		.enumItems(
																				"enum.snmp.association.cwp.",
																				new int[] {
			CLIENT_CWP_USED, CLIENT_CWP_NOT_USED								});

	private byte				clientCWPUsed;

	private int					clientVLAN;

	private int					clientUserProfId;

	private int					clientChannel;

	private int					clientLastTxRate;

	private int					clientLastRxRate;

	@Column(length = 255)
	private String				clientUsername					= "";

	private long				clientRxDataFrames;

	private long				clientRxDataOctets;

	private long				clientRxMgtFrames;

	private long				clientRxUnicastFrames;

	private long				clientRxMulticastFrames;

	private long				clientRxBroadcastFrames;

	private long				clientRxMICFailures;

	private long				clientTxDataFrames;

	private long				clientTxBeDataFrames;

	private long				clientTxBgDataFrames;

	private long				clientTxViDataFrames;

	private long				clientTxVoDataFrames;

	private long				clientTxMgtFrames;

	private long				clientTxDataOctets;

	private long				clientTxUnicastFrames;

	private long				clientTxMulticastFrames;

	private long				clientTxBroadcastFrames;

	private double				clientTxAirtime;

	private double				clientRxAirtime;

	private String				clientIP;

	@Column(length = 32)
	private String				clientHostname					= "";

	@Column(length = 32)
	private String				clientSSID						= "";

	// mac address
	private String				clientBSSID						= "";
	
	private String				clientOsInfo					= "";

	private long				clientAssociateTime;

	private HmTimeStamp			timeStamp						= HmTimeStamp.CURRENT_TIMESTAMP;

	@Transient
	private byte				slaConnectScore			= 100;
	@Transient
	private byte		ipNetworkConnectivityScore=100;
	@Transient
	private byte		applicationHealthScore=100;
	@Transient
	private byte		overallClientHealthScore=100;
	
	@Transient
	private String				os_option55;
	
	private String 				userProfileName;
	
	private short				SNR;
	
	public static final byte	CLIENT_MAC_BASED_AUTH_USED			= 1;

	public static final byte	CLIENT_MAC_BASED_AUTH_NOT_USED		= 2;
	
	private byte				clientMacBasedAuthUsed;
	
	@Transient
	private short 				managedStatus;
	
	@Transient
	private HmDomain loginUser;
	
	public String getUserProfileName() {
		return userProfileName;
	}

	public void setUserProfileName(String userProfileName) {
		this.userProfileName = userProfileName;
	}

	public String getOs_option55() {
		return os_option55;
	}

	public void setOs_option55(String os_option55) {
		this.os_option55 = os_option55;
	}
	
	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
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
		if (clientChannel == 0) {
			return "Unknown";
		}
		return String.valueOf(clientChannel);
	}

	public void setClientChannel(int clientChannel) {
		this.clientChannel = clientChannel;
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
	
	public String getIfName() {
		return ifName;
	}

	public void setIfName(String ifName) {
		this.ifName = ifName;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public int getClientLastTxRate() {
		return clientLastTxRate;
	}

	public String getClientLastTxRate4Show() {
		return String.valueOf(AhDecoder.int2long(clientLastTxRate));
	}

	public void setClientLastTxRate(int clientLastTxRate) {
		this.clientLastTxRate = clientLastTxRate;
	}

	public long getClientLinkUptime() {
		return clientLinkUptime;
	}

	public String getClientLinkupTimeShow() {
		return NmsUtil.transformTime((int) clientLinkUptime);
	}

	public void setClientLinkUptime(long clientLinkUptime) {
		this.clientLinkUptime = clientLinkUptime;
	}

	public String getClientMac() {
		return clientMac;
	}

	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}

	public byte getClientMACProtocol() {
		return clientMACProtocol;
	}

	public void setClientMACProtocol(byte clientMACProtocol) {
		this.clientMACProtocol = clientMACProtocol;
	}

	public int getClientRSSI() {
		if (this.clientRSSI == 0) {
			return this.SNR - 95;
		}
		return clientRSSI;
	}

	public String getClientRSSI4Show() {
		if (ifName != null && ifName.toLowerCase().contains("eth")) {
			return "N/A";
		}
		
		return this.getClientRSSI() + " dBm";
	}

	public void setClientRSSI(int clientRSSI) {
		this.clientRSSI = clientRSSI;
	}

	public long getClientRxDataFrames() {
		return clientRxDataFrames;
	}

	public void setClientRxDataFrames(long clientRxDataFrames) {
		this.clientRxDataFrames = clientRxDataFrames;
	}

	public long getClientRxDataOctets() {
		return clientRxDataOctets;
	}

	public void setClientRxDataOctets(long clientRxDataOctets) {
		this.clientRxDataOctets = clientRxDataOctets;
	}

	public long getClientRxMgtFrames() {
		return clientRxMgtFrames;
	}

	public void setClientRxMgtFrames(long clientRxMgtFrames) {
		this.clientRxMgtFrames = clientRxMgtFrames;
	}

	public long getClientRxMICFailures() {
		return clientRxMICFailures;
	}

	public void setClientRxMICFailures(long clientRxMICFailures) {
		this.clientRxMICFailures = clientRxMICFailures;
	}

	public long getClientRxMulticastFrames() {
		return clientRxMulticastFrames;
	}

	public void setClientRxMulticastFrames(long clientRxMulticastFrames) {
		this.clientRxMulticastFrames = clientRxMulticastFrames;
	}

	public long getClientRxUnicastFrames() {
		return clientRxUnicastFrames;
	}

	public void setClientRxUnicastFrames(long clientRxUnicastFrames) {
		this.clientRxUnicastFrames = clientRxUnicastFrames;
	}

	public String getClientSSID() {
		return clientSSID;
	}

	public void setClientSSID(String clientSSID) {
		this.clientSSID = clientSSID;
	}

	public long getClientTxDataFrames() {
		return clientTxDataFrames;
	}

	public void setClientTxDataFrames(long clientTxDataFrames) {
		this.clientTxDataFrames = clientTxDataFrames;
	}

	public long getClientTxDataOctets() {
		return clientTxDataOctets;
	}

	public void setClientTxDataOctets(long clientTxDataOctets) {
		this.clientTxDataOctets = clientTxDataOctets;
	}

	public long getClientTxMgtFrames() {
		return clientTxMgtFrames;
	}

	public void setClientTxMgtFrames(long clientTxMgtFrames) {
		this.clientTxMgtFrames = clientTxMgtFrames;
	}

	public String getClientUsername() {
		return clientUsername;
	}

	public void setClientUsername(String clientUsername) {
		this.clientUsername = clientUsername;
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
		return String.valueOf(AhDecoder.int2long(clientVLAN));
	}

	public String getClientVLAN4Show() {
		return String.valueOf(AhDecoder.int2long(clientVLAN));
	}

	public void setClientVLAN(int clientVLAN) {
		this.clientVLAN = clientVLAN;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	public String getStatTime() {
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			return AhDateTimeUtil.getSpecifyDateTime(new Date(timeStamp.getTime()), loginUser);
		}else{
			return AhDateTimeUtil.getFormattedDateTime(timeStamp, "yyyy-MM-dd HH:mm:ss");
		}
	}

	//
	// public Date getStatTimeValue() {
	// return statTime;
	// }
	//
	// public void setStatTime(Date statTime) {
	// this.statTime = statTime;
	// }

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

	public long getClientRxBroadcastFrames() {
		return clientRxBroadcastFrames;
	}

	public void setClientRxBroadcastFrames(long clientRxBroadcastFrames) {
		this.clientRxBroadcastFrames = clientRxBroadcastFrames;
	}

	public long getClientTxBroadcastFrames() {
		return clientTxBroadcastFrames;
	}

	public void setClientTxBroadcastFrames(long clientTxBroadcastFrames) {
		this.clientTxBroadcastFrames = clientTxBroadcastFrames;
	}

	public long getClientTxMulticastFrames() {
		return clientTxMulticastFrames;
	}

	public void setClientTxMulticastFrames(long clientTxMulticastFrames) {
		this.clientTxMulticastFrames = clientTxMulticastFrames;
	}

	public long getClientTxUnicastFrames() {
		return clientTxUnicastFrames;
	}

	public void setClientTxUnicastFrames(long clientTxUnicastFrames) {
		this.clientTxUnicastFrames = clientTxUnicastFrames;
	}

	public byte getClientCWPUsed() {
		return clientCWPUsed;
	}

	public void setClientCWPUsed(byte clientCWPUsed) {
		this.clientCWPUsed = clientCWPUsed;
	}

	// --------implement interface function--------
	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return "ahAssociation";
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

	@ManyToOne(fetch = FetchType.LAZY)
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

	public String getClientAuthMethodString() {
		switch (clientAuthMethod) {
		case CLIENTAUTHMETHOD_OPEN:
		case CLIENTAUTHMETHOD_WEPOPEN:
		case CLIENTAUTHMETHOD_WEPSHARED:
		case CLIENTAUTHMETHOD_WPAPSK:
		case CLIENTAUTHMETHOD_WPA2PSK:
		case CLIENTAUTHMETHOD_WPA8021X:
		case CLIENTAUTHMETHOD_WPA28021X:
		case CLIENTAUTHMETHOD_WPAAUTOPSK:
		case CLIENTAUTHMETHOD_WPAAUTO8021X:
		case CLIENTAUTHMETHOD_DYNAMICWEP:
		case CLIENTAUTHMETHOD_8021X:
			return MgrUtil
					.getEnumString("enum.snmp.association.authentication." + clientAuthMethod);
		default:
			return "Unknown";
		}
	}

	public String getClientEncryptionMethodString() {
		switch (clientEncryptionMethod) {
		case CLIENTENCRYMETHOD_AES:
		case CLIENTENCRYMETHOD_TKIP:
		case CLIENTENCRYMETHOD_WEP:
		case CLIENTENCRYMETHOD_NON:
			return MgrUtil.getEnumString("enum.snmp.association.encryption."
					+ clientEncryptionMethod);
		default:
			return "Unknown";
		}
	}

	public String getClientMacPtlString() {
		if (ifName != null && ifName.toLowerCase().contains("eth")) {
			return "Ethernet";
		}
		
		switch (clientMACProtocol) {
		case CLIENTMACPROTOCOL_AMODE:
		case CLIENTMACPROTOCOL_BMODE:
		case CLIENTMACPROTOCOL_GMODE:
		case CLIENTMACPROTOCOL_NAMODE:
		case CLIENTMACPROTOCOL_NGMODE:
		case CLIENTMACPROTOCOL_ACMODE:
		case CLIENT_MAC_PROTOCOL_8023MODE:
			return MgrUtil.getEnumString("enum.snmp.association.macProtocol." + clientMACProtocol);
		default:
			return "Unknown";
		}
	}

	public String getLinkUpTimeString() {
		return NmsUtil.transformTime((int) clientLinkUptime);
	}

	// public String getClientMacString()
	// {
	// String strClientMac = clientMac;
	//
	// String strOui = strClientMac.substring(0, 6);
	// if (AhHistoryClientSession.macOuiList != null
	// && AhHistoryClientSession.macOuiList.get(strOui) != null)
	// {
	// strClientMac = strClientMac + "("
	// + AhHistoryClientSession.macOuiList.get(strOui) + ")";
	// }
	// return strClientMac;
	// }

	public String getClientCWPUsedString() {
		if (clientCWPUsed == 0) {
			return "";
		} else {
			return MgrUtil.getEnumString("enum.snmp.association.cwp." + clientCWPUsed);
		}
	}

	public int getClientLastRxRate() {
		return clientLastRxRate;
	}

	public void setClientLastRxRate(int clientLastRxRate) {
		this.clientLastRxRate = clientLastRxRate;
	}

	public String getClientLastRxRate4Show() {
		return String.valueOf(AhDecoder.int2long(clientLastRxRate));
	}

	public long getClientTxBeDataFrames() {
		return clientTxBeDataFrames;
	}

	public void setClientTxBeDataFrames(long clientTxBeDataFrames) {
		this.clientTxBeDataFrames = clientTxBeDataFrames;
	}

	public long getClientTxBgDataFrames() {
		return clientTxBgDataFrames;
	}

	public void setClientTxBgDataFrames(long clientTxBgDataFrames) {
		this.clientTxBgDataFrames = clientTxBgDataFrames;
	}

	public long getClientTxViDataFrames() {
		return clientTxViDataFrames;
	}

	public void setClientTxViDataFrames(long clientTxViDataFrames) {
		this.clientTxViDataFrames = clientTxViDataFrames;
	}

	public long getClientTxVoDataFrames() {
		return clientTxVoDataFrames;
	}

	public void setClientTxVoDataFrames(long clientTxVoDataFrames) {
		this.clientTxVoDataFrames = clientTxVoDataFrames;
	}

	public double getClientRxAirtime() {
		return clientRxAirtime;
	}

	public void setClientRxAirtime(double clientRxAirtime) {
		this.clientRxAirtime = clientRxAirtime;
	}

	public double getClientTxAirtime() {
		return clientTxAirtime;
	}

	public void setClientTxAirtime(double clientTxAirtime) {
		this.clientTxAirtime = clientTxAirtime;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
	}

	public String getClientBSSID() {
		return clientBSSID;
	}

	public void setClientBSSID(String clientBSSID) {
		this.clientBSSID = clientBSSID;
	}
	
	public String getClientOsInfo() {
		return clientOsInfo;
	}

	public void setClientOsInfo(String clientOsInfo) {
		this.clientOsInfo = clientOsInfo;
	}

	// mark: in 3.2previous version, clientAssociateTime attribute is not be set, so we need
	// initialize it, otherwise, client start time maybe new Date(0)
	public long getClientAssociateTime() {
		//add protection, use current system time - link up time if association time is invalid
		if(clientAssociateTime < (System.currentTimeMillis()/1000 - 86400*365))
			clientAssociateTime = System.currentTimeMillis()/1000 - clientLinkUptime;
		return clientAssociateTime;
	}

	public void setClientAssociateTime(long clientAssociateTime) {
		this.clientAssociateTime = clientAssociateTime;
	}

	public HmTimeStamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(HmTimeStamp timeStamp) {
		this.timeStamp = timeStamp;
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

	public short getSNR() {
		return SNR;
	}

	public void setSNR(short sNR) {
		SNR = sNR;
	}
	
	public String getClientSNRShow(){
		return String.valueOf(this.SNR) + " dB";
	}
	
	public byte getClientMacBasedAuthUsed() {
		return clientMacBasedAuthUsed;
	}

	public void setClientMacBasedAuthUsed(byte clientMacBasedAuthUsed) {
		this.clientMacBasedAuthUsed = clientMacBasedAuthUsed;
	}

	public HmDomain getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(HmDomain loginUser) {
		this.loginUser = loginUser;
	}

	public short getManagedStatus() {
		return managedStatus;
	}

	public void setManagedStatus(short managedStatus) {
		this.managedStatus = managedStatus;
	}
}