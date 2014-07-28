/**
 * @filename			FilterParam.java
 * @version
 * @author				Administrator
 * @since
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

/**
 * 
 */
public class FilterParam {

	private String name;
	
	private String type;
	
	private String value;
	
	private String operator;

	public FilterParam(String name, String type, String value, String operator) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.operator = operator;
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
	 * getter of type
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * setter of type
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
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

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	
}
