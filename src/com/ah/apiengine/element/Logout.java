package com.ah.apiengine.element;

import java.nio.ByteBuffer;

import com.ah.apiengine.AbstractElement;
import com.ah.apiengine.DecodeException;
import com.ah.apiengine.EncodeException;

public class Logout extends AbstractElement {

	private static final long serialVersionUID = 1L;

	@Override
	public short getElemType() {
		return LOGOUT;
	}

	@Override
	public String getElemName() {
		return "Logout";
	}

	@Override
	public int encode(ByteBuffer respBB) throws EncodeException {
		return 0;
	}

	@Override
	public int decode(ByteBuffer reqBB, int len) throws DecodeException {
		return 0;
	}

}