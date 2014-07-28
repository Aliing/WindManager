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

import javax.persistence.Embeddable;


/**
 * @author		fisher
 * @version		V1.0.0.0 
 */
@Embeddable
public class TvScheduleMapPeriodTime implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	
	private int 	section;
	
	private String	startTime;
	
	private String  endTime;

	/**
	 * @return the section
	 */
	public int getSection() {
		return section;
	}

	/**
	 * @param section the section to set
	 */
	public void setSection(int section) {
		this.section = section;
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
	
	public String getSHour(){
		if (startTime!=null && startTime.length()==5) {
			return startTime.substring(0, 2);
		}
		return "00";
	}
	public String getSMin(){
		if (startTime!=null && startTime.length()==5) {
			return startTime.substring(3, 5);
		}
		return "00";
	}
	public String getEHour(){
		if (endTime!=null && endTime.length()==5) {
			return endTime.substring(0, 2);
		}
		return "00";
	}
	public String getEMin(){
		if (endTime!=null && endTime.length()==5) {
			return endTime.substring(3, 5);
		}
		return "00";
	}

}
