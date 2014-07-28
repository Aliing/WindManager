/**
 * @filename			HAMonitor.java
 * @version				1.0
 * @author				Joseph chen
 * @since				3.3
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.ha;

public interface HAMonitor extends HAStatusObserver {
	
	/**
	 * Start HA monitor.
	 *
	 * @throws HAException -
	 * @author Joseph Chen
	 */
	void start() throws HAException;
	
	/**
	 * Stop HA monitor.
	 *
	 * @throws HAException -
	 * @author Joseph Chen
	 */
	void stop() throws HAException;

	boolean isSuspending();

	void setSuspending(boolean suspending);
	
	/**
	 * Get current HA status.
	 *
	 * @return -
	 * @author Joseph Chen
	 */
	HAStatus getCurrentStatus();

	/**
	 * Change new HA status.
	 *
	 * @param newStatus the new HA status
	 * @author Joseph Chen
	 */
	void changeStatus(HAStatus newStatus);

}