/**
 * @filename			CwpPageField.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.2
 * 
 * Copyright (c) 2006-2008 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.bo.wlan;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * 
 */
@Embeddable
public class CwpPageField implements Serializable {

	private static final long	serialVersionUID	= 1L;
	
	public static final String FIRSTNAME="First Name";
	
	public static final String LASTNAME="Last Name";
	
	public static final String EMAIL="Email";
	
	public static final String PHONE="Phone";
	
	public static final String VISITING="Visiting";
	
	public static final String COMMENT="Comment";
	
	public static final String REPRESENTING="Representing";
	
	public static final String FIRSTNAMEMARK="FirstNameMark";
	
	public static final String LASTNAMEMARK="LastNameMark";
	
	public static final String EMAILMARK="EmailMark";
	
	public static final String PHONEMARK="PhoneMark";
	
	public static final String VISITINGMARK="VisitingMark";
	
	public static final String COMMENTMARK="CommentMark";
	
	public static final String REPRESENTINGMARK="RepresentingMark";

	public static final String[] FIELDS		= {FIRSTNAME,LASTNAME,EMAIL,PHONE,VISITING,COMMENT,REPRESENTING};
		
	//public static final String[] FIELDS		= {"First Name", "Last Name", "Email", "Phone", "Visiting", "Comment"};
	
	@Transient
	private String field;
	
	private String label;
	
	private boolean enabled;
	
	private boolean required;
	
	private byte place;
	
	private String labelName;

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
	 * getter of enabled
	 * @return the enabled
	 */
	public boolean getEnabled() {
		return enabled;
	}

	/**
	 * setter of enabled
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
	
	private String label2;
	
	private String label3;
	
	private String label4;
	
	private String label5;
	
	private String label6;
	
	private String label7;
	
	private String label8;
	
	private String label9;

	private String fieldMark;
	
	
	public String getFieldMark() {
		return fieldMark;
	}

	public void setFieldMark(String fieldMark) {
		this.fieldMark = fieldMark;
	}

	public String getLabel2() {
		return label2;
	}

	public void setLabel2(String label2) {
		this.label2 = label2;
	}

	public String getLabel3() {
		return label3;
	}

	public void setLabel3(String label3) {
		this.label3 = label3;
	}

	public String getLabel4() {
		return label4;
	}

	public void setLabel4(String label4) {
		this.label4 = label4;
	}

	public String getLabel5() {
		return label5;
	}

	public void setLabel5(String label5) {
		this.label5 = label5;
	}

	public String getLabel6() {
		return label6;
	}

	public void setLabel6(String label6) {
		this.label6 = label6;
	}

	public String getLabel7() {
		return label7;
	}

	public void setLabel7(String label7) {
		this.label7 = label7;
	}

	public String getLabel8() {
		return label8;
	}

	public void setLabel8(String label8) {
		this.label8 = label8;
	}

	public String getLabel9() {
		return label9;
	}

	public void setLabel9(String label9) {
		this.label9 = label9;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

}
