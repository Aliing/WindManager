package com.ah.util.bo.report.freechart;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;

import com.ah.bo.report.AhReportElement;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.AhSeries;
import com.ah.bo.report.AhSeriesData;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.NumberTypeUtil;
import com.lowagie.text.Font;

public class AhFreechartDatetimeStackedBar extends AhFreechartBase implements AhChartExportInterf {

	@Override
	public AhFreechartWrapper invoke(AhReportElement aReport) throws Exception {
		if (aReport == null) {
			return null;
		}
		if (aReport.isReportNull()) {
			return new AhNullFreechartWrapper(aReport);
		}
		
		List<AhSeries> seriesLst = aReport.getSeries();
		
		org.jfree.text.TextUtilities.setUseDrawRotatedStringWorkaround(false);
  		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
  		sdf.setTimeZone(aReport.getTz());
  		
  		long minTimeRange = getMinTimeRangeBetweenPoints(aReport);
  		TimeTableXYDataset dataset = new TimeTableXYDataset();
  		for (int i = seriesLst.size() - 1; i >= 0; i--) {
       		AhSeries aSeries = seriesLst.get(i);
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
           	for (AhSeriesData data : reportData) {
           		dataset.add(new SimpleTimePeriod(new Date((Long)data.getName() - minTimeRange/2), 
           				new Date((Long)data.getName() + minTimeRange/2)) , 
           				NumberTypeUtil.convertToBigDecimal(data.getValue(), valueType).doubleValue(), 
           				seriesLegend);
           	}
       	}
  		
  		DateAxis domainAxis = new DateAxis(aReport.getCategoryTitle());
        domainAxis.setTickMarkPosition(DateTickMarkPosition.START);
        domainAxis.setDateFormatOverride(sdf);
        domainAxis.setLabelPaint(Color.BLACK);
        domainAxis.setLabelFont(new java.awt.Font(FONT_PATH,Font.NORMAL,8));
        domainAxis.setTickLabelFont(new java.awt.Font(FONT_PATH,Font.NORMAL,8));
        domainAxis.setLowerMargin(0.01D);
        domainAxis.setUpperMargin(0.01D);

        NumberAxis rangeAxis = new NumberAxis(aReport.getValueTitle());
        rangeAxis.setLabelPaint(Color.BLACK);
		rangeAxis.setLabelFont(new java.awt.Font(FONT_PATH,Font.NORMAL,8));
		rangeAxis.setTickLabelFont(new java.awt.Font(FONT_PATH,Font.NORMAL,8));
		rangeAxis.setUpperMargin(0.10000000000000001D);

		double renderMargin = 0.14999999999999999D;
		if (getDataCount(aReport) == 1) {
			renderMargin = 0.64999999999999999D;
		}
		StackedXYBarRenderer renderer = new StackedXYBarRenderer(renderMargin);
        //renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
        renderer.setShadowXOffset(0);
        renderer.setShadowYOffset(0);
        renderer.setBarPainter(new StandardXYBarPainter());
        
        XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
        int curColorIdx = 0;
		int curSeriesIdx = 0;
		for (int i = seriesLst.size() - 1; i >= 0; i--) {
			AhSeries aSeries = seriesLst.get(i);
       		if (aSeries.isNullSeries()) {
       			continue;
       		}
			if (!StringUtils.isBlank(aSeries.getCustomColor())) {
				plot.getRenderer().setSeriesPaint(curSeriesIdx, new Color(AhNewReportUtil.getHexColorValue(aSeries.getCustomColor())));
			} else {
				plot.getRenderer().setSeriesPaint(curSeriesIdx, new Color(AhNewReportUtil.getHexColorValue(AhReportProperties.SERIES_ALL_COLORS[curColorIdx++])));
			}
			curSeriesIdx++;
		}
        
        
        JFreeChart chart = new JFreeChart("", plot);
        
		chart.setBackgroundPaint(Color.white); 
		chart.setPadding(new RectangleInsets(0, 0, 0, 10));
		
		NumberAxis numAxis = (NumberAxis)plot.getRangeAxis();
		if (this.getChartOption().isBlnIntValue()) {
	 		numAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		}
		prepareValueLabelFormat(numAxis);
		prepareValueRange(numAxis);
		
		chart.getLegend().setPosition(org.jfree.ui.RectangleEdge.LEFT);
		chart.getLegend().setBorder(0,0,0,0);
		chart.getLegend().setVerticalAlignment(VerticalAlignment.TOP);
		chart.getLegend().setItemFont(new java.awt.Font(FONT_PATH,Font.NORMAL,6));
		
        return new AhFreechartWrapper(aReport, chart);
	}

}