package com.ah.apiengine;

import java.nio.ByteBuffer;

import javax.servlet.http.HttpServletRequest;

public interface Request extends Header, Message {

    //*********************************************************************************
    // Methods for API-Engine
    //*********************************************************************************

	HttpServletRequest getHttpServletRequest();

	void setHttpServletRequest(HttpServletRequest httpServletRequest);

	ByteBuffer execute() throws EncodeException;

	void callback();

}