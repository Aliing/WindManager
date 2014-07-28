package com.ah.bo.performance;

import java.sql.Timestamp;
import java.util.Calendar;
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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import com.ah.be.parameter.BeParaModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "HM_NEW_REPORT")
@org.hibernate.annotations.Table(appliesTo = "HM_NEW_REPORT", indexes = {
		@Index(name = "NEW_REPORT_OWNER", columnNames = { "OWNER" })
		})
public class AhNewReport implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Column(length = DEFAULT_STRING_LENGTH,nullable = false)
	private String name;
	
	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	private String ssidName;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "LOCATION_ID")
	private MapContainerNode location;
	
	//this field is no used for this version, maybe used next version.
	private int reportType=1;
	
	// 1:Immediately
	// 2:Schedule
	public static final String NEW_REPORT_EXCUTETYPE_IMME="1";
	public static final String NEW_REPORT_EXCUTETYPE_SCHE="2";
	private String excuteType="1";
	
	//1:dayly, 2:weekly, 3: monthly 4: custom(used for report)
	public static final int NEW_REPORT_FREQUENCY_DAILY=1;
	public static final int NEW_REPORT_FREQUENCY_WEEKLY=2;
	public static final int NEW_REPORT_FREQUENCY_MONTHLY=3;
	public static final int NEW_REPORT_FREQUENCY_CUSTOM=4;
	private int frequency=NEW_REPORT_FREQUENCY_DAILY;
	public static EnumItem[] NEW_REPORT_FREQUENCY_TYPE = MgrUtil.enumItems(
			"enum.report.networkusage.frequency.", new int[] {
					NEW_REPORT_FREQUENCY_DAILY, NEW_REPORT_FREQUENCY_WEEKLY,
					NEW_REPORT_FREQUENCY_MONTHLY});
	
	private boolean customDay=false;
	private boolean customTime=false;
	
	private String customDayValue="0111110";
	
	private int customTimeStart;
	private int customTimeEnd;
	
	public static final int NEW_REPORT_PERIOD_LASTONEHOUR = 1;
	public static final int NEW_REPORT_PERIOD_LASTCLOCKHOUR = 2;
	public static final int NEW_REPORT_PERIOD_LASTONEDAY = 3;
	public static final int NEW_REPORT_PERIOD_LASTCALENDARDAY = 4;
	public static final int NEW_REPORT_PERIOD_LASTWEEK = 5;
	public static final int NEW_REPORT_PERIOD_LASTCALENDARWEEK = 6;
	public static final int NEW_REPORT_PERIOD_LASTONEMONTH = 7;
	public static final int NEW_REPORT_PERIOD_LASTCALENDARMONTH = 8;
	public static final int NEW_REPORT_PERIOD_CUSTOM = 9;
	public static EnumItem[] NEW_REPORT_PERIOD_TYPE = MgrUtil.enumItems(
			"enum.report.networkusage.reportPeriod.", new int[] {
					NEW_REPORT_PERIOD_LASTONEHOUR, NEW_REPORT_PERIOD_LASTCLOCKHOUR,
					NEW_REPORT_PERIOD_LASTONEDAY, NEW_REPORT_PERIOD_LASTCALENDARDAY,
					NEW_REPORT_PERIOD_LASTWEEK, NEW_REPORT_PERIOD_LASTCALENDARWEEK,
					NEW_REPORT_PERIOD_LASTONEMONTH,
					NEW_REPORT_PERIOD_LASTCALENDARMONTH, NEW_REPORT_PERIOD_CUSTOM});

	private int reportPeriod=NEW_REPORT_PERIOD_CUSTOM;

	private long startTime;
	
	private long endTime;

	private String emailAddress;
	
	private boolean defaultFlag;
	
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
		return name;
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
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getReportPeriod() {
		return reportPeriod;
	}

	public void setReportPeriod(int reportPeriod) {
		this.reportPeriod = reportPeriod;
	}


	public String getExcuteType() {
		return excuteType;
	}

	public String getExcuteTypeString() {
		if (excuteType.equals("2")) {
			return "Scheduled";
		}
		return "On Demand";
	}

	public void setExcuteType(String excuteType) {
		this.excuteType = excuteType;
	}
	
	public long getRunStartTime(Calendar ca){
		if (excuteType.equals("1")) {
			switch (reportPeriod) {
			case NEW_REPORT_PERIOD_LASTONEHOUR: 
				return ca.getTimeInMillis()-3600000L;
			case NEW_REPORT_PERIOD_LASTCLOCKHOUR: 
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				return ca.getTimeInMillis()-3600000L;
			case NEW_REPORT_PERIOD_LASTONEDAY:
				return ca.getTimeInMillis()-3600000L * 24;
			case NEW_REPORT_PERIOD_LASTCALENDARDAY: 
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				ca.set(Calendar.HOUR_OF_DAY,0);
				return ca.getTimeInMillis()-3600000L * 24;
			case NEW_REPORT_PERIOD_LASTWEEK: 
				return ca.getTimeInMillis()-3600000L * 24 * 7;
			case NEW_REPORT_PERIOD_LASTCALENDARWEEK: 
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				ca.set(Calendar.HOUR_OF_DAY,0);
				ca.add(Calendar.DATE, (ca.get(Calendar.DAY_OF_WEEK)-1) *-1);
				return ca.getTimeInMillis()-3600000L * 24 * 7;
			case NEW_REPORT_PERIOD_LASTONEMONTH: 
				return ca.getTimeInMillis()-3600000L * 24 * 30;
			case NEW_REPORT_PERIOD_LASTCALENDARMONTH: 
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				ca.set(Calendar.HOUR_OF_DAY,0);
				ca.add(Calendar.DATE, (ca.get(Calendar.DAY_OF_MONTH)-1) *-1);
				return ca.getTimeInMillis()-3600000L * 24 * 30;
			case NEW_REPORT_PERIOD_CUSTOM: 
				return startTime;
			}
		} else {
			if (frequency==NEW_REPORT_FREQUENCY_DAILY){
				if (isCustomTime()) {
					ca.clear(Calendar.MILLISECOND);
					ca.clear(Calendar.SECOND);
					ca.clear(Calendar.MINUTE);
					ca.set(Calendar.HOUR_OF_DAY,0);
					ca.add(Calendar.DATE, -1);
					ca.set(Calendar.HOUR_OF_DAY,customTimeStart);
					return ca.getTimeInMillis();
				} else {
					ca.clear(Calendar.MILLISECOND);
					ca.clear(Calendar.SECOND);
					ca.clear(Calendar.MINUTE);
					ca.set(Calendar.HOUR_OF_DAY,0);
					ca.add(Calendar.DATE, -1);
					return ca.getTimeInMillis();
				}
			} else if (frequency==NEW_REPORT_FREQUENCY_WEEKLY){
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				ca.set(Calendar.HOUR_OF_DAY,0);
				ca.add(Calendar.DATE, -7);
				return ca.getTimeInMillis();
			} else {
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				ca.set(Calendar.HOUR_OF_DAY,0);
				ca.add(Calendar.DATE, -30);
				return ca.getTimeInMillis();
			}
		}
		        
		return 0;
	}
	
	public long getRunEndTime(Calendar ca){
		if (excuteType.equals("1")) {
			switch (reportPeriod) {
			case NEW_REPORT_PERIOD_LASTONEHOUR: 
				return ca.getTimeInMillis();
			case NEW_REPORT_PERIOD_LASTCLOCKHOUR: 
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				return ca.getTimeInMillis();
			case NEW_REPORT_PERIOD_LASTONEDAY:
				return ca.getTimeInMillis();
			case NEW_REPORT_PERIOD_LASTCALENDARDAY: 
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				ca.set(Calendar.HOUR_OF_DAY,0);
				return ca.getTimeInMillis();
			case NEW_REPORT_PERIOD_LASTWEEK: 
				return ca.getTimeInMillis();
			case NEW_REPORT_PERIOD_LASTCALENDARWEEK: 
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				ca.set(Calendar.HOUR_OF_DAY,0);
				ca.add(Calendar.DATE, (ca.get(Calendar.DAY_OF_WEEK)-1) *-1);
				return ca.getTimeInMillis();
			case NEW_REPORT_PERIOD_LASTONEMONTH: 
				return ca.getTimeInMillis();
			case NEW_REPORT_PERIOD_LASTCALENDARMONTH: 
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				ca.set(Calendar.HOUR_OF_DAY,0);
				ca.add(Calendar.DATE, (ca.get(Calendar.DAY_OF_MONTH)-1) *-1);
				return ca.getTimeInMillis();
			case NEW_REPORT_PERIOD_CUSTOM: 
				return endTime;
			}
		} else {
			if (frequency==NEW_REPORT_FREQUENCY_DAILY){
				if (isCustomTime()) {
					ca.clear(Calendar.MILLISECOND);
					ca.clear(Calendar.SECOND);
					ca.clear(Calendar.MINUTE);
					ca.set(Calendar.HOUR_OF_DAY,0);
					ca.add(Calendar.DATE, -1);
					ca.set(Calendar.HOUR_OF_DAY,customTimeEnd);
					return ca.getTimeInMillis();
				} else {
					ca.clear(Calendar.MILLISECOND);
					ca.clear(Calendar.SECOND);
					ca.clear(Calendar.MINUTE);
					ca.set(Calendar.HOUR_OF_DAY,0);
					return ca.getTimeInMillis();
				}
			} else {
				ca.clear(Calendar.MILLISECOND);
				ca.clear(Calendar.SECOND);
				ca.clear(Calendar.MINUTE);
				ca.set(Calendar.HOUR_OF_DAY,0);
				return ca.getTimeInMillis();
			}
		}
		return 0;
	}

	public boolean getRunScheduleTime(long timeInMil) {
		if (excuteType.equals("1")) {
			return false;
		}
		Calendar renCalendar = Calendar.getInstance(owner.getTimeZone());
		renCalendar.setTimeInMillis(timeInMil);
		//renCalendar.setTimeZone(owner.getTimeZone());
		if (frequency==NEW_REPORT_FREQUENCY_DAILY){
			if (renCalendar.get(Calendar.HOUR_OF_DAY)==3){
				renCalendar.add(Calendar.DATE, -1);
				if(customDay){
					if (customDayValue.substring(renCalendar.get(Calendar.DAY_OF_WEEK)-1, renCalendar.get(Calendar.DAY_OF_WEEK)).equals("1")){
						return true;
					}
				} else {
					return true;
				}
			}
		} else if (frequency==NEW_REPORT_FREQUENCY_WEEKLY){
			if (renCalendar.get(Calendar.HOUR_OF_DAY)==3 && renCalendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
				return true;
			}
		} else {
			if (renCalendar.get(Calendar.HOUR_OF_DAY)==3 && renCalendar.get(Calendar.DAY_OF_MONTH)==1){
				return true;
			}
		}
		return false;

	}
	
	@Transient
	private String runStartTimeString; 
	
	public String getRunStartTimeString() {
		return runStartTimeString;
	}

	public void setRunStartTimeString(String runStartTimeString) {
		this.runStartTimeString = runStartTimeString;
	}

	public String getRunEndTimeString() {
		return runEndTimeString;
	}

	public void setRunEndTimeString(String runEndTimeString) {
		this.runEndTimeString = runEndTimeString;
	}

	@Transient
	private String runEndTimeString; 


	public long getStartTime() {
		return startTime;
	}
	
	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public boolean isCustomDay() {
		return customDay;
	}

	public void setCustomDay(boolean customDay) {
		this.customDay = customDay;
	}

	public boolean isCustomTime() {
		return customTime;
	}

	public void setCustomTime(boolean customTime) {
		this.customTime = customTime;
	}

	public String getCustomDayValue() {
		return customDayValue;
	}

	public void setCustomDayValue(String customDayValue) {
		this.customDayValue = customDayValue;
	}

	public int getCustomTimeStart() {
		return customTimeStart;
	}

	public void setCustomTimeStart(int customTimeStart) {
		this.customTimeStart = customTimeStart;
	}

	public int getCustomTimeEnd() {
		return customTimeEnd;
	}

	public void setCustomTimeEnd(int customTimeEnd) {
		this.customTimeEnd = customTimeEnd;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public MapContainerNode getLocation() {
		return location;
	}

	public void setLocation(MapContainerNode location) {
		this.location = location;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getSsidName() {
		return ssidName;
	}

	public void setSsidName(String ssidName) {
		this.ssidName = ssidName;
	}
	
	public String getNoBlankString(String strValue){
		if (strValue==null) {
			return "";
		}
		return strValue.replaceAll(" ", "");
	}

	@Override
	public void setVersion(Timestamp version) {

	}

	public TimeZone getTz() {
		if (tz==null) return getOwner().getTimeZone();
		return tz;
	}

	public void setTz(TimeZone tz) {
		this.tz = tz;
	}

	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	@Transient
	private int buttonType=1;
	
	@Transient
	private long reportStartTime = 0;

	public long getReportStartTime() {
		return reportStartTime;
	}

	public void setReportStartTime(long reportStartTime) {
		this.reportStartTime = reportStartTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getButtonType() {
		return buttonType;
	}

	public void setButtonType(int buttonType) {
		this.buttonType = buttonType;
	}

	public boolean isScheduleMode() {
		return this.getExcuteType().equals(NEW_REPORT_EXCUTETYPE_SCHE);
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

	@Transient
	private Set<String> apNameList;

	public Set<String> getApNameList() {
		return apNameList;
	}

	public void setApNameList(Set<String> apNameList) {
		this.apNameList = apNameList;
	}
	
	@Transient
	private Set<String> apMacList;

	public Set<String> getApMacList() {
		return apMacList;
	}

	public void setApMacList(Set<String> apMacList) {
		this.apMacList = apMacList;
	}
	
	public String getReportPeriodString(){
		if (this.getExcuteType().equals("2")) {
			return MgrUtil.getEnumString("enum.report.networkusage.frequency." + frequency);
		} else {
			return MgrUtil.getEnumString("enum.report.networkusage.reportPeriod." + reportPeriod);
		}
	}
	
	public boolean isForSample() {
		if (BeParaModule.DEFAULT_NETWORK_REPORT_SAMPLE.equals(this.name) && getDefaultFlag()) {
			return true;
		}
		
		return false;
	}
}