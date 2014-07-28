package com.ah.be.db.configuration;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmUser;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.lan.LanProfile;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.mobility.TunnelSetting;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.network.AccessConsole;
import com.ah.bo.network.AlgConfiguration;
import com.ah.bo.network.BonjourGatewaySettings;
import com.ah.bo.network.DnsServiceProfile;
import com.ah.bo.network.DomainObject;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.FirewallPolicy;
import com.ah.bo.network.IdsPolicy;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.IpFilter;
import com.ah.bo.network.IpPolicy;
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.MacPolicy;
import com.ah.bo.network.NetworkService;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.RadiusAttrs;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.SubNetworkResource;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VlanDhcpServer;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnService;
import com.ah.bo.performance.AhNewReport;
import com.ah.bo.performance.AhReport;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.LocationServer;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.MgmtServiceSyslog;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusLibrarySip;
import com.ah.bo.useraccess.RadiusOnHiveap; //import com.ah.bo.useraccess.RadiusUserProfileRule;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.bo.wlan.Cwp; //import com.ah.bo.wlan.EthernetAccess;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.Navigation;
import com.ah.util.Tracer;

public class ConfigurationUtils {

	private static final Tracer log = new Tracer(ConfigurationUtils.class
			.getSimpleName());

	private static final String msg = "argument is null.";

	private static String getAllHiveAps(Long domainId) {
		return "select id from hive_ap where owner = " + domainId;
	}

	/*
	 * HiveAp-->Neighbor HiveAp
	 */
	private static String getHiveApByNeighborHiveAp(HiveAp neighborHiveAp) {
		if (null == neighborHiveAp) {
			throw new IllegalArgumentException(msg);
		}
		return "select hive_ap_id from hive_ap_l3cfg_neighbor where neighbormac = '"
				+ neighborHiveAp.getMacAddress() + "'";
	}

	/*
	 * HiveAp-->Radio profile
	 */
	private static String getHiveApByRadioProfile(String radio_profile_id_query) {
		return "select DISTINCT id from hive_ap where wifi0_radio_profile_id in ("
				+ radio_profile_id_query
				+ ") or wifi1_radio_profile_id in ("
				+ radio_profile_id_query + ")";
	}

	private static String getHiveApByRadioProfile(RadioProfile radio) {
		if (null == radio) {
			throw new IllegalArgumentException(msg);
		}
		return getHiveApByRadioProfile(String.valueOf(radio.getId()));
	}

	/*
	 * HiveAp-->Cwp
	 */
	private static String getHiveApByCwpProfile(String cwp_profile_id_query) {
		return "select DISTINCT id from hive_ap where ethernet_cwp_id in ("
				+ cwp_profile_id_query + ")";
	}

	private static String getHiveApByCwpProfile(Cwp cwp) {
		if (null == cwp) {
			throw new IllegalArgumentException(msg);
		}
		return getHiveApByCwpProfile(String.valueOf(cwp.getId()));
	}

	/*
	 * HiveAp-->RADIUS client
	 */
	private static String getHiveApByRadiusAssignment(String assignment_id_query) {
		return "select DISTINCT id from hive_ap where radius_client_id in ("
				+ assignment_id_query + ")";
	}

	private static String getHiveApByRadiusAssignment(RadiusAssignment assignment) {
		if (null == assignment) {
			throw new IllegalArgumentException(msg);
		}
		return getHiveApByRadiusAssignment(String.valueOf(assignment.getId()));
	}

	/*
	 * HiveAp-->RADIUS proxy
	 */
	private static String getHiveApByRadiusProxy(String proxy_id_query) {
		return "select DISTINCT id from hive_ap where radius_proxy_id in ("
				+ proxy_id_query + ")";
	}

	private static String getHiveApByRadiusProxy(RadiusProxy proxy) {
		if (null == proxy) {
			throw new IllegalArgumentException(msg);
		}
		return getHiveApByRadiusProxy(String.valueOf(proxy.getId()));
	}

	/*
	 * RADIUS proxy-->Radius Assignment
	 */
	private static String getRadiusProxyByRadiusAssignment(String assignment_id_query) {
		return "select DISTINCT radius_proxy_id from radius_proxy_realm where radius_server_id in ("
				+ assignment_id_query + ")";
	}

	private static String getRadiusProxyByRadiusAssignment(RadiusAssignment assignment) {
		if (null == assignment) {
			throw new IllegalArgumentException(msg);
		}
		return getRadiusProxyByRadiusAssignment(String.valueOf(assignment
				.getId()));
	}

	/*
	 * RADIUS proxy-->IpAddress
	 */
	private static String getRadiusProxyByIpAddress(String ipAddress_id_query) {
		return "select DISTINCT radius_proxy_id from radius_proxy_nas where ip_address_id in ("
				+ ipAddress_id_query + ")";
	}

	private static String getRadiusProxyByIpAddress(IpAddress ipAddress) {
		if (null == ipAddress) {
			throw new IllegalArgumentException(msg);
		}
		return getRadiusProxyByIpAddress(String.valueOf(ipAddress.getId()));
	}

	/*
	 * HiveAp-->RadiusOnHiveap
	 */
	private static String getHiveApByRadiusOnHiveap(String radius_server_id_query) {
		return "select DISTINCT id from hive_ap where radius_server_id in ("
				+ radius_server_id_query + ")";
	}

	private static String getHiveApByRadiusOnHiveap(RadiusOnHiveap radius) {
		if (null == radius) {
			throw new IllegalArgumentException(msg);
		}
		return getHiveApByRadiusOnHiveap(String.valueOf(radius.getId()));
	}

	/*
	 * RadiusOnHiveap-->ActiveDirectoryOrOpenLdap
	 */
	private static String getRadiusOnHiveApByActiveDirectoryOrOpenLdap(
			String directory_or_ldap_id_query) {
		return "select DISTINCT directory_openldap_id from directory_openldap_info where directory_or_ldap_id in ("
				+ directory_or_ldap_id_query + ")";
	}

	private static String getRadiusOnHiveApByActiveDirectoryOrOpenLdap(
			ActiveDirectoryOrOpenLdap adLdap) {
		if (null == adLdap) {
			throw new IllegalArgumentException(msg);
		}
		return getRadiusOnHiveApByActiveDirectoryOrOpenLdap(String
				.valueOf(adLdap.getId()));
	}

	private static String getActiveDirectoryOrOpenLdapByIpAddress(
			String ipAddress_id_query) {
		return "select DISTINCT id from active_directory_or_ldap where ad_ipaddress_id in ("
				+ ipAddress_id_query
				+ ") or ldap_ipaddress_id in ("
				+ ipAddress_id_query + ")";
	}

	private static String getActiveDirectoryOrOpenLdapByIpAddress(IpAddress ipAddress) {
		if (null == ipAddress) {
			throw new IllegalArgumentException(msg);
		}
		return getActiveDirectoryOrOpenLdapByIpAddress(String.valueOf(ipAddress
				.getId()));
	}

	/*
	 * RadiusOnHiveap-->Local User Group
	 */
	private static String getRadiusOnHiveApByLocalUserGroup(
			String local_user_group_id_query) {
		return "select DISTINCT ldap_user_profile_id from radius_hiveap_ldap_user_profile where local_user_group_id in ("
				+ local_user_group_id_query
				+ ") union select DISTINCT radius_on_hiveap_id from radius_on_hiveap_local_user_group where local_user_group_id in ("
				+ local_user_group_id_query + ")";
	}

	private static String getRadiusOnHiveApByLocalUserGroup(LocalUserGroup localGroup) {
		if (null == localGroup) {
			throw new IllegalArgumentException(msg);
		}
		return getRadiusOnHiveApByLocalUserGroup(String.valueOf(localGroup
				.getId()));
	}

	private static String getLocalUserGroupByScheduler(String scheduler_id_query) {
		return "select DISTINCT id from local_user_group where schedule_id in ("
				+ scheduler_id_query + ")";
	}

	private static String getLocalUserGroupByScheduler(Scheduler scheduler) {
		if (null == scheduler) {
			throw new IllegalArgumentException(msg);
		}
		return getLocalUserGroupByScheduler(String.valueOf(scheduler.getId()));
	}

	/*
	 * RadiusOnHiveap-->Local User
	 */
	// private String getRadiusOnHiveApByLocalUser(String local_user_id_query) {
	// return "select DISTINCT radius_on_hiveap_id from
	// radius_on_hiveap_local_user where local_user_id in ("
	// + local_user_id_query + ")";
	// }
	//
	// private String getRadiusOnHiveApByLocalUser(LocalUser localUser) {
	// if (null == localUser) {
	// throw new IllegalArgumentException(msg);
	// }
	// return getRadiusOnHiveApByLocalUser(String.valueOf(localUser.getId()));
	// }
	/*
	 * RadiusOnHiveap-->IpAddress
	 */
	private static String getRadiusOnHiveApByIpAddress(String ip_address_id_query) {
		return "select DISTINCT auth_id from radius_hiveap_auth where ip_address_id in ("
				+ ip_address_id_query 
				+ ") union select DISTINCT id from radius_on_hiveap where library_sip_server_id in ("
				+ ip_address_id_query + ")";
	}

	private static String getRadiusOnHiveApByIpAddress(IpAddress ip) {
		if (null == ip) {
			throw new IllegalArgumentException(msg);
		}
		return getRadiusOnHiveApByIpAddress(String.valueOf(ip.getId()));
	}
	
	/*
	 * RadiusOnHiveap-->RADIUS library sip
	 */
	private static String getRadiusOnHiveApByLibrarySip(String sip_id_query) {
		return "select DISTINCT id from radius_on_hiveap where library_sip_policy_id in ("
				+ sip_id_query + ")";
	}

	private static String getRadiusOnHiveApByLibrarySip(RadiusLibrarySip sip) {
		if (null == sip) {
			throw new IllegalArgumentException(msg);
		}
		return getRadiusOnHiveApByLibrarySip(String.valueOf(sip.getId()));
	}
	
	/*
	 * RADIUS library sip-->Local User Group
	 */
	private static String getLibrarySipByLocalUserGroup(
			String local_user_group_id_query) {
		return "select DISTINCT id from radius_library_sip where default_user_group_id in ("
				+ local_user_group_id_query
				+ ") union select DISTINCT radius_library_sip_id from sip_policy_rule where user_group_id in ("
				+ local_user_group_id_query + ")";
	}

	private static String getLibrarySipByLocalUserGroup(LocalUserGroup localGroup) {
		if (null == localGroup) {
			throw new IllegalArgumentException(msg);
		}
		return getLibrarySipByLocalUserGroup(String.valueOf(localGroup
				.getId()));
	}

	/*
	 * HiveAp-->LLDP/CDP profile
	 */
	// private static String getHiveApByLldpCdp(String lldpCdp_id_query) {
	// return "select DISTINCT id from hive_ap where lldpcdp_id in ("
	// + lldpCdp_id_query + ")";
	// }
	//
	// private static String getHiveApByLldpCdp(LLDPCDPProfile lldpCdp) {
	// if (null == lldpCdp) {
	// throw new IllegalArgumentException(msg);
	// }
	// return getHiveApByLldpCdp(String.valueOf(lldpCdp.getId()));
	// }

	/*
	 * HiveAp-->IP Address
	 */
	private static String getHiveApByIpAddress(String capwap_ip_id_query) {
		return "select DISTINCT id from hive_ap where capwap_ip_id in ("
				+ capwap_ip_id_query + ") or capwap_backup_ip_id in ("
				+ capwap_ip_id_query + ")";
	}

	private static String getHiveApByIpAddress(IpAddress ip) {
		if (null == ip) {
			throw new IllegalArgumentException(msg);
		}
		return getHiveApByIpAddress(String.valueOf(ip.getId()));
	}

	/*
	 * HiveAp-->IP Tracking
	 */
	// private String getHiveApByIpTracking(String iptracks_id_query) {
	// return
	// "select DISTINCT hive_ap_id from hive_ap_ip_track where iptracks_id in ("
	// + iptracks_id_query + ")";
	// }
	//
	// private String getHiveApByIpTracking(MgmtServiceIPTrack ipTrack) {
	// if (null == ipTrack) {
	// throw new IllegalArgumentException(msg);
	// }
	// return getHiveApByIpTracking(String.valueOf(ipTrack.getId()));
	// }

	/*
	 * HiveAp-->DHCP Server Settings
	 */
	private static String getHiveApByDhcpServer(String dhcpServer_id_query) {
		return "select DISTINCT hive_ap_id from hive_ap_dhcp_server where dhcpservers_id in ("
				+ dhcpServer_id_query + ")";
	}

	private static String getHiveApByDhcpServer(VlanDhcpServer dhcpServer) {
		if (null == dhcpServer) {
			throw new IllegalArgumentException(msg);
		}
		return getHiveApByDhcpServer(String.valueOf(dhcpServer.getId()));
	}

	/*
	 * HiveAp-->MAC or OUI
	 */
	private static String getHiveApByMacOrOui(String macOrOui_id_query) {
		return "select DISTINCT hive_ap_id from hive_ap_learning_mac where learning_mac_id in ("
				+ macOrOui_id_query + ")";
	}

	private static String getHiveApByMacOrOui(MacOrOui macOrOui) {
		if (null == macOrOui) {
			throw new IllegalArgumentException(msg);
		}
		return getHiveApByMacOrOui(String.valueOf(macOrOui.getId()));
	}

	/*
	 * HiveAp-->User Profile
	 */
	private static String getHiveApByUserProfile(String userProfile_id_query) {
		return "select DISTINCT id from hive_ap where eth0_user_profile_id in ("
				+ userProfile_id_query
				+ ") or eth1_user_profile_id in ("
				+ userProfile_id_query
				+ ") or agg0_user_profile_id in ("
				+ userProfile_id_query
				+ ") or red0_user_profile_id in ("
				+ userProfile_id_query
				+ ") or default_eth_auth_user_profile_id in ("
				+ userProfile_id_query
				+ ") or default_eth_reg_user_profile_id in ("
				+ userProfile_id_query
				+ ") union select DISTINCT hive_ap_id from hive_ap_user_profile where user_profile_id in ("
				+ userProfile_id_query + ")";
	}

	private static String getHiveApByUserProfile(UserProfile userProfile) {
		if (null == userProfile) {
			throw new IllegalArgumentException(msg);
		}
		return getHiveApByUserProfile(String.valueOf(userProfile.getId()));
	}

	/*
	 * HiveAp-->Configuration template
	 */
	private static String getHiveApByConfigTemplate(String template_id_query) {
		return "select DISTINCT id from hive_ap where devicetype != 2 and template_id in ("
				+ template_id_query + ")";
	}

	private static String getHiveApByVpnNetwork(VpnNetwork network) {
		if (null == network) {
			throw new IllegalArgumentException(msg);
		}
		return getHiveApByVpnNetwork(String.valueOf(network.getId()));
	}
	
	/*
	 * HiveAp-->Configuration Vpnnetwork
	 */
	private static String getHiveApByVpnNetwork(String network_id_query) {
		return "select DISTINCT id from hive_ap where devicetype = 2 and cvg_mgt0_network_id in ("
				+ network_id_query + ")";
	}
	
	private static String getHiveApByVlan(Vlan vlan) {
		if (null == vlan) {
			throw new IllegalArgumentException(msg);
		}
		return getHiveApByVlan(String.valueOf(vlan.getId()));
	}
	
	/*
	 * HiveAp-->Configuration Vlan
	 */
	private static String getHiveApByVlan(String vlan_id_query) {
		return "select DISTINCT id from hive_ap where devicetype = 2 and cvg_mgt0_vlan_id in ("
				+ vlan_id_query + ")";
	}
	
	

	private static String getHiveApByConfigTemplate(ConfigTemplate template) {
		if (null == template) {
			throw new IllegalArgumentException(msg);
		}
		return getHiveApByConfigTemplate(String.valueOf(template.getId()));
	}

	/*
	 * Configuration template-->HiveProfile-->...
	 */

	private static String getConfigTemplateByHive(String hive_profile_id_query) {
		return "select DISTINCT id from config_template where hive_profile_id in ("
				+ hive_profile_id_query + ")";
	}

	private static String getConfigTemplateByHive(HiveProfile hiveProfile) {
		if (null == hiveProfile) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByHive(String.valueOf(hiveProfile.getId()));
	}

	/*- Remove InterRoaming bo.
	private static String getHiveProfileByInterRoaming(String inter_roaming_id_query) {
		return "select DISTINCT id from hive_profile where inter_roaming_id in ("
				+ inter_roaming_id_query + ")";
	}

	private static String getHiveProfileByInterRoaming(InterRoaming interRoaming) {
		if (null == interRoaming) {
			throw new IllegalArgumentException(msg);
		}
		return getHiveProfileByInterRoaming(String
				.valueOf(interRoaming.getId()));
	}*/
	
	/*
	 * Configuration template-->Firewall Policy-->...
	 */

	private static String getConfigTemplateByFirewall(String fw_policy_id_query) {
		return "select DISTINCT id from config_template where firewall_policy_id in ("
				+ fw_policy_id_query + ")";
	}
	
	private static String getConfigTemplateByFirewall(FirewallPolicy fwPolicy) {
		if (null == fwPolicy) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByFirewall(String.valueOf(fwPolicy.getId()));
	}
	
	private static String getFwPolicyByIpAddress(String ip_id_query) {
		return "select DISTINCT firewall_policy_id from firewall_policy_rule where source_ip_id in ("
				+ ip_id_query
				+ ") or destination_ip_id in ("
				+ ip_id_query
				+ ")";
	}

	private static String getFwPolicyByIpAddress(IpAddress ip) {
		if (null == ip) {
			throw new IllegalArgumentException(msg);
		}
		return getFwPolicyByIpAddress(String.valueOf(ip.getId()));
	}

	private static String getFwPolicyByNetworkService(String network_service_id_query) {
		return "select DISTINCT firewall_policy_id from firewall_policy_rule where network_service_id in ("
				+ network_service_id_query + ")";
	}

	private static String getFwPolicyByNetworkService(NetworkService service) {
		if (null == service) {
			throw new IllegalArgumentException(msg);
		}
		return getFwPolicyByNetworkService(String.valueOf(service.getId()));
	}
	
	private static String getFwPolicyByVpnNetwork(String vpn_network_id_query) {
		return "select DISTINCT firewall_policy_id from firewall_policy_rule where source_network_id in ("
				+ vpn_network_id_query
				+ ") or destination_network_id in ("
				+ vpn_network_id_query
				+ ")";
	}

	private static String getFwPolicyByVpnNetwork(VpnNetwork network) {
		if (null == network) {
			throw new IllegalArgumentException(msg);
		}
		return getFwPolicyByVpnNetwork(String.valueOf(network.getId()));
	}
	
	private static String getFwPolicyByUserProfile(String user_profile_id_query) {
		return "select DISTINCT firewall_policy_id from firewall_policy_rule where source_up_id in ("
				+ user_profile_id_query + ")";
	}

	private static String getFwPolicyByUserProfile(UserProfile upObj) {
		if (null == upObj) {
			throw new IllegalArgumentException(msg);
		}
		return getFwPolicyByUserProfile(String.valueOf(upObj.getId()));
	}

	private static String getHiveProfileByDosPrevention(String dos_id_query) {
		return "select DISTINCT id from hive_profile where hive_dos_id in ("
				+ dos_id_query + ") or station_dos_id in (" + dos_id_query
				+ ")";
	}

	private static String getHiveProfileByDosPrevention(DosPrevention dosPrevention) {
		if (null == dosPrevention) {
			throw new IllegalArgumentException(msg);
		}
		return getHiveProfileByDosPrevention(String.valueOf(dosPrevention
				.getId()));
	}

	private static String getHiveProfileByMacFilter(String mac_filter_id_query) {
		return "select DISTINCT hive_profile_id from hive_profile_mac_filter where mac_filter_id in ("
				+ mac_filter_id_query + ")";
	}

	private static String getHiveProfileByMacFilter(MacFilter macFilter) {
		if (null == macFilter) {
			throw new IllegalArgumentException(msg);
		}
		return getHiveProfileByMacFilter(String.valueOf(macFilter.getId()));
	}

	private static String getMacFilterByMacOrOui(String mac_or_oui_id_query) {
		return "select DISTINCT mac_filter_id from mac_filter_mac_or_oui where mac_or_oui_id in ("
				+ mac_or_oui_id_query + ")";
	}

	private static String getMacFilterByMacOrOui(MacOrOui macOrOui) {
		if (null == macOrOui) {
			throw new IllegalArgumentException(msg);
		}
		return getMacFilterByMacOrOui(String.valueOf(macOrOui.getId()));
	}
	
	private static String getUserProfileByOsObject(String os_object_id_query) {
		return "select DISTINCT user_profile_id from device_policy_rule where os_obj_id in ("
				+ os_object_id_query + ")";
	}

	private static String getUserProfileByOsObject(OsObject osObj) {
		if (null == osObj) {
			throw new IllegalArgumentException(msg);
		}
		return getUserProfileByOsObject(String.valueOf(osObj.getId()));
	}
	
	private static String getUserProfileByMacOrOui(String mac_or_oui_id_query) {
		return "select DISTINCT user_profile_id from device_policy_rule where mac_obj_id in ("
				+ mac_or_oui_id_query + ")";
	}

	private static String getUserProfileByMacOrOui(MacOrOui macOrOui) {
		if (null == macOrOui) {
			throw new IllegalArgumentException(msg);
		}
		return getUserProfileByMacOrOui(String.valueOf(macOrOui.getId()));
	}
	
	private static String getUserProfileByDomainObject(String dom_object_id_query) {
		return "select DISTINCT user_profile_id from device_policy_rule where domain_obj_id in ("
				+ dom_object_id_query + ")";
	}

	private static String getUserProfileByDomainObject(DomainObject domObj) {
		if (null == domObj) {
			throw new IllegalArgumentException(msg);
		}
		return getUserProfileByDomainObject(String.valueOf(domObj.getId()));
	}

	/*
	 * Configuration template-->SSID Profile-->...
	 */
	private static String getConfigTemplateBySsid(String ssid_profile_id_query) {
		return "select DISTINCT config_template_id from config_template_ssid where ssid_profile_id in ("
				+ ssid_profile_id_query + ")";
	}

	private static String getConfigTemplateBySsidNoDistinct(
			String ssid_profile_id_query) {
		return "select config_template_id from config_template_ssid where ssid_profile_id in ("
				+ ssid_profile_id_query + ")";
	}

	private static String getConfigTemplateNameBySsid(String ssid_profile_id_query) {
		return "select DISTINCT bo2.configname from config_template_ssid bo1,config_template bo2 "
				+ "where bo1.config_template_id = bo2.id and bo1.ssid_profile_id in ("
				+ ssid_profile_id_query + ")";
	}

	private static String getConfigTemplateBySsid(SsidProfile ssid) {
		if (null == ssid) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateBySsid(String.valueOf(ssid.getId()));
	}

	private static String getSsidProfileByDosPrevention(String dos_id_query) {
		return "select DISTINCT id from ssid_profile where ip_dos_id in ("
				+ dos_id_query + ") or ssid_dos_id in (" + dos_id_query
				+ ") or station_dos_id in (" + dos_id_query + ")";
	}

	private static String getSsidProfileByDosPrevention(DosPrevention dos) {
		if (null == dos) {
			throw new IllegalArgumentException(msg);
		}
		return getSsidProfileByDosPrevention(String.valueOf(dos.getId()));
	}

	private static String getSsidProfileByMacFilter(String mac_filter_id_query) {
		return "select DISTINCT ssid_profile_id from ssid_profile_mac_filter where mac_filter_id in ("
				+ mac_filter_id_query + ")";
	}

	private static String getSsidProfileByMacFilter(MacFilter macFilter) {
		if (null == macFilter) {
			throw new IllegalArgumentException(msg);
		}
		return getSsidProfileByMacFilter(String.valueOf(macFilter.getId()));
	}

	private static String getSsidProfileByScheduler(String scheduler_id_query) {
		return "select DISTINCT ssid_profile_id from ssid_profile_scheduler where scheduler_id in ("
				+ scheduler_id_query + ")";
	}

	private static String getSsidProfileByScheduler(Scheduler scheduler) {
		if (null == scheduler) {
			throw new IllegalArgumentException(msg);
		}
		return getSsidProfileByScheduler(String.valueOf(scheduler.getId()));
	}

	private static String getSsidProfileByServiceFilter(String serviceFilter_id_query) {
		return "select DISTINCT id from ssid_profile where service_filter_id in ("
				+ serviceFilter_id_query + ")";
	}

	private static String getSsidProfileByServiceFilter(ServiceFilter filter) {
		if (null == filter) {
			throw new IllegalArgumentException(msg);
		}
		return getSsidProfileByServiceFilter(String.valueOf(filter.getId()));
	}
	
//	private static String getLanProfileByServiceFilter(String serviceFilter_id_query) {
//		return "select DISTINCT id from lan_profile where service_filter_id in ("
//				+ serviceFilter_id_query + ")";
//	}

//	private static String getLanProfileByServiceFilter(ServiceFilter filter) {
//		if (null == filter) {
//			throw new IllegalArgumentException(msg);
//		}
//		return getLanProfileByServiceFilter(String.valueOf(filter.getId()));
//	}

	/*- Cwp bo is associated with ConfigTemplate now. cwp back to SSID profile*/
	private static String getSsidProfileByCwp(String cwp_id_query) {
		return "select DISTINCT id from ssid_profile where cwp_id in ("
				+ cwp_id_query + ") or cwp_userpolicy_id in (" + cwp_id_query
				+ ")";
	}

	private static String getSsidProfileByCwp(Cwp cwp) {
		if (null == cwp) {
			throw new IllegalArgumentException(msg);
		}
		return getSsidProfileByCwp(String.valueOf(cwp.getId()));
	}
	
//	private static String getLanProfileByCwp(String cwp_id_query) {
//		return "select DISTINCT id from lan_profile where cwp_id in ("
//				+ cwp_id_query + ")";
//	}

//	private static String getLanProfileByCwp(Cwp cwp) {
//		if (null == cwp) {
//			throw new IllegalArgumentException(msg);
//		}
//		return getLanProfileByCwp(String.valueOf(cwp.getId()));
//	}

	/*
	 * Configuration template-->Cwp Profile-->... Deprecated
	 */
	/*-
	private static String getConfigTemplateByCwp(String cwp_id_query) {
		return "select DISTINCT config_template_id from config_template_ssid where cwp_id in ("
				+ cwp_id_query + ")";
	}

	private static String getConfigTemplateByCwp(Cwp cwp) {
		if (null == cwp) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByCwp(String.valueOf(cwp.getId()));
	}*/

	private static String getSsidProfileByRadiusAssignment(
			String radius_service_assign_id_query) {
		return "select DISTINCT id from ssid_profile where radius_service_assign_id in ("
				+ radius_service_assign_id_query + ")";
	}

	private static String getSsidProfileByRadiusAssignment(RadiusAssignment assign) {
		if (null == assign) {
			throw new IllegalArgumentException(msg);
		}
		return getSsidProfileByRadiusAssignment(String.valueOf(assign.getId()));
	}
	
//	private static String getLanProfileByRadiusAssignment(
//			String radius_service_assign_id_query) {
//		return "select DISTINCT id from lan_profile where radius_service_assign_id in ("
//				+ radius_service_assign_id_query + ")";
//	}

//	private static String getLanProfileByRadiusAssignment(RadiusAssignment assign) {
//		if (null == assign) {
//			throw new IllegalArgumentException(msg);
//		}
//		return getLanProfileByRadiusAssignment(String.valueOf(assign.getId()));
//	}

	private static String getSsidProfileByUserProfile(String user_profile_id_query) {
		return "select DISTINCT id from ssid_profile where userprofile_default_id in ("
				+ user_profile_id_query
				+ ") union select DISTINCT id from ssid_profile where userprofile_selfreg_id in ("
				+ user_profile_id_query
				+ ") union select DISTINCT id from ssid_profile where userprofile_guest_id in ("
				+ user_profile_id_query
				+ ") union select DISTINCT ssid_profile_id from ssid_profile_user_profile where user_profile_id in ("
				+ user_profile_id_query
				+ ") union select DISTINCT id from ssid_profile where userprofile_default_id in ("
				+ getUserProfileByUserProfile(user_profile_id_query)
				+ ") union select DISTINCT id from ssid_profile where userprofile_selfreg_id in ("
				+ getUserProfileByUserProfile(user_profile_id_query)
				+ ") union select DISTINCT id from ssid_profile where userprofile_guest_id in ("
				+ getUserProfileByUserProfile(user_profile_id_query)
				+ ") union select DISTINCT ssid_profile_id from ssid_profile_user_profile where user_profile_id in ("
				+ getUserProfileByUserProfile(user_profile_id_query)
				+ ")";
	}
	
	private static String getUserProfileByUserProfile(String user_profile_id_query) {
		return "select DISTINCT user_profile_id from device_policy_rule where userprofileid in ("
			+ user_profile_id_query + ")";
	}

	private static String getSsidProfileByUserProfile(UserProfile userProfile) {
		if (null == userProfile) {
			throw new IllegalArgumentException(msg);
		}
		return getSsidProfileByUserProfile(String.valueOf(userProfile.getId()));
	}
	
	private static String getConfigTemplateByRadiusOnHiveAp(
			String radius_server_id_query) {
		return "select DISTINCT id from config_template where RADIUS_SERVER_ID in ("
				+ radius_server_id_query + ")";
	}

	private static String getConfigTemplateByRadiusOnHiveAp(RadiusOnHiveap radiusOnHiveap) {
		if (null == radiusOnHiveap) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByRadiusOnHiveAp(String.valueOf(radiusOnHiveap.getId()));
	}
	
	private static String getConfigTemplateByRadiusProxy(
			String radius_proxy_id_query) {
		return "select DISTINCT id from config_template where RADIUS_PROXY_ID in ("
				+ radius_proxy_id_query + ")";
	}

	private static String getConfigTemplateByRadiusProxy(RadiusProxy radiusProxy) {
		if (null == radiusProxy) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByRadiusProxy(String.valueOf(radiusProxy.getId()));
	}

	/*
	 * Configuration template-->MgmtServiceDns
	 */
	private static String getConfigTemplateByMgmtServiceDns(
			String mgmt_service_dns_id_query) {
		return "select DISTINCT id from config_template where mgmt_service_dns_id in ("
				+ mgmt_service_dns_id_query + ")";
	}

	private static String getConfigTemplateByMgmtServiceDns(MgmtServiceDns mgmt) {
		if (null == mgmt) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByMgmtServiceDns(String.valueOf(mgmt.getId()));
	}

	private static String getMgmtServiceDnsByIpAddress(
			String mgmt_service_ip_address_id_query) {
		return "select mgmt_service_dns_id from mgmt_service_dns_info where mgmt_service_ip_address_id in ("
				+ mgmt_service_ip_address_id_query + ")";
	}

	private static String getMgmtServiceDnsByIpAddress(IpAddress ip) {
		if (null == ip) {
			throw new IllegalArgumentException(msg);
		}
		return getMgmtServiceDnsByIpAddress(String.valueOf(ip.getId()));
	}

	/*
	 * Configuration template-->MgmtServiceSyslog
	 */
	private static String getConfigTemplateByMgmtServiceSyslog(
			String mgmt_service_syslog_id_query) {
		return "select DISTINCT id from config_template where mgmt_service_syslog_id in ("
				+ mgmt_service_syslog_id_query + ")";
	}

	private static String getConfigTemplateByMgmtServiceSyslog(MgmtServiceSyslog syslog) {
		if (null == syslog) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByMgmtServiceSyslog(String.valueOf(syslog
				.getId()));
	}

	private static String getMgmtServiceSyslogByIpAddress(
			String mgmt_service_ip_address_id_query) {
		return "select mgmt_service_syslog_id from mgmt_service_syslog_info where mgmt_service_ip_address_id in ("
				+ mgmt_service_ip_address_id_query + ")";
	}

	private static String getMgmtServiceSyslogByIpAddress(IpAddress ip) {
		if (null == ip) {
			throw new IllegalArgumentException(msg);
		}
		return getMgmtServiceSyslogByIpAddress(String.valueOf(ip.getId()));
	}

	/*
	 * Configuration template-->MgmtServiceSnmp
	 */
	private static String getConfigTemplateByMgmtServiceSnmp(
			String mgmt_service_snmp_id_query) {
		return "select DISTINCT id from config_template where mgmt_service_snmp_id in ("
				+ mgmt_service_snmp_id_query + ")";
	}

	private static String getConfigTemplateByMgmtServiceSnmp(MgmtServiceSnmp snmp) {
		if (null == snmp) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByMgmtServiceSnmp(String.valueOf(snmp.getId()));
	}

	private static String getMgmtServiceSnmpByIpAddress(
			String mgmt_service_ip_address_id_query) {
		return "select mgmt_service_snmp_id from mgmt_service_snmp_info where mgmt_service_ip_address_id in ("
				+ mgmt_service_ip_address_id_query + ")";
	}

	private static String getMgmtServiceSnmpByIpAddress(IpAddress ip) {
		if (null == ip) {
			throw new IllegalArgumentException(msg);
		}
		return getMgmtServiceSnmpByIpAddress(String.valueOf(ip.getId()));
	}

	/*
	 * Configuration template-->MgmtServiceTime
	 */
	private static String getConfigTemplateByMgmtServiceTime(
			String mgmt_service_time_id_query) {
		return "select DISTINCT id from config_template where mgmt_service_time_id in ("
				+ mgmt_service_time_id_query + ")";
	}

	private static String getConfigTemplateByMgmtServiceTime(MgmtServiceTime time) {
		if (null == time) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByMgmtServiceTime(String.valueOf(time.getId()));
	}

	private static String getMgmtServiceTimeByIpAddress(
			String mgmt_service_ip_address_id_query) {
		return "select mgmt_service_time_id from mgmt_service_time_info where mgmt_service_ip_address_id in ("
				+ mgmt_service_ip_address_id_query + ")";
	}

	private static String getMgmtServiceTimeByIpAddress(IpAddress ip) {
		if (null == ip) {
			throw new IllegalArgumentException(msg);
		}
		return getMgmtServiceTimeByIpAddress(String.valueOf(ip.getId()));
	}

	/*
	 * Configuration template-->MgmtServiceOption
	 */
	private static String getConfigTemplateByMgmtServiceOption(
			String mgmt_service_option_id_query) {
		return "select DISTINCT id from config_template where mgmt_service_option_id in ("
				+ mgmt_service_option_id_query + ")";
	}

	private static String getConfigTemplateByMgmtServiceOption(MgmtServiceOption option) {
		if (null == option) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByMgmtServiceOption(String.valueOf(option
				.getId()));
	}

	private static String getMgmtServiceOptionByRadiusAssignment(
			String radius_service_assign_id_query) {
		return "select DISTINCT id from mgmt_service_option where radius_service_assign_id in ("
				+ radius_service_assign_id_query + ")";
	}

	private static String getMgmtServiceOptionByRadiusAssignment(
			RadiusAssignment assign) {
		if (null == assign) {
			throw new IllegalArgumentException(msg);
		}
		return getMgmtServiceOptionByRadiusAssignment(String.valueOf(assign
				.getId()));
	}

	/*
	 * Configuration template-->IdsPolicy
	 */
	private static String getConfigTemplateByIdsPolicy(String ids_policy_id_query) {
		return "select DISTINCT id from config_template where ids_policy_id in ("
				+ ids_policy_id_query + ")";
	}

	private static String getConfigTemplateByIdsPolicy(IdsPolicy ids) {
		if (null == ids) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByIdsPolicy(String.valueOf(ids.getId()));
	}

	private static String getIdsPolicyByVlan(String vlan_id_query) {
		return "select DISTINCT ids_policy_id from ids_policy_vlan where vlan_id in ("
				+ vlan_id_query + ")";
	}

	private static String getIdsPolicyByVlan(Vlan vlan) {
		if (null == vlan) {
			throw new IllegalArgumentException(msg);
		}
		return getIdsPolicyByVlan(String.valueOf(vlan.getId()));
	}

	private static String getIdsPolicyByMacOrOui(String mac_or_oui_id_query) {
		return "select DISTINCT ids_policy_id from ids_policy_mac_or_oui where mac_or_oui_id in ("
				+ mac_or_oui_id_query + ")";
	}

	private static String getIdsPolicyByMacOrOui(MacOrOui oui) {
		if (null == oui) {
			throw new IllegalArgumentException(msg);
		}
		return getIdsPolicyByMacOrOui(String.valueOf(oui.getId()));
	}

//	private static String getIdsPolicyBySsid(String ssid_profile_id_query) {
//		return "select DISTINCT ids_policy_id from ids_policy_ssid_profile where ssid_profile_id in ("
//				+ ssid_profile_id_query + ")";
//	}
//
//	private static String getIdsPolicyBySsid(SsidProfile ssid) {
//		if (null == ssid) {
//			throw new IllegalArgumentException(msg);
//		}
//		return getIdsPolicyBySsid(String.valueOf(ssid.getId()));
//	}

	/*
	 * Configuration template-->RadiusAssignment. Deprecated
	 */
	/*-
	private static String getConfigTemplateByRadiusAssignment(
			String radius_service_assign_id_query) {
		return "select DISTINCT id from config_template where radius_service_assign_id in ("
				+ radius_service_assign_id_query
				+ ") union select DISTINCT config_template_id from config_template_ssid where radius_service_assign_id in ("
				+ radius_service_assign_id_query + ")";
	}

	private static String getConfigTemplateByRadiusAssignment(RadiusAssignment assign) {
		if (null == assign) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByRadiusAssignment(String.valueOf(assign
				.getId()));
	}*/

	private static String getRadiusAssignmentByIpAddress(String ip_address_id_query) {
		return "select assignment_id from radius_service where ip_address_id in ("
				+ ip_address_id_query + ")";
	}

	private static String getRadiusAssignmentByIpAddress(IpAddress ip) {
		if (null == ip) {
			throw new IllegalArgumentException(msg);
		}
		return getRadiusAssignmentByIpAddress(String.valueOf(ip.getId()));
	}

	/*
	 * Configuration template-->Vlan
	 */
	private static String getConfigTemplateByVlan(String vlan_id_query) {
		return "select DISTINCT id from config_template where vlan_id in ("
				+ vlan_id_query
				+ ") union select DISTINCT id from config_template where native_vlan_id in ("
				+ vlan_id_query 
				+ ") union select DISTINCT config_template_id as id from user_profile_vlan_mapping where vlan_id in ("
				+ vlan_id_query + ")";
	}

	private static String getConfigTemplateByVlan(Vlan vlan) {
		if (null == vlan) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByVlan(String.valueOf(vlan.getId()));
	}

	/*
	 * Configuration template-->IpFilter
	 */
	private static String getConfigTemplateByIpFilter(String ip_filter_id_query) {
		return "select DISTINCT id from config_template where ip_filter_id in ("
				+ ip_filter_id_query + ")";
	}

	private static String getConfigTemplateByIpFilter(IpFilter ipFilter) {
		if (null == ipFilter) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByIpFilter(String.valueOf(ipFilter.getId()));
	}

	private static String getIpFilterByIpAddress(String ip_address_id_query) {
		return "select ip_filter_id from ip_filter_ip_address where ip_address_id in ("
				+ ip_address_id_query + ")";
	}

	private static String getIpFilterByIpAddress(IpAddress ip) {
		if (null == ip) {
			throw new IllegalArgumentException(msg);
		}
		return getIpFilterByIpAddress(String.valueOf(ip.getId()));
	}

	/*
	 * Configuration template-->AccessConsole
	 */
	private static String getConfigTemplateByAccessConsole(
			String access_console_id_query) {
		return "select DISTINCT id from config_template where access_console_id in ("
				+ access_console_id_query + ")";
	}

	private static String getConfigTemplateByAccessConsole(AccessConsole ac) {
		if (null == ac) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByAccessConsole(String.valueOf(ac.getId()));
	}

	/*
	 * Configuration template-->RadiusAttrs
	 */
	private static String getConfigTemplateByRadiusAttrs(
			String radius_attrs_id_query) {
		return "select DISTINCT id from config_template where radius_attrs_id in ("
				+ radius_attrs_id_query + ")";
	}
	
	private static String getConfigTemplateByRadiusAttrs(RadiusAttrs ra) {
		if (null == ra) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByRadiusAttrs(String.valueOf(ra.getId()));
	}

	private static String getAccessConsoleByMacFilter(String mac_filter_id_query) {
		return "select DISTINCT access_console_id from access_console_mac_filter where mac_filter_id in ("
				+ mac_filter_id_query + ")";
	}

	private static String getAccessConsoleByMacFilter(MacFilter macFilter) {
		if (null == macFilter) {
			throw new IllegalArgumentException(msg);
		}
		return getAccessConsoleByMacFilter(String.valueOf(macFilter.getId()));
	}

	/*
	 * Configuration template-->ServiceFilter
	 */
	private static String getConfigTemplateByServiceFilter(
			String service_filter_id_query) {
		return "select DISTINCT id from config_template where ETH0_SERVICE_FILTER_ID in ("
				+ service_filter_id_query
				+ ") or WIRE_SERVICE_FILTER_ID in ("
				+ service_filter_id_query
				+ ") or ETH0BACK_SERVICE_FILTER_ID in ("
				+ service_filter_id_query
				+ ") or ETH1BACK_SERVICE_FILTER_ID in ("
				+ service_filter_id_query
				+ ") or RED0BACK_SERVICE_FILTER_ID in ("
				+ service_filter_id_query
				+ ") or AGG0BACK_SERVICE_FILTER_ID in ("
				+ service_filter_id_query
				+ ") or ETH1_SERVICE_FILTER_ID in ("
				+ service_filter_id_query
				+ ") or RED0_SERVICE_FILTER_ID in ("
				+ service_filter_id_query
				+ ") or AGG0_SERVICE_FILTER_ID in ("
				+ service_filter_id_query + ")";
	}

	private static String getConfigTemplateByServiceFilter(ServiceFilter filter) {
		if (null == filter) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByServiceFilter(String.valueOf(filter.getId()));
	}

	/*
	 * Configuration template-->LocationServer
	 */
	private static String getConfigTemplateByLocationServer(
			String location_server_id_query) {
		return "select DISTINCT id from config_template where location_server_id in ("
				+ location_server_id_query + ")";
	}

	private static String getConfigTemplateByLocationServer(LocationServer server) {
		if (null == server) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByLocationServer(String.valueOf(server.getId()));
	}

	private static String getLocationServerByIpAddress(String ipaddress_id_query) {
		return "select DISTINCT id from location_server where ipaddress_id in ("
				+ ipaddress_id_query + ")";
	}

	private static String getLocationServerByIpAddress(IpAddress ip) {
		if (null == ip) {
			throw new IllegalArgumentException(msg);
		}
		return getLocationServerByIpAddress(String.valueOf(ip.getId()));
	}

	/*
	 * Configuration template-->LLDP/CDP
	 */
	private static String getConfigTemplateByLldpCdp(String lldpCpd_id_query) {
		return "select DISTINCT id from config_template where lldpcdp_id in ("
				+ lldpCpd_id_query + ")";
	}

	private static String getConfigTemplateByLldpCdp(LLDPCDPProfile lldpCdp) {
		if (null == lldpCdp) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByLldpCdp(String.valueOf(lldpCdp.getId()));
	}
	
	/*
	 * HiveAp -->IP Tracking
	 */
	private static String getHiveApByIpTracking(String ipTrack_id_query) {
		return "select DISTINCT id from hive_ap where vpn_ip_track_id in ("
				+ ipTrack_id_query + ")";
	}

	/*
	 * Configuration template-->IP Tracking
	 */
	private static String getConfigTemplateByIpTracking(String ipTrack_id_query) {
		return "select DISTINCT config_template_id from config_template_ip_track where iptracks_id in ("
				+ ipTrack_id_query + ")";
	}

	private static String getConfigTemplateByIpTracking(MgmtServiceIPTrack ipTrack) {
		if (null == ipTrack) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByIpTracking(String.valueOf(ipTrack.getId()));
	}

	/*
	 * Configuration template-->IP Tracking
	 */
	private static String getConfigTemplateByIpTrackingRouter(String ipTrack_id_query) {
		return "select DISTINCT id from config_template where primary_ip_track_id in ("
				+ ipTrack_id_query + 
				") union select DISTINCT id from config_template where backup1_ip_track_id in ("
				+ ipTrack_id_query + 
				") union select DISTINCT id from config_template where backup2_ip_track_id in ("
				+ ipTrack_id_query + ")";
	}
	
	private static String getConfigTemplateByIpTrackingRouter(MgmtServiceIPTrack ipTrack) {
		if (null == ipTrack) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByIpTrackingRouter(String.valueOf(ipTrack.getId()));
	}

	/*
	 * Configuration template-->EthernetAccess
	 */
	/*-
	private static String getConfigTemplateByEthernetAccess(String access_query) {
		return "select DISTINCT id from config_template where ETHERNET_ACCESS_ID in ("
				+ access_query
				+ ") or ETHERNET_BRIDGE_ID in ("
				+ access_query
				+ ") or ETHERNET_ACCESS_ID_ETH1 in("
				+ access_query
				+ ") or ETHERNET_BRIDGE_ID_ETH1 in ("
				+ access_query
				+ ") or ETHERNET_ACCESS_ID_RED in ("
				+ access_query
				+ ") or ETHERNET_BRIDGE_ID_RED in ("
				+ access_query
				+ ") or ETHERNET_ACCESS_ID_AGG in ("
				+ access_query
				+ ") or ETHERNET_BRIDGE_ID_AGG in (" + access_query + ")";
	}

	private static String getConfigTemplateByEthernetAccess(EthernetAccess access) {
		if (null == access) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByEthernetAccess(String.valueOf(access.getId()));
	}*/

	/*-
	private static String getEthernetAccessByUserProfile(String user_profile_id_query) {
		return "select DISTINCT id from ethernet_access where user_profile_id in ("
				+ user_profile_id_query + ")";
	}

	private static String getEthernetAccessByUserProfile(UserProfile userProfile) {
		if (null == userProfile) {
			throw new IllegalArgumentException(msg);
		}
		return getEthernetAccessByUserProfile(String.valueOf(userProfile
				.getId()));
	}
	 */
//	private static String getUserProfileByVpnNetwork(String network_id_query) {
//		return "select DISTINCT id from user_profile where vpn_network_id in ("
//				+ network_id_query + ")";
//	}
	
//	private static String getUserProfileByVpnNetwork(VpnNetwork vpnNetwork) {
//		if (null == vpnNetwork) {
//			throw new IllegalArgumentException(msg);
//		}
//		return getUserProfileByVpnNetwork(String.valueOf(vpnNetwork.getId()));
//	}
	
	private static String getUserProfileByVlan(String vlan_id_query) {
		return "select DISTINCT id from user_profile where vlan_id in ("
				+ vlan_id_query + ")";
	}

	private static String getUserProfileByVlan(Vlan vlan) {
		if (null == vlan) {
			throw new IllegalArgumentException(msg);
		}
		return getUserProfileByVlan(String.valueOf(vlan.getId()));
	}

	private static String getUserProfileByUserProfileAttribute(
			String attritute_group_id_query) {
		return "select DISTINCT id from user_profile where attritute_group_id in ("
				+ attritute_group_id_query + ")";
	}

	private static String getUserProfileByUserProfileAttribute(
			UserProfileAttribute attribute) {
		if (null == attribute) {
			throw new IllegalArgumentException(msg);
		}
		return getUserProfileByUserProfileAttribute(String.valueOf(attribute
				.getId()));
	}

	private static String getUserProfileByQosRateControl(
			String qos_rate_control_id_query) {
		return "select DISTINCT id from user_profile where qos_rate_control_id in ("
				+ qos_rate_control_id_query + ")";
	}

	private static String getUserProfileByQosRateControl(QosRateControl control) {
		if (null == control) {
			throw new IllegalArgumentException(msg);
		}
		return getUserProfileByQosRateControl(String.valueOf(control.getId()));
	}

	private static String getUserProfileByTunnelSetting(
			String identity_based_tunnel_id_query) {
		return "select DISTINCT id from user_profile where identity_based_tunnel_id in ("
				+ identity_based_tunnel_id_query + ")";
	}

	private static String getUserProfileByTunnelSetting(TunnelSetting tunnel) {
		if (null == tunnel) {
			throw new IllegalArgumentException(msg);
		}
		return getUserProfileByTunnelSetting(String.valueOf(tunnel.getId()));
	}

	private static String getTunnelSettingByIpAddress(String ip_address_id_query) {
		return "select DISTINCT id from tunnel_setting where ip_address_id in ("
				+ ip_address_id_query
				+ ") union select DISTINCT tunnel_setting_id from tunnel_setting_ip_address where ip_address_id in ("
				+ ip_address_id_query + ")";
	}

	private static String getTunnelSettingByIpAddress(IpAddress ip) {
		if (null == ip) {
			throw new IllegalArgumentException(msg);
		}
		return getTunnelSettingByIpAddress(String.valueOf(ip.getId()));
	}

	private static String getUserProfileByIpPolicy(String ip_police_id_query) {
		return "select DISTINCT id from user_profile where ip_police_from_id in ("
				+ ip_police_id_query
				+ ") or ip_police_to_id in ("
				+ ip_police_id_query + ")";
	}

	private static String getUserProfileByIpPolicy(IpPolicy policy) {
		if (null == policy) {
			throw new IllegalArgumentException(msg);
		}
		return getUserProfileByIpPolicy(String.valueOf(policy.getId()));
	}

	private static String getIpPolicyByIpAddress(String ip_id_query) {
		return "select DISTINCT ip_policy_id from ip_policy_rule where source_ip_id in ("
				+ ip_id_query
				+ ") or destination_ip_id in ("
				+ ip_id_query
				+ ")";
	}

	private static String getIpPolicyByIpAddress(IpAddress ip) {
		if (null == ip) {
			throw new IllegalArgumentException(msg);
		}
		return getIpPolicyByIpAddress(String.valueOf(ip.getId()));
	}

	private static String getIpPolicyByNetworkService(String network_service_id_query) {
		return "select DISTINCT ip_policy_id from ip_policy_rule where network_service_id in ("
				+ network_service_id_query + ")";
	}

	private static String getIpPolicyByNetworkService(NetworkService service) {
		if (null == service) {
			throw new IllegalArgumentException(msg);
		}
		return getIpPolicyByNetworkService(String.valueOf(service.getId()));
	}

	private static String getUserProfileByMacPolicy(String mac_policy_id_query) {
		return "select DISTINCT id from user_profile where mac_policy_from_id in ("
				+ mac_policy_id_query
				+ ") or mac_policy_to_id in ("
				+ mac_policy_id_query + ")";
	}

	private static String getUserProfileByMacPolicy(MacPolicy policy) {
		if (null == policy) {
			throw new IllegalArgumentException(msg);
		}
		return getUserProfileByMacPolicy(String.valueOf(policy.getId()));
	}

	private static String getMacPolicyByMacOrOui(String mac_id_query) {
		return "select DISTINCT mac_policy_id from mac_policy_rule where source_mac_id in ("
				+ mac_id_query
				+ ") or destination_mac_id in ("
				+ mac_id_query
				+ ")";
	}

	private static String getMacPolicyByMacOrOui(MacOrOui oui) {
		if (null == oui) {
			throw new IllegalArgumentException(msg);
		}
		return getMacPolicyByMacOrOui(String.valueOf(oui.getId()));
	}

	private static String getUserProfileByScheduler(String scheduler_id_query) {
		return "select DISTINCT user_profile_id from user_profile_scheduler where scheduler_id in ("
				+ scheduler_id_query + ")";
	}

	private static String getUserProfileByScheduler(Scheduler scheduler) {
		if (null == scheduler) {
			throw new IllegalArgumentException(msg);
		}
		return getUserProfileByScheduler(String.valueOf(scheduler.getId()));
	}

	private static String getSsidProfileByLocalUserGroup(String localUserGroup_id_query) {
		return "select DISTINCT ssid_profile_id from ssid_local_user_group where local_user_group_id in ("
				+ localUserGroup_id_query + ")";
	}

	private static String getSsidProfileByLocalUserGroup(LocalUserGroup group) {
		if (null == group) {
			throw new IllegalArgumentException(msg);
		}
		return getSsidProfileByLocalUserGroup(String.valueOf(group.getId()));
	}

	/*-
	private static String getEthernetAccessByMacOrOui(String mac_or_oui_id_query) {
		return "select DISTINCT ethernet_access_id from ethernet_access_mac where mac_or_oui_id in ("
				+ mac_or_oui_id_query + ")";
	}

	private static String getEthernetAccessByMacOrOui(MacOrOui oui) {
		if (null == oui) {
			throw new IllegalArgumentException(msg);
		}
		return getEthernetAccessByMacOrOui(String.valueOf(oui.getId()));
	}
	 */
	/*
	 * Configuration template-->AlgConfiguration
	 */
	private static String getConfigTemplateByAlgConfiguration(
			String alg_configuration_id_query) {
		return "select DISTINCT id from config_template where alg_configuration_id in ("
				+ alg_configuration_id_query + ")";
	}

	private static String getConfigTemplateByAlgConfiguration(AlgConfiguration alg) {
		if (null == alg) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByAlgConfiguration(String.valueOf(alg.getId()));
	}

	/*
	 * Configuration template-->QosClassification
	 */
	private static String getConfigTemplateByQosClassification(
			String qos_classification_id_query) {
		return "select DISTINCT id from config_template where qos_classification_id in ("
				+ qos_classification_id_query + ")";
	}

	private static String getConfigTemplateByQosClassification(QosClassification qos) {
		if (null == qos) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByQosClassification(String.valueOf(qos.getId()));
	}

	private static String getQosClassificationByNetworkService(
			String network_service_id_query) {
		return "select DISTINCT qos_classification_id from qos_classification_service where network_service_id in ("
				+ network_service_id_query + ")";
	}

	private static String getQosClassificationByNetworkService(NetworkService service) {
		if (null == service) {
			throw new IllegalArgumentException(msg);
		}
		return getQosClassificationByNetworkService(String.valueOf(service
				.getId()));
	}

//	private static String getQosClassificationBySsid(String ssid_id_query) {
//		return "select DISTINCT qos_classification_id from qos_classification_ssid where ssid_id in ("
//				+ ssid_id_query + ")";
//	}
//
//	private static String getQosClassificationBySsid(SsidProfile ssid) {
//		if (null == ssid) {
//			throw new IllegalArgumentException(msg);
//		}
//		return getQosClassificationBySsid(String.valueOf(ssid.getId()));
//	}

	private static String getQosClassificationByMacOrOui(String mac_or_oui_id_query) {
		return "select DISTINCT qos_classification_id from qos_classification_mac where mac_or_oui_id in ("
				+ mac_or_oui_id_query + ")";
	}

	private static String getQosClassificationByMacOrOui(MacOrOui oui) {
		if (null == oui) {
			throw new IllegalArgumentException(msg);
		}
		return getQosClassificationByMacOrOui(String.valueOf(oui.getId()));
	}

	/*
	 * Configuration template-->QosMarking
	 */
	private static String getConfigTemplateByQosMarking(String qos_marking_id_query) {
		return "select DISTINCT id from config_template where qos_marking_id in ("
				+ qos_marking_id_query + ")";
	}

	private static String getConfigTemplateByQosMarking(QosMarking marking) {
		if (null == marking) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByQosMarking(String.valueOf(marking.getId()));
	}

	/*
	 * Configuration template-->UserProfile Deprecated
	 */
	/*-
	private static String getConfigTemplateByUserProfile(String user_profile_id_query) {
		return "select DISTINCT config_template_id from config_template_ssid_user_profile where user_profile_id in ("
				+ user_profile_id_query + ")";
	}

	private static String getConfigTemplateByUserProfile(UserProfile userProfile) {
		if (null == userProfile) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByUserProfile(String.valueOf(userProfile
				.getId()));
	}*/

	/*
	 * Configuration template-->RadiusUserProfileRule
	 */
	/*- removed
	private static String getConfigTemplateByRadiusUserProfileRule(
			String radius_user_profile_rule_id_query) {
		return "select DISTINCT config_template_id from config_template_ssid where radius_user_profile_rule_id in ("
				+ radius_user_profile_rule_id_query + ")";
	}

	private static String getConfigTemplateByRadiusUserProfileRule(
			RadiusUserProfileRule rule) {
		if (null == rule) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByRadiusUserProfileRule(String.valueOf(rule
				.getId()));
	}

	private static String getRadiusUserProfileRuleByUserProfile(
			String user_profile_id_query) {
		return "select DISTINCT radius_user_profile_rule_id from radius_rule_user_profile where user_profile_id in ("
				+ user_profile_id_query + ")";
	}

	private static String getRadiusUserProfileRuleByUserProfile(UserProfile userProfile) {
		if (null == userProfile) {
			throw new IllegalArgumentException(msg);
		}
		return getRadiusUserProfileRuleByUserProfile(String.valueOf(userProfile
				.getId()));
	}*/

	/*
	 * Configuration template-->QosClassfierAndMarker
	 */
	/*- removed
	private static String getConfigTemplateByQosClassfierAndMarker(
			String qos_classfier_and_marker_query) {
		return "select config_template_id from config_template_ssid where qos_classfier_and_marker in ("
				+ qos_classfier_and_marker_query + ")";
	}

	private static String getConfigTemplateByQosClassfierAndMarker(
			QosClassfierAndMarker qos) {
		if (null == qos) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByQosClassfierAndMarker(String.valueOf(qos
				.getId()));
	}*/

	/*
	 * Configuration template-->VPN Service
	 */
	private static String getConfigTemplateByVpnService(String vpn_service_id_query) {
		return "select DISTINCT id from config_template where vpn_service_id in ("
				+ vpn_service_id_query + ")";
	}

	private static String getConfigTemplateByVpnService(VpnService service) {
		if (null == service) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByVpnService(String.valueOf(service.getId()));
	}

	/*
	 * VPN Service--> IP Address
	 */
	private static String getVpnServiceByIpAddress(String ip_address_id_query) {
		return "select DISTINCT id from vpn_service where dns_ip in ("
				+ ip_address_id_query + ")";
	}

	private static String getVpnServiceByIpAddress(IpAddress ip) {
		if (null == ip) {
			throw new IllegalArgumentException(msg);
		}
		return getVpnServiceByIpAddress(String.valueOf(ip.getId()));
	}
	
	/*
	 * VPN Service--> Domain Object
	 */
	private static String getVpnServiceByDomainObject(String dom_obj_id_query) {
		return "select DISTINCT id from vpn_service where domainobject_id in ("
				+ dom_obj_id_query + ")";
	}

	private static String getVpnServiceByDomainObject(DomainObject dom_obj) {
		if (null == dom_obj) {
			throw new IllegalArgumentException(msg);
		}
		return getVpnServiceByDomainObject(String.valueOf(dom_obj.getId()));
	}
	
	/*
	 * VPN Network--> Vlan
	 */
//	private static String getVpnNetworkByVlan(String vlan_id_query) {
//		return "select DISTINCT id from vpn_network where vlan_id in ("
//				+ vlan_id_query + ")";
//	}
//
//	private static String getVpnNetworkByVlan(Vlan vlan) {
//		if (null == vlan) {
//			throw new IllegalArgumentException(msg);
//		}
//		return getVpnNetworkByVlan(String.valueOf(vlan.getId()));
//	}
	
	/*
	 * VPN Network--> DnsServiceProfile
	 */
	private static String getVpnNetworkByDnsService(String dns_obj_id_query) {
		return "select DISTINCT id from vpn_network where vpn_dns_id in ("
				+ dns_obj_id_query + ")";
	}

	private static String getVpnNetworkByDnsService(DnsServiceProfile dns_obj) {
		if (null == dns_obj) {
			throw new IllegalArgumentException(msg);
		}
		return getVpnNetworkByDnsService(String.valueOf(dns_obj.getId()));
	}
	
	/*
	 * VPN Network--> Websense or Barracuda Server Settings 
	 */
	private static String getVpnNetworkByWebsense(Long owner_id) {
		return "select DISTINCT id from vpn_network where websecurity = "+VpnNetwork.VPN_NETWORK_WEBSECURITY_WEBSENSE+" and owner = "+ owner_id;
	}
	
	private static String getVpnNetworkByBarracuda(Long owner_id) {
		return "select DISTINCT id from vpn_network where websecurity = "+VpnNetwork.VPN_NETWORK_WEBSECURITY_BARRACUDA+" and owner = "+ owner_id;
	}

	private static String getVpnNetworkByHMServices(HMServicesSettings hm_obj) {
		if (null == hm_obj || null == hm_obj.getOwner()) {
			throw new IllegalArgumentException(msg);
		}
		if (hm_obj.isEnableBarracuda()) {
			return getVpnNetworkByBarracuda(hm_obj.getOwner().getId());
		} else {
			return getVpnNetworkByWebsense(hm_obj.getOwner().getId());
		}
	}
	
	/*
	 * VPN Network--> Domain Object
	 */
	private static String getVpnNetworkByDomainObject(Long dom_obj_id) {
		String web_obj_query = "select DISTINCT owner from hmservicessettings where enablewebsense = true and websensewhitelist_id = "+dom_obj_id;
		String bar_obj_query = "select DISTINCT owner from hmservicessettings where enablebarracuda = true and barracudawhitelist_id = "+dom_obj_id;
		return "select DISTINCT id from vpn_network where (websecurity = "+VpnNetwork.VPN_NETWORK_WEBSECURITY_WEBSENSE+" and owner in ("
				+ web_obj_query + ")) or (websecurity = "+VpnNetwork.VPN_NETWORK_WEBSECURITY_BARRACUDA+" and owner in ("
				+ bar_obj_query + "))";
	}

	private static String getVpnNetworkByDomainObject(DomainObject dom_obj) {
		if (null == dom_obj) {
			throw new IllegalArgumentException(msg);
		}
		return getVpnNetworkByDomainObject(dom_obj.getId());
	}
	
	/*
	 * DnsServiceProfile--> Domain Object
	 */
	private static String getDnsServiceByDomainObject(String dom_obj_id_query) {
		return "select DISTINCT id from dns_service_profile where domain_object_id in ("
				+ dom_obj_id_query + ")";
	}

	private static String getDnsServiceByDomainObject(DomainObject dom_obj) {
		if (null == dom_obj) {
			throw new IllegalArgumentException(msg);
		}
		return getDnsServiceByDomainObject(String.valueOf(dom_obj.getId()));
	}
	
	/*
	 * DnsServiceProfile--> IP Address
	 */
	private static String getDnsServiceByIpAddress(String ip_id_query) {
		return "select DISTINCT id from dns_service_profile where (external_dns1_id in ("
				+ ip_id_query + ") or external_dns2_id in ("
				+ ip_id_query + ") or external_dns3_id in ("
				+ ip_id_query + ") or internal_dns1_id in ("
				+ ip_id_query + ") or internal_dns2_id in ("
				+ ip_id_query + ") or internal_dns3_id in ("
				+ ip_id_query + ")) union select DISTINCT dns_service_id from dns_specific_settings where specificdns in ("
				+ ip_id_query + ")";
	}

	private static String getDnsServiceByIpAddress(IpAddress ip) {
		if (null == ip) {
			throw new IllegalArgumentException(msg);
		}
		return getDnsServiceByIpAddress(String.valueOf(ip.getId()));
	}
	
	public static Set<String> getRelevantConfigTemplateFromSsid(String ssidId) {
		String query = getConfigTemplateNameBySsid(ssidId);
		return executeQueryName(ConfigTemplate.class, query);
	}

	public static Set<Long> getRelevantHiveAp(HmBo bo) {
		Set<Long> set = new HashSet<Long>();
		if (null == bo) {
			return set;
		}
		if (bo instanceof HiveAp) {
			// if bo is HiveAp, this function return the HiveAPs bind this AP as
			// Neighbor
			set = getHiveAp((HiveAp) bo);
		} else if (bo instanceof HiveProfile) {
			set = getHiveAp((HiveProfile) bo);
		} else if (bo instanceof MacFilter) {
			set = getHiveAp((MacFilter) bo);
		} else if (bo instanceof MacOrOui) {
			set = getHiveAp((MacOrOui) bo);
		} else if (bo instanceof QosClassification) {
			set = getHiveAp((QosClassification) bo);
		} else if (bo instanceof QosMarking) {
			set = getHiveAp((QosMarking) bo);
		} else if (bo instanceof QosRateControl) {
			set = getHiveAp((QosRateControl) bo);
		} else if (bo instanceof NetworkService) {
			set = getHiveAp((NetworkService) bo);
		} else if (bo instanceof Vlan) {
			set = getHiveAp((Vlan) bo);
		} else if (bo instanceof RadiusAssignment) {
			set = getHiveAp((RadiusAssignment) bo);
		} else if (bo instanceof UserProfileAttribute) {
			set = getHiveAp((UserProfileAttribute) bo);
		} else if (bo instanceof UserProfile) {
			set = getHiveAp((UserProfile) bo);
		} else if (bo instanceof VpnNetwork) {
			set = getHiveAp((VpnNetwork) bo);
		} else if (bo instanceof IpAddress) {
			set = getHiveAp((IpAddress) bo);
		} else if (bo instanceof IpPolicy) {
			set = getHiveAp((IpPolicy) bo);
		} else if (bo instanceof MacPolicy) {
			set = getHiveAp((MacPolicy) bo);
		} else if (bo instanceof SsidProfile) {
			set = getHiveAp((SsidProfile) bo);
		} else if (bo instanceof Scheduler) {
			set = getHiveAp((Scheduler) bo);
		} else if (bo instanceof Cwp) {
			set = getHiveAp((Cwp) bo);
		} else if (bo instanceof RadioProfile) {
			set = getHiveAp((RadioProfile) bo);
		} else if (bo instanceof DosPrevention) {
			set = getHiveAp((DosPrevention) bo);
		} else if (bo instanceof IdsPolicy) {
			set = getHiveAp((IdsPolicy) bo);
		} else if (bo instanceof TunnelSetting) {
			set = getHiveAp((TunnelSetting) bo);
		} else if (bo instanceof IpFilter) {
			set = getHiveAp((IpFilter) bo);
		} else if (bo instanceof ServiceFilter) {
			set = getHiveAp((ServiceFilter) bo);
		} else if (bo instanceof MgmtServiceDns) {
			set = getHiveAp((MgmtServiceDns) bo);
		} else if (bo instanceof MgmtServiceSyslog) {
			set = getHiveAp((MgmtServiceSyslog) bo);
		} else if (bo instanceof MgmtServiceSnmp) {
			set = getHiveAp((MgmtServiceSnmp) bo);
		} else if (bo instanceof MgmtServiceTime) {
			set = getHiveAp((MgmtServiceTime) bo);
		} else if (bo instanceof MgmtServiceOption) {
			set = getHiveAp((MgmtServiceOption) bo);
		} else if (bo instanceof ConfigTemplate) {
			set = getHiveAp((ConfigTemplate) bo);
		} else if (bo instanceof RadiusOnHiveap) {
			set = getHiveAp((RadiusOnHiveap) bo);
		} else if (bo instanceof AlgConfiguration) {
			set = getHiveAp((AlgConfiguration) bo);
		}/*- else if (bo instanceof EthernetAccess) {
			set = getHiveAp((EthernetAccess) bo);
		} */else if (bo instanceof LocationServer) {
			set = getHiveAp((LocationServer) bo);
		}/*- else if (bo instanceof QosClassfierAndMarker) {
			set = getHiveAp((QosClassfierAndMarker) bo);
		} */else if (bo instanceof ActiveDirectoryOrOpenLdap) {
			set = getHiveAp((ActiveDirectoryOrOpenLdap) bo);
		} else if (bo instanceof LocalUserGroup) {
			set = getHiveAp((LocalUserGroup) bo);
		} else if (bo instanceof MgmtServiceIPTrack) {
			set = getHiveAp((MgmtServiceIPTrack) bo);
		} else if (bo instanceof AccessConsole) {
			set = getHiveAp((AccessConsole) bo);
		} else if (bo instanceof VlanDhcpServer) {
			set = getHiveAp((VlanDhcpServer) bo);
		} else if (bo instanceof LLDPCDPProfile) {
			set = getHiveAp((LLDPCDPProfile) bo);
		} else if (bo instanceof VpnService) {
			set = getHiveAp((VpnService) bo);
		} else if (bo instanceof HmStartConfig) {
			set = getHiveAp((HmStartConfig) bo);
		} else if (bo instanceof RadiusProxy) {
			set = getHiveAp((RadiusProxy) bo);
		} else if (bo instanceof RadiusLibrarySip) {
			set = getHiveAp((RadiusLibrarySip) bo);
		} else if (bo instanceof OsObject) {
			set = getHiveAp((OsObject) bo);
		} else if (bo instanceof FirewallPolicy) {
			set = getHiveAp((FirewallPolicy) bo);
		} else if (bo instanceof DomainObject) {
			set = getHiveAp((DomainObject) bo);
		} else if (bo instanceof DnsServiceProfile) {
			set = getHiveAp((DnsServiceProfile) bo);
		} else if (bo instanceof RadiusAttrs) {
            set = getHiveAp((RadiusAttrs) bo);
//        } else if (bo instanceof LanProfile) {
//		    set = getHiveAp((LanProfile)bo);
		} else if (bo instanceof HMServicesSettings) {
			set = getHiveAp((HMServicesSettings)bo);
		} else if(bo instanceof BonjourGatewaySettings){
			set = getHiveAp((BonjourGatewaySettings)bo);
		} else if(bo instanceof PortGroupProfile){
			set = getHiveAp((PortGroupProfile)bo);
		} else if(bo instanceof PortAccessProfile){
			set = getHiveAp((PortAccessProfile)bo);
		}
		log.info("getRelevantHiveAp", "BO '" + bo.getLabel()
				+ "' relevant HiveAp size:" + set.size());
		return set;
	}

    /**
	 * Get the description which will be persisted in DB
	 *
	 * @param bo -
	 * @param event -
	 * @return -
	 */
	public static String getDescription(HmBo bo, ConfigurationChangedEvent event) {
		if (null == bo) {
			return "";
		}
		String description = "";
		if (bo instanceof HiveProfile) {
			description = getValue(Navigation.L2_FEATURE_HIVE_PROFILES, bo
					.getLabel());
		} else if (bo instanceof MacFilter) {
			description = getValue(Navigation.L2_FEATURE_MAC_FILTERS, bo
					.getLabel());
		} else if (bo instanceof MacOrOui) {
			description = getValue(Navigation.L2_FEATURE_MAC_OR_OUI, bo
					.getLabel());
		} else if (bo instanceof QosClassification) {
			description = getValue(Navigation.L2_FEATURE_QOS_CLASSIFICATION, bo
					.getLabel());
		} else if (bo instanceof QosMarking) {
			description = getValue(Navigation.L2_FEATURE_QOS_MARKING, bo
					.getLabel());
		} else if (bo instanceof QosRateControl) {
			description = getValue(Navigation.L2_FEATURE_QOS_RATE_CONTROL, bo
					.getLabel());
		} else if (bo instanceof NetworkService) {
			description = getValue(Navigation.L2_FEATURE_NETWORK_SERVICE, bo
					.getLabel());
		} else if (bo instanceof Vlan) {
			description = getValue(Navigation.L2_FEATURE_VLAN, bo.getLabel());
		} else if (bo instanceof RadiusAssignment) {
			description = getValue(Navigation.L2_FEATURE_RADIUS_SERVER_ASSIGN,
					bo.getLabel());
		} else if (bo instanceof UserProfileAttribute) {
			description = getValue(
					Navigation.L2_FEATURE_USER_PROFILE_ATTRIBUTE, bo.getLabel());
		} else if (bo instanceof UserProfile) {
			description = getValue(Navigation.L2_FEATURE_USER_PROFILE, bo
					.getLabel());
			/*-} else if (bo instanceof RadiusUserProfileRule) {
				description = getValue(
						Navigation.L2_FEATURE_RADIUS_USER_PROFILE_RULE, bo
								.getLabel());
			 */
		} else if (bo instanceof IpAddress) {
			description = getValue(Navigation.L2_FEATURE_IP_ADDRESS, bo
					.getLabel());
		} else if (bo instanceof IpPolicy) {
			description = getValue(Navigation.L2_FEATURE_IP_POLICY, bo
					.getLabel());
		} else if (bo instanceof MacPolicy) {
			description = getValue(Navigation.L2_FEATURE_MAC_POLICY, bo
					.getLabel());
		} else if (bo instanceof SsidProfile) {
			description = getValue(Navigation.L2_FEATURE_SSID_PROFILES, bo
					.getLabel());
		} else if (bo instanceof Scheduler) {
			description = getValue(Navigation.L2_FEATURE_SCHEDULER, bo
					.getLabel());
		} else if (bo instanceof Cwp) {
			description = getValue(Navigation.L2_FEATURE_CAPTIVE_PORTAL_WEB, bo
					.getLabel());
		} else if (bo instanceof RadioProfile) {
			description = getValue(Navigation.L2_FEATURE_RADIO_PROFILE, bo
					.getLabel());
		} else if (bo instanceof DosPrevention) {
			description = getValue(Navigation.L2_FEATURE_DOSPREVENTION, bo
					.getLabel());
		} else if (bo instanceof IdsPolicy) {
			description = getValue(Navigation.L2_FEATURE_IDS_POLICY, bo
					.getLabel());
		} else if (bo instanceof TunnelSetting) {
			description = getValue(
					Navigation.L2_FEATURE_IDENTITY_BASED_TUNNELS, bo.getLabel());
		} else if (bo instanceof IpFilter) {
			description = getValue(Navigation.L2_FEATURE_IP_FILTERS, bo
					.getLabel());
		} else if (bo instanceof ServiceFilter) {
			description = getValue(Navigation.L2_FEATURE_SERVICE_FILTERS, bo
					.getLabel());
		} else if (bo instanceof MgmtServiceDns) {
			description = getValue(Navigation.L2_FEATURE_MGMT_SERVICE_DNS, bo
					.getLabel());
		} else if (bo instanceof MgmtServiceSyslog) {
			description = getValue(Navigation.L2_FEATURE_MGMT_SERVICE_SYSLOG,
					bo.getLabel());
		} else if (bo instanceof MgmtServiceSnmp) {
			description = getValue(Navigation.L2_FEATURE_MGMT_SERVICE_SNMP, bo
					.getLabel());
		} else if (bo instanceof MgmtServiceTime) {
			description = getValue(Navigation.L2_FEATURE_MGMT_SERVICE_TIME, bo
					.getLabel());
		} else if (bo instanceof MgmtServiceOption) {
			description = getValue(Navigation.L2_FEATURE_MGMT_SERVICE_OPTION,
					bo.getLabel());
		} else if (bo instanceof ConfigTemplate) {
			if (event.isExpressMode()) {
				description = "Policy configuration";
			} else {
				description = getValue(
					Navigation.L2_FEATURE_CONFIGURATION_TEMPLATE, bo.getLabel());
			}
		} else if (bo instanceof RadiusOnHiveap) {
			description = getValue(Navigation.L2_FEATURE_RADIUS_SERVER_HIVEAP,
					bo.getLabel());
		} else if (bo instanceof AlgConfiguration) {
			description = getValue(Navigation.L2_FEATURE_ALG_CONFIGURATION, bo
					.getLabel());
		}/*- else if (bo instanceof EthernetAccess) {
																																																															description = getValue(Navigation.L2_FEATURE_ETHERNET_ACCESS, bo
																																																																	.getLabel());
																																																														}*/else if (bo instanceof LocationServer) {
			description = getValue(Navigation.L2_FEATURE_LOCATION_SERVER, bo
					.getLabel());
		}/*- else if (bo instanceof QosClassfierAndMarker) {
																																													description = getValue(
																																															Navigation.L2_FEATURE_QOS_CLASSFIER_AND_MARKER, bo
																																																	.getLabel());
																																												} */else if (bo instanceof ActiveDirectoryOrOpenLdap) {
			description = getValue(
					Navigation.L2_FEATURE_RADIUS_ACTIVE_DIRECTORY, bo
							.getLabel());
		} else if (bo instanceof LocalUser) {
			description = getValue(Navigation.L2_FEATURE_LOCAL_USER, bo
					.getLabel());
		} else if (bo instanceof LocalUserGroup) {
			description = getValue(Navigation.L2_FEATURE_LOCAL_USER_GROUP, bo
					.getLabel());
		} else if (bo instanceof MgmtServiceIPTrack) {
			description = getValue(Navigation.L2_FEATURE_MGMT_IP_TRACKING, bo
					.getLabel());
		} else if (bo instanceof AccessConsole) {
			description = getValue(Navigation.L2_FEATURE_ACCESS_CONSOLE, bo
					.getLabel());
		} else if (bo instanceof VlanDhcpServer) {
			description = getValue(Navigation.L2_FEATURE_VLAN_DHCP_SERVER, bo
					.getLabel());
		} else if (bo instanceof LLDPCDPProfile) {
			description = getValue(Navigation.L2_FEATURE_LLDPCDP_PROFILE, bo
					.getLabel());
		} else if (bo instanceof VpnService) {
			description = getValue(Navigation.L2_FEATURE_VPN_SERVICE, bo
					.getLabel());
		} else if (bo instanceof HmStartConfig) {
			description = getValue(Navigation.L2_FEATURE_START_HERE, bo
					.getLabel());
		} else if (bo instanceof RadiusProxy) {
			description = getValue(Navigation.L2_FEATURE_RADIUS_SERVER_PROXY,
					bo.getLabel());
		} else if (bo instanceof VpnNetwork) {
			description = getValue(Navigation.L2_FEATURE_VPN_NETWORK,
					bo.getLabel());
		} else if (bo instanceof LanProfile) {
			description = getValue(Navigation.L2_FEATURE_LAN,
					bo.getLabel());
		} else if (bo instanceof SubNetworkResource) {
		    description = getValue(Navigation.L2_FEATURE_SUBNETWORK_ALLOCATIONS,
		            bo.getLabel());
		} else if (bo instanceof BonjourGatewaySettings){
			description = getValue(Navigation.L2_FEATURE_BONJOUR_GATEWAY_SETTINGS, bo.getLabel());
		} else if (bo instanceof DnsServiceProfile) {
			description = getValue(Navigation.L2_FEATURE_DNS_SERVICE, bo.getLabel());
		} else if (bo instanceof PortAccessProfile) {
			description = getValue(Navigation.L2_FEATURE_PORTTYPE, bo.getLabel());
		} else if (bo instanceof PortGroupProfile) {
			description = "Device Template item <" + ((PortGroupProfile)bo).getLabel() + ">";
		}
		log.debug("getDescription", description);
		return description;
	}

	private static String getValue(String featureKey, String boName) {
		return Navigation.getFeatureName(featureKey) + " item <" + boName + ">";
	}

	private static Set<Long> getHiveAp(HiveProfile hive) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByHive(hive));
		return executeQuery(HiveProfile.class, query);
	}
	
	private static Set<Long> getHiveAp(FirewallPolicy fwPolicy) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByFirewall(fwPolicy));
		return executeQuery(FirewallPolicy.class, query);
	}
	
	private static Set<Long> getHiveAp(PortGroupProfile portGroup) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByPortGroup(portGroup));
		return executeQuery(PortGroupProfile.class, query);
	}
	
	private static Set<Long> getHiveAp(PortAccessProfile portAccess) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByPortGroup(getPortGroupByPortAccess(portAccess)));
		return executeQuery(PortAccessProfile.class, query);
	}
	
	/*
	 * Configuration template-->Port Group
	 */
	private static String getConfigTemplateByPortGroup(String portgroup_id_query) {
		return "select DISTINCT config_template_id from CONFIG_TEMPLATE_PORT where PORTPROFILES_ID in ("
				+ portgroup_id_query + ")";
	}

	private static String getConfigTemplateByPortGroup(PortGroupProfile portGroup) {
		if (null == portGroup) {
			throw new IllegalArgumentException(msg);
		}
		return getConfigTemplateByPortGroup(String.valueOf(portGroup.getId()));
	}
	
	/*
	 * Configuration template-->Port Access
	 */
	private static String getPortGroupByPortAccess(String portAccess_id_query) {
		return "select DISTINCT PORTGROUPS_ID from PORT_BASIC_PROFILE where ACCESSPROFILE_ID in ("
				+ portAccess_id_query + ")";
	}

	private static String getPortGroupByPortAccess(PortAccessProfile portAccess) {
		if (null == portAccess) {
			throw new IllegalArgumentException(msg);
		}
		return getPortGroupByPortAccess(String.valueOf(portAccess.getId()));
	}
	
	

	private static Set<Long> getHiveAp(MacFilter macFilter) {
		Long start = System.currentTimeMillis();
		String query = getHiveApByConfigTemplate(getConfigTemplateByHive(getHiveProfileByMacFilter(macFilter)));
		Set<Long> set = executeQuery(MacFilter.class, query);
		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByMacFilter(macFilter)));
		set.addAll(executeQuery(MacFilter.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByMacFilter(macFilter))));
//		set.addAll(executeQuery(MacFilter.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByMacFilter(macFilter))));
//		set.addAll(executeQuery(MacFilter.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByAccessConsole(getAccessConsoleByMacFilter(macFilter)));
		set.addAll(executeQuery(MacFilter.class, query));
		Long end = System.currentTimeMillis();
		log
				.debug("getHiveAp", "Query MacFilter cost:" + (end - start)
						+ " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(MacOrOui macOrOui) {
		Long start = System.currentTimeMillis();
		String query = getHiveApByConfigTemplate(getConfigTemplateByHive(getHiveProfileByMacFilter(getMacFilterByMacOrOui(macOrOui))));
		Set<Long> set = executeQuery(MacOrOui.class, query);
		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByMacFilter(getMacFilterByMacOrOui(macOrOui))));
		set.addAll(executeQuery(MacOrOui.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByMacFilter(getMacFilterByMacOrOui(macOrOui)))));
//		set.addAll(executeQuery(MacOrOui.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationByMacOrOui(macOrOui)));
		set.addAll(executeQuery(MacOrOui.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyByMacOrOui(macOrOui)));
		set.addAll(executeQuery(MacOrOui.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByMacFilter(getMacFilterByMacOrOui(macOrOui)))));
//		set.addAll(executeQuery(MacOrOui.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByEthernetAccess(getEthernetAccessByMacOrOui(macOrOui)));
		// set.addAll(executeQuery(MacOrOui.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByEthernetAccess(getEthernetAccessByUserProfile(getUserProfileByMacPolicy(getMacPolicyByMacOrOui(macOrOui)))));
		// set.addAll(executeQuery(MacOrOui.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByMacPolicy(getMacPolicyByMacOrOui(macOrOui)))));
		set.addAll(executeQuery(MacOrOui.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByMacPolicy(getMacPolicyByMacOrOui(macOrOui))))));
//		set.addAll(executeQuery(MacOrOui.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByMacPolicy(getMacPolicyByMacOrOui(macOrOui))))));
//		set.addAll(executeQuery(MacOrOui.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByUserProfile(getUserProfileByMacPolicy(getMacPolicyByMacOrOui(macOrOui))));
		// set.addAll(executeQuery(MacOrOui.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByRadiusUserProfileRule(getRadiusUserProfileRuleByUserProfile(getUserProfileByMacPolicy(getMacPolicyByMacOrOui(macOrOui)))));
		// set.addAll(executeQuery(MacOrOui.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByAccessConsole(getAccessConsoleByMacFilter(getMacFilterByMacOrOui(macOrOui))));
		set.addAll(executeQuery(MacOrOui.class, query));
		query = getHiveApByMacOrOui(macOrOui);
		set.addAll(executeQuery(MacOrOui.class, query));
		query = getHiveApByUserProfile(getUserProfileByMacPolicy(getMacFilterByMacOrOui(macOrOui)));
		set.addAll(executeQuery(MacOrOui.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByMacOrOui(macOrOui))));
		set.addAll(executeQuery(MacOrOui.class, query));
		
		
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByMacOrOui(macOrOui))));
//		set.addAll(executeQuery(MacOrOui.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByMacPolicy(getMacPolicyByMacOrOui(macOrOui)))));
//		set.addAll(executeQuery(MacOrOui.class, query));
		
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query MacOrOui cost:" + (end - start) + " ms.");
		return set;
	}
	
	private static Set<Long> getHiveAp(OsObject osObj) {
		Long start = System.currentTimeMillis();
		String query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByOsObject(osObj))));
		Set<Long> set = executeQuery(OsObject.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByOsObject(osObj)))));
//		set.addAll(executeQuery(OsObject.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByOsObject(osObj)))));
//		set.addAll(executeQuery(OsObject.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByOsObject(osObj))));
//		set.addAll(executeQuery(OsObject.class, query));
		query = getHiveApByUserProfile(getUserProfileByOsObject(osObj));
		set.addAll(executeQuery(OsObject.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByFirewall(getFwPolicyByUserProfile(getUserProfileByOsObject(osObj))));
		set.addAll(executeQuery(OsObject.class, query));
		
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query OsObject cost:" + (end - start) + " ms.");
		return set;
	}
	
	private static Set<Long> getHiveAp(DomainObject domObj) {
		Long start = System.currentTimeMillis();
		/*
		 * User Profile
		 */
		String query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByDomainObject(domObj))));
		Set<Long> set = executeQuery(DomainObject.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByDomainObject(domObj)))));
//		set.addAll(executeQuery(DomainObject.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByDomainObject(domObj)))));
//		set.addAll(executeQuery(DomainObject.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByDomainObject(domObj))));
//		set.addAll(executeQuery(DomainObject.class, query));
		query = getHiveApByUserProfile(getUserProfileByDomainObject(domObj));
		set.addAll(executeQuery(DomainObject.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByFirewall(getFwPolicyByUserProfile(getUserProfileByDomainObject(domObj))));
		set.addAll(executeQuery(DomainObject.class, query));
		/*
		 * VPN Network
		 */
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByDomainObject(domObj)))))));
//		set.addAll(executeQuery(DomainObject.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByDomainObject(domObj)))))));
//		set.addAll(executeQuery(DomainObject.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByDomainObject(domObj))))));
//		set.addAll(executeQuery(DomainObject.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByDomainObject(domObj))))));
//		set.addAll(executeQuery(DomainObject.class, query));
//		query = getHiveApByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByDomainObject(domObj))));
//		set.addAll(executeQuery(DomainObject.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByFirewall(getFwPolicyByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByDomainObject(domObj)))));
		set.addAll(executeQuery(DomainObject.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByDomainObject(domObj))));
		set.addAll(executeQuery(DomainObject.class, query));
		query = getHiveApByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByDomainObject(domObj)));
		set.addAll(executeQuery(DomainObject.class, query));
		
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDomainObject(domObj))))));
//		set.addAll(executeQuery(DomainObject.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDomainObject(domObj))))));
//		set.addAll(executeQuery(DomainObject.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDomainObject(domObj)))));
//		set.addAll(executeQuery(DomainObject.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDomainObject(domObj)))));
//		set.addAll(executeQuery(DomainObject.class, query));
//		query = getHiveApByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDomainObject(domObj)));
//		set.addAll(executeQuery(DomainObject.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByFirewall(getFwPolicyByVpnNetwork(getVpnNetworkByDomainObject(domObj))));
		set.addAll(executeQuery(DomainObject.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByVpnNetwork(getVpnNetworkByDomainObject(domObj)));
		set.addAll(executeQuery(DomainObject.class, query));
		query = getHiveApByVpnNetwork(getVpnNetworkByDomainObject(domObj));
		set.addAll(executeQuery(DomainObject.class, query));
		/*
		 * VPN Service
		 */
		query = getConfigTemplateByVpnService(getVpnServiceByDomainObject(domObj));
		String vpnQuery = "select DISTINCT id from hive_ap where template_id in ("
				+ query + ") and (vpnmark = " + HiveAp.VPN_MARK_CLIENT + " or vpnmark = " + HiveAp.VPN_MARK_SERVER + ")";
		set.addAll(executeQuery(DomainObject.class, vpnQuery));
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query DomainObject cost:" + (end - start) + " ms.");
		return set;
	}
	
	private static Set<Long> getHiveAp(DnsServiceProfile dspObj) {
		Long start = System.currentTimeMillis();
		/*
		 * VPN Network
		 */
//		String query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDnsService(dspObj))))));
//		Set<Long> set = executeQuery(DnsServiceProfile.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDnsService(dspObj))))));
//		set.addAll(executeQuery(DnsServiceProfile.class, query));
//		String query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDnsService(dspObj)))));
//		Set<Long> set = executeQuery(DnsServiceProfile.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDnsService(dspObj)))));
//		set.addAll(executeQuery(DnsServiceProfile.class, query));
//		query = getHiveApByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDnsService(dspObj)));
//		set.addAll(executeQuery(DnsServiceProfile.class, query));
		String query = getHiveApByConfigTemplate(getConfigTemplateByFirewall(getFwPolicyByVpnNetwork(getVpnNetworkByDnsService(dspObj))));
		Set<Long> set = executeQuery(DnsServiceProfile.class, query);
		query = getHiveApByConfigTemplate(getConfigTemplateByVpnNetwork(getVpnNetworkByDnsService(dspObj)));
		set.addAll(executeQuery(DnsServiceProfile.class, query));
		query = getHiveApByVpnNetwork(getVpnNetworkByDnsService(dspObj));
		set.addAll(executeQuery(DnsServiceProfile.class, query));
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query DnsServiceProfile cost:" + (end - start) + " ms.");
		return set;
	}
	
	private static Set<Long> getHiveAp(HMServicesSettings hmObj) {
		Long start = System.currentTimeMillis();
		/*
		 * VPN Network
		 */
//		String query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByHMServices(hmObj))))));
//		Set<Long> set = executeQuery(HMServicesSettings.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByHMServices(hmObj))))));
//		set.addAll(executeQuery(HMServicesSettings.class, query));
//		String query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByHMServices(hmObj)))));
//		Set<Long> set = executeQuery(HMServicesSettings.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getLanByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByHMServices(hmObj)))));
//		set.addAll(executeQuery(HMServicesSettings.class, query));
//		query = getHiveApByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByHMServices(hmObj)));
//		set.addAll(executeQuery(HMServicesSettings.class, query));
		String query = getHiveApByConfigTemplate(getConfigTemplateByFirewall(getFwPolicyByVpnNetwork(getVpnNetworkByHMServices(hmObj))));
		Set<Long> set = executeQuery(HMServicesSettings.class, query);
		query = getHiveApByConfigTemplate(getConfigTemplateByVpnNetwork(getVpnNetworkByHMServices(hmObj)));
		set.addAll(executeQuery(HMServicesSettings.class, query));
		query = getHiveApByVpnNetwork(getVpnNetworkByHMServices(hmObj));
		set.addAll(executeQuery(HMServicesSettings.class, query));
		
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query HMServicesSettings cost:" + (end - start) + " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(QosClassification qos) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(qos));
		return executeQuery(QosClassification.class, query);
	}

	private static Set<Long> getHiveAp(QosMarking marking) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByQosMarking(marking));
		return executeQuery(QosMarking.class, query);
	}

	private static Set<Long> getHiveAp(QosRateControl control) {
		Long start = System.currentTimeMillis();
		// String query =
		// getHiveApByConfigTemplate(getConfigTemplateByUserProfile(getUserProfileByQosRateControl(control)));
//		String query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByQosRateControl(control)))));
//		Set<Long> set = executeQuery(QosRateControl.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByQosRateControl(control)))));
//		set.addAll(executeQuery(QosRateControl.class, query));
		String query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByQosRateControl(control))));
		Set<Long> set = executeQuery(QosRateControl.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByQosRateControl(control))));
//		set.addAll(executeQuery(QosRateControl.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByRadiusUserProfileRule(getRadiusUserProfileRuleByUserProfile(getUserProfileByQosRateControl(control))));
		// set.addAll(executeQuery(QosRateControl.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByEthernetAccess(getEthernetAccessByUserProfile(getUserProfileByQosRateControl(control))));
		// set.addAll(executeQuery(QosRateControl.class, query));
		query = getHiveApByUserProfile(getUserProfileByQosRateControl(control));
		set.addAll(executeQuery(QosRateControl.class, query));
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query QosRateControl cost:" + (end - start)
				+ " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(NetworkService service) {
		Long start = System.currentTimeMillis();
		String query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationByNetworkService(service)));
		Set<Long> set = executeQuery(NetworkService.class, query);
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByEthernetAccess(getEthernetAccessByUserProfile(getUserProfileByIpPolicy(getIpPolicyByNetworkService(service)))));
		// set.addAll(executeQuery(NetworkService.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByUserProfile(getUserProfileByIpPolicy(getIpPolicyByNetworkService(service))));
		// set.addAll(executeQuery(NetworkService.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByRadiusUserProfileRule(getRadiusUserProfileRuleByUserProfile(getUserProfileByIpPolicy(getIpPolicyByNetworkService(service)))));
		// set.addAll(executeQuery(NetworkService.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByIpPolicy(getIpPolicyByNetworkService(service))))));
//		set.addAll(executeQuery(NetworkService.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByIpPolicy(getIpPolicyByNetworkService(service))))));
//		set.addAll(executeQuery(NetworkService.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByIpPolicy(getIpPolicyByNetworkService(service)))));
		set.addAll(executeQuery(NetworkService.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByIpPolicy(getIpPolicyByNetworkService(service)))));
//		set.addAll(executeQuery(NetworkService.class, query));
		query = getHiveApByUserProfile(getUserProfileByIpPolicy(getIpPolicyByNetworkService(service)));
		set.addAll(executeQuery(NetworkService.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByFirewall(getFwPolicyByNetworkService(service)));
		set.addAll(executeQuery(NetworkService.class, query));
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query NetworkService cost:" + (end - start)
				+ " ms.");
		return set;
	}

    private static Set<Long> getHiveAp(Vlan vlan) {
		Long start = System.currentTimeMillis();
		String query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyByVlan(vlan)));
		Set<Long> set = executeQuery(Vlan.class, query);
		query = getHiveApByConfigTemplate(getConfigTemplateByVlan(vlan));
		set.addAll(executeQuery(Vlan.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByVlan(vlan)))));
//		set.addAll(executeQuery(Vlan.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByVlan(vlan)))));
//		set.addAll(executeQuery(Vlan.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByVlan(vlan))));
		set.addAll(executeQuery(Vlan.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByVlan(vlan))));
//		set.addAll(executeQuery(Vlan.class, query));
		query = getHiveApByUserProfile(getUserProfileByVlan(vlan));
		set.addAll(executeQuery(Vlan.class, query));
		query = getHiveApByVlan(vlan);
		set.addAll(executeQuery(Vlan.class, query));
		/*
		 * VPN Network
		 */
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByVlan(vlan))))));
//		set.addAll(executeQuery(Vlan.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByVlan(vlan))))));
//		set.addAll(executeQuery(Vlan.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByVlan(vlan)))));
//		set.addAll(executeQuery(Vlan.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByVlan(vlan)))));
//		set.addAll(executeQuery(Vlan.class, query));
//		query = getHiveApByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByVlan(vlan)));
//		set.addAll(executeQuery(Vlan.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByFirewall(getFwPolicyByVpnNetwork(getVpnNetworkByVlan(vlan))));
//		set.addAll(executeQuery(Vlan.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByVpnNetwork(getVpnNetworkByVlan(vlan)));
//		set.addAll(executeQuery(Vlan.class, query));
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query Vlan cost:" + (end - start) + " ms.");
		return set;
	}
	
	private static Set<Long> getHiveAp(VpnNetwork vpnNetwork) {
		Long start = System.currentTimeMillis();
		String query = getHiveApByConfigTemplate(getConfigTemplateByVpnNetwork(vpnNetwork));
		Set<Long> set = executeQuery(VpnNetwork.class, query);
		query = getHiveApByConfigTemplate(getConfigTemplateByFirewall(getFwPolicyByVpnNetwork(vpnNetwork)));
		set.addAll(executeQuery(VpnNetwork.class, query));
		query = getHiveApByVpnNetwork(vpnNetwork);
		set.addAll(executeQuery(VpnNetwork.class, query));
		
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query VpnNetwork cost:" + (end - start) + " ms.");
		return set;
	}

    private static Set<Long> getHiveAp(RadiusAssignment assignment) {
		Long start = System.currentTimeMillis();
		// String query =
		// getHiveApByConfigTemplate(getConfigTemplateByRadiusAssignment(assignment));
//		String query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByRadiusAssignment(assignment))));
//		Set<Long> set = executeQuery(RadiusAssignment.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByRadiusAssignment(assignment))));
//		set.addAll(executeQuery(RadiusAssignment.class, query));
		String query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByRadiusAssignment(assignment)));
		Set<Long> set = executeQuery(RadiusAssignment.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanProfileByRadiusAssignment(assignment)));
//		set.addAll(executeQuery(RadiusAssignment.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByMgmtServiceOption(getMgmtServiceOptionByRadiusAssignment(assignment)));
		set.addAll(executeQuery(RadiusAssignment.class, query));
		query = getHiveApByRadiusAssignment(assignment);
		set.addAll(executeQuery(RadiusAssignment.class, query));
		query = getHiveApByRadiusProxy(getRadiusProxyByRadiusAssignment(assignment));
		set.addAll(executeQuery(RadiusAssignment.class, query));
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query RadiusAssignment cost:" + (end - start)
				+ " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(RadiusProxy proxy) {
		Long start = System.currentTimeMillis();
		String query = getHiveApByRadiusProxy(proxy);
		Set<Long> set = executeQuery(RadiusProxy.class, query);
		query = getHiveApByConfigTemplate(getConfigTemplateByRadiusProxy(proxy));
		set.addAll(executeQuery(RadiusProxy.class, query));
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query RADIUS proxy cost:" + (end - start)
				+ " ms.");
		return set;
	}
	
	private static Set<Long> getHiveAp(RadiusLibrarySip sip) {
		Long start = System.currentTimeMillis();
		String query = getHiveApByRadiusOnHiveap(getRadiusOnHiveApByLibrarySip(sip));
		Set<Long> set = executeQuery(RadiusLibrarySip.class, query);
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query RADIUS library sip policy cost:" + (end - start)
				+ " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(UserProfileAttribute attribute) {
		Long start = System.currentTimeMillis();
		// String query =
		// getHiveApByConfigTemplate(getConfigTemplateByUserProfile(getUserProfileByUserProfileAttribute(attribute)));
//		String query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByUserProfileAttribute(attribute)))));
//		Set<Long> set = executeQuery(UserProfileAttribute.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByUserProfileAttribute(attribute)))));
//		set.addAll(executeQuery(UserProfileAttribute.class, query));
		String query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByUserProfileAttribute(attribute))));
		Set<Long> set = executeQuery(UserProfileAttribute.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByUserProfileAttribute(attribute))));
//		set.addAll(executeQuery(UserProfileAttribute.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByRadiusUserProfileRule(getRadiusUserProfileRuleByUserProfile(getUserProfileByUserProfileAttribute(attribute))));
		// set.addAll(executeQuery(UserProfileAttribute.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByEthernetAccess(getEthernetAccessByUserProfile(getUserProfileByUserProfileAttribute(attribute))));
		// set.addAll(executeQuery(UserProfileAttribute.class, query));
		query = getHiveApByUserProfile(getUserProfileByUserProfileAttribute(attribute));
		set.addAll(executeQuery(UserProfileAttribute.class, query));
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query UserProfileAttribute cost:"
				+ (end - start) + " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(UserProfile userProfile) {
		Long start = System.currentTimeMillis();
		// String query =
		// getHiveApByConfigTemplate(getConfigTemplateByUserProfile(userProfile));
//		String query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(userProfile))));
//		Set<Long> set = executeQuery(UserProfile.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(userProfile))));
//		set.addAll(executeQuery(UserProfile.class, query));
		String query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(userProfile)));
		Set<Long> set = executeQuery(UserProfile.class, query);
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByRadiusUserProfileRule(getRadiusUserProfileRuleByUserProfile(userProfile)));
		// set.addAll(executeQuery(UserProfileAttribute.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByEthernetAccess(getEthernetAccessByUserProfile(userProfile)));
		// set.addAll(executeQuery(UserProfile.class, query));
		query = getHiveApByUserProfile(userProfile);
		set.addAll(executeQuery(UserProfile.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByFirewall(getFwPolicyByUserProfile(userProfile)));
		set.addAll(executeQuery(UserProfile.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(userProfile)));
//		set.addAll(executeQuery(UserProfile.class, query));
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query UserProfile cost:" + (end - start)
				+ " ms.");
		return set;
	}

	/*-
	private static Set<Long> getHiveAp(RadiusUserProfileRule rule) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByRadiusUserProfileRule(rule));
		return executeQuery(RadiusUserProfileRule.class, query);
	}*/

	private static Set<Long> getHiveAp(IpAddress ip) {
		Long start = System.currentTimeMillis();
		String query = getHiveApByConfigTemplate(getConfigTemplateByMgmtServiceSnmp(getMgmtServiceSnmpByIpAddress(ip)));
		Set<Long> set = executeQuery(IpAddress.class, query);
		query = getHiveApByConfigTemplate(getConfigTemplateByMgmtServiceDns(getMgmtServiceDnsByIpAddress(ip)));
		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByMgmtServiceSyslog(getMgmtServiceSyslogByIpAddress(ip)));
		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByIpFilter(getIpFilterByIpAddress(ip)));
		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByIpAddress(ip);
		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByLocationServer(getLocationServerByIpAddress(ip)));
		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByMgmtServiceTime(getMgmtServiceTimeByIpAddress(ip)));
		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByRadiusOnHiveap(getRadiusOnHiveApByIpAddress(ip));
		set.addAll(executeQuery(IpAddress.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByRadiusAssignment(getRadiusAssignmentByIpAddress(ip)))));
//		set.addAll(executeQuery(IpAddress.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByRadiusAssignment(getRadiusAssignmentByIpAddress(ip)))));
//		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByRadiusAssignment(getRadiusAssignmentByIpAddress(ip))));
		set.addAll(executeQuery(IpAddress.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanProfileByRadiusAssignment(getRadiusAssignmentByIpAddress(ip))));
//		set.addAll(executeQuery(IpAddress.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByIpPolicy(getIpPolicyByIpAddress(ip))))));
//		set.addAll(executeQuery(IpAddress.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByIpPolicy(getIpPolicyByIpAddress(ip))))));
//		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByIpPolicy(getIpPolicyByIpAddress(ip)))));
		set.addAll(executeQuery(IpAddress.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByIpPolicy(getIpPolicyByIpAddress(ip)))));
//		set.addAll(executeQuery(IpAddress.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByTunnelSetting(getTunnelSettingByIpAddress(ip))))));
//		set.addAll(executeQuery(IpAddress.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByTunnelSetting(getTunnelSettingByIpAddress(ip))))));
//		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByTunnelSetting(getTunnelSettingByIpAddress(ip)))));
		set.addAll(executeQuery(IpAddress.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByTunnelSetting(getTunnelSettingByIpAddress(ip)))));
//		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByRadiusOnHiveap(getRadiusOnHiveApByActiveDirectoryOrOpenLdap(getActiveDirectoryOrOpenLdapByIpAddress(ip)));
		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByVpnService(getVpnServiceByIpAddress(ip)));
		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByUserProfile(getUserProfileByTunnelSetting(getTunnelSettingByIpAddress(ip)));
		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByUserProfile(getUserProfileByIpPolicy(getIpPolicyByIpAddress(ip)));
		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByRadiusAssignment(getRadiusAssignmentByIpAddress(ip));
		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByRadiusProxy(getRadiusProxyByIpAddress(ip));
		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByRadiusProxy(getRadiusProxyByRadiusAssignment(getRadiusAssignmentByIpAddress(ip)));
		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByFirewall(getFwPolicyByIpAddress(ip)));
		set.addAll(executeQuery(IpAddress.class, query));
		/*
		 * VPN Network
		 */
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByIpAddress(ip)))))));
//		set.addAll(executeQuery(IpAddress.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByIpAddress(ip)))))));
//		set.addAll(executeQuery(IpAddress.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByIpAddress(ip))))));
//		set.addAll(executeQuery(IpAddress.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByIpAddress(ip))))));
//		set.addAll(executeQuery(IpAddress.class, query));
//		query = getHiveApByUserProfile(getUserProfileByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByIpAddress(ip))));
//		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByFirewall(getFwPolicyByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByIpAddress(ip)))));
		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByIpAddress(ip))));
		set.addAll(executeQuery(IpAddress.class, query));
		query = getHiveApByVpnNetwork(getVpnNetworkByDnsService(getDnsServiceByIpAddress(ip)));
		set.addAll(executeQuery(IpAddress.class, query));
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query IpAddress cost:" + (end - start) + " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(IpPolicy policy) {
		Long start = System.currentTimeMillis();
		// String query =
		// getHiveApByConfigTemplate(getConfigTemplateByUserProfile(getUserProfileByIpPolicy(policy)));
//		String query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByIpPolicy(policy)))));
//		Set<Long> set = executeQuery(IpPolicy.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByIpPolicy(policy)))));
//		set.addAll(executeQuery(IpPolicy.class, query));
		String query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByIpPolicy(policy))));
		Set<Long> set = executeQuery(IpPolicy.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getLanByUserProfile(getUserProfileByIpPolicy(policy))));
//		set.addAll(executeQuery(IpPolicy.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByEthernetAccess(getEthernetAccessByUserProfile(getUserProfileByIpPolicy(policy))));
		// set.addAll(executeQuery(IpPolicy.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByRadiusUserProfileRule(getRadiusUserProfileRuleByUserProfile(getUserProfileByIpPolicy(policy))));
		// set.addAll(executeQuery(IpPolicy.class, query));
		query = getHiveApByUserProfile(getUserProfileByIpPolicy(policy));
		set.addAll(executeQuery(IpPolicy.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateByFirewall(getFwPolicyByUserProfile(getUserProfileByIpPolicy(policy))));
		set.addAll(executeQuery(IpPolicy.class, query));
		
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query IpPolicy cost:" + (end - start) + " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(MacPolicy policy) {
		Long start = System.currentTimeMillis();
		// String query =
		// getHiveApByConfigTemplate(getConfigTemplateByUserProfile(getUserProfileByMacPolicy(policy)));
//		String query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByMacPolicy(policy)))));
//		Set<Long> set = executeQuery(MacPolicy.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByMacPolicy(policy)))));
//		set.addAll(executeQuery(MacPolicy.class, query));
		String query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByMacPolicy(policy))));
		Set<Long> set = executeQuery(MacPolicy.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByMacPolicy(policy))));
//		set.addAll(executeQuery(MacPolicy.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByEthernetAccess(getEthernetAccessByUserProfile(getUserProfileByMacPolicy(policy))));
		// set.addAll(executeQuery(MacPolicy.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByRadiusUserProfileRule(getRadiusUserProfileRuleByUserProfile(getUserProfileByMacPolicy(policy))));
		// set.addAll(executeQuery(MacPolicy.class, query));
		query = getHiveApByUserProfile(getUserProfileByMacPolicy(policy));
		set.addAll(executeQuery(MacPolicy.class, query));
		Long end = System.currentTimeMillis();
		log
				.debug("getHiveAp", "Query MacPolicy cost:" + (end - start)
						+ " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(SsidProfile ssid) {
		Long start = System.currentTimeMillis();
//		String query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(ssid)));
//		Set<Long> set = executeQuery(SsidProfile.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(ssid)));
//		set.addAll(executeQuery(SsidProfile.class, query));
		String query = getHiveApByConfigTemplate(getConfigTemplateBySsid(ssid));
		Set<Long> set = executeQuery(SsidProfile.class, query);
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query SsidProfile cost:" + (end - start)
				+ " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(Scheduler scheduler) {
		Long start = System.currentTimeMillis();
//		String query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByScheduler(scheduler))));
//		Set<Long> set = executeQuery(Scheduler.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByScheduler(scheduler))));
//		set.addAll(executeQuery(Scheduler.class, query));
		String query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByScheduler(scheduler)));
		Set<Long> set = executeQuery(Scheduler.class, query);
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByUserProfile(getUserProfileByScheduler(scheduler)));
		// set.addAll(executeQuery(Scheduler.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByScheduler(scheduler)))));
//		set.addAll(executeQuery(Scheduler.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByScheduler(scheduler)))));
//		set.addAll(executeQuery(Scheduler.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByScheduler(scheduler))));
		set.addAll(executeQuery(Scheduler.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByScheduler(scheduler))));
//		set.addAll(executeQuery(Scheduler.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByLocalUserGroup(getLocalUserGroupByScheduler(scheduler)))));
//		set.addAll(executeQuery(Scheduler.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByLocalUserGroup(getLocalUserGroupByScheduler(scheduler)))));
//		set.addAll(executeQuery(Scheduler.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByLocalUserGroup(getLocalUserGroupByScheduler(scheduler))));
		set.addAll(executeQuery(Scheduler.class, query));
		query = getHiveApByRadiusOnHiveap(getRadiusOnHiveApByLocalUserGroup(getLocalUserGroupByScheduler(scheduler)));
		set.addAll(executeQuery(Scheduler.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByRadiusUserProfileRule(getRadiusUserProfileRuleByUserProfile(getUserProfileByScheduler(scheduler))));
		// set.addAll(executeQuery(LocalUserGroup.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByEthernetAccess(getEthernetAccessByUserProfile(getUserProfileByScheduler(scheduler))));
		// set.addAll(executeQuery(Scheduler.class, query));
		query = getHiveApByUserProfile(getUserProfileByScheduler(scheduler));
		set.addAll(executeQuery(Scheduler.class, query));
		Long end = System.currentTimeMillis();
		log
				.debug("getHiveAp", "Query Scheduler cost:" + (end - start)
						+ " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(Cwp cwp) {
		Long start = System.currentTimeMillis();
		/*- Cwp bo is associated with ConfigTemplate now.
		String query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByCwp(cwp))));
		Set<Long> set = executeQuery(Cwp.class, query);
		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByCwp(cwp))));
		set.addAll(executeQuery(Cwp.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByCwp(cwp)));
		set.addAll(executeQuery(Cwp.class, query));*/
		// String query =
		// getHiveApByConfigTemplate(getConfigTemplateByCwp(cwp));
//		String query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByCwp(cwp))));
//		Set<Long> set = executeQuery(Cwp.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByCwp(cwp))));
//		set.addAll(executeQuery(Cwp.class, query));
		String query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByCwp(cwp)));
		Set<Long> set = executeQuery(Cwp.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanProfileByCwp(cwp)));
//		set.addAll(executeQuery(Cwp.class, query));
		query = getHiveApByCwpProfile(cwp);
		set.addAll(executeQuery(Cwp.class, query));
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query Cwp cost:" + (end - start) + " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(RadioProfile radio) {
		String query = getHiveApByRadioProfile(radio);
		return executeQuery(RadioProfile.class, query);
	}

	private static Set<Long> getHiveAp(DosPrevention dos) {
		Long start = System.currentTimeMillis();
//		String query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByDosPrevention(dos))));
//		Set<Long> set = executeQuery(DosPrevention.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByDosPrevention(dos))));
//		set.addAll(executeQuery(DosPrevention.class, query));
		String query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByDosPrevention(dos)));
		Set<Long> set = executeQuery(DosPrevention.class, query);
		query = getHiveApByConfigTemplate(getConfigTemplateByHive(getHiveProfileByDosPrevention(dos)));
		set.addAll(executeQuery(DosPrevention.class, query));
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query DosPrevention cost:" + (end - start)
				+ " ms.");
		return set;
	}

	/*- Remove InterRoaming bo.
	private static Set<Long> getHiveAp(InterRoaming inter) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByHive(getHiveProfileByInterRoaming(inter)));
		return executeQuery(InterRoaming.class, query);
	}*/

	private static Set<Long> getHiveAp(IdsPolicy ids) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(ids));
		return executeQuery(IdsPolicy.class, query);
	}

	private static Set<Long> getHiveAp(TunnelSetting tunnel) {
		Long start = System.currentTimeMillis();
		// String query =
		// getHiveApByConfigTemplate(getConfigTemplateByUserProfile(getUserProfileByTunnelSetting(tunnel)));
//		String query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByUserProfile(getUserProfileByTunnelSetting(tunnel)))));
//		Set<Long> set = executeQuery(TunnelSetting.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByUserProfile(getUserProfileByTunnelSetting(tunnel)))));
//		set.addAll(executeQuery(TunnelSetting.class, query));
		String query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByUserProfile(getUserProfileByTunnelSetting(tunnel))));
		Set<Long> set = executeQuery(TunnelSetting.class, query);
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanByUserProfile(getUserProfileByTunnelSetting(tunnel))));
//		set.addAll(executeQuery(TunnelSetting.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByRadiusUserProfileRule(getRadiusUserProfileRuleByUserProfile(getUserProfileByTunnelSetting(tunnel))));
		// set.addAll(executeQuery(TunnelSetting.class, query));
		// query =
		// getHiveApByConfigTemplate(getConfigTemplateByEthernetAccess(getEthernetAccessByUserProfile(getUserProfileByTunnelSetting(tunnel))));
		// set.addAll(executeQuery(TunnelSetting.class, query));
		query = getHiveApByUserProfile(getUserProfileByTunnelSetting(tunnel));
		set.addAll(executeQuery(TunnelSetting.class, query));
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query TunnelSetting cost:" + (end - start)
				+ " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(IpFilter filter) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByIpFilter(filter));
		return executeQuery(IpFilter.class, query);
	}

	private static Set<Long> getHiveAp(ServiceFilter filter) {
		Long start = System.currentTimeMillis();
		String query = getHiveApByConfigTemplate(getConfigTemplateByServiceFilter(filter));
		Set<Long> set = executeQuery(ServiceFilter.class, query);
		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByServiceFilter(filter)));
		set.addAll(executeQuery(ServiceFilter.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(getLanProfileByServiceFilter(filter)));
//		set.addAll(executeQuery(ServiceFilter.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByServiceFilter(filter))));
//		set.addAll(executeQuery(ServiceFilter.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByServiceFilter(filter))));
//		set.addAll(executeQuery(ServiceFilter.class, query));
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query ServiceFilter cost:" + (end - start)
				+ " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(MgmtServiceDns dns) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByMgmtServiceDns(dns));
		return executeQuery(MgmtServiceDns.class, query);
	}

	private static Set<Long> getHiveAp(MgmtServiceSyslog syslog) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByMgmtServiceSyslog(syslog));
		return executeQuery(MgmtServiceSyslog.class, query);
	}

	private static Set<Long> getHiveAp(MgmtServiceSnmp snmp) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByMgmtServiceSnmp(snmp));
		return executeQuery(MgmtServiceSnmp.class, query);
	}

	private static Set<Long> getHiveAp(MgmtServiceTime time) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByMgmtServiceTime(time));
		return executeQuery(MgmtServiceTime.class, query);
	}

	private static Set<Long> getHiveAp(MgmtServiceOption option) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByMgmtServiceOption(option));
		return executeQuery(MgmtServiceOption.class, query);
	}

	private static Set<Long> getHiveAp(ConfigTemplate template) {
		String query = getHiveApByConfigTemplate(template);
		return executeQuery(ConfigTemplate.class, query);
	}

	private static Set<Long> getHiveAp(RadiusOnHiveap radius) {
		String query = getHiveApByRadiusOnHiveap(radius);
		Set<Long> set = executeQuery(RadiusOnHiveap.class, query);
		
		query = getHiveApByConfigTemplate(getConfigTemplateByRadiusOnHiveAp(radius));
		set.addAll(executeQuery(RadiusOnHiveap.class, query));
		return set;
	}

	private static Set<Long> getHiveAp(AlgConfiguration alg) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByAlgConfiguration(alg));
		return executeQuery(AlgConfiguration.class, query);
	}

	// private static Set<Long> getHiveAp(EthernetAccess access) {
	// String query =
	// getHiveApByConfigTemplate(getConfigTemplateByEthernetAccess(access));
	// return executeQuery(EthernetAccess.class, query);
	// }

	private static Set<Long> getHiveAp(LocationServer location) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByLocationServer(location));
		return executeQuery(LocationServer.class, query);
	}

	// private static Set<Long> getHiveAp(QosClassfierAndMarker marker) {
	// String query =
	// getHiveApByConfigTemplate(getConfigTemplateByQosClassfierAndMarker(marker));
	// return executeQuery(QosClassfierAndMarker.class, query);
	// }

	private static Set<Long> getHiveAp(ActiveDirectoryOrOpenLdap ldap) {
		String query = getHiveApByRadiusOnHiveap(getRadiusOnHiveApByActiveDirectoryOrOpenLdap(ldap));
		return executeQuery(ActiveDirectoryOrOpenLdap.class, query);
	}

	private static Set<Long> getHiveAp(LocalUserGroup group) {
		Long start = System.currentTimeMillis();
		String query = getHiveApByRadiusOnHiveap(getRadiusOnHiveApByLocalUserGroup(group));
		Set<Long> set = executeQuery(LocalUserGroup.class, query);
		query = getHiveApByRadiusOnHiveap(getRadiusOnHiveApByLibrarySip(getLibrarySipByLocalUserGroup(group)));
		set.addAll(executeQuery(LocalUserGroup.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByQosClassification(getQosClassificationBySsid(getSsidProfileByLocalUserGroup(group))));
//		set.addAll(executeQuery(LocalUserGroup.class, query));
//		query = getHiveApByConfigTemplate(getConfigTemplateByIdsPolicy(getIdsPolicyBySsid(getSsidProfileByLocalUserGroup(group))));
//		set.addAll(executeQuery(LocalUserGroup.class, query));
		query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByLocalUserGroup(group)));
		set.addAll(executeQuery(LocalUserGroup.class, query));
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query LocalUserGroup cost:" + (end - start)
				+ " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(HiveAp neighborHiveAp) {
		String query = getHiveApByNeighborHiveAp(neighborHiveAp);
		return executeQuery(HiveAp.class, query);
	}

	private static Set<Long> getHiveAp(MgmtServiceIPTrack ipTrack) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByIpTracking(ipTrack));
		Set<Long> set= executeQuery(MgmtServiceIPTrack.class, query);
		query = getHiveApByConfigTemplate(getConfigTemplateByIpTrackingRouter(ipTrack));
		set.addAll(executeQuery(MgmtServiceIPTrack.class, query));
		if (null != ipTrack) {
			query = getHiveApByIpTracking(String.valueOf(ipTrack.getId()));
			set.addAll(executeQuery(MgmtServiceIPTrack.class, query));
		}
		return set;
	}

	private static Set<Long> getHiveAp(VlanDhcpServer dhcpServer) {
		String query = getHiveApByDhcpServer(dhcpServer);
		return executeQuery(VlanDhcpServer.class, query);
	}

	private static Set<Long> getHiveAp(AccessConsole ac) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByAccessConsole(ac));
		return executeQuery(AccessConsole.class, query);
	}

	private static Set<Long> getHiveAp(RadiusAttrs ra) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByRadiusAttrs(ra));
		return executeQuery(RadiusAttrs.class, query);
	}

	private static Set<Long> getHiveAp(LLDPCDPProfile lp) {
		String query = getHiveApByConfigTemplate(getConfigTemplateByLldpCdp(lp));
		return executeQuery(LLDPCDPProfile.class, query);
	}

	private static Set<Long> getHiveAp(VpnService vpn) {
		Long start = System.currentTimeMillis();
		Set<Long> set = new HashSet<Long>();
		Set<Long> clients = getRelevantVpnClients(vpn);
		Set<Long> servers = getRelevantVpnServers(vpn);
		set.addAll(clients);
		set.addAll(servers);
		Long end = System.currentTimeMillis();
		log.debug("getHiveAp", "Query VPN Service cost:" + (end - start)
				+ " ms.");
		return set;
	}

	private static Set<Long> getHiveAp(HmStartConfig hsc) {
		Set<Long> set = new HashSet<Long>();
		if (hsc.getModeType() == HmStartConfig.HM_MODE_EASY) {
			set = executeQuery(HiveAp.class, getAllHiveAps(hsc.getOwner()
					.getId()));
		}
		return set;
	}
	/*-------------------- Bonjour Gateway Porfile start--------------*/
	private static Set<Long> getHiveAp(BonjourGatewaySettings bonjourGatewaySettings) {
		Long start = System.currentTimeMillis();
        String query = getHiveApByConfigTemplate(getConfigTemplateByLBwProfile(bonjourGatewaySettings));
        Set<Long> set = executeQuery(BonjourGatewaySettings.class, query);
        Long end = System.currentTimeMillis();
        log.debug("getHiveAp", "Query BonjourGatewaySettings cost:" + (end - start) + "ms.");
        return set;
	}
	
	 private static String getConfigTemplateByLBwProfile(BonjourGatewaySettings bonjourGatewaySettings) {
        if(null == bonjourGatewaySettings) {
            throw new IllegalArgumentException(msg);
        }
        return getConfigTemplateByLBwProfile(String.valueOf(bonjourGatewaySettings.getId()));
    }
	
    private static String getConfigTemplateByLBwProfile(String bw_ids) {
    	return "select DISTINCT id from config_template where bonjour_gateway_id in ("
			+ bw_ids + ")";
    }
	 /*-------------------- Bonjour Gateway Porfile end--------------*/
	 
	/*-------------------- LanProfile ---------------------------*/
//    private static Set<Long> getHiveAp(LanProfile lanProfile) {
//        Long start = System.currentTimeMillis();
//        String query = getHiveApByConfigTemplate(getConfigTemplateByLanProfile(lanProfile));
//        Set<Long> set = executeQuery(LanProfile.class, query);
//        Long end = System.currentTimeMillis();
//        log.debug("getHiveAp", "Query LanProfile cost:" + (end - start) + "ms.");
//        return set;
//    }
//    
//    private static String getConfigTemplateByLanProfile(LanProfile lanProfile) {
//        if(null == lanProfile) {
//            throw new IllegalArgumentException(msg);
//        }
//        return getConfigTemplateByLanProfile(String.valueOf(lanProfile.getId()));
//    }
//
//    private static String getConfigTemplateByLanProfile(String lan_ids) {
//        return "select DISTINCT config_template_id from config_template_lan where lanprofiles_id in ("
//                + lan_ids + ")";
//    }
    
    private static String getConfigTemplateByVpnNetwork(VpnNetwork vpnNetwork) {
        if(null == vpnNetwork) {
            throw new IllegalArgumentException(msg);
        }
        return getConfigTemplateByVpnNetwork(String.valueOf(vpnNetwork.getId()));
    }
    
    private static String getConfigTemplateByVpnNetwork(String network_ids) {
        return "select DISTINCT CONFIG_TEMPLATE_ID from CONFIG_TEMPLATE_VLANNETWORK where VPN_NETWORK_ID in ("
                + network_ids + ")";
    }

//    private static String getLanByVpnNetwork(VpnNetwork vpnNetwork) {
//        if (null == vpnNetwork) {
//            throw new IllegalArgumentException(msg);
//        }
//        return getLanByVpnNetwork(String.valueOf(vpnNetwork.getId()));
//    }
//
//    private static String getLanByVpnNetwork(String vpnNetwork_ids) {
//        return "select DISTINCT id from lan_profile where native_network_id in ("
//                + vpnNetwork_ids
//                + ") union select DISTINCT lan_profile_id from lan_profile_regular_networks where networks_id in ("
//                + vpnNetwork_ids + ")";
//    }
    
//	private static String getLanByUserProfile(String user_profile_id_query) {
//		return "select DISTINCT id from lan_profile where userprofile_default_id in ("
//				+ user_profile_id_query
//				+ ") union select DISTINCT id from lan_profile where userprofile_selfreg_id in ("
//				+ user_profile_id_query
//				+ ") union select DISTINCT lan_profile_id from lan_profile_user_profile where user_profile_id in ("
//				+ user_profile_id_query
//				+ ") union select DISTINCT id from lan_profile where userprofile_default_id in ("
//				+ getUserProfileByUserProfile(user_profile_id_query)
//				+ ") union select DISTINCT id from lan_profile where userprofile_selfreg_id in ("
//				+ getUserProfileByUserProfile(user_profile_id_query)
//				+ ") union select DISTINCT lan_profile_id from lan_profile_user_profile where user_profile_id in ("
//				+ getUserProfileByUserProfile(user_profile_id_query)
//				+ ")";
//	}
	
//	private static String getLanByUserProfile(UserProfile userProfile) {
//		if (null == userProfile) {
//			throw new IllegalArgumentException(msg);
//		}
//		return getLanByUserProfile(String.valueOf(userProfile.getId()));
//	}
	
    /*-------------------- LanProfile ---------------------------**/

	private static Set<Long> executeQuery(Class<? extends HmBo> srcClass,
			String query) {
		log.debug("executeQuery", "Query for:" + srcClass.getSimpleName()
				+ ", sql:" + query);
		Set<Long> set = new HashSet<Long>();
		List<?> list = QueryUtil.executeNativeQuery(query);
		for (Object object : list) {
			set.add(((BigInteger) object).longValue());
		}
		log.debug("executeQuery", "The above query sql effect object count:"
				+ set.size());
		return set;
	}

	private static Set<String> executeQueryName(Class<? extends HmBo> srcClass,
			String query) {
		log.debug("executeQuery", "Query for:" + srcClass.getSimpleName()
				+ ", sql:" + query);
		Set<String> set = new HashSet<String>();
		List<?> list = QueryUtil.executeNativeQuery(query);
		for (Object object : list) {
			set.add(object.toString());
		}
		log.debug("executeQuery", "The above query sql effect object count:"
				+ set.size());
		return set;
	}
	
	private static List<?> executeQueryProperties(Class<? extends HmBo> srcClass,
			String query) {
		log.debug("executeQuery", "Query for:" + srcClass.getSimpleName()
				+ ", sql:" + query);
		List<?> list = QueryUtil.executeNativeQuery(query);
		log.debug("executeQuery", "The above query sql effect object count:"
				+ list.size());
		return list;
	}

	public static Set<Long> getSsidProfiles(LocalUserGroup group) {
		String query = getSsidProfileByLocalUserGroup(group);
		return executeQuery(SsidProfile.class, query);
	}

	public static List<Long> getConfigTemplates(LocalUserGroup group) {
		String query = getConfigTemplateBySsidNoDistinct(getSsidProfileByLocalUserGroup(group));
		return new ArrayList<Long>(executeQuery(ConfigTemplate.class, query));
	}

	public static Set<Long> getConfigTemplates(SsidProfile ssid) {
		String query = getConfigTemplateBySsid(ssid);
		return executeQuery(ConfigTemplate.class, query);
	}

	/* for map container used query */
	public static Set<String> getRelevantIpAddressName(MapContainerNode container) {
		if (null == container) {
			return null;
		}
		String query = "select DISTINCT bo2.addressname from ip_address_item bo1, ip_address bo2 "
				+ "where bo1.ip_address_id = bo2.id and bo1.location_id in ("
				+ String.valueOf(container.getId()) + ")";
		return executeQueryName(MapContainerNode.class, query);
	}

	public static Set<String> getRelevantMacAddressName(MapContainerNode container) {
		if (null == container) {
			return null;
		}
		String query = "select DISTINCT bo2.macorouiname from mac_or_oui_item bo1, mac_or_oui bo2 "
				+ "where bo1.mac_or_oui_id = bo2.id and bo1.location_id in ("
				+ String.valueOf(container.getId()) + ")";
		return executeQueryName(MapContainerNode.class, query);
	}

	public static Set<String> getRelevantVlanName(MapContainerNode container) {
		if (null == container) {
			return null;
		}
		String query = "select DISTINCT bo2.vlanname from vlan_item bo1, vlan bo2 "
				+ "where bo1.vlan_id = bo2.id and bo1.location_id in ("
				+ String.valueOf(container.getId()) + ")";
		return executeQueryName(MapContainerNode.class, query);
	}

	public static Set<String> getRelevantUserAttributeName(MapContainerNode container) {
		if (null == container) {
			return null;
		}
		String query = "select DISTINCT bo2.attributename from attribute_item bo1, user_profile_attribute bo2 "
				+ "where bo1.attribute_id = bo2.id and bo1.location_id in ("
				+ String.valueOf(container.getId()) + ")";
		return executeQueryName(MapContainerNode.class, query);
	}

	public static Set<String> getRelevantLocationClientWatchName(
			MapContainerNode container) {
		if (null == container) {
			return null;
		}
		String query = "select DISTINCT bo2.name from locationclient_item bo1, locationclientwatch bo2 "
				+ "where bo1.locationclientwatch_id = bo2.id and bo1.location_id in ("
				+ String.valueOf(container.getId()) + ")";
		return executeQueryName(MapContainerNode.class, query);
	}

	public static Set<String> getRelevantCustomizedReportName(
			MapContainerNode container) {
		if (null == container) {
			return null;
		}
		String query = "select name from hm_custom_report where location_id = "
				+ String.valueOf(container.getId());
		return executeQueryName(MapContainerNode.class, query);
	}

	public static Set<AhReport> getRelevantReports(MapContainerNode container) {
		if (null == container) {
			return null;
		}
		String query = "select name, reporttype from hm_report where location_id = "
				+ String.valueOf(container.getId());
		List<?> list = executeQueryProperties(MapContainerNode.class, query);
		Set<AhReport> set = new HashSet<AhReport>();
		for (Object object : list) {
			Object[] obj = (Object[]) object;
			String name = (String) obj[0];
			String type = (String) obj[1];
			AhReport report = new AhReport();
			report.setName(name);
			report.setReportType(type);
			set.add(report);
		}
		return set;
	}
	
	public static Set<AhNewReport> getRelevantNewReports(MapContainerNode container) {
		if (null == container) {
			return null;
		}
		String query = "select name from hm_new_report where location_id = "
				+ String.valueOf(container.getId());
		List<?> list = executeQueryProperties(MapContainerNode.class, query);
		Set<AhNewReport> set = new HashSet<AhNewReport>();
		for (Object object : list) {
			String name = object.toString();
			AhNewReport report = new AhNewReport();
			report.setName(name);
			set.add(report);
		}
		return set;
	}
	
	public static Set<AhDashboard> getRelevantDashboardReports(MapContainerNode container) {
		if (null == container) {
			return null;
		}
		String query ="select distinct dashname,userName from hm_dashboard where objectType='-1' and objectId='" + String.valueOf(container.getId()) + "'";
		query =query + " union select distinct dashname,userName from hm_dashboard d,hm_dashboard_layout l,hm_dashboard_widget w ";
		query =query + " where d.id=l.dashboard_id and l.id=w.da_layout_id and w.objectType='-1' and w.objectId='" + String.valueOf(container.getId()) + "'";
	
		List<?> list = executeQueryProperties(MapContainerNode.class, query);
		Set<AhDashboard> set = new HashSet<AhDashboard>();
		for (Object object : list) {
			Object[] oneItem = (Object[])object;
			if (oneItem[0]==null || oneItem[0].toString().equals("")) {
				continue;
			}
			if (oneItem[1]!=null && !oneItem[1].toString().equals("")) {
				HmUser user= QueryUtil.findBoByAttribute(HmUser.class, "userName", oneItem[1].toString(), container.getOwner().getId());
				if (user==null) {
					continue;
				}
			}
			String name = oneItem[0].toString();
			AhDashboard report = new AhDashboard();
			report.setDashName(name);
			if (oneItem[1]!=null && !oneItem[1].toString().equals("")) {
				report.setUserName(oneItem[1].toString());
			}
			set.add(report);
		}
		return set;
	}
	
	/* end */

	/* for vpn used */
	public static Set<Long> getRelevantVpnClients(VpnService vpn) {
		String query = getConfigTemplateByVpnService(vpn);
		String clientQuery = "select DISTINCT id from hive_ap where template_id in ("
				+ query + ") and vpnmark = " + HiveAp.VPN_MARK_CLIENT;
		return executeQuery(VpnService.class, clientQuery);
	}

	public static Set<Long> getRelevantVpnClients(ConfigTemplate template) {
		String query = String.valueOf(template.getId());
		String clientQuery = "select DISTINCT id from hive_ap where template_id in ("
				+ query + ") and vpnmark = " + HiveAp.VPN_MARK_CLIENT;
		return executeQuery(ConfigTemplate.class, clientQuery);
	}

	public static Set<Long> getRelevantVpnServers(VpnService vpn) {
		String query = getConfigTemplateByVpnService(vpn);
		String serverQuery;
		if(vpn.getIpsecVpnType() == VpnService.IPSEC_VPN_LAYER_3){
			serverQuery = "select hiveapid from vpn_gateway_setting where vpn_gateway_setting_id = "+vpn.getId();
			return executeQuery(VpnService.class, serverQuery);
		}else{
			serverQuery = "select DISTINCT id from hive_ap where template_id in ("
				+ query + ") and (vpnmark = " + HiveAp.VPN_MARK_SERVER + ")";
			return executeQuery(VpnService.class, serverQuery);
		}
		
	}
	/* end */

	public static Set<Long> getHiveAPsByLocalUser(LocalUser user) {
		if (user == null || user.getLocalUserGroup() == null) {
			return null;
		}

		String query;
		Set<Long> hiveaps;

		if (user.getLocalUserGroup().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_RADIUS) {
			query = getHiveApByRadiusOnHiveap(getRadiusOnHiveApByLocalUserGroup(user
					.getLocalUserGroup()));
			hiveaps = executeQuery(LocalUserGroup.class, query);

		} else {
			query = getHiveApByConfigTemplate(getConfigTemplateBySsid(getSsidProfileByLocalUserGroup(user
					.getLocalUserGroup())));
			hiveaps = executeQuery(LocalUserGroup.class, query);
		}

		return hiveaps;
	}

}