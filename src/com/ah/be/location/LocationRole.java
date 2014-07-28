/**
 * @filename			LocationRole.java
 * @version				1.0
 * @author				Administrator
 * @since				3.3
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.location;

/**
 * Role who takes part in location tracking.
 * 
 * A location role could be tracker or tracking client.
 */
public class LocationRole {
	private String identification;

	public LocationRole() {
		
	}
	
	public LocationRole(String identification) {
		this.identification = identification;
	}
	
	/**
	 * getter of identification
	 * @return the identification
	 */
	public String getIdentification() {
		return identification;
	}

	/**
	 * setter of identification
	 * @param identification the identification to set
	 */
	public void setIdentification(String identification) {
		this.identification = identification;
	}

}
