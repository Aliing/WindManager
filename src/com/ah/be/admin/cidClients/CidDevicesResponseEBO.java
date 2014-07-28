package com.ah.be.admin.cidClients;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("content")
public class CidDevicesResponseEBO {
	
	@XStreamAlias("TotalNumber")
	private String totalNumber;
	
	@XStreamAlias("TotalPages")
	private String totalPages;
	
	@XStreamAlias("CidList")
	private List<CidDeviceEBO> cidList;
	
	public String getTotalNumber(){
		return this.totalNumber;
	}
	
	public void setTotalNumber(String totalNumber){
		this.totalNumber = totalNumber;
	}
	
	public String getTotalPages(){
		return this.totalPages;
	}
	
	public void setTotalPages(String totalPages){
		this.totalPages = totalPages;
	}
	
	public List<CidDeviceEBO> getCidList(){
		return this.cidList;
	}
	
	public void setCidList(List<CidDeviceEBO> cidList){
		this.cidList = cidList;
	}

}
