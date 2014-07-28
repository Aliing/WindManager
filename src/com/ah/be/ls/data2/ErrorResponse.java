package com.ah.be.ls.data2;

import java.nio.ByteBuffer;

import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class ErrorResponse implements TxObject {
	String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ByteBuffer pack() {
		ByteBuffer buf = ByteBuffer.allocate(8192);
		AhEncoder.putString(buf, message);

		buf.flip();
		return buf;
	}

	public void unpack(ByteBuffer buf) {
		message = AhDecoder.getString(buf);
	}

}
