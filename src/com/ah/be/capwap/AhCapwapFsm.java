/**
 *@filename		AhCapwapFsm.java
 *@version
 *@author		Long
 *@createtime	2007-8-4 11:42:08 AM.
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.capwap;

// java import
import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

// aerohive import
import com.ah.be.capwap.event.*;
import com.ah.be.capwap.event.impl.AhCapwapEventMgmtImpl;
import com.ah.be.capwap.event.request.client.*;
import com.ah.be.capwap.event.request.client.AhCapwapClientRequest.RequestType;
import com.ah.be.capwap.event.request.server.*;
import com.ah.be.db.discovery.AhCapwapDiscovery;
import com.ah.util.Tracer;
import com.ah.util.coder.AhEncoder;

/**
 * @author Long
 * @version V1.0.0.0
 */
public class AhCapwapFsm extends AhWtpAttributes implements AhCapwapConstants {

	private static final long	serialVersionUID	= 1L;
	private static final Tracer logger = new Tracer(AhCapwapFsm.class.getSimpleName());

	/*
	 * WaitJoin : draft ietf section 4.5.11. The maximum time, in seconds, a WTP
	 * MUST wait without having received a DTLS Handshake message from an AC.
	 * This timer must be greater than 30 seconds. Default: 60 Range : more than
	 * 30
	 */
	private static final int TIMER_WAIT_FOR_JOIN = 60;

	/**
	 * EchoInterval : draft ietf section 4.5.11. The minimum time, in seconds,
	 * between sending echo requests to the AC with which the WTP has joined.
	 * Default: 30
	 */
	private static final int TIMER_WAIT_FOR_ECHO = 105;

	private static final int TIMER_WAIT_FOR_WTP_EVENT = TIMER_WAIT_FOR_ECHO;

	private FsmState				fsmState;
	private int						seqNum;
	private int						timer;
	private final int				BUFFER_SIZE = 1024;
	private final Map<Integer, AhCapwapServerRequest> serverReqs;
	private final Map<Integer, AhWtpEvent> wtpEvents;
	private BlockingQueue<AhCapwapEvent> clientReqQue;
	private SocketAddress clientSocket;
	private AhUdpHandler udpHandler;

	private AhCapwapFsm() {
		initFsm();
		serverReqs = Collections.synchronizedMap(new HashMap<Integer, AhCapwapServerRequest>());
		wtpEvents = Collections.synchronizedMap(new HashMap<Integer, AhWtpEvent>());
	}

	protected AhCapwapFsm(BlockingQueue<AhCapwapEvent> clientReqQue, AhUdpHandler udpHandler) {
		this();
		this.clientReqQue = clientReqQue;
		this.udpHandler = udpHandler;
	}

	public void initFsm() {
		timer = 0;
		fsmState = FsmState.RESET;
	}

	public FsmState getFsmState() {
		return fsmState;
	}

	private synchronized int getNewSeqNum() {
		if (++seqNum >= 256) {
			seqNum = 0;
		}

		return seqNum;
	}

	private void startTimer(int timer) {
		this.timer = timer * 1000;// It is a millisecond unit.
	}

	protected int getRemainTimer() {
		return timer;
	}

	public void clearTimer() {
		timer = 0;
	}

	protected void decreaseTimer(int decValue) {
		timer -= decValue;

		// Decrease timer to judge whether each AC Request Event is timed out or not.
		decRequestTimer(decValue);

		// Decrease timer to judge whether each WTP Event is timed out.
		decWtpEventTimer(decValue);

		try {
			AhCapwapDiscovery.decreaseFlushTimer();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Decrease timer for all requests to judge whether each request is timed out.
	 * <p>
	 * @param decValue  Decrease value to be reduced.
	 */
	private void decRequestTimer(int decValue) {
		synchronized (serverReqs) {
			AhCapwapServerRequest request;

			for (Iterator<AhCapwapServerRequest> iter = serverReqs.values().iterator(); iter.hasNext();) {
				request = iter.next();

				// Whether a retransmit interval has been past.
				if (request.decreaseTimer(decValue)) {
					if (request.isTimeout()) {
						// Remove AC Request when the last retransmit is past.
						logger.debug("decRequestTimer", "["+ip+"]Server request is timed out, remove " + request.getRequestName()
								+ ", retransmit count = " + request.getTranCount()
								+ ", retransmit interval = " + request.getRetranInterval());
						iter.remove();
						request.setReqRet(AhCapwapServerRequest.AH_CAPWAP_REQUEST_TIMEOUT);
					//	request.setResponse("Could not receive any response from HiveAp for 3 requests.");
					} else {
						// Retransmit request if the corresponding response is not received during a retransmit interval.
						logger.debug("decRequestTimer", "["+ip+"]Retransmit " + request.getRequestName());
						sendRequest(request);
					}
				}
			}
		}
	}

	/**
	 * Decrease timer for all WTP events to judge whether each WTP event is timed out.
	 * <p>
	 * @param decValue  Decrease value to be reduced.
	 */
	private void decWtpEventTimer(int decValue) {
		synchronized (wtpEvents) {
			AhWtpEvent event;

			for (Iterator<AhWtpEvent> iter = wtpEvents.values().iterator(); iter.hasNext();) {
				event = iter.next();

				if (event.decreaseTimer(decValue) <= 0) {
					// Remove WTP event once the timer is exhausted.
					logger.debug("decWtpEventTimer", "["+ip+"]Timer is timed out, remove " + getWtpEventName(event.getType()));
					iter.remove();
				}
			}
		}
	}

	/**
	 * Put bytes into ByteBuffer.
	 * <p>
	 * @param buf  ByteBuffer which is used to save bytes.
	 * @param value  int value to be converted into bytes.
	 * @param byteCount  The number of byte to be put into ByteBuffer.
	 */
	private void putBytes(ByteBuffer buf, int value, int byteCount) {
		byte[] b_array = AhEncoder.int2bytes(value);

		for (int i = byteCount; i > 0; i--) {
			buf.put(b_array[4 - i]);
		}
	}

	private byte[] getBytesByEncoding(String s) {
		try {
			return s.getBytes("iso-8859-1");
		} catch (UnsupportedEncodingException uee) {
			return s.getBytes();
		}
	}

	/**
	 * Fill capwap response header fill response header All CAPWAP protocol
	 * messages are encapsulated using a common header format, regardless of the
	 * CAPWAP control or CAPWAP Data transport used to carry the messages.
	 * However, certain flags are not applicable for a given transport. Refer to
	 * the specific transport section in order to determine which flags are
	 * valid. Note that the optional fields defined in this section MUST be
	 * present in the precise order shown below.
	 * <p>
	 * 0 1 2 3 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Version| RID | HLEN | WBID |T|F|L|W|M| Flags |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Fragment ID | Frag Offset |Rsvd |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * (optional) Radio MAC Address |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * (optional) Wireless Specific Information |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
	 * Payload .... |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * <p>
	 * @param buf	Capwap response packet.
	 * @return  Capwap response header.
	 * @throws AhCapwapEncodeException  If error occurs in encoding capwap option message.
	 */
	private int buildCapwapRespHdr(ByteBuffer buf) throws AhCapwapEncodeException {
		final int version_size = 4;
		final int rid_size = 5;
		final int hlen_size = 5;
		final int wbid_size = 5;
		final int t_size = 1;
		final int f_size = 1;
		final int l_size = 1;
		final int w_size = 1;
		final int m_size = 1;
		final int flags_size = 8;
		final int fragment_id_size = 16;
		final int frag_offset_size = 13;
		final int rsvd_size = 3;

		try {
			// Version|RID|HLEN|WBID|T|F|L|W|M|Flags
			int offset = 32 - version_size;
			int version = 4;
			version = (version << offset) & 0xf0000000;
			offset -= rid_size;
			int rid = 0;
			rid = (rid << offset) & 0xf800000;
			offset -= hlen_size;
			int hlen = 8;
			hlen = (hlen << offset) & 0x7c0000;
			offset -= wbid_size;
			int wbid = 0;
			wbid = (wbid << offset) & 0x3e000;
			offset -= t_size;
			int t = 0;
			t = (t << offset) & 0x1000;
			offset -= f_size;
			int f = 0;
			f = (f << offset) & 0x800;
			offset -= l_size;
			int l = 0;
			l = (l << offset) & 0x400;
			offset -= w_size;
			int w = 0;
			w = (w << offset) & 0x200;
			offset -= m_size;
			int m = 0;
			m = (m << offset) & 0x100;
			offset -= flags_size;
			int flags = 0;
			flags = (flags << offset) & 0xff;
			buf.putInt(version + rid + hlen + wbid + t + f + l + w + m + flags);

			// Fragment ID|Frag Offset|Rsvd
			offset = 32 - fragment_id_size;
			int fragment_id = 0;
			fragment_id = (fragment_id << offset) & 0xffff0000;
			offset -= frag_offset_size;
			int frag_offset = 0;
			frag_offset = (frag_offset << offset) & 0xfff8;
			offset -= rsvd_size;
			int rsvd = 0;
			rsvd = (rsvd << offset) & 0x07;
			buf.putInt(fragment_id + frag_offset + rsvd);
		} catch (Exception e) {
			throw new AhCapwapEncodeException(
					"["+ip+"]Build Capwap Response Header Error : " + e.getMessage(), e);
		}

		return 8;
	}

	/**
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                       Message Type                            |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |    Seq Num    |        Msg Element Length     |     Flags     |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                           Time Stamp                          |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * | Msg Element [0..N] ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+
	 * <p>
	 * @param buf  Capwap response packet.
	 * @param type  Message type.
	 * @param seqNum  A sequence number which indicates a specific packet sent from WTP to AC.
	 * @throws AhCapwapEncodeException  If error occurs in encoding capwap option message.
	 */
	private void buildCapwapCntlMsgHdr(ByteBuffer buf, int type, int seqNum) throws AhCapwapEncodeException {
		try {
			buf.putInt(type);
			putBytes(buf, seqNum, 1);
			putBytes(buf, 7, 2);
			int flags = 0;
			putBytes(buf, flags, 1);
			buf.putInt(new Long(System.currentTimeMillis() / 1000).intValue());
		} catch (Exception e) {
			throw new AhCapwapEncodeException(
					"["+ip+"]Build Capwap Response Control Message Header Error : " + e.getMessage(), e);
		}
	}

	/**
	 * Fill element length.
	 * <p>
	 * @param buf  Capwap response packet.
	 * @param headerLen  The length of capwap header.
	 * @param tlv_len  The length of TLV structure.
	 * @throws AhCapwapEncodeException  If error occurs in encoding capwap option message.
	 */
	private void fillElementLength(ByteBuffer buf, int headerLen, int tlv_len) throws AhCapwapEncodeException {
		try {
			byte[] b_array = AhEncoder.int2bytes(tlv_len + 7);
			buf.put(headerLen + 5, b_array[2]);
			buf.put(headerLen + 6, b_array[3]);
		} catch (Exception e) {
			throw new AhCapwapEncodeException(
					"["+ip+"]Build Capwap Response TLV Structure Error : " + e.getMessage(), e);
		}
	}

	/**
	 * Fill msg type and length for each capwap element.
	 * <p>
	 * @param buf  Capwap response packet.
	 * @param msgType  Message type.
	 * @param len  Message length.
	 * @return  Type and length of structure.
	 * @throws AhCapwapEncodeException  If error occurs in encoding capwap option message.
	 */
	private int fillMsgTypeAndLength(ByteBuffer buf, int msgType, int len) throws AhCapwapEncodeException {
		try {
			// Type Length : 4 bytes for capwap draft 2, but 2 for capwap draft3
			buf.putInt(msgType);
			// Message Length : 2 bytes.
			putBytes(buf, len, 2);
		} catch (Exception e) {
			throw new AhCapwapEncodeException(
					"["+ip+"]Fill Msg Type and Length for Capwap Response Error : " + e.getMessage(), e);
		}

		return 6;
	}

	/**
	 * AC Descriptor.
	 * <p>
	 * @param buf  Capwap response packet.
	 * @return  AC Descriptor packet.
	 * @throws AhCapwapEncodeException  If error occurs in encoding capwap option message.
	 */
	private int fillACDescriptor(ByteBuffer buf) throws AhCapwapEncodeException {
		String hardware_version = "1.0.0.0";
		String software_version = "1.0.0.0";
		byte[] b_hardware = getBytesByEncoding(hardware_version);
		byte[] b_software = getBytesByEncoding(software_version);
		int length = 28 + b_hardware.length + b_software.length;

		// Msg type and length.
		int len = length + fillMsgTypeAndLength(buf, AC_DESCRIPTOR, length);

		try {
			int stations = 0;
			putBytes(buf, stations, 2);
			int limit = 0;
			putBytes(buf, limit, 2);
			int active_wtps = 0;
			putBytes(buf, active_wtps, 2);
			int max_wtps = 0;
			putBytes(buf, max_wtps, 2);
			int security = 0;
			buf.put((byte) security);
			int r_mac_field = 0;
			buf.put((byte) r_mac_field);
			int wireless_field = 0;
			buf.put((byte) wireless_field);
			int reserved = 0;
			buf.put((byte) reserved);

			byte[] b_array;
			int vendor_identifier;
			int type;

			for (int i = 0; i < 2; i++) {
				vendor_identifier = IANA;
				buf.putInt(vendor_identifier);

				switch (i) {
					case 0:
						// Hardware Version
						type = 4;
						b_array = b_hardware;
						break;
					case 1:
						// Software Version
						type = 5;
						b_array = b_software;
						break;
					default:
						type = 4;
						b_array = b_software;
						break;
				}

				putBytes(buf, type, 2);
				putBytes(buf, b_array.length, 2);
				buf.put(b_array);
			}
		} catch (Exception e) {
			throw new AhCapwapEncodeException(
					"["+ip+"]Build AC Descriptor Element Error : " + e.getMessage(), e);
		}

		return len;
	}

	/**
	 * AC Name.
	 * <p>
	 * @param buf  Capwap response packet.
	 * @return  AC Name packet.
	 * @throws AhCapwapEncodeException  If error occurs in encoding capwap option message.
	 */
	private int fillACName(ByteBuffer buf) throws AhCapwapEncodeException {
		byte[] b_name = getBytesByEncoding(AH_AC_NAME);
		int length = b_name.length;

		// Msg type and length.
		int len = length + fillMsgTypeAndLength(buf, AC_NAME, length);

		try {
			buf.put(b_name);
		} catch (Exception e) {
			throw new AhCapwapEncodeException("["+ip+"]Build AC Name Element Error : " + e.getMessage(), e);
		}

		return len;
	}

	/**
	 * CAPWAP Control IPv4 Address.
	 * <p>
	 * @param buf  Capwap response packet.
	 * @return  IPv4 Address packet.
	 * @throws AhCapwapEncodeException  If error occurs in encoding capwap option message.
	 */
	private int fillIPv4(ByteBuffer buf) throws AhCapwapEncodeException {
		String s_ipv4 = "127.0.0.1";
		int i_ipv4 = new Long(AhEncoder.ip2Long(s_ipv4)).intValue();
		int wtp_count = 0;

		// Msg type and length.
		int len = 6 + fillMsgTypeAndLength(buf, AC_IPV4, 6);

		try {
			buf.putInt(i_ipv4);
			putBytes(buf, wtp_count, 2);
		} catch (Exception e) {
			throw new AhCapwapEncodeException(
					"["+ip+"]Build AC IPv4 Address Element Error : " + e.getMessage(), e);
		}

		return len;
	}

	/**
	 * CAPWAP Control IPv6 Address.
	 * <p>
	 * @param buf  Capwap response packet.
	 * @return  IPv6 Address packet.
	 * @throws AhCapwapEncodeException	if error occurs in encoding capwap packet.
	 */
	private int fillIPv6(ByteBuffer buf) throws AhCapwapEncodeException {
		int i_ipv6_1 = 0;
		int i_ipv6_2 = 0;
		int i_ipv6_3 = 0;
		int i_ipv6_4 = 0;
		int wtp_count = 0;

		// Msg type and length.
		int len = 18 + fillMsgTypeAndLength(buf, AC_IPV6, 18);

		try {
			buf.putInt(i_ipv6_1);
			buf.putInt(i_ipv6_2);
			buf.putInt(i_ipv6_3);
			buf.putInt(i_ipv6_4);
			putBytes(buf, wtp_count, 2);
		} catch (Exception e) {
			throw new AhCapwapEncodeException(
					"["+ip+"]Build AC IPv6 Address Element Error : " + e.getMessage(), e);
		}

		return len;
	}

	/**
	 * WTP Event Control.
	 * <p>
     * 0 1 2 3 4 5 6 7 8
     * +-+-+-+-+-+-+-+-+
     * |    Enabling   |
     * +-+-+-+-+-+-+-+-+
	 * <p>
	 * Enabling : 0 - Enable.
	 *            1 - Disable.
	 * <p>
	 * @param buf  Capwap response packet.
	 * @param request  This object consist of all the necessary messages used for option.
	 * @return  The size of WTP Event Control.
	 * @throws AhCapwapEncodeException  If error occurs in encoding capwap option message.
	 */
	private int fillWtpEventControl(ByteBuffer buf, AhWtpEventControlRequest request) throws AhCapwapEncodeException {
		int reqPacketLen = request.getReqPacket().length;
		int len = reqPacketLen + fillMsgTypeAndLength(buf, WTP_EVENT_CONTROL, reqPacketLen);

		try {
			buf.put(request.getReqPacket());
		} catch (Exception e) {
			throw new AhCapwapEncodeException("["+ip+"]Build WTP Event Control Element Error : " + e.getMessage(), e);
		}

		return len;
	}

	/**
	 * WTP File Download.
	 * <p>
     * 0 1 2 3 4 5 6 7 8
     * +-+-+-+-+-+-+-+-+
     * |    Clis...
     * +-+-+-+-+-+-+-+-+
	 * <p>
	 * @param buf  Capwap response packet.
	 * @param request  This object consist of all the necessary messages used for option.
	 * @return  The size of the option.
	 * @throws AhCapwapEncodeException  If error occurs in encoding capwap option message.
	 */
	private int fillFileDownload(ByteBuffer buf, AhFileDownloadRequest request) throws AhCapwapEncodeException {
		int reqPacketLen = request.getReqPacket().length;
		int len = reqPacketLen + fillMsgTypeAndLength(buf, FILE_DOWNLOAD, reqPacketLen);
		
		try {
			buf.put(request.getReqPacket());
		} catch (Exception e) {
			throw new AhCapwapEncodeException("["+ip+"]Build WTP Event Control Element Error : " + e.getMessage(), e);
		}

		return len;
	}

	private String getRequestName(int msgType) {
		String reqName;

		switch (msgType) {
			case WTP_EVENT_CONTROL_REQUEST:
				reqName = "WTP Event Control Request";
				break;
			case IDP_QUERY_REQUEST:
				reqName = "IDP Query Request";
				break;
			case LAYER3_ROAMING_CONFIG_REQUEST:
				reqName = "Layer3 Roaming Config Request";
				break;
			case WTP_FILE_DOWNLOAD_REQUEST:
				reqName = "File Download Request";
				break;
			default:
				reqName = "Unknown Request";
				break;
		}

		return reqName;
	}

	/**
	 * Add different kinds of capwap request into a map reqeust type to request.
	 * Add then send them rely on the request type.
	 * <p>
	 * @param reqType  The type of request.
	 * @param request  This is made up of request type and request messages to be sent.
	 */
	private void addRequest(int reqType, AhCapwapServerRequest request) {
		logger.debug("addRequest", "["+ip+"]Capwap request size = " + serverReqs.size());
		
		synchronized (serverReqs) {
			if (serverReqs.containsKey(reqType)) {
				logger.error("addRequest", "["+ip+"]The " + getRequestName(reqType) + " is exist, discard the new request");
			} else {
				logger.debug("addRequest", "["+ip+"]Add " + request.getRequestName());
				request.startTimer(); // Start timer for retransmiting if no response is received during timeout interval.
				serverReqs.put(reqType, request);
			}
		}
	}

	/**
	 * Send different kinds of capwap request.
	 * <p>
	 * @param request  This is made up of request type and request messages to be sent.
	 * @return  The sequence number of request sent to WTP.
	 */
	public int sendRequest(AhCapwapServerRequest request) {
		int seqNum = -1;
		int reqType = request.getType();

		switch (reqType) {
			case WTP_EVENT_CONTROL_REQUEST:
				logger.debug("sendRequest", "["+ip+"]Send WTP Event Control Request.");
				seqNum = sendWtpEventCntlReq((AhWtpEventControlRequest)request);
				break;
			case IDP_QUERY_REQUEST:
				logger.debug("sendRequest", "["+ip+"]Send IDP Query Request.");
				seqNum = sendIdpQueryReq();
				break;
			case LAYER3_ROAMING_CONFIG_REQUEST:
				logger.debug("sendRequest", "["+ip+"]Send Layer3 Roaming Config Request.");
			//	seqNum = sendL3RoamConfigReq((AhLayer3RoamingConfigRequest)request);
				break;
			case WTP_FILE_DOWNLOAD_REQUEST:
				logger.debug("sendRequest", "["+ip+"]Send File Download Request.");
				seqNum = sendFileDownloadReq((AhFileDownloadRequest)request);
				break;
			default:
				logger.error("sendRequest", "["+ip+"]Unknown Server Request, type = " + reqType);
				break;
		}

		if (seqNum >= 0) {
			addRequest(reqType, request);
		}

		return seqNum;
	}

	/**
	 * Process response corresponding a capwap request sent from AC with the same request type specified as argument.
	 * <p>
	 * @param reqType  The request type used for AC.
	 */
	protected void procWtpResp(int reqType) {
		logger.debug("procWtpResp", "["+ip+"]Server request size = " + serverReqs.size());
		String respName = getRequestName(reqType);
		AhCapwapServerRequest request = null;

		synchronized (serverReqs) {
			if (serverReqs.containsKey(reqType)) {
				// Remove request after receiving the corresponding response sent from WTP.
				logger.debug("procWtpResp", "["+ip+"]Remove " + respName);
				request = serverReqs.remove(reqType);
			} else {
				logger.debug("procWtpResp", "["+ip+"]Received unexpected client response corresponds to " + respName);
			}
		}

		if (request != null) {
			switch (reqType) {
				case WTP_EVENT_CONTROL_REQUEST:
				case IDP_QUERY_REQUEST:
				case LAYER3_ROAMING_CONFIG_REQUEST:
					request.setReqRet(AhCapwapServerRequest.AH_CAPWAP_REQUEST_SUCCESS);
					break;
				default:
					logger.error("procWtpResp", "["+ip+"]Unknown Server Request, type = " + reqType);
					break;
			}

			AhCapwapEventMgmtImpl.getInstance().notify(request);
		}
	}

	/**
	 * Send Discovery Response.
	 * <p>
	 * @param seqNum  A sequence number which indicates a specific packet sent from WTP to AC.
	 * @param request Capwap client request.
	 * @return  true if sending discovery response successfully, false otherwise.
	 */
	private boolean sendDiscoveryResp(int seqNum, AhCapwapClientRequest request) {
		/**
		 * Discovery Response Message : see 5.2 of
		 * draft-ietf-capwap-protocol-specification-03.txt o AC Descriptor, see
		 * Section 4.4.1 o AC Name, see Section 4.4.4 o CAPWAP Control IPv4
		 * Address, see Section 4.4.10 o CAPWAP Control IPv6 Address, see
		 * Section 4.4.11
		 */
		boolean ret = false;
		ByteBuffer resp_buf = ByteBuffer.allocate(BUFFER_SIZE);

		try {
			int resp_header_len = buildCapwapRespHdr(resp_buf);
			buildCapwapCntlMsgHdr(resp_buf, DISCOVERY_RESPONSE, seqNum);
			int ac_desc_len = fillACDescriptor(resp_buf);
			int ac_name_len = fillACName(resp_buf);
			int ac_ipv4_len = fillIPv4(resp_buf);
			int ac_ipv6_len = fillIPv6(resp_buf);
			fillElementLength(resp_buf, resp_header_len, ac_desc_len + ac_name_len + ac_ipv4_len + ac_ipv6_len);
			resp_buf.flip();
			udpHandler.xmitMsg(resp_buf, request.getReqSocket());
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			resp_buf.clear();
		}

		return ret;
	}

	/**
	 * Send Join Response.
	 * <p>
	 * @param seqNum  A sequence number which indicates a specific packet sent from WTP to AC.
	 * @param request Capwap client request.
	 * @return  true if sending join response successfully, false otherwise.
	 */
	private boolean sendJoinResp(int seqNum, AhCapwapClientRequest request) {
		/**
		 * Discovery Response Message : see 6.2 of
		 * draft-ietf-capwap-protocol-specification-03.txt Must be included o AC
		 * Descriptor, see Section 4.4.1 May be included o AC IPv4 List, see
		 * Section 4.4.2 o AC Name, see Section 4.4.4 o Result Code, see Section
		 * 4.4.31 o Session ID, see Section 4.4.32
		 */
		boolean ret = false;
		ByteBuffer resp_buf = ByteBuffer.allocate(BUFFER_SIZE);

		try {
			int resp_header_len = buildCapwapRespHdr(resp_buf);
			buildCapwapCntlMsgHdr(resp_buf, JOIN_RESPONSE, seqNum);
			int ac_desc_len = fillACDescriptor(resp_buf);
			fillElementLength(resp_buf, resp_header_len, ac_desc_len);
			resp_buf.flip();
			udpHandler.xmitMsg(resp_buf, request.getReqSocket());
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			resp_buf.clear();
		}

		return ret;
	}

	/**
	 * Send Change State Response.
	 * <p>
	 * @param seqNum  A sequence number which indicates a specific packet sent from WTP to AC.
	 * @param request Capwap client request.
	 * @return  true if sending change state response successfully, false
	 *         otherwise.
	 */
	private boolean sendChangeStateResp(int seqNum, AhCapwapClientRequest request) {
		boolean ret = false;
		ByteBuffer resp_buf = ByteBuffer.allocate(BUFFER_SIZE);

		try {
			int resp_header_len = buildCapwapRespHdr(resp_buf);
			buildCapwapCntlMsgHdr(resp_buf, CHANGE_STATE_RESPONSE, seqNum);
			fillElementLength(resp_buf, resp_header_len, 0);
			resp_buf.flip();
			udpHandler.xmitMsg(resp_buf, request.getReqSocket());
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			resp_buf.clear();
		}

		return ret;
	}

	/**
	 * Send Echo Response.
	 * <p>
	 * @param seqNum  A sequence number which indicates a specific packet sent from WTP to AC.
	 * @param request Capwap client request.
	 * @return  true if sending echo response successfully, false otherwise.
	 */
	private boolean sendEchoResp(int seqNum, AhCapwapClientRequest request) {
		boolean ret = false;
		ByteBuffer resp_buf = ByteBuffer.allocate(BUFFER_SIZE);

		try {
			int resp_header_len = buildCapwapRespHdr(resp_buf);
			buildCapwapCntlMsgHdr(resp_buf, ECHO_RESPONSE, seqNum);
			fillElementLength(resp_buf, resp_header_len, 0);
			resp_buf.flip();
			udpHandler.xmitMsg(resp_buf, request.getReqSocket());
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			resp_buf.clear();
		}

		return ret;
	}

	/**
	 * Send WTP Event Response.
	 * <p>
	 * @param seqNum  A sequence number which indicates a specific packet sent from WTP to AC.
	 * @param request Capwap client request.
	 * @return  true if sending wtp event response successfully, false otherwise.
	 */
	private boolean sendWtpEventResp(int seqNum, AhCapwapClientRequest request) {
		boolean ret = false;
		ByteBuffer resp_buf = ByteBuffer.allocate(BUFFER_SIZE);

		try {
			int resp_header_len = buildCapwapRespHdr(resp_buf);
			buildCapwapCntlMsgHdr(resp_buf, WTP_EVENT_RESPONSE, seqNum);
			fillElementLength(resp_buf, resp_header_len, 0);
			resp_buf.flip();
			udpHandler.xmitMsg(resp_buf, request.getReqSocket());
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			resp_buf.clear();
		}

		return ret;
	}

	/**
	 * Send WTP Event Control Request.
	 * <p>
	 * @param request  This object consist of all the necessary messages used for this request sent from AC to WTP.
	 * @return  The sequence number of request sent to WTP.
	 */
	private int sendWtpEventCntlReq(AhWtpEventControlRequest request) {
		int seq_num = getNewSeqNum();
		ByteBuffer resp_buf = ByteBuffer.allocate(BUFFER_SIZE);

		try {
			int resp_header_len = buildCapwapRespHdr(resp_buf);
			buildCapwapCntlMsgHdr(resp_buf, WTP_EVENT_CONTROL_REQUEST, seq_num);
			int wtp_event_cntl_len = fillWtpEventControl(resp_buf, request);
			fillElementLength(resp_buf, resp_header_len, wtp_event_cntl_len);
			resp_buf.flip();
			udpHandler.xmitMsg(resp_buf, clientSocket);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			resp_buf.clear();
		}

		return seq_num;
	}

	/**
	 * Send IDP Query Request.
	 * <p>
	 * @return  The sequence number of request sent to WTP.
	 */
	private int sendIdpQueryReq() {
		int seq_num = getNewSeqNum();
		ByteBuffer resp_buf = ByteBuffer.allocate(BUFFER_SIZE);

		try {
			int resp_header_len = buildCapwapRespHdr(resp_buf);
			buildCapwapCntlMsgHdr(resp_buf, IDP_QUERY_REQUEST, seq_num);
			fillElementLength(resp_buf, resp_header_len, 0);
			resp_buf.flip();
			udpHandler.xmitMsg(resp_buf, clientSocket);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			resp_buf.clear();
		}

		return seq_num;
	}

	/**
	 * Send File Download Request.
	 * <p>
	 * @param request  File download request object.
	 * @return  The sequence number of request sent to WTP.
	 */
	private int sendFileDownloadReq(AhFileDownloadRequest request) {
		int seqNum = getNewSeqNum();
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);

		try {
			int headerLen = buildCapwapRespHdr(buf);
			buildCapwapCntlMsgHdr(buf, WTP_FILE_DOWNLOAD_REQUEST, seqNum);
			int optionLen = fillFileDownload(buf, request);

			if (optionLen > 0) {
				fillElementLength(buf, headerLen, optionLen);
				buf.flip();
				udpHandler.xmitMsg(buf, clientSocket);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			buf.clear();
		}

		return seqNum;
	}

//	/**
//	 * Send layer3 roaming configuration request.
//	 * <p>
//	 * @param request Capwap client request.
//	 * @return  true if sending config request successfully, false otherwise.
//	 */
//	private int sendL3RoamConfigReq(AhLayer3RoamingConfigRequest request) {
//		int ret = FSM_ERROR;
//		ByteBuffer resp_buf = ByteBuffer.allocate(m_buffer_size);
//
//		try {
//			int resp_header_len = buildCapwapRespHdr(resp_buf);
//			buildCapwapCntlMsgHdr(resp_buf, LAYER3_ROAMING_CONFIG_REQUEST, 1);
//			int ac_roaming_len = fillLayer3Roaming(resp_buf);
//
//			if (ac_roaming_len > 0) {
//				fillElementLength(resp_buf, resp_header_len, ac_roaming_len);
//				resp_buf.flip();
//				udpHandler.xmitMsg(resp_buf, clientSocket);
//				ret = FSM_SUCCESS;
//			} else {
//				configuring = true;
//				resp_buf.clear();
//				ret = 1;
//			}
//		} catch (Exception e) {
//			ex.printStackTrace();
//		} finally {
//			resp_buf.clear();
//		}
//
//		return ret;
//	}


	private boolean updateAp() {
		try {
		//	AhCapwapDiscovery.discover(this);

			return true;
		} catch (Exception e) {
			logger.error("updateAp", "Filter Ap Failed : " + e.getMessage());
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Get capwap client request name by request type.
	 * <p>
	 * @param type  The type of capwap client request.
	 * @return  The name of capwap client request.
	 */
	private String getReqName(ClientReqType type) {
		String reqName;

		switch (type) {
			case DISCOVER :
				reqName = "Discovery Request";
				break;
			case JOIN:
				reqName = "Join Request";
				break;
			case CHANGE_STATE :
				reqName = "Change State Request";
				break;
			case ECHO :
				reqName = "Echo Request";
				break;
			case TIMEOUT :
				reqName = "Timeout Request";
				break;
			default:
				reqName = "Unknown Capwap Client Request Request";
				break;
		}

		return reqName;
	}

	/**
	 * Get WTP event name by the specified WTP event type.
	 * <p>
	 * @param type  Specific type of WTP Event.
	 * @return  WTP event name.
	 */
	private String getWtpEventName(int type) {
		String eventName;

		switch (type) {
			case IDP_STATISTICS :
				eventName = "IDP Event";
				break;
			case AP_TYPE_CHANGE :
				eventName = "AP Type Change Event";
				break;
			case FILE_DOWNLOAD_RESULT:
				eventName = "File Download Result Event";
				break;
			case FILE_DOWNLOAD_PROGRESS:
				eventName = "File Download Progress Event";
				break;
			default:
				eventName = "Unknown WTP Event";
				break;
		}

		return eventName;
	}

	/**
	 * Capwap finite state machine.
	 * <p>
	 * @param reqType  The type of capwap client request.
	 * @param seqNum  A sequence number which indicates a specific packet sent from WTP to AC.
	 * @param request  Capwap client request.
	 * @return  The state of fsm.
	 */
	protected FsmState capwapFsm(ClientReqType reqType, int seqNum, AhCapwapClientRequest request) {
		synchronized (this) {
			switch (fsmState) {
				case RESET:
					procInResetState(reqType, seqNum, request);
					break;
				case IDLE:
					procInIdleState(reqType, seqNum, request);
					break;
				case JOIN:
					procInJoinState(reqType, seqNum, request);
					break;
				case RUN:
					procInRunState(reqType, seqNum, request);
					break;
				default:
					logger.error("capwapFsm", "["+ip+"]Unknown State " + fsmState);
					break;
			}
		}

		return fsmState;
	}

	/**
	 * Process capwap request when fsm is in reset state.
	 * <p>
	 * @param reqType  The type of capwap client request.
	 * @param seqNum  Sequence number of each capwap packet.
	 * @param request  Capwap client request.
	 */
	private void procInResetState(ClientReqType reqType, int seqNum, AhCapwapClientRequest request) {
		String fsmEvent = getReqName(reqType);

		switch (reqType) {
			/*
			 * Event Action Discovery event Send Discovery Response, Transition to
			 * FSM_IDLE state start waitForJoin timer Others Ignore
			 */
			case DISCOVER:
				if (sendDiscoveryResp(seqNum, request)) {
					fsmState = FsmState.IDLE;
					startTimer(TIMER_WAIT_FOR_JOIN);
				}
				break;
			case JOIN:
				if (capwapVer == CAPWAP_VERSION_V4) {
					if (sendJoinResp(seqNum, request)) {
						fsmState = FsmState.JOIN;
						startTimer(TIMER_WAIT_FOR_JOIN);
					}
				} else {
					logger.debug("procInResetState", "["+ip+"]Ignore event ["+fsmEvent+"] in 'RESET' State.");
				}
				break;
			default:
				logger.debug("procInResetState", "["+ip+"]Ignore event ["+fsmEvent+"] in 'RESET' State.");
				break;
		}
	}

	/**
	 * Process capwap request when fsm is in idle state.
	 * <p>
	 * @param reqType  The type of capwap client request.
	 * @param seqNum  Sequence number of each capwap packet.
	 * @param request  Capwap client request.
	 */
	private void procInIdleState(ClientReqType reqType, int seqNum, AhCapwapClientRequest request) {
		String fsmEvent = getReqName(reqType);

		switch (reqType) {
			case TIMEOUT:
				initFsm();
				break;
			case JOIN:
				if (sendJoinResp(seqNum, request)) {
					fsmState = FsmState.JOIN;
					startTimer(TIMER_WAIT_FOR_JOIN);
				}
				break;
			default:
				logger.debug("procInIdleState", "["+ip+"]Ignore event ["+fsmEvent+"] in 'IDLE' State.");
				break;
		}
	}

	/**
	 * Process capwap request when fsm is in join state.
	 * <p>
	 * @param reqType  The type of capwap client request.
	 * @param seqNum  Sequence number of each capwap packet.
	 * @param request  Capwap client request.
	 */
	private void procInJoinState(ClientReqType reqType, int seqNum, AhCapwapClientRequest request) {
		String fsmEvent = getReqName(reqType);

		/*
		 * Event Action ChangeState Req Send ChangeState Response State change
		 * to FSM_RUN Start Echo Interval Timer Initialize Echo Request Timer
		 * Timeout(WaitForJoin) Transition state to RESET Others Ignore
		 */
		switch (reqType) {
			case TIMEOUT:
				initFsm();
				break;
			case CHANGE_STATE:
				if (sendChangeStateResp(seqNum, request)) {
					fsmState = FsmState.RUN;

					// Save each client socket communicated between WTP and AC so
					// that AC may send any request to WTP forwardly at any time.
					clientSocket = request.getReqSocket();

					if (updateAp()) {
						// Go to run state and start time if database operation is
						// successful.
						startTimer(TIMER_WAIT_FOR_ECHO);
						// startTimer(AC_NEIGHBOR_DEAD_INTERVAL_FOR_ECHO);
					} else {
						// Remove fsm if database operation is unsuccessful.
						logger.debug("procInJoinState", "["+ip+"]Reinitialize state if error occurs in database operation when handing Change State Event.");
						initFsm();
					}
				}
				break;
			default:
				logger.debug("procInJoinState", "["+ip+"]Ignore event ["+fsmEvent+"] in 'JOIN' State.");
				break;
		}
	}

	/**
	 * Process capwap request when fsm is in run state.
	 * <p>
	 * @param reqType  The type of capwap client request.
	 * @param seqNum  Sequence number of each capwap packet.
	 * @param request  Capwap client request.
	 */
	private void procInRunState(ClientReqType reqType, int seqNum, AhCapwapClientRequest request) {
		String fsmEvent = getReqName(reqType);

		// Event Action Echo Request Section 7.2 of IETF CAPWAP draft standard
		// Send Echo Response Set EchoInterval Timer Timeout(NeighberDead
		// Interval) Transition state to IDLE Link Down Transition State to IDLE
		switch (reqType) {
			case TIMEOUT:
				initFsm();
				updateAp();
				break;
			case DISCOVER:
				if (capwapVer == CAPWAP_VERSION_V4) {
					sendDiscoveryResp(seqNum, request);// no change of state
				} else {
					logger.debug("procInRunState", "["+ip+"]Ignore event ["+fsmEvent+"] in 'RUN' State.");
				}
				break;
			case JOIN:
				if (capwapVer == CAPWAP_VERSION_V4) {
					clearTimer();

					// To remove old fsm before creating new fsm adding a timeout
					// event before adding the original join request event into
					// queue.
					AhCapwapClientRequest timeoutReq = new AhCapwapClientRequest(RequestType.TIMEOUT_REQUEST);
					clientReqQue.offer(timeoutReq);

					// Reprocess join request event to establish capwap connection
					// as soon as possible between HiveAp and HM.
					// Flip request buffer and put the original join request event
					// into queue again.
					request.getReqPacket().flip();
					clientReqQue.offer(request);
				} else {
					logger.debug("procInRunState", "["+ip+"]Ignore event ["+fsmEvent+"] in 'RUN' State.");
				}
				break;
			case ECHO:
				if (sendEchoResp(seqNum, request)) {
					startTimer(TIMER_WAIT_FOR_ECHO);
					// startTimer(AC_NEIGHBOR_DEAD_INTERVAL_FOR_RUN);
				}

				// if (m_capwap_version == CAPWAP_VERSION_V4 && !configuring &&
				// m_config_num < MAX_CONFIG_NUM)
				// {
				// /**
				// * Send configuration request to wtp after receiving echo request
				// from wtp
				// */
				// int status = sendConfigRequest(request);
				// if (status == FSM_SUCCESS)
				// {
				// logDebugMsg("Francis", "CapwapFSM", "procInRunState", "Sending
				// Configuration Request Successfully");
				// }
				// else if (status == 1)
				// {
				// logDebugMsg("Francis", "CapwapFSM", "procInRunState", "Layer3
				// Roaming Configuration is set disable or none include and exclude
				// neighbors are chosen");
				// }
				// else
				// {
				// logDebugMsg("Francis", "CapwapFSM", "procInRunState", "Sending
				// Configuration Request failed, Error Code = " + status);
				// }
				//
				// m_config_num++;
				// }
				break;
			default:
				logger.debug("procInRunState", "["+ip+"]Ignore event ["+fsmEvent+"] in 'RUN' State.");
				break;
		}
	}

	/**
	 * Process WTP Event.
	 * <p>
	 * @param type  WTP Event Type.
	 * @return  true if FSM in "RUN" state, false otherwise. Only in "RUN" state is permitted to process WTP Event.
	 */
	protected boolean checkStatus(int type) {
		String wtpEvent = getWtpEventName(type);

		switch (fsmState) {
			case RESET:
				logger.debug("checkStatus", "["+ip+"]Ignore "+wtpEvent+" in 'RESET' State.");
				break;
			case IDLE:
				logger.debug("checkStatus", "["+ip+"]Ignore "+wtpEvent+" in 'IDLE' State.");
				break;
			case JOIN:
				logger.debug("checkStatus", "["+ip+"]Ignore "+wtpEvent+" in 'JOIN' State.");
				break;
			case RUN:
				return true;
			default:
				logger.error("checkStatus", "["+ip+"]Ignore "+wtpEvent+" in 'Unknown' State.");
				break;
		}

		return false;
	}

	/**
	 * Process WTP Event.
	 * <p>
	 * @param eventType  WTP Event Type.
	 * @param seqNum  A sequence number which indicates a specific packet sent from WTP to AC.
	 * @param request  WTP Event Request message.
	 * @param fragFlag  Indicates whether this idp msg is a fragment.
	 * @param lastFragFlag  Indicates whether the idp msg is the last fragment.
	 * @param fragId  The whose value is assigned to each group of fragments making up a complete set.
	 * @param offset  Indicates where in the payload this fragment belongs during re-assembly.
	 * @param msg  A fragment of idp message.
	 */
	protected void procWtpEvent(
			int eventType,
			int seqNum,
			AhCapwapClientRequest request,
			int fragFlag,
			int lastFragFlag,
			int fragId,
			int offset,
			byte[] msg) {
		logger.debug("procWtpEvent", "["+ip+"]WTP event size = " + wtpEvents.size() + ", receive " + getWtpEventName(eventType)
				+ ", sequence number = " + seqNum + ", fragment flag = " + fragFlag
				+ ", last fragment flag = " + lastFragFlag + ", fragment id = " + fragId);

		switch (eventType) {
			case IDP_STATISTICS:
				procIdpEvent(fragFlag, lastFragFlag, fragId, offset, msg);
				break;
			case AP_TYPE_CHANGE:
				procApTypeChangeEvent(msg);
				break;
			case FILE_DOWNLOAD_RESULT:
				procDownloadFinishEvent(msg);
				break;
			case FILE_DOWNLOAD_PROGRESS:
				procDownloadProgressEvent(msg);
				break;
			default:
				logger.error("procWtpEvent", "["+ip+"]Unknown WTP Event["+eventType+"].");
				break;
		}

		if (sendWtpEventResp(seqNum, request)) {
			// See capwap draft about Control Channel Management.
			// Use WTP Event Request instead of Echo Request.
			startTimer(TIMER_WAIT_FOR_ECHO);
		}
	}

	/**
	 * Process IDP Event.
	 * <p>
	 * @param fragFlag  Indicates whether this idp msg is a fragment.
	 * @param lastFragFlag  Indicates whether the idp msg is the last fragment.
	 * @param fragId  The whose value is assigned to each group of fragments making up a complete set.
	 * @param offset  Indicates where in the payload this fragment belongs during re-assembly.
	 * @param msg  A fragment of idp message.
	 */
	private void procIdpEvent(
			int fragFlag,
			int lastFragFlag,
			int fragId,
			int offset,
			byte[] msg) {
		logger.debug("procIdpEvent", "["+ip+"]WTP event size = " + wtpEvents.size());

		synchronized (wtpEvents) {
			AhIdpReportEvent event;

			if (fragFlag == 1) {
				if (!wtpEvents.containsKey(fragId)) {
					event = new AhIdpReportEvent();
					event.setFragId(fragId);
					logger.debug("procIdpEvent", "["+ip+"]Add new fragment, id = " + fragId + ", offset = " + offset);
					event.addFrag(offset, msg);

					if (lastFragFlag == 1) {
						event.setTotalFragNum(offset + 1);
						logger.debug("procIdpEvent", "["+ip+"]"
								+ "Total fragments = " + event.getTotalFragNum()
								+ ", received fragments = " + event.size());
					} else {
						logger.debug("procIdpEvent", "["+ip+"]"
								+ "Received fragments = " + event.size());
					}

					// Start timer for waiting the rest of fragments.
					event.startTimer(TIMER_WAIT_FOR_WTP_EVENT);
					logger.debug("procIdpEvent", "["+ip+"]Add new IDP event, key = " + fragId);
					wtpEvents.put(fragId, event);
				} else {
					Object obj = wtpEvents.get(fragId);

					if (obj instanceof AhIdpReportEvent) {
						event = (AhIdpReportEvent)obj;
						event.addFrag(offset, msg);

						if (lastFragFlag == 1) {
							event.setTotalFragNum(offset + 1);
							logger.debug("procIdpEvent", "["+ip+"]"
									+ "Total fragments = " + event.getTotalFragNum()
									+ ", received fragments = " + event.size());
						} else {
							logger.debug("procIdpEvent", "["+ip+"]"
									+ "Received fragments = " + event.size());
						}

						if (event.allFragsPresent()) {
							try {
								// Process idp event if all the fragments are received completely.
								event.processEvent(this);
								logger.debug("procIdpEvent", "["+ip+"]Remove IDP event, key = " + fragId);
								wtpEvents.remove(fragId);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							// Start timer for waiting the rest of fragments.
							event.startTimer(TIMER_WAIT_FOR_WTP_EVENT);
						}
					} else {
						logger.error("procIdpEvent", "The WTP event key " + fragId + " is not mapping to a IDP event object. Dropping the fragment, offset = " + offset);
					}
				}
			} else {
				// Not fragments.
				event = new AhIdpReportEvent(msg);

				try {
					event.processEvent(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Process Ap Type Change Event.
	 * <p>
	 * @param packet  Which is wrapped in the event used to illuminate the event.
	 */
	private void procApTypeChangeEvent(byte[] packet) {
		logger.debug("procApTypeChangeEvent", "["+serialNum+"]The Ap Type has been converted into " + (apType == AP_TYPE_PORTAL ? "'Portal'." : "'MP'."));

		AhApTypeChangeEvent event = new AhApTypeChangeEvent(packet);

		try {
			event.processEvent(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Process File Download Result Event.
	 * <p>
	 * @param packet  Which is wrapped in the event used to illuminate the event.
	 */
	private void procDownloadFinishEvent(byte[] packet) {
		logger.debug("procDownloadFinishEvent", "["+ip+"]Receive file download finish event.");

		AhFileDownloadFinishEvent event = new AhFileDownloadFinishEvent(packet);

		try {
			event.processEvent(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Process File Download Progress Event.
	 * <p>
	 * @param packet  Which is wrapped in the event used to illuminate the event.
	 */
	private void procDownloadProgressEvent(byte[] packet) {
		logger.debug("procDownloadProgressEvent", "["+ip+"]Receive file download progress event.");

		AhFileDownloadProgressEvent event = new AhFileDownloadProgressEvent(packet);

		try {
			event.processEvent(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}