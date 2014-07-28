package com.ah.be.config.create.source;

import java.io.IOException;

import com.ah.be.config.create.CreateXMLException;
import com.ah.xml.be.config.LDAPAuthVerifyServerValue;
import com.ah.xml.be.config.LdapServerProtocolValue;
import com.ah.xml.be.config.MacCaseSensitivityValue;
import com.ah.xml.be.config.MacDelimiterValue;
import com.ah.xml.be.config.MacStyleValue;
import com.ah.xml.be.config.RadiusRealmFormatValue;

/**
 * 
 * @author zhang
 *
 */
public interface AAAProfileInt {
	
	public static enum RADIUS_PRIORITY_TYPE{
		primary, backup1, backup2, backup3
	}
	
	public enum STA_AUTH_TYPE{
		tls, peap, ttls, leap, md5
	}
	
	public String getRadiusGuiName();
	
	public String getRadiusName();
	
	public String getRadiusAssGuiName();
	
	public String getRadiusAssName();
	
	public String getMgmtServiceGuiName();
	
	public String getMgmtServiceName();
	
	public String getApVersion();
	
	public boolean isConfigureAAAProfile();
	
	public String getAAAUpdateTime();
	
	public String getLocalUpdateTime();
	
	public boolean isConfigureRetryInterval();
	
	public boolean isConfigurePriority(RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isConfigAcctPriority(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getServerIp(RADIUS_PRIORITY_TYPE priorityType) throws CreateXMLException;
	
	public String getAcctServerIp(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws CreateXMLException;
	
	public boolean isConfigSharedSecret(RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isConfigAcctSharedSecret(RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isConfigSharedSecretOld(RADIUS_PRIORITY_TYPE priorityType) throws CreateXMLException;
	
	public String getSharedSecret(RADIUS_PRIORITY_TYPE priorityType) throws IOException;
	
	public String getAcctSharedSecret(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws IOException;
	
	public int getAuthPort(RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isConfigureAcctPort(RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isConfigureAcctRadAcctPort(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public int getAcctPort(RADIUS_PRIORITY_TYPE priorityType);
	
	public int getAcctAcctPort(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public int getRetryInterval();
	
	public int getAccountInterimInterval();
	
	public boolean isConfigureLocal();
	
	public boolean isConfigRadiusServer();
	
	public boolean isRadiusServerEnable();
	
	public int getLocalPort();
	
	public boolean isConfigureSTAauthType(STA_AUTH_TYPE type);
	
	public boolean isConfigCaCert();
	
	public String getSTAauthCaCertFile();
	
	public String getSTAauthServerCertFile();
	
	public String getSTAauthPrivateKey();
	
	public boolean isConfigSTAauthPrivateKeyPassword();
	
	public String getSTAauthPrivateKeyPassword();
	
	public boolean isConfigureNAS();
	
	public int getNASSize();
	
	public String getNASIpAddress(int index) throws CreateXMLException;
	
	public boolean isConfigNASSharedKey(int index);
	
	public String getNASSharedKey(int index);
	
	public boolean isEnableNasTls(int index);
	
	public boolean isConfigureUser();
	
	public int getLocalUserGroupSize() throws CreateXMLException;
	
	public String getLocalUserGroupName(int index);
	
//	public boolean isLocalUserGroup(int index);
	
	public int getUserGroupReauthTime(int index);
	
	public boolean isConfigUserGroupReauthTime(int index);
	
	public boolean isConfigUserGroupProfileAttr(int index);
	
	public int getUserGroupProfileAttr(int index);
	
	public boolean isConfigUserGroupVlanId(int index);
	
	public int getUserGroupVlanId(int index);
	
	public int getLocalUserSize();
	
	public String getLocalUserName(int index);
	
	public String getLocalUserPassword(int index) throws IOException;
	
	public boolean isLocalUserBindUserGroup(int index);
	
	public String getLocalUserBindUserGroup(int index);
	
	public boolean isRadiusDBTypeLocal();
	
	public boolean isRadiusDBTypeActive();
	
	public boolean isRadiusDBTypeOpen();
	
	public boolean isConfigureLocalCache();
	
	public boolean isEnableLocalCache();
	
	public int getCacheLifeTime();
	
	public boolean isConfigureAttrMap();
	
	public boolean isConfigGroupAttrName();
	
	public boolean isConfigureReauthAttrName();
	
	public String getReauthAttrName();
	
	public boolean isConfigureUserProfileAttr();
	
	public String getGroupAttrName();
	
	public String getUserProfileAttrName();
	
	public boolean isConfigureVlanAttr();
	
	public String getVlanAttrName();
	
	public String getActiveDirectoryWorkgroup(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getOpenLdapServer(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws CreateXMLException;
	
	public String getActiveDirectoryRealm(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getOpenLdapBasedn(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getActiveServer(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws CreateXMLException;
	
	public String getOpenLdapPassword(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws IOException;
	
	public String getOpenLdapIdentity(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
//	public boolean isConfigActiveBasedn(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
//	
//	public String getActiveBasedn(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getActiveUser(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isEnableTls(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getActivePassword(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws IOException;
	
	public String getOpenLdapTlsCaCert(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isConfigureLdapTlsClientCert();
	
	public LDAPAuthVerifyServerValue getVerifyServerValue(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getOpenLdapTlsClientCert(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getOpenLdapTlsPrivateKey(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getOpenLdapTlsPrivateKeyPassword(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws IOException;
	
//	public boolean isConfigureAuthType();
	
	public int getOpenLdapPort(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public LdapServerProtocolValue getOpenLdapProtocol(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isConfigActiveComputerOu(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getActiveComputerOu(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isConfigActiveDirectory(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isConfigOpenLdap(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isConfigLdapAuth(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isEnableTlsCheckCertCn();
	
	public boolean isEnableTlsCheckInDb();
	
	public boolean isEnableTtlsCheckDb();
	
	public boolean isEnablePeapCheckDb();
	
	public boolean isConfigLocalCheckPeriod();
	
	public boolean isConfigRemoteCheckPeriod();
	
	public boolean isConfigRetryInterval();
	
	public int getLocalCheckPeriodValue();
	
	public int getRemoteCheckPeriodValue();
	
	public int getRetryIntervalValue();
	
//	public boolean isConfigAccountInterim();

	public boolean isConfigRadioAccountInterim();
	
	public boolean isConfigAAAMacFormat();
	
	public MacDelimiterValue getDelimiterType();
	
	public MacStyleValue getStyleType();
	
	public MacCaseSensitivityValue getCaseSensitivityType();
	
	public boolean isConfigLdapFilterAttr(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getLdapFilterAttr(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isEnableEdirServer();
	
	public boolean isEnablePolicyCheck();
	
	public boolean isConfigADLogin(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public int getADDomainSize(RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isEnableGlobalCatalog(RADIUS_PRIORITY_TYPE priorityType);
	
	public String getADDomainName(RADIUS_PRIORITY_TYPE priorityType, int index);
	
//	public boolean isConfigDomainBaseDn(RADIUS_PRIORITY_TYPE priorityType, int index);
//	
//	public String getADDomainBasedn(RADIUS_PRIORITY_TYPE priorityType, int index);
	
	public boolean isConfigDomainServer(RADIUS_PRIORITY_TYPE priorityType, int index);
	
	public String getADDomainServer(RADIUS_PRIORITY_TYPE priorityType, int index);
	
	public boolean isConfigDomainFullname(RADIUS_PRIORITY_TYPE priorityType, int index);
	
	public String getADDomainFullname(RADIUS_PRIORITY_TYPE priorityType, int index);
	
	public boolean isConfigADDomainDefault(RADIUS_PRIORITY_TYPE priorityType, int index);
	
	public String getADDomainBinddn(RADIUS_PRIORITY_TYPE priorityType, int index);
	
	public String getADDomainBinddnPassword(RADIUS_PRIORITY_TYPE priorityType, int index) throws IOException;
	
	public boolean isConfigDynamicAuth();
	
	public boolean isEnableDynamicAuth();
	
	public boolean isEnableVpnTunnel(String serverAddr) throws CreateXMLException;
	
	public boolean isEnableVpnTunnelAd(String serverAddr) throws CreateXMLException;
	
	public boolean isEnableVpnTunnelLdap(String serverAddr) throws CreateXMLException;
	
	public boolean isRadiusDBTypeOpenDirectory();
	
	public boolean isConfigOpenDirectory(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isConfigOpenDirectoryUser(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getOpenDirectoryUser(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getOpenDirectoryPassword(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isOpenDirectoryTlsEnable(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getOpenDirectoryDomain(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getOpenDirectoryFullName(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getOpenDirectoryBindn(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getOpenDirectoryBindnPass(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isConfigRadiusProxy();
	
	public int getProxyDeadTime();
	
	public int getProxyRetryDelay();
	
	public int getProxyRetryCount();
	
	public RadiusRealmFormatValue getProxyFormat();
	
	public int getProxyRealmSize();
	
	public boolean isConfigProxyRealm(int index);
	
	public String getProxyRealmName(int index);
	
	public boolean isConfigProxyRealmServerPrimary(int index);
	
	public boolean isConfigProxyRealmServerBackup(int index);
	
	public boolean isProxyRealmNoStrip(int index);
	
	public boolean isConfigProxyRealmNoStrip(int index);
	
	public String getProxyRealmServerPrimaryName(int index);
	
	public String getProxyRealmServerBackupName(int index);
	
	public int getRealmSize();
	
	public String getRealmName(int index);
	
	public int getRealmAcctPort(int index);
	
	public int getRealmAuthPort(int index);
	
	public String getRealmIp(int index) throws CreateXMLException;
	
	public String getRealmPass(int index);
	
	public boolean isEnableKeepalive();
	
	public int getKeepaliveInterval();
	
	public int getKeepaliveRetry();
	
	public int getKeepaliveRetryInterval();
	
	public boolean isConfigKeepaliveUsername();
	
	public String getKeepaliveUsername();
	
	public String getKeepalivePassword();
	
	public boolean isEnableLibrarySipPolicy();
	
	public String getLibrarySipService() throws CreateXMLException;
	
	public int getLibrarySipPort();
	
	public String getLibrarySipInstitutionId();
	
	public boolean isLibrarySipLoginEnable();
	
	public String getLibrarySipSeparator();
	
	public String getLibrarySipUserName();
	
	public String getLibrarySipPassword();
	
	public String getLibrarySipPolicyName();
	
	public int getPpskAutoSaveInt();
	
	public boolean isConfigPpskRadius();
	
	public boolean isConfigPpskPriority(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getPpskRadiusServerIpOrHost(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws CreateXMLException;
	
	public boolean isConfigPpskRadiusSecret(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public String getPpskRadiusSecret(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public int getPpskRadiusAuthPort(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
	public boolean isConfigAaaAttribute();
	
	public boolean isConfigNasIdentifier();
	
	public String getNasIdentifier();
	
	public boolean isConfigOperatorName();
	
	public String getOperatorName() throws CreateXMLException;
	
	public String getNamespaceId() throws CreateXMLException;
	
	public boolean isProxyOperatorNameEnable();
	
	public boolean isEnableStripFilter(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
//	public boolean isConfigProxyRadsec();
	
//	public boolean isEnableProxyRadsec();
	
//	public boolean isConfigRadsecTlsPort();
	
	public int getRadsecTlsPort();
	
	public boolean isProxyServerCloudAuth(int index);
	
	public int getRadsecRealmSize();
	
	public boolean isAuthRealmValid(int index);
	
	public String getRadsecRealmName(int index);
	
	public boolean isRadsecPrimaryRealm(int index);
	
	public boolean isRadsecBackupRealm(int index);
	
	public String getRadsecRealmPrimaryValue(int index);
	
	public String getRadsecRealmBackupValue(int index);
	
	public boolean isConfigSTAAuthDefaultType();
	
	public String getSTAAuthDefaultType();
	
	public String getSaslWrappingValue(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType);
	
//	public boolean isConfigRadsecDynamicAuthExtension();
	
//	public boolean isEnableIdmProxy();
}
