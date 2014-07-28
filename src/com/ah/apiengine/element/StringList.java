package com.ah.apiengine.element;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import com.ah.apiengine.AbstractElement;
import com.ah.apiengine.DecodeException;
import com.ah.apiengine.EncodeException;
import com.ah.util.Tracer;

public class StringList extends AbstractElement {

	private static final long	serialVersionUID	= 1L;

	private static final Tracer	log					= new Tracer(StringList.class);

	private Collection<String>	strs;

	public Collection<String> getStrs() {
		return strs;
	}

	public void setStrs(Collection<String> strs) {
		this.strs = strs;
	}

	@Override
	public int decode(ByteBuffer bb, int len) throws DecodeException {

		try {
			// Start Position
			int startPos = bb.position();

			short strCount = bb.getShort();
			strs = new ArrayList<String>(strCount);

			for (int i = 0; i < strCount; i++) {
				/* HHM Super User Password */
				strs.add(Tool.getString(bb));
			}

			// End Position
			int endPos = bb.position();
			log.debug("decode", "End Position: " + endPos);

			return endPos - startPos;
		} catch (BufferUnderflowException e) {
			throw new DecodeException(
					"Incorrect element length '" + len + "' for " + getElemName(), e);
		} catch (Exception e) {
			throw new DecodeException("Decoding '" + getElemName() + "' Error.", e);
		}

	}

	@Override
	public int encode(ByteBuffer bb) throws EncodeException {

		try {
			// Element Header
			int elemHeaderLen = encodeElementHeader(bb, 0);

			// Start Position
			int startPos = bb.position();

			/* Number of HHMs */
			int hhmNum = strs.size();
			bb.putShort((short) hhmNum);

			for (String str : strs) {
				Tool.putString(bb, str);
			}

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

	@Override
	public String getElemName() {
		return "String List";
	}

	@Override
	public short getElemType() {
		return STRING_LIST;
	}

}
