/**
 *@filename	AhRestoreColNotExistException.java
 *@version
 *@author		Fisher
 *@createtime	2007-05-11 10:35:27
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.admin.restoredb;

/**
 * @author Fisher
 * @version V1.0.0.0
 */

public class AhRestoreColNotExistException extends Exception
{
	private static final long	serialVersionUID	= 1L;
	/**
	 * 
	 */
	public AhRestoreColNotExistException()
	{
		super();
	}
	/**
	 * @param string
	 */
	public AhRestoreColNotExistException(String arg_String)
	{
		super(arg_String);
	}

}
