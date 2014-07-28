package com.ah.be.config.create.source.impl;

import java.util.Collection;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.ConfigTemplateVlanNetwork;
import com.ah.bo.hiveap.DeviceStpSettings;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.igmp.MulticastGroup;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosCustomService;
import com.ah.bo.mobility.QosMacOui;
import com.ah.bo.mobility.TunnelSetting;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.network.AccessConsole;
import com.ah.bo.network.AirScreenRule;
import com.ah.bo.network.AirScreenRuleGroup;
import com.ah.bo.network.ApplicationProfile;
import com.ah.bo.network.BonjourGatewaySettings;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.DevicePolicyRule;
import com.ah.bo.network.DnsServiceProfile;
import com.ah.bo.network.DnsSpecificSettings;
import com.ah.bo.network.FirewallPolicy;
import com.ah.bo.network.FirewallPolicyRule;
import com.ah.bo.network.IdsPolicy;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.IpFilter;
import com.ah.bo.network.IpPolicy;
import com.ah.bo.network.IpPolicyRule;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacFilterInfo;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.MacPolicy;
import com.ah.bo.network.MacPolicyRule;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.RadiusAttrs;
import com.ah.bo.network.RoutingPolicy;
import com.ah.bo.network.RoutingPolicyRule;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.bo.network.StpSettings;
import com.ah.bo.network.SubNetworkResource;
import com.ah.bo.network.SwitchSettings;
import com.ah.bo.network.UserProfileForTrafficL2;
import com.ah.bo.network.UserProfileForTrafficL3;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VlanDhcpServer;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnNetworkSub;
import com.ah.bo.network.VpnService;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.port.PortPseProfile;
import com.ah.bo.useraccess.ActiveDirectoryOrLdapInfo;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceDnsInfo;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.MgmtServiceSnmpInfo;
import com.ah.bo.useraccess.MgmtServiceSyslog;
import com.ah.bo.useraccess.MgmtServiceSyslogInfo;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.bo.useraccess.MgmtServiceTimeInfo;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusHiveapAuth;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.useraccess.RadiusProxyRealm;
import com.ah.bo.useraccess.RadiusServer;
import com.ah.bo.useraccess.RadiusUserProfileRule;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.bo.useraccess.UserProfileVlanMapping;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.EthernetAccess;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SsidProfile;
import com.ah.bo.wlan.WalledGardenItem;

/**
 * @author zhang
 * @version 2008-5-28 10:04:00
 */

public class ConfigLazyQueryBo implements QueryBo {

	public Collection<HmBo> load(HmBo bo) {
		/** HiveAp */
		if (bo instanceof HiveAp) {
			HiveAp hiveApBo = (HiveAp) bo;
			
			if (hiveApBo.getMapContainer() != null && hiveApBo.getMapContainer().getParentMap() != null)
				hiveApBo.getMapContainer().getParentMap().getId();
			if (hiveApBo.getL3Neighbors() != null)
				hiveApBo.getL3Neighbors().size();
			if (hiveApBo.getWifi0RadioProfile() != null)
				loadRadioProfile(hiveApBo.getWifi0RadioProfile());
			if (hiveApBo.getWifi1RadioProfile() != null)
				loadRadioProfile(hiveApBo.getWifi1RadioProfile());
			if (hiveApBo.getStaticRoutes() != null)
				for (int i = 0; i < hiveApBo.getStaticRoutes().size(); i++) {
					hiveApBo.getStaticRoutes().get(i).getDestinationMac();
					hiveApBo.getStaticRoutes().get(i).getInterfaceType();
					hiveApBo.getStaticRoutes().get(i).getNextHopMac();
				}
			if (hiveApBo.getDynamicRoutes() != null)
				for (int i = 0; i < hiveApBo.getDynamicRoutes().size(); i++) {
					hiveApBo.getDynamicRoutes().get(i).getNeighborMac();
					hiveApBo.getDynamicRoutes().get(i).getRouteMaximun();
					hiveApBo.getDynamicRoutes().get(i).getRouteMinimun();
				}
			if (hiveApBo.getConfigTemplate() != null)
				loadConfigTemplate(hiveApBo.getConfigTemplate());
			if (hiveApBo.getRadiusServerProfile() != null)
				loadRadiusOnHiveap(hiveApBo.getRadiusServerProfile());
			if (hiveApBo.getDhcpServers() != null)
				hiveApBo.getDhcpServers().size();
			if (hiveApBo.getOwner() != null)
				hiveApBo.getOwner().getId();
			if (hiveApBo.getDisabledSsids() != null)
				hiveApBo.getDisabledSsids().size();
			if (hiveApBo.getEth0UserProfile() != null)
				hiveApBo.getEth0UserProfile().getId();
			if (hiveApBo.getEth1UserProfile() != null)
				hiveApBo.getEth1UserProfile().getId();
			if (hiveApBo.getAgg0UserProfile() != null)
				hiveApBo.getAgg0UserProfile().getId();
			if (hiveApBo.getRed0UserProfile() != null)
				hiveApBo.getRed0UserProfile().getId();
			if (hiveApBo.getLearningMacs() != null) {
				for (int i = 0; i < hiveApBo.getLearningMacs().size(); i++) {
					MacOrOui mac = hiveApBo.getLearningMacs().get(i).getMac();
					mac.getId();
					loadMacOuiItems(mac);
				}
			}
			if (hiveApBo.getIpRoutes() != null)
				hiveApBo.getIpRoutes().size();
			if (hiveApBo.getEthCwpCwpProfile() != null)
				loadCwp(hiveApBo.getEthCwpCwpProfile());
			if (hiveApBo.getEthCwpRadiusClient() != null)
				hiveApBo.getEthCwpRadiusClient().getId();
			if (hiveApBo.getEthCwpDefaultAuthUserProfile() != null)
				hiveApBo.getEthCwpDefaultAuthUserProfile().getId();
			if (hiveApBo.getEthCwpDefaultRegUserProfile() != null)
				hiveApBo.getEthCwpDefaultRegUserProfile().getId();
			if (hiveApBo.getEthCwpRadiusUserProfiles() != null)
				hiveApBo.getEthCwpRadiusUserProfiles().size();
			if (hiveApBo.getRadiusProxyProfile() != null)
				loadRadiusProxy(hiveApBo.getRadiusProxyProfile());
			if (hiveApBo.getVirtualConnections() != null)
				hiveApBo.getVirtualConnections().size();
			if (hiveApBo.getMultipleVlan() != null)
				hiveApBo.getMultipleVlan().size();
			if (hiveApBo.getDeviceInterfaces() != null)
				hiveApBo.getDeviceInterfaces().values();
			if (hiveApBo.getRoutingProfile() != null){
				hiveApBo.getRoutingProfile().getId();
				if(hiveApBo.getRoutingProfile().getItems() != null){
					hiveApBo.getRoutingProfile().getItems().size();
				}
			}
			if(hiveApBo.getInternalNetworks()!=null){
				hiveApBo.getInternalNetworks().size();
			}
			if (hiveApBo.getUsbModemList() != null)
				hiveApBo.getUsbModemList().size();
			if (hiveApBo.getCvgDPD() != null){
				if (hiveApBo.getCvgDPD().getMgtNetwork() != null){
					loadVpnNetwork(hiveApBo.getCvgDPD().getMgtNetwork());
				}
				if (hiveApBo.getCvgDPD().getDnsForCVG() != null){
					loadMgmtServiceDns(hiveApBo.getCvgDPD().getDnsForCVG());
				}
				if (hiveApBo.getCvgDPD().getNtpForCVG() != null){
					loadMgmtServiceTime(hiveApBo.getCvgDPD().getNtpForCVG());
				}
				if (hiveApBo.getCvgDPD().getMgmtServiceSyslog() != null){
					this.loadMgmtServiceSyslog(hiveApBo.getCvgDPD().getMgmtServiceSyslog());
				}
				if (hiveApBo.getCvgDPD().getMgmtServiceSnmp() != null){
					this.loadMgmtServiceSnmp(hiveApBo.getCvgDPD().getMgmtServiceSnmp());
				}
			}
			if (hiveApBo.getEthCwpRadiusClient() != null){
				loadRadiusAssignment(hiveApBo.getEthCwpRadiusClient());
			}
			if (hiveApBo.getRoutingPolicy() != null){
				loadRoutingPolicy(hiveApBo.getRoutingPolicy());
			}
			if(hiveApBo.getRoutingProfilePolicy()!=null){
				loadRoutingProfilePolicy(hiveApBo.getRoutingProfilePolicy());
			}
			if (hiveApBo.getStormControlList() != null){
				hiveApBo.getStormControlList().size();
			}
			loadIpItems(hiveApBo.getCapwapIpBind());
			loadIpItems(hiveApBo.getCapwapBackupIpBind());
			if (hiveApBo.getForwardingDB() != null) {
				hiveApBo.getForwardingDB().getId();
				if (hiveApBo.getForwardingDB().getMacAddressEntries() != null) {
					hiveApBo.getForwardingDB().getMacAddressEntries().size();
				}
			}
			if (hiveApBo.getIgmpPolicys() != null) {
				hiveApBo.getIgmpPolicys().size();
			}
			if (hiveApBo.getMulticastGroups() != null) {
				hiveApBo.getMulticastGroups().size();
				for (MulticastGroup group : hiveApBo.getMulticastGroups()) {
					if (group != null && group.getInterfaces() != null) {
						group.getInterfaces().size();
					}
				}
			}
			if(hiveApBo.getDeviceStpSettings() != null){
				this.loadDeviceStpSettings(hiveApBo.getDeviceStpSettings());
			}
			if(hiveApBo.getWifiClientPreferredSsids() != null){
				hiveApBo.getWifiClientPreferredSsids().size();
			}
			if(hiveApBo.getDhcpServers() != null){
				for(VlanDhcpServer dhcpserver : hiveApBo.getDhcpServers()){
					if (dhcpserver.getCustoms() != null)
						dhcpserver.getCustoms().size();
					if (dhcpserver.getIpPools() != null)
						dhcpserver.getIpPools().size();
				}
			}
			if(hiveApBo.getDownloadInfo() != null)
				hiveApBo.getDownloadInfo().getId();
			if(hiveApBo.getSupplementalCLI() != null)
				hiveApBo.getSupplementalCLI().getId();
		}else if (bo instanceof ConfigTemplate) {
			/** ConfigTemplate */
			ConfigTemplate configTemplate = (ConfigTemplate) bo;
			loadConfigTemplate(configTemplate);
		}else if (bo instanceof UserProfile) {
			/** UserProfilesAction */
			UserProfile userProfile = (UserProfile) bo;
			loadUserProfile(userProfile);
		}else if (bo instanceof QosClassification) {
			/** QosClassification */
			loadQosClassification(bo);
		}else if (bo instanceof EthernetAccess) {
			/** EthernetAccess */
			EthernetAccess ethernetObj = (EthernetAccess) bo;

			if (ethernetObj.getUserProfile() != null)
				ethernetObj.getUserProfile().getId();
			if (ethernetObj.getMacAddress() != null)
				for(MacOrOui mac : ethernetObj.getMacAddress()){
					loadMacOuiItems(mac);
				}
		}else if (bo instanceof SsidProfile) {
			loadSsidProfile((SsidProfile)bo);
		}else if (bo instanceof HiveProfile) {
			/** HiveProfile */
			loadHiveProfile(bo);
		}else if (bo instanceof IpFilter) {
			/** IpFilter */
			loadIpFilter(bo);
		}else if (bo instanceof RadiusOnHiveap) {
			RadiusOnHiveap radiusObj = (RadiusOnHiveap)bo;
			loadRadiusOnHiveap(radiusObj);
		}else if (bo instanceof RadiusUserProfileRule) {
			/*
			 * radius user profile rule joseph chen , 08/26/2008
			 */
			RadiusUserProfileRule rule = (RadiusUserProfileRule) bo;

			if (rule.getPermittedUserProfiles() != null)
				rule.getPermittedUserProfiles().size();
		}else if (bo instanceof AccessConsole) {
			/** AccessConsole */
			this.loadAccessConsole(bo);
		}else if(bo instanceof TunnelSetting){
			/** TunnelSetting */
			loadTunnelSetting(bo);
		}else if(bo instanceof RadiusAssignment){
			RadiusAssignment radiusAssProfile = (RadiusAssignment)bo;
			loadRadiusAssignment(radiusAssProfile);
		}else if(bo instanceof Cwp){
			/** Cwp */
			loadCwp((Cwp)bo);
		}else if(bo instanceof RadioProfile){
			/** RadioProfile */
			RadioProfile radioObj = (RadioProfile)bo;
			if(radioObj.getWmmItems() != null)
				radioObj.getWmmItems().size();
		}else if(bo instanceof VpnService){
			/** VpnService */
			loadVpnService((VpnService)bo);
		}else if(bo instanceof AirScreenRule){
			/** AirScreenRule */
			AirScreenRule airRule = (AirScreenRule)bo;
			
			if(airRule.getSource() != null){
				airRule.getSource().getId();
				loadMacOuiItems(airRule.getSource().getOui());
			}
			if(airRule.getBehaviors() != null)
				airRule.getBehaviors().size();
			if(airRule.getActions() != null)
				airRule.getActions().size();
		}else if(bo instanceof AirScreenRuleGroup){
			/** AirScreenRuleGroup */
			AirScreenRuleGroup asGroup = (AirScreenRuleGroup)bo;
			
			if(asGroup.getRules() != null)
				asGroup.getRules().size();
		}else if(bo instanceof RadiusProxy){
			/** RadiusProxy */
			RadiusProxy proxy = (RadiusProxy)bo;
			loadRadiusProxy(proxy);
		}else if(bo instanceof IpAddress){
			IpAddress ipAddr = (IpAddress)bo;
			if(ipAddr.getItems() != null)
				ipAddr.getItems().size();
		}else if(bo instanceof RadiusAttrs){
			RadiusAttrs radiusAttrs = (RadiusAttrs)bo;
			if(radiusAttrs.getItems() != null)
				radiusAttrs.getItems().size();
		}else if(bo instanceof Vlan){
			Vlan vlan = (Vlan)bo;
			if(vlan.getItems() != null)
				vlan.getItems().size();
		}else if(bo instanceof MacOrOui){
			MacOrOui mac = (MacOrOui)bo;
			if(mac.getItems() != null)
				mac.getItems().size();
		}else if(bo instanceof UserProfileAttribute){
			UserProfileAttribute userAttr = (UserProfileAttribute)bo;
			if(userAttr.getItems() != null){
				userAttr.getItems().size();
			}
		}else if(bo instanceof LocationClientWatch){
			LocationClientWatch location = (LocationClientWatch)bo;
			if(location.getItems() != null)
				location.getItems().size();
		}else if(bo instanceof PortGroupProfile){
			PortGroupProfile portProfile = (PortGroupProfile)bo;
			if(portProfile != null)
				loadPortGroupProfile(portProfile);
		}else if(bo instanceof SubNetworkResource){
			SubNetworkResource resObj = (SubNetworkResource)bo;
			if(resObj.getVpnNetwork() != null){	
				resObj.getVpnNetwork().getId();
//				if(resObj.getVpnNetwork().getVlan() != null){
//					resObj.getVpnNetwork().getVlan().getId();
//				};
			}
		}else if(bo instanceof HMServicesSettings){
			HMServicesSettings settings = (HMServicesSettings)bo;
			if(settings.getWebsenseWhitelist() != null){
				if(settings.getWebsenseWhitelist().getItems() != null){
					settings.getWebsenseWhitelist().getItems().size();
				}
			}
			if(settings.getBarracudaWhitelist() != null){
				if(settings.getBarracudaWhitelist().getItems() != null){
					settings.getBarracudaWhitelist().getItems().size();
				}
			}
			if(settings.getOpenDNSAccount() != null){
				settings.getOpenDNSAccount().getId();
			}
		}else if(bo instanceof OsObject){
			OsObject osObj = (OsObject)bo;
			if(osObj.getItems() != null){
				osObj.getItems().size();
			}
			if(osObj.getDhcpItems() != null){
				osObj.getDhcpItems().size();
			}
		}else if(bo instanceof HiveApAutoProvision) {
			HiveApAutoProvision autoProvision = (HiveApAutoProvision) bo;

			if (autoProvision.getMacAddresses() != null) {
				autoProvision.getMacAddresses().size();
			}
			if(autoProvision.getDeviceInterfaces() != null)
				autoProvision.getDeviceInterfaces().size();
			if(autoProvision.getIpSubNetworks() != null)
				autoProvision.getIpSubNetworks().size();
		}else if(bo instanceof ApplicationProfile) {
			ApplicationProfile profile = (ApplicationProfile) bo;
			if (profile.getApplicationList() != null) {
				profile.getApplicationList().size();
			}
			if (profile.getCustomApplicationList() != null) {
				profile.getCustomApplicationList().size();
				for (CustomApplication ca : profile.getCustomApplicationList()) {
					if (ca.getRules() != null) {
						ca.getRules().size();
					}
				}
 			}
		}else if(bo instanceof CustomApplication) {
			CustomApplication ca = (CustomApplication) bo;
			if (ca.getRules() != null) {
				ca.getRules().size();
			}
		}
		else if(bo instanceof IpPolicy){
			this.loadIpPolicy(bo);
		}

		return null;
	}

    private void loadIpCollectionItems(Collection<IpAddress> collection) {
        for (IpAddress ipAddress : collection) {
            loadIpItems(ipAddress);
        }
    }
    
    private void loadIpItems(IpAddress ipAddress) {
    	if (null != ipAddress && null != ipAddress.getItems() )
    		ipAddress.getItems().size();
    }
    
    private void loadMacOrOuiCollectionItems(Collection<MacOrOui> collection) {
    	for (MacOrOui macOrOui : collection) {
    		loadMacOuiItems(macOrOui);
    	}
    }
    
    private void loadMacOuiItems(MacOrOui macOrOui) {
    	if (null != macOrOui && null != macOrOui.getItems())
    		macOrOui.getItems().size();
    }
    
    private void loadVlanCollectionItems(Collection<Vlan> collection) {
    	for (Vlan vlan : collection) {
    		loadVlanItems(vlan);
    	}
    }
    
    private void loadVlanItems(Vlan vlan) {
    	if (null != vlan && null != vlan.getItems())
    		vlan.getItems().size();
    }
    
    private void loadSsidProfile(SsidProfile ssidProfileObj){
    	if(ssidProfileObj == null){
    		return;
    	}

		if (ssidProfileObj.getOwner() != null)
			ssidProfileObj.getOwner().getId();
		if (ssidProfileObj.getSchedulers() != null)
			ssidProfileObj.getSchedulers().size();
		if (ssidProfileObj.getMacFilters() != null)
			for(MacFilter mf : ssidProfileObj.getMacFilters()){
				this.loadMacFilter(mf);
			}
		if (ssidProfileObj.getRadiusUserProfile() != null)
			ssidProfileObj.getRadiusUserProfile().size();
		if (ssidProfileObj.getLocalUserGroups() != null)
			ssidProfileObj.getLocalUserGroups().size();
		if (ssidProfileObj.getRadiusUserGroups() != null)
			ssidProfileObj.getRadiusUserGroups().size();
		if (ssidProfileObj.getGRateSets() != null)
			ssidProfileObj.getGRateSets().values();
		if (ssidProfileObj.getARateSets() != null)
			ssidProfileObj.getARateSets().values();
		if (ssidProfileObj.getNRateSets() != null)
			ssidProfileObj.getNRateSets().values();
		if (ssidProfileObj.getAcRateSets() != null)
			ssidProfileObj.getAcRateSets().size();
		if (ssidProfileObj.getSsidDos() != null){
			if(ssidProfileObj.getSsidDos().getDosParamsMap() != null){
				ssidProfileObj.getSsidDos().getDosParamsMap().values();
			}
		}
		if (ssidProfileObj.getIpDos() != null){
			if(ssidProfileObj.getIpDos().getDosParamsMap() != null){
				ssidProfileObj.getIpDos().getDosParamsMap().values();
			}
		}
		if (ssidProfileObj.getStationDos() != null){
			if(ssidProfileObj.getStationDos().getDosParamsMap() != null){
				ssidProfileObj.getStationDos().getDosParamsMap().values();
			}
		}
		if (ssidProfileObj.getRadiusAssignmentPpsk() != null && ssidProfileObj.getRadiusAssignmentPpsk().getServices() != null){
			loadRadiusAssignment(ssidProfileObj.getRadiusAssignmentPpsk());
		}
		
		if (ssidProfileObj.getRadiusAssignment() != null && ssidProfileObj.getRadiusAssignment().getServices() != null){
			loadRadiusAssignment(ssidProfileObj.getRadiusAssignment());
		}
		
		if(ssidProfileObj.getUserProfileSelfReg() != null){
			loadUserProfile(ssidProfileObj.getUserProfileSelfReg());
		}
		if(ssidProfileObj.getUserProfileDefault() != null){
			loadUserProfile(ssidProfileObj.getUserProfileDefault());
		}
		if(ssidProfileObj.getRadiusUserProfile() != null){
			for(UserProfile upObj : ssidProfileObj.getRadiusUserProfile()){
				loadUserProfile(upObj);
			}
		}
		if(ssidProfileObj.getCwp() != null)
			loadCwp(ssidProfileObj.getCwp());
		if(ssidProfileObj.getUserPolicy() != null)
			loadCwp(ssidProfileObj.getUserPolicy());
		if(ssidProfileObj.getPpskECwp() != null)
			loadCwp(ssidProfileObj.getPpskECwp());
		if(ssidProfileObj.getWpaECwp() != null)
			loadCwp(ssidProfileObj.getWpaECwp());
		if (ssidProfileObj.getUserProfileGuest() != null) {
		    ssidProfileObj.getUserProfileGuest().getId();
		}
    }
    
    private void loadPortProfile(PortGroupProfile lanProfile){
    	if(lanProfile == null){
    		return;
    	}
		if(lanProfile.getBasicProfiles() != null){
			lanProfile.getBasicProfiles().size();
			for (PortBasicProfile basic : lanProfile.getBasicProfiles()) {
				basic.getAccessProfile().getId();
				if(null != basic.getAccessProfile().getCwp()){
					basic.getAccessProfile().getCwp().getId();
				}
				if(null != basic.getAccessProfile().getDefUserProfile()){
					basic.getAccessProfile().getDefUserProfile().getId();
					if(null != basic.getAccessProfile().getDefUserProfile().getVlan()){
						basic.getAccessProfile().getDefUserProfile().getVlan().getId();
					}
				}
				if(null != basic.getAccessProfile().getSelfRegUserProfile()){
					basic.getAccessProfile().getSelfRegUserProfile().getId();
					if(null != basic.getAccessProfile().getSelfRegUserProfile().getVlan()){
						basic.getAccessProfile().getSelfRegUserProfile().getVlan().getId();
					}
				}
//				if(null != basic.getAccessProfile().getDataUserProfile()){
//					basic.getAccessProfile().getDataUserProfile().getId();
//					if(null != basic.getAccessProfile().getDataUserProfile().getVlan()){
//						basic.getAccessProfile().getDataUserProfile().getVlan().getId();
//					}
//				}
				if(null != basic.getAccessProfile().getAuthOkUserProfile()){
					basic.getAccessProfile().getAuthOkUserProfile().size();
					for(UserProfile userProfile : basic.getAccessProfile().getAuthOkUserProfile()){
						userProfile.getVlan().getId();
					}
				}
				if(null != basic.getAccessProfile().getAuthOkDataUserProfile()){
					basic.getAccessProfile().getAuthOkDataUserProfile().size();
					for(UserProfile userProfile : basic.getAccessProfile().getAuthOkDataUserProfile()){
						userProfile.getVlan().getId();
					}
				}
				if(null != basic.getAccessProfile().getAuthFailUserProfile()){
					basic.getAccessProfile().getAuthFailUserProfile().size();
					for(UserProfile userProfile : basic.getAccessProfile().getAuthFailUserProfile()){
						userProfile.getVlan().getId();
					}
				}
			}
		}
    	if (lanProfile.getPortPseProfiles() != null) {
    		lanProfile.getPortPseProfiles().size();
    		for (PortPseProfile profile : lanProfile.getPortPseProfiles()) {
    			if (profile.getPseProfile() != null) {
    				profile.getPseProfile().getId();
    			}
    		}
    	}
    }
    
    private void loadVpnNetwork(VpnNetwork vpnNetwork){
    	if(vpnNetwork == null){
    		return;
    	}
    	
//    	if(vpnNetwork.getVlan() != null)
//    		loadVlanItems(vpnNetwork.getVlan());
    	if(vpnNetwork.getVpnDnsService() != null)
    		this.loadDnsServiceProfile(vpnNetwork.getVpnDnsService());
    	if(vpnNetwork.getSubItems() != null)
    		vpnNetwork.getSubItems().size();
    	if(vpnNetwork.getSubItems().size() > 0){
    		for(VpnNetworkSub subnet : vpnNetwork.getSubItems()){
    			if(subnet.isOverrideDNSService()){
    				if(subnet.getDnsService() != null){
    					subnet.getDnsService().getId();
    					if(subnet.getDnsService().getSpecificInfos() != null){
    						subnet.getDnsService().getSpecificInfos().size();
    						if(subnet.getDnsService().getDomainObj() != null){
    							if(subnet.getDnsService().getDomainObj().getItems() != null){
    								subnet.getDnsService().getDomainObj().getItems().size();
    							}
    						}
    					}
    					if(subnet.getDnsService().getExternalDns1() != null){
    						if(subnet.getDnsService().getExternalDns1().getItems() != null && 
    								!subnet.getDnsService().getExternalDns1().getItems().isEmpty()){
    							subnet.getDnsService().getExternalDns1().getItems().size();
    						}
    					}
    					
    					if(subnet.getDnsService().getExternalDns2() != null){
    						if(subnet.getDnsService().getExternalDns2().getItems() != null && 
    								!subnet.getDnsService().getExternalDns2().getItems().isEmpty()){
    							subnet.getDnsService().getExternalDns2().getItems().size();
    						}
    					}
    					
    					if(subnet.getDnsService().getExternalDns3() != null){
    						if(subnet.getDnsService().getExternalDns3().getItems() != null && 
    								!subnet.getDnsService().getExternalDns3().getItems().isEmpty()){
    							subnet.getDnsService().getExternalDns3().getItems().size();
    						}
    					}
    					
    					if(subnet.getDnsService().getInternalDns1() != null){
    						if(subnet.getDnsService().getInternalDns1().getItems() != null && 
    								!subnet.getDnsService().getInternalDns1().getItems().isEmpty()){
    							subnet.getDnsService().getInternalDns1().getItems().size();
    						}
    					}
    					
    					if(subnet.getDnsService().getInternalDns2() != null){
    						if(subnet.getDnsService().getInternalDns2().getItems() != null && 
    								!subnet.getDnsService().getInternalDns2().getItems().isEmpty()){
    							subnet.getDnsService().getInternalDns2().getItems().size();
    						}
    					}
    					
    					if(subnet.getDnsService().getInternalDns3() != null){
    						if(subnet.getDnsService().getInternalDns3().getItems() != null && 
    								!subnet.getDnsService().getInternalDns3().getItems().isEmpty()){
    							subnet.getDnsService().getInternalDns3().getItems().size();
    						}
    					}
    					
    				}
    			}
    		}
    	}
    	if(vpnNetwork.getSubNetwokClass() != null)
    		vpnNetwork.getSubNetwokClass().size();
    	if(vpnNetwork.getReserveClass() != null)
    		vpnNetwork.getReserveClass().size();
    	if(vpnNetwork.getCustomOptions() != null)
    		vpnNetwork.getCustomOptions().size();
    	if(vpnNetwork.getSubNetworkRes() != null)
    		vpnNetwork.getSubNetworkRes().size();
    	if(vpnNetwork.getSubnetworkDHCPCustoms() != null)
    		vpnNetwork.getSubnetworkDHCPCustoms().size();
    	if(vpnNetwork.getPortForwardings() != null)
    		vpnNetwork.getPortForwardings().size();
    }
    
    private void loadDnsServiceProfile(DnsServiceProfile dnsServiceProfile){
    	if(dnsServiceProfile == null){
    		return;
    	}
    	
    	if(dnsServiceProfile.getInternalDns1() != null)
    		loadIpItems(dnsServiceProfile.getInternalDns1());
    	if(dnsServiceProfile.getInternalDns2() != null)
    		loadIpItems(dnsServiceProfile.getInternalDns2());
    	if(dnsServiceProfile.getInternalDns3() != null)
    		loadIpItems(dnsServiceProfile.getInternalDns3());
    	if(dnsServiceProfile.getExternalDns1() != null)
    		loadIpItems(dnsServiceProfile.getExternalDns1());
		if(dnsServiceProfile.getExternalDns2() != null)
			loadIpItems(dnsServiceProfile.getExternalDns2());
		if(dnsServiceProfile.getExternalDns3() != null)
			loadIpItems(dnsServiceProfile.getExternalDns3());
    	if(dnsServiceProfile.getDomainObj() != null){
    		if(dnsServiceProfile.getDomainObj().getItems() != null){
    			dnsServiceProfile.getDomainObj().getItems().size();
    		}
    	}
    	if(dnsServiceProfile.getSpecificInfos() != null){
    		for(DnsSpecificSettings subBo : dnsServiceProfile.getSpecificInfos()){
    			if(subBo.getDnsServer() != null){
    				this.loadIpItems(subBo.getDnsServer());
    			}
    		}
    	}
    }
    
    private void loadUserProfile(UserProfile userProfile){
    	if(userProfile == null){
    		return;
    	}
    	if (userProfile.getUserProfileAttribute() != null){
    		if(userProfile.getUserProfileAttribute().getItems() != null){
    			userProfile.getUserProfileAttribute().getItems().size();
    		}
    	}
		if (userProfile.getIpPolicyFrom() != null)
			loadIpPolicy(userProfile.getIpPolicyFrom());
		if (userProfile.getIpPolicyTo() != null)
			loadIpPolicy(userProfile.getIpPolicyTo());
		if (userProfile.getMacPolicyFrom() != null)
			loadMacPolicy(userProfile.getMacPolicyFrom());
		if (userProfile.getMacPolicyTo() != null)
			loadMacPolicy(userProfile.getMacPolicyTo());
		if (userProfile.getQosRateControl() != null){
			userProfile.getQosRateControl().getId();
			if(userProfile.getQosRateControl().getQosRateLimit() != null){
				userProfile.getQosRateControl().getQosRateLimit().size();
			}
		}
		if (userProfile.getTunnelSetting() != null)
			loadTunnelSetting(userProfile.getTunnelSetting());
		if (userProfile.getVlan() != null){
			userProfile.getVlan().getId();
			loadVlanItems(userProfile.getVlan());
		}
		userProfile.getAttributeValue();
		if (userProfile.getAsRuleGroup() != null)
			userProfile.getAsRuleGroup().getId();
		if (userProfile.getUserProfileSchedulers()!=null) {
			userProfile.getUserProfileSchedulers().size();
			for (Scheduler scheduler : userProfile.getUserProfileSchedulers())
				scheduler.getId();
		}
		if(userProfile.getAssignRules() != null){
			for(DevicePolicyRule rule: userProfile.getAssignRules()){
				if(rule.getMacObj() != null && rule.getMacObj().getItems() != null)
					rule.getMacObj().getItems().size();
				if(rule.getOsObj() != null && rule.getOsObj().getItems() != null)
					rule.getOsObj().getItems().size();
				if(rule.getDomObj() != null && rule.getDomObj().getItems() != null)
					rule.getDomObj().getItems().size();
				if(rule.getOsObj() != null && rule.getOsObj().getDhcpItems() != null)
					rule.getOsObj().getDhcpItems().size();
			}
		}
		if(userProfile.getMarkerMap() != null)
			userProfile.getMarkerMap().getId();
//		if(userProfile.getNetworkObj() != null){
//			this.loadVpnNetwork(userProfile.getNetworkObj());
//		}
    }
    
    private void loadFirewallPolicy(FirewallPolicy policy){
    	if(policy == null){
    		return;
    	}
    	
    	if(policy.getRules() != null){
    		for(FirewallPolicyRule rule : policy.getRules()){
    			this.loadVpnNetwork(rule.getSourceNtObj());
    			this.loadVpnNetwork(rule.getDestinationNtObj());
    			this.loadIpItems(rule.getSourceIp());
    			this.loadIpItems(rule.getDestinationIp());
    		}
    	}
    }
    
    private void loadVpnService(VpnService vpnServerObj){
    	if(vpnServerObj == null){
    		return;
    	}
    	if(vpnServerObj.getVpnCredentials() != null)
			vpnServerObj.getVpnCredentials().size();
		if(vpnServerObj.getVpnGateWaysSetting() != null)
			vpnServerObj.getVpnGateWaysSetting().size();
		if(vpnServerObj.getUserProfileTrafficL3() != null){
			for(UserProfileForTrafficL3 l3Traffic : vpnServerObj.getUserProfileTrafficL3()){
				if(l3Traffic.getUserProfile() != null){
					l3Traffic.getUserProfile().getId();
				}
			}
		}
		if(vpnServerObj.getUserProfileTrafficL2() != null){
			for(UserProfileForTrafficL2 l2Traffic : vpnServerObj.getUserProfileTrafficL2()){
				if(l2Traffic.getUserProfile() != null){
					l2Traffic.getUserProfile().getId();
				}
			}
		}
		if(vpnServerObj.getDnsIp() != null){
			this.loadIpItems(vpnServerObj.getDnsIp());
		}
    }
    
    private void loadCwp(Cwp cwpObj){
    	if(cwpObj == null){
    		return;
    	}
		if(cwpObj.getCertificate() != null)
			cwpObj.getCertificate().getId();
		if(cwpObj.getVlan() != null){
			cwpObj.getVlan().getId();
			loadVlanItems(cwpObj.getVlan());
		}
		if(cwpObj.getWalledGarden() != null){
			for(WalledGardenItem item : cwpObj.getWalledGarden()){
				if(item.getServer() != null && item.getServer().getItems() != null){
					item.getServer().getItems().size();
				}
			}
		}
		if(cwpObj.getIpAddressSuccess() != null){
			this.loadIpItems(cwpObj.getIpAddressSuccess());
		}
		if(cwpObj.getIpAddressFailure() != null){
			this.loadIpItems(cwpObj.getIpAddressFailure());
		}
    }
    
    private void loadMgmtServiceDns(MgmtServiceDns dns){
    	if(dns == null){
    		return;
    	}
    	dns.getId();
		if(dns.getDnsInfo() != null) {
			dns.getDnsInfo().size();
			for (MgmtServiceDnsInfo dnsInfo : dns.getDnsInfo()) {
			    loadIpItems(dnsInfo.getIpAddress());
            }
		}
    }
    
    private void loadMgmtServiceTime(MgmtServiceTime ntp){
    	if(ntp == null){
    		return;
    	}
    	ntp.getId();
		if(ntp.getTimeInfo() != null) {
			ntp.getTimeInfo().size();
			for (MgmtServiceTimeInfo info : ntp.getTimeInfo()){
				loadIpItems(info.getIpAddress());
			}
		}
    }
    
    private void loadConfigTemplate(ConfigTemplate configTemplate){
    	if(configTemplate == null){
    		return;
    	}
    	
    	if (configTemplate.getHiveProfile() != null)
    		loadHiveProfile(configTemplate.getHiveProfile());
		if (configTemplate.getMgmtServiceDns() != null) {
			this.loadMgmtServiceDns(configTemplate.getMgmtServiceDns());
		}
		if (configTemplate.getMgmtServiceSyslog() != null) {
			this.loadMgmtServiceSyslog(configTemplate.getMgmtServiceSyslog());
		}
		if (configTemplate.getMgmtServiceSnmp() != null) {
			this.loadMgmtServiceSnmp(configTemplate.getMgmtServiceSnmp());
		}
		if (configTemplate.getMgmtServiceTime() != null) {
			loadMgmtServiceTime(configTemplate.getMgmtServiceTime());
		}
		if (configTemplate.getMgmtServiceOption() != null){
			if(configTemplate.getMgmtServiceOption().getMultipleVlan() != null){
				configTemplate.getMgmtServiceOption().getMultipleVlan().size();
			}
			if(configTemplate.getMgmtServiceOption().getRadiusServer() != null){
				this.loadRadiusAssignment(configTemplate.getMgmtServiceOption().getRadiusServer());
			}
		}
		if (configTemplate.getIdsPolicy() != null){
			IdsPolicy idsPolicyObj = (IdsPolicy) configTemplate.getIdsPolicy();
			if (idsPolicyObj.getIdsSsids() != null)
				idsPolicyObj.getIdsSsids().size();
			if (idsPolicyObj.getMacOrOuis() != null){
				idsPolicyObj.getMacOrOuis().size();
				loadMacOrOuiCollectionItems(idsPolicyObj.getMacOrOuis());
			}
			if (idsPolicyObj.getVlans() != null){
				idsPolicyObj.getVlans().size();
				loadVlanCollectionItems(idsPolicyObj.getVlans());
			}
		}
		if (configTemplate.getVlan() != null){
			configTemplate.getVlan().getId();
			loadVlanItems(configTemplate.getVlan());
		}
		if (configTemplate.getIpFilter() != null)
			loadIpFilter(configTemplate.getIpFilter());

		if (configTemplate.getEth0ServiceFilter() != null)
			configTemplate.getEth0ServiceFilter().getId();
		if (configTemplate.getEth0BackServiceFilter() != null)
			configTemplate.getEth0BackServiceFilter().getId();

		if (configTemplate.getEth1ServiceFilter() != null)
			configTemplate.getEth1ServiceFilter().getId();
		if (configTemplate.getEth1BackServiceFilter() != null)
			configTemplate.getEth1BackServiceFilter().getId();

		if (configTemplate.getAgg0ServiceFilter() != null)
			configTemplate.getAgg0ServiceFilter().getId();
		if (configTemplate.getAgg0BackServiceFilter() != null)
			configTemplate.getAgg0BackServiceFilter().getId();

		if (configTemplate.getRed0ServiceFilter() != null)
			configTemplate.getRed0ServiceFilter().getId();
		if (configTemplate.getRed0BackServiceFilter() != null)
			configTemplate.getRed0BackServiceFilter().getId();

		if (configTemplate.getWireServiceFilter() != null)
			configTemplate.getWireServiceFilter().getId();

		if (configTemplate.getLocationServer() != null){
			configTemplate.getLocationServer().getId();
			if(configTemplate.getLocationServer().getServerIP() != null){
				loadIpItems(configTemplate.getLocationServer().getServerIP());
			}
		}

		if (configTemplate.getAlgConfiguration() != null){
			configTemplate.getAlgConfiguration().getId();
			if(configTemplate.getAlgConfiguration().getItems() != null){
				configTemplate.getAlgConfiguration().getItems().values();
			}
		}
		if (configTemplate.getClassifierMap() != null)
			this.loadQosClassification(configTemplate.getClassifierMap());
		if (configTemplate.getMarkerMap() != null)
			configTemplate.getMarkerMap().getId();
		if (configTemplate.getSsidInterfaces() != null) {
			for(ConfigTemplateSsid ssidTemp : configTemplate.getSsidInterfaces().values()){
				if(ssidTemp.getSsidProfile() != null){
					this.loadSsidProfile(ssidTemp.getSsidProfile());
				}
			}
		}
		if (configTemplate.getAccessConsole() != null) {
			loadAccessConsole(configTemplate.getAccessConsole());
		}
		if (configTemplate.getRadiusAttrs()!= null) {
			configTemplate.getRadiusAttrs().getId();
			if(configTemplate.getRadiusAttrs().getItems()!=null){
				configTemplate.getRadiusAttrs().getItems().size();
			}
		}
		if (configTemplate.getVlanNative() != null){
			configTemplate.getVlanNative().getId();
			loadVlanItems(configTemplate.getVlanNative());
		}
		if (configTemplate.getVpnService() != null)
			loadVpnService(configTemplate.getVpnService());
//		if (configTemplate.getRouterIpTrack() != null)
//			configTemplate.getRouterIpTrack().getId();
		if (configTemplate.getIpTracks() != null)
			configTemplate.getIpTracks().size();
		if (configTemplate.getLldpCdp() != null)
			configTemplate.getLldpCdp().getId();
		if (configTemplate.getClientWatch() != null){
			configTemplate.getClientWatch().getId();
			if(configTemplate.getClientWatch().getItems() != null){
				configTemplate.getClientWatch().getItems().size();
			}
		}
		if (configTemplate.getTvNetworkService() != null)
			configTemplate.getTvNetworkService().size();
		if (configTemplate.getPortProfiles() != null){
			for(PortGroupProfile portProfile : configTemplate.getPortProfiles()){
				this.loadPortProfile(portProfile);
			}
		}
//		if (configTemplate.getMgtNetwork() != null)
//			loadVpnNetwork(configTemplate.getMgtNetwork());
		if (configTemplate.getFwPolicy() != null)
			this.loadFirewallPolicy(configTemplate.getFwPolicy());
		if (configTemplate.getRadiusServerProfile() != null)
			loadRadiusOnHiveap(configTemplate.getRadiusServerProfile());
		if (configTemplate.getRadiusProxyProfile() != null)
			this.loadRadiusProxy(configTemplate.getRadiusProxyProfile());
		if (configTemplate.getBonjourGw() != null)
			this.loadBonjourGatewaySettings(configTemplate.getBonjourGw());
		if (configTemplate.getRoutingPolicy() != null){
			loadRoutingPolicy(configTemplate.getRoutingPolicy());
		}
		if (configTemplate.getRoutingProfilePolicy() != null){
			loadRoutingProfilePolicy(configTemplate.getRoutingProfilePolicy());
		}
		if(configTemplate.getAppProfile() != null){
		    configTemplate.getAppProfile().getId();
		    if(configTemplate.getAppProfile().getApplicationList() != null){
			    configTemplate.getAppProfile().getApplicationList().size();
		    }
	    }
		if(configTemplate.getVlanNetwork() != null){
			for(ConfigTemplateVlanNetwork vlanNet : configTemplate.getVlanNetwork()){
				if(vlanNet.getVlan() != null){
					this.loadVlanItems(vlanNet.getVlan());
				}
				if(vlanNet.getNetworkObj() != null){
					this.loadVpnNetwork(vlanNet.getNetworkObj());
				}
			}
		}
		if(configTemplate.getPortProfiles() != null){
			for(PortGroupProfile group : configTemplate.getPortProfiles()){
				this.loadPortGroupProfile(group);
			}
		}
		if(configTemplate.getUpVlanMapping() != null){
			for(UserProfileVlanMapping map : configTemplate.getUpVlanMapping()){
				this.loadUserProfileVlanMapping(map);
			}
		}
		if(configTemplate.getSwitchSettings() != null){
			this.loadSwitchSettings(configTemplate.getSwitchSettings());
		}
		if(configTemplate.getStormControlList() != null){
			configTemplate.getStormControlList().size();
		}
		if(configTemplate.getDeviceServiceFilter() != null){
			configTemplate.getDeviceServiceFilter().getId();
		}
		if(configTemplate.getSupplementalCLI() != null){
			configTemplate.getSupplementalCLI().getId();
		}
    }
    
    private void loadRadiusOnHiveap(RadiusOnHiveap radiusObj){
    	if(radiusObj == null){
    		return;
    	}

		if (radiusObj.getDirectoryOrLdap() != null){
			radiusObj.getDirectoryOrLdap().size();
			for(ActiveDirectoryOrLdapInfo ldap : radiusObj.getDirectoryOrLdap()){
				if (ldap.getDirectoryOrLdap() != null) {
					if (ldap.getDirectoryOrLdap().getAdDomains() != null)
						ldap.getDirectoryOrLdap().getAdDomains().size();

					loadIpItems(ldap.getDirectoryOrLdap().getAdServer());
					loadIpItems(ldap.getDirectoryOrLdap().getLdapServer());
				}
			}
		}
		if (radiusObj.getIpOrNames() != null){
			radiusObj.getIpOrNames().size();
			for (RadiusHiveapAuth auth : radiusObj.getIpOrNames()) {
			    loadIpItems(auth.getIpAddress());
            }
		}
		if (radiusObj.getLdapOuUserProfiles() != null)
			radiusObj.getLdapOuUserProfiles().size();
		if (radiusObj.getLocalUserGroup() != null)
			radiusObj.getLocalUserGroup().size();
		if (radiusObj.getSipPolicy() != null){
			if(radiusObj.getSipPolicy().getRules() != null)
				radiusObj.getSipPolicy().getRules().size();
		}
		if (radiusObj.getSipPolicy() != null){
			if(radiusObj.getSipPolicy().getRules() != null)
				radiusObj.getSipPolicy().getRules().size();
		}
		loadIpItems(radiusObj.getSipServer());
    }
    
    private void loadRadiusProxy(RadiusProxy proxy){
    	if(proxy == null){
    		return;
    	}
    	
    	if(proxy.getRadiusNas() != null)
			proxy.getRadiusNas().size();
			for (RadiusHiveapAuth auth : proxy.getRadiusNas()) {
			    loadIpItems(auth.getIpAddress());
            }
		if(proxy.getRadiusRealm() != null){
			for(RadiusProxyRealm realm : proxy.getRadiusRealm()){
				loadRadiusAssignment(realm.getRadiusServer());
			}
		}
    }
    
    private void loadRadiusAssignment(RadiusAssignment radiusAssProfile){
    	if(radiusAssProfile == null){
    		return;
    	}
		if(radiusAssProfile.getServices() != null){
			radiusAssProfile.getServices().size();
			for(RadiusServer radiusServer : radiusAssProfile.getServices()){
				this.loadIpItems(radiusServer.getIpAddress());
			}
//			for (RadiusServer radiusServer : radiusAssProfile.getServices())
//				loadIpItems(radiusServer.getIpAddress());;;;;;;;}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
		}
    }
    
    private void loadTunnelSetting(HmBo bo){
		if(bo instanceof TunnelSetting){
			TunnelSetting tunnelProfile = (TunnelSetting)bo;
			
			loadIpItems(tunnelProfile.getIpAddress());
			
			if(tunnelProfile.getIpAddressList() != null){
				tunnelProfile.getIpAddressList().size();
				loadIpCollectionItems(tunnelProfile.getIpAddressList());
			}
		}
    }
    
    private void loadBonjourGatewaySettings(BonjourGatewaySettings bonjourPorifle){
    	if(bonjourPorifle.getBonjourActiveServices() != null)
    		bonjourPorifle.getBonjourActiveServices().size();
    	if(bonjourPorifle.getRules() != null){
    		bonjourPorifle.getRules().size();
    	}
    }
    
    private void loadRoutingPolicy(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	if(bo instanceof RoutingPolicy){
    		RoutingPolicy policy = (RoutingPolicy)bo;
    		
    		if(policy.getRoutingPolicyRuleList() != null){
    			for(RoutingPolicyRule rule : policy.getRoutingPolicyRuleList()){
    				if(rule.getSourceUserProfile() != null){
    					rule.getSourceUserProfile().getId();
    				}
    			}
    		}
    		if(policy.getDomainObjectForDesList() != null){
    			if(policy.getDomainObjectForDesList().getItems() != null){
    				policy.getDomainObjectForDesList().getItems().size();
    			}
    		}
    	}
    }
    
    private void loadRoutingProfilePolicy(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	if (bo instanceof RoutingProfilePolicy) {
    		RoutingProfilePolicy routingprofilepolicy = (RoutingProfilePolicy)bo;
			if(routingprofilepolicy!=null){
				routingprofilepolicy.getId();
				if(routingprofilepolicy.getRoutingProfilePolicyRuleList()!=null){
					routingprofilepolicy.getRoutingProfilePolicyRuleList().size();
				}
			}
		}
    	
    }
    
    
    private void loadRadioProfile(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	if(bo instanceof RadioProfile){
    		RadioProfile radio = (RadioProfile)bo;
    		
    		if(radio.getWmmItems() != null)
    			radio.getWmmItems().size();
    		if (radio.getSupressBprOUIs() != null) {
    			radio.getSupressBprOUIs().size();
    			for (MacOrOui macOrOui : radio.getSupressBprOUIs()) {
    				if (macOrOui.getItems() != null) {
    					macOrOui.getItems().size();
    				}
    			}
    		}
    			
    	}
    }
    
    private void loadPortGroupProfile(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	if(bo instanceof PortGroupProfile){
    		PortGroupProfile portProfile = (PortGroupProfile)bo;
    		
    		if(portProfile.getBasicProfiles() != null){
    			for(PortBasicProfile base : portProfile.getBasicProfiles()){
    				loadPortAccessProfile(base.getAccessProfile());
    			}
    		}
    		if(portProfile.getMonitorProfiles() != null){
    			portProfile.getMonitorProfiles().size();
    		}
    		if(portProfile.getPortPseProfiles() != null){
    			portProfile.getPortPseProfiles().size();
    		}
    		if(portProfile.getItems() != null){
    			portProfile.getItems().size();
    		}
    	}
    }
    
    private void loadPortAccessProfile(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	if(bo instanceof PortAccessProfile){
    		PortAccessProfile accessProfile = (PortAccessProfile)bo;
    		
    		if(accessProfile.getRadiusAssignment() != null)
    			this.loadRadiusAssignment(accessProfile.getRadiusAssignment());
    		if(accessProfile.getCwp() != null)
    			this.loadCwp(accessProfile.getCwp());
    		if(accessProfile.getDefUserProfile() != null)
    			this.loadUserProfile(accessProfile.getDefUserProfile());
    		if(accessProfile.getSelfRegUserProfile() != null)
    		    this.loadUserProfile(accessProfile.getSelfRegUserProfile());
//    		if(accessProfile.getDataUserProfile() != null)
//    		    this.loadUserProfile(accessProfile.getDataUserProfile());
    		if(accessProfile.getVoiceVlan() != null)
    			this.loadVlanItems(accessProfile.getVoiceVlan());
    		if(accessProfile.getDataVlan() != null)
    			this.loadVlanItems(accessProfile.getDataVlan());
    		if(accessProfile.getAuthOkUserProfile() != null){
    			for(UserProfile up : accessProfile.getAuthOkUserProfile()){
    				this.loadUserProfile(up);
    			}
    		}
    		if(accessProfile.getAuthFailUserProfile() != null){
    			for(UserProfile up : accessProfile.getAuthFailUserProfile()){
    				this.loadUserProfile(up);
    			}
    		}
    		if(accessProfile.getAuthOkDataUserProfile() != null){
    			for(UserProfile up : accessProfile.getAuthOkDataUserProfile()){
    				this.loadUserProfile(up);
    			}
    		}
    		if(accessProfile.getNativeVlan() != null)
    			this.loadVlanItems(accessProfile.getNativeVlan());
    		if(accessProfile.getServiceFilter() != null)
    			accessProfile.getServiceFilter().getId();
    		if(accessProfile.getRadiusUserGroups() != null)
    			accessProfile.getRadiusUserGroups().size();
    	}
    }
    
    private void loadUserProfileVlanMapping(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	if(bo instanceof UserProfileVlanMapping){
    		UserProfileVlanMapping mapping = (UserProfileVlanMapping)bo;
    		
    		if(mapping.getUserProfile() != null)
    			mapping.getUserProfile().getId();
    		if(mapping.getVlan() != null)
    			this.loadVlanItems(mapping.getVlan());
    	}
    }
    
    private void loadSwitchSettings(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	if(bo instanceof SwitchSettings){
    		SwitchSettings swhSet = (SwitchSettings)bo;
    		
    		if(swhSet.getStpSettings() != null){
    			this.loadStpSettings(swhSet.getStpSettings());
    		}
    	}
    }
    
    private void loadDeviceStpSettings(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	if(bo instanceof DeviceStpSettings){
    		DeviceStpSettings stpSettings = (DeviceStpSettings)bo;
    		
    		if(stpSettings.getInterfaceStpSettings() != null)
    			stpSettings.getInterfaceStpSettings().size();
    		if(stpSettings.getInterfaceMstpSettings() != null)
    			stpSettings.getInterfaceMstpSettings().size();
    		if(stpSettings.getInstancePriority() != null)
    			stpSettings.getInstancePriority().size();
    	}
    }
    
    private void loadStpSettings(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	if(bo instanceof StpSettings){
    		StpSettings stpSettings = (StpSettings)bo;
    		
    		if(stpSettings.getMstpRegion() != null){
    			if(stpSettings.getMstpRegion().getMstpRegionPriorityList() != null){
    				stpSettings.getMstpRegion().getMstpRegionPriorityList().size();
    			}
    		}
    	}
    }
    
    private void loadQosClassification(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	if(bo instanceof QosClassification){
    		QosClassification qosClass = (QosClassification) bo;

    		if (qosClass.getNetworkServices() != null)
    			qosClass.getNetworkServices().values();
    		if (qosClass.getCustomServices() != null) {
    			qosClass.getCustomServices().values();
    			for (QosCustomService customService : qosClass.getCustomServices().values()) {
    				customService.getCustomAppService().getId();
    			}
    		}
    		if (qosClass.getQosMacOuis() != null){
    			qosClass.getQosMacOuis().values();
    			for (QosMacOui qosMacOui : qosClass.getQosMacOuis().values()) {
    				loadMacOuiItems(qosMacOui.getMacOui());
    			}
    		}
    		if (qosClass.getQosSsids() != null)
    			qosClass.getQosSsids().values();
    	}
    }
    
    private void loadAccessConsole(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	AccessConsole accessConsoleObj = (AccessConsole) bo;
		if (accessConsoleObj.getMacFilters() != null)
			for(MacFilter mf : accessConsoleObj.getMacFilters()){
				this.loadMacFilter(mf);
			}
    }
    
    private void loadIpFilter(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	IpFilter ipFilterObj = (IpFilter) bo;

		if (ipFilterObj.getOwner() != null)
			ipFilterObj.getOwner().getId();
		if (ipFilterObj.getIpAddress() != null){
			ipFilterObj.getIpAddress().size();
			loadIpCollectionItems(ipFilterObj.getIpAddress());
		}
    }
    
    private void loadHiveProfile(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	HiveProfile hiveProfileObj = (HiveProfile) bo;

		if (hiveProfileObj.getOwner() != null)
			hiveProfileObj.getOwner().getId();
		if (hiveProfileObj.getMacFilters() != null)
			for(MacFilter mf : hiveProfileObj.getMacFilters()){
				this.loadMacFilter(mf);
			}
		if (hiveProfileObj.getHiveDos() != null){
			if(hiveProfileObj.getHiveDos().getDosParamsMap() != null){
				hiveProfileObj.getHiveDos().getDosParamsMap().values();
			}
		}
		if (hiveProfileObj.getStationDos() != null){
			if(hiveProfileObj.getStationDos().getDosParamsMap() != null){
				hiveProfileObj.getStationDos().getDosParamsMap().values();
			}
		}
    }
    
    private void loadIpPolicy(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	/** IpPolicy */
		IpPolicy ipPolicyObj = (IpPolicy) bo;

		if (ipPolicyObj.getRules() != null){
			ipPolicyObj.getRules().size();
			for (IpPolicyRule rule : ipPolicyObj.getRules()) {
			    loadIpItems(rule.getDesctinationIp());
			    loadIpItems(rule.getSourceIp());
            }
		}
    }
    
    private void loadMacPolicy(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	MacPolicy macPolicyObj = (MacPolicy) bo;

		if (macPolicyObj.getRules() != null){
			macPolicyObj.getRules().size();
			for (MacPolicyRule rule : macPolicyObj.getRules()) {
				loadMacOuiItems(rule.getSourceMac());
				loadMacOuiItems(rule.getDestinationMac());
			}
		}
    }
    
    private void loadMacFilter(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	/** MacFilter */
		MacFilter macFilterObj = (MacFilter) bo;

		if (macFilterObj.getOwner() != null)
			macFilterObj.getOwner().getId();
		if (macFilterObj.getFilterInfo() != null)
			macFilterObj.getFilterInfo().size();
		for (MacFilterInfo macFilterInfo : macFilterObj.getFilterInfo()) {
			loadMacOuiItems(macFilterInfo.getMacOrOui());
		}
    }
    
    private void loadMgmtServiceSyslog(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	MgmtServiceSyslog sysLog = (MgmtServiceSyslog)bo;
    	if(sysLog.getSyslogInfo() != null) {
			for (MgmtServiceSyslogInfo syslog : sysLog.getSyslogInfo()) {
			    loadIpItems(syslog.getIpAddress());
            }
		}
    }
    
    private void loadMgmtServiceSnmp(HmBo bo){
    	if(bo == null){
    		return;
    	}
    	MgmtServiceSnmp snmpObj = (MgmtServiceSnmp)bo;
		if(snmpObj.getSnmpInfo() != null) {
			for(MgmtServiceSnmpInfo infoObj : snmpObj.getSnmpInfo()){
				loadIpItems(infoObj.getIpAddress());
			}
		}
    }
    
    public static class SimpllyHiveAp implements QueryBo {

		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (bo instanceof HiveAp) {
				HiveAp hiveAp = (HiveAp)bo;
				
				if(hiveAp.getConfigTemplate() != null){
					if(hiveAp.getConfigTemplate().getSsidInterfaces() != null){
						for(ConfigTemplateSsid ssidTemp : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
							if(ssidTemp.getSsidProfile() != null){
								ssidTemp.getSsidProfile().getId();
							}
						}
					}
					
					if(hiveAp.getConfigTemplate().getPortProfiles() != null){
						for(PortGroupProfile group : hiveAp.getConfigTemplate().getPortProfiles()){
							if(group.getBasicProfiles() != null){
								for(PortBasicProfile basicProfile : group.getBasicProfiles()){
									if(basicProfile.getAccessProfile() != null){
										basicProfile.getAccessProfile().getId();
									}
								}
							}
						}
					}
				}
			}
			return null;
		}
    	
    }
    
}