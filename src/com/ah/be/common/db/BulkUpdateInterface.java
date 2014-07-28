package com.ah.be.common.db;

public interface BulkUpdateInterface {
	
	/**
	 * get value by column name and bo
	 * @param object
	 * @param columnName
	 * @param aValue	aValue[0] store the value
	 * @return whether use the value returned.
	 */
	boolean getValue(Object object,String columnName,Object[] aValue);
}
