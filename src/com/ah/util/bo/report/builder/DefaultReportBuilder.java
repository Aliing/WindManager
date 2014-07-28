package com.ah.util.bo.report.builder;

import com.ah.bo.report.AhAbstractReport;
import com.ah.util.bo.report.AhReportRequest;

public class DefaultReportBuilder implements AhReportBuilder {

	@Override
	public AhAbstractReport build(AhReportRequest ar, 
			Class<? extends AhAbstractReport> reportClass) throws Exception {
		AhAbstractReport report = (AhAbstractReport)reportClass.newInstance();
		report.initReportElement(ar);
		return report;
	}

}
