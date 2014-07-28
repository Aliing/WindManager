package com.ah.util.bo.report.freechart;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.VerticalAlignment;

import com.ah.bo.report.AhReportElement;
import com.ah.bo.report.AhSeries;
import com.ah.bo.report.AhSeriesData;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.NumberTypeUtil;
import com.lowagie.text.Font;
//import org.jfree.chart.axis.NumberTickUnit;

public class AhFreechartStackedBar extends AhFreechartBase implements AhChartExportInterf {
	
	public AhFreechartStackedBar() {
	}
	public AhFreechartStackedBar(boolean blnIntUnit) {
		this.blnIntUnit = blnIntUnit;
	}
	public AhFreechartStackedBar(boolean blnIntUnit, int labelCount) {
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
		
		boolean blnAllValueZero = true;
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Map<String, String> seriesNameGroupMap = new HashMap<>();
		List<String> categoriesTmp = aReport.getCategorieStrings();
		Map<Integer, Integer> colValues = new HashMap<Integer, Integer>();
       	for (int i = 0; i < aReport.getSeries().size(); i++) {
       		AhSeries aSeries = aReport.getSeries().get(i);
       		if (aSeries.isNullSeries()) {
       			continue;
       		}
       		if (categoriesTmp.size() != aSeries.getData().size()) {
       			continue;
       		}
       		this.addColorForDataSet(aSeries);
       		String seriesLegend = AhNewReportUtil.getLegendStringForFreeChart(aSeries);
       		seriesNameGroupMap.put(seriesLegend, aSeries.getStackGroupString());
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
           		BigDecimal tmpValue = NumberTypeUtil.convertToBigDecimal(data.getValue(), valueType);
           		dataset.addValue(tmpValue,
           				seriesLegend, categoriesTmp.get(curCategoryIdx));
           		if (blnAllValueZero
           				&& tmpValue.compareTo(BIGDECIMAL_ZERO) != 0) {
           			blnAllValueZero = false;
           		}
           		
           		if (this.getChartOption().isBlnIntValue()) {
	           		if (!colValues.containsKey(curCategoryIdx)) {
	           			colValues.put(curCategoryIdx, tmpValue.intValue());
	           		} else {
	           			colValues.put(curCategoryIdx, colValues.get(curCategoryIdx) + tmpValue.intValue());
	           		}
           		}
           		
           		curCategoryIdx++;
           	}
       	}
       	
        JFreeChart chart;
        if (this.getChartOption().isBlnStacked()) {
        	chart = ChartFactory.createStackedBarChart(
        			"", aReport.getCategoryTitle(), aReport.getValueTitle(),
        			dataset,
        			PlotOrientation.HORIZONTAL,
        			true, true, false);
        } else {
	        chart = ChartFactory.createBarChart(
	        		"", aReport.getCategoryTitle(), aReport.getValueTitle(),
	                dataset,
	                PlotOrientation.HORIZONTAL,
	                true, true, false);
        }
        
        chart.setBackgroundPaint(Color.white);
        
        CategoryPlot plot = (CategoryPlot)chart.getPlot();   
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		
		if (this.getChartOption().isBlnStacked()) {
	        GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
	        KeyToGroupMap map = new KeyToGroupMap("0");
	        for (String key : seriesNameGroupMap.keySet()) {
	        	map.mapKeyToGroup(key, seriesNameGroupMap.get(key));
	        }
	        renderer.setSeriesToGroupMap(map);
	        plot.setRenderer(renderer);
        }
		
		BarRenderer barrenderer = (BarRenderer)plot.getRenderer();
	    barrenderer.setMaximumBarWidth(0.2);
	    barrenderer.setShadowVisible(false);
	    barrenderer.setShadowXOffset(0);
	    barrenderer.setShadowYOffset(0);
	    barrenderer.setDrawBarOutline(false);
	    barrenderer.setBarPainter(new StandardBarPainter());
		for (int i=0; i< dataset.getRowKeys().size(); i++) {
			barrenderer.setSeriesPaint(i, this.getADefinedColor(i));
		}
		
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
 		if (blnAllValueZero) {
 			rangeAxis.setAutoTickUnitSelection(false);
 		}
 		
 		NumberAxis numAxis = (NumberAxis)plot.getRangeAxis();
 		if (this.getChartOption().isBlnIntValue()) {
 			if (this.labelCount < 1) {
 				this.labelCount = 8;
 			}
 			Integer maxValue = 0;
 			for (Integer valTmp : colValues.values()) {
 				if (valTmp.compareTo(maxValue) > 0) {
 					maxValue = valTmp;
 				}
 			}
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
