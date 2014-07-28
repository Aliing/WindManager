package com.ah.util.bo.report.freechart;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;

import com.ah.bo.report.AhReportElement;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.AhSeries;
import com.ah.bo.report.AhSeriesData;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.NumberTypeUtil;
import com.lowagie.text.Font;

public class AhFreechartDatetimeStackedArea extends AhFreechartBase implements AhChartExportInterf {

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
           	int dataLen = reportData.size();
           	for (int j = 0; j < dataLen; j++) {
           		AhSeriesData data = reportData.get(j);
           		dataset.add(new SimpleTimePeriod(new Date((Long)data.getName()), 
           				new Date((Long)getNextDataPoint(reportData, j).getName())) , 
           				NumberTypeUtil.convertToBigDecimal(data.getValue(), valueType).doubleValue(), 
           				seriesLegend);
           	}
       	}

		JFreeChart chart = ChartFactory.createStackedXYAreaChart(
		           "",
		           aReport.getCategoryTitle(), aReport.getValueTitle(),
		           (TableXYDataset)dataset,
		           PlotOrientation.VERTICAL,
		           true,
		           true,
		           false
		       );
		chart.setBackgroundPaint(Color.white);
		chart.setPadding(new RectangleInsets(0, 0, 0, 10));
		
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		//plot.setDomainGridlinePaint(Color.GRAY);
		//plot.setRangeGridlinePaint(Color.GRAY);
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		plot.setDomainAxis(new DateAxis());
		
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
		NumberAxis numAxis = (NumberAxis)plot.getRangeAxis();
		prepareValueLabelFormat(numAxis);
		prepareValueRange(numAxis);
		
		// X
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(sdf);
        axis.setLabelPaint(Color.BLACK);
        axis.setLabelFont(new java.awt.Font(FONT_PATH,Font.NORMAL,8));
        axis.setTickLabelFont(new java.awt.Font(FONT_PATH,Font.NORMAL,8));
        axis.setTickMarkPosition(DateTickMarkPosition.START);
        axis.setLowerMargin(0);
        axis.setUpperMargin(0);
        
		// Y
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setLabelPaint(Color.BLACK);
		rangeAxis.setLabelFont(new java.awt.Font(FONT_PATH,Font.NORMAL,8));
		rangeAxis.setTickLabelFont(new java.awt.Font(FONT_PATH,Font.NORMAL,8));
		chart.getLegend().setPosition(org.jfree.ui.RectangleEdge.LEFT);
		chart.getLegend().setBorder(0,0,0,0);
		chart.getLegend().setVerticalAlignment(VerticalAlignment.TOP);
		chart.getLegend().setItemFont(new java.awt.Font(FONT_PATH,Font.NORMAL,6));
		
        return new AhFreechartWrapper(aReport, chart);
	}

}
