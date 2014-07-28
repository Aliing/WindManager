package com.ah.be.admin.restoredb;

public interface CheckRowValidInterface {
	/**
	 * check valid of this row
	 * @param xmlParser		object of XML reader
	 * @param row			row of XML data
	 * @return
	 */
	public boolean isValid(AhRestoreGetXML xmlParser,int row);
}
