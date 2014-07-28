package com.ah.bo.report.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.ReportPagingImpl;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.AhInterfaceStatsInterf;
import com.ah.bo.report.AhDatetimeSeries;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.annotation.AhReportConfig;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.builder.DefaultNewReportBuilder;
import com.ah.util.bo.report.freechart.AhFreechartDatetimeLine;

@AhReportConfig(id=AhReportProperties.REPORT_BANDWIDTH_OVER_TIME,
		builder=DefaultNewReportBuilder.class,
		groupCalEnabled=false)
public class BandwidthOverTimeReport extends AhAbstractNewReport {
	
	public static final String SUB_TYPE_OUTBOUND = "out";
	public static final String SUB_TYPE_INBOUND = "in";
	private boolean blnCountOut = false;

	@Override
	public void init() {
		this.setReportValueTitle("Mbps");
		if (SUB_TYPE_OUTBOUND.equals(this.getRequest().getSubType())) blnCountOut = true;
		if (blnCountOut) {
			this.setReportTitle(MgrUtil.getUserMessage("report.title.text.bandwidth.out.over.time"));
			this.setReportSummary(MgrUtil.getUserMessage("report.summary.text.bandwidth.out.over.time"));
		} else {
			this.setReportTitle(MgrUtil.getUserMessage("report.title.text.bandwidth.in.over.time"));
			this.setReportSummary(MgrUtil.getUserMessage("report.summary.text.bandwidth.in.over.time"));
		}
	}
	
	@Override
	public void initReportChartExportEl() {
		this.setExportChartEl(new AhFreechartDatetimeLine());
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
		
		int tmpPeriodType = this.getPeriodType();
		if (this.getReportOptions().isScheduleMode()) {
			tmpPeriodType=-2;
		}
		
		Long timePadding = AhNewReportUtil.getTimeIntervalPadding(600000L, tmpPeriodType, this.getStartTime(), this.getEndTime());
		
		List<String> apMaces = new ArrayList<String>(this.getReportOptions().getApMacList());
		SortParams sort = new SortParams("timeStamp");
		String where = "timeStamp > :s1 and timeStamp <= :s2 and apMac in (:s3)";
		FilterParams filter = new FilterParams(where,
				new Object[] {this.getStartTime() - timePadding, this.getEndTime(), apMaces});
		
		Class<? extends AhInterfaceStatsInterf> boClass = 
			AhNewReportUtil.getInterfaceStatsCertainImplementClass(this.getPeriodType(), this.getStartTime(), this.getEndTime(), this.getReportOptions().isScheduleMode());
		
		@SuppressWarnings("unchecked")
		ReportPagingImpl<AhInterfaceStatsInterf> page = new ReportPagingImpl(boClass);
		page.setPageSize(5000);
		page.clearRowCount();
		
		List<?> bos;
		Map<Long, Map<String, Long>> resultData = new HashMap<Long, Map<String, Long>>();
		
		Long curCountTime = this.getStartTime();
		Long nextCountTime = this.getStartTime() + timePadding;
		Long totalData = 0L;
		Map<String, Long> wifiTotalData = new HashMap<String, Long>();
		
		setSupportedWifiRange(wifiTotalData, 0L);
		
		while(page.hasNext()) {
			bos = page.next().executeQuery("select apName, txByteCount, rxByteCount, timeStamp, radioType from " + boClass.getSimpleName(),
					sort, filter, this.getDomainId());
			
			for(Object bo : bos) {
				if(bo == null) {
					continue;
				}
				
				Object[] stat = (Object[])bo;
				Long txByteCount = stat[1] == null ? 0L : (Long)stat[1];
				Long rxByteCount = stat[2] == null ? 0L : (Long)stat[2];
				Long recordTime = stat[3] == null ? this.getStartTime() : (Long)stat[3];
				String radioType = stat[4] == null ? String.valueOf(AhInterfaceStats.RADIOTYPE_24G) : String.valueOf(stat[4]);
				
				while (recordTime >= curCountTime) {
					curCountTime = nextCountTime;
					nextCountTime += timePadding; 
				}
				
				Long curCountData = blnCountOut?txByteCount:rxByteCount;
				totalData += curCountData;
				
				Long deltaValue = 0L;
				if (resultData.containsKey(curCountTime)) {
					Long valueTmp = resultData.get(curCountTime).get(radioType);
					deltaValue = valueTmp == null ? deltaValue : valueTmp;
				} else {
					Map<String, Long> mapTmp = new HashMap<String, Long>();
					setSupportedWifiRange(mapTmp, 0L);
					resultData.put(curCountTime, mapTmp);
				}
				if (resultData.get(curCountTime).containsKey(radioType)) {
					resultData.get(curCountTime).put(radioType, curCountData + deltaValue);
				}
				
				Long wifiTotalDeltaValue = 0L;
				if (wifiTotalData.containsKey(radioType)) {
					wifiTotalDeltaValue = wifiTotalData.get(radioType);
					wifiTotalData.put(radioType, wifiTotalDeltaValue + curCountData);
				}
			}
		}
		
		if (resultData != null
				&& !resultData.isEmpty()) {
			AhNewReportUtil.fillNullValuesWithPadding(resultData, this.getStartTime(), this.getEndTime(), timePadding);

			Long diffTime = this.getEndTime() - this.getStartTime();
			diffTime = diffTime.compareTo(0L) > 0 ? diffTime : 1;
			
			AhDatetimeSeries seriesTotal = new AhDatetimeSeries();
			this.addSeries(seriesTotal);
			seriesTotal.setId(1L);
			seriesTotal.setName("Total");
			seriesTotal.setShowType(AhReportProperties.SERIES_TYPE_LINE);
			double avgData = AhNewReportUtil.dataFromBToMB(totalData*1000, diffTime + timePadding/2);
			seriesTotal.addSummary("Total: " + AhNewReportUtil.dataFromBToGB(totalData) + AhNewReportUtil.getFormattedDataUnit("GB"));
			seriesTotal.addSummary("Average: " + AhNewReportUtil.getDataBitFromByte(avgData) + AhNewReportUtil.getFormattedDataUnit("Mbps"));
			
			List<Map.Entry<Long, Map<String, Long>>> mapEntry = new ArrayList<Map.Entry<Long, Map<String, Long>>>(resultData.entrySet());
			Collections.sort(mapEntry, new Comparator<Map.Entry<Long, Map<String, Long>>>(){
				@Override
				public int compare(Map.Entry<Long, Map<String, Long>> entry1, Map.Entry<Long, Map<String, Long>> entry2) {
					return entry1.getKey().compareTo(entry2.getKey());
				}
			});
			
			Map<String, AhDatetimeSeries> subSeries = new HashMap<String, AhDatetimeSeries>();
			setSupportedWifiRange(subSeries, null);
			Long curSeriesId = 2L;
			for (String keyValue : subSeries.keySet()) {
				AhDatetimeSeries tmpSeries = new AhDatetimeSeries();
				tmpSeries.setId(curSeriesId++);
				tmpSeries.setName(AhNewReportUtil.getShownNameForInterfStatsRadioType(keyValue));
				tmpSeries.setShowType(AhReportProperties.SERIES_TYPE_LINE);
				Long totalWifiDataTmp = wifiTotalData.get(keyValue);
				totalWifiDataTmp = totalWifiDataTmp == null ? 0L : totalWifiDataTmp;
				tmpSeries.addSummary("Total: " + AhNewReportUtil.dataFromBToGB(totalWifiDataTmp) + AhNewReportUtil.getFormattedDataUnit("GB"));
				tmpSeries.addSummary("Average: " + AhNewReportUtil.getDataBitFromByte(AhNewReportUtil.dataFromBToMB(totalWifiDataTmp*1000, diffTime + timePadding/2)) + AhNewReportUtil.getFormattedDataUnit("Mbps"));
				subSeries.put(keyValue, tmpSeries);
			}
			for (AhDatetimeSeries seriesValue : subSeries.values()) {
				this.addSeries(seriesValue);
			}
			
			for (Map.Entry<Long, Map<String, Long>> entry : mapEntry) {
				Long keyTmp = entry.getKey();
				Long totalTmp = 0L;
				Map<String, Long> wifixMap = entry.getValue();
				if (wifixMap != null) {
					for (String wifiTmp : wifixMap.keySet()) {
						totalTmp += wifixMap.get(wifiTmp);
						if (subSeries.containsKey(wifiTmp)) {
							subSeries.get(wifiTmp).addData(keyTmp, AhNewReportUtil.getDataBitFromByte(AhNewReportUtil.dataFromBToMB(wifixMap.get(wifiTmp)*1000, timePadding)));
						}
					}
				} else {
					wifixMap = new HashMap<String, Long>();
					setSupportedWifiRange(wifixMap, 0L);
					for (String wifiTmp : wifixMap.keySet()) {
						if (subSeries.containsKey(wifiTmp)) {
							subSeries.get(wifiTmp).addData(keyTmp, AhNewReportUtil.getDataBitFromByte(AhNewReportUtil.dataFromBToMB(wifixMap.get(wifiTmp)*1000, timePadding)));
						}
					}
				}
				seriesTotal.addData(keyTmp, AhNewReportUtil.getDataBitFromByte(AhNewReportUtil.dataFromBToMB(totalTmp*1000, timePadding)));
			}
		}
	}
	
	private void prepareScaleTimingData() {
		// do nothing for now
	}

	@SuppressWarnings("unchecked")
	private void setSupportedWifiRange(@SuppressWarnings("rawtypes") Map map, Object defValue) {
		if (map == null) {
			return;
		}
		map.put(String.valueOf(AhInterfaceStats.RADIOTYPE_24G), defValue);
		map.put(String.valueOf(AhInterfaceStats.RADIOTYPE_5G), defValue);
	}
	
	protected void prepareSampleData() {
		int tmpPeriodType = this.getPeriodType();
		if (this.getReportOptions().isScheduleMode()) {
			tmpPeriodType=-2;
		}
		Long timePadding = AhNewReportUtil.getTimeIntervalPadding(600000L, tmpPeriodType, this.getStartTime(), this.getEndTime());
		Long curCountTime = this.getStartTime();
		List<Long> timePoints = new LinkedList<Long>();
		while (curCountTime <= this.getEndTime()) {
			timePoints.add(curCountTime);
			curCountTime += timePadding;
		}
		
		Long[] dataPoints = new Long[]{0L, 4L, 5L, 9L, 13L, 18L, 8L, 6L, 6L};
		if (!blnCountOut) {
			for (int i = 0; i < dataPoints.length; i++) {
				dataPoints[i] += 2L;
			}
		}
		for (int i = 0; i < dataPoints.length; i++) {
			dataPoints[i] *= 1024*1024*timePadding/2000;
		}
		
		Map<Long, Long> wifi0Data = new HashMap<Long, Long>(timePoints.size());
		Map<Long, Long> wifi1Data = new HashMap<Long, Long>(timePoints.size());
		
		int curIdx = 0;
		int dataLen = dataPoints.length;
		for (Long timePoint : timePoints) {
			wifi0Data.put(timePoint, dataPoints[curIdx++%dataLen]);
			wifi1Data.put(timePoint, dataPoints[curIdx++%dataLen] + 2L);
		}
		
		String ghz24str = String.valueOf(AhInterfaceStats.RADIOTYPE_24G);
		String ghz5str = String.valueOf(AhInterfaceStats.RADIOTYPE_5G);
		Map<String, Long> wifiTotalData = new HashMap<String, Long>();
		setSupportedWifiRange(wifiTotalData, 0L);
		for (Long key : wifi0Data.keySet()) {
			wifiTotalData.put(ghz24str, wifiTotalData.get(ghz24str) + wifi0Data.get(key));
		}
		for (Long key : wifi1Data.keySet()) {
			wifiTotalData.put(ghz5str, wifiTotalData.get(ghz5str) + wifi1Data.get(key));
		}
		Long totalData = wifiTotalData.get(ghz24str) + wifiTotalData.get(ghz5str);
		
		Long diffTime = this.getEndTime() - this.getStartTime();
		diffTime = diffTime.compareTo(0L) > 0 ? diffTime : 1L;
		
		AhDatetimeSeries seriesTotal = new AhDatetimeSeries();
		this.addSeries(seriesTotal);
		seriesTotal.setId(1L);
		seriesTotal.setName("Total");
		seriesTotal.setShowType(AhReportProperties.SERIES_TYPE_LINE);
		double avgData = AhNewReportUtil.dataFromBToMB(totalData*1000, diffTime);
		seriesTotal.addSummary("Total: " + AhNewReportUtil.dataFromBToGB(totalData) + AhNewReportUtil.getFormattedDataUnit("GB"));
		seriesTotal.addSummary("Average: " + avgData + AhNewReportUtil.getFormattedDataUnit("Mbps"));
		
		List<Map.Entry<Long, Long>> mapEntry = new ArrayList<Map.Entry<Long, Long>>(wifi0Data.entrySet());
		Collections.sort(mapEntry, new Comparator<Map.Entry<Long, Long>>(){
			@Override
			public int compare(Map.Entry<Long, Long> entry1, Map.Entry<Long, Long> entry2) {
				return entry1.getKey().compareTo(entry2.getKey());
			}
		});
		
		Map<String, AhDatetimeSeries> subSeries = new HashMap<String, AhDatetimeSeries>();
		setSupportedWifiRange(subSeries, null);
		Long curSeriesId = 2L;
		for (String keyValue : subSeries.keySet()) {
			AhDatetimeSeries tmpSeries = new AhDatetimeSeries();
			tmpSeries.setId(curSeriesId++);
			tmpSeries.setName(AhNewReportUtil.getShownNameForInterfStatsRadioType(keyValue));
			tmpSeries.setShowType(AhReportProperties.SERIES_TYPE_LINE);
			Long totalWifiDataTmp = wifiTotalData.get(keyValue);
			totalWifiDataTmp = totalWifiDataTmp == null ? 0L : totalWifiDataTmp;
			tmpSeries.addSummary("Total: " + AhNewReportUtil.dataFromBToGB(totalWifiDataTmp) + AhNewReportUtil.getFormattedDataUnit("GB"));
			tmpSeries.addSummary("Average: " + AhNewReportUtil.dataFromBToMB(totalWifiDataTmp*1000, diffTime + timePadding/2) + AhNewReportUtil.getFormattedDataUnit("Mbps"));
			subSeries.put(keyValue, tmpSeries);
		}
		for (AhDatetimeSeries seriesValue : subSeries.values()) {
			this.addSeries(seriesValue);
		}
		
		for (Map.Entry<Long, Long> entry : mapEntry) {
			try {
				subSeries.get(ghz24str).addData(entry.getKey(), AhNewReportUtil.dataFromBToMB(entry.getValue()*1000, timePadding));
				subSeries.get(ghz5str).addData(entry.getKey(), AhNewReportUtil.dataFromBToMB(wifi1Data.get(entry.getKey())*1000, timePadding));
				seriesTotal.addData(entry.getKey(), AhNewReportUtil.dataFromBToMB((entry.getValue() + wifi1Data.get(entry.getKey()))*1000, timePadding));
			} catch (Exception e) {
				
			}
		}
	}
	
}
