package com.ah.util.bo.dashboard;

import com.ah.bo.report.AhReportElement;
import com.ah.util.bo.report.freechart.AhFreechartWrapper;

public class DaExportedSingleData {
	private AhFreechartWrapper freechartWrapper;
	private AhReportElement reportEl;
	private DataType dataType;
	
	private String chartType;
	private boolean chartInverted;
	
	private int column;
	private int order;
	
	private String axisKey;
	// this key is for used inner to mark unique metric
	private int uuKey;
	
	private enum DataType {
		JFREECHART, RAW_DATA, NONE
	};
	
	public DaExportedSingleData() {
		this.dataType = DataType.NONE;
	}
	
	public DaExportedSingleData(AhFreechartWrapper freechartWrapper) {
		this.freechartWrapper = freechartWrapper;
		this.dataType = DataType.JFREECHART;
	}
	
	public DaExportedSingleData(AhReportElement reportEl) {
		this.reportEl = reportEl;
		this.dataType = DataType.RAW_DATA;
	}
	
	public String getTitle() {
		if (!this.isNull()) {
			if (this.isExportedAsJFreechart()) {
				return this.freechartWrapper.getTitle();
			}
			if (this.isExportedAsRawData()) {
				return this.reportEl.getTitle();
			}
		}
		
		return "";
	}
	
	public boolean isNull() {
		return this.dataType == DataType.NONE;
	}
	
	public boolean isExportedAsJFreechart() {
		return this.dataType == DataType.JFREECHART;
	}
	
	public boolean isExportedAsRawData() {
		return this.dataType == DataType.RAW_DATA;
	}

	public AhFreechartWrapper getFreechartWrapper() {
		return freechartWrapper;
	}

	public AhReportElement getReportEl() {
		return reportEl;
	}

	public int getColumn() {
		return column;
	}

	public DaExportedSingleData setColumn(int column) {
		this.column = column;
		return this;
	}

	public int getOrder() {
		return order;
	}

	public DaExportedSingleData setOrder(int order) {
		this.order = order;
		return this;
	}

	public String getChartType() {
		return chartType;
	}

	public DaExportedSingleData setChartType(String chartType) {
		this.chartType = chartType;
		return this;
	}

	public boolean isChartInverted() {
		return chartInverted;
	}

	public DaExportedSingleData setChartInverted(boolean chartInverted) {
		this.chartInverted = chartInverted;
		return this;
	}

	public String getAxisKey() {
		return axisKey;
	}

	public DaExportedSingleData setAxisKey(String axisKey) {
		this.axisKey = axisKey;
		return this;
	}

	public int getUuKey() {
		return uuKey;
	}

	public DaExportedSingleData setUuKey(int uuKey) {
		this.uuKey = uuKey;
		return this;
	}
}
