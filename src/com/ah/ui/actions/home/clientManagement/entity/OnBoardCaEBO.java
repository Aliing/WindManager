package com.ah.ui.actions.home.clientManagement.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("content")
public class OnBoardCaEBO {
	
	@XStreamAsAttribute
	private String version = "1.0";
	
	@XStreamAlias("CertPayload")
	private String certPayload;
	
	@XStreamAlias("ApiKey")
	private String apiKey;
	
	public String getCertPayload(){
		return this.certPayload;
	}
	
	public void setCertPayload(String certPayload){
		this.certPayload = certPayload;
	}
	
	public String getApiKey(){
		return this.apiKey;
	}
	
	public void setApiKey(String apiKey){
		this.apiKey = apiKey;
	}

}
