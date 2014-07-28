package com.ah.be.admin.cidClients;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("content")
public class CidDevicesSendEBO {
	
	@XStreamAsAttribute
	private String version = "1.0";
	
	@XStreamAlias("CustomerId")
	private String customerId;
	
	@XStreamAlias("CidList")
	private List<CidDeviceEBO> cidList;
	
	public CidDevicesSendEBO(){
		
	}
	
	public CidDevicesSendEBO(String customerId,List<CidDeviceEBO> cidList){
		this.customerId = customerId;
		this.cidList = cidList;
	}
	
	public String getCustomerId(){
		return this.customerId;
	}
	
	public void setCustomerId(String customerId){
		this.customerId = customerId;
	}
	
	public List<CidDeviceEBO> getCidList(){
		return this.cidList;
	}
	
	public void setCidList(List<CidDeviceEBO> cidList){
		this.cidList = cidList;
	}

}
