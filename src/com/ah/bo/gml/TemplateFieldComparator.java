/**
 * @filename			TemplateFieldComparator.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.bo.gml;

import java.util.Comparator;

/**
 * A comparator for TemplateField objects.
 * The TemplateField objects will be compared by the order of the field.
 */
public class TemplateFieldComparator implements Comparator<TemplateField> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(TemplateField field1, TemplateField field2) {
		if(field1 == null) {
			return -1;
		}
		
		if(field2 == null) {
			return 1;
		}
		
		return field1.getPlace() - field2.getPlace();
	}

}
