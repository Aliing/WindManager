package lazyload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ah.bo.admin.HmAccessControl;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.hiveap.HiveApUpdateResult;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.hiveap.IdpSettings;
import com.ah.bo.hiveap.WifiClientPreferredSsid;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.mobility.QosRateControl;
import com.ah.bo.mobility.TunnelSetting;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.Trex;
import com.ah.bo.network.AccessConsole;
import com.ah.bo.network.AlgConfiguration;
import com.ah.bo.network.Application;
import com.ah.bo.network.BonjourGatewaySettings;
import com.ah.bo.network.CLIBlob;
import com.ah.bo.network.CompliancePolicy;
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
import com.ah.bo.network.PPPoE;
import com.ah.bo.network.PseProfile;
import com.ah.bo.network.RadiusAttrs;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.USBModem;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VlanDhcpServer;
import com.ah.bo.network.VlanGroup;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnService;
import com.ah.bo.performance.AhClientEditValues;
import com.ah.bo.performance.AhCustomReport;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.teacherView.TvClass;
import com.ah.bo.teacherView.TvComputerCart;
import com.ah.bo.teacherView.TvResourceMap;
import com.ah.bo.teacherView.TvStudentRoster;
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
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.RadiusProxy;
import com.ah.bo.useraccess.RadiusUserProfileRule;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.CwpCertificate;
import com.ah.bo.wlan.EthernetAccess;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SsidProfile;

public class TestAutoLoader {

    public static void main(String[] args) {
        final String pathname = TestAutoLoader.class.getResource("").getPath();
        final String templateName = "seQueryBo.ftl";
        
        final String generateFileName = System.getProperty("user.dir")
                + File.separator + "test" + File.separator + "lazyload"
                + File.separator + "generate" + File.separator + "SearchEngineLazyLoad.java";
        
        final String packagePath = "lazyload.generate";
        
        AutoQueryBoLoader loader = new AutoQueryBoLoader(new File(pathname, templateName), generateFileName);
        setIgnoredList(loader);
        
        final Class<?>[] classArray = new Class[] { MapContainerNode.class,
                MapLeafNode.class, HiveAp.class, ConfigTemplate.class,
                UserProfile.class, QosClassification.class, IdsPolicy.class,
                EthernetAccess.class, HiveProfile.class,
                SsidProfile.class, PortAccessProfile.class, VpnNetwork.class,
                USBModem.class, MacFilter.class, IpFilter.class,
                IpPolicy.class, MacPolicy.class, RadiusOnHiveap.class,
                AccessConsole.class, RadiusUserProfileRule.class,
                VlanDhcpServer.class, TunnelSetting.class, RadiusAssignment.class,
                AlgConfiguration.class, HmAccessControl.class, DosPrevention.class,
                UserProfileAttribute.class, AhCustomReport.class, Cwp.class,
                RadioProfile.class, VpnService.class, HmUserGroup.class,
                HmUser.class, HiveApUpdateResult.class, TvClass.class, TvComputerCart.class,
                MgmtServiceDns.class, MgmtServiceSyslog.class, MgmtServiceSnmp.class,
                MgmtServiceTime.class, MgmtServiceOption.class, QosRateControl.class, 
                IdpSettings.class, HiveApAutoProvision.class, RadiusProxy.class,
                Idp.class, LocationClientWatch.class, Vlan.class, MacOrOui.class,
                IpAddress.class,
                // new Classes as below
                BonjourGatewaySettings.class, DomainObject.class, DnsServiceProfile.class,
                OsObject.class, PPPoE.class, PseProfile.class,RadiusAttrs.class,
                RoutingProfilePolicy.class, VlanGroup.class, WifiClientPreferredSsid.class,
                Application.class, ConfigTemplateMdm.class, 
                LLDPCDPProfile.class, NetworkService.class, Scheduler.class, FirewallPolicy.class,
                ServiceFilter.class, QosMarking.class, MgmtServiceIPTrack.class, LocationServer.class,
                ActiveDirectoryOrOpenLdap.class, RadiusLibrarySip.class, LocalUserGroup.class,
                LocalUser.class, CwpCertificate.class, CompliancePolicy.class,  TvStudentRoster.class,
                TvResourceMap.class, AhClientEditValues.class, Trex.class, CLIBlob.class
                };
        
        loader.generate(classArray, packagePath);
    }

    private static void setIgnoredList(AutoQueryBoLoader loader) {
        List<Class<?>> ignoredClassFields = new ArrayList<>();
        
        ignoredClassFields.add(HmDomain.class);
        
        loader.setIgnoredClassFields(ignoredClassFields);
    }

}
