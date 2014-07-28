/**
 * @filename			EntityTarget.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ET")
@SuppressWarnings("serial")
public class EntityTarget extends Target {
	private Long boId;
	
	private String fieldName;
	
	@Column(length = 20480)
	private String fieldValue;
	
	private String reference;

	public EntityTarget() {
	}

	public EntityTarget(String action, 
			String feature, 
			int type, 
			String urlParams,
			String userName,
			Long userDomainId,
			Long boDomainId) {
		super(action, 
				feature, 
				type, 
				urlParams, 
				userName, 
				userDomainId,
				boDomainId);
	}

	/**
	 * getter of boId
	 * @return the boId
	 */
	public Long getBoId() {
		return boId;
	}

	/**
	 * setter of boId
	 * @param boId the boId to set
	 */
	public void setBoId(Long boId) {
		this.boId = boId;
	}

	/**
	 * getter of fieldName
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * setter of fieldName
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * getter of fieldValue
	 * @return the fieldValue
	 */
	public String getFieldValue() {
		return fieldValue;
	}

	/**
	 * setter of fieldValue
	 * @param fieldValue the fieldValue to set
	 */
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	/**
	 * getter of reference
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * setter of reference
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	

}
