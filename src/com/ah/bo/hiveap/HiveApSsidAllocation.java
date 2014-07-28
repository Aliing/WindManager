package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

/*
 * Since the SSID objects are configured in WLAN policy, and them may changed
 * after WLAN policy binds to HiveAP, by default the SSID Objects are enabled.
 * so it is better that just store the ones are disabled.
 */
@Embeddable
public class HiveApSsidAllocation implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final short WIFI2G = 1;
	public static final short WIFI5G = 2;

	private Long ssid;
	
	private short interType;

	@Transient
	private String ssidName;

	@Transient
	private boolean checked;

	@Transient
	private String tooltip;

	public Long getSsid() {
		return ssid;
	}

	public void setSsid(Long ssid) {
		this.ssid = ssid;
	}

	public short getInterType() {
		return interType;
	}

	public void setInterType(short interType) {
		this.interType = interType;
	}

	public String getSsidName() {
		return ssidName;
	}

	public void setSsidName(String ssidName) {
		this.ssidName = ssidName;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof HiveApSsidAllocation)) {
			return false;
		}
		if (null == ssid || interType <= 0) {
			return false;
		}
		HiveApSsidAllocation otherObject = (HiveApSsidAllocation) other;
		return ssid.equals(otherObject.getSsid()) && interType == otherObject.getInterType();
	}

	@Override
	public int hashCode() {
		return null == ssid ? super.hashCode() : ssid.intValue();
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

}