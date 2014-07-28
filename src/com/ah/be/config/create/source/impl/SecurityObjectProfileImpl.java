package com.ah.be.config.create.source.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.AAAProfileInt;
import com.ah.be.config.create.source.SsidProfileInt;
import com.ah.be.config.create.source.SsidProfileInt.AuthMethodType;
import com.ah.be.config.create.source.SsidProfileInt.UserProfileDenyAction;
import com.ah.be.config.create.source.common.AcmOsObject;
import com.ah.be.config.create.source.impl.baseImpl.SecurityObjectBaseImpl;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.ConfigMDMAirWatchNonCompliance;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.VpnService;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusServer;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.SsidProfile;
import com.ah.bo.wlan.SsidSecurity;
import com.ah.bo.wlan.WalledGardenItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.xml.be.config.CwpMultiLanguageValue;
import com.ah.xml.be.config.SecurityWebServer;

/**
 * @author zhang
 * @version 2010-4-29 14:41:46
 */

@SuppressWarnings("static-access")
public class SecurityObjectProfileImpl extends SecurityObjectBaseImpl {
	
	private static final Tracer log = new Tracer(SecurityObjectProfileImpl.class
			.getSimpleName());
	
	public static final String PPSK_SERVER_FROM_DHCP = "ppsk-server-from-dhcp";
	
	public static final String ACM_SUPPORT_DEVICE = "0,4,5";
	public static final String WALL_GARDEN_ADDRESS = "17.0.0.0/8";
		
	private SsidProfile ssidProfile;
	private ConfigTemplateMdm configMdm;
	private ConfigMDMAirWatchNonCompliance mdmAirwatchNonCompliance;
	private Cwp cwp;
	
	public static final String SEC_SUFFIX = "_eth";
	
	private UserProfile defUserProfile;
	private UserProfile regUserProfile;
	private UserProfile authUserProfile;
	private RadiusAssignment radiusAssignment;
	private RadiusServer primaryRadius;
	private RadiusServer backup1Radius;
	private RadiusServer backup2Radius;
	private RadiusServer backup3Radius;
	
	private RadiusServer acctPrimaryRadius;
	private RadiusServer acctBackup1Radius;
	private RadiusServer acctBackup2Radius;
	private RadiusServer acctBackup3Radius;
	
	private List<UserProfile> permittedUserProfiles;
	private final List<WalledGarden> IpWallGardenList = new ArrayList<WalledGarden>();
	private final List<WalledGarden> HostWallGardenList = new ArrayList<WalledGarden>();
	private List<String> wepSharedList;
	private SsidSecurity ssidSecurity;
	private boolean isCwpFSsid = false;
	private boolean isBadCTMdMWork=false;
	private boolean isEthernetProfile = false;
	
	public SecurityObjectProfileImpl(SsidProfile ssidProfileO, HiveAp hiveAp) throws Exception{
		this.ssidProfile = ssidProfileO;
		this.hiveAp = hiveAp;
		if(ssidProfileO != null)
			isCwpFSsid = true;
		
		if(ssidProfile == null && (hiveAp.isEthCwpEnableEthCwp() || hiveAp.isEthCwpEnableMacAuth() || hiveAp.isEnableMDM())){
			ssidProfile = new SsidProfile();
			ssidProfile.setSsid(hiveAp.getMacAddress()+SEC_SUFFIX);
			isEthernetProfile = true;
			if(hiveAp.isEnableMDM()){
				isBadCTMdMWork=true;
				ssidProfile.setEnableMDM(hiveAp.isEnableMDM());
//				if(!hiveAp.isEthCwpEnableEthCwp() && !hiveAp.isEthCwpEnableMacAuth()){
//					ssidProfile.setSsid(hiveAp.getConfigTemplateMdm().getPolicyname()+SEC_SUFFIX);
//				}
			}
			
			if(hiveAp.isEthCwpEnableEthCwp() || hiveAp.isEthCwpEnableMacAuth()){
				Cwp ethCwp = hiveAp.getEthCwpCwpProfile();
				ssidProfile.setCwp(ethCwp);
				ssidProfile.setCwpSelectEnabled(true);
//				if(ssidProfile.getCwp() != null){
//					ssidProfile.setSsid(ssidProfile.getCwp().getCwpName()+SEC_SUFFIX);
//				}else{
//					ssidProfile.setSsid(hiveAp.getMacAddress()+SEC_SUFFIX);
//				}
				ssidProfile.setMacAuthEnabled(hiveAp.isEthCwpEnableMacAuth());
				ssidProfile.setRadiusAssignment(hiveAp.getEthCwpRadiusClient());
				ssidProfile.setPersonPskRadiusAuth(hiveAp.getEthCwpAuthMethod());
				ssidProfile.setUserProfileDefault(hiveAp.getEthCwpDefaultAuthUserProfile());
				ssidProfile.setUserProfileSelfReg(hiveAp.getEthCwpDefaultRegUserProfile());
				ssidProfile.setRadiusUserProfile(hiveAp.getEthCwpRadiusUserProfiles());
				ssidProfile.setChkUserOnly(hiveAp.isEthCwpLimitUserProfiles());
				ssidProfile.setDenyAction(hiveAp.getEthCwpDenyAction());
				ssidProfile.setActionTime(hiveAp.getEthCwpActiveTime());
				ssidProfile.setChkDeauthenticate(hiveAp.isEthCwpEnableStriction());
				ssidProfile.setCwpSelectEnabled(hiveAp.isEthCwpEnableEthCwp());
			}
			
		}
		
		
		if(ssidProfile.getCwp() != null){
			this.cwp = ssidProfile.getCwp();
		}else if(ssidProfile.getUserPolicy() != null){
			this.cwp = ssidProfile.getUserPolicy();
		}else if(ssidProfile.isEnableSingleSsid()){
 			ssidProfile.setCwp(ssidProfile.getPpskECwp());
  			this.cwp = ssidProfile.getPpskECwp();
 		}

		if(ssidProfile.isEnablePpskSelfReg() && ssidProfile.getCwp() != null && 
				(ssidProfile.getParentPpskSsid() != null && !ssidProfile.getParentPpskSsid().isEnabledIDM())
			){
			Cwp ppskCwp = ssidProfile.getPpskECwp();
			String protocol = ppskCwp.isEnabledHttps()? "https" : "http";
			String ipAddr = "";
			if(ssidProfile.isBlnBrAsPpskServer() && hiveAp.isBranchRouter()){
				ipAddr = hiveAp.getCfgIpAddress();
			}else if(ssidProfile.isBlnBrAsPpskServer() && !hiveAp.isBranchRouter()){
				ipAddr = PPSK_SERVER_FROM_DHCP;
			}else if(ssidProfile.getPpskServer() != null){
				ipAddr = ssidProfile.getPpskServer().getCfgIpAddress();
			}
			String webDir = ppskCwp.getDirectoryName();
			String loginPage = ppskCwp.getWebPageName();
			String eCwpUrl = protocol + "://" + ipAddr + "/ppsk/" + webDir + "/" + loginPage;
			ssidProfile.getCwp().setLoginURL(eCwpUrl);
		}
		
		if(isBadCTMdMWork){
			configMdm=hiveAp.getConfigTemplateMdm();
		}else{
			configMdm=ssidProfile.getConfigmdmId();
		}
		//configMdm=configmdmservice.getConfigTemplateMdmByDomain(ssidProfile.getOwner().getId());
		
		if (configMdm != null) {
		    mdmAirwatchNonCompliance = configMdm.getAwNonCompliance();
		} 
		//mdmAirwatchNonCompliance = (mdmAirwatchNonCompliance == null) ? new ConfigMDMAirWatchNonCompliance() : mdmAirwatchNonCompliance;
		
		if(ssidProfile != null){
			this.ssidSecurity = ssidProfile.getSsidSecurity();
			
			//load RadiusServer
			radiusAssignment = ssidProfile.getRadiusAssignment();
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
//				if(this.isMergeRadiusServer(acctPrimaryRadius, primaryRadius)){
//					primaryRadius.setServerType(RadiusServer.RADIUS_SERVER_TYPE_BOTH);
//					primaryRadius.setAcctPort(acctPrimaryRadius.getAcctPort());
//					acctPrimaryRadius = null;
//				}
//				if(this.isMergeRadiusServer(acctBackup1Radius, backup1Radius)){
//					backup1Radius.setServerType(RadiusServer.RADIUS_SERVER_TYPE_BOTH);
//					backup1Radius.setAcctPort(acctBackup1Radius.getAcctPort());
//					acctBackup1Radius = null;
//				}
//				if(this.isMergeRadiusServer(acctBackup2Radius, backup2Radius)){
//					backup2Radius.setServerType(RadiusServer.RADIUS_SERVER_TYPE_BOTH);
//					backup2Radius.setAcctPort(acctBackup2Radius.getAcctPort());
//					acctBackup2Radius = null;
//				}
//				if(this.isMergeRadiusServer(acctBackup3Radius, backup3Radius)){
//					backup3Radius.setServerType(RadiusServer.RADIUS_SERVER_TYPE_BOTH);
//					backup3Radius.setAcctPort(acctBackup3Radius.getAcctPort());
//					acctBackup3Radius = null;
//				}
			}
		
			
			//cwp userprofile type, fix bug 26077.
			defUserProfile = ssidProfile.getUserProfileDefault();
			if(cwp != null && !ssidProfile.isEnableSingleSsid()){
				short cwpType = cwp.getRegistrationType();
				switch(cwpType){
				case Cwp.REGISTRATION_TYPE_AUTHENTICATED:
					authUserProfile = defUserProfile;
					if(defUserProfile.getId() < 0){
						defUserProfile =  MgrUtil.getQueryEntity().findBoByAttribute(UserProfile.class, "userProfileName", "default-profile", new ConfigLazyQueryBo());
					}
					break;
				case Cwp.REGISTRATION_TYPE_REGISTERED:
					regUserProfile = ssidProfile.getUserProfileSelfReg();
					defUserProfile = regUserProfile;
					break;
				case Cwp.REGISTRATION_TYPE_BOTH:
					regUserProfile = ssidProfile.getUserProfileSelfReg();
					authUserProfile = defUserProfile;
					break;
				case Cwp.REGISTRATION_TYPE_EULA:
					regUserProfile = defUserProfile;
					break;
				case Cwp.REGISTRATION_TYPE_EXTERNAL:
					authUserProfile = defUserProfile;
					break;
				default:
					regUserProfile = ssidProfile.getUserProfileSelfReg();
					break;
				}
			}
			
			//userprofile deny
			permittedUserProfiles = new ArrayList<UserProfile>();
			if(ssidProfile.getUserProfileSelfReg() != null){
				permittedUserProfiles.add(ssidProfile.getUserProfileSelfReg());
			}
			if(ssidProfile.getUserProfileDefault() != null){
				permittedUserProfiles.add(ssidProfile.getUserProfileDefault());
			}
			if(ssidProfile.getRadiusUserProfile() != null && !ssidProfile.getRadiusUserProfile().isEmpty()){
				permittedUserProfiles.addAll(ssidProfile.getRadiusUserProfile());
			}
		}
		
		//load wallgarden and jssEnroll
		if((this.cwp != null && this.cwp.getWalledGarden() != null) || (ssidProfile.isEnableMdmBusiness() && isSupportMDM())){
			
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
				
				if(hiveAp.getDeviceType() != HiveAp.Device_TYPE_VPN_GATEWAY && 
						!StringUtils.isEmpty(cwp.getLoginURL()) && (
						cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL || 
						(cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK && !ssidProfile.isEnabledIDM())) ){
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
			}else if(ssidProfile.isEnableSingleSsid()){
				String addr = ssidProfile.getPpskServer().getCfgIpAddress();
		
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
			}
			
			String regex = "((?i)http|(?i)https)(://)([[\\w-_]+\\.]+)(:?)(\\d*)(/?)(\\S*)";
			Pattern pattern = Pattern.compile(regex);
			if (ssidProfile.isEnableMDM() && isSupportMDM() && 
					(ssidProfile.getConfigmdmId() != null && ssidProfile.getConfigmdmId().getMdmType()==0 || NmsUtil.compareSoftwareVersion("6.0.0.0", hiveAp.getSoftVer()) <= 0 )){
				wallGardenList.add(genenrateWalledGarden(pattern, configMdm.getRootURLPath()));
				if (configMdm.getMdmType() == ConfigTemplateMdm.MDM_ENROLL_TYPE_AIRWATCH)
					wallGardenList.add(genenrateWalledGarden(pattern, configMdm.getApiURL()));

				wallGardenList.addAll(generateSpecialWalledGarden(getDeviceIpAddress()));
			}
			
			if (ssidProfile.isEnableAerohiveMdm() && NmsUtil.compareSoftwareVersion("6.0.2.0", hiveAp.getSoftVer()) <= 0) {
				
				WalledGardenItem item = genenrateWalledGarden(pattern, hiveAp.getDownloadInfo().getMdmURLPath());
				item.setService(WalledGardenItem.SERVICE_ALL);
				wallGardenList.add(item);
				
				List<WalledGardenItem> items = generateSpecialWalledGarden(getDeviceIpAddress());
				wallGardenList.addAll(items);
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
	
	private List<String> getDeviceIpAddress(){
		String[] support = this.ACM_SUPPORT_DEVICE.split(",");
		List<String> supportIpAddress = new ArrayList<String>();
		for(String pre : support){
			int deviceType = Integer.parseInt(pre);
			String ipAddress = AcmOsObject.getIpAddressByValue(deviceType);
			if(ipAddress.contains(";")){
				String[] ips = ipAddress.split(";");
				for(String ip : ips){
					supportIpAddress.add(ip);
				}
			}else{
				supportIpAddress.add(ipAddress);
			}
		}
		return supportIpAddress;
	}
	
	private List<WalledGardenItem> generateSpecialWalledGarden(List<String> supportDeviceIpAddress) {
		//new SingleTableItem
		//String addr = WALL_GARDEN_ADDRESS;
		String regex = "[a-zA-Z_]{1,}[0-9]{0,}\\.(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}";
		Pattern pattern = Pattern.compile(regex);
		List<WalledGardenItem> deviceWalledGarden = new ArrayList<WalledGardenItem>();
		if(null != supportDeviceIpAddress && supportDeviceIpAddress.size() > 0){
			for(String addr : supportDeviceIpAddress){
				SingleTableItem addrItem = new SingleTableItem();
				String[] pros = null;
				if(addr.contains(",")){
					pros = addr.split(",");
				}
				if(pros == null){
					addrItem.setIpAddress(addr);
				}else{
					addrItem.setIpAddress(pros[0]);
				}
				
				addrItem.setType(SingleTableItem.TYPE_GLOBAL);
				
				//new IpAddress
				IpAddress ipAddrObj = new IpAddress();
				ipAddrObj.getItems().add(addrItem);
				Matcher matcher = pattern.matcher(addrItem.getIpAddress());
				if(matcher.matches()){
					ipAddrObj.setTypeFlag(IpAddress.TYPE_HOST_NAME);
				}else{
					ipAddrObj.setTypeFlag(IpAddress.TYPE_IP_ADDRESS);
				}
				
				
				//fix bug 17961
//				WalledGardenItem webObj_3 = new WalledGardenItem();
//				webObj_3.setPort(80);
//				webObj_3.setProtocol(6);
//				webObj_3.setServer(ipAddrObj);
//				webObj_3.setService(WalledGardenItem.SERVICE_PROTOCOL);
//				wallGardenList.add(webObj_3);
//				
//				WalledGardenItem webObj_4 = new WalledGardenItem();
//				webObj_4.setPort(443);
//				webObj_4.setProtocol(6);
//				webObj_4.setServer(ipAddrObj);
//				webObj_4.setService(WalledGardenItem.SERVICE_PROTOCOL);
//				wallGardenList.add(webObj_4);
				
				WalledGardenItem webObj_5 = new WalledGardenItem();
				if(pros != null){
					webObj_5.setPort(Integer.parseInt(pros[2]));
					webObj_5.setProtocol(Integer.parseInt(pros[1]));
					webObj_5.setService(WalledGardenItem.SERVICE_PROTOCOL);
				}else{
					webObj_5.setService(WalledGardenItem.SERVICE_ALL);
				}
				webObj_5.setServer(ipAddrObj);
				deviceWalledGarden.add(webObj_5);
			}
		}
		

		
		return deviceWalledGarden;
	}
	
	private WalledGardenItem genenrateWalledGarden(Pattern pattern, String url) {
		if (url == null)
			return null;
		
		Matcher matcher = pattern.matcher(url);
		WalledGardenItem webObj_1 = null;
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
			
			webObj_1 = new WalledGardenItem();
			webObj_1.setProtocol(6);
			webObj_1.setServer(ipAddrObj);
			if(port != null && !port.equals("")){
				webObj_1.setPort(Integer.parseInt(port));
				webObj_1.setService(WalledGardenItem.SERVICE_PROTOCOL);
			}else if(matcher.group(1).equals("https")){
				webObj_1.setPort(443);
				webObj_1.setService(WalledGardenItem.SERVICE_PROTOCOL);
			}else if(matcher.group(1).equals("http")){
				webObj_1.setPort(80);
				webObj_1.setService(WalledGardenItem.SERVICE_PROTOCOL);
			}
		}
		
		return webObj_1;
	}
//	private boolean isMergeRadiusServer(RadiusServer authRad, RadiusServer acctRad){
//		String authIp=null, acctIp=null;
//		String authPas, acctPas;
//		try{
//			if(authRad.getIpAddress() != null){
//				authIp = CLICommonFunc.getIpAddress(authRad.getIpAddress(), this.hiveAp).getIpAddress();
//			}
//			if(acctRad.getIpAddress() != null){
//				acctIp = CLICommonFunc.getIpAddress(acctRad.getIpAddress(), this.hiveAp).getIpAddress();
//			}
//			authPas = authRad.getSharedSecret();
//			acctPas = acctRad.getSharedSecret();
//			if(authIp != null && authIp.equals(acctIp) &&
//					authPas != null && authPas.equals(acctPas)){
//				return true;
//			}else{
//				return false;
//			}
//		}catch(Exception ex){
//			return false;
//		}
//	}
	
	public String getSsidGuiKey(){
		return MgrUtil.getUserMessage("config.upload.debug.ssidProfiles");
	}
	
	public boolean isSupportThisDevice(){
		return true;
	}
	
	public String getSsidName(){
		if(ssidProfile != null){
			return ssidProfile.getSsid();
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
		return ssidProfile.getSsid();
	}
	
	public boolean isConfigDefaultUserProfile(){
		return defUserProfile != null;
	}
	
	public int getDefaultUserProfileId() {
		return defUserProfile.getAttributeValue();
	}
	
	public boolean isConfigureWebServer() {
		return cwp != null || ssidProfile.isEnableMdmBusiness();
	}
	
	public boolean isConfigExternalCwp(){
		if(ssidProfile.getCwp() == null){
			return false;
		}
		
		if(ssidProfile.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL){
			return true;
		}
		
		if(ssidProfile.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK && 
				(ssidProfile.getParentPpskSsid() != null && !ssidProfile.getParentPpskSsid().isEnabledIDM())){
			return true;
		}
		
		return false;
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
		if(cwp != null){
			String fileName = cwp.getWebPageName();
			return fileName != null && !"".equals(fileName) && !"None available".equals(fileName);
		}
		return false;
	}
	
	public String getWebServerIndexFile() {
		return cwp.getWebPageName();
	}
	
	public boolean isConfigSsidWebPage(){
		if(cwp != null){
			return (cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED && cwp.getWebPageSource() == Cwp.WEB_PAGE_SOURCE_AUTOGENERATE) || 
				(cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED && cwp.getWebPageSource() == Cwp.WEB_PAGE_SOURCE_IMPORT) ||
				(cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH ) ||
				cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA;
		}
		return false;
//			this.isCwpEula();
	}
	
	public int getMandatoryFieldValue(SecurityWebServer.WebPage.MandatoryField mandatoryObj) {
//		if (cwp.getWebPageSource() == Cwp.WEB_PAGE_SOURCE_AUTOGENERATE) {
//			return mandatoryObj.getValue();
//		} else {
//			return cwp.getRequestField();
//		}
		if(cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA){
			return 0;
		}else{
			return cwp.getRequestField();
		}
	}
	
	public int getOptionalFieldValue(SecurityWebServer.WebPage.MandatoryField.OptionalField optionalObj) {
//		if (cwp.getWebPageSource() == Cwp.WEB_PAGE_SOURCE_AUTOGENERATE) {
//			return optionalObj.getValue();
//		} else {
//			return cwp.getNumberField();
//		}
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
		return getCwpFailureFileName() != null && !"".equals(getCwpFailureFileName());
	}
	
	public String getCwpFailureFileName(){
		if(cwp == null){
			return null;
		}else if(isConfigCwpAnonymousAccess()){
			return cwp.getWebPageName();
		}else if(cwp.isShowFailurePage()){
			return cwp.getFailurePageName();
		}else{
			return null;
		}
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
	
	private boolean isConfigRadiusUserProfile(){
		if(ssidProfile == null){
			return false;
		}
		boolean blnCwp = this.isConfigureCWP() && this.cwp.getRegistrationType() != Cwp.REGISTRATION_TYPE_REGISTERED && 
			this.cwp.getRegistrationType() != Cwp.REGISTRATION_TYPE_EULA;
		boolean bln8021x = ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X ||
						ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X ||
						ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X ||
						ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP;
		boolean blnMacAuth = ssidProfile.getMacAuthEnabled() && ssidProfile.getAccessMode() != SsidProfile.ACCESS_MODE_PSK;
		boolean blnenGuestManager = ssidProfile.getEnabledUseGuestManager();
		boolean blnIdmEnable = ssidProfile.isBlnDisplayIDM();
		
		return blnCwp || bln8021x || blnMacAuth || blnenGuestManager || blnIdmEnable;
	}
	
	public int getUserProfileAllowedSize(){
		if(permittedUserProfiles == null){
			return 0;
		}else{
			return permittedUserProfiles.size();
		}
	}
	
	public boolean isConfigUserProfileAllowed(){
		if(this.isConfigRadiusUserProfile()){
			return ssidProfile.getChkUserOnly() && this.getUserProfileAllowedSize() > 0;
		}else{
			return false;
		}
	}
	
	public String getUserProfileAllowedName(int index){
		return permittedUserProfiles.get(index).getUserProfileName();
	}
	
	public boolean isConfigUserProfileDeny(){
		if(this.isConfigRadiusUserProfile()){
			return ssidProfile.getChkUserOnly();
		}else{
			return false;
		}
	}
	
	public boolean isConfigUserProfileAction(UserProfileDenyAction actionType){
		if(actionType == UserProfileDenyAction.ban){
			return ssidProfile.getDenyAction() == SsidProfile.DENY_ACTION_BAN;
		}else if(actionType == UserProfileDenyAction.banForever){
			return ssidProfile.getDenyAction() == SsidProfile.DENY_ACTION_BAN_FOREVER;
		}else if(actionType == UserProfileDenyAction.disconnect){
			return ssidProfile.getDenyAction() == SsidProfile.DENY_ACTION_DISCONNECT;
		}else{
			return false;
		}
	}
	
	public int getBanValue(){
		return (int)ssidProfile.getActionTime();
	}
	
	public boolean isEnableStrict(){
		return ssidProfile.getChkDeauthenticate();
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
		if(ssidProfile == null){
			return false;
		}
		return this.isProtocolWpaAuto_8021x() ||
			this.isProtocolWpa2Tkip_8021x() ||
			this.isProtocolWpa2Aes_8021x();
	}
	
	public boolean isProtocolOpen(){
		return ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_OPEN && 
			ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_OPEN;
	}
	
	public boolean isProtocolWepOpen(){
		return ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_WEP && 
			ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK &&
			ssidProfile.getAuthentication() == SsidProfile.KEY_AUT_OPEN;
	}
	public boolean isProtocolWepShared(){
		return ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_WEP &&
			ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK &&
			ssidProfile.getAuthentication() == SsidProfile.KEY_AUT_SHARED;
	}
	public boolean isProtocolWep104_8021x(){
		return ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_WEP &&
			ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP &&
			ssidProfile.getEncryption() == SsidProfile.KEY_ENC_WEP104;
	}
	public boolean isProtocolWep40_8021x(){
		return ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_WEP &&
			ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP &&
			ssidProfile.getEncryption() == SsidProfile.KEY_ENC_WEP40;
	}
	
	public boolean isProtocolWpaAes_8021x(){
		if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_230){
			return false;
		}
		return ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_8021X &&
		ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X &&
		ssidProfile.getEncryption() == SsidProfile.KEY_ENC_CCMP;
	}
	public boolean isProtocolWpaTkip_8021x(){
		if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_230){
			return false;
		}
		return ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_8021X &&
		ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X &&
		ssidProfile.getEncryption() == SsidProfile.KEY_ENC_TKIP;
	}
	public boolean isProtocolWpa2Aes_8021x(){
		return ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_8021X &&
		ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X &&
		ssidProfile.getEncryption() == SsidProfile.KEY_ENC_CCMP;
	}
	public boolean isProtocolWpa2Tkip_8021x(){
		return ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_8021X &&
		ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X &&
		ssidProfile.getEncryption() == SsidProfile.KEY_ENC_TKIP;
	}
	public boolean isProtocolWpaAuto_8021x(){
		boolean blnRes = ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_8021X &&
		ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X &&
		ssidProfile.getEncryption() == SsidProfile.KEY_ENC_AUTO_TKIP_OR_CCMP;
		
		if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_230){
			blnRes = blnRes || 
					(ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_8021X &&
						ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA_EAP_802_1_X &&
						(ssidProfile.getEncryption() == SsidProfile.KEY_ENC_CCMP || ssidProfile.getEncryption() == SsidProfile.KEY_ENC_TKIP)
					);
		}
		
		return blnRes;
	}
	
	public boolean isProtocolWpaAesPsk(){
		if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_230){
			return false;
		}
		return ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA_PSK &&
		ssidProfile.getEncryption() == SsidProfile.KEY_ENC_CCMP;
	}
	public boolean isProtocolWpaTkipPsk(){
		if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_230){
			return false;
		}
		return ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA_PSK &&
		ssidProfile.getEncryption() == SsidProfile.KEY_ENC_TKIP;
	}
	public boolean isProtocolWpa2AesPsk(){
		return ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_PSK &&
		ssidProfile.getEncryption() == SsidProfile.KEY_ENC_CCMP;
	}
	public boolean isProtocolWpa2TkipPsk(){
		return ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_PSK &&
		ssidProfile.getEncryption() == SsidProfile.KEY_ENC_TKIP;
	}
	public boolean isProtocolWpaAutoPsk(){
		boolean blnRes = ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK &&
		ssidProfile.getEncryption() == SsidProfile.KEY_ENC_AUTO_TKIP_OR_CCMP;
		
		if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_230){
			blnRes = blnRes || 
					(ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA_PSK && 
						(ssidProfile.getEncryption() == SsidProfile.KEY_ENC_TKIP || ssidProfile.getEncryption() == SsidProfile.KEY_ENC_CCMP)
					);
		}
		
		return blnRes;
	}
	
	public boolean isSecurityPreauthEnable() {
		return ssidProfile.isPreauthenticationEnabled();
	}
	
	public boolean isConfigSsidAAARadius() {
//		return radiusAssignment != null && this.cwp != null && 
//				(this.cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED || 
//					this.cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH);
		return (radiusAssignment != null && ssidProfile.getBlnDisplayRadius()) || 
				(radiusAssignment != null && ssidProfile.isEnabledIDM()) || 
				(radiusAssignment != null && ssidProfile.getParentPpskSsid() != null && ssidProfile.getParentPpskSsid().isEnabledIDM());
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
			log.error("SsidProfileImpl", errMsg);
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
						log.error("SsidProfileImpl.isEnableVpnTunnel", errMsg);
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
		return ssidProfile.isEnableSingleSsid() ? false : (ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_PSK);
	}
	
	public boolean isConfigPrivatePsk(){
		return ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_PSK;
	}
	
	public boolean isEnableRadiusAuth(){
		return ssidProfile.getEnabledUseGuestManager();
	}
	
	public AuthMethodType getPskAuthMethod(){
		if(ssidProfile.getPersonPskRadiusAuth() == Cwp.AUTH_METHOD_CHAP){
			return AuthMethodType.chap;
		}else if(ssidProfile.getPersonPskRadiusAuth() == Cwp.AUTH_METHOD_MSCHAPV2){
			return AuthMethodType.msChapV2;
		}else{
			return AuthMethodType.pap;
		}
	}
	
	public int getPskUserLimit(){
		return ssidProfile.getSsidSecurity().getPskUserLimit();
	}
	
	public boolean isPPSKMacBindingEnable(){
		return ssidProfile.getSsidSecurity().isBlnMacBindingEnable();
	}
	
	public boolean isPPSKExternalServerEnable(){
		return ssidProfile.isEnabledIDM();
	}
	
	public int getRoamingUpdateInterval(){
		return ssidProfile.getUpdateInterval();
	}
	
	public int getRoamingAgeout(){
		return ssidProfile.getAgeOut();
	}
	
	public boolean isConfigLocalCacheTimeOut(){
		return ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_8021X;
	}
	
	public int getLocalCacheTimeOut(){
		return ssidProfile.getLocalCacheTimeout();
	}
	
	public int getEapTimeOut(){
		return ssidProfile.getEapTimeOut();
	}
	
	public int getEapRetries(){
		return ssidProfile.getEapRetries();
	}
	
	public boolean isMacBasedAuthEnable() {
		return ssidProfile.isMacAuthEnabled();
	}
	
	public boolean isConfigFallbackToEcwp(){
		return hiveAp.getDownloadInfo().isEcwpDepaul() || hiveAp.getDownloadInfo().isEcwpNnu();
	}
	
	public boolean isEnableFallbackToEcwp(){
		return ssidProfile.getFallBackToEcwp();
	}
	
	public AuthMethodType getMacAuthType(){
		if(!ssidProfile.isMacAuthEnabled()){
			return null;
		}else{
			if(ssidProfile.getPersonPskRadiusAuth() == Cwp.AUTH_METHOD_MSCHAPV2){
				return AuthMethodType.msChapV2;
			}else if(ssidProfile.getPersonPskRadiusAuth() == Cwp.AUTH_METHOD_CHAP){
				return AuthMethodType.chap;
			}else{
				return AuthMethodType.pap;
			}
		}
	}
	
	public boolean isConfigCwpRegUserProfile() {
		return (regUserProfile != null && cwp.getRegistrationType() != Cwp.REGISTRATION_TYPE_EXTERNAL)
			|| (regUserProfile != null && cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK
					&& ssidProfile.isEnablePpskSelfReg());
	}
	
	public int getCwpRegUserProfileAttr() {
		return regUserProfile.getAttributeValue();
	}
	
	public boolean isConfigCwpAuthUserProfile() {
//		return authUserProfile != null && cwp.getRegistrationType() != Cwp.REGISTRATION_TYPE_EXTERNAL;
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
		if (wepSharedList == null) {
			wepSharedList = new ArrayList<String>();
			wepSharedList.add(ssidProfile.getSsidSecurity().getFirstKeyValue());
			wepSharedList
					.add(ssidProfile.getSsidSecurity().getSecondKeyValue());
			wepSharedList.add(ssidProfile.getSsidSecurity().getThirdKeyValue());
			wepSharedList.add(ssidProfile.getSsidSecurity().getFourthValue());
		}
		return wepSharedList.size();
	}
	
	public boolean isConfigureProtocolWep(int index) {
		String keyValue = wepSharedList.get(index);
		return (keyValue != null) && (!keyValue.equals(""));
	}
	
	public boolean isConfigureProtocolAscii() {
		if(ssidProfile.getLocalUserGroups() != null && ssidProfile.getLocalUserGroups().size() > 0){
			return true;
		}else if(ssidProfile.isEnableSingleSsid()){
			return true;
		}else{
			return ssidSecurity.getKeyType() == 0;
		}
	}
	
	public boolean isConfigureProtocolHex() {
		return !isConfigureProtocolAscii();
	}
	
	public String getProtocolWepValue(int i) throws IOException {
		return AhConfigUtil.hiveApCommonEncrypt(wepSharedList.get(i));
	}
	
	public boolean isConfigureWepDefault(int i) {
		return ssidSecurity.getDefaultKeyIndex() == i + 1;
	}
	
	public int getProtocolReplayWindow() {
		return ssidSecurity.getReplayWindow();
	}
	
	public boolean isEnableProtocolLocalTkip() {
		return ssidSecurity.getLocalTkip();
	}
	
	public boolean isEnableProtocolRemoteTkip() {
		return ssidSecurity.getRemoteTkip();
	}
	
	public String getProtocolKeyValue() throws IOException {
		String keyValue = ssidProfile.isEnableSingleSsid() ? ssidProfile.getSingleSsidValue() : ssidSecurity.getFirstKeyValue();
		if(keyValue == null || "".equals(keyValue)){
			keyValue = "aerohive";
		}
		return AhConfigUtil
				.hiveApCommonEncrypt(keyValue);
	}
	
	public boolean isConfigureProtocolStrict() {
		return !ssidSecurity.getStrict();
	}
	
	public boolean isConfigureProtocolNoStrict() {
		return ssidSecurity.getStrict();
	}
	
	public int getProtocolGmkRekeyPeriod() {
		return ssidSecurity.getRekeyPeriodGMK();
	}
	
	public int getProtocolRekeyPeriod() {
		return ssidSecurity.getRekeyPeriod();
	}
	
	public int getProtocolPtkTimeout() {
		return ssidSecurity.getPtkTimeOut();
	}
	
	public int getProtocolPtkRetry() {
		return ssidSecurity.getPtkRetries();
	}
	
	public int getProtocolGtkTimeout() {
		return ssidSecurity.getGtkTimeOut();
	}
	
	public int getProtocolGtkRetry() {
		return ssidSecurity.getGtkRetries();
	}
	
	public int getPtkRekeyPeriod(){
		return ssidProfile.getSsidSecurity().getRekeyPeriodPTK();
	}
	
	public boolean isRoamingProactivePmkidResponse() {
		return ssidSecurity.getProactiveEnabled();
	}
	
	public boolean isEnableReauthInterval(){
		return ssidProfile.getSsidSecurity().getReauthInterval() > 0;
	}
	
	public int getReauthIntervalValue(){
		return ssidProfile.getSsidSecurity().getReauthInterval();
	}
	
	public boolean isCwpFromSsid(){
		return isCwpFSsid;
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
		return ssidProfile.isEnableOsDection();
	}
	
	public String getDevicePolicy(){
		return hiveAp.getMacAddress() + DEVICE_POLICY_SUFFIX;
	}
	
	public boolean isConfigPpskServer(){
		return ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_PSK 
			&& (ssidProfile.isEnablePpskSelfReg() || ssidProfile.getSsidSecurity().isBlnMacBindingEnable())
			&& ((ssidProfile.isBlnBrAsPpskServer() && hiveAp.isBranchRouter()) || 
				(ssidProfile.getPpskServer() != null && !StringUtils.isEmpty(ssidProfile.getPpskServer().getCfgIpAddress()))
				)
			&& !ssidProfile.isEnabledIDM();
	}
	
	public String getPpskServerIp(){
		if(ssidProfile.isBlnBrAsPpskServer() && hiveAp.isBranchRouter()){
			return hiveAp.getCfgIpAddress();
		}else {
			return ssidProfile.getPpskServer().getCfgIpAddress();
		}
	}
	
	public boolean isConfigPpskWebServer(){
		return (ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_OPEN || ssidProfile.isEnableSingleSsid())
					&& ssidProfile.isEnablePpskSelfReg()
					&& ssidProfile.getPpskECwp() != null
					&& !ssidProfile.isEnabledIDM();
	}
	
	public boolean isPpskWebServerHttps(){
		return ssidProfile.getPpskECwp().isEnabledHttps();
	}
	
	public String getBindToPpskSsid(){
		return ssidProfile.isEnableSingleSsid() ? ssidProfile.getSsid() : ssidProfile.getPpskOpenSsid();
	}
	
	public boolean isUseDefaultPpskPage(){
		return false;
	}
	
	public boolean isConfigPpskWebDir(){
		String dir = getPpskWebDir();
		return ssidProfile.getPpskECwp().isValidWebDirector() && dir != null && !"".equals(dir);
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
		return ssidProfile.getPpskECwp().getDirectoryName();
	}
	
	public String getPpskLoginPage(){
		return ssidProfile.getPpskECwp().getWebPageName();
	}
	
	public String getUserProfileSequence(){
		int upId = ssidProfile.getAuthSequence();
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
		return cwp.isPpskServer();
	}
	
	public boolean isConfigPpskAuthUser(){
		return ssidProfile.isEnablePpskSelfReg() && ssidProfile.getRadiusAssignmentPpsk() != null
			&& ssidProfile.getPpskServer() != null && ssidProfile.getPpskServer().getCfgIpAddress().equals(hiveAp.getCfgIpAddress());
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

	public boolean isInjectOperatorNameEnable() {
		return radiusAssignment!=null&&radiusAssignment.isInjectOperatorNmAttri();
	}
	
	public boolean isEnabled8021X(){
		return false;
	}

	public boolean isEnableAaaUserProfileMapping() {
		return ssidProfile.isEnableAssignUserProfile();
	}

	public int getUserProfileMappingAttributeId() {
		return ssidProfile.getAssignUserProfileAttributeId();
	}

	public boolean isConfigUserProfileMappingVendorId() {
		return ssidProfile.getUserProfileAttributeType() == SsidProfile.USERPROFILE_ATTRIBUTE_CUSTOMER;
	}
	
	public int getUserProfileMappingVendorId() {
		return ssidProfile.getAssignUserProfileVenderId();
	}
	
	public boolean isMdmTypeEnabled(int mdmType) {
		if (!isSupportMDM())
			return false;
		
		switch (mdmType) {
		case ConfigTemplateMdm.MDM_ENROLL_TYPE_JSS:
			return ssidProfile.isEnableMDM() && configMdm != null && (configMdm.getMdmType() == ConfigTemplateMdm.MDM_ENROLL_TYPE_JSS);
		case ConfigTemplateMdm.MDM_ENROLL_TYPE_AIRWATCH:
			return ssidProfile.isEnableMDM() && configMdm != null && (configMdm.getMdmType() == ConfigTemplateMdm.MDM_ENROLL_TYPE_AIRWATCH) 
				&& NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"6.0.2.0") >= 0;
		case ConfigTemplateMdm.MDM_ENROLL_TYPE_AEROHIVE:
			if (NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"6.0.2.0") < 0)
				return false;
			
			HMServicesSettings ss = MgrUtil.getQueryEntity().findBoByAttribute(HMServicesSettings.class, "owner", ssidProfile.getOwner());
			return ss == null ? false : ss.isEnableClientManagement() && ssidProfile.isEnableAerohiveMdm();
		}

		return false;
	}
	
	public String getMdmRootURLPath() {
		if (configMdm != null)
			return configMdm.getRootURLPath();
		if (isMdmTypeEnabled(ConfigTemplateMdm.MDM_ENROLL_TYPE_AEROHIVE))
			return hiveAp.getDownloadInfo().getMdmURLPath();
		else
			return null;
	}
	
	public String getMdmApiURL() {
		return configMdm==null?null:configMdm.getApiURL();
	}
	
	public String getMdmApiKey() {
		if (configMdm != null)
			return configMdm.getApiKey();
		
		if (isMdmTypeEnabled(ConfigTemplateMdm.MDM_ENROLL_TYPE_AEROHIVE)) {
			HMServicesSettings bo = hiveAp.getDownloadInfo().isHHMApp() ? MgrUtil.getQueryEntity().findBoByAttribute(HMServicesSettings.class, "owner", ssidProfile.getOwner()) : 
				MgrUtil.getQueryEntity().findBoByAttribute(HMServicesSettings.class, "owner.domainName", HmDomain.HOME_DOMAIN);
			return bo == null ? null : bo.getApiKey();
		}
		else
			return null;
	}
	
	public String getMdmApiInstanceId() {
		if (configMdm != null) {
			log.error("[SecurityObjectProfileImpl] error found, ApiInstanceId is only available for Aerohive MDM, and configMdm ought to be null");
			return null;
		}
		
		if (isMdmTypeEnabled(ConfigTemplateMdm.MDM_ENROLL_TYPE_AEROHIVE)) {
			return hiveAp.getDownloadInfo().getVhmInstanceId();
		}
		else
			return null;
	}
	
	public String getMdmHttpAuthUser() {
		return configMdm==null?null:configMdm.getMdmUserName();
	}
	
	public String getMdmHttpAuthPassword() {
		return configMdm==null?null:configMdm.getMdmPassword();
	}
	
	public boolean isEnableUsePolicy(){
		return cwp.isEnableUsePolicy();
	}
	
	public String getMdmOsObject(short i){
		switch (i) {
		case MDM_OSOBJECT_IOS: 			return MDM_OBJECT_IOS;
		case MDM_OSOBJECT_MACOS:		return MDM_OBJECT_MACOS;
		default:						return null;
		}
	}
	
	public boolean isConfigMdmOsObject(short i){
		switch (i) {
		case MDM_OSOBJECT_IOS: 			return configMdm==null?false:configMdm.isEnableAppleOs();
		case MDM_OSOBJECT_MACOS:		return NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"5.1.1.0") > 0 && configMdm==null?false:configMdm.isEnableMacOs();
		case MDM_OSOBJECT_SYMBIAN:		return NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"6.0.0.0") > 0 && configMdm==null?false:configMdm.isEnableSymbianOs();
		case MDM_OSOBJECT_BLACKBERRY:	return NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"6.0.0.0") > 0 && configMdm==null?false:configMdm.isEnableBlackberryOs();
        case MDM_OSOBJECT_ANDROID:		return NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"6.0.0.0") > 0 && configMdm==null?false:configMdm.isEnableAndroidOs();
        case MDM_OSOBJECT_WINDOWSPHONE:	return NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"6.1.0.0") > 0 && configMdm==null?false:configMdm.isEnableWindowsphoneOs();
		default:						return false;
		}
	}
	
	public boolean isEnable80211w(){
		return ssidSecurity.isEnable80211w();
	}
	
	public boolean isEnableBip(){
		return ssidSecurity.isEnableBip();
	}
	
	public boolean isConfigmfpMandatory(){
		return ssidSecurity.getWpa2mfpType() == WPA2_80211W_MODE_MANDATORY;
	}
	
	public boolean isConfigmfpOptional(){
		return ssidSecurity.getWpa2mfpType() == WPA2_80211W_MODE_OPTIONAL;
	}
	
	public CwpMultiLanguageValue getCWPLanguageValue(){
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
	
	//11r is only enabled under ACCESS_MODE_8021X
	@Override
	public boolean isEnabled80211r() {
		return ssidProfile.isEnabled80211r() && 
				(ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X 
						|| ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK
						|| ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_PSK
						|| ssidProfile.getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
				);
	}
	
	
	@Override
	public boolean isSupportMDM() {
		if (NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.0.0.0") >= 0) {
			if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100
					|| (hiveAp.isCVGAppliance() && hiveAp
							.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR200)) {
				return false;
			}
			return true;
		} else if (NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "5.1.1.0") >= 0) {
			if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100
					|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200
					|| hiveAp.isCVGAppliance()) {
				return false;
			}
			return true;
		} else
			return false;
	}
	
	@Override
	public boolean isConfigCwpAnonymousAccess() {
		return ssidProfile.getAccessMode() == SsidProfile.ACCESS_MODE_OPEN && 
				ssidProfile.isEnabledIDM() && 
				ssidProfile.getCwp() != null && 
				ssidProfile.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA;
	}
	
	public boolean isConfigSelfRegViaIdm(){
		return (ssidProfile.isEnabledIDM() || 
					(ssidProfile.getParentPpskSsid() != null && ssidProfile.getParentPpskSsid().isEnabledIDM()) 
				) && 
				ssidProfile.getCwp() != null && 
				getSelfRegViaIdmModel() != null;
	}
	
	private String getSelfRegViaIdmModel(){
		if(ssidProfile.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED){
			return "self-register";
		}else if(ssidProfile.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH){
			return "both";
		}else if(ssidProfile.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK){
			return "ppsk";
		}else{
			return null;
		}
	}
	
	public String getOnboardSsid() {
		if(ssidProfile.isEnableSingleSsid()){
			return ssidProfile.getSsid();
		}
		switch (ssidProfile.getAccessMode()) {
		case SsidProfile.ACCESS_MODE_8021X:
			return ssidProfile.getSsid();
		case SsidProfile.ACCESS_MODE_OPEN:
			return ssidProfile.getOnboardSsid();
		default:
			return null;
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
	
	public boolean isEnablePPSKSelfRegister(){
		return ssidProfile.isEnablePpskSelfReg() && !ssidProfile.isEnabledIDM();
	}
	
	public boolean isEnableMdmNonCompliance() {
		if (mdmAirwatchNonCompliance == null) {
			return false;
		}
		return mdmAirwatchNonCompliance.isEnabledNonCompliance();
	}
	
	public int getMdmGuestUserProfileId() {
	    if (ssidProfile == null || ssidProfile.getUserProfileGuest() == null) {
	        return -1;
	    }
	    return ssidProfile.getUserProfileGuest().getAttributeValue();
    }

    @Override
    public boolean isEnableMdmDisconnectForVlanChange() {
        return mdmAirwatchNonCompliance != null && mdmAirwatchNonCompliance.isDisconnectVlanChanged();
    }

    @Override
    public int getMdmPollStatusInterval() {
        if (mdmAirwatchNonCompliance == null) {
           return -1;
        }
        return mdmAirwatchNonCompliance.getPollingInterval();
    }

    @Override
    public boolean isEnableMdmSendMessageViaEmail() {
        return mdmAirwatchNonCompliance != null && mdmAirwatchNonCompliance.isNotifyViaEmail();
    }

    @Override
    public boolean isEnableMdmSendMessageViaPush() {
        return mdmAirwatchNonCompliance != null && mdmAirwatchNonCompliance.isNotifyViaPush();
    }

    @Override
    public boolean isEnableMdmSendMessageViaSms() {
        return mdmAirwatchNonCompliance != null && mdmAirwatchNonCompliance.isNotifyViaSMS();
    }

    @Override
    public String getMdmSendMessageTitle() {
        return (mdmAirwatchNonCompliance == null) ? "" : mdmAirwatchNonCompliance.getTitle();
    }

    @Override
    public String getMdmSendMessageContent() {
        return (mdmAirwatchNonCompliance == null) ? "" : mdmAirwatchNonCompliance.getContent();
    }
	
    public boolean isConfigPortBased(){
		return !isConfigHostBased();
	}
	
	public boolean isConfigHostBased(){
		return hiveAp.isEnabledSameVlan();
	}
	
	public boolean isConfigAuthMethod() {
		return isEthernetProfile() && (hiveAp.isEthCwpEnableEthCwp() || hiveAp.isEthCwpEnableMacAuth()) && NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"6.1.1.0") > 0;
	}

	public boolean isEthernetProfile() {
		return isEthernetProfile && (HiveAp.is350HiveAP(hiveAp.getHiveApModel()) || HiveAp.is330HiveAP(hiveAp.getHiveApModel()) 
				|| HiveAp.HIVEAP_MODEL_230 == hiveAp.getHiveApModel());
	}

	public void setEthernetProfile(boolean isEthernetProfile) {
		this.isEthernetProfile = isEthernetProfile;
	}
	
	public SsidProfile getSsidProfile(){
		return this.ssidProfile;
	}
}