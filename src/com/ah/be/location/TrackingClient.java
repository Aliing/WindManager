/**
 * @filename			TrackingClient.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.3
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.location;

/**
 * client to be tracked for location
 */
public class TrackingClient extends LocationRole {
	
	
	public TrackingClient() {
		super();
	}
	
	public TrackingClient(String identification) {
		super(identification);
	}	
}
