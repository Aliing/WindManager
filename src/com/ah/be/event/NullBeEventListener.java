/**
 *@filename		NullBeEventListener.java
 *@version
 *@author		Steven
 *@createtime	2007-9-7 02:07:24
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
public class NullBeEventListener implements BeEventListener
{

	/** 
	 * @see com.ah.be.event.BeEventListener#eventGenerated(com.ah.be.event.BeBaseEvent)
	 */
	public void eventGenerated(BeBaseEvent arg_Event)
	{

		return;
	}

	/**
	 * 
	 * @see com.ah.be.event.BeEventListener#getListenerId()
	 */
	public int getListenerId()
	{

		return -1;
	}

}
