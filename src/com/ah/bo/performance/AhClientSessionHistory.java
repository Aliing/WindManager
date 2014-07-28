package com.ah.bo.performance;

import java.sql.Timestamp;
import java.util.Date;
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

import org.hibernate.annotations.Index;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.coder.AhDecoder;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "AH_CLIENTSESSION_HISTORY")
@org.hibernate.annotations.Table(appliesTo = "AH_CLIENTSESSION_HISTORY", indexes = {
		@Index(name = "CLIENT_SESSION_HISTORY_OWNER", columnNames = { "OWNER" }),
		@Index(name = "CLIENT_SESSION_HISTORY_CLIENT_START_END", columnNames = { "CLIENTMAC", "STARTTIMESTAMP","ENDTIMESTAMP" })
		})
public class AhClientSessionHistory implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long	id;

	@Column(length = 64)
	private String	apName;

	private String	apMac;

	@Column(length = 14)
	private String	apSerialNumber;

	private int		ifIndex;

	private String	ifName;

	// @Column(length = 48, unique = true, nullable = false)
	@Column(nullable = false)
	private String	clientMac;

	private byte	clientAuthMethod;

	private byte	clientEncryptionMethod;

	// default "802.11b"
	private byte	clientMACProtocol		= 1;

	private int		clientVLAN;

	private int		clientUserProfId;

	private int		clientChannel;

	@Column(length = 32)
	private String	clientHostname			= "";

	@Column(length = 32)
	private String	clientSSID				= "";

	private String	clientIP;

	@Column(length = 255)
	private String	clientUsername;

	private long	startTimeStamp;

	private long	endTimeStamp;

	private String	startTimeZone;

	private String	endTimeZone;

	private String	memo;

	private byte	clientCWPUsed;

	private Long	mapId;

	@Column(length = 32)
	private String	comment1;

	@Column(length = 32)
	private String	comment2;

	// mac address
	private String	clientBSSID				= "";

	private int		bandWidthSentinelStatus	= AhBandWidthSentinelHistory.STATUS_CLEAR;

	private boolean	simulated;

	private String				clientOsInfo;
	
	private String		email;
	
	private String		companyName;

	private String		userProfileName;
	

	public String getIfName() {
		return ifName;
	}

	public void setIfName(String ifName) {
		this.ifName = ifName;
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

	public String getStartTimeShow() {
		if(BaseAction.getSessionUserContext() !=null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			return AhDateTimeUtil.getSpecifyDateTime(new Date(startTimeStamp),
					TimeZone.getTimeZone(startTimeZone), loginUser != null ? loginUser : owner);
		}else{
			return AhDateTimeUtil.getSpecifyDateTime(new Date(startTimeStamp),
					TimeZone.getTimeZone(startTimeZone));
		}
	}

	@Transient
	public String getEndTimeShow() {
		if(BaseAction.getSessionUserContext() !=null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			return AhDateTimeUtil.getSpecifyDateTime(new Date(endTimeStamp), 
					TimeZone.getTimeZone(endTimeZone), loginUser != null ? loginUser : owner);
		}else{
			return AhDateTimeUtil.getSpecifyDateTime(new Date(endTimeStamp), 
					TimeZone.getTimeZone(endTimeZone));
		}
	}

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
		if (clientChannel == 0) {
			return "Unknown";
		}
		return String.valueOf(clientChannel);
	}

	public String getClientChannel4Show() {
		return String.valueOf(AhDecoder.int2long(clientChannel));
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
		if(BaseAction.getSessionUserContext() !=null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			return AhDateTimeUtil.getSpecifyDateTime(new Date(startTimeStamp),
					TimeZone.getTimeZone(startTimeZone), loginUser != null ? loginUser : owner);
		}else{
			return AhDateTimeUtil.getFormatDateTime(new Date(startTimeStamp), "yyyy-MM-dd HH:mm:ss",
					TimeZone.getTimeZone(startTimeZone));
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
	}

	@Transient
	public String getClientAuthMethodString() {
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
			return MgrUtil
					.getEnumString("enum.snmp.association.authentication." + clientAuthMethod);
		default:
			return "Unknown";
		}
	}

	@Transient
	public String getClientEncryptionMethodString() {
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
	}

	// @Transient
	// private String clientTypeIcon;
	//
	// public String getClientTypeIcon()
	// {
	// return clientTypeIcon;
	// }
	//
	// public void setClientTypeIcon(String clientTypeIcon)
	// {
	// this.clientTypeIcon = clientTypeIcon;
	// }

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

	// public int getClientLastRxRate() {
	// return clientLastRxRate;
	// }
	//
	// public void setClientLastRxRate(int clientLastRxRate) {
	// this.clientLastRxRate = clientLastRxRate;
	// }
	//
	// public String getClientLastRxRate4Show() {
	// return String.valueOf(AhDecoder.int2long(clientLastRxRate));
	// }

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
	}

	// public Date getEndTime()
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
		return clientBSSID;
	}

	public void setClientBSSID(String clientBSSID) {
		this.clientBSSID = clientBSSID;
	}

	public long getEndTimeStamp() {
		return endTimeStamp;
	}

	public void setEndTimeStamp(long endTime) {
		this.endTimeStamp = endTime;
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

	public long getStartTimeStamp() {
		return startTimeStamp;
	}

	public void setStartTimeStamp(long startTimeStamp) {
		this.startTimeStamp = startTimeStamp;
	}

	public boolean isSimulated() {
		return simulated;
	}

	public void setSimulated(boolean simulated) {
		this.simulated = simulated;
	}

	public String getClientOsInfo() {
		return clientOsInfo;
	}

	public void setClientOsInfo(String clientOsInfo) {
		this.clientOsInfo = clientOsInfo;
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

	public String getUserProfileName() {
		return userProfileName;
	}

	public void setUserProfileName(String userProfileName) {
		this.userProfileName = userProfileName;
	}
	
	@Transient
	private HmDomain loginUser;
	
	public void setLoginUser(HmDomain loginUser){
		this.loginUser = loginUser;
	}
}