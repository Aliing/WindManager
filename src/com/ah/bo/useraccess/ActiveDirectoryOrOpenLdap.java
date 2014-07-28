/**
 *@filename		ActiveDirectoryOrOpenLdap.java
 *@version
 *@author		Fiona
 *@createtime	2008-1-14 PM 02:27:02
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.useraccess;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "ACTIVE_DIRECTORY_OR_LDAP")
@org.hibernate.annotations.Table(appliesTo = "ACTIVE_DIRECTORY_OR_LDAP", indexes = {
		@Index(name = "ACTIVE_DIRECTORY_OR_LDAP_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class ActiveDirectoryOrOpenLdap implements HmBo {
	
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

	@Column(length = DEFAULT_DESCRIPTION_LENGTH, nullable = false)
	private String name = "";

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description = "";

	private short typeFlag = TYPE_ACTIVE_DIRECTORY;

	public static final short TYPE_ACTIVE_DIRECTORY = 1;

	public static final short TYPE_OPEN_LDAP = 2;
	
	public static final short TYPE_OPEN_DIRECTORY = 3;

	// @Column(length = 64)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AD_IPADDRESS_ID")
	private IpAddress adServer;

	// name of administrator
	@Column(length = 64)
	private String userNameA = "";

	// password of administrator
	@Column(length = 64)
	private String passwordA = "";

	@Column(length = 256)
	private String computerOU = "";

	// @Column(length = 32)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "LDAP_IPADDRESS_ID")
	private IpAddress ldapServer;

	@Column(length = 256)
	private String basedN = "";

	// only for LDAP bindDN
	@Column(length = 256)
	private String bindDnName = "";
	
	public static final String FILTER_ATTR_OPEN_LDAP = "cn";
	
	public static final String FILTER_ATTR_OPEN_DIRECTORY = "uid";
	
	@Column(length = 32)
	private String filterAttr = FILTER_ATTR_OPEN_LDAP;
	
	@Transient
	private String filterAttrOd = FILTER_ATTR_OPEN_DIRECTORY;
	
	public static final short LDAP_SERVER_PROTOCOL_LDAP = 1;
	
	public static final short LDAP_SERVER_PROTOCOL_LDAPS = 2;
	
	public static EnumItem[] LDAP_SERVER_PROTOCOL = MgrUtil.enumItems("enum.ldap.server.protocol.", 
		new int[]{LDAP_SERVER_PROTOCOL_LDAP, LDAP_SERVER_PROTOCOL_LDAPS});
	
	private short ldapProtocol = LDAP_SERVER_PROTOCOL_LDAP;

	@Range(min = 1, max = 65535)
	private int destinationPort = 389;

	@Column(length = 64)
	private String keyPasswordO = "";

	// password for LDAP bindDN
	@Column(length = 64)
	private String passwordO = "";

	private boolean authTlsEnable;

	private String caCertFileO = BeAdminCentOSTools.AH_NMS_DEFAULT_CA;

	private String clientFile = BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_CERT;

	private String keyFileO = BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_KEY;

	public static final short RADIUS_VERIFY_SERVER_TRY = 1;

	public static final short RADIUS_VERIFY_SERVER_NEVER = 2;

	public static final short RADIUS_VERIFY_SERVER_DEMAND = 3;

	public static EnumItem[] RADIUS_VERIFY_SERVER = MgrUtil.enumItems(
			"enum.radiusVerifyServer.", new int[] { RADIUS_VERIFY_SERVER_TRY,
					RADIUS_VERIFY_SERVER_NEVER, RADIUS_VERIFY_SERVER_DEMAND });

	private short verifyServer = RADIUS_VERIFY_SERVER_TRY;
	
	// for active directory multiple domain
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "RADIUS_AD_DOMAIN", joinColumns = @JoinColumn(name = "AD_DOMAIN_ID"))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<ActiveDirectoryDomain> adDomains = new ArrayList<ActiveDirectoryDomain>();

	@Transient
	private String apHostName = "";

	private String apMac = "";
	
	@Transient
	private boolean dnsApplied;
	@Transient
	private String staticHiveAPIpAddress;
	@Transient
	private String staticHiveAPNetmask;
	@Transient
	private String staticHiveAPGateway;
	@Transient
	private String dnsServer;
	@Transient
	private boolean dhcp;

	@Transient
	private String multipleDomainStyle = "none"; // by default;
	
	private boolean saveCredentials;
	
	boolean stripFilter = true;
	
	@Transient
	boolean stripFilterOd = true;
	
	public static final short CLIENT_LDAP_SASL_WRAPPING_PLAIN = 0;
	
	public static final short CLIENT_LDAP_SASL_WRAPPING_SIGN = 1;
	
	public static final short CLIENT_LDAP_SASL_WRAPPING_SEAL = 2;
	
	public static final String CLIENT_LDAP_SASL_WRAPPING_SIGN_STR = "sign";
	public static final String CLIENT_LDAP_SASL_WRAPPING_SEAL_STR = "seal";
	
/*	public static EnumItem[] CLIENT_LDAP_SASL_WRAPPING = MgrUtil.enumItems("enum.client.ldap.sasl.wrapping.", 
		new int[]{CLIENT_LDAP_SASL_WRAPPING_PLAIN, CLIENT_LDAP_SASL_WRAPPING_SIGN, CLIENT_LDAP_SASL_WRAPPING_SEAL});*/
	
	public static EnumItem[] CLIENT_LDAP_SASL_WRAPPING = MgrUtil.enumItems("enum.client.ldap.sasl.wrapping.", 
		new int[]{CLIENT_LDAP_SASL_WRAPPING_PLAIN, CLIENT_LDAP_SASL_WRAPPING_SIGN});
	
	private short ldapSaslWrapping = CLIENT_LDAP_SASL_WRAPPING_PLAIN;

	// for generate CLI
	public String getLdapSaslWrappingString() {
		return getLdapSaslWrappingString(this.ldapSaslWrapping);
	}

	public static String getLdapSaslWrappingString(short ldapSaslWrapping) {
		String strResult = "";
		// TODO revert LDAP SASL feature in FUJI, make it always return ""(plain);
		ldapSaslWrapping = CLIENT_LDAP_SASL_WRAPPING_PLAIN;
		
		switch (ldapSaslWrapping) {
		case CLIENT_LDAP_SASL_WRAPPING_PLAIN:
			strResult = "";
			break;
		case CLIENT_LDAP_SASL_WRAPPING_SIGN:
			strResult = CLIENT_LDAP_SASL_WRAPPING_SIGN_STR;
			break;
		case CLIENT_LDAP_SASL_WRAPPING_SEAL:
			strResult = CLIENT_LDAP_SASL_WRAPPING_SEAL_STR;
			break;
		default:
			break;
		}
		
		return strResult;
	}

	public short getLdapSaslWrapping() {
		return ldapSaslWrapping;
	}

	public void setLdapSaslWrapping(short ldapSaslWrapping) {
		this.ldapSaslWrapping = ldapSaslWrapping;
	}

	public boolean isStripFilterOd() {
		return stripFilterOd;
	}

	public void setStripFilterOd(boolean stripFilterOd) {
		this.stripFilterOd = stripFilterOd;
	}

	public boolean isStripFilter() {
		return stripFilter;
	}

	public void setStripFilter(boolean stripFilter) {
		this.stripFilter = stripFilter;
	}

	public boolean isSaveCredentials() {
		return saveCredentials;
	}

	public void setSaveCredentials(boolean saveCredentials) {
		this.saveCredentials = saveCredentials;
	}

	public String getMultipleDomainStyle() {
		return multipleDomainStyle;
	}

	public void setMultipleDomainStyle(String multipleDomainStyle) {
		this.multipleDomainStyle = multipleDomainStyle;
	}

	public boolean isDnsApplied() {
		return dnsApplied;
	}

	public void setDnsApplied(boolean dnsApplied) {
		this.dnsApplied = dnsApplied;
	}

	public boolean isDhcp() {
		return dhcp;
	}

	public void setDhcp(boolean dhcp) {
		this.dhcp = dhcp;
	}

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

	public String getApHostName() {
		if ("".equals(apHostName) && !"".equals(apMac)) {
			HiveAp ap = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", apMac);
			if (ap != null) {
				apHostName = ap.getHostName();
			}
		}
		return apHostName;
	}

	public void setApHostName(String apHostName) {
		this.apHostName = apHostName;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Transient
	private boolean selected;

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
		return name;
	}

	public String getComputerOU() {
		return computerOU;
	}

	public void setComputerOU(String computerOU) {
		this.computerOU = computerOU;
	}

	public String getUserNameA() {
		return userNameA;
	}

	public void setUserNameA(String userNameA) {
		this.userNameA = userNameA;
	}

	public String getPasswordA() {
		return passwordA;
	}

	public void setPasswordA(String passwordA) {
		this.passwordA = passwordA;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public IpAddress getAdServer()
	{
		return adServer;
	}

	public void setAdServer(IpAddress adServer)
	{
		this.adServer = adServer;
	}

	public IpAddress getLdapServer()
	{
		return ldapServer;
	}

	public void setLdapServer(IpAddress ldapServer)
	{
		this.ldapServer = ldapServer;
	}

	public String getBasedN() {
		return basedN;
	}

	public void setBasedN(String basedN) {
		this.basedN = basedN;
	}

	public int getDestinationPort() {
		return destinationPort;
	}

	public void setDestinationPort(int destinationPort) {
		this.destinationPort = destinationPort;
	}

	public String getKeyPasswordO() {
		return keyPasswordO;
	}

	public void setKeyPasswordO(String keyPasswordO) {
		this.keyPasswordO = keyPasswordO;
	}

	public String getPasswordO() {
		return passwordO;
	}

	public void setPasswordO(String passwordO) {
		this.passwordO = passwordO;
	}

	public boolean isAuthTlsEnable() {
		return authTlsEnable;
	}

	public void setAuthTlsEnable(boolean authTlsEnable) {
		this.authTlsEnable = authTlsEnable;
	}

	@Transient
	public String getTypeString() {
		switch (typeFlag) {
			case TYPE_ACTIVE_DIRECTORY:
				return "Active Directory";
			case TYPE_OPEN_LDAP:
				return "LDAP";
			case TYPE_OPEN_DIRECTORY:
				return "Open Directory";
		}
		return "";
	}

	public String getCaCertFileO() {
		return caCertFileO;
	}

	public void setCaCertFileO(String caCertFileO) {
		this.caCertFileO = caCertFileO;
	}

	public String getClientFile() {
		return clientFile;
	}

	public void setClientFile(String clientFile) {
		this.clientFile = clientFile;
	}

	public String getKeyFileO() {
		return keyFileO;
	}

	public void setKeyFileO(String keyFileO) {
		this.keyFileO = keyFileO;
	}

	public short getVerifyServer() {
		return verifyServer;
	}

	public void setVerifyServer(short verifyServer) {
		this.verifyServer = verifyServer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public short getTypeFlag() {
		return typeFlag;
	}

	public void setTypeFlag(short typeFlag) {
		this.typeFlag = typeFlag;
	}

	public String getBindDnName()
	{
		return bindDnName;
	}

	public void setBindDnName(String bindDnName)
	{
		this.bindDnName = bindDnName;
	}

	public String getFilterAttr()
	{
		return filterAttr;
	}

	public void setFilterAttr(String filterAttr)
	{
		this.filterAttr = filterAttr;
	}

	public List<ActiveDirectoryDomain> getAdDomains()
	{
		return adDomains;
	}

	public void setAdDomains(List<ActiveDirectoryDomain> adDomains)
	{
		this.adDomains = adDomains;
	}

	public short getLdapProtocol()
	{
		return ldapProtocol;
	}

	public void setLdapProtocol(short ldapProtocol)
	{
		this.ldapProtocol = ldapProtocol;
	}
	
	@Transient
	private String optionalStyle = "none";

	public String getOptionalStyle()
	{
		return optionalStyle;
	}

	public void setOptionalStyle(String optionalStyle)
	{
		this.optionalStyle = optionalStyle;
	}
	
	@Override
	public ActiveDirectoryOrOpenLdap clone() {
		try {
			return (ActiveDirectoryOrOpenLdap)super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	@Transient
	private String ipInputValue;

	public String getIpInputValue()
	{
		return ipInputValue;
	}

	public void setIpInputValue(String ipInputValue)
	{
		this.ipInputValue = ipInputValue;
	}
	
	@Transient
	private ActiveDirectoryDomain defDomain = new ActiveDirectoryDomain();

	public ActiveDirectoryDomain getDefDomain()
	{
		return defDomain;
	}

	public void setDefDomain(ActiveDirectoryDomain defDomain) {
		this.defDomain = defDomain;
	}
	
	@Transient
	private List<ActiveDirectoryDomain> nonDefDomains = new ArrayList<ActiveDirectoryDomain>();

	public List<ActiveDirectoryDomain> getNonDefDomains()
	{
		return nonDefDomains;
	}

	public void setNonDefDomains(List<ActiveDirectoryDomain> nonDefDomains)
	{
		this.nonDefDomains = nonDefDomains;
	}

	// add for ad configuration improvement
	@Transient
	private String userNameOd = "";
	@Transient
	private String passwordOd = "";
	@Transient
	private String hidAdServer = "";
	@Transient
	private String hidAdBaseDn = "";
	
	@Transient
	private ActiveDirectoryDomain defOdDomain = new ActiveDirectoryDomain();
	
	@Transient
	private int retrieveSuccess = 0;
	@Transient
	private int testJoinSuccess = 0;
	@Transient
	private int testAuthSuccess = 0;
	
	@Transient
	private int adminIsShow = 0;
	@Transient
	private int domainUserIsShow = 0;
	@Transient
	private int multiDomainIsShow = 0;
	
	public String getHidAdServer() {
		return hidAdServer;
	}

	public void setHidAdServer(String hidAdServer) {
		this.hidAdServer = hidAdServer;
	}

	public String getHidAdBaseDn() {
		return hidAdBaseDn;
	}

	public void setHidAdBaseDn(String hidAdBaseDn) {
		this.hidAdBaseDn = hidAdBaseDn;
	}

	public String getUserNameOd() {
		return userNameOd;
	}

	public void setUserNameOd(String userNameOd) {
		this.userNameOd = userNameOd;
	}

	public String getPasswordOd() {
		return passwordOd;
	}

	public void setPasswordOd(String passwordOd) {
		this.passwordOd = passwordOd;
	}

	public ActiveDirectoryDomain getDefOdDomain() {
		return defOdDomain;
	}

	public void setDefOdDomain(ActiveDirectoryDomain defOdDomain) {
		this.defOdDomain = defOdDomain;
	}

	public int getRetrieveSuccess() {
		return retrieveSuccess;
	}

	public void setRetrieveSuccess(int retrieveSuccess) {
		this.retrieveSuccess = retrieveSuccess;
	}

	public int getTestJoinSuccess() {
		return testJoinSuccess;
	}

	public void setTestJoinSuccess(int testJoinSuccess) {
		this.testJoinSuccess = testJoinSuccess;
	}

	public int getTestAuthSuccess() {
		return testAuthSuccess;
	}

	public void setTestAuthSuccess(int testAuthSuccess) {
		this.testAuthSuccess = testAuthSuccess;
	}

	public int getAdminIsShow() {
		return adminIsShow;
	}

	public void setAdminIsShow(int adminIsShow) {
		this.adminIsShow = adminIsShow;
	}

	public int getDomainUserIsShow() {
		return domainUserIsShow;
	}

	public void setDomainUserIsShow(int domainUserIsShow) {
		this.domainUserIsShow = domainUserIsShow;
	}

	public int getMultiDomainIsShow() {
		return multiDomainIsShow;
	}

	public void setMultiDomainIsShow(int multiDomainIsShow) {
		this.multiDomainIsShow = multiDomainIsShow;
	}
	
	@Transient
	private boolean parentIframeOpenFlg;
	@Transient
	private String parentDomID = "";
	@Transient
	private String contentShowType = "subdrawer";
	
	public String getContentShowType() {
		return contentShowType;
	}

	public void setContentShowType(String contentShowType) {
		this.contentShowType = contentShowType;
	}
	
	public boolean isParentIframeOpenFlg() {
		return parentIframeOpenFlg;
	}

	public void setParentIframeOpenFlg(boolean parentIframeOpenFlg) {
		this.parentIframeOpenFlg = parentIframeOpenFlg;
	}

	public String getParentDomID() {
		return parentDomID;
	}

	public void setParentDomID(String parentDomID) {
		this.parentDomID = parentDomID;
	}

	public String getFilterAttrOd() {
		return filterAttrOd;
	}

	public void setFilterAttrOd(String filterAttrOd) {
		this.filterAttrOd = filterAttrOd;
	}
	
}