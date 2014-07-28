package com.ah.ws.rest.models.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Vhm {
	private String vhmId;

	public String getVhmId() {
		return vhmId;
	}

	public void setVhmId(String vhmId) {
		this.vhmId = vhmId;
	}
}
