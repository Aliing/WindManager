package com.ah.ui.actions.home.clientManagement.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("content")
public class CertificateQueryResEBO {

	@XStreamAsAttribute
	private String version = "1.0";
	
	@XStreamAlias("CertName")
	private String certName;
	
	@XStreamAlias("CertPayload")
	private String certPayload;
	
	public String getCertName() {
		return certName;
	}

	public void setCertName(String certName) {
		this.certName = certName;
	}
	
	public String getCertPayload() {
		return certPayload;
	}

	public void setCertPayload(String certPayload) {
		this.certPayload = certPayload;
	}
}
