package com.ah.bo.hiveap;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.bo.lan.LanProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

import edu.emory.mathcs.backport.java.util.Collections;

@Embeddable
public class DeviceInterface implements AhInterface {

	private static final long serialVersionUID = 1L;

	private short deviceIfType;

	private String interfaceName;
	
	private short role;
	
	public short getRole() {
		return role;
	}

	public void setRole(short role) {
		this.role = role;
	}

	@Transient
	private String interfaceNameCli;
	
	private short adminState;

	private boolean ifActive = true;

	private String ipAddress;

	private String netMask;

	private String gateway;

	private boolean enableDhcp = true;

	private short duplex = ETH_DUPLEX_AUTO;

	private short speed = ETH_SPEED_AUTO;

	private short pseState = AhInterface.ETH_PSE_8023af;

	private boolean pseEnabled = true;

	private String psePriority = AhInterface.ETH_PSE_PRIORITY_ETH2;

	private boolean enableMaxDownload = false;
	private boolean enableMaxUpload = false;

	private short maxDownload = 100;

	private short maxUpload = 100;

//	private short role;

	private boolean enableNat = true;
	private boolean disablePortForwarding;
	private static final int DEVICE_INTERFACE_PRIORITY_INIT = 1000;
	private static final int DEVICE_INTERFACE_PRIORITY_ETH0 = 1;
	private static final int DEVICE_INTERFACE_PRIORITY_ETH_FROM = 10;
	private static final int DEVICE_INTERFACE_PRIORITY_WIFI_FROM = 500;
	private static final int DEVICE_INTERFACE_PRIORITY_USB = 600;
	private static final int DEVICE_INTERFACE_PRIORITY_SFP_FROM = 700;
	
	private int priority = DEVICE_INTERFACE_PRIORITY_INIT;

	/** Add for Chesapeake switch column start */
	
	private int wanOrder = 0;

//	private int nativeVlan = 1;
//
//	private String allowedVlan = "All";

	public int getWanOrder() {
		return wanOrder;
	}

	public void setWanOrder(int wanOrder) {
		switch (wanOrder){
		case 1:
			switch (this.deviceIfType){
			case AhInterface.DEVICE_IF_TYPE_ETH0:
				setPriority(2);
				break;
			case AhInterface.DEVICE_IF_TYPE_USB:
				setPriority(1);
				break;
			default:
				setPriority(4);
			}
			break;
		case 2:
			setPriority(6);
			break;
		case 3:
			setPriority(8);
			break;
		case 0:
			if (deviceIfType == DEVICE_IF_TYPE_ETH0) {
				priority = DEVICE_INTERFACE_PRIORITY_ETH0;
			} else if (deviceIfType == DEVICE_IF_TYPE_USB) {
				priority = DEVICE_INTERFACE_PRIORITY_USB;
			} else if (deviceIfType >= DEVICE_IF_TYPE_ETH1 && deviceIfType <= DEVICE_IF_TYPE_ETH48)
				priority = (deviceIfType - DEVICE_IF_TYPE_ETH1) * 10 + DEVICE_INTERFACE_PRIORITY_ETH_FROM;
			else if (deviceIfType >= DEVICE_IF_TYPE_WIFI0 && deviceIfType <= DEVICE_IF_TYPE_WIFI1) {
				priority = (deviceIfType - DEVICE_IF_TYPE_WIFI0) * 10 + DEVICE_INTERFACE_PRIORITY_WIFI_FROM;
			} else if(DeviceInfType.isSfpPort(deviceIfType, hiveApModel)) {
				int index = DeviceInfType.getInstance(deviceIfType, hiveApModel).getIndex();
				int[] indexArrays = DeviceInfType.getSfpIndexArray(hiveApModel);
				int firstIndex = indexArrays[0];
				priority = (index - firstIndex + 1) * 10 + DEVICE_INTERFACE_PRIORITY_SFP_FROM;
//				for(int i=0; i<indexArrays.length; i++){
//					if(index == indexArrays[i]){
//						priority = (i + 1) * 10 + DEVICE_INTERFACE_PRIORITY_SFP_FROM;
//						break;
//					}
//				}
			}
			break;
		}
		this.wanOrder = wanOrder;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getInterfaceNameCli() {
		return interfaceNameCli;
	}

	public static int getDeviceInterfacePriorityInit() {
		return DEVICE_INTERFACE_PRIORITY_INIT;
	}

	public static int getDeviceInterfacePriorityEth0() {
		return DEVICE_INTERFACE_PRIORITY_ETH0;
	}

	public static int getDeviceInterfacePriorityEthFrom() {
		return DEVICE_INTERFACE_PRIORITY_ETH_FROM;
	}

	public static int getDeviceInterfacePriorityWifiFrom() {
		return DEVICE_INTERFACE_PRIORITY_WIFI_FROM;
	}

	public static int getDeviceInterfacePriorityUsb() {
		return DEVICE_INTERFACE_PRIORITY_USB;
	}

	public static int getDeviceInterfacePrioritySfpFrom() {
		return DEVICE_INTERFACE_PRIORITY_SFP_FROM;
	}

	public static short getDefaultParentValue() {
		return DEFAULT_PARENT_VALUE;
	}

	private short flowControlStatus = FLOW_CONTROL_STATUS_DISABLE;

	private boolean lldpTransmit = true;

	private boolean lldpReceive = true;
	
	private boolean cdpReceive = true;
	
	/*Added for LLDP/CDP setting to identify whether the port can be override*/
	private boolean lldpEnable;
	
	private boolean cdpEnable;

	private boolean autoMdix = true;

	private int mtu = 1500;

	private int debounceTimer = 0;
	private String connectionType = "1";
//	private String staticIp;
//	private String defaultGateway;
	
	/*Added for enable/disable client report on switch */
	private boolean clientReporting = true;
	
	private boolean enableClientReporting;
	
	private String  portDescription;
	
	private boolean enableOverridePortDescription;
	
	@Transient
	private boolean usedInPBR;
	
	/*
	 * this parameter is only used in hiveapdetail page. don't use it in other place.
	 */
	@Transient
	private boolean wanType;
	
	public boolean getWanType() {
		return wanType;
	}

	public void setWanType(boolean isWANType) {
		this.wanType = isWANType;
	}

	public boolean getUsedInPBR() {
		return usedInPBR;
	}

	public void setUsedInPBR(boolean isUsedInPBR) {
		this.usedInPBR = isUsedInPBR;
	}
	
	public String getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
		setEnableDhcp("1".equals(connectionType));
	}

//	public String getStaticIp() {
//		return staticIp;
//	}
//
//	public void setStaticIp(String staticIp) {
//		this.staticIp = staticIp;
//	}
//
//	public String getDefaultGateway() {
//		return defaultGateway;
//	}
//
//	public void setDefaultGateway(String defaultGateway) {
//		this.defaultGateway = defaultGateway;
//	}
	
	@Transient
	private short hiveApModel;

	@Transient
	private List<Short> members;

	public static final short DEFAULT_PARENT_VALUE = -1;
	@Transient
	private short parent = DEFAULT_PARENT_VALUE;

	/** Add for Chesapeake switch column start */

	public boolean isEnableMaxDownload() {
		return enableMaxDownload;
	}

	public void setEnableMaxDownload(boolean enableMaxDownload) {
		this.enableMaxDownload = enableMaxDownload;
	}

	public boolean isEnableMaxUpload() {
		return enableMaxUpload;
	}

	public void setEnableMaxUpload(boolean enableMaxUpload) {
		this.enableMaxUpload = enableMaxUpload;
	}

	public short getMaxDownload() {
		return maxDownload;
	}

	public void setMaxDownload(short maxDownload) {
		this.maxDownload = maxDownload;
	}

	public short getMaxUpload() {
		return maxUpload;
	}

	public void setMaxUpload(short maxUpload) {
		this.maxUpload = maxUpload;
	}

	public short getOperationMode() {
		return -1;
	}

	public void setOperationMode(short operationMode) {

	}

	public short getDeviceIfType() {
		return this.deviceIfType;
	}

	public void setDeviceIfType(short deviceIfType) {
		this.deviceIfType = deviceIfType;
		initInterfacePriority();
	}

	public String getInterfaceName() {
		return this.interfaceName;
	}

	public void setInterfaceNameCli(String interfaceNameCli) {
		this.interfaceNameCli = interfaceNameCli;
	}

	public String getInterfaceNameEnum() {
		return MgrUtil.getEnumString("enum.switch.interface." + deviceIfType);
	}

	public String getMemberStr(){
		if(members == null || members.isEmpty()){
			return "";
		}
		String menberStr = "";
		Collections.sort(members);
		for(Short mem : members){
			String temp = MgrUtil.getEnumString("enum.switch.interface." + mem);
			if("".equals(menberStr)){
				menberStr = temp;
			}else{
				menberStr += ", " + temp;
			}
		}
		return menberStr;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public short getAdminState() {
		return this.adminState;
	}

	public void setAdminState(short adminState) {
		this.adminState = adminState;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getGateway() {
		return this.gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public boolean isEnableDhcp() {
		return this.enableDhcp;
	}

	public void setEnableDhcp(boolean enableDhcp) {
		this.enableDhcp = enableDhcp;
	}

	public short getDuplex() {
		return this.duplex;
	}

	public void setDuplex(short duplex) {
		this.duplex = duplex;
	}

	public short getSpeed() {
		return this.speed;
	}

	public void setSpeed(short speed) {
		this.speed = speed;
	}

	public String getNetMask() {
		return netMask;
	}

	public void setNetMask(String netMask) {
		this.netMask = netMask;
	}

	@Transient
	public String getIpAndNetmask() {
		if (ipAddress != null && !"".equals(ipAddress) && netMask != null
				&& !"".equals(netMask)) {
			return this.ipAddress + "/"
					+ CLICommonFunc.turnNetMaskToNum(netMask);
		} else {
			return "";
		}
	}

	@Transient
	public void setIpAndNetmask(String ipAndNetmask) {
		if (ipAndNetmask != null && ipAndNetmask.contains("/")) {
			String ipAndNetmaskTmp = ipAndNetmask.trim();
			this.setIpAddress(ipAndNetmaskTmp.substring(0,
					ipAndNetmaskTmp.indexOf("/")));
			this.setNetMask(NmsUtil.getNetmask(Integer.valueOf(ipAndNetmaskTmp
					.substring(ipAndNetmaskTmp.indexOf("/") + 1))));
		}
	}

	@Transient
	public String getAdminStateString() {
		return MgrUtil.getEnumString("enum.interface.adminState."
				+ getAdminState());
	}
	

	@Transient
	public boolean getDisabledPseState() {
		return pseEnabled == false;
	}

	private boolean isContainSFPInf(){
		if(DeviceInfType.getInstance(this.deviceIfType, hiveApModel).getDeviceInfType() == DeviceInfType.Gigabit){
			return false;
		}else if(DeviceInfType.getInstance(this.deviceIfType, hiveApModel).getDeviceInfType() == DeviceInfType.SFP){
			return true;
		}else if(members == null){
			return false;
		}else{
			for(Short memInf : members){
				if(DeviceInfType.getInstance(memInf, hiveApModel).getDeviceInfType() == DeviceInfType.SFP){
					return true;
				}
			}
			return false;
		}
	}
	
	@Transient
	private boolean isSR2124(){
		//SR2124 SFP port only support Speed 10G, Duplex Full.
		return this.hiveApModel == HiveAp.HIVEAP_MODEL_SR2124P;
	}
	
	@Transient
	private boolean isSR48(){
		DeviceInfo dInfo = NmsUtil.getDeviceInfo(hiveApModel);
		if(dInfo != null){
			return dInfo.isSptEthernetMore_48();
		}
		return false;
	}

	@Transient
	public EnumItem[] getEnumSpeedType() {
		boolean isSFPPort = DeviceInfType.getInstance(this.deviceIfType, hiveApModel).getDeviceInfType() == DeviceInfType.SFP;
		if((isSFPPort || isContainSFPInf()) && 
				NmsUtil.getDeviceInfo(hiveApModel).isSupportAttribute(DeviceInfo.SPT_SFPSPEED_ONLY_AUTO) ){
			return AhInterface.ETH_SPEED_ONLY_AUTO_TYPE;
		}else if(isSFPPort || isContainSFPInf()){
			return AhInterface.ETH_SPEED_SFP_TYPE;
		}else{
			return AhInterface.ETH_SPEED_TYPE;
		}
	}
	
	@Transient
	public EnumItem[] getEnumDuplexType() {
		boolean isSFPPort = DeviceInfType.getInstance(this.deviceIfType, hiveApModel).getDeviceInfType() == DeviceInfType.SFP;
		if(isSFPPort || isContainSFPInf()){
			return AhInterface.ETH_DUPLEX_SFP_TYPE;
		}else{
			return AhInterface.ETH_DUPLEX_TYPE;
		}
	}

	@Transient
	public EnumItem[] getEnumFlowCtlType(){
		if(DeviceInfType.getInstance(this.deviceIfType, hiveApModel).getDeviceInfType() == DeviceInfType.SFP || isContainSFPInf()){
			return AhInterface.FLOW_CONTROL_TYPE_SFP;
		}else{
			return AhInterface.FLOW_CONTROL_TYPE;
		}
	}

	// @Transient
	// public boolean getDisabledPowerNumber() {
	// return powerThreshold==AhInterface.PSE_POWER_THRESHOLD_CLASSBASE;
	// }
	//
	// public String getDisplayPowerNumber(){
	// if (powerThreshold==AhInterface.PSE_POWER_THRESHOLD_USERDEFINE) {
	// return "";
	// }
	// return "none";
	// }
	//
	// public String getDisplayPowerNumberNote(){
	// if (powerThreshold==AhInterface.PSE_POWER_THRESHOLD_CLASSBASE) {
	// return "";
	// }
	// return "none";
	// }

	/* for branch router details_start */
	@Transient
	private String linkStatusString;
	@Transient
	private LanProfile lanProfile;
	@Transient
	private String accessModeString;
	@Transient
	private long pppoeID;

	public String getLinkStatusString() {
		return linkStatusString;
	}

	public void setLinkStatusString(String linkStatusString) {
		this.linkStatusString = linkStatusString;
	}

	public String getAccessModeString() {
		return accessModeString;
	}

	public void setAccessModeString(String accessModeString) {
		this.accessModeString = accessModeString;
	}

	public LanProfile getLanProfile() {
		return lanProfile;
	}

	public void setLanProfile(LanProfile lanProfile) {
		this.lanProfile = lanProfile;
	}

	/* for branch router details_end */

	public boolean isPortSFP(){
		return DeviceInfType.getInstance(this.deviceIfType, hiveApModel).getDeviceInfType() == DeviceInfType.SFP;
	}

	public boolean isPortChannel(){
		return DeviceInfType.getInstance(this.deviceIfType, hiveApModel).getDeviceInfType() == DeviceInfType.PortChannel;
	}

	public boolean isPortUSB(){
		return DeviceInfType.getInstance(this.deviceIfType, hiveApModel).getDeviceInfType() == DeviceInfType.USB;
	}

	public boolean isBindPortChannel(){
		return this.parent > DEFAULT_PARENT_VALUE;
	}

	// BO set get function

	public short getPseState() {
		return pseState;
	}

	public void setPseState(short pseState) {
		this.pseState = pseState;
	}

//	public short getRole() {
//		if(getPriority()<1000){
//			return ROLE_WAN;
//		}
//		return ROLE_LAN;
//	}
	
//	public void setRole(short role) {
//		this.role = role;
//	}

	public String getPsePriority() {
		return psePriority;
	}

	public void setPsePriority(String psePriority) {
		this.psePriority = psePriority;
	}

	public boolean isPseEnabled() {
		return pseEnabled;
	}

	public void setPseEnabled(boolean pseEnabled) {
		this.pseEnabled = pseEnabled;
	}

	public boolean isIfActive() {
		return ifActive;
	}

	public void setIfActive(boolean ifActive) {
		this.ifActive = ifActive;
	}

	public boolean isEnableNat() {
		return enableNat;
	}

	public void setEnableNat(boolean enableNat) {
		this.enableNat = enableNat;
	}

	public boolean isDisablePortForwarding() {
		return disablePortForwarding;
	}

	public void setDisablePortForwarding(boolean disablePortForwarding) {
		this.disablePortForwarding = disablePortForwarding;
	}
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	public short getFlowControlStatus() {
		return flowControlStatus;
	}

	public void setFlowControlStatus(short flowControlStatus) {
		this.flowControlStatus = flowControlStatus;
	}

	public boolean isAutoMdix() {
		return autoMdix;
	}

	public void setAutoMdix(boolean autoMdix) {
		this.autoMdix = autoMdix;
	}

	public int getMtu() {
		return mtu;
	}

	public void setMtu(int mtu) {
		this.mtu = mtu;
	}

	public int getDebounceTimer() {
		return debounceTimer;
	}

	public void setDebounceTimer(int debounceTimer) {
		this.debounceTimer = debounceTimer;
	}

//	public int getNativeVlan() {
//		return nativeVlan;
//	}
//
//	public void setNativeVlan(int nativeVlan) {
//		this.nativeVlan = nativeVlan;
//	}
//
//	public String getAllowedVlan() {
//		return allowedVlan;
//	}
//
//	public void setAllowedVlan(String allowedVlan) {
//		this.allowedVlan = allowedVlan;
//	}

	public boolean isLldpTransmit() {
		return lldpTransmit;
	}

	public void setLldpTransmit(boolean lldpTransmit) {
		this.lldpTransmit = lldpTransmit;
	}

	public boolean isLldpReceive() {
		return lldpReceive;
	}

	public void setLldpReceive(boolean lldpReceive) {
		this.lldpReceive = lldpReceive;
	}

	public boolean isCdpReceive() {
		return cdpReceive;
	}

	public void setCdpReceive(boolean cdpReceive) {
		this.cdpReceive = cdpReceive;
	}
	
	public void initMembers(PortBasicProfile baseProfile){
		if(baseProfile == null){
			return;
		}
		String[] eths = baseProfile.getETHs();
		String[] sfps = baseProfile.getSFPs();
		if(eths != null && eths.length > 0){
			for(int i=0; i<eths.length; i++){
				getMembers().add(DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(eths[i]), hiveApModel));
			}
		}
		if(sfps != null && sfps.length > 0){
			for(int i=0; i<sfps.length; i++){
				getMembers().add(DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfps[i]), hiveApModel));
			}
		}
	}

	public List<Short> getMembers() {
		if(members == null){
			members = new ArrayList<Short>();
		}
		return members;
	}

	public void setMembers(List<Short> members) {
		this.members = members;
	}
	
	public short getHiveApModel() {
		return hiveApModel;
	}

	public void setHiveApModel(short hiveApModel) {
		this.hiveApModel = hiveApModel;
	}

	public short getParent() {
		return parent;
	}

	public void setParent(short parent) {
		this.parent = parent;
	}

	public boolean isLldpEnable() {
		return lldpEnable;
			}
			
	public void setLldpEnable(boolean lldpEnable) {
	this.lldpEnable = lldpEnable;
		}	
		
	/**
	 * get the display label in PBR configuration UI
	 * @param deviceIfType
	 * @return name used in PBR configuration UI
	 */
	
	public static String getPbrDisplayLabel(short deviceIfType){
		switch(deviceIfType){
		case AhInterface.DEVICE_IF_TYPE_USB:
			return MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.usb");
		case AhInterface.DEVICE_IF_TYPE_WIFI0:
			return MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.wifi0");
		case AhInterface.DEVICE_IF_TYPE_WIFI1:
			return MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.wifi1");
		case AhInterface.DEVICE_IF_TYPE_ETH0:
			return MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth0");
		case AhInterface.DEVICE_IF_TYPE_ETH1:
			return MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth1");
		case AhInterface.DEVICE_IF_TYPE_ETH2:
			return MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth2");
		case AhInterface.DEVICE_IF_TYPE_ETH3:
			return MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth3");
		case AhInterface.DEVICE_IF_TYPE_ETH4:
			return MgrUtil.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth4");
		default:
			return "";
		}
	}
	
	public static String getPSwitchDisplayLabel(short deviceIfType){
		return MgrUtil.getEnumString("enum.switch.interface." + deviceIfType);
	}
	
	public boolean isCdpEnable() {
		return cdpEnable;
	}

	public void setCdpEnable(boolean cdpEnable) {
		this.cdpEnable = cdpEnable;
	}
	
	private void initInterfacePriority () {
		if (priority == DEVICE_INTERFACE_PRIORITY_INIT) {
			if (deviceIfType == DEVICE_IF_TYPE_ETH0) {
				priority = DEVICE_INTERFACE_PRIORITY_ETH0;
			} else if (deviceIfType == DEVICE_IF_TYPE_USB) {
				priority = DEVICE_INTERFACE_PRIORITY_USB;
			} else if (deviceIfType >= DEVICE_IF_TYPE_ETH1 && deviceIfType <= DEVICE_IF_TYPE_ETH48)
				priority = (deviceIfType - DEVICE_IF_TYPE_ETH1) * 10 + DEVICE_INTERFACE_PRIORITY_ETH_FROM;
			else if (deviceIfType >= DEVICE_IF_TYPE_WIFI0 && deviceIfType <= DEVICE_IF_TYPE_WIFI1) {
				priority = (deviceIfType - DEVICE_IF_TYPE_WIFI0) * 10 + DEVICE_INTERFACE_PRIORITY_WIFI_FROM;
			} else if(DeviceInfType.isSfpPort(deviceIfType, hiveApModel)) {
				int index = DeviceInfType.getInstance(deviceIfType, hiveApModel).getIndex();
				int[] indexArrays = DeviceInfType.getSfpIndexArray(hiveApModel);
				int firstIndex = indexArrays[0];
				priority = (index - firstIndex + 1) * 10 + DEVICE_INTERFACE_PRIORITY_SFP_FROM;
//				for(int i=0; i<indexArrays.length; i++){
//					if(index == indexArrays[i]){
//						priority = (i + 1) * 10 + DEVICE_INTERFACE_PRIORITY_SFP_FROM;
//						break;
//					}
//				}
			}
		}
	}
public String getStaticIpFlag(){
		
		if("2".equals(connectionType))
		{
			return "";
		}
		return "none";
	}
public String getPPPoEFlag(){
	
	if("3".equals(connectionType))
	{
		return "";
	}
	return "none";
}

public long getPppoeID() {
	return pppoeID;
}

public void setPppoeID(long pppoeID) {
	this.pppoeID = pppoeID;
}

public boolean isClientReporting() {
	return clientReporting;
}

public void setClientReporting(boolean clientReporting) {
	this.clientReporting = clientReporting;
}

public boolean isEnableClientReporting() {
	return enableClientReporting;
}

public void setEnableClientReporting(boolean enableClientReporting) {
	this.enableClientReporting = enableClientReporting;
}

public String getPortDescription() {
	return portDescription;
}

public void setPortDescription(String portDescription) {
	this.portDescription = portDescription;
}

public boolean isEnableOverridePortDescription() {
	return enableOverridePortDescription;
}

public void setEnableOverridePortDescription(
		boolean enableOverridePortDescription) {
	this.enableOverridePortDescription = enableOverridePortDescription;
}

@Transient
public String getWanOrderName() {
	return MgrUtil.getEnumString("enum.interface.priority."+this.wanOrder);
}

@Transient
public String wanPortName;

@Transient
public String wanStatusImg;

@Transient
public String wanStatusImgAlt;


public String getWanPortName() {
	return wanPortName;
}

public void setWanPortName(String wanPortName) {
	this.wanPortName = wanPortName;
}

public String getWanStatusImg() {
	return wanStatusImg;
}

public void setWanStatusImg(String wanStatusImg) {
	this.wanStatusImg = wanStatusImg;
}

public String getWanStatusImgAlt() {
	return wanStatusImgAlt;
}

public void setWanStatusImgAlt(String wanStatusImgAlt) {
	this.wanStatusImgAlt = wanStatusImgAlt;
}



}
