/**
 *
 * Template Name: seQueryBo.ftl
 * FreeMarker version: 2.3.19
 * Generate Time: Nov 29, 2013 11:21:17 AM 
 *
 */
package com.ah.be.search.querybo;

import java.util.Collection;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAccessControl;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.hiveap.HiveApUpdateResult;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.hiveap.IdpSettings;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.mobility.TunnelSetting;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.monitor.Trex;
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
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacOrOui;
import com.ah.bo.network.MacPolicy;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.RadiusAttrs;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.bo.network.USBModem;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VlanDhcpServer;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnService;
import com.ah.bo.performance.AhCustomReport;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.teacherView.TvClass;
import com.ah.bo.teacherView.TvComputerCart;
import com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap;
import com.ah.bo.useraccess.MgmtServiceDns;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.bo.useraccess.MgmtServiceSnmp;
import com.ah.bo.useraccess.MgmtServiceSyslog;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.RadiusLibrarySip;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.useraccess.RadiusUserProfileRule;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.CwpPageCustomization;
import com.ah.bo.wlan.EthernetAccess;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.SsidProfile;

public class SearchEngineLazyLoad implements QueryBo {

    @Override
    public Collection<HmBo> load(HmBo bo) {
        if (null == bo) {
            return null;
        }
        
        if (bo instanceof MapContainerNode) {
            MapContainerNode hmbo = (MapContainerNode) bo;
            loadMapContainerNode(hmbo); 
        }
        else if (bo instanceof MapLeafNode) {
            MapLeafNode hmbo = (MapLeafNode) bo;
            loadMapLeafNode(hmbo); 
        }
        else if (bo instanceof HiveAp) {
            HiveAp hmbo = (HiveAp) bo;
            loadHiveAp(hmbo); 
        }
        else if (bo instanceof ConfigTemplate) {
            ConfigTemplate hmbo = (ConfigTemplate) bo;
            loadConfigTemplate(hmbo); 
        }
        else if (bo instanceof UserProfile) {
            UserProfile hmbo = (UserProfile) bo;
            loadUserProfile(hmbo); 
        }
        else if (bo instanceof QosClassification) {
            QosClassification hmbo = (QosClassification) bo;
            loadQosClassification(hmbo); 
        }
        else if (bo instanceof IdsPolicy) {
            IdsPolicy hmbo = (IdsPolicy) bo;
            loadIdsPolicy(hmbo); 
        }
        else if (bo instanceof EthernetAccess) {
            EthernetAccess hmbo = (EthernetAccess) bo;
            loadEthernetAccess(hmbo); 
        }
        else if (bo instanceof HiveProfile) {
            HiveProfile hmbo = (HiveProfile) bo;
            loadHiveProfile(hmbo); 
        }
        else if (bo instanceof SsidProfile) {
            SsidProfile hmbo = (SsidProfile) bo;
            loadSsidProfile(hmbo); 
        }
        else if (bo instanceof PortAccessProfile) {
            PortAccessProfile hmbo = (PortAccessProfile) bo;
            loadPortAccessProfile(hmbo); 
        }
        else if (bo instanceof VpnNetwork) {
            VpnNetwork hmbo = (VpnNetwork) bo;
            loadVpnNetwork(hmbo); 
        }
        else if (bo instanceof USBModem) {
            USBModem hmbo = (USBModem) bo;
            loadUSBModem(hmbo); 
        }
        else if (bo instanceof MacFilter) {
            MacFilter hmbo = (MacFilter) bo;
            loadMacFilter(hmbo); 
        }
        else if (bo instanceof IpFilter) {
            IpFilter hmbo = (IpFilter) bo;
            loadIpFilter(hmbo); 
        }
        else if (bo instanceof IpPolicy) {
            IpPolicy hmbo = (IpPolicy) bo;
            loadIpPolicy(hmbo); 
        }
        else if (bo instanceof MacPolicy) {
            MacPolicy hmbo = (MacPolicy) bo;
            loadMacPolicy(hmbo); 
        }
        else if (bo instanceof RadiusOnHiveap) {
            RadiusOnHiveap hmbo = (RadiusOnHiveap) bo;
            loadRadiusOnHiveap(hmbo); 
        }
        else if (bo instanceof AccessConsole) {
            AccessConsole hmbo = (AccessConsole) bo;
            loadAccessConsole(hmbo); 
        }
        else if (bo instanceof RadiusUserProfileRule) {
            RadiusUserProfileRule hmbo = (RadiusUserProfileRule) bo;
            loadRadiusUserProfileRule(hmbo); 
        }
        else if (bo instanceof VlanDhcpServer) {
            VlanDhcpServer hmbo = (VlanDhcpServer) bo;
            loadVlanDhcpServer(hmbo); 
        }
        else if (bo instanceof TunnelSetting) {
            TunnelSetting hmbo = (TunnelSetting) bo;
            loadTunnelSetting(hmbo); 
        }
        else if (bo instanceof RadiusAssignment) {
            RadiusAssignment hmbo = (RadiusAssignment) bo;
            loadRadiusAssignment(hmbo); 
        }
        else if (bo instanceof AlgConfiguration) {
            AlgConfiguration hmbo = (AlgConfiguration) bo;
            loadAlgConfiguration(hmbo); 
        }
        else if (bo instanceof HmAccessControl) {
            HmAccessControl hmbo = (HmAccessControl) bo;
            loadHmAccessControl(hmbo); 
        }
        else if (bo instanceof DosPrevention) {
            DosPrevention hmbo = (DosPrevention) bo;
            loadDosPrevention(hmbo); 
        }
        else if (bo instanceof UserProfileAttribute) {
            UserProfileAttribute hmbo = (UserProfileAttribute) bo;
            loadUserProfileAttribute(hmbo); 
        }
        else if (bo instanceof AhCustomReport) {
            AhCustomReport hmbo = (AhCustomReport) bo;
            loadAhCustomReport(hmbo); 
        }
        else if (bo instanceof Cwp) {
            Cwp hmbo = (Cwp) bo;
            loadCwp(hmbo); 
        }
        else if (bo instanceof RadioProfile) {
            RadioProfile hmbo = (RadioProfile) bo;
            loadRadioProfile(hmbo); 
        }
        else if (bo instanceof VpnService) {
            VpnService hmbo = (VpnService) bo;
            loadVpnService(hmbo); 
        }
        else if (bo instanceof HmUserGroup) {
            HmUserGroup hmbo = (HmUserGroup) bo;
            loadHmUserGroup(hmbo); 
        }
        else if (bo instanceof HiveApUpdateResult) {
            HiveApUpdateResult hmbo = (HiveApUpdateResult) bo;
            loadHiveApUpdateResult(hmbo); 
        }
        else if (bo instanceof TvClass) {
            TvClass hmbo = (TvClass) bo;
            loadTvClass(hmbo); 
        }
        else if (bo instanceof TvComputerCart) {
            TvComputerCart hmbo = (TvComputerCart) bo;
            loadTvComputerCart(hmbo); 
        }
        else if (bo instanceof MgmtServiceDns) {
            MgmtServiceDns hmbo = (MgmtServiceDns) bo;
            loadMgmtServiceDns(hmbo); 
        }
        else if (bo instanceof MgmtServiceSyslog) {
            MgmtServiceSyslog hmbo = (MgmtServiceSyslog) bo;
            loadMgmtServiceSyslog(hmbo); 
        }
        else if (bo instanceof MgmtServiceSnmp) {
            MgmtServiceSnmp hmbo = (MgmtServiceSnmp) bo;
            loadMgmtServiceSnmp(hmbo); 
        }
        else if (bo instanceof MgmtServiceTime) {
            MgmtServiceTime hmbo = (MgmtServiceTime) bo;
            loadMgmtServiceTime(hmbo); 
        }
        else if (bo instanceof MgmtServiceOption) {
            MgmtServiceOption hmbo = (MgmtServiceOption) bo;
            loadMgmtServiceOption(hmbo); 
        }
        else if (bo instanceof QosRateControl) {
            QosRateControl hmbo = (QosRateControl) bo;
            loadQosRateControl(hmbo); 
        }
        else if (bo instanceof IdpSettings) {
            IdpSettings hmbo = (IdpSettings) bo;
            loadIdpSettings(hmbo); 
        }
        else if (bo instanceof HiveApAutoProvision) {
            HiveApAutoProvision hmbo = (HiveApAutoProvision) bo;
            loadHiveApAutoProvision(hmbo); 
        }
        else if (bo instanceof RadiusProxy) {
            RadiusProxy hmbo = (RadiusProxy) bo;
            loadRadiusProxy(hmbo); 
        }
        else if (bo instanceof Idp) {
            Idp hmbo = (Idp) bo;
            loadIdp(hmbo); 
        }
        else if (bo instanceof LocationClientWatch) {
            LocationClientWatch hmbo = (LocationClientWatch) bo;
            loadLocationClientWatch(hmbo); 
        }
        else if (bo instanceof Vlan) {
            Vlan hmbo = (Vlan) bo;
            loadVlan(hmbo); 
        }
        else if (bo instanceof MacOrOui) {
            MacOrOui hmbo = (MacOrOui) bo;
            loadMacOrOui(hmbo); 
        }
        else if (bo instanceof IpAddress) {
            IpAddress hmbo = (IpAddress) bo;
            loadIpAddress(hmbo); 
        }
        else if (bo instanceof BonjourGatewaySettings) {
            BonjourGatewaySettings hmbo = (BonjourGatewaySettings) bo;
            loadBonjourGatewaySettings(hmbo); 
        }
        else if (bo instanceof DomainObject) {
            DomainObject hmbo = (DomainObject) bo;
            loadDomainObject(hmbo); 
        }
        else if (bo instanceof DnsServiceProfile) {
            DnsServiceProfile hmbo = (DnsServiceProfile) bo;
            loadDnsServiceProfile(hmbo); 
        }
        else if (bo instanceof OsObject) {
            OsObject hmbo = (OsObject) bo;
            loadOsObject(hmbo); 
        }
        else if (bo instanceof RadiusAttrs) {
            RadiusAttrs hmbo = (RadiusAttrs) bo;
            loadRadiusAttrs(hmbo); 
        }
        else if (bo instanceof RoutingProfilePolicy) {
            RoutingProfilePolicy hmbo = (RoutingProfilePolicy) bo;
            loadRoutingProfilePolicy(hmbo); 
        }
        else if (bo instanceof FirewallPolicy) {
            FirewallPolicy hmbo = (FirewallPolicy) bo;
            loadFirewallPolicy(hmbo); 
        }
        else if (bo instanceof ActiveDirectoryOrOpenLdap) {
            ActiveDirectoryOrOpenLdap hmbo = (ActiveDirectoryOrOpenLdap) bo;
            loadActiveDirectoryOrOpenLdap(hmbo); 
        }
        else if (bo instanceof RadiusLibrarySip) {
            RadiusLibrarySip hmbo = (RadiusLibrarySip) bo;
            loadRadiusLibrarySip(hmbo); 
        }
        else if (bo instanceof Trex) {
            Trex hmbo = (Trex) bo;
            loadTrex(hmbo); 
        }

        return null;
    }
    
    private void loadMapContainerNode(MapContainerNode hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getChildNodes()) {
            hmbo.getChildNodes().size();
            
            /*Manual Changes --start*/
            for (MapNode mapNode : hmbo.getChildNodes()) {
                if (!mapNode.isLeafNode()) {
                    loadMapContainerNode((MapContainerNode) mapNode);
                }
            }
            /*Manual Changes --end--*/
        }
        if (null != hmbo.getChildLinks()) {
            hmbo.getChildLinks().size();
        }
        if (null != hmbo.getPerimeter()) {
            hmbo.getPerimeter().size();
        }
        if (null != hmbo.getWalls()) {
            hmbo.getWalls().size();
        }
        if (null != hmbo.getPlannedAPs()) {
            hmbo.getPlannedAPs().size();
        }
    }
    private void loadMapLeafNode(MapLeafNode hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getHiveAp()) {
            hmbo.getHiveAp().getId();
        }
    }
    private void loadHiveAp(HiveAp hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getConfigTemplate()) {
            hmbo.getConfigTemplate().getId();
        }
        if (null != hmbo.getRadiusServerProfile()) {
            hmbo.getRadiusServerProfile().getId();
        }
        if (null != hmbo.getSupplementalCLI()) {
            hmbo.getSupplementalCLI().getId();
        }
        if (null != hmbo.getRadiusProxyProfile()) {
            hmbo.getRadiusProxyProfile().getId();
        }
        if (null != hmbo.getPppoeAuthProfile()) {
            hmbo.getPppoeAuthProfile().getId();
        }
        if (null != hmbo.getWifi0RadioProfile()) {
            hmbo.getWifi0RadioProfile().getId();
        }
        if (null != hmbo.getWifi1RadioProfile()) {
            hmbo.getWifi1RadioProfile().getId();
        }
        if (null != hmbo.getStaticRoutes()) {
            hmbo.getStaticRoutes().size();
        }
        if (null != hmbo.getDynamicRoutes()) {
            hmbo.getDynamicRoutes().size();
        }
        if (null != hmbo.getIpRoutes()) {
            hmbo.getIpRoutes().size();
        }
        if (null != hmbo.getInternalNetworks()) {
            hmbo.getInternalNetworks().size();
        }
        if (null != hmbo.getMultipleVlan()) {
            hmbo.getMultipleVlan().size();
        }
        if (null != hmbo.getL3Neighbors()) {
            hmbo.getL3Neighbors().size();
        }
        if (null != hmbo.getLearningMacs()) {
            hmbo.getLearningMacs().size();
        }
        if (null != hmbo.getWifiClientPreferredSsids()) {
            hmbo.getWifiClientPreferredSsids().size();
        }
        if (null != hmbo.getDhcpServers()) {
            hmbo.getDhcpServers().size();
        }
        if (null != hmbo.getEth0UserProfile()) {
            hmbo.getEth0UserProfile().getId();
        }
        if (null != hmbo.getEth1UserProfile()) {
            hmbo.getEth1UserProfile().getId();
        }
        if (null != hmbo.getAgg0UserProfile()) {
            hmbo.getAgg0UserProfile().getId();
        }
        if (null != hmbo.getRed0UserProfile()) {
            hmbo.getRed0UserProfile().getId();
        }
        if (null != hmbo.getDownloadInfo()) {
            hmbo.getDownloadInfo().getId();
        }
        if (null != hmbo.getEthCwpCwpProfile()) {
            hmbo.getEthCwpCwpProfile().getId();
        }
        if (null != hmbo.getEthCwpRadiusClient()) {
            hmbo.getEthCwpRadiusClient().getId();
        }
        if (null != hmbo.getEthCwpDefaultAuthUserProfile()) {
            hmbo.getEthCwpDefaultAuthUserProfile().getId();
        }
        if (null != hmbo.getEthCwpDefaultRegUserProfile()) {
            hmbo.getEthCwpDefaultRegUserProfile().getId();
        }
        if (null != hmbo.getEthCwpRadiusUserProfiles()) {
            hmbo.getEthCwpRadiusUserProfiles().size();
        }
        if (null != hmbo.getDisabledSsids()) {
            hmbo.getDisabledSsids().size();
        }
        if (null != hmbo.getVirtualConnections()) {
            hmbo.getVirtualConnections().size();
        }
        if (null != hmbo.getDeviceInterfaces()) {
            hmbo.getDeviceInterfaces().size();
        }
        if (null != hmbo.getSecondVPNGateway()) {
            hmbo.getSecondVPNGateway().getId();
            
            /*Manual Changes --start--*/
            if (null != hmbo.getSecondVPNGateway().getDeviceInterfaces()) {
                hmbo.getSecondVPNGateway().getDeviceInterfaces().size();
            }
            /*Manual Changes --end--*/
        }
        if (null != hmbo.getRoutingProfile()) {
            hmbo.getRoutingProfile().getId();
        }
        if (null != hmbo.getUsbModemList()) {
            hmbo.getUsbModemList().size();
        }
        if (null != hmbo.getRoutingPolicy()) {
            hmbo.getRoutingPolicy().getId();
        }
        if (null != hmbo.getRoutingProfilePolicy()) {
            hmbo.getRoutingProfilePolicy().getId();
        }
        if (null != hmbo.getStormControlList()) {
            hmbo.getStormControlList().size();
        }
        if (null != hmbo.getIgmpPolicys()) {
            hmbo.getIgmpPolicys().size();
        }
        if (null != hmbo.getMulticastGroups()) {
            hmbo.getMulticastGroups().size();
        }
        if (null != hmbo.getDeviceStpSettings()) {
            hmbo.getDeviceStpSettings().getId();
        }
    }
    private void loadConfigTemplate(ConfigTemplate hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getHiveProfile()) {
            hmbo.getHiveProfile().getId();
        }
        if (null != hmbo.getMgmtServiceDns()) {
            hmbo.getMgmtServiceDns().getId();
        }
        if (null != hmbo.getMgmtServiceSyslog()) {
            hmbo.getMgmtServiceSyslog().getId();
        }
        if (null != hmbo.getMgmtServiceSnmp()) {
            hmbo.getMgmtServiceSnmp().getId();
        }
        if (null != hmbo.getMgmtServiceTime()) {
            hmbo.getMgmtServiceTime().getId();
        }
        if (null != hmbo.getMgmtServiceOption()) {
            hmbo.getMgmtServiceOption().getId();
        }
        if (null != hmbo.getClientWatch()) {
            hmbo.getClientWatch().getId();
        }
        if (null != hmbo.getIdsPolicy()) {
            hmbo.getIdsPolicy().getId();
        }
        if (null != hmbo.getVlan()) {
            hmbo.getVlan().getId();
        }
        if (null != hmbo.getVlanNative()) {
            hmbo.getVlanNative().getId();
        }
        if (null != hmbo.getIpFilter()) {
            hmbo.getIpFilter().getId();
        }
        if (null != hmbo.getAccessConsole()) {
            hmbo.getAccessConsole().getId();
        }
        if (null != hmbo.getDeviceServiceFilter()) {
            hmbo.getDeviceServiceFilter().getId();
        }
        if (null != hmbo.getEth0ServiceFilter()) {
            hmbo.getEth0ServiceFilter().getId();
        }
        if (null != hmbo.getWireServiceFilter()) {
            hmbo.getWireServiceFilter().getId();
        }
        if (null != hmbo.getEth0BackServiceFilter()) {
            hmbo.getEth0BackServiceFilter().getId();
        }
        if (null != hmbo.getEth1BackServiceFilter()) {
            hmbo.getEth1BackServiceFilter().getId();
        }
        if (null != hmbo.getRed0BackServiceFilter()) {
            hmbo.getRed0BackServiceFilter().getId();
        }
        if (null != hmbo.getAgg0BackServiceFilter()) {
            hmbo.getAgg0BackServiceFilter().getId();
        }
        if (null != hmbo.getLocationServer()) {
            hmbo.getLocationServer().getId();
        }
        if (null != hmbo.getAlgConfiguration()) {
            hmbo.getAlgConfiguration().getId();
        }
        if (null != hmbo.getClassifierMap()) {
            hmbo.getClassifierMap().getId();
        }
        if (null != hmbo.getMarkerMap()) {
            hmbo.getMarkerMap().getId();
        }
        if (null != hmbo.getSsidInterfaces()) {
            hmbo.getSsidInterfaces().size();
            
            /*Manual Changes --start--*/
            for (ConfigTemplateSsid ctSsid : hmbo.getSsidInterfaces().values()) {
                if(null != ctSsid.getSsidProfile()) {
                    ctSsid.getSsidProfile().getId();
                }
            }
            /*Manual Changes --end--*/
        }
        if (null != hmbo.getVlanNetwork()) {
            hmbo.getVlanNetwork().size();
        }
        if (null != hmbo.getIpTracks()) {
            hmbo.getIpTracks().size();
        }
        if (null != hmbo.getEth1ServiceFilter()) {
            hmbo.getEth1ServiceFilter().getId();
        }
        if (null != hmbo.getRed0ServiceFilter()) {
            hmbo.getRed0ServiceFilter().getId();
        }
        if (null != hmbo.getAgg0ServiceFilter()) {
            hmbo.getAgg0ServiceFilter().getId();
        }
        if (null != hmbo.getVpnService()) {
            hmbo.getVpnService().getId();
        }
        if (null != hmbo.getLldpCdp()) {
            hmbo.getLldpCdp().getId();
        }
        if (null != hmbo.getTvNetworkService()) {
            hmbo.getTvNetworkService().size();
        }
        if (null != hmbo.getFwPolicy()) {
            hmbo.getFwPolicy().getId();
        }
        if (null != hmbo.getBonjourGw()) {
            hmbo.getBonjourGw().getId();
        }
        if (null != hmbo.getUpVlanMapping()) {
            hmbo.getUpVlanMapping().size();
        }
        if (null != hmbo.getRadiusServerProfile()) {
            hmbo.getRadiusServerProfile().getId();
        }
        if (null != hmbo.getRadiusProxyProfile()) {
            hmbo.getRadiusProxyProfile().getId();
        }
        if (null != hmbo.getRadiusAttrs()) {
            hmbo.getRadiusAttrs().getId();
        }
        if (null != hmbo.getSupplementalCLI()) {
            hmbo.getSupplementalCLI().getId();
        }
        if (null != hmbo.getStormControlList()) {
            hmbo.getStormControlList().size();
        }
        if (null != hmbo.getRoutingProfilePolicy()) {
            hmbo.getRoutingProfilePolicy().getId();
        }
        if (null != hmbo.getRoutingPolicy()) {
            hmbo.getRoutingPolicy().getId();
        }
        if (null != hmbo.getAppProfile()) {
            hmbo.getAppProfile().getId();
        }
        if (null != hmbo.getSwitchSettings()) {
            hmbo.getSwitchSettings().getId();
        }
    }
    private void loadUserProfile(UserProfile hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getUserProfileAttribute()) {
            hmbo.getUserProfileAttribute().getId();
        }
        if (null != hmbo.getVlan()) {
            hmbo.getVlan().getId();
        }
        if (null != hmbo.getQosRateControl()) {
            hmbo.getQosRateControl().getId();
        }
        if (null != hmbo.getTunnelSetting()) {
            hmbo.getTunnelSetting().getId();
        }
        if (null != hmbo.getIpPolicyTo()) {
            hmbo.getIpPolicyTo().getId();
        }
        if (null != hmbo.getIpPolicyFrom()) {
            hmbo.getIpPolicyFrom().getId();
        }
        if (null != hmbo.getMacPolicyTo()) {
            hmbo.getMacPolicyTo().getId();
        }
        if (null != hmbo.getMacPolicyFrom()) {
            hmbo.getMacPolicyFrom().getId();
        }
        if (null != hmbo.getAsRuleGroup()) {
            hmbo.getAsRuleGroup().getId();
        }
        if (null != hmbo.getUserProfileSchedulers()) {
            hmbo.getUserProfileSchedulers().size();
        }
        if (null != hmbo.getAssignRules()) {
            hmbo.getAssignRules().size();
        }
        if (null != hmbo.getMarkerMap()) {
            hmbo.getMarkerMap().getId();
        }
    }
    private void loadQosClassification(QosClassification hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getNetworkServices()) {
            hmbo.getNetworkServices().size();
        }
        if (null != hmbo.getCustomServices()) {
            hmbo.getCustomServices().size();
        }
        if (null != hmbo.getQosMacOuis()) {
            hmbo.getQosMacOuis().size();
        }
        if (null != hmbo.getQosSsids()) {
            hmbo.getQosSsids().size();
        }
    }
    private void loadIdsPolicy(IdsPolicy hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getIdsSsids()) {
            hmbo.getIdsSsids().size();
        }
        if (null != hmbo.getMacOrOuis()) {
            hmbo.getMacOrOuis().size();
        }
        if (null != hmbo.getVlans()) {
            hmbo.getVlans().size();
        }
    }
    private void loadEthernetAccess(EthernetAccess hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getUserProfile()) {
            hmbo.getUserProfile().getId();
        }
        if (null != hmbo.getMacAddress()) {
            hmbo.getMacAddress().size();
        }
    }
    private void loadHiveProfile(HiveProfile hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getMacFilters()) {
            hmbo.getMacFilters().size();
        }
    }
    private void loadSsidProfile(SsidProfile hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getSchedulers()) {
            hmbo.getSchedulers().size();
        }
        if (null != hmbo.getUserProfileGuest()) {
            hmbo.getUserProfileGuest().getId();
        }
        if (null != hmbo.getRadiusUserProfile()) {
            hmbo.getRadiusUserProfile().size();
        }
        if (null != hmbo.getGRateSets()) {
            hmbo.getGRateSets().size();
        }
        if (null != hmbo.getARateSets()) {
            hmbo.getARateSets().size();
        }
        if (null != hmbo.getNRateSets()) {
            hmbo.getNRateSets().size();
        }
        if (null != hmbo.getAcRateSets()) {
            hmbo.getAcRateSets().size();
        }
        if (null != hmbo.getLocalUserGroups()) {
            hmbo.getLocalUserGroups().size();
        }
        if (null != hmbo.getRadiusUserGroups()) {
            hmbo.getRadiusUserGroups().size();
        }
        if (null != hmbo.getMacFilters()) {
            hmbo.getMacFilters().size();
            
            /*Manual Changes --start--*/
            for (MacFilter macFilter : hmbo.getMacFilters()) {
                loadMacFilter(macFilter);
            }
            /*Manual Changes --end--*/
        }
        
        /*Manual Changes --start--*/
        if (null != hmbo.getRadiusAssignment()) {
            if (null != hmbo.getRadiusAssignment().getServices()) {
                hmbo.getRadiusAssignment().getServices().size();
            }
        }
        /*Manual Changes --end--*/
    }
    private void loadPortAccessProfile(PortAccessProfile hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getRadiusAssignment()) {
            hmbo.getRadiusAssignment().getId();
        }
        if (null != hmbo.getCwp()) {
            hmbo.getCwp().getId();
        }
        if (null != hmbo.getDefUserProfile()) {
            hmbo.getDefUserProfile().getId();
        }
        if (null != hmbo.getSelfRegUserProfile()) {
            hmbo.getSelfRegUserProfile().getId();
        }
        if (null != hmbo.getGuestUserProfile()) {
            hmbo.getGuestUserProfile().getId();
        }
        if (null != hmbo.getVoiceVlan()) {
            hmbo.getVoiceVlan().getId();
        }
        if (null != hmbo.getDataVlan()) {
            hmbo.getDataVlan().getId();
        }
        if (null != hmbo.getAuthOkUserProfile()) {
            hmbo.getAuthOkUserProfile().size();
        }
        if (null != hmbo.getAuthOkDataUserProfile()) {
            hmbo.getAuthOkDataUserProfile().size();
        }
        if (null != hmbo.getAuthFailUserProfile()) {
            hmbo.getAuthFailUserProfile().size();
        }
        if (null != hmbo.getRadiusUserGroups()) {
            hmbo.getRadiusUserGroups().size();
        }
        if (null != hmbo.getServiceFilter()) {
            hmbo.getServiceFilter().getId();
        }
        if (null != hmbo.getNativeVlan()) {
            hmbo.getNativeVlan().getId();
        }
    }
    private void loadVpnNetwork(VpnNetwork hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getVpnDnsService()) {
            hmbo.getVpnDnsService().getId();
        }
        if (null != hmbo.getPortForwardings()) {
            hmbo.getPortForwardings().size();
        }
        if (null != hmbo.getSubItems()) {
            hmbo.getSubItems().size();
        }
        if (null != hmbo.getSubNetwokClass()) {
            hmbo.getSubNetwokClass().size();
        }
        if (null != hmbo.getReserveClass()) {
            hmbo.getReserveClass().size();
        }
        if (null != hmbo.getSubNetworkRes()) {
            hmbo.getSubNetworkRes().size();
        }
        if (null != hmbo.getCustomOptions()) {
            hmbo.getCustomOptions().size();
        }
        if (null != hmbo.getSubnetworkDHCPCustoms()) {
            hmbo.getSubnetworkDHCPCustoms().size();
        }
    }
    private void loadUSBModem(USBModem hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getUsbSignalStrengthCheckList()) {
            hmbo.getUsbSignalStrengthCheckList().size();
        }
    }
    private void loadMacFilter(MacFilter hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getFilterInfo()) {
            hmbo.getFilterInfo().size();
        }
    }
    private void loadIpFilter(IpFilter hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getIpAddress()) {
            hmbo.getIpAddress().size();
        }
    }
    private void loadIpPolicy(IpPolicy hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getRules()) {
            hmbo.getRules().size();
        }
        if (null != hmbo.getUserProfileFrom()) {
            hmbo.getUserProfileFrom().size();
        }
        if (null != hmbo.getUserProfileTo()) {
            hmbo.getUserProfileTo().size();
        }
    }
    private void loadMacPolicy(MacPolicy hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getRules()) {
            hmbo.getRules().size();
        }
        if (null != hmbo.getUserProfileFrom()) {
            hmbo.getUserProfileFrom().size();
        }
        if (null != hmbo.getUserProfileTo()) {
            hmbo.getUserProfileTo().size();
        }
    }
    private void loadRadiusOnHiveap(RadiusOnHiveap hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getLocalUserGroup()) {
            hmbo.getLocalUserGroup().size();
        }
        if (null != hmbo.getDirectoryOrLdap()) {
            hmbo.getDirectoryOrLdap().size();
        }
        if (null != hmbo.getIpOrNames()) {
            hmbo.getIpOrNames().size();
        }
        if (null != hmbo.getLdapOuUserProfiles()) {
            hmbo.getLdapOuUserProfiles().size();
        }
    }
    private void loadAccessConsole(AccessConsole hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getMacFilters()) {
            hmbo.getMacFilters().size();
        }
    }
    private void loadRadiusUserProfileRule(RadiusUserProfileRule hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getPermittedUserProfiles()) {
            hmbo.getPermittedUserProfiles().size();
        }
    }
    private void loadVlanDhcpServer(VlanDhcpServer hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getCustoms()) {
            hmbo.getCustoms().size();
        }
        if (null != hmbo.getIpPools()) {
            hmbo.getIpPools().size();
        }
    }
    private void loadTunnelSetting(TunnelSetting hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getIpAddressList()) {
            hmbo.getIpAddressList().size();
        }
        if (null != hmbo.getUserProfile()) {
            hmbo.getUserProfile().size();
        }
    }
    private void loadRadiusAssignment(RadiusAssignment hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getServices()) {
            hmbo.getServices().size();
        }
    }
    private void loadAlgConfiguration(AlgConfiguration hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getItems()) {
            hmbo.getItems().size();
        }
    }
    private void loadHmAccessControl(HmAccessControl hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getIpAddresses()) {
            hmbo.getIpAddresses().size();
        }
    }
    private void loadDosPrevention(DosPrevention hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getDosParamsMap()) {
            hmbo.getDosParamsMap().size();
        }
    }
    private void loadUserProfileAttribute(UserProfileAttribute hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getItems()) {
            hmbo.getItems().size();
        }
        if (null != hmbo.getUserProfile()) {
            hmbo.getUserProfile().size();
        }
    }
    private void loadAhCustomReport(AhCustomReport hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getCustomFields()) {
            hmbo.getCustomFields().size();
        }
    }
    private void loadCwp(Cwp hmbo) {
        if (null == hmbo) {
            return;
        }
        
        /*Manual Changes --start--*/
        CwpPageCustomization pageCustom = hmbo.getPageCustomization();
        if (null != pageCustom.getFields()) {
            pageCustom.getFields().size();
        }
        /*Manual Changes --end--*/
        
        if (null != hmbo.getCertificate()) {
            hmbo.getCertificate().getId();
        }
        if (null != hmbo.getVlan()) {
            hmbo.getVlan().getId();
        }
        if (null != hmbo.getWalledGarden()) {
            hmbo.getWalledGarden().size();
        }
        if (null != hmbo.getIpAddressSuccess()) {
            hmbo.getIpAddressSuccess().getId();
        }
        if (null != hmbo.getIpAddressFailure()) {
            hmbo.getIpAddressFailure().getId();
        }
    }
    private void loadRadioProfile(RadioProfile hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getWmmItems()) {
            hmbo.getWmmItems().size();
        }
        if (null != hmbo.getSupressBprOUIs()) {
            hmbo.getSupressBprOUIs().size();
        }
    }
    private void loadVpnService(VpnService hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getVpnCredentials()) {
            hmbo.getVpnCredentials().size();
        }
        if (null != hmbo.getVpnGateWaysSetting()) {
            hmbo.getVpnGateWaysSetting().size();
        }
        if (null != hmbo.getUserProfileTrafficL3()) {
            hmbo.getUserProfileTrafficL3().size();
        }
        if (null != hmbo.getUserProfileTrafficL2()) {
            hmbo.getUserProfileTrafficL2().size();
        }
        if (null != hmbo.getDomObj()) {
            hmbo.getDomObj().getId();
        }
    }
    private void loadHmUserGroup(HmUserGroup hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getInstancePermissions()) {
            hmbo.getInstancePermissions().size();
        }
        if (null != hmbo.getFeaturePermissions()) {
            hmbo.getFeaturePermissions().size();
        }
    }
    private void loadHiveApUpdateResult(HiveApUpdateResult hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getItems()) {
            hmbo.getItems().size();
        }
    }
    private void loadTvClass(TvClass hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getItems()) {
            hmbo.getItems().size();
        }
    }
    private void loadTvComputerCart(TvComputerCart hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getItems()) {
            hmbo.getItems().size();
        }
    }
    private void loadMgmtServiceDns(MgmtServiceDns hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getDnsInfo()) {
            hmbo.getDnsInfo().size();
        }
    }
    private void loadMgmtServiceSyslog(MgmtServiceSyslog hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getSyslogInfo()) {
            hmbo.getSyslogInfo().size();
        }
    }
    private void loadMgmtServiceSnmp(MgmtServiceSnmp hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getSnmpInfo()) {
            hmbo.getSnmpInfo().size();
        }
    }
    private void loadMgmtServiceTime(MgmtServiceTime hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getTimeInfo()) {
            hmbo.getTimeInfo().size();
        }
    }
    private void loadMgmtServiceOption(MgmtServiceOption hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getMultipleVlan()) {
            hmbo.getMultipleVlan().size();
        }
    }
    private void loadQosRateControl(QosRateControl hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getQosRateLimit()) {
            hmbo.getQosRateLimit().size();
        }
    }
    private void loadIdpSettings(IdpSettings hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getEnclosedRogueAps()) {
            hmbo.getEnclosedRogueAps().size();
        }
        if (null != hmbo.getEnclosedFriendlyAps()) {
            hmbo.getEnclosedFriendlyAps().size();
        }
    }
    private void loadHiveApAutoProvision(HiveApAutoProvision hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getMacAddresses()) {
            hmbo.getMacAddresses().size();
        }
        if (null != hmbo.getIpSubNetworks()) {
            hmbo.getIpSubNetworks().size();
        }
        if (null != hmbo.getDeviceInterfaces()) {
            hmbo.getDeviceInterfaces().size();
        }
    }
    private void loadRadiusProxy(RadiusProxy hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getRadiusRealm()) {
            hmbo.getRadiusRealm().size();
        }
        if (null != hmbo.getRadiusNas()) {
            hmbo.getRadiusNas().size();
        }
    }
    private void loadIdp(Idp hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getMitiAps()) {
            hmbo.getMitiAps().size();
        }
    }
    private void loadLocationClientWatch(LocationClientWatch hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getItems()) {
            hmbo.getItems().size();
        }
    }
    private void loadVlan(Vlan hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getItems()) {
            hmbo.getItems().size();
        }
        if (null != hmbo.getUserProfile()) {
            hmbo.getUserProfile().size();
        }
    }
    private void loadMacOrOui(MacOrOui hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getItems()) {
            hmbo.getItems().size();
        }
    }
    private void loadIpAddress(IpAddress hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getItems()) {
            hmbo.getItems().size();
        }
    }
    private void loadBonjourGatewaySettings(BonjourGatewaySettings hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getBonjourActiveServices()) {
            hmbo.getBonjourActiveServices().size();
        }
        if (null != hmbo.getRules()) {
            hmbo.getRules().size();
        }
    }
    private void loadDomainObject(DomainObject hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getItems()) {
            hmbo.getItems().size();
        }
    }
    private void loadDnsServiceProfile(DnsServiceProfile hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getInternalDns1()) {
            hmbo.getInternalDns1().getId();
        }
        if (null != hmbo.getInternalDns2()) {
            hmbo.getInternalDns2().getId();
        }
        if (null != hmbo.getInternalDns3()) {
            hmbo.getInternalDns3().getId();
        }
        if (null != hmbo.getDomainObj()) {
            hmbo.getDomainObj().getId();
        }
        if (null != hmbo.getSpecificInfos()) {
            hmbo.getSpecificInfos().size();
        }
        if (null != hmbo.getExternalDns1()) {
            hmbo.getExternalDns1().getId();
        }
        if (null != hmbo.getExternalDns2()) {
            hmbo.getExternalDns2().getId();
        }
        if (null != hmbo.getExternalDns3()) {
            hmbo.getExternalDns3().getId();
        }
    }
    private void loadOsObject(OsObject hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getItems()) {
            hmbo.getItems().size();
        }
        if (null != hmbo.getDhcpItems()) {
            hmbo.getDhcpItems().size();
        }
    }
    private void loadRadiusAttrs(RadiusAttrs hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getItems()) {
            hmbo.getItems().size();
        }
    }
    private void loadRoutingProfilePolicy(RoutingProfilePolicy hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getRoutingProfilePolicyRuleList()) {
            hmbo.getRoutingProfilePolicyRuleList().size();
        }
    }
    private void loadFirewallPolicy(FirewallPolicy hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getRules()) {
            hmbo.getRules().size();
        }
    }
    private void loadActiveDirectoryOrOpenLdap(ActiveDirectoryOrOpenLdap hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getAdDomains()) {
            hmbo.getAdDomains().size();
        }
    }
    private void loadRadiusLibrarySip(RadiusLibrarySip hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getRules()) {
            hmbo.getRules().size();
        }
    }
    private void loadTrex(Trex hmbo) {
        if (null == hmbo) {
            return;
        }
        if (null != hmbo.getParentMap()) {
            hmbo.getParentMap().getId();
        }
    }
}