package com.ah.be.admin.restoredb;

public interface GetColumnValueInterface {
	/**
	 * get value of column
	 * @param columnName name of column
	 * @param value 
	 * 		the value in XML file
	 * @param xmlParser		object of XML reader
	 * @param row			row of XML data
	 * @return
	 */
	public String getValue(String columnName,String value,AhRestoreGetXML xmlParser,int row);
}
