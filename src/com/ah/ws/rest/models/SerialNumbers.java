package com.ah.ws.rest.models;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SerialNumbers {
	public SerialNumbers() {}

	private String vhmid;

	private List<String> sn;

	public String getVhmid() {
		return vhmid;
	}

	public void setVhmid(String vhmid) {
		this.vhmid = vhmid;
	}

	@XmlElement
	public List<String> getSn() {
		return sn;
	}

	public void setSn(List<String> sn) {
		this.sn = sn;
	}
}
