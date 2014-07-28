package com.ah.ui.actions.monitor.enrolledclients.entity;


import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
@Embeddable
@XStreamAlias("ScanInfo")
public class EnrolledClientScanResultItem
{	
	@Transient
	@XStreamAlias("SSID")
	public String ssid;
	@Transient
	@XStreamAlias("BSSID")
	public String bssid;
	@Transient
	@XStreamAlias("Security")
	public String security;
	@Transient
	@XStreamAlias("Frequency")
	public String frequency;
	@Transient
	@XStreamAlias("Band")
	public String band;
	@Transient
	@XStreamAlias("Channel")
	public String channel;
	@Transient
	@XStreamAlias("Strength")
	public String strength;
	@Transient
	@XStreamAlias("RSSI")
	public String rssi;
	
	public EnrolledClientScanResultItem()
	{
		super();
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

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}
	
	public String getSecurityString() {
		if (security.equals("wep")) {
			return "WEP";
		} else if (security.equals("802_1x")) {
			return "WPA/WPA2 802.1X";
		} else if (security.equals("psk")) {
			return "WPA/WPA2 PSK";
		} else if (security.equals("none")) {
			return "Open";
		} else {
			return "";
		}
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getBand() {
		return band;
	}
	
	public String getBandString() {
		if (band.equals("1")) {
			return "2.4 GHz";
		} else if (band.equals("2")) {
			return "5 GHz";
		} else {
			return "";
		}
	}

	public void setBand(String band) {
		this.band = band;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getStrength() {
		return strength;
	}
	
	public String getStrengthString() {
		return strength==null? "": strength + " %";
	}

	public void setStrength(String strength) {
		this.strength = strength;
	}

	public String getRssi() {
		return rssi;
	}
	
	public String getRssiString() {
		return rssi==null? "": rssi + " dBm";
	}

	public void setRssi(String rssi) {
		this.rssi = rssi;
	}


}
