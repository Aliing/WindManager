package com.ah.bo.wlan;

import java.sql.Timestamp;
import java.util.Calendar;
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
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.be.app.DebugUtil;
import com.ah.be.config.create.source.impl.ScheduleProfileImpl;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "SCHEDULER")
@org.hibernate.annotations.Table(appliesTo = "SCHEDULER", indexes = {
		@Index(name = "SCHEDULER_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Scheduler implements HmBo {

	private static final long serialVersionUID = 1L;
	
	public static final int ONE_TIME = 1;
	public static final int RECURRENT = 0;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp version;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String schedulerName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	@Column(length = 20)
	private String beginDate;

	@Column(length = 20)
	private String endDate;

	@Column(length = 30)
	private String weeks;

	@Column(length = 20)
	private String beginTime;

	@Column(length = 20)
	private String endTime;

	@Column(length = 20)
	private String beginTimeS;

	@Column(length = 20)
	private String endTimeS;

	private int type = ONE_TIME;

	@Transient
	public String[] getFieldValues() {
		return new String[] { "id", "schedulerName", "description",
				"beginDate", "endDate", "weeks", "beginTime", "endTime",
				"beginTimeS", "endTimeS", "type","owner" };
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getBeginTimeS() {
		return beginTimeS;
	}

	public void setBeginTimeS(String beginTimeS) {
		this.beginTimeS = beginTimeS;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getEndTimeS() {
		return endTimeS;
	}

	public void setEndTimeS(String endTimeS) {
		this.endTimeS = endTimeS;
	}

	public String getSchedulerName() {
		return schedulerName;
	}

	public void setSchedulerName(String schedulerName) {
		this.schedulerName = schedulerName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getWeeks() {
		return weeks;
	}

	public void setWeeks(String weeks) {
		this.weeks = weeks;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Transient
	public String getValue() {
		return schedulerName;
	}

	@Override
	public String getLabel() {
		return schedulerName;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Transient
	private String recurrent = "One-time";

	public String getRecurrent() {
		if (type != ONE_TIME)
			recurrent = "Recurrent";
		return recurrent;
	}

	public void setRecurrent(String recurrent) {
		this.recurrent = recurrent;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
//	public String getAllTimeString(){
//		if (type==ONE_TIME) {
//			return "[StartTime:]" + beginDate + " " + beginTime + " [EndTime:]" + endDate + " " + endTime;
//		} else {
//			String retStr = "[StartTime1]:" + beginTime + " [EndTime1]:" + endTime;
//			if (beginTimeS != null && !beginTimeS.equals("")) {
//				retStr = retStr + " [StartTime2]:" + beginTimeS + " [EndTime2]:" + endTimeS;
//			}
//			if(weeks != null && !weeks.equals("")) {
//				retStr = retStr + " [Weeks]:" + weeks;
//			}
//			if (beginDate != null && !beginDate.equals("")){
//				retStr = retStr + " [StartDate]:" + beginDate;
//			}
//			if (endDate != null && !endDate.equals("")){
//				retStr = retStr + " [EndDate]:" + endDate;
//			}
//			return retStr;
//		}
//	}
	
	/**
	 * get date from string ,such as 2009-03-05
	 *
	 * @param arg_Date -
	 * @return -
	 */
	private Calendar getDateFromString(String arg_Date) {
		if(null == arg_Date)
			return null;
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(0);
		try {
			String[] split=arg_Date.split("-");
			if(split.length != 3)
				return null;
			int year = Integer.parseInt(split[0]);
			int month = Integer.parseInt(split[1])-1;
			int day = Integer.parseInt(split[2]);
			date.set(year, month, day,0,0,0);
			//System.out.println(date.getTime());
			return date;
		}catch(Exception e)
		{
			return null;
		}
	}
	
	/**
	 * get time from string ,such as 03:00
	 *
	 * @param arg_Time -
	 * @return -
	 */
	private long getTimeFromString(String arg_Time) {
		if(arg_Time == null)
			return 0;
		try {
			String[] split=arg_Time.split(":");
			if(split.length != 2)
				return 0;
			int hour = Integer.parseInt(split[0]);
			int minute = Integer.parseInt(split[1]);
			
			return (hour*60*60+minute*60)*1000;
		}catch(Exception e)
		{
			return 0;
		}
	}
	
	private int getDayInWeekIndex(String weekName) {
		if("Saturday".equals(weekName)) {
			return 7;
		}
		else if("Friday".equals(weekName)) {
			return 6;
		}
		else if("Thursday".equals(weekName)) {
			return 5;
		}
		else if("Wednesday".equals(weekName)) {
			return 4;
		}
		else if("Tuesday".equals(weekName)) {
			return 3;
		}
		else if("Monday".equals(weekName)) {
			return 2;
		}
		else if("Sunday".equals(weekName)) {
			return 1;
		}
		return 1;
	}
	

	/**
	 * get start and stop time according with current time
	 *
	 * @param apTimeZone -
	 * @return -
	 */
	public Calendar[] getStartAndEndTime(TimeZone apTimeZone) {
		long long_Start;
		long long_Stop;
		
		long startTimeOffset=0;
		long stopTimeOffset=0;
		
		Calendar[] calen_Return = null;
		Calendar start_Date = getDateFromString(beginDate);
		Calendar stop_Date = getDateFromString(endDate);
		if (start_Date!=null){
			startTimeOffset = getTimeZoneOffSet(start_Date,apTimeZone);
			start_Date.add(Calendar.HOUR_OF_DAY, (int)startTimeOffset/3600000);
		}
		if (stop_Date!=null){
			stopTimeOffset = getTimeZoneOffSet(stop_Date,apTimeZone);
			stop_Date.add(Calendar.HOUR_OF_DAY, (int)stopTimeOffset/3600000);
		}
		long long_StartTime = getTimeFromString(beginTime);
		long long_StopTime = getTimeFromString(endTime);
		long long_StartTimeS = getTimeFromString(beginTimeS);
		long long_StopTimeS = getTimeFromString(endTimeS);
		long now = System.currentTimeMillis();
		if(type == ONE_TIME)
		{
			//one time
			if(start_Date == null || stop_Date == null)
				return null;
			
			long_Start = start_Date.getTimeInMillis()+long_StartTime;
			long_Stop = stop_Date.getTimeInMillis()+long_StopTime;
			
			if(now <= long_Stop)
			{
				calen_Return = new Calendar[2];
				start_Date.setTimeInMillis(long_Start - startTimeOffset);
				stop_Date.setTimeInMillis(long_Stop - stopTimeOffset);
				calen_Return[0] = start_Date;
				calen_Return[1] = stop_Date;
			}
			return calen_Return;
		}
		//recurrent
		//get period time 1 and period time 2 order by time
		long long_StartTime1 = long_StartTime < long_StartTimeS ? long_StartTime:long_StartTimeS;
		long long_StartTime2 = long_StartTime > long_StartTimeS ? long_StartTime:long_StartTimeS;
		
		long long_StopTime1 = long_StopTime < long_StopTimeS ? long_StopTime:long_StopTimeS;
		long long_StopTime2 = long_StopTime > long_StopTimeS ? long_StopTime:long_StopTimeS;
		
		//check stop date
		if(stop_Date != null) {
			long_Stop = stop_Date.getTimeInMillis()+long_StopTime2;
			if(now > long_Stop) {
				return null;
			}
		}
		String fromWeek;
		String toWeek;
		int int_fromWeek;
		int int_toWeek;
		boolean[] weekEnable = new boolean[7];
		for(int i = 0; i < 7; i++)
			weekEnable[i] = false;
		if(weeks != null && weeks.length() != 0)
		{
			
			fromWeek = ScheduleProfileImpl.getFromWeek(weeks);
			toWeek = ScheduleProfileImpl.getToWeekRealForPPSK(weeks);
			if (fromWeek.equals(toWeek)) {
				int_fromWeek = getDayInWeekIndex(fromWeek);
				weekEnable[int_fromWeek-1] = true;
			} else {
				int_fromWeek = getDayInWeekIndex(fromWeek);
				int_toWeek = getDayInWeekIndex(toWeek);
				
				weekEnable[int_fromWeek-1] = true;
				while(int_fromWeek!= int_toWeek) {
					if(int_fromWeek < 7)
						int_fromWeek++;
					else
						int_fromWeek = 1;
					weekEnable[int_fromWeek-1] = true;
				}
			}
		}
		
		//get current date
		Calendar calen_CurrentDate = Calendar.getInstance();
		startTimeOffset = getTimeZoneOffSet(calen_CurrentDate,apTimeZone);
		calen_CurrentDate.setTimeInMillis(now-startTimeOffset);
		calen_CurrentDate.set(Calendar.HOUR_OF_DAY, 0);
		calen_CurrentDate.set(Calendar.MINUTE, 0);
		calen_CurrentDate.set(Calendar.SECOND, 0);
		calen_CurrentDate.set(Calendar.MILLISECOND, 0);
		calen_CurrentDate.setTimeZone(apTimeZone);
//		startTimeOffset = getTimeZoneOffSet(calen_CurrentDate,apTimeZone);
//		calen_CurrentDate.add(Calendar.HOUR_OF_DAY, (int)startTimeOffset/3600000);
		
		if(start_Date != null) {
			long_Start = (calen_CurrentDate.getTimeInMillis() > start_Date.getTimeInMillis())?calen_CurrentDate.getTimeInMillis():start_Date.getTimeInMillis();
		} else {
			long_Start = calen_CurrentDate.getTimeInMillis();
		}	
		calen_CurrentDate.setTimeInMillis(long_Start);
		
		calen_Return = new Calendar[2];
		calen_Return[0] = Calendar.getInstance();
		calen_Return[1] = Calendar.getInstance();
		
		//get the next start and stop time from current date
		while(true) {
			//check week
			if(weeks != null && weeks.length() != 0) {
				int int_CurrWeek = calen_CurrentDate.get(Calendar.DAY_OF_WEEK);
				
				if(!weekEnable[int_CurrWeek - 1])
				{
					calen_CurrentDate.add(Calendar.DAY_OF_MONTH, 1);
					continue;
				}
			}
			//check period time 1
			if(long_StartTime1 != 0 || long_StopTime1 != 0) {
				long_Start = calen_CurrentDate.getTimeInMillis()+long_StartTime1;
				long_Stop = calen_CurrentDate.getTimeInMillis()+long_StopTime1;
				if(now <= long_Stop)
					break;
			}
			
			//check period time 2
			if(long_StartTime2 != 0 || long_StopTime2 != 0) {
				long_Start = calen_CurrentDate.getTimeInMillis()+long_StartTime2;
				long_Stop = calen_CurrentDate.getTimeInMillis()+long_StopTime2;
				if(now <= long_Stop)
					break;
			}
			calen_CurrentDate.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		Calendar retCale = Calendar.getInstance();
		retCale.setTimeInMillis(long_Start);
		startTimeOffset = getTimeZoneOffSet(retCale,apTimeZone);
		
		retCale.setTimeInMillis(long_Stop);
		stopTimeOffset = getTimeZoneOffSet(retCale,apTimeZone);
		
		calen_Return[0].setTimeInMillis(long_Start-startTimeOffset);
		calen_Return[1].setTimeInMillis(long_Stop-stopTimeOffset);
		return calen_Return;
	}
	
	public  long getTimeZoneOffSet(Calendar cale,TimeZone tz){
		try{
			Calendar retCale = Calendar.getInstance(tz);
			retCale.clear(Calendar.MILLISECOND);
			retCale.set(Calendar.YEAR,cale.get(Calendar.YEAR));
			retCale.set(Calendar.MONTH,cale.get(Calendar.MONTH));
			retCale.set(Calendar.DAY_OF_MONTH,cale.get(Calendar.DAY_OF_MONTH));
			retCale.set(Calendar.HOUR_OF_DAY,cale.get(Calendar.HOUR_OF_DAY));
			retCale.set(Calendar.MINUTE,cale.get(Calendar.MINUTE));
			retCale.set(Calendar.SECOND,cale.get(Calendar.SECOND));
			
			return cale.get(Calendar.ZONE_OFFSET) + cale.get(Calendar.DST_OFFSET) - retCale.get(Calendar.ZONE_OFFSET) - retCale.get(Calendar.DST_OFFSET);
		} catch (Exception e) {
			DebugUtil .commonDebugWarn( "Local User get PSK convert time zone catch IOException: ", e);
			return 0;
		}
	}
	
	@Override
	public Scheduler clone() {
		try {
			return (Scheduler) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	private boolean defaultFlag;
	
	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

}