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

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.bo.monitor.AhEvent;
import com.ah.bo.performance.AhEventsFilter;
import com.ah.events.BoEvent;
import com.ah.events.BoEvent.BoEventType;
import com.ah.events.impl.BoObserver;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class AhEventsAction extends AhTrapsAction {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(AhEventsAction.class
			.getSimpleName());

	/*
	 * ID of table columns in list view
	 */

	public static final int COLUMN_AP_NAME = 1;

	public static final int COLUMN_AP_ID = 2;

	public static final int COLUMN_TRAP_TIME = 3;

	public static final int COLUMN_DESCRIPTION = 4;

	public static final int COLUMN_OBJECT_NAME = 5;
	
	private int	eventInterval;
	
	public static final String MANAGED_EVENTS_CURRENT_FILTER = "managed_events_current_filter";

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}

		try {
			if ("view".equals(operation)) {
				jsonArray = new JSONArray(viewEvent());
				return "json";
			} else if ("resetFilter".equals(operation)) {
				resetFilterParams();
				return prepareFilteredEventsList(true);
			} else if ("search".equals(operation)) {
				saveFilter();
				saveToSessionFilterList();
				return prepareFilteredEventsList(true);
			} else if ("export".equals(operation)) {
				getSessionFiltering();
				prepareFilteredEventsList(true);
				return "export";
			} else if (baseOperation()) {
				return prepareFilteredEventsList(true);
			} else if ("updates".equals(operation)) {
				log.debug("execute", "Updates from cache: " + cacheId);
				EventsPagingCache eventsPagingCache = getEventsListCache();
				jsonArray = new JSONArray(eventsPagingCache.getUpdates(cacheId));
				return "json";
			} else if ("refreshFromCache".equals(operation)) {
				log.debug("execute", "Refresh from cache: " + cacheId);
				return prepareFilteredEventsList(false);
			} else if ("updateLogSetting".equals(operation)) {
				boolean isSucc = updateLogConfig();
				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				return "json";
			} else if ("requestFilterValues".equals(operation)) {
				log.info("execute", "requestFilterValues operation");
				prepareFilterValues();
				return "json";
			} else if ("removeFilter".equals(operation)) {
				resetFilterParams();
				removeEventsFilter();
				return prepareFilteredEventsList(true);
			}else if("editEventLogSettings".equals(operation)){
				jsonObject = new JSONObject();
				List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class, null, null);
				if (list.isEmpty()) {
					// set default value
					eventInterval = 7;
				} else {
					eventInterval = list.get(0).getEventInterval();
				}
				jsonObject.put("eventInterval", eventInterval);
				return "json";
			} else {
				// Clear filter
				resetFilterParams();
				setSessionFiltering();
				return prepareFilteredEventsList(true);
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_EVENTS);
		setDataSource(AhEvent.class);

		keyColumnId = COLUMN_AP_ID;
		tableId = HmTableColumn.TABLE_EVENT;
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

		bo.setEventInterval(eventInterval);

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
		case COLUMN_AP_NAME:
			code = "monitor.alarms.apName";
			break;
		case COLUMN_AP_ID:
			code = "monitor.alarms.apId";
			break;
		case COLUMN_TRAP_TIME:
			code = "monitor.alarms.alarmTime";
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
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(5);

		columns.add(new HmTableColumn(COLUMN_AP_NAME));
		columns.add(new HmTableColumn(COLUMN_AP_ID));
		columns.add(new HmTableColumn(COLUMN_TRAP_TIME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		columns.add(new HmTableColumn(COLUMN_OBJECT_NAME));

		return columns;
	}

	protected String prepareFilteredEventsList(boolean initCache)
			throws Exception {
		getSessionFiltering();
		restoreFilter();
		enableSorting();
		if (null==operation) {
			sortParams.setOrderBy("id");
			sortParams.setAscending(false);
		}

		return prepareEventsList(initCache);
	}

	private static final String EVENTS_CURRENT_FILTER = "events_current_filter";

	protected void saveFilter() {
		String searchSQL = "";
		List<Object> lstCondition = new ArrayList<Object>();

		if (apId != null && !apId.trim().isEmpty()) {
			searchSQL = "lower(apId) like :s" + (lstCondition.size() + 1);
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
					+ "time >= :s" + (lstCondition.size() + 1);
			lstCondition.add(this.getStartTime());
		}
		
		if(endDate != null && !endDate.trim().isEmpty()){
			searchSQL = searchSQL + ((lstCondition.isEmpty()) ? "" : " AND ")
					+ "time < :s" + (lstCondition.size() + 1);
			lstCondition.add(this.getEndTime());
		}

		if (lstCondition.isEmpty()) {
			filterParams = null;
		} else {
			filterParams = new FilterParams(searchSQL, lstCondition.toArray());
		}

		setSessionFiltering();

		List<Object> filterParameters = new ArrayList<Object>();
		filterParameters.add(apId);
		filterParameters.add(filterVHM);
		filterParameters.add(component);
		filterParameters.add(this.getStartTimeS());
		filterParameters.add(this.getEndTimeS());
		MgrUtil.setSessionAttribute(EVENTS_CURRENT_FILTER, filterParameters);
	}
	
	private void resetFilterParams() throws Exception {
		// get active clients
		filterParams = null;
		setSessionFiltering();
		MgrUtil.removeSessionAttribute(MANAGED_EVENTS_CURRENT_FILTER);
	}

	protected void restoreFilter() {
		List<Object> filterParameters = (List<Object>) MgrUtil
				.getSessionAttribute(EVENTS_CURRENT_FILTER);

		if (null == filterParameters) {
			return;
		}

		apId = (String) filterParameters.get(0);
		filterVHM = (String) filterParameters.get(1);
	}

	public String prepareEventsList(boolean initCache) {
		clearDataSource();
		EventsPagingCache eventsPagingCache = getEventsListCache();
		if (initCache) {
			enablePaging();
			paging.setLazyRowCount(true);
			cacheId = eventsPagingCache.init();
		} else {
			paging = (Paging<? extends HmBo>) MgrUtil.getSessionAttribute(boClass
					.getSimpleName()
					+ "Paging");
		}
		page = eventsPagingCache.getBos(cacheId);
		
		// show as user context timezone
		String userTimeZone = userContext.getTimeZone();
//		for (Iterator iter = page.iterator(); iter.hasNext();) {
//			AhEvent event = (AhEvent) iter.next();
//			event.getTrapTimeStamp().setTimeZone(userTimeZone);
//		}
		setUserTimeZone(page,userTimeZone);
		setTableColumns();
		return SUCCESS;
	}
	
	public static void setUserTimeZone(List<AhEvent> page, String userTimeZone) {
		if (null == page) {
			return;
		}
		for (AhEvent event : page) {
			event.getTrapTimeStamp().setTimeZone(userTimeZone);
		}
	}

	/*
	 * Only create 1 map events cache per session.
	 */
	protected EventsPagingCache getEventsListCache() {
		EventsPagingCache eventsPagingCache = (EventsPagingCache) MgrUtil
				.getSessionAttribute(SessionKeys.EVENTS_PAGING_CACHE);
		if (eventsPagingCache == null) {
			eventsPagingCache = new EventsPagingCache(userContext);
			MgrUtil.setSessionAttribute(SessionKeys.EVENTS_PAGING_CACHE,
					eventsPagingCache);
		}
		return eventsPagingCache;
	}

	protected Collection<JSONObject> viewEvent() throws Exception {
		Collection<JSONObject> eventDetails = new Vector<JSONObject>();
		AhEvent event = (AhEvent) findBoById(boClass, id);
		log.info("viewEvent", "view: " + id);
		if (id == null) {
			return eventDetails;
		}
		
		// show as user context timezone
		event.getTrapTimeStamp().setTimeZone(userContext.getTimeZone());
		
		JSONObject eventAttribute = new JSONObject();
		eventAttribute.put("id", "apName");
		eventAttribute.put("v", event.getApName());
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "apMac");
		eventAttribute.put("v", event.getApId());
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "code");
		eventAttribute.put("v", event.getCode());
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "trapTime");
		eventAttribute.put("v", event.getTrapTimeString());
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "trapDesc");
		eventAttribute.put("v", event.getTrapDesc());
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "objectName");
		eventAttribute.put("v", event.getObjectName());
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "eventType");
		eventAttribute.put("v", event.getEventType());
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "eventTypeString");
		eventAttribute.put("v", AhEvent
				.getEventTypeString(event.getEventType()));
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "curValue");
		eventAttribute.put("v", event.getCurValue());
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "thresholdHigh");
		eventAttribute.put("v", event.getThresholdHigh());
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "thresholdLow");
		eventAttribute.put("v", event.getThresholdLow());
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "currentState");
		eventAttribute
				.put("v", AhEvent.getStateString(event.getCurrentState()));
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "previousState");
		eventAttribute.put("v", AhEvent
				.getStateString(event.getPreviousState()));
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "ifIndex");
		eventAttribute.put("v", event.getIfIndex());
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "objectType");
		eventAttribute.put("v", AhEvent.getObjectTypeString(event
				.getObjectType()));
		eventDetails.add(eventAttribute);
		// client info, added by joseph chen, 07/11/2008
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "ssid");
		eventAttribute.put("v", event.getSsid());
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "clientIp");
		eventAttribute.put("v", event.getClientIp());
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "clientHostName");
		eventAttribute.put("v", event.getClientHostName());
		eventDetails.add(eventAttribute);
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "clientUserName");
		eventAttribute.put("v", event.getClientUserName());
		eventDetails.add(eventAttribute);
		/*
		 * connection change event
		 * joseph chen
		 * 08/20/2008
		 */
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "clientCWPUsed");
		eventAttribute.put("v", event.getClientCWPUsed() == 1 ? "Yes" : "No");
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "clientAuthMethod");
		eventAttribute.put("v", AhEvent.getClientAuthMethodString(event.getClientAuthMethod()));
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "clientEncryptionMethod");
		eventAttribute.put("v", AhEvent.getClientEncryptMethodString(event.getClientEncryptionMethod()));
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "clientMacProtocol");
		eventAttribute.put("v", AhEvent.getClientMacProtocolString(event.getClientMacProtocol()));
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "clientVLAN");
		eventAttribute.put("v", event.getClientVLAN());
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "clientUserProfId");
		eventAttribute.put("v", event.getClientUserProfId());
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "clientChannel");
		eventAttribute.put("v", event.getClientChannel());
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "clientBSSID");
		eventAttribute.put("v", event.getClientBSSID());
		eventDetails.add(eventAttribute);

		/*
		 * power information event
		 * joseph chen
		 * 08/21/2008
		 */
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "powerSource");
		eventAttribute.put("v", AhEvent.getPowerSourceString(event.getPowerSource()));
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "poEEth0On");
		eventAttribute.put("v", event.getPoEEth0On() == AhEvent.POE_ON ? "ON" : "OFF");
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "poEEth0Pwr");
		eventAttribute.put("v", AhEvent.getPoEPowerString(event.getPoEEth0Pwr()));
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "poEEth1On");
		eventAttribute.put("v", event.getPoEEth1On() == AhEvent.POE_ON ? "ON" : "OFF");
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "poEEth1Pwr");
		eventAttribute.put("v", AhEvent.getPoEPowerString(event.getPoEEth1Pwr()));
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "poEEth0MaxSpeed");
		eventAttribute.put("v", AhEvent.getPoEEthMaxSpeed(event.getPoEEth0MaxSpeed()));
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "poEEth1MaxSpeed");
		eventAttribute.put("v", AhEvent.getPoEEthMaxSpeed(event.getPoEEth1MaxSpeed()));
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "poEWifi0Setting");
		eventAttribute.put("v", AhEvent.getPoEWifiSetting(event.getPoEWifi0Setting()));
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "poEWifi1Setting");
		eventAttribute.put("v", AhEvent.getPoEWifiSetting(event.getPoEWifi1Setting()));
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "poEWifi2Setting");
		eventAttribute.put("v", AhEvent.getPoEWifiSetting(event.getPoEWifi2Setting()));
		eventDetails.add(eventAttribute);

		/*
		 * channel power change event
		 * joseph chen
		 * 08/21/2008
		 */
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "radioChannel");
		eventAttribute.put("v", event.getRadioChannel());
		eventDetails.add(eventAttribute);

		eventAttribute = new JSONObject();
		eventAttribute.put("id", "radioTxPower");
		eventAttribute.put("v", event.getRadioTxPower() + " dBm");
		eventDetails.add(eventAttribute);
		
		// interface/client stats trap
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "alertType");
		eventAttribute.put("v", event.getAlertTypeShow());
		eventDetails.add(eventAttribute);
		
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "thresholdValue");
		eventAttribute.put("v", event.getThresholdValue());
		eventDetails.add(eventAttribute);
		
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "shorttermValue");
		eventAttribute.put("v", event.getShorttermValue());
		eventDetails.add(eventAttribute);
		
		eventAttribute = new JSONObject();
		eventAttribute.put("id", "snapshotValue");
		eventAttribute.put("v", event.getSnapshotValue());
		eventDetails.add(eventAttribute);

		return eventDetails;
	}

	/*
	 * Remove Hive Manager Business Objects by class
	 */
	/*- the function is not used already.
	@Override
	protected int removeAllBos(Class<? extends HmBo> boClass,
			Collection<Long> defaultIds) throws Exception {
		AccessControl.checkUserAccess(getUserContext(),
				getSelectedL2FeatureKey(), CrudOperation.DELETE);
		int count = QueryUtil.bulkRemoveBos(boClass, filterParams,
				getUserContext(), defaultIds);
		if (count > 0) {
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, "Remove " + count + " "
					+ getSelectedL2Feature().getDescription());
		}
		return count;
	}
	*/
	
	@Override
	protected int removeAllBos(Class<? extends HmBo> boClass,
			FilterParams filterParams, Collection<Long> defaultIds)
			throws Exception {
		
		AccessControl.checkUserAccess(getUserContext(), getSelectedL2FeatureKey(), CrudOperation.DELETE);
		int count = QueryUtil.bulkRemoveBos(boClass, filterParams, getUserContext(), defaultIds);
		
		if (count > 0) {
			//notify event paging cache
			AhEvent	removedEvent = new AhEvent();
			BoObserver.notifyListeners(new BoEvent<AhEvent>(removedEvent,
					BoEventType.REMOVED));
			
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.base.operation.remove") + count + " "
					+ getSelectedL2Feature().getDescription());
		}
		return count;
	}

	public int getEventInterval() {
		return eventInterval;
	}

	public void setEventInterval(int eventInterval) {
		this.eventInterval = eventInterval;
	}
	
	public List<String> getFilterList() {
		List<String> filterMap = (List<String>) QueryUtil.executeQuery(
				"select filterName from "
						+ AhEventsFilter.class.getSimpleName(), null,
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
			List<AhEventsFilter> filterList = QueryUtil
			.executeQuery(AhEventsFilter.class, null,
					new FilterParams("filterName=:s1 AND userName=:s2",
							new Object[] { filterName,
									getUserContext().getUserName() }),
					domainId);
			
			if (!filterList.isEmpty()) {
				AhEventsFilter filter = filterList
						.get(0);
				filter.setApId(this.apId);
				filter.setComponent(this.component);
				filter.setStartTime(this.getStartTime());
				filter.setEndTime(this.getEndTime());
				filter.setFilterName(this.filterName);
				setId(filter.getId());
				QueryUtil.updateBo(filter);
			} else {
				AhEventsFilter filter = new AhEventsFilter();
				filter.setUserName(getUserContext().getUserName());
				filter.setFilterName(filterName);
				filter.setApId(this.apId);
				filter.setComponent(this.component);
				filter.setStartTime(this.getStartTime());
				filter.setEndTime(this.getEndTime());
				filter.setOwner(getDomain());
				QueryUtil.createBo(filter);
			}

			MgrUtil.setSessionAttribute(MANAGED_EVENTS_CURRENT_FILTER,
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
					.getSessionAttribute(MANAGED_EVENTS_CURRENT_FILTER);
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
						"select apId,component,startTime,endTime from "
								+ AhEventsFilter.class.getSimpleName(),
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
			jsonObject.put("eComponent", objArray[1]);
		}
		if (objArray.length > 2) {
			jsonObject.put("eBeginDate", this.getDateStr((Long)objArray[2]));
			jsonObject.put("eBeginTime", this.getHourStr((Long)objArray[2]));
		}
		if (objArray.length > 3) {
			jsonObject.put("eEndDate", this.getDateStr((Long)objArray[3]));
			jsonObject.put("eEndTime", this.getHourStr((Long)objArray[3]));
		}
	}
	
	private void removeEventsFilter() {
		if (null == filterName || "".equals(filterName.trim())) {
			return;
		}

		try {
			List<AhEventsFilter> list = QueryUtil
					.executeQuery(AhEventsFilter.class, null,
							new FilterParams("filterName=:s1 AND userName=:s2",
									new Object[] { filterName,
											getUserContext().getUserName() }),
							domainId);
			if (!list.isEmpty()) {
				AhEventsFilter filter = list.get(0);
				AhEventsFilter rmbos = findBoById(AhEventsFilter.class, filter
						.getId());
				QueryUtil.removeBoBase(rmbos);
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage("action.error.event.filter.remove.fail"));
		}
	}

}