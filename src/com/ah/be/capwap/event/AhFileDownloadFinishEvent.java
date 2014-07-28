/**
 *@filename		AhFileDownloadFinishEvent.java
 *@version
 *@author		Francis
 *@createtime	2007-10-10 02:43:13 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Inc.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.capwap.event;

// java import
import java.util.StringTokenizer;

// aerohive import
import com.ah.be.capwap.AhCapwapDecodeException;
import com.ah.util.coder.AhDecoder;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhFileDownloadFinishEvent extends AhWtpEvent {

	private static final long	serialVersionUID	= 1L;

	public static final int AH_FILE_DOWNLOAD_SUCC = 0;
	public static final int AH_FILE_DOWNLOAD_FAIL = 1;
	public static final int AH_FILE_DOWNLOAD_QUEUE_FULL = 2;
	public static final String AH_CLI_SPLIT_SYMBOL = "\n";

	private int downloadRet;// Download result.
	private String[] failClis ;// Failure executive clis.

	public AhFileDownloadFinishEvent() {
		super(FILE_DOWNLOAD_RESULT);
	}

	public AhFileDownloadFinishEvent(byte[] packet) {
		super(AP_TYPE_CHANGE, packet);
	}

	public int getDownloadRet() {
		return downloadRet;
	}

	public String[] getFailClis() {
		return failClis;
	}

	/**
	 * File Download Result Event (Defined by Aerohive, msg type : 6102).
	 * <p>
	 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                           Timestamp                           |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |     Result    |        Error Cli Length       | Error Clis...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * <p>
	 * Result : 0 - success.
	 *          1 - fail.
	 *          2 - download queue is full.
	 * Error Cli Length : The attribute is exist only when the result is equals to 1.
	 * Error Cli : The attribute is exist only when the result is equals to 1.
	 * @throws AhCapwapDecodeException  if error occurs in parsing capwap option packet.
	 */
	@Override
	public void parsePacket() throws AhCapwapDecodeException {
		if (packet == null) {
			throw new AhCapwapDecodeException("Event packet is required.");
		}

		if (fsm == null) {
			throw new AhCapwapDecodeException("Fsm is required.");
		}

		if (packet.length < 1) {
			throw new AhCapwapDecodeException("["+fsm.getIp()+"]Error [File Download Result] option length, the right value should be no less than 1.");
		}

		try {
			downloadRet = packet[0];

			if (downloadRet == AH_FILE_DOWNLOAD_FAIL) {
				int givenLen = AhDecoder.bytes2int(packet, 1, 2);
				int recvLen = packet.length - 3;

				if (givenLen != recvLen) {
					throw new AhCapwapDecodeException("["+fsm.getIp()+"]Error [File Download Result] element length, the given element[Error Clis] len = "+givenLen+", but the received len = " + recvLen);
				}

				String clis = new String(packet, 3, givenLen, "iso-8859-1");
				StringTokenizer token = new StringTokenizer(clis, "\n");
				int count = token.countTokens();

				if (count > 0) {
					failClis = new String[count];

					for (int i = 0; i < count; i++) {
						failClis[i] = token.nextToken();
					}
				}
			}
		} catch (Exception e) {
			throw new AhCapwapDecodeException("Parse File Download Result option failed", e);
		}
	}

}