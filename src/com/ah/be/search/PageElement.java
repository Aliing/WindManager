/**
 * @filename			PageElement.java
 * @version				1.0
 * @author				Josehp Chen
 * @since				3.5R2
 * 
 * Copyright (c) 2006-2010 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import java.io.Serializable;

/**
 * PageElement represents an element of character string on a page 
 */
public class PageElement implements Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private String value;
	
	private short category;
	
	private int hashCode = Integer.MIN_VALUE;
	
	public PageElement() {
		
	}
	
	public PageElement(String value, short category) {
		this.value = value;
		this.category = category;
	}

	/**
	 * getter of value
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * setter of value
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * getter of category
	 * @return the category
	 */
	public short getCategory() {
		return category;
	}

	/**
	 * setter of category
	 * @param category the category to set
	 */
	public void setCategory(short category) {
		this.category = category;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		
		if(!(obj instanceof PageElement)) {
			return false;
		}
		
		PageElement element = (PageElement)obj;
		
		return this.value.equals(element.getValue())
			&& this.category == element.getCategory();
	}
	
	@Override
	public int hashCode() {
		if(this.hashCode == Integer.MIN_VALUE) {
			if(this.value == null) {
				return super.hashCode();
			}
			
			this.hashCode = (this.value + "_" + String.valueOf(this.category)).hashCode();
		}
		
		return this.hashCode;
	}
}
