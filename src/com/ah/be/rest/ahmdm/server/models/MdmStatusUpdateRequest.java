package com.ah.be.rest.ahmdm.server.models;

import com.ah.be.rest.server.models.BaseModel;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("content")
public class MdmStatusUpdateRequest extends BaseModel {
	@XStreamAlias("APMacAddress")
	private String apMacAddress;
	@XStreamAlias("CustomerId")
	private String customerId;
	@XStreamAlias("Data")
	private String data;
	
	public String getApMacAddress() {
		return apMacAddress;
	}
	public void setApMacAddress(String apMacAddress) {
		this.apMacAddress = apMacAddress;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

}
