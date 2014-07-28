package com.ah.bo.report.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.ReportPagingImpl;
import com.ah.bo.performance.AhMaxClientsCount;
import com.ah.bo.performance.AhSsidClientsCount;
import com.ah.bo.performance.AhSsidClientsCountInterface;
import com.ah.bo.report.AhDatetimeSeries;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.annotation.AhReportConfig;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.AhNewReportUtil.RadioModeType;
import com.ah.util.bo.report.builder.DefaultNewReportBuilder;
import com.ah.util.bo.report.freechart.AhFreechartDatetimeLine;

@AhReportConfig(id=AhReportProperties.REPORT_CLIENTS_NUMBER_OVER_TIME,
		builder=DefaultNewReportBuilder.class)
public class ClientsNumberOverTimeReport extends AhAbstractNewReport {

	@Override
	public void init() {
		this.setReportValueTitle("Concurrent Clients");
		this.setReportTitle(MgrUtil.getUserMessage("report.title.text.clients.over.time"));
		this.setReportSummary(MgrUtil.getUserMessage("report.summary.text.clients.over.time"));
	}
	
	@Override
	public void initReportChartExportEl() {
		this.setExportChartEl(new AhFreechartDatetimeLine(true));
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
		SortParams sort = new SortParams("timeStamp");
		String where;
		FilterParams filter;
		
		Class<? extends AhSsidClientsCountInterface> boClass = 
			AhNewReportUtil.getSsidClientsCountCertainImplementClass(this.getPeriodType(), this.getStartTime(), this.getEndTime(),this.getReportOptions().isScheduleMode());
		boolean blnOriginalData = false;
		if (boClass.getName().equals(AhSsidClientsCount.class.getName())) {
			blnOriginalData = true;
		}
		
		ReportPagingImpl<AhSsidClientsCountInterface> page = new ReportPagingImpl(boClass);
		page.setPageSize(5000);
		page.clearRowCount();
		
		List<?> bos;
		Map<Long, Map<RadioModeType, Map<String, Integer>>> resultData = new HashMap<Long, Map<RadioModeType, Map<String, Integer>>>();
		int tmpPeriodType = this.getPeriodType();
		if (this.getReportOptions().isScheduleMode()) {
			tmpPeriodType=-2;
		}
		Long timePadding = AhNewReportUtil.getTimeIntervalPadding(600000L, tmpPeriodType, this.getStartTime(), this.getEndTime());
		Long curCountTime = this.getStartTime();
		Long nextCountTime = this.getStartTime() + timePadding;
		
		Long calStartTime = this.getStartTime() - timePadding;
		Long calEndTime = this.getEndTime();
		
		List<Long> timeTicks = null;
		if (blnOriginalData) {
			timeTicks = getExactTimeTicks();
		}
		int timeTicksCount = timeTicks == null ? 0 : timeTicks.size();
		int curTimeTickIdx = 0;
		if (timeTicksCount > 1) {
			curCountTime = timeTicks.get(0);
			nextCountTime = timeTicks.get(1);
			curTimeTickIdx++;
			calStartTime = timeTicks.get(0);
			calEndTime = timeTicks.get(timeTicks.size()-1);
		}
		Set<Long> usedTimeTicks = new HashSet<Long>();
		
		if (this.getReportOptions().getSsidName().equalsIgnoreCase("All")) {
			where = "timeStamp >= :s1 and timeStamp <= :s2 and apMac in (:s3) and ssid is not null";
			filter = new FilterParams(where,
				new Object[] {calStartTime, calEndTime, apMaces});
		} else {
			where = "timeStamp >= :s1 and timeStamp <= :s2 and apMac in (:s3) and ssid=:s4";
			filter = new FilterParams(where,
				new Object[] {calStartTime, calEndTime, apMaces, this.getReportOptions().getSsidName()});
		}
		
		while(page.hasNext()) {
			bos = page.next().executeQuery("select radioMode, clientCount, apMac, ssid, timeStamp  from " + boClass.getSimpleName(),
					sort, filter, this.getDomainId());
			
			for(Object bo : bos) {
				if(bo == null) {
					continue;
				}
				
				Object[] stat = (Object[])bo;
				int colIndex = 0;
				Integer radioMode = stat[colIndex] == null ? 1 : (Integer)stat[colIndex];
				colIndex++; Integer clientCount = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; String apMac = stat[colIndex] == null ? "N/A" : (String)stat[colIndex];
				colIndex++; String ssid = stat[colIndex] == null ? "N/A" : (String)stat[colIndex];
				colIndex++; Long recordTime = stat[colIndex] == null ? curCountTime : (Long)stat[colIndex];
				
				if (!blnOriginalData || timeTicksCount < 1) {
					while (recordTime >= curCountTime) {
						curCountTime = nextCountTime;
						nextCountTime += timePadding; 
					}
				} else {
					while (recordTime >= nextCountTime) {
						if (curTimeTickIdx > timeTicksCount - 1) {
							curCountTime = nextCountTime;
							nextCountTime += timePadding; 
						} else if (curTimeTickIdx == timeTicksCount - 1) {
							curCountTime = timeTicks.get(curTimeTickIdx);
							nextCountTime = curCountTime + timePadding;
							curTimeTickIdx++;
						} else {
							curCountTime = timeTicks.get(curTimeTickIdx);
							nextCountTime = timeTicks.get(curTimeTickIdx + 1);
							curTimeTickIdx++;
						}
					}
				}
				
				String apSsidMark = apMac + "_" + ssid;
				RadioModeType radioModeType = AhNewReportUtil.getRadioModeType(radioMode);
				Integer deltaValue = -1;
				if (resultData.containsKey(curCountTime)) {
					if (resultData.get(curCountTime).containsKey(radioModeType)) {
						Map<String, Integer> mapTmp = resultData.get(curCountTime).get(radioModeType);
						if (mapTmp != null
								&& mapTmp.containsKey(apSsidMark)) {
							deltaValue = mapTmp.get(apSsidMark);
						}
					}
				} else {
					Map<RadioModeType, Map<String, Integer>> mapTmp = new HashMap<RadioModeType, Map<String, Integer>>();
					setSupportedClientTypeRange(mapTmp, null);
					resultData.put(curCountTime, mapTmp);
					usedTimeTicks.add(curCountTime);
				}
				if (clientCount.compareTo(deltaValue) > 0
						&& resultData.get(curCountTime).containsKey(radioModeType)) {
					if (resultData.get(curCountTime).get(radioModeType) == null) {
						resultData.get(curCountTime).put(radioModeType, new HashMap<String, Integer>());
					}
					resultData.get(curCountTime).get(radioModeType).put(apSsidMark, clientCount);
				}
			}
		}
		
		if (blnOriginalData
				&& timeTicks != null) {
			for (Long timetick : timeTicks) {
				if (!usedTimeTicks.contains(timetick)) {
					resultData.put(timetick, null);
				}
			}
		}
		
		if (resultData == null
				|| resultData.isEmpty()) return;
		
		Integer totalClients = 0;
		Integer maxClients = 0;
		Map<RadioModeType, Integer> maxClientsMap = new HashMap<RadioModeType, Integer>();
		Map<RadioModeType, Integer> totalClientsMap = new HashMap<RadioModeType, Integer>();
		setSupportedClientTypeRange(maxClientsMap, 0);
		setSupportedClientTypeRange(totalClientsMap, 0);
		for (Iterator<Long> iter = resultData.keySet().iterator(); iter.hasNext();) {
			Long keyId = iter.next();
			Integer tmpTotalData = 0;
			Map<RadioModeType, Map<String, Integer>> mapTmp = resultData.get(keyId);
			if (mapTmp == null) continue;
			for (Map.Entry<RadioModeType, Map<String, Integer>> entry : mapTmp.entrySet()) {
				Integer maxTmp = getSumMap(entry.getValue());
				if (maxTmp.compareTo(maxClientsMap.get(entry.getKey())) > 0) {
					maxClientsMap.put(entry.getKey(), maxTmp);
				}
				totalClientsMap.put(entry.getKey(), totalClientsMap.get(entry.getKey()) + maxTmp);
				tmpTotalData += maxTmp;
			}
			if (maxClients.compareTo(tmpTotalData) < 0) {
				maxClients = tmpTotalData;
			}
			totalClients += tmpTotalData;
		}
		
		if (!blnOriginalData) {
			AhNewReportUtil.fillNullValuesWithPadding(resultData, this.getStartTime(), this.getEndTime(), timePadding);
		}
		
		Long diffTime = this.getEndTime() - this.getStartTime();
		diffTime = diffTime.compareTo(0L) > 0 ? diffTime : 1;
		List<Map.Entry<Long, Map<RadioModeType, Map<String, Integer>>>> mapEntry = new ArrayList<Map.Entry<Long, Map<RadioModeType, Map<String, Integer>>>>(resultData.entrySet());
		Collections.sort(mapEntry, new Comparator<Map.Entry<Long, Map<RadioModeType, Map<String, Integer>>>>(){
			@Override
			public int compare(Map.Entry<Long, Map<RadioModeType, Map<String, Integer>>> entry1, Map.Entry<Long, Map<RadioModeType, Map<String, Integer>>> entry2) {
				return entry1.getKey().compareTo(entry2.getKey());
			}
		});
		
		AhDatetimeSeries seriesTotal = new AhDatetimeSeries();
		this.addSeries(seriesTotal);
		seriesTotal.setId(1L);
		seriesTotal.setName("Total Clients");
		seriesTotal.setShowType(AhReportProperties.SERIES_TYPE_LINE);
		seriesTotal.addSummary("Maximum: " + maxClients);
		seriesTotal.addSummary("Average: " + totalClients/mapEntry.size());
		
		Map<RadioModeType, AhDatetimeSeries> subSeries = new HashMap<RadioModeType, AhDatetimeSeries>();
		setSupportedClientTypeRange(subSeries, null);
		Long curSeriesId = 2L;
		for (RadioModeType keyValue : subSeries.keySet()) {
			AhDatetimeSeries tmpSeries = new AhDatetimeSeries();
			tmpSeries.setId(curSeriesId++);
			tmpSeries.setName(StringUtils.capitalize(keyValue.getValue()));
			tmpSeries.setShowType(AhReportProperties.SERIES_TYPE_LINE);
			Integer totalClientDataTmp = maxClientsMap.get(keyValue);
			totalClientDataTmp = totalClientDataTmp == null ? 0 : totalClientDataTmp;
			tmpSeries.addSummary("Maximum: " + totalClientDataTmp);
			tmpSeries.addSummary("Average: " + totalClientsMap.get(keyValue)/mapEntry.size());
			subSeries.put(keyValue, tmpSeries);
		}
		List<RadioModeType> lstSeriesEntry = new ArrayList<RadioModeType>(subSeries.keySet());
		Collections.sort(lstSeriesEntry, new Comparator<RadioModeType>(){
			@Override
			public int compare(RadioModeType entry1, RadioModeType entry2) {
				return entry1.ordinal() - entry2.ordinal();
			}
		});
		for (RadioModeType radioTypeTmp : lstSeriesEntry) {
			this.addSeries(subSeries.get(radioTypeTmp));
		}

		for (Map.Entry<Long, Map<RadioModeType, Map<String, Integer>>> entry : mapEntry) {
			Integer totalDataTmp = 0;
			Map<RadioModeType, Map<String, Integer>> mapCalTmp = entry.getValue();
			if (mapCalTmp != null) {
				for (RadioModeType keyTmp : mapCalTmp.keySet()) {
					Integer dataTmp = getSumMap(mapCalTmp.get(keyTmp));
					if (subSeries.get(keyTmp) != null) {
						subSeries.get(keyTmp).addData(entry.getKey(), dataTmp);
					}
					totalDataTmp += dataTmp;
				}
			} else {
				mapCalTmp = new HashMap<RadioModeType, Map<String, Integer>>();
				setSupportedClientTypeRange(mapCalTmp, null);
				for (RadioModeType keyTmp : mapCalTmp.keySet()) {
					if (subSeries.get(keyTmp) != null) {
						subSeries.get(keyTmp).addData(entry.getKey(), 0);
					}
				}
			}
			seriesTotal.addData(entry.getKey(), totalDataTmp);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setSupportedClientTypeRange(Map map, Object defValue) {
		if (map == null) {
			return;
		}
		map.put(RadioModeType.GHz24, defValue);
		map.put(RadioModeType.GHz50, defValue);
		map.put(RadioModeType.WIRED, defValue);
	}
	
	private Integer getSumMap(Map<String, Integer> mapArg) {
		if (mapArg != null
				&& !mapArg.isEmpty()) {
			Integer result = 0;
			for (Integer value : mapArg.values()) {
				result += value;
			}
			
			return result;
		}
		
		return 0;
	}
	
	private List<Long> getExactTimeTicks() {
		SortParams sort = new SortParams("timeStamp");
		String where = "timeStamp >= :s1 and timeStamp <= :s2 and globalFlg is true";
		FilterParams filter = new FilterParams(where,
			new Object[] {this.getStartTime(), this.getEndTime()});
		
		List<?> bos = QueryUtil.executeQuery("select stat.timeStamp from " + AhMaxClientsCount.class.getSimpleName() + " stat", sort, filter);
		
		if (bos != null
				&& !bos.isEmpty()) {
			List<Long> result = new ArrayList<Long>();
			for(Object bo : bos) {
				if(bo == null) {
					continue;
				}
				Long timeStamp = (Long)bo;
				if (timeStamp != null) {
					result.add(timeStamp);
				}
			}
			
			return result;
		}
		
		return null;
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
		
		Integer[] dataPoints = new Integer[]{24, 202, 200, 195, 142, 142, 123, 176, 185, 156};
		int pointsCount = timePoints.size();
		Map<Long, Integer> cg24 = new HashMap<Long, Integer>(pointsCount);
		Map<Long, Integer> cg50 = new HashMap<Long, Integer>(pointsCount);
		Map<Long, Integer> cwired = new HashMap<Long, Integer>(pointsCount);
		int curIdx = 0;
		int dataLen = dataPoints.length;
		Integer totalClients = 0;
		Integer maxClients = 0;
		Map<RadioModeType, Integer> maxClientsMap = new HashMap<RadioModeType, Integer>();
		Map<RadioModeType, Integer> totalClientsMap = new HashMap<RadioModeType, Integer>();
		setSupportedClientTypeRange(maxClientsMap, 0);
		setSupportedClientTypeRange(totalClientsMap, 0);
		for (Long timePoint : timePoints) {
			Integer dataTmp = dataPoints[curIdx++%dataLen];
			Integer g24Tmp = dataTmp/2;
			Integer g50Tmp = dataTmp/3;
			Integer wiredTmp = dataTmp - g24Tmp - g50Tmp;
			cg24.put(timePoint, g24Tmp);
			cg50.put(timePoint, g50Tmp);
			cwired.put(timePoint, wiredTmp);
			
			if (maxClients.compareTo(dataTmp) < 0) {
				maxClients = dataTmp;
			}
			totalClients += dataTmp;
			
			if (maxClientsMap.get(RadioModeType.GHz24).compareTo(g24Tmp) < 0) {
				maxClientsMap.put(RadioModeType.GHz24, g24Tmp);
			}
			totalClientsMap.put(RadioModeType.GHz24, totalClientsMap.get(RadioModeType.GHz24) + g24Tmp);
			
			if (maxClientsMap.get(RadioModeType.GHz50).compareTo(g50Tmp) < 0) {
				maxClientsMap.put(RadioModeType.GHz50, g50Tmp);
			}
			totalClientsMap.put(RadioModeType.GHz50, totalClientsMap.get(RadioModeType.GHz50) + g50Tmp);
			
			if (maxClientsMap.get(RadioModeType.WIRED).compareTo(wiredTmp) < 0) {
				maxClientsMap.put(RadioModeType.WIRED, wiredTmp);
			}
			totalClientsMap.put(RadioModeType.WIRED, totalClientsMap.get(RadioModeType.WIRED) + wiredTmp);
		}
		
		AhDatetimeSeries seriesTotal = new AhDatetimeSeries();
		this.addSeries(seriesTotal);
		seriesTotal.setId(1L);
		seriesTotal.setName("Total Clients");
		seriesTotal.setShowType(AhReportProperties.SERIES_TYPE_LINE);
		seriesTotal.addSummary("Maximum: " + maxClients);
		seriesTotal.addSummary("Average: " + totalClients/pointsCount);
		
		Map<RadioModeType, AhDatetimeSeries> subSeries = new HashMap<RadioModeType, AhDatetimeSeries>();
		setSupportedClientTypeRange(subSeries, null);
		Long curSeriesId = 2L;
		for (RadioModeType keyValue : subSeries.keySet()) {
			AhDatetimeSeries tmpSeries = new AhDatetimeSeries();
			tmpSeries.setId(curSeriesId++);
			tmpSeries.setName(StringUtils.capitalize(keyValue.getValue()));
			tmpSeries.setShowType(AhReportProperties.SERIES_TYPE_LINE);
			Integer totalClientDataTmp = maxClientsMap.get(keyValue);
			totalClientDataTmp = totalClientDataTmp == null ? 0 : totalClientDataTmp;
			tmpSeries.addSummary("Maximum: " + totalClientDataTmp);
			tmpSeries.addSummary("Average: " + totalClientsMap.get(keyValue)/pointsCount);
			subSeries.put(keyValue, tmpSeries);
		}
		List<RadioModeType> lstSeriesEntry = new ArrayList<RadioModeType>(subSeries.keySet());
		Collections.sort(lstSeriesEntry, new Comparator<RadioModeType>(){
			@Override
			public int compare(RadioModeType entry1, RadioModeType entry2) {
				return entry1.ordinal() - entry2.ordinal();
			}
		});
		for (RadioModeType radioTypeTmp : lstSeriesEntry) {
			this.addSeries(subSeries.get(radioTypeTmp));
		}
		
		List<Map.Entry<Long, Integer>> mapEntry = new ArrayList<Map.Entry<Long, Integer>>(cg24.entrySet());
		Collections.sort(mapEntry, new Comparator<Map.Entry<Long, Integer>>(){
			@Override
			public int compare(Map.Entry<Long, Integer> entry1, Map.Entry<Long, Integer> entry2) {
				return entry1.getKey().compareTo(entry2.getKey());
			}
		});
		
		for (Map.Entry<Long, Integer> entry : mapEntry) {
			Integer cg24Tmp = cg24.get(entry.getKey());
			Integer cg50Tmp = cg50.get(entry.getKey());
			Integer cwiredTmp = cwired.get(entry.getKey());
			try {
				subSeries.get(RadioModeType.GHz24).addData(entry.getKey(), cg24Tmp);
				subSeries.get(RadioModeType.GHz50).addData(entry.getKey(), cg50Tmp);
				subSeries.get(RadioModeType.WIRED).addData(entry.getKey(), cwiredTmp);
				seriesTotal.addData(entry.getKey(), cg24Tmp + cg50Tmp + cwiredTmp);
			} catch(Exception e) {
				
			}
		}
	}
}
