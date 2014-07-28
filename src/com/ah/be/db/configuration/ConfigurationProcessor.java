package com.ah.be.db.configuration;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.ah.be.app.DebugUtil;
import com.ah.be.db.configuration.ConfigurationChangedEvent.Operation;
import com.ah.be.event.AhShutdownEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmBo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.util.MgrUtil;

public class ConfigurationProcessor implements Runnable {

	public enum ConfigurationType {
		Configuration, UserDatabase
	}

	private final BlockingQueue<BeBaseEvent> eventQueue;
	private final AtomicInteger lostEventCount;
	private Thread eventMgr;

	public ConfigurationProcessor() {
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(5000);
		lostEventCount = new AtomicInteger(0);
	}

	public void start() {
		if (isStart()) {
			return;
		}
		eventMgr = new Thread(this);
		eventMgr.setName("Configuration process");
		eventMgr.start();
	}

	public boolean isStart() {
		return eventMgr != null && eventMgr.isAlive();
	}

	public final void stop() {
		eventQueue.clear();
		BeBaseEvent stopEvent = new AhShutdownEvent();
		addConfigurationEvent(stopEvent);
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_CONFIG,
				"<BE Thread> Configuration processor is running...");
		
		while (true) {
			try {
				// take() method blocks
				BeBaseEvent event = eventQueue.take();

				if (event.getEventType() == BeEventConst.AH_SHUTDOWN_EVENT) {
					BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_CONFIG,
							"<BE Thread> Configuration processor is shutdown, events lost: "
									+ lostEventCount.intValue());
					break;
				} else {
					processEvent(event);
				}
			} catch (Exception e) {
				DebugUtil.configDebugError(
						"ConfigurationProcessor run exception", e);
			} catch (Error e) {
				DebugUtil.configDebugError("ConfigurationProcessor run error",
						e);
			}
		}
	}

	public synchronized void addConfigurationEvent(BeBaseEvent event) {
		if (!eventQueue.offer(event)) {
			lostEventCount.incrementAndGet();

			DebugUtil.configDebugInfo("Configuration processor queue is full, "
					+ lostEventCount.intValue() + " events lost.");

			// New event is more important, so remove the head of queue in order
			// to add new event into the FIFO queue.
			BeBaseEvent lostEvent = eventQueue.poll();

			if (lostEvent != null) {
				DebugUtil.configDebugInfo("Discarding Event. Type:["
						+ lostEvent.getEventType() + "]");
			}

			if (!eventQueue.offer(event)) {
				DebugUtil
						.configDebugInfo("Configuration processor queue is full even after removing the head of queue.");
			}
		}
	}

	public void processEvent(BeBaseEvent event) {
		try {
			if (null != event && event instanceof ConfigurationChangedEvent) {
				ConfigurationChangedEvent configEvent = (ConfigurationChangedEvent) event;
				HmBo bo = configEvent.getBo();
				if (bo instanceof HiveAp) {// HiveAp is special
					dealHiveAp((HiveAp) bo, configEvent);
				} else if (bo instanceof LocalUser) {
					dealLocalUser((LocalUser) bo, configEvent);
				} else {
					dealOtherProfiles(bo, configEvent);
				}
			}
		} catch (Exception e) {
			DebugUtil.configDebugError("processEvent", e);
		}
	}

	private void dealHiveAp(HiveAp hiveAp, ConfigurationChangedEvent configEvent)
			throws Exception {
		Date oldVer = configEvent.getOldVer();
		if (null != oldVer) {
			// must be multiple modify, the IP cannot be changed in multiple
			// modify, so it won't affect to other APs. Just update itself;
			boolean isChanged = configEvent.isConfigurationChanged();
			if (!isChanged) {
				DebugUtil.configDebugInfo(hiveAp.getLabel()
						+ " updated, but no field changed.");
				return;
			}
			BoMgmt.getHiveApMgmt().updateConfigurationIndication(hiveAp);
		} else {
			HiveAp updatedHiveAp = QueryUtil.findBoById(HiveAp.class,
					hiveAp.getId());
			if (null == updatedHiveAp) {
				return;
			}
			if (updatedHiveAp.getVersion().equals(hiveAp.getVersion())) {
				DebugUtil.configDebugInfo(hiveAp.getLabel()
						+ " updated, but no field changed.");
				return;
			}
			// update itself first;
			BoMgmt.getHiveApMgmt().updateConfigurationIndication(updatedHiveAp);
			// update related HiveAP(which set it as neighbor)
			// Only ip&mask changes affect neighbor
			String ip = hiveAp.getCfgIpAddress();
			String mask = hiveAp.getCfgNetmask();
			String updatedIp = updatedHiveAp.getCfgIpAddress();
			String updatedMask = updatedHiveAp.getCfgNetmask();
			if ((null != ip && !ip.equals(updatedIp))
					|| (null != mask && !mask.equals(updatedMask))) {
				Set<Long> hiveAps = ConfigurationUtils.getRelevantHiveAp(hiveAp);
				if (null != hiveAps && !hiveAps.isEmpty()) {
					int pendingIndex = ConfigurationResources.CONFIG_HIVEAP_NEIGHBOR_CHANGE;
					BoMgmt.getHiveApMgmt().updateConfigurationIndication(
							hiveAps, true, pendingIndex, null,
							ConfigurationType.Configuration);
				}
			}
		}
	}

	/*- the function used Private PSK association with user database.
	private void dealSsidProfile(SsidProfile ssidProfile,
			ConfigurationChangedEvent event) throws Exception {
		SsidProfile oldSsidProfile = (SsidProfile) event.getOldBo();
		if (null == oldSsidProfile) {
			DebugUtil.configDebugError(ssidProfile.getLabel()
					+ " updated, cannot find its old ssid profile.");
			return;
		}
		dealOtherProfiles(ssidProfile, event);

		Set<Long> hiveAps = cu.getRelevantHiveAp(ssidProfile);
		if (null == hiveAps || hiveAps.isEmpty()) {
			return;
		}
		// update the indication of user database(PSK) if needed;
		Set<PersonalizedPsk> oldSet = oldSsidProfile.getPrivatePsks();
		Set<PersonalizedPsk> set = ssidProfile.getPrivatePsks();
		Set<Long> oldPsks = new HashSet<Long>();
		if (null != oldSet && !oldSet.isEmpty()) {
			for (PersonalizedPsk psk : oldSet) {
				oldPsks.add(psk.getId());
			}
		}
		Set<Long> psks = new HashSet<Long>();
		if (null != set && !set.isEmpty()) {
			for (PersonalizedPsk psk : set) {
				psks.add(psk.getId());
			}
		}
		if (oldPsks.isEmpty() && !psks.isEmpty()) {
			// update indication flag to true
			String desc = cu.getDescription(ssidProfile);
			int pendingIndex = ConfigurationResources.CONFIGURATION_CHANGE;
			ConfigurationType type = ConfigurationType.UserDatabase;
			BoMgmt.getHiveApMgmt().updateConfigurationIndication(hiveAps, true,
					pendingIndex, desc, type);
		} else if (!oldPsks.isEmpty() && psks.isEmpty()) {
			// need to check RADIUS Server setting
			String where = "radiusServerProfile != null and id in (:s1)";
			Object[] values = new Object[] { hiveAps };
			List<?> list = QueryUtil.executeQuery("select id from "
					+ HiveAp.class.getSimpleName(), null, new FilterParams(
					where, values));
			// HiveAP set as RADIUS Server
			Set<Long> set_with_radius = new HashSet<Long>();
			for (Object object : list) {
				set_with_radius.add((Long) object);
			}
			if (!set_with_radius.isEmpty()) {
				int pendingIndex = ConfigurationResources.CONFIG_HIVEAP_INITIAL;
				ConfigurationType type = ConfigurationType.UserDatabase;
				BoMgmt.getHiveApMgmt().updateConfigurationIndication(
						set_with_radius, true, pendingIndex, null, type);
			}
			// HiveAP set as normal AP
			hiveAps.removeAll(set_with_radius);
			ConfigurationType type = ConfigurationType.UserDatabase;
			BoMgmt.getHiveApMgmt().updateConfigurationIndication(hiveAps,
					false, 0, null, type);

		} else if (!oldPsks.isEmpty() && !psks.isEmpty()) {
			// need to check if the PSK entries changed
			boolean changed = false;
			if (oldPsks.size() != psks.size()) {
				changed = true;
			} else {
				for (Long id : psks) {
					if (!oldPsks.contains(id)) {
						changed = true;
						break;
					}
				}
			}
			if (changed) {
				String desc = cu.getDescription(ssidProfile);
				int pendingIndex = ConfigurationResources.CONFIGURATION_CHANGE;
				ConfigurationType type = ConfigurationType.UserDatabase;
				BoMgmt.getHiveApMgmt().updateConfigurationIndication(hiveAps,
						true, pendingIndex, desc, type);
			}
		}

	}

	private void dealSsidProfile(SsidProfile ssidProfile,
			ConfigurationChangedEvent event) throws Exception {
		SsidProfile oldSsidProfile = (SsidProfile) event.getOldBo();
		if (null == oldSsidProfile) {
			DebugUtil.configDebugError(ssidProfile.getLabel()
					+ " updated, cannot find its old ssid profile.");
			return;
		}
		dealOtherProfiles(ssidProfile, event);

		Set<Long> hiveAps = cu.getRelevantHiveAp(ssidProfile);
		if (null == hiveAps || hiveAps.isEmpty()) {
			return;
		}
		// update the indication of PSK if needed;
		Set<PersonalizedPsk> oldSet = oldSsidProfile.getPrivatePsks();
		Set<PersonalizedPsk> set = ssidProfile.getPrivatePsks();
		Set<Long> oldPsks = new HashSet<Long>();
		if (null != oldSet && !oldSet.isEmpty()) {
			for (PersonalizedPsk psk : oldSet) {
				oldPsks.add(psk.getId());
			}
		}
		Set<Long> psks = new HashSet<Long>();
		if (null != set && !set.isEmpty()) {
			for (PersonalizedPsk psk : set) {
				psks.add(psk.getId());
			}
		}
		if (oldPsks.isEmpty() && !psks.isEmpty()) {
			// update indication flag to true
			String desc = cu.getDescription(ssidProfile);
			int pendingIndex = ConfigurationResources.CONFIGURATION_CHANGE;
			ConfigurationType type = ConfigurationType.Psk;
			BoMgmt.getHiveApMgmt().updateConfigurationIndication(hiveAps, true,
					pendingIndex, desc, type);
		} else if (!oldPsks.isEmpty() && psks.isEmpty()) {
			// update indication flag to false
			ConfigurationType type = ConfigurationType.Psk;
			BoMgmt.getHiveApMgmt().updateConfigurationIndication(hiveAps,
					false, 0, null, type);
		} else if (!oldPsks.isEmpty() && !psks.isEmpty()) {
			// need to check if the PSK entries changed
			boolean changed = false;
			if (oldPsks.size() != psks.size()) {
				changed = true;
			} else {
				for (Long id : psks) {
					if (!oldPsks.contains(id)) {
						changed = true;
						break;
					}
				}
			}
			if (changed) {
				String desc = cu.getDescription(ssidProfile);
				int pendingIndex = ConfigurationResources.CONFIGURATION_CHANGE;
				ConfigurationType type = ConfigurationType.Psk;
				BoMgmt.getHiveApMgmt().updateConfigurationIndication(hiveAps,
						true, pendingIndex, desc, type);
			}
		}
	}*/

	private void dealLocalUser(LocalUser localUser,
			ConfigurationChangedEvent event) throws Exception {
		Operation operation = event.getOperation();
		LocalUserGroup group = localUser.getLocalUserGroup();
		if (Operation.CREATE == operation || Operation.REMOVE == operation
				|| Operation.REVOKE == operation) {
			// get the binding local user group.
			Set<Long> hiveAps = ConfigurationUtils.getRelevantHiveAp(group);
			if (null != hiveAps && !hiveAps.isEmpty()) {
				String desc = ConfigurationUtils.getDescription(localUser,event);
				int pendingIndex = ConfigurationChangedEvent.Operation.CREATE == operation ? ConfigurationResources.CONFIGURATION_CREATE
						: (ConfigurationChangedEvent.Operation.REMOVE == operation ? ConfigurationResources.CONFIGURATION_REMOVE
								: ConfigurationResources.CONFIGURATION_REVOKE);
				BoMgmt.getHiveApMgmt().updateConfigurationIndication(hiveAps,
						true, pendingIndex, desc,
						ConfigurationType.UserDatabase);
			}
		} else if (Operation.UPDATE == operation) {
			LocalUser user = QueryUtil.findBoById(LocalUser.class,
					localUser.getId());
			if (null == user
					|| user.getVersion().equals(localUser.getVersion())) {
				// doesn't update any field, just return;
				return;
			}
			LocalUserGroup updatedGroup = user.getLocalUserGroup();
			String desc = ConfigurationUtils.getDescription(user,event);
			if (null != group && null != updatedGroup
					&& group.getId().equals(updatedGroup.getId())) {
				// local user group not changed;
				Set<Long> hiveAps = ConfigurationUtils.getRelevantHiveAp(group);
				if (null != hiveAps && !hiveAps.isEmpty()) {
					int pendingIndex = ConfigurationResources.CONFIGURATION_CHANGE;
					BoMgmt.getHiveApMgmt().updateConfigurationIndication(
							hiveAps, true, pendingIndex, desc,
							ConfigurationType.UserDatabase);
				}
			} else {
				Set<Long> hiveAps = ConfigurationUtils.getRelevantHiveAp(group);
				if (null != hiveAps && !hiveAps.isEmpty()) {
					int pendingIndex = ConfigurationResources.CONFIGURATION_REMOVE;
					BoMgmt.getHiveApMgmt().updateConfigurationIndication(
							hiveAps, true, pendingIndex, desc,
							ConfigurationType.UserDatabase);
				}
				Set<Long> updatedHiveAps = ConfigurationUtils.getRelevantHiveAp(updatedGroup);
				if (null != updatedHiveAps && !updatedHiveAps.isEmpty()) {
					int pendingIndex = ConfigurationResources.CONFIGURATION_CREATE;
					BoMgmt.getHiveApMgmt().updateConfigurationIndication(
							updatedHiveAps, true, pendingIndex, desc,
							ConfigurationType.UserDatabase);
				}
			}
		}
	}

	private void dealOtherProfiles(HmBo bo, ConfigurationChangedEvent event)
			throws Exception {
		boolean isChanged = event.isConfigurationChanged();
		if (!isChanged) {
			DebugUtil.configDebugInfo(bo.getLabel()
					+ " updated, but no field changed.");
			return;
		}
		Set<Long> hiveAps = ConfigurationUtils.getRelevantHiveAp(bo);
		if (null != hiveAps && !hiveAps.isEmpty()) {
			String desc = ConfigurationUtils.getDescription(bo, event);
			int pendingIndex = ConfigurationResources.CONFIGURATION_CHANGE;
			ConfigurationType type = ConfigurationType.Configuration;
			if (bo instanceof LocalUser) {
				type = ConfigurationType.UserDatabase;
			}
			BoMgmt.getHiveApMgmt().updateConfigurationIndication(hiveAps, true,
					pendingIndex, desc, type);
		}
	}

}