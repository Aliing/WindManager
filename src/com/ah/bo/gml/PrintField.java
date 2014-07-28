/**
 * @filename			PrintField.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4R3
 * 
 * Copyright (c) 2006-2010 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.bo.gml;

/**
 * The class is used in printing account.
 * field label is from print template
 * field value is from user object 
 */
public class PrintField {

	// label of field
	private String label;
	
	// value of field
	private String value;

	public PrintField() {
		
	}
	
	public PrintField(String label, String value) {
		this.label = label;
		this.value = value;
	}

	/**
	 * getter of label
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * setter of label
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
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
	 * @param args
	 * @author Joseph Chen
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
