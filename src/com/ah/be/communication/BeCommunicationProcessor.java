package com.ah.be.communication;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeEventUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.event.*;
import com.ah.be.communication.event.portal.BeCreateUserEvent;
import com.ah.be.communication.event.portal.BeCreateVHMEvent;
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
import com.ah.be.communication.event.portal.BeQueryRevertHmolResult;
import com.ah.be.communication.event.portal.BeQueryUpgradeAvailHmolsResult;
import com.ah.be.communication.event.portal.BeQueryVhmUsersEvent;
import com.ah.be.communication.event.portal.BeRemoveUserEvent;
import com.ah.be.communication.event.portal.BeRemoveVHMEvent;
import com.ah.be.communication.event.portal.BeResetVhmAdminPasswordEvent;
import com.ah.be.communication.event.portal.BeSearchAPFromHMEvent;
import com.ah.be.communication.event.portal.BeSendCredentialEvent;
import com.ah.be.communication.event.portal.BeSyncUserEvent;
import com.ah.be.communication.event.portal.BeVHMStatusChangeEvent;
import com.ah.be.communication.event.portal.BeVersionUpdateInfoEvent;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.HM_License;
import com.ah.be.license.LicenseInfo;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.CapwapClient;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ha.HAMonitor;
import com.ah.ha.HAStatus;
import com.ah.ha.HAUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

/**
 * Send application message to capwap and receive packet.
 *
 *@filename		BeCommunicationProcessor.java
 *@version
 *@author		juyizhou
 *@createtime	19 Nov 2007 14:03:11
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 */
public class BeCommunicationProcessor {

	private static final Tracer log = new Tracer(BeCommunicationProcessor.class.getSimpleName());

	final ClientChannel								myClientSocket					= new ClientChannel();

	final BeCommunicationRequestManager				requestManager;

	// self-added number
	private int										mySerialNumber					= 101;

	// private Process capwapProcess = null;
	private boolean									isContinue						= true;

	/**
	 * cache serialNumber-responseListener map
	 */
	private final Map<Integer, ResponseListener>	syncResponseMap					= new Hashtable<Integer, ResponseListener>();

	/**
	 * cache sequenceNumber-serialNum map
	 */
	private final Map<Integer, Integer>				syncSequenceSerialNumMap		= new Hashtable<Integer, Integer>();

	/**
	 * cache serialNumber-responseListener map
	 */
	private final Map<Integer, ResponseListener>	syncGroupResponseMap			= new Hashtable<Integer, ResponseListener>();

	/**
	 * cache sequenceNumber-serialNum map
	 */
	private final Map<Integer, Integer>				syncGroupSequenceSerialNumMap	= new Hashtable<Integer, Integer>();

	private MsgProcessorThread processorThread;

	// private Map<Integer,ResponseListener> syncEventMap = new
	// Hashtable<Integer, ResponseListener>();

	public BeCommunicationProcessor() {
		requestManager = new BeCommunicationRequestManager();
	}

	public void startTask() {
		isContinue = true;

		requestManager.start();

		// start event process thread
		processorThread = new MsgProcessorThread();
		processorThread.setName("message processor thread");
		processorThread.start();
	}

	/**
	 * open channel and connect
	 *
	 * @return true if ok, else false
	 */
	private boolean open() {
		int successFlag = myClientSocket.open();
		if (0 != successFlag) {
			DebugUtil
					.commonDebugInfo("BeCommunicationProcessor.open(): Open channel and connect failed!");
			return false;
		}
		DebugUtil
				.commonDebugInfo("BeCommunicationProcessor.open(): Open channel and connect success!");

		// send HA status
		distributeHAStatus();

		// send BeCapwapConnectEvent to other module
		BeCapwapConnectEvent event = new BeCapwapConnectEvent();
		event.setConnectState(true);
		HmBeEventUtil.eventGenerated(event);

		try {
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

			if (NmsUtil.isHostedHMApplication()) {
				// push new capwap client settings if hmol
				CapwapClient setting = QueryUtil.findBoByAttribute(CapwapClient.class,
						"serverType", CapwapClient.SERVERTYPE_PORTAL);
				if (setting != null) {
					BeCapwapClientParamConfigEvent capwapEvent = new BeCapwapClientParamConfigEvent();
					List<CapwapClient> list = new ArrayList<CapwapClient>();
					list.add(setting);
					capwapEvent.setCapwapClientList(list);
					capwapEvent.buildPacket();

					HmBeCommunicationUtil.sendRequest(capwapEvent);
				}
			}

		} catch (Exception e) {
			DebugUtil.commonDebugWarn("BeCommunicationProcessor.open() catch exception. ", e);
		}

		return true;
	}

	/**
	 * receive packet, parse it and dispatch message
	 *
	 * @see java.lang.Thread#run()
	 */
	class MsgProcessorThread extends Thread {
		@Override
		public void run() {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			int int_OpenFail = 0;
			BeCommunicationEvent event = null;
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_COMMON,
					"<BE Thread> Communication processor is running...");

			while (isContinue) {
				try {
					// if disconnect, try to connect first.
					if (!myClientSocket.getConnectState()) {
						// reopen
						if (!open()) {
							int_OpenFail++;
							if (int_OpenFail > 3) {
								int_OpenFail = 0;
								Thread.sleep(10000);
							}
							Thread.sleep(5000);
							continue;
						}
					}

					// 1.receive bytebuffer
					ByteBuffer buf;
					try {
						buf = myClientSocket.recv();
					} catch (Exception e) {
						DebugUtil.commonDebugWarn(
								"BeCommunicationProcessor.run() catch exception ", e);
						myClientSocket.close();
						try {
							Thread.sleep(5000);
						} catch (Exception ex) {
							// do nothing
						}

						continue;
					}

					//
					if (buf == null) {
						continue;
					}

					// 2.parse message head
					short msgType = buf.getShort();
					int serialNum = buf.getInt();
					long timeStamp = AhDecoder.int2long(buf.getInt()) * 1000;
					//add protection, use current system time if time is invalid (< 1 year or > 1 day)
					if(timeStamp < (System.currentTimeMillis() - 31536000000L) ||
						timeStamp > (System.currentTimeMillis() + 86400000L))
						timeStamp = System.currentTimeMillis();
					byte timeZone = AhDecoder.parseTimeZone(buf.get());

					// got remaining byte array
					byte[] byteArray = new byte[buf.remaining()];
					buf.get(byteArray, 0, buf.remaining());

					// handle request from capwap server.
					if (msgType == BeCommunicationConstant.MESSAGETYPE_CAPWAPPAYLOADREQ) {
						handleCapwapPayloadRequest(byteArray);
						continue;
					}

					// 3.create event obj
					switch (msgType) {
					case BeCommunicationConstant.MESSAGETYPE_APCONNECT:
						event = new BeAPConnectEvent();
						((BeAPConnectEvent) event)
								.setConnectState(BeAPConnectEvent.CONNECTSTATE_CONNECT);

						// log
//						DebugUtil
//								.commonDebugInfo("Communication module receive AP connect message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_APDISCONNECT:
						event = new BeAPConnectEvent();
						((BeAPConnectEvent) event)
								.setConnectState(BeAPConnectEvent.CONNECTSTATE_DISCONNECT);

//						DebugUtil
//								.commonDebugInfo("Communication module receive AP disconnect message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTCONNECT:
						event = new BeCapwapClientConnectEvent();

						// log
//						DebugUtil
//								.commonDebugInfo("Communication module receive capwap client connect message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_APWTPEVENT: {
						// AP WTP Event is special
						event = new BeAPWTPEvent();
						BeAPWTPEvent wtpEvent = (BeAPWTPEvent)event;
						wtpEvent.setPacket(byteArray);
						wtpEvent.parsePacket();
						String apMac = wtpEvent.getApMac();

						byte[] wtpData = wtpEvent.getWtpMsgData();
						int wtpMsgType = wtpEvent.getWtpMsgType();
						switch (wtpMsgType) {
						case BeCommunicationConstant.MESSAGEELEMENTTYPE_IDPSTATISTICS:
							wtpEvent = new BeCapwapIDPStatisticsEvent();

//							DebugUtil
//									.commonDebugInfo("Communication module receive IDP statistics message.");
							break;

						case BeCommunicationConstant.MESSAGEELEMENTTYPE_APTYPECHANGE:
							wtpEvent = new BeCapwapAPTypeChangeEvent();

//							DebugUtil
//									.commonDebugInfo("Communication module receive AP type change message.");
							break;

						case BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT:
							wtpEvent = new BeCapwapCliResultEvent();

//							DebugUtil
//									.commonDebugInfo("Communication module receive cli result message.");
							break;

						case BeCommunicationConstant.MESSAGEELEMENTTYPE_FILEDOWNLOADPROGRESS:
							wtpEvent = new BeCapwapFileDownProgressEvent();

//							DebugUtil
//									.commonDebugInfo("Communication module receive file download progress message.");
							break;

						case BeCommunicationConstant.MESSAGEELEMENTTYPE_STATISTICRESULT:
							wtpEvent = new BeStatisticResultEvent();

//							DebugUtil
//									.commonDebugInfo("Communication module receive statistics result message.");
							break;

						case BeCommunicationConstant.MESSAGEELEMENTTYPE_REBOOTFAILEVENT:
							wtpEvent = new BeRebootFailEvent();

//							DebugUtil
//									.commonDebugInfo("Communication module receive reboot fail message.");
							break;

						case BeCommunicationConstant.MESSAGEELEMENTTYPE_CONFIGVERSIONEVENT:
							wtpEvent = new BeConfigVersionEvent();

//							DebugUtil
//									.commonDebugInfo("Communication module receive config version message.");
							break;

						case BeCommunicationConstant.MESSAGEELEMENTTYPE_TRAPEVENT:
							wtpEvent = new BeTrapEvent();

//							DebugUtil
//									.commonDebugInfo("Communication module receive trap event message.");
							break;

						case BeCommunicationConstant.MESSAGEELEMENTTYPE_CWPDIRECTORYRESULT:
							wtpEvent = new BeCWPDirectoryResultEvent();

//							DebugUtil
//									.commonDebugInfo("Communication module receive cwp directory result message.");
							break;

						case BeCommunicationConstant.MESSAGEELEMENTTYPE_HOSTIDENTIFICATIONKEYRESULT:
							wtpEvent = new BeHostIdentificationResultEvent();

//							DebugUtil
//									.commonDebugInfo("Communication module receive host identification key result message.");
							break;

						case BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT:
							wtpEvent = new BeCapwapClientResultEvent();

//							DebugUtil
//									.commonDebugInfo("Communication module receive capwap client result message.");
							break;

						case BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPPAYLOADRESULT:
							wtpEvent = new BeCapwapPayloadResultEvent();

//							DebugUtil
//									.commonDebugInfo("Communication module receive capwap payload result message.");
							break;

						case BeCommunicationConstant.MESSAGEELEMENTTYPE_HIVENAMECHANGE:
							wtpEvent = new BeHiveNameChangeEvent();

//							DebugUtil
//									.commonDebugInfo("Communication module receive hive name change message.");
							break;

						default:
							continue;
						}

						wtpEvent.setSerialNum(serialNum);
						wtpEvent.setPacket(byteArray);
						wtpEvent.setWtpMsgData(wtpData);

						wtpEvent.setMessageTimeStamp(timeStamp);
						wtpEvent.setApMac(apMac);
						wtpEvent.setMessageTimeZone(wtpEvent.parseTimeZoneFromAP(timeZone));

						// modify mark, communication module do response event parse operation
						wtpEvent.parsePacket();

						dispatchWTPEvent(wtpEvent);

						continue;
					}

					case BeCommunicationConstant.MESSAGETYPE_APDTLSAUTHORIZEEVENT:
						event = new BeAPDTLSAuthorizeEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive AP DTLS authorize message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_CAPWAPDTLSCONFIGRSP:
						event = new BeCapwapDTLSConfigEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive DTLS config rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_CAPWAPSERVERCONFIGRSP:
						event = new BeCapwapServerParamConfigEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive capwap server config rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTPARAMCONFIGRSP:
						event = new BeCapwapClientParamConfigEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive capwap client config rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_CAPWAPSERVERLICENSERSP:
						event = new BeCapwapServerLicenseEvent();
//						DebugUtil
//								.commonDebugInfo("Communication module receive capwap server license rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_IDPQUERYRSP:
						event = new BeIDPQueryEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive IDP query rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_L3ROAMINGCONFIGRSP:
						event = new BeL3RoamingConfigEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive L3 roaming config rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_SHUTDOWNRSP:
						event = new BeShutDownEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive shutdown rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_WTPCONTROLRSP:
						event = new BeWTPEventControlEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive WTP control rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_GETSTATISTICRSP:
						event = new BeGetStatisticEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive statistic rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_CLIRSP:
						event = new BeCliEvent();

//						DebugUtil.commonDebugInfo("Communication module receive CLI rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_DELETEAPCONNECTRSP:
						event = new BeDeleteAPConnectEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive delete ap connect rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_ABORTRSP:
						event = new BeAbortEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive abort rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_SHOWCWPDIRECTORYRSP:
						event = new BeCWPDirectoryEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive show cwp directory rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_HOSTIDENTIFICATIONKEYRSP:
						event = new BeHostIdentificationKeyEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive host identification key rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTINFORSP:
						event = new BeCapwapClientInfoEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive capwap client information rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP:
						event = new BeCapwapClientEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive capwap client event rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_SIMULATEHIVEAPRSP:
						event = new BeSimulateHiveAPEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive simulate hiveap rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_CAPWAPSTATRSP:
						event = new BeCapwapStatisticsEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive capwap statistics rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_CAPWAPPAYLOADRSP:
						event = new BeCapwapPayloadEvent();

//						DebugUtil
//								.commonDebugInfo("Communication module receive capwap payload rsp message.");
						break;

					case BeCommunicationConstant.MESSAGETYPE_HAQUERYRSP:
					case BeCommunicationConstant.MESSAGETYPE_HASETRSP:
					case BeCommunicationConstant.MESSAGETYPE_APPLICATIONINFORSP:
						// discard.
						continue;

					default:
						// event = new BeCommunicationEvent();
						DebugUtil
								.commonDebugError("Unknown message type received. message value is "
										+ msgType);
						continue;
					}

					event.setMsgType(msgType);
					event.setSerialNum(serialNum);
					event.setPacket(byteArray);

					// modify mark, communication module do response event parse operation
					event.parsePacket();

					event.setMessageTimeStamp(timeStamp);
					event.setMessageTimeZone(event.parseTimeZoneFromAP(timeZone));
					
					//handle all request map when ap disconnect
					if (msgType == BeCommunicationConstant.MESSAGETYPE_APDISCONNECT) {
						requestManager.clearRequesetObjForAp(event.getApMac());
					} else if (msgType == BeCommunicationConstant.MESSAGETYPE_APCONNECT) {
						SimpleHiveAp simpleHiveAp = event.getSimpleHiveAp();
						if(simpleHiveAp != null) {
							simpleHiveAp.setConnectStatus(HiveAp.CONNECT_UP);
						}
					}

					// dispatch event
					dispatchResponse(event);
				} catch (IOException e) {
					DebugUtil.commonDebugWarn(
							"BeCommunicationProcessor.run() catch IO exception, connect closed! ",
							e);
					myClientSocket.close();
					try {
						Thread.sleep(5000);
					} catch (Exception ex) {
						// do nothing
					}

				} catch (Exception e) {
					String apInfo = "Unknown Ap";
					if(event != null && event.getSimpleHiveAp() != null)apInfo = event.getSimpleHiveAp().toString();
					DebugUtil
							.commonDebugWarn("BeCommunicationProcessor.run() catch exception. AP Info:" + apInfo, e);
					// myClientSocket.close();
					// try
					// {
					// sleep(5000);
					// }
					// catch (Exception ex)
					// {
					// DebugUtil
					// .commonDebugWarn(
					// "BeCommunicationProcessor.run() Sleep thread catch exception!
					// ",
					// e);
					// }
				} catch (Error e) {
					DebugUtil.commonDebugWarn("BeCommunicationProcessor.run(),catch error! ", e);
					myClientSocket.close();
					try {
						Thread.sleep(5000);
					} catch (Exception ex) {
						// do nothing
					}
				}
			}

			// shutdown process not in control now.
			if (AhAppContainer.HmBe != null) {
				BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_COMMON,
						"<BE Thread> Communication processor is shutdown");

			}
		}
	}

	/**
	 * dispatch wtp event
	 *
	 * @param wtpEvent
	 *            -
	 * @throws Exception
	 *             -
	 */
	void dispatchWTPEvent(BeAPWTPEvent wtpEvent) throws Exception {
		int sequenceNum = wtpEvent.getSequenceNum();
		int wtpMsgType = wtpEvent.getMsgType();
		
		//update the connect status if connect status is down
		SimpleHiveAp simpleHiveAp = wtpEvent.getSimpleHiveAp();
		if(null != simpleHiveAp) {
			if(simpleHiveAp.getConnectStatus() == HiveAp.CONNECT_DOWN) {
				simpleHiveAp.setConnectStatus(HiveAp.CONNECT_UP);
				BeDeleteAPConnectEvent deleteEvent = new BeDeleteAPConnectEvent();
				deleteEvent.setApMac(simpleHiveAp.getMacAddress());
				deleteEvent.setDeleteAP(false);
				deleteEvent.buildPacket();
				sendRequest(deleteEvent);
				BeLogTools.error(HmLogConst.M_COMMON,"Connect status is mismatch, Let Device "+simpleHiveAp.getMacAddress()+" reconnects to HiveManager");
			}
		}

		// set ap obj
		// maybe there's no ap object in BeCommunication event.
		BeCommunicationEvent requestEvent = requestManager.getResultEventRequestObj(sequenceNum);
		if (null != requestEvent) {
			// if (requestEvent.getSimpleHiveApNoQuery() == null) {
			// wtpEvent.setAp(requestEvent.getAp());
			// } else {
			// wtpEvent.setSimpleHiveAp(requestEvent.getSimpleHiveAp());
			// }
			wtpEvent.setAp(requestEvent.getApNoQuery());
			wtpEvent.setSimpleHiveAp(requestEvent.getSimpleHiveAp());
			// wtpEvent.setSerialNum(requestEvent.getSerialNum());
		}

		wtpEvent.setSerialNum(wtpEvent.getSequenceNum());

		// remove request from manager
		requestManager.removeResultEventRequest(sequenceNum);

		// special for capwap client result event
		if (wtpMsgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
			wtpEvent = handleCapwapClientEventResult(wtpEvent);
		}

		// need special for capwap payload result event here
		if (wtpMsgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPPAYLOADRESULT) {
			wtpEvent = handleCapwapPayloadResultEvent(wtpEvent);
		}

		// sync request
		if (syncSequenceSerialNumMap.containsKey(sequenceNum)) {
			ResponseListener responseListener = syncResponseMap.get(syncSequenceSerialNumMap
					.get(sequenceNum));

			responseListener.onResponse(wtpEvent);

			// // remove request from manager
			// requestManager.removeRequest(event);

			return;
		}

		// sync group request
		if (syncGroupSequenceSerialNumMap.containsKey(sequenceNum)) {
			ResponseListener responseListener = syncGroupResponseMap
					.get(syncGroupSequenceSerialNumMap.get(sequenceNum));
			responseListener.onResponse(wtpEvent);

			// it's different process with single request,We need remove req
			// obj from manager queue in time avoid all request obj return
			// as timeout response in the end.
			// requestManager.removeRequest(syncGroupSequenceSerialNumMap.get(sequenceNum));
			// requestManager.removeResultEventRequest(sequenceNum);

			return;
		}

		// trap event
		if (wtpMsgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_TRAPEVENT) {
			BeTrapEvent trapEvent = (BeTrapEvent)wtpEvent;
//			if (trapEvent.getTrapType() == BeTrapEvent.TYPE_CLIENTOSINFOMATION
//					|| trapEvent.getTrapType() == BeTrapEvent.TYPE_LDAP_ALARM) {
//				HmBeEventUtil.eventGenerated(wtpEvent);
//				return;
//			}
			// for compatible with previous implement, let's build a snmp trap event.
/*			SnmpTrapEvent snmpTrapEvent = parseTrap(trapEvent);
			if (snmpTrapEvent == null) {
				return;
			}

			snmpTrapEvent.setModuleId(BaseModule.ModuleID_SNMP);
			snmpTrapEvent.setEventType(BeEventConst.Be_Snmp_RecTrap);*/

//			try {
//				trapEvent.parsePacket();
//			} catch (BeCommunicationDecodeException e) {
//				DebugUtil.commonDebugWarn("BeCommunicationProcessor.parseTrap(): Error  when parse trap event.", e);
//
//				return;
//			}

			// discard event which ap not in managed status
			SimpleHiveAp ap = trapEvent.getSimpleHiveAp();
			if (ap == null)return;
			if (ap.getManageStatus() != HiveAp.STATUS_MANAGED)return;

			trapEvent.setApName(ap.getHostname());
			trapEvent.setTimeStamp(System.currentTimeMillis());
			trapEvent.setTimeZone(trapEvent.getMessageTimeZone());

			HmBeEventUtil.eventGenerated(trapEvent);

			return;
		}

		HmBeEventUtil.eventGenerated(wtpEvent);
	}

	/**
	 * special process for capwap client event result
	 *
	 * @param wtpEvent
	 *            -
	 * @return -
	 * @throws Exception
	 *             -
	 */
	private BeAPWTPEvent handleCapwapClientEventResult(BeAPWTPEvent wtpEvent) throws Exception {
		BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent) wtpEvent;
		short resultType = resultEvent.getResultType();
		BeAPWTPEvent cacheObj = resultEvent;

		switch (resultType) {
		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_IDPMITIGATION:
			resultEvent = new BeIDPMitigationResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SHOWCAPTURESTATUS:
			resultEvent = new BeShowCaptureStatusResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTMONITORING:
			resultEvent = new BeTroubleShootingResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VLANPROBE:
			resultEvent = new BeVLANProbeResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LOCATIONTRACK:
			resultEvent = new BeLocationTrackingResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PATHPROBE:
			resultEvent = new BePathProbeResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SHOWACSPSTATS:
			resultEvent = new BeShowACSPStatsResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_POESTATUS:
			resultEvent = new BePOEStatusResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_INTERFERENCEMAP:
			resultEvent = new BeInterferenceMapResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LLDPCDPINFO:
			resultEvent = new BeLLDPCDPInfoResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VPNSTATUS:
			resultEvent = new BeVPNStatusResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PCIDATA:
			resultEvent = new BePCIDataResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_HIVECOMM:
			resultEvent = new BeHiveCommResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_INTERFACECLIENT:
			resultEvent = new BeInterfaceClientResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_AAATEST:
			resultEvent = new BeAAATestResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_TEACHERVIEW_STUDENTNOTFOUND:
			resultEvent = new BeTeacherViewStudentNotFoundEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RETRIEVELDAPINFO:
			resultEvent = new BeRetrieveLDAPInfoResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LDAPTREEINFO:
			resultEvent = new BeLDAPTreeInfoResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_QUERYADINFO:
			resultEvent = new BeQueryADInfoResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SPECTRALANALYSIS:
			resultEvent = new BeSpectralAnalysisEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_DATACOLLECTIONINFO:
			resultEvent = new BeDataCollectionInfoEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PORT_AVAILABILITY:
			resultEvent = new BePortAvailabilityResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_NAAS_LICENSE:
			resultEvent = new BeNaasLicenseResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_MITIGATION_ARBITRATOR:
			resultEvent = new BeMitigationArbitratorResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_AUTO_MITIGATION:
			resultEvent = new BeAutoMitigationResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PSE_STATUS:
			resultEvent = new BePSEStatusResultEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTPERFORMANCE:
		    resultEvent = new BeClientPerformanceMonitorResultEvent();
		    break;
		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_BONJOUR_GATEWAY:
			resultEvent = new BeBonjourGatewayResultEvent();
			break;
		    
		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RADSEC_CERT_CREATION:
		    resultEvent = new BeRadSecCertCreationResultEvent();
		    break;
		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_OTP:
			resultEvent = new BeOTPStatusResultEvent();
			break;
			
		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PRESENCE:
			resultEvent = new BePresenceResultEvent();
			break;
			
		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_APPREPORTCOLLECTIONINFO:
 			resultEvent = new BeAppReportCollectionInfoEvent();
			break;
		
		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SWITCH_PORT_INFO:
			resultEvent = new BeSwitchPortInfoResultEvent();
            break;
            
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SWITCH_PORT_STATS:
        	resultEvent = new BeSwitchPortStatsResultEvent();
            break;
            
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SWITCH_SYSTEM_INFO:
        	resultEvent = new BeSwitchSystemInfoResultEvent();
            break;
            
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_APPLICATION_FLOW_INFO:
        	resultEvent = new BeApplicationFlowInfoResultEvent();
        	break;
        	
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_L7_SIGNATURE_FILE_VERSION:
			resultEvent = new BeL7SignatureFileVersionResultEvent();
            break;
            
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_ROUTER_LTE_VZ_INFO:
        	resultEvent = new BeRouterLTEVZInfoResultEvent();
        	break;

        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SWITCH_PORT_STATS_REPORT:
        	resultEvent = new BeSwitchPortStatsReportResultEvent();
        	break;
        	
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RADSEC_PROXY_INFO:
        	resultEvent = new BeRadsecProxyInfoResultEvent();
        	break;
        	
		default:
			break;
		}

		resultEvent.setSerialNum(cacheObj.getSerialNum());
		resultEvent.setPacket(cacheObj.getPacket());
		resultEvent.setWtpMsgData(cacheObj.getWtpMsgData());
		resultEvent.setSimpleHiveAp(cacheObj.getSimpleHiveAp());
		resultEvent.setAp(cacheObj.getApNoQuery());
		resultEvent.setMessageTimeStamp(cacheObj.getMessageTimeStamp());
		resultEvent.setMessageTimeZone(cacheObj.getMessageTimeZone());
		resultEvent.parsePacket();

		return resultEvent;
	}

	private BeAPWTPEvent handleCapwapPayloadResultEvent(BeAPWTPEvent wtpEvent) throws Exception {
		BeCapwapPayloadResultEvent resultEvent = (BeCapwapPayloadResultEvent) wtpEvent;
		short queryType = resultEvent.getQueryType();
		BeAPWTPEvent cacheObj = resultEvent;

		switch (queryType) {
		case BeCapwapPayloadEvent.QUERYTYPE_PORTALANDHM: {
			byte payloadResultType = resultEvent.getPayloadResultType();
			if (payloadResultType == BePortalHMPayloadResultEvent.RESULTTYPE_BASE) {
				resultEvent = new BePortalHMPayloadResultEvent();
			} else if (payloadResultType == BePortalHMPayloadResultEvent.RESULTTYPE_QUERYHHM) {
				resultEvent = new BeQueryHmolResultEvent();
			} else if (payloadResultType == BePortalHMPayloadResultEvent.RESULTTYPE_VHMUPGRADE) {
				resultEvent = new BeQueryUpgradeAvailHmolsResult();
			} else if (payloadResultType == BePortalHMPayloadResultEvent.RESULTTYPE_QUERY_VHMREVERT_DESTHMOL) {
				resultEvent = new BeQueryRevertHmolResult();
			} else if (payloadResultType == BePortalHMPayloadResultEvent.RESULTTYPE_VHM_MODIFY) {
				resultEvent = new BeModifyVhmResult();
			}

			break;
		}

		default:
			break;
		}

		resultEvent.setSerialNum(cacheObj.getSerialNum());
		resultEvent.setSequenceNum(cacheObj.getSequenceNum());
		resultEvent.setPacket(cacheObj.getPacket());
		resultEvent.setWtpMsgData(cacheObj.getWtpMsgData());
		resultEvent.setApMac(cacheObj.getApMac());
		resultEvent.parsePacket();

		return resultEvent;
	}

	/**
	 * parse BeTrapEvent into SnmpTrapEvent
	 *
	 * @param trapEvent
	 *            -
	 * @return -
	 */
//	private SnmpTrapEvent parseTrap(BeTrapEvent trapEvent) {
//		SnmpTrapEvent snmpTrapEvent = new SnmpTrapEvent();
//
//		try {
//			trapEvent.parsePacket();
//		} catch (BeCommunicationDecodeException e) {
//			DebugUtil.commonDebugWarn(
//					"BeCommunicationProcessor.parseTrap(): Error  when parse trap event.", e);
//
//			return null;
//		}
//
//		// discard event which ap not in managed status
//		if (trapEvent.getSimpleHiveAp().getManageStatus() != HiveAp.STATUS_MANAGED) {
//			return null;
//		}
//
//		snmpTrapEvent.setAhAPId(trapEvent.getSimpleHiveAp().getMacAddress());
//		snmpTrapEvent.setAhAPName(trapEvent.getSimpleHiveAp().getHostname());
//		switch (trapEvent.getTrapType()) {
//		case BeTrapEvent.TYPE_FAILURE: {
//			snmpTrapEvent.setTrapType(SnmpConstance.AH_FAILURE_TRAP);
//			snmpTrapEvent.setAhObjectName(trapEvent.getObjectName());
//			snmpTrapEvent.setAhTrapDesc(trapEvent.getDescribe());
//			snmpTrapEvent.setAhCode(trapEvent.getCode());
//			snmpTrapEvent.setAhSeverity(trapEvent.getSeverity());
//			snmpTrapEvent.setAhProbableCause(trapEvent.getProbableCause());
//			snmpTrapEvent.setFailureFlag(trapEvent.isFailureFlag());
//
//			break;
//		}
//
//		case BeTrapEvent.TYPE_THRESHOLDCROSSING: {
//			snmpTrapEvent.setTrapType(SnmpConstance.AH_THRESHOLD_CROSSING_EVENT);
//			snmpTrapEvent.setAhObjectName(trapEvent.getObjectName());
//			snmpTrapEvent.setAhTrapDesc(trapEvent.getDescribe());
//			snmpTrapEvent.setAhCode(trapEvent.getCode());
//			snmpTrapEvent.setAhCurValue(trapEvent.getCurrentValue());
//			snmpTrapEvent.setAhThresholdHigh(trapEvent.getThresholdhigh());
//			snmpTrapEvent.setAhThresholdLow(trapEvent.getThresholdlow());
//
//			break;
//		}
//
//		case BeTrapEvent.TYPE_STATECHANGE: {
//			snmpTrapEvent.setTrapType(SnmpConstance.AH_STATE_CHANGE_EVENT);
//			snmpTrapEvent.setAhObjectName(trapEvent.getObjectName());
//			snmpTrapEvent.setAhTrapDesc(trapEvent.getDescribe());
//			snmpTrapEvent.setAhCode(trapEvent.getCode());
//			snmpTrapEvent.setAhPreviousState(trapEvent.getPreviousState());
//			snmpTrapEvent.setAhCurrentState(trapEvent.getCurrentState());
//
//			break;
//		}
//
//		case BeTrapEvent.TYPE_CONNECTCHANGE: {
//			snmpTrapEvent.setTrapType(SnmpConstance.AH_CONNECTION_CHANGE_EVENT);
//			snmpTrapEvent.setAhObjectName(trapEvent.getObjectName());
//			snmpTrapEvent.setAhTrapDesc(trapEvent.getDescribe());
//			snmpTrapEvent.setAhCode(trapEvent.getCode());
//			snmpTrapEvent.setAhIfIndex(trapEvent.getIfIndex());
//			snmpTrapEvent.setAhRemoteId(trapEvent.getRemoteID());
//			snmpTrapEvent.setAhCurrentState(trapEvent.getCurrentState());
//			snmpTrapEvent.setAhObjectType(trapEvent.getObjectType());
//			snmpTrapEvent.setAhSSID(trapEvent.getClientSSID());
//			snmpTrapEvent.setClientIp(trapEvent.getClientIP());
//			snmpTrapEvent.setClientHostName(trapEvent.getClientHostName());
//			snmpTrapEvent.setClientUserName(trapEvent.getClientUserName());
//
//			snmpTrapEvent.setClientCWPUsed(trapEvent.getClientCWPUsed());
//			snmpTrapEvent.setClientAuthMethod(trapEvent.getClientAuthMethod());
//			snmpTrapEvent.setClientEncryptionMethod(trapEvent.getClientEncryptionMethod());
//			snmpTrapEvent.setClientMacProtocol(trapEvent.getClientMacProtocol());
//			snmpTrapEvent.setClientVLAN(trapEvent.getClientVLAN());
//			snmpTrapEvent.setClientUserProfId(trapEvent.getClientUserProfId());
//			snmpTrapEvent.setClientChannel(trapEvent.getClientChannel());
//			snmpTrapEvent.setClientBSSID(trapEvent.getClientBSSID());
//			snmpTrapEvent.setAssociationTime(trapEvent.getAssociationTime());
//			snmpTrapEvent.setAhIfName(trapEvent.getIfName());
//
//			break;
//		}
//
//		case BeTrapEvent.TYPE_CLIENTINFOMATION: {
//			snmpTrapEvent.setTrapType(SnmpConstance.AH_CLIENTINFO_EVENT);
//			snmpTrapEvent.setAhObjectName(trapEvent.getObjectName());
//			snmpTrapEvent.setAhTrapDesc(trapEvent.getDescribe());
//			snmpTrapEvent.setAhCode(trapEvent.getCode());
//			snmpTrapEvent.setAhSSID(trapEvent.getClientSSID());
//			snmpTrapEvent.setClientMac(trapEvent.getRemoteID());
//			snmpTrapEvent.setClientIp(trapEvent.getClientIP());
//			snmpTrapEvent.setClientHostName(trapEvent.getClientHostName());
//			snmpTrapEvent.setClientUserName(trapEvent.getClientUserName());
//
//			break;
//		}
//
//		case BeTrapEvent.TYPE_POE: {
//			snmpTrapEvent.setTrapType(SnmpConstance.AH_POE_EVENT);
//			snmpTrapEvent.setAhObjectName(trapEvent.getObjectName());
//			snmpTrapEvent.setAhTrapDesc(trapEvent.getDescribe());
//			snmpTrapEvent.setAhCode(trapEvent.getCode());
//			snmpTrapEvent.setPowerSource(trapEvent.getPowerSource());
//			snmpTrapEvent.setPoEEth0On(trapEvent.getPoEEth0On());
//			snmpTrapEvent.setPoEEth0Pwr(trapEvent.getPoEEth0Pwr());
//			snmpTrapEvent.setPoEEth1On(trapEvent.getPoEEth1On());
//			snmpTrapEvent.setPoEEth1Pwr(trapEvent.getPoEEth1Pwr());
//			snmpTrapEvent.setPoEEth0MaxSpeed(trapEvent.getPoEEth0MaxSpeed());
//			snmpTrapEvent.setPoEEth1MaxSpeed(trapEvent.getPoEEth1MaxSpeed());
//			snmpTrapEvent.setPoEWifi0Setting(trapEvent.getPoEWifi0Setting());
//			snmpTrapEvent.setPoEWifi1Setting(trapEvent.getPoEWifi0Setting());
//			snmpTrapEvent.setPoEWifi2Setting(trapEvent.getPoEWifi0Setting());
//
//			break;
//		}
//
//		case BeTrapEvent.TYPE_CHANNELPOWERCHANGE: {
//			snmpTrapEvent.setTrapType(SnmpConstance.AH_CHANNELPOWERCHANGE_EVENT);
//			snmpTrapEvent.setAhObjectName(trapEvent.getObjectName());
//			snmpTrapEvent.setAhTrapDesc(trapEvent.getDescribe());
//			snmpTrapEvent.setAhCode(trapEvent.getCode());
//			snmpTrapEvent.setAhIfIndex(trapEvent.getIfIndex());
//			snmpTrapEvent.setRadioChannel(trapEvent.getRadioChannel());
//			snmpTrapEvent.setRadioTxPower(trapEvent.getRadioTxPower());
//			snmpTrapEvent.setBeaconInterval(trapEvent.getBeaconInterval());
//
//			break;
//		}
//
//		case BeTrapEvent.TYPE_TIMEBOMBWARNING: {
//			snmpTrapEvent.setTrapType(SnmpConstance.AH_TIME_BOMB_WARNING_EVENT);
//			snmpTrapEvent.setAhSeverity(trapEvent.getSeverity());
//			snmpTrapEvent.setAhTrapDesc(trapEvent.getDescribe());
//
//			break;
//		}
//
//		case BeTrapEvent.TYPE_INTERFERENCEALERT: {
//			snmpTrapEvent.setTrapType(SnmpConstance.AH_INTERFERENCE_ALERT_EVENT);
//			snmpTrapEvent.setAhObjectName(trapEvent.getObjectName());
//			snmpTrapEvent.setAhTrapDesc(trapEvent.getDescribe());
//			snmpTrapEvent.setAhCode(trapEvent.getCode());
//			snmpTrapEvent.setAhIfIndex(trapEvent.getIfIndex());
//			snmpTrapEvent.setFailureFlag(trapEvent.isFailureFlag());
//			snmpTrapEvent.setAhSeverity(trapEvent.getSeverity());
//			snmpTrapEvent.setInterferenceCUThreshold(trapEvent.getInterferenceCUThreshold());
//			snmpTrapEvent.setAverageInterferenceCU(trapEvent.getAverageInterferenceCU());
//			snmpTrapEvent.setShortTermInterferenceCU(trapEvent.getShortTermInterferenceCU());
//			snmpTrapEvent.setSnapShotInterferenceCU(trapEvent.getSnapShotInterferenceCU());
//			snmpTrapEvent.setCrcErrorRateThreshold(trapEvent.getCrcErrorRateThreshold());
//			snmpTrapEvent.setCrcErrorRate(trapEvent.getCrcErrorRate());
//
//			break;
//		}
//
//		case BeTrapEvent.TYPE_AIRSCREENREPORT: {
//			snmpTrapEvent.setTrapType(SnmpConstance.AH_AIRSCREEN_REPORT_EVENT);
//			snmpTrapEvent.setAhObjectName(trapEvent.getObjectName());
//			snmpTrapEvent.setAhTrapDesc(trapEvent.getDescribe());
//			snmpTrapEvent.setReportType(trapEvent.getReportType());
//			snmpTrapEvent.setNameType(trapEvent.getNameType());
//			snmpTrapEvent.setName(trapEvent.getName());
//			snmpTrapEvent.setSourceType(trapEvent.getSourceType());
//			snmpTrapEvent.setSourceID(trapEvent.getSourceID());
//			snmpTrapEvent.setAirScreenTime(trapEvent.getAirScreenTime());
//			snmpTrapEvent.setRuleName(trapEvent.getRuleName());
//			snmpTrapEvent.setInstanceID(trapEvent.getInstanceID());
//
//			break;
//		}
//
//		case BeTrapEvent.TYPE_BANDWIDTHSENTINELEVENT: {
//			snmpTrapEvent.setTrapType(SnmpConstance.AH_BANDWIDTH_SENTINEL_EVENT);
//			snmpTrapEvent.setAhObjectName(trapEvent.getObjectName());
//			snmpTrapEvent.setAhTrapDesc(trapEvent.getDescribe());
//			snmpTrapEvent.setAhCode(trapEvent.getCode());
//			snmpTrapEvent.setAhIfIndex(trapEvent.getIfIndex());
//			snmpTrapEvent.setAhRemoteId(trapEvent.getRemoteID());
//			snmpTrapEvent.setBandwidthSentinelStatus(trapEvent.getBandwidthSentinelStatus());
//			snmpTrapEvent.setGuaranteedBandWidth(trapEvent.getGuaranteedBandWidth());
//			snmpTrapEvent.setActualBandWidth(trapEvent.getActualBandWidth());
//			snmpTrapEvent.setBandWidthAction(trapEvent.getBandWidthAction());
//
//			break;
//		}
//
//		case BeTrapEvent.TYPE_VPN: {
//			snmpTrapEvent.setTrapType(SnmpConstance.AH_VPN_SERVICE_EVENT);
//			snmpTrapEvent.setAhObjectName(trapEvent.getObjectName());
//			snmpTrapEvent.setAhTrapDesc(trapEvent.getDescribe());
//
//			break;
//		}
//
//		case BeTrapEvent.TYPE_SSID_STATECHANGE: {
//			snmpTrapEvent.setTrapType(SnmpConstance.AH_SSID_STATECHANGE_EVENT);
//			snmpTrapEvent.setAhObjectName(trapEvent.getObjectName());
//			snmpTrapEvent.setAhTrapDesc(trapEvent.getDescribe());
//			snmpTrapEvent.setAhIfIndex(trapEvent.getIfIndex());
//			snmpTrapEvent.setClientBSSID(trapEvent.getClientBSSID());
//			snmpTrapEvent.setAhSSID(trapEvent.getClientSSID());
//			snmpTrapEvent.setState(trapEvent.getState());
//
//			break;
//		}
//
//		case BeTrapEvent.TYPE_INTERFACECLIENTTRAP: {
//			snmpTrapEvent.setTrapType(SnmpConstance.AH_INTERFACE_CLIENT_EVENT);
//			snmpTrapEvent.setAhObjectName(trapEvent.getObjectName());
//			snmpTrapEvent.setAhTrapDesc(trapEvent.getDescribe());
//			snmpTrapEvent.setAhCode(trapEvent.getCode());
//			snmpTrapEvent.setSourceType(trapEvent.getSourceType());
//			snmpTrapEvent.setAhIfIndex(trapEvent.getIfIndex());
//			snmpTrapEvent.setAhRemoteId(trapEvent.getRemoteID());
//			snmpTrapEvent.setAhSSID(trapEvent.getClientSSID());
//			snmpTrapEvent.setAlertType(trapEvent.getAlertType());
//			snmpTrapEvent.setThresholdValue(trapEvent.getThresholdValue());
//			snmpTrapEvent.setShorttermValue(trapEvent.getShorttermValue());
//			snmpTrapEvent.setSnapshotValue(trapEvent.getSnapshotValue());
//			snmpTrapEvent.setAhSeverity(trapEvent.getSeverity());
//			snmpTrapEvent.setFailureFlag(trapEvent.isFailureFlag());
//
//			break;
//		}
//
//		case BeTrapEvent.TYPE_SECURITY_ALARM: {
//			snmpTrapEvent.setTrapType(SnmpConstance.AH_SECURITY_ALARM_TRAP);
//			snmpTrapEvent.setAhObjectName(trapEvent.getObjectName());
//			snmpTrapEvent.setAhTrapDesc(trapEvent.getDescribe());
//			snmpTrapEvent.setAhCode(trapEvent.getCode());
//			snmpTrapEvent.setAhSeverity(trapEvent.getSeverity());
//			snmpTrapEvent.setAhProbableCause(trapEvent.getProbableCause());
//
//			break;
//		}
//
//		default: {
//			DebugUtil
//					.commonDebugWarn("BeCommunicationProcessor.parseTrap(): Invalid trap type when parse trap event. type="
//							+ trapEvent.getTrapType());
//
//			// discard
//			return null;
//		}
//		}
//
//		snmpTrapEvent.setTimeStamp(System.currentTimeMillis());
//		snmpTrapEvent.setTimeZone(trapEvent.getMessageTimeZone());
//
//		// snmpTrapEvent.setAhTimeDisplay(AhDateTimeUtil.getCurrentDate("MMM dd,yyyy hh:mm:ss a"));
//
//		return snmpTrapEvent;
//	}

	/**
	 * dispatch response communication event
	 *
	 * @param event
	 *            -
	 * @throws Exception
	 *             -
	 */
	void dispatchResponse(BeCommunicationEvent event) throws Exception {
		dispatchResponse(event,true);
	}
	/**
	 * dispatch response communication event
	 *
	 * @param event
	 *            -
	 * @throws Exception
	 *             -
	 */
	void dispatchResponse(BeCommunicationEvent event,boolean removeRequest) throws Exception {

		// set ap obj
		// maybe there's no ap object in BeCommunication event.
		BeCommunicationEvent requestEvent = requestManager.getRequestObj(event.getSerialNum());
		if (requestEvent == null) {
			// mark: this api maybe be invoked when event request
			requestEvent = requestManager.getResultEventRequestObj(event.getSequenceNum());
		}
		if (requestEvent != null) {
			// if (requestEvent.getSimpleHiveApNoQuery() == null) {
			// event.setAp(requestEvent.getAp());
			// } else {
			// event.setSimpleHiveAp(requestEvent.getSimpleHiveAp());
			// }
			event.setAp(requestEvent.getApNoQuery());
			event.setSimpleHiveAp(requestEvent.getSimpleHiveAp());

			event.setSequenceNum(requestEvent.getSequenceNum());
		}

		// special for capwap client info response
		if (event.getMsgType() == BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTINFORSP
				&& requestEvent != null) {
			event = handleCapwapClientInfoRsp(event, requestEvent);
		}

		// special for capwap client event response
		if (event.getMsgType() == BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP
				&& requestEvent != null) {
			event = handleCapwapClientEventRsp(event, requestEvent);
		}

		if (isSyncResponse(event.getSerialNum())) {
			handleSyncResponse(event);
		} else if (isSyncGroupResponse(event.getSerialNum())) {
			handleSyncGroupReponse(event);
		} else {
			HmBeEventUtil.eventGenerated(event);

			// modify mark: if request with result event, let's remove request when receive result
			// event
			if (event.getResult() == BeCommunicationConstant.RESULTTYPE_SUCCESS
					&& isResponseMessageWithResultEvent(event.getMsgType())) {
				requestManager.migrateRequest(event);
			}

			// when time out, event removed by BeCommunicationRequestManager
			if (removeRequest) {
				// remove request from manager
				requestManager.removeRequest(event.getSerialNum());
			}
		}
	}

	/**
	 * special process for capwap client info response
	 *
	 * @param event
	 *            -
	 * @param requestEvent
	 *            -
	 * @return -
	 * @throws Exception
	 *             -
	 */
	private BeCommunicationEvent handleCapwapClientInfoRsp(BeCommunicationEvent event,
			BeCommunicationEvent requestEvent) throws Exception {
		BeCapwapClientInfoEvent infoEvent = (BeCapwapClientInfoEvent) event;
		infoEvent.setQueryType(((BeCapwapClientInfoEvent) requestEvent).getQueryType());
		BeCommunicationEvent cacheObj = infoEvent;

		switch (infoEvent.getQueryType()) {
		case BeCapwapClientInfoEvent.TYPE_DELETECOOKIE:
			// Conserve necessary properties which may be used for some receiving terminal processes
			// to this response.
			BeDeleteCookieEvent deleteCookieEvent = new BeDeleteCookieEvent();
			deleteCookieEvent.setCookie(((BeDeleteCookieEvent) requestEvent).getCookie());
			deleteCookieEvent.setCookieType(((BeDeleteCookieEvent) requestEvent).getCookieType());
			infoEvent = deleteCookieEvent;
			break;
		case BeCapwapClientInfoEvent.TYPE_CPUMEMQUERY:
			infoEvent = new BeCPUMemInfoEvent();
			break;
		case BeCapwapClientInfoEvent.TYPE_TEACHERVIEW_STUDUENTINFO:
			infoEvent = new BeTeacherViewStudentInfoEvent();
			break;
		case BeCapwapClientInfoEvent.TYPE_TEACHERVIEW_CLASSINFO:
			infoEvent = new BeTeacherViewClassInfoEvent();
			break;
		default:
			break;
		}

		infoEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTINFORSP);
		infoEvent.setSerialNum(cacheObj.getSerialNum());
		infoEvent.setPacket(cacheObj.getPacket());
		infoEvent.setSimpleHiveAp(cacheObj.getSimpleHiveAp());
		infoEvent.setAp(cacheObj.getApNoQuery());
		infoEvent.setResult(cacheObj.getResult());
		infoEvent.setMessageTimeStamp(cacheObj.getMessageTimeStamp());
		infoEvent.setMessageTimeZone(cacheObj.getMessageTimeZone());

		infoEvent.parsePacket();

		return infoEvent;
	}

	/**
	 * special process for capwap client event response
	 *
	 * @param event
	 *            -
	 * @param requestEvent
	 *            -
	 * @return -
	 * @throws Exception
	 *             -
	 */
	private BeCommunicationEvent handleCapwapClientEventRsp(BeCommunicationEvent event,
			BeCommunicationEvent requestEvent) throws Exception {
		BeCapwapClientEvent capwapClientEvent = (BeCapwapClientEvent) event;
		capwapClientEvent.setQueryType(((BeCapwapClientEvent) requestEvent).getQueryType());
		BeCommunicationEvent cacheObj = event;

		switch (capwapClientEvent.getQueryType()) {
		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_IDPMITIGATION:
			capwapClientEvent = new BeIDPMitigationQueryEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SHOWCAPTURESTATUS:
			capwapClientEvent = new BeShowCaptureStatusEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTMONITORING:
			capwapClientEvent = new BeClientMonitoringEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VLANPROBE:
			capwapClientEvent = new BeVLANProbeEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LOCATIONTRACK:
			capwapClientEvent = new BeLocationTrackingEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PATHPROBE:
			capwapClientEvent = new BePathProbeEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SHOWACSPSTATS:
			capwapClientEvent = new BeShowACSPStatsEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_POESTATUS:
			capwapClientEvent = new BePOEStatusEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_INTERFERENCEMAP:
			capwapClientEvent = new BeInterferenceMapEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LLDPCDPINFO:
			capwapClientEvent = new BeLLDPCDPInfoEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VPNSTATUS:
			capwapClientEvent = new BeVPNStatusEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PCIDATA:
			capwapClientEvent = new BePCIDataEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_HIVECOMM:
			capwapClientEvent = new BeHiveCommEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_INTERFACECLIENT:
			capwapClientEvent = new BeInterfaceClientEvent();
			break;

		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_AAATEST:
			capwapClientEvent = new BeAAATestEvent();
			break;
		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RETRIEVELDAPINFO:
			capwapClientEvent = new BeRetrieveLDAPInfoEvent();
			break;
		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LDAPTREEINFO:
			capwapClientEvent = new BeLDAPTreeInfoEvent();
			break;
		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_QUERYADINFO:
			capwapClientEvent = new BeQueryADInfoEvent();
			break;
		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_PORT_AVAILABILITY:
			capwapClientEvent = new BePortAvailabilityEvent();
			break;
		case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_BONJOUR_GATEWAY:
			capwapClientEvent = new BeBonjourGatewayEvent();
			break;
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_OTP:
        	capwapClientEvent = new BeOTPStatusEvent();
        	break;
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RADSEC_CERT_CREATION:
            capwapClientEvent = new BeRadSecCertCreationEvent();
            break;
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RADSEC_CERT_REVOKEN:
            capwapClientEvent = new BeRadSecCertRevokenEvent();
            break;
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SWITCH_PORT_INFO:
            capwapClientEvent = new BeSwitchPortInfoEvent();
            break;
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SWITCH_PORT_STATS:
            capwapClientEvent = new BeSwitchPortStatsEvent();
            break;
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SWITCH_SYSTEM_INFO:
            capwapClientEvent = new BeSwitchSystemInfoEvent();
            break;
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_APPLICATION_FLOW_INFO:
            capwapClientEvent = new BeApplicationFlowInfoEvent();
            break;
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_ROUTER_LTE_VZ_INFO:
        	capwapClientEvent= new BeRouterLTEVZInfoEvent();
        	break;
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SWITCH_PORT_STATS_REPORT:
        	capwapClientEvent = new BeSwitchPortStatsReportEvent();
        	break;
        case BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RADSEC_PROXY_INFO:
        	capwapClientEvent = new BeRadsecProxyInfoQueryEvent();
        	break;
		default:
			break;
		}

		capwapClientEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP);
		capwapClientEvent.setSerialNum(cacheObj.getSerialNum());
		capwapClientEvent.setPacket(cacheObj.getPacket());
		capwapClientEvent.setSimpleHiveAp(cacheObj.getSimpleHiveAp());
		capwapClientEvent.setAp(cacheObj.getApNoQuery());
		capwapClientEvent.setResult(cacheObj.getResult());
		capwapClientEvent.setMessageTimeStamp(cacheObj.getMessageTimeStamp());
		capwapClientEvent.setMessageTimeZone(cacheObj.getMessageTimeZone());

		capwapClientEvent.parsePacket();

		return capwapClientEvent;
	}

	/**
	 * create connect close/timeout event <br>
	 * Mark: this change need to be verified.
	 *
	 * @param req
	 *            -
	 * @param result
	 *            -
	 * @return -
	 */
	static BeCommunicationEvent createRspEvent(BeCommunicationEvent req, byte result) {
		BeCommunicationEvent responseEvent;
		int msgType = req.getMsgType();
		switch (msgType) {
		case BeCommunicationConstant.MESSAGETYPE_CAPWAPDTLSCONFIGREQ:
			responseEvent = new BeCapwapDTLSConfigEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_CAPWAPDTLSCONFIGRSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_CAPWAPSERVERCONFIGREQ:
			responseEvent = new BeCapwapServerParamConfigEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_CAPWAPSERVERCONFIGRSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_CAPWAPSERVERLICENSEREQ:
			responseEvent = new BeCapwapServerLicenseEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_CAPWAPSERVERLICENSERSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_CLIREQ:
			responseEvent = new BeCliEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_CLIRSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_DELETEAPCONNECTREQ:
			responseEvent = new BeDeleteAPConnectEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_DELETEAPCONNECTRSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_GETSTATISTICREQ:
			responseEvent = new BeGetStatisticEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_GETSTATISTICRSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_IDPQUERYREQ:
			responseEvent = new BeIDPQueryEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_IDPQUERYRSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_L3ROAMINGCONFIGREQ:
			responseEvent = new BeL3RoamingConfigEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_L3ROAMINGCONFIGRSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_SHUTDOWNREQ:
			responseEvent = new BeShutDownEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_SHUTDOWNRSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_WTPCONTROLREQ:
			responseEvent = new BeWTPEventControlEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_WTPCONTROLRSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_ABORTREQ:
			responseEvent = new BeAbortEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_ABORTRSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_SHOWCWPDIRECTORYREQ:
			responseEvent = new BeCWPDirectoryEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_SHOWCWPDIRECTORYRSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_HOSTIDENTIFICATIONKEYREQ:
			responseEvent = new BeHostIdentificationKeyEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_HOSTIDENTIFICATIONKEYRSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTINFOREQ:
			responseEvent = new BeCapwapClientInfoEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTINFORSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTREQ:
			responseEvent = new BeCapwapClientEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_SIMULATEHIVEAPREQ:
			responseEvent = new BeSimulateHiveAPEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_SIMULATEHIVEAPRSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_CAPWAPSTATREQ:
			responseEvent = new BeCapwapStatisticsEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_CAPWAPSTATRSP);
			break;

		case BeCommunicationConstant.MESSAGETYPE_CAPWAPPAYLOADREQ:
			responseEvent = new BeCapwapPayloadEvent();
			responseEvent.setMsgType(BeCommunicationConstant.MESSAGETYPE_CAPWAPPAYLOADRSP);
			break;

		default:
			responseEvent = new BeCommunicationEvent();
			break;
		}

		req.setResult(result);
		responseEvent.setResult(result);
		responseEvent.setPacket(new byte[0]);
		responseEvent.setSerialNum(req.getSerialNum());
		responseEvent.setSequenceNum(req.getSequenceNum());
		responseEvent.setSimpleHiveAp(req.getSimpleHiveAp());
		responseEvent.setAp(req.getApNoQuery());

		// set serial number
		if (result == BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE) {
			responseEvent.setSerialNum(BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED);
		}

		return responseEvent;
	}

	/**
	 * Send response event.
	 *
	 * @param rsp
	 *            -
	 * @return -
	 */
	public boolean sendResponse(BeCommunicationEvent rsp) {
		if (!myClientSocket.getConnectState()) {
			DebugUtil
					.commonDebugWarn("BeCommunicationProcessor.sendResponse(): Disconnect to capwap, send response failed! Message type is "
							+ rsp.getMsgType());

			return false;
		}

		int serialNum = rsp.getSequenceNum() > 0 ? rsp.getSequenceNum() : getNextSerialNum();
		rsp.setSerialNum(serialNum);

		byte[] msgBuf = rsp.getPacket();
		int int_Len = (msgBuf == null) ? 11 : 11 + msgBuf.length;
		ByteBuffer buf = ByteBuffer.allocate(4 + int_Len);
		buf.putInt(int_Len);
		buf.putShort((short) rsp.getMsgType());
		buf.putInt(serialNum);
		buf.putInt((int) System.currentTimeMillis()); // time stamp
		buf.put((byte) 0); // time zone
		if (msgBuf != null) {
			buf.put(msgBuf);
		}
		buf.flip();

		try {
			myClientSocket.send(buf);

			// log
//			DebugUtil
//					.commonDebugInfo("BeCommunicationProcessor.sendResponse(): Send response success! Message type is "
//							+ rsp.getMsgType());
		} catch (Exception e) {
			DebugUtil.commonDebugWarn(
					"BeCommunicationProcessor.sendResponse():  Send response failed! Message type is "
							+ rsp.getMsgType() + "\n Exception message: ", e);
			// close connection
			myClientSocket.close();
			return false;
		}

		return true;
	}

	/**
	 * asynchronize request
	 *
	 * @param req
	 *            req message obj
	 * @param timeOut
	 *            define time out number
	 * @return serialNum
	 * @throws BeCommunicationEncodeException
	 *             -
	 */
	public int sendRequest(BeCommunicationEvent req, int timeOut)
			throws BeCommunicationEncodeException {
		// check connect state
		if (!myClientSocket.getConnectState()) {
			DebugUtil
					.commonDebugWarn("BeCommunicationProcessor.sendRequest(): Disconnect to capwap, send request failed! Message type is "
							+ req.getMsgType());

			// report connect close
			HmBeEventUtil.eventGenerated(createRspEvent(req,
					BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE));

			return BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED;
		}

		// mark: serial number should combine with sequence number in next version
		int serialNum = req.getSequenceNum() > 0 ? req.getSequenceNum() : getNextSerialNum();
		req.setSerialNum(serialNum);

		// check build result
		if (req.getResult() == BeCommunicationConstant.RESULTTYPE_MESSAGELENEXCEEDLIMIT) {
			BeCommunicationEvent rspEvent = createRspEvent(req, req.getResult());
			HmBeEventUtil.eventGenerated(rspEvent);

			return serialNum;
		}

		// set timeout value before put into request manager.
		req.setTimeout(timeOut);

		// add request to manager
		byte result = requestManager.addRequest(req);
		if (result != BeCommunicationConstant.RESULTTYPE_SUCCESS) {
			BeCommunicationEvent rspEvent = createRspEvent(req, result);
			HmBeEventUtil.eventGenerated(rspEvent);

			return serialNum;
		}

		byte[] msgBuf = req.getPacket();
		int int_Len = (msgBuf == null) ? 11 : 11 + msgBuf.length;
		ByteBuffer buf = ByteBuffer.allocate(4 + int_Len);
		buf.putInt(int_Len);
		buf.putShort((short) req.getMsgType());
		buf.putInt(serialNum);
		buf.putInt((int) System.currentTimeMillis()); // time stamp
		buf.put((byte) 0); // time zone
		if (msgBuf != null) {
			buf.put(msgBuf);
		}
		buf.flip();

		try {
			myClientSocket.send(buf);

			// log
//			DebugUtil
//					.commonDebugInfo("BeCommunicationProcessor.sendRequest(): Send request success! Message type is "
//							+ req.getMsgType());
		} catch (Exception e) {
			DebugUtil.commonDebugWarn(
					"BeCommunicationProcessor.sendRequest():  Send request failed! Message type is "
							+ req.getMsgType() + "\n Exception message: ", e);
			// close connection
			myClientSocket.close();

			// remove request from manager
			requestManager.removeRequest(serialNum);

			// report connect close
			req.setResult(BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE);
			HmBeEventUtil.eventGenerated(req);
			return BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED;
		}

		return serialNum;
	}

	/**
	 * asynchronize request
	 *
	 * @param req
	 *            req message obj,timeout is default value
	 * @return serialNum
	 * @throws BeCommunicationEncodeException
	 *             -
	 */
	public int sendRequest(BeCommunicationEvent req) throws BeCommunicationEncodeException {
		return sendRequest(req, BeCommunicationConstant.DEFAULTTIMEOUT);
	}

	/**
	 * send sync request
	 *
	 * @param req
	 *            -
	 * @return response packet
	 * @throws BeCommunicationEncodeException
	 *             -
	 */
	public BeCommunicationEvent sendSyncRequest(BeCommunicationEvent req)
			throws BeCommunicationEncodeException {
		return sendSyncRequest(req, BeCommunicationConstant.DEFAULTTIMEOUT);
	}

	/**
	 * send sync request
	 *
	 * @param req
	 *            -
	 * @param timeout
	 *            -
	 * @return response packet
	 * @throws BeCommunicationEncodeException
	 *             -
	 */
	public BeCommunicationEvent sendSyncRequest(BeCommunicationEvent req, int timeout)
			throws BeCommunicationEncodeException {
		// check connect state of HM and capwap
		if (!myClientSocket.getConnectState()) {
			return createRspEvent(req, BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE);
		}

		SyncResponseListener syncResponse = new SyncResponseListener();
		synchronized (syncResponse) {
			int serialNum = sendRequest(req, timeout);

			// check send result.
			if (req.getResult() != BeCommunicationConstant.RESULTTYPE_SUCCESS) {
				return createRspEvent(req, req.getResult());
			}

			syncResponseMap.put(serialNum, syncResponse);

			int sequenceNumber = getSequenceNumber(req);
			if (sequenceNumber > 0) {
				syncSequenceSerialNumMap.put(sequenceNumber, serialNum);
			}

			try {
				syncResponse.wait((timeout + 5) * 1000);
				BeCommunicationEvent rsp = syncResponse.getResponse();
				if (rsp == null) {
					rsp = createRspEvent(req, BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT);
				}
				return rsp;
			} catch (InterruptedException e) {
				DebugUtil
						.commonDebugWarn(
								"BeCommunicationProcessor.sendSyncRequest(): current waiting thread be interrupted.",
								e);
				return null;
			} finally {
				syncResponseMap.remove(serialNum);
				syncSequenceSerialNumMap.remove(sequenceNumber);
				// requestManager.removeRequest(req.getSerialNum());
				// requestManager.removeResultEventRequest(req.getSequenceNum());
			}
		}
	}

	/**
	 * send group request synchronously
	 *
	 * @param requestList
	 *            -
	 * @return -
	 * @throws BeCommunicationEncodeException
	 *             -
	 */
	public List<BeCommunicationEvent> sendSyncGroupRequest(List<BeCommunicationEvent> requestList)
			throws BeCommunicationEncodeException {
		return sendSyncGroupRequest(requestList, BeCommunicationConstant.DEFAULTTIMEOUT);
	}

	/**
	 * send group request synchronously
	 *
	 * @param requestList
	 *            -
	 * @param timeOut
	 *            -
	 * @return -
	 * @throws BeCommunicationEncodeException
	 *             -
	 */
	public List<BeCommunicationEvent> sendSyncGroupRequest(
			List<? extends BeCommunicationEvent> requestList, int timeOut)
			throws BeCommunicationEncodeException {
		// check connect state of HM and capwap
		if (!myClientSocket.getConnectState()) {
			List<BeCommunicationEvent> responseList = new ArrayList<BeCommunicationEvent>(
					requestList.size());
			for (BeCommunicationEvent req : requestList) {
				responseList.add(createRspEvent(req,
						BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE));
			}

			return responseList;
		}

		SyncGroupResponseListener groupResponseListener = new SyncGroupResponseListener(requestList);
		List<Integer> serialNumList = new ArrayList<Integer>(requestList.size());
		List<Integer> sequenceNumberList = new ArrayList<Integer>(requestList.size());

		synchronized (groupResponseListener) {
			for (BeCommunicationEvent request : requestList) {
				int serialNum = sendRequest(request, timeOut);

				if (request.getResult() == BeCommunicationConstant.RESULTTYPE_SUCCESS) {
					syncGroupResponseMap.put(serialNum, groupResponseListener);

					int sequenceNumber = getSequenceNumber(request);
					if (sequenceNumber > 0) {
						syncGroupSequenceSerialNumMap.put(sequenceNumber, serialNum);
					}

					serialNumList.add(serialNum);
					sequenceNumberList.add(sequenceNumber);
				} else {
					boolean isAll = groupResponseListener.onResponseExt(createRspEvent(request,
							request.getResult()));
					if (isAll) {
						return groupResponseListener.getResponseList();
					}
				}
			}

			try {
				groupResponseListener.wait((timeOut + 5) * 1000);
				return groupResponseListener.getResponseList();
			} catch (InterruptedException e) {
				DebugUtil
						.commonDebugWarn(
								"BeCommunicationProcessor.sendSyncGroupRequest(): current waiting thread be interrupted.",
								e);
				return null;
			} finally {
				for (Integer serialNum : serialNumList) {
					syncGroupResponseMap.remove(serialNum);
				}

				for (Integer sequenceNumber : sequenceNumberList) {
					syncGroupSequenceSerialNumMap.remove(sequenceNumber);
				}

				// thread lock maybe caused when timeout occur in group request,
				// remove request from manager when receive response/event

				// for (BeCommunicationEvent req : requestList)
				// {
				// requestManager.removeRequest(req);
				// }
			}
		}
	}

	/**
	 * api for bundle cli request,it will be sent only when all ap have free channel.
	 *
	 * @param requestMap
	 *            : key is ap mac, value is event
	 * @param timeOut
	 *            : seconds unit for average value
	 *
	 * @return map: key is ap mac, value is error result(defined in BeCommunicationConstant) or
	 *         serial number
	 * @throws BeCommunicationEncodeException
	 *             -
	 */
	public Map<String, Integer> sendBundleClisRequest(Map<String, BeCliEvent> requestMap,
			int timeOut) throws BeCommunicationEncodeException {
		Map<String, Integer> resultMap = new HashMap<String, Integer>(requestMap.size());
		for (String apMac : requestMap.keySet()) {
			resultMap.put(apMac, BeCommunicationConstant.ERRORCODE_REQUEST_NOERROR);
		}

		// request permit for these configuration cli
		Set<String> apMacSet = requestManager.requestConfigurationCliPermit(requestMap.keySet());
		if (!apMacSet.isEmpty()) {
			// some failed.
			for (String apMac : apMacSet) {
				resultMap.put(apMac, BeCommunicationConstant.ERRORCODE_REQUEST_RESOUCEBUSY);
			}

			return resultMap;
		}

		// all could be sent
		for (String apMac : requestMap.keySet()) {
			BeCliEvent requestEvent = requestMap.get(apMac);
			int serialNumber = sendRequest(requestEvent, timeOut);
			if (serialNumber == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED) {
				resultMap.put(apMac, BeCommunicationConstant.ERRORCODE_REQUEST_CAPWAPDISCONNECT);
			} else {
				resultMap.put(apMac, serialNumber);
			}
		}

		return resultMap;
	}

	/**
	 * get sequenceNumber from request object, return 0 if doesn't existed.
	 *
	 * @param req
	 *            -
	 * @return -
	 */
	private int getSequenceNumber(BeCommunicationEvent req) {
		int sequenceNumber;

		// int requestType = req.getMsgType();
		// switch (requestType) {
		// case BeCommunicationConstant.MESSAGETYPE_GETSTATISTICREQ: {
		// sequenceNumber = ((BeGetStatisticEvent) req).getStatsSerialNum();
		//
		// break;
		// }
		//
		// case BeCommunicationConstant.MESSAGETYPE_IDPQUERYREQ: {
		// sequenceNumber = ((BeIDPQueryEvent) req).getIdpSequenceNumber();
		//
		// break;
		// }
		//
		// case BeCommunicationConstant.MESSAGETYPE_CLIREQ: {
		// sequenceNumber = ((BeCliEvent) req).getCliSerialNum();
		//
		// break;
		// }
		//
		// case BeCommunicationConstant.MESSAGETYPE_SHOWCWPDIRECTORYREQ: {
		// sequenceNumber = ((BeCWPDirectoryEvent) req).getCwpSerialNumber();
		//
		// break;
		// }
		//
		// default:
		// break;
		// }

		sequenceNumber = req.getSequenceNum();

		return sequenceNumber;
	}

	/**
	 * check whether this package is for sync request
	 *
	 * @param serialNumber
	 *            -
	 * @return -
	 */
	private boolean isSyncResponse(int serialNumber) {
		return syncResponseMap.containsKey(serialNumber);
	}

	/**
	 * check whether this package is for sync group request
	 *
	 * @param serialNumber
	 *            -
	 * @return -
	 */
	private boolean isSyncGroupResponse(int serialNumber) {
		return syncGroupResponseMap.containsKey(serialNumber);
	}

	/**
	 * response message which device will return result with result event.
	 *
	 * @param messageType
	 *            -
	 * @return -
	 */
	private boolean isResponseMessageWithResultEvent(int messageType) {
		if (messageType == BeCommunicationConstant.MESSAGETYPE_GETSTATISTICRSP
				|| messageType == BeCommunicationConstant.MESSAGETYPE_IDPQUERYRSP
				|| messageType == BeCommunicationConstant.MESSAGETYPE_CLIRSP
				|| messageType == BeCommunicationConstant.MESSAGETYPE_SHOWCWPDIRECTORYRSP
				|| messageType == BeCommunicationConstant.MESSAGETYPE_HOSTIDENTIFICATIONKEYRSP
				|| messageType == BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP
				|| messageType == BeCommunicationConstant.MESSAGETYPE_CAPWAPPAYLOADRSP) {
			return true;
		}

		return false;
	}

	/**
	 * handle sync response
	 *
	 * @param event
	 *            -
	 */
	private void handleSyncResponse(BeCommunicationEvent event) {
		ResponseListener responseListener = syncResponseMap.get(event.getSerialNum());

//		DebugUtil
//				.commonDebugInfo("BeCommunicationProcessor.handleSyncResponse(): Success receive sync response, message type is "
//						+ event.getMsgType()
//						+ ",serialNumber is "
//						+ event.getSerialNum()
//						+ ",result is " + event.getResult());

		int msgType = event.getMsgType();
		if (isResponseMessageWithResultEvent(msgType)) {
			if (event.getResult() == BeCommunicationConstant.RESULTTYPE_SUCCESS) {
				requestManager.migrateRequest(event);

				return;
			}
		}

		// return response with result
		responseListener.onResponse(event);

		if (event.getResult() != BeCommunicationConstant.RESULTTYPE_TIMEOUT
				&& event.getResult() != BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT) {
			// remove req obj from request manager
			requestManager.removeRequest(event.getSerialNum());
		}
	}

	/**
	 * handle sync group response
	 *
	 * @param event
	 *            -
	 */
	private void handleSyncGroupReponse(BeCommunicationEvent event) {
//		DebugUtil
//				.commonDebugInfo("BeCommunicationProcessor.handleSyncGroupReponse(): Receive sync response, message type is "
//						+ event.getMsgType()
//						+ ",serialNumber is "
//						+ event.getSerialNum()
//						+ ",result is " + event.getResult());

		int msgType = event.getMsgType();
		if (isResponseMessageWithResultEvent(msgType)) {
			if (event.getResult() == BeCommunicationConstant.RESULTTYPE_SUCCESS) {
				requestManager.migrateRequest(event);

				return;
			}
		}

		ResponseListener responseListener = syncGroupResponseMap.get(event.getSerialNum());

		// return response with result
		responseListener.onResponse(event);

		// it's different process with single request,We need remove req
		// obj from manager queue in time avoid all request obj return
		// as timeout response in the end.
		if (event.getResult() != BeCommunicationConstant.RESULTTYPE_TIMEOUT
				&& event.getResult() != BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT) {
			// remove req obj from request manager
			requestManager.removeRequest(event.getSerialNum());
		}
	}

	/**
	 * handle capwap request from capwap servers, such as portal
	 *
	 * @param byteArray
	 *            -
	 */
	private void handleCapwapPayloadRequest(byte[] byteArray) {
		try {
			BeCapwapPayloadEvent payloadEvent = new BeCapwapPayloadEvent();
			payloadEvent.setPacket(byteArray);
			byte[] remaining = payloadEvent.parseRequest();
			String portalMac = payloadEvent.getApMac();

			if (payloadEvent.getQueryType() == BeCapwapPayloadEvent.QUERYTYPE_PORTALANDHM) {
				BePortalHMPayloadEvent portalEvent = new BePortalHMPayloadEvent();
				portalEvent.setPacket(remaining);
				remaining = portalEvent.parseRequest();
				int sequenceNumber = portalEvent.getSequenceNum();

				switch (portalEvent.getOperationType()) {
				case BePortalHMPayloadEvent.OPERATIONTYPE_USER_CREATE:
					portalEvent = new BeCreateUserEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_USER_MODIFY:
					portalEvent = new BeModifyUserEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_USER_REMOVE:
					portalEvent = new BeRemoveUserEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_USER_SYNC:
					portalEvent = new BeSyncUserEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_QUERY_VHMUSERS:
					portalEvent = new BeQueryVhmUsersEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_CREATE:
					portalEvent = new BeCreateVHMEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_MODIFY:
					portalEvent = new BeModifyVHMEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_MOVE:
					portalEvent = new BeMoveVHMEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_REMOVE:
					portalEvent = new BeRemoveVHMEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_STATUSCHANGE:
					portalEvent = new BeVHMStatusChangeEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_RESET_VHM_ADMIN_PASSWORD:
					portalEvent = new BeResetVhmAdminPasswordEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_SEND_CREDENTIAL:
					portalEvent = new BeSendCredentialEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_HMOL_MODIFY:
					portalEvent = new BeModifyHMOLEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_HMOL_POWEROFF:
					portalEvent = new BePoweroffHmolEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_HA_BREAK:
					portalEvent = new BeHABreakEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_HA_ENABLE:
					portalEvent = new BeHAEnableEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_HA_JOIN:
					portalEvent = new BeHAJoinEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_HA_STATUSINFO:
					portalEvent = new BeHAStatusInfoEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_HA_SWITCHOVER:
					portalEvent = new BeHASwitchOverEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_HA_MAINTENANCE:
					portalEvent = new BeHAMaintenanceEvent();
					break;
				case BePortalHMPayloadEvent.OPERATIONTYPE_HMOL_QUERYINFO:
				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_QUERY_MOVING_STATUS:
				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_CLEAR_MOVING_STATUS:
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_HMOL_VERSIONUPDATEINFO:
					portalEvent = new BeVersionUpdateInfoEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_HM_SEARCH_AP:
					portalEvent = new BeSearchAPFromHMEvent();
					break;

				case BePortalHMPayloadEvent.OPERATIONTYPE_HM_DELETE_AP:
					portalEvent = new BeDeleteAPFromHMEvent();
					break;
					
				case BePortalHMPayloadEvent.OPERATIONTYPE_VHM_GET_USER_GROUPS:
					portalEvent = new BeQueryGroupInfoFromHMEvent();
					break;

				default:
					DebugUtil
							.commonDebugError("BeCommunicationProcessor.handleCapwapPayloadRequest() Parse error, not supported operation type in CAPWAP payload event, query type is "
									+ portalEvent.getOperationType());
					return;
				}

				if (portalEvent.getOperationType() == BePortalHMPayloadEvent.OPERATIONTYPE_HMOL_QUERYINFO
						|| portalEvent.getOperationType() == BePortalHMPayloadEvent.OPERATIONTYPE_VHM_QUERY_MOVING_STATUS
						|| portalEvent.getOperationType() == BePortalHMPayloadEvent.OPERATIONTYPE_VHM_CLEAR_MOVING_STATUS) {
					// don't parse packet 2010.4.8
				} else {
					portalEvent.setPacket(remaining);
					portalEvent.parseRequest();
				}
				portalEvent.setSequenceNum(sequenceNumber);
				portalEvent.setApMac(portalMac);

				HmBeCommunicationUtil.getBeCAPWAPRequestProcessor().addEvent(portalEvent);
			} else {
				DebugUtil
						.commonDebugError("BeCommunicationProcessor.handleCapwapPayloadRequest() Parse error, not supported query type in CAPWAP payload event, query type is "
								+ payloadEvent.getQueryType());
			}
		} catch (Exception e) {
			DebugUtil.commonDebugError(
					"BeCommunicationProcessor.handleCapwapPayloadRequest() catch exception. ", e);
		}
	}

	/**
	 * get next valid serial number for packet
	 *
	 * @return Serial Number
	 */
	synchronized int getNextSerialNum() {
		// mark: minimum sequence number is 101, restriction from AP
		if (++mySerialNumber == 0X7FFFFFFF) {
			mySerialNumber = 101;
		}
		// mySerialNumber = mySerialNumber % 0X7FFFFFFF + 1;
		return mySerialNumber;
	}

	public boolean isContinue() {
		return isContinue;
	}

	public void setContinue(boolean isContinue) {
		this.isContinue = isContinue;
	}

	/**
	 * shut down thread
	 */
	public void shutdown() {
		syncResponseMap.clear();
		syncSequenceSerialNumMap.clear();
		syncGroupResponseMap.clear();
		syncGroupSequenceSerialNumMap.clear();

		requestManager.shutDown();
		// requestManager = null;

		// send HA status
		distributeHAStatus();

		BeShutDownEvent req = new BeShutDownEvent();
		setContinue(false);

		try {
			sendRequest(req);
		} catch (Exception e) {
			e.printStackTrace();
		}

		myClientSocket.close();

		/*
		 * this must be invoked. otherwise, in HA modal, when start be next time,
		 * IllegalThreadStateException will occur. joseph chen, 09-02-11
		 *
		 *
		 * with instance = null, do not need to do this. further, if do this, this thread may throw
		 * out an exception when closing this processor. because this thread may be in sleeping (in
		 * run method) when call interrupt().
		 */
		// this.interrupt();
		// instance = null;

		DebugUtil
				.commonDebugInfo("BeCommunicationProcessor.shutdown(): Shutting down successfully!");
	}

	/**
	 * send HA status of local machine to CAPWAP
	 */
	private void distributeHAStatus() {
		HAMonitor haMonitor = HAUtil.getHAMonitor();
		HAStatus haStatus = haMonitor.getCurrentStatus();

		if (haStatus == null) {
			return;
		}

		log.warn("distributeHAStatus", "Starting CAPWAP with HA status: " + haStatus.getStatus());
		BeCapwapHAStatusEvent statusEvent = new BeCapwapHAStatusEvent();
		statusEvent.setHaStatus(haStatus.getStatus());

		try {
			statusEvent.buildPacket();
			HmBeCommunicationUtil.sendRequest(statusEvent);
			DebugUtil.commonDebugInfo("Sent HA status (" + haStatus.getStatus()
					+ ") to CAPWAP server");
		} catch (Exception e) {
			DebugUtil.commonDebugInfo("Failed to send HA status (" + haStatus.getStatus()
					+ ") to CAPWAP server");
		}
	}

	public BeCommunicationRequestManager getBeCommunicationRequestManager() {
		return requestManager;
	}

}