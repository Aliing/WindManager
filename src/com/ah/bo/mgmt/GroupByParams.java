package com.ah.bo.mgmt;

/*
 * @author Chris Scheers
 */

public class GroupByParams {

	public GroupByParams() {
	}

	public GroupByParams(String groupBys[]) {
		this.groupBys = groupBys;
	}

	private String groupBys[] = null;

	private String url;

	public String[] getGroupBys() {
		return groupBys;
	}

	public void setGroupBys(String[] groupBys) {
		this.groupBys = groupBys;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSelectString() {
		if (groupBys == null) {
			return "";
		}
		String strSelect = "";
		for (int i = 0; i < groupBys.length; i++) {
			strSelect = strSelect + groupBys[i];
			if (i != groupBys.length - 1) {
				strSelect = strSelect + ", ";
			}
		}
		return "select " + strSelect;
	}

	public String getQuery() {
		if (groupBys == null) {
			return "";
		}
		String strGroupBy = "";
		for (int i = 0; i < groupBys.length; i++) {
			strGroupBy = strGroupBy + groupBys[i];
			if (i != groupBys.length - 1) {
				strGroupBy = strGroupBy + ", ";
			}
		}
		return " group by " + strGroupBy;
	}
}
