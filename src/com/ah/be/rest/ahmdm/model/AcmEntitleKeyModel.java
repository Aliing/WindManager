/**
 *@filename		AcmEntitleKeyModel.java
 *@version
 *@author		Fiona
 *@createtime	Sep 17, 2013 2:38:10 PM
 *Copyright (c) 2006-2013 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.rest.ahmdm.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@XStreamAlias("content")
public class AcmEntitleKeyModel {
	
	@XStreamAsAttribute
	private String version = "1.0";
	
	@XStreamAlias("CustomerId")
	private String customerId;
	
	@XStreamAlias("HmId")
	private String hmId;
	
	@XStreamAlias("EntitlementKey")
	private String entitlementKey;
	
	@XStreamAlias("OrderType")
	private String orderType;
	
	@XStreamAlias("NumOfClients")
	private String numOfClients;
	
	@XStreamAlias("StartDate")
	private String startDate;
	
	@XStreamAlias("EndDate")
	private String endDate;
	
	@XStreamAlias("SalesOrder")
	private String salesOrder;
	
	@XStreamAlias("HMVersion")
	private String hmVersion;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getHmId() {
		return hmId;
	}

	public void setHmId(String hmId) {
		this.hmId = hmId;
	}

	public String getEntitlementKey() {
		return entitlementKey;
	}

	public void setEntitlementKey(String entitlementKey) {
		this.entitlementKey = entitlementKey;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getNumOfClients() {
		return numOfClients;
	}

	public void setNumOfClients(String numOfClients) {
		this.numOfClients = numOfClients;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String getSalesOrder() {
		return salesOrder;
	}

	public void setSalesOrder(String salesOrder) {
		this.salesOrder = salesOrder;
	}

	public String getHmVersion() {
		return hmVersion;
	}

	public void setHmVersion(String hmVersion) {
		this.hmVersion = hmVersion;
	}
	
}
