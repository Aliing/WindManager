package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class HiveApMultipleVlan implements Serializable {

	private static final long serialVersionUID = 1L;

	private String vlanid;

	public String getVlanid() {
		return vlanid;
	}
	public void setVlanid(String vlanid) {
		this.vlanid = vlanid;
	}

}