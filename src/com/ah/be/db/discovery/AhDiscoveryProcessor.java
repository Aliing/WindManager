package com.ah.be.db.discovery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeConfigUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.app.HmBeParaUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.db.discovery.event.AhDiscoveryEvent;
import com.ah.be.db.discovery.event.AhDiscoveryEvent.HiveApType;
import com.ah.be.parameter.BeParaModule;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.DeviceInventory;
import com.ah.bo.hiveap.DeviceResetConfig;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.HiveApMgmt;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.HiveApMgmtImpl;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.useraccess.MgmtServiceIPTrack;
import com.ah.bo.wlan.RadioProfile;
import com.ah.ui.actions.monitor.SystemStatusCache;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.ws.rest.client.utils.DeviceImpUtils;
import com.ah.ws.rest.client.utils.DeviceUtils;

public class AhDiscoveryProcessor implements QueryBo {

	private static final Tracer log = new Tracer(AhDiscoveryProcessor.class.getSimpleName());

	public static final short DEFAULT_BULK_SIZE = 50;

	public static final short DEFAULT_FLUSH_INTERVAL = 1; // in seconds.

	private final short bulkSize;

	private final short flushInterval;

	private final Map<String, BeAPConnectEvent> localReqsHolder;

	/* Default Config Template */
	private final ConfigTemplate defTemp;

	/* Default Radio Profile for A Mode */
	private final RadioProfile defRadioA;

	/* Default Radio Profile for BG Mode */
	private final RadioProfile defRadioBG;

	/* Default Radio Profile for NG Mode */
	private final RadioProfile defRadioNG;

	/* Default Radio Profile for NA Mode */
	private final RadioProfile defRadioNA;

	/* Default Radio Profile for AC Mode */
	private final RadioProfile defRadioAC;

	private short flushTimer;

	public AhDiscoveryProcessor(short bulkSize, short flushInterval) {
		this.bulkSize = bulkSize > 0 ? bulkSize : DEFAULT_BULK_SIZE;
		this.flushInterval = flushInterval > 0 ? flushInterval : DEFAULT_FLUSH_INTERVAL;
		localReqsHolder = new HashMap<String, BeAPConnectEvent>();
		defTemp = HmBeParaUtil.getDefaultTemplate();
		defRadioA = HmBeParaUtil.getDefaultRadioAProfile();
		defRadioBG = HmBeParaUtil.getDefaultRadioBGProfile();
		defRadioNG = HmBeParaUtil.getDefaultRadioNGProfile();
		defRadioNA = HmBeParaUtil.getDefaultRadioNAProfile();
		defRadioAC = HmBeParaUtil.getDefaultRadioACProfile();
	}

	public void reinit() {
		flushTimer = flushInterval;
	}

	public void decreaseFlushTimer() {
		if (--flushTimer <= 0) {
			// Flush request holder periodically.
			flush();

			// Reinitialize flush timer.
			reinit();
		}
	}

	public void execute(BeAPConnectEvent event) {
		String macAddress = event.getApMac();
		BeAPConnectEvent connectEvent = localReqsHolder.put(macAddress, event);

		if (connectEvent != null) {
			String oldEventType = connectEvent.isConnectState() ? "CAPWAP Connect Event"
					: "CAPWAP Disconnect Event";
			String newEventType = event.isConnectState() ? "CAPWAP Connect Event"
					: "CAPWAP Disconnect Event";
			log.info("execute", "[" + macAddress + "]Substituted " + newEventType + " for "
					+ oldEventType);
		}

		if (localReqsHolder.size() >= bulkSize) {
			// Flush request holder directly when reaching the max number of
			// requests.
			flush();

			// Reinitialize flush timer.
			reinit();
		}
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HiveApAutoProvision) {
			HiveApAutoProvision autoProvision = (HiveApAutoProvision) bo;

			if (autoProvision.getMacAddresses() != null) {
				autoProvision.getMacAddresses().size();
			}

			if (autoProvision.getDeviceInterfaces() != null) {
				autoProvision.getDeviceInterfaces().size();
			}

			if (autoProvision.getIpSubNetworks() != null) {
				autoProvision.getIpSubNetworks().size();
			}
		} else if (bo instanceof ConfigTemplate) {
			if (bo.getOwner() != null) {
				bo.getOwner().getId();
			}
		}

		return null;
	}

	private void flush() {
		int size = localReqsHolder.size();

		if (size == 0) {
			return;
		}

		HiveApMgmt hiveApMgmt = BoMgmt.getHiveApMgmt();
		hiveApMgmt.updateDiscoveredHiveAps(localReqsHolder);
		Collection<BeAPConnectEvent> connectEvents = localReqsHolder.values();

		// Filter disconnect events.
		filter(connectEvents);

		size = localReqsHolder.size();

		if (size == 0) {
			return;
		}

		List<HiveAp> newHiveAps = new ArrayList<HiveAp>(size);
		Collection<HiveAp> rejectedHiveAps = new ArrayList<HiveAp>(size);
		Collection<HiveAp> resetConfigHiveAps = new ArrayList<HiveAp>(size);
		Collection<HiveAp> notNeedresetConfigHiveAps = new ArrayList<HiveAp>(size);
		Collection<HiveAp> rejectedHiveApsNeedLog = new ArrayList<HiveAp>(size);
		Collection<HiveAp> needRemovePreConfigHiveAps = new ArrayList<HiveAp>(size);

		// A map of HmDomains, keyed by HmDomain and value is a collection of HiveApAutoProvisions.
		Map<HmDomain, Collection<HiveApAutoProvision>> hmDomains = findDomains(connectEvents);

		// A map of HmStartConfigs, keyed by the HmDomain.
		Map<HmDomain, HmStartConfig> hmStartConfigs = findHmStartConfigs(hmDomains.keySet());

		// A map of express mode ConfigTemplates, keyed by the HmDomain.
		Map<Long, ConfigTemplate> expressModeTemplates = findExpressModeConfigTemplates(hmStartConfigs
				.values());

		for (BeAPConnectEvent event : connectEvents) {
			HiveAp newHiveAp = createHiveAp(event);

			// Real/Simulated HiveAP CAPWAP connections.
			if (event.getCapwapClientType() == BeCommunicationConstant.CAPWAPCLIENTTYPE_AP) {
				HmDomain hmDomain = getMatchedDomain(hmDomains, event, newHiveAp);

				if (hmDomain != null) {
					newHiveAp.setOwner(hmDomain);
					newHiveAp.initDeviceStpSettings();

					if (hmDomain.isRestoring()) {
						// Because the restoration is being executed on the VHM into which newly discovered HiveAPs entering are rejected.
						AhAppContainer.getBeFaultModule().getHiveApAlarmMgmt().vhmRestore(newHiveAp,
								true);
						rejectedHiveAps.add(newHiveAp);
					} else {
						if (isNotResetConfigDevice(newHiveAp)) {
							notNeedresetConfigHiveAps.add(newHiveAp);
							//resetConfigHiveAps.add(newHiveAp);
						}
						if (isNotCustomDevice(newHiveAp)) {
							if (!notNeedresetConfigHiveAps.isEmpty() && notNeedresetConfigHiveAps.contains(newHiveAp)) {
								resetConfigHiveAps.add(newHiveAp);
								notNeedresetConfigHiveAps.remove(newHiveAp);
								continue;
							}
							rejectedHiveApsNeedLog.add(newHiveAp);
							continue;
						} else {
							if (!notNeedresetConfigHiveAps.isEmpty() && notNeedresetConfigHiveAps.contains(newHiveAp)) {
								long count = QueryUtil.findRowCount(DeviceInventory.class,
										new FilterParams("owner.id=:s1 and serialNumber=:s2",
												new Object[]{hmDomain.getId(), newHiveAp.getSerialNumber()}));
								if (count==0) {
									resetConfigHiveAps.add(newHiveAp);
									notNeedresetConfigHiveAps.remove(newHiveAp);
									continue;
								}
							}
						}
						if (NmsUtil.isHostedHMApplication() && !newHiveAp.isSimulated()) {
							List<HiveAp> needRemovePreAp = QueryUtil.executeQuery(HiveAp.class,null,
									new FilterParams("serialNumber=:s1 and manageStatus=:s2",
											new Object[] {newHiveAp.getSerialNumber(), HiveAp.STATUS_PRECONFIG}),
											newHiveAp.getOwner().getId());
							if (!needRemovePreAp.isEmpty()) {
								HiveAp preHiveAp = needRemovePreAp.get(0);
								String oldMacAddress = preHiveAp.getMacAddress();
								needRemovePreConfigHiveAps.add(preHiveAp);
								preHiveAp.setMacAddress(newHiveAp.getMacAddress());
								hiveApMgmt.updateDiscoveredPreviewConfigHiveAps(preHiveAp, event, oldMacAddress);
								continue;
							}
						}

						// Reset config template if using HM easy mode.
						ConfigTemplate expressModeTemplate = expressModeTemplates.get(hmDomain.getId());

						if (expressModeTemplate != null && newHiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP) {
							newHiveAp.setConfigTemplate(expressModeTemplate);
						}

						HiveApAutoProvision autoProvision = getAutoProvisionByDevice(newHiveAp, hmDomains.get(hmDomain));

						if (autoProvision != null) {
							if (newHiveAp.isSimulated()) {
								// For simulated HiveAPs.
								if (!hmDomain.isManagedSimApNumFull()) {
									hiveApMgmt.setAutoProvisioningConfig(newHiveAp, autoProvision);
									hmDomain.setManagedSimApNum(hmDomain.getManagedSimApNum() + 1);
								} else {
									// Reach the maximum number of acceptable
									// simulated APs.
									AhAppContainer.getBeFaultModule().getHiveApAlarmMgmt()
											.reachMaxApNum(newHiveAp);
								}
							} else {
								String msg = null;
								// For real HiveAPs.
								if ((newHiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP
										|| newHiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER
										|| newHiveAp.getDeviceType() == HiveAp.Device_TYPE_SWITCH)
										&& !hmDomain.isManagedApNumFull()) {
									hiveApMgmt.setAutoProvisioningConfig(newHiveAp, autoProvision);
									hmDomain.setManagedApNum(hmDomain.getManagedApNum() + 1);
									//FIXME	cvg does not support auto provision now
								} else if (newHiveAp.isVpnGateway()) {
//									&& (msg = BeTopoModuleUtil.isDomainAllowManageRealHiveAP(hmDomain.getId(), 1, true)) == null) {
//								hiveApMgmt.setAutoProvisioningConfig(newHiveAp, autoProvision);
								} else {
									// Reach the maximum number of acceptable
									// APs.
									AhAppContainer.getBeFaultModule().getHiveApAlarmMgmt()
											.reachMaxApNum(newHiveAp, msg);
								}
							}
						} else {
							// Simulated HiveAPs should be accepted directly.
							if (newHiveAp.isSimulated()) {
								if (!hmDomain.isManagedSimApNumFull()) {
									newHiveAp.setManageStatus(HiveAp.STATUS_MANAGED);
									hmDomain.setManagedSimApNum(hmDomain.getManagedSimApNum() + 1);
								} else {
									// Reach the maximum number of acceptable APs.
									AhAppContainer.getBeFaultModule().getHiveApAlarmMgmt().reachMaxApNum(
											newHiveAp);
								}
							}
						}

						log.info("flush", "Discovered a new HiveAP: " + newHiveAp + "; Auto-Provisioning Enabled: " + (newHiveAp.getAutoProvisioningConfig() != null));
						newHiveAps.add(newHiveAp);
					}
				} else {
					log.warn("flush", "Rejected a HiveOS " + newHiveAp + " connection due to none VHMs to which it might be assigned.");
					rejectedHiveAps.add(newHiveAp);
				}
			} else {
				// Reject non-HiveAP connection.
				log.warn("flush", "Rejected a non-HiveOS " + newHiveAp + " connection.");
				rejectedHiveAps.add(newHiveAp);
			}
		}

		// Alarm is prior to AP to persist into DB sometimes, so it needs to set alarm severity during AP creation.
		setAlarmSeverity(newHiveAps);

		bulkCreateHiveAps(newHiveAps);

		if (rejectedHiveApsNeedLog!=null) {
			rejectedHiveAps.addAll(rejectedHiveApsNeedLog);
			DeviceUtils diu = DeviceImpUtils.getInstance();
			for(HiveAp oneAp : rejectedHiveApsNeedLog) {
				diu.generateAuditLog(HmAuditLog.STATUS_SUCCESS,
					MgrUtil.getUserMessage("geneva_08.reject.device.log",oneAp.getSerialNumber()), oneAp.getOwner());
			}
		}

		// Disconnect with the HiveAPs rejected.
		BeTopoModuleUtil.sendBeDeleteAPConnectRequest(rejectedHiveAps, false);

		resetDevicesConfig(resetConfigHiveAps, notNeedresetConfigHiveAps);

		// remove serial number is error device.
		//removePreconfigDevice(needRemovePreConfigHiveAps);

		localReqsHolder.clear();
	}

//	private void removePreconfigDevice(Collection<HiveAp> needRemovePreConfigHiveAps) {
//		try {
//			if (!needRemovePreConfigHiveAps.isEmpty()) {
//				Collection<Long> needRemovePreApIds = new ArrayList<Long>();
//				for(HiveAp oneAp: needRemovePreConfigHiveAps){
//					needRemovePreApIds.add(oneAp.getId());
//				}
//				Collection<Long> successLst = null;
//
//					successLst = BoMgmt.getMapMgmt().removeHiveApsReturnRemovedIds(needRemovePreApIds, false, false, null);
//
//				if (successLst!=null) {
//					DeviceUtils diu = DeviceImpUtils.getInstance();
//					for(HiveAp oneAp : needRemovePreConfigHiveAps) {
//						if (successLst.contains(oneAp.getId())) {
//						diu.generateAuditLog(HmAuditLog.STATUS_SUCCESS,
//							MgrUtil.getUserMessage("glasgow_05.hm.audit.log.remove.ap.from.hiveaplist.serialNum.notmatch",
//									new String[]{oneAp.getHostName()
//									, oneAp.getSerialNumber()
//									, HiveApAction.getHiveApListName(oneAp.getManageStatus())}), oneAp.getOwner());
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			log.error(e);
//		}
//	}

	private void resetDevicesConfig(Collection<HiveAp> resetConfigHiveAps,Collection<HiveAp> noNeedresetConfigHiveAps) {
		if (resetConfigHiveAps.isEmpty() &&  noNeedresetConfigHiveAps.isEmpty()) {
			return ;
		}
		String cli = AhCliFactory.getResetDeviceToDefaultCli();

		List<HiveAp> serailSuccAp = new ArrayList<HiveAp>();
		List<String> serailSucc = new ArrayList<String>();

		for(HiveAp ap : resetConfigHiveAps) {
			try {
				BeCliEvent cliRequest = new BeCliEvent();
				int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
				cliRequest.setAp(ap);
				cliRequest.setClis(new String[] { cli });
				cliRequest.setSequenceNum(sequenceNum);
				cliRequest.buildPacket();
				int ret = AhAppContainer.getBeCommunicationModule().sendRequest(cliRequest);
				log.error("new discover device reset config result seq:" + ret);
				serailSuccAp.add(ap);
				serailSucc.add(ap.getSerialNumber());
			} catch (Exception e) {
				log.error("resetDevicesConfig", "new discover device:" + ap.getSerialNumber() + " reset config error! reason:", e);
			}
		}

		if (!serailSucc.isEmpty()) {
			try {
				QueryUtil.removeBos(DeviceResetConfig.class, new FilterParams("serialNumber", serailSucc));
				DeviceUtils diu = DeviceImpUtils.getInstance();
				for(HiveAp oneAp : serailSuccAp) {
					diu.generateAuditLog(HmAuditLog.STATUS_SUCCESS,
						MgrUtil.getUserMessage("info.cli.reset.device.successwithName",oneAp.getSerialNumber()), oneAp.getOwner());
				}
			} catch (Exception e) {
				log.error(e);
			}
		}
		if (!noNeedresetConfigHiveAps.isEmpty()) {
			try {
				List<String> removeSes = new ArrayList<String>();
				for(HiveAp ap : noNeedresetConfigHiveAps) {
					removeSes.add(ap.getSerialNumber());
				}

				QueryUtil.removeBos(DeviceResetConfig.class, new FilterParams("serialNumber", removeSes));
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

	private boolean isNotCustomDevice(HiveAp newHiveAp) {
		try {
			if (!NmsUtil.isHostedHMApplication() || newHiveAp.isSimulated()) {
				return false;
			}
			if (newHiveAp==null || newHiveAp.getSerialNumber()==null || newHiveAp.getSerialNumber().isEmpty()) {
				return true;
			}
			long count = QueryUtil.findRowCount(DeviceInventory.class,
					new FilterParams("owner.id=:s1 and serialNumber=:s2",
							new Object[]{newHiveAp.getOwner().getId(), newHiveAp.getSerialNumber()}));
			if (count==0) {
				return true;
			}
		} catch (Exception e) {
			log.error(e);
		}
		return false;
	}

	private boolean isNotResetConfigDevice(HiveAp newHiveAp) {
		try {
			if (newHiveAp.isSimulated()) {
				return false;
			}
			if (newHiveAp==null || newHiveAp.getSerialNumber()==null || newHiveAp.getSerialNumber().isEmpty()) {
				return false;
			}

			long count = QueryUtil.findRowCount(DeviceResetConfig.class,
					new FilterParams("serialNumber", newHiveAp.getSerialNumber()));
			if (count>0) {
				return true;
			}
		} catch (Exception e) {
			log.error(e);
		}
		return false;
	}

	private void filter(Collection<BeAPConnectEvent> events) {
		for (Iterator<BeAPConnectEvent> iter = events.iterator(); iter.hasNext();) {
			BeAPConnectEvent event = iter.next();

			if (!event.isConnectState()) {
				iter.remove();
			}
		}
	}

	private Map<HmDomain, Collection<HiveApAutoProvision>> findDomains(
			Collection<BeAPConnectEvent> events) {
		int size = events.size();
		Collection<String> serialNums = new ArrayList<String>(size);
		Collection<String> domainNames = new ArrayList<String>(size);
		String homeDomainName = HmDomain.HOME_DOMAIN.toLowerCase();
		domainNames.add(homeDomainName);
		StringBuilder inClauseForDomainNames = new StringBuilder("'" + homeDomainName + "'");

		for (BeAPConnectEvent event : events) {
			String domainName = event.getDomainName().trim().toLowerCase();
			String serialNum = event.getApSerialNum().trim();
			log.info("findDomains", "AP Node ID: " + event.getApMac() + "; Domain Name: " + domainName
					+ "; Serial Number: " + serialNum);

			if (!domainName.isEmpty() && !domainNames.contains(domainName)) {
				domainNames.add(domainName);
				inClauseForDomainNames.append(", '").append(domainName).append("'");
			}

			if (!serialNum.isEmpty()) {
				serialNums.add(serialNum);
			}
		}

//		List<?> configuredAutoProvisions = QueryUtil
//				.executeQuery(
//						"select bo from " + HiveApAutoProvision.class.getSimpleName()
//						+ " as bo where lower(bo.owner.domainName) in (" + inClauseForDomainNames.toString() + ")"
//						+ " and bo.autoProvision = true",
//						null, null, null, this);

		List<?> configuredAutoProvisions = QueryUtil
				.executeQuery(
						"select bo from " + HiveApAutoProvision.class.getSimpleName()
								+ " as bo where (lower(bo.owner.domainName) in (" + inClauseForDomainNames.toString() + ")"
								+ " and bo.autoProvision = true )"
								+ " or bo.id in (select a.id from "
								+ HiveApAutoProvision.class.getSimpleName()
								+ " as a join a.macAddresses as joined",
						new SortParams("deviceType"),
						new FilterParams(
								"a.autoProvision = :s1 and a.accessControled = :s2 and joined in (:s3) )",
								new Object[] { true, true, serialNums }), null, this);

		// A map of HmDomains, keyed by HmDomain and value is a collection of HiveApAutoProvisions.
		Map<HmDomain, Collection<HiveApAutoProvision>> hmDomains = new HashMap<HmDomain, Collection<HiveApAutoProvision>>(
				domainNames.size());

		for (Object obj : configuredAutoProvisions) {
			HiveApAutoProvision autoProvision = (HiveApAutoProvision) obj;
			HmDomain hmDomain = autoProvision.getOwner();
			Collection<HiveApAutoProvision> autoProvisions = hmDomains.get(hmDomain);

			if (autoProvisions == null) {
				hmDomain.computeManagedApNum();
				hmDomain.computeManagedSimApNum();
				autoProvisions = new ArrayList<HiveApAutoProvision>();
				hmDomains.put(hmDomain, autoProvisions);

				// Remove this domain name.
				domainNames.remove(hmDomain.getDomainName().toLowerCase());
			}

			autoProvisions.add(autoProvision);
		}

		// Find extra domains where none Auto-Provisioning profiles are configured.
		if (!domainNames.isEmpty()) {
			List<HmDomain> extraDomains  = QueryUtil.executeQuery(HmDomain.class, null, new FilterParams(
					"lower(domainName)", domainNames));

			for (HmDomain hmDomain : extraDomains) {
				hmDomain.computeManagedApNum();
				hmDomain.computeManagedSimApNum();
				Collection<HiveApAutoProvision> autoProvisions = new ArrayList<HiveApAutoProvision>(
						0);
				hmDomains.put(hmDomain, autoProvisions);
			}
		}

		return hmDomains;
	}

	private Map<HmDomain, HmStartConfig> findHmStartConfigs(Collection<HmDomain> hmDomains) {
		List<HmStartConfig> startConfigs = QueryUtil.executeQuery(HmStartConfig.class, null,
				new FilterParams("owner", hmDomains));
		Map<HmDomain, HmStartConfig> domainAndStartConfigMap = new HashMap<HmDomain, HmStartConfig>(startConfigs.size());

		for (HmStartConfig startConfig : startConfigs) {
			domainAndStartConfigMap.put(startConfig.getOwner(), startConfig);
		}

		return domainAndStartConfigMap;
	}

	private Map<Long, ConfigTemplate> findExpressModeConfigTemplates(
			Collection<HmStartConfig> hmStartConfigs) {
		Collection<HmDomain> expressModeDomains = new ArrayList<HmDomain>(hmStartConfigs.size());
		Map<Long, ConfigTemplate> templates = new HashMap<Long, ConfigTemplate>(
				hmStartConfigs.size());

		for (HmStartConfig startConfig : hmStartConfigs) {
			if (startConfig.getModeType() == HmStartConfig.HM_MODE_EASY) {
				HmDomain startConfigOwner = startConfig.getOwner();
				expressModeDomains.add(startConfigOwner);
			}
		}

		if (!expressModeDomains.isEmpty()) {
			List<ConfigTemplate> expressModeDefaultConfigTemplates = QueryUtil.executeQuery(ConfigTemplate.class, null, new FilterParams("defaultFlag = :s1 and owner in (:s2)", new Object[]{ false, expressModeDomains }), null, this);

			for (ConfigTemplate expressModeDefaultConfigTemplate : expressModeDefaultConfigTemplates) {
				HmDomain configTemplateOwner = expressModeDefaultConfigTemplate.getOwner();

				final Long id = configTemplateOwner.getId();
				if (!templates.containsKey(id)) {
                    templates.put(id, expressModeDefaultConfigTemplate);
				}
			}
		}

		return templates;
	}

	private HmDomain getMatchedDomain(Map<HmDomain, Collection<HiveApAutoProvision>> hmDomains,
			BeAPConnectEvent event, HiveAp hiveAp) {
		HmDomain candidate = null;
		HmDomain homeDomain = null;
		String configDomainName = event.getDomainName().trim();

		// Find home and pre-configured domains (if any) out first.
		for (HmDomain hmDomain : hmDomains.keySet()) {
			if (configDomainName.equalsIgnoreCase(hmDomain.getDomainName())) {
				candidate = hmDomain;
			}

			if (hmDomain.isHomeDomain()) {
				homeDomain = hmDomain;
			}
		}

		// Find domain by HiveAP serial number.
		scope:
		for (HmDomain hmDomain : hmDomains.keySet()) {
			for (HiveApAutoProvision autoProvision : hmDomains.get(hmDomain)) {
				if (hiveAp.getHiveApModel() == autoProvision.getModelType()
						&& autoProvision.isAccessControled()
						&& autoProvision.getMacAddresses() != null
						&& autoProvision.getMacAddresses().contains(hiveAp.getSerialNumber())) {
					candidate = hmDomain;
					break scope;
				}
			}
		}

		// Consider 'home' as the target domain if no one was found above.
		if (candidate == null) {
			boolean isHmolHomeDiscoveryDisabled;
			boolean isHMOL = NmsUtil.isHostedHMApplication();

			if (isHMOL) {
				String homeDiscoveryDisabled = System.getProperty("hm.homeDiscoveryDisabled");
				isHmolHomeDiscoveryDisabled = homeDiscoveryDisabled == null || Boolean.valueOf(homeDiscoveryDisabled);
			} else {
				isHmolHomeDiscoveryDisabled = false;
			}

			if (!configDomainName.isEmpty()) {
				hiveAp.setOwner(homeDomain);
				AhAppContainer.getBeFaultModule().getHiveApAlarmMgmt().vhmAbsence(hiveAp,
						configDomainName, isHmolHomeDiscoveryDisabled);
			} else {
				if (isHmolHomeDiscoveryDisabled) {
					hiveAp.setOwner(homeDomain);
					AhAppContainer.getBeFaultModule().getHiveApAlarmMgmt().homeDiscoveryDisabled(hiveAp);
				}
			}

			if (!isHmolHomeDiscoveryDisabled) {
				candidate = homeDomain;
			}
		} else if (candidate.isHomeDomain()) {
			boolean isHMOL = NmsUtil.isHostedHMApplication();

			if (isHMOL) {
				String homeDiscoveryDisabled = System.getProperty("hm.homeDiscoveryDisabled");
				boolean isHomeDiscoveryDisabled = homeDiscoveryDisabled == null || Boolean.valueOf(homeDiscoveryDisabled);

				if (isHomeDiscoveryDisabled) {
					hiveAp.setOwner(candidate);
					AhAppContainer.getBeFaultModule().getHiveApAlarmMgmt().homeDiscoveryDisabled(hiveAp);
					candidate = null;
				}
			}
		} else if (candidate.isDisabled()) {
			// Disabled domain name.
			String disabledDomainName = candidate.getDomainName();
			boolean isHmolHomeDiscoveryDisabled;
			boolean isHMOL = NmsUtil.isHostedHMApplication();

			if (isHMOL) {
				String homeDiscoveryDisabled = System.getProperty("hm.homeDiscoveryDisabled");
				isHmolHomeDiscoveryDisabled = homeDiscoveryDisabled == null || Boolean.valueOf(homeDiscoveryDisabled);
			} else {
				isHmolHomeDiscoveryDisabled = false;
			}

			hiveAp.setOwner(homeDomain);

			// Give out an alarm in 'home' domain on the domain replacement.
			AhAppContainer.getBeFaultModule().getHiveApAlarmMgmt().vhmDisable(hiveAp,
					disabledDomainName, true, isHmolHomeDiscoveryDisabled);

			// Use 'home' instead of the domain disabled.
			candidate = isHmolHomeDiscoveryDisabled ? null : homeDomain;
		}

		return candidate;
	}

	private HiveAp createHiveAp(BeAPConnectEvent event) {
		long currTime = System.currentTimeMillis();
		boolean isDynamicIp = event.getIpType() == 0;
		boolean isDtlsEnable = event.getDtlsState() == BeCommunicationConstant.DTLSSTATE_USEDTLS;
		short type = event.getAPType() == BeCommunicationConstant.AP_TYPE_PORTAL ? HiveAp.HIVEAP_TYPE_PORTAL
				: HiveAp.HIVEAP_TYPE_MP;
		String productName = event.getProductName();
		short hiveApModel = AhConstantUtil.getHiveApModelByProductName(productName);
		RadioProfile wifi0RadioProfile = HiveAp.is11nHiveAP(hiveApModel) ? defRadioNG : defRadioBG;
		RadioProfile wifi1RadioProfile = null;
		if(HiveAp.is11acHiveAP(hiveApModel)){
			wifi1RadioProfile = defRadioAC;
		}else{
			wifi1RadioProfile = HiveAp.is11nHiveAP(hiveApModel) ? defRadioNA : defRadioA;
		}
		String macAddress = event.getApMac();
		String gateway = event.getGateway();

		if ("0.0.0.0".equals(gateway)) {
			gateway = null;
		}

		String disVer = event.getDisplayVersion();

		if (disVer == null || disVer.isEmpty()) {
			disVer = event.getSoftVersion();
		}

		HiveAp hiveAp = new HiveAp();
		hiveAp.setSerialNumber(event.getApSerialNum());
		hiveAp.setMacAddress(macAddress);
		hiveAp.setHostName(event.getWtpName());
		hiveAp.setIpAddress(event.getIpAddr());
		hiveAp.setGateway(gateway);
		hiveAp.setNetmask(event.getNetmask());
		hiveAp.setDhcp(isDynamicIp);
		hiveAp.setProductName(productName);
		hiveAp.setHiveApModel(hiveApModel);
		hiveAp.setSignatureVer(event.getL7SignatureFileVersion());
		hiveAp.setSwitchChipVersion(event.getSwitchChipVersion());
		hiveAp.setHardwareRevision(event.getHardwareRevision());

		if(event.getTypeFlag() == BeAPConnectEvent.TYPE_FLAG_AP){
			if(hiveAp.getDeviceInfo().isSptEthernetMore_24()){
				hiveAp.setDeviceType(HiveAp.Device_TYPE_SWITCH);
			}else{
				hiveAp.setDeviceType(HiveAp.Device_TYPE_HIVEAP);
			}
		}else if(event.getTypeFlag() == BeAPConnectEvent.TYPE_FLAG_BR){
			hiveAp.setDeviceType(HiveAp.Device_TYPE_BRANCH_ROUTER);
		}else if(event.getTypeFlag() == BeAPConnectEvent.TYPE_FLAG_CVG){
			if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_330 ||
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_350 ||
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200 ||
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_WP ||
					hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ){
				hiveAp.setDeviceType(HiveAp.Device_TYPE_VPN_BR);
			}else{
				hiveAp.setDeviceType(HiveAp.Device_TYPE_VPN_GATEWAY);
			}
		}else{
			hiveAp.setDeviceType(HiveAp.Device_TYPE_HIVEAP);
		}
		//device BR100 and CVG from 5.1.1.0 version can support AP model.
		if(NmsUtil.compareSoftwareVersion("5.1.1.0", event.getSoftVersion()) > 0 &&
				(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100 || hiveAp.isCVGAppliance()) ){
			hiveAp.setDeviceType(AhConstantUtil.getDeviceTypeByProductName(productName));
		}else if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200 || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_WP || hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ){
			hiveAp.setDeviceType(AhConstantUtil.getDeviceTypeByProductName(productName));
		}

		// fix bug 17587 CVG first time connect to HM eth0 mode is wan but L3 VPN not config, so send HM is BR mode.
		if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER && hiveAp.isCVGAppliance()){
			hiveAp.setDeviceType(AhConstantUtil.getDeviceTypeByProductName(productName));
		}
		
		hiveAp.init();
		hiveAp.initInterface();

		if(hiveAp.isVpnGateway() || hiveAp.isBranchRouter()){
			hiveAp.setDhcp(false);
		}
		hiveAp.setHiveApType(type);
		hiveAp.setSoftVer(event.getSoftVersion());
		hiveAp.setDisplayVer(disVer);
		hiveAp.setConfigVer(event.getConfigVersion());
		hiveAp.setLocation(event.getLocation());
		hiveAp.setCountryCode(event.getCountryCode());
		hiveAp.setRegionCode(event.getRegionCode());
		hiveAp.setCapwapLinkIp(event.getHivemanagerIP());
		hiveAp.setCapwapClientIp(event.getCapwapClientIP());
		hiveAp.setCurrentDtlsEnable(isDtlsEnable);
		hiveAp.setOrigin(HiveAp.ORIGIN_DISCOVERED);
		hiveAp.setTimeZoneOffset((byte) (TimeZone.getTimeZone(event.getMessageTimeZone())
				.getRawOffset() / 3600000));

		if (hiveApModel == HiveAp.HIVEAP_MODEL_BR200_WP || hiveApModel == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ) hiveAp.setRadioConfigType(HiveAp.RADIO_MODE_ACCESS_ALL);
		hiveAp.setWifi0RadioProfile(wifi0RadioProfile);
		hiveAp.setWifi1RadioProfile(wifi1RadioProfile);
		hiveAp.setConfigTemplate(defTemp);
		hiveAp.setDiscoveryTime(currTime);
		hiveAp.setConnChangedTime(currTime);
		hiveAp.setTotalConnectTimes(1);
		hiveAp.setRunningHive(event.getHiveName());
		hiveAp.setCapwapConnectEvent(event);
		hiveAp.setProxyName(event.getProxyName());
		hiveAp.setProxyPort(event.getProxyPort());
		hiveAp.setProxyUsername(event.getProxyUserName());
		hiveAp.setProxyPassword(event.getProxyPassword());
		hiveAp.setTransferProtocol(event.getTransferMode());
		hiveAp.setTransferPort(event.getConnectPort());
		hiveAp.setConnected(true);
		hiveAp.setPppoeEnableCurrent(event.isEnablePppoe());

		if (!isDynamicIp) {
			hiveAp.setCfgIpAddress(event.getIpAddr());
			hiveAp.setCfgGateway(gateway);
			hiveAp.setCfgNetmask(event.getNetmask());
		}

		if (event.getUpTime() > 0) {
			hiveAp.setUpTime(currTime - event.getUpTime() * 1000);
		}

		if (event.isSimulate()) {
			hiveAp.setSimulated(event.isSimulate());
			hiveAp.setSimulateCode(event.getSimulateCode());
			hiveAp.setSimulateClientInfo(event.getSimulateClientInfo());
			hiveAp.getWifi0().setChannel(event.getWifi0Channel());
			hiveAp.getWifi0().setPower(event.getWifi0Power());
			hiveAp.getWifi1().setChannel(event.getWifi1Channel());
			hiveAp.getWifi1().setPower(event.getWifi1Power());

//			fix bug 17980
			if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100) {
				hiveAp.setDeviceType(AhConstantUtil.getDeviceTypeByProductName(productName));
			}
		}
		if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY){
			hiveAp.setVpnMark(HiveAp.VPN_MARK_SERVER);
			hiveAp.setVpnIpTrack(QueryUtil.findBoByAttribute(MgmtServiceIPTrack.class, "trackName",
					BeParaModule.DEFAULT_IP_TRACKING_VPN_GATEWAY_NAME_NEW));
			HiveApMgmtImpl.synchronizeCVGInterfaceState(hiveAp);
		}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER){
			hiveAp.setVpnMark(HiveAp.VPN_MARK_CLIENT);
		}else if(hiveAp.getDeviceType() == HiveAp.Device_TYPE_VPN_BR){
			hiveAp.setVpnMark(HiveAp.VPN_MARK_SERVER);
		}

		//init bonjour priority
		hiveAp.setPriority(HiveAp.getDefaultBonjourPriority(hiveAp.getHiveApModel()));

		//init switch pse setting
		hiveAp.setMaxpowerBudget(HiveAp.getDefaultMaxPowerBudget(hiveAp.getHiveApModel()));

		return hiveAp;
	}

	private void setAlarmSeverity(Collection<HiveAp> newDiscoveredHiveAps) {
		if (newDiscoveredHiveAps.isEmpty()) {
			return;
		}

		StringBuilder inClauseBufForNewApNodeIds = new StringBuilder();

		for (Iterator<HiveAp> newHiveApIter = newDiscoveredHiveAps.iterator(); newHiveApIter.hasNext();) {
			HiveAp newHiveAp = newHiveApIter.next();
			String newHiveApNodeId = newHiveAp.getMacAddress();
			inClauseBufForNewApNodeIds.append("'").append(newHiveApNodeId).append("'");

			if (newHiveApIter.hasNext()) {
				inClauseBufForNewApNodeIds.append(",");
			}
		}

		List<?> alarms = QueryUtil.executeNativeQuery("select distinct on (apId) apId, severity from ah_alarm where apId in (" + inClauseBufForNewApNodeIds.toString() + ") order by apId, severity desc");

		for (Object alarm : alarms) {
			Object[] alarmAttrs = (Object[]) alarm;
			String apNodeId = (String) alarmAttrs[0];
			short severity = (Short) alarmAttrs[1];

			for (HiveAp newHiveAp : newDiscoveredHiveAps) {
				String newHiveApNodeId = newHiveAp.getMacAddress();

				if (apNodeId.equals(newHiveApNodeId)) {
					newHiveAp.setSeverity(severity);
					break;
				}
			}
		}
	}

	private void bulkCreateHiveAps(Collection<HiveAp> newDiscoveredHiveAps) {
		if (newDiscoveredHiveAps.isEmpty()) {
			return;
		}

		try {
			QueryUtil.bulkCreateBos(newDiscoveredHiveAps);
			
			// Update Topology
			updateTopology(newDiscoveredHiveAps);

			// Update relevant caches.
			updateCaches(newDiscoveredHiveAps);

			// Notify other modules of these newly discovered HiveAPs.
			notify(newDiscoveredHiveAps);

		} catch (Exception e) {
			String errorMsg = MgrUtil.getUserMessage("hm.system.log.ad.discovery.processor.creation.for.ap.fail");
			log.error("bulkCreateHiveAps", errorMsg, e);
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_CRITICAL, HmSystemLog.FEATURE_DISCOVERY,
					errorMsg);

			// Disconnect with the HiveAPs which couldn't be added into database.
			BeTopoModuleUtil.sendBeDeleteAPConnectRequest(newDiscoveredHiveAps, false);
		}
	}

	private void updateTopology(Collection<HiveAp> hiveAps) {
	    try {
            for (HiveAp hiveAp : hiveAps) {
                MapContainerNode mapParent = hiveAp.getMapContainer();
                if (mapParent != null) {
                    MapLeafNode leafNode = new MapLeafNode();
                    BoMgmt.getMapMgmt().placeIcon(mapParent, leafNode);
                    leafNode.setSeverity(hiveAp.getSeverity());
                    leafNode.setIconName(MapMgmt.BASE_LEAFNODE_ICON);
                    hiveAp = BoMgmt.getMapMgmt().createMapLeafNode(hiveAp, leafNode, mapParent);

                    // propagate to Map Hierarchy Cache;
                    BoMgmt.getMapHierarchyCache().hiveApAdded(hiveAp);
                    // propagate the updated HiveAP to Cache.
                    CacheMgmt.getInstance().updateSimpleHiveAp(hiveAp);
                }
            }
        } catch (Exception e) {
            String errorMsg = "Fail to update Topology for new HiveAP";
            log.error("updateTopology", errorMsg, e);
        }
    }

    private void updateCaches(Collection<HiveAp> hiveAps) {
		// Update HiveAP cache.
		CacheMgmt.getInstance().bulkAddHiveAps(hiveAps);

		for (HiveAp hiveAp : hiveAps) {
			// Update map cache.
			BoMgmt.getMapHierarchyCache().hiveApAdded(hiveAp);

			// Update domain cache.
			if (hiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
				SystemStatusCache.getInstance().incrementNewHiveApCount(1,
						hiveAp.getOwner().getId());
			}
		}
	}

	private void notify(Collection<HiveAp> discoveredHiveAps) {
		Collection<AhDiscoveryEvent> discoveryEvents = new ArrayList<AhDiscoveryEvent>(
				discoveredHiveAps.size());

		for (HiveAp discoveredHiveAp : discoveredHiveAps) {
			AhDiscoveryEvent discoveryEvent = new AhDiscoveryEvent(discoveredHiveAp,
					HiveApType.CREATED);
			discoveryEvent.setSeparatingMapLocation(true);
			discoveryEvent.setCapwapConnectEvent(discoveredHiveAp.getCapwapConnectEvent());
			discoveryEvents.add(discoveryEvent);

			//create hiveap auto provision mapping
			if(discoveredHiveAp.getAutoProvisioningConfig() != null){
				HmBeConfigUtil.getProvisionProcessor().getDeviceAPMapping().put(
						discoveredHiveAp.getMacAddress(), discoveredHiveAp.getAutoProvisioningConfig());
			}
		}

		BoMgmt.getHiveApMgmt().sendDiscoveryEvent(discoveryEvents);
	}

	public static HiveApAutoProvision getAutoProvisionByDevice(HiveAp newHiveAp, Collection<HiveApAutoProvision> autoProvisionList){
		HiveApAutoProvision autoProvision = null;

		if(newHiveAp == null || autoProvisionList == null || autoProvisionList.isEmpty()){
			return autoProvision;
		}

		//get HiveApAutoProvision with serial number
		for (HiveApAutoProvision autoObj : autoProvisionList){
			if(newHiveAp.getHiveApModel() == autoObj.getModelType() &&
					autoObj.isAccessControled() &&
					autoObj.getMacAddresses() != null &&
					autoObj.getMacAddresses().contains(newHiveAp.getSerialNumber()) ){
				autoProvision = autoObj;
				break;
			}
		}

		//get HiveApAutoProvision with Ip subnet
		if(autoProvision == null){
			HiveApMgmtImpl.NetworkRange rangeAp = new HiveApMgmtImpl.NetworkRange(newHiveAp.getIpAddress(), newHiveAp.getHiveApModel());
			for (HiveApAutoProvision autoObj : autoProvisionList){
				if(autoProvision != null){
					break;
				}
				if(newHiveAp.getHiveApModel() == autoObj.getModelType() &&
						newHiveAp.getDeviceType() == autoObj.getDeviceType() &&
						autoObj.isAccessControled() &&
						autoObj.getIpSubNetworks() != null ){
					for(String ipNetwork : autoObj.getIpSubNetworks()){
						HiveApMgmtImpl.NetworkRange rangeNet = new HiveApMgmtImpl.NetworkRange(ipNetwork, autoObj.getModelType());
						if(rangeNet.inRange(rangeAp)){
							autoProvision = autoObj;
							break;
						}
					}
				}
			}
		}

		//get HiveApAutoProvision with HiveAP model
		if(autoProvision == null){
			for (HiveApAutoProvision autoObj : autoProvisionList){
				if(newHiveAp.getHiveApModel() == autoObj.getModelType() &&
						newHiveAp.getDeviceType() == autoObj.getDeviceType() &&
						!autoObj.isAccessControled() ){
					autoProvision = autoObj;
					break;
				}
			}
		}

		return autoProvision;
	}

}