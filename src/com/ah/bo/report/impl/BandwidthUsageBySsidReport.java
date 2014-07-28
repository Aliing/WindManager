package com.ah.bo.report.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.impl.ReportPagingImpl;
import com.ah.bo.performance.AhClientStatsInterf;
import com.ah.bo.report.AhLinearSeries;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.annotation.AhReportConfig;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.builder.DefaultNewReportBuilder;
import com.ah.util.bo.report.freechart.AhFreechartPie;

@AhReportConfig(id=AhReportProperties.REPORT_BANDWIDTH_USAGE_BY_SSID,
		builder=DefaultNewReportBuilder.class)
public class BandwidthUsageBySsidReport extends AhAbstractNewReport {
	private static final String SUB_REPORT_WIFI0 = "wifi0";
	private static final String SUB_REPORT_WIFI1 = "wifi1";

	@Override
	public void init() {
		this.setReportTitle(MgrUtil.getUserMessage("report.title.text.bandwidth.usage.ssid"));
		this.setReportSummary(MgrUtil.getUserMessage("report.summary.text.bandwidth.usage.ssid"));
		if (this.isBlnGroupCal()) {
			this.addGroupReportEl(SUB_REPORT_WIFI0);
			this.getGroupReportEl(SUB_REPORT_WIFI0).setTitle("Bandwidth Usage by SSID(wifi0)");
			
			this.addGroupReportEl(SUB_REPORT_WIFI1);
			this.getGroupReportEl(SUB_REPORT_WIFI1).setTitle("Bandwidth Usage by SSID(wifi1)");
		}
	}
	
	@Override
	public void initReportChartExportEl() {
		this.setExportChartEl(new AhFreechartPie("MB"));
	}

	@Override
	protected void doCalculate() throws Exception {
		prepareChartData();
	}
	
	@SuppressWarnings("rawtypes")
	private void prepareChartData() throws Exception {
		if (this.getReportOptions().getApMacList() == null
				|| this.getReportOptions().getApMacList().isEmpty()) {
			return;
		}
		
		List<String> apMaces = new ArrayList<String>(this.getReportOptions().getApMacList());
		String where;
		FilterParams filter;
		if (this.getReportOptions().getSsidName().equalsIgnoreCase("All")) {
			where = "timeStamp >= :s1 and timeStamp <= :s2 and apMac in (:s3) and ssidName is not null";
			filter = new FilterParams(where,
				new Object[] {this.getStartTime(), this.getEndTime(), apMaces});
		} else {
			where = "timeStamp >= :s1 and timeStamp <= :s2 and apMac in (:s3) and ssidName=:s4";
			filter = new FilterParams(where,
				new Object[] {this.getStartTime(), this.getEndTime(), apMaces, this.getReportOptions().getSsidName()});
		}
		
		Class<? extends AhClientStatsInterf> boClass = 
			AhNewReportUtil.getClientStatsCertainImplementClass(this.getPeriodType(), this.getStartTime(), this.getEndTime(),this.getReportOptions().isScheduleMode());
		
		ReportPagingImpl<AhClientStatsInterf> page = new ReportPagingImpl(boClass);
		page.setPageSize(5000);
		page.clearRowCount();
		
		List<?> bos;
		Map<String, Long> resultData = new HashMap<String, Long>();
		
		while(page.hasNext()) {
			bos = page.next().executeQuery("select ssidName, txFrameByteCount, rxFrameByteCount from " + boClass.getSimpleName(),
					null, filter, this.getDomainId());
			
			for(Object bo : bos) {
				if(bo == null) {
					continue;
				}
				
				Object[] stat = (Object[])bo;
				String ssidName = stat[0] == null ? "N/A" : (String)stat[0];
				Long txByteCount = stat[1] == null ? 0L : (Long)stat[1];
				Long rxByteCount = stat[2] == null ? 0L : (Long)stat[2];
				
				
				if(stat == null || StringUtils.isBlank(ssidName)) {
					continue;
				}
				
				Long deltaValue = 0L;
				if (resultData.containsKey(ssidName)) {
					deltaValue = resultData.get(ssidName);
				}
				resultData.put(ssidName, txByteCount + rxByteCount + deltaValue);
			}
		}
		
		if (resultData != null
				&& !resultData.isEmpty()) {
			AhLinearSeries series = prepareSupportedSeries();
			Long diffTime = this.getEndTime() - this.getStartTime();
			diffTime = diffTime.compareTo(0L) > 0 ? diffTime : 1;
			List<Map.Entry<String, Long>> mapEntry = new ArrayList<Map.Entry<String, Long>>(resultData.entrySet());
			Collections.sort(mapEntry, new Comparator<Map.Entry<String, Long>>(){
				@Override
				public int compare(Map.Entry<String, Long> entry1, Map.Entry<String, Long> entry2) {
					return entry2.getValue().compareTo(entry1.getValue());
				}
			});
			if (mapEntry.size() > AhReportProperties.MAX_DISPLAY_DATA_COUNT_FOR_REPORT) {
				mapEntry = mapEntry.subList(0, AhReportProperties.MAX_DISPLAY_DATA_COUNT_FOR_REPORT);
			}
			Long totalData = 0L;
			for (Map.Entry<String, Long> entry : mapEntry) {
				totalData += entry.getValue();
			}
			for (Map.Entry<String, Long> entry : mapEntry) {
				series.addData(entry.getKey(), AhNewReportUtil.dataFromBToMB(entry.getValue()));
			}
		}
	}
	
	private AhLinearSeries prepareSupportedSeries() {
		AhLinearSeries series = new AhLinearSeries();
		this.addSeries(series);
		series.setId(1L);
		series.setName("Bandwidth");
		series.setShowType(AhReportProperties.SERIES_TYPE_PIE);
		
		return series;
	}
	
	protected void prepareSampleData() {
		String[] items = new String[]{"Employee", "Guest", "Contractor", "Partner", "Testing"};
		Integer[] values = new Integer[]{45, 25, 20, 6, 4};
		AhLinearSeries series = prepareSupportedSeries();
		for (int i = 0; i < items.length; i++) {
			series.addData(items[i], values[i]);
		}
	}
}
