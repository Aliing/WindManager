package com.ah.be.admin.restoredb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.ConfigTemplateVlanNetwork;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.lan.LanProfile;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.TunnelSetting;
import com.ah.bo.network.Application;
import com.ah.bo.network.ApplicationProfile;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.network.DevicePolicy;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.MstpRegion;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.OsVersion;
import com.ah.bo.network.StpSettings;
import com.ah.bo.network.SwitchSettings;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.RadiusLibrarySip;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.wlan.EthernetAccess;
import com.ah.bo.wlan.SsidProfile;

public class AhRestoreNewMapTools {
	
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapMacFilter				= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapMacPolicy				= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapIpPolicy					= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapIDSPolicy				= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapQosClassification		= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapQosClassificationAndMark	= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapQosRateControlAndQueu	= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapHives					= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapIdentityBasedTunnel		= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapRadiusServerAssign		= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapRadiusServerOnHiveAP		= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapDirectoryOrLdap			= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapSsid						= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapDns						= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapSyslog					= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapSnmp						= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapTimeAndDate				= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapOption					= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapConfigTemplate			= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapIpAdddress				= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapCapWebPortal				= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapCwpCertificate			= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapDosPrevention			= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapVlan						= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapRadiusAttrs				= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapLayer3Romaing			= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapSchedule					= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapRadioProfile				= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapMacAddress				= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapNetworkService			= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapUserAttribute			= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapMgtIpFilter				= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapUserProfile				= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapRadiusUserProfileRule	= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapMgtServiceFilter			= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapMarking					= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapLocalUserGroup			= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapLocalUser				= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapMacAuth				    = new HashMap<Long, Long>();
	//Long:old radius id; Set<Long>:old user group id
	private static Map<Long, Set<Long>> mapOldLocalUserGroup	    = new HashMap<Long, Set<Long>>();
	//Long:old radius id; Set<Long>:new user group id
	private static Map<Long, Set<LocalUserGroup>> mapOldLocalUser	= new HashMap<Long, Set<LocalUserGroup>>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapHiveAP					= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapMapContainer				= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapUserGroup				= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapUser						= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapAlgConfiguration			= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapLocationServer			= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapEthernetAccess			= new HashMap<Long, Long>();
	//Long:old id; old object
	private static Map<Long, EthernetAccess> mapEthernetAccessBo	= new HashMap<Long, EthernetAccess>();
	//Long:old id; Long:new id
	//private static Map<Long, Long[]>	mapUserProfileQos_old		= new HashMap<Long, Long[]>();
	//Long:old id; Long:new id
	private static Map<String, Short>		mapAlarm					= new HashMap<String, Short>();
	
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapMACAddressChangeToOUI	= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapMgmtIpTracking			= new HashMap<Long, Long>();
	
	private static Map<Long, Long>		mapRoutingPolicy			= new HashMap<Long, Long>();
	
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapAccessConsole			= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapVlanDhcpServer			= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	//private static Map<Long,Cwp>			mapSsidCwpMapping 			= new HashMap<Long,Cwp>();
	//Long:old id; Long:new id
	private static Map<String,SsidProfile>	mapSsidTemplateIdSsidProfile = new HashMap<String,SsidProfile>();
	//Long:old id; Long:new id
	private static Map<Long, Long>	    lldpcdpProfileMap           = new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapVpnService				= new HashMap<Long, Long>();

	//Long: old LAN profile id; Long: new Access profile id;
	private static Map<Long, Long>		mapLanProfile				= new HashMap<Long, Long>();
	//Long: new Access profile id; old LAN profile id;
	private static Map<Long, Long>		mapLanProfileReverse		= new HashMap<Long, Long>();
	private static Map<Long, LanProfile>   mapLanProfileObj               = new HashMap<Long, LanProfile>();
	
	private static Map<Long, Long>		mapFirewallPolicy			= new HashMap<Long, Long>();
	
	private static Map<Long, Long>		mapPPPoE			= new HashMap<Long, Long>();
	
	private static Map<Long, Long>		mapPseProfile			= new HashMap<Long, Long>();
	
	private static Map<Long, Long>		mapBonjourGatewayCategory			= new HashMap<Long, Long>();
	
	private static Map<Long, Long>		mapBonjourService			= new HashMap<Long, Long>();
	
	private static Map<Long, Long>		mapBonjourGatewaySetting			= new HashMap<Long, Long>();
	
	private static Map<Long, Long> 		mapOneTimePassword          = new HashMap<Long, Long>();
	
	private static Map<Long, Long> 		mapHiveApAutoProvision          = new HashMap<Long, Long>();
	
	private static Map<Long, Long> 		mapVlanGroup          = new HashMap<Long, Long>();

	private static Map<Long, Long> 		mapWifiClientPreferredSsid          = new HashMap<Long, Long>();

	private static Map<Long, Long>		mapForwardingDBMap			= new HashMap<Long, Long>();
	
	private static Map<Long, Long>		mapConfigTemplateMDM		= new HashMap<Long, Long>();
	
	private static Map<Long, Long> 		mapOpenDNSDevice          = new HashMap<Long, Long>();
	
	private static Map<Long, Long> 		mapOpenDNSAccount         = new HashMap<Long, Long>();
	
	/*
	 * for Tunnel Policy
	 */
	//String:old id; TunnelSetting:old object
	private static Map<String, TunnelSetting> tunnelSettingOldInfo 	= new HashMap<String, TunnelSetting>();
	//String:user profile id; String:tunnel id
	private static Map<String, String>		mapOldUserProfileTunnel	= new HashMap<String, String>();
	
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapLocationClientWatch		= new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapAirscreenRuleGroup   = new HashMap<Long, Long>();
	//Long:old id; Long:new id
	private static Map<Long, Map<String,EthernetAccess>> mayEthernetAccessResotre = new HashMap<Long, Map<String,EthernetAccess>>();
	//Long:old id; Long:new id
	//private static Map<Long,EthernetAccess> mapEthernetAccessBo = new HashMap<Long,EthernetAccess>();
	
	// Wired port template: oldId-newId
	private static Map<Long, Long>     mapPortTemplateMap               = new HashMap<Long, Long>();
	// Wired port template: oldId-newId
	private static Map<Long, Long>     mapPortAccessMap               = new HashMap<Long, Long>();
		
	/*
	 * for Teacher view
	 */
	private static Map<Long, Long> mapTvComputerCart = new HashMap<Long, Long>();
	private static Map<Long, Long> mapTvClass = new HashMap<Long, Long>();
	private static Map<Long, Long> mapTvStudentRoster = new HashMap<Long, Long>();
	private static Map<Long, Long> mapTvResourceMap = new HashMap<Long, Long>();
	
	// for dashboard
	private static Map<Long, Long> mapAhDashboard = new HashMap<Long, Long>();
	private static Map<Long, Long> mapAhDashboardLayout = new HashMap<Long, Long>();
	private static Map<Long, Long> mapAhDashboardWidget = new HashMap<Long, Long>();
	private static Map<Long, Long> mapAhDashboardComponent = new HashMap<Long, Long>();
	private static Map<Long, Long> mapAhDashboardMetric = new HashMap<Long, Long>();
	
	// LAN VLAN and Network mapping (New ID) :: for LAN profile Untagged/Tagged networks
	private static Map<Long, Map<Long, Long>> mapLAN4VLANObjectNetwork = new HashMap<>();
	// network object and vlan mapping (OLD ID)
	private static Map<Long, Long> mapNetworkObjectVlan = new HashMap<Long, Long>();
	// network object and vlan mapping (New ID)
	private static Map<Long, Long> mapNetworkObjectVlanWithNewID = null;
	// user profile and network object mapping (OLD ID)
	private static Map<Long, Long> mapUserProfileNetworkObject = new HashMap<Long, Long>();
	// user profile and network object mapping (New ID)
	private static Map<Long, Long> mapUserProfileNetworkObjectWithNewID = null;
	// network policy mgtnetwork mapping (new ID)
	private static Map<Long, ConfigTemplateVlanNetwork> mapWlanNetworkObjectVlan = new HashMap<Long, ConfigTemplateVlanNetwork>();
	
	/**
	 * domain name - owner user id
	 */
	private static Map<String, Long>								domainNameVADIDMap				= new HashMap<String, Long>();
	
	//Long:old id; Long:new id
	private static Map<Long, Long>		domainMap					= new HashMap<Long, Long>();
	
	//Long:old id; hmdomain
	public static Map<Long, HmDomain> hmDomainMap                  = new HashMap<Long, HmDomain>();
	
	/*
	 * For Radius Proxy (old id, new id)
	 */
	private static Map<Long, Long> mapRadiusProxy = new HashMap<Long, Long>();
	
	/*
	 * For Radius Library SIP (old id, new id)
	 */
	private static Map<Long, Long> mapRadiusLibrarySip = new HashMap<Long, Long>();
	
	/*
	 * For OS Object (old id, new id)
	 */
	private static Map<Long, Long> mapOsObject = new HashMap<Long, Long>();
	
	/*
	 * For Application Object(old id, new id)
	 */
	private static Map<Long, Long> mapApplication = new HashMap<Long, Long>();
	
	/*
	 * For ApplicationProfile Object(old id, new id) 
	 */
	private static Map<Long, Long> mapAppProfile = new HashMap<Long, Long>();
	/*
	 * For OS Version (old id, new id)
	 */
	private static Map<Long, Long> mapOsVersion = new HashMap<Long, Long>();
	
	/*
	 * For Domain Object (old id, new id)
	 */
	private static Map<Long, Long> mapDomainObject = new HashMap<Long, Long>();
	
	/*
	 * For Device Policy Detection (old id, new id)
	 */
	private static Map<Long, Long> mapDeviceGroupPolicy = new HashMap<Long, Long>();
	
	//Long:old id; Long:new id
	private static Map<Long, Long> mapDNSSerivces = new HashMap<Long, Long>();
	
	/*
	 * For RoutingProfile (old id, new id)
	 */
	private static Map<Long, Long> mapRoutingProfile = new HashMap<Long, Long>();
	
	/*
	 * VPN Network (old id, new id)
	 */
	private static Map<Long, Long> mapVpnNetwork = new HashMap<Long, Long>();
	/*
	 * VPN Network (old id, new id)
	 */
	private static Map<Long, VpnNetwork> mapVpnNetworkBo = new HashMap<Long, VpnNetwork>();
	
	/*
	 * SubNetworkResource (old id, new id)
	 */
	private static Map<Long, Long> mapSubNetworkResource = new HashMap<Long, Long>();
	
	/*
	 * map for ppsk server of ssid profile and hiveAp
	 */
	private static Map<Long, Set<Long>> mapPpskServerHiveAp = new HashMap<Long, Set<Long>>();
	
	/*
	 * Storm Control (old id, new id)
	 */
	private static Map<Long, Long> mapStormControl = new HashMap<Long, Long>();
	
	/*
	 * used to hold previous default configtemplate, use old id as key, use new id as value.
	 * you can use it to find out those configtemplate who is default in previous version
	 */
	public static Map<Long, Long> mapDefaultConfigTemplates = new HashMap<Long, Long>();
	
	/*
	 * Multicast group (old id, new id)
	 */
	private static Map<Long, Long> mapMulticastGroup = new HashMap<Long, Long>();
	
	/*
	 * For Switch Settings Object(old id, new id) 
	 */
	private static Map<Long, Long> mapSwitchSettings = new HashMap<Long, Long>();
	
	/*
	 * For Mstp Region Object(old id, new id) 
	 */
	private static Map<Long, Long> mapMstpRegions = new HashMap<Long, Long>();
	
	/*
	 * For Stp Settings Object(old id, new id) 
	 */
	private static Map<Long, Long> mapStpSettings = new HashMap<Long, Long>();
	
	//Long:old id; Long:new id
	private static Map<Long, Long>		mapCustomApplication				= new HashMap<Long, Long>();
	
	/*
	 * NetworkDeviceHistory (old id, new id)
	 */
	private static Map<Long, Long> mapNetworkDeviceHistory = new HashMap<Long, Long>();
	
	private static Map<Long, Long> mapCLIBlob = new HashMap<Long,Long>();
	
	/*
	 * for fix bug 32249, store all emails belongs to VHM which is under restoring
	 */
	private static Set<String> vhmEmails = new HashSet<String>();
	
	public static boolean isSingleVhmRestore() {
		// if there is only one VHM in file hm_domain, return true. otherwise return false;
		return (hmDomainMap.keySet()).size() == 1;
	}
	
	public static Set<String> getVhmEmails() {
		return vhmEmails;
	}
	
	public static void clearVhmEmails() {
		vhmEmails.clear();
	}

	public static void addVhmEmails(List<String> emails) {
		if (emails == null || emails.isEmpty()) {
			return;
		}
		vhmEmails.addAll(emails);
	}

	public static void addVhmEmails(String email) {
		if (StringUtils.isEmpty(email)) {
			return;
		}
		vhmEmails.add(email);
	}

	public static void setMapDefaultConfigTemplates(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}
		mapDefaultConfigTemplates.put(strKey, strValue);
	}

	public static Long getDefaultConfigTemplateId(Long strKey) {
		if (strKey == null || mapDefaultConfigTemplates == null)
		{
			return null;
		}
		return mapDefaultConfigTemplates.get(strKey);
	}
	
	public static Map<Long, Set<Long>> getMapPpskServerHiveAp() {
		return mapPpskServerHiveAp;
	}
	
	public static Set<Long> getPpskServersOfHiveAp(Long strKey) {
		if (strKey == null || mapPpskServerHiveAp == null)
		{
			return null;
		}
		return mapPpskServerHiveAp.get(strKey);
	}

	public static void setMapPpskServerHiveAp(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}
		if (mapPpskServerHiveAp.get(strKey) != null) {
			mapPpskServerHiveAp.get(strKey).add(strValue);
		} else {
			Set<Long> values = new HashSet<Long>();
			values.add(strValue);
			mapPpskServerHiveAp.put(strKey, values);
		}
	}

	public static Long getMapVpnNetwork(Long strKey)
	{
		if (strKey == null || mapVpnNetwork == null)
		{
			return null;
		}
		return mapVpnNetwork.get(strKey);
	}

	public static void setMapVpnNetwork(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapVpnNetwork.put(strKey, strValue);
	}

    public static VpnNetwork getMapVpnNetworkBo(Long key) {
        if (key == null || mapVpnNetworkBo == null) {
            return null;
        }
        return mapVpnNetworkBo.get(key);
    }
	
    public static void setMapVpnNetworkBo(Long key, VpnNetwork network) {
        if (null == key || null == network) {
            return;
        }

        mapVpnNetworkBo.put(key, network);
    }
	
	public static Long getMapSubNetworkResource(Long strKey)
	{
		if (strKey == null || mapSubNetworkResource == null)
		{
			return null;
		}
		return mapSubNetworkResource.get(strKey);
	}

	public static void setMapSubNetworkResource(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapSubNetworkResource.put(strKey, strValue);
	}

	public static Map<Long, HmDomain> getHmDomainMap()
	{
		return hmDomainMap;
	}
	
	public static HmDomain getonlyDomain()
	{
		if(null == hmDomainMap || hmDomainMap.isEmpty())
		{
			return null;
		}
		
		HmDomain oReturn = null;

		Iterator<Map.Entry<Long, HmDomain>> it = hmDomainMap.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<Long, HmDomain> entry = it.next();
			oReturn = entry.getValue();
			break;
 		}
		
		return oReturn;
	}
	
	public static void setHmDomain(Long lID, HmDomain hmDomain)
	{
		if(null == lID || null == hmDomain)
		{
			return ;
		}
		
		hmDomainMap.put(lID, hmDomain);
	}
	
	public static HmDomain getHmDomain(Long lID)
	{
		return hmDomainMap.get(lID);
	}
	
	public static Short getAlarmSeverity(String key)
	{
		if (key == null || mapAlarm == null)
		{
			return -1;
		}
		return mapAlarm.get(key);
	}

	public static void setAlarmSeverity(String key, Short value)
	{
		if (key != null)
		{
			mapAlarm.put(key, value);
		}
	}

//	public static Long[] getMapUserProfileQos_old(Long key)
//	{
//		if (key == null)
//			return null;
//		if (mapUserProfileQos_old == null)
//			return null;
//		return mapUserProfileQos_old.get(key);
//	}

//	public static void setMapUserProfileQos_old(Long key, Long[] value)
//	{
//		if (mapUserProfileQos_old == null)
//			return;
//		mapUserProfileQos_old.put(key, value);
//	}

	public static void setMapMacFilter(Long lKey, Long lValue)
	{
		if (null == lKey || null == lValue)
		{
			return;
		}

		mapMacFilter.put(lKey, lValue);
	}

	public static Long getMapMacFilter(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapMacFilter.get(strKey);
	}

	public static void setMapMacPolicy(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapMacPolicy.put(strKey, strValue);
	}

	public static Long getMapMacPolicy(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapMacPolicy.get(strKey);
	}

	public static void setMapIpPolicy(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapIpPolicy.put(strKey, strValue);
	}

	public static Long getMapIpPolicy(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapIpPolicy.get(strKey);
	}

	public static void setMapIDSPolicy(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapIDSPolicy.put(strKey, strValue);
	}

	public static Long getMapIDSPolicy(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapIDSPolicy.get(strKey);
	}

	public static void setMapQosClassification(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapQosClassification.put(strKey, strValue);
	}

	public static Long getMapQosClassification(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapQosClassification.get(strKey);
	}

	public static void setMapQosClassificationAndMark(
			Long strKey,
			Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapQosClassificationAndMark.put(strKey, strValue);
	}

	public static Long getMapQosClassificationAndMark(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapQosClassificationAndMark.get(strKey);
	}

	public static void setMapQosRateControlAndQueun(
			Long strKey,
			Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapQosRateControlAndQueu.put(strKey, strValue);
	}

	public static Long getMapQosRateControlAndQueu(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapQosRateControlAndQueu.get(strKey);
	}

	public static void setMapHives(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapHives.put(strKey, strValue);
	}

	public static Long getMapHives(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapHives.get(strKey);
	}

	public static void setMapIdentityBasedTunnel(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapIdentityBasedTunnel.put(strKey, strValue);
	}

	public static Long getMapIdentityBasedTunnel(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapIdentityBasedTunnel.get(strKey);
	}

	public static void setMapRadiusServerAssign(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapRadiusServerAssign.put(strKey, strValue);
	}

	public static Long getMapRadiusServerAssign(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapRadiusServerAssign.get(strKey);
	}

	public static void setMapRadiusServerOnHiveAP(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapRadiusServerOnHiveAP.put(strKey, strValue);
	}

	public static Long getMapRadiusServerOnHiveAP(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapRadiusServerOnHiveAP.get(strKey);
	}

	public static void setMapSsid(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapSsid.put(strKey, strValue);
	}

	public static Long getMapSsid(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapSsid.get(strKey);
	}

	public static void setMapDns(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapDns.put(strKey, strValue);
	}

	public static Long getMapDns(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapDns.get(strKey);
	}

	public static void setMapSyslog(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapSyslog.put(strKey, strValue);
	}

	public static Long getMapSyslog(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapSyslog.get(strKey);
	}

	public static void setMapSnmp(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapSnmp.put(strKey, strValue);
	}

	public static Long getMapSnmp(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapSnmp.get(strKey);
	}

	public static void setMapTimeAndDate(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapTimeAndDate.put(strKey, strValue);
	}

	public static Long getMapTimeAndDate(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapTimeAndDate.get(strKey);
	}

	public static void setMapConfigTemplate(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapConfigTemplate.put(strKey, strValue);
	}

	public static Long getMapConfigTemplate(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapConfigTemplate.get(strKey);
	}

	public static void setMapIpAdddress(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapIpAdddress.put(strKey, strValue);
	}

	public static Long getMapIpAdddress(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapIpAdddress.get(strKey);
	}

	public static void setMapCapWebPortal(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapCapWebPortal.put(strKey, strValue);
	}

	public static Long getMapCapWebPortal(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapCapWebPortal.get(strKey);
	}

	public static void setMapDosPrevention(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapDosPrevention.put(strKey, strValue);
	}

	public static Long getMapCwpCertificate(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapCwpCertificate.get(strKey);
	}

	public static void setMapCwpCertificate(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapCwpCertificate.put(strKey, strValue);
	}

	public static Long getMapDosPrevention(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapDosPrevention.get(strKey);
	}

	public static void setMapVlan(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapVlan.put(strKey, strValue);
	}

	public static Long getMapVlan(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapVlan.get(strKey);
	}
	
	public static void setMapRadiusAttrs(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapRadiusAttrs.put(strKey, strValue);
	}

	public static Long getMapRadiusAttrs(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapRadiusAttrs.get(strKey);
	}

	public static void setMapLayer3Romaing(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapLayer3Romaing.put(strKey, strValue);
	}

	public static Long getMapLayer3Romaing(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapLayer3Romaing.get(strKey);
	}

	public static void setMapSchedule(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapSchedule.put(strKey, strValue);
	}

	public static Long getMapSchedule(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapSchedule.get(strKey);
	}

	public static void setMapRadioProfile(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapRadioProfile.put(strKey, strValue);
	}

	public static Long getMapRadioProfile(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapRadioProfile.get(strKey);
	}

	public static void setMapMacAddress(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapMacAddress.put(strKey, strValue);
	}

	public static Long getMapMacAddress(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapMacAddress.get(strKey);
	}

	public static void setMapNetworkService(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapNetworkService.put(strKey, strValue);
	}

	public static Long getMapNetworkService(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapNetworkService.get(strKey);
	}

	public static void setMapUserAttribute(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapUserAttribute.put(strKey, strValue);
	}

	public static Long getMapUserAttribute(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapUserAttribute.get(strKey);
	}

	public static void setMapMgtIpFilter(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapMgtIpFilter.put(strKey, strValue);
	}

	public static Long getMapMgtIpFilter(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapMgtIpFilter.get(strKey);
	}

	public static void setMapUserProfile(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapUserProfile.put(strKey, strValue);
	}

	public static Long getMapUserProfile(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapUserProfile.get(strKey);
	}
	
	public static Long getOldUserProfileID(Long newId) {
		Set<Map.Entry<Long, Long>> entrySet = mapUserProfile.entrySet();
		for (Map.Entry<Long, Long> entry : entrySet) {
			if (entry.getValue().equals(newId))
				return entry.getKey();
		}
		
		return null;
	}

	public static void setMapRadiusUserProfileRule(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return ;
		}

		mapRadiusUserProfileRule.put(strKey, strValue);
	}

	public static Long getMapRadiusUserProfileRule(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapRadiusUserProfileRule.get(strKey);
	}

	public static void setMapMgtServiceFilter(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapMgtServiceFilter.put(strKey, strValue);
	}

	public static Long getMapMgtServiceFilter(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapMgtServiceFilter.get(strKey);
	}
	
	public static void setMapAhDashboard(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapAhDashboard.put(strKey, strValue);
	}

	public static Long getMapAhDashboard(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapAhDashboard.get(strKey);
	}
	
	public static void setMapAhDashboardLayout(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapAhDashboardLayout.put(strKey, strValue);
	}

	public static Long getMapAhDashboardLayout(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapAhDashboardLayout.get(strKey);
	}
	
	public static void setMapAhDashboardWidget(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapAhDashboardWidget.put(strKey, strValue);
	}

	public static Long getMapAhDashboardWidget(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapAhDashboardWidget.get(strKey);
	}
	
	public static void setMapAhDashboardComponent(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapAhDashboardComponent.put(strKey, strValue);
	}

	public static Long getMapAhDashboardComponent(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapAhDashboardComponent.get(strKey);
	}
	
	public static void setMapAhDashboardMetric(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapAhDashboardMetric.put(strKey, strValue);
	}

	public static Long getMapAhDashboardMetric(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapAhDashboardMetric.get(strKey);
	}
	
	public static void setMapMarking(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapMarking.put(strKey, strValue);
	}

	public static Long getMapMarking(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapMarking.get(strKey);
	}

	public static void setMapOption(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapOption.put(strKey, strValue);
	}

	public static Long getMapOption(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapOption.get(strKey);
	}

	public static void setMapUser(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapUser.put(strKey, strValue);
	}

	public static Long getMapUser(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapUser.get(strKey);
	}
	
	public static Long getOwnerUserIDFromDomainName(String domainName)
	{
		return domainNameVADIDMap.get(domainName);
	}
	
	public static void setDomainNameVADID(String domainName, Long userID)
	{
		domainNameVADIDMap.put(domainName, userID);
	}

	public static void setMapUserGroup(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapUserGroup.put(strKey, strValue);
	}

	public static Long getMapUserGroup(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapUserGroup.get(strKey);
	}

	public static void setMapDomain(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		domainMap.put(strKey, strValue);
	}

	public static Long getMapDomain(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return domainMap.get(strKey);
	}

	private static HmDomain globalDomain;

	public static HmDomain getGlobalDomain() throws Exception
	{
		if (globalDomain == null)
		{
			FilterParams filterParams = new FilterParams("domainName",
				HmDomain.GLOBAL_DOMAIN);
			List<HmDomain> domainList = QueryUtil.executeQuery(HmDomain.class,
				null, filterParams);
			if (domainList.isEmpty())
			{
				globalDomain = new HmDomain();
				globalDomain.setDomainName(HmDomain.GLOBAL_DOMAIN);
				QueryUtil.createBo(globalDomain);
			}
			else
			{
				globalDomain = domainList.get(0);
			}
		}

		return globalDomain;
	}

	private static HmDomain homeDomain;

	public static HmDomain getHomeDomain() throws Exception
	{
		if (homeDomain == null)
		{
			homeDomain = QueryUtil.findBoByAttribute(HmDomain.class,
				"domainName", HmDomain.HOME_DOMAIN);
		}

		return homeDomain;
	}

	public static void setMapLocalUserGroup(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapLocalUserGroup.put(strKey, strValue);
	}

	public static Long getMapMacAuth(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapMacAuth.get(strKey);
	}
	
	public static void setMapMacAuth(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapMacAuth.put(strKey, strValue);
	}

	public static Long getMapLocalUserGroup(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapLocalUserGroup.get(strKey);
	}

	public static void setMapLocalUser(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapLocalUser.put(strKey, strValue);
	}

	public static Long getMapLocalUser(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapLocalUser.get(strKey);
	}

	public static void setMapOldLocalUserGroup(Long strKey, Set<Long> lsValue)
	{
		if (null == strKey || null == lsValue)
		{
			return;
		}

		mapOldLocalUserGroup.put(strKey, lsValue);
	}

	public static Set<Long> getMapOldLocalUserGroup(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapOldLocalUserGroup.get(strKey);
	}
	
	public static void setMapOldLocalUser(Long strKey, Set<LocalUserGroup> lsValue)
	{
		if (null == strKey || null == lsValue)
		{
			return;
		}

		mapOldLocalUser.put(strKey, lsValue);
	}

	public static Set<LocalUserGroup> getMapOldLocalUser(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapOldLocalUser.get(strKey);
	}

	public static void setMapHiveAP(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapHiveAP.put(strKey, strValue);
	}

	public static Long getMapHiveAP(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapHiveAP.get(strKey);
	}

	public static void setMapMapContainer(Long strId, Long strName)
	{
		if (null == strId || null == strName)
		{
			return;
		}
		mapMapContainer.put(strId, strName);
	}

	public static Long getMapMapContainer(Long strId)
	{
		if (null == strId)
		{
			return null;
		}
		return mapMapContainer.get(strId);
	}

	public static void setMapAlgConfiguration(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapAlgConfiguration.put(strKey, strValue);
	}

	public static Long getMapAlgConfiguration(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapAlgConfiguration.get(strKey);
	}

	public static void setMapLocationServer(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapLocationServer.put(strKey, strValue);
	}

	public static Long getMapLocationServer(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapLocationServer.get(strKey);
	}

	public static void setMapEthernetAccess(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapEthernetAccess.put(strKey, strValue);
	}

	public static Long getMapEthernetAccess(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapEthernetAccess.get(strKey);
	}

	public static Long getMapDirectoryOrLdap(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapDirectoryOrLdap.get(strKey);
	}

	public static void setMapDirectoryOrLdap(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapDirectoryOrLdap.put(strKey, strValue);
	}

	/**
	 * Add this for MAP Policy rule remove mask
	 * @param strKey : mac address name
	 * @return new mac oui name
	 */
	public static Long getMapMACAddressChangeToOUI(Long strKey) {
		if (null == strKey)
		{
			return null;
		}
		return mapMACAddressChangeToOUI.get(strKey);
	}

	/**
	 * Set the values to map
	 * @param strKey : mac address name
	 * @param strValue : mac oui name
	 */
	public static void setMapMACAddressChangeToOUI(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}
		mapMACAddressChangeToOUI.put(strKey, strValue);
	}

	public static void setMapMgmtIpTracking(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapMgmtIpTracking.put(strKey, strValue);
	}

	public static Long getMapMgmtIpTracking(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapMgmtIpTracking.get(strKey);
	}
	
	public static void setMapRoutingPolicy(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapRoutingPolicy.put(strKey, strValue);
	}

	public static Long getMapRoutingPolicy(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapRoutingPolicy.get(strKey);
	}

	public static void setMapAccessConsole(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapAccessConsole.put(strKey, strValue);
	}

	public static Long getMapAccessConsole(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapAccessConsole.get(strKey);
	}
	
	public static Long getMapPPPoE(Long strKey) {
		if (null == strKey)
		{
			return null;
		}

		return mapPPPoE.get(strKey);
	}

	public static void setMapPPPoE(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapPPPoE.put(strKey, strValue);
	}
	
	public static Long getMapPseProfile(Long strKey) {
		if (null == strKey)
		{
			return null;
		}

		return mapPseProfile.get(strKey);
	}

	public static void setMapPseProfile(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapPseProfile.put(strKey, strValue);
	}
	
	public static Long getMapBonjourGatewayCategory(Long strKey) {
		if (null == strKey)
		{
			return null;
		}

		return mapBonjourGatewayCategory.get(strKey);
	}

	public static void setMapBonjourGatewayCategory(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapBonjourGatewayCategory.put(strKey, strValue);
	}
	
	public static Long getMapBonjourService(Long strKey) {
		if (null == strKey)
		{
			return null;
		}

		return mapBonjourService.get(strKey);
	}

	public static void setMapBonjourService(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapBonjourService.put(strKey, strValue);
	}
	
	public static Long getMapVlanGroup(Long strKey) {
		if (null == strKey)
		{
			return null;
		}

		return mapVlanGroup.get(strKey);
	}

	public static void setMapVlanGroup(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapVlanGroup.put(strKey, strValue);
	}

	public static Long getMapBonjourGatewaySetting(Long strKey) {
		if (null == strKey)
		{
			return null;
		}

		return mapBonjourGatewaySetting.get(strKey);
	}

	public static void setMapBonjourGatewaySetting(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapBonjourGatewaySetting.put(strKey, strValue);
	}
	
	public static void setLLDPCDPProfileMap(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		lldpcdpProfileMap.put(strKey, strValue);
	}

	public static Long getLLDPCDPProfileMap(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return lldpcdpProfileMap.get(strKey);
	}

	public static Long getMapVlanDhcpServer(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}
		return mapVlanDhcpServer.get(strKey);
	}

	public static void setMapVlanDhcpServer(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}
		mapVlanDhcpServer.put(strKey, strValue);
	}
//
//	public static Cwp getMapSsidCwpMapping(Long key)
//	{
//		if (key == null || mapSsidCwpMapping == null)
//		{
//			return null;
//		}
//		return mapSsidCwpMapping.get(key);
//	}

//	public static void setMapSsidCwpMapping(Long key, Cwp value)
//	{
//		if (key != null)
//		{
//			mapSsidCwpMapping.put(key, value);
//		}
//	}
//
	public static SsidProfile getMapSsidTemplateIdSsidProfile(String key)
	{
		if (key == null || mapSsidTemplateIdSsidProfile == null)
		{
			return null;
		}
		return mapSsidTemplateIdSsidProfile.get(key);
	}

	public static void setMapSsidTemplateIdSsidProfile(String key, SsidProfile value)
	{
		if (key != null)
		{
			mapSsidTemplateIdSsidProfile.put(key, value);
		}
	}

	public static Long getMapVpnService(Long key) {
		if (key == null || mapVpnService == null)
		{
			return null;
		}
		return mapVpnService.get(key);
	}

	public static void setMapVpnService(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}
		mapVpnService.put(strKey, strValue);
	}
	
	public static Long getMapLanProfile(Long key) {
		if (key == null || mapLanProfile == null)
		{
			return null;
		}
		return mapLanProfile.get(key);
	}

	public static void setMapLanProfile(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}
		mapLanProfile.put(strKey, strValue);
	}
	
	public static Long getMapLanProfileReverse(Long key) {
	    if (key == null || mapLanProfileReverse == null)
	    {
	        return null;
	    }
	    return mapLanProfileReverse.get(key);
	}
	
	public static void setMapLanProfileReverse(Long strKey, Long strValue) {
	    if (null == strKey || null == strValue)
	    {
	        return;
	    }
	    mapLanProfileReverse.put(strKey, strValue);
	}
	
	public static LanProfile getMapLanProfileObj(Long key) {
	    if (key == null || mapLanProfileObj == null)
	    {
	        return null;
	    }
	    return mapLanProfileObj.get(key);
	}
	
	public static void setMapLanProfileObj(Long strKey, LanProfile lanProfile) {
	    if (null == strKey || null == lanProfile)
	    {
	        return;
	    }
	    mapLanProfileObj.put(strKey, lanProfile);
	}
	
	public static Long getMapFirewallPolicy(Long key) {
		if (key == null || mapFirewallPolicy == null)
		{
			return null;
		}
		return mapFirewallPolicy.get(key);
	}

	public static void setMapFirewallPolicy(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}
		mapFirewallPolicy.put(strKey, strValue);
	}

	public static TunnelSetting getTunnelSettingOldInfo(String strKey)
	{
		if (null == strKey)
		{
			return null;
		}
		return tunnelSettingOldInfo.get(strKey);
	}

	public static void setTunnelSettingOldInfo(String strKey, TunnelSetting tunnelInfo)
	{
		if (null == strKey || null == tunnelInfo)
		{
			return;
		}
		tunnelSettingOldInfo.put(strKey, tunnelInfo);
	}

	public static String[] getTunnelSettingResult(Set<String> strKeys)
	{
		String[] result = new String[2];
		if (null == strKeys) {
			return null;
		}
		StringBuffer success = new StringBuffer();
		StringBuffer fail = new StringBuffer();
		String tunnelName;
		for (String tunnelId : tunnelSettingOldInfo.keySet()) {
			tunnelName = tunnelSettingOldInfo.get(tunnelId).getTunnelName();
			if (strKeys.contains(tunnelId)) {
				success.append(tunnelName).append(" ");
			} else {
				fail.append(tunnelName).append(" ");
			}
		}
		result[0] = success.toString();
		result[1] = fail.toString();
		return result;
	}

	public static Set<String> getTunnelSettingNoUsed(Set<String> strKeys)
	{
		Set<String> result = new HashSet<String>();
		if (null == strKeys) {
			return null;
		}
		String tunnelName;
		for (String tunnelId : tunnelSettingOldInfo.keySet()) {
			if (!strKeys.contains(tunnelId)) {
				tunnelName = tunnelSettingOldInfo.get(tunnelId).getTunnelName();
				result.add(tunnelName);
			}
		}
		return result;
	}

	public static String getMapOldUserProfileTunnel(String strKey)
	{
		if (null == strKey)
		{
			return null;
		}
		return mapOldUserProfileTunnel.get(strKey);
	}

	public static void setMapOldUserProfileTunnel(String strKey, String strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}
		mapOldUserProfileTunnel.put(strKey, strValue);
	}
	public static Set<String> getUserProfileNamesByTunnel(String strValue)
	{
		if (null == strValue) {
			return null;
		}
		Set<String> result = new HashSet<String>();
		for (String userProName : mapOldUserProfileTunnel.keySet()) {
			if (strValue.equals(mapOldUserProfileTunnel.get(userProName))) {
				result.add(userProName);
			}
		}
		return result.size() > 0 ? result : null;
	}
	
	public static Long getMapLocationClientWatch(Long key) {
		if (key == null || mapLocationClientWatch == null)
		{
			return null;
		}
		return mapLocationClientWatch.get(key);
	}

	public static void setMapLocationClientWatch(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}
		mapLocationClientWatch.put(strKey, strValue);
	}
	
	public static Long getMapAirscreenRuleGroup(Long key) {
		if (key == null || mapAirscreenRuleGroup == null)
		{
			return null;
		}
		return mapAirscreenRuleGroup.get(key);
	}

	public static void setMapAirscreenRuleGroup(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}
		mapAirscreenRuleGroup.put(strKey, strValue);
	}
	
	public static Map<String,EthernetAccess> getMapEthernetAccessResotre(Long key) {
		if (key == null || mayEthernetAccessResotre == null)
		{
			return null;
		}
		return mayEthernetAccessResotre.get(key);
	}

	public static void setMapEthernetAccessResotre(Long strKey, Map<String,EthernetAccess> strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}
		mayEthernetAccessResotre.put(strKey, strValue);
	}

	public static EthernetAccess getMapEthernetAccessBo(Long key) {
		if (key == null || mapEthernetAccessBo == null)
		{
			return null;
		}
		return mapEthernetAccessBo.get(key);
	}

	public static void setMapEthernetAccessBo(Long strKey, EthernetAccess strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}
		mapEthernetAccessBo.put(strKey, strValue);
	}
	
	public static RadiusProxy getMapRadiusProxy(Long oldId)
	{
		if (null == oldId)
			return null;
		Long newId = mapRadiusProxy.get(oldId);
		if (null != newId) {
			return AhRestoreNewTools.CreateBoWithId(RadiusProxy.class, newId);
		}
		return null;
	}

	public static void setMapRadiusProxy(Long oldId, Long newId)
	{
		if (null == oldId || null == newId)
			return;
		mapRadiusProxy.put(oldId, newId);
	}
	
	public static RadiusLibrarySip getMapRadiusLibrarySip(Long oldId)
	{
		if (null == oldId)
			return null;
		Long newId = mapRadiusLibrarySip.get(oldId);
		if (null != newId) {
			return AhRestoreNewTools.CreateBoWithId(RadiusLibrarySip.class, newId);
		}
		return null;
	}

	public static void setMapRadiusLibrarySip(Long oldId, Long newId)
	{
		if (null == oldId || null == newId)
			return;
		mapRadiusLibrarySip.put(oldId, newId);
	}
	
	public static OsObject getMapOsObject(Long oldId)
	{
		if (null == oldId)
			return null;
		Long newId = mapOsObject.get(oldId);
		if (null != newId) {
			return AhRestoreNewTools.CreateBoWithId(OsObject.class, newId);
		}
		return null;
	}

	public static void setMapOsObject(Long oldId, Long newId)
	{
		if (null == oldId || null == newId)
			return;
		mapOsObject.put(oldId, newId);
	}
	
	public static Application getMapApplication(Long oldId) {
		if(null == oldId)
			return null;
		Long newId = mapApplication.get(oldId);
		if(null != newId)
			return AhRestoreNewTools.CreateBoWithId(Application.class, newId);
		return null;
	}
	
	public static void setMapApplication(Long oldId, Long newId) {
		if( null == oldId || null == newId )
			return;
		mapApplication.put(oldId, newId);
	}
	
	public static ApplicationProfile getMapAppProfile(Long oldId) {
		if(null == oldId)
			return null;
		Long newId = mapAppProfile.get(oldId);
		if(null != newId)
			return AhRestoreNewTools.CreateBoWithId(ApplicationProfile.class, newId);
		return null;
	}
	
	public static void setMapAppProfile(Long oldId, Long newId) {
		if( null == oldId || null == newId)
			return;
		mapAppProfile.put(oldId, newId);
	}
	
	public static OsVersion getMapOsVersion(Long oldId)
	{
		if (null == oldId)
			return null;
		Long newId = mapOsVersion.get(oldId);
		if (null != newId) {
			return AhRestoreNewTools.CreateBoWithId(OsVersion.class, newId);
		}
		return null;
	}

	public static void setMapOsVersion(Long oldId, Long newId)
	{
		if (null == oldId || null == newId)
			return;
		mapOsVersion.put(oldId, newId);
	}
	
	public static DomainObject getMapDomainObject(Long oldId)
	{
		if (null == oldId)
			return null;
		Long newId = mapDomainObject.get(oldId);
		if (null != newId) {
			return AhRestoreNewTools.CreateBoWithId(DomainObject.class, newId);
		}
		return null;
	}

	public static void setMapDomainObject(Long oldId, Long newId)
	{
		if (null == oldId || null == newId)
			return;
		mapDomainObject.put(oldId, newId);
	}
	
	public static DevicePolicy getMapDeviceGroupPolicy(Long oldId)
	{
		if (null == oldId)
			return null;
		Long newId = mapDeviceGroupPolicy.get(oldId);
		if (null != newId) {
			return AhRestoreNewTools.CreateBoWithId(DevicePolicy.class, newId);
		}
		return null;
	}

	public static void setMapDeviceGroupPolicy(Long oldId, Long newId)
	{
		if (null == oldId || null == newId)
			return;
		mapDeviceGroupPolicy.put(oldId, newId);
	}
	
	public static void setMapTvComputerCart(Long strKey, Long strValue){
		if (null == strKey || null == strValue){
			return;
		}
		mapTvComputerCart.put(strKey, strValue);
	}

	public static Long getMapTvComputerCart(Long strKey){
		if (null == strKey){
			return null;
		}
		return mapTvComputerCart.get(strKey);
	}
	
	public static void setMapTvClass(Long strKey, Long strValue){
		if (null == strKey || null == strValue){
			return;
		}
		mapTvClass.put(strKey, strValue);
	}

	public static Long getMapTvClass(Long strKey){
		if (null == strKey){
			return null;
		}
		return mapTvClass.get(strKey);
	}
	
	public static void setMapTvStudentRoster(Long strKey, Long strValue){
		if (null == strKey || null == strValue){
			return;
		}
		mapTvStudentRoster.put(strKey, strValue);
	}

	public static Long getMapTvStudentRoster(Long strKey){
		if (null == strKey){
			return null;
		}
		return mapTvStudentRoster.get(strKey);
	}
	
	public static void setMapTvResourceMap(Long strKey, Long strValue){
		if (null == strKey || null == strValue){
			return;
		}
		mapTvResourceMap.put(strKey, strValue);
	}

	public static Long getMapTvResourceMap(Long strKey){
		if (null == strKey){
			return null;
		}
		return mapTvResourceMap.get(strKey);
	}
	
	public static void setMapDNSServices(Long strKey, Long strValue) {
		if (null == strKey || null == strValue) {
			return;
		}

		mapDNSSerivces.put(strKey, strValue);
	}

	public static Long getMapDNSServices(Long strKey) {
		if (null == strKey) {
			return null;
		}

		return mapDNSSerivces.get(strKey);
	}
	
	public static Long getMapRoutingProfile(Long strKey) {
		if (null == strKey) {
			return null;
		}

		return mapRoutingProfile.get(strKey);
	}
	
	public static void setMapRoutingProfile(Long strKey, Long strValue) {
		if (null == strKey || null == strValue) {
			return;
		}

		mapRoutingProfile.put(strKey, strValue);
	}
	
	public static void cleanHmDomainCache()
	{
		hmDomainMap.clear();
	}
	
	/**
	 * disponse cache for restore
	 */
	public static void resetCache()
	{
		mapMacFilter.clear();
		mapMacPolicy.clear();
		mapIpPolicy.clear();
		mapIDSPolicy.clear();
		mapQosClassification.clear();
		mapQosClassificationAndMark.clear();
		mapQosRateControlAndQueu.clear();
		mapHives.clear();
		mapIdentityBasedTunnel.clear();
		mapRadiusServerAssign.clear();
		mapRadiusServerOnHiveAP.clear();
		mapDirectoryOrLdap.clear();
		mapSsid.clear();
		mapDns.clear();
		mapSyslog.clear();
		mapSnmp.clear();
		mapTimeAndDate.clear();
		mapOption.clear();
		mapConfigTemplate.clear();
		mapIpAdddress.clear();
		mapCapWebPortal.clear();
		mapCwpCertificate.clear();
		mapDosPrevention.clear();
		mapVlan.clear();
		mapRadiusAttrs.clear();
		mapLayer3Romaing.clear();
		mapSchedule.clear();
		mapRadioProfile.clear();
		mapMacAddress.clear();
		mapNetworkService.clear();
		mapUserAttribute.clear();
		mapMgtIpFilter.clear();
		mapUserProfile.clear();
		mapRadiusUserProfileRule.clear();
		mapMgtServiceFilter.clear();
		mapMarking.clear();
		mapLocalUserGroup.clear();
		mapLocalUser.clear();
		mapMacAuth.clear();
		mapOldLocalUserGroup.clear();
		mapOldLocalUser.clear();
		mapRoutingProfile.clear();
		mapHiveAP.clear();
		mapMapContainer.clear();
		mapUserGroup.clear();
		mapUser.clear();
		mapAlgConfiguration.clear();
		mapLocationServer.clear();
		mapEthernetAccess.clear();
		//mapUserProfileQos_old.clear();
		mapAlarm.clear();
		domainMap.clear();
		mapMACAddressChangeToOUI.clear();
		mapMgmtIpTracking.clear();
		mapRoutingPolicy.clear();
		mapAccessConsole.clear();
		mapVlanDhcpServer.clear();
		//mapSsidCwpMapping.clear();
		mapSsidTemplateIdSsidProfile.clear();
		lldpcdpProfileMap.clear();
		tunnelSettingOldInfo.clear();
		mapOldUserProfileTunnel.clear();
		mapVpnService.clear();
		mapLanProfile.clear();
		mapLanProfileReverse.clear();
		mapLanProfileObj.clear();
		mapFirewallPolicy.clear();
		mapLocationClientWatch.clear();
		mapAirscreenRuleGroup.clear();
		mayEthernetAccessResotre.clear();
		//mapEthernetAccessBo.clear();
		domainNameVADIDMap.clear();
		mapEthernetAccessBo.clear();		
		mapRadiusProxy.clear();
		mapRadiusLibrarySip.clear();
		mapOsObject.clear();
		mapApplication.clear();
		mapAppProfile.clear();
		mapDomainObject.clear();
		mapDeviceGroupPolicy.clear();
		
		mapTvComputerCart.clear();
		mapTvClass.clear();
		mapTvStudentRoster.clear();
		mapTvResourceMap.clear();
		
		mapDNSSerivces.clear();
		mapVpnNetwork.clear();
		mapVpnNetworkBo.clear();
		mapSubNetworkResource.clear();
		
		mapPpskServerHiveAp.clear();
		mapDefaultConfigTemplates.clear();
		mapAhDashboard.clear();
		mapAhDashboardLayout.clear();
		mapAhDashboardWidget.clear();
		mapAhDashboardComponent.clear();
		mapAhDashboardMetric.clear();
		mapWifiClientPreferredSsid.clear();
		mapNetworkObjectVlan.clear();
		mapUserProfileNetworkObject.clear();
		mapUserProfileNetworkObjectWithNewID=null;
		mapNetworkObjectVlanWithNewID=null;
		mapWlanNetworkObjectVlan.clear();
		mapLAN4VLANObjectNetwork.clear();
		mapOpenDNSDevice.clear();
		mapOpenDNSAccount.clear();
		
		//start from Chesapeake
		mapForwardingDBMap.clear();
		mapConfigTemplateMDM.clear();
		mapPortTemplateMap.clear();
		mapPortAccessMap.clear();
		mapMulticastGroup.clear();
		mapPseProfile.clear();
		mapSwitchSettings.clear();
		mapMstpRegions.clear();
		mapStpSettings.clear();
		mapNetworkDeviceHistory.clear();
		mapCustomApplication.clear();
	}
	
	public static Long getMapOneTimePassword(Long key) {
		if (key == null || mapOneTimePassword == null)
		{
			return null;
		}
		return mapOneTimePassword.get(key);
	}
	
	public static void setMapOneTimePassword(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}
		mapOneTimePassword.put(strKey, strValue);
	}
	
	public static void setMapHiveApAutoProvision(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}
		mapHiveApAutoProvision.put(strKey, strValue);
	}
	
	public static HiveApAutoProvision getMapHiveApAutoProvision(Long key) {
		if (key == null || mapHiveApAutoProvision == null)
		{
			return null;
		}
		Long newId = mapHiveApAutoProvision.get(key);
		
		if (null != newId) {
			return AhRestoreNewTools.CreateBoWithId(HiveApAutoProvision.class, newId);
		}
		return null;
	}
	
	public static Long getMapWifiClientPreferredSsid(Long strKey) {
		if (null == strKey)
		{
			return null;
		}

		return mapWifiClientPreferredSsid.get(strKey);
	}
	
	public static Long getMapOpenDNSDevice(Long strKey) {
		if (null == strKey)
		{
			return null;
		}

		return mapOpenDNSDevice.get(strKey);
	}
	
	public static void setMapOpenDNSDevice(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapOpenDNSDevice.put(strKey, strValue);
	}
	
	public static Long getMapOpenDNSAccount(Long strKey) {
		if (null == strKey)
		{
			return null;
		}

		return mapOpenDNSAccount.get(strKey);
	}
	
	public static void setMapOpenDNSAccount(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapOpenDNSAccount.put(strKey, strValue);
	}

	public static void setMapWifiClientPreferredSsid(Long strKey, Long strValue) {
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapWifiClientPreferredSsid.put(strKey, strValue);
	}

	public static void setMapStormControl(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapStormControl.put(strKey, strValue);
	}

	public static Long getMapStormControl(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapStormControl.get(strKey);
	}
	
	public static void setMapNetworkObjectVlan(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapNetworkObjectVlan.put(strKey, strValue);
	}

	public static Long getMapNetworkObjectVlan(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapNetworkObjectVlan.get(strKey);
	}
	
	public static void setMapUserProfileNetworkObject(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapUserProfileNetworkObject.put(strKey, strValue);
	}

	public static Long getMapUserProfileNetworkObject(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapUserProfileNetworkObject.get(strKey);
	}
	
	public static Map<Long, Long> getMapLANVLANObjectNetwork(Long lanId) {
	    if(null == lanId) {
	        return null;
	    }
        return mapLAN4VLANObjectNetwork.get(lanId);
    }

    public static void setMapVLANObjectNetwork(Long landId, Long vlanId, Long networkId) {
        if(null == landId || null == vlanId || null == networkId) {
            return;
        }
        if(null == mapLAN4VLANObjectNetwork.get(landId)) {
            Map<Long, Long> vlanObjNetwork = new HashMap<>();
            vlanObjNetwork.put(vlanId, networkId);
            mapLAN4VLANObjectNetwork.put(landId, vlanObjNetwork);
        } else {
            mapLAN4VLANObjectNetwork.get(landId).put(vlanId, networkId);
        }
    }

    public static Long getMapUserProfileNetworkObjectWithNewID(Long strKey)
	{
		if (mapUserProfileNetworkObjectWithNewID==null) {
			mapUserProfileNetworkObjectWithNewID = new HashMap<Long, Long>(); 
			for(Long ln: mapUserProfileNetworkObject.keySet()){
				Long newUpId = mapUserProfile.get(ln);
				Long newNetWorkId=null;
				if (mapUserProfileNetworkObject.get(ln)!=null) {
					newNetWorkId= mapVpnNetwork.get(mapUserProfileNetworkObject.get(ln));
				}
				if (newUpId!=null && newNetWorkId!=null) {
					mapUserProfileNetworkObjectWithNewID.put(newUpId,newNetWorkId);
				}
			}
		}
		
		if (null == strKey)
		{
			return null;
		}
		
		if (mapUserProfileNetworkObjectWithNewID == null) {
			return null;
		}

		return mapUserProfileNetworkObjectWithNewID.get(strKey);
	}
	
	public static Long getMapNetworkObjectVlanWithNewID(Long strKey)
	{
		if (mapNetworkObjectVlanWithNewID==null) {
			mapNetworkObjectVlanWithNewID = new HashMap<Long, Long>(); 
			for(Long ln: mapNetworkObjectVlan.keySet()){
				Long newNetWorkId = mapVpnNetwork.get(ln);
				Long newVlanId=null;
				if (mapNetworkObjectVlan.get(ln)!=null) {
					newVlanId= mapVlan.get(mapNetworkObjectVlan.get(ln));
				}
				if (newNetWorkId!=null && newVlanId!=null) {
					mapNetworkObjectVlanWithNewID.put(newNetWorkId,newVlanId);
				}
			}
		}
		
		if (null == strKey)
		{
			return null;
		}
		
		if (mapNetworkObjectVlanWithNewID == null) {
			return null;
		}

		return mapNetworkObjectVlanWithNewID.get(strKey);
	}
	
	public static void setMapWlanNetworkObjectVlan(Long strKey, Vlan strVlan, VpnNetwork strNetwork)
	{
		if (null == strKey || null == strVlan || strNetwork==null)
		{
			return;
		}
		ConfigTemplateVlanNetwork cvn = new ConfigTemplateVlanNetwork();
		cvn.setNetworkObj(strNetwork);
		cvn.setVlan(strVlan);
		mapWlanNetworkObjectVlan.put(strKey, cvn);
	}

	public static ConfigTemplateVlanNetwork getMapWlanNetworkObjectVlan(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapWlanNetworkObjectVlan.get(strKey);
	}
	
	public static Long getMapForwardingDBMap(Long strKey) {
		if(null == strKey){
			return null;
		}
		
		return mapForwardingDBMap.get(strKey);
	}
	
	public static Long getMapConfigTemplateMDM(Long strKey) {
		if(null == strKey){
			return null;
		}
		
		return mapConfigTemplateMDM.get(strKey);
	}
	
	
	public static void setMapConfigTemplateMDM(Long strKey, Long strValue) {
		if(null == strKey || null == strValue){
			return;
		}
		
		mapConfigTemplateMDM.put(strKey, strValue);
	}

	public static void setMapForwardingDBMap(Long strKey, Long strValue) {
		if(null == strKey || null == strValue){
			return;
		}
		
		mapForwardingDBMap.put(strKey, strValue);
	}

//	public static EthernetAccess getOldEthernetAccessBo(Long oldId)
//	{
//		return mapEthernetAccessBo.get(oldId);
//	}
//
//	public static void setMapEthernetAccessBo(Long oldId, EthernetAccess ethBo)
//	{
//		if (null == oldId || null == ethBo)
//		{
//			return;
//		}
//		AhRestoreNewMapTools.mapEthernetAccessBo.put(oldId, ethBo);
//	}

    public static Long getMapWiredPortTemplateId(Long key) {
        if (key == null || mapPortTemplateMap == null) {
            return null;
        }
        return mapPortTemplateMap.get(key);
    }

    public static void setMapWiredPortTemplateId(Long oldValue, Long newValue) {
        if (null == oldValue || null == newValue) {
            return;
        }
        mapPortTemplateMap.put(oldValue, newValue);
    }
    public static Long getMapWiredPortAccessId(Long key) {
        if (key == null || mapPortAccessMap == null) {
            return null;
        }
        return mapPortAccessMap.get(key);
    }
    
    public static void setMapWiredPortAccessId(Long oldValue, Long newValue) {
        if (null == oldValue || null == newValue) {
            return;
        }
        mapPortAccessMap.put(oldValue, newValue);
    }
    
	public static void setMapMulticastGroup(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapMulticastGroup.put(strKey, strValue);
	}

	public static Long getMapMulticastGroup(Long strKey)
	{
		if (null == strKey)
		{
			return null;
		}

		return mapMulticastGroup.get(strKey);
	}
	
	
	public static SwitchSettings getMapSwitchSettings(Long oldId) {
		if(null == oldId)
			return null;
		Long newId = mapSwitchSettings.get(oldId);
		if(null != newId)
			return AhRestoreNewTools.CreateBoWithId(SwitchSettings.class, newId);
		return null;
	}
	
	public static void setMapSwitchSettings(Long oldId, Long newId) {
		if( null == oldId || null == newId)
			return;
		mapSwitchSettings.put(oldId, newId);
	}
	
	public static MstpRegion getMapMstpRegions(Long oldId) {
		if(null == oldId)
			return null;
		Long newId = mapMstpRegions.get(oldId);
		if(null != newId)
			return AhRestoreNewTools.CreateBoWithId(MstpRegion.class, newId);
		return null;
	}

	public static void setMstpRegions(Long oldId, Long newId) {
		if( null == oldId || null == newId)
			return;
		mapMstpRegions.put(oldId, newId);
	}
	
	public static void setMapNetworkDeviceHistory(Long strKey, Long strValue)
   	{
   		if (null == strKey || null == strValue)
   		{
   			return;
   		}

   		mapNetworkDeviceHistory.put(strKey, strValue);
   	}

   	public static Long getMapNetworkDeviceHistory(Long strKey)
   	{
   		if (null == strKey)
   		{
   			return null;
   		}

   		return mapNetworkDeviceHistory.get(strKey);
   	}
   	
   	public static StpSettings getMapStpSettings(Long oldId) {
		if(null == oldId)
			return null;
		Long newId = mapStpSettings.get(oldId);
		if(null != newId)
			return AhRestoreNewTools.CreateBoWithId(StpSettings.class, newId);
		return null;
	}

	public static void setMapStpSettings(Long oldId, Long newId) {
		if( null == oldId || null == newId)
			return;
		mapStpSettings.put(oldId, newId);
	}
	
	public static void setMapCustomApplication(Long strKey, Long strValue)
	{
		if (null == strKey || null == strValue)
		{
			return;
		}

		mapCustomApplication.put(strKey, strValue);
	}

	
	public static CustomApplication getMapCustomApplication(Long oldId) {
		if(null == oldId)
			return null;
		Long newId = mapCustomApplication.get(oldId);
		if(null != newId)
			return AhRestoreNewTools.CreateBoWithId(CustomApplication.class, newId);
		return null;
	}

	public static Long getMapCLIBlob(Long oldId) {
		if(null == oldId)
			return null;
		return mapCLIBlob.get(oldId);
	}

	public static void setMapCLIBlob(Long oldId, Long newId) {
		if(oldId == null || newId == null){
			return;
		}
		mapCLIBlob.put(oldId, newId);
	}
}