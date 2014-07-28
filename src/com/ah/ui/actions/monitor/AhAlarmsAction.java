package com.ah.ui.actions.monitor;

/*
 * @author Chris Scheers
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.be.fault.BeFaultConst;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.PagingImpl;
import com.ah.bo.mgmt.impl.TrapMgmtImpl;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.performance.AhAlarmsFilter;
import com.ah.events.BoEvent;
import com.ah.events.BoEvent.BoEventType;
import com.ah.events.impl.BoObserver;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.datetime.AhDateTimeUtil;

public class AhAlarmsAction extends AhTrapsAction {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(AhAlarmsAction.class
			.getSimpleName());

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_SEVERITY = 1;

	public static final int COLUMN_AP_NAME = 2;

	public static final int COLUMN_AP_ID = 3;

	public static final int COLUMN_TRAP_TIME = 4;

	public static final int COLUMN_CLEAR_TIME = 5;

	public static final int COLUMN_DESCRIPTION = 6;

	public static final int COLUMN_OBJECT_NAME = 7;
	
	public static final String MANAGED_ALARMS_CURRENT_FILTER = "managed_alarms_current_filter";

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		MgrUtil.removeSessionAttribute(SessionKeys.DOMAIN_SESSION_KEY);
		try {
			if ("clear".equals(operation)) {
				clearAlarms(true);
				return prepareFilteredAlarmsList(true);
			} else if ("remove".equals(operation)) {
				if (clearAlarms(false) != -1) {
					removeOperation();
				}
				return prepareFilteredAlarmsList(true);
			} else if ("systemStatus".equals(operation)) {
				jsonArray = new JSONArray(systemStatus());
				return "json";
			} else if ("search".equals(operation)) {
				saveFilter();
				saveToSessionFilterList();
				return prepareAlarmsList(true);
			} else if (baseOperation()) {
				return prepareFilteredAlarmsList(true);
			} else if ("updates".equals(operation)) {
				log.debug("execute", "Updates from cache: " + cacheId);
				AlarmsPagingCache alarmsPagingCache = getAlarmsListCache();
				jsonArray = new JSONArray(alarmsPagingCache.getUpdates(cacheId));
				return "json";
			} else if ("refreshFromCache".equals(operation)) {
				log.debug("execute", "Refresh from cache: " + cacheId);
				return prepareFilteredAlarmsList(false);
			} else if ("updateLogSetting".equals(operation)) {
				boolean isSucc = updateLogConfig();
				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				return "json";
			} else if ("view".equals(operation)) {
				resetFilterParams();
				return prepareAlarmsList(true);
			} else if ("requestFilterValues".equals(operation)) {
				log.info("execute", "requestFilterValues operation");
				prepareFilterValues();
				return "json";
			} else if ("removeFilter".equals(operation)) {
				resetFilterParams();
				removeAlarmsFilter();
				return prepareAlarmsList(true);
			}else if("editAlarmLogSettings".equals(operation)){
				jsonObject = new JSONObject();
				List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class, null, null);
				if (!list.isEmpty()) {
					logSettings =list.get(0);
				}else{
					logSettings=new LogSettings();
				}
				jsonObject.put("alarmInterval", logSettings.getAlarmInterval());
				jsonObject.put("retainUnclearDays", logSettings.getAlarmRetainUnclearDays());
				jsonObject.put("maxRecords", logSettings.getShowMaxRecordsValue());
				jsonObject.put("reminderDays", logSettings.getAlarmReminderDays());
				return "json";
			}else if("clearCriticalAlarm".equals(operation)){
				jsonObject = new JSONObject();
				try{
					List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class, null, null);
					if (list.isEmpty()) {
						log.error("clearCriticalAlarm", "Can't find log settings bo.");
					} else {
						int reminderDays=list.get(0).getAlarmReminderDays();
						String where = "trap_time < :s1 and severity = :s2";
						Object[] values = new Object[] { AhDateTimeUtil.getDateAfter2(-reminderDays),(short) BeFaultConst.ALERT_SERVERITY_CRITICAL};
						List<Long> alarmIds = QueryUtil.findBoIds(AhAlarm.class, null, new FilterParams(where, values), domainId);
						if(null!=alarmIds){
						   clearAlarms(alarmIds);
						}
						jsonObject.put("success", true);
					}
				}catch(Exception e){
					log.error("clearCriticalAlarm error:", e);
				}
				return "json";
			} else {
				// Clear filter
				resetFilterParams();
				setSessionFiltering();
				return prepareFilteredAlarmsList(true);
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_ALARMS);
		setDataSource(AhAlarm.class);
		enableSorting();
//		if ("id".equals(sortParams.getOrderBy())) {
//			sortParams.setOrderBy("trap_time");
//			sortParams.setAscending(false);
//		}
		sortParams.setOrderBy("id");
		sortParams.setAscending(false);

		keyColumnId = COLUMN_AP_ID;
		tableId = HmTableColumn.TABLE_ALARM;
	}
	
	/**
	 * update log config info
	 * 
	 * @return -
	 */
	private boolean updateLogConfig() {
		// 1. update db
		LogSettings bo;
		List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class, null, null);
		if (list.isEmpty()) {
			log.error("updateLogConfig", "Can't find log settings bo.");
			return false;
		} else {
			bo = list.get(0);
		}
		bo.setAlarmInterval(logSettings.getAlarmInterval());
		bo.setAlarmRetainUnclearDays(logSettings.getAlarmRetainUnclearDays());
		bo.setAlarmMaxRecords(logSettings.getAlarmMaxRecords());
		bo.setAlarmReminderDays(logSettings.getAlarmReminderDays());
		try {
			QueryUtil.updateBo(bo);
			return true;
		} catch (Exception e) {
			log.error("updateLogConfig", "Update log settings catch exception!", e);
			return false;
		}
	}

	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return -
	 * @author Joseph Chen
	 */
	@Override
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_SEVERITY:
			code = "monitor.alarms.severity";
			break;
		case COLUMN_AP_NAME:
			code = "monitor.alarms.apName";
			break;
		case COLUMN_AP_ID:
			code = "monitor.alarms.apId";
			break;
		case COLUMN_TRAP_TIME:
			code = "monitor.alarms.alarmTime";
			break;
		case COLUMN_CLEAR_TIME:
			code = "monitor.alarms.clearTime";
			break;
		case COLUMN_DESCRIPTION:
			code = "monitor.alarms.alarmDesc";
			break;
		case COLUMN_OBJECT_NAME:
			code = "monitor.alarms.objectName";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(7);

		columns.add(new HmTableColumn(COLUMN_SEVERITY));
		columns.add(new HmTableColumn(COLUMN_AP_NAME));
		columns.add(new HmTableColumn(COLUMN_AP_ID));
		columns.add(new HmTableColumn(COLUMN_TRAP_TIME));
		columns.add(new HmTableColumn(COLUMN_CLEAR_TIME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		columns.add(new HmTableColumn(COLUMN_OBJECT_NAME));

		return columns;
	}

	public String prepareFilteredAlarmsList(boolean initCache) {
		getSessionFiltering();
		restoreFilter();
		return prepareAlarmsList(initCache);
	}

	private static final String ALARMS_CURRENT_FILTER = "alarms_current_filter";

	protected void saveFilter() {
		String searchSQL = "";
		List<Object> lstCondition = new ArrayList<Object>();

		if (severity > 0) {
			searchSQL = "severity = :s1";
			lstCondition.add(severity);
		}

		if (apId != null && !apId.trim().isEmpty()) {
			searchSQL = searchSQL + ((lstCondition.isEmpty()) ? "" : " AND ")
					+ "lower(apId) like :s" + (lstCondition.size() + 1);
			lstCondition.add("%" + apId.toLowerCase() + "%");
		}

		if (filterVHM != null && !filterVHM.trim().isEmpty()) {
			searchSQL = searchSQL + ((lstCondition.isEmpty()) ? "" : " AND ")
					+ "lower(owner.domainName) like :s"
					+ (lstCondition.size() + 1);
			lstCondition.add("%" + filterVHM.toLowerCase() + "%");
		}
		
		if(component != null && !component.trim().isEmpty()){
			searchSQL = searchSQL + ((lstCondition.isEmpty()) ? "" : " AND ")
					+ "lower(objectName) like :s" + (lstCondition.size() + 1);
			lstCondition.add("%" + component.toLowerCase() + "%");
		}
		
		if(beginDate != null && !beginDate.trim().isEmpty()){
			searchSQL = searchSQL + ((lstCondition.isEmpty()) ? "" : " AND ")
					+ "trapTimeStamp.time > :s" + (lstCondition.size() + 1);
			lstCondition.add(this.getStartTime());
		}
		
		if(endDate != null && !endDate.trim().isEmpty()){
			searchSQL = searchSQL + ((lstCondition.isEmpty()) ? "" : " AND ")
					+ "trapTimeStamp.time < :s" + (lstCondition.size() + 1);
			lstCondition.add(this.getEndTime());
		}

		if (lstCondition.isEmpty()) {
			filterParams = null;
		} else {
			filterParams = new FilterParams(searchSQL, lstCondition.toArray());
		}

		setSessionFiltering();

		List<Object> filterParameters = new ArrayList<Object>();
		filterParameters.add(severity);
		filterParameters.add(apId);
		filterParameters.add(filterVHM);
		filterParameters.add(component);
		filterParameters.add(this.getStartTimeS());
		filterParameters.add(this.getEndTimeS());
		MgrUtil.setSessionAttribute(ALARMS_CURRENT_FILTER, filterParameters);
	}
	
	private void resetFilterParams() throws Exception {
		// get active clients
		filterParams = null;
		setSessionFiltering();
		MgrUtil.removeSessionAttribute(MANAGED_ALARMS_CURRENT_FILTER);
	}
	
	protected void restoreFilter() {
		List<Object> filterParameters = (List<Object>) MgrUtil
				.getSessionAttribute(ALARMS_CURRENT_FILTER);

		if (null == filterParameters) {
			return;
		}

		severity = (Short) filterParameters.get(0);
		apId = (String) filterParameters.get(1);
		filterVHM = (String) filterParameters.get(2);
	}

	public String prepareAlarmsList(boolean initCache) {
		clearDataSource();
		AlarmsPagingCache alarmsPagingCache = getAlarmsListCache();
		if (initCache) {
			enablePaging();
			paging.setLazyRowCount(true);
			cacheId = alarmsPagingCache.init();
		} else {
			paging = (Paging<? extends HmBo>) MgrUtil.getSessionAttribute(boClass
					.getSimpleName()
					+ "Paging");
		}
		page = alarmsPagingCache.getBos(cacheId);
		
		// show as user context timezone
		String userTimeZone = userContext.getTimeZone();
		for (Object obj : page) {
			AhAlarm alarm = (AhAlarm) obj;
			alarm.getTrapTimeStamp().setTimeZone(userTimeZone);
			alarm.getModifyTimeStamp().setTimeZone(userTimeZone);
			alarm.getClearTimeStamp().setTimeZone(userTimeZone);
		}
		setTableColumns();
		return SUCCESS;
	}

	/*
	 * Only create 1 map alarms cache per session.
	 */
	protected AlarmsPagingCache getAlarmsListCache() {
		AlarmsPagingCache alarmsPagingCache = (AlarmsPagingCache) MgrUtil
				.getSessionAttribute(SessionKeys.ALARMS_PAGING_CACHE);
		if (alarmsPagingCache == null) {
			alarmsPagingCache = new AlarmsPagingCache(userContext);
			MgrUtil.setSessionAttribute(SessionKeys.ALARMS_PAGING_CACHE,
					alarmsPagingCache);
		}
		return alarmsPagingCache;
	}

	public EnumItem[] getEnumSeverity() {
		return AhAlarm.ENUM_AH_SEVERITY;
	}

	public int clearAlarms(boolean clearSelections) throws Exception {
		int count = -1;
		if (isAllItemsSelected()) {
			count = clearAllAlarms();
		} else if (getAllSelectedIds() != null
				&& !getAllSelectedIds().isEmpty()) {
			count = clearAlarms(getAllSelectedIds());
		}
		log.info("removeOperation", "Count: " + count);
		if (count < 0) {
			addActionMessage(MgrUtil.getUserMessage(SELECT_OBJECT));
		} else if (count == 0) {
			addActionMessage(MgrUtil.getUserMessage(NO_ALARMS_CLEARED));
		} else {
			addActionMessage(MgrUtil.getUserMessage(ALARMS_CLEARED, NmsUtil.convertNumToEnglish(count, true) + ""));
		}
		if (clearSelections) {
			setAllSelectedIds(null);
		}
		return count;
	}

	public int clearAllAlarms() throws Exception {
		int count = 0;
		Paging<AhAlarm> paging = new PagingImpl<>(AhAlarm.class);
		SortParams sortParams = new SortParams("id");
		getSessionFiltering();
		for (paging.setPageSize(100); paging.hasNext();) {
			List<AhAlarm> alarms = paging.next().executeQuery(sortParams,
					filterParams);
			log.info("clearAllAlarms", "Clearing: " + alarms.size()
					+ " alarms.");
			for (AhAlarm alarm : alarms) {
				if (BoMgmt.getTrapMgmt().clearAlarm(alarm)) {
					count++;
				}
			}
		}
		return count;
	}

	public int clearAlarms(Collection<Long> alarmIds) throws Exception {
		int count = 0;
		for (Long alarmId : alarmIds) {
			log.debug("clearAlarms", "Clearing alarm: " + alarmId);
			if (BoMgmt.getTrapMgmt().clearAlarmId(alarmId)) {
				count++;
			}
		}
		return count;
	}

	/*
	 * Remove Hive Manager Business Objects by class
	 */
	/*- the function is not used already.
	 @Override
	 protected int removeAllBos(Class<? extends HmBo> boClass,
	 Collection<Long> defaultIds) throws Exception {
	 int count = BoMgmt.removeAllBos(boClass, filterParams,
	 getUserContext(), defaultIds, getSelectedL2FeatureKey());
	 if (count > 0) {
	 generateAuditLog(HmAuditLog.STATUS_SUCCESS, "Remove " + count + " "
	 + getSelectedL2Feature().getDescription());
	 }
	 return count;
	 }
	 */

	protected Collection<JSONObject> systemStatus() throws Exception {
		Collection<JSONObject> systemStatus = new Vector<JSONObject>();
		
		try {
			JSONObject alarmCount = new JSONObject();
			alarmCount.put("cr", getCriticalAlarmCount());
			alarmCount.put("ma", getMajorAlarmCount());
			alarmCount.put("mi", getMinorAlarmCount());
			alarmCount.put("cl", getClearedAlarmCount());
			alarmCount.put("nh", getNewHiveAPCount());
			alarmCount.put("rc", getRogueClientCount());
			alarmCount.put("innet", getInnetRogueCount());
			alarmCount.put("onmap", getOnmapRogueCount());
			alarmCount.put("strong", getStrongRogueCount());
			alarmCount.put("weak", getWeakRogueCount());
			systemStatus.add(alarmCount);
		} catch (OutOfMemoryError oome) {
			log.error("systemStatus", "Out of memory occurred.", oome);
		}

		return systemStatus;
	}
	
	@Override
	protected int removeAllBos(Class<? extends HmBo> boClass, FilterParams filterParams,
			Collection<Long> defaultIds) throws Exception {
		AccessControl.checkUserAccess(getUserContext(), getSelectedL2FeatureKey(),
				CrudOperation.DELETE);
		int count = QueryUtil.bulkRemoveBos(boClass, filterParams, getUserContext(), defaultIds);

		if (count > 0) {
			// clear alarm at first, now let's decrement clear alarms.
			SystemStatusCache.getInstance().decrementAlarmCount(count,
					AhAlarm.AH_SEVERITY_UNDETERMINED, getDomainId());

			// notify alarm paging cache
			BoObserver.notifyListeners(new BoEvent<AhAlarm>(new AhAlarm(), BoEventType.REMOVED));

			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.base.operation.remove") + count + " "
					+ getSelectedL2Feature().getDescription());
		}
		return count;
	}

	
	public List<String> getFilterList() {
		List<String> filterMap = (List<String>) QueryUtil.executeQuery(
				"select filterName from "
						+ AhAlarmsFilter.class.getSimpleName(), null,
				new FilterParams("userName", getUserContext().getUserName()),
				domainId);
		// order by name
		Collections.sort(filterMap, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
		});
		return filterMap;
	}
	
	private void saveToSessionFilterList(){
		if (null == filterName || "".equals(filterName.trim())) {
			return;
		}
		
		try{
			List<AhAlarmsFilter> filterList = QueryUtil
			.executeQuery(AhAlarmsFilter.class, null,
					new FilterParams("filterName=:s1 AND userName=:s2",
							new Object[] { filterName,
									getUserContext().getUserName() }),
					domainId);
			
			if (!filterList.isEmpty()) {
				AhAlarmsFilter filter = filterList
						.get(0);
				filter.setApId(this.apId);
				filter.setSeverity(this.getSeverity());
				filter.setComponent(this.component);
				filter.setStartTime(this.getStartTime());
				filter.setEndTime(this.getEndTime());
				filter.setFilterName(this.filterName);
				setId(filter.getId());
				QueryUtil.updateBo(filter);
			} else {
				AhAlarmsFilter filter = new AhAlarmsFilter();
				filter.setUserName(getUserContext().getUserName());
				filter.setFilterName(filterName);
				filter.setApId(this.apId);
				filter.setSeverity(this.getSeverity());
				filter.setComponent(this.component);
				filter.setStartTime(this.getStartTime());
				filter.setEndTime(this.getEndTime());
				filter.setOwner(getDomain());
				QueryUtil.createBo(filter);
			}

			MgrUtil.setSessionAttribute(MANAGED_ALARMS_CURRENT_FILTER,
					filterName);
			filter = filterName;
		}catch (Exception e) {
			addActionError(MgrUtil.getUserMessage("action.error.event.filter.fail"));
		}
	}
	
	private String filter;
	
	public String getFilter() {
		if (null == filter) {
			filter = (String) MgrUtil
					.getSessionAttribute(MANAGED_ALARMS_CURRENT_FILTER);
		}
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	private void prepareFilterValues() throws JSONException {
		jsonObject = new JSONObject();
		if (null == filter) {
			return;
		}
		jsonObject.put("fName", filter);
		// Map<String, List<Object>> filterCache = (Map<String, List<Object>>) MgrUtil
		// .getSessionAttribute(MANAGED_ACTIVECLIENT_FILTERS);
		List<?> filterMap = QueryUtil
				.executeQuery(
						"select apId, severity, component,startTime,endTime from "
								+ AhAlarmsFilter.class.getSimpleName(),
						null, new FilterParams(
								"filterName=:s1 and userName=:s2",
								new Object[] { filter,
										getUserContext().getUserName() }),
						domainId);

		if (filterMap.isEmpty()) {
			return;
		}
		Object[] objArray = (Object[]) filterMap.get(0);
		if (null == objArray) {
			return;
		}

		if (objArray.length > 0) {
			jsonObject.put("nodeId", objArray[0]);
		}
		if (objArray.length > 1) {
			jsonObject.put("aSeverity", objArray[1]);
		}
		if (objArray.length > 2) {
			jsonObject.put("aComponent", objArray[2]);
		}
		if (objArray.length > 3) {
			jsonObject.put("aBeginDate", this.getDateStr((Long)objArray[3]));
			jsonObject.put("aBeginTime", this.getHourStr((Long)objArray[3]));
		}
		if (objArray.length > 4) {
			jsonObject.put("aEndDate", this.getDateStr((Long)objArray[4]));
			jsonObject.put("aEndTime", this.getHourStr((Long)objArray[4]));
		}
	}
	
	private void removeAlarmsFilter() {
		if (null == filterName || "".equals(filterName.trim())) {
			return;
		}

		try {
			List<AhAlarmsFilter> list = QueryUtil
					.executeQuery(AhAlarmsFilter.class, null,
							new FilterParams("filterName=:s1 AND userName=:s2",
									new Object[] { filterName,
											getUserContext().getUserName() }),
							domainId);
			if (!list.isEmpty()) {
				AhAlarmsFilter filter = list.get(0);
				AhAlarmsFilter rmbos = findBoById(AhAlarmsFilter.class, filter
						.getId());
				QueryUtil.removeBoBase(rmbos);
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage("action.error.remove.alarm.filter.fail"));
		}
	}
	
	public LogSettings logSettings;

	public LogSettings getLogSettings() {
		return logSettings;
	}

	public void setLogSettings(LogSettings logSettings) {
		this.logSettings = logSettings;
	}
	
}