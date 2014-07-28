package com.ah.bo.mgmt;

/*
 * @author Chris Scheers
 */

public class SortParams {

	public SortParams() {
	}

	public SortParams(String orderBy) {
		this.orderBy = orderBy;
	}

	public SortParams(String orderBy, boolean ascending) {
		this.orderBy = orderBy;
		this.ascending = ascending;
	}

	private String orderBy = null;

	private boolean ascending = true;

	private String primaryOrderBy = null;

	private boolean primaryAscending = true;
	
	//used to indicate the sorting column cannot be got from a query
	private String extOrderBy = null;
	
	private boolean extAscending = true;
	
	private boolean orderByNumber = false;
	
	private String url;
	
	private boolean orderByIp = false;

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public void setPrimaryOrderBy(String primaryOrderBy) {
		this.primaryOrderBy = primaryOrderBy;
	}

	public void setPrimaryAscending(boolean primaryAscending) {
		this.primaryAscending = primaryAscending;
	}

	public String getExtOrderBy() {
		return extOrderBy;
	}

	public void setExtOrderBy(String extOrderBy) {
		this.extOrderBy = extOrderBy;
	}

	public boolean isExtAscending() {
		return extAscending;
	}

	public void setExtAscending(boolean extAscending) {
		this.extAscending = extAscending;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getQuery() {
		if (null != primaryOrderBy && null != orderBy) {
			return " order by " + primaryOrderBy
					+ (primaryAscending ? "," : " desc,") 
					+ (orderByIp ? "inet("+orderBy+")" : orderBy)
					+ (ascending ? "" : " desc");
		} else if (null != primaryOrderBy) {
			return " order by " + primaryOrderBy
					+ (primaryAscending ? "" : " desc");
		} else if (null != orderBy) {
			return " order by " 
					+(orderByIp ? "inet("+orderBy+")" : orderBy) 
					+ (ascending ? "" : " desc");
		} else {
			return "";
		}
	}

	public boolean isOrderByNumber() {
		return orderByNumber;
	}

	public void setOrderByNumber(boolean orderByNumber) {
		this.orderByNumber = orderByNumber;
	}
	
	public boolean isOrderByIp() {
		return orderByIp;
	}

	public void setOrderByIp(boolean orderByIp) {
		this.orderByIp = orderByIp;
	}
}
