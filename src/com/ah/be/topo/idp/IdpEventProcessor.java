package com.ah.be.topo.idp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.admin.util.EmailElement;
import com.ah.be.admin.util.SendMailThread;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAutoMitigationResultEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCapwapIDPStatisticsEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.communication.event.BeMitigationArbitratorResultEvent;
import com.ah.be.communication.util.classifyap.ClassifyApAutoUtil;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.hiveap.IdpAp;
import com.ah.bo.hiveap.IdpSettings;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IdsPolicy;
import com.ah.util.Tracer;

public class IdpEventProcessor implements QueryBo {

	private static final Tracer log = new Tracer(IdpEventProcessor.class
			.getSimpleName());

	private final Hashtable<String, IdpStatisticHiveAp> statisticList;

	private final Hashtable<Long, IdpSettings> cachedIdpSettings;

	public IdpEventProcessor() {
		statisticList = new Hashtable<String, IdpStatisticHiveAp>();
		cachedIdpSettings = new Hashtable<Long, IdpSettings>();
	}

	public Hashtable<String, IdpStatisticHiveAp> getStatisticList() {
		return statisticList;
	}

	/**
	 * add a new HiveAp that need to do IDP statistic.
	 *
	 * @param statisticHiveAp -
	 */
	public synchronized void add(IdpStatisticHiveAp statisticHiveAp) {
		String nodeId = statisticHiveAp.getNodeId();
		statisticList.put(nodeId, statisticHiveAp);
		log.debug("add", "New IDP statistic HiveAp:" + nodeId
				+ " added in. Current idp HiveAp list size:"
				+ statisticList.size());
	}

	public synchronized void remove(String nodeId) {
		if (null != nodeId) {
			statisticList.remove(nodeId);
		}
	}

	public synchronized void initIdpSettings() {
		List<IdpSettings> list = QueryUtil.executeQuery(IdpSettings.class, null, null,null, this);
		for (IdpSettings setting : list) {
			cachedIdpSettings.put(setting.getOwner().getId(), setting);
		}
	}

	public void updateIdpSetting(Long domainId, IdpSettings setting) {
		cachedIdpSettings.put(domainId, setting);
	}

	public IdpSettings getIdpSetting(Long domainId) {
		return cachedIdpSettings.get(domainId);
	}

	public void dealIdpEvent(BeCapwapIDPStatisticsEvent event) {
		long start = System.currentTimeMillis();
		String nodeId = event.getApMac();
		byte msgType = event.getIdpMsgType();
		int sequenceNum = event.getSequenceNum();
		try {
			IdpStatisticHiveAp ap = statisticList.get(nodeId);
			if (null == ap) {
				log.error("dealIdpEvent", "HiveAP:" + nodeId
						+ " is not in the statistic list. Ignore this event.");
				return;
			}
			SimpleHiveAp hiveAp = CacheMgmt.getInstance().getSimpleHiveAp(
					ap.getNodeId());
			if (null == hiveAp) {
				log
						.error(
								"dealIdpEvent",
								"HiveAP:"
										+ nodeId
										+ " is not in the SimpleHiveAp Cache list. Ignore this event.");
				return;
			}
			if (msgType == BeCommunicationConstant.IDP_MSG_QUERY) {
				int reqSequenceNum = ap.getSequenceNum();
				if (sequenceNum == reqSequenceNum) {
					if (!ap.isOpened()) {
						// convert the switch to opened first.
						ap.setOpened(true);
						dealQueriedIdpEvent(hiveAp, event);
						if (null == event.getAp()) {
							log
									.error(
											"dealIdpEvent",
											"Try to send mitigation query, but not found HiveAP object in the IDP query result event..");
						} else {
							BeTopoModuleUtil.sendMitigationQuery(ap, event
									.getAp());
						}
					}
				} else {
					log.info("dealIdpEvent", "This HiveAP:" + nodeId
							+ " IDP event sequenceNum[" + sequenceNum
							+ "] is not match its request sequenceNum["
							+ reqSequenceNum + "]. Ignore this event.");
					return;
				}
			} else if (msgType == BeCommunicationConstant.IDP_MSG_REPORT) {
				if (ap.isOpened()) {
					dealReportedIdpEvent(hiveAp, event);
				} else {
					log.info("dealIdpEvent",
							"Reported IDP event from a HiveAP:" + nodeId
									+ " haven't opened switch. Ignore it.");
					return;
				}
			} else {
				log.error("dealIdpEvent", "Unknown IDP message type ["
						+ msgType + "] from HiveAP:" + nodeId + ". Ignore it.");
				return;
			}
		} catch (Exception e) {
			log.error("dealIdpEvent", "Deal IDP event from HiveAP:" + nodeId
					+ " error.", e);
		}
		long end = System.currentTimeMillis();
		log.debug("dealIdpEvent", "Deal IDP cost:" + (end - start) + "ms.");
	}

	private void dealQueriedIdpEvent(SimpleHiveAp s_ap,
			BeCapwapIDPStatisticsEvent event) {
		// query for mitigated RogueAPs
		/*-
		String where = "reportNodeId = :s1 and mitigated = :s2";
		Object[] values = new Object[] { s_ap.getMacAddress(), true };
		List<?> mitigated = QueryUtil.executeQuery("select ifMacAddress from "
				+ Idp.class.getSimpleName() + " bo", null, new FilterParams(
				where, values), s_ap.getDomainId());*/
		// delete previous IDP information report from this HiveAP
		BoMgmt.getIdpMgmt()
				.removeIdps(s_ap.getMacAddress(), s_ap.getDomainId());
		// add new IDP information report from this HiveAP
		List<Idp> idpList = event.getIdpDtoList();
		dealIdpList(s_ap, idpList);
	}

	private void dealReportedIdpEvent(SimpleHiveAp s_ap,
			BeCapwapIDPStatisticsEvent event) {
		// add the reported IDP information.
		List<Idp> idpList = event.getIdpDtoList();
		dealIdpList(s_ap, idpList);
	}

	private void dealIdpList(SimpleHiveAp s_ap, List<Idp> idpList) {
		if (null == idpList) {
			return;
		}
		Long domainId = s_ap.getDomainId();
		debugIdpInfo(s_ap, idpList);
		HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(domainId);
		if (null == owner) {
			log.error("dealIdpList",
					"Cannot find domain object in cache by domain id:"
							+ s_ap.getDomainId() + " from SimpleHiveAp:"
							+ s_ap.getMacAddress());
			return;
		}

		List<SimpleHiveAp> managedHiveAPs = CacheMgmt.getInstance()
				.getManagedApList(domainId);
		Map<String, Idp> idpMap = new HashMap<String, Idp>(idpList.size());
		List<String> removedIdp = new ArrayList<String>();
		Set<Idp> inNetIdp = new HashSet<Idp>();
		for (Idp idp : idpList) {
			if (null == idp) {
				continue;
			}

			String bssid = idp.getIfMacAddress();
			if (idp.getRemovedFlag() == BeCommunicationConstant.IDP_FLAG_REMOVED) {
				removedIdp.add(bssid);
			} else {
				short type = idp.getIdpType();
				short stationType = idp.getStationType();
				if (type != BeCommunicationConstant.IDP_TYPE_ROGUE
						&& type != BeCommunicationConstant.IDP_TYPE_VALID
						&& type != BeCommunicationConstant.IDP_TYPE_EXTERNAL) {
					log.error("dealIdpList", "HiveAP:" + s_ap.getMacAddress()
							+ " report a IDP with unknown IDP type [" + type
							+ "].");
					continue;
				}
				if (stationType != BeCommunicationConstant.IDP_STATION_TYPE_AP
						&& stationType != BeCommunicationConstant.IDP_STATION_TYPE_CLIENT) {
					log.error("dealIdpList", "HiveAP:" + s_ap.getMacAddress()
							+ " report a IDP with unknown station type ["
							+ stationType + "].");
					continue;
				}

				idp.setReportNodeId(s_ap.getMacAddress());
				idp.setMapId(s_ap.getMapContainerId());
				idp.setSimulated(s_ap.isSimulated());
				idp.setOwner(owner);
				idp.setManaged(isManagedHiveAPBssid(bssid, managedHiveAPs));
				IdpSettings setting = getIdpSetting(owner.getId());
				if (null != setting) {
					if (setting.getEnclosedFriendlyAps().contains(
							idp.getIfMacAddress())) {
						// set as friendly ap;
						idp.setIdpType(BeCommunicationConstant.IDP_TYPE_VALID);
						log.debug("dealIdpList", "BSSID: "
								+ idp.getIfMacAddress()
								+ " in the enclosed friendly ap list.");
					} else if (setting.getEnclosedRogueAps().contains(
							idp.getIfMacAddress())) {
						// set as rogue ap;
						idp.setIdpType(BeCommunicationConstant.IDP_TYPE_ROGUE);
						log.debug("dealIdpList", "BSSID: "
								+ idp.getIfMacAddress()
								+ " in the enclosed rogue ap list.");
					}
				}

				idpMap.put(bssid, idp);

				if (idp.getInNetworkFlag() == BeCommunicationConstant.IDP_CONNECTION_IN_NET
						&& stationType == BeCommunicationConstant.IDP_STATION_TYPE_AP
						&& idp.getIdpType() == BeCommunicationConstant.IDP_TYPE_ROGUE) {
					inNetIdp.add(idp);
				}
			}
		}

		try {
			if (removedIdp.size() > 0) {
				BoMgmt.getIdpMgmt().removeIdps(removedIdp,
						s_ap.getMacAddress(), owner.getId());
				BoMgmt.getIdpMgmt().removeMitigationIdps(removedIdp,
						s_ap.getMacAddress(), owner.getId());
			}
			if (idpMap.size() > 0) {
				BoMgmt.getIdpMgmt().addIdps(idpMap, s_ap.getMacAddress(),
						owner.getId());
			}
			if (inNetIdp.size() > 0) {
				SendMailThread mailProcessor = AhAppContainer.getBeAdminModule().getSendMailThread();
				MailNotification config = mailProcessor
						.getMailNotification(owner.getDomainName());
				if (null != config && config.getSendMailFlag()
						&& config.isInNetIdp()) {
					for (Idp idp : inNetIdp) {
						EmailElement mail = new EmailElement();
						mail.setDomainName(owner.getDomainName());
						mail
								.setSubject(NmsUtil
										.getUserMessage("info.innet.rogue.email.title"));
						mail.setMailContent(NmsUtil.getUserMessage(
								"info.inent.rogue.email.content", new String[] {
										idp.getIfMacAddress(),
										s_ap.getHostname(),
										s_ap.getMacAddress() }));
						mail.setToEmail(config.getMailTo());
						mailProcessor.sendEmail(mail);
						log.debug("", "email send for In-net IDP:"
								+ idp.getIfMacAddress()
								+ ", reported by HiveAP:"
								+ idp.getReportNodeId());
					}
				}
			}
		} catch (Exception e) {
			log.error("dealIdpList", "Deal IDP error.", e);
		}
	}

	private void debugIdpInfo(SimpleHiveAp s_ap, List<Idp> idpList) {
		try {
			log.debug("debugIdpInfo", "IDP Information from HiveAP:"
					+ s_ap.getMacAddress() + ", size:" + idpList.size());
			for (Idp idp : idpList) {
				StringBuilder buffer = new StringBuilder("Detail information:\n");
				buffer.append("IDP type:\t").append(idp.getIdpType()).append("\n");
				buffer.append("BSSID:\t").append(idp.getIfMacAddress()).append("\n");
				buffer.append("Remove flag:\t").append(idp.getRemovedFlag()).append("\n");
				buffer.append("Station type:\t").append(idp.getStationType()).append("\n");
				buffer.append("SSID:\t").append(idp.getSsid()).append("\n");
				buffer.append("innet flag:\t").append(idp.getInNetworkFlag()).append("\n");
				buffer.append("RSSI:\t").append(idp.getRssi()).append("\n");
				log.debug("debugIdpInfo", buffer.toString());
			}
		} catch (Exception e) {
			log.error("debugIdpInfo", "debug idp error.", e);
		}
	}

	public static boolean isManagedHiveAPBssid(String bssid,
			List<SimpleHiveAp> managedHiveAPs) {
		if (null == bssid || "".equals(bssid) || null == managedHiveAPs) {
			return false;
		}
		if (!NmsUtil.isAhMacOui(bssid)) {
			return false;
		}
		int length = bssid.length();
		String s = bssid.substring(0, length - 2);
		String e = bssid.substring(length - 2);
		int i = Integer.parseInt(e, 16) & 0x00C0; // for 11n ap only
		String n = Integer.toHexString(i);
		String nodeId_11n = s + (n.length() == 1 ? ("0" + n) : n).toUpperCase();
		String nodeId_eap = bssid.substring(0, length - 1) + "0";
		for (SimpleHiveAp ap : managedHiveAPs) {
			if (ap.getMacAddress().equals(nodeId_11n)) {
				return true;
			}
			if (ap.getMacAddress().equals(nodeId_eap)) {
				return true;
			}
		}
		return false;
	}

	public void dealMitigationEvent(IdpMitigationEvent event) {
		try {
			String bssid = event.getIdpBssid();
			String nodeId = event.getReportNodeId();
			String wifix = event.getWifix();
			String mitiMac = event.getMitiMac();
			boolean exec = event.isExec();
			Idp idpData = event.getIdp();

			SimpleHiveAp hiveAp = CacheMgmt.getInstance().getSimpleHiveAp(
					mitiMac);
			if (null == hiveAp) {
				log.error("dealMitigationEvent",
						"Cannot exec mitigation for HiveAP:" + mitiMac
								+ ", since it cannot be found in cache.");
				return;
			}

			String softVer = hiveAp.getSoftVer();
			boolean verFlag = false;// 4.0.1.0 version above
			// The function of mitigation is not supported for
			// HiveAP with the software version lower than 3.2.0.0.
			if (NmsUtil.compareSoftwareVersion("3.2.0.0", softVer) > 0) {
				log.info("dealMitigationEvent",
						"The reported HiveAP is not support mitigation feature. version:"
								+ softVer);
				return;
			}
			if (NmsUtil.compareSoftwareVersion("4.0.1.0", softVer) <= 0) verFlag = true;
			if (idpData.getMode() == IdsPolicy.MITIGATION_MODE_SEMIAUTO && verFlag) wifix = null;

			String exeCli = exec ? AhCliFactory
					.getExecMitigateCli(wifix, bssid, verFlag) : AhCliFactory
					.getCancelMitigateCli(wifix, bssid, verFlag);
			log.info("dealMitigationEvent", "mitigation execute cli:" + exeCli);

			BeCliEvent c_event = new BeCliEvent();
			c_event.setSimpleHiveAp(hiveAp);
			c_event.setClis(new String[] { exeCli });
			c_event.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			c_event.buildPacket();
			BeCommunicationEvent response = HmBeCommunicationUtil
					.sendSyncRequest(c_event, 35);
			if (response.getMsgType() == BeCommunicationConstant.MESSAGETYPE_CLIRSP) {
				log.info("dealMitigationEvent",
						"Receive response, mitigation execute failed for HiveAP:"
								+ mitiMac);
			} else if (response.getMsgType() == BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT) {
				response.parsePacket();
				BeCapwapCliResultEvent cliResult = (BeCapwapCliResultEvent) response;
				if (cliResult.getCliResult() != BeCommunicationConstant.CLIRESULT_SUCCESS) {
					log.info("dealMitigationEvent",
							"Receive unsuccess result event, mitigation execute failed for HiveAP:"
									+ mitiMac);
				} else {
					String where = "ifMacAddress = :s1 and reportNodeId = :s2";
					Object[] values = new Object[] { bssid, nodeId };
					List<Idp> list = QueryUtil.executeQuery(Idp.class, null,
							new FilterParams(where, values),null,this);
					if (!list.isEmpty()) {
						Idp idp = list.get(0);

						if (exec) {
							//idp.setMitigated(true);
							IdpAp ap = new IdpAp();
							ap.setIfName(wifix);
							ap.setMitiMac(mitiMac);
							idp.getMitiAps().add(ap);
							BoMgmt.getIdpMgmt().updateIdp(idp);
						} else {
							//idp.setMitigated(false);
							for (IdpAp ap : idp.getMitiAps()) {
								if (ap.getMitiMac().equals(mitiMac)) {
									idp.getMitiAps().remove(ap);
									break;
								}
							}
							BoMgmt.getIdpMgmt().updateIdp(idp);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("dealMitigationEvent", "error.", e);
		}
	}

	public void dealDAQueryEvent(BeMitigationArbitratorResultEvent event) {
		// catch the event and then send data to DA and report AP of rogue APs
		// no response on this event to DA, but send others events to it
		try {
			String daMac = event.getApMac();
			SimpleHiveAp hiveAp = CacheMgmt.getInstance().getSimpleHiveAp(
					daMac);
			if (null == hiveAp) {
				log.error("dealDAQueryEvent",
						"Cannot deal the event for DA:" + daMac
								+ ", since it cannot be found in cache.");
				return;
			}
			ClassifyApAutoUtil.dealDAMitigationQueryEvent(hiveAp);
		} catch (Exception e) {
			log.error("dealDAQueryEvent", "error.", e);
		}
	}

	public void dealAPClassificationResponseEvent(BeAutoMitigationResultEvent event) {
		// only log the response result
		try {
			String apMac = event.getApMac();
			SimpleHiveAp hiveAp = CacheMgmt.getInstance().getSimpleHiveAp(
					apMac);
			if (null == hiveAp) {
				log.error("dealAPClassificationResponseEvent",
						"Cannot deal the response event for AP:" + apMac
								+ ", since it cannot be found in cache.");
				return;
			}
			if (event.isSucc()) {
				log.info("Response of AP classification from:" + apMac + " is success.");
			} else {
				log.info("Response of AP classification from:" + apMac + " is failed.");
			}
		} catch (Exception e) {
			log.error("dealAPClassificationResponseEvent", "error.", e);
		}
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if(bo == null){
			return null;
		}

		if(bo instanceof IdpSettings){
			IdpSettings idp = (IdpSettings)bo;
			if(idp.getEnclosedRogueAps() != null)
				idp.getEnclosedRogueAps().size();
			if(idp.getEnclosedFriendlyAps() != null)
				idp.getEnclosedFriendlyAps().size();
		}

		if(bo instanceof Idp){
			Idp idp = (Idp)bo;
			if(idp.getMitiAps() != null)idp.getMitiAps().size();
		}
		return null;
	}

}