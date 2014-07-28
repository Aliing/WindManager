package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class IpNatMap implements Serializable, Cloneable {
	private String localIp;
	private String natIp;

	public String getLocalIp() {
		return localIp;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	public String getNatIp() {
		return natIp;
	}

	public void setNatIp(String natIp) {
		this.natIp = natIp;
	}

}
