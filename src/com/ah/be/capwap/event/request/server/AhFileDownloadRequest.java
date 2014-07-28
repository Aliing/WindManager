/**
 *@filename		AhFileDownloadRequest.java
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
package com.ah.be.capwap.event.request.server;

// java import
import java.io.UnsupportedEncodingException;

// aerohive import
import com.ah.be.capwap.AhCapwapDecodeException;
import com.ah.be.capwap.AhCapwapEncodeException;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhFileDownloadRequest extends AhCapwapServerRequest {

	private static final long	serialVersionUID	= 1L;

	// Download Type
	public static final int AH_DOWNLOAD_IMAGE = 1;
	public static final int AH_DOWNLOAD_SCRIPT = 2;
	public static final int AH_DOWNLOAD_BOOTSTRAP = 3;
	public static final int AH_DOWNLOAD_CWP_KEY = 4;
	public static final int AH_DOWNLOAD_CWP_PAGE = 5;
	public static final int AH_DOWNLOAD_RADIUS_CERTIFICATE = 6;

	public static EnumItem[] UPDATE_TYPE = MgrUtil.enumItems(
			"enum.hiveAp.update.", new int[] { AH_DOWNLOAD_IMAGE,
					AH_DOWNLOAD_SCRIPT, AH_DOWNLOAD_BOOTSTRAP,
					AH_DOWNLOAD_CWP_KEY, AH_DOWNLOAD_CWP_PAGE,
					AH_DOWNLOAD_RADIUS_CERTIFICATE });

	// Download Result
	public static final int AH_DOWNLOAD_SUCC = 0;
	public static final int AH_DOWNLOAD_FAIL = 1;
	public static final int AH_DOWNLOAD_CONGEST = 2;
	public static final int AH_DOWNLOAD_TIMEOUT = 3;

	private int downloadType;
	private String[] clis;

	public AhFileDownloadRequest() {
		super(WTP_FILE_DOWNLOAD_REQUEST);
	}

	public AhFileDownloadRequest(int downloadType) {
		super(WTP_FILE_DOWNLOAD_REQUEST);
		this.downloadType = downloadType;
	}

	public AhFileDownloadRequest(int downloadType, String serialNum) {
		super(WTP_FILE_DOWNLOAD_REQUEST, serialNum);
		this.downloadType = downloadType;
	}

	public AhFileDownloadRequest(int downloadType, String serialNum, String[] clis) {
		this(downloadType, serialNum);
		this.clis = clis;
	}

	public int getDownloadType() {
		return downloadType;
	}

	public void setDownloadType(int downloadType) {
		this.downloadType = downloadType;
	}

	public String[] getClis() {
		return clis;
	}

	public void setClis(String[] clis) {
		this.clis = clis;
	}

	@Override
	public byte[] buildPacket() throws AhCapwapEncodeException {
		if (clis == null || clis.length == 0) {
			throw new AhCapwapEncodeException("cli(s) is(are) required.");
		}

		String s = "";

		for (String cli : clis) {
			s += cli;
		}

		try {
			reqPacket = s.getBytes("iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			reqPacket = s.getBytes();
		}

		return reqPacket;
	}

	@Override
	public void parsePacket() throws AhCapwapDecodeException {

	}

	@Override
	public String getRequestName() {
		String reqName;

		switch (downloadType) {
			case AH_DOWNLOAD_IMAGE:
				reqName = "Img Download";
				break;
			case AH_DOWNLOAD_SCRIPT:
				reqName = "Config Download";
				break;
			case AH_DOWNLOAD_BOOTSTRAP:
				reqName = "Bootstrap Download";
				break;
			case AH_DOWNLOAD_CWP_KEY:
				reqName = "CWP Key Download";
				break;
			case AH_DOWNLOAD_CWP_PAGE:
				reqName = "CWP Page Download";
				break;
			case AH_DOWNLOAD_RADIUS_CERTIFICATE:
				reqName = "Radius Certificate Download";
				break;
			default:
				reqName = "File Download";
		}

		return reqName;
	}

}