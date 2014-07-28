package com.ah.ui.actions.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.ah.bo.admin.HmL3FirewallLog;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;

/**
 * 
 *@filename		L3firewallLogAction.java
 *@version		congo
 *@author		wpliang
 *@createtime	2011-6-13 10:01:52
 *Copyright (c) 2006-2011 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class L3firewallLogAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private static String startTime;

	private static int startHour;

	private static String endTime;

	private static int endHour;

	private List<CheckItem> lstHours;

	private boolean timeRange = false;

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}

		try {
			lstHours = initLstHours();

			if ("refresh".equals(operation)) {
				setFilterParam();

				return prepareL3FirewallLogList();
			} else {
				baseOperation();

				return prepareL3FirewallLogList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_L3FIREWALLLOG);
		setDataSource(HmL3FirewallLog.class);
		//the column of this keyColumnId on this page can not be hidden.
		keyColumnId = COLUMN_CLIENTIP; 
		tableId = HmTableColumn.TABLE_L3FIREWALLLOG;
	}

	public HmSystemLog getDataSource() {
		return (HmSystemLog) dataSource;
	}

	private void initValue() {
		if (filterParams == null) {
			TimeZone userTimeZone = TimeZone.getTimeZone(userContext
					.getTimeZone());
			Calendar calendar = Calendar.getInstance(userTimeZone);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(userTimeZone);
			startTime = formatter.format(calendar.getTime());
			startHour = 0;
			endHour = calendar.get(Calendar.HOUR_OF_DAY) + 1;
			if (endHour == 24) {
				endHour = 0;
				calendar.add(Calendar.HOUR_OF_DAY, 1);
			}
			endTime = startTime;
		}
	}

	public String prepareL3FirewallLogList() throws Exception {
		if (sortParams == null) {
			enableSorting();
			sortParams.setOrderBy("id");
			sortParams.setAscending(false);
		}

		initValue();
		setTableColumns();
		String result = prepareBoList();

		//show as user context timezone
		String userTimeZone = userContext.getTimeZone();
		for (Object obj : page) {
			HmL3FirewallLog l3firewallLog = (HmL3FirewallLog) obj;
			l3firewallLog.setOperationTimeZone(userTimeZone);
		}

		return result;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_CLIENTIP = 1;

	public static final int COLUMN_USERNAME = 2;

	public static final int COLUMN_DEVICE = 3;

	public static final int COLUMN_SOURCE = 4;

	public static final int COLUMN_DESTINATION = 5;

	public static final int COLUMN_SERVICE = 6;

	public static final int COLUMN_ACTION = 7;

	public static final int COLUMN_OPERATIONTIME = 8;

	/**
	 * get the description of column by id
	 * 
	 * @param id
	 *            -
	 * @return -
	 */
	public final String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_CLIENTIP:
			code = "admin.l3firewallLog.clientIp";
			break;
		case COLUMN_USERNAME:
			code = "admin.l3firewallLog.username";
			break;
		case COLUMN_DEVICE:
			code = "admin.l3firewallLog.device";
			break;
		case COLUMN_SOURCE:
			code = "admin.l3firewallLog.source";
			break;
		case COLUMN_DESTINATION:
			code = "admin.l3firewallLog.destination";
			break;
		case COLUMN_SERVICE:
			code = "admin.l3firewallLog.service";
			break;
		case COLUMN_ACTION:
			code = "admin.l3firewallLog.action";
			break;
		case COLUMN_OPERATIONTIME:
			code = "admin.l3firewallLog.operationTime";
			break;
		}

		

		return MgrUtil.getUserMessage(code);
	}

	/**
	 * set the description of columns
	 * 
	 * @param columns
	 *            -
	 */
	public final void setColumnDescription(List<HmTableColumn> columns) {
		for (HmTableColumn column : columns) {
			column.setColumnDescription(getColumnDescription(column
					.getColumnId()));
			column.setTableId(tableId);
		}
	}

	protected void setTableColumns() {

		selectedColumns = getUserContext().getTableViews().get(tableId);

		if (selectedColumns == null) {
			selectedColumns = getDefaultSelectedColums();
		}

		setColumnDescription(selectedColumns);
		availableColumns = getDefaultSelectedColums();
		setColumnDescription(availableColumns);
		availableColumns.removeAll(selectedColumns);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_CLIENTIP));
		columns.add(new HmTableColumn(COLUMN_USERNAME));
		columns.add(new HmTableColumn(COLUMN_DEVICE));
		columns.add(new HmTableColumn(COLUMN_SOURCE));
		columns.add(new HmTableColumn(COLUMN_DESTINATION));
		columns.add(new HmTableColumn(COLUMN_SERVICE));
		columns.add(new HmTableColumn(COLUMN_ACTION));
		columns.add(new HmTableColumn(COLUMN_OPERATIONTIME));

		return columns;
	}

	// format hours list
	private List<CheckItem> initLstHours() {
		List<CheckItem> lstHour = new ArrayList<CheckItem>();
		for (int i = 0; i < 24; i++) {
			lstHour.add(new CheckItem((long) i, String.valueOf(i + ":00")));
		}
		return lstHour;
	}

	private void setFilterParam() {
		List<Object> lstCondition = new ArrayList<Object>();

		String searchSQL = "";

		if (timeRange) {
			searchSQL = searchSQL + ((lstCondition.size() == 0) ? "" : " AND ");
			searchSQL = searchSQL + "operationTimeStamp >= :s"
					+ (lstCondition.size() + 1) + " AND operationTimeStamp <= :s"
					+ (lstCondition.size() + 2);

			Calendar cal = Calendar.getInstance(TimeZone
					.getTimeZone(userContext.getTimeZone()));
			String tmpStartDate[] = startTime.trim().split("-");
			cal.set(Integer.parseInt(tmpStartDate[0]),
					Integer.parseInt(tmpStartDate[1]) - 1,
					Integer.parseInt(tmpStartDate[2]), startHour, 0, 0);
			lstCondition.add(cal.getTime().getTime());

			String tmpEndDate[] = endTime.trim().split("-");
			cal.set(Integer.parseInt(tmpEndDate[0]),
					Integer.parseInt(tmpEndDate[1]) - 1,
					Integer.parseInt(tmpEndDate[2]), endHour, 0, 0);
			lstCondition.add(cal.getTime().getTime());
		}

		if (lstCondition.size() == 0) {
			filterParams = null;
			return;
		}

		Object values[] = new Object[lstCondition.size()];
		for (int i = 0; i < lstCondition.size(); i++) {
			values[i] = lstCondition.get(i);
		}

		filterParams = new FilterParams(searchSQL, values);
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		L3firewallLogAction.endHour = endHour;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		L3firewallLogAction.endTime = endTime;
	}

	public List<CheckItem> getLstHours() {
		return lstHours;
	}

	public void setLstHours(List<CheckItem> lstHours) {
		this.lstHours = lstHours;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		L3firewallLogAction.startHour = startHour;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		L3firewallLogAction.startTime = startTime;
	}

	public boolean isTimeRange() {
		return timeRange;
	}

	public void setTimeRange(boolean timeRange) {
		this.timeRange = timeRange;
	}

	public String getDisabledTimeRangeButton() {
		if (timeRange) {
			return "";
		}

		return "disabled";
	}

}