/**
 * @filename			NameValuePair.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import java.io.Serializable;

import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class NameValuePair implements Serializable {

	private String name;
	
	private String value;
	
	public NameValuePair() {
		
	}

	public NameValuePair(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	/**
	 * getter of name
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * setter of name
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	
	

}
