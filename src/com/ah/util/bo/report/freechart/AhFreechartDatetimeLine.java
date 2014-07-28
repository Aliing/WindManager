package com.ah.util.bo.report.freechart;

import java.awt.Color;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
//import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;

import com.ah.bo.report.AhReportElement;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.AhSeries;
import com.ah.bo.report.AhSeriesData;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.NumberTypeUtil;
import com.lowagie.text.Font;

public class AhFreechartDatetimeLine extends AhFreechartBase implements AhChartExportInterf {
	
	public AhFreechartDatetimeLine() {
	}
	public AhFreechartDatetimeLine(boolean blnIntUnit) {
		this.blnIntUnit = blnIntUnit;
	}
	public AhFreechartDatetimeLine(boolean blnIntUnit, int labelCount) {
		this(blnIntUnit);
		this.labelCount = labelCount;
	}

	@Override
	public AhFreechartWrapper invoke(AhReportElement aReport) throws Exception {
		if (aReport == null) {
			return null;
		}
		if (aReport.isReportNull()) {
			return new AhNullFreechartWrapper(aReport);
		}
		
		// to compatible previous version of implementation
		if (this.blnIntUnit) {
			this.getChartOption().setBlnIntValue(this.blnIntUnit);
		}
		
		Integer maxValue = 0;
		org.jfree.text.TextUtilities.setUseDrawRotatedStringWorkaround(false);
  		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
  		sdf.setTimeZone(aReport.getTz());
  		
  		TimeSeriesCollection dataset = new TimeSeriesCollection();
  		for (int i = 0; i < aReport.getSeries().size(); i++) {
       		AhSeries aSeries = aReport.getSeries().get(i);
       		if (aSeries.isNullSeries()) {
       			continue;
       		}
       		String seriesLegend = AhNewReportUtil.getLegendStringForFreeChart(aSeries);
       		List<AhSeriesData> reportData = aSeries.getData();
           	String valueType = NumberTypeUtil.NUMBER_TYPE_NOT_NUMBER;
           	for (AhSeriesData data : reportData) {
           		if (data != null
           				&& data.getValue() != null) {
           			valueType = NumberTypeUtil.getNumberType(data.getValue());
           			break;
           		}
           	}
           	TimeSeries seriesTmp = new TimeSeries(seriesLegend);
           	for (AhSeriesData data : reportData) {
           		BigDecimal tmpValue = NumberTypeUtil.convertToBigDecimal(data.getValue(), valueType);
           		seriesTmp.addOrUpdate(new Minute(new Date((Long)data.getName())) , tmpValue);
           		
           		if (this.getChartOption().isBlnIntValue()) {
           			if (maxValue < tmpValue.intValue()) {
           				maxValue = tmpValue.intValue();
           			}
           		}
           	}
           	
           	dataset.addSeries(seriesTmp);
       	}

		JFreeChart chart = ChartFactory.createTimeSeriesChart(
		           "",
		           aReport.getCategoryTitle(), aReport.getValueTitle(),
		           dataset,
		           true,
		           true,
		           false
		       );
		chart.setBackgroundPaint(Color.white); 
		chart.setPadding(new RectangleInsets(0, 0, 0, 10));
		
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setOutlinePaint(Color.white);
		
		for (int i=0; i<AhReportProperties.SERIES_ALL_COLORS.length && i< dataset.getSeriesCount(); i++) {
			plot.getRenderer().setSeriesPaint(i,new Color(AhNewReportUtil.getHexColorValue(AhReportProperties.SERIES_ALL_COLORS[i])));
		}
		// X
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(sdf);
        axis.setLabelPaint(Color.BLACK);
        axis.setLabelFont(new java.awt.Font(FONT_PATH,Font.NORMAL,8));
        axis.setTickLabelFont(new java.awt.Font(FONT_PATH,Font.NORMAL,8));
        axis.setTickMarkPosition(DateTickMarkPosition.START);
        
		// Y
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setLabelPaint(Color.BLACK);
		rangeAxis.setLabelFont(new java.awt.Font(FONT_PATH,Font.NORMAL,8));
		rangeAxis.setTickLabelFont(new java.awt.Font(FONT_PATH,Font.NORMAL,8));
		
		NumberAxis numAxis = (NumberAxis)plot.getRangeAxis();
		if (this.getChartOption().isBlnIntValue()) {
			if (this.labelCount < 1) {
 				this.labelCount = 8;
 			}
	 		numAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	 		/*int tickUnitTmp = maxValue/this.labelCount;
	 		if (tickUnitTmp < 1) {
	 			tickUnitTmp = 1;
	 		}
	 		numAxis.setTickUnit(new NumberTickUnit(tickUnitTmp));*/
		}
		prepareValueLabelFormat(numAxis);
		prepareValueRange(numAxis);
		
		chart.getLegend().setPosition(org.jfree.ui.RectangleEdge.LEFT);
		chart.getLegend().setBorder(0,0,0,0);
		chart.getLegend().setVerticalAlignment(VerticalAlignment.TOP);
		chart.getLegend().setItemFont(new java.awt.Font(FONT_PATH,Font.NORMAL,6));
		return new AhFreechartWrapper(aReport, chart);
	}
	
	private boolean blnIntUnit;
	private int labelCount = 8;
	
	public boolean isBlnIntUnit() {
		return blnIntUnit;
	}

	public void setBlnIntUnit(boolean blnIntUnit) {
		this.blnIntUnit = blnIntUnit;
	}
	
	public int getLabelCount() {
		return labelCount;
	}

	public void setLabelCount(int labelCount) {
		this.labelCount = labelCount;
	}

}
