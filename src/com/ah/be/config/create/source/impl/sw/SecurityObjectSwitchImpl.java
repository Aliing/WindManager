package com.ah.be.config.create.source.impl.sw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.AAAProfileInt;
import com.ah.be.config.create.source.SsidProfileInt;
import com.ah.be.config.create.source.SsidProfileInt.AuthMethodType;
import com.ah.be.config.create.source.SsidProfileInt.UserProfileDenyAction;
import com.ah.be.config.create.source.impl.SecurityObjectProfileImpl;
import com.ah.be.config.create.source.impl.baseImpl.SecurityObjectBaseImpl;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.lan.LanProfile;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.VpnService;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.useraccess.MgmtServiceOption;
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

public class SecurityObjectSwitchImpl extends SecurityObjectBaseImpl {
	
	private static final Tracer log = new Tracer(SecurityObjectSwitchImpl.class
			.getSimpleName());
	
	private PortAccessProfile accessProfile;
	private ConfigTemplateMdm configMdm;
	public static final String WALL_GARDEN_ADDRESS = "17.0.0.0/8";
	
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
	
	public SecurityObjectSwitchImpl(PortAccessProfile accessProfile, HiveAp hiveAp) throws CreateXMLException{
		this.accessProfile = accessProfile;
		this.hiveAp = hiveAp;
		
		if(accessProfile != null){
			isCwpFLan = true;
		}
		
		if(accessProfile.isEnabledCWP() && accessProfile.getCwp() != null){
			this.cwp = accessProfile.getCwp();
		}
		
		if(accessProfile!=null && accessProfile.getPortType()==PortAccessProfile.PORT_TYPE_ACCESS){
			configMdm=accessProfile.getConfigtempleMdm();
		//configMdm=configmdmservice.getConfigTempleMdmByDomain(accessProfile.getOwner().getId());
	    }
		
		if(accessProfile != null){
			
			radiusAssignment = accessProfile.getRadiusAssignment();
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
	
	private void prepareUserProfile(){
		//cwp userprofile type, fix bug 26077.
		defUserProfile = accessProfile.getDefUserProfile();
		if(cwp != null){
			short cwpType = cwp.getRegistrationType();
			switch(cwpType){
			case Cwp.REGISTRATION_TYPE_AUTHENTICATED:
				authUserProfile = defUserProfile;
				break;
			case Cwp.REGISTRATION_TYPE_REGISTERED:
				regUserProfile = accessProfile.getSelfRegUserProfile();
				defUserProfile = regUserProfile;
				break;
			case Cwp.REGISTRATION_TYPE_BOTH:
				regUserProfile = accessProfile.getSelfRegUserProfile();
				authUserProfile = defUserProfile;
				break;
			case Cwp.REGISTRATION_TYPE_EULA:
				regUserProfile = defUserProfile;
				break;
			case Cwp.REGISTRATION_TYPE_EXTERNAL:
				authUserProfile = defUserProfile;
				break;
			default:
				regUserProfile = accessProfile.getSelfRegUserProfile();
				break;
			}
		}
		
		//userprofile deny
		permittedUserProfiles = new ArrayList<UserProfile>();
		//current access profile not exists self regust user profile.
//		if(accessProfile.getSelfRegUserProfile() != null){
//			permittedUserProfiles.add(accessProfile.getSelfRegUserProfile());
//		}
		if(accessProfile.getDefUserProfile() != null){
			permittedUserProfiles.add(accessProfile.getDefUserProfile());
		}
		if(accessProfile.getAuthOkUserProfile() != null && !accessProfile.getAuthOkUserProfile().isEmpty()){
			permittedUserProfiles.addAll(accessProfile.getAuthOkUserProfile());
		}
		if(accessProfile.getAuthFailUserProfile() != null && !accessProfile.getAuthFailUserProfile().isEmpty()){
			permittedUserProfiles.addAll(accessProfile.getAuthFailUserProfile());
		}
	}
	
	private void addWalledGarden(List<WalledGardenItem> list, Pattern pattern, String url) {
		Matcher matcher = pattern.matcher(url);
		if(matcher.matches()){
			//new SingleTableItem
			String addr = matcher.group(3);
			String port = matcher.group(5);
			
			SingleTableItem addrItem = new SingleTableItem();
			addrItem.setIpAddress(addr);
			addrItem.setType(SingleTableItem.TYPE_GLOBAL);
			
			//new IpAddress
			IpAddress ipAddrObj = new IpAddress();
			ipAddrObj.getItems().add(addrItem);
			ipAddrObj.setTypeFlag(IpAddress.TYPE_HOST_NAME);
			
			WalledGardenItem webObj_1 = new WalledGardenItem();
			webObj_1.setProtocol(6);
			webObj_1.setServer(ipAddrObj);
			if(port != null && !port.equals("")){
				webObj_1.setPort(Integer.parseInt(port));
				webObj_1.setService(WalledGardenItem.SERVICE_PROTOCOL);
				list.add(webObj_1);
			}else if(matcher.group(1).equals("https")){
				webObj_1.setPort(443);
				webObj_1.setService(WalledGardenItem.SERVICE_PROTOCOL);
				list.add(webObj_1);
			}else if(matcher.group(1).equals("http")){
				webObj_1.setPort(80);
				webObj_1.setService(WalledGardenItem.SERVICE_PROTOCOL);
				list.add(webObj_1);
			}
		}
	}
	
	private void prepareWalledGarden() throws CreateXMLException{
		//load wallgarden
		if((this.cwp != null && this.cwp.getWalledGarden() != null) || (accessProfile.isEnableMDM())){
			
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
			
			if(hiveAp.getDeviceType() != HiveAp.Device_TYPE_VPN_GATEWAY /*&& !ssidProfile.isBlnBrAsPpskServer()*/ && 
					(cwp != null && (cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL || 
						cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK) ) 
				){
				String urlStr = cwp.getLoginURL();
				String regex = "((?i)http|(?i)https)(://)([[\\w-_]+\\.]+)(:?)(\\d*)(/?)(\\S*)";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(urlStr);
				if(matcher.matches()){
//					String protoStr = matcher.group(1);
					String addr = matcher.group(3);
					String portStr = matcher.group(5);
					
					//new SingleTableItem
					SingleTableItem addrItem = new SingleTableItem();
					addrItem.setIpAddress(addr);
					addrItem.setType(SingleTableItem.TYPE_GLOBAL);
					
					//new IpAddress
					IpAddress ipAddrObj = new IpAddress();
					ipAddrObj.getItems().add(addrItem);
					if(CLICommonFunc.isIpAddress(addr)){
						ipAddrObj.setTypeFlag(IpAddress.TYPE_IP_ADDRESS);
					}else{
						ipAddrObj.setTypeFlag(IpAddress.TYPE_HOST_NAME);
					}
					
					WalledGardenItem wallUrlHttp = new WalledGardenItem();
					wallUrlHttp.setService(WalledGardenItem.SERVICE_PROTOCOL);
					wallUrlHttp.setServer(ipAddrObj);
					wallUrlHttp.setProtocol(6);
					wallUrlHttp.setPort(80);
					wallGardenList.add(wallUrlHttp);
					
					WalledGardenItem wallUrlHttps = new WalledGardenItem();
					wallUrlHttps.setService(WalledGardenItem.SERVICE_PROTOCOL);
					wallUrlHttps.setServer(ipAddrObj);
					wallUrlHttps.setProtocol(6);
					wallUrlHttps.setPort(443);
					wallGardenList.add(wallUrlHttps);
					
					if(portStr != null && !"".equals(portStr)){
						WalledGardenItem wallUrlPort = new WalledGardenItem();
						wallUrlPort.setService(WalledGardenItem.SERVICE_PROTOCOL);
						wallUrlPort.setServer(ipAddrObj);
						wallUrlPort.setProtocol(6);
						wallUrlPort.setPort(Integer.valueOf(portStr));
						wallGardenList.add(wallUrlPort);
					}
				}else{
					String errMsg = NmsUtil.getUserMessage("error.be.config.create.lawlessURL", new String[]{urlStr});
					log.error("SsidProfileImpl", errMsg);
					throw new CreateXMLException(errMsg);
				}
			}
			
			if(accessProfile.isEnableMDM() && isSupportMDM() && configMdm != null && !(configMdm.getMdmType()!=ConfigTemplateMdm.MDM_ENROLL_TYPE_JSS && NmsUtil.compareSoftwareVersion("6.0.0.0",hiveAp.getSoftVer())>0 )){
					String regex = "((?i)http|(?i)https)(://)([[\\w-_]+\\.]+)(:?)(\\d*)(/?)(\\S*)";
					Pattern pattern = Pattern.compile(regex);
					addWalledGarden(wallGardenList, pattern, configMdm.getRootURLPath());
					if (configMdm.getMdmType() == ConfigTemplateMdm.MDM_ENROLL_TYPE_AIRWATCH)
						addWalledGarden(wallGardenList, pattern, configMdm.getApiURL());
					
					//new SingleTableItem
					String addr = WALL_GARDEN_ADDRESS;
					SingleTableItem addrItem = new SingleTableItem();
					addrItem.setIpAddress(addr);
					addrItem.setType(SingleTableItem.TYPE_GLOBAL);
					
					//new IpAddress
					IpAddress ipAddrObj = new IpAddress();
					ipAddrObj.getItems().add(addrItem);
					ipAddrObj.setTypeFlag(IpAddress.TYPE_IP_ADDRESS);
					
					//fix bug 17961
//					WalledGardenItem webObj_3 = new WalledGardenItem();
//					webObj_3.setPort(80);
//					webObj_3.setProtocol(6);
//					webObj_3.setServer(ipAddrObj);
//					webObj_3.setService(WalledGardenItem.SERVICE_PROTOCOL);
//					wallGardenList.add(webObj_3);
//					
//					WalledGardenItem webObj_4 = new WalledGardenItem();
//					webObj_4.setPort(443);
//					webObj_4.setProtocol(6);
//					webObj_4.setServer(ipAddrObj);
//					webObj_4.setService(WalledGardenItem.SERVICE_PROTOCOL);
//					wallGardenList.add(webObj_4);
					
					WalledGardenItem webObj_5 = new WalledGardenItem();
					webObj_5.setPort(5223);
					webObj_5.setProtocol(6);
					webObj_5.setServer(ipAddrObj);
					webObj_5.setService(WalledGardenItem.SERVICE_PROTOCOL);
					wallGardenList.add(webObj_5);				
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
	
	@Override
	public boolean isSupportMDM() {
		if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"6.0.0.0") >= 0){
		if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100 || (hiveAp.isCVGAppliance() && hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200))
		{
			return false;
		}
		}
		else if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"5.1.1.0") >= 0){
			if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200
					|| hiveAp.isCVGAppliance()){
				return false;
			}
			
		}
		return true;
	}
	
	
	public boolean isEnableJssEnroll() {
		return accessProfile.isEnableMDM() && isSupportMDM() && (configMdm==null?false:configMdm.getMdmType() == ConfigTemplateMdm.MDM_ENROLL_TYPE_JSS);
	}
	
	public boolean isEnableAirwatchEnroll() {
		return accessProfile.isEnableMDM() && isSupportMDM() && (configMdm==null?false:configMdm.getMdmType() == ConfigTemplateMdm.MDM_ENROLL_TYPE_AIRWATCH);
	}
	
	public String getMdmRootURLPath() {
		return configMdm==null?null:configMdm.getRootURLPath();
	}
	
	public String getMdmApiURL() {
		return configMdm==null?null:configMdm.getApiURL();
	}
	
	public String getMdmApiKey() {
		return configMdm==null?null:configMdm.getApiKey();
	}
	
	public String getMdmHttpAuthUser() {
		return configMdm==null?null:configMdm.getMdmUserName();
	}
	
	public String getMdmHttpAuthPassword() {
		return configMdm==null?null:configMdm.getMdmPassword();
	}
	
	public String getMdmOsObject(short i){
		switch (i) {
		case MDM_OSOBJECT_IOS: 			return SecurityObjectProfileImpl.MDM_OBJECT_IOS;
		case MDM_OSOBJECT_MACOS:		return SecurityObjectProfileImpl.MDM_OBJECT_MACOS;
		case MDM_OSOBJECT_SYMBIAN:		return SecurityObjectProfileImpl.MDM_OBJECT_SYMBIAN;
		case MDM_OSOBJECT_BLACKBERRY:	return SecurityObjectProfileImpl.MDM_OBJECT_BLACKBERRY;
		case MDM_OSOBJECT_ANDROID:      return SecurityObjectProfileImpl.MDM_OBJECT_ANDROID;
		case MDM_OSOBJECT_WINDOWSPHONE: return SecurityObjectProfileImpl.MDM_OBJECT_WINDOWSPHONE;
		default:						return null;
		}
	}
	
	
	public boolean isConfigMdmOsObject(short i){
		switch (i) {
		case MDM_OSOBJECT_IOS: 			return configMdm==null?false:configMdm.isEnableAppleOs();
		case MDM_OSOBJECT_MACOS:		return NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"5.1.1.0") > 0 && configMdm==null?false:configMdm.isEnableMacOs();
		case MDM_OSOBJECT_SYMBIAN:		return NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"6.0.0.0") > 0 && configMdm==null?false:configMdm.isEnableSymbianOs();
		case MDM_OSOBJECT_BLACKBERRY:	return NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"6.0.0.0") > 0 && configMdm==null?false:configMdm.isEnableBlackberryOs();
		case MDM_OSOBJECT_ANDROID:      return NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"6.0.0.0") > 0 && configMdm==null?false:configMdm.isEnableAndroidOs();
		case MDM_OSOBJECT_WINDOWSPHONE: return NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"6.1.0.0") > 0 && configMdm==null?false:configMdm.isEnableAndroidOs();
		default:						return false;
		}
	}
	
	
	public String getSsidGuiKey(){
		return MgrUtil.getUserMessage("config.upload.debug.accessPoints");
	}
	
	public String getSsidName(){
		if(this.accessProfile != null){
			return accessProfile.getName();
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
		return accessProfile.getName();
	}
	
	public boolean isConfigDefaultUserProfile(){
		return defUserProfile != null;
	}
	
	public int getDefaultUserProfileId() {
		return defUserProfile.getAttributeValue();
	}
	
	public boolean isConfigureWebServer() {
		return cwp != null || accessProfile.isEnableMDM();
	}
	
	public boolean isConfigExternalCwp(){
		return accessProfile.getCwp() != null && (accessProfile.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL || 
				accessProfile.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK);
	}
	
	public String getCwpGuiKey(){
		return null;
	}
	
	public String getCwpName(){
		if(cwp != null){
			return cwp.getCwpName();
		}else{
			return null;
		}
	}
	
	public boolean isConfigIndexFile(){
		if(cwp == null){
			return false;
		}
		String fileName = cwp.getWebPageName();
		return fileName != null && !"".equals(fileName) && !"None available".equals(fileName);
	}
	
	public String getWebServerIndexFile() {
		return cwp.getWebPageName();
	}
	
	public boolean isConfigSsidWebPage(){
		if(cwp == null){
			return false;
		}
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
		return cwp != null && cwp.isShowSuccessPage() && cwp.getResultPageName() != null && !"".equals(cwp.getResultPageName());
	}
	
	public String getWebServerSuccessFile() {
		return cwp.getResultPageName();
	}
	
	public boolean isConfigCwpFailureFile(){
		return cwp != null && cwp.isShowFailurePage() && cwp.getFailurePageName() != null && !"".equals(cwp.getFailurePageName());
	}
	
	public String getCwpFailureFileName(){
		return cwp.getFailurePageName();
	}
	
	public boolean isConfigureWebServerSsl() {
		return cwp != null && cwp.isEnabledHttps() && cwp.getCertificate() != null && cwp.getCertificate().getCertName() != null
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
		if(accessProfile == null){
			return false;
		}
		boolean blnCwp = this.isConfigureCWP() && this.cwp.getRegistrationType() != Cwp.REGISTRATION_TYPE_REGISTERED && 
			this.cwp.getRegistrationType() != Cwp.REGISTRATION_TYPE_EULA;
		boolean bln8021x = accessProfile.isEnabled8021X();
		
		return blnCwp || bln8021x;
	}
	
	public boolean isConfigUserProfileAllowed(){
		if(this.isConfigRadiusUserProfile()){
			return accessProfile.isChkUserOnly() && this.getUserProfileAllowedSize() > 0;
		}else{
			return false;
		}
	}
	
	public String getUserProfileAllowedName(int index){
		return permittedUserProfiles.get(index).getUserProfileName();
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
	
	public boolean isConfigUserProfileDeny(){
		if(this.isConfigRadiusUserProfile()){
			return accessProfile.isChkUserOnly();
		}else{
			return false;
		}
	}
	
	public boolean isConfigUserProfileAction(UserProfileDenyAction actionType){
		if(actionType == UserProfileDenyAction.ban){
			return accessProfile.getDenyAction() == LanProfile.DENY_ACTION_BAN;
		}else if(actionType == UserProfileDenyAction.banForever){
			return accessProfile.getDenyAction() == LanProfile.DENY_ACTION_BAN_FOREVER;
		}else if(actionType == UserProfileDenyAction.disconnect){
			return accessProfile.getDenyAction() == LanProfile.DENY_ACTION_DISCONNECT;
		}else{
			return false;
		}
	}
	
	public int getBanValue(){
		return (int)accessProfile.getActionTime();
	}
	
	public boolean isEnableStrict(){
		return accessProfile.isChkDeauthenticate();
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
		if(this.accessProfile == null){
			return false;
		}
		return isPortAccessMode();
	}
	
	public boolean isProtocolOpen(){
		return !this.accessProfile.isEnabled8021X();
	}
	
	private boolean isPortAccessMode(){
		return accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_ACCESS || 
				accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_MONITOR;
	}
	
	public boolean isConfigSsidAAARadius() {
		return (radiusAssignment != null && accessProfile.isRadiusAuthEnable()) || 
				(radiusAssignment != null && accessProfile.isEnabledIDM());
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
	
	public boolean isMacBasedAuthEnable() {
		return accessProfile.isEnabledMAC();
	}
	
	public boolean isConfigFallbackToEcwp(){
		return hiveAp.getDownloadInfo().isEcwpDepaul() || hiveAp.getDownloadInfo().isEcwpNnu();
	}
	
	public boolean isEnableFallbackToEcwp(){
		return false;
	}
	
	public AuthMethodType getMacAuthType(){
		if(!accessProfile.isEnabledMAC()){
			return null;
		}else{
			if(accessProfile.getAuthProtocol() == Cwp.AUTH_METHOD_MSCHAPV2){
				return AuthMethodType.msChapV2;
			}else if(accessProfile.getAuthProtocol() == Cwp.AUTH_METHOD_CHAP){
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
	
	public boolean isConfigureProtocolHex() {
		return !isConfigureProtocolAscii();
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
		return accessProfile.isEnableOsDection() && accessProfile.isExistsAuthMode(hiveAp.getDeviceType());
	}
	
	public String getDevicePolicy(){
		return hiveAp.getMacAddress() + DEVICE_POLICY_SUFFIX;
	}
	
	public boolean isConfigPpskWebDir(){
		String dir = getPpskWebDir();
		return dir != null && !"".equals(dir);
	}
	
	public boolean isConfigPpskLoginPage(){
		String page = getPpskLoginPage();
		return page != null && !"".equals(page);
	}
	
	public String getUserProfileSequence(){
		int upId = accessProfile.getAuthSequence();
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

	public boolean isInjectOperatorNameEnable() {
		return radiusAssignment!=null&&radiusAssignment.isInjectOperatorNmAttri();
	}
	
	public boolean isEnabled8021X(){
		return this.accessProfile.isEnabled8021X();
	}

	public boolean isEnableAaaUserProfileMapping() {
		return accessProfile.isEnableAssignUserProfile();
	}

	public int getUserProfileMappingAttributeId() {
		return accessProfile.getAssignUserProfileAttributeId();
	}

	public boolean isConfigUserProfileMappingVendorId() {
		return accessProfile.getAssignUserProfileVenderId() > 0;
	}

	public int getUserProfileMappingVendorId() {
		return accessProfile.getAssignUserProfileVenderId();
	}
	
	public boolean isEnableUsePolicy() {
		return cwp.isEnableUsePolicy();
	}
	
	public boolean isConfigAuthMethod() {
		return accessProfile.isExistsAuthMode(hiveAp.getDeviceType());
	}
	
	public boolean isConfigPortBased(){
		return !isConfigHostBased();
	}
	
	public boolean isConfigHostBased(){
		return accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA || accessProfile.isEnabledSameVlan();
	}
	
	public boolean isMdmTypeEnabled(int mdmType) {
		if (!isSupportMDM())
			return false;
		
		switch (mdmType) {
		case ConfigTemplateMdm.MDM_ENROLL_TYPE_JSS:
			return isEnableJssEnroll();
		case ConfigTemplateMdm.MDM_ENROLL_TYPE_AIRWATCH:
			return isEnableAirwatchEnroll();
		case ConfigTemplateMdm.MDM_ENROLL_TYPE_AEROHIVE:
				return false;
		}
		return false;
	}
	
	
	public boolean isConfigPortBasedFailedVlan(){
		return accessProfile.getAuthFailUserProfile() != null && 
				!accessProfile.getAuthFailUserProfile().isEmpty();
	}
	
	public int getPortBasedFailedVlan(){
		for(UserProfile up : accessProfile.getAuthFailUserProfile()){
			if(up != null){
				return (int)up.getAttributeValue();
			}
		}
		return -1;
	}
	
	public boolean isConfigMultipleDomain(){
		return accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA;
	}
	
	public boolean isConfigInitialAuthMethod(){
		return !accessProfile.isFirst8021X();
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

	public boolean isEnableReportedDataByCWP(){
//		if(hiveAp.isOverrideCaptureDataByCWP()){
//			return hiveAp.isEnableCaptureDataByCWP();
//		}else{
		MgmtServiceOption mso = hiveAp.getConfigTemplate().getMgmtServiceOption();
		if(null != mso){
			return mso.isEnableCaptureDataByCWP();
		}else{
			return false;
		}
//		}
	}
	
	public boolean isEnableCWPAndSelfRegister() {
		if(null != cwp && (cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED
				|| cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH)){
			return true;
		}
		return false;
	}
	
	public boolean isConfigSelfRegViaIdm(){
		return accessProfile.isEnabledIDM() && 
				accessProfile.getCwp() != null && 
				getSelfRegViaIdmModel() != null;
	}
	
	private String getSelfRegViaIdmModel(){
		if(accessProfile.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED){
			return "self-register";
		}else if(accessProfile.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH){
			return "both";
		}else{
			return null;
		}
	}
	
	public PortAccessProfile getAccessProfile(){
		return this.accessProfile;
	}
	
}
