/**
 *@filename		AhCapwapEventMgmt.java
 *@version
 *@author		Francis
 *@createtime	2007-10-10 02:43:13 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Inc.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.capwap.event;

// java import
import java.io.Serializable;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public interface AhCapwapEventMgmt extends Serializable {

	void start();

	boolean isStart();

	void stop();
	
	/*
	 * Register capwap event listener.
	 */
	void register(AhCapwapEventListener listener);

	/*
	 * Unregister capwap event listener.
	 */
	void unregister(AhCapwapEventListener listener);

	/*
	 * Add Capwap request event. BlockingQueue by itself is thread safe, but in case
	 * offer() fails, we want to be able to remove the head of the queue and
	 * re-try inserting the new event in the queue. Therefore the
	 * synchronization.
	 */
	void addEvent(AhCapwapEvent event);

	void notify(AhCapwapEvent event);

}