package com.ah.util.bo.report.builder;

import java.util.Calendar;

import com.ah.bo.performance.AhNewReport;
import com.ah.bo.report.AhAbstractReport;
import com.ah.bo.report.impl.AhAbstractNewReport;
import com.ah.util.bo.report.AhReportRequest;

public class DefaultNewReportBuilder implements AhReportBuilder {

	@Override
	public AhAbstractReport build(AhReportRequest ar,
			Class<? extends AhAbstractReport> reportClass) throws Exception {
		AhAbstractReport resultObject = (AhAbstractReport)reportClass.newInstance();
		resultObject.setRequest(ar);
		
		if (resultObject instanceof AhAbstractNewReport) {
			AhAbstractNewReport reportObject = (AhAbstractNewReport)resultObject;
			
			reportObject.initReportElement(ar);
			reportObject.setReportOptions((AhNewReport)ar.getDataSource());
			if (ar.isPeriodTypeNotDefined()) {
				reportObject.setPeriodType(reportObject.getReportOptions().getReportPeriod());
			} else {
				reportObject.setPeriodType(ar.getPeriodType());
			}
			reportObject.setTz(reportObject.getReportOptions().getTz());
			reportObject.setDomainId(ar.getDomainId());
			reportObject.setBlnSampleData(reportObject.getReportOptions().isForSample());
			
			long currentTime= System.currentTimeMillis();
			Calendar ca = Calendar.getInstance(reportObject.getTz());
			ca.setTimeInMillis(currentTime);
			reportObject.setStartTime(reportObject.getReportOptions().getRunStartTime(ca));
			ca.setTimeInMillis(currentTime);
			reportObject.setEndTime(reportObject.getReportOptions().getRunEndTime(ca));
			
			reportObject.setBlnGroupCal(ar.isBlnGroupCal());
			
			reportObject.setReportStartTime(reportObject.getStartTime());
			reportObject.setReportEndTime(reportObject.getEndTime());
		}
		
		return resultObject;
	}

}
