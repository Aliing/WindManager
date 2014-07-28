package com.ah.ws.rest.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SerialNumberList {
	public SerialNumberList() {
	}
	public SerialNumberList(short status) {
		super();
		this.status = status;
	}
	private short status;

	private String description;

	private List<String> sn;

	public short getStatus() {
		return status;
	}
	public void setStatus(short status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@XmlElement
	public List<String> getSn() {
		return sn;
	}
	public void setSn(List<String> sn) {
		this.sn = sn;
	}
	public void setSerialNumber(String sn) {
		if (this.sn == null) this.sn = new ArrayList<String>();
		if (!this.sn.contains(sn)) {
			this.sn.add(sn);
		}
	}
}