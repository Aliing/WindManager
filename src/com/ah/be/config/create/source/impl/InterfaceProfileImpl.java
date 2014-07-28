package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.common.NmsUtil;
import com.ah.be.common.SensorModeUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.InterfaceProfileInt;
import com.ah.be.config.create.source.impl.baseImpl.InterfaceProfileBaseImpl;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApEth;
import com.ah.bo.hiveap.HiveApLearningMac;
import com.ah.bo.hiveap.HiveApSsidAllocation;
import com.ah.bo.hiveap.HiveApWifi;
import com.ah.bo.mobility.HiveProfile;
import com.ah.bo.mobility.QosClassification;
import com.ah.bo.mobility.QosMarking;
import com.ah.bo.network.BonjourGatewaySettings;
import com.ah.bo.network.DhcpServerIpPool;
import com.ah.bo.network.DhcpServerOptionsCustom;
import com.ah.bo.network.DnsServiceProfile;
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.VlanDhcpServer;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.SsidProfile;
import com.ah.util.CountryCode;
import com.ah.util.MgrUtil;
import com.ah.xml.be.config.EthDuplex;
import com.ah.xml.be.config.InterfaceRadioFixedAntennaValue;
import com.ah.xml.be.config.MgtDnsServerModeValue;

/**
 * 
 * @author zhang
 * 
 */

@SuppressWarnings("static-access")
public class InterfaceProfileImpl extends InterfaceProfileBaseImpl {

	protected HiveAp hiveAp;
	private LLDPCDPProfile lldpCdpProfile;

	private Map<InterType, ServiceFilter> ethServiceFilter;
	private List<SsidProfile> templateSsidList;

	private Map<InterType, ConfigTemplateSsid> templateEth;
	private Map<InterType, List<String>> macLearning = new HashMap<InterType, List<String>>();
//	private List<MacOrOui> macLearningStaticList;
	private Map<InterType, HiveApEth> interMap;

	private Map<MgtType, VlanDhcpServer> mgtMap = new HashMap<MgtType, VlanDhcpServer>();
	private Map<MgtType, List<String>> ipHelperMap = new HashMap<MgtType, List<String>>();
	private Map<MgtType, List<PoolIp>> ipPoolMap = new HashMap<MgtType, List<PoolIp>>();
	private Map<MgtType, List<String>> hivemanagerMap = new HashMap<MgtType, List<String>>();
	private Map<MgtType, List<DhcpServerOptionsCustom>> customMap = new HashMap<MgtType, List<DhcpServerOptionsCustom>>();
	
	private Map<MgtType, VpnNetwork> mgtVPNNetworkMap = new HashMap<MgtType, VpnNetwork>();
	private Map<MgtType, List<String[]>> mgtVPNNetworkDomainListMap = new HashMap<MgtType, List<String[]>>();
	
	private Set<String> disableSsidWifi0;
	private Set<String> disableSsidWifi1;
	
	private List<String> mgt0DhcpValns = new ArrayList<String>();

	public InterfaceProfileImpl(HiveAp hiveAp) throws CreateXMLException {
		this.hiveAp = hiveAp;
		lldpCdpProfile = hiveAp.getConfigTemplate().getLldpCdp();
		this.loadmgt0DhcpKeepValns();
		setEthServiceFilter();

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

		List<String> macEth0 = new ArrayList<String>();
		List<String> macEth1 = new ArrayList<String>();
		List<String> macAgg0 = new ArrayList<String>();
		List<String> macRed0 = new ArrayList<String>();
		for(HiveApLearningMac macLn : hiveAp.getLearningMacs()){
			String macAddr = CLICommonFunc.getMacAddressOrOui(macLn.getMac(), this.hiveAp).getMacEntry();
			macAddr = CLICommonFunc.transFormMacAddrOrOui(macAddr);
			if(macLn.getLearningMacType() == HiveApLearningMac.LEARNING_MAC_ETH0){
				macEth0.add(macAddr);
			}else if(macLn.getLearningMacType() == HiveApLearningMac.LEARNING_MAC_ETH1){
				macEth1.add(macAddr);
			}else if(macLn.getLearningMacType() == HiveApLearningMac.LEARNING_MAC_AGG0){
				macAgg0.add(macAddr);
			}else if(macLn.getLearningMacType() == HiveApLearningMac.LEARNING_MAC_RED0){
				macRed0.add(macAddr);
			}
		}
		macLearning.put(InterType.eth0, macEth0);
		macLearning.put(InterType.eth1, macEth1);
		macLearning.put(InterType.agg0, macAgg0);
		macLearning.put(InterType.red0, macRed0);

		interMap = new HashMap<InterType, HiveApEth>();

		interMap.put(InterType.eth0, hiveAp.getEth0());
		if (hiveAp.getEth1() != null) {
			interMap.put(InterType.eth1, hiveAp.getEth1());
		}
		if (hiveAp.getAgg0() != null) {
			interMap.put(InterType.agg0, hiveAp.getAgg0());
		}
		if (hiveAp.getRed0() != null) {
			interMap.put(InterType.red0, hiveAp.getRed0());
		}

		// set mgtMap map;
		Set<VlanDhcpServer> VlanDhcpServerSet = hiveAp.getDhcpServers();
		if (VlanDhcpServerSet != null && !VlanDhcpServerSet.isEmpty()) {
			for (VlanDhcpServer vlanDhcpServerObj : VlanDhcpServerSet) {
				String mgtStr = CLICommonFunc.getEnumItemValue(
						VlanDhcpServer.ENUM_INTERFACE_FOR_OPTION,
						vlanDhcpServerObj.getDhcpMgt());
				if (MgtType.mgt0.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt0, vlanDhcpServerObj);
				} else if (MgtType.mgt01.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt01, vlanDhcpServerObj);
				} else if (MgtType.mgt02.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt02, vlanDhcpServerObj);
				} else if (MgtType.mgt03.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt03, vlanDhcpServerObj);
				} else if (MgtType.mgt04.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt04, vlanDhcpServerObj);
				} else if (MgtType.mgt05.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt05, vlanDhcpServerObj);
				} else if (MgtType.mgt06.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt06, vlanDhcpServerObj);
				} else if (MgtType.mgt07.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt07, vlanDhcpServerObj);
				} else if (MgtType.mgt08.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt08, vlanDhcpServerObj);
				} else if (MgtType.mgt09.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt09, vlanDhcpServerObj);
				} else if (MgtType.mgt010.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt010, vlanDhcpServerObj);
				} else if (MgtType.mgt011.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt011, vlanDhcpServerObj);
				} else if (MgtType.mgt012.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt012, vlanDhcpServerObj);
				} else if (MgtType.mgt013.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt013, vlanDhcpServerObj);
				} else if (MgtType.mgt014.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt014, vlanDhcpServerObj);
				} else if (MgtType.mgt015.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt015, vlanDhcpServerObj);
				} else if (MgtType.mgt016.getValue().equals(mgtStr)) {
					mgtMap.put(MgtType.mgt016, vlanDhcpServerObj);
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
								hiveAp.getWifi0RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA || 
								hiveAp.getWifi0RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC)){
					disableSsidWifi0.add(ssidName);
				}
				if(hiveAp.getWifi1RadioProfile() != null && 
						(hiveAp.getWifi1RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_A || 
								hiveAp.getWifi1RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_NA ||
								hiveAp.getWifi1RadioProfile().getRadioMode() == RadioProfile.RADIO_PROFILE_MODE_AC)){
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

	// public String getUpdateTime(){
	// List<Object> interTimeList = new ArrayList<Object>();
	// interTimeList.add(hiveAp);
	// interTimeList.add(eth0ServiceFilter);
	// interTimeList.add(macLearning);
	// interTimeList.add(templateEth0);
	// if(macLearning != null){
	// interTimeList.add(macLearning.getAttribute());
	// interTimeList.addAll(macLearning.getMacAddress());
	// }
	// interTimeList.add(hiveAp.getConfigTemplate());
	// interTimeList.add(hiveAp.getWifi0RadioProfile());
	// interTimeList.add(hiveAp.getWifi1RadioProfile());
	// if(ssidList != null){
	// for(SsidProfile ssidObj : ssidList){
	// if(ssidObj != null){
	// interTimeList.add(ssidObj.getCwp());
	// }
	// }
	// }
	// if(hiveAp.getConfigTemplate() != null){
	// interTimeList.add(hiveAp.getConfigTemplate().getVlan());
	// }
	// return CLICommonFunc.getLastUpdateTime(interTimeList);
	// }
	//	
	// public String getEthUpdateTime(){
	// List<Object> ehtTime = new ArrayList<Object>();
	// ehtTime.add(hiveAp);
	// ehtTime.add(eth0ServiceFilter);
	// ehtTime.add(macLearning);
	// ehtTime.add(templateEth0);
	// if(macLearning != null){
	// ehtTime.add(macLearning.getAttribute());
	// if(macLearning.getMacAddress() != null){
	// ehtTime.addAll(macLearning.getMacAddress());
	// }
	// }
	// return CLICommonFunc.getLastUpdateTime(ehtTime);
	// }
	//	
	// public String getWifiUpdateTime(){
	// List<Object> wifiTimeList = new ArrayList<Object>();
	// wifiTimeList.add(hiveAp);
	// wifiTimeList.add(hiveAp.getConfigTemplate());
	// wifiTimeList.add(hiveAp.getWifi0RadioProfile());
	// wifiTimeList.add(hiveAp.getWifi1RadioProfile());
	// for(SsidProfile ssidObj : ssidList){
	// if(ssidObj != null){
	// wifiTimeList.add(ssidObj.getCwp());
	// }
	// }
	// return CLICommonFunc.getLastUpdateTime(wifiTimeList);
	// }
	//	
	// public String getMgtUpdateTime(){
	// List<Object> mgtTimeList = new ArrayList<Object>();
	// mgtTimeList.add(hiveAp);
	// mgtTimeList.add(hiveAp.getConfigTemplate());
	// if(hiveAp.getConfigTemplate() != null){
	// mgtTimeList.add(hiveAp.getConfigTemplate().getVlan());
	// }
	// return CLICommonFunc.getLastUpdateTime(mgtTimeList);
	// }

	public boolean isInterShutdown(InterType type) {
		short ehtStatus = interMap.get(type).getAdminState();
		return ehtStatus == AhInterface.ADMIN_STATE_DOWM;
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
		return templateEth.get(type).getInterfaceName();
	}

	public boolean isConfigInterQosMarker(InterType type) {
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
		return templateEth.get(type).getInterfaceName();
	}

	public String getMgtIpAndMask() throws CreateXMLException {
//		return hiveAp.getCfgIpAddress()+ " " + hiveAp.getCfgNetmask();
		return hiveAp.getCfgIpAddress() + "/"
				+ CLICommonFunc.turnNetMaskToNum(hiveAp.getCfgNetmask());
	}

	public boolean isConfigMgtHive() {
		HiveProfile hiveProf = hiveAp.getConfigTemplate().getHiveProfile();
		return hiveProf != null && !hiveProf.getDefaultFlag();
	}

	public String getMgtBindHive() {
		return hiveAp.getConfigTemplate().getHiveProfile().getHiveName();
	}
	
	public boolean isConfigMgtVlan(){
		return true;
	}

	public int getMgtVlanId() throws CreateXMLException {
		int mgtVlan = hiveAp.getMgtVlan();
		if(mgtVlan > 0){
			return mgtVlan;
		}else{
			return CLICommonFunc.getVlan(hiveAp.getConfigTemplate().getVlan(), hiveAp).getVlanId();
		}
		
	}

	private short turnInterfaceMode(InterfaceMode mode) {
		if(mode == InterfaceMode.wan){
			return -1;
		}
		if (mode == InterfaceProfileInt.InterfaceMode.access) {
			return AhInterface.OPERATION_MODE_ACCESS;
		} else if (mode == InterfaceProfileInt.InterfaceMode.bridgeAccess) {
			return AhInterface.OPERATION_MODE_ACCESS;
		} else if (mode == InterfaceProfileInt.InterfaceMode.backhaul) {
			return AhInterface.OPERATION_MODE_BACKHAUL;
		} else if (mode == InterfaceProfileInt.InterfaceMode.bridge8021q) {
			return AhInterface.OPERATION_MODE_BRIDGE;
		} else if (mode == InterfaceProfileInt.InterfaceMode.bridge) {
			return AhInterface.OPERATION_MODE_BRIDGE;
		} else if (mode == InterfaceProfileInt.InterfaceMode.dual){
			return AhInterface.OPERATION_MODE_DUAL;
		} else {
			return AhInterface.OPERATION_MODE_BACKHAUL;
		}
	}

	public boolean isConfigInterMode(InterfaceMode mode, InterType type)
			throws CreateXMLException {

		return interMap.get(type).getOperationMode() == turnInterfaceMode(mode);
	}
	
	public boolean isEnableWanNat(InterType type){
		return false;
	}

	private void setEthServiceFilter() {
		ethServiceFilter = new HashMap<InterType, ServiceFilter>();
		if (hiveAp.getEth0().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS) {
			ethServiceFilter.put(InterType.eth0, hiveAp.getConfigTemplate()
					.getEth0ServiceFilter());
		} else if (hiveAp.getEth0().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL) {
			ethServiceFilter.put(InterType.eth0, hiveAp.getConfigTemplate()
					.getEth0BackServiceFilter());
		} else if (hiveAp.getEth0().getOperationMode() == AhInterface.OPERATION_MODE_BRIDGE) {
			ethServiceFilter.put(InterType.eth0, hiveAp.getConfigTemplate()
					.getEth0ServiceFilter());
		}

		if (this.isHiveAp11n()) {
			if (hiveAp.getEth1().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS) {
				ethServiceFilter.put(InterType.eth1, hiveAp.getConfigTemplate()
						.getEth1ServiceFilter());
			} else if (hiveAp.getEth1().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL) {
				ethServiceFilter.put(InterType.eth1, hiveAp.getConfigTemplate()
						.getEth1BackServiceFilter());
			} else if (hiveAp.getEth1().getOperationMode() == AhInterface.OPERATION_MODE_BRIDGE) {
				ethServiceFilter.put(InterType.eth1, hiveAp.getConfigTemplate()
						.getEth1ServiceFilter());
			}

			if (hiveAp.getAgg0().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS) {
				ethServiceFilter.put(InterType.agg0, hiveAp.getConfigTemplate()
						.getAgg0ServiceFilter());
			} else if (hiveAp.getAgg0().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL) {
				ethServiceFilter.put(InterType.agg0, hiveAp.getConfigTemplate()
						.getAgg0BackServiceFilter());
			} else if (hiveAp.getAgg0().getOperationMode() == AhInterface.OPERATION_MODE_BRIDGE) {
				ethServiceFilter.put(InterType.agg0, hiveAp.getConfigTemplate()
						.getAgg0ServiceFilter());
			}

			if (hiveAp.getRed0().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS) {
				ethServiceFilter.put(InterType.red0, hiveAp.getConfigTemplate()
						.getRed0ServiceFilter());
			} else if (hiveAp.getRed0().getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL) {
				ethServiceFilter.put(InterType.red0, hiveAp.getConfigTemplate()
						.getRed0BackServiceFilter());
			} else if (hiveAp.getRed0().getOperationMode() == AhInterface.OPERATION_MODE_BRIDGE) {
				ethServiceFilter.put(InterType.red0, hiveAp.getConfigTemplate()
						.getRed0ServiceFilter());
			}
		}

	}

	public boolean isConfigInterManage(InterType type) {
		return ethServiceFilter.get(type) != null;
	}

	public boolean isEnableInterManage(ManageType type, InterType type1) {

//		if (this.isConfigInterBind(type1)) {
//			return true;
//		}

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
	
	public InterfaceProfileInt.InterfaceWifiModeValue getInterWifiMode(InterfaceWifi wifi) {
//		short mode = getHiveApWifi(wifi).getOperationMode();
		if(isWifiModeWan(wifi)) {
			return InterfaceWifiModeValue.WAN;
		} else if (isWifiModeDual(wifi)) {
			if(hiveAp.getSoftVer() != null && !"".equals(hiveAp.getSoftVer()) 
					&& NmsUtil.compareSoftwareVersion("4.0.1.0", hiveAp.getSoftVer()) > 0){
				return InterfaceWifiModeValue.ACCESS;
			}
			return InterfaceWifiModeValue.DUAL;
		} else if(isWifiModeBackhaul(wifi)) {
			return InterfaceWifiModeValue.BACKHAUL;
		} else if(isWifiModeAccess(wifi)) {
			return InterfaceWifiModeValue.ACCESS;
		} else {
			return InterfaceWifiModeValue.DUAL;
		}
	}

	protected HiveApWifi getHiveApWifi(InterfaceWifi wifi) {
		if (wifi == InterfaceWifi.wifi0) {
			return hiveAp.getWifi0();
		} else {
			return hiveAp.getWifi1();
		}
	}
	
	public boolean isWifiModeAccess(InterfaceWifi wifi){
		return getHiveApWifi(wifi).getOperationMode() == AhInterface.OPERATION_MODE_ACCESS;
	}
	
	public boolean isWifiModeBackhaul(InterfaceWifi wifi){
		return getHiveApWifi(wifi).getOperationMode() == AhInterface.OPERATION_MODE_BACKHAUL;
	}
	
	public boolean isWifiModeDual(InterfaceWifi wifi){
		return getHiveApWifi(wifi).getOperationMode() == AhInterface.OPERATION_MODE_DUAL;
	}
	
	public boolean isWifiModeSensor(InterfaceWifi wifi){
		return getHiveApWifi(wifi).getOperationMode() == AhInterface.OPERATION_MODE_SENSOR;
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

	// public boolean isConfigureRadioAntenna(InterfaceWifi wifi){
	// return isConfigureRadioAntennaExternal(wifi);
	// }

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

	public boolean isConfigureInterfaceWlan(InterfaceWifi wifi) {
		short operationMode=getHiveApWifi(wifi).getOperationMode();
		if(!SensorModeUtil.isSupportWIPSOnSensor(hiveAp,operationMode)){
			return false;
		}
		//if WIPS is disabled in radio profile page, not generate related CLI
		RadioProfile radioProfile=getWifiRadioProfile(wifi);
		if(!radioProfile.isEnableWips()){
			return false;
		}
		return (operationMode == AhInterface.OPERATION_MODE_ACCESS 
				|| operationMode == AhInterface.OPERATION_MODE_WAN_ACCESS
				|| operationMode == AhInterface.OPERATION_MODE_DUAL
				|| operationMode == AhInterface.OPERATION_MODE_SENSOR)
				&& hiveAp.getConfigTemplate().getIdsPolicy() != null;
	}

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
				if (findBindSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
					return true;
				} else if (findBindSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BG) {
					return getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_BG
							|| getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_NG;
				} else if (findBindSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_A) {
					return getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_A
							|| getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_NA
							|| getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_AC ;
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
						getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_NA ||
						getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_AC) {
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
				getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_NA ||
				getHiveApWifi(wifi).getRadioMode() == AhInterface.RADIO_MODE_AC) {
			ip = templateSsidList.get(index).getCwp().getIpForAMode();
			mask = templateSsidList.get(index).getCwp().getMaskForAMode();
		} else {
			ip = templateSsidList.get(index).getCwp().getIpForBGMode();
			mask = templateSsidList.get(index).getCwp().getMaskForBGMode();
		}
		return ip + "/" + CLICommonFunc.turnNetMaskToNum(mask);
	}
	
	private HiveApEth getHiveApEthType(InterType type){
		if(InterType.eth0 == type){
			return this.hiveAp.getEth0();
		}else if(InterType.eth1 == type){
			return this.hiveAp.getEth1();
		}else if(InterType.agg0 == type){
			return this.hiveAp.getAgg0();
		}else if(InterType.red0 == type){
			return this.hiveAp.getRed0();
		}else{
			return null;
		}
	}

//	public boolean isConfigInterMacLearning(InterType type) {
//		return this.getHiveApEthType(type).getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL;
////			!this.getHiveApEthType(type).isUseDefaultSettings();
////		return this.getHiveApEthType(type).isMacLearningEnabled();
////		return macLearning.get(type) != null;
//		// if(CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())){
//		// return false;
//		// }else
//		// if(CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())){
//		// return macLearning.get(type) != null;
//		// }else{
//		// return false;
//		// }
//	}

	public boolean isEnableInterMacLearning(InterType type) {
		return this.getHiveApEthType(type).getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL;
//		return this.getHiveApEthType(type).isMacLearningEnabled();
//		return macLearning.get(type).isMacLearning();
	}
	
	public boolean isConfigIdleTimeout(InterType type){
		return this.getHiveApEthType(type) != null;
	}

//	public boolean isConfigInterIdleTimeout(InterType type) {
//		return isEnableInterMacLearning(type)
//				&& this.getHiveApEthType(type).isEnableIdle();
//	}

	public int getInterIdleTimeout(InterType type) {
		return this.getHiveApEthType(type).getIdelTimeout();
	}

	public int getInterMacLearningStaticSize(InterType type) {
		return macLearning.get(type).size();
	}

	public String getStaticMacAddr(InterType type, int index) throws Exception {
		return macLearning.get(type).get(index);
	}

	public boolean isEnableMgtDhcp() {
		return hiveAp.isDhcp();
	}

	public boolean isConfigMgtIp() {
		return (!hiveAp.isDhcp() && hiveAp.getCfgIpAddress() != null && !""
				.equals(hiveAp.getCfgIpAddress()))
				|| (isEnableDhcpFallBack() && hiveAp.getCfgIpAddress() != null && !""
						.equals(hiveAp.getCfgIpAddress()));
	}

	public int getMgtNativeVlan() throws CreateXMLException {
		int nativeVlan = hiveAp.getNativeVlan();
		if(nativeVlan > 0){
			return nativeVlan;
		}else{
			return CLICommonFunc.getVlan(hiveAp.getConfigTemplate().getVlanNative(), hiveAp).getVlanId();
		}
	}
	
	public boolean isConfigBridgeUserProfile(InterType type){
		UserProfile userProfileObj = null;
		if(InterType.eth0 == type){
			userProfileObj = this.hiveAp.getEth0UserProfile();
		}else if(InterType.eth1 == type){
			userProfileObj = this.hiveAp.getEth1UserProfile();
		}else if(InterType.agg0 == type){
			userProfileObj = this.hiveAp.getAgg0UserProfile();
		}else if(InterType.red0 == type){
			userProfileObj = this.hiveAp.getRed0UserProfile();
		}
//		return this.getHiveApEthType(type).isMacLearningEnabled() && userProfileObj != null;
		return userProfileObj != null;
	}

	public int getInterAccessUserProfileAttr(InterType type) {
		if(isConfigBridgeUserProfile(type)){
			UserProfile userProfileObj = null;
			if(InterType.eth0 == type){
				userProfileObj = this.hiveAp.getEth0UserProfile();
			}else if(InterType.eth1 == type){
				userProfileObj = this.hiveAp.getEth1UserProfile();
			}else if(InterType.agg0 == type){
				userProfileObj = this.hiveAp.getAgg0UserProfile();
			}else if(InterType.red0 == type){
				userProfileObj = this.hiveAp.getRed0UserProfile();
			}
			return userProfileObj.getAttributeValue();
		}else{
			return 0;
		}
	}

	// public boolean isConfigInterModeBridge(InterType type) {
	// if(CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())){
	// return true;
	// }else if(CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())){
	// return false;
	// }else{
	// return false;
	// }
	// }

	public boolean isEnableDhcpFallBack() {
		return hiveAp.isDhcp() && hiveAp.isDhcpFallback();
	}

	public int getDhcpTimeOutt() {
		return hiveAp.getDhcpTimeout();
	}

	public boolean isConfigMgtDefaultIpPrefix() {
		String ipAddr = hiveAp.getCfgIpAddress();
		return hiveAp.isDhcp() && !hiveAp.isDhcpFallback() && ipAddr != null
				&& !"".equals(ipAddr) && !"0.0.0.0".equals(ipAddr);
		// if(CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(this.getApVersion())){
		// return false;
		// }else if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_11N){
		// return false;
		// }else{
		// String ipAddr = hiveAp.getCfgIpAddress();
		// return hiveAp.isDhcp() && !hiveAp.isDhcpFallback() &&
		// ipAddr != null && !"".equals(ipAddr) && !"0.0.0.0".equals(ipAddr);
		// }
	}

	public String getMgtDefaultIpPrefix() {
		String netMask = hiveAp.getCfgNetmask();
		int sNetMask;
		if(netMask != null && !"".equals(netMask)){
			sNetMask = CLICommonFunc.turnNetMaskToNum(netMask);
		}else{
			sNetMask = 16;
		}
		String ipAddr = CLICommonFunc.getStartIpAddress(hiveAp.getCfgIpAddress(), netMask);
		return ipAddr + "/" + sNetMask;
	}
	
	public String getMgtOldDefaultIpPrefix(){
		return NmsUtil.getIpPrefix(hiveAp.getCfgIpAddress());
	}

	public boolean isHiveAp11n() {
		return hiveAp.is11nHiveAP();
	}
	
	public boolean isConfigEthx(InterType ethType){
		if(ethType == InterType.eth0){
			return true;
		}else if(ethType == InterType.eth1){
			return hiveAp.getDeviceInfo().getIntegerValue(DeviceInfo.SPT_ETHERNET_COUNTS) > 1;
		}else if(ethType == InterType.eth2){
			return false;
		}else if(ethType == InterType.eth3){
			return false;
		}else if(ethType == InterType.eth4){
			return false;
		}else{
			return true;
		}
	}

	public boolean isConfigInterBind(InterType type) {
		return isHiveAp11n()
				&& (interMap.get(type).getBindInterface() == AhInterface.ETH_BIND_IF_RED0 || interMap
						.get(type).getBindInterface() == AhInterface.ETH_BIND_IF_AGG0);
	}

	public boolean isConfigInterBindAgg0(InterType type) {
		return interMap.get(type).getBindInterface() == AhInterface.ETH_BIND_IF_AGG0;
	}

	public boolean isConfigInterBindRed0(InterType type) {
		return interMap.get(type).getBindInterface() == AhInterface.ETH_BIND_IF_RED0;
	}

	public boolean isConfigInterBindRedPrimary(InterType type) {
		return interMap.get(type).getBindRole() == AhInterface.ETH_BIND_ROLE_PRI;
	}

	public boolean isConfigInterRed0() {
		return isHiveAp11n()
				&& (interMap.get(InterType.eth0).getBindInterface() == AhInterface.ETH_BIND_IF_RED0 || interMap
						.get(InterType.eth1).getBindInterface() == AhInterface.ETH_BIND_IF_RED0);
	}

	public boolean isConfigInterAgg0() {
		return isHiveAp11n()
				&& (interMap.get(InterType.eth0).getBindInterface() == AhInterface.ETH_BIND_IF_AGG0 || interMap
						.get(InterType.eth1).getBindInterface() == AhInterface.ETH_BIND_IF_AGG0);
	}

	public boolean isConfigDhcpAddressOnly() {
		return hiveAp.isDhcp();
		// if(CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(this.getApVersion())){
		// return false;
		// }else if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_11N){
		// return false;
		// }else{
		// return hiveAp.isDhcp();
		// }
	}

	public boolean isEnableDhcpAddressOnly() {
		return hiveAp.isAddressOnly();
	}

	public boolean isConfigInterStationTraffic(InterType type) throws CreateXMLException {
		return ethServiceFilter.get(type) != null && 
			!this.isConfigInterMode(InterfaceMode.backhaul, type);
	}

	public boolean isEnableInterStationTraffic(InterType type) {
		return ethServiceFilter.get(type).getInterTraffic();
	}

	public boolean isConfigMgtChild(MgtType type) {
		return mgtMap.get(type) != null;
	}

	public int getMgtChildVlan(MgtType type) throws CreateXMLException  {
		return mgtMap.get(type).getInterVlan();
	}

	public String getMgtChildIp(MgtType type) throws CreateXMLException {
		return mgtMap.get(type).getInterfaceIp()
				+ "/"
				+ CLICommonFunc.turnNetMaskToNum(mgtMap.get(type)
						.getInterfaceNet());
	}

	public boolean isConfigMgtChildIpHelper(MgtType type) {
		return mgtMap.get(type).getTypeFlag() == VlanDhcpServer.ENABLE_DHCP_RELAY;
	}

	public int getMgtChildIpHelperSize(MgtType type) {
		if (ipHelperMap.get(type) == null) {
			List<String> ipList = new ArrayList<String>();
			String ipHelper1, ipHelper2;
			ipHelper1 = mgtMap.get(type).getIpHelper1();
			ipHelper2 = mgtMap.get(type).getIpHelper2();
			if (ipHelper1 != null && !"".equals(ipHelper1)) {
				ipList.add(ipHelper1);
			}
			if (ipHelper2 != null && !"".equals(ipHelper2)) {
				ipList.add(ipHelper2);
			}
			ipHelperMap.put(type, ipList);
		}
		return ipHelperMap.get(type).size();
	}

	public String getMgtChildIpHelperAddress(MgtType type, int index) {
		return ipHelperMap.get(type).get(index);
	}

	public boolean isMgtChildPingEnable(MgtType type) {
		return mgtMap.get(type).isEnablePing();
	}

	public boolean isEnableMgtChildDhcpServer(MgtType type) {
		return mgtMap.get(type).getTypeFlag() == VlanDhcpServer.ENABLE_DHCP_SERVER;
	}

	public boolean isEnableMgtChildAuthoritative(MgtType type) {
		return mgtMap.get(type).isAuthoritative();
	}

	
	public int getMgtChildIpPoolSize(MgtType type) {
		if (ipPoolMap.get(type) == null) {
			List<DhcpServerIpPool> ipPools = mgtMap.get(type).getIpPools();
			List<PoolIp> ipPoolsCli = new ArrayList<PoolIp>();
			for(DhcpServerIpPool pool : ipPools){
				long startIpIndex = CLICommonFunc.getIpIndex(pool.getStartIp());
				long endIpIndex = CLICommonFunc.getIpIndex(pool.getEndIp());
				PoolIp ipPoolObj = new PoolIp();
				ipPoolObj.setStartIp(pool.getStartIp());
				ipPoolObj.setEndIp(pool.getEndIp());
				ipPoolObj.setStartIpIndex(startIpIndex);
				ipPoolObj.setEndIpIndex(endIpIndex);
				ipPoolsCli.add(ipPoolObj);
			}
//			if (ipPools != null) {
//				ipPoolMap.put(type, mergeIpArea(ipPoolsCli));
//			} else {
//				ipPoolMap.put(type, new ArrayList<PoolIp>());
//			}
			ipPoolMap.put(type, mergeIpArea(ipPoolsCli));
		}
		return ipPoolMap.get(type).size();
	}
	
	private List<PoolIp> mergeIpArea(List<PoolIp> ipList){
		List<PoolIp> ipPoolMerged = new ArrayList<PoolIp>();
		for(PoolIp ipPoolS : ipList){
			long startS = ipPoolS.getStartIpIndex();
			long endS = ipPoolS.getEndIpIndex();
			boolean isCross = false;
			List<PoolIp> tempList = new ArrayList<PoolIp>();
			for(PoolIp ipPoolR : ipPoolMerged){
				long startR = ipPoolR.getStartIpIndex();
				long endR = ipPoolR.getEndIpIndex();
				if(endS < startR - 1){
					//null
				}else if(endS >= startR - 1 && endS <= endR + 1){
					tempList.add(ipPoolR);
					isCross = true;
				}else if(endS > endR + 1 && startS <= endR + 1){
					tempList.add(ipPoolR);
					isCross = true;
				}else if(startS > endR + 1){
					//null
				}
			}
			
			//if this area don't cross to any exist area, add to list
			if(!isCross){
				ipPoolMerged.add(ipPoolS);
			}else{
				//if this area cross to many, account max area and remove old area
				long minIpIndex = ipPoolS.getStartIpIndex(), maxIpIndex = ipPoolS.getEndIpIndex();
				String minIpStr = ipPoolS.getStartIp(), maxIpStr = ipPoolS.getEndIp();
				for(PoolIp tempIp : tempList){
					if(tempIp.getStartIpIndex() < minIpIndex){
						minIpIndex = tempIp.getStartIpIndex();
						minIpStr = tempIp.getStartIp();
					}
					if(tempIp.getEndIpIndex() > maxIpIndex){
						maxIpIndex = tempIp.getEndIpIndex();
						maxIpStr = tempIp.getEndIp();
					}
					ipPoolMerged.remove(tempIp);
				}
				tempList.clear();
				PoolIp ipPoolObj = new PoolIp();
				ipPoolObj.setStartIp(minIpStr);
				ipPoolObj.setStartIpIndex(minIpIndex);
				ipPoolObj.setEndIp(maxIpStr);
				ipPoolObj.setEndIpIndex(maxIpIndex);
				ipPoolMerged.add(ipPoolObj);
			}
		}
		
		return ipPoolMerged;
	}
	
	private static class PoolIp{
		
		private String startIp;
		
		private String endIp;
		
		private long startIpIndex;
		
		private long endIpIndex;
		
		public String getStartIp(){
			return this.startIp;
		}
		
		public void setStartIp(String startIp){
			this.startIp = startIp;
		}
		
		public String getEndIp(){
			return this.endIp;
		}
		
		public void setEndIp(String endIp){
			this.endIp = endIp;
		}
		
		public long getStartIpIndex(){
			return this.startIpIndex;
		}
		
		public void setStartIpIndex(long startIpIndex){
			this.startIpIndex = startIpIndex;
		}
		
		public long getEndIpIndex(){
			return this.endIpIndex;
		}
		
		public void setEndIpIndex(long endIpIndex){
			this.endIpIndex = endIpIndex;
		}
	}

	public String getMgtChildIpPoolName(MgtType type, int index) throws CreateXMLException {
		List<PoolIp> ipPools = ipPoolMap.get(type);
		return ipPools.get(index).getStartIp() + " "
				+ ipPools.get(index).getEndIp();
	}

	public boolean isEnableMgtChildArpCheck(MgtType type) {
		return mgtMap.get(type).isEnableArp();
	}

	public boolean isConfigMgtChildOptionsDefaultGateway(MgtType type) {
		String defGateway = mgtMap.get(type).getDefaultGateway();
		return defGateway != null && !"".equals(defGateway);
	}

	public String getMgtChildOptionsDefaultGateway(MgtType type) throws CreateXMLException {
		return mgtMap.get(type).getDefaultGateway();
	}
	
	public boolean isMgtDhcpNatSupport(MgtType type){
		return mgtMap.get(type).isNatSupport();
	}

	public boolean isConfigMgtChildOptionsLeaseTime(MgtType type) {
		String leaseTime = mgtMap.get(type).getLeaseTime();
		return leaseTime != null && !"".equals(leaseTime);
	}

	public int getMgtChildOptionsLeaseTime(MgtType type) {
		return Integer.valueOf(mgtMap.get(type).getLeaseTime());
	}

	public boolean isConfigMgtChildOptionsNetMask(MgtType type) {
		String netMaskStr = mgtMap.get(type).getDhcpNetmask();
		return netMaskStr != null && !"".equals(netMaskStr);
	}

	public String getMgtChildOptionsNetMask(MgtType type) {
		return mgtMap.get(type).getDhcpNetmask();
	}

	public int getMgtChildOptionsHivemanagerSize(MgtType type) throws CreateXMLException {
		if (hivemanagerMap.get(type) == null) {
			List<String> hmStrList = new ArrayList<String>();
			List<DhcpServerOptionsCustom> dhcpCustoms = mgtMap.get(type).getCustoms();
			if(dhcpCustoms != null){
				for(DhcpServerOptionsCustom dhcpCustom : dhcpCustoms){
					/** HiveManager Name = 225, HiveManager IP = 226 */
					if(dhcpCustom.getNumber() == 225 || dhcpCustom.getNumber() == 226){
						hmStrList.add(dhcpCustom.getValue(hiveAp.getSoftVer()));
					}
				}
			}
			hivemanagerMap.put(type, hmStrList);
//			if(mgtMap.get(type).getHiveManagerServer() != null){
//				SingleTableItem hostIp = CLICommonFunc.getIpAddress(mgtMap.get(type).getHiveManagerServer(), hiveAp);
//				String hmIp = hostIp.getIpAddress();
//				if (hmIp != null && !"".equals(hmIp)) {
//					hmStrList.add(hmIp);
//				}
//			}
//			hivemanagerMap.put(type, hmStrList);
		}
		return hivemanagerMap.get(type).size();
	}

	public String getMgtChildOptionsHivemanager(MgtType type, int index) {
		return hivemanagerMap.get(type).get(index);
	}

	public boolean isConfigMgtChildOptionsDoMain(MgtType type) {
		String domainStr = mgtMap.get(type).getDomainName();
		return domainStr != null && !"".equals(domainStr);
	}

	public String getMgtChildOptionsDoMain(MgtType type) {
		return mgtMap.get(type).getDomainName();
	}

	public boolean isConfigMgtChildOptionsMtu(MgtType type) {
		String mtuStr = mgtMap.get(type).getMtu();
		return mtuStr != null && !"".equals(mtuStr);
	}

	public int getMgtChildOptionsMtu(MgtType type) {
		return Integer.valueOf(mgtMap.get(type).getMtu());
	}

	public boolean isConfigMgtChildOptionsDns1(MgtType type) {
		String dns1Str = mgtMap.get(type).getDnsServer1();
		return dns1Str != null && !"".equals(dns1Str);
	}

	public String getMgtChildOptionsDns1(MgtType type) throws CreateXMLException {
		return mgtMap.get(type).getDnsServer1();
	}

	public boolean isConfigMgtChildOptionsDns2(MgtType type) throws CreateXMLException {
		String dns2Str = mgtMap.get(type).getDnsServer2();
		return dns2Str != null && !"".equals(dns2Str);
	}

	public String getMgtChildOptionsDns2(MgtType type) throws CreateXMLException {
		return mgtMap.get(type).getDnsServer2();
	}

	public boolean isConfigMgtChildOptionsDns3(MgtType type) throws CreateXMLException {
		String dns3Str = mgtMap.get(type).getDnsServer3();
		return dns3Str != null && !"".equals(dns3Str);
	}

	public String getMgtChildOptionsDns3(MgtType type) throws CreateXMLException {
		return mgtMap.get(type).getDnsServer3();
	}

	public boolean isConfigMgtChildOptionsNtp1(MgtType type) {
		String ntp1Str = mgtMap.get(type).getNtpServer1();
		return ntp1Str != null && !"".equals(ntp1Str);
	}

	public String getMgtChildOptionsNtp1(MgtType type) {
		return mgtMap.get(type).getNtpServer1();
	}

	public boolean isConfigMgtChildOptionsNtp2(MgtType type) {
		String ntp2Str = mgtMap.get(type).getNtpServer2();
		return ntp2Str != null && !"".equals(ntp2Str);
	}

	public String getMgtChildOptionsNtp2(MgtType type) {
		return mgtMap.get(type).getNtpServer2();
	}

	public boolean isConfigMgtChildOptionsPop3(MgtType type) {
		String pop3Str = mgtMap.get(type).getPop3();
		return pop3Str != null && !"".equals(pop3Str);
	}

	public String getMgtChildOptionsPop3(MgtType type) {
		return mgtMap.get(type).getPop3();
	}

	public boolean isConfigMgtChildOptionsSmtp(MgtType type) {
		String smtpStr = mgtMap.get(type).getSmtp();
		return smtpStr != null && !"".equals(smtpStr);
	}

	public String getMgtChildOptionsSmtp(MgtType type) {
		return mgtMap.get(type).getSmtp();
	}

	public boolean isConfigMgtChildOptionsWins1(MgtType type) {
		String winsStr1 = mgtMap.get(type).getWins1();
		return winsStr1 != null && !"".equals(winsStr1);
	}

	public String getMgtChildOptionsWins1(MgtType type) {
		return mgtMap.get(type).getWins1();
	}
	
	public boolean isConfigMgtChildOptionsWins2(MgtType type) {
		String winsStr2 = mgtMap.get(type).getWins2();
		return winsStr2 != null && !"".equals(winsStr2);
	}

	public String getMgtChildOptionsWins2(MgtType type) {
		return mgtMap.get(type).getWins2();
	}

	public boolean isConfigMgtChildOptionsLogsrv(MgtType type) {
		String logsrvStr = mgtMap.get(type).getLogsrv();
		return logsrvStr != null && !"".equals(logsrvStr);
	}

	public String getMgtChildOptionsLogsrv(MgtType type) {
		return mgtMap.get(type).getLogsrv();
	}

	public int getMgtChildOptionsCustomSize(MgtType type) {
		if (customMap.get(type) == null) {
			List<DhcpServerOptionsCustom> dhcpCustomers = mgtMap.get(type).getCustoms();
			if (dhcpCustomers != null) {
				List<DhcpServerOptionsCustom> simpleCustomers = new ArrayList<DhcpServerOptionsCustom>();
				for(DhcpServerOptionsCustom custom : dhcpCustomers){
					/** HiveManager Name = 225, HiveManager IP = 226 */
					if(custom.getNumber() != 225 && custom.getNumber() != 226){
						simpleCustomers.add(custom);
					}
				}
				customMap.put(type, simpleCustomers);
			} else {
				customMap.put(type, new ArrayList<DhcpServerOptionsCustom>());
			}
		}
		return customMap.get(type).size();
	}

	public int getMgtChildOptionsCustomName(MgtType type, int index) {
		return customMap.get(type).get(index).getNumber();
	}

	public boolean isConfigMgtChildOptionsCustomInteger(MgtType type, int index) {
		return customMap.get(type).get(index).getType() == DhcpServerOptionsCustom.CUSTOM_TYPE_INTEGER;
	}

	public int getMgtChildOptionsCustomIntegerValue(MgtType type, int index) {
		return Integer.valueOf(customMap.get(type).get(index).getValue(hiveAp.getSoftVer()));
	}

	public boolean isConfigMgtChildOptionsCustomIp(MgtType type, int index) {
		return customMap.get(type).get(index).getType() == DhcpServerOptionsCustom.CUSTOM_TYYPE_IP;
	}

	public String getMgtChildOptionsCustomIpValue(MgtType type, int index) {
		return customMap.get(type).get(index).getValue(hiveAp.getSoftVer());
	}

	public boolean isConfigMgtChildOptionsCustomString(MgtType type, int index) {
		return customMap.get(type).get(index).getType() == DhcpServerOptionsCustom.CUSTOM_TYYPE_STRING;
	}
	
	public boolean isConfigMgtChildOptionsCustomHex(MgtType type, int index){
		return customMap.get(type).get(index).getType() == DhcpServerOptionsCustom.CUSTOM_TYYPE_HEX;
	}

	public String getMgtChildOptionsCustomStringValue(MgtType type, int index) {
		return customMap.get(type).get(index).getValue(hiveAp.getSoftVer());
	}
	
	public String getMgtChildOptionsCustomHexValue(MgtType type, int index){
		String resultStr = customMap.get(type).get(index).getValue(hiveAp.getSoftVer());
		return resultStr == null ? resultStr : resultStr.toUpperCase();
	}
	
//	public boolean isEnableInterLldp(InterType type){
//		if(lldpCdpProfile == null){
//			return false;
//		}
//		
//		if(!lldpCdpProfile.isEnableLLDP()){
//			return false;
//		}
//		
////		if(!lldpCdpProfile.isEnableLLDPEtherInterface()){
////			return false;
////		}
//		
//		switch(type){
//			case eth0 : 
//				return lldpCdpProfile.isLldpEth0();
//			case eth1 :
//				return lldpCdpProfile.isLldpEth1();
//			case red0 :
//				return lldpCdpProfile.isLldpRed0();
//			case agg0 :
//				return lldpCdpProfile.isLldpAgg0();
//			default :
//				return false;
//		}
//	}
//	
//	public boolean isEnableInterCdp(InterType type){
//		if(lldpCdpProfile == null){
//			return false;
//		}
//		
//		if(!lldpCdpProfile.isEnableCDP()){
//			return false;
//		}
//		
////		if(!lldpCdpProfile.isEnableCDPEtherInterface()){
////			return false;
////		}
//		
//		switch(type){
//			case eth0 : 
//				return lldpCdpProfile.isCdpEth0();
//			case eth1 :
//				return lldpCdpProfile.isCdpEth1();
//			case red0 :
//				return lldpCdpProfile.isCdpRed0();
//			case agg0 :
//				return lldpCdpProfile.isCdpAgg0();
//			default :
//				return false;
//		}
//	}
//	
//	public boolean isEnableWifiLldp(InterfaceWifi type){
//		if(lldpCdpProfile == null){
//			return false;
//		}
//		
//		if(!lldpCdpProfile.isEnableLLDP()){
//			return false;
//		}
//		
////		if(!lldpCdpProfile.isEnableLLDPMeshInterface()){
////			return false;
////		}
//		
//		switch(type){
//			case wifi0 :
//				return lldpCdpProfile.isLldpWifi0();
//			case wifi1 :
//				return lldpCdpProfile.isLldpWifi1();
//			default :
//				return false;
//		}
//	}
//	
//	public boolean isEnableWifiCdp(InterfaceWifi type){
//		if(lldpCdpProfile == null){
//			return false;
//		}
//		
//		if(!lldpCdpProfile.isEnableCDP()){
//			return false;
//		}
//		
////		if(!lldpCdpProfile.isEnableCDPMeshInterface()){
////			return false;
////		}
//		
//		switch(type){
//			case wifi0 :
//				return lldpCdpProfile.isCdpWifi0();
//			case wifi1 :
//				return lldpCdpProfile.isCdpWifi1();
//			default :
//				return false;
//		}
//	}
	
	public boolean isConfigLinkDiscovery(){
		return lldpCdpProfile != null;
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
		String allowedVlan = interMap.get(type).getAllowedVlan();
		return allowedVlan != null && !"".equals(allowedVlan);
	}
	
	public boolean isConfigAllowedVlanAll(InterType type){
		String allowedVlan = interMap.get(type).getAllowedVlan();
		return allowedVlan != null && HiveApEth.ALLOWED_VLAN_ALL.equalsIgnoreCase(allowedVlan.trim());
	}
	
	public boolean isConfigAllowedVlanAuto(InterType type){
		String allowedVlan = interMap.get(type).getAllowedVlan();
		allowedVlan = allowedVlan.toLowerCase();
		return allowedVlan != null && allowedVlan.contains(HiveApEth.ALLOWED_VLAN_AUTO);
	}
	
	public boolean isConfigAllowedVlanNum(InterType type){
		String allowedVlan = interMap.get(type).getAllowedVlan();
		allowedVlan = allowedVlan.toLowerCase();
		return allowedVlan != null && !HiveApEth.ALLOWED_VLAN_ALL.equals(allowedVlan.trim()) &&
			!HiveApEth.ALLOWED_VLAN_AUTO.equals(allowedVlan.trim());
	}
	
	private List<String> eth0AllowedVlan;
	private List<String> eth1AllowedVlan;
	private List<String> red0AllowedVlan;
	private List<String> agg0AllowedVlan;
	
	private List<String> getAllowedVlanArg(InterType type){
		if(type == InterType.eth0){
			return eth0AllowedVlan;
		}else if(type == InterType.eth1){
			return eth1AllowedVlan;
		}else if(type == InterType.red0){
			return red0AllowedVlan;
		}else{
			return agg0AllowedVlan;
		}
	}
	
	public int getAllowedVlanSize(InterType type) {
		if(getAllowedVlanArg(type) == null){
			String vlanStr = interMap.get(type).getAllowedVlan();
			if(vlanStr != null && !"".equals(vlanStr)){
				vlanStr = vlanStr.toLowerCase();
				vlanStr = vlanStr.replace(HiveApEth.ALLOWED_VLAN_AUTO, "");
				vlanStr = vlanStr.replace(",,", ",");
				if(vlanStr.startsWith(","))
					vlanStr = vlanStr.substring(1);
				if(vlanStr.endsWith(","))
					vlanStr = vlanStr.substring(0, vlanStr.length()-1);
				vlanStr = CLICommonFunc.mergeRange(vlanStr);
			}
			if(eth0AllowedVlan == null && type == InterType.eth0){
				eth0AllowedVlan = CLICommonFunc.mergeRangeList(vlanStr);
			}
			if(eth1AllowedVlan == null && type == InterType.eth1){
				eth1AllowedVlan = CLICommonFunc.mergeRangeList(vlanStr);
			}
			if(red0AllowedVlan == null && type == InterType.red0){
				red0AllowedVlan = CLICommonFunc.mergeRangeList(vlanStr);
			}
			if(agg0AllowedVlan == null && type == InterType.agg0){
				agg0AllowedVlan = CLICommonFunc.mergeRangeList(vlanStr);
			}
		}
		
		return getAllowedVlanArg(type).size();
	}
	
	public String getAllowedVlanStr(InterType type, int i){
		return getAllowedVlanArg(type).get(i);
	}
	
	public boolean isConfigEthSecurity(InterType type){
		if(this.hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
			if(type == InterType.eth0){
				return hiveAp.getEthConfigType() == HiveAp.USE_ETHERNET_BOTH 
					&& hiveAp.getEth0().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS 
					&& (hiveAp.isEthCwpEnableEthCwp() || hiveAp.isEthCwpEnableMacAuth() || hiveAp.isEnableMDM());
			}else if(type == InterType.eth1){
				return hiveAp.getEthConfigType() == HiveAp.USE_ETHERNET_BOTH 
					&& hiveAp.getEth1().getOperationMode() == AhInterface.OPERATION_MODE_ACCESS 
					&& (hiveAp.isEthCwpEnableEthCwp() || hiveAp.isEthCwpEnableMacAuth() || hiveAp.isEnableMDM());
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public String getEthSecurityObjName(InterType type){
		if(type == InterType.eth0 || type == InterType.eth1){
			return hiveAp.getMacAddress() + SecurityObjectProfileImpl.SEC_SUFFIX;
		}else{
			return null;
		}
	}
	
	public boolean isConfigEthIp(InterType type){
		boolean result = false;
		Cwp cwpObj = null;
		if(hiveAp.isEthCwpEnableEthCwp()){
			cwpObj = hiveAp.getEthCwpCwpProfile();
		}
		if (cwpObj == null) {
			result = false;
		} else {
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
		}
		return result;
	}
	
	public String getEthIp(InterType type){
		String ip, mask;
		Cwp cwpObj = null;
		if(hiveAp.isEthCwpEnableEthCwp()){
			cwpObj = hiveAp.getEthCwpCwpProfile();
		}
		if (type == InterType.eth0) {
			ip = cwpObj.getIpForEth0();
			mask = cwpObj.getMaskForEth0();
		} else {
			ip = cwpObj.getIpForEth1();
			mask = cwpObj.getMaskForEth1();
		}
		return ip + "/" + CLICommonFunc.turnNetMaskToNum(mask);
	}
	
	public boolean isConfigEthNativeVlan(InterType type){
		if(interMap.get(type) != null && interMap.get(type).getOperationMode() != AhInterface.OPERATION_MODE_ACCESS){
			Integer nativeVlan = interMap.get(type).getMultiNativeVlan();
			if(nativeVlan == null){
				return false;
			}else{
				return nativeVlan.intValue() > 0;
			}
		}else{
			return false;
		}
	}
	
	public int getEthNativeVlan(InterType type) throws CreateXMLException{
		return interMap.get(type).getMultiNativeVlan();
	}
	
	public boolean isConfigEthDhcp(InterType type){
		if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
			return false;
		}else if(hiveAp.isBranchRouter()){
			if(type == InterType.eth0){
				return true;
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
		if(type == InterType.eth0){
			return hiveAp.getEth0Interface().isEnableDhcp();
		}else if(type == InterType.eth1){
			return hiveAp.getEth1Interface().isEnableDhcp();
		}else{
			return true;
		}
	}
	
	public boolean isConfigMgtDnsServer(InterfaceProfileInt.MgtType type){
		return mgtVPNNetworkMap.get(type) != null && mgtVPNNetworkMap.get(type).getVpnDnsService() != null;
	}
	
	public boolean isEnableMgtDnsServer(InterfaceProfileInt.MgtType type){
		return mgtVPNNetworkMap.get(type).getVpnDnsService() != null;
	}
	
	public MgtDnsServerModeValue getMgtDnsServerMode(InterfaceProfileInt.MgtType type){
		if(mgtVPNNetworkMap.get(type).getVpnDnsService().getServiceType() == DnsServiceProfile.SEPARATE_DNS){
			return MgtDnsServerModeValue.SPLIT;
		}else{
			if(mgtVPNNetworkMap.get(type).getVpnDnsService().getExternalServerType() == DnsServiceProfile.LOCAL_DNS_TYPE){
				return MgtDnsServerModeValue.SPLIT;
			}else{
				return MgtDnsServerModeValue.NONSPLIT;
			}
		}
	}
	
	public int getIntDomainNameSize(InterfaceProfileInt.MgtType type){
		return mgtVPNNetworkDomainListMap.get(type).size();
	}
	
	public String getIntDomainName(InterfaceProfileInt.MgtType type, int index){
		return mgtVPNNetworkDomainListMap.get(type).get(index)[0];
	}
	
	public String getIntDnsServer(InterfaceProfileInt.MgtType type, int index){
		return mgtVPNNetworkDomainListMap.get(type).get(index)[1];
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
	
	public int getEthxBypassAuthVlan(InterfaceProfileInt.InterType ethx) throws CreateXMLException{
		return -1;
	}
	
	public String getEthxBypassAuthVlan(InterfaceProfileInt.InterType ethx, int index){
		return String.valueOf(-1);
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
		return mgt0DhcpValns.size() > 0;
	}
	
	public int getMgt0DhcpKeepaliveVlanSize(){
		return mgt0DhcpValns.size();
	}
	
	public String getMgt0DhcpKeepaliveValn(int index) {
		return mgt0DhcpValns.get(index);
	}

	@Override
	public boolean isConfigWifi0() {
		return true;
	}

	@Override
	public boolean isConfigInterfaceUSB() {
		return true;
	}

	@Override
	public boolean isConfigMgtX(MgtType type) {
		return true;
	}
	
	/**
	 * @description: AP230 only support 12 ssids under Dual mode, while others support 15 ssids.
	 * @author huihe@aerohive.com
	 * @TODO:Need move the number setting to the device.property.
	 */
	public short getSsidSupportedUnderDual(){
		if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_230){
			return 12;
		}
		
		return 15;
	}

}