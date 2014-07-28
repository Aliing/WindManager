package com.ah.ui.actions.home.clientManagement.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("content")
public class EnableClientManageEBO {
	
	@XStreamAsAttribute
	private String version = "1.0";
	
	@XStreamAlias("CustomerId")
	private String customId;
	
	@XStreamAlias("ClientManagementStatus")
	private String clientManagementStatus;
	
	@XStreamAlias("HmId")
	private String hmId;
	
	public String getHmId() {
		return hmId;
	}

	public void setHmId(String hmId) {
		this.hmId = hmId;
	}
	
/*	@XStreamAlias("CidPolicyEnforcement")
	private String cidPolicyEnforcement;*/
	
/*	@XStreamAlias("UseCustomCert")
	private String useCustomCert;
	
	@XStreamAlias("OnboardCA")
	private String onboardCA;
	
	@XStreamAlias("OnboardCAKey")
	private String onboardCAKey;*/
	
	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}
	
	public String getClientManagementStatus(){
		return clientManagementStatus;
	}
	
	public void setClientManagementStatus(String clientManStatus){
		this.clientManagementStatus = clientManStatus;
	}
	
/*	public String getCidPolicyEnforcement(){
		return cidPolicyEnforcement;
	}
	
	public void setCidPolicyEnforcement(String cidPolicyEnforcement){
		this.cidPolicyEnforcement = cidPolicyEnforcement;
	}*/
	
/*	public String getUseCustomCert(){
		return useCustomCert;
	}
	
	public void setUseCustomCert(String useCustomCert){
		this.useCustomCert = useCustomCert;
	}
	
	public String getOnboardCA() {
		return onboardCA;
	}

	public void setOnboardCA(String onboardCA) {
		this.onboardCA = onboardCA;
	}
	
	public String getOnboardCAKey() {
		return onboardCAKey;
	}

	public void setOnboardCAKey(String onboardCAKey) {
		this.onboardCAKey = onboardCAKey;
	}*/

}
