/**
 *@filename		BeEventProcesser.java
 *@version
 *@author		Steven
 *@createtime	2007-9-7 02:46:44
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.event;

import java.util.Queue;
import java.util.Vector;

import com.ah.be.app.BaseModule;


/**
 * @author		Steven
 * @version		V1.0.0.0 
 */
public interface BeEventProcesser
{

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void testMethod();

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void setEventModule(BaseModule arg_Module);

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void setEventQueue(Queue<BeBaseEvent> arg_Queue);

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void setEventListener(
		Vector<BeEventDispatchListener> arg_ListenerList);

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void startProcesser();

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void stopProcesser();
}
