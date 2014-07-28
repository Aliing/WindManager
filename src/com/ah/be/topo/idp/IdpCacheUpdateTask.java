package com.ah.be.topo.idp;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.GroupByParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.monitor.SystemStatusCache;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class IdpCacheUpdateTask implements Runnable {

	private static final Tracer log = new Tracer(IdpCacheUpdateTask.class
			.getSimpleName());

	private final Set<Long> idpChangedDomains;

	public IdpCacheUpdateTask() {

		idpChangedDomains = new HashSet<Long>();

	}

	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
		try {
			updateIdpCache();
		} catch (Exception e) {
			log.error("run", "IdpCacheUpdateTask run exception", e);
		} catch (Error e) {
			log.error("run", "IdpCacheUpdateTask run error", e);
		}
	}

	/*
	 * Only do query while any IDP has been changed!
	 */
	private synchronized void updateIdpCache() {
		if (!idpChangedDomains.isEmpty()) {
			long start = System.currentTimeMillis();
			Map<Long, Long> result = queryInnetRogueApCount();
			if (null != result) {
				for (Long domainId : idpChangedDomains) {
					Long count = result.get(domainId);
					if (null == count || count < 0) {
						SystemStatusCache.getInstance().updateInnetRogueCount(
								0, domainId);
					} else {
						SystemStatusCache.getInstance().updateInnetRogueCount(
								count, domainId);
					}
				}
			}
			result = queryOnmapRogueApCount();
			if (null != result) {
				for (Long domainId : idpChangedDomains) {
					Long count = result.get(domainId);
					if (null == count || count < 0) {
						SystemStatusCache.getInstance().updateOnmapRogueCount(
								0, domainId);
					} else {
						SystemStatusCache.getInstance().updateOnmapRogueCount(
								count, domainId);
					}
				}
			}
			result = queryStrongRogueApCount();
			if (null != result) {
				for (Long domainId : idpChangedDomains) {
					Long count = result.get(domainId);
					if (null == count || count < 0) {
						SystemStatusCache.getInstance().updateStrongRogueCount(
								0, domainId);
					} else {
						SystemStatusCache.getInstance().updateStrongRogueCount(
								count, domainId);
					}
				}
			}
			result = queryWeakRogueApCount();
			if (null != result) {
				for (Long domainId : idpChangedDomains) {
					Long count = result.get(domainId);
					if (null == count || count < 0) {
						SystemStatusCache.getInstance().updateWeakRogueCount(0,
								domainId);
					} else {
						SystemStatusCache.getInstance().updateWeakRogueCount(
								count, domainId);
					}
				}
			}
			result = queryRogueClientCount();
			if (null != result) {
				for (Long domainId : idpChangedDomains) {
					Long count = result.get(domainId);
					if (null == count || count < 0) {
						SystemStatusCache.getInstance().updateRogueClientCount(
								0, domainId);
					} else {
						SystemStatusCache.getInstance().updateRogueClientCount(
								count, domainId);
					}
				}
			}

			long end = System.currentTimeMillis();
			log.debug("updateIdpCache", "Query DB to update IDP Cache cost:"
					+ (end - start) + "ms.");
		}
		// Clear the Set after update.
		idpChangedDomains.clear();
	}

	/*
	 * get category as in-net rogue AP count group by domain
	 */
	private Map<Long, Long> queryInnetRogueApCount() {
		try {
			String query = "select bo.owner.id, count(distinct ifMacAddress) from "
					+ Idp.class.getSimpleName() + " bo";
			String where = "stationType = :s1 AND idpType = :s2 AND inNetworkFlag = :s3 AND owner.id in (:s4)";
			Object[] values = { BeCommunicationConstant.IDP_STATION_TYPE_AP,
					BeCommunicationConstant.IDP_TYPE_ROGUE,
					Idp.IDP_CONNECTION_IN_NET, idpChangedDomains };
			List<?> list = QueryUtil.executeQuery(query, null,
					new FilterParams(where, values), new GroupByParams(
							new String[] { "owner.id" }), null);
			Map<Long, Long> map = new HashMap<Long, Long>(list.size());
			for (Object object : list) {
				Object[] attributes = (Object[]) object;
				map.put((Long) attributes[0], (Long) attributes[1]);
			}
			return map;
		} catch (Exception e) {
			log.error("queryInnetRogueApCount",
					"query innet rogue ap count error.", e);
			return null;
		}
	}

	/*
	 * get category as on map rogue AP count group by domain
	 */
	private Map<Long, Long> queryOnmapRogueApCount() {
		try {
			String query = "select bo.owner.id, count(distinct ifMacAddress) from "
					+ Idp.class.getSimpleName() + " bo";
			String where = "stationType = :s1 AND idpType = :s2 AND mapId > :s3 AND owner.id in (:s4)";
			Object[] values = { BeCommunicationConstant.IDP_STATION_TYPE_AP,
					BeCommunicationConstant.IDP_TYPE_ROGUE, 0L,
					idpChangedDomains };
			List<?> list = QueryUtil.executeQuery(query, null,
					new FilterParams(where, values), new GroupByParams(
							new String[] { "owner.id" }), null);
			Map<Long, Long> map = new HashMap<Long, Long>(list.size());
			for (Object object : list) {
				Object[] attributes = (Object[]) object;
				map.put((Long) attributes[0], (Long) attributes[1]);
			}
			return map;
		} catch (Exception e) {
			log.error("queryOnmapRogueApCount",
					"query on map rogue ap count error.", e);
			return null;
		}
	}

	private String getQueryString4StrongOrWeek(String type){
		StringBuilder query = new StringBuilder("select bo.owner, count(distinct bo.ifMacAddress) from idp bo");
		query.append(" where bo.stationType= ");
		query.append(BeCommunicationConstant.IDP_STATION_TYPE_AP);
		query.append(" AND bo.idpType= ");
		query.append(BeCommunicationConstant.IDP_TYPE_ROGUE);
		query.append(" AND ( ");
		boolean isFirst = true;
		for (Long domainId : idpChangedDomains) {
			int threshold = BoMgmt.getIdpMgmt()
					.getSignalThreshold(domainId) + 95;
			if (isFirst) {
				query.append(" (bo.owner = ").append(domainId);
				isFirst = false;
			} else {
				query.append(" or (bo.owner = ").append(domainId);
			}
			query.append(" and bo.rssi").append("Strong".equals(type) ? " >= ": " < ").append(threshold).append(")");
		}
		query.append(")");
		query.append(" group by bo.owner");
		
		return query.toString();
	}
	/*
	 * get category as strong RSSI rogue AP count group by domain
	 */
	private Map<Long, Long> queryStrongRogueApCount() {
		try {
			String query = getQueryString4StrongOrWeek("Strong");
			List<?> list = QueryUtil.executeNativeQuery(query.toString());
			Map<Long, Long> map = null;
			if (list != null
					&& !list.isEmpty()) {
				map = new HashMap<Long, Long>(list.size());
				for (Object obj : list) {
					Object[] attributes = (Object[])obj;
					map.put(((BigInteger) attributes[0]).longValue(), ((BigInteger) attributes[1]).longValue());
				}
			}

			return map;
		} catch (Exception e) {
			log.error("queryStrongRogueApCount",
					"query strong RSSI rogue ap count error.", e);
			return null;
		}
	}

	/*
	 * get category as weak RSSI rogue AP count group by domain
	 */
	private Map<Long, Long> queryWeakRogueApCount() {
		try {
			String query = getQueryString4StrongOrWeek("Weak");
			List<?> list = QueryUtil.executeNativeQuery(query.toString());
			Map<Long, Long> map = null;
			if (list != null
					&& !list.isEmpty()) {
				map = new HashMap<Long, Long>(list.size());
				for (Object obj : list) {
					Object[] attributes = (Object[])obj;
					map.put(((BigInteger) attributes[0]).longValue(), ((BigInteger) attributes[1]).longValue());
				}
			}
			return map;
		} catch (Exception e) {
			log.error("queryWeakRogueApCount",
					"query weak RSSI rogue ap count error.", e);
			return null;
		}
	}

	/*
	 * get rogue client count group by domain
	 */
	private Map<Long, Long> queryRogueClientCount() {
		try {
			String query = "select bo.owner.id, count(distinct ifMacAddress) from "
					+ Idp.class.getSimpleName() + " bo";
			String where = "stationType = :s1 AND idpType = :s2 AND owner.id in (:s3)";
			Object[] values = {
					BeCommunicationConstant.IDP_STATION_TYPE_CLIENT,
					BeCommunicationConstant.IDP_TYPE_ROGUE, idpChangedDomains };
			List<?> list = QueryUtil.executeQuery(query, null,
					new FilterParams(where, values), new GroupByParams(
							new String[] { "owner.id" }), null);
			Map<Long, Long> map = new HashMap<Long, Long>(list.size());
			for (Object object : list) {
				Object[] attributes = (Object[]) object;
				map.put((Long) attributes[0], (Long) attributes[1]);
			}
			return map;
		} catch (Exception e) {
			log.error("queryRogueClientCount",
					"query rogue client count error.", e);
			return null;
		}
	}

	/**
	 * While IDP data changed for a specify Domain's, add it into set.
	 *
	 * @param domainId -
	 */
	public synchronized void idpChanged(Long domainId) {
		idpChangedDomains.add(domainId);
	}

}