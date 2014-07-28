package com.ah.bo.mgmt.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.events.BoEventFilter;
import com.ah.events.BoEventListener;
import com.ah.events.impl.BoObserver;
import com.ah.ui.actions.monitor.CurrentUserCache;
import com.ah.util.Tracer;

public class AccessControlImpl extends AccessControl implements BoEventListener<MapContainerNode>, QueryBo {

	private static final Tracer log = new Tracer(AccessControlImpl.class
			.getSimpleName());

	private AccessControlImpl() {
	}

	private static AccessControl instance;

	public synchronized static AccessControl getInstance() {
		if (instance == null) {
			instance = new AccessControlImpl();
		}

		return instance;
	}

	@Override
	public synchronized void init() {
		BoObserver.addBoEventListener(this,
				new BoEventFilter<MapContainerNode>(MapContainerNode.class));
	}

	@Override
	public synchronized void destroy() {
		BoObserver.removeBoEventListener(this);
	}

	@Override
	public void boCreated(MapContainerNode mapContainerNode) {
		log.info("boCreated", "New map: " + mapContainerNode.getLabel());
		try {
			mapContainerNode = QueryUtil.findBoById(
					MapContainerNode.class, mapContainerNode.getId(), this);
			addPermissions(mapContainerNode);
			CurrentUserCache.getInstance().updateSessionAttribute(
					mapContainerNode.getOwner().getId());
		} catch (Exception e) {
			log.error("boCreated", "addPermissions failed.", e);
		}
	}

	@Override
	public void boUpdated(MapContainerNode mapContainerNode) {
		log.info("boUpdated", "Ignored: map updated: " + mapContainerNode.getLabel());
	}

	@Override
	public void boRemoved(MapContainerNode mapContainerNode) {
		log.info("boRemoved", "Map removed: " + mapContainerNode.getLabel());
		try {
			removePermissions(mapContainerNode);
		} catch (Exception e) {
			log.error("boRemoved", "removePermissions failed.", e);
		}
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HmUserGroup) {
			HmUserGroup userGroup = (HmUserGroup) bo;

			if (userGroup.getFeaturePermissions() != null) {
				userGroup.getFeaturePermissions().size();
			}
			
			if (userGroup.getInstancePermissions() != null) {
				userGroup.getInstancePermissions().size();
			}
		}
		if (bo instanceof MapContainerNode) {
			MapContainerNode mapContainerNode = (MapContainerNode) bo;
			if (mapContainerNode.getParentMap()!=null) {
				mapContainerNode.getParentMap().getId();
			}
		}

		return null;
	}

	protected void addPermissions(MapContainerNode mapContainerNode)
			throws Exception {
		Long parentMapId = mapContainerNode.getParentMap().getId();
		log.info("addPermissions", "Parent map ID is: " + parentMapId);
		for (Long boId : getRelevantUserGroupIds(mapContainerNode)) {
			HmUserGroup hmUserGroup = QueryUtil.findBoById(
					HmUserGroup.class, boId, this);
			if (hmUserGroup.isAdministrator()) {
				continue;
			}
			HmPermission parentMapPermission = hmUserGroup
					.getInstancePermissions().get(parentMapId);
			if (parentMapPermission != null) {
				HmPermission mapPermission = new HmPermission();
				mapPermission.setOperations((short) 0);
				if (parentMapPermission.hasAccess(HmPermission.OPERATION_READ)) {
					mapPermission.addOperation(HmPermission.OPERATION_READ);
				}
				if (parentMapPermission.hasAccess(HmPermission.OPERATION_WRITE)) {
					mapPermission.addOperation(HmPermission.OPERATION_WRITE);
				}
				hmUserGroup.getInstancePermissions().put(
						mapContainerNode.getId(), mapPermission);
				hmUserGroup = QueryUtil.updateBo(hmUserGroup);
			}
		}
	}

	protected void removePermissions(MapContainerNode mapContainerNode)
			throws Exception {
		for (Long boId : getRelevantUserGroupIds(mapContainerNode)) {
			HmUserGroup hmUserGroup = QueryUtil.findBoById(
					HmUserGroup.class, boId, this);
			if (hmUserGroup.getInstancePermissions().remove(
					mapContainerNode.getId()) != null) {
				hmUserGroup = QueryUtil.updateBo(hmUserGroup);
			}
		}
	}

	private List<Long> getRelevantUserGroupIds(MapContainerNode mapContainerNode) {
		List<Long> domainIds;
		List<?> ownerIds = QueryUtil.executeQuery("select owner.id from "
				+ MapContainerNode.class.getSimpleName(), null,
				new FilterParams("id", mapContainerNode.getId()));
		if (ownerIds.isEmpty()) {
			domainIds = new ArrayList<Long>(0);
		} else {
			Long domainId = (Long) ownerIds.get(0);
			log.info("getRelevantUserGroupIds", "new map's domain id: "
					+ domainId);
			List<?> boIds = QueryUtil.executeQuery("select id from "
					+ HmUserGroup.class.getSimpleName(), null,
					new FilterParams("owner.id", domainId));
			int size = boIds.size();
			log.info("getRelevantUserGroupIds", "# of User Groups to update: "
					+ size);
			domainIds = new ArrayList<Long>(size);
			for (Object boId : boIds) {
				domainIds.add((Long) boId);
			}
		}

		return domainIds;
	}

}