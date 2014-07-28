package com.ah.be.config.create.source;

import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.impl.branchRouter.InterfaceBRImpl.DhcpServerInfo;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.xml.be.config.EthDuplex;
import com.ah.xml.be.config.InterfaceFlowControlValue;
import com.ah.xml.be.config.InterfaceRadioFixedAntennaValue;
import com.ah.xml.be.config.InterfaceSwitchportModeValue;
import com.ah.xml.be.config.MgtDnsServerModeValue;
import com.ah.xml.be.config.SpanningTreeBpduProtectionValue;

/***
 * 
 * @author zhang
 *
 */
public interface InterfaceProfileInt {
	
	public static enum InterfaceWifi{
		wifi0, wifi1
	}
	
	public static enum InterfaceMode{
		access, bridgeAccess, backhaul, bridge8021q,bridge,dual,wan
	};
	
	public static enum ManageType{
		SNMP, SSH, Telnet, ping
	}
	
	public static enum InterType{
		eth0, eth1,eth2,eth3,eth4, agg0, red0, usb, wifi0, wifi1
	}
	
	public static class MgtType{
		public static MgtType mgt0 = new MgtType("mgt0");
		public static MgtType mgt01 = new MgtType("mgt0.1");
		public static MgtType mgt02 = new MgtType("mgt0.2");
		public static MgtType mgt03 = new MgtType("mgt0.3");
		public static MgtType mgt04 = new MgtType("mgt0.4");
		public static MgtType mgt05 = new MgtType("mgt0.5");
		public static MgtType mgt06 = new MgtType("mgt0.6");
		public static MgtType mgt07 = new MgtType("mgt0.7");
		public static MgtType mgt08 = new MgtType("mgt0.8");
		public static MgtType mgt09 = new MgtType("mgt0.9");
		public static MgtType mgt010 = new MgtType("mgt0.10");
		public static MgtType mgt011 = new MgtType("mgt0.11");
		public static MgtType mgt012 = new MgtType("mgt0.12");
		public static MgtType mgt013 = new MgtType("mgt0.13");
		public static MgtType mgt014 = new MgtType("mgt0.14");
		public static MgtType mgt015 = new MgtType("mgt0.15");
		public static MgtType mgt016 = new MgtType("mgt0.16");

		private String value;
		
		public MgtType(String value){
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public boolean equals(Object obj) {
			return new EqualsBuilder().append(this.value, ((MgtType) obj).getValue()).isEquals();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(this.value).toHashCode();
		}
	}
	
	public static enum InterfaceWifiModeValue {
		ACCESS("access"), BACKHAUL("backhaul"), DUAL("dual"), WAN("wan-client");
		
		private String value;
		InterfaceWifiModeValue(String value) {
			this.value = value;
		}
		
		public String value() {
			return value;
		}
	}
	
	public String getHiveApGuiName();
	
	public String getHiveApName();
	
	public String getApVersion();

//	public String getUpdateTime();
//	
//	public String getEthUpdateTime();
//	
//	public String getWifiUpdateTime();
//	
//	public String getMgtUpdateTime();
	
	public boolean isConfigInterManage(InterType type);
	
	public boolean isInterShutdown(InterType type);
	
	public String getInterSpeed(InterType type);
	
	public EthDuplex getInterDuplex(InterType type);
	
	public boolean isInterConfigQosClass(InterType type);
	
	public String getInterQosClassifier(InterType type);
	
	public boolean isConfigInterQosMarker(InterType type);
	
	public String getInterQosMarker(InterType type);
	
	public String getMgtIpAndMask() throws CreateXMLException;
	
	public boolean isConfigMgtHive();
	
	public String getMgtBindHive();
	
	public boolean isConfigMgtVlan();

	public int getMgtVlanId() throws CreateXMLException;
	
	public boolean isConfigInterMode(InterfaceMode mode, InterType type) throws CreateXMLException;
	
	public boolean isEnableWanNat(InterType type);
	
	public int getInterfacePriority(InterType type) throws Exception;

	public boolean isEnableInterManage(ManageType type, InterType type1);
	
	public InterfaceWifiModeValue getInterWifiMode(InterfaceWifi wifi);
	
	public boolean isWifiModeAccess(InterfaceWifi wifi);
	
	public boolean isWifiModeBackhaul(InterfaceWifi wifi);
	
	public boolean isWifiModeDual(InterfaceWifi wifi);
	
	public boolean isWifiModeSensor(InterfaceWifi wifi);
	
	public boolean isWifiModeWan(InterfaceWifi wifi);

	public boolean isEnableWifiAsClient(int index);
	
	public boolean isWifiBindSsid(InterfaceWifi wifi);
	
	public String getInterfaceWifiRadioChannel(InterfaceWifi wifi );
	
	public String getInterfaceWifiRadioPower(InterfaceWifi wifi );
	
	public String getInterfaceWifiRadioProfileName(InterfaceWifi wifi );
	
	public boolean isConfigureWifiRadioProfile(InterfaceWifi wifi );
	
//	public boolean isConfigureRadioAntenna(InterfaceWifi wifi);
	
	public boolean isConfigureRadioAntennaExternal(InterfaceWifi wifi);
	
	public int getRadioAntennaDefault(InterfaceWifi wifi);
	
	public boolean isConfigureInterfaceWlan(InterfaceWifi wifi);
	
	public String getInterfaceWlanProfileName();
	
	public int getInterfaceWifiSsidSize();
	
	public boolean isConfigWifiSsid(InterfaceWifi wifi, int index);
	
	public String getWifiSsidName(int index);
	
	public boolean isConfigureSsidIp(InterfaceWifi wifi, int index);
	
	public boolean isShutDownWifiSsid(InterfaceWifi wifi, int index);
	
	public String getWifiSsidIp(InterfaceWifi wifi, int index);
	
//	public boolean isConfigInterMacLearning(InterType type);
	
	public boolean isEnableInterMacLearning(InterType type);
	
	public boolean isConfigIdleTimeout(InterType type);
	
//	public boolean isConfigInterIdleTimeout(InterType type);
	
	public int getInterIdleTimeout(InterType type);
	
	public int getInterMacLearningStaticSize(InterType type);
	
	public String getStaticMacAddr(InterType type, int index) throws Exception;
	
	public boolean isEnableMgtDhcp();
	
	public boolean isConfigMgtIp();
	
//	public boolean isConfigMgtNativeVlan() throws CreateXMLException;
	
	public int getMgtNativeVlan() throws CreateXMLException;
	
	public boolean isConfigBridgeUserProfile(InterType type);
	
	public int getInterAccessUserProfileAttr(InterType type);
	
//	public boolean isConfigInterModeBridge(InterType type);
	
	public boolean isEnableDhcpFallBack();
	
	public int getDhcpTimeOutt();
	
	public boolean isConfigMgtDefaultIpPrefix();
	
	public String getMgtDefaultIpPrefix();
	
	public String getMgtOldDefaultIpPrefix();
	
	public boolean isHiveAp11n();
	
	public boolean isConfigEthx(InterType ethType);
	
	public boolean isConfigInterBind(InterType type);
	
	public boolean isConfigInterBindAgg0(InterType type);
	
	public boolean isConfigInterBindRed0(InterType type);
	
	public boolean isConfigInterBindRedPrimary(InterType type);
	
	public boolean isConfigInterRed0();
	
	public boolean isConfigInterAgg0();
	
	public boolean isConfigDhcpAddressOnly();
	
	public boolean isEnableDhcpAddressOnly();
	
	public boolean isConfigInterStationTraffic(InterType type) throws CreateXMLException;
	
	public boolean isEnableInterStationTraffic(InterType type);
	
	public boolean isConfigMgtChild(MgtType type);
	
	public int getMgtChildVlan(MgtType type) throws CreateXMLException;
	
	public String getMgtChildIp(MgtType type) throws CreateXMLException;
	
	public boolean isConfigMgtChildIpHelper(MgtType type);
	
	public int getMgtChildIpHelperSize(MgtType type);
	
	public String getMgtChildIpHelperAddress(MgtType type, int index);
	
	public boolean isMgtChildPingEnable(MgtType type);
	
	public boolean isEnableMgtChildDhcpServer(MgtType type);
	
	public boolean isEnableMgtChildAuthoritative(MgtType type);
	
	public int getMgtChildIpPoolSize(MgtType type);
	
	public String getMgtChildIpPoolName(MgtType type, int index) throws CreateXMLException;
	
	public boolean isEnableMgtChildArpCheck(MgtType type);
	
	public boolean isConfigMgtChildOptionsDefaultGateway(MgtType type);
	
	public String getMgtChildOptionsDefaultGateway(MgtType type) throws CreateXMLException;
	
	public boolean isMgtDhcpNatSupport(MgtType type);
	
	public boolean isConfigMgtChildOptionsLeaseTime(MgtType type);
	
	public int getMgtChildOptionsLeaseTime(MgtType type);
	
	public boolean isConfigMgtChildOptionsNetMask(MgtType type);
	
	public String getMgtChildOptionsNetMask(MgtType type);
	
	public int getMgtChildOptionsHivemanagerSize(MgtType type) throws CreateXMLException;
	
	public String getMgtChildOptionsHivemanager(MgtType type, int index);
	
	public boolean isConfigMgtChildOptionsDoMain(MgtType type);
	
	public String getMgtChildOptionsDoMain(MgtType type);
	
	public boolean isConfigMgtChildOptionsMtu(MgtType type);
	
	public int getMgtChildOptionsMtu(MgtType type);
	
	public boolean isConfigMgtChildOptionsDns1(MgtType type);
	
	public String getMgtChildOptionsDns1(MgtType type) throws CreateXMLException;
	
	public boolean isConfigMgtChildOptionsDns2(MgtType type) throws CreateXMLException;
	
	public String getMgtChildOptionsDns2(MgtType type) throws CreateXMLException;
	
	public boolean isConfigMgtChildOptionsDns3(MgtType type) throws CreateXMLException;
	
	public String getMgtChildOptionsDns3(MgtType type) throws CreateXMLException;
	
	public boolean isConfigMgtChildOptionsNtp1(MgtType type);
	
	public String getMgtChildOptionsNtp1(MgtType type);
	
	public boolean isConfigMgtChildOptionsNtp2(MgtType type);
	
	public String getMgtChildOptionsNtp2(MgtType type);
	
	public boolean isConfigMgtChildOptionsPop3(MgtType type);
	
	public String getMgtChildOptionsPop3(MgtType type);
	
	public boolean isConfigMgtChildOptionsSmtp(MgtType type);
	
	public String getMgtChildOptionsSmtp(MgtType type);
	
	public boolean isConfigMgtChildOptionsWins1(MgtType type);
	
	public String getMgtChildOptionsWins1(MgtType type);
	
	public boolean isConfigMgtChildOptionsWins2(MgtType type);
	
	public String getMgtChildOptionsWins2(MgtType type);
	
	public boolean isConfigMgtChildOptionsLogsrv(MgtType type);
	
	public String getMgtChildOptionsLogsrv(MgtType type);
	
	public int getMgtChildOptionsCustomSize(MgtType type);
	
	public int getMgtChildOptionsCustomName(MgtType type, int index);
	
	public boolean isConfigMgtChildOptionsCustomInteger(MgtType type, int index);
	
	public int getMgtChildOptionsCustomIntegerValue(MgtType type, int index);
	
	public boolean isConfigMgtChildOptionsCustomIp(MgtType type, int index);
	
	public String getMgtChildOptionsCustomIpValue(MgtType type, int index);
	
	public boolean isConfigMgtChildOptionsCustomString(MgtType type, int index);
	
	public boolean isConfigMgtChildOptionsCustomHex(MgtType type, int index);
	
	public String getMgtChildOptionsCustomStringValue(MgtType type, int index);
	
	public String getMgtChildOptionsCustomHexValue(MgtType type, int index);
	
//	public boolean isEnableInterLldp(InterType type);
//	
//	public boolean isEnableInterCdp(InterType type);
//	
//	public boolean isEnableWifiLldp(InterfaceWifi type);
//	
//	public boolean isEnableWifiCdp(InterfaceWifi type);
	
	public boolean isConfigLinkDiscovery();
	
	public int getInterfaceRadioRange(InterfaceWifi wifi);
	
	public boolean isConfigRadioFixedAntenna(InterfaceWifi wifi);
	
	public InterfaceRadioFixedAntennaValue getRadioFixedAntenna(InterfaceWifi wifi);
	
	public boolean isEnableCCA(InterfaceWifi wifi);
	
	public int getMaxCca(InterfaceWifi wifi);
	
	public int getDefaultCca(InterfaceWifi wifi);
	
	public boolean isConfigWifi1();
	
	public boolean isConfigWifi0();
	
	public boolean isWifiShutdown(InterfaceWifi wifi);
	
	public boolean isConfigWifiShutdown(InterfaceWifi wifi);
	
	public boolean isWifiClientModeShutdown(InterfaceWifi wifi);
	
	public boolean isEnableAntennaDiversity(InterfaceWifi wifi);
	
	public boolean isHiveAp20();
	
	public boolean isHiveAp28();
	
	public boolean isConfigEthAllowedVlan(InterType type);
	
	public boolean isConfigAllowedVlanAll(InterType type);
	
	public boolean isConfigAllowedVlanAuto(InterType type);
	
	public int getAllowedVlanSize(InterType type);
	
	public String getAllowedVlanStr(InterType type, int i);
	
	public boolean isConfigEthSecurity(InterType type);
	
	public String getEthSecurityObjName(InterType type);
	
	public boolean isConfigEthIp(InterType type);
	
	public String getEthIp(InterType type);
	
	public boolean isConfigAllowedVlanNum(InterType type);
	
	public boolean isConfigEthNativeVlan(InterType type);
	
	public int getEthNativeVlan(InterType type) throws CreateXMLException;
	
	public boolean isConfigEthDhcp(InterType type);
	
	public boolean isEnableEthDhcp(InterType type);
	
	public boolean isConfigMgtDnsServer(InterfaceProfileInt.MgtType type);
	
	public boolean isEnableMgtDnsServer(InterfaceProfileInt.MgtType type);
	
	public MgtDnsServerModeValue getMgtDnsServerMode(InterfaceProfileInt.MgtType type);
	
	public int getIntDomainNameSize(InterfaceProfileInt.MgtType type);
	
	public String getIntDomainName(InterfaceProfileInt.MgtType type, int index);
	
	public String getIntDnsServer(InterfaceProfileInt.MgtType type, int index);
	
	public boolean isConfigDnsIntResolve(InterfaceProfileInt.MgtType type);
	
	public boolean isConfigDnsExtResolve(InterfaceProfileInt.MgtType type);
	
	public String getIntResolveDns1(InterfaceProfileInt.MgtType type) throws CreateXMLException;
	
	public String getIntResolveDns2(InterfaceProfileInt.MgtType type) throws CreateXMLException;
	
	public String getIntResolveDns3(InterfaceProfileInt.MgtType type) throws CreateXMLException;
	
	public String getExtResolveDns1(InterfaceProfileInt.MgtType type) throws CreateXMLException;
	
	public String getExtResolveDns2(InterfaceProfileInt.MgtType type) throws CreateXMLException;
	
	public String getExtResolveDns3(InterfaceProfileInt.MgtType type) throws CreateXMLException;
	
//	public int getEthxBypassAuthVlan(InterfaceProfileInt.InterType ethx) throws CreateXMLException;
//	
//	public String getEthxBypassAuthVlan(InterfaceProfileInt.InterType ethx, int index);
	
	public Map<MgtType, DhcpServerInfo> getMgtSubResourceMap();
	
	public boolean isConfigEthxPse(InterfaceProfileInt.InterType type);
	
	public String getEthxPseMode(InterfaceProfileInt.InterType type);
	
	public boolean isEnableEthxShutdown(InterfaceProfileInt.InterType type);
	
	public Integer getEthxPsePriority(InterfaceProfileInt.InterType type);	
	
	public boolean isConfigMgt0DhcpKeepalive();
	
	public int getMgt0DhcpKeepaliveVlanSize();
	
	public String getMgt0DhcpKeepaliveValn(int index);
	
	public boolean isConfigInterfaceUSB();
	
	public boolean isConfigMgtX(InterfaceProfileInt.MgtType type);
	
	
	// switch
	public int getSRInfeSize(DeviceInfType type);
	
	public int getInterfaceVlansize();
	
	public MgtType getMgtTypeType(int index);
	
	public int getMTUValue();
	
	public int getMgt0MTUValue();
	
	public String getInfPortName(DeviceInfType type, int index);
	
	public String getInfSpeed(DeviceInfType type, int index);
	
	public EthDuplex getInfDuplex(DeviceInfType type, int index);
	
	public boolean isEnableInfAutoMdix(DeviceInfType type, int index);
	
	public InterfaceFlowControlValue getInfFlowControlValue(DeviceInfType type, int index);
	
	public int getInfLinkDebounce(DeviceInfType type, int index);
	
	public boolean isConfigInfSwitchPort(DeviceInfType type, int index);
	
	public InterfaceSwitchportModeValue getInfPortVlanMode(DeviceInfType type, int index);
	
	public boolean isConfigSwitchPortAccess(DeviceInfType type, int index);
	
	public boolean isConfigSwitchPortTrunk(DeviceInfType type, int index);
	
	public int getAccessVlan(DeviceInfType type, int index) throws CreateXMLException;
	
	public int getNativeVlan(DeviceInfType type, int index) throws CreateXMLException;
	
	public boolean isInfPortAllowedVlanAll(DeviceInfType type, int index);
	
	public String[] getInfPortAllowedVlan(DeviceInfType type, int index) throws CreateXMLException;
	
	public boolean isConfigPortUserProfileId(DeviceInfType type, int index);
	
	public int getPortUserProfileId(DeviceInfType type, int index);
	
	public int getInfPortVoiceVlan(DeviceInfType type, int index) throws CreateXMLException;
	
	public boolean isConfigInfPortChannel(DeviceInfType type, int index);
	
	public String getInfPortChannel(DeviceInfType type, int index);
	
	public boolean isConfigInfSecurityObject(DeviceInfType type, int index);
	
	public String getInfSecurityObject(DeviceInfType type, int index);
	
	public boolean isConfigInfDhcpClient(DeviceInfType type, int index);
	
	public boolean isEnableInfDhcpClient(DeviceInfType type, int index);
	
	public boolean isConfigInfIp(DeviceInfType type, int index);
	
	public String getInfIp(DeviceInfType type, int index);
	
	public boolean isInfShutdown(DeviceInfType type, int index);
	
	public boolean isConfigInfWAN(DeviceInfType type, int index);
	
	public boolean isConfigInfStormControl(DeviceInfType type, int index);
	
	public boolean isInfWanNatEnable(DeviceInfType type, int index);
	
	public boolean isConfigInfQosClassifier(DeviceInfType type, int index);
	
	public String getInfQosClassifierName(DeviceInfType type, int index);
	
	public boolean isConfigInfQosMarker(DeviceInfType type, int index);
	
	public String getInfQosMarkerName(DeviceInfType type, int index);
	
	public boolean isConfigInfQosShaper(DeviceInfType type, int index);
	
	public int getInfQosShaperValue(DeviceInfType type, int index);
	
	public boolean isConfigStormControlAll(DeviceInfType type, int index);
	
	public boolean isConfigStormControlBroadcast(DeviceInfType type, int index);
	
	public boolean isConfigStormControlMulticast(DeviceInfType type, int index);
	
	public boolean isConfigStormControlUnknownUnicast(DeviceInfType type, int index);
	
	public boolean isConfigStormControlTcpSyn(DeviceInfType type, int index);
	
	public String isConfigStormControlMode();
	
	public boolean isConfigStormControlPercentage(DeviceInfType type, int index);
	
	public int getStormControlPercentage(DeviceInfType type, int index);
	
	public boolean isConfigStormControlBps(DeviceInfType type, int index);
	
	public int getStormControlBps(DeviceInfType type, int index);
	
	public boolean isConfigStormControlKbps(DeviceInfType type, int index);
	
	public int getStormControlKbps(DeviceInfType type, int index);
	
	public boolean isConfigStormControlPps(DeviceInfType type, int index);
	
	public int getStormControlPps(DeviceInfType type, int index);
	
	public String getInfVlanName(MgtType type) throws CreateXMLException;
	
	public boolean isEnableLldpReceive(DeviceInfType type, int index);
	
	public boolean isEnableLldpTransmit(DeviceInfType type, int index);
	
	public boolean isEnableCdpReceive(DeviceInfType type, int index);
	
	public boolean isBindToPortChannel(DeviceInfType type, int index);
	
	public boolean isConfigInfPse(DeviceInfType type, int index);
	
	public boolean isShutdownInfPse(DeviceInfType type, int index);
	
	public boolean isConfigPseProfile(DeviceInfType type, int index);
	
	public String getInfPseProfileName(DeviceInfType type, int index);
	
	public boolean isConfigInfDescription(DeviceInfType type, int index);
	
	public String getInfDescription(DeviceInfType type, int index);
	
	public int getWanInterfacePriority(InterType type);
	
	public int getWanInterfacePriority(DeviceInfType type,int index);
	
	public boolean isConfigClientReport(DeviceInfType type, int index);
	
	public boolean isEnableClientReport(DeviceInfType type, int index);
	
	/** start spanning tree */
	
	public boolean isConfigSpanningTree(DeviceInfType type, int index);
	
	public boolean isEnableSpanningTree(DeviceInfType type, int index);
	
	public boolean isConfigSpanningPathCost(DeviceInfType type, int index);
	
	public int getSpanningPathCost(DeviceInfType type, int index);
	
	public int getSpanningPriority(DeviceInfType type, int index);
	
	public boolean isEnableSpanningEdgePort(DeviceInfType type, int index);
	
	public SpanningTreeBpduProtectionValue getSpanningBpdu(DeviceInfType type, int index);
	
	public int getSpanningMstInstanceSize(DeviceInfType type, int index);
	
	public String getSpanningMstInstanceName(DeviceInfType type, int index, int i);
	
	public int getMstInstancePathCost(DeviceInfType type, int index, int i);
	
	public boolean isConfigMstInstancePathCost(DeviceInfType type, int index, int i);
	
	public int getMstInstancePriority(DeviceInfType type, int index, int i);
	
	public boolean isEnableWanNatPolicy(InterType type);
	
	public boolean isInfWanNatPolicyEnable(DeviceInfType type, int index);
	
	public int getNatPolicyNameForSubNetworkSize();
	
	public int getNatPolicyNameForPortForwardingSize();

	public String getNatPolicyNameForSubNetwork(int index);
	
	public String getNatPolicyNameForPortForwarding(int index);
	
	public int getNatPolicySize();
	
	public String getNatPolicyName(int index);
	
	public boolean isNatPolicyConfigMatch(int index);

	public boolean isNatPolicyConfigVirtualHost(int index);

	public String getNatPolicyMatchInsideValue(int index);

	public String getNatPolicyMatchOutsideValue(int index);
	
	public String getNatPolicyVhostInsideHostValue(int index);

	public String getNatPolicyVhostInsidePortValue(int index);

	public String getNatPolicyVhostOutsidePortValue(int index);

	public String getNatPolicyVhostProtocolValue(int index);
	
	/** end spanning tree */
	
	public boolean isConfigInterfaceManage();
	
	public boolean isEnableManageSnmp();
	
	public boolean isEnableManageSSH();
	
	public boolean isEnableManageTelnet();
	
	public boolean isEnableManagePing();
	
	public short getSsidSupportedUnderDual();
}
