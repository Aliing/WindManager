package com.ah.apiengine.element;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ah.apiengine.AbstractElement;
import com.ah.apiengine.DecodeException;
import com.ah.apiengine.EncodeException;
import com.ah.util.coder.AhDecoder;

public class CommandLine extends AbstractElement {

	private static final long	serialVersionUID	= 1L;
	
	private final static Log	log	= LogFactory.getLog("tracerlog.Command");

	private String	string;

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	@Override
	public int decode(ByteBuffer bb, int msgLen) throws DecodeException {
		try {
			// Start Position
			int startPos = bb.position();
			log.debug("decode: Start Position: " + startPos);

			short len = bb.getShort();
			string = AhDecoder.bytes2String(bb, len);

			// End Position
			int endPos = bb.position();
			log.debug("decode: End Position: " + endPos);

			return endPos - startPos;
		} catch (BufferUnderflowException e) {
			throw new DecodeException(
					"Incorrect element length '" + msgLen + "' for " + getElemName(), e);
		} catch (Exception e) {
			throw new DecodeException("Decoding '" + getElemName() + "' Error.", e);
		}
	}

	@Override
	public int encode(ByteBuffer bb) throws EncodeException {
		try {
			// Element Header
			int elemHeaderLen = encodeElementHeader(bb, 0);
			log.debug("encode: Element Header Length: " + elemHeaderLen);

			// Start Position
			int startPos = bb.position();
			log.debug("encode: Start Position: " + startPos);

			/* string */
			byte[] tmp = string.getBytes("iso-8859-1");
			int len = tmp.length;
			bb.putShort((short) len);
			bb.put(tmp);

			// End Position
			int endPos = bb.position();
			log.debug("encode: End Position: " + endPos);

			// Element Length
			int elemBodyLen = endPos - startPos;
			log.debug("encode: Element Length: " + elemBodyLen);

			// Fill pending element length.
			fillPendingElementLength(bb, startPos, elemBodyLen);

			return elemHeaderLen + elemBodyLen;
		} catch (Exception e) {
			throw new EncodeException("Encoding '" + getElemName() + "' Error.", e);
		}
	}

	@Override
	public String getElemName() {
		return "CommandLine";
	}

	@Override
	public short getElemType() {
		return COMMAND_LINE;
	}

}
