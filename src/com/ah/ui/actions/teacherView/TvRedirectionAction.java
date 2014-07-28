/**
 * @filename			TvRedirectionAction.java
 * @version
 * @author				Administrator
 * @since
 *
 * Copyright (c) 2006-2010 Aerohive Co., Ltd.
 * All right reserved.
 */
package com.ah.ui.actions.teacherView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeMiscUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.misc.teacherview.ClearClassRequest;
import com.ah.be.misc.teacherview.TeacherViewException;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.teacherView.TvClass;
import com.ah.bo.teacherView.TvClassSchedule;
import com.ah.bo.teacherView.TvResourceMap;
import com.ah.bo.teacherView.ViewingClass;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.LoginAction;
import com.ah.util.CheckItem;
import com.ah.util.HmProxyUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.datetime.AhDateTimeUtil;

public class TvRedirectionAction extends LoginAction implements QueryBo {

	private static final long	serialVersionUID	= 1L;

	private static final Tracer log = new Tracer(TvRedirectionAction.class
											.getSimpleName());

	private static final String REDIRECT_ACTION_UPDATE			= "update";
	//private static final String REDIRECT_ACTION_APPEND			= "append";

	/**
	 * The replacement of AP address if students cannot find their host AP
	 */
	public static final String NONE_AP_IP						= "0.0.0.0";
	
	public static String NONE_AP_IP_MAC = NONE_AP_IP + "/" + "00:00:00:00:00:00";

	/**
	 * The maximum count of classes could be viewed by one designated AP
	 */
	private static final int MAX_VIEW_COUNT						= 8;

	private static final String CLASS_TIME_SEPERATOR			= ":";
	
	private static final int MAX_ADMIN_USER 				= 32;

	@Override
	public String execute() throws Exception {
		try {
			if (null == userContext) {
				return "redirectLogin";
			}
			if("classInfo".equals(operation)) {
				getClassDetail();
				return "json";
			} else if("teacherList".equals(operation)) {
				getTeacherList();
				return "json";
			} else if("refreshClassList".equals(operation)) {
				refreshClassList();
				return "json";
			} else if("redirect".equals(operation)) {
				if(executeRedirection()) {
					/*
					 * redirect user to HiveUI
					 */
					return "redirect";
				} else {
					/*
					 * redirection failed, error message will be added into
					 * the page.
					 */
					return SUCCESS;
				}
			}
			return SUCCESS;
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		userContext = getSessionUserContext();
		licTile = MgrUtil.getUserMessage("teacherView.title");

		// hm version information
		versionInfo = getSessionVersionInfo();
		if (null == versionInfo) {
			versionInfo = NmsUtil.getVersionInfo();

			if (null != versionInfo) {
				setSessionVersionInfo(versionInfo);
			}
		}

		if (null == userContext) {
			authTeacher();
		}
		domainId = QueryUtil.getDependentDomainFilter(userContext);
		setDataSource(TvClass.class);
	}

	/**
	 * Teacher view user has authentication by cas
	 *
	 * @throws Exception -
	 */
	public void authTeacher() throws Exception {
		// HA status
		if (HAUtil.isSlave()) {
			redirectUrl = getMasterURL() + "/teacherView.action";
			return;
		}

		redirectUrl = NmsUtil.getAuthServiceURL()+"/logout";
		if (!NmsUtil.isHTTPEnable() && !"https".equals(request.getScheme())) {
			// SSL redirect (check this only in case of HTTP not enabled)
		} else {
			// get user from cas server
			String user = request.getRemoteUser();
			if (null != user) {
				// the HiveManager is in maintenance mode
				if (HmBeOsUtil.getMaintenanceModeFromDb()) {
					redirectUrl += "?url=error.authentication.credentials.bad.maintenance";
					return;
				}
				// select by user name
				List<HmUser> allUsers = QueryUtil.executeQuery(HmUser.class, new SortParams("id"), new FilterParams("lower(userName) = :s1 AND userGroup.groupName = :s2",
					new Object[]{user.toLowerCase(), HmUserGroup.TEACHER}), null, this);

				if (allUsers.size() != 1) {
					// select by email address
					allUsers = QueryUtil.executeQuery(HmUser.class, new SortParams("id"), new FilterParams("lower(emailAddress) = :s1 AND userGroup.groupName = :s2",
						new Object[]{user.toLowerCase(), HmUserGroup.TEACHER}), null, this);
				}
				if (allUsers.size() == 1) {
					userContext = allUsers.get(0);
				}
			}
			if (null != userContext) {
				try {
					setSessionUserContext(userContext);
					userContext.setUserIpAddress(HmProxyUtil.getClientIp(request));

					initLicenseAndActKeyInfo();

					// check license and activation key
					if (null == licenseInfo || !getLicenseValidFlag() || !HmBeActivationUtil.ACTIVATION_KEY_VALID) {
						userContext = null;
						return;
					}

					refreshNavigationTree();
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("info.teacherView.teacher.login",
							new String[] {userContext.getUserName()}));
				} catch (Exception e) {
					userContext = null;
				}
			}
		}
	}

	/**
	 * This method is just to eliminate some javascript for feature 'hm search' in topPane.jsp.
	 * Because feature 'hm search' is not permitted in Teacher View
	 *
	 * @return -
	 */
	public boolean getTopoSearch() {
		return true;
	}

	/**
	 * Get classes which are taught by the current teacher user.
	 *
	 * @return	a <code>List</code> of <code>CheckItem</code> objects, <code>id</code> is the index ID of class
	 * 			in database table, <code>value</code> is spelled by CLASS_NAME + (SUBJECT).
	 * @author Joseph Chen
	 */
	public List<CheckItem> getTeacherClassList() {
		List<CheckItem> items = new ArrayList<CheckItem>();

		/*
		 * find classes from database
		 */
		List<TvClass> bos = QueryUtil.executeQuery(TvClass.class,
				null,
//				new FilterParams("teacherId", this.getUserContext().getUserName()),
				new FilterParams("lower(teacherId)", StringUtils.lowerCase(this.getUserContext().getEmailAddress())), // fix bug 23956 in Geneva,make VHM  with IDM can create teacher
				this.getDomainId(),
				this);

		if(bos.isEmpty()) {
			items.add(new CheckItem((long) CHECK_ITEM_ID_NONE, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
			return items;
		}

		/*
		 * put the most wanted class at the first place
		 */
		List<TvClass> orderedClass;

		if(bos.size() > 1) {
			orderedClass = moveTopClass(bos);
		} else {
			orderedClass = bos;
		}

		/*
		 * set initial subject and teacher
		 */
		TvClass currentClass = orderedClass.get(0);
		this.subject = currentClass.getSubject();
		this.teacher = currentClass.getTeacherId();

		/*
		 * add class info to CheckItem list
		 */
		for(TvClass tvClass : orderedClass) {
			String itemValue = new StringBuffer(tvClass.getClassName())
									.append("(")
									.append(tvClass.getSubject())
									.append(")").toString();

			items.add(new CheckItem(tvClass.getId(), itemValue));
		}

		return items;
	}

	/**
	 * Get all subjects of class in a school
	 *
	 * @return a <code>List</code> of <code>String</code> objects, element is subject name
	 * @author Joseph Chen
	 */
	public List<String> getSubjectList() {
		List<String> subjects = new ArrayList<String>();

		/*
		 * find subjects from database
		 */
		StringBuilder sql = new StringBuilder("SELECT DISTINCT subject FROM tv_class ")
								.append("WHERE owner=").append(this.getDomainId());
		List<?> objs = QueryUtil.executeNativeQuery(sql.toString());

		if(objs.isEmpty()) {
			subjects.add(MgrUtil
					.getUserMessage("config.optionsTransfer.none"));
			return subjects;
		}

		/*
		 * add subjects to list
		 */
		for(Object obj : objs) {
			subjects.add((String)obj);
		}

		return subjects;
	}

	private List<TvClass> moveTopClass(List<TvClass> classes) {
		/*
		 * check if there is class at today(weekday)
		 */
	//	String currentWeekDay = AhDateTimeUtil.getCurrentWeekDay(getUserTimeZone());

		TvClass topClass = null;

		for(TvClass tvClass : classes) {
			List<TvClassSchedule> periods = tvClass.getItems();

			if(periods.size() == 0) {
				continue;
			}

			for(TvClassSchedule period : periods) {
				if(period == null) {
					continue;
				}

				if(hasClassToday(period)) {
					if(isClassExpired(period)) {
						continue;
					}

					if(topClass == null) {
						topClass = tvClass;
					} else {
						if(moreClose(topClass, tvClass)) {
							topClass = tvClass;
						}
					}

					break;
				}
			}
		}

		/*
		 * if no class is at today, exit
		 */
		if(topClass == null) {
			return classes;
		}

		List<TvClass> newList = new ArrayList<TvClass>();
		newList.add(topClass);

		/*
		 * put classes into a linked list
		 */
		for(TvClass tvClass : classes) {
			if(!tvClass.getId().equals(topClass.getId())) {
				newList.add(tvClass);
			}
		}

		return newList;
	}

	private boolean moreClose(TvClass topClass, TvClass newClass) {
		return getTimeDistance(newClass) < getTimeDistance(topClass);
	}

	private long getTimeDistance(TvClass tvClass) {
		Calendar calClass = Calendar.getInstance(getUserTimeZone());
		calClass.setTimeInMillis(System.currentTimeMillis());

		/*
		 * set the start time of class to calClass
		 */

		for(TvClassSchedule period : tvClass.getItems()) {
			if(hasClassToday(period)) {
				/*
				 * set the HH and MM to calClass
				 */
				setClassTime(calClass, period.getStartTime());
				break;
			}
		}

		/*
		 * return the absolute value of subtraction
		 */
		return Math.abs(calClass.getTimeInMillis() - System.currentTimeMillis());
	}

	private void setClassTime(Calendar calendar, String time) {
		if(time == null) {
			return ;
		}

		String[] segments = time.split(CLASS_TIME_SEPERATOR);

		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(segments[0]));
		calendar.set(Calendar.MINUTE, Integer.parseInt(segments[1]));
	}

	private boolean isClassExpired(TvClassSchedule period) {
		if(period == null) {
			return true;
		}

		/*
		 * set HH and MM to a calendar of now
		 */
		Calendar calNow = Calendar.getInstance(getUserTimeZone());
		calNow.setTimeInMillis(System.currentTimeMillis());
		setClassTime(calNow, period.getEndTime());

		return System.currentTimeMillis() - calNow.getTimeInMillis() >= 0;
	}

	private void getClassDetail() throws JSONException {
		if(getClassId() == null || getClassId() == -1) {
			return ;
		}

		/*
		 * get class object from database
		 */
		TvClass aClass = QueryUtil.findBoById(TvClass.class, getClassId(), this);

		if(aClass == null) {
			return ;
		}

		List<TvClassSchedule> schedule = aClass.getItems();

		if(schedule == null) {
			return ;
		}

		/*
		 * add class info to JSON array
		 */
		jsonArray = new JSONArray();

		for(TvClassSchedule period : schedule) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("selected", isClassToday(period));
			jsonObject.put("day", period.getWeekdaySecString());
			jsonObject.put("start", period.getStartTime());
			jsonObject.put("end", period.getEndTime());
			jsonObject.put("room", period.getRoom());
			jsonArray.put(jsonObject);
		}
	}

	private boolean isClassToday(TvClassSchedule period) {
		return period != null && hasClassToday(period) && !isClassExpired(period);
	}

	private void getTeacherList() throws JSONException {
		if(getSubject() == null) {
			return ;
		}

		/*
		 * get teachers of a subject from database
		 */
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT teacherid FROM tv_class ");
		sql.append("WHERE subject=\'").append(NmsUtil.convertSqlStr(getSubject())).append("\'");
		sql.append("AND owner=").append(this.getDomainId());

		List<?> list = QueryUtil.executeNativeQuery(sql.toString());

		if(list.isEmpty()) {
			return ;
		}

		List<String> teachers = new ArrayList<String>();

		if(getClassId() != null) {
			TvClass theClass = QueryUtil.findBoById(TvClass.class, getClassId(), this);
			teachers.add(theClass.getTeacherId());
		}

		for(Object obj : list) {
			if(!teachers.contains(obj.toString())) {
				teachers.add((String)obj);
			}
		}

		/*
		 * add class info to JSON array
		 */
		jsonArray = new JSONArray();

		for(Object obj : teachers) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("teacher", obj);
			jsonArray.put(jsonObject);
		}
	}

	private void refreshClassList() throws JSONException {
		if(getTeacher() == null) {
			return ;
		}

		/*
		 * find classes from database
		 */
		List<TvClass> bos = QueryUtil.executeQuery(TvClass.class,
				null,
				new FilterParams("subject = :s1 AND teacherId = :s2",
						new String[] {getSubject(), getTeacher()}),
				this.getDomainId(),
				this);

		if(bos.isEmpty()) {
			return ;
		}

		/*
		 * put the most wanted class at the first place
		 */
		List<TvClass> orderedClass;

		if(bos.size() > 1) {
			orderedClass = moveTopClass(bos);
		} else {
			orderedClass = bos;
		}

		/*
		 * add class info to JSON array
		 */
		jsonArray = new JSONArray();

		for(TvClass tvClass : orderedClass) {
			String className = new StringBuffer(tvClass.getClassName())
									.append(" (")
									.append(tvClass.getSubject())
									.append(")").toString();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", tvClass.getId());
			jsonObject.put("name", className);
			jsonArray.put(jsonObject);
		}
	}

	private boolean executeRedirection() {
		/*
		 * get class name by class id
		 */
		TvClass tvClass = QueryUtil.findBoById(TvClass.class, getClassId(), this);

		if(tvClass == null) {
			this.addActionError(MgrUtil.getUserMessage("error.teacherView.class.notFound"));
			return false;
		}

		/*
		 * search for existing record in table viewing_class
		 */
		ViewingClass viewingClass = QueryUtil.findBoByAttribute(ViewingClass.class,
															"className",
															tvClass.getClassName(),
															this.getDomainId());

		if(viewingClass != null) { // class is being viewing
			String apAddress = viewingClass.getApIpAddress();
			String apMacAddress = viewingClass.getApMacAddress();
			ClassAP classAP = getClassAPStatus(apMacAddress);
			boolean isAPConnected = classAP == null ? false : classAP.isConnected();
			boolean isSptHttps = classAP == null ? false : classAP.isSptHttps();
			if(isAPConnected) {
				/*
				 * redirect to the AP
				 */
				return redirect(tvClass, apAddress, apMacAddress, isSptHttps);
			} else {
				/*
				 * an CAPWAP event should be send to tell the AP
				 *  to clear old student list
				 */
				try {
					log.info(MgrUtil.getUserMessage("info.teacherView.redirect.clear.event",
							new String[] {apAddress,
							tvClass.getClassName()}));
					HmBeMiscUtil.addClearClassRequest(new ClearClassRequest(apAddress,
														tvClass.getClassName()));
				} catch (Exception e) {
					log.error(MgrUtil.getUserMessage("error.teacherView.redirect.clear.event",
							new String[] {apAddress,
							tvClass.getClassName()}));
				}
			}
		}

		ApConnectedStudents designatedAP = null;

		try {
			designatedAP = getDesignatedAP(tvClass);
			if(designatedAP == null)
				throw new TeacherViewException(MgrUtil.getUserMessage("error.teacherView.redirect.no.online.student"));
		} catch(TeacherViewException e) {
			this.addActionError(e.getMessage());
			log.error(e.getMessage(), e);
			return false;
		} catch(Exception e) {
			this.addActionError(MgrUtil.getUserMessage("error.teacherView.redirect.unknown"));
			log.error(e.getMessage(), e);
			return false;
		}

		if(redirect(tvClass, designatedAP.getApAddress(),designatedAP.getMacAddress(), designatedAP.isSptHttps())) {
			addViewingClass(tvClass, designatedAP);
			return true;
		} else {
			return false;
		}
	}
	
	//We just can use macAddress to query the HiveAp information, if use IP, sometimes will get duplicate info.
	private ClassAP getClassAPStatus(String macAddress) {
		if(macAddress == null) {
			return null;
		}
		
		ClassAP classAP = new ClassAP();

		List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams("macAddress = :s1 and simulated = :s2 and hiveApModel in (:s3)", new Object[] { macAddress, false, AhConstantUtil.getTeacherViewSupportDevices()}), this.getDomainId());
		
		if(hiveAps == null || hiveAps.size() == 0){
			return classAP;
		}else{
			classAP.setConnected(hiveAps.get(0) != null && hiveAps.get(0).isConnected());	
			boolean after_Guadalupe = hiveAps.get(0) == null ? false : hiveAps.get(0).getSoftVer() != null && (NmsUtil.compareSoftwareVersion("6.1.6.0", hiveAps.get(0).getSoftVer()) <= 0);
			classAP.setSptHttps(after_Guadalupe);
		}

		return classAP;
	}

	private boolean redirect(TvClass tvClass, String apAddress, String apMacAddress, boolean isSptHttps) {
		/*
		 * download CAS and resource map to AP
		 */
		boolean downloadSucc = downloadTeacherViewSettings(apMacAddress);

		if(!downloadSucc) {
			this.addActionError(MgrUtil.getUserMessage("error.teacherView.redirect.download.settings",
					apAddress));
			return false;
		}

		this.setRedirectURL(getRedirectRequestURL(apAddress, isSptHttps));
		this.setClassName(tvClass.getClassName());
		this.setRosterType(tvClass.getRosterType());

		HMServicesSettings hmService = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", getDomain());
		if(hmService.isEnableTVProxy()){
			try{
				this.setProxyServer(encodeProxyServer(hmService.getTvProxyIP(),hmService.getTvProxyPort(),hmService.getTvAutoProxyFile()));
			}catch (TeacherViewException e) {
				addActionError(e.getMessage());
				return false;
			} catch (Exception e) {
				log.error(MgrUtil.getUserMessage("error.teacherView.redirect.encoding",
					"proxy server"), e);
			}
		}
		
		this.caseInsensitive = hmService.getEnableCaseSensitive();
		/*
		 * AP and Student list
		 */
		String apStudents = null;

		try {
			apStudents = encodeAPStudents(tvClass);
		} catch (Exception e) {
			log.error(MgrUtil.getUserMessage("error.teacherView.redirect.encoding"), e);
		}

		if(apStudents == null) {
			this.addActionError(MgrUtil.getUserMessage("error.teacherView.redirect.encoding"));
			return false;
		} else {
			this.setApStudents(apStudents);
		}
		
		try{
			this.setAdminUserName(encodeAdminUsers());
		}catch (TeacherViewException e) {
			addActionError(e.getMessage());
			return false;
		} catch (Exception e) {
			log.error(MgrUtil.getUserMessage("error.teacherView.redirect.encoding",
				"error to get the admin user name list"), e);
		}

		/*
		 * Expired Time of class
		 */
		String strExpiredTime = null;

		try {
			strExpiredTime = getClassExpiredTime(tvClass);

			if(strExpiredTime == null) {
				this.addActionError(MgrUtil.getUserMessage("error.teacherView.redirect.class.expired",
						new String[] {AhDateTimeUtil.getCurrentDate("HH:mm", getUserTimeZone()),
										getClassTime(tvClass)}));
				return false;
			}

			 strExpiredTime = String.valueOf((getExpiredTimeMillis(strExpiredTime)
					 - System.currentTimeMillis()) / 1000);
		}  catch(TeacherViewException e) {
			this.addActionError(e.getMessage());
			return false;
		} catch (Exception e) {
			log.error(MgrUtil.getUserMessage("error.teacherView.redirect.expiredTime",
							tvClass.getClassName()), e);
		}

		if(strExpiredTime == null) {
			this.addActionError(MgrUtil.getUserMessage("error.teacherView.redirect.class.expired",
					new String[] {AhDateTimeUtil.getCurrentDate("HH:mm", getUserTimeZone()),
					getClassTime(tvClass)}));
			return false;
		} else {
			this.setExpiredTime(strExpiredTime);
		}

		this.setRedirectAction(REDIRECT_ACTION_UPDATE);

		return true;
	}

	private String getClassTime(TvClass tvClass) {
		List<TvClassSchedule> schedule = tvClass.getItems();

		if(schedule == null || schedule.size() == 0) {
			return "Not Available";
		}

		TvClassSchedule lastPeriod = schedule.get(schedule.size() - 1);

		return lastPeriod.getStartTime() + " - " + lastPeriod.getEndTime();
	}

	private String getRedirectRequestURL(String apAddress, boolean isSptHttps) {
		StringBuilder url = new StringBuilder();
		if(isSptHttps){
			url.append("https://");
		}else{
			url.append("http://");
		}
		url.append(apAddress);
		url.append("/cmn/teacherView.php5");

		return url.toString();
	}

	private String encodeAPStudents(TvClass tvClass) throws Exception {
		Map<String, Map<String, String>> apStudents;

		if(tvClass.getApStudents() != null) {
			apStudents = tvClass.getApStudents();
		} else {
			apStudents = getAPStudents(tvClass);
			tvClass.setApStudents(apStudents);
		}

		String apStudentString;

		try {
			apStudentString = transformString(apStudents);
		} catch(Exception e) {
			log.error(MgrUtil.getUserMessage("error.teacherView.redirect.encode.json",
					tvClass.getClassName()), e);
			throw new TeacherViewException(MgrUtil.getUserMessage("error.teacherView.redirect.encode.json",
					tvClass.getClassName()));
		}

		return apStudentString;
	}
	
	private String encodeAdminUsers() throws Exception{
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT distinct userName FROM HmUser ");
		hql.append("WHERE owner = ").append(this.getDomainId());

		List<?> adminUsers = QueryUtil.executeQuery(hql.toString(), MAX_ADMIN_USER);
		List<String> adminUserNames = new ArrayList<String>(adminUsers.size());
		for(Object adminUser : adminUsers){
			adminUserNames.add((String)adminUser);			
		}
		
		String adminUserEncodeStr;
		
		try{
			adminUserEncodeStr = transformAdminUserStr(adminUserNames);
		}catch(Exception e) {
			log.error(MgrUtil.getUserMessage("error.teacherView.redirect.encode.adminuser.json"), e);
			throw new TeacherViewException(MgrUtil.getUserMessage("error.teacherView.redirect.encode.adminuser.json"));
		}
		
		adminUserEncodeStr = adminUserEncodeStr.replace("\\", "\\\\").replace("'", "\\'");
		return adminUserEncodeStr;
	}
	
	private String transformAdminUserStr(List<String> adminUsers) throws Exception{
		JSONObject adminUsersEle = new JSONObject();
		JSONArray adminUserArray = new JSONArray();
		for(String adminUser : adminUsers){			
			JSONObject nameElement = new JSONObject();
			nameElement.put("name", adminUser);
			adminUserArray.put(nameElement);
		}
		adminUsersEle.put("adminUserName",adminUserArray);
		String adminUserEncodeStr = adminUsersEle.toString().replace("\\", "\\\\").replace("'", "\\'");
		return adminUserEncodeStr;
	} 

	private String getClassExpiredTime(final TvClass tvClass) throws Exception {
		List<TvClassSchedule> schedule = tvClass.getItems();

		if(schedule == null) {
			throw new TeacherViewException(MgrUtil.getUserMessage("error.teacherView.class.no.schedule",
									tvClass.getClassName()));
		}

		String expiredTime = null;
		boolean hasClassToday = false;

		Map<Long, TvClassSchedule> timeMinusMap = new HashMap<Long, TvClassSchedule>();
		Long timeMinus;
		for(TvClassSchedule period : schedule) {
			if(period == null) {
				continue;
			}

			if(hasClassToday(period)) {
				hasClassToday = true;
				timeMinus = getExpiredTimeMillis(period.getEndTime()) - System.currentTimeMillis();
				if(timeMinus < 0) {
					/*
					 * the period is expired
					 */
					continue;
				}else{
					timeMinusMap.put(timeMinus,period);
				}
			}
		}

		if(timeMinusMap.size() > 0){
			Long minus = System.currentTimeMillis();
			for(Long timeMillis : timeMinusMap.keySet()){
				if(timeMillis < minus){
					minus = timeMillis;
				}
			}

			expiredTime = timeMinusMap.get(minus).getEndTime();
		}

		if(!hasClassToday) {
			throw new TeacherViewException(MgrUtil.getUserMessage("error.teacherView.redirect.today.no"));
		}

		return expiredTime;
	}

	private boolean hasClassToday(final TvClassSchedule period) {
		String periodWeekday = period.getWeekdaySecString();
		String currentWeekday = AhDateTimeUtil.getCurrentWeekDay(getUserTimeZone());

		if(TvClassSchedule.MONDAY_TO_FRIDAY.equals(period.getWeekdaySec())) {
			int day = AhDateTimeUtil.getDatePart(new Date(), "w", getUserTimeZone());

			if(day > Calendar.SUNDAY && day < Calendar.SATURDAY) {
				return true;
			}
		} else {
			if(periodWeekday.toLowerCase().contains(currentWeekday.toLowerCase().substring(0, 3))) {
				return true;
			}
		}

		return false;
	}

	private long getExpiredTimeMillis(String endTime) {
		if(endTime == null) {
			return 0;
		}

		Calendar calendar = Calendar.getInstance(getUserTimeZone());
		calendar.setTimeInMillis(System.currentTimeMillis());
		setClassTime(calendar, endTime);

		return calendar.getTimeInMillis();
	}

	private void addToMap(Map<String, Map<String, String>> map,
			Object studentId, Object studentName, Object ap) {
		String key;

		if(ap == null) {
			key = NONE_AP_IP_MAC;
		} else {
			SimpleHiveAp hiveAp = CacheMgmt.getInstance().getSimpleHiveAp(ap.toString());

			if(hiveAp != null) {
				short hiveapModel = hiveAp.getHiveApModel();

				if(hiveapModel == HiveAp.HIVEAP_MODEL_BR100 || HiveAp.isCVGAppliance(hiveapModel)) {
					key = NONE_AP_IP_MAC;
				} else {
					key = hiveAp.getIpAddress()+"/"+hiveAp.getMacAddress();
				}
			} else {
				key = NONE_AP_IP_MAC;
			}
		}

		if(map.containsKey(key)) {
			Map<String, String> values = map.get(key);
			values.put(studentId.toString(), studentName.toString());
		} else {
			Map<String, String> values = new HashMap<String, String>();
			values.put(studentId.toString(), studentName.toString());
			map.put(key, values);
		}
	}

	private String transformString(Map<String, Map<String, String>> apStudents) throws Exception{
		JSONArray apArray = new JSONArray();

		for(String key : apStudents.keySet()) {
			if(key == null) {
				continue;
			}

			JSONObject apElement = new JSONObject();
			apElement.put("ap", key);
			JSONArray studentArray = new JSONArray();

			Map<String, String> students = apStudents.get(key);

			for(String student : students.keySet()) {
				if(student == null) {
					continue;
				}

				JSONObject studentItem = new JSONObject();
				studentItem.put("id", student);
				studentItem.put("name", students.get(student));
				studentArray.put(studentItem);
			}

			apElement.put("students", studentArray);
			apArray.put(apElement);
		}

		String apArrayStr = apArray.toString();
		//To fix the bug 23065
		apArrayStr = apArrayStr.replace("\\", "\\\\");
		apArrayStr = apArrayStr.replace("'", "\\'");
		return apArrayStr;
	}

	private Map<String, Map<String, String>> getAPStudents(final TvClass tvClass) throws Exception {

		/*
		 * get student - mac mapping
		 * 1. get student from HM database
		 */
		StringBuilder sql = new StringBuilder();
		int rosterType = tvClass.getRosterType();

		if(rosterType == TvClass.TV_ROSTER_TYPE_STUDENT) { // student roster
			sql.append("SELECT studentid,studentname ");
			sql.append("FROM tv_student_roster ");
			sql.append("WHERE class_id = ").append(tvClass.getId()).append(" ");
			sql.append("AND owner = ").append(this.getDomainId());
		} else if(rosterType == TvClass.TV_ROSTER_TYPE_COMPUTERCART) { // computer cart
			sql.append("SELECT stumac,stuname ");
			sql.append("FROM tv_computer_cart_mac ");
			sql.append("WHERE tv_cart_id = ").append(tvClass.getComputerCart().getId());
		} else {
			throw new TeacherViewException(MgrUtil.getUserMessage("error.teacherView.class.no.student",
					tvClass.getClassName()));
		}

		List<?> studentResults = QueryUtil.executeNativeQuery(sql.toString());

		if(studentResults.isEmpty()) {
			throw new TeacherViewException(MgrUtil.getUserMessage("error.teacherView.class.no.student",
					tvClass.getClassName()));
		}

		/*
		 * get student - mac mapping
		 * 2. get mac from memory database
		 */
		StringBuilder sqlActiveClient = new StringBuilder();

		if(rosterType == TvClass.TV_ROSTER_TYPE_STUDENT) { // student roster
			sqlActiveClient.append("SELECT clientusername,apmac FROM ah_clientsession ");
			sqlActiveClient.append("WHERE lower(clientusername) IN (");
		} else if(rosterType == TvClass.TV_ROSTER_TYPE_COMPUTERCART) { // computer cart
			sqlActiveClient.append("SELECT clientmac,apmac FROM ah_clientsession ");
			sqlActiveClient.append("WHERE lower(clientmac) IN (");
		}


		int counter = 0;

		for(Object obj : studentResults) {
			Object[] columns = (Object[])obj;

			if(counter++ == studentResults.size() - 1) {
				sqlActiveClient.append("\'").append(columns[0].toString().toLowerCase()).append("\'");
			} else {
				sqlActiveClient.append("\'").append(columns[0].toString().toLowerCase()).append("\',");
			}
		}

		sqlActiveClient.append(")");

		List<?> clientResults = DBOperationUtil.executeQuery(sqlActiveClient.toString());

		if(clientResults == null) {
			throw new TeacherViewException(MgrUtil.getUserMessage("error.teacherView.redirect.no.online.student"));
		}

		/*
		 * get student - mac mapping
		 * 3. build student-mac mapping
		 */
		List<Object[]> studentMac = new ArrayList<Object[]>();

		for(Object obj : studentResults) {
			Object[] studentInfo = (Object[])obj;
			Object[] studentMacInfo = new Object[3];
			System.arraycopy(studentInfo, 0, studentMacInfo, 0, studentInfo.length);
			studentMacInfo[2] = getMacFromClientResult(studentInfo[0], clientResults);
			studentMac.add(studentMacInfo);
		}

		/*
		 * put data into a map<ap, List<student>>
		 */
		Map<String, Map<String, String>> apStudents = new HashMap<String, Map<String, String>>();

		for(Object obj : studentMac) {
			Object[] columns = (Object[])obj;

			/*
			 * find ap ip by ap mac
			 */
			addToMap(apStudents, columns[0], columns[1], columns[2]);
		}

		return apStudents;
	}

	private Object getMacFromClientResult(Object student, List<?> clientList) {
		Object mac = null;

		for(Object obj : clientList) {
			Object[] columns = (Object[])obj;

			if(student.toString().equalsIgnoreCase(columns[0].toString())) {
				mac = columns[1];
				break;
			}
		}

		return mac;
	}

	private ApConnectedStudents getDesignatedAP(TvClass tvClass) throws Exception {
		/*
		 * get AP and students
		 */
		Map<String, Map<String, String>> apStudents;

		if(tvClass.getApStudents() == null) {
			apStudents = getAPStudents(tvClass);
			tvClass.setApStudents(apStudents);
		} else {
			apStudents = tvClass.getApStudents();
		}

		/*
		 * sort APs by student count
		 */
		List<ApConnectedStudents> apList = new ArrayList<ApConnectedStudents>();

		for(String key : apStudents.keySet()) {
			String[] ipAndMAC = key.split("/");
			if(ipAndMAC.length != 2){
				throw new TeacherViewException(MgrUtil.getUserMessage("error.teacherView.redirect.ap.disconnected"));
			}
			String ipAddress = ipAndMAC[0];
			String macAddress = ipAndMAC[1];
						
			ClassAP classAP = getClassAPStatus(macAddress);
			boolean isSptHttps = classAP == null ? false : classAP.isSptHttps();
			
			ApConnectedStudents counter = new ApConnectedStudents();
			counter.setApAddress(ipAddress);
			counter.setMacAddress(macAddress);
			counter.setSptHttps(isSptHttps);

			if(apStudents.get(key) != null) {
				counter.setStudentCount(apStudents.get(key).size());
			} else {
				counter.setStudentCount(0);
			}

			apList.add(counter);
		}

		Collections.sort(apList);

		int maxCounter = 0; // an counter of AP which hosts 2 or more classes
		int disconnectCounter = 0; // an counter of AP which is disconnected
		int noApCounter = 0; // an counter of AP whose address is "0.0.0.0"

		/*
		 * test each AP to find a designated one
		 */
		for(ApConnectedStudents sc : apList) {
			String apAddress = sc.getApAddress();
			String macAddress = sc.getMacAddress();
			if("0.0.0.0".equals(apAddress)) {
				noApCounter++;
			} else if(getViewedCount(apAddress) >= MAX_VIEW_COUNT) {
				maxCounter++;
			} else {
				ClassAP classAP = getClassAPStatus(macAddress);
				boolean isAPConnected = classAP == null ? false : classAP.isConnected();
				if(isAPConnected) {
					return sc;
				} else {
					disconnectCounter++;
				}
			}
		}

		/*
		 * no designated AP is found, give reasons
		 */
		if(noApCounter == apList.size()) {
			throw new TeacherViewException(MgrUtil.getUserMessage("error.teacherView.redirect.no.online.student"));
		}

		if(maxCounter == apList.size()) {
			throw new TeacherViewException(MgrUtil.getUserMessage("error.teacherView.redirect.exceed.max",
					String.valueOf(MAX_VIEW_COUNT)));
		}

		if(disconnectCounter == apList.size()) {
			throw new TeacherViewException(MgrUtil.getUserMessage("error.teacherView.redirect.ap.disconnected"));
		}

		return null;
	}

	private long getViewedCount(String apAddress) {
		String where = "apIpAddress = :s1 AND owner.id = :s2";
		Object[] values = new Object[2];
		values[0] = apAddress;
		values[1] = this.getDomainId();
		return QueryUtil.findRowCount(ViewingClass.class, new FilterParams(where, values));
	}

	private void addViewingClass(final TvClass tvClass, ApConnectedStudents designatedAP) {
		ViewingClass viewingClass = new ViewingClass();
		viewingClass.setClassName(tvClass.getClassName());
		viewingClass.setApIpAddress(designatedAP.getApAddress());
		viewingClass.setApMacAddress(designatedAP.getMacAddress());
		viewingClass.setSelectedTime(System.currentTimeMillis());

		try {
			viewingClass.setEndTime(getExpiredTimeMillis(getClassExpiredTime(tvClass)));
		} catch(Exception e) {
			log.error(MgrUtil.getUserMessage("error.teacherView.class.endTime",
					tvClass.getClassName()), e);
		}

		viewingClass.setOwner(getDomain());

		try {
			QueryUtil.createBo(viewingClass);
		} catch (Exception e) {
			log.error(MgrUtil.getUserMessage("error.teacherView.class.record",
					tvClass.getClassName()), e);
		}
	}

	private boolean downloadTeacherViewSettings(String apMacAddress) {
		List<String> clis = new ArrayList<String>();

		/*
		 * CAS
		 */
		clis.addAll(getCASCLIs());
		/*
		 * resource map
		 */
		clis.addAll(getResourceMapCLIs());
		/*
		 * save configuration
		 */
		clis.add("save config\n");

		HiveAp hiveAp = QueryUtil.findBoByAttribute(HiveAp.class,
				"macAddress",
				apMacAddress,
				this.getDomainId());
		/*
		 * send event to AP
		 */
		BeCliEvent cliEvent = new BeCliEvent();
		cliEvent.setAp(hiveAp);
		String[] cliArray = new String[clis.size()];

		for(int i=0; i<clis.size(); i++) {
			cliArray[i] = clis.get(i);
		}

		cliEvent.setClis(cliArray);
		cliEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());

		try {
			cliEvent.buildPacket();
		} catch (BeCommunicationEncodeException e) {
			log.error(MgrUtil.getUserMessage("error.teacherView.redirect.cli.build")
								, e);
			return false;
		}

		int serialNum = HmBeCommunicationUtil.sendRequest(cliEvent);

		return serialNum != BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED;
	}

	private String[] getCASServer() {
		/*
		 * like "https://myhive-auth.aerohive.com:443/cas"
		 */
		String casAddress = NmsUtil.getAuthServiceURL();

		if(casAddress == null) {
			log.error(MgrUtil.getUserMessage("error.teacherView.redirect.no.cas"));
			return null;
		}

		String[] casSettings = new String[2];

		casSettings[0] = casAddress.substring(8, casAddress.indexOf(":", 8));

		casSettings[1] = casAddress.substring(casAddress.indexOf(":",
				casAddress.indexOf(casSettings[0])) + 1,
			casAddress.indexOf("/cas"));

		return casSettings;
	}

	private List<String> getResourceMapCLIs() {
		StringBuilder cli = new StringBuilder();

		List<String> clis = new ArrayList<String>();
		clis.add("no teacher-view resource-map\n");

		List<TvResourceMap> maps = QueryUtil.executeQuery(TvResourceMap.class,
				null, null, this.getDomainId());

		for(TvResourceMap map : maps) {
			cli.append("teacher-view resource-map name ")
				.append("\"" + map.getResource() + "\"").append(" ip ")
				.append(map.getAlias()).append(" port ")
				.append(map.getPort()).append("\n");
			clis.add(cli.toString());
			cli.delete(0, cli.length());
		}

		return clis;
	}

	private List<String> getCASCLIs() {
		List<String> clis = new ArrayList<String>();

		/*
		 *  CLI for CAS settings
		 */
		String[] casServer = getCASServer();

		if(casServer == null) {
			return clis;
		}

		/*
		 * cas address
		 */
		StringBuilder cli = new StringBuilder();
		cli.append("hiveui cas client server name ").append(casServer[0]).append("\n");
		clis.add(cli.toString());
		cli.delete(0, cli.length());

		/*
		 * port
		 */
		cli.append("hiveui cas client server port ").append(casServer[1]).append("\n");
		clis.add(cli.toString());
		cli.delete(0, cli.length());

		return clis;
	}

	private class ApConnectedStudents implements Comparable<Object> {
		private String apAddress;
		private String macAddress;
		private int studentCount;
		private boolean sptHttps = false;

		public ApConnectedStudents() {

		}

		/**
		 * getter of apAddress
		 * @return the apAddress
		 */
		public String getApAddress() {
			return apAddress;
		}
		/**
		 * setter of apAddress
		 * @param apAddress the apAddress to set
		 */
		public void setApAddress(String apAddress) {
			this.apAddress = apAddress;
		}
		/**
		 * getter of studentCount
		 * @return the studentCount
		 */
		public int getStudentCount() {
			return studentCount;
		}
		/**
		 * setter of studentCount
		 * @param studentCount the studentCount to set
		 */
		public void setStudentCount(int studentCount) {
			this.studentCount = studentCount;
		}
		
		public String getMacAddress() {
			return macAddress;
		}

		public void setMacAddress(String macAddress) {
			this.macAddress = macAddress;
		}

		public boolean isSptHttps() {
			return sptHttps;
		}

		public void setSptHttps(boolean sptHttps) {
			this.sptHttps = sptHttps;
		}

		@Override
		public int compareTo(Object obj) {
			if(obj == null) {
				return -1;
			}

			if(!(obj instanceof ApConnectedStudents)) {
				return -1;
			}

			ApConnectedStudents sc = (ApConnectedStudents)obj;

			if(sc.getStudentCount() > this.getStudentCount()) {
				return 1;
			} else if (sc.getStudentCount() == this.getStudentCount()) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	private Long classId;

	/**
	 * getter of classId
	 * @return the classId
	 */
	public Long getClassId() {
		return classId;
	}

	/**
	 * setter of classId
	 * @param classId the classId to set
	 */
	public void setClassId(Long classId) {
		this.classId = classId;
	}

	private String subject;

	/**
	 * getter of subject
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * setter of subject
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	private String teacher;

	/**
	 * getter of teacher
	 * @return the teacher
	 */
	public String getTeacher() {
		return teacher;
	}

	/**
	 * setter of teacher
	 * @param teacher the teacher to set
	 */
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	private String className;

	/**
	 * getter of className
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * setter of className
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	private int rosterType;

	/**
	 * getter of rosterType
	 * @return the rosterType
	 */
	public int getRosterType() {
		return rosterType;
	}

	/**
	 * setter of rosterType
	 * @param rosterType the rosterType to set
	 */
	public void setRosterType(int rosterType) {
		this.rosterType = rosterType;
	}

	private String apStudents;

	/**
	 * getter of apStudents
	 * @return the apStudents
	 */
	public String getApStudents() {
		return apStudents;
	}

	/**
	 * setter of apStudents
	 * @param apStudents the apStudents to set
	 */
	public void setApStudents(String apStudents) {
		this.apStudents = apStudents;
	}

	private String expiredTime;

	/**
	 * getter of expiredTime
	 * @return the expiredTime
	 */
	public String getExpiredTime() {
		return expiredTime;
	}

	/**
	 * setter of expiredTime
	 * @param expiredTime the expiredTime to set
	 */
	public void setExpiredTime(String expiredTime) {
		this.expiredTime = expiredTime;
	}

	private String redirectAction;

	private String redirectURL;

	/**
	 * getter of redirectURL
	 * @return the redirectURL
	 */
	public String getRedirectURL() {
		return redirectURL;
	}

	/**
	 * setter of redirectURL
	 * @param redirectURL the redirectURL to set
	 */
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	/**
	 * getter of redirectAction
	 * @return the redirectAction
	 */
	public String getRedirectAction() {
		return redirectAction;
	}

	/**
	 * setter of redirectAction
	 * @param redirectAction the redirectAction to set
	 */
	public void setRedirectAction(String redirectAction) {
		this.redirectAction = redirectAction;
	}

	public String getHMTVURL() {
		StringBuilder url = new StringBuilder();
		String casClient = NmsUtil.getCasClient();

		if(null != casClient) {
			url.append(casClient);

			if(casClient.endsWith("/")) {
				url.append("hm/teacherView.action");
			} else {
				url.append("/hm/teacherView.action");
			}
		} else {
		url.append("https://");
		url.append(HmBeOsUtil.getHiveManagerIPAddr());
		url.append("/hm/teacherView.action");
		}


		return url.toString();
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if(bo instanceof TvClass) {
			TvClass tvClass = (TvClass)bo;

			if(tvClass.getItems() != null) {
				tvClass.getItems().size();
			}
		}


		if(bo instanceof HmUser) {
			HmUser user = (HmUser)bo;
			HmUserGroup group = user.getUserGroup();

			if(group.getFeaturePermissions() != null) {
				group.getFeaturePermissions().size();
			}
		}

		return null;
	}

	private String proxyServer;

	public void setProxyServer(String proxyServer) {
		this.proxyServer = proxyServer;
	}

	public String getProxyServer() {
		return proxyServer;
	}

	/**
	 * Convert Proxy server information to JSON
	 * @author huihe
	 * @param proxyIP, proxyPort
	 * @return
	 * @throws Exception
	 */
	private String encodeProxyServer(String proxyIP, int proxyPort, String autoProxyFile) throws Exception {
		JSONObject proxyElement = new JSONObject();
		JSONObject subElement = new JSONObject();
		subElement.put("ip", proxyIP);
		subElement.put("port", proxyPort);
		subElement.put("autoProxyFile", autoProxyFile);
		proxyElement.put("proxyServer", subElement);
		return proxyElement.toString();
	}
	
	//Case Sensitive with LDAP
	private short caseInsensitive;

	public short getCaseInsensitive() {
		return caseInsensitive;
	}

	public void setCaseInsensitive(short caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}
	
	//This admin user name will send to Device, to help them exclude the admin name from the student list.
	private String adminUserName;
	
	public String getAdminUserName() {
		return adminUserName;
	}

	public void setAdminUserName(String adminUserName) {
		this.adminUserName = adminUserName;
	}
	
	private class ClassAP{
		private boolean isConnected = false;
		private boolean isSptHttps = false;
		
		public boolean isConnected() {
			return isConnected;
		}
		public void setConnected(boolean isConnected) {
			this.isConnected = isConnected;
		}
		public boolean isSptHttps() {
			return isSptHttps;
		}
		public void setSptHttps(boolean isSptHttps) {
			this.isSptHttps = isSptHttps;
		}		
	}
}
