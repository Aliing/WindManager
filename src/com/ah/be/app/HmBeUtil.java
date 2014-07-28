/**
 *@filename		HmBeUtil.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3 09:51:59 AM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.app;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class HmBeUtil {

	public static boolean startWatchDog() {
		return AhAppContainer.HmBe.getWatchDogModule().startWatch();
	}

	public static boolean stopWatchDog() {
		return AhAppContainer.HmBe.getWatchDogModule().stopWatch();
	}

}