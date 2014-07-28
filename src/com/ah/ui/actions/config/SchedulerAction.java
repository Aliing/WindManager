package com.ah.ui.actions.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeOsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.lan.LanProfile;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.wlan.Scheduler;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
/*
 * Modification History
 * 
 * support VHM
 *     set owner to null when cloning
 * joseph chen 05/07/2008
 */
public class SchedulerAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private static final String WEEK_SPLIT_TO = " to ";
	private static final String HOUR = "hr";
	private static final String MINUTE = "min";
	
	private static final Tracer log = new Tracer(CwpAction.class
			.getSimpleName());
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_TYPE = 2;
	
	public static final int COLUMN_START_DATE = 3;
	
	public static final int COLUMN_END_DATE = 4;
	
	public static final int COLUMN_WEEKS = 5;
	
	public static final int COLUMN_START_TIME1 = 6;
	
	public static final int COLUMN_END_TIME1 = 7;

	public static final int COLUMN_START_TIME2 = 8;
	
	public static final int COLUMN_END_TIME2 = 9;
	
	public static final int COLUMN_DESCRIPTION = 10;

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.schedules"))) {
					return getLstForward();
				}
				Scheduler newScheduler = new Scheduler();
				if ("localUserGroup".equalsIgnoreCase(getLstForward())){
					newScheduler.setType(Scheduler.RECURRENT);
					radioRecurrent = "recurrent";
				}
				setSessionDataSource(newScheduler);
				return getReturnPathWithJsonMode(INPUT, "schedulerDrawer", "schedulerDialog");
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				jsonObject = new JSONObject();
				prepareSchedulerValues();
				if (checkNameExists("schedulerName", getDataSource().getSchedulerName())) {
					if (isJsonMode()) {
						if (isContentShownInSubDrawer()) {
							return "schedulerDrawer";
						}
						if (!isParentIframeOpenFlg()) {
							jsonObject = new JSONObject();
							jsonObject.put("resultStatus", false);
							jsonObject.put("errMsg", getActionErrors().toArray()[0].toString());
							return "json";
						}
					}
					return getReturnPathWithJsonMode(INPUT, "schedulerDrawer", "schedulerDialog");
				}
				
				if(isJsonMode()) {
					if (isContentShownInDlg()) {
						if (isParentIframeOpenFlg()) {
							id = createBo(dataSource);
							setUpdateContext(true);
							return getLstForward();
						} else {
							try {
								id = createBo(dataSource);
								jsonObject.put("schedulerId",id);
								setUpdateContext(true);
								jsonObject.put("id", id);
								jsonObject.put("parentDomID",getParentDomID());
								jsonObject.put("name", getDataSource().getSchedulerName());
								jsonObject.put("resultStatus",true);
							} catch (Exception e) {
								jsonObject.put("resultStatus", false);
								jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
								return "json";
							}
							return "json";
						}
					} else {
						createBo(dataSource);
						/*
						 * set scheduler to SSID
						 */
						if(bindTarget == 1) { // for SSID
							SsidProfile ssid = QueryUtil.findBoById(SsidProfile.class, ssidId,new SsidProfilesAction());

							if(ssid != null) {
								Set<Scheduler> schedulers = new HashSet<Scheduler>();
								if (this.selectedSchedulerList != null && !this.selectedSchedulerList.equals("null")) {
									String[] selectedSchedulerLists = selectedSchedulerList.split(",");
									for (String id : selectedSchedulerLists) {
										Scheduler schduler = QueryUtil.findBoById(Scheduler.class,Long.valueOf(id));
										schedulers.add(schduler);
									}
								}
								schedulers.add((Scheduler)dataSource);
								if (schedulers.size() > 8) {
									jsonObject.put("ok", "false");
									jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.scheduler.notcreate"));
								}
								ssid.setSchedulers(schedulers);
								
								try {
									updateBoWithEvent(ssid);
									jsonObject.put("ok", true);
								} catch (Exception e) {
									log.error(MgrUtil.getUserMessage("error.config.networkPolicy.ssid.scheduler.failed"), e);
									jsonObject.put("ok", false);
									jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.ssid.scheduler.failed"));
								}
							} else {
								jsonObject.put("ok", false);
								jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.ssid.scheduler.failed"));
							}
						} else { // for LAN
							LanProfile lan = QueryUtil.findBoById(LanProfile.class, ssidId,new LanProfilesAction());
							
							if(lan != null) {
								
								Set<Scheduler> schedulers = new HashSet<Scheduler>();
								if (this.selectedSchedulerList != null && !this.selectedSchedulerList.equals("null")) {
									String[] selectedSchedulerLists = selectedSchedulerList.split(",");
									for (String id : selectedSchedulerLists) {
										Scheduler schduler = QueryUtil.findBoById(Scheduler.class,Long.valueOf(id));
										schedulers.add(schduler);
									}
								}
								schedulers.add((Scheduler)dataSource);
								if (schedulers.size() > 8) {
									jsonObject.put("ok", "false");
									jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.scheduler.notcreate"));
								}
								lan.setSchedulers(schedulers);
								
								try {
									updateBoWithEvent(lan);
									jsonObject.put("ok", true);
								} catch (Exception e) {
									log.error(MgrUtil.getUserMessage("error.config.networkPolicy.lan.scheduler.failed"), e);
									jsonObject.put("ok", false);
									jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.lan.scheduler.failed"));
								}
							} else {
								jsonObject.put("ok", false);
								jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.lan.scheduler.failed"));
							}
						}					
						return "json";
					}
					
				} else {
					if ("create".equals(operation)) {
						return createBo();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if ("edit".equals(operation)) {
				String strForward = editBo();
				initScheduleValues();
				addLstTitle(getText("config.title.schedules.edit") + " '"
						+ this.getDisplayName() + "'");
				
				return getReturnPathWithJsonMode(strForward, "schedulerDrawer", "schedulerDialog");
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {
				emptyBoValue();
				prepareSchedulerValues();
				if (isJsonMode() && !isParentIframeOpenFlg()) {
					updateBo(dataSource);
					jsonObject = new JSONObject();
					setUpdateContext(true);
					jsonObject.put("parentDomID",getParentDomID());
					jsonObject.put("resultStatus",true);
					jsonObject.put("ok", true);
					return "json";
				}
				
				if ("update".equals(operation)) {
					return updateBo();
				} else {
					setUpdateContext(true);
					updateBo(dataSource);
					return getLstForward();
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				Scheduler clone = (Scheduler) findBoById(boClass, cloneId);
				clone.setId(null);
				clone.setSchedulerName("");
				clone.setOwner(null);    // joseph chen
				clone.setVersion(null);  // joseph chen 06/17/2008
				setSessionDataSource(clone);
				initScheduleValues();
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					if (dataSource != null)
						((Scheduler) dataSource).setRecurrent("Once");
					return prepareBoList();
				}
			}else if ("createSchedulerFromSSID".equals(operation)) {
				Scheduler newScheduler = new Scheduler();
				if ("localUserGroup".equalsIgnoreCase(getLstForward())){
					newScheduler.setType(Scheduler.RECURRENT);
					radioRecurrent = "recurrent";
				}
				setSessionDataSource(newScheduler);
				
				return "schedulerDrawer";
			}else if ("schedulerListDialog".equals(operation)) {
				Scheduler newScheduler = new Scheduler();
				setSessionDataSource(newScheduler);
				return "schedulerListDialog";
			}else if ("setSchedulerToSSID".equals(operation)) {
				setSchedulerToSSID();
				return "json";
			} else {
				baseOperation();
				if (dataSource != null)
					((Scheduler) dataSource).setRecurrent("Once");
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_SCHEDULER);
		setDataSource(Scheduler.class);
		this.chboxDateTime = true;
		this.chboxTime = true;
		Calendar calendar = HmBeOsUtil.getServerTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		beginDateTime = formatter.format(calendar.getTime());
		endDateTime = beginDateTime;
		
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_SCHEDULER;
	}

	/**
	 * get the description of column by id
	 * @param id
	 * @return
	 * @author Joseph Chen
	 */
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "config.wlanAccess.scheduler.schedulerName";
			break;
		case COLUMN_TYPE:
			code = "config.wlanAccess.scheduler.type";
			break;
		case COLUMN_START_DATE:
			code = "config.wlanAccess.scheduler.beginDate";
			break;
		case COLUMN_END_DATE:
			code = "config.wlanAccess.scheduler.endDate";
			break;
		case COLUMN_WEEKS:
			code = "config.wlanAccess.scheduler.weeks";
			break;
		case COLUMN_START_TIME1:
			code = "config.wlanAccess.scheduler.beginTime1";
			break;
		case COLUMN_END_TIME1:
			code = "config.wlanAccess.scheduler.endTime1";
			break;
		case COLUMN_START_TIME2:
			code = "config.wlanAccess.scheduler.beginTime2";
			break;
		case COLUMN_END_TIME2:
			code = "config.wlanAccess.scheduler.endTime2";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.wlanAccess.scheduler.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_TYPE));
		columns.add(new HmTableColumn(COLUMN_START_DATE));
		columns.add(new HmTableColumn(COLUMN_END_DATE));
		columns.add(new HmTableColumn(COLUMN_WEEKS));
		columns.add(new HmTableColumn(COLUMN_START_TIME1));
		columns.add(new HmTableColumn(COLUMN_END_TIME1));
		columns.add(new HmTableColumn(COLUMN_START_TIME2));
		columns.add(new HmTableColumn(COLUMN_END_TIME2));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		
		return columns;
	}

	public boolean getDisableName() {
		return getDataSource() != null && getDataSource().getId() != null;
	}

	public String getDisplayName() {
		return getDataSource().getSchedulerName().replace("\\", "\\\\")
				.replace("'", "\\'");
	}

	public int getNameLength() {
		return getAttributeLength("schedulerName");
	}

	public int getDescriptionLength() {
		return getAttributeLength("description");
	}

	public Scheduler getDataSource() {
		return (Scheduler) dataSource;
	}

	String hide = "none";

	public String getHide() {
		return hide;
	}

	public void setHide(String hide) {
		this.hide = hide;
	}

	private void initScheduleValues() {
		if (getDataSource() == null)
			return;

		String[] timeBegin = initTime(getDataSource().getBeginTime());
		String[] timeEnd = initTime(getDataSource().getEndTime());
		if (getDataSource().getType() == 1) {
			this.radioRecurrent = "once";
			this.chboxDateTime = true;
			this.beginDateTime = getDataSource().getBeginDate();
			this.endDateTime = getDataSource().getEndDate();
			if (timeBegin != null) {
				this.beginDateTimeH = timeBegin[0];
				this.beginDateTimeM = timeBegin[1];
			}
			if (timeEnd != null) {
				this.endDateTimeH = timeEnd[0];
				this.endDateTimeM = timeEnd[1];
			}
		} else {
			radioRecurrent = "recurrent";
			this.chboxTime = true;
			if (timeBegin != null) {
				this.beginTimeH = timeBegin[0];
				this.beginTimeM = timeBegin[1];
			}
			if (timeEnd != null) {
				this.endTimeH = timeEnd[0];
				this.endTimeM = timeEnd[1];
			}
			if (getDataSource().getBeginTimeS() != null
					&& !getDataSource().getBeginTimeS().trim().equals("")) {
				this.chboxTimeS = true;
				timeBegin = initTime(getDataSource().getBeginTimeS());
				timeEnd = initTime(getDataSource().getEndTimeS());
				if (timeBegin != null) {
					this.beginTimeSH = timeBegin[0];
					this.beginTimeSM = timeBegin[1];
				}
				if (timeEnd != null) {
					this.endTimeSH = timeEnd[0];
					this.endTimeSM = timeEnd[1];
				}
			}
			if (getDataSource().getType() == 2) {
				this.chboxDate = true;
				this.beginDate = getDataSource().getBeginDate();
				if (getDataSource().getEndDate() != null
						&& !getDataSource().getEndDate().trim().equals("")) {
					this.endDate = getDataSource().getEndDate();
					this.chboxEndDate = true;
				}
			}
			if (getDataSource().getType() == 3) {
				this.chboxWeek = true;
				this.weekBegin = initWeek(getDataSource().getWeeks())[0];
				this.weekEnd = initWeek(getDataSource().getWeeks())[1];
			}
			if (getDataSource().getType() == 4) {
				this.chboxDate = true;
				this.chboxWeek = true;
				this.weekBegin = initWeek(getDataSource().getWeeks())[0];
				this.weekEnd = initWeek(getDataSource().getWeeks())[1];
				this.beginDate = getDataSource().getBeginDate();
				if (getDataSource().getEndDate() != null
						&& !getDataSource().getEndDate().trim().equals("")) {
					this.endDate = getDataSource().getEndDate();
					this.chboxEndDate = true;
				}
			}
		}
	}

	private String[] initWeek(String week) {
		if (week == null || week.trim().equals(""))
			return null;
		return week.split(WEEK_SPLIT_TO);
	}

	private String[] initTime(String time) {
		if (time == null || time.trim().equals(""))
			return null;
		String[] tmp = time.split(":");
		for (int i = 0; i < tmp.length; i++)
			// if(tmp[i].trim().length()==1)
			tmp[i] = String.valueOf(Integer.parseInt(tmp[i]));
		return tmp;
	}

	public void prepareSchedulerValues() {
		// for debug
		if (getDataSource() != null) {
			if (this.radioRecurrent.equals("once")) {
				getDataSource().setBeginDate(formatDate(beginDateTime));
				getDataSource().setEndDate(formatDate(endDateTime));
				getDataSource().setBeginTime(
						prepareTime(beginDateTimeH, beginDateTimeM));
				getDataSource().setEndTime(
						prepareTime(endDateTimeH, endDateTimeM));

			}
			if (this.radioRecurrent.equals("recurrent")) {
				if (this.isChboxTimeS()) {
					getDataSource().setBeginTimeS(
							prepareTime(beginTimeSH, beginTimeSM));
					getDataSource().setEndTimeS(
							prepareTime(endTimeSH, endTimeSM));
				} else {
					getDataSource().setBeginTimeS(null);
					getDataSource().setEndTimeS(null);
				}
				
				if (this.isChboxWeek()) {
					getDataSource().setWeeks(
							weekBegin + WEEK_SPLIT_TO + weekEnd);
				} else {
					getDataSource().setWeeks(null);
				}
				
				if (this.isChboxDate()) {
					getDataSource().setBeginDate(formatDate(beginDate));
					if (isChboxEndDate())
						getDataSource().setEndDate(formatDate(endDate));
					else
						getDataSource().setEndDate(formatDate(null));
				} else {
					getDataSource().setBeginDate(null);
					getDataSource().setEndDate(null);
				}


				getDataSource().setBeginTime(
						prepareTime(beginTimeH, beginTimeM));
				getDataSource().setEndTime(prepareTime(endTimeH, endTimeM));
			}
			
			getDataSource().setType(preareType());
		}
	}

	private void emptyBoValue() {
		if (getDataSource() == null)
			return;
		getDataSource().setBeginDate(null);
		getDataSource().setEndDate(null);
		getDataSource().setBeginTimeS(null);
		getDataSource().setEndTimeS(null);
		getDataSource().setWeeks(null);
	}

	private String prepareTime(String h, String m) {

		if (h == null || h.trim().equals("") || m == null
				|| m.trim().equals(""))
			return "";
		if (h.length() == 1)
			h = "0" + h;
		if (m.length() == 1)
			m = "0" + m;
		return h + ":" + m;
	}

	private int preareType() {
		int type = -1;
		if (this.getRadioRecurrent().equals("once"))
			type = 1;
		if (this.getRadioRecurrent().equals("recurrent")) {
			if (!chboxDate && !chboxWeek)
				type = 0;
			if (chboxDate && !chboxWeek)
				type = 2;
			if (!chboxDate && chboxWeek)
				type = 3;
			if (chboxDate && chboxWeek)
				type = 4;
		}
		return type;
	}

	private String formatDate(String date) {
		if (date == null || date.trim().equals(""))
			return "";
		String[] tmp = date.split("-");
		String dt = null;
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i] != null) {
				if (i == 0)
					dt = formatStringByZero(tmp[i]) + "-";
				if (i == 1)
					dt = dt + formatStringByZero(tmp[i]) + "-";
				if (i == 2)
					dt = dt + formatStringByZero(tmp[i]);
			}
		}
		return dt;
	}

	private String formatStringByZero(String str) {
		if (str == null)
			return str;
		if (str.length() == 1)
			return 0 + str;
		return str;
	}

	public enum Week {
		Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday;
		public String getKey() {
			return name();
		}

		public String getValue() {
			return MgrUtil.getUserMessage(name());
		}
	}

	public Week[] getWeekValues() {
		return Week.values();
	}

	public static EnumItem[] ENUM_HOURS = enumItems("enum.hours.", 24, HOUR);

	public static EnumItem[] ENUM_MINUTES = enumItems("enum.minutes.", 60,
			MINUTE);

	private static EnumItem[] enumItems(String prefix, int len, String type) {
		EnumItem[] enumItems = new EnumItem[len];
		for (int i = 0; i < len; i++) {
			String tmp = String.valueOf(i);
			if (tmp.length() == 1)
				tmp = "0" + tmp;
			tmp = tmp + " " + type;
			enumItems[i] = new EnumItem(i, tmp);
		}
		return enumItems;
	}

	public EnumItem[] getEnumHours() {
		return ENUM_HOURS;
	}

	public EnumItem[] getEnumMinutes() {
		return ENUM_MINUTES;
	}

	protected String beginDate = "";

	protected String endDate = "";

	protected String beginTimeH = "00";

	protected String endTimeH = "23";

	protected String beginTimeM = "00";

	protected String endTimeM = "59";

	protected String beginTimeSH = "";

	protected String endTimeSH = "";

	protected String beginTimeSM = "";

	protected String endTimeSM = "";

	protected String weekBegin = "";

	protected String weekEnd = "";

	protected String beginDateTime = "";

	protected String endDateTime = "";

	protected String beginDateTimeH = "00";

	protected String beginDateTimeM = "00";

	protected String endDateTimeH = "23";

	protected String endDateTimeM = "59";

	protected boolean chboxDate = false;

	protected boolean chboxWeek = false;

	protected boolean chboxTime = false;

	protected boolean chboxTimeS = false;

	protected boolean chboxEndDate = false;

	protected boolean chboxDateTime = false;

	public boolean isChboxDateTime() {
		return chboxDateTime;
	}

	public void setChboxDateTime(boolean chboxDateTime) {
		this.chboxDateTime = chboxDateTime;
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getBeginTimeH() {
		return beginTimeH;
	}

	public void setBeginTimeH(String beginTimeH) {
		this.beginTimeH = beginTimeH;
	}

	public String getBeginTimeM() {
		return beginTimeM;
	}

	public void setBeginTimeM(String beginTimeM) {
		this.beginTimeM = beginTimeM;
	}

	public String getBeginTimeSH() {
		return beginTimeSH;
	}

	public void setBeginTimeSH(String beginTimeSH) {
		this.beginTimeSH = beginTimeSH;
	}

	public String getBeginTimeSM() {
		return beginTimeSM;
	}

	public void setBeginTimeSM(String beginTimeSM) {
		this.beginTimeSM = beginTimeSM;
	}

	public String getEndTimeSM() {
		return endTimeSM;
	}

	public void setEndTimeSM(String endTimeSM) {
		this.endTimeSM = endTimeSM;
	}

	public boolean isChboxDate() {
		return chboxDate;
	}

	public boolean getChboxDate() {
		return chboxDate;
	}

	public void setChboxDate(boolean chboxDate) {
		this.chboxDate = chboxDate;
	}

	public boolean isChboxEndDate() {
		return chboxEndDate;
	}

	public boolean getChboxEndDate() {
		return chboxEndDate;
	}

	public void setChboxEndDate(boolean chboxEndDate) {
		this.chboxEndDate = chboxEndDate;
	}

	public boolean isChboxTime() {
		return chboxTime;
	}

	public boolean getChboxTime() {
		return chboxTime;
	}

	public void setChboxTime(boolean chboxTime) {
		this.chboxTime = chboxTime;
	}

	public boolean isChboxTimeS() {
		return chboxTimeS;
	}

	public boolean getChboxTimeS() {
		return chboxTimeS;
	}

	public void setChboxTimeS(boolean chboxTimeS) {
		this.chboxTimeS = chboxTimeS;
	}

	public boolean isChboxWeek() {
		return chboxWeek;
	}

	public boolean getChboxWeek() {
		return chboxWeek;
	}

	public void setChboxWeek(boolean chboxWeek) {
		this.chboxWeek = chboxWeek;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTimeH() {
		return endTimeH;
	}

	public void setEndTimeH(String endTimeH) {
		this.endTimeH = endTimeH;
	}

	public String getEndTimeM() {
		return endTimeM;
	}

	public void setEndTimeM(String endTimeM) {
		this.endTimeM = endTimeM;
	}

	public String getEndTimeSH() {
		return endTimeSH;
	}

	public void setEndTimeSH(String endTimeSH) {
		this.endTimeSH = endTimeSH;
	}

	public String getWeekBegin() {
		return weekBegin;
	}

	public void setWeekBegin(String weekBegin) {
		this.weekBegin = weekBegin;
	}

	public String getWeekEnd() {
		return weekEnd;
	}

	public void setWeekEnd(String weekEnd) {
		this.weekEnd = weekEnd;
	}

	private String radioRecurrent = "once";

	private boolean hideBeginTimeSH = true;

	private boolean hideBeginTimeSM = true;

	private boolean hideEndTimeSH = true;

	private boolean hideEndTimeSM = true;

	private boolean hideWeekBegin = true;

	private boolean hideWeekEnd = true;

	private String hideBeginDate = "none";

	private String hideEndDate = "none";

	private boolean hideChboxEndDate = true;

	public String getHideBeginDate() {
		if (getDataSource() != null && getDataSource().getBeginDate() != null)
			return "";
		return hideBeginDate;
	}

	public boolean getHideChboxEndDate() {
		if (getDataSource() != null && getDataSource().getBeginDate() != null)
			return !hideChboxEndDate;
		return hideChboxEndDate;
	}

	public String getHideEndDate() {
		if (getDataSource() != null && getDataSource().getEndDate() != null)
			return "";
		return hideEndDate;
	}

	public boolean getHideWeekBegin() {
		if (getDataSource() != null && getDataSource().getWeeks() != null)
			return !hideWeekBegin;
		return hideWeekBegin;
	}

	public boolean getHideWeekEnd() {
		if (getDataSource() != null && getDataSource().getWeeks() != null)
			return !hideWeekEnd;
		return hideWeekEnd;
	}

	public boolean getHideBeginTimeSH() {
		if (getDataSource() != null && getDataSource().getBeginTimeS() != null)
			return !hideBeginTimeSH;
		return hideBeginTimeSH;
	}

	public boolean getHideBeginTimeSM() {
		if (getDataSource() != null && getDataSource().getBeginTimeS() != null)
			return !hideBeginTimeSM;
		return hideBeginTimeSM;
	}

	public boolean getHideEndTimeSH() {
		if (getDataSource() != null && getDataSource().getEndTimeS() != null)
			return !hideEndTimeSH;
		return hideEndTimeSH;
	}

	public boolean getHideEndTimeSM() {
		if (getDataSource() != null && getDataSource().getEndTimeS() != null)
			return !hideEndTimeSM;
		return hideEndTimeSM;
	}

	public void setRadioRecurrent(String radioRecurrent) {
		this.radioRecurrent = radioRecurrent;
	}

	public String getRadioRecurrent() {
		return radioRecurrent;
	}

	public String getBeginDateTime() {
		return beginDateTime;
	}

	public void setBeginDateTime(String beginDateTime) {
		this.beginDateTime = beginDateTime;
	}

	public String getBeginDateTimeH() {
		return beginDateTimeH;
	}

	public void setBeginDateTimeH(String beginDateTimeH) {
		this.beginDateTimeH = beginDateTimeH;
	}

	public String getBeginDateTimeM() {
		return beginDateTimeM;
	}

	public void setBeginDateTimeM(String beginDateTimeM) {
		this.beginDateTimeM = beginDateTimeM;
	}

	public String getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}

	public String getEndDateTimeH() {
		return endDateTimeH;
	}

	public void setEndDateTimeH(String endDateTimeH) {
		this.endDateTimeH = endDateTimeH;
	}

	public String getEndDateTimeM() {
		return endDateTimeM;
	}

	public void setEndDateTimeM(String endDateTimeM) {
		this.endDateTimeM = endDateTimeM;
	}

	public String getOnce_div() {
		return getDataSource().getType() == 1 ? "" : "none";
	}

	public String getRecurrent_div() {
		return getDataSource().getType() == 1 ? "none" : "";
	}
	
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		Scheduler source = QueryUtil.findBoById(Scheduler.class,
				paintbrushSource);
		if (null == source) {
			return null;
		}
		List<Scheduler> list = QueryUtil.executeQuery(Scheduler.class,
				null, new FilterParams("id", destinationIds), domainId);
		
		if (list.isEmpty()) {
			return null;
		}
		
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		
		for (Scheduler profile : list) {
			if (profile.isDefaultFlag()) {
				continue;
			}
			
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			Scheduler scheduler = source.clone();
			
			if (null == scheduler) {
				continue;
			}
			
			scheduler.setId(profile.getId());
			scheduler.setVersion(profile.getVersion());
			scheduler.setSchedulerName(profile.getSchedulerName());
			scheduler.setOwner(profile.getOwner());
			scheduler.setDefaultFlag(false);
			hmBos.add(scheduler);
		}
	
		return hmBos;
	}	
	
	private Long selectedScheduler;
	
	public Long getSelectedScheduler() {
		return selectedScheduler;
	}

	public void setSelectedScheduler(Long selectedScheduler) {
		this.selectedScheduler = selectedScheduler;
	}
	
	private List<Long> selectedSchedulers;
	
	public List<Long> getSelectedSchedulers() {
		selectedSchedulers =  new ArrayList<Long>();
		
		/*
		 * get set schedulers profile in SSID profile
		 */
		if(this.ssidId != null) {
			if(this.bindTarget == 1) { // for SSID
				SsidProfile ssid = QueryUtil.findBoById(SsidProfile.class, ssidId,new SsidProfilesAction());
				
				if (null != ssid && null != ssid.getSchedulers()) {
						Iterator<?> it =  ssid.getSchedulers().iterator();
						while(it.hasNext()) {
							selectedSchedulers.add(((Scheduler) it.next()).getId());
					}
				}
			} else { // for LAN
				LanProfile lan = QueryUtil.findBoById(LanProfile.class, ssidId,new LanProfilesAction());
				if (null != lan && null != lan.getSchedulers()) {
					Iterator<?> it =  lan.getSchedulers().iterator();
					while(it.hasNext()) {
						selectedSchedulers.add(((Scheduler) it.next()).getId());
					}
				}
			}
			
		}
		return selectedSchedulers;
	}

	public void setSelectedSchedulers(List<Long> selectedSchedulers) {
		this.selectedSchedulers = selectedSchedulers;
	}

	private void setSchedulerToSSID() throws JSONException {
		jsonObject = new JSONObject();
		
		Set<Scheduler> schSet = new HashSet<Scheduler>();
		if (this.selectedSchedulers != null) {
			for (Long id : this.selectedSchedulers) {
				Scheduler schduler = QueryUtil.findBoById(Scheduler.class,id);
				schSet.add(schduler);
			}
		}
		
		if(this.bindTarget == 1) { // for SSID
			if(ssidId != null) {
				SsidProfile ssid = QueryUtil.findBoById(SsidProfile.class, ssidId);
				
				if(ssid != null) {
					
					ssid.setSchedulers(schSet);
					
					try {
						QueryUtil.updateBo(ssid);
						jsonObject.put("ok", true);
					} catch (Exception e) {
						log.error(MgrUtil.getUserMessage("error.config.networkPolicy.ssid.scheduler.failed"), e);
						jsonObject.put("ok", false);
						jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.ssid.scheduler.failed"));
					}
					
					
				} else {
					jsonObject.put("ok", false);
					jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.ssid.scheduler.failed"));
					return ;
				}
			} else {
				jsonObject.put("ok", false);
				jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.ssid.scheduler.failed"));
				return ;
			}
		} else { // for LAN
			if(ssidId != null) {
				LanProfile lan = QueryUtil.findBoById(LanProfile.class, ssidId);
				
				if(lan != null) {
					
					lan.setSchedulers(schSet);
					
					try {
						QueryUtil.updateBo(lan);
						jsonObject.put("ok", true);
					} catch (Exception e) {
						log.error(MgrUtil.getUserMessage("error.config.networkPolicy.lan.scheduler.failed"), e);
						jsonObject.put("ok", false);
						jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.lan.scheduler.failed"));
					}
					
					
				} else {
					jsonObject.put("ok", false);
					jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.lan.scheduler.failed"));
					return ;
				}
			} else {
				jsonObject.put("ok", false);
				jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.lan.scheduler.failed"));
				return ;
			}
		}
		
	}
	
	private Long ssidId;
	
	public Long getSsidId() {
		return ssidId;
	}

	public void setSsidId(Long ssidId) {
		this.ssidId = ssidId;
	}
	
	public List<CheckItem> getAvailableSchedulers() {
		FilterParams params = null;
		
		return this.getBoCheckItems("schedulerName", Scheduler.class, params);
	}
	
	private int bindTarget;

	public int getBindTarget() {
		return bindTarget;
	}

	public void setBindTarget(int bindTarget) {
		this.bindTarget = bindTarget;
	}
	
	private String selectedSchedulerList;
	
	
	public String getSelectedSchedulerList() {
		return selectedSchedulerList;
	}

	public void setSelectedSchedulerList(String selectedSchedulerList) {
		this.selectedSchedulerList = selectedSchedulerList;
	}

	public boolean getSavePermit() {
		SsidProfile ssid = QueryUtil.findBoById(SsidProfile.class, ssidId);
		
		if ("".equals(getWriteDisabled())){
			if (!ssid.getDefaultFlag()){
				return true;
			}
		}
		return false;
	}	
}