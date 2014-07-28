package com.ah.ui.actions.home.clientManagement.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("content")
public class CertificateQueryEBO {

	@XStreamAsAttribute
	private String version="1.0";
	
	@XStreamAlias("CustomerId")
	private String customId;
	
	@XStreamAlias("CertType")
	private String certType;
	
	@XStreamAlias("HmId")
	private String hmId;
	
	public String getHmId() {
		return hmId;
	}

	public void setHmId(String hmId) {
		this.hmId = hmId;
	}
	
	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}
	
	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}
	
}
