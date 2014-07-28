/**
 * @filename			HmBeLocationUtil.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.3
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.app;

import com.ah.be.location.TrackingList;
import com.ah.be.location.TrackingParameter;

/**
 * 
 */
public class HmBeLocationUtil {
	/**
	 * add some target clients to track for location
	 * 
	 * @param clients
	 * @throws Exception
	 * @author Joseph Chen
	 */
	public static void startTracking(TrackingList clients) throws Exception {
		AhAppContainer.HmBe.getLocationModule().startTracking(clients);
	}

	/**
	 * cancel some target tracking clients
	 * 
	 * @param clients
	 * @throws Exception
	 * @author Joseph Chen
	 */
	public static void stopTracking(TrackingList clients) throws Exception {
		AhAppContainer.HmBe.getLocationModule().stopTracking(clients);
	}
	
	/**
	 * modify tracking parameters
	 * 
	 * @param parameter
	 * @throws Exception
	 * @author Joseph Chen
	 */
	public static void modifyTracking(TrackingParameter parameter) throws Exception {
		AhAppContainer.HmBe.getLocationModule().modifyTracking(parameter);
	}
	
	/**
	 * query the location of some specified clients
	 * 
	 * @param clients
	 * @throws Exception
	 * @author Joseph Chen
	 */
	public static void queryLocation(TrackingList clients) throws Exception {
		AhAppContainer.HmBe.getLocationModule().queryLocation(clients);
	}
}
