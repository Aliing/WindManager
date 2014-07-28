package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class HiveApIpRoute implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String sourceIp;
	
	private String netmask;
	
	private String gateway;
	
	private boolean advertiseCvg = false;
	
	private boolean distributeBR = false;

	public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public boolean isAdvertiseCvg() {
		return advertiseCvg;
	}

	public void setAdvertiseCvg(boolean advertiseCvg) {
		this.advertiseCvg = advertiseCvg;
	}
	
	public boolean isDistributeBR() {
		return distributeBR;
	}

	public void setDistributeBR(boolean distributeBR) {
		this.distributeBR = distributeBR;
	}

}