package com.ah.util.bo.device;

import java.util.List;

import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.lan.LanProfile;
import com.ah.ui.actions.hiveap.HiveApMonitor.MgtInterface4BrReport;
import com.ah.util.MgrUtil;
import com.ah.util.bo.BoGenerationUtil;

/**
 * 
 * @date Dec 26, 2011
 * @author wx
 *
 * used for: an adapter of DeviceInterface, so it can supply more operations
 */
public class DeviceInterfaceAdapter {
	
	private DeviceInterface deviceInterface = null;
	
	/**
	 * you should pass a DeviceInterface to this adapter
	 */
	public DeviceInterfaceAdapter(DeviceInterface deviceInterface) {
		if (deviceInterface == null) {
			deviceInterface = BoGenerationUtil.genDefaultDeviceInterface(DeviceInterfaceUtil.DEVICE_INTERFACE_NAME_ETH0);
		}
		this.deviceInterface = deviceInterface;
	}
	
	public void setLanProfile(LanProfile lanProfile) {
		deviceInterface.setLanProfile(lanProfile);
		blnLanPort = true;
	}
	
	public void setAccessModeString(String accessModeString) {
		// access mode string should not be set for wan port(eth0 & usb)
		if (deviceInterface.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_ETH0
				|| deviceInterface.getDeviceIfType() == AhInterface.DEVICE_IF_TYPE_USB) {
			return;
		}
		deviceInterface.setAccessModeString(accessModeString);
	}
	
	public void setLinkStatusString(String linkStatusString) {
		deviceInterface.setLinkStatusString(linkStatusString);
	}
	
	private List<MgtInterface4BrReport> mgtInterface4BrReports;
	
	
	public List<MgtInterface4BrReport> getMgtInterface4BrReports() {
		return mgtInterface4BrReports;
	}

	public void setMgtInterface4BrReports(
			List<MgtInterface4BrReport> mgtInterface4BrReports) {
		this.mgtInterface4BrReports = mgtInterface4BrReports;
	}
	
	public String getInterfaceDisplayName(){
		if(deviceInterface.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ && deviceInterface.isPortUSB()){
			return MgrUtil.getUserMessage("hiveAp.autoProvisioning.device.if.port.cellularmodem");
		}
		return deviceInterface.getInterfaceName();
	}

	private String ifterLowerName;
	
	private short ifterNum;
	
	private String portGroupName;
	
	private String accessProfileName;
	
	/**
	 * whether the port can be used as LAN port
	 */
	private boolean blnLanPort;
	
	/**
	 * whether the port can be used as PSE port
	 */
	private boolean blnSupportPSE;
	
	/**
	 * whether the port is a usb port
	 */
	private boolean blnUsbPort;
	
	public DeviceInterface getDeviceInterface() {
		return deviceInterface;
	}

	public void setDeviceInterface(DeviceInterface deviceInterface) {
		this.deviceInterface = deviceInterface;
	}

	public boolean isBlnLanPort() {
		return blnLanPort;
	}

	public void setBlnLanPort(boolean blnLanPort) {
		this.blnLanPort = blnLanPort;
	}

	public boolean isBlnSupportPSE() {
		return blnSupportPSE;
	}

	public void setBlnSupportPSE(boolean blnSupportPSE) {
		this.blnSupportPSE = blnSupportPSE;
	}

	public boolean isBlnUsbPort() {
		return blnUsbPort;
	}

	public void setBlnUsbPort(boolean blnUsbPort) {
		this.blnUsbPort = blnUsbPort;
	}

	public String getIfterLowerName() {
		return ifterLowerName;
	}

	public void setIfterLowerName(String ifterLowerName) {
		this.ifterLowerName = ifterLowerName;
	}

	public short getIfterNum() {
		return ifterNum;
	}

	public void setIfterNum(short ifterNum) {
		this.ifterNum = ifterNum;
	}

	public String getPortGroupName() {
		return portGroupName;
	}

	public void setPortGroupName(String portGroupName) {
		this.portGroupName = portGroupName;
	}

	public String getAccessProfileName() {
		return accessProfileName;
	}

	public void setAccessProfileName(String accessProfileName) {
		this.accessProfileName = accessProfileName;
	}

}
