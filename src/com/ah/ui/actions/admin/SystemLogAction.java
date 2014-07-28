package com.ah.ui.actions.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * 
 *@filename		SystemLogAction.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-6 10:01:52
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class SystemLogAction extends BaseAction {

	private static final long	serialVersionUID	= 1L;

	private static String		startTime;

	private static int			startHour;

	private static String		endTime;

	private static int			endHour;

	private List<CheckItem>		lstHours;

	private boolean				timeRange			= false;

	private static short		selectLevel;

	private static EnumItem[]	levels				= getEnumItems_Levels();

	private static EnumItem[] getEnumItems_Levels() {
		// call be function
		EnumItem[] enumItems = new EnumItem[4];
		enumItems[0] = new EnumItem(0, "All");
		enumItems[1] = new EnumItem(HmSystemLog.LEVEL_CRITICAL, HmSystemLog.LEVEL_CRITICAL_SHOW);
		enumItems[2] = new EnumItem(HmSystemLog.LEVEL_MAJOR, HmSystemLog.LEVEL_MAJOR_SHOW);
		enumItems[3] = new EnumItem(HmSystemLog.LEVEL_MINOR, HmSystemLog.LEVEL_MINOR_SHOW);

		return enumItems;
	}

	private static short		selectSource;

	private static EnumItem[]	features	= getEnumItems_Features();

	private static EnumItem[] getEnumItems_Features() {
		// call be function
		EnumItem[] enumItems = new EnumItem[6];
		enumItems[0] = new EnumItem(0, "All");
		enumItems[1] = new EnumItem(1, HmSystemLog.FEATURE_TOPOLOGY);
		enumItems[2] = new EnumItem(2, HmSystemLog.FEATURE_MONITORING);
		enumItems[3] = new EnumItem(3, HmSystemLog.FEATURE_HIVEAPS);
		enumItems[4] = new EnumItem(4, HmSystemLog.FEATURE_CONFIGURATION);
		enumItems[5] = new EnumItem(5, HmSystemLog.FEATURE_ADMINISTRATION);

		return enumItems;
	}

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}

		try {
			lstHours = initLstHours();

			if ("refresh".equals(operation)) {
				setFilterParam();
				
				return prepareSystemLogList();
			} else {
				baseOperation();

				return prepareSystemLogList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_SYSTEMLOG);
		setDataSource(HmSystemLog.class);
		keyColumnId = COLUMN_LOGINFO;
		tableId = HmTableColumn.TABLE_SYSTEMLOG;
	}

	public HmSystemLog getDataSource() {
		return (HmSystemLog) dataSource;
	}
	
	private void initValue() {
		if (filterParams == null) {
			TimeZone userTimeZone = TimeZone.getTimeZone(userContext.getTimeZone());
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
	
	public String prepareSystemLogList() throws Exception
	{
		if (sortParams == null) {
			enableSorting();
			sortParams.setOrderBy("id");
			sortParams.setAscending(false);
		}
		
		initValue();
		setTableColumns();
		String result =  prepareBoList();
		
		//show as user context timezone
		String userTimeZone = userContext.getTimeZone();
		for (Object obj : page) {
			HmSystemLog systemLog = (HmSystemLog) obj;
			systemLog.setLogTimeZone(userTimeZone);
		}
		
		return result;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int	COLUMN_LEVEL	= 1;

	public static final int	COLUMN_SOURCE	= 2;

	public static final int	COLUMN_LOGINFO	= 3;

	public static final int	COLUMN_LOGTIME	= 4;
	
	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 */
	public final String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_LEVEL:
			code = "admin.systemLog.level";
			break;
		case COLUMN_SOURCE:
			code = "admin.systemLog.source";
			break;
		case COLUMN_LOGINFO:
			code = "admin.systemLog.info";
			break;
		case COLUMN_LOGTIME:
			code = "admin.systemLog.time";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	/**
	 * set the description of columns
	 * 
	 * @param columns -
	 */
	public final void setColumnDescription(List<HmTableColumn> columns) {
		for (HmTableColumn column : columns) {
			column.setColumnDescription(getColumnDescription(column.getColumnId()));
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

		columns.add(new HmTableColumn(COLUMN_LEVEL));
		columns.add(new HmTableColumn(COLUMN_SOURCE));
		columns.add(new HmTableColumn(COLUMN_LOGINFO));
		columns.add(new HmTableColumn(COLUMN_LOGTIME));

		return columns;
	}

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

		if (selectLevel > 0) {
			searchSQL = "level = :s1";
			lstCondition.add(selectLevel);
		}

		if (selectSource > 0) {
			searchSQL = searchSQL + ((lstCondition.size() == 0) ? "" : " AND ");
			searchSQL = searchSQL + "source like :s" + (lstCondition.size() + 1);
			lstCondition.add("%" + features[selectSource].getValue() + "%");
		}

		if (timeRange) {
			searchSQL = searchSQL + ((lstCondition.size() == 0) ? "" : " AND ");
			searchSQL = searchSQL + "logTimeStamp >= :s" + (lstCondition.size() + 1)
					+ " AND logTimeStamp <= :s" + (lstCondition.size() + 2);

			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(userContext.getTimeZone()));
			String tmpStartDate[] = startTime.trim().split("-");
			cal.set(Integer.parseInt(tmpStartDate[0]), Integer.parseInt(tmpStartDate[1]) - 1,
					Integer.parseInt(tmpStartDate[2]), startHour, 0, 0);
			lstCondition.add(cal.getTime().getTime());

			String tmpEndDate[] = endTime.trim().split("-");
			cal.set(Integer.parseInt(tmpEndDate[0]), Integer.parseInt(tmpEndDate[1]) - 1, Integer
					.parseInt(tmpEndDate[2]), endHour, 0, 0);
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
		SystemLogAction.endHour = endHour;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		SystemLogAction.endTime = endTime;
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
		SystemLogAction.startHour = startHour;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		SystemLogAction.startTime = startTime;
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

	public short getSelectLevel() {
		return selectLevel;
	}

	public void setSelectLevel(short selectLevel) {
		SystemLogAction.selectLevel = selectLevel;
	}

	public short getSelectSource() {
		return selectSource;
	}

	public void setSelectSource(short selectSource) {
		SystemLogAction.selectSource = selectSource;
	}

	public static void setFeatures(EnumItem[] features) {
		SystemLogAction.features = features;
	}

	public static void setLevels(EnumItem[] levels) {
		SystemLogAction.levels = levels;
	}

	public static EnumItem[] getFeatures() {
		return features;
	}

	public static EnumItem[] getLevels() {
		return levels;
	}

}