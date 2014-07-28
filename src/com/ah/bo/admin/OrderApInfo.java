package com.ah.bo.admin;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class OrderApInfo implements Serializable {
	private static final long	serialVersionUID	= 1L;

	@Column(length = 20)
	private String				mac;

	@Column(length = 14)
	private String				sn;

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

}
