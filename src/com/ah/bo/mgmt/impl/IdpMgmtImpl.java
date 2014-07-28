package com.ah.bo.mgmt.impl;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;

import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;

import com.ah.be.app.HmBeTopoUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.hiveap.IdpSettings;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.IdpMgmt;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.Tracer;

public class IdpMgmtImpl implements IdpMgmt, QueryBo {

	private static final Tracer log = new Tracer(
			IdpMgmtImpl.class.getSimpleName());

	private static IdpMgmt instance;

	private IdpMgmtImpl() {
	}

	public synchronized static IdpMgmt getInstance() {
		if (instance == null) {
			instance = new IdpMgmtImpl();
		}

		return instance;
	}

	public synchronized void removeIdps(String reportHiveApNodeId, Long domainId) {
		if (null == reportHiveApNodeId) {
			return;
		}
		try {
			int count = QueryUtil.bulkRemoveBos(Idp.class, new FilterParams(
					"reportNodeId", reportHiveApNodeId));
			// IDP changed for this domain
			HmBeTopoUtil.getIdpScheduledExecutor().getIdpCacheUpdateTask()
					.idpChanged(domainId);
			log.debug("removeIdps", "remove all IDPs reported by:"
					+ reportHiveApNodeId + " total count:" + count);
		} catch (Exception e) {
			log.error("removeIdps", "Remove all IDPs reported by:"
					+ reportHiveApNodeId + " error.", e);
		}
	}

	public synchronized int removeIdps(Collection<Long> idList, Long domainId)
			throws Exception {
		if (null == idList || idList.isEmpty()) {
			return 0;
		}
		int count = QueryUtil.bulkRemoveBos(Idp.class, new FilterParams("id",
				idList));
		// IDP changed for this domain
		HmBeTopoUtil.getIdpScheduledExecutor().getIdpCacheUpdateTask()
				.idpChanged(domainId);
		log.debug("", "remove a list of IDPs, total count:" + count);
		return count;
	}

	public synchronized void addIdps(Map<String, Idp> idps,
			String reportHiveApNodeId, Long domainId) throws Exception {
		if (null == idps || idps.isEmpty() || null == reportHiveApNodeId) {
			return;
		}
		String where = "reportNodeId = :s1 AND ifMacAddress in (:s2)";
		Object[] values = { reportHiveApNodeId, idps.keySet() };
		/*-
		List<?> list = QueryUtil.executeQuery(
				"select id, ifMacAddress from Idp", null, new FilterParams(
						where, values));
		log.debug("addIdps", "Updated idp count:" + list.size());
		for (Object object : list) {
			Object[] idp = (Object[]) object;
			idps.get(idp[1].toString()).setId((Long) idp[0]);
		}*/

		EntityManager em = null;
		EntityTransaction tx = null;
		Idp idp = null;

		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			em.createNativeQuery("/*NO LOAD BALANCE*/select 1").getResultList();
			Query jpQuery = QueryUtil
					.createQuery(em, "select id, ifMacAddress from "
							+ Idp.class.getSimpleName(), null,
							new FilterParams(where, values), domainId, null);
			List<?> bos = jpQuery.setMaxResults(Paging.MAX_RESULTS)
					.getResultList();
			log.info("addIdps but need to update idp count: " + bos.size());
			for (Object object : bos) {
				Object[] obj = (Object[]) object;
				idps.get(obj[1].toString()).setId((Long) obj[0]);
			}

			for (Idp bo : idps.values()) {
				idp = bo;
				em.merge(idp);
			}

			tx.commit();
			// IDP changed for this domain
			HmBeTopoUtil.getIdpScheduledExecutor().getIdpCacheUpdateTask()
					.idpChanged(domainId);
			// Must to re-calculate on-map for rogue IDPs
			// Set<Idp> rogueAps = new HashSet<Idp>();
			// for (Idp object : idps.values()) {
			// if (object.getStationType() ==
			// BeCommunicationConstant.IDP_STATION_TYPE_AP
			// && object.getIdpType() == BeCommunicationConstant.IDP_TYPE_ROGUE)
			// {
			// rogueAps.add(object);
			// }
			// }
			// IdpCalculationTask.getInstance().addBssid(rogueAps, domainId);
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			log.error("addIdps", "Add IDP objects failed.");
			if (e instanceof OptimisticLockException
					&& e.getCause() instanceof StaleObjectStateException) {
				throw new HmException("Add IDP " + idp.getId()
						+ " failed, stale object state.", e,
						HmMessageCodes.STALE_OBJECT);
			} else if (e.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) e
						.getCause();
				log.info("addIdps", "Constraint: " + cve.getConstraintName());
				throw new HmException(
						"Add IDP  '" + idp.getLabel() + "' failed.",
						e,
						HmMessageCodes.CONSTRAINT_VIOLATION,
						new String[] { idp.getLabel(), cve.getConstraintName() });
			} else {
				throw e;
			}
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}

	public synchronized void updateIdp(Idp idp) throws Exception {
		QueryUtil.updateBo(idp);
		// IDP changed for this domain
		HmBeTopoUtil.getIdpScheduledExecutor().getIdpCacheUpdateTask()
				.idpChanged(idp.getOwner().getId());
	}

	public synchronized void removeIdps(List<String> bssids,
			String reportHiveApNodeId, Long domainId) throws Exception {
		if (null == bssids || bssids.isEmpty() || null == reportHiveApNodeId) {
			return;
		}
		String where = "reportNodeId = :s1 AND ifMacAddress in (:s2)";
		Object[] values = { reportHiveApNodeId, bssids };
		int count = QueryUtil.bulkRemoveBos(Idp.class, new FilterParams(where,
				values));
		// IDP changed for this domain
		HmBeTopoUtil.getIdpScheduledExecutor().getIdpCacheUpdateTask()
				.idpChanged(domainId);
		log.debug("", "remove IDP reported by:" + reportHiveApNodeId
				+ " total count:" + count);
	}

	public synchronized void removeMitigationIdps(List<String> bssids,
			String reportHiveApNodeId, Long domainId) throws Exception {
		if (null == bssids || bssids.isEmpty() || null == reportHiveApNodeId) {
			return;
		}
		String where = "reportNodeId = :s1 AND parentBssid in (:s2)";
		Object[] values = { reportHiveApNodeId, bssids };
		int count = QueryUtil.bulkRemoveBos(Idp.class, new FilterParams(where,
				values));
		// IDP changed for this domain
		HmBeTopoUtil.getIdpScheduledExecutor().getIdpCacheUpdateTask()
				.idpChanged(domainId);
		log.debug("", "remove mitigated clients reported by:"
				+ reportHiveApNodeId + " total count:" + count);
	}

	public synchronized void updateMitigationFlag(List<String> bssids,
			String reportHiveApNodeId, boolean mitigated) throws Exception {
		if (null == bssids || bssids.isEmpty() || null == reportHiveApNodeId) {
			return;
		}
		QueryUtil.updateBos(Idp.class, "mitigated = :s1",
				"reportNodeId = :s2 AND ifMacAddress in (:s3)", new Object[] {
						mitigated, reportHiveApNodeId, bssids });
	}

	public int getRefreshInterval(Long domainId) {
		List<IdpSettings> list = QueryUtil.executeQuery(IdpSettings.class,
				null, null, domainId);
		if (list.isEmpty()) {
			return IdpSettings.DEFAULT_INTERVAL;
		} else {
			return list.get(0).getInterval();
		}
	}

	public int getSignalThreshold(Long domainId) {
		List<IdpSettings> list = QueryUtil.executeQuery(IdpSettings.class,
				null, null, domainId);
		if (list.isEmpty()) {
			return IdpSettings.DEFAULT_THRESHOLD;
		} else {
			return list.get(0).getThreshold();
		}
	}

	public Set<String> getEnclosedRogueAps(Long domainId) {
		List<IdpSettings> list = QueryUtil.executeQuery(IdpSettings.class,
				null, null, domainId, this);
		if (list.isEmpty()) {
			return new HashSet<String>();
		} else {
			return new HashSet<String>(list.get(0).getEnclosedRogueAps());
		}
	}

	public Set<String> getEnclosedFriendlyAps(Long domainId) {
		List<IdpSettings> list = QueryUtil.executeQuery(IdpSettings.class,
				null, null, domainId, this);
		if (list.isEmpty()) {
			return new HashSet<String>();
		} else {
			return new HashSet<String>(list.get(0).getEnclosedFriendlyAps());
		}
	}

	public Set<String> addEnclosedFriendlyAps(Set<String> bssids, Long domainId) {
		if (null == bssids) {
			return null;
		}
		try {
			List<IdpSettings> list = QueryUtil.executeQuery(IdpSettings.class,
					null, null, domainId, this);
			if (list.isEmpty()) {
				HmDomain domain = QueryUtil
						.findBoById(HmDomain.class, domainId);
				IdpSettings settings = new IdpSettings();
				settings.setOwner(domain);
				settings.setEnclosedFriendlyAps(new ArrayList<String>(bssids));
				QueryUtil.createBo(settings);
				// update cache
				HmBeTopoUtil.getIdpEventListener().getIdpEventProcessor()
						.updateIdpSetting(domainId, settings);
				return bssids;
			} else {
				IdpSettings settings = list.get(0);
				Set<String> existed = new HashSet<String>(
						settings.getEnclosedFriendlyAps());
				Set<String> existedRogue = new HashSet<String>(
						settings.getEnclosedRogueAps());
				existed.addAll(bssids);
				existedRogue.removeAll(bssids);// must remove from rogue
				settings.setEnclosedFriendlyAps(new ArrayList<String>(existed));
				settings.setEnclosedRogueAps(new ArrayList<String>(existedRogue));
				QueryUtil.updateBo(settings);
				// update cache
				HmBeTopoUtil.getIdpEventListener().getIdpEventProcessor()
						.updateIdpSetting(domainId, settings);
				return existed;
			}
		} catch (Exception e) {
			log.error("addEnclosedFriendlyAps",
					"add enclosed friendly Aps error.", e);
			return null;
		}
	}

	public Set<String> addEnclosedRogueAps(Set<String> bssids, Long domainId) {
		if (null == bssids) {
			return null;
		}
		try {
			List<IdpSettings> list = QueryUtil.executeQuery(IdpSettings.class,
					null, null, domainId, this);
			if (list.isEmpty()) {
				HmDomain domain = QueryUtil
						.findBoById(HmDomain.class, domainId);
				IdpSettings settings = new IdpSettings();
				settings.setOwner(domain);
				settings.setEnclosedRogueAps(new ArrayList<String>(bssids));
				QueryUtil.createBo(settings);
				// update cache
				HmBeTopoUtil.getIdpEventListener().getIdpEventProcessor()
						.updateIdpSetting(domainId, settings);
				return bssids;
			} else {
				IdpSettings settings = list.get(0);
				Set<String> existed = new HashSet<String>(
						settings.getEnclosedRogueAps());
				Set<String> existedFri = new HashSet<String>(
						settings.getEnclosedFriendlyAps());
				existed.addAll(bssids);
				existedFri.removeAll(bssids);// must remove from friendly
				settings.setEnclosedFriendlyAps(new ArrayList<String>(
						existedFri));
				settings.setEnclosedRogueAps(new ArrayList<String>(existed));
				QueryUtil.updateBo(settings);
				// update cache
				HmBeTopoUtil.getIdpEventListener().getIdpEventProcessor()
						.updateIdpSetting(domainId, settings);
				return existed;
			}
		} catch (Exception e) {
			log.error("addEnclosedRogueAps", "add enclosed rogue Aps error.", e);
			return null;
		}
	}

	public Set<String> removeEnclosedFriendlyAps(Set<String> bssids,
			Long domainId) {
		try {
			List<IdpSettings> list = QueryUtil.executeQuery(IdpSettings.class,
					null, null, domainId, this);
			if (list.isEmpty()) {
				return new HashSet<String>();// return empty set;
			} else {
				IdpSettings settings = list.get(0);
				List<String> existed = settings.getEnclosedFriendlyAps();
				existed.removeAll(bssids);
				QueryUtil.updateBo(settings);
				// update cache
				HmBeTopoUtil.getIdpEventListener().getIdpEventProcessor()
						.updateIdpSetting(domainId, settings);
				return new HashSet<String>(existed);
			}
		} catch (Exception e) {
			log.error("removeEnclosedFriendlyAps",
					"remove enclosed rogue Aps error.", e);
			return null;
		}
	}

	public Set<String> removeEnclosedRogueAps(Set<String> bssids, Long domainId) {
		try {
			List<IdpSettings> list = QueryUtil.executeQuery(IdpSettings.class,
					null, null, domainId, this);
			if (list.isEmpty()) {
				return new HashSet<String>();// return empty set;
			} else {
				IdpSettings settings = list.get(0);
				List<String> existed = settings.getEnclosedRogueAps();
				existed.removeAll(bssids);
				QueryUtil.updateBo(settings);
				// update cache
				HmBeTopoUtil.getIdpEventListener().getIdpEventProcessor()
						.updateIdpSetting(domainId, settings);
				return new HashSet<String>(existed);
			}
		} catch (Exception e) {
			log.error("removeEnclosedFriendlyAps",
					"remove enclosed friendly Aps error.", e);
			return null;
		}
	}

	public boolean updateRefreshInterval(Long domainId, int value) {
		try {
			List<IdpSettings> list = QueryUtil.executeQuery(IdpSettings.class,
					null, null, domainId);
			if (list.isEmpty()) {
				if (value != IdpSettings.DEFAULT_INTERVAL) {
					HmDomain domain = QueryUtil.findBoById(HmDomain.class,
							domainId);
					IdpSettings settings = new IdpSettings();
					settings.setInterval(value);
					settings.setOwner(domain);
					QueryUtil.createBo(settings);
				}
			} else {
				IdpSettings settings = list.get(0);
				if (settings.getInterval() != value) {
					settings.setInterval(value);
					QueryUtil.updateBo(settings);
				}
			}
			return true;
		} catch (Exception e) {
			log.error("updateRefreshInterval",
					"Update IDP refresh interval error.", e);
			return false;
		}
	}

	public boolean updateSignalThreshold(Long domainId, int value) {
		try {
			List<IdpSettings> list = QueryUtil.executeQuery(IdpSettings.class,
					null, null, domainId);
			if (list.isEmpty()) {
				if (value != IdpSettings.DEFAULT_THRESHOLD) {
					HmDomain domain = QueryUtil.findBoById(HmDomain.class,
							domainId);
					IdpSettings settings = new IdpSettings();
					settings.setThreshold(value);
					settings.setOwner(domain);
					QueryUtil.createBo(settings);
					// IDP changed for this domain
					HmBeTopoUtil.getIdpScheduledExecutor()
							.getIdpCacheUpdateTask().idpChanged(domainId);
				}
			} else {
				IdpSettings settings = list.get(0);
				if (settings.getThreshold() != value) {
					settings.setThreshold(value);
					QueryUtil.updateBo(settings);
					// IDP changed for this domain
					HmBeTopoUtil.getIdpScheduledExecutor()
							.getIdpCacheUpdateTask().idpChanged(domainId);
				}
			}
			return true;
		} catch (Exception e) {
			log.error("updateRefreshInterval",
					"Update IDP refresh interval error.", e);
			return false;
		}
	}

	public void updateIdpLocation(Set<String> bssids, Point2D xy, Long mapId,
			Long domainId) {
		if (null == bssids || null == domainId) {
			return;
		}
		try {
			QueryUtil.updateBos(Idp.class, "mapId = :s1",
					"ifMacAddress in (:s2)", new Object[] { mapId, bssids },
					domainId);
			// IDP changed for this domain
			HmBeTopoUtil.getIdpScheduledExecutor().getIdpCacheUpdateTask()
					.idpChanged(domainId);
		} catch (Exception e) {
			log.error("updateIdpLocation", "update IDP location error.", e);
		}
	}

	public void updateManagedHiveAPBssidFilter(Long domainId, boolean filter) {
		try {
			List<IdpSettings> list = QueryUtil.executeQuery(IdpSettings.class,
					null, null, domainId);
			if (list.isEmpty()) {
				if (!filter) {
					HmDomain domain = QueryUtil.findBoById(HmDomain.class,
							domainId);
					IdpSettings settings = new IdpSettings();
					settings.setFilterManagedHiveAPBssid(filter);
					settings.setOwner(domain);
					QueryUtil.createBo(settings);
				}
			} else {
				IdpSettings settings = list.get(0);
				if (settings.isFilterManagedHiveAPBssid() != filter) {
					settings.setFilterManagedHiveAPBssid(filter);
					QueryUtil.updateBo(settings);
				}
			}
		} catch (Exception e) {
			log.error("updateManagedHiveAPBssidFilter",
					"Update IDP filter managed HiveAP BSSID error.", e);
		}
	}

	public boolean getManagedHiveAPBssidFilter(Long domainId) {
		List<IdpSettings> list = QueryUtil.executeQuery(IdpSettings.class,
				null, null, domainId);
		if (list.isEmpty()) {
			return IdpSettings.DEFAULT_FILTER_MANAGED_HIVEAP_BSSID;
		} else {
			return list.get(0).isFilterManagedHiveAPBssid();
		}
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}

		if (bo instanceof IdpSettings) {
			IdpSettings idp = (IdpSettings) bo;
			if (idp.getEnclosedRogueAps() != null)
				idp.getEnclosedRogueAps().size();
			if (idp.getEnclosedFriendlyAps() != null)
				idp.getEnclosedFriendlyAps().size();
		}

		return null;
	}

	public synchronized int removeIdpsByBssid(Collection<String> bssidList,
			Long domainId) throws Exception {
		if (null == bssidList || bssidList.isEmpty()) {
			return 0;
		}
		int count = QueryUtil.bulkRemoveBos(Idp.class, new FilterParams(
				"ifMacAddress", bssidList));
		// IDP changed for this domain
		HmBeTopoUtil.getIdpScheduledExecutor().getIdpCacheUpdateTask()
				.idpChanged(domainId);
		log.debug("", "remove a list of IDPs, total count:" + count);
		return count;
	}

}