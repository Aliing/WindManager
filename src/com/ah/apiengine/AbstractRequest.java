package com.ah.apiengine;

import java.nio.ByteBuffer;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ah.util.Tracer;

public abstract class AbstractRequest implements Request {

	private static final long serialVersionUID = 1L;

	private static final Tracer	log = new Tracer(AbstractRequest.class.getSimpleName());

	/* Version */
	protected short version = 1;

	/* Sequence Number */
	protected short seqNum;

	/* Time Zone */
	protected String timezone;

	/* Time Stamp */
	protected int timestamp;

	protected HttpServletRequest httpServletRequest;

	@Override
	public short getVersion() {
		return version;
	}

	@Override
	public void setVersion(short version) {
		this.version = version;
	}

	@Override
	public short getSeqNum() {
		return seqNum;
	}

	@Override
	public void setSeqNum(short seqNum) {
		this.seqNum = seqNum;
	}

	@Override
	public String getTimezone() {
		return timezone;
	}

	@Override
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	@Override
	public int getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	@Override
	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}

	/*
	 * Except for login, logout and heart beat, other requests should invoke this method to check the validity before executing request.
	 */
	public boolean checkValidity() {
		boolean isValid = true;

		if (getMsgType() != LOGIN_REQUEST && getMsgType() != LOGOUT_REQUEST && getMsgType() != HEART_BEAT_REQUEST) {
			if (httpServletRequest != null) {
				HttpSession session = httpServletRequest.getSession(false);
				isValid = session != null;
			} else {
				// This should never occur.
				log.warning("checkValidity", "HttpServeletRequest instance doesn't exist.");
				isValid = false;
			}
		}

		return isValid;
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
	protected int encodeHeader(ByteBuffer bb) throws EncodeException {
		String elemName = "'Header'";

		try {
			// Start Position
			int startPos = bb.position();
			log.debug("encodeHeader", "Start Position: " + startPos);

			/* Version */
			log.debug("encodeHeader", "Version: " + version);
			bb.putShort(version);

			/* Sequence Number */
			log.debug("encodeHeader", "Sequence Number: " + seqNum);
			bb.putShort(seqNum);

			String timezone = TimeZone.getDefault().getID();
			byte[] b_tz = timezone.getBytes("iso-8859-1");
			int timezoneLen = b_tz.length;
			bb.put((byte) timezoneLen);

			/* Time Zone */
			log.debug("encodeHeader", "Time Zone: " + timezone);
			bb.put(b_tz);

			/* Time Stamp */
			int timestamp = new Long((System.currentTimeMillis() / 1000)).intValue();
			log.debug("encodeHeader", "Time Stamp: " + timestamp);
			bb.putInt(timestamp);

			// End Position
			int endPos = bb.position();
			log.debug("encodeHeader", "End Position: " + endPos);

			// Encode Control Message.
			return endPos - startPos + encodeControlMessage(bb);
		} catch (Exception e) {
			throw new EncodeException("Encoding " + elemName + " Error.", e);
		}
	}

	/*
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
     * Length = 6;
	 */
	private int encodeControlMessage(ByteBuffer bb) throws EncodeException {
		String elemName = "'Control Message'";

		try {
			// Start Position
			int startPos = bb.position();
			log.debug("encodeControlMessage", "Start Position: " + startPos);

			/* Message Type */
			int msgType = getMsgType();
			log.debug("encodeControlMessage", "Message Type: " + msgType);
			bb.putInt(msgType);

			// This value will be overwritten by the invocation of <tt>fillPendingElementLength</tt> in the subclasses inherited from this class.
			bb.putShort((short) 0);

			// End Position
			int endPos = bb.position();
			log.debug("encodeControlMessage", "End Position: " + endPos);

			return endPos - startPos;
		} catch (Exception e) {
			throw new EncodeException("Encoding " + elemName + " Error.");
		}
	}

	protected void fillPendingElementsLength(ByteBuffer bb, int headerLen, int totalElemsLen) {
		log.debug("fillPendingElementsLength", "Total Elements Length: " + totalElemsLen);
		bb.putShort(headerLen - 2, (short) totalElemsLen);
	}

}