/**
 * @filename			PageDocument.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5R2
 * 
 * Copyright (c) 2006-2010 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

/**
 * 
 */
public class PageDocument implements IDocument {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	
	/*
	 * action of the page, will be used to generate URL
	 */
	private String action;
	
	/**
	 * the document is table column header
	 */
	public final static short CATEGORY_TABLE_HEADER		=	1;
	
	/**
	 * the document is an element on page
	 */
	public final static short CATEGORY_PAGE_ELEMENT		=	2;
	
	/*
	 * the category of the document
	 */
	private short category = CATEGORY_PAGE_ELEMENT;

	private int hashCode = Integer.MIN_VALUE;
	
	public PageDocument() {
		
	}
	
	public PageDocument(String action, short category) {
		this.action = action;
		this.category = category;
	}
	
	/**
	 * getter of action
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * setter of action
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
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
		
		if(!(obj instanceof PageDocument)) {
			return false;
		}
		
		PageDocument doc = (PageDocument)obj;
		
		return this.action.equals(doc.getAction())
			&& this.category == doc.getCategory();
	}
	
	@Override
	public int hashCode() {
		if(this.hashCode == Integer.MIN_VALUE) {
			if(this.action == null) {
				return super.hashCode();
			}
			
			this.hashCode = (this.action + "_" + String.valueOf(this.category)).hashCode();
		}
		
		return this.hashCode;
	}
}
