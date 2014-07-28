package com.ah.ui.actions.monitor.enrolledclients.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@Embeddable
@XStreamAlias("content")
public class EnrolledClientNetworkInfo implements Serializable{

	private static final long serialVersionUID = 4298819175215669373L;

	@Transient
	@XStreamAlias("ICCID")
	private String iccid;
	
	@Transient
	@XStreamAlias("BluetoothMAC")
	private String blueToothMAC;
	
	@Transient
	@XStreamAlias("WiFiMAC")
	private String wifiMac;
	
	@Transient
	@XStreamAlias("CurrentCarrierNetwork")
	private String carrier;
		
	@Transient
	@XStreamAlias("SimCarrierNetwork")
	private String simCarrierNetwork;
	
	@Transient
	@XStreamAlias("SubscriberCarrierNetwork")
	private String subscriberCarrierNetwork;
	
	@Transient
	@XStreamAlias("CarrierSettingsVersion")
	private String carrierVersion;
		
	@Transient
	@XStreamAlias("PhoneNumber")
	private String phoneNumber;
	
	@Transient
	@XStreamAlias("VoiceRoamingEnabled")
	private boolean voiceRoamingEnabled;
	
	@Transient
	@XStreamAlias("DataRoamingEnabled")
	private boolean dataRoamingEnabled;
	
	@Transient
	@XStreamAlias("IsRoaming")
	private String isRoaming;
	
	@Transient
	@XStreamAlias("SubscriberMCC")
	private String subscriberMCC;
	
	@Transient
	@XStreamAlias("SubscriberMNC")
	private String subscriberMNC;

	@Transient
	@XStreamAlias("CurrentMCC")
	private String currentMCC;
	
	@Transient
	@XStreamAlias("CurrentMNC")
	private String currentMNC;
	
	@Transient
	@XStreamAlias("CellRSSI")
	private String cellRssi;
	
	@Transient
	@XStreamAlias("WifiRSSI")
	private String wifiRssi;
	
	@Transient
	@XStreamAlias("SSID")
	private String ssid;
	
	@Transient
	@XStreamAlias("BSSID")
	private String bssid;
	
	@Transient
	@XStreamAlias("LinkSpeed")
	private String linkSpeed;
	
	public String getCurrentMNC() {
		return currentMNC;
	}

	public void setCurrentMNC(String currentMNC) {
		this.currentMNC = currentMNC;
	}

	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}

	public String getWifiMac() {
		if (wifiMac==null || wifiMac.isEmpty()) {
			return "--";
		} 
		return wifiMac.toUpperCase();
	}

	public void setWifiMac(String wifiMac) {
		this.wifiMac = wifiMac;
	}

	public String getSimCarrierNetwork() {
		if (simCarrierNetwork==null || simCarrierNetwork.isEmpty()) {
			return "--";
		}
		return simCarrierNetwork;
	}

	public void setSimCarrierNetwork(String simCarrierNetwork) {
		this.simCarrierNetwork = simCarrierNetwork;
	}

	public String getPhoneNumber() {
		if (phoneNumber == null || phoneNumber.isEmpty()) {
			return "--";
		}
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isVoiceRoamingEnabled() {
		return voiceRoamingEnabled;
	}

	public void setVoiceRoamingEnabled(boolean voiceRoamingEnabled) {
		this.voiceRoamingEnabled = voiceRoamingEnabled;
	}

	public boolean isDataRoamingEnabled() {
		return dataRoamingEnabled;
	}

	public void setDataRoamingEnabled(boolean dataRoamingEnabled) {
		this.dataRoamingEnabled = dataRoamingEnabled;
	}

	public String getIsRoaming() {
		return isRoaming;
	}

	public void setIsRoaming(String isRoaming) {
		this.isRoaming = isRoaming;
	}

	public String getSubscriberMCC() {
		return subscriberMCC;
	}

	public void setSubscriberMCC(String subscriberMCC) {
		this.subscriberMCC = subscriberMCC;
	}

	public String getSubscriberCarrierNetwork() {
		if (subscriberCarrierNetwork==null || subscriberCarrierNetwork.isEmpty()) {
			return "--";
		}
		return subscriberCarrierNetwork;
	}

	public void setSubscriberCarrierNetwork(String subscriberCarrierNetwork) {
		this.subscriberCarrierNetwork = subscriberCarrierNetwork;
	}

	public String getSubscriberMNC() {
		return subscriberMNC;
	}

	public void setSubscriberMNC(String subscriberMNC) {
		this.subscriberMNC = subscriberMNC;
	}

	public String getCurrentMCC() {
		return currentMCC;
	}

	public void setCurrentMCC(String currentMCC) {
		this.currentMCC = currentMCC;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getCarrierVersion() {
		return carrierVersion;
	}

	public void setCarrierVersion(String carrierVersion) {
		this.carrierVersion = carrierVersion;
	}

	public String getBlueToothMAC() {
		if (blueToothMAC==null || blueToothMAC.isEmpty()) {
			return "--";
		}
		return blueToothMAC.toUpperCase();
	}

	public void setBlueToothMAC(String blueToothMAC) {
		this.blueToothMAC = blueToothMAC;
	}

	public String getCellRssi() {
		if (cellRssi==null || cellRssi.isEmpty()) {
			return "--";
		}
		return cellRssi + " dBm";
	}

	public void setCellRssi(String cellRssi) {
		this.cellRssi = cellRssi;
	}

	public String getWifiRssi() {
		if (wifiRssi==null || wifiRssi.isEmpty()) {
			return "--";
		}
		return wifiRssi + " dBm";
	}

	public void setWifiRssi(String wifiRssi) {
		this.wifiRssi = wifiRssi;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public String getLinkSpeed() {
		if (linkSpeed==null || linkSpeed.isEmpty()) {
			return "--";
		}
		return linkSpeed + " Mbps";
	}

	public void setLinkSpeed(String linkSpeed) {
		this.linkSpeed = linkSpeed;
	}

}
