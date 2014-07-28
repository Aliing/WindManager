package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MacAddressLearningEntry implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int vlanId;
	/*@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vlan_id")
	private Vlan vlanProfile;*/
	
	private short deviceInfoConstant;
	
	@Column(length = 17)
	private String macAddress;

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public short getDeviceInfoConstant() {
		return deviceInfoConstant;
	}

	public void setDeviceInfoConstant(short deviceInfoConstant) {
		this.deviceInfoConstant = deviceInfoConstant;
	}

	public int getVlanId() {
		return vlanId;
	}

	public void setVlanId(int vlanId) {
		this.vlanId = vlanId;
	}
}
