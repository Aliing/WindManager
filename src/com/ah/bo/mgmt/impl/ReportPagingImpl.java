package com.ah.bo.mgmt.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.ah.be.common.DBOperationUtil;
import com.ah.bo.HmBo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.GroupByParams;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.performance.AhClientSession;
import com.ah.util.Tracer;

public class ReportPagingImpl <T extends Object> {

	private static final Tracer log = new Tracer(ReportPagingImpl.class
			.getSimpleName());

	public ReportPagingImpl(Class<HmBo> boClass) {
		this.boClass = boClass;
		this.clearRowCount();
		this.clearNext();
	}

	/*
	 * Paging
	 */
	private long rowCount;

	private int pageIndex = 1;

	private int pageSize = Paging.DEFAULT_PAGE_SIZE;

	private final Class<HmBo> boClass;

	private boolean firstPage, nextPage, previousPage;

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
		if (!firstPage && !nextPage && !previousPage) {
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

	public boolean hasNext() {
		return rowCount < 0 || getPageIndex() < getPageCount();
	}

	public ReportPagingImpl<T> next() {
		if (rowCount > 0) {
			pageIndex++;
		}
		return this;
	}

	public void clearNext() {
		firstPage = false;
		nextPage = false;
		previousPage = false;
	}

	@SuppressWarnings("unchecked")
	public List<T> executeQuery(SortParams sortParams, FilterParams filterParams) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			setRowCount(em, filterParams, (Long)null);

			List<T> hmBos;
			if (rowCount == 0) {
				hmBos = new ArrayList<T>();
			} else {
				if(boClass.getSimpleName().equals(AhClientSession.class.getSimpleName())) {
					hmBos = (List<T>)DBOperationUtil.executeQuery(boClass, sortParams, 
							filterParams, null,getPageSize(),getFirstResult());
				} else {
					Query query = QueryUtil.createQuery(em, boClass, sortParams,
							filterParams);
					hmBos = query.setFirstResult(getFirstResult()).setMaxResults(
							getPageSize()).getResultList();
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
	public List<T> executeQuery(String queryString, SortParams sortParams, FilterParams filterParams) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			setRowCount(em, filterParams, (Long)null);

			List<T> hmBos;
			if (rowCount == 0) {
				hmBos = new ArrayList<T>();
			} else {
				if(boClass.getSimpleName().equals(AhClientSession.class.getSimpleName())) {
					hmBos = (List<T>)DBOperationUtil.executeQuery(queryString, sortParams, 
							filterParams, null,null,getPageSize(),getFirstResult());
				}
				else {
					Query query = QueryUtil.createQuery(em, queryString, sortParams,
							filterParams);
					hmBos = query.setFirstResult(getFirstResult()).setMaxResults(
							getPageSize()).getResultList();
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
	public List<T> executeQuery(String queryString, SortParams sortParams, FilterParams filterParams, Long domainId) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();
			
			setRowCount(em, filterParams, domainId);

			List<T> hmBos;
			if (rowCount == 0) {
				hmBos = new ArrayList<T>();
			} else {
				if(boClass.getSimpleName().equals(AhClientSession.class.getSimpleName())) {
					hmBos = (List<T>)DBOperationUtil.executeQuery(queryString, sortParams, 
							filterParams, null,domainId,getPageSize(),getFirstResult());
				} else {
					Query query = QueryUtil.createQuery(em, queryString, sortParams,
							filterParams,domainId);
					hmBos = query.setFirstResult(getFirstResult()).setMaxResults(
							getPageSize()).getResultList();
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
	
	private void setRowCount(EntityManager em, FilterParams filterParams, Long domainId) {
		if(boClass.getSimpleName().equals(AhClientSession.class.getSimpleName())) {
			List<?> rs = DBOperationUtil.executeQuery("select count(*) from ah_clientsession",
					null,filterParams, domainId);
			if (rs.isEmpty()) {
				availableRowCount = 0;
			} else {
				availableRowCount = Long.parseLong(rs.get(0).toString());
			}
			rowCount = availableRowCount;
		} else {
			availableRowCount =(Long) QueryUtil.createQuery(em,
					"select count(bo) from " + boClass.getSimpleName() + " bo",
					null, filterParams, domainId)
					.getSingleResult();
			rowCount = availableRowCount;
		}
	}

	private void setRowCount(EntityManager em, FilterParams filterParams,
			GroupByParams groupByParams) {
		if(boClass.getSimpleName().equals(AhClientSession.class.getSimpleName())) {
			List<?> rs = DBOperationUtil.executeQuery("select count(*) from ah_clientsession",
					null,filterParams,groupByParams, null);
			if (rs.isEmpty()) {
				availableRowCount = 0;
			} else {
				availableRowCount =rs.size();
			}
			rowCount = availableRowCount;
		} else {
			availableRowCount = QueryUtil.createQuery(em,
					"select count(bo) from " + boClass.getSimpleName() + " bo",
					null, filterParams, groupByParams, null)
					.getResultList().size();
			rowCount = availableRowCount;
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> executeQuery(SortParams sortParams, FilterParams filterParams,
			Long domainId) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			setRowCount(em, filterParams, domainId);

			List<T> hmBos;
			if (rowCount == 0) {
				hmBos = new ArrayList<T>();
			} else {
				if(boClass.getSimpleName().equals(AhClientSession.class.getSimpleName())) {
					hmBos = (List<T>)DBOperationUtil.executeQuery(boClass, sortParams, 
							filterParams, domainId,getPageSize(),getFirstResult());
				} else {
					Query query = QueryUtil.createQuery(em, boClass, sortParams,
							filterParams, domainId);
					hmBos = query.setFirstResult(getFirstResult()).setMaxResults(
							getPageSize()).getResultList();
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
	public List executeQuery(SortParams sortParams, FilterParams filterParams,
			GroupByParams groupByParams) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			setRowCount(em, filterParams, groupByParams);

			List hmBos;
			if (rowCount == 0) {
				hmBos = new ArrayList();
			} else {
					Query query = QueryUtil.createQuery(em, groupByParams
							.getSelectString()
							+ " from " + boClass.getSimpleName() + " bo",
							sortParams, filterParams, groupByParams, null);
					hmBos = query.setFirstResult(getFirstResult()).setMaxResults(
							getPageSize()).getResultList();
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
	public List executeQuery(String queryString, SortParams sortParams, FilterParams filterParams,
			GroupByParams groupByParams) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = QueryUtil.getEntityManager();
			tx = em.getTransaction();
			tx.begin();

			setRowCount(em, filterParams, groupByParams);

			List hmBos;
			if (rowCount == 0) {
				hmBos = new ArrayList();
			} else {
				if(boClass.getSimpleName().equals(AhClientSession.class.getSimpleName())) {
					hmBos = DBOperationUtil.executeQuery(queryString, sortParams,
							filterParams, groupByParams, null,getPageSize(),getFirstResult());
				}
				else {
					Query query = QueryUtil.createQuery(em, queryString + " from " + boClass.getSimpleName(),
							sortParams, filterParams, groupByParams, null);
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

}