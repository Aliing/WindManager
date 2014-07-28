/**
 * @filename			ReportDTO.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.3
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.location;

import java.util.Date;

/**
 * DTO for location report collected from HiveAP
 */
public class ReportDTO {
	private	String	reporter;
	
	private	int		channel;
	
	private	int		rssi;
	
	private	Date	time;
	
	public ReportDTO() {
		
	}
	
	public ReportDTO(String reporter,
						int channel,
						int rssi,
						Date time) {
		this.reporter = reporter;
		this.channel = channel;
		this.rssi = rssi;
		this.time = time;
	}

	/**
	 * getter of reporter
	 * @return the reporter
	 */
	public String getReporter() {
		return reporter;
	}

	/**
	 * setter of reporter
	 * @param reporter the reporter to set
	 */
	public void setReporter(String reporter) {
		this.reporter = reporter;
	}

	/**
	 * getter of channel
	 * @return the channel
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * setter of channel
	 * @param channel the channel to set
	 */
	public void setChannel(int channel) {
		this.channel = channel;
	}

	/**
	 * getter of rssi
	 * @return the rssi
	 */
	public int getRssi() {
		return rssi;
	}

	/**
	 * setter of rssi
	 * @param rssi the rssi to set
	 */
	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	/**
	 * getter of time
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * setter of time
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}
	
	
}
