/**
 * @filename			CwpPageFieldComparator.java
 * @version
 * @author				Administrator
 * @since
 * 
 * Copyright (c) 2006-2008 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.bo.wlan;

import java.util.Comparator;

/**
 * A comparator for CwpPageField objects.
 * The CwpPageField objects will be compared by the order of the field.
 */
public class CwpPageFieldComparator implements Comparator<CwpPageField> {

	@Override
	public int compare(CwpPageField field1, CwpPageField field2) {
		if(field1 == null) {
			return -1;
		}
		
		if(field2 == null) {
			return 1;
		}
		
		return field1.getPlace() - field2.getPlace();
	}

}
