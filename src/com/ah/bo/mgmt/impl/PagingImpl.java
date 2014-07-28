package com.ah.bo.mgmt.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.ah.be.common.DBOperationUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.GroupByParams;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.performance.AhClientSession;
import com.ah.util.Tracer;

public class PagingImpl <T extends HmBo> implements Paging<T> {

	private static final Tracer log = new Tracer(PagingImpl.class
			.getSimpleName());

	public PagingImpl(Class<T> boClass) {
		this.boClass = boClass;
		this.clearRowCount();
		this.clearNext();
	}

	/*
	 * Paging
	 */
	private long rowCount;

	private int pageIndex = 1;

	private int pageSize = DEFAULT_PAGE_SIZE;

	private final Class<T> boClass;

	private Set<Long> selectedIds;

	private boolean firstPage, nextPage, previousPage;

	private boolean lazyRowCount = false;

	private boolean plus = false;

	// for VHM used. When super user view list table, the other domain entries
	// is disabled selection. So the available entry should be below number;
	private long availableRowCount;

	private int availablePageRowCount;

	public long getAvailableRowCount() {
		return availableRowCount;
	}

	public int getAvailablePageRowCount() {
		return availablePageRowCount;
	}

	/*
	 * Total row count. If -1 then will be recalculated in next query.
	 * 
	 * @see com.ah.util.Paging#getRowCount()
	 */
	public long getRowCount() {
		return rowCount;
	}

	/*
	 * Force recalculation of row count
	 * 
	 * @see com.ah.util.Paging#clearRowCount()
	 */
	public void clearRowCount() {
		if (!lazyRowCount) {
			rowCount = -1;
		} else if (!firstPage && !nextPage && !previousPage) {
			rowCount = -1;
		}
	}

	/*
	 * Total page count is always calculated from total row count
	 * 
	 * @see com.ah.util.Paging#getPageCount()
	 */
	public int getPageCount() {
		if (rowCount < 0) {
			// Row Count needs to be recalculated
			return -1;
		}
		int pageCount = (int) rowCount / pageSize;
		if (rowCount % pageSize > 0) {
			pageCount++;
		}
		return pageCount;
	}

	/*
	 * Make sure that page index is never higher than total page count.
	 * 
	 * @see com.ah.util.Paging#getPageIndex()
	 */
	public int getPageIndex() {
		int pageCount = getPageCount();
		if (pageIndex > pageCount || pageIndex < 1) {
			// Go to last page
			pageIndex = pageCount;
		}
		return pageIndex < 1 ? 1 : pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		firstPage = pageIndex == 1;
		nextPage = pageIndex == this.pageIndex + 1;
		previousPage = pageIndex == this.pageIndex - 1;
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getFirstResult() {
		return (getPageIndex() - 1) * getPageSize();
	}

	public void setSelectedIds(Set<Long> selectedIds) {
		this.selectedIds = selectedIds;
	}

	public boolean hasNext() {
		return rowCount < 0 || getPageIndex() < getPageCount();
	}

	public Paging<T> next() {
		if (rowCount > 0) {
			pageIndex++;
		}
		return this;
	}

	public void setLazyRowCount(boolean lazyRowCount) {
		this.lazyRowCount = lazyRowCount;
	}

	public void clearNext() {
		firstPage = false;
		nextPage = false;
		previousPage = false;
	}

	public String getPagePlus() {
		return plus ? "+" : "";
	}

	@SuppressWarnings("unchecked")
	public List<T> executeQuery(SortParams sortParams, FilterParams filterParams) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			em.flush(); // Before query

			if (rowCount < 0) {
				if(boClass == AhClientSession.class) {
					List<?> rs = DBOperationUtil.executeQuery("select count(*) from ah_clientsession",
							null,filterParams);
					rowCount = Long.parseLong(rs.get(0).toString());
				}
				else {
					rowCount = (Long) QueryUtil.createQuery(
							em,
							"select count(bo) from " + boClass.getSimpleName()
									+ " bo", null, filterParams).getSingleResult();
				}
				log.debug("executeQuery", "Row count: " + rowCount);
			}

			List<T> hmBos;
			if (rowCount == 0) {
				hmBos = new ArrayList<T>();
			} else {
				if(boClass == AhClientSession.class) {
					hmBos = (List<T>)DBOperationUtil.executeQuery(boClass, sortParams, 
							filterParams, null, getPageSize(),getFirstResult());
				}
				else {
					Query query = QueryUtil.createQuery(em, boClass, sortParams,
							filterParams);
					hmBos = query.setFirstResult(getFirstResult()).setMaxResults(
							getPageSize()).getResultList();
				}
			}

			if (selectedIds != null) {
				for (HmBo hmBo : hmBos) {
					hmBo.setSelected(selectedIds.contains(hmBo.getId()));
				}
			}

			for (HmBo hmBo : hmBos) {
				if (hmBo.getOwner() != null) {
					// Just to trigger a load
					hmBo.getOwner().getId();
				}
			}

			tx.commit();
			// must be the same.
			availableRowCount = rowCount;
			availablePageRowCount = hmBos.size();
			return hmBos;
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			log.error("executeQuery", "Execute query failed.", e);
			throw e;
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> executeQuery(SortParams sortParams, FilterParams filterParams,
			HmUser user, boolean refresh) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Long domainId = QueryUtil.getDomainFilter(user);
			Long dependentDomainId = QueryUtil.getDependentDomainFilter(user);
			if (lazyRowCount && pageIndex != -1) {
				if (nextPage || previousPage) {
					if (pageIndex == getPageCount()) {
						if (!setEstimatedRowCount(em, sortParams, filterParams,
								domainId, dependentDomainId)) {
							setRowCount(em, filterParams, domainId,
									dependentDomainId);
						}
					}
				}
				if (!firstPage && !nextPage && !previousPage) {
					if (!setEstimatedRowCount(em, sortParams, filterParams,
							domainId, dependentDomainId)) {
						setRowCount(em, filterParams, domainId,
								dependentDomainId);
					}
				}
			} else if (rowCount < 0) {
				plus = false;
				setRowCount(em, filterParams, domainId, dependentDomainId);
			}

			List<T> hmBos;
			if (rowCount == 0) {
				hmBos = new ArrayList<T>();
			} else {
				if(boClass == AhClientSession.class) {
					hmBos = (List<T>)DBOperationUtil.executeQuery(boClass, sortParams, filterParams, domainId, getPageSize(),getFirstResult());
				}
				else {
					Query query = QueryUtil.createQuery(em, boClass, sortParams,
							filterParams, domainId);
					hmBos = query.setFirstResult(getFirstResult()).setMaxResults(
							getPageSize()).getResultList();
				}
			}

			if (selectedIds != null) {
				for (HmBo hmBo : hmBos) {
					hmBo.setSelected(selectedIds.contains(hmBo.getId()));
				}
			}

			availablePageRowCount = 0;
			HmDomain global = BoMgmt.getDomainMgmt().getGlobalDomain();
			for (HmBo hmBo : hmBos) {
				if (hmBo.getOwner() != null) {
					// Just to trigger a load
					Long ownerId = hmBo.getOwner().getId();
					if (ownerId.equals(dependentDomainId)
							|| (null != global && ownerId
									.equals(global.getId()))) {
						availablePageRowCount++;
					}
				} else {
					availablePageRowCount++;
				}
			}
			if (availableRowCount < 0) {
				availableRowCount = availablePageRowCount;
			}

			log.debug("executeQuery", "Row count: " + rowCount
					+ ", available Row count: " + availableRowCount);
			tx.commit();
			return hmBos;
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			log.error("executeQuery", "Execute query failed.", e);
			throw e;
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}

	private void setRowCount(EntityManager em, FilterParams filterParams,
			Long domainId, Long dependentDomainId) {
		if(boClass == AhClientSession.class) {
			List<?> rs = DBOperationUtil.executeQuery("select count(*) from ah_clientsession",
					null,filterParams,dependentDomainId);
			availableRowCount = Long.parseLong(rs.get(0).toString());
			if (dependentDomainId.equals(domainId)) {
				rowCount = availableRowCount;
			} else {
				rs = DBOperationUtil.executeQuery("select count(*) from ah_clientsession",
						null,filterParams,domainId);
				rowCount = Long.parseLong(rs.get(0).toString());
			}
		}
		else {
			availableRowCount = (Long) QueryUtil.createQuery(em,
					"select count(bo) from " + boClass.getSimpleName() + " bo",
					null, filterParams, dependentDomainId, null).getSingleResult();
			if (dependentDomainId.equals(domainId)) {
				rowCount = availableRowCount;
			} else {
				rowCount = (Long) QueryUtil.createQuery(em,
						"select count(bo) from " + boClass.getSimpleName() + " bo",
						null, filterParams, domainId, null).getSingleResult();
			}
		}
	}

	private void setRowCount(EntityManager em, FilterParams filterParams,
			GroupByParams groupByParams, Long domainId, Long dependentDomainId) {
		availableRowCount = QueryUtil.createQuery(em,
				"select count(bo) from " + boClass.getSimpleName() + " bo",
				null, filterParams, groupByParams, dependentDomainId)
				.getResultList().size();
		if (dependentDomainId.equals(domainId)) {
			rowCount = availableRowCount;
		} else {
			rowCount = QueryUtil.createQuery(em,
					"select count(bo) from " + boClass.getSimpleName() + " bo",
					null, filterParams, groupByParams, domainId)
					.getResultList().size();
		}
	}

	private boolean setEstimatedRowCount(EntityManager em,
			SortParams sortParam, FilterParams filterParams, Long domainId,
			Long dependentDomainId) {
		int lookAhead = 50;
		int firstResult = (pageIndex + lookAhead) * pageSize + 1;
		if (pageIndex == 1) {
			firstResult -= pageSize;
		}

		List<?> ids = queryEstimatedRowCount(em, filterParams, domainId,
				firstResult);
		plus = ids.size() > 0;
		if (!plus) {
			return false;
		}
		rowCount = firstResult - 1;
		if (dependentDomainId.equals(domainId)) {
			availableRowCount = rowCount;
		} else {
			firstResult /= 2;
			ids = queryEstimatedRowCount(em, filterParams, dependentDomainId,
					firstResult);
			if (ids.size() > 0) {
				availableRowCount = firstResult - 1;
			} else {
				availableRowCount = -1;
			}
		}
		return true;
	}

	private List<?> queryEstimatedRowCount(EntityManager em,
			FilterParams filterParams, Long domainId, int firstResult) {
		return QueryUtil.createQuery(em,
				"select id from " + boClass.getSimpleName() + " bo", null,
				filterParams, domainId, null).setFirstResult(firstResult)
				.setMaxResults(1).getResultList();
	}

//	private void setSortIds(List<HmBo> hmBos, String name) {
//		String methodName = "get" + name.substring(0, 1).toUpperCase()
//				+ name.substring(1);
//		Method method;
//		try {
//			method = boClass.getMethod(methodName);
//		} catch (NoSuchMethodException nsme) {
//			log.debug("getSortId", "Method: " + methodName + " not found.");
//			return;
//		}
//		Object firstId = getSortId(hmBos.get(0), method);
//		Object lastId = getSortId(hmBos.get(hmBos.size() - 1), method);
//	}
//
//	private Object getSortId(HmBo hmBo, Method method) {
//		try {
//			return method.invoke(hmBo, (Object[]) null);
//		} catch (IllegalAccessException iae) {
//			log.debug("getSortId", "IllegalAccessException" + iae);
//		} catch (InvocationTargetException ite) {
//			log.debug("getSortId", "InvocationTargetException" + ite);
//		}
//		return null;
//	}

	@SuppressWarnings("unchecked")
	public List<T> executeQuery(SortParams sortParams, FilterParams filterParams,
			Long domainId) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			if (rowCount < 0) {
				if(boClass == AhClientSession.class) {
					List<?> rs = DBOperationUtil.executeQuery("select count(*) from ah_clientsession",
							null,filterParams,domainId);
					rowCount = Long.parseLong(rs.get(0).toString());
				}
				else {
					rowCount = (Long) QueryUtil.createQuery(
							em,
							"select count(bo) from " + boClass.getSimpleName()
									+ " bo", null, filterParams, domainId, null)
							.getSingleResult();
				}
				log.debug("executeQuery", "Row count: " + rowCount);
			}

			List<T> hmBos;
			if (rowCount == 0) {
				hmBos = new ArrayList<T>();
			} else {
				if(boClass == AhClientSession.class) {
					hmBos = (List<T>)DBOperationUtil.executeQuery(boClass, sortParams, 
							filterParams, domainId, getPageSize(),getFirstResult());
				}
				else {
					Query query = QueryUtil.createQuery(em, boClass, sortParams,
							filterParams, domainId);
					hmBos = query.setFirstResult(getFirstResult()).setMaxResults(
							getPageSize()).getResultList();
				}
			}

			if (selectedIds != null) {
				for (HmBo hmBo : hmBos) {
					hmBo.setSelected(selectedIds.contains(hmBo.getId()));
				}
			}

			for (HmBo hmBo : hmBos) {
				if (hmBo.getOwner() != null) {
					// Just to trigger a load
					hmBo.getOwner().getId();
				}
			}

			tx.commit();
			// must be the same.
			availableRowCount = rowCount;
			availablePageRowCount = hmBos.size();
			return hmBos;
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			log.error("executeQuery", "Execute query failed.", e);
			throw e;
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}
	
	/**
	 * paging with lazy objects
	 * 
	 * @param sortParams -
	 * @param filterParams -
	 * @return -
	 * @author Joseph Chen
	 */
	@SuppressWarnings("unchecked")
	public List<T> executeQuery(SortParams sortParams, FilterParams filterParams,
			HmUser hmUser, QueryBo queryBo) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			if (rowCount < 0) {
				if(boClass == AhClientSession.class) {
					List<?> rs = DBOperationUtil.executeQuery("select count(*) from ah_clientsession",
							null,filterParams);
					rowCount = Long.parseLong(rs.get(0).toString());
				}
				else {
					rowCount = (Long) QueryUtil.createQuery(
							em,
							"select count(bo) from " + boClass.getSimpleName()
									+ " bo", null, filterParams, hmUser, null)
							.getSingleResult();
				}
				log.debug("executeQuery", "Row count: " + rowCount);
			}

			List<T> hmBos;
			if (rowCount == 0) {
				hmBos = new ArrayList<T>();
			} else {
				if(boClass == AhClientSession.class) {
					hmBos = (List<T>)DBOperationUtil.executeQuery(boClass, sortParams, 
							filterParams, QueryUtil.getDomainFilter(hmUser), getPageSize(),getFirstResult());
				}
				else {
					Query query = QueryUtil.createQuery(em, boClass, sortParams,
							filterParams, hmUser);
					hmBos = query.setFirstResult(getFirstResult()).setMaxResults(
							getPageSize()).getResultList();
				}
			}

			if (selectedIds != null) {
				for (HmBo hmBo : hmBos) {
					hmBo.setSelected(selectedIds.contains(hmBo.getId()));
				}
			}

			for (HmBo hmBo : hmBos) {
				if (hmBo.getOwner() != null) {
					// Just to trigger a load
					hmBo.getOwner().getId();
				}
				
				queryBo.load(hmBo);
			}

			tx.commit();
			// must be the same.
			availableRowCount = rowCount;
			availablePageRowCount = hmBos.size();
			return hmBos;
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			log.error("executeQuery", "Execute query failed.", e);
			throw e;
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}

	@SuppressWarnings("unchecked")
	public List executeQuery(SortParams sortParams, FilterParams filterParams,
			GroupByParams groupByParams, HmUser user) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			em.flush(); // Before query

			Long domainId = QueryUtil.getDomainFilter(user);
			Long dependentDomainId = QueryUtil.getDependentDomainFilter(user);
			setRowCount(em, filterParams, groupByParams, domainId,
					dependentDomainId);

			List hmBos;
			if (rowCount == 0) {
				hmBos = new ArrayList();
			} else {
				if(boClass == AhClientSession.class) {
					hmBos = DBOperationUtil.executeQuery(boClass, sortParams,
							filterParams, QueryUtil.getDomainFilter(user), getPageSize(),getFirstResult());
				}
				else {
					Query query = QueryUtil.createQuery(em, groupByParams
							.getSelectString()
							+ " from " + boClass.getSimpleName() + " bo",
							sortParams, filterParams, groupByParams, domainId);
					hmBos = query.setFirstResult(getFirstResult()).setMaxResults(
							getPageSize()).getResultList();
				}
			}
			availablePageRowCount = hmBos.size();
			log.debug("executeQuery", "Row count: " + rowCount
					+ ", available Row count: " + availableRowCount);
			tx.commit();

			return hmBos;
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			log.error("executeQuery", "Execute query failed.", e);
			throw e;
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<T> executeQueryAll(FilterParams filterParams,
			HmUser user, boolean refresh) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Long domainId = QueryUtil.getDomainFilter(user);
			Long dependentDomainId = QueryUtil.getDependentDomainFilter(user);
			if (lazyRowCount && pageIndex != -1) {
				if (nextPage || previousPage) {
					if (pageIndex == getPageCount()) {
						if (!setEstimatedRowCount(em, null, filterParams,
								domainId, dependentDomainId)) {
							setRowCount(em, filterParams, domainId,
									dependentDomainId);
						}
					}
				}
				if (!firstPage && !nextPage && !previousPage) {
					if (!setEstimatedRowCount(em, null, filterParams,
							domainId, dependentDomainId)) {
						setRowCount(em, filterParams, domainId,
								dependentDomainId);
					}
				}
			} else if (rowCount < 0) {
				plus = false;
				setRowCount(em, filterParams, domainId, dependentDomainId);
			}

			List<T> hmBos;
			if (rowCount == 0) {
				hmBos = new ArrayList<T>();
			} else {
				if(boClass == AhClientSession.class) {
					hmBos = (List<T>)DBOperationUtil.executeQuery(boClass, null, filterParams, domainId, getPageSize(),getFirstResult());
				}
				else {
					Query query = QueryUtil.createQuery(em, boClass, null,
							filterParams, domainId);
					hmBos = (List<T>)query.setFirstResult(0).setMaxResults(
							MAX_RESULTS).getResultList();
				}
			}

			if (selectedIds != null) {
				for (HmBo hmBo : hmBos) {
					hmBo.setSelected(selectedIds.contains(hmBo.getId()));
				}
			}

			availablePageRowCount = 0;
			HmDomain global = BoMgmt.getDomainMgmt().getGlobalDomain();
			for (HmBo hmBo : hmBos) {
				if (hmBo.getOwner() != null) {
					// Just to trigger a load
					Long ownerId = hmBo.getOwner().getId();
					if (ownerId.equals(dependentDomainId)
							|| (null != global && ownerId
									.equals(global.getId()))) {
						availablePageRowCount++;
					}
				} else {
					availablePageRowCount++;
				}
			}
			if (availableRowCount < 0) {
				availableRowCount = availablePageRowCount;
			}

			log.debug("executeQueryAll", "Row count: " + rowCount
					+ ", available Row count: " + availableRowCount);
			tx.commit();
			return hmBos;
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			log.error("executeQueryAll", "Execute query failed.", e);
			throw e;
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}
	
	public List<T> getAPageFromObjects(List<T> objects) {
		if (objects == null || objects.isEmpty()) {
			return new ArrayList<T>();
		}
		int fromIndex = getFirstResult();
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		int toIndex = fromIndex + getPageSize();
		if (toIndex > objects.size()) {
			toIndex = objects.size();
		}
		return objects.subList(fromIndex , toIndex);
	}
	
	@SuppressWarnings("unchecked")
	public List<T> executeQueryWithNativeSql(String nativeSql, String sortString, FilterParams filterParams,
			HmUser user, boolean refresh, Class entityClass) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Long domainId = QueryUtil.getDomainFilter(user);
			Long dependentDomainId = QueryUtil.getDependentDomainFilter(user);
			if (lazyRowCount && pageIndex != -1) {
				if (nextPage || previousPage) {
					if (pageIndex == getPageCount()) {
						if (!setEstimatedRowCount(em, null, filterParams,
								domainId, dependentDomainId)) {
							setRowCount(em, filterParams, domainId,
									dependentDomainId);
						}
					}
				}
				if (!firstPage && !nextPage && !previousPage) {
					if (!setEstimatedRowCount(em, null, filterParams,
							domainId, dependentDomainId)) {
						setRowCount(em, filterParams, domainId,
								dependentDomainId);
					}
				}
			} else if (rowCount < 0) {
				plus = false;
				setRowCount(em, filterParams, domainId, dependentDomainId);
			}

			List<T> hmBos;
			if (rowCount == 0) {
				hmBos = new ArrayList<T>();
			} else {
				if(boClass == AhClientSession.class) {
					hmBos = (List<T>)DBOperationUtil.executeQuery(boClass, null, filterParams, domainId, getPageSize(),getFirstResult());
				}
				else {
					StringBuilder sb = new StringBuilder(QueryUtil.getQuery(nativeSql, null, filterParams, null, dependentDomainId, null));
					sb.append(" ").append(sortString);
					Query query = em.createNativeQuery(sb.toString(), entityClass);
					QueryUtil.addQueryParameters(query, filterParams, domainId == null ? dependentDomainId : domainId, null);
					hmBos = (List<T>)query.setFirstResult(getFirstResult()).setMaxResults(
							getPageSize()).getResultList();
				}
			}

			if (selectedIds != null) {
				for (HmBo hmBo : hmBos) {
					hmBo.setSelected(selectedIds.contains(hmBo.getId()));
				}
			}

			availablePageRowCount = 0;
			HmDomain global = BoMgmt.getDomainMgmt().getGlobalDomain();
			for (HmBo hmBo : hmBos) {
				if (hmBo.getOwner() != null) {
					// Just to trigger a load
					Long ownerId = hmBo.getOwner().getId();
					if (ownerId.equals(dependentDomainId)
							|| (null != global && ownerId
									.equals(global.getId()))) {
						availablePageRowCount++;
					}
				} else {
					availablePageRowCount++;
				}
			}
			if (availableRowCount < 0) {
				availableRowCount = availablePageRowCount;
			}

			log.debug("executeQueryWithNativeSql", "Row count: " + rowCount
					+ ", available Row count: " + availableRowCount);
			tx.commit();
			return hmBos;
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			log.error("executeQueryWithNativeSql", "Execute query failed.", e);
			throw e;
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}

	@Override
	public void setRowCount(long rowCount) {
		this.rowCount=rowCount;
	}

	@Override
	public List<?> executeNativeQuery(String sql, SortParams sortParams,
			FilterParams filterParams) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			StringBuilder sb = new StringBuilder(QueryUtil.getQuery(sql, sortParams, filterParams, null, null, null));
			Query query = em.createNativeQuery(sb.toString());
			QueryUtil.addQueryParameters(query, filterParams, null, null);

			String countSql = "select count(1) from (" + sb.toString() + ") as mycounttbltmp1";
			Query countQuery = em.createNativeQuery(countSql);
			QueryUtil.addQueryParameters(countQuery, filterParams, null, null);
			
			if (rowCount < 0) {
				rowCount = ((BigInteger) countQuery.getSingleResult()).longValue();
				log.debug("executeQuery", "Row count: " + rowCount);
			}

			List<?> resultBos;
			if (rowCount == 0) {
				resultBos = new ArrayList<>();
			} else {
				resultBos = query.setFirstResult(getFirstResult()).setMaxResults(
						getPageSize()).getResultList();
			}

			tx.commit();
			// must be the same.
			availableRowCount = rowCount;
			availablePageRowCount = resultBos.size();
			return resultBos;
		} catch (RuntimeException e) {
			QueryUtil.rollback(tx);
			log.error("executeQuery", "Execute query failed.", e);
			throw e;
		} finally {
			QueryUtil.closeEntityManager(em);
		}
	}

	@Override
	public boolean isSelectedId(Long id) {
		return selectedIds.contains(id);
	}
}