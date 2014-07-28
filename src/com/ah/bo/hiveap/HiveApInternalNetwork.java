package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.ah.bo.HmBoBase;

@Embeddable
public class HiveApInternalNetwork implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String internalNetwork;

	@Column(length = HmBoBase.IP_ADDRESS_LENGTH)
	private String netmask;
	
	public String getInternalNetwork() {
		return internalNetwork;
	}

	public void setInternalNetwork(String internalNetwork) {
		this.internalNetwork = internalNetwork;
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

}