package com.ah.util.bo.report.freechart;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;

import com.ah.bo.report.AhReportElement;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.AhSeries;
import com.ah.bo.report.AhSeriesData;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.NumberTypeUtil;
import com.lowagie.text.Font;

public class AhFreechartDatetimeArea extends AhFreechartBase implements AhChartExportInterf {

	@Override
	public AhFreechartWrapper invoke(AhReportElement aReport) throws Exception {
		if (aReport == null) {
			return null;
		}
		if (aReport.isReportNull()
				|| aReport.getSeries().get(0).isNullSeries()) {
			return new AhNullFreechartWrapper(aReport);
		}
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
  		sdf.setTimeZone(aReport.getTz());
		List<String> categoriesTmp = new ArrayList<String>(aReport.getSeries().get(0).getData().size());
		List<AhSeriesData> reportDataFirstSeries = aReport.getSeries().get(0).getData();
		for (AhSeriesData data : reportDataFirstSeries) {
			categoriesTmp.add(sdf.format(new Date((Long)data.getName())));
		}
       	for (int i = 0; i < aReport.getSeries().size(); i++) {
       		AhSeries aSeries = aReport.getSeries().get(i);
       		if (aSeries.isNullSeries()) {
       			continue;
       		}
       		if (categoriesTmp.size() != aSeries.getData().size()) {
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
           	int curCategoryIdx = 0;
           	for (AhSeriesData data : reportData) {
           		dataset.addValue(NumberTypeUtil.convertToBigDecimal(data.getValue(), valueType),
           				seriesLegend, categoriesTmp.get(curCategoryIdx));
           		curCategoryIdx++;
           	}
       	}
    	
        JFreeChart chart = ChartFactory.createAreaChart(
          "", aReport.getCategoryTitle(), aReport.getValueTitle(),
           dataset,
            PlotOrientation.VERTICAL,
            true, true, false);
        
        chart.setBackgroundPaint(Color.white); 
        chart.setPadding(new RectangleInsets(0, 0, 0, 10));
        
        CategoryPlot plot = (CategoryPlot)chart.getPlot();   
		plot.setBackgroundPaint(Color.white);
//		plot.setDomainGridlinePaint(Color.white);
//		plot.setRangeGridlinePaint(Color.white);
		
		for (int i=0; i<AhReportProperties.SERIES_ALL_COLORS.length && i< dataset.getColumnKeys().size(); i++) {
			plot.getRenderer().setSeriesPaint(i, new Color(AhNewReportUtil.getHexColorValue(AhReportProperties.SERIES_ALL_COLORS[i])));
		}
		
		NumberAxis numAxis = (NumberAxis)plot.getRangeAxis();
		prepareValueLabelFormat(numAxis);
		prepareValueRange(numAxis);
		
		// X
		CategoryAxis axis = (CategoryAxis) plot.getDomainAxis();
		axis.setLabelPaint(Color.BLACK);
        axis.setLabelFont(new java.awt.Font(FONT_PATH,Font.NORMAL,8));
        axis.setTickLabelFont(new java.awt.Font(FONT_PATH,Font.NORMAL,8));
        
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
