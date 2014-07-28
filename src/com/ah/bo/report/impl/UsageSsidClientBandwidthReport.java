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
import com.ah.bo.performance.AhClientStatsInterf;
import com.ah.bo.report.AhDatetimeSeries;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.annotation.AhReportConfig;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.builder.DefaultNewReportBuilder;
import com.ah.util.bo.report.freechart.AhFreechartDatetimeLine;

@AhReportConfig(id=AhReportProperties.REPORT_SSID_CLIENTS_BANDWIDTH,
		builder=DefaultNewReportBuilder.class)
public class UsageSsidClientBandwidthReport extends AhAbstractNewReport {
	
	@Override
	public void init() {
		this.setReportTitle(MgrUtil.getUserMessage("report.title.text.bandwidth.per.ssid"));
		this.setReportValueTitle("Kbps");
		this.setReportSummary(MgrUtil.getUserMessage("report.summary.text.bandwidth.per.ssid"));
	}
	
	@Override
	public void initReportChartExportEl() {
		this.setExportChartEl(new AhFreechartDatetimeLine());
	}

	@Override
	protected void doCalculate() throws Exception {
		prepareDataSeries();
	}

	private void prepareDataSeries() throws Exception {
		prepareTimeScaleRequest();
	}

	@SuppressWarnings("unchecked")
	private void prepareTimeScaleRequest() throws Exception {
		if (this.getReportOptions().getApMacList() == null
				|| this.getReportOptions().getApMacList().isEmpty()) {
			return;
		}
		
		int tmpPeriodType = this.getPeriodType();
		if (this.getReportOptions().isScheduleMode()) {
			tmpPeriodType=-2;
		}
		Long timePadding = AhNewReportUtil.getTimeIntervalPadding(600000L, tmpPeriodType, this.getStartTime(), this.getEndTime());

		
		List<String> apMacs = new ArrayList<String>(this.getReportOptions().getApMacList());
		SortParams sort = new SortParams("ssidName,timeStamp");
		String where;
		FilterParams filter;
		if (this.getReportOptions().getSsidName().equalsIgnoreCase("All")) {
			where = "timeStamp > :s1 and timeStamp <= :s2 and apMac in (:s3) and ssidName is not null";
			filter = new FilterParams(where,
				new Object[] {this.getStartTime(), this.getEndTime(), apMacs});
		} else {
			where = "timeStamp > :s1 and timeStamp <= :s2 and apMac in (:s3) and ssidName=:s4";
			filter = new FilterParams(where,
				new Object[] {this.getStartTime() - timePadding, this.getEndTime(), apMacs, this.getReportOptions().getSsidName()});
		}
		Class<? extends AhClientStatsInterf> boClass = 
			AhNewReportUtil.getClientStatsCertainImplementClass(this.getPeriodType(), this.getStartTime(), this.getEndTime(),this.getReportOptions().isScheduleMode());
		
		ReportPagingImpl<AhClientStatsInterf> page = new ReportPagingImpl(boClass);
		page.setPageSize(5000);
		page.clearRowCount();
		
		List<?> bos;
		Map<String,HashMap<Long, Long>> resultData = new HashMap<String,HashMap<Long, Long>>();
		Map<String,Long> resultSizeCont = new HashMap<String,Long>();
		
		Long curCountTime=0L;
		Long nextCountTime=0L;
		String curSsidName=null;
		
		while(page.hasNext()) {
			bos = page.next().executeQuery("select ssidName, txFrameByteCount, rxFrameByteCount, timeStamp from " + boClass.getSimpleName(),
					sort, filter, this.getDomainId());
			
			for(Object bo : bos) {
				if(bo == null) {
					continue;
				}
				
				Object[] stat = (Object[])bo;
				String ssidName = stat[0] == null ? "N/A" : (String)stat[0];
				Long txByteCount = stat[1] == null ? 0L : (Long)stat[1];
				Long rxByteCount = stat[2] == null ? 0L : (Long)stat[2];
				Long recordTime = stat[3] == null ? this.getStartTime() : (Long)stat[3];
				
				if (curSsidName == null || !curSsidName.equals(ssidName)) {
					curSsidName =ssidName; 
					curCountTime = this.getStartTime();
					nextCountTime = this.getStartTime() + timePadding;
				}

				while (recordTime > curCountTime) {
					curCountTime = nextCountTime;
					nextCountTime += timePadding; 
				}
				
				Long deltaValue = 0L;
				if (resultData.get(curSsidName)!=null && resultData.get(curSsidName).containsKey(curCountTime)) {
					deltaValue = resultData.get(curSsidName).get(curCountTime);
				}
				Long curCountData = txByteCount + rxByteCount;
				if (resultData.get(curSsidName)==null) {
					HashMap<Long, Long> map = new HashMap<Long, Long>();
					map.put(curCountTime, curCountData + deltaValue);
					resultData.put(curSsidName, map);
				} else {
					resultData.get(curSsidName).put(curCountTime, curCountData + deltaValue);
				}
			}
		}
		
		if (resultData != null && !resultData.isEmpty()) {
			for (String ssid: resultData.keySet()){
				resultSizeCont.put(ssid, Long.valueOf(resultData.get(ssid).size()));
			}
			fillNullValuesWithPadding(resultData, this.getStartTime(), this.getEndTime(), timePadding);
			Long serId=1L;
			for(String ssid:resultData.keySet()){
				if (serId>20) {break;}
				AhDatetimeSeries series = new AhDatetimeSeries();
				this.addSeries(series);
				series.setId(serId++);
				series.setName(ssid);
				series.setShowType(AhReportProperties.SERIES_TYPE_LINE);
				
				List<Map.Entry<Long, Long>> mapEntry = new ArrayList<Map.Entry<Long, Long>>(resultData.get(ssid).entrySet());
				Collections.sort(mapEntry, new Comparator<Map.Entry<Long, Long>>(){
					@Override
					public int compare(Map.Entry<Long, Long> entry1, Map.Entry<Long, Long> entry2) {
						return entry1.getKey().compareTo(entry2.getKey());
					}
				});
				long seTotalData=0L;
				for (Map.Entry<Long, Long> entry : mapEntry) {
					series.addData(entry.getKey(), AhNewReportUtil.getDataBitFromByte(AhNewReportUtil.dataFromBToKB(entry.getValue()* 1000,timePadding)));
					seTotalData+=entry.getValue();
				}
								
				long seAvgData = resultSizeCont.get(ssid) ==0 ? 0 : seTotalData/resultSizeCont.get(ssid);
				series.addSummary(AhNewReportUtil.dataFromBToMB(seTotalData) + " - Total MB");
				series.addSummary(AhNewReportUtil.getDataBitFromByte(AhNewReportUtil.dataFromBToKB(seAvgData * 1000,timePadding))  + " - Average Kbps");
			}
		}
	}

	private void fillNullValuesWithPadding(Map<String,HashMap<Long, Long>> map, Long startTime, Long endTime, Long padding) {
		if (map == null
				|| startTime == null
				|| endTime == null
				|| padding == null) {
			return;
		}
		int timeCResult = startTime.compareTo(endTime);
		int paddingCResult = padding.compareTo(0L);
		if (timeCResult == 0 || paddingCResult == 0) {
			return;
		} else if (timeCResult > 0) {
			if (paddingCResult > 0) {
				return;
			}
		} else {
			if (paddingCResult < 0) {
				return;
			}
		}
		
		for (Map<Long, Long> mapItem : map.values()){
			Long curTime = startTime;
			while (curTime <= endTime) {
				if (!mapItem.containsKey(curTime)) {
					mapItem.put(curTime, 0L);
				}
				curTime += padding;
			}
		}
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
		
		String[] ssids = new String[]{"Employee", "Guest", "Contractor", "Partner", "Testing"};
		Long[] values = new Long[]{101L, 99L, 87L, 92L, 84L, 75L, 93L};
		
		Long serId = 1L;
		int valueLen = values.length;
		int pointsCount = timePoints.size();
		Long curSsidIdx = 0L;
		for (String ssid : ssids) {
			AhDatetimeSeries seriesTmp = new AhDatetimeSeries();
			this.addSeries(seriesTmp);
			seriesTmp.setId(serId++);
			seriesTmp.setName(ssid);
			seriesTmp.setShowType(AhReportProperties.SERIES_TYPE_LINE);
			
			Long totalData = 0L;
			
			int curIdx = 0;
			try {
				for (Long timePoint : timePoints) {
					Long dataTmp = values[curIdx++%valueLen] - curSsidIdx*8;
					seriesTmp.addData(timePoint, dataTmp);
					totalData += dataTmp;
				}
			} catch (Exception e) {
				
			}
			
			seriesTmp.addSummary(AhNewReportUtil.dataFromBToKB(totalData) + " - Total MB");
			seriesTmp.addSummary(AhNewReportUtil.dataFormatWithPrecision(totalData*1.0/pointsCount, 2)  + " - Average Kbps");
			
			curSsidIdx++;
		}
	}
	
}
