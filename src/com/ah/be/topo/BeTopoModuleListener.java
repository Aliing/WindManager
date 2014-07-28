package com.ah.be.topo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.ah.be.admin.hhmoperate.APSwitchCenter;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeConfigUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeCPUMemInfoEvent;
import com.ah.be.communication.event.BeCapwapAPTypeChangeEvent;
import com.ah.be.communication.event.BeConfigVersionEvent;
import com.ah.be.communication.event.BeHiveNameChangeEvent;
import com.ah.be.communication.event.BeHostIdentificationKeyEvent;
import com.ah.be.communication.event.BeHostIdentificationResultEvent;
import com.ah.be.communication.event.BeOTPStatusEvent;
import com.ah.be.communication.event.BeRebootFailEvent;
import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.be.config.hiveap.UpdateHiveAp;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.config.hiveap.provision.ProvisionProcessor;
import com.ah.be.db.discovery.AhDiscoveryProcessor;
import com.ah.be.db.discovery.event.AhDiscoveryEvent;
import com.ah.be.event.AhShutdownEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.fault.HiveAPKernelDumpTrap;
import com.ah.be.sa3party.SaProcess;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.hiveap.HiveApUpdateResult;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.OneTimePassword;
import com.ah.ui.actions.hiveap.HiveApUpdateResultAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class BeTopoModuleListener implements QueryBo {

	private static final Tracer log = new Tracer(BeTopoModuleListener.class
			.getSimpleName());

	private final BlockingQueue<BeBaseEvent> eventQueue;

	private final AtomicInteger lostEventCount;

	private final Map<String, AhDiscoveryEvent> cachedEvents;

	private Thread eventMgr;

	public BeTopoModuleListener() {
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(30000);
		lostEventCount = new AtomicInteger(0);
		cachedEvents = Collections.synchronizedMap(new HashMap<String, AhDiscoveryEvent>());
	}

	public synchronized void addEvent(BeBaseEvent event) {
		// filter events which is interested
		if(event == null) {
			return ;
		}
		int type = event.getEventType();
		if (type != BeEventConst.HIVE_AP_MANAGE_STATUS_CHANGED
				&& type != BeEventConst.AH_DISCOVERY_EVENT
				&& type != BeEventConst.COMMUNICATIONEVENTTYPE
				&& type != BeEventConst.AH_SHUTDOWN_EVENT) {
			return;
		}
		if (type == BeEventConst.COMMUNICATIONEVENTTYPE) {
			BeCommunicationEvent commEvent = (BeCommunicationEvent) event;
			int msgType = commEvent.getMsgType();
			if (msgType != BeCommunicationConstant.MESSAGETYPE_CAPWAPCONNECT
					&& msgType != BeCommunicationConstant.MESSAGEELEMENTTYPE_REBOOTFAILEVENT
					&& msgType != BeCommunicationConstant.MESSAGETYPE_WTPCONTROLRSP
					&& msgType != BeCommunicationConstant.MESSAGETYPE_HOSTIDENTIFICATIONKEYRSP
					&& msgType != BeCommunicationConstant.MESSAGEELEMENTTYPE_HOSTIDENTIFICATIONKEYRESULT
					&& msgType != BeCommunicationConstant.MESSAGEELEMENTTYPE_CONFIGVERSIONEVENT
					&& msgType != BeCommunicationConstant.MESSAGEELEMENTTYPE_APTYPECHANGE
					//&& msgType != BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT
					&& msgType != BeCommunicationConstant.MESSAGEELEMENTTYPE_HIVENAMECHANGE) {
				return;
			}
			// Process the LLDP/CDP event in a new signal thread BeLLDPCDPEventProcessor.
			/*if(msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT){
				BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent) commEvent;
				short resultType = resultEvent.getResultType();
				if(resultType != BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LLDPCDPINFO){
					return;
				}
			}*/
		}
		// filter end
		if (eventQueue.offer(event)) {

			if(lostEventCount.intValue() > 0) {
				log.error("addEvent", "Topo Module Event queue is full, "
						+ lostEventCount.intValue() + " events lost.");
				lostEventCount.set(0);
			}
		}
		else {
			lostEventCount.incrementAndGet();
			// New event is more important, so remove the head of queue in order
			// to add new event into the FIFO queue.
			eventQueue.poll();

//			BeBaseEvent lostEvent = eventQueue.poll();
//			if (lostEvent != null) {
//				log.warning("addEvent",
//						"Topo Module Event queue Discarding Event. Type:["
//								+ lostEvent.getEventType() + "]");
//			}

			if (!eventQueue.offer(event)) {
				log
						.error("addEvent",
								"Topo Module Event queue is full even after removing the head of queue.");
			}
		}
	}

	public void start() {
		if (isStart()) {
			return;
		}
		eventMgr = new Thread() {
			@Override
			public void run() {
				log
						.info("start",
								"<BE Thread> Topo Module Event Notification thread started.");

				while (true) {
					try {
						// take() method blocks
						BeBaseEvent event = eventQueue.take();

						if (event.getEventType() == BeEventConst.AH_SHUTDOWN_EVENT) {
							log
									.info(
											"stop",
											"<BE Thread> Application is shutdown, close Topo Module event thread, events lost: "
													+ lostEventCount.intValue());
							break;
						} else {
							processEvent(event);
						}
					} catch (Exception e) {
						log.error("start",
								"BeTopoModuleListener run exception", e);
					} catch (Error e) {
						log.error("start", "BeTopoModuleListener run error", e);
					}
				}
			}
		};
		eventMgr.setName("BeTopoModuleListener");
		eventMgr.start();
	}

	public boolean isStart() {
		return eventMgr != null && eventMgr.isAlive();
	}

	public final void stop() {
		eventQueue.clear();
		BeBaseEvent stopEvent = new AhShutdownEvent();
		addEvent(stopEvent);
	}

	private void processEvent(BeBaseEvent event) {
		try {
			if (null != event) {
				int type = event.getEventType();

				switch (type) {
				case BeEventConst.HIVE_AP_MANAGE_STATUS_CHANGED:
					HiveApManageStatusChangedEvent a_event = (HiveApManageStatusChangedEvent) event;
					processManageStatusChangedEvent(a_event);
					break;
				case BeEventConst.AH_DISCOVERY_EVENT:
					AhDiscoveryEvent d_event = (AhDiscoveryEvent) event;
					processDiscoveryEvent(d_event);
					break;
				case BeEventConst.COMMUNICATIONEVENTTYPE:
					BeCommunicationEvent c_event = (BeCommunicationEvent) event;
					processCommunicationEvent(c_event);
					break;
				}
			}
		} catch (Exception e) {
			log.error("processEvent", "process event error.", e);
		}
	}

	private void processManageStatusChangedEvent(
			HiveApManageStatusChangedEvent event) {
		HiveAp hiveAp = event.getHiveAp();
		short oldManageStatus = event.getPreviousManageStatus();
		short manageStatus = hiveAp.getManageStatus();
		String mac = hiveAp.getMacAddress();

		if (manageStatus == HiveAp.STATUS_MANAGED
				&& oldManageStatus != HiveAp.STATUS_MANAGED) {

			if (hiveAp.isConnected()) {
				log.debug("processManageStatusChangedEvent", "Connected HiveAP:"
						+ mac
						+ " come to managed list. To fetch data from HiveAP.");
				// indicate this HiveAP need to do upload user group CLIs
				// process when HM receive the WTP response. Note: it's just a
				// Transient value, for sendMemoryConfig used.
				hiveAp.setReConnectedByReboot(true);
				// send requests to this HiveAP for querying data;
				BeTopoModuleUtil.sendRequestsForManagedHiveAP(hiveAp);
			} else {
				log.debug("processManageStatusChangedEvent", "Disconnected AP:"
						+ mac + " move to managed list. Do nothing.");
			}
		} else if (manageStatus != HiveAp.STATUS_MANAGED
				&& oldManageStatus == HiveAp.STATUS_MANAGED) {

			if (hiveAp.isConnected()) {
				log.debug("processManageStatusChangedEvent", "Connected AP:"
						+ mac + " move to unmanaged list.");
				BeTopoModuleUtil.clearHiveAPRelatedData(hiveAp);
			} else {
				log.debug("processManageStatusChangedEvent", "Disconnected AP:"
						+ mac + " move to unmanaged list. Do nothing.");
			}
		}
	}

	private void processDiscoveryEvent(AhDiscoveryEvent event) {
		HiveAp hiveAp = event.getHiveAp();
		//add sa 3 deal with connection status
		SaProcess saProcess = AhAppContainer.getBeAdminModule().getSaProcess();
		saProcess.sendApConnStaus(hiveAp);
		//end
		String mac = hiveAp.getMacAddress();
		String domainName = hiveAp.getOwner().getDomainName();
		short manageStatus = hiveAp.getManageStatus();
		BeAPConnectEvent connectEvent = event.getCapwapConnectEvent();
		int type = -1, coreDump = -1;
		String msg = "";
		if (null != connectEvent) {
			type = connectEvent.getReconnectReason();
			coreDump = connectEvent.getCoreDumpFlag();
			msg = connectEvent.getReconnectDescription();
		} else {
			log.error("processDiscoveryEvent",
					"cannot find BeAPConnectEvent in AhDiscoveryEvent!!");
		}
		/* Check whether should to switch HiveAP */
		APSwitchCenter deviceSwitchCenter = AhAppContainer.getBeAdminModule().getDeviceSwitchCenter();
		String switchInfo = deviceSwitchCenter.getSwitchInfo(domainName);
		if ((null != switchInfo && !"".equals(switchInfo))
				&& hiveAp.isConnected()) {
			log.debug("processDiscoveryEvent", "HiveAP:" + mac
					+ " will be transfer to New CAPWAP IP:" + switchInfo);
			// it's in switch status, try to transfer HiveAP
			BeTopoModuleUtil.transferHiveAP(hiveAp, switchInfo);
			return;
		}
		// regular process

		/* Check if it is needed to update its mapContainer; */
		if (event.isSeparatingMapLocation()) {
			log.debug("processDiscoveryEvent",
					"Update mapContainer, combined location:"
							+ hiveAp.getLocation() + ". Discovery type:"
							+ event.getType() + ". HiveAP:" + mac);
			try {
				BoMgmt.getMapMgmt().assignMapLeafNodeToDiscoveredHiveAp(hiveAp);
			} catch (Exception e) {
				log.error("processDiscoveryEvent", "HiveAP:" + mac
						+ " update the map leaf node failed.", e);
			}
		}

		/* Configuration indication */
		if (event.getType() == AhDiscoveryEvent.HiveApType.UPDATED) {
			// configuration version mismatch
//			if (hiveAp.getPreviousConfigVer() != hiveAp.getConfigVer()) {
//				boolean pending = true;
//				int pendingIndex = ConfigurationResources.CONFIG_VERSION_MISMATCH;
//				String desc = null;
//				Set<Long> ids = new HashSet<Long>(1);
//				ids.add(hiveAp.getId());
//				try {
//					BoMgmt.getHiveApMgmt().updateConfigurationIndication(ids,
//							pending, pendingIndex, desc,
//							ConfigurationType.Configuration);
//				} catch (Exception e) {
//					log.error("processDiscoveryEvent", "HiveAP:" + mac
//							+ " update configuration indication"
//							+ "in Config version case failed.", e);
//				}
//			}
			// effective with reboot
//			if (type == BeAPConnectEvent.CLIENT_HIVE_AP_REBOOTED) {
//				try {
//					BoMgmt.getHiveApMgmt()
//							.updateConfigurationIndicationForReboot(hiveAp);
//				} catch (Exception e) {
//					log.error("processDiscoveryEvent", "HiveAP:" + mac
//							+ " update configuration indication"
//							+ "in effective with reboot case failed.", e);
//				}
//			}
		}

		if (manageStatus == HiveAp.STATUS_MANAGED) {
			if (hiveAp.isConnected()) {
				log.info("processDiscoveryEvent", "Managed HiveAP:" + mac
						+ " connected, reconnect reason type:" + msg + ", ["
						+ type + "]. Core dump flag:" + coreDump + ".");
				// See if the reconnect is reboot case;
				if (event.getType() == AhDiscoveryEvent.HiveApType.UPDATED
						&& type == BeAPConnectEvent.CLIENT_HIVE_AP_REBOOTED) {
					// indicate this HiveAP need to do upload user
					// group CLIs process when HM receive the WTP
					// response. Note: it's just a Transient value,
					// for response used.
					hiveAp.setReConnectedByReboot(true);
				}
				// add into cached event, only cached managed and connected
				cachedEvents.put(hiveAp.getMacAddress(), event);
				log.debug("processDiscoveryEvent",
						"Cached Discovery event count:" + cachedEvents.size());
			} else {
				log.info("processDiscoveryEvent", "Managed HiveAP:" + mac
						+ " disconnected.");
				BeTopoModuleUtil.clearHiveAPRelatedData(hiveAp);
			}
		}
		/* Send WTP Event (for both new/managed HiveAPs) */
		if (hiveAp.isConnected()) {
			BeTopoModuleUtil.sendWTPControlRequest(hiveAp);
		}
	}

	private void processCommunicationEvent(BeCommunicationEvent event) throws Exception {
		int msgType = event.getMsgType();
		if (BeCommunicationConstant.MESSAGETYPE_CAPWAPCONNECT == msgType) {
			log.debug("processCommunicationEvent",
					"Capwap Server connected response received.");
			BeTopoModuleUtil.sendCapwapServerParamConfig();
			BeTopoModuleUtil.sendInitHiveApDTLSParamConfig();
			BeTopoModuleUtil.sendInitHiveApSimulateConfig();
		} else if (BeCommunicationConstant.MESSAGEELEMENTTYPE_REBOOTFAILEVENT == msgType) {
			BeTopoModuleUtil
					.dealHiveAPRebootFailedEvent((BeRebootFailEvent) event);
		} else if (BeCommunicationConstant.MESSAGETYPE_WTPCONTROLRSP == msgType) {
			byte result = event.getResult();
			HiveAp hiveAp = event.getApNoQuery();
			if (null == hiveAp) {
				log.error("Cannot found hiveAp object which should be there set in WTPCONTROLREQ!");
			} else {
				String mac = hiveAp.getMacAddress();
				String transferCapwap = hiveAp.getNewTransferCapwap();
				log.debug("processCommunicationEvent",
						"WTP Control response of HiveAP:" + mac
								+ " received. result:" + result);
				if (result != BeCommunicationConstant.RESULTTYPE_SUCCESS) {
					log.error("processCommunicationEvent",
							"WTP Control request failed for HiveAP:" + mac);
					if (null != hiveAp.getAutoProvisioningConfig()) {
						// system log
						String message = "Auto provisioning for "
								+ NmsUtil.getOEMCustomer().getAccessPonitName()
								+ ": "
								+ mac
								+ " failed because of WTP control request failed.";
						AhAppContainer.HmBe.setSystemLog(
								HmSystemLog.LEVEL_MAJOR,
								HmSystemLog.FEATURE_HIVEAPS, message);
					}
					// add protection code in case of WTP request failed, let
					// device disconnect from HiveMananger, see bug 23205.
					BeTopoModuleUtil
							.sendBeDeleteAPConnectRequest(hiveAp, false);
				} else {
					if (null != transferCapwap && !"".equals(transferCapwap)) {
						// need to transfer the HiveAP to a new CAPWAP Server
						BeTopoModuleUtil.sendCliTransferCapwapIp(hiveAp,
								transferCapwap);
						log.info("processCommunicationEvent",
								"CLI send for HiveAP:" + mac
										+ " transfer to New CAPWAP IP:"
										+ transferCapwap);
					} else {
						// The function of SSH Public Authentication is not
						// supported for
						// HiveAP with the software version lower than 3.2.0.0.
						if (NmsUtil.compareSoftwareVersion("3.2.0.0",
								hiveAp.getSoftVer()) > 0) {
							/* send auto provisioning directly */
							BeTopoModuleUtil.processAutoProvisioning(hiveAp);
						} else {/*
								 * send auto provisioning after identification
								 * successfully
								 */
							// send Identification key to HiveAP
							BeTopoModuleUtil.sendIdentification(hiveAp,
									(short) 1);
						}
						/* Only for managed HiveAPs! */
						if (hiveAp.getManageStatus() == HiveAp.STATUS_MANAGED) {
							BeTopoModuleUtil
									.sendRequestsForManagedHiveAP(hiveAp);
						}

						// Auto upload config
						deviceAutoProvisionManage(hiveAp);

						// send request for get CPU and DA domain message from
						// device
						sendRequestGetDAMessage(hiveAp);
					}
				}
			}
		} else if (BeCommunicationConstant.MESSAGETYPE_HOSTIDENTIFICATIONKEYRSP == msgType) {
			BeHostIdentificationKeyEvent i_event = (BeHostIdentificationKeyEvent) event;
			byte result = i_event.getResult();
			HiveAp hiveAp = i_event.getApNoQuery();
			String mac = hiveAp.getMacAddress();
			short attemptCount = i_event.getAttemptCount();
			log.debug("processCommunicationEvent",
					"Identification response of HiveAP:" + mac
							+ " received. result:" + result
							+ ", current attempt count:" + attemptCount);
			if (result == BeCommunicationConstant.RESULTTYPE_SUCCESS) {
				// Cannot do it here, must do it after receive result!!
				// BeTopoModuleUtil.processAutoProvisioning(hiveAp);
			} else if (result == BeCommunicationConstant.RESULTTYPE_TIMEOUT
					|| result == BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT) {
				// retry
				if (attemptCount < 3) {
					BeTopoModuleUtil.sendIdentification(hiveAp, ++attemptCount);
				} else {
					String message = "HiveAP:" + mac
							+ " send Identification request attempts failed.";
					AhAppContainer.HmBe.setSystemLog(HmSystemLog.LEVEL_MAJOR,
							HmSystemLog.FEATURE_HIVEAPS, message);
					// disconnect HiveAP if send identification request failed.
					BeTopoModuleUtil
							.sendBeDeleteAPConnectRequest(hiveAp, false);
				}
			} else {
				if (null != hiveAp.getAutoProvisioningConfig()) {
					// system log
					String message = "Auto provisioning for "+NmsUtil.getOEMCustomer().getAccessPonitName()+": "
							+ mac
							+ " failed because of Identification request failed.";
					AhAppContainer.HmBe.setSystemLog(HmSystemLog.LEVEL_MAJOR,
							HmSystemLog.FEATURE_HIVEAPS, message);
				}
			}
		} else if (BeCommunicationConstant.MESSAGEELEMENTTYPE_HOSTIDENTIFICATIONKEYRESULT == msgType) {
			BeHostIdentificationResultEvent i_event = (BeHostIdentificationResultEvent) event;
			try {
				i_event.parsePacket();
				int result = i_event.getHostIdenti_result();
				String mac = i_event.getApMac();
				HiveAp hiveAp = i_event.getApNoQuery();
				log.debug("processCommunicationEvent",
						"Identification result of HiveAP:" + mac
								+ " received. result:" + result);
				if (result == BeHostIdentificationResultEvent.RESULT_SUCCESS) {
					// auto provisioning
					BeTopoModuleUtil.processAutoProvisioning(hiveAp);
					
					//send l7 signature file.
					HmBeConfigUtil.getUpdateManager().l7_signatureUpdate(hiveAp);

					AhDiscoveryEvent cachedEvent = cachedEvents.remove(i_event
							.getApMac());
					if (null != cachedEvent
							&& null != cachedEvent.getCapwapConnectEvent()
							&& cachedEvent.getCapwapConnectEvent()
									.getCoreDumpFlag() == BeAPConnectEvent.FLAG_EXISTSCOREDUMPFILE) {
						// core dump file existed.
						// 1)send request to load file from HiveAP to HM.
						BeTopoModuleUtil.sendKernelDumpFileRequest(i_event
								.getApNoQuery());
						// 2)generate alarm to alarm list.
						HiveAPKernelDumpTrap.generateKernelDumpTrap(i_event.getApNoQuery());
					}
				} else {
					log.error("Identification result failed for device: " + mac
							+ ", result: " + result
							+ ", try to disconnect the device.");
					// disconnect HiveAP if send identification request failed.
					BeTopoModuleUtil
							.sendBeDeleteAPConnectRequest(hiveAp, false);
				}
			} catch (Exception e) {
				log.error("processCommunicationEvent",
						"deal identification result error.", e);
			}
		} else if (BeCommunicationConstant.MESSAGEELEMENTTYPE_CONFIGVERSIONEVENT == msgType) {
			BeConfigVersionEvent v_event = (BeConfigVersionEvent) event;
			int configVer = v_event.getVersionNumber();
			String macAddress = v_event.getApMac();
			log.info("processCommunicationEvent", "Received a config version changed event. HiveAP: " + macAddress+ "; New Config Version: " + configVer);

			/* Configuration indication
			try {
				BoMgmt.getHiveApMgmt().updateConfigurationIndication(macAddress, configVer);
			} catch (Exception e) {
				log.error("processCommunicationEvent", "HiveAP:" + macAddress
						+ " update configuration indication"
						+ "in Config version case failed.", e);
			}*/
		} else if (BeCommunicationConstant.MESSAGEELEMENTTYPE_APTYPECHANGE == msgType) {
			BeCapwapAPTypeChangeEvent t_event = (BeCapwapAPTypeChangeEvent) event;
			String hiveApMac = t_event.getApMac();
			log.debug("processCommunicationEvent", "[" + hiveApMac
					+ "]Received a AP type change event.");
			short hiveApType = t_event.getApType() == BeCommunicationConstant.AP_TYPE_PORTAL ? HiveAp.HIVEAP_TYPE_PORTAL
					: HiveAp.HIVEAP_TYPE_MP;

			try {
				List<?> hiveApTypes = QueryUtil.executeQuery(
						"select hiveApType from "
								+ HiveAp.class.getSimpleName(), null,
						new FilterParams("macAddress", hiveApMac));

				if (!hiveApTypes.isEmpty()
						&& (Short) hiveApTypes.get(0) != hiveApType) {
					BoMgmt.getHiveApMgmt().updateHiveApType(hiveApMac,
							hiveApType);
				}

				log.info("processCommunicationEvent", "["
						+ hiveApMac
						+ "]The type of HiveAP was changed to "
						+ (hiveApType == HiveAp.HIVEAP_TYPE_PORTAL ? "'Portal'"
								: "'Mesh Point'"));
			} catch (Exception e) {
				String errorMsg = MgrUtil.getUserMessage("hm.system.log.be.tool.module.listener.change.ap.type.error")
						+ (hiveApType == HiveAp.HIVEAP_TYPE_PORTAL ? "'Portal'"
								: "'Mesh Point'");
				log.error("processCommunicationEvent", errorMsg, e);
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR,
						HmSystemLog.FEATURE_DISCOVERY, errorMsg);
			}
		}else if (BeCommunicationConstant.MESSAGEELEMENTTYPE_HIVENAMECHANGE == msgType) {
			try {
				BeHiveNameChangeEvent h_event = (BeHiveNameChangeEvent) event;
				h_event.parsePacket();
				String hiveApMac = h_event.getApMac();
				String runningHive = h_event.getHiveName();
				BoMgmt.getHiveApMgmt().updateHiveApRunningHive(hiveApMac,
						runningHive);
			} catch (Exception e) {
				log.error("processCommunicationEvent", "handle HiveAP Hive name change Event error", e);
			}
		}
	}

	private boolean retryHiveApUploadStaged(String macAddr){
		HiveAp hiveAp = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", macAddr);
		if (null == hiveAp) {
			return false;
		}
		HiveApUpdateResult resultObj = getStagedResults(hiveAp);
		if (null == resultObj) {
			return false;
		}
		//staged time is limited
		if(resultObj.getStagedTime() <= 0){
			return false;
		}
		UpdateHiveAp upAp = HiveApUpdateResultAction.getUpdateHiveAp(resultObj, hiveAp, true);
		if(null == upAp){
			log.error("Unable to retry staged update operation for HiveAP: " + macAddr);
			return false;
		}
		HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_DISCOVERY,
				MgrUtil.getUserMessage("hm.system.log.hive.ap.mgmt.device.macaddress.upload",macAddr));
		HmBeLogUtil.generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.device.macaddress.reconnect.to.hm",macAddr));
		
		List<UpdateHiveAp> upApList = new ArrayList<UpdateHiveAp>();
		upApList.add(upAp);
		List<String[]> errList = HmBeConfigUtil.getUpdateManager().addUpdateObjects(upApList);
		if (null != errList && errList.size() > 0) {
			for (String[] errInfo : errList) {
				log.error("stagedOperation", errInfo[1]);
			}
		} else {
			Set<Long> idList = new HashSet<Long>();
			idList.add(resultObj.getId());
			HmBeConfigUtil.getUpdateManager().updateRetryFlag(idList);
		}
		return true;
	}

	private HiveApUpdateResult getStagedResults(HiveAp hiveAp){
		List<HiveApUpdateResult> list = QueryUtil.executeQuery(HiveApUpdateResult.class, null,
				new FilterParams("nodeId=:s1 and result=:s2", new Object[]{hiveAp.getMacAddress(), UpdateParameters.UPDATE_STAGED}),
				hiveAp.getOwner().getId(), this);
		if(!list.isEmpty()){
			return list.get(0);
		}else{
			return null;
		}
	}

	private void deviceAutoProvisionManage(HiveAp hiveAp) throws Exception{

		boolean uploadResult = false;
		boolean needCheckOtp = false;

		//send OTP request for device reconnect.
		List<?> resList = QueryUtil.executeQuery("select upper(macAddress) from "+OneTimePassword.class.getSimpleName(), null,
				new FilterParams("upper(macAddress) = :s1 and hiveApAutoProvision != null and hiveApAutoProvision.autoProvision = :s2 and hiveApAutoProvision.enableOneTimePassword = :s3",
						new Object[]{hiveAp.getMacAddress().toUpperCase(), true, true}));
		if(resList != null && !resList.isEmpty()){
			needCheckOtp = true;
			for(Object resultObj : resList){
				String macAddr = String.valueOf(resultObj);
				BeTopoModuleUtil.sendOtpEventQuery(hiveAp, BeOTPStatusEvent.OTP_MODE_QUERY,null);
				log.info("Device "+macAddr+ " reconnect to HM send OTP request.");
			}
		}else{
			//if mapping not exists mapping auto provision
			List<HiveApAutoProvision> provisionList = QueryUtil.executeQuery(HiveApAutoProvision.class,
					null, new FilterParams("autoProvision", true), hiveAp.getOwner().getId(), new ConfigLazyQueryBo());
			HiveApAutoProvision provision = AhDiscoveryProcessor.getAutoProvisionByDevice(hiveAp, provisionList);
			if(provision != null && provision.isAutoProvision() && provision.isEnableOneTimePassword()){
				needCheckOtp = true;
				BeTopoModuleUtil.sendOtpEventQuery(hiveAp, BeOTPStatusEvent.OTP_MODE_QUERY,null);
				log.info("Device "+hiveAp.getHostName()+ " reconnect to HM send OTP request.");
			}
		}

		//staged hiveap upload
		uploadResult = retryHiveApUploadStaged(hiveAp.getMacAddress());

		//do Auto Provision when BR reset config
		if(!uploadResult && !needCheckOtp && hiveAp.getConfigVer() <= 1
				&& hiveAp.getManageStatus() == HiveAp.STATUS_MANAGED ){
			ProvisionProcessor.doAutoProvisionForReset(hiveAp);
		}
	}

	private void sendRequestGetDAMessage(HiveAp hiveAp){
		try{
			if(hiveAp.isSimulated() && NmsUtil.compareSoftwareVersion("5.1.3.0", hiveAp.getSoftVer()) > 0){
				return;
			}
			BeCPUMemInfoEvent event = new BeCPUMemInfoEvent();
			event.setAp(hiveAp);
			event.buildPacket();
			HmBeCommunicationUtil.sendRequest(event, 30);
		}catch(Exception ex){
			log.error("Device "+hiveAp.getHostName()+ " reconnect to HM send get CPU and DA message error.", ex);
		}
	}

	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HiveApAutoProvision) {
			HiveApAutoProvision autoProvision = (HiveApAutoProvision) bo;

			if (autoProvision.getMacAddresses() != null) {
				autoProvision.getMacAddresses().size();
			}
			if (autoProvision.getIpSubNetworks() != null)
				autoProvision.getIpSubNetworks().size();
			if (autoProvision.getDeviceInterfaces() != null)
				autoProvision.getDeviceInterfaces().size();
		}else if(bo instanceof HiveAp){
			HiveAp hiveAp = (HiveAp)bo;
			if(hiveAp.getDeviceInterfaces() != null)
				hiveAp.getDeviceInterfaces().values();
		}else if(bo instanceof HiveApUpdateResult){
			HiveApUpdateResult result = (HiveApUpdateResult)bo;
			if(result.getItems() != null)
				result.getItems().size();
		}

		return null;
	}

}