package com.ah.ui.actions.monitor;

/*
 * @author Fisher
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.be.parameter.BeParaModuleDefImpl;
import com.ah.be.performance.BeNetworkReportScheduleImpl;
import com.ah.be.performance.BeNetworkReportScheduleModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.performance.AhNewReport;
import com.ah.bo.report.impl.ClientsTopNReport;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.admin.NavigationCustomizationUtil;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.bo.report.AhReportInvoker;
import com.ah.util.bo.report.AhReportInvokerInterface;
import com.ah.util.bo.report.AhReportRequest;

public class NetworkUsageReportAction extends BaseAction implements QueryBo {
	
	private static final long serialVersionUID = 1L;

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;

	public static final int COLUMN_EMAILADDRESS = 2;
	
	public static final int COLUMN_REPORT_TYPE = 3;
	
	public static final int COLUMN_TIMEPERIOD = 4;
	
	public static final int COLUMN_DESCRIPTION = 5;

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
		case COLUMN_NAME:
			code = "report.reportList.name";
			break;
		case COLUMN_EMAILADDRESS:
			code = "report.reportList.emailAddress";
			break;
		case COLUMN_REPORT_TYPE:
			code = "report.reportList.title.type";
			break;
		case COLUMN_TIMEPERIOD:
			code = "report.reportList.reportPeriod";
			break;
		case COLUMN_DESCRIPTION:
			code = "report.reportList.clientAuth.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(5);

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_REPORT_TYPE));
		columns.add(new HmTableColumn(COLUMN_TIMEPERIOD));
		columns.add(new HmTableColumn(COLUMN_EMAILADDRESS));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	@Override
	public String execute() throws Exception {
		resetPermission();
		tz = getUserTimeZone();
		
		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.report.networkusage"))) {
					return getLstForward();
				}
				setSessionDataSource(new AhNewReport());
				prepareGuiSettings();
				getDataSource().setOwner(getDomain());
				return INPUT;
			} else if ("create".equals(operation)) {
				saveGuiValue();
				if (checkNameExists("name", getDataSource().getName())) {
					prepareGuiSettings();
					return INPUT;
				}
				return createBo();
			} else if ("edit".equals(operation)) {
				editBo();
				if (dataSource != null) {
					if (getDataSource().getId() != null) {
						dataSource = findBoById(AhNewReport.class, getDataSource().getId());
					}
					setSessionDataSource(dataSource);
				}
				prepareGuiSettings();
				return INPUT;
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				AhNewReport ahReport = (AhNewReport) findBoById(boClass, cloneId);
				if (ahReport.getDefaultFlag() 
						&& ahReport.getName().equals(BeParaModuleDefImpl.DEFAULT_NETWORK_REPORT_SAMPLE)) {
					baseOperation();
					addActionError(MgrUtil.getUserMessage("error.clone.sample.data"));
					return prepareBoList();
				}
				ahReport.setId(null);
				ahReport.setOwner(getDomain());
				if (ahReport.getDefaultFlag()) {
					ahReport.setDescription("");
				}
				ahReport.setDefaultFlag(false);
				ahReport.setName("");
				setSessionDataSource(ahReport);
				prepareGuiSettings();
				addLstTitle(getText("config.title.report.networkusage"));
				return INPUT;
			} else if ("run".equals(operation) || "runNow".equals(operation)) {
				if (getDataSource() == null) {
					Long cloneId;
					if ("runNow".equals(operation)) {
						cloneId = id;
					} else {
						cloneId = getSelectedIds().get(0);
					}
					AhNewReport ahReport = (AhNewReport) findBoById(boClass, cloneId, this);
					setId(cloneId);
					setSessionDataSource(ahReport);
				} else {
					if (getTabIndex().equals("1")) {
						locationId = cuLocationId;
					} else {
						cuLocationId=locationId;
					}
					saveGuiValue();
				}
				prepareGuiSettings();
				if (locationId != null && locationId > -1) {
					MapContainerNode node = QueryUtil.findBoById(MapContainerNode.class, locationId);
					if (node==null) {
						addActionError(MgrUtil.getUserMessage("action.error.map.select.fail"));
						return INPUT;
					} else {
						getDataSource().setLocation(node);
					}
				}
				if (!checkTimeRangeBetweenFormAndTo()){
					return INPUT;
				}
				if (getDataSource().getExcuteType().equals("2")) {
					addActionError(MgrUtil.getUserMessage("action.error.schedule.report.fail"));
					return INPUT;
				}
				Set<String> apMacList = new HashSet<String>();
				Set<String> apNameList = new HashSet<String>();
				if (!getDataSource().isForSample()) {
					apNameList = prepareSearchDevices(apMacList);
				} else {
					lstDevice = new ArrayList<TextItem>();
					lstDevice.add(new TextItem("-1#","All Devices", "-1#"));
					lstDevice.add(new TextItem("-2#","Access Points", "-2#"));
					lstDevice.add(new TextItem("AP_10th_Grade","AP_10th_Grade", "0"));
					lstDevice.add(new TextItem("AP_9th_Grade","AP_9th_Grade", "0"));
					lstDevice.add(new TextItem("AP_Assembly_Rm","AP_Assembly_Rm", "0"));
					lstDevice.add(new TextItem("AP_Bldg1_12","AP_Bldg1_12", "0"));
					lstDevice.add(new TextItem("AP_Teachers_Rm","AP_Teachers_Rm", "0"));
					lstDevice.add(new TextItem("AP_Admin_Office","AP_Admin_Office", "0"));
					lstDevice.add(new TextItem("AP_Main_Library","AP_Main_Library", "0"));
					lstDevice.add(new TextItem("AP_Bldg5_25","AP_Bldg5_25", "0"));
					lstDevice.add(new TextItem("AP_Gynasium1","AP_Gynasium1", "0"));
					lstDevice.add(new TextItem("AP_Conf_RmR","AP_Conf_RmR", "0"));
				}
				int supportApCount = 200;
				List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class,
						null, null);
				if (!list.isEmpty()) {
					supportApCount = list.get(0).getMaxSupportAp();
				}
				
				if (apMacList.size()>supportApCount) {
					addActionError(MgrUtil.getUserMessage("action.error.run.report.fail",String.valueOf(supportApCount)));
					return INPUT;
				}
				showReportTab = true;
				getDataSource().setReportType(buttonType);
				getDataSource().setApNameList(apNameList);
				getDataSource().setApMacList(apMacList);
				getDataSource().setTz(tz);

				SimpleDateFormat l_sdf = new SimpleDateFormat("MMM d, yyyy, h:mma");
				l_sdf.setTimeZone(tz);
				
				long systime = System.currentTimeMillis();
				Calendar ca = Calendar.getInstance(tz);
				ca.setTimeInMillis(systime);
				long startTimeMill = getDataSource().getRunStartTime(ca);
				ca.setTimeInMillis(startTimeMill);
				getDataSource().setRunStartTimeString(l_sdf.format(ca.getTime()));
				getDataSource().setStartTime(startTimeMill);
				
				ca.setTimeInMillis(systime);
				long endTimeMill = getDataSource().getRunEndTime(ca);
				ca.setTimeInMillis(endTimeMill);
				getDataSource().setRunEndTimeString(l_sdf.format(ca.getTime()));
				getDataSource().setEndTime(endTimeMill);
				return INPUT;
			} else if ("sendMail".equals(operation)) {
				if (mailAddress!=null) {
					getDataSource().setEmailAddress(mailAddress);
				}
				if (getDataSource().getDefaultFlag()) {
					getDataSource().setOwner(getDomain());
				}
				getDataSource().setReportStartTime(System.currentTimeMillis());
				boolean isSucc = BeNetworkReportScheduleImpl.excutePerformance(getDataSource(), false, tz);
				if (isSucc) {
					// TODO
//					BeNetworkReportScheduleModule.mailCsvFile(getDataSource(), false,getDataSource().getEmailAddress());
				}
				generateAuditReportLog(isSucc);
				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				return "json";
			} else if ("createDownloadData".equals(operation)) {
				getDataSource().setReportStartTime(System.currentTimeMillis());
				if (getDataSource().getDefaultFlag()) {
					getDataSource().setOwner(getDomain());
				}
				boolean isSucc = BeNetworkReportScheduleImpl.excutePerformance(getDataSource(), false, tz);
				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				generateAuditReportLog(isSucc);

				return "json";
			} else if ("download".equals(operation)) {
				File file = new File(getInputPath());
				if (!file.exists()) {
					// commonly, logic should not come here
					addActionError(MgrUtil.getUserMessage("action.error.cannot.find.file"));
					// generateAuditLog(HmAuditLog.STATUS_FAILURE,
					// "Save support bundle");
					prepareGuiSettings();
					return INPUT;
				}
				return "download";
			} else if ("update".equals(operation)) {
				saveGuiValue();
				return updateBo();
			} else if ("changeLocation".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("v", fetchSsidList());
				jsonObject.put("id", "ssidListSelect");
				return "json";
			} else if ("changeLocationForAp".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("v", new JSONArray(fetchDevicesList()));
				jsonObject.put("id", "lstDeviceSelect");
				return "json";
			} else if ("getChartData".equals(operation)) {
				getSessionDataSource();
				// set common variables
				rp.setDomainId(getDomain().getId());
				rp.setTimeZone(tz);
				rp.setDataSource(getDataSource());
				
				if (rp.getPeriodType() <= 0
						&& getDataSource() != null) {
					rp.setPeriodType(getDataSource().getReportPeriod());
				}
				
				AhReportInvokerInterface reportInvoker = new AhReportInvoker();
				reportInvoker.init(rp, !rp.isBlnReqDesc());
				reportInvoker.invoke();
				jsonArray = reportInvoker.getJSONResult();
				// test code start
				//BeNetworkReportScheduleImpl.excutePerformance(getDataSource(), false, rp.getTimeZone());
				// test code end
				return "json";
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	
	public void generateAuditReportLog(boolean flg) {
		long diffTimer = System.currentTimeMillis() - getDataSource().getReportStartTime();
		String diffTimerStr = diffTimer > 1000 ? diffTimer/1000 + "s" : diffTimer + "ms";
		generateAuditLog(flg? HmAuditLog.STATUS_SUCCESS: HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.run.network.summary.report") 
				+ MgrUtil.getUserMessage("report.reportList.name") + " (" + getDataSource().getName() + "),"
				+ MgrUtil.getUserMessage("report.reportList.excuteType") + " (" + getDataSource().getExcuteTypeString() + ")"
				+ ".  "+MgrUtil.getUserMessage("hm.audit.log.time.used") + diffTimerStr);
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setDataSource(AhNewReport.class);
		setSelectedL2Feature(Navigation.L2_FEATURE_REPORT_NETWORKUSAGE);
		enableSorting();
		// default sorting
		if (sortParams.getOrderBy() == null) {
			sortParams.setOrderBy("name");
		}
		keyColumnId = COLUMN_NAME;
		tableId = HmTableColumn.TABLE_REPORT_NETWORKUSAGE;
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
		
		if (endTime != null && !endTime.equals("")) {
			String datetime[] = endTime.split("-");
			Calendar calendar = Calendar.getInstance(tz);
			calendar.clear(Calendar.MINUTE);
			calendar.clear(Calendar.SECOND);
			calendar.clear(Calendar.MILLISECOND);
			calendar.set(Calendar.YEAR, Integer.parseInt(datetime[0]));
			calendar.set(Calendar.MONTH, Integer.parseInt(datetime[1]) - 1);
			calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datetime[2]));
			calendar.set(Calendar.HOUR_OF_DAY, endHour);
			// calendar.set(Calendar.MINUTE, startMin);
			getDataSource().setEndTime(calendar.getTimeInMillis());
		} else {
			getDataSource().setEndTime(0);
		}

		if (locationId != null && locationId > -1) {
			MapContainerNode location = findBoById(MapContainerNode.class,
					locationId);
			getDataSource().setLocation(location);
		} else {
			getDataSource().setLocation(null);
		}
		
		if (ssid != null) {
			getDataSource().setSsidName(ssid);
		} else {
			getDataSource().setSsidName("All");
		}

		if (getDataSource().getExcuteType().equals("1")) {
			getDataSource().setEmailAddress("");
		}
		if (getDataSource().isCustomDay()) {
			StringBuilder sbf = new StringBuilder(7);
			sbf.append(c7?"1":"0");
			sbf.append(c1?"1":"0");
			sbf.append(c2?"1":"0");
			sbf.append(c3?"1":"0");
			sbf.append(c4?"1":"0");
			sbf.append(c5?"1":"0");
			sbf.append(c6?"1":"0");
			getDataSource().setCustomDayValue(sbf.toString());
		} else {
			getDataSource().setCustomDayValue("0111110");
		}
		
		if (!getDataSource().getExcuteType().equals("1")
				|| getDataSource().getReportPeriod()!=AhNewReport.NEW_REPORT_PERIOD_CUSTOM) {
			getDataSource().setStartTime(0);
			getDataSource().setEndTime(0);
		}
	}

	public boolean checkTimeRangeBetweenFormAndTo(){
		if (getDataSource().getExcuteType().equals(AhNewReport.NEW_REPORT_EXCUTETYPE_IMME)){
			if (getDataSource().getReportPeriod()==AhNewReport.NEW_REPORT_PERIOD_CUSTOM) {
				if (getDataSource().getStartTime()>System.currentTimeMillis()) {
					addActionError(MgrUtil.getUserMessage("action.error.fromtime.larger.currenttime"));
					return false;
				} else if (getDataSource().getEndTime()>System.currentTimeMillis()) {
						addActionError(MgrUtil.getUserMessage("action.error.totime.larger.currenttime"));
						return false;
				} else {
					if (System.currentTimeMillis()-getDataSource().getStartTime() >3600000L * 7*24){
						if (getDataSource().getEndTime()-getDataSource().getStartTime()< 3600000L * 2*24){
							addActionError(MgrUtil.getUserMessage("action.error.time.range.too.small"));
							return false;
						}
					} else if (System.currentTimeMillis()-getDataSource().getStartTime() >3600000L * 30 *24){
						if (getDataSource().getEndTime()-getDataSource().getStartTime()< 3600000L * 14*24){
							addActionError(MgrUtil.getUserMessage("action.error.time.range.too.small"));
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	public void prepareGuiSettings() {
		long tmpStartTime = getDataSource().getStartTime();
		if (tmpStartTime != 0) {
			Calendar calendar = Calendar.getInstance(tz);
			calendar.setTimeInMillis(tmpStartTime);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(tz);
			startTime = formatter.format(calendar.getTime());
			startHour = calendar.get(Calendar.HOUR_OF_DAY);
		}
		
		long tmpEndTime = getDataSource().getEndTime();
		if (tmpEndTime != 0) {
			Calendar calendar = Calendar.getInstance(tz);
			calendar.setTimeInMillis(tmpEndTime);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(tz);
			endTime = formatter.format(calendar.getTime());
			endHour = calendar.get(Calendar.HOUR_OF_DAY);
		} else if (tmpStartTime==0){
			Calendar calendar = Calendar.getInstance(tz);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(tz);
			endTime = formatter.format(calendar.getTime());
			startTime = formatter.format(calendar.getTime());
			endHour = calendar.get(Calendar.HOUR_OF_DAY);
		}
		if (getDataSource().getLocation() != null) {
			locationId = getDataSource().getLocation().getId();
		}
		if (getDataSource().getSsidName() != null) {
			ssid = getDataSource().getSsidName();
			
		}
		
		String v = getDataSource().getCustomDayValue().substring(0, 1);
		c7= v.equals("1");
		v = getDataSource().getCustomDayValue().substring(1, 2);
		c1= v.equals("1");
		v = getDataSource().getCustomDayValue().substring(2, 3);
		c2= v.equals("1");
		v = getDataSource().getCustomDayValue().substring(3, 4);
		c3= v.equals("1");
		v = getDataSource().getCustomDayValue().substring(4, 5);
		c4= v.equals("1");
		v = getDataSource().getCustomDayValue().substring(5, 6);
		c5= v.equals("1");
		v = getDataSource().getCustomDayValue().substring(6, 7);
		c6= v.equals("1");
		
		prepareLocationList();
		prepareSsidList();
		prepareReportPeriodList();
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	private void prepareReportPeriodList() {
		lstReportPeriod = AhNewReport.NEW_REPORT_PERIOD_TYPE;
	}
	
	private void prepareLocationList() {
		lstLocation = new ArrayList<CheckItem>();
		lstLocation = BoMgmt.getMapMgmt().getMapListView(
				getDomain().getId(), null, true);
		if (lstLocation.isEmpty()) {
			lstLocation.add(new CheckItem(-1l, "All"));
		}
		if (locationId==null || locationId<0) {
			locationId = lstLocation.get(0).getId();
		}
	}
	
	private void prepareSsidList() {
		StringBuilder sql = new StringBuilder(16);
		sql.append("select distinct s.ssid from hive_ap a, config_template_ssid c, ssid_profile s");
		sql.append(" where a.owner=").append(getDomain().getId());
		sql.append(" and a.managestatus=").append(HiveAp.STATUS_MANAGED);
		sql.append(" and a.template_id = c.config_template_id");
		sql.append(" and c.ssid_profile_id = s.id");
		
		if (locationId!=null && locationId>0) {
			Set<Long> mapIds =  BoMgmt.getMapMgmt().getContainerDownIds(locationId);
			StringBuilder tmpId = new StringBuilder();
			for(Long it: mapIds){
				if (tmpId.length()>0) {
					tmpId.append(",");
				}
				tmpId.append(it);
			}
			if (tmpId.length()>0) {
				sql.append(" and a.map_container_id in (").append(tmpId.toString()).append(")");
			}
		}
		sql.append(" order by s.ssid");
		
		Set<String> ssidStr = new HashSet<String>();
		lstSsid = new ArrayList<TextItem>();
		List<?> lstHiveAp = QueryUtil.executeNativeQuery(sql.toString());
		for(Object oneAp: lstHiveAp) {
				ssidStr.add(oneAp.toString());
		}
		for(String ssidone: ssidStr){
			lstSsid.add(new TextItem(ssidone,ssidone));
		}
		
		if (!lstSsid.isEmpty()) {
			sortTextItem(lstSsid);
		}
		lstSsid.add(0,new TextItem("All", "All"));
		if (ssid==null) {
			ssid = lstSsid.get(0).getKey();
		}
	}
	
	private List<String> fetchSsidList() {
		StringBuilder sql = new StringBuilder(16);
		sql.append("select distinct s.ssid from hive_ap a, config_template_ssid c, ssid_profile s");
		sql.append(" where a.owner=").append(getDomain().getId());
		sql.append(" and a.managestatus=").append(HiveAp.STATUS_MANAGED);
		sql.append(" and a.template_id = c.config_template_id");
		sql.append(" and c.ssid_profile_id = s.id");
		
		if (locationId!=null && locationId>0) {
			Set<Long> mapIds =  BoMgmt.getMapMgmt().getContainerDownIds(locationId);
			StringBuilder tmpId = new StringBuilder();
			for(Long it: mapIds){
				if (tmpId.length()>0) {
					tmpId.append(",");
				}
				tmpId.append(it);
			}
			if (tmpId.length()>0) {
				sql.append(" and a.map_container_id in (").append(tmpId.toString()).append(")");
			}
		}
		sql.append(" order by s.ssid");
		
		
		List<String> ssidStr = new ArrayList<String>();
		List<?> lstHiveAp = QueryUtil.executeNativeQuery(sql.toString());
		for(Object oneAp: lstHiveAp) {
			ssidStr.add(oneAp.toString());
		}

		ssidStr.add(0,"All");
		return ssidStr;
	}
	
	private List<JSONObject> fetchDevicesList() throws JSONException{
		StringBuilder serSql = new StringBuilder();
		if (ssid!=null && !ssid.equals("All")) {
			serSql.append("select distinct ap.hostname,ap.devicetype from hive_ap ap, config_template_ssid temp, ssid_profile ssid where ap.owner=")
			.append(getDomain().getId())
			.append(" and ap.devicetype!=").append(HiveAp.Device_TYPE_VPN_GATEWAY)
			.append(" and ap.managestatus=").append(HiveAp.STATUS_MANAGED)
			.append(" and temp.ssid_profile_id=ssid.id and ap.template_id= temp.config_template_id ")
			.append(" and ssid.ssid='").append(NmsUtil.convertSqlStr(ssid)).append("'");
			
		} else {
			serSql.append("select hostname,devicetype from hive_ap ap where ap.owner=")
			.append(getDomain().getId())
			.append(" and ap.managestatus=").append(HiveAp.STATUS_MANAGED)
			.append(" and ap.devicetype!=").append(HiveAp.Device_TYPE_VPN_GATEWAY);
		}
		
		if (cuLocationId!=null && cuLocationId>0) {
			Set<Long> mapIds =  BoMgmt.getMapMgmt().getContainerDownIds(cuLocationId);
			StringBuilder tmpId = new StringBuilder();
			for(Long it: mapIds){
				if (tmpId.length()>0) {
					tmpId.append(",");
				}
				tmpId.append(it);
			}
			if (tmpId.length()>0) {
				serSql.append(" and ap.map_container_id in (").append(tmpId.toString()).append(")");
			}
		}
		
		serSql.append(" order by devicetype, hostname");
		
		List<?> deviceList = QueryUtil.executeNativeQuery(serSql.toString());
		
		List<JSONObject> apNameList = new ArrayList<JSONObject>();
		JSONObject keyValue = new JSONObject();
		keyValue.put("k", "All Devices");
		keyValue.put("v", "-1#");
		apNameList.add(keyValue);
		boolean apFlg = false;
		boolean brFlg = false;
		for(Object oneObj: deviceList){
			Object[] oneRec = (Object[]) oneObj;
			if (oneRec[1].toString().equals("0") && !apFlg) {
				keyValue = new JSONObject();
				keyValue.put("k", "Access Points");
				keyValue.put("v", "-2#");
				apNameList.add(keyValue);
				apFlg=true;
			} else if (oneRec[1].toString().equals("1") && !brFlg) {
				keyValue = new JSONObject();
				keyValue.put("k", "Branch Routers");
				keyValue.put("v", "-3#");
				apNameList.add(keyValue);
				brFlg=true;
			}
			keyValue = new JSONObject();
			keyValue.put("k", oneRec[0].toString());
			keyValue.put("v", oneRec[0].toString());
			apNameList.add(keyValue);
		}
		
		return apNameList;
	}
	
	public List<TextItem> sortTextItem(List<TextItem> lst) {
		Collections.sort(lst, new Comparator<TextItem>() {
			@Override
			public int compare(TextItem o1, TextItem o2) {
					return o1.getKey().compareToIgnoreCase(o2.getKey());
			}
		});
		return lst;
	}
	
	public List<CheckItem> getLstHours() {
		List<CheckItem> lstHour = new ArrayList<CheckItem>();
		for (int i = 0; i < 12; i++) {
			if (i>9) {
				lstHour.add(new CheckItem((long) i, String.valueOf(i + ":00"+ " AM")));
			} else {
				if (i==0) {
					lstHour.add(new CheckItem((long) i, String.valueOf("12:00"+ " AM")));
				} else {
					lstHour.add(new CheckItem((long) i, String.valueOf("0" + i  + ":00"+ " AM")));
				}
			}
		}
		for (int i = 0; i < 12; i++) {
			if (i>9) {
				lstHour.add(new CheckItem((long) (i + 12), String.valueOf(i + ":00"+ " PM")));
			} else {
				if (i==0) {
					lstHour.add(new CheckItem((long) (i + 12), String.valueOf("12:00"+ " PM")));
				} else {
					lstHour.add(new CheckItem((long) (i + 12), String.valueOf("0" + i  + ":00"+ " PM")));
				}
			}
		}
		return lstHour;
	}
	
	public EnumItem[] getLstFrequency(){
		return AhNewReport.NEW_REPORT_FREQUENCY_TYPE;
	}

	@Override
	public AhNewReport getDataSource() {
		return (AhNewReport) dataSource;
	}

	public String getChangedReportName() {
		return getDataSource().getName().replace("\\", "\\\\").replace("'", "\\'");
	}
	
	public String getImmediatelyDivDisplay() {
		if (getDataSource().getExcuteType().equals(AhNewReport.NEW_REPORT_EXCUTETYPE_IMME)){
			return "";
		} 
		return "none";
	}
	
	public String getSchedulerDivDisplay() {
		if (getDataSource().getExcuteType().equals(AhNewReport.NEW_REPORT_EXCUTETYPE_SCHE)){
			return "";
		} 
		return "none";
	}
	
	public String getschedulerDailyDivDisplay(){
		if (getDataSource().getFrequency()==AhNewReport.NEW_REPORT_FREQUENCY_DAILY) {
			return "";
		}
		return "none";
	}
	
	public String getCustomTimeDivDisplay() {
		if (getDataSource().getReportPeriod()==AhNewReport.NEW_REPORT_PERIOD_CUSTOM){
			return "";
		}
		return "none";
	}
	
	public boolean getCustomDayEnabled() {
		return !getDataSource().isCustomDay();
	}
	
	public boolean getCustomTimeEnabled() {
		return !getDataSource().isCustomTime();
	}
	
	private Set<String> prepareSearchDevices(Set<String> apMacList){
		StringBuilder serSql = new StringBuilder();
		if (ssid!=null && !ssid.equals("All")) {
			serSql.append("select ap.hostname,ap.devicetype,ap.macAddress from hive_ap ap, config_template_ssid temp, ssid_profile ssid where ap.owner=")
			.append(getDomain().getId())
			.append(" and ap.devicetype!=").append(HiveAp.Device_TYPE_VPN_GATEWAY)
			.append(" and temp.ssid_profile_id=ssid.id and ap.template_id= temp.config_template_id ")
			.append(" and ssid.ssid='").append(NmsUtil.convertSqlStr(ssid)).append("'");
			
		} else {
			serSql.append("select ap.hostname,ap.devicetype,ap.macAddress from hive_ap ap where ap.owner=")
			.append(getDomain().getId())
			.append(" and ap.devicetype!=").append(HiveAp.Device_TYPE_VPN_GATEWAY);
		}
		Long mapId = locationId;
		if (getTabIndex().equals("1")) {
			mapId = cuLocationId;
		}
		if (mapId!=null && mapId>0) {
			Set<Long> mapIds =  BoMgmt.getMapMgmt().getContainerDownIds(mapId);
			StringBuilder tmpId = new StringBuilder();
			for(Long it: mapIds){
				if (tmpId.length()>0) {
					tmpId.append(",");
				}
				tmpId.append(it);
			}
			if (tmpId.length()>0) {
				serSql.append(" and ap.map_container_id in (").append(tmpId.toString()).append(")");
			}
		}
		
		serSql.append(" order by devicetype, hostname");
		
		List<?> deviceList = QueryUtil.executeNativeQuery(serSql.toString());
		lstDevice = new ArrayList<TextItem>();
		Set<String> apNameList = new HashSet<String>();
		lstDevice.add(new TextItem("-1#","All Devices", "-1#"));
		boolean apFlg = false;
		boolean brFlg = false;
		for(Object oneObj: deviceList){
			Object[] oneRec = (Object[]) oneObj;
			if (oneRec[1].toString().equals("0") && !apFlg) {
				lstDevice.add(new TextItem("-2#","Access Points", "-2#"));
				apFlg=true;
			} else if (oneRec[1].toString().equals("1") && !brFlg) {
				lstDevice.add(new TextItem("-3#","Branch Routers", "-3#"));
				brFlg=true;
			}
			lstDevice.add(new TextItem(oneRec[0].toString(),oneRec[0].toString(), oneRec[1].toString()));
			apNameList.add(oneRec[0].toString());
			apMacList.add(oneRec[2].toString());
		}
		
		return apNameList;
	}
	private String mailAddress;
	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}
	private boolean showReportTab;
	private String startTime;
	private int startHour;
	private String endTime;
	private int endHour;
	private Long locationId;
	private String ssid;
	private TimeZone tz;
	private int buttonType=1;
	
	private String tabIndex = "";
	
	private Long cuLocationId;
	private String cuDeivce="-1#";
	
	private List<CheckItem> lstLocation;
	private List<TextItem> lstSsid;
	private EnumItem[] lstReportPeriod;
	private List<TextItem> lstDevice;
	
	private boolean c1,c2,c3,c4,c5,c6,c7;
	
	public boolean isC1() {
		return c1;
	}

	public void setC1(boolean c1) {
		this.c1 = c1;
	}

	public boolean isC2() {
		return c2;
	}

	public void setC2(boolean c2) {
		this.c2 = c2;
	}

	public boolean isC3() {
		return c3;
	}

	public void setC3(boolean c3) {
		this.c3 = c3;
	}

	public boolean isC4() {
		return c4;
	}

	public void setC4(boolean c4) {
		this.c4 = c4;
	}

	public boolean isC5() {
		return c5;
	}

	public void setC5(boolean c5) {
		this.c5 = c5;
	}

	public boolean isC6() {
		return c6;
	}

	public void setC6(boolean c6) {
		this.c6 = c6;
	}

	public boolean isC7() {
		return c7;
	}

	public void setC7(boolean c7) {
		this.c7 = c7;
	}

//	public static final String mailFileNamePdf = "currentReportData.pdf";
	
	public String getInputPath() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		sf.setTimeZone(getDataSource().getTz());
		String mailFileName = "NetworkSummary" + "_"  + getDataSource().getName() + "_" + sf.format(new Date()) + ".pdf";
		return BeNetworkReportScheduleModule.fileDirPathCurrent + File.separator
				+ getDomain().getDomainName() + File.separator + mailFileName;
	}

	public String getLocalFileName() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		sf.setTimeZone(getDataSource().getTz());
		return "NetworkSummary" + "_"  + getDataSource().getName() + "_" + sf.format(new Date()) + ".pdf";
	}

	public InputStream getInputStream() throws Exception {
		return new FileInputStream(getInputPath());
	}
	
	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HiveAp) {
			HiveAp ap = (HiveAp) bo;
			if (ap.getConfigTemplate()!=null) {
				if (ap.getConfigTemplate().getSsidInterfaces()!=null) {
					ap.getConfigTemplate().getSsidInterfaces().values();
				}
			}
		}
		return null;
	}

	public boolean getShowReportTab() {
		return showReportTab;
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

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public TimeZone getTz() {
		return tz;
	}

	public void setTz(TimeZone tz) {
		this.tz = tz;
	}

	public int getButtonType() {
		return buttonType;
	}

	public void setButtonType(int buttonType) {
		this.buttonType = buttonType;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public List<CheckItem> getLstLocation() {
		return lstLocation;
	}

	public List<TextItem> getLstSsid() {
		return lstSsid;
	}

	public EnumItem[] getLstReportPeriod() {
		return lstReportPeriod;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	AhReportRequest rp = new AhReportRequest();

	public AhReportRequest getRp() {
		return rp;
	}

	public void setRp(AhReportRequest rp) {
		this.rp = rp;
	}

	public List<TextItem> getLstDevice() {
		if (lstDevice==null) {
			List<TextItem> aa = new ArrayList<TextItem>();
			aa.add(new TextItem(getText("config.optionsTransfer.none"),getText("config.optionsTransfer.none")));
			return aa;
		}
		return lstDevice;
	}

	public String getCuDeivce() {
		return cuDeivce;
	}

	public void setCuDeivce(String cuDeivce) {
		this.cuDeivce = cuDeivce;
	}

	public Long getCuLocationId() {
		if (cuLocationId==null) {
			cuLocationId = locationId;
		}
		return cuLocationId;
	}

	public void setCuLocationId(Long cuLocationId) {
		this.cuLocationId = cuLocationId;
	}

	public String getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(String tabIndex) {
		this.tabIndex = tabIndex;
	}
	
	public boolean isCalTopNClients() {
		return getDataSource() != null && ClientsTopNReport.isCalForTheRequest(getDataSource());
	}
}