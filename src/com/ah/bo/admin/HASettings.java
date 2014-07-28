package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.bo.HmBo;

@Entity
@Table(name = "HA_SETTINGS")
public class HASettings implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long				id;

	// status which begging of a long time operation as like: standalone->ha enable
	public static final byte	HASTATUS_INITIAL			= 0;

	// ha disable status
	public static final byte	HASTATUS_DIABLE				= 1;

	// ha enable status
	public static final byte	HASTATUS_ENABLE				= 2;

	private byte				haStatus;

	private boolean				enableFailBack;
	
	//enable external database
	// disable ha, connect local db
	public static final byte	EXTERNALDB_DISABLEHA_INITIAL		= 0;

	// disable ha, connect remote db
	public static final byte	EXTERNALDB_DISABLEHA_REMOTE			= 1;

	// ha enable status
	public static final byte	EXTERNALDB_ENABLEHA_REMOTE			= 2;
	private byte				enableExternalDb;

	private String				primaryHostName				= "";

	private String				secondaryHostName			= "";

	private String				domainName					= "";

	private String				primaryMGTIP				= "";

	private String				primaryMGTNetmask			= "";

	private String				secondaryMGTIP				= "";

	private String				secondaryMGTNetmask			= "";

	private String				primaryLANIP				= "";

	private String				primaryLANNetmask			= "";

	private String				secondaryLANIP				= "";

	private String				secondaryLANNetmask			= "";

	private String				primaryDefaultGateway		= "";

	private String				secondaryDefaultGateway		= "";

	private String				haSecret					= "";

	private String				masterHostNameSticky		= "";

	private String				primarySystemId				= "";

	private String				secondarySystemId			= "";

	public static final byte	HAPORT_MGT					= 0;

	public static final byte	HAPORT_LAN					= 1;

	private byte				haPort						= HAPORT_MGT;

	private boolean				useExternalIPHostname		= false;

	private String				primaryExternalIPHostname	= "";

	private String				secondaryExternalIPHostname	= "";

	private int heartbeatTimeOutValue = 60;

	public static final String DEFAULT_HA_NOTIFY_EMAIL = "techops_mon@aerohive.com";
	// add HA Notify Email
	private String haNotifyEmail;
	
	/*
	 * Add from 2012/04/10
	 */
	private long primaryUpTime;
	
	private long secondaryUpTime;
	
	private long lastSwitchOverTime;
	
	private String primaryDbUrl;
	
//	@Transient
	private String primaryDbPwd;
	
	private String secondaryDbUrl;
	
//	@Transient
	private String secondaryDbPwd;
	
	public byte getEnableExternalDb() {
		return enableExternalDb;
	}

	public void setEnableExternalDb(byte enableExternalDb) {
		this.enableExternalDb = enableExternalDb;
	}
	
	public long getPrimaryUpTime() {
		return primaryUpTime;
	}

	public void setPrimaryUpTime(long primaryUpTime) {
		this.primaryUpTime = primaryUpTime;
	}

	public long getSecondaryUpTime() {
		return secondaryUpTime;
	}

	public void setSecondaryUpTime(long secondaryUpTime) {
		this.secondaryUpTime = secondaryUpTime;
	}

	public long getLastSwitchOverTime() {
		return lastSwitchOverTime;
	}

	public void setLastSwitchOverTime(long lastSwitchOverTime) {
		this.lastSwitchOverTime = lastSwitchOverTime;
	}

	public String getPrimaryDbUrl() {
		return primaryDbUrl;
	}

	public void setPrimaryDbUrl(String primaryDbUrl) {
		this.primaryDbUrl = primaryDbUrl;
	}

	public String getPrimaryDbPwd() {
		return primaryDbPwd;
	}

	public void setPrimaryDbPwd(String primaryDbPwd) {
		this.primaryDbPwd = primaryDbPwd;
	}

	public String getSecondaryDbUrl() {
		return secondaryDbUrl;
	}

	public void setSecondaryDbUrl(String secondaryDbUrl) {
		this.secondaryDbUrl = secondaryDbUrl;
	}

	public String getSecondaryDbPwd() {
		return secondaryDbPwd;
	}

	public void setSecondaryDbPwd(String secondaryDbPwd) {
		this.secondaryDbPwd = secondaryDbPwd;
	}

	public byte getHaPort() {
		return haPort;
	}

	public void setHaPort(byte haPort) {
		this.haPort = haPort;
	}

	public boolean isUseExternalIPHostname() {
		return useExternalIPHostname;
	}

	public void setUseExternalIPHostname(boolean useExternalIPHostname) {
		this.useExternalIPHostname = useExternalIPHostname;
	}

	public String getPrimaryExternalIPHostname() {
		return primaryExternalIPHostname;
	}

	public void setPrimaryExternalIPHostname(String primaryExternalIPHostname) {
		this.primaryExternalIPHostname = primaryExternalIPHostname == null ? ""
				: primaryExternalIPHostname;
	}

	public String getSecondaryExternalIPHostname() {
		return secondaryExternalIPHostname;
	}

	public void setSecondaryExternalIPHostname(String secondaryExternalIPHostname) {
		this.secondaryExternalIPHostname = secondaryExternalIPHostname == null ? ""
				: secondaryExternalIPHostname;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public boolean isEnableFailBack() {
		return enableFailBack;
	}

	public void setEnableFailBack(boolean enableFailBack) {
		this.enableFailBack = enableFailBack;
	}

	public boolean isEnableHA() {
		return haStatus == HASTATUS_ENABLE;
	}

	public String getHaSecret() {
		return haSecret;
	}

	public void setHaSecret(String haSecret) {
		this.haSecret = haSecret;
	}

	public String getMasterHostNameSticky() {
		return masterHostNameSticky;
	}

	public void setMasterHostNameSticky(String masterHostNameSticky) {
		this.masterHostNameSticky = masterHostNameSticky;
	}

	public String getPrimaryHostName() {
		return primaryHostName;
	}

	public void setPrimaryHostName(String primaryHostName) {
		this.primaryHostName = primaryHostName;
	}

	public String getPrimaryLANIP() {
		return primaryLANIP;
	}

	public void setPrimaryLANIP(String primaryLANIP) {
		this.primaryLANIP = primaryLANIP == null ? "" : primaryLANIP;
	}

	public String getPrimaryLANNetmask() {
		return primaryLANNetmask;
	}

	public void setPrimaryLANNetmask(String primaryLANNetmask) {
		this.primaryLANNetmask = primaryLANNetmask == null ? "" : primaryLANNetmask;
	}

	public String getPrimaryMGTIP() {
		return primaryMGTIP;
	}

	public void setPrimaryMGTIP(String primaryMGTIP) {
		this.primaryMGTIP = primaryMGTIP;
	}

	public String getPrimaryMGTNetmask() {
		return primaryMGTNetmask;
	}

	public void setPrimaryMGTNetmask(String primaryMGTNetmask) {
		this.primaryMGTNetmask = primaryMGTNetmask;
	}

	public String getSecondaryHostName() {
		return secondaryHostName;
	}

	public void setSecondaryHostName(String secondaryHostName) {
		this.secondaryHostName = secondaryHostName;
	}

	public String getSecondaryLANIP() {
		return secondaryLANIP;
	}

	public void setSecondaryLANIP(String secondaryLANIP) {
		this.secondaryLANIP = secondaryLANIP == null ? "" : secondaryLANIP;
	}

	public String getSecondaryLANNetmask() {
		return secondaryLANNetmask;
	}

	public void setSecondaryLANNetmask(String secondaryLANNetmask) {
		this.secondaryLANNetmask = secondaryLANNetmask == null ? "" : secondaryLANNetmask;
	}

	public String getSecondaryMGTIP() {
		return secondaryMGTIP;
	}

	public void setSecondaryMGTIP(String secondaryMGTIP) {
		this.secondaryMGTIP = secondaryMGTIP;
	}

	public String getSecondaryMGTNetmask() {
		return secondaryMGTNetmask;
	}

	public void setSecondaryMGTNetmask(String secondaryMGTNetmask) {
		this.secondaryMGTNetmask = secondaryMGTNetmask;
	}

	// ------------implement interface
	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getLabel() {
		return "HA Settings";
	}

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "OWNER", nullable = false)
	// private HmDomain owner;
	//
	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
		// this.owner = owner;
	}

	@Version
	private Timestamp version;

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
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
	public void setId(Long id) {
		this.id = id;
	}

	public String getPrimarySystemId() {
		return primarySystemId;
	}

	public void setPrimarySystemId(String primarySystemId) {
		this.primarySystemId = primarySystemId;
	}

	public String getSecondarySystemId() {
		return secondarySystemId;
	}

	public void setSecondarySystemId(String secondarySystemId) {
		this.secondarySystemId = secondarySystemId;
	}

	public String getPrimaryDefaultGateway() {
		return primaryDefaultGateway;
	}

	public void setPrimaryDefaultGateway(String primaryDefaultGateway) {
		this.primaryDefaultGateway = primaryDefaultGateway;
	}

	public String getSecondaryDefaultGateway() {
		return secondaryDefaultGateway;
	}

	public void setSecondaryDefaultGateway(String secondaryDefaultGateway) {
		this.secondaryDefaultGateway = secondaryDefaultGateway;
	}

	public byte getHaStatus() {
		return haStatus;
	}

	public void setHaStatus(byte haStatus) {
		this.haStatus = haStatus;
	}

	public String getHaNotifyEmail() {
		return haNotifyEmail;
	}

	public void setHaNotifyEmail(String haNotifyEmail) {
		this.haNotifyEmail = haNotifyEmail;
	}

	public int getHeartbeatTimeOutValue() {
		return heartbeatTimeOutValue;
	}

	public void setHeartbeatTimeOutValue(int heartbeatTimeOutValue) {
		this.heartbeatTimeOutValue = heartbeatTimeOutValue;
	}

}