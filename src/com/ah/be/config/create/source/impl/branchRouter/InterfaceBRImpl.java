package com.ah.be.config.create.source.impl.branchRouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.common.CVGAndBRIpResourceManage;
import com.ah.be.config.create.source.InterfaceProfileInt;
import com.ah.be.config.create.source.InterfaceProfileInt.InterType;
import com.ah.be.config.create.source.impl.ConfigureProfileFunction;
import com.ah.be.config.create.source.impl.InterfaceProfileImpl;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.ConfigTemplateVlanNetwork;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApPreferredSsid;
import com.ah.bo.hiveap.HiveApSsidAllocation;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.network.BonjourGatewaySettings;
import com.ah.bo.network.DhcpServerOptionsCustom;
import com.ah.bo.network.DnsServiceProfile;
import com.ah.bo.network.DnsSpecificSettings;
import com.ah.bo.network.DomainNameItem;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.PortForwarding;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.SubNetworkResource;
import com.ah.bo.network.SubnetworkDHCPCustom;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnNetworkSub;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.SsidProfile;
import com.ah.util.CountryCode;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;
import com.ah.xml.be.config.EthDuplex;
import com.ah.xml.be.config.InterfaceRadioFixedAntennaValue;
import com.ah.xml.be.config.MgtDnsServerModeValue;

@SuppressWarnings("static-access")
public class InterfaceBRImpl extends InterfaceProfileImpl {
	
	private static final Tracer log = new Tracer(InterfaceBRImpl.class
			.getSimpleName());
	
	protected HiveAp hiveAp;
	
	private Map<InterType, ServiceFilter> ethServiceFilter;
	private List<SsidProfile> templateSsidList;
	private Map<InterType, ConfigTemplateSsid> templateEth;
	
	private Map<MgtType, DhcpServerInfo> dhcpInfoMap = new HashMap<MgtType, DhcpServerInfo>();
	
	private Map<InterType, DeviceInterface> interMap;
	
	private Set<String> disableSsidWifi0;
	private Set<String> disableSsidWifi1;
	
	private Map<InterType, PortAccessProfile> ethPortMap = null;
	private Map<InterType, List<String>> lanVlanMap = new HashMap<InterType, List<String>>();
	
	private boolean view;
	
	private List<String> mgt0DhcpValns = new ArrayList<String>();
	
	private List<Map<String, String>> natPolicyList = new ArrayList<Map<String, String>>();
	private List<String> natPolicyNameForSubNetworkList = new ArrayList<String>();
	private List<String> natPolicyNameForPortForwardingList = new ArrayList<String>();
	
	//interface ethx pse mode
	public static final String modeaf="802.3af";
	public static final String modeat="802.3at";
	public static final String modextend="802.3af-extended";
	
	private Map<Long, Integer> wanOrderMap = new HashMap<Long, Integer>();
	
	private void initWanOrderMap() {
		//key:AhInterface.DEVICE_IF_TYPE  value:wanOrder(priority)
		Map<Long, Integer> networkMap = hiveAp.getWanInfoByNetworkPolicy();
		Map<Long, Integer> deviceMap = hiveAp.getWanInfoByDevice();
		
		int maxWanOrder = 0;
		for (Iterator<Long> iter = deviceMap.keySet().iterator(); iter.hasNext();) {
			Long key = iter.next();
			Integer wanOrder = deviceMap.get(key);
			if (wanOrder > maxWanOrder) {
				maxWanOrder = wanOrder;
			}
			wanOrderMap.put(key, wanOrder);
		}
		
		for (Iterator<Long> iter = networkMap.keySet().iterator(); iter.hasNext();) {
			Long key = iter.next();
			if (wanOrderMap.get(key) != null) {
				continue;
			}
			maxWanOrder ++;
			wanOrderMap.put(key, maxWanOrder);
		}
		
		//wan priority from 2 to 4, all value need add 1.
		for (Iterator<Long> iter = wanOrderMap.keySet().iterator(); iter.hasNext();) {
			Long key = iter.next();
			Integer wanOrder = wanOrderMap.get(key);
			wanOrder += 1;
			wanOrderMap.put(key, wanOrder);
		}
	}
	
	public static class DhcpServerInfo {
		private Vlan vlan;
		private VpnNetwork vpnNetwork;
		private SubNetworkResource subNetwork;
		private VpnNetworkSub vpnNetworkSub;
		private List<DhcpServerOptionsCustom> dhcpCustoms;
		private List<String> hivemanagerList;
		private List<String[]> domainList;

		public VpnNetwork getVpnNetwork() {
			return vpnNetwork;
		}

		public void setVpnNetwork(VpnNetwork vpnNetwork) {
			this.vpnNetwork = vpnNetwork;
		}

		public Vlan getVlan() {
			return vlan;
		}

		public void setVlan(Vlan vlan) {
			this.vlan = vlan;
		}

		public SubNetworkResource getSubNetwork() {
			return subNetwork;
		}

		public void setSubNetwork(SubNetworkResource subNetwork) {
			this.subNetwork = subNetwork;
		}

		public VpnNetworkSub getVpnNetworkSub() {
			return vpnNetworkSub;
		}

		public void setVpnNetworkSub(VpnNetworkSub vpnNetworkSub) {
			this.vpnNetworkSub = vpnNetworkSub;
		}

		public List<String> getHivemanagerList() {
			if(hivemanagerList == null){
				hivemanagerList = new ArrayList<String>();
			}
			return hivemanagerList;
		}
		
		public void setHivemanagerList(List<String> hivemanagerList) {
			this.hivemanagerList = hivemanagerList;
		}

		public List<DhcpServerOptionsCustom> getDhcpCustoms() {
			if(dhcpCustoms == null) {
				dhcpCustoms = new ArrayList<DhcpServerOptionsCustom>();
			}
			return dhcpCustoms;
		}

		public void setDhcpCustoms(List<DhcpServerOptionsCustom> dhcpCustoms) {
			this.dhcpCustoms = dhcpCustoms;
		}

		public List<String[]> getDomainList() {
			if(domainList == null){
				domainList = new ArrayList<String[]>();
			}
			return domainList;
		}

		public void setDomainList(List<String[]> domainList) {
			this.domainList = domainList;
		}
	}

	public InterfaceBRImpl(HiveAp hiveAp, boolean view) throws Exception {
		super(hiveAp);
		this.hiveAp = hiveAp;
		this.view = view;
		initDhcpInfoMap();
		this.loadEthLanMap();
		this.loadmgt0DhcpKeepValns();

		setEthServiceFilter();

		//eth qos settings
		templateEth = new HashMap<InterType, ConfigTemplateSsid>();
		for (ConfigTemplateSsid template : hiveAp.getConfigTemplate()
				.getSsidInterfaces().values()) {
			if (InterType.eth0.name().equalsIgnoreCase(
					template.getInterfaceName())) {
				templateEth.put(InterType.eth0, template);
			}
			if (InterType.eth1.name().equalsIgnoreCase(
					template.getInterfaceName())) {
				templateEth.put(InterType.eth1, template);
			}
			if (InterType.eth2.name().equalsIgnoreCase(
					template.getInterfaceName())) {
				templateEth.put(InterType.eth2, template);
			}
			if (InterType.eth3.name().equalsIgnoreCase(
					template.getInterfaceName())) {
				templateEth.put(InterType.eth3, template);
			}
			if (InterType.eth4.name().equalsIgnoreCase(
					template.getInterfaceName())) {
				templateEth.put(InterType.eth4, template);
			}
			if (InterType.agg0.name().equals(template.getInterfaceName())) {
				templateEth.put(InterType.agg0, template);
			}
			if (InterType.red0.name().equals(template.getInterfaceName())) {
				templateEth.put(InterType.red0, template);
			}
		}

		interMap = new HashMap<InterType, DeviceInterface>();
		if(hiveAp.getEth0Interface() != null && !hiveAp.isSwitchProduct()){
			interMap.put(InterType.eth0, hiveAp.getEth0Interface());
		}
		if(hiveAp.getEth1Interface() != null){
			interMap.put(InterType.eth1, hiveAp.getEth1Interface());
		}
		if(hiveAp.getEth2Interface() != null){
			interMap.put(InterType.eth2, hiveAp.getEth2Interface());
		}
		if(hiveAp.getEth3Interface() != null){
			interMap.put(InterType.eth3, hiveAp.getEth3Interface());
		}
		if(hiveAp.getEth4Interface() != null){
			interMap.put(InterType.eth4, hiveAp.getEth4Interface());
		}
		if(hiveAp.getUSBInterface() != null){
			interMap.put(InterType.usb, hiveAp.getUSBInterface());
		}

		//set static IP address for BR as PPSK server
		if(hiveAp.isBranchRouter()){
			String ipNet = getMgtIpAndMask();
			if(ipNet.indexOf("/") > 0){
				hiveAp.setCfgIpAddress(ipNet.substring(0, ipNet.indexOf("/")));
			}else{
				hiveAp.setCfgIpAddress(ipNet);
			}
			for(ConfigTemplateSsid ssidTemp : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
				if(ssidTemp.getSsidProfile() != null && ssidTemp.getSsidProfile().getPpskServer() != null){
					if(hiveAp.getId().equals(ssidTemp.getSsidProfile().getPpskServer().getId())){
						ssidTemp.getSsidProfile().getPpskServer().setCfgIpAddress(hiveAp.getCfgIpAddress());
					}
				}
			}
		}
		
		disableSsidWifi0 = new HashSet<String>();
		disableSsidWifi1 = new HashSet<String>();
		for(HiveApSsidAllocation disableSsid : hiveAp.getDisabledSsids()){
			SsidProfile ssidObj = (SsidProfile)MgrUtil.getQueryEntity().findBoById(SsidProfile.class, disableSsid.getSsid());
			String ssidName = null;
			if(ssidObj != null){
				ssidName = ssidObj.getSsidName();
			}
			if(disableSsid.getInterType() == HiveApSsidAllocation.WIFI2G){
				if(hiveAp.getWifi0RadioProfile() != null && 
						(hiveAp.getWifi0RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_BG || 
								hiveAp.getWifi0RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG)){
					disableSsidWifi0.add(ssidName);
				}
				if(hiveAp.getWifi1RadioProfile() != null && 
						(hiveAp.getWifi1RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_BG || 
								hiveAp.getWifi1RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NG)){
					disableSsidWifi1.add(ssidName);
				}
			}
			if(disableSsid.getInterType() == HiveApSsidAllocation.WIFI5G){
				if(hiveAp.getWifi0RadioProfile() != null && 
						(hiveAp.getWifi0RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A || 
								hiveAp.getWifi0RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA)){
					disableSsidWifi0.add(ssidName);
				}
				if(hiveAp.getWifi1RadioProfile() != null && 
						(hiveAp.getWifi1RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A || 
								hiveAp.getWifi1RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA)){
					disableSsidWifi1.add(ssidName);
				}
			}
		}
		//PPSK Selfreg ssid down/up sync with open ssid
		for(ConfigTemplateSsid ssidTemp : hiveAp.getConfigTemplate().getSsidInterfaces().values()){
			if(ssidTemp.getSsidProfile() != null && ssidTemp.getSsidProfile().isEnablePpskSelfReg()){
				String ssidName = ssidTemp.getSsidProfile().getSsidName();
				String openSsidName = ssidTemp.getSsidProfile().getPpskOpenSsid();
				if(disableSsidWifi0.contains(ssidName)){
					disableSsidWifi0.add(openSsidName);
				}
				if(disableSsidWifi1.contains(ssidName)){
					disableSsidWifi1.add(openSsidName);
				}
			}
		}
		initNatPolicyData();
		if (natPolicyNameForSubNetworkList.size() > 16) {
			throw new CreateXMLException("the max number of 1:1 NAT policies on tunnel is over 16");
		}
		if (natPolicyNameForPortForwardingList.size() > 16) {
			throw new CreateXMLException("the max number of port forwarding policies on physical WAN is over 16");
		}
		initWanOrderMap();
	}
	
	private static String getIpFromNetwork(String network, int index, byte defaulGateway) {
		if(VpnNetworkSub.DEFAULT_GATEWAY_FIRST_IP == defaulGateway) {
			index++;
		}
		int flagIndex = network.indexOf("/");
		String ipAddr = network.substring(0, flagIndex);
		long ipNum = AhEncoder.ip2Long(ipAddr);
		long resIpNum = ipNum + index;
		return AhDecoder.long2Ip(resIpNum);
	}
	
	private void initNatPolicyData() {
		Map<MgtType, DhcpServerInfo> map = getMgtSubResourceMap();
		Map<String, String> networkMap = new HashMap<String, String>();
		if (map != null) {
			for (Iterator<MgtType> iter = map.keySet().iterator(); iter.hasNext();) {
				Map<String, String> dataMap = new HashMap<String, String>();
				MgtType key = iter.next();
				if (map.get(key) != null) {
					SubNetworkResource value = map.get(key).getSubNetwork();
					if (value != null && value.isEnableNat()) {
						dataMap.put("policyName", value.getNatPolicyName());
						dataMap.put("configMatch", "true");
						dataMap.put("configVirtualHost", "false");
						dataMap.put("matchInside", value.getLocalNetwork());
						dataMap.put("matchOutside", value.getNetwork());
						natPolicyList.add(dataMap);
						natPolicyNameForSubNetworkList.add(value.getNatPolicyName());
					}
					if (value != null) {
						networkMap.put(value.getParentLocalNetwork(), value.getLocalNetwork());
					}
				}
			}
		}
		List<VpnNetwork> vpnNetworkList = getAllVpnNetwork(hiveAp);
		if(null != vpnNetworkList) {
			for(VpnNetwork vpnNetwork : vpnNetworkList) {
				if (vpnNetwork != null) {
					List<PortForwarding> portForwardingList = vpnNetwork.getPortForwardings();
					if (portForwardingList != null) {
						for (PortForwarding forwarding : portForwardingList) {
							String parentLocalNetwork = null;
							byte defaultGateway = VpnNetworkSub.DEFAULT_GATEWAY_FIRST_IP;
							for(VpnNetworkSub vpnNetworkSub : vpnNetwork.getSubItems()) {
								if(forwarding.getKey() == vpnNetworkSub.getKey()) {
									parentLocalNetwork = vpnNetworkSub.getLocalIpNetwork();
									defaultGateway = vpnNetworkSub.getDefaultGateway();
									break;
								}
							}
							if(parentLocalNetwork == null || null == networkMap.get(parentLocalNetwork)) {
								continue;
							}
							
							String internalHostIPAddress = getIpFromNetwork(networkMap.get(parentLocalNetwork), forwarding.getPositionId(), defaultGateway);
							String policyName = (internalHostIPAddress + "-" + forwarding.getDestinationPortNumber()).replace(".", "-").replace("/", "-");
							
							if (forwarding.getProtocol() == PortForwarding.PROTOCOL_ANY) { //split 1 to 2 record.
								Map<String, String> dataMap = new HashMap<String, String>();
								dataMap.put("policyName", policyName + "_tcp");
								dataMap.put("configMatch", "false");
								dataMap.put("configVirtualHost", "true");
								dataMap.put("insideHost", internalHostIPAddress);
								dataMap.put("insidePort", forwarding.getInternalHostPortNumber());
								dataMap.put("outsidePort", forwarding.getDestinationPortNumber());
								dataMap.put("protocol", "tcp");
								natPolicyList.add(dataMap);
								natPolicyNameForPortForwardingList.add(policyName + "_tcp");
								dataMap = new HashMap<String, String>();
								dataMap.put("policyName", policyName + "_udp");
								dataMap.put("configMatch", "false");
								dataMap.put("configVirtualHost", "true");
								dataMap.put("insideHost", internalHostIPAddress);
								dataMap.put("insidePort", forwarding.getInternalHostPortNumber());
								dataMap.put("outsidePort", forwarding.getDestinationPortNumber());
								dataMap.put("protocol", "udp");
								natPolicyList.add(dataMap);
								natPolicyNameForPortForwardingList.add(policyName + "_udp");
							}
							else {
								if(forwarding.getProtocol() == PortForwarding.PROTOCOL_TCP) {
									policyName = policyName + "_tcp";
								} else {
									policyName = policyName + "_udp";
								}
								Map<String, String> dataMap = new HashMap<String, String>();
								dataMap.put("policyName", policyName);
								dataMap.put("configMatch", "false");
								dataMap.put("configVirtualHost", "true");
								dataMap.put("insideHost", internalHostIPAddress);
								dataMap.put("insidePort", forwarding.getInternalHostPortNumber());
								dataMap.put("outsidePort", forwarding.getDestinationPortNumber());
								String protocol = forwarding.getProtocol() == (PortForwarding.PROTOCOL_TCP) ? "tcp" : "udp" ;
								dataMap.put("protocol", protocol);
								natPolicyList.add(dataMap);
								natPolicyNameForPortForwardingList.add(policyName);
							}
						}
						
					}
				}	
			}
		}

	}
	
	private void initDhcpInfoMap() throws Exception{
		
		List<DhcpServerInfo> dhcpInfoList = new ArrayList<DhcpServerInfo>();
		
		//init VpnNetwork and Vlan
		List<VpnNetwork> networkList = getAllVpnNetwork(this.hiveAp);
		for(VpnNetwork vpnNetwork : networkList){
			Vlan vlan = hiveAp.getConfigTemplate().getVlanByNetwork(vpnNetwork);
			DhcpServerInfo dhcpInfo = new DhcpServerInfo();
			dhcpInfo.setVpnNetwork(vpnNetwork);
			dhcpInfo.setVlan(vlan);
			dhcpInfoList.add(dhcpInfo);
		}
		
		//init SubNetworkResource
		List<VpnNetwork> allVpnNetworks = new ArrayList<VpnNetwork>();
		for(DhcpServerInfo dhcpInfo : dhcpInfoList){
			VpnNetwork vpnNetwork = dhcpInfo.getVpnNetwork();
			allVpnNetworks.add(vpnNetwork);
			if(vpnNetwork != null && (vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
					vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT)){
				SubNetworkResource subResource = CVGAndBRIpResourceManage.getVpnNetworkRes(vpnNetwork, this.hiveAp, view);
				if(subResource != null){
					dhcpInfo.setSubNetwork(subResource);
				}
			}
		}
		//release SubNetworkResource that are not used
		if(!view && !allVpnNetworks.isEmpty()){
			CVGAndBRIpResourceManage.releaseUnusedSubNetworkResource(hiveAp.getOwner(), allVpnNetworks, hiveAp.getMacAddress());
		}
		
		//init VpnNetworkSub
		for(DhcpServerInfo dhcpInfo : dhcpInfoList) {
			if(dhcpInfo.getSubNetwork() == null){
				continue;
			}
			String parentNetwork = dhcpInfo.getSubNetwork().getParentNetwork();
			for(VpnNetworkSub netSub : dhcpInfo.getVpnNetwork().getSubItems()){
				if(parentNetwork.equals(netSub.getIpNetwork())){
					dhcpInfo.setVpnNetworkSub(netSub);
					break;
				}
			}
		}
		
		//init HiveMananger list
		for(DhcpServerInfo dhcpInfo : dhcpInfoList) {
			VpnNetwork vpnNetwork = dhcpInfo.getVpnNetwork();
			List<DhcpServerOptionsCustom> dhcpCustoms = new ArrayList<DhcpServerOptionsCustom>();
			if(vpnNetwork != null && (vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
					vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT)){
				int key = dhcpInfo.getVpnNetworkSub().getKey();
				for(SubnetworkDHCPCustom subCustom : dhcpInfo.getVpnNetwork().getSubnetworkDHCPCustoms()){
					if(key == subCustom.getKey()){
						dhcpCustoms.add(subCustom);
					}
				}
			}else{
				dhcpCustoms = dhcpInfo.getVpnNetwork().getCustomOptions();
			}
			
			if(dhcpCustoms != null){
				for(DhcpServerOptionsCustom dhcpCustom : dhcpCustoms){
					/** HiveManager Name = 225, HiveManager IP = 226 */
					if(dhcpCustom.getNumber() == 225 || dhcpCustom.getNumber() == 226){
						dhcpInfo.getHivemanagerList().add(dhcpCustom.getValue(hiveAp.getSoftVer()));
					}
				}
			}
		}
		
		//init DhcpServerOptionsCustom list
		for(DhcpServerInfo dhcpInfo : dhcpInfoList) {
			VpnNetwork vpnNetwork = dhcpInfo.getVpnNetwork();
			if(vpnNetwork != null && (vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
					vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT)){
				int key = dhcpInfo.getVpnNetworkSub().getKey();
				for(SubnetworkDHCPCustom subCustom : dhcpInfo.getVpnNetwork().getSubnetworkDHCPCustoms()){
					if(key == subCustom.getKey() && subCustom.getNumber() != 225 && subCustom.getNumber() != 226){
						dhcpInfo.getDhcpCustoms().add(subCustom);
					}
				}
			}else if(vpnNetwork.getCustomOptions() != null){
				for(DhcpServerOptionsCustom subCustom : vpnNetwork.getCustomOptions()){
					if(subCustom.getNumber() != 225 && subCustom.getNumber() != 226){
						dhcpInfo.getDhcpCustoms().add(subCustom);
					}
				}
			}
			
			boolean isMgt0Dhcp = false;
			try{
				isMgt0Dhcp = dhcpInfo.getVlan().getId().equals(this.hiveAp.getConfigTemplate().getVlan().getId());
			}catch(Exception e){}
			
			if(isMgt0Dhcp && vpnNetwork.getNetworkType() != VpnNetwork.VPN_NETWORK_TYPE_GUEST){
				
				boolean exPpskServer=false, exRadServerAccounting = false, exRadServerAuth = false;
				//BR100 not support radius server
				if(this.hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
					exRadServerAccounting = true;
					exRadServerAuth = true;
				}
				for(DhcpServerOptionsCustom custom : dhcpInfo.getDhcpCustoms()){
					if(custom.getNumber() == 229){
						exPpskServer = true;
					}else if(custom.getNumber() == 231){
						exRadServerAccounting = true;
					}else if(custom.getNumber() == 230){
						exRadServerAuth = true;
					}
				}
				if(!exPpskServer && dhcpInfo.getSubNetwork() != null){
					DhcpServerOptionsCustom opCustom = new DhcpServerOptionsCustom();
					opCustom.setType(DhcpServerOptionsCustom.CUSTOM_TYYPE_IP);
					opCustom.setNumber((short)229);
					opCustom.setValue(dhcpInfo.getSubNetwork().getFirstIp());
					dhcpInfo.getDhcpCustoms().add(opCustom);
				}
				if(!exRadServerAuth && dhcpInfo.getSubNetwork() != null){
					DhcpServerOptionsCustom opCustom = new DhcpServerOptionsCustom();
					opCustom.setType(DhcpServerOptionsCustom.CUSTOM_TYYPE_IP);
					opCustom.setNumber((short)230);
					opCustom.setValue(dhcpInfo.getSubNetwork().getFirstIp());
					dhcpInfo.getDhcpCustoms().add(opCustom);
				}
				if(!exRadServerAccounting && dhcpInfo.getSubNetwork() != null){
					DhcpServerOptionsCustom opCustom = new DhcpServerOptionsCustom();
					opCustom.setType(DhcpServerOptionsCustom.CUSTOM_TYYPE_IP);
					opCustom.setNumber((short)231);
					opCustom.setValue(dhcpInfo.getSubNetwork().getFirstIp());
					dhcpInfo.getDhcpCustoms().add(opCustom);
				}
			}
		}
		
		//init domainList list
		for(DhcpServerInfo dhcpInfo : dhcpInfoList) {
			VpnNetwork network = dhcpInfo.getVpnNetwork();
			if(network != null && network.getVpnDnsService() != null){
				List<String[]> domainList = new ArrayList<String[]>();
				DnsServiceProfile dns;
				if(dhcpInfo.getVpnNetworkSub() != null 
						&& dhcpInfo.getVpnNetworkSub().isOverrideDNSService() 
						&& dhcpInfo.getVpnNetworkSub().getDnsService() != null){
					 dns = dhcpInfo.getVpnNetworkSub().getDnsService();
				}else{
					dns = network.getVpnDnsService();
				}
				if(dns.getServiceType() == DnsServiceProfile.SEPARATE_DNS){
					if(dns.getSpecificInfos() != null){
						for(DnsSpecificSettings spDomain : dns.getSpecificInfos()){
							String domainName = spDomain.getDomainName();
							String ipAddr = CLICommonFunc.getIpAddress(spDomain.getDnsServer(), this.hiveAp).getIpAddress();
							domainList.add(new String[]{domainName, ipAddr});
						}
					}
					if(dns.getDomainObj() != null && dns.getDomainObj().getItems() != null){
						for(DomainNameItem domainItem : dns.getDomainObj().getItems()){
							if(domainItem != null && domainItem.getDomainName() != null){
								String[] childDomain = domainItem.getDomainName().split("\n");
								for(int index=0; index<childDomain.length; index++){
									if(childDomain[index] != null && !"".equals(childDomain[index])){
										domainList.add(new String[]{childDomain[index]});
									}
								}
							}
						}
					}
				}
				dhcpInfo.setDomainList(domainList);
			}
		}
		
		//init dhcpInfoMap
		Iterator<DhcpServerInfo> dhcpItems;
		if(hiveAp.getDeviceInfo().isSptEthernetMore_24()){
			//mapping for mgt0
			if(dhcpInfoMap.get(MgtType.mgt0) == null){
				dhcpItems = dhcpInfoList.iterator();
				while(dhcpItems.hasNext()){
					DhcpServerInfo dhcpInfo = dhcpItems.next();
					boolean isMgt0Vlan = false;
					try{
						isMgt0Vlan = dhcpInfo.getVlan().getId().equals(this.hiveAp.getConfigTemplate().getVlan().getId());
					}catch(Exception e){}
					
					if(isMgt0Vlan){
						dhcpInfoMap.put(MgtType.mgt0, dhcpInfo);
						dhcpItems.remove();
						break;
					}
				}
			}
			
			dhcpItems = dhcpInfoList.iterator();
			while(dhcpItems.hasNext()){
				DhcpServerInfo dhcpInfo = dhcpItems.next();
				int vlan = CLICommonFunc.getVlan(dhcpInfo.getVlan(), hiveAp).getVlanId();
				MgtType type = new MgtType("vlan-"+vlan);
				dhcpInfoMap.put(type, dhcpInfo);
			}
			
			//build mapping between interface mgt0.X and SubNetworkResource
			List<SubNetworkResource> resources = new ArrayList<SubNetworkResource>();
			for(MgtType type : dhcpInfoMap.keySet()){
				if(dhcpInfoMap.get(type).getSubNetwork() == null){
					continue;
				}
				SubNetworkResource subResource = dhcpInfoMap.get(type).getSubNetwork();
				subResource.setHiveApMgtx(getVlanShortType(type));
				resources.add(subResource);
			}
			CVGAndBRIpResourceManage.updateMgtxNetworkMapping(hiveAp.getOwner(), resources);
		}else{
			//old mapping
			dhcpItems = dhcpInfoList.iterator();
			while(dhcpItems.hasNext()){
				DhcpServerInfo dhcpInfo = dhcpItems.next();
				MgtType type = null;
				if(dhcpInfo.getSubNetwork() != null && dhcpInfo.getSubNetwork().getHiveApMgtx() > 0){
					type = getMgtType(dhcpInfo.getSubNetwork().getHiveApMgtx());
				}
				if(type != null) {
					dhcpInfoMap.put(type, dhcpInfo);
					dhcpItems.remove();
				}
			}
			
			//mapping for mgt0
			if(dhcpInfoMap.get(MgtType.mgt0) == null){
				dhcpItems = dhcpInfoList.iterator();
				while(dhcpItems.hasNext()){
					DhcpServerInfo dhcpInfo = dhcpItems.next();
					boolean isMgt0Vlan = false;
					try{
						isMgt0Vlan = dhcpInfo.getVlan().getId().equals(this.hiveAp.getConfigTemplate().getVlan().getId());
					}catch(Exception e){}
					
					if(isMgt0Vlan){
						dhcpInfoMap.put(MgtType.mgt0, dhcpInfo);
						dhcpItems.remove();
						break;
					}
				}
			}
			
			//mapping assign freedom
			dhcpItems = dhcpInfoList.iterator();
			while(dhcpItems.hasNext()){
				DhcpServerInfo dhcpInfo = dhcpItems.next();
				for(int i=1; i<=16; i++){
					MgtType type = getMgtType((short)i);
					if(!dhcpInfoMap.containsKey(type)) {
						dhcpInfoMap.put(type, dhcpInfo);
						break;
					}
				}
				dhcpItems.remove();
			}
			
			//build mapping between interface mgt0.X and SubNetworkResource
			List<SubNetworkResource> resources = new ArrayList<SubNetworkResource>();
			for(MgtType type : dhcpInfoMap.keySet()){
				if(dhcpInfoMap.get(type).getSubNetwork() == null){
					continue;
				}
				SubNetworkResource subResource = dhcpInfoMap.get(type).getSubNetwork();
				subResource.setHiveApMgtx(getMgtShortType(type));
				resources.add(subResource);
			}
			CVGAndBRIpResourceManage.updateMgtxNetworkMapping(hiveAp.getOwner(), resources);
		}
	}
	
	private void loadEthLanMap() throws CreateXMLException{
	    //TODO Port Template Profiles
		ethPortMap = new HashMap<InterType, PortAccessProfile>();
		if(hiveAp.getPortGroup() != null){
			for(PortBasicProfile portProfile : hiveAp.getPortGroup().getBasicProfiles()){
				if(portProfile.getETHs() == null){
					continue;
				}
				for(int i=0; i<portProfile.getETHs().length; i++) {
					if("1".equals(portProfile.getETHs()[i])){
						ethPortMap.put(InterType.eth1, portProfile.getAccessProfile());
					}else if("2".equals(portProfile.getETHs()[i])){
						ethPortMap.put(InterType.eth2, portProfile.getAccessProfile());
					}else if("3".equals(portProfile.getETHs()[i])){
						ethPortMap.put(InterType.eth3, portProfile.getAccessProfile());
					}else if("4".equals(portProfile.getETHs()[i])){
						ethPortMap.put(InterType.eth4, portProfile.getAccessProfile());
					}
				}
			}
			
			for(InterType lanType : ethPortMap.keySet()){
				PortAccessProfile accessProfile = ethPortMap.get(lanType);
				if(accessProfile == null){
					continue;
				}
				if(lanVlanMap.get(lanType) == null){
					lanVlanMap.put(lanType, new ArrayList<String>());
				}
				if(accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_8021Q && accessProfile.getNativeVlan() != null){
					int nativeVlan = CLICommonFunc.getVlan(accessProfile.getNativeVlan(), this.hiveAp).getVlanId();
					String allowedVlanStr = accessProfile.getAllowedVlan();
					if(allowedVlanStr == null || "".equals(allowedVlanStr)){
						allowedVlanStr = String.valueOf(nativeVlan);
					}else{
						allowedVlanStr = allowedVlanStr + "," + String.valueOf(nativeVlan);
					}
					allowedVlanStr = allowedVlanStr.toLowerCase();
					if(allowedVlanStr.contains("all")){
						continue;
					}
					lanVlanMap.get(lanType).addAll(CLICommonFunc.mergeRangeList(allowedVlanStr));
				}
			}
			
			//Fix bug 22511, BR100 should not allow "allow vlan" more than 16.
			if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
				int maxVlans = 16;
				for(List<String> allowVlanList : lanVlanMap.values()){
					boolean[] mergeList = new boolean[CLICommonFunc.MAX_MERGE_RANGE];
					String allowedVlanStr = null;
					for(String vlans : allowVlanList){
						CLICommonFunc.mergeRange(mergeList, vlans);
						if(allowedVlanStr == null){
							allowedVlanStr = vlans;
						}else{
							allowedVlanStr += "," + vlans;
						}
					}
					int allowVlanCount = 0;
					for(int i=0; i<mergeList.length; i++){
						if(mergeList[i]){
							allowVlanCount ++;
						}
					}
					if(allowVlanCount > maxVlans){
						String[] errParams = {allowedVlanStr, String.valueOf(maxVlans)};
						String errMsg = NmsUtil.getUserMessage(
								"error.be.config.create.maxAllowVlan.br100", errParams);
						throw new CreateXMLException(errMsg);
					}
				}
			}
		}
	}
	
	private void loadmgt0DhcpKeepValns(){
		BonjourGatewaySettings bonjourGateway = hiveAp.getConfigTemplate().getBonjourGw();
		if(bonjourGateway != null){
			List<String> list = CLICommonFunc.mergeRangeList(bonjourGateway.getVlans());
			for(String vlan : list){
				if(vlan.indexOf("-")>0){
					vlan = vlan.replaceAll("\\s*\\-\\s*"," ");
				}
				mgt0DhcpValns.add(vlan);
			}
		}
	}
	
//	private Map<MgtType, List<String[]>> loadVPNDomainListMap() throws CreateXMLException{
//		Map<MgtType, List<String[]>> resMap = new HashMap<MgtType, List<String[]>>();
//		
//
//		return resMap;
//	}
	
	public static int getUserProfileVlan(UserProfile userProfile, HiveAp hiveAp){
		int vlan = -1;
		if(userProfile == null){
			return vlan;
		}
		
		try{
			Vlan userVlan = CLICommonFunc.getMappingVlan(userProfile, hiveAp.getConfigTemplate());
			if(userVlan != null){
				vlan = CLICommonFunc.getVlan(userVlan, hiveAp).getVlanId();
			}
			return vlan;
		}catch(Exception ex){
			log.error("load UserProfile Vlan error", ex);
			return vlan;
		}
	}
	
	public static int getNativeVlanForLan(PortAccessProfile accessProfile, HiveAp hiveAp){
		int vlan = 1;
		if(accessProfile == null){
			return vlan;
		}
		
		try{
			Vlan nativeVlan = null;
			if(accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_8021Q){
				nativeVlan = accessProfile.getNativeVlan();
			}
			if(nativeVlan != null){
				vlan = CLICommonFunc.getVlan(nativeVlan, hiveAp).getVlanId();
			}
			return vlan;
		}catch(Exception ex){
			log.error("load native vlan error", ex);
			return vlan;
		}
	}
	
	//current allowed vlan is string
//	public static List<String> getRegularVlanForLan(PortAccessProfile accessProfile, HiveAp hiveAp){
//		List<Integer> resVlan = new ArrayList<Integer>();
//		if(lanProfile == null){
//			return resVlan;
//		}
//		
//		try{
//			int intVlan;
//			if(lanProfile.getRegularVlans() != null && !lanProfile.getRegularVlans().isEmpty()){
//				for(Vlan vlan : lanProfile.getRegularVlans()){
//					intVlan = CLICommonFunc.getVlan(vlan, hiveAp).getVlanId();
//					resVlan.add(intVlan);
//				}
//			}
//			return resVlan;
//		}catch(Exception ex){
//			Log.error("load regular vlan error", ex);
//			return resVlan;
//		}
//	}
	
	public static List<VpnNetwork> getAllVpnNetwork(HiveAp hiveAp){
		List<ConfigTemplate> wlanList = new ArrayList<ConfigTemplate>();
		wlanList.add(hiveAp.getConfigTemplate());
		return getAllVpnNetwork(wlanList);
	}
	
	public static List<VpnNetwork> getAllVpnNetwork(List<ConfigTemplate> wlanList){
		ConcurrentMap<Long, VpnNetwork> resMap = new ConcurrentHashMap<Long, VpnNetwork>();
		for(ConfigTemplate wlan : wlanList){
			if(wlan.getVlanNetwork() != null){
				for(ConfigTemplateVlanNetwork vlanNet : wlan.getVlanNetwork()){
					if(vlanNet.getNetworkObj() != null && !vlanNet.isBlnRemoved()){
						resMap.putIfAbsent(vlanNet.getNetworkObj().getId(), vlanNet.getNetworkObj());
					}
				}
			}
		}
		return new ArrayList<VpnNetwork>(resMap.values());
	}
	
	public static MgtType getMgtType(short type){
		switch(type){
		case 0:
			return MgtType.mgt0;
		case 1:
			return MgtType.mgt01;
		case 2:
			return MgtType.mgt02;
		case 3:
			return MgtType.mgt03;
		case 4:
			return MgtType.mgt04;
		case 5:
			return MgtType.mgt05;
		case 6:
			return MgtType.mgt06;
		case 7:
			return MgtType.mgt07;
		case 8:
			return MgtType.mgt08;
		case 9:
			return MgtType.mgt09;
		case 10:
			return MgtType.mgt010;
		case 11:
			return MgtType.mgt011;
		case 12:
			return MgtType.mgt012;
		case 13:
			return MgtType.mgt013;
		case 14:
			return MgtType.mgt014;
		case 15:
			return MgtType.mgt015;
		case 16:
			return MgtType.mgt016;
		default:
			return null;
		}
	}
	
	public static short getVlanShortType(MgtType type){
		if(type == MgtType.mgt0){
			return (short)0;
		}else {
			int startPostion = type.getValue().indexOf("vlan-")+5;
			String value = type.getValue().substring(startPostion, type.getValue().length());
			return Short.valueOf(value);
		}
	}
	
	public static short getMgtShortType(MgtType type){
		if(type == MgtType.mgt0){
			return (short)0;
		}else if(type == MgtType.mgt01){
			return (short)1;
		}else if(type == MgtType.mgt02){
			return (short)2;
		}else if(type == MgtType.mgt03){
			return (short)3;
		}else if(type == MgtType.mgt04){
			return (short)4;
		}else if(type == MgtType.mgt05){
			return (short)5;
		}else if(type == MgtType.mgt06){
			return (short)6;
		}else if(type == MgtType.mgt07){
			return (short)7;
		}else if(type == MgtType.mgt08){
			return (short)8;
		}else if(type == MgtType.mgt09){
			return (short)9;
		}else if(type == MgtType.mgt010){
			return (short)10;
		}else if(type == MgtType.mgt011){
			return (short)11;
		}else if(type == MgtType.mgt012){
			return (short)12;
		}else if(type == MgtType.mgt013){
			return (short)13;
		}else if(type == MgtType.mgt014){
			return (short)14;
		}else if(type == MgtType.mgt015){
			return (short)15;
		}else if(type == MgtType.mgt016){
			return (short)16;
		}
		return -1;
	}

	private void setSsidList() {
		templateSsidList = new ArrayList<SsidProfile>();

		if (hiveAp.getConfigTemplate().getSsidInterfaces() != null) {
			for (ConfigTemplateSsid templateSsid : hiveAp.getConfigTemplate()
					.getSsidInterfaces().values()) {
				if (!InterType.eth0.name().equalsIgnoreCase(
						templateSsid.getInterfaceName())
						&& !ConfigureProfileFunction.INTERFACE_WIRELESS_MESH
								.equalsIgnoreCase(templateSsid
										.getInterfaceName())
						&& !InterType.eth1.name().equalsIgnoreCase(
								templateSsid.getInterfaceName())
						&& !InterType.eth2.name().equalsIgnoreCase(
								templateSsid.getInterfaceName())
						&& !InterType.eth3.name().equalsIgnoreCase(
								templateSsid.getInterfaceName())
						&& !InterType.eth4.name().equalsIgnoreCase(
								templateSsid.getInterfaceName())
						&& !InterType.agg0.name().equalsIgnoreCase(
								templateSsid.getInterfaceName())
						&& !InterType.red0.name().equalsIgnoreCase(
								templateSsid.getInterfaceName())) {
					templateSsidList.add(templateSsid.getSsidProfile());
				}
			}
		}
	}
	
	public String getHiveApGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.configuration");
	}
	
	public String getHiveApName(){
		return hiveAp.getHostName();
	}

	public String getApVersion() {
		return hiveAp.getSoftVer();
	}
	
	public boolean isInterShutdown(InterType type){
		if(NmsUtil.compareSoftwareVersion("5.0.3.0", this.hiveAp.getSoftVer()) > 0){
			return isInterShutdown_old(type);
		}else{
			return isInterShutdown_new(type);
		}
	}

	private boolean isInterShutdown_old(InterType type) {
		if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100 && type == InterType.eth1){
			return false;
		}
		short ehtStatus = interMap.get(type).getAdminState();
		return ehtStatus == AhInterface.ADMIN_STATE_DOWM;
	}
	
	private boolean isInterShutdown_new(InterType type) {
		return interMap.get(type).getAdminState() == AhInterface.ADMIN_STATE_DOWM;
	}

	public String getInterSpeed(InterType type) {
		String speedStr = null;
		short speed = interMap.get(type).getSpeed();
		switch (speed) {
		case 0:
			speedStr = "auto";
			break;
		case 1:
			speedStr = "10";
			break;
		case 2:
			speedStr = "100";
			break;
		case 3:
			speedStr = "1000";
			break;
		}
		return speedStr.toLowerCase();
	}

	public EthDuplex getInterDuplex(InterType type) {
		EthDuplex duplexStr = null;
		switch (interMap.get(type).getDuplex()) {
		case 0:
			duplexStr = EthDuplex.AUTO;
			break;
		case 1:
			duplexStr = EthDuplex.HALF;
			break;
		case 2:
			duplexStr = EthDuplex.FULL;
			break;
		}
		return duplexStr;
	}

	public boolean isInterConfigQosClass(InterType type) {
		if(type == InterType.eth0){
			return false;
		}
		ConfigTemplateSsid template = templateEth.get(type);
		QosClassification classMap = hiveAp.getConfigTemplate().getClassifierMap();
		boolean autoApply = classMap != null && !hiveAp.getConfigTemplate().getEnabledMapOverride() && (
				classMap.getMacOuisEnabled() || classMap.getNetworkServicesEnabled() || classMap.getSsidEnabled() ||
				(classMap.getGeneralEnabled() && classMap.getPrtclP() != null && !"".equals(classMap.getPrtclP())) ||
				(classMap.getGeneralEnabled() && classMap.getPrtclD() != null && !"".equals(classMap.getPrtclD()))
		);
		boolean manualApply =  hiveAp.getConfigTemplate().getEnabledMapOverride() && template != null && (template.getNetworkServicesEnabled() || template.getMacOuisEnabled() || 
			template.getSsidEnabled() || template.getCheckE() ||
			template.getCheckP() || template.getCheckD() || template.getSsidOnlyEnabled());
		return autoApply || manualApply;
		
//		return hiveAp.getConfigTemplate().getClassifierMap() != null;
	}

	public String getInterQosClassifier(InterType type) {
		if(templateEth.get(type) != null){
			return templateEth.get(type).getInterfaceName();
		}else{
			return null;
		}
	}

	public boolean isConfigInterQosMarker(InterType type) {
		if(type == InterType.eth0){
			return false;
		}
		ConfigTemplateSsid template = templateEth.get(type);
		boolean manualApply = hiveAp.getConfigTemplate().getEnabledMapOverride() && template != null && 
			(template.getCheckET() || template.getCheckPT() || template.getCheckDT());
		
		QosMarking markMap = hiveAp.getConfigTemplate().getMarkerMap();
		boolean autoApply = markMap != null && !hiveAp.getConfigTemplate().getEnabledMapOverride() && (
				(markMap.getPrtclD() != null && !"".equals(markMap.getPrtclD())) ||
				(markMap.getPrtclP() != null && !"".equals(markMap.getPrtclP()))
		);
		return autoApply || manualApply;
		
//		return hiveAp.getConfigTemplate().getMarkerMap() != null;
	}

	public String getInterQosMarker(InterType type) {
		if(templateEth.get(type) != null){
			return templateEth.get(type).getInterfaceName();
		}else{
			return null;
		}
	}

	public String getMgtIpAndMask() throws CreateXMLException {
		VpnNetwork network = dhcpInfoMap.get(MgtType.mgt0).getVpnNetwork();
		if(network != null){
			if(network.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
					network.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
				SubNetworkResource subNet = dhcpInfoMap.get(MgtType.mgt0).getSubNetwork();
				String networkIp = subNet.getNetwork();
				int index = networkIp.indexOf("/");
				networkIp = networkIp.substring(index+1);
				return subNet.getFirstIp() + "/" + networkIp;
			}else{
				return getFirstIp(network.getIpAddressSpace());
			}
		}else{
			return "";
		}
	}

	public boolean isConfigMgtHive() {
		HiveProfile hiveProf = hiveAp.getConfigTemplate().getHiveProfile();
		return hiveProf != null && !hiveProf.getDefaultFlag();
	}

	public String getMgtBindHive() {
		return hiveAp.getConfigTemplate().getHiveProfile().getHiveName();
	}
	
	public boolean isConfigMgtVlan(){
		return this.hiveAp.getConfigTemplate().getVlan() != null;
//		return mgtMap.get(MgtType.mgt0) != null && mgtMap.get(MgtType.mgt0).getVlan() != null;
	}

	public int getMgtVlanId() throws CreateXMLException {
		return CLICommonFunc.getVlan(hiveAp.getConfigTemplate().getVlan(), this.hiveAp).getVlanId();
	}

	public boolean isConfigInterMode(InterfaceMode mode, InterType type)
			throws CreateXMLException {
		if(type == InterType.eth0){
			return mode == InterfaceMode.wan;
		}else if (InterType.eth1.compareTo(type) <=0 && InterType.eth4.compareTo(type) >= 0) {
			boolean isMode = mode == getInterfaceModeFromLan(ethPortMap.get(type));
			
			if (isMode) {
				if (mode == InterfaceMode.wan) {
					return NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.0.2.0") >= 0;
				}
				return true;
			}
		}
		return false;
	}
	
	private InterfaceMode getInterfaceModeFromLan(PortAccessProfile portProfile){
		if(NmsUtil.compareSoftwareVersion("5.0.3.0", this.hiveAp.getSoftVer()) > 0){
			return getInterfaceModeFromLan_old(portProfile);
		}else{
			return getInterfaceModeFromLan_new(portProfile);
		}
	}
	
	private InterfaceMode getInterfaceModeFromLan_old(PortAccessProfile portProfile){
		if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
			return InterfaceMode.bridge8021q;
		}
		if(portProfile == null){
			return InterfaceMode.bridgeAccess;
		}
		if(portProfile.getPortType() == PortAccessProfile.PORT_TYPE_8021Q){
			return InterfaceMode.bridge8021q;
		}else{
			return InterfaceMode.bridgeAccess;
		}
	}
	
	private InterfaceMode getInterfaceModeFromLan_new(PortAccessProfile portProfile){
		if(portProfile == null){
			return InterfaceMode.bridgeAccess;
		}
		if(portProfile.getPortType() == PortAccessProfile.PORT_TYPE_8021Q){
			return InterfaceMode.bridge8021q;
		}else if(portProfile.isEnabled8021X()){
			return InterfaceMode.bridgeAccess;
		}else if(portProfile.getPortType() == PortAccessProfile.PORT_TYPE_WAN){
			return InterfaceMode.wan;
		}else{
			return InterfaceMode.bridgeAccess;
		}
	}
	
	public boolean isEnableWanNat(InterType type){
		if(type == InterType.eth0){
			return hiveAp.getEth0Interface().isEnableNat();
		}else if(type == InterType.usb){
			return hiveAp.getUSBInterface().isEnableNat();
		}else if(type == InterType.wifi0){
			return hiveAp.getWifi0Interface().isEnableNat();
		}else if(type == InterType.wifi1){
			return hiveAp.getWifi1Interface().isEnableNat();
		}else if(type == InterType.eth1){
			return hiveAp.getEth1Interface().isEnableNat();
		}else if(type == InterType.eth2){
			return hiveAp.getEth2Interface().isEnableNat();
		}else if(type == InterType.eth3){
			return hiveAp.getEth3Interface().isEnableNat();
		}else if(type == InterType.eth4){
			return hiveAp.getEth4Interface().isEnableNat();
		}else{
			return false;
		}
	}
	
	public boolean isEnableWanNatPolicy(InterType type) {
		if (type == InterType.eth0) {
			return !hiveAp.getEth0Interface().isDisablePortForwarding();
		} else if (type == InterType.usb) {
			return !hiveAp.getUSBInterface().isDisablePortForwarding();
		} else if (type == InterType.wifi0) {
			return !hiveAp.getWifi0Interface().isDisablePortForwarding();
		} else if (type == InterType.wifi1) {
			return !hiveAp.getWifi1Interface().isDisablePortForwarding();
		} else if(type == InterType.eth1){
			return !hiveAp.getEth1Interface().isDisablePortForwarding();
		} else if(type == InterType.eth2){
			return !hiveAp.getEth2Interface().isDisablePortForwarding();
		} else if(type == InterType.eth3){
			return !hiveAp.getEth3Interface().isDisablePortForwarding();
		} else if(type == InterType.eth4){
			return !hiveAp.getEth4Interface().isDisablePortForwarding();
		} else {
			return false;
		}
	}
	
	public int getWanInterfacePriority(DeviceInfType type, int index) {
		long key = type.getFinalValue(index, this.hiveAp.getHiveApModel());
		return wanOrderMap.get(key) != null ? wanOrderMap.get(key) : 0;
	}
	
	public int getInterfacePriority(InterType type) throws Exception {
//		DeviceInterface deviceIntf = null;
//		if (type == InterType.eth0)
//			deviceIntf = hiveAp.getEth0Interface();
//		else if (type == InterType.eth1)
//			deviceIntf = hiveAp.getEth1Interface();
//		else if (type == InterType.eth2)
//			deviceIntf = hiveAp.getEth2Interface();
//		else if (type == InterType.eth3)
//			deviceIntf = hiveAp.getEth3Interface();
//		else if (type == InterType.eth4)
//			deviceIntf = hiveAp.getEth4Interface();
//		else if (type == InterType.usb)
//			deviceIntf = hiveAp.getUSBInterface();
//		else if (type == InterType.wifi0)
//			deviceIntf = hiveAp.getWifi0Interface();
//		else if (type == InterType.wifi1)
//			deviceIntf = hiveAp.getWifi1Interface();
//		return (deviceIntf == null) ? super.getInterfacePriority(type) : deviceIntf.getPriority();
		if (type == InterType.eth0) {
			return wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_ETH0) != null ? wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_ETH0) : 0;
		}
		else if (type == InterType.eth1) {
			return wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_ETH1) != null ? wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_ETH1) : 0;
		}
		else if (type == InterType.eth2) {
			return wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_ETH2) != null ? wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_ETH2) : 0;
		}
		else if (type == InterType.eth3) {
			return wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_ETH3) != null ? wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_ETH3) : 0;
		}
		else if (type == InterType.eth4) {
			return wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_ETH4) != null ? wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_ETH4) : 0;
		}
		else if (type == InterType.usb) {
			return wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_USB) != null ? wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_USB) : 0;
		}
		else if (type == InterType.wifi0) {
			return wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_WIFI0) != null ? wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_WIFI0) : 0;
		}
		else if (type == InterType.wifi1) {
			return wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_WIFI1) != null ? wanOrderMap.get((long)AhInterface.DEVICE_IF_TYPE_WIFI1) : 0;
		}
		else {
			return 0;
		}
	}

	private void setEthServiceFilter() {
		ethServiceFilter = new HashMap<InterType, ServiceFilter>();
		ServiceFilter firstFilter = null;
		//TODO Port Template Profiles
		if(hiveAp.getPortGroup() != null && hiveAp.getPortGroup().getBasicProfiles() != null){
			for(PortBasicProfile portBase : hiveAp.getPortGroup().getBasicProfiles()){
				PortAccessProfile accessProfile = portBase.getAccessProfile();
				if(accessProfile.getServiceFilter() != null){
					if(firstFilter == null){
						firstFilter = accessProfile.getServiceFilter();
					}
					if(portBase.getETHs() != null){
						for(int i=0; i<portBase.getETHs().length; i++) {
							if("1".equals(portBase.getETHs()[i])){
								ethServiceFilter.put(InterType.eth1, accessProfile.getServiceFilter());
							}else if("2".equals(portBase.getETHs()[i])){
								ethServiceFilter.put(InterType.eth2, accessProfile.getServiceFilter());
							}else if("3".equals(portBase.getETHs()[i])){
								ethServiceFilter.put(InterType.eth3, accessProfile.getServiceFilter());
							}else if("4".equals(portBase.getETHs()[i])){
								ethServiceFilter.put(InterType.eth4, accessProfile.getServiceFilter());
							}
						}
					}
				}
			}
			if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100 
					|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_330
					|| hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_350){
				ethServiceFilter.put(InterType.eth1, firstFilter);
			}
		}
		
	}

	public boolean isConfigInterManage(InterType type) {
		return ethServiceFilter.get(type) != null;
	}

	public boolean isEnableInterManage(ManageType type, InterType type1) {
		if (type == InterfaceProfileInt.ManageType.ping) {
			return ethServiceFilter.get(type1).getEnablePing();
		} else if (type == InterfaceProfileInt.ManageType.SNMP) {
			return ethServiceFilter.get(type1).getEnableSNMP();
		} else if (type == InterfaceProfileInt.ManageType.SSH) {
			return ethServiceFilter.get(type1).getEnableSSH();
		} else
			return type == ManageType.Telnet
					&& ethServiceFilter.get(type1).getEnableTelnet();
	}

	public boolean isWifiModeWan(InterfaceWifi wifi){
		DeviceInterface wifiInf = null;
		if(wifi == InterfaceWifi.wifi0){
			wifiInf = hiveAp.getWifi0Interface();
		}else if(wifi == InterfaceWifi.wifi1){
			wifiInf = hiveAp.getWifi1Interface();
		}
		return wifiInf != null && !isWifiClientModeShutdown(wifi);
	}
	
	public boolean isEnableWifiAsClient(int index){
		String ssidName = templateSsidList.get(index).getSsid();
		for(HiveApPreferredSsid wifiSsid : hiveAp.getWifiClientPreferredSsids()){
			if(wifiSsid.getPreferredSsid() == null){
				continue;
			}
			if(ssidName.equals(wifiSsid.getPreferredSsid().getSsid())){
				return true;
			}
		}
		return false;
	}

	public boolean isWifiBindSsid(InterfaceWifi wifi) {
		return getHiveApWifi(wifi).getOperationMode() == AhInterface.OPERATION_MODE_ACCESS
				|| getHiveApWifi(wifi).getOperationMode() == AhInterface.OPERATION_MODE_WAN_ACCESS
				|| getHiveApWifi(wifi).getOperationMode() == AhInterface.OPERATION_MODE_DUAL;
	}

	public String getInterfaceWifiRadioChannel(InterfaceWifi wifi) {
		return getHiveApWifi(wifi).getChannelStr().toLowerCase();
	}

	public String getInterfaceWifiRadioPower(InterfaceWifi wifi) {
		return getHiveApWifi(wifi).getPowerStr().toLowerCase();
	}

	public boolean isConfigureWifiRadioProfile(InterfaceWifi wifi) {
		return this.getWifiRadioProfile(wifi) != null;
	}

	public String getInterfaceWifiRadioProfileName(InterfaceWifi wifi) {
		return this.getWifiRadioProfile(wifi).getRadioName();
	}

	private RadioProfile getWifiRadioProfile(InterfaceWifi wifi) {
		RadioProfile raidoProfile;
		if (wifi == InterfaceWifi.wifi0) {
			raidoProfile = hiveAp.getWifi0RadioProfile();
		} else {
			raidoProfile = hiveAp.getWifi1RadioProfile();
		}
		return raidoProfile;
	}

	public boolean isConfigureRadioAntennaExternal(InterfaceWifi wifi) {
		return this.getWifiRadioProfile(wifi).getAntennaType20() == RadioProfile.RADIO_ANTENNA20_TYPE_E;
	}
	
	public int getRadioAntennaDefault(InterfaceWifi wifi){
		short defaultS =  this.getWifiRadioProfile(wifi).getAntennaType20();
		switch(defaultS){
			case RadioProfile.RADIO_ANTENNA20_TYPE_I:
				return 0;
			case RadioProfile.RADIO_ANTENNA20_TYPE_E:
				return 2;
			default:
				return 0;
		}
	}

//	public boolean isConfigureInterfaceWlan(InterfaceWifi wifi) {
//		return (getHiveApWifi(wifi).getOperationMode() == AhInterface.OPERATION_MODE_ACCESS 
//				|| getHiveApWifi(wifi).getOperationMode() == AhInterface.OPERATION_MODE_WAN_ACCESS
//				|| getHiveApWifi(wifi).getOperationMode() == AhInterface.OPERATION_MODE_DUAL)
//				&& hiveAp.getConfigTemplate().getIdsPolicy() != null;
//	}

	public String getInterfaceWlanProfileName() {
		return hiveAp.getConfigTemplate().getIdsPolicy().getPolicyName();
	}

	public int getInterfaceWifiSsidSize() {
		if(templateSsidList == null){
			setSsidList();
		}
		if (templateSsidList == null) {
			return 0;
		} else {
			return templateSsidList.size();
		}
	}

	public boolean isConfigWifiSsid(InterfaceWifi wifi, int index) {
		String ssidName = null;
		if (templateSsidList.get(index) != null) {
			ssidName = templateSsidList.get(index).getSsidName();
		}
		ConfigTemplateSsid findBindSsid;
		if (ssidName != null && !"".equals(ssidName)) {
			findBindSsid = null;
			for (ConfigTemplateSsid ssidBindObj : hiveAp.getConfigTemplate()
					.getSsidInterfaces().values()) {
				if (ssidBindObj.getSsidProfile() != null) {
					if (ssidName.equals(ssidBindObj.getSsidProfile()
							.getSsidName())) {
						findBindSsid = ssidBindObj;
						break;
					}
				}
			}
			if (findBindSsid != null) {
				//fix bug 24106,ssid don't bind with wifi0. This code added by llchen for fix bug 18046(mdm should not send to br100).
				//but it should not block ssid. It should block security-object
				/*if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100 && wifi == InterfaceWifi.wifi0 
					&& findBindSsid.getSsidProfile().isEnableMDM() && NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"5.1.1.0") >= 0){
					return false;
				}else */
				if (findBindSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
					return true;
				} else if (findBindSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BG) {
					return getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_BG
							|| getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_NG;
				} else if (findBindSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_A) {
					return getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_A
							|| getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_NA
							|| getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_AC;
				} else {
					return false;
				}
			} else {
				return false;
			}

		} else {
			return false;
		}
	}

	public String getWifiSsidName(int index) {
		return templateSsidList.get(index).getSsid();
	}

	public boolean isConfigureSsidIp(InterfaceWifi wifi, int index) {
		boolean result;
		if (templateSsidList.get(index).getCwp() == null) {
			result = false;
		} else {
			if (templateSsidList.get(index).getCwp().getUseDefaultNetwork()) {
				result = false;
			} else {
				if (getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_A ||
						getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_NA) {
					String ipForAMode = templateSsidList.get(index).getCwp()
							.getIpForAMode();
					String maskForAMode = templateSsidList.get(index).getCwp()
							.getMaskForAMode();
					result = ipForAMode != null && !"".equals(ipForAMode)
							&& maskForAMode != null && !"".equals(maskForAMode);
				} else {
					String ipForBGMode = templateSsidList.get(index).getCwp()
							.getIpForBGMode();
					String maskForBGMode = templateSsidList.get(index).getCwp()
							.getMaskForBGMode();
					result = ipForBGMode != null && !"".equals(ipForBGMode)
							&& maskForBGMode != null
							&& !"".equals(maskForBGMode);
				}
			}
		}
		return result;
	}
	
	public boolean isShutDownWifiSsid(InterfaceWifi wifi, int index){
		if(getHiveApWifi(wifi).getOperationMode() == AhInterface.OPERATION_MODE_ACCESS
				|| getHiveApWifi(wifi).getOperationMode() == AhInterface.OPERATION_MODE_WAN_ACCESS
				|| getHiveApWifi(wifi).getOperationMode() == AhInterface.OPERATION_MODE_DUAL){
			if(getHiveApWifi(wifi).getAdminState() == AhInterface.ADMIN_STATE_UP){
				if(wifi == InterfaceWifi.wifi0){
					return disableSsidWifi0.contains(templateSsidList.get(index).getSsidName());
				}else{
					return disableSsidWifi1.contains(templateSsidList.get(index).getSsidName());
				}
			}else{
				return true;
			}
		}else{
			return false;
		}
		
	}

	public String getWifiSsidIp(InterfaceWifi wifi, int index) {
		String ip, mask;
		if (getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_A || 
				getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_NA) {
			ip = templateSsidList.get(index).getCwp().getIpForAMode();
			mask = templateSsidList.get(index).getCwp().getMaskForAMode();
		} else {
			ip = templateSsidList.get(index).getCwp().getIpForBGMode();
			mask = templateSsidList.get(index).getCwp().getMaskForBGMode();
		}
		return ip + "/" + CLICommonFunc.turnNetMaskToNum(mask);
	}

	public boolean isEnableInterMacLearning(InterType type) {
		if(type == InterType.eth1){
			return true;
		}else if(type == InterType.eth2){
			return true;
		}else if(type == InterType.eth3){
			return true;
		}else if(type == InterType.eth4){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isConfigIdleTimeout(InterType type){
		return false;
	}

	public int getInterIdleTimeout(InterType type) {
		return -1;
	}

	public int getInterMacLearningStaticSize(InterType type) {
		return -1;
	}

	public String getStaticMacAddr(InterType type, int index) throws Exception {
		return null;
	}

	public boolean isEnableMgtDhcp() {
		return false;
	}

	public boolean isConfigMgtIp() {
		String ipAddr = null;
		try{
			ipAddr = getMgtIpAndMask();
		}catch(Exception ex){
			
		}
		return ipAddr != null && !"".equals(ipAddr);
	}

	public int getMgtNativeVlan() throws CreateXMLException {
		return CLICommonFunc.getVlan(hiveAp.getConfigTemplate().getVlanNative(), hiveAp).getVlanId();
	}
	
	public boolean isConfigBridgeUserProfile(InterType type){
		PortAccessProfile portProfile = ethPortMap.get(type);
		return portProfile != null && portProfile.getPortType() != PortAccessProfile.PORT_TYPE_8021Q && portProfile.getDefUserProfile() != null;
	}

	public int getInterAccessUserProfileAttr(InterType type) {
		if(isConfigBridgeUserProfile(type)){
			return ethPortMap.get(type).getDefUserProfile().getAttributeValue();
		}else{
			return 0;
		}
	}

	public boolean isEnableDhcpFallBack() {
		return false;
	}

	public int getDhcpTimeOutt() {
		return -1;
	}

	public boolean isConfigMgtDefaultIpPrefix() {
		return false;
	}

	public String getMgtDefaultIpPrefix() {
		return null;
	}
	
	public String getMgtOldDefaultIpPrefix(){
		return null;
	}

	public boolean isHiveAp11n() {
		return false;
	}
	
	public boolean isConfigEthx(InterType ethType){
		return true;
	}

	public boolean isConfigInterBind(InterType type) {
		return false;
	}

	public boolean isConfigInterBindAgg0(InterType type) {
		return false;
	}

	public boolean isConfigInterBindRed0(InterType type) {
		return false;
	}

	public boolean isConfigInterBindRedPrimary(InterType type) {
		return false;
	}

	public boolean isConfigInterRed0() {
		return false;
	}

	public boolean isConfigInterAgg0() {
		return false;
	}

	public boolean isConfigDhcpAddressOnly() {
		return false;
	}

	public boolean isEnableDhcpAddressOnly() {
		return false;
	}

	public boolean isConfigInterStationTraffic(InterType type) throws CreateXMLException {
		return ethServiceFilter.get(type) != null && 
				!this.isConfigInterMode(InterfaceMode.backhaul, type);
	}

	public boolean isEnableInterStationTraffic(InterType type) {
		return ethServiceFilter.get(type).getInterTraffic();
	}

	public boolean isConfigMgtChild(MgtType type) {
		return dhcpInfoMap.get(type) != null && dhcpInfoMap.get(type).getVpnNetwork() != null;
	}

	public int getMgtChildVlan(MgtType type) throws CreateXMLException {
		VpnNetwork network = dhcpInfoMap.get(type).getVpnNetwork();
		Vlan vlan =  hiveAp.getConfigTemplate().getVlanByNetwork(network);
		return CLICommonFunc.getVlan(vlan, this.hiveAp).getVlanId();
	}

	public String getMgtChildIp(MgtType type) throws CreateXMLException {
		if(dhcpInfoMap.get(type) != null && dhcpInfoMap.get(type).getVpnNetwork() != null){
			VpnNetwork network = dhcpInfoMap.get(type).getVpnNetwork();
			if(network.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
					network.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
				SubNetworkResource subNet = dhcpInfoMap.get(type).getSubNetwork();
				String networkIp = subNet.getNetwork();
				int index = networkIp.indexOf("/");
				networkIp = networkIp.substring(index+1);
				return subNet.getFirstIp() + "/" + networkIp;
			}else{
				return getFirstIp(network.getIpAddressSpace());
			}
		}else{
			return null;
		}
	}

	public boolean isConfigMgtChildIpHelper(MgtType type) {
		return false;
	}

	public int getMgtChildIpHelperSize(MgtType type) {
		return -1;
	}

	public String getMgtChildIpHelperAddress(MgtType type, int index) {
		return null;
	}

	public boolean isMgtChildPingEnable(MgtType type) {
		return true;
	}

	public boolean isEnableMgtChildDhcpServer(MgtType type) {
		VpnNetwork vpnNetwork = dhcpInfoMap.get(type).getVpnNetwork();
		if(vpnNetwork == null){
			return false;
		}
		if(vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
				vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
			return dhcpInfoMap.get(type) != null && 
					dhcpInfoMap.get(type).getVpnNetworkSub() != null && 
					dhcpInfoMap.get(type).getVpnNetworkSub().isEnableDhcp();
		}else{
			return vpnNetwork.isEnableDhcp();
		}
	}

	public boolean isEnableMgtChildAuthoritative(MgtType type) {
		return true;
	}

	
	public int getMgtChildIpPoolSize(MgtType type) {
		return 1;
	}

	public String getMgtChildIpPoolName(MgtType type, int index) throws CreateXMLException {
		if(dhcpInfoMap.get(type) != null && dhcpInfoMap.get(type).getVpnNetwork() != null){
			VpnNetwork network = dhcpInfoMap.get(type).getVpnNetwork();
			if(network.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
					network.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
				SubNetworkResource resource = dhcpInfoMap.get(type).getSubNetwork();
				return resource.getIpPoolStart() + " " + resource.getIpPoolEnd();
			}else{
				return getIpPool(network.getIpAddressSpace(), network.getGuestLeftReserved(), network.getGuestRightReserved());
			}
		}else{
			return null;
		}
	}

	public boolean isEnableMgtChildArpCheck(MgtType type) {
		VpnNetwork vpnNetwork = dhcpInfoMap.get(type).getVpnNetwork();
		if(vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
				vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
			return dhcpInfoMap.get(type).getVpnNetworkSub().isEnableDhcp() && dhcpInfoMap.get(type).getVpnNetworkSub().isEnableArpCheck();
		}else{
			return vpnNetwork.isEnableDhcp() && vpnNetwork.isEnableArpCheck();
		}
	}

	public boolean isConfigMgtChildOptionsDefaultGateway(MgtType type) {
		return true;
	}

	public String getMgtChildOptionsDefaultGateway(MgtType type) throws CreateXMLException {
		if(dhcpInfoMap.get(type) != null && dhcpInfoMap.get(type).getVpnNetwork() != null){
			VpnNetwork network = dhcpInfoMap.get(type).getVpnNetwork();
			if(network.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
					network.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
				SubNetworkResource resource = dhcpInfoMap.get(type).getSubNetwork();
				return resource.getFirstIp();
			}else{
				String ipAddr = getFirstIp(network.getIpAddressSpace());
				return ipAddr.substring(0, ipAddr.indexOf("/"));
			}
		}else{
			return null;
		}
	}
	
	public boolean isMgtDhcpNatSupport(MgtType type){
		return false;
	}

	public boolean isConfigMgtChildOptionsLeaseTime(MgtType type) {
		VpnNetwork vpnNetwork = dhcpInfoMap.get(type).getVpnNetwork();
		if(vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
				vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
			return dhcpInfoMap.get(type).getVpnNetworkSub().isEnableDhcp() && dhcpInfoMap.get(type).getVpnNetworkSub().getLeaseTime() > 0;
		}else{
			return vpnNetwork.isEnableDhcp() && vpnNetwork.getLeaseTime() > 0;
		}
	}

	public int getMgtChildOptionsLeaseTime(MgtType type) {
		VpnNetwork vpnNetwork = dhcpInfoMap.get(type).getVpnNetwork();
		if(vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
				vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
			return dhcpInfoMap.get(type).getVpnNetworkSub().getLeaseTime();
		}else{
			return vpnNetwork.getLeaseTime();
		}
	}

	public boolean isConfigMgtChildOptionsNetMask(MgtType type) {
		return false;
	}

	public String getMgtChildOptionsNetMask(MgtType type) {
		return null;
	}

	public int getMgtChildOptionsHivemanagerSize(MgtType type) throws CreateXMLException {
		return dhcpInfoMap.get(type).getHivemanagerList().size();
	}

	public String getMgtChildOptionsHivemanager(MgtType type, int index) {
		return dhcpInfoMap.get(type).getHivemanagerList().get(index);
	}

	public boolean isConfigMgtChildOptionsDoMain(MgtType type) {
		if(dhcpInfoMap.get(type) == null){
			return false;
		}
		VpnNetwork vpnNetwork = dhcpInfoMap.get(type).getVpnNetwork();
		if(vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
			return dhcpInfoMap.get(type) != null && dhcpInfoMap.get(type).getVpnNetworkSub() != null &&
					dhcpInfoMap.get(type).getVpnNetworkSub().getDomainName() != null && 
					!"".equals(dhcpInfoMap.get(type).getVpnNetworkSub().getDomainName());
		}else{
			return vpnNetwork.getDomainName() != null && !"".equals(vpnNetwork.getDomainName());
		}
	}

	public String getMgtChildOptionsDoMain(MgtType type) {
		VpnNetwork vpnNetwork = dhcpInfoMap.get(type).getVpnNetwork();
		if(vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
				vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
			return dhcpInfoMap.get(type).getVpnNetworkSub().getDomainName();
		}else{
			return vpnNetwork.getDomainName();
		}
	}

	public boolean isConfigMgtChildOptionsMtu(MgtType type) {
		return false;
	}

	public int getMgtChildOptionsMtu(MgtType type) {
		return -1;
	}

	public boolean isConfigMgtChildOptionsDns1(MgtType type) {
		return isEnableMgtDnsServer(type);
	}

	public String getMgtChildOptionsDns1(MgtType type) throws CreateXMLException {
		if(dhcpInfoMap.get(type) != null && dhcpInfoMap.get(type).getVpnNetwork() != null){
			VpnNetwork network = dhcpInfoMap.get(type).getVpnNetwork();
			VpnNetworkSub vpnSub = dhcpInfoMap.get(type).getVpnNetworkSub();
			if(vpnSub != null && vpnSub.isOverrideDNSService() && vpnSub.getDnsService() != null){
				if(vpnSub.getDnsService() != null && vpnSub.getDnsService().getServiceType() != DnsServiceProfile.EXTERNAL_DNS){
					if(network.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
							network.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
						SubNetworkResource resource = dhcpInfoMap.get(type).getSubNetwork();
						return resource.getFirstIp();
					}else{
						String ipAddr = getFirstIp(network.getIpAddressSpace());
						return ipAddr.substring(0, ipAddr.indexOf("/"));
					}
				}else{
					if(vpnSub.getDnsService().getExternalDns1() != null){
						IpAddress dns1 = vpnSub.getDnsService().getExternalDns1();
						return CLICommonFunc.getIpAddress(dns1, this.hiveAp).getIpAddress();
					}
				}
			}
			if(network.getVpnDnsService() != null){
				if(network.getVpnDnsService().getServiceType() != DnsServiceProfile.EXTERNAL_DNS){
					if(network.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
							network.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
						SubNetworkResource resource = dhcpInfoMap.get(type).getSubNetwork();
						return resource.getFirstIp();
					}else{
						String ipAddr = getFirstIp(network.getIpAddressSpace());
						return ipAddr.substring(0, ipAddr.indexOf("/"));
					}
				}else{
					if(network.getVpnDnsService().getExternalDns1() != null){
						IpAddress dns1 = network.getVpnDnsService().getExternalDns1();
						return CLICommonFunc.getIpAddress(dns1, this.hiveAp).getIpAddress();
					}
				}
			}
		}
		return null;
	}

	public boolean isConfigMgtChildOptionsDns2(MgtType type) throws CreateXMLException {
		if (getMgtChildOptionsDns2(type) != null && !getMgtChildOptionsDns2(type).isEmpty())
			return true;
		else
			return false;
	}

	public String getMgtChildOptionsDns2(MgtType type) throws CreateXMLException {
		if(dhcpInfoMap.get(type) != null && dhcpInfoMap.get(type).getVpnNetwork() != null){
			VpnNetwork network = dhcpInfoMap.get(type).getVpnNetwork();
			VpnNetworkSub vpnSub = dhcpInfoMap.get(type).getVpnNetworkSub();
			if(vpnSub != null && vpnSub.isOverrideDNSService() && vpnSub.getDnsService() != null){
				if(vpnSub.getDnsService().getServiceType() == DnsServiceProfile.EXTERNAL_DNS){
					if(vpnSub.getDnsService().getExternalDns2() != null){
						IpAddress dns2 = vpnSub.getDnsService().getExternalDns2();
						return CLICommonFunc.getIpAddress(dns2, this.hiveAp).getIpAddress();
					}
				}
			}
			
			if(network.getVpnDnsService() != null){
				if(network.getVpnDnsService().getServiceType() == DnsServiceProfile.EXTERNAL_DNS){
					if(network.getVpnDnsService().getExternalDns2() != null){
						IpAddress dns2 = network.getVpnDnsService().getExternalDns2();
						return CLICommonFunc.getIpAddress(dns2, this.hiveAp).getIpAddress();
					}
				}
			}
		}
		return null;
	}

	public boolean isConfigMgtChildOptionsDns3(MgtType type) throws CreateXMLException {
		if (getMgtChildOptionsDns3(type) != null && !getMgtChildOptionsDns3(type).isEmpty())
			return true;
		else
			return false;
	}

	public String getMgtChildOptionsDns3(MgtType type) throws CreateXMLException {
		if(dhcpInfoMap.get(type) != null && dhcpInfoMap.get(type).getVpnNetwork() != null){
			VpnNetworkSub vpnSub = dhcpInfoMap.get(type).getVpnNetworkSub();
			VpnNetwork network = dhcpInfoMap.get(type).getVpnNetwork();
			if(vpnSub != null && vpnSub.isOverrideDNSService() && vpnSub.getDnsService() != null){
				if(vpnSub.getDnsService().getServiceType() == DnsServiceProfile.EXTERNAL_DNS){
					if(vpnSub.getDnsService().getExternalDns3() != null){
						IpAddress dns3 = vpnSub.getDnsService().getExternalDns3();
						return CLICommonFunc.getIpAddress(dns3, this.hiveAp).getIpAddress();
					}
				}
			}
			if(network.getVpnDnsService() != null){
				if(network.getVpnDnsService().getServiceType() == DnsServiceProfile.EXTERNAL_DNS){
					if(network.getVpnDnsService().getExternalDns3() != null){
						IpAddress dns3 = network.getVpnDnsService().getExternalDns3();
						return CLICommonFunc.getIpAddress(dns3, this.hiveAp).getIpAddress();
					}
				}
			}
		}
		return null;
	}

	public boolean isConfigMgtChildOptionsNtp1(MgtType type) {
		if(dhcpInfoMap.get(type) == null){
			return false;
		}
		VpnNetwork vpnNetwork = dhcpInfoMap.get(type).getVpnNetwork();
		if(vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
				vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
			return dhcpInfoMap.get(type).getVpnNetworkSub().isEnableDhcp() && 
					dhcpInfoMap.get(type).getVpnNetworkSub().getNtpServerIp() != null && 
					!"".equals(dhcpInfoMap.get(type).getVpnNetworkSub().getNtpServerIp());
		}else{
			return vpnNetwork.isEnableDhcp() && vpnNetwork.getNtpServerIp() != null && !"".equals(vpnNetwork.getNtpServerIp());
		}
	}

	public String getMgtChildOptionsNtp1(MgtType type) {
		VpnNetwork vpnNetwork = dhcpInfoMap.get(type).getVpnNetwork();
		if(vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_INTERNAL || 
				vpnNetwork.getNetworkType() == VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
			return dhcpInfoMap.get(type).getVpnNetworkSub().getNtpServerIp();
		}else{
			return vpnNetwork.getNtpServerIp();
		}
	}

	public boolean isConfigMgtChildOptionsNtp2(MgtType type) {
		return false;
	}

	public String getMgtChildOptionsNtp2(MgtType type) {
		return null;
	}

	public boolean isConfigMgtChildOptionsPop3(MgtType type) {
		return false;
	}

	public String getMgtChildOptionsPop3(MgtType type) {
		return null;
	}

	public boolean isConfigMgtChildOptionsSmtp(MgtType type) {
		return false;
	}

	public String getMgtChildOptionsSmtp(MgtType type) {
		return null;
	}

	public boolean isConfigMgtChildOptionsWins1(MgtType type) {
		return false;
	}

	public String getMgtChildOptionsWins1(MgtType type) {
		return null;
	}
	
	public boolean isConfigMgtChildOptionsWins2(MgtType type) {
		return false;
	}

	public String getMgtChildOptionsWins2(MgtType type) {
		return null;
	}

	public boolean isConfigMgtChildOptionsLogsrv(MgtType type) {
		return false;
	}

	public String getMgtChildOptionsLogsrv(MgtType type) {
		return null;
	}

	public int getMgtChildOptionsCustomSize(MgtType type) {
		
		return dhcpInfoMap.get(type).getDhcpCustoms().size();
	}
	
//	private boolean isEnableRadiusServer(){
//		return this.hiveAp.isEnabledBrAsRadiusServer() && 
//				(hiveAp.getRadiusServerProfile() != null || hiveAp.getConfigTemplate().getRadiusServerProfile() != null);
//	}
//	
//	private boolean isEnablePpskServer(){
//		for(ConfigTemplateSsid ssidTemp : this.hiveAp.getConfigTemplate().getSsidInterfaces().values()){
//			if(ssidTemp.getSsidProfile() != null 
//					&& ssidTemp.getSsidProfile().getPpskServer() != null
//					&& hiveAp.getId().equals(ssidTemp.getSsidProfile().getPpskServer().getId())){
//				return true;
//			}
//		}
//		return false;
//	}

	public int getMgtChildOptionsCustomName(MgtType type, int index) {
		return dhcpInfoMap.get(type).getDhcpCustoms().get(index).getNumber();
	}

	public boolean isConfigMgtChildOptionsCustomInteger(MgtType type, int index) {
		return dhcpInfoMap.get(type).getDhcpCustoms().get(index).getType() == DhcpServerOptionsCustom.CUSTOM_TYPE_INTEGER;
	}

	public int getMgtChildOptionsCustomIntegerValue(MgtType type, int index) {
		return Integer.valueOf(dhcpInfoMap.get(type).getDhcpCustoms().get(index).getValue(hiveAp.getSoftVer()));
	}

	public boolean isConfigMgtChildOptionsCustomIp(MgtType type, int index) {
		return dhcpInfoMap.get(type).getDhcpCustoms().get(index).getType() == DhcpServerOptionsCustom.CUSTOM_TYYPE_IP;
	}

	public String getMgtChildOptionsCustomIpValue(MgtType type, int index) {
		return dhcpInfoMap.get(type).getDhcpCustoms().get(index).getValue(hiveAp.getSoftVer());
	}

	public boolean isConfigMgtChildOptionsCustomString(MgtType type, int index) {
		return dhcpInfoMap.get(type).getDhcpCustoms().get(index).getType() == DhcpServerOptionsCustom.CUSTOM_TYYPE_STRING;
	}
	
	public boolean isConfigMgtChildOptionsCustomHex(MgtType type, int index){
		return dhcpInfoMap.get(type).getDhcpCustoms().get(index).getType() == DhcpServerOptionsCustom.CUSTOM_TYYPE_HEX;
	}

	public String getMgtChildOptionsCustomStringValue(MgtType type, int index) {
		return dhcpInfoMap.get(type).getDhcpCustoms().get(index).getValue(hiveAp.getSoftVer());
	}
	
	public String getMgtChildOptionsCustomHexValue(MgtType type, int index){
		String resultStr = dhcpInfoMap.get(type).getDhcpCustoms().get(index).getValue(hiveAp.getSoftVer());
		return resultStr == null ? resultStr : resultStr.toUpperCase();
	}
	
	public boolean isConfigLinkDiscovery(){
		return false;
	}
	
	public int getInterfaceRadioRange(InterfaceWifi wifi){
		return getWifiRadioProfile(wifi).getRadioRange();
	}
	
	public boolean isConfigRadioFixedAntenna(InterfaceWifi wifi){
		return getWifiRadioProfile(wifi).getAntennaType28() == RadioProfile.RADIO_ANTENNA28_TYPE_A ||
			getWifiRadioProfile(wifi).getAntennaType28() == RadioProfile.RADIO_ANTENNA28_TYPE_B;
	}
	
	public InterfaceRadioFixedAntennaValue getRadioFixedAntenna(InterfaceWifi wifi){
		short type = getWifiRadioProfile(wifi).getAntennaType28();
		if(type == RadioProfile.RADIO_ANTENNA28_TYPE_A){
			return InterfaceRadioFixedAntennaValue.A;
		}else if(type == RadioProfile.RADIO_ANTENNA28_TYPE_B) {
			return InterfaceRadioFixedAntennaValue.B;
		}else{
			return null;
		}
	}
	
	public boolean isEnableCCA(InterfaceWifi wifi){
		return getWifiRadioProfile(wifi).isEnableCca();
	}
	
	public int getMaxCca(InterfaceWifi wifi){
		return getWifiRadioProfile(wifi).getMaxCcaValue();
	}
	
	public int getDefaultCca(InterfaceWifi wifi){
		return getWifiRadioProfile(wifi).getDefaultCcaValue();
	}

	public boolean isConfigWifi1(){
		return CountryCode.is5GHzChannelAvailable(hiveAp.getCountryCode());
	}
	
	public boolean isWifiShutdown(InterfaceWifi wifi) {
		return getHiveApWifi(wifi).getAdminState() == AhInterface.ADMIN_STATE_DOWM;
	}
	
	public boolean isConfigWifiShutdown(InterfaceWifi wifi){
		return getHiveApWifi(wifi).getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL || 
			getHiveApWifi(wifi).getOperationMode() == AhInterface.OPERATION_MODE_DUAL;
	}
	
	public boolean isWifiClientModeShutdown(InterfaceWifi wifi){
//		if(hiveAp.getWifiClientPreferredSsids() == null || hiveAp.getWifiClientPreferredSsids().isEmpty()){
//			return true;
//		}
		if(wifi == InterfaceWifi.wifi0){
			DeviceInterface wifi0Interface = hiveAp.getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_WIFI0);
			return hiveAp.getRole(wifi0Interface) != AhInterface.ROLE_WAN;
		}else{
			DeviceInterface wifi0Interface = hiveAp.getDeviceInterfaces().get((long)AhInterface.DEVICE_IF_TYPE_WIFI1);
			return hiveAp.getRole(wifi0Interface)!= AhInterface.ROLE_WAN;
		}
	}
	
	public boolean isEnableAntennaDiversity(InterfaceWifi wifi){
		return getWifiRadioProfile(wifi).getAntennaType28() == RadioProfile.RADIO_ANTENNA28_TYPE_D;
	}
	
	public boolean isHiveAp20(){
		return hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_20;
	}
	
	public boolean isHiveAp28(){
		return hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_28;
	}
	
	public boolean isConfigEthAllowedVlan(InterType type){
		return type != InterType.eth0 && ethPortMap.get(type) != null 
				&& ethPortMap.get(type).getPortType() == PortAccessProfile.PORT_TYPE_8021Q 
				&& getAllowedVlanSize(type) > 0;
	}
	
	public boolean isConfigAllowedVlanAll(InterType type){
		String allowedVlan = ethPortMap.get(type).getAllowedVlan();
		return allowedVlan != null && allowedVlan.toLowerCase().contains("all");
	}
	
	public boolean isConfigAllowedVlanAuto(InterType type){
		return false;
	}
	
	public boolean isConfigAllowedVlanNum(InterType type){
		return true;
	}
	
	public int getAllowedVlanSize(InterType type) {
		if(lanVlanMap.get(type) == null){
			return 0;
		}else{
			return lanVlanMap.get(type).size();
		}
	}
	
	public String getAllowedVlanStr(InterType type, int i){
		return String.valueOf(lanVlanMap.get(type).get(i));
	}
	
	public boolean isConfigEthSecurity(InterType type){
		PortAccessProfile portObj = ethPortMap.get(type);
		if(portObj == null){
			return false;
		}else if(portObj.getPortType() == PortAccessProfile.PORT_TYPE_WAN){
			return false;
		}
		
		boolean enable8021Q = portObj.getPortType() == PortAccessProfile.PORT_TYPE_8021Q;
		if(type == InterType.eth0){
			return false;
		}else if(type == InterType.eth1){
			return this.hiveAp.isEnableLan_1() && portObj != null && !enable8021Q;
		}else if(type == InterType.eth2){
			return this.hiveAp.isEnableLan_2() && portObj != null && !enable8021Q;
		}else if(type == InterType.eth3){
			return this.hiveAp.isEnableLan_3() && portObj != null && !enable8021Q;
		}else if(type == InterType.eth4){
			return this.hiveAp.isEnableLan_4() && portObj != null && !enable8021Q;
		}else{
			return false;
		}
	}
	
	public String getEthSecurityObjName(InterType type){
		return ethPortMap.get(type).getName();
	}
	
	public boolean isConfigEthIp(InterType type) {
		try {
			if(isConfigInterMode(InterfaceProfileInt.InterfaceMode.wan, type)){
				if(type == InterfaceProfileInt.InterType.eth0){
					return hiveAp.getEth0Interface().getConnectionType().equals("2");
				}else if(type == InterfaceProfileInt.InterType.eth1){
					return hiveAp.getEth1Interface().getConnectionType().equals("2");
				}else if(type == InterfaceProfileInt.InterType.eth2){
					return hiveAp.getEth2Interface().getConnectionType().equals("2");
				}else if(type == InterfaceProfileInt.InterType.eth3){
					return hiveAp.getEth3Interface().getConnectionType().equals("2");
				}else if(type == InterfaceProfileInt.InterType.eth4){
					return hiveAp.getEth4Interface().getConnectionType().equals("2");
				}else if(type == InterfaceProfileInt.InterType.usb){
					return hiveAp.getUSBInterface().getConnectionType().equals("2");
				}else if(type == InterfaceProfileInt.InterType.wifi0){
					return hiveAp.getWifi0Interface().getConnectionType().equals("2");
				}else if(type == InterfaceProfileInt.InterType.wifi1){
					return hiveAp.getWifi1Interface().getConnectionType().equals("2");
				}
			}
		}catch (CreateXMLException e) {
			e.printStackTrace();
		}
			if(type == InterType.eth0){
				return !hiveAp.getEth0Interface().isEnableDhcp() && !hiveAp.isPppoeEnableCurrent();
			}else{
				PortAccessProfile portProfile = ethPortMap.get(type);
				if(portProfile == null || !portProfile.isEnabledCWP() || portProfile.getCwp() == null){
					return false;
				}
				Cwp cwpObj = portProfile.getCwp();
				boolean result = false;
				if (cwpObj.getUseDefaultNetwork()) {
					result = false;
				} else {
					if (type == InterType.eth0) {
						String ipForEth0 = cwpObj.getIpForEth0();
						String maskForEth0 = cwpObj.getMaskForEth0();
						result = ipForEth0 != null && !"".equals(ipForEth0)
								&& maskForEth0 != null && !"".equals(maskForEth0);
					}else if(type == InterType.eth1) {
						String ipForEth1 = cwpObj.getIpForEth1();
						String maskForEth1 = cwpObj.getMaskForEth1();
						result = ipForEth1 != null && !"".equals(ipForEth1)
								&& maskForEth1 != null
								&& !"".equals(maskForEth1);
					}
				}
				return result;
			}
		} 
	
	public String getEthIp(InterType type){
		try {
		if(isConfigInterMode(InterfaceProfileInt.InterfaceMode.wan, type)){
			if(type == InterfaceProfileInt.InterType.eth0){
				return hiveAp.getEth0Interface().getIpAndNetmask();
			}else if(type == InterfaceProfileInt.InterType.eth1){
				return hiveAp.getEth1Interface().getIpAndNetmask();
			}else if(type == InterfaceProfileInt.InterType.eth2){
				return hiveAp.getEth2Interface().getIpAndNetmask();
			}else if(type == InterfaceProfileInt.InterType.eth3){
				return hiveAp.getEth3Interface().getIpAndNetmask();
			}else if(type == InterfaceProfileInt.InterType.eth4){
				return hiveAp.getEth4Interface().getIpAndNetmask();
			}else if(type == InterfaceProfileInt.InterType.usb){
				return hiveAp.getUSBInterface().getIpAndNetmask();
			}else if(type == InterfaceProfileInt.InterType.wifi0){
				return hiveAp.getWifi0Interface().getIpAndNetmask();
			}else if(type == InterfaceProfileInt.InterType.wifi1){
				return hiveAp.getWifi1Interface().getIpAndNetmask();
			}
		}
	}catch (CreateXMLException e) {
		e.printStackTrace();
	}
		if(type == InterType.eth0){
			return hiveAp.getEth0Interface().getIpAndNetmask();
		}else {
			String ip, mask;
			Cwp cwpObj = ethPortMap.get(type).getCwp();
			if (type == InterType.eth0) {
				ip = cwpObj.getIpForEth0();
				mask = cwpObj.getMaskForEth0();
			} else {
				ip = cwpObj.getIpForEth1();
				mask = cwpObj.getMaskForEth1();
			}
			return ip + "/" + CLICommonFunc.turnNetMaskToNum(mask);
		}
	}
	
	public boolean isConfigEthNativeVlan(InterType type){
		PortAccessProfile portProfile = ethPortMap.get(type);
		return portProfile != null && portProfile.getPortType() == PortAccessProfile.PORT_TYPE_8021Q 
				&& portProfile.getNativeVlan() != null;
	}
	
	public int getEthNativeVlan(InterType type) throws CreateXMLException{
		PortAccessProfile portProfile = ethPortMap.get(type);
		return getNativeVlanForLan(portProfile, this.hiveAp);
	}
	
	public boolean isConfigEthDhcp(InterType type){
		if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
			return false;
		}else if(hiveAp.isBranchRouter()){
			if(type == InterType.eth0){
				return hiveAp.getRole(hiveAp.getEth0Interface()) == AhInterface.ROLE_WAN ;
			}else if(type == InterType.eth1){
				return hiveAp.getRole(hiveAp.getEth1Interface()) == AhInterface.ROLE_WAN ;
			}else if(type == InterType.eth2){
				return hiveAp.getRole(hiveAp.getEth2Interface()) == AhInterface.ROLE_WAN ;
			}else if(type == InterType.eth3){
				return hiveAp.getRole(hiveAp.getEth3Interface()) == AhInterface.ROLE_WAN ;
			}else if(type == InterType.eth4){
				return hiveAp.getRole(hiveAp.getEth4Interface()) == AhInterface.ROLE_WAN ;
			}else if(type == InterType.usb){
				return hiveAp.getRole(hiveAp.getUSBInterface()) == AhInterface.ROLE_WAN ;
			}else if(type == InterType.wifi0){
				return hiveAp.getRole(hiveAp.getWifi0Interface()) == AhInterface.ROLE_WAN ;
			}else if(type == InterType.wifi1){
				return hiveAp.getRole(hiveAp.getWifi1Interface()) == AhInterface.ROLE_WAN ;
			}else{
				return false;
			}
		}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isEnableEthDhcp(InterType type){
//		if(type == InterType.eth0){
//			return hiveAp.getEth0Interface().isEnableDhcp() && !hiveAp.isPppoeEnableCurrent();
//		}else if(type == InterType.eth1){
//			return hiveAp.getEth1Interface().isEnableDhcp();
//		}else{
//			return true;
//		}
		if(type == InterType.eth0){
			return hiveAp.getEth0Interface().getConnectionType().equals("1");
		}else if(type == InterType.eth1){
			return hiveAp.getEth1Interface().getConnectionType().equals("1");
		}else if(type == InterType.eth2){
			return hiveAp.getEth2Interface().getConnectionType().equals("1");
		}else if(type == InterType.eth3){
			return hiveAp.getEth3Interface().getConnectionType().equals("1");
		}else if(type == InterType.eth4){
			return hiveAp.getEth4Interface().getConnectionType().equals("1");
		}else if(type == InterType.usb){
			return hiveAp.getUSBInterface().getConnectionType().equals("1");
		}else if(type == InterType.wifi0){
			return hiveAp.getWifi0Interface().getConnectionType().equals("1");
		}else if(type == InterType.wifi1){
			return hiveAp.getWifi1Interface().getConnectionType().equals("1");
		}else{
			return true;
		}
	}
	
	public boolean isConfigMgtDnsServer(InterfaceProfileInt.MgtType type){
		return dhcpInfoMap.get(type) != null &&
				dhcpInfoMap.get(type).getVpnNetwork() != null &&
				dhcpInfoMap.get(type).getVpnNetwork().getVpnDnsService() != null;
	}
	
	public boolean isEnableMgtDnsServer(InterfaceProfileInt.MgtType type){
		return dhcpInfoMap.get(type) != null &&
				dhcpInfoMap.get(type).getVpnNetwork() != null &&
				dhcpInfoMap.get(type).getVpnNetwork().getVpnDnsService() != null;
	}
	
	public MgtDnsServerModeValue getMgtDnsServerMode(InterfaceProfileInt.MgtType type){
		DnsServiceProfile vpnDns = dhcpInfoMap.get(type).getVpnNetwork().getVpnDnsService();
		VpnNetworkSub vpnSub = dhcpInfoMap.get(type).getVpnNetworkSub();
		if(vpnSub != null && vpnSub.isOverrideDNSService() && vpnSub.getDnsService() != null){
			if(vpnSub.getDnsService().getServiceType() == DnsServiceProfile.SEPARATE_DNS){
				return MgtDnsServerModeValue.SPLIT;
			}else{
				if(vpnSub.getDnsService().getExternalServerType() == DnsServiceProfile.LOCAL_DNS_TYPE){
					return MgtDnsServerModeValue.SPLIT;
				}else{
					return MgtDnsServerModeValue.NONSPLIT;
				}
			}
		}
		
		if(vpnDns != null && vpnDns.getServiceType() == DnsServiceProfile.SEPARATE_DNS){
			return MgtDnsServerModeValue.SPLIT;
		}else{
			if(vpnDns != null && vpnDns.getExternalServerType() == DnsServiceProfile.LOCAL_DNS_TYPE){
				return MgtDnsServerModeValue.SPLIT;
			}else{
				return MgtDnsServerModeValue.NONSPLIT;
			}
		}
	}
	
	public int getIntDomainNameSize(InterfaceProfileInt.MgtType type){
		return dhcpInfoMap.get(type).getDomainList().size();
	}
	
	public String getIntDomainName(InterfaceProfileInt.MgtType type, int index){
		return dhcpInfoMap.get(type).getDomainList().get(index)[0];
	}
	
	public String getIntDnsServer(InterfaceProfileInt.MgtType type, int index){
		String[] dnsArg = dhcpInfoMap.get(type).getDomainList().get(index);
		if(dnsArg.length == 2){
			return dnsArg[1];
		}else{
			return "";
		}
	}
	
	public boolean isConfigDnsIntResolve(InterfaceProfileInt.MgtType type){
		if(dhcpInfoMap.get(type) == null){
			return false;
		}
		DnsServiceProfile dnsServer = dhcpInfoMap.get(type).getVpnNetwork().getVpnDnsService();
		VpnNetworkSub vpnSub = dhcpInfoMap.get(type).getVpnNetworkSub();
		if(vpnSub != null && vpnSub.isOverrideDNSService() && vpnSub.getDnsService() != null){
			if(vpnSub.getDnsService().getServiceType() == DnsServiceProfile.EXTERNAL_DNS){
				return false;
			}
			if(vpnSub.getDnsService().getServiceType() == DnsServiceProfile.SEPARATE_DNS){
				return (vpnSub.getDnsService().getInternalDns1() != null && !"".equals(vpnSub.getDnsService().getInternalDns1())) || 
						(vpnSub.getDnsService().getInternalDns2() != null && !"".equals(vpnSub.getDnsService().getInternalDns2())) ||
						(vpnSub.getDnsService().getInternalDns3() != null && !"".equals(vpnSub.getDnsService().getInternalDns3()));
			}else{
				if(vpnSub.getDnsService().getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
					return (vpnSub.getDnsService().getInternalDns1() != null && !"".equals(vpnSub.getDnsService().getInternalDns1())) || 
							(vpnSub.getDnsService().getInternalDns2() != null && !"".equals(vpnSub.getDnsService().getInternalDns2())) ||
							(vpnSub.getDnsService().getInternalDns3() != null && !"".equals(vpnSub.getDnsService().getInternalDns3()));
				}else if(vpnSub.getDnsService().getExternalServerType() == DnsServiceProfile.OPEN_DNS_TYPE){
					return true;
				}else{
					return false;
				}
			}
		}else{
			if(dnsServer.getServiceType() == DnsServiceProfile.EXTERNAL_DNS){
				return false;
			}
			
			if(dnsServer.getServiceType() == DnsServiceProfile.SEPARATE_DNS){
				return (dnsServer.getInternalDns1() != null && !"".equals(dnsServer.getInternalDns1())) || 
						(dnsServer.getInternalDns2() != null && !"".equals(dnsServer.getInternalDns2())) ||
						(dnsServer.getInternalDns3() != null && !"".equals(dnsServer.getInternalDns3()));
			}else{
				if(dnsServer.getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
					return (dnsServer.getInternalDns1() != null && !"".equals(dnsServer.getInternalDns1())) || 
							(dnsServer.getInternalDns2() != null && !"".equals(dnsServer.getInternalDns2())) ||
							(dnsServer.getInternalDns3() != null && !"".equals(dnsServer.getInternalDns3()));
				}else if(dnsServer.getExternalServerType() == DnsServiceProfile.OPEN_DNS_TYPE){
					return true;
				}else{
					return false;
				}
			}
		}
	}
	
	public boolean isConfigDnsExtResolve(InterfaceProfileInt.MgtType type){
		if(dhcpInfoMap.get(type) == null){
			return false;
		}
		DnsServiceProfile dnsServer = dhcpInfoMap.get(type).getVpnNetwork().getVpnDnsService();
		VpnNetworkSub vpnSub = dhcpInfoMap.get(type).getVpnNetworkSub();
		if(vpnSub != null && vpnSub.isOverrideDNSService() && vpnSub.getDnsService() != null){
			if(vpnSub.getDnsService().getServiceType() == DnsServiceProfile.EXTERNAL_DNS){
				return false;
			}
			if(vpnSub.getDnsService().getServiceType() == DnsServiceProfile.SEPARATE_DNS){
				if(vpnSub.getDnsService().getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
					return (vpnSub.getDnsService().getExternalDns1() != null && !"".equals(vpnSub.getDnsService().getExternalDns1())) ||
							(vpnSub.getDnsService().getExternalDns2() != null && !"".equals(vpnSub.getDnsService().getExternalDns2())) ||
							(vpnSub.getDnsService().getExternalDns3() != null && !"".equals(vpnSub.getDnsService().getExternalDns3()));
				}else if(vpnSub.getDnsService().getExternalServerType() == DnsServiceProfile.OPEN_DNS_TYPE){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else{
			if(dnsServer.getServiceType() == DnsServiceProfile.SEPARATE_DNS){
				if(dnsServer.getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
					return (dnsServer.getExternalDns1() != null && !"".equals(dnsServer.getExternalDns1())) ||
							(dnsServer.getExternalDns2() != null && !"".equals(dnsServer.getExternalDns2())) ||
							(dnsServer.getExternalDns3() != null && !"".equals(dnsServer.getExternalDns3()));
				}else if(dnsServer.getExternalServerType() == DnsServiceProfile.OPEN_DNS_TYPE){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}
	}
	
	public String getIntResolveDns1(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		DnsServiceProfile dnsServer = dhcpInfoMap.get(type).getVpnNetwork().getVpnDnsService();
		VpnNetworkSub vpnSub = dhcpInfoMap.get(type).getVpnNetworkSub();
		if(vpnSub != null && vpnSub.isOverrideDNSService() && vpnSub.getDnsService() != null){
			if(vpnSub.getDnsService().getServiceType() == DnsServiceProfile.SEPARATE_DNS){
				IpAddress dns1 = vpnSub.getDnsService().getInternalDns1();
				if(dns1 == null){
					return null;
				}
				return CLICommonFunc.getIpAddress(dns1, this.hiveAp).getIpAddress();
			}else{
				if(vpnSub.getDnsService().getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
					IpAddress dns1 = vpnSub.getDnsService().getInternalDns1();
					if(dns1 == null){
						return null;
					}
					return CLICommonFunc.getIpAddress(dns1, this.hiveAp).getIpAddress();
				}else if(vpnSub.getDnsService().getExternalServerType() == DnsServiceProfile.OPEN_DNS_TYPE){
					return "208.67.222.222";
				}else{
					return null;
				}
			}
		}else{
			if(dnsServer.getServiceType() == DnsServiceProfile.SEPARATE_DNS){
				IpAddress dns1 = dnsServer.getInternalDns1();
				if(dns1 == null){
					return null;
				}
				return CLICommonFunc.getIpAddress(dns1, this.hiveAp).getIpAddress();
			}else{
				if(dnsServer.getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
					IpAddress dns1 = dnsServer.getInternalDns1();
					if(dns1 == null){
						return null;
					}
					return CLICommonFunc.getIpAddress(dns1, this.hiveAp).getIpAddress();
				}else if(dnsServer.getExternalServerType() == DnsServiceProfile.OPEN_DNS_TYPE){
					return "208.67.222.222";
				}else{
					return null;
				}
			}
		}
	}
	
	public String getIntResolveDns2(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		DnsServiceProfile dnsServer = dhcpInfoMap.get(type).getVpnNetwork().getVpnDnsService();
		VpnNetworkSub vpnSub = dhcpInfoMap.get(type).getVpnNetworkSub();
		if(vpnSub != null && vpnSub.isOverrideDNSService() && vpnSub.getDnsService() != null){
			if(vpnSub.getDnsService().getServiceType() == DnsServiceProfile.SEPARATE_DNS){
				IpAddress dns2 = vpnSub.getDnsService().getInternalDns2();
				if(dns2 == null){
					return null;
				}
				return CLICommonFunc.getIpAddress(dns2, this.hiveAp).getIpAddress();
			}else{
				if(vpnSub.getDnsService().getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
					IpAddress dns2 = vpnSub.getDnsService().getInternalDns2();
					if(dns2 == null){
						return null;
					}
					return CLICommonFunc.getIpAddress(dns2, this.hiveAp).getIpAddress();
				}else if(vpnSub.getDnsService().getExternalServerType() == DnsServiceProfile.OPEN_DNS_TYPE){
					return "208.67.220.220";
				}else{
					return null;
				}
			}
		}else{
			if(dnsServer.getServiceType() == DnsServiceProfile.SEPARATE_DNS){
				IpAddress dns2 = dnsServer.getInternalDns2();
				if(dns2 == null){
					return null;
				}
				return CLICommonFunc.getIpAddress(dns2, this.hiveAp).getIpAddress();
			}else{
				if(dnsServer.getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
					IpAddress dns2 = dnsServer.getInternalDns2();
					if(dns2 == null){
						return null;
					}
					return CLICommonFunc.getIpAddress(dns2, this.hiveAp).getIpAddress();
				}else if(dnsServer.getExternalServerType() == DnsServiceProfile.OPEN_DNS_TYPE){
					return "208.67.220.220";
				}else{
					return null;
				}
			}
		}
	}
	
	public String getIntResolveDns3(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		DnsServiceProfile dnsServer = dhcpInfoMap.get(type).getVpnNetwork().getVpnDnsService();
		VpnNetworkSub vpnSub = dhcpInfoMap.get(type).getVpnNetworkSub();
		if(vpnSub != null && vpnSub.isOverrideDNSService() && vpnSub.getDnsService() != null){
			if(vpnSub.getDnsService().getServiceType() == DnsServiceProfile.SEPARATE_DNS){
				IpAddress dns3 = vpnSub.getDnsService().getInternalDns3();
				if(dns3 == null){
					return null;
				}
				return CLICommonFunc.getIpAddress(dns3, this.hiveAp).getIpAddress();
			}else{
				if(vpnSub.getDnsService().getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
					IpAddress dns3 = vpnSub.getDnsService().getInternalDns3();
					if(dns3 == null){
						return null;
					}
					return CLICommonFunc.getIpAddress(dns3, this.hiveAp).getIpAddress();
				}else{
					return null;
				}
			}
		}else{
			if(dnsServer.getServiceType() == DnsServiceProfile.SEPARATE_DNS){
				IpAddress dns3 = dnsServer.getInternalDns3();
				if(dns3 == null){
					return null;
				}
				return CLICommonFunc.getIpAddress(dns3, this.hiveAp).getIpAddress();
			}else{
				if(dnsServer.getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
					IpAddress dns3 = dnsServer.getInternalDns3();
					if(dns3 == null){
						return null;
					}
					return CLICommonFunc.getIpAddress(dns3, this.hiveAp).getIpAddress();
				}else{
					return null;
				}
			}
		}
	}
	
	public String getExtResolveDns1(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		DnsServiceProfile dnsServer = dhcpInfoMap.get(type).getVpnNetwork().getVpnDnsService();
		VpnNetworkSub vpnSub = dhcpInfoMap.get(type).getVpnNetworkSub();
		if(vpnSub != null && vpnSub.isOverrideDNSService() && vpnSub.getDnsService() != null){
			if(vpnSub.getDnsService().getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
				IpAddress exDns1 = vpnSub.getDnsService().getExternalDns1();
				if(exDns1 == null){
					return null;
				}
				return CLICommonFunc.getIpAddress(exDns1, this.hiveAp).getIpAddress();
			}else if(vpnSub.getDnsService().getExternalServerType() == DnsServiceProfile.OPEN_DNS_TYPE){
				return "208.67.222.222";
			}else{
				return null;
			}
		}else{
			if(dnsServer.getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
				IpAddress exDns1 = dnsServer.getExternalDns1();
				if(exDns1 == null){
					return null;
				}
				return CLICommonFunc.getIpAddress(exDns1, this.hiveAp).getIpAddress();
			}else if(dnsServer.getExternalServerType() == DnsServiceProfile.OPEN_DNS_TYPE){
				return "208.67.222.222";
			}else{
				return null;
			}
		}
	}
	
	public String getExtResolveDns2(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		DnsServiceProfile dnsServer = dhcpInfoMap.get(type).getVpnNetwork().getVpnDnsService();
		VpnNetworkSub vpnSub = dhcpInfoMap.get(type).getVpnNetworkSub();
		if(vpnSub != null && vpnSub.isOverrideDNSService() && vpnSub.getDnsService() != null){
			if(vpnSub.getDnsService().getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
				IpAddress exDns2 = vpnSub.getDnsService().getExternalDns2();
				if(exDns2 == null){
					return null;
				}
				return CLICommonFunc.getIpAddress(exDns2, this.hiveAp).getIpAddress();
			}else if(vpnSub.getDnsService().getExternalServerType() == DnsServiceProfile.OPEN_DNS_TYPE){
				return "208.67.220.220";
			}else{
				return null;
			}
		}else{
			if(dnsServer.getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
				IpAddress exDns2 = dnsServer.getExternalDns2();
				if(exDns2 == null){
					return null;
				}
				return CLICommonFunc.getIpAddress(exDns2, this.hiveAp).getIpAddress();
			}else if(dnsServer.getExternalServerType() == DnsServiceProfile.OPEN_DNS_TYPE){
				return "208.67.220.220";
			}else{
				return null;
			}
		}
	}
	
	public String getExtResolveDns3(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		DnsServiceProfile dnsServer = dhcpInfoMap.get(type).getVpnNetwork().getVpnDnsService();
		VpnNetworkSub vpnSub = dhcpInfoMap.get(type).getVpnNetworkSub();
		if(vpnSub != null && vpnSub.isOverrideDNSService() && vpnSub.getDnsService() != null){
			if(vpnSub.getDnsService().getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
				IpAddress exDns3 = vpnSub.getDnsService().getExternalDns3();
				if(exDns3 == null){
					return null;
				}
				return CLICommonFunc.getIpAddress(exDns3, this.hiveAp).getIpAddress();
			}else{
				return null;
			}
		}else{
			if(dnsServer.getExternalServerType() == DnsServiceProfile.SPECIFIC_DNS_TYPE){
				IpAddress exDns3 = dnsServer.getExternalDns3();
				if(exDns3 == null){
					return null;
				}
				return CLICommonFunc.getIpAddress(exDns3, this.hiveAp).getIpAddress();
			}else{
				return null;
			}
		}
	}
	
	public static String getIpPool(String ipAndMask, int start, int end){
		if(ipAndMask == null || !ipAndMask.contains("/")){
			return null;
		}
		
		int index = ipAndMask.indexOf("/");
		String ipStr = ipAndMask.substring(0, index);
		long ipLong = AhEncoder.ip2Long(ipStr);
		int mask = Integer.valueOf(ipAndMask.substring(index+1));
		long maskLong = (long)Math.pow(2,(32-mask));
		long startIp = MgrUtil.getStartIpAddressValue(ipLong, mask);
		
		long poolstart = startIp + start + 2;
		long poolEnd = startIp + maskLong - end -2;
		return AhDecoder.long2Ip(poolstart) + " " + AhDecoder.long2Ip(poolEnd);
	}
	
	public static String getFirstIp(String ipAndMask){
		if(ipAndMask == null || !ipAndMask.contains("/")){
			return null;
		}
		
		int index = ipAndMask.indexOf("/");
		String ipStr = ipAndMask.substring(0, index);
		long ipLong = AhEncoder.ip2Long(ipStr);
		int mask = Integer.valueOf(ipAndMask.substring(index+1));
		long startIp = MgrUtil.getStartIpAddressValue(ipLong, mask);
		return AhDecoder.long2Ip(startIp + 1) + "/" + String.valueOf(mask);
	}
	
//	public int getEthxBypassAuthVlan(InterType ethx) throws CreateXMLException{
//		if(ethx != InterType.eth1){
//			return -1;
//		}
//		if(ethxPassVlan == null){
//			List<Integer> ethxPassVlanInt = new ArrayList<Integer>();
//			ethxPassVlan = new ArrayList<String>();
//			//TODO Port Template Profiles
//			if(this.hiveAp.getConfigTemplate().getLanProfiles() != null){
//				for(LanProfile lanObj : this.hiveAp.getConfigTemplate().getLanProfiles()){
//					if(lanObj.isEnabled8021Q()){
//						ethxPassVlanInt.add(getNativeVlanForLan(lanObj, this.hiveAp));
//						ethxPassVlanInt.addAll(getRegularVlanForLan(lanObj, this.hiveAp));
//					}
//				}
//			}
//			if(!ethxPassVlanInt.isEmpty()){
//				String allVlans = "";
//				for(int vlanId : ethxPassVlanInt){
//					if("".equals(allVlans)){
//						allVlans += String.valueOf(vlanId);
//					}else{
//						allVlans += "," + String.valueOf(vlanId);
//					}
//				}
//				allVlans = CLICommonFunc.mergeRange(allVlans);
//				String[] allVlanArg = allVlans.split(",");
//				for(int i=0; i<allVlanArg.length; i++){
//					ethxPassVlan.add(allVlanArg[i]);
//				}
//			}
//		}
//		
//		return ethxPassVlan.size();
//	}
	
	public static void main(String[] args){
	}
	
//	public String getEthxBypassAuthVlan(InterType ethx, int index){
//		return String.valueOf(ethxPassVlan.get(index));
//	}
	
	public Map<MgtType, DhcpServerInfo> getMgtSubResourceMap(){
		return this.dhcpInfoMap;
	}
	
	public boolean isConfigEthxPse(InterType type){
		return type == InterType.eth1 || type == InterType.eth2;
	}
	
	public String getEthxPseMode(InterType type){
		String modestr = null;
		short mode = interMap.get(type).getPseState();
		switch (mode) {
		case AhInterface.ETH_PSE_8023af:
			modestr = modeaf;
			break;
		case AhInterface.ETH_PSE_8023at:
			modestr = modeat;
			break;
		case AhInterface.ETH_PSE_8023af_EXTENDED:
			modestr = modextend;
			break;
		default:
			modestr = modeaf;
		}
		return modestr;
	}
	
	public boolean isEnableEthxShutdown(InterType type){
		return !interMap.get(type).isPseEnabled();
	}
	
	public Integer getEthxPsePriority(InterType type){
		String priority = interMap.get(type).getPsePriority();
		if(priority.equals(AhInterface.ETH_PSE_PRIORITY_ETH1)){
			return Integer.valueOf(AhInterface.ETH_PSE_PRIORITY_ETH2);
		}else{
			return Integer.valueOf(AhInterface.ETH_PSE_PRIORITY_ETH1);
		}
	}
	
	public boolean isConfigMgt0DhcpKeepalive() {
		return mgt0DhcpValns.size() > 0 && (hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_WP ||
				hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200 || 
				hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ);
	}
	
	public int getMgt0DhcpKeepaliveVlanSize(){
		return mgt0DhcpValns.size();
	}

	public String getMgt0DhcpKeepaliveValn(int index) {
		return mgt0DhcpValns.get(index);
	}
	
	public int getNatPolicyNameForSubNetworkSize() {
		return this.natPolicyNameForSubNetworkList.size();
	}
	
	public int getNatPolicyNameForPortForwardingSize() {
		return this.natPolicyNameForPortForwardingList.size();
	}

	public String getNatPolicyNameForSubNetwork(int index) {
		return this.natPolicyNameForSubNetworkList.get(index);
	}
	
	public String getNatPolicyNameForPortForwarding(int index) {
		return this.natPolicyNameForPortForwardingList.get(index);
	}
	
	public int getNatPolicySize() {
		return this.natPolicyList.size();
	}
	
	public String getNatPolicyName(int index) {
		return natPolicyList.get(index).get("policyName");
	}

	public boolean isNatPolicyConfigMatch(int index) {
		return "true".equals(natPolicyList.get(index).get("configMatch"));
	}

	public boolean isNatPolicyConfigVirtualHost(int index) {
		return "true".equals(natPolicyList.get(index).get("configVirtualHost"));
	}

	public String getNatPolicyMatchInsideValue(int index) {
		return natPolicyList.get(index).get("matchInside");
	}

	public String getNatPolicyMatchOutsideValue(int index) {
		return natPolicyList.get(index).get("matchOutside");
	}
	
	public String getNatPolicyVhostInsideHostValue(int index) {
		return natPolicyList.get(index).get("insideHost");
	}

	public String getNatPolicyVhostInsidePortValue(int index) {
		return natPolicyList.get(index).get("insidePort");
	}

	public String getNatPolicyVhostOutsidePortValue(int index) {
		return natPolicyList.get(index).get("outsidePort");
	}

	public String getNatPolicyVhostProtocolValue(int index) {
		return natPolicyList.get(index).get("protocol");
	}
}