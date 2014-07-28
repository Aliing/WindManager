package com.ah.be.communication;

public class BeCommunicationDecodeException extends Exception
{
	private static final long	serialVersionUID	= 1L;

	public BeCommunicationDecodeException()
	{
		super();
	}

	public BeCommunicationDecodeException(String errorMsg)
	{
		super(errorMsg);
	}

	public BeCommunicationDecodeException(String errorMsg, Throwable e)
	{
		super(errorMsg, e);
	}
}
