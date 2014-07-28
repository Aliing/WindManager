package com.ah.util.bo.report.freechart;

import org.apache.commons.lang.StringUtils;

public class AhFreechartOption {
	private boolean blnIntValue;
	
	// for pie chart
	private boolean blnPercentOnChart;
	
	private boolean hasMaxValue;
	private boolean hasMinValue;
	private Integer maxValue;
	private Integer minValue;
	
	private boolean blnStacked = true;
	
	private String myUnit;
	
	private int dataSample;
	
	public int getDataSample() {
		return dataSample;
	}

	public AhFreechartOption setDataSample(int dataSample) {
		this.dataSample = dataSample;
		return this;
	}

	public boolean isBlnIntValue() {
		return blnIntValue;
	}

	public AhFreechartOption setBlnIntValue(boolean blnIntValue) {
		this.blnIntValue = blnIntValue;
		return this;
	}

	public boolean isBlnPercentOnChart() {
		return blnPercentOnChart;
	}

	public AhFreechartOption setBlnPercentOnChart(boolean blnPercentOnChart) {
		this.blnPercentOnChart = blnPercentOnChart;
		return this;
	}

	public boolean isBlnStacked() {
		return blnStacked;
	}

	public AhFreechartOption setBlnStacked(boolean blnStacked) {
		this.blnStacked = blnStacked;
		return this;
	}

	public boolean isHasMaxValue() {
		return hasMaxValue;
	}

	public boolean isHasMinValue() {
		return hasMinValue;
	}

	public Integer getMaxValue() {
		return maxValue;
	}

	protected void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
		this.hasMaxValue = true;
	}

	public Integer getMinValue() {
		return minValue;
	}

	protected void setMinValue(Integer minValue) {
		this.minValue = minValue;
		this.hasMinValue = true;
	}
	
	public AhFreechartOption setValueRange(String valRange) {
		if (StringUtils.isNotBlank(valRange)) {
			String[] vals = valRange.split("to");
			if (vals.length > 0) {
				this.setMinValue(Integer.valueOf(vals[0]));
			}
			if (vals.length > 1) {
				this.setMaxValue(Integer.valueOf(vals[1]));
			}
		}
		return this;
	}

	public String getMyUnit() {
		return myUnit;
	}

	public AhFreechartOption setMyUnit(String myUnit) {
		this.myUnit = myUnit;
		return this;
	}
}
