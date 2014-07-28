/**
 *@filename		AhFileDownloadProgressEvent.java
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

// aerohive import
import com.ah.be.capwap.AhCapwapDecodeException;
import com.ah.util.coder.AhDecoder;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhFileDownloadProgressEvent extends AhWtpEvent {

	private static final long	serialVersionUID	= 1L;

	public static final int AH_DOWNLOAD_PROCESSING = 1;
	public static final int AH_DOWNLOAD_FINISH = 2;

	private int downloadType;
	private int status;// The status of file downloading.
	private int finishSize;// The file size which has been download successfully.

	public AhFileDownloadProgressEvent() {
		super(FILE_DOWNLOAD_PROGRESS);
	}

	public AhFileDownloadProgressEvent(byte[] packet) {
		super(AP_TYPE_CHANGE, packet);
	}

	public int getDownloadType() {
		return downloadType;
	}

	public int getStatus() {
		return status;
	}

	public int getFinishSize() {
		return finishSize;
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
	 * @throws AhCapwapDecodeException  if error occurs in parsing capwap option message.
	 */
	@Override
	public void parsePacket() throws AhCapwapDecodeException {
		if (packet == null) {
			throw new AhCapwapDecodeException("Event packet is required.");
		}

		if (fsm == null) {
			throw new AhCapwapDecodeException("Fsm is required.");
		}

		if (packet.length != 6) {
			throw new AhCapwapDecodeException("["+fsm.getIp()+"]The file download progress option length is wrong, right value is 6.");
		}

		downloadType = packet[0];
		status = packet[1];
		finishSize = AhDecoder.bytes2int(packet, 2, 4);
	}

}