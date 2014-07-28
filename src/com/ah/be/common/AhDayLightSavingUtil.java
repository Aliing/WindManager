/**
 *@filename		AhDayLightSavingUtil.java
 *@version
 *@author		Fiona
 *@createtime	Jul 30, 2007 7:31:47 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.common;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeOsUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class AhDayLightSavingUtil implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean isUseDayLightSaving = false;
	private String startDLSdate;
	private String startDLStime;
	private String endDLSdate;
	private String endDLStime;

	public boolean isUseDayLightSaving() {
		return isUseDayLightSaving;
	}

	public void setUseDayLightSaving(boolean isUseDayLightSaving) {
		this.isUseDayLightSaving = isUseDayLightSaving;
	}

	public String getStartDLSdate() {
		return startDLSdate;
	}

	public void setStartDLSdate(String startDLSdate) {
		this.startDLSdate = startDLSdate;
	}

	public String getStartDLStime() {
		return startDLStime;
	}

	public void setStartDLStime(String startDLStime) {
		this.startDLStime = startDLStime;
	}

	public String getEndDLSdate() {
		return endDLSdate;
	}

	public void setEndDLSdate(String endDLSdate) {
		this.endDLSdate = endDLSdate;
	}

	public String getEndDLStime() {
		return endDLStime;
	}

	public void setEndDLStime(String endDLStime) {
		this.endDLStime = endDLStime;
	}

	public AhDayLightSavingUtil(String arg_TimeZone) {
		try {
			String str_TimeZone = "America/Los_Angeles";
			if (!("").equals(arg_TimeZone) && arg_TimeZone.contains(" "))
				str_TimeZone = arg_TimeZone
						.substring(arg_TimeZone.indexOf(" "));
			TimeZoneInfo tz = new TimeZoneInfo(str_TimeZone.trim());

			if (tz.useDaylightTime()) {
				Calendar rightnow = Calendar.getInstance();
				rightnow.setTimeZone(tz);
				Calendar rightUtc = Calendar.getInstance();
				rightUtc.setTimeZone(tz);

				int j = 0;
				for (int i = 0; i < tz.transTimes.length; ++i) {
					if (j == 2) {
						break;
					}
					int t = tz.transTimes[i];
					
					rightnow.setTime(new Date((t - 1) * 1000L));
					rightUtc.setTime(new Date(t * 1000L));
					if (HmBeOsUtil.getServerTime().get(Calendar.YEAR) == rightnow
							.get(Calendar.YEAR)) {
						j++;
						String MM = getFixedLengthString(String
								.valueOf(rightnow.get(Calendar.MONTH) + 1),
								true, 2, '0');
						String DD = getFixedLengthString(String
								.valueOf(rightnow.get(Calendar.DAY_OF_MONTH)),
								true, 2, '0');

						String hh = getFixedLengthString(String
								.valueOf(rightnow.get(Calendar.HOUR_OF_DAY)),
								true, 2, '0');
						String mm = getFixedLengthString(String
								.valueOf(rightnow.get(Calendar.MINUTE)), true,
								2, '0');
						String ss = getFixedLengthString(String
								.valueOf(rightnow.get(Calendar.SECOND)), true,
								2, '0');

						int newHour = rightUtc.get(Calendar.HOUR_OF_DAY);
						int oldHour = rightnow.get(Calendar.HOUR_OF_DAY);
						if (newHour == oldHour) {
							this.setEndDLSdate(MM + "-" + DD);
							this.setEndDLStime(hh + ":" + mm + ":" + ss);
						} else {
							// some time zone newHour always not equal oldHour
							// (GMT-03:30) America/St_Johns
							// (GMT-03:30) Canada/Newfoundland
							if (null != this.getStartDLSdate()) {
								this.setEndDLSdate(MM + "-" + DD);
								this.setEndDLStime(hh + ":" + mm + ":" + ss);
							} else {
								this.setStartDLSdate(MM + "-" + DD);
								this.setStartDLStime(hh + ":" + mm + ":" + ss);
							}
						}
						this.setUseDayLightSaving(true);
					}
				}
			}
		} catch (IOException e) {
			DebugUtil
					.commonDebugWarn(
							"AhDayLightSavingUtil.AhDayLightSavingUtil() catch IOException: ",
							e);
		}
	}

	/**
	 * Add one ore more fixed chars before or behind the input string to a fixed
	 * length.
	 * 
	 * @param arg_initial :
	 *            the initial string; arg_Before : true is add the char before
	 *            the initial string,false is add the char behind the initial
	 *            string; arg_Length : the fixed length; arg_Add : the add char
	 * @param arg_Before -
	 * @param arg_Length -
	 * @param arg_Add -
	 * @return the fixed length string, if the input is wrong return null, if
	 *         the initial string's length longer than the fixed length return
	 *         null
	 */
	public static String getFixedLengthString(String arg_initial,
			boolean arg_Before, int arg_Length, char arg_Add) {
		if (arg_initial == null || arg_initial.trim().equals("")
				|| arg_Add == ' ')
			return null;
		int int_Length = arg_initial.length();
		if (int_Length < arg_Length) {
			StringBuffer str = new StringBuffer();
			for (int i = 0; i < (arg_Length - int_Length); i++)
				str.append(arg_Add);
			if (arg_Before)
				arg_initial = str.toString() + arg_initial;
			else
				arg_initial = arg_initial + str.toString();
		} else if (int_Length > arg_Length)
			return null;
		return arg_initial;
	}
	
	public String getDayLightTime(){
		return getStartDLSdate() + " "
				+ getStartDLStime() + " "
				+ getEndDLSdate() + " "
				+ getEndDLStime();
	}
}