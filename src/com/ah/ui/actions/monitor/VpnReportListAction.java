package com.ah.ui.actions.monitor;

/*
 * @author Fisher
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.json.JSONObject;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.DebugUtil;
import com.ah.be.performance.AhPerformanceScheduleModule;
import com.ah.be.performance.BeInterfaceReportProcessor;
import com.ah.be.performance.BePerformScheduleImpl;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.VpnServiceCredential;
import com.ah.bo.performance.AhPortAvailability;
import com.ah.bo.performance.AhReport;
import com.ah.bo.performance.AhStatsAvailabilityHigh;
import com.ah.bo.performance.AhStatsAvailabilityLow;
import com.ah.bo.performance.AhStatsLatencyHigh;
import com.ah.bo.performance.AhStatsLatencyLow;
import com.ah.bo.performance.AhStatsThroughputHigh;
import com.ah.bo.performance.AhStatsThroughputLow;
import com.ah.bo.performance.AhStatsVpnStatusHigh;
import com.ah.bo.performance.AhStatsVpnStatusLow;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.Navigation;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.datetime.AhDateTimeUtil;

public class VpnReportListAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;
	public static final String REPORT_LIST_TYPE = "reportListType";
	public static final String REPORT_HIVEAP_NAME = "reportHiveApName";
	public static final String REPORT_HIVEAP_LST = "reportHiveApList";
	public static final String REPORT_TUNNEL_NAME = "reportTunnelName";
	public static final String REPORT_TUNNELNAME_MAP = "reportTunnelNameMap";
	public static final String REPORT_TUNNELNAMEIP_MAP = "reportTunnelNameIPMap";
	public static final String REPORT_TUNNELSERVERIP_NAME_MAP = "reportTunnelServerIPNameMap";
	public static final String REPORT_HIVEAPNAME_MAC_MAP = "reportHiveApNameMacMap";
	public static final String REPORT_HIVEAPNAME_TUNNELCOUNT_MAP = "reportHiveApNameTunnelCountMap";
	
	private String listType;
	
	private String buttonType;

	public String getListType() {
		return listType;
	}

	public void setListType(String listType) {
		this.listType = listType;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_REPORT = 2;
	
	public static final int COLUMN_TYPE = 3;

	public static final int COLUMN_EMAILADDRESS = 4;

	public static final int COLUMN_NEXTSCHEDULE = 5;

	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 * @author Joseph Chen
	 */
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "report.reportList.name";
			break;
		case COLUMN_REPORT:
			code = "report.reportList.title.type";
			break;
		case COLUMN_TYPE:
			code = "report.reportList.excuteType";
			break;
		case COLUMN_EMAILADDRESS:
			code = "report.reportList.emailAddress";
			break;
		case COLUMN_NEXTSCHEDULE:
			code = "report.reportList.startTime";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_REPORT));
		columns.add(new HmTableColumn(COLUMN_TYPE));
		columns.add(new HmTableColumn(COLUMN_EMAILADDRESS));
		columns.add(new HmTableColumn(COLUMN_NEXTSCHEDULE));
		return columns;
	}

	public String execute() throws Exception {
		if (listType == null || listType.equals("")) {
			if (MgrUtil.getSessionAttribute(REPORT_LIST_TYPE)!=null) {
				listType = MgrUtil.getSessionAttribute(REPORT_LIST_TYPE).toString();
			} else {
				listType =Navigation.L2_FEATURE_REPORT_WIREDBRREPORTS;
			}
		} else {
			MgrUtil.setSessionAttribute(REPORT_LIST_TYPE, listType);
		}
		if (listType.equalsIgnoreCase(Navigation.L2_FEATURE_REPORT_WIREDBRREPORTS)){
			List<String> filter = new ArrayList<String>();
			filter.add(L2_FEATURE_REPORT_WANAVAILABILITY);
			filter.add(L2_FEATURE_REPORT_WANTHROUGHPUT);
			filterParams = new FilterParams("reportType", filter);
		} else if (listType.equalsIgnoreCase(L2_FEATURE_REPORT_WIREDCVGREPORTS)){
				List<String> filter = new ArrayList<String>();
				filter.add(L2_FEATURE_REPORT_GWWANAVAILABILITY);
				filter.add(L2_FEATURE_REPORT_GWWANTHROUGHPUT);
				filterParams = new FilterParams("reportType", filter);
		} else if (listType.equalsIgnoreCase(L2_FEATURE_REPORT_VPNBRREPORTS)){
			List<String> filter = new ArrayList<String>();
			filter.add(L2_FEATURE_REPORT_VPNAVAILABILITY);
			filter.add(L2_FEATURE_REPORT_VPNTHROUGHPUT);
			filter.add(L2_FEATURE_REPORT_VPNLATENCY);
			filterParams = new FilterParams("reportType", filter);
		} else if (listType.equalsIgnoreCase(L2_FEATURE_REPORT_VPNCVGREPORTS)){
			List<String> filter = new ArrayList<String>();
			filter.add(L2_FEATURE_REPORT_GWVPNAVAILABILITY);
			filterParams = new FilterParams("reportType", filter);
		} else {
			filterParams = new FilterParams("reportType", listType);
		}

		setSelectedL1Feature(Navigation.L1_FEATURE_MONITOR);
		if (listType.equalsIgnoreCase(L2_FEATURE_REPORT_VPNAVAILABILITY)||
				listType.equalsIgnoreCase(L2_FEATURE_REPORT_VPNTHROUGHPUT) ||
				listType.equalsIgnoreCase(L2_FEATURE_REPORT_VPNLATENCY)){
			setSelectedL2Feature(Navigation.L2_FEATURE_REPORT_VPNBRREPORTS);
		} else if (listType.equalsIgnoreCase(L2_FEATURE_REPORT_WANAVAILABILITY) ||
				listType.equalsIgnoreCase(L2_FEATURE_REPORT_WANTHROUGHPUT)){
			setSelectedL2Feature(Navigation.L2_FEATURE_REPORT_WIREDBRREPORTS);
		} else if (listType.equalsIgnoreCase(L2_FEATURE_REPORT_GWVPNAVAILABILITY)){
			setSelectedL2Feature(Navigation.L2_FEATURE_REPORT_VPNCVGREPORTS);
		} else if (listType.equalsIgnoreCase(L2_FEATURE_REPORT_GWWANAVAILABILITY) ||
				listType.equalsIgnoreCase(L2_FEATURE_REPORT_GWWANTHROUGHPUT)){
			setSelectedL2Feature(Navigation.L2_FEATURE_REPORT_WIREDCVGREPORTS);
		} else {
			setSelectedL2Feature(listType);
		}
		
		resetPermission();
		tz = getUserTimeZone();
		
		try {
			if ("test".equals(operation)) {
				addTestData();
				baseOperation();
				clearOldSession();
				String retStr =  prepareBoList();
				if (page!=null){
					TimeZone myTimeZone = TimeZone.getTimeZone(userContext.getTimeZone()); 
					for(Object myObject:page){
						AhReport tmpClass = (AhReport)myObject;
						tmpClass.setTz(myTimeZone);
					}
				}
				return retStr;
			}
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(listType + " > New")) {
					return getLstForward();
				}
				setSessionDataSource(new AhReport());
				if (listType.equalsIgnoreCase(Navigation.L2_FEATURE_REPORT_VPNBRREPORTS)){
					getDataSource().setReportType(L2_FEATURE_REPORT_VPNAVAILABILITY);
				} else if (listType.equalsIgnoreCase(Navigation.L2_FEATURE_REPORT_VPNCVGREPORTS)){
					getDataSource().setReportType(L2_FEATURE_REPORT_GWVPNAVAILABILITY);
				} else if (listType.equalsIgnoreCase(Navigation.L2_FEATURE_REPORT_WIREDBRREPORTS)){
					getDataSource().setReportType(L2_FEATURE_REPORT_WANAVAILABILITY);
				} else if (listType.equalsIgnoreCase(Navigation.L2_FEATURE_REPORT_WIREDCVGREPORTS)){
					getDataSource().setReportType(L2_FEATURE_REPORT_GWWANAVAILABILITY);
				} else {
					getDataSource().setReportType(listType);
				}
				getDataSource().setOwner(getDomain());
				clearOldSession();
				return getDataSource().getReportType();
			} else if ("create".equals(operation)) {
				if (checkNameExists("name=:s1 and reportType=:s2", new Object[] {
						getDataSource().getName(), getDataSource().getReportType() })) {
					return getDataSource().getReportType();
				}
				saveGuiValue();
				clearOldSession();
				String retString =  createBo();
				if (page!=null){
					TimeZone myTimeZone = TimeZone.getTimeZone(userContext.getTimeZone()); 
					for(Object myObject:page){
						AhReport tmpClass = (AhReport)myObject;
						tmpClass.setTz(myTimeZone);
					}
				}
				return retString;
			} else if ("edit".equals(operation)) {
				editBo();
				if (dataSource != null) {
					if (getDataSource().getId() != null) {
						findBoById(AhReport.class, getDataSource().getId(), this);
					}
					setSessionDataSource(dataSource);
				}
				prepareGuiSetting();
				clearOldSession();
				return getDataSource().getReportType();
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				AhReport ahReport = (AhReport) findBoById(boClass, cloneId);
				ahReport.setId(null);
				ahReport.setOwner(getDomain());
				ahReport.setDefaultFlag(false);
				ahReport.setName("");
				setSessionDataSource(ahReport);
				prepareGuiSetting();
				addLstTitle(listType + " > New");
				clearOldSession();
				return getDataSource().getReportType();
			} else if ("viewTunnel".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("id", "reportTunnelName");
				jsonObject.put("v", ((Map<String, List<String>>) MgrUtil
						.getSessionAttribute(REPORT_TUNNELNAME_MAP)).get(selectHiveAPName));
				return "json";
			} else if ("run".equals(operation)) {
				if (getDataSource() == null) {
					long cloneId = getSelectedIds().get(0);
					AhReport ahReport = (AhReport) findBoById(boClass, cloneId, this);
					setId(cloneId);
					setSessionDataSource(ahReport);
				} else {
					saveGuiValue();
				}
				getDataSource().setReportStartTime(System.currentTimeMillis());
				prepareGuiSetting();
				if (buttonType!=null && !buttonType.equals("")){
					getDataSource().setReportType(buttonType);
				}
				showReportTab = true;
				prepareFlash();
				prepareSelectDevice();
				if (hiveApNameMacMap.get(reportAPName)!=null) {
				
				long  retLong = AhAppContainer.HmBe.getPerformModule().getBeInterfaceReportProcessor().fetchReportData(hiveApNameMacMap.get(reportAPName), getDataSource().getReportPeriod());
					if (retLong!=BeInterfaceReportProcessor.QUERY_OK && retLong!=BeInterfaceReportProcessor.QUERY_DONE){
						addActionError(MgrUtil.getUserMessage("action.error.fetch.report") + getErrrorCodeString(retLong));
					}
				}
				generalCSVAndEmail();
				return getDataSource().getReportType();
			} else if ("getFlashData".equals(operation)) {
				reportAPName = MgrUtil.getSessionAttribute(REPORT_HIVEAP_NAME).toString();
				if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNAVAILABILITY)
						|| getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNTHROUGHPUT)
						|| getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNLATENCY)) {
					reportTunnelName = MgrUtil.getSessionAttribute(REPORT_TUNNEL_NAME).toString();
				}
				initFlashData();
				
				long diffTimer = System.currentTimeMillis() - getDataSource().getReportStartTime();
				String diffTimerStr = diffTimer > 1000 ? diffTimer/1000 + "s" : diffTimer + "ms";
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.run.report") 
						+ MgrUtil.getUserMessage("report.reportList.name") + " (" + getDataSource().getName() + "),"
						+ MgrUtil.getUserMessage("report.reportList.title.type") + " (" + getDataSource().getReportTypeShowInGUI() + "),"
						+ MgrUtil.getUserMessage("report.reportList.reportPeriod") + " (" + getDataSource().getReportPeriodVpnString() + "),"
						+ MgrUtil.getUserMessage("report.reportList.excuteType") + " (" + getDataSource().getExcuteTypeString() + ")"
						+ ".  "+MgrUtil.getUserMessage("hm.audit.log.time.used") + diffTimerStr);
				
				return "vpnReportListData";

			} else if ("createDownloadData".equals(operation)) {
				setId(getDataSource().getId());
				if (!getUpdateDisabled().equals("")) {
					jsonObject = new JSONObject();
					jsonObject.put("success", false);
					
					jsonObject.put("eword","User '" + getUserContext().getUserName()
							+ "' does not have WRITE access to object '"
							+ getDataSource().getLabel() + "'.");
					return "json";
				}
				getDataSource().setReportStartTime(System.currentTimeMillis());
				
				reportAPName = MgrUtil.getSessionAttribute(REPORT_HIVEAP_NAME).toString();
				if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNAVAILABILITY)
						|| getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNTHROUGHPUT)
						|| getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNLATENCY)) {
					reportTunnelName = MgrUtil.getSessionAttribute(REPORT_TUNNEL_NAME).toString();
				}
//				initFlashData();
				boolean isSucc = generalCurrentCvsFile();
				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				
				long diffTimer = System.currentTimeMillis() - getDataSource().getReportStartTime();
				String diffTimerStr = diffTimer > 1000 ? diffTimer/1000 + "s" : diffTimer + "ms";
				generateAuditLog(HmAuditLog.STATUS_SUCCESS,  MgrUtil.getUserMessage("hm.audit.log.run.report") 
						+ MgrUtil.getUserMessage("report.reportList.name") + " (" + getDataSource().getName() + "),"
						+ MgrUtil.getUserMessage("report.reportList.title.type") + " (" + getDataSource().getReportTypeShowInGUI() + "),"
						+ MgrUtil.getUserMessage("report.reportList.reportPeriod") + " (" + getDataSource().getReportPeriodVpnString() + "),"
						+ MgrUtil.getUserMessage("report.reportList.excuteType") + " (" + getDataSource().getExcuteTypeString() + ")"
						+ ".  "+MgrUtil.getUserMessage("hm.audit.log.time.used") + diffTimerStr);
				
				return "json";
			} else if ("download".equals(operation)) {
				setId(getDataSource().getId());
				if (!getUpdateDisabled().equals("")) {
					addActionError("User '" + getUserContext().getUserName()
							+ "' does not have WRITE access to object '"
							+ getDataSource().getLabel() + "'.");
					return getDataSource().getReportType();
				}
				File file = new File(getInputPath());
				if (!file.exists()) {
					// commonly, logic should not come here
					addActionError(MgrUtil.getUserMessage("action.error.cannot.find.file"));
					// generateAuditLog(HmAuditLog.STATUS_FAILURE,
					// "Save support bundle");
					return getDataSource().getReportType();
				}
				return "download";
			} else if ("update".equals(operation)) {
				List<?> boIds = QueryUtil.executeQuery("select id from "
						+ boClass.getSimpleName(), null,
						new FilterParams("name=:s1 and reportType=:s2", 
						new Object[] {getDataSource().getName(), getDataSource().getReportType()})
						, domainId);
				if (!boIds.isEmpty() && !String.valueOf(boIds.get(0)).equals(getDataSource().getId().toString())) {
					addActionError(MgrUtil.getUserMessage("error.objectExists",
							getDataSource().getName()));
					return getDataSource().getReportType();
				}
				saveGuiValue();
				clearOldSession();
				if (id == null)
					setId(getDataSource().getId());
				String retString = updateBo();
				if (page!=null){
					TimeZone myTimeZone = TimeZone.getTimeZone(userContext.getTimeZone()); 
					for(Object myObject:page){
						AhReport tmpClass = (AhReport)myObject;
						tmpClass.setTz(myTimeZone);
					}
				}
				return retString;
			} else {
				baseOperation();
				clearOldSession();
				String retStr =  prepareBoList();
				if (page!=null){
					TimeZone myTimeZone = TimeZone.getTimeZone(userContext.getTimeZone()); 
					for(Object myObject:page){
						AhReport tmpClass = (AhReport)myObject;
						tmpClass.setTz(myTimeZone);
					}
				}
				return retStr;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	
	public static TimeZone tz;

	public void prepare() throws Exception {
		super.prepare();
		setDataSource(AhReport.class);
		enableSorting();
		// default sorting
		if (sortParams.getOrderBy().equals("id")) {
			sortParams.setPrimaryOrderBy("defaultFlag");
			sortParams.setPrimaryAscending(false);
		}
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_REPORT;
	}
	
	public boolean getHasPredefinedReport() {
		boolean has = false;
		if (null != getPage()) {
			for (Object object : getPage()) {
				AhReport item = (AhReport) object;
				if (item.getDefaultFlag()) {
					has = true;
					break;
				}
			}
		}
		return has;
	}

	public static void clearOldSession() {
		MgrUtil.removeSessionAttribute(REPORT_HIVEAP_NAME);
		MgrUtil.removeSessionAttribute(REPORT_HIVEAP_LST);
		MgrUtil.removeSessionAttribute(REPORT_TUNNEL_NAME);
		MgrUtil.removeSessionAttribute(REPORT_TUNNELNAME_MAP);
		MgrUtil.removeSessionAttribute(REPORT_TUNNELNAMEIP_MAP);
		MgrUtil.removeSessionAttribute(REPORT_TUNNELSERVERIP_NAME_MAP);
		MgrUtil.removeSessionAttribute(REPORT_HIVEAPNAME_MAC_MAP);
		MgrUtil.removeSessionAttribute(REPORT_HIVEAPNAME_TUNNELCOUNT_MAP);
	}

	public String getErrrorCodeString(long errorCode) {
		if (errorCode == BeInterfaceReportProcessor.ERROR_CLI) {
			return "Push cli error.";
		} else if (errorCode == BeInterfaceReportProcessor.ERROR_DB) {
			return "Database operation error.";
		} else if (errorCode == BeInterfaceReportProcessor.ERROR_FILE) {
			return "File operation error.";
		} else if (errorCode == BeInterfaceReportProcessor.ERROR_INIT) {
			return "Directory create error.";
		} else if (errorCode == BeInterfaceReportProcessor.ERROR_NODEVICE) {
			return "Cannot find device.";
		} else if (errorCode == BeInterfaceReportProcessor.ERROR_NOTMANAGED) {
			return "Device is not in management.";
		} else if (errorCode == BeInterfaceReportProcessor.ERROR_DISCONNECTED) {
			return "Device is disconnected.";
		} else {
			return "Unknow error.";
		}
	}
	
	public void saveGuiValue() throws Exception {
		if (startTime != null && !startTime.equals("")) {
			String datetime[] = startTime.split("-");
			Calendar calendar = Calendar.getInstance(tz);
			calendar.clear(Calendar.MINUTE);
			calendar.clear(Calendar.SECOND);
			calendar.clear(Calendar.MILLISECOND);
			calendar.set(Calendar.YEAR, Integer.parseInt(datetime[0]));
			calendar.set(Calendar.MONTH, Integer.parseInt(datetime[1]) - 1);
			calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datetime[2]));
			calendar.set(Calendar.HOUR_OF_DAY, startHour);
			// calendar.set(Calendar.MINUTE, startMin);
			getDataSource().setStartTime(calendar.getTimeInMillis());
		} else {
			getDataSource().setStartTime(0);
		}
		
		if (!getDataSource().getExcuteType().equals("2")) {
			getDataSource().setEnabledRecurrence(false);
		}

		if (!getDataSource().getEnabledEmail()) {
			getDataSource().setEmailAddress("");
		}
	}

	public void prepareGuiSetting() {
		long tmpStartTime = getDataSource().getStartTime();
		if (tmpStartTime != 0) {
			Calendar calendar = Calendar.getInstance(tz);
			calendar.setTimeInMillis(tmpStartTime);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(tz);
			startTime = formatter.format(calendar.getTime());
			startHour = calendar.get(Calendar.HOUR_OF_DAY);
		}
	}

	protected void prepareFlash() {
		swf = getDataSource().getReportType();
		application = getDataSource().getReportType();
		width = "100%";
		bgcolor = "ffffff";
		height = "530";
	}

	protected void generalCSVAndEmail() {
		if (!tabIndex.equals("1")) {
			if (getDataSource().getExcuteType().equals("1")
					&& !getDataSource().getEmailAddress().equals("")) {
				BePerformScheduleImpl bePerformScheduleImpl = new BePerformScheduleImpl();
				File tmpFileDir = new File(AhPerformanceScheduleModule.fileDirPath);
				if (!tmpFileDir.exists()) {
					tmpFileDir.mkdirs();
				}
				boolean ret = bePerformScheduleImpl.excutePerformance(getDataSource().getReportType(), getDataSource(),tz);
				if (ret) {
					AhPerformanceScheduleModule.mailCsvFile(getDataSource(), tz);
				}
			}
		}
	}
	
//	protected boolean generalClientSessionCSVFile() {
//		BePerformScheduleImpl bePerformScheduleImpl = new BePerformScheduleImpl();
//		File tmpFileDir = new File(AhPerformanceScheduleModule.fileDirPath);
//		if (!tmpFileDir.exists()) {
//			tmpFileDir.mkdirs();
//		}
//		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHH");
//		sf.setTimeZone(tz);
//		clientExportFileName = getDataSource().getReportTypeShow() + "-"  + getDataSource().getName() + "-"+   sf.format(new Date()) + ".csv";
//		HmDomain tmpDomain = getDataSource().getOwner();
//		if (tmpDomain.getDomainName().equalsIgnoreCase("global")){
//			getDataSource().setOwner(getDomain());
//		}
//		boolean ret =bePerformScheduleImpl.excutePerformance(getDataSource().getReportType(), getDataSource(),tz);
//		getDataSource().setOwner(tmpDomain);
//		return ret;
//	}

	public void prepareSelectDevice() {
		if (reportAPName != null
				&& !reportAPName.equals(MgrUtil.getUserMessage("config.optionsTransfer.none"))
				&& tabIndex.equals("1")) {
			lstHiveAPName = (Set<String>) MgrUtil.getSessionAttribute(REPORT_HIVEAP_LST);
			MgrUtil.setSessionAttribute(REPORT_HIVEAP_NAME, reportAPName);
			hiveApNameMacMap = (HashMap<String,String>)MgrUtil.getSessionAttribute(REPORT_HIVEAPNAME_MAC_MAP);
			hiveApNameTunnelCountMap = (HashMap<String,String>)MgrUtil.getSessionAttribute(REPORT_HIVEAPNAME_TUNNELCOUNT_MAP);
			
			if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNAVAILABILITY) 
					|| getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNTHROUGHPUT)
					|| getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNLATENCY)) {
				lstReportTunnelName = ((Map<String, List<String>>) MgrUtil
						.getSessionAttribute(REPORT_TUNNELNAME_MAP)).get(reportAPName);
				if (reportTunnelName==null) {
					reportTunnelName= lstReportTunnelName.get(0);
				}
				MgrUtil.setSessionAttribute(REPORT_TUNNEL_NAME, reportTunnelName);
			}
		} else if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNAVAILABILITY) 
				|| getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNTHROUGHPUT)
				|| getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNLATENCY)
				|| getDataSource().getReportType().equals(L2_FEATURE_REPORT_WANAVAILABILITY)
				|| getDataSource().getReportType().equals(L2_FEATURE_REPORT_WANTHROUGHPUT)) {

			String searchSQL = "select a.hostname, a.macAddress, d.tunnelName, d.externalIpAddress from hive_ap a, " +
								" config_template b, " + 
								" VPN_SERVICE c, " +
								"(select e.hostname as tunnelName, f.VPN_GATEWAY_SETTING_ID, f.externalIpAddress " +
								"from hive_ap e,VPN_GATEWAY_SETTING f where e.id=f.hiveApId) d " +
								"where a.template_id=b.id and b.VPN_SERVICE_ID=c.id " +
								"and c.id= d.VPN_GATEWAY_SETTING_ID " +
								"and lower(a.hostName) like '%" + 
								getDataSource().getApNameForSQL().toLowerCase() + "%'" +
								" and a.deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER + 
								" and a.manageStatus=" + HiveAp.STATUS_MANAGED + 
								" and a.simulated=false" + 
								" AND a.owner=" + getDomain().getId();
			
//			String searchSQL = "select DISTINCT hostName,hostName from HiveAp where "
//					+ " lower(hostName) like '%"
//					+ getDataSource().getApNameForSQL().toLowerCase() + "%'"
//					+ " and deviceType=" + HiveAp.Device_TYPE_BRANCH_ROUTER;
//			searchSQL = searchSQL + " AND owner.id=" + getDomain().getId();

			List<?> profiles = QueryUtil.executeNativeQuery(searchSQL);
			lstHiveAPName = new TreeSet<String>();
			lstReportTunnelName = new ArrayList<String>();
			hiveApNameMacMap = new HashMap<String, String>();
			Map<String, List<String>> tunnelNameMap = new HashMap<String, List<String>>();
			Map<String, String> tunnelNameIPMap = new HashMap<String, String>();
			Map<String, String> tunnelServerIPNameMap = new HashMap<String, String>();

			if (profiles == null || profiles.size() < 1) {
				lstHiveAPName.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				reportAPName = MgrUtil.getUserMessage("config.optionsTransfer.none");

				List<String> tmpList = new ArrayList<String>();
				tmpList.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				tunnelNameMap.put(MgrUtil.getUserMessage("config.optionsTransfer.none"), tmpList);
			} else {
				for (Object profile : profiles) {
					Object[] tmp = (Object[]) profile;
					
					if (tmp[0] != null && !tmp[0].toString().equals("")) {
						if (lstHiveAPName.size() < 1) {
							reportAPName = tmp[0].toString();
						}
						lstHiveAPName.add(tmp[0].toString());
						hiveApNameMacMap.put(tmp[0].toString(), tmp[1].toString().toLowerCase());
						if (tmp[2] != null && !tmp[2].toString().equals("") && tmp[3] != null && !tmp[3].toString().equals("")) {
							if (tunnelNameMap.get(tmp[0].toString()) == null) {
								List<String> tmpList = new ArrayList<String>();
								tmpList.add(tmp[2].toString());
								tunnelNameMap.put(tmp[0].toString(), tmpList);
							} else {
								tunnelNameMap.get(tmp[0].toString()).add(tmp[2].toString());
								if (tunnelNameMap.get(tmp[0].toString()).size()>1) {
									tunnelNameMap.get(tmp[0].toString()).add(0,"All");
								}
							}
							tunnelNameIPMap.put(tmp[0].toString()+ "-_-" + tmp[2].toString(), tmp[3].toString());
							tunnelServerIPNameMap.put(tmp[0].toString()+ "-_-" + tmp[3].toString(), tmp[2].toString());
						}
					}
				}
			}

			if (tunnelNameMap.size() < 1) {
				lstHiveAPName.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				reportAPName = MgrUtil.getUserMessage("config.optionsTransfer.none");

				List<String> tmpList = new ArrayList<String>();
				tmpList.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				tunnelNameMap.put(MgrUtil.getUserMessage("config.optionsTransfer.none"), tmpList);
			}

			lstReportTunnelName = tunnelNameMap.get(reportAPName);
			reportTunnelName = lstReportTunnelName.get(0);

			MgrUtil.setSessionAttribute(REPORT_HIVEAP_LST, lstHiveAPName);
			MgrUtil.setSessionAttribute(REPORT_HIVEAP_NAME, reportAPName);

			MgrUtil.setSessionAttribute(REPORT_TUNNELNAME_MAP, tunnelNameMap);
			MgrUtil.setSessionAttribute(REPORT_TUNNELNAMEIP_MAP, tunnelNameIPMap);
			MgrUtil.setSessionAttribute(REPORT_TUNNELSERVERIP_NAME_MAP, tunnelServerIPNameMap);
			MgrUtil.setSessionAttribute(REPORT_TUNNEL_NAME, reportTunnelName);
			MgrUtil.setSessionAttribute(REPORT_HIVEAPNAME_MAC_MAP, hiveApNameMacMap);
		} else {
			hiveApNameMacMap = new HashMap<String, String>();
			hiveApNameTunnelCountMap = new HashMap<String, String>();
			Map<String, String> hiveApIdTunnelCountMap = new HashMap<String, String>();
			
			String vpnCountSql = 
				"select c.hiveapid, count(c.hiveapid) from vpn_service a, vpn_service_credential b, " +
				"vpn_gateway_setting c where a.id=b.vpn_service_id and a.id=c.vpn_gateway_setting_id " + 
//				"and a.ipsecvpntype=4 and a.owner=" + getDomain().getId() + " group by c.hiveapid";
				"and a.ipsecvpntype=4 and b.allocatedStatus="+ VpnServiceCredential.ALLOCATED_STATUS_USED +
				" and a.owner=" + getDomain().getId() + " group by c.hiveapid";
				List<?> profilesIds = QueryUtil.executeNativeQuery(vpnCountSql);
				
			for(Object oneApObj: profilesIds){
				Object[] singleObj = (Object[])oneApObj;
				hiveApIdTunnelCountMap.put(singleObj[0].toString(), singleObj[1].toString());
			}
			
			String searchSQL = "select DISTINCT hostName,macAddress, id from " + HiveAp.class.getSimpleName() + " where "
				+ " lower(hostName) like '%"
				+ getDataSource().getApNameForSQL().toLowerCase() + "%'"
				+ " and manageStatus=" + HiveAp.STATUS_MANAGED 
				+ " and simulated=false"
				+ " and (deviceType=" + HiveAp.Device_TYPE_VPN_GATEWAY
				+ " or deviceType=" + HiveAp.Device_TYPE_VPN_BR
				+ ")";
			searchSQL = searchSQL + " AND owner.id=" + getDomain().getId();
			List<?> hiveApProfiless = QueryUtil.executeQuery(searchSQL, null, null);
		
			lstHiveAPName = new TreeSet<String>();
			if (hiveApProfiless == null || hiveApProfiless.size() < 1) {
				lstHiveAPName.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				reportAPName = MgrUtil.getUserMessage("config.optionsTransfer.none");
			} else {
				for (int i = 0; i < hiveApProfiless.size(); i++) {
					Object[] oneObjPro = (Object[])hiveApProfiless.get(i);
					lstHiveAPName.add(oneObjPro[0].toString());
					hiveApNameMacMap.put(oneObjPro[0].toString(), oneObjPro[1].toString().toLowerCase());
					String tunnelCount= hiveApIdTunnelCountMap.get(oneObjPro[2].toString());
					hiveApNameTunnelCountMap.put(oneObjPro[0].toString(), tunnelCount==null?"0":tunnelCount);
					if (i == 0) {
						reportAPName = oneObjPro[0].toString();
					}
				}
			}
			MgrUtil.setSessionAttribute(REPORT_HIVEAP_LST, lstHiveAPName);
			MgrUtil.setSessionAttribute(REPORT_HIVEAP_NAME, reportAPName);
			MgrUtil.setSessionAttribute(REPORT_HIVEAPNAME_MAC_MAP, hiveApNameMacMap);
			MgrUtil.setSessionAttribute(REPORT_HIVEAPNAME_TUNNELCOUNT_MAP, hiveApNameTunnelCountMap);
		}
	}

	private String vTitle="";
	private String vUptime="0";
	private String vDowntime="0";
	private int vDowntimeNumber;
	private List<TextItem> vpn_availability=null;
	private List<TextItem> vpn_uptime=null;
	
	private double vAputIn;
	private double vAputOut;
	private double vLputIn;
	private double vLputOut;
	private double vHputIn;
	private double vHputOut;
	private List<TextItem> vpn_throughput_in=null;
	private List<TextItem> vpn_throughput_out=null;
	
	private double vALatency;
	private double vSLatency;
	private double vHLatency;
	private List<TextItem> vpn_latency=null;


	public Calendar getReportDateTime() {
		int reportHour = 0;
		int reportDay = 0;
		int reportMonth = 0;
		switch (getDataSource().getReportPeriod()) {
		case AhReport.REPORT_PERIOD_VPN_ONEHOUR:
			reportHour = 1;
			break;
		case AhReport.REPORT_PERIOD_VPN_ONEDAY:
			reportDay = 1;
			break;
		case AhReport.REPORT_PERIOD_VPN_TWODAY:
			reportDay = 2;
			break;
		case AhReport.REPORT_PERIOD_VPN_THREEDAY:
			reportDay = 3;
			break;
		case AhReport.REPORT_PERIOD_VPN_ONEWEEK:
			reportDay = 7;
			break;
		case AhReport.REPORT_PERIOD_VPN_TWOWEEK:
			reportDay = 14;
			break;
		case AhReport.REPORT_PERIOD_VPN_THREEWEEK:
			reportDay = 21;
			break;
		case AhReport.REPORT_PERIOD_VPN_ONEMONTH:
			reportDay = 30;
			break;
		}
		Calendar calendar = Calendar.getInstance(tz);
		calendar.add(Calendar.DAY_OF_MONTH, reportDay * -1);
		calendar.add(Calendar.MONTH, reportMonth * -1);
		calendar.add(Calendar.HOUR_OF_DAY, reportHour * -1);
		return calendar;
	}

	public long getReportTimeAggregation() {
		int reportHour;
		switch (getDataSource().getTimeAggregation()) {
		case AhReport.TIME_AGGREGATION_FOURHOURS:
			reportHour = 4;
			break;
		case AhReport.TIME_AGGREGATION_EIGHTHOURS:
			reportHour = 8;
			break;
		case AhReport.TIME_AGGREGATION_ONEDAY:
			reportHour = 24;
			break;
		case AhReport.TIME_AGGREGATION_TWODAYS:
			reportHour = 48;
			break;
		case AhReport.TIME_AGGREGATION_ONEWEEK:
			reportHour = 168;
			break;
		case AhReport.TIME_AGGREGATION_TWOWEEKS:
			reportHour = 336;
			break;
		default:
			reportHour = 1;

		}
		return reportHour * 3600000;
	}

	public void initFlashData() throws Exception {
		Calendar reportDateTime = getReportDateTime();
//		long reportTimeAggregation = getReportTimeAggregation();

		if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNAVAILABILITY)) {
			setVpnAvailablity(reportDateTime);
		} else if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNTHROUGHPUT)) {
			setVpnThroughput(reportDateTime);
		} else if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNLATENCY)) {
			setVpnLatency(reportDateTime);
		} else if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_WANAVAILABILITY)
				|| getDataSource().getReportType().equals(L2_FEATURE_REPORT_GWWANAVAILABILITY)) {
			setWanAvailablity(reportDateTime);
		} else if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_WANTHROUGHPUT)
				|| getDataSource().getReportType().equals(L2_FEATURE_REPORT_GWWANTHROUGHPUT)) {
			setWanThroughput(reportDateTime);
		} else if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_GWVPNAVAILABILITY)) {
			setGwVpnAvailablity(reportDateTime);
//		} else if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_GWWANAVAILABILITY)) {
//			setGwWanAvailablity(reportDateTime);
//		} else if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_GWWANTHROUGHPUT)) {
//			setGwWanThroughput(reportDateTime);
		}
	}
	
	public void setVpnAvailablity(Calendar reportDateTime) {
		long timeTigg=0;
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			timeTigg=1;
			vTitle="VPN Availability: Last 60 Minutes";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEDAY) {
			timeTigg=60;
			vTitle="VPN Availability: Last One Day";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_TWODAY) {
			timeTigg=120;
			vTitle="VPN Availability: Last Two Days";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_THREEDAY) {
			timeTigg=180;
			vTitle="VPN Availability: Last Three Days";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEWEEK) {
			timeTigg=420;
			vTitle="VPN Availability: Last One Week";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_TWOWEEK) {
			timeTigg=840;
			vTitle="VPN Availability: Last Two Weeks";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_THREEWEEK) {
			timeTigg=1260;
			vTitle="VPN Availability: Last Three Weeks";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEMONTH) {
			timeTigg=1800;
			vTitle="VPN Availability: Last One Month";
		}
		
		if (reportTunnelName.equals(MgrUtil.getUserMessage("config.optionsTransfer.none"))){
			return;
		}
		
		hiveApNameMacMap = (HashMap<String,String>)MgrUtil.getSessionAttribute(REPORT_HIVEAPNAME_MAC_MAP);
		DecimalFormat df = new DecimalFormat("0.00");
		String searchSQL = "lower(mac)=:s1 and interfType=:s2";

		Object values[] = new Object[2];
//		values[0] = reportDateTime.getTimeInMillis();
		values[0] = hiveApNameMacMap.get(reportAPName)==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(reportAPName);
		values[1] = AhPortAvailability.INTERFACE_TYPE_VPN;
		List<?> lstInterfaceInfo=null;
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			lstInterfaceInfo = QueryUtil.executeQuery(AhStatsLatencyHigh.class, new SortParams("time"),
					new FilterParams(searchSQL, values), getDomain().getId());
		} else {
			lstInterfaceInfo = QueryUtil.executeQuery(AhStatsLatencyLow.class, new SortParams("time"),
					new FilterParams(searchSQL, values), getDomain().getId());
		}
		int totalBarCount=0;
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			totalBarCount=60;
		} else {
			totalBarCount=24;
		}
		
		int[] totalCount= new int[totalBarCount];
		int[] upCount= new int[totalBarCount];
		
		Map<String, List<String>> tunnelNameMap = (Map<String, List<String>>)MgrUtil.getSessionAttribute(REPORT_TUNNELNAME_MAP);
		int allTunnelCount = tunnelNameMap.get(reportAPName).size() -1;
		if (!reportTunnelName.equals("All")){
			allTunnelCount =1;
		}
		
		int index=0;
		long currentTime=0;
		long nextTime=0;
		long oneTimeRecordCount=0;
		long totalTimeRecordtime=0;
		long totalUptime=0;
		
		vpn_uptime= new ArrayList<TextItem>();
		vpn_availability= new ArrayList<TextItem>();
		
		long lastRecordTime=System.currentTimeMillis();
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			List<AhStatsAvailabilityHigh> oneTimeRec = QueryUtil.executeQuery(AhStatsAvailabilityHigh.class, new SortParams("time", false),
					new FilterParams("lower(mac)", values[0].toString()), getDomain().getId(), 1);
			if (!oneTimeRec.isEmpty()) {
				lastRecordTime = oneTimeRec.get(0).getTime();
			}
			
		} else {
			List<AhStatsAvailabilityLow> oneTimeRec = QueryUtil.executeQuery(AhStatsAvailabilityLow.class, new SortParams("time", false),
					new FilterParams("lower(mac)", values[0].toString()), getDomain().getId(), 1);
			if (!oneTimeRec.isEmpty()) {
				lastRecordTime = oneTimeRec.get(0).getTime();
			}
		}
		lastRecordTime = lastRecordTime - timeTigg * 60 *1000L * totalBarCount;
		
		
		for(Object OneRec : lstInterfaceInfo) {
			long recTime;
			byte recStatus;
			String recServerIp;
			if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
				recTime = ((AhStatsLatencyHigh)OneRec).getTime();
				recStatus = ((AhStatsLatencyHigh)OneRec).getTargetStatus();
				recServerIp = ((AhStatsLatencyHigh)OneRec).getInterfServer();
			} else {
				recTime = ((AhStatsLatencyLow)OneRec).getTime();
				recStatus = ((AhStatsLatencyLow)OneRec).getTargetStatus();
				recServerIp = ((AhStatsLatencyLow)OneRec).getInterfServer();
			}
			if (recTime<=lastRecordTime) {
				continue;
			} else if(currentTime==0 && recTime>lastRecordTime + timeTigg * 60 *1000L) {
				currentTime = lastRecordTime + (index) * timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;
				if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					oneTimeRecordCount = 1;
				} else {
					oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
				}
				totalCount[index]= allTunnelCount * ((int)oneTimeRecordCount);
				totalTimeRecordtime = totalTimeRecordtime + totalCount[index];
				
				while (recTime>lastRecordTime + (index +1) * timeTigg * 60 *1000L){
					long reportTimeConvert=nextTime;
//					if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//						reportTimeConvert = nextTime;
//					}
					vpn_availability.add(new TextItem(
							df.format(((float)upCount[index] *100)/(totalCount[index])),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					
					vpn_uptime.add(new TextItem(
							df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					if (index <(totalBarCount-1)){
						index++;
						currentTime = currentTime + timeTigg * 60 *1000L;
						nextTime=currentTime + timeTigg * 60 *1000L;
						if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
							oneTimeRecordCount = 1;
						} else {
							oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
						}
						totalCount[index]= allTunnelCount * ((int)oneTimeRecordCount);
						totalTimeRecordtime = totalTimeRecordtime + totalCount[index];
					} else {
						break;
					}
				}
			}
			
			if (currentTime==0) {
				currentTime = lastRecordTime;
				nextTime=currentTime + timeTigg * 60 *1000L;
				if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					oneTimeRecordCount = 1;
				} else {
					oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
				}
				totalCount[index]= allTunnelCount * ((int)oneTimeRecordCount);
				totalTimeRecordtime = totalTimeRecordtime + totalCount[index];
			}
			
			if (recTime <= nextTime) {
				if (reportTunnelName.equals("All")){
					if (recStatus==AhStatsLatencyHigh.TARGET_STATUS_UP){
						upCount[index]++;
						totalUptime++;
					}
				} else {
					Map<String, String> tunnelNameIPMap = (Map<String, String>)MgrUtil.getSessionAttribute(REPORT_TUNNELNAMEIP_MAP);
					if (recServerIp.equals(tunnelNameIPMap.get(reportAPName + "-_-" + reportTunnelName))) {
						if (recStatus==AhStatsLatencyHigh.TARGET_STATUS_UP){
							upCount[index]++;
							totalUptime++;
						} 
					}
				}
			} else {
				long reportTimeConvert=nextTime;
//				if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//					reportTimeConvert = nextTime;
//				}
				vpn_availability.add(new TextItem(
						df.format(((float)upCount[index] *100)/(totalCount[index])),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				
				vpn_uptime.add(new TextItem(
						df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				if (index<(totalBarCount-1)){
					index++;
					currentTime = currentTime + timeTigg * 60 *1000L;
					nextTime=currentTime + timeTigg * 60 *1000L;
					if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
						oneTimeRecordCount = 1;
					} else {
						oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
					}
					totalCount[index]= allTunnelCount * ((int)oneTimeRecordCount);
					totalTimeRecordtime = totalTimeRecordtime + totalCount[index];
					
					while (recTime> currentTime && index <totalBarCount){
						if (recTime<=nextTime) {
							if (reportTunnelName.equals("All")){
								if (recStatus==AhStatsLatencyHigh.TARGET_STATUS_UP){
									upCount[index]++;
									totalUptime++;
								}
							} else {
								Map<String, String> tunnelNameIPMap = (Map<String, String>)MgrUtil.getSessionAttribute(REPORT_TUNNELNAMEIP_MAP);
								if (recServerIp.equals(tunnelNameIPMap.get(reportAPName + "-_-" + reportTunnelName))) {
									if (recStatus==AhStatsLatencyHigh.TARGET_STATUS_UP){
										upCount[index]++;
										totalUptime++;
									} 
								}
							}
							break;
						} else {
							reportTimeConvert=nextTime;
//							if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//								reportTimeConvert = nextTime;
//							}
							vpn_availability.add(new TextItem(
									df.format(((float)upCount[index] *100)/(totalCount[index])),
									AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
							
							vpn_uptime.add(new TextItem(
									df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
									AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
							if (index <(totalBarCount-1)){
								index++;
								currentTime = currentTime + timeTigg * 60 *1000L;
								nextTime=currentTime + timeTigg * 60 *1000L;
								if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
									oneTimeRecordCount = 1;
								} else {
									oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
								}
								totalCount[index]= allTunnelCount * ((int)oneTimeRecordCount);
								totalTimeRecordtime = totalTimeRecordtime + totalCount[index];
							} else {
								break;
							}
						}
					}
				}
			}
		}
		if (vpn_availability.size()<totalBarCount && index!=0) {
			long reportTimeConvert=nextTime;
//			if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//				reportTimeConvert = nextTime;
//			}
			vpn_availability.add(new TextItem(
					df.format(((float)upCount[index] *100)/(totalCount[index])),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			
			vpn_uptime.add(new TextItem(
					df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			index++;
		}

		while (index <totalBarCount){
			currentTime = lastRecordTime + (index) * timeTigg * 60 *1000L;
			nextTime=currentTime + timeTigg * 60 *1000L;
			if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
				oneTimeRecordCount = 1;
			} else {
				oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
			}
			totalCount[index]= allTunnelCount * ((int)oneTimeRecordCount);
			totalTimeRecordtime = totalTimeRecordtime + totalCount[index];
			
			long reportTimeConvert=nextTime;
			vpn_availability.add(new TextItem(
					df.format(((float)upCount[index] *100)/(totalCount[index])),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			
			vpn_uptime.add(new TextItem(
					df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			index++;
		}
		
		if (totalTimeRecordtime!=0) {
			vUptime=df.format(((float)totalUptime *100)/(totalTimeRecordtime));
			vDowntime=String.valueOf((totalTimeRecordtime-totalUptime)* ((timeTigg==1?1:60)));
			vDowntimeNumber=(int)(totalTimeRecordtime-totalUptime);
		}

	}
	
	public void setVpnThroughput(Calendar reportDateTime) {
		hiveApNameMacMap = (HashMap<String,String>)MgrUtil.getSessionAttribute(REPORT_HIVEAPNAME_MAC_MAP);
		DecimalFormat df = new DecimalFormat("0.00");
		String searchSQL = "lower(mac)=:s1 and interfType=:s2";

		Object values[] = new Object[2];
//		values[0] = reportDateTime.getTimeInMillis();
		values[0] = hiveApNameMacMap.get(reportAPName)==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(reportAPName);
		values[1] = AhPortAvailability.INTERFACE_TYPE_VPN;
		List<?> lstInterfaceInfo=null;
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputHigh.class, new SortParams("time"),
					new FilterParams(searchSQL, values), getDomain().getId());
		} else {
			lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputLow.class, new SortParams("time"),
					new FilterParams(searchSQL, values), getDomain().getId());
		}
		long timeTigg=0;
		long hourTigg=0;
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			timeTigg=1;
			hourTigg=60;
			vTitle="VPN Throughput: Last 60 Minutes";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEDAY) {
			timeTigg=60;
			hourTigg=24;
			vTitle="VPN Throughput: Last One Day";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_TWODAY) {
			timeTigg=60;
			hourTigg=48;
			vTitle="VPN Throughput: Last Two Days";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_THREEDAY) {
			timeTigg=60;
			hourTigg=72;
			vTitle="VPN Throughput: Last Three Days";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEWEEK) {
			timeTigg=60;
			hourTigg=168;
			vTitle="VPN Throughput: Last One Week";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_TWOWEEK) {
			timeTigg=60;
			hourTigg=336;
			vTitle="VPN Throughput: Last Two Weeks";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_THREEWEEK) {
			timeTigg=60;
			hourTigg=504;
			vTitle="VPN Throughput: Last Three Weeks";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEMONTH) {
			timeTigg=60;
			hourTigg=720;
			vTitle="VPN Throughput: Last One Month";
		}
			
		int index=0;
		long currentTime=0;
		long nextTime=0;
		
		long throughput_in=0;
		long throughput_out=0;
		vpn_throughput_in = new ArrayList<TextItem>();
		vpn_throughput_out = new ArrayList<TextItem>();
		vAputIn=0;
		vAputOut=0;
		vLputIn=-1;
		vLputOut=-1;
		vHputIn=0;
		vHputOut=0;
		
		long lastRecordTime=System.currentTimeMillis();
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			List<AhStatsAvailabilityHigh> oneTimeRec = QueryUtil.executeQuery(AhStatsAvailabilityHigh.class, new SortParams("time", false),
					new FilterParams("lower(mac)", values[0].toString()), getDomain().getId(), 1);
			if (!oneTimeRec.isEmpty()) {
				lastRecordTime = oneTimeRec.get(0).getTime();
			}
			
		} else {
			List<AhStatsAvailabilityLow> oneTimeRec = QueryUtil.executeQuery(AhStatsAvailabilityLow.class, new SortParams("time", false),
					new FilterParams("lower(mac)", values[0].toString()), getDomain().getId(), 1);
			if (!oneTimeRec.isEmpty()) {
				lastRecordTime = oneTimeRec.get(0).getTime();
			}
		}
		lastRecordTime = lastRecordTime - timeTigg * 60 *1000L * hourTigg;
		long timePerPoint = timeTigg * 60;
		for(Object OneRec : lstInterfaceInfo) {
			long recTime;
			String recServerIp;
			long vPutIn=0;
			long vPutOut=0;
			
			if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
				recTime = ((AhStatsThroughputHigh)OneRec).getTime();
				recServerIp = ((AhStatsThroughputHigh)OneRec).getInterfServer();
				vPutIn = ((AhStatsThroughputHigh)OneRec).getRxBytes();
				vPutOut = ((AhStatsThroughputHigh)OneRec).getTxBytes();
			} else {
				recTime = ((AhStatsThroughputLow)OneRec).getTime();
				recServerIp = ((AhStatsThroughputLow)OneRec).getInterfServer();
				vPutIn = ((AhStatsThroughputLow)OneRec).getRxBytes();
				vPutOut = ((AhStatsThroughputLow)OneRec).getTxBytes();
			}
			
			if (recTime<=lastRecordTime) {
				continue;
			} else if(currentTime==0 && recTime>lastRecordTime + timeTigg * 60 *1000L) {
				currentTime = lastRecordTime + (index) * timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;
								
				while (recTime>lastRecordTime + (index +1) * timeTigg * 60 *1000L){
					long reportTimeConvert=nextTime;
//					if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//						reportTimeConvert = nextTime;
//					}
					vpn_throughput_in.add(new TextItem(
							df.format(((float)throughput_in)*8/1024/timePerPoint),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					vpn_throughput_out.add(new TextItem(
							df.format(((float)throughput_out*8/1024/timePerPoint)),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					vAputIn=vAputIn + throughput_in;
					vAputOut=vAputOut+ throughput_out;
					vLputIn=vLputIn==-1? throughput_in: (vLputIn<throughput_in? vLputIn: throughput_in);
					vLputOut=vLputOut==-1? throughput_out: (vLputOut<throughput_out? vLputOut: throughput_out);
					vHputIn=vHputIn>throughput_in?vHputIn: throughput_in;
					vHputOut=vHputOut>throughput_out?vHputOut: throughput_out;
					
					throughput_in=0;
					throughput_out=0;
					index++;
					currentTime = currentTime + timeTigg * 60 *1000L;
					nextTime=currentTime + timeTigg * 60 *1000L;
				}
			}
			
			
			if (currentTime==0) {
				currentTime = lastRecordTime;
				nextTime=currentTime + timeTigg * 60 *1000L;
			}
			
			if (recTime <= nextTime) {
				if (reportTunnelName.equals("All")){
					throughput_in = throughput_in + vPutIn;
					throughput_out = throughput_out + vPutOut; 
				} else {
					Map<String, String> tunnelNameIPMap = (Map<String, String>)MgrUtil.getSessionAttribute(REPORT_TUNNELNAMEIP_MAP);
					if (recServerIp.equals(tunnelNameIPMap.get(reportAPName + "-_-" + reportTunnelName))) {
						throughput_in = throughput_in + vPutIn;
						throughput_out = throughput_out + vPutOut; 
					}
				}
			} else {
				long reportTimeConvert=nextTime;
//				if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//					reportTimeConvert = nextTime;
//				}
				
				vpn_throughput_in.add(new TextItem(
						df.format(((float)throughput_in)*8/1024/timePerPoint),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				vpn_throughput_out.add(new TextItem(
						df.format(((float)throughput_out*8/1024/timePerPoint)),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				vAputIn=vAputIn + throughput_in;
				vAputOut=vAputOut+ throughput_out;
				vLputIn=vLputIn==-1? throughput_in: (vLputIn<throughput_in? vLputIn: throughput_in);
				vLputOut=vLputOut==-1? throughput_out: (vLputOut<throughput_out? vLputOut: throughput_out);
				vHputIn=vHputIn>throughput_in?vHputIn: throughput_in;
				vHputOut=vHputOut>throughput_out?vHputOut: throughput_out;
				
				throughput_in=0;
				throughput_out=0;

				index++;
				currentTime = currentTime + timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;
				
				while (recTime> currentTime){
					if (recTime<=nextTime) {
						if (reportTunnelName.equals("All")){
							throughput_in = throughput_in + vPutIn;
							throughput_out = throughput_out + vPutOut; 
						} else {
							Map<String, String> tunnelNameIPMap = (Map<String, String>)MgrUtil.getSessionAttribute(REPORT_TUNNELNAMEIP_MAP);
							if (recServerIp.equals(tunnelNameIPMap.get(reportAPName + "-_-" + reportTunnelName))) {
								throughput_in = throughput_in + vPutIn;
								throughput_out = throughput_out + vPutOut; 
							}
						}
						break;
					} else {
						reportTimeConvert=nextTime;
//						if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//							reportTimeConvert = nextTime;
//						}
						vpn_throughput_in.add(new TextItem(
								df.format(((float)throughput_in)*8/1024/timePerPoint),
								AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
						vpn_throughput_out.add(new TextItem(
								df.format(((float)throughput_out*8/1024/timePerPoint)),
								AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
						vAputIn=vAputIn + throughput_in;
						vAputOut=vAputOut+ throughput_out;
						vLputIn=vLputIn==-1? throughput_in: (vLputIn<throughput_in? vLputIn: throughput_in);
						vLputOut=vLputOut==-1? throughput_out: (vLputOut<throughput_out? vLputOut: throughput_out);
						vHputIn=vHputIn>throughput_in?vHputIn: throughput_in;
						vHputOut=vHputOut>throughput_out?vHputOut: throughput_out;
						
						throughput_in=0;
						throughput_out=0;
						
						index++;
						currentTime = currentTime + timeTigg * 60 *1000L;
						nextTime=currentTime + timeTigg * 60 *1000L;
					}
				}

			}
		}
		if (index!=0) {
			long reportTimeConvert=nextTime;
//			if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//				reportTimeConvert = nextTime;
//			}
			vpn_throughput_in.add(new TextItem(
					df.format(((float)throughput_in)*8/1024/timePerPoint),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			vpn_throughput_out.add(new TextItem(
					df.format(((float)throughput_out*8/1024/timePerPoint)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			
			vAputIn=vAputIn + throughput_in;
			vAputOut=vAputOut+ throughput_out;
			vLputIn=Double.valueOf(df.format(vLputIn==-1? ((float)throughput_in)*8/1024/timePerPoint: (vLputIn<throughput_in? vLputIn*8/1024/timePerPoint: ((float)throughput_in)*8/1024/timePerPoint)));
			vLputOut=Double.valueOf(df.format(vLputOut==-1? ((float)throughput_out)*8/1024/timePerPoint: (vLputOut<throughput_out? vLputOut*8/1024/timePerPoint: ((float)throughput_out)*8/1024/timePerPoint)));
			vHputIn=Double.valueOf(df.format(vHputIn>throughput_in?vHputIn*8/1024/timePerPoint: ((float)throughput_in)*8/1024/timePerPoint));
			vHputOut=Double.valueOf(df.format(vHputOut>throughput_out?vHputOut*8/1024/timePerPoint: ((float)throughput_out)*8/1024/timePerPoint));
			
			index++;
			currentTime = currentTime + timeTigg * 60 *1000L;
			nextTime=currentTime + timeTigg * 60 *1000L;
			throughput_in=0;
			throughput_out=0;
		}
		
		if (currentTime==0) {
			currentTime = lastRecordTime;
			nextTime=currentTime + timeTigg * 60 *1000L;
			throughput_in=0;
			throughput_out=0;
		}
		while (nextTime <=lastRecordTime + timeTigg * 60 *1000L * hourTigg) {
			long reportTimeConvert=nextTime;
			vpn_throughput_in.add(new TextItem(
					df.format(((float)throughput_in)*8/1024/timePerPoint),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			vpn_throughput_out.add(new TextItem(
					df.format(((float)throughput_out*8/1024/timePerPoint)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			vAputIn=vAputIn + throughput_in;
			vAputOut=vAputOut+ throughput_out;
			vLputIn=vLputIn==-1? throughput_in: (vLputIn<throughput_in? vLputIn: throughput_in);
			vLputOut=vLputOut==-1? throughput_out: (vLputOut<throughput_out? vLputOut: throughput_out);
			vHputIn=vHputIn>throughput_in?vHputIn: throughput_in;
			vHputOut=vHputOut>throughput_out?vHputOut: throughput_out;
			
			throughput_in=0;
			throughput_out=0;
			index++;
			currentTime = currentTime + timeTigg * 60 *1000L;
			nextTime=currentTime + timeTigg * 60 *1000L;
		}
		
		if (index!=0) {
			vAputIn = Double.valueOf(df.format(vAputIn/(index)*8/1024/timePerPoint));
			vAputOut = Double.valueOf(df.format(vAputOut/(index)*8/1024/timePerPoint));
		}
	}
	
	public void setVpnLatency(Calendar reportDateTime) {
		hiveApNameMacMap = (HashMap<String,String>)MgrUtil.getSessionAttribute(REPORT_HIVEAPNAME_MAC_MAP);
		DecimalFormat df = new DecimalFormat("0.00");
		String searchSQL = "lower(mac)=:s1 and interfType=:s2";

		Object values[] = new Object[2];
//		values[0] = reportDateTime.getTimeInMillis();
		values[0] = hiveApNameMacMap.get(reportAPName)==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(reportAPName);
		values[1] = AhPortAvailability.INTERFACE_TYPE_VPN;
		List<?> lstInterfaceInfo=null;
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			lstInterfaceInfo = QueryUtil.executeQuery(AhStatsLatencyHigh.class, new SortParams("time"),
					new FilterParams(searchSQL, values), getDomain().getId());
		} else {
			lstInterfaceInfo = QueryUtil.executeQuery(AhStatsLatencyLow.class, new SortParams("time"),
					new FilterParams(searchSQL, values), getDomain().getId());
		}
		long timeTigg=0;
		long hourTigg=0;
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			timeTigg=1;
			hourTigg=60;
			vTitle="VPN Latency: Last 60 Minutes";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEDAY) {
			timeTigg=60;
			hourTigg=24;
			vTitle="VPN Latency: Last One Day";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_TWODAY) {
			timeTigg=60;
			hourTigg=48;
			vTitle="VPN Latency: Last Two Days";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_THREEDAY) {
			timeTigg=60;
			hourTigg=72;
			vTitle="VPN Latency: Last Three Days";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEWEEK) {
			timeTigg=60;
			hourTigg=168;
			vTitle="VPN Latency: Last One Week";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_TWOWEEK) {
			timeTigg=60;
			hourTigg=336;
			vTitle="VPN Latency: Last Two Weeks";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_THREEWEEK) {
			timeTigg=60;
			hourTigg=504;
			vTitle="VPN Latency: Last Three Weeks";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEMONTH) {
			timeTigg=60;
			hourTigg=720;
			vTitle="VPN Latency: Last One Month";
		}
		int index=0;
		long currentTime=0;
		long nextTime=0;
		
		double latecny_rrt=0;
		long latecny_rrt_count=0;
		vpn_latency = new ArrayList<TextItem>();
		vALatency=0;
		long latencyCount=0;
		vSLatency=-1;
		vHLatency=-1;
		
		long lastRecordTime=System.currentTimeMillis();
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			List<AhStatsAvailabilityHigh> oneTimeRec = QueryUtil.executeQuery(AhStatsAvailabilityHigh.class, new SortParams("time", false),
					new FilterParams("lower(mac)", values[0].toString()), getDomain().getId(), 1);
			if (!oneTimeRec.isEmpty()) {
				lastRecordTime = oneTimeRec.get(0).getTime();
			}
			
		} else {
			List<AhStatsAvailabilityLow> oneTimeRec = QueryUtil.executeQuery(AhStatsAvailabilityLow.class, new SortParams("time", false),
					new FilterParams("lower(mac)", values[0].toString()), getDomain().getId(), 1);
			if (!oneTimeRec.isEmpty()) {
				lastRecordTime = oneTimeRec.get(0).getTime();
			}
		}
		lastRecordTime = lastRecordTime - timeTigg * 60 *1000L * hourTigg;
		
		for(Object OneRec : lstInterfaceInfo) {
			long recTime;
			String recServerIp;
			double vRrt=0;
			
			if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
				
				recTime = ((AhStatsLatencyHigh)OneRec).getTime();
				recServerIp = ((AhStatsLatencyHigh)OneRec).getInterfServer();
				if (((AhStatsLatencyHigh)OneRec).getTargetStatus()==AhStatsLatencyHigh.TARGET_STATUS_UP){
					vRrt = ((AhStatsLatencyHigh)OneRec).getRtt();
				} else {
					vRrt=0;
				}
				
			} else {
				recTime = ((AhStatsLatencyLow)OneRec).getTime();
				recServerIp = ((AhStatsLatencyLow)OneRec).getInterfServer();
				if (((AhStatsLatencyLow)OneRec).getTargetStatus()==AhStatsLatencyHigh.TARGET_STATUS_UP){
					vRrt = ((AhStatsLatencyLow)OneRec).getRtt();
				} else {
					vRrt=0;
				}
			}
			
			if (recTime<=lastRecordTime) {
				continue;
			} else if(currentTime==0 && recTime>lastRecordTime + timeTigg * 60 *1000L) {
				currentTime = lastRecordTime + (index) * timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;
				
				while (recTime>lastRecordTime + (index +1) * timeTigg * 60 *1000L){
					long reportTimeConvert=nextTime;
//					if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//						reportTimeConvert = nextTime;
//					}
					
					vpn_latency.add(new TextItem(
							df.format((latecny_rrt>0 && latecny_rrt_count>0)?latecny_rrt/latecny_rrt_count:-1),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					
					vALatency=vALatency + latecny_rrt;
					if (latecny_rrt>0 && latecny_rrt_count>0) {
						vSLatency=vSLatency==-1? latecny_rrt/latecny_rrt_count: (vSLatency<(latecny_rrt/latecny_rrt_count)? vSLatency: (latecny_rrt/latecny_rrt_count));
					}
					if (latecny_rrt>0 && latecny_rrt_count>0) {
						vHLatency=vHLatency>latecny_rrt/latecny_rrt_count?vHLatency: latecny_rrt/latecny_rrt_count;
					}
					latecny_rrt=0;
					latecny_rrt_count=0;

					index++;
					currentTime = currentTime + timeTigg * 60 *1000L;
					nextTime=currentTime + timeTigg * 60 *1000L;
					
				}
			}
			
			if (currentTime==0) {
				currentTime = lastRecordTime;
				nextTime=currentTime + timeTigg * 60 *1000L;
			}
			
			if (recTime <= nextTime) {
				if (reportTunnelName.equals("All")){
					latecny_rrt = latecny_rrt + vRrt;
					if (vRrt!=0){
						latecny_rrt_count++;
						latencyCount++;
					}
				} else {
					Map<String, String> tunnelNameIPMap = (Map<String, String>)MgrUtil.getSessionAttribute(REPORT_TUNNELNAMEIP_MAP);
					if (recServerIp.equals(tunnelNameIPMap.get(reportAPName + "-_-" + reportTunnelName))) {
						latecny_rrt = latecny_rrt + vRrt;
						if (vRrt!=0){
							latecny_rrt_count++;
							latencyCount++;
						}
					}
				}
			} else {
				long reportTimeConvert=nextTime;
//				if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//					reportTimeConvert = nextTime;
//				}
				
				vpn_latency.add(new TextItem(
						df.format((latecny_rrt>0 && latecny_rrt_count>0)?latecny_rrt/latecny_rrt_count:-1),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				
				vALatency=vALatency + latecny_rrt;
				if (latecny_rrt>0 && latecny_rrt_count>0) {
					vSLatency=vSLatency==-1? latecny_rrt/latecny_rrt_count: (vSLatency<(latecny_rrt/latecny_rrt_count)? vSLatency: (latecny_rrt/latecny_rrt_count));
				}
				if (latecny_rrt>0 && latecny_rrt_count>0) {
					vHLatency=vHLatency>latecny_rrt/latecny_rrt_count?vHLatency: latecny_rrt/latecny_rrt_count;
				}
				latecny_rrt=0;
				latecny_rrt_count=0;

				index++;
				currentTime = currentTime + timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;
				
				while (recTime> currentTime){
					if (recTime<=nextTime) {
						if (reportTunnelName.equals("All")){
							latecny_rrt = latecny_rrt + vRrt;
							if (vRrt!=0){
								latecny_rrt_count++;
								latencyCount++;
							}
						} else {
							Map<String, String> tunnelNameIPMap = (Map<String, String>)MgrUtil.getSessionAttribute(REPORT_TUNNELNAMEIP_MAP);
							if (recServerIp.equals(tunnelNameIPMap.get(reportAPName + "-_-" + reportTunnelName))) {
								latecny_rrt = latecny_rrt + vRrt;
								if (vRrt!=0){
									latecny_rrt_count++;
									latencyCount++;
								}
							}
						}
						break;
					} else {
						reportTimeConvert=nextTime;
//						if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//							reportTimeConvert = nextTime;
//						}
						vpn_latency.add(new TextItem(
								df.format((latecny_rrt>0 && latecny_rrt_count>0)?latecny_rrt/latecny_rrt_count:-1),
								AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
						
						vALatency=vALatency + latecny_rrt;
						if (latecny_rrt>0 && latecny_rrt_count>0) {
							vSLatency=vSLatency==-1? latecny_rrt/latecny_rrt_count: (vSLatency<(latecny_rrt/latecny_rrt_count)? vSLatency: (latecny_rrt/latecny_rrt_count));
						}
						if (latecny_rrt>0 && latecny_rrt_count>0) {
							vHLatency=vHLatency>latecny_rrt/latecny_rrt_count?vHLatency: latecny_rrt/latecny_rrt_count;
						}
						
						latecny_rrt=0;
						latecny_rrt_count=0;
						index++;
						currentTime = currentTime + timeTigg * 60 *1000L;
						nextTime=currentTime + timeTigg * 60 *1000L;
					}
				}
			}
		}
		
		if (index!=0) {
			long reportTimeConvert=nextTime;
//			if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//				reportTimeConvert = nextTime;
//			}
			vpn_latency.add(new TextItem(
					df.format((latecny_rrt>0 && latecny_rrt_count>0)?latecny_rrt/latecny_rrt_count:-1),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			
			vALatency=vALatency + latecny_rrt;
			if (latecny_rrt>0 && latecny_rrt_count>0) {
				vSLatency=vSLatency==-1? latecny_rrt/latecny_rrt_count: (vSLatency<(latecny_rrt/latecny_rrt_count)? vSLatency: (latecny_rrt/latecny_rrt_count));
			}
			if (latecny_rrt>0 && latecny_rrt_count>0) {
				vHLatency=vHLatency>latecny_rrt/latecny_rrt_count?vHLatency: latecny_rrt/latecny_rrt_count;
			}
			
			latecny_rrt=0;
			latecny_rrt_count=0;
			index++;
			currentTime = currentTime + timeTigg * 60 *1000L;
			nextTime=currentTime + timeTigg * 60 *1000L;
			
		}
		if (currentTime==0) {
			currentTime = lastRecordTime;
			nextTime=currentTime + timeTigg * 60 *1000L;
			latecny_rrt=0;
			latecny_rrt_count=0;
		}
		while (nextTime<=lastRecordTime + timeTigg * 60 *1000L * hourTigg){
			long reportTimeConvert=nextTime;
			vpn_latency.add(new TextItem(
					df.format((latecny_rrt>0 && latecny_rrt_count>0)?latecny_rrt/latecny_rrt_count:-1),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			
			vALatency=vALatency + latecny_rrt;
			if (latecny_rrt>0 && latecny_rrt_count>0) {
				vSLatency=vSLatency==-1? latecny_rrt/latecny_rrt_count: (vSLatency<(latecny_rrt/latecny_rrt_count)? vSLatency: (latecny_rrt/latecny_rrt_count));
			}
			if (latecny_rrt>0 && latecny_rrt_count>0) {
				vHLatency=vHLatency>latecny_rrt/latecny_rrt_count?vHLatency: latecny_rrt/latecny_rrt_count;
			}
			latecny_rrt=0;
			latecny_rrt_count=0;

			index++;
			currentTime = currentTime + timeTigg * 60 *1000L;
			nextTime=currentTime + timeTigg * 60 *1000L;
			
		}

		if (latencyCount>0){
			vALatency = Double.valueOf(df.format(vALatency/latencyCount));
		}
		vSLatency = Double.valueOf(df.format(vSLatency));
		vHLatency = Double.valueOf(df.format(vHLatency));
	}
	public void setWanAvailablity(Calendar reportDateTime) {
		hiveApNameMacMap = (HashMap<String,String>)MgrUtil.getSessionAttribute(REPORT_HIVEAPNAME_MAC_MAP);
		DecimalFormat df = new DecimalFormat("0.00");
		String searchSQL = "lower(mac)=:s1";

		Object values[] = new Object[1];
//		values[0] = reportDateTime.getTimeInMillis();
		values[0] = hiveApNameMacMap.get(reportAPName)==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(reportAPName);

		List<?> lstInterfaceInfo=null;
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			lstInterfaceInfo = QueryUtil.executeQuery(AhStatsAvailabilityHigh.class, new SortParams("time"),
					new FilterParams(searchSQL, values), getDomain().getId());
		} else {
			lstInterfaceInfo = QueryUtil.executeQuery(AhStatsAvailabilityLow.class, new SortParams("time"),
					new FilterParams(searchSQL, values), getDomain().getId());
		}
		long timeTigg=0;
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			timeTigg=1;
			vTitle="WAN Availability: Last 60 Minutes";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEDAY) {
			timeTigg=60;
			vTitle="WAN Availability: Last One Day";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_TWODAY) {
			timeTigg=120;
			vTitle="WAN Availability: Last Two Days";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_THREEDAY) {
			timeTigg=180;
			vTitle="WAN Availability: Last Three Days";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEWEEK) {
			timeTigg=420;
			vTitle="WAN Availability: Last One Week";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_TWOWEEK) {
			timeTigg=840;
			vTitle="WAN Availability: Last Two Weeks";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_THREEWEEK) {
			timeTigg=1260;
			vTitle="WAN Availability: Last Three Weeks";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEMONTH) {
			timeTigg=1800;
			vTitle="WAN Availability: Last One Month";
		}
		
		int totalBarCount=0;
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			totalBarCount=60;
		} else {
			totalBarCount=24;
		}
		
		int[] upCount= new int[totalBarCount];
		int index=0;
		long currentTime=0;
		long nextTime=0;
		long oneTimeRecordCount=0;
		long totalTimeRecordtime=0;
		long totalUptime=0;
		
		vpn_uptime= new ArrayList<TextItem>();
		vpn_availability= new ArrayList<TextItem>();
		
		long lastRecordTime=System.currentTimeMillis();
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			List<AhStatsAvailabilityHigh> oneTimeRec = QueryUtil.executeQuery(AhStatsAvailabilityHigh.class, new SortParams("time", false),
					new FilterParams("lower(mac)", values[0].toString()), getDomain().getId(), 1);
			if (!oneTimeRec.isEmpty()) {
				lastRecordTime = oneTimeRec.get(0).getTime();
			}
			
		} else {
			List<AhStatsAvailabilityLow> oneTimeRec = QueryUtil.executeQuery(AhStatsAvailabilityLow.class, new SortParams("time", false),
					new FilterParams("lower(mac)", values[0].toString()), getDomain().getId(), 1);
			if (!oneTimeRec.isEmpty()) {
				lastRecordTime = oneTimeRec.get(0).getTime();
			}
		}
		lastRecordTime = lastRecordTime - timeTigg * 60 *1000L * totalBarCount;
		
		for(Object OneRec : lstInterfaceInfo) {
			long recTime;
			byte recStatus;
			int activeStatus=0;
			
			if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
				recTime = ((AhStatsAvailabilityHigh)OneRec).getTime();
				recStatus = ((AhStatsAvailabilityHigh)OneRec).getInterfStatus();
				activeStatus = ((AhStatsAvailabilityHigh)OneRec).getInterfActive();
			} else {
				recTime = ((AhStatsAvailabilityLow)OneRec).getTime();
				recStatus = ((AhStatsAvailabilityLow)OneRec).getInterfStatus();
				activeStatus = ((AhStatsAvailabilityLow)OneRec).getInterfActive();
			}
			if (activeStatus==0) {
				continue;
			}
			
			if (recTime<=lastRecordTime) {
				continue;
			} else if(currentTime==0 && recTime>lastRecordTime + timeTigg * 60 *1000L) {
				currentTime = lastRecordTime + (index) * timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;
				if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					oneTimeRecordCount = 1;
				} else {
					oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
				}
				totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
				
				while (recTime>lastRecordTime + (index +1) * timeTigg * 60 *1000L){
					long reportTimeConvert=nextTime;
//					if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//						reportTimeConvert = nextTime;
//					}
					vpn_availability.add(new TextItem(
							df.format(0),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					
					vpn_uptime.add(new TextItem(
							df.format(0),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					if (index <(totalBarCount-1)){
						index++;
						currentTime = currentTime + timeTigg * 60 *1000L;
						nextTime=currentTime + timeTigg * 60 *1000L;
						if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
							oneTimeRecordCount = 1;
						} else {
							oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
						}
						totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
					} else {
						break;
					}
				}
			}
			
			if (currentTime==0) {
				currentTime = lastRecordTime;
				nextTime=currentTime + timeTigg * 60 *1000L;
				if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					oneTimeRecordCount = 1;
				} else {
					oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
				}
				totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
			}
			
			
			if (recTime <= nextTime) {
				if (recStatus==AhPortAvailability.INTERFACE_STATUS_UP){
					upCount[index]++;
					totalUptime++;
				}
			} else {
				long reportTimeConvert=nextTime;
//				if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//					reportTimeConvert = nextTime;
//				}
				vpn_availability.add(new TextItem(
						df.format(((float)upCount[index] *100)/(oneTimeRecordCount)),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				
				vpn_uptime.add(new TextItem(
						df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				
				if (index<(totalBarCount-1)){
					index++;
					currentTime = currentTime + timeTigg * 60 *1000L;
					nextTime=currentTime + timeTigg * 60 *1000L;
					if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
						oneTimeRecordCount = 1;
					} else {
						oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
					}
					totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
					
					while (recTime> currentTime && index <totalBarCount){
						if (recTime<=nextTime) {
							if (recStatus==AhPortAvailability.INTERFACE_STATUS_UP){
								upCount[index]++;
								totalUptime++;
							}
							break;
						} else {
							reportTimeConvert=nextTime;
//							if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//								reportTimeConvert = nextTime;
//							}
							vpn_availability.add(new TextItem(
									df.format(((float)upCount[index] *100)/(oneTimeRecordCount)),
									AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
							
							vpn_uptime.add(new TextItem(
									df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
									AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
							if (index <(totalBarCount-1)){
								index++;
								currentTime = currentTime + timeTigg * 60 *1000L;
								nextTime=currentTime + timeTigg * 60 *1000L;
								if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
									oneTimeRecordCount = 1;
								} else {
									oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
								}
								totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
							} else {
								break;
							}
						}
					}
				}
			}
		}
		if (vpn_availability.size()<totalBarCount && index!=0) {
			long reportTimeConvert=nextTime;
//			if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//				reportTimeConvert = nextTime;
//			}
			vpn_availability.add(new TextItem(
					df.format(((float)upCount[index] *100)/(oneTimeRecordCount)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			
			vpn_uptime.add(new TextItem(
					df.format(((float)totalUptime *100)/(totalTimeRecordtime)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
		}
		if (totalTimeRecordtime!=0) {
			vUptime=df.format(((float)totalUptime *100)/(totalTimeRecordtime));
			vDowntime=String.valueOf((totalTimeRecordtime-totalUptime)* ((timeTigg==1?1:60)));
			vDowntimeNumber=(int)(totalTimeRecordtime-totalUptime);
		}	
	}
	
	public void setWanThroughput(Calendar reportDateTime) {
		hiveApNameMacMap = (HashMap<String,String>)MgrUtil.getSessionAttribute(REPORT_HIVEAPNAME_MAC_MAP);
		DecimalFormat df = new DecimalFormat("0.00");
		String searchSQL = "lower(mac)=:s1 and interfType=:s2";

		Object values[] = new Object[2];
//		values[0] = reportDateTime.getTimeInMillis();
		values[0] = hiveApNameMacMap.get(reportAPName)==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(reportAPName);
		values[1] = AhPortAvailability.INTERFACE_TYPE_WAN;
		List<?> lstInterfaceInfo=null;
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputHigh.class, new SortParams("time"),
					new FilterParams(searchSQL, values), getDomain().getId());
		} else {
			lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputLow.class, new SortParams("time"),
					new FilterParams(searchSQL, values), getDomain().getId());
		}
		long timeTigg=0;
		long hourTigg=0;
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			timeTigg=1;
			hourTigg=60;
			vTitle="WAN Throughput: Last 60 Minutes";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEDAY) {
			timeTigg=60;
			hourTigg=24;
			vTitle="WAN Throughput: Last One Day";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_TWODAY) {
			timeTigg=60;
			hourTigg=48;
			vTitle="WAN Throughput: Last Two Days";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_THREEDAY) {
			timeTigg=60;
			hourTigg=72;
			vTitle="WAN Throughput: Last Three Days";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEWEEK) {
			timeTigg=60;
			hourTigg=168;
			vTitle="WAN Throughput: Last One Week";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_TWOWEEK) {
			timeTigg=60;
			hourTigg=336;
			vTitle="WAN Throughput: Last Two Weeks";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_THREEWEEK) {
			timeTigg=60;
			hourTigg=504;
			vTitle="WAN Throughput: Last Three Weeks";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEMONTH) {
			timeTigg=60;
			hourTigg=720;
			vTitle="WAN Throughput: Last One Month";
		}
			
		
		int index=0;
		long currentTime=0;
		long nextTime=0;
		
		long throughput_in=0;
		long throughput_out=0;
		vpn_throughput_in = new ArrayList<TextItem>();
		vpn_throughput_out = new ArrayList<TextItem>();
		vAputIn=0;
		vAputOut=0;
		vLputIn=-1;
		vLputOut=-1;
		vHputIn=0;
		vHputOut=0;
		
		long lastRecordTime=System.currentTimeMillis();
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			List<AhStatsAvailabilityHigh> oneTimeRec = QueryUtil.executeQuery(AhStatsAvailabilityHigh.class, new SortParams("time", false),
					new FilterParams("lower(mac)", values[0].toString()), getDomain().getId(), 1);
			if (!oneTimeRec.isEmpty()) {
				lastRecordTime = oneTimeRec.get(0).getTime();
			}
			
		} else {
			List<AhStatsAvailabilityLow> oneTimeRec = QueryUtil.executeQuery(AhStatsAvailabilityLow.class, new SortParams("time", false),
					new FilterParams("lower(mac)", values[0].toString()), getDomain().getId(), 1);
			if (!oneTimeRec.isEmpty()) {
				lastRecordTime = oneTimeRec.get(0).getTime();
			}
		}
		
		lastRecordTime = lastRecordTime - timeTigg * 60 *1000L * hourTigg;
		long timePerPoint = timeTigg * 60;
		for(Object OneRec : lstInterfaceInfo) {
			long recTime;
			long vPutIn=0;
			long vPutOut=0;
			
			if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
				recTime = ((AhStatsThroughputHigh)OneRec).getTime();
				vPutIn = ((AhStatsThroughputHigh)OneRec).getRxBytes();
				vPutOut = ((AhStatsThroughputHigh)OneRec).getTxBytes();
			} else {
				recTime = ((AhStatsThroughputLow)OneRec).getTime();
				vPutIn = ((AhStatsThroughputLow)OneRec).getRxBytes();
				vPutOut = ((AhStatsThroughputLow)OneRec).getTxBytes();
			}
			
			if (recTime<=lastRecordTime) {
				continue;
			} else if(currentTime==0 && recTime>lastRecordTime + timeTigg * 60 *1000L) {
				currentTime = lastRecordTime + (index) * timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;
				
				while (recTime>lastRecordTime + (index +1) * timeTigg * 60 *1000L){
					long reportTimeConvert=nextTime;
//					if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//						reportTimeConvert = nextTime;
//					}
					
					vpn_throughput_in.add(new TextItem(
							df.format(((float)throughput_in)*8/1024/timePerPoint),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					vpn_throughput_out.add(new TextItem(
							df.format(((float)throughput_out*8/1024/timePerPoint)),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					vAputIn=vAputIn + throughput_in;
					vAputOut=vAputOut+ throughput_out;
					vLputIn=vLputIn==-1? throughput_in: (vLputIn<throughput_in? vLputIn: throughput_in);
					vLputOut=vLputOut==-1? throughput_out: (vLputOut<throughput_out? vLputOut: throughput_out);
					vHputIn=vHputIn>throughput_in?vHputIn: throughput_in;
					vHputOut=vHputOut>throughput_out?vHputOut: throughput_out;
					
					throughput_in=0;
					throughput_out=0;

					index++;
					currentTime = currentTime + timeTigg * 60 *1000L;
					nextTime=currentTime + timeTigg * 60 *1000L;
					
				}
			}
			
			if (currentTime==0) {
				currentTime = lastRecordTime;
				nextTime=currentTime + timeTigg * 60 *1000L;
			}
			
			if (recTime <= nextTime) {
				throughput_in = throughput_in + vPutIn;
				throughput_out = throughput_out + vPutOut; 
				
			} else {
				long reportTimeConvert=nextTime;
//				if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//					reportTimeConvert = nextTime;
//				}
				
				vpn_throughput_in.add(new TextItem(
						df.format(((float)throughput_in)*8/1024/timePerPoint),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				vpn_throughput_out.add(new TextItem(
						df.format(((float)throughput_out*8/1024/timePerPoint)),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				vAputIn=vAputIn + throughput_in;
				vAputOut=vAputOut+ throughput_out;
				vLputIn=vLputIn==-1? throughput_in: (vLputIn<throughput_in? vLputIn: throughput_in);
				vLputOut=vLputOut==-1? throughput_out: (vLputOut<throughput_out? vLputOut: throughput_out);
				vHputIn=vHputIn>throughput_in?vHputIn: throughput_in;
				vHputOut=vHputOut>throughput_out?vHputOut: throughput_out;
				
				throughput_in=0;
				throughput_out=0;
				
				index++;
				currentTime = currentTime + timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;
				
				while (recTime> currentTime){
					if (recTime<=nextTime) {
						throughput_in = throughput_in + vPutIn;
						throughput_out = throughput_out + vPutOut; 
						break;
					} else {
						reportTimeConvert=nextTime;
//						if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//							reportTimeConvert = nextTime;
//						}
						vpn_throughput_in.add(new TextItem(
								df.format(((float)throughput_in)*8/1024/timePerPoint),
								AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
						vpn_throughput_out.add(new TextItem(
								df.format(((float)throughput_out*8/1024/timePerPoint)),
								AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
						vAputIn=vAputIn + throughput_in;
						vAputOut=vAputOut+ throughput_out;
						vLputIn=vLputIn==-1? throughput_in: (vLputIn<throughput_in? vLputIn: throughput_in);
						vLputOut=vLputOut==-1? throughput_out: (vLputOut<throughput_out? vLputOut: throughput_out);
						vHputIn=vHputIn>throughput_in?vHputIn: throughput_in;
						vHputOut=vHputOut>throughput_out?vHputOut: throughput_out;
						
						throughput_in=0;
						throughput_out=0;
						index++;
						currentTime = currentTime + timeTigg * 60 *1000L;
						nextTime=currentTime + timeTigg * 60 *1000L;
					}
				}
			}
		}
		if (index!=0) {
			long reportTimeConvert=nextTime;
//			if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//				reportTimeConvert = nextTime;
//			}
			vpn_throughput_in.add(new TextItem(
					df.format(((float)throughput_in)*8/1024/timePerPoint),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			vpn_throughput_out.add(new TextItem(
					df.format(((float)throughput_out*8/1024/timePerPoint)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			
			vAputIn=vAputIn + throughput_in;
			vAputOut=vAputOut+ throughput_out;
			vLputIn=Double.valueOf(df.format(vLputIn==-1? ((float)throughput_in)*8/1024/timePerPoint: (vLputIn<throughput_in? vLputIn*8/1024/timePerPoint: ((float)throughput_in)*8/1024/timePerPoint)));
			vLputOut=Double.valueOf(df.format(vLputOut==-1? ((float)throughput_out)*8/1024/timePerPoint: (vLputOut<throughput_out? vLputOut*8/1024/timePerPoint: ((float)throughput_out)*8/1024/timePerPoint)));
			vHputIn=Double.valueOf(df.format(vHputIn>throughput_in?vHputIn*8/1024/timePerPoint: ((float)throughput_in)*8/1024/timePerPoint));
			vHputOut=Double.valueOf(df.format(vHputOut>throughput_out?vHputOut*8/1024/timePerPoint: ((float)throughput_out)*8/1024/timePerPoint));
			
			throughput_in=0;
			throughput_out=0;

			index++;
			currentTime = currentTime + timeTigg * 60 *1000L;
			nextTime=currentTime + timeTigg * 60 *1000L;
		
		}
		
		if (currentTime==0) {
			currentTime = lastRecordTime;
			nextTime=currentTime + timeTigg * 60 *1000L;
			throughput_in=0;
			throughput_out=0;
		}
		
		while (nextTime <=lastRecordTime + timeTigg * 60 *1000L * hourTigg) {
			long reportTimeConvert=nextTime;
			
			vpn_throughput_in.add(new TextItem(
					df.format(((float)throughput_in)*8/1024/timePerPoint),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			vpn_throughput_out.add(new TextItem(
					df.format(((float)throughput_out*8/1024/timePerPoint)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			vAputIn=vAputIn + throughput_in;
			vAputOut=vAputOut+ throughput_out;
			vLputIn=vLputIn==-1? throughput_in: (vLputIn<throughput_in? vLputIn: throughput_in);
			vLputOut=vLputOut==-1? throughput_out: (vLputOut<throughput_out? vLputOut: throughput_out);
			vHputIn=vHputIn>throughput_in?vHputIn: throughput_in;
			vHputOut=vHputOut>throughput_out?vHputOut: throughput_out;
			
			throughput_in=0;
			throughput_out=0;

			index++;
			currentTime = currentTime + timeTigg * 60 *1000L;
			nextTime=currentTime + timeTigg * 60 *1000L;
			
		}
		
		if (index!=0) {
			vAputIn = Double.valueOf(df.format(vAputIn/(index)*8/1024/timePerPoint));
			vAputOut = Double.valueOf(df.format(vAputOut/(index)*8/1024/timePerPoint));
		}

	}
	
	public void setGwVpnAvailablity(Calendar reportDateTime) {
		long timeTigg=0;
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			timeTigg=1;
			vTitle="VPN Availability: Last 60 Minutes";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEDAY) {
			timeTigg=60;
			vTitle="VPN Availability: Last One Day";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_TWODAY) {
			timeTigg=120;
			vTitle="VPN Availability: Last Two Days";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_THREEDAY) {
			timeTigg=180;
			vTitle="VPN Availability: Last Three Days";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEWEEK) {
			timeTigg=420;
			vTitle="VPN Availability: Last One Week";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_TWOWEEK) {
			timeTigg=840;
			vTitle="VPN Availability: Last Two Weeks";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_THREEWEEK) {
			timeTigg=1260;
			vTitle="VPN Availability: Last Three Weeks";
		} else if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEMONTH) {
			timeTigg=1800;
			vTitle="VPN Availability: Last One Month";
		}
		
		hiveApNameTunnelCountMap = (HashMap<String,String>)MgrUtil.getSessionAttribute(REPORT_HIVEAPNAME_TUNNELCOUNT_MAP);
		String strTotalTunnelCount = hiveApNameTunnelCountMap.get(reportAPName);
		if (strTotalTunnelCount==null || strTotalTunnelCount.equals("0")){
			return;
		}
		
		hiveApNameMacMap = (HashMap<String,String>)MgrUtil.getSessionAttribute(REPORT_HIVEAPNAME_MAC_MAP);
		DecimalFormat df = new DecimalFormat("0.00");
		String searchSQL = "lower(mac)=:s1";

		Object values[] = new Object[1];
//		values[0] = reportDateTime.getTimeInMillis();
		values[0] = hiveApNameMacMap.get(reportAPName)==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(reportAPName);
		List<?> lstInterfaceInfo=null;
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			lstInterfaceInfo = QueryUtil.executeQuery(AhStatsVpnStatusHigh.class, new SortParams("time"),
					new FilterParams(searchSQL, values), getDomain().getId());
		} else {
			lstInterfaceInfo = QueryUtil.executeQuery(AhStatsVpnStatusLow.class, new SortParams("time"),
					new FilterParams(searchSQL, values), getDomain().getId());
		}
		
		int totalBarCount=0;
		if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
			totalBarCount=60;
		} else {
			totalBarCount=24;
		}
		
//		int[] totalCount= new int[60];
		int[] upCount= new int[totalBarCount];
		
		int index=0;
		long currentTime=0;
		long nextTime=0;
		long oneTimeRecordCount=0;
		long totalTimeRecordtime=0;
		long totalUptime=0;
		long tunnelCount = Integer.parseInt(strTotalTunnelCount);
		vpn_uptime= new ArrayList<TextItem>();
		vpn_availability= new ArrayList<TextItem>();

		long lastRecordTime=System.currentTimeMillis();
		if (!lstInterfaceInfo.isEmpty()) {
			if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
				lastRecordTime = ((AhStatsVpnStatusHigh)lstInterfaceInfo.get(lstInterfaceInfo.size()-1)).getTime();
			} else {
				lastRecordTime = ((AhStatsVpnStatusLow)lstInterfaceInfo.get(lstInterfaceInfo.size()-1)).getTime();
			}
		}
		lastRecordTime = lastRecordTime - timeTigg * 60 *1000L * totalBarCount;
		
		for(Object OneRec : lstInterfaceInfo) {
			long recTime;
			int recTunnelCount;
			if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
				recTime = ((AhStatsVpnStatusHigh)OneRec).getTime();
				recTunnelCount = ((AhStatsVpnStatusHigh)OneRec).getTunnelCount();
			} else {
				recTime = ((AhStatsVpnStatusLow)OneRec).getTime();
				recTunnelCount = ((AhStatsVpnStatusLow)OneRec).getTunnelCount();
			}
			
			if (recTunnelCount>tunnelCount) {
				recTunnelCount = Integer.parseInt(strTotalTunnelCount);
			}
			
			if (recTime<=lastRecordTime) {
				continue;
			} else if(currentTime==0 && recTime>lastRecordTime + timeTigg * 60 *1000L) {
				currentTime = lastRecordTime + (index) * timeTigg * 60 *1000L;
				nextTime=currentTime + timeTigg * 60 *1000L;
				if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					oneTimeRecordCount = 1;
				} else {
					oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
				}
				totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
				
				while (recTime>lastRecordTime + (index +1) * timeTigg * 60 *1000L){
					long reportTimeConvert=nextTime;
//					if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//						reportTimeConvert = nextTime;
//					}
					vpn_availability.add(new TextItem(
							df.format(((float)upCount[index] *100)/(oneTimeRecordCount * tunnelCount)),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					
					vpn_uptime.add(new TextItem(
							df.format(((float)totalUptime *100)/(totalTimeRecordtime * tunnelCount)),
							AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
					
					if (index <(totalBarCount-1)){
						index++;
						currentTime = currentTime + timeTigg * 60 *1000L;
						nextTime=currentTime + timeTigg * 60 *1000L;
						if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
							oneTimeRecordCount = 1;
						} else {
							oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
						}
						totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
					} else {
						break;
					}
				}
			}
			
			if (currentTime==0) {
				currentTime = lastRecordTime;
				nextTime=currentTime + timeTigg * 60 *1000L;
				if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					oneTimeRecordCount = 1;
				} else {
					oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
				}
				totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
			}
			
			if (recTime <= nextTime) {
				upCount[index] = upCount[index] + recTunnelCount;
				totalUptime = totalUptime + recTunnelCount;

			} else {
				long reportTimeConvert=nextTime;
//				if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//					reportTimeConvert = nextTime;
//				}
				vpn_availability.add(new TextItem(
						df.format(((float)upCount[index] *100)/(oneTimeRecordCount * tunnelCount)),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				
				vpn_uptime.add(new TextItem(
						df.format(((float)totalUptime *100)/(totalTimeRecordtime * tunnelCount)),
						AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
				if (index<(totalBarCount-1)){
					index++;
					currentTime = currentTime + timeTigg * 60 *1000L;
					nextTime=currentTime + timeTigg * 60 *1000L;
					if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
						oneTimeRecordCount = 1;
					} else {
						oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
					}
					totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
					
					while (recTime>currentTime && index <totalBarCount){
						if (recTime<=nextTime) {
							upCount[index] = upCount[index] + recTunnelCount;
							totalUptime = totalUptime + recTunnelCount;
							break;
						} else {
							reportTimeConvert=nextTime;
//							if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//								reportTimeConvert = nextTime;
//							}
							vpn_availability.add(new TextItem(
									df.format(((float)upCount[index] *100)/(oneTimeRecordCount * tunnelCount)),
									AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
							
							vpn_uptime.add(new TextItem(
									df.format(((float)totalUptime *100)/(totalTimeRecordtime * tunnelCount)),
									AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
							if (index <(totalBarCount-1)){
								index++;
								currentTime = currentTime + timeTigg * 60 *1000L;
								nextTime=currentTime + timeTigg * 60 *1000L;
								if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
									oneTimeRecordCount = 1;
								} else {
									oneTimeRecordCount = (nextTime - currentTime)/(60*60*1000L);
								}
								totalTimeRecordtime = totalTimeRecordtime + oneTimeRecordCount;
							} else {
								break;
							}
						}
					}
				}
			}
		}
		if (vpn_availability.size()<totalBarCount && index!=0) {
			long reportTimeConvert=nextTime;
//			if (getDataSource().getReportPeriod()!=AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
//				reportTimeConvert = nextTime;
//			}
			vpn_availability.add(new TextItem(
					df.format(((float)upCount[index] *100)/(oneTimeRecordCount * tunnelCount)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
			
			vpn_uptime.add(new TextItem(
					df.format(((float)totalUptime *100)/(totalTimeRecordtime * tunnelCount)),
					AhDateTimeUtil.getSpecifyDateTimeReport(reportTimeConvert,tz)));
		}
		if (totalTimeRecordtime!=0) {
			vUptime=df.format(((float)totalUptime *100)/(totalTimeRecordtime * tunnelCount));
			vDowntime=strTotalTunnelCount;
			vDowntimeNumber=(int)(totalTimeRecordtime* tunnelCount-totalUptime);
		}
		
	}
	public void setGwWanAvailablity(Calendar reportDateTime) {}
	public void setGwWanThroughput(Calendar reportDateTime) {}
	
	public long checkValueLessThanZero(long value) {
		if (value < 0) {
			return 0;
		}
		return value;
	}

	public long checkValueLessThanZero(long value1, long value2) {
		if (value1 - value2 < 0) {
			return value1;
		}
		return value1 - value2;
	}

	public double checkValueLessThanZero(double value1, double value2) {
		if (value1 - value2 < 0) {
			return value1;
		}
		return value1 - value2;
	}

	private String startTime;
	private int startHour;

	private boolean showReportTab;

	private String swf, width, height, application, bgcolor;

	private Set<String> lstHiveAPName;

	private String reportAPName;

	private String reportTunnelName;

	private List<String> lstReportTunnelName;
	
	private Map<String, String> hiveApNameMacMap;
	
	private Map<String, String> hiveApNameTunnelCountMap;

	private String tabIndex = "";

	private String selectHiveAPName;

	public AhReport getDataSource() {
		return (AhReport) dataSource;
	}

	public boolean getShowReportTab() {
		return showReportTab;
	}

	public String getChangedReportName() {
		return getDataSource().getName().replace("\\", "\\\\").replace("'", "\\'");
	}

	public String getRunReportTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(tz);
		return formatter.format(new Date());
	}

	public int getNameLength() {
		return getAttributeLength("name");
	}
	
	public List<TextItem> getEnumReportType(){
		List<TextItem> typeList = new ArrayList<TextItem>();
		if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_VPNAVAILABILITY)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_VPNTHROUGHPUT)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_VPNLATENCY)){
			typeList.add(new TextItem(L2_FEATURE_REPORT_VPNAVAILABILITY,"VPN Availablity"));
			typeList.add(new TextItem(L2_FEATURE_REPORT_VPNTHROUGHPUT,"VPN Throughput"));
			typeList.add(new TextItem(L2_FEATURE_REPORT_VPNLATENCY,"VPN Latency"));
		} else if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_WANAVAILABILITY)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_WANTHROUGHPUT)){
			typeList.add(new TextItem(L2_FEATURE_REPORT_WANAVAILABILITY,"WAN Availablity"));
			typeList.add(new TextItem(L2_FEATURE_REPORT_WANTHROUGHPUT,"WAN Throughput"));
		} else if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_GWWANAVAILABILITY)
					||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_GWWANTHROUGHPUT)){
				typeList.add(new TextItem(L2_FEATURE_REPORT_GWWANAVAILABILITY,"WAN Availablity"));
				typeList.add(new TextItem(L2_FEATURE_REPORT_GWWANTHROUGHPUT,"WAN Throughput"));
		} else if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_GWVPNAVAILABILITY)){
			typeList.add(new TextItem(L2_FEATURE_REPORT_GWVPNAVAILABILITY,"VPN Availablity"));
		}
		return typeList;
	}
	
	public String getShowReportType(){
		if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_VPNAVAILABILITY)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_VPNTHROUGHPUT)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_VPNLATENCY)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_WANAVAILABILITY)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_WANTHROUGHPUT)
				
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_GWVPNAVAILABILITY)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_GWWANAVAILABILITY)
				||getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_REPORT_GWWANTHROUGHPUT)){
			return "";
		}
		
		return "none";
	}


	public EnumItem[] getEnumReportPeriod() {
		return AhReport.REPORT_PERIOD_VPN_TYPE;
	}

	public EnumItem[] getEnumWeekDay() {
		return AhReport.REPORT_WEEKDAY_TYPE;
	}

	public List<CheckItem> getLstHours() {
		List<CheckItem> lstHour = new ArrayList<CheckItem>();
		for (int i = 0; i < 24; i++) {
			lstHour.add(new CheckItem((long) i, String.valueOf(i + " hour")));
		}
		return lstHour;
	}

	public String getHideSchedule() {
		if (getDataSource().getExcuteType().equals("2")) {
			return "";
		} else {
			return "none";
		}
	}

	public String getShowRecurrence() {
		if (getDataSource().getEnabledRecurrence() && getDataSource().getExcuteType().equals("2")) {
			return "";
		} else {
			return "none";
		}
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public String getSwf() {
		return swf;
	}

	public String getWidth() {
		return width;
	}

	public String getHeight() {
		return height;
	}

	public String getApplication() {
		return application;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public Set<String> getLstHiveAPName() {
		if (lstHiveAPName == null) {
			lstHiveAPName = new HashSet<String>();
			lstHiveAPName.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		}
		return lstHiveAPName;
	}

	public String getReportAPName() {
		if (reportAPName == null || reportAPName.equals("")) {
			return MgrUtil.getUserMessage("config.optionsTransfer.none");
		}
		return reportAPName;
	}


	public String getViewTunnel() {
		return "viewTunnel";
	}

	public String getSelectHiveAPName() {
		return selectHiveAPName;
	}

	public void setSelectHiveAPName(String selectHiveAPName) {
		this.selectHiveAPName = selectHiveAPName;
	}

	public String getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(String tabIndex) {
		this.tabIndex = tabIndex;
	}

	public String getReportTunnelName() {
		if (reportTunnelName == null || reportTunnelName.equals("")) {
			return MgrUtil.getUserMessage("config.optionsTransfer.none");
		}
		return reportTunnelName;
	}

	public List<String> getLstReportTunnelName() {
		if (lstReportTunnelName == null) {
			lstReportTunnelName = new ArrayList<String>();
			lstReportTunnelName.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		}
		return lstReportTunnelName;
	}

	public void setReportAPName(String reportAPName) {
		this.reportAPName = reportAPName;
	}

	public void setReportTunnelName(String reportTunnelName) {
		this.reportTunnelName = reportTunnelName;
	}

	
	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof AhReport) {
			dataSource = bo;
			if (getDataSource().getOwner() != null) {
				getDataSource().getOwner().getId();
				getDataSource().getOwner().getDomainName();
			}
		}
		if (bo instanceof HiveAp) {
			HiveAp hiveap = (HiveAp) bo;
			if (hiveap.getConfigTemplate() != null) {
				hiveap.getConfigTemplate().getId();
				hiveap.getConfigTemplate().getHiveProfile().getId();
				hiveap.getConfigTemplate().getSsidInterfaces().values();
				hiveap.getConfigTemplate().getEth0ServiceFilter().getId();
				hiveap.getConfigTemplate().getEth1ServiceFilter().getId();
				hiveap.getConfigTemplate().getRed0ServiceFilter().getId();
				hiveap.getConfigTemplate().getAgg0ServiceFilter().getId();
				hiveap.getConfigTemplate().getEth0BackServiceFilter().getId();
				hiveap.getConfigTemplate().getEth1BackServiceFilter().getId();
				hiveap.getConfigTemplate().getRed0BackServiceFilter().getId();
				hiveap.getConfigTemplate().getAgg0BackServiceFilter().getId();
			}
		}
		return null;
	}

	// struts download support
	private final String mailFileName = "currentReportData.csv";
//	private final String mailFileNamePdf = "currentReportData.pdf";
//	private static String clientExportFileName = "currentReportData.csv";
	
	public String getInputPath() {
		return AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
				+ getDomain().getDomainName() + File.separator + mailFileName;
	}

	public String getLocalFileName() {
		return mailFileName;
	}

	public InputStream getInputStream() throws Exception {
		return new FileInputStream(getInputPath());
	}

	
	
	public synchronized boolean generalCurrentCvsFile() {
		try {
			String currentFileDir = AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
					+ getDomain().getDomainName();
			File tmpFileDir = new File(currentFileDir);
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
			StringBuffer strOutput;
			File tmpFile = new File(currentFileDir + File.separator + mailFileName);
			FileWriter out = new FileWriter(tmpFile);
			hiveApNameMacMap = (HashMap<String,String>)MgrUtil.getSessionAttribute(REPORT_HIVEAPNAME_MAC_MAP);
			if (hiveApNameMacMap.get(reportAPName)==null) {
				return false;
			}
			
			long  retLong = AhAppContainer.HmBe.getPerformModule().getBeInterfaceReportProcessor().fetchReportData(hiveApNameMacMap.get(reportAPName), getDataSource().getReportPeriod());
			if (retLong!=BeInterfaceReportProcessor.QUERY_OK && retLong!=BeInterfaceReportProcessor.QUERY_DONE){
				return false;
			}
			
			//getDataFromAP();
			if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNAVAILABILITY)) {
				
				String searchSQL = "lower(mac)=:s1 and interfType=:s2";

				Object values[] = new Object[2];
				values[0] = hiveApNameMacMap.get(reportAPName)==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(reportAPName);
				values[1] = AhPortAvailability.INTERFACE_TYPE_VPN;
				List<?> lstInterfaceInfo=null;
				if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsLatencyHigh.class, new SortParams("interfServer, time"),
							new FilterParams(searchSQL, values), getDataSource().getOwner().getId());
				} else {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsLatencyLow.class, new SortParams("interfServer, time"),
							new FilterParams(searchSQL, values), getDataSource().getOwner().getId());
				}
				
				strOutput = new StringBuffer();
				strOutput.append("Device Name").append(",");
				strOutput.append("Tunnel Name,");
				strOutput.append("Server,");
				strOutput.append("Destination Name,");
				strOutput.append("Status,");
				strOutput.append("Report Time");
				strOutput.append("\n");
				out.write(strOutput.toString());

				strOutput = new StringBuffer();
				long recTime;
				byte recStatus;
				String recServerIp;
				String name;
				Map<String, String> tunnelServerIPNameMap = (Map<String, String>)MgrUtil.getSessionAttribute(REPORT_TUNNELSERVERIP_NAME_MAP);
				Map<String, String> tunnelNameIPMap = (Map<String, String>)MgrUtil.getSessionAttribute(REPORT_TUNNELNAMEIP_MAP);
				for(Object OneRec : lstInterfaceInfo) {
					if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
						recTime = ((AhStatsLatencyHigh)OneRec).getTime();
						recStatus = ((AhStatsLatencyHigh)OneRec).getTargetStatus();
						recServerIp = ((AhStatsLatencyHigh)OneRec).getInterfServer();
						name = ((AhStatsLatencyHigh)OneRec).getName();
					} else {
						recTime = ((AhStatsLatencyLow)OneRec).getTime();
						recStatus = ((AhStatsLatencyLow)OneRec).getTargetStatus();
						recServerIp = ((AhStatsLatencyLow)OneRec).getInterfServer();
						name = ((AhStatsLatencyLow)OneRec).getName();
					}
					
					if (reportTunnelName.equals("All")){
						strOutput.append(reportAPName).append(",");
						strOutput.append(tunnelServerIPNameMap.get(reportAPName+ "-_-" + recServerIp)==null? recServerIp : tunnelServerIPNameMap.get(reportAPName+ "-_-" + recServerIp)).append(",");
						strOutput.append(recServerIp).append(",");
						strOutput.append(name).append(",");
						strOutput.append(recStatus==AhStatsLatencyHigh.TARGET_STATUS_UP? "Up" : "Down").append(",");
						strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(recTime,tz));
						strOutput.append("\n");
					} else {
						if (recServerIp.equals(tunnelNameIPMap.get(reportAPName + "-_-" + reportTunnelName))) {
							strOutput.append(reportAPName).append(",");
							strOutput.append(tunnelServerIPNameMap.get(reportAPName+ "-_-" + recServerIp)==null? recServerIp : tunnelServerIPNameMap.get(reportAPName+ "-_-" + recServerIp)).append(",");
							strOutput.append(recServerIp).append(",");
							strOutput.append(name).append(",");
							strOutput.append(recStatus==AhStatsLatencyHigh.TARGET_STATUS_UP? "Up" : "Down").append(",");
							strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(recTime,tz));
							strOutput.append("\n");
						}
					}
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNTHROUGHPUT)) {
				DecimalFormat df = new DecimalFormat("0.00");
				String searchSQL = "lower(mac)=:s1 and interfType=:s2";

				Object values[] = new Object[2];
				values[0] = hiveApNameMacMap.get(reportAPName)==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(reportAPName);
				values[1] = AhPortAvailability.INTERFACE_TYPE_VPN;
				List<?> lstInterfaceInfo=null;
				if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputHigh.class, new SortParams("interfName, time"),
							new FilterParams(searchSQL, values), getDomain().getId());
				} else {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputLow.class, new SortParams("interfName, time"),
							new FilterParams(searchSQL, values), getDomain().getId());
				}
				strOutput = new StringBuffer();
				strOutput.append("Device Name").append(",");
				strOutput.append("Tunnel Name,");
				strOutput.append("Server,");
				strOutput.append("Data In (kbps),");
				strOutput.append("Data Out (kbps),");
				strOutput.append("Report Time");
				strOutput.append("\n");
				out.write(strOutput.toString());
				
				strOutput = new StringBuffer();
				long recTime;
				String dataIn;
				String dataOut;
				String recServerIp;
				Map<String, String> tunnelServerIPNameMap = (Map<String, String>)MgrUtil.getSessionAttribute(REPORT_TUNNELSERVERIP_NAME_MAP);
				Map<String, String> tunnelNameIPMap = (Map<String, String>)MgrUtil.getSessionAttribute(REPORT_TUNNELNAMEIP_MAP);
				
				for(Object OneRec : lstInterfaceInfo) {
					if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
						recTime = ((AhStatsThroughputHigh)OneRec).getTime();
						dataIn = df.format(((AhStatsThroughputHigh)OneRec).getRxBytes()*8/(float)1024/60);
						dataOut = df.format(((AhStatsThroughputHigh)OneRec).getTxBytes()*8/(float)1024/60);
						recServerIp = ((AhStatsThroughputHigh)OneRec).getInterfServer();
					} else {
						recTime = ((AhStatsThroughputLow)OneRec).getTime();
						dataIn = df.format(((AhStatsThroughputLow)OneRec).getRxBytes()*8/(float)1024/3600);
						dataOut = df.format(((AhStatsThroughputLow)OneRec).getTxBytes()*8/(float)1024/3600);
						recServerIp = ((AhStatsThroughputLow)OneRec).getInterfServer();
					}
					if (reportTunnelName.equals("All")){
						strOutput.append(reportAPName).append(",");
						strOutput.append(tunnelServerIPNameMap.get(reportAPName+ "-_-" + recServerIp)==null? recServerIp : tunnelServerIPNameMap.get(reportAPName+ "-_-" + recServerIp)).append(",");
						strOutput.append(recServerIp).append(",");
						strOutput.append(dataIn).append(",");
						strOutput.append(dataOut).append(",");
						strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(recTime,tz));
						strOutput.append("\n");
					} else {
						if (recServerIp.equals(tunnelNameIPMap.get(reportAPName + "-_-" + reportTunnelName))) {
							strOutput.append(reportAPName).append(",");
							strOutput.append(tunnelServerIPNameMap.get(reportAPName+ "-_-" + recServerIp)==null? recServerIp : tunnelServerIPNameMap.get(reportAPName+ "-_-" + recServerIp)).append(",");
							strOutput.append(recServerIp).append(",");
							strOutput.append(dataIn).append(",");
							strOutput.append(dataOut).append(",");
							strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(recTime,tz));
							strOutput.append("\n");
						}
					}
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_VPNLATENCY)) {
				String searchSQL = "lower(mac)=:s1 and interfType=:s2";

				Object values[] = new Object[2];
				values[0] = hiveApNameMacMap.get(reportAPName)==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(reportAPName);
				values[1] = AhPortAvailability.INTERFACE_TYPE_VPN;
				List<?> lstInterfaceInfo=null;
				if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsLatencyHigh.class, new SortParams("interfServer, time"),
							new FilterParams(searchSQL, values), getDomain().getId());
				} else {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsLatencyLow.class, new SortParams("interfServer, time"),
							new FilterParams(searchSQL, values), getDomain().getId());
				}
				
				strOutput = new StringBuffer();
				strOutput.append("Device Name").append(",");
				strOutput.append("Tunnel Name,");
				strOutput.append("Server,");
				strOutput.append("Destination Name,");
				strOutput.append("Rtt (msec),");
				strOutput.append("Report Time");
				strOutput.append("\n");
				out.write(strOutput.toString());
				
				strOutput = new StringBuffer();
				long recTime;
				double rtt;
				String recServerIp;
				String name;
				Map<String, String> tunnelServerIPNameMap = (Map<String, String>)MgrUtil.getSessionAttribute(REPORT_TUNNELSERVERIP_NAME_MAP);
				Map<String, String> tunnelNameIPMap = (Map<String, String>)MgrUtil.getSessionAttribute(REPORT_TUNNELNAMEIP_MAP);
				
				for(Object OneRec : lstInterfaceInfo) {
					if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
						recTime = ((AhStatsLatencyHigh)OneRec).getTime();
						if (((AhStatsLatencyHigh)OneRec).getTargetStatus()==AhStatsLatencyHigh.TARGET_STATUS_DOWN) {
							rtt = -1;
						} else {
							rtt = ((AhStatsLatencyHigh)OneRec).getRtt();
						}
						recServerIp = ((AhStatsLatencyHigh)OneRec).getInterfServer();
						name= ((AhStatsLatencyHigh)OneRec).getName();
					} else {
						recTime = ((AhStatsLatencyLow)OneRec).getTime();
						if (((AhStatsLatencyLow)OneRec).getTargetStatus()==AhStatsLatencyHigh.TARGET_STATUS_DOWN) {
							rtt = -1;
						} else {
							rtt = ((AhStatsLatencyLow)OneRec).getRtt();
						}
						recServerIp = ((AhStatsLatencyLow)OneRec).getInterfServer();
						name= ((AhStatsLatencyHigh)OneRec).getName();
					}
					if (reportTunnelName.equals("All")){
						strOutput.append(reportAPName).append(",");
						strOutput.append(tunnelServerIPNameMap.get(reportAPName+ "-_-" + recServerIp)==null? recServerIp : tunnelServerIPNameMap.get(reportAPName+ "-_-" + recServerIp)).append(",");
						strOutput.append(recServerIp).append(",");
						strOutput.append(name).append(",");
						strOutput.append(rtt).append(",");
						strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(recTime,tz));
						strOutput.append("\n");
					} else {
						if (recServerIp.equals(tunnelNameIPMap.get(reportAPName + "-_-" + reportTunnelName))) {
							strOutput.append(reportAPName).append(",");
							strOutput.append(tunnelServerIPNameMap.get(reportAPName+ "-_-" + recServerIp)==null? recServerIp : tunnelServerIPNameMap.get(reportAPName+ "-_-" + recServerIp)).append(",");
							strOutput.append(recServerIp).append(",");
							strOutput.append(name).append(",");
							strOutput.append(rtt).append(",");
							strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(recTime,tz));
							strOutput.append("\n");
						}
					}
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_WANAVAILABILITY)
					|| getDataSource().getReportType().equals(L2_FEATURE_REPORT_GWWANAVAILABILITY)) {
				String searchSQL = "lower(mac)=:s1";

				Object values[] = new Object[1];
				values[0] = hiveApNameMacMap.get(reportAPName)==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(reportAPName);
				List<?> lstInterfaceInfo=null;
				if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsAvailabilityHigh.class, new SortParams("interfName, time"),
							new FilterParams(searchSQL, values), getDomain().getId());
				} else {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsAvailabilityLow.class, new SortParams("interfName, time"),
							new FilterParams(searchSQL, values), getDomain().getId());
				}
				
				strOutput = new StringBuffer();
				strOutput.append("Device Name").append(",");
				strOutput.append("Interface Name,");
				strOutput.append("Status,");
				strOutput.append("Report Time");
				strOutput.append("\n");
				out.write(strOutput.toString());
				
				strOutput = new StringBuffer();
				long recTime;
				byte recStatus;
				String recName;
				for(Object OneRec : lstInterfaceInfo) {
					if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
						recTime = ((AhStatsAvailabilityHigh)OneRec).getTime();
						recStatus = ((AhStatsAvailabilityHigh)OneRec).getInterfStatus();
						recName = ((AhStatsAvailabilityHigh)OneRec).getInterfName();
					} else {
						recTime = ((AhStatsAvailabilityLow)OneRec).getTime();
						recStatus = ((AhStatsAvailabilityLow)OneRec).getInterfStatus();
						recName = ((AhStatsAvailabilityLow)OneRec).getInterfName();
					}
				
					strOutput.append(reportAPName.toString()).append(",");
					strOutput.append(recName).append(",");
					strOutput.append(recStatus==AhPortAvailability.INTERFACE_STATUS_UP? "Up" : "Down").append(",");
					strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(recTime,tz));
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
				
			} else if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_WANTHROUGHPUT)
					|| getDataSource().getReportType().equals(L2_FEATURE_REPORT_GWWANTHROUGHPUT)) {
				DecimalFormat df = new DecimalFormat("0.00");
				String searchSQL = "lower(mac)=:s1 and interfType=:s2";

				Object values[] = new Object[2];
				values[0] = hiveApNameMacMap.get(reportAPName)==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(reportAPName);
				values[1] = AhPortAvailability.INTERFACE_TYPE_WAN;
				List<?> lstInterfaceInfo=null;
				if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputHigh.class, new SortParams("interfName, time"),
							new FilterParams(searchSQL, values), getDomain().getId());
				} else {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsThroughputLow.class, new SortParams("interfName, time"),
							new FilterParams(searchSQL, values), getDomain().getId());
				}
				strOutput = new StringBuffer();
				strOutput.append("Device Name").append(",");
				strOutput.append("Interface Name,");
				strOutput.append("Data In (kbps),");
				strOutput.append("Data Out (kbps),");
				strOutput.append("Report Time");
				strOutput.append("\n");
				out.write(strOutput.toString());
				
				strOutput = new StringBuffer();
				long recTime;
				String dataIn;
				String dataOut;
				String ifName;
				for(Object OneRec : lstInterfaceInfo) {
					if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
						recTime = ((AhStatsThroughputHigh)OneRec).getTime();
						dataIn = df.format(((AhStatsThroughputHigh)OneRec).getRxBytes()*8/(float)1024/60);
						dataOut = df.format(((AhStatsThroughputHigh)OneRec).getTxBytes()*8/(float)1024/60);
						ifName = ((AhStatsThroughputHigh)OneRec).getInterfName();
					} else {
						recTime = ((AhStatsThroughputLow)OneRec).getTime();
						dataIn = df.format(((AhStatsThroughputLow)OneRec).getRxBytes()*8/(float)1024/3600);
						dataOut = df.format(((AhStatsThroughputLow)OneRec).getTxBytes()*8/(float)1024/3600);
						ifName = ((AhStatsThroughputLow)OneRec).getInterfName();
					}
				
					strOutput.append(reportAPName.toString()).append(",");
					strOutput.append(ifName).append(",");
					strOutput.append(dataIn).append(",");
					strOutput.append(dataOut).append(",");
					strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(recTime,tz));
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			} else if (getDataSource().getReportType().equals(L2_FEATURE_REPORT_GWVPNAVAILABILITY)) {
				String searchSQL = "lower(mac)=:s1";

				Object values[] = new Object[1];
				values[0] = hiveApNameMacMap.get(reportAPName)==null? MgrUtil.getUserMessage("config.optionsTransfer.none"):hiveApNameMacMap.get(reportAPName);
				List<?> lstInterfaceInfo=null;
				if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsVpnStatusHigh.class, new SortParams("time"),
							new FilterParams(searchSQL, values), getDomain().getId());
				} else {
					lstInterfaceInfo = QueryUtil.executeQuery(AhStatsVpnStatusLow.class, new SortParams("time"),
							new FilterParams(searchSQL, values), getDomain().getId());
				}
				
				hiveApNameTunnelCountMap = (HashMap<String,String>)MgrUtil.getSessionAttribute(REPORT_HIVEAPNAME_TUNNELCOUNT_MAP);
				String strTotalTunnelCount = hiveApNameTunnelCountMap.get(reportAPName);
				if (strTotalTunnelCount==null || strTotalTunnelCount.equals("0")){
					strTotalTunnelCount="0";
				}
				
				strOutput = new StringBuffer();
				strOutput.append("Device Name").append(",");
				strOutput.append("Mac Address,");
				strOutput.append("Tunnels Count,");
				strOutput.append("UP Tunnels Count,");
				strOutput.append("Report Time");
				strOutput.append("\n");
				out.write(strOutput.toString());
				
				strOutput = new StringBuffer();
				long recTime;
				int recTunnelCount;
				String recMac;
				for(Object OneRec : lstInterfaceInfo) {
					if (getDataSource().getReportPeriod()==AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
						recTime = ((AhStatsVpnStatusHigh)OneRec).getTime();
						recTunnelCount = ((AhStatsVpnStatusHigh)OneRec).getTunnelCount();
						recMac = ((AhStatsVpnStatusHigh)OneRec).getMac();
					} else {
						recTime = ((AhStatsVpnStatusLow)OneRec).getTime();
						recTunnelCount = ((AhStatsVpnStatusHigh)OneRec).getTunnelCount();
						recMac = ((AhStatsVpnStatusLow)OneRec).getMac();
					}
				
					strOutput.append(reportAPName.toString()).append(",");
					strOutput.append(recMac).append(",");
					strOutput.append(strTotalTunnelCount).append(",");
					strOutput.append(recTunnelCount).append(",");
					strOutput.append(AhDateTimeUtil.getSpecifyDateTimeReport(recTime,tz));
					strOutput.append("\n");
				}
				out.write(strOutput.toString());
			}
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("exportCurrentData in report:", e);
			return false;
		}
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public boolean getEmailUsabled() {
		return !getUpdateDisabled().equals("");
	}

	public String convertRateToM(int rateValue){
		if (rateValue%1000==0) {
			return String.valueOf(rateValue/1000);
		} else {
			return String.valueOf(((float)rateValue)/1000);
		}
	}

	

	public String getButtonType() {
		return buttonType;
	}

	public void setButtonType(String buttonType) {
		this.buttonType = buttonType;
	}

	public String getTempTableSuffix(){
		char[] tmpField = getUserContext().getEmailAddress().toCharArray();
		StringBuffer bufField = new StringBuffer();
		for (char field : tmpField) {
			if ((field >= 'a' && field <= 'z')
					|| (field >= 'A' && field <= 'Z')
					|| (field >= '0' && field <= '9')) {
				bufField.append(field);
			} else {
				bufField.append("_");
			}
		}
		
		if (bufField.toString().length()>36){
			return bufField.toString().substring(0, 36);
		}
		return bufField.toString();
	}

	public String getVTitle() {
		return vTitle;
	}

	public String getVUptime() {
		return vUptime;
	}

	public String getVDowntime() {
		return vDowntime;
	}

	public int getVDowntimeNumber() {
		return vDowntimeNumber;
	}

	public List<TextItem> getVpn_availability() {
		return vpn_availability;
	}

	public List<TextItem> getVpn_uptime() {
		return vpn_uptime;
	}

	public double getVAputIn() {
		return vAputIn;
	}

	public double getVAputOut() {
		return vAputOut;
	}

	public double getVLputIn() {
		return vLputIn<0? 0 : vLputIn;
	}

	public double getVLputOut() {
		return vLputOut<0? 0 : vLputOut;
	}

	public double getVHputIn() {
		return vHputIn;
	}

	public double getVHputOut() {
		return vHputOut;
	}

	public List<TextItem> getVpn_throughput_in() {
		return vpn_throughput_in;
	}

	public List<TextItem> getVpn_throughput_out() {
		return vpn_throughput_out;
	}

	public double getVALatency() {
		return vALatency;
	}

	public double getVSLatency() {
		return vSLatency<0 ? 0 : vSLatency;
	}

	public double getVHLatency() {
		return vHLatency<0 ? 0 : vHLatency;
	}

	public List<TextItem> getVpn_latency() {
		return vpn_latency;
	}
	
	public void addTestData() throws Exception{
		long time = System.currentTimeMillis();
		List<AhStatsAvailabilityHigh> lstAvh= new ArrayList<AhStatsAvailabilityHigh>();
		List<AhStatsThroughputHigh> lstthh= new ArrayList<AhStatsThroughputHigh>();
		List<AhStatsLatencyHigh> lstlh= new ArrayList<AhStatsLatencyHigh>();
		
		for(int i=0; i<60; i++) {
			AhStatsAvailabilityHigh avh = new AhStatsAvailabilityHigh();
			avh.setHostName("ah0001");
			avh.setInterfName("wan01");
			avh.setSid("ah0001");
			avh.setTime(time-i*1000*60);
			avh.setInterfStatus(AhPortAvailability.INTERFACE_STATUS_UP);
			avh.setOwner(getDomain());
			avh.setMac("ah0001");
			lstAvh.add(avh);
			
			AhStatsAvailabilityHigh avh1 = new AhStatsAvailabilityHigh();
			avh1.setHostName("ah0001");
			avh1.setInterfName("wan02");
			avh1.setSid("ah0001");
			avh1.setTime(time-i*1000*60);
			avh1.setInterfStatus(AhPortAvailability.INTERFACE_STATUS_DOWN);
			avh1.setOwner(getDomain());
			avh1.setMac("ah0001");
			lstAvh.add(avh1);
			
			AhStatsThroughputHigh th = new AhStatsThroughputHigh();
			th.setHostName("ah0001");
			th.setInterfName("tunnel0");
			th.setSid("ah0001");
			th.setTime(time-i*1000*60);
			th.setInterfServer("10.155.20.1");
			th.setInterfType(AhPortAvailability.INTERFACE_TYPE_VPN);
			th.setRxBytes(200000 + i*10000);
			th.setTxBytes(300000 + i*10000);
			th.setOwner(getDomain());
			th.setMac("ah0001");
			lstthh.add(th);
			
			AhStatsThroughputHigh th1 = new AhStatsThroughputHigh();
			th1.setHostName("ah0001");
			th1.setInterfName("tunnel1");
			th1.setSid("ah0001");
			th1.setTime(time-i*1000*60);
			th1.setInterfServer("10.155.20.2");
			th1.setInterfType(AhPortAvailability.INTERFACE_TYPE_VPN);
			th1.setRxBytes(400000 + i*10000);
			th1.setTxBytes(500000 + i*10000);
			th1.setOwner(getDomain());
			th1.setMac("ah0001");
			lstthh.add(th1);
			
			AhStatsThroughputHigh th2 = new AhStatsThroughputHigh();
			th2.setHostName("ah0001");
			th2.setInterfName("wan0");
			th2.setSid("ah0001");
			th2.setTime(time-i*1000*60);
			th2.setInterfType(AhPortAvailability.INTERFACE_TYPE_WAN);
			th2.setRxBytes(400000 + i*10000);
			th2.setTxBytes(500000 + i*10000);
			th2.setOwner(getDomain());
			th2.setMac("ah0001");
			lstthh.add(th2);
			
			AhStatsThroughputHigh th3 = new AhStatsThroughputHigh();
			th3.setHostName("ah0001");
			th3.setInterfName("wan1");
			th3.setSid("ah0001");
			th3.setTime(time-i*1000*60);
			th3.setInterfType(AhPortAvailability.INTERFACE_TYPE_WAN);
			th3.setRxBytes(200000 + i*10000);
			th3.setTxBytes(300000 + i*10000);
			th3.setOwner(getDomain());
			th3.setMac("ah0001");
			lstthh.add(th3);
			
			
			AhStatsLatencyHigh lh = new AhStatsLatencyHigh();
			lh.setHostName("ah0001");
			lh.setInterfName("tunnel0");
			lh.setSid("ah0001");
			lh.setTime(time-i*1000*60);
			lh.setInterfType(AhPortAvailability.INTERFACE_TYPE_VPN);
			lh.setRtt(30 + i);
			lh.setTargetStatus(AhStatsLatencyHigh.TARGET_STATUS_UP);
			lh.setInterfServer("10.155.20.1");
			lh.setOwner(getDomain());
			lh.setMac("ah0001");
			lstlh.add(lh);
			
			AhStatsLatencyHigh lh1 = new AhStatsLatencyHigh();
			lh1.setHostName("ah0001");
			lh1.setInterfName("tunnel1");
			lh1.setSid("ah0001");
			lh1.setTime(time-i*1000*60);
			lh1.setInterfType(AhPortAvailability.INTERFACE_TYPE_VPN);
			lh1.setRtt(50 + i);
			if (i%10==0) {
				lh1.setTargetStatus(AhStatsLatencyHigh.TARGET_STATUS_UP);
			} else {
				lh1.setTargetStatus(AhStatsLatencyHigh.TARGET_STATUS_DOWN);
			}
			lh1.setInterfServer("10.155.20.2");
			lh1.setOwner(getDomain());
			lh1.setMac("ah0001");
			lstlh.add(lh1);
			
			AhStatsLatencyHigh lh2 = new AhStatsLatencyHigh();
			lh2.setHostName("ah0001");
			lh2.setInterfName("eth0");
			lh2.setSid("ah0001");
			lh2.setTime(time-i*1000*60);
			lh2.setInterfType(AhPortAvailability.INTERFACE_TYPE_WAN);
			lh2.setRtt(50 + i);
			if (i%10==0) {
				lh1.setTargetStatus(AhStatsLatencyHigh.TARGET_STATUS_UP);
			} else {
				lh1.setTargetStatus(AhStatsLatencyHigh.TARGET_STATUS_DOWN);
			}
			lh2.setOwner(getDomain());
			lh2.setMac("ah0001");
			lstlh.add(lh2);
		}
		QueryUtil.bulkCreateBos(lstAvh);
		QueryUtil.bulkCreateBos(lstthh);
		QueryUtil.bulkCreateBos(lstlh);
	}

}