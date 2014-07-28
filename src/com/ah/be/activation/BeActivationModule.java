/**
 *@filename		BeActivationModule.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3 02:04:23 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.activation;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public interface BeActivationModule {
	
	// how long HiveManager can use without activation key, the unit is day
	int ACTIVATION_KEY_GRACE_PERIOD = 30;
	
	//for test
	//int ACTIVATION_KEY_GRACE_PERIOD = 5;
	
	//int SEND_VERSION_INFO_DEFAULT_PERIOD = 8;
	
	// the unit is hour
	int SEND_VERSION_INFO_DEFAULT_PERIOD = 7*24;

}