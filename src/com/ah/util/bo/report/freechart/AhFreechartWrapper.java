package com.ah.util.bo.report.freechart;

import org.jfree.chart.JFreeChart;

import com.ah.bo.report.AhReportElement;

public class AhFreechartWrapper {
	private Long reportId;
	private String subType;
	private String summary;
	private String title;
	private JFreeChart freeChart;
	
	protected AhFreechartWrapper() {
	}
	
	protected AhFreechartWrapper(AhReportElement aReport) {
		if (aReport != null) {
			this.reportId = aReport.getId();
			this.subType = aReport.getGrpMark();
			this.summary = aReport.getSummary();
			this.title = aReport.getTitle();
		}
	}
	
	public AhFreechartWrapper(AhReportElement aReport, JFreeChart chart) {
		this(aReport);
		this.freeChart = chart;
	}
	
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	public JFreeChart getFreeChart() {
		return freeChart;
	}
	public void setFreeChart(JFreeChart freeChart) {
		this.freeChart = freeChart;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
