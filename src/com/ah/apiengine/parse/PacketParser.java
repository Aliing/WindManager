package com.ah.apiengine.parse;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.ah.apiengine.DecodeException;
import com.ah.apiengine.Element;
import com.ah.apiengine.Header;
import com.ah.apiengine.PacketParsable;
import com.ah.apiengine.Request;
//import com.ah.apiengine.Response;
import com.ah.apiengine.element.Elements;
import com.ah.apiengine.request.IllegalRequest;
import com.ah.apiengine.request.Requests;

import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

public class PacketParser implements PacketParsable {

	private static final Tracer	log = new Tracer(PacketParser.class.getSimpleName());

	/* Version */
	private short version;

	/* Sequence Number */
	private short seqNum;

	/* Time Zone */
	private String timezone;

	/* Time Stamp */
	private int timestamp;

	/* Message Type */
	private int msgType;

	private HttpServletRequest httpServletRequest;

	public PacketParser() {
		super();
	}

	public PacketParser(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}

	/*-
	 * API Client/Engine Message Header Format.
	 *
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |            Version            |        Sequence Number        |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * | Time Zone Len |                 Time Zone ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                           Time Stamp                          |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *
     * Length > 9
	 */
	protected void decodeHeader(ByteBuffer bb) throws DecodeException {
		String elemName = "'Header'";
		int headerLen = 15;

		if (bb.limit() < headerLen) {
			throw new DecodeException("Incorrect length [" + bb.limit() + "] for " + elemName + ", it should be no less than " + headerLen);
		}

		try {
			/* Version */
			version = bb.getShort();
			log.debug("decodeHeader", "Version: " + version);

			/* Sequence Number */
			seqNum = bb.getShort();
			log.debug("decodeHeader", "Sequence Number: " + seqNum);

			byte timezoneLen = bb.get();

			/* Time Zone */
			timezone = AhDecoder.bytes2String(bb, timezoneLen);
			log.debug("decodeHeader", "Time Zone: " + timezone);

			/* Time Stamp */
			timestamp = bb.getInt();
			log.debug("decodeHeader", "Time Stamp: " + timestamp);
		} catch (Exception e) {
			throw new DecodeException("Incorrect " + elemName, e);
		}
	}

	/*-
	 * API Client/Engine Control Message Format.
	 *
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                       Message Type                            |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |     Message Element Length    |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *
     * Length = 6
	 */
	protected void decodeControlMessage(ByteBuffer bb) throws DecodeException {
		// Decode Header.
		decodeHeader(bb);

		String elemName = "'Control Message'";
		int msgElemLen;

		try {
			/* Message Type */
			msgType = bb.getInt();
			log.debug("decodeControlMessage", "Message Type: " + msgType);

			/* Message Element Length */
			msgElemLen = bb.getShort();
			log.debug("decodeControlMessage", "Message Element Length: " + msgElemLen);
		} catch (Exception e) {
			throw new DecodeException("Incorrect " + elemName, e);
		}

		int remaining = bb.remaining();

		if (remaining != msgElemLen) {
			throw new DecodeException("Given element length [" + msgElemLen + "] and actual element length [" + remaining + "] are mismatch." );
		}
	}

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
     *
     * Length > 4
	 */
	@Override
	public Request parseRequest(ByteBuffer reqBB) throws DecodeException {
		// Decode Control Message.
		decodeControlMessage(reqBB);

		Request request = Requests.getInstance(msgType);

		if (request == null) {
			String errMsg = "Unknown request with message type '" + msgType + "'.";
			request = new IllegalRequest(errMsg);
		} else {
			Collection<Element> elements = parseElements(reqBB);

			// Set Header Messages.
			setHeaderMsgs(request);

			// Set Elements.
			request.setElements(elements);
		}

		log.warning("parseRequest", "Received a " + request.getMsgName());

		// Set HttpServletRequest.
		request.setHttpServletRequest(httpServletRequest);

		return request;
	}

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
     *
     * Length > 4
	 */
//	@Override
//	public Response<? extends Request> parseResponse(ByteBuffer respBB) throws DecodeException {
//		// Decode Control Message.
//		decodeControlMessage(respBB);
//
//		Response<? extends Request> response = Responses.getInstance(msgType);
//
//		if (response == null) {
//			return null;
//		}
//
//		log.info("parseResponse", "Received a " + response.getMsgName());
//
//		Collection<Element> elements = parseElements(respBB);
//
//		// Set Header Messages.
//		setHeaderMsgs(response);
//
//		// Set Elements.
//		response.setElements(elements);
//
//		return response;
//	}

	protected Collection<Element> parseElements(ByteBuffer bb) throws DecodeException {
		Collection<Element> elements = new ArrayList<Element>();

		while (bb.hasRemaining()) {
			short type;
			short len;

			// Decode type and length for each element.
			try {
				type = bb.getShort();
				len = bb.getShort();
			} catch (Exception e) {
				throw new DecodeException("TVL Structure Error.", e);
			}

			Element e = Elements.getInstance(type);
			int remaining = bb.remaining();

			if (len > remaining) {
				throw new DecodeException("Buffer overflow while decoding element with type '" + e.getElemType() + "'. Buffer Remaining Size: " + remaining + "; Given Element Length: " + len);
			}

			if (e != null) {
				// Decode Element.
				log.info("parseElements", "Decoding '" + e.getElemName() + "' element.");
				int decodeLen = e.decode(bb, len);

				if (decodeLen > len) {
					throw new DecodeException("The actual element length '" + decodeLen + "' is more than the given length '" + len + "'. Element Type: " + e.getElemType());
				}

				// Skip the exceeded element component.
				if (len > decodeLen) {
					int skipLen = len - decodeLen;
					int currPos = bb.position();
					log.warning("parseElements", "Skipping number of " + skipLen + " bytes from position '" + currPos + "' to ingore decoding the unknown components. Element Type: " + e.getElemType() + "; Given Element length: " + len + "; Decoded Length: " + decodeLen);
					bb.position(currPos + skipLen);
				}

				elements.add(e);
			} else {
				log.warning("parseElements", "Unknown element with type '" + type + "'.");
				int currPos = bb.position();
				log.warning("parseElements", "Skipping number of " + len + " bytes from position '" + currPos + "' to ignore decoding the unknown element with type '" + type + "'.");
				bb.position(currPos + len);
			}
		}

		return elements;
	}

	private void setHeaderMsgs(Header header) {
		header.setVersion(version);
		header.setSeqNum(seqNum);
		header.setTimezone(timezone);
		header.setTimestamp(timestamp);
	}

}
