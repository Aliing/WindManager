/**
 * @filename			SearchResultSet.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import java.util.List;

import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.PagingImpl;

public class SearchResultSet {

	private String				searchKey;

	private String				searchKeyShow;

	private List<ResultEntry>	pageResult;

	private int					pageSize		= 20;

	private boolean				config			= true;

	private boolean				hiveap			= true;

	private boolean				client			= true;

	private boolean				fault			= false;

	private boolean				admin			= true;

	private boolean				tool			= true;

	private int					totalCount;

	private HmUser				userContext;

	private SortParams			sortParams		= new SortParams("id", true);

	private FilterParams		filterParams	= null;

	public SearchResultSet() {
		super();
	}

	public void init() {
		enablePaging();
		filterParams = new FilterParams("userName = :s1 and userDomainId = :s2", new Object[] {
				userContext.getUserName(), userContext.getDomain().getId() });
		preparePageSearchResult();
	}

	public int getPageCount() {
		return paging == null ? 0 : paging.getPageCount();
	}

	public int getPageIndex() {
		return paging.getPageIndex();
	}

	public void setPageIndex(int pageIndex) {
		paging.setPageIndex(pageIndex);
	}

	public List<ResultEntry> getPageResult() {
		return pageResult;
	}

	public void setPageResult(List<ResultEntry> pageResult) {
		this.pageResult = pageResult;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	private void preparePageSearchResult() {

		List list = paging.executeQuery(sortParams, filterParams);
		pageResult = IndexUtil.convertResult((List<Target>) list, searchKey);
	}

	public void previousPage() {
		int newIndex = paging.getPageIndex() - 1;
		if (newIndex < 1) {
			newIndex = 1;
		}
		paging.setPageIndex(newIndex);

		paging.clearNext();
		paging.clearRowCount();

		preparePageSearchResult();
	}

	public void nextPage() {

		int newIndex = paging.getPageIndex() + 1;
		if (newIndex > getPageCount()) {
			newIndex = getPageCount();
		}
		paging.setPageIndex(newIndex);

		paging.clearNext();
		paging.clearRowCount();

		preparePageSearchResult();
	}

	public void gotoPage(int pageIndex) {
		if (pageIndex < 1) {
			pageIndex = 1;
		}
		if (pageIndex > getPageCount()) {
			pageIndex = getPageCount();
		}
		paging.setPageIndex(pageIndex);

		paging.clearNext();
		paging.clearRowCount();

		preparePageSearchResult();
	}

	private Paging	paging;

	private void enablePaging() {
		if (paging == null) {
			paging = new PagingImpl(Target.class);
		}
		paging.setPageSize(pageSize);
	}

	public void resizePage(int pageSize) {
		this.pageSize = pageSize;
		if (pageSize != 0) {
			paging.setPageSize(pageSize);
		}
		init();
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getSearchKeyShow() {
		return searchKeyShow;
	}

	public void setSearchKeyShow(String searchKeyShow) {
		this.searchKeyShow = searchKeyShow;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isClient() {
		return client;
	}

	public void setClient(boolean client) {
		this.client = client;
	}

	public boolean isConfig() {
		return config;
	}

	public void setConfig(boolean config) {
		this.config = config;
	}

	public boolean isFault() {
		return fault;
	}

	public void setFault(boolean fault) {
		this.fault = fault;
	}

	public boolean isHiveap() {
		return hiveap;
	}

	public void setHiveap(boolean hiveap) {
		this.hiveap = hiveap;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public HmUser getUserContext() {
		return userContext;
	}

	public void setUserContext(HmUser userContext) {
		this.userContext = userContext;
	}

	public boolean isTool() {
		return tool;
	}

	public void setTool(boolean tool) {
		this.tool = tool;
	}
}
