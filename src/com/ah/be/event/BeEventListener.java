/**
 *@filename		BeEventListener.java
 *@version
 *@author		Steven
 *@createtime	2007-9-6  02:17:19
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
public interface BeEventListener
{

	/**
	 * 
	 *
	 *
	 *@param 
	 *
	 *@return
	 */
	public void eventGenerated(BeBaseEvent arg_Event);
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
