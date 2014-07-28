package com.ah.be.config.create.source.impl.baseImpl;

import java.io.IOException;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.AAAProfileInt.RADIUS_PRIORITY_TYPE;
import com.ah.be.config.create.source.SecurityObjectProfileInt;
import com.ah.be.config.create.source.SsidProfileInt.AuthMethodType;
import com.ah.be.config.create.source.SsidProfileInt.UserProfileDenyAction;
import com.ah.bo.hiveap.HiveAp;
import com.ah.xml.be.config.CwpMultiLanguageValue;
import com.ah.xml.be.config.SecurityWebServer.WebPage.MandatoryField;
import com.ah.xml.be.config.SecurityWebServer.WebPage.MandatoryField.OptionalField;

public class SecurityObjectBaseImpl implements SecurityObjectProfileInt {
	
	protected HiveAp hiveAp;
	
	public SecurityObjectBaseImpl(){}
	
	public HiveAp getHiveAp() {
		return hiveAp;
	}
	
	public void setHiveAp(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
	}
	
	@Override
	public boolean isSupportThisDevice() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSsidGuiKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSsidName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRadiusAssGuiName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRadiusAssName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSecurityObjectName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigDefaultUserProfile() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDefaultUserProfileId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigureWebServer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigExternalCwp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCwpGuiKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCwpName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigIndexFile() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getWebServerIndexFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigSsidWebPage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMandatoryFieldValue(MandatoryField mandatoryObj) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOptionalFieldValue(OptionalField optionalObj) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigCwpSuccessFile() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getWebServerSuccessFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigCwpFailureFile() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCwpFailureFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigureWebServerSsl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getWebServerKeyValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigureWebDirect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getWebDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnableInternalServers() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDhcpServerLeaseTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isDhcpServerBroadcast() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDhcpServerUnicast() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDhcpServerKeepSilent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigureCWP() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getUserProfileAllowedSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigUserProfileAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUserProfileAllowedName(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigUserProfileDeny() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigUserProfileAction(UserProfileDenyAction actionType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getBanValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEnableStrict() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigWalledGarden() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getWallGardenIpSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWallGardenHostSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getWallGardenAddress(short ipOrHost, int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getWallGardenAll(short ipOrHost, int i) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getWallGardenWeb(short ipOrHost, int i) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getWallGardenProtocolSize(short ipOrHost, int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWallGardenProtocolValue(short ipOrHost, int i, int j) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWallGardenPortSize(short ipOrHost, int i, int j) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWallGardenPortValue(short ipOrHost, int i, int j, int k) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigurePreauth() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtocolOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtocolWepOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtocolWepShared() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtocolWep104_8021x() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtocolWep40_8021x() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtocolWpaAes_8021x() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtocolWpaTkip_8021x() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtocolWpa2Aes_8021x() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtocolWpa2Tkip_8021x() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtocolWpaAuto_8021x() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtocolWpaAesPsk() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtocolWpaTkipPsk() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtocolWpa2AesPsk() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtocolWpa2TkipPsk() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtocolWpaAutoPsk() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSecurityPreauthEnable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigSsidAAARadius() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getAAARadiusRetryInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAAARadiusAcctInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isDynamicAuthExtensionEnable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigRadiusServer(RADIUS_PRIORITY_TYPE primaryType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getAAARadiusServerIpOrHost(RADIUS_PRIORITY_TYPE primaryType)
			throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigAAARadiusServerSharedSecretOld(
			RADIUS_PRIORITY_TYPE primaryType) throws CreateXMLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getAAARadiusServerSharedSecret(
			RADIUS_PRIORITY_TYPE primaryType) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getAAARadiusAuthPort(RADIUS_PRIORITY_TYPE primaryType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigAAARadiusAcctPort(RADIUS_PRIORITY_TYPE primaryType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getAAARadiusServerAcctPort(RADIUS_PRIORITY_TYPE primaryType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigAAARadiusServerSharedSecret(
			RADIUS_PRIORITY_TYPE primaryType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableVpnTunnel(String serverAddr)
			throws CreateXMLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigAcctRadiusServer(RADIUS_PRIORITY_TYPE primaryType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getAcctAAARadiusServerIpOrHost(
			RADIUS_PRIORITY_TYPE primaryType) throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigAcctAAARadiusServerSharedSecret(
			RADIUS_PRIORITY_TYPE primaryType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getAcctAAARadiusServerSharedSecret(
			RADIUS_PRIORITY_TYPE primaryType) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigAcctAAARadiusAcctPort(
			RADIUS_PRIORITY_TYPE primaryType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getAcctAAARadiusServerAcctPort(RADIUS_PRIORITY_TYPE primaryType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isDisableDefPsk() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigPrivatePsk() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableRadiusAuth() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AuthMethodType getPskAuthMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPskUserLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isPPSKMacBindingEnable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPPSKExternalServerEnable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getRoamingUpdateInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRoamingAgeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigLocalCacheTimeOut() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLocalCacheTimeOut() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getEapTimeOut() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getEapRetries() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isMacBasedAuthEnable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigFallbackToEcwp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableFallbackToEcwp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AuthMethodType getMacAuthType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigCwpRegUserProfile() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getCwpRegUserProfileAttr() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigCwpAuthUserProfile() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getCwpAuthUserProfileAttr() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCWPTimeOutValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigCWPAuthMethod() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AuthMethodType getCwpAuthMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnableCwpTimerDisplay() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableNewWindow() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getAlert() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigPasthrough() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getCwpExternalVlan() throws CreateXMLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigExCwpUrl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getExCwpUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEcwpDefault() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigExCwpPassBasic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigExCwpPassShared() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEcwpNnu() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableNoRoamingAtLogin() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEcwpDepaul() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableNoRadiusAuth() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableSuccessRegister() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getExCwpPassSharedValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCwpNoFailurePage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCwpNoSuccessPage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSuccessRedirect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSuccessRedirectExternal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSuccessRedirectExternalURL() throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSuccessDelay() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isSuccessRedirectOriginal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFailureRedirect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFailureRedirectExternal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getFailureRedirectExternalURL() throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFailureDelay() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isFailureRedirectLogin() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnablehttp302() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSsidProtocolWepSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigureProtocolWep(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigureProtocolAscii() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigureProtocolHex() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getProtocolWepValue(int i) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigureWepDefault(int i) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getProtocolReplayWindow() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEnableProtocolLocalTkip() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableProtocolRemoteTkip() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getProtocolKeyValue() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigureProtocolStrict() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigureProtocolNoStrict() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getProtocolGmkRekeyPeriod() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getProtocolRekeyPeriod() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getProtocolPtkTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getProtocolPtkRetry() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getProtocolGtkTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getProtocolGtkRetry() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPtkRekeyPeriod() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isRoamingProactivePmkidResponse() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableReauthInterval() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getReauthIntervalValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isCwpFromSsid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigServerName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigCertDN() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigCwpServer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCwpServerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnableProcessSipInfo() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getProcessSipInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserProfileSequence() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigDevicePolicy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDevicePolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigPpskServer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPpskServerIp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigPpskWebServer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPpskWebServerHttps() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getBindToPpskSsid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUseDefaultPpskPage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigPpskWebDir() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigPpskLoginPage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPpskWebDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPpskLoginPage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUseForPpskServer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigPpskAuthUser() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInjectOperatorNameEnable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled8021X() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableAaaUserProfileMapping() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getUserProfileMappingAttributeId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigUserProfileMappingVendorId() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getUserProfileMappingVendorId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEnableUsePolicy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnable80211w() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigmfpMandatory() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigmfpOptional() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CwpMultiLanguageValue getCWPLanguageValue() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public boolean isConfigAuthMethod() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigPortBased() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigHostBased() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigPortBasedFailedVlan() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getPortBasedFailedVlan() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigInitialAuthMethod() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMdmRootURLPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMdmApiURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMdmApiKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMdmHttpAuthUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMdmHttpAuthPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMdmOsObject(short i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnabled80211r() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isConfigMdmOsObject(short i) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigMultipleDomain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSupportMDM() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableBip() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigCwpAnonymousAccess() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigSelfRegViaIdm() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigSelfRegViaIdmApi(){
		String apiStr = getSelfRegViaIdmApi();
		return apiStr != null && !"".equals(apiStr) && !"null".equalsIgnoreCase(apiStr);
	}

	@Override
	public String getSelfRegViaIdmApi(){
	    // use 6.1r4 instead of 6.1r5 due to QA environment
	    if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.4.0") < 0) {
	        return hiveAp.getDownloadInfo().getIdmRadSecConfig().getOldIdmSelfRegDeviceGuestAPI();
	    } else {
	        return hiveAp.getDownloadInfo().getIdmRadSecConfig().getIdmSelfRegDeviceGuestAPI();
	    }
	}

	@Override
	public boolean isConfigRegViaIdmCrlFile(){
		String crlFileStr = getSelfRegViaIdmCrlFile();
		return crlFileStr != null && !"".equals(crlFileStr) && !"null".equalsIgnoreCase(crlFileStr);
	}

	@Override
	public String getSelfRegViaIdmCrlFile(){
		return hiveAp.getDownloadInfo().getIdmRadSecConfig().getIdmSelfRegDeviceCRL();
	}

	@Override
	public boolean isMdmTypeEnabled(int mdmType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMdmApiInstanceId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOnboardSsid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnableReportedDataByCWP() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableCWPAndSelfRegister() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnablePPSKSelfRegister() {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    public int getMdmGuestUserProfileId() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isEnableMdmDisconnectForVlanChange() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getMdmPollStatusInterval() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isEnableMdmSendMessageViaEmail() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEnableMdmSendMessageViaPush() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEnableMdmSendMessageViaSms() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getMdmSendMessageTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMdmSendMessageContent() {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public boolean isEnableMdmNonCompliance() {
		// TODO Auto-generated method stub
		return false;
	}
		
}
