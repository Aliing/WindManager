package com.ah.apiengine.element;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.ah.apiengine.AbstractElement;
import com.ah.apiengine.DecodeException;
import com.ah.apiengine.EncodeException;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

public class Session extends AbstractElement {

	private static final long serialVersionUID = 1L;

	private static final Tracer	log = new Tracer(Session.class.getSimpleName());

	/* Session ID */
	private String sessId = "";

	public Session() {
   	    super();
	}

	public String getSessId() {
		return sessId;
	}

	public void setSessId(String sessId) {
		if (sessId != null) {
			this.sessId = sessId;
		}		
	}

	@Override
	public short getElemType() {
		return SESSION;
	}

	@Override
	public String getElemName() {
		return "Session";
	}

	/*-
	 * API Engine/Client Session Format.
	 *
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * | Session ID Len|                 Session ID ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *
     * Length: > 1
	 */
	@Override
	public int encode(ByteBuffer bb) throws EncodeException {
		try {
			// Element Header
			int elemHeaderLen = encodeElementHeader(bb, 0);
			log.debug("encode", "Element Header Length: " + elemHeaderLen);

			// Start Position
			int startPos = bb.position();
			log.debug("encode", "Start Position: " + startPos);

			/* Session ID */
			log.debug("encode", "Session ID: " + sessId);
			byte[] toBytes = sessId.getBytes("iso-8859-1");
			int sessIdLen = toBytes.length;
			bb.put((byte) sessIdLen);
			bb.put(toBytes);

			// End Position
			int endPos = bb.position();
			log.debug("encode", "End Position: " + endPos);

			// Element Length
			int elemBodyLen = endPos - startPos;
			log.debug("encode", "Element Length: " + elemBodyLen);

			// Fill pending element length.
			fillPendingElementLength(bb, startPos, elemBodyLen);

			return elemHeaderLen + elemBodyLen;
		} catch (Exception e) {
			throw new EncodeException("Encoding '" + getElemName() + "' Error.", e);
		}
	}

	/*-
	 * API Engine/Client Session Format.
	 *
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * | Session ID Len|                 Session ID ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *
     * Length: > 1
	 */
	@Override
	public int decode(ByteBuffer bb, int len) throws DecodeException {
		try {
			// Start Position
			int startPos = bb.position();
			log.debug("decode", "Start Position: " + startPos);

			/* Session ID */
			byte sessIdLen = bb.get();
			sessId = AhDecoder.bytes2String(bb, sessIdLen);
			log.debug("decode", "Session ID[len/value]: " + sessIdLen + "/" + sessId);

			// End Position
			int endPos = bb.position();
			log.debug("decode", "End Position: " + endPos);

			return endPos - startPos;
		} catch (BufferUnderflowException e) {
			throw new DecodeException("Incorrect element length '" + len + "' for " + getElemName(), e);
		} catch (Exception e) {
			throw new DecodeException("Decoding '" + getElemName() + "' Error.", e);
		}
	}

}