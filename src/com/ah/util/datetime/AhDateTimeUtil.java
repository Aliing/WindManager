package com.ah.util.datetime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.QueryUtil;

public class AhDateTimeUtil {

	private static String					arrWeekdays[]				= { "Sunday", "Monday",
			"Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"	};

	public static final String				DATA_FORMAT_yyyy_MM_dd		= "yyyy-MM-dd";

	/*
	 * default date and time format in HM
	 */
	public static final String				DEFAULT_DATE_TIME_FORMAT	= "MM-dd-yyyy HH:mm:ss";
	public static final String				DATA_FORMAT_MM_DD_YYYY_HH_MI= "MM/dd/yyyy HH:mm";
	public static final String				DATA_FORMAT_MM_DD_YYYY_HH_MI_AMPM= "MM/dd/yyyy hh:mm a";
	
	/**
	 * format for onetime password
	 */
	public static final String				CUSTOMER_DATE_TIME_FORMAT	= "dd/MM/yyyy";

	public static final SimpleDateFormat	DEFAULT_FORMATTER			= new SimpleDateFormat(
																				DEFAULT_DATE_TIME_FORMAT);

	public static final String				REPORT_DATE_TIME_FORMAT		= "yyyy-MM-dd HH:mm:ss";

	public static final SimpleDateFormat	REPORT_FORMATTER			= new SimpleDateFormat(
																				REPORT_DATE_TIME_FORMAT);
	public static final SimpleDateFormat   ORDERKEY_DATE_FORMAT         = new SimpleDateFormat("MM-dd-yyyy");
	
	/**
	 * format for onetime password
	 */
	public static final SimpleDateFormat	CUSTOMER_FORMATTER			= new SimpleDateFormat(
																				CUSTOMER_DATE_TIME_FORMAT);

	/**
	 * For a instance : getDatePart(new Date(),"y"); Parameters list
	 * 
	 * <pre>
	 * Parameter illuminate
	 * y	year
	 * m	month
	 * d	day
	 * dm	day of the month
	 * dy	day of the year
	 * h	hour
	 * M	minute
	 * s	second
	 * S	millisecond
	 * w	week
	 * wim	week in a month
	 * wm	week of the month
	 * wy	week of the year
	 * </pre>
	 * 
	 * @param dateValue -
	 * @param interval -
	 * @param timeZone -
	 * @return get the value corresponds on the date to be given
	 */
	public static int getDatePart(Date dateValue, String interval, TimeZone timeZone) {
		Calendar c = Calendar.getInstance();
		c.setTimeZone(timeZone);
		c.setTime(dateValue);

		if (interval.equalsIgnoreCase("y")) {
			return c.get(Calendar.YEAR);
		}
		if (interval.equals("M")) {
			return c.get(Calendar.MONTH);
		}
		if (interval.equalsIgnoreCase("d")) {
			return c.get(Calendar.DATE);
		}
		if (interval.equalsIgnoreCase("dm")) {
			return c.get(Calendar.DAY_OF_MONTH);
		}
		if (interval.equalsIgnoreCase("dy")) {
			return c.get(Calendar.DAY_OF_YEAR);
		}
		if (interval.equals("h")) {
			return c.get(Calendar.HOUR); // 12hours
		}
		if (interval.equals("H")) {
			return c.get(Calendar.HOUR_OF_DAY); // 24hours
		}
		if (interval.equals("m")) {
			return c.get(Calendar.MINUTE);
		}
		if (interval.equals("s")) {
			return c.get(Calendar.SECOND);
		}
		if (interval.equals("S")) {
			return c.get(Calendar.MILLISECOND);
		}
		if (interval.equalsIgnoreCase("w")) {
			return c.get(Calendar.DAY_OF_WEEK);
		}
		if (interval.equalsIgnoreCase("wim")) {
			return c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
		}
		if (interval.equalsIgnoreCase("wm")) {
			return c.get(Calendar.WEEK_OF_MONTH);
		}
		if (interval.equalsIgnoreCase("wy")) {
			return c.get(Calendar.WEEK_OF_YEAR);
		}

		return -1;
	}

	/**
	 * For a instance : getDatePart("2003-10-11", "yyyy-MM-dd", "M");
	 * 
	 * @param strDateValue -
	 * @param pattern -
	 * @param interval -
	 * @return date correspond the paramater
	 * @throws ParseException -
	 */
	public static int getDatePart(String strDateValue, String pattern, String interval,
			TimeZone timeZone) throws ParseException {
		SimpleDateFormat sdfFormat = new SimpleDateFormat(pattern);
		sdfFormat.setTimeZone(timeZone);
		Date dateValue = sdfFormat.parse(strDateValue);

		return getDatePart(dateValue, interval, timeZone);
	}

	/**
	 * For a instance : getDateAdd(new Date(),1,"y");
	 * 
	 * @param dateValue -
	 * @param intDiff -
	 * @param interval -
	 * @return difference between the current date and the given
	 */
	public static Date getDateAdd(Date dateValue, int intDiff, String interval) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateValue);

		if (interval.equalsIgnoreCase("y")) {
			c.add(Calendar.YEAR, intDiff);
		}
		if (interval.equals("M")) {
			c.add(Calendar.MONTH, intDiff);
		}
		if (interval.equalsIgnoreCase("d")) {
			c.add(Calendar.DATE, intDiff);
		}
		if (interval.equalsIgnoreCase("h")) {
			c.add(Calendar.HOUR, intDiff);
		}
		if (interval.equals("m")) {
			c.add(Calendar.MINUTE, intDiff);
		}
		if (interval.equals("s")) {
			c.add(Calendar.SECOND, intDiff);
		}
		if (interval.equals("S")) {
			c.add(Calendar.MILLISECOND, intDiff);
		}

		return c.getTime();
	}

	/**
	 * @return last month
	 */
	public static String getLastMonth(TimeZone timeZone) {
		return getLastMonth(new Date(), timeZone);
	}

	/**
	 * For a instance : getLastMonth(new Date());
	 * 
	 * @param dateValue -
	 * @return the last month of the month to be given
	 */
	public static String getLastMonth(Date dateValue, TimeZone timeZone) {
		Calendar c = Calendar.getInstance();
		c.setTimeZone(timeZone);
		c.setTime(dateValue);
		c.add(Calendar.MONTH, 0);

		DateFormat formatter = new SimpleDateFormat("yyyyMM");
		formatter.setTimeZone(timeZone);

		return formatter.format(c.getTime());
	}

	/**
	 * get string representation of current time in specific pattern and time zone
	 * 
	 * @param pattern
	 *            the pattern of string representation
	 * @param timeZone
	 *            the given time zone
	 * @return string of date or datetime
	 * 
	 * @author Joseph Chen
	 */
	public static String getCurrentDate(String pattern, TimeZone timeZone) {
		return getFormatDateTime(new Date(), pattern, timeZone);
	}

	/**
	 * get string representation of current time in specific pattern and in default time zone
	 * 
	 * @param pattern
	 *            the pattern of string representation
	 * @return string of date or datetime
	 * 
	 * @author Joseph Chen
	 */
	public static String getCurrentDate(String pattern) {
		return getCurrentDate(pattern, TimeZone.getDefault());
	}

	/**
	 * get string representation of current date with default pattern in specific time zone
	 * 
	 * @param timeZone
	 *            the given time zone
	 * @return string of date
	 * 
	 * @author Joseph Chen
	 */
	public static String getCurrentDate(TimeZone timeZone) {
		return getCurrentDate(DATA_FORMAT_yyyy_MM_dd, timeZone);
	}

	/**
	 * get string representation of current date with default pattern in default time zone
	 * 
	 * @return string of date
	 * 
	 * @author Joseph Chen
	 */
	public static String getCurrentDate() {
		return getCurrentDate(TimeZone.getDefault());
	}

	/**
	 * get string representation of current datetime with default pattern in specific time zone
	 * 
	 * @param timeZone
	 *            the given time zone
	 * @return string of datetime
	 * 
	 * @author Joseph Chen
	 */
	public static String getCurrentDateTime(TimeZone timeZone) {
		return getCurrentDate(DEFAULT_DATE_TIME_FORMAT, timeZone);
	}

	/**
	 * get string representation of current datetime with default pattern in default time zone
	 * 
	 * @param timeZone
	 *            the given time zone
	 * @return string of datetime
	 * 
	 * @author Joseph Chen
	 */
	public static String getCurrentDateTime() {
		return getCurrentDateTime(TimeZone.getDefault());
	}

	/**
	 * get string representation of datetime from milliseconds in default time zone
	 * 
	 * @param value
	 *            long value of milliseconds
	 * @return string representation of datetime
	 * 
	 * @author Joseph Chen
	 */
	public static String getSpecifyDateTime(long value) {
		return getSpecifyDateTime(value, TimeZone.getDefault());
	}
	
	public static String getSpecifyDateTime(long value, HmDomain owner) {
		return getSpecifyDateTime(value, TimeZone.getDefault(), owner);
	}

	/**
	 * get string representation of datetime from milliseconds in specific time zone
	 * 
	 * @param value
	 *            long value of milliseconds
	 * @param timeZone
	 *            the given time zone
	 * @return string representation of datetime
	 * 
	 * @author Joseph Chen
	 */
	public static String getSpecifyDateTime(long value, TimeZone timeZone) {
		return getFormatDateTime(new Date(value), timeZone);
	}
	
	/**
	 * get string representation of datetime from milliseconds in specific time zone
	 * 
	 * @param value
	 *            long value of milliseconds
	 * @param timeZone
	 *            the given time zone
	 * @param domainId
	 *            VHM owner Id
	 * @return string representation of datetime
	 * 
	 */
	public static String getSpecifyDateTime(long value, TimeZone timeZone, HmDomain owner) {
		return getFormatDateTime(new Date(value), timeZone, owner);
	}

	/**
	 * get string representation of datetime from milliseconds in specific time zone
	 * 
	 * @param value
	 *            long value of milliseconds
	 * @param timeZone
	 *            the given time zone
	 * @return string representation of datetime
	 * 
	 * @author Joseph Chen
	 */
	public static String getSpecifyDateTimeReport(long value, TimeZone timeZone) {
		return getFormatDateTimeReport(new Date(value), timeZone);
	}

	/**
	 * get string representation of datetime from date in specific time zone
	 * 
	 * @param value
	 *            date
	 * @param timeZone
	 *            the given time zone
	 * @param domainId
	 *            VHM owner Id
	 * @return string representation of datetime
	 * 
	 */
	public static String getSpecifyDateTime(Date value, TimeZone timeZone,HmDomain owner) {
		return getFormatDateTime(value, timeZone, owner);
	}
	
	/**
	 * get string representation of datetime from date in specific time zone
	 * 
	 * @param value
	 *            date
	 * @param timeZone
	 *            the given time zone
	 * @return string representation of datetime
	 * 
	 * @author Joseph Chen
	 */
	public static String getSpecifyDateTime(Date value, TimeZone timeZone) {
		return getFormatDateTime(value, timeZone);
	}
	
	/**
	 * get string representation of datetime from date in specific time zone
	 * 
	 * @param value
	 *            date
	 * @param owner
	 *            vhm owner
	 * @return string representation of datetime
	 * 
	 */
	public static String getSpecifyDateTime(Date value,HmDomain owner) {
		return getFormatDateTime(value, owner);
	}

	/**
	 * get date from milliseconds in specific time zone
	 * 
	 * @param value
	 *            long value of milliseconds
	 * @param timeZone
	 *            the given time zone
	 * @return date
	 * 
	 * @author Joseph Chen
	 */
	public static Date getDateTime(long value, TimeZone timeZone) {
		Calendar c = Calendar.getInstance();
		c.setTimeZone(timeZone);
		c.setTimeInMillis(value);

		return c.getTime();
	}

	/**
	 * get date from milliseconds in default time zone
	 * 
	 * @param value
	 *            long value of milliseconds
	 * @return date
	 * 
	 * @author Joseph Chen
	 */
	public static Date getDateTime(long value) {
		return getDateTime(value, TimeZone.getDefault());
	}

	/**
	 * get day in a week of current date in specific time zone
	 * 
	 * @param timeZone
	 * @return
	 * @author Joseph Chen
	 */
	public static String getCurrentWeekDay(TimeZone timeZone) {
		return arrWeekdays[getDatePart(new Date(), "w", timeZone) - 1];
	}

	/**
	 * get the day in a week from a specific date in specific time zone
	 * 
	 * @param dateValue
	 * @param timeZone
	 * @return
	 * @author Joseph Chen
	 */
	public static String getWeekDay(Date dateValue, TimeZone timeZone) {
		return arrWeekdays[getDatePart(dateValue, "w", timeZone) - 1];
	}

	/**
	 * get week day from a date string with specific pattern in specific time zone
	 * 
	 * @param dateValue
	 * @param pattern
	 * @param timeZone
	 * @return
	 * @throws ParseException
	 * @author Joseph Chen
	 */
	public static String getWeekDay(String dateValue, String pattern, TimeZone timeZone)
			throws ParseException {
		return arrWeekdays[getDatePart(dateValue, pattern, "w", timeZone) - 1];
	}

	/**
	 * get string representation from a given date with specific pattern and timeZone
	 * 
	 * @param dateValue
	 * @param strFormat
	 * @param timeZone
	 * @return
	 * @author Joseph Chen
	 */
	public static String getFormatDateTime(Date dateValue, String strFormat, TimeZone timeZone) {
		DateFormat formatter = new SimpleDateFormat(strFormat);
		formatter.setTimeZone(timeZone);
		return formatter.format(dateValue);
	}

  	public static String getFormatDateTime(long longValue, String strFormat)
	{
		return new SimpleDateFormat(strFormat).format(longValue);
	}

	/**
	 * get string representation in specific format from given time stamp
	 * 
	 * @param timeStamp
	 *            source time stamp which is of HmTimeStamp
	 * @param format
	 *            the givne format
	 * 
	 * @return string representation of time
	 * 
	 * @author Joseph Chen
	 */
	public static String getFormattedDateTime(HmTimeStamp timeStamp, String format) {
		if (timeStamp == null || format == null) {
			return null;
		}

		return getFormatDateTime(new Date(timeStamp.getTime()), format, TimeZone
				.getTimeZone(timeStamp.getTimeZone()));
	}
	
	
	/**
	 * get string representation in specific format from given time stamp
	 * 
	 * @param timeStamp
	 *            source time stamp which is of HmTimeStamp
	 * @param format
	 *            the givne format
	 * 
	 * @return string representation of time
	 * 
	 * @author Joseph Chen
	 */
	public static String getFormattedDateTime(HmTimeStamp timeStamp, HmDomain owner) {
		if (timeStamp == null || owner == null) {
			return null;
		}

		return getFormatDateTime(new Date(timeStamp.getTime()), TimeZone
				.getTimeZone(timeStamp.getTimeZone()), owner);
	}

	/**
	 * get string representation in default format from given time stamp
	 * 
	 * @param timeStamp
	 *            source time stamp which is of HmTimeStamp
	 * @return string representation of time
	 * 
	 * @author Joseph Chen
	 */
	public static String getFormattedDateTime(HmTimeStamp timeStamp) {
		if (timeStamp == null) {
			return null;
		}

		return getFormattedDateTime(timeStamp, DEFAULT_DATE_TIME_FORMAT);
	}

	/**
	 * get string representation in default format from given time stamp
	 * 
	 * @param timeStamp
	 *            source time stamp which is of HmTimeStamp
	 * @return string representation of time
	 * 
	 * @author Joseph Chen
	 */
	public static String getFormattedDateTimeReport(HmTimeStamp timeStamp) {
		if (timeStamp == null) {
			return null;
		}

		return getFormattedDateTime(timeStamp, REPORT_DATE_TIME_FORMAT);
	}

	/**
	 * get string representation in specific format from given time stamp and offset to GMT 00
	 * 
	 * @param time
	 *            long value of millisecond
	 * @param timeZoneOffset
	 *            offset to GMT 00
	 * @param format
	 *            the given format
	 * 
	 * @return string representation of time
	 * 
	 * @author Joseph Chen
	 */
	public static String getFormattedDateTime(long time, int timeZoneOffset, String format) {
		if (format == null) {
			return null;
		}

		byte offset = (byte) (timeZoneOffset % 24);
		String zoneID = "GMT"
				+ (offset > 0 ? "+" + String.valueOf(offset) : String.valueOf(offset));

		return getFormatDateTime(new Date(time), format, TimeZone.getTimeZone(zoneID));
	}

	/**
	 * For a instance : getFormatDateTime("20021011", "yyyyMMdd", "yyyy-MM-dd");
	 * 
	 * @param strDateValue -
	 * @param strFormat1 -
	 * @param strFormat2 -
	 * @return change date into another pattern
	 * @throws ParseException -
	 */
	public static String getFormatDateTime(String strDateValue, String strFormat1,
			String strFormat2, TimeZone timeZone) throws ParseException {
		DateFormat formatter = new SimpleDateFormat(strFormat1);
		formatter.setTimeZone(timeZone);

		return getFormatDateTime(formatter.parse(strDateValue), strFormat2, timeZone);
	}

	/**
	 * @param strDate -
	 * @return change string to date
	 * @throws java.text.ParseException -
	 */
	public static Date changeStringToDate(String strDate) throws ParseException {
		if (strDate == null || strDate.equals("")) {
			return null;
		}

		return DateFormat.getDateInstance(DateFormat.MEDIUM).parse(strDate);
	}

	/**
	 * For a instance : getDateDiff(date1, date2);
	 * 
	 * @param date1 -
	 * @param date2 -
	 * @return get the difference between two dates
	 */
	public static int getDateDiff(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);
		Long lngDiff = (c1.getTime().getTime() - c2.getTime().getTime()) / (24 * 3600 * 1000);
		return (lngDiff.intValue());
	}

	/**
	 * For a instance : getDateDiff(new Date(),"2003-10-1","yyyy-MM-dd");
	 * 
	 * @param date1 -
	 * @param date2 -
	 * @param pattern -
	 * @return get the number of day between two dates
	 * @throws ParseException -
	 */
	public static int getDateDiff(Date date1, String date2, String pattern) throws ParseException {
		SimpleDateFormat sdfFormat = new SimpleDateFormat(pattern);
		return getDateDiff(date1, sdfFormat.parse(date2));
	}

	/**
	 * For a instance : getDateDiff("2003-10-1", "2003-9-1", "yyyy-MM-dd");
	 * 
	 * @param date1 -
	 * @param date2 -
	 * @param pattern -
	 * @return get the number of day between two dates
	 * @throws ParseException -
	 */
	public static int getDateDiff(String date1, String date2, String pattern) throws ParseException {
		SimpleDateFormat sdfFormat = new SimpleDateFormat(pattern);
		return getDateDiff(sdfFormat.parse(date1), sdfFormat.parse(date2));
	}

	/**
	 * For a instance : getDateDiff("2003/10/1", "yyyy/MM/dd", "2003-09-01", "yyyy-MM-dd");
	 * 
	 * @param date1 -
	 * @param pattern1 -
	 * @param date2 -
	 * @param pattern2 -
	 * @return get the number of day between two dates
	 * @throws ParseException -
	 */
	public static int getDateDiff(String date1, String pattern1, String date2, String pattern2)
			throws ParseException {
		SimpleDateFormat sdfFormat1 = new SimpleDateFormat(pattern1);
		SimpleDateFormat sdfFormat2 = new SimpleDateFormat(pattern2);
		return getDateDiff(sdfFormat1.parse(date1), sdfFormat2.parse(date2));
	}

	/**
	 * For a instance : getDateAfer(10,"yyyy-MM-dd")
	 * 
	 * @param inter -
	 * @param format -
	 * @return get the day before or after today
	 */
	public static String getDateAfter(long inter, String format, TimeZone timeZone) {
		Date d1 = new Date();
		d1.setTime(d1.getTime() + inter * 24 * 3600 * 1000);
		return getFormatDateTime(d1, format, timeZone);
	}

	public static Date getDateAfter(long inter) {
		Date d1 = new Date();
		d1.setTime(d1.getTime() + inter * 24 * 3600 * 1000);
		return d1;
	}

	public static long getDateAfter2(long inter) {
		return System.currentTimeMillis() + inter * 24 * 3600 * 1000;
	}

	public static String getDateAfter(String date1, String date1format, long inter, String format,
			TimeZone timeZone) throws ParseException {
		SimpleDateFormat sdfFormat = new SimpleDateFormat(date1format);
		sdfFormat.setTimeZone(timeZone);
		Date dateValue = sdfFormat.parse(date1);
		dateValue.setTime(dateValue.getTime() + inter * 24 * 3600 * 1000);
		return getFormatDateTime(dateValue, format, timeZone);
	}

	public static long changeDateStringToLong(String s_date) {
		DateFormat format = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
		Date date;
		long l = System.currentTimeMillis();
		try {
			date = format.parse(s_date);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			l = c.getTimeInMillis();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}

	public static long changeDateStringToLong(String s_date, String s_format) {
		DateFormat format = new SimpleDateFormat(s_format);
		Date date;
		long l = System.currentTimeMillis();
		try {
			date = format.parse(s_date);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			l = c.getTimeInMillis();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}

	/**
	 * get time representation with default format
	 * 
	 * @param value
	 *            Date value of the time
	 * @param timeZone
	 *            ID of time zone
	 * @return String representation of time
	 * 
	 * @author Joseph Chen
	 */
	private static String getFormatDateTime(Date value, TimeZone timeZone) {

		DEFAULT_FORMATTER.setTimeZone(timeZone);
		return DEFAULT_FORMATTER.format(value);
	}
	
	/**
	 * get time representation with default format
	 * 
	 * @param value
	 *            Date value of the time
	 * @param timeZone
	 *            ID of time zone
	 * @param domainId
	 *            VHM owner Id
	 * @return String representation of time
	 * 
	 */
	public static String getFormatDateTime(Date value, TimeZone timeZone, HmDomain owner) {
//		HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", owner);
		HMServicesSettings bo = CacheMgmt.getInstance().getHMServiceSettingForTimeZoneByDomain(owner);
		String dateTimeFormatString = "MM/dd/yyyy hh:mm:ss a";
		if(bo.getTimeType() == HMServicesSettings.TIME_TYPE_1){
			if(bo.getDateFormat() == HMServicesSettings.DATE_FORMAT_TYPE_1){
				if(bo.getDateSeparator() == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_2;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_2;
				}
			}else{
				if(bo.getDateSeparator() == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_2;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_2;
				}
			}
		}else {
			if(bo.getDateFormat() == HMServicesSettings.DATE_FORMAT_TYPE_1){
				if(bo.getDateSeparator() == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_1;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_1;
				}
			}else{
				if(bo.getDateSeparator() == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_1;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_1;
				}
			}
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimeFormatString);

		simpleDateFormat.setTimeZone(timeZone);
		return simpleDateFormat.format(value);
	}
	
	public static String getFormatDateTime(Date value, HmDomain owner){
//		HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", owner);
		HMServicesSettings bo = CacheMgmt.getInstance().getHMServiceSettingForTimeZoneByDomain(owner);
		String dateTimeFormatString = "";
		if(bo.getTimeType() == HMServicesSettings.TIME_TYPE_1){
			if(bo.getDateFormat() == HMServicesSettings.DATE_FORMAT_TYPE_1){
				if(bo.getDateSeparator() == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_2;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_2;
				}
			}else{
				if(bo.getDateSeparator() == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_2;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_2;
				}
			}
		}else {
			if(bo.getDateFormat() == HMServicesSettings.DATE_FORMAT_TYPE_1){
				if(bo.getDateSeparator() == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_1;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_1;
				}
			}else{
				if(bo.getDateSeparator() == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_1;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_1;
				}
			}
		}
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimeFormatString);
		simpleDateFormat.setTimeZone(owner.getTimeZone());
		return simpleDateFormat.format(value);
	}

	/**
	 * get time representation with default format
	 * 
	 * @param value
	 *            Date value of the time
	 * @param timeZone
	 *            ID of time zone
	 * @return String representation of time
	 * 
	 * @author Joseph Chen
	 */
	private static String getFormatDateTimeReport(Date value, TimeZone timeZone) {

		REPORT_FORMATTER.setTimeZone(timeZone);
		return REPORT_FORMATTER.format(value);
	}
	
	public static String getDateStrFromLong(long dateLong) {
		return getDateStrFromLong(dateLong, ORDERKEY_DATE_FORMAT);
	}
	
	public static String getDateStrFromLong(long dateLong, SimpleDateFormat sdFormat) {
		sdFormat.setTimeZone(TimeZone.getTimeZone(HmBeOsUtil.getServerTimeZone()));
		return sdFormat.format(new Date(dateLong));
	}
	
	public static String getDateStrFromLong(long dateLong, SimpleDateFormat sdFormat, String timeZone) {
		sdFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		return sdFormat.format(new Date(dateLong));
	}
	
	public static String getDateStrFromLongNoTimeZone(long dateLong) {
		return getDateStrFromLong(dateLong, ORDERKEY_DATE_FORMAT, "GMT");
	}
	
	public static String getDateDiffDhm(String startDate, String endDate,
			String startDateFormat, String endDateFormat, String resultFormat) {
		SimpleDateFormat sdStart = new SimpleDateFormat(startDateFormat);
		SimpleDateFormat sdEnd = new SimpleDateFormat(endDateFormat);
		try {
			return getDateDiffDhm(sdStart.parse(startDate).getTime(), sdEnd.parse(endDate).getTime(), resultFormat);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getDateDiffFromNow(Long endDate, String resultFormat) {
		return getDateDiffDhm(System.currentTimeMillis(), endDate, resultFormat);
	}
	
	public static final String DIFF_FORMAT_DAY_HOUR_MIN_SEC = "dhms";
	public static final String DIFF_FORMAT_DAY_HOUR_MIN = "dhm";
	public static final String DIFF_FORMAT_HOUR_MIN_SEC = "hms";
	public static final String DIFF_FORMAT_HOUR_MIN = "hm";
	
	/**
	 * 
	 * return value example: 
	 * if startDate = endDate, return '0 day 0 hour 0 minute 0 second'
	 * else return '1 day 1 hour 1 minute 1 second' or '2 days 2 hours 2 minutes 2 seconds'
	 * 
	 * @param startDate
	 * @param endDate
	 * @param resultFormat
	 * @return
	 */
	public static String getDateDiffDhm(Long startDate, Long endDate, String resultFormat) {
		
		long msDay 				= 24 * 60 * 60 * 1000;
		long msHour 			= 60 * 60 * 1000;
		long msMin 				= 60 * 1000;
		long msSec 				= 1000;
		long day				= 0;
		long hour				= 0;
		long min				= 0;
		long sec				= 0;
		long diff				= Math.abs(endDate - startDate);
		String dayUnit			= "Day";
		String hourUnit			= "Hour";
		String minUnit			= "Minute";
		String secUnit			= "Second";
		StringBuffer resultStr	= new StringBuffer();
		
		if (diff != 0) {
			day  = diff / msDay;
			hour = diff % msDay / msHour + day * 24;
			min  = diff % msDay % msHour / msMin + hour * 60;
			sec  = diff % msDay % msHour % msMin / msSec + min * 60;
		}
		
		if (DIFF_FORMAT_DAY_HOUR_MIN_SEC.endsWith(resultFormat)) {
			resultStr.append(addUnitForTime(day, dayUnit));
			resultStr.append(addUnitForTime(hour > 0 ? hour - day * 24 : hour, hourUnit));
			resultStr.append(addUnitForTime(min > 0 ? min - hour * 60 : min, minUnit));
			resultStr.append(addUnitForTime(sec > 0 ? sec - min * 60 : sec, secUnit));
		} else if (DIFF_FORMAT_DAY_HOUR_MIN.endsWith(resultFormat)) {
			resultStr.append(addUnitForTime(day, dayUnit));
			resultStr.append(addUnitForTime(hour > 0 ? hour - day * 24 : hour, hourUnit));
			resultStr.append(addUnitForTime(min > 0 ? min - hour * 60 : min, minUnit));
		} else if (DIFF_FORMAT_HOUR_MIN_SEC.endsWith(resultFormat)) {
			resultStr.append(addUnitForTime(hour, hourUnit));
			resultStr.append(addUnitForTime(min > 0 ? min - hour * 60 : min, minUnit));
			resultStr.append(addUnitForTime(sec > 0 ? sec - min * 60 : sec, secUnit));
		} else if (DIFF_FORMAT_HOUR_MIN.endsWith(resultFormat)) {
			resultStr.append(addUnitForTime(hour, hourUnit));
			resultStr.append(addUnitForTime(min > 0 ? min - hour * 60 : min, minUnit));
		}
		
		return resultStr.toString();
	}
	
	public static String addUnitForTime(long time, String unit) {
		if (time <= 1) {
			return " " + time + " " + unit;
		} else {
			return " " + time + " " + unit + "s";
		}
	}

}