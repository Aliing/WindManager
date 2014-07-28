/**
 * @filename			TrackingParameter.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.3
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * parameter for location tracking
 */
public class TrackingParameter {
	
	private int reportInterval;
	
	private short updateThreshold;
	
	private short validTime;
	
	/*
	 * if TrackingParameter object is included in the TrackingList,
	 * then this field is null.
	 * 
	 * this field is only used in modify-tracking message
	 */
	private List<Tracker> trackers = null;
	
	/**
	 * getter of trackers
	 * @return the trackers
	 */
	public List<Tracker> getTrackers() {
		return trackers;
	}

	/**
	 * setter of trackers
	 * @param trackers the trackers to set
	 */
	public void setTrackers(List<Tracker> trackers) {
		this.trackers = trackers;
	}

	public TrackingParameter() {
		
	}
	
	public TrackingParameter(int reportInterval,
								short updateThreshold,
								short validTime) {
		this.reportInterval = reportInterval;
		this.updateThreshold = updateThreshold;
		this.validTime = validTime;
	}

	/**
	 * getter of reportInterval
	 * @return the reportInterval
	 */
	public int getReportInterval() {
		return reportInterval;
	}

	/**
	 * setter of reportInterval
	 * @param reportInterval the reportInterval to set
	 */
	public void setReportInterval(int reportInterval) {
		this.reportInterval = reportInterval;
	}

	/**
	 * getter of updateThreshold
	 * @return the updateThreshold
	 */
	public short getUpdateThreshold() {
		return updateThreshold;
	}

	/**
	 * setter of updateThreshold
	 * @param updateThreshold the updateThreshold to set
	 */
	public void setUpdateThreshold(short updateThreshold) {
		this.updateThreshold = updateThreshold;
	}

	/**
	 * getter of validTime
	 * @return the validTime
	 */
	public short getValidTime() {
		return validTime;
	}

	/**
	 * setter of validTime
	 * @param validTime the validTime to set
	 */
	public void setValidTime(short validTime) {
		this.validTime = validTime;
	}
	
	/**
	 * add tracker as the receiver of tracking parameters
	 * 
	 * @param tracker
	 * @author Joseph Chen
	 */
	public void addTracker(Tracker tracker) {
		if(this.trackers == null) {
			this.trackers = new ArrayList<Tracker>();
		}
		
		this.trackers.add(tracker);
	}
	
	/**
	 * add trackers as the receiver of tracking parameters
	 * 
	 * @param tracker
	 * @author Joseph Chen
	 */
	public void addTrackers(Collection<Tracker> tracker) {
		if(this.trackers == null) {
			this.trackers = new ArrayList<Tracker>();
		}
		
		this.trackers.addAll(trackers);
	}

}
