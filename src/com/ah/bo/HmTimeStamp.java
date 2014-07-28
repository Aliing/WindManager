/**
 * @filename			HmTimeStamp.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class HmTimeStamp implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	
	@Column(name = "TIME", nullable = false)
	private long time;
	
	@Column(name = "TIME_ZONE")
	private String timeZone;
	
	public static final HmTimeStamp	CURRENT_TIMESTAMP	= new HmTimeStamp(System
																.currentTimeMillis(), TimeZone
																.getDefault().getID());

	public static final HmTimeStamp	ZERO_TIMESTAMP		= new HmTimeStamp(0, null);
	
	public HmTimeStamp() {
		
	}
	
	public HmTimeStamp(long time, String timeZone) {
		this.time = time;
		this.timeZone = timeZone;
	}

	/**
	 * getter of time
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * setter of time
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * getter of timeZone
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * setter of timeZone
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	/**
	 * get HmTimeStamp instance by time value and offset to GMT
	 * 
	 * @param time		long value in milliseconds
	 * @param offset	offset to GMT 00 in hours(0-23)
	 * @return			HmTimeStamp instance
	 * 
	 * @author Joseph Chen
	 */
	public static HmTimeStamp getTimeStamp(long time, byte offset) {
		byte off = (byte)(offset % 24);
		
		return new HmTimeStamp(time, 
				"GMT" + (off > 0 
						? "+" + String.valueOf(off) 
								: String.valueOf(off)));
	}
	
	public static void main(String[] args) {
		HmTimeStamp stamp = HmTimeStamp.getTimeStamp(new Date().getTime(), (byte)-2);
		
		TimeZone zone = TimeZone.getTimeZone(stamp.getTimeZone());
		System.out.println(zone.getDisplayName());
		System.out.println(zone.getID());
		
	}

}
