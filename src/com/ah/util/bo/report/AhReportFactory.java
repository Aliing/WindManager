package com.ah.util.bo.report;

import com.ah.bo.report.AhAbstractReport;
import com.ah.bo.report.AhReportContainer;
import com.ah.bo.report.exception.AhReportNotDefinedException;
import com.ah.util.bo.report.builder.AhReportBuilder;

public class AhReportFactory {
	
	public static AhReportProxy create(AhReportRequest ar) throws Exception {
		if (ar == null
				|| ar.getId() == null) {
			return null;
		}
		
		Long id = ar.getId();
		if (!AhReportContainer.isContainReportId(id)) {
			throw new AhReportNotDefinedException(id);
		}
		
		AhReportProxy ap = new AhReportProxy();
		AhReportBuilder ahBuilder = AhReportContainer.getReportBuilderClassById(id).newInstance();

		AhAbstractReport reportExecutor = ahBuilder.build(ar, AhReportContainer.getReportClassById(id));
		ap.setReportExecutor(reportExecutor);
		
		return ap;
	}
}
