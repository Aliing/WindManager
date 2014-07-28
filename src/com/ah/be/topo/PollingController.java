package com.ah.be.topo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeTopoUtil;
import com.ah.bo.HmBo;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapSettings;

/**
 * On topology map, the HiveAP will polling neighbor time-lapse, but if do it
 * with all HiveAPs will bring weak performance, also it doesn't necessary. Now,
 * we just to refresh the links on which map container user click. i.e., to
 * refresh the latest map container clicked per session.
 */
public class PollingController implements QueryBo {

	private final Map<String, Long> pollingContainer = new HashMap<String, Long>();
	private final Map<Long, Integer> container_remainderTime = new HashMap<Long, Integer>();

	public PollingController() {
	}

	/**
	 * Once user click on a map, should add this map container into the polling
	 * list, and polling neighbor information periodic.
	 *
	 * @param session -
	 * @param containerId -
	 */
	public synchronized void addContainer(HttpSession session, Long containerId) {
		if (null == session || null == containerId) {
			return;
		}
		try {
			String sessionId = session.getId();
			Long existContainer = pollingContainer.get(sessionId);
			if (null == existContainer) {
				DebugUtil
						.topoDebugInfo("No previous map container polling for session ["
								+ sessionId
								+ "], now start polling map:(id="
								+ containerId + ").");
			} else {
				DebugUtil.topoDebugInfo("The old polling map:(id="
						+ existContainer + ") is replaced by the new map:(id="
						+ containerId + ") for session [" + sessionId + "].");
			}
			pollingContainer.put(sessionId, containerId);
			DebugUtil.topoDebugInfo("Current polling pool has "
					+ pollingContainer.size() + " sessions");
			// update container_remainderTime;
			updateContainerRemainderTime(existContainer, containerId);

			// try to polling this map container immediately
			Set<Long> pollingMapId_set = new HashSet<Long>(1);
			pollingMapId_set.add(containerId);
			HmBeTopoUtil.getMapLinkProcessor().addPollingContainers(
					pollingMapId_set);
		} catch (Exception e) {
			DebugUtil.topoDebugError("PollingController.addContainer", e);
		}
	}

	/**
	 * Once a login user leave, should remove the container from the periodic
	 * polling list.
	 *
	 * @param session -
	 */
	public synchronized void removeContainer(HttpSession session) {
		if (null == session) {
			return;
		}
		try {
			String sessionId = session.getId();
			Long containerId = pollingContainer.remove(sessionId);

			DebugUtil.topoDebugInfo("Session expiration, remove map:(id="
					+ containerId + "). Current polling sessions size: "
					+ pollingContainer.size());
			// update container_remainderTime;
			updateContainerRemainderTime(containerId, null);

		} catch (Exception e) {
			DebugUtil.topoDebugError("PollingController.removeContainer", e);
		}
	}

	/**
	 * get a set of container Id which should be polling neighbors.
	 *
	 * @return -
	 */
	public synchronized Set<Long> getPollingContainer() {
		Set<Long> containers = new HashSet<Long>();
		Collection<Long> session_containers = pollingContainer.values();
		if (null != session_containers && session_containers.size() > 0) {
			for (Long containerId : session_containers) {
				Integer timeValue = container_remainderTime.get(containerId);
				if (null != timeValue) {
					int remainderTime = timeValue
							- BeTopoModuleParameters.MAP_REFRESH_TASK_INTERVAL;
					if (remainderTime > 0) {
						container_remainderTime.put(containerId, remainderTime);
					} else {
						containers.add(containerId);
						container_remainderTime.put(containerId,
								getContainerPollingInterval(containerId));
					}
				}
			}
		}
		return containers;
	}

	private int getContainerPollingInterval(Long containerId) {
		if (null == containerId) {
			return MapSettings.DEFAULT_POLLING_INTERVAL;
		}
		MapContainerNode mapContainer = QueryUtil
				.findBoById(MapContainerNode.class, containerId, this);
		if (null == mapContainer) {
			return MapSettings.DEFAULT_POLLING_INTERVAL;
		}
		return BeTopoModuleUtil.getMapGlobalSetting(mapContainer.getOwner())
				.getPollingInterval();
	}

	private void updateContainerRemainderTime(Long removedId, Long addId) {
		if (null != removedId) {
			// one container removed from list
			if (!pollingContainer.containsValue(removedId)) {
				// remove the container from this list too.
				container_remainderTime.remove(removedId);
			}
		}
		if (null != addId) {
			container_remainderTime.put(addId,
					getContainerPollingInterval(addId));
		}
		DebugUtil.topoDebugInfo("Current size of Container_remainderTime list:"
				+ container_remainderTime.size());
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (null != bo) {
			if (bo instanceof MapContainerNode) {
				MapContainerNode mapContainerNode = (MapContainerNode) bo;
				mapContainerNode.getOwner().getId();
			}
		}
		return null;
	}

}