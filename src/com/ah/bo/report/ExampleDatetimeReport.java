package com.ah.bo.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.ah.bo.performance.AhNewReport;
import com.ah.bo.report.annotation.AhReportConfig;

@AhReportConfig(id=AhReportProperties.REPORT_EXAMPLE_FOR_TEST_DATETIME)
public class ExampleDatetimeReport extends AhAbstractReport {
	private static final Long SERIES_SSID_ONE = 1L;
	private static final Long SERIES_SSID_TWO = 2L;
	private static final Long SERIES_SSID_THREE = 3L;
	private static final Long SERIES_SSID_FOUR = 4L;
	
	@Override
	public void init() {
		this.setReportTitle("Datetime kind of Report Test");
		this.setReportSubTitle("Period: 3/2/2012 15:52:08 - 3/2/2012 17:04:00");
		this.setReportSummary("This is a test report with datetime.This is a test report with datetime.This is a test report with datetime."+
										"This is a test report with datetime.This is a test report with datetime.This is a test report with datetime.");
		this.setReportValueTitle("Clients count of ssid");
	}

	@Override
	protected void doCalculate() throws Exception {
		prepareDataSeries();
	}

	private void prepareDataSeries() throws Exception {
		if (!this.getRequest().isUseScaleArea()) {
			prepareCommonRequest();
		} else {
			prepareTimeScaleRequest();
		}
	}
	
	private List<Object> prepareSeriesValues(int count, int min, int max) {
		List<Object> list = new ArrayList<Object>(count);
		for (int i = 0; i < count; i++) {
			if (Math.random()*10%7 == 0) {
				list.add(null);
			} else {
				list.add((int)(Math.random()*(max-min) + min));
			}
		}
		return list;
	}
	
	private void prepareCommonRequest() throws Exception {
		int seriesCount = (int)(Math.random()*10)%4 + 1;
		
		Calendar reportDateTime = Calendar.getInstance(this.getRequest()
				.getTimeZone());
		
		int count = 24;
		int padding = 1;
		if (this.getRequest().getPeriodType() == AhNewReport.NEW_REPORT_PERIOD_LASTONEHOUR) {
			count = 60;
			reportDateTime.add(Calendar.HOUR_OF_DAY, -1);
		} else {
			count = 24;
			padding = 60;
			reportDateTime.add(Calendar.HOUR_OF_DAY, -24);
		}
		
		List<Long> reportTimes = new ArrayList<Long>(count);
		Long starttime = reportDateTime.getTimeInMillis();
		
		for (int i = 0; i < count; i++) {
			reportTimes.add(starttime + i*padding*60*1000);
		}
		
		this.setReportStartTime(starttime);
		this.setReportEndTime(reportTimes.get(count-1));
		
		if (seriesCount > 0) {
			AhDatetimeSeries seriesSsid1 = new AhDatetimeSeries();
			seriesSsid1.setId(SERIES_SSID_ONE);
			seriesSsid1.setName("Ssid-1");
			seriesSsid1.setShowType(AhReportProperties.SERIES_TYPE_LINE);
			seriesSsid1.addData(reportTimes, prepareSeriesValues(count, 2, 9));
			seriesSsid1.addSummary("Total: 16");
			seriesSsid1.addSummary("Average: 3.2");
			this.addSeries(seriesSsid1);
		}
		
		if (seriesCount > 1) {
			AhDatetimeSeries seriesSsid2 = new AhDatetimeSeries();
			seriesSsid2.setId(SERIES_SSID_TWO);
			seriesSsid2.setName("Ssid-2");
			seriesSsid2.setShowType(AhReportProperties.SERIES_TYPE_LINE);
			int i = 0;
			seriesSsid2.addData(reportTimes.get(i++), 5);
			seriesSsid2.addData(reportTimes.get(i++), 3);
			seriesSsid2.addData(reportTimes.get(i++), 4);
			seriesSsid2.addData(reportTimes.get(i++), 5);
			seriesSsid2.addData(reportTimes.get(i++), 2);
			for (int j = 0; j < count - 5; j++) {
				seriesSsid2.addData(reportTimes.get(i++), j%7);
			}
			seriesSsid2.addSummary("Total: 21");
			this.addSeries(seriesSsid2);
		}
		
		if (seriesCount > 2) {
			AhDatetimeSeries seriesSsid3 = new AhDatetimeSeries();
			seriesSsid3.setId(SERIES_SSID_THREE);
			seriesSsid3.setName("Ssid-3");
			seriesSsid3.setShowType(AhReportProperties.SERIES_TYPE_LINE);
			seriesSsid3.addData(reportTimes, prepareSeriesValues(count, 5, 8));
			seriesSsid3.addSummary("Average: 3.6");
			this.addSeries(seriesSsid3);
		}
		
		if (seriesCount > 3) {
			AhDatetimeSeries seriesSsid4 = new AhDatetimeSeries();
			seriesSsid4.setId(SERIES_SSID_FOUR);
			seriesSsid4.setName("Ssid-4");
			seriesSsid4.setShowType(AhReportProperties.SERIES_TYPE_LINE);
			seriesSsid4.addData(reportTimes, prepareSeriesValues(count, 1, 11));
			seriesSsid4.addSummary("datetime mark.");
			this.addSeries(seriesSsid4);
		}
	}
	
	private void prepareTimeScaleRequest() throws Exception {
		int seriesCount = (int)(Math.random()*10)%4 + 1;
		
		Calendar reportDateTime = Calendar.getInstance(this.getRequest()
				.getTimeZone());
		reportDateTime.add(Calendar.HOUR_OF_DAY, -1);
		
		List<Long> reportTimes = new ArrayList<Long>(10);
		//Long starttime = reportDateTime.getTimeInMillis();
		Long scaleStartTime = this.getRequest().getScaleAreaStart();
		//Long scaleEndTime = this.getRequest().getScaleAreaEnd();
		int j = 0;
		reportTimes.add(scaleStartTime + 3*j++*60*1000);
		reportTimes.add(scaleStartTime + 3*j++*60*1000);
		reportTimes.add(scaleStartTime + 3*j++*60*1000);
		reportTimes.add(scaleStartTime + 3*j++*60*1000);
		reportTimes.add(scaleStartTime + 3*j++*60*1000);
		reportTimes.add(scaleStartTime + 3*j++*60*1000);
		reportTimes.add(scaleStartTime + 3*j++*60*1000);
		reportTimes.add(scaleStartTime + 3*j++*60*1000);
		reportTimes.add(scaleStartTime + 3*j++*60*1000);
		reportTimes.add(scaleStartTime + 3*j++*60*1000);
		this.setReportStartTime(scaleStartTime);
		this.setReportEndTime(scaleStartTime + 3*9*60*1000);

		
		if (seriesCount > 0) {
			AhDatetimeSeries seriesSsid1 = new AhDatetimeSeries();
			seriesSsid1.setId(SERIES_SSID_ONE);
			seriesSsid1.setName("Ssid-1-detail");
			seriesSsid1.setShowType(AhReportProperties.SERIES_TYPE_LINE);
			seriesSsid1.addData(reportTimes, Arrays.asList(new Object[]{2, 5, 6, 2, 1, 3, 3, 3, 3, 3}));
			seriesSsid1.addSummary("Total: 16");
			seriesSsid1.addSummary("Average: 3.2");
			this.addSeries(seriesSsid1);
		}
		
		if (seriesCount > 1) {
			AhDatetimeSeries seriesSsid2 = new AhDatetimeSeries();
			seriesSsid2.setId(SERIES_SSID_TWO);
			seriesSsid2.setName("Ssid-2-detail");
			seriesSsid2.setShowType(AhReportProperties.SERIES_TYPE_SCATTER);
			int i = 0;
			seriesSsid2.addData(reportTimes.get(i++), 5);
			seriesSsid2.addData(reportTimes.get(i++), 3);
			seriesSsid2.addData(reportTimes.get(i++), 4);
			seriesSsid2.addData(reportTimes.get(i++), 5);
			seriesSsid2.addData(reportTimes.get(i++), 2);
			seriesSsid2.addData(reportTimes.get(i++), 4);
			seriesSsid2.addData(reportTimes.get(i++), 4);
			seriesSsid2.addData(reportTimes.get(i++), 4);
			seriesSsid2.addData(reportTimes.get(i++), 4);
			seriesSsid2.addData(reportTimes.get(i++), 4);
			seriesSsid2.addSummary("Total: 21");
			this.addSeries(seriesSsid2);
		}
		
		if (seriesCount > 2) {
			AhDatetimeSeries seriesSsid3 = new AhDatetimeSeries();
			seriesSsid3.setId(SERIES_SSID_THREE);
			seriesSsid3.setName("Ssid-3-detail");
			seriesSsid3.setShowType(AhReportProperties.SERIES_TYPE_AREA);
			seriesSsid3.addData(reportTimes, Arrays.asList(new Object[]{3, 4, 4, 2, 2, 2, 2}));
			seriesSsid3.addSummary("Average: 3.6");
			this.addSeries(seriesSsid3);
		}
		
		if (seriesCount > 3) {
			AhDatetimeSeries seriesSsid4 = new AhDatetimeSeries();
			seriesSsid4.setId(SERIES_SSID_FOUR);
			seriesSsid4.setName("Ssid-4-detail");
			seriesSsid4.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
			seriesSsid4.addData(reportTimes, Arrays.asList(new Object[]{3, 0, 4, 4, 3, 8, 4, 8, 4, 8}));
			seriesSsid4.addSummary("datetime mark.");
			this.addSeries(seriesSsid4);
		}
	}
	
}
