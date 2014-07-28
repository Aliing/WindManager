package com.ah.util.bo.report.freechart;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import com.ah.bo.report.AhReportElement;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.AhSeriesData;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.NumberTypeUtil;
import com.lowagie.text.Font;

public class AhFreechartPie extends AhFreechartBase implements AhChartExportInterf {

	public AhFreechartPie(String dataPointUnit) {
		this.dataPointUnit = dataPointUnit;
	}
	
	public AhFreechartPie() {
	}
	
	@Override
	public AhFreechartWrapper invoke(AhReportElement aReport) throws Exception {
		if (aReport == null) {
			return null;
		}
		if (aReport.isReportNull()
				|| aReport.getSeries().get(0).isNullSeries()) {
			return new AhNullFreechartWrapper(aReport);
		}
		
		if (StringUtils.isNotBlank(this.dataPointUnit)) {
			this.getChartOption().setMyUnit(this.dataPointUnit);
		}
		
		JFreeChart chart;
		org.jfree.text.TextUtilities.setUseDrawRotatedStringWorkaround(false);
       	DefaultPieDataset dataset = new DefaultPieDataset();
       	List<AhSeriesData> reportData = aReport.getSeries().get(0).getData();
       	String valueType = NumberTypeUtil.NUMBER_TYPE_NOT_NUMBER;
       	for (AhSeriesData data : reportData) {
       		if (data != null
       				&& data.getValue() != null) {
       			valueType = NumberTypeUtil.getNumberType(data.getValue());
       			break;
       		}
       	}
       	int curValueIdx = 0;
       	String curValue;
       	for (AhSeriesData data : reportData) {
       		if (this.dataNames != null) {
       			curValue = this.dataNames.get(curValueIdx++);
       		} else {
       			curValue = data.getName() == null ? "" : data.getName().toString();
       		}
       		dataset.setValue(curValue, NumberTypeUtil.convertToBigDecimal(data.getValue(), valueType));
       	}
    	chart = ChartFactory.createPieChart(
    			"", dataset, true,
				true, false);
		
    	
    	chart.setBackgroundPaint(Color.white); 
    	PiePlot pieplot = (PiePlot)chart.getPlot();
    	pieplot.setBackgroundPaint(Color.white);
    	
    	DecimalFormat df = new DecimalFormat("0.00%");
    	NumberFormat nf = NumberFormat.getNumberInstance();
    	if (this.getChartOption().isBlnPercentOnChart()) {
			pieplot.setLabelGenerator(new StandardPieSectionLabelGenerator("{2}", nf, df));
			pieplot.setLabelFont(new java.awt.Font(FONT_PATH,Font.NORMAL,8));
			pieplot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0} : {1}" + AhNewReportUtil.getFormattedDataUnit(this.getChartOption().getMyUnit())));
    	} else {
    		pieplot.setLabelGenerator(null);
    		pieplot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{2} {0} : {1}" + AhNewReportUtil.getFormattedDataUnit(this.getChartOption().getMyUnit()), nf, df));
    	}

    	chart.getLegend().setPosition(org.jfree.ui.RectangleEdge.LEFT);
		chart.getLegend().setBorder(0,0,0,0);
		chart.getLegend().setItemFont(new java.awt.Font(FONT_PATH,Font.NORMAL,6));
		chart.getLegend().setVerticalAlignment(org.jfree.ui.VerticalAlignment.TOP);
		
		if (this.dataColors != null) {
			for (int i=0; i< dataset.getKeys().size(); i++) {
				pieplot.setSectionPaint(dataset.getKey(i), new Color(AhNewReportUtil.getHexColorValue(this.dataColors.get(i))));
			}
		} else {
			for (int i=0; i<AhReportProperties.SERIES_ALL_COLORS.length && i< dataset.getKeys().size(); i++) {
				pieplot.setSectionPaint(dataset.getKey(i), new Color(AhNewReportUtil.getHexColorValue(AhReportProperties.SERIES_ALL_COLORS[i])));
			}
		}
		
		return new AhFreechartWrapper(aReport, chart);
	}

	private String dataPointUnit;

	public String getDataPointUnit() {
		return dataPointUnit;
	}

	public void setDataPointUnit(String dataPointUnit) {
		this.dataPointUnit = dataPointUnit;
	}
	
	private List<String> dataColors;
	private List<String> dataNames;

	public List<String> getDataColors() {
		return dataColors;
	}

	public void setDataColors(List<String> dataColors) {
		this.dataColors = dataColors;
	}

	public List<String> getDataNames() {
		return dataNames;
	}

	public void setDataNames(List<String> dataNames) {
		this.dataNames = dataNames;
	}
	
}
