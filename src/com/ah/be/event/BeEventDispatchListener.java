/**
 *@filename		BeEventDispatchListener.java
 *@version
 *@author		Steven
 *@createtime	2007-9-12 09:50:02
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.event;

/**
 * @author		Steven
 * @version		V1.0.0.0 
 */
public interface BeEventDispatchListener
{

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void eventDispatched(BeBaseEvent arg_Event);

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public int getListenerId();
}
