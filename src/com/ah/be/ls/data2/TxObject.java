package com.ah.be.ls.data2;

import java.nio.ByteBuffer;

public interface TxObject {
	public void unpack(ByteBuffer buf);

	public ByteBuffer pack();
}
