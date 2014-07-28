package com.ah.be.communication;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.event.BeApplicationInfoEvent;
import com.ah.be.communication.event.BeCapwapPayloadEvent;
import com.ah.be.communication.event.BeCapwapPayloadResultEvent;
import com.ah.be.communication.event.portal.BeCreateUserEvent;
import com.ah.be.communication.event.portal.BeModifyUserEvent;
import com.ah.be.communication.event.portal.BeModifyVHMEvent;
import com.ah.be.communication.event.portal.BePortalHMPayloadResultEvent;
import com.ah.be.communication.event.portal.BeQueryRevertHmolResult;
import com.ah.be.communication.event.portal.BeRemoveUserEvent;
import com.ah.be.communication.event.portal.BeReportVhmMovingStatusEvent;
import com.ah.be.communication.event.portal.BeReportVhmRevertStatusEvent;
import com.ah.be.communication.event.portal.BeReportVhmUpgradeStatusEvent;
import com.ah.be.communication.event.portal.BeQueryRevertHmolEvent;
import com.ah.be.communication.event.portal.BeQueryUpgradeAvailHmolsEvent;
import com.ah.be.communication.event.portal.BeQueryUpgradeAvailHmolsResult;
import com.ah.be.communication.event.portal.BeUpdateApplicationInfoEvent;
import com.ah.be.communication.mo.HmolInfo;
import com.ah.be.communication.mo.UserInfo;
import com.ah.be.communication.mo.VhmInfo;
import com.ah.be.communication.mo.VhmRumStatus;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.HM_License;
import com.ah.be.license.LicenseInfo;

public class RemotePortalOperationRequest {

	private final static Log log = LogFactory.getLog("commonlog.RemotePortalOperationRequest");

	private static String portalMacAddress = HmBeCommunicationUtil.getPortalMac();

	/**
	 * request avail upgrade destination HMOLs
	 * 
	 * @param vhmName
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> requestUpgradeVhm(String vhmName) throws Exception {
		String operation = "requestUpgradeVhm";
		checkPortalConnect();
		try {
			BeQueryUpgradeAvailHmolsEvent request = new BeQueryUpgradeAvailHmolsEvent();
			request.setApMac(portalMacAddress);
			// set attributes
			request.setVhmName(vhmName);

			// set sequence number and build packet
			request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			request.buildPacket();

			BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(request);
			parseSyncResponseEvent(operation, response);

			BeQueryUpgradeAvailHmolsResult resp = (BeQueryUpgradeAvailHmolsResult) response;

			return resp.getDestHmols();
		} catch (BeCommunicationEncodeException e) {
			log.error(operation + " catch BeCommunicationEncodeException!", e);
			throw new Exception("communication packet error!");
		} catch (Exception e) {
			log.error(operation + " failed!", e);
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * request avail revert destination HMOL
	 * 
	 * @param vhmName
	 * @return
	 * @throws Exception
	 */
	public static HmolInfo requestRevertVhm(String vhmName) throws Exception {
		String operation = "requestRevertVhm";
		checkPortalConnect();
		try {
			BeQueryRevertHmolEvent request = new BeQueryRevertHmolEvent();
			request.setApMac(portalMacAddress);
			// set attributes
			request.setVhmName(vhmName);

			// set sequence number and build packet
			request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			request.buildPacket();

			BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(request);
			parseSyncResponseEvent(operation, response);

			BeQueryRevertHmolResult resp = (BeQueryRevertHmolResult) response;

			return resp.getDestHmol();
		} catch (BeCommunicationEncodeException e) {
			log.error(operation + " catch BeCommunicationEncodeException!", e);
			throw new Exception("communication packet error!");
		} catch (Exception e) {
			log.error(operation + " failed!", e);
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * report upgrade status
	 * 
	 * @param vhmUpgradingStatuses
	 * @throws Exception
	 */
	public static void reportVhmUpgradeStatus(VhmRumStatus upgradeStatuses) throws Exception {
		String operation = "reportVhmUpgradeStatus";
		checkPortalConnect();
		try {
			BeReportVhmUpgradeStatusEvent request = new BeReportVhmUpgradeStatusEvent();
			request.setApMac(portalMacAddress);
			// set attributes
			request.setUpgradeStatus(upgradeStatuses);

			// set sequence number and build packet
			request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			request.buildPacket();

			BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(request);
			parseSyncResponseEvent(operation, response);
		} catch (BeCommunicationEncodeException e) {
			log.error(operation + " catch BeCommunicationEncodeException!", e);
			throw new Exception("communication packet error!");
		} catch (Exception e) {
			log.error(operation + " failed!", e);
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * report upgrade status
	 * 
	 * @param vhmUpgradingStatuses
	 * @throws Exception
	 */
	public static void reportVhmRevertStatus(VhmRumStatus revertStatus) throws Exception {
		String operation = "reportVhmUpgradeStatus";
		checkPortalConnect();
		try {
			BeReportVhmRevertStatusEvent request = new BeReportVhmRevertStatusEvent();
			request.setApMac(portalMacAddress);
			// set attributes
			request.setRevertStatus(revertStatus);

			// set sequence number and build packet
			request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			request.buildPacket();

			BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(request);
			parseSyncResponseEvent(operation, response);
		} catch (BeCommunicationEncodeException e) {
			log.error(operation + " catch BeCommunicationEncodeException!", e);
			throw new Exception("communication packet error!");
		} catch (Exception e) {
			log.error(operation + " failed!", e);
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * report move status
	 * 
	 * @param moveStatuses
	 * @throws Exception
	 */
	public static void reportVhmMovingStatus(VhmRumStatus moveStatus) throws Exception {
		String operation = "reportVhmMovingStatus";
		checkPortalConnect();
		try {
			BeReportVhmMovingStatusEvent request = new BeReportVhmMovingStatusEvent();
			request.setApMac(portalMacAddress);
			// set attributes
			request.setMoveStatus(moveStatus);

			// set sequence number and build packet
			request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			request.buildPacket();

			BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(request);
			parseSyncResponseEvent(operation, response);
		} catch (BeCommunicationEncodeException e) {
			log.error(operation + " catch BeCommunicationEncodeException!", e);
			throw new Exception("communication packet error!");
		} catch (Exception e) {
			log.error(operation + " failed!", e);
			throw new Exception(e.getMessage());
		}
	}

	public static void reportHmolInfo(HmolInfo hmInfo) throws Exception {
	}

	public static void createVhmUser(UserInfo userInfo) throws Exception {
		String operation = "createVhmUser";
		checkPortalConnect();
		try {
			BeCreateUserEvent request = new BeCreateUserEvent();
			request.setApMac(portalMacAddress);
			// set attributes
			request.setUser(userInfo);

			// set sequence number and build packet
			request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			request.buildPacket();

			BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(request);
			parseSyncResponseEvent(operation, response);
		} catch (BeCommunicationEncodeException e) {
			log.error(operation + " catch BeCommunicationEncodeException!", e);
			throw new Exception("communication packet error!");
		} catch (Exception e) {
			log.error(operation + " failed!", e);
			throw new Exception(e.getMessage());
		}
	}

	private static void checkPortalConnect() throws Exception {
		portalMacAddress = HmBeCommunicationUtil.getPortalMac();

		if (portalMacAddress == null) {
			throw new Exception("no portal was found!");
		}

		if (!HmBeCommunicationUtil.isConnectedToPortal()) {
			throw new Exception("portal is disconnected!");
		}
	}

	public static void modifyVhmUser(UserInfo userInfo) throws Exception {
		String operation = "modifyVhmUser";
		checkPortalConnect();
		try {
			BeModifyUserEvent request = new BeModifyUserEvent();
			request.setApMac(portalMacAddress);
			// set attributes
			request.setUser(userInfo);

			// set sequence number and build packet
			request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			request.buildPacket();

			BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(request);
			parseSyncResponseEvent(operation, response);
		} catch (BeCommunicationEncodeException e) {
			log.error(operation + " catch BeCommunicationEncodeException!", e);
			throw new Exception("communication packet error!");
		} catch (Exception e) {
			log.error(operation + " failed!", e);
			throw new Exception(e.getMessage());
		}
	}

	public static void removeVhmUser(String vhmName, String username) throws Exception {
		String operation = "removeVhmUser";
		checkPortalConnect();
		try {
			BeRemoveUserEvent request = new BeRemoveUserEvent();
			request.setApMac(portalMacAddress);
			// set attributes
			request.setVhmName(vhmName);
			request.setUserName(username);

			// set sequence number and build packet
			request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			request.buildPacket();

			BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(request);
			parseSyncResponseEvent(operation, response);
		} catch (BeCommunicationEncodeException e) {
			log.error(operation + " catch BeCommunicationEncodeException!", e);
			throw new Exception("communication packet error!");
		} catch (Exception e) {
			log.error(operation + " failed!", e);
			throw new Exception(e.getMessage());
		}
	}

	private static void parseSyncResponseEvent(String operation, BeCommunicationEvent response)
			throws Exception {
		if (response == null) {
			log.error("RemotePortalOperationRequest " + operation + " failed! No response object.");
			throw new Exception("Communication time out.");
		}

		switch (response.getMsgType()) {
		case BeCommunicationConstant.MESSAGETYPE_CAPWAPPAYLOADRSP: {
			log.error("RemotePortalOperationRequest " + operation + " communication error, result="
					+ response.getResult());
			throw new Exception("Communication error, result=" + response.getResult());
		}

		case BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPPAYLOADRESULT: {
			BeCapwapPayloadResultEvent resultEvent = (BeCapwapPayloadResultEvent) response;
			if (resultEvent.getQueryType() != BeCapwapPayloadEvent.QUERYTYPE_PORTALANDHM) {
				log
						.error("RemotePortalOperationRequest " + operation
								+ " receive invalid result event, query type="
								+ resultEvent.getQueryType());
				throw new Exception("Communication error, receive invalid result event.");
			}

			BePortalHMPayloadResultEvent portalResult = (BePortalHMPayloadResultEvent) resultEvent;
			if (!portalResult.isSuccess()) {
				log.info("RemotePortalOperationRequest " + operation
						+ " receive result event, error message=" + portalResult.getErrorMessage());
				throw new Exception(portalResult.getErrorMessage());
			} else {
				log.info("RemotePortalOperationRequest " + operation
						+ " receive success result event.");
			}

			break;
		}

		default: {
			log.error("RemotePortalOperationRequest " + operation
					+ " receive invalid response event, message type=" + response.getMsgType());
			throw new Exception("Invalid response event.");
		}
		}
	}

	/**
	 * Update license information in portal for whole HiveManager
	 * 
	 *@param LicenseInfo
	 */
	public static void updateApplicationInfo(LicenseInfo newLicenseInfo) {
		try {
			if (!NmsUtil.isHostedHMApplication()) {
				return;
			}
			
			// send app info to capwap
			byte appType = NmsUtil.isHostedHMApplication() ? BeCommunicationConstant.CAPWAPCLIENTTYPE_HMOL
					: BeCommunicationConstant.CAPWAPCLIENTTYPE_HM;
			String hostName = HmBeOsUtil.getHostName();
			String systemID = HM_License.getInstance().get_system_id();
			BeVersionInfo versionInfo = NmsUtil.getVersionInfo();
			String version = versionInfo.getMainVersion() + "r" + versionInfo.getSubVersion();
			String innerVer = NmsUtil.getInnerVersion();
			LicenseInfo licInfo = HmBeLicenseUtil.getLicenseInfo();

			BeApplicationInfoEvent appReq = new BeApplicationInfoEvent();
			appReq.setApplicationType(appType);
			appReq.setHostname(hostName);
			appReq.setSystemID(systemID);
			appReq.setVersion(version);
			appReq.setInnerVersion(innerVer);
			
			// maybe there is no license information
			if (null != licInfo && !BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(licInfo.getLicenseType())) {
				// max ap number
				appReq.setApCount(licInfo.getHiveAps());
				
				// max vhm number
				appReq.setVhmCount(licInfo.getVhmNumber());
			}

			appReq.buildPacket();
			HmBeCommunicationUtil.sendRequest(appReq);
			
			// send new app info to portal
			checkPortalConnect();

			BeUpdateApplicationInfoEvent req = new BeUpdateApplicationInfoEvent();
			req.setApMac(HmBeCommunicationUtil.getPortalMac());
			req.setApCount(newLicenseInfo.getHiveAps());
			req.setVhmCount(newLicenseInfo.getVhmNumber());

			req.buildPacket();
			HmBeCommunicationUtil.sendRequest(req);
		} catch (Exception e) {
			log.error("updateApplicationInfo() catch exception", e);
		}
	}

	/**
	 * Update support ap information in portal for VHM
	 * 
	 *@param vhm
	 *            name and support new ap number
	 */
	public static void updateVHMInfo(String vhmName, int apCount) {
		try {
			if (!NmsUtil.isHostedHMApplication()) {
				return;
			}

			checkPortalConnect();
			
			BeUpdateApplicationInfoEvent req = new BeUpdateApplicationInfoEvent();
			req.setApMac(HmBeCommunicationUtil.getPortalMac());
			req.setApCount(apCount);
			req.setVhmName(vhmName);

			req.buildPacket();
			HmBeCommunicationUtil.sendRequest(req);
		} catch (Exception e) {
			log.error("updateVHMInfo() catch exception", e);
		}
	}

	public static void modifyVhmInfo(VhmInfo vhmInfo) throws Exception {
		String operation = "modifyVHM";
		checkPortalConnect();
		try {
/*			BeModifyUserEvent request = new BeModifyUserEvent();
			request.setApMac(portalMacAddress);
			// set attributes
			request.setUser(vhmInfo);

			// set sequence number and build packet
			request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			request.buildPacket();

			BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(request);
			parseSyncResponseEvent(operation, response);*/

			BeModifyVHMEvent request = new BeModifyVHMEvent();
			request.setApMac(portalMacAddress);
			// set attributes
			request.setVhmInfo(vhmInfo);
			request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());

			// build packet
			request.buildPacket();

			BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(request);

			parseSyncResponseEvent(operation, response);
			
		} catch (BeCommunicationEncodeException e) {
			log.error(operation + " catch BeCommunicationEncodeException!", e);
			throw new Exception("communication packet error!");
		} catch (Exception e) {
			log.error(operation + " failed!", e);
			throw new Exception(e.getMessage());
		}
	}
}
