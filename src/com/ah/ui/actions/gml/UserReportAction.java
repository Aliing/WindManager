/**
 * @filename			UserReportAction.java
 * @version				1.0
 * @author				fisher 
 * @since				3.5
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.ui.actions.gml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.json.JSONObject;

import com.Ostermiller.util.CSVPrinter;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.performance.AhPerformanceScheduleModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhReport;
import com.ah.bo.performance.AhUserReport;
import com.ah.bo.useraccess.UserProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.admin.NavigationCustomizationUtil;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class UserReportAction extends BaseAction implements QueryBo{

	private static final long	serialVersionUID	= 1L;
	private static final Tracer	log	= new Tracer(UserReportAction.class.getSimpleName());
	public static final String USER_REPORT_LIST_TYPE = "userReportListType";
	
	private static final String REPORT_RESULT_SESSION_KEY = "reportResultSessionKey";
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_DESCRIPTION = 2;

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
		case COLUMN_DESCRIPTION:
			code = "gml.permanent.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(2);

		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}
	
	private String userReportType;

	@Override
	public String execute() throws Exception {
		if (userReportType == null || userReportType.equals("")) {
			userReportType = MgrUtil.getSessionAttribute(USER_REPORT_LIST_TYPE).toString();
		} else {
			MgrUtil.setSessionAttribute(USER_REPORT_LIST_TYPE, userReportType);
		}

		filterParams = new FilterParams("reportType", userReportType);
		setSelectedL1Feature(Navigation.L1_FEATURE_USER_MGR);
		setSelectedL2Feature(userReportType);
		resetPermission();
		tz = getUserTimeZone();
		
		try {
			if("new".equals(operation)) {
				AhUserReport report = new AhUserReport();
				report.setReportType(userReportType);
				setSessionDataSource(report);
				return userReportType;
			} else if ("create".equals(operation)) {
				if (checkNameExists("name=:s1 and reportType=:s2", new Object[] {
						getDataSource().getName(), userReportType })) {
					return userReportType;
				}
				return createBo();
			} else if ("edit".equals(operation)) {
				editBo();
				return userReportType;
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				AhUserReport ahReport = (AhUserReport) findBoById(boClass, cloneId);
				ahReport.setId(null);
				ahReport.setOwner(getDomain());
				ahReport.setDefaultFlag(false);
				ahReport.setName("");
				setSessionDataSource(ahReport);
				return userReportType;
			} else if ("run".equals(operation)) {
				if (getDataSource() == null) {
					long cloneId = getSelectedIds().get(0);
					AhUserReport ahReport = (AhUserReport) findBoById(boClass, cloneId);
					setId(cloneId);
					setSessionDataSource(ahReport);
				}
				showReportTab = true;
				initFlashData();
				if (reportResult.size()>1){
					prepareFlash();
				}
				return userReportType;
			} else if ("getFlashData".equals(operation)) {
				reportResult = (List<CheckItem>)MgrUtil.getSessionAttribute(REPORT_RESULT_SESSION_KEY);
				return "userReportData";
			} else if ("createDownloadData".equals(operation)) {
				reportResult = (List<CheckItem>)MgrUtil.getSessionAttribute(REPORT_RESULT_SESSION_KEY);
				boolean isSucc = generalCurrentCvsFile();
				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				return "json";
			} else if ("download".equals(operation)) {
				File file = new File(getInputPath());
				if (!file.exists()) {
					// commonly, logic should not come here
					addActionError(MgrUtil.getUserMessage("action.error.cannot.find.file"));
					// generateAuditLog(HmAuditLog.STATUS_FAILURE,
					// "Save support bundle");
					return userReportType;
				}
				return "download";
			} else if ("update".equals(operation)) {
				if (id == null)
					setId(getDataSource().getId());
				return updateBo();
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch(Exception e) {
			return prepareActionError(e);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setDataSource(AhUserReport.class);
		enableSorting();
		// default sorting
		if (sortParams.getOrderBy() == null) {
			sortParams.setOrderBy("name");
		}
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_USER_REPORT;
	}

	@Override
	public AhUserReport getDataSource() {
		return (AhUserReport) dataSource;
	}
	
	public static TimeZone tz;
	private String	swf, width, height, application, bgcolor;
	
	private boolean showReportTab;
	private final String mailFileName = "currentReportData.csv";
	
	public String getInputPath() {
		return AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
				+ getDomain().getDomainName() + File.separator + mailFileName;
	}
	public InputStream getInputStream() throws Exception {
		return new FileInputStream(getInputPath());
	}
	
	public String getLocalFileName() {
		return mailFileName;
	}
	
	protected void prepareFlash() {
		swf = "gmlReport";
		width = "100%";
		height = "420";
		application = "gmlReport";
		bgcolor = "ffffff";
	}
	
	public String prepareGuessAccessMap() {
		List<String> sqlList = new ArrayList<String>();
		try {
			String strSql = "select a.macaddress, c.ssid, d.id "
					+ " from hive_ap a, config_template_ssid b,ssid_profile c, user_profile d "
					+ " where a.template_id = b.config_template_id "
					+ " and b.ssid_profile_id = c.id "
					+ " and (c.userprofile_default_id=d.id or c.userprofile_selfreg_id = d.id "
					+ " or d.id in (select e.user_profile_id from ssid_profile_user_profile e where e.ssid_profile_id = c.id))"
					+ " and d.blnUserManager=true and a.owner="
					+ getDomain().getId() + " and c.owner=" + getDomain().getId() + " and d.owner="
					+ getDomain().getId();
			List<?> queryResult = QueryUtil.executeNativeQuery(strSql);
			if (queryResult != null && queryResult.size() > 0) {
				for (Object obj : queryResult) {
					Object[] result = (Object[]) obj;
					UserProfile up = findBoById(UserProfile.class, Long
							.parseLong(result[2].toString()), this);
					sqlList.add("(apMac='" + result[0].toString() + "' and clientssid='"
							+ result[1].toString() + "' and clientuserprofid="
							+ up.getAttributeValue() + ")");
					Set<String> setUserAttribute = new HashSet<String>();
					if (up.getUserProfileAttribute() != null) {
						for (SingleTableItem singleTable : up.getUserProfileAttribute().getItems()) {
							String[] strAttrValue = singleTable.getAttributeValue().split(",");
							for (String value : strAttrValue) {
								String[] attrRange = value.split("-");
								if (attrRange.length > 1) {
									for (int addCount = Integer.parseInt(attrRange[0]); addCount < Integer
											.parseInt(attrRange[1]) + 1; addCount++) {
										setUserAttribute.add(String.valueOf(addCount));
									}
								} else {
									setUserAttribute.add(String.valueOf(attrRange[0]));
								}
							}
						}
					}
					for (String value : setUserAttribute) {
							sqlList.add("(apMac='" + result[0].toString()
									+ "' and clientssid='" + result[1].toString()
									+ "' and clientuserprofid=" + value + ")");
					}
				}
			}
		} catch (Exception e) {
			log.error("prepareGuessAccessMap", "catch exception", e);
		}
		
		String resultSql = "";
		if (sqlList.size() > 0) {
			for (int i = 0; i < (sqlList.size()-1); i++) {
				resultSql = resultSql + sqlList.get(i) + " or ";
			}
			resultSql += sqlList.get(sqlList.size()-1);
		}
		
		return resultSql;
	}
	
	public void initFlashData(){
		if (getDataSource().getReportType().equals(L2_FEATURE_UP_USER_DAY)){
			getUserPerDayValue();
		} else if (getDataSource().getReportType().equals(L2_FEATURE_UP_SESS_DAY)){
			getSessionPerDayValue();
		} else if (getDataSource().getReportType().equals(L2_FEATURE_UP_SESS_TIME_DAY)){
			getAverageSessionTimePerDayValue();
		} else if (getDataSource().getReportType().equals(L2_FEATURE_UP_SESS_NAS)){
			getApSessionCountValue();
		}
	}
	private List<CheckItem> reportResult = new ArrayList<CheckItem>();
	
	public void getApSessionCountValue(){
		Calendar reportStartTime = getReportDateTime();
		long timeInMills = reportStartTime.getTimeInMillis();
		String filterCondition = prepareGuessAccessMap();
		
		List<?> initHistoryResult=null;
		StringBuffer searchSql;
		if (filterCondition.length()!=0) {
			searchSql = new StringBuffer();
			searchSql.append("select apName,count(apName) from ah_clientsession_history");
			searchSql.append(" where owner=").append(getDomain().getId());
			if (!getDataSource().getApName().trim().equals("")){
				searchSql.append(" and lower(apName) like '%").append(getDataSource().getApNameForSQL()).append("%'");
			}
			searchSql.append(" and (").append(filterCondition).append(")");
			searchSql.append(" and endtimestamp >=").append(timeInMills);
			searchSql.append(" group by apName");
			
			initHistoryResult = QueryUtil.executeNativeQuery(searchSql.toString(), 100000);
		}
		
		if (initHistoryResult!=null && initHistoryResult.size()>0){
			for(Object obj:initHistoryResult){
				Object[] oneItem = (Object[]) obj;
				reportResult.add(new CheckItem(Long.valueOf(oneItem[1].toString()), oneItem[0].toString()));
			}
		}
		
		MgrUtil.setSessionAttribute(REPORT_RESULT_SESSION_KEY, reportResult);
	}
	
	public void getSessionPerDayValue(){
		Calendar reportStartTime = getReportDateTime();
		long timeInMills = reportStartTime.getTimeInMillis();
		long sysTime = System.currentTimeMillis();
		Map<String,Integer> resultMap = new HashMap<String,Integer>();
		String filterCondition = prepareGuessAccessMap();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setTimeZone(tz);
		
		List<?> initHistoryResult=null;
		StringBuffer searchSql;
		if (filterCondition.length()!=0) {
			searchSql = new StringBuffer();
			searchSql.append("select starttimestamp,endtimestamp from ah_clientsession_history");
			searchSql.append(" where owner=").append(getDomain().getId());
			if (!getDataSource().getApName().trim().equals("")){
				searchSql.append(" and lower(apName) like '%").append(getDataSource().getApNameForSQL()).append("%'");
			}
			if (!getDataSource().getAuthMac().trim().equals("")){
				searchSql.append(" and lower(clientMac) like '%").append(getDataSource().getAuthMacForSQL()).append("%'");
			}
			if (!getDataSource().getAuthHostName().trim().equals("")){
				searchSql.append(" and lower(clientHostname) like '%").append(getDataSource().getAuthHostNameForSQL()).append("%'");
			}
			if (!getDataSource().getAuthUserName().trim().equals("")){
				searchSql.append(" and lower(clientUsername) like '%").append(getDataSource().getAuthUserName()).append("%'");
			}
			if (!getDataSource().getAuthIp().trim().equals("")){
				searchSql.append(" and lower(clientIP) like '%").append(getDataSource().getAuthIpForSQL()).append("%'");
			}
			searchSql.append(" and (").append(filterCondition).append(")");
			searchSql.append(" and endtimestamp >=").append(timeInMills);
			searchSql.append(" order by starttimestamp");
			
			initHistoryResult = QueryUtil.executeNativeQuery(searchSql.toString(), 100000);
		}
		
		while(reportStartTime.getTimeInMillis()<sysTime){
			if (resultMap.get(df.format(reportStartTime.getTime()))==null){
				resultMap.put(df.format(reportStartTime.getTime()), 0);
			}
			if (initHistoryResult!=null && initHistoryResult.size()>0){
				for(Object obj: initHistoryResult){
					Object[] oneItem = (Object[]) obj;
					if (Long.parseLong(oneItem[0].toString())> reportStartTime.getTimeInMillis() + 86400000){
						break;
					}
					if (Long.parseLong(oneItem[0].toString())< reportStartTime.getTimeInMillis() + 86400000 
							&& Long.parseLong(oneItem[1].toString())>=reportStartTime.getTimeInMillis()){
						int mapSizeCount = resultMap.get(df.format(reportStartTime.getTime())) + 1;
						resultMap.put(df.format(reportStartTime.getTime()),mapSizeCount);
					}
				}
			}
			reportStartTime.add(Calendar.DAY_OF_MONTH, 1);
		}

		for(String key: resultMap.keySet()){
			reportResult.add(new CheckItem(Long.valueOf(resultMap.get(key)), key));
		}
		Collections.sort(reportResult, new Comparator<CheckItem>() {
			@Override
			public int compare(CheckItem o1, CheckItem o2) {
				try {
					SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
					sf1.setTimeZone(tz);
					Date reportTime1 = sf1.parse(o1.getValue());
					Date reportTime2 = sf1.parse(o2.getValue());
					long diff = reportTime1.getTime() - reportTime2.getTime();
					if (diff>0)return 1;
					else if (diff<0) return -1;
					else return 0;
				} catch (Exception e) {
					return 0;
				}
			}
		});
		MgrUtil.setSessionAttribute(REPORT_RESULT_SESSION_KEY, reportResult);
	}
	
	public void getAverageSessionTimePerDayValue(){
		Calendar reportStartTime = getReportDateTime();
		long timeInMills = reportStartTime.getTimeInMillis();
		long sysTime = System.currentTimeMillis();
		Map<String,Long> resultTimeMap = new HashMap<String,Long>();
		Map<String,Integer> resultCountMap = new HashMap<String,Integer>();
		String filterCondition = prepareGuessAccessMap();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setTimeZone(tz);
		
		List<?> initHistoryResult=null;
		StringBuffer searchSql;
		if (filterCondition.length()!=0) {
			searchSql = new StringBuffer();
			searchSql.append("select starttimestamp,endtimestamp from ah_clientsession_history");
			searchSql.append(" where owner=").append(getDomain().getId());
			if (!getDataSource().getApName().trim().equals("")){
				searchSql.append(" and lower(apName) like '%").append(getDataSource().getApNameForSQL()).append("%'");
			}
			if (!getDataSource().getAuthMac().trim().equals("")){
				searchSql.append(" and lower(clientMac) like '%").append(getDataSource().getAuthMacForSQL()).append("%'");
			}
			if (!getDataSource().getAuthHostName().trim().equals("")){
				searchSql.append(" and lower(clientHostname) like '%").append(getDataSource().getAuthHostNameForSQL()).append("%'");
			}
			if (!getDataSource().getAuthUserName().trim().equals("")){
				searchSql.append(" and lower(clientUsername) like '%").append(getDataSource().getAuthUserName()).append("%'");
			}
			if (!getDataSource().getAuthIp().trim().equals("")){
				searchSql.append(" and lower(clientIP) like '%").append(getDataSource().getAuthIpForSQL()).append("%'");
			}
			searchSql.append(" and (").append(filterCondition).append(")");
			searchSql.append(" and endtimestamp >=").append(timeInMills);
			searchSql.append(" order by starttimestamp");
			
			initHistoryResult = QueryUtil.executeNativeQuery(searchSql.toString(), 100000);
		}

		while(reportStartTime.getTimeInMillis()<sysTime){
			if (resultTimeMap.get(df.format(reportStartTime.getTime()))==null){
				resultTimeMap.put(df.format(reportStartTime.getTime()), (long) 0);
			}
			if (resultCountMap.get(df.format(reportStartTime.getTime()))==null){
				resultCountMap.put(df.format(reportStartTime.getTime()), 0);
			}
			if (initHistoryResult!=null && initHistoryResult.size()>0){
				for(Object obj: initHistoryResult){
					Object[] oneItem = (Object[]) obj;
					if (Long.parseLong(oneItem[0].toString())> reportStartTime.getTimeInMillis() + 86400000){
						break;
					}
					if (Long.parseLong(oneItem[0].toString())< reportStartTime.getTimeInMillis() + 86400000 
							&& Long.parseLong(oneItem[1].toString())>=reportStartTime.getTimeInMillis()){
						long mapTotalTime = resultTimeMap.get(df.format(reportStartTime.getTime())) + 
							Long.parseLong(oneItem[1].toString()) -  Long.parseLong(oneItem[0].toString());
						int mapSizeCount = resultCountMap.get(df.format(reportStartTime.getTime())) + 1;
						
						resultTimeMap.put(df.format(reportStartTime.getTime()),mapTotalTime);
						resultCountMap.put(df.format(reportStartTime.getTime()),mapSizeCount);
					}
				}
			}
			reportStartTime.add(Calendar.DAY_OF_MONTH, 1);
		}

		for(String key: resultTimeMap.keySet()){
			if (resultCountMap.get(key)==0){
				reportResult.add(new CheckItem((long) 0, key));
			} else {
				reportResult.add(new CheckItem(resultTimeMap.get(key) / resultCountMap.get(key), key));
			}
		}
		Collections.sort(reportResult, new Comparator<CheckItem>() {
			@Override
			public int compare(CheckItem o1, CheckItem o2) {
				try {
					SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
					sf1.setTimeZone(tz);
					Date reportTime1 = sf1.parse(o1.getValue());
					Date reportTime2 = sf1.parse(o2.getValue());
					long diff = reportTime1.getTime() - reportTime2.getTime();
					if (diff>0)return 1;
					else if (diff<0) return -1;
					else return 0;
				} catch (Exception e) {
					return 0;
				}
			}
		});
		MgrUtil.setSessionAttribute(REPORT_RESULT_SESSION_KEY, reportResult);
	}
	
	public void getUserPerDayValue(){
		Calendar reportStartTime = getReportDateTime();
		long timeInMills = reportStartTime.getTimeInMillis();
		long sysTime = System.currentTimeMillis();
		Map<String, Set<String>> resultMap = new HashMap<String, Set<String>>();
		String filterCondition = prepareGuessAccessMap();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setTimeZone(tz);
		
		List<?> initHistoryResult=null;
		StringBuffer searchSql;
		if (filterCondition.length()!=0) {
			searchSql = new StringBuffer();
			searchSql.append("select clientMac,starttimestamp,endtimestamp from ah_clientsession_history");
			searchSql.append(" where owner=").append(getDomain().getId());
			if (!getDataSource().getApName().trim().equals("")){
				searchSql.append(" and lower(apName) like '%").append(getDataSource().getApNameForSQL()).append("%'");
			}
			searchSql.append(" and (").append(filterCondition).append(")");
			searchSql.append(" and endtimestamp >=").append(timeInMills);
			searchSql.append(" order by starttimestamp");
			
			initHistoryResult = QueryUtil.executeNativeQuery(searchSql.toString(), 100000);
		}
		while(reportStartTime.getTimeInMillis()<sysTime){
			if (resultMap.get(df.format(reportStartTime.getTime()))==null){
				Set<String> tmpSet = new HashSet<String>();
				resultMap.put(df.format(reportStartTime.getTime()), tmpSet);
			}
			if (initHistoryResult!=null && initHistoryResult.size()>0){
				for(Object obj: initHistoryResult){
					Object[] oneItem = (Object[]) obj;
					if (Long.parseLong(oneItem[1].toString())> reportStartTime.getTimeInMillis() + 86400000){
						break;
					}
					if (Long.parseLong(oneItem[1].toString())< reportStartTime.getTimeInMillis() + 86400000 
							&& Long.parseLong(oneItem[2].toString())>=reportStartTime.getTimeInMillis()){
						resultMap.get(df.format(reportStartTime.getTime())).add(oneItem[0].toString());
					}
				}
			}
			reportStartTime.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		List<?> initActiveResult=null;
		if (filterCondition.length()!=0) {
			reportStartTime.setTimeInMillis(timeInMills);
			searchSql = new StringBuffer();
			searchSql.append("select clientMac,starttimestamp from ah_clientsession");
			searchSql.append(" where owner=").append(getDomain().getId());
			if (!getDataSource().getApName().trim().equals("")){
				searchSql.append(" and lower(apName) like '%").append(getDataSource().getApNameForSQL()).append("%'");
			}
			searchSql.append(" and (").append(filterCondition).append(")");
			searchSql.append(" and connectstate =" + AhClientSession.CONNECT_STATE_UP);
			searchSql.append(" order by starttimestamp");
			
//			initActiveResult = QueryUtil.executeNativeQuery(searchSql.toString(), 100000);
			searchSql.append(" limit 100000");
			initActiveResult = DBOperationUtil.executeQuery(searchSql.toString());
		}
		
		while(reportStartTime.getTimeInMillis()<sysTime){
			if (resultMap.get(df.format(reportStartTime.getTime()))==null){
				Set<String> tmpSet = new HashSet<String>();
				resultMap.put(df.format(reportStartTime.getTime()), tmpSet);
			}
			if (initActiveResult!=null && initActiveResult.size()>0){
				for(Object obj: initActiveResult){
					Object[] oneItem = (Object[]) obj;
					if (Long.parseLong(oneItem[1].toString())< reportStartTime.getTimeInMillis() + 86400000){
						resultMap.get(df.format(reportStartTime.getTime())).add(oneItem[0].toString());
					}
				}
			}
			reportStartTime.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		for(String key: resultMap.keySet()){
			reportResult.add(new CheckItem((long) resultMap.get(key).size(), key));
		}
		Collections.sort(reportResult, new Comparator<CheckItem>() {
			@Override
			public int compare(CheckItem o1, CheckItem o2) {
				try {
					SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
					sf1.setTimeZone(tz);
					Date reportTime1 = sf1.parse(o1.getValue());
					Date reportTime2 = sf1.parse(o2.getValue());
					long diff = reportTime1.getTime() - reportTime2.getTime();
					if (diff>0)return 1;
					else if (diff<0) return -1;
					else return 0;
				} catch (Exception e) {
					return 0;
				}
			}
		});
		MgrUtil.setSessionAttribute(REPORT_RESULT_SESSION_KEY, reportResult);
	}
	
	public Calendar getReportDateTime() {
		int reportDay = 0;
		int reportMonth = 0;
		switch (getDataSource().getReportPeriod()) {
		case AhReport.REPORT_PERIOD_LASTONEDAY:
			reportDay = 1;
			break;
		case AhReport.REPORT_PERIOD_LASTTWODAYS:
			reportDay = 2;
			break;
		case AhReport.REPORT_PERIOD_LASTTHREEDAYS:
			reportDay = 3;
			break;
		case AhReport.REPORT_PERIOD_LASTONEWEEK:
			reportDay = 7;
			break;
		case AhReport.REPORT_PERIOD_LASTTWOWEEKS:
			reportDay = 14;
			break;
		case AhReport.REPORT_PERIOD_LASTTHREEWEEKS:
			reportDay = 21;
			break;
		case AhReport.REPORT_PERIOD_LASTONEMONTH:
			reportMonth = 1;
			break;
		case AhReport.REPORT_PERIOD_LASTTWOMONTHs:
			reportMonth = 2;
			break;
		}
		Calendar calendar = Calendar.getInstance(tz);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.HOUR_OF_DAY,0);
		
		calendar.add(Calendar.DAY_OF_MONTH, reportDay * -1);
		calendar.add(Calendar.MONTH, reportMonth * -1);
		return calendar;
	}
	
	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof UserProfile) {
			UserProfile userp = (UserProfile) bo;
			if (userp.getUserProfileAttribute() != null){
				userp.getUserProfileAttribute().getId();
				if (userp.getUserProfileAttribute().getItems()!=null) {
					userp.getUserProfileAttribute().getItems().size();
				}
			}
		}
		return null;
	}
	
	public synchronized boolean generalCurrentCvsFile() {
		try {
			String currentFileDir = AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
					+ getDomain().getDomainName();
			File tmpFileDir = new File(currentFileDir);
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(getInputPath());
			CSVPrinter printer = new CSVPrinter(fos);
			
			String[][] allValues;
			int totalRecordSize;
			if (reportResult==null){
				totalRecordSize = 1;
			} else {
				totalRecordSize=reportResult.size() + 1;
			}
			allValues = new String[totalRecordSize][2];
			// the title
			int i = -1;
			String[] title = new String[2];
			if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_UP_USER_DAY)){
				title[0] = getText("report.reportList.title.time");
				title[1] = getText("report.reportList.gml.clientCount");
			} else if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_UP_SESS_DAY)){
				title[0] = getText("report.reportList.title.time");
				title[1] = getText("report.reportList.gml.sessionCount");
			} else if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_UP_SESS_TIME_DAY)){
				title[0] = getText("report.reportList.title.time");
				title[1] = getText("report.reportList.gml.avgSessionTime");
			} else if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_UP_SESS_NAS)){
				title[0] = getText("report.reportList.apName");
				title[1] = getText("report.reportList.gml.sessionCount");
			}
			allValues[++i] = title;
			if (reportResult!= null && reportResult.size()>0){
				for (CheckItem obj : reportResult) {
					title = new String[2];
					title[0]=obj.getValue();
					if (getDataSource().getReportType().equalsIgnoreCase(L2_FEATURE_UP_SESS_TIME_DAY)){
						title[1]=obj.getLongToTime();
					} else {
						title[1]=obj.getId().toString();
					}
					allValues[++i] = title;
				}
			}
			printer.writeln(allValues);
			fos.close();
			printer.close();
			return true;
		} catch (Exception ex) {
			addActionError(MgrUtil.getUserMessage(ex));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, boClass.getSimpleName()
					+ " " + MgrUtil.getUserMessage(ex));
			return false;
		}
	}
	
	public String getUserReportType() {
		return userReportType;
	}

	public void setUserReportType(String userReportType) {
		this.userReportType = userReportType;
	}
	
	public String getChangedReportName() {
		return getDataSource().getName().replace("\\", "\\\\").replace("'", "\\'");
	}
	
	public int getNameLength() {
		return getAttributeLength("name");
	}
	
	public EnumItem[] getEnumReportPeriod() {
		return AhUserReport.REPORT_PERIOD_TYPE;
	}
	
	public boolean getEmailUsabled() {
		return !getUpdateDisabled().equals("");
	}
	
	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}
	
	public String getRunReportTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(tz);
		return formatter.format(new Date());
	}

	public boolean getShowReportTab() {
		return showReportTab;
	}

	public void setShowReportTab(boolean showReportTab) {
		this.showReportTab = showReportTab;
	}
	
	public boolean getCheckIp() {
		return getDataSource().getAuthIp() != null && !getDataSource().getAuthIp().equals("");
	}

	public boolean getCheckHostName() {
		return getDataSource().getAuthHostName() != null && !getDataSource().getAuthHostName().equals("");
	}

	public boolean getCheckUserName() {
		return getDataSource().getAuthUserName() != null && !getDataSource().getAuthUserName().equals("");
	}
	
	public String getShowClientCondition(){
		if (userReportType.equalsIgnoreCase(Navigation.L2_FEATURE_UP_SESS_DAY) ||
				userReportType.equalsIgnoreCase(Navigation.L2_FEATURE_UP_SESS_TIME_DAY)){
			return "";
		}
		return "none";
	}
	
	public String getShowClientIpAddress(){
		if (getShowClientCondition().equals("")){
			if (getCheckIp()){
				return "";
			}
		}
		return "none";
	}
	
	public String getShowClientHostName(){
		if (getShowClientCondition().equals("")){
			if (getCheckHostName()){
				return "";
			}
		}
		return "none";
	}
	
	public String getShowClientUserName(){
		if (getShowClientCondition().equals("")){
			if (getCheckUserName()){
				return "";
			}
		}
		return "none";
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

	public List<CheckItem> getReportResult() {
		return reportResult;
	}
	
}