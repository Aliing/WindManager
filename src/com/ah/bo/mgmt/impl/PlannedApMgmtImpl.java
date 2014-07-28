package com.ah.bo.mgmt.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.PlannedApMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.monitor.PlannedAP;
import com.ah.bo.wlan.RadioProfile;
import com.ah.util.HmException;
import com.ah.util.Tracer;

public final class PlannedApMgmtImpl implements PlannedApMgmt, QueryBo {

	private static final Tracer log = new Tracer(
			PlannedApMgmtImpl.class.getSimpleName());

	private static PlannedApMgmt instance;

	private PlannedApMgmtImpl() {
	}

	public synchronized static PlannedApMgmt getInstance() {
		if (instance == null) {
			instance = new PlannedApMgmtImpl();
		}

		return instance;
	}

	/*
	 * Create a simulated AP and return a (non-persistent) PlannedAP object.
	 */
	public PlannedAP createPlannedAP(MapContainerNode mapContainerNode,
			PlannedAP plannedAP, HmUser user, String feature) throws Exception {
		log.info("createPlannedAP",
				"mapContainerNode:" + mapContainerNode.getMapName());
		Long domainId = QueryUtil.getDependentDomainFilter(user);
		try {
			Long plannedId = BeTopoModuleUtil.sendHiveApSimulateConfigSync(
					CacheMgmt.getInstance().getCacheDomainById(domainId),
					plannedAP, mapContainerNode);
			HiveAp hiveAp = loadHiveAPById(plannedId);
			if (null != hiveAp && null != hiveAp.findMapLeafNode()) {
				return convert(hiveAp);
			}
		} catch (HmException e) {
			log.error("createPlannedAP", e);
			throw e;
		}
		return null;
	}

	/*
	 * Find planned AP by id.
	 */
	public PlannedAP findPlannedAP(long plannedId) throws Exception {
		log.debug("findPlannedAP", "plannedId:" + plannedId);
		HiveAp hiveAp = loadHiveAPById(plannedId);
		if (null != hiveAp && null != hiveAp.findMapLeafNode()) {
			return convert(hiveAp);
		}
		return null;
	}

	/*
	 * This method is executed inside a transaction, so it can follow lazy
	 * relationships. Load all planned APs on this map from database.
	 */
	public void loadPlannedAPs(MapContainerNode mapContainerNode,
			Set<MapNode> childNodes) {
		Set<PlannedAP> plannedAPs = new HashSet<PlannedAP>();
		// TODO don't use cached nodes for now since the node positions
		// in the cached node list is incorrect after dragging nodes,
		childNodes = mapContainerNode.getChildNodes();
		// so load from database instead.
		for (MapNode mapNode : childNodes) {
			if (!mapNode.isLeafNode()) {
				continue;
			}
			MapLeafNode mapLeafNode = (MapLeafNode) mapNode;
			HiveAp hiveAp = mapLeafNode.getHiveAp();
			// shall check null value, e.g. foreign node
			if (null == hiveAp || !hiveAp.isSimulated()) {
				continue;
			}
			plannedAPs.add(convert(hiveAp));
		}
		mapContainerNode.setPlannedAPs(plannedAPs);
	}

	/*
	 * Update planned AP.
	 */
	public PlannedAP updatePlannedAP(MapContainerNode mapContainerNode,
			long plannedId, String hostName, short apModel, short wifi0Channel,
			short wifi1Channel, short wifi0Power, short wifi1Power, short radio)
			throws Exception {
		log.info("updatePlannedAP", "plannedId:" + plannedId + ", apModel:"
				+ apModel + ", wifi0Channel:" + wifi0Channel + ", wifi0Power:"
				+ wifi0Power + ", wifi1Channel:" + wifi1Channel
				+ ", wifi1Power:" + wifi1Power);
		HiveAp hiveAp = loadHiveAPById(plannedId);
		if (null != hiveAp && null != hiveAp.findMapLeafNode()) {
			hiveAp.setHiveApModel(apModel);
			hiveAp.getWifi0().setChannel(wifi0Channel);
			hiveAp.getWifi0().setPower(wifi0Power);
			hiveAp.getWifi1().setChannel(wifi1Channel);
			hiveAp.getWifi1().setPower(wifi1Power);
			hiveAp = BoMgmt.getMapMgmt().updateHiveAp(hiveAp);
			hiveAp = loadHiveAPById(plannedId);// must to load for radio
			// profiles
			return convert(hiveAp);
		}
		return null;
	}

	/*
	 * Move planned AP.
	 */
	public PlannedAP movePlannedAP(MapContainerNode mapContainerNode,
			long plannedId, double x, double y) throws Exception {
		log.info("movePlannedAP", "plannedId:" + plannedId + ", x:" + x
				+ ", y:" + y);
		HiveAp hiveAp = loadHiveAPById(plannedId);
		if (null != hiveAp && null != hiveAp.findMapLeafNode()) {
			BoMgmt.getMapMgmt().updateLeafNodePosition(
					hiveAp.findMapLeafNode().getId(), x, y);
			hiveAp = loadHiveAPById(plannedId);
			return convert(hiveAp);
		}
		return null;
	}

	/*
	 * Remove planned AP.
	 */
	public void removePlannedAP(long plannedId) throws Exception {
		log.info("removePlannedAP", "plannedId:" + plannedId);
		List<Long> removeIds = new ArrayList<Long>(1);
		removeIds.add(plannedId);
		BoMgmt.getMapMgmt().removeHiveAps(removeIds);
	}

	private PlannedAP convert(HiveAp hiveAp) {
		if (null != hiveAp) {
			PlannedAP plannedAP = new PlannedAP();
			plannedAP.setId(hiveAp.getId());
			plannedAP.setOwner(hiveAp.getOwner());
			plannedAP.setParentMap(hiveAp.getMapContainer());
			plannedAP.hostName = hiveAp.getHostName();
			plannedAP.apModel = hiveAp.getHiveApModel();
			plannedAP.countryCode = hiveAp.getCountryCode();
			plannedAP.wifi0Channel = (short) hiveAp.getWifi0().getChannel();
			plannedAP.wifi0Power = (short) hiveAp.getWifi0().getPower();
			plannedAP.wifi1Channel = (short) hiveAp.getWifi1().getChannel();
			plannedAP.wifi1Power = (short) hiveAp.getWifi1().getPower();
			plannedAP.x = hiveAp.findMapLeafNode().getX();
			plannedAP.y = hiveAp.findMapLeafNode().getY();
			RadioProfile radio0 = hiveAp.getWifi0RadioProfile();
			RadioProfile radio1 = hiveAp.getWifi1RadioProfile();
			if (null != radio0) {
				plannedAP.wifi0ChannelWidth = radio0.getChannelWidth();
			}
			if (null != radio1) {
				plannedAP.wifi1ChannelWidth = radio1.getChannelWidth();
			}
			return plannedAP;
		}
		return null;
	}

	private HiveAp loadHiveAPById(Long plannedId) {
		if (null != plannedId) {
			return QueryUtil.findBoById(HiveAp.class, plannedId, this);
		}
		return null;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HiveAp) {
			HiveAp hiveAp = (HiveAp) bo;
			if (null != hiveAp.getMapContainer()) {
				hiveAp.getMapContainer().getId();
			}
			if (null != hiveAp.getWifi0RadioProfile()) {
				hiveAp.getWifi0RadioProfile().getId();
			}
			if (null != hiveAp.getWifi1RadioProfile()) {
				hiveAp.getWifi1RadioProfile().getId();
			}
		}
		return null;
	}
}
