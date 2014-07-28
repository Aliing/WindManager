/**
 * @filename			UpgradeLogAction.java
 * @version
 * @author				Joseph Chen
 * @since
 * 
 * Copyright (c) 2006-2008 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.ui.actions.admin;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmUpgradeLog;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;

/**
 * 
 */
public class UpgradeLogAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {
		String forward = globalForward();

		if (forward != null) {
			return forward;
		}

		try {
			if ("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id));
				addLstTitle(" > Edit");
				if (dataSource == null) {
					return prepareBoList();
				} else {
					return INPUT;
				}
			} else if ("update".equals(operation)) {
				return updateBo();
			} else if ("cancel".equals(operation)) {
				baseOperation();
				return prepareUpgradeLogList();
			} else {
				baseOperation();
				return prepareUpgradeLogList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	protected String prepareUpgradeLogList() throws Exception {
		if (sortParams == null) {
			enableSorting();
			sortParams.setAscending(false);
		}
		return prepareBoList();
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_UPGRADELOG);
		setDataSource(HmUpgradeLog.class);
		tableId = HmTableColumn.TABL_UPGRADELOG;
	}

	public HmUpgradeLog getDataSource() {
		return (HmUpgradeLog) dataSource;
	}

	public String getSystemVersion() {
		BeVersionInfo versionInfo = getSessionVersionInfo();
		return versionInfo.getMainVersion() + "r" + versionInfo.getSubVersion();
	}

	public String getUpgradeTime() {
		List<?> list = QueryUtil.executeQuery(HmUpgradeLog.class,
				new SortParams("logTimeStamp.time", false), null, userContext,
				1);
		if (list.isEmpty()) {
			return "";
		}
		HmUpgradeLog upgradeLog = (HmUpgradeLog) list.get(0);
		upgradeLog.getLogTime().setTimeZone(userContext.getTimeZone());
		return upgradeLog.getLogTimeString();
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_FORMER_CONTENT = 1;

	public static final int COLUMN_POST_CONTENT = 2;

	public static final int COLUMN_RECOMMEND_ACTION = 3;

	public static final int COLUMN_ANNOTATION = 4;

	public static final int COLUMN_UPGRADE_TIME = 5;

	/**
	 * get the description of column by id
	 * 
	 * @param id
	 *            -
	 * @return String -
	 */
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_FORMER_CONTENT:
			code = "admin.upgradeLog.formerContent";
			break;
		case COLUMN_POST_CONTENT:
			code = "admin.upgradeLog.postContent";
			break;
		case COLUMN_RECOMMEND_ACTION:
			code = "admin.upgradeLog.recommendAction";
			break;
		case COLUMN_ANNOTATION:
			code = "admin.upgradeLog.annotation";
			break;
		case COLUMN_UPGRADE_TIME:
			code = "admin.upgradeLog.upgradeTime";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();

		columns.add(new HmTableColumn(COLUMN_FORMER_CONTENT));
		columns.add(new HmTableColumn(COLUMN_POST_CONTENT));
		columns.add(new HmTableColumn(COLUMN_RECOMMEND_ACTION));
		columns.add(new HmTableColumn(COLUMN_ANNOTATION));
		columns.add(new HmTableColumn(COLUMN_UPGRADE_TIME));
		return columns;
	}

}
