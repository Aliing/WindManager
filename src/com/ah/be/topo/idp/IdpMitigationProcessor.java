package com.ah.be.topo.idp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.admin.util.EmailElement;
import com.ah.be.admin.util.SendMailThread;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeTopoUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.event.BeIDPMitigationResultEvent;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.MailNotification;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.hiveap.IdpMitigation;
import com.ah.bo.hiveap.IdpSettings;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.util.Tracer;

public class IdpMitigationProcessor {
	
	private static final Tracer log = new Tracer(IdpMitigationProcessor.class
			.getSimpleName());

	public IdpMitigationProcessor() {
	}

	public void dealIdpMitigationEvent(BeIDPMitigationResultEvent event) {
		long start = System.currentTimeMillis();
		String nodeId = event.getApMac();
		int sequenceNum = event.getSequenceNum();
		try {
			IdpStatisticHiveAp ap = HmBeTopoUtil.getIdpEventListener().getIdpEventProcessor().getStatisticList().get(nodeId);
			if (null == ap) {
				log.error("dealIdpMitigationEvent", "HiveAP:" + nodeId
						+ " is not in the statistic list. Ignore this event.");
				return;
			}
			SimpleHiveAp hiveAp = CacheMgmt.getInstance().getSimpleHiveAp(
					ap.getNodeId());
			if (null == hiveAp) {
				log
						.error(
								"dealIdpMitigationEvent",
								"HiveAP:"
										+ nodeId
										+ " is not in the SimpleHiveAp Cache list. Ignore this event.");
				return;
			}
			if (sequenceNum == 0) {
				// report event;
				if (ap.isMitigationOpened()) {
					dealReportedIdpMitigationEvent(hiveAp, event);
				} else {
					log.info("dealIdpMitigationEvent",
							"Reported IDP mitigation event from a HiveAP:"
									+ nodeId
									+ " haven't opened switch. Ignore it.");
					return;
				}
			} else {
				// query event;
				int reqSequenceNum = ap.getSequenceNum();
				if (sequenceNum == reqSequenceNum) {
					if (!ap.isMitigationOpened()) {
						// convert the switch to opened first.
						ap.setMitigationOpened(true);
						dealQueriedIdpMitigationEvent(hiveAp, event);
					}
				} else {
					log.info("dealIdpMitigationEvent", "This HiveAP:" + nodeId
							+ " IDP mitigation event sequenceNum["
							+ sequenceNum
							+ "] is not match its request sequenceNum["
							+ reqSequenceNum + "]. Ignore this event.");
					return;
				}
			}
		} catch (Exception e) {
			log.error("dealIdpMitigationEvent",
					"Deal IDP mitigation event from HiveAP:" + nodeId
							+ " error.", e);
		}
		long end = System.currentTimeMillis();
		log.debug("dealIdpMitigationEvent", "Deal IDP mitigation cost:"
				+ (end - start) + "ms.");
	}

	private void dealQueriedIdpMitigationEvent(SimpleHiveAp s_ap,
			BeIDPMitigationResultEvent event) {
		// add new IDP information query from this HiveAP
		List<IdpMitigation> mitigationList = event.getIdpMitigationList();
		dealMitigationList(s_ap, mitigationList, true);
	}

	private void dealReportedIdpMitigationEvent(SimpleHiveAp s_ap,
			BeIDPMitigationResultEvent event) {
		// add the reported IDP information.
		List<IdpMitigation> mitigationList = event.getIdpMitigationList();
		dealMitigationList(s_ap, mitigationList, false);
	}

	private void dealMitigationList(SimpleHiveAp s_ap,
			List<IdpMitigation> mitigationList, boolean isQueried) {
		debugIdpMitigationInfo(s_ap, mitigationList);
		List<String> mitigated = new ArrayList<String>();
		List<String> parents = new ArrayList<String>();
		for (IdpMitigation mitigation : mitigationList) {
			String bssid = mitigation.getBssid();
			byte flag = mitigation.getFlag();
			List<Idp> idpList = mitigation.getClients();
			if (flag == IdpMitigation.ADD) {
				// indicate it's under mitigation
				dealIdpList(s_ap, idpList);
				if (isQueried) {
					// indicate it's mitigation
					mitigated.add(bssid);
				}
			} else if (flag == IdpMitigation.REPORTED) {
				// indicate it's not under mitigation
				dealIdpList(s_ap, idpList);
			} else if (flag == IdpMitigation.MITIGATE_START) {
				// indicate it's started mitigation
				dealStartIdpList(s_ap, bssid);
			} else {
				parents.add(bssid);
			}
		}
		if (!mitigated.isEmpty()) {
			try {
				BoMgmt.getIdpMgmt().updateMitigationFlag(mitigated,
						s_ap.getMacAddress(), true);
			} catch (Exception e) {
				log
						.error(
								"dealMitigationList",
								"error while deal with update mitigation flag to true.",
								e);
			}
		}
		if (!parents.isEmpty()) {
			try {
				BoMgmt.getIdpMgmt().removeMitigationIdps(parents,
						s_ap.getMacAddress(), s_ap.getDomainId());
				// update parent IDP
				BoMgmt.getIdpMgmt().updateMitigationFlag(parents,
						s_ap.getMacAddress(), false);
			} catch (Exception e) {
				log
						.error(
								"dealMitigationList",
								"error while deal with remove all mitigation event.",
								e);
			}
		}
	}

	private void dealStartIdpList(SimpleHiveAp s_ap, String bssid) {
		List<String> bssids = new ArrayList<String>();
		bssids.add(bssid);
		try {
			BoMgmt.getIdpMgmt().updateMitigationFlag(bssids, s_ap.getMacAddress(), true);
		} catch (Exception e) {
			log.error(
					"dealStartIdpList",
					"error while deal with mitigate start mitigation event.",
					e);
		}
	}

	private void dealIdpList(SimpleHiveAp s_ap, List<Idp> idpList) {
		if (null == idpList) {
			return;
		}
		Long domainId = s_ap.getDomainId();
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
				idp.setManaged(IdpEventProcessor.isManagedHiveAPBssid(bssid,
						managedHiveAPs));
				IdpSettings setting = HmBeTopoUtil.getIdpEventListener().getIdpEventProcessor()
						.getIdpSetting(owner.getId());
				if (null != setting) {
					if (setting.getEnclosedFriendlyAps().contains(
							idp.getIfMacAddress())) {
						// set as friendly ap;
						idp.setIdpType(BeCommunicationConstant.IDP_TYPE_VALID);
						log.debug("dealIdpList", "Mitigation BSSID: "
								+ idp.getIfMacAddress()
								+ " in the enclosed friendly ap list.");
					} else if (setting.getEnclosedRogueAps().contains(
							idp.getIfMacAddress())) {
						// set as rogue ap;
						idp.setIdpType(BeCommunicationConstant.IDP_TYPE_ROGUE);
						log.debug("dealIdpList", "Mitigation BSSID: "
								+ idp.getIfMacAddress()
								+ " in the enclosed rogue ap list.");
					}
				}

				idpMap.put(bssid, idp);
				
				if (idp.getInNetworkFlag() == BeCommunicationConstant.IDP_CONNECTION_IN_NET
						&& stationType == BeCommunicationConstant.IDP_STATION_TYPE_AP
						&& type == BeCommunicationConstant.IDP_TYPE_ROGUE) {
					inNetIdp.add(idp);
				}
			}
		}

		try {
			if (removedIdp.size() > 0) {
				BoMgmt.getIdpMgmt().removeIdps(removedIdp,
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

	private void debugIdpMitigationInfo(SimpleHiveAp s_ap,
			List<IdpMitigation> idpList) {
		try {
			log.debug("debugIdpMitigationInfo",
					"IDP mitigation Information from HiveAP:"
							+ s_ap.getMacAddress() + ", rogue ap size:"
							+ idpList.size());
			for (IdpMitigation mitigation : idpList) {
				String bssid = mitigation.getBssid();
				byte flag = mitigation.getFlag();
				List<Idp> clients = mitigation.getClients();

				StringBuilder buffer = new StringBuilder("Detail information:\n");
				buffer.append("Mitigation BSSID:\t" + bssid + "\n");
				buffer.append("Mitigation flag:\t" + flag + "\n");
				buffer.append("Mitigation client size:\t"
						+ (null == clients ? "null" : clients.size()) + "\n");
				if (null != clients) {
					for (Idp idp : clients) {
						buffer.append("IDP type:\t" + idp.getIdpType() + "\n");
						buffer
								.append("BSSID:\t" + idp.getIfMacAddress()
										+ "\n");
						buffer.append("Remove flag:\t" + idp.getRemovedFlag()
								+ "\n");
						buffer.append("Station type:\t" + idp.getStationType()
								+ "\n");
						buffer.append("SSID:\t" + idp.getSsid() + "\n");
						buffer.append("innet flag:\t" + idp.getInNetworkFlag()
								+ "\n");
						buffer.append("RSSI:\t" + idp.getRssi() + "\n");
					}
				}
				log.debug("debugIdpMitigationInfo", buffer.toString());
			}
		} catch (Exception e) {
			log.error("debugIdpMitigationInfo", "debug idp error.", e);
		}
	}

}