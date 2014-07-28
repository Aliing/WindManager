package com.ah.bo.monitor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
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

import com.ah.be.communication.event.BeTrapEvent;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.monitor.ReportListAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.datetime.AhDateTimeUtil;

/*
 * @author Chris Scheers
 */

@Entity
@Table(name = "AH_EVENT")
@org.hibernate.annotations.Table(appliesTo = "AH_EVENT", indexes = {
		@Index(name = "EVENT_OWNER", columnNames = { "OWNER" }),
		@Index(name = "EVENT_AP_ID", columnNames = { "APID" }),
		@Index(name = "EVENT_AP_NAME", columnNames = { "APNAME" }),
		@Index(name = "EVENT_OBJECT_NAME", columnNames = { "OBJECTNAME" })
		})
public class AhEvent implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long		id;

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

	@Column(length = MAC_ADDRESS_LENGTH)
	private String		apId;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String		apName;

	@Column(length = 64)
	private String		objectName;

	private int			code;

	private String		trapDesc;

	@Embedded
	private HmTimeStamp	trapTimeStamp	= HmTimeStamp.ZERO_TIMESTAMP;

	public String getApId() {
		return apId;
	}

	public void setApId(String apId) {
		this.apId = apId;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getTrapDesc() {
		return trapDesc;
	}

	/**
	 * This function is just for display at GUI.
	 *
	 * @return -
	 * @author Joseph Chen
	 */
	public String getTrapDescString() {
		return trapDesc.replaceAll("\n", "<br />");
	}

	public void setTrapDesc(String trapDesc) {
		this.trapDesc = trapDesc;
	}

	public String getTrapTimeStringFromBE() {
	    if (trapTimeStamp == null || trapTimeStamp.getTime() <= 0) {
	        return "-";
	    }
	    return AhDateTimeUtil.getFormattedDateTime(getTrapTimeStamp());
	}
	
	public String getTrapTimeString() {
		if (trapTimeStamp == null || trapTimeStamp.getTime() <= 0) {
			return "-";
		}
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			return AhDateTimeUtil.getFormattedDateTime(getTrapTimeStamp(), loginUser != null ? loginUser : owner);
		}else{
			return AhDateTimeUtil.getFormattedDateTime(getTrapTimeStamp());
		}
	}

	@Transient
	public String getTrapTimeExcel() {
		if (trapTimeStamp == null || trapTimeStamp.getTime() <= 0) {
			return "-";
		}

		return MgrUtil.getExcelDateTimeString(new Date(trapTimeStamp.getTime()), TimeZone
				.getTimeZone(trapTimeStamp.getTimeZone()));
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

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

	@Override
	public String getLabel() {
		return apName;
	}

	// ---------------------------------------------------------------

	/*
	 * AhEventType
	 */
	public static final short	AH_EVENT_TYPE_THRESHOLD_CROSSING	= 1;

	public static final short	AH_EVENT_TYPE_STATE_CHANGE			= 2;

	public static final short	AH_EVENT_TYPE_CONNECTION_CHANGE		= 3;

	public static final short	AH_EVENT_TYPE_CLIENTINFO_CHANGE		= 4;

	public static final short	AH_EVENT_TYPE_POE					= 107;

	public static final short	AH_EVENT_TYPE_CHANNELPOWERCHANGE	= 108;

	public static final short	AH_EVENT_TYPE_AIRSCREEN				= 101;

	public static final short	AH_EVENT_TYPE_INTERFACECLIENT		= 102;

	public static final short	AH_EVENT_TYPE_PSE		            = 103;
	
	public static final short	AH_EVENT_TYPE_CWP_INFO				= 104;
	
	public static final short	AH_EVENT_TYPE_POWERMODECHANGE		= 115;
	
	public static final short	AH_EVENT_TYPE_UNKNOW				= 999;

	public static EnumItem[]	ENUM_AH_EVENT_TYPE					= MgrUtil.enumItems(
																			"enum.ahEventType.",
																			new int[] {
			AH_EVENT_TYPE_THRESHOLD_CROSSING, AH_EVENT_TYPE_STATE_CHANGE,
			AH_EVENT_TYPE_CONNECTION_CHANGE, AH_EVENT_TYPE_CLIENTINFO_CHANGE });

	@Transient
	public static String getEventTypeString(short eventType) {
		switch (eventType) {
		case AH_EVENT_TYPE_THRESHOLD_CROSSING:
		case AH_EVENT_TYPE_STATE_CHANGE:
		case AH_EVENT_TYPE_CONNECTION_CHANGE:
		case AH_EVENT_TYPE_CLIENTINFO_CHANGE:
		case AH_EVENT_TYPE_POE:
		case AH_EVENT_TYPE_CHANNELPOWERCHANGE:
		case AH_EVENT_TYPE_INTERFACECLIENT:
			return MgrUtil.getEnumString("enum.ahEventType." + eventType);
		default:
			return "Unknown";
		}
	}

	/*
	 * AhObjectType
	 */
	public static final short	AH_OBJECT_TYPE_CLIENT_LINK	= 1;

	public static final short	AH_OBJECT_TYPE_NEIGHBORLINK	= 2;

	public static EnumItem[]	ENUM_AH_OBJECT_TYPE			= MgrUtil.enumItems(
																	"enum.ahObjectType.",
																	new int[] {
			AH_OBJECT_TYPE_CLIENT_LINK, AH_OBJECT_TYPE_NEIGHBORLINK });

	@Transient
	public static String getObjectTypeString(short objectType) {
		switch (objectType) {
		case AH_OBJECT_TYPE_CLIENT_LINK:
		case AH_OBJECT_TYPE_NEIGHBORLINK:
			return MgrUtil.getEnumString("enum.ahObjectType." + objectType);
		default:
			return "Unknown";
		}
	}

	/*
	 * AhState
	 */
	public static final short	AH_STATE_UP		= 1;

	public static final short	AH_STATE_DOWN	= 2;

	@Transient
	public static String getStateString(short state) {
		switch (state) {
		case AH_STATE_UP:
			return MgrUtil.getEnumString("enum.interface.adminState.0");
		case AH_STATE_DOWN:
			return MgrUtil.getEnumString("enum.interface.adminState.1");
		default:
			return "Unknown";
		}
	}

	private short				eventType;

	private String				tag1;

	/*
	 * ThresholdCrossingEvent
	 */
	private int					curValue;
	private int					thresholdHigh;
	private int					thresholdLow;

	/*
	 * StateChangeEvent
	 */
	private short				previousState;
	private short				currentState;

	/*
	 * ConnectionChangeEvent, also uses currentState
	 */
	private int					ifIndex;
	private short				objectType;

	@Column(length = MAC_ADDRESS_LENGTH)
	private String				remoteId;

	/**
	 * 2008-7-11, add 4 attributes for client info event
	 */
	private String				ssid;
	private String				clientIp;
	private String				clientHostName;
	private String				clientUserName;

	/**
	 * 2008-8-20, add 9 attributes for connection change event
	 */
	private byte				clientCWPUsed;

	public static final byte	CLIENT_AUTH_METHOD_CWP			= 0;

	public static final byte	CLIENT_AUTH_METHOD_OPEN			= 1;

	public static final byte	CLIENT_AUTH_METHOD_WEPOPEN		= 2;

	public static final byte	CLIENT_AUTH_METHOD_WEPSHARED	= 3;

	public static final byte	CLIENT_AUTH_METHOD_WPAPSK		= 4;

	public static final byte	CLIENT_AUTH_METHOD_WPA2PSK		= 5;

	public static final byte	CLIENT_AUTH_METHOD_WPA8021X		= 6;

	public static final byte	CLIENT_AUTH_METHOD_WPA28021X	= 7;

	public static final byte	CLIENT_AUTH_METHOD_WPAAUTOPSK	= 8;

	public static final byte	CLIENT_AUTH_METHOD_WPAAUTO8021X	= 9;

	public static final byte	CLIENT_AUTH_METHOD_DYNAMICWEP	= 10;
	
	public static final byte	CLIENT_AUTH_METHOD_8021X		= 11;

	@Transient
	public static String getClientAuthMethodString(byte authMethod) {
		switch (authMethod) {
		case CLIENT_AUTH_METHOD_CWP:
		case CLIENT_AUTH_METHOD_OPEN:
		case CLIENT_AUTH_METHOD_WEPOPEN:
		case CLIENT_AUTH_METHOD_WEPSHARED:
		case CLIENT_AUTH_METHOD_WPAPSK:
		case CLIENT_AUTH_METHOD_WPA2PSK:
		case CLIENT_AUTH_METHOD_WPA8021X:
		case CLIENT_AUTH_METHOD_WPA28021X:
		case CLIENT_AUTH_METHOD_WPAAUTOPSK:
		case CLIENT_AUTH_METHOD_WPAAUTO8021X:
		case CLIENT_AUTH_METHOD_DYNAMICWEP:
		case CLIENT_AUTH_METHOD_8021X:
			return MgrUtil.getEnumString("enum.snmp.association.authentication." + authMethod);
		default:
			return "Unknown";
		}
	}

	private byte				clientAuthMethod;

	public static final byte	CLIENT_ENCRYPT_METHOD_AES	= 0;

	public static final byte	CLIENT_ENCRYPT_METHOD_TKIP	= 1;

	public static final byte	CLIENT_ENCRYPT_METHOD_WEP	= 2;

	public static final byte	CLIENT_ENCRYPT_METHOD_NON	= 3;

	@Transient
	public static String getClientEncryptMethodString(byte encryptMethod) {
		switch (encryptMethod) {
		case CLIENT_ENCRYPT_METHOD_AES:
		case CLIENT_ENCRYPT_METHOD_TKIP:
		case CLIENT_ENCRYPT_METHOD_WEP:
		case CLIENT_ENCRYPT_METHOD_NON:
			return MgrUtil.getEnumString("enum.clientEncryptMethod." + encryptMethod);
		default:
			return "Unknown";
		}
	}

	private byte				clientEncryptionMethod;

	public static final byte	CLIENT_MAC_PROTOCOL_AH11A	= 0;

	public static final byte	CLIENT_MAC_PROTOCOL_AH11B	= 1;

	public static final byte	CLIENT_MAC_PROTOCOL_AH11G	= 2;
	
	public static final byte	CLIENT_MAC_PROTOCOL_NAMODE	= 6;

	public static final byte	CLIENT_MAC_PROTOCOL_NGMODE	= 7;

	public static final byte	CLIENT_MAC_PROTOCOL_ACMODE	= 5;
	
	public static final byte	CLIENT_MAC_PROTOCOL_8023MODE	= 8;

	@Transient
	public static String getClientMacProtocolString(byte macProtocol) {
		switch (macProtocol) {
		case CLIENT_MAC_PROTOCOL_AH11A:
		case CLIENT_MAC_PROTOCOL_AH11B:
		case CLIENT_MAC_PROTOCOL_AH11G:
		case CLIENT_MAC_PROTOCOL_NAMODE:
		case CLIENT_MAC_PROTOCOL_NGMODE:
		case CLIENT_MAC_PROTOCOL_ACMODE:
		case CLIENT_MAC_PROTOCOL_8023MODE:
			return MgrUtil.getEnumString("enum.snmp.association.macProtocol." + macProtocol);
		default:
			return "Unknown";
		}
	}

	private byte			clientMacProtocol;
	private int				clientVLAN;
	private int				clientUserProfId;
	private int				clientChannel;
	private String			clientBSSID;
	private long			associationTime;

	/**
	 * 2008-8-20, add 10 attributes for power info event
	 */
	public static final int	POWER_SOURCE_ADAPTOR	= 0;

	public static final int	POWER_SOURCE_POE		= 1;
	public static final int	POWER_SOURCE_POE_AF		= 2;
	public static final int	POWER_SOURCE_POE_AT		= 3;

	@Transient
	public static String getPowerSourceString(int powerSource) {
		switch (powerSource) {
		case POWER_SOURCE_ADAPTOR:
		case POWER_SOURCE_POE:
		case POWER_SOURCE_POE_AF:
		case POWER_SOURCE_POE_AT:
			return MgrUtil.getEnumString("enum.powerSource." + powerSource);
		default:
			return "Unknown";
		}
	}

	private int					powerSource;

	public static final byte	POE_ON			= 1;

	public static final byte	POE_OFF			= 0;

	public static final byte	POE_POWER_RATIO	= 10;

	public static final String	POE_POWER_UNIT	= "Watts";

	public static String getPoEPowerString(int power) {
		return power / POE_POWER_RATIO + " " + POE_POWER_UNIT;
	}

	private byte				poEEth0On;
	private int					poEEth0Pwr;
	private byte				poEEth1On;
	private int					poEEth1Pwr;

	public static final byte	POE_ETH_MAX_SPEED_DOWN	= 1;

	public static final byte	POE_ETH_MAX_SPEED_10M	= 2;

	public static final byte	POE_ETH_MAX_SPEED_100M	= 3;

	public static final byte	POE_ETH_MAX_SPEED_1000M	= 4;

	@Transient
	public static String getPoEEthMaxSpeed(byte maxSpeed) {
		switch (maxSpeed) {
		case POE_ETH_MAX_SPEED_DOWN:
		case POE_ETH_MAX_SPEED_10M:
		case POE_ETH_MAX_SPEED_100M:
		case POE_ETH_MAX_SPEED_1000M:
			return MgrUtil.getEnumString("enum.ethMaxSpeed." + maxSpeed);
		default:
			return "Unknown";
		}
	}

	private byte				poEEth0MaxSpeed;
	private byte				poEEth1MaxSpeed;

	public static final byte	POE_WIFI_SETTING_INVALID	= 0;

	public static final byte	POE_WIFI_SETTING_DOWN		= 1;

	public static final byte	POE_WIFI_SETTING_CONFIGURED	= 2;

	public static final byte	POE_WIFI_SETTING_TX2RX3		= 3;

	@Transient
	public static String getPoEWifiSetting(byte maxSpeed) {
		switch (maxSpeed) {
		case POE_WIFI_SETTING_INVALID:
		case POE_WIFI_SETTING_DOWN:
		case POE_WIFI_SETTING_CONFIGURED:
		case POE_WIFI_SETTING_TX2RX3:
			return MgrUtil.getEnumString("enum.poeWifiSetting." + maxSpeed);
		default:
			return "Unknown";
		}
	}

	private byte		poEWifi0Setting;
	private byte		poEWifi1Setting;
	private byte		poEWifi2Setting;

	/**
	 * 2008-8-20, add 2 attributes for channel power change event
	 */
	private int			radioChannel;
	private int			radioTxPower;

	/**
	 * 2009-6-1, add 8 attributes for air screen
	 */
	private byte		asReportType;
	private byte		asNameType;
	private String		asName;
	private byte		asSourceType;
	private String		asSourceID;

	/**
	 * add fields for interface/client alert trap
	 */
	private byte	alertType;
	private int		thresholdValue;
	private int		shorttermValue;
	private int		snapshotValue;

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "time", column = @Column(name = "AS_TIME", nullable = false)),
			@AttributeOverride(name = "timeZone", column = @Column(name = "AS_TIME_ZONE")) })
	private HmTimeStamp	asTimeStamp	= HmTimeStamp.CURRENT_TIMESTAMP;

	private String		asRuleName;
	private int			asInstanceID;

	public byte getAsReportType() {
		return asReportType;
	}

	public void setAsReportType(byte asReportType) {
		this.asReportType = asReportType;
	}

	public byte getAsNameType() {
		return asNameType;
	}

	public void setAsNameType(byte asNameType) {
		this.asNameType = asNameType;
	}

	public String getAsName() {
		return asName;
	}

	public void setAsName(String asName) {
		this.asName = asName;
	}

	public byte getAsSourceType() {
		return asSourceType;
	}

	public void setAsSourceType(byte asSourceType) {
		this.asSourceType = asSourceType;
	}

	public String getAsSourceID() {
		return asSourceID;
	}

	public void setAsSourceID(String asSourceID) {
		this.asSourceID = asSourceID;
	}

	public HmTimeStamp getAsTimeStamp() {
		return asTimeStamp;
	}

	public void setAsTimeStamp(HmTimeStamp asTimeStamp) {
		if (trapTimeStamp == null) {
			this.asTimeStamp = HmTimeStamp.ZERO_TIMESTAMP;
		} else {
			this.asTimeStamp = asTimeStamp;
		}
	}

	public String getAsRuleName() {
		return asRuleName;
	}

	public void setAsRuleName(String asRuleName) {
		this.asRuleName = asRuleName;
	}

	public int getAsInstanceID() {
		return asInstanceID;
	}

	public void setAsInstanceID(int asInstanceID) {
		this.asInstanceID = asInstanceID;
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

	public long getAssociationTime() {
		return associationTime;
	}

	public void setAssociationTime(long associationTime) {
		this.associationTime = associationTime;
	}

	public int getPowerSource() {
		return powerSource;
	}

	public void setPowerSource(int powerSource) {
		this.powerSource = powerSource;
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

	/**
	 * getter of ssid
	 *
	 * @return the ssid
	 */
	public String getSsid() {
		return ssid;
	}

	/**
	 * setter of ssid
	 *
	 * @param ssid
	 *            the ssid to set
	 */
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	/**
	 * getter of clientIp
	 *
	 * @return the clientIp
	 */
	public String getClientIp() {
		return clientIp;
	}

	/**
	 * setter of clientIp
	 *
	 * @param clientIp
	 *            the clientIp to set
	 */
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	/**
	 * getter of clientHostName
	 *
	 * @return the clientHostName
	 */
	public String getClientHostName() {
		return clientHostName;
	}

	/**
	 * setter of clientHostName
	 *
	 * @param clientHostName
	 *            the clientHostName to set
	 */
	public void setClientHostName(String clientHostName) {
		this.clientHostName = clientHostName;
	}

	/**
	 * getter of clientUserName
	 *
	 * @return the clientUserName
	 */
	public String getClientUserName() {
		return clientUserName;
	}

	/**
	 * setter of clientUserName
	 *
	 * @param clientUserName
	 *            the clientUserName to set
	 */
	public void setClientUserName(String clientUserName) {
		this.clientUserName = clientUserName;
	}

	public short getEventType() {
		return eventType;
	}

	public void setEventType(short eventType) {
		this.eventType = eventType;
	}

	public short getCurrentState() {
		return currentState;
	}

	public void setCurrentState(short currentState) {
		this.currentState = currentState;
	}

	public int getCurValue() {
		return curValue;
	}

	public void setCurValue(int curValue) {
		this.curValue = curValue;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	public short getObjectType() {
		return objectType;
	}

	public void setObjectType(short objectType) {
		this.objectType = objectType;
	}

	public short getPreviousState() {
		return previousState;
	}

	public void setPreviousState(short previousState) {
		this.previousState = previousState;
	}

	public String getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(String remoteId) {
		this.remoteId = remoteId;
	}

	public int getThresholdHigh() {
		return thresholdHigh;
	}

	public void setThresholdHigh(int thresholdHigh) {
		this.thresholdHigh = thresholdHigh;
	}

	public int getThresholdLow() {
		return thresholdLow;
	}

	public void setThresholdLow(int thresholdLow) {
		this.thresholdLow = thresholdLow;
	}

	public String getCodeString() {
		if (eventType == AH_EVENT_TYPE_CONNECTION_CHANGE
				&& objectType == AH_OBJECT_TYPE_CLIENT_LINK && currentState == AH_STATE_UP) {
			return "Authentication";
		} else if (eventType == AH_EVENT_TYPE_CONNECTION_CHANGE
				&& objectType == AH_OBJECT_TYPE_CLIENT_LINK && currentState == AH_STATE_DOWN
				&& getCode() != ReportListAction.CLIENT_DE_AUTH_CODE) {
			return "Deauthentication";
		} else if (eventType == AH_EVENT_TYPE_CONNECTION_CHANGE
				&& objectType == AH_OBJECT_TYPE_CLIENT_LINK && currentState == AH_STATE_DOWN
				&& getCode() == ReportListAction.CLIENT_DE_AUTH_CODE) {
			return "Rejection";
		} else {
			return "Unknown";
		}
	}

	public String getVendorNameString() {
		return AhConstantUtil.getMacOuiComName(remoteId.substring(0, 6).toUpperCase());
	}

	/*
	 * Event objects have no version
	 */
	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public void setVersion(Timestamp version) {
	}

	/**
	 * getter of trapTimeStamp
	 *
	 * @return the trapTimeStamp
	 */
	public HmTimeStamp getTrapTimeStamp() {
		return trapTimeStamp;
	}

	/**
	 * setter of trapTimeStamp
	 *
	 * @param trapTimeStamp
	 *            the trapTimeStamp to set
	 */
	public void setTrapTimeStamp(HmTimeStamp trapTimeStamp) {
		if (trapTimeStamp == null) {
			this.trapTimeStamp = HmTimeStamp.ZERO_TIMESTAMP;
		} else {
			this.trapTimeStamp = trapTimeStamp;
		}
	}

	public String getAlertTypeShow()
	{
		switch (alertType) {
		case BeTrapEvent.ALERTTYPE_CRCERROR:
			return "CRC error rate";

		case BeTrapEvent.ALERTTYPE_TXDROP:
			return "Tx drop rate";

		case BeTrapEvent.ALERTTYPE_TXRETRY:
			return "Tx retry rate";

		case BeTrapEvent.ALERTTYPE_RXDROP:
			return "Rx drop rate";

		case BeTrapEvent.ALERTTYPE_AIRTIMECONSUME:
			return "Airtime consumption";

		default:
			return "Unknown";
		}
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

	public String getTag1() {
		return tag1;
	}

	public void setTag1(String tag1) {
		this.tag1 = tag1;
	}

	@Transient
	private HmDomain loginUser;
	
	public void setLoginUser(HmDomain loginUser){
		this.loginUser = loginUser;
	}
}