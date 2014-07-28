package com.ah.ui.actions.monitor;

/*
 * @author Fisher
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.ah.be.app.DebugUtil;
import com.ah.be.performance.AhPerformanceScheduleModule;
import com.ah.be.performance.BePerformScheduleImpl;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.performance.AhAdminLoginSession;
import com.ah.bo.performance.AhReport;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.Navigation;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

public class ReportLoginUserAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	public static final int CLIENT_DE_AUTH_CODE = 117440512;
	public static TimeZone tz;
	public static final String REPORT_LIST_TYPE = "reportListType";
	
	private String listType;

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
		tz = getUserTimeZone();
		if (listType == null || listType.equals("")) {
			if (MgrUtil.getSessionAttribute(REPORT_LIST_TYPE)!=null) {
				listType = MgrUtil.getSessionAttribute(REPORT_LIST_TYPE).toString();
			} else {
				listType =Navigation.L2_FEATURE_SUMMARYUSAGE;
			}
		} else {
			MgrUtil.setSessionAttribute(REPORT_LIST_TYPE, listType);
		}
		
		filterParams = new FilterParams("reportType", listType);
		setSelectedL1Feature(Navigation.L1_FEATURE_MONITOR);
		setSelectedL2Feature(listType);
		resetPermission();
		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(listType + " > New")) {
					return getLstForward();
				}
				setSessionDataSource(new AhReport());
				getDataSource().setReportType(listType);
				getDataSource().setOwner(getDomain());
				return getDataSource().getReportType();
			} else if ("create".equals(operation)) {
				if (checkNameExists("name=:s1 and reportType=:s2", new Object[] {
						getDataSource().getName(), getDataSource().getReportType() })) {
					return getDataSource().getReportType();
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
			} else if ("edit".equals(operation)) {
				editBo();
				if (dataSource != null) {
					if (getDataSource().getId() != null) {
						findBoById(AhReport.class, getDataSource().getId(), this);
					}
					setSessionDataSource(dataSource);
				}
				prepareGuiSetting();
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
				return getDataSource().getReportType();
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
			} else if ("run".equals(operation) 
					|| "changePageSize".equals(operation)
					|| "changeNextPage".equals(operation)
					|| "changeGotoPage".equals(operation)
					|| "changePreviousPage".equals(operation)
					|| "changeSort".equals(operation)){
				if (getDataSource() == null) {
					long cloneId = getSelectedIds().get(0);
					AhReport ahReport = (AhReport) findBoById(boClass, cloneId, this);
					setId(cloneId);
					setSessionDataSource(ahReport);
				} else {
					saveGuiValue();
				}
				if ("changeSort".equalsIgnoreCase(operation)){
					if (getDataSource().getCuPageSort()==sortIndex){
						getDataSource().setCuPageSortDesc(!getDataSource().getCuPageSortDesc());
					} else {
						getDataSource().setCuPageSortDesc(false);
					}
					getDataSource().setCuPageSort(sortIndex);
					
				}
				prepareGuiSetting();
				showReportTab = true;
				searchReportData();
				if ("changePreviousPage".equals(operation)){
					getDataSource().setCuPageIndex(getDataSource().getCuPageIndex()-1);
				} else if ("changeNextPage".equals(operation)){
					getDataSource().setCuPageIndex(getDataSource().getCuPageIndex()+1);
				} else if ("changeGotoPage".equals(operation)){
					if (!cuSearchGotoPage.equals("")){
						try {
							getDataSource().setCuPageIndex(Integer.parseInt(cuSearchGotoPage));
							cuSearchGotoPage="";
						} catch (Exception ex){
							cuSearchGotoPage="";
							ex.printStackTrace();
						}
					}
				}
				resetPageInfo();
				if ("run".equals(operation)){
					generalCSVAndEmail();
				}
				return getDataSource().getReportType();		
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
				searchReportData();
				boolean isSucc = generalCurrentCvsFile();
				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				return "json";
			} else if ("download".equals(operation)) {
				setId(getDataSource().getId());
				if (!getUpdateDisabled().equals("")) {
					addActionError("User '" + getUserContext().getUserName()
							+ "' does not have WRITE access to object '"
							+ getDataSource().getLabel() + "'.");
					return INPUT;
				}
				File file = new File(getInputPath());
				if (!file.exists()) {
					// commonly, logic should not come here
					addActionError(MgrUtil.getUserMessage("action.error.cannot.find.file"));
					// generateAuditLog(HmAuditLog.STATUS_FAILURE,
					// "Save support bundle");
					return INPUT;
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
			return prepareActionError(e);
		}
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
		return null;
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
	
	protected void searchReportData(){
		if (getDataSource().getReportType().equalsIgnoreCase(Navigation.L2_FEATURE_SUMMARYUSAGE)){
			String sumLoginTimeSQL= "select username,owner,sum(totallogintime),count(id) " +
					"from ah_adminlogin_session where logintime>=" + getReportDateTime().getTimeInMillis() + 
					" group by username, owner";
			List<?> lstSumLoginTime = QueryUtil.executeNativeQuery(sumLoginTimeSQL);
			
			String uniqueAdminSQL= "select DISTINCT ON (username,owner) id " +
					"from ah_adminlogin_session where logintime>=" + getReportDateTime().getTimeInMillis() +
					" order by username,owner, logintime desc";
			List<?> lstUniqueAdminId = QueryUtil.executeNativeQuery(uniqueAdminSQL);
			
	
			String apCountSQL= "select owner,count(id) from hive_ap " +
					" where managestatus=1 group by owner";
			List<?> lstApCount = QueryUtil.executeNativeQuery(apCountSQL);
			
			FilterParams myFilterParams=null;
			if (!lstUniqueAdminId.isEmpty()){
				List<Long> filterIds = new ArrayList<Long>();
				for(Object object: lstUniqueAdminId){
					filterIds.add(Long.parseLong(object.toString()));
				}
				myFilterParams = new FilterParams("id", filterIds);
			}
			long sysTime = System.currentTimeMillis();
			
			if (myFilterParams!=null){
				reportResult = QueryUtil.executeQuery(AhAdminLoginSession.class, null, myFilterParams);
				List<HmUser> lstHmUser = QueryUtil.executeQuery(HmUser.class, null, new FilterParams("defaultFlag",true));
				for(HmUser oneUser:lstHmUser){
					boolean needAdd = true;
					for(AhAdminLoginSession oneObjFinal:reportResult){
						if (oneObjFinal.getOwner().getId().equals(oneUser.getOwner().getId())){
							needAdd=false;
							break;
						}
					}
					if (needAdd){
						AhAdminLoginSession needaddClass = new AhAdminLoginSession();
						needaddClass.setApCount(0);
						needaddClass.setCurrentLoginCount(0);
						needaddClass.setEmailAddress(oneUser.getEmailAddress());
						needaddClass.setLoginCount(0);
						needaddClass.setOwner(oneUser.getDomain());
						needaddClass.setPlannerAdminTimeZone(tz);
						needaddClass.setTimeZone(oneUser.getTimeZone());
						needaddClass.setTotalLoginTime(0);
						needaddClass.setUserFullName(oneUser.getUserFullName());
						needaddClass.setUserName(oneUser.getUserName());
						reportResult.add(needaddClass);
					}
				}
				for (AhAdminLoginSession oneUser : reportResult) {
					oneUser.setPlannerAdminTimeZone(tz);
					if (lstSumLoginTime!=null){
						for(Object sumOne:lstSumLoginTime){
							Object[] oneObjectSum = (Object[])sumOne;
							if (oneObjectSum[0].toString().equalsIgnoreCase(oneUser.getUserName())
									&& oneObjectSum[1].toString().equals(oneUser.getOwner().getId().toString())) {
								oneUser.setTotalLoginTime(Long.parseLong(oneObjectSum[2].toString()));
								oneUser.setLoginCount(Long.parseLong(oneObjectSum[3].toString()));
							}
						}
					}
					if (lstApCount!=null){
						for(Object oneApCount:lstApCount){
							Object[] oneObjectApCount = (Object[])oneApCount;
							if (oneObjectApCount[0].toString().equals(oneUser.getOwner().getId().toString())) {
								oneUser.setApCount(Long.parseLong(oneObjectApCount[1].toString()));
							}
						}
					}
					
					for (HttpSession activeUser : CurrentUserCache.getInstance()
							.getActiveSessions()) {
						HmUser sessionUser;
						try {
							sessionUser = (HmUser) activeUser.getAttribute(USER_CONTEXT);
						} catch (Exception e){
							continue;
						}
						if (sessionUser != null) {
							if (sessionUser.getUserName().equalsIgnoreCase(oneUser.getUserName())
									&& sessionUser.getDomain().getId().equals(oneUser.getOwner().getId())) {
								if (sysTime - activeUser.getCreationTime() >= 0) {
									oneUser.setTotalLoginTime(oneUser.getTotalLoginTime()
											+ sysTime - activeUser.getCreationTime());
								}
								if (oneUser.getLoginTime()<activeUser.getCreationTime()){
									oneUser.setLoginTime(activeUser.getCreationTime());
								}
								oneUser.setCurrentLoginCount(oneUser.getCurrentLoginCount() + 1);
								oneUser.setLoginCount(oneUser.getLoginCount() + 1);
							}
						}
					}
				} 
			} else {
				Map<String,AhAdminLoginSession> userLoginMap = new HashMap<String,AhAdminLoginSession>();

				for (HttpSession activeUser : CurrentUserCache.getInstance()
						.getActiveSessions()) {
					HmUser sessionUser;
					try {
						sessionUser = (HmUser) activeUser.getAttribute(USER_CONTEXT);
					} catch (Exception e) {
						continue;
					}
					if (sessionUser != null) {
						String keyValue = sessionUser.getOwner().getId().toString();
						if (userLoginMap.get(keyValue)==null) {
							userLoginMap.put(keyValue, new AhAdminLoginSession());
						}
						AhAdminLoginSession oneUser = userLoginMap.get(keyValue);
						
						List<Short> cvgList = new ArrayList<>();
						cvgList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
						cvgList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY);
						long apCount = QueryUtil.findRowCount(HiveAp.class, new FilterParams(
								"manageStatus = :s1 and owner.id=:s2 " +
								" and (deviceType=:s3 or deviceType=:s4 or deviceType=:s5) " +
								" and hiveApModel not in (:s6)", 
								new Object[] { HiveAp.STATUS_MANAGED, 
										sessionUser.getOwner().getId(), HiveAp.Device_TYPE_HIVEAP, 
										HiveAp.Device_TYPE_BRANCH_ROUTER, HiveAp.Device_TYPE_VPN_BR, cvgList}));
						oneUser.setApCount(apCount);
						
						oneUser.setCurrentLoginCount(oneUser.getCurrentLoginCount()+1);
						oneUser.setEmailAddress(sessionUser.getEmailAddress());
						oneUser.setLoginCount(oneUser.getLoginCount()+1);
						if (oneUser.getLoginTime()<activeUser.getCreationTime()){
							oneUser.setLoginTime(activeUser.getCreationTime());
						}
						oneUser.setOwner(sessionUser.getDomain());
						oneUser.setPlannerAdminTimeZone(tz);
						oneUser.setTimeZone(sessionUser.getTimeZone());
						oneUser.setTotalLoginTime(oneUser.getTotalLoginTime() + sysTime - activeUser.getCreationTime());
						oneUser.setUserFullName(sessionUser.getUserFullName());
						oneUser.setUserName(sessionUser.getUserName());
					}
				}
				reportResult = new ArrayList<AhAdminLoginSession>();
				for(String key:userLoginMap.keySet()){
					reportResult.add(userLoginMap.get(key));
				}
				List<HmUser> lstHmUser = QueryUtil.executeQuery(HmUser.class, null, new FilterParams("defaultFlag",true));
				for(HmUser oneUser:lstHmUser){
					boolean needAdd = true;
					for(AhAdminLoginSession oneObjFinal:reportResult){
						if (oneObjFinal.getOwner().getId().equals(oneUser.getOwner().getId())){
							needAdd=false;
							break;
						}
					}
					if (needAdd){
						AhAdminLoginSession needaddClass = new AhAdminLoginSession();
						needaddClass.setApCount(0);
						needaddClass.setCurrentLoginCount(0);
						needaddClass.setEmailAddress(oneUser.getEmailAddress());
						needaddClass.setLoginCount(0);
						needaddClass.setOwner(oneUser.getDomain());
						needaddClass.setPlannerAdminTimeZone(tz);
						needaddClass.setTimeZone(oneUser.getTimeZone());
						needaddClass.setTotalLoginTime(0);
						needaddClass.setUserFullName(oneUser.getUserFullName());
						needaddClass.setUserName(oneUser.getUserName());
						reportResult.add(needaddClass);
					}
				}
			}
		} else {
			FilterParams myFilterParams=new FilterParams("loginTime>=:s1 and owner.domainName=:s2",
					new Object[]{getReportDateTime().getTimeInMillis(),getDataSource().getDetailDomainName()});
			reportResult = QueryUtil.executeQuery(AhAdminLoginSession.class, null, myFilterParams);
			for(AhAdminLoginSession oneSession:reportResult){
				oneSession.setPlannerAdminTimeZone(tz);
			}
		}
		
		if (reportResult!=null && !reportResult.isEmpty()){
			Collections.sort(reportResult, new Comparator<AhAdminLoginSession>() {
				@Override
				public int compare(AhAdminLoginSession o1, AhAdminLoginSession o2) {
					double ret = 0;
					if (getDataSource().getCuPageSort()==1){
						ret = o1.getUserName().compareToIgnoreCase(o2.getUserName());
					} else if (getDataSource().getCuPageSort()==2){
						ret = o1.getUserFullName().compareToIgnoreCase(o2.getUserFullName());
					} else if (getDataSource().getCuPageSort()==3){
						ret = o1.getEmailAddress().compareToIgnoreCase(o2.getEmailAddress());
					} else if (getDataSource().getCuPageSort()==4){
						ret = o1.getApCount()-o2.getApCount();
					} else if (getDataSource().getCuPageSort()==5){
						ret = o1.getLoginCount()-o2.getLoginCount();
					} else if (getDataSource().getCuPageSort()==6){
						ret = o1.getCurrentLoginCount()-o2.getCurrentLoginCount();
					} else if (getDataSource().getCuPageSort()==7){
						ret = o1.getTotalLoginTime()-o2.getTotalLoginTime();
					} else if (getDataSource().getCuPageSort()==8){
						ret = o1.getLoginTime()-o2.getLoginTime();
					}
					if (!getDataSource().getCuPageSortDesc()) {
						ret = ret * -1;
					}
					if (ret > 0) {
						return 1;
					} else if (ret < 0) {
						return -1;
					}
					return 0;
				}
			});
		}
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
//					List<MailNotification> mailNotification = QueryUtil.executeQuery(MailNotification.class, null,
//							null,getDomain().getId());
//					String serverName = "";
//					String mailFrom = "";
//					if (!mailNotification.isEmpty()) {
//						serverName = mailNotification.get(0).getServerName();
//						mailFrom = mailNotification.get(0).getMailFrom();
//					}
//					if (serverName != null && !serverName.equals("") && mailFrom != null
//							&& !mailFrom.equals("")) {
//						AhPerformanceScheduleModule.mailCsvFile(getDataSource(), getDomain()
//								.getDomainName(), serverName, mailFrom,tz);
//					}
					
					AhPerformanceScheduleModule.mailCsvFile(getDataSource(),tz);
				}
			}
		}
	}
	
	public boolean generalCurrentCvsFile(){
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
			
			if (getDataSource().getReportType().equals(L2_FEATURE_SUMMARYUSAGE)) {
				strOutput = new StringBuffer();
				strOutput.append(getText("admin.user.userName")).append(",");
				strOutput.append(getText("admin.user.userFullName")).append(",");
				strOutput.append(getText("admin.user.emailAddress")).append(",");
				strOutput.append(getText("report.plannerInfo.apCount")).append(",");
				strOutput.append(getText("report.plannerInfo.loginCount")).append(",");
				strOutput.append(getText("report.plannerInfo.currentLoginCount")).append(",");
				strOutput.append(getText("report.plannerInfo.totalLoginTime")).append(",");
				strOutput.append(getText("report.plannerInfo.lastLoginTime")).append(",");
				strOutput.append(getText("config.domain"));
				strOutput.append("\n");
				out.write(strOutput.toString());

				if (reportResult!=null && !reportResult.isEmpty()){
					strOutput = new StringBuffer();
					for (AhAdminLoginSession oneSession : reportResult) {
						strOutput.append("\"").append(oneSession.getUserName()).append("\",");
						strOutput.append("\"").append(oneSession.getUserFullName() == null ? "" : oneSession.getUserFullName()).append("\",");
						strOutput.append("\"").append(oneSession.getEmailAddress()).append("\",");
						strOutput.append("\"").append(oneSession.getApCount()).append("\",");
						strOutput.append("\"").append(oneSession.getLoginCount()).append("\",");
						strOutput.append("\"").append(oneSession.getCurrentLoginCount()).append("\",");
						strOutput.append("\"").append(oneSession.getTotalLoginTimeString()).append("\",");
						strOutput.append("\"").append(oneSession.getLastLoginTimeString()).append("\",");
						strOutput.append("\"").append(oneSession.getOwner().getDomainName()).append("\"");
						strOutput.append("\n");
					}
					out.write(strOutput.toString());
				}
			} else {
				strOutput = new StringBuffer();
				strOutput.append(getText("admin.user.userName")).append(",");
				strOutput.append(getText("admin.user.userFullName")).append(",");
				strOutput.append(getText("admin.user.emailAddress")).append(",");
				strOutput.append(getText("report.plannerInfo.apCount")).append(",");
				strOutput.append(getText("report.plannerInfo.sessionTime")).append(",");
				strOutput.append(getText("report.plannerInfo.loginTime")).append(",");
				strOutput.append(getText("config.domain"));
				strOutput.append("\n");
				out.write(strOutput.toString());
				if (reportResult!=null && !reportResult.isEmpty()){
					strOutput = new StringBuffer();
					for (AhAdminLoginSession oneSession : reportResult) {
						strOutput.append("\"").append(oneSession.getUserName()).append("\",");
						strOutput.append("\"").append(oneSession.getUserFullName() == null ? "" : oneSession.getUserFullName()).append("\",");
						strOutput.append("\"").append(oneSession.getEmailAddress()).append("\",");
						strOutput.append("\"").append(oneSession.getApCount()).append("\",");
						strOutput.append("\"").append(oneSession.getTotalLoginTimeString()).append("\",");
						strOutput.append("\"").append(oneSession.getLastLoginTimeString()).append("\",");
						strOutput.append("\"").append(oneSession.getOwner().getDomainName()).append("\"");
						strOutput.append("\n");
					}
					out.write(strOutput.toString());
				}
			}
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			DebugUtil.performanceDebugWarn("exportCurrentData in report:", e);
			return false;
		}
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
		calendar.add(Calendar.DAY_OF_MONTH, reportDay * -1);
		calendar.add(Calendar.MONTH, reportMonth * -1);
		return calendar;
	}
	
	private String startTime;
	private int startHour;
	private boolean showReportTab;
	private List<AhAdminLoginSession> reportResult;
	private String cuSearchGotoPage="";
	private String tabIndex = "";
	private int sortIndex=0;

	public void prepare() throws Exception {
		super.prepare();
		setDataSource(AhReport.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_REPORT;
	}
	public AhReport getDataSource() {
		return (AhReport) dataSource;
	}
	
	private final String mailFileName = "currentAdminReportData.csv";
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
	
	public void resetPageInfo(){
		if (reportResult!= null && !reportResult.isEmpty()) {
			if (getDataSource().getCuPageIndex() ==0){
				getDataSource().setCuPageIndex(1);
			}
			if (reportResult.size()%getDataSource().getCuPageSize()!=0){
				getDataSource().setCuPageCount(reportResult.size()/getDataSource().getCuPageSize()+1);
			} else {
				getDataSource().setCuPageCount(reportResult.size()/getDataSource().getCuPageSize());
			}
			if (getDataSource().getCuPageIndex()>getDataSource().getCuPageCount()){
				getDataSource().setCuPageIndex(getDataSource().getCuPageCount());
			}
		} else {
			getDataSource().setCuPageIndex(0);
			getDataSource().setCuPageCount(0);
		}
	}
	
	public String getShowDetailDomain() {
		if (getDataSource().getReportType().equals(L2_FEATURE_DETAILUSAGE)) {
			return "";
		}
		return "none";
	}

	public String getChangedReportName() {
		return getDataSource().getName().replace("\\", "\\\\").replace("'", "\\'");
	}
	
	public int getNameLength() {
		return getAttributeLength("name");
	}
	
	public EnumItem[] getEnumReportPeriod() {
		return AhReport.REPORT_PERIOD_TYPE;
	}
	
	public EnumItem[] getEnumWeekDay() {
		return AhReport.REPORT_WEEKDAY_TYPE;
	}
	
	public int getCuStartItem(){
		return (getDataSource().getCuPageIndex()-1) * getDataSource().getCuPageSize();
	}
	public int getCuEndItem(){
		return getDataSource().getCuPageIndex() * getDataSource().getCuPageSize();
	}
	
	public List<String> getDetailDomain() {
		String sql = "SELECT bo.domainName FROM " + HmDomain.class.getSimpleName() + " bo";
		List<?> bos = QueryUtil.executeQuery(sql, new SortParams("domainName"), null, null);
		List<String> items = new ArrayList<String>();
		for (Object obj : bos) {
			if (HmDomain.GLOBAL_DOMAIN.equals(obj.toString())) {
					continue;
			}
			items.add(obj.toString());
		}
		return items;
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
	
	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public boolean getEmailUsabled() {
		return !getUpdateDisabled().equals("");
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

	public List<AhAdminLoginSession> getReportResult() {
		return reportResult;
	}

	public void setReportResult(List<AhAdminLoginSession> reportResult) {
		this.reportResult = reportResult;
	}

	public String getCuSearchGotoPage() {
		return cuSearchGotoPage;
	}

	public void setCuSearchGotoPage(String cuSearchGotoPage) {
		this.cuSearchGotoPage = cuSearchGotoPage;
	}

	public String getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(String tabIndex) {
		this.tabIndex = tabIndex;
	}

	public boolean getShowReportTab() {
		return showReportTab;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

}