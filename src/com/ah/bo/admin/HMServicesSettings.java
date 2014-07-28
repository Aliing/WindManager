package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Range;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.IpAddress;

@Entity
@Table(name = "hmservicessettings")
public class HMServicesSettings implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long				id;

	private boolean				enableClientRefresh;
	
    private boolean             enableClientManagement;
    
    private boolean             enableCidPolicyEnforcement;
    
    private boolean             enableCustomerCa;
	
	private String              apiKey;

	private int					refreshInterval;

	private String				refreshFilterName;

	private int					sessionExpiration;

	private boolean				infiniteSession;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SNMPRECEIVERIP")
	private IpAddress			snmpReceiverIP;

	private String				snmpCommunity;

	private boolean				showNotifyInfo;

	private String				notifyInformationTitle;

	@Column(length = 1024)
	private String				notifyInformation;

	public static final short	HM_OLINE_STATUS_NORMAL		= 1;

	public static final short	HM_OLINE_STATUS_MAINT		= 2;

	// it is used for maintenance mode of this HM
	private short				hmStatus					= HM_OLINE_STATUS_NORMAL;

	private String 				virtualHostName;
	
	@Column(length = 255)
	private String				classifierTag;

	private boolean				enableTeacher;
    private boolean             presenceEnable;
       
	public static final int		AP_SLA_STATS_ALL			= 1;
	public static final int		AP_SLA_STATS_SLA			= 2;
	public static final int		AP_SLA_STATS_CRC			= 3;
	public static final int		AP_SLA_STATS_TXDROP			= 4;
	public static final int		AP_SLA_STATS_RXDROP			= 5;
	public static final int		AP_SLA_STATS_TXRETRY		= 6;
	public static final int		AP_SLA_STATS_AIRTIME		= 7;

	public static final int		CLIENT_SLA_STATS_ALL		= 1;
	public static final int		CLIENT_SLA_STATS_SLA		= 2;
	public static final int		CLIENT_SLA_STATS_AIRTIME	= 3;
	public static final int		CLIENT_SLA_STATS_SCORE		= 4;

	private int					apSlaType					= AP_SLA_STATS_ALL;
	private int					clientSlaType				= CLIENT_SLA_STATS_ALL;

	private boolean				enableProxy;

	private String				proxyServer;

	private int					proxyPort;

	private String				proxyUserName;

	private String				proxyPassword;

	private boolean 			enableTVProxy;
	
	private short 			    enableCaseSensitive          = 1;

	@Column(length = 128)
	private String 				tvProxyIP;

	private int 				tvProxyPort=3128;

	@Column(length = 128)
	private String 				tvAutoProxyFile;

	private int 				snpMaximum=20;

	public static final int MAX_HIVEOS_SOFTVER_UPDATE_NUM = 25;
	private int 				maxUpdateNum = MAX_HIVEOS_SOFTVER_UPDATE_NUM;


	public static final byte DEFAULT_CONCURRENT_CONFIG_GEN_NUM = 3;
	public static final byte MAX_CONCURRENT_CONFIG_GEN_NUM = 10;
	public static final byte MIN_CONCURRENT_CONFIG_GEN_NUM = 1;

	@Range(min = MIN_CONCURRENT_CONFIG_GEN_NUM, max = MAX_CONCURRENT_CONFIG_GEN_NUM)
	private byte 				concurrentConfigGenNum  = DEFAULT_CONCURRENT_CONFIG_GEN_NUM;

	public static final byte DEFAULT_CONCURRENT_SEARCH_USER_NUM = 1;
	public static final byte MAX_CONCURRENT_SEARCH_USER_NUM = 10;
	public static final byte MIN_CONCURRENT_SEARCH_USER_NUM = 1;

	private byte 				concurrentSearchUserNum  = DEFAULT_CONCURRENT_SEARCH_USER_NUM;
	
	private boolean enabledBetaIDM;
	
	public static final String DATE_FORMAT_1 = "MM dd yyyy";
	public static final String DATE_FORMAT_2 = "dd MM yyyy";
	public static final String DATE_SEPARATOR_1 = "/";
	public static final String DATE_SEPARATOR_2 = "-";
	public static final String TIME_FORMAT_1 = "HH:mm:ss";
	public static final String TIME_FORMAT_2 = "hh:mm:ss a";
	
	public static final short DATE_FORMAT_TYPE_1 = 1;
	public static final short DATE_FORMAT_TYPE_2 = 2;
	public static final short TIME_FORMAT_TYPE_1 = 1;
	public static final short TIME_FORMAT_TYPE_2 = 2;
	public static final short DATE_SEPARATOR_TYPE_1 = 1;
	public static final short DATE_SEPARATOR_TYPE_2 = 2;
	
	public static final short TIME_TYPE_1 = 12;
	public static final short TIME_TYPE_2 = 24;
	
	private short timeType = TIME_TYPE_1;
	private short dateFormat = DATE_FORMAT_TYPE_1;
	private short timeFormat = TIME_FORMAT_TYPE_1;
	private short dateSeparator = DATE_SEPARATOR_TYPE_1;
	
	private boolean enableCollectAppData = false;
	
	private boolean enableRadarDetection;
	
	private boolean enableSystemL7Switch = true;
	
	private boolean notifyUpdateWatchList;
	
	private boolean notifyCleanWatchList;
	
	private boolean notifyDisableL7;
	
	@Column
	private String apiUserName;
	@Column
	private String apiPassword;
	@Column
	private boolean enableApiAccess;
	
    private boolean	enableSupplementalCLI;
	
	public boolean isNotifyDisableL7() {
		return notifyDisableL7;
	}

	public void setNotifyDisableL7(boolean notifyDisableL7) {
		this.notifyDisableL7 = notifyDisableL7;
	}

	public boolean isNotifyUpdateWatchList() {
		return notifyUpdateWatchList;
	}

	public void setNotifyUpdateWatchList(boolean notifyUpdateWatchList) {
		this.notifyUpdateWatchList = notifyUpdateWatchList;
	}

	public boolean isNotifyCleanWatchList() {
		return notifyCleanWatchList;
	}

	public void setNotifyCleanWatchList(boolean notifyCleanWatchList) {
		this.notifyCleanWatchList = notifyCleanWatchList;
	}

	public boolean isEnableSystemL7Switch() {
		return enableSystemL7Switch;
	}

	public void setEnableSystemL7Switch(boolean enableSystemL7Switch) {
		this.enableSystemL7Switch = enableSystemL7Switch;
	}

	public boolean isEnableClientManagement(){
		return this.enableClientManagement;
	}
	
	public void setEnableClientManagement(boolean enableClientManagement){
		this.enableClientManagement = enableClientManagement;
	}
	
	public boolean isEnableCidPolicyEnforcement(){
		return this.enableCidPolicyEnforcement;
	}
	
	public void setEnableCidPolicyEnforcement(boolean enableCidPolicyEnforcement){
		this.enableCidPolicyEnforcement = enableCidPolicyEnforcement;
	}
	
	public boolean isEnableCustomerCa(){
		return this.enableCustomerCa;
	}
	
	public void setEnableCustomerCa(boolean enableCustomerCa){
		this.enableCustomerCa = enableCustomerCa;
	}
	
	public String getApiKey(){
		return this.apiKey;
	}
	
	public void setApiKey(String apiKey){
		this.apiKey = apiKey;
	}

	public boolean isEnableCollectAppData() {
		return enableCollectAppData;
	}

	public void setEnableCollectAppData(boolean enableCollectAppData) {
		this.enableCollectAppData = enableCollectAppData;
	}

	public boolean isEnableProxy() {
		return enableProxy;
	}

	public void setEnableProxy(boolean enableProxy) {
		this.enableProxy = enableProxy;
	}

	public String getProxyServer() {
		return proxyServer;
	}

	public void setProxyServer(String proxyServer) {
		this.proxyServer = proxyServer;
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

	public boolean isEnableTeacher() {
		return enableTeacher;
	}

	public void setEnableTeacher(boolean enableTeacher) {
		this.enableTeacher = enableTeacher;
	}

	public boolean isPresenceEnable() {
		return presenceEnable;
	}

	public void setPresenceEnable(boolean presenceEnable) {
		this.presenceEnable = presenceEnable;
	}

	public String getNotifyInformation() {
		return notifyInformation;
	}

	public void setNotifyInformation(String notifyInformation) {
		this.notifyInformation = notifyInformation;
	}

	public boolean isShowNotifyInfo() {
		return showNotifyInfo;
	}

	public void setShowNotifyInfo(boolean showNotifyInfo) {
		this.showNotifyInfo = showNotifyInfo;
	}

	public int getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(int refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	// ------------implement interface
	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getLabel() {
		return "Management Settings";
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain	owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
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

	public int getSessionExpiration() {
		return sessionExpiration;
	}

	public void setSessionExpiration(int sessionExpiration) {
		this.sessionExpiration = sessionExpiration;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public boolean isInfiniteSession() {
		return infiniteSession;
	}

	public void setInfiniteSession(boolean infiniteSession) {
		this.infiniteSession = infiniteSession;
	}

	public boolean isEnableClientRefresh() {
		return enableClientRefresh;
	}

	public void setEnableClientRefresh(boolean enableClientRefresh) {
		this.enableClientRefresh = enableClientRefresh;
	}

	public String getRefreshFilterName() {
		return refreshFilterName;
	}

	public void setRefreshFilterName(String refreshFilterName) {
		this.refreshFilterName = refreshFilterName;
	}

	public String getSnmpCommunity() {
		return snmpCommunity;
	}

	public void setSnmpCommunity(String snmpCommunity) {
		this.snmpCommunity = snmpCommunity;
	}

	public IpAddress getSnmpReceiverIP() {
		return snmpReceiverIP;
	}

	public void setSnmpReceiverIP(IpAddress snmpReceiverIP) {
		this.snmpReceiverIP = snmpReceiverIP;
	}

	public String getNotifyInformationTitle() {
		return notifyInformationTitle;
	}

	public void setNotifyInformationTitle(String notifyInformationTitle) {
		this.notifyInformationTitle = notifyInformationTitle;
	}

	public short getHmStatus() {
		return hmStatus;
	}

	public void setHmStatus(short hmStatus) {
		this.hmStatus = hmStatus;
	}

	// /**
	// * @return the clientScore
	// */
	// public boolean isClientScore() {
	// return clientScore;
	// }
	//
	// /**
	// * @param clientScore the clientScore to set
	// */
	// public void setClientScore(boolean clientScore) {
	// this.clientScore = clientScore;
	// }

	/**
	 * @return the apSlaType
	 */
	public int getApSlaType() {
		return apSlaType;
	}

	/**
	 * @param apSlaType
	 *            the apSlaType to set
	 */
	public void setApSlaType(int apSlaType) {
		this.apSlaType = apSlaType;
	}

	/**
	 * @return the clientSlaType
	 */
	public int getClientSlaType() {
		return clientSlaType;
	}

	/**
	 * @param clientSlaType
	 *            the clientSlaType to set
	 */
	public void setClientSlaType(int clientSlaType) {
		this.clientSlaType = clientSlaType;
	}

	public int getSnpMaximum() {
		return snpMaximum;
	}

	public void setSnpMaximum(int snpMaximum) {
		this.snpMaximum = snpMaximum;
	}

	// web security
	// Barracuda
	@Column(length = 40)
	private String authorizationKey;

	private String serviceHost;

	public final static short SERVICEPROT_DEFAULT_VALUE = 8080;

	private int servicePort = SERVICEPROT_DEFAULT_VALUE;

	private String windowsDomain;

	private String barracudaDefaultUserName;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "WEBSENSEWHITELIST_ID")
	private DomainObject websenseWhitelist;

	private boolean enableBarracuda;

	// WebSense
	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String accountID;

	private String securityKey;

	private String webSenseServiceHost = NmsUtil.getText("resources.hmResources","admin.management.webSecurity.websense.serviceHost.hosted");

	public final static short PORT_DEFAULT_VALUE = 8081;

	private int port = PORT_DEFAULT_VALUE;

	public final static short WEBSENSEMODE_HOSTED = 0;

	public final static short WEBSENSEMODE_HYBRID = 1;

	private short wensenseMode = WEBSENSEMODE_HOSTED;

	private String defaultDomain;
	
	private String webSenseDefaultUserName;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BARRACUDAWHITELIST_ID")
	private DomainObject barracudaWhitelist;

	private boolean enableWebsense;
	
	/***OpenDNS Settings****/
	private boolean enableOpenDNS;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OPENDNS_ACCOUNT_ID")
	private OpenDNSAccount openDNSAccount;
	
	@Transient
	private String editOpenDNSInfo;
	@Transient
	private String[] openDNSUserProfileNames;
	@Transient
	private String[] openDNSDeviceIDLabels;
	
	public String getEditOpenDNSInfo() {
		return editOpenDNSInfo;
	}

	public void setEditOpenDNSInfo(String editOpenDNSInfo) {
		this.editOpenDNSInfo = editOpenDNSInfo;
	}

	public OpenDNSAccount getOpenDNSAccount() {
		return openDNSAccount;
	}

	public void setOpenDNSAccount(OpenDNSAccount openDNSAccount) {
		this.openDNSAccount = openDNSAccount;
	}

	public String[] getOpenDNSUserProfileNames() {
		return openDNSUserProfileNames;
	}

	public void setOpenDNSUserProfileNames(String[] openDNSUserProfileNames) {
		this.openDNSUserProfileNames = openDNSUserProfileNames;
	}

	public String[] getOpenDNSDeviceIDLabels() {
		return openDNSDeviceIDLabels;
	}

	public void setOpenDNSDeviceIDLabels(String[] openDNSDeviceIDLabels) {
		this.openDNSDeviceIDLabels = openDNSDeviceIDLabels;
	}

	public boolean isEnableOpenDNS() {
		return enableOpenDNS;
	}

	public void setEnableOpenDNS(boolean enableOpenDNS) {
		this.enableOpenDNS = enableOpenDNS;
	}

	public String getAuthorizationKey() {
		return authorizationKey;
	}

	public void setAuthorizationKey(String authorizationKey) {
		this.authorizationKey = authorizationKey;
	}

	public String getServiceHost() {
		return serviceHost;
	}

	public void setServiceHost(String serviceHost) {
		this.serviceHost = serviceHost;
	}

	public int getServicePort() {
		return servicePort;
	}

	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}

	public String getWindowsDomain() {
		return windowsDomain;
	}

	public void setWindowsDomain(String windowsDomain) {
		this.windowsDomain = windowsDomain;
	}

	public String getAccountID() {
		return accountID;
	}

	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}

	public String getWebSenseServiceHost() {
		return webSenseServiceHost;
	}

	public void setWebSenseServiceHost(String webSenseServiceHost) {
		this.webSenseServiceHost = webSenseServiceHost;
	}

	public String getWebSenseDefaultUserName() {
		return webSenseDefaultUserName;
	}

	public void setWebSenseDefaultUserName(String webSenseDefaultUserName) {
		this.webSenseDefaultUserName = webSenseDefaultUserName;
	}

	public String getBarracudaDefaultUserName() {
		return barracudaDefaultUserName;
	}

	public void setBarracudaDefaultUserName(String barracudaDefaultUserName) {
		this.barracudaDefaultUserName = barracudaDefaultUserName;
	}

	public int getMaxUpdateNum() {
		return maxUpdateNum;
	}

	public void setMaxUpdateNum(int maxUpdateNum) {
		this.maxUpdateNum = maxUpdateNum;
	}

	public String getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public short getWensenseMode() {
		return wensenseMode;
	}

	public void setWensenseMode(short wensenseMode) {
		this.wensenseMode = wensenseMode;
	}

	public byte getConcurrentConfigGenNum() {
		return concurrentConfigGenNum;
	}

	public void setConcurrentConfigGenNum(byte concurrentConfigGenNum) {
		this.concurrentConfigGenNum = concurrentConfigGenNum;
	}

	public String getDefaultDomain() {
		return defaultDomain;
	}

	public void setDefaultDomain(String defaultDomain) {
		this.defaultDomain = defaultDomain;
	}

	public DomainObject getWebsenseWhitelist() {
		return websenseWhitelist;
	}

	public void setWebsenseWhitelist(DomainObject websenseWhitelist) {
		this.websenseWhitelist = websenseWhitelist;
	}

	public DomainObject getBarracudaWhitelist() {
		return barracudaWhitelist;
	}

	public void setBarracudaWhitelist(DomainObject barracudaWhitelist) {
		this.barracudaWhitelist = barracudaWhitelist;
	}

	public byte getConcurrentSearchUserNum() {
		return concurrentSearchUserNum;
	}

	public void setConcurrentSearchUserNum(byte concurrentSearchUserNum) {
		this.concurrentSearchUserNum = concurrentSearchUserNum;
	}

	public boolean isEnableBarracuda() {
		return enableBarracuda;
	}

	public void setEnableBarracuda(boolean enableBarracuda) {
		this.enableBarracuda = enableBarracuda;
	}

	public boolean isEnableWebsense() {
		return enableWebsense;
	}

	public void setEnableWebsense(boolean enableWebsense) {
		this.enableWebsense = enableWebsense;
	}

	public String getVirtualHostName() {
		return virtualHostName;
	}

	public void setVirtualHostName(String virtualHostName) {
		this.virtualHostName = virtualHostName;
	}

	public void setEnableTVProxy(boolean enableTVProxy) {
		this.enableTVProxy = enableTVProxy;
	}

	public boolean isEnableTVProxy() {
		return enableTVProxy;
	}

	public void setTvProxyIP(String tvProxyIP) {
		this.tvProxyIP = tvProxyIP;
	}

	public String getTvProxyIP() {
		return tvProxyIP;
	}

	public void setTvProxyPort(int tvProxyPort) {
		this.tvProxyPort = tvProxyPort;
	}

	public int getTvProxyPort() {
		return tvProxyPort;
	}

	public void setTvAutoProxyFile(String tvAutoProxyFile) {
		this.tvAutoProxyFile = tvAutoProxyFile;
	}

	public String getTvAutoProxyFile() {
		return tvAutoProxyFile;
	}

    public boolean isEnabledBetaIDM() {
        return enabledBetaIDM;
    }

    public void setEnabledBetaIDM(boolean enabledBetaIDM) {
        this.enabledBetaIDM = enabledBetaIDM;
    }
    
    public String getClassifierTag() {
		return classifierTag;
	}

	public void setClassifierTag(String classifierTag) {
		this.classifierTag = classifierTag;
	}

	public short getTimeType() {
		return timeType;
	}

	public void setTimeType(short timeType) {
		this.timeType = timeType;
	}

	public short getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(short dateFormat) {
		this.dateFormat = dateFormat;
	}

	public short getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(short timeFormat) {
		this.timeFormat = timeFormat;
	}

	public short getDateSeparator() {
		return dateSeparator;
	}

	public void setDateSeparator(short dateSeparator) {
		this.dateSeparator = dateSeparator;
	}

	public short getEnableCaseSensitive() {
		return enableCaseSensitive;
	}

	public void setEnableCaseSensitive(short enableCaseSensitive) {
		this.enableCaseSensitive = enableCaseSensitive;
	}
	
	public boolean isEnableRadarDetection() {
		return enableRadarDetection;
	}

	public void setEnableRadarDetection(boolean enableRadarDetection) {
		this.enableRadarDetection = enableRadarDetection;
	}

	public String getApiUserName() {
		return apiUserName;
	}

	public void setApiUserName(String apiUserName) {
		this.apiUserName = apiUserName;
	}

	public String getApiPassword() {
		return apiPassword;
	}

	public void setApiPassword(String apiPassword) {
		this.apiPassword = apiPassword;
	}

	public boolean isEnableApiAccess() {
		return enableApiAccess;
	}

	public void setEnableApiAccess(boolean enableApiAccess) {
		this.enableApiAccess = enableApiAccess;
	}

	public boolean isEnableSupplementalCLI() {
		return enableSupplementalCLI;
	}

	public void setEnableSupplementalCLI(boolean enableSupplementalCLI) {
		this.enableSupplementalCLI = enableSupplementalCLI;
	}
	
}