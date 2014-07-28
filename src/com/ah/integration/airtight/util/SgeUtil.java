package com.ah.integration.airtight.util;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.StringTokenizer;

import com.airtight.spectraguard.api.dataobjects.devices.WiFiInterface;
import com.airtight.spectraguard.api.exceptions.APIException;
import com.airtight.spectraguard.api.exceptions.FailedRecordDetails;
import com.airtight.spectraguard.api.exceptions.FailedRecords;

import com.ah.be.log.HmLogConst;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
//import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class SgeUtil {

	private static final Tracer log = new Tracer(SgeUtil.class, HmLogConst.M_SGE);

	public static String getUnformattedMac(String macAddress) {
		return macAddress.replace(":", "").toUpperCase();
	}

	public static String getSgeFormatMac(String macAddress) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0 ; i < macAddress.length(); i++) {
			if (i != 0 && i % 2 == 0) {
				sb.append(":");
			}

			sb.append(macAddress.charAt(i));
		}

		return sb.toString();
	}

	public static String getNetworkTag(String ipAddress, String netmask) {
		StringBuilder nwTag = new StringBuilder();

		for (StringTokenizer ipToken = new StringTokenizer(ipAddress, "."), nmToken = new StringTokenizer(netmask, "."); ipToken.hasMoreTokens() && nmToken.hasMoreTokens();) {
			String ipItem = ipToken.nextToken();
			int intIpItem = Integer.parseInt(ipItem);

			String nmItem = nmToken.nextToken();
			int intNmItem = Integer.parseInt(nmItem);

			int intNwTagItem = intIpItem & intNmItem;
			nwTag.append(intNwTagItem);

			if (ipToken.hasMoreTokens() && nmToken.hasMoreTokens()) {
				nwTag.append(".");
			}
		}

		int intNm = AhEncoder.netmask2int(netmask);

		nwTag.append("/").append(intNm);

		return nwTag.toString();
	}

	/*-
	public static String getNetworkTag(String ipAddress, String netmask) {
		int intIp = AhEncoder.ip2Int(ipAddress);
		int intNm = AhEncoder.ip2Int(netmask);
		int intNwTag = intIp & intNm;

		String nwTagPrefix = AhDecoder.int2IP(intNwTag);
		int nwTagSuffix = AhEncoder.netmask2int(netmask);

		return nwTagPrefix + "/" + nwTagSuffix;
	}*/

	public static String getDeviceName(String vendorName, String macAddress) {
		String sgeFormatMac = macAddress.contains(":") ? macAddress : getSgeFormatMac(macAddress);

		return !vendorName.trim().isEmpty() ? vendorName + "_" + sgeFormatMac.substring(9) : sgeFormatMac;
	}

	public static int getProtocolByChannel(int channel) {
		return channel < 20 ? WiFiInterface.PROTOCOL_BG : WiFiInterface.PROTOCOL_A;
	}

	public static int getProtocolByFrequency(int frequency) {
		return frequency < 2500 ? WiFiInterface.PROTOCOL_BG : WiFiInterface.PROTOCOL_A;
	}

	public static void printErrorDetail(APIException e) {
		log.error("printErrorDetail", "Error Code: " + e.getErrorCode() + "; Error Message: " + e.getMessage());
		Collection<FailedRecords> failedRecordsList = e.getFailedRecords();

		if (failedRecordsList != null) {
			for (FailedRecords failedRecords : failedRecordsList) {
				Object errorObject = failedRecords.getErrorObject();
				Collection<FailedRecordDetails> failedRecordDetailsList = failedRecords.getErrorString();

				for (FailedRecordDetails failedRecordDetails : failedRecordDetailsList) {
					int errorCode = failedRecordDetails.getErrorCode();
					String errorMessage = failedRecordDetails.getErrorMessage();
					log.error("printErrorDetail", "Error Object: " + errorObject + "; Error Code: " + errorCode + "; Error Message: " + errorMessage);
				}
			}
		}
	}

	public static String getUserMsg(APIException e) {
		String userMsg;
		Throwable cause = e.getCause();

		if (cause != null) {
			if (cause instanceof NoRouteToHostException) {
				userMsg = MgrUtil.getUserMessage("error.airtight.network.no.route");
			} else if (cause instanceof UnknownHostException) {
				userMsg = MgrUtil.getUserMessage("error.airtight.host.unknown");
			} else {
				userMsg = getUserMsgByErrorMsg(cause.getMessage());
			}
		} else {
			userMsg = getUserMsgByErrorMsg(e.getMessage());
		}

		return userMsg;
	}

	private static String getUserMsgByErrorMsg(String errorMsg) {
		String userMsg;

		if (errorMsg != null && !errorMsg.isEmpty()) {
			String errorMsgToLowercase = errorMsg.toLowerCase();

			if (errorMsgToLowercase.contains("session")) {
				userMsg = errorMsgToLowercase.contains("expire")
						? MgrUtil.getUserMessage("error.airtight.session.expired")
						: MgrUtil.getUserMessage("error.airtight.session.invalid");
			} else if (errorMsgToLowercase.contains("cookie-invalid")) {
				userMsg = MgrUtil.getUserMessage("error.airtight.cookie.invalid");
			} else if (errorMsgToLowercase.contains("network is unreachable")) {
				userMsg = MgrUtil.getUserMessage("error.airtight.network.unreachable");
			} else if (errorMsgToLowercase.contains("no route to host")) {
				userMsg = MgrUtil.getUserMessage("error.airtight.network.no.route");
			} else if (errorMsgToLowercase.contains("connection refused") || errorMsgToLowercase.contains("invalid stream header")) {
				userMsg = MgrUtil.getUserMessage("error.airtight.connection.request.refused");
			} else if (errorMsgToLowercase.contains("connection timed out")) {
				userMsg = MgrUtil.getUserMessage("error.airtight.connection.request.timeout");
			} else if (errorMsgToLowercase.contains("authentication failed due to invalid credentials")) {
				userMsg = MgrUtil.getUserMessage("error.airtight.login.failed");
			} else {
				userMsg = MgrUtil.getUserMessage("error.airtight.error.withReason", errorMsg);
			}
		} else {
			userMsg = MgrUtil.getUserMessage("error.airtight.error.unknown");
		}

		return userMsg;
	}

}