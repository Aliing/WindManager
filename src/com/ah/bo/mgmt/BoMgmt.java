package com.ah.bo.mgmt;

import java.util.Collection;
import java.util.List;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.bo.mgmt.impl.AccessControlImpl;
import com.ah.bo.mgmt.impl.DomainMgmtImpl;
import com.ah.bo.mgmt.impl.HiveApMgmtImpl;
import com.ah.bo.mgmt.impl.IdpMgmtImpl;
import com.ah.bo.mgmt.impl.LocationTrackingImpl;
import com.ah.bo.mgmt.impl.MapHierarchyCacheImpl;
import com.ah.bo.mgmt.impl.MapMgmtImpl;
import com.ah.bo.mgmt.impl.TrapMgmtImpl;
import com.ah.events.BoEventMgmt;
import com.ah.events.impl.BoEventMgmtImpl;
import com.ah.ui.actions.monitor.MapsAction;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;

/*
 * @author Chris Scheers
 */

public final class BoMgmt {

	private BoMgmt() {

	}

	/*
	 * Create BO, returns id of new BO.
	 */
	public static Long createBo(HmBo hmBo, HmUser user, String feature)
			throws Exception {
		AccessControl.checkUserAccess(user, feature, CrudOperation.CREATE);
		if (user != null && hmBo.getOwner() == null) {
			if (user.getSwitchDomain() != null) {
				hmBo.setOwner(user.getSwitchDomain());
			} else {
				hmBo.setOwner(user.getDomain());
			}
		}
		return QueryUtil.createBo(hmBo);
	}

	/*
	 * Update BO, returns merged BO.
	 *
	 * Synchronized to avoid object being removed between find and tx commit.
	 *
	 * Synchronization is across all classes, if enabled here. Should be fine
	 * tuned by synchronizing at the class level, or even instance level.
	 */
	public synchronized static <T extends HmBo> T updateBo(T hmBo, HmUser user,
			String feature) throws Exception {
		AccessControl.checkUserAccess(user, feature, CrudOperation.UPDATE);
		// user couldn't update across objects
		if(null != user){
			// load owner first
			HmBo bo = QueryUtil.findBoById(hmBo.getClass(), hmBo.getId());
			// user can edit default ip tracking
			if (null != bo && null != bo.getOwner()
					&& !HmDomain.GLOBAL_DOMAIN.equals(bo.getOwner().getDomainName())
					&& !bo.getOwner().getId().equals(
							QueryUtil.getDependentDomainFilter(user))) {
				throw new HmException("User '" + user.getUserName()
						+ "' does not have WRITE access to object '"
						+ bo.getLabel() + "'.",
						HmMessageCodes.PERMISSION_DENIED_OBJECT_OPERATION,
						new String[] { user.getUserName(), bo.getLabel() });
			}
		}
		return QueryUtil.updateBo(hmBo);
	}

	public synchronized static Collection<HmBo> bulkUpdateBos(
			List<? extends HmBo> hmBos, HmUser user, String feature)
			throws Exception {
		AccessControl.checkUserAccess(user, feature, CrudOperation.UPDATE);
		if(null != user){
			for(HmBo hmBo : hmBos){
				// load owner first
				HmBo bo = QueryUtil.findBoById(hmBo.getClass(), hmBo.getId());
				if (null != bo
						&& null != bo.getOwner()
						&& !bo.getOwner().getId().equals(
								QueryUtil.getDependentDomainFilter(user))) {
					throw new HmException("User '" + user.getUserName()
							+ "' does not have WRITE access to object '"
							+ bo.getLabel() + "'.",
							HmMessageCodes.PERMISSION_DENIED_OBJECT_OPERATION,
							new String[] { user.getUserName(), bo.getLabel() });
				}
			}
		}
		return QueryUtil.bulkUpdateBos(hmBos);
	}

	/*
	 * Find BO by ID
	 */
	public static <T extends HmBo> T findBoById(Class<T> boClass, Long id,
			HmUser user, String feature) throws Exception {
		AccessControl.checkUserAccess(user, feature, CrudOperation.READ);
		T hmBo = QueryUtil.findBoById(boClass, id);
		if (null != hmBo
				&& null != hmBo.getOwner()
				&& null != QueryUtil.getDomainFilter(user)
				&& !HmDomain.GLOBAL_DOMAIN.equals(hmBo.getOwner()
						.getDomainName())
				&& !hmBo.getOwner().getId().equals(
						QueryUtil.getDependentDomainFilter(user))) {
			hmBo = null;
		}
		return hmBo;
	}

	/*
	 * Find BO by ID
	 */
	public static <T extends HmBo> T findBoById(Class<T> boClass, Long id,
			QueryBo queryBo, HmUser user, String feature) throws Exception {
		AccessControl.checkUserAccess(user, feature, CrudOperation.READ);
		T hmBo = QueryUtil.findBoById(boClass, id, queryBo);
		if (null != hmBo
				&& null != hmBo.getOwner()
				&& null != QueryUtil.getDomainFilter(user)
				&& !HmDomain.GLOBAL_DOMAIN.equals(hmBo.getOwner()
						.getDomainName())
				&& !hmBo.getOwner().getId().equals(
						QueryUtil.getDependentDomainFilter(user))) {
			hmBo = null;
		}
		return hmBo;
	}

	/*
	 * Remove BOs by ID, return count
	 *
	 * Synchronized to avoid object being removed between get reference and tx
	 * commit (causes org.hibernate.StaleStateException).
	 *
	 * Synchronization is across all classes, if enabled here. Should be fine
	 * tuned by synchronizing at the class level, or even instance level.
	 */
	public synchronized static int removeBos(Class<? extends HmBo> boClass,
			Collection<Long> ids, HmUser user, String feature) throws Exception {
		AccessControl.checkUserAccess(user, feature, CrudOperation.DELETE);
		return QueryUtil.removeBos(boClass, ids);
	}

	/*
	 * Remove BOs by class, return count
	 *
	 * Synchronized to avoid object being removed between get reference and tx
	 * commit (causes org.hibernate.StaleStateException).
	 *
	 * Synchronization is across all classes, if enabled here. Should be fine
	 * tuned by synchronizing at the class level, or even instance level.
	 */
	public synchronized static int removeAllBos(Class<? extends HmBo> boClass,
			FilterParams filterParams, HmUser user,
			Collection<Long> defaultIds, String feature) throws Exception {
		AccessControl.checkUserAccess(user, feature, CrudOperation.DELETE);
		return QueryUtil.removeBos(boClass, filterParams, user, defaultIds);
	}

	/*
	 * Find and sort BOs, no paging, filter by object ownership.
	 */
	public static <T extends HmBo> List<T> findBos(Class<T> boClass,
			SortParams sortParams, HmUser user, String feature)
			throws Exception {
		AccessControl.checkUserAccess(user, feature, CrudOperation.READ);
		return QueryUtil.executeQuery(boClass, sortParams, null, user);
	}

	/*
	 * Find, sort and filter BOs, no paging.
	 */
	public static <T extends HmBo> List<T> findBos(Class<T> boClass,
			SortParams sortParams, FilterParams filterParams, HmUser user,
			String feature) throws Exception {
		AccessControl.checkUserAccess(user, feature, CrudOperation.READ);
		return QueryUtil.executeQuery(boClass, sortParams, filterParams, user);
	}

	/*
	 * Find and sort BOs, with paging.
	 */
	public static <T extends HmBo> List<T> findBos(Paging<T> paging,
			SortParams sortParams, FilterParams filterParams, HmUser user,
			String feature) throws Exception {
		AccessControl.checkUserAccess(user, feature, CrudOperation.READ);
		return paging.executeQuery(sortParams, filterParams, user, false);
	}
	
	public static <T extends HmBo> List<T> findBosWithNativeSqlSortByNumber(String nativeSql, SortParams sortParams,
			Paging<T> paging, FilterParams filterParams, HmUser user,
			String feature, Class<T> entityClass) throws Exception {
		String sortStr = " order by " + "regexp_replace("+sortParams.getOrderBy()+", '[0-9]+$', '') "+ (sortParams.isAscending() ? "" : " desc")
				+ ", to_number(substring("+sortParams.getOrderBy()+" from '[0-9]+$'),'999999999') " + (sortParams.isAscending() ? "" : " desc");
		return findBosWithNativeSql(nativeSql, sortStr, paging, filterParams, user, feature, entityClass);
	}
	
	public static <T extends HmBo> List<T> findBosWithNativeSql(String nativeSql, String sortString, 
			Paging<T> paging, FilterParams filterParams, HmUser user,
			String feature, Class<T> entityClass) throws Exception {
		AccessControl.checkUserAccess(user, feature, CrudOperation.READ);
		return paging.executeQueryWithNativeSql(nativeSql, sortString, filterParams, user, false, entityClass);
	}

	public static <T extends HmBo> List<?> findBos(Paging<T> paging,
			String sql, SortParams sortParams, FilterParams filterParams, HmUser user,
			String feature) throws Exception {
		AccessControl.checkUserAccess(user, feature, CrudOperation.READ);
		return paging.executeNativeQuery(sql, sortParams, filterParams);
	}
	
	/*
	 * Find and sort BOs, with Lazy field loaded
	 */
	public static <T extends HmBo> List<T> findBos(Paging<T> paging,
			SortParams sortParams, FilterParams filterParams, HmUser user,
			String feature, QueryBo queryBo) throws Exception {
		AccessControl.checkUserAccess(user, feature, CrudOperation.READ);
		return paging.executeQuery(sortParams, filterParams, user, queryBo);
	}

	/*
	 * Find and sort BOs, with paging.
	 */
	public static <T extends HmBo> List<T> findBos(Paging<T> paging,
			SortParams sortParams, FilterParams filterParams, HmUser user,
			String feature, Long domainId) throws Exception {
		AccessControl.checkUserAccess(user, feature, CrudOperation.READ);
		return paging.executeQuery(sortParams, filterParams, domainId);
	}

	public static <T extends HmBo> T findBoByAttribute(Class<T> boClass,
			String attrName, String attrValue, HmUser user, String feature)
			throws Exception {
		AccessControl.checkUserAccess(user, feature, CrudOperation.READ);
		T hmBo = QueryUtil.findBoByAttribute(boClass, attrName, attrValue);
		if (null != hmBo
				&& null != hmBo.getOwner()
				&& null != QueryUtil.getDomainFilter(user)
				&& !HmDomain.GLOBAL_DOMAIN.equals(hmBo.getOwner()
						.getDomainName())
				&& !hmBo.getOwner().getId().equals(
						QueryUtil.getDependentDomainFilter(user))) {
			hmBo = null;
		}
		return hmBo;
	}

	public static MapMgmt getMapMgmt() {
		return MapMgmtImpl.getInstance();
	}

	public static LocationTracking getLocationTracking() {
		return LocationTrackingImpl.getInstance();
	}

	public static TrapMgmt getTrapMgmt() {
		return TrapMgmtImpl.getInstance();
	}

	public static AccessControl getAccessControl() {
		return AccessControlImpl.getInstance();
	}

	public static BoEventMgmt getBoEventMgmt() {
		return BoEventMgmtImpl.getInstance();
	}

	public static MapHierarchyCache getMapHierarchyCache() {
		return MapHierarchyCacheImpl.getInstance();
	}

	public static HiveApMgmt getHiveApMgmt() {
		return HiveApMgmtImpl.getInstance();
	}

	public static DomainMgmt getDomainMgmt() {
		return DomainMgmtImpl.getInstance();
	}

	public static IdpMgmt getIdpMgmt() {
		return IdpMgmtImpl.getInstance();
	}

	public static PlannedApMgmt getPlannedApMgmt() {
		return new MapsAction.PlannedApMgmtImpl();
		//return PlannedApMgmtImpl.getInstance();
	}

}