package com.ah.be.admin.cidClients;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("Sort")
@XStreamConverter(value = ToAttributedValueConverter.class,strings={"orderBy"})
public class CidDeviceSortEBO {
	
	@XStreamAsAttribute
	private String direction;
	
	private String orderBy;
	
	public CidDeviceSortEBO(){
		
	}
	
	public CidDeviceSortEBO(String direction,String orderBy){
		this.direction = direction;
		this.orderBy = orderBy;
	}
	
	public String getDirection(){
		return direction;
	}
	
	public void setDirectioin(String direction){
		this.direction = direction;
	}
	
	public String getOrderBy(){
		return this.orderBy;
	}
	
	public void setOrderBy(String orderBy){
		this.orderBy = orderBy;
	}

}
