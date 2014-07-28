package com.ah.apiengine;

import java.nio.ByteBuffer;

public interface Response<R extends Request> extends Header, Message {

    //*********************************************************************************
    // Methods for API-Engine
    //*********************************************************************************

	ByteBuffer build(R request) throws EncodeException;

}