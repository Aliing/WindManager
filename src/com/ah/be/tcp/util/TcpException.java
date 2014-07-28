package com.ah.be.tcp.util;

public class TcpException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct method
	 */
	public TcpException()
	{
		super();
	}

	/**
	 * Construct method
	 * 
	 * @param message
	 *            description of exception
	 */
	public TcpException(String message)
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
	public TcpException(Throwable cause)
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
	public TcpException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
