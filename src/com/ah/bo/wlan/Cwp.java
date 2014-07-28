package com.ah.bo.wlan;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.Vlan;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "CWP")
@org.hibernate.annotations.Table(appliesTo = "CWP", indexes = {
		@Index(name = "CWP_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Cwp implements HmBo {

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
	private String cwpName;

	private boolean useDefaultNetwork = true;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String ipForAMode;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String maskForAMode;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String ipForBGMode;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String maskForBGMode;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String ipForEth0;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String maskForEth0;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String ipForEth1;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String maskForEth1;

	@Range(min = 1, max = 120960)
	private int registrationPeriod = 720;

	@Range(min = 5, max = 36000)
	private int leaseTime = 10;

	private int authMethod = AUTH_METHOD_CHAP;

	public static final int AUTH_METHOD_PAP = 1;

	public static final int AUTH_METHOD_CHAP = 2;
	
	public static final int AUTH_METHOD_MSCHAPV2 = 3;

	public static EnumItem[] ENUM_AUTH_METHOD = MgrUtil
			.enumItems("enum.authMethod.", new int[] { AUTH_METHOD_PAP,
					AUTH_METHOD_CHAP,  AUTH_METHOD_MSCHAPV2});

	private int dhcpMode;

	public static final int MODE_BROADCAST = 0;
	public static final int MODE_UNICAST = 1;
	public static final int MODE_KEEPSILENT = 2;

	public static EnumItem[] ENUM_DHCP_MODE = MgrUtil.enumItems(
			"enum.dhcpMode.", new int[] { MODE_BROADCAST, MODE_UNICAST, MODE_KEEPSILENT });

	@Range(min = 0, max = 8)
	private int numberField = 2;

	@Range(min = 0, max = 8)
	private int requestField = 4;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String directoryName;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String webPageName;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String resultPageName;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String failurePageName;

	private boolean enabledHttps;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CERTIFICATE_ID")
	private CwpCertificate certificate;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String comment;

	private boolean enabledPopup;

	private boolean enabledNewWin;
	
	/*
	 * use external DHCP and DNS servers on the network
	 */
	public static final short CWP_EXTERNAL = 1;

	/*
	 * use internal DHCP and DNS servers on the HiveAP
	 */
	public static final short CWP_INTERNAL = 2;

	private int serverType = CWP_EXTERNAL;
	
//	@OneToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "CWP_PAGE_CUSTOMIZATION_ID", nullable = true)
	@Embedded
	private CwpPageCustomization pageCustomization;
	
	public static final byte WEB_PAGE_SOURCE_AUTOGENERATE = 1;
	
	public static final byte WEB_PAGE_SOURCE_IMPORT = 2;
	
	private byte webPageSource = WEB_PAGE_SOURCE_AUTOGENERATE;
	
	public static final byte SUCCESS_PAGE_SOURCE_CUSTOMIZE = 2;
	
	public static final byte SUCCESS_PAGE_SOURCE_IMPORT = 3;
	
	private byte successPageSource = SUCCESS_PAGE_SOURCE_CUSTOMIZE;
	
	public static final byte FAILURE_PAGE_SOURCE_CUSTOMIZE = 2;
	
	public static final byte FAILURE_PAGE_SOURCE_IMPORT = 3;
	
	private byte failurePageSource = FAILURE_PAGE_SOURCE_CUSTOMIZE;
	
	public static final byte REGISTRATION_TYPE_AUTHENTICATED = 1;
	
	public static final byte REGISTRATION_TYPE_EXTERNAL = 5;
	
	public static final byte REGISTRATION_TYPE_REGISTERED = 2;
	
	public static final byte REGISTRATION_TYPE_BOTH = 3;
	
	public static final byte REGISTRATION_TYPE_EULA = 4;
	
	public static final byte REGISTRATION_TYPE_PPSK = 6;
	
	public static final EnumItem[] ENUM_REGISTRATION_TYPE = MgrUtil.enumItems(
			"enum.registrationType.", new int[] { REGISTRATION_TYPE_AUTHENTICATED,
					REGISTRATION_TYPE_EXTERNAL,
					REGISTRATION_TYPE_REGISTERED,
					REGISTRATION_TYPE_BOTH,
					REGISTRATION_TYPE_EULA,
					REGISTRATION_TYPE_PPSK});
	
	public static EnumItem[] ENUM_REGISTRATION_TYPE_EXPRESS = MgrUtil.enumItems(
			"enum.registrationType.", new int[] { REGISTRATION_TYPE_AUTHENTICATED,
					REGISTRATION_TYPE_REGISTERED,
					REGISTRATION_TYPE_EULA});
	
	public static EnumItem[] ENUM_REGISTRATION_TYPE_SELFONLY = MgrUtil.enumItems(
			"enum.registrationType.", new int[] {REGISTRATION_TYPE_EULA});
	public static final EnumItem[] ENUM_REGISTRATION_TYPE_AUTHONLY = MgrUtil.enumItems(
	        "enum.idm.registrationType.", new int[] {Cwp.REGISTRATION_TYPE_AUTHENTICATED});
	
    public static final EnumItem[] ENUM_IDM_REGISTRATION_NOT_OPEN = MgrUtil.enumItems(
            "enum.idm.registrationType.", new int[] {
                    Cwp.REGISTRATION_TYPE_AUTHENTICATED,
                    Cwp.REGISTRATION_TYPE_BOTH });
    public static final EnumItem[] ENUM_IDM_REGISTRATION_OPEN = MgrUtil.enumItems(
            "enum.idm.registrationType.", new int[] {
                    Cwp.REGISTRATION_TYPE_AUTHENTICATED,
                    Cwp.REGISTRATION_TYPE_BOTH,
                    Cwp.REGISTRATION_TYPE_EULA});
    public static final EnumItem[] ENUM_IDM_REGISTRATION_WIRED = MgrUtil.enumItems(
            "enum.idm.registrationType.", new int[] {
                    Cwp.REGISTRATION_TYPE_AUTHENTICATED,
                    Cwp.REGISTRATION_TYPE_BOTH});
    public static final EnumItem[] ENUM_IDM_REGISTRATION_EXPRESS_OPEN = MgrUtil.enumItems(
            "enum.idm.registrationType.", new int[] {
                    Cwp.REGISTRATION_TYPE_AUTHENTICATED,
                    Cwp.REGISTRATION_TYPE_EULA});
	
	private byte registrationType = REGISTRATION_TYPE_AUTHENTICATED;
	
	private boolean overrideVlan;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VLAN_ID")
	private Vlan vlan;

	/*
	 * begin:   external CWP , Nov 12th,2009 ==================
	 */
	@Column(length = 256, nullable = true)
	private String loginURL = "http://";

	public static final byte PASSWORD_ENCRYPTION_BASIC = 1;

	public static final byte PASSWORD_ENCRYPTION_SHARED = 2;

	public static final byte PASSWORD_ENCRYPTION_PLAIN = 3;

	public static EnumItem[] ENUM_PASSWORD_ENCRYPTION = MgrUtil
			.enumItems("enum.cwp.passwordEncryption.", new int[] { PASSWORD_ENCRYPTION_BASIC,
					PASSWORD_ENCRYPTION_SHARED,
					PASSWORD_ENCRYPTION_PLAIN});

	private byte passwordEncryption = PASSWORD_ENCRYPTION_SHARED;
	
	@Column(length = 128, nullable = true)
	private String sharedSecret;
	
	public static final short DEFAULT_SESSION_EXPIRATION_ALERT = 5;
	
	@Range(min=1, max=30)
	private short sessionAlert = DEFAULT_SESSION_EXPIRATION_ALERT; // minutes
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "WALLED_GARDEN_ITEM", joinColumns = @JoinColumn(name = "WALLED_GARDEN_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<WalledGardenItem> walledGarden = new ArrayList<WalledGardenItem>();

	public static final byte SUCCESS_REDIRECT_NO = 1;
	
	public static final byte SUCCESS_REDIRECT_ORIGINAL = 2;
	
	public static final byte SUCCESS_REDIRECT_EXTERNAL = 3;
	
	private byte successRedirection = SUCCESS_REDIRECT_NO;

	@Column(length = 256, nullable = true)
	private String successExternalURL = "http://";
	
	public static final byte FAILURE_REDIRECT_NO = 1;
	
	public static final byte FAILURE_REDIRECT_LOGIN = 2;
	
	public static final byte FAILURE_REDIRECT_EXTERNAL = 3;
	
	private byte failureRedirection = FAILURE_REDIRECT_NO;

	@Column(length = 256, nullable = true)
	private String failureExternalURL = "http://";
	
	private boolean showSuccessPage = true;
	
	private boolean showFailurePage = true;
	
	private boolean useLoginAsFailure = true;
	
	public static final byte DEFAULT_SUCCESS_DELAY = 5;
	
	@Range(min=5, max=60)
	private byte successDelay = DEFAULT_SUCCESS_DELAY;
	
	public static final byte DEFAULT_FAILURE_DELAY = 5;
	
	@Range(min=5, max=60)
	private byte failureDelay = DEFAULT_FAILURE_DELAY;
	
	private boolean disableRoamingLogin;
	
	private boolean needReassociate = true;
	
	private boolean enabledHTTP302;
	
	@Column(name = "multiLanguageSupport",  nullable = false,columnDefinition="INT default 128")
	private int multiLanguageSupport = 128;
	
	@Column(name = "defaultLanguage",  nullable = false,columnDefinition="INT default 1")
	private int defaultLanguage = 1;
	
	public int getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(int defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public int getMultiLanguageSupport() {
		return multiLanguageSupport;
	}

	public void setMultiLanguageSupport(int multiLanguageSupport) {
		this.multiLanguageSupport = multiLanguageSupport;
	}
	
	@Transient
    private String languageSupportDisplayStyle = "none"; // by default
	

	public String getLanguageSupportDisplayStyle() {
		return languageSupportDisplayStyle;
	}

	public void setLanguageSupportDisplayStyle(String languageSupportDisplayStyle) {
		this.languageSupportDisplayStyle = languageSupportDisplayStyle;
	}

	@Transient
	private String selfRegOnly;
	
	@Column(length = 32, nullable = true)
	private String serverDomainName;
	
	private boolean certificateDN;
	
	@Column(length = 256)
	private String blockRedirectURL;
	
	public final static byte PPSK_SERVER_TYPE_AUTH = 1;
	
	public final static byte PPSK_SERVER_TYPE_REG = 2;
	
	public final static byte PPSK_SERVER_DEFAULT_TYPE = PPSK_SERVER_TYPE_AUTH;
	
	private byte ppskServerType = PPSK_SERVER_DEFAULT_TYPE; 
	
	private boolean enableUsePolicy;
	
	private boolean idmSelfReg;
	
	public boolean isPpskServer() {
		return this.registrationType == Cwp.REGISTRATION_TYPE_PPSK;
	}
	public boolean isAuthRegType(){
		return this.registrationType == Cwp.REGISTRATION_TYPE_AUTHENTICATED
				|| this.registrationType == Cwp.REGISTRATION_TYPE_EXTERNAL;
	}
	
    @Transient
    private int ssidAccessMode;
    @Transient
    private int previousRegType;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IPADDRESS_SUCCESS_ID")
	private IpAddress ipAddressSuccess;
    
    @ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name = "IPADDRESS_FAILURE_ID")
 	private IpAddress ipAddressFailure;
    
    public static final byte EXTERNAL_URL_SINGLE = 1;
	
	public static final byte EXTERNAL_URL_TAGGED = 2;
	
	private byte externalURLSuccessType = EXTERNAL_URL_SINGLE;
	
	private byte externalURLFailureType = EXTERNAL_URL_SINGLE;

	/**
	 * getter of loginURL
	 * @return the loginURL
	 */
	public String getLoginURL() {
		return loginURL;
	}

	/**
	 * setter of loginURL
	 * @param loginURL the loginURL to set
	 */
	public void setLoginURL(String loginURL) {
		this.loginURL = loginURL;
	}

	/**
	 * getter of passwordEncryption
	 * @return the passwordEncryption
	 */
	public byte getPasswordEncryption() {
		return passwordEncryption;
	}

	/**
	 * setter of passwordEncryption
	 * @param passwordEncryption the passwordEncryption to set
	 */
	public void setPasswordEncryption(byte passwordEncryption) {
		this.passwordEncryption = passwordEncryption;
	}

	/**
	 * getter of sharedSecret
	 * @return the sharedSecret
	 */
	public String getSharedSecret() {
		return sharedSecret;
	}

	/**
	 * setter of sharedSecret
	 * @param sharedSecret the sharedSecret to set
	 */
	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}

	/**
	 * getter of sessionAlert
	 * @return the sessionAlert
	 */
	public short getSessionAlert() {
		return sessionAlert;
	}

	/**
	 * setter of sessionAlert
	 * @param sessionAlert the sessionAlert to set
	 */
	public void setSessionAlert(short sessionAlert) {
		this.sessionAlert = sessionAlert;
	}

	/**
	 * getter of walledGarden
	 * @return the walledGarden
	 */
	public List<WalledGardenItem> getWalledGarden() {
		return walledGarden;
	}

	/**
	 * setter of walledGarden
	 * @param walledGarden the walledGarden to set
	 */
	public void setWalledGarden(List<WalledGardenItem> walledGarden) {
		this.walledGarden = walledGarden;
	}
	
	/*
	 * end:   external CWP , Nov 12th,2009 ==================
	 */
	/**
	 * getter of registrationType
	 * @return the registrationType
	 */
	public byte getRegistrationType() {
		return registrationType;
	}

	/**
	 * setter of registrationType
	 * @param registrationType the registrationType to set
	 */
	public void setRegistrationType(byte registrationType) {
		this.registrationType = registrationType;
	}

	public int getDhcpMode() {
		return dhcpMode;
	}

	public void setDhcpMode(int dhcpMode) {
		this.dhcpMode = dhcpMode;
	}

	public String getDirectoryName() {
		return directoryName;
	}

	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}

	public int getRegistrationPeriod() {
		return registrationPeriod;
	}

	public void setRegistrationPeriod(int registrationPeriod) {
		this.registrationPeriod = registrationPeriod;
	}

	public boolean isUseDefaultNetwork() {
		return useDefaultNetwork;
	}

	public boolean getUseDefaultNetwork() {
		return useDefaultNetwork;
	}

	public void setUseDefaultNetwork(boolean useDefaultNetwork) {
		this.useDefaultNetwork = useDefaultNetwork;
	}

	public String getIpForAMode() {
		return ipForAMode;
	}

	public void setIpForAMode(String ipForAMode) {
		this.ipForAMode = ipForAMode;
	}

	public String getIpForBGMode() {
		return ipForBGMode;
	}

	public void setIpForBGMode(String ipForBGMode) {
		this.ipForBGMode = ipForBGMode;
	}

	public CwpCertificate getCertificate() {
		return this.certificate;
	}
	
	public void setCertificate(CwpCertificate certificate) {
		this.certificate = certificate;
	}

	public int getLeaseTime() {
		return leaseTime;
	}

	public void setLeaseTime(int leaseTime) {
		this.leaseTime = leaseTime;
	}

	public String getMaskForAMode() {
		return maskForAMode;
	}

	public void setMaskForAMode(String maskForAMode) {
		this.maskForAMode = maskForAMode;
	}

	public String getMaskForBGMode() {
		return maskForBGMode;
	}

	public void setMaskForBGMode(String maskForBGMode) {
		this.maskForBGMode = maskForBGMode;
	}
	
	/**
	 * getter of ipForEth0
	 * @return the ipForEth0
	 */
	public String getIpForEth0() {
		return ipForEth0;
	}

	/**
	 * setter of ipForEth0
	 * @param ipForEth0 the ipForEth0 to set
	 */
	public void setIpForEth0(String ipForEth0) {
		this.ipForEth0 = ipForEth0;
	}

	/**
	 * getter of maskForEth0
	 * @return the maskForEth0
	 */
	public String getMaskForEth0() {
		return maskForEth0;
	}

	/**
	 * setter of maskForEth0
	 * @param maskForEth0 the maskForEth0 to set
	 */
	public void setMaskForEth0(String maskForEth0) {
		this.maskForEth0 = maskForEth0;
	}

	/**
	 * getter of ipForEth1
	 * @return the ipForEth1
	 */
	public String getIpForEth1() {
		return ipForEth1;
	}

	/**
	 * setter of ipForEth1
	 * @param ipForEth1 the ipForEth1 to set
	 */
	public void setIpForEth1(String ipForEth1) {
		this.ipForEth1 = ipForEth1;
	}

	/**
	 * getter of maskForEth1
	 * @return the maskForEth1
	 */
	public String getMaskForEth1() {
		return maskForEth1;
	}

	/**
	 * setter of maskForEth1
	 * @param maskForEth1 the maskForEth1 to set
	 */
	public void setMaskForEth1(String maskForEth1) {
		this.maskForEth1 = maskForEth1;
	}

	public String getResultPageName() {
		return resultPageName;
	}

	public void setResultPageName(String resultPageName) {
		this.resultPageName = resultPageName;
	}
	
	/**
	 * getter of failurePageName
	 * @return the failurePageName
	 */
	public String getFailurePageName() {
		return failurePageName;
	}

	/**
	 * setter of failurePageName
	 * @param failurePageName the failurePageName to set
	 */
	public void setFailurePageName(String failurePageName) {
		this.failurePageName = failurePageName;
	}

	public String getWebPageName() {
		return webPageName;
	}

	public void setWebPageName(String webPageName) {
		this.webPageName = webPageName;
	}

	public String getCwpName() {
		return cwpName;
	}
	
	public String getCwpNameSubstr() {
		if (cwpName==null) {
			return "";
		}
		if (cwpName.length()> BaseAction.DISPLAY_LENGTH_IN_GUI_OK) {
			return cwpName.substring(0, BaseAction.DISPLAY_LENGTH_IN_GUI) + "...";
		}
		
		return cwpName;
	}

	public void setCwpName(String cwpName) {
		this.cwpName = cwpName;
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
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return this.cwpName;
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

	public int getNumberField() {
		return numberField;
	}

	public void setNumberField(int numberField) {
		this.numberField = numberField;
	}

	public int getRequestField() {
		return requestField;
	}

	public void setRequestField(int requestField) {
		this.requestField = requestField;
	}

	public boolean isEnabledHttps() {
		return enabledHttps;
	}

	public boolean getEnabledHttps() {
		return enabledHttps;
	}

	public void setEnabledHttps(boolean enabledHttps) {
		this.enabledHttps = enabledHttps;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public int getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(int authMethod) {
		this.authMethod = authMethod;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Cwp)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((Cwp) other).getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}

	public boolean isEnabledPopup() {
		return enabledPopup;
	}

	public void setEnabledPopup(boolean enabledPopup) {
		this.enabledPopup = enabledPopup;
	}

	@Transient
	public String getAuthString() {
		short authType = getRegistrationType();
		
		if(authType == Cwp.REGISTRATION_TYPE_AUTHENTICATED
				|| authType == Cwp.REGISTRATION_TYPE_BOTH)
			return MgrUtil.getEnumString("enum.authMethod."+ authMethod);
		else
			return "-";
	}

	public boolean isEnabledNewWin()
	{
		return enabledNewWin;
	}

	public void setEnabledNewWin(boolean enabledNewWin)
	{
		this.enabledNewWin = enabledNewWin;
	}

	@Transient
	public String getSessionTimerDisplay() {
		return this.enabledPopup ? "" : "none";
	}
	
	public int getServerType() {
		return serverType;
	}

	public void setServerType(int serverType) {
		this.serverType = serverType;
	}

	/**
	 * getter of pageCustomization
	 * @return the pageCustomization
	 */
	public CwpPageCustomization getPageCustomization() {
		return pageCustomization;
	}

	/**
	 * setter of pageCustomization
	 * @param pageCustomization the pageCustomization to set
	 */
	public void setPageCustomization(CwpPageCustomization pageCustomization) {
		this.pageCustomization = pageCustomization;
	}

	/**
	 * getter of webPageSource
	 * @return the webPageSource
	 */
	public byte getWebPageSource() {
		return webPageSource;
	}

	/**
	 * setter of webPageSource
	 * @param webPageSource the webPageSource to set
	 */
	public void setWebPageSource(byte webPageSource) {
		this.webPageSource = webPageSource;
	}
	
	
	/**
	 * getter of successPageSource
	 * @return the successPageSource
	 */
	public byte getSuccessPageSource() {
		return successPageSource;
	}

	/**
	 * setter of successPageSource
	 * @param successPageSource the successPageSource to set
	 */
	public void setSuccessPageSource(byte successPageSource) {
		this.successPageSource = successPageSource;
	}

	/**
	 * getter of failurePageSource
	 * @return the failurePageSource
	 */
	public byte getFailurePageSource() {
		return failurePageSource;
	}

	/**
	 * setter of failurePageSource
	 * @param failurePageSource the failurePageSource to set
	 */
	public void setFailurePageSource(byte failurePageSource) {
		this.failurePageSource = failurePageSource;
	}

	public String getRegistrationTypeString() {
		for (EnumItem ei : ENUM_REGISTRATION_TYPE) {
			if (ei.getKey() == this.getRegistrationType())
				return ei.getValue();
		}
		
		return "-";
	}
	
	@Transient
	private String currentOperation;

	public String getCurrentOperation() {
		return currentOperation;
	}

	public void setCurrentOperation(String currentOperation) {
		this.currentOperation = currentOperation;
	}

	/**
	 * getter of overrideVlan
	 * @return the overrideVlan
	 */
	public boolean isOverrideVlan() {
		return overrideVlan;
	}

	/**
	 * setter of overrideVlan
	 * @param overrideVlan the overrideVlan to set
	 */
	public void setOverrideVlan(boolean overrideVlan) {
		this.overrideVlan = overrideVlan;
	}

	/**
	 * getter of vlan
	 * @return the vlan
	 */
	public Vlan getVlan() {
		return vlan;
	}

	/**
	 * setter of vlan
	 * @param vlan the vlan to set
	 */
	public void setVlan(Vlan vlan) {
		this.vlan = vlan;
	}
	
	@Override
	public Cwp clone() {
		try {
			return (Cwp) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	private boolean defaultFlag;
	
	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}
	
	@Transient
	private byte tempService = WalledGardenItem.SERVICE_ALL;
	
	@Transient
	private int tempProtocol;
	
	@Transient
	private int tempPort;
	
	@Transient
	private String hideCreateItem = "none";

	@Transient
	private String hideNewButton = "";

	/**
	 * getter of hideCreateItem
	 * @return the hideCreateItem
	 */
	public String getHideCreateItem() {
		return hideCreateItem;
	}

	/**
	 * setter of hideCreateItem
	 * @param hideCreateItem the hideCreateItem to set
	 */
	public void setHideCreateItem(String hideCreateItem) {
		this.hideCreateItem = hideCreateItem;
	}

	/**
	 * getter of hideNewButton
	 * @return the hideNewButton
	 */
	public String getHideNewButton() {
		return hideNewButton;
	}

	/**
	 * setter of hideNewButton
	 * @param hideNewButton the hideNewButton to set
	 */
	public void setHideNewButton(String hideNewButton) {
		this.hideNewButton = hideNewButton;
	}

	/**
	 * getter of tempService
	 * @return the tempService
	 */
	public byte getTempService() {
		return tempService;
	}

	/**
	 * setter of tempService
	 * @param tempService the tempService to set
	 */
	public void setTempService(byte tempService) {
		this.tempService = tempService;
	}

	/**
	 * getter of tempProtocol
	 * @return the tempProtocol
	 */
	public int getTempProtocol() {
		return tempProtocol;
	}

	/**
	 * setter of tempProtocol
	 * @param tempProtocol the tempProtocol to set
	 */
	public void setTempProtocol(int tempProtocol) {
		this.tempProtocol = tempProtocol;
	}

	/**
	 * getter of tempPort
	 * @return the tempPort
	 */
	public int getTempPort() {
		return tempPort;
	}

	/**
	 * setter of tempPort
	 * @param tempPort the tempPort to set
	 */
	public void setTempPort(int tempPort) {
		this.tempPort = tempPort;
	}
	
	@Transient
    private String advancedDisplayStyle = "none"; // by default

	/**
	 * getter of advancedDisplayStyle
	 * @return the advancedDisplayStyle
	 */
	public String getAdvancedDisplayStyle() {
		return advancedDisplayStyle;
	}

	/**
	 * setter of advancedDisplayStyle
	 * @param advancedDisplayStyle the advancedDisplayStyle to set
	 */
	public void setAdvancedDisplayStyle(String advancedDisplayStyle) {
		this.advancedDisplayStyle = advancedDisplayStyle;
	}

	@Transient
    private String walledGardenDisplayStyle = "none"; // by default

	/**
	 * getter of walledGardenDisplayStyle
	 * @return the walledGardenDisplayStyle
	 */
	public String getWalledGardenDisplayStyle() {
		return walledGardenDisplayStyle;
	}

	/**
	 * setter of walledGardenDisplayStyle
	 * @param walledGardenDisplayStyle the walledGardenDisplayStyle to set
	 */
	public void setWalledGardenDisplayStyle(String walledGardenDisplayStyle) {
		this.walledGardenDisplayStyle = walledGardenDisplayStyle;
	}

	@Transient
    private String loginDisplayStyle = "none"; // by default
	@Transient
    private String successDisplayStyle = "none"; // by default
	@Transient
    private String failureDisplayStyle = "none"; // by default

	
	/**
	 * getter of loginDisplayStyle
	 * @return the loginDisplayStyle
	 */
	public String getLoginDisplayStyle() {
		return loginDisplayStyle;
	}

	/**
	 * setter of loginDisplayStyle
	 * @param loginDisplayStyle the loginDisplayStyle to set
	 */
	public void setLoginDisplayStyle(String loginDisplayStyle) {
		this.loginDisplayStyle = loginDisplayStyle;
	}

	/**
	 * getter of successDisplayStyle
	 * @return the successDisplayStyle
	 */
	public String getSuccessDisplayStyle() {
		return successDisplayStyle;
	}

	/**
	 * setter of successDisplayStyle
	 * @param successDisplayStyle the successDisplayStyle to set
	 */
	public void setSuccessDisplayStyle(String successDisplayStyle) {
		this.successDisplayStyle = successDisplayStyle;
	}

	/**
	 * getter of failureDisplayStyle
	 * @return the failureDisplayStyle
	 */
	public String getFailureDisplayStyle() {
		return failureDisplayStyle;
	}

	/**
	 * setter of failureDisplayStyle
	 * @param failureDisplayStyle the failureDisplayStyle to set
	 */
	public void setFailureDisplayStyle(String failureDisplayStyle) {
		this.failureDisplayStyle = failureDisplayStyle;
	}

	/**
	 * getter of successRedirection
	 * @return the successRedirection
	 */
	public byte getSuccessRedirection() {
		return successRedirection;
	}

	/**
	 * setter of successRedirection
	 * @param successRedirection the successRedirection to set
	 */
	public void setSuccessRedirection(byte successRedirection) {
		this.successRedirection = successRedirection;
	}

	/**
	 * getter of successExternalURL
	 * @return the successExternalURL
	 */
	public String getSuccessExternalURL() {
		return successExternalURL;
	}

	/**
	 * setter of successExternalURL
	 * @param successExternalURL the successExternalURL to set
	 */
	public void setSuccessExternalURL(String successExternalURL) {
		this.successExternalURL = successExternalURL;
	}

	/**
	 * getter of failureRedirection
	 * @return the failureRedirection
	 */
	public byte getFailureRedirection() {
		return failureRedirection;
	}

	/**
	 * setter of failureRedirection
	 * @param failureRedirection the failureRedirection to set
	 */
	public void setFailureRedirection(byte failureRedirection) {
		this.failureRedirection = failureRedirection;
	}

	/**
	 * getter of failureExternalURL
	 * @return the failureExternalURL
	 */
	public String getFailureExternalURL() {
		return failureExternalURL;
	}

	/**
	 * setter of failureExternalURL
	 * @param failureExternalURL the failureExternalURL to set
	 */
	public void setFailureExternalURL(String failureExternalURL) {
		this.failureExternalURL = failureExternalURL;
	}

	/**
	 * getter of showSuccessPage
	 * @return the showSuccessPage
	 */
	public boolean isShowSuccessPage() {
		return showSuccessPage;
	}

	/**
	 * setter of showSuccessPage
	 * @param showSuccessPage the showSuccessPage to set
	 */
	public void setShowSuccessPage(boolean showSuccessPage) {
		this.showSuccessPage = showSuccessPage;
	}

	/**
	 * getter of showFailurePage
	 * @return the showFailurePage
	 */
	public boolean isShowFailurePage() {
		return showFailurePage;
	}

	/**
	 * setter of showFailurePage
	 * @param showFailurePage the showFailurePage to set
	 */
	public void setShowFailurePage(boolean showFailurePage) {
		this.showFailurePage = showFailurePage;
	}

	/**
	 * getter of successDelay
	 * @return the successDelay
	 */
	public byte getSuccessDelay() {
		return successDelay;
	}

	/**
	 * setter of successDelay
	 * @param successDelay the successDelay to set
	 */
	public void setSuccessDelay(byte successDelay) {
		this.successDelay = successDelay;
	}

	/**
	 * getter of failureDelay
	 * @return the failureDelay
	 */
	public byte getFailureDelay() {
		return failureDelay;
	}

	/**
	 * setter of failureDelay
	 * @param failureDelay the failureDelay to set
	 */
	public void setFailureDelay(byte failureDelay) {
		this.failureDelay = failureDelay;
	}

	/**
	 * getter of disableRoamingLogin
	 * @return the disableRoamingLogin
	 */
	public boolean isDisableRoamingLogin() {
		return disableRoamingLogin;
	}

	/**
	 * setter of disableRoamingLogin
	 * @param disableRoamingLogin the disableRoamingLogin to set
	 */
	public void setDisableRoamingLogin(boolean disableRoamingLogin) {
		this.disableRoamingLogin = disableRoamingLogin;
	}

	/**
	 * getter of needReassociate
	 * @return the needReassociate
	 */
	public boolean isNeedReassociate() {
		return needReassociate;
	}

	/**
	 * setter of needReassociate
	 * @param needReassociate the needReassociate to set
	 */
	public void setNeedReassociate(boolean needReassociate) {
		this.needReassociate = needReassociate;
	}

	/**
	 * getter of enabledHTTP302
	 * @return the enabledHTTP302
	 */
	public boolean isEnabledHTTP302() {
		return enabledHTTP302;
	}

	/**
	 * setter of enabledHTTP302
	 * @param enabledHTTP302 the enabledHTTP302 to set
	 */
	public void setEnabledHTTP302(boolean enabledHTTP302) {
		this.enabledHTTP302 = enabledHTTP302;
	}
	
	public String getSelfRegOnly() {
		return selfRegOnly;
	}

	public void setSelfRegOnly(String selfRegOnly) {
		this.selfRegOnly = selfRegOnly;
	}
	
	/**
	 * getter of serverDomainName
	 * @return the serverDomainName
	 */
	public String getServerDomainName() {
		return serverDomainName;
	}

	/**
	 * setter of serverDomainName
	 * @param serverDomainName the serverDomainName to set
	 */
	public void setServerDomainName(String serverDomainName) {
		this.serverDomainName = serverDomainName;
	}

	/**
	 * getter of certificateDN
	 * @return the certificateDN
	 */
	public boolean isCertificateDN() {
		return certificateDN;
	}

	/**
	 * setter of certificateDN
	 * @param certificateDN the certificateDN to set
	 */
	public void setCertificateDN(boolean certificateDN) {
		this.certificateDN = certificateDN;
	}

	public String getBlockRedirectURL() {
		return blockRedirectURL;
	}

	public void setBlockRedirectURL(String blockRedirectURL) {
		this.blockRedirectURL = blockRedirectURL;
	}

	/**
	 * getter of ppskServerType
	 * @return the ppskServerType
	 */
	public byte getPpskServerType() {
		return ppskServerType;
	}

	/**
	 * setter of ppskServerType
	 * @param ppskServerType the ppskServerType to set
	 */
	public void setPpskServerType(byte ppskServerType) {
		this.ppskServerType = ppskServerType;
	}
	
	public boolean isUseLoginAsFailure() {
		return useLoginAsFailure;
	}

	public void setUseLoginAsFailure(boolean useLoginAsFailure) {
		this.useLoginAsFailure = useLoginAsFailure;
	}

	@Transient
	public String getRegistrationTypeName() {
		String regName = null;
		
		for(EnumItem item : ENUM_REGISTRATION_TYPE) {
			if(item.getKey() == this.getRegistrationType()) {
				regName = item.getValue();
				break;
			}
		}
		
		return "CWP: " + regName;
	}
	@Transient
	public String getRegistrationTypeName4IDM() {
	    String regName = null;
	    
	    for(EnumItem item : ENUM_IDM_REGISTRATION_OPEN) {
	        if(item.getKey() == this.getRegistrationType()) {
	            regName = item.getValue();
	            break;
	        }
	    }
	    
	    return "CWP: " + regName;
	}

	public boolean isEnableUsePolicy() {
		return enableUsePolicy;
	}

	public void setEnableUsePolicy(boolean enableUsePolicy) {
		this.enableUsePolicy = enableUsePolicy;
	}
	
	@Transient
	public boolean isValidWebDirector(){
		return !(registrationType == REGISTRATION_TYPE_EXTERNAL && !showSuccessPage && !showFailurePage) || 
				this.isEnabledHttps();
	}

	public boolean isIdmSelfReg() {
		return idmSelfReg;
	}

	public void setIdmSelfReg(boolean idmSelfReg) {
		this.idmSelfReg = idmSelfReg;
	}

    public int getSsidAccessMode() {
        return ssidAccessMode;
    }

    public void setSsidAccessMode(int ssidAccessMode) {
        this.ssidAccessMode = ssidAccessMode;
    }

    public int getPreviousRegType() {
        return previousRegType;
    }

    public void setPreviousRegType(int previousRegType) {
        this.previousRegType = previousRegType;
    }

	public IpAddress getIpAddressSuccess() {
		return ipAddressSuccess;
	}

	public void setIpAddressSuccess(IpAddress ipAddressSuccess) {
		this.ipAddressSuccess = ipAddressSuccess;
	}

	public IpAddress getIpAddressFailure() {
		return ipAddressFailure;
	}

	public void setIpAddressFailure(IpAddress ipAddressFailure) {
		this.ipAddressFailure = ipAddressFailure;
	}

	public byte getExternalURLSuccessType() {
		return externalURLSuccessType;
	}

	public void setExternalURLSuccessType(byte externalURLSuccessType) {
		this.externalURLSuccessType = externalURLSuccessType;
	}

	public byte getExternalURLFailureType() {
		return externalURLFailureType;
	}

	public void setExternalURLFailureType(byte externalURLFailureType) {
		this.externalURLFailureType = externalURLFailureType;
	}
}