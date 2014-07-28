package com.ah.bo.report.impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.GroupByParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.ReportPagingImpl;
import com.ah.bo.performance.AhMaxClientsCount;
import com.ah.bo.performance.AhSsidClientsCountInterface;
import com.ah.bo.report.AhDatetimeSeries;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.annotation.AhReportConfig;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.builder.DefaultNewReportBuilder;
import com.ah.util.bo.report.freechart.AhFreechartDatetimeLine;

@AhReportConfig(id=AhReportProperties.REPORT_SSID_CLIENTS_OVER_TIME,
		builder=DefaultNewReportBuilder.class)
public class UsageSsidClientReport extends AhAbstractNewReport {
	
	@Override
	public void init() {
		this.setReportTitle(MgrUtil.getUserMessage("report.title.text.clients.per.ssid"));
		this.setReportValueTitle("Clients");
		this.setReportSummary(MgrUtil.getUserMessage("report.summary.text.clients.per.ssid"));
	}
	
	@Override
	public void initReportChartExportEl() {
		this.setExportChartEl(new AhFreechartDatetimeLine(true));
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
		long timePadding = AhNewReportUtil.getTimeIntervalPadding(600000L, tmpPeriodType, this.getStartTime(), this.getEndTime());
		
		Map<String,HashMap<Long, Long>> resultData = new HashMap<String,HashMap<Long, Long>>();
		
		SortParams sort = new SortParams("ssid, timeStamp asc");
		String where;
		FilterParams filter;
		if (this.getReportOptions().getSsidName().equalsIgnoreCase("All")) {
			where = "timeStamp>= :s1 AND timeStamp <= :s2 AND apMac in(:s3) AND radioMode !=:s4 AND owner.id = :s5";
			filter = new FilterParams(where,
					new Object[] {this.getStartTime(),
					this.getEndTime(),
					this.getReportOptions().getApMacList(),
					3,
					this.getDomainId()});
		} else {
			where = "timeStamp>= :s1 AND timeStamp <= :s2 AND apMac in(:s3) AND ssid=:s4 AND radioMode !=:s5 AND owner.id = :s6";
			filter = new FilterParams(where,
					new Object[] {this.getStartTime(),
					this.getEndTime(),
					this.getReportOptions().getApMacList(),
					this.getReportOptions().getSsidName(),
					3,
					this.getDomainId()});
		}
		GroupByParams groupBy = new GroupByParams(new String[]{"ssid", "timeStamp"});
		Class<? extends AhSsidClientsCountInterface> boClass = 
			AhNewReportUtil.getSsidClientsCountCertainImplementClass(this.getPeriodType(), this.getStartTime(), this.getEndTime(),this.getReportOptions().isScheduleMode());
				
		ReportPagingImpl<AhSsidClientsCountInterface> page = new ReportPagingImpl(boClass);
		page.setPageSize(5000);
		page.clearRowCount();
		
		List<?> bos;
		
		List<?> lineTimes = QueryUtil.executeQuery("select timeStamp from " + AhMaxClientsCount.class.getSimpleName(), 
				new SortParams("timeStamp"), new FilterParams("globalFlg=:s1 and timeStamp>=:s2 and timeStamp<=:s3",
						new Object[]{true,this.getStartTime(),this.getEndTime()}));
		
		Long curCountTime=0L;
		Long nextCountTime=0L;
		String curSsidName=null;
		int index=0;
		while(page.hasNext()) {
			bos = page.next().executeQuery("select ssid, timeStamp, sum(clientCount) ", sort, filter,groupBy);
			for(Object bo : bos) {
				if(bo == null) {
					continue;
				}
				Object[] oneRec = (Object[])bo;
				if (oneRec[0]==null ||StringUtils.isBlank(oneRec[0].toString())){
					continue;
				}
				String ssid = oneRec[0].toString();
				Long time = Long.valueOf(oneRec[1].toString());
				Long count = Long.valueOf(oneRec[2].toString());
				
				if (curSsidName == null || !curSsidName.equals(ssid)) {
					curSsidName =ssid; 
					index=0;
					if (timePadding==600000 && lineTimes.size()>(index+1)) {
						curCountTime = (Long)lineTimes.get(index++);
						nextCountTime = (Long)lineTimes.get(index++);
					} else {
						curCountTime = this.getStartTime();
						nextCountTime = this.getStartTime() + timePadding;
					}
				}

				while (time >= nextCountTime) {
					curCountTime = nextCountTime;
					if (timePadding==600000 && lineTimes.size()>(index)) {
						nextCountTime = (Long)lineTimes.get(index++);
					} else {
						nextCountTime += timePadding; 
					}
				}
				
				Long deltaValue = 0L;
				if (resultData.get(curSsidName)!=null && resultData.get(curSsidName).containsKey(curCountTime)) {
					deltaValue = resultData.get(curSsidName).get(curCountTime);
				}
				
				if (resultData.get(curSsidName)==null) {
					HashMap<Long, Long> map = new HashMap<Long, Long>();
					map.put(curCountTime, count > deltaValue? count:deltaValue);
					resultData.put(curSsidName, map);
				} else {
					resultData.get(curSsidName).put(curCountTime, count > deltaValue? count:deltaValue);
				}
			}
		}
		
	
		if (resultData != null && !resultData.isEmpty()) {
			fillNullValuesWithPadding(resultData, this.getStartTime(), this.getEndTime(), timePadding, lineTimes);
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
				long avgData = 0L;
				Long totalData = 0L;
				
				for (Map.Entry<Long, Long> entry : mapEntry) {
					series.addData(entry.getKey(), entry.getValue());
					if (entry.getValue().compareTo(totalData)>0) {
						totalData = entry.getValue();
					}
					avgData = avgData + entry.getValue();
				}
				avgData = avgData/mapEntry.size();
				series.addSummary(totalData + " - Maximum Clients");
				series.addSummary(avgData + " - Average Clients");
			}
		}

	}

	private void fillNullValuesWithPadding(Map<String,HashMap<Long, Long>> map, Long startTime, Long endTime, Long padding, List<?> lineTimes) {
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
			int index=0;
			Long curTime  = startTime;
			if (padding==600000 && lineTimes.size()>index) {
				curTime = (Long)lineTimes.get(index++);
			}
			while (curTime <= endTime) {
				if (!mapItem.containsKey(curTime)) {
					mapItem.put(curTime, 0L);
				}
				if (padding==600000 && lineTimes.size()>index) {
					curTime = (Long)lineTimes.get(index++);
				} else {
					curTime += padding;
				}
				
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
		Integer[] clientCounts = new Integer[]{101, 99, 87, 92, 84, 75, 93};
		
		Long serId = 1L;
		int pointsCount = timePoints.size();
		int clientLen = clientCounts.length;
		int curSsidIdx = 0;
		for (String ssid : ssids) {
			AhDatetimeSeries seriesTmp = new AhDatetimeSeries();
			this.addSeries(seriesTmp);
			seriesTmp.setId(serId++);
			seriesTmp.setName(ssid);
			seriesTmp.setShowType(AhReportProperties.SERIES_TYPE_LINE);
			
			Integer totalData = 0;
			Integer maxData = 0;
			
			int curIdx = 0;
			try {
				for (Long timePoint : timePoints) {
					Integer dataTmp = clientCounts[curIdx++%clientLen] - curSsidIdx*8;
					seriesTmp.addData(timePoint, dataTmp);
					if (dataTmp > maxData) {
						maxData = dataTmp;
					}
					totalData += dataTmp;
				}
			} catch (Exception e) {
				
			}
			
			seriesTmp.addSummary(maxData + " - Maximum Clients");
			seriesTmp.addSummary(totalData/pointsCount + " - Average Clients");
			
			curSsidIdx++;
		}
	}
}
