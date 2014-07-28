package com.ah.bo.mgmt.impl;

/*
 * @author Chris Scheers
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.MapNodeUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.DeviceResetConfig;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApUpdateResult;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryLazyBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapLink;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.monitor.OneTimePassword;
import com.ah.bo.network.SubNetworkResource;
import com.ah.bo.network.VpnGatewaySetting;
import com.ah.bo.network.VpnService;
import com.ah.bo.network.VpnServiceCredential;
import com.ah.events.BoEvent;
import com.ah.events.BoEvent.BoEventType;
import com.ah.events.BoEventFilter;
import com.ah.events.BoEventListener;
import com.ah.events.impl.BoObserver;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.monitor.SystemStatusCache;
import com.ah.util.CheckItem;
import com.ah.util.EnumConstUtil;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.Tracer;
import com.ah.ws.rest.client.utils.ClientUtils;
import com.ah.ws.rest.client.utils.LicenseResUtils;
import com.ah.ws.rest.client.utils.RedirectorResUtils;
import com.ah.ws.rest.models.ModelConstant;
import com.ah.ws.rest.models.SerialNumberList;
import com.ah.ws.rest.models.SerialNumbers;
import com.sun.jersey.api.client.ClientHandlerException;

public final class MapMgmtImpl implements MapMgmt, BoEventListener<AhAlarm>,
		QueryBo {

	private static final Tracer log = new Tracer(
			MapMgmtImpl.class.getSimpleName());

	private MapMgmtImpl() {
	}

	private static MapMgmt instance;

	public synchronized static MapMgmt getInstance() {
		if (instance == null) {
			instance = new MapMgmtImpl();
		}

		return instance;
	}

	private Long rootMapId;

	@Override
	public synchronized void init() {
		BoMgmt.getBoEventMgmt().addBoEventListener(this,
				new BoEventFilter<AhAlarm>(AhAlarm.class));
	}

	@Override
	public synchronized void destroy() {
		BoMgmt.getBoEventMgmt().removeBoEventListener(this);
	}

	public final Long getRootMapId() {
		return rootMapId;
	}

	public final void setRootMapId(Long rootMapId) {
		this.rootMapId = rootMapId;
	}

	public final MapContainerNode getRootMap() {
		return (MapContainerNode) QueryUtil.findBoById(MapNode.class,
				getRootMapId(), this);
	}

	public final MapContainerNode getVHMRootMap(Long vhmId) {
		return (MapContainerNode) QueryUtil.findBoByAttribute(MapNode.class,
				"parentMap.id", getRootMapId(), vhmId, this);
	}

	public Set<Long> getContainerDownIds(Long mapId) {
		final Set<Long> subMapIdSet = new HashSet<Long>();
		final class QueryMap implements QueryBo {
			private void findMapIds(MapContainerNode mapContainerNode) {
				subMapIdSet.add(mapContainerNode.getId());
				for (MapNode mapNode : mapContainerNode.getChildNodes()) {
					if (!mapNode.isLeafNode()) {
						findMapIds((MapContainerNode) mapNode);
					}
				}
			}

			@Override
			public Collection<HmBo> load(HmBo bo) {
				if (bo instanceof MapContainerNode) {
					findMapIds((MapContainerNode) bo);
				}
				return null;
			}
		}
		QueryUtil.findBoById(MapNode.class, mapId, new QueryMap());
		return subMapIdSet;
	}

	private void loadMapHierarchy(MapContainerNode mapContainerNode) {
		if (mapContainerNode.getParentMap() == null
				|| mapContainerNode.getParentMap().getParentMap() == null) {
			// Trigger owner load
			mapContainerNode.getOwner().getId();
		}
		for (MapNode mapNode : mapContainerNode.getChildNodes()) {
			if (!mapNode.isLeafNode()) {
				loadMapHierarchy((MapContainerNode) mapNode);
			}
		}
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}
		if (bo instanceof MapNode) {
			MapNode mapNode = (MapNode) bo;
			if (mapNode.isLeafNode()) {
				// Propagate new leaf node severity up the hierarchy.
				return propagateAlarm((MapLeafNode) mapNode);
			} else {
				// Pre-load the map hierarchy, for getRootMap().
				loadMapHierarchy((MapContainerNode) mapNode);
				return null;
			}
		} else {
			return null;
		}
	}

	public Long createWorldMap(HmDomain hmDomain) throws Exception {
		Long worldMapId = getWorldMapId(hmDomain.getId());
		if (worldMapId == null) {
			MapContainerNode rootMap = (MapContainerNode) QueryUtil.findBoById(
					MapNode.class, getRootMapId());
			MapContainerNode worldMap = new MapContainerNode();
			worldMap.setParentMap(rootMap);
			worldMap.setOwner(hmDomain);
			worldMap.setEnvironment(EnumConstUtil.MAP_ENV_OUTDOOR_FREE_SPACE);
			worldMap.setMapName(VHM_ROOT_MAP_NAME);
			worldMap.setBackground("world.png");
			worldMap.setWidth(1470);
			worldMap.setHeight(865);
			worldMap.setActualWidth(0);
			worldMap.setActualHeight(0);
			worldMap.setX(0);
			worldMap.setY(0);
			worldMapId = QueryUtil.createBo(worldMap);
			BoMgmt.getBoEventMgmt()
					.publishBoEvent(
							new BoEvent<MapContainerNode>(worldMap,
									BoEventType.CREATED));
		}
		return worldMapId;
	}

	public Long getWorldMapId(Long domainId) {
		FilterParams filterParams = new FilterParams(
				"parentMap.id = :s1 AND owner.id = :s2", new Object[] {
						getRootMapId(), domainId });
		List<MapNode> list = QueryUtil.executeQuery(MapNode.class, null,
				filterParams);

		return !list.isEmpty() ? (list.get(0)).getId() : null;
	}

	@Override
	public void boCreated(AhAlarm alarm) {
		log.debug("boCreated", "New alarm: " + alarm.getLabel());
		propagateAlarm(alarm);
	}

	@Override
	public void boUpdated(AhAlarm alarm) {
		log.debug("boUpdated", "Alarm updated: " + alarm.getLabel());
		propagateAlarm(alarm);
	}

	@Override
	public void boRemoved(AhAlarm alarm) {
	}

	public void propagateAlarm(AhAlarm alarm) {
		if (null == alarm.getOwner()) {
			log.error("propagateAlarm", "alarm from:" + alarm.getApId()
					+ " has no owner.");
			return;
		}

		String hiveApMac = alarm.getApId();
		Long domainId = alarm.getOwner().getId();
		short newSeverity = AhAlarm.AH_SEVERITY_UNDETERMINED;
		List<?> list = QueryUtil.executeQuery("select max(severity) from "
				+ AhAlarm.class.getSimpleName(), null, new FilterParams("apId",
				hiveApMac), alarm.getOwner().getId());

		Object[] obj = list.toArray();
		if (null != obj && obj.length == 1 && null != obj[0]) {
			newSeverity = (Short) obj[0];
		}
		HiveAp mergedHiveAp = null;

		try {
			// Update the latest severity which is different from that in
			// HiveAp.
			log.debug("propagateAlarm", "Propagating New alarm to HiveAp: "
					+ hiveApMac + " with new severity: " + newSeverity);
			mergedHiveAp = BoMgmt.getHiveApMgmt().updateHiveApSeverity(
					hiveApMac, domainId, newSeverity);
		} catch (Exception e) {
			log.error("propagateAlarm", "Update HiveAp severity failed.", e);
		}

		if (mergedHiveAp == null) {
			return;
		}

		MapLeafNode leafNode = mergedHiveAp.findMapLeafNode();

		if (leafNode == null || leafNode.getSeverity() == newSeverity) {
			return;
		}

		try {
			// Propagate the latest severity up to the whole map hierarchy.
			Collection<HmBo> mergedHmBos = updateLeafNodeSeverity(
					leafNode.getId(), newSeverity, this);

			if (null != mergedHmBos) {
				for (HmBo mapNode : mergedHmBos) {
					// Notify MapAlarmsCache objects
					if (((MapNode) mapNode).isLeafNode()) {
						BoObserver.notifyListeners(new BoEvent<MapLeafNode>(
								(MapLeafNode) mapNode, BoEventType.UPDATED));
					} else {
						MapContainerNode mapContainerNode = (MapContainerNode) mapNode;
						// for MapHierarchyCache
						mapContainerNode.setMapType((short) -1);
						// Use notifyListeners instead of publishBoEvent
						BoObserver
								.notifyListeners(new BoEvent<MapContainerNode>(
										mapContainerNode, BoEventType.UPDATED));
					}
				}
			}
		} catch (Exception e) {
			log.error("propagateAlarm", "Propagate new alarm with severity "
					+ newSeverity + " up to the whole map hierarchy failed.", e);
		}
	}

	public Collection<HmBo> propagateAlarm(MapLeafNode mapLeafNode) {
		Collection<HmBo> mapTrail = new Vector<HmBo>();
		MapContainerNode parentMap = mapLeafNode.getParentMap();
		while (!ROOT_MAP_NAME.equals(parentMap.getMapName())) {
			short parentSeverity = findHighestSeverity(parentMap);
			log.debug("propagateAlarm",
					"Propagating severity: " + parentSeverity
							+ " to parent map node: " + parentMap.getLabel());
			parentMap.setSeverity(parentSeverity);
			mapTrail.add(parentMap);
			parentMap = parentMap.getParentMap();
		}
		return mapTrail;
	}

	protected short findHighestSeverity(MapContainerNode mapContainerNode) {
		short severity = AhAlarm.AH_SEVERITY_UNDETERMINED;
		for (MapNode childNode : mapContainerNode.getChildNodes()) {
			if (childNode.getSeverity() > severity) {
				severity = childNode.getSeverity();
			}
		}
		return severity;
	}

	public List<CheckItem> getMapListView(Long domainId) {
		return getMapListView(domainId, null);
	}

	public List<CheckItem> getMapListView(Long domainId,
			Collection<Long> includeOnly) {
		MapContainerNode rootMap = getVHMRootMap(domainId);
		List<CheckItem> childrenMap = new ArrayList<CheckItem>();
		buildSubMap(childrenMap, rootMap, 0, domainId, false, includeOnly,
				false, false);
		List<CheckItem> totalMap = new ArrayList<CheckItem>();

		if (!MapMgmt.VHM_ROOT_MAP_NAME.equals(rootMap.getLabel())) {
			totalMap.add(new CheckItem(rootMap.getId(), rootMap.getLabel()));
		}

		addChildrenIndent(totalMap, childrenMap);
		return totalMap;
	}

	public List<CheckItem> getMapListView(Long domainId,
			Collection<Long> includeOnly, boolean allNode) {
		MapContainerNode rootMap = getVHMRootMap(domainId);
		List<CheckItem> childrenMap = new ArrayList<CheckItem>();
		buildSubMap(childrenMap, rootMap, 0, domainId, false, includeOnly,
				false, allNode);
		List<CheckItem> totalMap = new ArrayList<CheckItem>();

		if (!MapMgmt.VHM_ROOT_MAP_NAME.equals(rootMap.getLabel())) {
			totalMap.add(new CheckItem(rootMap.getId(), rootMap.getLabel()));
		}

		addChildrenIndent(totalMap, childrenMap);
		return totalMap;
	}

	public List<CheckItem> getParentMapList(Long domainId, boolean orderFolders) {
		MapContainerNode rootMap = getVHMRootMap(domainId);
		List<CheckItem> parentMaps = new ArrayList<CheckItem>();
		parentMaps.add(new CheckItem(rootMap.getId(), rootMap.getLabel()));
		buildSubMap(parentMaps, rootMap, 1, domainId, orderFolders, null, true,
				false);
		return parentMaps;
	}

	private void addChildrenIndent(List<CheckItem> map, List<CheckItem> subMap) {
		if (map == null || subMap == null) {
			return;
		}

		for (CheckItem item : subMap) {
			map.add(addItemIndent(item));
		}
	}

	private CheckItem addItemIndent(CheckItem item) {
		if (item == null) {
			return null;
		}

		String newLabel;
		String value = item.getValue();

		if (!value.contains("|_")) {
			/*
			 * add '|_' at the beginning of the value
			 */
			newLabel = "|_" + value;
		} else {
			/*
			 * insert '_' after the '|_' in the value
			 */
			newLabel = "|_ _" + value.substring("|_".length());
		}

		return new CheckItem(item.getId(), newLabel);
	}

	private void buildSubMap(List<CheckItem> maps,
			final MapContainerNode currentNode, int level, Long domainId,
			final boolean orderFolders, Collection<Long> includeOnly,
			boolean containersOnly, boolean allNode) {
		// order by id
		Set<MapNode> children = currentNode.getChildNodes();
		List<MapNode> list = MapNodeUtil.sortMapTree(children, orderFolders, currentNode,true);
		level++;
		for (MapNode mapNode : list) {
			if (mapNode instanceof MapContainerNode) {
				if (mapNode.getOwner().getId().equals(domainId)) {
					if (includeOnly == null
							|| includeOnly.contains(mapNode.getId())) {
						CheckItem item;
						if (allNode) {
							item = new CheckItem(mapNode.getId(),
									getIndent(level) + mapNode.getLabel());
						} else {
							if (((MapContainerNode) mapNode).getMapType() == 1) {
								item = new CheckItem(mapNode.getId(),
										getIndent(level) + mapNode.getLabel());
							} else {
								item = new CheckItem(mapNode.getId(),
										getIndent(level - 1)
												+ currentNode.getLabel() + "_"
												+ mapNode.getLabel());
							}
						}

						if (MapMgmt.VHM_ROOT_MAP_NAME
								.equals(mapNode.getLabel())) {
							continue;
						}
						if (allNode) {
							maps.add(item);
						} else {
							if (containersOnly) {
								if (((MapContainerNode) mapNode).getMapType() == 1) {
									maps.add(item);
								}
							} else {
								if (((MapContainerNode) mapNode).getMapType() != MapContainerNode.MAP_TYPE_BUILDING) {
									maps.add(item);
								}
							}
						}
					}
					buildSubMap(maps, (MapContainerNode) mapNode, level,
							domainId, orderFolders, includeOnly,
							containersOnly, allNode);
				}
			}
		}
	}

	private String getIndent(int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < level; i++) {
			if (i == 1) {
				sb.append("|_");
			} else {
				sb.append(" _");
			}
		}
		return sb.toString();
	}

	public HiveAp createMapLeafNode(HiveAp hiveAp, MapLeafNode node,
			MapContainerNode parent) throws Exception {
		if (parent == null || hiveAp == null) {
			return hiveAp;
		}
		if (parent.getMapType() == MapContainerNode.MAP_TYPE_BUILDING) {
			throw new HmException("create map leaf node for device: "
					+ hiveAp.getId()
					+ " failed, the assigned map node cannot be a building.",
					"error.map.building.assign.device");
		}
		
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			synchronized (BoMgmt.getHiveApMgmt()) {
				// Merge HiveAp.
				hiveAp.setMapContainer(parent);
				hiveAp = em.merge(hiveAp);

				// Persist MapLeafNode.
				node.setParentMap(parent);
				node.setOwner(parent.getOwner());
				node.setHiveAp(hiveAp);
				node.setApId(hiveAp.getMacAddress());
				node.setApName(hiveAp.getHostName());
				node.setSeverity(hiveAp.getSeverity());

				em.persist(node);
			}
			tx.commit();
			BoObserver.notifyListeners(new BoEvent<MapLeafNode>(node,
					BoEventType.CREATED));
			return hiveAp;
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			log.error("createMapLeafNode", "Create MapLeafNode object failed.",
					e);
			if (e instanceof OptimisticLockException
					&& e.getCause() instanceof StaleObjectStateException) {
				throw new HmException("Update object " + hiveAp.getId()
						+ " failed, stale object state.", e,
						HmMessageCodes.STALE_OBJECT);
			} else if (e.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) e
						.getCause();
				log.info("createMapLeafNode",
						"Constraint: " + cve.getConstraintName());
				throw new HmException("Update BO '" + hiveAp.getLabel()
						+ "' failed.", e, HmMessageCodes.CONSTRAINT_VIOLATION,
						new String[] { hiveAp.getLabel(),
								cve.getConstraintName() });
			} else {
				throw e;
			}
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}

	/*
	 * Update a hiveAp, and delete corresponding mapLeafNode. This method is be
	 * used in case of update a HiveAp, deselect any topology map.
	 */
	public HiveAp removeMapLeafNode(HiveAp hiveAp) throws Exception {
		if (null == hiveAp) {
			return null;
		}

		MapLeafNode oldMapLeafNode = hiveAp.findMapLeafNode();
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			synchronized (this) {
				if (null != oldMapLeafNode) {
					oldMapLeafNode = em.find(MapLeafNode.class,
							oldMapLeafNode.getId());
				}
				removeMapLeafNode(em, oldMapLeafNode);

				synchronized (BoMgmt.getHiveApMgmt()) {
					hiveAp.setMapContainer(null);
					hiveAp = em.merge(hiveAp);
					tx.commit();
				}
			}

			if (oldMapLeafNode != null) {
				// We can not use the oldMapLeafNode object itself, because the
				// class name was enhanced by Hibernate. Just create a new node
				// and fill in the parent map, which is all that the
				// MapAlarmsCache uses.
				MapLeafNode mapLeafNode = new MapLeafNode();
				mapLeafNode.setParentMap(oldMapLeafNode.getParentMap());
				BoObserver.notifyListeners(new BoEvent<MapLeafNode>(
						mapLeafNode, BoEventType.REMOVED));
			}

			return hiveAp;
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			log.error("removeMapLeafNode", "Remove MapLeafNode object failed.",
					e);
			if (e instanceof OptimisticLockException
					&& e.getCause() instanceof StaleObjectStateException) {
				throw new HmException("Update object " + hiveAp.getId()
						+ " failed, stale object state.", e,
						HmMessageCodes.STALE_OBJECT);
			} else if (e.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) e
						.getCause();
				log.info("removeMapLeafNode",
						"Constraint: " + cve.getConstraintName());
				throw new HmException("Create BO '" + hiveAp.getLabel()
						+ "' failed.", e, HmMessageCodes.CONSTRAINT_VIOLATION,
						new String[] { hiveAp.getLabel(),
								cve.getConstraintName() });
			} else {
				throw e;
			}
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}

	/*
	 * Update a HiveAp, and remove the old mapLeafNode, add a new mapLeafNode.
	 * This method is used in case of while update a HiveAp, and change its
	 * topology map.
	 */
	public HiveAp replaceMapLeafNode(HiveAp hiveAp, MapLeafNode newNode,
			MapContainerNode newParent) throws Exception {
		if (null == hiveAp || null == newNode || null == newParent) {
			return hiveAp;
		}
		if (newParent.getMapType() == MapContainerNode.MAP_TYPE_BUILDING) {
			throw new HmException("replace map leaf node for device: "
					+ hiveAp.getId()
					+ " failed, the assigned map node cannot be a building.",
					"error.map.building.assign.device");
		}
		
		MapLeafNode oldMapLeafNode = hiveAp.findMapLeafNode();
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			synchronized (this) {
				if (null != oldMapLeafNode) {
					oldMapLeafNode = em.find(MapLeafNode.class,
							oldMapLeafNode.getId());
				}
				// Remove the old MapLeafNode.
				removeMapLeafNode(em, oldMapLeafNode);

				synchronized (BoMgmt.getHiveApMgmt()) {
					// Merge the MapContainerNode object into HiveAp.
					hiveAp.setMapContainer(newParent);
					hiveAp = em.merge(hiveAp);

					// Persist the new MapLeafNode object into database.
					newNode.setParentMap(newParent);
					newNode.setOwner(newParent.getOwner());
					newNode.setHiveAp(hiveAp);
					newNode.setApId(hiveAp.getMacAddress());
					newNode.setApName(hiveAp.getHostName());
					newNode.setSeverity(hiveAp.getSeverity());

					em.persist(newNode);
				}
				tx.commit();
			}

			// Notify MapAlarmsCache objects
			if (oldMapLeafNode != null) {
				// We can not use the oldMapLeafNode object itself, because
				// the
				// class name was enhanced by Hibernate. Just create a new
				// node
				// and fill in the parent map, which is all that the
				// MapAlarmsCache uses.
				MapLeafNode mapLeafNode = new MapLeafNode();
				mapLeafNode.setParentMap(oldMapLeafNode.getParentMap());
				BoObserver.notifyListeners(new BoEvent<MapLeafNode>(
						mapLeafNode, BoEventType.REMOVED));
			}
			BoObserver.notifyListeners(new BoEvent<MapLeafNode>(newNode,
					BoEventType.CREATED));
			return hiveAp;
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			log.error("replaceMapLeafNode",
					"Replace MapLeafNode object failed.", e);
			if (e instanceof OptimisticLockException
					&& e.getCause() instanceof StaleObjectStateException) {
				throw new HmException("Update object " + hiveAp.getId()
						+ " failed, stale object state.", e,
						HmMessageCodes.STALE_OBJECT);
			} else if (e.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) e
						.getCause();
				log.info("replaceMapLeafNode",
						"Constraint: " + cve.getConstraintName());
				throw new HmException("Create BO '" + hiveAp.getLabel()
						+ "' failed.", e, HmMessageCodes.CONSTRAINT_VIOLATION,
						new String[] { hiveAp.getLabel(),
								cve.getConstraintName() });
			} else {
				throw e;
			}
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}

	/*
	 * Create the passed HiveAP object(contains synchronizing its mapLeafNode &
	 * mapContainer) and propagate the managed status.
	 */
	public HiveAp createHiveApWithPropagation(HiveAp hiveAp,
			MapContainerNode newParent) throws Exception {
		if (null == hiveAp) {
			return null;
		}
		// Create HiveAP without MapContainer reference
		hiveAp.setMapContainer(null);
		Long hiveApId = QueryUtil.createBo(hiveAp);
		if (hiveAp.getManageStatus() == HiveAp.STATUS_NEW) {
			// Send new HiveAp count to System Cache.
			SystemStatusCache.getInstance().incrementNewHiveApCount(
					hiveAp.getOwner().getId());
		}
		// propagate the new HiveAP to Cache.
		CacheMgmt.getInstance().addSimpleHiveAp(hiveAp);

		hiveAp.setId(hiveApId);
		if (newParent != null) {
			MapLeafNode leafNode = new MapLeafNode();
			placeIcon(newParent, leafNode);
			leafNode.setIconName(MapMgmt.BASE_LEAFNODE_ICON);
			// To make sure that MapContainer reference and creating leaf
			// node is done in the same transaction.
			hiveAp = createMapLeafNode(hiveAp, leafNode, newParent);

			// propagate to Map Hierarchy Cache;
			BoMgmt.getMapHierarchyCache().hiveApAdded(hiveAp);
			// propagate the updated HiveAP to Cache.
			CacheMgmt.getInstance().updateSimpleHiveAp(hiveAp);
		}

		return hiveAp;
	}

	/*
	 * Update the passed HiveAP object(contains synchronizing its mapLeafNode &
	 * mapContainer) and propagate the managed status, connection status into
	 * cache.
	 */
	public HiveAp updateHiveApWithPropagation(HiveAp hiveAp,
			MapContainerNode newParent, boolean oldConnection,
			short oldManagedStatus) throws Exception {
		if (null == hiveAp) {
			return null;
		}
		MapLeafNode mapLeafNode = hiveAp.findMapLeafNode();
		HiveAp updatedHiveAp;
		Long oldContainerId = (null == mapLeafNode || null == mapLeafNode
				.getParentMap()) ? null : mapLeafNode.getParentMap().getId();

		if (null == newParent && null == mapLeafNode) {
			// not assign a map now and before. just update HiveAp self.
			updatedHiveAp = updateHiveAp(hiveAp);
		} else if (null == newParent) {
			// not assign a map now, but before assigned a map.
			updatedHiveAp = removeMapLeafNode(hiveAp);
		} else if (null == mapLeafNode) {
			// assigned a map now, but before not assigned.
			MapLeafNode leafNode = new MapLeafNode();
			placeIcon(newParent, leafNode);
			leafNode.setIconName(MapMgmt.BASE_LEAFNODE_ICON);
			leafNode.setSeverity(hiveAp.getSeverity());
			updatedHiveAp = createMapLeafNode(hiveAp, leafNode, newParent);
		} else if (!(newParent.getId().equals(mapLeafNode.getParentMap()
				.getId()))) {
			// both has a map now and before, but map is changed.
			MapLeafNode leafNode = new MapLeafNode();
			placeIcon(newParent, leafNode);
			leafNode.setIconName(MapMgmt.BASE_LEAFNODE_ICON);
			leafNode.setSeverity(hiveAp.getSeverity());
			updatedHiveAp = replaceMapLeafNode(hiveAp, leafNode, newParent);
		} else {
			updatedHiveAp = updateHiveAp(hiveAp);
		}
		// propagate to Map Hierarchy Cache;
		if (null != updatedHiveAp) {
			BoMgmt.getMapHierarchyCache().hiveApUpdated(updatedHiveAp);
			// propagate to System Cache;
			short newManagedStatus = updatedHiveAp.getManageStatus();
			if (newManagedStatus != HiveAp.STATUS_NEW
					&& oldManagedStatus == HiveAp.STATUS_NEW) {
				SystemStatusCache.getInstance().decrementNewHiveApCount(
						updatedHiveAp.getOwner().getId());
			}
			// propagate the updated HiveAP to Cache.
			CacheMgmt.getInstance().updateSimpleHiveAp(hiveAp);
		}
		return updatedHiveAp;
	}

	private Long getMapContainerId(HiveAp hiveAp) {
		Long containerId = null;
		if (null != hiveAp && null != hiveAp.findMapLeafNode()
				&& null != hiveAp.findMapLeafNode().getParentMap()) {
			containerId = hiveAp.findMapLeafNode().getParentMap().getId();
		}
		return containerId;
	}

	/*
	 * Update hiveAp and propagate the host name to leafNode if needed.
	 */
	public HiveAp updateHiveAp(HiveAp hiveAp) throws Exception {
		if (null == hiveAp) {
			return null;
		}

		EntityManager em = null;
		EntityTransaction tx = null;
		MapLeafNode mapLeafNode = hiveAp.findMapLeafNode();

		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			synchronized (this) {
				// Merge MapLeafNode object.
				if (null != mapLeafNode) {
					mapLeafNode = em.find(MapLeafNode.class,
							mapLeafNode.getId());
				}

				if (null != mapLeafNode
						&& !hiveAp.getHostName()
								.equals(mapLeafNode.getApName())) {
					mapLeafNode.setApName(hiveAp.getHostName());
					em.merge(mapLeafNode);
				}

				synchronized (BoMgmt.getHiveApMgmt()) {
					// Merge HiveAp object.
					HiveAp mergedHiveAp = em.merge(hiveAp);
					tx.commit();

					return mergedHiveAp;
				}
			}
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			log.error("updateHiveAp", "Update HiveAp object failed.", e);
			if (e instanceof OptimisticLockException
					&& e.getCause() instanceof StaleObjectStateException) {
				throw new HmException("Update object " + hiveAp.getId()
						+ " failed, stale object state.", e,
						HmMessageCodes.STALE_OBJECT);
			} else if (e.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) e
						.getCause();
				log.info("updateHiveAp",
						"Constraint: " + cve.getConstraintName());
				throw new HmException("Create BO '" + hiveAp.getLabel()
						+ "' failed.", e, HmMessageCodes.CONSTRAINT_VIOLATION,
						new String[] { hiveAp.getLabel(),
								cve.getConstraintName() });
			} else {
				throw e;
			}
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}
	
	public int removeHiveAps(Collection<Long> ids, boolean resetFlag) throws Exception {
		return removeHiveApsReturnRemovedIds(ids, false, false, null, resetFlag).size();
	}

	public int removeHiveAps(Collection<Long> ids) throws Exception {
		return removeHiveAps(ids, false);
		//return removeHiveApsReturnRemovedIds(ids, false, false, null).size();
	}

	private Set<Long> removeDeviceFromLSandRedirector(Collection<Long> ids,List<String> resetConfigError,
			boolean removedFromLS, boolean removeFromRedirector, String vhmIdPa, boolean resetFlag) throws ClientHandlerException {
		Map<String, Long> serialNumberIdsMap = new HashMap<String, Long>();
		Set<Long> trueRemoveIds = new HashSet<Long>();

		try {
			List<HiveAp> removedApstats = QueryUtil.executeQuery(HiveAp.class,
					null, new FilterParams("id", ids));
			String vhmId="home";
			if (vhmIdPa==null) {
				HmUser us = BaseAction.getSessionUserContext();
				if (us!=null) {
					vhmId = us.getSwitchDomain() == null ? us.getDomain()
							.getVhmID() : us.getSwitchDomain().getVhmID();
					if (vhmId==null || vhmId.isEmpty()) {
						vhmId="home";
					}
				}
			} else {
				vhmId = vhmIdPa;
			}

			List<String> serialNumbers = new ArrayList<String>();
			for (HiveAp oneAp : removedApstats) {
				Long oneItemId = oneAp.getId();
				boolean oneItemSimu = oneAp.isSimulated();
				boolean oneItemConn = oneAp.isConnected();
				boolean oneItemPreconfig = oneAp.getManageStatus()==HiveAp.STATUS_PRECONFIG ? true: false;
				String oneItemSn = oneAp.getSerialNumber()==null? "": oneAp.getSerialNumber();

				serialNumberIdsMap.put(oneItemSn, oneItemId);
				if (!oneItemSimu && oneItemConn && resetFlag) {
					String cli = AhCliFactory.getResetDeviceToDefaultCli();
					try {
						BeCliEvent cliRequest = new BeCliEvent();
						int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
						cliRequest.setAp(oneAp);
						cliRequest.setClis(new String[] { cli });
						cliRequest.setSequenceNum(sequenceNum);
						cliRequest.buildPacket();
						int ret = AhAppContainer.getBeCommunicationModule().sendRequest(cliRequest);
						log.error("remove device reset config result:" + ret);
					} catch (Exception e) {
						log.error("removeHiveAps", "remove device reset config error! reason:", e);
					}
//					BeTopoModuleUtil.sendSyncCliRequest(oneAp,
//							new String[] { cli }, BeCliEvent.CLITYPE_NORMAL, 5);
				}
				if (!oneItemSimu && !oneItemPreconfig && !oneItemConn && !oneItemSn.isEmpty() && resetFlag) {
					resetConfigError.add(oneItemSn);
				}
				if (!oneItemSimu && !oneItemSn.isEmpty()) {
					serialNumbers.add(oneItemSn);
					if (!NmsUtil.isHostedHMApplication() || (!removedFromLS && !removeFromRedirector)) {
						trueRemoveIds.add(oneItemId);
					}
				} else {
					trueRemoveIds.add(oneItemId);
				}
			}

			if (NmsUtil.isHostedHMApplication()) {
				SerialNumbers sns = new SerialNumbers();
				sns.setVhmid(vhmId);
				sns.setSn(serialNumbers);
				List<SerialNumberList> retSerialNumbers = null;
				if (removedFromLS) {
					LicenseResUtils lu = ClientUtils.getLicenseResUtils();
					if (!sns.getSn().isEmpty()) {
						retSerialNumbers = lu.removeSerialNumbers(sns);
						sns.getSn().clear();
						for (SerialNumberList lst : retSerialNumbers) {
							if (lst.getStatus() == ModelConstant.SN_SUCCESS) {
								sns.getSn().addAll(lst.getSn());
								if (lst.getSn() != null && !removeFromRedirector) {
									for (String sn : lst.getSn()) {
										trueRemoveIds.add(serialNumberIdsMap
												.get(sn));
									}
								}
							}
							if (lst.getStatus() == ModelConstant.SN_NOTEXIST) {
								sns.getSn().addAll(lst.getSn());
								if (lst.getSn() != null && !removeFromRedirector) {
									for (String sn : lst.getSn()) {
										trueRemoveIds.add(serialNumberIdsMap
												.get(sn));
									}
								}
							}
						}
					}
				}
				
				if (removeFromRedirector) {
					if (!sns.getSn().isEmpty()) {
						RedirectorResUtils ru = ClientUtils
								.getRedirectorResUtils();
						retSerialNumbers = ru.removeSerialNumbers(sns);

						for (SerialNumberList lst : retSerialNumbers) {
							if (lst.getStatus() == ModelConstant.SN_SUCCESS) {
								if (lst.getSn() != null) {
									for (String sn : lst.getSn()) {
										trueRemoveIds.add(serialNumberIdsMap
												.get(sn));
									}
								}
							}
							if (lst.getStatus() == ModelConstant.SN_NOTEXIST) {
								if (lst.getSn() != null) {
									for (String sn : lst.getSn()) {
										trueRemoveIds.add(serialNumberIdsMap
												.get(sn));
									}
								}
							}
						}
					}
				}
			}
		} catch (ClientHandlerException ex) {
			log.error(ex);
			throw ex;
		} catch (Exception e) {
			log.error(e);
		}

		return trueRemoveIds;
	}

	public Collection<Long> removeHiveApsReturnRemovedIds(Collection<Long> ids,
			boolean removedFromLS, boolean removeFromRedirector, String vhmId, boolean resetFlag) throws Exception {
		Collection<Long> retRemovedList = new ArrayList<Long>();
		if (ids == null || ids.isEmpty()) {
			return retRemovedList;
		}

		// int count = 0;
		HiveAp hiveAp = null;
		// initialize a list to store affected map container;
		Map<Long, MapLeafNode> parent_leafNode = new HashMap<Long, MapLeafNode>();
		// initialize a list to store affected hiveAP;
		List<HiveAp> hiveAps = new ArrayList<HiveAp>();
		EntityManager em = null;
		EntityTransaction tx = null;
		Map<Long, String> removeIds = new HashMap<Long, String>();

		synchronized (this) {
			synchronized (BoMgmt.getHiveApMgmt()) {
				List<MapLeafNode> leafs = QueryUtil.executeQuery(
						MapLeafNode.class, null, new FilterParams("hiveAp.id",
								ids), null, new QueryLazyBo());
				Map<String, MapLeafNode> map = new HashMap<String, MapLeafNode>();
				for (MapLeafNode leaf : leafs) {
					map.put(leaf.getApId(), leaf);
				}

				Collection<Long> trueRemoveIds;
				List<String> resetConfigError = new ArrayList<String>();
				if (removedFromLS || removeFromRedirector) {
					trueRemoveIds = removeDeviceFromLSandRedirector(ids,resetConfigError, removedFromLS, removeFromRedirector, vhmId, resetFlag);
				} else {
					trueRemoveIds = removeDeviceFromLSandRedirector(ids,resetConfigError, false, false, vhmId, resetFlag);;
				}
				try {
					em = QueryUtil.getEntityManager();
					tx = em.getTransaction();
					tx.begin();

					for (Object id : trueRemoveIds) {
						// hiveAp = em.getReference(HiveAp.class, id);
						// we cannot use getReference() because the class name
						// was enhanced by Hibernate.
						hiveAp = em.find(HiveAp.class, id);
						if (null == hiveAp) {
							continue;
						}
						MapLeafNode mapLeafNode;
						removeIds.put(hiveAp.getId(), hiveAp.getLabel());
						mapLeafNode = map.get(hiveAp.getMacAddress());
						if (null != mapLeafNode) {
							mapLeafNode = em.getReference(MapLeafNode.class,
									mapLeafNode.getId());
						}
						removeMapLeafNode(em, mapLeafNode);

						if (null != mapLeafNode
								&& !parent_leafNode.containsKey(mapLeafNode
										.getParentMap().getId())) {
							// We can not use the mapLeafNode object itself,
							// because the class name was enhanced by Hibernate.
							// Just create a new node and fill in the parent
							// map, which is all that the MapAlarmsCache uses.
							MapLeafNode newLeafNode = new MapLeafNode();
							newLeafNode
									.setParentMap(mapLeafNode.getParentMap());
							parent_leafNode.put(mapLeafNode.getParentMap()
									.getId(), newLeafNode);
						}
						hiveAps.add(hiveAp);

						// remove VPN gateway setting.
						if (hiveAp.isVpnGateway()) {
							// Query query
							// =em.createNativeQuery("DELETE FROM VPN_GATEWAY_SETTING WHERE hiveapid = "
							// + id);
							// query.executeUpdate();

							Query jpQuery = QueryUtil
									.createQuery(
											em,
											"select distinct bo from "
													+ VpnService.class
															.getSimpleName()
													+ " as bo join bo.vpnGateWaysSetting as joined",
											null, new FilterParams(
													"joined.hiveApId", id));
							List<?> vpnServices = jpQuery.getResultList();
							for (Object obj : vpnServices) {
								VpnService vpnService = (VpnService) obj;

								for (Iterator<VpnGatewaySetting> iter = vpnService
										.getVpnGateWaysSetting().iterator(); iter
										.hasNext();) {
									if (iter.next().getApId().equals(id)) {
										iter.remove();
									}
								}
								em.merge(vpnService);
							}
						}

						// remove vpn user
						Query query = QueryUtil.createQuery(
								em,
								"select macAddress from  "
										+ HiveAp.class.getSimpleName(), null,
								new FilterParams("id", id));
						List<?> apMac = query.getResultList();
						String cuApMac = null;
						if (!apMac.isEmpty() && apMac.get(0) != null) {
							cuApMac = apMac.get(0).toString();
						}
						if (cuApMac != null && !cuApMac.equals("")) {
							query = QueryUtil
									.createQuery(
											em,
											"select distinct bo from "
													+ VpnService.class
															.getSimpleName()
													+ " as bo join bo.vpnCredentials as joined",
											null, new FilterParams(
													"joined.assignedClient",
													cuApMac));
							List<?> vpnServices = query.getResultList();
							for (Object obj : vpnServices) {
								VpnService vpnService = (VpnService) obj;
								List<VpnServiceCredential> vpnCredentials = vpnService
										.getVpnCredentials();

								for (VpnServiceCredential vpnServiceCredential : vpnCredentials) {
									String assignedClient = vpnServiceCredential
											.getAssignedClient();
									if (cuApMac.equals(assignedClient)) {
										vpnServiceCredential
												.setAssignedClient(null);
										vpnServiceCredential
												.setAllocated(false);
										vpnServiceCredential
												.setPrimaryRole(VpnServiceCredential.SERVER_ROLE_NONE);
										vpnServiceCredential
												.setBackupRole(VpnServiceCredential.SERVER_ROLE_NONE);
									}
								}
								em.merge(vpnService);
							}

							String updateClause = QueryUtil
									.getUpdateClause(
											SubNetworkResource.class,
											"status = :s1, hiveApMgtx = :s2, hiveApMac = :s3",
											"hiveApMac = :s4", null);
							query = em.createQuery(updateClause);
							QueryUtil
									.addUpdateParameters(
											query,
											new Object[] {
													SubNetworkResource.IP_SUBBLOCKS_STATUS_FREE,
													(short) -1, "", cuApMac },
											null);
							query.executeUpdate();

							// remove HiveAp clear upload staged status.
							String updateStaged = QueryUtil.getUpdateClause(
									HiveApUpdateResult.class,
									"result = :s1, stagedTime = :s2",
									"nodeId = :s3 and result = :s4", null);
							query = em.createQuery(updateStaged);
							QueryUtil.addUpdateParameters(query, new Object[] {
									UpdateParameters.UPDATE_ABORT, 0, cuApMac,
									UpdateParameters.UPDATE_STAGED }, null);
							query.executeUpdate();

							// remove map between AP and OTP
							Query queryOtp = QueryUtil.createQuery(
									em,
									"select id from  "
											+ OneTimePassword.class
													.getSimpleName(), null,
									new FilterParams("macAddress", cuApMac));
							List<?> otpList = queryOtp.getResultList();
							if (!otpList.isEmpty() && otpList.get(0) != null) {
								String updateOtpClause = QueryUtil
										.getUpdateClause(
												OneTimePassword.class,
												"macAddress = :s1,deviceModel = :s2,hiveApAutoProvision = :s3",
												"id = :s4", null);
								query = em.createQuery(updateOtpClause);
								QueryUtil.addUpdateParameters(
										query,
										new Object[] {
												null,
												(short) -1,
												null,
												Long.valueOf(otpList.get(0)
														.toString()) }, null);
								query.executeUpdate();
							}
						}

						em.remove(hiveAp);

						retRemovedList.add(Long.valueOf(id.toString()));
						// count++;
					}

					tx.commit();
					
					for (MapLeafNode mapLeafNode : parent_leafNode.values()) {
						BoObserver.notifyListeners(new BoEvent<MapLeafNode>(
								mapLeafNode, BoEventType.REMOVED));
					}
					for (HiveAp removedHiveAp : hiveAps) {
						BoMgmt.getBoEventMgmt().publishBoEvent(
								new BoEvent<HiveAp>(removedHiveAp,
										BoEventType.REMOVED));
					}
					
					// save reset config error device
					List<DeviceResetConfig> lstReset = new ArrayList<DeviceResetConfig>();
					for(HiveAp removedHiveAp : hiveAps) {
						if (resetConfigError.contains(removedHiveAp.getSerialNumber())) {
							DeviceResetConfig drc = new DeviceResetConfig();
							drc.setOwner(removedHiveAp.getOwner());
							drc.setSerialNumber(removedHiveAp.getSerialNumber());
							drc.setTimestamp(System.currentTimeMillis());
							lstReset.add(drc);
						}
					}
					if (!lstReset.isEmpty()) {
						QueryUtil.restoreBulkCreateBos(lstReset);
					}
				} catch (RuntimeException e) {
					QueryUtil.rollback(tx);
					log.error("removeHiveAps", "Remove HiveAp(s) failed.", e);
					if (e instanceof PersistenceException
							&& (e.getCause() instanceof ConstraintViolationException || (e
									.getCause() != null && e.getCause()
									.getCause() instanceof ConstraintViolationException))
							&& hiveAp != null) {
						String errLabel;
						try {
							String errMsg = e.getCause().getMessage();
							Long id = Long.parseLong(errMsg.substring(
									errMsg.indexOf("#") + 1,
									errMsg.length() - 1));
							errLabel = removeIds.get(id);
							if (errLabel == null) {
								errLabel = hiveAp.getLabel();
							}
						} catch (Exception exc) {
							errLabel = hiveAp.getLabel();
						}
						throw new HmException("Remove object " + hiveAp.getId()
								+ " failed, stale object state.", e,
								HmMessageCodes.OBJECT_IN_USE,
								new String[] { errLabel });
					} else {
						throw e;
					}
				} finally {
					QueryUtil.closeEntityManager(em);
				}
			}
		}

		return retRemovedList;
	}

	private void removeMapLeafNode(EntityManager em, MapLeafNode mapLeafNode) {
		if (mapLeafNode == null) {
			return;
		}
		removeRelatedMapLinks(em, mapLeafNode);
		// Remove the MapLeafNode.
		em.remove(mapLeafNode);
	}

	private void removeRelatedMapLinks(EntityManager em, MapLeafNode mapLeafNode) {
		// Remove the map leaf node and all the links involving this node
		MapContainerNode mapContainerNode = mapLeafNode.getParentMap();
		Map<String, MapLink> childLinks = mapContainerNode.getChildLinks();

		// Remove all the related MapLink objects of which either the FromNode
		// or ToNode is equals to the MapLeafNode object specified as the
		// argument{mapLeafNode} out of the MapContainerNode.
		if (childLinks == null || childLinks.isEmpty()) {
			return;
		}

		Collection<MapLink> mapLinks = childLinks.values();
		Collection<MapLink> removeList = new Vector<MapLink>();
		Collection<MapLeafNode> foreignList = new Vector<MapLeafNode>();
		for (MapLink mapLink : mapLinks) {
			MapLeafNode fromNode = mapLink.getFromNode();
			MapLeafNode toNode = mapLink.getToNode();
			if (fromNode.getId().equals(mapLeafNode.getId())) {
				removeList.add(mapLink);
				// check the other side leafNode is foreign node?
				if (toNode.getApId().charAt(0) == 'M') {
					foreignList.add(toNode);
				}
			} else if (toNode.getId().equals(mapLeafNode.getId())) {
				removeList.add(mapLink);
				// check the other side leafNode is foreign node?
				if (fromNode.getApId().charAt(0) == 'M') {
					foreignList.add(fromNode);
				}
			}
		}

		// check whether the foreign node is just link this removed leafNode, if
		// so, remove this foreign node too.
		Collection<MapNode> removeForeignList = new Vector<MapNode>();

		for (MapLeafNode foreignNode : foreignList) {
			boolean linkedOtherNodes = false;
			for (MapLink mapLink : mapLinks) {
				MapLeafNode fromNode = mapLink.getFromNode();
				MapLeafNode toNode = mapLink.getToNode();
				if ((fromNode.getId().equals(foreignNode.getId()) && !(toNode
						.getId().equals(mapLeafNode.getId())))
						|| (toNode.getId().equals(foreignNode.getId()) && !(fromNode
								.getId().equals(mapLeafNode.getId())))) {
					linkedOtherNodes = true;
					break;
				}
			}
			if (!linkedOtherNodes) {
				removeForeignList.add(foreignNode);
			}
		}

		if (!removeList.isEmpty()) {
			mapContainerNode.removeLinks(removeList);
			// Update MapContainerNode will remove links
			em.merge(mapContainerNode);
		}

		for (MapNode foreignNode : removeForeignList) {
			em.remove(foreignNode);
		}
	}

	public void placeIcon(MapContainerNode mapContainerNode, MapNode mapNode) {
		// Line up new nodes near the top of the parent map
		// Note that height could be 0 if mapContainerNode is a group node (with
		// no dimensions)
		mapNode.setX(mapContainerNode.getHeight() * 0.2 + Math.random()
				* mapContainerNode.getHeight() * 0.6);
		mapNode.setY(mapContainerNode.getWidth() * 0.03);
	}

	public MapContainerNode updateMapContainerLinks(Long containerId,
			Map<String, MapLink> childLinks, QueryBo queryBo) throws Exception {
		synchronized (this) {
			MapContainerNode container = QueryUtil.findBoById(
					MapContainerNode.class, containerId);
			if (null == container) {
				return null;
			}
			container.setChildLinks(childLinks);
			Collection<HmBo> nodes = QueryUtil.updateBo(container, queryBo);
			if (null == nodes || nodes.isEmpty()) {
				return null;
			}
			return (MapContainerNode) nodes.iterator().next();
		}
	}

	/*
	 * Generally create map container.
	 */
	public Long createMapContainer(MapContainerNode container,
			MapContainerNode peer) throws Exception {
		synchronized (this) {
			int maxOrder = peer == null ? 0 : peer.getMapOrder() - 1;
			Collection<MapContainerNode> floors = new Vector<MapContainerNode>();
			for (MapNode mapNode : container.getParentMap().getChildNodes()) {
				if (mapNode.isLeafNode()) {
					continue;
				}
				MapContainerNode map = (MapContainerNode) mapNode;
				if (peer == null) {
					if (map.getMapOrder() > maxOrder) {
						maxOrder = map.getMapOrder();
					}
				} else {
					if (map.getMapOrder() >= peer.getMapOrder()) {
						MapContainerNode floor = (MapContainerNode) QueryUtil
								.findBoById(MapNode.class, map.getId());
						floor.setMapOrder(floor.getMapOrder() + 1);
						floors.add(floor);
					}
				}
			}
			container.setMapOrder(maxOrder + 1);
			if (floors.size() > 0) {
				QueryUtil.bulkUpdateBos(floors);
			}
		}
		return QueryUtil.createBo(container);
	}

	/*
	 * Generally update map container.
	 */
	public MapContainerNode updateMapContainer(MapContainerNode container)
			throws Exception {
		synchronized (this) {
			return QueryUtil.updateBo(container);
		}
	}

	public void assignMapLeafNodeToDiscoveredHiveAp(HiveAp ap) throws Exception {
		synchronized (this) {
			synchronized (BoMgmt.getHiveApMgmt()) {
				HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class, ap.getId());
				if (hiveAp == null) {
					return;
				}
				Object[] locationAndMap = BeTopoModuleUtil
						.separateLocationAndMap(ap.getLocation(), ap.getOwner());
				String new_location = (String) locationAndMap[0];
				MapContainerNode mapContainer = (MapContainerNode) locationAndMap[1];

				// Need to update HiveAP's location.
				if (null != new_location && new_location.length() > 32) {
					log.info("assignMapLeafNodeToDiscoveredHiveAp",
							"location length is more than 32: [" + new_location
									+ "], subString the first 32 char only.");
					new_location = new_location.substring(0, 32);
				}

				if (mapContainer != null) {
					HiveAp updatedHiveAp = null;
					MapContainerNode oldMapContainer = hiveAp.getMapContainer();
					if (null == oldMapContainer) {
						// Need to create mapLeafNode of this HiveAp.
						hiveAp.setLocation(new_location);

						MapLeafNode leafNode = new MapLeafNode();
						leafNode.setIconName(MapMgmt.BASE_LEAFNODE_ICON);
						leafNode.setSeverity(hiveAp.getSeverity());

						placeIcon(mapContainer, leafNode);
						updatedHiveAp = createMapLeafNode(hiveAp, leafNode,
								mapContainer);
					} else if (!oldMapContainer.getId().equals(
							mapContainer.getId())) {
						hiveAp.setLocation(new_location);

						MapLeafNode leafNode = new MapLeafNode();
						leafNode.setIconName(MapMgmt.BASE_LEAFNODE_ICON);
						leafNode.setSeverity(hiveAp.getSeverity());

						placeIcon(mapContainer, leafNode);
						updatedHiveAp = replaceMapLeafNode(hiveAp, leafNode,
								mapContainer);
					}

					// Propagate to map hierarchy cache.
					Long oldMapContainerId = null == oldMapContainer ? null
							: oldMapContainer.getId();
					if (updatedHiveAp != null) {
						hiveAp = updatedHiveAp;
						BoMgmt.getMapHierarchyCache().hiveApUpdated(
								updatedHiveAp);
					}
					// propagate the updated HiveAP to Cache.
					CacheMgmt.getInstance().updateSimpleHiveAp(updatedHiveAp);
				}
				if (hiveAp.getLocation() != null
						&& !hiveAp.getLocation().equals(new_location)) {
					hiveAp.setLocation(new_location);
					QueryUtil.updateBo(hiveAp);
				}
			}
		}
	}

	public Collection<HmBo> updateLeafNodeSeverity(Long leafNodeId,
			short severity, QueryBo queryBo) throws Exception {
		synchronized (this) {
			MapLeafNode leafNode = QueryUtil.findBoById(MapLeafNode.class,
					leafNodeId);
			if (null != leafNode) {
				leafNode.setSeverity(severity);
				return QueryUtil.updateBo(leafNode, queryBo);
			}
			return null;
		}
	}

	public void updateLeafNodeEthId(Long leafNodeId, String ethId)
			throws Exception {
		synchronized (this) {
			// MapLeafNode leafNode = (MapLeafNode) QueryUtil.findBoById(
			// MapLeafNode.class, leafNodeId);
			// if (null != leafNode) {
			// leafNode.setEthId(ethId);
			// QueryUtil.updateBo(leafNode);
			// }

			QueryUtil.updateBos(MapLeafNode.class, "ethId = :s1", "id = :s2",
					new Object[] { ethId, leafNodeId });
		}
	}

	public void updateLeafNodePosition(Long leafNodeId, double x, double y)
			throws Exception {
		synchronized (this) {
			QueryUtil.updateBos(MapNode.class, "x = :s1, y=:s2", "id = :s3",
					new Object[] { x, y, leafNodeId });
		}
	}

	public void updateLeafNodeFetchTimeout(Long leafNodeId, boolean timeout)
			throws Exception {
		synchronized (this) {
			// MapLeafNode leafNode = (MapLeafNode) QueryUtil.findBoById(
			// MapLeafNode.class, leafNodeId);
			// if (null != leafNode) {
			// leafNode.setFetchLinksTimeout(timeout);
			// QueryUtil.updateBo(leafNode);
			// }

			QueryUtil.updateBos(MapLeafNode.class, "fetchLinksTimeout = :s1",
					"id = :s2", new Object[] { timeout, leafNodeId });
		}
	}

	public void reassignDomain(HiveAp hiveAp, ConfigTemplate newTemplate,
			HmDomain domain) throws Exception {
		if (null == hiveAp || null == domain) {
			return;
		}
		HmDomain oldDomain = hiveAp.getOwner();
		synchronized (BoMgmt.getHiveApMgmt()) {
			hiveAp = QueryUtil.findBoById(HiveAp.class, hiveAp.getId());
			if (null == hiveAp) {
				return;
			}
			// update its related Alarm & Event to another domain;
			QueryUtil.updateBos(AhEvent.class, "owner = :s1",
					"apId = :s2 AND owner.id = :s3", new Object[] { domain,
							hiveAp.getMacAddress(), oldDomain.getId() });
			List<AhAlarm> list = QueryUtil.executeQuery(AhAlarm.class, null,
					new FilterParams("apId", hiveAp.getMacAddress()),
					oldDomain.getId());
			Collection<HmBo> hmBos = new ArrayList<HmBo>(list.size() + 1);
			int cri = 0, maj = 0, min = 0, und = 0;
			if (!list.isEmpty()) {
				for (AhAlarm alarm : list) {
					short severity = alarm.getSeverity();
					switch (severity) {
					case AhAlarm.AH_SEVERITY_CRITICAL:
						cri++;
						break;
					case AhAlarm.AH_SEVERITY_MAJOR:
						maj++;
						break;
					case AhAlarm.AH_SEVERITY_MINOR:
						min++;
						break;
					case AhAlarm.AH_SEVERITY_UNDETERMINED:
						und++;
						break;
					}
					alarm.setOwner(domain);
					hmBos.add(alarm);
				}
			}
			hiveAp.setOwner(domain);
			hiveAp.setConfigTemplate(newTemplate);
			hiveAp.setCfgAdminUser(null);// set credential to null
			hiveAp.setCfgPassword(null);
			hmBos.add(hiveAp);
			QueryUtil.bulkUpdateBos(hmBos);
			HiveAp newAp = QueryUtil.findBoById(HiveAp.class, hiveAp.getId());
			SystemStatusCache.getInstance().decrementAlarmCount(cri,
					AhAlarm.AH_SEVERITY_CRITICAL, oldDomain.getId());
			SystemStatusCache.getInstance().decrementAlarmCount(maj,
					AhAlarm.AH_SEVERITY_MAJOR, oldDomain.getId());
			SystemStatusCache.getInstance().decrementAlarmCount(min,
					AhAlarm.AH_SEVERITY_MINOR, oldDomain.getId());
			SystemStatusCache.getInstance().decrementAlarmCount(und,
					AhAlarm.AH_SEVERITY_UNDETERMINED, oldDomain.getId());
			SystemStatusCache.getInstance().incrementAlarmCount(cri,
					AhAlarm.AH_SEVERITY_CRITICAL, domain.getId());
			SystemStatusCache.getInstance().incrementAlarmCount(maj,
					AhAlarm.AH_SEVERITY_MAJOR, domain.getId());
			SystemStatusCache.getInstance().incrementAlarmCount(min,
					AhAlarm.AH_SEVERITY_MINOR, domain.getId());
			SystemStatusCache.getInstance().incrementAlarmCount(und,
					AhAlarm.AH_SEVERITY_UNDETERMINED, domain.getId());

			hiveAp.setOwner(oldDomain);// reset the old domain;
			CacheMgmt.getInstance().removeSimpleHiveAp(hiveAp);
			CacheMgmt.getInstance().addSimpleHiveAp(newAp);

			short oldManagedStatus = hiveAp.getManageStatus();
			short newManagedStatus = newAp.getManageStatus();
			switch (oldManagedStatus) {
			case HiveAp.STATUS_NEW:
				SystemStatusCache.getInstance().decrementNewHiveApCount(
						oldDomain.getId());
				break;
			}
			switch (newManagedStatus) {
			case HiveAp.STATUS_NEW:
				SystemStatusCache.getInstance().incrementNewHiveApCount(
						domain.getId());
				break;
			}
			// for update reassign hiveap network device history
			try {
				QueryUtil
						.executeNativeUpdate("update network_device_history set owner="
								+ newAp.getOwner().getId()
								+ " where mac='"
								+ newAp.getMacAddress() + "'");
			} catch (Exception e) {
				log.error("update reassign hiveap network device history error: "
						+ e);
			}

		}
	}

}