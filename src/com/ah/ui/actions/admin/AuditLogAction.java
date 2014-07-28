package com.ah.ui.actions.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * 
 *@filename		AuditLogAction.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-1-15 10:00:51
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class AuditLogAction extends BaseAction {

	private static final long	serialVersionUID	= 1L;

	private static final Tracer	log					= new Tracer(AuditLogAction.class
															.getSimpleName());

	protected List<HmUser>		users = new ArrayList<HmUser>();

	private String				startTime;

	private int					startHour;

	private String				endTime;

	private int					endHour;

	private List<CheckItem>		lstHours;

	private boolean				timeRange			= false;

	private long				userId;

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}

		try {
			lstHours = initLstHours();

			if ("refresh".equals(operation)) {
				setFilterParam();
				setSessionFiltering();

				return prepareAuditLogList();
			} else {
				baseOperation();

				// restore filter field value
				restoreFilter();

				return prepareAuditLogList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_AUDITLOG);
		setDataSource(HmAuditLog.class);
		keyColumnId = COLUMN_OPERATION;
		tableId = HmTableColumn.TABLE_AUDITLOG;
	}

	public HmAuditLog getDataSource() {
		return (HmAuditLog) dataSource;
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

	public String prepareAuditLogList() throws Exception {
		initValue();
		prepareUsers();
		String result = prepareBoList();

		// show as user context timezone
		String userTimeZone = userContext.getTimeZone();
		for (Object obj : page) {
			HmAuditLog log = (HmAuditLog) obj;
			log.setLogTimeZone(userTimeZone);
		}
		
		return result;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int	COLUMN_USERNAME			= 1;

	public static final int	COLUMN_HOSTIP			= 2;

	public static final int	COLUMN_OPERATION		= 3;

	public static final int	COLUMN_RESULT			= 4;

	public static final int	COLUMN_OPERATIONTIME	= 5;

	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 */
	public final String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_USERNAME:
			code = "admin.auditLog.userName";
			break;
		case COLUMN_HOSTIP:
			code = "admin.auditLog.hostIP";
			break;
		case COLUMN_OPERATION:
			code = "admin.auditLog.operation";
			break;
		case COLUMN_RESULT:
			code = "admin.auditLog.status";
			break;
		case COLUMN_OPERATIONTIME:
			code = "admin.auditLog.time";
			break;
		}

		return MgrUtil.getUserMessage(code);
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

		columns.add(new HmTableColumn(COLUMN_USERNAME));
		columns.add(new HmTableColumn(COLUMN_HOSTIP));
		columns.add(new HmTableColumn(COLUMN_OPERATION));
		columns.add(new HmTableColumn(COLUMN_RESULT));
		columns.add(new HmTableColumn(COLUMN_OPERATIONTIME));

		return columns;
	}

	/**
	 * restore filter field value
	 */
	private void restoreFilter() {
		getSessionFiltering();

		if (filterParams == null) {
			return;
		}

		if (filterParams.getBindings().length == 1) {
			String userName = (String) filterParams.getBindings()[0];
			try {
				List<HmUser> list = QueryUtil.executeQuery(HmUser.class, null, new FilterParams("username",
						userName));

				if (list.size() > 0) {
					userId = list.get(0).getId();
				}
			} catch (Exception e) {
				log.warning("setFilterParam", "catch exception", e);
			}
		} else if (filterParams.getBindings().length == 2) {
			timeRange = true;

			Date startDate = new Date((Long) filterParams.getBindings()[0]);
			Date endDate = new Date((Long) filterParams.getBindings()[1]);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(TimeZone.getTimeZone(userContext.getTimeZone()));
			startTime = formatter.format(startDate);
			endTime = formatter.format(endDate);

			formatter = new SimpleDateFormat("H");
			formatter.setTimeZone(TimeZone.getTimeZone(userContext.getTimeZone()));
			startHour = Integer.valueOf(formatter.format(startDate));
			endHour = Integer.valueOf(formatter.format(endDate));
		} else if (filterParams.getBindings().length == 3) {
			timeRange = true;

			String userName = (String) filterParams.getBindings()[0];
			try {
				List<HmUser> list = QueryUtil.executeQuery(HmUser.class, null, new FilterParams("username",
						userName));

				if (list.size() > 0) {
					userId = list.get(0).getId();
				}
			} catch (Exception e) {
				log.warning("setFilterParam", "catch exception", e);
			}

			Date startDate = new Date((Long) filterParams.getBindings()[1]);
			Date endDate = new Date((Long) filterParams.getBindings()[2]);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(TimeZone.getTimeZone(userContext.getTimeZone()));
			startTime = formatter.format(startDate);
			endTime = formatter.format(endDate);

			formatter = new SimpleDateFormat("H");
			formatter.setTimeZone(TimeZone.getTimeZone(userContext.getTimeZone()));
			startHour = Integer.valueOf(formatter.format(startDate));
			endHour = Integer.valueOf(formatter.format(endDate));
		}
	}

	public void prepareUsers() throws Exception {
		users = BoMgmt.findBos(HmUser.class, null, getUserContext(), getSelectedL2FeatureKey());
		HmUser user_all = new HmUser();
		user_all.setId((long) 0);
		user_all.setUserName("All");
		users.add(0, user_all);
	}

	private List<CheckItem> initLstHours() {
		List<CheckItem> lstHour = new ArrayList<CheckItem>();
		for (int i = 0; i < 24; i++) {
			lstHour.add(new CheckItem((long) i, String.valueOf(i + ":00")));
		}
		return lstHour;
	}

	/**
	 * set filterParam
	 */
	private void setFilterParam() {
		List<Object> lstCondition = new ArrayList<Object>();

		String searchSQL;
		if (userId > 0) {
			String userName = "";
			try {
				userName = (findBoById(HmUser.class, userId)).getUserName();
			} catch (Exception e) {
				log.warning("setFilterParam", "catch exception", e);
			}

			if (timeRange) {
				searchSQL = "userOwner = :s1 AND logTimeStamp >= :s2 AND logTimeStamp <= :s3";
			} else {
				searchSQL = "userOwner = :s1";
			}

			lstCondition.add(userName);
		} else {
			if (!timeRange) {
				filterParams = null;
				return;
			}

			searchSQL = "logTimeStamp >= :s1 AND logTimeStamp <= :s2";
		}

		if (timeRange) {
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

		Object values[] = new Object[lstCondition.size()];
		for (int i = 0; i < lstCondition.size(); i++) {
			values[i] = lstCondition.get(i);
		}

		filterParams = new FilterParams(searchSQL, values);
	}

	public List<HmUser> getUsers() {
		return users;
	}

	public void setUsers(List<HmUser> users) {
		this.users = users;
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
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
		this.startHour = startHour;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
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

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}