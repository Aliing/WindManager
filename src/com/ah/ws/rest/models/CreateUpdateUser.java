package com.ah.ws.rest.models;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateUpdateUser {

	private String customerId;

	/*
	 * type
	 * 0, all users under customerId
	 * 1, users have permission on IDM
	 * 2, users have permission on one VHM
	 * 3, users have permission on OPR
	 *
	 */
	private short optType;

	private String productId;

	private List<CustomerUserInfo> users;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public short getOptType() {
		return optType;
	}

	public void setOptType(short optType) {
		this.optType = optType;
	}

	public List<CustomerUserInfo> getUsers() {
		return users;
	}

	public void setUsers(List<CustomerUserInfo> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "CreateUpdateUser [customerId=" + customerId + ", optType="
				+ optType + ", productId=" + productId + ", users size=" + (users != null? users.size() : 0) + "]";
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

}
