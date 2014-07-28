package com.ah.bo.dashboard;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Index;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.performance.AhNewReport;
import com.ah.ui.actions.monitor.DashboardAction;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "HM_DASHBOARD")
@org.hibernate.annotations.Table(appliesTo = "HM_DASHBOARD", indexes = {
		@Index(name = "DASHBOARD_OWNER", columnNames = { "OWNER" })
		})
public class AhDashboard implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	private boolean defaultFlag;

	private String userName="";

	public static final int TAB_TIME_LASTHOUR = 1;
	public static final int TAB_TIME_LASTDAY = 2;
	public static final int TAB_TIME_LASTWEEK = 3;
	public static final int TAB_TIME_CUSTOM = 4;
	public static final int TAB_TIME_LASTCALEDAY = 5;
	public static final int TAB_TIME_LASTCALEWEEK = 6;
	public static final int TAB_TIME_LAST8HOUR = 7;

	private boolean active;
	
	int position=0;

	private String dashName="";

	public static final int DASHBOARD_TYPE_DASH=1;
	public static final int DASHBOARD_TYPE_REPORT=2;
	public static final int DASHBOARD_TYPE_APP=3;
	public static final int DASHBOARD_TYPE_USER=4;
	public static final int DASHBOARD_TYPE_DRILLDAWN=5;
	public static final int DASHBOARD_TYPE_CLIENT=6;
	public static final int DASHBOARD_TYPE_DEVICE=7;
	public static final int DASHBOARD_TYPE_PORT=8;
	public static final int DASHBOARD_TYPE_APPCLIENT=9;
	private int daType=DASHBOARD_TYPE_DASH;

	public static final int REPORT_STATUS_UNSCHEDULED=0;
	public static final int REPORT_STATUS_SCHEDULED=1;
	public static final int REPORT_STATUS_DISABLED=2;
	private int  reportScheduleStatus=0;

//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "LOCATION_ID")
//	private MapContainerNode location;
	// tree node type
	private String objectType=AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL;
	//tree node id
	private String objectId = String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);

	// tree filter node type
	private String filterObjectType=AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL;
	//tree filter node id
	private String filterObjectId = String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);

	private int selectTimeType=TAB_TIME_LASTDAY;
	private long customStartTime;
	private long customEndTime;

	private boolean enableTimeLocal;

	private boolean bgRollup;

	// for report schedule begin
	private int refrequency=AhNewReport.NEW_REPORT_FREQUENCY_DAILY;
	private boolean reCustomDay=false;
	private boolean reCustomTime=false;
	private String reCustomDayValue="0111110";
	private int reCustomTimeStart;
	private int reCustomTimeEnd;
	private String reEmailAddress="";
	private int reWeekStart=0;

	private int reCmTimeType=1;//1 means day,2: month
	private int reCmTimePeriod=1;
	private int reCmTimeStartDayType=1; //1 means day,2: week
	private int reCmTimeStartDayValue=1;
	private int reCmTimeStartMontyYear=14; //1:january, 2:febuary, ... 13:everymonth, 14, everyyear.
	private int reCmTimeStartSepcYear=Calendar.getInstance().get(Calendar.YEAR);

	@Column(length = 128)
	private String pdfHeader="";
	@Column(length = 128)
	private String pdfFooter="";
	@Column(length = 1024)
	private String pdfSummary="";

	@Column(length = 128)
	private String description="";
	
	public static final short DA_CUSTOM_TYPE_COMMON = 0;
	public static final short DA_CUSTOM_TYPE_CUSTOM_TEMPLATE = 1;
	public static final short DA_CUSTOM_TYPE_SCHEDULED_REPORT = 2;

	public static final short DA_TEMPLATE_TYPE_COMMON = 0;
	public static final short DA_TEMPLATE_TYPE_USAGE = 1;
	public static final short DA_TEMPLATE_TYPE_HEALTH = 2;
	public static final short DA_TEMPLATE_TYPE_SECURITY = 3;

	@Version
	private Timestamp version;

	@Transient
	private boolean  cloneDash=false;
	// for report schedule end

	//private static  final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	public String getApTimeZoneID(Calendar ca){
		if(isEnableTimeLocal()){
			return ca.getTimeZone().getID();
		}
		return null;
	}

	public String getDisplayReportCurrentTimeString(){
		Calendar ca = Calendar.getInstance();
		ca.setTimeZone(getOwner().getTimeZone());
		long[] aa = getScheduleStartTimePast(ca);
		SimpleDateFormat l_sdf = new SimpleDateFormat("MMM d, yyyy, h:mma");
		l_sdf.setTimeZone(getTz());
		ca.setTimeInMillis(aa[0]);
		String ret =l_sdf.format(ca.getTime());
		ca.setTimeInMillis(aa[1]);
		ret =ret + " to " + l_sdf.format(ca.getTime());
		return ret;
	}

	public boolean isRunSchedule(long scheduleTime){
		Calendar ca = Calendar.getInstance(owner.getTimeZone());
		ca.setTimeInMillis(scheduleTime);
		//ca.setTimeZone(owner.getTimeZone());
		long endTime=0;
		if (daType!=DASHBOARD_TYPE_REPORT || reportScheduleStatus!=REPORT_STATUS_SCHEDULED || owner.getRunStatus()!=HmDomain.DOMAIN_DEFAULT_STATUS){
			return false;
		}
		if(refrequency==AhNewReport.NEW_REPORT_FREQUENCY_DAILY){
			if (isReCustomTime()) {
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				ca.set(Calendar.HOUR_OF_DAY,0);
				ca.add(Calendar.DATE, -1);
				ca.set(Calendar.HOUR_OF_DAY,reCustomTimeStart);
			} else {
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				ca.set(Calendar.HOUR_OF_DAY,0);
				ca.add(Calendar.DATE, -1);
			}
			if(isReCustomDay()) {
				while (!reCustomDayValue.substring(ca.get(Calendar.DAY_OF_WEEK)-1, ca.get(Calendar.DAY_OF_WEEK)).equals("1")){
					ca.add(Calendar.DATE, -1);
				}
			}
			ca.add(Calendar.DATE, 1);
			endTime=ca.getTimeInMillis();

		} else if (refrequency==AhNewReport.NEW_REPORT_FREQUENCY_WEEKLY) {
			ca.clear(Calendar.MILLISECOND);
			ca.clear(Calendar.SECOND);
			ca.clear(Calendar.MINUTE);
			ca.set(Calendar.HOUR_OF_DAY,0);
			while (ca.get(Calendar.DAY_OF_WEEK)-1 != reWeekStart) {
				ca.add(Calendar.DATE, -1);
			}
			endTime=ca.getTimeInMillis();
		} else if (refrequency==AhNewReport.NEW_REPORT_FREQUENCY_MONTHLY) {
			ca.clear(Calendar.MILLISECOND);
			ca.clear(Calendar.SECOND);
			ca.clear(Calendar.MINUTE);
			ca.set(Calendar.HOUR_OF_DAY,0);
			while (ca.get(Calendar.DAY_OF_MONTH) != 1) {
				ca.add(Calendar.DATE, -1);
			}
			endTime=ca.getTimeInMillis();
		} else {
			ca.clear(Calendar.MILLISECOND);
			ca.clear(Calendar.SECOND);
			ca.clear(Calendar.MINUTE);
			ca.set(Calendar.HOUR_OF_DAY,0);
			if(reCmTimeStartMontyYear==13) {
				ca.set(Calendar.DAY_OF_MONTH, 1);
			} else if (reCmTimeStartMontyYear==14) {
				ca.set(Calendar.DAY_OF_YEAR, 1);
			} else if (reCmTimeStartMontyYear==15) {
				ca.set(Calendar.DAY_OF_YEAR, 1);
				ca.set(Calendar.YEAR, reCmTimeStartSepcYear);
			} else {
				ca.set(Calendar.MONTH, reCmTimeStartMontyYear-1);
				ca.set(Calendar.DAY_OF_MONTH, 1);
				ca.set(Calendar.YEAR, reCmTimeStartSepcYear);
			}

			if(reCmTimeStartMontyYear==13) {
				ca = reSetDayandWeek(ca);
				if (ca.getTimeInMillis()>System.currentTimeMillis()){
					ca.add(Calendar.MONTH, -1);
					ca.set(Calendar.DAY_OF_MONTH, 1);
					ca = reSetDayandWeek(ca);
				}
			} else if (reCmTimeStartMontyYear==14) {
				ca = reSetDayandWeek(ca);
				if (ca.getTimeInMillis()>System.currentTimeMillis()){
					ca.add(Calendar.YEAR, -1);
					ca.set(Calendar.DAY_OF_YEAR, 1);
					ca = reSetDayandWeek(ca);
				}
			} else {
				ca = reSetDayandWeek(ca);
				while (ca.getTimeInMillis()<System.currentTimeMillis()) {
					if(reCmTimeType==1) {
						ca.add(Calendar.DATE, reCmTimePeriod);
					} else {
						ca.add(Calendar.MONTH, reCmTimePeriod);
					}
				}
				while (ca.getTimeInMillis()>System.currentTimeMillis()) {
					if(reCmTimeType==1) {
						ca.add(Calendar.DATE, reCmTimePeriod*-1);
					} else {
						ca.add(Calendar.MONTH, reCmTimePeriod*-1);
					}
				}
			}
			endTime=ca.getTimeInMillis();
		}
		if (scheduleTime==(endTime + 3600000*5)){
			return true;
		}
		return false;
	}

	public long[] getScheduleStartTimePast(Calendar ca){
		long[] ret = {0,0};
		if(refrequency==AhNewReport.NEW_REPORT_FREQUENCY_DAILY){
			if (isReCustomTime()) {
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				ca.set(Calendar.HOUR_OF_DAY,0);
				ca.add(Calendar.DATE, -1);
				ca.set(Calendar.HOUR_OF_DAY,reCustomTimeStart);
			} else {
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				ca.set(Calendar.HOUR_OF_DAY,0);
				ca.add(Calendar.DATE, -1);
			}
			if(isReCustomDay()) {
				while (!reCustomDayValue.substring(ca.get(Calendar.DAY_OF_WEEK)-1, ca.get(Calendar.DAY_OF_WEEK)).equals("1")){
					ca.add(Calendar.DATE, -1);
				}
			}
//			if(isEnableTimeLocal()) {
//				FORMAT.setTimeZone(ca.getTimeZone());
//				ret[0]=Long.valueOf(FORMAT.format(ca.getTime()));
//				if (isReCustomTime()) {
//					ca.set(Calendar.HOUR_OF_DAY,reCustomTimeEnd);
//					ret[1]=Long.valueOf(FORMAT.format(ca.getTime()));
//				} else {
//					ca.add(Calendar.DATE, 1);
//					ret[1]=Long.valueOf(FORMAT.format(ca.getTime()));
//				}
//			} else {
				ret[0]=ca.getTimeInMillis();
				if (isReCustomTime()) {
					ca.set(Calendar.HOUR_OF_DAY,reCustomTimeEnd);
				} else {
					ca.add(Calendar.DATE, 1);
				}
				ret[1]=ca.getTimeInMillis();
//			}
		} else if (refrequency==AhNewReport.NEW_REPORT_FREQUENCY_WEEKLY) {
			ca.clear(Calendar.MILLISECOND);
			ca.clear(Calendar.SECOND);
			ca.clear(Calendar.MINUTE);
			ca.set(Calendar.HOUR_OF_DAY,0);
			while (ca.get(Calendar.DAY_OF_WEEK)-1 != reWeekStart) {
				ca.add(Calendar.DATE, -1);
			}
			ca.add(Calendar.DATE, -7);
//			if(isEnableTimeLocal()) {
//				FORMAT.setTimeZone(ca.getTimeZone());
//				ret[0]=Long.valueOf(FORMAT.format(ca.getTime()));
//				ca.add(Calendar.DATE, 7);
//				ret[1]=Long.valueOf(FORMAT.format(ca.getTime()));
//			} else {
				ret[0]=ca.getTimeInMillis();
				ca.add(Calendar.DATE, 7);
				ret[1]=ca.getTimeInMillis();
//			}
		} else if (refrequency==AhNewReport.NEW_REPORT_FREQUENCY_MONTHLY) {
			ca.clear(Calendar.MILLISECOND);
			ca.clear(Calendar.SECOND);
			ca.clear(Calendar.MINUTE);
			ca.set(Calendar.HOUR_OF_DAY,0);
			while (ca.get(Calendar.DAY_OF_MONTH) != 1) {
				ca.add(Calendar.DATE, -1);
			}
			ca.add(Calendar.MONTH, -1);
//			if(isEnableTimeLocal()) {
//				FORMAT.setTimeZone(ca.getTimeZone());
//				ret[0]=Long.valueOf(FORMAT.format(ca.getTime()));
//				ca.add(Calendar.MONTH, 1);
//				ret[1]=Long.valueOf(FORMAT.format(ca.getTime()));
//			} else {
				ret[0]=ca.getTimeInMillis();
				ca.add(Calendar.MONTH, 1);
				ret[1]=ca.getTimeInMillis();
//			}
		} else {
			ca.clear(Calendar.MILLISECOND);
			ca.clear(Calendar.SECOND);
			ca.clear(Calendar.MINUTE);
			ca.set(Calendar.HOUR_OF_DAY,0);
			if(reCmTimeStartMontyYear==13) {
				ca.set(Calendar.DAY_OF_MONTH, 1);
			} else if (reCmTimeStartMontyYear==14) {
				ca.set(Calendar.DAY_OF_YEAR, 1);
			} else if (reCmTimeStartMontyYear==15) {
				ca.set(Calendar.DAY_OF_YEAR, 1);
				ca.set(Calendar.YEAR, reCmTimeStartSepcYear);
			} else {
				ca.set(Calendar.MONTH, reCmTimeStartMontyYear-1);
				ca.set(Calendar.DAY_OF_MONTH, 1);
				ca.set(Calendar.YEAR, reCmTimeStartSepcYear);
			}

			if(reCmTimeStartMontyYear==13) {
				ca = reSetDayandWeek(ca);
				if (ca.getTimeInMillis()>System.currentTimeMillis()){
					ca.add(Calendar.MONTH, -1);
					ca.set(Calendar.DAY_OF_MONTH, 1);
					ca = reSetDayandWeek(ca);
				}
			} else if (reCmTimeStartMontyYear==14) {
				ca = reSetDayandWeek(ca);
				if (ca.getTimeInMillis()>System.currentTimeMillis()){
					ca.add(Calendar.YEAR, -1);
					ca.set(Calendar.DAY_OF_YEAR, 1);
					ca = reSetDayandWeek(ca);
				}
			} else {
				ca = reSetDayandWeek(ca);
				while (ca.getTimeInMillis()<System.currentTimeMillis()) {
					if(reCmTimeType==1) {
						ca.add(Calendar.DATE, reCmTimePeriod);
					} else {
						ca.add(Calendar.MONTH, reCmTimePeriod);
					}
				}
				while (ca.getTimeInMillis()>System.currentTimeMillis()) {
					if(reCmTimeType==1) {
						ca.add(Calendar.DATE, reCmTimePeriod*-1);
					} else {
						ca.add(Calendar.MONTH, reCmTimePeriod*-1);
					}
				}
			}

//			if(isEnableTimeLocal()) {
//				FORMAT.setTimeZone(ca.getTimeZone());
//				ret[1]=Long.valueOf(FORMAT.format(ca.getTime()));
//				if(reCmTimeType==1) {
//					ca.add(Calendar.DATE, reCmTimePeriod * -1);
//				} else {
//					ca.add(Calendar.MONTH, reCmTimePeriod * -1);
//				}
//				ret[0]=Long.valueOf(FORMAT.format(ca.getTime()));
//			} else {
				ret[1]=ca.getTimeInMillis();
				if(reCmTimeType==1) {
					ca.add(Calendar.DATE, reCmTimePeriod * -1);
				} else {
					ca.add(Calendar.MONTH, reCmTimePeriod * -1);
				}
				ret[0]=ca.getTimeInMillis();
//			}
		}
		return ret;
	}

	public Calendar reSetDayandWeek(Calendar ca){
		if(reCmTimeStartDayType==1) {
			ca.add(Calendar.DATE, reCmTimeStartDayValue);
		} else {
			if(ca.get(Calendar.DAY_OF_WEEK)>reCmTimeStartDayValue) {
				ca.add(Calendar.DATE, 7-(ca.get(Calendar.DAY_OF_WEEK) - reCmTimeStartDayValue));
			} else {
				ca.add(Calendar.DATE, reCmTimeStartDayValue - ca.get(Calendar.DAY_OF_WEEK));
			}
		}
		return ca;
	}


//	public long[] getScheduleTimeNext(){
//
//	}

	@OneToMany(mappedBy = "dashboard")
	private Set<AhDashboardLayout> daLayouts = new HashSet<AhDashboardLayout>();

	public boolean getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return dashName;
	}

	@Transient
	private boolean selected;

	@Transient
	private TimeZone tz;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}


	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public TimeZone getTz() {
		if (tz==null) return getOwner().getTimeZone();
		return tz;
	}

	public void setTz(TimeZone tz) {
		this.tz = tz;
	}

//	public String getCurrentDashMapTreeId(){
//		if (getLocation()!=null) {
//			return getLocation().getId().toString();
//		}
//		return String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_MAP);
//	}

	public String getCurrentDashMapFilterId(){
		if (getObjectId()!=null) {
			return getObjectId();
		}
		return String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
	}

	public String getCurrentDashMapFilterSecondId(){
		if (getFilterObjectId()!=null) {
			return getFilterObjectId();
		}
		return String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL);
	}

	public String getDashPDFReportTimeString(){
		 if (daType==DASHBOARD_TYPE_REPORT) {
			return getDisplayReportCurrentTimeString();
		 } else {
			if (getSelectTimeType()==TAB_TIME_CUSTOM || 
					getSelectTimeType() == TAB_TIME_LAST8HOUR ||
					(position==DashboardAction.DA_TAB_POSITION_APPLICATION &&
					 getSelectTimeType() == TAB_TIME_LASTDAY) || 
					 (position==DashboardAction.DA_TAB_POSITION_APPLICATION &&
					 getSelectTimeType() == TAB_TIME_LASTWEEK)) {
				return getCurrentDashCustomTimeString();
			} else if (getSelectTimeType()==TAB_TIME_LASTHOUR) {
				StringBuilder bd = new StringBuilder();
				SimpleDateFormat l_sdf = new SimpleDateFormat("MMM d, yyyy, h:mma");
				l_sdf.setTimeZone(getTz());
				Calendar ca = Calendar.getInstance(getTz());
				ca.setTimeInMillis(ca.getTimeInMillis()-3600000L);
				bd.append(l_sdf.format(ca.getTime()));
				ca.setTimeInMillis(ca.getTimeInMillis()+3600000L);
				bd.append(" to ");
				bd.append(l_sdf.format(ca.getTime()));
				return bd.toString();
			} else if (getSelectTimeType()==TAB_TIME_LASTDAY) {
				StringBuilder bd = new StringBuilder();
				SimpleDateFormat l_sdf = new SimpleDateFormat("MMM d, yyyy, h:mma");
				l_sdf.setTimeZone(getTz());
				Calendar ca = Calendar.getInstance(getTz());
				long currentTime = ca.getTimeInMillis();
				ca.setTimeInMillis(currentTime-3600000L*24);
				ca.setTimeInMillis(ca.getTimeInMillis()/3600000L*3600000L);
				bd.append(l_sdf.format(ca.getTime()));
				ca.setTimeInMillis(currentTime/3600000L*3600000L);
				bd.append(" to ");
				bd.append(l_sdf.format(ca.getTime()));
				return bd.toString();
			} else if (getSelectTimeType()==TAB_TIME_LASTWEEK) {
				StringBuilder bd = new StringBuilder();
				SimpleDateFormat l_sdf = new SimpleDateFormat("MMM d, yyyy, h:mma");
				l_sdf.setTimeZone(getTz());
				Calendar ca = Calendar.getInstance(getTz());
				long currentTime = ca.getTimeInMillis();
				ca.setTimeInMillis(currentTime-3600000*24 * 7);
				ca.clear(Calendar.MINUTE);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MILLISECOND);
				ca.set(Calendar.HOUR_OF_DAY,0);
				bd.append(l_sdf.format(ca.getTime()));
				
				ca.setTimeInMillis(currentTime);
				ca.clear(Calendar.MINUTE);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MILLISECOND);
				ca.set(Calendar.HOUR_OF_DAY,0);
				bd.append(" to ");
				bd.append(l_sdf.format(ca.getTime()));
				return bd.toString();
			} else if (getSelectTimeType()==TAB_TIME_LASTCALEDAY) {
				StringBuilder bd = new StringBuilder();
				SimpleDateFormat l_sdf = new SimpleDateFormat("MMM d, yyyy, h:mma");
				l_sdf.setTimeZone(getTz());
				Calendar ca = Calendar.getInstance(getTz());
				long currentTime = ca.getTimeInMillis();
				ca.setTimeInMillis(currentTime-3600000*24);
				ca.clear(Calendar.MINUTE);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MILLISECOND);
				ca.set(Calendar.HOUR_OF_DAY,0);
				bd.append(l_sdf.format(ca.getTime()));
				
				ca.add(Calendar.DAY_OF_MONTH,1);
				bd.append(" to ");
				bd.append(l_sdf.format(ca.getTime()));
				return bd.toString();
			} else if (getSelectTimeType()==TAB_TIME_LASTCALEWEEK) {
				StringBuilder bd = new StringBuilder();
				SimpleDateFormat l_sdf = new SimpleDateFormat("MMM d, yyyy, h:mma");
				l_sdf.setTimeZone(getTz());
				Calendar ca = Calendar.getInstance(getTz());
				long currentTime = ca.getTimeInMillis();
				ca.setTimeInMillis(currentTime-3600000*24 * 7);
				ca.clear(Calendar.MINUTE);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MILLISECOND);
				ca.set(Calendar.HOUR_OF_DAY,0);
				ca.add(Calendar.DAY_OF_MONTH,(-1* (ca.get(Calendar.DAY_OF_WEEK)-1)));
				bd.append(l_sdf.format(ca.getTime()));
				ca.add(Calendar.DAY_OF_MONTH,7);
				bd.append(" to ");
				bd.append(l_sdf.format(ca.getTime()));
				return bd.toString();
				
			} else {
				return "";
			}
		}
	}

	public String getCurrentDashCustomTimeString(){
		if (daType != DASHBOARD_TYPE_REPORT && 
				(getSelectTimeType() == TAB_TIME_CUSTOM || 
				getSelectTimeType() == TAB_TIME_LAST8HOUR ||
				getSelectTimeType() == TAB_TIME_LASTCALEWEEK ||
				(position==DashboardAction.DA_TAB_POSITION_APPLICATION &&
				 getSelectTimeType() == TAB_TIME_LASTDAY) || 
				 (position==DashboardAction.DA_TAB_POSITION_APPLICATION &&
				 getSelectTimeType() == TAB_TIME_LASTWEEK))) {
			if (getSelectTimeType() == TAB_TIME_CUSTOM) {
				StringBuilder bd = new StringBuilder();
				SimpleDateFormat l_sdf = new SimpleDateFormat("MMM d, yyyy, h:mma");
				l_sdf.setTimeZone(getTz());
				Calendar ca = Calendar.getInstance(getTz());
				ca.setTimeInMillis(getCustomStartTime());
				bd.append(l_sdf.format(ca.getTime()));
				ca.setTimeInMillis(getCustomEndTime());
				bd.append(" to ");
				bd.append(l_sdf.format(ca.getTime()));
				return bd.toString();
			} else if (getSelectTimeType()==TAB_TIME_LAST8HOUR) {
				StringBuilder bd = new StringBuilder();
				SimpleDateFormat l_sdf = new SimpleDateFormat("MMM d, yyyy, h:mma");
				l_sdf.setTimeZone(getTz());
				Calendar ca = Calendar.getInstance(getTz());
				long currentTime = ca.getTimeInMillis();
				ca.setTimeInMillis(currentTime-3600000L*8);
				ca.setTimeInMillis(ca.getTimeInMillis()/3600000L*3600000L);
				bd.append(l_sdf.format(ca.getTime()));
				ca.setTimeInMillis(currentTime/3600000L*3600000L);
				bd.append(" to ");
				bd.append(l_sdf.format(ca.getTime()));
				return bd.toString();
			} else if (getSelectTimeType()==TAB_TIME_LASTDAY) {
				StringBuilder bd = new StringBuilder();
				SimpleDateFormat l_sdf = new SimpleDateFormat("MMM d, yyyy, h:mma");
				l_sdf.setTimeZone(getTz());
				Calendar ca = Calendar.getInstance(getTz());
				long currentTime = ca.getTimeInMillis();
				ca.setTimeInMillis(currentTime-3600000L*24);
				ca.setTimeInMillis(ca.getTimeInMillis()/3600000L*3600000L);
				bd.append(l_sdf.format(ca.getTime()));
				ca.setTimeInMillis(currentTime/3600000L*3600000L);
				bd.append(" to ");
				bd.append(l_sdf.format(ca.getTime()));
				return bd.toString();
			} else if (getSelectTimeType()==TAB_TIME_LASTWEEK) {
				StringBuilder bd = new StringBuilder();
				SimpleDateFormat l_sdf = new SimpleDateFormat("MMM d, yyyy, h:mma");
				l_sdf.setTimeZone(getTz());
				Calendar ca = Calendar.getInstance(getTz());
				long currentTime = ca.getTimeInMillis();
				ca.setTimeInMillis(currentTime-3600000*24 * 7);
				ca.clear(Calendar.MINUTE);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MILLISECOND);
				ca.set(Calendar.HOUR_OF_DAY,0);
				bd.append(l_sdf.format(ca.getTime()));
				
				ca.setTimeInMillis(currentTime);
				ca.clear(Calendar.MINUTE);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MILLISECOND);
				ca.set(Calendar.HOUR_OF_DAY,0);
				bd.append(" to ");
				bd.append(l_sdf.format(ca.getTime()));
				return bd.toString();
			} else if (getSelectTimeType()==TAB_TIME_LASTCALEWEEK) {
				StringBuilder bd = new StringBuilder();
				SimpleDateFormat l_sdf = new SimpleDateFormat("MMM d, yyyy, h:mma");
				l_sdf.setTimeZone(getTz());
				Calendar ca = Calendar.getInstance(getTz());
				long currentTime = ca.getTimeInMillis();
				ca.setTimeInMillis(currentTime-3600000*24 * 7);
				ca.clear(Calendar.MINUTE);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MILLISECOND);
				ca.set(Calendar.HOUR_OF_DAY,0);
				ca.add(Calendar.DAY_OF_MONTH,(-1* (ca.get(Calendar.DAY_OF_WEEK)-1)));
				bd.append(l_sdf.format(ca.getTime()));
				ca.add(Calendar.DAY_OF_MONTH,7);
				bd.append(" to ");
				bd.append(l_sdf.format(ca.getTime()));
				return bd.toString();
			}
			
		}
		return "";
	}

	public Set<AhDashboardLayout> getDaLayouts() {
		return daLayouts;
	}

	public void setDaLayouts(Set<AhDashboardLayout> daLayouts) {
		this.daLayouts = daLayouts;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = (userName==null? "": userName);
	}

	public String getDashName() {
		return dashName;
	}

	public void setDashName(String dashName) {
		this.dashName = (dashName==null? "": dashName);
	}

	public int getDaType() {
		return daType;
	}

	public void setDaType(int daType) {
		this.daType = daType;
	}

//	public MapContainerNode getLocation() {
//		return location;
//	}
//
//	public void setLocation(MapContainerNode location) {
//		this.location = location;
//	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public int getSelectTimeType() {
		return selectTimeType;
	}

	public void setSelectTimeType(int selectTimeType) {
		this.selectTimeType = selectTimeType;
	}

	public long getCustomStartTime() {
		return customStartTime;
	}

	public void setCustomStartTime(long customStartTime) {
		this.customStartTime = customStartTime;
	}

	public long getCustomEndTime() {
		return customEndTime;
	}

	public void setCustomEndTime(long customEndTime) {
		this.customEndTime = customEndTime;
	}

	public boolean isBgRollup() {
		return bgRollup;
	}

	public void setBgRollup(boolean bgRollup) {
		this.bgRollup = bgRollup;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public AhDashboard clone() {
		try {
			return (AhDashboard) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public boolean isEnableTimeLocal() {
		return enableTimeLocal;
	}

	public void setEnableTimeLocal(boolean enableTimeLocal) {
		this.enableTimeLocal = enableTimeLocal;
	}

	@Transient
	public boolean isMonitorKindOfDa() {
		return this.daType != DASHBOARD_TYPE_DASH
				&& this.daType != DASHBOARD_TYPE_REPORT;
	}

	public String getFilterObjectType() {
		return filterObjectType;
	}

	public void setFilterObjectType(String filterObjectType) {
		this.filterObjectType = filterObjectType;
	}

	public String getFilterObjectId() {
		return filterObjectId;
	}

	public void setFilterObjectId(String filterObjectId) {
		this.filterObjectId = filterObjectId;
	}

	public int getRefrequency() {
		return refrequency;
	}

	public void setRefrequency(int refrequency) {
		this.refrequency = refrequency;
	}

	public boolean isReCustomDay() {
		return reCustomDay;
	}

	public void setReCustomDay(boolean reCustomDay) {
		this.reCustomDay = reCustomDay;
	}

	public boolean isReCustomTime() {
		return reCustomTime;
	}

	public void setReCustomTime(boolean reCustomTime) {
		this.reCustomTime = reCustomTime;
	}

	public String getReCustomDayValue() {
		return reCustomDayValue;
	}

	public void setReCustomDayValue(String reCustomDayValue) {
		this.reCustomDayValue = reCustomDayValue;
	}

	public int getReCustomTimeStart() {
		return reCustomTimeStart;
	}

	public void setReCustomTimeStart(int reCustomTimeStart) {
		this.reCustomTimeStart = reCustomTimeStart;
	}

	public int getReCustomTimeEnd() {
		return reCustomTimeEnd;
	}

	public void setReCustomTimeEnd(int reCustomTimeEnd) {
		this.reCustomTimeEnd = reCustomTimeEnd;
	}

	public String getReEmailAddress() {
		return reEmailAddress;
	}

	public void setReEmailAddress(String reEmailAddress) {
		this.reEmailAddress = (reEmailAddress==null? "": reEmailAddress);
	}

	public int getReWeekStart() {
		return reWeekStart;
	}

	public void setReWeekStart(int reWeekStart) {
		this.reWeekStart = reWeekStart;
	}

	public int getReCmTimeType() {
		return reCmTimeType;
	}

	public void setReCmTimeType(int reCmTimeType) {
		this.reCmTimeType = reCmTimeType;
	}

	public int getReCmTimePeriod() {
		return reCmTimePeriod;
	}

	public void setReCmTimePeriod(int reCmTimePeriod) {
		this.reCmTimePeriod = reCmTimePeriod;
	}

	public int getReCmTimeStartDayType() {
		return reCmTimeStartDayType;
	}

	public void setReCmTimeStartDayType(int reCmTimeStartDayType) {
		this.reCmTimeStartDayType = reCmTimeStartDayType;
	}

	public int getReCmTimeStartDayValue() {
		return reCmTimeStartDayValue;
	}

	public void setReCmTimeStartDayValue(int reCmTimeStartDayValue) {
		this.reCmTimeStartDayValue = reCmTimeStartDayValue;
	}

	public int getReCmTimeStartMontyYear() {
		return reCmTimeStartMontyYear;
	}

	public void setReCmTimeStartMontyYear(int reCmTimeStartMontyYear) {
		this.reCmTimeStartMontyYear = reCmTimeStartMontyYear;
	}

	public int getReCmTimeStartSepcYear() {
		return reCmTimeStartSepcYear;
	}

	public void setReCmTimeStartSepcYear(int reCmTimeStartSepcYear) {
		this.reCmTimeStartSepcYear = reCmTimeStartSepcYear;
	}

	public String getPdfHeader() {
		return pdfHeader;
	}

	public void setPdfHeader(String pdfHeader) {
		this.pdfHeader = pdfHeader;
	}

	public String getPdfFooter() {
		return pdfFooter;
	}

	public void setPdfFooter(String pdfFooter) {
		this.pdfFooter = pdfFooter;
	}

	public String getPdfSummary() {
		return pdfSummary;
	}

	public void setPdfSummary(String pdfSummary) {
		this.pdfSummary = pdfSummary;
	}


	/**
	 * indicate whether the dashboard is newly created but without data,
	 * you should only use this flag when you want to indicate this dashboard is not persistent one
	 */
	@Transient
	private boolean blnNullNewDa;

	public boolean isBlnNullNewDa() {
		return blnNullNewDa;
	}

	public void setBlnNullNewDa(boolean blnNullNewDa) {
		this.blnNullNewDa = blnNullNewDa;
	}

	public boolean isBlnTypeDashboard() {
		return this.daType == DASHBOARD_TYPE_DASH;
	}

	public boolean isBlnTypeRecurReport() {
		return this.daType == DASHBOARD_TYPE_REPORT;
	}

	@Transient
	public JSONObject getDashboardAsWidgetConfigJSONObject(TimeZone tz) throws JSONException {
		JSONObject jObjInfo = new JSONObject();

		jObjInfo.put("checked", false);
//		jObjInfo.put("lid", this.location == null?Long.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_MAP):this.location.getId());
		jObjInfo.put("obType", StringUtils.isBlank(this.getObjectType()) ? AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL :this.getObjectType());
		jObjInfo.put("obId", StringUtils.isBlank(this.getObjectId()) ? String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL) : this.getObjectId());
		jObjInfo.put("fobType", StringUtils.isBlank(this.getFilterObjectType()) ? String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL) : this.getFilterObjectType());
		jObjInfo.put("fobId", StringUtils.isBlank(this.getFilterObjectId()) ? String.valueOf(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL) : this.getFilterObjectId());
		jObjInfo.put("timeType", this.getSelectTimeType() <= 0 ? TAB_TIME_LASTDAY : this.getSelectTimeType());
		jObjInfo.put("enableTimeLocal", this.isEnableTimeLocal());

		Calendar calendar;
		if (tz != null) {
			calendar = Calendar.getInstance(tz);
		} else {
			calendar = Calendar.getInstance();
		}
		SimpleDateFormat l_sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (tz != null) {
			l_sdf.setTimeZone(tz);
		}
		calendar.setTimeInMillis(this.getCustomStartTime());
		jObjInfo.put("startDate", l_sdf.format(calendar.getTime()));
		jObjInfo.put("startHour", calendar.get(Calendar.HOUR_OF_DAY));
		calendar.setTimeInMillis(this.getCustomEndTime());
		jObjInfo.put("endDate", l_sdf.format(calendar.getTime()));
		jObjInfo.put("endHour", calendar.get(Calendar.HOUR_OF_DAY));

		return jObjInfo;
	}

	public int getReportScheduleStatus() {
		return reportScheduleStatus;
	}

	public void setReportScheduleStatus(int reportScheduleStatus) {
		this.reportScheduleStatus = reportScheduleStatus;
	}

	@Transient
	public String getRefrequencyString() {
		switch (refrequency) {
		case AhNewReport.NEW_REPORT_FREQUENCY_DAILY:
		case AhNewReport.NEW_REPORT_FREQUENCY_WEEKLY:
		case AhNewReport.NEW_REPORT_FREQUENCY_MONTHLY:
		case AhNewReport.NEW_REPORT_FREQUENCY_CUSTOM:
			return MgrUtil.getEnumString("enum.report.networkusage.frequency."
					+ refrequency);
		default:
			return "";
		}
	}

	@Transient
	public String getReportScheduleStatusString() {
		switch (reportScheduleStatus) {
		case REPORT_STATUS_UNSCHEDULED:
		case REPORT_STATUS_SCHEDULED:
		case REPORT_STATUS_DISABLED:
			return MgrUtil.getEnumString("enum.report.schedule.status."
					+ reportScheduleStatus);
		default:
			return "";
		}
	}

	public boolean isCloneDash() {
		return cloneDash;
	}

	public void setCloneDash(boolean cloneDash) {
		this.cloneDash = cloneDash;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Transient
	private Map<String, Set<String>> ssidCountList;
	@Transient
	private Map<String, Set<String>> apTypeCountList;
	
	public Map<String, Set<String>> getSsidCountList() {
		return ssidCountList;
	}

	public void setSsidCountList(Map<String, Set<String>> ssidCountList) {
		this.ssidCountList = ssidCountList;
	}

	public Map<String, Set<String>> getApTypeCountList() {
		return apTypeCountList;
	}

	public void setApTypeCountList(Map<String, Set<String>> apTypeCountList) {
		this.apTypeCountList = apTypeCountList;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public boolean isApplicationPerspective() {
		return this.getPosition() == DashboardAction.DA_TAB_POSITION_APPLICATION;
	}
	
}