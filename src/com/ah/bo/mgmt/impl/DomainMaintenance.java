/**
 *@filename		DomainMaintenance.java
 *@version		v1.1
 *@author		Fiona
 *@createtime	2008-6-6 PM 02:01:54
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.mgmt.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.admin.restoredb.AhRestoreNewMapTools;
import com.ah.be.admin.restoredb.AhRestoreNewTools;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.performance.BeOsInfoProcessor;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.admin.OpenDNSAccount;
import com.ah.bo.admin.OpenDNSDevice;
import com.ah.bo.admin.OpenDNSMapping;
import com.ah.bo.gml.PrintTemplate;
import com.ah.bo.gml.TemplateField;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.ConfigTemplateStormControl;
import com.ah.bo.hiveap.ConfigTemplateVlanNetwork;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.hiveap.HiveApUpdateSettings;
import com.ah.bo.hiveap.IdpSettings;
import com.ah.bo.hiveap.MdmProfiles;
import com.ah.bo.hiveap.WifiClientPreferredSsid;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosCustomService;
import com.ah.bo.mobility.QosMacOui;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.mobility.QosNetworkService;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.mobility.QosRateLimit;
import com.ah.bo.mobility.QosSsid;
import com.ah.bo.mobility.TunnelSetting;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.network.AccessConsole;
import com.ah.bo.network.AirScreenRuleGroup;
import com.ah.bo.network.AlgConfiguration;
import com.ah.bo.network.AlgConfigurationInfo;
import com.ah.bo.network.AlgConfigurationInfo.GatewayType;
import com.ah.bo.network.Application;
import com.ah.bo.network.ApplicationProfile;
import com.ah.bo.network.BonjourActiveService;
import com.ah.bo.network.BonjourFilterRule;
import com.ah.bo.network.BonjourGatewaySettings;
import com.ah.bo.network.BonjourService;
import com.ah.bo.network.BonjourServiceCategory;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.CustomApplicationRule;
import com.ah.bo.network.DevicePolicy;
import com.ah.bo.network.DevicePolicyRule;
import com.ah.bo.network.DhcpServerIpPool;
import com.ah.bo.network.DhcpServerOptionsCustom;
import com.ah.bo.network.DnsServiceProfile;
import com.ah.bo.network.DnsSpecificSettings;
import com.ah.bo.network.DomainNameItem;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.DosParams;
import com.ah.bo.network.DosParams.FrameType;
import com.ah.bo.network.DosParams.ScreeningType;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.DosPrevention.DosType;
import com.ah.bo.network.FirewallPolicy;
import com.ah.bo.network.FirewallPolicyRule;
import com.ah.bo.network.IdsPolicy;
import com.ah.bo.network.IdsPolicySsidProfile;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.IpFilter;
import com.ah.bo.network.IpPolicy;
import com.ah.bo.network.IpPolicyRule;
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacFilterInfo;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.MacPolicy;
import com.ah.bo.network.MacPolicyRule;
import com.ah.bo.network.MstpRegion;
import com.ah.bo.network.MstpRegionPriority;
import com.ah.bo.network.NeighborsNameItem;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.OsObjectVersion;
import com.ah.bo.network.PortForwarding;
import com.ah.bo.network.PseProfile;
import com.ah.bo.network.RadiusAttrs;
import com.ah.bo.network.RoutingPolicy;
import com.ah.bo.network.RoutingPolicyRule;
import com.ah.bo.network.RoutingProfile;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.bo.network.RoutingProfilePolicyRule;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.StpSettings;
import com.ah.bo.network.SubnetworkDHCPCustom;
import com.ah.bo.network.SwitchSettings;
import com.ah.bo.network.UserProfileForTrafficL2;
import com.ah.bo.network.UserProfileForTrafficL3;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VlanDhcpServer;
import com.ah.bo.network.VlanGroup;
import com.ah.bo.network.VpnGatewaySetting;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnNetworkSub;
import com.ah.bo.network.VpnService;
import com.ah.bo.network.VpnServiceCredential;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.port.PortPseProfile;
import com.ah.bo.useraccess.ActiveDirectoryDomain;
import com.ah.bo.useraccess.ActiveDirectoryOrLdapInfo;
import com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap;
import com.ah.bo.useraccess.LdapServerOuUserProfile;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.LocationServer;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceDnsInfo;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.MgmtServiceSnmpInfo;
import com.ah.bo.useraccess.MgmtServiceSyslog;
import com.ah.bo.useraccess.MgmtServiceSyslogInfo;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.bo.useraccess.MgmtServiceTimeInfo;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusHiveapAuth;
import com.ah.bo.useraccess.RadiusLibrarySip;
import com.ah.bo.useraccess.RadiusLibrarySipRule;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.useraccess.RadiusProxyRealm;
import com.ah.bo.useraccess.RadiusServer;
import com.ah.bo.useraccess.RadiusUserProfileRule;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.bo.useraccess.UserProfileVlanMapping;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.CwpCertificate;
import com.ah.bo.wlan.CwpPageCustomization;
import com.ah.bo.wlan.CwpPageField;
import com.ah.bo.wlan.EthernetAccess;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.RadioProfileWmmInfo;
import com.ah.bo.wlan.RadioProfileWmmInfo.AccessCategory;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SsidProfile;
import com.ah.bo.wlan.TX11aOr11gRateSetting;
import com.ah.bo.wlan.TX11aOr11gRateSetting.ARateType;
import com.ah.bo.wlan.TX11aOr11gRateSetting.GRateType;
import com.ah.bo.wlan.TX11aOr11gRateSetting.NRateType;
import com.ah.bo.wlan.Tx11acRateSettings;
import com.ah.bo.wlan.WalledGardenItem;
import com.ah.events.BoEvent;
import com.ah.events.BoEvent.BoEventType;
import com.ah.mdm.core.profile.entity.MdmObject;
import com.ah.mdm.core.profile.impl.ProfileMgrServiceImpl;
import com.ah.ui.actions.config.VpnNetworksAction;
import com.ah.util.Tracer;

/**
 * @author Fiona
 * @version v1.1
 */
public class DomainMaintenance implements QueryBo {

	private static final Tracer log = new Tracer(DomainMaintenance.class
			.getSimpleName());
	public static DomainMaintenance cloneInfo;

	private DomainMaintenance() {
	}

	public synchronized static DomainMaintenance getInstance() {
		if (cloneInfo == null) {
			cloneInfo = new DomainMaintenance();
		}

		return cloneInfo;
	}

	/**
	 * Clone all the ip address from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneIPAddress(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<IpAddress> list = getAlltheObjectsInDomain(IpAddress.class, srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (IpAddress bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<SingleTableItem> items = new ArrayList<SingleTableItem>();
			for (SingleTableItem info : bo.getItems()) {
				if (SingleTableItem.TYPE_MAP == info.getType()) {
					MapContainerNode newLocation = QueryUtil
							.findBoByAttribute(MapContainerNode.class,
									"mapName", info.getLocation().getMapName(),
									destDomain.getId());
					info.setLocation(newLocation);
				}
				items.add(info);
			}
			bo.setItems(items);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the mac address or oui from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneMACOrOUI(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<MacOrOui> list = getAlltheObjectsInDomain(MacOrOui.class, srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (MacOrOui bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<SingleTableItem> items = new ArrayList<SingleTableItem>();
			for (SingleTableItem info : bo.getItems()) {
				if (SingleTableItem.TYPE_MAP == info.getType()) {
					MapContainerNode newLocation = QueryUtil
							.findBoByAttribute(MapContainerNode.class,
									"mapName", info.getLocation().getMapName(),
									destDomain.getId());
					info.setLocation(newLocation);
				}
				items.add(info);
			}
			bo.setItems(items);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	public void cloneApplicationProfile(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<ApplicationProfile> list = getAlltheObjectsInDomain(ApplicationProfile.class, srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (ApplicationProfile bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			Set<Application> appList = new HashSet<Application>();
			appList.addAll(bo.getApplicationList());
			bo.setApplicationList(appList);
			Set<CustomApplication> customappList = new HashSet<CustomApplication>();
			customappList.addAll(bo.getCustomApplicationList());
			bo.setCustomApplicationList(customappList);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	/**
	 * Clone all the os object from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneOsObject(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<OsObject> list = getAlltheObjectsInDomain(OsObject.class, srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (OsObject bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<OsObjectVersion> items = new ArrayList<OsObjectVersion>();
			items.addAll(bo.getItems());
			bo.setItems(items);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
		BeOsInfoProcessor.getInstance().resetOsName(destDomain.getId());
	}
	
	/**
	 * Clone all the domain object from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneDomainObject(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<DomainObject> list = getAlltheObjectsInDomain(DomainObject.class, srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (DomainObject bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<DomainNameItem> items = new ArrayList<DomainNameItem>();
			items.addAll(bo.getItems());
			bo.setItems(items);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * clone location client watch bo
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneLocationClientWatch(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}

		List<LocationClientWatch> list = getAlltheObjectsInDomain(LocationClientWatch.class,
				srcDomain,this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (LocationClientWatch bo : list) {
			if (bo.isDefaultFlag()) {
				continue;
			}
			
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<SingleTableItem> items = new ArrayList<SingleTableItem>();
			for (SingleTableItem info : bo.getItems()) {
				if (SingleTableItem.TYPE_MAP == info.getType()) {
					MapContainerNode newLocation = QueryUtil
							.findBoByAttribute(MapContainerNode.class,
									"mapName", info.getLocation().getMapName(),
									destDomain.getId());
					info.setLocation(newLocation);
				}
				items.add(info);
			}
			bo.setItems(items);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the vlan from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneVlan(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<Vlan> list = getAlltheObjectsInDomain(Vlan.class, srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (Vlan bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<SingleTableItem> items = new ArrayList<SingleTableItem>();
			for (SingleTableItem info : bo.getItems()) {
				if (SingleTableItem.TYPE_MAP == info.getType()) {
					MapContainerNode newLocation = QueryUtil
							.findBoByAttribute(MapContainerNode.class,
									"mapName", info.getLocation().getMapName(),
									destDomain.getId());
					info.setLocation(newLocation);
				}
				items.add(info);
			}
			bo.setItems(items);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the user profile attribute from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneUserProfileAttribute(HmDomain srcDomain,
			HmDomain destDomain) throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<UserProfileAttribute> list = getAlltheObjectsInDomain(UserProfileAttribute.class,
				srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (UserProfileAttribute bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<SingleTableItem> items = new ArrayList<SingleTableItem>();
			for (SingleTableItem info : bo.getItems()) {
				if (SingleTableItem.TYPE_MAP == info.getType()) {
					MapContainerNode newLocation = QueryUtil
							.findBoByAttribute(MapContainerNode.class,
									"mapName", info.getLocation().getMapName(),
									destDomain.getId());
					info.setLocation(newLocation);
				}
				items.add(info);
			}
			bo.setItems(items);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the mac filter from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneMacFilter(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<MacFilter> list = getAlltheObjectsInDomain(MacFilter.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (MacFilter filter : list) {
			MacFilter bo = QueryUtil.findBoById(MacFilter.class,
					filter.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<MacFilterInfo> filterInfo = new ArrayList<MacFilterInfo>();
			for (MacFilterInfo info : bo.getFilterInfo()) {
				MacOrOui newMac = QueryUtil.findBoByAttribute(
						MacOrOui.class, "macOrOuiName", info.getMacOrOui()
								.getMacOrOuiName(), destDomain.getId());
				info.setMacOrOui(newMac);
				filterInfo.add(info);
			}
			bo.setFilterInfo(filterInfo);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the Hive Profile from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneHiveProfile(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain)
			return;
		List<HiveProfile> list = getAlltheObjectsInDomain(HiveProfile.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (HiveProfile hiveProfile : list) {
			HiveProfile bo = QueryUtil.findBoById(
					HiveProfile.class, hiveProfile.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			Set<MacFilter> cloneMacFilters = new HashSet<MacFilter>();
			for (MacFilter tempClass : bo.getMacFilters()) {
				MacFilter newTempClass = QueryUtil
						.findBoByAttribute(MacFilter.class, "filterName",
								tempClass.getFilterName(), destDomain.getId());
				cloneMacFilters.add(newTempClass);
			}
			bo.setMacFilters(cloneMacFilters);

			if (bo.getHiveDos() != null) {
				List<DosPrevention> newHiveDos = QueryUtil.executeQuery(DosPrevention.class, null,
						new FilterParams(
								"dosPreventionName=:s1 AND dosType=:s2",
								new Object[] {
										bo.getHiveDos().getDosPreventionName(),
										bo.getHiveDos().getDosType() }),
						destDomain.getId());
				if (!newHiveDos.isEmpty()) {
					bo.setHiveDos(newHiveDos.get(0));
				}
			}

			if (bo.getStationDos() != null) {
				List<DosPrevention> newStationDos = QueryUtil.executeQuery(DosPrevention.class, null,
						new FilterParams(
								"dosPreventionName=:s1 AND dosType=:s2",
								new Object[] {
										bo.getStationDos()
												.getDosPreventionName(),
										bo.getStationDos().getDosType() }),
						destDomain.getId());
				if (!newStationDos.isEmpty()) {
					bo.setStationDos(newStationDos.get(0));
				}
			}

			// if (bo.getInterRoaming() != null) {
			// InterRoaming newTempClass = (InterRoaming) QueryUtil
			// .findBoByAttribute(InterRoaming.class, "roamingName",
			// bo.getInterRoaming().getRoamingName(),
			// destDomain.getId());
			// bo.setInterRoaming(newTempClass);
			// }

			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	/**
	 * Clone all the Vpn Network from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneVpnNetwork(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain)
			return;
		List<VpnNetwork> list = getAlltheObjectsInDomain(VpnNetwork.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (VpnNetwork vpnNetwork : list) {
			VpnNetwork bo = QueryUtil.findBoById(
					VpnNetwork.class, vpnNetwork.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			
			List<DhcpServerOptionsCustom> optionCustons = new ArrayList<DhcpServerOptionsCustom>();
			for (DhcpServerOptionsCustom tempClass : bo.getCustomOptions()) {
				optionCustons.add(tempClass);
			}
			bo.setCustomOptions(optionCustons);

			List<VpnNetworkSub> netWorkSub = new ArrayList<VpnNetworkSub>();
			for (VpnNetworkSub tempClass : bo.getSubItems()) {
				if(tempClass.isOverrideDNSService() && tempClass.getDnsService() != null){
					tempClass.setDnsService(QueryUtil
						.findBoByAttribute(DnsServiceProfile.class, "serviceName",
								tempClass.getDnsService().getServiceName(),
								destDomain.getId()));
				}
				netWorkSub.add(tempClass);
			}
			bo.setSubItems(netWorkSub);
			
			List<SingleTableItem> vcs = new ArrayList<SingleTableItem>();
			for (SingleTableItem tempClass : bo.getSubNetwokClass()) {
				vcs.add(tempClass);
			}
			bo.setSubNetwokClass(vcs);
			
			List<SingleTableItem> vIps = new ArrayList<SingleTableItem>();
			for (SingleTableItem tempClass : bo.getReserveClass()) {
				vIps.add(tempClass);
			}
			bo.setReserveClass(vIps);
			
			List<PortForwarding> portForwardings = new ArrayList<PortForwarding>();
			for (PortForwarding tempClass : bo.getPortForwardings()) {
				portForwardings.add(tempClass);
			}
			bo.setPortForwardings(portForwardings);
			
			//subNetworkRes clone at the end of this function
			
			List<SubnetworkDHCPCustom> subCustoms = new ArrayList<SubnetworkDHCPCustom>();
			for(SubnetworkDHCPCustom subCustom : bo.getSubnetworkDHCPCustoms()){
				subCustoms.add(subCustom);
			}
			bo.setSubnetworkDHCPCustoms(subCustoms);

			if (bo.getVpnDnsService() != null) {
				bo.setVpnDnsService(QueryUtil
						.findBoByAttribute(DnsServiceProfile.class, "serviceName",
								bo.getVpnDnsService().getServiceName(),
								destDomain.getId()));
			}

			
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
		
		//subNetworkRes clone
		List<VpnNetwork> listNew = getAlltheObjectsInDomain(VpnNetwork.class, destDomain);
		for(VpnNetwork network : listNew){
			VpnNetworksAction.prepareSubNetworkRes(network.getId(), null);
		}
	}

	// /**
	// * Clone all the Personalized PSK from srcDomain to destDomain.
	// *
	// * @param srcDomain
	// * @param destDomain
	// * @throws Exception
	// */
	// public void clonePersonalizedPsk(HmDomain srcDomain, HmDomain destDomain)
	// throws Exception {
	// if (null == srcDomain || null == destDomain)
	// return;
	// List<PersonalizedPsk> list = getAlltheObjectsInDomain(PersonalizedPsk.class,
	// srcDomain);
	// List<HmBo> newBos = new ArrayList<HmBo>();
	// for (PersonalizedPsk bo : list) {
	// bo.setId(null);
	// bo.setOwner(destDomain);
	// bo.setVersion(null);
	//
	// if (bo.getSchedule() != null) {
	// bo.setSchedule((Scheduler) QueryUtil
	// .findBoByAttribute(Scheduler.class,
	// "schedulerName", bo.getSchedule()
	// .getSchedulerName(), destDomain
	// .getId()));
	// }
	//
	// if (bo.getPskGroup() != null) {
	// bo.setPskGroup((PersonalizedPskGroup) QueryUtil
	// .findBoByAttribute(PersonalizedPskGroup.class,
	// "pskGroupName", bo.getPskGroup()
	// .getPskGroupName(), destDomain
	// .getId()));
	// }
	//
	// newBos.add(bo);
	// }
	// if (!newBos.isEmpty()) {
	// QueryUtil.bulkCreateBos(newBos);
	// }
	// }

	/**
	 * Clone all the Ip Filter from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneIpFilter(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain)
			return;
		List<IpFilter> list = getAlltheObjectsInDomain(IpFilter.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (IpFilter ipFilter : list) {
			IpFilter bo = QueryUtil.findBoById(IpFilter.class,
					ipFilter.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			Set<IpAddress> cloneIpAddress = new HashSet<IpAddress>();
			for (IpAddress tempClass : bo.getIpAddress()) {
				IpAddress newTempClass = QueryUtil
						.findBoByAttribute(IpAddress.class, "addressName",
								tempClass.getAddressName(), destDomain.getId());
				cloneIpAddress.add(newTempClass);
			}
			bo.setIpAddress(cloneIpAddress);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the Ssid Profile from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneSsidProfile(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain)
			return;
		List<SsidProfile> list = getAlltheObjectsInDomain(SsidProfile.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (SsidProfile ssidProfile : list) {
			SsidProfile bo = QueryUtil.findBoById(
					SsidProfile.class, ssidProfile.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);

			// fix bug 5992 ----- begin
			Map<String, TX11aOr11gRateSetting> gRateSets = new HashMap<String, TX11aOr11gRateSetting>();
			for (GRateType gType : TX11aOr11gRateSetting.GRateType.values()) {
				TX11aOr11gRateSetting rateSet = bo
						.getTX11aOr11gRateSetting(gType);
				rateSet.setGRateType(gType);
				gRateSets.put(rateSet.getkey(), rateSet);
			}
			bo.setGRateSets(gRateSets);
			Map<String, TX11aOr11gRateSetting> aRateSets = new HashMap<String, TX11aOr11gRateSetting>();
			for (ARateType aType : TX11aOr11gRateSetting.ARateType.values()) {
				TX11aOr11gRateSetting rateSet = bo
						.getTX11aOr11gRateSetting(aType);
				rateSet.setARateType(aType);
				aRateSets.put(rateSet.getkey(), rateSet);
			}
			bo.setARateSets(aRateSets);
			
			Map<String, TX11aOr11gRateSetting> nRateSets = new HashMap<String, TX11aOr11gRateSetting>();
			for (NRateType nType : TX11aOr11gRateSetting.NRateType.values()) {
				TX11aOr11gRateSetting rateSet = bo
						.getTX11aOr11gRateSetting(nType);
				if (rateSet==null) {
					rateSet = new TX11aOr11gRateSetting();
					rateSet.setRateSet(TX11aOr11gRateSetting.RATE_SET_TYPE_OPT);
				}  
				rateSet.setNRateType(nType);
				nRateSets.put(rateSet.getkey(), rateSet);
			}
			bo.setNRateSets(nRateSets);
			
			// fix bug 5992 ----- end
			
			//clone 11ac rate settings
			
			if(bo.getAcRateSets() != null && bo.getAcRateSets().size() > 0){
				List<Tx11acRateSettings> acRateList = new ArrayList<Tx11acRateSettings>();
				for(Tx11acRateSettings settings : bo.getAcRateSets()){
					Tx11acRateSettings acRateSettings = new Tx11acRateSettings();
					acRateSettings.setStreamEnable(settings.isStreamEnable());
					acRateSettings.setMcsValue(settings.getMcsValue());
					acRateSettings.setStreamType(settings.getStreamType());
					acRateList.add(acRateSettings);
				}
				bo.setAcRateSets(acRateList);
			}else{
				List<Tx11acRateSettings> acRateList = new ArrayList<Tx11acRateSettings>();
				for (short i = Tx11acRateSettings.STREAM_TYPE_SINGLE; i <= Tx11acRateSettings.STREAM_TYPE_THREE; i ++){
					Tx11acRateSettings acRateSet = new Tx11acRateSettings();
					acRateSet.setStreamType(i);
					acRateSet.setMcsValue(Tx11acRateSettings.MAX_MCS_VALUE);
					acRateList.add(acRateSet);
				}
				bo.setAcRateSets(acRateList);
			}
			
			Set<Scheduler> cloneSchedulers = new HashSet<Scheduler>();
			for (Scheduler tempClass : bo.getSchedulers()) {
				Scheduler newTempClass = QueryUtil
						.findBoByAttribute(Scheduler.class, "schedulerName",
								tempClass.getSchedulerName(), destDomain
										.getId());
				cloneSchedulers.add(newTempClass);
			}
			bo.setSchedulers(cloneSchedulers);

			Set<MacFilter> cloneMacFilters = new HashSet<MacFilter>();
			for (MacFilter tempClass : bo.getMacFilters()) {
				MacFilter newTempClass = QueryUtil
						.findBoByAttribute(MacFilter.class, "filterName",
								tempClass.getFilterName(), destDomain.getId());
				cloneMacFilters.add(newTempClass);
			}
			bo.setMacFilters(cloneMacFilters);

			Set<UserProfile> cloneRadiusUserProfiles = new HashSet<UserProfile>();
			for (UserProfile tempClass : bo.getRadiusUserProfile()) {
				UserProfile newTempClass = QueryUtil
						.findBoByAttribute(UserProfile.class,
								"userProfileName", tempClass
										.getUserProfileName(), destDomain
										.getId());
				cloneRadiusUserProfiles.add(newTempClass);
			}
			bo.setRadiusUserProfile(cloneRadiusUserProfiles);

			Set<LocalUserGroup> cloneLocalUserGroup = new HashSet<LocalUserGroup>();
			for (LocalUserGroup tempClass : bo.getLocalUserGroups()) {
				LocalUserGroup newTempClass = QueryUtil
						.findBoByAttribute(LocalUserGroup.class, "groupName",
								tempClass.getGroupName(), destDomain.getId());
				cloneLocalUserGroup.add(newTempClass);
			}
			bo.setLocalUserGroups(cloneLocalUserGroup);

			Set<LocalUserGroup> cloneRadiusUserGroup = new HashSet<LocalUserGroup>();
			for (LocalUserGroup tempClass : bo.getRadiusUserGroups()) {
				LocalUserGroup newTempClass = QueryUtil
						.findBoByAttribute(LocalUserGroup.class, "groupName",
								tempClass.getGroupName(), destDomain.getId());
				cloneRadiusUserGroup.add(newTempClass);
			}
			bo.setRadiusUserGroups(cloneRadiusUserGroup);

			// if (bo.getPskGroup() != null) {
			// bo.setPskGroup((PersonalizedPskGroup) QueryUtil
			// .findBoByAttribute(PersonalizedPskGroup.class,
			// "pskGroupName", bo.getPskGroup()
			// .getPskGroupName(), destDomain
			// .getId()));
			// }

			if (bo.getSsidDos() != null) {
				List<DosPrevention> newSsidDos = QueryUtil.executeQuery(DosPrevention.class, null,
						new FilterParams(
								"dosPreventionName=:s1 AND dosType=:s2",
								new Object[] {
										bo.getSsidDos().getDosPreventionName(),
										bo.getSsidDos().getDosType() }),
						destDomain.getId());
				if (!newSsidDos.isEmpty()) {
					bo.setSsidDos(newSsidDos.get(0));
				}
			}

			if (bo.getStationDos() != null) {
				List<DosPrevention> newStationDos = QueryUtil.executeQuery(DosPrevention.class, null,
						new FilterParams(
								"dosPreventionName=:s1 AND dosType=:s2",
								new Object[] {
										bo.getStationDos()
												.getDosPreventionName(),
										bo.getStationDos().getDosType() }),
						destDomain.getId());
				if (!newStationDos.isEmpty()) {
					bo.setStationDos(newStationDos.get(0));
				}
			}

			if (bo.getIpDos() != null) {
				List<DosPrevention> newIpDos = QueryUtil.executeQuery(DosPrevention.class, null,
						new FilterParams(
								"dosPreventionName=:s1 AND dosType=:s2",
								new Object[] {
										bo.getIpDos().getDosPreventionName(),
										bo.getIpDos().getDosType() }),
						destDomain.getId());
				if (!newIpDos.isEmpty()) {
					bo.setIpDos(newIpDos.get(0));
				}
			}
			
			if (bo.getConfigmdmId() != null) {
				bo.setConfigmdmId(QueryUtil.findBoByAttribute(ConfigTemplateMdm.class, "policyname",bo.getConfigmdmId().getPolicyname(),
								destDomain.getId()));
				
			}

			if (bo.getServiceFilter() != null) {
				bo.setServiceFilter(QueryUtil
						.findBoByAttribute(ServiceFilter.class, "filterName",
								bo.getServiceFilter().getFilterName(),
								destDomain.getId()));
			}

			if (null != bo.getAsRuleGroup()) {
				AirScreenRuleGroup newRuleGroup = QueryUtil
						.findBoByAttribute(AirScreenRuleGroup.class,
								"profileName", bo.getAsRuleGroup()
										.getProfileName(), destDomain.getId());
				bo.setAsRuleGroup(newRuleGroup);
			}

			if (bo.getCwp() != null) {
				bo.setCwp(QueryUtil
						.findBoByAttribute(Cwp.class, "cwpName", bo.getCwp()
								.getCwpName(), destDomain.getId()));
			}

			if (bo.getUserPolicy() != null) {
				bo.setUserPolicy(QueryUtil.findBoByAttribute(Cwp.class,
						"cwpName", bo.getUserPolicy().getCwpName(), destDomain
								.getId()));
			}

			if (bo.getUserProfileDefault() != null) {
				bo.setUserProfileDefault(QueryUtil
						.findBoByAttribute(UserProfile.class,
								"userProfileName", bo.getUserProfileDefault()
										.getUserProfileName(), destDomain
										.getId()));
			}

			if (bo.getUserProfileSelfReg() != null) {
				bo.setUserProfileSelfReg(QueryUtil
						.findBoByAttribute(UserProfile.class,
								"userProfileName", bo.getUserProfileSelfReg()
										.getUserProfileName(), destDomain
										.getId()));
			}
			
			if (bo.getUserProfileGuest() != null) {
			    bo.setUserProfileGuest(QueryUtil
			            .findBoByAttribute(UserProfile.class,
			                    "userProfileName", bo.getUserProfileGuest()
			                    .getUserProfileName(), destDomain
			                    .getId()));
			}

			if (bo.getRadiusAssignment() != null) {
				bo.setRadiusAssignment(QueryUtil
						.findBoByAttribute(RadiusAssignment.class,
								"radiusName", bo.getRadiusAssignment()
										.getRadiusName(), destDomain.getId()));
			}
			
			if (bo.getRadiusAssignmentPpsk() !=null) {
				bo.setRadiusAssignmentPpsk(QueryUtil
						.findBoByAttribute(RadiusAssignment.class,
								"radiusName", bo.getRadiusAssignmentPpsk()
										.getRadiusName(), destDomain.getId()));
			}
			
			if (bo.getPpskECwp() !=null) {
				bo.setPpskECwp(QueryUtil
						.findBoByAttribute(Cwp.class,
								"cwpName", bo.getPpskECwp()
										.getCwpName(), destDomain.getId()));
			}
			if (bo.getWpaECwp() !=null) {
				bo.setWpaECwp(QueryUtil
						.findBoByAttribute(Cwp.class,
								"cwpName", bo.getWpaECwp()
										.getCwpName(), destDomain.getId()));
			}

			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the Tunnel Setting from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneTunnelSetting(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain)
			return;
		List<TunnelSetting> list = getAlltheObjectsInDomain(TunnelSetting.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (TunnelSetting tunnelSetting : list) {
			TunnelSetting bo = QueryUtil.findBoById(
					TunnelSetting.class, tunnelSetting.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			Set<IpAddress> ipAddressList = new HashSet<IpAddress>();
			for (IpAddress tempClass : bo.getIpAddressList()) {
				IpAddress newTempIpAddress = QueryUtil
						.findBoByAttribute(IpAddress.class, "addressName",
								tempClass.getAddressName(), destDomain.getId());
				ipAddressList.add(newTempIpAddress);
			}
			bo.setIpAddressList(ipAddressList);

			if (bo.getIpAddress() != null) {
				IpAddress tempIpAddress = QueryUtil
						.findBoByAttribute(IpAddress.class, "addressName", bo
								.getIpAddress().getAddressName(), destDomain
								.getId());
				bo.setIpAddress(tempIpAddress);
			}
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the WLAN Policy from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneConfigTemplate(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain)
			return;
		List<ConfigTemplate> list = getAlltheObjectsInDomain(ConfigTemplate.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (ConfigTemplate configTemplate : list) {
			ConfigTemplate bo = QueryUtil.findBoById(
					ConfigTemplate.class, configTemplate.getId(), this);

			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);

			if (bo.getHiveProfile() != null) {
				bo.setHiveProfile(QueryUtil.findBoByAttribute(
						HiveProfile.class, "hiveName", bo.getHiveProfile()
								.getHiveName(), destDomain.getId()));
			}
			if (bo.getMgmtServiceDns() != null) {
				bo.setMgmtServiceDns(QueryUtil
						.findBoByAttribute(MgmtServiceDns.class, "mgmtName", bo
								.getMgmtServiceDns().getMgmtName(), destDomain
								.getId(), this));
			}
			if (bo.getMgmtServiceSyslog() != null) {
				bo.setMgmtServiceSyslog(QueryUtil
						.findBoByAttribute(MgmtServiceSyslog.class, "mgmtName",
								bo.getMgmtServiceSyslog().getMgmtName(),
								destDomain.getId(), this));
			}
			if (bo.getMgmtServiceSnmp() != null) {
				bo.setMgmtServiceSnmp(QueryUtil
						.findBoByAttribute(MgmtServiceSnmp.class, "mgmtName",
								bo.getMgmtServiceSnmp().getMgmtName(),
								destDomain.getId(), this));
			}
			if (bo.getMgmtServiceTime() != null) {
				bo.setMgmtServiceTime(QueryUtil
						.findBoByAttribute(MgmtServiceTime.class, "mgmtName",
								bo.getMgmtServiceTime().getMgmtName(),
								destDomain.getId(), this));
			}
			if (bo.getMgmtServiceOption() != null) {
				bo.setMgmtServiceOption(QueryUtil
						.findBoByAttribute(MgmtServiceOption.class, "mgmtName",
								bo.getMgmtServiceOption().getMgmtName(),
								destDomain.getId()));
			}
			if (bo.getClientWatch() != null) {
				bo.setClientWatch(QueryUtil
						.findBoByAttribute(LocationClientWatch.class, "name",
								bo.getClientWatch().getName(), destDomain
										.getId()));
			}
			if (bo.getIdsPolicy() != null) {
				bo.setIdsPolicy(QueryUtil.findBoByAttribute(
						IdsPolicy.class, "policyName", bo.getIdsPolicy()
								.getPolicyName(), destDomain.getId()));
			}
			if (bo.getLldpCdp() != null) {
				bo.setLldpCdp(QueryUtil.findBoByAttribute(
						LLDPCDPProfile.class, "profileName", bo.getLldpCdp()
								.getProfileName(), destDomain.getId()));
			}
			
			if (bo.getRadiusProxyProfile()!=null) {
				bo.setRadiusProxyProfile(QueryUtil.findBoByAttribute(
						RadiusProxy.class, "proxyName", bo.getRadiusProxyProfile()
								.getProxyName(), destDomain.getId()));
			}
			
			if (bo.getRadiusServerProfile()!=null) {
				bo.setRadiusServerProfile(QueryUtil.findBoByAttribute(
						RadiusOnHiveap.class, "radiusName", bo.getRadiusServerProfile()
								.getRadiusName(), destDomain.getId()));
			}
			
			if (bo.getFwPolicy() != null) {
				bo.setFwPolicy(QueryUtil.findBoByAttribute(
						FirewallPolicy.class, "policyName", bo.getFwPolicy()
								.getPolicyName(), destDomain.getId()));
			}
			
			if(bo.getBonjourGw() != null){
				bo.setBonjourGw(QueryUtil.findBoByAttribute(
						BonjourGatewaySettings.class, "bonjourGwName", bo.getBonjourGw().getBonjourGwName(), 
						destDomain.getId()));
			}
			
			// if (bo.getRadiusAssignment() != null) {
			// bo.setRadiusAssignment((RadiusAssignment) QueryUtil
			// .findBoByAttribute(RadiusAssignment.class,
			// "radiusName", bo.getRadiusAssignment()
			// .getRadiusName(), destDomain.getId()));
			// }
			if (bo.getVlan() != null) {
				bo.setVlan(QueryUtil.findBoByAttribute(Vlan.class,
						"vlanName", bo.getVlan().getVlanName(), destDomain
								.getId()));
			}
			if (bo.getVlanNative() != null) {
				bo.setVlanNative(QueryUtil.findBoByAttribute(Vlan.class,
						"vlanName", bo.getVlanNative().getVlanName(),
						destDomain.getId()));
			}
//			if (bo.getMgtNetwork() != null) {
//				bo.setMgtNetwork(QueryUtil.findBoByAttribute(VpnNetwork.class,
//						"networkName", bo.getMgtNetwork().getNetworkName(),
//						destDomain.getId()));
//			}
			if (bo.getIpFilter() != null) {
				bo.setIpFilter(QueryUtil.findBoByAttribute(
						IpFilter.class, "filterName", bo.getIpFilter()
								.getFilterName(), destDomain.getId()));
			}
			if (bo.getAccessConsole() != null) {
				bo.setAccessConsole(QueryUtil
						.findBoByAttribute(AccessConsole.class, "consoleName",
								bo.getAccessConsole().getConsoleName(),
								destDomain.getId()));
			}
			if (bo.getEth0ServiceFilter() != null) {
				bo.setEth0ServiceFilter(QueryUtil
						.findBoByAttribute(ServiceFilter.class, "filterName",
								bo.getEth0ServiceFilter().getFilterName(),
								destDomain.getId()));
			}
			if (bo.getWireServiceFilter() != null) {
				bo.setWireServiceFilter(QueryUtil
						.findBoByAttribute(ServiceFilter.class, "filterName",
								bo.getWireServiceFilter().getFilterName(),
								destDomain.getId()));
			}
			if (bo.getEth0BackServiceFilter() != null) {
				bo.setEth0BackServiceFilter(QueryUtil
						.findBoByAttribute(ServiceFilter.class, "filterName",
								bo.getEth0BackServiceFilter().getFilterName(),
								destDomain.getId()));
			}
			if (bo.getEth1BackServiceFilter() != null) {
				bo.setEth1BackServiceFilter(QueryUtil
						.findBoByAttribute(ServiceFilter.class, "filterName",
								bo.getEth1BackServiceFilter().getFilterName(),
								destDomain.getId()));
			}
			if (bo.getRed0BackServiceFilter() != null) {
				bo.setRed0BackServiceFilter(QueryUtil
						.findBoByAttribute(ServiceFilter.class, "filterName",
								bo.getRed0BackServiceFilter().getFilterName(),
								destDomain.getId()));
			}
			if (bo.getAgg0BackServiceFilter() != null) {
				bo.setAgg0BackServiceFilter(QueryUtil
						.findBoByAttribute(ServiceFilter.class, "filterName",
								bo.getAgg0BackServiceFilter().getFilterName(),
								destDomain.getId()));
			}
			if (bo.getEth1ServiceFilter() != null) {
				bo.setEth1ServiceFilter(QueryUtil
						.findBoByAttribute(ServiceFilter.class, "filterName",
								bo.getEth1ServiceFilter().getFilterName(),
								destDomain.getId()));
			}
			if (bo.getRed0ServiceFilter() != null) {
				bo.setRed0ServiceFilter(QueryUtil
						.findBoByAttribute(ServiceFilter.class, "filterName",
								bo.getRed0ServiceFilter().getFilterName(),
								destDomain.getId()));
			}
			if (bo.getAgg0ServiceFilter() != null) {
				bo.setAgg0ServiceFilter(QueryUtil
						.findBoByAttribute(ServiceFilter.class, "filterName",
								bo.getAgg0ServiceFilter().getFilterName(),
								destDomain.getId()));
			}
			if (bo.getLocationServer() != null) {
				bo.setLocationServer(QueryUtil
						.findBoByAttribute(LocationServer.class, "name", bo
								.getLocationServer().getName(), destDomain
								.getId()));
			}
			if (bo.getAlgConfiguration() != null) {
				bo.setAlgConfiguration(QueryUtil
						.findBoByAttribute(AlgConfiguration.class,
								"configName", bo.getAlgConfiguration()
										.getConfigName(), destDomain.getId()));
			}
			if (bo.getClassifierMap() != null) {
				bo.setClassifierMap(QueryUtil
						.findBoByAttribute(QosClassification.class,
								"classificationName", bo.getClassifierMap()
										.getClassificationName(), destDomain
										.getId()));
			}
			if (bo.getMarkerMap() != null) {
				bo.setMarkerMap(QueryUtil.findBoByAttribute(
						QosMarking.class, "qosName", bo.getMarkerMap()
								.getQosName(), destDomain.getId()));
			}
//			if (bo.getEthernetAccess() != null) {
//				bo
//						.setEthernetAccess((EthernetAccess) QueryUtil
//								.findBoByAttribute(EthernetAccess.class,
//										"ethernetName", bo.getEthernetAccess()
//												.getEthernetName(), destDomain
//												.getId()));
//			}
//			if (bo.getEthernetBridge() != null) {
//				bo
//						.setEthernetBridge((EthernetAccess) QueryUtil
//								.findBoByAttribute(EthernetAccess.class,
//										"ethernetName", bo.getEthernetBridge()
//												.getEthernetName(), destDomain
//												.getId()));
//			}
//			if (bo.getEthernetAccessEth1() != null) {
//				bo
//						.setEthernetAccessEth1((EthernetAccess) QueryUtil
//								.findBoByAttribute(EthernetAccess.class,
//										"ethernetName", bo
//												.getEthernetAccessEth1()
//												.getEthernetName(), destDomain
//												.getId()));
//			}
//			if (bo.getEthernetBridgeEth1() != null) {
//				bo
//						.setEthernetBridgeEth1((EthernetAccess) QueryUtil
//								.findBoByAttribute(EthernetAccess.class,
//										"ethernetName", bo
//												.getEthernetBridgeEth1()
//												.getEthernetName(), destDomain
//												.getId()));
//			}
//			if (bo.getEthernetAccessRed() != null) {
//				bo
//						.setEthernetAccessRed((EthernetAccess) QueryUtil
//								.findBoByAttribute(EthernetAccess.class,
//										"ethernetName", bo
//												.getEthernetAccessRed()
//												.getEthernetName(), destDomain
//												.getId()));
//			}
//			if (bo.getEthernetBridgeRed() != null) {
//				bo
//						.setEthernetBridgeRed((EthernetAccess) QueryUtil
//								.findBoByAttribute(EthernetAccess.class,
//										"ethernetName", bo
//												.getEthernetBridgeRed()
//												.getEthernetName(), destDomain
//												.getId()));
//			}
//			if (bo.getEthernetAccessAgg() != null) {
//				bo
//						.setEthernetAccessAgg((EthernetAccess) QueryUtil
//								.findBoByAttribute(EthernetAccess.class,
//										"ethernetName", bo
//												.getEthernetAccessAgg()
//												.getEthernetName(), destDomain
//												.getId()));
//			}
//			if (bo.getEthernetBridgeAgg() != null) {
//				bo
//						.setEthernetBridgeAgg((EthernetAccess) QueryUtil
//								.findBoByAttribute(EthernetAccess.class,
//										"ethernetName", bo
//												.getEthernetBridgeAgg()
//												.getEthernetName(), destDomain
//												.getId()));
//			}
			if (bo.getVpnService() != null) {
				bo.setVpnService(QueryUtil.findBoByAttribute(
						VpnService.class, "profileName", bo.getVpnService()
								.getProfileName(), destDomain.getId()));
			}
			
			if (bo.getPrimaryIpTrack() != null) {
				bo.setPrimaryIpTrack(QueryUtil.findBoByAttribute(
						MgmtServiceIPTrack.class, "trackName", bo.getPrimaryIpTrack()
								.getTrackName(), destDomain.getId()));
			}
			
			if (bo.getBackup1IpTrack() != null) {
				bo.setBackup1IpTrack(QueryUtil.findBoByAttribute(
						MgmtServiceIPTrack.class, "trackName", bo.getBackup1IpTrack()
								.getTrackName(), destDomain.getId()));
			}
			
			if (bo.getBackup2IpTrack() != null) {
				bo.setBackup2IpTrack(QueryUtil.findBoByAttribute(
						MgmtServiceIPTrack.class, "trackName", bo.getBackup2IpTrack()
								.getTrackName(), destDomain.getId()));
			}
			
			if (bo.getRoutingProfilePolicy() != null) {
				bo.setRoutingProfilePolicy(QueryUtil.findBoByAttribute(
						RoutingProfilePolicy.class, "profileName", bo.getRoutingProfilePolicy().getProfileName()
								, destDomain.getId()));
			}
			
			if (bo.getRadiusAttrs() != null) {
				bo.setRadiusAttrs(QueryUtil.findBoByAttribute(
						RadiusAttrs.class, "objectName", bo.getRadiusAttrs().getObjectName()
								, destDomain.getId()));
			}
			if (bo.getAppProfile() != null) {
				bo.setAppProfile(QueryUtil.findBoByAttribute(
						ApplicationProfile.class, "profileName", bo.getAppProfile().getProfileName()
								, destDomain.getId()));
			}

			if (bo.getSwitchSettings() != null) {
				SwitchSettings switchSettings = new SwitchSettings();
				switchSettings = (SwitchSettings) bo.getSwitchSettings().clone();
				switchSettings.setOwner(destDomain);
				switchSettings.setVersion(null);
				if (bo.getSwitchSettings().getStpSettings() != null){
					StpSettings stpSettings = new StpSettings();
					stpSettings = (StpSettings) bo.getSwitchSettings().getStpSettings().clone();
					stpSettings.setOwner(destDomain);
					stpSettings.setVersion(null);
					if(bo.getSwitchSettings().getStpSettings().getMstpRegion() != null){
						MstpRegion mstp = QueryUtil.findBoByAttribute(MstpRegion.class, "regionName",
								switchSettings.getStpSettings().getMstpRegion()
								.getRegionName(), destDomain.getId());
						stpSettings.setMstpRegion(mstp);
					}
					switchSettings.setStpSettings(stpSettings);
				}
				bo.setSwitchSettings(switchSettings);
			}
			
			// Map<String, ConfigTemplateSsidUserProfile> cloneSsidUserProfiles
			// = new HashMap<String, ConfigTemplateSsidUserProfile>();
			// for (ConfigTemplateSsidUserProfile tempClass : bo
			// .getSsidUserProfiles().values()) {
			// if (tempClass.getSsidProfile() != null) {
			// tempClass.setSsidProfile((SsidProfile) QueryUtil
			// .findBoByAttribute(SsidProfile.class, "ssidName",
			// tempClass.getSsidProfile().getSsidName(),
			// destDomain.getId()));
			// }
			// if (tempClass.getUserProfile() != null) {
			// tempClass.setUserProfile((UserProfile) QueryUtil
			// .findBoByAttribute(UserProfile.class,
			// "userProfileName", tempClass
			// .getUserProfile()
			// .getUserProfileName(), destDomain
			// .getId()));
			// cloneSsidUserProfiles.put(tempClass.getKey(), tempClass);
			// }
			// }
			// bo.setSsidUserProfiles(cloneSsidUserProfiles);

			Map<Long, ConfigTemplateSsid> cloneSsidInterfaces = new HashMap<Long, ConfigTemplateSsid>();
			for (ConfigTemplateSsid tempClass : bo.getSsidInterfaces().values()) {
				if (tempClass.getSsidProfile() != null) {
					tempClass.setSsidProfile(QueryUtil
							.findBoByAttribute(SsidProfile.class, "ssidName",
									tempClass.getSsidProfile().getSsidName(),
									destDomain.getId()));
				}

				// if (tempClass.getRadiusAssignment() != null) {
				// tempClass.setRadiusAssignment((RadiusAssignment) QueryUtil
				// .findBoByAttribute(RadiusAssignment.class,
				// "radiusName", tempClass
				// .getRadiusAssignment()
				// .getRadiusName(), destDomain
				// .getId()));
				// }
				//
				// if (tempClass.getCwp() != null) {
				// tempClass.setCwp((Cwp) QueryUtil
				// .findBoByAttribute(Cwp.class,
				// "cwpName", tempClass.getCwp()
				// .getCwpName(), destDomain
				// .getId()));
				// }

				// if (tempClass.getServiceFilter() != null) {
				// tempClass.setServiceFilter((ServiceFilter) QueryUtil
				// .findBoByAttribute(ServiceFilter.class,
				// "filterName", tempClass.getServiceFilter()
				// .getFilterName(), destDomain
				// .getId()));
				// }

//				if (tempClass.getClassfierAndMarker() != null) {
//					tempClass
//							.setClassfierAndMarker((QosClassfierAndMarker) QueryUtil
//									.findBoByAttribute(
//											QosClassfierAndMarker.class,
//											"qosName", tempClass
//													.getClassfierAndMarker()
//													.getQosName(), destDomain
//													.getId()));
//				}
//
//				if (tempClass.getClassfierAndMarker() != null) {
//					tempClass
//							.setClassfierAndMarker((QosClassfierAndMarker) QueryUtil
//									.findBoByAttribute(
//											QosClassfierAndMarker.class,
//											"qosName", tempClass
//													.getClassfierAndMarker()
//													.getQosName(), destDomain
//													.getId()));
//				}

				// if (tempClass.getUserProfileRule() != null) {
				// tempClass
				// .setUserProfileRule((RadiusUserProfileRule) QueryUtil
				// .findBoByAttribute(
				// RadiusUserProfileRule.class,
				// "radiusUserProfileRuleName",
				// tempClass
				// .getUserProfileRule()
				// .getRadiusUserProfileRuleName(),
				// destDomain.getId()));
				// }

				// Set<ConfigTemplateUserProfileType> tmpUserProfiles = new
				// HashSet<ConfigTemplateUserProfileType>();
				// for (ConfigTemplateUserProfileType tmpUserClass : tempClass
				// .getUserProfiles()) {
				// if (tmpUserClass.getUserProfile() != null) {
				// tmpUserClass.setUserProfile((UserProfile) QueryUtil
				// .findBoByAttribute(UserProfile.class,
				// "userProfileName", tmpUserClass
				// .getUserProfile()
				// .getUserProfileName(),
				// destDomain.getId()));
				// }
				// tmpUserProfiles.add(tmpUserClass);
				// }
				// tempClass.setUserProfiles(tmpUserProfiles);

				if (tempClass.getSsidProfile() != null) {
					cloneSsidInterfaces.put(tempClass.getSsidProfile().getId(),
							tempClass);
					// Fiona add for dual port
				} else if ("eth0"
						.equalsIgnoreCase(tempClass.getInterfaceName())) {
					cloneSsidInterfaces.put((long) -1, tempClass);
				} else if ("eth1"
						.equalsIgnoreCase(tempClass.getInterfaceName())) {
					cloneSsidInterfaces.put((long) -2, tempClass);
				} else if ("eth2"
						.equalsIgnoreCase(tempClass.getInterfaceName())) {
					cloneSsidInterfaces.put((long) -5, tempClass);
				} else if ("eth3"
						.equalsIgnoreCase(tempClass.getInterfaceName())) {
					cloneSsidInterfaces.put((long) -6, tempClass);
				} else if ("eth4"
						.equalsIgnoreCase(tempClass.getInterfaceName())) {
					cloneSsidInterfaces.put((long) -7, tempClass);
				} else if ("red0"
						.equalsIgnoreCase(tempClass.getInterfaceName())) {
					cloneSsidInterfaces.put((long) -3, tempClass);
				} else {
					cloneSsidInterfaces.put((long) -4, tempClass);
				}
				// Fiona add end
			}
			bo.setSsidInterfaces(cloneSsidInterfaces);

			Set<MgmtServiceIPTrack> cloneIpTracks = new HashSet<MgmtServiceIPTrack>();
			for (MgmtServiceIPTrack tempClass : bo.getIpTracks()) {
				MgmtServiceIPTrack newTempClass = QueryUtil
						.findBoByAttribute(MgmtServiceIPTrack.class, "trackName",
								tempClass.getTrackName(), destDomain
										.getId());
				cloneIpTracks.add(newTempClass);
			}
			bo.setIpTracks(cloneIpTracks);
			
			Set<NetworkService> cloneTvNetworkServices = new HashSet<NetworkService>();
			for (NetworkService tempClass : bo.getTvNetworkService()) {
				NetworkService newTempClass = QueryUtil
						.findBoByAttribute(NetworkService.class, "serviceName",
								tempClass.getServiceName(), destDomain
										.getId());
				cloneTvNetworkServices.add(newTempClass);
			}
			bo.setTvNetworkService(cloneTvNetworkServices);
			
			Set<PortGroupProfile> clonePortProfiles = new HashSet<PortGroupProfile>();
			for (PortGroupProfile tempClass : bo.getPortProfiles()) {
				PortGroupProfile newTempClass = QueryUtil
						.findBoByAttribute(PortGroupProfile.class, "name",
								tempClass.getName(), destDomain
										.getId());
				clonePortProfiles.add(newTempClass);
			}
			bo.setPortProfiles(clonePortProfiles);
			
			List<ConfigTemplateStormControl> stormControlList = new ArrayList<ConfigTemplateStormControl>();
			for (ConfigTemplateStormControl tempClass : bo.getStormControlList()) {
				stormControlList.add(tempClass);
			}
			bo.setStormControlList(stormControlList);
			
			List<ConfigTemplateVlanNetwork> vlanNetwork = new ArrayList<ConfigTemplateVlanNetwork>();
			for (ConfigTemplateVlanNetwork tempClass : bo.getVlanNetwork()) {
				ConfigTemplateVlanNetwork newPro = new ConfigTemplateVlanNetwork();
				Vlan newTempClass = QueryUtil
						.findBoByAttribute(Vlan.class, "vlanName",
								tempClass.getVlan().getVlanName(), destDomain
										.getId());
				newPro.setVlan(newTempClass);
				if (tempClass.getNetworkObj()!=null) {
					VpnNetwork newVpnClass = QueryUtil
						.findBoByAttribute(VpnNetwork.class, "networkName",
								tempClass.getNetworkObj().getNetworkName(), destDomain
										.getId());
					
					newPro.setNetworkObj(newVpnClass);
				}
				newPro.setBlnUserAdd(tempClass.isBlnUserAdd());
				vlanNetwork.add(newPro);
			}
			bo.setVlanNetwork(vlanNetwork);
			
			Set<UserProfileVlanMapping> upVlanMappings = new HashSet<UserProfileVlanMapping>();
			for (UserProfileVlanMapping tempClass : bo.getUpVlanMapping()) {
				UserProfileVlanMapping newPro = new UserProfileVlanMapping();
				Vlan newTempClass = QueryUtil
						.findBoByAttribute(Vlan.class, "vlanName",
								tempClass.getVlan().getVlanName(), destDomain
										.getId());
				UserProfile userProfile = QueryUtil
						.findBoByAttribute(UserProfile.class, "userProfileName",
								tempClass.getUserProfile().getUserProfileName(), destDomain
										.getId());
				newPro.setVlan(newTempClass);
				newPro.setUserProfile(userProfile);
				newPro.setOwner(destDomain);
				newPro.setNetworkPolicy(bo);
				upVlanMappings.add(newPro);
			}
			bo.setUpVlanMapping(upVlanMappings);

			newBos.add(bo);
		}

		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone single table information.
	 * 
	 * @param arg_Class -
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public <T extends HmBo> void cloneSingleTableInfo(Class<T> arg_Class,
			HmDomain srcDomain, HmDomain destDomain) throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<T> list = getAlltheObjectsInDomain(arg_Class, srcDomain);
		List<T> newBos = new ArrayList<T>();
		for (T bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			newBos.add(bo);
		}
		
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	public void cloneStartConfig(HmDomain srcDomain, HmDomain destDomain) {
		try {
			if (null == srcDomain || null == destDomain) {
				return;
			}
			HmStartConfig srcBo = QueryUtil.findBoByAttribute(HmStartConfig.class, "owner", srcDomain);
			HmStartConfig destBo = QueryUtil.findBoByAttribute(HmStartConfig.class, "owner", destDomain);
			
			if (srcBo != null && destBo != null) {
				destBo.setAdminUserLogin(srcBo.isAdminUserLogin());
				destBo.setHiveApPassword(srcBo.getHiveApPassword());
				destBo.setLedBrightness(srcBo.getLedBrightness());
				destBo.setModeType(srcBo.getModeType());
				destBo.setNetworkName(srcBo.getNetworkName());
				destBo.setUseAccessConsole(srcBo.isUseAccessConsole());
				
				QueryUtil.updateBo(destBo);
			} else {
				log.error("cloneStartConfig", "DB data not expected, some vhm has no start config bo.");
			}
			
		} catch (Exception e) {
			log.error("cloneStartConfig", "catch exception.", e);
		}
	}
	
	public void cloneMailNotify(HmDomain srcDomain, HmDomain destDomain) {
		try {
			if (null == srcDomain || null == destDomain) {
				return;
			}
			MailNotification srcBo = QueryUtil.findBoByAttribute(MailNotification.class, "owner", srcDomain);
			MailNotification destBo = QueryUtil.findBoByAttribute(MailNotification.class, "owner", destDomain);
			
			if (srcBo != null && destBo != null) {
				destBo.setAuth(srcBo.isAuth());
				destBo.setCapWap(srcBo.getCapWap());
				destBo.setSecurity(srcBo.getSecurity());
				destBo.setConfig(srcBo.getConfig());
				destBo.setHdCpu(srcBo.isHdCpu());
				destBo.setHdMemory(srcBo.isHdMemory());
				destBo.setHdRadio(srcBo.getHdRadio());
				destBo.setInterfaceValue(srcBo.isInterfaceValue());
				destBo.setClientMonitor(srcBo.isClientMonitor());
				destBo.setL2Dos(srcBo.isL2Dos());
				destBo.setMailFrom(srcBo.getMailFrom());
				destBo.setMailTo(srcBo.getMailTo());
				destBo.setScreen(srcBo.isScreen());
				destBo.setTimeBomb(srcBo.getTimeBomb());
				destBo.setSendMailFlag(srcBo.getSendMailFlag());
				destBo.setServerName(srcBo.getServerName());
				destBo.setSupportPwdAuth(srcBo.isSupportPwdAuth());
				destBo.setSupportSSL(srcBo.isSupportSSL());
				destBo.setEmailUserName(srcBo.getEmailUserName());
				destBo.setEmailPassword(srcBo.getEmailPassword());
				destBo.setSupportTLS(srcBo.isSupportTLS());
				destBo.setPort(srcBo.getPort());
				destBo.setClient(srcBo.getClient());
				destBo.setTca(srcBo.getTca());
				destBo.setSystem(srcBo.getSystem());
				
				QueryUtil.updateBo(destBo);
				
				HmBeAdminUtil.updateMailNotification(destBo);
			} else {
				log.error("cloneMailNotify", "DB data not expected, some vhm has no email notify settings bo.");
			}			
		} catch (Exception e) {
			log.error("cloneMailNotify", "catch exception.", e);
		}
	}
	
	public void cloneHMSerivceSettings(HmDomain srcDomain, HmDomain destDomain) {
		try {
			if (null == srcDomain || null == destDomain) {
				return;
			}
			HMServicesSettings srcBo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", srcDomain, this);
			HMServicesSettings destBo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", destDomain);
			
			if (srcBo != null && destBo != null) {
				destBo.setEnableClientRefresh(srcBo.isEnableClientRefresh());
				destBo.setInfiniteSession(srcBo.isInfiniteSession());
				destBo.setRefreshFilterName(srcBo.getRefreshFilterName());
				destBo.setRefreshInterval(srcBo.getRefreshInterval());
				destBo.setSessionExpiration(srcBo.getSessionExpiration());
				destBo.setSnmpCommunity(srcBo.getSnmpCommunity());
				destBo.setSnmpReceiverIP(srcBo.getSnmpReceiverIP());
				destBo.setShowNotifyInfo(srcBo.isShowNotifyInfo());
				destBo.setNotifyInformation(srcBo.getNotifyInformation());
				destBo.setEnableTeacher(srcBo.isEnableTeacher());
				destBo.setAuthorizationKey(srcBo.getAuthorizationKey());
				destBo.setServiceHost(srcBo.getServiceHost());
				destBo.setWindowsDomain(srcBo.getWindowsDomain());
				destBo.setServicePort(srcBo.getServicePort());
				destBo.setBarracudaDefaultUserName(srcBo.getBarracudaDefaultUserName());
				destBo.setBarracudaWhitelist(srcBo.getBarracudaWhitelist());
				destBo.setEnableBarracuda(srcBo.isEnableBarracuda());
				destBo.setAccountID(srcBo.getAccountID());
				destBo.setWebSenseDefaultUserName(srcBo.getWebSenseDefaultUserName());
				destBo.setSecurityKey(srcBo.getSecurityKey());
				destBo.setWebSenseServiceHost(srcBo.getWebSenseServiceHost());
				destBo.setPort(srcBo.getPort());
				destBo.setWensenseMode(srcBo.getWensenseMode());
				destBo.setDefaultDomain(srcBo.getDefaultDomain());
				destBo.setWebsenseWhitelist(srcBo.getWebsenseWhitelist());
				destBo.setEnableWebsense(srcBo.isEnableWebsense());
				destBo.setClassifierTag(srcBo.getClassifierTag());
				
                destBo.setEnabledBetaIDM(srcBo.isEnabledBetaIDM());
                destBo.setTimeType(srcBo.getTimeType());
                destBo.setDateFormat(srcBo.getDateFormat());
                destBo.setDateSeparator(srcBo.getDateSeparator());
                destBo.setTimeFormat(srcBo.getTimeFormat());
				
                destBo.setEnableRadarDetection(srcBo.isEnableRadarDetection());
                destBo.setEnableOpenDNS(srcBo.isEnableOpenDNS());
                
    			if (srcBo.getOpenDNSAccount() != null) {
    				destBo.setOpenDNSAccount(QueryUtil
    						.findBoByAttribute(OpenDNSAccount.class,
    								"userName", srcBo.getOpenDNSAccount().getUserName(),
    								destDomain.getId()));
    			}
                
				QueryUtil.updateBo(destBo);
			} else {
				log.error("cloneMailNotify", "DB data not expected, some vhm has no email notify settings bo.");
			}
		} catch (Exception e) {
			log.error("cloneMailNotify", "catch exception.", e);
		}
	}

	public void cloneCwpCertficate(HmDomain srcDomain, HmDomain destDomain) {
		try {
			if (null == srcDomain || null == destDomain) {
				return;
			}
			List<CwpCertificate> list = getAlltheObjectsInDomain(CwpCertificate.class,
					srcDomain);
			List<HmBo> newBos = new ArrayList<HmBo>();
			for (CwpCertificate bo : list) {
				if (bo.getDefaultFlag()) {
					continue;
				}
				bo.setId(null);
				bo.setOwner(destDomain);
				bo.setVersion(null);
				newBos.add(bo);
			}
			if (!newBos.isEmpty()) {
				QueryUtil.bulkCreateBos(newBos);
			}
		} catch (Exception e) {
			log.error("cloneCwpCertficate", "catch exception.", e);
		}
	}

	public void cloneAutoProvisioning(HmDomain srcDomain, HmDomain destDomain) {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		try {
			String src = srcDomain.getDomainName();
			String des = destDomain.getDomainName();
			List<HiveApAutoProvision> list = getAlltheObjectsInDomain(HiveApAutoProvision.class,
					srcDomain, this);
			List<HmBo> newBos = new ArrayList<HmBo>();
			for (HiveApAutoProvision bo : list) {
				bo.setId(null);
				bo.setOwner(destDomain);
				bo.setVersion(null);

				if (null != bo.getMacAddresses()
						&& !bo.getMacAddresses().isEmpty()) {
					bo.setMacAddresses(null);
					bo.setAutoProvision(false);
					bo.setAccessControled(false);
					String msg = "The autoProvision from '"
							+ src
							+ "' is access controled for HiveAP, reset access controled to false for destination domain '"
							+ des
							+ "', also set the auto provision flag to false.";
					log.warning("cloneAutoProvisioning", msg);
				}
				Long templateId = bo.getConfigTemplateId();
				Long containerId = bo.getMapContainerId();
				Long radio0Id = bo.getWifi0ProfileId();
				Long radio1Id = bo.getWifi1ProfileId();
				//Long lldpcdpId = bo.getLldpProfileId();
				Long capwapId = bo.getCfgCapwapIpId();

				ConfigTemplate newTemplate = null;
				MapContainerNode newContainer = null;
				//LLDPCDPProfile newLldpcdp = null;
				IpAddress newCapwap = null;
				String configTemplateName = null;

				if (null != templateId && templateId > 0) {
					ConfigTemplate oldTemplate = QueryUtil
							.findBoById(ConfigTemplate.class, templateId);
					if (null != oldTemplate) {
						configTemplateName = oldTemplate.getConfigName();
						newTemplate = QueryUtil
								.findBoByAttribute(ConfigTemplate.class,
										"configName", oldTemplate
												.getConfigName(), destDomain
												.getId());
					}
				}

				if (newTemplate == null) {
					if (configTemplateName == null) {
						log
								.warn(
										"cloneAutoProvisioning",
										"Could not find the corresponding config template in the new domain, ignoring clone for auto provision.");
					} else {
						log
								.warn(
										"cloneAutoProvisioning",
										"Could not find the corresponding config template named '" + configTemplateName + "' in the new domain, ignoring clone for auto provision.");
					}

					return;
				}

				if (null != containerId && containerId > 0) {
					MapContainerNode oldContainer = QueryUtil
							.findBoById(MapContainerNode.class, containerId);
					if (null != oldContainer) {
						newContainer = QueryUtil
								.findBoByAttribute(MapContainerNode.class,
										"mapName", oldContainer.getMapName(),
										destDomain.getId());
					}
				}
//				if (null != lldpcdpId && lldpcdpId > 0) {
//					LLDPCDPProfile oldLldpcdp = (LLDPCDPProfile) QueryUtil
//							.findBoById(LLDPCDPProfile.class, lldpcdpId);
//					if (null != oldLldpcdp) {
//						newLldpcdp = (LLDPCDPProfile) QueryUtil
//								.findBoByAttribute(LLDPCDPProfile.class,
//										"profileName", oldLldpcdp
//												.getProfileName(), destDomain
//												.getId());
//					}
//				}
				if (null != capwapId && capwapId > 0) {
					IpAddress oldCapwapIp = QueryUtil.findBoById(
							IpAddress.class, capwapId);
					if (null != oldCapwapIp) {
						newCapwap = QueryUtil.findBoByAttribute(
								IpAddress.class, "addressName", oldCapwapIp
										.getAddressName(), destDomain.getId());
					}
				}
				RadioProfile newWifi0Profile = getNewRadioProfile(radio0Id,
						destDomain);
				RadioProfile newWifi1Profile = getNewRadioProfile(radio1Id,
						destDomain);
//				if (null == newTemplate || null == newWifi0Profile
//						|| null == newWifi1Profile) {
//					log
//							.error(
//									"cloneAutoProvisioning",
//									"config template or radio profiles are not existed in the new domain. do not clone for auto provision.");
//					return;
//				}

				// The AP100 has only 1 available radio, but other kinds of APs have 2 available radios.
				// Now, use device properties to define which radio profile is proper for the device.
				if (newWifi0Profile == null && HiveAp.isWifi0Available(bo.getModelType())) {
					log
							.warn(
									"cloneAutoProvisioning",
									"The WIFI0 radio profile was not found in the new domain, ignoring clone for auto provision.");
					return;
				} else if (newWifi1Profile == null && HiveAp.isWifi1Available(bo.getModelType())) {
					log
							.warn(
									"cloneAutoProvisioning",
									"The WIFI1 radio profile was not found in the new domain, ignoring clone for auto provision.");
					return;
				}
				if (newWifi0Profile != null) {
					bo.setWifi0ProfileId(newWifi0Profile.getId());
				}

				if (newWifi1Profile != null) {
					bo.setWifi1ProfileId(newWifi1Profile.getId());
				}

				bo.setConfigTemplateId(newTemplate.getId());
				bo.setMapContainerId(newContainer == null ? null : newContainer
						.getId());
//				bo.setLldpProfileId(newLldpcdp == null ? null : newLldpcdp
//						.getId());
				bo.setCfgCapwapIpId(newCapwap == null ? null : newCapwap
						.getId());
				newBos.add(bo);
			}
			if (!newBos.isEmpty()) {
				QueryUtil.bulkCreateBos(newBos);
			}
		} catch (Exception e) {
			log.error("cloneAutoProvisioning",
					"clone autoprovisioning for # domain:" + destDomain.getId()
							+ " error.", e);
		}
	}

	private RadioProfile getNewRadioProfile(Long oldProfileId,
			HmDomain newDomain) {
		if (null != oldProfileId && oldProfileId > 0) {
			RadioProfile oldProfile = QueryUtil.findBoById(
					RadioProfile.class, oldProfileId);
			if (null != oldProfile) {
				return QueryUtil.findBoByAttribute(
						RadioProfile.class, "radioName", oldProfile
								.getRadioName(), newDomain.getId());
			}
		}
		return null;
	}

	public void cloneHiveApUpdateSetting(HmDomain srcDomain, HmDomain destDomain) {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		try {
			List<HiveApUpdateSettings> list = getAlltheObjectsInDomain(HiveApUpdateSettings.class,
					srcDomain);
			if (!list.isEmpty()) {
				HiveApUpdateSettings setting = list
						.get(0);
				setting.setId(null);
				setting.setOwner(destDomain);
				setting.setVersion(null);
				QueryUtil.createBo(setting);
			}
		} catch (Exception e) {

		}
	}

	public void cloneMapContainer(HmDomain srcDomain, HmDomain destDomain) {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		Long srcWorldMapId = BoMgmt.getMapMgmt().getWorldMapId(
				srcDomain.getId());
		Long destWorldMapId = BoMgmt.getMapMgmt().getWorldMapId(
				destDomain.getId());
		if (null == srcWorldMapId || null == destWorldMapId) {
			return;
		}
		MapContainerNode srcWorldMap = QueryUtil.findBoById(
				MapContainerNode.class, srcWorldMapId, this);
		MapContainerNode destWorldMap = QueryUtil
				.findBoById(MapContainerNode.class, destWorldMapId, this);
		if (null == srcWorldMap || null == destWorldMap) {
			return;
		}
		try {
			destWorldMap.setMapName(srcWorldMap.getMapName());
			destWorldMap.setActualHeight(srcWorldMap.getActualHeight());
			destWorldMap.setActualWidth(srcWorldMap.getActualWidth());
			destWorldMap.setBackground(srcWorldMap.getBackground());
			destWorldMap.setEnvironment(srcWorldMap.getEnvironment());
			destWorldMap.setHeight(srcWorldMap.getHeight());
			destWorldMap.setIconName(srcWorldMap.getIconName());
			// destWorldMap.setSeverity(srcWorldMap.getSeverity());
			destWorldMap.setWidth(srcWorldMap.getWidth());
			destWorldMap.setX(srcWorldMap.getX());
			destWorldMap.setY(srcWorldMap.getY());
			QueryUtil.updateBo(destWorldMap);
		} catch (Exception e) {
			log.error("cloneMapContainer", "update world map container error.",
					e);
		}

		createMapHierarchy(srcWorldMap, destDomain);
	}

	/**
	 * clone all DNSs of management service from srcDomain to destDomain.
	 *
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 * @author Joseph Chen
	 * @since 06/10/2008
	 */
	public void cloneMgmtServiceDNS(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || destDomain == null) {
			return;
		}

		List<MgmtServiceDns> list = getAlltheObjectsInDomain(MgmtServiceDns.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();

		for (MgmtServiceDns dns : list) {
			MgmtServiceDns bo = QueryUtil.findBoById(MgmtServiceDns.class,
					dns.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			IpAddress newIp;
			List<MgmtServiceDnsInfo> dnsInfo = new ArrayList<MgmtServiceDnsInfo>();
			for (MgmtServiceDnsInfo info : bo.getDnsInfo()) {
				if (null != info.getIpAddress()) {
					newIp = QueryUtil.findBoByAttribute(
							IpAddress.class, "addressName", info.getIpAddress()
									.getAddressName(), destDomain.getId());

					info.setIpAddress(newIp);
				}
				dnsInfo.add(info);
			}
			bo.setDnsInfo(dnsInfo);
			newBos.add(bo);
		}

		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * clone all SNMPs of management service from srcDomain to destDomain.
	 *
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 * @author Joseph Chen
	 * @since 06/10/2008
	 */
	public void cloneMgmtServiceSNMP(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || destDomain == null) {
			return;
		}

		List<MgmtServiceSnmp> list = getAlltheObjectsInDomain(MgmtServiceSnmp.class,
				srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();

		for (MgmtServiceSnmp snmp : list) {
			MgmtServiceSnmp bo = QueryUtil.findBoById(MgmtServiceSnmp.class,
					snmp.getId(), this);
			
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			IpAddress newIp;
			List<MgmtServiceSnmpInfo> snmpInfo = new ArrayList<MgmtServiceSnmpInfo>();
			for (MgmtServiceSnmpInfo info : bo.getSnmpInfo()) {
				if (null != info.getIpAddress()) {
					newIp = QueryUtil.findBoByAttribute(
							IpAddress.class, "addressName", info.getIpAddress()
									.getAddressName(), destDomain.getId());

					info.setIpAddress(newIp);
				}
				snmpInfo.add(info);
			}
			bo.setSnmpInfo(snmpInfo);
			newBos.add(bo);
		}

		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * clone all syslogs of management service from srcDomain to destDomain.
	 *
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 * @author Joseph Chen
	 * @since 06/10/2008
	 */
	public void cloneMgmtServiceSyslog(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || destDomain == null) {
			return;
		}

		List<MgmtServiceSyslog> list = getAlltheObjectsInDomain(MgmtServiceSyslog.class,
				srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();

		for (MgmtServiceSyslog syslog : list) {
			MgmtServiceSyslog bo = QueryUtil.findBoById(MgmtServiceSyslog.class,
					syslog.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			IpAddress newIp;
			List<MgmtServiceSyslogInfo> syslogInfo = new ArrayList<MgmtServiceSyslogInfo>();
			for (MgmtServiceSyslogInfo info : bo.getSyslogInfo()) {
				if (null != info.getIpAddress()) {
					newIp = QueryUtil.findBoByAttribute(
							IpAddress.class, "addressName", info.getIpAddress()
									.getAddressName(), destDomain.getId());

					info.setIpAddress(newIp);
				}
				syslogInfo.add(info);
			}
			bo.setSyslogInfo(syslogInfo);
			newBos.add(bo);
		}

		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * clone all times of management service from srcDomain to destDomain.
	 *
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 * @author Joseph Chen
	 * @since 06/10/2008
	 */
	public void cloneMgmtServiceTime(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || destDomain == null) {
			return;
		}

		List<MgmtServiceTime> list = getAlltheObjectsInDomain(MgmtServiceTime.class,
				srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();

		for (MgmtServiceTime time : list) {
			MgmtServiceTime bo = QueryUtil.findBoById(MgmtServiceTime.class,
					time.getId(), this);
			
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			IpAddress newIp;
			List<MgmtServiceTimeInfo> timeInfo = new ArrayList<MgmtServiceTimeInfo>();
			for (MgmtServiceTimeInfo info : bo.getTimeInfo()) {
				if (null != info.getIpAddress()) {
					newIp = QueryUtil.findBoByAttribute(
							IpAddress.class, "addressName", info.getIpAddress()
									.getAddressName(), destDomain.getId());

					info.setIpAddress(newIp);
				}
				timeInfo.add(info);
			}
			bo.setTimeInfo(timeInfo);
			newBos.add(bo);
		}

		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * clone all options of management service from srcDomain to destDomain.
	 *
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneMgmtServiceOption(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || destDomain == null) {
			return;
		}

		List<MgmtServiceOption> list = getAlltheObjectsInDomain(MgmtServiceOption.class,
				srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		RadiusAssignment radius;

		for (MgmtServiceOption bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			if (null != bo.getRadiusServer()) {
				radius = QueryUtil.findBoByAttribute(
						RadiusAssignment.class, "radiusName", bo
								.getRadiusServer().getRadiusName(), destDomain
								.getId());
				bo.setRadiusServer(radius);
			}
			newBos.add(bo);
		}

		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * clone all QoS classifications from srcDomain to destDomain.
	 *
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 * @author Joseph Chen
	 * @since 06/10/2008
	 */
	public void cloneQosClassification(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || destDomain == null) {
			return;
		}

		List<QosClassification> list = getAlltheObjectsInDomain(QosClassification.class,
				srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();

		for (QosClassification bo : list) {
			bo = QueryUtil.findBoById(
					QosClassification.class, bo.getId(),
					this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			NetworkService newNetworkService;
			MacOrOui newMacOui;
			SsidProfile newSsid;
			CustomApplication customApp;

			// network service
			if (bo.getNetworkServicesEnabled()) {
				Map<Long, QosNetworkService> map_service = new HashMap<Long, QosNetworkService>();

				for (QosNetworkService info : bo.getNetworkServices().values()) {
					newNetworkService = QueryUtil
							.findBoByAttribute(NetworkService.class,
									"serviceName", info.getNetworkService()
											.getServiceName(), destDomain
											.getId());
					info.setNetworkService(newNetworkService);

					map_service.put(newNetworkService.getId(), info);
				}

				bo.setNetworkServices(map_service);
				Map<Long, QosCustomService> map_customservice = new HashMap<Long, QosCustomService>();

				for (QosCustomService info : bo.getCustomServices().values()) {
					customApp = QueryUtil
							.findBoByAttribute(CustomApplication.class,
									"customAppName", info.getCustomAppService().getCustomAppName(),
											destDomain.getId());
					info.setCustomAppService(customApp);

					map_customservice.put(customApp.getId(), info);
				}

				bo.setCustomServices(map_customservice);
			}

			// qos mac oui
			if (bo.getMacOuisEnabled()) {
				Map<Long, QosMacOui> map_macoui = new HashMap<Long, QosMacOui>();

				for (QosMacOui info : bo.getQosMacOuis().values()) {
					newMacOui = QueryUtil.findBoByAttribute(
							MacOrOui.class, "macOrOuiName", info.getMacOui()
									.getMacOrOuiName(), destDomain.getId());
					info.setMacOui(newMacOui);
					map_macoui.put(newMacOui.getId(), info);
				}

				bo.setQosMacOuis(map_macoui);
			}

			// qos ssid
			if (bo.getSsidEnabled()) {
				Map<Long, QosSsid> map_ssid = new HashMap<Long, QosSsid>();

				for (QosSsid info : bo.getQosSsids().values()) {
					newSsid = QueryUtil.findBoByAttribute(
							SsidProfile.class, "ssidName", info.getSsid()
									.getSsidName(), destDomain.getId());
					info.setSsid(newSsid);
					map_ssid.put(newSsid.getId(), info);
				}

				bo.setQosSsids(map_ssid);
			}

			newBos.add(bo);
		}

		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * clone all user profiles of management service from srcDomain to
	 * destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 * @author Joseph Chen
	 * @since 06/10/2008
	 */
	public void cloneUserProfile(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || destDomain == null) {
			return;
		}

		List<UserProfile> list = getAlltheObjectsInDomain(UserProfile.class, srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		List<Long> lOldId = new ArrayList<Long>();
		Set<Long> userProOldIds = new HashSet<Long>();

		for (UserProfile bo : list) {
			lOldId.add(bo.getId());
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);

			if (null != bo.getUserProfileAttribute()) {
				UserProfileAttribute newAttribute = QueryUtil
						.findBoByAttribute(UserProfileAttribute.class,
								"attributeName", bo.getUserProfileAttribute()
										.getAttributeName(), destDomain.getId());

				bo.setUserProfileAttribute(newAttribute);
			}

			if (null != bo.getVlan()) {
				Vlan newVlan = QueryUtil.findBoByAttribute(Vlan.class,
						"vlanName", bo.getVlan().getVlanName(), destDomain
								.getId());
				bo.setVlan(newVlan);
			}

			if (null != bo.getQosRateControl()) {
				QosRateControl newRateControl = QueryUtil
						.findBoByAttribute(QosRateControl.class, "qosName", bo
								.getQosRateControl().getQosName(), destDomain
								.getId(), this);

				bo.setQosRateControl(newRateControl);
			}

			if (null != bo.getTunnelSetting()) {
				TunnelSetting newTunnel = QueryUtil
						.findBoByAttribute(TunnelSetting.class, "tunnelName",
								bo.getTunnelSetting().getTunnelName(),
								destDomain.getId());

				bo.setTunnelSetting(newTunnel);
			}

			if (null != bo.getAsRuleGroup()) {
				AirScreenRuleGroup newRuleGroup = QueryUtil
						.findBoByAttribute(AirScreenRuleGroup.class,
								"profileName", bo.getAsRuleGroup()
										.getProfileName(), destDomain.getId());

				bo.setAsRuleGroup(newRuleGroup);
			}

			if (null != bo.getIpPolicyFrom()) {
				IpPolicy newPolicy = QueryUtil.findBoByAttribute(
						IpPolicy.class, "policyName", bo.getIpPolicyFrom()
								.getPolicyName(), destDomain.getId());

				bo.setIpPolicyFrom(newPolicy);
			}

			if (null != bo.getIpPolicyTo()) {
				IpPolicy newPolicy = QueryUtil.findBoByAttribute(
						IpPolicy.class, "policyName", bo.getIpPolicyTo()
								.getPolicyName(), destDomain.getId());

				bo.setIpPolicyTo(newPolicy);
			}

			if (null != bo.getMacPolicyFrom()) {
				MacPolicy newPolicy = QueryUtil.findBoByAttribute(
						MacPolicy.class, "policyName", bo.getMacPolicyFrom()
								.getPolicyName(), destDomain.getId());

				bo.setMacPolicyFrom(newPolicy);
			}

			if (null != bo.getMacPolicyTo()) {
				MacPolicy newPolicy = QueryUtil.findBoByAttribute(
						MacPolicy.class, "policyName", bo.getMacPolicyTo()
								.getPolicyName(), destDomain.getId());

				bo.setMacPolicyTo(newPolicy);
			}

			if (null != bo.getUserProfileSchedulers()) {
				Scheduler newScheduler;
				Set<Scheduler> newSchedulers = new HashSet<Scheduler>();

				for (Scheduler scheduler : bo.getUserProfileSchedulers()) {
					newScheduler = QueryUtil.findBoByAttribute(
							Scheduler.class, "schedulerName", scheduler
									.getSchedulerName(), destDomain.getId());
					newSchedulers.add(newScheduler);
				}

				bo.setUserProfileSchedulers(newSchedulers);
			}
			
			if (null != bo.getMarkerMap()) {
				QosMarking newPolicy = QueryUtil.findBoByAttribute(
						QosMarking.class, "qosName", bo.getMarkerMap()
								.getQosName(), destDomain.getId());

				bo.setMarkerMap(newPolicy);
			}
			
			if (null != bo.getAssignRules()) {
				List<DevicePolicyRule> rules = new ArrayList<DevicePolicyRule>();

				for (DevicePolicyRule rule : bo.getAssignRules()) {
					if (rule == null) {
						continue;
					}
					if (null != rule.getMacObj()) {
						rule.setMacObj(QueryUtil.findBoByAttribute(MacOrOui.class, "macOrOuiName", 
							rule.getMacObj().getMacOrOuiName(), destDomain.getId()));
					}
					if (null != rule.getOsObj()) {
						rule.setOsObj(QueryUtil.findBoByAttribute(OsObject.class, "osName", 
							rule.getOsObj().getOsName(), destDomain.getId()));
					}
					if (null != rule.getDomObj()) {
						rule.setDomObj(QueryUtil.findBoByAttribute(DomainObject.class, "objName", 
							rule.getDomObj().getObjName(), destDomain.getId()));
					}
					userProOldIds.add(rule.getUserProfileId());
					rules.add(rule);
				}

				bo.setAssignRules(rules);
			}
//			if (null != bo.getNetworkObj()) {
//				VpnNetwork serverNet = QueryUtil
//						.findBoByAttribute(VpnNetwork.class, "networkName",
//							bo.getNetworkObj().getNetworkName(),
//								destDomain.getId());
//				bo.setNetworkObj(serverNet);
//			}

			newBos.add(bo);
		}

		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
			
			for(int i=0; i < newBos.size(); ++i)
			{
				AhRestoreNewMapTools.setMapUserProfile(lOldId.get(i), newBos.get(i).getId());
			}
			
			// get those user profile belong to domain "global"
			List<?> globalIdLst = QueryUtil.executeQuery("select id from "+ UserProfile.class.getSimpleName(), null, new FilterParams(
				"owner.domainName", HmDomain.GLOBAL_DOMAIN));
			List<Long> globalUserProfileIds = new ArrayList<Long>();

			for (Object obj: globalIdLst) {
				globalUserProfileIds.add((Long)obj);
			}
			
			if (!userProOldIds.isEmpty()) {
				for (Long oldId : userProOldIds) {
					Long newId = AhRestoreNewMapTools.getMapUserProfile(oldId);
					List<UserProfile> userProfiles = (List<UserProfile>) QueryUtil.executeQuery("select distinct bo from " + UserProfile.class.getSimpleName() + " as bo join bo.assignRules as joined", null, new FilterParams("joined.userProfileId", oldId), null, this);

					if (!userProfiles.isEmpty()) {
						if (null == newId) {
							for (UserProfile userProfile : userProfiles) {
								// if the user profile is source profile, do not need to deal with userProfileId of devicePolicyRule.
								if (AhRestoreNewMapTools.getMapUserProfile(userProfile.getId()) != null) {
									continue;
								}
								for (Iterator<DevicePolicyRule> devicePolicyRuleIter = userProfile.getAssignRules().iterator(); devicePolicyRuleIter.hasNext();) {
									DevicePolicyRule devicePolicyRule = devicePolicyRuleIter.next();
									Long userProfileId = devicePolicyRule.getUserProfileId();

									if (oldId.equals(userProfileId)) {
										// if the user profile set as re-assign user profile is global, do not change it.
										if (!globalUserProfileIds.contains(oldId)) {
											devicePolicyRuleIter.remove();
										} else {
											devicePolicyRule.setUserProfileId(oldId);
										}
									}
								}
							}
						} else {
							for (UserProfile userProfile : userProfiles) {
								// if the user profile is source profile, do not need to deal with userProfileId of devicePolicyRule.
								if (AhRestoreNewMapTools.getMapUserProfile(userProfile.getId()) != null) {
									continue;
								}
								for (DevicePolicyRule devicePolicyRule : userProfile.getAssignRules()) {
									Long userProfileId = devicePolicyRule.getUserProfileId();

									if (oldId.equals(userProfileId)) {
										devicePolicyRule.setUserProfileId(newId);
									}
								}
							}
						}

						QueryUtil.bulkUpdateBos(userProfiles);
					}
				}
			}
		}
	}
	
	/**
	 * clone all device group policy from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
//	public void cloneDeviceGroupPolicy(HmDomain srcDomain, HmDomain destDomain)
//			throws Exception {
//		if (null == srcDomain || destDomain == null) {
//			return;
//		}
//
//		List<DevicePolicy> list = getAlltheObjectsInDomain(DevicePolicy.class, srcDomain, this);
//		List<HmBo> newBos = new ArrayList<HmBo>();
//
//		for (DevicePolicy policy : list) {
//			policy.setId(null);
//			policy.setOwner(destDomain);
//			policy.setVersion(null);
//
//			if (null != policy.getRules()) {
//				List<DevicePolicyRule> rules = new ArrayList<DevicePolicyRule>();
//
//				for (DevicePolicyRule rule : policy.getRules()) {
//					if (null != rule.getMacObj()) {
//						rule.setMacObj(QueryUtil.findBoByAttribute(MacOrOui.class, "macOrOuiName", 
//							rule.getMacObj().getMacOrOuiName(), destDomain.getId()));
//					}
//					if (null != rule.getOsObj()) {
//						rule.setOsObj(QueryUtil.findBoByAttribute(OsObject.class, "osName", 
//							rule.getOsObj().getOsName(), destDomain.getId()));
//					}
////					if (null != rule.getUserProfile()) {
////						rule.setUserProfile(QueryUtil.findBoByAttribute(UserProfile.class, "userProfileName", 
////							rule.getUserProfile().getUserProfileName(), destDomain.getId()));
////					}
//					rules.add(rule);
//				}
//
//				policy.setRules(rules);
//			}
//
//			newBos.add(policy);
//		}
//
//		if (!newBos.isEmpty()) {
//			QueryUtil.bulkCreateBos(newBos);
//		}
//	}

	/**
	 * clone all RADIUS user profile rules from srcDomain to destDomain.
	 *
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 * @author Joseph Chen
	 * @since 06/10/2008
	 */
	public void cloneRadiusUserProfileRule(HmDomain srcDomain,
			HmDomain destDomain) throws Exception {
		if (null == srcDomain || destDomain == null) {
			return;
		}

		List<RadiusUserProfileRule> list = getAlltheObjectsInDomain(RadiusUserProfileRule.class,
				srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		RadiusUserProfileRule bo;

		for (RadiusUserProfileRule radiusUserProfileRule : list) {
			bo = QueryUtil.findBoById(
					RadiusUserProfileRule.class, radiusUserProfileRule
							.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			Set<UserProfile> newUserProfiles = new HashSet<UserProfile>();
			UserProfile newProfile;

			for (UserProfile profile : bo.getPermittedUserProfiles()) {
				newProfile = QueryUtil.findBoByAttribute(
						UserProfile.class, "userProfileName", profile
								.getUserProfileName(), destDomain.getId());
				newUserProfiles.add(newProfile);
			}

			bo.setPermittedUserProfiles(newUserProfiles);
			newBos.add(bo);
		}

		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	private void createMapHierarchy(MapContainerNode worldMapNode,
			HmDomain destDomain) {
		if (null == worldMapNode.getChildNodes()) {
			return;
		}
		for (MapNode node : worldMapNode.getChildNodes()) {
			try {
				if (!node.isLeafNode()) {
					MapContainerNode containerNode = (MapContainerNode) node;
					MapContainerNode parentMap = QueryUtil
							.findBoByAttribute(MapContainerNode.class,
									"mapName", containerNode.getParentMap()
											.getMapName(), destDomain.getId());
					if (null == parentMap) {
						continue;
					}
					createMapContainer(containerNode, parentMap, destDomain);
					createMapHierarchy(containerNode, destDomain);
				}
			} catch (Exception e) {
				log.error("createMapHierarchy", "Create map container error.",
						e);
			}
		}
	}

	private void createMapContainer(MapContainerNode mapContainer,
			MapContainerNode parent, HmDomain owner) throws Exception {
		if (null == mapContainer || null == parent || null == owner) {
			return;
		}
		MapContainerNode node = new MapContainerNode();
		node.setOwner(owner);
		node.setParentMap(parent);
		node.setMapName(mapContainer.getMapName());
		node.setActualHeight(mapContainer.getActualHeight());
		node.setActualWidth(mapContainer.getActualWidth());
		node.setBackground(mapContainer.getBackground());
		node.setEnvironment(mapContainer.getEnvironment());
		node.setHeight(mapContainer.getHeight());
		node.setIconName(mapContainer.getIconName());
		// for empty map container;
		// node.setSeverity(mapContainer.getSeverity());
		node.setWidth(mapContainer.getWidth());
		node.setX(mapContainer.getX());
		node.setY(mapContainer.getY());
		node.setMapType(mapContainer.getMapType());
		QueryUtil.createBo(node);
		BoMgmt.getBoEventMgmt().publishBoEvent(
				new BoEvent<MapContainerNode>(node, BoEventType.CREATED));
	}

	/**
	 * Clone all the Location Server from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneLocationServer(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<LocationServer> list = getAlltheObjectsInDomain(LocationServer.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (LocationServer bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			IpAddress serverIP = QueryUtil.findBoByAttribute(
					IpAddress.class, "addressName", bo.getServerIP()
							.getAddressName(), destDomain.getId());
			bo.setServerIP(serverIP);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the Ethernet Access from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneEthernetAccess(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<EthernetAccess> list = getAlltheObjectsInDomain(EthernetAccess.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (EthernetAccess ethernetAccess : list) {
			EthernetAccess bo = QueryUtil.findBoById(
					EthernetAccess.class, ethernetAccess.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			// fix bug 5991 ----- begin
			UserProfile newProfile = QueryUtil.findBoByAttribute(
					UserProfile.class, "userProfileName", bo.getUserProfile()
							.getUserProfileName(), destDomain.getId());
			// fix bug 5991 ----- end
			bo.setUserProfile(newProfile);
			Set<MacOrOui> macAddress = new HashSet<MacOrOui>();
			for (MacOrOui info : bo.getMacAddress()) {
				MacOrOui newMac = QueryUtil.findBoByAttribute(
						MacOrOui.class, "macOrOuiName", info.getMacOrOuiName(),
						destDomain.getId());
				macAddress.add(newMac);
			}
			bo.setMacAddress(macAddress);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the Access Console from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneAccessConsole(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<AccessConsole> list = getAlltheObjectsInDomain(AccessConsole.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (AccessConsole accessConsole : list) {
			AccessConsole bo = QueryUtil.findBoById(
					AccessConsole.class, accessConsole.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			Set<MacFilter> mac = new HashSet<MacFilter>();
			for (MacFilter info : bo.getMacFilters()) {
				MacFilter newMac = QueryUtil.findBoByAttribute(
						MacFilter.class, "filterName", info.getFilterName(),
						destDomain.getId());
				mac.add(newMac);
			}
			bo.setMacFilters(mac);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the Vlan Dhcp Server from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneVlanDhcpServer(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<VlanDhcpServer> list = getAlltheObjectsInDomain(VlanDhcpServer.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (VlanDhcpServer vlanDhcpServer : list) {
			VlanDhcpServer bo = QueryUtil.findBoById(
					VlanDhcpServer.class, vlanDhcpServer.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<DhcpServerIpPool> ipPools = new ArrayList<DhcpServerIpPool>();
			for (DhcpServerIpPool info : bo.getIpPools()) {
				ipPools.add(info);
			}
			bo.setIpPools(ipPools);
			List<DhcpServerOptionsCustom> customs = new ArrayList<DhcpServerOptionsCustom>();
			for (DhcpServerOptionsCustom single : bo.getCustoms()) {
				customs.add(single);
			}
			bo.setCustoms(customs);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the Custom Application from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneCustomApplication(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<CustomApplication> list = getAlltheObjectsInDomain(CustomApplication.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (CustomApplication customApp : list) {
			CustomApplication bo = QueryUtil.findBoById(CustomApplication.class,
					customApp.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<CustomApplicationRule> rules = new ArrayList<CustomApplicationRule>();
			bo.setRules(rules);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	/**
	 * Clone all the IP Policy from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneIpPolicy(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<IpPolicy> list = getAlltheObjectsInDomain(IpPolicy.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (IpPolicy ipPolicy : list) {
			IpPolicy bo = QueryUtil.findBoById(IpPolicy.class,
					ipPolicy.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<IpPolicyRule> rules = new ArrayList<IpPolicyRule>();
			for (IpPolicyRule info : bo.getRules()) {
				if (null != info.getSourceIp()) {
					IpAddress serverIP = QueryUtil
							.findBoByAttribute(IpAddress.class, "addressName",
									info.getSourceIp().getAddressName(),
									destDomain.getId());
					info.setSourceIp(serverIP);
				}
				if (null != info.getDesctinationIp()) {
					IpAddress serverIP = QueryUtil
							.findBoByAttribute(IpAddress.class, "addressName",
									info.getDesctinationIp().getAddressName(),
									destDomain.getId());
					info.setDesctinationIp(serverIP);
				}
				if (null != info.getNetworkService()) {
					NetworkService service = QueryUtil
							.findBoByAttribute(NetworkService.class,
									"serviceName", info.getNetworkService()
											.getServiceName(), destDomain
											.getId());
					info.setNetworkService(service);
				}
				if(null != info.getCustomApp()){
					CustomApplication customApp = QueryUtil.findBoByAttribute(CustomApplication.class, 
										info.getCustomApp().getCustomAppName(),
										destDomain.getId());
					info.setCustomApp(customApp);
				}
				rules.add(info);
			}
			bo.setRules(rules);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	/**
	 * Clone all the Firewall Policy from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneFirewallPolicy(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<FirewallPolicy> list = getAlltheObjectsInDomain(FirewallPolicy.class, srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (FirewallPolicy ipPolicy : list) {
			ipPolicy.setId(null);
			ipPolicy.setOwner(destDomain);
			ipPolicy.setVersion(null);
			List<FirewallPolicyRule> rules = new ArrayList<FirewallPolicyRule>();
			for (FirewallPolicyRule info : ipPolicy.getRules()) {
				if (null != info.getSourceIp()) {
					IpAddress serverIP = QueryUtil
							.findBoByAttribute(IpAddress.class, "addressName",
									info.getSourceIp().getAddressName(),
									destDomain.getId());
					info.setSourceIp(serverIP);
				}
				if (null != info.getSourceNtObj()) {
					VpnNetwork serverNet = QueryUtil
							.findBoByAttribute(VpnNetwork.class, "networkName",
									info.getSourceNtObj().getNetworkName(),
									destDomain.getId());
					info.setSourceNtObj(serverNet);
				}
				if (null != info.getSourceUp()) {
					UserProfile serverUp = QueryUtil
							.findBoByAttribute(UserProfile.class, "userProfileName",
									info.getSourceUp().getUserProfileName(),
									destDomain.getId());
					info.setSourceUp(serverUp);
				}
				if (null != info.getDestinationIp()) {
					IpAddress serverIP = QueryUtil
							.findBoByAttribute(IpAddress.class, "addressName",
									info.getDestinationIp().getAddressName(),
									destDomain.getId());
					info.setDestinationIp(serverIP);
				}
				if (null != info.getDestinationNtObj()) {
					VpnNetwork serverNet = QueryUtil
							.findBoByAttribute(VpnNetwork.class, "networkName",
									info.getDestinationNtObj().getNetworkName(),
									destDomain.getId());
					info.setDestinationNtObj(serverNet);
				}
				if (null != info.getNetworkService()) {
					NetworkService service = QueryUtil
							.findBoByAttribute(NetworkService.class,
									"serviceName", info.getNetworkService()
											.getServiceName(), destDomain
											.getId());
					info.setNetworkService(service);
				}
				rules.add(info);
			}
			ipPolicy.setRules(rules);
			newBos.add(ipPolicy);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the Radius On Hiveap from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneRadiusOnHiveap(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<RadiusOnHiveap> list = getAlltheObjectsInDomain(RadiusOnHiveap.class, srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (RadiusOnHiveap bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);

/*			// set the local user groups for LDAP server
			bo.setLdapOuUserProfiles(getLdapUserGroupBaseDomain(bo
					.getLdapOuUserProfiles(), destDomain.getId()));*/

			// set the local user groups for local dbtype
			bo.setLocalUserGroup(getLocalUserGroupBaseDomain(bo
					.getLocalUserGroup(), destDomain.getId()));

			List<ActiveDirectoryOrLdapInfo> directory = new ArrayList<ActiveDirectoryOrLdapInfo>();
			for (ActiveDirectoryOrLdapInfo info : bo.getDirectoryOrLdap()) {
				ActiveDirectoryOrOpenLdap directoryOrLdap = QueryUtil
						.findBoByAttribute(ActiveDirectoryOrOpenLdap.class,
								"name", info.getDirectoryOrLdap().getName(),
								destDomain.getId());
				info.setDirectoryOrLdap(directoryOrLdap);
				directory.add(info);
			}
			bo.setDirectoryOrLdap(directory);
			List<RadiusHiveapAuth> ipOrNames = new ArrayList<RadiusHiveapAuth>();
			for (RadiusHiveapAuth info : bo.getIpOrNames()) {
				IpAddress serverIP = QueryUtil.findBoByAttribute(
						IpAddress.class, "addressName", info.getIpAddress()
								.getAddressName(), destDomain.getId());
				info.setIpAddress(serverIP);
				ipOrNames.add(info);
			}
			bo.setIpOrNames(ipOrNames);
			
			// library sip policy
			if (null != bo.getSipPolicy()) {
				RadiusLibrarySip newSip = QueryUtil.findBoByAttribute(RadiusLibrarySip.class, "policyName",
					bo.getSipPolicy().getPolicyName(), destDomain.getId());
				bo.setSipPolicy(newSip);
			}
			
			// sip server
			if (null != bo.getSipServer()) {
				IpAddress serverIP = QueryUtil
						.findBoByAttribute(IpAddress.class, "addressName",
							bo.getSipServer().getAddressName(),
								destDomain.getId());
				bo.setSipServer(serverIP);
			}
			
			List<LdapServerOuUserProfile> ldapOuUserProfiles = new ArrayList<LdapServerOuUserProfile>();
			if (null != bo.getLdapOuUserProfiles()) {
				for (LdapServerOuUserProfile info : bo.getLdapOuUserProfiles()) {
					UserProfile upFile = QueryUtil.findBoByAttribute(UserProfile.class, "userProfileName",
							info.getUserProfileName(), destDomain.getId());
					if (null == upFile)
						continue;
					info.setUserProfileId(upFile.getId());
					if (info.getServerId() != null) {
						Long newAdLdapId = AhRestoreNewMapTools.getMapDirectoryOrLdap(info.getServerId());
/*						if (null == newAdLdapId)
							continue;*/
						info.setServerId(newAdLdapId);
					}
					info.setLocalUserGroup(QueryUtil.findBoByAttribute(LocalUserGroup.class, "groupName",
						info.getLocalUserGroup().getGroupName(), destDomain.getId()));
					ldapOuUserProfiles.add(info);
				}
				bo.setLdapOuUserProfiles(ldapOuUserProfiles);
			}
			
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Get groups in new domain base on the groups in old domain.
	 * 
	 * @param arg_Group old domain groups
	 * @param arg_DomainId new domain id
	 * @return Set<LocalUserGroup> : new domain groups
	 */
	private Set<LocalUserGroup> getLocalUserGroupBaseDomain(
			Set<LocalUserGroup> arg_Group, Long arg_DomainId) {
		if (null == arg_Group || null == arg_DomainId) {
			return null;
		}
		Set<LocalUserGroup> localGroup = new HashSet<LocalUserGroup>();
		for (LocalUserGroup oneGroup : arg_Group) {
			LocalUserGroup group = QueryUtil
					.findBoByAttribute(LocalUserGroup.class, "groupName",
							oneGroup.getGroupName(), arg_DomainId);
			localGroup.add(group);
		}
		return localGroup;
	}

/*	*//**
	 * Get groups in new domain base on the groups in old domain.
	 * 
	 * @param arg_LdapGroup old domain groups
	 * @param arg_DomainId new domain id
	 * @return List<LdapServerOuUserProfile> : new domain groups and user profiles
	 *//*
	private List<LdapServerOuUserProfile> getLdapUserGroupBaseDomain(
			List<LdapServerOuUserProfile> arg_LdapGroup, Long arg_DomainId) {
		if (null == arg_LdapGroup || null == arg_DomainId) {
			return null;
		}
		List<LdapServerOuUserProfile> ldapLocalGroup = new ArrayList<LdapServerOuUserProfile>();
		for (LdapServerOuUserProfile oneLdapGroup : arg_LdapGroup) {
			// query new local user group
			LocalUserGroup group = QueryUtil
					.findBoByAttribute(LocalUserGroup.class, "groupName",
							oneLdapGroup.getLocalUserGroup().getGroupName(), arg_DomainId);
			oneLdapGroup.setLocalUserGroup(group);
			
			// query new serverId (AD/LDAP/OD's id)
			ActiveDirectoryOrOpenLdap serverOld = QueryUtil
					.findBoById(ActiveDirectoryOrOpenLdap.class, oneLdapGroup
							.getServerId());
			ActiveDirectoryOrOpenLdap serverNew = QueryUtil.findBoByAttribute(
					ActiveDirectoryOrOpenLdap.class, "name", serverOld
							.getName(), arg_DomainId);
			oneLdapGroup.setServerId(serverNew.getId());
			
			ldapLocalGroup.add(oneLdapGroup);
		}
		return ldapLocalGroup;
	}*/

	/**
	 * Clone all the IDS Policy from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneIdsPolicy(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<IdsPolicy> list = getAlltheObjectsInDomain(IdsPolicy.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (IdsPolicy idsPolicy : list) {
			IdsPolicy bo = QueryUtil.findBoById(IdsPolicy.class,
					idsPolicy.getId(), this);
			if (null == bo) {
				continue;
			}
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			if (bo.isOuiEnable()) {
				Set<MacOrOui> macOrOuis = new HashSet<MacOrOui>();
				for (MacOrOui info : bo.getMacOrOuis()) {
					MacOrOui newMac = QueryUtil.findBoByAttribute(
							MacOrOui.class, "macOrOuiName", info
									.getMacOrOuiName(), destDomain.getId());
					macOrOuis.add(newMac);
				}
				bo.setMacOrOuis(macOrOuis);
			}
			if (bo.isInNetworkEnable()) {
				Set<Vlan> vlans = new HashSet<Vlan>();
				for (Vlan info : bo.getVlans()) {
					Vlan vlan = QueryUtil.findBoByAttribute(Vlan.class,
							"vlanName", info.getVlanName(), destDomain.getId());
					vlans.add(vlan);
				}
				bo.setVlans(vlans);
			}
			if (bo.isSsidEnable()) {
				List<IdsPolicySsidProfile> idsSsids = new ArrayList<IdsPolicySsidProfile>();
				for (IdsPolicySsidProfile info : bo.getIdsSsids()) {
					SsidProfile newSsid = QueryUtil
							.findBoByAttribute(SsidProfile.class, "ssidName",
									info.getSsidProfile().getSsidName(),
									destDomain.getId());
					info.setSsidProfile(newSsid);
					idsSsids.add(info);
				}
				bo.setIdsSsids(idsSsids);
			}
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the MAC Policy from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneMacPolicy(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<MacPolicy> list = getAlltheObjectsInDomain(MacPolicy.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (MacPolicy macPolicy : list) {
			MacPolicy bo = QueryUtil.findBoById(MacPolicy.class,
					macPolicy.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<MacPolicyRule> rules = new ArrayList<MacPolicyRule>();
			for (MacPolicyRule info : bo.getRules()) {
				if (null != info.getSourceMac()) {
					MacOrOui newMac = QueryUtil.findBoByAttribute(
							MacOrOui.class, "macOrOuiName", info.getSourceMac()
									.getMacOrOuiName(), destDomain.getId());
					info.setSourceMac(newMac);
				}
				if (null != info.getDestinationMac()) {
					MacOrOui newMac = QueryUtil.findBoByAttribute(
							MacOrOui.class, "macOrOuiName", info
									.getDestinationMac().getMacOrOuiName(),
							destDomain.getId());
					info.setDestinationMac(newMac);
				}
				rules.add(info);
			}
			bo.setRules(rules);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the ActiveDirectory or Ldap from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneActiveDirectoryOrLdap(HmDomain srcDomain,
			HmDomain destDomain) throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<ActiveDirectoryOrOpenLdap> list = getAlltheObjectsInDomain(
				ActiveDirectoryOrOpenLdap.class, srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		List<Long> lOldId = new ArrayList<Long>();
		IpAddress serverIP;
		for (ActiveDirectoryOrOpenLdap bo : list) {
			lOldId.add(bo.getId());
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			if (ActiveDirectoryOrOpenLdap.TYPE_ACTIVE_DIRECTORY == bo
					.getTypeFlag()) {
				serverIP = QueryUtil.findBoByAttribute(
						IpAddress.class, "addressName", bo.getAdServer()
								.getAddressName(), destDomain.getId());
				bo.setAdServer(serverIP);
				List<ActiveDirectoryDomain> newDomain = new ArrayList<ActiveDirectoryDomain>();
				for (ActiveDirectoryDomain oneDomain : bo.getAdDomains()) {
					newDomain.add(oneDomain);
				}
				bo.setAdDomains(newDomain);
			} else {
				serverIP = QueryUtil.findBoByAttribute(
						IpAddress.class, "addressName", bo.getLdapServer()
								.getAddressName(), destDomain.getId());
				bo.setLdapServer(serverIP);
			}
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
			
			for(int i=0; i < newBos.size(); ++i)
			{
				AhRestoreNewMapTools.setMapDirectoryOrLdap(lOldId.get(i), newBos.get(i).getId());
			}
		}
	}

	/**
	 * Clone all the Radius Assignment from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneRadiusAssignment(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<RadiusAssignment> list = getAlltheObjectsInDomain(RadiusAssignment.class,
				srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (RadiusAssignment radiusAssignment : list) {
			RadiusAssignment bo = QueryUtil.findBoById(
					RadiusAssignment.class, radiusAssignment.getId(),
					this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<RadiusServer> services = new ArrayList<RadiusServer>();
			for (RadiusServer info : bo.getServices()) {
				IpAddress serverIP = QueryUtil.findBoByAttribute(
						IpAddress.class, "addressName", info.getIpAddress()
								.getAddressName(), destDomain.getId());
				info.setIpAddress(serverIP);
				services.add(info);
			}
			bo.setServices(services);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the Local User from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneLocalUser(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<LocalUser> list = getAlltheObjectsInDomain(LocalUser.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (LocalUser bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			if (null != bo.getLocalUserGroup()) {
				LocalUserGroup group = QueryUtil
						.findBoByAttribute(LocalUserGroup.class, "groupName",
								bo.getLocalUserGroup().getGroupName(),
								destDomain.getId());
				bo.setLocalUserGroup(group);
			}
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	/**
	 * Clone all the Print Template from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void clonePrintTemplate(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return ;
		}
		
		List<PrintTemplate> list = getAlltheObjectsInDomain(PrintTemplate.class, srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		
		for (PrintTemplate bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			
			Map<String, TemplateField> fields = new HashMap<String, TemplateField>();

			for (String field : TemplateField.FIELDS) {
				TemplateField newField = bo.getField(field);

				if (newField == null) {
					continue;
				}

				newField.setField(field);
				fields.put(field, newField);
			}

			bo.setFields(fields);

			newBos.add(bo);
		}
		
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the Local User group from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneLocalUserGroup(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<LocalUserGroup> list = getAlltheObjectsInDomain(LocalUserGroup.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (LocalUserGroup bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			if (null != bo.getSchedule()) {
				Scheduler scheduler = QueryUtil.findBoByAttribute(
						Scheduler.class, "schedulerName", bo.getSchedule()
								.getSchedulerName(), destDomain.getId());
				bo.setSchedule(scheduler);
			}
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	/**
	 * Clone all the RadiusLibrarySip from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneRadiusLibrarySip(HmDomain srcDomain,
			HmDomain destDomain) throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<RadiusLibrarySip> list = getAlltheObjectsInDomain(
			RadiusLibrarySip.class, srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (RadiusLibrarySip bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			if (null != bo.getDefUserGroup()) {
				bo.setDefUserGroup(QueryUtil.findBoByAttribute(
					LocalUserGroup.class, "groupName", bo.getDefUserGroup()
					.getGroupName(), destDomain.getId()));
			}
			List<RadiusLibrarySipRule> rules = new ArrayList<RadiusLibrarySipRule>();
			for (RadiusLibrarySipRule info : bo.getRules()) {
				LocalUserGroup group = QueryUtil.findBoByAttribute(
					LocalUserGroup.class, "groupName", info.getUserGroup()
								.getGroupName(), destDomain.getId());
				info.setUserGroup(group);
				rules.add(info);
			}
			bo.setRules(rules);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone DosPrevention table information from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneDosPrevention(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<DosPrevention> list = getAlltheObjectsInDomain(DosPrevention.class, srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (DosPrevention bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			Map<String, DosParams> dosParamsMap = new LinkedHashMap<String, DosParams>();
			if (bo.getDosType() == DosType.IP) {
				for (ScreeningType screeningType : DosParams.ScreeningType
						.values()) {
					DosParams dosParams = bo.getDosParams(screeningType);
					if (dosParams == null) {
						dosParams = new DosParams();
					}
					dosParams.setScreeningType(screeningType);
					dosParamsMap.put(dosParams.getkey(), dosParams);
				}
			} else {
				for (FrameType frameType : DosParams.FrameType.values()) {
					DosParams dosParams = bo.getDosParams(frameType);
					if (dosParams == null) {
						dosParams = new DosParams();
					}
					dosParams.setFrameType(frameType);
					dosParamsMap.put(dosParams.getkey(), dosParams);
				}
			}

			bo.setDosParamsMap(dosParamsMap);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone QosRateControl table information from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneQosRateControl(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<QosRateControl> list = getAlltheObjectsInDomain(QosRateControl.class, srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (QosRateControl bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<QosRateLimit> qosRateLimit = new ArrayList<QosRateLimit>();
			for (QosRateLimit info : bo.getQosRateLimit()) {
				qosRateLimit.add(info);
			}
			bo.setQosRateLimit(qosRateLimit);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone AlgConfiguration table information from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneAlgConfiguration(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<AlgConfiguration> list = getAlltheObjectsInDomain(AlgConfiguration.class,
				srcDomain,this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (AlgConfiguration bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			Map<String, AlgConfigurationInfo> newitem = new LinkedHashMap<String, AlgConfigurationInfo>();
			for (GatewayType gatewayType : AlgConfigurationInfo.GatewayType
					.values()) {
				AlgConfigurationInfo oneItem = bo.getAlgInfo(gatewayType);
				if (oneItem == null) {
					oneItem = new AlgConfigurationInfo();
				}
				oneItem.setGatewayType(gatewayType);
				newitem.put(oneItem.getkey(), oneItem);
			}
			bo.setItems(newitem);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone RadioProfile table information from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneRadioProfile(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<RadioProfile> list = getAlltheObjectsInDomain(RadioProfile.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (RadioProfile radioProfile : list) {
			RadioProfile bo = QueryUtil.findBoById(
					RadioProfile.class, radioProfile.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			Map<String, RadioProfileWmmInfo> newitem = new LinkedHashMap<String, RadioProfileWmmInfo>();
			for (AccessCategory acType : RadioProfileWmmInfo.AccessCategory
					.values()) {
				RadioProfileWmmInfo oneItem = bo.getWmmInfo(acType);
				if (oneItem == null) {
					oneItem = new RadioProfileWmmInfo();
				}
				oneItem.setAcType(acType);
				newitem.put(oneItem.getkey(), oneItem);
			}
			
			Set<MacOrOui> macOrOuis = new HashSet<MacOrOui>();
			if (!bo.isEnableBroadcastProbe()) {
				if(bo.isEnableSupressBPRByOUI()){
					for (MacOrOui info : bo.getSupressBprOUIs()) {
						MacOrOui newMac = QueryUtil.findBoByAttribute(
								MacOrOui.class, "macOrOuiName", info
										.getMacOrOuiName(), destDomain.getId());
						macOrOuis.add(newMac);
					}
				}
			}
			bo.setWmmItems(newitem);
			bo.setSupressBprOUIs(macOrOuis);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	public void cloneIdpSettings(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<IdpSettings> list = getAlltheObjectsInDomain(IdpSettings.class, srcDomain, this);

		List<HmBo> newBos = new ArrayList<HmBo>();

		for (IdpSettings bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);

			if (null != bo.getEnclosedFriendlyAps()) {
				List<String> bssids = new ArrayList<String>();
				bssids.addAll(bo.getEnclosedFriendlyAps());
				bo.setEnclosedFriendlyAps(bssids);
			}

			if (null != bo.getEnclosedRogueAps()) {
				List<String> bssids = new ArrayList<String>();
				bssids.addAll(bo.getEnclosedRogueAps());
				bo.setEnclosedRogueAps(bssids);
			}

			newBos.add(bo);
		}

		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	/**
	 * Clone bonjour service table from source VHM into destination VHM
	 * 
	 * @param srcDomain
	 *            source VHM
	 * @param destDomain
	 *            destination VHM
	 * @throws Exception -
	 * @author wpliang
	 */
	public void cloneService(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<BonjourService> list = getAlltheObjectsInDomain(BonjourService.class, srcDomain, this);
		
		List<HmBo> newBos = new ArrayList<HmBo>();
		List<Long> lOldId = new ArrayList<Long>();
		
		for (BonjourService bo : list) {
			lOldId.add(bo.getId());
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);

			if (null != bo.getBonjourServiceCategory()) {
				BonjourServiceCategory category = QueryUtil.findBoByAttribute(BonjourServiceCategory.class, "serviceCategoryName", bo.getBonjourServiceCategory().getServiceCategoryName(), destDomain.getId());
				bo.setBonjourServiceCategory(category);
			}
		
			newBos.add(bo);
		}
		
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
			for(int i=0; i<list.size(); i++)
			{
				AhRestoreNewMapTools.setMapBonjourService(lOldId.get(i), newBos.get(i).getId());
			}
		}
	}
	
	/**
	 * Clone bonjour gateway setting table from source VHM into destination VHM
	 * 
	 * @param srcDomain
	 *            source VHM
	 * @param destDomain
	 *            destination VHM
	 * @throws Exception -
	 * @author wpliang
	 */
	public void cloneBonjourGatewaySetting(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<BonjourGatewaySettings> list = getAlltheObjectsInDomain(BonjourGatewaySettings.class, srcDomain, this);
		
		List<HmBo> newBos = new ArrayList<HmBo>();
		String[] serviceTypes = BonjourService.getDefaultBonjouServiceType();
		for (BonjourGatewaySettings bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			if (null != bo.getBonjourActiveServices()) {
				List<BonjourActiveService> bonjourActiveServices = new ArrayList<BonjourActiveService>();
				List<BonjourFilterRule> bonjourlFilterRules = new ArrayList<BonjourFilterRule>();
				for(BonjourActiveService activeService : bo.getBonjourActiveServices()){
					if (activeService.getBonjourService() != null) {
						BonjourActiveService bonjourActiveService = new BonjourActiveService();
						String type = activeService.getBonjourService().getType();
						boolean bool =false;
						for(String serviceType : serviceTypes){
							if(serviceType.equals(type)){
								bonjourActiveServices.add(activeService);
								bool = true;
								break;
							}
						}
						if(bool){
							continue;
						}
					
						Long serviceId_new = AhRestoreNewMapTools.getMapBonjourService(activeService.getBonjourService().getId());
						if (null != serviceId_new) {
							bonjourActiveService.setBonjourService((AhRestoreNewTools
									.CreateBoWithId(BonjourService.class,
											serviceId_new)));
							bonjourActiveServices.add(bonjourActiveService);
						} else {
							log.error("cloneBonjourGatewaySetting", "Cound not find the new singleInfo id mapping to old id:"+activeService.getBonjourService().getId());
						}
					} else{
						log.error("cloneBonjourGatewaySetting", "Cound not find the active service");
					}
				}
				bo.setBonjourActiveServices(bonjourActiveServices);
				for(BonjourFilterRule rule : bo.getRules()){
					BonjourFilterRule newRule = new BonjourFilterRule();
					String type = rule.getBonjourService().getType();
					boolean bool =false;
					for(String serviceType : serviceTypes){
						if(serviceType.equals(type)){
							bool = true;
							break;
						}
					}
					if(bool){
						newRule.setBonjourService(rule.getBonjourService());
					} else {
						Long serviceId_new = AhRestoreNewMapTools.getMapBonjourService(rule.getBonjourService().getId());
						if (null != serviceId_new) {
							newRule.setBonjourService((AhRestoreNewTools
									.CreateBoWithId(BonjourService.class,
											serviceId_new)));
						} else {
							log.error("cloneBonjourGatewaySetting", "Cound not find the new singleInfo id mapping to old id:"+rule.getBonjourService().getId());
						}
					}
					
					if(rule.getFromVlanGroup() != null){
						Long fromVlanGroupId_new = AhRestoreNewMapTools.getMapVlanGroup(rule.getFromVlanGroup().getId());
						if (null != fromVlanGroupId_new) {
							newRule.setFromVlanGroup((AhRestoreNewTools
									.CreateBoWithId(VlanGroup.class,
											fromVlanGroupId_new)));
						} else {
							log.error("cloneBonjourGatewaySetting", "Cound not find the new singleInfo id mapping to old id(FromVlanGroup):"+rule.getFromVlanGroup().getId());
						}
					}
					
					if(rule.getToVlanGroup() != null){
						Long toVlanGroupId_new = AhRestoreNewMapTools.getMapVlanGroup(rule.getToVlanGroup().getId());
						if (null != toVlanGroupId_new) {
							newRule.setFromVlanGroup((AhRestoreNewTools
									.CreateBoWithId(VlanGroup.class,
											toVlanGroupId_new)));
						} else {
							log.error("cloneBonjourGatewaySetting", "Cound not find the new singleInfo id mapping to old id(ToVlanGroup):"+rule.getToVlanGroup().getId());
						}
					}
					newRule.setRuleId(rule.getRuleId());
					newRule.setMetric(rule.getMetric());
					bonjourlFilterRules.add(newRule);
				}
				bo.setRules(bonjourlFilterRules);
			}
			
			newBos.add(bo);
		}
		
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	/**
	 * Clone Cwp table from source VHM into destination VHM
	 * 
	 * @param srcDomain
	 *            source VHM
	 * @param destDomain
	 *            destination VHM
	 * @throws Exception -
	 * @author Joseph Chen
	 */
	public void cloneCwp(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}

		List<Cwp> list = getAlltheObjectsInDomain(Cwp.class, srcDomain);

		List<HmBo> newBos = new ArrayList<HmBo>();
		Cwp bo;
		CwpPageCustomization customization;

		for (Cwp cwp : list) {
			bo = QueryUtil.findBoById(Cwp.class, cwp.getId(),
					this);

			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);

			customization = bo.getPageCustomization();
			Map<String, CwpPageField> newitem = new LinkedHashMap<String, CwpPageField>();

			for (String field : CwpPageField.FIELDS) {
				CwpPageField oneItem = customization.getPageField(field);

				if (oneItem == null) {
					oneItem = new CwpPageField();
				}

				oneItem.setField(field);
				newitem.put(field, oneItem);
			}

			customization.setFields(newitem);

			if (null != bo.getCertificate()) {
				CwpCertificate certificate = QueryUtil
						.findBoByAttribute(CwpCertificate.class, "certName", bo
								.getCertificate().getCertName(), destDomain
								.getId());
				bo.setCertificate(certificate);
			}

			if (null != bo.getVlan()) {
				Vlan vlan = QueryUtil.findBoByAttribute(Vlan.class,
						"vlanName", bo.getVlan().getVlanName(), destDomain
								.getId());

				bo.setVlan(vlan);
			}

			if (null != bo.getWalledGarden()
					&& !bo.getWalledGarden().isEmpty()) {
				List<WalledGardenItem> walledGarden = new ArrayList<WalledGardenItem>();
				walledGarden.addAll(bo.getWalledGarden());
				bo.setWalledGarden(walledGarden);
			}

			newBos.add(bo);
		}

		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	public void cloneVpnService(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		List<VpnService> list = getAlltheObjectsInDomain(VpnService.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (VpnService vpnService : list) {
			VpnService vpn = QueryUtil.findBoById(VpnService.class, vpnService
					.getId(), this);
			if (null == vpn) {
				continue;
			}
			vpn.setId(null);
			vpn.setVersion(null);
			vpn.setOwner(destDomain);
			if (null != vpn.getDnsIp()) {
				IpAddress ip = QueryUtil.findBoByAttribute(
						IpAddress.class, "addressName", vpn.getDnsIp()
								.getAddressName(), destDomain.getId());
				vpn.setDnsIp(ip);
			}
			List<VpnServiceCredential> list_sc = new ArrayList<VpnServiceCredential>();
//			for (VpnServiceCredential sc : vpn.getVpnCredentials()) {
//				sc.setAllocated(false);
//				sc.setAssignedClient(null);
//				sc.setPrimaryRole(VpnServiceCredential.SERVER_ROLE_NONE);
//				sc.setBackupRole(VpnServiceCredential.SERVER_ROLE_NONE);
//				list_sc.add(sc);
//			}
			vpn.setVpnCredentials(list_sc);
			
			//set vpn gateway
			List<VpnGatewaySetting> list_gs = new ArrayList<VpnGatewaySetting>();
			vpn.setVpnGateWaysSetting(list_gs);
			
			//set User Profiles for Traffic Management in layer 3
			List<UserProfileForTrafficL3> list_up3 = new ArrayList<UserProfileForTrafficL3>();
			for(UserProfileForTrafficL3 userProfileForTrafficL3:vpn.getUserProfileTrafficL3()){
				if(null != userProfileForTrafficL3.getUserProfile()){
					UserProfile ups = QueryUtil.findBoById(UserProfile.class, userProfileForTrafficL3.getUserProfile().getId(), this);
					if(null != ups){
						UserProfile upd = QueryUtil.findBoByAttribute(UserProfile.class, "userProfileName", ups.getUserProfileName(), destDomain.getId(), this);
						if(null != upd){
							userProfileForTrafficL3.setUserProfile(upd);
							list_up3.add(userProfileForTrafficL3);
						}
					}
				}
			}
			vpn.setUserProfileTrafficL3(list_up3);
			
			//set User Profiles for Traffic Management in layer 2
			List<UserProfileForTrafficL2> list_up2 = new ArrayList<UserProfileForTrafficL2>();
			for(UserProfileForTrafficL2 userProfileForTrafficL2:vpn.getUserProfileTrafficL2()){
				if(userProfileForTrafficL2.getVpnTunnelModeL2() == UserProfileForTrafficL2.VPNTUNNEL_MODE_ENABLED){
					if(null != userProfileForTrafficL2.getUserProfile()){
						UserProfile ups = QueryUtil.findBoById(UserProfile.class, userProfileForTrafficL2.getUserProfile().getId(), this);
						if(null != ups){
							UserProfile upd = QueryUtil.findBoByAttribute(UserProfile.class, "userProfileName", ups.getUserProfileName(), destDomain.getId(), this);
							if(null != upd){
								userProfileForTrafficL2.setUserProfile(upd);
								list_up2.add(userProfileForTrafficL2);
							}
						}
					}
				}
			}
			vpn.setUserProfileTrafficL2(list_up2);
			
			//set Tunnel Exception Destination 
			if (null != vpn.getDomObj()) {
				List<DomainObject> objs = QueryUtil.executeQuery(DomainObject.class, null,
						new FilterParams(
								"objName=:s1 AND objType=:s2",
								new Object[] {
										vpn.getDomObj().getObjName(),
										DomainObject.VPN_TUNNEL}),
						destDomain.getId(),this);
				if (!objs.isEmpty()) {
					vpn.setDomObj(objs.get(0));
				}
			}

			newBos.add(vpn);
		}

		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	/**
	 * Clone all the Radius Proxy from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneRadiusProxy(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<RadiusProxy> list = getAlltheObjectsInDomain(RadiusProxy.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (RadiusProxy radiusProxy : list) {
			RadiusProxy bo = QueryUtil.findBoById(
				RadiusProxy.class, radiusProxy.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);

			List<RadiusProxyRealm> radiusRealm = new ArrayList<RadiusProxyRealm>();
			for (RadiusProxyRealm info : bo.getRadiusRealm()) {
				RadiusAssignment primary = QueryUtil
					.findBoByAttribute(RadiusAssignment.class,
							"radiusName", info.getRadiusServer().getRadiusName(),
								destDomain.getId());
				info.setRadiusServer(primary);
				radiusRealm.add(info);
			}
			bo.setRadiusRealm(radiusRealm);
			List<RadiusHiveapAuth> ipOrNames = new ArrayList<RadiusHiveapAuth>();
			for (RadiusHiveapAuth info : bo.getRadiusNas()) {
				IpAddress serverIP = QueryUtil.findBoByAttribute(
						IpAddress.class, "addressName", info.getIpAddress()
								.getAddressName(), destDomain.getId());
				info.setIpAddress(serverIP);
				ipOrNames.add(info);
			}
			bo.setRadiusNas(ipOrNames);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	/**
	 * TA620: DNS profile domain clone
	 * - Clone the DnsServiceProfile from srcDomain to destDomain
	 * @author Yunzhi Lin
	 * - Time: Jul 22, 2011 3:21:51 PM
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneDNSServices(HmDomain srcDomain, HmDomain destDomain) throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}

		List<DnsServiceProfile> list = getAlltheObjectsInDomain(DnsServiceProfile.class, srcDomain);

		List<HmBo> newBos = new ArrayList<HmBo>();
		DnsServiceProfile bo;
		IpAddress ipAddress;

		for (DnsServiceProfile dnsService : list) {
			bo = QueryUtil.findBoById(DnsServiceProfile.class, dnsService.getId(), this);

			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);

			if (null != bo.getInternalDns1()) {
				ipAddress = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", bo
						.getInternalDns1().getAddressName(), destDomain.getId());
				bo.setInternalDns1(ipAddress);
			}
			if (null != bo.getInternalDns2()) {
				ipAddress = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", bo
						.getInternalDns2().getAddressName(), destDomain.getId());
				bo.setInternalDns2(ipAddress);
			}
			if (null != bo.getInternalDns3()) {
				ipAddress = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", bo
						.getInternalDns2().getAddressName(), destDomain.getId());
				bo.setInternalDns3(ipAddress);
			}
			if (null != bo.getDomainObj()) {
				DomainObject domainObj = QueryUtil.findBoByAttribute(DomainObject.class, "objName",
						bo.getDomainObj().getObjName(), destDomain.getId());
				bo.setDomainObj(domainObj);
			}
			if (null != bo.getExternalDns1()) {
				ipAddress = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", bo
						.getExternalDns1().getAddressName(), destDomain.getId());
				bo.setExternalDns1(ipAddress);
			}
			if (null != bo.getExternalDns2()) {
				ipAddress = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", bo
						.getExternalDns2().getAddressName(), destDomain.getId());
				bo.setExternalDns2(ipAddress);
			}
			if (null != bo.getExternalDns3()) {
				ipAddress = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", bo
						.getExternalDns3().getAddressName(), destDomain.getId());
				bo.setExternalDns3(ipAddress);
			}

			if (!(null == bo.getSpecificInfos() || bo.getSpecificInfos().isEmpty())) {
				List<DnsSpecificSettings> specifics = new ArrayList<DnsSpecificSettings>();
				for (DnsSpecificSettings ds : bo.getSpecificInfos()) {
				    ds.setDnsServer(QueryUtil.findBoByAttribute(IpAddress.class, "addressName", 
				            ds.getDnsServer().getAddressName(), destDomain.getId()));
                    specifics.add(ds);
                }
				bo.setSpecificInfos(specifics);
			}

			newBos.add(bo);
		}

		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	/**
	 * @author huihe@aerohive.com
	 * @param srcDomain
	 * @param destDomain
	 * @throws Exception
	 */
	public void cloneOpenDNSAccount(HmDomain srcDomain, HmDomain destDomain) throws Exception {
		if (null == srcDomain || null == destDomain)
			return;
		List<OpenDNSAccount> list = getAlltheObjectsInDomain(OpenDNSAccount.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (OpenDNSAccount openDNSAccount : list) {
			OpenDNSAccount bo = QueryUtil.findBoById(
					OpenDNSAccount.class, openDNSAccount.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);

			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	/**
	 * @author huihe@aerohive.com
	 * @param srcDomain
	 * @param destDomain
	 * @throws Exception
	 */
	public void cloneOpenDNSDevice(HmDomain srcDomain, HmDomain destDomain) throws Exception {
		if (null == srcDomain || null == destDomain)
			return;
		List<OpenDNSDevice> list = getAlltheObjectsInDomain(OpenDNSDevice.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (OpenDNSDevice openDNSDevice : list) {
			OpenDNSDevice bo = QueryUtil.findBoById(
					OpenDNSDevice.class, openDNSDevice.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			
			if (bo.getOpenDNSAccount() != null) {
				bo.setOpenDNSAccount(QueryUtil
						.findBoByAttribute(OpenDNSAccount.class,
								"userName", bo.getOpenDNSAccount().getUserName(),
								destDomain.getId()));
			}

			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	/**
	 * @author huihe@aerohive.com
	 * @param srcDomain
	 * @param destDomain
	 * @throws Exception
	 */
	public void cloneOpenDNSMapping(HmDomain srcDomain, HmDomain destDomain) throws Exception {
		if (null == srcDomain || null == destDomain)
			return;
		List<OpenDNSMapping> list = getAlltheObjectsInDomain(OpenDNSMapping.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (OpenDNSMapping openDNSMapping : list) {
			OpenDNSMapping bo = QueryUtil.findBoById(
					OpenDNSMapping.class, openDNSMapping.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			
			if (bo.getOpenDNSAccount() != null) {
				bo.setOpenDNSAccount(QueryUtil
						.findBoByAttribute(OpenDNSAccount.class,
								"userName", bo.getOpenDNSAccount().getUserName(),
								destDomain.getId()));
			}
			
			if (bo.getOpenDNSDevice() != null) {
				bo.setOpenDNSDevice(QueryUtil
						.findBoByAttribute(OpenDNSDevice.class,
								"deviceId", bo.getOpenDNSDevice().getDeviceId(),
								destDomain.getId()));
			}
			
			if (bo.getUserProfile() != null) {
				bo.setUserProfile(QueryUtil
						.findBoByAttribute(UserProfile.class,
								"userProfileName", bo.getUserProfile().getUserProfileName(),
								destDomain.getId()));
			}

			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	/**
	 * @author huihe@aerohive.com
	 * @param srcDomain
	 * @param destDomain
	 * @throws Exception
	 */
	public void cloneWifiClientPreSSID(HmDomain srcDomain, HmDomain destDomain) throws Exception {
		if (null == srcDomain || null == destDomain)
			return;
		List<WifiClientPreferredSsid> list = getAlltheObjectsInDomain(WifiClientPreferredSsid.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (WifiClientPreferredSsid preferredSSID : list) {
			WifiClientPreferredSsid bo = QueryUtil.findBoById(
					WifiClientPreferredSsid.class, preferredSSID.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	/**
	 * Clone all the Routing Profiles from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneRoutingProfiles(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain)
			return;
		List<RoutingProfile> list = getAlltheObjectsInDomain(RoutingProfile.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (RoutingProfile routingProfile : list) {
			RoutingProfile bo = QueryUtil.findBoById(
					RoutingProfile.class, routingProfile.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);

			List<NeighborsNameItem> items = new ArrayList<NeighborsNameItem>();
			for (NeighborsNameItem tempClass : bo.getItems()) {
				items.add(tempClass);
			}
			bo.setItems(items);

			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}

	/**
	 * Clone all the RADIUS operator name attributes from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneRadiusAttri(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<RadiusAttrs> list = getAlltheObjectsInDomain(RadiusAttrs.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (RadiusAttrs radiusAttrs : list) {
			RadiusAttrs bo = QueryUtil.findBoById(
					RadiusAttrs.class, radiusAttrs.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);

			List<SingleTableItem> items = new ArrayList<SingleTableItem>();
			for (SingleTableItem info : bo.getItems()) {
				if (SingleTableItem.TYPE_MAP == info.getType()) {
					MapContainerNode newLocation = QueryUtil
							.findBoByAttribute(MapContainerNode.class,
									"mapName", info.getLocation().getMapName(),
									destDomain.getId());
					info.setLocation(newLocation);
				}
				items.add(info);
			}
			bo.setItems(items);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
	}
	
	public void cloneRoutingProfilePolicies(HmDomain srcDomain, HmDomain destDomain)
	{
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<RoutingProfilePolicy> list = getAlltheObjectsInDomain(RoutingProfilePolicy.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (RoutingProfilePolicy routingprofilepolicy : list) {
			RoutingProfilePolicy bo = QueryUtil.findBoById(
					RoutingProfilePolicy.class, routingprofilepolicy.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<RoutingProfilePolicyRule> rules = new ArrayList<RoutingProfilePolicyRule>();
			for (RoutingProfilePolicyRule rule : bo.getRoutingProfilePolicyRuleList()) {
				RoutingProfilePolicyRule descRule = (RoutingProfilePolicyRule) rule.clone();
				rules.add(descRule);
			}
			bo.setRoutingProfilePolicyRuleList(rules);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			try {
				QueryUtil.bulkCreateBos(newBos);
			} catch (Exception e) {
				log.error("Failed to clone routing policies between domains.", e);
			}
		}
	}
	
	public void cloneConfigMdmPolicies(HmDomain srcDomain, HmDomain destDomain)
	{
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<ConfigTemplateMdm> list = getAlltheObjectsInDomain(ConfigTemplateMdm.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (ConfigTemplateMdm configtemplatemdm : list) {
			ConfigTemplateMdm bo = QueryUtil.findBoById(ConfigTemplateMdm.class, configtemplatemdm.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
		
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			try {
				QueryUtil.bulkCreateBos(newBos);
			} catch (Exception e) {
				log.error("Failed to clone routing policies between domains.", e);
			}
		}
	}
	public void cloneMdmProfilesPolicies(HmDomain srcDomain, HmDomain destDomain)
	{
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<MdmProfiles> list = getAlltheObjectsInDomain(MdmProfiles.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (MdmProfiles mdmProfiles : list) {
			MdmProfiles bo = QueryUtil.findBoById(MdmProfiles.class, mdmProfiles.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
		
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			try {
				QueryUtil.bulkCreateBos(newBos);
			} catch (Exception e) {
				log.error("Failed to clone mdmProfiles policies between domains.", e);
			}
		}
	}
	
	public void cloneMdmProfilesPoliciesViaApi(HmDomain srcDomain, HmDomain destDomain)
	{
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<MdmProfiles> list = getAlltheObjectsInDomain(MdmProfiles.class, srcDomain);
		ProfileMgrServiceImpl impl =new ProfileMgrServiceImpl();
		try {
			for (MdmProfiles mdmProfiles : list) {
				MdmObject object = new MdmObject();
				object = impl.getMdmProfile(mdmProfiles.getMdmProfilesName(), srcDomain.getId().toString());
				impl.setMdmProfile(object.getValidTimeInfo(),object.getConfigurationProfileInfo(), destDomain.getId().toString());
			}
		} catch (Exception e) {
			log.error("Failed to clone mdmProfiles policies(in MDM via api) between domains.", e);
		}
	}
	
	public void cloneRoutingPolicies(HmDomain srcDomain, HmDomain destDomain) {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<RoutingPolicy> list = getAlltheObjectsInDomain(RoutingPolicy.class, srcDomain);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (RoutingPolicy routingPolicy : list) {
			RoutingPolicy bo = QueryUtil.findBoById(
					RoutingPolicy.class, routingPolicy.getId(), this);
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			
			if (bo.getIpTrackForCheck() != null) {
				bo.setIpTrackForCheck(QueryUtil
						.findBoByAttribute(MgmtServiceIPTrack.class,
								"trackName", bo.getIpTrackForCheck().getTrackName(),
								destDomain.getId()));
			}
			if (bo.getDomainObjectForDesList() != null) {
				bo.setDomainObjectForDesList(QueryUtil
						.findBoByAttribute(DomainObject.class,
								"objName", bo.getDomainObjectForDesList().getObjName(),
								destDomain.getId()));
			}

			List<RoutingPolicyRule> rules = new ArrayList<RoutingPolicyRule>();
			for (RoutingPolicyRule rule : bo.getRoutingPolicyRuleList()) {
				RoutingPolicyRule descRule = rule.clone();
				if (rule.getIpTrackReachablePri() != null) {
					descRule.setIpTrackReachablePri(QueryUtil
							.findBoByAttribute(MgmtServiceIPTrack.class,
									"trackName", rule.getIpTrackReachablePri().getTrackName(),
									destDomain.getId()));
				}
				if (rule.getIpTrackReachableSec() != null) {
					descRule.setIpTrackReachableSec(QueryUtil
							.findBoByAttribute(MgmtServiceIPTrack.class,
									"trackName", rule.getIpTrackReachableSec().getTrackName(),
									destDomain.getId()));
				}
				if (rule.getSourceUserProfile() != null) {
					descRule.setSourceUserProfile(QueryUtil
							.findBoByAttribute(UserProfile.class,
									"userProfileName", rule.getSourceUserProfile().getUserProfileName(),
									destDomain.getId()));
				}
				rules.add(descRule);
			}
			bo.setRoutingPolicyRuleList(rules);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			try {
				QueryUtil.bulkCreateBos(newBos);
			} catch (Exception e) {
				log.error("Failed to clone routing policies between domains.", e);
			}
		}
	}

    public void clonePortAccessProfile(HmDomain srcDomain, HmDomain destDomain)
            throws Exception {
        if (null == srcDomain || null == destDomain)
            return;
        List<PortAccessProfile> list = getAlltheObjectsInDomain(
                PortAccessProfile.class, srcDomain);
        List<HmBo> newBos = new ArrayList<HmBo>();
        PortAccessProfile bo;
        for (PortAccessProfile accProfile : list) {
            bo = QueryUtil.findBoById(PortAccessProfile.class,
                    accProfile.getId(), this);
            bo.setId(null);
            bo.setOwner(destDomain);
            bo.setVersion(null);

            if (bo.getDefUserProfile() != null) {
                bo.setDefUserProfile(QueryUtil.findBoByAttribute(
                        UserProfile.class, "userProfileName", bo
                                .getDefUserProfile().getUserProfileName(),
                        destDomain.getId()));
            }
            if (bo.getSelfRegUserProfile() != null) {
                bo.setSelfRegUserProfile(QueryUtil.findBoByAttribute(
                        UserProfile.class, "userProfileName", bo
                                .getSelfRegUserProfile().getUserProfileName(),
                        destDomain.getId()));
            }
            if (bo.getGuestUserProfile() != null) {
                bo.setGuestUserProfile(QueryUtil.findBoByAttribute(
                        UserProfile.class, "userProfileName", bo
                        .getGuestUserProfile().getUserProfileName(),
                        destDomain.getId()));
            }
            if (bo.getConfigtempleMdm() != null) {
            	bo.setConfigtempleMdm(QueryUtil.findBoByAttribute(
                        ConfigTemplateMdm.class, "policyname", bo
                        .getConfigtempleMdm().getPolicyname(),
                destDomain.getId()));
               
            }

            Set<UserProfile> authOkUserProfiles = new HashSet<UserProfile>();
            for (UserProfile tempClass : bo.getAuthOkUserProfile()) {
                UserProfile newTempClass = QueryUtil.findBoByAttribute(
                        UserProfile.class, "userProfileName",
                        tempClass.getUserProfileName(), destDomain.getId());
                authOkUserProfiles.add(newTempClass);
            }
            bo.setAuthOkUserProfile(authOkUserProfiles);
            
            Set<UserProfile> authOkDataUserProfiles = new HashSet<UserProfile>();
            for (UserProfile tempClass : bo.getAuthOkDataUserProfile()) {
                UserProfile newTempClass = QueryUtil.findBoByAttribute(
                        UserProfile.class, "userProfileName",
                        tempClass.getUserProfileName(), destDomain.getId());
                authOkDataUserProfiles.add(newTempClass);
            }
            bo.setAuthOkDataUserProfile(authOkDataUserProfiles);

            Set<UserProfile> authFailUserProfiles = new HashSet<UserProfile>();
            for (UserProfile tempClass : bo.getAuthOkUserProfile()) {
                UserProfile newTempClass = QueryUtil.findBoByAttribute(
                        UserProfile.class, "userProfileName",
                        tempClass.getUserProfileName(), destDomain.getId());
                authFailUserProfiles.add(newTempClass);
            }
            bo.setAuthFailUserProfile(authFailUserProfiles);

            /*--RADIUS attribute mapping, assign user profile, Jianliang Chen, 2012-04-01--*/
            Set<LocalUserGroup> cloneRadiusUserGroups = new HashSet<LocalUserGroup>();

            for (LocalUserGroup userGroup : bo.getRadiusUserGroups()) {
                LocalUserGroup newUserGroup = QueryUtil.findBoByAttribute(
                        LocalUserGroup.class, "groupName",
                        userGroup.getGroupName(), destDomain.getId());
                cloneRadiusUserGroups.add(newUserGroup);
            }

            bo.setRadiusUserGroups(cloneRadiusUserGroups);

            if (bo.getCwp() != null) {
                bo.setCwp(QueryUtil.findBoByAttribute(Cwp.class, "cwpName", bo
                        .getCwp().getCwpName(), destDomain.getId()));
            }

            if (bo.getRadiusAssignment() != null) {
                bo.setRadiusAssignment(QueryUtil.findBoByAttribute(
                        RadiusAssignment.class, "radiusName", bo
                                .getRadiusAssignment().getRadiusName(),
                        destDomain.getId()));
            }

            if (bo.getServiceFilter() != null) {
                bo.setServiceFilter(QueryUtil.findBoByAttribute(
                        ServiceFilter.class, "filterName", bo
                                .getServiceFilter().getFilterName(), destDomain
                                .getId()));
            }

            if (bo.getNativeVlan() != null) {
                bo.setNativeVlan(QueryUtil.findBoByAttribute(Vlan.class,
                        "vlanName", bo.getNativeVlan().getVlanName(),
                        destDomain.getId()));
            }
            
            if (bo.getVoiceVlan() != null) {
                bo.setVoiceVlan(QueryUtil.findBoByAttribute(Vlan.class,
                        "vlanName", bo.getNativeVlan().getVlanName(),
                        destDomain.getId()));
            }
            if (bo.getDataVlan() != null) {
                bo.setDataVlan(QueryUtil.findBoByAttribute(Vlan.class,
                        "vlanName", bo.getNativeVlan().getVlanName(),
                        destDomain.getId()));
            }

            newBos.add(bo);
        }
        if (!newBos.isEmpty()) {
            QueryUtil.bulkCreateBos(newBos);
        }
    }
    
    public void updatePortTemplateProfileItem(HmDomain srcDomain, HmDomain destDomain)
    throws Exception {
	if (null == srcDomain || null == destDomain)
	    return;
	    
	    List<PortGroupProfile> uplist = getAlltheObjectsInDomain(
	            PortGroupProfile.class, destDomain);
	    List<HmBo> upnewBos = new ArrayList<HmBo>();
	    PortGroupProfile upbo;
	    for (PortGroupProfile template : uplist) {
	    	upbo = QueryUtil.findBoById(PortGroupProfile.class,
	                template.getId(), this);
	    	
	        List<SingleTableItem> lst=upbo.getItems();
	        List<SingleTableItem> newlst = new ArrayList<SingleTableItem>();
	        for (SingleTableItem item : lst) {
	        	SingleTableItem newitem =  item.clone();
	        	
	        	//configTemplateId reset
	        	ConfigTemplate configTemplate = QueryUtil.findBoById(ConfigTemplate.class, item.getConfigTemplateId());
	        	if(configTemplate != null){
	        		ConfigTemplate configTemplatenew = QueryUtil.findBoByAttribute(ConfigTemplate.class, "configname", configTemplate.getConfigName(), destDomain.getId());
	        		if(configTemplatenew != null){
	        			newitem.setConfigTemplateId(configTemplatenew.getId());
	        		}
	        	}
	        	
	        	//noDefaultId reset
	        	PortGroupProfile profile = QueryUtil.findBoById(PortGroupProfile.class, item.getNonGlobalId());
	        	if(profile != null){
	        		PortGroupProfile profilenew = QueryUtil.findBoByAttribute(PortGroupProfile.class, "name", profile.getName(), destDomain.getId());
	        		if(profilenew != null){
	        			newitem.setNonGlobalId(profilenew.getId());
	        		}
	        	}
	        	
	        	newlst.add(newitem);
	        }
	        upbo.setItems(newlst);
	        upnewBos.add(upbo);
	    }
	    if (!upnewBos.isEmpty()) {
	        QueryUtil.bulkUpdateBos(upnewBos);
	    }   
	}
    
    public void clonePortTemplateProfile(HmDomain srcDomain, HmDomain destDomain)
            throws Exception {
        if (null == srcDomain || null == destDomain)
            return;
        List<PortGroupProfile> list = getAlltheObjectsInDomain(
                PortGroupProfile.class, srcDomain);
        List<HmBo> newBos = new ArrayList<HmBo>();
        PortGroupProfile bo;
        for (PortGroupProfile template : list) {
            bo = QueryUtil.findBoById(PortGroupProfile.class,
                    template.getId(), this);
            bo.setId(null);
            bo.setOwner(destDomain);
            bo.setVersion(null);
            //================================
            List<SingleTableItem> lst=bo.getItems();
            List<SingleTableItem> newlst = new ArrayList<SingleTableItem>();
            for (SingleTableItem item : lst) {
            	newlst.add(item);
            }
            bo.setItems(newlst);
            //================================
            if (!(null == bo.getBasicProfiles() || bo.getBasicProfiles().isEmpty())) {
                List<PortBasicProfile> tempBasics = new ArrayList<>();
                for (PortBasicProfile basic : bo.getBasicProfiles()) {
                    if (null != basic.getAccessProfile()) {
                        final PortAccessProfile access = QueryUtil
                                .findBoByAttribute(PortAccessProfile.class,
                                        "name", basic.getAccessProfile().getName(),
                                        destDomain.getId());
                        
                        basic.setAccessProfile(access);
                        tempBasics.add(basic);
                    }
                }
                bo.setBasicProfiles(tempBasics);
            }
            
            if (!(null == bo.getPortPseProfiles() || bo.getPortPseProfiles().isEmpty())) {
            	List<PortPseProfile> tempPseProfiles = new ArrayList<PortPseProfile>();
                for (PortPseProfile pse : bo.getPortPseProfiles()) {
                    if (null != pse.getPseProfile()) {
                        final PseProfile pseProfile = QueryUtil
                                .findBoByAttribute(PseProfile.class,
                                        "name", pse.getPseProfile().getName(),
                                        destDomain.getId());
                        
                        pse.setPseProfile(pseProfile);
                        tempPseProfiles.add(pse);
                    }
                }
                bo.setPortPseProfiles(tempPseProfiles);
            }

            newBos.add(bo);
        }
        if (!newBos.isEmpty()) {
            QueryUtil.bulkCreateBos(newBos);
        }
    }

	/**
	 * Get all the objects of this class bo in this domain.
	 * 
	 * @param arg_Class -
	 * @param srcDomain -
	 * @return -
	 */
	private <T extends HmBo> List<T> getAlltheObjectsInDomain(Class<T> arg_Class,
			HmDomain srcDomain) {		
		return QueryUtil
				.executeQuery(arg_Class,
						new SortParams("id"), new FilterParams(
								"owner.domainName", srcDomain.getDomainName()));
	}

	private <T extends HmBo> List<T> getAlltheObjectsInDomain(Class<T> arg_Class,
			HmDomain srcDomain, QueryBo queryBo) {
		return QueryUtil
				.executeQuery(arg_Class,
						new SortParams("id"), new FilterParams(
								"owner.domainName", srcDomain.getDomainName()), null, queryBo);
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (null == bo) {
			return null;
		}
		if (bo instanceof MapContainerNode) {
			MapContainerNode node = (MapContainerNode) bo;
			loadMapHierarchy(node);
//		} else if (bo instanceof HiveAp) {
//			HiveAp hiveAp = (HiveAp) bo;
//			if (null != hiveAp.getL3Neighbors()) {
//				hiveAp.getL3Neighbors().size();
//			}
		} else if (bo instanceof ConfigTemplate) {
			ConfigTemplate configTemplate = (ConfigTemplate) bo;
			if (configTemplate.getHiveProfile() != null)
				configTemplate.getHiveProfile().getId();
			if (configTemplate.getMgmtServiceDns() != null)
				configTemplate.getMgmtServiceDns().getId();
			if (configTemplate.getMgmtServiceSyslog() != null)
				configTemplate.getMgmtServiceSyslog().getId();
			if (configTemplate.getMgmtServiceSnmp() != null)
				configTemplate.getMgmtServiceSnmp().getId();
			if (configTemplate.getMgmtServiceTime() != null)
				configTemplate.getMgmtServiceTime().getId();
			if (configTemplate.getMgmtServiceOption() != null)
				configTemplate.getMgmtServiceOption().getId();
			if (configTemplate.getIdsPolicy() != null)
				configTemplate.getIdsPolicy().getId();
			 if (configTemplate.getLldpCdp() != null)
				 configTemplate.getLldpCdp().getId();
			 if (configTemplate.getRadiusProxyProfile() != null)
				 configTemplate.getRadiusProxyProfile().getId();
			 if (configTemplate.getRadiusServerProfile() != null)
				 configTemplate.getRadiusServerProfile().getId();
			if (configTemplate.getVlan() != null)
				configTemplate.getVlan().getId();
			if (configTemplate.getVlanNative() != null)
				configTemplate.getVlanNative().getId();
			if (configTemplate.getIpFilter() != null)
				configTemplate.getIpFilter().getId();
			if (configTemplate.getAccessConsole() != null)
				configTemplate.getAccessConsole().getId();
			if (configTemplate.getEth0ServiceFilter() != null)
				configTemplate.getEth0ServiceFilter().getId();
			if (configTemplate.getWireServiceFilter() != null)
				configTemplate.getWireServiceFilter().getId();
			if (configTemplate.getEth0BackServiceFilter() != null)
				configTemplate.getEth0BackServiceFilter().getId();
			if (configTemplate.getEth1BackServiceFilter() != null)
				configTemplate.getEth1BackServiceFilter().getId();
			if (configTemplate.getRed0BackServiceFilter() != null)
				configTemplate.getRed0BackServiceFilter().getId();
			if (configTemplate.getAgg0BackServiceFilter() != null)
				configTemplate.getAgg0BackServiceFilter().getId();
			if (configTemplate.getAlgConfiguration() != null)
				configTemplate.getAlgConfiguration().getId();
			if (configTemplate.getLocationServer() != null)
				configTemplate.getLocationServer().getId();
			if (configTemplate.getClientWatch() != null)
				configTemplate.getClientWatch().getId();
//			if (configTemplate.getEthernetAccess() != null)
//				configTemplate.getEthernetAccess().getId();
//			if (configTemplate.getEthernetBridge() != null)
//				configTemplate.getEthernetBridge().getId();
			if (configTemplate.getClassifierMap() != null)
				configTemplate.getClassifierMap().getId();
			if (configTemplate.getMarkerMap() != null)
				configTemplate.getMarkerMap().getId();
//			if (configTemplate.getMgtNetwork() != null)
//				configTemplate.getMgtNetwork().getId();

			/*
			 * Fiona add for dual port
			 */
			if (configTemplate.getEth1ServiceFilter() != null)
				configTemplate.getEth1ServiceFilter().getId();
//			if (configTemplate.getEthernetAccessEth1() != null)
//				configTemplate.getEthernetAccessEth1().getId();
//			if (configTemplate.getEthernetBridgeEth1() != null)
//				configTemplate.getEthernetBridgeEth1().getId();

			if (configTemplate.getAgg0ServiceFilter() != null)
				configTemplate.getAgg0ServiceFilter().getId();
//			if (configTemplate.getEthernetAccessAgg() != null)
//				configTemplate.getEthernetAccessAgg().getId();
//			if (configTemplate.getEthernetBridgeAgg() != null)
//				configTemplate.getEthernetBridgeAgg().getId();

			if (configTemplate.getRed0ServiceFilter() != null)
				configTemplate.getRed0ServiceFilter().getId();
//			if (configTemplate.getEthernetAccessRed() != null)
//				configTemplate.getEthernetAccessRed().getId();
//			if (configTemplate.getEthernetBridgeRed() != null)
//				configTemplate.getEthernetBridgeRed().getId();
			if (configTemplate.getVpnService() != null)
				configTemplate.getVpnService().getId();
			if (configTemplate.getRoutingProfilePolicy() != null)
				configTemplate.getRoutingProfilePolicy().getId();
			if (configTemplate.getFwPolicy() != null)
				configTemplate.getFwPolicy().getId();
			if(configTemplate.getBonjourGw() != null){
				configTemplate.getBonjourGw().getId();
			}
//			if (configTemplate.getRouterIpTrack() != null)
//				configTemplate.getRouterIpTrack().getId();
			if (configTemplate.getRoutingPolicy() != null)
				configTemplate.getRoutingPolicy().getId();
			if(configTemplate.getRoutingProfilePolicy()!=null)
				configTemplate.getRoutingProfilePolicy();
			if (configTemplate.getRadiusAttrs() != null)
				configTemplate.getRadiusAttrs().getId();
			if (configTemplate.getAppProfile() != null)
				configTemplate.getAppProfile().getId();

			// configTemplate.getSsidUserProfiles().values();
			configTemplate.getSsidInterfaces().values();
			configTemplate.getIpTracks().size();
			configTemplate.getTvNetworkService().size();
			configTemplate.getPortProfiles().size();
			configTemplate.getStormControlList().size();
			
			configTemplate.getVlanNetwork().size();
			for(ConfigTemplateVlanNetwork cvn: configTemplate.getVlanNetwork()){
				if (cvn.getNetworkObj()!=null) {
					cvn.getNetworkObj().getId();
				}
				if (cvn.getVlan()!=null) {
					cvn.getVlan().getId();
				}
			}
			if (configTemplate.getUpVlanMapping() != null) {
				configTemplate.getUpVlanMapping().size();
				for (UserProfileVlanMapping mapping : configTemplate.getUpVlanMapping()) {
					if (mapping.getUserProfile() != null) {
						mapping.getUserProfile().getId();
					}
					if (mapping.getVlan() != null) {
						mapping.getVlan().getId();
					}
				}
			}
			
			if (configTemplate.getSwitchSettings() != null) {
				configTemplate.getSwitchSettings().getId();
				if (configTemplate.getSwitchSettings().getStpSettings() != null) {
					if (configTemplate.getSwitchSettings().getStpSettings()
							.getMstpRegion() != null) {
						configTemplate.getSwitchSettings().getStpSettings()
								.getMstpRegion().getId();
						configTemplate.getSwitchSettings().getStpSettings()
								.getMstpRegion().getMstpRegionPriorityList()
								.size();
					}
				}
			}
		} else if (bo instanceof UserProfile) {
			UserProfile profile = (UserProfile) bo;

			if (null != profile.getUserProfileAttribute())
				profile.getUserProfileAttribute().getId();

			if (null != profile.getVlan())
				profile.getVlan().getId();

			if (null != profile.getQosRateControl())
				profile.getQosRateControl().getId();

			if (null != profile.getTunnelSetting())
				profile.getTunnelSetting().getId();

			if (null != profile.getIpPolicyFrom())
				profile.getIpPolicyFrom().getId();

			if (null != profile.getIpPolicyTo())
				profile.getIpPolicyTo().getId();

			if (null != profile.getMacPolicyFrom())
				profile.getMacPolicyFrom().getId();

			if (null != profile.getMacPolicyTo())
				profile.getMacPolicyTo().getId();
			if (null != profile.getAsRuleGroup()) {
				profile.getAsRuleGroup().getId();
			}
			if (null != profile.getUserProfileSchedulers()) {
				profile.getUserProfileSchedulers().size();
			}
			if (null != profile.getAssignRules()) {
				profile.getAssignRules().size();
			}
//			if (null != profile.getNetworkObj()) {
//				profile.getNetworkObj().getId();
//			}
			if(null != profile.getMarkerMap()){
				profile.getMarkerMap().getId();
			}
			
			for (Scheduler scheduler : profile.getUserProfileSchedulers())
				scheduler.getId();
		} else if (bo instanceof UserProfileAttribute) {
			UserProfileAttribute profile = (UserProfileAttribute) bo;
			if (profile.getItems()!=null) {
				profile.getItems().size();
			}
			
		} else if (bo instanceof QosClassification) {
			QosClassification profile = (QosClassification) bo;
			profile.getCustomServices().values();
			profile.getNetworkServices().values();
			profile.getQosMacOuis().values();
			profile.getQosSsids().values();
		} else if (bo instanceof IdsPolicy) {
			IdsPolicy ids = (IdsPolicy) bo;
			if (null != ids.getIdsSsids()) {
				ids.getIdsSsids().size();
			}
			if (null != ids.getVlans()) {
				ids.getVlans().size();
			}
			if (null != ids.getMacOrOuis()) {
				ids.getMacOrOuis().size();
			}
		} else if (bo instanceof EthernetAccess) {
			EthernetAccess profile = (EthernetAccess) bo;
			if (null != profile.getUserProfile())
				profile.getUserProfile().getId();
			if (null != profile.getMacAddress())
				profile.getMacAddress().size();
		} else if (bo instanceof HiveProfile) {
			HiveProfile profile = (HiveProfile) bo;
			if (null != profile.getMacFilters())
				profile.getMacFilters().size();
		} else if (bo instanceof SsidProfile) {
			SsidProfile profile = (SsidProfile) bo;
			if (profile.getMacFilters() != null) {
				profile.getMacFilters().size();
			}
			if (profile.getSchedulers() != null) {
				profile.getSchedulers().size();
			}
			if (profile.getRadiusUserProfile() != null) {
				profile.getRadiusUserProfile().size();
			}
			if (profile.getLocalUserGroups() != null) {
				profile.getLocalUserGroups().size();
			}
			if (profile.getRadiusUserGroups() != null) {
				profile.getRadiusUserGroups().size();
			}
			if (profile.getUserProfileGuest() != null) {
			    profile.getUserProfileGuest().getId();
			}
			if (profile.getGRateSets()!=null){
				profile.getGRateSets().values();
			}
			if (profile.getARateSets()!=null){
				profile.getARateSets().values();
			}
			if (profile.getNRateSets()!=null){
				profile.getNRateSets().values();
			}
			if (profile.getAcRateSets()!=null && profile.getAcRateSets().size() > 0){
				profile.getAcRateSets().size();
			}
		} else if (bo instanceof MacFilter) {
			MacFilter profile = (MacFilter) bo;
			if (profile.getFilterInfo() != null) {
				profile.getFilterInfo().size();
			}
		} else if (bo instanceof VpnNetwork) {
			VpnNetwork profile = (VpnNetwork) bo;
//			if (profile.getVlan() != null) {
//				profile.getVlan().getId();
//			}
			if (profile.getVpnDnsService() != null) {
				profile.getVpnDnsService().getId();
			}
			if (profile.getCustomOptions() != null) {
				profile.getCustomOptions().size();
			}
			if (profile.getSubItems() != null) {
				profile.getSubItems().size();
				if(profile.getSubItems().size() > 0){
		    		for(VpnNetworkSub subnet : profile.getSubItems()){
		    			if(subnet.isOverrideDNSService()){
		    				if(subnet.getDnsService() != null){
		    					subnet.getDnsService().getId();
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
			}
			if (profile.getSubNetwokClass() != null) {
				profile.getSubNetwokClass().size();
			}
			if (profile.getReserveClass() != null) {
				profile.getReserveClass().size();
			}
			if (profile.getSubNetworkRes() != null){
				profile.getSubNetworkRes().size();
			}
			if (profile.getSubnetworkDHCPCustoms() != null){
				profile.getSubnetworkDHCPCustoms().size();
			}
			if (profile.getPortForwardings() != null){
				profile.getPortForwardings().size();
			}
		} else if (bo instanceof IpFilter) {
			IpFilter profile = (IpFilter) bo;
			if (profile.getIpAddress() != null) {
				profile.getIpAddress().size();
			}
		} else if (bo instanceof IpPolicy) {
			IpPolicy profile = (IpPolicy) bo;
			if (profile.getRules() != null) {
				profile.getRules().size();
			}
		} else if (bo instanceof MacPolicy) {
			MacPolicy profile = (MacPolicy) bo;
			if (profile.getRules() != null) {
				profile.getRules().size();
			}
		} else if (bo instanceof DosPrevention) {
			DosPrevention profile = (DosPrevention) bo;
			if (profile.getDosParamsMap() != null) {
				profile.getDosParamsMap().size();
			}
		} else if (bo instanceof RadiusOnHiveap) {
			RadiusOnHiveap profile = (RadiusOnHiveap) bo;
			if (profile.getLdapOuUserProfiles() != null) {
				profile.getLdapOuUserProfiles().size();
			}
			if (profile.getLocalUserGroup() != null) {
				profile.getLocalUserGroup().size();
			}
			if (profile.getDirectoryOrLdap() != null) {
				profile.getDirectoryOrLdap().size();
			}
			if (profile.getIpOrNames() != null) {
				profile.getIpOrNames().size();
			}
		} else if (bo instanceof AccessConsole) {
			AccessConsole profile = (AccessConsole) bo;
			if (profile.getMacFilters() != null) {
				profile.getMacFilters().size();
			}
		} else if (bo instanceof RadiusUserProfileRule) {
			RadiusUserProfileRule profile = (RadiusUserProfileRule) bo;
			if (profile.getPermittedUserProfiles() != null) {
				profile.getPermittedUserProfiles().size();
			}
		} else if (bo instanceof VlanDhcpServer) {
			VlanDhcpServer profile = (VlanDhcpServer) bo;
			if (profile.getIpPools() != null) {
				profile.getIpPools().size();
			}
			if (profile.getCustoms() != null) {
				profile.getCustoms().size();
			}
		} else if (bo instanceof TunnelSetting) {
			TunnelSetting profile = (TunnelSetting) bo;
			if (profile.getIpAddressList() != null) {
				profile.getIpAddressList().size();
			}
		} else if (bo instanceof RadiusAssignment) {
			RadiusAssignment profile = (RadiusAssignment) bo;
			if (profile.getServices() != null) {
				profile.getServices().size();
			}
		} else if (bo instanceof Cwp) {
			Cwp cwp = (Cwp) bo;
			CwpPageCustomization pageCustom = cwp.getPageCustomization();

			if (pageCustom.getFields() != null) {
				pageCustom.getFields().size();
			}

			if (cwp.getCertificate() != null) {
				cwp.getCertificate().getId();
			}

			if (cwp.getVlan() != null) {
				cwp.getVlan().getId();
			}
			
			if (cwp.getWalledGarden() != null) {
				cwp.getWalledGarden().size();
			}
		} else if (bo instanceof RadioProfile) {
			RadioProfile radio = (RadioProfile) bo;
			if (radio.getWmmItems() != null) {
				radio.getWmmItems().size();
			}
			if(radio.getSupressBprOUIs() != null){
				radio.getSupressBprOUIs().size();
			}
		} else if (bo instanceof VpnService) {
			VpnService vpn = (VpnService) bo;
			if (null != vpn.getVpnCredentials()) {
				vpn.getVpnCredentials().size();
			}
			if(null != vpn.getDomObj()){
				vpn.getDomObj().getId();
			}
			if(null != vpn.getUserProfileTrafficL3()){
				vpn.getUserProfileTrafficL3().size();
				for(UserProfileForTrafficL3 userProfileForTrafficL3:vpn.getUserProfileTrafficL3()){
					if(null != userProfileForTrafficL3.getUserProfile()){
						userProfileForTrafficL3.getUserProfile().getId();
					}
				}
			}
			if(null != vpn.getUserProfileTrafficL2()){
				vpn.getUserProfileTrafficL2().size();
				for (UserProfileForTrafficL2 userProfileForTrafficL2 : vpn
						.getUserProfileTrafficL2()) {
					if (null != userProfileForTrafficL2.getUserProfile()) {
						userProfileForTrafficL2.getUserProfile().getId();
					}
				}
			}
			if (null != vpn.getUserProfileTrafficL3() ) {
				vpn.getUserProfileTrafficL3().size();
				for(UserProfileForTrafficL3 userProfileForTraffic:vpn.getUserProfileTrafficL3()){
					if(userProfileForTraffic.getUserProfile() != null){
						userProfileForTraffic.getUserProfile().getId();
						// TODO for remove network object in user profile
//						if(userProfileForTraffic.getUserProfile().getNetworkObj() != null){
//							userProfileForTraffic.getUserProfile().getNetworkObj().getId();
//						}
					}
				}
			}
		} else if (bo instanceof RadiusProxy) {
			RadiusProxy radius = (RadiusProxy) bo;
			if (radius.getRadiusRealm() != null)
				radius.getRadiusRealm().size();
			if (radius.getRadiusNas() != null)
				radius.getRadiusNas().size();
		} else if (bo instanceof MgmtServiceDns) {
			MgmtServiceDns dns = (MgmtServiceDns)bo;
			
			if(dns.getDnsInfo() != null) {
				dns.getDnsInfo().size();
			}
		} else if (bo instanceof MgmtServiceSyslog) {
			MgmtServiceSyslog syslog = (MgmtServiceSyslog)bo;
			
			if(syslog.getSyslogInfo() != null) {
				syslog.getSyslogInfo().size();
			}
		} else if (bo instanceof MgmtServiceSnmp) {
			MgmtServiceSnmp snmp = (MgmtServiceSnmp)bo;
			
			if(snmp.getSnmpInfo() != null) {
				snmp.getSnmpInfo().size();
			}
		} else if (bo instanceof MgmtServiceTime) {
			MgmtServiceTime time = (MgmtServiceTime)bo;
			
			if(time.getTimeInfo() != null) {
				time.getTimeInfo().size();
			}
		} else if (bo instanceof HiveApAutoProvision) {
			HiveApAutoProvision autoProvision = (HiveApAutoProvision) bo;

			if (autoProvision.getMacAddresses() != null) {
				autoProvision.getMacAddresses().size();
			}
		} else if (bo instanceof RadiusLibrarySip) {
			RadiusLibrarySip sip = (RadiusLibrarySip) bo;

			if (sip.getRules() != null) {
				sip.getRules().size();
			}
		} else if (bo instanceof IdpSettings) {
			IdpSettings idp = (IdpSettings)bo;
			if(idp.getEnclosedRogueAps() != null)
				idp.getEnclosedRogueAps().size();
			if(idp.getEnclosedFriendlyAps() != null)
				idp.getEnclosedFriendlyAps().size();
		} else if (bo instanceof AlgConfiguration) {
			AlgConfiguration alg = (AlgConfiguration)bo;
			if(alg.getItems() != null)
				alg.getItems().size();
		} else if (bo instanceof PrintTemplate) {
			PrintTemplate template = (PrintTemplate)bo;
			
			if(template.getFields() != null) {
				template.getFields().size();
			}
		} else if(bo instanceof QosRateControl) {
			QosRateControl qosRate = (QosRateControl)bo;
			
			if(qosRate.getQosRateLimit() != null) {
				qosRate.getQosRateLimit().size();
			}
		}else if (bo instanceof LocationClientWatch) {
            LocationClientWatch locationClientWatch = (LocationClientWatch) bo;
            if (null != locationClientWatch.getItems())
                locationClientWatch.getItems().size();
        }else if(bo instanceof IpAddress){
            IpAddress ipAddress= (IpAddress) bo;
            if (null != ipAddress.getItems())
                ipAddress.getItems().size();
        }else if(bo instanceof MacOrOui){
        	MacOrOui macOrOui = (MacOrOui)bo;
        	if (null != macOrOui.getItems())
        		macOrOui.getItems().size();
        }else if(bo instanceof Vlan){
        	Vlan vlan = (Vlan) bo;
        	if (null != vlan.getItems())
        		vlan.getItems().size();
        }else if(bo instanceof ActiveDirectoryOrOpenLdap){
        	ActiveDirectoryOrOpenLdap activeDirectoryOrOpenLdap = (ActiveDirectoryOrOpenLdap) bo;
        	if (null != activeDirectoryOrOpenLdap.getAdDomains())
        		activeDirectoryOrOpenLdap.getAdDomains().size();
        }else if(bo instanceof OsObject){
        	OsObject osObj = (OsObject) bo;
        	if (null != osObj.getItems())
        		osObj.getItems().size();
        }else if(bo instanceof DevicePolicy){
        	DevicePolicy policy = (DevicePolicy) bo;
        	if (null != policy.getRules())
        		policy.getRules().size();
        }else if(bo instanceof DomainObject){
        	DomainObject domObj = (DomainObject) bo;
        	if (null != domObj.getItems())
        		domObj.getItems().size();
		} else if (bo instanceof DnsServiceProfile) {
			DnsServiceProfile dnsServiceProfile = (DnsServiceProfile) bo;
			if (null != dnsServiceProfile.getDomainObj()
					&& null != dnsServiceProfile.getDomainObj().getItems()) {
				dnsServiceProfile.getDomainObj().getItems().size();
			}
			if (null != dnsServiceProfile.getSpecificInfos()) {
				dnsServiceProfile.getSpecificInfos().size();
				for (DnsSpecificSettings dSpecificSettings : dnsServiceProfile.getSpecificInfos()) {
					if (null != dSpecificSettings.getDnsServer()
							&& null != dSpecificSettings.getDnsServer().getItems()) {
						dSpecificSettings.getDnsServer().getItems().size();
					}
				}
			}
			IpAddress tmpInternalDns1 = dnsServiceProfile.getInternalDns1();
			if (null != tmpInternalDns1 && null != tmpInternalDns1.getItems()) {
				tmpInternalDns1.getItems().size();
			}
			IpAddress tmpInternalDns2 = dnsServiceProfile.getInternalDns2();
			if (null != tmpInternalDns2 && null != tmpInternalDns2.getItems()) {
				tmpInternalDns2.getItems().size();
			}
			IpAddress tmpInternalDns3 = dnsServiceProfile.getInternalDns3();
			if (null != tmpInternalDns3 && null != tmpInternalDns3.getItems()) {
				tmpInternalDns3.getItems().size();
			}
			IpAddress tmpExternalDns1 = dnsServiceProfile.getExternalDns1();
			if (null != tmpExternalDns1 && null != tmpExternalDns1.getItems()) {
				tmpExternalDns1.getItems().size();
			}
			IpAddress tmpExternalDns2 = dnsServiceProfile.getExternalDns2();
			if (null != tmpExternalDns2 && null != tmpExternalDns2.getItems()) {
				tmpExternalDns2.getItems().size();
			}
			IpAddress tmpExternalDns3 = dnsServiceProfile.getExternalDns3();
			if (null != tmpExternalDns3 && null != tmpExternalDns3.getItems()) {
				tmpExternalDns3.getItems().size();
			}
		} else if (bo instanceof FirewallPolicy) {
			FirewallPolicy profile = (FirewallPolicy) bo;
			if (profile.getRules() != null) {
				profile.getRules().size();
			}
		} else if (bo instanceof PortAccessProfile) {
		    PortAccessProfile accPofile = (PortAccessProfile) bo;
			if (null != accPofile.getDefUserProfile()) {
				accPofile.getDefUserProfile().getId();
			}
			if (null != accPofile.getVoiceVlan()) {
			    accPofile.getVoiceVlan().getId();
			}
			if (null != accPofile.getDataVlan()) {
			    accPofile.getDataVlan().getId();
			}
			if (null != accPofile.getSelfRegUserProfile()) {
				accPofile.getSelfRegUserProfile().getId();
			}
			if (null != accPofile.getGuestUserProfile()) {
			    accPofile.getGuestUserProfile().getId();
			}
			if (null != accPofile.getAuthOkUserProfile()) {
				accPofile.getAuthOkUserProfile().size();
			}
			if (null != accPofile.getAuthOkDataUserProfile()) {
			    accPofile.getAuthOkDataUserProfile().size();
			}
			if (null != accPofile.getAuthFailUserProfile()) {
			    accPofile.getAuthFailUserProfile().size();
			}
			if (null != accPofile.getCwp()) {
				accPofile.getCwp().getId();
			}
			if (null != accPofile.getServiceFilter()) {
				accPofile.getServiceFilter().getId();
			}
			if (null != accPofile.getRadiusAssignment()) {
				accPofile.getRadiusAssignment().getId();
			}
			if (null != accPofile.getNativeVlan()) {
			    accPofile.getNativeVlan().getId();
			}
			if (null != accPofile.getRadiusUserGroups()) {
				accPofile.getRadiusUserGroups().size();
			}
		} else if (bo instanceof PortGroupProfile) {
		    PortGroupProfile group = (PortGroupProfile)bo;
		    if(null != group.getBasicProfiles()) {
		        group.getBasicProfiles().size();
		        for (PortBasicProfile basic : group.getBasicProfiles()) {
                    if(null != basic.getAccessProfile()) {
                        basic.getAccessProfile().getId();
                    }
                }
            }
            if(null != group.getPortPseProfiles()){
            	group.getPortPseProfiles().size();
            	for (PortPseProfile portPse : group.getPortPseProfiles()) {
                    if(null != portPse.getPseProfile()) {
                    	portPse.getPseProfile().getId();
                    }
                }
            }
        } else if(bo instanceof RadiusAttrs){
			RadiusAttrs radiusAttrs= (RadiusAttrs) bo;
            if (null != radiusAttrs.getItems())
            	radiusAttrs.getItems().size();
        } else if(bo instanceof RoutingProfile){
        	RoutingProfile routingProfile = (RoutingProfile)bo;
        	if(routingProfile.getItems() != null){
        		routingProfile.getItems().size();
        	}
        } else if(bo instanceof BonjourGatewaySettings){
        	BonjourGatewaySettings bonjourGatewaySettings = (BonjourGatewaySettings)bo;
        	if(null != bonjourGatewaySettings){
        		if(null != bonjourGatewaySettings.getBonjourActiveServices()){
        			bonjourGatewaySettings.getBonjourActiveServices().size();
        		}
        		if(null !=bonjourGatewaySettings.getRules()){
        			bonjourGatewaySettings.getRules().size();
        		}
        	}
        	
        } else if(bo instanceof RoutingProfilePolicy){
        	RoutingProfilePolicy routingprofilepolicy = (RoutingProfilePolicy)bo;
        	if(routingprofilepolicy.getRoutingProfilePolicyRuleList()!=null)
        	{
        		routingprofilepolicy.getRoutingProfilePolicyRuleList().size();
        	}
        }  else if (bo instanceof RoutingPolicy) {
        	RoutingPolicy routingPolicy = (RoutingPolicy)bo;
        	if (routingPolicy.getDomainObjectForDesList() != null) {
        		routingPolicy.getDomainObjectForDesList().getId();
        	}
        
        	if (routingPolicy.getIpTrackForCheck() != null) {
        		routingPolicy.getIpTrackForCheck().getId();
        	}
        	if (routingPolicy.getRoutingPolicyRuleList() != null) {
        		routingPolicy.getRoutingPolicyRuleList().size();
        		for (RoutingPolicyRule rule : routingPolicy.getRoutingPolicyRuleList()) {
        			if (rule.getIpTrackReachablePri() != null) {
        				rule.getIpTrackReachablePri().getId();
        			}
        			if (rule.getIpTrackReachableSec() != null) {
        				rule.getIpTrackReachableSec().getId();
        			}
        			if (rule.getSourceUserProfile() != null) {
        				rule.getSourceUserProfile().getId();
        			}
        		}
        	}
        }else if (bo instanceof SwitchSettings) {
			SwitchSettings switchSettings = (SwitchSettings) bo;
			if (switchSettings != null) {
				switchSettings.getId();
				if (switchSettings.getStpSettings() != null) {
					if (switchSettings.getStpSettings().getMstpRegion() != null) {
						switchSettings.getStpSettings().getMstpRegion().getId();
						switchSettings.getStpSettings().getMstpRegion().getMstpRegionPriorityList().size();
					}
				}
			}
		}else if (bo instanceof StpSettings) {
			StpSettings stpSettings = (StpSettings) bo;
			if (stpSettings != null) {
				if (stpSettings.getMstpRegion() != null) {
					stpSettings.getMstpRegion().getId();
					stpSettings.getMstpRegion().getMstpRegionPriorityList().size();
				}
			}
		}else if (bo instanceof MstpRegion) {
			MstpRegion mstpRegion = (MstpRegion) bo;
			if (mstpRegion != null) {
				mstpRegion.getId();
				mstpRegion.getMstpRegionPriorityList().size();
			}
		} else if (bo instanceof ApplicationProfile) {
			ApplicationProfile profile = (ApplicationProfile) bo;
			if (profile != null) {
				profile.getId();
				profile.getApplicationList().size();
				profile.getCustomApplicationList().size();
			}
		} else if(bo instanceof CustomApplication){
			CustomApplication customApp = (CustomApplication) bo;
			if (customApp.getRules() != null) {
				customApp.getRules().size();
			}
		} else if(bo instanceof OpenDNSDevice){
			OpenDNSDevice device = (OpenDNSDevice)bo;
			if(device.getOpenDNSAccount() != null){
				device.getOpenDNSAccount().getId();
			}
		} else if(bo instanceof OpenDNSMapping){
			OpenDNSMapping mapping = (OpenDNSMapping)bo;
			if(mapping.getOpenDNSAccount() != null){
				mapping.getOpenDNSAccount().getId();
			}
			
			if(mapping.getOpenDNSDevice() != null){
				mapping.getOpenDNSDevice().getId();
			}
			
			if(mapping.getUserProfile() != null){
				mapping.getUserProfile().getId();
			}
		} else if(bo instanceof HMServicesSettings){
			HMServicesSettings settings = (HMServicesSettings) bo;
			if(settings.getOpenDNSAccount() != null){
				settings.getOpenDNSAccount().getId();
			}
		}

		return null;
	}

	private void loadMapHierarchy(MapContainerNode node) {
		for (MapNode mapNode : node.getChildNodes()) {
			if (!mapNode.isLeafNode()) {
				loadMapHierarchy((MapContainerNode) mapNode);
			}
		}
	}

	public void removeMapContainer(Long domainId) throws Exception {
		if (null == domainId) {
			return;
		}
		long start = System.currentTimeMillis();
		Long worldMapId = BoMgmt.getMapMgmt().getWorldMapId(domainId);
		if (null == worldMapId) {
			return;
		}
		MapContainerNode worldMap = QueryUtil.findBoById(MapContainerNode.class,
				worldMapId, this);
		if (null == worldMap) {
			return;
		}
		removeMapHierarchy(worldMap);
		long end = System.currentTimeMillis();
		log.debug("removeMapContainer", "remove Map Container for  # domain:" + domainId + " cost "
				+ (end - start) + "ms.");
	}

	private void removeMapHierarchy(MapContainerNode mapContainer)
			throws Exception {
		if (null != mapContainer.getChildNodes()) {
			for (MapNode node : mapContainer.getChildNodes()) {
				if (!node.isLeafNode()) {
					removeMapHierarchy((MapContainerNode) node);
				}
			}
		}
		MapContainerNode removeMap = QueryUtil.findBoById(MapContainerNode.class, mapContainer
				.getId());
		Collection<Long> removeMapId = new ArrayList<Long>(1);
		removeMapId.add(mapContainer.getId());
		QueryUtil.removeBos(MapNode.class, removeMapId);
		BoMgmt.getBoEventMgmt().publishBoEvent(
				new BoEvent<MapContainerNode>(removeMap, BoEventType.REMOVED));
	}

	public void removeHiveAp(Long domainId, boolean resetDeviceDefault) {
		if (null == domainId) {
			return;
		}
		try {
			long start = System.currentTimeMillis();
			List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, null, null,
					domainId);
			List<Long> ids = new ArrayList<Long>(hiveAps.size());
			List<HiveAp> newAndManaged = new ArrayList<HiveAp>(hiveAps.size());
			for (HiveAp ap : hiveAps) {
				Long id = ap.getId();
				HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class, id,
						this);
				// Don't need update first, the neighbor bo has be removed, use
				// mac address instead
				// if (null != hiveAp && null != hiveAp.getL3Neighbors()) {
				// hiveAp.setL3Neighbors(null);
				// BoMgmt.getMapMgmt().updateHiveAp(hiveAp);
				// }
				// add to id list;
				ids.add(id);
				// add to hiveAp list;
				if (null != hiveAp
						&& (HiveAp.STATUS_MANAGED == hiveAp.getManageStatus() 
						|| HiveAp.STATUS_NEW == hiveAp.getManageStatus()
						|| HiveAp.STATUS_PRECONFIG == hiveAp.getManageStatus())) {
					newAndManaged.add(hiveAp);
				}
			}
			BoMgmt.getMapMgmt().removeHiveAps(ids, resetDeviceDefault);
			BeTopoModuleUtil.sendBeDeleteAPConnectRequest(newAndManaged, true);
			long end = System.currentTimeMillis();
			log.debug("removeHiveAp", "remove HiveAp for  # domain:" + domainId
					+ " cost " + (end - start) + "ms.");
		} catch (Exception e) {
			log.error("removeHiveAp", "remove HiveAp error.", e);
		}
	}

	/**
	 * Clone all the mstp region object from srcDomain to destDomain.
	 * 
	 * @param srcDomain -
	 * @param destDomain -
	 * @throws Exception -
	 */
	public void cloneMstpRegion(HmDomain srcDomain, HmDomain destDomain)
			throws Exception {
		if (null == srcDomain || null == destDomain) {
			return;
		}
		List<MstpRegion> list = getAlltheObjectsInDomain(MstpRegion.class, srcDomain, this);
		List<HmBo> newBos = new ArrayList<HmBo>();
		for (MstpRegion bo : list) {
			bo.setId(null);
			bo.setOwner(destDomain);
			bo.setVersion(null);
			List<MstpRegionPriority> items = new ArrayList<MstpRegionPriority>();
			items.addAll(bo.getMstpRegionPriorityList());
			bo.setMstpRegionPriorityList(items);
			newBos.add(bo);
		}
		if (!newBos.isEmpty()) {
			QueryUtil.bulkCreateBos(newBos);
		}
		BeOsInfoProcessor.getInstance().resetOsName(destDomain.getId());
	}
}
