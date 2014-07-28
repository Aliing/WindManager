package com.ah.bo.report.impl;

import java.util.TimeZone;

import com.ah.bo.performance.AhNewReport;
import com.ah.bo.report.AhAbstractReport;
import com.ah.bo.report.AhReportElement;
import com.ah.util.bo.report.AhReportRequest;

public abstract class AhAbstractNewReport extends AhAbstractReport {
	
	protected final String reportSummaryPlaceHolder = "";

	@Override
	public abstract void init();

	@Override
	protected abstract void doCalculate() throws Exception;
	
	private Long startTime = 0L, endTime = 0L;
	private int periodType;
	private TimeZone tz;
	private AhNewReport reportOptions;
	private Long domainId;
	private boolean blnGroupCal;
	
	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public int getPeriodType() {
		return periodType;
	}

	public void setPeriodType(int periodType) {
		this.periodType = periodType;
	}

	public TimeZone getTz() {
		return tz;
	}

	public void setTz(TimeZone tz) {
		this.tz = tz;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public AhNewReport getReportOptions() {
		return reportOptions;
	}

	public void setReportOptions(AhNewReport reportOptions) {
		this.reportOptions = reportOptions;
	}

	public boolean isBlnGroupCal() {
		return blnGroupCal;
	}
	
	public void setBlnGroupCal(boolean blnGroupCal) {
		this.blnGroupCal = blnGroupCal;
	}

	@Override
	public void addGroupReportEl(String subType) {
		AhReportElement reportElTmp = new AhReportElement(super.getId());
		reportElTmp.setStartTime(this.getStartTime());
		reportElTmp.setEndTime(this.getEndTime());
		reportElTmp.setGrpMark(subType);
		reportElTmp.setTz(this.getRequest().getTimeZone());
		reportElsMap.put(subType, reportElTmp);
	}
	
}
