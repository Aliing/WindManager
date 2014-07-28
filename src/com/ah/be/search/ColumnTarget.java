/**
 * @filename			ColumnTarget.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 
 */
@Entity
@DiscriminatorValue("CT")
@SuppressWarnings("serial")
public class ColumnTarget extends Target {

	@Column(name="column_name")
	private String column;
	
	public ColumnTarget() {
	}

	public ColumnTarget(String action, 
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
	 * getter of column
	 * @return the column
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * setter of column
	 * @param column the column to set
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	public String toString() {
		if(this == null) {
			return "null";
		}
		
		StringBuffer buffer = new StringBuffer("[ColumnTarget]");
		buffer.append(" action: ").append(this.getAction()).append(";");
		buffer.append(" feature: ").append(this.getFeature()).append(";");
		buffer.append(" type: ").append(this.getType()).append(";");
		buffer.append(" column: ").append(this.getColumn()).append(";");
		
		return buffer.toString();
	}
}
