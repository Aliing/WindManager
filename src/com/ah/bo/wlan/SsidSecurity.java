package com.ah.bo.wlan;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SsidSecurity implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean proactiveEnabled;
	
	private boolean localTkip=true;
	
	private boolean remoteTkip=true;
	
	private int rekeyPeriod = 0;

	private int rekeyPeriodGMK = 0;
	
	private int ptkTimeOut = 4000;
	
	private int ptkRetries = 3;
	
	private int gtkTimeOut = 4000;
	
	private int gtkRetries = 3;
	
	private int replayWindow=0;
	
	private int reauthInterval=0;
	
	private int pskUserLimit=0;
	
	private boolean blnMacBindingEnable=false;
	
	private int rekeyPeriodPTK=0;

	private boolean strict = true;

	private int keyType = 0;
	
	private boolean enable80211w = false;
	
	private boolean enableBip = false;
	
	private int wpa2mfpType = 1;

	@Column(length = 64)
	private String firstKeyValue;

	@Column(length = 64)
	private String secondKeyValue;

	@Column(length = 64)
	private String thirdKeyValue;

	@Column(length = 64)
	private String fourthValue;

	private int defaultKeyIndex = 1;

	public boolean getProactiveEnabled() {
		return proactiveEnabled;
	}

	public void setProactiveEnabled(boolean proactiveEnabled) {
		this.proactiveEnabled = proactiveEnabled;
	}

	public int getDefaultKeyIndex() {
		return defaultKeyIndex;
	}

	public void setDefaultKeyIndex(int defaultKeyIndex) {
		this.defaultKeyIndex = defaultKeyIndex;
	}

	public String getFirstKeyValue() {
		return firstKeyValue;
	}

	public void setFirstKeyValue(String firstKeyValue) {
		this.firstKeyValue = firstKeyValue;
	}

	public String getFourthValue() {
		return fourthValue;
	}

	public void setFourthValue(String fourthValue) {
		this.fourthValue = fourthValue;
	}

	public int getKeyType() {
		return keyType;
	}

	public void setKeyType(int keyType) {
		this.keyType = keyType;
	}

	public int getRekeyPeriod() {
		return rekeyPeriod;
	}

	public void setRekeyPeriod(int rekeyPeriod) {
		this.rekeyPeriod = rekeyPeriod;
	}

	public int getRekeyPeriodGMK() {
		return rekeyPeriodGMK;
	}

	public void setRekeyPeriodGMK(int rekeyPeriodGMK) {
		this.rekeyPeriodGMK = rekeyPeriodGMK;
	}

	public String getSecondKeyValue() {
		return secondKeyValue;
	}

	public void setSecondKeyValue(String secondKeyValue) {
		this.secondKeyValue = secondKeyValue;
	}

	public boolean getStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	public String getThirdKeyValue() {
		return thirdKeyValue;
	}

	public void setThirdKeyValue(String thirdKeyValue) {
		this.thirdKeyValue = thirdKeyValue;
	}

	public int getPtkTimeOut() {
		return ptkTimeOut;
	}

	public void setPtkTimeOut(int ptkTimeOut) {
		this.ptkTimeOut = ptkTimeOut;
	}

	public int getPtkRetries() {
		return ptkRetries;
	}

	public void setPtkRetries(int ptkRetries) {
		this.ptkRetries = ptkRetries;
	}

	public int getGtkTimeOut() {
		return gtkTimeOut;
	}

	public void setGtkTimeOut(int gtkTimeOut) {
		this.gtkTimeOut = gtkTimeOut;
	}

	public int getGtkRetries() {
		return gtkRetries;
	}

	public void setGtkRetries(int gtkRetries) {
		this.gtkRetries = gtkRetries;
	}

	public boolean getLocalTkip() {
		return localTkip;
	}

	public void setLocalTkip(boolean localTkip) {
		this.localTkip = localTkip;
	}

	public boolean getRemoteTkip() {
		return remoteTkip;
	}

	public void setRemoteTkip(boolean remoteTkip) {
		this.remoteTkip = remoteTkip;
	}

	public int getReplayWindow() {
		return replayWindow;
	}

	public void setReplayWindow(int replayWindow) {
		this.replayWindow = replayWindow;
	}

	public int getReauthInterval() {
		return reauthInterval;
	}

	public void setReauthInterval(int reauthInterval) {
		this.reauthInterval = reauthInterval;
	}

	public int getRekeyPeriodPTK() {
		return rekeyPeriodPTK;
	}

	public void setRekeyPeriodPTK(int rekeyPeriodPTK) {
		this.rekeyPeriodPTK = rekeyPeriodPTK;
	}

	public int getPskUserLimit() {
		return pskUserLimit;
	}

	public void setPskUserLimit(int pskUserLimit) {
		this.pskUserLimit = pskUserLimit;
	}

	public boolean isBlnMacBindingEnable() {
		return blnMacBindingEnable;
	}

	public void setBlnMacBindingEnable(boolean blnMacBindingEnable) {
		this.blnMacBindingEnable = blnMacBindingEnable;
	}

	public boolean isEnable80211w() {
		return enable80211w;
	}

	public void setEnable80211w(boolean enable80211w) {
		this.enable80211w = enable80211w;
	}

	public int getWpa2mfpType() {
		return wpa2mfpType;
	}

	public void setWpa2mfpType(int wpa2mfpType) {
		this.wpa2mfpType = wpa2mfpType;
	}

	public boolean isEnableBip() {
		return enableBip;
	}

	public void setEnableBip(boolean enableBip) {
		this.enableBip = enableBip;
	}
}
