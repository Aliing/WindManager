/**
 *@filename		AhClientRequestProcessor.java
 *@version
 *@author		Long
 *@createtime	2007-8-4 12:28:31 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.capwap;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

// aerohive import
import com.ah.be.capwap.event.AhCapwapEvent;
import com.ah.be.capwap.event.AhCapwapShutdownEvent;
import com.ah.be.capwap.event.request.client.AhCapwapClientRequest;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

/**
 * @author Long
 * @version V1.0.0.0
 */
class AhClientRequestProcessor extends Thread implements AhCapwapConstants {

	private static final long	serialVersionUID	= 1L;
	private static final Tracer logger = new Tracer(AhClientRequestProcessor.class.getSimpleName());

	private int m_capwap_version;
	private int m_wtp_ipv4;
	private int m_netmask;
	private int m_gateway;
	private int m_region_code;
	private int m_country_code;
	private int	m_ip_type;
	private int	m_ap_type;
	private int	m_event_type;
	private String m_serial_number;
	private String m_hardware_version;
	private String m_software_version;
	private String m_mac_address;

	private int m_rid;
	private int m_hlen;
	private int m_wbid;
	private int m_t;
	private int m_f;
	private int m_l;
	private int m_w;
	private int m_m;
	private int m_flags;
	private int m_frag_id;
	private int m_frag_offset;
	private int m_rsvd;
	private int m_msg_type;
	private int m_seq_num;
	private int m_msg_element_length;
	private int m_time_stamp;
	private int m_discovery_type;
	private String m_location;
	private int m_radio_id;
	private int m_admin_state;
	private int m_cause;
	private int m_session_id;
	private int m_max_radios;
	private int m_radios_in_use;
	private int m_encryption;
	private int m_fallback;
	private int m_tunnel_mode;
	private int m_mac_type;
	private String m_wtp_name;
	private int m_tx_queue_level;
	private int m_wireless_link_frames;

	private byte[] fragment;
	private String linkIp;
	private final Map<String, AhCapwapFsm> fsmHash;
	private final BlockingQueue<AhCapwapEvent> clientReqQue;
	private final AhUdpHandler udpHandler;

	protected AhClientRequestProcessor(AhCapwapServer capwapServer) {
		fsmHash = capwapServer.fsmHash;
		clientReqQue = capwapServer.clientReqQue;
		udpHandler = capwapServer.udpHandler;
	}

	@Override
	public void run() {
		AhCapwapEvent event;
		AhCapwapClientRequest request;

		while (true) {
			try {
				// Retrieve capwap client request from queue, block if the queue is empty.
				event = clientReqQue.take();

				if (event instanceof AhCapwapClientRequest) {
					request = (AhCapwapClientRequest) event;
					
					switch (request.getReqType()) {
						case CLIENT_REQUEST:
							// Get the ip address of WTP which is communicated with AC.
							linkIp = ((InetSocketAddress) request.getReqSocket()).getAddress().getHostAddress();
							parseCapwapRequest(request.getReqPacket());

							if (m_msg_type >= 0) {
								processRequest(m_msg_type, request);
							} else {
								logger.error("run", "[" + linkIp + "]Wrong msg type["
										+ m_msg_type
										+ "] in parsing capwap client request");
							}

							reinitVars();
							break;
						case TIMEOUT_REQUEST:
							processTimeout();
							break;
						default:
							break;
					}
				} else if (event instanceof AhCapwapShutdownEvent) {
					break;
				} else {
					logger.error("run", "Unknown capwap event");
				}
			} catch (Exception e) {
				logger.error("run", "[" + linkIp + "]Parsing capwap client reqeust packet failed", e);
			}
		}
	}

	private void reinitVars() {
		m_seq_num = -1;
		m_capwap_version = 0;
		m_wtp_ipv4 = 0;
		m_netmask = 0;
		m_gateway = 0;
		m_region_code = 0;
		m_country_code = 840; // Use US as default country code
		m_ip_type = IP_TYPE_DYNAMIC;
		m_ap_type = AP_TYPE_UNKNOWN;
		m_event_type = -1;
		linkIp = "";
		m_serial_number = "";
		m_hardware_version = "";
		m_software_version = "";
		m_wtp_name = "";
		m_location = "";
		m_mac_address = "";
		fragment = null;
	}

	/**
	 * Type : The following values are supported. The Hardware Version, Software
	 * Version, and Boot Version values MUST be included.
	 * <p>
	 * 0 - WTP Model Number : The WTP Model Number MUST be included in the WTP
	 * Board Data message element. 1 - WTP Serial Number: The WTP Serial Number
	 * MUST be included in the WTP Board Data message element. 2 - Board ID: A
	 * hardware identifier, which MAY be included in the WTP Board Data mesage
	 * element. 3 - Board Revision A revision number of the board, which MAY be
	 * included in the WTP Board Data message element. 4 - Hardware Version: The
	 * WTP's hardware version number. 5 - Software Version: The WTP's Firmware
	 * version number. 6 - Boot Version: The WTP's boot loader's version number.
	 * <p>
	 * @param msgType  Message type.
	 * @param value  Message value.
	 */
	private void setValueWithType(int msgType, String value) {
		switch (msgType) {
			case 0:
				// m_model_number = value;
				break;
			case 1:
				m_serial_number = value;
				break;
			case 2:
				// m_board_ID = value;
				break;
			case 3:
				// m_board_revision = value;
				break;
			case 4:
				m_hardware_version = value;
				break;
			case 5:
				m_software_version = value;
				break;
			case 6:
				// m_boot_version = value;
				break;
			default:
				break;
		}
	}

	/**
	 * Parse capwap header.
	 * <p>
	 * Decode capwap header All CAPWAP protocol messages are encapsulated using
	 * a common header format, regardless of the CAPWAP control or CAPWAP Data
	 * transport used to carry the messages. However, certain flags are not
	 * applicable for a given transport. Refer to the specific transport section
	 * in order to determine which flags are valid. Note that the optional
	 * fields defined in this section MUST be present in the precise order shown
	 * below.
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
	 * @param buf -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap header.
	 */
	private void parseCapwapHeader(ByteBuffer buf) throws AhCapwapDecodeException {
		if (buf.limit() < 2) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong Capwap Header length[" + buf.limit()
					+ "], the right length should be no less than 2");
		}

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
			int temp = buf.getInt();
			int offset = 32 - version_size;
			m_capwap_version = AhDecoder.bits2int(temp, offset, version_size);
			offset -= rid_size;
			m_rid = AhDecoder.bits2int(temp, offset, rid_size);
			offset -= hlen_size;
			m_hlen = AhDecoder.bits2int(temp, offset, hlen_size);
			offset -= wbid_size;
			m_wbid = AhDecoder.bits2int(temp, offset, wbid_size);
			offset -= t_size;
			m_t = AhDecoder.bits2int(temp, offset, t_size);
			offset -= f_size;
			m_f = AhDecoder.bits2int(temp, offset, f_size);
			offset -= l_size;
			m_l = AhDecoder.bits2int(temp, offset, l_size);
			offset -= w_size;
			m_w = AhDecoder.bits2int(temp, offset, w_size);
			offset -= m_size;
			m_m = AhDecoder.bits2int(temp, offset, m_size);
			offset -= flags_size;
			m_flags = AhDecoder.bits2int(temp, offset, flags_size);

			// Fragment ID|Frag Offset|Rsvd
			temp = buf.getInt();
			offset = 32 - fragment_id_size;
			m_frag_id = AhDecoder.bits2int(temp, offset, fragment_id_size);
			offset -= frag_offset_size;
			m_frag_offset = AhDecoder.bits2int(temp, offset, frag_offset_size);
			offset -= rsvd_size;
			m_rsvd = AhDecoder.bits2int(temp, offset, rsvd_size);
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing Capwap Header Error", e);
		}
	}

	/**
	 * Capwap Control Message Format.
	 * <p>
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
	 * @param buf -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void parseCapwapRequest(ByteBuffer buf) throws AhCapwapDecodeException {
		// Parse capwap header.
		parseCapwapHeader(buf);

		try {
			m_msg_type = buf.getInt();
			m_seq_num = buf.get();
			m_msg_element_length = AhDecoder.bytes2int(buf, 2);
			m_flags = buf.get();
			m_time_stamp = buf.getInt();
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing Capwap Control Message Error", e);
		}

		// Parse all kinds of capwap elements.
		parseCapwapElements(buf, m_msg_element_length - 7);
	}

	/**
	 * Parse all kinds of capwap elements.
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void parseCapwapElements(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (buf.remaining() != len) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Received packet length[" + buf.remaining() + "] mismatch the given length[" + len + "]");
		}

		final int TYPE_LENGTH = 4;
		final int LEN_LENGTH = 2;

		int msgType;
		int msgLen;

		while (buf.hasRemaining()) {
			try {
				msgType = AhDecoder.bytes2int(buf, TYPE_LENGTH);
				msgLen = AhDecoder.bytes2int(buf, LEN_LENGTH);
			} catch (Exception e) {
				throw new AhCapwapDecodeException("[" + linkIp + "]Parsing Capwap Message Type and Length Error", e);
			}

			switch (msgType) {
				case DISCOVERY_TYPE:
					extractDiscoveryType(buf, msgLen);
					break;
				case LOCATION_DATE:
					extractLocationData(buf, msgLen);
					break;
				case RADIO_ADMINISTRATIVE_STATE:
					extractRadioAdminState(buf, msgLen);
					break;
				case SESSION_ID:
					extractSessionId(buf, msgLen);
					break;
				case DESCRIPTOR:
					extractDescriptor(buf, msgLen);
					break;
				case FALLBACK:
					extractFallback(buf, msgLen);
					break;
				case FRAME_TUNNEL_MODE:
					extractFrameTunnelMode(buf, msgLen);
					break;
				case IPV4:
					extractIPv4(buf, msgLen);
					break;
				case MAC_TYPE:
					extractMacType(buf, msgLen);
					break;
				case WTP_NAME:
					extractName(buf, msgLen);
					break;
				case OPERATIONAL_STATISTICS:
					extractOperStat(buf, msgLen);
					break;
				case MAC_ADDRESS:
					extractMacAddress(buf, msgLen);
					break;
				case NETMASK:
					extractNetmask(buf, msgLen);
					break;
				case GATEWAY:
					extractGateway(buf, msgLen);
					break;
				case REGION_CODE:
					extractRegionCode(buf, msgLen);
					break;
				case COUNTRY_CODE:
					extractCountryCode(buf, msgLen);
					break;
				case IP_TYPE:
					extractIpType(buf, msgLen);
					break;
				case AP_TYPE:
					extractApType(buf, msgLen);
					break;
				case IDP_STATISTICS:
					m_event_type = msgType;
					extractIdpStat(buf, msgLen);
					break;
				case AP_TYPE_CHANGE:
					m_event_type = msgType;
					extractApType(buf, msgLen);
					break;
				case FILE_DOWNLOAD_RESULT:
					m_event_type = msgType;
					extractFileDownloadResult(buf, msgLen);
					break;
				case FILE_DOWNLOAD_PROGRESS:
					m_event_type = msgType;
					extractFileDownloadProgress(buf, msgLen);
					break;
				default:
					throw new AhCapwapDecodeException("[" + linkIp + "]Unknown msg type[" + msgType + "]");
			}
		}
	}

	/**
	 * 4.6.20. Discovery Type.
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractDiscoveryType(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 1) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong Discovery Type length[" + len
					+ "], 1 is right value");
		}

		try {
			m_discovery_type = buf.get();
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Parsing Discovery Type Error", e);
		}
	}

	/**
	 * Location Data.
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractLocationData(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len < 1) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong Location Data length[" + len
					+ "], the right value should be no less than 1");
		}

		try {
			m_location = AhDecoder.bytes2String(buf, len);
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Parsing Location Data Error", e);
		}
	}

	/**
	 * Radio Administrative State.
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractRadioAdminState(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 3) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong Radio Administrative State length[" + len
					+ "], 3 is right value");
		}

		try {
			m_radio_id = buf.get();
			m_admin_state = buf.get();
			m_cause = buf.get();
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing Radio Admin State Error", e);
		}
	}

	/**
	 * Session ID.
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractSessionId(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 4) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong Session Id length[" + len
					+ "], 4 is right value");
		}

		try {
			m_session_id = buf.getInt();
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing Session Id Error", e);
		}
	}

	/**
	 * WTP Descriptor.
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractDescriptor(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		try {
			m_max_radios = buf.get();
			m_radios_in_use = buf.get();
			m_encryption = AhDecoder.bytes2int(buf, 2);

			len -= 4;
			int vendor_identifier;
			int type;
			int length;
			String value;

			while (len > 0) {
				vendor_identifier = buf.getInt();
				type = AhDecoder.bytes2int(buf, 2);
				length = AhDecoder.bytes2int(buf, 2);
				value = AhDecoder.bytes2String(buf, length);

				setValueWithType(type, value);
				len = len - 8 - length;
			}
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing WTP Descriptor Error", e);
		}
	}

	/**
	 * WTP Fallback.
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractFallback(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 1) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong WTP Fallback length[" + len
					+ "], 1 is right value");
		}

		try {
			m_fallback = buf.get();
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing WTP Fallback Error", e);
		}
	}

	/**
	 * WTP Frame Tunnel Mode.
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractFrameTunnelMode(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 1) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong WTP Frame Tunnel Mode length[" + len
					+ "], 1 is right value");
		}

		try {
			m_tunnel_mode = buf.get();
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing WTP Frame Tunnel Mode Error", e);
		}
	}

	/**
	 * WTP IPv4 IP Address.
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractIPv4(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 4) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong WTP IPv4 length[" + len + "], 4 is right value");
		}

		try {
			m_wtp_ipv4 = buf.getInt();
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing WTP IPv4 Error", e);
		}
	}

	/**
	 * WTP MAC Type.
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractMacType(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 1) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong WTP MAC Type length[" + len
					+ "], 1 is right value");
		}

		try {
			m_mac_type = buf.get();
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing Mac Type Error", e);
		}
	}

	/**
	 * 4.6.45. WTP Name.
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractName(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len < 1) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong WTP Name length[" + len
					+ "], the right value should be no less than 1");
		}

		try {
			m_wtp_name = AhDecoder.bytes2String(buf, len);
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing WTP Name Error", e);
		}
	}

	/**
	 * 4.6.46. WTP Operational Statistics.
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractOperStat(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 4) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong WTP Operational Statistics length[" + len
					+ "], 4 is right value");
		}

		try {
			m_radio_id = buf.get();
			m_tx_queue_level = buf.get();
			m_wireless_link_frames = AhDecoder.bytes2int(buf, 2);
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing WTP Operational Statistics Error", e);
		}
	}

	/**
	 * WTP Mgt0 Mac Address (Defined by Aerohive, msg type : 5000)
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractMacAddress(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 6) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong WTP MAC Address length[" + len
					+ "], 6 is right value");
		}

		try {
			m_mac_address = AhDecoder.bytes2hex(buf, len);
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing WTP Mac Address Error", e);
		}
	}

	/**
	 * WTP Netmask (Defined by Aerohive, msg type : 5001)
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractNetmask(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 4) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong WTP Netmask length[" + len
					+ "], 4 is right value");
		}

		try {
			m_netmask = buf.getInt();
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing WTP Netmask Error", e);
		}
	}

	/**
	 * WTP Default Gateway (Defined by Aerohive, msg type : 5002)
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractGateway(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 4) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong WTP Gateway length[" + len
					+ "], 4 is right value");
		}

		try {
			m_gateway = buf.getInt();
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing WTP Gateway Error", e);
		}
	}

	/**
	 * Region Code (Defined by Aerohive, msg type : 5003)
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractRegionCode(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 4) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong Region Code length[" + len
					+ "], 4 is right value");
		}

		try {
			m_region_code = buf.getInt();
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing Region Code Error", e);
		}
	}

	/**
	 * Country Code (Defined by Aerohive, msg type : 5004)
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractCountryCode(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 4) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong Country Code length[" + len
					+ "], 4 is right value");
		}

		try {
			m_country_code = buf.getInt();
		} catch (Exception e) {
			throw new AhCapwapDecodeException("[" + linkIp + "]Parsing Country Code Error", e);
		}
	}

	/**
	 * Ip Type (Defined by Aerohive, msg type : 5005)
	 * <p>
	 * +-+-+-+-+-+-+-+-+
	 * |    Static     |
	 * +-+-+-+-+-+-+-+-+
	 * <p>
	 * Static:   An 8-bit boolean stating whether the WTP should use a static IP address or not.
	 *           A value of zero disables the static IP address, while a value of one enables it.
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractIpType(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 1) {
			throw new AhCapwapDecodeException("["+linkIp+"]Wrong Ip Type length["+len+"], 1 is right value");
		}

		try {
			m_ip_type = buf.get();
		} catch (Exception e) {
			throw new AhCapwapDecodeException("["+linkIp+"]Parsing Ip Type Error", e);
		}
	}

	/**
	 * Ap Type (Defined by Aerohive, msg type : 5006)
	 * <p>
	 * +-+-+-+-+-+-+-+-+
	 * |    Portal     |
	 * +-+-+-+-+-+-+-+-+
	 * <p>
	 * Portal:   An 8-bit boolean stating whether the Ap is Portal or MP.
	 *           A value of zero indicates MP and one indicates Portal.
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractApType(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 1) {
			throw new AhCapwapDecodeException("["+linkIp+"]Wrong Ap Type length["+len+"], 1 is right value");
		}

		try {
			fragment = new byte[len];
			buf.get(fragment);
			m_ap_type = fragment[0];
		} catch (Exception e) {
			throw new AhCapwapDecodeException("["+linkIp+"]Parsing Ap Type Error", e);
		}
	}

	/**
	 * Idp Statistics Event (Defined by Aerohive, msg type : 6001).
	 * <p>
	 * 0 1 2 3 4 5 6 7 8
	 * +-+-+-+-+-+-+-+-+
	 * | Idp Statistics
	 * +-+-+-+-+-+-+-+-+
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractIdpStat(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len < 1) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong IDP Statistics length[" + len
					+ "], the right value should be no less than 1");
		}

		try {
			fragment = new byte[len];
			buf.get(fragment);
		} catch (Exception e) {
			throw new AhCapwapDecodeException("["+linkIp+"]Parsing Idp Statistics Error", e);
		}
	}

	/**
	 * File Download Result Event (Defined by Aerohive, msg type : 6102).
	 * <p>
	 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |     Result    |      Error Cli Length     |     Error Cli...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * <p>
	 * Result : 0 - success.
	 *          1 - fail.
	 *          2 - download queue is full.
	 * Error Cli Length : The attribute is exist only when the result is equals to 1.
	 * Error Cli : The attribute is exist only when the result is equals to 1.
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractFileDownloadResult(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len < 1) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong File Download Result length["+len+"], the right value should be no less than 1");
		}

		try {
			fragment = new byte[len];
			buf.get(fragment);
		} catch (Exception e) {
			throw new AhCapwapDecodeException("["+linkIp+"]File Download Result Error", e);
		}
	}

	/**
	 * <p>
	 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * | Download Type |     Status    |          Finish Size
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *            Finish Size          |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * <p>
	 * Download Type : 1 - Image.
	 *                 2 - Script.
	 *                 3 - Bootstrap.
	 *                 4 - Captive web portal key.
	 *                 5 - Captive web portal page.
	 *                 6 - Radius certificate.
	 * <p>
	 * Status : 1 - Downloading.
	 *          2 - Finish.
	 * <p>
	 * Finish Size : Indicates how many bytes of the file have been downloaded completely.
	 * <p>
	 * @param buf -
	 * @param len -
	 * @throws AhCapwapDecodeException  If error occurs in parsing capwap option message.
	 */
	private void extractFileDownloadProgress(ByteBuffer buf, int len) throws AhCapwapDecodeException {
		if (len != 6) {
			throw new AhCapwapDecodeException("[" + linkIp
					+ "]Wrong File Download Progress length["+len+"], 6 is right value");
		}

		try {
			fragment = new byte[len];
			buf.get(fragment);
		} catch (Exception e) {
			throw new AhCapwapDecodeException("["+linkIp+"]File Download Progress Error", e);
		}
	}

	/**
	 * Validate the serial number.
	 * <p>
	 * @param serialNum  The serial number of WTP.
	 * @return  true if valid, otherwise false.
	 */
	private boolean validateSerialNumber(String serialNum) {
		String regex = "[0-9 a-f A-F]";

		for (int i = 0; i < serialNum.length(); i++) {
			if (!serialNum.substring(i, i + 1).matches(regex)) {
				logger.error("validateSerialNumber", "Invalid serial number[" + m_serial_number
						+ "] detected from " + linkIp);

				return false;
			}
		}

		return true;
	}

	/**
	 * Get fsm key in the fsmHash table by serail number.
	 * <p>
	 * @param serialNum  The serial number of WTP.
	 * @return  fsm key relative to serial number
	 */
	private String getOriginalFsmKey(String serialNum) {
		AhCapwapFsm fsm;

		for (String key : fsmHash.keySet()) {
			fsm = fsmHash.get(key);

			if (fsm.getSerialNum().equals(serialNum)) {
				return key;
			}
		}

		return null;
	}

	/*
	 * loop thru each of capwap client in the fsmHash table.
	 */
	private void processTimeout() {
		synchronized (fsmHash) {
			FsmState fsmState;
			AhCapwapFsm fsm;

			for (Iterator<AhCapwapFsm> iter = fsmHash.values().iterator(); iter.hasNext();) {
				fsm = iter.next();
				fsm.decreaseTimer(AhCapwapServer.TIMEOUT_EVENT_INTERVAL);

				// Timeout occurred, generate timeout event.
				if (fsm.getRemainTimer() <= 0) {
					fsmState = fsm.capwapFsm(ClientReqType.TIMEOUT, m_seq_num, null);

					if (fsmState == FsmState.RESET) {
						// Remove unuseful fsm which is timed out.
						logger.debug("processTimeout", "Remove FSM for client " + fsm.getSerialNum());
						iter.remove();
					}
				}
			}
		}
	}

	/**
	 * Process all kinds of capwap requests.
	 * <p>
	 * @param msgType  Message Type.
	 * @param request -
	 */
	private void processRequest(int msgType, AhCapwapClientRequest request) {
		switch (msgType) {
			case DISCOVERY_REQUEST:
				procDiscoveryReq(request);
				break;
			case JOIN_REQUEST:
				procJoinReq(request);
				break;
			case CHANGE_STATE_REQUEST:
				procChangeStateReq(request);
				break;
			case ECHO_REQUEST:
				procEchoReq(request);
				break;
			case WTP_EVENT_REQUEST:
				procWtpEvent(request);
				break;
			case LAYER3_ROAMING_CONFIG_RESPONSE:
				procL3RoamConfigResp();
				break;
			case IDP_QUERY_RESPONSE:
				procIdpQueryResp();
				break;
			case WTP_EVENT_CONTROL_RESPONSE:
				procWtpEventControlResp();
				break;
			case WTP_FILE_DOWNLOAD_RESPONSE:
				procFileDownloadResp();
				break;
			default:
				logger.error("processRequest", "Unknown Client Request from client " + linkIp);
				break;
		}
	}

	/**
	 * Process Discovery Request.
	 * <p>
	 * @param request -
	 */
	private void procDiscoveryReq(AhCapwapClientRequest request) {
		logger.debug("procDiscoveryReq", "["+linkIp+"]FSM size = " + fsmHash.size() + " when receiving Discovery Request.");

		boolean new_fsm = false;
		AhCapwapFsm fsm = fsmHash.get(linkIp);

		if (fsm == null) {
			// Check the format of serial number.
			if (!validateSerialNumber(m_serial_number)) {
				return;
			}

			synchronized (fsmHash) {
				String fsmKey = getOriginalFsmKey(m_serial_number);

				if (fsmKey == null) {
					if (m_capwap_version != CAPWAP_VERSION_V4) {
						logger.debug("procDiscoveryReq", "Create a new FSM for Capwap V2, Ip = "
								+ linkIp + ", SN = " + m_serial_number
								+ ", Hardware Version = " + m_hardware_version
								+ ", Software Version = " + m_software_version
								+ " when receiving Discovery Request");
					}

					fsm = new AhCapwapFsm(clientReqQue, udpHandler);
					fsm.setCapwapVer(m_capwap_version);
					fsm.setSerialNum(m_serial_number);
					fsm.setHwVer(m_hardware_version);
					fsm.setSwVer(m_software_version);
					new_fsm = true;
				} else {
					logger.debug("procDiscoveryReq", "Replace original FSM key["+fsmKey+"] with new FSM key["+linkIp+"].");

					// Replace old key with new key.
					fsm = fsmHash.remove(fsmKey);
					fsmHash.put(linkIp, fsm);
				}
			}
		}

		FsmState fsmState = fsm.capwapFsm(ClientReqType.DISCOVER, m_seq_num, request);

		if (m_capwap_version != CAPWAP_VERSION_V4) {
			if (new_fsm && fsmState == FsmState.IDLE) {
				synchronized (fsmHash) {
					fsmHash.put(linkIp, fsm);
				}
			}
		}
	}

	/**
	 * Process Join Request.
	 * <p>
	 * @param request -
	 */
	private void procJoinReq(AhCapwapClientRequest request) {
		AhCapwapFsm fsm = fsmHash.get(linkIp);

		if (fsm == null) {
			if (m_capwap_version != CAPWAP_VERSION_V4) {
				logger.debug("procJoinReq", "["+linkIp+"]Could not find FSM when receiving Join Request.");
			} else {
				// Check the serial number in join request
				// For v4 or higher of capwap version,
				// fsm is abled to created in join request not in discovery
				// request event.
				// So serial number checkage is also needed as done in discovery
				// request event for capawp v2.
				if (!validateSerialNumber(m_serial_number)) {
					return;
				}

				logger.debug("procJoinReq", "Create a new FSM for Capwap V4, Ip = "
						+ linkIp
						+ ", SN = " + m_serial_number
						+ ", Hardware Version = " + m_hardware_version
						+ ", Software Version = " + m_software_version
						+ " when receiving Join Request");

				fsm = new AhCapwapFsm(clientReqQue, udpHandler);
				fsm.setCapwapVer(m_capwap_version);

				synchronized (fsmHash) {
					fsmHash.put(linkIp, fsm);
				}
			}
		}

		if (fsm != null) {
			logger.debug("procJoinReq", "["+linkIp+"]FSM size = " + fsmHash.size() + " when receiving Join Request.");

			// Set useful attributes.
			fsm.setSerialNum(m_serial_number);
			fsm.setHwVer(m_hardware_version);
			fsm.setSwVer(m_software_version);
			fsm.setHostname(m_wtp_name);
			fsm.setLocation(m_location);
			fsm.setMac(m_mac_address);
			String ipv4 = AhDecoder.long2Ip(m_wtp_ipv4);
			fsm.setIp(ipv4);
			String netmask = (m_netmask != 0) ? AhDecoder.long2Ip(m_netmask) : "255.255.255.0";
			fsm.setNetmask(netmask);
			String gateway = (m_gateway != 0) ? AhDecoder.long2Ip(m_gateway) : "0.0.0.0";
			fsm.setGateway(gateway);
			fsm.setRegionCode(m_region_code);
			fsm.setCountryCode(m_country_code);
			fsm.setIpType(m_ip_type);
			fsm.setApType(m_ap_type);
			fsm.capwapFsm(ClientReqType.JOIN, m_seq_num, request);
		}
	}

	/**
	 * Process Change State Request.
	 * <p>
	 * @param request -
	 */
	private void procChangeStateReq(AhCapwapClientRequest request) {
		logger.debug("procChangeStateReq", "["+linkIp+"]FSM size = " + fsmHash.size() + " when receiving Change State Request.");

		AhCapwapFsm fsm = fsmHash.get(linkIp);

		if (fsm != null) {
			fsm.capwapFsm(ClientReqType.CHANGE_STATE, m_seq_num, request);
		} else {
			logger.debug("procChangeStateReq", "["+linkIp+"]Could not find FSM when receiving Change State Request.");
		}
	}

	/**
	 * Process Echo Request.
	 * <p>
	 * @param request -
	 */
	private void procEchoReq(AhCapwapClientRequest request) {
		logger.debug("procEchoReq", "["+linkIp+"]FSM size = " + fsmHash.size() + " when receiving Echo Request.");

		AhCapwapFsm fsm = fsmHash.get(linkIp);

		if (fsm != null) {
			fsm.capwapFsm(ClientReqType.ECHO, m_seq_num, request);
		} else {
			logger.debug("procEchoReq", "["+linkIp+"]Could not find FSM when receiving Echo Request.");
		}
	}

	/**
	 * Process WTP Event Control Response.
	 */
	private void procWtpEventControlResp() {
		logger.debug("procWtpEventCntlResp", "["+linkIp+"]FSM size = " + fsmHash.size() + " when receiving WTP Event Control Response.");

		AhCapwapFsm fsm = fsmHash.get(linkIp);

		if (fsm != null) {
			fsm.procWtpResp(WTP_EVENT_CONTROL_REQUEST);
		} else {
			logger.debug("procWtpEventControlResp", "["+linkIp+"]Could not find FSM when receiving WTP Event Control Response.");
		}
	}

	/**
	 * Process IDP Query Response.
	 */
	private void procIdpQueryResp() {
		logger.debug("procIdpQueryResp", "["+linkIp+"]FSM size = " + fsmHash.size() + " when receiving IDP Query Response.");

		AhCapwapFsm fsm = fsmHash.get(linkIp);

		if (fsm != null) {
			fsm.procWtpResp(IDP_QUERY_REQUEST);
		} else {
			logger.debug("procIdpQueryResp", "["+linkIp+"]Could not find FSM when receiving IDP Query Response.");
		}
	}

	/**
	 * Process Layer3 Roaming Config Response.
	 */
	private void procL3RoamConfigResp() {
		logger.debug("procL3RoamConfigResp", "["+linkIp+"]FSM size = " + fsmHash.size() + " when receiving Layer3 Roaming Config Response.");

		AhCapwapFsm fsm = fsmHash.get(linkIp);

		if (fsm != null) {
			fsm.procWtpResp(LAYER3_ROAMING_CONFIG_REQUEST);
		} else {
			logger.debug("procL3RoamConfigResp", "["+linkIp+"]Could not find FSM when receiving Layer3 Roaming Config Response.");
		}
	}

	/**
	 * Process File Download Response.
	 */
	private void procFileDownloadResp() {
		logger.debug("procFileDownloadResp", "["+linkIp+"]FSM size = " + fsmHash.size() + " when receiving File Download Response.");

		AhCapwapFsm fsm = fsmHash.get(linkIp);

		if (fsm != null) {
			fsm.procWtpResp(WTP_FILE_DOWNLOAD_REQUEST);
		} else {
			logger.debug("procFileDownloadResp", "["+linkIp+"]Could not find FSM when receiving File Download Response.");
		}
	}

	/**
	 * Process WTP Event.
	 * <p>
	 * @param request  Capwap client request.
	 */
	private void procWtpEvent(AhCapwapClientRequest request) {
		AhCapwapFsm fsm = fsmHash.get(linkIp);

		if (fsm != null) {
			if (fsm.checkStatus(m_event_type)) {
				// See the capwap protocol section 4.3 about flag 'F' and 'L'.
				fsm.procWtpEvent(m_event_type, m_seq_num, request, m_f, m_l, m_frag_id, m_frag_offset, fragment);
			}
		} else {
			logger.debug("procWtpEvent", "["+linkIp+"]Could not find FSM when receiving WTP Event Request.");
		}
	}

}