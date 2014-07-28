/**
 * @filename			BeLocationModule.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.3
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.location;

import java.util.concurrent.BlockingQueue;

import com.ah.be.event.BeBaseEvent;

/**
 * location module of HM BE
 */
public interface BeLocationModule {
	/**
	 * add some target clients to track for location
	 * 
	 * @param clients
	 * @throws Exception
	 * @author Joseph Chen
	 */
	public void startTracking(TrackingList clients) throws Exception;
	
	/**
	 * cancel some target tracking clients
	 * 
	 * @param clients
	 * @throws Exception
	 * @author Joseph Chen
	 */
	public void stopTracking(TrackingList clients) throws Exception;
	
	/**
	 * modify tracking parameters
	 * 
	 * @param parameter
	 * @throws Exception
	 * @author Joseph Chen
	 */
	public void modifyTracking(TrackingParameter parameter) throws Exception;
	
	/**
	 * query the location of some specified clients
	 * 
	 * @param clients
	 * @throws Exception
	 * @author Joseph Chen
	 */
	public void queryLocation(TrackingList clients) throws Exception;
	
	/**
	 * report location of tracking clients
	 * 
	 * @param reports
	 * @throws Exception
	 * @author Joseph Chen
	 */
	public void reportLocation(LocationReport reports) throws Exception;
	
	/**
	 * get the location event queue
	 * @return
	 * @author Joseph Chen
	 */
	public BlockingQueue<BeBaseEvent> getEventQueue();
	
}
