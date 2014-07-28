package com.ah.ui.actions.monitor.enrolledclients.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@Embeddable
@XStreamAlias("content")
public class NetworkInfoForUI implements Serializable{

	private static final long serialVersionUID = 4298819015215669373L;

	@Transient
	@XStreamAlias("ICCID")
	private String iccid;
	
	@Transient
	@XStreamAlias("WiFiMAC")
	private String wifiMac;
	
	@Transient
	@XStreamAlias("SimCarrierNetwork")
	private String simCarrierNetwork;
	
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
	@XStreamAlias("SubscriberCarrierNetwork")
	private String subscriberCarrierNetwork;
	
	@Transient
	@XStreamAlias("SubscriberMNC")
	private String subcriberMNC;
	
	/*@Transient
	@XStreamAlias("SIMMCC")
	private String simmcc;
	
	@Transient
	@XStreamAlias("SIMMNC")
	private String simmnc;*/
	
	@Transient
	@XStreamAlias("CurrentMCC")
	private String currentMCC;
	
	@Transient
	@XStreamAlias("CurrentMNC")
	private String currentMNC;
	
	@Transient
	@XStreamAlias("CurrentCarrierNetwork")
	private String carrier;
	
	@Transient
	@XStreamAlias("CarrierSettingsVersion")
	private String carrierVersion;
	
	@Transient
	@XStreamAlias("BluetoothMAC")
	private String blueToothMAC;
	
	@Transient
	private String cellularTech;
	
	@Transient
	private String ipAddress;
	
	@Transient
	private String modemFirmware;
	
	@Transient
	private String latitude;
	
	@Transient
	private String longitude;
	
	@Transient
	private String mapAddress;
	
	
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getMapAddress() {
		return mapAddress;
	}

	public void setMapAddress(String mapAddress) {
		this.mapAddress = mapAddress;
	}

	public String getCurrentMNC() {
		return currentMNC;
	}

	public void setCurrentMNC(String currentMNC) {
		this.currentMNC = currentMNC;
	}

	public String getModemFirmware() {
		return modemFirmware;
	}

	public void setModemFirmware(String modemFirmware) {
		this.modemFirmware = modemFirmware;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getCellularTech() {
		return cellularTech;
	}

	public void setCellularTech(String cellularTech) {
		this.cellularTech = cellularTech;
	}

	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}

	public String getWifiMac() {
		return wifiMac;
	}

	public void setWifiMac(String wifiMac) {
		this.wifiMac = wifiMac;
	}

	public String getSimCarrierNetwork() {
		return simCarrierNetwork;
	}

	public void setSimCarrierNetwork(String simCarrierNetwork) {
		this.simCarrierNetwork = simCarrierNetwork;
	}

	public String getPhoneNumber() {
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
		return subscriberCarrierNetwork;
	}

	public void setSubscriberCarrierNetwork(String subscriberCarrierNetwork) {
		this.subscriberCarrierNetwork = subscriberCarrierNetwork;
	}

	public String getSubcriberMNC() {
		return subcriberMNC;
	}

	public void setSubcriberMNC(String subcriberMNC) {
		this.subcriberMNC = subcriberMNC;
	}

/*	public String getSimmcc() {
		return simmcc;
	}

	public void setSimmcc(String simmcc) {
		this.simmcc = simmcc;
	}

	public String getSimmnc() {
		return simmnc;
	}

	public void setSimmnc(String simmnc) {
		this.simmnc = simmnc;
	}*/

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
		return blueToothMAC;
	}

	public void setBlueToothMAC(String blueToothMAC) {
		this.blueToothMAC = blueToothMAC;
	}

}
