package com.ah.be.admin.adminOperateImpl;

/**
 *@filename		BeOperateException.java
 *@version
 *@author		Xiaolanbao
 *@createtime	2007-9-27 10:58:18
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
public class BeOperateException extends Exception
{
	private static final long	serialVersionUID	= 1L;
	
	public BeOperateException()
	{
		super();
	}

	/**
	 * @param string
	 */
	public BeOperateException(String strException)
	{
		super(strException);
	}

}
