package com.ah.bo.mgmt.impl;

/*
 * @author Chris Scheers
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeGetStatisticEvent;
import com.ah.be.config.event.AhDeviceRebootResultEvent;
import com.ah.be.config.hiveap.ScriptConfigObject;
import com.ah.be.config.hiveap.UpdateObject;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.be.db.configuration.ConfigurationProcessor.ConfigurationType;
import com.ah.be.db.configuration.ConfigurationResources;
import com.ah.be.db.discovery.event.AhDiscoveryEvent;
import com.ah.be.db.discovery.event.AhDiscoveryEvent.HiveApType;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.AutoProvisionDeviceInterface;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.hiveap.HiveApUpdateResult;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.HiveApMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.events.BoEventFilter;
import com.ah.events.BoEventListener;
import com.ah.ui.actions.hiveap.HiveApAction;
import com.ah.ui.actions.monitor.SystemStatusCache;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.coder.AhEncoder;
import com.ah.ws.rest.client.utils.DeviceImpUtils;
import com.ah.ws.rest.client.utils.DeviceUtils;
//import java.util.HashMap;

public final class HiveApMgmtImpl implements HiveApMgmt, QueryBo,
		BoEventListener<HiveAp> {

	private static final Tracer log = new Tracer(
			HiveApMgmtImpl.class.getSimpleName());

	private static HiveApMgmt instance;

	private HiveApMgmtImpl() {
	}

	public synchronized static HiveApMgmt getInstance() {
		if (instance == null) {
			instance = new HiveApMgmtImpl();
		}

		return instance;
	}

	@Override
	public synchronized void init() {
		BoMgmt.getBoEventMgmt().addBoEventListener(this,
				new BoEventFilter<>(HiveAp.class));
	}

	@Override
	public synchronized void destroy() {
		BoMgmt.getBoEventMgmt().removeBoEventListener(this);
	}

	@Override
	public void resetCapwapStatus(Long domainId) throws Exception {
		synchronized (this) {
			QueryUtil
					.updateBos(HiveAp.class,
							"connected = :s1,connectStatus=:s2",
							"connected = :s3", new Object[] { false,
									HiveAp.CONNECT_DOWN, true }, domainId);
		}
	}

	@Override
	public void resetConnectStatusViaCAPWAP(Long domainId) throws Exception {
		// query for id;
		List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, null,
				new FilterParams("connected", true), domainId);

		if (!hiveAps.isEmpty()) {
			log.debug("resetConnectStatusViaCAPWAP",
					"HiveAP size:" + hiveAps.size());

			// reset CAPWAP status;
			resetCapwapStatus(domainId);

			// sent disconnect event;
			BeTopoModuleUtil.sendBeDeleteAPConnectRequest(hiveAps, false);
		}
	}

	@Override
	public void updateUserDatabaseResult(HiveAp hiveAp,
			HiveApUpdateResult updateResult, ScriptConfigObject sco)
			throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			synchronized (this) {
				em = QueryUtil.getEntityManager();
				tx = em.getTransaction();
				tx.begin();
				if (hiveAp != null) {
					HiveAp merged = em.find(HiveAp.class, hiveAp.getId());
					if (merged != null) {
						boolean actived = sco.isActived();
						short result = sco.getResult();
						updateUserDatabaseIndication(merged, actived,
								result == UpdateParameters.UPDATE_SUCCESSFUL);
						em.merge(merged);
					}
				}

				if (updateResult != null) {
					em.merge(updateResult);
				}
				tx.commit();
			}
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			throw e;
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}

	private void updateUserDatabaseIndication(HiveAp hiveAp, boolean actived,
			boolean updateSuc) {
		if (updateSuc) {
			if (actived) {
				hiveAp.setPending_user(false);
				hiveAp.setPendingMsg_user(null);
				hiveAp.setPendingIndex_user(0);
			} else {
				if (hiveAp.isPending_user()) {
					int index = ConfigurationResources.CONFIGURATION_INEFFICACIOUS;
					hiveAp.setPendingIndex_user(index);
					hiveAp.setPendingMsg_user(null);
				}
			}
		} else {
			// update user database failed only changed for the pending HiveAP
			if (hiveAp.isPending_user()) {
				int index = ConfigurationResources.CONFIGURATION_UPLOAD_FAILED;
				hiveAp.setPendingIndex_user(index);
				hiveAp.setPendingMsg_user(null);
			}
		}
	}

	@Override
	public void updateConfigResult(HiveAp hiveAp,
			HiveApUpdateResult updateResult, UpdateObject upObject,
			int newConfigVer) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			synchronized (this) {
				em = QueryUtil.getEntityManager();
				tx = em.getTransaction();
				tx.begin();
				if (hiveAp != null) {
					HiveAp merged = em.find(HiveAp.class, hiveAp.getId());

					if (merged != null) {
						// If update successful, update config version also
						if (upObject.getResult() == UpdateParameters.UPDATE_SUCCESSFUL) {
							merged.setConfigVer(newConfigVer);
							merged.setLastCfgTime(System.currentTimeMillis());
						}
						boolean actived = upObject.isActived();
						short result = upObject.getResult();
						updateConfigIndication(merged, actived,
								result == UpdateParameters.UPDATE_SUCCESSFUL);
						em.merge(merged);
					}
				}

				if (updateResult != null) {
					em.merge(updateResult);
				}

				tx.commit();
			}
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			throw e;
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}

	private void updateConfigIndication(HiveAp hiveAp, boolean actived,
			boolean updateSuc) {
		if (updateSuc) {
			if (actived) {
				hiveAp.setPending(false);
				hiveAp.setPendingMsg(null);
				hiveAp.setPendingIndex(0);
			} else {
				// not effective only changed for the pending HiveAP
				if (hiveAp.isPending()) {
					int index = ConfigurationResources.CONFIGURATION_INEFFICACIOUS;
					hiveAp.setPendingIndex(index);
					hiveAp.setPendingMsg(null);
				}
			}
		} else {
			// update configuration failed
			if (hiveAp.isPending()) {
				// only changed for the pending HiveAP
				int index = ConfigurationResources.CONFIGURATION_UPLOAD_FAILED;
				hiveAp.setPendingIndex(index);
				hiveAp.setPendingMsg(null);
			}
		}
	}

	public void updateDelayTime(String apMac, int delayTime, short connectStatus)
			throws Exception {
		synchronized (this) {
			boolean connected = (connectStatus != 0) ? true:false;
			QueryUtil.updateBos(HiveAp.class,
					"connected = :s1,connectstatus = :s2,delaytime = :s3", "macAddress = :s4",
					new Object[] { connected,connectStatus, delayTime, apMac });
		}
	}

	public void updateImageResult(HiveAp hiveAp, HiveApUpdateResult updateResult)
			throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			synchronized (this) {
				em = QueryUtil.getEntityManager();
				tx = em.getTransaction();
				tx.begin();
				if (hiveAp != null) {
					HiveAp merged = em.find(HiveAp.class, hiveAp.getId());

					if (merged != null) {
						merged.setLastImageTime(System.currentTimeMillis());
						em.merge(merged);
					}
				}
				if (updateResult != null) {
					em.merge(updateResult);
				}
				tx.commit();
			}
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			throw e;
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}


	public void updateSignatureResult(HiveAp hiveAp, HiveApUpdateResult updateResult)
			throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			synchronized (this) {
				em = QueryUtil.getEntityManager();
				tx = em.getTransaction();
				tx.begin();
				if (hiveAp != null) {
					HiveAp merged = em.find(HiveAp.class, hiveAp.getId());

					if (merged != null) {
						merged.setLastSignatureTime(System.currentTimeMillis());
						em.merge(merged);
					}
				}
				if (updateResult != null) {
					em.merge(updateResult);
				}
				tx.commit();
			}
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			throw e;
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}

	public void updateL7SignatureVersion(String hiveApMac, int signatureVer)
			throws Exception {
		synchronized (this) {
			QueryUtil.updateBo(HiveAp.class, "signaturever = :s1",
					new FilterParams("macaddress = :s2", new Object[] {
							signatureVer, hiveApMac }));
		}
	}

	@Override
	public void updateHiveApType(String hiveApMac, short hiveApType)
			throws Exception {
		synchronized (this) {
			QueryUtil.updateBos(HiveAp.class, "hiveApType = :s1",
					"macAddress = :s2", new Object[] { hiveApType, hiveApMac });
		}
	}

	@Override
	public void updateHiveApRunningHive(String hiveApMac, String runningHive)
			throws Exception {
		synchronized (this) {
			QueryUtil
					.updateBos(HiveAp.class, "runningHive = :s1",
							"macAddress = :s2", new Object[] { runningHive,
									hiveApMac });
		}
	}

	@Override
	public void updateConfigurationIndication(HiveAp updateHiveAp)
			throws Exception {
		if (null == updateHiveAp) {
			return;
		}
		synchronized (this) {
			int pendingIndex = ConfigurationResources.CONFIG_HIVEAP_CHANGE;
			QueryUtil.updateBos(HiveAp.class,
					"pending = :s1, pendingIndex = :s2, pendingMsg = :s3",
					"id = :s4", new Object[] { true, pendingIndex, null,
							updateHiveAp.getId() });
		}
	}

	// for bug 20171
	@Override
	public void updateConfigurationIndication(boolean pending,
			int pendingIndex, String desc, ConfigurationType type)
			throws Exception {
		synchronized (this) {
			if (ConfigurationType.Configuration == type) {
				QueryUtil.updateBos(HiveAp.class,
						"pending = :s1, pendingIndex = :s2, pendingMsg = :s3",
						null, new Object[] { pending, pendingIndex,
								desc});
			}
		}
	}

	@Override
	public void updateConfigIndicationForL7Device(Long ownerId, boolean pending,
			int pendingIndex, String desc, ConfigurationType type)
			throws Exception {
		synchronized (this) {
			if (ConfigurationType.Configuration == type) {
				QueryUtil.updateBos(HiveAp.class,
						"pending = :s1, pendingIndex = :s2, pendingMsg = :s3",
						"(devicetype = 0 or devicetype = 1) and hiveapmodel <> 17 and softver >= '6.0.2.0'",
						new Object[] { pending, pendingIndex, desc}, ownerId);
			}
		}
	}

	@Override
	public void updateConfigurationIndication(Set<Long> ids, boolean pending,
			int pendingIndex, String desc, ConfigurationType type)
			throws Exception {
		synchronized (this) {
			if (ConfigurationType.Configuration == type) {
				QueryUtil.updateBos(HiveAp.class,
						"pending = :s1, pendingIndex = :s2, pendingMsg = :s3",
						"id in (:s4)", new Object[] { pending, pendingIndex,
								desc, ids });
			} else if (ConfigurationType.UserDatabase == type) {
				QueryUtil
						.updateBos(
								HiveAp.class,
								"pending_user = :s1, pendingIndex_user = :s2, pendingMsg_user = :s3",
								"id in (:s4)", new Object[] { pending,
										pendingIndex, desc, ids });
			}
		}
	}

	@Override
	public void updateConfigurationIndication(HiveAp auditHiveAp,
			Date auditTime, boolean match, ConfigurationType type)
			throws Exception {
		if (null == auditHiveAp) {
			return;
		}
		boolean pending = false;
		int pendingIndex = 0;
		String desc = null;
		if (!match) {
			pending = true;
			pendingIndex = ConfigurationResources.CONFIGURATION_AUDIT_MISMATCH;
		}
		synchronized (this) {
			if (ConfigurationType.Configuration == type) {
				QueryUtil
						.updateBos(
								HiveAp.class,
								"pending = :s1, pendingIndex = :s2, pendingMsg = :s3, lastAuditTime = :s4",
								"id = :s5",
								new Object[] { pending, pendingIndex, desc,
										auditTime.getTime(),
										auditHiveAp.getId() });
			} else {
				QueryUtil
						.updateBos(
								HiveAp.class,
								"pending_user = :s1, pendingIndex_user = :s2, pendingMsg_user = :s3, lastAuditTime = :s4",
								"id = :s5",
								new Object[] { pending, pendingIndex, desc,
										auditTime.getTime(),
										auditHiveAp.getId() });
			}
		}
	}

	@Override
	public void updateConfigurationIndicationForReboot(HiveAp rebootAp)
			throws Exception {
		if (null == rebootAp) {
			return;
		}
		synchronized (this) {
			HiveAp hiveAp = QueryUtil
					.findBoById(HiveAp.class, rebootAp.getId());
			if (null != hiveAp) {
				boolean needUpdate = false;
				// Only if the pending index is not effective, should update
				if (hiveAp.isPending()
						&& hiveAp.getPendingIndex() == ConfigurationResources.CONFIGURATION_INEFFICACIOUS) {
					needUpdate = true;
					hiveAp.setPending(false);
					hiveAp.setPendingIndex(0);
					hiveAp.setPendingMsg(null);
				}
				if (hiveAp.isPending_user()
						&& hiveAp.getPendingIndex_user() == ConfigurationResources.CONFIGURATION_INEFFICACIOUS) {
					needUpdate = true;
					hiveAp.setPending_user(false);
					hiveAp.setPendingIndex_user(0);
					hiveAp.setPendingMsg_user(null);
				}
				if (needUpdate) {
					QueryUtil.updateBo(hiveAp);
				}
				// try to update result entries when reboot
				QueryUtil.updateBos(HiveApUpdateResult.class,
						"actionType = :s1", "nodeId =:s2 and actionType =:s3",
						new Object[] { (short) -1, hiveAp.getMacAddress(),
								UpdateParameters.ACTION_REBOOT }, hiveAp
								.getOwner().getId());
			}
		}
	}

	/*-
	@Override
	public void updateConfigurationIndication(String macAddress, int reportedConfigVer) throws Exception {
		synchronized (this) {
			List<?> configVers = QueryUtil.executeQuery("select configVer from " + HiveAp.class.getSimpleName(), null, new FilterParams("macAddress", macAddress));

			if (configVers.isEmpty()) {
				log.error("updateConfigurationIndication", "The HiveAP:"
						+ macAddress + " doesn't exist in database.");
				return;
			}

			int recordedConfigVer = (Integer) configVers.get(0);
			log.info("updateConfigurationIndication", "HiveAP:" + macAddress
					+ " config version:" + recordedConfigVer
					+ ", Event config version:" + reportedConfigVer);
			if (reportedConfigVer != recordedConfigVer) {
				boolean pending = true;
				int pendingIndex = ConfigurationResources.CONFIG_VERSION_MISMATCH;
				String desc = null;
				QueryUtil.bulkUpdateBos(HiveAp.class,
						"pending = :s1, pendingIndex = :s2, pendingMsg = :s3",
						"macAddress = :s4",
						new Object[] { pending, pendingIndex, desc, macAddress });
			}
		}
	}*/

	@Override
	public void setAutoProvisioningConfig(HiveAp hiveAp,
			HiveApAutoProvision autoProvision) {
		// For 11n AP only, exception for HiveAP120
		if (hiveAp.isEth1Available()) {
			hiveAp.setEth1(autoProvision.getEth1());
			hiveAp.setAgg0(autoProvision.getAgg0());
			hiveAp.setRed0(autoProvision.getRed0());
		}

		// Reorganize HiveAP location with provisioned location and topology
		// map.
		if (autoProvision.isRewriteMap()) {
			String reorganizedLocation = reorganizeLocation(
					hiveAp.getLocation(), hiveAp.getOwner(),
					autoProvision.getMapContainer());
			hiveAp.setLocation(reorganizedLocation);
			if (autoProvision.getMapContainer() != null) {
				hiveAp.setMapContainer(autoProvision.getMapContainer());
			}
		}

		short originalDeviceType = hiveAp.getDeviceType();
		hiveAp.setDeviceType(autoProvision.getDeviceType());
		// hiveAp.setDeviceInterfaces(autoProvision.getDeviceInterfaces());
		hiveAp.setWifi0RadioProfile(autoProvision.getWifi0RadioProfile());
		hiveAp.setWifi1RadioProfile(autoProvision.getWifi1RadioProfile());
		hiveAp.setConfigTemplate(autoProvision.getConfigTemplate());
		// hiveAp.setLldpCdp(autoProvision.getLLDPCDPProfile());
		hiveAp.setCapwapIpBind(autoProvision.getCapwapIpAddress());
		hiveAp.setCapwapBackupIpBind(autoProvision.getCapwapBackupIpAddress());
		hiveAp.setEth0(autoProvision.getEth0());
		hiveAp.setWifi0(autoProvision.getWifi0());
		hiveAp.setWifi1(autoProvision.getWifi1());
		hiveAp.setCfgAdminUser(autoProvision.getCfgAdminUser());
		hiveAp.setCfgPassword(autoProvision.getCfgPassword());
		hiveAp.setCfgReadOnlyUser(autoProvision.getCfgReadOnlyUser());
		hiveAp.setCfgReadOnlyPassword(autoProvision.getCfgReadOnlyPassword());
		hiveAp.setIncludeTopologyInfo(autoProvision.isIncludeTopologyInfo());

		if (autoProvision.getPassPhrase() != null
				&& !autoProvision.getPassPhrase().trim().isEmpty()) {
			hiveAp.setPassPhrase(autoProvision.getPassPhrase());
		}

		hiveAp.setProvision(HiveAp.HIVEAP_PROVISION);
		hiveAp.setManageStatus(HiveAp.STATUS_MANAGED);
		hiveAp.setAutoProvisioningConfig(autoProvision);

		// Set this eth config base on radio settings
		HiveAp.setHiveApEthConfigType(hiveAp);

		// Set this radio config base on radio settings
		HiveAp.setHiveApRadioConfigType(hiveAp);

		// Set radio mode
		HiveAp.setHiveApRadioModes(hiveAp);

		if (autoProvision.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER) {
			hiveAp.setVpnMark(HiveAp.VPN_MARK_CLIENT);
			hiveAp.setDhcp(false);
		} else if (autoProvision.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY
				|| autoProvision.getDeviceType() == HiveAp.Device_TYPE_VPN_BR) {
			hiveAp.setVpnMark(HiveAp.VPN_MARK_SERVER);
			hiveAp.setDhcp(false);
		}

		//fix bug 28541, if device change from BR to AP, auto clear static IP address and enable DHCP.
		if((originalDeviceType == HiveAp.Device_TYPE_BRANCH_ROUTER
				|| originalDeviceType == HiveAp.Device_TYPE_VPN_BR
				|| originalDeviceType == HiveAp.Device_TYPE_VPN_GATEWAY) &&
				autoProvision.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
			hiveAp.setDhcp(true);
			hiveAp.setCfgIpAddress(null);
			hiveAp.setCfgNetmask(null);
		}

		// set TAG
		hiveAp.setClassificationTag1(autoProvision.getClassificationTag1());
		hiveAp.setClassificationTag2(autoProvision.getClassificationTag2());
		hiveAp.setClassificationTag3(autoProvision.getClassificationTag3());

		hiveAp.setUsbConnectionModel(autoProvision.getUsbConnectionModel());

		// set device interface
		//fix bug 25299 switch not exists eth0 interface.
		if (autoProvision.getDeviceType() != HiveAp.Device_TYPE_HIVEAP
				&& !hiveAp.getDeviceInfo().isSptEthernetMore_24()) {
			short interfaceType;
			for (AutoProvisionDeviceInterface autoInterface : autoProvision
					.getDeviceInterfaces()) {
				interfaceType = autoInterface.getInterfacePort();
				DeviceInterface hiveApInterface = new DeviceInterface();
				if (interfaceType == AhInterface.DEVICE_IF_TYPE_ETH0) {
					hiveApInterface
							.setInterfaceName(MgrUtil
									.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth0"));
					hiveApInterface
							.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH0);
				} else if (interfaceType == AhInterface.DEVICE_IF_TYPE_ETH1) {
					hiveApInterface
							.setInterfaceName(MgrUtil
									.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth1"));
					hiveApInterface
							.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH1);
					hiveApInterface
							.setPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH1);
				} else if (interfaceType == AhInterface.DEVICE_IF_TYPE_ETH2) {
					hiveApInterface
							.setInterfaceName(MgrUtil
									.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth2"));
					hiveApInterface
							.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH2);
					hiveApInterface
							.setPsePriority(AhInterface.ETH_PSE_PRIORITY_ETH2);
				} else if (interfaceType == AhInterface.DEVICE_IF_TYPE_ETH3) {
					hiveApInterface
							.setInterfaceName(MgrUtil
									.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth3"));
					hiveApInterface
							.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH3);
				} else if (interfaceType == AhInterface.DEVICE_IF_TYPE_ETH4) {
					hiveApInterface
							.setInterfaceName(MgrUtil
									.getUserMessage("hiveAp.autoProvisioning.br100.if.port.eth4"));
					hiveApInterface
							.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_ETH4);
				} else if (interfaceType == AhInterface.DEVICE_IF_TYPE_USB) {
					hiveApInterface
							.setInterfaceName(MgrUtil
									.getUserMessage("hiveAp.autoProvisioning.br100.if.port.usb"));
					hiveApInterface
							.setDeviceIfType(AhInterface.DEVICE_IF_TYPE_USB);
				}

				hiveApInterface.setAdminState(autoInterface.getAdminState());
				hiveApInterface.setSpeed(autoInterface.getInterfaceSpeed());
				hiveApInterface.setDuplex(autoInterface
						.getInterfaceTransmissionType());
				hiveApInterface.setPseEnabled(autoInterface.isPseEnabled());
				hiveApInterface.setPsePriority(autoInterface.getPsePriority());
				hiveApInterface.setPseState(autoInterface.getPseState());
//				hiveApInterface.setRole(autoInterface.getInterfaceRole());
				hiveApInterface.setEnableNat(autoInterface.isEnableNat());
				//set wan port priority
				switch(autoInterface.getInterfaceRole()){
				case AhInterface.ETHX_DEVICE_INTERFACE_ROLE_WAN_PRIMARY:
					hiveApInterface.setWanOrder(1);
					break;
				case AhInterface.ETHX_DEVICE_INTERFACE_ROLE_WAN_BACKUP1:
					hiveApInterface.setWanOrder(2);
					break;
				default:
					hiveApInterface.setWanOrder(0);
					break;
				}

				if (interfaceType == AhInterface.DEVICE_IF_TYPE_ETH0) {
					hiveAp.setEth0Interface(hiveApInterface);
				} else if (interfaceType == AhInterface.DEVICE_IF_TYPE_ETH1) {
					hiveAp.setEth1Interface(hiveApInterface);
				} else if (interfaceType == AhInterface.DEVICE_IF_TYPE_ETH2) {
					hiveAp.setEth2Interface(hiveApInterface);
				} else if (interfaceType == AhInterface.DEVICE_IF_TYPE_ETH3) {
					hiveAp.setEth3Interface(hiveApInterface);
				} else if (interfaceType == AhInterface.DEVICE_IF_TYPE_ETH4) {
					hiveAp.setEth4Interface(hiveApInterface);
				} else if (interfaceType == AhInterface.DEVICE_IF_TYPE_USB) {
					hiveAp.setUSBInterface(hiveApInterface);
				}
			}
		}
	}

	@Override
	public void sendDiscoveryEvent(AhDiscoveryEvent discoveryEvent) {
		if (discoveryEvent == null) {
			return;
		}

		AhAppContainer.getBeEventListener().eventGenerated(discoveryEvent);
		log.info("sendDiscoveryEvent", "Sent a HiveAP discovery event - "
				+ discoveryEvent);
	}

	@Override
	public void sendDiscoveryEvent(Collection<AhDiscoveryEvent> discoveryEvents) {
		if (discoveryEvents == null) {
			return;
		}

		for (AhDiscoveryEvent discoveryEvent : discoveryEvents) {
			sendDiscoveryEvent(discoveryEvent);
		}
	}

	@Override
	public void updateDiscoveredPreviewConfigHiveAps(HiveAp hiveAp, BeAPConnectEvent event, String oldMacAddress) {

		try {
			synchronized (this) {
				if (hiveAp == null) {
					return;
				}

				// Holder for the HiveAPs to be updated.
				Collection<HiveAp> updatedHiveAps = new ArrayList<>(1);

				// Holder for the HiveAPs to be disconnected.
				Collection<HiveAp> rejectedHiveAps = new ArrayList<>(1);

				HmDomain owner = hiveAp.getOwner();

				if (owner.isRestoring()) {
					// Because the restoration is being executed on target
					// VHM to which newly discovered HiveAPs belonging are
					// rejected.
					AhAppContainer.getBeFaultModule().getHiveApAlarmMgmt()
							.vhmRestore(hiveAp, false);
					rejectedHiveAps.add(hiveAp);
				} else {
					// Save part of old HiveAP information to update HiveAP
					// cache later.
					hiveAp.saveOldHiveApInfo();

					// Update HiveAP.
					updateHiveAp(hiveAp, event);

					updatedHiveAps.add(hiveAp);
				}


				// update the preconfiguration device
				// update the cache and increate new ap count.
				if (!updatedHiveAps.isEmpty()) {
					bulkUpdateHiveAps(updatedHiveAps);
					if (oldMacAddress!=null) {
						HiveAp oneAp = new HiveAp();
						oneAp.setOwner(hiveAp.getOwner());
						oneAp.setMacAddress(oldMacAddress);
						CacheMgmt.getInstance().removeSimpleHiveAp(oneAp);
					}
				}

				// Disconnect the HiveAPs whose VHM(s) is/are being restored.
				if (!rejectedHiveAps.isEmpty()) {
					BeTopoModuleUtil.sendBeDeleteAPConnectRequest(rejectedHiveAps,
						false);
				}
			}
		} catch (Exception e) {
			log.error("updateDiscoveredHiveAps", "HiveAp lookup failed.", e);

			// Disconnect with the HiveAPs which couldn't be successfully
			// updated.
			BeTopoModuleUtil.sendBeDeleteAPConnectRequest(hiveAp,
					false);
		}

	}
	@Override
	public void updateDiscoveredHiveAps(Map<String, BeAPConnectEvent> reqsHolder) {
		Set<String> discoveredApNodeIds = new HashSet<>(
				reqsHolder.keySet());

		try {
			synchronized (this) {
				/*-
				Map<String, BeAPConnectEvent> stagedReqsHolder = new HashMap<String, BeAPConnectEvent>();

				for (String key : reqsHolder.keySet()) {
					stagedReqsHolder.put(key, reqsHolder.get(key));
				}*/

				List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class,
						null, new FilterParams("macAddress",
								discoveredApNodeIds), null, this);
				int size = hiveAps.size();

				if (size == 0) {
					return;
				}

				// Holder for the HiveAPs to be updated.
				Collection<HiveAp> updatedHiveAps = new ArrayList<>(size);

				Collection<String> updatedSerials = new ArrayList<>(size);

				// Holder for the HiveAPs to be disconnected.
				Collection<HiveAp> rejectedHiveAps = new ArrayList<>(size);

				/*-
				// A map of unaccepted HiveAP decrements(negative) for domain,
				// keyed by domain id.
				Map<Long, Integer> unacceptedHiveApDecrements = new HashMap<Long, Integer>(size);

				// A map of HiveApAutoProvisions, keyed by HiveAP node id.
				Map<String, HiveApAutoProvision> apNodeIdAndAutoProvMap = getHiveApAutoProvMap(hiveAps);

				Map<NetworkRange, HiveApAutoProvision> apNetAndAutoProvMap = getHiveApAutoProvMapByIP(hiveAps);*/

				for (HiveAp hiveAp : hiveAps) {
					HmDomain owner = hiveAp.getOwner();
					String hiveApMac = hiveAp.getMacAddress();
					BeAPConnectEvent event = reqsHolder.remove(hiveApMac);

					if (owner.isRestoring()) {
						// Because the restoration is being executed on target
						// VHM to which newly discovered HiveAPs belonging are
						// rejected.
						AhAppContainer.getBeFaultModule().getHiveApAlarmMgmt()
								.vhmRestore(hiveAp, false);
						rejectedHiveAps.add(hiveAp);
					} else {
						// Save part of old HiveAP information to update HiveAP
						// cache later.
						hiveAp.saveOldHiveApInfo();

						/*
						short discoveredModel = AhConstantUtil
								.getHiveApModelByProductName(event
										.getProductName());

						HiveApAutoProvision autoProvision = apNodeIdAndAutoProvMap
								.get(hiveApMac);
						if (autoProvision == null) {
							NetworkRange hiveApIpRange = new NetworkRange(
									hiveAp.getIpAddress(), discoveredModel);
							for (NetworkRange range : apNetAndAutoProvMap
									.keySet()) {
								if (range.inRange(hiveApIpRange)) {
									autoProvision = apNetAndAutoProvMap
											.get(range);
									break;
								}
							}
						}

						if (autoProvision != null) {
							short configuredModel = autoProvision
									.getModelType();

							if (discoveredModel != configuredModel) {
								// Model mismatch.
								String discoveredModelName = HiveAp
										.getModelEnumString(discoveredModel);
								String configuredModelName = HiveAp
										.getModelEnumString(configuredModel);
								AhAppContainer
										.getBeFaultModule()
										.getHiveApAlarmMgmt()
										.mismatchHiveApModel(hiveAp,
												discoveredModelName,
												configuredModelName);
							} else {
								HmDomain hmDomain = autoProvision.getOwner();

								if (hmDomain.isManagedApNumFull()) {
									// Reach the maximum number of acceptable
									// APs.
									AhAppContainer.getBeFaultModule()
											.getHiveApAlarmMgmt()
											.reachMaxApNum(hiveAp);
								} else {
									if (owner.isDisabled()) {
										// HiveAP auto-Provisioning is suspended
										// in the disabled mode of VHM.
										AhAppContainer
												.getBeFaultModule()
												.getHiveApAlarmMgmt()
												.vhmDisable(hiveAp,
														owner.getDomainName(),
														false, false);
									} else {
										// Location.
										if (hiveAp.getLocation() == null
												|| hiveAp.getLocation().trim()
														.isEmpty()) {
											hiveAp.setLocation(event
													.getLocation());
										}

										setAutoProvisioningConfig(hiveAp,
												autoProvision);

										// The number of managed and unaccepted
										// HiveAPs are increased and decreased
										// separately.
										hmDomain.setManagedApNum(hmDomain
												.getManagedApNum() + 1);
										merge(unacceptedHiveApDecrements,
												hmDomain.getId(), -1);
									}
								}
							}
						}*/

						// Update HiveAP.
						updateHiveAp(hiveAp, event);

						updatedHiveAps.add(hiveAp);
						if (!hiveAp.isSimulated()) {
							updatedSerials.add(hiveAp.getSerialNumber());
						}
					}
				}

				// Update discovered HiveAPs.
			//	bulkUpdateHiveAps(updatedHiveAps, unacceptedHiveApDecrements);

				bulkUpdateHiveAps(updatedHiveAps);

				removeDupSerialNumbers(updatedSerials);

				// Disconnect the HiveAPs whose VHM(s) is/are being restored.
				BeTopoModuleUtil.sendBeDeleteAPConnectRequest(rejectedHiveAps,
						false);
			}
		} catch (Exception e) {
			log.error("updateDiscoveredHiveAps", "HiveAp lookup failed.", e);

			// Disconnect with the HiveAPs which couldn't be successfully
			// updated.
			BeTopoModuleUtil.sendBeDeleteAPConnectRequest(discoveredApNodeIds,
					false);

			// Clear the request holder to protect the requests inside from
			// being accessed by any other processes subsequently.
			reqsHolder.clear();
		}
	}
	private void removeDupSerialNumbers(Collection<String> serials) {
		if (NmsUtil.isHostedHMApplication() && !serials.isEmpty()) {
			List<HiveAp> needRemovePreAp = QueryUtil.executeQuery(HiveAp.class,null,
					new FilterParams("serialNumber in (:s1) and manageStatus=:s2",
							new Object[] {serials, HiveAp.STATUS_PRECONFIG}));
			if (!needRemovePreAp.isEmpty()) {
				List<Long> removeIds = new ArrayList<Long>();
				for(HiveAp oneAp: needRemovePreAp) {
					removeIds.add(oneAp.getId());
				}
				try {
					Collection<Long> successLst =  BoMgmt.getMapMgmt().removeHiveApsReturnRemovedIds(removeIds, false, false, null, false);

					DeviceUtils diu = DeviceImpUtils.getInstance();
					for(HiveAp oneAp : needRemovePreAp) {
						if (successLst.contains(oneAp.getId())) {
						diu.generateAuditLog(HmAuditLog.STATUS_SUCCESS,
							MgrUtil.getUserMessage("glasgow_05.hm.audit.log.remove.ap.from.hiveaplist.serialNum.notmatch",
									new String[]{oneAp.getHostName()
									, oneAp.getSerialNumber()
									, HiveApAction.getHiveApListName(oneAp.getManageStatus())}), oneAp.getOwner());
						}
					}

				} catch (Exception e) {
					log.error(e);
				}
			}
		}
	}


	@Override
	public HiveAp updateHiveApSeverity(String hiveApMac, Long domainId,
			short newSeverity) throws Exception {
		synchronized (this) {
			int r = QueryUtil.updateBo(HiveAp.class, "severity = :s1",
					new FilterParams("macAddress = :s2", new Object[] {
							newSeverity, hiveApMac }));
			if (r > 0) {
				return QueryUtil.findBoByAttribute(HiveAp.class, "macAddress",
						hiveApMac, domainId);
			}
			return null;
		}
	}

	@Override
	public HiveAp updateAdminDtlsInfo(HiveAp hiveAp, String cfgAdmin,
			String cfgPsd) throws Exception {
		// Update admin user;
		String runAdmin = hiveAp.getAdminUser();
		String runPsd = hiveAp.getAdminPassword();
		String cfgRdAdmin = hiveAp.getCfgReadOnlyUser();
		String cfgRdPsd = hiveAp.getCfgReadOnlyPassword();
		String runRdAdmin = hiveAp.getReadOnlyUser();
		String runRdPsd = hiveAp.getReadOnlyPassword();
		// CAPWAP DTLS
		int cfgKeyId = hiveAp.getKeyId();
		int runKeyId = hiveAp.getCurrentKeyId();
		String cfgPassPhrase = hiveAp.getPassPhrase();
		String runPassPhrase = hiveAp.getCurrentPassPhrase();
		boolean adminChangeFlag = (null != cfgAdmin
				&& !("".equals(cfgAdmin.trim())) && !(cfgAdmin.equals(runAdmin)))
				|| (null != cfgPsd && !("".equals(cfgPsd.trim())) && !(cfgPsd
						.equals(runPsd)));
		boolean cfgAdminChangeFlag = (null != cfgRdAdmin
				&& !("".equals(cfgRdAdmin.trim())) && !(cfgRdAdmin
					.equals(runRdAdmin)))
				|| (null != cfgRdPsd && !("".equals(cfgRdPsd.trim())) && !(cfgRdPsd
						.equals(runRdPsd)));
		boolean dtlsChangeFlag = (cfgKeyId != runKeyId)
				|| (null != cfgPassPhrase && !("".equals(cfgPassPhrase.trim())) && !(cfgPassPhrase
						.equals(runPassPhrase)));
		HiveAp ap = null;

		if (adminChangeFlag || cfgAdminChangeFlag || dtlsChangeFlag) {
			synchronized (this) {
				ap = QueryUtil.findBoById(HiveAp.class, hiveAp.getId());

				if (ap != null) {
					if (adminChangeFlag) {
						ap.setAdminUser(cfgAdmin);
						ap.setAdminPassword(cfgPsd);
					}

					if (cfgAdminChangeFlag) {
						ap.setReadOnlyUser(cfgRdAdmin);
						ap.setReadOnlyPassword(cfgRdPsd);
					}

					if (dtlsChangeFlag) {
						ap.setCurrentPassPhrase(cfgPassPhrase);
						ap.setCurrentKeyId(cfgKeyId);
					}

					ap = QueryUtil.updateBo(ap);
				}
			}
		}

		return ap;
	}

	@Override
	public void updateLldpCdpInfo(String hiveApMac, String eth0DeviceId,
			String eth0PortId, String eth0SystemId, String eth1DeviceId,
			String eth1PortId, String eth1SystemId) throws Exception {
		synchronized (this) {
			QueryUtil
					.updateBos(
							HiveAp.class,
							"eth0DeviceId = :s1, eth0PortId = :s2, eth1DeviceId = :s3, eth1PortId = :s4, eth0SystemId = :s5, eth1SystemId = :s6",
							"macAddress = :s7", new Object[] { eth0DeviceId,
									eth0PortId, eth1DeviceId, eth1PortId,
									eth0SystemId, eth1SystemId, hiveApMac });
		}
	}

	/*
	private Map<String, HiveApAutoProvision> getHiveApAutoProvMap(
			List<HiveAp> hiveAps) {
		int size = hiveAps.size();

		// A map of HiveApAutoProvisions, keyed by HiveAP node id.
		Map<String, HiveApAutoProvision> apNodeIdAndAutoProvMap = new HashMap<String, HiveApAutoProvision>(
				size);

		// HiveAP node ids.
		Collection<String> hiveApNodeIds = new ArrayList<String>(size);

		// Domain ids.
		Collection<HmDomain> hmDomains = new ArrayList<HmDomain>(size);

		for (HiveAp hiveAp : hiveAps) {
			if (hiveAp.getManageStatus() == HiveAp.STATUS_NEW
					&& hiveAp.getOrigin() == HiveAp.ORIGIN_CREATE) {
				String apNodeId = hiveAp.getMacAddress();
				hiveApNodeIds.add(apNodeId);

				HmDomain hmDomain = hiveAp.getOwner();

				if (!hmDomains.contains(hmDomain)) {
					hmDomains.add(hmDomain);
				}
			}
		}

		if (!hmDomains.isEmpty() && !hiveApNodeIds.isEmpty()) {
			// List<?> configuredAutoProvisions = QueryUtil
			// .executeQuery(
			// "from " + HiveApAutoProvision.class.getSimpleName()
			// + " as bo join bo.macAddresses as joined",
			// null,
			// new FilterParams(
			// "bo.owner in (:s1) and bo.autoProvision = :s2 and bo.accessControled = :s3 and bo.aclType = :s4 and joined in (:s5)",
			// new Object[] { hmDomains, true, true,
			// HiveApAutoProvision.ACL_MANUAL_AP, hiveApNodeIds }), null, this);

			List<?> configuredAutoProvisions = QueryUtil
					.executeQuery(
							"from " + HiveApAutoProvision.class.getSimpleName()
									+ " as bo join bo.macAddresses as joined",
							null,
							new FilterParams(
									"bo.owner in (:s1) and bo.autoProvision = :s2 and bo.accessControled = :s3 and joined in (:s4)",
									new Object[] { hmDomains, true, true,
											hiveApNodeIds }), null, this);
			Map<String, HmDomain> domains = new HashMap<String, HmDomain>(size);

			for (Object obj : configuredAutoProvisions) {
				HiveApAutoProvision autoProvision = (HiveApAutoProvision) obj;
				HmDomain owner = autoProvision.getOwner();
				String domainName = owner.getDomainName();
				HmDomain hmDomain = domains.get(domainName);

				if (hmDomain == null) {
					hmDomain = owner;

					// Compute current managed AP number.
					hmDomain.computeManagedApNum();

					domains.put(domainName, hmDomain);
				} else {
					// Make sure all HiveApAutoProvisions queried have the same
					// HmDomain reference.
					autoProvision.setOwner(hmDomain);
				}

				for (String hiveApNodeId : autoProvision.getMacAddresses()) {
					apNodeIdAndAutoProvMap.put(hiveApNodeId, autoProvision);
				}
			}
		}

		return apNodeIdAndAutoProvMap;
	}

	private Map<NetworkRange, HiveApAutoProvision> getHiveApAutoProvMapByIP(
			List<HiveAp> hiveAps) {
		// A map of HiveApAutoProvisions, keyed by HiveAP node id.
		Map<NetworkRange, HiveApAutoProvision> apIPAndAutoProvMap = new HashMap<NetworkRange, HiveApAutoProvision>();

		// HiveAP node ids.
		Collection<String> hiveApIPs = new ArrayList<String>();

		// Domain ids.
		Collection<HmDomain> hmDomains = new ArrayList<HmDomain>();

		for (HiveAp hiveAp : hiveAps) {
			if (hiveAp.getManageStatus() == HiveAp.STATUS_NEW
					&& hiveAp.getOrigin() == HiveAp.ORIGIN_CREATE) {
				String apIp = hiveAp.getIpAddress();
				hiveApIPs.add(apIp);

				HmDomain hmDomain = hiveAp.getOwner();

				if (!hmDomains.contains(hmDomain)) {
					hmDomains.add(hmDomain);
				}
			}
		}

		if (!hmDomains.isEmpty() && !hiveApIPs.isEmpty()) {
			// List<?> configuredAutoProvisions = QueryUtil
			// .executeQuery(
			// "from " + HiveApAutoProvision.class.getSimpleName()
			// + " as bo",
			// null,
			// new FilterParams(
			// "bo.owner in (:s1) and bo.autoProvision = :s2 and bo.accessControled = :s3 and bo.aclType = :s4",
			// new Object[] { hmDomains, true, true,
			// HiveApAutoProvision.ACL_MANUAL_AP}), null, this);
			List<?> configuredAutoProvisions = QueryUtil
					.executeQuery(
							"from " + HiveApAutoProvision.class.getSimpleName()
									+ " as bo",
							null,
							new FilterParams(
									"bo.owner in (:s1) and bo.autoProvision = :s2 and bo.accessControled = :s3",
									new Object[] { hmDomains, true, true }),
							null, this);
			Map<String, HmDomain> domains = new HashMap<String, HmDomain>();

			for (Object obj : configuredAutoProvisions) {
				HiveApAutoProvision autoProvision = (HiveApAutoProvision) obj;
				if (autoProvision.getIpSubNetworks() == null
						|| autoProvision.getIpSubNetworks().isEmpty()) {
					continue;
				}
				HmDomain owner = autoProvision.getOwner();
				String domainName = owner.getDomainName();
				HmDomain hmDomain = domains.get(domainName);

				if (hmDomain == null) {
					hmDomain = owner;

					// Compute current managed AP number.
					hmDomain.computeManagedApNum();

					domains.put(domainName, hmDomain);
				} else {
					// Make sure all HiveApAutoProvisions queried have the same
					// HmDomain reference.
					autoProvision.setOwner(hmDomain);
				}

				for (String hiveApIpNet : autoProvision.getIpSubNetworks()) {
					apIPAndAutoProvMap.put(new NetworkRange(hiveApIpNet,
							autoProvision.getModelType()), autoProvision);
				}
			}
		}

		return apIPAndAutoProvMap;
	}

	private void merge(Map<Long, Integer> unacceptedHiveApDecrements,
					   Long domainId, int num) {
		if (unacceptedHiveApDecrements.containsKey(domainId)) {
			unacceptedHiveApDecrements.put(domainId,
					unacceptedHiveApDecrements.get(domainId) + num);
		} else {
			unacceptedHiveApDecrements.put(domainId, num);
		}
	}*/

	private String reorganizeLocation(String originalLocation, HmDomain domain,
			MapContainerNode provisionedMap) {
		String reorganizedLocation = (String) BeTopoModuleUtil
				.separateLocationAndMap(originalLocation, domain)[0];

		if (reorganizedLocation == null
				|| reorganizedLocation.trim().equals("")) {
			reorganizedLocation = HiveAp.DEFAULT_LOCATION;
		}

		/*-
		if (provisionedMap != null) {
			String mapName = provisionedMap.getMapName();

			if (mapName != null) {
				mapName = mapName.trim();

				if (!mapName.equals("")) {
					reorganizedLocation += "@" + mapName;
				}
			}
		}*/

		return reorganizedLocation;
	}

	private void updateHiveAp(HiveAp hiveAp, BeAPConnectEvent event) {
		long currTime = System.currentTimeMillis();

		if (event.isConnectState()) {
			boolean isDtlsEnable = event.getDtlsState() == BeCommunicationConstant.DTLSSTATE_USEDTLS;
			short hiveApType = event.getAPType() == BeCommunicationConstant.AP_TYPE_PORTAL ? HiveAp.HIVEAP_TYPE_PORTAL
					: HiveAp.HIVEAP_TYPE_MP;
			String productName = event.getProductName();
			short hiveApModel = AhConstantUtil
					.getHiveApModelByProductName(productName);

			String gateway = event.getGateway();

			if ("0.0.0.0".equals(gateway)) {
				gateway = null;
			}

			String disVer = event.getDisplayVersion();

			if (disVer == null || disVer.isEmpty()) {
				disVer = event.getSoftVersion();
			}

			String passPhrase = hiveAp.getPassPhrase();

			if (passPhrase == null || passPhrase.trim().equals("")) {
				hiveAp.setPassPhrase(NmsUtil.generatePassphrase());
			}

			long discoveryTime = hiveAp.getDiscoveryTime();

			if (discoveryTime == 0) {
				hiveAp.setDiscoveryTime(currTime);
			}

			if (event.getUpTime() > 0) {
				hiveAp.setUpTime(currTime - event.getUpTime() * 1000);
			} else {
				hiveAp.setUpTime(0);
			}

			hiveAp.setSerialNumber(event.getApSerialNum());
			hiveAp.setIpAddress(event.getIpAddr());
			hiveAp.setGateway(gateway);
			hiveAp.setNetmask(event.getNetmask());
			hiveAp.setProductName(productName);

			// device reconnect to HM no need set device type, only fist time
			// create device need set.
			// short deviceType =
			// AhConstantUtil.getDeviceTypeByProductName(productName);
			// if (!productName.equals(hiveAp.getProductName())) {
			// hiveAp.setDeviceType(deviceType);
			// } else if (deviceType != hiveAp.getDeviceType()) {
			// if (HiveAp.is330HiveAP(hiveAp.getHiveApModel()) ||
			// HiveAp.is350HiveAP(hiveAp.getHiveApModel())) {
			// if (HiveAp.Device_TYPE_VPN_GATEWAY == hiveAp.getDeviceType()) {
			// hiveAp.setDeviceType(deviceType);
			// }
			// } else {
			// hiveAp.setDeviceType(deviceType);
			// }
			// }

			hiveAp.setHiveApModel(hiveApModel);
			if (hiveAp.getManageStatus()==HiveAp.STATUS_PRECONFIG) {
				hiveAp.setManageStatus(HiveAp.STATUS_NEW);
			}
			hiveAp.setHiveApType(hiveApType);
			hiveAp.setSoftVer(event.getSoftVersion());
			hiveAp.setDisplayVer(disVer);
			hiveAp.setConfigVer(event.getConfigVersion());
			hiveAp.setCountryCode(event.getCountryCode());
			hiveAp.setRegionCode(event.getRegionCode());
			hiveAp.setCapwapLinkIp(event.getHivemanagerIP());
			hiveAp.setCapwapClientIp(event.getCapwapClientIP());
			hiveAp.setCurrentDtlsEnable(isDtlsEnable);
			hiveAp.setTimeZoneOffset((byte) (TimeZone.getTimeZone(
					event.getMessageTimeZone()).getRawOffset() / 3600000));
			hiveAp.setSimulated(event.isSimulate());
			hiveAp.setSimulateCode(event.getSimulateCode());
			hiveAp.setSimulateClientInfo(event.getSimulateClientInfo());
			hiveAp.setRunningHive(event.getHiveName());
			hiveAp.setProxyName(event.getProxyName());
			hiveAp.setProxyPort(event.getProxyPort());
			hiveAp.setProxyUsername(event.getProxyUserName());
			hiveAp.setProxyPassword(event.getProxyPassword());
			hiveAp.setTransferProtocol(event.getTransferMode());
			hiveAp.setTransferPort(event.getConnectPort());
			hiveAp.setPppoeEnableCurrent(event.isEnablePppoe());
			hiveAp.setSignatureVer(event.getL7SignatureFileVersion());
			hiveAp.setSwitchChipVersion(event.getSwitchChipVersion());
			hiveAp.setHardwareRevision(event.getHardwareRevision());
			if (hiveAp.isBranchRouter()) {
				hiveAp.setDhcp(false);
				hiveAp.setCfgIpAddress(event.getIpAddr());
				hiveAp.setCfgNetmask(event.getNetmask());
				hiveAp.setCfgGateway(gateway);
			}

			if (hiveAp.isConnected()) {
				long lastConnectTime = hiveAp.getConnChangedTime();
				long connectDurationTime = currTime - lastConnectTime;

				if (connectDurationTime >= 0) {
					hiveAp.setTotalConnectTime(hiveAp.getTotalConnectTime()
							+ connectDurationTime);
				} else {
					log.warn("updateHiveAp", "The last connect time '"
							+ lastConnectTime
							+ "' is larger than the new connect time '"
							+ currTime
							+ "', totalConnectTime update was ignored!");
				}
			} else {
				hiveAp.setConnected(true);
			}

			hiveAp.setConnChangedTime(currTime);
			hiveAp.setTotalConnectTimes(hiveAp.getTotalConnectTimes() + 1);

			//add for Mission-UX update, when devcie reconnect get reboot result successful, clierror, config rollback, image rollback.
			short rebootResult;
			if(event.getCliError() > 0){
				rebootResult = AhDeviceRebootResultEvent.RESULT_TYPE_CLI_ERROR;
			}else if(event.getReconnectReason() == BeAPConnectEvent.CLIENT_CONFIG_ROLLBACK){
				rebootResult = AhDeviceRebootResultEvent.RESULT_TYPE_CONFIG_ROLLBACK;
			}else if(event.getReconnectReason() == BeAPConnectEvent.CLIENT_IMAGE_ROLLBACK){
				rebootResult = AhDeviceRebootResultEvent.RESULT_TYPE_IMAGE_ROLLBACK;
			}else{
				rebootResult = AhDeviceRebootResultEvent.RESULT_TYPE_SUCCESSFUL;
			}
			hiveAp.setRebootResult(rebootResult);
		} else {
			if (hiveAp.isConnected()) {
				long lastConnectTime = hiveAp.getConnChangedTime();
				long connectDurationTime = currTime - lastConnectTime;

				if (connectDurationTime >= 0) {
					hiveAp.setTotalConnectTime(hiveAp.getTotalConnectTime()
							+ connectDurationTime);
				} else {
					log.warn("updateHiveAp", "The last connect time '"
							+ lastConnectTime
							+ "' is larger than the new connect time '"
							+ currTime
							+ "', totalConnectTime update was ignored!");
				}

				hiveAp.setConnected(false);
				hiveAp.setUpTime(0);
				hiveAp.setDisconnChangedTime(currTime);
			}
		}

		hiveAp.setCapwapConnectEvent(event);
	}

	/*-
	private void bulkUpdateHiveAps(Collection<HiveAp> updatedHiveAps,
			Map<Long, Integer> unacceptedHiveApDecrements) {
		try {
			QueryUtil.bulkUpdateBos(updatedHiveAps);

			// Update relevant caches.
			updateCaches(updatedHiveAps, unacceptedHiveApDecrements);

			// Notify other modules of these discovered HiveAPs.
			notify(updatedHiveAps);
		} catch (Exception e) {
			log.error("bulkUpdateHiveAps", "Bulk of update for HiveAp(s) error.", e);

			// Disconnect with the HiveAPs which couldn't be successfully
			// updated.
			BeTopoModuleUtil
					.sendBeDeleteAPConnectRequest(updatedHiveAps, false);
		}
	}

	private void updateCaches(Collection<HiveAp> hiveAps,
			Map<Long, Integer> unacceptedHiveApDecrements) {
		// Update HiveAP cache.
		CacheMgmt.getInstance().bulkUpdateHiveAps(hiveAps);

		// Update Map cache.
		for (HiveAp hiveAp : hiveAps) {
			Object[] oldHiveApInfo = hiveAp.getOldHiveApInfos();
			BoMgmt.getMapHierarchyCache().hiveApUpdated(hiveAp,
					(Long) oldHiveApInfo[0], (Boolean) oldHiveApInfo[1],
					(Short) oldHiveApInfo[2]);
		}

		// Update Domain cache.
		for (Long domainId : unacceptedHiveApDecrements.keySet()) {
			SystemStatusCache.getInstance().incrementNewHiveApCount(
					unacceptedHiveApDecrements.get(domainId), domainId);
		}
	}*/

	private void bulkUpdateHiveAps(Collection<HiveAp> updatedHiveAps) {
		try {
			QueryUtil.bulkUpdateBos(updatedHiveAps);

			// Update relevant caches.
			updateCaches(updatedHiveAps);

			// Notify other modules of these discovered HiveAPs.
			notify(updatedHiveAps);
		} catch (Exception e) {
			log.error("bulkUpdateHiveAps", "Bulk of update for HiveAp(s) error.", e);

			// Disconnect with the HiveAPs which couldn't be successfully
			// updated.
			BeTopoModuleUtil
					.sendBeDeleteAPConnectRequest(updatedHiveAps, false);
		}
	}

	private void updateCaches(Collection<HiveAp> hiveAps) {
		// Update HiveAP cache.
		CacheMgmt.getInstance().bulkUpdateHiveAps(hiveAps);

		// Update Map cache.
		for (HiveAp hiveAp : hiveAps) {
			Object[] oldHiveApInfo = hiveAp.getOldHiveApInfos();
			BoMgmt.getMapHierarchyCache().hiveApUpdated(hiveAp);

			if (((Short) oldHiveApInfo[2]) == HiveAp.STATUS_PRECONFIG) {
				SystemStatusCache.getInstance().incrementNewHiveApCount(1,
						hiveAp.getOwner().getId());
			}
		}
	}

	private void notify(Collection<HiveAp> discoveredHiveAps) {
		Collection<AhDiscoveryEvent> discoveryEvents = new ArrayList<>(
				discoveredHiveAps.size());

		for (HiveAp discoveredHiveAp : discoveredHiveAps) {
			if (discoveredHiveAp != null) {
				AhDiscoveryEvent discoveryEvent = new AhDiscoveryEvent(
						discoveredHiveAp, HiveApType.UPDATED);

				if (discoveredHiveAp.getAutoProvisioningConfig() != null) {
					// Separate combined topology map and location.
					discoveryEvent.setSeparatingMapLocation(true);
				}

				discoveryEvent.setCapwapConnectEvent(discoveredHiveAp
						.getCapwapConnectEvent());
				discoveryEvents.add(discoveryEvent);
			}
		}
		sendDiscoveryEvent(discoveryEvents);
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HiveApAutoProvision) {
			HiveApAutoProvision autoProvision = (HiveApAutoProvision) bo;

			if (autoProvision.getMacAddresses() != null) {
				autoProvision.getMacAddresses().size();
			}
			if (autoProvision.getIpSubNetworks() != null) {
				autoProvision.getIpSubNetworks().size();
			}
			if (autoProvision.getDeviceInterfaces() != null) {
				autoProvision.getDeviceInterfaces().size();
			}
		} else if (bo instanceof HiveAp) {
			HiveAp hiveAp = (HiveAp) bo;
			if (hiveAp.getDeviceInterfaces() != null) {
				hiveAp.getDeviceInterfaces().values();
			}
		} else if (bo instanceof HiveApUpdateResult) {
			HiveApUpdateResult result = (HiveApUpdateResult) bo;
			if (result.getItems() != null) {
				result.getItems().size();
			}
		}

		return null;
	}

	@Override
	public void boCreated(HiveAp hiveAp) {

	}

	@Override
	public void boRemoved(HiveAp hiveAp) {
		if (null == hiveAp) {
			return;
		}
		short managedStatus = hiveAp.getManageStatus();

		if (HiveAp.STATUS_NEW == managedStatus) {
			// propagate newHiveAp count to System Cache;
			SystemStatusCache.getInstance().decrementNewHiveApCount(
					hiveAp.getOwner().getId());
		} else if (HiveAp.STATUS_MANAGED == managedStatus) {
			BeTopoModuleUtil.clearHiveAPRelatedData(hiveAp);
		}
		// propagate the hiveAp count to map hierarchyCache.
		BoMgmt.getMapHierarchyCache().hiveApRemoved(hiveAp);
		// notify CAPWAP the removed HiveAp;
		BeTopoModuleUtil.sendBeDeleteAPConnectRequest(hiveAp, true);
		// delete configuration script files existed.
		try {
			AhConfigUtil.removeAllHiveApConfigs(hiveAp.getOwner()
					.getDomainName(), hiveAp.getMacAddress());
		} catch (Exception e) {
			log.error("boRemoved",
					"remove config directories and files associated HiveAP:"
							+ hiveAp.getMacAddress() + " error.", e);
		}
		// remove Alarms associate to this HiveAP
		try {
			BoMgmt.getTrapMgmt().removeAlarms(hiveAp);
		} catch (Exception e) {
			log.error("boRemoved",
					"remove alarms associated HiveAP:" + hiveAp.getMacAddress()
							+ " error.", e);
		}
		// remove NetworkDeviceHistory associate to this HiveAP
		try {
			BoMgmt.getTrapMgmt().removeNetworkDeviceHistorys(hiveAp);
		} catch (Exception e) {
			log.error("boRemoved",
					"remove network device history associated HiveAP:" + hiveAp.getMacAddress()
							+ " error.", e);
		}
		// remove statistic data associate to this HiveAP(Only for simulated
		// HiveAP)
		/*- do not do it for performance issue
		if (hiveAp.isSimulated()) {
			try {
				HmBePerformUtil.clearAllStatsData(hiveAp);
			} catch (Exception e) {
				log.error("boRemoved",
						"remove statistic data associated HiveAP:"
								+ hiveAp.getMacAddress() + " error.", e);
			}
		}*/
		// propagate the removed HiveAP to Cache./put this at last;
		CacheMgmt.getInstance().removeSimpleHiveAp(hiveAp);
	}

	@Override
	public void boUpdated(HiveAp hiveAp) {

	}

	/**
	 * Update the report flag if send hiveap first information to license server
	 * success
	 *
	 * @param macs
	 *            -
	 *
	 */
	public void updateHiveApReportFlag(Collection<String> macs)
			throws Exception {
		synchronized (this) {
			QueryUtil.updateBos(HiveAp.class, "discoveryReported = :s1",
					"macAddress in (:s2)", new Object[] { true, macs });
		}
	}

	public static void synchronizeCVGInterfaceState(HiveAp hiveAp) {
		if (hiveAp.getDeviceType() != HiveAp.Device_TYPE_VPN_GATEWAY) {
			return;
		}
		List<Byte> tableIDList = new ArrayList<>();
		tableIDList.add(BeCommunicationConstant.STATTABLE_AHXIF);
		try {
			BeGetStatisticEvent event = BeTopoModuleUtil.getStatisticEvent(
					hiveAp, tableIDList);
			HmBeCommunicationUtil.sendRequest(event);
		} catch (Exception e) {
			log.error("synchronizeInterfaceState", e.getMessage(), e);
		}
	}

	public static class NetworkRange {

		private final String network;

		private short hiveApMode = -1;

		private long ipLong = -1;

		private long rangeStart = -1;

		private long rangeEnd = -1;

		public NetworkRange(String network, short hiveApMode) {
			this.network = network;
			this.hiveApMode = hiveApMode;
			generate();
		}

		private void generate() {
			if (network == null || "".equals(network)) {
				return;
			}
			try {
				String ipAddress;
				String netStr;
				int netInt;
				if (network.contains("/")) {
					ipAddress = network.substring(0, network.indexOf("/"));
					netStr = network.substring(network.indexOf("/") + 1);
					netInt = Integer.parseInt(netStr.trim());
				} else {
					ipAddress = network;
					netInt = 32;
				}

				ipLong = AhEncoder.ip2Long(ipAddress);
				rangeStart = MgrUtil
						.getStartIpAddressValue(ipLong, netInt);
				rangeEnd = (long) (rangeStart + Math.pow(2,
						32 - netInt));
			} catch (Exception e) {
				log.error("generate", "parse Ip Address : " + network + " error:", e);
			}
		}

		public long getIpLong() {
			return this.ipLong;
		}

		public long getRangeStart() {
			return this.rangeStart;
		}

		public long getRangeEnd() {
			return this.rangeEnd;
		}

		public short getHiveApMode() {
			return this.hiveApMode;
		}

		public boolean inRange(Object obj) {
			if (obj instanceof NetworkRange) {
				NetworkRange range = (NetworkRange) obj;
				return hiveApMode == range.getHiveApMode()
						&& range.getIpLong() >= rangeStart
						&& range.getIpLong() <= rangeEnd;
			} else {
				return false;
			}
		}
	}

}