package com.ah.ws.rest.models;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DeleteResetUser {

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

	private Object[] userEmails;

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

	public Object[] getUserEmails() {
		return userEmails;
	}

	public void setUserEmails(Object[] userEmails) {
		this.userEmails = userEmails;
	}

	@Override
	public String toString() {
		return "DeleteResetUser [customerId=" + customerId + ", optType="
				+ optType + ", userEmails=" + Arrays.toString(userEmails) + "]";
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

}
