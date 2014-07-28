package com.ah.apiengine;

import java.nio.ByteBuffer;

public interface PacketParsable extends ElementTypes {

	Request parseRequest(ByteBuffer bb) throws DecodeException;

}