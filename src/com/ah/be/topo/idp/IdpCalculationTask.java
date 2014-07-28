package com.ah.be.topo.idp;

//import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.GroupByParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class IdpCalculationTask implements Runnable {

	private static final Tracer log = new Tracer(IdpCalculationTask.class
			.getSimpleName());

	private final Map<Long, Set<Idp>> bssidByDomain = new HashMap<Long, Set<Idp>>();

	public IdpCalculationTask() {

	}

	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		try {
			idpCalculate();
		} catch (Exception e) {
			log.error("run", "IdpCalculationtask run exception", e);
		} catch (Error e) {
			log.error("run", "IdpCalculationtask run error", e);
		}
	}

	private void idpCalculate() {
		if (bssidByDomain.isEmpty()) {
			return;
		}
		long start = System.currentTimeMillis();
		Map<Long, Set<Idp>> calculateBssids = new HashMap<Long, Set<Idp>>(
				bssidByDomain.size());
		int bssidCount = 0;
		synchronized (this) {
			for (Long domainId : bssidByDomain.keySet()) {
				Set<Idp> bssids = bssidByDomain.get(domainId);
				bssidCount += bssids.size();
				calculateBssids.put(domainId, bssids);
			}
			bssidByDomain.clear();
		}
		log.debug("idpCalculate", "Calculated Rogue AP location size:"
				+ bssidCount);
		for (Long domainId : calculateBssids.keySet()) {
			Set<Idp> rogueAps = calculateBssids.get(domainId);
			if (rogueAps.isEmpty()) {
				continue;
			}
			// for (String bssid : bssids) {
			// calculation(bssid, domainId);
			// }
			// remove idp with same bssid
			List<Idp> calIdps = new ArrayList<Idp>();
			List<String> bssids = new ArrayList<String>();
			for (Idp idp : rogueAps) {
				if (!bssids.contains(idp.getIfMacAddress())) {
					calIdps.add(idp);
					bssids.add(idp.getIfMacAddress());
				}
			}
			updateRogueApLocation(calIdps, domainId);
		}
		long end = System.currentTimeMillis();
		log.debug("idpCalculate", "Calculate Rogue AP location cost:"
				+ (end - start) + "ms.");
	}

	private void updateRogueApLocation(List<Idp> idps, Long domainId) {
		try {
			HmDomain domain = CacheMgmt.getInstance().getCacheDomainById(
					domainId);
			if (null == domain) {
				log.error("updateRogueApLocation",
						"Can not find HmDomain bo in cache, domainId:"
								+ domainId);
			} else {
				BoMgmt.getLocationTracking().findRogueRssi(domain, idps);
				Map<Long, Set<String>> idpMap = new HashMap<Long, Set<String>>();
				for (Idp idp : idps) {
					Long mapId = idp.getMapId();
					if (null == idpMap.get(mapId)) {
						Set<String> bssids = new HashSet<String>();
						idpMap.put(mapId, bssids);
					}
					idpMap.get(mapId).add(idp.getIfMacAddress());
				}
				// bulk update for the same map id
				for (Long mapId : idpMap.keySet()) {
					BoMgmt.getIdpMgmt().updateIdpLocation(idpMap.get(mapId),
							null, mapId, domainId);
				}
			}
		} catch (Exception e) {
			log.error("updateRogueApLocation",
					"Calculation Rogue AP location error, domainId" + domainId,
					e);
		}
	}
	@Deprecated
	protected void calculation(String bssid, Long domainId) {
		try {
			List<?> list = QueryUtil.executeQuery("select reportNodeId from "
					+ Idp.class.getSimpleName() + " bo", null,
					new FilterParams("ifMacAddress", bssid), domainId);
			if (!list.isEmpty()) {
				String where = "macAddress in (:s1) AND owner.id = :s2";
				Object[] binds = new Object[] { list, domainId };
				List<?> results = QueryUtil.executeQuery(
						"select bo.mapContainer.id, count(*) from "
								+ HiveAp.class.getSimpleName() + " bo", null,
						new FilterParams(where, binds), new GroupByParams(
								new String[] { "mapContainer.id" }), null);
				if (!results.isEmpty()) {
					Long maxMapId = 0L, maxCount = 0L;
					for (Object object : results) {
						Object[] values = (Object[]) object;
						Long mapId = (Long) values[0];
						Long count = (Long) values[1];
						if (null != mapId && count > maxCount) {
							maxCount = count;
							maxMapId = mapId;
						}
					}
					log.debug("calculation", "IDP:" + bssid + " likey on map:"
							+ maxMapId + ", Report HiveAP on that map count:"
							+ maxCount);
					if (maxMapId <= 0 || maxCount < 3) {
						return;
					}
					// invoke to calculate the location
					QueryLazyLeafNode queryBo = new QueryLazyLeafNode();
					QueryUtil.findBoById(MapContainerNode.class, maxMapId,
									queryBo);

				//	Point2D xy = null; // BoMgmt.getLocationTracking().locateIdp(bssid, container, queryBo.getLeafNodes());
				//	if (null != xy) {
						// BoMgmt.getIdpMgmt().updateIdpLocation(bssid, xy,
						// maxMapId, domainId);
				//	}
				}
			}
		} catch (Exception e) {
			log.error("calculation", "Calculation BSSID:" + bssid + " error.",
					e);
		}
	}

	private class QueryLazyLeafNode implements QueryBo {
		private Map<String, MapLeafNode> leafNodes;

		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (null != bo && bo instanceof MapContainerNode) {
				Set<MapNode> children = ((MapContainerNode) bo).getChildNodes();
				if (null != children) {
					leafNodes = new HashMap<String, MapLeafNode>();
					for (MapNode child : children) {
						if (child.isLeafNode()) {
							MapLeafNode leafNode = (MapLeafNode) child;
							leafNodes.put(leafNode.getApId(), leafNode);
							HiveAp hiveAp = leafNode.getHiveAp();
							if (hiveAp != null) {
								// Trigger load from DB
								hiveAp.getHiveApModel(); // we need this to
								// calculate ERP
							}
						}
					}
				}
			}
			return null;
		}

//		public Map<String, MapLeafNode> getLeafNodes() {
//			if (null == leafNodes) {
//				leafNodes = new HashMap<String, MapLeafNode>();
//			}
//			return leafNodes;
//		}
	}

	public synchronized void addBssid(Set<Idp> newSet, Long domainId) {
		if (null == newSet || null == domainId) {
			return;
		}
		Set<Idp> rogueAps = bssidByDomain.get(domainId);
		if (null == rogueAps) {
			rogueAps = new HashSet<Idp>();
		}
		rogueAps.addAll(newSet);
		bssidByDomain.put(domainId, rogueAps);
	}

}