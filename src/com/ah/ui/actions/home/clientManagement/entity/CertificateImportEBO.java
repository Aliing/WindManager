package com.ah.ui.actions.home.clientManagement.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("content")
public class CertificateImportEBO {

	@XStreamAsAttribute
	private String version="1.0";
	
	@XStreamAlias("CustomerId")
	private String customId;
	
	@XStreamAlias("CertType")
	private String certType;
	
	@XStreamAlias("CertName")
	private String certName;
	
	@XStreamAlias("CertPayload")
	private String certPayload;
	
	@XStreamAlias("CertPrivateKey")
	private String certPrivateKey;
	
	@XStreamAlias("PrivateKeyPassword")
	private String privateKeyPassword;
	
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
	
	public String getCertPrivateKey() {
		return certPrivateKey;
	}

	public void setCertPrivateKey(String certPrivateKey) {
		this.certPrivateKey = certPrivateKey;
	}
	
	public String getPrivateKeyPassword() {
		return privateKeyPassword;
	}

	public void setPrivateKeyPassword(String privateKeyPassword) {
		this.privateKeyPassword = privateKeyPassword;
	}
	
	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}
}
