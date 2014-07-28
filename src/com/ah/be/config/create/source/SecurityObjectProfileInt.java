package com.ah.be.config.create.source;

import java.io.IOException;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.SsidProfileInt.AuthMethodType;
import com.ah.be.config.create.source.SsidProfileInt.UserProfileDenyAction;
import com.ah.xml.be.config.CwpMultiLanguageValue;
import com.ah.xml.be.config.SecurityWebServer; 

/**
 * @author zhang
 * @version 2010-4-29 14:40:41
 */

public interface SecurityObjectProfileInt {
	
	public static short WALL_GARDEN_IPADDRESS = 0;
	public static short WALL_GARDEN_HOSTNAME = 1;

	public static short MDM_OSOBJECT_IOS = 0;
	public static short MDM_OSOBJECT_MACOS = 1;
	public static short MDM_OSOBJECT_SYMBIAN = 2;
	public static short MDM_OSOBJECT_BLACKBERRY = 3;
	public static short MDM_OSOBJECT_ANDROID = 4;
	public static short MDM_OSOBJECT_WINDOWSPHONE = 5;
	public static short MDM_OSOBJECT_CHROME = 6;
	
	public static final String MDM_OBJECT_IOS="iPod/iPhone/iPad";
	public static final String MDM_OBJECT_MACOS="MacOS";
	public static final String MDM_OBJECT_SYMBIAN="Symbian";
	public static final String MDM_OBJECT_BLACKBERRY="Blackberry";
	public static final String MDM_OBJECT_ANDROID="Android";
	public static final String MDM_OBJECT_WINDOWSPHONE="WindowsPhone";
	public static final String MDM_OBJECT_CHROME="Chrome";
	
	public static final String DEVICE_POLICY_SUFFIX = "_MDP";
	public static final int WPA2_80211W_MODE_MANDATORY = 1;
	public static final int WPA2_80211W_MODE_OPTIONAL = 2;
	
	public boolean isSupportThisDevice();
	
	public String getSsidGuiKey();
	
	public String getSsidName();
	
	public String getRadiusAssGuiName();
	
	public String getRadiusAssName();

	public String getSecurityObjectName();
	
	public boolean isConfigDefaultUserProfile();
	
	public int getDefaultUserProfileId();
	
	public boolean isConfigureWebServer();
	
	public boolean isConfigExternalCwp();
	
	public String getCwpGuiKey();
	
	public String getCwpName();
	
	public boolean isConfigIndexFile();
	
	public String getWebServerIndexFile();
	
	public boolean isConfigSsidWebPage();
	
	public int getMandatoryFieldValue(SecurityWebServer.WebPage.MandatoryField mandatoryObj);
	
	public int getOptionalFieldValue(SecurityWebServer.WebPage.MandatoryField.OptionalField optionalObj);
	
	public boolean isConfigCwpSuccessFile();
	
	public String getWebServerSuccessFile();
	
	public boolean isConfigCwpFailureFile();
	
	public String getCwpFailureFileName();
	
	public boolean isConfigureWebServerSsl();
	
	public int getWebServerKeyValue();
	
	public boolean isConfigureWebDirect();
	
	public String getWebDirectory();
	
	public boolean isEnableInternalServers();
	
	public int getDhcpServerLeaseTime();
	
	public boolean isDhcpServerBroadcast();
	
	public boolean isDhcpServerUnicast();
	
	public boolean isDhcpServerKeepSilent();
	
	public boolean isConfigureCWP();
	
	public int getUserProfileAllowedSize();
	
	public boolean isConfigUserProfileAllowed();
	
	public String getUserProfileAllowedName(int index);
	
	public boolean isConfigUserProfileDeny();
	
	public boolean isConfigUserProfileAction(UserProfileDenyAction actionType);
	
	public int getBanValue();
	
	public boolean isEnableStrict();
	
	public boolean isConfigWalledGarden();
	
	public int getWallGardenIpSize();
	
	public int getWallGardenHostSize();
	
	public String getWallGardenAddress(short ipOrHost, int i);
	
	public boolean getWallGardenAll(short ipOrHost, int i);
	
	public boolean getWallGardenWeb(short ipOrHost, int i);
	
	public int getWallGardenProtocolSize(short ipOrHost, int i);
	
	public int getWallGardenProtocolValue(short ipOrHost, int i, int j);
	
	public int getWallGardenPortSize(short ipOrHost, int i, int j);
	
	public int getWallGardenPortValue(short ipOrHost, int i, int j, int k);
	
	public boolean isConfigurePreauth();
	
	public boolean isProtocolOpen();
	
	public boolean isProtocolWepOpen();
	
	public boolean isProtocolWepShared();
	
	public boolean isProtocolWep104_8021x();
	
	public boolean isProtocolWep40_8021x();
	
	public boolean isProtocolWpaAes_8021x();
	
	public boolean isProtocolWpaTkip_8021x();
	
	public boolean isProtocolWpa2Aes_8021x();
	
	public boolean isProtocolWpa2Tkip_8021x();
	
	public boolean isProtocolWpaAuto_8021x();
	
	public boolean isProtocolWpaAesPsk();
	
	public boolean isProtocolWpaTkipPsk();
	
	public boolean isProtocolWpa2AesPsk();
	
	public boolean isProtocolWpa2TkipPsk();
	
	public boolean isProtocolWpaAutoPsk();
	
	public boolean isSecurityPreauthEnable();
	
	public boolean isConfigSsidAAARadius();
	
	public int getAAARadiusRetryInterval();
	
	public int getAAARadiusAcctInterval();
	
	public boolean isDynamicAuthExtensionEnable();
	
	public boolean isConfigRadiusServer(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType);
	
	public String getAAARadiusServerIpOrHost(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) throws CreateXMLException;
	
	public boolean isConfigAAARadiusServerSharedSecretOld(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) throws CreateXMLException;
	
	public String getAAARadiusServerSharedSecret(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) throws IOException;
	
	public int getAAARadiusAuthPort(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType);
	
	public boolean isConfigAAARadiusAcctPort(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType);
	
	public int getAAARadiusServerAcctPort(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType);
	
	public boolean isConfigAAARadiusServerSharedSecret(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType);
	
	public boolean isEnableVpnTunnel(String serverAddr) throws CreateXMLException;
	
	public boolean isConfigAcctRadiusServer(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType);
	
	public String getAcctAAARadiusServerIpOrHost(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType)throws CreateXMLException;
	
	public boolean isConfigAcctAAARadiusServerSharedSecret(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType);
	
	public String getAcctAAARadiusServerSharedSecret(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) throws IOException;
	
	public boolean isConfigAcctAAARadiusAcctPort(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType);
	
	public int getAcctAAARadiusServerAcctPort(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType);
	
	public boolean isDisableDefPsk();
	
	public boolean isConfigPrivatePsk();
	
	public boolean isEnableRadiusAuth();
	
	public AuthMethodType getPskAuthMethod();
	
	public int getPskUserLimit();
	
	public boolean isPPSKMacBindingEnable();
	
	public boolean isPPSKExternalServerEnable();
	
	public int getRoamingUpdateInterval();
	
	public int getRoamingAgeout();
	
	public boolean isConfigLocalCacheTimeOut();
	
	public int getLocalCacheTimeOut();
	
	public int getEapTimeOut();
	
	public int getEapRetries();
	
	public boolean isMacBasedAuthEnable();
	
	public boolean isConfigFallbackToEcwp();
	
	public boolean isEnableFallbackToEcwp();
	
	public AuthMethodType getMacAuthType();
	
	public boolean isConfigCwpRegUserProfile();
	
	public int getCwpRegUserProfileAttr();
	
	public boolean isConfigCwpAuthUserProfile();
	
	public int getCwpAuthUserProfileAttr();
	
	public int getCWPTimeOutValue();
	
	public boolean isConfigCWPAuthMethod();
	
	public AuthMethodType getCwpAuthMethod();
	
	public boolean isEnableCwpTimerDisplay();
	
	public boolean isEnableNewWindow();
	
	public int getAlert();
	
	public boolean isConfigPasthrough();
	
	public int getCwpExternalVlan() throws CreateXMLException;
	
	public boolean isConfigExCwpUrl();
	
	public String getExCwpUrl();
	
	public boolean isEcwpDefault();
	
	public boolean isConfigExCwpPassBasic();
	
	public boolean isConfigExCwpPassShared();
	
	public boolean isEcwpNnu();
	
	public boolean isEnableNoRoamingAtLogin();
	
	public boolean isEcwpDepaul();
	
	public boolean isEnableNoRadiusAuth();
	
	public boolean isEnableSuccessRegister();
	
	public String getExCwpPassSharedValue();
	
	public boolean isCwpNoFailurePage();
	
	public boolean isCwpNoSuccessPage();
	
	public boolean isSuccessRedirect();
	
	public boolean isSuccessRedirectExternal();
	
	public String getSuccessRedirectExternalURL() throws CreateXMLException;
	
	public int getSuccessDelay();
	
	public boolean isSuccessRedirectOriginal();
	
	public boolean isFailureRedirect();
	
	public boolean isFailureRedirectExternal();
	
	public String getFailureRedirectExternalURL() throws CreateXMLException;
	
	public int getFailureDelay();
	
	public boolean isFailureRedirectLogin();
	
	public boolean isEnablehttp302();
	
	public int getSsidProtocolWepSize();
	
	public boolean isConfigureProtocolWep(int index);
	
	public boolean isConfigureProtocolAscii();
	
	public boolean isConfigureProtocolHex();
	
	public String getProtocolWepValue(int i) throws IOException;
	
	public boolean isConfigureWepDefault(int i);
	
	public int getProtocolReplayWindow();
	
	public boolean isEnableProtocolLocalTkip();
	
	public boolean isEnableProtocolRemoteTkip();
	
	public String getProtocolKeyValue() throws IOException;
	
	public boolean isConfigureProtocolStrict();
	
	public boolean isConfigureProtocolNoStrict();
	
	public int getProtocolGmkRekeyPeriod();
	
	public int getProtocolRekeyPeriod();
	
	public int getProtocolPtkTimeout();
	
	public int getProtocolPtkRetry();
	
	public int getProtocolGtkTimeout();
	
	public int getProtocolGtkRetry();
	
	public int getPtkRekeyPeriod();
	
	public boolean isRoamingProactivePmkidResponse();
	
	public boolean isEnableReauthInterval();
	
	public int getReauthIntervalValue();
	
	public boolean isCwpFromSsid();
	
	public boolean isConfigServerName();
	
	public boolean isConfigCertDN();
	
	public boolean isConfigCwpServer();
	
	public String getCwpServerName();
	
	public boolean isEnableProcessSipInfo();
	
	public String getProcessSipInfo();
	
	public String getUserProfileSequence();
	
	public boolean isConfigDevicePolicy();
	
	public String getDevicePolicy();
	
	public boolean isConfigPpskServer();
	
	public String getPpskServerIp();
	
	public boolean isConfigPpskWebServer();
	
	public boolean isPpskWebServerHttps();
	
	public String getBindToPpskSsid();
	
	public boolean isUseDefaultPpskPage();
	
	public boolean isConfigPpskWebDir();
	
	public boolean isConfigPpskLoginPage();
	
//	public boolean isConfigPpskLoginScript();
	
	public String getPpskWebDir();
	
	public String getPpskLoginPage();
	
	public boolean isUseForPpskServer();
	
	public boolean isConfigPpskAuthUser();
	
	public boolean isInjectOperatorNameEnable();
	
	public boolean isEnabled8021X();
	
	public boolean isEnableAaaUserProfileMapping();
	
	public int getUserProfileMappingAttributeId();
	
	public boolean isConfigUserProfileMappingVendorId();
	
	public int getUserProfileMappingVendorId();
	
	public boolean isMdmTypeEnabled(int mdmType);
	
	public String getMdmRootURLPath();
	
	public String getMdmApiURL();
	
	public String getMdmApiKey();
	public String getMdmApiInstanceId();
	
	public String getMdmHttpAuthUser();
	
	public String getMdmHttpAuthPassword();
	
	public String getMdmOsObject(short i);
	
	public boolean isEnableUsePolicy();
	
	public boolean isConfigMdmOsObject(short i);
	
	public boolean isEnable80211w();
	
	public boolean isEnableBip();
	
	public boolean isConfigmfpMandatory();
	
	public boolean isConfigmfpOptional();
	
	public CwpMultiLanguageValue getCWPLanguageValue();
	
	public boolean isEnabled80211r();
	
	public boolean isConfigAuthMethod();
	
	public boolean isConfigPortBased();
	
	public boolean isConfigHostBased();
	
	public boolean isConfigPortBasedFailedVlan();
	
	public int getPortBasedFailedVlan();
	
	public boolean isConfigMultipleDomain();
	
	public boolean isConfigInitialAuthMethod();
	
	public boolean isSupportMDM();
	
	public boolean isConfigCwpAnonymousAccess();
	
	public boolean isConfigSelfRegViaIdm();
	
	public boolean isConfigSelfRegViaIdmApi();
	
	public String getSelfRegViaIdmApi();
	
	public boolean isConfigRegViaIdmCrlFile();
	
	public String getSelfRegViaIdmCrlFile();
	
	public String getOnboardSsid();	
	
	public boolean isEnableReportedDataByCWP();
	
	public boolean isEnableCWPAndSelfRegister();
	
	public boolean isEnablePPSKSelfRegister();
	
	///////////////for mdm airwatch
	
	public boolean isEnableMdmNonCompliance();
	
	public int getMdmGuestUserProfileId();
	
	public boolean isEnableMdmDisconnectForVlanChange();
	
	public int getMdmPollStatusInterval();
	
	public boolean isEnableMdmSendMessageViaEmail();
	
	public boolean isEnableMdmSendMessageViaPush();
	
    public boolean isEnableMdmSendMessageViaSms();
    
    public String getMdmSendMessageTitle();
    
    public String getMdmSendMessageContent();
	
}
