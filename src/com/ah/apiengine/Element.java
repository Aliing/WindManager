package com.ah.apiengine;

import java.io.Serializable;
import java.nio.ByteBuffer;

public interface Element extends Serializable, ElementTypes {

	short getElemType();

	String getElemName();

	int encode(ByteBuffer respBB) throws EncodeException;

	int decode(ByteBuffer reqBB, int msgLen) throws DecodeException;

}