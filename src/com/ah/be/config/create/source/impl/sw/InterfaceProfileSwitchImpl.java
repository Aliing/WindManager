package com.ah.be.config.create.source.impl.sw;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.InterfaceProfileInt;
import com.ah.be.config.create.source.SpanningTreeInt;
import com.ah.be.config.create.source.impl.InterfaceProfileImpl;
import com.ah.be.config.create.source.impl.QosProfileImpl;
import com.ah.be.config.create.source.impl.SpanningTreeImpl;
import com.ah.be.config.create.source.impl.StromControlImpl;
import com.ah.be.config.create.source.impl.baseImpl.InterfaceProfileBaseImpl;
import com.ah.be.config.create.source.impl.branchRouter.InterfaceBRImpl;
import com.ah.be.config.create.source.impl.branchRouter.InterfaceBRImpl.DhcpServerInfo;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.ConfigTemplateStormControl;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.InterfaceMstpSettings;
import com.ah.bo.hiveap.InterfaceStpSettings;
import com.ah.bo.network.LLDPCDPProfile;
import com.ah.bo.network.Vlan;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortPseProfile;
import com.ah.bo.useraccess.UserProfile;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.xml.be.config.EthDuplex;
import com.ah.xml.be.config.InterfaceFlowControlValue;
import com.ah.xml.be.config.InterfaceSwitchportModeValue;
import com.ah.xml.be.config.MgtDnsServerModeValue;
import com.ah.xml.be.config.SpanningTreeBpduProtectionValue;
import com.ah.xml.be.config.SpanningTreeModeValue;

import edu.emory.mathcs.backport.java.util.Collections;

public class InterfaceProfileSwitchImpl extends InterfaceProfileBaseImpl {
	
	private static final Tracer log = new Tracer(InterfaceProfileSwitchImpl.class
			.getSimpleName());

	private HiveAp hiveAp;
	private SpanningTreeInt spanningImpl;
	
	private InterfaceProfileInt interfaceApImpl;
	private InterfaceProfileInt interfaceBrImpl;

	private Map<DeviceInfType, List<InterfaceDataInfo>> interfaceMap = new HashMap<DeviceInfType, List<InterfaceDataInfo>>();
	{
		interfaceMap.put(DeviceInfType.Gigabit,new ArrayList<InterfaceDataInfo>());
		interfaceMap.put(DeviceInfType.SFP, new ArrayList<InterfaceDataInfo>());
		interfaceMap.put(DeviceInfType.PortChannel, new ArrayList<InterfaceDataInfo>());
		interfaceMap.put(DeviceInfType.USB, new ArrayList<InterfaceDataInfo>());
	}

//	private Map<Integer, NetworkInfo> infVlanMap = new HashMap<Integer, NetworkInfo>();
	
	private boolean isView;
	
	private List<MgtType> l3_vlan_list = new ArrayList<MgtType>();

	public InterfaceProfileSwitchImpl(HiveAp hiveAp, boolean isView) throws Exception {
		this.hiveAp = hiveAp;
		this.isView = isView;
		spanningImpl = new SpanningTreeImpl(hiveAp);
		init();
		
		interfaceApImpl = new InterfaceProfileImpl(hiveAp);
		if(hiveAp.isBranchRouter() && hiveAp.getDeviceInfo().isSptEthernetMore_24()){
			//check interface wan whether exists
//			if(!checkSwitchWanPort()){
//				String errMsg = NmsUtil.getUserMessage("error.switch.port.settings.wan");
//				throw new CreateXMLException(errMsg);
//			}
			
			interfaceBrImpl = new InterfaceBRImpl(this.hiveAp, this.isView);
			Map<MgtType, DhcpServerInfo> dhcpInfoMap = interfaceBrImpl.getMgtSubResourceMap();
			for(MgtType type : dhcpInfoMap.keySet()) {
				if(type != MgtType.mgt0){
					l3_vlan_list.add(type);
				}
			}
		}
		interfaceBrImpl = (interfaceBrImpl == null) ? new InterfaceProfileBaseImpl() : interfaceBrImpl;
	}
	
	@Override
	public int getInterfacePriority(InterType type) throws Exception {
		if (interfaceBrImpl != null) {
			return interfaceBrImpl.getInterfacePriority(type);
		}
		return 0;
	}
	
	private boolean checkSwitchWanPort(){
		if(!hiveAp.isBranchRouter()){
			return true;
		}
		
		boolean existsWan = false;
		for(List<InterfaceDataInfo> dataInfoList : interfaceMap.values()){
			if(dataInfoList == null){
				continue;
			}
			for(InterfaceDataInfo dataInfo : dataInfoList){
				if(dataInfo.isPortWanModel()){
					existsWan = true;
					break;
				}
			}
			if(existsWan){
				break;
			}
		}
		return existsWan;
	}

	private void init() {
		if (hiveAp.getDeviceInfo().getIntegerValue(
				DeviceInfo.SPT_ETHERNET_COUNTS) < 24) {
			return;
		}
		Map<DeviceInfType, Map<Short, InterfaceDataInfo>> tempMap = new HashMap<DeviceInfType, Map<Short, InterfaceDataInfo>>();
		for (DeviceInterface dInterface : hiveAp.getDeviceInterfaces().values()) {
			DeviceInfType type = DeviceInfType.getInstance(dInterface.getDeviceIfType(), hiveAp.getHiveApModel()).getDeviceInfType();
			if(type == null || type == DeviceInfType.PortChannel){
				continue;
			}
			if (tempMap.get(type) == null) {
				tempMap.put(type, new HashMap<Short, InterfaceDataInfo>());
			}
			if (tempMap.get(type).get(dInterface.getDeviceIfType()) == null) {
				tempMap.get(type).put(dInterface.getDeviceIfType(),
						new InterfaceDataInfo());
			}
			tempMap.get(type).get(dInterface.getDeviceIfType())
					.setdInterface(dInterface);
		}

		if (this.hiveAp.getPortGroup() != null
				&& hiveAp.getPortGroup().getBasicProfiles() != null) {
			for (PortBasicProfile port : hiveAp.getPortGroup()
					.getBasicProfiles()) {
				String[] ethsArg = port.getETHs();
				if (ethsArg != null) {
					for (int index = 0; index < ethsArg.length; index++) {
						short ifType = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(ethsArg[index]), hiveAp.getHiveApModel());
						tempMap.get(DeviceInfType.Gigabit).get(ifType).setPortProfile(port);
					}
				}
				String[] spfsArg = port.getSFPs();
				if (spfsArg != null) {
					for (int index = 0; index < spfsArg.length; index++) {
						short ifType = DeviceInfType.SFP.getFinalValue(Integer.valueOf(spfsArg[index]), hiveAp.getHiveApModel());
						tempMap.get(DeviceInfType.SFP).get(ifType).setPortProfile(port);
					}
				}
				String[] usbArg = port.getUSBs();
				if (usbArg != null) {
					for (int index = 0; index < usbArg.length; index++) {
						short ifType = DeviceInfType.USB.getFinalValue(Integer.valueOf(usbArg[index]), hiveAp.getHiveApModel());
						tempMap.get(DeviceInfType.USB).get(ifType).setPortProfile(port);
					}
				}
				if (port.isEnabledlinkAggregation()) {
					short channel = DeviceInfType.PortChannel.getFinalValue(port.getPortChannel(), hiveAp.getHiveApModel());
					if (tempMap.get(DeviceInfType.PortChannel) == null) {
						tempMap.put(DeviceInfType.PortChannel,
								new HashMap<Short, InterfaceDataInfo>());
					}
					if (tempMap.get(DeviceInfType.PortChannel).get(channel) == null) {
						tempMap.get(DeviceInfType.PortChannel).put(channel,
								new InterfaceDataInfo());
					}
					tempMap.get(DeviceInfType.PortChannel).get(channel)
							.setPortProfile(port);
					
					DeviceInterface dInf = hiveAp.getDeviceInterfaces().get(Long.valueOf((long)channel));
					if(dInf == null){
						dInf = new DeviceInterface();
						dInf.setDeviceIfType(channel);
					}
					dInf.setHiveApModel(hiveAp.getHiveApModel());
					
					//init menber
					dInf.initMembers(port);
					
					tempMap.get(DeviceInfType.PortChannel).get(channel).setdInterface(dInf);
				}
			}
		}
		
		// storm control init
		if(hiveAp.isEnableOverrideStormControl()){
			for (ConfigTemplateStormControl storm : hiveAp.getStormControlList()) {
				short infType = storm.getInterfaceNum();
				DeviceInfType type = DeviceInfType.getInstance(infType, hiveAp.getHiveApModel()).getDeviceInfType();
				if(type == null){
					continue;
				}
				if (tempMap.get(type) == null) {
					tempMap.put(type, new HashMap<Short, InterfaceDataInfo>());
				}
				if (tempMap.get(type).get(infType) == null) {
					tempMap.get(type).put(infType, new InterfaceDataInfo());
				}
				tempMap.get(type).get(infType).setStormControl(storm);
			}
		} else {	//for storm control override
			List<ConfigTemplateStormControl> stormControlList = hiveAp.getConfigTemplate().getStormControlList();
			String accessMode = MgrUtil.getUserMessage("config.configTemplate.access");
			String trunkMode = MgrUtil.getUserMessage("config.configTemplate.8021Q");
			ConfigTemplateStormControl accessST = null;
			ConfigTemplateStormControl trunkST = null;
			for(ConfigTemplateStormControl stormObj : stormControlList){
				if(accessMode.equals(stormObj.getInterfaceType())){
					accessST = stormObj;
				}else if(trunkMode.equals(stormObj.getInterfaceType())){
					trunkST = stormObj;
				}
			}
			
			for(Map<Short, InterfaceDataInfo> temp1 : tempMap.values()){
				for(InterfaceDataInfo infInfo : temp1.values()){
					if(infInfo.isPortTrunkModel()){
						infInfo.setStormControl(trunkST);
					}else if(infInfo.isPortAccessModel()){
						infInfo.setStormControl(accessST);
					}
				}
			}
		}
		
		//for pse profile init
		if (this.hiveAp.getPortGroup() != null && hiveAp.getPortGroup().getPortPseProfiles() != null){
			for(PortPseProfile pseObj : hiveAp.getPortGroup().getPortPseProfiles()){
				short infType = pseObj.getInterfaceNum();
				DeviceInfType typeObj = DeviceInfType.getInstance(infType, hiveAp.getHiveApModel()).getDeviceInfType();
				if (tempMap.get(typeObj) == null) {
					tempMap.put(typeObj, new HashMap<Short, InterfaceDataInfo>());
				}
				if (tempMap.get(typeObj).get(infType) == null) {
					tempMap.get(typeObj).put(infType, new InterfaceDataInfo());
				}
				tempMap.get(typeObj).get(infType).setPseProfile(pseObj);
			}
		}
		
		//for lldp cdp override
		LLDPCDPProfile profile = hiveAp.getConfigTemplate().getLldpCdp();
		if (profile != null) {
			for(Map<Short, InterfaceDataInfo> temp1 : tempMap.values()){
				for(InterfaceDataInfo infInfo : temp1.values()){
					boolean global_lldp_enable = infInfo.isPortTrunkModel() ? profile.isEnableLLDPNonHostPorts() : profile.isEnableLLDPHostPorts();
					boolean global_cdp_enable = infInfo.isPortTrunkModel() ? profile.isEnableCDPNonHostPorts() : profile.isEnableCDPHostPorts();
					
					if(!hiveAp.isOverrideNetworkPolicySetting() || !global_lldp_enable){
						infInfo.getdInterface().setLldpReceive(global_lldp_enable);
						infInfo.getdInterface().setLldpTransmit(global_lldp_enable && !profile.isLldpReceiveOnly());
					}
					if(!hiveAp.isOverrideNetworkPolicySetting() || !global_cdp_enable){
						infInfo.getdInterface().setCdpReceive(global_cdp_enable);
					}
				}
			}
		}
		
		//STP MSTP init.
		if(hiveAp.getDeviceStpSettings() != null && spanningImpl.isEnableSpanningTree()){
			boolean overrideStp = hiveAp.getDeviceStpSettings().isOverrideStp();
			if(hiveAp.getDeviceStpSettings() != null){
				
				//init STP port level settings.
				for(InterfaceStpSettings infStp : hiveAp.getDeviceStpSettings().getInterfaceStpSettings()){
					short infType = infStp.getInterfaceNum();
					DeviceInfType type = DeviceInfType.getInstance(infType, hiveAp.getHiveApModel()).getDeviceInfType();
					if(type == null){
						continue;
					}
					if(type == DeviceInfType.PortChannel && !isPortChannelExists(infType)){
						continue;
					}
					if (tempMap.get(type) == null) {
						tempMap.put(type, new HashMap<Short, InterfaceDataInfo>());
					}
					if (tempMap.get(type).get(infType) == null) {
						tempMap.get(type).put(infType, new InterfaceDataInfo());
					}
					
					//fix bug 24885 if port is phone, access, mirror auto enable stp, edgeport, BPDU.
					if(!overrideStp){
						infStp = new InterfaceStpSettings();
					}
					if(tempMap.get(type).get(infType).getPortProfile() != null && 
							tempMap.get(type).get(infType).getPortProfile().isExistsAuthMode(this.hiveAp.getDeviceType())){
						infStp.setEnableStp(true);
						infStp.setEdgePort(true);
						tempMap.get(type).get(infType).setStpProfile(infStp);
					}else if(overrideStp){
						tempMap.get(type).get(infType).setStpProfile(infStp);
					}else if(isAutoEnableSpanningTree(tempMap.get(type).get(infType).getPortProfile())){
						infStp.setEnableStp(true);
						infStp.setEdgePort(true);
						infStp.setBpduMode(InterfaceStpSettings.BPDU_GUARD_MODE);
						tempMap.get(type).get(infType).setStpProfile(infStp);
					}
				}
				
				//init MSTP settings
				if(overrideStp && spanningImpl.getSpanningMode() == SpanningTreeModeValue.MSTP){
					for(InterfaceMstpSettings mstpInf : hiveAp.getDeviceStpSettings().getInterfaceMstpSettings()){
						short infType = mstpInf.getInterfaceNum();
						DeviceInfType type = DeviceInfType.getInstance(infType, hiveAp.getHiveApModel()).getDeviceInfType();
						if(type == null){
							continue;
						}
						if(type == DeviceInfType.PortChannel && !isPortChannelExists(infType)){
							continue;
						}
						if (tempMap.get(type) == null) {
							tempMap.put(type, new HashMap<Short, InterfaceDataInfo>());
						}
						if (tempMap.get(type).get(infType) == null) {
							tempMap.get(type).put(infType, new InterfaceDataInfo());
						}
						
						tempMap.get(type).get(infType).getMstpInstances().add(mstpInf);
					}
				}
			}
		}
		

		if(tempMap.get(DeviceInfType.Gigabit) != null){
			interfaceMap.get(DeviceInfType.Gigabit).addAll(tempMap.get(DeviceInfType.Gigabit).values());
		}
		if(tempMap.get(DeviceInfType.SFP) != null){
			interfaceMap.get(DeviceInfType.SFP).addAll(tempMap.get(DeviceInfType.SFP).values());
		}
		if(tempMap.get(DeviceInfType.PortChannel) != null){
			interfaceMap.get(DeviceInfType.PortChannel).addAll(tempMap.get(DeviceInfType.PortChannel).values());
		}
		if (hiveAp.isBranchRouter()) {
			InterfaceDataInfo info = new InterfaceDataInfo();
			info.setdInterface(new DeviceInterface());
			info.setPseProfile(new PortPseProfile());
			info.setStormControl(new ConfigTemplateStormControl());
			info.setStpProfile(new InterfaceStpSettings());
			PortBasicProfile basicProfile = new PortBasicProfile();
			PortAccessProfile accessProfile = new PortAccessProfile();
			accessProfile.setPortType(PortAccessProfile.PORT_TYPE_WAN);
            basicProfile.setAccessProfile(accessProfile);
			info.setPortProfile(basicProfile);
			interfaceMap.get(DeviceInfType.USB).add(info);
		}
		else {
			if (tempMap.get(DeviceInfType.USB) != null){
				interfaceMap.get(DeviceInfType.USB).addAll(tempMap.get(DeviceInfType.USB).values());
			}
		}
		
		for(List<InterfaceDataInfo> infInfoList : interfaceMap.values()){
			this.orderInterfaceDataInfo(infInfoList);
		}
	}
	
	private boolean isAutoEnableSpanningTree(PortBasicProfile portProfile){
		if(portProfile == null || portProfile.getAccessProfile() == null){
			return false;
		}
		
		short portType = portProfile.getAccessProfile().getPortType();
		switch(portType){
		case PortAccessProfile.PORT_TYPE_PHONEDATA:
		case PortAccessProfile.PORT_TYPE_MONITOR:
		case PortAccessProfile.PORT_TYPE_ACCESS:
			return true;
		default:
			return false;
		}
	}

	private class InterfaceDataInfo {
		private DeviceInterface dInterface;
		private ConfigTemplateStormControl stormControl;
		private PortBasicProfile portProfile;
		private InterfaceStpSettings stpProfile;
		private List<InterfaceMstpSettings> mstpInstances;
		private PortPseProfile pseProfile;

		public DeviceInterface getdInterface() {
			return dInterface;
		}

		public void setdInterface(DeviceInterface dInterface) {
			this.dInterface = dInterface;
		}

		public ConfigTemplateStormControl getStormControl() {
			return stormControl;
		}

		public void setStormControl(ConfigTemplateStormControl stormControl) {
			this.stormControl = stormControl;
		}

		public PortBasicProfile getPortProfile() {
			return portProfile;
		}

		public void setPortProfile(PortBasicProfile portProfile) {
			this.portProfile = portProfile;
		}

		public InterfaceStpSettings getStpProfile() {
			return stpProfile;
		}

		public void setStpProfile(InterfaceStpSettings stpProfile) {
			this.stpProfile = stpProfile;
		}

		public short getPortType() {
			if (portProfile != null && portProfile.getAccessProfile() != null) {
				return portProfile.getAccessProfile().getPortType();
			} else {
				return PortAccessProfile.PORT_TYPE_NONE;
			}
		}

		public boolean isPortPhone() {
			return getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA;
		}

		public PortPseProfile getPseProfile() {
			return pseProfile;
		}

		public void setPseProfile(PortPseProfile pseProfile) {
			this.pseProfile = pseProfile;
		}

		public List<InterfaceMstpSettings> getMstpInstances() {
			if(mstpInstances == null){
				mstpInstances = new ArrayList<InterfaceMstpSettings>();
			}
			return mstpInstances;
		}

		public boolean isPortAccessModel() {
			short portType = getPortType();
			return portType == PortAccessProfile.PORT_TYPE_MONITOR
					|| portType == PortAccessProfile.PORT_TYPE_ACCESS;
		}

		public boolean isPortTrunkModel() {
			short portType = getPortType();
			return portType == PortAccessProfile.PORT_TYPE_PHONEDATA
					|| portType == PortAccessProfile.PORT_TYPE_AP
					|| portType == PortAccessProfile.PORT_TYPE_8021Q;
		}

		public boolean isPortWanModel() {
			return getPortType() == PortAccessProfile.PORT_TYPE_WAN;
		}
	}
	
	private boolean isPortChannelExists(short finalValue){
		if (this.hiveAp.getPortGroup() != null && hiveAp.getPortGroup().getBasicProfiles() != null) {
			for (PortBasicProfile port : hiveAp.getPortGroup().getBasicProfiles()) {
				if(port.isEnabledlinkAggregation()){
					short portIndex = port.getPortChannel();
					if(finalValue == DeviceInfType.PortChannel.getFinalValue(portIndex, hiveAp.getHiveApModel())){
						return true;
					}
				}
			}
		}
		return false;
	}

//	private VlanDataInfo getVlanDataInfo(MgtType type) {
//		if (type == MgtType.vlan) {
//			Integer indexObj = Integer.valueOf(type.getIndex());
//			return infVlanMap.get(indexObj);
//		} else {
//			return null;
//		}
//	}
	
	/** start mgt0 settings */
	
	public boolean isConfigMgt0DhcpKeepalive(){
		return this.interfaceApImpl.isConfigMgt0DhcpKeepalive();
	}
	
	public boolean isEnableMgtDhcp(){
		return this.interfaceApImpl.isEnableMgtDhcp();
	}
	
	public boolean isEnableDhcpFallBack(){
		return this.interfaceApImpl.isEnableDhcpFallBack();
	}
	
	public int getDhcpTimeOutt(){
		return this.interfaceApImpl.getDhcpTimeOutt();
	}
	
	public boolean isConfigDhcpAddressOnly(){
		return this.interfaceApImpl.isConfigDhcpAddressOnly();
	}
	
	public boolean isEnableDhcpAddressOnly(){
		return this.interfaceApImpl.isEnableDhcpAddressOnly();
	}
	
	public int getMgt0DhcpKeepaliveVlanSize(){
		return this.interfaceApImpl.getMgt0DhcpKeepaliveVlanSize();
	}
	
	public String getMgt0DhcpKeepaliveValn(int index){
		return this.interfaceApImpl.getMgt0DhcpKeepaliveValn(index);
	}
	
	public boolean isConfigMgtIp(){
		return this.interfaceApImpl.isConfigMgtIp();
	}
	
	public String getMgtIpAndMask() throws CreateXMLException{
		if(hiveAp.isBranchRouter()){
			return this.interfaceBrImpl.getMgtIpAndMask();
		}else{
			return this.interfaceApImpl.getMgtIpAndMask();
		}
	}
	
	public boolean isConfigMgtHive() {
		return interfaceApImpl.isConfigMgtHive();
	}
	
	public String getMgtBindHive() {
		return interfaceApImpl.getMgtBindHive();
	}
	
	public boolean isConfigMgtVlan(){
		return this.interfaceApImpl.isConfigMgtVlan();
	}
	
	public int getMgtVlanId() throws CreateXMLException{
		return this.interfaceApImpl.getMgtVlanId();
	}
	
	public int getMgtNativeVlan() throws CreateXMLException{
		return this.interfaceApImpl.getMgtNativeVlan();
	}
	
	public boolean isConfigMgtDefaultIpPrefix(){
		return this.interfaceApImpl.isConfigMgtDefaultIpPrefix();
	}
	
	public String getMgtDefaultIpPrefix(){
		return this.interfaceApImpl.getMgtDefaultIpPrefix();
	}
	
	/** end mgt0 settings */
	

	public int getSRInfeSize(DeviceInfType type) {
		return interfaceMap.get(type).size();
	}

	public int getMTUValue() {
		int mtuValue = hiveAp.getInterfaceMtu4Ethernet();
		if(NmsUtil.compareSoftwareVersion("6.0.2.0", hiveAp.getSoftVer()) > 0 && 
				mtuValue < 1536){
			return 1536;
		}else{
			return mtuValue;
		}
	}
	
	public int getMgt0MTUValue() {
		return hiveAp.getInterfaceMtu4Mgt0();
	}

	public String getInfPortName(DeviceInfType type, int index) {
		if(type == DeviceInfType.PortChannel){
			return String.valueOf(interfaceMap.get(type).get(index).getPortProfile().getPortChannel());
		}else{
			short deviceIfType = interfaceMap.get(type).get(index).getdInterface().getDeviceIfType();
			String infCLIName = DeviceInfType.getInstance(deviceIfType, hiveAp.getHiveApModel()).getCLIName(hiveAp.getHiveApModel());
			infCLIName = infCLIName.replace("eth", "");
			return infCLIName;
		}
	}
	
	//for fix bug 23940, agg port member type changed, if speed, duplex, flow control return to default value.
	private short getValidPortItem(short value, EnumItem[] attrItem){
		boolean isFound = false;
		for(int i=0; i<attrItem.length; i++){
			if(value == attrItem[i].getKey()){
				isFound = true;
				break;
			}
		}
		if(isFound){
			return value;
		}else{
			return (short)attrItem[0].getKey();
		}
	}

	public String getInfSpeed(DeviceInfType type, int index) {
		InterfaceDataInfo ifData = interfaceMap.get(type).get(index);
		short speed = this.getValidPortItem(ifData.getdInterface().getSpeed(), 
				ifData.getdInterface().getEnumSpeedType());
		
		switch (speed) {
		case AhInterface.ETH_SPEED_AUTO:
			return "auto";
		case AhInterface.ETH_SPEED_10M:
			return "10";
		case AhInterface.ETH_SPEED_100M:
			return "100";
		case AhInterface.ETH_SPEED_1000M:
			return "1000";
		case AhInterface.ETH_SPEED_10000M:
			return "10000";
		default:
			return "auto";
		}
	}

	public EthDuplex getInfDuplex(DeviceInfType type, int index) {
		InterfaceDataInfo ifData = interfaceMap.get(type).get(index);
		short duplex = this.getValidPortItem(ifData.getdInterface().getDuplex(), 
				ifData.getdInterface().getEnumDuplexType());
		
		switch (duplex) {
		case AhInterface.ETH_DUPLEX_AUTO:
			return EthDuplex.AUTO;
		case AhInterface.ETH_DUPLEX_HALF:
			return EthDuplex.HALF;
		case AhInterface.ETH_DUPLEX_FULL:
			return EthDuplex.FULL;
		default:
			return EthDuplex.AUTO;
		}
	}

	public boolean isEnableInfAutoMdix(DeviceInfType type, int index) {
		return interfaceMap.get(type).get(index).getdInterface().isAutoMdix();
	}

	public InterfaceFlowControlValue getInfFlowControlValue(DeviceInfType type,
			int index) {
		InterfaceDataInfo ifData = interfaceMap.get(type).get(index);
		short fControl = this.getValidPortItem(ifData.getdInterface().getFlowControlStatus(), 
				ifData.getdInterface().getEnumFlowCtlType()) ;
		switch (fControl) {
		case AhInterface.FLOW_CONTROL_STATUS_AUTO:
			return InterfaceFlowControlValue.AUTO;
		case AhInterface.FLOW_CONTROL_STATUS_ENABLE:
			return InterfaceFlowControlValue.ENABLE;
		case AhInterface.FLOW_CONTROL_STATUS_DISABLE:
			return InterfaceFlowControlValue.DISABLE;
		default:
			return InterfaceFlowControlValue.AUTO;
		}
	}

	public int getInfLinkDebounce(DeviceInfType type, int index) {
		return interfaceMap.get(type).get(index).getdInterface()
				.getDebounceTimer();
	}

	public boolean isConfigInfSwitchPort(DeviceInfType type, int index) {
		InterfaceDataInfo ifData = interfaceMap.get(type).get(index);
		return ifData != null
				&& ifData.getPortProfile() != null
				&& ifData.getPortProfile().getAccessProfile() != null
				&& ifData.getPortProfile().getAccessProfile().getPortType() != PortAccessProfile.PORT_TYPE_WAN;
	}

	public InterfaceSwitchportModeValue getInfPortVlanMode(DeviceInfType type, int index) {
		InterfaceDataInfo ifData = interfaceMap.get(type).get(index);
		if (ifData.isPortAccessModel()) {
			return InterfaceSwitchportModeValue.ACCESS;
		} else if (ifData.isPortTrunkModel()) {
			return InterfaceSwitchportModeValue.TRUNK;
		}
		return null;
	}
	
	public boolean isConfigSwitchPortAccess(DeviceInfType type, int index){
		InterfaceDataInfo ifData = interfaceMap.get(type).get(index);
		return ifData.isPortAccessModel() && !ifData.getPortProfile().isExistsAuthMode(hiveAp.getDeviceType());
	}
	
	public boolean isConfigSwitchPortTrunk(DeviceInfType type, int index){
		InterfaceDataInfo ifData = interfaceMap.get(type).get(index);
		return ifData.isPortTrunkModel() && !ifData.getPortProfile().isExistsAuthMode(hiveAp.getDeviceType());
	}

	public int getAccessVlan(DeviceInfType type, int index)
			throws CreateXMLException {
		InterfaceDataInfo ifData = interfaceMap.get(type).get(index);
		if(ifData != null && ifData.getPortProfile() != null && ifData.getPortProfile().getAccessProfile() != null && 
				ifData.getPortProfile().getAccessProfile().getDefUserProfile() != null){
			UserProfile up = ifData.getPortProfile().getAccessProfile().getDefUserProfile();
			Vlan vlan = CLICommonFunc.getMappingVlan(up, hiveAp.getConfigTemplate());
			if (vlan != null) {
				return CLICommonFunc.getVlan(vlan, hiveAp).getVlanId();
			}
		}
		return 1;
	}
	
	public int getNativeVlan(DeviceInfType type, int index) throws CreateXMLException{
		InterfaceDataInfo ifData = interfaceMap.get(type).get(index);
		if(ifData != null && ifData.getPortProfile() != null && ifData.getPortProfile().getAccessProfile() != null){
			Vlan nativeVlan;
			if(ifData.getPortProfile().getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA){
//				UserProfile nativeUP = ifData.getPortProfile().getAccessProfile().getDataUserProfile();
//				nativeVlan = CLICommonFunc.getMappingVlan(nativeUP, hiveAp.getConfigTemplate());
				nativeVlan = ifData.getPortProfile().getAccessProfile().getDataVlan();
			}else{
				nativeVlan = ifData.getPortProfile().getAccessProfile().getNativeVlan();
			}
			if (nativeVlan != null) {
				return CLICommonFunc.getVlan(nativeVlan, hiveAp).getVlanId();
			}
		}
		return -1;
	}
	
	public boolean isInfPortAllowedVlanAll(DeviceInfType type, int index){
		String[] allowedVlans = null;
		try{
			allowedVlans = getInfPortAllowedVlan(type, index);
		}catch(Exception e){
			log.error("isInfPortAllowedVlanAll", e);
		}
		
		if(allowedVlans == null || allowedVlans.length == 0) {
			return true;
		}
		for(int i=0; i<allowedVlans.length; i++){
			if(allowedVlans[i].equalsIgnoreCase("all")){
				return true;
			}
		}
		return false;
	}

	public String[] getInfPortAllowedVlan(DeviceInfType type, int index)
			throws CreateXMLException {
		InterfaceDataInfo ifData = interfaceMap.get(type).get(index);
		String allowedVlans = null;
		if (ifData.isPortPhone() && !ifData.getPortProfile().isExistsAuthMode(hiveAp.getDeviceType())) {
			//fix Bug 22072 -<Edinburgh_HM_CLI> please not use allow vlan all for phone&data port + no auth
			int nativeVlan = getNativeVlan(type, index);
			int voiceVlan = getInfPortVoiceVlan(type, index);
			if(nativeVlan > 0){
				allowedVlans = String.valueOf(nativeVlan);
			}
			if(voiceVlan > 0){
				if(allowedVlans == null){
					allowedVlans = String.valueOf(voiceVlan);
				}else{
					allowedVlans += "," + voiceVlan;
				}
			}
		} else if (ifData.isPortTrunkModel()) {
			allowedVlans = ifData.getPortProfile().getAccessProfile()
					.getAllowedVlan();
		}
		
		if(allowedVlans != null && allowedVlans.toLowerCase().contains("all")){
			return null;
		}else if (allowedVlans != null) {
			int nativeVlan = getNativeVlan(type, index);
			if(nativeVlan > 0){
				allowedVlans += "," + nativeVlan;
			}
			List<String> vlanList = CLICommonFunc.mergeRangeList(allowedVlans);
			if(vlanList == null || vlanList.isEmpty()){
				return new String[0];
			}else{
				String[] resVlans = new String[vlanList.size()];
				for(int i=0; i<resVlans.length; i++){
					resVlans[i] = vlanList.get(i);
				}
				return resVlans;
			}
		}

		return null;
	}
	
	private UserProfile getPortUserProfile(DeviceInfType type, int index) {
		InterfaceDataInfo ifData = interfaceMap.get(type).get(index);
		if(ifData == null || ifData.getPortProfile() == null || ifData.getPortProfile().getAccessProfile() == null){
			return null;
		}
		PortAccessProfile accessProfile = ifData.getPortProfile().getAccessProfile();
		if(accessProfile.getDefUserProfile() != null){
			return accessProfile.getDefUserProfile();
//		}else if(accessProfile.getDataUserProfile() != null){
//			return accessProfile.getDataUserProfile();
		}else if(accessProfile.getSelfRegUserProfile() != null){
			return accessProfile.getSelfRegUserProfile();
		}else if(accessProfile.getAuthOkUserProfile() != null && !accessProfile.getAuthOkUserProfile().isEmpty()){
			return accessProfile.getAuthOkUserProfile().iterator().next();
		}else if(accessProfile.getAuthOkDataUserProfile() != null && !accessProfile.getAuthOkDataUserProfile().isEmpty()){
			return accessProfile.getAuthOkDataUserProfile().iterator().next();
		}else if(accessProfile.getAuthFailUserProfile() != null && !accessProfile.getAuthFailUserProfile().isEmpty()){
			return accessProfile.getAuthFailUserProfile().iterator().next();
		}else {
			return null;
		}
	}
	
	public boolean isConfigPortUserProfileId(DeviceInfType type, int index){
		return type != DeviceInfType.PortChannel && getPortUserProfile(type, index) != null;
	}
	
	public int getPortUserProfileId(DeviceInfType type, int index) {
		return getPortUserProfile(type, index).getAttributeValue();
	}

	public int getInfPortVoiceVlan(DeviceInfType type, int index)
			throws CreateXMLException {
		InterfaceDataInfo ifData = interfaceMap.get(type).get(index);
		Vlan voiceVlan = ifData.getPortProfile().getAccessProfile().getVoiceVlan();
		if(ifData.isPortPhone() && voiceVlan != null){
			return CLICommonFunc.getVlan(voiceVlan, hiveAp).getVlanId();
		}else{
			return -1;
		}
	}

	public boolean isConfigInfPortChannel(DeviceInfType type, int index){
		InterfaceDataInfo ifData = interfaceMap.get(type).get(index);
		return ifData.getPortProfile() != null && ifData.getPortProfile().isEnabledlinkAggregation();
	}

	public String getInfPortChannel(DeviceInfType type, int index) {
		InterfaceDataInfo ifData = interfaceMap.get(type).get(index);
		return String.valueOf(ifData.getPortProfile().getPortChannel());
	}

	public boolean isConfigInfSecurityObject(DeviceInfType type, int index) {
		InterfaceDataInfo ifData = interfaceMap.get(type).get(index);
		return ifData.getPortProfile() != null && 
				ifData.getPortProfile().isExistsAuthMode(hiveAp.getDeviceType()) && 
				!isConfigInfPortChannel(type, index);
	}

	public String getInfSecurityObject(DeviceInfType type, int index) {
		PortBasicProfile port = interfaceMap.get(type).get(index)
				.getPortProfile();
		return port.getAccessProfile().getName();
	}

	// public boolean isConfigInfDhcpClient(DeviceInfType type, int index){
	// PortBasicProfile port =
	// interfaceMap.get(type).get(index).getPortProfile();
	// return port != null &&
	// interfaceMap.get(type).get(index).isPortWanModel();
	// }

	// public boolean isEnableInfDhcpClient(DeviceInfType type, int index);

	// public boolean isConfigInfIp(DeviceInfType type, int index){
	// PortBasicProfile port =
	// interfaceMap.get(type).get(index).getPortProfile();
	// return port != null &&
	// interfaceMap.get(type).get(index).isPortWanModel();
	// }

	// public String getInfIp(DeviceInfType type, int index);

	public boolean isInfShutdown(DeviceInfType type, int index) {
		InterfaceDataInfo infInfo = interfaceMap.get(type).get(index);
		if(hiveAp.isOverrideNetworkPolicySetting() && infInfo != null && infInfo.getdInterface() != null){
			short adminState = infInfo.getdInterface().getAdminState();
			return adminState == AhInterface.ADMIN_STATE_DOWM;
		}else if(!hiveAp.isOverrideNetworkPolicySetting() && infInfo != null && infInfo.getPortProfile() != null &&
				infInfo.getPortProfile().getAccessProfile() != null){
			return infInfo.getPortProfile().getAccessProfile().isShutDownPorts();
		}else{
			return false;
		}
	}

	// public boolean isConfigInfWAN(DeviceInfType type, int index);
	//
	// public boolean isInfWanEnable(DeviceInfType type, int index);
	//
	// public boolean isInfWanNatEnable(DeviceInfType type, int index);
	
	public boolean isConfigInfStormControl(DeviceInfType type, int index){
		ConfigTemplateStormControl stromObj = getStormControlBo(type, index);
		return stromObj != null && (
				stromObj.isAllTrafficType() || 
				stromObj.isBroadcast() || 
				stromObj.isMulticast() ||
				stromObj.isTcpsyn() || 
				stromObj.isUnknownUnicast() );
	}

	public boolean isConfigInfQosClassifier(DeviceInfType type, int index) {
		InterfaceDataInfo infInfo = interfaceMap.get(type).get(index);
		return QosProfileImpl.isSwitchQosEnable(this.hiveAp) 
				&& infInfo.getPortProfile() != null 
				&& (infInfo.getPortProfile().isEnabledlinkAggregation() ? type == DeviceInfType.PortChannel : true) 
				&& infInfo.getPortProfile().getAccessProfile().getPortType() != PortAccessProfile.PORT_TYPE_WAN
				&& infInfo.getPortProfile().getAccessProfile().getPortType() != PortAccessProfile.PORT_TYPE_MONITOR
				&& infInfo.getPortProfile().getAccessProfile().getQosClassificationMode() == PortAccessProfile.QOS_CLASSIFICATION_MODE_TRUSTED;
	}

	public String getInfQosClassifierName(DeviceInfType type, int index) {
		return interfaceMap.get(type).get(index).getPortProfile()
				.getAccessProfile().getName();
	}

	public boolean isConfigInfQosMarker(DeviceInfType type, int index) {
		InterfaceDataInfo infInfo = interfaceMap.get(type).get(index);
		return QosProfileImpl.isSwitchQosEnable(this.hiveAp)
				&& infInfo.getPortProfile() != null
				&& (infInfo.getPortProfile().isEnabledlinkAggregation() ? type == DeviceInfType.PortChannel : true) 
				&& infInfo.getPortProfile().getAccessProfile().getPortType() != PortAccessProfile.PORT_TYPE_WAN
				&& infInfo.getPortProfile().getAccessProfile().getPortType() != PortAccessProfile.PORT_TYPE_MONITOR
				&& infInfo.getPortProfile().getAccessProfile().isEnableQosMark();
	}

	public String getInfQosMarkerName(DeviceInfType type, int index) {
		return interfaceMap.get(type).get(index).getPortProfile()
				.getAccessProfile().getName();
	}

//	public boolean isConfigInfQosShaper(DeviceInfType type, int index) {
//		InterfaceDataInfo infInfo = interfaceMap.get(type).get(index);
//		return infInfo.getPortProfile() != null
//				&& infInfo.getPortProfile().getAccessProfile().getPortType() != PortAccessProfile.PORT_TYPE_WAN
//				&& infInfo.getPortProfile().getAccessProfile().getPortType() != PortAccessProfile.PORT_TYPE_MONITOR
//				&& infInfo.getPortProfile().getAccessProfile()
//						.getQosClassificationMode() == PortAccessProfile.QOS_CLASSIFICATION_MODE_UNTRUSTED;
//	}

//	public int getInfQosShaperValue(DeviceInfType type, int index) {
//		return interfaceMap.get(type).get(index).getPortProfile()
//				.getAccessProfile().getUntrustedPriority();
//	}

	public boolean isConfigStormControlAll(DeviceInfType type, int index) {
		ConfigTemplateStormControl storm = getStormControlBo(type, index);
		return storm.isAllTrafficType()
				|| (storm.isBroadcast() && storm.isMulticast()
						&& storm.isUnknownUnicast() && storm.isTcpsyn());
	}

	public boolean isConfigStormControlBroadcast(DeviceInfType type, int index) {
		ConfigTemplateStormControl storm = getStormControlBo(type, index);
		return !storm.isAllTrafficType() && storm.isBroadcast();
	}

	public boolean isConfigStormControlMulticast(DeviceInfType type, int index) {
		ConfigTemplateStormControl storm = getStormControlBo(type, index);
		return !storm.isAllTrafficType() && storm.isMulticast();
	}

	public boolean isConfigStormControlUnknownUnicast(DeviceInfType type,
			int index) {
		ConfigTemplateStormControl storm = getStormControlBo(type, index);
		return !storm.isAllTrafficType() && storm.isUnknownUnicast();
	}

	public boolean isConfigStormControlTcpSyn(DeviceInfType type, int index) {
		ConfigTemplateStormControl storm = getStormControlBo(type, index);
		return !storm.isAllTrafficType() && storm.isTcpsyn();
	}
	
	public String isConfigStormControlMode(){
		return StromControlImpl.getStormControlMode(this.hiveAp).value();
	}

	public boolean isConfigStormControlPercentage(DeviceInfType type, int index) {
		ConfigTemplateStormControl storm = getStormControlBo(type, index);
		return storm.getRateLimitType() == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PERCENTAGE_ID;
	}

	public int getStormControlPercentage(DeviceInfType type, int index) {
		return (int) getStormControlBo(type, index).getRateLimitValue();
	}
	
	public boolean isConfigStormControlBps(DeviceInfType type, int index) {
		return isConfigStormControlKbps(type, index);
	}
	
	private ConfigTemplateStormControl getStormControlBo(DeviceInfType type, int index){
		InterfaceDataInfo infInfo = interfaceMap.get(type).get(index);
		if(infInfo != null){
			return infInfo.getStormControl();
		}else{
			return null;
		}
	}

	public int getStormControlBps(DeviceInfType type, int index) {
		int kbps = getStormControlKbps(type, index);
		return kbps * 1000;
	}
	
	public boolean isConfigStormControlKbps(DeviceInfType type, int index){
		ConfigTemplateStormControl storm = getStormControlBo(type, index);
		return storm.getRateLimitType() == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_BPS_ID;
	}
	
	public int getStormControlKbps(DeviceInfType type, int index){
		ConfigTemplateStormControl storm = getStormControlBo(type, index);
		return (int)storm.getRateLimitValue();
	}

	public boolean isConfigStormControlPps(DeviceInfType type, int index) {
		ConfigTemplateStormControl storm = getStormControlBo(type, index);
		return storm.getRateLimitType() == ConfigTemplateStormControl.STORM_CONTROL_RATE_LIMIT_TYPE_PPS_ID;
	}

	public int getStormControlPps(DeviceInfType type, int index) {
		ConfigTemplateStormControl storm = getStormControlBo(type, index);
		return (int)storm.getRateLimitValue();
	}
	
	/** start spanning tree */
	
	public boolean isConfigSpanningTree(DeviceInfType type, int index){
		//if port is wan model or access profile config auth function not config spanning tree.
		InterfaceDataInfo infData = interfaceMap.get(type).get(index);
		return !infData.isPortWanModel() &&
				infData.getStpProfile() != null;
	}
	
	public boolean isEnableSpanningTree(DeviceInfType type, int index){
		return interfaceMap.get(type).get(index).getStpProfile().isEnableStp();
	}
	
	public boolean isConfigSpanningPathCost(DeviceInfType type, int index){
		return getSpanningPathCost(type, index) > 0;
	}
	
	public int getSpanningPathCost(DeviceInfType type, int index){
		return interfaceMap.get(type).get(index).getStpProfile().getDevicePathCost();
	}
	
	public int getSpanningPriority(DeviceInfType type, int index){
		return interfaceMap.get(type).get(index).getStpProfile().getDevicePriority();
	}
	
	public boolean isEnableSpanningEdgePort(DeviceInfType type, int index){
		return interfaceMap.get(type).get(index).getStpProfile().isEdgePort();
	}
	
	public SpanningTreeBpduProtectionValue getSpanningBpdu(DeviceInfType type, int index){
		short bpdu = interfaceMap.get(type).get(index).getStpProfile().getBpduMode();
		switch(bpdu){
		case InterfaceStpSettings.BPDU_FILTER_MODE:
			return SpanningTreeBpduProtectionValue.BPDU_FILTER;
		case InterfaceStpSettings.BPDU_GUARD_MODE:
			return SpanningTreeBpduProtectionValue.BPDU_GUARD;
		default:
			return null;
		}
	}
	
	public boolean isEnableLldpReceive(DeviceInfType type, int index){
		return interfaceMap.get(type).get(index).getdInterface().isLldpReceive();
	}
	
	public boolean isEnableLldpTransmit(DeviceInfType type, int index){
		return interfaceMap.get(type).get(index).getdInterface().isLldpTransmit();
	}
	
	public boolean isEnableCdpReceive(DeviceInfType type, int index){
		return interfaceMap.get(type).get(index).getdInterface().isCdpReceive();
	}
	
	public boolean isBindToPortChannel(DeviceInfType type, int index){
		if(type == DeviceInfType.PortChannel){
			return false;
		}
		PortBasicProfile portBase = interfaceMap.get(type).get(index).getPortProfile();
		return portBase != null && portBase.isEnabledlinkAggregation();
	}
	
	public boolean isConfigInfPse(DeviceInfType type, int index){
		PortPseProfile pseProfile = interfaceMap.get(type).get(index).getPseProfile();
		return pseProfile != null;
	}
	
	public boolean isShutdownInfPse(DeviceInfType type, int index){
		PortPseProfile pseProfile = interfaceMap.get(type).get(index).getPseProfile();
		return !pseProfile.isEnabelIfPse();
	}
	
	public boolean isConfigPseProfile(DeviceInfType type, int index){
		PortPseProfile pseProfile = interfaceMap.get(type).get(index).getPseProfile();
		return pseProfile.getPseProfile() != null;
	}
	
	public String getInfPseProfileName(DeviceInfType type, int index){
		PortPseProfile pseProfile = interfaceMap.get(type).get(index).getPseProfile();
		return pseProfile.getPseProfile().getName();
	}
	
	public int getSpanningMstInstanceSize(DeviceInfType type, int index){
		if(interfaceMap.get(type).get(index).getMstpInstances() == null){
			return 0;
		}else{
			return interfaceMap.get(type).get(index).getMstpInstances().size();
		}
	}
	
	public String getSpanningMstInstanceName(DeviceInfType type, int index, int i){
		return String.valueOf(interfaceMap.get(type).get(index).getMstpInstances().get(i).getInstance());
	}
	
	public boolean isConfigMstInstancePathCost(DeviceInfType type, int index, int i){
		return getMstInstancePathCost(type, index, i) > 0;
	}
	
	public int getMstInstancePathCost(DeviceInfType type, int index, int i){
		return interfaceMap.get(type).get(index).getMstpInstances().get(i).getDevicePathCost();
	}
	
	public int getMstInstancePriority(DeviceInfType type, int index, int i){
		return interfaceMap.get(type).get(index).getMstpInstances().get(i).getDevicePriority();
	}
	
	/** end spanning tree */
	
	/** start switch used for Router */
	
	public boolean isConfigInfWAN(DeviceInfType type, int index){
		InterfaceDataInfo dataInfo = interfaceMap.get(type).get(index);
		return hiveAp.isBranchRouter() && dataInfo != null && dataInfo.getPortProfile() != null && 
				dataInfo.getPortProfile().getAccessProfile() != null && 
				dataInfo.getPortProfile().getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_WAN;
	}
	
	public boolean isInfWanNatEnable(DeviceInfType type, int index){
		InterfaceDataInfo dataInfo = interfaceMap.get(type).get(index);
		return dataInfo.getdInterface().isEnableNat();
	}
	
	public boolean isInfWanNatPolicyEnable(DeviceInfType type, int index){
		InterfaceDataInfo dataInfo = interfaceMap.get(type).get(index);
		return !dataInfo.getdInterface().isDisablePortForwarding();
	}
	
	public boolean isConfigInfDhcpClient(DeviceInfType type, int index){
		return isConfigInfWAN(type, index);
	}
	
	public boolean isEnableInfDhcpClient(DeviceInfType type, int index){
		//when pppoe enable must disable dhcp.
		if(type == DeviceInfType.Gigabit){
			DeviceInterface ethInterface = hiveAp.getDeviceInterfaces().get((long)(index + AhInterface.DEVICE_IF_TYPE_ETH1));
			return ethInterface.getConnectionType().equals("1");
		}else if(type == DeviceInfType.SFP){
			short firstSfpPort = DeviceInfType.SFP.getFinalValue(1, hiveAp.getHiveApModel());
			DeviceInterface sfpInterface = hiveAp.getDeviceInterfaces().get((long)(index+ firstSfpPort));
			return sfpInterface.getConnectionType().equals("1");
		}else if(type == DeviceInfType.USB){
			DeviceInterface usbInterface = hiveAp.getUSBInterface();
			return usbInterface.getConnectionType().equals("1");
		}else{
			return false;
		}
		/*if(hiveAp.isPppoeEnableCurrent() || hiveAp.isEnablePppoe()){
			return false;
		}else{
			return hiveAp.getEth0Interface() != null && hiveAp.getEth0Interface().isEnableDhcp();
		}*/
	}
	
	public boolean isConfigStaticIp(DeviceInfType type, int index){
		if(type == DeviceInfType.Gigabit){
			DeviceInterface ethInterface = hiveAp.getDeviceInterfaces().get((long)(index + AhInterface.DEVICE_IF_TYPE_ETH1));
			return ethInterface.getConnectionType().equals("2") && ethInterface.getIpAndNetmask() !=null;
		}else if(type == DeviceInfType.SFP){
			short firstSfpPort = DeviceInfType.SFP.getFinalValue(1, hiveAp.getHiveApModel());
			DeviceInterface sfpInterface = hiveAp.getDeviceInterfaces().get((long)(index + firstSfpPort));
			return sfpInterface.getConnectionType().equals("2")&& sfpInterface.getIpAndNetmask() !=null;
		}else if(type == DeviceInfType.USB){
			DeviceInterface usbInterface = hiveAp.getUSBInterface();
			return usbInterface.getConnectionType().equals("2")&& usbInterface.getIpAndNetmask() !=null;
		}else{
			return false;
		}
		
	}
	public boolean isConfigInfIp(DeviceInfType type, int index){
		return isConfigInfWAN(type, index) && !isEnableInfDhcpClient(type, index) && 
				isConfigStaticIp(type, index);
	}
	
	public String getInfIp(DeviceInfType type, int index){
		if(type == DeviceInfType.Gigabit){
			DeviceInterface ethInterface = hiveAp.getDeviceInterfaces().get((long)(index + AhInterface.DEVICE_IF_TYPE_ETH1));
			return  ethInterface.getIpAndNetmask();
		}else if(type == DeviceInfType.SFP){
			short firstSfpPort = DeviceInfType.SFP.getFinalValue(1, hiveAp.getHiveApModel());
			DeviceInterface sfpInterface = hiveAp.getDeviceInterfaces().get((long)(index + firstSfpPort));
			return sfpInterface.getIpAndNetmask();
		}else if(type == DeviceInfType.USB){
			DeviceInterface usbInterface = hiveAp.getUSBInterface();
			return usbInterface.getIpAndNetmask();
		}else{
			return "";
		}
	}
	
	public boolean isConfigInterfaceUSB() {
		if(interfaceMap.get(DeviceInfType.USB) == null || interfaceMap.get(DeviceInfType.USB).isEmpty()){
			return false;
		}
		InterfaceDataInfo infData = interfaceMap.get(DeviceInfType.USB).get(0);
		return infData != null && infData.isPortWanModel();
	}
	
	public boolean isEnableWanNat(InterType type){
		return interfaceBrImpl.isEnableWanNat(type);
	}
	
	public Map<MgtType, DhcpServerInfo> getMgtSubResourceMap(){
		if(interfaceBrImpl != null){
			return interfaceBrImpl.getMgtSubResourceMap();
		}else{
			return null;
		}
	}
	
	public int getWanInterfacePriority(DeviceInfType type,int index){	
		if (interfaceBrImpl != null) {
			return this.interfaceBrImpl.getWanInterfacePriority(type, index);
		}
		return 0;
	}
	
	public int getInterfaceVlansize(){
		return this.l3_vlan_list.size();
	}
	
	public MgtType getMgtTypeType(int index){
		return l3_vlan_list.get(index);
	}

	public boolean isConfigMgtChild(MgtType type){
		return interfaceBrImpl != null && interfaceBrImpl.isConfigMgtChild(type);
	}

	public int getMgtChildVlan(MgtType type) throws CreateXMLException{
		return interfaceBrImpl.getMgtChildVlan(type);
	}

	public String getMgtChildIp(MgtType type) throws CreateXMLException{
		return interfaceBrImpl.getMgtChildIp(type);
	}

	public boolean isConfigMgtChildIpHelper(MgtType type){
		return interfaceBrImpl.isConfigMgtChildIpHelper(type);
	}

	public int getMgtChildIpHelperSize(MgtType type){
		return interfaceBrImpl.getMgtChildIpHelperSize(type);
	}

	public String getMgtChildIpHelperAddress(MgtType type, int index){
		return interfaceBrImpl.getMgtChildIpHelperAddress(type, index);
	}

	public boolean isMgtChildPingEnable(MgtType type){
		return interfaceBrImpl.isMgtChildPingEnable(type);
	}

	public boolean isEnableMgtChildDhcpServer(MgtType type){
		return interfaceBrImpl.isEnableMgtChildDhcpServer(type);
	}

	public boolean isEnableMgtChildAuthoritative(MgtType type){
		return interfaceBrImpl.isEnableMgtChildAuthoritative(type);
	}

	public int getMgtChildIpPoolSize(MgtType type){
		return interfaceBrImpl.getMgtChildIpPoolSize(type);
	}

	public String getMgtChildIpPoolName(MgtType type, int index) throws CreateXMLException{
		return interfaceBrImpl.getMgtChildIpPoolName(type, index);
	}

	public boolean isEnableMgtChildArpCheck(MgtType type){
		return interfaceBrImpl.isEnableMgtChildArpCheck(type);
	}

	public boolean isConfigMgtChildOptionsDefaultGateway(MgtType type){
		return interfaceBrImpl.isConfigMgtChildOptionsDefaultGateway(type);
	}

	public String getMgtChildOptionsDefaultGateway(MgtType type) throws CreateXMLException{
		return interfaceBrImpl.getMgtChildOptionsDefaultGateway(type);
	}

	public boolean isMgtDhcpNatSupport(MgtType type){
		return interfaceBrImpl.isMgtDhcpNatSupport(type);
	}

	public boolean isConfigMgtChildOptionsLeaseTime(MgtType type){
		return interfaceBrImpl.isConfigMgtChildOptionsLeaseTime(type);
	}

	public int getMgtChildOptionsLeaseTime(MgtType type){
		return interfaceBrImpl.getMgtChildOptionsLeaseTime(type);
	}

	public boolean isConfigMgtChildOptionsNetMask(MgtType type){
		return interfaceBrImpl.isConfigMgtChildOptionsNetMask(type);
	}

	public String getMgtChildOptionsNetMask(MgtType type){
		return interfaceBrImpl.getMgtChildOptionsNetMask(type);
	}

	public int getMgtChildOptionsHivemanagerSize(MgtType type) throws CreateXMLException{
		return interfaceBrImpl.getMgtChildOptionsHivemanagerSize(type);
	}

	public String getMgtChildOptionsHivemanager(MgtType type, int index){
		return interfaceBrImpl.getMgtChildOptionsHivemanager(type, index);
	}

	public boolean isConfigMgtChildOptionsDoMain(MgtType type){
		return interfaceBrImpl.isConfigMgtChildOptionsDoMain(type);
	}

	public String getMgtChildOptionsDoMain(MgtType type){
		return interfaceBrImpl.getMgtChildOptionsDoMain(type);
	}

	public boolean isConfigMgtChildOptionsMtu(MgtType type){
		return interfaceBrImpl.isConfigMgtChildOptionsMtu(type);
	}

	public int getMgtChildOptionsMtu(MgtType type){
		return interfaceBrImpl.getMgtChildOptionsMtu(type);
	}

	public boolean isConfigMgtChildOptionsDns1(MgtType type){
		return interfaceBrImpl.isConfigMgtChildOptionsDns1(type);
	}

	public String getMgtChildOptionsDns1(MgtType type) throws CreateXMLException{
		return interfaceBrImpl.getMgtChildOptionsDns1(type);
	}

	public boolean isConfigMgtChildOptionsDns2(MgtType type) throws CreateXMLException{
		return interfaceBrImpl.isConfigMgtChildOptionsDns2(type);
	}

	public String getMgtChildOptionsDns2(MgtType type) throws CreateXMLException{
		return interfaceBrImpl.getMgtChildOptionsDns2(type);
	}

	public boolean isConfigMgtChildOptionsDns3(MgtType type) throws CreateXMLException{
		return interfaceBrImpl.isConfigMgtChildOptionsDns3(type);
	}

	public String getMgtChildOptionsDns3(MgtType type) throws CreateXMLException{
		return interfaceBrImpl.getMgtChildOptionsDns3(type);
	}

	public boolean isConfigMgtChildOptionsNtp1(MgtType type){
		return interfaceBrImpl.isConfigMgtChildOptionsNtp1(type);
	}

	public String getMgtChildOptionsNtp1(MgtType type){
		return interfaceBrImpl.getMgtChildOptionsNtp1(type);
	}

	public boolean isConfigMgtChildOptionsNtp2(MgtType type){
		return interfaceBrImpl.isConfigMgtChildOptionsNtp2(type);
	}

	public String getMgtChildOptionsNtp2(MgtType type){
		return interfaceBrImpl.getMgtChildOptionsNtp2(type);
	}

	public boolean isConfigMgtChildOptionsPop3(MgtType type){
		return interfaceBrImpl.isConfigMgtChildOptionsPop3(type);
	}

	public String getMgtChildOptionsPop3(MgtType type){
		return interfaceBrImpl.getMgtChildOptionsPop3(type);
	}

	public boolean isConfigMgtChildOptionsSmtp(MgtType type){
		return interfaceBrImpl.isConfigMgtChildOptionsSmtp(type);
	}

	public String getMgtChildOptionsSmtp(MgtType type){
		return interfaceBrImpl.getMgtChildOptionsSmtp(type);
	}

	public boolean isConfigMgtChildOptionsWins1(MgtType type){
		return interfaceBrImpl.isConfigMgtChildOptionsWins1(type);
	}

	public String getMgtChildOptionsWins1(MgtType type){
		return interfaceBrImpl.getMgtChildOptionsWins1(type);
	}

	public boolean isConfigMgtChildOptionsWins2(MgtType type){
		return interfaceBrImpl.isConfigMgtChildOptionsWins2(type);
	}

	public String getMgtChildOptionsWins2(MgtType type){
		return interfaceBrImpl.getMgtChildOptionsWins2(type);
	}

	public boolean isConfigMgtChildOptionsLogsrv(MgtType type){
		return interfaceBrImpl.isConfigMgtChildOptionsLogsrv(type);
	}

	public String getMgtChildOptionsLogsrv(MgtType type){
		return interfaceBrImpl.getMgtChildOptionsLogsrv(type);
	}

	public int getMgtChildOptionsCustomSize(MgtType type){
		return interfaceBrImpl.getMgtChildOptionsCustomSize(type);
	}

	public int getMgtChildOptionsCustomName(MgtType type, int index){
		return interfaceBrImpl.getMgtChildOptionsCustomName(type, index);
	}

	public boolean isConfigMgtChildOptionsCustomInteger(MgtType type, int index){
		return interfaceBrImpl.isConfigMgtChildOptionsCustomInteger(type, index);
	}

	public int getMgtChildOptionsCustomIntegerValue(MgtType type, int index){
		return interfaceBrImpl.getMgtChildOptionsCustomIntegerValue(type, index);
	}

	public boolean isConfigMgtChildOptionsCustomIp(MgtType type, int index){
		return interfaceBrImpl.isConfigMgtChildOptionsCustomIp(type, index);
	}

	public String getMgtChildOptionsCustomIpValue(MgtType type, int index){
		return interfaceBrImpl.getMgtChildOptionsCustomIpValue(type, index);
	}

	public boolean isConfigMgtChildOptionsCustomString(MgtType type, int index){
		return interfaceBrImpl.isConfigMgtChildOptionsCustomString(type, index);
	}

	public boolean isConfigMgtChildOptionsCustomHex(MgtType type, int index){
		return interfaceBrImpl.isConfigMgtChildOptionsCustomHex(type, index);
	}

	public String getMgtChildOptionsCustomStringValue(MgtType type, int index){
		return interfaceBrImpl.getMgtChildOptionsCustomStringValue(type, index);
	}

	public String getMgtChildOptionsCustomHexValue(MgtType type, int index){
		String resultStr = interfaceBrImpl.getMgtChildOptionsCustomHexValue(type, index);
		return resultStr == null ? resultStr : resultStr.toUpperCase();
	}

	public boolean isConfigMgtDnsServer(InterfaceProfileInt.MgtType type){
		return interfaceBrImpl != null && interfaceBrImpl.isConfigMgtDnsServer(type);
	}

	public boolean isEnableMgtDnsServer(InterfaceProfileInt.MgtType type){
		return interfaceBrImpl.isEnableMgtDnsServer(type);
	}

	public MgtDnsServerModeValue getMgtDnsServerMode(InterfaceProfileInt.MgtType type){
		return interfaceBrImpl.getMgtDnsServerMode(type);
	}

	public int getIntDomainNameSize(InterfaceProfileInt.MgtType type){
		return interfaceBrImpl.getIntDomainNameSize(type);
	}

	public String getIntDomainName(InterfaceProfileInt.MgtType type, int index){
		return interfaceBrImpl.getIntDomainName(type, index);
	}

	public String getIntDnsServer(InterfaceProfileInt.MgtType type, int index){
		return interfaceBrImpl.getIntDnsServer(type, index);
	}

	public boolean isConfigDnsIntResolve(InterfaceProfileInt.MgtType type){
		return interfaceBrImpl != null && interfaceBrImpl.isConfigDnsIntResolve(type);
	}

	public boolean isConfigDnsExtResolve(InterfaceProfileInt.MgtType type){
		return interfaceBrImpl != null && interfaceBrImpl.isConfigDnsExtResolve(type);
	}

	public String getIntResolveDns1(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		return interfaceBrImpl.getIntResolveDns1(type);
	}

	public String getIntResolveDns2(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		return interfaceBrImpl.getIntResolveDns2(type);
	}

	public String getIntResolveDns3(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		return interfaceBrImpl.getIntResolveDns3(type);
	}

	public String getExtResolveDns1(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		return interfaceBrImpl.getExtResolveDns1(type);
	}

	public String getExtResolveDns2(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		return interfaceBrImpl.getExtResolveDns2(type);
	}

	public String getExtResolveDns3(InterfaceProfileInt.MgtType type) throws CreateXMLException{
		return interfaceBrImpl.getExtResolveDns3(type);
	}

	public boolean isConfigMgtX(InterfaceProfileInt.MgtType type){
		if(hiveAp.isBranchRouter()){
			return interfaceBrImpl.isConfigMgtX(type);
		}else{
			return true;
		}
	}
	
	/** end switch used for Router */
	
	private void orderInterfaceDataInfo(List<InterfaceDataInfo> listInfo){
		Collections.sort(listInfo, new Comparator<InterfaceDataInfo>() {
			@Override
			public int compare(InterfaceDataInfo o1, InterfaceDataInfo o2) {
				if(o1.getdInterface() != null && o2.getdInterface() != null){
					return o1.getdInterface().getDeviceIfType() - o2.getdInterface().getDeviceIfType();
				}else if(o1.getPortProfile() != null && o2.getPortProfile() != null){
					return o1.getPortProfile().getPortChannel() - o2.getPortProfile().getPortChannel();
				}else{
					return 0;
				}
			}
		});
	}
	
	public boolean isConfigInterfaceManage(){
		return hiveAp.getConfigTemplate().getDeviceServiceFilter() != null;
	}
	
	public boolean isEnableManageSnmp(){
		return hiveAp.getConfigTemplate().getDeviceServiceFilter().getEnableSNMP();
	}
	
	public boolean isEnableManageSSH(){
		return hiveAp.getConfigTemplate().getDeviceServiceFilter().getEnableSSH();
	}
	
	public boolean isEnableManageTelnet(){
		return hiveAp.getConfigTemplate().getDeviceServiceFilter().getEnableTelnet();
	}
	
	public boolean isEnableManagePing(){
		return hiveAp.getConfigTemplate().getDeviceServiceFilter().getEnablePing();
	}
	
	public int getNatPolicySize() {
		return interfaceBrImpl.getNatPolicySize();
	}
	
	public String getNatPolicyName(int index) {
		return interfaceBrImpl.getNatPolicyName(index);
	}

	public boolean isNatPolicyConfigMatch(int index) {
		return interfaceBrImpl.isNatPolicyConfigMatch(index);
	}

	public boolean isNatPolicyConfigVirtualHost(int index) {
		return interfaceBrImpl.isNatPolicyConfigVirtualHost(index);
	}

	public String getNatPolicyMatchInsideValue(int index) {
		return interfaceBrImpl.getNatPolicyMatchInsideValue(index);
	}

	public String getNatPolicyMatchOutsideValue(int index) {
		return interfaceBrImpl.getNatPolicyMatchOutsideValue(index);
	}
	
	public String getNatPolicyVhostInsideHostValue(int index) {
		return interfaceBrImpl.getNatPolicyVhostInsideHostValue(index);
	}

	public String getNatPolicyVhostInsidePortValue(int index) {
		return interfaceBrImpl.getNatPolicyVhostInsidePortValue(index);
	}

	public String getNatPolicyVhostOutsidePortValue(int index) {
		return interfaceBrImpl.getNatPolicyVhostOutsidePortValue(index);
	}

	public String getNatPolicyVhostProtocolValue(int index) {
		return interfaceBrImpl.getNatPolicyVhostProtocolValue(index);
	}
	
	public int getNatPolicyNameForPortForwardingSize() {
		return interfaceBrImpl.getNatPolicyNameForPortForwardingSize();
	}
	
	public String getNatPolicyNameForPortForwarding(int index) {
		return interfaceBrImpl.getNatPolicyNameForPortForwarding(index);
	}
	
	public int getNatPolicyNameForSubNetworkSize() {
		return interfaceBrImpl.getNatPolicyNameForSubNetworkSize();
	}
	
	public String getNatPolicyNameForSubNetwork(int index) {
		return interfaceBrImpl.getNatPolicyNameForSubNetwork(index);
	}
	
	public boolean isEnableWanNatPolicy(InterType type) {
		return interfaceBrImpl.isEnableWanNatPolicy(type);
	}
	
	public boolean isConfigClientReport(DeviceInfType type, int index) {
		PortBasicProfile portBase = interfaceMap.get(type).get(index).getPortProfile();
		return portBase != null && portBase.getAccessProfile() != null && 
				portBase.getAccessProfile().getPortType() != PortAccessProfile.PORT_TYPE_WAN && 
				portBase.getAccessProfile().getPortType() != PortAccessProfile.PORT_TYPE_MONITOR;
	}
	
	public boolean isEnableClientReport(DeviceInfType type, int index) {
		if(hiveAp.isOverrideNetworkPolicySetting()){
			return interfaceMap.get(type).get(index).getdInterface().isClientReporting();
		}else{
			return interfaceMap.get(type).get(index).getPortProfile().getAccessProfile().isEnabledClientReport();
		}
	}
	
	public boolean isConfigInfDescription(DeviceInfType type, int index) {
		String descript = getInfDescription(type, index);
		return descript != null && !"".equals(descript);
	}
	
	public String getInfDescription(DeviceInfType type, int index) {
		InterfaceDataInfo interfaceInfo = interfaceMap.get(type).get(index);
		if(hiveAp.isOverrideNetworkPolicySetting()){
			return interfaceInfo.getdInterface().getPortDescription();
		}else if(interfaceInfo.getPortProfile() != null && interfaceInfo.getPortProfile().getAccessProfile() != null){
			return interfaceInfo.getPortProfile().getAccessProfile().getPortDescription();
		}else{
			return null;
		}
	}
}
