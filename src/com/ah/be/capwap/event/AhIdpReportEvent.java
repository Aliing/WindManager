/**
 *@filename		AhIdpReportEvent.java
 *@version
 *@author		Francis
 *@createtime	2007-8-15 02:53:19 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Inc.
 *All right reserved.
 */
package com.ah.be.capwap.event;

// java import
//import java.nio.ByteBuffer;

// aerohive import
import com.ah.be.capwap.AhCapwapDecodeException;
//import com.ah.util.coder.AhDecoder;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhIdpReportEvent extends AhWtpEvent {

	private static final long	serialVersionUID	= 1L;

	public static final int		AH_IDP_QUERY		= 0;
	public static final int		AH_IDP_REPORT		= 1;
	public static final int		AH_AP_REMOVED		= 1;
	public static final int		AH_AP_IN_NETWORK	= 1;

	public AhIdpReportEvent() {
		super(IDP_STATISTICS);
	}

	public AhIdpReportEvent(byte[] msg) {
		super(IDP_STATISTICS, msg);
	}

	/**
	 * <p>
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |  IDP Msg Type |          Data Length          | + multi following IDP datas.
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * <p>
	 * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |   Single IDP Message Length   |           Remote Id
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *                             Remote Id                           |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |   If Index    |  Remove Flag  |    IDP Type   |    Channel    |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |     RSSI      |In Network Flag|  Station Type |  Station Data
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *    Station Data |           Compliance          |  SSID Length  |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                             SSID...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * <p>
	 * @throws AhCapwapDecodeException  if error occurs in parsing capwap option message.
	 */
	@Override
	public void parsePacket() throws AhCapwapDecodeException {
//		if (packet == null) {
//			throw new AhCapwapDecodeException("Event packet is required.");
//		}
//
//		if (packet.length <= 0) {
//			throw new AhCapwapDecodeException("["+fsm.getIp()+"]The IDP option length is wrong, the right value should be no less than 1.");
//		}
//
//		if (fsm == null) {
//			throw new AhCapwapDecodeException("Fsm is required.");
//		}
//
//		int idp_msg_type;
//		int total_msg_len;
//		int single_msg_len;
//		int if_index;
//		int idp_type;
//		int channel;
//		int rssi;
//		int in_network_flag;
//		int station_type;
//		int station_data;
//		int compliance;
//		int remove_flag;
//		int ssid_len;
//		String remote_id;
//		String ssid;
//		String reportMac = fsm.getMac();
//		String hostname = fsm.getHostname();
//		ByteBuffer buf = ByteBuffer.allocate(packet.length);
//		buf.put(packet);
//		buf.flip();
//
//		try {
//			idp_msg_type = buf.get();
//			// System.err.println("IDP Msg Type = " + idp_msg_type);
//			total_msg_len = AhDecoder.bytes2int(buf, 2);
//			// System.err.println("IDP Msg Total Length = " + total_msg_len);
//
//			if (total_msg_len > 0) {
//				while (buf.hasRemaining()) {
//					single_msg_len = AhDecoder.bytes2int(buf, 2);
//					// System.err.println("Single IDP Msg Length = "
//					// + single_msg_len);
//					remote_id = AhDecoder.bytes2hex(buf, 6);
//					// System.err.println("Remote Id = " + remote_id);
//					if_index = buf.get();
//					// System.err.println("If Index = " + if_index);
//					remove_flag = buf.get();
//					// System.err.println("Remove Flag = " + remove_flag);
//					idp_type = buf.get();
//					// System.err.println("IDP Type = " + idp_type);
//					channel = buf.get();
//					// System.err.println("Channel = " + channel);
//					rssi = buf.get();
//					// System.err.println("RSSI = " + rssi);
//					if (rssi < 0)
//						rssi = 0;
//					in_network_flag = buf.get();
//					// System.err.println("In Network Flag = " +
//					// in_network_flag);
//					station_type = buf.get();
//					// System.err.println("Station Type = " + station_type);
//					station_data = AhDecoder.bytes2int(buf, 2);
//					// System.err.println("Station Data = " + station_data);
//					compliance = AhDecoder.bytes2int(buf, 2);
//					// System.err.println("Compliance = " + compliance);
//					ssid_len = buf.get();
//					// System.err.println("SSID Length = " + ssid_len);
//					ssid = AhDecoder.bytes2String(buf, ssid_len);
//					// System.err.println("SSID = " + ssid);
//				}
//			}
//		} catch (Exception e) {
//			throw new AhCapwapDecodeException("Parse IDP msg failed", e);
//		}
	}

}