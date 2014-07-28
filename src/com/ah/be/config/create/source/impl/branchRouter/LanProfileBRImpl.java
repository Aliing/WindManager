package com.ah.be.config.create.source.impl.branchRouter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.AAAProfileInt;
import com.ah.be.config.create.source.SsidProfileInt;
import com.ah.be.config.create.source.SsidProfileInt.AuthMethodType;
import com.ah.be.config.create.source.SsidProfileInt.UserProfileDenyAction;
import com.ah.be.config.create.source.impl.SecurityObjectProfileImpl;
import com.ah.be.config.create.source.impl.baseImpl.SecurityObjectBaseImpl;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.lan.LanProfile;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.VpnService;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusServer;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.SsidProfile;
import com.ah.bo.wlan.WalledGardenItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.xml.be.config.CwpMultiLanguageValue;
import com.ah.xml.be.config.SecurityWebServer;

@Deprecated
public class LanProfileBRImpl extends SecurityObjectBaseImpl {
	
	private static final Tracer log = new Tracer(SecurityObjectProfileImpl.class
			.getSimpleName());
	
	private LanProfile lanProfile;
	private HiveAp hiveAp;
	
	private Cwp cwp;
	private RadiusAssignment radiusAssignment;
	
	private RadiusServer acctPrimaryRadius;
	private RadiusServer acctBackup1Radius;
	private RadiusServer acctBackup2Radius;
	private RadiusServer acctBackup3Radius;
	
	private RadiusServer primaryRadius;
	private RadiusServer backup1Radius;
	private RadiusServer backup2Radius;
	private RadiusServer backup3Radius;
	
	private UserProfile defUserProfile;
	private UserProfile regUserProfile;
	private UserProfile authUserProfile;
	private List<UserProfile> permittedUserProfiles;
	private final List<WalledGarden> IpWallGardenList = new ArrayList<WalledGarden>();
	private final List<WalledGarden> HostWallGardenList = new ArrayList<WalledGarden>();
	
	private boolean isCwpFLan = false;

	public LanProfileBRImpl(LanProfile lanProfile, HiveAp hiveAp) throws Exception{
		this.lanProfile = lanProfile;
		this.hiveAp = hiveAp;
		if(lanProfile != null){
			isCwpFLan = true;
		}
		
		if(lanProfile.isCwpSelectEnabled() && lanProfile.getCwp() != null){
			this.cwp = lanProfile.getCwp();
		}
//		else if(lanProfile.getUserPolicy() != null){
//			this.cwp = lanProfile.getUserPolicy();
//		}
		
		if(lanProfile != null){
			
			radiusAssignment = lanProfile.getRadiusAssignment();
			//load RadiusServer
			if(radiusAssignment != null && radiusAssignment.getServices() != null){
				for (RadiusServer radiusServerObj : radiusAssignment.getServices()) {
					if (radiusServerObj.getServerPriority() == RadiusServer.RADIUS_PRIORITY_PRIMARY) {
						if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_ACCT){
							acctPrimaryRadius = radiusServerObj;
						}else if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH){
							primaryRadius = radiusServerObj;
						}else if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH){
							acctPrimaryRadius = radiusServerObj;
							primaryRadius = radiusServerObj;
						}
					} else if (radiusServerObj.getServerPriority() == RadiusServer.RADIUS_PRIORITY_BACKUP1) {
						if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_ACCT){
							acctBackup1Radius = radiusServerObj;
						}else if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH){
							backup1Radius = radiusServerObj;
						}else if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH){
							acctBackup1Radius = radiusServerObj;
							backup1Radius = radiusServerObj;
						}
					} else if (radiusServerObj.getServerPriority() == RadiusServer.RADIUS_PRIORITY_BACKUP2) {
						if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_ACCT){
							acctBackup2Radius = radiusServerObj;
						}else if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH){
							backup2Radius = radiusServerObj;
						}else if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH){
							acctBackup2Radius = radiusServerObj;
							backup2Radius = radiusServerObj;
						}
					} else if (radiusServerObj.getServerPriority() == RadiusServer.RADIUS_PRIORITY_BACKUP3) {
						if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_ACCT){
							acctBackup3Radius = radiusServerObj;
						}else if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH){
							backup3Radius = radiusServerObj;
						}else if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH){
							acctBackup3Radius = radiusServerObj;
							backup3Radius = radiusServerObj;
						}
					}
				}
			}
			
			prepareUserProfile();
			prepareWalledGarden();
		}
		
	}
	
	private void prepareWalledGarden() throws CreateXMLException{
		//load wallgarden
		if(this.cwp != null && this.cwp.getWalledGarden() != null){
			
			//turn service web to protocol 6 port 80 and protocol 6 port 443
			List<WalledGardenItem> wallGardenList = new ArrayList<WalledGardenItem>();
			if(this.cwp != null && this.cwp.getWalledGarden() != null){
				for(WalledGardenItem wallObj : cwp.getWalledGarden()){
					if(wallObj.getService() == WalledGardenItem.SERVICE_WEB){
						WalledGardenItem webObj_1 = new WalledGardenItem();
						webObj_1.setPort(80);
						webObj_1.setProtocol(6);
						webObj_1.setServer(wallObj.getServer());
						webObj_1.setService(WalledGardenItem.SERVICE_PROTOCOL);
						wallGardenList.add(webObj_1);
						
						WalledGardenItem webObj_2 = new WalledGardenItem();
						webObj_2.setPort(443);
						webObj_2.setProtocol(6);
						webObj_2.setServer(wallObj.getServer());
						webObj_2.setService(WalledGardenItem.SERVICE_PROTOCOL);
						wallGardenList.add(webObj_2);
					}else{
						wallGardenList.add(wallObj);
					}
				}
			}
			
			for(WalledGardenItem wallObj : wallGardenList){
				
				List<WalledGarden> wallList;
				WalledGarden wallGardenObj = null;
				String address;
				
				//get address
				IpAddress ipAddr = wallObj.getServer();
				SingleTableItem ipItem = CLICommonFunc.getIpAddress(ipAddr, this.hiveAp);
				if(ipAddr.getTypeFlag() == IpAddress.TYPE_IP_NETWORK || ipAddr.getTypeFlag() == IpAddress.TYPE_IP_ADDRESS){
					if(ipItem.getNetmask() != null && !"".equals(ipItem.getNetmask()) && CLICommonFunc.isIpAddress(ipItem.getNetmask())){
						address = CLICommonFunc.countIpAndMask(ipItem.getIpAddress(), ipItem.getNetmask());
						address = address + "/" + CLICommonFunc.turnNetMaskToNum(ipItem.getNetmask());
					}else{
						address = ipItem.getIpAddress();
					}
					wallList = IpWallGardenList;
				}else{
					address = ipItem.getIpAddress();
					wallList = HostWallGardenList;
				}
				
				// set address
				for(WalledGarden gardenObj : wallList){
					if(address != null && address.equals(gardenObj.getAddress())){
						wallGardenObj = gardenObj;
						break;
					}
				}
				if(wallGardenObj == null){
					wallGardenObj = new WalledGarden();
					wallList.add(wallGardenObj);
				}
				wallGardenObj.setAddress(address);
				if(wallObj.getService() == WalledGardenItem.SERVICE_ALL){
					wallGardenObj.setAll(true);
				}
				if(wallObj.getService() == WalledGardenItem.SERVICE_WEB){
					wallGardenObj.setWeb(true);
				}
				
				//set protocol
				if(wallObj.getService() == WalledGardenItem.SERVICE_PROTOCOL){
					WalledGarden.Protocol protocolValue = null;
					for(WalledGarden.Protocol protocolObj : wallGardenObj.getProtocolList()){
						if(protocolObj.getProtocolValue() == wallObj.getProtocol()){
							protocolValue = protocolObj;
							break;
						}
					}
					if(protocolValue == null){
						protocolValue = wallGardenObj.newProtocol();
						wallGardenObj.getProtocolList().add(protocolValue);
					}
					protocolValue.setProtocolValue(wallObj.getProtocol());
					
					//set port
					boolean isFoundPort = false;
					for(Integer portValue : protocolValue.getPortList()){
						if(portValue == wallObj.getPort()){
							isFoundPort = true;
							break;
						}
					}
					if(!isFoundPort){
						protocolValue.getPortList().add(wallObj.getPort());
					}
				}
			}
			
			//clear protocol when contain "all" for one server address
			for(WalledGarden wallObj : IpWallGardenList){
				if(wallObj.isAll()){
					wallObj.getProtocolList().clear();
				}
			}
			for(WalledGarden wallObj : HostWallGardenList){
				if(wallObj.isAll()){
					wallObj.getProtocolList().clear();
				}
			}
		}
	}
	
	private void prepareUserProfile(){
		//cwp userprofile type, fix bug 26077.
		defUserProfile = lanProfile.getUserProfileDefault();
		if(cwp != null){
			short cwpType = cwp.getRegistrationType();
			switch(cwpType){
			case Cwp.REGISTRATION_TYPE_AUTHENTICATED:
				authUserProfile = defUserProfile;
				break;
			case Cwp.REGISTRATION_TYPE_REGISTERED:
				regUserProfile = lanProfile.getUserProfileSelfReg();
				defUserProfile = regUserProfile;
				break;
			case Cwp.REGISTRATION_TYPE_BOTH:
				regUserProfile = lanProfile.getUserProfileSelfReg();
				authUserProfile = defUserProfile;
				break;
			case Cwp.REGISTRATION_TYPE_EULA:
				regUserProfile = defUserProfile;
				break;
			case Cwp.REGISTRATION_TYPE_EXTERNAL:
				authUserProfile = defUserProfile;
				break;
			default:
				regUserProfile = lanProfile.getUserProfileSelfReg();
				break;
			}
		}
		
		//userprofile deny
		permittedUserProfiles = new ArrayList<UserProfile>();
		if(lanProfile.getUserProfileSelfReg() != null){
			permittedUserProfiles.add(lanProfile.getUserProfileSelfReg());
		}
		if(lanProfile.getUserProfileDefault() != null){
			permittedUserProfiles.add(lanProfile.getUserProfileDefault());
		}
		if(lanProfile.getRadiusUserProfile() != null && !lanProfile.getRadiusUserProfile().isEmpty()){
			permittedUserProfiles.addAll(lanProfile.getRadiusUserProfile());
		}
	}
	
	public boolean isSupportThisDevice(){
		return false;
	}
	
	public String getSsidGuiKey(){
		return MgrUtil.getUserMessage("config.upload.debug.lanProfiles");
	}
	
	public String getSsidName(){
		if(lanProfile != null){
			return lanProfile.getName();
		}else{
			return "";
		}
	}
	
	public String getRadiusAssGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.radiusServerAssignments");
	}
	
	public String getRadiusAssName(){
		return radiusAssignment.getRadiusName();
	}

	public String getSecurityObjectName(){
		return lanProfile.getName();
	}
	
	public boolean isConfigDefaultUserProfile(){
		return defUserProfile != null;
	}
	
	public int getDefaultUserProfileId() {
		return defUserProfile.getAttributeValue();
	}
	
	public boolean isConfigureWebServer() {
		return cwp != null;
	}
	
	public boolean isConfigExternalCwp(){
		return lanProfile.getCwp() != null && (lanProfile.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL || 
				lanProfile.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK);
	}
	
	public String getCwpGuiKey(){
		return MgrUtil.getUserMessage("config.upload.debug.cwpPageCustomization");
	}
	
	public String getCwpName(){
		if(cwp != null){
			return cwp.getCwpName();
		}else{
			return null;
		}
	}
	
	public boolean isConfigIndexFile(){
		String fileName = cwp.getWebPageName();
		return fileName != null && !"".equals(fileName) && !"None available".equals(fileName);
	}
	
	public String getWebServerIndexFile() {
		return cwp.getWebPageName();
	}
	
	public boolean isConfigSsidWebPage(){
		return (cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED && cwp.getWebPageSource() == Cwp.WEB_PAGE_SOURCE_AUTOGENERATE) || 
			(cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED && cwp.getWebPageSource() == Cwp.WEB_PAGE_SOURCE_IMPORT) ||
			(cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH ) ||
			cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA;
	}
	
	public int getMandatoryFieldValue(SecurityWebServer.WebPage.MandatoryField mandatoryObj) {
		if(cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA){
			return 0;
		}else{
			return cwp.getRequestField();
		}
	}
	
	public int getOptionalFieldValue(SecurityWebServer.WebPage.MandatoryField.OptionalField optionalObj) {
		if(cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA){
			return 0;
		}else{
			return cwp.getNumberField();
		}
	}
	
	public boolean isConfigCwpSuccessFile(){
		return cwp.isShowSuccessPage() && cwp.getResultPageName() != null && !"".equals(cwp.getResultPageName());
	}
	
	public String getWebServerSuccessFile() {
		return cwp.getResultPageName();
	}
	
	public boolean isConfigCwpFailureFile(){
		return cwp.isShowFailurePage() && cwp.getFailurePageName() != null && !"".equals(cwp.getFailurePageName());
	}
	
	public String getCwpFailureFileName(){
		return cwp.getFailurePageName();
	}
	
	public boolean isConfigureWebServerSsl() {
		return cwp.isEnabledHttps() && cwp.getCertificate() != null && cwp.getCertificate().getCertName() != null
				&& !cwp.getCertificate().getCertName().equals("");
	}
	
	public int getWebServerKeyValue() {
//		return Integer.valueOf(cwp.getCertificate().getCertName().replace(".pem", ""));
		return cwp.getCertificate().getIndex();
	}
	
	public boolean isConfigureWebDirect() {
		return cwp != null
				&& cwp.getDirectoryName() != null && !cwp.getDirectoryName()
						.equals("")
				&& cwp.isValidWebDirector();
	}
	
	public String getWebDirectory() {
		return cwp.getDirectoryName();
	}
	
	public boolean isEnableInternalServers(){
		return cwp != null && cwp.getServerType() == Cwp.CWP_INTERNAL;
	}
	
	public int getDhcpServerLeaseTime() {
		return cwp.getLeaseTime();
	}
	
	public boolean isDhcpServerBroadcast(){
		return cwp.getDhcpMode() == Cwp.MODE_BROADCAST;
	}
	
	public boolean isDhcpServerUnicast(){
		return cwp.getDhcpMode() == Cwp.MODE_UNICAST;
	}
	
	public boolean isDhcpServerKeepSilent(){
		return cwp.getDhcpMode() == Cwp.MODE_KEEPSILENT;
	}
	
	public boolean isConfigureCWP() {
		return cwp != null;
	}
	
	public int getUserProfileAllowedSize(){
		if(permittedUserProfiles == null){
			return 0;
		}else{
			return permittedUserProfiles.size();
		}
	}
	
	private boolean isConfigRadiusUserProfile(){
		if(lanProfile == null){
			return false;
		}
		boolean blnCwp = this.isConfigureCWP() && this.cwp.getRegistrationType() != Cwp.REGISTRATION_TYPE_REGISTERED && 
			this.cwp.getRegistrationType() != Cwp.REGISTRATION_TYPE_EULA;
		boolean bln8021x = lanProfile.isEnabled8021X();
		
		return blnCwp || bln8021x;
	}
	
	public boolean isConfigUserProfileAllowed(){
		if(this.isConfigRadiusUserProfile()){
			return lanProfile.isChkUserOnly() && this.getUserProfileAllowedSize() > 0;
		}else{
			return false;
		}
	}
	
	public String getUserProfileAllowedName(int index){
		return permittedUserProfiles.get(index).getUserProfileName();
	}
	
	public boolean isConfigUserProfileDeny(){
		if(this.isConfigRadiusUserProfile()){
			return lanProfile.isChkUserOnly();
		}else{
			return false;
		}
	}
	
	public boolean isConfigUserProfileAction(UserProfileDenyAction actionType){
		if(actionType == UserProfileDenyAction.ban){
			return lanProfile.getDenyAction() == LanProfile.DENY_ACTION_BAN;
		}else if(actionType == UserProfileDenyAction.banForever){
			return lanProfile.getDenyAction() == LanProfile.DENY_ACTION_BAN_FOREVER;
		}else if(actionType == UserProfileDenyAction.disconnect){
			return lanProfile.getDenyAction() == LanProfile.DENY_ACTION_DISCONNECT;
		}else{
			return false;
		}
	}
	
	public int getBanValue(){
		return (int)lanProfile.getActionTime();
	}
	
	public boolean isEnableStrict(){
		return lanProfile.isChkDeauthenticate();
	}
	
	public boolean isConfigWalledGarden(){
		return !IpWallGardenList.isEmpty() || !HostWallGardenList.isEmpty();
	}
	
	public int getWallGardenIpSize(){
		return IpWallGardenList.size();
	}
	
	public int getWallGardenHostSize(){
		return HostWallGardenList.size();
	}
	
	public String getWallGardenAddress(short ipOrHost, int i){
		if(ipOrHost == SsidProfileInt.WALL_GARDEN_IPADDRESS){
			return IpWallGardenList.get(i).getAddress();
		}else{
			return HostWallGardenList.get(i).getAddress();
		}
	}
	
	public boolean getWallGardenAll(short ipOrHost, int i){
		if(ipOrHost == SsidProfileInt.WALL_GARDEN_IPADDRESS){
			return IpWallGardenList.get(i).isAll();
		}else{
			return HostWallGardenList.get(i).isAll();
		}
	}
	
	public boolean getWallGardenWeb(short ipOrHost, int i){
		if(ipOrHost == SsidProfileInt.WALL_GARDEN_IPADDRESS){
			return IpWallGardenList.get(i).isWeb();
		}else{
			return HostWallGardenList.get(i).isWeb();
		}
	}
	
	public int getWallGardenProtocolSize(short ipOrHost, int i){
		if(ipOrHost == SsidProfileInt.WALL_GARDEN_IPADDRESS){
			return IpWallGardenList.get(i).getProtocolList().size();
		}else{
			return HostWallGardenList.get(i).getProtocolList().size();
		}
	}
	
	public int getWallGardenProtocolValue(short ipOrHost, int i, int j){
		if(ipOrHost == SsidProfileInt.WALL_GARDEN_IPADDRESS){
			return IpWallGardenList.get(i).getProtocolList().get(j).getProtocolValue();
		}else{
			return HostWallGardenList.get(i).getProtocolList().get(j).getProtocolValue();
		}
	}
	
	public int getWallGardenPortSize(short ipOrHost, int i, int j){
		if(ipOrHost == SsidProfileInt.WALL_GARDEN_IPADDRESS){
			return IpWallGardenList.get(i).getProtocolList().get(j).getPortList().size();
		}else{
			return HostWallGardenList.get(i).getProtocolList().get(j).getPortList().size();
		}
	}
	
	public int getWallGardenPortValue(short ipOrHost, int i, int j, int k){
		if(ipOrHost == SsidProfileInt.WALL_GARDEN_IPADDRESS){
			return IpWallGardenList.get(i).getProtocolList().get(j).getPortList().get(k);
		}else{
			return HostWallGardenList.get(i).getProtocolList().get(j).getPortList().get(k);
		}
	}
	
	public boolean isConfigurePreauth() {
		if(lanProfile == null){
			return false;
		}
		return !lanProfile.isEnabled8021Q();
//		return this.isProtocolWpaAuto_8021x() ||
//			this.isProtocolWpa2Tkip_8021x() ||
//			this.isProtocolWpa2Aes_8021x();
	}
	
	public boolean isProtocolOpen(){
		return !lanProfile.isEnabled8021X();
	}
	
	public boolean isProtocolWepOpen(){
		return false;
	}
	public boolean isProtocolWepShared(){
		return false;
	}
	public boolean isProtocolWep104_8021x(){
		return false;
	}
	public boolean isProtocolWep40_8021x(){
		return false;
	}
	
	public boolean isProtocolWpaAes_8021x(){
//		return lanProfile.getAccessMode() == lanProfile.ACCESS_MODE_8021X &&
//		lanProfile.getMgmtKey() == lanProfile.KEY_MGMT_WPA_EAP_802_1_X &&
//		lanProfile.getEncryption() == lanProfile.KEY_ENC_CCMP;
		return false;
	}
	public boolean isProtocolWpaTkip_8021x(){
//		return lanProfile.getAccessMode() == lanProfile.ACCESS_MODE_8021X &&
//		lanProfile.getMgmtKey() == lanProfile.KEY_MGMT_WPA_EAP_802_1_X &&
//		lanProfile.getEncryption() == lanProfile.KEY_ENC_TKIP;
		return false;
	}
	public boolean isProtocolWpa2Aes_8021x(){
//		return lanProfile.getAccessMode() == lanProfile.ACCESS_MODE_8021X &&
//		lanProfile.getMgmtKey() == lanProfile.KEY_MGMT_WPA2_EAP_802_1_X &&
//		lanProfile.getEncryption() == lanProfile.KEY_ENC_CCMP;
		return false;
	}
	public boolean isProtocolWpa2Tkip_8021x(){
//		return lanProfile.getAccessMode() == lanProfile.ACCESS_MODE_8021X &&
//		lanProfile.getMgmtKey() == lanProfile.KEY_MGMT_WPA2_EAP_802_1_X &&
//		lanProfile.getEncryption() == lanProfile.KEY_ENC_TKIP;
		return false;
	}
	public boolean isProtocolWpaAuto_8021x(){
//		return lanProfile.getAccessMode() == lanProfile.ACCESS_MODE_8021X &&
//		lanProfile.getMgmtKey() == lanProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X &&
//		lanProfile.getEncryption() == lanProfile.KEY_ENC_AUTO_TKIP_OR_CCMP;
		return false;
	}
	
	public boolean isProtocolWpaAesPsk(){
		return false;
	}
	public boolean isProtocolWpaTkipPsk(){
		return false;
	}
	public boolean isProtocolWpa2AesPsk(){
		return false;
	}
	public boolean isProtocolWpa2TkipPsk(){
		return false;
	}
	public boolean isProtocolWpaAutoPsk(){
		return false;
	}
	
	public boolean isSecurityPreauthEnable() {
		return false;
	}
	
	public boolean isConfigSsidAAARadius() {
		return radiusAssignment != null && lanProfile.isRadiusAuthEnable();
	}
	
	public int getAAARadiusRetryInterval() {
		return radiusAssignment.getRetryInterval();
	}
	
	public int getAAARadiusAcctInterval() {
		int interval = radiusAssignment.getUpdateInterval();
		String apVersion = hiveAp.getSoftVer();
		int iVersion = 0;
		if(apVersion != null){
			iVersion = Integer.valueOf(apVersion.replace(".", ""));
		}
		if(iVersion < 3020 && interval < 600){
			return 600;
		}else{
			return interval;
		}
	}
	
	public boolean isDynamicAuthExtensionEnable(){
		return radiusAssignment.getEnableExtensionRadius();
	}
	
	public boolean isConfigRadiusServer(
			AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) {
		if (AAAProfileInt.RADIUS_PRIORITY_TYPE.primary == primaryType) {
			return primaryRadius != null && (primaryRadius.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH || primaryRadius.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH);
		} else if (AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1 == primaryType) {
			return backup1Radius != null && (backup1Radius.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH || backup1Radius.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH);
		} else if (AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2 == primaryType) {
			return backup2Radius != null && (backup2Radius.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH || backup2Radius.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH);
		} else {
			return backup3Radius != null && (backup3Radius.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH || backup3Radius.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH);
		}
	}
	
	private RadiusServer getRadiusServerByType(
			AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) {
		if (primaryType == AAAProfileInt.RADIUS_PRIORITY_TYPE.primary) {
			return primaryRadius;
		} else if (primaryType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1) {
			return backup1Radius;
		} else if (primaryType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2) {
			return backup2Radius;
		} else {
			return backup3Radius;
		}
	}
	
	public String getAAARadiusServerIpOrHost(
			AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType)
			throws CreateXMLException {
		RadiusServer radiusServerObj = getRadiusServerByType(primaryType);
		if(radiusServerObj.isUseSelfAsServer()){
			return hiveAp.getCfgIpAddress();
		}else{
			return CLICommonFunc.getIpAddress(radiusServerObj.getIpAddress(),
					hiveAp).getIpAddress();
		}
	}
	
	public boolean isConfigAAARadiusServerSharedSecretOld(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) throws CreateXMLException{
		String key = getRadiusServerByType(primaryType).getSharedSecret();
		if(NmsUtil.compareSoftwareVersion("3.4.0.0", hiveAp.getSoftVer()) > 0 && 
				(key == null || "".equals(key))
			){
			String errMsg = NmsUtil.getUserMessage("error.be.config.create.emptyRadiusClientPassword");
			log.error("lanProfileImpl", errMsg);
			throw new CreateXMLException(errMsg);
		}
		return key != null && !"".equals(key);
	}
	
	public String getAAARadiusServerSharedSecret(
			AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) throws IOException {
		return getRadiusServerByType(primaryType).getSharedSecret();
	}
	
	public int getAAARadiusAuthPort(
			AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) {
		return getRadiusServerByType(primaryType).getAuthPort();
	}
	
	public boolean isConfigAAARadiusAcctPort(
			AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) {
		RadiusServer serverObj = getRadiusServerByType(primaryType);
		return serverObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_ACCT || 
		serverObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH;
	}
	
	public int getAAARadiusServerAcctPort(
			AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) {
		return getRadiusServerByType(primaryType).getAcctPort();
	}
	
	public boolean isConfigAAARadiusServerSharedSecret(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType){
		String key = getRadiusServerByType(primaryType).getSharedSecret();
		return key != null && !"".equals(key);
	}
	
	public boolean isEnableVpnTunnel(String serverAddr) throws CreateXMLException{
		if(hiveAp.getVpnMark() == HiveAp.VPN_MARK_CLIENT){
			VpnService vpnClient = hiveAp.getConfigTemplate().getVpnService();
			if(vpnClient == null){
				return false;
			}else{
				if(vpnClient.isRadiusThroughTunnel()){
					if(!CLICommonFunc.isIpAddress(serverAddr)){
						String errMsg = NmsUtil.getUserMessage("error.be.config.create.VPNPermitIp", new String[]{"Radius"});
						log.error("lanProfileImpl.isEnableVpnTunnel", errMsg);
						throw new CreateXMLException(errMsg);
					}
					return true;
				}else{
					return false;
				}
			}
		}else{
			return false;
		}
	}
	
	public boolean isConfigAcctRadiusServer(
			AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) {
		if (AAAProfileInt.RADIUS_PRIORITY_TYPE.primary == primaryType) {
			return acctPrimaryRadius != null && acctPrimaryRadius.getServerType() != RadiusServer.RADIUS_SERVER_TYPE_AUTH;
		} else if (AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1 == primaryType) {
			return acctBackup1Radius != null && acctBackup1Radius.getServerType() != RadiusServer.RADIUS_SERVER_TYPE_AUTH;
		} else if (AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2 == primaryType) {
			return acctBackup2Radius != null && acctBackup2Radius.getServerType() != RadiusServer.RADIUS_SERVER_TYPE_AUTH;
		} else {
			return acctBackup3Radius != null && acctBackup3Radius.getServerType() != RadiusServer.RADIUS_SERVER_TYPE_AUTH;
		}
	}
	
	private RadiusServer getAcctRadiusServerByType(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.primary) {
			return this.acctPrimaryRadius;
		} else if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1) {
			return this.acctBackup1Radius;
		} else if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2) {
			return this.acctBackup2Radius;
		} else if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3) {
			return this.acctBackup3Radius;
		} else {
			return null;
		}
	}
	
	public String getAcctAAARadiusServerIpOrHost(
			AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType)
			throws CreateXMLException {
		RadiusServer radiusServerObj = getAcctRadiusServerByType(primaryType);
		if(radiusServerObj.isUseSelfAsServer()){
			return hiveAp.getCfgIpAddress();
		}else{
			return CLICommonFunc.getIpAddress(radiusServerObj.getIpAddress(),
					hiveAp).getIpAddress();
		}
	}
	
	public boolean isConfigAcctAAARadiusServerSharedSecret(AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType){
		String key = getAcctRadiusServerByType(primaryType).getSharedSecret();
		return key != null && !"".equals(key);
	}
	
	public String getAcctAAARadiusServerSharedSecret(
			AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) throws IOException {
		return getAcctRadiusServerByType(primaryType).getSharedSecret();
	}
	
	public boolean isConfigAcctAAARadiusAcctPort(
			AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) {
		RadiusServer serverObj = getAcctRadiusServerByType(primaryType);
		return serverObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_ACCT || 
		serverObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH;
	}
	
	public int getAcctAAARadiusServerAcctPort(
			AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) {
		return getAcctRadiusServerByType(primaryType).getAcctPort();
	}
	
	public boolean isDisableDefPsk(){
		return false;
	}
	
	public boolean isConfigPrivatePsk(){
		return false;
	}
	
	public boolean isEnableRadiusAuth(){
		return false;
	}
	
	public AuthMethodType getPskAuthMethod(){
		return null;
	}
	
	public int getPskUserLimit(){
		return -1;
	}
	
	public boolean isPPSKMacBindingEnable(){
		return false;
	}
	
	public boolean isPPSKExternalServerEnable(){
		return false;
	}
	
	public int getRoamingUpdateInterval(){
		return -1;
	}
	
	public int getRoamingAgeout(){
		return -1;
	}
	
	public boolean isConfigLocalCacheTimeOut(){
		return false;
	}
	
	public int getLocalCacheTimeOut(){
		return -1;
	}
	
	public int getEapTimeOut(){
		return -1;
	}
	
	public int getEapRetries(){
		return -1;
	}
	
	public boolean isMacBasedAuthEnable() {
		return lanProfile.isMacAuthEnabled();
	}
	
	public boolean isConfigFallbackToEcwp(){
		return hiveAp.getDownloadInfo().isEcwpDepaul() || hiveAp.getDownloadInfo().isEcwpNnu();
	}
	
	public boolean isEnableFallbackToEcwp(){
		return false;
	}
	
	public AuthMethodType getMacAuthType(){
		if(!lanProfile.isMacAuthEnabled()){
			return null;
		}else{
			if(lanProfile.getAuthProtocol() == Cwp.AUTH_METHOD_MSCHAPV2){
				return AuthMethodType.msChapV2;
			}else if(lanProfile.getAuthProtocol() == Cwp.AUTH_METHOD_CHAP){
				return AuthMethodType.chap;
			}else{
				return AuthMethodType.pap;
			}
		}
	}
	
	public boolean isConfigCwpRegUserProfile() {
		return regUserProfile != null && cwp.getRegistrationType() != Cwp.REGISTRATION_TYPE_EXTERNAL;
	}
	
	public int getCwpRegUserProfileAttr() {
		return regUserProfile.getAttributeValue();
	}
	
	public boolean isConfigCwpAuthUserProfile() {
		return authUserProfile != null;
	}
	
	public int getCwpAuthUserProfileAttr() {
		return authUserProfile.getAttributeValue();
	}
	
	public int getCWPTimeOutValue() {
		return cwp.getRegistrationPeriod();
	}
	
	public boolean isConfigCWPAuthMethod(){
		return cwp != null && cwp.getRegistrationType() != Cwp.REGISTRATION_TYPE_REGISTERED && 
			cwp.getRegistrationType() != Cwp.REGISTRATION_TYPE_EULA && !cwp.isPpskServer();
	}
	
	public AuthMethodType getCwpAuthMethod(){
		if(cwp.getAuthMethod() == Cwp.AUTH_METHOD_CHAP){
			return AuthMethodType.chap;
		}else if(cwp.getAuthMethod() == Cwp.AUTH_METHOD_MSCHAPV2){
			return AuthMethodType.msChapV2;
		}else {
			return AuthMethodType.pap;
		}
	}
	
	public boolean isEnableCwpTimerDisplay(){
		return cwp.isEnabledPopup();
	}
	
	public boolean isEnableNewWindow(){
		return cwp.isEnabledNewWin();
	}
	
	public int getAlert(){
		return cwp.getSessionAlert();
	}
	
	public boolean isConfigPasthrough(){
		return cwp.isOverrideVlan() && (cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL || 
			cwp.getServerType() == Cwp.CWP_EXTERNAL);
	}
	
	public int getCwpExternalVlan() throws CreateXMLException{
		return CLICommonFunc.getVlan(cwp.getVlan(), this.hiveAp).getVlanId();
	}
	
	public boolean isConfigExCwpUrl(){
		return cwp.getLoginURL() != null && !"".equals(cwp.getLoginURL());
	}
	
	public String getExCwpUrl(){
		return cwp.getLoginURL();
	}
	
	public boolean isEcwpDefault(){
		return hiveAp.getDownloadInfo().isEcwpDefault();
	}
	
	public boolean isConfigExCwpPassBasic(){
		return cwp.getPasswordEncryption() == Cwp.PASSWORD_ENCRYPTION_BASIC;
	}
	
	public boolean isConfigExCwpPassShared(){
		String password = getExCwpPassSharedValue();
		return cwp.getPasswordEncryption() == Cwp.PASSWORD_ENCRYPTION_SHARED && 
				password != null && !"".equals(password);
	}
	
	public boolean isEcwpNnu(){
		return hiveAp.getDownloadInfo().isEcwpNnu();
	}
	
	public boolean isEnableNoRoamingAtLogin(){
		return true;
	}
	
	public boolean isEcwpDepaul(){
		return hiveAp.getDownloadInfo().isEcwpDepaul();
	}
	
	public boolean isEnableNoRadiusAuth(){
		return true;
	}
	
	public boolean isEnableSuccessRegister(){
		return !cwp.isNeedReassociate();
	}
	
	public String getExCwpPassSharedValue(){
		return cwp.getSharedSecret();
	}
	
	public boolean isCwpNoFailurePage(){
		return !cwp.isShowFailurePage();
	}
	
	public boolean isCwpNoSuccessPage(){
		return !cwp.isShowSuccessPage();
	}
	
	public boolean isSuccessRedirect(){
//		return cwp.isShowSuccessPage() && (cwp.getSuccessRedirection() == Cwp.SUCCESS_REDIRECT_ORIGINAL || cwp.getSuccessRedirection() == Cwp.SUCCESS_REDIRECT_EXTERNAL);
		return cwp.getSuccessRedirection() == Cwp.SUCCESS_REDIRECT_ORIGINAL || cwp.getSuccessRedirection() == Cwp.SUCCESS_REDIRECT_EXTERNAL;
	}
	
	public boolean isSuccessRedirectExternal(){
		return cwp.getSuccessRedirection() == Cwp.SUCCESS_REDIRECT_EXTERNAL;
	}
	
	public String getSuccessRedirectExternalURL() throws CreateXMLException{
		if(cwp.getExternalURLSuccessType() == Cwp.EXTERNAL_URL_SINGLE){
			return cwp.getSuccessExternalURL();
		}else{
			return CLICommonFunc.getIpAddress(cwp.getIpAddressSuccess(), this.hiveAp).getIpAddress();
		}
	}
	
	public int getSuccessDelay(){
		return cwp.getSuccessDelay();
	}
	
	public boolean isSuccessRedirectOriginal(){
		return cwp.getSuccessRedirection() == Cwp.SUCCESS_REDIRECT_ORIGINAL;
	}
	
	public boolean isFailureRedirect(){
//		return cwp.isShowFailurePage() && (cwp.getFailureRedirection() == Cwp.FAILURE_REDIRECT_LOGIN || cwp.getFailureRedirection() == Cwp.FAILURE_REDIRECT_EXTERNAL);
		return cwp.getFailureRedirection() == Cwp.FAILURE_REDIRECT_LOGIN || cwp.getFailureRedirection() == Cwp.FAILURE_REDIRECT_EXTERNAL;
	}
	
	public boolean isFailureRedirectExternal(){
		return cwp.getFailureRedirection() == Cwp.FAILURE_REDIRECT_EXTERNAL;
	}
	
	public String getFailureRedirectExternalURL() throws CreateXMLException{
		if(cwp.getExternalURLFailureType() == Cwp.EXTERNAL_URL_SINGLE){
			return cwp.getFailureExternalURL();
		}else{
			return CLICommonFunc.getIpAddress(cwp.getIpAddressFailure(), this.hiveAp).getIpAddress();
		}
	}
	
	public int getFailureDelay(){
		return cwp.getFailureDelay();
	}
	
	public boolean isFailureRedirectLogin(){
		return cwp.getFailureRedirection() == Cwp.FAILURE_REDIRECT_LOGIN;
	}
	
	public boolean isEnablehttp302(){
		if(hiveAp.getDownloadInfo().isEcwpNnu()){
			return true;
		}else{
			return cwp.isEnabledHTTP302();
		}
	}
	
	public int getSsidProtocolWepSize() {
		return -1;
	}
	
	public boolean isConfigureProtocolWep(int index) {
		return false;
	}
	
	public boolean isConfigureProtocolAscii() {
		return false;
	}
	
	public boolean isConfigureProtocolHex() {
		return !isConfigureProtocolAscii();
	}
	
	public String getProtocolWepValue(int i) throws IOException {
		return null;
	}
	
	public boolean isConfigureWepDefault(int i) {
		return false;
	}
	
	public int getProtocolReplayWindow() {
		return -1;
	}
	
	public boolean isEnableProtocolLocalTkip() {
		return false;
	}
	
	public boolean isEnableProtocolRemoteTkip() {
		return false;
	}
	
	public String getProtocolKeyValue() throws IOException {
		return null;
	}
	
	public boolean isConfigureProtocolStrict() {
		return false;
	}
	
	public boolean isConfigureProtocolNoStrict() {
		return false;
	}
	
	public int getProtocolGmkRekeyPeriod() {
		return -1;
	}
	
	public int getProtocolRekeyPeriod() {
		return -1;
	}
	
	public int getProtocolPtkTimeout() {
		return -1;
	}
	
	public int getProtocolPtkRetry() {
		return -1;
	}
	
	public int getProtocolGtkTimeout() {
		return -1;
	}
	
	public int getProtocolGtkRetry() {
		return -1;
	}
	
	public int getPtkRekeyPeriod(){
		return -1;
	}
	
	public boolean isRoamingProactivePmkidResponse() {
		return false;
	}
	
	public boolean isEnableReauthInterval(){
		return false;
	}
	
	public int getReauthIntervalValue(){
		return -1;
	}
	
	public boolean isCwpFromSsid(){
		return this.isCwpFLan;
	}
	
	public boolean isConfigServerName(){
		String serverDomain = cwp.getServerDomainName();
		return serverDomain != null && !"".equals(serverDomain);
	}
	
	public boolean isConfigCertDN(){
		return cwp.isEnabledHttps() && cwp.isCertificateDN();
	}
	
	public boolean isConfigCwpServer(){
		return this.isConfigServerName() || this.isConfigCertDN();
	}
	
	public String getCwpServerName(){
		return cwp.getServerDomainName();
	}
	
	public boolean isEnableProcessSipInfo(){
		String sipInfo = cwp.getBlockRedirectURL();
		return sipInfo != null && !"".equals(sipInfo);
	}
	
	public String getProcessSipInfo(){
		return cwp.getBlockRedirectURL();
	}
	
	public boolean isConfigDevicePolicy(){
		return lanProfile.isEnableOsDection();
	}
	
	public String getDevicePolicy(){
		return hiveAp.getMacAddress() + DEVICE_POLICY_SUFFIX;
	}
	
	public boolean isConfigPpskServer(){
		return false;
	}
	
	public String getPpskServerIp(){
		return null;
	}
	
	public boolean isConfigPpskWebServer(){
		return false;
	}
	
	public boolean isPpskWebServerHttps(){
		return false;
	}
	
	public String getBindToPpskSsid(){
		return null;
	}
	
	public boolean isUseDefaultPpskPage(){
		return false;
	}
	
	public boolean isConfigPpskWebDir(){
		String dir = getPpskWebDir();
		return dir != null && !"".equals(dir);
	}
	
	public boolean isConfigPpskLoginPage(){
		String page = getPpskLoginPage();
		return page != null && !"".equals(page);
	}
	
//	public boolean isConfigPpskLoginScript(){
//		String page = getPpskLoginScript();
//		return page != null && !"".equals(page);
//	}
	
	public String getPpskWebDir(){
		return null;
	}
	
	public String getPpskLoginPage(){
		return null;
	}
	
	public String getUserProfileSequence(){
		int upId = lanProfile.getAuthSequence();
		String upIdStr=null;
		switch(upId){
			case SsidProfile.AUTH_SEQUENCE_MAC_SSID_CWP:
				upIdStr = "mac-ssid-cwp";
				break;
			case SsidProfile.AUTH_SEQUENCE_MAC_CWP_SSID:
				upIdStr = "mac-cwp-ssid";
				break;
			case SsidProfile.AUTH_SEQUENCE_SSID_MAC_CWP:
				upIdStr = "ssid-mac-cwp";
				break;
			case SsidProfile.AUTH_SEQUENCE_SSID_CWP_MAC:
				upIdStr = "ssid-cwp-mac";
				break;
			case SsidProfile.AUTH_SEQUENCE_CWP_MAC_SSID:
				upIdStr = "cwp-mac-ssid";
				break;
			case SsidProfile.AUTH_SEQUENCE_CWP_SSID_MAC:
				upIdStr = "cwp-ssid-mac";
				break;
			default:
				upIdStr = "mac-ssid-cwp";
		}
		return upIdStr;
	}
	
	public boolean isUseForPpskServer(){
		return false;
	}
	
	public boolean isConfigPpskAuthUser(){
		return false;
	}

	public boolean isInjectOperatorNameEnable() {
		return radiusAssignment!=null&&radiusAssignment.isInjectOperatorNmAttri();
	}
	
	public boolean isEnabled8021X(){
		return !lanProfile.isEnabled8021Q() && lanProfile.isEnabled8021X();
	}

	public boolean isEnableAaaUserProfileMapping() {
		return lanProfile.isEnableAssignUserProfile();
	}

	public int getUserProfileMappingAttributeId() {
		return lanProfile.getAssignUserProfileAttributeId();
	}

	public boolean isConfigUserProfileMappingVendorId() {
		return lanProfile.getAssignUserProfileVenderId() > 0;
	}

	public int getUserProfileMappingVendorId() {
		return lanProfile.getAssignUserProfileVenderId();
	}

	public boolean isEnableJssEnroll() {
		return false;
	}
	
	public boolean isEnableAirwatchEnroll() {
		return false;
	}

	public String getMdmRootURLPath() {
		return null;
	}

	public String getMdmApiURL() {
		return null;
	}
	
	public String getMdmApiKey() {
		return null;
	}

	public String getMdmHttpAuthUser() {
		return null;
	}
	
	public String getMdmHttpAuthPassword() {
		return null;
	}

	public String getMdmOsObject(short i) {
		return null;
	}
	
	public boolean isEnableUsePolicy() {
		return cwp.isEnableUsePolicy();
	}
	
	public boolean isConfigMdmOsObject(short i){
		return false;
	}
	
	public boolean isEnable80211w(){
		return false;
	}
	
	public boolean isConfigmfpMandatory(){
		return false;
	}
	
	public boolean isConfigmfpOptional(){
		return false;
	}
	
public class WalledGarden{
		
		private String address;
		
		private boolean all = false;
		
		private boolean web = false;
		
		private final List<Protocol> protocolList = new ArrayList<Protocol>();
		
		public void setAddress(String address){
			this.address = address;
		}
		
		public String getAddress(){
			return this.address;
		}
		
		public void setAll(boolean isAll){
			this.all = isAll;
		}
		
		public boolean isAll(){
			return this.all;
		}
		
		public void setWeb(boolean isWeb){
			this.web = isWeb;
		}
		
		public boolean isWeb(){
			return this.web;
		}
		
		public List<Protocol> getProtocolList(){
			return this.protocolList;
		}
		
		public Protocol newProtocol(){
			return new Protocol();
		}

		public class Protocol{
			
			private int protocolValue;
			
			private final List<Integer> portList = new ArrayList<Integer>();
			
			public void setProtocolValue(int value){
				this.protocolValue = value;
			}
			
			public int getProtocolValue(){
				return this.protocolValue;
			}
			
			public List<Integer> getPortList(){
				return this.portList;
			}
		}
	}

	@Override
	public CwpMultiLanguageValue getCWPLanguageValue() {
		int dlang=this.cwp.getDefaultLanguage();
		switch (dlang)
		{
		case 1:
			return CwpMultiLanguageValue.ENGLISH;
		case 2:
			return CwpMultiLanguageValue.CHINESE_SIMPLE;
		case 3:
			return CwpMultiLanguageValue.GERMAN;
		case 4:
			return CwpMultiLanguageValue.FRENCH;
		case 5:
			return CwpMultiLanguageValue.KOREAN;
		case 6:
			return CwpMultiLanguageValue.DUTCH;
		case 7:
			return CwpMultiLanguageValue.SPANISH;
		case 8:
			return CwpMultiLanguageValue.CHINESE_TRADITIONAL;
		case 9:
			return CwpMultiLanguageValue.ITALIAN;
		}
		return CwpMultiLanguageValue.ENGLISH;
	}
	
	@Override
	public boolean isEnabled80211r() {
		return false;
	}
}
