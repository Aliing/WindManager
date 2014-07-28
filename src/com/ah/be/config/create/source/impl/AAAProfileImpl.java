package com.ah.be.config.create.source.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.cloudauth.IDMConfig;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.AAAProfileInt;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.RadiusAttrs;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.VpnService;
import com.ah.bo.useraccess.ActiveDirectoryDomain;
import com.ah.bo.useraccess.ActiveDirectoryOrLdapInfo;
import com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap;
import com.ah.bo.useraccess.LdapServerOuUserProfile;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusHiveapAuth;
import com.ah.bo.useraccess.RadiusLibrarySipRule;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.useraccess.RadiusProxyRealm;
import com.ah.bo.useraccess.RadiusServer;
import com.ah.bo.wlan.SsidProfile;
import com.ah.util.EnumConstUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
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
@SuppressWarnings("static-access")
public class AAAProfileImpl implements AAAProfileInt {
	
	private static final Tracer log = new Tracer(AAAProfileImpl.class
			.getSimpleName());
	
	public static final String PRIMARY_SUFIX = "_primary";
	public static final String BACKUP_SUFIX = "_backup";
	public static final int REALM_LENGTH=23;
	public static final String AUTH_PRIMARY_SUFIX = "_aprimary";
	public static final String AUTH_BACKUP_SUFIX = "_abackup";

	private final HiveAp hiveAp;
	private RadiusAssignment radiusAssignment;
	private RadiusServer servicesFirst;
	private RadiusServer servicesSecond;
	private RadiusServer servicesThird;
	private RadiusServer servicesFourth;
	
	private RadiusServer acctServicesFirst;
	private RadiusServer acctServicesSecond;
	private RadiusServer acctServicesThird;
	private RadiusServer acctServicesFourth;

	private RadiusOnHiveap radiusServerProfile;
	private ActiveDirectoryOrOpenLdap primaryObj;
	private ActiveDirectoryOrOpenLdap backup1Obj;
	private ActiveDirectoryOrOpenLdap backup2Obj;
	private ActiveDirectoryOrOpenLdap backup3Obj;

	private final List<RadiusHiveapAuth> radiusServerNAS = new ArrayList<RadiusHiveapAuth>();
	private List<LocalUserGroup> localUserGroupList;
	private final List<LocalUser> localUserList = new ArrayList<LocalUser>();

	private MgmtServiceOption mgmtServiceOpt;
	private RadiusProxy radProxy;
	private Set<ProxyRadiusServer> proxySet = new HashSet<ProxyRadiusServer>();
	private ProxyRadiusServer[] proxyArray;
	private RadiusAssignment ppskRadioServer = null;
	private RadiusServer ppskRadiusPrimary;
	private RadiusServer ppskRadiusBackup1;
	private RadiusServer ppskRadiusBackup2;
	private RadiusServer ppskRadiusBackup3;
	
	private IDMConfig radSecConfig;
	
	private boolean versionAfter61r3;

	public AAAProfileImpl(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
		initialize();
	}
	
	public List<LocalUser> getAllRadiusUser(){
		return this.localUserList;
	}

	private void initialize() {
		versionAfter61r3 = NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.2.0") >= 0;
	    radSecConfig = hiveAp.getDownloadInfo().getIdmRadSecConfig();
	    
		mgmtServiceOpt = hiveAp.getConfigTemplate().getMgmtServiceOption();
		if(mgmtServiceOpt != null && (mgmtServiceOpt.getUserAuth() == EnumConstUtil.ADMIN_USER_AUTHENTICATION_RADIUS ||
				mgmtServiceOpt.getUserAuth() == EnumConstUtil.ADMIN_USER_AUTHENTICATION_BOTH)){
			radiusAssignment = mgmtServiceOpt.getRadiusServer();
		}

		if (radiusAssignment != null) {
			for (RadiusServer radiuServerObj : radiusAssignment.getServices()) {
				switch (radiuServerObj.getServerPriority()) {
				case RadiusServer.RADIUS_PRIORITY_PRIMARY:
					if(radiuServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_ACCT){
						acctServicesFirst = radiuServerObj;
					}else if(radiuServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH){
						servicesFirst = radiuServerObj;
					}else if(radiuServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH){
						acctServicesFirst = radiuServerObj;
						servicesFirst = radiuServerObj;
					}
					break;
				case RadiusServer.RADIUS_PRIORITY_BACKUP1:
					if(radiuServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_ACCT){
						acctServicesSecond = radiuServerObj;
					}else if(radiuServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH){
						servicesSecond = radiuServerObj;
					}else if(radiuServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH){
						acctServicesSecond = radiuServerObj;
						servicesSecond = radiuServerObj;
					}
					break;
				case RadiusServer.RADIUS_PRIORITY_BACKUP2:
					if(radiuServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_ACCT){
						acctServicesThird = radiuServerObj;
					}else if(radiuServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH){
						servicesThird = radiuServerObj;
					}else if(radiuServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH){
						acctServicesThird = radiuServerObj;
						servicesThird = radiuServerObj;
					}
					break;
				case RadiusServer.RADIUS_PRIORITY_BACKUP3:
					if(radiuServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_ACCT){
						acctServicesFourth = radiuServerObj;
					}else if(radiuServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH){
						servicesFourth = radiuServerObj;
					}else if(radiuServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH){
						acctServicesFourth = radiuServerObj;
						servicesFourth = radiuServerObj;
					}
					break;
				}
			}
		}
		
		if(hiveAp.isEnabledBrAsRadiusServer()){
			if(hiveAp.isOverWriteRadiusServer()){
				radiusServerProfile = hiveAp.getRadiusServerProfile();
			}else{
				radiusServerProfile = null;
			}
		}else{
			radiusServerProfile = hiveAp.getRadiusServerProfile();
		}

		if(hiveAp.getRadiusServerProfile() != null){
			radiusServerProfile = hiveAp.getRadiusServerProfile();
		}

		if (radiusServerProfile != null) {
			radiusServerNAS.addAll(radiusServerProfile.getIpOrNames());

			for (ActiveDirectoryOrLdapInfo infoObj : radiusServerProfile
					.getDirectoryOrLdap()) {
				if (infoObj.getServerPriority() == RadiusServer.RADIUS_PRIORITY_PRIMARY) {
					primaryObj = infoObj.getDirectoryOrLdap();
				} else if (infoObj.getServerPriority() == RadiusServer.RADIUS_PRIORITY_BACKUP1) {
					backup1Obj = infoObj.getDirectoryOrLdap();
				} else if (infoObj.getServerPriority() == RadiusServer.RADIUS_PRIORITY_BACKUP2) {
					backup2Obj = infoObj.getDirectoryOrLdap();
				} else if (infoObj.getServerPriority() == RadiusServer.RADIUS_PRIORITY_BACKUP3) {
					backup3Obj = infoObj.getDirectoryOrLdap();
				}
			}
		}

		if(radiusServerProfile != null){
			
			short databaseType = radiusServerProfile.getDatabaseType();
			
			//local all local user groups from lldp server
			Map<String, LocalUserGroup> userGroupMap = new HashMap<String, LocalUserGroup>();
			if(this.isConfigureAttrMap() && radiusServerProfile.getMapByGroupOrUser() == RadiusOnHiveap.RADIUS_SERVER_MAP_BY_GROUPATTRI &&
					radiusServerProfile.getLdapOuUserProfiles() != null){
				for(LdapServerOuUserProfile ldapServer : radiusServerProfile.getLdapOuUserProfiles()){
					if(!userGroupMap.containsKey(ldapServer.getLocalUserGroup().getGroupName()) && ldapServer.getLocalUserGroup().getUserType() != LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
						userGroupMap.put(ldapServer.getLocalUserGroup().getGroupName(), ldapServer.getLocalUserGroup());
					}
				}
			}
			//local local user groups from local database
			if(databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL || 
					databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_ACTIVE ||
					databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN){
				if(radiusServerProfile.getLocalUserGroup() != null){
					for(LocalUserGroup userGroup : radiusServerProfile.getLocalUserGroup()){
						if(!userGroupMap.containsKey(userGroup.getGroupName()) && userGroup.getUserType() != LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK){
							userGroupMap.put(userGroup.getGroupName(), userGroup);
						}
					}
				}
			}
			//local all local user groups from Sip server
			if(radiusServerProfile.isLibrarySipCheck() && radiusServerProfile.getSipPolicy() != null){
				LocalUserGroup defUserGroup = radiusServerProfile.getSipPolicy().getDefUserGroup();
				if(defUserGroup != null && !userGroupMap.containsKey(defUserGroup.getGroupName())){
					userGroupMap.put(defUserGroup.getGroupName(), defUserGroup);
				}
				if(radiusServerProfile.getSipPolicy().getRules() != null){
					for(RadiusLibrarySipRule sipRule : radiusServerProfile.getSipPolicy().getRules()){
						if(sipRule.getUserGroup() != null && !userGroupMap.containsKey(sipRule.getUserGroup().getGroupName())){
							userGroupMap.put(sipRule.getUserGroup().getGroupName(), sipRule.getUserGroup());
						}
					}
				}
			}
			
			localUserGroupList = new ArrayList<LocalUserGroup>(userGroupMap.values());
			
			//load all local users
			if(localUserGroupList.size() > 0 ){
				localUserList.addAll(
						MgrUtil.getQueryEntity().executeQuery(LocalUser.class,
								null, 
								new FilterParams("localUserGroup", localUserGroupList), 
								hiveAp.getOwner().getId())
				);
			}
			
		}
		
		radProxy = hiveAp.getRadiusProxyProfile();
		if(radProxy != null){
			String serverName;
			for(RadiusProxyRealm realmObj : radProxy.getRadiusRealm()){
				if(realmObj.isProxy4RadSec()){
					proxySet.add(new ProxyRadiusServer(realmObj.getServerName()+PRIMARY_SUFIX, realmObj.isProxy4RadSec(), radSecConfig.getIdmGatewayServer()));
				}
				
				if(realmObj.getRadiusServer() != null){
					serverName = realmObj.getRadiusServer().getRadiusName().trim();
					serverName = serverName.substring(0,serverName.length() > REALM_LENGTH ? REALM_LENGTH : serverName.length());
					RadiusServer primary = null;
					RadiusServer backup = null;
					int primaryPiro = 999, backupPiro = 999;
					for(RadiusServer radServer : realmObj.getRadiusServer().getServices()){
						if((radServer.getSharedSecret() == null || "".equals(radServer.getSharedSecret())) && !realmObj.isUseIDM()){
							continue;
						}
						if(radServer.getServerPriority() < primaryPiro){
							backupPiro = primaryPiro;
							backup = primary;
							
							primaryPiro = radServer.getServerPriority();
							primary = radServer;
						}else if(radServer.getServerPriority() < backupPiro){
							backupPiro = radServer.getServerPriority();
							backup = radServer;
						}
					}
					if(primary != null){
						if(realmObj.isIdmAuthProxy()){
							ProxyRadiusServer proxyServer = new ProxyRadiusServer(serverName+AUTH_PRIMARY_SUFIX, primary);
							proxyServer.setAuthProxy(realmObj.isIdmAuthProxy());
							proxySet.add(proxyServer);
						}else{
							proxySet.add(new ProxyRadiusServer(serverName+PRIMARY_SUFIX, primary));
						}
					}
					if(backup != null){
						if(realmObj.isIdmAuthProxy()){
							ProxyRadiusServer proxyServer = new ProxyRadiusServer(serverName+AUTH_BACKUP_SUFIX, backup);
							proxyServer.setAuthProxy(realmObj.isIdmAuthProxy());
							proxySet.add(proxyServer);
						}else{
							proxySet.add(new ProxyRadiusServer(serverName+BACKUP_SUFIX, backup));
						}
					}
				}
			}
			
			if(!proxySet.isEmpty()){
				proxyArray = proxySet.toArray(new ProxyRadiusServer[0]);
			}
			
			if(radProxy.getRadiusNas() != null){
				radiusServerNAS.addAll(radProxy.getRadiusNas());
			}
		}
		
		for(ConfigTemplateSsid ssidMap : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
			SsidProfile ssidObj = ssidMap.getSsidProfile();
			if(ssidObj != null && ssidObj.getAccessMode() == SsidProfile.ACCESS_MODE_PSK 
					&& ssidObj.isEnablePpskSelfReg() && ssidObj.getPpskServer() != null 
					&& ssidObj.getPpskServer().getCfgIpAddress() != null
					&& ssidObj.getPpskServer().getCfgIpAddress().equals(hiveAp.getCfgIpAddress())
					&& ssidObj.getRadiusAssignmentPpsk() != null 
					&& ssidObj.getRadiusAssignmentPpsk().getServices() != null 
					&& !ssidObj.getRadiusAssignmentPpsk().getServices().isEmpty()){
				ppskRadioServer = ssidObj.getRadiusAssignmentPpsk();
				break;
			}
		}
		if(ppskRadioServer != null && ppskRadioServer.getServices() != null){
			for (RadiusServer radiusServerObj : ppskRadioServer.getServices()){
				if (radiusServerObj.getServerPriority() == RadiusServer.RADIUS_PRIORITY_PRIMARY){
					if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH ||
							radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH){
						ppskRadiusPrimary = radiusServerObj;
					}
				}else if(radiusServerObj.getServerPriority() == RadiusServer.RADIUS_PRIORITY_BACKUP1){
					if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH ||
							radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH){
						ppskRadiusBackup1 = radiusServerObj;
					}
				}else if(radiusServerObj.getServerPriority() == RadiusServer.RADIUS_PRIORITY_BACKUP2){
					if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH ||
							radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH){
						ppskRadiusBackup2 = radiusServerObj;
					}
				}else if(radiusServerObj.getServerPriority() == RadiusServer.RADIUS_PRIORITY_BACKUP3){
					if(radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH ||
							radiusServerObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH){
						ppskRadiusBackup3 = radiusServerObj;
					}
				}
			}
		}
		
		// fix bug 27146, code remove.
//		if(this.isConfigProxyRadsec() && !hiveAp.isIDMProxy()){
//			RadiusServer radServer = new RadiusServer();
//			IpAddress ipAddress = CLICommonFunc.getGlobalIpAddress(hiveAp.getCfgIpAddress(), hiveAp.getNetmask());
//			radServer.setIpAddress(ipAddress);
//			if(servicesFirst == null){
//				servicesFirst = radServer;
//			}else if(servicesSecond == null){
//				servicesSecond = radServer;
//			}else if(servicesThird == null){
//				servicesThird = radServer;
//			}else if(servicesFourth == null){
//				servicesFourth = radServer;
//			}
//		}
		
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
	
	public String getRadiusGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.radiusService");
	}
	
	public String getRadiusName(){
		if(radiusServerProfile != null){
			return radiusServerProfile.getRadiusName();
		}else{
			return null;
		}
	}
	
	public String getRadiusAssGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.radiusServerAssignments");
	}
	
	public String getRadiusAssName(){
		if(radiusAssignment != null){
			return radiusAssignment.getRadiusName();
		}else{
			return null;
		}
	}
	
	public String getMgmtServiceGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.mgmtServiceOption");
	}
	
	public String getMgmtServiceName(){
		if(mgmtServiceOpt != null){
			return mgmtServiceOpt.getMgmtName();
		}else{
			return null;
		}
	}

	public String getApVersion() {
		return hiveAp.getSoftVer();
	}

	public boolean isConfigureAAAProfile() {
		return radiusAssignment != null || null != hiveAp.getRadiusServerProfile()/*hiveAp.isRadiusServer()*/;
	}

	public String getAAAUpdateTime() {
		// List<Object> aaaDataObj= new ArrayList<Object>();
		// aaaDataObj.add(hiveAp);
		// aaaDataObj.add(radiusAssignment);
		// aaaDataObj.add(radiusServerProfile);
		// aaaDataObj.add(primaryObj);
		// aaaDataObj.add(backup1Obj);
		// aaaDataObj.add(backup2Obj);
		// aaaDataObj.add(backup3Obj);
		// if(localUserGroupList != null){
		// aaaDataObj.addAll(localUserGroupList);
		// }
		// if(localUserList != null){
		// aaaDataObj.addAll(localUserList);
		// }
		// return String.valueOf(CLICommonFunc.getLastUpdateTime(aaaDataObj));
		return CLICommonFunc.getLastUpdateTime(null);
	}

	public String getLocalUpdateTime() {
		// List<Object> aaaDataObj= new ArrayList<Object>();
		// aaaDataObj.add(radiusServerProfile);
		// aaaDataObj.add(primaryObj);
		// aaaDataObj.add(backup1Obj);
		// aaaDataObj.add(backup2Obj);
		// aaaDataObj.add(backup3Obj);
		// if(localUserGroupList != null){
		// aaaDataObj.addAll(localUserGroupList);
		// }
		// if(localUserList != null){
		// aaaDataObj.addAll(localUserList);
		// }
		// return CLICommonFunc.getLastUpdateTime(aaaDataObj);
		return CLICommonFunc.getLastUpdateTime(null);
	}

	private RadiusServer getRadiusServerByType(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.primary) {
			return this.servicesFirst;
		} else if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1) {
			return this.servicesSecond;
		} else if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2) {
			return this.servicesThird;
		} else if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3) {
			return this.servicesFourth;
		} else {
			return null;
		}
	}
	
	private RadiusServer getAcctRadiusServerByType(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.primary) {
			return this.acctServicesFirst;
		} else if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1) {
			return this.acctServicesSecond;
		} else if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2) {
			return this.acctServicesThird;
		} else if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3) {
			return this.acctServicesFourth;
		} else {
			return null;
		}
	}

	public boolean isConfigureRetryInterval() {
		return radiusAssignment != null;
	}

	public boolean isConfigurePriority(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		RadiusServer serverObj = getRadiusServerByType(priorityType);
		return serverObj != null && (serverObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH || 
				serverObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_AUTH);
	}
	
	public boolean isConfigAcctPriority(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		RadiusServer serverObj = getAcctRadiusServerByType(priorityType);
		return serverObj != null && serverObj.getServerType() != RadiusServer.RADIUS_SERVER_TYPE_AUTH;
	}

	public String getServerIp(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType)
			throws CreateXMLException {
		RadiusServer serverObj = getRadiusServerByType(priorityType);
		if (serverObj != null) {
			if (serverObj.getIpAddress() != null) {
				return CLICommonFunc.getIpAddress(serverObj.getIpAddress(),
						hiveAp).getIpAddress();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public String getAcctServerIp(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws CreateXMLException {
		RadiusServer serverObj = getAcctRadiusServerByType(priorityType);
		if (serverObj != null) {
			if (serverObj.getIpAddress() != null) {
				return CLICommonFunc.getIpAddress(serverObj.getIpAddress(),
						hiveAp).getIpAddress();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public boolean isConfigSharedSecret(RADIUS_PRIORITY_TYPE priorityType){
		RadiusServer serverObj = getRadiusServerByType(priorityType);
		return serverObj != null && serverObj.getSharedSecret() != null && !"".equals(serverObj.getSharedSecret());
	}
	
	public boolean isConfigAcctSharedSecret(RADIUS_PRIORITY_TYPE priorityType){
		RadiusServer serverObj = getAcctRadiusServerByType(priorityType);
		return serverObj != null && serverObj.getSharedSecret() != null && !"".equals(serverObj.getSharedSecret());
	}
	
	public boolean isConfigSharedSecretOld(RADIUS_PRIORITY_TYPE priorityType) throws CreateXMLException{
		RadiusServer serverObj = getRadiusServerByType(priorityType);
		if(NmsUtil.compareSoftwareVersion("3.4.0.0", hiveAp.getSoftVer()) > 0 && 
				(serverObj.getSharedSecret() == null || "".equals(serverObj.getSharedSecret()))
			){
			String errMsg = NmsUtil.getUserMessage("error.be.config.create.emptyRadiusClientPassword");
			log.error("AAAProfileImpl", errMsg);
			throw new CreateXMLException(errMsg);
		}
		return serverObj != null && serverObj.getSharedSecret() != null && !"".equals(serverObj.getSharedSecret());
	}

	public String getSharedSecret(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws IOException {
		RadiusServer serverObj = getRadiusServerByType(priorityType);
		if (serverObj == null) {
			return null;
		}
		return serverObj.getSharedSecret();
	}
	
	public String getAcctSharedSecret(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws IOException {
		RadiusServer serverObj = getAcctRadiusServerByType(priorityType);
		if (serverObj == null) {
			return null;
		}
		return serverObj.getSharedSecret();
	}

	public int getAuthPort(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		RadiusServer serverObj = getRadiusServerByType(priorityType);
		if (serverObj == null) {
			return -1;
		}
		return serverObj.getAuthPort();
	}

	public boolean isConfigureAcctPort(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		RadiusServer serverObj = getRadiusServerByType(priorityType);
		return serverObj != null && (serverObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_ACCT || 
				serverObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH);
	}
	
	public boolean isConfigureAcctRadAcctPort(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		RadiusServer serverObj = getAcctRadiusServerByType(priorityType);
		return serverObj != null && (serverObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_ACCT || 
				serverObj.getServerType() == RadiusServer.RADIUS_SERVER_TYPE_BOTH);
	}
	
	public int getAcctPort(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		RadiusServer serverObj = getRadiusServerByType(priorityType);
		return serverObj.getAcctPort();
	}

	public int getAcctAcctPort(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		RadiusServer serverObj = getAcctRadiusServerByType(priorityType);
		return serverObj.getAcctPort();
	}

	public int getRetryInterval() {
		return radiusAssignment.getRetryInterval();
	}

	public int getAccountInterimInterval() {
		int interval = radiusAssignment.getUpdateInterval();
		String apVersion = this.getApVersion();
		int iVersion = 0;
		if(apVersion != null && !"".equals(apVersion)){
			iVersion = Integer.valueOf(apVersion.replace(".", ""));
		}
		if(iVersion < 3020 && interval < 600){
			return 600;
		}else{
			return interval;
		}
	}

	public boolean isConfigureLocal() {
		return null != hiveAp.getRadiusServerProfile() || radProxy != null;
	}
	
	public boolean isConfigRadiusServer(){
		return null != hiveAp.getRadiusServerProfile();
	}

	public boolean isRadiusServerEnable() {
		if(radiusServerProfile != null && radiusServerProfile.getServerEnable()){
			return true;
		}
		if(radProxy != null && radProxy.getRadiusRealm() != null){
			for(RadiusProxyRealm realm : radProxy.getRadiusRealm()){
				if(!realm.isProxy4RadSec() && !realm.isIdmAuthProxy()){
					return true;
				}
			}
		}
		return false;
	}

	public int getLocalPort() {
		return radiusServerProfile.getServerPort();
	}

	private List<STA_AUTH_TYPE> turnSTAauthType(short AuthType) {
		List<STA_AUTH_TYPE> authTypeList = new ArrayList<STA_AUTH_TYPE>();
		if(AuthType == RadiusOnHiveap.RADIUS_AUTH_TYPE_ALL_ADDED_MD5) {
			authTypeList.add(STA_AUTH_TYPE.tls);
			authTypeList.add(STA_AUTH_TYPE.ttls);
			authTypeList.add(STA_AUTH_TYPE.peap);
			authTypeList.add(STA_AUTH_TYPE.leap);
			authTypeList.add(STA_AUTH_TYPE.md5);
		}else if (AuthType == RadiusOnHiveap.RADIUS_AUTH_TYPE_ALL) {
			authTypeList.add(STA_AUTH_TYPE.tls);
			authTypeList.add(STA_AUTH_TYPE.ttls);
			authTypeList.add(STA_AUTH_TYPE.peap);
			authTypeList.add(STA_AUTH_TYPE.leap);
		} else if (AuthType == RadiusOnHiveap.RADIUS_AUTH_TYPE_PEAP) {
			authTypeList.add(STA_AUTH_TYPE.tls);
			authTypeList.add(STA_AUTH_TYPE.peap);
		} else if (AuthType == RadiusOnHiveap.RADIUS_AUTH_TYPE_TTLS) {
			authTypeList.add(STA_AUTH_TYPE.tls);
			authTypeList.add(STA_AUTH_TYPE.ttls);
		} else if (AuthType == RadiusOnHiveap.RADIUS_AUTH_TYPE_TLS) {
			authTypeList.add(STA_AUTH_TYPE.tls);
		} else if (AuthType == RadiusOnHiveap.RADIUS_AUTH_TYPE_LEAP) {
			authTypeList.add(STA_AUTH_TYPE.leap);
		}
		return authTypeList;
	}

	public boolean isConfigureSTAauthType(STA_AUTH_TYPE type) {
		short authType =-1;
		if(radiusServerProfile == null && this.radProxy != null){
			authType = -1;
		}else if(radiusServerProfile != null){
			authType = radiusServerProfile.getAuthType();
		}
		return turnSTAauthType(authType)
				.contains(type);
	}
	
	public boolean isConfigCaCert(){
		return radiusServerProfile != null && radiusServerProfile.getCaCertFile() != null && !"".equals(radiusServerProfile.getCaCertFile()) && 
			radiusServerProfile.getAuthType() != RadiusOnHiveap.RADIUS_AUTH_TYPE_LEAP;
	}

	public String getSTAauthCaCertFile() {
		return radiusServerProfile.getCaCertFile();
	}

	public String getSTAauthServerCertFile() {
		return radiusServerProfile.getServerFile();
	}

	public String getSTAauthPrivateKey() {
		return radiusServerProfile.getKeyFile();
	}

	public boolean isConfigSTAauthPrivateKeyPassword(){
		return radiusServerProfile.getKeyPassword() != null &&
			!"".equals(radiusServerProfile.getKeyPassword());
	}

	public String getSTAauthPrivateKeyPassword() {
		return radiusServerProfile.getKeyPassword();
	}

	public boolean isConfigureNAS() {
		return radiusServerNAS != null && radiusServerNAS.size() > 0;
	}

	public int getNASSize() {
		return radiusServerNAS.size();
	}

	public String getNASIpAddress(int index) throws CreateXMLException {
		RadiusHiveapAuth hiveApAuth = radiusServerNAS.get(index);
		
		IpAddress ipAddress = hiveApAuth.getIpAddress();
		SingleTableItem ipAddItem = CLICommonFunc.getIpAddress(ipAddress, hiveAp);
		String res = null;
		if(ipAddress.getTypeFlag() == IpAddress.TYPE_IP_NETWORK){
			String ip = ipAddItem.getIpAddress().trim();
			String netMask = ipAddItem.getNetmask().trim();
			if (netMask != null && !"".equals(netMask)) {
				ip = CLICommonFunc.countIpAndMask(ip, netMask) + "/" + CLICommonFunc.turnNetMaskToNum(netMask);
			}
			res = ip;
		}else if(ipAddress.getTypeFlag() == IpAddress.TYPE_HOST_NAME || ipAddress.getTypeFlag() == IpAddress.TYPE_IP_ADDRESS){
			res = ipAddItem.getIpAddress();
		}
		return res;
	}
	
	public boolean isConfigNASSharedKey(int index) {
		RadiusHiveapAuth hiveApAuth = radiusServerNAS.get(index);
		String key = hiveApAuth.getSharedKey();
		return key != null && !"".equals(key);
	}

	public String getNASSharedKey(int index) {
		RadiusHiveapAuth hiveApAuth = radiusServerNAS.get(index);
		return hiveApAuth.getSharedKey();
	}
	
	public boolean isEnableNasTls(int index) {
		RadiusHiveapAuth hiveApAuth = radiusServerNAS.get(index);
		return hiveApAuth.isEnableTls();
	}

	public boolean isConfigureUser() {
		if(NmsUtil.compareSoftwareVersion("3.2.0.0", hiveAp.getSoftVer()) <=0){
			return false;
		}
		short databaseType = radiusServerProfile.getDatabaseType();
		return databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL
				|| databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_ACTIVE
				|| databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN;
	}

	public int getLocalUserGroupSize() throws CreateXMLException {
//		if(localUserGroupList.size() > 64){
//			String errMsg = NmsUtil.getUserMessage(
//					"error.be.config.create.MaxUserGroup");
//			log.error("getLocalUserGroupSize", errMsg);
//			throw new CreateXMLException(errMsg);
//		}
		return localUserGroupList.size();
	}

	public String getLocalUserGroupName(int index) {
		return (localUserGroupList.get(index)).getGroupName();
	}
	
//	public boolean isLocalUserGroup(int index){
//		return localGroupMap.containsKey((localUserGroupList.get(index)).getGroupName());
//	}

	public int getUserGroupReauthTime(int index) {
		return (localUserGroupList.get(index)).getReauthTime();
	}

	public boolean isConfigUserGroupReauthTime(int index){
		return (localUserGroupList.get(index)).getReauthTime() != -1;
	}

	public boolean isConfigUserGroupProfileAttr(int index){
		return (localUserGroupList.get(index)).getUserProfileId() > -1;
	}

	public int getUserGroupProfileAttr(int index) {
		return (localUserGroupList.get(index))
				.getUserProfileId();
	}

	public boolean isConfigUserGroupVlanId(int index){
		return (localUserGroupList.get(index)).getVlanId() > 0;
	}

	public int getUserGroupVlanId(int index) {
		return (localUserGroupList.get(index)).getVlanId();
	}

	public int getLocalUserSize() {
		return localUserList.size();
	}

	public String getLocalUserName(int index) {
		return (localUserList.get(index)).getUserName();
	}

	public String getLocalUserPassword(int index) throws IOException {
		return AhConfigUtil.hiveApCommonEncrypt((localUserList
				.get(index)).getLocalUserPassword());
	}

	public boolean isLocalUserBindUserGroup(int index) {
		return (localUserList.get(index)).getLocalUserGroup() != null;
	}

	public String getLocalUserBindUserGroup(int index) {
		return (localUserList.get(index)).getLocalUserGroup()
				.getGroupName();
	}

	public boolean isRadiusDBTypeLocal() {
		short databaseType = radiusServerProfile.getDatabaseType();
		return databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL
				|| databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_ACTIVE
				|| databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN;
	}

	public boolean isRadiusDBTypeActive() {
		short databaseType = radiusServerProfile.getDatabaseType();
		return databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_ACTIVE
				|| databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_ACTIVE;
	}

	public boolean isRadiusDBTypeOpen() {
		short databaseType = radiusServerProfile.getDatabaseType();
		return databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN
				|| databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN;
	}

	public boolean isConfigureLocalCache() {
		return radiusServerProfile.getDatabaseType() != RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL;
	}
	
	public boolean isEnableLocalCache(){
		return radiusServerProfile.getCacheEnable();
	}

	public int getCacheLifeTime() {
		return radiusServerProfile.getCacheTime();
	}

	public boolean isConfigureAttrMap() {
		return (isRadiusDBTypeActive() || isRadiusDBTypeOpen() || isRadiusDBTypeOpenDirectory())
				&& radiusServerProfile.getMapEnable();
	}

	public boolean isConfigGroupAttrName() {
		String groupAttrNameStr = radiusServerProfile.getGroupAttribute();
		return radiusServerProfile.getMapByGroupOrUser() == RadiusOnHiveap.RADIUS_SERVER_MAP_BY_GROUPATTRI
				&& groupAttrNameStr != null && !"".equals(groupAttrNameStr);
	}

	public boolean isConfigureReauthAttrName() {
		String reauthAttrName = radiusServerProfile.getReauthTime();
		return radiusServerProfile.getMapByGroupOrUser() == RadiusOnHiveap.RADIUS_SERVER_MAP_BY_USERATTRI
				&& reauthAttrName != null && !"".equals(reauthAttrName);
	}

	public String getReauthAttrName() {
		return radiusServerProfile.getReauthTime();
	}

	public boolean isConfigureUserProfileAttr() {
		String userProfileAttr = radiusServerProfile.getUserProfileId();
		return radiusServerProfile.getMapByGroupOrUser() == RadiusOnHiveap.RADIUS_SERVER_MAP_BY_USERATTRI
				&& userProfileAttr != null && !"".equals(userProfileAttr);
	}

	public String getGroupAttrName() {
		return radiusServerProfile.getGroupAttribute();
	}

	public String getUserProfileAttrName() {
		return radiusServerProfile.getUserProfileId();
	}

	public boolean isConfigureVlanAttr() {
		String vlanAttrName = radiusServerProfile.getVlanId();
		return radiusServerProfile.getMapByGroupOrUser() == RadiusOnHiveap.RADIUS_SERVER_MAP_BY_USERATTRI
				&& vlanAttrName != null && !"".equals(vlanAttrName);
	}

	public String getVlanAttrName() {
		return radiusServerProfile.getVlanId();
	}

	public String getActiveDirectoryWorkgroup(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {

		ActiveDirectoryDomain defDomain = getDefaultDomain(priorityType);
		if(defDomain != null){
//			return CLICommonFunc.addQutoTwoSide(defDomain.getDomain());
			return defDomain.getDomain();
		}else{
			return null;
		}
	}

	private ActiveDirectoryDomain getDefaultDomain(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		ActiveDirectoryDomain defDomain = null;
		ActiveDirectoryOrOpenLdap addirectoryObj = getActiveOrOpenldap(priorityType);
		List<ActiveDirectoryDomain> adDomains = addirectoryObj.getAdDomains();
		for(ActiveDirectoryDomain adDomain : adDomains){
			if(adDomain.isDefaultFlag()){
				defDomain = adDomain;
				break;
			}
		}
		return defDomain;
	}

	public String getOpenLdapServer(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws CreateXMLException {
		SingleTableItem hostName = CLICommonFunc.getIpAddress(this.getActiveOrOpenldap(priorityType).getLdapServer(), hiveAp);
		return hostName.getIpAddress();
	}

	public String getActiveDirectoryRealm(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {

		ActiveDirectoryDomain defDomain = this.getDefaultDomain(priorityType);
		if(defDomain != null){
//			return CLICommonFunc.addQutoTwoSide(defDomain.getFullName());
			return defDomain.getFullName();
		}else{
			return null;
		}

	}

	public String getOpenLdapBasedn(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
//		return CLICommonFunc.addQutoTwoSide(this.getActiveOrOpenldap(priorityType)
//				.getBasedN());
		return this.getActiveOrOpenldap(priorityType).getBasedN();
	}

	public String getActiveServer(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws CreateXMLException {
		SingleTableItem host = CLICommonFunc.getIpAddress(this.getActiveOrOpenldap(priorityType).getAdServer(), hiveAp);
		return host.getIpAddress();
	}

	public String getOpenLdapPassword(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws IOException {
		return AhConfigUtil.hiveApCommonEncrypt(this.getActiveOrOpenldap(
				priorityType).getPasswordO());
	}

	public String getOpenLdapIdentity(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
//		return CLICommonFunc.addQutoTwoSide(this.getActiveOrOpenldap(priorityType)
//				.getBindDnName());
		return this.getActiveOrOpenldap(priorityType).getBindDnName();
	}

//	public boolean isConfigActiveBasedn(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
//		ActiveDirectoryDomain defDomain = this.getDefaultDomain(priorityType);
//		return defDomain != null && defDomain.getBasedN() != null && !"".equals(defDomain.getBasedN());
//	}
//
//	public String getActiveBasedn(
//			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
//
//		ActiveDirectoryDomain defDomain = this.getDefaultDomain(priorityType);
//		if(defDomain != null){
////			return CLICommonFunc.addQutoTwoSide(defDomain.getBasedN());
//			return defDomain.getBasedN();
//		}else{
//			return null;
//		}
//	}

	public String getActiveUser(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
//		return CLICommonFunc.addQutoTwoSide(this.getActiveOrOpenldap(priorityType)
//				.getUserNameA());
		return this.getActiveOrOpenldap(priorityType).getUserNameA();
	}
	
	public boolean isEnableTls(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		return this.getActiveOrOpenldap(priorityType).isAuthTlsEnable();
	}

	public String getActivePassword(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws IOException {
		return AhConfigUtil.hiveApCommonEncrypt(this.getActiveOrOpenldap(
				priorityType).getPasswordA());
	}

	public boolean isConfigActiveComputerOu(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		return this.getActiveOrOpenldap(priorityType).getComputerOU() != null &&
			!"".equals(this.getActiveOrOpenldap(priorityType).getComputerOU());
	}

	public String getActiveComputerOu(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
//		return CLICommonFunc.addQutoTwoSide(this.getActiveOrOpenldap(priorityType).getComputerOU());
		return this.getActiveOrOpenldap(priorityType).getComputerOU();
	}

	public String getOpenLdapTlsCaCert(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
//		return CLICommonFunc.addQutoTwoSide(this.getActiveOrOpenldap(priorityType).getCaCertFileO());
		return this.getActiveOrOpenldap(priorityType).getCaCertFileO();
	}

	public boolean isConfigureLdapTlsClientCert() {
		return this.isRadiusDBTypeOpen()
				&& (isConfigLdapAuth(AAAProfileInt.RADIUS_PRIORITY_TYPE.primary)
						|| isConfigLdapAuth(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1)
						|| isConfigLdapAuth(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2) || isConfigLdapAuth(AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3));
	}

	public LDAPAuthVerifyServerValue getVerifyServerValue(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		short serverType = this.getActiveOrOpenldap(priorityType)
				.getVerifyServer();
		switch (serverType) {
		case ActiveDirectoryOrOpenLdap.RADIUS_VERIFY_SERVER_TRY:
			return LDAPAuthVerifyServerValue.TRY;
		case ActiveDirectoryOrOpenLdap.RADIUS_VERIFY_SERVER_NEVER:
			return LDAPAuthVerifyServerValue.NEVER;
		case ActiveDirectoryOrOpenLdap.RADIUS_VERIFY_SERVER_DEMAND:
			return LDAPAuthVerifyServerValue.DEMAND;
		default:
			return LDAPAuthVerifyServerValue.TRY;
		}
	}

	public String getOpenLdapTlsClientCert(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
//		return CLICommonFunc.addQutoTwoSide(this.getActiveOrOpenldap(priorityType).getClientFile());
		return this.getActiveOrOpenldap(priorityType).getClientFile();
	}

	public String getOpenLdapTlsPrivateKey(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
//		return CLICommonFunc.addQutoTwoSide(this.getActiveOrOpenldap(priorityType).getKeyFileO());
		return this.getActiveOrOpenldap(priorityType).getKeyFileO();
	}

	public String getOpenLdapTlsPrivateKeyPassword(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws IOException {
		return AhConfigUtil.hiveApCommonEncrypt(this.getActiveOrOpenldap(
				priorityType).getKeyPasswordO());
	}

	// public boolean isConfigureAuthType() {
	// return radiusServerProfile.getAuthType() !=
	// RadiusOnHiveap.RADIUS_AUTH_TYPE_ALL;
	// }

	public int getOpenLdapPort(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		return this.getActiveOrOpenldap(priorityType).getDestinationPort();
	}
	
	public LdapServerProtocolValue getOpenLdapProtocol(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		short sProtocol = this.getActiveOrOpenldap(priorityType).getLdapProtocol();
		if(sProtocol == ActiveDirectoryOrOpenLdap.LDAP_SERVER_PROTOCOL_LDAP){
			return LdapServerProtocolValue.LDAP;
		}else{
			return LdapServerProtocolValue.LDAPS;
		}
	}

	public boolean isConfigActiveDirectory(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		ActiveDirectoryOrOpenLdap activeObj = getActiveOrOpenldap(priorityType);
		return activeObj != null
				&& activeObj.getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY;
	}

	public boolean isConfigOpenLdap(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		ActiveDirectoryOrOpenLdap openObj = getActiveOrOpenldap(priorityType);
		return openObj != null
				&& openObj.getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP;
	}

	public boolean isConfigLdapAuth(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		ActiveDirectoryOrOpenLdap openObj = getActiveOrOpenldap(priorityType);
		return openObj != null
				&& openObj.getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_OPEN_LDAP
				&& openObj.isAuthTlsEnable();
	}

	private ActiveDirectoryOrOpenLdap getActiveOrOpenldap(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.primary) {
			return primaryObj;
		} else if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1) {
			return backup1Obj;
		} else if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2) {
			return backup2Obj;
		} else if (priorityType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup3) {
			return backup3Obj;
		} else {
			return null;
		}
	}

	public boolean isEnableTlsCheckCertCn() {
		return radiusServerProfile.isCnEnable();
	}

	public boolean isEnableTlsCheckInDb() {
		return radiusServerProfile.isDbEnable();
	}
	
	public boolean isEnableTtlsCheckDb(){
		return radiusServerProfile.isTtlsCheckInDb();
	}
	
	public boolean isEnablePeapCheckDb(){
		return radiusServerProfile.isPeapCheckInDb();
	}

	public boolean isConfigLocalCheckPeriod() {
		return isRadiusDBTypeActive() || isRadiusDBTypeOpen() || isRadiusDBTypeOpenDirectory();
	}

	public boolean isConfigRemoteCheckPeriod() {
		return isRadiusDBTypeActive() || isRadiusDBTypeOpen() || isRadiusDBTypeOpenDirectory();
	}

	public boolean isConfigRetryInterval() {
		return isRadiusDBTypeActive() || isRadiusDBTypeOpen() || isRadiusDBTypeOpenDirectory();
	}

	public int getLocalCheckPeriodValue() {
		return radiusServerProfile.getLocalInterval();
	}

	public int getRemoteCheckPeriodValue() {
		return radiusServerProfile.getRemoteInterval();
	}

	public int getRetryIntervalValue() {
		return radiusServerProfile.getRetryInterval();
	}

//	public boolean isConfigAccountInterim(){
//		if(CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(this.getApVersion())){
//			return true;
//		}else if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_11N){
//			return true;
//		}else{
//			return false;
//		}
//	}

	public boolean isConfigRadioAccountInterim(){
		return radiusAssignment != null;
	}

	public boolean isConfigAAAMacFormat(){
		return mgmtServiceOpt != null;
	}

	public MacDelimiterValue getDelimiterType(){
		short macDelimiter = mgmtServiceOpt.getMacAuthDelimiter();
		MacDelimiterValue resDelimiter;
		switch(macDelimiter){
			case RadiusAssignment.RADIUS_MACAUTHDELIMITER_COLON :
				resDelimiter = MacDelimiterValue.COLON;
				break;
			case RadiusAssignment.RADIUS_MACAUTHDELIMITER_DASH :
				resDelimiter = MacDelimiterValue.DASH;
				break;
			case RadiusAssignment.RADIUS_MACAUTHDELIMITER_DOT :
				resDelimiter = MacDelimiterValue.DOT;
				break;
			default:
				resDelimiter = MacDelimiterValue.COLON;
		}
		return resDelimiter;
	}

	public MacStyleValue getStyleType(){
		short macAuthStyle = mgmtServiceOpt.getMacAuthStyle();
		MacStyleValue resStyle;
		switch(macAuthStyle){
			case RadiusAssignment.RADIUS_MACAUTHSTYLE_NO :
				resStyle = MacStyleValue.NO_DELIMITER;
				break;
			case RadiusAssignment.RADIUS_MACAUTHSTYLE_TWO :
				resStyle = MacStyleValue.TWO_DELIMITER;
				break;
			case RadiusAssignment.RADIUS_MACAUTHSTYLE_FIVE :
				resStyle = MacStyleValue.FIVE_DELIMITER;
				break;
			default:
				resStyle = MacStyleValue.NO_DELIMITER;
		}
		return resStyle;
	}

	public MacCaseSensitivityValue getCaseSensitivityType(){
		short macCase = mgmtServiceOpt.getMacAuthCase();
		MacCaseSensitivityValue resCase;
		switch(macCase){
			case RadiusAssignment.RADIUS_MACAUTHCASE_LOWER:
				resCase = MacCaseSensitivityValue.LOWER_CASE;
				break;
			case RadiusAssignment.RADIUS_MACAUTHCASE_UPPER:
				resCase = MacCaseSensitivityValue.UPPER_CASE;
				break;
			default:
				resCase = MacCaseSensitivityValue.LOWER_CASE;
		}
		return resCase;
	}
	
	public boolean isConfigLdapFilterAttr(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		String filterAttr = this.getActiveOrOpenldap(priorityType).getFilterAttr();
		return filterAttr != null && !"".equals(filterAttr);
	}

	public String getLdapFilterAttr(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		return this.getActiveOrOpenldap(priorityType).getFilterAttr();
	}

	public boolean isEnableEdirServer(){
		return radiusServerProfile.isUseEdirect();
	}

	public boolean isEnablePolicyCheck(){
		return radiusServerProfile.isAccPolicy();
	}

	public boolean isConfigADLogin(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		String username = this.getActiveUser(priorityType);
		return username != null && !"".equals(username);
	}

	public int getADDomainSize(RADIUS_PRIORITY_TYPE priorityType){
		ActiveDirectoryOrOpenLdap adType = getActiveOrOpenldap(priorityType);
		return adType.getAdDomains().size();
	}
	
	public boolean isEnableGlobalCatalog(RADIUS_PRIORITY_TYPE priorityType){
		return radiusServerProfile.isGlobalCatalog();
	}

	public String getADDomainName(RADIUS_PRIORITY_TYPE priorityType, int index){
		ActiveDirectoryOrOpenLdap adType = getActiveOrOpenldap(priorityType);
//		return CLICommonFunc.addQutoTwoSide(adType.getAdDomains().get(index).getDomain());
		String domainName = adType.getAdDomains().get(index).getDomain();
		String fullName = adType.getAdDomains().get(index).getFullName();
		if(domainName != null && !"".equals(domainName)){
			return domainName;
		}
		if(fullName != null && !"".equals(fullName)){
//			String repFullName = fullName.replace(".", "_");
//			return repFullName;
			return fullName;
		}else{
			return "";
		}
	}

//	public boolean isConfigDomainBaseDn(RADIUS_PRIORITY_TYPE priorityType, int index){
//		String basednStr = getADDomainBasedn(priorityType, index);
//		return basednStr != null && !"".equals(basednStr);
//	}
//
//	public String getADDomainBasedn(RADIUS_PRIORITY_TYPE priorityType, int index){
//		ActiveDirectoryOrOpenLdap adType = getActiveOrOpenldap(priorityType);
////		return CLICommonFunc.addQutoTwoSide(adType.getAdDomains().get(index).getBasedN());
//		return adType.getAdDomains().get(index).getBasedN();
//	}
	
	public boolean isConfigDomainServer(RADIUS_PRIORITY_TYPE priorityType, int index){
		ActiveDirectoryOrOpenLdap adType = getActiveOrOpenldap(priorityType);
		String serverStr = adType.getAdDomains().get(index).getServer();
		return !adType.getAdDomains().get(index).isDefaultFlag() &&
			serverStr != null && !"".equals(serverStr);
	}
	
	public String getADDomainServer(RADIUS_PRIORITY_TYPE priorityType, int index){
		ActiveDirectoryOrOpenLdap adType = getActiveOrOpenldap(priorityType);
		return adType.getAdDomains().get(index).getServer();
	}
	
	public boolean isConfigDomainFullname(RADIUS_PRIORITY_TYPE priorityType, int index){
		ActiveDirectoryOrOpenLdap adType = getActiveOrOpenldap(priorityType);
		String fullName = adType.getAdDomains().get(index).getFullName();
		return adType.getAdDomains().get(index).isDefaultFlag() &&
			fullName != null && !"".equals(fullName);
	}

	public String getADDomainFullname(RADIUS_PRIORITY_TYPE priorityType, int index){
		ActiveDirectoryOrOpenLdap adType = getActiveOrOpenldap(priorityType);
//		return CLICommonFunc.addQutoTwoSide(adType.getAdDomains().get(index).getFullName());
		return adType.getAdDomains().get(index).getFullName();
	}

	public boolean isConfigADDomainDefault(RADIUS_PRIORITY_TYPE priorityType, int index){
		ActiveDirectoryOrOpenLdap adType = getActiveOrOpenldap(priorityType);
		return adType.getAdDomains().get(index).isDefaultFlag();
	}

	public String getADDomainBinddn(RADIUS_PRIORITY_TYPE priorityType, int index){
		ActiveDirectoryOrOpenLdap adType = getActiveOrOpenldap(priorityType);
//		return CLICommonFunc.addQutoTwoSide(adType.getAdDomains().get(index).getBindDnName());
		return adType.getAdDomains().get(index).getBindDnWithFullName();
	}

	public String getADDomainBinddnPassword(RADIUS_PRIORITY_TYPE priorityType, int index) throws IOException{
		ActiveDirectoryOrOpenLdap adType = getActiveOrOpenldap(priorityType);
		return AhConfigUtil.hiveApCommonEncrypt(adType.getAdDomains().get(index).getBindDnPass());
	}

	public boolean isConfigDynamicAuth(){
		return radiusAssignment != null;
	}

	public boolean isEnableDynamicAuth(){
		return radiusAssignment != null && radiusAssignment.getEnableExtensionRadius();
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
						log.error("AAAProfileImpl.isEnableVpnTunnel", errMsg);
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
	
	public boolean isEnableVpnTunnelAd(String serverAddr) throws CreateXMLException{
		if(hiveAp.getVpnMark() == HiveAp.VPN_MARK_CLIENT){
			VpnService vpnClient = hiveAp.getConfigTemplate().getVpnService();
			if(vpnClient == null){
				return false;
			}else{
				if(vpnClient.isDbTypeAdThroughTunnel()){
					if(!CLICommonFunc.isIpAddress(serverAddr)){
						String errMsg = NmsUtil.getUserMessage("error.be.config.create.VPNPermitIp", new String[]{"Active Directory"});
						log.error("AAAProfileImpl.isEnableVpnTunnelAd", errMsg);
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
	
	public boolean isEnableVpnTunnelLdap(String serverAddr) throws CreateXMLException{
		if(hiveAp.getVpnMark() == HiveAp.VPN_MARK_CLIENT){
			VpnService vpnClient = hiveAp.getConfigTemplate().getVpnService();
			if(vpnClient == null){
				return false;
			}else{
				if(vpnClient.isDbTypeLdapThroughTunnel()){
					if(!CLICommonFunc.isIpAddress(serverAddr)){
						String errMsg = NmsUtil.getUserMessage("error.be.config.create.VPNPermitIp", new String[]{"Ldap"});
						log.error("AAAProfileImpl.isEnableVpnTunnelAd", errMsg);
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
	
	public boolean isRadiusDBTypeOpenDirectory() {
		short databaseType = radiusServerProfile.getDatabaseType();
		return databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_OPEN_DIRECT
				|| databaseType == RadiusOnHiveap.RADIUS_SERVER_DBTYPE_LOCAL_AND_OPEN_DIRECT;
	}
	
	public boolean isConfigOpenDirectory(
			AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		ActiveDirectoryOrOpenLdap openObj = getActiveOrOpenldap(priorityType);
		return openObj != null
				&& openObj.getTypeFlag() == ActiveDirectoryOrOpenLdap.TYPE_OPEN_DIRECTORY;
	}
	
	public boolean isConfigOpenDirectoryUser(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		ActiveDirectoryOrOpenLdap opDir = getActiveOrOpenldap(priorityType);
		String user = opDir.getUserNameA();
		return user != null && !"".equals(user);
	}
	
	public String getOpenDirectoryUser(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		ActiveDirectoryOrOpenLdap opDir = getActiveOrOpenldap(priorityType);
		return opDir.getUserNameA();
	}
	
	public String getOpenDirectoryPassword(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		ActiveDirectoryOrOpenLdap opDir = getActiveOrOpenldap(priorityType);
		return opDir.getPasswordA();
	}
	
	public boolean isOpenDirectoryTlsEnable(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		ActiveDirectoryOrOpenLdap opDir = getActiveOrOpenldap(priorityType);
		return opDir.isAuthTlsEnable();
	}
	
	public String getOpenDirectoryDomain(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		ActiveDirectoryOrOpenLdap opDir = getActiveOrOpenldap(priorityType);
		ActiveDirectoryDomain defDomain = null;
		for(ActiveDirectoryDomain domain : opDir.getAdDomains()){
			if(domain.isDefaultFlag()){
				defDomain = domain;
				break;
			}
		}
		if(defDomain != null){
			return defDomain.getDomain();
		}else{
			return null;
		}
	}
	
	public String getOpenDirectoryFullName(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		ActiveDirectoryOrOpenLdap opDir = getActiveOrOpenldap(priorityType);
		ActiveDirectoryDomain defDomain = null;
		for(ActiveDirectoryDomain domain : opDir.getAdDomains()){
			if(domain.isDefaultFlag()){
				defDomain = domain;
				break;
			}
		}
		if(defDomain != null){
			return defDomain.getFullName();
		}else{
			return null;
		}
	}
	
	public String getOpenDirectoryBindn(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		ActiveDirectoryOrOpenLdap opDir = getActiveOrOpenldap(priorityType);
		ActiveDirectoryDomain defDomain = null;
		for(ActiveDirectoryDomain domain : opDir.getAdDomains()){
			if(domain.isDefaultFlag()){
				defDomain = domain;
				break;
			}
		}
		if(defDomain != null){
			return defDomain.getBindDnName();
		}else{
			return null;
		}
	}
	
	public String getOpenDirectoryBindnPass(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		ActiveDirectoryOrOpenLdap opDir = getActiveOrOpenldap(priorityType);
		ActiveDirectoryDomain defDomain = null;
		for(ActiveDirectoryDomain domain : opDir.getAdDomains()){
			if(domain.isDefaultFlag()){
				defDomain = domain;
				break;
			}
		}
		if(defDomain != null){
			return defDomain.getBindDnPass();
		}else{
			return null;
		}
	}
	
	public boolean isConfigRadiusProxy(){
		return radProxy != null;
	}
	
	public int getProxyDeadTime(){
		return radProxy.getDeadTime(); 
	}
	
	public int getProxyRetryDelay(){
		return radProxy.getRetryDelay();
	}
	
	public int getProxyRetryCount(){
		return radProxy.getRetryCount();
	}
	
	public RadiusRealmFormatValue getProxyFormat(){
		if(radProxy.getProxyFormat() == RadiusProxy.RADIUS_PROXY_FORMAT_NAI){
			return RadiusRealmFormatValue.NAI;
		}else{
			return RadiusRealmFormatValue.NT_DOMAIN;
		}
	}
	
	public int getProxyRealmSize(){
		if(radProxy.getRadiusRealm() == null){
			return 0;
		}else{
			return radProxy.getRadiusRealm().size();
		}
	}
	
	public boolean isConfigProxyRealm(int index){
		if(versionAfter61r3 && radProxy.getRadiusRealm().get(index).isProxy4RadSec()){
			return false;
		}else if(!versionAfter61r3 && radProxy.getRadiusRealm().get(index).isProxy4RadSec()){
			return true;
		}else{
			return radProxy.getRadiusRealm().get(index).getRadiusServer() != null && !radProxy.getRadiusRealm().get(index).isIdmAuthProxy();
		}
	}
	
	public String getProxyRealmName(int index){
		String serverName = radProxy.getRadiusRealm().get(index).getServerName();
		if(RadiusProxyRealm.DEFAULT_REALM_NAME.equals(serverName) || RadiusProxyRealm.NULL_REALM_NAME.equals(serverName)){
			serverName = serverName.toUpperCase();
		}
		return serverName;
	}
	
	public boolean isConfigProxyRealmNoStrip(int index){
		String name = this.getProxyRealmName(index);
		return name != null && !name.equals(RadiusProxyRealm.NULL_REALM_NAME);
	}
	
	public boolean isProxyRealmNoStrip(int index){
		return !radProxy.getRadiusRealm().get(index).isStrip();
	}
	
	public boolean isConfigProxyRealmServerPrimary(int index){
		if(radProxy.getRadiusRealm().get(index).isProxy4RadSec()){
			return true;
		}else if(radProxy.getRadiusRealm().get(index).isIdmAuthProxy()){
			return false;
		}else if(radProxy.getRadiusRealm().get(index).getRadiusServer() == null){
			return false;
		}
		String serverName;
		for(ProxyRadiusServer realm : proxySet){
			serverName = radProxy.getRadiusRealm().get(index).getRadiusServer().getRadiusName().trim();
			serverName = serverName.substring(0,serverName.length() > REALM_LENGTH ? REALM_LENGTH : serverName.length());
			if(realm.getName().equals(serverName+PRIMARY_SUFIX)){
				return true;
			}
		}
		return false;
//		return radProxy.getRadiusRealm().get(index).getRadiusServer() != null && 
//			radProxy.getRadiusRealm().get(index).getRadiusServer().getServices().size() >0;
	}
	
	public boolean isConfigProxyRealmServerBackup(int index){
		if(radProxy.getRadiusRealm().get(index).isProxy4RadSec()){
			return false;
		}else if(radProxy.getRadiusRealm().get(index).isIdmAuthProxy()){
			return false;
		}else if(radProxy.getRadiusRealm().get(index).getRadiusServer() == null){
			return false;
		}
		String serverName;
		for(ProxyRadiusServer realm : proxySet){
			serverName = radProxy.getRadiusRealm().get(index).getRadiusServer().getRadiusName().trim();
			serverName = serverName.substring(0,serverName.length() > REALM_LENGTH ? REALM_LENGTH : serverName.length());
			if(realm.getName().equals(serverName+BACKUP_SUFIX)){
				return true;
			}
		}
		return false;
//		return radProxy.getRadiusRealm().get(index).getRadiusServer() != null &&
//			radProxy.getRadiusRealm().get(index).getRadiusServer().getServices().size() >1;
	}
	
	public String getProxyRealmServerPrimaryName(int index){
		if(radProxy.getRadiusRealm().get(index).isProxy4RadSec()){
			return radProxy.getRadiusRealm().get(index).getServerName()+PRIMARY_SUFIX;
		}
		String serverName = radProxy.getRadiusRealm().get(index).getRadiusServer().getRadiusName().trim();
		serverName = serverName.substring(0,serverName.length() > REALM_LENGTH ? REALM_LENGTH : serverName.length());
		return serverName+PRIMARY_SUFIX;
	}
	
	public String getProxyRealmServerBackupName(int index){
		String serverName = radProxy.getRadiusRealm().get(index).getRadiusServer().getRadiusName().trim();
		serverName = serverName.substring(0,serverName.length() > REALM_LENGTH ? REALM_LENGTH : serverName.length());
		return serverName+BACKUP_SUFIX;
	}
	
	public int getRealmSize(){
		return proxySet.size();
	}
	
	public String getRealmName(int index){
		return proxyArray[index].getName();
	}
	
	public int getRealmAcctPort(int index){
		return proxyArray[index].getRadServer().getAcctPort();
	}
	
	public int getRealmAuthPort(int index){
		return proxyArray[index].getRadServer().getAuthPort();
	}
	
	public String getRealmIp(int index) throws CreateXMLException{
		if(proxyArray[index].isProxy4RadSec()){
			return proxyArray[index].getCloudAuthIp();
		}else {
			return CLICommonFunc.getIpAddress(proxyArray[index].getRadServer().getIpAddress(), this.hiveAp).getIpAddress();
		}
	}
	
	public String getRealmPass(int index){
		return proxyArray[index].getRadServer().getSharedSecret();
	}
	
	public boolean isEnableKeepalive(){
		return this.hiveAp.getConfigTemplate().isEnableProbe();
	}
	
	public int getKeepaliveInterval(){
		return this.hiveAp.getConfigTemplate().getProbeInterval();
	}
	
	public int getKeepaliveRetry(){
		return this.hiveAp.getConfigTemplate().getProbeRetryCount();
	}
	
	public int getKeepaliveRetryInterval(){
		return this.hiveAp.getConfigTemplate().getProbeRetryInterval();
	}
	
	public boolean isConfigKeepaliveUsername(){
		String username = this.hiveAp.getConfigTemplate().getProbeUsername();
		return username != null && !"".equals(username);
	}
	
	public String getKeepaliveUsername(){
		return this.hiveAp.getConfigTemplate().getProbeUsername();
	}
	
	public String getKeepalivePassword(){
		return this.hiveAp.getConfigTemplate().getProbePassword();
	}
	
	public boolean isEnableLibrarySipPolicy(){
		return radiusServerProfile != null && radiusServerProfile.isLibrarySipCheck() && radiusServerProfile.getSipPolicy() != null;
	}
	
	public String getLibrarySipService() throws CreateXMLException{
		IpAddress sipIp = radiusServerProfile.getSipServer();
		return CLICommonFunc.getIpAddress(sipIp, this.hiveAp).getIpAddress();
	}
	
	public int getLibrarySipPort(){
		return radiusServerProfile.getSipPort();
	}
	
	public String getLibrarySipInstitutionId(){
		return radiusServerProfile.getInstitutionId();
	}
	
	public boolean isLibrarySipLoginEnable(){
		return radiusServerProfile.isLoginEnable();
	}
	
	public String getLibrarySipSeparator(){
		return radiusServerProfile.getSeparator();
	}
	
	public String getLibrarySipUserName(){
		return radiusServerProfile.getLoginUser();
	}
	
	public String getLibrarySipPassword(){
		return radiusServerProfile.getLoginPwd();
	}
	
	public String getLibrarySipPolicyName(){
		return radiusServerProfile.getSipPolicy().getPolicyName();
	}
	
	public int getPpskAutoSaveInt(){
		return this.mgmtServiceOpt.getPpskAutoSaveInt();
	}
	
	public boolean isConfigPpskRadius(){
		return ppskRadioServer != null;
	}
	
	private RadiusServer getPpskRadiusServerByType(
			AAAProfileInt.RADIUS_PRIORITY_TYPE primaryType) {
		if (primaryType == AAAProfileInt.RADIUS_PRIORITY_TYPE.primary) {
			return ppskRadiusPrimary;
		} else if (primaryType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup1) {
			return ppskRadiusBackup1;
		} else if (primaryType == AAAProfileInt.RADIUS_PRIORITY_TYPE.backup2) {
			return ppskRadiusBackup2;
		} else {
			return ppskRadiusBackup3;
		}
	}
	
	public boolean isConfigPpskPriority(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		return getPpskRadiusServerByType(priorityType) != null;
	}
	
	public String getPpskRadiusServerIpOrHost(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) throws CreateXMLException{
		RadiusServer radServer = getPpskRadiusServerByType(priorityType);
		if(radServer.isUseSelfAsServer()){
			return hiveAp.getCfgIpAddress();
		}else{
			return CLICommonFunc.getIpAddress(radServer.getIpAddress(), this.hiveAp).getIpAddress();
		}
	}
	
	public boolean isConfigPpskRadiusSecret(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		RadiusServer radServer = getPpskRadiusServerByType(priorityType);
		return radServer.getSharedSecret() != null && !"".equals(radServer.getSharedSecret());
	}
	
	public String getPpskRadiusSecret(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		return getPpskRadiusServerByType(priorityType).getSharedSecret();
	}
	
	public int getPpskRadiusAuthPort(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType){
		return getPpskRadiusServerByType(priorityType).getAuthPort();
	}
	
	public static class ProxyRadiusServer{
		
		private RadiusServer radServer = null;
		private String name;
		private String cloudAuthIp = null;
		private boolean useCloudAuth = false;
		private boolean authProxy = false;

		public ProxyRadiusServer(String name, RadiusServer radServer){
			this.name = name;
			this.radServer = radServer;
		}
		
		public ProxyRadiusServer(String name, boolean useCloudAuth, String cloudAuthIp){
			this.name = name;
			this.useCloudAuth = useCloudAuth;
			this.cloudAuthIp = cloudAuthIp;
		}
		
		public String getName(){
			return this.name;
		}
		
		public RadiusServer getRadServer(){
			return this.radServer;
		}
		
		public boolean isProxy4RadSec(){
			return this.useCloudAuth;
		}
		
		public String getCloudAuthIp(){
			return this.cloudAuthIp;
		}
		
		public boolean isAuthProxy() {
			return authProxy;
		}

		public void setAuthProxy(boolean authProxy) {
			this.authProxy = authProxy;
		}
		
		public boolean equals(Object obj){
	        if ( obj != null && obj instanceof ProxyRadiusServer){
	        	ProxyRadiusServer proxyObj = (ProxyRadiusServer)obj;
	        	if((proxyObj.name.equals(this.name))){
	        		return true;
	        	}
	        }
	        return false;
	    }
		
	    public int hashCode(){  
	        return  this.name.hashCode();
	    }
	}
	
	public boolean isConfigAaaAttribute(){
		return isConfigNasIdentifier() || isConfigOperatorName();
	}

	public boolean isConfigNasIdentifier(){
		return hiveAp.getNasIdentifierType() == HiveAp.USE_CUSTOMIZED_NAS_IDE
		&& !hiveAp.getCustomizedNasIdentifier().equals(hiveAp.getHostName()) ;
	}
	
	public String getNasIdentifier() {
		return hiveAp.getCustomizedNasIdentifier();
	}
	
	public boolean isConfigOperatorName(){
		RadiusAttrs radiusAttrs = hiveAp.getConfigTemplate().getRadiusAttrs();
		if(radiusAttrs == null){
			return false;
		}else{
			return true;
		}
		
	}

	public String getOperatorName() throws CreateXMLException {
		RadiusAttrs radiusAttrs = hiveAp.getConfigTemplate().getRadiusAttrs();
		return CLICommonFunc.getRadiusAttrs(radiusAttrs,hiveAp).getOperatorName();
	}
	
	public String getNamespaceId()throws CreateXMLException {
		RadiusAttrs radiusAttrs = hiveAp.getConfigTemplate().getRadiusAttrs();
		String nameSpace=null;
		short nameSpaceId=CLICommonFunc.getRadiusAttrs(radiusAttrs,hiveAp).getNameSpaceId();
		switch(nameSpaceId){
			case SingleTableItem.TYPE_NAMESPACE_REALM :
				nameSpace ="REALM";
				break;
			case SingleTableItem.TYPE_NAMESPACE_TADIG :
				nameSpace ="TADIG";
				break;
			case SingleTableItem.TYPE_NAMESPACE_E212 :
				nameSpace ="E212";
				break;
			case SingleTableItem.TYPE_NAMESPACE_ICC :
				nameSpace ="ICC";
				break;
			default:
				nameSpace ="REALM";
		}
		return nameSpace;
	}
	
	public boolean isProxyOperatorNameEnable() {
		return radProxy!=null && radProxy.isInjectOperatorNmAttri();
	}

	public boolean isEnableStripFilter(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		return !this.getActiveOrOpenldap(priorityType).isStripFilter();
	}
	
//	public boolean isConfigProxyRadsec(){
//		if(hiveAp.getDownloadInfo().isEnableIdm() && radiusServerProfile != null && 
//				hiveAp.isEnableIDMAuthProxy()){
//			//Auth proxy
//			return true;
//		}else if(hiveAp.isIDMProxy()){
//			//IDM proxy
//			return true;
//		}else{
//			return false;
//		}
////		
////		if(radProxy == null || radProxy.getRadiusRealm() == null){
////			return false;
////		}
////		for(RadiusProxyRealm realm : radProxy.getRadiusRealm()){
////			if(realm.isProxy4RadSec() || realm.isIdmAuthProxy()){
////				return true;
////			}
////		}
////		return false;
//	}
	
//	public boolean isEnableProxyRadsec(){
//		return isConfigProxyRadsec();
//	}
	
//	public boolean isConfigRadsecTlsPort(){
//		return getRadsecTlsPort() != IDMRadSecConfig.DEFAULT_RADSEC_TLS_PORT;
//	}
	
	public int getRadsecTlsPort(){
		return radSecConfig.getTlsPort();
	}
	
	public boolean isProxyServerCloudAuth(int index){
		return proxyArray[index].isProxy4RadSec();
	}
	
	public int getRadsecRealmSize(){
		if(radProxy == null || radProxy.getRadiusRealm() == null){
			return 0;
		}else{
			return radProxy.getRadiusRealm().size();
		}
	}
	
	public boolean isAuthRealmValid(int index){
		if(versionAfter61r3){
			return radProxy.getRadiusRealm().get(index).isUseIDM();
		}else{
			return radProxy.getRadiusRealm().get(index).isIdmAuthProxy();
		}
	}
	
	public String getRadsecRealmName(int index){
		return getProxyRealmName(index);
	}
	
	public boolean isRadsecPrimaryRealm(int index){
		if(radProxy.getRadiusRealm().get(index).isProxy4RadSec()){
			return true;
		}else if(radProxy.getRadiusRealm().get(index).isIdmAuthProxy()){
			return false;
		}else if(radProxy.getRadiusRealm().get(index).getRadiusServer() == null){
			return false;
		}
		String serverName;
		for(ProxyRadiusServer realm : proxySet){
			serverName = radProxy.getRadiusRealm().get(index).getRadiusServer().getRadiusName().trim();
			serverName = serverName.substring(0,serverName.length() > REALM_LENGTH ? REALM_LENGTH : serverName.length());
			if(realm.getName().equals(serverName+AUTH_PRIMARY_SUFIX)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isRadsecBackupRealm(int index){
		if(radProxy.getRadiusRealm().get(index).isProxy4RadSec()){
			return false;
		}else if(radProxy.getRadiusRealm().get(index).isIdmAuthProxy()){
			return false;
		}else if(radProxy.getRadiusRealm().get(index).getRadiusServer() == null){
			return false;
		}
		String serverName;
		for(ProxyRadiusServer realm : proxySet){
			serverName = radProxy.getRadiusRealm().get(index).getRadiusServer().getRadiusName().trim();
			serverName = serverName.substring(0,serverName.length() > REALM_LENGTH ? REALM_LENGTH : serverName.length());
			if(realm.getName().equals(serverName+AUTH_BACKUP_SUFIX)){
				return true;
			}
		}
		return false;
	}
	
	public String getRadsecRealmPrimaryValue(int index){
		if(radProxy.getRadiusRealm().get(index).isProxy4RadSec() && versionAfter61r3){
			return radProxy.getRadiusRealm().get(index).getServerName()+PRIMARY_SUFIX;
		}
		String serverName = radProxy.getRadiusRealm().get(index).getRadiusServer().getRadiusName().trim();
		serverName = serverName.substring(0,serverName.length() > REALM_LENGTH ? REALM_LENGTH : serverName.length());
		return serverName+AUTH_PRIMARY_SUFIX;
	}
	
	public String getRadsecRealmBackupValue(int index){
		String serverName = radProxy.getRadiusRealm().get(index).getRadiusServer().getRadiusName().trim();
		serverName = serverName.substring(0,serverName.length() > REALM_LENGTH ? REALM_LENGTH : serverName.length());
		return serverName+AUTH_BACKUP_SUFIX;
	}
	
	public boolean isConfigSTAAuthDefaultType() {
		return radiusServerProfile != null;
	}
	
	public String getSTAAuthDefaultType(){
		short type = radiusServerProfile.getAuthTypeDefault();
		switch(type){
		case RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_PEAP:
			return STA_AUTH_TYPE.peap.name();
		case RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_TTLS:
			return STA_AUTH_TYPE.ttls.name();
		case RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_TLS:
			return STA_AUTH_TYPE.tls.name();
		case RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_LEAP:
			return STA_AUTH_TYPE.leap.name();
		case RadiusOnHiveap.RADIUS_AUTH_TYPE_DEFAULT_MD5:
			return STA_AUTH_TYPE.md5.name();
		default:
			return STA_AUTH_TYPE.peap.name();
		}
	}
	
	public String getSaslWrappingValue(AAAProfileInt.RADIUS_PRIORITY_TYPE priorityType) {
		if (getActiveOrOpenldap(priorityType) != null) {
			return this.getActiveOrOpenldap(priorityType).getLdapSaslWrappingString();
		}
		return "";
	}

//	@Override
//	public boolean isConfigRadsecDynamicAuthExtension() {
//		if(isEnableIdmProxy()){
//			return true;
//		}
//		if(hiveAp.getConfigTemplate() != null && hiveAp.getConfigTemplate().getSsidInterfaces() != null){
//			for(ConfigTemplateSsid tempSsid : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
//				if(tempSsid.getSsidProfile() == null){
//					continue;
//				}
//				SsidProfile ssidObj = tempSsid.getSsidProfile();
//				
//				if(ssidObj.getAccessMode() == SsidProfile.ACCESS_MODE_OPEN && 
//						ssidObj.isEnabledIDM() && 
//						ssidObj.getCwp() != null && 
//						ssidObj.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA){
//					return true;
//				}
//			}
//		}
//		return false;
//	}
	
//	public boolean isEnableIdmProxy(){
//		return hiveAp.isIDMProxy();
//	}
}