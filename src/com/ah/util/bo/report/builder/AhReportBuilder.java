package com.ah.util.bo.report.builder;

import com.ah.bo.report.AhAbstractReport;
import com.ah.util.bo.report.AhReportRequest;

public interface AhReportBuilder {
	public AhAbstractReport build(AhReportRequest ar, 
				Class<? extends AhAbstractReport> reportClass) throws Exception;
}
