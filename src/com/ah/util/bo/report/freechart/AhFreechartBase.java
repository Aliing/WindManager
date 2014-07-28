package com.ah.util.bo.report.freechart;

import java.awt.Color;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.axis.NumberAxis;

import com.ah.bo.report.AhReportElement;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.AhSeries;
import com.ah.bo.report.AhSeriesData;
import com.ah.util.bo.report.AhNewReportUtil;

/**
 * this class is only work for those classes who implements AhChartExportInterf.java
 *
 */
public abstract class AhFreechartBase implements AhChartExportInterf {
	private AhFreechartOption chartOption;
	protected static final BigDecimal BIGDECIMAL_ZERO = new BigDecimal(0);
	
	public AhFreechartOption getChartOption() {
		if (chartOption == null) {
			chartOption = new AhFreechartOption();
		}
		return chartOption;
	}

	public AhChartExportInterf setChartOption(AhFreechartOption chartOption) {
		this.chartOption = chartOption;
		return this;
	}
	
	protected void prepareValueRange(NumberAxis numAxis) {
		if (this.chartOption.isHasMaxValue()) {
			numAxis.setUpperBound(this.chartOption.getMaxValue());
		}
		if (this.chartOption.isHasMinValue()) {
			numAxis.setLowerBound(this.chartOption.getMinValue());
		}
	}
	
	protected void prepareValueLabelFormat(NumberAxis numAxis) {
		if (!this.chartOption.isBlnIntValue()) {
			numAxis.setNumberFormatOverride(new DecimalFormat("0.00"));
		}
	}
	
	private List<String> colorSets = new ArrayList<>();
	protected void addColorForDataSet(String color) {
		this.colorSets.add(color);
	}
	protected void addColorForDataSet(AhSeries aSeries) {
		this.colorSets.add(aSeries.getCustomColor());
	}
	protected List<String> getColorSets() {
		return this.colorSets;
	}
	
	protected String getADefinedColorString(int index) {
		String preferColor = null;
		if (index < this.colorSets.size()) {
			preferColor = this.colorSets.get(index);
		}
		if (StringUtils.isBlank(preferColor)) {
			preferColor = AhReportProperties.SERIES_ALL_COLORS[index%AhReportProperties.SERIES_ALL_COLORS_SIZE];
		}
		
		return preferColor;
	}
	protected Color getADefinedColor(int index) {
		return new Color(AhNewReportUtil.getHexColorValue(this.getADefinedColorString(index)));
	}
	
	protected long getMinTimeRangeBetweenPoints(AhReportElement aReport) {
		long result = 0;
		List<AhSeries> seriesLst = aReport.getSeries();
		for (AhSeries aSeries : seriesLst) {
			if (aSeries.isNullSeries()) {
       			continue;
       		}
			int dataLen = aSeries.getData().size();
			for (int i = 0; i < dataLen; i++) {
				AhSeriesData data = aSeries.getData().get(i);
				AhSeriesData nextData = data;
				if (i < dataLen - 1) {
					nextData = aSeries.getData().get(i+1);
					if (result == 0
							|| ((Long)nextData.getName() - (Long)data.getName()) < result) {
						result = (Long)nextData.getName() - (Long)data.getName();
					}
				}
			}
			break;
		}
		
		if (this.chartOption != null
				&& this.chartOption.getDataSample() != 0
				&& (result == 0 
					|| this.chartOption.getDataSample() < result)) {
			result = this.chartOption.getDataSample() * 1000;
		}
		return result;
	}
	

	protected int getDataCount(AhReportElement aReport) {
		int result = 0;
		List<AhSeries> seriesLst = aReport.getSeries();
		for (AhSeries aSeries : seriesLst) {
			if (aSeries.isNullSeries()) {
       			continue;
       		}
			result = aSeries.getData().size();
			break;
		}
		
		return result;
	}
	
	protected AhSeriesData getNextDataPoint(List<AhSeriesData> reportData, int curIdx) {
		if (curIdx >= reportData.size() - 1) {
			curIdx = reportData.size() - 1;
		} else {
			curIdx = curIdx + 1;
		}
		return reportData.get(curIdx);
	}
}
