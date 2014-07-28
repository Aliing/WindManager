package com.ah.util.bo.device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.bo.HmBo;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.lan.LanProfile;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.SubNetworkResource;
import com.ah.bo.performance.AhPortAvailability;
import com.ah.bo.performance.AhSwitchPortInfo;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.UserProfile;
import com.ah.ui.actions.hiveap.HiveApMonitor;
import com.ah.ui.actions.hiveap.HiveApMonitor.MgtInterface4BrReport;
import com.ah.util.MgrUtil;
import com.ah.util.coder.AhDecoder;

/**
 * 
 * @date Dec 26, 2011
 * @author wx
 *
 * used for: deal with list of interfaces of certain device
 */
public class DeviceInterfaceBundle {
	
	private Map<String, DeviceInterfaceAdapter> deviceInterfaceAdapters = 
						new HashMap<>();
	private HiveAp hiveAp;
	
	private String getDeviceInterfaceName(DeviceInterface deviceInterface) {
		if (deviceInterface == null) {
			return "";
		}
		if (StringUtils.isBlank(deviceInterface.getInterfaceName())) {
			if (deviceInterface.getDeviceIfType() > 0) {
				String ifName = DeviceInterfaceUtil.getDeviceIfNameWithCertainIfType(deviceInterface.getDeviceIfType());
				deviceInterface.setInterfaceName(ifName);
				return ifName;
			} else {
				return "";
			}
		}
		return deviceInterface.getInterfaceName();
	}

	private void initConstructorArguments(List<DeviceInterface> deviceInterfaces, HiveAp hiveAp) {
		this.hiveAp = hiveAp;
		if(hiveAp.isSwitchProduct()){
			deviceInterfaceAdapters = DeviceInterfaceUtil.getDeviceInterfacesSupported4Switch(hiveAp.getHiveApModel());
			if (deviceInterfaceAdapters != null && !deviceInterfaceAdapters.isEmpty()
					&& deviceInterfaces != null && !deviceInterfaces.isEmpty()) {
				for (DeviceInterface deviceInterface : deviceInterfaces) {
					if(deviceInterface !=null && deviceInterface.getDeviceIfType() > 0){
						String ifName = MgrUtil.getEnumString("enum.switch.interface."+deviceInterface.getDeviceIfType());
						deviceInterface.setInterfaceName(ifName);
						deviceInterface.setHiveApModel(hiveAp.getHiveApModel());
						if (deviceInterfaceAdapters.get(StringUtils.lowerCase(ifName)) != null) {
							deviceInterfaceAdapters.get(StringUtils.lowerCase(ifName))
																.setDeviceInterface(deviceInterface);
						}
					}
				}
			}
		} else {
			deviceInterfaceAdapters = DeviceInterfaceUtil.getDeviceInterfacesSupported(hiveAp.getHiveApModel());
			if (deviceInterfaceAdapters != null && !deviceInterfaceAdapters.isEmpty()
					&& deviceInterfaces != null && !deviceInterfaces.isEmpty()) {
				for (DeviceInterface deviceInterface : deviceInterfaces) {
					deviceInterface.setHiveApModel(hiveAp.getHiveApModel());
					String ifName = getDeviceInterfaceName(deviceInterface);
					if (deviceInterfaceAdapters.get(StringUtils.lowerCase(ifName)) != null) {
						deviceInterfaceAdapters.get(StringUtils.lowerCase(ifName))
															.setDeviceInterface(deviceInterface);
					}
				}
			}
		}
	}
	
	/**
	 * convert device interface to be adapter of device interface
	 */
	public DeviceInterfaceBundle(List<DeviceInterface> deviceInterfaces, HiveAp hiveAp) {
		initConstructorArguments(deviceInterfaces, hiveAp);
	}
	
	public DeviceInterfaceBundle(Map<Long, DeviceInterface> mapDeviceInterfaces, HiveAp hiveAp) {
		List<DeviceInterface> deviceInterfaces = null;
		if (mapDeviceInterfaces != null && !mapDeviceInterfaces.isEmpty()) {
			deviceInterfaces = new ArrayList<>(mapDeviceInterfaces.values());
		}
		initConstructorArguments(deviceInterfaces, hiveAp);
	}
	
	public List<DeviceInterfaceAdapter> getSortedDeviceInterfaces() {
		if (deviceInterfaceAdapters == null || deviceInterfaceAdapters.isEmpty()) {
			return null;
		}
		List<DeviceInterfaceAdapter> adapters = new ArrayList<>(deviceInterfaceAdapters.values());
		
		if(hiveAp.isSwitchProduct()){
			Collections.sort(adapters, new Comparator<DeviceInterfaceAdapter>() {
				@Override
				public int compare(DeviceInterfaceAdapter adapter1, DeviceInterfaceAdapter adapter2) {
					return adapter1.getIfterNum() -adapter2.getIfterNum();
				}
			});
		} else {
			Collections.sort(adapters, new Comparator<DeviceInterfaceAdapter>() {
				@Override
				public int compare(DeviceInterfaceAdapter adapter1, DeviceInterfaceAdapter adapter2) {
					return adapter1.getIfterLowerName().compareTo(adapter2.getIfterLowerName());
				}
			});
		}
		
		return adapters;
	}
	
	public void initializeAccessModeString() {
		if (deviceInterfaceAdapters == null || deviceInterfaceAdapters.isEmpty()) {
			return;
		}
		for (Entry<String, DeviceInterfaceAdapter> deviceInterfaceAdapter : deviceInterfaceAdapters.entrySet()) {
			initializeAccessModeString(deviceInterfaceAdapter.getValue());
		}
	}
	
	private String getWanUpStatus(HiveAp hiveAp,DeviceInterface dInterface) {
		String wanUpStatus = DeviceInterfaceUtil.DEVICE_INTERFACE_LINK_STATUS_FAILED;
		String searchSQL = "lower(mac)=:s1 AND interfType=:s2 ";
		Object values[] = new Object[2];
		values[0] = hiveAp.getMacAddress().toLowerCase();
		values[1] = AhPortAvailability.INTERFACE_TYPE_WAN;
//		values[2] = getPbrDisplayLabel(hiveAp,dInterface.getDeviceIfType());
		List<?> lstPortInfo = QueryUtil.executeQuery(AhPortAvailability.class, null,
				new FilterParams(searchSQL, values));
		for(Object oneRec : lstPortInfo) {
			AhPortAvailability ahPort = (AhPortAvailability)oneRec;
			if (HiveApMonitor.getWanPortLabel(hiveAp,dInterface.getDeviceIfType()).equalsIgnoreCase(ahPort.getInterfName())) {
				if(AhPortAvailability.WAN_ACTIVE == ahPort.getWanactive() ){
					wanUpStatus = DeviceInterfaceUtil.DEVICE_INTERFACE_LINK_STATUS_OK;
					break;
				}
			}
		}
		return wanUpStatus;
	}
	
	private void initializeUsedInPBR() {
		if (deviceInterfaceAdapters == null
				|| deviceInterfaceAdapters.isEmpty()) {
			return;
		}
		for (Entry<String, DeviceInterfaceAdapter> deviceInterfaceAdapter : deviceInterfaceAdapters
				.entrySet()) {
			DeviceInterface dInf = deviceInterfaceAdapter.getValue()
					.getDeviceInterface();
			if (hiveAp.getRole(dInf) == AhInterface.ROLE_WAN) {
				dInf.setWanType(true);
				if (DeviceInterfaceUtil.DEVICE_INTERFACE_LINK_STATUS_OK.equals(getWanUpStatus(hiveAp,dInf))){
					dInf.setUsedInPBR(true);
				} else {
					dInf.setUsedInPBR(false);
				}
			} else {
				dInf.setWanType(false);
			}
		}
	}
	
	public void prepareEthNetwork(Map<String,String> wanIfInfoMap) {
		if (!checkArguments()) {
			return;
		}
		
		List<SubNetworkResource> subResources = QueryUtil.executeQuery(SubNetworkResource.class, null, new FilterParams("lower(hiveapmac)",hiveAp.getMacAddress().toLowerCase()), null,new ConfigLazyQueryBo());
		if (subResources.size() < 1) {
			return;
		}
		
//		//set eth0 ip address for fix bug 19461
//		String ipAddress = hiveAp.getCapwapClientIp();
//		
//		if (getCertainDeviceInterfaceAdapter(AhInterface.DEVICE_IF_TYPE_ETH0) != null) {
//			MgtInterface4BrReport mReport = new HiveApMonitor().new MgtInterface4BrReport();
//			mReport.setExactIP(ipAddress);
//			List<MgtInterface4BrReport> mReports = getCertainDeviceInterfaceAdapter(AhInterface.DEVICE_IF_TYPE_ETH0).getMgtInterface4BrReports();
//			if(mReports == null){
//				mReports = new ArrayList<>();
//				mReports.add(mReport);
//			} else {
//				mReports.add(mReport);
//			}
//			getCertainDeviceInterfaceAdapter(AhInterface.DEVICE_IF_TYPE_ETH0).setMgtInterface4BrReports(mReports);
//		}
		
		//fix bug 25088 
		String searchSQL = "lower(mac)=:s1 AND interfType=:s2 ";
		Object values[] = new Object[2];
		values[0] = hiveAp.getMacAddress().toLowerCase();
		values[1] = AhPortAvailability.INTERFACE_TYPE_WAN;
		List<AhPortAvailability> lstPortInfo = QueryUtil.executeQuery(AhPortAvailability.class, null, new FilterParams(searchSQL, values), hiveAp.getOwner().getId());
		if(lstPortInfo != null){
			for(AhPortAvailability port :lstPortInfo){
				String interfName = StringUtils.lowerCase(port.getInterfName());
				String usbName= MgrUtil.getUserMessage("hiveAp.router.br.deviceInterface.devicePort.name.usb");
				if(usbName.equalsIgnoreCase(interfName)){
					interfName =  StringUtils.lowerCase(DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_USB);
				}
				
				if (getCertainDeviceInterfaceAdapter(interfName) != null) {
					MgtInterface4BrReport mReport = new HiveApMonitor().new MgtInterface4BrReport();
					if(wanIfInfoMap.containsKey(interfName)){
						String ip = wanIfInfoMap.get(interfName);
						if(ip== null ||ip.isEmpty()){
							mReport.setExactIP(AhDecoder.int2IP(port.getWanipaddress()));
						} else {
							if(ip.indexOf("/")>0){
								mReport.setExactIP(ip.substring(0,ip.indexOf("/")));
							}else {
								mReport.setExactIP(ip);
							}
						}
						
					} else {
						mReport.setExactIP(AhDecoder.int2IP(port.getWanipaddress()));
					}
					List<MgtInterface4BrReport> mReports = getCertainDeviceInterfaceAdapter(interfName).getMgtInterface4BrReports();
					if(mReports == null){
						mReports = new ArrayList<>();
						mReports.add(mReport);
					} else {
						mReports.add(mReport);
					}
					getCertainDeviceInterfaceAdapter(interfName).setMgtInterface4BrReports(mReports);
				}
			}
		}
		
		for(SubNetworkResource subResource : subResources){
			prepareEthNetwork(subResource);
		}
	}
	
	private String getMgtxName(Short mgtx) {
		if (mgtx == 0) {
			return "mgt0";
		} else if (mgtx > 0) {
			if(hiveAp.getDeviceInfo().isSptEthernetMore_24()){
				return "vlan"+mgtx.toString();
			} else {
				return "mgt0."+mgtx.toString();
			}
		}
		return "";
	}
	
	public void preparePortsLinkStatus() {
		if (!checkArguments()) {
			return;
		}
		//first, initialize all port link status to be failed
		initializeLinkstatusString();
		if(hiveAp.isSwitchProduct()){
			fetchSwitchPortStatusFromDB();
		} else {
			fetchPortStatusFromDB();
		}
		initializeUsedInPBR();
	}
	
	private void prepareEthNetwork(SubNetworkResource subResource){
		if(subResource == null){
			return;
		}
		Set<Short> ports = subResource.getPortMapping();
		if(ports.isEmpty()){
			return;
		}
		MgtInterface4BrReport mReport = new HiveApMonitor().new MgtInterface4BrReport();
		mReport.setMgtName(getMgtxName(subResource.getHiveApMgtx()));
		mReport.setSubnet(subResource.getLocalNetwork());
		mReport.setExactIP(subResource.getFirstIp());
		mReport.setNetworkName(subResource.getVpnNetwork().getNetworkName());
		for(short port : ports){
			if (getCertainDeviceInterfaceAdapter(port) != null) {
				List<MgtInterface4BrReport> mReports = getCertainDeviceInterfaceAdapter(port).getMgtInterface4BrReports();
				if(mReports == null){
					mReports = new ArrayList<MgtInterface4BrReport>();
					mReports.add(mReport);
				} else {
					mReports.add(mReport);
				}
				getCertainDeviceInterfaceAdapter(port).setMgtInterface4BrReports(mReports);
				
				PortGroupProfile portGroup = hiveAp.getPortGroup();
				if(portGroup != null){
					//set port group name
					getCertainDeviceInterfaceAdapter(port).setPortGroupName(portGroup.getName());
					//set Access profile name
					for(PortBasicProfile basic : portGroup.getBasicProfiles()){
						if(basic.getAccessProfile() != null){
							String name = basic.getAccessProfile().getName();
							if(basic.getETHs() != null){
								for(String eth : basic.getETHs()){
									short sEth = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(eth), portGroup.getFirstHiveApModel());
									if(sEth == port){
										getCertainDeviceInterfaceAdapter(port).setAccessProfileName(name);
										break;
									}
								}
							}
							if(basic.getSFPs() != null){
								for(String sfp : basic.getSFPs()){
									short sSfp = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfp), portGroup.getFirstHiveApModel());
									if(sSfp == port){
										getCertainDeviceInterfaceAdapter(port).setAccessProfileName(name);
										break;
									}
								}
							}
							if(basic.getUSBs() != null){
								for(String usb : basic.getUSBs()){
									short sUsb = DeviceInfType.USB.getFinalValue(Integer.valueOf(usb), portGroup.getFirstHiveApModel());
									if(sUsb == port){
										getCertainDeviceInterfaceAdapter(port).setAccessProfileName(name);
										break;
									}
								}
							}
						}
					}
				}
				
			}
		}
	}
	
	private DeviceInterfaceAdapter getCertainDeviceInterfaceAdapter(String ifName) {
		return deviceInterfaceAdapters.get(StringUtils.lowerCase(ifName));
	}
	
	private DeviceInterfaceAdapter getCertainDeviceInterfaceAdapter(short deviceIfType) {
		if(hiveAp.isSwitchProduct()){
			return deviceInterfaceAdapters.get(StringUtils.lowerCase(MgrUtil
					.getEnumString("enum.switch.interface."+deviceIfType)));
		} else {
			if (deviceIfType == AhInterface.DEVICE_IF_TYPE_ETH0) {
				return deviceInterfaceAdapters.get(StringUtils.lowerCase(DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_ETH0));
			} else if (deviceIfType == AhInterface.DEVICE_IF_TYPE_ETH1) {
				return deviceInterfaceAdapters.get(StringUtils.lowerCase(DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_ETH1));
			} else if (deviceIfType == AhInterface.DEVICE_IF_TYPE_ETH2) {
				return deviceInterfaceAdapters.get(StringUtils.lowerCase(DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_ETH2));
			} else if (deviceIfType == AhInterface.DEVICE_IF_TYPE_ETH3) {
				return deviceInterfaceAdapters.get(StringUtils.lowerCase(DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_ETH3));
			} else if (deviceIfType == AhInterface.DEVICE_IF_TYPE_ETH4) {
				return deviceInterfaceAdapters.get(StringUtils.lowerCase(DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_ETH4));
			} else if (deviceIfType == AhInterface.DEVICE_IF_TYPE_USB) {
				return deviceInterfaceAdapters.get(StringUtils.lowerCase(DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_USB));
			} else if (deviceIfType == AhInterface.DEVICE_IF_TYPE_WIFI0){
				return deviceInterfaceAdapters.get(StringUtils.lowerCase(DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_WIFI0));
			} else if(deviceIfType == AhInterface.DEVICE_IF_TYPE_WIFI1){
				return deviceInterfaceAdapters.get(StringUtils.lowerCase(DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_WIFI1));
			}
		}
		return null;
	}
	
	private String getLinkStatusString(byte status) {
		if (status == AhPortAvailability.INTERFACE_STATUS_UP) {
			return DeviceInterfaceUtil.DEVICE_INTERFACE_LINK_STATUS_OK;
		} else {
			return DeviceInterfaceUtil.DEVICE_INTERFACE_LINK_STATUS_FAILED;
		}
	}
	
	private String getPortAccessModeString(Byte mode) {
		if (mode == 2) {
			return DeviceInterfaceUtil.DEVICE_INTERFACE_ACCESSMODE_TRUNK;
		} else {
			return DeviceInterfaceUtil.DEVICE_INTERFACE_ACCESSMODE_ACCESS;
		}
	}
	
	private void initializeLinkstatusString() {
		if (deviceInterfaceAdapters == null || deviceInterfaceAdapters.isEmpty()) {
			return;
		}
		for (Entry<String, DeviceInterfaceAdapter> deviceInterfaceAdapter : deviceInterfaceAdapters.entrySet()) {
			initializeLinkstatusString(deviceInterfaceAdapter.getValue());
		}
	}
	
	private void initializeLinkstatusString(DeviceInterfaceAdapter adapter) {
		adapter.getDeviceInterface().setLinkStatusString(DeviceInterfaceUtil.DEVICE_INTERFACE_LINK_STATUS_FAILED);
	}
	
	private String getInterfNameMappedFromPortEventName(String portIfName) {
		portIfName = StringUtils.lowerCase(portIfName);
		if (DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_ETH0.equals(portIfName)) {
			return DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_ETH0;
		} else if (DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_ETH1.equals(portIfName)) {
			return DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_ETH1;
		} else if (DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_ETH2.equals(portIfName)) {
			return DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_ETH2;
		} else if (DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_ETH3.equals(portIfName)) {
			return DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_ETH3;
		} else if (DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_ETH4.equals(portIfName)) {
			return DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_ETH4;
		} else if (DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_ETH5.equals(portIfName)) {
			return DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_ETH5;
		} else if (DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_ETH6.equals(portIfName)) {
			return DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_ETH6;
		} else if (DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_ETH7.equals(portIfName)) {
			return DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_ETH7;
		} else if (DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_ETH8.equals(portIfName)) {
			return DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_ETH8;
		} else if (DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_USB.equals(portIfName)) {
			return DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_USB;
		} else if(DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_WIFI0.equals(portIfName)){
			return DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_WIFI0;
		} else if(DeviceInterfaceUtil.DEVICE_INTERFACE_EVENT_PORT_NAME_WIFI1.equals(portIfName)){
			return DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_WIFI1;
		}
		return null;
	}
	
	private void fetchPortStatusFromDB() {
		String searchSQL = "lower(mac)=:s1";
		Object values[] = new Object[1];
		values[0] = hiveAp.getMacAddress().toLowerCase();
		List<AhPortAvailability> lstPortInfo = QueryUtil.executeQuery(AhPortAvailability.class, null,
				new FilterParams(searchSQL, values));
		for(AhPortAvailability ahPort : lstPortInfo) {
			String portIfName = getInterfNameMappedFromPortEventName(ahPort.getInterfName());
			if (getCertainDeviceInterfaceAdapter(portIfName) != null) {
				getCertainDeviceInterfaceAdapter(portIfName)
						.setLinkStatusString(getLinkStatusString(ahPort.getInterfStatus()));
				getCertainDeviceInterfaceAdapter(portIfName)
						.setAccessModeString(getPortAccessModeString(ahPort.getInterfMode()));
			}
		}
	}
	
	private void fetchSwitchPortStatusFromDB(){
		String searchSQL = "lower(mac)=:s1";
		Object values[] = new Object[1];
		values[0] = hiveAp.getMacAddress().toLowerCase();
		List<?> lstPortInfo = QueryUtil.executeQuery(AhSwitchPortInfo.class, null,
				new FilterParams(searchSQL, values));
		for(Object oneRec : lstPortInfo) {
			AhSwitchPortInfo ahPort = (AhSwitchPortInfo)oneRec;
			String portIfName = ahPort.getPortName();
			if (getCertainDeviceInterfaceAdapter(portIfName) != null) {
				getCertainDeviceInterfaceAdapter(portIfName)
						.setLinkStatusString(getLinkStatusString(ahPort.getState()));
			}
		}
	}

	private void initializeAccessModeString(DeviceInterfaceAdapter adapter) {
		short ifType = adapter.getDeviceInterface().getDeviceIfType();
		if (ifType == AhInterface.DEVICE_IF_TYPE_ETH0) {
			adapter.getDeviceInterface().setAccessModeString(
					MgrUtil.getUserMessage("interface.ethx.device.interface.mode.wan.primary"));
		} else if (ifType == AhInterface.DEVICE_IF_TYPE_ETH1) {
			adapter.getDeviceInterface().setAccessModeString(
					MgrUtil.getUserMessage("interface.ethx.device.interface.mode.wan.backup"));
		} else if (ifType == AhInterface.DEVICE_IF_TYPE_ETH2) {
			adapter.getDeviceInterface().setAccessModeString(
					MgrUtil.getUserMessage("interface.ethx.device.interface.mode.access"));
		} else if (ifType == AhInterface.DEVICE_IF_TYPE_ETH3) {
			adapter.getDeviceInterface().setAccessModeString(
					MgrUtil.getUserMessage("interface.ethx.device.interface.mode.access"));
		} else if (ifType == AhInterface.DEVICE_IF_TYPE_ETH4) {
			adapter.getDeviceInterface().setAccessModeString(
					MgrUtil.getUserMessage("interface.ethx.device.interface.mode.access"));
		} else if (ifType == AhInterface.DEVICE_IF_TYPE_USB) {
			adapter.getDeviceInterface().setAccessModeString(
					MgrUtil.getUserMessage("interface.ethx.device.interface.mode.access"));
		} else {
			adapter.getDeviceInterface().setAccessModeString(
					MgrUtil.getUserMessage("interface.ethx.device.interface.mode.access"));
		}
	}
	
	private boolean checkArguments() {
		return hiveAp != null;
	}
	
	public Map<String, DeviceInterfaceAdapter> getDeviceInterfaceAdapters() {
		return deviceInterfaceAdapters;
	}

	public void setDeviceInterfaceAdapters(
			Map<String, DeviceInterfaceAdapter> deviceInterfaceAdapters) {
		this.deviceInterfaceAdapters = deviceInterfaceAdapters;
	}
	
	/**
	 * used to load lazy objects (network about) for LAN profile
	 */
	public class LazyNetworkObjLoader implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if(bo instanceof LanProfile) {
				LanProfile lanProfile = (LanProfile)bo;
				if(null != lanProfile.getUserProfileDefault()) {
					lanProfile.getUserProfileDefault().getId();
				}
				if(null != lanProfile.getUserProfileSelfReg()) {
					lanProfile.getUserProfileSelfReg().getId();
				}
				if(null != lanProfile.getRadiusUserProfile()) {
					lanProfile.getRadiusUserProfile().size();
					for (UserProfile userProfileTmp : lanProfile.getRadiusUserProfile()) {
						if (userProfileTmp != null) {
							userProfileTmp.getId();
						}
					}
				}
			}
			return null;
		}
	}
	
}