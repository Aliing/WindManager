package com.ah.bo.hiveap;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class IdpAp {
	@Column(length = 12, nullable = false)
	private String mitiMac;

	@Column(length = 32)
	private String ifName;

	public String getMitiMac() {
		return mitiMac;
	}

	public void setMitiMac(String mitiMac) {
		this.mitiMac = mitiMac;
	}

	public String getIfName() {
		return ifName;
	}

	public void setIfName(String ifName) {
		this.ifName = ifName;
	}
}
