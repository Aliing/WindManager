package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.InterfaceProfileInt;
import com.ah.be.config.create.source.InterfaceProfileInt.InterType;
import com.ah.be.config.create.source.InterfaceProfileInt.InterfaceWifi;
import com.ah.be.config.create.source.InterfaceProfileInt.MgtType;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.util.Tracer;
import com.ah.xml.be.config.AhManage;
import com.ah.xml.be.config.AntennaDefault;
import com.ah.xml.be.config.AssistantMode;
import com.ah.xml.be.config.DhcpClientTimeout;
import com.ah.xml.be.config.DhcpServerEnable;
import com.ah.xml.be.config.DhcpServerOptionsCustom;
import com.ah.xml.be.config.DhcpServerOptionsDefaultGateway;
import com.ah.xml.be.config.DnsServerEnable;
import com.ah.xml.be.config.Eth;
import com.ah.xml.be.config.EthDuplex;
import com.ah.xml.be.config.EthxDhcp;
import com.ah.xml.be.config.EthxDhcpClient;
import com.ah.xml.be.config.EthxModeWan;
import com.ah.xml.be.config.EthxPse;
import com.ah.xml.be.config.EthxPseMode;
import com.ah.xml.be.config.EthxPsePriority;
import com.ah.xml.be.config.GethLinkDiscoveryCdpMode;
import com.ah.xml.be.config.GethLinkDiscoveryLldpMode;
import com.ah.xml.be.config.GethMode;
import com.ah.xml.be.config.GethModeWan;
import com.ah.xml.be.config.GethSecurityObject;
import com.ah.xml.be.config.GethStormControlMode;
import com.ah.xml.be.config.InterfaceAllowedVlan;
import com.ah.xml.be.config.InterfaceAttribute;
import com.ah.xml.be.config.InterfaceAutoMdix;
import com.ah.xml.be.config.InterfaceClientReport;
import com.ah.xml.be.config.InterfaceDuplex;
import com.ah.xml.be.config.InterfaceEthxNativeVlan;
import com.ah.xml.be.config.InterfaceFlowControl;
import com.ah.xml.be.config.InterfaceFlowControlValue;
import com.ah.xml.be.config.InterfaceGethLinkDiscovery;
import com.ah.xml.be.config.InterfaceGethLinkDiscoveryCdp;
import com.ah.xml.be.config.InterfaceGethLinkDiscoveryLldp;
import com.ah.xml.be.config.InterfaceGethPse;
import com.ah.xml.be.config.InterfaceGigabitEthernet;
import com.ah.xml.be.config.InterfaceIpHelper;
import com.ah.xml.be.config.InterfaceMacLearning;
import com.ah.xml.be.config.InterfaceMode;
import com.ah.xml.be.config.InterfaceObj;
import com.ah.xml.be.config.InterfacePortChannel;
import com.ah.xml.be.config.InterfaceSpanningTree;
import com.ah.xml.be.config.InterfaceSpanningTreeMstInstance;
import com.ah.xml.be.config.InterfaceSpeed;
import com.ah.xml.be.config.InterfaceStormControl;
import com.ah.xml.be.config.InterfaceStormControlRateLimit;
import com.ah.xml.be.config.InterfaceSwitchport;
import com.ah.xml.be.config.InterfaceSwitchportAccess;
import com.ah.xml.be.config.InterfaceSwitchportMode;
import com.ah.xml.be.config.InterfaceSwitchportModeValue;
import com.ah.xml.be.config.InterfaceSwitchportNative;
import com.ah.xml.be.config.InterfaceSwitchportTrunk;
import com.ah.xml.be.config.InterfaceSwitchportTrunkAllow;
import com.ah.xml.be.config.InterfaceSwitchportTrunkAllowVlan;
import com.ah.xml.be.config.InterfaceVlan;
import com.ah.xml.be.config.InterfaceVlanx;
import com.ah.xml.be.config.Mgt;
import com.ah.xml.be.config.MgtDhcpKeepalive;
import com.ah.xml.be.config.MgtDhcpServer;
import com.ah.xml.be.config.MgtDnsResolver;
import com.ah.xml.be.config.MgtDnsServer;
import com.ah.xml.be.config.MgtDnsServerIntDomainName;
import com.ah.xml.be.config.MgtDnsServerMode;
import com.ah.xml.be.config.Mgtxy;
import com.ah.xml.be.config.RedxAggx;
import com.ah.xml.be.config.SpanningTreeBpduProtection;
import com.ah.xml.be.config.SpanningTreeBpduProtectionValue;
import com.ah.xml.be.config.StormControlType;
import com.ah.xml.be.config.Usbnet;
import com.ah.xml.be.config.UsbnetMode;
import com.ah.xml.be.config.UsbnetWan;
import com.ah.xml.be.config.Wifi;
import com.ah.xml.be.config.WifixMode;
import com.ah.xml.be.config.WifixModeWan;

/**
 * 
 * @author zhang
 *
 */
public class CreateInterfaceTree {
	
	private static final Tracer log = new Tracer(CreateInterfaceTree.class
			.getSimpleName());
	
	private final InterfaceProfileInt interfaceImpl;
	private InterfaceObj interfaceObj;
	
	private final GenerateXMLDebug oDebug;
	
	private final List<Object> interfaceChildList_1 = new ArrayList<Object>();
	private final List<Object> interfaceChildList_2 = new ArrayList<Object>();
	private final List<Object> interfaceChildList_3 = new ArrayList<Object>();
	private final List<Object> interfaceChildList_4 = new ArrayList<Object>();
	private final List<Object> wifiChildList_1 = new ArrayList<Object>();
	private final List<Object> wifiChildList_2 = new ArrayList<Object>();
	
	private final List<Object> ethChildList_1 = new ArrayList<Object>();
	private final List<Object> ethChildList_2 = new ArrayList<Object>();
	
	private final List<Object> aggRedChildList_1 = new ArrayList<Object>();

	public CreateInterfaceTree(InterfaceProfileInt interfaceImpl, GenerateXMLDebug oDebug) {
		this.interfaceImpl = interfaceImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		interfaceObj = new InterfaceObj();
		generateInterfaceLevel_1();
	}
	
	public InterfaceObj getInterfaceObj(){
		return this.interfaceObj;
	}
	
	private void generateInterfaceLevel_1() throws Exception {
		/**
		 * <interface>				InterfaceObj
		 */
		
//		/** attribute: updateTime */
//		interfaceObj.setUpdateTime(interfaceImpl.getUpdateTime());
		
		/** element: <interface>.<eth0> */
		if(interfaceImpl.isConfigEthx(InterType.eth0)){
			Eth eth0Obj = new Eth();
			setEthChildLevel(eth0Obj, InterfaceProfileInt.InterType.eth0);
			interfaceObj.setEth0(eth0Obj);
		}
		
		/** element: <interface>.<eth1> */
		oDebug.debug("/configuration/interface", 
				"eth1", GenerateXMLDebug.CONFIG_ELEMENT,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		if(interfaceImpl.isConfigEthx(InterType.eth1)){
			Eth eth1Obj = new Eth();
			setEthChildLevel(eth1Obj, InterfaceProfileInt.InterType.eth1);
			interfaceObj.setEth1(eth1Obj);
		}
		
		/** element: <interface>.<eth2> */
		oDebug.debug("/configuration/interface", 
				"eth2", GenerateXMLDebug.CONFIG_ELEMENT,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		if(interfaceImpl.isConfigEthx(InterType.eth2)){
			Eth eth2Obj = new Eth();
			setEthChildLevel(eth2Obj, InterfaceProfileInt.InterType.eth2);
			interfaceObj.setEth2(eth2Obj);
		}
		
		/** element: <interface>.<eth3> */
		oDebug.debug("/configuration/interface", 
				"eth3", GenerateXMLDebug.CONFIG_ELEMENT,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		if(interfaceImpl.isConfigEthx(InterType.eth3)){
			Eth eth3Obj = new Eth();
			setEthChildLevel(eth3Obj, InterfaceProfileInt.InterType.eth3);
			interfaceObj.setEth3(eth3Obj);
		}
		
		/** element: <interface>.<eth4> */
		oDebug.debug("/configuration/interface", 
				"eth4", GenerateXMLDebug.CONFIG_ELEMENT,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		if(interfaceImpl.isConfigEthx(InterType.eth4)){
			Eth eth4Obj = new Eth();
			setEthChildLevel(eth4Obj, InterfaceProfileInt.InterType.eth4);
			interfaceObj.setEth4(eth4Obj);
		}
		
		/** element: <interface>.<usbnet0> */
		if(interfaceImpl.isConfigInterfaceUSB()){
			Usbnet usbObj = new Usbnet();
			interfaceChildList_1.add(usbObj);
			interfaceObj.setUsbnet0(usbObj);
		}
		
		/** element: <interface>.<mgt0> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt0)){
			Mgt mgtObj = new Mgt();
			interfaceChildList_1.add(mgtObj);
			interfaceObj.setMgt0(mgtObj);
		}
		
		/** element: <interface>.<wifi0> */
		if(interfaceImpl.isConfigWifi0()){
			Wifi wifiObj = new Wifi();
			setWifiObj(wifiObj, InterfaceProfileInt.InterfaceWifi.wifi0);
			interfaceObj.setWifi0(wifiObj);
		}
		
		/** element: <interface>.<wifi1> */
		oDebug.debug("/configuration/interface", 
				"wifi1", GenerateXMLDebug.CONFIG_ELEMENT,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		if(interfaceImpl.isConfigWifi1()){
			Wifi wifiObj1 = new Wifi();
			setWifiObj(wifiObj1, InterfaceProfileInt.InterfaceWifi.wifi1);
			interfaceObj.setWifi1(wifiObj1);
		}
		
		/** element: <interface>.<red0> */
		oDebug.debug("/configuration/interface",
				"red0", GenerateXMLDebug.CONFIG_ELEMENT,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		if(interfaceImpl.isConfigInterRed0()){
			RedxAggx red0Obj = new RedxAggx();
			setRedxAggxChildLevel_1(red0Obj, InterfaceProfileInt.InterType.red0);
			interfaceObj.setRed0(red0Obj);
		}
		
		/** element: <interface>.<agg0> */
		oDebug.debug("/configuration/interface",
				"agg0", GenerateXMLDebug.CONFIG_ELEMENT,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		if(interfaceImpl.isConfigInterAgg0()){
			RedxAggx agg0Obj = new RedxAggx();
			setRedxAggxChildLevel_1(agg0Obj, InterfaceProfileInt.InterType.agg0);
			interfaceObj.setAgg0(agg0Obj);
		}
		
		/** element: <interface>.<mgt0.1> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt01)){
			interfaceObj.setMgt01(createMgtxy(InterfaceProfileInt.MgtType.mgt01));
		}
		
		/** element: <interface>.<mgt0.2> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt02)){
			interfaceObj.setMgt02(createMgtxy(InterfaceProfileInt.MgtType.mgt02));
		}
		
		/** element: <interface>.<mgt0.3> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt03)){
			interfaceObj.setMgt03(createMgtxy(InterfaceProfileInt.MgtType.mgt03));
		}
		
		/** element: <interface>.<mgt0.4> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt04)){
			interfaceObj.setMgt04(createMgtxy(InterfaceProfileInt.MgtType.mgt04));
		}
		
		/** element: <interface>.<mgt0.5> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt05)){
			interfaceObj.setMgt05(createMgtxy(InterfaceProfileInt.MgtType.mgt05));
		}
		
		/** element: <interface>.<mgt0.6> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt06)){
			interfaceObj.setMgt06(createMgtxy(InterfaceProfileInt.MgtType.mgt06));
		}
		
		/** element: <interface>.<mgt0.7> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt07)){
			interfaceObj.setMgt07(createMgtxy(InterfaceProfileInt.MgtType.mgt07));
		}
		
		/** element: <interface>.<mgt0.8> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt08)){
			interfaceObj.setMgt08(createMgtxy(InterfaceProfileInt.MgtType.mgt08));
		}
		
		/** element: <interface>.<mgt0.9> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt09)){
			interfaceObj.setMgt09(createMgtxy(InterfaceProfileInt.MgtType.mgt09));
		}
		
		/** element: <interface>.<mgt0.10> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt010)){
			interfaceObj.setMgt010(createMgtxy(InterfaceProfileInt.MgtType.mgt010));
		}
		
		/** element: <interface>.<mgt0.11> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt011)){
			interfaceObj.setMgt011(createMgtxy(InterfaceProfileInt.MgtType.mgt011));
		}
		
		/** element: <interface>.<mgt0.12> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt012)){
			interfaceObj.setMgt012(createMgtxy(InterfaceProfileInt.MgtType.mgt012));
		}
		
		/** element: <interface>.<mgt0.13> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt013)){
			interfaceObj.setMgt013(createMgtxy(InterfaceProfileInt.MgtType.mgt013));
		}
		
		/** element: <interface>.<mgt0.14> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt014)){
			interfaceObj.setMgt014(createMgtxy(InterfaceProfileInt.MgtType.mgt014));
		}
		
		/** element: <interface>.<mgt0.15> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt015)){
			interfaceObj.setMgt015(createMgtxy(InterfaceProfileInt.MgtType.mgt015));
		}
		
		/** element: <interface>.<mgt0.16> */
		if(interfaceImpl.isConfigMgtX(InterfaceProfileInt.MgtType.mgt016)){
			interfaceObj.setMgt016(createMgtxy(InterfaceProfileInt.MgtType.mgt016));
		}
		
		/** element: <interface>.<gigabitethernet> */
		for(int index=0; index<interfaceImpl.getSRInfeSize(DeviceInfType.Gigabit); index++){
			interfaceObj.getEth().add(this.createInterfaceGigabitEthernet(index));
		}
		
		/** element: <interface>.<sfp> */
		for(int index=0; index<interfaceImpl.getSRInfeSize(DeviceInfType.SFP); index++){
			interfaceObj.getEth().add(this.createInterfaceSfp(index));
		}
		
		/** element: <interface>.<port-channel> */
		for(int index=0; index<interfaceImpl.getSRInfeSize(DeviceInfType.PortChannel); index++){
			interfaceObj.getAgg().add(this.createInterfacePortChannel(index));
		}
		
		for(int index=0; index<interfaceImpl.getInterfaceVlansize(); index++){
			MgtType type = interfaceImpl.getMgtTypeType(index);
			
			/** element: <interface>.<_vlan-id> */
			interfaceObj.getVlanId().add(CLICommonFunc.createAhNameActValue(
					String.valueOf(interfaceImpl.getMgtChildVlan(type)), CLICommonFunc.getYesDefault()));
			
			/** element: <interface>.<vlan> */
			interfaceObj.getVlan().add(this.createInterfaceVlanx(type));
		}
		
		/** element: <interface>.<mtu> */
		interfaceObj.setMtu(CLICommonFunc.createAhIntActObj(
				interfaceImpl.getMTUValue(), CLICommonFunc.getYesDefault()));
		
		/** element: <interface>.<manage> */
		if(interfaceImpl.isConfigInterfaceManage()){
			AhManage manageObj = new AhManage();
			interfaceChildList_1.add(manageObj);
			interfaceObj.setManage(manageObj);
		}
		
		generateInterfaceLevel_2();
	}
	
	private void generateInterfaceLevel_2() throws Exception {
		/**
		 * <interface>.<mgt0>			Mgt
		 * <interface>.<usbnet0>		Usbnet
		 * <interface>.<manage>			AhManage
		 */
		for(Object childObj : interfaceChildList_1 ){
			
			/** element: <interface>.<mgt0> */
			if(childObj instanceof Mgt){
				Mgt mgtObj = (Mgt)childObj;
				
				/** element: <interface>.<mgt0>.<mtu> */
				mgtObj.setMtu(CLICommonFunc.createAhIntActObj(interfaceImpl.getMgt0MTUValue(), true));
				
				/** element: <interface>.<mgt0>.<dhcp> */
				Mgt.Dhcp dhcpObj = new Mgt.Dhcp();
				interfaceChildList_2.add(dhcpObj);
				mgtObj.setDhcp(dhcpObj);
				
				/** element: <interface>.<mgt0>.<ip> */
				oDebug.debug("/configuration/interface/mgt0",
						"ip", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigMgtIp()){
					
					oDebug.debug("/configuration/interface/mgt0",
							"ip", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					mgtObj.setIp(
							CLICommonFunc.createAhStringActQuoteProhibited(interfaceImpl.getMgtIpAndMask(), 
									CLICommonFunc.getYesDefault(),CLICommonFunc.getYesDefault())
					);
				}
				
				/** element: <interface>.<mgt0>.<hive> */
				oDebug.debug("/configuration/interface/mgt0",
						"hive", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigMgtHive()){
					
					oDebug.debug("/configuration/interface/mgt0",
							"hive", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					mgtObj.setHive(CLICommonFunc.createAhNameActObj(interfaceImpl.getMgtBindHive(), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <interface>.<mgt0>.<vlan> */
				if(interfaceImpl.isConfigMgtVlan()){
					oDebug.debug("/configuration/interface/mgt0",
							"vlan", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					Object[][] vlanParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getMgtVlanId()},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					mgtObj.setVlan(
							(InterfaceVlan)CLICommonFunc.createObjectWithName(InterfaceVlan.class, vlanParm)
					);
				}
				
				
				/** element: <interface>.<mgt0>.<native-vlan> */
				oDebug.debug("/configuration/interface/mgt0",
						"native-vlan", GenerateXMLDebug.SET_VALUE,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
//				if(interfaceImpl.isConfigMgtNativeVlan()){
				Object[][] nativeVlanParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getMgtNativeVlan()},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				mgtObj.setNativeVlan(
						(Mgt.NativeVlan)CLICommonFunc.createObjectWithName(Mgt.NativeVlan.class, nativeVlanParm)
				);
//				}
				
				/** element: <interface>.<mgt0>.<default-ip-prefix> */
				oDebug.debug("/configuration/interface/mgt0",
						"default-ip-prefix", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigMgtDefaultIpPrefix()){
					
					oDebug.debug("/configuration/interface/mgt0",
							"default-ip-prefix", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					Object[][] ipPrefix = {
							{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getMgtDefaultIpPrefix()},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					mgtObj.setDefaultIpPrefix(
							(Mgt.DefaultIpPrefix)CLICommonFunc.createObjectWithName(Mgt.DefaultIpPrefix.class, ipPrefix)
					);
				}
				
				/** element: <interface>.<mgt0>.<old-default-ip-prefix> */
				oDebug.debug("/configuration/interface/mgt0",
						"old-default-ip-prefix", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigMgtDefaultIpPrefix()){
					
					oDebug.debug("/configuration/interface/mgt0",
							"old-default-ip-prefix", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					Object[][] ipPrefix = {
							{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getMgtOldDefaultIpPrefix()},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					mgtObj.setOldDefaultIpPrefix(
							(Mgt.OldDefaultIpPrefix)CLICommonFunc.createObjectWithName(Mgt.OldDefaultIpPrefix.class, ipPrefix)
					);
				}
				
				if(interfaceImpl.isConfigMgtChild(MgtType.mgt0)){
					
					/** element: <interface>.<mgt0>.<ip-helper> */
					oDebug.debug("/configuration/interface/mgt0",
							"ip-helper", GenerateXMLDebug.CONFIG_ELEMENT,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					if(interfaceImpl.isConfigMgtChildIpHelper(MgtType.mgt0)){
						mgtObj.setIpHelper(this.createInterfaceIpHelper(MgtType.mgt0));
					}
					
					/** element: <interface>.<mgt0>.<dhcp-server> */
					mgtObj.setDhcpServer(this.createMgtDhcpServer(MgtType.mgt0));
					
				}
				
				/** element: <interface>.<mgt0>.<dns-server> */
				if(interfaceImpl.isConfigMgtDnsServer(MgtType.mgt0)){
					mgtObj.setDnsServer(this.createMgtDnsServer(MgtType.mgt0));
				}
				
			}
			
			/** element: <interface>.<usbnet0> */
			if(childObj instanceof Usbnet){
				Usbnet usbObj = (Usbnet)childObj;
				
				/** element: <interface>.<usbnet0>.<mode> */
				UsbnetMode mode = new UsbnetMode();
				interfaceChildList_2.add(mode);
				usbObj.setMode(mode);
			}
			
			/** element: <interface>.<manage> */
			if(childObj instanceof AhManage){
				AhManage manageObj = (AhManage)childObj;
				
				/** element: <interface>.<manage>.<SNMP> */
				manageObj.setSNMP(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableManageSnmp()));
				
				/** element: <interface>.<manage>.<SSH> */
				manageObj.setSSH(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableManageSSH()));
				
				/** element: <interface>.<manage>.<Telnet> */
				manageObj.setTelnet(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableManageTelnet()));
				
				/** element: <interface>.<manage>.<ping> */
				manageObj.setPing(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableManagePing()));
			}
		}
		generateInterfaceLevel_3();
	}
	
	private void generateInterfaceLevel_3() throws Exception {
		/**
		 * <interface>.<mgt0>.<dhcp>			Mgt.Dhcp
		 * <interface>.<usbnet0>.<mode>			UsbnetMode
		 */
		for(Object childObj : interfaceChildList_2 ){
			
			/** element: <interface>.<mgt0>.<dhcp> */
			if(childObj instanceof Mgt.Dhcp){
				Mgt.Dhcp dhcpObj = (Mgt.Dhcp)childObj;
				
				/** element: <interface>.<mgt0>.<dhcp>.<client> */
				Mgt.Dhcp.Client clientObj = new Mgt.Dhcp.Client();
				interfaceChildList_3.add(clientObj);
				dhcpObj.setClient(clientObj);
				
				/** element: <interface>.<mgt0>.<dhcp>.<keepalive> */
				if(interfaceImpl.isConfigMgt0DhcpKeepalive()){
					MgtDhcpKeepalive dhcpKeepObj = new MgtDhcpKeepalive();
					interfaceChildList_3.add(dhcpKeepObj);
					dhcpObj.setKeepalive(dhcpKeepObj);
				}
				
			}
			
			/** element: <interface>.<usbnet0>.<mode> */
			if(childObj instanceof UsbnetMode){
				UsbnetMode mode = (UsbnetMode)childObj;
				
				/** element: <interface>.<usbnet0>.<mode>.<wan> */
				UsbnetWan wanObj = new UsbnetWan();
				interfaceChildList_3.add(wanObj);
				mode.setWan(wanObj);
			}
		}
		generateInterfaceLevel_4();
	}
	
	private void generateInterfaceLevel_4() throws Exception {
		/**
		 * <interface>.<mgt0>.<dhcp>.<client>				Mgt.Dhcp.Client
		 * <interface>.<mgt0>.<dhcp>.<keepalive> 			MgtDhcpKeepalive
		 * <interface>.<usbnet0>.<mode>.<wan>				UsbnetWan
		 */
		for(Object childObj : interfaceChildList_3){
			
			/** element: <interface>.<mgt0>.<dhcp>.<client> */
			if(childObj instanceof Mgt.Dhcp.Client){
				Mgt.Dhcp.Client clientObj = (Mgt.Dhcp.Client)childObj;
				
				/** element: <interface>.<mgt0>.<dhcp>.<client>.<cr> */
				oDebug.debug("/configuration/interface/mgt0/dhcp/client",
						"cr", GenerateXMLDebug.SET_OPERATION,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				clientObj.setCr(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableMgtDhcp()));
				
				if(interfaceImpl.isEnableMgtDhcp()){
					
					/** element: <interface>.<mgt0>.<dhcp>.<client>.<fallback-to-static-ip> */
					oDebug.debug("/configuration/interface/mgt0/dhcp/client",
							"fallback-to-static-ip", GenerateXMLDebug.SET_OPERATION,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					clientObj.setFallbackToStaticIp(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableDhcpFallBack()));
					
					/** element: <interface>.<mgt0>.<dhcp>.<client>.<timeout> */
					oDebug.debug("/configuration/interface/mgt0/dhcp/client",
							"timeout", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					Object[][] timeOutParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getDhcpTimeOutt()},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					clientObj.setTimeout(
							(DhcpClientTimeout)CLICommonFunc.createObjectWithName(DhcpClientTimeout.class, timeOutParm)
					);
					
					/** element: <interface>.<mgt0>.<dhcp>.<client>.<address-only> */
					oDebug.debug("/configuration/interface/mgt0/dhcp/client",
							"address-only", GenerateXMLDebug.CONFIG_ELEMENT,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					if(interfaceImpl.isConfigDhcpAddressOnly()){
						
						oDebug.debug("/configuration/interface/mgt0/dhcp/client",
								"address-only", GenerateXMLDebug.SET_VALUE,
								interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
						clientObj.setAddressOnly(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableDhcpAddressOnly()));
					}
					
				}
				
			}
			
			/** element: <interface>.<mgt0>.<dhcp>.<keepalive> */
			if(childObj instanceof MgtDhcpKeepalive){
				MgtDhcpKeepalive dhcpKeepObj = (MgtDhcpKeepalive)childObj;
				
				/** element: <interface>.<mgt0>.<dhcp>.<keepalive>.<vlan> */
				oDebug.debug("/configuration/interface/mgt0/dhcp/keepalive",
						"vlan", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				
				for(int index = 0; index < interfaceImpl.getMgt0DhcpKeepaliveVlanSize();index++){
					dhcpKeepObj.getVlan().add(CLICommonFunc.createAhNameActValueQuoteProhibited(interfaceImpl.getMgt0DhcpKeepaliveValn(index),
							CLICommonFunc.getYesDefault(),CLICommonFunc.getYesDefault()));
				}
			}
			
			/** element: <interface>.<usbnet0>.<mode>.<wan> */
			if(childObj instanceof UsbnetWan){
				UsbnetWan wanObj = (UsbnetWan)childObj;
				
				/** element: <interface>.<usbnet0>.<mode>.<wan>.<nat> */
				wanObj.setNat(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableWanNat(InterType.usb)));
				
				/** element: <interface>.<usbnet0>.<mode>.<wan>.<nat-policy> */
				if (interfaceImpl.isEnableWanNatPolicy(InterType.usb)) {
					for (int i = 0 ; i < interfaceImpl.getNatPolicyNameForPortForwardingSize(); i++) {
						wanObj.getNatPolicy().add(CLICommonFunc.createAhNameActValue(interfaceImpl.getNatPolicyNameForPortForwarding(i), true));
					}
				}
				
				/** element: <interface>.<usbnet0>.<mode>.<wan>.<priority> */
				if (interfaceImpl.getInterfacePriority(InterType.usb) > 0) {
					wanObj.setPriority(CLICommonFunc.createAhIntNameActObj(
							interfaceImpl.getInterfacePriority(InterType.usb), CLICommonFunc.getYesDefault()));
				}
			}
		}
		interfaceChildList_3.clear();
		generateInterfaceLevel_5();
	}
	
	private void generateInterfaceLevel_5() throws Exception{
		/**
		 * <interface>.<mgt0>.<dhcp-server>.<options>.<default-gateway>			DhcpServerOptionsDefaultGateway
		 */
		for(Object childObj : interfaceChildList_4){
			
			/** element: <interface>.<mgt0>.<dhcp-server>.<options>.<default-gateway> */
			if(childObj instanceof DhcpServerOptionsDefaultGateway){
				
				DhcpServerOptionsDefaultGateway defaultGatewayObj = (DhcpServerOptionsDefaultGateway)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/interface/mgt0/dhcp-server/options",
						"default-gateway", GenerateXMLDebug.SET_VALUE,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				defaultGatewayObj.setValue(interfaceImpl.getMgtChildOptionsDefaultGateway(InterfaceProfileInt.MgtType.mgt0));
				
				/** attribute: operation */
				defaultGatewayObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <interface>.<mgt0>.<dhcp-server>.<options>.<default-gateway>.<nat-support> */
				oDebug.debug("/configuration/interface/mgt0/dhcp-server/options/default-gateway",
						"nat-support", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isMgtDhcpNatSupport(InterfaceProfileInt.MgtType.mgt0)){
					defaultGatewayObj.setNatSupport(CLICommonFunc.getAhOnlyAct(interfaceImpl.isMgtDhcpNatSupport(InterfaceProfileInt.MgtType.mgt0)));
				}
			}
		}
		interfaceChildList_4.clear();
	}
	
	private void setWifiObj(Wifi wifiObj, InterfaceProfileInt.InterfaceWifi wifi) throws Exception {
		/**
		 * <interface>.<wifi>			Wifi
		 */
		
//		/** attribute: updatTime */
//		wifiObj.setUpdateTime(interfaceImpl.getWifiUpdateTime());
		
		/** element: <interface>.<wifi>.<mode> */
		WifixMode wifixMode = new WifixMode();
		wifiChildList_1.add(wifixMode);
		wifiObj.setMode(wifixMode);
		
		/** element: <interface>.<wifi>.<radio> */
		Wifi.Radio interRadio = new Wifi.Radio();
		wifiChildList_1.add(interRadio);
		wifiObj.setRadio(interRadio);
		
		/** element: <interface>.<wifi>.<wlan-idp> */
		oDebug.debug("/configuration/interface/"+wifi.name(), 
				"wlan-idp", GenerateXMLDebug.CONFIG_ELEMENT,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		if(interfaceImpl.isConfigureInterfaceWlan(wifi) ){
			Wifi.WlanIdp wlanIdpObj = new Wifi.WlanIdp();
			wifiChildList_1.add(wlanIdpObj);
			wifiObj.setWlanIdp(wlanIdpObj);
		}
		
		/** element: <interface>.<wifi>.<ssid> */
		oDebug.debug("/configuration/interface/"+wifi.name(), 
				"ssid", GenerateXMLDebug.CONFIG_ELEMENT,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		if( interfaceImpl.isWifiBindSsid(wifi) ){
			int count = 0;
			for(int i=0; i<interfaceImpl.getInterfaceWifiSsidSize(); i++ ){
				if(interfaceImpl.isConfigWifiSsid(wifi, i)){
					Wifi.Ssid ssidObj = new Wifi.Ssid();
					setWifiSsid(ssidObj, wifi, i);
					wifiObj.getSsid().add(ssidObj);
					count ++;
				}
			}
			if(count > 8 && NmsUtil.compareSoftwareVersion(interfaceImpl.getApVersion(), "3.4.3.0") < 0){
				String errMsg = NmsUtil.getUserMessage("error.be.config.create.tooManySsid");
				log.error("GenerateXML", errMsg);
				throw new CreateXMLException(errMsg);
			}
			if(count > 8 && NmsUtil.compareSoftwareVersion(interfaceImpl.getApVersion(), "3.4.3.0") < 0){
				String errMsg = NmsUtil.getUserMessage("error.be.config.create.tooManySsid");
				log.error("GenerateXML", errMsg);
				throw new CreateXMLException(errMsg);
			}

			short ssidSupportNumber = interfaceImpl.getSsidSupportedUnderDual();
			if(count > ssidSupportNumber && interfaceImpl.getInterWifiMode(wifi) == InterfaceProfileInt.InterfaceWifiModeValue.DUAL){
				for(int i=count; i > ssidSupportNumber; i--){
					wifiObj.getSsid().remove(i-1);
				}
			}
		}
		
//		/** element: <interface>.<wifi>.<link-discovery> */
//		oDebug.debug("/configuration/interface/"+wifi.name(), 
//				"link-discover", GenerateXMLDebug.CONFIG_ELEMENT,
//				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
//		if(interfaceImpl.isConfigLinkDiscovery()){
//			wifiObj.setLinkDiscovery(this.CreateInterfaceLinkDiscovery(wifi));
//		}
		
		/** element: <interface>.<wifi>.<hive> */
		Wifi.Hive hiveObj = new Wifi.Hive();
		wifiChildList_1.add(hiveObj);
		wifiObj.setHive(hiveObj);
		
		/** element: <interface>.<wifi>.<client-mode> */
//		WifixClientMode clientModeObj = new WifixClientMode();
//		wifiChildList_1.add(clientModeObj);
//		wifiObj.setClientMode(clientModeObj);

		setWifiObj_1(wifi);
	}
	
	private void setWifiObj_1(InterfaceProfileInt.InterfaceWifi wifi) throws Exception {
		/**
		 * <interface>.<wifi>.<mode>			Wifi.Mode
		 * <interface>.<wifi>.<radio>			Wifi.Radio
		 * <interface>.<wifi>.<wlan-idp>		Wifi.WlanIdp
		 * <interface>.<wifi>.<hive>			Wifi.Hive
		 * <interface>.<wifi>.<client-mode>		WifixClientMode
		 */
		for(Object childObj : wifiChildList_1){
			
			/** element: <interface>.<wifi>.<mode> */
			if(childObj instanceof WifixMode){
				WifixMode wifiModeObj = (WifixMode)childObj;
				
				/** attribute: operation */
				wifiModeObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				if(interfaceImpl.isWifiModeWan(wifi)){
					
					/** element: <interface>.<wifi>.<mode>.<wan> */
					WifixModeWan wanObj = new WifixModeWan();
					wifiChildList_2.add(wanObj);
					wifiModeObj.setWanClient(wanObj);
				}else if(interfaceImpl.isWifiModeAccess(wifi)){
					
					/** element: <interface>.<wifi>.<mode>.<access> */
					wifiModeObj.setAccess("");
				}else if(interfaceImpl.isWifiModeBackhaul(wifi)){
					
					/** element: <interface>.<wifi>.<mode>.<backhaul> */
					wifiModeObj.setBackhaul("");
				}else if(interfaceImpl.isWifiModeDual(wifi)){
					
					/** element: <interface>.<wifi>.<mode>.<dual> */
					wifiModeObj.setDual("");
				}else if(interfaceImpl.isWifiModeSensor(wifi)){
					/** element: <interface>.<wifi>.<mode>.<sensor> */
					wifiModeObj.setSensor("");
				}
			}
			
			/** element: <interface>.<wifi>.<radio> */
			if(childObj instanceof Wifi.Radio){
				Wifi.Radio interRadio = (Wifi.Radio)childObj;
				
				/** element: <interface>.<wifi>.<radio>.<channel> */
				oDebug.debug("/configuration/interface/"+wifi.name()+"/radio", 
						"channel", GenerateXMLDebug.SET_VALUE,
						interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
				interRadio.setChannel(
						CLICommonFunc.createAhNameActObj(
								interfaceImpl.getInterfaceWifiRadioChannel(wifi), CLICommonFunc.getYesDefault()	
						)
				);
				
				/** element: <interface>.<wifi>.<radio>.<power> */
				oDebug.debug("/configuration/interface/"+wifi.name()+"/radio", 
						"power", GenerateXMLDebug.SET_VALUE,
						interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
				Object[][] powerParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getInterfaceWifiRadioPower(wifi)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				interRadio.setPower(
						(Wifi.Radio.Power)CLICommonFunc.createObjectWithName(Wifi.Radio.Power.class, powerParm)
				);
				
				/** element: <interface>.<wifi>.<radio>.<profile> */
				oDebug.debug("/configuration/interface/"+wifi.name()+"/radio", 
						"profile", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigureWifiRadioProfile(wifi) ){
					
					oDebug.debug("/configuration/interface/"+wifi.name()+"/radio", 
							"profile", GenerateXMLDebug.SET_NAME,
							interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
					interRadio.setProfile(
							CLICommonFunc.createAhNameActObj(interfaceImpl.getInterfaceWifiRadioProfileName(wifi), CLICommonFunc.getYesDefault())
					);
				}
				
				/** element: <interface>.<wifi>.<radio>.<antenna> */
				Wifi.Radio.Antenna antennaObj = new Wifi.Radio.Antenna();
				wifiChildList_2.add(antennaObj);
				interRadio.setAntenna(antennaObj);
				
				/** element: <interface>.<wifi>.<radio>.<range> */
				if(interfaceImpl.isConfigureWifiRadioProfile(wifi)) {
					oDebug.debug("/configuration/interface/"+wifi.name()+"/radio", 
							"range", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
					Object[][] rangeParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getInterfaceRadioRange(wifi)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					interRadio.setRange(
							(Wifi.Radio.Range)CLICommonFunc.createObjectWithName(Wifi.Radio.Range.class, rangeParm)
					);
				}
				
				/** element: <interface>.<wifi>.<radio>.<adaptive-cca> */
				if(interfaceImpl.isConfigureWifiRadioProfile(wifi)){
					Wifi.Radio.AdaptiveCca adaptiveObj = new Wifi.Radio.AdaptiveCca();
					wifiChildList_2.add(adaptiveObj);
					interRadio.setAdaptiveCca(adaptiveObj);
				}
			}
			
			/** element: <interface>.<wifi>.<wlan-idp> */
			if(childObj instanceof Wifi.WlanIdp ){
				Wifi.WlanIdp wlanIdpObj = (Wifi.WlanIdp)childObj;
				
				/** element: <interface>.<wifi>.<wlan-idp>.<profile> */
				oDebug.debug("/configuration/interface/"+wifi.name()+"/wlan-idp", 
						"profile", GenerateXMLDebug.SET_NAME,
						interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
				wlanIdpObj.setProfile(CLICommonFunc.createAhNameActObj(
						interfaceImpl.getInterfaceWlanProfileName(), CLICommonFunc.getYesDefault()) );
			}
			
			/** element: <interface>.<wifi>.<hive> */
			if(childObj instanceof Wifi.Hive){
				Wifi.Hive hiveObj = (Wifi.Hive)childObj;
				
				/** attribute: value */
				oDebug.debug("/configuration/interface/"+wifi.name(), 
						"hive", GenerateXMLDebug.SET_VALUE,
						interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
				hiveObj.setValue(interfaceImpl.getMgtBindHive());
				
				/** attribute: operation */
				hiveObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
				
				/** element: <interface>.<wifi>.<hive>.<shutdown> */
				oDebug.debug("/configuration/interface/"+wifi.name()+"/hive", 
						"shutdown", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigWifiShutdown(wifi)){
					Wifi.Hive.Shutdown shutDown = new Wifi.Hive.Shutdown();
					wifiChildList_2.add(shutDown);
					hiveObj.setShutdown(shutDown);
				}
			}
			
			/** element: <interface>.<wifi>.<client-mode> */
//			if(childObj instanceof WifixClientMode){
//				WifixClientMode clientModeObj = (WifixClientMode)childObj;
//				
//				/** element: <interface>.<wifi>.<client-mode>.<shutdown> */
//				clientModeObj.setShutdown(CLICommonFunc.getAhOnlyAct(interfaceImpl.isWifiClientModeShutdown(wifi)));
//			}
		}
		wifiChildList_1.clear();
		setWifiObj_2(wifi);
	}
	
	private void setWifiObj_2(InterfaceProfileInt.InterfaceWifi wifi) throws Exception {
		/**
		 * <interface>.<wifi>.<radio>.<antenna>					Wifi.Radio.Antenna
		 * <interface>.<wifi>.<radio>.<adaptive-cca>			Wifi.Radio.AdaptiveCca
		 * <interface>.<wifi>.<hive>.<shutdown>					Wifi.Hive.Shutdown
		 * <interface>.<wifi>.<mode>.<wan>						WifixModeWan
		 */
		for(Object childObj : wifiChildList_2) {
			
			/** element: <interface>.<wifi>.<radio>.<antenna> */
			if(childObj instanceof Wifi.Radio.Antenna){
				Wifi.Radio.Antenna antennaObj = (Wifi.Radio.Antenna)childObj;
				
				/** attribute: operation */
				antennaObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				if(interfaceImpl.isHiveAp20()){
					
					oDebug.debug("/configuration/interface/"+wifi.name()+"/radio/antenna", 
							"external", GenerateXMLDebug.CONFIG_ELEMENT,
							interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
					if(interfaceImpl.isConfigureRadioAntennaExternal(wifi) ){
						/** element: <interface>.<wifi>.<radio>.<antenna>.<external> */
						antennaObj.setExternal(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
					}else{
						/** element: <interface>.<wifi>.<radio>.<antenna>.<internal> */
						antennaObj.setInternal(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
					}
					
					/** element: <interface>.<wifi>.<radio>.<antenna>.<default> */
					oDebug.debug("/configuration/interface/"+wifi.name()+"/radio/antenna", 
							"default", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
					Object[][] anDefaultParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getRadioAntennaDefault(wifi)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					antennaObj.setDefault(
							(AntennaDefault)CLICommonFunc.createObjectWithName(AntennaDefault.class, anDefaultParm)
					);
				}
				
				if(interfaceImpl.isHiveAp28()){
					
					/** element: <interface>.<wifi>.<radio>.<antenna>.<fixed-antenna> */
					oDebug.debug("/configuration/interface/"+wifi.name()+"/radio/antenna", 
							"fixed-antenna", GenerateXMLDebug.CONFIG_ELEMENT,
							interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
					if(interfaceImpl.isConfigRadioFixedAntenna(wifi)){
						
						oDebug.debug("/configuration/interface/"+wifi.name()+"/radio/antenna", 
								"fixed-antenna", GenerateXMLDebug.SET_VALUE,
								interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
						Object[][] fixedAntennaParm = {
								{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getRadioFixedAntenna(wifi)},
								{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
						};
						antennaObj.setFixedAntenna(
								(Wifi.Radio.Antenna.FixedAntenna)CLICommonFunc.createObjectWithName(Wifi.Radio.Antenna.FixedAntenna.class, fixedAntennaParm)
						);
					}
					
					/** element: <interface>.<wifi>.<radio>.<antenna>.<diversity> */
					oDebug.debug("/configuration/interface/"+wifi.name()+"/radio/antenna", 
							"diversity", GenerateXMLDebug.SET_OPERATION,
							interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
					antennaObj.setDiversity(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableAntennaDiversity(wifi)));
				}
			}
			
			/** element: <interface>.<wifi>.<radio>.<adaptive-cca> */
			if(childObj instanceof Wifi.Radio.AdaptiveCca){
				Wifi.Radio.AdaptiveCca ccaObj = (Wifi.Radio.AdaptiveCca)childObj;
				
				/** element: <interface>.<wifi>.<radio>.<adaptive-cca>.<enable> */
				oDebug.debug("/configuration/interface/"+wifi.name()+"/radio/adaptive-cca", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
				ccaObj.setEnable(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableCCA(wifi)));
				
				if(interfaceImpl.isEnableCCA(wifi)){
					
					/** element: <interface>.<wifi>.<radio>.<adaptive-cca>.<max-cca> */
					oDebug.debug("/configuration/interface/"+wifi.name()+"/radio/adaptive-cca", 
							"max-cca", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
					Object[][] maxCcaParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getMaxCca(wifi)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					ccaObj.setMaxCca(
							(Wifi.Radio.AdaptiveCca.MaxCca)CLICommonFunc.createObjectWithName(Wifi.Radio.AdaptiveCca.MaxCca.class, maxCcaParm)
					);
					
					/** element: <interface>.<wifi>.<radio>.<adaptive-cca>.<default-cca> */
					oDebug.debug("/configuration/interface/"+wifi.name()+"/radio/adaptive-cca", 
							"default-cca", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
					Object[][] defaultCcaParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getDefaultCca(wifi)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					ccaObj.setDefaultCca(
							(Wifi.Radio.AdaptiveCca.DefaultCca)CLICommonFunc.createObjectWithName(
									Wifi.Radio.AdaptiveCca.DefaultCca.class, defaultCcaParm)
					);
				}
			}
			
			/** element: <interface>.<wifi>.<hive>.<shutdown> */
			if(childObj instanceof Wifi.Hive.Shutdown){
				Wifi.Hive.Shutdown shutdownObj = (Wifi.Hive.Shutdown)childObj;
				
				/** element: <interface>.<wifi>.<hive>.<shutdown>.<cr> */
				oDebug.debug("/configuration/interface/"+wifi.name()+"/hive/shutdown", 
						"cr", GenerateXMLDebug.SET_OPERATION,
						interfaceImpl.getHiveApGuiName (), interfaceImpl.getHiveApName());
				shutdownObj.setCr(CLICommonFunc.getAhOnlyAct(interfaceImpl.isWifiShutdown(wifi)));
			}
			
			/** element: <interface>.<wifi>.<mode>.<wan> */
			if(childObj instanceof WifixModeWan){
				WifixModeWan wanObj = (WifixModeWan)childObj;
				
				InterType intType = null;
				if(wifi == InterfaceWifi.wifi0){
					intType = InterType.wifi0;
				}else{
					intType = InterType.wifi1;
				}
				
				/** element: <interface>.<wifi>.<mode>.<wan>.<cr> */
				wanObj.setCr("");
				
				/** element: <interface>.<wifi>.<mode>.<wan>.<nat> */
				wanObj.setNat(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableWanNat(intType)));
				
				/** element: <interface>.<wifi>.<mode>.<wan>.<nat-policy> */
				if (interfaceImpl.isEnableWanNatPolicy(intType)) {
					for (int i = 0 ; i < interfaceImpl.getNatPolicyNameForPortForwardingSize(); i++) {
						wanObj.getNatPolicy().add(CLICommonFunc.createAhNameActValue(interfaceImpl.getNatPolicyNameForPortForwarding(i), true));
					}
				}
								
				/** element: <interface>.<wifi>.<mode>.<wan>.<priority> */
				wanObj.setPriority(CLICommonFunc.createAhIntNameActObj(
						interfaceImpl.getInterfacePriority(intType), CLICommonFunc.getYesDefault()));
			}
		}
		wifiChildList_2.clear();
	}
	
	private void setWifiSsid(Wifi.Ssid ssidObj, InterfaceProfileInt.InterfaceWifi wifi, int index){
		/**
		 * <interface>.<wifi>.<ssid>			Wifi.Ssid
		 */
		
		/** attribute: name */
		oDebug.debug("/configuration/interface/"+wifi.name(), 
				"ssid", GenerateXMLDebug.SET_NAME,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		ssidObj.setName(interfaceImpl.getWifiSsidName(index));
		
		/** attribute: operation */
		ssidObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
//		if(interfaceImpl.isEnableWifiAsClient(index)){
//			/** element: <interface>.<wifi>.<ssid>.<as-client> */
//			ssidObj.setAsClient("");
//		}else{
			
			/** element: <cr> */
			ssidObj.setCr("");
			
			/** element: <interface>.<wifi>.<ssid>.<ip> */
			oDebug.debug("/configuration/interface/"+wifi.name()+"/ssid[@name='"+ssidObj.getName()+"']", 
					"ip", GenerateXMLDebug.CONFIG_ELEMENT,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			if(interfaceImpl.isConfigureSsidIp(wifi, index) ){
				
				oDebug.debug("/configuration/interface/"+wifi.name()+"/ssid[@name='"+ssidObj.getName()+"']", 
						"ip", GenerateXMLDebug.SET_VALUE,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				ssidObj.setIp(
						CLICommonFunc.createAhNameActObj(interfaceImpl.getWifiSsidIp(wifi, index), CLICommonFunc.getYesDefault())
				);
			}
			
			/** element: <interface>.<wifi>.<ssid>.<shutdown> */
			oDebug.debug("/configuration/interface/"+wifi.name()+"/ssid[@name='"+ssidObj.getName()+"']", 
					"shutdown", GenerateXMLDebug.SET_OPERATION,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			ssidObj.setShutdown(CLICommonFunc.getAhOnlyAct(interfaceImpl.isShutDownWifiSsid(wifi, index)));
			
			/** element: <interface>.<wifi>.<ssid>.<mode> */
			AssistantMode ssidModel = new AssistantMode();
			ssidModel.setName(interfaceImpl.getInterWifiMode(wifi).value());
			ssidModel.setAHDELTAASSISTANT(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
			ssidObj.setMode(ssidModel);
//		}
	}
	
	private void setEthChildLevel(Eth ethi, InterfaceProfileInt.InterType type) throws Exception{
		
		/** element: <interface>.<eth>.<speed> */
		oDebug.debug("/configuration/interface/"+type.name(), 
				"speed", GenerateXMLDebug.SET_VALUE,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		Object[][] ethParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getInterSpeed(type)},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		ethi.setSpeed(
				(Eth.Speed)CLICommonFunc.createObjectWithName(Eth.Speed.class, ethParm)
		);
		
		/** element: <interface>.<eth>.<pse> */
		if(interfaceImpl.isConfigEthxPse(type)){
			EthxPse pseObj=new EthxPse();
			ethChildList_1.add(pseObj);
			ethi.setPse(pseObj);
		}
		
		/** element: <interface>.<eth>.<duplex> */
		Eth.Duplex duplexObj = new Eth.Duplex();
		ethChildList_1.add(duplexObj);
		ethi.setDuplex(duplexObj);
		
		/** element: <interface>.<eth>.<dhcp> */
		if(interfaceImpl.isConfigEthDhcp(type)){
			EthxDhcp dhcp = new EthxDhcp();
			ethChildList_1.add(dhcp);
			ethi.setDhcp(dhcp);
		}
		
		if(interfaceImpl.isConfigInterBind(type)){
			/** element: <interface>.<eth>.<bind> */
			Eth.Bind bindObj = new Eth.Bind();
			ethChildList_1.add(bindObj);
			ethi.setBind(bindObj);
		}else{
			/** element: <interface>.<eth>.<manage> */
			if(interfaceImpl.isConfigInterManage(type)){
				AhManage manageObj = new AhManage();
				ethChildList_1.add(manageObj);
				ethi.setManage(manageObj);
			}
			
			/** element: <interface>.<eth>.<mode> */
			InterfaceMode ethMode = new InterfaceMode();
			ethChildList_1.add(ethMode);
			ethi.setMode(ethMode);
			
			/** element: <interface>.<eth>.<shutdown> */
			oDebug.debug("/configuration/interface/"+type.name(), 
					"shutdown", GenerateXMLDebug.SET_OPERATION,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			ethi.setShutdown(CLICommonFunc.getAhOnlyAct(interfaceImpl.isInterShutdown(type)));
			
			/** element: <interface>.<eth>.<qos-classifier> */
			oDebug.debug("/configuration/interface/"+type.name(), 
					"qos-classifier", GenerateXMLDebug.CONFIG_ELEMENT,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			if(interfaceImpl.isInterConfigQosClass(type)){
				
				oDebug.debug("/configuration/interface/"+type.name(), 
						"qos-classifier", GenerateXMLDebug.SET_NAME,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				ethi.setQosClassifier(CLICommonFunc.createAhNameActObj(
						interfaceImpl.getInterQosClassifier(type), CLICommonFunc.getYesDefault()));
			}
			
			/** element: <interface>.<eth>.<qos-marker> */
			oDebug.debug("/configuration/interface/"+type.name(), 
					"qos-marker", GenerateXMLDebug.CONFIG_ELEMENT,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			if(interfaceImpl.isConfigInterQosMarker(type)){
				
				oDebug.debug("/configuration/interface/"+type.name(), 
						"qos-marker", GenerateXMLDebug.SET_NAME,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				ethi.setQosMarker(CLICommonFunc.createAhNameActObj(
						interfaceImpl.getInterQosMarker(type), CLICommonFunc.getYesDefault()));
			}
			
			/** element: <interface>.<eth>.<mac-learning> */
			oDebug.debug("/configuration/interface/"+type.name(), 
					"mac-learning", GenerateXMLDebug.CONFIG_ELEMENT,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			if(interfaceImpl.isEnableInterMacLearning(type)){
				InterfaceMacLearning macLearnObj = new InterfaceMacLearning();
				ethChildList_1.add(macLearnObj);
				ethi.setMacLearning(macLearnObj);
			}
			
			/** element: <interface>.<eth>.<inter-station-traffic> */
			oDebug.debug("/configuration/interface/"+type.name(), 
					"inter-station-traffic", GenerateXMLDebug.CONFIG_ELEMENT,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			if(interfaceImpl.isConfigInterStationTraffic(type)){
				
				oDebug.debug("/configuration/interface/"+type.name(), 
						"inter-station-traffic", GenerateXMLDebug.SET_OPERATION,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				ethi.setInterStationTraffic(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableInterStationTraffic(type)));
			}
			
			/** element: <interface>.<eth>.<allowed-vlan> */
			oDebug.debug("/configuration/interface/"+type.name(), 
					"allowed-vlan", GenerateXMLDebug.CONFIG_ELEMENT,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			if(interfaceImpl.isConfigEthAllowedVlan(type)){
				InterfaceAllowedVlan allowedVlan = new InterfaceAllowedVlan();
				ethChildList_1.add(allowedVlan);
				ethi.setAllowedVlan(allowedVlan);
			}
			
			/** element: <interface>.<eth>.<security-object> */
			oDebug.debug("/configuration/interface/"+type.name(), 
					"security-object", GenerateXMLDebug.CONFIG_ELEMENT,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			if(interfaceImpl.isConfigEthSecurity(type)){
				
				oDebug.debug("/configuration/interface/"+type.name(), 
						"security-object", GenerateXMLDebug.SET_VALUE,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				ethi.setSecurityObject(CLICommonFunc.createAhNameActValue(
						interfaceImpl.getEthSecurityObjName(type), CLICommonFunc.getYesDefault()));
			}
			
			/** element: <interface>.<eth>.<ip> */
			oDebug.debug("/configuration/interface/"+type.name(), 
					"ip", GenerateXMLDebug.CONFIG_ELEMENT,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			if(interfaceImpl.isConfigEthIp(type)){
				
				oDebug.debug("/configuration/interface/"+type.name(), 
						"ip", GenerateXMLDebug.SET_NAME,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				ethi.setIp(CLICommonFunc.createAhNameActObj(
						interfaceImpl.getEthIp(type), CLICommonFunc.getYesDefault()));
			}
			
//			/** element: <interface>.<eth>.<link-discovery> */
//			oDebug.debug("/configuration/interface/"+type.name(), 
//					"link-discovery", GenerateXMLDebug.CONFIG_ELEMENT,
//					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
//			if(interfaceImpl.isConfigLinkDiscovery()){
//				ethi.setLinkDiscovery(this.CreateInterfaceLinkDiscovery(type));
//			}
			
			/** element: <interface>.<eth>.<native-vlan> */
			oDebug.debug("/configuration/interface/"+type.name(), 
					"native-vlan", GenerateXMLDebug.CONFIG_ELEMENT,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			if(interfaceImpl.isConfigEthNativeVlan(type)){
				
				oDebug.debug("/configuration/interface/"+type.name(), 
						"native-vlan", GenerateXMLDebug.SET_VALUE,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				Object[][] nativeVlanParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getEthNativeVlan(type)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				ethi.setNativeVlan((InterfaceEthxNativeVlan)CLICommonFunc.createObjectWithName(InterfaceEthxNativeVlan.class, nativeVlanParm));
			}
		}
		
		generateEthChildLeve_2(type);
	}
	
	private void generateEthChildLeve_2(InterfaceProfileInt.InterType type) throws Exception{
		/**
		 * <interface>.<eth>.<mode>					InterfaceMode
		 * <interface>.<eth>.<manage>				AhManage
		 * <interface>.<eth>.<duplex>				Eth.Duplex
		 * <interface>.<eth>.<mac-learning>			InterfaceMacLearning
		 * <interface>.<eth>.<bind>					Eth.Bind
		 * <interface>.<eth>.<allowed-vlan>			InterfaceAllowedVlan
		 * <interface>.<eth>.<dhcp>					EthxDhcp
		 * * <interface>.<eth>.<pse>				EthxPse
		 */
		for(Object childObj : ethChildList_1){
			
			/** element: <interface>.<eth>.<mode> */
			if(childObj instanceof InterfaceMode ){
				InterfaceMode interModeObj = (InterfaceMode)childObj;
				
				/** attribute: operation */
				interModeObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <interface>.<eth>.<mode>.<bridge-access> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mode", 
						"bridge-access", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if( interfaceImpl.isConfigInterMode(InterfaceProfileInt.InterfaceMode.bridgeAccess, type) ){
					InterfaceMode.BridgeAccess bridgeAccessObj = new InterfaceMode.BridgeAccess();
					ethChildList_2.add(bridgeAccessObj);
					interModeObj.setBridgeAccess(bridgeAccessObj);
				}
				
				/** element: <interface>.<eth>.<mode>.<access> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mode", 
						"access", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if( interfaceImpl.isConfigInterMode(InterfaceProfileInt.InterfaceMode.access, type) ){
					InterfaceMode.Access accessObj = new InterfaceMode.Access();
					ethChildList_2.add(accessObj);
					interModeObj.setAccess(accessObj);
				}
				
				/** element: <interface>.<eth>.<mode>.<backhaul> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mode", 
						"backhaul", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if( interfaceImpl.isConfigInterMode(InterfaceProfileInt.InterfaceMode.backhaul, type) ){
					interModeObj.setBackhaul(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
				
				/** element: <interface>.<eth>.<mode>.<bridge-802.1q> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mode", 
						"bridge-802.1q", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if( interfaceImpl.isConfigInterMode(InterfaceProfileInt.InterfaceMode.bridge8021q, type)){
					InterfaceMode.Bridge8021Q bridgeObj = new InterfaceMode.Bridge8021Q();
					ethChildList_2.add(bridgeObj);
					interModeObj.setBridge8021Q(bridgeObj);
				}
				
				/** element: <interface>.<eth>.<mode>.<bridge> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mode", 
						"bridge", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if( interfaceImpl.isConfigInterMode(InterfaceProfileInt.InterfaceMode.bridge8021q, type)){
					InterfaceMode.Bridge bridgeR0Obj = new InterfaceMode.Bridge();
					ethChildList_2.add(bridgeR0Obj);
					interModeObj.setBridge(bridgeR0Obj);
				}
				
				/** element: <interface>.<eth>.<mode>.<wan> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mode", 
						"wan", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigInterMode(InterfaceProfileInt.InterfaceMode.wan, type)){
					EthxModeWan wanObj = new EthxModeWan();
					ethChildList_2.add(wanObj);
					interModeObj.setWan(wanObj);
				}
			}
			
			/** element: <interface>.<eth>.<manage> */
			if(childObj instanceof AhManage){
				AhManage ethManageObj = (AhManage)childObj;
					
				/** element: <interface>.<eth>.<manage>.<SNMP> */
				oDebug.debug("/configuration/interface/"+type.name()+"/manage", 
						"SNMP", GenerateXMLDebug.SET_OPERATION,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				ethManageObj.setSNMP(
						CLICommonFunc.getAhOnlyAct(
								interfaceImpl.isEnableInterManage(InterfaceProfileInt.ManageType.SNMP, type)
						)
				);
				
				/** element: <interface>.<eth>.<manage>.<SSH> */
				oDebug.debug("/configuration/interface/"+type.name()+"/manage", 
						"SSH", GenerateXMLDebug.SET_OPERATION,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				ethManageObj.setSSH(
						CLICommonFunc.getAhOnlyAct(
								interfaceImpl.isEnableInterManage(InterfaceProfileInt.ManageType.SSH, type)
						)
				);
				
				/** element: <interface>.<eth>.<manage>.<Telnet> */
				oDebug.debug("/configuration/interface/"+type.name()+"/manage", 
						"Telnet", GenerateXMLDebug.SET_OPERATION,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				ethManageObj.setTelnet(
						CLICommonFunc.getAhOnlyAct(
								interfaceImpl.isEnableInterManage(InterfaceProfileInt.ManageType.Telnet, type)
						)
				);
				
				/** element: <interface>.<eth>.<manage>.<ping> */
				oDebug.debug("/configuration/interface/"+type.name()+"/manage", 
						"ping", GenerateXMLDebug.SET_OPERATION,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				ethManageObj.setPing(
						CLICommonFunc.getAhOnlyAct(
								interfaceImpl.isEnableInterManage(InterfaceProfileInt.ManageType.ping, type)
						)
				);

			}
			
			/** element: <interface>.<eth>.<duplex> */
			if(childObj instanceof Eth.Duplex){
				Eth.Duplex duplexObj = (Eth.Duplex)childObj;
				
				/** attribute: operation */
				duplexObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				oDebug.debug("/configuration/interface/"+type.name(), 
						"duplex", GenerateXMLDebug.SET_VALUE,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				duplexObj.setValue(interfaceImpl.getInterDuplex(type));
			}
			
			/** element: <interface>.<eth>.<mac-learning> */
			if(childObj instanceof InterfaceMacLearning){
				InterfaceMacLearning macLearnObj = (InterfaceMacLearning)childObj;
				
				/** element: <interface>.<eth>.<mac-learning>.<enable> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mac-learning", 
						"enable", GenerateXMLDebug.SET_OPERATION,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				macLearnObj.setEnable(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableInterMacLearning(type)));
				
				/** element: <interface>.<eth>.<mac-learning>.<idle-timeout> */
				if(interfaceImpl.isConfigIdleTimeout(type)){
					oDebug.debug("/configuration/interface/"+type.name()+"/mac-learning", 
							"idle-timeout", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					Object[][] idleTimeOutParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getInterIdleTimeout(type)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					macLearnObj.setIdleTimeout(
							(InterfaceMacLearning.IdleTimeout)CLICommonFunc.createObjectWithName(InterfaceMacLearning.IdleTimeout.class, idleTimeOutParm)
					);
				}
				
				/** element: <interface>.<eth>.<mac-learning>.<static> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mac-learning", 
						"static", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				for(int i=0; i<interfaceImpl.getInterMacLearningStaticSize(type); i++){
					
					oDebug.debug("/configuration/interface/"+type.name()+"/mac-learning", 
							"static", GenerateXMLDebug.SET_NAME,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					macLearnObj.getStatic().add(
							CLICommonFunc.createAhNameActValue(interfaceImpl.getStaticMacAddr(type, i), CLICommonFunc.getYesDefault())
					);
				}
				
			}
			
			/** element: <interface>.<eth>.<bind> */
			if(childObj instanceof Eth.Bind){
				Eth.Bind bindObj = (Eth.Bind)childObj;
				
				/** attribute: operation */
				bindObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <interface>.<eth>.<bind>.<agg0> */
				oDebug.debug("/configuration/interface/"+type.name()+"/bind", 
						"agg0", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigInterBindAgg0(type)){
					bindObj.setAgg0("");
				}
				
				/** element: <interface>.<eth>.<bind>.<red0> */
				oDebug.debug("/configuration/interface/"+type.name()+"/bind", 
						"red0", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigInterBindRed0(type)){
					Eth.Bind.Red0 red0Obj = new Eth.Bind.Red0();
					ethChildList_2.add(red0Obj);
					bindObj.setRed0(red0Obj);
				}
			}
			
			/** element: <interface>.<eth>.<allowed-vlan> */
			if(childObj instanceof InterfaceAllowedVlan){
				InterfaceAllowedVlan allowedVlan = (InterfaceAllowedVlan)childObj;
				
				/** attribute: operation */
				allowedVlan.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <interface>.<eth>.<allowed-vlan>.<all> */
				oDebug.debug("/configuration/interface/"+type.name()+"/allowed-vlan", 
						"all", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigAllowedVlanAll(type)){
					allowedVlan.setAll("");
				}
				
				/** element: <interface>.<eth>.<allowed-vlan>.<auto> */
				oDebug.debug("/configuration/interface/"+type.name()+"/allowed-vlan", 
						"auto", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigAllowedVlanAuto(type)){
					allowedVlan.setAuto("");
				}
				
				/** element: <interface>.<eth>.<allowed-vlan>.<cr> */
				oDebug.debug("/configuration/interface/"+type.name()+"/allowed-vlan", 
						"cr", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigAllowedVlanNum(type)){
					
					oDebug.debug("/configuration/interface/"+type.name()+"/allowed-vlan", 
							"cr", GenerateXMLDebug.SET_NAME,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					for(int i=0; i<interfaceImpl.getAllowedVlanSize(type); i++){
						allowedVlan.getCr().add(CLICommonFunc.createAhNameActValueQuoteProhibited(
								interfaceImpl.getAllowedVlanStr(type, i), CLICommonFunc.getYesDefault(), true));
					}
				}
			}
			
			/** element: <interface>.<eth>.<dhcp> */
			if(childObj instanceof EthxDhcp){
				EthxDhcp dhcpObj = (EthxDhcp)childObj;
				
				/** element: <interface>.<eth>.<dhcp>.<client> */
				EthxDhcpClient clientObj = new EthxDhcpClient();
				ethChildList_2.add(clientObj);
				dhcpObj.setClient(clientObj);
			}
			
			/** element: <interface>.<eth>.<pse> */
			if(childObj instanceof EthxPse){
				EthxPse pseObj=(EthxPse)childObj;
				
				/** element: <interface>.<eth>.<pse>.<shutdown> */
				oDebug.debug("/configuration/interface/"+type.name()+"/pse", 
						"shutdown", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				pseObj.setShutdown(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableEthxShutdown(type)));
				
				/** element: <interface>.<eth>.<pse>.<mode> */
				if(!interfaceImpl.isEnableEthxShutdown(type)){
					oDebug.debug("/configuration/interface/"+type.name()+"/pse", 
							"mode", GenerateXMLDebug.CONFIG_ELEMENT,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					Object[][] modeParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getEthxPseMode(type)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					pseObj.setMode(
							(EthxPseMode)CLICommonFunc.createObjectWithName(EthxPseMode.class, modeParm)
					);
				}
				
				/** element: <interface>.<eth>.<pse>.<priority> */
				oDebug.debug("/configuration/interface/"+type.name()+"/pse", 
						"priority", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				Object[][] priorityParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getEthxPsePriority(type)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				pseObj.setPriority(
						(EthxPsePriority)CLICommonFunc.createObjectWithName(EthxPsePriority.class, priorityParm)
				);
			}
		}
		ethChildList_1.clear();
		generateEthChildLeve_3(type);
	}
	
	private void generateEthChildLeve_3(InterfaceProfileInt.InterType type) throws Exception{
		/**
		 * <interface>.<eth>.<mode>.<bridge-access>			InterfaceMode.BridgeAccess
		 * <interface>.<eth>.<mode>.<access>					InterfaceMode.Access
		 * <interface>.<eth>.<mode>.<bridge-802.1q>			InterfaceMode.Bridge8021Q
		 * <interface>.<eth>.<mode>.<wan>					EthxModeWan
		 * <interface>.<eth>.<bind>.<red0>					Eth.Bind.Red0
		 * <interface>.<eth>.<mode>.<bridge>				InterfaceMode.Bridge
		 * <interface>.<eth>.<dhcp>.<client>				EthxDhcpClient
		 */
		for(Object childObj : ethChildList_2){
			
			/** element: <interface>.<eth>.<mode>.<access> */
			if(childObj instanceof InterfaceMode.Access){
				InterfaceMode.Access accessObj = (InterfaceMode.Access)childObj;
				
				/** attribute: operation */
				accessObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <interface>.<eth>.<mode>.<bridge-access> */
			if(childObj instanceof InterfaceMode.BridgeAccess){
				InterfaceMode.BridgeAccess bridgeAccessObj = (InterfaceMode.BridgeAccess)childObj;
				
				/** attribute: operation */
				bridgeAccessObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
				
				/** element: <interface>.<eth>.<mode>.<bridge-access>.<user-profile-attribute> */
//				oDebug.debug("/configuration/interface/"+type.name()+"/mode/bridge-access", 
//						"user-profile-attribute", GenerateXMLDebug.CONFIG_ELEMENT,
//						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
//				if(interfaceImpl.isConfigBridgeUserProfile(type)){
					
				oDebug.debug("/configuration/interface/"+type.name()+"/mode/bridge-access", 
						"user-profile-attribute", GenerateXMLDebug.SET_VALUE,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				Object[][] attrParm = {
						{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getInterAccessUserProfileAttr(type)},
						{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
				};
				bridgeAccessObj.setUserProfileAttribute(
						(InterfaceAttribute)CLICommonFunc.createObjectWithName(InterfaceAttribute.class, attrParm)
				);
					
//				}
			}
			
			/** element: <interface>.<eth0>.<mode>.<bridge-802.1q> */
			if(childObj instanceof InterfaceMode.Bridge8021Q){
				InterfaceMode.Bridge8021Q bridgeObj = (InterfaceMode.Bridge8021Q)childObj;
				
				/** attribute: operation */
				bridgeObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
				
				/** element: <interface>.<eth0>.<mode>.<bridge-802.1q>.<user-profile-attribute> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mode/bridge-802.1q", 
						"user-profile-attribute", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigBridgeUserProfile(type)){
					
					oDebug.debug("/configuration/interface/"+type.name()+"/mode/bridge-802.1q", 
							"user-profile-attribute", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					Object[][] attrParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getInterAccessUserProfileAttr(type)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					bridgeObj.setUserProfileAttribute(
							(InterfaceAttribute)CLICommonFunc.createObjectWithName(InterfaceAttribute.class, attrParm)
					);
				}
			}
			
			/** element <interface>.<eth>.<mode>.<bridge> */
			if(childObj instanceof InterfaceMode.Bridge){
				InterfaceMode.Bridge bridgeR0Obj = (InterfaceMode.Bridge)childObj;
				
				/** attribute: operation */
				bridgeR0Obj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
				
				
				/** element <interface>.<eth>.<mode>.<bridge>.<user-profile-attribute> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mode/bridge", 
						"user-profile-attribute", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigBridgeUserProfile(type)){
					
					oDebug.debug("/configuration/interface/"+type.name()+"/mode/bridge", 
							"user-profile-attribute", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					Object[][] attrParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getInterAccessUserProfileAttr(type)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					bridgeR0Obj.setUserProfileAttribute(
							(InterfaceAttribute)CLICommonFunc.createObjectWithName(InterfaceAttribute.class, attrParm)
					);
				}
			}
			
			/** element: <interface>.<eth>.<bind>.<red0                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        > */
			if(childObj instanceof Eth.Bind.Red0){
				Eth.Bind.Red0 redObj = (Eth.Bind.Red0)childObj;
				
				/** element: <interface>.<eth>.<bind>.<red0>.<primary> */
				oDebug.debug("/configuration/interface/"+type.name()+"/bind/red0", 
						"primary", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigInterBindRedPrimary(type)){
					redObj.setPrimary("");
				}	
			}
			
			/** element: <interface>.<eth>.<dhcp>.<client> */
			if(childObj instanceof EthxDhcpClient){
				EthxDhcpClient clientObj = (EthxDhcpClient)childObj;
				
				/** element: <interface>.<eth>.<dhcp>.<client>.<cr> */
				clientObj.setCr(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableEthDhcp(type)));
			}
			
			/** element: <interface>.<eth>.<mode>.<wan>	*/
			if(childObj instanceof EthxModeWan){
				EthxModeWan wanObj = (EthxModeWan)childObj;
				
				/** element: <interface>.<eth>.<mode>.<wan>.<cr> */
				wanObj.setCr("");
				
				/** element: <interface>.<eth>.<mode>.<wan>.<nat> */
				wanObj.setNat(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableWanNat(type)));
				
				/** element: <interface>.<eth>.<mode>.<wan>.<nat-policy> */
				if (interfaceImpl.isEnableWanNatPolicy(type)) {
					for (int i = 0 ; i < interfaceImpl.getNatPolicyNameForPortForwardingSize(); i++) {
						wanObj.getNatPolicy().add(CLICommonFunc.createAhNameActValue(interfaceImpl.getNatPolicyNameForPortForwarding(i), true));
					}
				}
				
				/** element: <interface>.<eth>.<mode>.<wan>.<priority> */
				try {
					if (interfaceImpl.getInterfacePriority(type) > 0) {
						wanObj.setPriority(CLICommonFunc.createAhIntNameActObj(interfaceImpl.getInterfacePriority(type), true));
					}	
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		}
		ethChildList_2.clear();
	}
	
	private void setRedxAggxChildLevel_1(RedxAggx redxAggx, InterfaceProfileInt.InterType type) throws Exception{
		
		/** element: <interface>.<agg/red>.<mode> */
		InterfaceMode modeObj = new InterfaceMode();
		aggRedChildList_1.add(modeObj);
		redxAggx.setMode(modeObj);
		
		/** element: <interface>.<agg/red>.<manage> */
		oDebug.debug("/configuration/interface/"+type.name(),
				"manage", GenerateXMLDebug.CONFIG_ELEMENT,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		if(interfaceImpl.isConfigInterManage(type) ){
			AhManage manageObj = new AhManage();
			aggRedChildList_1.add(manageObj);
			redxAggx.setManage(manageObj);
		}
		
		/** element: <interface>.<agg/red>.<shutdown> */
		oDebug.debug("/configuration/interface/"+type.name(),
				"shutdown", GenerateXMLDebug.SET_OPERATION,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		redxAggx.setShutdown(CLICommonFunc.getAhOnlyAct(interfaceImpl.isInterShutdown(type)));
		
		/** element: <interface>.<agg/red>.<qos-classifier> */
		oDebug.debug("/configuration/interface/"+type.name(),
				"qos-classifier", GenerateXMLDebug.CONFIG_ELEMENT,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		if(interfaceImpl.isInterConfigQosClass(type)){
			
			oDebug.debug("/configuration/interface/"+type.name(),
					"qos-classifier", GenerateXMLDebug.SET_NAME,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			redxAggx.setQosClassifier(CLICommonFunc.createAhNameActObj(
					interfaceImpl.getInterQosClassifier(type), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <interface>.<agg/red>.<qos-marker> */
		oDebug.debug("/configuration/interface/"+type.name(),
				"qos-marker", GenerateXMLDebug.CONFIG_ELEMENT,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		if(interfaceImpl.isConfigInterQosMarker(type)){
			
			oDebug.debug("/configuration/interface/"+type.name(),
					"qos-marker", GenerateXMLDebug.SET_NAME,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			redxAggx.setQosMarker(CLICommonFunc.createAhNameActObj(
					interfaceImpl.getInterQosMarker(type), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <interface>.<agg/red>.<mac-learning> */
//		oDebug.debug("/configuration/interface/"+type.name(),
//				"mac-learning", GenerateXMLDebug.CONFIG_ELEMENT,
//				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
//		if(interfaceImpl.isConfigInterMacLearning(type)){
		InterfaceMacLearning macLearnObj = new InterfaceMacLearning();
		aggRedChildList_1.add(macLearnObj);
		redxAggx.setMacLearning(macLearnObj);
//		}
		
		/** element: <interface>.<agg/red>.<inter-station-traffic> */
		oDebug.debug("/configuration/interface/"+type.name(),
				"inter-station-traffic", GenerateXMLDebug.CONFIG_ELEMENT,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		if(interfaceImpl.isConfigInterStationTraffic(type)){
			
			oDebug.debug("/configuration/interface/"+type.name(),
					"inter-station-traffic", GenerateXMLDebug.SET_OPERATION,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			redxAggx.setInterStationTraffic(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableInterStationTraffic(type)));
		}
		
//		/** element: <interface>.<agg/red>.<link-discovery> */
//		oDebug.debug("/configuration/interface/"+type.name(),
//				"link-discovery", GenerateXMLDebug.CONFIG_ELEMENT,
//				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
//		if(interfaceImpl.isConfigLinkDiscovery()){
//			redxAggx.setLinkDiscovery(this.CreateInterfaceLinkDiscovery(type));
//		}
		
		setRedxAggxChildLevel_2(type);
	}
	
	private void setRedxAggxChildLevel_2(InterfaceProfileInt.InterType type) throws Exception{
		/**
		 * <interface>.<agg/red>.<mode>				InterfaceMode
		 * <interface>.<agg/red>.<manage>			AhManage
		 * <interface>.<agg/red>.<mac-learning>		InterfaceMacLearning
		 */
		for(Object childObj : aggRedChildList_1){
			
			/** element: <interface>.<agg/red>.<mode> */
			if(childObj instanceof InterfaceMode ){
				InterfaceMode interModeObj = (InterfaceMode)childObj;
				
				/** attribute: operation */
				interModeObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** element: <interface>.<agg/red>.<mode>.<bridge-access> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mode",
						"bridge-access", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if( interfaceImpl.isConfigInterMode(InterfaceProfileInt.InterfaceMode.bridgeAccess, type) ){
					InterfaceMode.BridgeAccess bridgeAccessObj = new InterfaceMode.BridgeAccess();
					ethChildList_2.add(bridgeAccessObj);
					interModeObj.setBridgeAccess(bridgeAccessObj);
				}
				
				/** element: <interface>.<agg/red>.<mode>.<access> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mode",
						"access", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if( interfaceImpl.isConfigInterMode(InterfaceProfileInt.InterfaceMode.access, type) ){
					InterfaceMode.Access accessObj = new InterfaceMode.Access();
					ethChildList_2.add(accessObj);
					interModeObj.setAccess(accessObj);
				}
				
				/** element: <interface>.<agg/red>.<mode>.<backhaul> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mode",
						"backhaul", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if( interfaceImpl.isConfigInterMode(InterfaceProfileInt.InterfaceMode.backhaul, type) ){
					interModeObj.setBackhaul(CLICommonFunc.createAhActShow(CLICommonFunc.getYesDefault()));
				}
				
				/** element: <interface>.<agg/red>.<mode>.<bridge-802.1q> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mode",
						"bridge-802.1q", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if( interfaceImpl.isConfigInterMode(InterfaceProfileInt.InterfaceMode.bridge8021q, type) ){
					InterfaceMode.Bridge8021Q bridgeObj = new InterfaceMode.Bridge8021Q();
					ethChildList_2.add(bridgeObj);
					interModeObj.setBridge8021Q(bridgeObj);
				}
			}
			
			/** element: <interface>.<agg/red>.<manage> */
			if(childObj instanceof AhManage){
				AhManage ethManageObj = (AhManage)childObj;
					
				/** element: <interface>.<agg/red>.<manage>.<SNMP> */
				oDebug.debug("/configuration/interface/"+type.name()+"/manage",
						"SNMP", GenerateXMLDebug.SET_OPERATION,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				ethManageObj.setSNMP(
						CLICommonFunc.getAhOnlyAct(
								interfaceImpl.isEnableInterManage(InterfaceProfileInt.ManageType.SNMP, type)
						)
				);
				
				/** element: <interface>.<agg/red>.<manage>.<SSH> */
				oDebug.debug("/configuration/interface/"+type.name()+"/manage",
						"SSH", GenerateXMLDebug.SET_OPERATION,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				ethManageObj.setSSH(
						CLICommonFunc.getAhOnlyAct(
								interfaceImpl.isEnableInterManage(InterfaceProfileInt.ManageType.SSH, type)
						)
				);
				
				/** element: <interface>.<agg/red>.<manage>.<Telnet> */
				oDebug.debug("/configuration/interface/"+type.name()+"/manage",
						"Telnet", GenerateXMLDebug.SET_OPERATION,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				ethManageObj.setTelnet(
						CLICommonFunc.getAhOnlyAct(
								interfaceImpl.isEnableInterManage(InterfaceProfileInt.ManageType.Telnet, type)
						)
				);
				
				/** element: <interface>.<agg/red>.<manage>.<ping> */
				oDebug.debug("/configuration/interface/"+type.name()+"/manage",
						"ping", GenerateXMLDebug.SET_OPERATION,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				ethManageObj.setPing(
						CLICommonFunc.getAhOnlyAct(
								interfaceImpl.isEnableInterManage(InterfaceProfileInt.ManageType.ping, type)
						)
				);

			}
			
			/** element: <interface>.<agg/red>.<mac-learning> */
			if(childObj instanceof InterfaceMacLearning){
				InterfaceMacLearning macLearnObj = (InterfaceMacLearning)childObj;
				
				/** element: <interface>.<agg/red>.<mac-learning>.<enable> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mac-learning",
						"enable", GenerateXMLDebug.SET_OPERATION,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				macLearnObj.setEnable(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableInterMacLearning(type)));
				
				/** element: <interface>.<agg/red>.<mac-learning>.<idle-timeout> */
				if(interfaceImpl.isEnableInterMacLearning(type)){
					oDebug.debug("/configuration/interface/"+type.name()+"/mac-learning",
							"idle-timeout", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					Object[][] idleTimeOutParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getInterIdleTimeout(type)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					macLearnObj.setIdleTimeout(
							(InterfaceMacLearning.IdleTimeout)CLICommonFunc.createObjectWithName(InterfaceMacLearning.IdleTimeout.class, idleTimeOutParm)
					);
				}
				
				/** element: <interface>.<agg/red>.<mac-learning>.<static> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mac-learning",
						"static", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				for(int i=0; i<interfaceImpl.getInterMacLearningStaticSize(type); i++){
					
					oDebug.debug("/configuration/interface/"+type.name()+"/mac-learning",
							"static", GenerateXMLDebug.SET_NAME,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					macLearnObj.getStatic().add(
							CLICommonFunc.createAhNameActValue(interfaceImpl.getStaticMacAddr(type, i), CLICommonFunc.getYesDefault())
					);
				}
				
			}
		}
		aggRedChildList_1.clear();
		setRedxAggxChildLevel_3(type);
	}
	
	private void setRedxAggxChildLevel_3(InterfaceProfileInt.InterType type) throws Exception{
		/**
		 * <interface>.<agg/red>.<mode>.<bridge-access>			InterfaceMode.BridgeAccess
		 * <interface>.<agg/red>.<mode>.<access>					InterfaceMode.Access
		 * <interface>.<agg/red>.<mode>.<bridge-802.1q>			InterfaceMode.Bridge8021Q
		 * <interface>.<eth>.<bind>.<red0>					Eth.Bind.Red0
		 */
		for(Object childObj : ethChildList_2){
			
			/** element: <interface>.<agg/red>.<mode>.<access> */
			if(childObj instanceof InterfaceMode.Access){
				InterfaceMode.Access accessObj = (InterfaceMode.Access)childObj;
				
				/** attribute: operation */
				accessObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
			}
			
			/** element: <interface>.<agg/red>.<mode>.<bridge-access> */
			if(childObj instanceof InterfaceMode.BridgeAccess){
				InterfaceMode.BridgeAccess bridgeAccessObj = (InterfaceMode.BridgeAccess)childObj;
				
				/** attribute: operation */
				bridgeAccessObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
				
				/** element: <interface>.<agg/red>.<mode>.<bridge-access>.<user-profile-attribute> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mode/bridge-access",
						"user-profile-attribute", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigBridgeUserProfile(type)){
					
					oDebug.debug("/configuration/interface/"+type.name()+"/mode/bridge-access",
							"user-profile-attribute", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					Object[][] attrParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getInterAccessUserProfileAttr(type)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					bridgeAccessObj.setUserProfileAttribute(
							(InterfaceAttribute)CLICommonFunc.createObjectWithName(InterfaceAttribute.class, attrParm)
					);
					
				}
			}
			
			/** element: <interface>.<agg/red>.<mode>.<bridge-802.1q> */
			if(childObj instanceof InterfaceMode.Bridge8021Q){
				InterfaceMode.Bridge8021Q bridgeObj = (InterfaceMode.Bridge8021Q)childObj;
				
				/** attribute: operation */
				bridgeObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
				
				/** element: <interface>.<agg/red>.<mode>.<bridge-802.1q>.<user-profile-attribute> */
				oDebug.debug("/configuration/interface/"+type.name()+"/mode/bridge-802.1q",
						"user-profile-attribute", GenerateXMLDebug.CONFIG_ELEMENT,
						interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
				if(interfaceImpl.isConfigBridgeUserProfile(type)){
					
					oDebug.debug("/configuration/interface/"+type.name()+"/mode/bridge-802.1q",
							"user-profile-attribute", GenerateXMLDebug.SET_VALUE,
							interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
					Object[][] attrParm = {
							{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getInterAccessUserProfileAttr(type)},
							{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
					};
					bridgeObj.setUserProfileAttribute(
							(InterfaceAttribute)CLICommonFunc.createObjectWithName(InterfaceAttribute.class, attrParm)
					);
				}
			}
		}
		ethChildList_2.clear();
	}
	
	private Mgtxy createMgtxy(MgtType type) throws Exception{
		Mgtxy mgtxyObj = new Mgtxy();
		
		oDebug.debug("/configuration/interface",
				type.getValue(), GenerateXMLDebug.CONFIG_ELEMENT,
				interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
		if(interfaceImpl.isConfigMgtChild(type)){
			/** element: <interface>.<mgtx.y>.<vlan> */
			oDebug.debug("/configuration/interface/"+type.getValue(),
					"vlan", GenerateXMLDebug.SET_VALUE,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			Object[][] vlanObj = {
					{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getMgtChildVlan(type)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			mgtxyObj.setVlan(
					(InterfaceVlan)CLICommonFunc.createObjectWithName(InterfaceVlan.class, vlanObj)
			);
			
			/** element: <interface>.<mgtx.y>.<ip> */
			oDebug.debug("/configuration/interface/"+type.getValue(),
					"ip", GenerateXMLDebug.SET_VALUE,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			mgtxyObj.setIp(
					CLICommonFunc.createAhStringActObj(interfaceImpl.getMgtChildIp(type), CLICommonFunc.getYesDefault())
			);
			
			/** element: <interface>.<mgtx.y>.<ip-helper> */
			oDebug.debug("/configuration/interface/"+type.getValue(),
					"ip-helper", GenerateXMLDebug.CONFIG_ELEMENT,
					interfaceImpl.getHiveApGuiName(), interfaceImpl.getHiveApName());
			if(interfaceImpl.isConfigMgtChildIpHelper(type)){
				mgtxyObj.setIpHelper(this.createInterfaceIpHelper(type));
			}
			
			/** element: <interface>.<mgtx.y>.<manage> */
			mgtxyObj.setManage(this.createMgtxyManage(type));
			
			/** element: <interface>.<mgtx.y>.<dhcp-server> */
			mgtxyObj.setDhcpServer(this.createMgtDhcpServer(type));
			
		}
		
		/** element: <interface>.<mgtx.y>.<dns-server> */
		if(interfaceImpl.isConfigMgtDnsServer(type)){
			mgtxyObj.setDnsServer(this.createMgtDnsServer(type));
		}
		
		return mgtxyObj;
	}
	
	private MgtDnsResolver createMgtDnsResolver(String dns1, String dns2, String dns3){
		MgtDnsResolver dnsResolver = new MgtDnsResolver();
		
		/** attribute: operation */
		dnsResolver.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <dns1> */
		if(dns1 != null && !"".equals(dns1)){
			dnsResolver.setDns1(CLICommonFunc.createAhStringActObj(dns1, CLICommonFunc.getYesDefault()));
		}
		
		/** element: <dns2> */
		if(dns2 != null && !"".equals(dns2)){
			dnsResolver.setDns2(CLICommonFunc.createAhStringActObj(dns2, CLICommonFunc.getYesDefault()));
		}
		
		/** element: <dns3> */
		if(dns3 != null && !"".equals(dns3)){
			dnsResolver.setDns3(CLICommonFunc.createAhStringActObj(dns3, CLICommonFunc.getYesDefault()));
		}
		
		return dnsResolver;
	}
	
	private MgtDnsServerIntDomainName createMgtDnsServerIntDomainName(String domainName, String dnsServer){
		if(domainName == null || "".equals(domainName)){
			return null;
		}
		MgtDnsServerIntDomainName domainServer = new MgtDnsServerIntDomainName();
		
		/** attribute: name */
		domainServer.setName(domainName);
		
		/** attribute: operation */
		domainServer.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <cr> */
		if(dnsServer != null && !"".equals(dnsServer)){
			domainServer.setCr(CLICommonFunc.createAhName(dnsServer));
		}
		
		return domainServer;
	}
	
	private InterfaceGigabitEthernet createInterfaceGigabitEthernet(int index) throws Exception{
		InterfaceGigabitEthernet gbIntObj = new InterfaceGigabitEthernet();
		
		/** attribute: name */
		gbIntObj.setName(interfaceImpl.getInfPortName(DeviceInfType.Gigabit, index));
		
		/** attribute: operation */
		gbIntObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		if(!interfaceImpl.isBindToPortChannel(DeviceInfType.Gigabit, index)){
			
			/** element: <interface>.<gigabitethernet>.<speed> */
			Object[][] speedParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getInfSpeed(DeviceInfType.Gigabit, index)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			gbIntObj.setSpeed((InterfaceSpeed)CLICommonFunc.createObjectWithName(InterfaceSpeed.class, speedParm));
			
			/** element: <interface>.<gigabitethernet>.<duplex> */
			gbIntObj.setDuplex(this.createInterfaceDuplex(interfaceImpl.getInfDuplex(DeviceInfType.Gigabit, index)));
			
			/** element: <interface>.<gigabitethernet>.<flow-control> */
			InterfaceFlowControlValue fControlValue = interfaceImpl.getInfFlowControlValue(DeviceInfType.Gigabit, index);
			gbIntObj.setFlowControl(this.createInterfaceFlowControl(fControlValue));
			
			/** element: <interface>.<gigabitethernet>.<switchport> */
			if(interfaceImpl.isConfigInfSwitchPort(DeviceInfType.Gigabit, index)){
				gbIntObj.setSwitchport(this.createSwitchportObj(DeviceInfType.Gigabit, index));
			}
			
			/** element: <interface>.<gigabitethernet>.<shutdown> */
			gbIntObj.setShutdown(CLICommonFunc.getAhOnlyAct(interfaceImpl.isInfShutdown(
					DeviceInfType.Gigabit, index)));
			
			/** element: <interface>.<gigabitethernet>.<qos-classifier> */
			if(interfaceImpl.isConfigInfQosClassifier(DeviceInfType.Gigabit, index)){
				gbIntObj.setQosClassifier(CLICommonFunc.createAhNameActObj(
						interfaceImpl.getInfQosClassifierName(DeviceInfType.Gigabit, index), CLICommonFunc.getYesDefault()));
			}
			
			/** element: <interface>.<gigabitethernet>.<qos-marker> */
			if(interfaceImpl.isConfigInfQosMarker(DeviceInfType.Gigabit, index)){
				gbIntObj.setQosMarker(CLICommonFunc.createAhNameActObj(
						interfaceImpl.getInfQosMarkerName(DeviceInfType.Gigabit, index), CLICommonFunc.getYesDefault()));
			}
			
			/** element: <interface>.<gigabitethernet>.<qos-shaper> */
			if(interfaceImpl.isConfigInfQosShaper(DeviceInfType.Gigabit, index)){
				gbIntObj.setQosShaper(CLICommonFunc.createAhIntActObj(
						interfaceImpl.getInfQosShaperValue(DeviceInfType.Gigabit, index), CLICommonFunc.getYesDefault()));
			}
			
			/** element: <interface>.<gigabitethernet>.<spanning-tree> */
			if(interfaceImpl.isConfigSpanningTree(DeviceInfType.Gigabit, index)){
				gbIntObj.setSpanningTree(this.createInterfaceSpanningTree(DeviceInfType.Gigabit, index));
			}
			
			/** element: <interface>.<gigabitethernet>.<client-report> */
			if(interfaceImpl.isConfigClientReport(DeviceInfType.Gigabit, index)){
				gbIntObj.setClientReport(this.createInterfaceClientReport(
						interfaceImpl.isEnableClientReport(DeviceInfType.Gigabit, index)));
			}
			
		}
		
		/** element: <interface>.<gigabitethernet>.<auto-mdix> */
		boolean autoMdixEnable = interfaceImpl.isEnableInfAutoMdix(DeviceInfType.Gigabit, index);
		gbIntObj.setAutoMdix(this.createInterfaceAutoMdix(autoMdixEnable));

		/** element: <interface>.<gigabitethernet>.<link-debounce> */
		gbIntObj.setLinkDebounce(CLICommonFunc.createAhIntActObj(
				interfaceImpl.getInfLinkDebounce(DeviceInfType.Gigabit, index), CLICommonFunc.getYesDefault()));
		
		/** element: <interface>.<gigabitethernet>.<port-channel> */
		if(interfaceImpl.isConfigInfPortChannel(DeviceInfType.Gigabit, index)){
			gbIntObj.setAgg(CLICommonFunc.createAhName(
					interfaceImpl.getInfPortChannel(DeviceInfType.Gigabit, index)));
		}
		
		/** element: <interface>.<gigabitethernet>.<security-object> */
		if(interfaceImpl.isConfigInfSecurityObject(DeviceInfType.Gigabit, index)){
			gbIntObj.setSecurityObject(createGethSecurityObject(DeviceInfType.Gigabit, index));
		}
		
		/** element: <interface>.<gigabitethernet>.<dhcp> */
		if(interfaceImpl.isConfigInfDhcpClient(DeviceInfType.Gigabit, index)){
			boolean dhcpClient = interfaceImpl.isEnableInfDhcpClient(DeviceInfType.Gigabit, index);
			gbIntObj.setDhcp(this.createEthxDhcp(dhcpClient));
		}
		
		/** element: <interface>.<gigabitethernet>.<ip> */
		if(interfaceImpl.isConfigInfIp(DeviceInfType.Gigabit, index)){
			gbIntObj.setIp(CLICommonFunc.createAhNameActObj(
					interfaceImpl.getInfIp(DeviceInfType.Gigabit, index), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <interface>.<gigabitethernet>.<mode> */
		if(interfaceImpl.isConfigInfWAN(DeviceInfType.Gigabit, index)){
			gbIntObj.setMode(this.createGethMode(DeviceInfType.Gigabit, index));
		}
		
		/** element: <interface>.<gigabitethernet>.<storm-control> */
		if(interfaceImpl.isConfigInfStormControl(DeviceInfType.Gigabit, index)){
			gbIntObj.setStormControl(this.createInterfaceStormControl(DeviceInfType.Gigabit, index));
		}
		
		/** element: <interface>.<gigabitethernet>.<link-discovery> */
		gbIntObj.setLinkDiscovery(this.createInterfaceGethLinkDiscovery(DeviceInfType.Gigabit, index));
		
		/** element: <interface>.<gigabitethernet>.<pse> */
		if(interfaceImpl.isConfigInfPse(DeviceInfType.Gigabit, index)){
			gbIntObj.setPse(createInterfaceGethPse(DeviceInfType.Gigabit, index));
		}
		
		/** element: <interface>.<gigabitethernet>.<description> */
		if(interfaceImpl.isConfigInfDescription(DeviceInfType.Gigabit, index)){
			gbIntObj.setDescription(CLICommonFunc.createAhStringActObj(
					interfaceImpl.getInfDescription(DeviceInfType.Gigabit, index), CLICommonFunc.getYesDefault()));
		}
		
		return gbIntObj;
	}
	
	private InterfaceGigabitEthernet createInterfaceSfp(int index) throws Exception{
		InterfaceGigabitEthernet sfpObj = new InterfaceGigabitEthernet();
		
		/** attribute: name */
		sfpObj.setName(interfaceImpl.getInfPortName(DeviceInfType.SFP, index));
		
		/** attribute: operation */
		sfpObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		if(!interfaceImpl.isBindToPortChannel(DeviceInfType.SFP, index)){
			
			/** element: <interface>.<sfp>.<speed> */
			Object[][] speedParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getInfSpeed(DeviceInfType.SFP, index)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			sfpObj.setSpeed((InterfaceSpeed)CLICommonFunc.createObjectWithName(InterfaceSpeed.class, speedParm));
			
			/** element: <interface>.<sfp>.<duplex> */
			Object[][] duplexParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getInfDuplex(DeviceInfType.SFP, index)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			sfpObj.setDuplex((InterfaceDuplex)CLICommonFunc.createObjectWithName(InterfaceDuplex.class, duplexParm));
			
			/** element: <interface>.<sfp>.<flow-control> */
			InterfaceFlowControlValue fControlValue = interfaceImpl.getInfFlowControlValue(DeviceInfType.SFP, index);
			sfpObj.setFlowControl(this.createInterfaceFlowControl(fControlValue));
			
			/** element: <interface>.<sfp>.<switchport> */
			if(interfaceImpl.isConfigInfSwitchPort(DeviceInfType.SFP, index)){
				sfpObj.setSwitchport(this.createSwitchportObj(DeviceInfType.SFP, index));
			}
			
			/** element: <interface>.<sfp>.<shutdown> */
			sfpObj.setShutdown(CLICommonFunc.getAhOnlyAct(interfaceImpl.isInfShutdown(
					DeviceInfType.SFP, index)));
			
			/** element: <interface>.<sfp>.<qos-classifier> */
			if(interfaceImpl.isConfigInfQosClassifier(DeviceInfType.SFP, index)){
				sfpObj.setQosClassifier(CLICommonFunc.createAhNameActObj(
						interfaceImpl.getInfQosClassifierName(DeviceInfType.SFP, index), CLICommonFunc.getYesDefault()));
			}
			
			/** element: <interface>.<sfp>.<qos-marker> */
			if(interfaceImpl.isConfigInfQosMarker(DeviceInfType.SFP, index)){
				sfpObj.setQosMarker(CLICommonFunc.createAhNameActObj(
						interfaceImpl.getInfQosMarkerName(DeviceInfType.SFP, index), CLICommonFunc.getYesDefault()));
			}
			
			/** element: <interface>.<sfp>.<qos-shaper> */
			if(interfaceImpl.isConfigInfQosShaper(DeviceInfType.SFP, index)){
				sfpObj.setQosShaper(CLICommonFunc.createAhIntActObj(
						interfaceImpl.getInfQosShaperValue(DeviceInfType.SFP, index), CLICommonFunc.getYesDefault()));
			}
			
			/** element: <interface>.<sfp>.<spanning-tree> */
			if(interfaceImpl.isConfigSpanningTree(DeviceInfType.SFP, index)){
				sfpObj.setSpanningTree(this.createInterfaceSpanningTree(DeviceInfType.SFP, index));
			}
			
			/** element: <interface>.<sfp>.<client-report> */
			if(interfaceImpl.isConfigClientReport(DeviceInfType.SFP, index)){
				sfpObj.setClientReport(this.createInterfaceClientReport(
						interfaceImpl.isEnableClientReport(DeviceInfType.SFP, index)));
			}
		}
		
		/** element: <interface>.<sfp>.<storm-control> */
		if(interfaceImpl.isConfigInfStormControl(DeviceInfType.SFP, index)){
			sfpObj.setStormControl(this.createInterfaceStormControl(DeviceInfType.SFP, index));
		}
		
		/** element: <interface>.<sfp>.<link-debounce> */
		sfpObj.setLinkDebounce(CLICommonFunc.createAhIntActObj(
				interfaceImpl.getInfLinkDebounce(DeviceInfType.SFP, index), CLICommonFunc.getYesDefault()));
		
		/** element: <interface>.<sfp>.<port-channel> */
		if(interfaceImpl.isConfigInfPortChannel(DeviceInfType.SFP, index)){
			sfpObj.setAgg(CLICommonFunc.createAhName(
					interfaceImpl.getInfPortChannel(DeviceInfType.SFP, index)));
		}
		
		/** element: <interface>.<sfp>.<dhcp> */
		if(interfaceImpl.isConfigInfDhcpClient(DeviceInfType.SFP, index)){
			boolean dhcpClient = interfaceImpl.isEnableInfDhcpClient(DeviceInfType.SFP, index);
			sfpObj.setDhcp(this.createEthxDhcp(dhcpClient));
		}
		
		/** element: <interface>.<sfp>.<ip> */
		if(interfaceImpl.isConfigInfIp(DeviceInfType.SFP, index)){
			sfpObj.setIp(CLICommonFunc.createAhNameActObj(
					interfaceImpl.getInfIp(DeviceInfType.SFP, index), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <interface>.<sfp>.<mode> */
		if(interfaceImpl.isConfigInfWAN(DeviceInfType.SFP, index)){
			sfpObj.setMode(this.createGethMode(DeviceInfType.SFP, index));
		}
		
		/** element: <interface>.<sfp>.<link-discovery> */
		sfpObj.setLinkDiscovery(this.createInterfaceGethLinkDiscovery(DeviceInfType.SFP, index));
		
		/** element: <interface>.<sfp>.<description> */
		if(interfaceImpl.isConfigInfDescription(DeviceInfType.SFP, index)){
			sfpObj.setDescription(CLICommonFunc.createAhStringActObj(
					interfaceImpl.getInfDescription(DeviceInfType.SFP, index), CLICommonFunc.getYesDefault()));
		}
		
		return sfpObj;
	}
	
	private InterfacePortChannel createInterfacePortChannel(int index) throws Exception{
		InterfacePortChannel portChannel = new InterfacePortChannel();
		
		/** attribute: name */
		portChannel.setName(interfaceImpl.getInfPortName(DeviceInfType.PortChannel, index));
		
		/** attribute: operation */
		portChannel.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <interface>.<port-channel>.<speed> */
		Object[][] speedParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getInfSpeed(DeviceInfType.PortChannel, index)},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		portChannel.setSpeed((InterfaceSpeed)CLICommonFunc.createObjectWithName(InterfaceSpeed.class, speedParm));
		
		/** element: <interface>.<port-channel>.<duplex> */
		Object[][] duplexParm = {
				{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getInfDuplex(DeviceInfType.PortChannel, index)},
				{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
		};
		portChannel.setDuplex((InterfaceDuplex)CLICommonFunc.createObjectWithName(InterfaceDuplex.class, duplexParm));
		
		/** element: <interface>.<port-channel>.<flow-control> */
		InterfaceFlowControlValue fControlValue = interfaceImpl.getInfFlowControlValue(DeviceInfType.PortChannel, index);
		portChannel.setFlowControl(this.createInterfaceFlowControl(fControlValue));
		
		/** element: <interface>.<port-channel>.<switchport> */
		if(interfaceImpl.isConfigInfSwitchPort(DeviceInfType.PortChannel, index)){
			portChannel.setSwitchport(this.createSwitchportObj(DeviceInfType.PortChannel, index));
		}
		
		/** element: <interface>.<port-channel>.<shutdown> */
		portChannel.setShutdown(CLICommonFunc.getAhOnlyAct(interfaceImpl.isInfShutdown(
				DeviceInfType.PortChannel, index)));
		
		/** element: <interface>.<port-channel>.<qos-classifier> */
		if(interfaceImpl.isConfigInfQosClassifier(DeviceInfType.PortChannel, index)){
			portChannel.setQosClassifier(CLICommonFunc.createAhNameActObj(
					interfaceImpl.getInfQosClassifierName(DeviceInfType.PortChannel, index), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <interface>.<port-channel>.<qos-marker> */
		if(interfaceImpl.isConfigInfQosMarker(DeviceInfType.PortChannel, index)){
			portChannel.setQosMarker(CLICommonFunc.createAhNameActObj(
					interfaceImpl.getInfQosMarkerName(DeviceInfType.PortChannel, index), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <interface>.<port-channel>.<qos-shaper> */
		if(interfaceImpl.isConfigInfQosShaper(DeviceInfType.PortChannel, index)){
			portChannel.setQosShaper(CLICommonFunc.createAhIntActObj(
					interfaceImpl.getInfQosShaperValue(DeviceInfType.PortChannel, index), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <interface>.<port-channel>.<spanning-tree> */
		if(interfaceImpl.isConfigSpanningTree(DeviceInfType.PortChannel, index)){
			portChannel.setSpanningTree(this.createInterfaceSpanningTree(DeviceInfType.PortChannel, index));
		}
		
		/** element: <interface>.<agg>.<client-report> */
		if(interfaceImpl.isConfigClientReport(DeviceInfType.PortChannel, index)){
			portChannel.setClientReport(this.createInterfaceClientReport(
					interfaceImpl.isEnableClientReport(DeviceInfType.PortChannel, index)));
		}
		
		/** element: <interface>.<agg>.<description> */
		if(interfaceImpl.isConfigInfDescription(DeviceInfType.PortChannel, index)){
			portChannel.setDescription(CLICommonFunc.createAhStringActObj(
					interfaceImpl.getInfDescription(DeviceInfType.PortChannel, index), CLICommonFunc.getYesDefault()));
		}
		
		return portChannel;
	}
	
	private InterfaceVlanx createInterfaceVlanx(MgtType type) throws Exception{
		InterfaceVlanx vlanObj = new InterfaceVlanx();
		
		/** attribute: name */
		vlanObj.setName(String.valueOf(interfaceImpl.getMgtChildVlan(type)));
		
		/** attribute: operation */
		vlanObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
		
		/** element: <interface>.<vlan>.<ip> */
		vlanObj.setIp(CLICommonFunc.createAhStringActObj(
				interfaceImpl.getMgtChildIp(type), CLICommonFunc.getYesDefault()));
		
		/** element: <interface>.<vlan>.<dhcp-server> */
		vlanObj.setDhcpServer(this.createMgtDhcpServer(type));
		
		/** element: <interface>.<vlan>.<ip-helper> */
		if(interfaceImpl.isConfigMgtChildIpHelper(type)){
			vlanObj.setIpHelper(this.createInterfaceIpHelper(type));
		}
		
		/** element: <interface>.<vlan>.<dns-server> */
		if(interfaceImpl.isConfigMgtDnsServer(type)){
			vlanObj.setDnsServer(this.createMgtDnsServer(type));
		}
		
		return vlanObj;
	}
	
	private InterfaceSwitchport createSwitchportObj(DeviceInfType type, int index) throws CreateXMLException{
		InterfaceSwitchport switchPort = new InterfaceSwitchport();
		
		int accessVlan = interfaceImpl.getAccessVlan(type, index);
		int nativeVlan = interfaceImpl.getNativeVlan(type, index);
		int voiceVlan = interfaceImpl.getInfPortVoiceVlan(type, index);
		String[] allowedVlan = interfaceImpl.getInfPortAllowedVlan(type, index);
		
		List<Object> childList_1 = new ArrayList<Object>();
		List<Object> childList_2 = new ArrayList<Object>();
		List<Object> childList_3 = new ArrayList<Object>();
		
		
		/** Level_1	##################################################################### */
		{
			/** element: <mode> */
			switchPort.setMode(new InterfaceSwitchportMode());
			switchPort.getMode().setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			switchPort.getMode().setValue(interfaceImpl.getInfPortVlanMode(type, index));
			
			/** element: <access> */
			if(interfaceImpl.isConfigSwitchPortAccess(type, index)){
				InterfaceSwitchportAccess accessObj = new InterfaceSwitchportAccess(); 
				switchPort.setAccess(accessObj);
				childList_1.add(accessObj);
			}
			
			/** element: <trunk> */
			if(interfaceImpl.isConfigSwitchPortTrunk(type, index)){
				InterfaceSwitchportTrunk trunkObj = new InterfaceSwitchportTrunk();
				switchPort.setTrunk(trunkObj);
				childList_1.add(trunkObj);
			}
			
			/** element: <user-profile-attribute> */
			if(interfaceImpl.isConfigPortUserProfileId(type, index)){
				switchPort.setUserProfileAttribute(CLICommonFunc.createAhIntActObj(
						interfaceImpl.getPortUserProfileId(type, index), CLICommonFunc.getYesDefault()));
			}
		}
		
		/** Level_2	##################################################################### */
		{
			for(Object childObj : childList_1){
				
				/** element: <access> */
				if(childObj instanceof InterfaceSwitchportAccess){
					InterfaceSwitchportAccess accessObj = (InterfaceSwitchportAccess)childObj;
					
					if(accessVlan > 0 ){
						accessObj.setVlan(CLICommonFunc.createAhIntActObj(accessVlan, CLICommonFunc.getYesDefault()));
					}
				}
				
				/** element: <trunk> */
				if(childObj instanceof InterfaceSwitchportTrunk){
					InterfaceSwitchportTrunk trunkObj = (InterfaceSwitchportTrunk)childObj;
					
					/** element: <trunk>.<voice-vlan> */
					if(voiceVlan > 0){
						trunkObj.setVoiceVlan(CLICommonFunc.createAhIntActObj(voiceVlan, CLICommonFunc.getYesDefault()));
					}
					
					/** element: <trunk>.<native> */
					InterfaceSwitchportNative nativeObj = new InterfaceSwitchportNative();
					trunkObj.setNative(nativeObj);
					childList_2.add(nativeObj);
					
					/** element: <trunk>.<allow> */
					InterfaceSwitchportTrunkAllow allowObj = new InterfaceSwitchportTrunkAllow();
					trunkObj.setAllow(allowObj);
					childList_2.add(allowObj);
				}
			}
			childList_1.clear();
		}
		
		/** Level_3	##################################################################### */
		{
			for(Object childObj : childList_2){
				
				/** element: <trunk>.<native> */
				if(childObj instanceof InterfaceSwitchportNative){
					InterfaceSwitchportNative nativeObj = (InterfaceSwitchportNative)childObj;
					
					/** element: <trunk>.<native>.<vlan> */
					if(nativeVlan > 0){
						nativeObj.setVlan(CLICommonFunc.createAhIntActObj(nativeVlan, CLICommonFunc.getYesDefault()));
					}
				}
				
				/** element: <trunk>.<allow> */
				if(childObj instanceof InterfaceSwitchportTrunkAllow){
					InterfaceSwitchportTrunkAllow allowObj = (InterfaceSwitchportTrunkAllow)childObj;
					
					/** element: <trunk>.<allow>.<vlan> */
					InterfaceSwitchportTrunkAllowVlan vlanObj = new InterfaceSwitchportTrunkAllowVlan();
					childList_3.add(vlanObj);
					allowObj.setVlan(vlanObj);
				}
			}
			childList_2.clear();
		}
		
		/** Level_4	##################################################################### */
		{
			for(Object childObj : childList_3){
				
				/** element: <trunk>.<allow>.<vlan> */
				if(childObj instanceof InterfaceSwitchportTrunkAllowVlan){
					InterfaceSwitchportTrunkAllowVlan vlanObj = (InterfaceSwitchportTrunkAllowVlan)childObj;
					
					/** element: <trunk>.<allow>.<vlan>.<all> */
					vlanObj.setAll(CLICommonFunc.getAhOnlyAct(interfaceImpl.isInfPortAllowedVlanAll(type, index)));
					
					/** element: <trunk>.<allow>.<vlan>.<cr> */
					if(allowedVlan != null && allowedVlan.length > 0){
						for(int i=0; i<allowedVlan.length; i++){
							vlanObj.getCr().add(
									CLICommonFunc.createAhNameActValueQuoteProhibited(
											allowedVlan[i], CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault())
							);
						}
					}
				}
			}
			childList_3.clear();
		}
		
		childList_1 = null;
		childList_2 = null;
		childList_3 = null;
		
		return switchPort;
	}
	
	private EthxDhcp createEthxDhcp(boolean enableDhcp){
		EthxDhcp dhcp = new EthxDhcp();
		
		/** element: <client> */
		dhcp.setClient(new EthxDhcpClient());
		
		/** element: <client>.<cr> */
		dhcp.getClient().setCr(CLICommonFunc.getAhOnlyAct(enableDhcp));
		
		return dhcp;
	}
	
	private GethMode createGethMode(DeviceInfType type, int index){
		GethMode modeObj = new GethMode();
		
		List<Object> childList_1 = new ArrayList<Object>();
		
		{ /** Level_1 ############################################################# */
			
			/** attribute: operation */
			modeObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
			
			/** element: <wan> */
			GethModeWan wanObj = new GethModeWan();
			modeObj.setWan(wanObj);
			childList_1.add(wanObj);
		}
		
		{/** Level_2 ############################################################# */
			
			for(Object childObj :childList_1 ){
				
				/** element: <wan> */
				if(childObj instanceof GethModeWan){
					GethModeWan wanObj = (GethModeWan)childObj;
					
					/** element: <wan>.<cr> */
					wanObj.setCr("");
					
					/** element: <wan>.<nat> */
					wanObj.setNat(CLICommonFunc.getAhOnlyAct(interfaceImpl.isInfWanNatEnable(type, index)));
					
					/** element: <wan>.<nat-policy> */
					if (interfaceImpl.isInfWanNatPolicyEnable(type, index)) {
						for (int i = 0 ; i < interfaceImpl.getNatPolicyNameForPortForwardingSize(); i++) {
							wanObj.getNatPolicy().add(CLICommonFunc.createAhNameActValue(interfaceImpl.getNatPolicyNameForPortForwarding(i), true));
						}
					}
					
					/** element: <interface>.<gigabitethernet>.<mode>.<wan>.<priority> */
					wanObj.setPriority(CLICommonFunc.createAhIntNameActObj(
							interfaceImpl.getWanInterfacePriority(type,index + 1), CLICommonFunc.getYesDefault()));
				}
			}
			childList_1.clear();
			
		}
		
		childList_1 = null;
		
		return modeObj;
	}
	
	private InterfaceAutoMdix createInterfaceAutoMdix(boolean enable){
		InterfaceAutoMdix autoMdix = new InterfaceAutoMdix();
		
		/** element: <auto-mdix>.<enable> */
		autoMdix.setEnable(CLICommonFunc.getAhOnlyAct(enable));
		
		return autoMdix;
	}
	
	private InterfaceFlowControl createInterfaceFlowControl(InterfaceFlowControlValue value){
		InterfaceFlowControl fControl = new InterfaceFlowControl();
		
		/** attribute: operation */
		fControl.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** attribute: value */
		fControl.setValue(value);
		
		return fControl;
	}
	
	private InterfaceStormControl createInterfaceStormControl(DeviceInfType type, int index){
		InterfaceStormControl stormControl = new InterfaceStormControl();
		
		/** element: <storm-control>.<type> */
		stormControl.setType(new StormControlType());
		
		/** element: <storm-control>.<rate-limit> */
		stormControl.setRateLimit(new InterfaceStormControlRateLimit());
		stormControl.getRateLimit().setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <storm-control>.<type>.<value> */
		stormControl.getType().setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** element: <storm-control>.<type>.<all> */
		if(interfaceImpl.isConfigStormControlAll(type, index)){
			stormControl.getType().setAll(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <storm-control>.<type>.<broadcast> */
		if(interfaceImpl.isConfigStormControlBroadcast(type, index)){
			stormControl.getType().setBroadcast(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <storm-control>.<type>.<multicast> */
		if(interfaceImpl.isConfigStormControlMulticast(type, index)){
			stormControl.getType().setMulticast(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <storm-control>.<type>.<unknown-unicast> */
		if(interfaceImpl.isConfigStormControlUnknownUnicast(type, index)){
			stormControl.getType().setUnknownUnicast(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <storm-control>.<type>.<tcp-syn> */
		if(interfaceImpl.isConfigStormControlTcpSyn(type, index)){
			stormControl.getType().setTcpSyn(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		}
		
		/** element: <storm-control>.<type>.<mode> */
		GethStormControlMode modeObj = new GethStormControlMode();
		modeObj.setName(interfaceImpl.isConfigStormControlMode());
		stormControl.getType().setMode(modeObj);
		
		/** element: <storm-control>.<rate-limit>.<percentage> */
		if(interfaceImpl.isConfigStormControlPercentage(type, index)){
			stormControl.getRateLimit().setPercentage(CLICommonFunc.getAhInt(
					interfaceImpl.getStormControlPercentage(type, index)));
		}
		
		/** element: <storm-control>.<rate-limit>.<bps> */
		if(interfaceImpl.isConfigStormControlBps(type, index)){
			stormControl.getRateLimit().setBps(CLICommonFunc.getAhInt(
					interfaceImpl.getStormControlBps(type, index)));
		}
		
		/** element: <storm-control>.<rate-limit>.<pps> */
		if(interfaceImpl.isConfigStormControlPps(type, index)){
			stormControl.getRateLimit().setPps(CLICommonFunc.getAhInt(
					interfaceImpl.getStormControlPps(type, index)));
		}
		
		/** element: <storm-control>.<rate-limit>.<kbps> */
		if(interfaceImpl.isConfigStormControlKbps(type, index)){
			stormControl.getRateLimit().setKbps(CLICommonFunc.getAhInt(
					interfaceImpl.getStormControlKbps(type, index)));
		}
		
		return stormControl;
	}
	
	private MgtDhcpServer createMgtDhcpServer(MgtType type) throws Exception{
		MgtDhcpServer dhcpServer = new MgtDhcpServer();
		
		/** Level_1	##################################################################### */
		List<Object> dhcpList_1 = new ArrayList<Object>();
		{
			/** element: <dhcp-server>.<enable> */
			DhcpServerEnable enable = new DhcpServerEnable();
			dhcpList_1.add(enable);
			dhcpServer.setEnable(enable);
			
			if(interfaceImpl.isEnableMgtChildDhcpServer(type)){
				/** element: <dhcp-server>.<authoritative-flag> */
				dhcpServer.setAuthoritativeFlag(CLICommonFunc.getAhOnlyAct(
						interfaceImpl.isEnableMgtChildAuthoritative(type)));
				
				/** element: <dhcp-server>.<ipPool> */
				for(int i=0; i<interfaceImpl.getMgtChildIpPoolSize(type); i++){
					dhcpServer.getIpPool().add(
							CLICommonFunc.createAhNameActValueQuoteProhibited(
									interfaceImpl.getMgtChildIpPoolName(type, i), 
									CLICommonFunc.getYesDefault(),CLICommonFunc.getYesDefault())
					);
				}
				
				/** element: <dhcp-server>.<arp-check> */
				dhcpServer.setArpCheck(CLICommonFunc.getAhOnlyAct(
						interfaceImpl.isEnableMgtChildArpCheck(type)));
				
				/** element: <dhcp-server>.<options> */
				MgtDhcpServer.Options optionsObj = new MgtDhcpServer.Options();
				dhcpList_1.add(optionsObj);
				dhcpServer.setOptions(optionsObj);
			}
		}
		
		/** Level_2	##################################################################### */
		List<Object> dhcpList_2 = new ArrayList<Object>();
		{
			for(Object childObj : dhcpList_1){
				/** element: <dhcp-server>.<enable> */
				if(childObj instanceof DhcpServerEnable){
					DhcpServerEnable dhcpEnable = (DhcpServerEnable)childObj;
					
					/** operation: operation */
					dhcpEnable.setOperation(CLICommonFunc.getAhEnumAct(
							interfaceImpl.isEnableMgtChildDhcpServer(type)));
					
					/** element: <dhcp-server>.<enable>.<cr> */
					dhcpEnable.setCr("");
				}
				
				/** element: <dhcp-server>.<options> */
				if(childObj instanceof MgtDhcpServer.Options){
					MgtDhcpServer.Options optionsObj = (MgtDhcpServer.Options)childObj;
					
					/** element: <dhcp-server>.<options>.<default-gateway> */
					if(interfaceImpl.isConfigMgtChildOptionsDefaultGateway(type)){
						DhcpServerOptionsDefaultGateway defaultGatewayObj = new DhcpServerOptionsDefaultGateway();
						dhcpList_2.add(defaultGatewayObj);
						optionsObj.setDefaultGateway(defaultGatewayObj);
					}
					
					/** element: <dhcp-server>.<options>.<lease-time> */
					if(interfaceImpl.isConfigMgtChildOptionsLeaseTime(type)){
						Object[][] leasTimeParm = {
								{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getMgtChildOptionsLeaseTime(type)},
								{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
						};
						optionsObj.setLeaseTime(
								(MgtDhcpServer.Options.LeaseTime)CLICommonFunc.createObjectWithName(
										MgtDhcpServer.Options.LeaseTime.class, leasTimeParm)
						);
					}
					
					/** element: <dhcp-server>.<options>.<netmask> */
					if(interfaceImpl.isConfigMgtChildOptionsNetMask(type)){
						optionsObj.setNetmask(CLICommonFunc.createAhStringActObj(
								interfaceImpl.getMgtChildOptionsNetMask(type), CLICommonFunc.getYesDefault()));
					}
					
					/** element: <dhcp-server>.<options>.<hivemanager> */
					for(int i=0; i<interfaceImpl.getMgtChildOptionsHivemanagerSize(type); i++){
						optionsObj.getHivemanager().add(CLICommonFunc.createAhNameActValue(
								interfaceImpl.getMgtChildOptionsHivemanager(type, i), CLICommonFunc.getYesDefault()));
					}
					
					/** element: <dhcp-server>.<options>.<domain-name> */
					if(interfaceImpl.isConfigMgtChildOptionsDoMain(type)){
						optionsObj.setDomainName(CLICommonFunc.createAhStringActObj(
								interfaceImpl.getMgtChildOptionsDoMain(type), CLICommonFunc.getYesDefault()));
					}
					
					/** element: <dhcp-server>.<options>.<mtu> */
					if(interfaceImpl.isConfigMgtChildOptionsMtu(type)){
						Object[][] mtuParm = {
								{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getMgtChildOptionsMtu(type)},
								{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
						};
						optionsObj.setMtu(
								(MgtDhcpServer.Options.Mtu)CLICommonFunc.createObjectWithName(MgtDhcpServer.Options.Mtu.class, mtuParm)
						);
					}
					
					/** element: <dhcp-server>.<options>.<dns1> */
					if(interfaceImpl.isConfigMgtChildOptionsDns1(type)){
						optionsObj.setDns1(CLICommonFunc.createAhStringActObj(
								interfaceImpl.getMgtChildOptionsDns1(type), CLICommonFunc.getYesDefault()));
					}
					
					/** element: <dhcp-server>.<options>.<dns2> */
					if(interfaceImpl.isConfigMgtChildOptionsDns2(type)){
						optionsObj.setDns2(CLICommonFunc.createAhStringActObj(
								interfaceImpl.getMgtChildOptionsDns2(type), CLICommonFunc.getYesDefault()));
					}
					
					/** element: <dhcp-server>.<options>.<dns3> */
					if(interfaceImpl.isConfigMgtChildOptionsDns3(type)){
						optionsObj.setDns3(CLICommonFunc.createAhStringActObj(
								interfaceImpl.getMgtChildOptionsDns3(type), CLICommonFunc.getYesDefault()));
					}
					
					/** element: <dhcp-server>.<options>.<ntp1> */
					if(interfaceImpl.isConfigMgtChildOptionsNtp1(type)){
						optionsObj.setNtp1(CLICommonFunc.createAhStringActObj(
								interfaceImpl.getMgtChildOptionsNtp1(type), CLICommonFunc.getYesDefault()));
					}
					
					/** element: <dhcp-server>.<options>.<ntp2> */
					if(interfaceImpl.isConfigMgtChildOptionsNtp2(type)){
						optionsObj.setNtp2(CLICommonFunc.createAhStringActObj(
								interfaceImpl.getMgtChildOptionsNtp2(type), CLICommonFunc.getYesDefault()));
					}
					
					/** element: <dhcp-server>.<options>.<pop3> */
					if(interfaceImpl.isConfigMgtChildOptionsPop3(type)){
						optionsObj.setPop3(CLICommonFunc.createAhStringActObj(
								interfaceImpl.getMgtChildOptionsPop3(type), CLICommonFunc.getYesDefault()));
					}
					
					/** element: <dhcp-server>.<options>.<smtp> */
					if(interfaceImpl.isConfigMgtChildOptionsSmtp(type)){
						optionsObj.setSmtp(CLICommonFunc.createAhStringActObj(
								interfaceImpl.getMgtChildOptionsSmtp(type), CLICommonFunc.getYesDefault()));
					}
					
					if(interfaceImpl.isConfigMgtChildOptionsWins1(type)){
						/** element: <dhcp-server>.<options>.<wins> */
						optionsObj.setWins(CLICommonFunc.createAhStringActObj(
								interfaceImpl.getMgtChildOptionsWins1(type), CLICommonFunc.getYesDefault()));
						
						/** element: <dhcp-server>.<options>.<wins1> */
						optionsObj.setWins1(CLICommonFunc.createAhStringActObj(
								interfaceImpl.getMgtChildOptionsWins1(type), CLICommonFunc.getYesDefault()));
					}
					
					/** element: <interface>.<mgtx.y>.<dhcp-server>.<options>.<wins2> */
					if(interfaceImpl.isConfigMgtChildOptionsWins2(type)){
						optionsObj.setWins2(CLICommonFunc.createAhStringActObj(
								interfaceImpl.getMgtChildOptionsWins2(type), CLICommonFunc.getYesDefault()));
					}
					
					/** element: <interface>.<mgtx.y>.<dhcp-server>.<options>.<logsrv> */
					if(interfaceImpl.isConfigMgtChildOptionsLogsrv(type)){
						optionsObj.setLogsrv(CLICommonFunc.createAhStringActObj(
								interfaceImpl.getMgtChildOptionsLogsrv(type), CLICommonFunc.getYesDefault()));
					}
					
					/** element: <interface>.<mgtx.y>.<dhcp-server>.<options>.<custom> */
					for(int i=0; i<interfaceImpl.getMgtChildOptionsCustomSize(type); i++){
						optionsObj.getCustom().add(createDhcpServerOptionsCustom(type, i));
					}
				}
			}
			dhcpList_1.clear();
		}
		
		/** Level_3	##################################################################### */
		{
			for(Object childObj : dhcpList_2){
				
				/** element: <dhcp-server>.<options>.<default-gateway> */
				if(childObj instanceof DhcpServerOptionsDefaultGateway){
					DhcpServerOptionsDefaultGateway defaultGatewayObj = (DhcpServerOptionsDefaultGateway)childObj;
					
					/** attribute: value */
					defaultGatewayObj.setValue(interfaceImpl.getMgtChildOptionsDefaultGateway(type));
					
					/** attribute: operation */
					defaultGatewayObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
					
					/** element: <dhcp-server>.<options>.<default-gateway>.<nat-support> */
					if(interfaceImpl.isMgtDhcpNatSupport(type)){
						defaultGatewayObj.setNatSupport(CLICommonFunc.getAhOnlyAct(interfaceImpl.isMgtDhcpNatSupport(type)));
					}
				}
			}
			dhcpList_2.clear();
		}
		dhcpList_1 = null;
		dhcpList_2 = null;
		
		return dhcpServer;
	}
	
	private DhcpServerOptionsCustom createDhcpServerOptionsCustom(MgtType type, int index) throws Exception{
		
		DhcpServerOptionsCustom customObj = new DhcpServerOptionsCustom();
		
		/** attribute: name */
		customObj.setName(interfaceImpl.getMgtChildOptionsCustomName(type, index));
		
		/** attribute: operation */
		customObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** element: <dhcp-server>.<options>.<custom>.<integer> */
		if(interfaceImpl.isConfigMgtChildOptionsCustomInteger(type, index)){
			Object[][] integerParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getMgtChildOptionsCustomIntegerValue(type, index)}
			};
			customObj.setInteger(
					(DhcpServerOptionsCustom.Integer)CLICommonFunc.createObjectWithName(DhcpServerOptionsCustom.Integer.class, integerParm)
			);
		}
		
		/** element: <dhcp-server>.<options>.<custom>.<ip> */
		if(interfaceImpl.isConfigMgtChildOptionsCustomIp(type, index)){
			customObj.setIp(CLICommonFunc.createAhStringObj(interfaceImpl.getMgtChildOptionsCustomIpValue(type, index)));
		}
		
		/** element: <dhcp-server>.<options>.<custom>.<string> */
		if(interfaceImpl.isConfigMgtChildOptionsCustomString(type, index)){
			customObj.setString(CLICommonFunc.createAhStringObj(interfaceImpl.getMgtChildOptionsCustomStringValue(type, index)));
		}
		
		/** element: <dhcp-server>.<options>.<custom>.<hex> */
		if(interfaceImpl.isConfigMgtChildOptionsCustomHex(type, index)){
			customObj.setHex(CLICommonFunc.createAhStringObj(interfaceImpl.getMgtChildOptionsCustomHexValue(type, index)));
		}
		
		return customObj;
	}
	
	private InterfaceIpHelper createInterfaceIpHelper(MgtType type){
		InterfaceIpHelper ipHelperObj = new InterfaceIpHelper();
		
		/** element: <ip-helper>.<address> */
		for(int i=0; i<interfaceImpl.getMgtChildIpHelperSize(type); i++){
			ipHelperObj.getAddress().add(
					CLICommonFunc.createAhNameActValue(interfaceImpl.getMgtChildIpHelperAddress(type, i), CLICommonFunc.getYesDefault())
			);
		}
		
		return ipHelperObj;
	}
	
	private MgtDnsServer createMgtDnsServer(MgtType type) throws Exception{
		MgtDnsServer dnsServerObj = new MgtDnsServer();
		
		/** Level_1	##################################################################### */
		List<Object> dnsList_1 = new ArrayList<Object>();
		{
			/** element: <dns-server>.<enable> */
			DnsServerEnable dnsEnableObj = new DnsServerEnable();
			dnsList_1.add(dnsEnableObj);
			dnsServerObj.setEnable(dnsEnableObj);
			
			/** element: <dns-server>.<mode> */
			Object[][] modeParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, interfaceImpl.getMgtDnsServerMode(type)},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			dnsServerObj.setMode(
					(MgtDnsServerMode)CLICommonFunc.createObjectWithName(MgtDnsServerMode.class, modeParm));
			
			/** element: <dns-server>.<int-domain-name> */
			for(int index=0; index<interfaceImpl.getIntDomainNameSize(type); index++){
				dnsServerObj.getIntDomainName().add(this.createMgtDnsServerIntDomainName(
						interfaceImpl.getIntDomainName(type, index), interfaceImpl.getIntDnsServer(type, index)));
			}
			
			/** element: <dns-server>.<int-resolve> */
			if(interfaceImpl.isConfigDnsIntResolve(type)){
				dnsServerObj.setIntResolve(this.createMgtDnsResolver(interfaceImpl.getIntResolveDns1(type), 
						interfaceImpl.getIntResolveDns2(type), 
						interfaceImpl.getIntResolveDns3(type)));
			}
			
			/** element: <dns-server>.<ext-resolve> */
			if(interfaceImpl.isConfigDnsExtResolve(type)){
				dnsServerObj.setExtResolve(this.createMgtDnsResolver(interfaceImpl.getExtResolveDns1(type), 
						interfaceImpl.getExtResolveDns2(type), 
						interfaceImpl.getExtResolveDns3(type)));
			}
		}
		
		/** Level_2	##################################################################### */
		{
			for(Object childObj : dnsList_1){
				/** element: <dns-server>.<enable> */
				if(childObj instanceof DnsServerEnable){
					DnsServerEnable dnsEnable = (DnsServerEnable)childObj;
					
					/** attribute: operation */
					dnsEnable.setOperation(CLICommonFunc.getAhEnumAct(
							interfaceImpl.isEnableMgtDnsServer(type)));
					
					/** element: <dns-server>.<enable> */
					dnsEnable.setCr("");
				}	
			}
			dnsList_1.clear();
		}
		
		dnsList_1 = null;
		
		return dnsServerObj;
	}
	
	private Mgtxy.Manage createMgtxyManage(MgtType type){
		Mgtxy.Manage manageObj = new Mgtxy.Manage();
		
		/** element: <manage>.<ping> */
		manageObj.setPing(CLICommonFunc.getAhOnlyAct(interfaceImpl.isMgtChildPingEnable(type)));
		
		return manageObj;
	}
	
	private InterfaceDuplex createInterfaceDuplex(EthDuplex duplex){
		InterfaceDuplex duplexObj = new InterfaceDuplex();
		
		/** attribute: operation */
		duplexObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** attribute: value */
		duplexObj.setValue(duplex);
		
		return duplexObj;
	}
	
	private InterfaceSpanningTree createInterfaceSpanningTree(DeviceInfType type, int index){
		InterfaceSpanningTree spanningObj = new InterfaceSpanningTree();
		
		/** element: <spanning-tree>.<enable> */
		spanningObj.setEnable(CLICommonFunc.getAhOnlyAct(interfaceImpl.isEnableSpanningTree(type, index)));
		
		/** element: <spanning-tree>.<path-cost> */
		if(interfaceImpl.isConfigSpanningPathCost(type, index)){
			spanningObj.setPathCost(CLICommonFunc.createAhIntActObj(
					interfaceImpl.getSpanningPathCost(type, index), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <spanning-tree>.<priority> */
		spanningObj.setPriority(CLICommonFunc.createAhIntActObj(
				interfaceImpl.getSpanningPriority(type, index), CLICommonFunc.getYesDefault()));
		
		if (interfaceImpl.isEnableSpanningTree(type, index)){
			
			/** element: <spanning-tree>.<edge-port> */
			spanningObj.setEdgePort(CLICommonFunc.getAhOnlyAct(
					interfaceImpl.isEnableSpanningEdgePort(type, index)));
			
			/** element: <spanning-tree>.<bpdu-protection> */
			if(interfaceImpl.isEnableSpanningEdgePort(type, index)){
				spanningObj.setBpduProtection(createSpanningTreeBpduProtection(type, index));
			}
		}
		
//		/** element: <spanning-tree>.<mst-instance> */
//		for(int i=0; i<interfaceImpl.getSpanningMstInstanceSize(type, index); i++){
//			spanningObj.getMstInstance().add(this.createMstInstance(type, index, i));
//		}
		
		return spanningObj;
	}
	
	private SpanningTreeBpduProtection createSpanningTreeBpduProtection(DeviceInfType type, int index){
		SpanningTreeBpduProtectionValue bpduValue = interfaceImpl.getSpanningBpdu(type, index);
		if(bpduValue == null){
			return null;
		}
		
		SpanningTreeBpduProtection bpduObj = new SpanningTreeBpduProtection();
		
		/** attribute: operation */
		bpduObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
		
		/** attribute: value */
		bpduObj.setValue(bpduValue);
		
		return bpduObj;
	}
	
	private InterfaceSpanningTreeMstInstance createMstInstance(DeviceInfType type, int index, int i){
		InterfaceSpanningTreeMstInstance instanceObj = new InterfaceSpanningTreeMstInstance();
		
		/** attribute: operation */
		instanceObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		instanceObj.setName(interfaceImpl.getSpanningMstInstanceName(type, index, i));
		
		/** element: <path-cost> */
		if(interfaceImpl.isConfigMstInstancePathCost(type, index, i)){
			instanceObj.setPathCost(CLICommonFunc.createAhIntActObj(
					interfaceImpl.getMstInstancePathCost(type, index, i), CLICommonFunc.getYesDefault()));
		}
		
		/** element: <priority> */
		instanceObj.setPriority(CLICommonFunc.createAhIntActObj(
				interfaceImpl.getMstInstancePriority(type, index, i), CLICommonFunc.getYesDefault()));
		
		return instanceObj;
	}
	
	private InterfaceGethLinkDiscovery createInterfaceGethLinkDiscovery(DeviceInfType type, int index){
		InterfaceGethLinkDiscovery linkObj = new InterfaceGethLinkDiscovery();
		
		{/** Level_1	##################################################################### */
			/** element: <link-discovery>.<lldp> */
			linkObj.setLldp(new InterfaceGethLinkDiscoveryLldp());
			
			/** element: <link-discovery>.<cdp> */
			linkObj.setCdp(new InterfaceGethLinkDiscoveryCdp());
		}
		
		{/** Level_2	##################################################################### */
			
			/** element: <link-discovery>.<lldp>.<receive> */
			linkObj.getLldp().setReceive(new GethLinkDiscoveryLldpMode());
			
			/** element: <link-discovery>.<lldp>.<transmit> */
			linkObj.getLldp().setTransmit(new GethLinkDiscoveryLldpMode());
			
			/** element: <link-discovery>.<cdp>.<receive> */
			linkObj.getCdp().setReceive(new GethLinkDiscoveryCdpMode());
		}
		
		{/** Level_3	##################################################################### */
			
			/** element: <link-discovery>.<lldp>.<receive>.<enable> */
			linkObj.getLldp().getReceive().setEnable(CLICommonFunc.getAhOnlyAct(
					interfaceImpl.isEnableLldpReceive(type, index)));
			
			/** element: <link-discovery>.<lldp>.<transmit>.<enable> */
			linkObj.getLldp().getTransmit().setEnable(CLICommonFunc.getAhOnlyAct(
					interfaceImpl.isEnableLldpTransmit(type, index)));
			
			/** element: <link-discovery>.<cdp>.<receive>.<enable> */
			linkObj.getCdp().getReceive().setEnable(CLICommonFunc.getAhOnlyAct(
					interfaceImpl.isEnableCdpReceive(type, index)));
		}
		
		return linkObj;
	}
	
	private InterfaceGethPse createInterfaceGethPse(DeviceInfType type, int index){
		InterfaceGethPse pseObj = new InterfaceGethPse();
		
		/** element: <pse>.<shutdown> */
		pseObj.setShutdown(CLICommonFunc.getAhOnlyAct(interfaceImpl.isShutdownInfPse(type, index)));
		
		/** element: <pse>.<profile> */
		if(interfaceImpl.isConfigPseProfile(type, index)){
			pseObj.setProfile(CLICommonFunc.createAhNameActObj(
					interfaceImpl.getInfPseProfileName(type, index), CLICommonFunc.getYesDefault()));
		}
		
		return pseObj;
	}
	
	private GethSecurityObject createGethSecurityObject(DeviceInfType type, int index){
		GethSecurityObject resObj = new GethSecurityObject();
		
		/** attribute: operation */
		resObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		resObj.setName(interfaceImpl.getInfSecurityObject(DeviceInfType.Gigabit, index));
		
		/** element: <cr> */
		resObj.setCr("");
		
		/** element: <mode> */
		InterfaceSwitchportModeValue mode = interfaceImpl.getInfPortVlanMode(type, index);
		if(mode != null){
			AssistantMode modeObj = new AssistantMode();
			modeObj.setName(mode.value());
			resObj.setMode(modeObj);
		}
		
		return resObj;
	}
	
	private InterfaceClientReport createInterfaceClientReport(boolean enable){
		InterfaceClientReport report = new InterfaceClientReport();
		
		/** element: <enable> */
		report.setEnable(CLICommonFunc.getAhOnlyAct(enable));
		
		return report;
	}
	
}