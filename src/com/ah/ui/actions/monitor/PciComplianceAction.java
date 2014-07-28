package com.ah.ui.actions.monitor;

/*
 * @author Fisher
 */

//import java.awt.Graphics2D;

import java.io.File;
import java.io.FileInputStream;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONObject;

import com.ah.be.performance.AhPerformanceScheduleModule;
import com.ah.be.performance.PCIReportGenerateImpl;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.performance.AhReport;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

public class PciComplianceAction extends BaseAction{

	private static final long serialVersionUID = 1L;

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_TYPE = 2;

	public static final int COLUMN_EMAILADDRESS = 3;

	public static final int COLUMN_NEXTSCHEDULE = 4;

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
		columns.add(new HmTableColumn(COLUMN_TYPE));
		columns.add(new HmTableColumn(COLUMN_EMAILADDRESS));
		columns.add(new HmTableColumn(COLUMN_NEXTSCHEDULE));
		return columns;
	}
	
	public String execute() throws Exception {
		tz = getUserTimeZone();
		filterParams = new FilterParams("reportType", "pciCompliance");
		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess("PCI Compliance > New")) {
					return getLstForward();
				}
				setSessionDataSource(new AhReport());
				getDataSource().setOwner(getDomain());
				getDataSource().setReportType("pciCompliance");
				prepareGuiSetting();
				return INPUT;
			} else if ("create".equals(operation)) {
				if (checkNameExists("name=:s1 and reportType=:s2", new Object[] {
						getDataSource().getName(), getDataSource().getReportType() })) {
					return INPUT;
				}
				saveGuiValue();
				String retString =  createBo();
				if (page!=null){
					TimeZone myTimeZone = TimeZone.getTimeZone(userContext.getTimeZone()); 
					for(Object myObject:page){
						AhReport tmpClass = (AhReport)myObject;
						tmpClass.setTz(myTimeZone);
					}
				}
				return retString;
			} else if ("update".equals(operation)) {
				saveGuiValue();
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
			} else if ("edit".equals(operation)) {
				editBo();
				if (dataSource != null) {
					if (getDataSource().getId() != null) {
						findBoById(AhReport.class, getDataSource().getId());
					}
					setSessionDataSource(dataSource);
				}
				prepareGuiSetting();
				return INPUT;
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				AhReport ahReport = (AhReport) findBoById(boClass, cloneId);
				ahReport.setId(null);
				ahReport.setOwner(getDomain());
				ahReport.setDefaultFlag(false);
				ahReport.setName("");
				setSessionDataSource(ahReport);
				prepareGuiSetting();
				addLstTitle("PCI Compliance > > New");
				return INPUT;
			} else if ("export".equals(operation)) {
				long beginTimeRun = System.currentTimeMillis();
				saveGuiValue();
				boolean isSucc = generalCurrentPdfFile(getDataSource());
				generateAuditReportLog(beginTimeRun);
				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				return "json";
			} else if ("exportInList".equals(operation)) {
				jsonObject = new JSONObject();
				long exportId = getSelectedIds().get(0);
				AhReport ahReport = (AhReport) findBoById(boClass, exportId);
				if (ahReport!=null && !ahReport.getExcuteType().equals("1")) {
					jsonObject.put("success", false);
					jsonObject.put("msg", "Export operation only support for immediately type report.");
					return "json";
				}
				setSessionDataSource(ahReport);
				long beginTimeRun = System.currentTimeMillis();
				prepareGuiSetting();
				reportName=getDataSource().getName();
				boolean isSucc = generalCurrentPdfFile(getDataSource());
				generateAuditReportLog(beginTimeRun);
				jsonObject.put("success", isSucc);
				return "json";
			} else if ("download".equals(operation)) {
				File file = new File(getInputPath());
				if (!file.exists()) {
					// commonly, logic should not come here
					addActionError(MgrUtil.getUserMessage("action.error.cannot.find.file"));
					// generateAuditLog(HmAuditLog.STATUS_FAILURE,
					// "Save support bundle");
					return SUCCESS;
				}
				return "download";
			} else {
				baseOperation();
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
			addActionError(MgrUtil.getUserMessage(e));
			return SUCCESS;
		}
	}
	public void prepare() throws Exception {
		super.prepare();
		setDataSource(AhReport.class);
		enableSorting();
		if (sortParams.getOrderBy().equals("id")) {
			sortParams.setPrimaryOrderBy("defaultFlag");
			sortParams.setPrimaryAscending(false);
		}
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_REPORT_PCI;
		setSelectedL2Feature(L2_FEATURE_SECURITYPCICOMPLIANCE);
	}
	
	public AhReport getDataSource() {
		return (AhReport) dataSource;
	}
	
	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}
	
	public int getNameLength() {
		return getAttributeLength("name");
	}
	
	public String getHideSchedule() {
		if (getDataSource().getExcuteType().equals("2")) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getHideImmediately() {
		if (getDataSource().getExcuteType().equals("2")) {
			return "none";
		} else {
			return "";
		}
	}
	
	public List<CheckItem> getLstHours() {
		List<CheckItem> lstHour = new ArrayList<CheckItem>();
		for (int i = 0; i < 24; i++) {
			lstHour.add(new CheckItem((long) i, String.valueOf(i + " hour")));
		}
		return lstHour;
	}
	
	public EnumItem[] getEnumReportPeriod() {
		return AhReport.REPORT_PERIOD_TYPE;
	}
	
	public String getShowRecurrence() {
		if (getDataSource().getEnabledRecurrence() && getDataSource().getExcuteType().equals("2")) {
			return "";
		} else {
			return "none";
		}
	}
	
	public EnumItem[] getEnumWeekDay() {
		return AhReport.REPORT_WEEKDAY_TYPE;
	}
	
	protected void prepareGuiSetting(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.setTimeZone(tz);
		long tmpStartTime = getDataSource().getStartTime();
		if (tmpStartTime != 0) {
			Calendar ca = Calendar.getInstance(tz);
			ca.setTimeInMillis(tmpStartTime);
			startTimeForSchedule = formatter.format(ca.getTime());
			startHour = ca.get(Calendar.HOUR_OF_DAY);
		} else {
			Calendar ca = Calendar.getInstance(tz);
			startTimeForSchedule = formatter.format(ca.getTime());
			startHour=0;
		}
		
		if (getDataSource().getPciStartTime() != 0) {
			Calendar ca = Calendar.getInstance(tz);
			ca.setTimeInMillis(getDataSource().getPciStartTime());
			startTime = formatter.format(ca.getTime());
		} else {
			Calendar ca = Calendar.getInstance(tz);
			startTime = formatter.format(ca.getTime());
		}
		
		if (getDataSource().getPciEndTime() != 0) {
			Calendar ca = Calendar.getInstance(tz);
			ca.setTimeInMillis(getDataSource().getPciEndTime());
			endTime = formatter.format(ca.getTime());
		} else {
			Calendar ca = Calendar.getInstance(tz);
			endTime = formatter.format(ca.getTime());
		}
		
		if (getDataSource().getLocation() != null) {
			locationId = getDataSource().getLocation().getId();
		}
	}
	
	public void saveGuiValue() throws Exception {
		if (getDataSource().getExcuteType().equals("2")) {
			if (startTimeForSchedule != null && !startTimeForSchedule.equals("")) {
				String datetime[] = startTimeForSchedule.split("-");
				Calendar calendar = Calendar.getInstance(tz);
				calendar.clear(Calendar.MINUTE);
				calendar.clear(Calendar.SECOND);
				calendar.clear(Calendar.MILLISECOND);
				calendar.set(Calendar.YEAR, Integer.parseInt(datetime[0]));
				calendar.set(Calendar.MONTH, Integer.parseInt(datetime[1]) - 1);
				calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datetime[2]));
				calendar.set(Calendar.HOUR_OF_DAY, startHour);
				getDataSource().setStartTime(calendar.getTimeInMillis());
			} else {
				getDataSource().setStartTime(0);
			}
			
			getDataSource().setPciStartTime(0);
			getDataSource().setPciEndTime(0);
		} else {
			if (startTime != null && !startTime.equals("")) {
				String datetime[] = startTime.split("-");
				Calendar calendar = Calendar.getInstance(tz);
				calendar.clear(Calendar.MINUTE);
				calendar.clear(Calendar.SECOND);
				calendar.clear(Calendar.MILLISECOND);
				calendar.set(Calendar.YEAR, Integer.parseInt(datetime[0]));
				calendar.set(Calendar.MONTH, Integer.parseInt(datetime[1]) - 1);
				calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datetime[2]));
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				getDataSource().setPciStartTime(calendar.getTimeInMillis());
			} else {
				getDataSource().setPciStartTime(0);
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
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				getDataSource().setPciEndTime(calendar.getTimeInMillis());
			} else {
				getDataSource().setPciEndTime(0);
			}
			
			getDataSource().setStartTime(0);
			getDataSource().setReportPeriod(AhReport.REPORT_PERIOD_LASTONEDAY);
			getDataSource().setEnabledRecurrence(false);
			getDataSource().setRecurrenceType("1");
			getDataSource().setWeekDay(AhReport.REPORT_WEEKDAY_SUNDAY);
			getDataSource().setEmailAddress("");
		}
		
		if (locationId != null && locationId > -1) {
			MapContainerNode location = findBoById(MapContainerNode.class,
					locationId);
			getDataSource().setLocation(location);
		} else {
			getDataSource().setLocation(null);
		}
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
	
	public String getChangedReportName() {
		return getDataSource().getName().replace("\\", "\\\\").replace("'", "\\'");
	}
	
	public void generateAuditReportLog(long beginTimeRun) {
		long diffTimer = System.currentTimeMillis() - beginTimeRun;
		String diffTimerStr = diffTimer > 1000 ? diffTimer/1000 + "s" : diffTimer + "ms";
		generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.pci.report.time.used") + diffTimerStr);
	}
	
	//private final String mailFileNamePdf = "currentPCIReport.pdf";
	public String getInputPath() {
		return AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
		+ getDomain().getDomainName() + File.separator + getLocalFileName();
	}
	public String getLocalFileName() {
		return "currentPCIReport_" + getDataSource().getName() + ".pdf";
	}

	public InputStream getInputStream() throws Exception {
		return new FileInputStream(getInputPath());
	}
	
	public synchronized boolean generalCurrentPdfFile(AhReport profile) throws Exception {
		try {
			
			String currentDir = AhPerformanceScheduleModule.fileDirPathCurrent + File.separator
					+ getDomain().getDomainName();
			PCIReportGenerateImpl geImp = new PCIReportGenerateImpl(
					profile, tz,currentDir,getLocalFileName(),getStartTimeLong(),getEndTimeLong());
			return geImp.generatePciReport();
        } catch(Exception ioe) {
            ioe.printStackTrace();
            return false;
        }
	}
	
	public static TimeZone tz;
	private String startTime;
	private String endTime;
	private String startTimeForSchedule;
	private int startHour;
	private Long locationId;
	private String reportName;
	
	public long getStartTimeLong(){
		Calendar cal = Calendar.getInstance();
		cal.clear(Calendar.MILLISECOND);
		cal.setTimeZone(getUserTimeZone());
		String[] startTimeString = startTime.split("-");
		cal.set(Integer.parseInt(startTimeString[0]), Integer.parseInt(startTimeString[1])-1, Integer.parseInt(startTimeString[2]), 0, 0, 0);
		return cal.getTimeInMillis();
	}
	
	public long getEndTimeLong(){
		Calendar cal = Calendar.getInstance();
		cal.clear(Calendar.MILLISECOND);
		cal.setTimeZone(getUserTimeZone());
		String[] endTimeString = endTime.split("-");
		cal.set(Integer.parseInt(endTimeString[0]), Integer.parseInt(endTimeString[1])-1, Integer.parseInt(endTimeString[2]), 0, 0, 0);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return cal.getTimeInMillis();
	}

	public List<CheckItem> getLocation() {
		List<CheckItem> listLocation = getMapListView();
		listLocation.add(0, new CheckItem((long) -1, ""));
		return listLocation;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public Long getLocationId() {
		return locationId;
	}
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	private String globalDevicePwd = "";

	public String getStartTimeForSchedule() {
		return startTimeForSchedule;
	}

	public void setStartTimeForSchedule(String startTimeForSchedule) {
		this.startTimeForSchedule = startTimeForSchedule;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
		if(getDataSource()!=null && reportName!=null && !reportName.isEmpty()) {
			getDataSource().setName(reportName);
		}
	}
}