package com.ah.bo.wlan;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.be.cloudauth.HmCloudAuthCertMgmtImpl;
import com.ah.be.cloudauth.IDMConfig;
import com.ah.be.parameter.BeParaModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.AirScreenRuleGroup;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.Vlan;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.TX11aOr11gRateSetting.ARateType;
import com.ah.bo.wlan.TX11aOr11gRateSetting.GRateType;
import com.ah.bo.wlan.TX11aOr11gRateSetting.NRateType;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.monitor.enrolledclient.tools.URLUtils;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/*
 * @author Feeling
 */
@Entity
@Table(name = "SSID_PROFILE", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"OWNER", "SSIDNAME" }) })
@org.hibernate.annotations.Table(appliesTo = "SSID_PROFILE", indexes = {
		@Index(name = "SSID_PROFILE_OWNER", columnNames = { "OWNER" }),
		@Index(name = "SSID_PROFILE_OWNER_SSID", columnNames = { "OWNER", "SSID" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class SsidProfile implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

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

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String ssidName;
	
	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String ssid;

	private boolean hide;

	private boolean broadcase;

	private int encryption;

	private int authentication;

	private int mgmtKey;

	private boolean defaultFlag;

	public static final int KEY_MGMT_OPEN = 0;

	public static final int KEY_MGMT_WPA2_EAP_802_1_X = 1;

	public static final int KEY_MGMT_WPA2_PSK = 2;

	public static final int KEY_MGMT_WPA_EAP_802_1_X = 3;

	public static final int KEY_MGMT_WPA_PSK = 4;

	public static final int KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X = 5;

	public static final int KEY_MGMT_AUTO_WPA_OR_WPA2_PSK = 6;

	public static final int KEY_MGMT_WEP_PSK = 7;

	public static final int KEY_MGMT_DYNAMIC_WEP = 8;

	public static final int KEY_ENC_NONE = 0;

	public static final int KEY_ENC_CCMP = 1;

	public static final int KEY_ENC_TKIP = 2;

	public static final int KEY_ENC_WEP104 = 3;

	public static final int KEY_ENC_WEP40 = 4;

	public static final int KEY_ENC_AUTO_TKIP_OR_CCMP = 5;

	public static final int KEY_AUT_OPEN = 0;

	public static final int KEY_AUT_EAP = 1;

	public static final int KEY_AUT_SHARED = 2;

	public static EnumItem[] ENUM_KEY_MGMT = MgrUtil.enumItems("enum.keyMgmt.",
			new int[] { KEY_MGMT_OPEN, KEY_MGMT_WPA2_EAP_802_1_X,
					KEY_MGMT_WPA2_PSK, KEY_MGMT_WPA_EAP_802_1_X,
					KEY_MGMT_WPA_PSK, KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X,
					KEY_MGMT_AUTO_WPA_OR_WPA2_PSK, KEY_MGMT_WEP_PSK,
					KEY_MGMT_DYNAMIC_WEP });
	
	public static final int ACCESS_MODE_WPA = 1;
	public static final int ACCESS_MODE_PSK = 2;
	public static final int ACCESS_MODE_8021X = 3;
	public static final int ACCESS_MODE_WEP = 4;
	public static final int ACCESS_MODE_OPEN = 5;
	private int accessMode = ACCESS_MODE_OPEN;
	
	public static final int OPEN_SSID_MODLE = 0;
	public static final int	SINGLE_SSIDL_MODEL = 1;
	private int privateSsidModel = OPEN_SSID_MODLE;
	
	@Range(min = 1, max = 255)
	private int dtimSetting = 1;

	@Range(min = 1, max = 2346)
	private int rtsThreshold = 2346;

	@Range(min = 256, max = 2346)
	private int fragThreshold = 2346;
	
	@Range(min = 10, max = 36000)
	private int updateInterval = 60;
	
	@Range(min = 1, max = 1000)
	private int ageOut = 60;
	
	@Range(min = 1, max = 100)
	private int maxClient=100;
	
	@Range(min = 1, max = 30)
	private int clientAgeOut=5;
	
	public static final int LOCAL_CACHE_TIMEOUT = 86400;
	@Range(min = 60, max = 604800)
	private int localCacheTimeout = LOCAL_CACHE_TIMEOUT;
	
	private int eapTimeOut = 30;
	
	private int eapRetries = 3;

	private boolean preauthenticationEnabled;

	private boolean macAuthEnabled;
	
	private boolean cwpSelectEnabled;
	
	private boolean enableProvisionEnterprise;
	
	private boolean enableProvisionPersonal;
	
	private boolean enableProvisionPrivate;
	
	private boolean enabledSocialLogin;
	
	@Transient
	public boolean isEnabledCM(){
		boolean result = enableProvisionEnterprise || enableProvisionPersonal || enableProvisionPrivate;
		if(result){
			HMServicesSettings hmService = QueryUtil.findBoByAttribute(HMServicesSettings.class,"owner",getOwner());
			result = hmService == null ? false :hmService.isEnableClientManagement();
		}
		
		
		return result ;
	}
	
	public boolean isEnableProvisionEnterprise() {
		return enableProvisionEnterprise;
	}
	
	public void setEnableProvisionEnterprise(boolean enableProvisionEnterprise) {
		this.enableProvisionEnterprise = enableProvisionEnterprise;
		onboardSsid = enableProvisionEnterprise ? ssid : null;
	}
	
	public boolean isEnableProvisionPersonal() {
		return enableProvisionPersonal;
	}
	
	public void setEnableProvisionPersonal(boolean enableProvisionPersonal) {
		this.enableProvisionPersonal = enableProvisionPersonal;
	}
	
	public boolean isEnableProvisionPrivate() {
		return enableProvisionPrivate;
	}
	
	public void setEnableProvisionPrivate(boolean enableProvisionPrivate) {
		this.enableProvisionPrivate = enableProvisionPrivate;
	}
	//Add to enable Registration With Single Ssid
	
	private boolean enableSingleSsid;
	
	private String singleSsidValue;
	
	public boolean isEnableSingleSsid() {
		if(isEnableProvisionPrivate() && privateSsidModel == SINGLE_SSIDL_MODEL ){
			return true;
		}
		return false;
	}

	public void setEnableSingleSsid(boolean enableSingleSsid) {
		this.enableSingleSsid = enableSingleSsid;
	}

	public String getSingleSsidValue() {
		return singleSsidValue;
	}

	public void setSingleSsidValue(String singleSsidValue) {
		this.singleSsidValue = singleSsidValue;
	}

	public int getPrivateSsidModel() {
		return privateSsidModel;
	}

	public void setPrivateSsidModel(int privateSsidModel) {
		this.privateSsidModel = privateSsidModel;
	}

	public boolean isEnableAerohiveMdm() {
		switch (accessMode) {
		case SsidProfile.ACCESS_MODE_WPA:
			return isEnableProvisionPersonal();
		case SsidProfile.ACCESS_MODE_PSK:
			return isEnableProvisionPrivate();
		case SsidProfile.ACCESS_MODE_8021X:
			return isEnableProvisionEnterprise();
		case SsidProfile.ACCESS_MODE_OPEN:
			return isEnableProvisionPrivate() || isEnableProvisionPersonal(); // registration
		default:
			return false;
		}
	}
	public String getWpaOpenSsid() {
		return wpaOpenSsid;
	}
	public void setWpaOpenSsid(String ssidPPSKKey) {
		this.wpaOpenSsid = ssidPPSKKey;
	}
	private String wpaOpenSsid;
	private boolean fallBackToEcwp;

	private boolean enabledwmm = true;

	private boolean enabledUnscheduled;

	private boolean enabledLegacy;
	
	// Enable IDM for per SSID, enableCA4PPSK
	private boolean enabledIDM;
    
	private int defaultAction;
	
	//Enable voice-enterprise for SSID
	private boolean enabledVoiceEnterprise = false;
	
	//Enable 80211k for SSID
	private boolean enabled80211k = false;

	//Enable 80211v for SSID
	private boolean enabled80211v = false;

	//Enable 80211r for SSID
	private boolean enabled80211r = false;
	
	//Enable WMM-AC for SSID
	public boolean enabledAcBesteffort = false;
	public boolean enabledAcBackground = false;
	public boolean enabledAcVideo = false;
	public boolean enabledAcVoice = false;

	@Column(length = 256)
	private String comment;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "SSID_PROFILE_SCHEDULER", joinColumns = { @JoinColumn(name = "SSID_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "SCHEDULER_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<Scheduler> schedulers = new HashSet<Scheduler>();

	public Set<Scheduler> getSchedulers() {
		return schedulers;
	}

	public void setSchedulers(Set<Scheduler> schedulers) {
		this.schedulers = schedulers;
	}

	@Embedded
	private SsidSecurity ssidSecurity;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SSID_DOS_ID")
	private DosPrevention ssidDos;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "STATION_DOS_ID")
	private DosPrevention stationDos;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CONFIGMDM_ID")
	private ConfigTemplateMdm configmdmId;
	 private boolean enableMDM =false;

	public boolean isEnableMDM() {
		return enableMDM;
	}

	public void setEnableMDM(boolean enableMDM) {
		this.enableMDM = enableMDM;
	}

	public boolean isEnableMdmBusiness() {
		return isEnableMDM() || isEnableAerohiveMdm();
	}
	public ConfigTemplateMdm getConfigmdmId() {
		return configmdmId;
	}

	public void setConfigmdmId(ConfigTemplateMdm configmdmId) {
		this.configmdmId = configmdmId;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "IP_DOS_ID")
	private DosPrevention ipDos;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CWP_ID")
	private Cwp cwp;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CWP_USERPOLICY_ID")
	private Cwp userPolicy;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SERVICE_FILTER_ID")
	private ServiceFilter serviceFilter;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AS_RULE_GROUP_ID")
	private AirScreenRuleGroup asRuleGroup;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RADIUS_SERVICE_ASSIGN_ID")
	private RadiusAssignment radiusAssignment;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RADIUS_SERVICE_ASSIGN_ID_PPSK")
	private RadiusAssignment radiusAssignmentPpsk;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "USERPROFILE_SELFREG_ID")
	private UserProfile userProfileSelfReg;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "USERPROFILE_DEFAULT_ID")
	private UserProfile userProfileDefault;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERPROFILE_GUEST_ID")
    private UserProfile userProfileGuest; // for AirWatch with non-compliance notification enabled
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "SSID_PROFILE_USER_PROFILE", joinColumns = { @JoinColumn(name = "SSID_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_PROFILE_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<UserProfile> radiusUserProfile = new HashSet<UserProfile>();
	
	public static final short		DENY_ACTION_BAN				= 1;
	public static final short		DENY_ACTION_BAN_FOREVER		= 2;
	public static final short		DENY_ACTION_DISCONNECT		= 3;
	public static EnumItem[] DENY_ACTION = MgrUtil.enumItems(
			"enum.denyAction.", new int[] { DENY_ACTION_BAN, DENY_ACTION_BAN_FOREVER,
					DENY_ACTION_DISCONNECT });

	private short denyAction=DENY_ACTION_DISCONNECT;
	
	public static final int DEFAULT_ACTION_TIME = 60;
	@Range(min=1, max=100000000)
	private long actionTime=DEFAULT_ACTION_TIME;
	
	private boolean chkUserOnly;
	
	private boolean chkDeauthenticate;

	private boolean enableGRateSet = true;

	private boolean enableARateSet = true;
	
	private boolean enableNRateSet = true;
	
	private boolean enableACRateSet = true;

	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="mapkey")
	@CollectionTable(name = "G_RATE_SETTING_INFO", joinColumns = @JoinColumn(name = "SSID_PROFILE_ID"))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Map<String, TX11aOr11gRateSetting> gRateSets = new HashMap<String, TX11aOr11gRateSetting>();

	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="mapkey")
	@CollectionTable(name = "A_RATE_SETTING_INFO", joinColumns = @JoinColumn(name = "SSID_PROFILE_ID"))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Map<String, TX11aOr11gRateSetting> aRateSets = new HashMap<String, TX11aOr11gRateSetting>();

	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="mapkey")
	@CollectionTable(name = "N_RATE_SETTING_INFO", joinColumns = @JoinColumn(name = "SSID_PROFILE_ID"))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Map<String, TX11aOr11gRateSetting> nRateSets = new HashMap<String, TX11aOr11gRateSetting>();
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "AC_RATE_SETTING_INFO", joinColumns = @JoinColumn(name = "SSID_PROFILE_ID"))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<Tx11acRateSettings> acRateSets = new ArrayList<Tx11acRateSettings>();

	public static final int RADIOMODE_BG = 1;
	public static final int RADIOMODE_A = 2;
	public static final int RADIOMODE_BOTH = 3;
	public static EnumItem[] ENUM_RADIOMODE = MgrUtil.enumItems(
			"enum.wlanPolicy.radioProfileMode.", new int[] { RADIOMODE_BG,RADIOMODE_A,
					RADIOMODE_BOTH });
	private int radioMode = RADIOMODE_BOTH;
	
	private boolean enabledDefaultSetting;
	
	private boolean enabledUseGuestManager;
	
	private int personPskRadiusAuth=1;
	
	/**
	 * add for ppsk self reg
	 */
	private boolean enablePpskSelfReg;
	
	private String ppskServerIp="";
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PPSK_CWP_ID")
	private Cwp ppskECwp;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "WPA_CWP_ID")
	private Cwp wpaECwp;
	public Cwp getWpaECwp() {
		return wpaECwp;
	}
	public void setWpaECwp(Cwp wpaECwp) {
		this.wpaECwp = wpaECwp;
	}
	private String ppskOpenSsid="";
	
	@Transient
	private String onboardSsid;
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "GROUP_ID")
//	private PersonalizedPskGroup pskGroup;
	
//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(name = "SSID_PRIVATE_PSK", joinColumns = { @JoinColumn(name = "SSID_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "PRIVATE_PSK_ID") })
//	private Set<PersonalizedPsk> privatePsks = new HashSet<PersonalizedPsk>();
	
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "SSID_LOCAL_USER_GROUP", joinColumns = { @JoinColumn(name = "SSID_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "LOCAL_USER_GROUP_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<LocalUserGroup> localUserGroups = new HashSet<LocalUserGroup>();
	
	/**
	 * This set of local user groups is used for RADIUS attribute mapping.
	 * When RADIUS attribute mapping is enabled in SSID, local user groups have to be configured.
	 * 
	 * This is added in Dakar release.
	 * 
	 * Jianliang Chen
	 * 2012-03-30
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "SSID_RADIUS_USER_GROUP", joinColumns = { @JoinColumn(name = "SSID_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "LOCAL_USER_GROUP_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<LocalUserGroup> radiusUserGroups = new HashSet<LocalUserGroup>();
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "SSID_PROFILE_MAC_FILTER", joinColumns = { @JoinColumn(name = "SSID_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "MAC_FILTER_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<MacFilter> macFilters = new HashSet<MacFilter>();
	
	private boolean enableOsDection;
	
	// fnr add for easy mode user profile
	private boolean userInternetAccess = true;
	private int userRatelimit = 3000;
	private int userPskMethod = LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK;
	private boolean showExpressUserAccess = true;
	private int newRadiusType=Cwp.CWP_EXTERNAL;
	
	public static final int USER_CATEGORY_EMPLOOYEE = 1;
	public static final int USER_CATEGORY_GUEST = 2;
	public static final int USER_CATEGORY_VOICE = 3;
	public static final int USER_CATEGORY_CUSTOM = 4;
	private int userCategory=USER_CATEGORY_EMPLOOYEE;
	
	public static final int AUTH_SEQUENCE_MAC_SSID_CWP=1;
	public static final int AUTH_SEQUENCE_MAC_CWP_SSID=2;
	public static final int AUTH_SEQUENCE_SSID_MAC_CWP=3;
	public static final int AUTH_SEQUENCE_SSID_CWP_MAC=4;
	public static final int AUTH_SEQUENCE_CWP_MAC_SSID=5;
	public static final int AUTH_SEQUENCE_CWP_SSID_MAC=6;
	private int authSequence=AUTH_SEQUENCE_MAC_SSID_CWP;
	public static EnumItem[] ENUM_AUTH_SEQUENCE = MgrUtil.enumItems(
			"enum.ssid.auth.sequence.", new int[] { 
					AUTH_SEQUENCE_MAC_SSID_CWP,AUTH_SEQUENCE_MAC_CWP_SSID,
					AUTH_SEQUENCE_SSID_MAC_CWP,AUTH_SEQUENCE_SSID_CWP_MAC,
					AUTH_SEQUENCE_CWP_MAC_SSID,AUTH_SEQUENCE_CWP_SSID_MAC});
	
	@Transient
	private int userNumberPsk = 200;
	@Transient
	private Vlan userVlan;
	
	@Transient
	private boolean blnUserManager;
	//end add 
	
	// Transient value for new radius 
	@Transient
	private String newRadiusName;
	@Transient
	private Long newRadiusPrimaryIp;
	@Transient
	private String newRadiusSecret;
	@Transient
	private Long newRadiusSecondaryIp;
	@Transient
	private String newRadiusSecondSecret;
	@Transient
	private String newSelfUserProfileName;
	@Transient
	private short newSelfAttributeValue=1;
	@Transient
	private Long newSelfVlanId;
	@Transient
	private boolean newSelfBlnUserManager;
	
	@Transient
	private String newDefaultUserProfileName;
	@Transient
	private short newDefaultAttributeValue=1;
	@Transient
	private Long newDefaultVlanId;
	@Transient
	private boolean newDefaultBlnUserManager;
	
	@Transient
	private String newOptionUserProfileName;
	@Transient
	private short newOptionAttributeValue=1;
	@Transient
	private Long newOptionVlanId;
	@Transient
	private boolean newOptionBlnUserManager;
	
	@Transient
	private String selectNewHiveApRadiusPrimaryIp="";
	@Transient
	private String selectNewHiveApRadiusSecondaryIp="";
	
	// transient value for AD integration **Start** //
	// -----Radius server config------ //
	@Transient
	private String staticHiveAPIpAddress;
	@Transient
	private String staticHiveAPNetmask;
	@Transient
	private String staticHiveAPGateway;
	@Transient
	private String dnsServer;
	@Transient
	private boolean isPushedConfig; //use for push the configuration to HiveAP
	// -----AD integration config------ //
	private boolean enableADIntegration;
	@Transient
    private String adDomainFullName;
	@Transient
    private String adDomainAdmin;
	@Transient
    private String adDomainAdminPasswd;
	@Transient
    private String adDomainTestUser;
	@Transient
    private String adDomainTestUserPasswd;
	//--hidden field in jsp
	@Transient
	private String adDomainName;
	@Transient
    private String adServerIpAddress;
	@Transient
	private String baseDN;
	@Transient
	private int wifiPriority = -1;
	@Transient
	private short ldapSaslWrapping = 0;
	@Transient
	private boolean cloneFromPPSKServer = false;
	// transient value for AD integration **End** //
	
	public int getExpressEmployee() {
		if (userCategory!=USER_CATEGORY_GUEST) {
			return 1;
		}
		return 0;
	}
	
	public boolean getExpressUserCategoryVoice(){
		return userCategory == USER_CATEGORY_VOICE;
	}
	
	public void setExpressUserCategoryVoice(boolean bln){
		if (getId()==null) {
			if (userCategory!=USER_CATEGORY_GUEST){
				if (bln) {
					userCategory=USER_CATEGORY_VOICE;
				} else {
					userCategory=USER_CATEGORY_EMPLOOYEE;
				}
			} 
		}
	}
	
	public int getNewRadiusType() {
		return newRadiusType;
	}

	public void setNewRadiusType(int newRadiusType) {
		this.newRadiusType = newRadiusType;
	}

	public String getNewRadiusName() {
		return newRadiusName;
	}

	public void setNewRadiusName(String newRadiusName) {
		this.newRadiusName = newRadiusName;
	}

	public Long getNewRadiusPrimaryIp() {
		return newRadiusPrimaryIp;
	}

	public void setNewRadiusPrimaryIp(Long newRadiusPrimaryIp) {
		this.newRadiusPrimaryIp = newRadiusPrimaryIp;
	}

	public String getNewRadiusSecret() {
		return newRadiusSecret;
	}

	public void setNewRadiusSecret(String newRadiusSecret) {
		this.newRadiusSecret = newRadiusSecret;
	}

	public Long getNewRadiusSecondaryIp() {
		return newRadiusSecondaryIp;
	}

	public void setNewRadiusSecondaryIp(Long newRadiusSecondaryIp) {
		this.newRadiusSecondaryIp = newRadiusSecondaryIp;
	}

	public String getNewRadiusSecondSecret() {
		return newRadiusSecondSecret;
	}

	public void setNewRadiusSecondSecret(String newRadiusSecondSecret) {
		this.newRadiusSecondSecret = newRadiusSecondSecret;
	}
	// end add
	
	public Set<MacFilter> getMacFilters() {
		return macFilters;
	}

	public void setMacFilters(Set<MacFilter> macFilters) {
		this.macFilters = macFilters;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getDtimSetting() {
		return dtimSetting;
	}

	public void setDtimSetting(int dtimSetting) {
		this.dtimSetting = dtimSetting;
	}

	public int getFragThreshold() {
		return fragThreshold;
	}

	public void setFragThreshold(int fragThreshold) {
		this.fragThreshold = fragThreshold;
	}

	public int getAuthentication() {
		return authentication;
	}

	@Transient
	public String getAuthenticationString() {
		switch (authentication) {
		case KEY_AUT_OPEN:
			return "OPEN";
		case KEY_AUT_EAP:
			return "EAP(802.1X)";
		case KEY_AUT_SHARED:
			return "SHARED";
		default:
			return "Unknow";
		}
	}

	public void setAuthentication(int authentication) {
		this.authentication = authentication;
	}

	public int getEncryption() {
		return encryption;
	}

	@Transient
	public String getEncryptionString() {
		switch (encryption) {
		case KEY_ENC_NONE:
			return "NONE";
		case KEY_ENC_CCMP:
			return "CCMP (AES)";
		case KEY_ENC_TKIP:
			return "TKIP";
		case KEY_ENC_WEP104:
			return "WEP 104";
		case KEY_ENC_WEP40:
			return "WEP 40";
		case KEY_ENC_AUTO_TKIP_OR_CCMP:
			return "Auto-TKIP or CCMP (AES)";
		default:
			return "Unknow";
		}
	}

	public void setEncryption(int encryption) {
		this.encryption = encryption;
	}

	public String getSsidName() {
		return ssidName;
	}

	public void setSsidName(String ssidName) {
		this.ssidName = ssidName;
	}

	public int getRtsThreshold() {
		return rtsThreshold;
	}

	public void setRtsThreshold(int rtsThreshold) {
		this.rtsThreshold = rtsThreshold;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public String getLabel() {
		return ssidName;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public int getAllUserProfileSize(){
		int size=0;
		if (userProfileDefault!= null) {
			size++;
		}
		if (userProfileSelfReg!= null){
			size++;
		}
		if (userProfileGuest!= null){
		    size++;
		}
		return size + radiusUserProfile.size();	
	}
	
	public boolean getBlnDisplayRadius(){
		if (isBlnDisplayIDM()) {
			return false;
		}
		if (isCwpSelectEnabled() && cwp!=null &&
				(cwp.getRegistrationType()==Cwp.REGISTRATION_TYPE_AUTHENTICATED || 
						cwp.getRegistrationType()==Cwp.REGISTRATION_TYPE_EXTERNAL ||
						cwp.getRegistrationType()==Cwp.REGISTRATION_TYPE_BOTH)){
			return true;
		} else {
			if (getMacAuthEnabled() || 
				getEnabledUseGuestManager() ||
				getMgmtKey() == KEY_MGMT_WPA2_EAP_802_1_X||
				getMgmtKey() == KEY_MGMT_WPA_EAP_802_1_X||
				getMgmtKey() == KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X||
				getMgmtKey() == KEY_MGMT_DYNAMIC_WEP) {
				return true;
			}
		}
		return false;
	}

    public boolean isBlnDisplayIDM() {
        if (this.isEnabledIDM()) {
            if (this.getAccessMode() == ACCESS_MODE_8021X
                    || this.getAccessMode() == ACCESS_MODE_PSK) {
                return true;
            } else if (this.getAccessMode() == ACCESS_MODE_WPA) {
                if (isCwpSelectEnabled()) {
                    if (null == cwp
                            || (cwp != null && (cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                                    || cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH))) {
                        return true;
                    }
                }
            } else if (this.getAccessMode() == ACCESS_MODE_OPEN) {
                if (isCwpSelectEnabled()) {
                    if (null == cwp
                            || (cwp != null
                                    && (cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED)
                                    || cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA
                                    || cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH)) {
                        return true;
                    }
                }
            } else if (this.getAccessMode() == ACCESS_MODE_WEP) {
                if (isCwpSelectEnabled()) {
                    if (null == cwp
                            || (cwp != null && (cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                                    || cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA
                                    || cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
	
	public String getNewOptionUserProfileName() {
		return newOptionUserProfileName;
	}

	public void setNewOptionUserProfileName(String newOptionUserProfileName) {
		this.newOptionUserProfileName = newOptionUserProfileName;
	}

	public short getNewOptionAttributeValue() {
		return newOptionAttributeValue;
	}

	public void setNewOptionAttributeValue(short newOptionAttributeValue) {
		this.newOptionAttributeValue = newOptionAttributeValue;
	}

	public Long getNewOptionVlanId() {
		return newOptionVlanId;
	}

	public void setNewOptionVlanId(Long newOptionVlanId) {
		this.newOptionVlanId = newOptionVlanId;
	}

//	public int getNewOptionGuestAccess() {
//		return newOptionGuestAccess;
//	}
//
//	public void setNewOptionGuestAccess(int newOptionGuestAccess) {
//		this.newOptionGuestAccess = newOptionGuestAccess;
//	}

	public boolean getBroadcase() {
		return broadcase;
	}

	public boolean isBroadcase() {
		return broadcase;
	}

	public void setBroadcase(boolean broadcase) {
		this.broadcase = broadcase;
	}

	public boolean getHide() {
		return hide;
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public boolean getMacAuthEnabled() {
		return macAuthEnabled;
	}

	public boolean isMacAuthEnabled() {
		return macAuthEnabled;
	}

	public void setMacAuthEnabled(boolean macAuthEnabled) {
		this.macAuthEnabled = macAuthEnabled;
	}

	public boolean getPreauthenticationEnabled() {
		return preauthenticationEnabled;
	}

	public boolean isPreauthenticationEnabled() {
		return preauthenticationEnabled;
	}

	public void setPreauthenticationEnabled(boolean preauthenticationEnabled) {
		this.preauthenticationEnabled = preauthenticationEnabled;
	}

	public SsidSecurity getSsidSecurity() {
		return ssidSecurity;
	}

	public void setSsidSecurity(SsidSecurity ssidSecurity) {
		this.ssidSecurity = ssidSecurity;
	}

	public DosPrevention getIpDos() {
		return ipDos;
	}

	public void setIpDos(DosPrevention ipDos) {
		this.ipDos = ipDos;
	}

	public DosPrevention getSsidDos() {
		return ssidDos;
	}

	public void setSsidDos(DosPrevention ssidDos) {
		this.ssidDos = ssidDos;
	}

	public DosPrevention getStationDos() {
		return stationDos;
	}

	public void setStationDos(DosPrevention stationDos) {
		this.stationDos = stationDos;
	}

	public int getMgmtKey() {
		return mgmtKey;
	}

	@Transient
	public String getMgmtKeyString() {
		switch (mgmtKey) {
		case KEY_MGMT_OPEN:
		case KEY_MGMT_WPA2_EAP_802_1_X:
		case KEY_MGMT_WPA2_PSK:
		case KEY_MGMT_WPA_EAP_802_1_X:
		case KEY_MGMT_WPA_PSK:
		case KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X:
		case KEY_MGMT_AUTO_WPA_OR_WPA2_PSK:
		case KEY_MGMT_WEP_PSK:
		case KEY_MGMT_DYNAMIC_WEP:
			return MgrUtil.getEnumString("enum.keyMgmt." + mgmtKey);
		default:
			return "Unknow";
		}
	}

	public void setMgmtKey(int mgmtKey) {
		this.mgmtKey = mgmtKey;
	}

	public int getDefaultAction() {
		return defaultAction;
	}

	public void setDefaultAction(int defaultAction) {
		this.defaultAction = defaultAction;
	}

	public boolean getDefaultFlag() {
		return defaultFlag;
	}
	
	public boolean getDefaultPrepareFlg(){
		return defaultFlag || ssidName.equalsIgnoreCase(BeParaModule.SSID_PROFILE_TEMPLATE_SYMBOL_SCANNER)
				|| ssidName.equalsIgnoreCase(BeParaModule.SSID_PROFILE_TEMPLATE_LEGACY_CLIENTS)
				|| ssidName.equalsIgnoreCase(BeParaModule.SSID_PROFILE_TEMPLATE_HIGH_CAPACITY)
				|| ssidName.equalsIgnoreCase(BeParaModule.SSID_PROFILE_TEMPLATE_BLACK_BERRY)
				|| ssidName.equalsIgnoreCase(BeParaModule.SSID_PROFILE_TEMPLATE_SPECTRA_LINK);
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public boolean getEnabledwmm() {
		return enabledwmm;
	}

	public void setEnabledwmm(boolean enabledwmm) {
		this.enabledwmm = enabledwmm;
	}

	public boolean getEnabledUnscheduled() {
		return enabledUnscheduled;
	}

	public void setEnabledUnscheduled(boolean enabledUnscheduled) {
		this.enabledUnscheduled = enabledUnscheduled;
	}

	public TX11aOr11gRateSetting getTX11aOr11gRateSetting(GRateType type) {
		return gRateSets.get(type.name());
	}

	public Map<String, TX11aOr11gRateSetting> getGRateSets() {
		return gRateSets;
	}

	public void setGRateSets(Map<String, TX11aOr11gRateSetting> rateSets) {
		gRateSets = rateSets;
	}

	public TX11aOr11gRateSetting getTX11aOr11gRateSetting(ARateType type) {
		return aRateSets.get(type.name());
	}
	
	public TX11aOr11gRateSetting getTX11aOr11gRateSetting(NRateType type) {
		return nRateSets.get(type.name());
	}

	public Map<String, TX11aOr11gRateSetting> getARateSets() {
		return aRateSets;
	}

	public void setARateSets(Map<String, TX11aOr11gRateSetting> rateSets) {
		aRateSets = rateSets;
	}

	public boolean isEnableGRateSet() {
		return enableGRateSet;
	}

	public void setEnableGRateSet(boolean enableGRateSet) {
		this.enableGRateSet = enableGRateSet;
	}

	public boolean isEnableARateSet() {
		return enableARateSet;
	}

	public void setEnableARateSet(boolean enableARateSet) {
		this.enableARateSet = enableARateSet;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public int getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}

	public int getAgeOut() {
		return ageOut;
	}

	public void setAgeOut(int ageOut) {
		this.ageOut = ageOut;
	}

	public int getMaxClient() {
		return maxClient;
	}

	public void setMaxClient(int maxClient) {
		this.maxClient = maxClient;
	}

	public int getLocalCacheTimeout() {
		return localCacheTimeout;
	}

	public void setLocalCacheTimeout(int localCacheTimeout) {
		this.localCacheTimeout = localCacheTimeout;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid.trim();
	}

	public ServiceFilter getServiceFilter() {
		return serviceFilter;
	}

	public void setServiceFilter(ServiceFilter serviceFilter) {
		this.serviceFilter = serviceFilter;
	}

	public AirScreenRuleGroup getAsRuleGroup() {
		return asRuleGroup;
	}

	public void setAsRuleGroup(AirScreenRuleGroup asRuleGroup) {
		this.asRuleGroup = asRuleGroup;
	}

	public Cwp getCwp() {
		return cwp;
	}
	
	public String getCwpString(){
		if (cwp!=null || userPolicy!=null) {
			return "true";
		}
		return "false";
	}

	public void setCwp(Cwp cwp) {
		this.cwp = cwp;
	}

	public int getRadioMode() {
		return radioMode;
	}

	public void setRadioMode(int radioMode) {
		this.radioMode = radioMode;
	}
	
	@Transient
	public String getRadioModeString() {
		switch (radioMode) {
		case RADIOMODE_A:
		case RADIOMODE_BG:
		case RADIOMODE_BOTH:
			return MgrUtil.getEnumString("enum.wlanPolicy.radioProfileMode." + radioMode);
		default:
			return "INVALID";
		}
	}
	
	@Transient
	public boolean getUserProfileTypeDefault() {
		return getCwp() == null && !getMacAuthEnabled() &&
				getMgmtKey() != KEY_MGMT_WPA2_EAP_802_1_X &&
				getMgmtKey() != KEY_MGMT_WPA_EAP_802_1_X &&
				getMgmtKey() != KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X &&
				getMgmtKey() != KEY_MGMT_DYNAMIC_WEP;
	}

	public UserProfile getUserProfileSelfReg() {
		return userProfileSelfReg;
	}

	public void setUserProfileSelfReg(UserProfile userProfileSelfReg) {
		this.userProfileSelfReg = userProfileSelfReg;
	}

	public UserProfile getUserProfileDefault() {
		return userProfileDefault;
	}

	public void setUserProfileDefault(UserProfile userProfileDefault) {
		this.userProfileDefault = userProfileDefault;
	}

	public Set<UserProfile> getRadiusUserProfile() {
		return radiusUserProfile;
	}

	public void setRadiusUserProfile(Set<UserProfile> radiusUserProfile) {
		this.radiusUserProfile = radiusUserProfile;
	}

	public short getDenyAction() {
		return denyAction;
	}

	public void setDenyAction(short denyAction) {
		this.denyAction = denyAction;
	}

	public long getActionTime() {
		return actionTime;
	}

	public void setActionTime(long actionTime) {
		this.actionTime = actionTime;
	}

	public boolean getChkUserOnly() {
		return chkUserOnly;
	}

	public void setChkUserOnly(boolean chkUserOnly) {
		this.chkUserOnly = chkUserOnly;
	}

	public boolean getChkDeauthenticate() {
		return chkDeauthenticate;
	}

	public void setChkDeauthenticate(boolean chkDeauthenticate) {
		this.chkDeauthenticate = chkDeauthenticate;
	}

	public RadiusAssignment getRadiusAssignment() {
		return radiusAssignment;
	}

	public void setRadiusAssignment(RadiusAssignment radiusAssignment) {
		this.radiusAssignment = radiusAssignment;
	}

//	public PersonalizedPskGroup getPskGroup() {
//		return pskGroup;
//	}
//
//	public void setPskGroup(PersonalizedPskGroup pskGroup) {
//		this.pskGroup = pskGroup;
//	}
//
//	public Set<PersonalizedPsk> getPrivatePsks() {
//		return privatePsks;
//	}
//
//	public void setPrivatePsks(Set<PersonalizedPsk> privatePsks) {
//		this.privatePsks = privatePsks;
//	}
//
//	public boolean getEnabledPrivatePsk() {
//		return enabledPrivatePsk;
//	}
//
//	public void setEnabledPrivatePsk(boolean enabledPrivatePsk) {
//		this.enabledPrivatePsk = enabledPrivatePsk;
//	}

	public int getAccessMode() {
		return accessMode;
	}
	
	public String getAccessModeString(){
		switch (accessMode){
			case ACCESS_MODE_WPA:
				return "WPA/WPA2 PSK (Personal)";
			case ACCESS_MODE_PSK:
				return "Private PSK";
			case ACCESS_MODE_8021X:
				return "WPA/WPA2 802.1X (Enterprise)";
			case ACCESS_MODE_WEP:
				return "WEP";
			case ACCESS_MODE_OPEN:
				return "Open";
			default:
				return "Unknown";
		}
	}

	public void setAccessMode(int accessMode) {
		this.accessMode = accessMode;
	}

	public Set<LocalUserGroup> getLocalUserGroups() {
		return localUserGroups;
	}

	public void setLocalUserGroups(Set<LocalUserGroup> localUserGroups) {
		this.localUserGroups = localUserGroups;
	}

	public Set<LocalUserGroup> getRadiusUserGroups() {
		return this.radiusUserGroups;
	}

	public void setRadiusUserGroups(Set<LocalUserGroup> radiusUserGroups) {
		this.radiusUserGroups = radiusUserGroups;
	}

	public boolean getEnabledDefaultSetting() {
		return enabledDefaultSetting;
	}

	public void setEnabledDefaultSetting(boolean enabledDefaultSetting) {
		this.enabledDefaultSetting = enabledDefaultSetting;
	}

	public boolean getEnabledUseGuestManager() {
		return enabledUseGuestManager;
	}

	public void setEnabledUseGuestManager(boolean enabledUseGuestManager) {
		this.enabledUseGuestManager = enabledUseGuestManager;
	}

	public int getPersonPskRadiusAuth() {
		return personPskRadiusAuth;
	}

	public void setPersonPskRadiusAuth(int personPskRadiusAuth) {
		this.personPskRadiusAuth = personPskRadiusAuth;
	}

	public boolean getEnabledLegacy() {
		return enabledLegacy;
	}

	public void setEnabledLegacy(boolean enabledLegacy) {
		this.enabledLegacy = enabledLegacy;
	}

	public Cwp getUserPolicy() {
		return userPolicy;
	}

	public void setUserPolicy(Cwp userPolicy) {
		this.userPolicy = userPolicy;
	}

	public int getEapTimeOut() {
		return eapTimeOut;
	}

	public void setEapTimeOut(int eapTimeOut) {
		this.eapTimeOut = eapTimeOut;
	}

	public int getEapRetries() {
		return eapRetries;
	}

	public void setEapRetries(int eapRetries) {
		this.eapRetries = eapRetries;
	}

	public String getNewSelfUserProfileName() {
		return newSelfUserProfileName;
	}

	public void setNewSelfUserProfileName(String newSelfUserProfileName) {
		this.newSelfUserProfileName = newSelfUserProfileName;
	}

	public short getNewSelfAttributeValue() {
		return newSelfAttributeValue;
	}

	public void setNewSelfAttributeValue(short newSelfAttributeValue) {
		this.newSelfAttributeValue = newSelfAttributeValue;
	}

	public Long getNewSelfVlanId() {
		return newSelfVlanId;
	}

	public void setNewSelfVlanId(Long newSelfVlanId) {
		this.newSelfVlanId = newSelfVlanId;
	}

//	public int getNewSelfGuestAccess() {
//		return newSelfGuestAccess;
//	}
//
//	public void setNewSelfGuestAccess(int newSelfGuestAccess) {
//		this.newSelfGuestAccess = newSelfGuestAccess;
//	}

	public String getNewDefaultUserProfileName() {
		return newDefaultUserProfileName;
	}

	public void setNewDefaultUserProfileName(String newDefaultUserProfileName) {
		this.newDefaultUserProfileName = newDefaultUserProfileName;
	}

	public short getNewDefaultAttributeValue() {
		return newDefaultAttributeValue;
	}

	public void setNewDefaultAttributeValue(short newDefaultAttributeValue) {
		this.newDefaultAttributeValue = newDefaultAttributeValue;
	}

	public Long getNewDefaultVlanId() {
		return newDefaultVlanId;
	}

	public void setNewDefaultVlanId(Long newDefaultVlanId) {
		this.newDefaultVlanId = newDefaultVlanId;
	}

//	public int getNewDefaultGuestAccess() {
//		return newDefaultGuestAccess;
//	}
//
//	public void setNewDefaultGuestAccess(int newDefaultGuestAccess) {
//		this.newDefaultGuestAccess = newDefaultGuestAccess;
//	}
    @Override
    public SsidProfile clone() {
       try {
           return (SsidProfile) super.clone();
       } catch (CloneNotSupportedException e) {
           return null;
       }
    }

	public int getUserCategory() {
		return userCategory;
	}
	
	public String getUserCategoryString(){
//		int value = -1;
//		if (userProfileDefault!=null) {
//			value =  userProfileDefault.getUserCategory();
//		} else if (userProfileSelfReg!=null) {
//			value =  userProfileSelfReg.getUserCategory();
//		}
		switch (userCategory) {
			case USER_CATEGORY_EMPLOOYEE: 
				return "Employee";
			case USER_CATEGORY_GUEST: 
				return "Guest";
			case USER_CATEGORY_VOICE: 
				return "Voice Only";
			case USER_CATEGORY_CUSTOM: 
				return "Custom";
			default: return "";
		}
	}
	
	public void setUserCategory(int userCategory) {
		this.userCategory = userCategory;
	}

	public boolean getUserInternetAccess() {
		return userInternetAccess;
	}

	public void setUserInternetAccess(boolean userInternetAccess) {
		this.userInternetAccess = userInternetAccess;
	}

	public int getUserNumberPsk() {
		return userNumberPsk;
	}

	public void setUserNumberPsk(int userNumberPsk) {
		this.userNumberPsk = userNumberPsk;
	}

	public int getUserRatelimit() {
		return userRatelimit;
	}

	public void setUserRatelimit(int userRatelimit) {
		this.userRatelimit = userRatelimit;
	}

	public int getUserPskMethod() {
		return userPskMethod;
	}

	public void setUserPskMethod(int userPskMethod) {
		this.userPskMethod = userPskMethod;
	}

	public Vlan getUserVlan() {
		return userVlan;
	}

	public void setUserVlan(Vlan userVlan) {
		this.userVlan = userVlan;
	}

	public boolean getShowExpressUserAccess() {
		return showExpressUserAccess;
	}

	public void setShowExpressUserAccess(boolean showExpressUserAccess) {
		this.showExpressUserAccess = showExpressUserAccess;
	}

	public String getSelectNewHiveApRadiusPrimaryIp() {
		return selectNewHiveApRadiusPrimaryIp;
	}

	public void setSelectNewHiveApRadiusPrimaryIp(String selectNewHiveApRadiusPrimaryIp) {
		this.selectNewHiveApRadiusPrimaryIp = selectNewHiveApRadiusPrimaryIp;
	}

	public String getSelectNewHiveApRadiusSecondaryIp() {
		return selectNewHiveApRadiusSecondaryIp;
	}

	public void setSelectNewHiveApRadiusSecondaryIp(String selectNewHiveApRadiusSecondaryIp) {
		this.selectNewHiveApRadiusSecondaryIp = selectNewHiveApRadiusSecondaryIp;
	}

	public boolean getBlnUserManager() {
		return blnUserManager;
	}

	public void setBlnUserManager(boolean blnUserManager) {
		this.blnUserManager = blnUserManager;
	}

	public boolean getNewSelfBlnUserManager() {
		return newSelfBlnUserManager;
	}

	public void setNewSelfBlnUserManager(boolean newSelfBlnUserManager) {
		this.newSelfBlnUserManager = newSelfBlnUserManager;
	}

	public boolean getNewDefaultBlnUserManager() {
		return newDefaultBlnUserManager;
	}

	public void setNewDefaultBlnUserManager(boolean newDefaultBlnUserManager) {
		this.newDefaultBlnUserManager = newDefaultBlnUserManager;
	}

	public boolean getNewOptionBlnUserManager() {
		return newOptionBlnUserManager;
	}

	public void setNewOptionBlnUserManager(boolean newOptionBlnUserManager) {
		this.newOptionBlnUserManager = newOptionBlnUserManager;
	}

	public boolean getFallBackToEcwp() {
		return fallBackToEcwp;
	}

	public void setFallBackToEcwp(boolean fallBackToEcwp) {
		this.fallBackToEcwp = fallBackToEcwp;
	}

	public int getClientAgeOut() {
		return clientAgeOut;
	}

	public void setClientAgeOut(int clientAgeOut) {
		this.clientAgeOut = clientAgeOut;
	}

	public Map<String, TX11aOr11gRateSetting> getNRateSets() {
		return nRateSets;
	}

	public void setNRateSets(Map<String, TX11aOr11gRateSetting> rateSets) {
		nRateSets = rateSets;
	}

	public boolean getEnableNRateSet() {
		return enableNRateSet;
	}

	public void setEnableNRateSet(boolean enableNRateSet) {
		this.enableNRateSet = enableNRateSet;
	}

	@Transient
	public String getValue() {
		return ssidName;
	}

	/**
	 * @return the authSequence
	 */
	public int getAuthSequence() {
		return authSequence;
	}

	/**
	 * @param authSequence the authSequence to set
	 */
	public void setAuthSequence(int authSequence) {
		this.authSequence = authSequence;
	}

	public boolean isEnableOsDection()
	{
		return enableOsDection;
	}

	public void setEnableOsDection(boolean enableOsDection)
	{
		this.enableOsDection = enableOsDection;
	}

	public boolean isEnablePpskSelfReg(){
		return this.enablePpskSelfReg;
	}
	
	public void setEnablePpskSelfReg(boolean enablePpskSelfReg){
		this.enablePpskSelfReg = enablePpskSelfReg;
	}
	
	public String getPpskServerIp(){
		return this.ppskServerIp;
	}
	
	public void setPpskServerIp(String ppskServerIp){
		this.ppskServerIp = ppskServerIp;
	}
	
	public Cwp getPpskECwp(){
		return this.ppskECwp;
	}
	
	public void setPpskECwp(Cwp ppskECwp){
		this.ppskECwp = ppskECwp;
	}
	
	public String getPpskOpenSsid(){
		return this.ppskOpenSsid;
	}
	public String getWpaOpenSsidSubstr(){
		if (wpaOpenSsid == null) {
			return "";
		}
		if (wpaOpenSsid.length() > BaseAction.DISPLAY_LENGTH_IN_GUI_OK) {
			return wpaOpenSsid.substring(0, BaseAction.DISPLAY_LENGTH_IN_GUI)
					+ "...";
		}
		return wpaOpenSsid;
	
	}
	public String getPpskOpenSsidSubstr() {
		if (ppskOpenSsid==null) {
			return "";
		}
		if (ppskOpenSsid.length()> BaseAction.DISPLAY_LENGTH_IN_GUI_OK) {
			return ppskOpenSsid.substring(0, BaseAction.DISPLAY_LENGTH_IN_GUI) + "...";
		}
		
		return ppskOpenSsid;
	}
	
	public void setPpskOpenSsid(String ppskOpenSsid){
		this.ppskOpenSsid = ppskOpenSsid;
	}
	public String getOnboardSsid() {
		return onboardSsid;
	}
	public void setOnboardSsid(String onboardSsid) {
		this.onboardSsid = onboardSsid;
	}

	// transient value for AD integration **Start** //
	public String getStaticHiveAPIpAddress() {
		return staticHiveAPIpAddress;
	}

	public void setStaticHiveAPIpAddress(String staticHiveAPIpAddress) {
		this.staticHiveAPIpAddress = staticHiveAPIpAddress;
	}

	public String getStaticHiveAPNetmask() {
		return staticHiveAPNetmask;
	}

	public void setStaticHiveAPNetmask(String staticHiveAPNetmask) {
		this.staticHiveAPNetmask = staticHiveAPNetmask;
	}

	public String getStaticHiveAPGateway() {
		return staticHiveAPGateway;
	}

	public void setStaticHiveAPGateway(String staticHiveAPGateway) {
		this.staticHiveAPGateway = staticHiveAPGateway;
	}

	public String getDnsServer() {
		return dnsServer;
	}

	public void setDnsServer(String dnsServer) {
		this.dnsServer = dnsServer;
	}

	public boolean isPushedConfig() {
		return isPushedConfig;
	}

	public void setPushedConfig(boolean isPushedConfig) {
		this.isPushedConfig = isPushedConfig;
	}

	public boolean isEnableADIntegration() {
		return enableADIntegration;
	}

	public void setEnableADIntegration(boolean enableADIntegration) {
		this.enableADIntegration = enableADIntegration;
	}

	public String getAdDomainFullName() {
		return adDomainFullName;
	}

	public void setAdDomainFullName(String adDomainFullName) {
		this.adDomainFullName = adDomainFullName;
	}

	public String getAdDomainAdmin() {
		return adDomainAdmin;
	}

	public void setAdDomainAdmin(String adDomainAdmin) {
		this.adDomainAdmin = adDomainAdmin;
	}

	public String getAdDomainAdminPasswd() {
		return adDomainAdminPasswd;
	}

	public void setAdDomainAdminPasswd(String adDomainAdminPasswd) {
		this.adDomainAdminPasswd = adDomainAdminPasswd;
	}

	public String getAdDomainTestUser() {
		return adDomainTestUser;
	}

	public void setAdDomainTestUser(String adDomainTestUser) {
		this.adDomainTestUser = adDomainTestUser;
	}

	public String getAdDomainTestUserPasswd() {
		return adDomainTestUserPasswd;
	}

	public void setAdDomainTestUserPasswd(String adDomainTestUserPasswd) {
		this.adDomainTestUserPasswd = adDomainTestUserPasswd;
	}

	public String getAdDomainName() {
		return adDomainName;
	}

	public void setAdDomainName(String adDomainName) {
		this.adDomainName = adDomainName;
	}

	public String getAdServerIpAddress() {
		return adServerIpAddress;
	}

	public void setAdServerIpAddress(String adServerIpAddress) {
		this.adServerIpAddress = adServerIpAddress;
	}

	public String getBaseDN() {
		return baseDN;
	}

	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}

	public RadiusAssignment getRadiusAssignmentPpsk() {
		return radiusAssignmentPpsk;
	}

	public void setRadiusAssignmentPpsk(RadiusAssignment radiusAssignmentPpsk) {
		this.radiusAssignmentPpsk = radiusAssignmentPpsk;
	}

	public boolean isCwpSelectEnabled() {
		return cwpSelectEnabled;
	}

	public void setCwpSelectEnabled(boolean cwpSelectEnabled) {
		this.cwpSelectEnabled = cwpSelectEnabled;
	}
	
	public static final short CONVTOUNICAST_AUTO = 1;
	public static final short CONVTOUNICAST_ALWAYS = 2;
	public static final short CONVTOUNICAST_DISABLE = 3;
	public static final int CUTHRESHOLD_DEFAULT_VALUE = 60 ;
	public static final int MEMBERTHRESHOLD_DEFAULT_VALUE = 10 ;

	private short convtounicast = CONVTOUNICAST_DISABLE ;
	@Range(min = 1, max = 100)
	private int cuthreshold = CUTHRESHOLD_DEFAULT_VALUE;
	@Range(min = 1, max = 30)
	private int memberthreshold = MEMBERTHRESHOLD_DEFAULT_VALUE;
	
	public short getConvtounicast() {
		return convtounicast;
	}
	public void setConvtounicast(short convtounicast) {
		this.convtounicast = convtounicast;
	}
	public int getMemberthreshold() {
		return memberthreshold;
	}
	public void setMemberthreshold(int memberthreshold) {
		this.memberthreshold = memberthreshold;
	}
	public int getCuthreshold() {
		return cuthreshold;
	}
	public void setCuthreshold(int cuthreshold) {
		this.cuthreshold = cuthreshold;
	}
	// transient value for AD integration **End** //
	
	@Column(name = "PPSK_SERVER_ID")
	private Long ppskServerId;

	@Transient
	private HiveAp ppskServer;

	public HiveAp getPpskServer() {
		if (ppskServer != null) {
			return ppskServer;
		}
		if (ppskServerId != null && ppskServerId > 0) {
			ppskServer = QueryUtil.findBoById(HiveAp.class, ppskServerId);
			return ppskServer;
		}
		return null;
	}

	public void setPpskServer(HiveAp ppskServer) {
		this.ppskServer = ppskServer;
		if (ppskServer != null) {
			this.ppskServerId = ppskServer.getId();
		} else {
			this.ppskServerId = null;
		}
	}
	
	/**
	 * whether BR as private psk server is selected for this ssid profile
	 */
	private boolean blnBrAsPpskServer;

	public boolean isBlnBrAsPpskServer() {
		return blnBrAsPpskServer;
	}

	public void setBlnBrAsPpskServer(boolean blnBrAsPpskServer) {
		this.blnBrAsPpskServer = blnBrAsPpskServer;
	}
	
	@Transient
	public void setChkUserOnlyDefaults() {
		this.chkUserOnly = false;
		this.chkDeauthenticate = false;
		this.denyAction = DENY_ACTION_DISCONNECT;
		this.actionTime = DEFAULT_ACTION_TIME;
	}
	
	
	
	private boolean enableAssignUserProfile = false;
	
	private int assignUserProfileAttributeId;
	
	private int assignUserProfileVenderId;
	
	public static final short USERPROFILE_ATTRIBUTE_SPECIFIED = 1;
	
	public static final short USERPROFILE_ATTRIBUTE_CUSTOMER = 2;
	
	private short userProfileAttributeType = USERPROFILE_ATTRIBUTE_SPECIFIED;
	


	public boolean isEnableAssignUserProfile() {
		return enableAssignUserProfile;
	}

	public void setEnableAssignUserProfile(boolean enableAssignUserProfile) {
		this.enableAssignUserProfile = enableAssignUserProfile;
	}

	public int getAssignUserProfileAttributeId() {
		return assignUserProfileAttributeId;
	}

	public void setAssignUserProfileAttributeId(int assignUserProfileAttributeId) {
		this.assignUserProfileAttributeId = assignUserProfileAttributeId;
	}

	public int getAssignUserProfileVenderId() {
		return assignUserProfileVenderId;
	}

	public void setAssignUserProfileVenderId(int assignUserProfileVenderId) {
		this.assignUserProfileVenderId = assignUserProfileVenderId;
	}

	
	
	public short getUserProfileAttributeType() {
		return userProfileAttributeType;
	}

	public void setUserProfileAttributeType(short userProfileAttributeType) {
		this.userProfileAttributeType = userProfileAttributeType;
	}

    public boolean isEnabledIDM() {
        return enabledIDM;
    }

    public void setEnabledIDM(boolean enabledIDM) {
        this.enabledIDM = enabledIDM;
    }

	public boolean isEnabled80211k() {
		return enabled80211k;
	}

	public void setEnabled80211k(boolean enabled80211k) {
		this.enabled80211k = enabled80211k;
	}

	public boolean isEnabledAcBesteffort() {
		return enabledAcBesteffort;
	}

	public void setEnabledAcBesteffort(boolean enabledAcBesteffort) {
		this.enabledAcBesteffort = enabledAcBesteffort;
	}

	public boolean isEnabledAcBackground() {
		return enabledAcBackground;
	}

	public void setEnabledAcBackground(boolean enabledAcBackground) {
		this.enabledAcBackground = enabledAcBackground;
	}

	public boolean isEnabledAcVideo() {
		return enabledAcVideo;
	}

	public void setEnabledAcVideo(boolean enabledAcVideo) {
		this.enabledAcVideo = enabledAcVideo;
	}

	public boolean isEnabledAcVoice() {
		return enabledAcVoice;
	}

	public void setEnabledAcVoice(boolean enabledAcVoice) {
		this.enabledAcVoice = enabledAcVoice;
	}

	public boolean isEnabled80211v() {
		return enabled80211v;
	}

	public void setEnabled80211v(boolean enabled80211v) {
		this.enabled80211v = enabled80211v;
	}

	public boolean isEnabledVoiceEnterprise() {
		return enabledVoiceEnterprise;
	}

	public void setEnabledVoiceEnterprise(boolean enabledVoiceEnterprise) {
		this.enabledVoiceEnterprise = enabledVoiceEnterprise;
	}
	
	public boolean isEnabled80211r() {
		return enabled80211r;
	}

	public void setEnabled80211r(boolean enabled80211r) {
		this.enabled80211r = enabled80211r;
	}

	public int getWifiPriority() {
		return wifiPriority;
	}

	public void setWifiPriority(int wifiPriority) {
		this.wifiPriority = wifiPriority;
	}

	public short getLdapSaslWrapping() {
		return ldapSaslWrapping;
	}

	public void setLdapSaslWrapping(short ldapSaslWrapping) {
		this.ldapSaslWrapping = ldapSaslWrapping;
	}

	public boolean isCloneFromPPSKServer() {
		return cloneFromPPSKServer;
	}

	public void setCloneFromPPSKServer(boolean cloneFromPPSKServer) {
		this.cloneFromPPSKServer = cloneFromPPSKServer;
	}

	public List<Tx11acRateSettings> getAcRateSets() {
		return acRateSets;
	}

	public void setAcRateSets(List<Tx11acRateSettings> acRateSets) {
		this.acRateSets = acRateSets;
	}

	public boolean isEnableACRateSet() {
		return enableACRateSet;
	}

	public void setEnableACRateSet(boolean enableACRateSet) {
		this.enableACRateSet = enableACRateSet;
	}
	
	@Transient
	public boolean isRenameCWPType() {
        if (this.enabledIDM
                && (this.accessMode == ACCESS_MODE_WPA
                        || this.accessMode == ACCESS_MODE_OPEN 
                        || (this.accessMode == ACCESS_MODE_WEP && mgmtKey == KEY_MGMT_WEP_PSK))) {
            return true;
        } else {
            return false;
        }
	}
	
    @Transient
    public String getSuffixIDMType() {
        String typeName = "";
        if(this.enabledIDM) {
            if (this.accessMode == ACCESS_MODE_OPEN
                    && this.cwpSelectEnabled
                    && null != this.cwp
                    && this.cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA) {
                typeName = "(No Authentication)";
            } else if(this.accessMode == ACCESS_MODE_PSK) {
                typeName = "(Private PSK)";
            } else if(null != this.cwp || this.accessMode == ACCESS_MODE_8021X){
                typeName = "(User Name/Password)";
            }
        }
        return typeName;
    }
    
    @Transient
    public String getManageLink4CM(){
    	return URLUtils.getViewCMURL();
    }
    
    @Transient
    public String getManageLink4IDM(){
    	IDMConfig idmConfig = new HmCloudAuthCertMgmtImpl().getRadSecConfig(getOwner().getId());
    	if(idmConfig == null){
    		return "#";
    	} 

    	return idmConfig.getIdmWebServer()+"/home";
    }
    
    public UserProfile getUserProfileGuest() {
        return userProfileGuest;
    }

    public void setUserProfileGuest(UserProfile userProfileGuest) {
        this.userProfileGuest = userProfileGuest;
    }

    public boolean isEnabledSocialLogin() {
        return enabledSocialLogin;
    }

    public void setEnabledSocialLogin(boolean enabledSocialLogin) {
        this.enabledSocialLogin = enabledSocialLogin;
    }
    
    @Transient
    private SsidProfile parentPpskSsid;
    
    public SsidProfile getParentPpskSsid(){
    	return this.parentPpskSsid;
    }
    
    public void setParentPpskSsid(SsidProfile parentPpskSsid){
    	this.parentPpskSsid = parentPpskSsid;
    }
}
