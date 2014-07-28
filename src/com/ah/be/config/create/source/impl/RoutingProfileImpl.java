package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.InterfaceProfileInt.MgtType;
import com.ah.be.config.create.source.RoutingProfileInt;
import com.ah.be.config.create.source.impl.branchRouter.InterfaceBRImpl.DhcpServerInfo;
import com.ah.be.config.create.source.impl.cvg.InterfaceCVGImpl;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateVlanNetwork;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApInternalNetwork;
import com.ah.bo.hiveap.HiveApIpRoute;
import com.ah.bo.network.RoutingProfile;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.bo.network.RoutingProfilePolicyRule;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.UserProfileVlanMapping;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.xml.be.config.RoutingProtocolTypeValue;

@SuppressWarnings("static-access")
public class RoutingProfileImpl implements RoutingProfileInt {
    private static final Tracer log = new Tracer(SecurityObjectProfileImpl.class
            .getSimpleName());

    private HiveAp hiveAp;
	private ConfigureProfileFunction config;
	private RoutingProfile routing;
	List<UserProfileImpl> userProfileList;
	
	private	List<RoutingNetwork> routNetworkList;
	private List<RoutingProfilePolicyRule> routingRuleList;

    private HashMap<Short, String> wanIntfPool = new HashMap<>();


    public RoutingProfileImpl(HiveAp hiveAp, ConfigureProfileFunction config) throws CreateXMLException{
		this.hiveAp = hiveAp;
		this.config = config;
		this.routing = hiveAp.getRoutingProfile();
		this.routNetworkList = loadRoutNetworkList(hiveAp);
		this.routingRuleList= loadRoutingPolicyPolicyRuleList(hiveAp);

        prepareWanIntfPool();
	}
	
	private List<RoutingProfilePolicyRule> loadRoutingPolicyPolicyRuleList(HiveAp hiveAp){
		if(hiveAp == null){
			return null;
		}
		
		RoutingProfilePolicy policyBo = hiveAp.getRoutingProfilePolicy();
		if(policyBo == null){
			if (hiveAp.getConfigTemplate() != null)
				policyBo = hiveAp.getConfigTemplate().getRoutingProfilePolicy();
		}
		if(policyBo == null){
			return null;
		}

        List<RoutingProfilePolicyRule> list = policyBo.getRoutingProfilePolicyRuleList();
        Collections.sort(list, new Comparator<RoutingProfilePolicyRule>() {
            public int compare(RoutingProfilePolicyRule obj1, RoutingProfilePolicyRule obj2) {
                return obj1.getPriority() - obj2.getPriority();
            }
        });

        return list;
    }

	private List<RoutingNetwork> loadRoutNetworkList(HiveAp hiveAp) throws CreateXMLException{
		Set<RoutingNetwork> routNetworkSet = new HashSet<RoutingNetwork>();
		
		Map<MgtType, DhcpServerInfo> allNetworkMaps = config.getInterfaceProfileImpl().getMgtSubResourceMap();
		
		boolean routeAllNetowrk;
		if(hiveAp.isBranchRouter()){
			routeAllNetowrk = allNetworkMaps != null && hiveAp.getConfigTemplate().getVpnService() != null;
		}else{
			routeAllNetowrk = allNetworkMaps != null;
		}
		if(routeAllNetowrk){
			for(DhcpServerInfo networkInfo : allNetworkMaps.values()){
				if(networkInfo == null || networkInfo.getVpnNetwork() == null || 
						networkInfo.getSubNetwork() == null || 
						networkInfo.getVpnNetworkSub() == null){
					continue;
				}
				if(networkInfo.getVpnNetwork().getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
						networkInfo.getVpnNetwork().getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
					if(NmsUtil.compareSoftwareVersion(this.hiveAp.getSoftVer(), "6.1.5.0") >= 0){
						routNetworkSet.add(new RoutingNetwork(networkInfo.getVpnNetworkSub().getIpNetwork(), false, true));
					}else{
						routNetworkSet.add(new RoutingNetwork(networkInfo.getSubNetwork().getNetwork(), false, true));
					}
				}
			}
		}
		
		if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER){
			if(hiveAp.getIpRoutes() != null){
				for(HiveApIpRoute ipRoute : hiveAp.getIpRoutes()){
					if(ipRoute.isAdvertiseCvg()){
						String networkIp = ipRoute.getSourceIp();
						String networkMask = ipRoute.getNetmask();
						int networkMaskInt = CLICommonFunc.turnNetMaskToNum(networkMask);
						if(networkIp != null && !"".equals(networkIp) && networkMask != null && !"".equals(networkMask)){
							RoutingNetwork network = new RoutingNetwork(networkIp+"/"+String.valueOf(networkMaskInt),false, true);
							if(!routNetworkSet.contains(network)){
								routNetworkSet.add(network);
							}
						}
					}
				}
			}
		}
		
		if(hiveAp.isVpnGateway()){
			String eth0Ip = hiveAp.getEth0Interface().getIpAddress();
			String eth0Mask = hiveAp.getEth0Interface().getNetMask();
			int eth0MaskInt = CLICommonFunc.turnNetMaskToNum(eth0Mask);
			String eth1Ip = hiveAp.getEth1Interface().getIpAddress();
			String eth1Mask = hiveAp.getEth1Interface().getNetMask();
			int eth1MaskInt = CLICommonFunc.turnNetMaskToNum(eth1Mask);
			if(eth0Ip != null && !"".equals(eth0Ip) && eth0Mask != null && !"".equals(eth0Mask) && !InterfaceCVGImpl.isEqualPubIp(this.hiveAp)){
				String eth0Net = MgrUtil.getStartIpAddressValue(eth0Ip, eth0Mask);
				RoutingNetwork eth0Rout = new RoutingNetwork(eth0Net+"/"+String.valueOf(eth0MaskInt), true, true);
				if(!routNetworkSet.contains(eth0Rout)){
					routNetworkSet.add(eth0Rout);
				}
			}
			if(eth1Ip != null && !"".equals(eth1Ip) && eth1Mask != null && !"".equals(eth1Mask)){
				String eth1Net = MgrUtil.getStartIpAddressValue(eth1Ip, eth1Mask);
				RoutingNetwork eth1Rout = new RoutingNetwork(eth1Net+"/"+String.valueOf(eth1MaskInt), true, true);
				if(!routNetworkSet.contains(eth1Rout)){
					routNetworkSet.add(eth1Rout);
				}
			}
//			VpnNetwork mgtNetwork = hiveAp.getOrCreateCvgDPD().getMgtNetwork();
//			if(mgtNetwork != null){
//					if(mgtNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
//							mgtNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
//						for(VpnNetworkSub sub : mgtNetwork.getSubItems()){
//							routNetworkSet.add(new RoutingNetwork(sub.getIpNetwork(), false, true));
//						}
//					}
//			}
			if(hiveAp.getInternalNetworks()!=null){
				for(HiveApInternalNetwork internalNetworkset:hiveAp.getInternalNetworks()){
					String networkIp = internalNetworkset.getInternalNetwork();
					String networkMask = internalNetworkset.getNetmask();
					int networkMaskInt = CLICommonFunc.turnNetMaskToNum(networkMask);
					if(networkIp != null && !"".equals(networkIp) && networkMask != null && !"".equals(networkMask)){
						RoutingNetwork network = new RoutingNetwork(networkIp+"/"+String.valueOf(networkMaskInt),true, true);
						if(!routNetworkSet.contains(network)){
							routNetworkSet.add(network);
						}
					}
				}
			}
			if(hiveAp.getIpRoutes() != null){
				for(HiveApIpRoute ipRoute : hiveAp.getIpRoutes()){
					if(ipRoute.isDistributeBR()){
						String networkIp = ipRoute.getSourceIp();
						String networkMask = ipRoute.getNetmask();
						int networkMaskInt = CLICommonFunc.turnNetMaskToNum(networkMask);
						if(networkIp != null && !"".equals(networkIp) && networkMask != null && !"".equals(networkMask)){
							RoutingNetwork network = new RoutingNetwork(networkIp+"/"+String.valueOf(networkMaskInt),true, true);
							if(!routNetworkSet.contains(network)){
								routNetworkSet.add(network);
							}
						}
					}
				}
			}
		}
		
		//from static router
//		if(hiveAp.getIpRoutes() != null){
//			for(HiveApIpRoute route : hiveAp.getIpRoutes()){
//				int eth1MaskInt = CLICommonFunc.turnNetMaskToNum(route.getNetmask());
//				String startIp = CLICommonFunc.countIpAndMask(route.getSourceIp(), route.getNetmask());
//				routNetworkSet.add(new RoutingNetwork(startIp+"/"+String.valueOf(eth1MaskInt), true));
//			}
//		}
		if(routNetworkSet.size() > AhInterface.BR_MAX_ROUTE_COUNT && hiveAp.isBranchRouter()){
			String message = NmsUtil.getUserMessage("error.be.config.create.br.max.advertiseRoute", 
					new String[]{String.valueOf(AhInterface.BR_MAX_ROUTE_COUNT)});
			throw new CreateXMLException(message);
		}
		
		return new ArrayList<RoutingNetwork>(routNetworkSet);
	}
	
	public static List<ConfigTemplate> loadAllWlan(HiveAp hiveAp){
		List<ConfigTemplate> wlanList = new ArrayList<ConfigTemplate>();
		
		String allVpnSql = "select distinct VPN_GATEWAY_SETTING_ID from VPN_GATEWAY_SETTING where hiveapid=" + hiveAp.getId();
		List<?> allVpnId = MgrUtil.getQueryEntity().executeNativeQuery(allVpnSql);
		String vpnIdStr = "";
		for(Object selectItem : allVpnId){
			String gatewayId = selectItem.toString();
			if("".equals(vpnIdStr)){
				vpnIdStr += gatewayId;
			}else{
				vpnIdStr += ", " + gatewayId;
			}
		}
		if(!"".equals(vpnIdStr)){
			vpnIdStr = "(" + vpnIdStr + ")";
		}else{
			return wlanList;
		}
		
		String allWlanSql = "select distinct id from CONFIG_TEMPLATE where VPN_SERVICE_ID in " + vpnIdStr;
		List<?> allWlanId = MgrUtil.getQueryEntity().executeNativeQuery(allWlanSql);
		for(Object wlan : allWlanId){
			String wlanIdStr = wlan.toString();
			long wlanId = Long.valueOf(wlanIdStr);
			ConfigTemplate wlanObj = (ConfigTemplate)MgrUtil.getQueryEntity().findBoById(
					ConfigTemplate.class, wlanId, new ConfigLazyQueryBo());
			if(wlanObj != null){
				wlanList.add(wlanObj);
			}
		}

		return wlanList;
	}

	public boolean isConfigRouting() {
		return hiveAp.isVpnGateway() || 
				hiveAp.isBranchRouter();
	}

	public boolean isConfigRoutingProtocol() {
		return hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY && 
				routing != null &&
				routing.isEnableDynamicRouting();
	}

	public boolean isConfigRoutingRequest() {
		return hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER;
	}

	public int getRoutingSubNetworkSize() {
		return routNetworkList == null? 0 : routNetworkList.size();
	}
	
	public boolean isConfigRoutingSubNetwork(int index){
		return routNetworkList.get(index).isAvailably();
	}

	public String getRoutingSubNetworkValue(int index) {
		return routNetworkList.get(index).getNetwork();
	}

	public boolean isRoutingSubNetworkTunnel(int index) {
		return routNetworkList.get(index).isTunnelOnly();
	}

	public boolean isProtocolEnable() {
		return routing.isEnableDynamicRouting();
	}
	
	public boolean isConfigProtocolType(){
		return routing.getTypeFlag() == RoutingProfile.ENABLE_DRP_RIPV2 || 
				routing.getTypeFlag() == RoutingProfile.ENABLE_DRP_OSPF || 
				routing.getTypeFlag() == RoutingProfile.ENABLE_DRP_BGP;
	}

	public boolean isConfigProtocolRipv2() {
		return routing.getTypeFlag() == RoutingProfile.ENABLE_DRP_RIPV2;
	}

	public boolean isConfigProtocolOspf() {
		return routing.getTypeFlag() == RoutingProfile.ENABLE_DRP_OSPF;
	}

	public boolean isConfigProtocolBgp() {
		return routing.getTypeFlag() == RoutingProfile.ENABLE_DRP_BGP;
	}

	public boolean isEnableRouteRequest() {
		return hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER;
	}
	
	public int getRouteInterval(){
		return hiveAp.getRouteInterval();
	}

	public RoutingProtocolTypeValue getProtocolTypeValue() {
		if(isConfigProtocolRipv2()){
			return RoutingProtocolTypeValue.RIPV_2;
		}else if(isConfigProtocolOspf()){
			return RoutingProtocolTypeValue.OSPF;
		}else if(isConfigProtocolBgp()){
			return RoutingProtocolTypeValue.BGP;
		}else{
			return null;
		}
	}
	
	public AadvertiseType getAadvertise(){
		if(routing.isEnableRouteWan() && routing.isEnableRouteLan() && 
				hiveAp.getEth1Interface().isIfActive()){
			return AadvertiseType.both;
		}else if(routing.isEnableRouteWan()){
			return AadvertiseType.eth0;
		}else if(routing.isEnableRouteLan() && hiveAp.getEth1Interface().isIfActive()){
			return AadvertiseType.eth1;
		}else{
			return null;
		}
	}

	public boolean isConfigArea(){
		return routing.getArea() != null && !"".equals(routing.getArea());
	}
	
	public String getArea(){
		return routing.getArea();
	}
	
	public boolean isConfigRouterId(){
		return routing.getRouterId() != null && !"".equals(routing.getRouterId());
	}
	
	public String getRouterId(){
		return routing.getRouterId();
	}
	
	public boolean isConfigMd5Key(){
		return routing.isUseMD5() && routing.getPassword() != null && 
				!"".equals(routing.getPassword());
	}
	
	public String getMd5Key(){
		return routing.getPassword();
	}
	
	public boolean isConfigKeepalive(){
		return routing.getKeepalive() != null && routing.getKeepalive().intValue() > 0;
	}
	
	public int getKeepaliveValue(){
		return routing.getKeepalive().intValue();
	}
	
	public boolean isConfigSystemNumber(){
		return routing.getAutonmousSysNm() != null && routing.getAutonmousSysNm().intValue() > 0;
	}
	
	public int getSystemNumber(){
		return routing.getAutonmousSysNm();
	}
	
	public int getNeighborSize(){
		if(routing.getItems() == null || routing.getItems().isEmpty()){
			return 0;
		}else{
			return 1;
		}
	}
	
	public String getNeighborValue(int index){
		return routing.getItems().get(index).getNeighborsName();
	}
	
	private static class RoutingNetwork{
		
		private String network;
		
		private boolean tunnelOnly;
		
		private boolean availably;

		public RoutingNetwork(String network, boolean tunnelOnly, boolean availably){
			this.network = network;
			this.tunnelOnly = tunnelOnly;
			this.availably = availably;
		}
		
		public String getNetwork() {
			return network;
		}

		public boolean isTunnelOnly() {
			return tunnelOnly;
		}
		
		public boolean isAvailably() {
			return availably;
		}
		
		public boolean equals(Object obj){
			if(obj == null || !(obj instanceof RoutingNetwork)){
				return false;
			}
			RoutingNetwork objNet = (RoutingNetwork)obj;
			
			if(network != null && !network.equals(objNet.getNetwork())){
				return false;
			}else if(objNet.getNetwork() != null){
				return false;
			}
			
			if(tunnelOnly != objNet.isTunnelOnly()){
				return false;
			}
			
			return true;
		}
		
	}

    @Override
    public int getPolicyRuleSize() {
        return routingRuleList == null ? 0 : routingRuleList.size();
    }

    @Override
    public int getSourceType(int ruleIndex) {
        return routingRuleList.get(ruleIndex).getSourcetype();
    }

    @Override
    public String getSourceName(int ruleIndex) {
    	return RULE_PREFIX + (ruleIndex + 1);
    }

    @Override
    public List<String> getSourceValue(int ruleIndex) {
        int type = getSourceType(ruleIndex);
        String value = routingRuleList.get(ruleIndex).getSourcevalue();
        List<String> sourceValues = new ArrayList<>();

        if (type == RoutingProfilePolicyRule.MATCHMAP_SOURCE_INTERFACE) {
            Short intfType = Short.valueOf(value.substring(3));     // skip "Eth" 3 chars
            if (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_330 
            		|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_350) {
            	if (intfType <= 1)
                    sourceValues.add(CLI_ROUTER_ETH_PORT_PREFIX + intfType );
            } else if (hiveAp.getDeviceInfo().isSptEthernetMore_24()) {
                sourceValues.add(CLI_SWITCH_ETH_PORT_PREFIX + intfType);
            } else if (intfType <= 4) {
                sourceValues.add(CLI_ROUTER_ETH_PORT_PREFIX + intfType );
            }
        } else if (type == RoutingProfilePolicyRule.MATCHMAP_SOURCE_USERPROFILE
                && RoutingProfilePolicyRule.USER_PROFILES_ANY_GUEST.equals(value)) {
            return getGuestUserProfileNames();
        } else {
            sourceValues.add(value);
        }

        return sourceValues;
    }

    @Override
    public int getDestinationType(int ruleIndex) {
        return routingRuleList.get(ruleIndex).getDestinationtype();
    }

//    @Override
//    public String getDestinationName(int ruleIndex) {
//    	return DESTINATION_PREFIX + ruleIndex;
//    }

    @Override
    public String getDestinationValue(int ruleIndex) {
        return routingRuleList.get(ruleIndex).getDestinationvalue();
    }

    @Override
    public String getOut(int ruleIndex, int outIndex) {
        RoutingProfilePolicyRule rule = routingRuleList.get(ruleIndex);
        String portString = null;

        if (outIndex == 0)
            portString = rule.getOut1();
        else if (outIndex == 1)
            portString =  rule.getOut2();
        else if (outIndex == 2)
            portString =  rule.getOut3();
        else if (outIndex == 3)
            portString =  rule.getOut4();

        if (portString == null || portString.length() == 0)
            return null;

        short interfaceType;
        DeviceInterface wanIntf = null;
        try {
            if (portString.equals(String.valueOf(RoutingProfilePolicyRule.DEVICE_TYPE_PRIMARY_WAN_VALUE))) {
                wanIntf = hiveAp.getOrderWanInterface(0);
            } else if (portString.equals(String.valueOf(RoutingProfilePolicyRule.DEVICE_TYPE_BACKUP_WAN_1_VALUE))) {
                wanIntf = hiveAp.getOrderWanInterface(1);
            } else if (portString.equals(String.valueOf(RoutingProfilePolicyRule.DEVICE_TYPE_BACKUP_WAN_2_VALUE))) {
                wanIntf = hiveAp.getOrderWanInterface(2);
            }

            if (wanIntf != null) {
                interfaceType = wanIntf.getDeviceIfType();
            } else {
                interfaceType = Short.parseShort(portString);
            }

            return wanIntfPool.get(interfaceType);
        } catch (Exception exc) {
            log.error("RoutingProfileImpl", exc.getMessage(), exc);
        }

        return null;
    }

    private final static String CLI_SWITCH_ETH_PORT_PREFIX = "eth1/";
    private final static String CLI_ROUTER_ETH_PORT_PREFIX = "eth";
    private final static String CLI_PORT_ETH0 = "eth0";
    private final static String CLI_PORT_USB = "usbnet0";
    private final static String CLI_PORT_WIFI0 = "wifi0";
    private final static String CLI_PORT_VPN = "encrypted";
    private final static String CLI_PORT_DROP = "blackhole";
    private void prepareWanIntfPool() {
        try {
            PortGroupProfile portGroup = hiveAp.getPortGroup();
            List<Short> wanList = new ArrayList<>();
            if (portGroup != null) {
                List<Short> ethWanList = portGroup.getPortFinalValuesByPortType(AhInterface.DeviceInfType.Gigabit, PortAccessProfile.PORT_TYPE_WAN);
                List<Short> sfpWanList = portGroup.getPortFinalValuesByPortType(AhInterface.DeviceInfType.SFP, PortAccessProfile.PORT_TYPE_WAN);
                List<Short> usbWanList = portGroup.getPortFinalValuesByPortType(AhInterface.DeviceInfType.USB, PortAccessProfile.PORT_TYPE_WAN);
                if (ethWanList != null)
                    wanList.addAll(ethWanList);
                if (sfpWanList != null)
                    wanList.addAll(sfpWanList);
                if (usbWanList != null)
                    wanList.addAll(usbWanList);
            }

            if (hiveAp.getDeviceInfo().isSptEthernetMore_24()) {
                for (Short wanIf : wanList) {
                	String portName = DeviceInfType.getInstance(wanIf, hiveAp.getHiveApModel()).getCLIName(hiveAp.getHiveApModel());
                	wanIntfPool.put(wanIf, portName);
                }
            } else {
                wanIntfPool.put(AhInterface.DEVICE_IF_TYPE_ETH0, CLI_PORT_ETH0);
                for (Short wanIf : wanList) {
                    wanIntfPool.put(wanIf, wanIf == AhInterface.DEVICE_IF_TYPE_USB ?
                            CLI_PORT_USB : CLI_ROUTER_ETH_PORT_PREFIX + (wanIf - AhInterface.DEVICE_IF_TYPE_ETH0));
                }
                if ((hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_WP || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ)
                        && hiveAp.getRole(hiveAp.getWifi0Interface()) == AhInterface.ROLE_WAN) {
                    wanIntfPool.put(AhInterface.DEVICE_IF_TYPE_WIFI0, CLI_PORT_WIFI0);
                }
            }

            wanIntfPool.put(AhInterface.DEVICE_IF_TYPE_USB, CLI_PORT_USB);
            wanIntfPool.put(Short.parseShort(RoutingProfilePolicyRule.DEVICE_TYPE_CORPORATE_NETWORK_VPN_VALUE), CLI_PORT_VPN);
            wanIntfPool.put(Short.parseShort(RoutingProfilePolicyRule.DEVICE_TYPE_DROP_VALUE), CLI_PORT_DROP);
        } catch (Exception exc) {
            log.error("RoutingProfileImpl", exc.getMessage(), exc);
        }
    }

    private List<String> getGuestUserProfileNames() {
        List<String> names = new ArrayList<>();
        ConfigTemplate networkPolicy = hiveAp.getConfigTemplate();

        if (networkPolicy == null)
            return names;

        Map<Long, Integer> vlanId2NetworkType = new HashMap<>();
        List<ConfigTemplateVlanNetwork> vlanNetworkList = networkPolicy.getVlanNetwork();
        for (ConfigTemplateVlanNetwork vlanNetwork : vlanNetworkList) {
            if (vlanNetwork == null)
                continue;
            if (vlanNetwork.getVlan() != null && vlanNetwork.getNetworkObj() != null)
                vlanId2NetworkType.put(vlanNetwork.getVlan().getId(), vlanNetwork.getNetworkObj().getNetworkType());
        }

        Map<Long, Long> upId2VlanId = new HashMap<>();
        List<UserProfileImpl> userProfileList = config.getUserProfileList();
        for (UserProfileImpl impl : userProfileList) {
            try {
                if (impl == null)
                    continue;

                upId2VlanId.put(impl.getUserProfile().getId(), (long)impl.getUserProfile().getVlan().getId());
            } catch (Exception exc) {
                log.error("RoutingProfileImpl", exc.getMessage(), exc);
            }
        }

        Set<UserProfileVlanMapping> up2VlanMapping = networkPolicy.getUpVlanMapping();
        if (up2VlanMapping != null) {
            for (UserProfileVlanMapping mapping : up2VlanMapping) {
                if (mapping == null)
                    continue;
                if (mapping.getUserProfile() != null && mapping.getVlan() != null)
                    upId2VlanId.put(mapping.getUserProfile().getId(), mapping.getVlan().getId());
            }
        }

        for (UserProfileImpl impl : userProfileList) {
            if (impl == null)
                continue;

            Long vlanId = upId2VlanId.get(impl.getUserProfile().getId());
            if (vlanId == null)
                continue;

            Integer networkType = vlanId2NetworkType.get(vlanId);
            if (networkType != null && networkType == VpnNetwork.VPN_NETWORK_TYPE_GUEST) {
                names.add(impl.getUserProfileName());
            }
        }

        return names;
    }


}
