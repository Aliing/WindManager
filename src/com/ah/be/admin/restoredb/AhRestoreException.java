/**
 *@filename	AhRestoreException.java
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
public class AhRestoreException extends Exception
{
	private static final long	serialVersionUID	= 1L;

	/**
	 * 
	 */
	public AhRestoreException()
	{
		super();
	}

	/**
	 * @param string
	 */
	public AhRestoreException(String arg_String)
	{
		super(arg_String);
	}
}
