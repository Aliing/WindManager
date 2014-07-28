package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.ah.bo.HmBoBase;

@Embeddable
public class StaticRoutes implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(length=HmBoBase.IP_ADDRESS_LENGTH)
	private String destinationIp;
	
	@Column(length=HmBoBase.IP_ADDRESS_NETMASK_LENGTH)
	private String netmask;
	
	@Column(length=HmBoBase.IP_ADDRESS_LENGTH)
	private String gateway;

	public String getDestinationIp() {
		return destinationIp;
	}

	public void setDestinationIp(String destinationIp) {
		this.destinationIp = destinationIp;
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
	
}
