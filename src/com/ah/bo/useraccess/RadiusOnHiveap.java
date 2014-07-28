/**
 *@filename		RadiusOnHiveap.java
 *@version
 *@author		Fiona
 *@createtime	2007-10-9 PM 02:46:49
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *remove local user, add local user group
 *2009-02-17
 */
package com.ah.bo.useraccess;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Min;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.network.IpAddress;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "RADIUS_ON_HIVEAP")
@org.hibernate.annotations.Table(appliesTo = "RADIUS_ON_HIVEAP", indexes = {
		@Index(name = "RADIUS_ON_HIVE_AP_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class RadiusOnHiveap implements HmBo {

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
	private String radiusName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	private boolean serverEnable = true;

	@Range(min = 1, max = 65535)
	private int serverPort = 1812;

	private boolean cacheEnable;

	@Range(min = 3600, max = 2592000)
	private int cacheTime = 86400;

	@Range(min = 30, max = 3600)
	private int localInterval = 300;

	@Range(min = 10, max = 3600)
	private int remoteInterval = 30;

	@Min(60)
	private int retryInterval = 600;

	private boolean mapEnable;
	
	/*
	 * For authentication
	 */
	private boolean cnEnable;

	private boolean dbEnable;
	
	private boolean ttlsCheckInDb;
	
	private boolean peapCheckInDb;

	public static final short RADIUS_SERVER_MAP_BY_GROUPATTRI = 1;

	public static final short RADIUS_SERVER_MAP_BY_USERATTRI = 2;

	private short mapByGroupOrUser = RADIUS_SERVER_MAP_BY_GROUPATTRI;

	private String caCertFile = BeAdminCentOSTools.AH_NMS_DEFAULT_CA;

	private String keyFile = BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_KEY;

	@Column(length = 64)
	private String keyPassword = "";

	private String serverFile = BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_CERT;

	@Column(length = 32)
	private String reauthTime = "";

	@Column(length = 32)
	private String userProfileId = "";

	@Column(length = 32)
	private String groupAttribute = "";

	@Column(length = 32)
	private String vlanId = "";
	
	public static final short RADIUS_AUTH_TYPE_ALL_ADDED_MD5 = 0;

	public static final short RADIUS_AUTH_TYPE_ALL = 1;

	public static final short RADIUS_AUTH_TYPE_PEAP = 2;

	public static final short RADIUS_AUTH_TYPE_TTLS = 3;

	public static final short RADIUS_AUTH_TYPE_TLS = 4;

	public static final short RADIUS_AUTH_TYPE_LEAP = 5;

	public static EnumItem[] RADIUS_AUTH_TYPE = MgrUtil.enumItems(
			"enum.radiusAuthType.", new int[] {RADIUS_AUTH_TYPE_ALL_ADDED_MD5, RADIUS_AUTH_TYPE_ALL,
					RADIUS_AUTH_TYPE_PEAP, RADIUS_AUTH_TYPE_TTLS,
					RADIUS_AUTH_TYPE_TLS, RADIUS_AUTH_TYPE_LEAP });

	private short authType = RADIUS_AUTH_TYPE_ALL_ADDED_MD5;
	
	public static final short RADIUS_AUTH_TYPE_DEFAULT_PEAP = 1;

	public static final short RADIUS_AUTH_TYPE_DEFAULT_TTLS = 2;

	public static final short RADIUS_AUTH_TYPE_DEFAULT_TLS = 3;

	public static final short RADIUS_AUTH_TYPE_DEFAULT_LEAP = 4;

	public static final short RADIUS_AUTH_TYPE_DEFAULT_MD5= 5;

	
	public static EnumItem[] RADIUS_AUTH_TYPE_DEFAULT_ALL_0 = MgrUtil.enumItems(
			"enum.radiusAuthType.default.", new int[] { RADIUS_AUTH_TYPE_DEFAULT_PEAP,
					RADIUS_AUTH_TYPE_DEFAULT_TTLS, RADIUS_AUTH_TYPE_DEFAULT_TLS,
					RADIUS_AUTH_TYPE_DEFAULT_LEAP, RADIUS_AUTH_TYPE_DEFAULT_MD5 });
	
	public static EnumItem[] RADIUS_AUTH_TYPE_DEFAULT_ALL_1 = MgrUtil.enumItems(
			"enum.radiusAuthType.default.", new int[] { RADIUS_AUTH_TYPE_DEFAULT_PEAP,
					RADIUS_AUTH_TYPE_DEFAULT_TTLS, RADIUS_AUTH_TYPE_DEFAULT_TLS,
					RADIUS_AUTH_TYPE_DEFAULT_LEAP});
	
	public static EnumItem[] RADIUS_AUTH_TYPE_DEFAULT_ALL_2 = MgrUtil.enumItems(
			"enum.radiusAuthType.default.", new int[] { RADIUS_AUTH_TYPE_DEFAULT_PEAP,RADIUS_AUTH_TYPE_DEFAULT_TLS});
	
	public static EnumItem[] RADIUS_AUTH_TYPE_DEFAULT_ALL_3 = MgrUtil.enumItems(
			"enum.radiusAuthType.default.", new int[] {RADIUS_AUTH_TYPE_DEFAULT_TLS,RADIUS_AUTH_TYPE_DEFAULT_TTLS});
	
	public static EnumItem[] RADIUS_AUTH_TYPE_DEFAULT_ALL_4 = MgrUtil.enumItems(
			"enum.radiusAuthType.default.", new int[] {RADIUS_AUTH_TYPE_DEFAULT_TLS});
	
	public static EnumItem[] RADIUS_AUTH_TYPE_DEFAULT_ALL_5 = MgrUtil.enumItems(
			"enum.radiusAuthType.default.", new int[] {RADIUS_AUTH_TYPE_DEFAULT_LEAP});
	
	private short authTypeDefault = RADIUS_AUTH_TYPE_DEFAULT_PEAP;

	public static final short RADIUS_SERVER_DBTYPE_NONE = 0;
	
	public static final short RADIUS_SERVER_DBTYPE_LOCAL = 1;

	public static final short RADIUS_SERVER_DBTYPE_ACTIVE = 2; // Active Directory

	public static final short RADIUS_SERVER_DBTYPE_OPEN = 3; // LDAP Server
	
	public static final short RADIUS_SERVER_DBTYPE_OPEN_DIRECT = 6; // Open Directory

	public static final short RADIUS_SERVER_DBTYPE_LOCAL_AND_ACTIVE = 4;

	public static final short RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN = 5;
	
	public static final short RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN_DIRECT = 7;

	private short databaseType = RADIUS_SERVER_DBTYPE_LOCAL;
	
	// external DB include: 2:AD 3:LDAP 6:OD
	@Transient
	private short externalDbType = RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE;

	// local user group for local dbtype
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "RADIUS_ON_HIVEAP_LOCAL_USER_GROUP", joinColumns = { @JoinColumn(name = "RADIUS_ON_HIVEAP_ID") }, inverseJoinColumns = { @JoinColumn(name = "LOCAL_USER_GROUP_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<LocalUserGroup> localUserGroup = new HashSet<LocalUserGroup>();

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "DIRECTORY_OPENLDAP_INFO", joinColumns = @JoinColumn(name = "DIRECTORY_OPENLDAP_ID"))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<ActiveDirectoryOrLdapInfo> directoryOrLdap = new ArrayList<ActiveDirectoryOrLdapInfo>();

	@Transient
	private List<ActiveDirectoryOrLdapInfo> directory = new ArrayList<ActiveDirectoryOrLdapInfo>();

	@Transient
	private List<ActiveDirectoryOrLdapInfo> ldap = new ArrayList<ActiveDirectoryOrLdapInfo>();
	
	@Transient
	private List<ActiveDirectoryOrLdapInfo> openDir = new ArrayList<ActiveDirectoryOrLdapInfo>();

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "RADIUS_HIVEAP_AUTH", joinColumns = @JoinColumn(name = "AUTH_ID"))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<RadiusHiveapAuth> ipOrNames = new ArrayList<RadiusHiveapAuth>();
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "RADIUS_HIVEAP_LDAP_USER_PROFILE", joinColumns = @JoinColumn(name = "LDAP_USER_PROFILE_ID"))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<LdapServerOuUserProfile> ldapOuUserProfiles = new ArrayList<LdapServerOuUserProfile>();

	@Transient
	private List<LdapServerOuUserProfile> directoryOu = new ArrayList<LdapServerOuUserProfile>();
	@Transient
	private List<LdapServerOuUserProfile> ldapOu = new ArrayList<LdapServerOuUserProfile>();
	@Transient
	private List<LdapServerOuUserProfile> openDirOu = new ArrayList<LdapServerOuUserProfile>();
	
	@Transient
	private TreeNode dirTreeInfos = new TreeNode();
	@Transient
	private TreeNode ldapTreeInfos = new TreeNode();
	@Transient
	private TreeNode openDirTreeInfos = new TreeNode();
	
	@Transient
	private Collection<String> expandDnsDir;
	@Transient
	private Collection<String> expandDnsLdap;
	@Transient
	private Collection<String> expandDnsOpenDir;
	
	@Transient
	private String userGroupForGlobalCatalog;
	@Transient
	private List<ActiveDirectoryDomain> domainsForTreeDir = new ArrayList<ActiveDirectoryDomain>();
	@Transient
	private List<ActiveDirectoryDomain> domainsForTreeLdap = new ArrayList<ActiveDirectoryDomain>();
	@Transient
	private List<ActiveDirectoryDomain> domainsForTreeOpenDir = new ArrayList<ActiveDirectoryDomain>();
	@Transient
	private int domainForTreeDir = -1;
	@Transient
	private int domainForTreeLdap = -1;
	@Transient
	private int domainForTreeOpenDir = -1;
	
	// for LDAP server
	private boolean useEdirect;

	private boolean accPolicy;
	
	// for Active Direct add from 3.4r3
	private boolean globalCatalog;
	
	/**
	 * For library sip add from 3.5r3
	 */
	private boolean librarySipCheck;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "LIBRARY_SIP_SERVER_ID", nullable = true)
	private IpAddress sipServer;
	
	@Range(min=1, max=65535)
	private int sipPort = 6001;
	
	private boolean loginEnable;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String loginUser;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String loginPwd;
	
	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String institutionId;
	
	@Column(length = 1)
	private String separator = "|";
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "LIBRARY_SIP_POLICY_ID", nullable = true)
	private RadiusLibrarySip sipPolicy;

	@Transient
	private String apHostName = "";

	@Transient
	private String apMac = "";
	
	@Transient
	boolean checkCA = true;

	public boolean isCheckCA() {
		return checkCA;
	}

	public void setCheckCA(boolean checkCA) {
		this.checkCA = checkCA;
	}

	public String getApHostName() {
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
	
	// library sip end

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
		return radiusName;
	}

	public boolean getServerEnable() {
		return serverEnable;
	}

	public void setServerEnable(boolean serverEnable) {
		this.serverEnable = serverEnable;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public boolean getCacheEnable() {
		return cacheEnable;
	}

	public void setCacheEnable(boolean cacheEnable) {
		this.cacheEnable = cacheEnable;
	}

	public int getCacheTime() {
		return cacheTime;
	}

	public void setCacheTime(int cacheTime) {
		this.cacheTime = cacheTime;
	}

	public String getCaCertFile() {
		return caCertFile;
	}

	public void setCaCertFile(String caCertFile) {
		this.caCertFile = caCertFile;
	}

	public String getKeyFile() {
		return keyFile;
	}

	public void setKeyFile(String keyFile) {
		this.keyFile = keyFile;
	}

	public String getKeyPassword() {
		return keyPassword;
	}

	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}

	public String getServerFile() {
		return serverFile;
	}

	public void setServerFile(String serverFile) {
		this.serverFile = serverFile;
	}

	public String getReauthTime() {
		return reauthTime;
	}

	public void setReauthTime(String reauthTime) {
		this.reauthTime = reauthTime;
	}

	public String getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(String userProfileId) {
		this.userProfileId = userProfileId;
	}

	public String getVlanId() {
		return vlanId;
	}

	public void setVlanId(String vlanId) {
		this.vlanId = vlanId;
	}

	public short getAuthType() {
		return authType;
	}

	public void setAuthType(short authType) {
		this.authType = authType;
	}

	public short getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(short databaseType) {
		this.databaseType = databaseType;
	}

	public List<RadiusHiveapAuth> getIpOrNames() {
		return ipOrNames;
	}

	public void setIpOrNames(List<RadiusHiveapAuth> ipOrNames) {
		this.ipOrNames = ipOrNames;
	}

	public boolean getMapEnable() {
		return mapEnable;
	}

	public void setMapEnable(boolean mapEnable) {
		this.mapEnable = mapEnable;
	}

	public String getRadiusName() {
		return radiusName;
	}

	public void setRadiusName(String radiusName) {
		this.radiusName = radiusName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ActiveDirectoryOrLdapInfo> getDirectory() {
		if (directory.size() == 0
				&& null != directoryOrLdap
				&& directoryOrLdap.size() > 0
				&& directoryOrLdap.get(0)
						.getDirectoryOrLdap().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY) {
			directory = directoryOrLdap;
		}
		return directory;
	}

	public void setDirectory(List<ActiveDirectoryOrLdapInfo> directory) {
		this.directory = directory;
	}

	public List<ActiveDirectoryOrLdapInfo> getLdap() {
		if (ldap.size() == 0
				&& null != directoryOrLdap
				&& directoryOrLdap.size() > 0
				&& directoryOrLdap.get(0)
						.getDirectoryOrLdap().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP) {
			ldap = directoryOrLdap;
		}
		return ldap;
	}

	public void setLdap(List<ActiveDirectoryOrLdapInfo> ldap) {
		this.ldap = ldap;
	}

	public int getLocalInterval() {
		return localInterval;
	}

	public void setLocalInterval(int localInterval) {
		this.localInterval = localInterval;
	}

	public int getRemoteInterval() {
		return remoteInterval;
	}

	public void setRemoteInterval(int remoteInterval) {
		this.remoteInterval = remoteInterval;
	}

	public int getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}

	public short getMapByGroupOrUser() {
		return mapByGroupOrUser;
	}

	public void setMapByGroupOrUser(short mapByGroupOrUser) {
		this.mapByGroupOrUser = mapByGroupOrUser;
	}

	public String getGroupAttribute() {
		return groupAttribute;
	}

	public void setGroupAttribute(String groupAttribute) {
		this.groupAttribute = groupAttribute;
	}

	public boolean isCnEnable() {
		return cnEnable;
	}

	public void setCnEnable(boolean cnEnable) {
		this.cnEnable = cnEnable;
	}

	public boolean isDbEnable() {
		return dbEnable;
	}

	public void setDbEnable(boolean dbEnable) {
		this.dbEnable = dbEnable;
	}

	public List<ActiveDirectoryOrLdapInfo> getDirectoryOrLdap() {
		return directoryOrLdap;
	}

	public void setDirectoryOrLdap(
			List<ActiveDirectoryOrLdapInfo> directoryOrLdap) {
		this.directoryOrLdap = directoryOrLdap;
	}

	public boolean isUseEdirect() {
		return useEdirect;
	}

	public void setUseEdirect(boolean useEdirect) {
		this.useEdirect = useEdirect;
	}

	public boolean isAccPolicy() {
		return accPolicy;
	}

	public void setAccPolicy(boolean accPolicy) {
		this.accPolicy = accPolicy;
	}

	public Set<LocalUserGroup> getLocalUserGroup() {
		return localUserGroup;
	}

	public void setLocalUserGroup(Set<LocalUserGroup> localUserGroup) {
		this.localUserGroup = localUserGroup;
	}

	@Transient
	private String radiusSettingsStyle = "none"; // by default;
	
	@Transient
	private String databaseAccessStyle = "none";
	
	@Transient
	private String nasSettingsStyle = "none";

	public String getRadiusSettingsStyle() {
		return radiusSettingsStyle;
	}

	public void setRadiusSettingsStyle(String radiusSettingsStyle) {
		this.radiusSettingsStyle = radiusSettingsStyle;
	}

	public String getDatabaseAccessStyle() {
		return databaseAccessStyle;
	}

	public void setDatabaseAccessStyle(String databaseAccessStyle) {
		this.databaseAccessStyle = databaseAccessStyle;
	}

	public String getNasSettingsStyle() {
		return nasSettingsStyle;
	}

	public void setNasSettingsStyle(String nasSettingsStyle) {
		this.nasSettingsStyle = nasSettingsStyle;
	}

	public boolean isTtlsCheckInDb() {
		return ttlsCheckInDb;
	}

	public void setTtlsCheckInDb(boolean ttlsCheckInDb) {
		this.ttlsCheckInDb = ttlsCheckInDb;
	}

	public boolean isPeapCheckInDb() {
		return peapCheckInDb;
	}

	public void setPeapCheckInDb(boolean peapCheckInDb) {
		this.peapCheckInDb = peapCheckInDb;
	}
	
	@Override
	public RadiusOnHiveap clone() {
		try {
			return (RadiusOnHiveap) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public boolean isGlobalCatalog()
	{
		return globalCatalog;
	}

	public void setGlobalCatalog(boolean globalCatalog)
	{
		this.globalCatalog = globalCatalog;
	}

	public List<ActiveDirectoryOrLdapInfo> getOpenDir()
	{
		if (openDir.size() == 0
			&& null != directoryOrLdap
			&& directoryOrLdap.size() > 0
			&& directoryOrLdap.get(0).getDirectoryOrLdap().getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY) {
			openDir = directoryOrLdap;
		}
		return openDir;
	}

	public void setOpenDir(List<ActiveDirectoryOrLdapInfo> openDir)
	{
		this.openDir = openDir;
	}

	public IpAddress getSipServer()
	{
		return sipServer;
	}

	public void setSipServer(IpAddress sipServer)
	{
		this.sipServer = sipServer;
	}

	public int getSipPort()
	{
		return sipPort;
	}

	public void setSipPort(int sipPort)
	{
		this.sipPort = sipPort;
	}
	
	public boolean isLoginEnable(){
		return this.loginEnable;
	}
	
	public void setLoginEnable(boolean loginEnable){
		this.loginEnable = loginEnable;
	}

	public String getLoginUser()
	{
		return loginUser;
	}

	public void setLoginUser(String loginUser)
	{
		this.loginUser = loginUser;
	}

	public String getLoginPwd()
	{
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd)
	{
		this.loginPwd = loginPwd;
	}

	public String getInstitutionId()
	{
		return institutionId;
	}

	public void setInstitutionId(String institutionId)
	{
		this.institutionId = institutionId;
	}

	public String getSeparator()
	{
		return separator;
	}

	public void setSeparator(String separator)
	{
		this.separator = separator;
	}

	public RadiusLibrarySip getSipPolicy()
	{
		return sipPolicy;
	}

	public void setSipPolicy(RadiusLibrarySip sipPolicy)
	{
		this.sipPolicy = sipPolicy;
	}

	public boolean isLibrarySipCheck()
	{
		return librarySipCheck;
	}

	public void setLibrarySipCheck(boolean librarySipCheck)
	{
		this.librarySipCheck = librarySipCheck;
	}

	public List<LdapServerOuUserProfile> getLdapOuUserProfiles() {
		return ldapOuUserProfiles;
	}

	public void setLdapOuUserProfiles(
			List<LdapServerOuUserProfile> ldapOuUserProfiles) {
		this.ldapOuUserProfiles = ldapOuUserProfiles;
	}

	public List<LdapServerOuUserProfile> getDirectoryOu() {
		if (directoryOu.size() == 0
				&& null != ldapOuUserProfiles
				&& ldapOuUserProfiles.size() > 0
				&& ldapOuUserProfiles.get(0).getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY) {
			directoryOu = ldapOuUserProfiles;
		}
		return directoryOu;
	}

	public void setDirectoryOu(List<LdapServerOuUserProfile> directoryOu) {
		this.directoryOu = directoryOu;
	}

	public List<LdapServerOuUserProfile> getLdapOu() {
		if (ldapOu.size() == 0
				&& null != ldapOuUserProfiles
				&& ldapOuUserProfiles.size() > 0
				&& ldapOuUserProfiles.get(0).getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP) {
			ldapOu = ldapOuUserProfiles;
		}
		return ldapOu;
	}

	public void setLdapOu(List<LdapServerOuUserProfile> ldapOu) {
		this.ldapOu = ldapOu;
	}

	public List<LdapServerOuUserProfile> getOpenDirOu() {
		if (openDirOu.size() == 0
				&& null != ldapOuUserProfiles
				&& ldapOuUserProfiles.size() > 0
				&& ldapOuUserProfiles.get(0).getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY) {
			openDirOu = ldapOuUserProfiles;
		}
		return openDirOu;
	}

	public void setOpenDirOu(List<LdapServerOuUserProfile> openOu) {
		this.openDirOu = openOu;
	}

	public TreeNode getDirTreeInfos() {
		return dirTreeInfos;
	}

	public void setDirTreeInfos(TreeNode dirTreeInfos) {
		this.dirTreeInfos = dirTreeInfos;
	}

	public TreeNode getLdapTreeInfos() {
		return ldapTreeInfos;
	}

	public void setLdapTreeInfos(TreeNode ldapTreeInfos) {
		this.ldapTreeInfos = ldapTreeInfos;
	}

	public TreeNode getOpenDirTreeInfos() {
		return openDirTreeInfos;
	}

	public void setOpenDirTreeInfos(TreeNode openDirTreeInfos) {
		this.openDirTreeInfos = openDirTreeInfos;
	}

	public Collection<String> getExpandDnsDir() {
		return expandDnsDir;
	}

	public void setExpandDnsDir(Collection<String> expandDnsDir) {
		this.expandDnsDir = expandDnsDir;
	}

	public Collection<String> getExpandDnsLdap() {
		return expandDnsLdap;
	}

	public void setExpandDnsLdap(Collection<String> expandDnsLdap) {
		this.expandDnsLdap = expandDnsLdap;
	}

	public Collection<String> getExpandDnsOpenDir() {
		return expandDnsOpenDir;
	}

	public void setExpandDnsOpenDir(Collection<String> expandDnsOpenDir) {
		this.expandDnsOpenDir = expandDnsOpenDir;
	}

	public String getUserGroupForGlobalCatalog() {
		return userGroupForGlobalCatalog;
	}

	public void setUserGroupForGlobalCatalog(
			String userGroupForGlobalCatalog) {
		this.userGroupForGlobalCatalog = userGroupForGlobalCatalog;
	}
	
	public List<ActiveDirectoryDomain> getDomainsForTreeDir() {
		return domainsForTreeDir;
	}

	public void setDomainsForTreeDir(List<ActiveDirectoryDomain> domainsForTreeDir) {
		this.domainsForTreeDir = domainsForTreeDir;
	}

	public List<ActiveDirectoryDomain> getDomainsForTreeLdap() {
		return domainsForTreeLdap;
	}

	public void setDomainsForTreeLdap(List<ActiveDirectoryDomain> domainsForTreeLdap) {
		this.domainsForTreeLdap = domainsForTreeLdap;
	}

	public List<ActiveDirectoryDomain> getDomainsForTreeOpenDir() {
		return domainsForTreeOpenDir;
	}

	public void setDomainsForTreeOpenDir(
			List<ActiveDirectoryDomain> domainsForTreeOpenDir) {
		this.domainsForTreeOpenDir = domainsForTreeOpenDir;
	}

	public int getDomainForTreeDir() {
		return domainForTreeDir;
	}

	public void setDomainForTreeDir(int domainForTreeDir) {
		this.domainForTreeDir = domainForTreeDir;
	}

	public int getDomainForTreeLdap() {
		return domainForTreeLdap;
	}

	public void setDomainForTreeLdap(int domainForTreeLdap) {
		this.domainForTreeLdap = domainForTreeLdap;
	}

	public int getDomainForTreeOpenDir() {
		return domainForTreeOpenDir;
	}

	public void setDomainForTreeOpenDir(int domainForTreeOpenDir) {
		this.domainForTreeOpenDir = domainForTreeOpenDir;
	}
	
	public short getExternalDbType() {
		return externalDbType;
	}

	public void setExternalDbType(short externalDbType) {
		this.externalDbType = externalDbType;
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
	
	// for Router AAA server, hide the NAS settings
	@Transient
	private boolean server4Router;

    public boolean isServer4Router() {
        return server4Router;
    }

    public void setServer4Router(boolean server4Router) {
        this.server4Router = server4Router;
    }

	public short getAuthTypeDefault() {
		return authTypeDefault;
	}

	public void setAuthTypeDefault(short authTypeDefault) {
		this.authTypeDefault = authTypeDefault;
	}

}