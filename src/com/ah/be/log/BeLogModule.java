/**
 *@filename		BeLogModule.java
 *@version
 *@author		Steven
 *@createtime	2007-9-3 02:06:16
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.log;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public interface BeLogModule {
	public void addSystemLog(short logLevel, String moduleName, String msg);
}
