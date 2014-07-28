package com.ah.be.communication;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.communication.event.BeCapwapPayloadEvent;
import com.ah.be.communication.event.portal.BeCreateUserEvent;
import com.ah.be.communication.event.portal.BeCreateVHMEvent;
import com.ah.be.communication.event.portal.BeCreateVhmResult;
import com.ah.be.communication.event.portal.BeDeleteAPFromHMEvent;
import com.ah.be.communication.event.portal.BeHABreakEvent;
import com.ah.be.communication.event.portal.BeHAEnableEvent;
import com.ah.be.communication.event.portal.BeHAJoinEvent;
import com.ah.be.communication.event.portal.BeHAMaintenanceEvent;
import com.ah.be.communication.event.portal.BeHAStatusInfoEvent;
import com.ah.be.communication.event.portal.BeHASwitchOverEvent;
import com.ah.be.communication.event.portal.BeModifyHMOLEvent;
import com.ah.be.communication.event.portal.BeModifyUserEvent;
import com.ah.be.communication.event.portal.BeModifyVHMEvent;
import com.ah.be.communication.event.portal.BeModifyVhmResult;
import com.ah.be.communication.event.portal.BeMoveVHMEvent;
import com.ah.be.communication.event.portal.BePortalHMPayloadEvent;
import com.ah.be.communication.event.portal.BePortalHMPayloadResultEvent;
import com.ah.be.communication.event.portal.BePoweroffHmolEvent;
import com.ah.be.communication.event.portal.BeQueryGroupInfoFromHMEvent;
import com.ah.be.communication.event.portal.BeQueryHmolResultEvent;
import com.ah.be.communication.event.portal.BeQueryVhmMovingStatusResult;
import com.ah.be.communication.event.portal.BeQueryVhmUsersEvent;
import com.ah.be.communication.event.portal.BeQueryVhmUsersResult;
import com.ah.be.communication.event.portal.BeRemoveUserEvent;
import com.ah.be.communication.event.portal.BeRemoveVHMEvent;
import com.ah.be.communication.event.portal.BeResetVhmAdminPasswordEvent;
import com.ah.be.communication.event.portal.BeSearchAPFromHMEvent;
import com.ah.be.communication.event.portal.BeSendCredentialEvent;
import com.ah.be.communication.event.portal.BeSyncUserEvent;
import com.ah.be.communication.event.portal.BeVHMStatusChangeEvent;
import com.ah.be.communication.event.portal.BeVersionUpdateInfoEvent;
import com.ah.be.communication.mo.HmolInfo;
import com.ah.be.communication.mo.UserInfo;
import com.ah.be.communication.mo.VhmRumStatus;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.util.MgrUtil;

/**
 * 
 *@filename		BeCAPWAPRequestProcessor.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-1-27 03:03:06
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 *
 */
public class BeCAPWAPRequestProcessor {

	private final BlockingQueue<BeCommunicationEvent> eventQueue;

	private static final int eventQueueSize = 10000;

	private boolean isContinue = true;

//	private int eventCount = 0;

	private EventProcessorThread processEventThread;

	/**
	 * Construct method
	 */
	public BeCAPWAPRequestProcessor() {
		eventQueue = new LinkedBlockingQueue<BeCommunicationEvent>(eventQueueSize);
	}

	public void startTask() {
		// start event process thread
		isContinue = true;
		processEventThread = new EventProcessorThread();
		processEventThread.setName("CAPWAP request processor thread");
		processEventThread.start();
	}

	/**
	 * add event to queue
	 * 
	 * @param event
	 *            -
	 */
	public void addEvent(BeCommunicationEvent event) {
		try {
		//	eventCount++;
			eventQueue.add(event);
		} catch (Exception e) {
			DebugUtil.commonDebugError(
					"BeCAPWAPRequestProcessor.addEvent(): Exception while add event to queue", e);
		}
	}

	/**
	 * get event from queue
	 * 
	 * @return BeBaseEvent or null
	 */
	private BeCommunicationEvent getEvent() {
		try {
			return eventQueue.take();
		} catch (Exception e) {
			DebugUtil.commonDebugError(
					"BeCAPWAPRequestProcessor.getEvent(): Exception while get event from queue", e);
			return null;
		}
	}

	public boolean shutdown() {
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_COMMON,
				"<BE Thread> CAPWAP request processor is shutdown");

		isContinue = false;

		BeCommunicationEvent stopThreadEvent = new BeCommunicationEvent();
		eventQueue.add(stopThreadEvent);

		return true;
	}

	/**
	 * process for client trap and client event
	 * 
	 */
	class EventProcessorThread extends Thread {
		@Override
		public void run() {
			String preLog = "BeCAPWAPRequestProcessor.EventProcessorThread.run() ";
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_COMMON,
					"<BE Thread> CAPWAP request processor is running...");

			while (isContinue) {
				try {
					// take() method blocks
					BeCommunicationEvent event = getEvent();
					if (null == event) {
						continue;
					}

					int msgType = event.getMsgType();
					if (msgType == BeCommunicationConstant.MESSAGETYPE_CAPWAPPAYLOADREQ) {
						BeCapwapPayloadEvent payloadEvent = (BeCapwapPayloadEvent) event;
						if (payloadEvent.getQueryType() == BeCapwapPayloadEvent.QUERYTYPE_PORTALANDHM) {
							BePortalHMPayloadEvent portalEvent = (BePortalHMPayloadEvent) payloadEvent;
							handlePortalEvent(portalEvent);
						}
					}
				} catch (Exception e) {
					DebugUtil.commonDebugError(preLog + "Exception in processor thread", e);
				} catch (Error e) {
					DebugUtil.commonDebugError(preLog + "Error in processor thread", e);
				}
			}

			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_COMMON,
					"<BE Thread> CAPWAP request processor is shutdown");
		}

		private void handlePortalEvent(BePortalHMPayloadEvent portalEvent) {
			String preLog = "BeCAPWAPRequestProcessor.handlePortalEvent(): ";
			String exceptionMessage = null;
			try {
				switch (portalEvent.getOperationType()) {
				case BePortalHMPayloadEvent.OPERATIONTYPE_USER_CREATE: {
					DebugUtil.commonDebugInfo(preLog + "handle create user event.");
					BeCreateUserEvent event = (BeCreateUserEvent) portalEvent;
					BusinessUtil.createHomeUser(event.getUser());
				}
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_USER_MODIFY: {
					DebugUtil.commonDebugInfo(preLog + "handle modify user event.");
					BeModifyUserEvent event = (BeModifyUserEvent) portalEvent;
					BusinessUtil.modifyVhmUser(event.getUser());
				}
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_USER_REMOVE: {
					DebugUtil.commonDebugInfo(preLog + "handle remove user event.");
					BeRemoveUserEvent event = (BeRemoveUserEvent) portalEvent;
					BusinessUtil.removeHomeUser(event.getUserName());
				}
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_USER_SYNC: {
					DebugUtil.commonDebugInfo(preLog + "handle sync user event.");
					BeSyncUserEvent event = (BeSyncUserEvent) portalEvent;
					BusinessUtil.syncAllHomeUser(event.getUserList());
				}
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_QUERY_VHMUSERS: {
					DebugUtil.commonDebugInfo(preLog + "handle query vhm users.");
					BeQueryVhmUsersEvent event = (BeQueryVhmUsersEvent) portalEvent;
					handleQueryVhmUsers(event);
					return;
				}

				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_CREATE: {
					DebugUtil.commonDebugInfo(preLog + "handle create vhm event.");
					BeCreateVHMEvent event = (BeCreateVHMEvent) portalEvent;
					handleCreateVhm(event);
					return;
				}

				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_MODIFY: {
					DebugUtil.commonDebugInfo(preLog + "handle modify vhm event.");
					BeModifyVHMEvent event = (BeModifyVHMEvent) portalEvent;
					handleModifyVhm(event);
					return;
				}

				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_MOVE: {
					DebugUtil.commonDebugInfo(preLog + "handle remove vhm event.");
					BeMoveVHMEvent event = (BeMoveVHMEvent) portalEvent;
					BusinessUtil.moveVHM(event.getDestIPAddr(), event.getDestVersion(), event
							.getVhmNameList());
				}
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_REMOVE: {
					DebugUtil.commonDebugInfo(preLog + "handle remove vhm event.");
					BeRemoveVHMEvent event = (BeRemoveVHMEvent) portalEvent;
					BusinessUtil.removeVHM(event.getVhmName());
				}
					break;
				case BePortalHMPayloadEvent.OPERATIONTYPE_HMOL_POWEROFF: {
					DebugUtil.commonDebugInfo(preLog + "handle hmol poweroff event.");
					BePoweroffHmolEvent event = (BePoweroffHmolEvent) portalEvent;
					BusinessUtil.powerOffFromPortal(event);
				}
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_STATUSCHANGE: {
					DebugUtil.commonDebugInfo(preLog + "handle vhm status change event.");
					BeVHMStatusChangeEvent event = (BeVHMStatusChangeEvent) portalEvent;
					BusinessUtil.changeVHMStatus(event.getVhmName(), event.getStatus());
				}
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_RESET_VHM_ADMIN_PASSWORD: {
					DebugUtil.commonDebugInfo(preLog + "handle reset vhm admin password.");
					BeResetVhmAdminPasswordEvent event = (BeResetVhmAdminPasswordEvent) portalEvent;
					BusinessUtil
							.resetVhmAdminPassword(event.getVhmName(), event.getClearPassword());
				}
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_SEND_CREDENTIAL: {
					DebugUtil.commonDebugInfo(preLog + "handle send credential for vhm admin.");
					BeSendCredentialEvent event = (BeSendCredentialEvent) portalEvent;
					BusinessUtil.sendCredential(event.getCredInfo());
				}
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_HMOL_QUERYINFO: {
					DebugUtil.commonDebugInfo(preLog + "handle query hmol info.");
					handleQueryHmolInfo(portalEvent);
					return;
				}

				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_QUERY_MOVING_STATUS: {
					DebugUtil.commonDebugInfo(preLog + "handle query vhm moving status.");
					handleQueryVhmMovingStatus(portalEvent);
					return;
				}

				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_CLEAR_MOVING_STATUS: {
					DebugUtil.commonDebugInfo(preLog + "handle clear vhm moving status.");
					BusinessUtil.clearVhmMovingStatus();
				}
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_HMOL_MODIFY: {
					DebugUtil.commonDebugInfo(preLog + "handle modify hmol event.");
					BeModifyHMOLEvent event = (BeModifyHMOLEvent) portalEvent;
					BusinessUtil.modifyHMOL(event.getHmolInfo());
				}
					break;
					
				case BePortalHMPayloadEvent.OPERATIONTYPE_HMOL_VERSIONUPDATEINFO: {
					DebugUtil.commonDebugInfo(preLog + "handle version update info event.");
					BeVersionUpdateInfoEvent event = (BeVersionUpdateInfoEvent) portalEvent;
					BusinessUtil.handleVersionInfo(event);
				}
					break;
					
				case BePortalHMPayloadEvent.OPERATIONTYPE_HA_ENABLE: {
					DebugUtil.commonDebugInfo(preLog + "handle ha enable.");
					BusinessUtil.haEnable((BeHAEnableEvent)portalEvent);
					return;
				}
				
				case BePortalHMPayloadEvent.OPERATIONTYPE_HA_JOIN: {
					DebugUtil.commonDebugInfo(preLog + "handle ha join.");
					BusinessUtil.haJoin((BeHAJoinEvent)portalEvent);
					return;
				}
				
				case BePortalHMPayloadEvent.OPERATIONTYPE_HA_BREAK: {
					DebugUtil.commonDebugInfo(preLog + "handle ha break.");
					BusinessUtil.haBreak((BeHABreakEvent)portalEvent);
					return;
				}
				
				case BePortalHMPayloadEvent.OPERATIONTYPE_HA_STATUSINFO: {
					DebugUtil.commonDebugInfo(preLog + "handle ha status info event.");
					BeHAStatusInfoEvent event = (BeHAStatusInfoEvent) portalEvent;
					BusinessUtil.handleHAStatusInfoEvent(event);
					return;
				}

				case BePortalHMPayloadEvent.OPERATIONTYPE_HA_SWITCHOVER: {
					DebugUtil.commonDebugInfo(preLog + "handle ha switch over.");
					BusinessUtil.haSwitchOver((BeHASwitchOverEvent)portalEvent);
					return;
				}
				
				case BePortalHMPayloadEvent.OPERATIONTYPE_HA_MAINTENANCE: {
					DebugUtil.commonDebugInfo(preLog + "handle ha maintenance.");
					BusinessUtil.haMaintenance((BeHAMaintenanceEvent)portalEvent);
					return;
				}

				case BePortalHMPayloadEvent.OPERATIONTYPE_HM_SEARCH_AP: {
					DebugUtil.commonDebugInfo(preLog + "handle ap searching.");
					BusinessUtil.handleSearchAPwithSerialNum((BeSearchAPFromHMEvent)portalEvent);
					return;
				}
				
				case BePortalHMPayloadEvent.OPERATIONTYPE_HM_DELETE_AP: {
					DebugUtil.commonDebugInfo(preLog + "handle ap deleting.");
					BusinessUtil.handleDeleteAPwithSerialNum((BeDeleteAPFromHMEvent)portalEvent);
					return;
				}
				
				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_GET_USER_GROUPS: {
					DebugUtil.commonDebugInfo(preLog + "query vhm user group info.");
					BusinessUtil.handleUserGroupInfoByVhmId((BeQueryGroupInfoFromHMEvent)portalEvent);
					return;
				}

				default:
					DebugUtil.commonDebugError(preLog + "unknown operation type["
							+ portalEvent.getOperationType() + "]");
					return;
				}

			} catch (Exception e) {
				DebugUtil.commonDebugError(preLog + "catch exception", e);
				exceptionMessage = e.getMessage();
				if (exceptionMessage == null) {
					exceptionMessage = "The exception message is NULL.";
				}
			}

			boolean isSucc = false;
			try {
				BePortalHMPayloadResultEvent resultEvent = new BePortalHMPayloadResultEvent();
				resultEvent.setApMac(portalEvent.getApMac());
				resultEvent.setSequenceNum(portalEvent.getSequenceNum());
				resultEvent.setSuccess(exceptionMessage == null);
				resultEvent.setErrorMessage(exceptionMessage);
				resultEvent.buildPacket();
				isSucc = HmBeCommunicationUtil.sendResponse(resultEvent);
			} catch (Exception e) {
				DebugUtil.commonDebugError(preLog + "send result event catch exception", e);
			}

			if (isSucc) {
				// post process for business success
				if (portalEvent.getOperationType() == BePortalHMPayloadEvent.OPERATIONTYPE_VHM_MOVE) {
					BeMoveVHMEvent event = (BeMoveVHMEvent) portalEvent;
					Thread proc = new MoveProcess(event);
					proc.setName("MoveProcess");
					proc.start();
				} else if (portalEvent.getOperationType() == BePortalHMPayloadEvent.OPERATIONTYPE_HMOL_MODIFY) {
					if (BusinessUtil.restartHM) {
						try {
							HmBeAdminUtil.restartSoft();
						} catch (Exception e) {
							DebugUtil.commonDebugError("error when restart software because modify hm information", e);
						}
					}
				}
			} else {
				// post process for business failure
			}
		}

		class MoveProcess extends Thread {
			private final BeMoveVHMEvent event;

			public MoveProcess(BeMoveVHMEvent event) {
				this.event = event;
			}

			@Override
			public void run() {
				try {
					BusinessUtil.postProcessForMoveVHM(event.getDestIPAddr(), event
							.getDestVersion(), event.getVhmNameList());
				} catch (Exception e) {
					DebugUtil.commonDebugError("move exception ", e);
				}
			}
		}

		private void handleQueryVhmUsers(BeQueryVhmUsersEvent event) {
			String preLog = "handleQueryVhmUsers ";
			String exceptionMessage = "";
			List<UserInfo> users = null;
			try {
				users = BusinessUtil.queryVhmUsers(event.getVhmName());
			} catch (Exception e) {
				DebugUtil.commonDebugError(preLog + " catch exception", e);
				exceptionMessage = e.getMessage();
			}

			try {
				BeQueryVhmUsersResult resultEvent = new BeQueryVhmUsersResult();
				resultEvent.setApMac(event.getApMac());
				resultEvent.setSequenceNum(event.getSequenceNum());
				resultEvent.setSuccess(exceptionMessage == null
						|| exceptionMessage.trim().length() == 0);
				resultEvent.setErrorMessage(exceptionMessage);
				resultEvent.setUsers(users);
				resultEvent.buildPacket();
				HmBeCommunicationUtil.sendResponse(resultEvent);
			} catch (Exception e) {
				DebugUtil.commonDebugError(preLog + "send result event catch exception", e);
			}
		}

		private void handleModifyVhm(BeModifyVHMEvent event) {
			String preLog = "handleModifyVhm ";
			String exceptionMessage = "";
			int numberOfAP = -1;
			try {
				numberOfAP = BusinessUtil.modifyVHM(event.getVhmInfo());
			} catch (Exception e) {
				DebugUtil.commonDebugError(preLog + " catch exception", e);
				exceptionMessage = e.getMessage();
			}

			try {
				BeModifyVhmResult resultEvent = new BeModifyVhmResult();
				resultEvent.setApMac(event.getApMac());
				resultEvent.setSequenceNum(event.getSequenceNum());
				resultEvent.setSuccess(exceptionMessage == null
						|| exceptionMessage.trim().length() == 0);
				resultEvent.setErrorMessage(exceptionMessage);
				resultEvent.setNumberOfAP(numberOfAP);
				resultEvent.buildPacket();
				HmBeCommunicationUtil.sendResponse(resultEvent);
			} catch (Exception e) {
				DebugUtil.commonDebugError(preLog + "send result event catch exception", e);
			}
		}

		private void handleCreateVhm(BeCreateVHMEvent event) {
			String preLog = "handleCreateVhm ";
			String exceptionMessage = "";
			int numberOfAP = -1;
			try {
				numberOfAP = BusinessUtil.createVHM(event.getVhmInfo());
			} catch (Exception e) {
				DebugUtil.commonDebugError(preLog + " catch exception", e);
				exceptionMessage = e.getMessage();
			}

			try {
				BeCreateVhmResult resultEvent = new BeCreateVhmResult();
				resultEvent.setApMac(event.getApMac());
				resultEvent.setSequenceNum(event.getSequenceNum());
				resultEvent.setSuccess(exceptionMessage == null
						|| exceptionMessage.trim().length() == 0);
				resultEvent.setErrorMessage(exceptionMessage);
				resultEvent.setNumberOfAP(numberOfAP);
				resultEvent.buildPacket();
				HmBeCommunicationUtil.sendResponse(resultEvent);
			} catch (Exception e) {
				DebugUtil.commonDebugError(preLog + "send result event catch exception", e);
			}
		}

		private void handleQueryVhmMovingStatus(BePortalHMPayloadEvent event) {
			String preLog = "handleQueryVhmMovingStatus ";
			String exceptionMessage = "";
			List<VhmRumStatus> moveStatuses = null;
			try {
				moveStatuses = BusinessUtil.queryVhmMovingStatus();
			} catch (Exception e) {
				DebugUtil.commonDebugError(preLog + " catch exception", e);
				exceptionMessage = e.getMessage();
			}

			try {
				BeQueryVhmMovingStatusResult resultEvent = new BeQueryVhmMovingStatusResult();
				resultEvent.setApMac(event.getApMac());
				resultEvent.setSequenceNum(event.getSequenceNum());
				resultEvent.setSuccess(exceptionMessage == null
						|| exceptionMessage.trim().length() == 0);
				resultEvent.setErrorMessage(exceptionMessage);
				resultEvent.setMoveStatuses(moveStatuses);
				resultEvent.buildPacket();
				HmBeCommunicationUtil.sendResponse(resultEvent);
			} catch (Exception e) {
				DebugUtil.commonDebugError(preLog + "send result event catch exception", e);
			}
		}

		private void handleQueryHmolInfo(BePortalHMPayloadEvent event) {
			String preLog = "handleQueryHmolInfo ";
			String exceptionMessage = "";
			HmolInfo hmolInfo = null;
			try {
				hmolInfo = BusinessUtil.queryHmolInfo();
			} catch (Exception e) {
				DebugUtil.commonDebugError(preLog + " catch exception", e);
				exceptionMessage = e.getMessage();
			}

			try {
				BeQueryHmolResultEvent resultEvent = new BeQueryHmolResultEvent();
				resultEvent.setApMac(event.getApMac());
				resultEvent.setSequenceNum(event.getSequenceNum());
				resultEvent.setSuccess(exceptionMessage == null
						|| exceptionMessage.trim().length() == 0);
				resultEvent.setErrorMessage(exceptionMessage);
				resultEvent.setInfo(hmolInfo);
				resultEvent.buildPacket();
				HmBeCommunicationUtil.sendResponse(resultEvent);
			} catch (Exception e) {
				DebugUtil.commonDebugError(preLog + "send result event catch exception", e);
			}
		}
	}

}