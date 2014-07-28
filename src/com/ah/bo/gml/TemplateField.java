/**
 * @filename			TemplateField.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.bo.gml;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class TemplateField implements Serializable {
	
	private static final long	serialVersionUID	= 1L;

	public static final String[] FIELDS		= {"Visitor Name", 
		"PSK", 
		"Start Time", 
		"End Time", 
		"SSID Name",
		"Visitor Company", 
		"Sponsor",
		"Comment"};
	
	public static final String[] FIELDS_NAME = {"visitorName", 
		"strPsk", 
		"startTimeString", 
		"expiredTimeString", 
		"ssidName",
		"visitorCompany", 
		"sponsor",
		"comment"};
	
	@Transient
	private String field;
	
	private String label;
	
	private boolean required;
	
	private byte place;

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
	 * getter of required
	 * @return the required
	 */
	public boolean getRequired() {
		return required;
	}

	/**
	 * setter of required
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * getter of place
	 * @return the place
	 */
	public byte getPlace() {
		return place;
	}

	/**
	 * setter of place
	 * @param place the place to set
	 */
	public void setPlace(byte place) {
		this.place = place;
	}
	
	public static Map<String, TemplateField> getDefaultFields(){
		Map<String, TemplateField> fields = new LinkedHashMap<String, TemplateField>();
		byte i = 1;
		
		for (String field : TemplateField.FIELDS) {
			TemplateField templateField = new TemplateField();
				
			if ("Visitor Company".equals(field)
					|| "Sponsor".equals(field)
					|| "Comment".equals(field)) {
				templateField.setRequired(false);
			} else {
				templateField.setRequired(true);
			}
			
			templateField.setLabel(field);
			templateField.setPlace(i++);
			
			fields.put(field, templateField);
		}
		
		return fields;
	}
	
	/**
	 * get the responding field name in class LocalUser by label
	 * 
	 * @param label -
	 * @return -
	 * @author Joseph Chen
	 */
	public static String getFieldName(String label) {
		if(label == null) {
			return null;
		}
		
		for(int i=0; i<FIELDS.length; i++) {
			if(label.equals(FIELDS[i])) {
				return FIELDS_NAME[i];
			}
		}
		
		return null;
	}
	
	@Transient
	private String restoreId;

	public String getRestoreId()
	{
		return restoreId;
	}
	
	public void setRestoreId(String restoreId)
	{
		this.restoreId = restoreId;
	}

}