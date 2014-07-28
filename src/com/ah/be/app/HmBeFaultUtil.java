/**
 *@filename		HmBeFaultUtil.java
 *@version
 *@author		Steven
 *@createtime	2007-9-5 04:19:56 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.app;

import com.ah.be.event.BeBaseEvent;
import com.ah.bo.hiveap.HiveAp;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class HmBeFaultUtil
{
	public static void addTrapToEventQueue(BeBaseEvent arg_Trap)
	{
		AhAppContainer.HmBe.getFaultModule().addTrapToQueue(arg_Trap);
	}

	public void sendAlarm(HiveAp hiveAp, short severity, String description)
	{
		AhAppContainer.HmBe.getFaultModule().sendAlarm(hiveAp, severity, description);
	}

	public static void setCapwapTrapFilterInterval(int filterInterval)
	{
		AhAppContainer.HmBe.getFaultModule().setCapwapTrapFilterInterval(filterInterval);
	}
}