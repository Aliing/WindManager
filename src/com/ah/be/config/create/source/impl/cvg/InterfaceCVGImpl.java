package com.ah.be.config.create.source.impl.cvg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.common.CVGAndBRIpResourceManage;
import com.ah.be.config.create.source.InterfaceProfileInt;
import com.ah.be.config.create.source.impl.InterfaceProfileImpl;
import com.ah.be.config.create.source.impl.branchRouter.InterfaceBRImpl;
import com.ah.be.config.create.source.impl.branchRouter.InterfaceBRImpl.DhcpServerInfo;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.SubNetworkResource;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnGatewaySetting;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnNetworkSub;
import com.ah.util.MgrUtil;
import com.ah.xml.be.config.EthDuplex;
import com.ah.xml.be.config.InterfaceRadioFixedAntennaValue;
import com.ah.xml.be.config.MgtDnsServerModeValue;


public class InterfaceCVGImpl extends InterfaceProfileImpl {
	
	private Map<InterType, DeviceInterface> interMap;
	
	private Map<MgtType, DhcpServerInfo> dhcpInfoMap = new HashMap<MgtType, DhcpServerInfo>();;
	private boolean view;
	
	public InterfaceCVGImpl(HiveAp hiveAp, boolean view) throws Exception {
		super(hiveAp);
		this.view = view;
		initDhcpInfoMap();
		
		interMap = new HashMap<InterType, DeviceInterface>();
		interMap.put(InterType.eth0, hiveAp.getEth0Interface());
		interMap.put(InterType.eth1, hiveAp.getEth1Interface());
	}
	
	private void initDhcpInfoMap() throws Exception{
		List<DhcpServerInfo> dhcpInfoList = new ArrayList<DhcpServerInfo>();
		
		//init VpnNetwork and Vlan
		if(hiveAp.getCvgDPD() != null && hiveAp.getCvgDPD().getMgtVlan() != null 
				&& hiveAp.getCvgDPD().getMgtNetwork() != null){
			Vlan vlan = hiveAp.getCvgDPD().getMgtVlan();
			VpnNetwork mgt0Network = hiveAp.getCvgDPD().getMgtNetwork();
			DhcpServerInfo mgt0dhcpInfo = new DhcpServerInfo();
			mgt0dhcpInfo.setVpnNetwork(mgt0Network);
			mgt0dhcpInfo.setVlan(vlan);
			dhcpInfoList.add(mgt0dhcpInfo);
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
		
		//mapping for mgt0
		Iterator<DhcpServerInfo> dhcpItems = null;
		if(dhcpInfoMap.get(MgtType.mgt0) == null){
			dhcpItems = dhcpInfoList.iterator();
			while(dhcpItems.hasNext()){
				DhcpServerInfo dhcpInfo = dhcpItems.next();
				dhcpInfoMap.put(MgtType.mgt0, dhcpInfo);
				dhcpItems.remove();
				break;
			}
		}
		
		//build mapping between interface mgt0.X and SubNetworkResource
		List<SubNetworkResource> resources = new ArrayList<SubNetworkResource>();
		for(MgtType type : dhcpInfoMap.keySet()){
			SubNetworkResource subResource = dhcpInfoMap.get(type).getSubNetwork();
			subResource.setHiveApMgtx(InterfaceBRImpl.getMgtShortType(type));
			resources.add(subResource);
		}
		CVGAndBRIpResourceManage.updateMgtxNetworkMapping(hiveAp.getOwner(), resources);
	}
	
	public Map<MgtType, DhcpServerInfo> getMgtSubResourceMap(){
		return this.dhcpInfoMap;
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


	public boolean isInterShutdown(InterType type) {
		if(interMap.get(type) == null){
			return false;
		}
		short ehtStatus = interMap.get(type).getAdminState();
		return ehtStatus == AhInterface.ADMIN_STATE_DOWM;
	}

	public String getInterSpeed(InterType type) {
		return "";
	}

	public EthDuplex getInterDuplex(InterType type) {
		return null;
	}

	public boolean isInterConfigQosClass(InterType type) {
		return false;
	}

	public String getInterQosClassifier(InterType type) {
		return null;
	}

	public boolean isConfigInterQosMarker(InterType type) {
		return false;
	}

	public String getInterQosMarker(InterType type) {
		return null;
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
				return InterfaceBRImpl.getFirstIp(network.getIpAddressSpace());
			}
		}else{
			return null;
		}
	}

	public boolean isConfigMgtHive() {
		return false;
	}

	public String getMgtBindHive() {
		return null;
	}
	
	public boolean isConfigMgtVlan(){
		return this.hiveAp.getConfigTemplate().getVlan() != null;
//		return mgtMap.get(MgtType.mgt0) != null && mgtMap.get(MgtType.mgt0).getVlan() != null;
	}

	public int getMgtVlanId() throws CreateXMLException {
		return CLICommonFunc.getVlan(hiveAp.getConfigTemplate().getVlan(), hiveAp).getVlanId();
	}

	public boolean isConfigInterMode(InterfaceMode mode, InterType type){
		if(mode == InterfaceMode.wan){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isEnableWanNat(InterType type){
		if(type == InterType.eth0){
			return isEqualPubIp(this.hiveAp);
		}else if(type == InterType.eth1){
			return false;
		}else{
			return false;
		}
	}
	
	public static boolean isEqualPubIp(HiveAp hiveAp){
		String internalIp = hiveAp.getEth0Interface().getIpAddress();
		String pubIp=null;
		if(internalIp == null || "".equals(internalIp)){
			return false;
		}
		if(hiveAp.getConfigTemplate().getVpnService() == null || 
				hiveAp.getConfigTemplate().getVpnService().getVpnGateWaysSetting() == null){
			return false;
		}
		for(VpnGatewaySetting cvgObj : hiveAp.getConfigTemplate().getVpnService().getVpnGateWaysSetting()){
			if(hiveAp.getId().equals(cvgObj.getApId())){
				pubIp = cvgObj.getExternalIpAddress();
				break;
			}
		}
		return internalIp.equals(pubIp);
	}

	public boolean isConfigInterManage(InterType type) {
		return false;
	}

	public boolean isEnableInterManage(ManageType type, InterType type1) {
		return false;
	}

	public InterfaceWifiModeValue getInterWifiMode(InterfaceWifi wifi) {
		return null;
	}

	public boolean isWifiBindSsid(InterfaceWifi wifi) {
		return false;
	}

	public String getInterfaceWifiRadioChannel(InterfaceWifi wifi) {
		return null;
	}

	public String getInterfaceWifiRadioPower(InterfaceWifi wifi) {
		return null;
	}

	public boolean isConfigureWifiRadioProfile(InterfaceWifi wifi) {
		return false;
	}

	public String getInterfaceWifiRadioProfileName(InterfaceWifi wifi) {
		return null;
	}

	public boolean isConfigureRadioAntennaExternal(InterfaceWifi wifi) {
		return false;
	}
	
	public int getRadioAntennaDefault(InterfaceWifi wifi){
		return -1;
	}

	public boolean isConfigureInterfaceWlan(InterfaceWifi wifi) {
		return false;
	}

	public String getInterfaceWlanProfileName() {
		return null;
	}

	public int getInterfaceWifiSsidSize() {
		return -1;
	}

	public boolean isConfigWifiSsid(InterfaceWifi wifi, int index) {
		return false;
	}

	public String getWifiSsidName(int index) {
		return null;
	}

	public boolean isConfigureSsidIp(InterfaceWifi wifi, int index) {
		return false;
	}
	
	public boolean isShutDownWifiSsid(InterfaceWifi wifi, int index){
		return false;
	}

	public String getWifiSsidIp(InterfaceWifi wifi, int index) {
		return null;
	}

	public boolean isEnableInterMacLearning(InterType type) {
		return false;
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
//		Vlan nativeVlan = hiveAp.getConfigTemplate().getVlanNative();
//		return CLICommonFunc.getVlan(nativeVlan, hiveAp).getVlanId();
		return -1;
	}
	
	public boolean isConfigBridgeUserProfile(InterType type){
		return false;
	}

	public int getInterAccessUserProfileAttr(InterType type) {
		return -1;
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
		if(ethType == InterType.eth0){
			return true;
		}else if(ethType == InterType.eth1){
			return this.hiveAp.getEth1Interface().isIfActive();
		}else{
			return false;
		}
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
		return false;
	}

	public boolean isEnableInterStationTraffic(InterType type) {
		return false;
	}

	public boolean isConfigMgtChild(MgtType type) {
		return false;
	}

	public int getMgtChildVlan(MgtType type) {
		return -1;
	}

	public String getMgtChildIp(MgtType type) {
		return null;
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
		return false;
	}

	public boolean isEnableMgtChildDhcpServer(MgtType type) {
		return false;
	}

	public boolean isEnableMgtChildAuthoritative(MgtType type) {
		return false;
	}

	
	public int getMgtChildIpPoolSize(MgtType type) {
		return -1;
	}

	public String getMgtChildIpPoolName(MgtType type, int index) {
		return null;
	}

	public boolean isEnableMgtChildArpCheck(MgtType type) {
		return false;
	}

	public boolean isConfigMgtChildOptionsDefaultGateway(MgtType type) {
		return false;
	}

	public String getMgtChildOptionsDefaultGateway(MgtType type) {
		return null;
	}
	
	public boolean isMgtDhcpNatSupport(MgtType type){
		return false;
	}

	public boolean isConfigMgtChildOptionsLeaseTime(MgtType type) {
		return false;
	}

	public int getMgtChildOptionsLeaseTime(MgtType type) {
		return -1;
	}

	public boolean isConfigMgtChildOptionsNetMask(MgtType type) {
		return false;
	}

	public String getMgtChildOptionsNetMask(MgtType type) {
		return null;
	}

	public int getMgtChildOptionsHivemanagerSize(MgtType type) throws CreateXMLException {
		return -1;
	}

	public String getMgtChildOptionsHivemanager(MgtType type, int index) {
		return null;
	}

	public boolean isConfigMgtChildOptionsDoMain(MgtType type) {
		return false;
	}

	public String getMgtChildOptionsDoMain(MgtType type) {
		return null;
	}

	public boolean isConfigMgtChildOptionsMtu(MgtType type) {
		return false;
	}

	public int getMgtChildOptionsMtu(MgtType type) {
		return -1;
	}

	public boolean isConfigMgtChildOptionsDns1(MgtType type) {
		return false;
	}

	public String getMgtChildOptionsDns1(MgtType type) {
		return null;
	}

	public boolean isConfigMgtChildOptionsDns2(MgtType type) {
		return false;
	}

	public String getMgtChildOptionsDns2(MgtType type) {
		return null;
	}

	public boolean isConfigMgtChildOptionsDns3(MgtType type) {
		return false;
	}

	public String getMgtChildOptionsDns3(MgtType type) {
		return null;
	}

	public boolean isConfigMgtChildOptionsNtp1(MgtType type) {
		return false;
	}

	public String getMgtChildOptionsNtp1(MgtType type) {
		return null;
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
		return -1;
	}

	public int getMgtChildOptionsCustomName(MgtType type, int index) {
		return -1;
	}

	public boolean isConfigMgtChildOptionsCustomInteger(MgtType type, int index) {
		return false;
	}

	public int getMgtChildOptionsCustomIntegerValue(MgtType type, int index) {
		return -1;
	}

	public boolean isConfigMgtChildOptionsCustomIp(MgtType type, int index) {
		return false;
	}

	public String getMgtChildOptionsCustomIpValue(MgtType type, int index) {
		return null;
	}

	public boolean isConfigMgtChildOptionsCustomString(MgtType type, int index) {
		return false;
	}
	
	public boolean isConfigMgtChildOptionsCustomHex(MgtType type, int index){
		return false;
	}

	public String getMgtChildOptionsCustomStringValue(MgtType type, int index) {
		return null;
	}
	
	public String getMgtChildOptionsCustomHexValue(MgtType type, int index){
		return null;
	}
	
	public boolean isConfigLinkDiscovery(){
		return false;
	}
	
	public int getInterfaceRadioRange(InterfaceWifi wifi){
		return -1;
	}
	
	public boolean isConfigRadioFixedAntenna(InterfaceWifi wifi){
		return false;
	}
	
	public InterfaceRadioFixedAntennaValue getRadioFixedAntenna(InterfaceWifi wifi){
		return null;
	}
	
	public boolean isEnableCCA(InterfaceWifi wifi){
		return false;
	}
	
	public int getMaxCca(InterfaceWifi wifi){
		return -1;
	}
	
	public int getDefaultCca(InterfaceWifi wifi){
		return -1;
	}

	public boolean isConfigWifi1(){
		return false;
	}
	
	public boolean isWifiShutdown(InterfaceWifi wifi) {
		return false;
	}
	
	public boolean isConfigWifiShutdown(InterfaceWifi wifi){
		return false;
	}
	
	public boolean isEnableAntennaDiversity(InterfaceWifi wifi){
		return false;
	}
	
	public boolean isHiveAp20(){
		return false;
	}
	
	public boolean isHiveAp28(){
		return false;
	}
	
	public boolean isConfigEthAllowedVlan(InterType type){
		return false;
	}
	
	public boolean isConfigAllowedVlanAll(InterType type){
		return false;
	}
	
	public boolean isConfigAllowedVlanAuto(InterType type){
		return false;
	}
	
	public boolean isConfigAllowedVlanNum(InterType type){
		return false;
	}
	
	public int getAllowedVlanSize(InterType type) {
		return -1;
	}
	
	public String getAllowedVlanStr(InterType type, int i){
		return null;
	}
	
	public boolean isConfigEthSecurity(InterType type){
		return false;
	}
	
	public String getEthSecurityObjName(InterType type){
		return null;
	}
	
	public boolean isConfigEthIp(InterType type){
		if(type == InterType.eth0){
			String ipAddr = hiveAp.getEth0Interface().getIpAddress();
			return ipAddr != null && !"".equals(ipAddr);
		}else if(type == InterType.eth1){
			String ipAddr = hiveAp.getEth1Interface().getIpAddress();
			return ipAddr != null && !"".equals(ipAddr);
		}else{
			return false;
		}
	}
	
	public String getEthIp(InterType type){
		if(type == InterType.eth0){
			return hiveAp.getEth0Interface().getIpAndNetmask();
		}else if(type == InterType.eth1){
			return hiveAp.getEth1Interface().getIpAndNetmask();
		}else{
			return null;
		}
	}
	
	public boolean isConfigEthNativeVlan(InterType type){
		return false;
	}
	
	public int getEthNativeVlan(InterType type){
		return -1;
	}
	
	public boolean isConfigEthDhcp(InterType type){
		return true;
	}
	
	public boolean isEnableEthDhcp(InterType type){
		return false;
	}
	
	public boolean isConfigMgtDnsServer(InterfaceProfileInt.MgtType type){
		return false;
	}
	
	public boolean isEnableMgtDnsServer(InterfaceProfileInt.MgtType type){
		return false;
	}
	
	public MgtDnsServerModeValue getMgtDnsServerMode(InterfaceProfileInt.MgtType type){
		return null;
	}
	
	public int getIntDomainNameSize(InterfaceProfileInt.MgtType type){
		return -1;
	}
	
	public String getIntDomainName(InterfaceProfileInt.MgtType type, int index){
		return null;
	}
	
	public String getIntDnsServer(InterfaceProfileInt.MgtType type, int index){
		return null;
	}
	
	public boolean isConfigDnsIntResolve(InterfaceProfileInt.MgtType type){
		return false;
	}
	
	public boolean isConfigDnsExtResolve(InterfaceProfileInt.MgtType type){
		return false;
	}
	
	public String getIntResolveDns1(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		return null;
	}
	
	public String getIntResolveDns2(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		return null;
	}
	
	public String getIntResolveDns3(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		return null;
	}
	
	public String getExtResolveDns1(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		return null;
	}
	
	public String getExtResolveDns2(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		return null;
	}
	
	public String getExtResolveDns3(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		return null;
	}
	
	public boolean isConfigEthxPse(InterType type){
		return false;
	}
	public String getEthxPseMode(InterType type) {
		return null;
	}

	public boolean isEnableEthxShutdown(InterType type) {
		return false;
	}
	
	public Integer getEthxPsePriority(InterType type){
		return 0;
	}
	
	public boolean isConfigMgt0DhcpKeepalive() {
		return false;
	}
	
	public int getMgt0DhcpKeepaliveVlanSize(){
		return -1;
	}
	
	public String getMgt0DhcpKeepaliveValn(int index) {
		return null;
	}
}
