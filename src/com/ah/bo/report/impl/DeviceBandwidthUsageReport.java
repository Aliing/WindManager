package com.ah.bo.report.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.ReportPagingImpl;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.AhInterfaceStatsInterf;
import com.ah.bo.report.AhLinearSeries;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.annotation.AhReportConfig;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.builder.DefaultNewReportBuilder;
import com.ah.util.bo.report.freechart.AhFreechartStackedBar;

@AhReportConfig(id=AhReportProperties.REPORT_BANDWIDTH_USAGE_OF_DEVICE,
		builder=DefaultNewReportBuilder.class)
public class DeviceBandwidthUsageReport extends AhAbstractNewReport {

	@Override
	public void init() {
		this.setReportValueTitle("Bandwidth Mbps");
		this.setReportCategoryTitle("Device");
		this.setReportTitle(MgrUtil.getUserMessage("report.title.text.bandwidth.usage.device"));
		this.setReportSummary(MgrUtil.getUserMessage("report.summary.text.bandwidth.usage.device"));
	}
	
	@Override
	public void initReportChartExportEl() {
		this.setExportChartEl(new AhFreechartStackedBar());
	}

	@Override
	protected void doCalculate() throws Exception {
		if (this.getRequest().isUseScaleArea()) {
			prepareScaleTimingData();
		} else {
			prepareChartData();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void prepareChartData() throws Exception {
		if (this.getReportOptions().getApMacList() == null
				|| this.getReportOptions().getApMacList().isEmpty()) {
			return;
		}
		
		List<String> apMaces = new ArrayList<String>(this.getReportOptions().getApMacList());
		SortParams sort = new SortParams("apMac");
		String where = "timeStamp >= :s1 and timeStamp <= :s2 and apMac in (:s3)";
		FilterParams filter = new FilterParams(where,
				new Object[] {this.getStartTime(), this.getEndTime(), apMaces});
		
		Class<? extends AhInterfaceStatsInterf> boClass = 
			AhNewReportUtil.getInterfaceStatsCertainImplementClass(this.getPeriodType(), this.getStartTime(), this.getEndTime(),this.getReportOptions().isScheduleMode());
		
		@SuppressWarnings("unchecked")
		ReportPagingImpl<AhInterfaceStatsInterf> page = new ReportPagingImpl(boClass);
		page.setPageSize(5000);
		page.clearRowCount();
		
		List<?> bos;
		Map<String, Map<String, Long>> resultData = new HashMap<String, Map<String, Long>>();
		Map<String, String> deviceMacAndName = new HashMap<String, String>();
		
		while(page.hasNext()) {
			bos = page.next().executeQuery("select apName, txByteCount, rxByteCount, radioType, apMac from " + boClass.getSimpleName(),
					sort, filter, this.getDomainId());
			
			for(Object bo : bos) {
				if(bo == null) {
					continue;
				}
				
				Object[] stat = (Object[])bo;
				String apName = stat[0] == null ? "N/A" : (String)stat[0];
				Long txByteCount = stat[1] == null ? 0L : (Long)stat[1];
				Long rxByteCount = stat[2] == null ? 0L : (Long)stat[2];
				String radioType = stat[3] == null ? String.valueOf(AhInterfaceStats.RADIOTYPE_24G) : String.valueOf(stat[3]);
				String apMac = stat[4] == null ? "N/A" : (String)stat[4];
				
				deviceMacAndName.put(apMac, apName);
				
				Long deltaValue = 0L;
				if (resultData.containsKey(apMac)) {
					Long valueTmp = resultData.get(apMac).get(radioType);
					deltaValue = valueTmp == null ? deltaValue : valueTmp;
				} else {
					Map<String, Long> mapTmp = new HashMap<String, Long>();
					setSupportedWifiRange(mapTmp, 0L);
					resultData.put(apMac, mapTmp);
				}
				if (resultData.get(apMac).containsKey(radioType)) {
					resultData.get(apMac).put(radioType, txByteCount + rxByteCount + deltaValue);
				}
			}
		}
		
		if (resultData != null
				&& !resultData.isEmpty()) {
			Map<String, AhLinearSeries> seriesMap = prepareSupportedSeries();
			
			Long diffTime = this.getEndTime() - this.getStartTime();
			diffTime = diffTime.compareTo(0L) > 0 ? diffTime : 1;
			List<Map.Entry<String, Map<String, Long>>> mapEntry = new ArrayList<Map.Entry<String, Map<String, Long>>>(resultData.entrySet());
			Collections.sort(mapEntry, new Comparator<Map.Entry<String, Map<String, Long>>>(){
				@Override
				public int compare(Map.Entry<String, Map<String, Long>> entry1, Map.Entry<String, Map<String, Long>> entry2) {
					return getSumMapValue(entry2.getValue()).compareTo(getSumMapValue(entry1.getValue()));
				}
			});
			if (mapEntry.size() > AhReportProperties.MAX_DISPLAY_DATA_COUNT_FOR_REPORT) {
				mapEntry = mapEntry.subList(0, AhReportProperties.MAX_DISPLAY_DATA_COUNT_FOR_REPORT);
			}
			for (Map.Entry<String, Map<String, Long>> entry : mapEntry) {
				if (deviceMacAndName.containsKey(entry.getKey())) {
					this.addCategories(deviceMacAndName.get(entry.getKey()));
				} else {
					this.addCategories(entry.getKey());
				}
				for (String key : entry.getValue().keySet()) {
					if (seriesMap.containsKey(key)) {
						seriesMap.get(key).addData(AhNewReportUtil.getDataBitFromByte(AhNewReportUtil.dataFromBToMB(entry.getValue().get(key)*1000, diffTime)));
					}
				}
			}
		}
	}
	
	private void prepareScaleTimingData() {
		// do nothing for now
	}
	
	private Map<String, AhLinearSeries> prepareSupportedSeries() {
		Map<String, AhLinearSeries> seriesMap = new HashMap<String, AhLinearSeries>();
		setSupportedWifiRange(seriesMap, null);
		Long curIndex = 1L;
		for (String key : seriesMap.keySet()) {
			AhLinearSeries seriesTmp = new AhLinearSeries();
			seriesTmp.setId(curIndex++);
			seriesTmp.setName(AhNewReportUtil.getShownNameForInterfStatsRadioType(key));
			seriesTmp.setStackGroup("Bandwidth");
			seriesTmp.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
			seriesMap.put(key, seriesTmp);
		}
		List<Map.Entry<String, AhLinearSeries>> seriesMapLst = new ArrayList<Map.Entry<String, AhLinearSeries>>(seriesMap.entrySet());
		Collections.sort(seriesMapLst, new Comparator<Map.Entry<String, AhLinearSeries>>() {
			@Override
			public int compare(Map.Entry<String, AhLinearSeries> entry1, Map.Entry<String, AhLinearSeries> entry2) {
				return entry2.getKey().compareTo(entry1.getKey());
			}
		});
		for (Map.Entry<String, AhLinearSeries> entry : seriesMapLst) {
			this.addSeries(entry.getValue());
		}
		
		return seriesMap;
	}
	
	@SuppressWarnings("unchecked")
	private void setSupportedWifiRange(@SuppressWarnings("rawtypes") Map map, Object defValue) {
		if (map == null) {
			return;
		}
		map.put(String.valueOf(AhInterfaceStats.RADIOTYPE_24G), defValue);
		map.put(String.valueOf(AhInterfaceStats.RADIOTYPE_5G), defValue);
	}
	
	private Long getSumMapValue(Map<String, Long> map) {
		if (map != null
				&& !map.isEmpty()) {
			Long result = 0L;
			for (Long value : map.values()) {
				result += value;
			}
			return result;
		}
		return 0L;
	}
	
	protected void prepareSampleData() {
		String[] devices = new String[] {"AP_10th_Grade","AP_9th_Grade","AP_Bldg5_25","AP_Gynasium1","AP_Conf_RmR",
				"AP_Assembly_Rm","AP_Bldg1_12","AP_Teachers_Rm","AP_Admin_Office","AP_Main_Library"};
		
		Map<String, AhLinearSeries> seriesMap = prepareSupportedSeries();
		double maxValue = 2.0;
		String ghz24str = String.valueOf(AhInterfaceStats.RADIOTYPE_24G);
		String ghz5str = String.valueOf(AhInterfaceStats.RADIOTYPE_5G);
		for (String device : devices) {
			this.addCategories(device);
			seriesMap.get(ghz24str).addData(AhNewReportUtil.dataFormatWithPrecision(maxValue, 2));
			seriesMap.get(ghz5str).addData(AhNewReportUtil.dataFormatWithPrecision(maxValue - 0.2, 2));
			maxValue = maxValue - 0.1;
		}
	}
}
