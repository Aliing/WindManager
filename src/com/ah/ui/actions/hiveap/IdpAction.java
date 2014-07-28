package com.ah.ui.actions.hiveap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeTopoUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.communication.mo.classifyap.ClassifyBaseAp;
import com.ah.be.communication.util.classifyap.ClassifyApManualUtil;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.be.topo.idp.IdpMitigationEvent;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.hiveap.IdpAp;
import com.ah.bo.hiveap.MitigateManualModeItem;
import com.ah.bo.hiveap.MitigateManualModeList;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.PagingImpl;
import com.ah.bo.network.IdsPolicy;
import com.ah.bo.performance.AhLatestRadioAttribute;
import com.ah.bo.performance.AhLatestXif;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.SessionKeys;
import com.ah.ui.actions.monitor.MapsAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class IdpAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(
			IdpAction.class.getSimpleName());

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_ROGUE_APS);
		setDataSource(Idp.class);
		enableSorting();
		// default sorting
		if (sortParams.getOrderBy().equals("id")) {
			sortParams.setOrderBy("ifMacAddress");
		}
		keyColumnId = COLUMN_BSSID;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_BSSID = 1;

	public static final int COLUMN_SSID = 2;

	public static final int COLUMN_CHANNEL = 3;

	public static final int COLUMN_INNETWORK = 4;

	public static final int COLUMN_RSSI = 5;

	public static final int COLUMN_SUPPORTSETTINGS = 6;

	public static final int COLUMN_NOCOMPLIANTSETTINGS = 7;

	public static final int COLUMN_REPORTAP = 8;

	public static final int COLUMN_TIMEREPORTED = 9;

	public static final int COLUMN_VENDOR = 10;

	public static final int COLUMN_ONMAP = 11;

	public static final int COLUMN_MITIGATE = 12;

	public static final int COLUMN_CLIENTS = 13;

	public static final int COLUMN_HIGHEST_RSSI = 14;

	public static final int COLUMN_LASTEST_REPORTED = 15;

	public static final int COLUMN_REPORT_BSSID = 16;

	public static final int COLUMN_MODE = 17;

	/**
	 * get the description of column by id
	 * 
	 * @param id
	 *            -
	 * @return -
	 */
	@Override
	public String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_BSSID:
			if ("rogueClient".equals(listType)) {
				code = "monitor.rogueClient.clientMac";
			} else {
				code = "monitor.hiveAp.report.ifBSSID";
			}
			break;
		case COLUMN_SSID:
			code = "monitor.hiveAp.report.ifSSID";
			break;
		case COLUMN_CHANNEL:
			code = "monitor.hiveAp.report.channel";
			break;
		case COLUMN_INNETWORK:
			code = "monitor.hiveAp.report.network";
			break;
		case COLUMN_RSSI:
			code = "monitor.hiveAp.report.rssi";
			break;
		case COLUMN_SUPPORTSETTINGS:
			code = "monitor.hiveAp.report.support";
			break;
		case COLUMN_NOCOMPLIANTSETTINGS:
			code = "monitor.hiveAp.report.noncompliant";
			break;
		case COLUMN_REPORTAP:
			code = "monitor.hiveAp.report.hiveAp.NodeId";
			break;
		case COLUMN_TIMEREPORTED:
			code = "monitor.hiveAp.report.time";
			break;
		case COLUMN_VENDOR:
			code = "hiveAp.macaddress.macOui";
			break;
		case COLUMN_ONMAP:
			code = "monitor.hiveAp.report.onMap";
			break;
		case COLUMN_MITIGATE:
			code = "monitor.hiveAp.report.mitigation";
			break;
		case COLUMN_MODE:
			code = "monitor.hiveAp.report.mode";
			break;
		case COLUMN_CLIENTS:
			code = "monitor.hiveAp.report.clients";
			break;
		case COLUMN_HIGHEST_RSSI:
			code = "monitor.hiveAp.report.highest.rssi";
			break;
		case COLUMN_LASTEST_REPORTED:
			code = "monitor.hiveAp.lastDetectedTime";
			break;
		case COLUMN_REPORT_BSSID:
			code = "monitor.hiveAp.report.hiveAp.bssid";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(15);

		if ("rogueAps".equals(listType)) {
			if (viewMode == VIEW_MODE_PLAIN) {
				columns.add(new HmTableColumn(COLUMN_BSSID));
				columns.add(new HmTableColumn(COLUMN_VENDOR));
				columns.add(new HmTableColumn(COLUMN_SSID));
				columns.add(new HmTableColumn(COLUMN_ONMAP));
				columns.add(new HmTableColumn(COLUMN_HIGHEST_RSSI));
				columns.add(new HmTableColumn(COLUMN_LASTEST_REPORTED));
			} else {
				columns.add(new HmTableColumn(COLUMN_BSSID));
				columns.add(new HmTableColumn(COLUMN_VENDOR));
				columns.add(new HmTableColumn(COLUMN_SSID));
				columns.add(new HmTableColumn(COLUMN_CHANNEL));
				columns.add(new HmTableColumn(COLUMN_CLIENTS));
				columns.add(new HmTableColumn(COLUMN_MITIGATE));
				columns.add(new HmTableColumn(COLUMN_MODE));
				columns.add(new HmTableColumn(COLUMN_INNETWORK));
				columns.add(new HmTableColumn(COLUMN_ONMAP));
				columns.add(new HmTableColumn(COLUMN_RSSI));
				columns.add(new HmTableColumn(COLUMN_SUPPORTSETTINGS));
				columns.add(new HmTableColumn(COLUMN_NOCOMPLIANTSETTINGS));
				columns.add(new HmTableColumn(COLUMN_REPORTAP));
				columns.add(new HmTableColumn(COLUMN_REPORT_BSSID));
				columns.add(new HmTableColumn(COLUMN_TIMEREPORTED));
			}
		} else if ("friendlyAps".equals(listType)) {
			if (viewMode == VIEW_MODE_PLAIN) {
				columns.add(new HmTableColumn(COLUMN_BSSID));
				columns.add(new HmTableColumn(COLUMN_VENDOR));
				columns.add(new HmTableColumn(COLUMN_SSID));
				columns.add(new HmTableColumn(COLUMN_HIGHEST_RSSI));
				columns.add(new HmTableColumn(COLUMN_LASTEST_REPORTED));
			} else {
				columns.add(new HmTableColumn(COLUMN_BSSID));
				columns.add(new HmTableColumn(COLUMN_VENDOR));
				columns.add(new HmTableColumn(COLUMN_SSID));
				columns.add(new HmTableColumn(COLUMN_CHANNEL));
				columns.add(new HmTableColumn(COLUMN_INNETWORK));
				columns.add(new HmTableColumn(COLUMN_RSSI));
				columns.add(new HmTableColumn(COLUMN_SUPPORTSETTINGS));
				columns.add(new HmTableColumn(COLUMN_NOCOMPLIANTSETTINGS));
				columns.add(new HmTableColumn(COLUMN_REPORTAP));
				columns.add(new HmTableColumn(COLUMN_REPORT_BSSID));
				columns.add(new HmTableColumn(COLUMN_TIMEREPORTED));
			}
		} else if ("rogueClient".equals(listType)) {
			if (viewMode == VIEW_MODE_PLAIN) {
				columns.add(new HmTableColumn(COLUMN_BSSID));
				columns.add(new HmTableColumn(COLUMN_VENDOR));
				columns.add(new HmTableColumn(COLUMN_SSID));
				columns.add(new HmTableColumn(COLUMN_HIGHEST_RSSI));
				columns.add(new HmTableColumn(COLUMN_LASTEST_REPORTED));
			} else {
				columns.add(new HmTableColumn(COLUMN_BSSID));
				columns.add(new HmTableColumn(COLUMN_VENDOR));
				columns.add(new HmTableColumn(COLUMN_SSID));
				columns.add(new HmTableColumn(COLUMN_CHANNEL));
				// columns.add(new HmTableColumn(COLUMN_INNETWORK));
				columns.add(new HmTableColumn(COLUMN_RSSI));
				// columns.add(new HmTableColumn(COLUMN_SUPPORTSETTINGS));
				columns.add(new HmTableColumn(COLUMN_NOCOMPLIANTSETTINGS));
				columns.add(new HmTableColumn(COLUMN_REPORTAP));
				columns.add(new HmTableColumn(COLUMN_REPORT_BSSID));
				columns.add(new HmTableColumn(COLUMN_TIMEREPORTED));
			}
		}
		return columns;
	}

	private void setCurrentTableId() {
		if ("rogueAps".equals(listType)) {
			if (viewMode == VIEW_MODE_PLAIN) {
				tableId = HmTableColumn.TABLE_IDP_ROGUE_AP_PLAIN;
			} else {
				tableId = HmTableColumn.TABLE_IDP_ROGUE_AP;
			}
		} else if ("friendlyAps".equals(listType)) {
			if (viewMode == VIEW_MODE_PLAIN) {
				tableId = HmTableColumn.TABLE_IDP_FRIENDLY_AP_PLAIN;
			} else {
				tableId = HmTableColumn.TABLE_IDP_FRIENDLY_AP;
			}
		} else if ("rogueClient".equals(listType)) {
			if (viewMode == VIEW_MODE_PLAIN) {
				tableId = HmTableColumn.TABLE_ROGUECLIENT_PLAIN;
			} else {
				tableId = HmTableColumn.TABLE_ROGUECLIENT;
			}
		} else {
			tableId = 0;
		}
	}

	private void setCurrentViewMode() {
		Integer object = (Integer) MgrUtil
				.getSessionAttribute(IDP_LIST_VIEW_MODE);
		if (null != object) {
			viewMode = object;
		}
	}

	private void setCurrentManagedHiveAPBssidFilter() {
		if ("friendlyAps".equals(listType)) {
			filterManagedHiveAPBssid = BoMgmt.getIdpMgmt()
					.getManagedHiveAPBssidFilter(domainId);
		}
	}

	private static final String IDP_LIST_VIEW_MODE = "idp_list_view_mode";// session

	// key
	public static final int VIEW_MODE_DETAIL = 0;

	public static final int VIEW_MODE_PLAIN = 1;

	private int viewMode;

	public int getViewMode() {
		return viewMode;
	}

	public void setViewMode(int viewMode) {
		this.viewMode = viewMode;
	}

	public boolean getDetailViewMode() {
		return viewMode == VIEW_MODE_DETAIL;
	}

	public EnumItem[] getViewModeOption1() {
		return new EnumItem[] { new EnumItem(VIEW_MODE_DETAIL,
				getText("idp.view.mode.label.detail")) };
	}

	public EnumItem[] getViewModeOption2() {
		return new EnumItem[] { new EnumItem(VIEW_MODE_PLAIN,
				getText("idp.view.mode.label.plain")) };
	}

	protected int cacheId;

	public int getCacheId() {
		return cacheId;
	}

	public void setCacheId(int cacheId) {
		this.cacheId = cacheId;
	}

	private String listType;

	public void setListType(String listType) {
		this.listType = listType;
	}

	private String category;

	public void setCategory(String category) {
		this.category = category;
	}

	private boolean filterManagedHiveAPBssid;

	public void setFilterManagedHiveAPBssid(boolean filterManagedHiveAPBssid) {
		this.filterManagedHiveAPBssid = filterManagedHiveAPBssid;
	}

	public boolean isFilterManagedHiveAPBssid() {
		return filterManagedHiveAPBssid;
	}

	public boolean isInnetCategory() {
		String viewType = (String) MgrUtil
				.getSessionAttribute(IDP_ROGUE_CATEGORY_VIEW);
		return "innet".equals(viewType);
	}

	public boolean isOnmapCategory() {
		String viewType = (String) MgrUtil
				.getSessionAttribute(IDP_ROGUE_CATEGORY_VIEW);
		return "onmap".equals(viewType);
	}

	public boolean isStrongCategory() {
		String viewType = (String) MgrUtil
				.getSessionAttribute(IDP_ROGUE_CATEGORY_VIEW);
		return "strong".equals(viewType);
	}

	public boolean isWeakCategory() {
		String viewType = (String) MgrUtil
				.getSessionAttribute(IDP_ROGUE_CATEGORY_VIEW);
		return "weak".equals(viewType);
	}

	private FilterParams getFilterParams(String listType, String category,
			String bssid, String reportId, String parentBssid) {
		String where = null;
		List<Object> values = new ArrayList<Object>();
		if ("rogueAps".equals(listType)) {
			int threshold = BoMgmt.getIdpMgmt().getSignalThreshold(domainId) + 95;
			if ("innet".equals(category)) {
				where = "stationType = :s1 AND idpType = :s2 AND inNetworkFlag = :s3";
				values.add(BeCommunicationConstant.IDP_STATION_TYPE_AP);
				values.add(BeCommunicationConstant.IDP_TYPE_ROGUE);
				values.add(Idp.IDP_CONNECTION_IN_NET);
			} else if ("onmap".equals(category)) {
				where = "stationType = :s1 AND idpType = :s2 AND mapId > :s3";
				values.add(BeCommunicationConstant.IDP_STATION_TYPE_AP);
				values.add(BeCommunicationConstant.IDP_TYPE_ROGUE);
				values.add(0L);
			} else if ("strong".equals(category)) {
				where = "stationType = :s1 AND idpType = :s2 AND rssi >= :s3";
				values.add(BeCommunicationConstant.IDP_STATION_TYPE_AP);
				values.add(BeCommunicationConstant.IDP_TYPE_ROGUE);
				values.add((short) threshold);
			} else if ("weak".equals(category)) {
				where = "stationType = :s1 AND idpType = :s2 AND rssi < :s3";
				values.add(BeCommunicationConstant.IDP_STATION_TYPE_AP);
				values.add(BeCommunicationConstant.IDP_TYPE_ROGUE);
				values.add((short) threshold);
			} else {
				where = "stationType = :s1 AND idpType = :s2";
				values.add(BeCommunicationConstant.IDP_STATION_TYPE_AP);
				values.add(BeCommunicationConstant.IDP_TYPE_ROGUE);
			}
		} else if ("friendlyAps".equals(listType)) {
			boolean filter = BoMgmt.getIdpMgmt().getManagedHiveAPBssidFilter(
					domainId);
			if (filter) {
				Collection<Short> idpTypes = new ArrayList<Short>();
				idpTypes.add(BeCommunicationConstant.IDP_TYPE_EXTERNAL);
				idpTypes.add(BeCommunicationConstant.IDP_TYPE_VALID);
				where = "stationType = :s1 AND idpType in (:s2) AND isManaged = :s3";
				values.add(BeCommunicationConstant.IDP_STATION_TYPE_AP);
				values.add(idpTypes);
				values.add(false);
			} else {
				Collection<Short> idpTypes = new ArrayList<Short>();
				idpTypes.add(BeCommunicationConstant.IDP_TYPE_EXTERNAL);
				idpTypes.add(BeCommunicationConstant.IDP_TYPE_VALID);
				where = "stationType = :s1 AND idpType in (:s2)";
				values.add(BeCommunicationConstant.IDP_STATION_TYPE_AP);
				values.add(idpTypes);
			}
		} else if ("rogueClient".equals(listType)) {// rogue client
			where = "stationType = :s1 AND idpType = :s2";
			values.add(BeCommunicationConstant.IDP_STATION_TYPE_CLIENT);
			values.add(BeCommunicationConstant.IDP_TYPE_ROGUE);
			if (operation != null) {
				if (operation.equals("rogueClientInNet")) {
					where = where + " AND inNetworkFlag =:s3";
					values.add(BeCommunicationConstant.IDP_CONNECTION_IN_NET);
				} else if (operation.equals("rogueClientOnMap")) {
					where = where + " AND mapId is not null";
				}
			}
		}
		if (null == where) {
			log.error("getFilterParams",
					"no FilterParams while execute Idp action..");
			return null;
		}
		if (null != bssid && !"".equals(bssid.trim())) {
			where = where + " AND lower(ifMacAddress) like :s"
					+ (values.size() + 1);
			values.add("%" + bssid.trim().toLowerCase() + "%");
		}
		if (null != reportId && !"".equals(reportId.trim())) {
			where = where + " AND lower(reportNodeId) like :s"
					+ (values.size() + 1);
			values.add("%" + reportId.trim().toLowerCase() + "%");
		}
		if (null != parentBssid && !"".equals(parentBssid.trim())) {
			where = where + " AND parentBssid = :s" + (values.size() + 1);
			values.add(parentBssid);
		}
		return new FilterParams(where, values.toArray());
	}

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}

		// get list type from session
		if (null == listType || "".equals(listType)) {
			listType = (String) MgrUtil
					.getSessionAttribute(IDP_SELECTED_LIST_VIEW);
		} else {
			MgrUtil.setSessionAttribute(IDP_SELECTED_LIST_VIEW, listType);
		}
		// get category from session if category parameter not passed
		if (null == category || "".equals(category)) {
			category = (String) MgrUtil
					.getSessionAttribute(IDP_ROGUE_CATEGORY_VIEW);
		}
		setSelectedL2Feature(listType);
		resetPermission();

		if ("switch".equals(operation) || "search".equals(operation)
				|| "clients".equals(operation) || "removeFilter".equals(operation)) {
			filterParams = getFilterParams(listType, category, bssid, nodeId,
					parentBssid);
			setSessionFiltering();
		} else {
			getSessionFiltering();
		}

		try {
			log.info("execute", "operation:" + operation);
			if (null == operation) {
				MgrUtil.removeSessionAttribute(IDP_CURRENT_FILTER);
			}
			
			// check session token for CSRF attack
			if (!isCSRFTokenValida() && ("mitigate".equals(operation) || "nomitigate".equals(operation))) {
				generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("security.csrfattack") + getLastTitle());

				throw new HmException(MgrUtil.getUserMessage("error.security.invalidRequest"),						
						HmMessageCodes.SECURITY_REQUEST_INVALID, new String[] { "mitigate" });
			}
			
			if ("toRogue".equals(operation)) {
				Set<String> movedBssids = moveOperation(
						BeCommunicationConstant.IDP_TYPE_ROGUE,
						IdpAction.LISTVIEW_ROGUEAP);
				BoMgmt.getIdpMgmt().addEnclosedRogueAps(movedBssids, domainId);
				ClassifyApManualUtil
					.sendManualRogueApRequests(new ArrayList<String>(movedBssids), 
						ClassifyBaseAp.DATA_OPERATION_FLAG_ADD, getDomain());
				return prepareIdpList(true);
			} else if ("toFriendly".equals(operation)) {
				Set<String> movedBssids = moveOperation(
						BeCommunicationConstant.IDP_TYPE_VALID,
						IdpAction.LISTVIEW_FRIENDLYAP);
				BoMgmt.getIdpMgmt().addEnclosedFriendlyAps(movedBssids,
						domainId);
				ClassifyApManualUtil
					.sendManualFriendlyApRequests(new ArrayList<String>(movedBssids), 
							ClassifyBaseAp.DATA_OPERATION_FLAG_ADD, getDomain());
				return prepareIdpList(true);
			} else if ("switch".equals(operation)) {
				MgrUtil.setSessionAttribute(IDP_ROGUE_CATEGORY_VIEW, category);
				MgrUtil.removeSessionAttribute(IDP_LIST_VIEW_CURRENT_FILTERS);
				MgrUtil.removeSessionAttribute(IDP_CURRENT_FILTER);
				return prepareIdpList(true);
			} else if ("switchViewMode".equals(operation)) {
				MgrUtil.setSessionAttribute(IDP_LIST_VIEW_MODE, viewMode);
				sortParams.setOrderBy("ifMacAddress");
				return prepareIdpList(true);
			} else if ("filterManaged".equals(operation)) {
				BoMgmt.getIdpMgmt().updateManagedHiveAPBssidFilter(domainId,
						filterManagedHiveAPBssid);
				MgrUtil.removeSessionAttribute(IDP_LIST_VIEW_CURRENT_FILTERS);
				filterParams = getFilterParams(listType, category, null, null,
						null);
				setSessionFiltering();
				return prepareIdpList(true);
			} else if ("updates".equals(operation)) {
				log.debug("execute", "Updates from cache: " + cacheId);
				setCurrentViewMode();
				IdpPagingCache idpPagingCache = getIdpListCache(viewMode);
				jsonArray = new JSONArray(idpPagingCache.getUpdates(cacheId));
				return "json";
			} else if ("refreshFromCache".equals(operation)) {
				log.debug("execute", "Refresh from cache: " + cacheId);
				return prepareIdpList(false);
			} else if ("editInterval".equals(operation)) {
				jsonObject = getIntervalValue();
				return "json";
			} else if ("updateInterval".equals(operation)) {
				log.info("execute", "update interval, new value:" + interval);
				jsonObject = getIntervalUpdateResult();
				return "json";
			} else if ("editThreshold".equals(operation)) {
				jsonObject = getThresholdValue();
				return "json";
			} else if ("updateThreshold".equals(operation)) {
				log.info("execute", "update threshold, new value:" + -threshold
						+ "dbm.");
				jsonObject = getThresholdUpdateResult();
				return "json";
			} else if ("editEnclosedRogueAps".equals(operation)) {
				jsonObject = getEnclosedRogueAps();
				return "json";
			} else if ("editEnclosedFriendlyAps".equals(operation)) {
				jsonObject = getEnclosedFriendlyAps();
				return "json";
			} else if ("removeEnclosedRogueAps".equals(operation)) {
				log.info("removeEnclosedRogueAps", "selected bssids:"
						+ bssidString);
				Set<String> bssids = new HashSet<String>();
				if (null != bssidString) {
					String[] array = bssidString.split(",");
					bssids.addAll(Arrays.asList(array));
				}
				jsonObject = removeEnclosedRogueAps(bssids);
				return "json";
			} else if ("removeEnclosedFriendlyAps".equals(operation)) {
				log.info("removeEnclosedFriendlyAps", "selected bssids:"
						+ bssidString);
				Set<String> bssids = new HashSet<String>();
				if (null != bssidString) {
					String[] array = bssidString.split(",");
					bssids.addAll(Arrays.asList(array));
				}
				jsonObject = removeEnclosedFriendlyAps(bssids);
				return "json";
			} else if ("showBssidDetails".equals(operation)) {
				log.info("execute", "operation:" + operation + ", bssid:"
						+ bssid + ", domainId:" + domainId);
				jsonArray = getBssidDetails(bssid, domainId);
				return "json";
			} else if ("requestFilter".equals(operation)) {
				log.info("execute", "request selected filter, filter name:"
						+ filterName);
				jsonObject = getSelectedFilter();
				return "json";
			} else if ("search".equals(operation)) {
				searchByFilter();
				return prepareIdpList(true);
			}else if("removeFilter".equals(operation)){
				Map<String, List<Object>> filterMap = getFiltersInSession();
				String key="-" + listType + "-" + filterName.trim();
				filterMap.remove(key);
				return prepareIdpList(true);
			} else if ("refresh".equals(operation)) {
				refreshOperation();
				return prepareIdpList(true);
			} else if ("mitigate".equals(operation)) {
				doMitigateOperationFromGui(true);
				return prepareIdpList(true);
			} else if ("nomitigate".equals(operation)) {
				doMitigateOperationFromGui(false);
				return prepareIdpList(true);
			} else if ("manualMitigate".equals(operation)
					|| "manualMitigateCancel".equals(operation)) {
				if (!selectedIdpAps.isEmpty()
						&& "manualMitigate".equals(operation)) {
					Map<Long, List<String>> idpAps = new HashMap<Long, List<String>>();
					for (String idpApStr : selectedIdpAps) {
						String[] twoStr = idpApStr.split("-");
						Long idpId = Long.parseLong(twoStr[0]);

						List<String> apMacs = idpAps.get(idpId);
						if (null == apMacs) {
							apMacs = new ArrayList<String>();
							apMacs.add(twoStr[1]);
							idpAps.put(idpId, apMacs);
						} else {
							apMacs.add(twoStr[1]);
						}
					}
					mitigateOperationForManualMode(true, idpAps);

					addActionMessage(MgrUtil.getUserMessage("message.request.submit"));
				}
				List<MitigateManualModeList> manualApList = (List<MitigateManualModeList>) MgrUtil
						.getSessionAttribute(MONITOR_MANUAL_ROGUE_AP_MITIGATE);
				if (null != manualApList && !manualApList.isEmpty()) {
					manualModeAp = manualApList.get(0);
					manualApList.remove(0);

					if (!manualApList.isEmpty()) {
						MgrUtil.setSessionAttribute(
								MONITOR_MANUAL_ROGUE_AP_MITIGATE, manualApList);
					} else {
						MgrUtil.removeSessionAttribute(MONITOR_MANUAL_ROGUE_AP_MITIGATE);

						// clear selection or the previous selected id is still
						// in the list.
						setAllSelectedIds(null);
					}
				} else {
					// clear selection or the previous selected id is still in
					// the list.
					setAllSelectedIds(null);
				}
				return prepareIdpList(true);
			} else if ("showMitigate".equals(operation)) {
				log.info("execute", "BSSID:" + bssid
						+ ", report HiveAP nodeId:" + nodeId);
				fetchMitigateClients(bssid, nodeId);
				return "json";
			} else if ("clients".equals(operation)) {
				MgrUtil.removeSessionAttribute(IDP_LIST_VIEW_CURRENT_FILTERS);
				return prepareIdpList(true);
			} else if (baseOperation()) {
				return prepareIdpList(true);
			} else {
				MgrUtil.removeSessionAttribute(IDP_LIST_VIEW_CURRENT_FILTERS);
				filterParams = getFilterParams(listType, category, null, null,
						null);
				setSessionFiltering();
				return prepareIdpList(true);
			}
		} catch (Exception e) {
			setL3Features(null);
			prepareActionError(e);
			return listType;
		}
	}

	private JSONObject getIntervalValue() throws JSONException {
		JSONObject jsonObj = new JSONObject();
		int interval = BoMgmt.getIdpMgmt().getRefreshInterval(domainId);
		jsonObj.put("itv", interval);
		return jsonObj;
	}

	private JSONObject getThresholdValue() throws JSONException {
		JSONObject jsonObj = new JSONObject();
		int threshold = BoMgmt.getIdpMgmt().getSignalThreshold(domainId);
		jsonObj.put("trh", threshold);
		return jsonObj;
	}

	private JSONObject getEnclosedRogueAps() throws JSONException {
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		Set<String> bssids = BoMgmt.getIdpMgmt().getEnclosedRogueAps(domainId);
		if (null != bssids) {
			jsonArray = wrapJsonArray(bssids);
		} else {
			jsonObj.put("info", "Initialize Enclosed list error.");
		}
		jsonObj.put("eros", jsonArray);
		return jsonObj;
	}

	private JSONObject getEnclosedFriendlyAps() throws JSONException {
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		Set<String> bssids = BoMgmt.getIdpMgmt().getEnclosedFriendlyAps(
				domainId);
		if (null != bssids) {
			jsonArray = wrapJsonArray(bssids);
		} else {
			jsonObj.put("info", "Initialize Enclosed list error.");
		}
		jsonObj.put("efris", jsonArray);
		return jsonObj;
	}

	private JSONObject removeEnclosedFriendlyAps(Set<String> bssids)
			throws JSONException {
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		Set<String> existed = BoMgmt.getIdpMgmt().removeEnclosedFriendlyAps(
				bssids, domainId);
		if (null != existed) {
			jsonArray = wrapJsonArray(existed);
			jsonObj.put(
					"info",
					MgrUtil.getUserMessage("info.objectsRemoved",
							String.valueOf(bssids.size())));
			try {
				ClassifyApManualUtil
					.sendManualFriendlyApRequests(new ArrayList<String>(bssids), 
							ClassifyBaseAp.DATA_OPERATION_FLAG_REMOVE, getDomain());
			} catch (Exception e) {
				log.error("Failed to send mitigation event while removing classified rogue AP(s).");
			}
			try {
				BoMgmt.getIdpMgmt().removeIdpsByBssid(bssids, domainId);
			} catch (Exception e) {
				log.error("Failed to remove friendly AP(s) information.");
			}
		} else {
			jsonObj.put("info", "Remove Enclosed Friendly APs error.");
		}
		jsonObj.put("efris_r", jsonArray);
		return jsonObj;
	}

	private JSONObject removeEnclosedRogueAps(Set<String> bssids)
			throws JSONException {
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		Set<String> existed = BoMgmt.getIdpMgmt().removeEnclosedRogueAps(
				bssids, domainId);
		if (null != existed) {
			jsonArray = wrapJsonArray(existed);
			jsonObj.put(
					"info",
					MgrUtil.getUserMessage("info.objectsRemoved",
							String.valueOf(bssids.size())));
			try {
				ClassifyApManualUtil
					.sendManualRogueApRequests(new ArrayList<String>(bssids), 
							ClassifyBaseAp.DATA_OPERATION_FLAG_REMOVE, getDomain());
			} catch (Exception e) {
				log.error("Failed to send mitigation event while removing classified rogue AP(s).");
			}
			try {
				BoMgmt.getIdpMgmt().removeIdpsByBssid(bssids, domainId);
			} catch (Exception e) {
				log.error("Failed to remove rogue AP(s) information.");
			}
		} else {
			jsonObj.put("info", "Remove Enclosed Rogue APs error.");
		}
		jsonObj.put("eros_r", jsonArray);
		return jsonObj;
	}

	private JSONArray wrapJsonArray(Set<String> set) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for (String bssid : set) {
			JSONObject obj = new JSONObject();
			obj.put("key", bssid);
			obj.put("value", bssid);
			jsonArray.put(obj);
		}
		if (jsonArray.length() == 0) {
			JSONObject obj = new JSONObject();
			obj.put("key", "-1");
			obj.put("value",
					MgrUtil.getUserMessage("config.optionsTransfer.none"));
			jsonArray.put(obj);
		}
		return jsonArray;
	}

	private JSONObject getIntervalUpdateResult() throws JSONException {
		JSONObject jsonObj = new JSONObject();
		boolean result = BoMgmt.getIdpMgmt().updateRefreshInterval(domainId,
				interval);
		if (result) {
			jsonObj.put("suc", true);
		}
		return jsonObj;
	}

	private JSONObject getThresholdUpdateResult() throws JSONException {
		JSONObject jsonObj = new JSONObject();
		boolean result = BoMgmt.getIdpMgmt().updateSignalThreshold(domainId,
				-threshold);
		if (result) {
			jsonObj.put("suc", true);
		}
		return jsonObj;
	}

	private JSONArray getBssidDetails(String bssid, Long domainId)
			throws Exception {
		JSONArray array = new JSONArray();
		if (null != bssid && null != domainId) {
			FilterParams filter;
			if (null != filterParams && null != filterParams.getWhere()) {
				String newWhere = filterParams.getWhere()
						+ " and ifMacAddress = :s"
						+ (filterParams.getBindings().length + 1);
				Object[] binds = filterParams.getBindings();
				Object[] newBinds = new Object[binds.length + 1];
				System.arraycopy(binds, 0, newBinds, 0, binds.length);
				newBinds[binds.length] = bssid;
				filter = new FilterParams(newWhere, newBinds);
			} else {
				filter = new FilterParams("ifMacAddress", bssid);
			}
			List<Idp> list = QueryUtil.executeQuery(Idp.class, null, filter,
					domainId);
			if (!list.isEmpty()) {
				IdpPagingCache.queryMitigateClientInfo(list);
				IdpPagingCache.queryReportedBssidInfo(list);
				BoMgmt.getLocationTracking().findRogueRssi(getDomain(), list);
			}
			String userTimeZone = userContext.getTimeZone();
			for (Idp idp : list) {
				String apName = idp.getReportHostName();
				idp.setUserTimeZone(userTimeZone);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("bssid", idp.getIfMacAddress());
				if (null != apName) {
					String hyperlink = "<a href=\"hiveApMonitor.action?operation=hiveApDetails&id="
							+ idp.getReportHiveAPId() + "\">" + apName + "</a>";
					jsonObj.put("rp", hyperlink);
				} else {
					jsonObj.put("rp", idp.getReportNodeId());
				}
				jsonObj.put("rpb", idp.getReportedBssid());
				jsonObj.put("rpTime", idp.getReportTimeString());
				jsonObj.put("client", idp.getClientCount());
				jsonObj.put("mit", idp.getMitigatedString());
				jsonObj.put("mode", idp.getModeString());
				jsonObj.put("channel", idp.getChannelString());
				jsonObj.put("inNet", idp.getNetworkString());
				jsonObj.put("onMap", idp.getMapName());
				jsonObj.put("rssi", idp.getRssiDbm());
				jsonObj.put("support", idp.getSupportString());
				jsonObj.put("complince", idp.getComplianceString());
				array.put(jsonObj);
			}
		}
		return array;
	}

	private JSONObject getSelectedFilter() throws JSONException {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("name", filterName);

		Map<String, List<Object>> filterMap = getFiltersInSession();
		if (null == filterMap) {
			return jsonObj;
		}
		List<Object> list = filterMap.get("-" + listType + "-" + filterName);
		if (null == list) {
			return jsonObj;
		}
		if (!list.isEmpty()) {
			jsonObj.put("bssid", list.get(0));
		}
		return jsonObj;
	}

	private void searchByFilter() {
		List<Object> filter = new ArrayList<Object>();
		filter.add(bssid);
		MgrUtil.setSessionAttribute(IDP_LIST_VIEW_CURRENT_FILTERS, filter);

		if (null != filterName && !"".equals(filterName.trim())) {
			Map<String, List<Object>> filterMap = getFiltersInSession();
			if (null == filterMap) {
				filterMap = new LinkedHashMap<String, List<Object>>();
			}
			filter.add(filterName.trim());
			filterMap.put("-" + listType + "-" + filterName.trim(), filter);
			MgrUtil.setSessionAttribute(IDP_LIST_VIEW_FILTERS, filterMap);
			MgrUtil.setSessionAttribute(IDP_CURRENT_FILTER, filterName);
			this.filter = filterName;
		}
	}

	public List<String> getFilterListFromSession(String listType) {
		Map<String, List<Object>> filterMap = getFiltersInSession();
		if (null == filterMap) {
			return new ArrayList<String>();
		}
		Set<String> keySet = filterMap.keySet();
		List<String> list = new ArrayList<String>();
		for (String key : keySet) {
			if (key.startsWith("-" + listType + "-")) {
				list.add(key.replace("-" + listType + "-", ""));
			}
		}
		// order by name
		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
		});
		return list;
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<Object>> getFiltersInSession() {
		return (Map<String, List<Object>>) MgrUtil.getSessionAttribute(IDP_LIST_VIEW_FILTERS);
	}

	@SuppressWarnings("unchecked")
	private List<String> getCurrentFilterInSession() {
		return (List<String>) MgrUtil
				.getSessionAttribute(IDP_LIST_VIEW_CURRENT_FILTERS);
	}

	/*
	 * Only create 1 map IDP cache per session.
	 */
	protected IdpPagingCache getIdpListCache(int viewMode) {
		IdpPagingCache idpPagingCache = (IdpPagingCache) MgrUtil
				.getSessionAttribute(SessionKeys.IDP_PAGING_CACHE);
		if (idpPagingCache == null) {
			idpPagingCache = new IdpPagingCache(userContext);
			MgrUtil.setSessionAttribute(SessionKeys.IDP_PAGING_CACHE,
					idpPagingCache);
		}
		Set<Long> allSelectedIds = getAllSelectedIds();
		List<Long> selectedIdps = getSelectedIdpIds(allSelectedIds);
		idpPagingCache.setSelectedIds(selectedIdps);
		idpPagingCache.setViewMode(viewMode);
		return idpPagingCache;
	}

	private void sortIdpByClients() {
		if (sortParams.isExtAscending()) {
			Collections.sort((List<Idp>) page, new Comparator<Idp>() {
				@Override
				public int compare(Idp idp1, Idp idp2) {
					return new Long(idp1.getClientCount()).compareTo(idp2.getClientCount());
				}
			});
		} else {
			Collections.sort((List<Idp>) page, new Comparator<Idp>() {
				@Override
				public int compare(Idp idp1, Idp idp2) {
					return new Long(idp2.getClientCount()).compareTo(idp1.getClientCount());
				}
			});
		}
	}

	public String prepareIdpList(boolean initCache) throws Exception {
		setCurrentViewMode();
		setCurrentManagedHiveAPBssidFilter();
		setCurrentTableId();
		setTableColumns();
		clearDataSource();
		IdpPagingCache idpPagingCache = getIdpListCache(viewMode);
		if (initCache) {
			enablePaging();
			cacheId = idpPagingCache.init();
			page = idpPagingCache.getBos(cacheId);
			if (sortParams != null
					&& "clientCountRogueAp".equals(sortParams.getExtOrderBy())) {
				IdpPagingCache.queryMitigateClientInfo(page);
				sortIdpByClients();
				page = paging.getAPageFromObjects(page);
				request.setAttribute("extOrderBy", sortParams.getExtOrderBy());
				request.setAttribute("extAscending",
						sortParams.isExtAscending());
			}
			// BoMgmt.getLocationTracking().findRogueRssi(getDomain(), page);
		} else {
			paging = (Paging<? extends HmBo>) MgrUtil.getSessionAttribute(boClass
					.getSimpleName() + "Paging");
			page = idpPagingCache.getBos(cacheId);
		}
		String userTimeZone = userContext.getTimeZone();
		setUserTimeZone(page, userTimeZone);
		if (null != selectedColumns) {
			for (HmTableColumn column : selectedColumns) {
				if (column.getColumnId() == COLUMN_ONMAP) {
					// Only fill in this field if this column is selected.
					IdpPagingCache.queryLocationInfo(getDomain(), page);
				} else if (column.getColumnId() == COLUMN_CLIENTS) {
					// Only fill in this field if this column is selected.
					if (sortParams != null
							&& (sortParams.getExtOrderBy() == null || ""
									.equals(sortParams.getExtOrderBy()))) {
						IdpPagingCache.queryMitigateClientInfo(page);
					}
				} else if (column.getColumnId() == COLUMN_MODE) {
					IdpPagingCache.queryIdsInfo(page);
				} else if (column.getColumnId() == COLUMN_REPORT_BSSID) {
					IdpPagingCache.queryReportedBssidInfo(page);
				}
			}
		}
		return (listType == null || "".equals(listType)) ? SUCCESS : listType;
	}

	public static void setUserTimeZone(List<Idp> page, String userTimeZone) {
		if (null == page) {
			return;
		}
		for (Idp idp : page) {
			idp.setUserTimeZone(userTimeZone);
		}
	}

	private Set<String> moveOperation(short type, short newListViewType)
			throws Exception {
		Set<Long> allSelectedIds = getAllSelectedIds();
		List<Long> selectedIdps = getSelectedIdpIds(allSelectedIds);
		Set<String> movedBssid = new HashSet<String>();
		if (selectedIdps != null && !selectedIdps.isEmpty()) {
			for (Long selectedId : selectedIdps) {
				Idp idp = findBoById(Idp.class, selectedId);
				if (null != idp) {
					idp.setIdpType(type);

					try {
						BoMgmt.getIdpMgmt().updateIdp(idp);
						if (!movedBssid.contains(idp.getIfMacAddress())) {
							// add audit log;
							generateAuditLog(HmAuditLog.STATUS_SUCCESS,
									MgrUtil.getUserMessage("hm.audit.log.move.ap.between.idplist",new String[]{idp.getLabel(),getIdpListName(listViewType),getIdpListName(newListViewType)}));
							// add action message;
							addActionMessage(MgrUtil.getUserMessage(
									OBJECT_MOVED, idp.getLabel()));
							// add to moved list
							movedBssid.add(idp.getIfMacAddress());
						}
					} catch (Exception e) {
						generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.move.ap.between.idplist",new String[]{idp.getLabel(),getIdpListName(listViewType),getIdpListName(newListViewType)}));
					}
				}
			}
			// clear selection or the previous selected id is still in the list.
			setAllSelectedIds(null);
		}
		return movedBssid;
	}

	private int removeAllBos(Class<? extends HmBo> boClass,
			Collection<Long> defaultIds) throws Exception {
		Set<Long> allSelectedIds = getAllSelectedIds();
		List<Long> selectedIdps = getSelectedIdpIds(allSelectedIds);
		if (selectedIdps != null && !selectedIdps.isEmpty()) {
			try {
				int count = BoMgmt.getIdpMgmt().removeIdps(selectedIdps,
						domainId);
				if (count > 0) {
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.base.operation.remove")
							+ count + " " + getIdpListName(listViewType));
				}
				return count;
			} catch (Exception e) {
				log.error("removeOperation", "remove operation failed.", e);
				return 0;
			}
		}
		return 0;
	}

	@Override
	protected int removeAllBos(Class<? extends HmBo> boClass,
			FilterParams filterParams, Collection<Long> defaultIds)
			throws Exception {
		return removeAllBos(boClass, defaultIds);
	}

	@Override
	protected int removeBos(Class<? extends HmBo> boClass, Collection<Long> ids)
			throws Exception {
		Set<Long> idsSet = new HashSet<Long>();
		idsSet.addAll(ids);
		List<Long> selectedIdps = getSelectedIdpIds(idsSet);
		if (selectedIdps != null && !selectedIdps.isEmpty()) {
			try {
				int count = BoMgmt.getIdpMgmt().removeIdps(selectedIdps,
						domainId);
				if (count > 0) {
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.base.operation.remove")
							+ count + " " + getIdpListName(listViewType));
				}
				// in plain view mode, show item count user selected
				int displayCount = count;
				if (viewMode == VIEW_MODE_PLAIN && null != ids) {
					displayCount = Math.min(ids.size(), selectedIdps.size());
				}
				return displayCount;
			} catch (Exception e) {
				log.error("removeOperation", "remove operation failed.", e);
				return 0;
			}
		}
		return 0;
	}

	private void refreshOperation() {
		Set<Long> allSelectedIds = getAllSelectedIds();
		List<Long> selectedIdps = getSelectedIdpIds(allSelectedIds);
		if (selectedIdps != null && !selectedIdps.isEmpty()) {
			List<?> list = QueryUtil.executeQuery(
					"select distinct reportNodeId from "
							+ Idp.class.getSimpleName(), null,
					new FilterParams("id", selectedIdps));
			if (!list.isEmpty()) {
				List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class,
						null, new FilterParams("macAddress", list),
						PagingImpl.MAX_RESULTS);
				BeTopoModuleUtil.sendIDPQuery(hiveAps);
			}
			// clear selection or the previous selected id is still in the list.
			setAllSelectedIds(null);
		}
	}

	private void mitigateOperation(Set<Long> allSelectedIds, List<String> macs,
			boolean exec) {
		// List<Long> selectedIdps = getSelectedIdpIds(allSelectedIds);
		if (allSelectedIds != null && !allSelectedIds.isEmpty()) {
			List<Idp> aps = QueryUtil.executeQuery(Idp.class, null,
					new FilterParams("id", allSelectedIds), null,
					HmBeTopoUtil.getIdpEventListener().getIdpEventProcessor());

			List<String> apIdpMacs = new ArrayList<String>();
			List<String> apMacs = new ArrayList<String>();
			if (exec) {
				for (Idp idp : aps) {
					apIdpMacs.add(idp.getReportNodeId());
				}
				if (macs != null && !macs.isEmpty())
					for (String ap : macs) {
						apMacs.add(ap);
					}
			}

			if (!aps.isEmpty()) {
				Map<String, List<AhLatestXif>> map_inter = new HashMap<String, List<AhLatestXif>>();
				if (exec) {
					// get interfaces
					String where = "apMac in (:s1) and ifName in (:s2)";
					List<String> names = new ArrayList<String>(2);
					names.add("wifi0");
					names.add("wifi1");
					apIdpMacs.addAll(apMacs);
					Object[] values = new Object[] { apIdpMacs, names };
					List<AhLatestXif> interfaces = QueryUtil.executeQuery(
							AhLatestXif.class, null, new FilterParams(where,
									values));
					for (AhLatestXif inter : interfaces) {
						String apMac = inter.getApMac();
						List<AhLatestXif> inters = map_inter.get(apMac);
						if (null == inters) {
							inters = new ArrayList<AhLatestXif>();
							map_inter.put(apMac, inters);
						}
						inters.add(inter);
					}
				}

				IdpPagingCache.queryIdsInfo(aps);
				for (Idp idp : aps) {
					short mode = idp.getMode();
					if (!idp.isMitigated() && !exec)
						continue;

					String bssid = idp.getIfMacAddress();
					String nodeId = idp.getReportNodeId();
					Byte index = idp.getIfIndex();
					Short channel = idp.getChannel();

					if (mode == IdsPolicy.MITIGATION_MODE_SEMIAUTO) {
						apMacs.clear();
						apMacs.add(nodeId);
					} else if (mode == IdsPolicy.MITIGATION_MODE_AUTO) {
						continue;
					} else {
						apMacs.clear();
						if (exec) {
							if (macs != null && !macs.isEmpty())
								for (String ap : macs) {
									apMacs.add(ap);
								}
						}
					}

					Map<String, String> ifNames = null;
					if (!exec) {
						ifNames = new HashMap<String, String>();
						for (IdpAp ap : idp.getMitiAps()) {
							ifNames.put(ap.getMitiMac(), ap.getIfName());
							apMacs.add(ap.getMitiMac());
						}
						if (apMacs.isEmpty())
							apMacs.add(nodeId);
					}
					for (String mac : apMacs) {
						String ifName = "wifi0";// by default;
						if (exec || mode == IdsPolicy.MITIGATION_MODE_MANUAL) {
							List<AhLatestXif> inters = map_inter.get(mac);
							boolean xifExist = false;
							// 1) using xif to find the name
							if (null != inters) {
								for (AhLatestXif xif : inters) {
									if (xif.getIfIndex() == index) {
										ifName = xif.getIfName();
										xifExist = true;
										break;
									}
								}
							}
							if (!xifExist) {
								// 2) using channel number to find the name
								if (channel >= 15) {
									ifName = "wifi1";
								}
							}
						} else {
							ifName = ifNames.get(mac);
						}

						// generate event;
						IdpMitigationEvent event = new IdpMitigationEvent(
								bssid, nodeId, ifName, exec, idp, mac);
						HmBeTopoUtil.getIdpEventListener().addIdpEvent(event);
					}
				}
			}
		}
	}

	private void fetchMitigateClients(String bssid, String nodeId)
			throws JSONException {
		jsonObject = new JSONObject();
		if (null == bssid || null == nodeId) {
			log.error("fetchMitigateClients",
					"Invalid parameter to fetch mitigate clients. bssid:"
							+ bssid + ", nodeId:" + nodeId);
			return;
		}
		// get IDP entry
		String where = "ifMacAddress = :s1 and reportNodeId = :s2";
		Object[] values = new Object[] { bssid, nodeId };
		List<Idp> idps = QueryUtil.executeQuery(Idp.class, null,
				new FilterParams(where, values));
		if (idps.isEmpty()) {
			log.error("fetchMitigateClients", "Cannot find idp entry.bssid:"
					+ bssid + ", nodeId:" + nodeId);
			return;
		}
		SimpleHiveAp hiveAp = CacheMgmt.getInstance().getSimpleHiveAp(nodeId);
		if (null == hiveAp) {
			log.error("fetchMitigateClients",
					"Cannot show mitigation for HiveAP:" + nodeId
							+ ", since it cannot be found in cache.");
			return;
		}
		Idp idp = idps.get(0);
		// get interfaces
		List<AhLatestXif> interfaces = QueryUtil.executeQuery(
				AhLatestXif.class, null, new FilterParams("apMac", nodeId));
		String wifix = "wifi0"; // default value;
		for (AhLatestXif xif : interfaces) {
			if (xif.getIfIndex() == idp.getIfIndex()) {
				wifix = xif.getIfName();
				break;
			}
		}
		String exeCli = AhCliFactory.getMitigateClients(wifix, bssid);
		log.info("fetchMitigateClients", "show mitigation cli:" + exeCli);

		try {
			BeCliEvent c_event = new BeCliEvent();
			c_event.setSimpleHiveAp(hiveAp);
			c_event.setClis(new String[] { exeCli });
			c_event.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			c_event.buildPacket();
			BeCommunicationEvent response = HmBeCommunicationUtil
					.sendSyncRequest(c_event, 35);
			if (response.getMsgType() == BeCommunicationConstant.MESSAGETYPE_CLIRSP) {
				log.info("fetchMitigateClients",
						"Receive response, show mitigation failed for HiveAP:"
								+ nodeId);
			} else if (response.getMsgType() == BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT) {
				response.parsePacket();
				BeCapwapCliResultEvent cliResult = (BeCapwapCliResultEvent) response;
				if (!cliResult.isCliSuccessful()) {
					log.info("fetchMitigateClients",
							"Receive unsuccess result event, show mitigation failed for HiveAP:"
									+ nodeId);
				} else {
					String result = cliResult.getCliSucceedMessage();
					result = null == result ? "" : result;
					jsonObject.put("v", result);
				}
			}
		} catch (BeCommunicationEncodeException e) {
			log.error("fetchMitigateClients",
					"build show mitigation request failed.", e);
			jsonObject.put("v", NmsUtil.getOEMCustomer().getNmsName()
					+ " was unable to process your request.");
		} catch (BeCommunicationDecodeException e) {
			log.error("fetchMitigateClients",
					"parse show mitigation response failed.", e);
			jsonObject.put("v", NmsUtil.getOEMCustomer().getNmsName()
					+ " was unable to process the response.");
		}
	}

	public static final short LISTVIEW_FRIENDLYAP = 1;

	public static final short LISTVIEW_ROGUEAP = 2;

	public static final short LISTVIEW_ROGUECLIENT = 3;

	private short listViewType;

	public void setListViewType(short listViewType) {
		this.listViewType = listViewType;
	}

	private String getIdpListName(short type) {
		if (IdpAction.LISTVIEW_FRIENDLYAP == type) {
			return "Friendly AP Items";
		} else if (IdpAction.LISTVIEW_ROGUEAP == type) {
			return "Rogue AP Items";
		} else if (IdpAction.LISTVIEW_ROGUECLIENT == type) {
			return "Rogue Client Items";
		} else {
			return "Unknown APs Items";
		}
	}

	private List<Long> getSelectedIdpIds(Set<Long> allSelectedIds) {
		if (allItemsSelected) {
			if (domainId == null) {
				domainId = QueryUtil.getDependentDomainFilter(userContext);
			}
			List<?> ids = QueryUtil.executeQuery(
					"select id from " + boClass.getSimpleName(), null,
					filterParams, domainId);
			List<Long> selectedIds = new ArrayList<Long>(ids.size());

			for (Object obj : ids) {
				selectedIds.add((Long) obj);
			}
			return selectedIds;
		} else {
			List<Long> sIds = new ArrayList<Long>();
			if (null == allSelectedIds || allSelectedIds.isEmpty()) {
				return sIds;
			}
			
			// for bug fix: 16678, all rouge/friendly APs with same mac should be removed even select one item in detail mode
			// original: only summary mode use this section of code.
			List<?> list = QueryUtil
					.executeQuery(
							"select distinct ifMacAddress from "
									+ Idp.class.getSimpleName(), null,
							new FilterParams("id", allSelectedIds),
							domainId);
			if (!list.isEmpty()) {
				FilterParams filter;
				if (null != filterParams && null != filterParams.getWhere()) {
					String newWhere = filterParams.getWhere()
							+ " and ifMacAddress in (:s"
							+ (filterParams.getBindings().length + 1) + ")";
					Object[] binds = filterParams.getBindings();
					Object[] newBinds = new Object[binds.length + 1];
					System.arraycopy(binds, 0, newBinds, 0, binds.length);
					newBinds[binds.length] = list;
					filter = new FilterParams(newWhere, newBinds);
				} else {
					filter = new FilterParams("ifMacAddress", list);
				}
				List<?> ids = QueryUtil.executeQuery("select id from "
						+ boClass.getSimpleName(), null, filter, domainId);
				for (Object obj : ids) {
					sIds.add((Long) obj);
				}
			}
			// for bug fix: 16678, end
			
			return sIds;
		}
	}

	public List<CheckItem> getRssiThresholdValues() {
		return MapsAction.getRssiThresholdValues();
	}

	protected JSONArray jsonArray;

	protected JSONObject jsonObject;

	public String getJSONString() {
		if (jsonArray == null) {
			log.debug("getJSONString", "JSON string: " + jsonObject.toString());
			return jsonObject.toString();
		} else {
			log.debug("getJSONString", "JSON string: " + jsonArray.toString());
			return jsonArray.toString();
		}
	}

	public static final String IDP_CURRENT_FILTER = "idp_current_filter";

	private int interval;

	private int threshold;

	private String bssid;

	private String nodeId;

	private String parentBssid;

	private String filter;

	private String filterName;

	private String bssidString;

	// private List<String> filterList;

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public void setBssidString(String bssidString) {
		this.bssidString = bssidString;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public String getBssid() {
		return bssid;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public void setParentBssid(String parentBssid) {
		this.parentBssid = parentBssid;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public String getFilterName() {
		List<String> filter = getCurrentFilterInSession();
		if (null != filter && filter.size() > 1) {
			filterName = filter.get(1);
		}
		return filterName;
	}

	public List<String> getFilterList() {
		return getFilterListFromSession(listType);
	}

	public String getFilter() {
		if (null == filter) {
			filter = (String) MgrUtil.getSessionAttribute(IDP_CURRENT_FILTER);
		}
		return filter;
	}

	private void doMitigateOperationFromGui(boolean exec) {
		Set<Long> allSelectedIds = getAllSelectedIds();
		List<Long> selectedIdps = getSelectedIdpIds(allSelectedIds);

		List<Long> semiIdpIds = new ArrayList<Long>();
		Map<Long, List<String>> manualStopIdpIds = new HashMap<Long, List<String>>();
		List<String> manualMacs = new ArrayList<String>();
		List<MitigateManualModeList> manualModeApList = new ArrayList<MitigateManualModeList>();

		overloop: for (Long idpId : selectedIdps) {

			// get the bssid and reporting ap mac from idp table
			List<?> list = QueryUtil.executeQuery(
					"select ifMacAddress, reportNodeId, channel from "
							+ Idp.class.getSimpleName(), null,
					new FilterParams("id", idpId));

			if (list.size() == 1) {
				Object[] attributes = (Object[]) list.get(0);

				String bssid = (String) attributes[0];
				String nodeId = (String) attributes[1];
				// Byte index = (Byte) attributes[2];
				Short channel = (Short) attributes[2];

				String mitiModeSql = "select A.mitigationmode from (ids_policy A left join config_template B on A.id = B.ids_policy_id) where B.id = ("
						+ "select template_id from hive_ap where macaddress = '"
						+ nodeId + "')";

				// get the mitigation mode from ids policy table
				List<?> modeList = QueryUtil.executeNativeQuery(mitiModeSql);
				int mitiMode = IdsPolicy.MITIGATION_MODE_SEMIAUTO;
				if (null != modeList && !modeList.isEmpty()) {
					if (null != modeList.get(0)) {
						mitiMode = (Short) (modeList.get(0));
					}
				}
				if (IdsPolicy.MITIGATION_MODE_MANUAL == mitiMode) {
					// start mitigation
					if (exec) {

						for (MitigateManualModeList mitiList : manualModeApList) {
							if (mitiList.getRogueApMac().equals(bssid)) {
								for (MitigateManualModeItem mitiItem : mitiList
										.getSameHiveApList()) {
									if (mitiItem.getApMac().equals(nodeId)) {
										continue overloop;
									}
								}
							}
						}

						MitigateManualModeList oneMiti = new MitigateManualModeList();

						String sql = "select A.hostname, A.macaddress, A.id, B.ifindex, B.rssi from (hive_ap A left join idp B on A.macaddress = B.reportnodeid)"
								+ " where A.managestatus = 1 and A.template_id in (select id from config_template where hive_profile_id = "
								+ "(select hive_profile_id from config_template where id = (select template_id from hive_ap where macaddress = '"
								+ nodeId
								+ "'))) and B.ifmacaddress = '"
								+ bssid + "' order by B.rssi";

						// get all the ap with the reporting ap in the same hive
						List<?> apList = QueryUtil.executeNativeQuery(sql);

						oneMiti.setRogueApMac(bssid);

						if (bssid != null) {
							String strOui = bssid.substring(0, 6).toUpperCase();
							if (AhConstantUtil.getMacOuiComName(strOui) != null) {
								oneMiti.setRogueApVendor(AhConstantUtil
										.getMacOuiComName(strOui));
							}
						}

						if (!apList.isEmpty()) {

							List<MitigateManualModeItem> sameHiveApList = new ArrayList<MitigateManualModeItem>();

							for (Object obj : apList) {
								Object[] apInfo = (Object[]) obj;

								String hostName = (String) apInfo[0];
								String apMac = (String) apInfo[1];
								BigInteger apId = (BigInteger) apInfo[2];
								Short ifindex = (Short) apInfo[3];
								Short rssi = (Short) apInfo[4];

								MitigateManualModeItem oneApInfo = new MitigateManualModeItem();

								oneApInfo.setHostName(hostName);
								oneApInfo.setIdpId(idpId);
								oneApInfo.setApMac(apMac);
								oneApInfo.setApId(apId.longValue());

								List<?> rssilist = QueryUtil
										.executeQuery(
												"select radioChannel from "
														+ AhLatestRadioAttribute.class
																.getSimpleName(),
												new SortParams("radioChannel"),
												new FilterParams(
														"apMac = :s1 AND ifIndex = :s2",
														new Object[] {
																apMac,
																Integer.valueOf(ifindex) }));

								if (!rssilist.isEmpty()) {
									long radioChannel = (Long) (rssilist.get(0));

									oneApInfo
											.setSameChannel(channel == radioChannel ? "Yes"
													: "No");

									oneApInfo.setRssiStr((rssi - 95) + " dBm");

									// set active client count;
									SimpleHiveAp s_hiveAp = CacheMgmt
											.getInstance().getSimpleHiveAp(
													apMac);
									if (null != s_hiveAp) {
										oneApInfo.setClientCount(s_hiveAp
												.getActiveClientCount());
									}

									sameHiveApList.add(oneApInfo);
								} else {
								}
							}
							oneMiti.setSameHiveApList(sameHiveApList);
						}

						manualModeApList.add(oneMiti);

						// stop mitigation
					} else {
						manualStopIdpIds.put(idpId, manualMacs);
					}
					// semi auto do as before
				} else if (IdsPolicy.MITIGATION_MODE_SEMIAUTO == mitiMode) {
					semiIdpIds.add(idpId);
				}
			}
		}
		if (!semiIdpIds.isEmpty()) {
			mitigateOperationForSemiAutoMode(exec, semiIdpIds);

			// clear selection or the previous selected id is still in the list.
			setAllSelectedIds(null);

			addActionMessage(MgrUtil.getUserMessage("message.request.submit"));
		}
		if (!manualStopIdpIds.isEmpty()) {
			mitigateOperationForManualMode(false, manualStopIdpIds);

			// clear selection or the previous selected id is still in the list.
			setAllSelectedIds(null);

			addActionMessage(MgrUtil.getUserMessage("message.request.submit"));
		}

		if (!manualModeApList.isEmpty()) {
			// sort same channel or not
			for (MitigateManualModeList mmml : manualModeApList) {
				Collections.sort(mmml.getSameHiveApList(),
						new Comparator<MitigateManualModeItem>() {
							public int compare(MitigateManualModeItem item1,
									MitigateManualModeItem item2) {
								String channel1 = item1.getSameChannel();
								String channel2 = item2.getSameChannel();
								return channel2.compareTo(channel1);
							}
						});
			}
			manualModeAp = manualModeApList.get(0);
			manualModeApList.remove(0);

			if (!manualModeApList.isEmpty()) {
				MgrUtil.setSessionAttribute(MONITOR_MANUAL_ROGUE_AP_MITIGATE,
						manualModeApList);
			}
		}
	}

	private MitigateManualModeList manualModeAp;

	private List<String> selectedIdpAps = new ArrayList<String>();

	public List<String> getSelectedIdpAps() {
		return selectedIdpAps;
	}

	public void setSelectedIdpAps(List<String> selectedIdpAps) {
		this.selectedIdpAps = selectedIdpAps;
	}

	/**
	 * Do mitigate for semi auto mode rogue aps
	 * 
	 * @param exec
	 *            : if start; semiIdpIds : idp ids
	 */
	private void mitigateOperationForSemiAutoMode(boolean exec,
			List<Long> semiIdpIds) {
		Set<Long> idps = new HashSet<Long>();
		for (Long id : semiIdpIds)
			idps.add(id);

		mitigateOperation(idps, null, exec);
	}

	/**
	 * Do mitigate for manual mode rogue aps
	 * 
	 * @param exec
	 *            : if start; manualIds : Long:idp id, List<String>:mitigate AP
	 *            macs
	 */
	private void mitigateOperationForManualMode(boolean exec,
			Map<Long, List<String>> macs) {
		if (macs == null)
			return;

		Set<Long> keys = macs.keySet();
		Set<Long> idps = new HashSet<Long>();
		for (Long key : keys) {
			List<String> apMacs = macs.get(key);
			if (exec && apMacs == null)
				continue;
			idps.clear();
			idps.add(key);

			mitigateOperation(idps, apMacs, exec);
		}
	}

	public MitigateManualModeList getManualModeAp() {
		return manualModeAp;
	}

	public void setManualModeAp(MitigateManualModeList manualModeAp) {
		this.manualModeAp = manualModeAp;
	}

	public String getFilterBssidText() {
		if (L2_FEATURE_ROGUECLIENT.equals(listType)) {
			return getText("monitor.rogueClient.clientMac");
		} else {
			return getText("monitor.hiveAp.report.ifBSSID");
		}
	}

}