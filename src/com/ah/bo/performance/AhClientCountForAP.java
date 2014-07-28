package com.ah.bo.performance;

import java.io.Serializable;
//import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.TimeZone;

import com.ah.util.datetime.AhDateTimeUtil;

public class AhClientCountForAP implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String	apName;
	
	private long	reportTime;
	
	private TimeZone tz;
	
	private int clientCount;
	
	private int aModeCount=0;
	
	private int bModeCount=0;
	
	private int gModeCount=0;
	
	private int naModeCount=0;
	
	private int ngModeCount=0;
	
	private int acModeCount=0;

	public int getAModeCount() {
		return aModeCount;
	}

	public int getBModeCount() {
		return bModeCount;
	}

	public int getGModeCount() {
		return gModeCount;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public void setAModeCount(int modeCount) {
		aModeCount = modeCount;
	}

	public void setBModeCount(int modeCount) {
		bModeCount = modeCount;
	}

	public void setGModeCount(int modeCount) {
		gModeCount = modeCount;
	}

	public long getReportTime() {
		return reportTime;
	}
	
	public String getReportTimeString() {
//		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		sf.setTimeZone(tz);
		return AhDateTimeUtil.getSpecifyDateTimeReport(reportTime,tz);
	}

	public void setReportTime(long reportTime) {
		this.reportTime = reportTime;
	}

	public int getClientCount() {
		return clientCount;
	}

	public void addClientCount() {
		this.clientCount = clientCount+1;
	}

	public void setClientCount(int clientCount) {
		this.clientCount = clientCount;
	}
	
	public void addGModeCount() {
		this.gModeCount = gModeCount+1;
	}
	public void addBModeCount() {
		this.bModeCount = bModeCount+1;
	}
	public void addAModeCount() {
		this.aModeCount = aModeCount+1;
	}
	
	public void addNAModeCount() {
		this.naModeCount = naModeCount+1;
	}
	
	public void addNGModeCount() {
		this.ngModeCount = ngModeCount+1;
	}
	
	public void addACModeCount() {
		this.acModeCount = acModeCount+1;
	}

	public int getNaModeCount() {
		return naModeCount;
	}

	public void setNaModeCount(int naModeCount) {
		this.naModeCount = naModeCount;
	}

	public int getNgModeCount() {
		return ngModeCount;
	}

	public void setNgModeCount(int ngModeCount) {
		this.ngModeCount = ngModeCount;
	}

	public TimeZone getTz() {
		return tz;
	}

	public void setTz(TimeZone tz) {
		this.tz = tz;
	}

	public int getAcModeCount() {
		return acModeCount;
	}

	public void setAcModeCount(int acModeCount) {
		this.acModeCount = acModeCount;
	}

}
