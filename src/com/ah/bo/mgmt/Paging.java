package com.ah.bo.mgmt;

import java.util.List;
import java.util.Set;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmUser;

public interface Paging<T extends HmBo> {

	/*
	 * Maximum results if no paging is used.
	 */
	public static final int MAX_RESULTS = 100000;

	public static final int DEFAULT_PAGE_SIZE = 15;

	/*
	 * Total number of pages
	 */
	public long getRowCount();
	
	public void setRowCount(long rowCount);

	/*
	 * Force recalculation of row count
	 */
	public void clearRowCount();

	/*
	 * Total number of pages
	 */
	public int getPageCount();

	/*
	 * for VHM, when super user view list table, the other domain entries is
	 * disabled selection. So the available entry is different.
	 */
	public long getAvailableRowCount();

	/*
	 * for VHM, when super user view list table, the other domain entries is
	 * disabled selection. So the available entry is different.
	 */
	public int getAvailablePageRowCount();

	/*
	 * Current page index
	 */
	public int getPageIndex();

	public void setPageIndex(int pageIndex);

	/*
	 * Number of rows per page
	 */
	public int getPageSize();

	public void setPageSize(int pageSize);

	/*
	 * Row index of first result
	 */
	public int getFirstResult();

	public void setSelectedIds(Set<Long> selectedIds);

	/*
	 * Any pages left ?
	 */
	public boolean hasNext();

	/*
	 * Just use the page index
	 */
	public Paging<T> next();

	public void setLazyRowCount(boolean lazyRowCount);

	public void clearNext();

	public String getPagePlus();

	/*
	 * Execute query which has sorting/filtering embedded.
	 */
	public List<T> executeQuery(SortParams sortParams, FilterParams filterParams);

	/*
	 * Execute query which has sorting/filtering embedded, also filter by user.
	 */
	public List<T> executeQuery(SortParams sortParams, FilterParams filterParams,
			HmUser user, boolean refresh);

	/*
	 * Execute query which has sorting/filtering embedded, filter by domain.
	 */
	public List<T> executeQuery(SortParams sortParams, FilterParams filterParams,
			Long domainId);
	
	/*
	 * Execute query which has sorting/filtering embedded, filter by domain,
	 * with lazy objects
	 */
	public List<T> executeQuery(SortParams sortParams, FilterParams filterParams,
			HmUser hmUser, QueryBo queryBo);

	/*
	 * Execute query which has sorting/filtering embedded.
	 */
	public List<?> executeQuery(SortParams sortParams, FilterParams filterParams,
			GroupByParams groupByParams, HmUser user);
	
	/*
	 * Execute query which has filtering embedded, also filter by user,
	 * return all proper objects.
	 */
	public List<T> executeQueryAll(FilterParams filterParams,
			HmUser user, boolean refresh);

	/*
	 * Get a certain page from set of objects.
	 */
	public List<T> getAPageFromObjects(List<T> objects);
	
	/*
	 * Execute query which has filtering embedded, also filter by user,
	 * return all proper objects.
	 */
	public List<T> executeQueryWithNativeSql(String nativeSql, String sortString, FilterParams filterParams,
			HmUser user, boolean refresh, Class entityClass);
	
	/**
	 * used for those queries which need union, not only for certain BO class
	 * @param sql :native sql
	 * @param sortParams
	 * @param filterParams
	 * @return
	 */
	public List<?> executeNativeQuery(String sql, SortParams sortParams, FilterParams filterParams);
	
	/**
	 * check whether an id is in selected id set
	 * @param id
	 * @return
	 */
	public boolean isSelectedId(Long id);
}