/**
 * @filename			FieldTarget.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5R2
 * 
 * Copyright (c) 2006-2010 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 
 */
@Entity
@DiscriminatorValue("FT")
@SuppressWarnings("serial")
public class FieldTarget extends Target {
	@Column(name="field", length = 2048)
	private String field;

	public FieldTarget() {
		
	}
	
	public FieldTarget(String action, 
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
	
	public String toString() {
		if(this == null) {
			return "null";
		}
		
		StringBuffer buffer = new StringBuffer("[ColumnTarget]");
		buffer.append(" action: ").append(this.getAction()).append(";");
		buffer.append(" feature: ").append(this.getFeature()).append(";");
		buffer.append(" type: ").append(this.getType()).append(";");
		buffer.append(" field: ").append(this.getField()).append(";");
		
		return buffer.toString();
	}

	/**
	 * getter of field
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * setter of field
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}

	

}
