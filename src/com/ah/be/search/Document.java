/**
 * @filename			IndexOutputStream.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

/**
 * 
 */
public class Document implements IDocument {
	private int tableId;
	
	private long domainId;
	
	private long boId;

	public Document(int tableId, long domainId, long boId) {
		super();
		this.tableId = tableId;
		this.domainId = domainId;
		this.boId = boId;
	}

	/**
	 * getter of tableId
	 * @return the tableId
	 */
	public int getTableId() {
		return tableId;
	}

	/**
	 * setter of tableId
	 * @param tableId the tableId to set
	 */
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	/**
	 * getter of domainId
	 * @return the domainId
	 */
	public long getDomainId() {
		return domainId;
	}

	/**
	 * setter of domainId
	 * @param domainId the domainId to set
	 */
	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	/**
	 * getter of boId
	 * @return the boId
	 */
	public long getBoId() {
		return boId;
	}

	/**
	 * setter of boId
	 * @param boId the boId to set
	 */
	public void setBoId(long boId) {
		this.boId = boId;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(tableId);
		buffer.append(",").append(domainId);
		buffer.append(",").append(boId);
		
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		
		if(!(obj instanceof Document)) {
			return false;
		}
		
		Document doc = (Document)obj;
		
		if(this.getTableId() == doc.getTableId()
				&& this.getDomainId() == doc.getDomainId()
				&& this.getBoId() == doc.getBoId()) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return (int)this.boId;
	}
	
}
