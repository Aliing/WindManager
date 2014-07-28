package com.ah.be.config.create.source.impl.baseImpl;

import java.util.Map;

import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.InterfaceProfileInt;
import com.ah.be.config.create.source.impl.branchRouter.InterfaceBRImpl.DhcpServerInfo;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.xml.be.config.EthDuplex;
import com.ah.xml.be.config.InterfaceFlowControlValue;
import com.ah.xml.be.config.InterfaceRadioFixedAntennaValue;
import com.ah.xml.be.config.InterfaceSwitchportModeValue;
import com.ah.xml.be.config.MgtDnsServerModeValue;
import com.ah.xml.be.config.SpanningTreeBpduProtectionValue;

public class InterfaceProfileBaseImpl implements InterfaceProfileInt {

	@Override
	public String getHiveApGuiName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHiveApName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getApVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigInterManage(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInterShutdown(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInterSpeed(InterType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EthDuplex getInterDuplex(InterType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInterConfigQosClass(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInterQosClassifier(InterType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigInterQosMarker(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInterQosMarker(InterType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMgtIpAndMask() throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgtHive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtBindHive() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgtVlan() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMgtVlanId() throws CreateXMLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigInterMode(InterfaceMode mode, InterType type)
			throws CreateXMLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableWanNat(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInterfacePriority(InterType type) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEnableInterManage(ManageType type, InterType type1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InterfaceWifiModeValue getInterWifiMode(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnableWifiAsClient(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWifiBindSsid(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInterfaceWifiRadioChannel(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInterfaceWifiRadioPower(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInterfaceWifiRadioProfileName(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigureWifiRadioProfile(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigureRadioAntennaExternal(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getRadioAntennaDefault(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigureInterfaceWlan(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInterfaceWlanProfileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getInterfaceWifiSsidSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigWifiSsid(InterfaceWifi wifi, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getWifiSsidName(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigureSsidIp(InterfaceWifi wifi, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isShutDownWifiSsid(InterfaceWifi wifi, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getWifiSsidIp(InterfaceWifi wifi, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnableInterMacLearning(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigIdleTimeout(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInterIdleTimeout(InterType type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInterMacLearningStaticSize(InterType type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getStaticMacAddr(InterType type, int index) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnableMgtDhcp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigMgtIp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMgtNativeVlan() throws CreateXMLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigBridgeUserProfile(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInterAccessUserProfileAttr(InterType type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEnableDhcpFallBack() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDhcpTimeOutt() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigMgtDefaultIpPrefix() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtDefaultIpPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMgtOldDefaultIpPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isHiveAp11n() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigEthx(InterType ethType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigInterBind(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigInterBindAgg0(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigInterBindRed0(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigInterBindRedPrimary(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigInterRed0() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigInterAgg0() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigDhcpAddressOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableDhcpAddressOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigInterStationTraffic(InterType type)
			throws CreateXMLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableInterStationTraffic(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigMgtChild(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMgtChildVlan(MgtType type) throws CreateXMLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMgtChildIp(MgtType type) throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgtChildIpHelper(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMgtChildIpHelperSize(MgtType type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMgtChildIpHelperAddress(MgtType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMgtChildPingEnable(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableMgtChildDhcpServer(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableMgtChildAuthoritative(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMgtChildIpPoolSize(MgtType type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMgtChildIpPoolName(MgtType type, int index)
			throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnableMgtChildArpCheck(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigMgtChildOptionsDefaultGateway(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtChildOptionsDefaultGateway(MgtType type)
			throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMgtDhcpNatSupport(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigMgtChildOptionsLeaseTime(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMgtChildOptionsLeaseTime(MgtType type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigMgtChildOptionsNetMask(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtChildOptionsNetMask(MgtType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMgtChildOptionsHivemanagerSize(MgtType type)
			throws CreateXMLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMgtChildOptionsHivemanager(MgtType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgtChildOptionsDoMain(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtChildOptionsDoMain(MgtType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgtChildOptionsMtu(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMgtChildOptionsMtu(MgtType type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigMgtChildOptionsDns1(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtChildOptionsDns1(MgtType type)
			throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgtChildOptionsDns2(MgtType type) throws CreateXMLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtChildOptionsDns2(MgtType type) throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgtChildOptionsDns3(MgtType type) throws CreateXMLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtChildOptionsDns3(MgtType type) throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgtChildOptionsNtp1(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtChildOptionsNtp1(MgtType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgtChildOptionsNtp2(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtChildOptionsNtp2(MgtType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgtChildOptionsPop3(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtChildOptionsPop3(MgtType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgtChildOptionsSmtp(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtChildOptionsSmtp(MgtType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgtChildOptionsWins1(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtChildOptionsWins1(MgtType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgtChildOptionsWins2(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtChildOptionsWins2(MgtType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgtChildOptionsLogsrv(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtChildOptionsLogsrv(MgtType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMgtChildOptionsCustomSize(MgtType type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMgtChildOptionsCustomName(MgtType type, int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigMgtChildOptionsCustomInteger(MgtType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMgtChildOptionsCustomIntegerValue(MgtType type, int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigMgtChildOptionsCustomIp(MgtType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtChildOptionsCustomIpValue(MgtType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgtChildOptionsCustomString(MgtType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigMgtChildOptionsCustomHex(MgtType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMgtChildOptionsCustomStringValue(MgtType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMgtChildOptionsCustomHexValue(MgtType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigLinkDiscovery() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInterfaceRadioRange(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigRadioFixedAntenna(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InterfaceRadioFixedAntennaValue getRadioFixedAntenna(
			InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnableCCA(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMaxCca(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDefaultCca(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigWifi1() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigWifi0() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWifiShutdown(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigWifiShutdown(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWifiClientModeShutdown(InterfaceWifi wifi) {
		return true;
	}

	@Override
	public boolean isEnableAntennaDiversity(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isHiveAp20() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isHiveAp28() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigEthAllowedVlan(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigAllowedVlanAll(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigAllowedVlanAuto(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getAllowedVlanSize(InterType type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getAllowedVlanStr(InterType type, int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigEthSecurity(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getEthSecurityObjName(InterType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigEthIp(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getEthIp(InterType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigAllowedVlanNum(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigEthNativeVlan(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getEthNativeVlan(InterType type) throws CreateXMLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigEthDhcp(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableEthDhcp(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigMgtDnsServer(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableMgtDnsServer(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MgtDnsServerModeValue getMgtDnsServerMode(MgtType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIntDomainNameSize(MgtType type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getIntDomainName(MgtType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIntDnsServer(MgtType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigDnsIntResolve(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigDnsExtResolve(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getIntResolveDns1(MgtType type) throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIntResolveDns2(MgtType type) throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIntResolveDns3(MgtType type) throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExtResolveDns1(MgtType type) throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExtResolveDns2(MgtType type) throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExtResolveDns3(MgtType type) throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigEthxPse(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getEthxPseMode(InterType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnableEthxShutdown(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Integer getEthxPsePriority(InterType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigMgt0DhcpKeepalive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMgt0DhcpKeepaliveVlanSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMgt0DhcpKeepaliveValn(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigInterfaceUSB() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigMgtX(MgtType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSRInfeSize(DeviceInfType type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInterfaceVlansize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMTUValue() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int getMgt0MTUValue() {
		return 0;
	}

	@Override
	public String getInfPortName(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInfSpeed(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EthDuplex getInfDuplex(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnableInfAutoMdix(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InterfaceFlowControlValue getInfFlowControlValue(DeviceInfType type,
			int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getInfLinkDebounce(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigInfSwitchPort(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InterfaceSwitchportModeValue getInfPortVlanMode(DeviceInfType type,
			int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigSwitchPortAccess(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigSwitchPortTrunk(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getAccessVlan(DeviceInfType type, int index)
			throws CreateXMLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNativeVlan(DeviceInfType type, int index)
			throws CreateXMLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String[] getInfPortAllowedVlan(DeviceInfType type, int index)
			throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getInfPortVoiceVlan(DeviceInfType type, int index)
			throws CreateXMLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigInfPortChannel(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInfPortChannel(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigInfSecurityObject(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInfSecurityObject(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigInfDhcpClient(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableInfDhcpClient(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigInfIp(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInfIp(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInfShutdown(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigInfWAN(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigInfStormControl(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInfWanNatEnable(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigInfQosClassifier(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInfQosClassifierName(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigInfQosMarker(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInfQosMarkerName(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigInfQosShaper(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInfQosShaperValue(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigStormControlAll(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigStormControlBroadcast(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigStormControlMulticast(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigStormControlUnknownUnicast(DeviceInfType type,
			int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigStormControlTcpSyn(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigStormControlPercentage(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getStormControlPercentage(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigStormControlBps(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getStormControlBps(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigStormControlPps(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getStormControlPps(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEnableLldpReceive(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableLldpTransmit(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableCdpReceive(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBindToPortChannel(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigInfPse(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isShutdownInfPse(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigPseProfile(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInfPseProfileName(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigSpanningTree(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableSpanningTree(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigSpanningPathCost(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSpanningPathCost(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSpanningPriority(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEnableSpanningEdgePort(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SpanningTreeBpduProtectionValue getSpanningBpdu(DeviceInfType type,
			int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSpanningMstInstanceSize(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSpanningMstInstanceName(DeviceInfType type, int index,
			int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMstInstancePathCost(DeviceInfType type, int index, int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMstInstancePriority(DeviceInfType type, int index, int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isWifiModeAccess(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWifiModeBackhaul(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWifiModeDual(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isWifiModeSensor(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWifiModeWan(InterfaceWifi wifi) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String isConfigStormControlMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigPortUserProfileId(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getPortUserProfileId(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isInfPortAllowedVlanAll(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MgtType getMgtTypeType(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInfVlanName(MgtType type) throws CreateXMLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getWanInterfacePriority(InterType type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<MgtType, DhcpServerInfo> getMgtSubResourceMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnableWanNatPolicy(InterType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getNatPolicyNameForSubNetworkSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNatPolicyNameForPortForwardingSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getNatPolicyNameForSubNetwork(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNatPolicyNameForPortForwarding(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNatPolicySize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getNatPolicyName(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNatPolicyConfigMatch(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNatPolicyConfigVirtualHost(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getNatPolicyMatchInsideValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNatPolicyMatchOutsideValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNatPolicyVhostInsideHostValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNatPolicyVhostInsidePortValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNatPolicyVhostOutsidePortValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNatPolicyVhostProtocolValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigInterfaceManage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableManageSnmp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableManageSSH() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableManageTelnet() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableManagePing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInfWanNatPolicyEnable(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getWanInterfacePriority(DeviceInfType type,int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigClientReport(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnableClientReport(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConfigInfDescription(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInfDescription(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConfigStormControlKbps(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getStormControlKbps(DeviceInfType type, int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConfigMstInstancePathCost(DeviceInfType type, int index, int i) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public short getSsidSupportedUnderDual() {
		// TODO Auto-generated method stub
		return 0;
	}
}
