package com.ah.be.config.hiveap;

import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.event.BeAbortEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;

public class UpdateUtil {

	public static String getCommonResponseMessage(short resultType) {
		String msg = "";
		switch (resultType) {
		case BeCommunicationConstant.RESULTTYPE_NOFSM:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.capwap.server.nofsm");
//			msg = NmsUtil.getUserMessage("error.capwap.server.nofsm.staged");
			break;
		case BeCommunicationConstant.RESULTTYPE_FSMNOTRUN:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.capwap.server.fsmnotrun");
			break;
		case BeCommunicationConstant.RESULTTYPE_UNKNOWNMSG:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.capwap.server.unknownmessage",
					new String[] { String.valueOf(resultType) });
			break;
		case BeCommunicationConstant.RESULTTYPE_TIMEOUT:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.capwap.server.timeout");
			break;
		case BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.hiveAp.update.timeout");
			break;
		case BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.capwap.server.disconnect");
			break;
		case BeCommunicationConstant.RESULTTYPE_MESSAGELENEXCEEDLIMIT:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.capwap.server.messageOverflow");
			break;
		case BeCommunicationConstant.RESULTTYPE_RESOUCEBUSY:
			// request is failed;
			msg = NmsUtil.getUserMessage("error.cli.obj.onRequest.message");
			break;
		}
		return msg;
	}
	
	public static boolean isStagedStatus(short resultType){
		switch (resultType) {
		case BeCommunicationConstant.RESULTTYPE_NOFSM:
		case BeCommunicationConstant.RESULTTYPE_FSMNOTRUN:
		case BeCommunicationConstant.RESULTTYPE_UNKNOWNMSG:
		case BeCommunicationConstant.RESULTTYPE_TIMEOUT:
		case BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT:
		case BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE:
			return true;
		}
		return false;
	}

	public static String getCliResultMessage(short updateType,
			BeCapwapCliResultEvent event) {
		byte result = event.getCliResult();
		String msg;
		switch (result) {
		case BeCommunicationConstant.CLIRESULT_SUCCESS:
			msg = getCustomizedSucDesc(updateType);
			break;
		case BeCommunicationConstant.CLIRESULT_FAIL:
			String errorCli = event.getErrorCli();
			String errorMsg = event.getHiveOSErrorMessage();
			msg = getCustomizedFailDesc(errorCli, errorMsg);
			break;
		case BeCommunicationConstant.CLIRESULT_QUEUEFULL:
			msg = NmsUtil.getUserMessage("error.hiveAp.update.overflow");
			break;
		default:
			msg = NmsUtil.getUserMessage("error.hiveAp.update.unknown",
					new String[] { String.valueOf(result) });
		}
		return msg;
	}

	public static String getCustomizedSucDesc(short updateType) {
		switch (updateType) {
		case UpdateParameters.AH_DOWNLOAD_SCRIPT:
			return NmsUtil.getUserMessage("info.hiveAp.update.config.result");
		case UpdateParameters.AH_DOWNLOAD_IMAGE:
			return NmsUtil.getUserMessage("info.hiveAp.update.image.result");
		case UpdateParameters.AH_DOWNLOAD_L7_SIGNATURE:
			return NmsUtil.getUserMessage("info.hiveAp.update.l7.signature.result");
		case UpdateParameters.AH_DOWNLOAD_CWP:
			return NmsUtil.getUserMessage("info.hiveAp.update.cwp.result");
		case UpdateParameters.AH_DOWNLOAD_RADIUS_CERTIFICATE:
			return NmsUtil.getUserMessage("info.hiveAp.update.cert.result");
		case UpdateParameters.AH_DOWNLOAD_VPN_CERTIFICATE:
			return NmsUtil.getUserMessage("info.hiveAp.update.vpn.result");
		case UpdateParameters.AH_DOWNLOAD_BOOTSTRAP:
			return NmsUtil
					.getUserMessage("info.hiveAp.update.bootstrap.result");
		case UpdateParameters.AH_DOWNLOAD_COUNTRY_CODE:
			return NmsUtil
					.getUserMessage("info.hiveAp.update.countryCode.result");
		case UpdateParameters.AH_DOWNLOAD_POE:
			return NmsUtil.getUserMessage("info.hiveAp.update.poe.result");
		case UpdateParameters.AH_DOWNLOAD_OS_DETECTION:
			return NmsUtil.getUserMessage("info.hiveAp.update.osdetection.result");
		case UpdateParameters.AH_DOWNLOAD_NET_DUMP:
			return NmsUtil.getUserMessage("info.hiveAp.update.netdump.result");
		case UpdateParameters.AH_DOWNLOAD_PSK:
			return NmsUtil.getUserMessage("info.hiveAp.update.psk.result");
		case UpdateParameters.AH_DOWNLOAD_OUTDOORSTTINGS:
			return NmsUtil.getUserMessage("info.hiveAp.update.outdoorSettings.result");
		case UpdateParameters.AH_DOWNLOAD_CLOUDAUTH_CERTIFICATE:
			return NmsUtil.getUserMessage("info.hiveAp.update.cloudauthca.result");
		case UpdateParameters.AH_DOWNLOAD_DS_CONFIG:
			return NmsUtil.getUserMessage("info.hiveAp.update.config.result");
		case UpdateParameters.AH_DOWNLOAD_DS_USER_CONFIG:
			return NmsUtil.getUserMessage("info.hiveAp.update.psk.result");
		case UpdateParameters.AH_DOWNLOAD_DS_AUDIT_CONFIG:
			return NmsUtil.getUserMessage("info.hiveAp.update.ds.auditCfg.result");
		case UpdateParameters.AH_DOWNLOAD_REBOOT:
			return NmsUtil.getUserMessage("geneva_06.info.hiveAp.update.reboot.cli.successful");
		default:
			return NmsUtil.getUserMessage("info.hiveAp.update.general.result");
		}
	}

	public static String getCustomizedFailDesc(String errorCli, String errorMsg) {
		// hide user name and password
		if (null != errorCli) {
			errorCli = errorCli.replace(NmsUtil.getHMScpUser(), "******")
					.replace(NmsUtil.getHMScpPsd(), "******");
		}
		String errors = NmsUtil.getUserMessage("error.hiveAp.update.result",
				new String[] { errorCli });
		if (null != errorMsg && !"".equals(errorMsg)) {
			errors = errors + "\n" + errorMsg;
		}
		return errors;
	}

	public static int getUploadProtocolType(String clis) {
		int protocol = BeAbortEvent.ABORTTYPE_SCP_IMAGEDOWNLOAD;
		if (null != clis && clis.contains("tftp://")) {
			protocol = BeAbortEvent.ABORTTYPE_TFTP_IMAGEDOWNLOAD;
		}
		return protocol;
	}
	
	public static boolean getUploadStagedType(short resultType){
		boolean result;
		switch (resultType) {
			case BeCommunicationConstant.RESULTTYPE_NOFSM:
			case BeCommunicationConstant.RESULTTYPE_TIMEOUT:
			case BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT:
				result = true;
				break;
			default:
				result = false;
		}
		return result;
	}

}