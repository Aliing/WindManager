package com.ah.util.bo.report.freechart;

import com.ah.bo.report.AhReportElement;
import com.lowagie.text.FontFactory;

public interface AhChartExportInterf {
	
	public static final String FONT_PATH = FontFactory.TIMES_ROMAN;
	
	public AhFreechartWrapper invoke(AhReportElement aReport) throws Exception;
	
	public AhChartExportInterf setChartOption(AhFreechartOption chartOption);
}
