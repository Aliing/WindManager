package com.ah.be.admin.cidClients;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("content")
public class CidDevicesRequestEBO {
	
	@XStreamAsAttribute
	private String version = "1.0";
	
	@XStreamAlias("CustomerId")
	private String customerId;
	
	@XStreamAlias("PageNumber")
	private String pageNumber;
	
	@XStreamAlias("PageSize")
	private String pageSize;
	
	@XStreamAlias("SortList")
	private List<CidDeviceSortEBO> deviceSort;
	
	public CidDevicesRequestEBO(){
		
	}
	
	public CidDevicesRequestEBO(String customerId,String pageNumber,String pageSize,
			List<CidDeviceSortEBO> deviceSort){
		this.customerId = customerId;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.deviceSort = deviceSort;
	}
	
	public String getCustomerId(){
		return this.customerId;
	}
	
	public void setCustomerId(String customerId){
		this.customerId = customerId;
	}
	
	public String getPageNumber(){
		return this.pageNumber;
	}
	
	public void setPageNumber(String pageNumber){
		this.pageNumber = pageNumber;
	}
	
	public String getPageSize(){
		return this.pageSize;
	}
	
	public void setPageSize(String pageSize){
		this.pageSize = pageSize;
	}
	
	public List<CidDeviceSortEBO> getDeviceSort(){
		return this.deviceSort;
	}
	
	public void setDeviceSort(List<CidDeviceSortEBO> deviceSort){
		this.deviceSort = deviceSort;
	}

}
