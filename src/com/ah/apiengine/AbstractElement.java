package com.ah.apiengine;

import java.nio.ByteBuffer;

public abstract class AbstractElement implements Element {

	private static final long	serialVersionUID	= 1L;

	/*-
	 * The format of a message element uses the TLV format shown here:
	 *
	 * 0                   1                   2                   3
	 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |              Type             |             Length            |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |   Value ...   |
	 * +-+-+-+-+-+-+-+-+
	 */
	protected int encodeElementHeader(ByteBuffer bb, int valueLen) {
		int startPos = bb.position();

		/* Type */
		bb.putShort(getElemType());

		/* Length */
		bb.putShort((short) valueLen);

		int endPos = bb.position();

		return endPos - startPos;
	}

	protected void fillPendingElementLength(ByteBuffer bb, int elemPos, int elemBodyLen) {
		bb.putShort(elemPos - 2, (short) elemBodyLen);
	}

}