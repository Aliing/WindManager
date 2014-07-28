package com.ah.ui.actions.home.clientManagement.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("content")
public class CsrCertificateEBO {
	
	@XStreamAsAttribute
	private String version = "1.0";
	
	@XStreamAlias("CustomerId")
	private String customerId;
	
	@XStreamAlias("CertType")
	private String certType;
	
	@XStreamAlias("CSRPayload")
	private String csrPayload;
	
	@XStreamAlias("HmId")
	private String hmId;
	
	public String getHmId() {
		return hmId;
	}

	public void setHmId(String hmId) {
		this.hmId = hmId;
	}
	
	public String getCustomerId(){
		return this.customerId;
	}
	
	public void setCustomerId(String customerId){
		this.customerId = customerId;
	}
	
	public String getCertType(){
		return this.certType;
	}
	
	public void setCertType(String certType){
		this.certType = certType;
	}
	
	public String getCsrPayload(){
		return this.csrPayload;
	}
	
	public void setCsrPayload(String csrPayload){
		this.csrPayload = csrPayload;
	}

}
