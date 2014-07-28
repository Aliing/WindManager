package com.ah.util.bo.report.freechart;

import com.ah.bo.report.AhReportElement;

public class AhNullFreechartWrapper extends AhFreechartWrapper {
	
	@SuppressWarnings("unused")
	private AhNullFreechartWrapper() {
	}
	
	public AhNullFreechartWrapper(AhReportElement aReport) {
		super(aReport);
		this.setFreeChart(null);
	}
}
