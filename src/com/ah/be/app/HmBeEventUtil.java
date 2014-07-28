/**
 *@filename		HmBeEventUtil.java
 *@version
 *@author		Steven
 *@createtime	2007-9-5 04:19:43 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.app;

import com.ah.be.event.BeBaseEvent;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class HmBeEventUtil {

	/**
	 * dispatch event to other modules through event module
	 * 
	 * @param arg_Event -
	 */
	public static void eventGenerated(BeBaseEvent arg_Event) {
		AhAppContainer.HmBe.getEventListener().eventGenerated(arg_Event);
	}

}