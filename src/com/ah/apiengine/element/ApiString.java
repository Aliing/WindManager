package com.ah.apiengine.element;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.ah.apiengine.AbstractElement;
import com.ah.apiengine.DecodeException;
import com.ah.apiengine.EncodeException;
import com.ah.util.Tracer;

public class ApiString extends AbstractElement {

	private static final long	serialVersionUID	= 1L;
	private static final Tracer	log					= new Tracer(ApiString.class);
	private String				str;

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	@Override
	public int decode(ByteBuffer bb, int msgLen) throws DecodeException {
		try {
			// Start Position
			int startPos = bb.position();
			log.info("decode", "Start Position: " + startPos);

			str = Tool.getString(bb);

			// End Position
			int endPos = bb.position();
			log.info("decode", "End Position: " + endPos);

			return endPos - startPos;
		} catch (BufferUnderflowException e) {
			throw new DecodeException("Incorrect element length '" + msgLen + "' for "
					+ getElemName(), e);
		} catch (Exception e) {
			throw new DecodeException("Decoding '" + getElemName() + "' Error.", e);
		}
	}

	@Override
	public int encode(ByteBuffer bb) throws EncodeException {
		try {
			// Element Header
			int elemHeaderLen = encodeElementHeader(bb, 0);
			log.info("encode", "Element Header Length: " + elemHeaderLen);

			// Start Position
			int startPos = bb.position();
			log.info("encode", "Start Position: " + startPos);
			Tool.putString(bb, str);

			// End Position
			int endPos = bb.position();
			log.info("encode", "End Position: " + endPos);

			// Element Length
			int elemBodyLen = endPos - startPos;
			log.info("encode", "Element Length: " + elemBodyLen);

			// Fill pending element length.
			fillPendingElementLength(bb, startPos, elemBodyLen);

			return elemHeaderLen + elemBodyLen;
		} catch (Exception e) {
			throw new EncodeException("Encoding '" + getElemName() + "' Error.", e);
		}
	}

	@Override
	public String getElemName() {
		return "Api String";
	}

	@Override
	public short getElemType() {
		return API_STRING;
	}

}
