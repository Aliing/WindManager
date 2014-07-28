package com.ah.ui.actions.monitor;

import java.util.List;

import com.ah.bo.performance.AhAssociation;

/**
 *  support paginator in mem for client information
 *@filename		CacheClientPaginator.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-9-14 02:30:29
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
public class CacheClientPaginator {

	private List<AhAssociation>	pageResult;

	private List<AhAssociation>	clientList;

	private int					pageSize	= 12;

	private int					pageIndex	= 1;

	public CacheClientPaginator() {
	}

	public CacheClientPaginator(List<AhAssociation> list) {
		clientList = list;
	}

	public void init() {
		int pageCount = getPageCount();
		if (pageIndex > pageCount) {
			pageIndex = pageCount;
		}
		if (pageIndex <= 0) {
			pageIndex = 1;
		}
		pageResult = caculatePageResult(pageIndex);
	}

	private List<AhAssociation> caculatePageResult(int index) {
		int from = (index - 1) * pageSize;
		int to = from + pageSize;

		if (clientList.size() < to) {
			to = clientList.size();
		}

		return clientList.subList(from, to);
	}

	public List<AhAssociation> getClientList() {
		return clientList;
	}

	public void setClientList(List<AhAssociation> clientList) {
		this.clientList = clientList;
	}

	public List<AhAssociation> getPageResult() {
		return pageResult;
	}

	public int getTotalCount() {
		return clientList.size();
	}

	public int getPageCount() {
		int pageCount = clientList.size() / pageSize;

		if (clientList.size() % pageSize > 0) {
			return pageCount + 1;
		}

		return pageCount;
	}

	public void previousPage() {
		pageIndex--;
		pageResult = caculatePageResult(pageIndex);
	}

	public void nextPage() {
		pageIndex++;
		pageResult = caculatePageResult(pageIndex);
	}

	public void lastPage() {
		pageIndex = getPageCount();
		pageResult = caculatePageResult(pageIndex);
	}

	public void firstPage() {
		pageIndex = 1;
		pageResult = caculatePageResult(pageIndex);
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
}
