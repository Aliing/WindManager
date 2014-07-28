package com.ah.util.bo.dashboard;

import com.ah.util.bo.report.freechart.AhChartExportInterf;
import com.ah.util.bo.report.freechart.AhFreechartDatetimeStackedBar;
import com.ah.util.bo.report.freechart.AhFreechartStackedBar;
import com.ah.util.bo.report.freechart.AhFreechartDatetimeLine;
import com.ah.util.bo.report.freechart.AhFreechartPie;
import com.ah.util.bo.report.freechart.AhFreechartDatetimeArea;
import com.ah.util.bo.report.freechart.AhFreechartDatetimeStackedArea;

public class JfreechartTypeSelectUtil {
	public static Class<? extends AhChartExportInterf> 
					getCertainExportType(String chartType) {
		return getCertainExportType(chartType, false, false);
	}
	
	public static Class<? extends AhChartExportInterf> 
					getCertainExportType(String chartType, boolean blnOvertime) {
		return getCertainExportType(chartType, blnOvertime, false);
	}
	
	public static Class<? extends AhChartExportInterf> 
					getCertainExportType(String chartType, boolean blnOvertime, boolean blnStacked) {
		if (chartType != null) {
			if ("line".equals(chartType)) {
				if (blnOvertime) {
					return AhFreechartDatetimeLine.class;
				}
			} else if ("pie".equals(chartType)) {
				return AhFreechartPie.class;
			} else if ("area".equals(chartType)) {
				if (blnOvertime) {
					if (blnStacked) {
						return AhFreechartDatetimeStackedArea.class;
					} else {
						return AhFreechartDatetimeArea.class;
					}
				}
			} else if ("column".equals(chartType)) {
				if (blnOvertime) {
					return AhFreechartDatetimeStackedBar.class;
				} else {
					return AhFreechartStackedBar.class;
				}
			}
		}
		
		return null;
	}
}