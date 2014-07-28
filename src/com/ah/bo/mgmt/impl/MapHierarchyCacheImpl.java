package com.ah.bo.mgmt.impl;

/*
 * @author Chris Scheers
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.Folder;
import com.ah.bo.mgmt.MapHierarchyCache;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.events.BoEventFilter;
import com.ah.events.BoEventListener;
import com.ah.events.impl.BoObserver;
import com.ah.ui.actions.hiveap.HiveApMonitor;
import com.ah.util.Tracer;

public final class MapHierarchyCacheImpl implements MapHierarchyCache,
		BoEventListener<MapContainerNode> {

	private static final Tracer log = new Tracer(
			MapHierarchyCacheImpl.class.getSimpleName());

	private MapHierarchyCacheImpl() {
	}

	private static MapHierarchyCache instance;

	public synchronized static MapHierarchyCache getInstance() {
		if (instance == null) {
			instance = new MapHierarchyCacheImpl();
		}
		return instance;
	}

	private final Map<Long, Folder> folders = new HashMap<Long, Folder>();
	private Map<String, Long> deviceFolderIds = new HashMap<String, Long>();
	private final QueryBo loadHierarchyQueryBo = new LoadHierarchyQueryBo(),
			loadParentQueryBo = new LoadParentQueryBo();
	private final ScheduledExecutorService refreshService = Executors
			.newSingleThreadScheduledExecutor();

	@Override
	public synchronized void init() {
		log.info("init", "Initializing map hierarchy cache.");
		long start = System.currentTimeMillis();
		loadHierarchy(BoMgmt.getMapMgmt().getRootMapId());
		long end = System.currentTimeMillis();
		log.info("init", "Init hierarchy cache done: " + (end - start)
				+ "ms, map count: " + folders.size());
		refreshService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				reloadHierarchy();
			}
		}, 50, 500, TimeUnit.MINUTES);
		stateHierarchy();
	}

	@Override
	public synchronized void register() {
		BoObserver.addBoEventListener(this,
				new BoEventFilter<MapContainerNode>(MapContainerNode.class));
	}

	@Override
	public synchronized void deregister() {
		BoObserver.removeBoEventListener(this);
	}

	@Override
	public synchronized void destroy() {
		log.info("Cleaning up hierarchy cache.");
		deregister();
		refreshService.shutdown();
		deviceFolderIds.clear();
		folders.clear();
	}

	private synchronized void reloadHierarchy() {
		log.info("Reloading hierarchy.");
		long start = System.currentTimeMillis();
		for (Folder folder : folders.values()) {
			folder.setSelected(true);
		}
		loadHierarchy(BoMgmt.getMapMgmt().getRootMapId());
		garbageCollection();
		stateHierarchy();
		long end = System.currentTimeMillis();
		log.info("reloadHierarchy", "Reload hierarchy cache done: "
				+ (end - start) + "ms, map count: " + folders.size());
	}

	private synchronized Folder loadHierarchy(Long containerId) {
		QueryUtil.findBoById(MapNode.class, containerId, loadHierarchyQueryBo);
		return folders.get(containerId);
	}

	private class LoadHierarchyQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (bo == null) {
				return null;
			}
			MapContainerNode mapContainerNode = (MapContainerNode) bo;
			Folder parent = null;
			if (mapContainerNode.getParentMap() != null) {
				parent = folders.get(mapContainerNode.getParentMap().getId());
			}
			loadHierarchy(mapContainerNode, parent);
			return null;
		}
	}

	private synchronized void loadHierarchy(MapContainerNode mapContainerNode,
			Folder parent) {
		Folder folder = createFolder(mapContainerNode);
		if (parent != null) {
			folder.setParentId(parent.getId());
		}
		for (MapNode mapNode : mapContainerNode.getChildNodes()) {
			if (!mapNode.isLeafNode()) {
				// Proceed inside out
				loadHierarchy((MapContainerNode) mapNode, folder);
			}
		}
		// Remove it so it won't be used anymore
		Folder oldFolder = folders.remove(folder.getId());
		if (oldFolder != null) {
			// The folder will be recreated
			removeDeviceNodes(oldFolder);
		}
		loadFolder(mapContainerNode, folder);
		// At this point the folder is fully populated.
		updateCachedFolder(folder);
	}

	private synchronized void updateCachedFolder(Folder folder) {
		folders.put(folder.getId(), folder);
	}

	private Folder createFolder(MapContainerNode mapContainerNode) {
		Folder folder = new FolderNode(mapContainerNode.getId());
		folder.setName(mapContainerNode.getMapName());
		return folder;
	}

	protected MapContainerNode loadParent(Long containerId) {
		return QueryUtil.findBoById(MapContainerNode.class, containerId,
				loadParentQueryBo);
	}

	private class LoadParentQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (null == bo) {
				return null;
			}
			MapContainerNode mapContainerNode = (MapContainerNode) bo;
			if (mapContainerNode.getParentMap() != null) {
				mapContainerNode.getParentMap().getId(); // Trigger a load
			}
			return null;
		}
	}

	private DeviceNode createDeviceNode(Folder folder, HiveAp hiveAp) {
		DeviceNode deviceNode = new DeviceNode(hiveAp.getMacAddress(),
				hiveAp.getHostName());
		updateDeviceNode(deviceNode, hiveAp);
		addDeviceNode(folder, deviceNode);
		return deviceNode;
	}

	private void updateDeviceNode(DeviceNode deviceNode, HiveAp hiveAp) {
		deviceNode.setConnected(hiveAp.isConnected());
		deviceNode.setManageStatus(hiveAp.getManageStatus());
		updateClientCount(deviceNode, hiveAp);
	}

	private void updateClientCount(DeviceNode deviceNode, HiveAp hiveAp) {
		deviceNode.setClientCount((int) HiveApMonitor
				.getActiveClientCount(hiveAp));
	}

	private synchronized void addDeviceNode(Folder folder, DeviceNode deviceNode) {
		// Assumes folder is not null
		folder.addDeviceNode(deviceNode);
		deviceFolderIds.put(deviceNode.id, folder.getId());
	}

	private synchronized DeviceNode removeDeviceNode(Folder folder, String id) {
		// Assumes folder is not null
		deviceFolderIds.remove(id);
		return folder.removeDeviceNode(id);
	}

	private synchronized void removeDeviceNodes(Folder folder) {
		for (DeviceNode deviceNode : folder.getDeviceNodes()) {
			// Folder will be recreated, so don't remove the nodes from the
			// folder. folder.removeDeviceNode(deviceNode.id); would cause
			// concurrent modification exception
			deviceFolderIds.remove(deviceNode.id);
		}
	}

	private synchronized void loadFolder(MapContainerNode mapContainerNode,
			Folder folder) {
		List<MapContainerNode> folders = new ArrayList<MapContainerNode>();
		for (MapNode mapNode : mapContainerNode.getChildNodes()) {
			if (mapNode.isLeafNode()) {
				MapLeafNode mapLeafNode = (MapLeafNode) mapNode;
				if (mapLeafNode.getHiveAp() != null) {
					createDeviceNode(folder, mapLeafNode.getHiveAp());
				}
			} else {
				folders.add((MapContainerNode) mapNode);
			}
		}
		if (mapContainerNode.getMapType() == MapContainerNode.MAP_TYPE_BUILDING) {
			Collections.sort(folders, new Comparator<MapContainerNode>() {
				@Override
				public int compare(MapContainerNode o1, MapContainerNode o2) {
					return o2.getMapOrder() - o1.getMapOrder();
				}
			});
		}
		List<Long> folderIds = new ArrayList<Long>();
		for (MapContainerNode child : folders) {
			folderIds.add(child.getId());
		}
		folder.setFolderIds(folderIds);
		updateCounts(folder);
	}

	private void reloadFrom(Long containerId) {
		Folder folder = folders.get(containerId);
		int managedUp = 0, managedDown = 0, newUp = 0, newDown = 0, clients = 0;
		if (folder == null) {
			log.error("reloadFrom", "No cached folder for id: " + containerId
					+ " ??");
		} else {
			managedUp = folder.getManagedUpApCount();
			managedDown = folder.getManagedDownApCount();
			newUp = folder.getNewUpApCount();
			newDown = folder.getNewDownApCount();
			clients = folder.getClientCount();
		}
		folder = loadHierarchy(containerId);
		if (folder != null) {
			propagateDeltas(folder, managedUp, managedDown, newUp, newDown,
					clients);
		}
	}

	@Override
	public synchronized void boCreated(MapContainerNode mapContainerNode) {
		log.info("boCreated", "New map: " + mapContainerNode.getMapName()
				+ ", id: " + mapContainerNode.getId());
		// Don't assume parent is accessible
		mapContainerNode = loadParent(mapContainerNode.getId());
		if (mapContainerNode.getParentMap() == null) {
			return;
		}
		stateHierarchy();
		reloadFrom(mapContainerNode.getParentMap().getId());
		stateHierarchy();
	}

	@Override
	public synchronized void boUpdated(MapContainerNode mapContainerNode) {
		log.info("boUpdated", "Map updated: " + mapContainerNode.getId()
				+ ", name: " + mapContainerNode.getLabel());
		if (mapContainerNode.getMapType() < 0) {
			log.info("Only for the purpose of updating alarm status, ignore in MapHierachyCache.");
			return;
		}
		stateHierarchy();
		reloadFrom(mapContainerNode.getId());
		stateHierarchy();
	}

	@Override
	public synchronized void boRemoved(MapContainerNode hmBo) {
		log.info("boRemoved", "Map removed: " + hmBo.getLabel());
		Folder folder = folders.get(hmBo.getId());
		if (folder == null) {
			log.error("boRemoved", "No cached folder for id: " + hmBo.getId()
					+ " ??");
			return;
		}
		stateHierarchy();
		if (folder.getParentId() == null) {
			log.error("boRemoved", "Cached folder for id: " + hmBo.getId()
					+ " has no parent ??");
		} else {
			reloadFrom(folder.getParentId());
		}
		folders.remove(folder.getId());
		stateHierarchy();
	}

	@Override
	public synchronized void hiveApAdded(HiveAp hiveAp) {
		log.info("hiveApAdded", "HiveAp added: " + hiveAp.getLabel());
		MapContainerNode container = hiveAp.getMapContainer();
		if (container == null) {
			log.info_non("Device is not on a map, just ignore.");
			return;
		}
		stateHierarchy();
		Long containerId = deviceFolderIds.get(hiveAp.getMacAddress());
		Folder folder = folders.get(containerId);
		if (folder != null) {
			removeDeviceNode(folder, hiveAp.getMacAddress()); // Just in case
		}
		folder = folders.get(container.getId());
		if (folder != null) {
			createDeviceNode(folder, hiveAp);
		}
		updateCountsAndPropagate(folder);
		stateHierarchy();
	}

	@Override
	public synchronized void hiveApRemoved(HiveAp hiveAp) {
		log.info("hiveApRemoved", "HiveAp removed: " + hiveAp.getLabel());
		Long containerId = deviceFolderIds.get(hiveAp.getMacAddress());
		Folder folder = folders.get(containerId);
		if (folder == null) {
			return;
		}
		stateHierarchy();
		removeDeviceNode(folder, hiveAp.getMacAddress());
		updateCountsAndPropagate(folder);
		stateHierarchy();
	}

	@Override
	public synchronized void hiveApUpdated(HiveAp hiveAp) {
		log.info("hiveApUpdated", "HiveAp updated: " + hiveAp.getLabel());
		Long currentContainerId = (hiveAp.getMapContainer() == null ? null
				: hiveAp.getMapContainer().getId());
		Long oldContainerId = deviceFolderIds.get(hiveAp.getMacAddress());
		if (currentContainerId == null && oldContainerId == null) {
			// No change and not on a container
			return;
		}
		stateHierarchy();
		Folder oldFolder = folders.get(oldContainerId);
		DeviceNode oldDeviceNode = null;
		if (oldFolder != null) {
			oldDeviceNode = oldFolder.getDeviceNode(hiveAp.getMacAddress());
			if (oldDeviceNode == null) {
				log.error("hiveApUpdated", "AP: " + hiveAp.getMacAddress()
						+ " is not on " + oldFolder.getName() + " ??");
			}
		}
		if (currentContainerId != null
				&& currentContainerId.equals(oldContainerId)) {
			// AP remained on the same container, status update only.
			log.info("Same container, just update status and client count.");
			if (oldFolder == null) {
				log.error("hiveApUpdated", "No cached folder for id: "
						+ currentContainerId + " ??");
			} else {
				if (oldDeviceNode == null) {
					oldDeviceNode = createDeviceNode(oldFolder, hiveAp);
				} else {
					updateDeviceNode(oldDeviceNode, hiveAp);
				}
				updateCountsAndPropagate(oldFolder);
			}
		} else {
			log.info("Old and new container are different.");
			if (oldDeviceNode != null) {
				removeDeviceNode(oldFolder, oldDeviceNode.id);
				updateCountsAndPropagate(oldFolder);
			}
			Folder currentFolder = folders.get(currentContainerId);
			if (currentFolder != null) {
				// Remove, just in case
				removeDeviceNode(currentFolder, hiveAp.getMacAddress());
				createDeviceNode(currentFolder, hiveAp);
				updateCountsAndPropagate(currentFolder);
			}
		}
		stateHierarchy();
	}

	@Override
	public synchronized void activeClientAdded(SimpleHiveAp ap, int count) {
		if (ap == null) {
			return;
		}
		log.debug("activeClientAdded",
				"ActiveClient added to HiveAp: " + ap.getHostname());
		updateClientCountAndPropagate(ap);
	}

	@Override
	public synchronized void activeClientRemoved(SimpleHiveAp ap, int count) {
		if (ap == null) {
			return;
		}
		log.debug("activeClientRemoved", "ActiveClient removed from HiveAp: "
				+ ap.getHostname());
		updateClientCountAndPropagate(ap);
	}

	private void updateClientCountAndPropagate(SimpleHiveAp ap) {
		Long containerId = deviceFolderIds.get(ap.getMacAddress());
		Folder folder = folders.get(containerId);
		if (folder == null) {
			return;
		}
		DeviceNode deviceNode = folder.getDeviceNode(ap.getMacAddress());
		if (deviceNode == null) {
			return;
		}
		HiveAp hiveAp = new HiveAp();
		hiveAp.setMacAddress(ap.getMacAddress());
		HmDomain owner = new HmDomain();
		owner.setId(ap.getDomainId());
		hiveAp.setOwner(owner);
		updateClientCount(deviceNode, hiveAp);
		updateCountsAndPropagate(folder);
	}

	private void updateCountsAndPropagate(Folder folder) {
		int managedUp = folder.getManagedUpApCount();
		int managedDown = folder.getManagedDownApCount();
		int newUp = folder.getNewUpApCount();
		int newDown = folder.getNewDownApCount();
		int clients = folder.getClientCount();
		updateCounts(folder);
		propagateDeltas(folder, managedUp, managedDown, newUp, newDown, clients);
	}

	private void propagateDeltas(Folder folder, int managedUp, int managedDown,
			int newUp, int newDown, int clientCount) {
		log.info("Old counts: mUp: " + managedUp + ", mDown: " + managedDown
				+ ", nUp: " + newUp + ", nDown: " + newDown + ", clients: "
				+ clientCount);
		log.info("New counts: mUp: " + folder.getManagedUpApCount()
				+ ", mDown: " + folder.getManagedDownApCount() + ", nUp: "
				+ folder.getNewUpApCount() + ", nDown: "
				+ folder.getNewDownApCount() + ", clients: "
				+ folder.getClientCount());
		Long parentId = folder.getParentId();
		int delta = folder.getManagedUpApCount() - managedUp;
		if (delta != 0) {
			propagateManagedUp(parentId, delta);
		}
		delta = folder.getManagedDownApCount() - managedDown;
		if (delta != 0) {
			propagateManagedDown(parentId, delta);
		}
		delta = folder.getNewUpApCount() - newUp;
		if (delta != 0) {
			propagateNewUp(parentId, delta);
		}
		delta = folder.getNewDownApCount() - newDown;
		if (delta != 0) {
			propagateNewDown(parentId, delta);
		}
		delta = folder.getClientCount() - clientCount;
		if (delta != 0) {
			propagateClientCount(parentId, delta);
		}
	}

	private void updateCounts(Folder folder) {
		updateHiveApCounts(folder);
		updateActiveClientCounts(folder);
	}

	private void updateHiveApCounts(Folder folder) {
		// Folder must not be null
		int managedUp = 0, managedDown = 0, newUp = 0, newDown = 0;
		for (DeviceNode deviceNode : folder.getDeviceNodes()) {
			short manageStatus = deviceNode.getManageStatus();
			if (deviceNode.isConnected()) {
				if (manageStatus == HiveAp.STATUS_MANAGED) {
					managedUp++;
				} else if (manageStatus == HiveAp.STATUS_NEW) {
					newUp++;
				}
			} else {
				if (manageStatus == HiveAp.STATUS_MANAGED) {
					managedDown++;
				} else if (manageStatus == HiveAp.STATUS_NEW) {
					newDown++;
				}
			}
		}
		for (Long folderId : folder.getFolderIds()) {
			Folder child = folders.get(folderId);
			if (child == null) {
				log.error("updateHiveApCounts", "No cached folder for id: "
						+ folderId + " ??");
			} else {
				// Counts must have been updated in child folders
				managedUp += child.getManagedUpApCount();
				managedDown += child.getManagedDownApCount();
				newUp += child.getNewUpApCount();
				newDown += child.getNewDownApCount();
			}
		}
		folder.setManagedUpApCount(managedUp);
		folder.setManagedDownApCount(managedDown);
		folder.setNewUpApCount(newUp);
		folder.setNewDownApCount(newDown);
	}

	private void updateActiveClientCounts(Folder folder) {
		// Folder must not be null
		int clientCount = 0;
		for (DeviceNode deviceNode : folder.getDeviceNodes()) {
			clientCount += deviceNode.getClientCount();
		}
		for (Long folderId : folder.getFolderIds()) {
			Folder child = folders.get(folderId);
			if (child == null) {
				log.error("updateActiveClientCounts",
						"No cached folder for id: " + folderId + " ??");
			} else {
				// Counts must have been updated in child folders
				clientCount += child.getClientCount();
			}
		}
		folder.setClientCount(clientCount);
	}

	private void propagateClientCount(Long containerId, int delta) {
		Folder folder = folders.get(containerId);
		if (folder == null) {
			log.error("propagateClientCount", "No cached folder for id: "
					+ containerId + " ??");
			return;
		}
		while (true) {
			folder.setClientCount(folder.getClientCount() + delta);
			if (folder.getClientCount() < 0) {
				folder.setClientCount(0);
				log.error("propagateClientCount",
						"active client count is negative, reset to zero!");
			}
			Long id = folder.getParentId();
			if (id == null) {
				break;
			}
			folder = folders.get(id);
			if (null == folder) {
				log.error("propagateClientCount", "No cached folder for id: "
						+ containerId + " ??");
				break;
			}
		}
	}

	private void propagateManagedUp(Long containerId, int delta) {
		Folder folder = folders.get(containerId);
		if (folder == null) {
			log.error("propagateManagedUp", "No cached folder for id: "
					+ containerId + " ??");
			return;
		}
		while (true) {
			folder.setManagedUpApCount(folder.getManagedUpApCount() + delta);
			if (folder.getManagedUpApCount() < 0) {
				folder.setManagedUpApCount(0);
				log.error("propagateManagedUp",
						"managed up count is negative, reset to zero!");
			}
			if (folder.getParentId() == null) {
				break;
			}
			folder = folders.get(folder.getParentId());
			if (folder == null) {
				log.error("propagateManagedUp", "No cached folder for id: "
						+ containerId + " ??");
				break;
			}
		}
	}

	private void propagateManagedDown(Long containerId, int delta) {
		Folder folder = folders.get(containerId);
		if (folder == null) {
			log.error("propagateManagedDown", "No cached folder for id: "
					+ containerId + " ??");
			return;
		}
		while (true) {
			folder.setManagedDownApCount(folder.getManagedDownApCount() + delta);
			if (folder.getManagedDownApCount() < 0) {
				folder.setManagedDownApCount(0);
				log.error("propagateManagedDown",
						"managed down count is negative, reset to zero!");
			}
			if (folder.getParentId() == null) {
				break;
			}
			folder = folders.get(folder.getParentId());
			if (folder == null) {
				log.error("propagateManagedDown", "No cached folder for id: "
						+ containerId + " ??");
				break;
			}
		}
	}

	private void propagateNewUp(Long containerId, int delta) {
		Folder folder = folders.get(containerId);
		if (folder == null) {
			log.error("propagateNewUp", "No cached folder for id: "
					+ containerId + " ??");
			return;
		}
		while (true) {
			folder.setNewUpApCount(folder.getNewUpApCount() + delta);
			if (folder.getNewUpApCount() < 0) {
				folder.setNewUpApCount(0);
				log.error("propagateNewUp",
						"New up count is negative, reset to zero!");
			}
			if (folder.getParentId() == null) {
				break;
			}
			folder = folders.get(folder.getParentId());
			if (folder == null) {
				log.error("propagateNewUp", "No cached folder for id: "
						+ containerId + " ??");
				break;
			}
		}
	}

	private void propagateNewDown(Long containerId, int delta) {
		Folder folder = folders.get(containerId);
		if (folder == null) {
			log.error("propagateNewDown", "No cached folder for id: "
					+ containerId + " ??");
			return;
		}
		while (true) {
			folder.setNewDownApCount(folder.getNewDownApCount() + delta);
			if (folder.getNewDownApCount() < 0) {
				folder.setNewDownApCount(0);
				log.error("propagateNewDown",
						"New down count is negative, reset to zero!");
			}
			if (folder.getParentId() == null) {
				break;
			}
			folder = folders.get(folder.getParentId());
			if (folder == null) {
				log.error("propagateNewDown", "No cached folder for id: "
						+ containerId + " ??");
				break;
			}
		}
	}

	private void garbageCollection() {
		log.info("# folders before garbage collection: "
				+ folders.values().size());
		List<Long> unusedFolders = new ArrayList<>();
		for (Folder folder : folders.values()) {
			if (folder.isSelected()) {
				unusedFolders.add(folder.getId());
			}
		}
		log.info("# unused folders: " + unusedFolders.size());
		for (Long folderId : unusedFolders) {
			folders.remove(folderId);
		}
		log.info("# folders after garbage collection: "
				+ folders.values().size());

		log.info("# device folder mappings before garbage collection: "
				+ deviceFolderIds.values().size());
		List<String> unusedDeviceIds = new ArrayList<String>();
		for (String id : deviceFolderIds.keySet()) {
			Long folderId = deviceFolderIds.get(id);
			Folder folder = folders.get(folderId);
			if (folder == null) {
				unusedDeviceIds.add(id);
			} else {
				if (folder.getDeviceNode(id) == null) {
					unusedDeviceIds.add(id);
				}
			}
		}
		log.info("# unused device folder mappings: " + unusedDeviceIds.size());
		for (String id : unusedDeviceIds) {
			unusedDeviceIds.remove(id);
		}
		log.info("# device folder mappings after garbage collection: "
				+ deviceFolderIds.values().size());
	}

	private void stateHierarchy() {
		if (false) {
			stateFolderHierarchy(BoMgmt.getMapMgmt().getRootMapId(), "");
		}
	}

	private void stateFolderHierarchy(Long containerId, String indent) {
		Folder folder = folders.get(containerId);
		if (folder == null) {
			log.error("stateFolderHierarchy", "No cached folder for id: "
					+ containerId + " ??");
			return;
		}
		log.info_non(indent + "name: " + folder.getName() + ", managed up: "
				+ folder.getManagedUpApCount() + ", down: "
				+ folder.getManagedDownApCount() + ", new up: "
				+ folder.getNewUpApCount() + ", down: "
				+ folder.getNewDownApCount() + ", clients: "
				+ folder.getClientCount());
		for (DeviceNode deviceNode : folder.getDeviceNodes()) {
			log.info_non(indent + "   device: " + deviceNode.name + ", id: "
					+ deviceNode.id + ", clients: "
					+ deviceNode.getClientCount());
		}
		log.info_non(indent + "   devices count: "
				+ folder.getDeviceNodes().size());
		for (Long folderId : folder.getFolderIds()) {
			stateFolderHierarchy(folderId, indent + "   ");
		}
	}

	@Override
	public Folder getFolder(Long id) {
		return folders.get(id);
	}
}
