package com.ah.be.config.create.source.impl;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.source.UsbmodemInt;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.HiveAp;

public class UsbmodemImpl implements UsbmodemInt {
	
	private HiveAp hiveAp;
	
	public UsbmodemImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
	}
	
	public boolean isConfigUsb(){
		return hiveAp.getDeviceInfo().isOnlyRouterFunc();
	}

	public boolean isEnableUsbmodem() {
		return hiveAp.getUSBInterface() != null && 
				hiveAp.getUSBInterface().getAdminState() == AhInterface.ADMIN_STATE_UP;
	}
	
	public boolean isConfigUsbModem(UsbMode mode) {
		if(hiveAp.getUSBInterface().getPriority() <= AhInterface.PRIORITY_PRIMARY){
			return UsbMode.primarywan == mode;
		}else if(hiveAp.getUsbConnectionModel() == HiveAp.USB_CONNECTION_MODEL_ALWAYS){
			return UsbMode.alwaysconnected == mode;
		}else if(hiveAp.getUsbConnectionModel() == HiveAp.USB_CONNECTION_MODEL_NEEDED){
			return UsbMode.ondemand == mode;
		}else{
			return UsbMode.ondemand == mode;
		}
	}
	
	public int getUSBModemIdSize() {
		if(hiveAp.getUsbModemList() == null){
			return 0;
		}else{
			return hiveAp.getUsbModemList().size();
		}
	}
	
	public boolean isConfigUSBModem(int index){
		return NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), hiveAp.getUsbModemList().get(index).getOsVersion()) >= 0;
	}

	public String getModemId(int index) {
		return hiveAp.getUsbModemList().get(index).getModemName();
	}

	public String getApn(int index) {
		String apn = hiveAp.getUsbModemList().get(index).getApn();
		if(apn == null || "".equals(apn)){
			return " ";
		}else{
			return apn;
		}
	}

	public String getDialupNumber(int index) {
		String diaNum = hiveAp.getUsbModemList().get(index).getDialupNum();
		if(diaNum == null || "".equals(diaNum)){
			return " ";
		}else{
			return diaNum;
		}
	}

	public String getDialupUsername(int index) {
		String diaUser = hiveAp.getUsbModemList().get(index).getUserId();
		if(diaUser == null || "".equals(diaUser)){
			return " ";
		}else{
			return diaUser;
		}
	}

	public String getDialupPassword(int index) {
		String diaPas = hiveAp.getUsbModemList().get(index).getPassword();
		if(diaPas == null || "".equals(diaPas)){
			return " ";
		}else{
			return diaPas;
		}
	}
	
	public boolean isConfigApn(int index){
		String apn = this.getApn(index);
		return StringUtils.isNotBlank(apn); 
	}
	
	public boolean isConfigDialupNumber(int index){
		String DialupNumber = this.getDialupNumber(index);
		return StringUtils.isNotBlank(DialupNumber); 
	}
	
	public boolean isConfigDialupUsername(int index){
		String DialupUsername = this.getDialupUsername(index);
		return StringUtils.isNotBlank(DialupUsername); 
	}
	
	public boolean isConfigDialupPassword(int index){
		String DialupPassword = this.getDialupPassword(index);
		return StringUtils.isNotBlank(DialupPassword); 
	}
	
	public boolean isUsbmodemPowerEnable() {
		return hiveAp.isEnableCellularModem();
	}
	
	public String getUsbmodemNetworkMode() {
		if (hiveAp.getUsbModemList() != null && hiveAp.getUsbModemList().size() > 0) {
			return hiveAp.getUsbModemList().get(0).getCellularModeStr();
		}
		return "auto";
	}

}
