/**
 *@filename		SingleTableItem.java
 *@version
 *@author		Fiona
 *@createtime	2007-12-17 PM 03:33:15
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.teacherView;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import com.ah.bo.HmBoBase;
import com.ah.util.MgrUtil;

/**
 * @author		fisher
 * @version		V1.0.0.0 
 */
@Embeddable
public class TvClassSchedule implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	
	@Column(length = HmBoBase.DEFAULT_STRING_LENGTH)
	private String	weekday;
	
	
	/**
	 * format "0111110", Sunday...Saturday
	 */
	@Column(length = 7)
	private String weekdaySec;
	
	private String	startTime;
	
	private String  endTime;
	
	@Column(length = 256)
	private String  room;

	public static final String MONDAY_TO_FRIDAY = "0111110";
	
	/**
	 * @return the weekday
	 */
	public String getWeekday() {
		return weekday;
	}

	/**
	 * @param weekday the weekday to set
	 */
	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the room
	 */
	public String getRoom() {
		return room;
	}

	/**
	 * @param room the room to set
	 */
	public void setRoom(String room) {
		this.room = room;
	}

	/**
	 * @return the weekdaySec
	 */
	public String getWeekdaySec() {
		return weekdaySec;
	}

	/**
	 * @param weekdaySec the weekdaySec to set
	 */
	public void setWeekdaySec(String weekdaySec) {
		this.weekdaySec = weekdaySec;
	}
	
	public String getWeekdaySecString(){
		if (weekdaySec.equals(MONDAY_TO_FRIDAY)){
			return MgrUtil.getUserMessage("config.tv.weekday.8");
		}
		StringBuffer tmpBuf = new StringBuffer();
		boolean addBefore=false;
		if (weekdaySec.charAt(0)=='1'){
			tmpBuf.append(MgrUtil.getUserMessage("config.tv.weekday.1"));
			addBefore=true;
		}
		if (weekdaySec.charAt(1)=='1'){
			if (addBefore) {tmpBuf.append(", ");}
			tmpBuf.append(MgrUtil.getUserMessage("config.tv.weekday.2"));
			addBefore=true;
		}
		if (weekdaySec.charAt(2)=='1'){
			if (addBefore) {tmpBuf.append(", ");}
			tmpBuf.append(MgrUtil.getUserMessage("config.tv.weekday.3"));
			addBefore=true;
		}
		if (weekdaySec.charAt(3)=='1'){
			if (addBefore) {tmpBuf.append(", ");}
			tmpBuf.append(MgrUtil.getUserMessage("config.tv.weekday.4"));
			addBefore=true;
		}
		if (weekdaySec.charAt(4)=='1'){
			if (addBefore) {tmpBuf.append(", ");}
			tmpBuf.append(MgrUtil.getUserMessage("config.tv.weekday.5"));
			addBefore=true;
		}
		if (weekdaySec.charAt(5)=='1'){
			if (addBefore) {tmpBuf.append(", ");}
			tmpBuf.append(MgrUtil.getUserMessage("config.tv.weekday.6"));
			addBefore=true;
		}
		if (weekdaySec.charAt(6)=='1'){
			if (addBefore) {tmpBuf.append(", ");}
			tmpBuf.append(MgrUtil.getUserMessage("config.tv.weekday.7"));
			addBefore=true;
		}
		return tmpBuf.toString(); 
	}

}
