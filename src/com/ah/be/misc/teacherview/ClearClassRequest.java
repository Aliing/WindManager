/**
 * @filename			ClearClassRequest.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.5R1
 * 
 * Copyright (c) 2006-2010 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.misc.teacherview;

/**
 * This class is used to clear the students of class on HiveAP.
 * 
 * This kind of request will be added into a map in memory when a teacher 
 * selects a designated HiveAP for a class while currently this HiveAP is
 * not connected.
 * 
 * The request will be sent to HiveAP once it connects to HM.
 */
public class ClearClassRequest {
	private String classId;
	private String apAddress;

	public ClearClassRequest() {
		
	}
	
	public ClearClassRequest(String apAddress, String classId) {
		this.apAddress = apAddress;
		this.classId = classId;
	}
	
	/**
	 * getter of classId
	 * @return the classId
	 */
	public String getClassId() {
		return classId;
	}

	/**
	 * setter of classId
	 * @param classId the classId to set
	 */
	public void setClassId(String classId) {
		this.classId = classId;
	}

	/**
	 * getter of apAddress
	 * @return the apAddress
	 */
	public String getApAddress() {
		return apAddress;
	}

	/**
	 * setter of apAddress
	 * @param apAddress the apAddress to set
	 */
	public void setApAddress(String apAddress) {
		this.apAddress = apAddress;
	}
	
	
	
}
