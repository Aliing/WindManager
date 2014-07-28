package com.ah.be.debug.tcpserver;

/**
 * 
 *@filename		TcpChannelException.java
 *@version		V1.0.0.0
 *@author		xiaxiaoyin & juyizhou
 *@createtime	2008-1-8 02:39:25
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
public class TcpChannelException extends Exception
{
	final private static long	serialVersionUID	= 0L;

	/**
	 * Construct method
	 */
	public TcpChannelException()
	{
		super();
	}

	/**
	 * Construct method
	 * 
	 * @param message
	 *            description of exception
	 */
	public TcpChannelException(String message)
	{
		super(message);
	}

	/**
	 * Construct method
	 * 
	 * @param message
	 *            description of exception
	 * @param cause
	 *            cause of exception
	 */
	public TcpChannelException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct method
	 * 
	 * @param message
	 *            description of exception
	 * @param cause
	 *            cause of exception
	 */
	public TcpChannelException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
