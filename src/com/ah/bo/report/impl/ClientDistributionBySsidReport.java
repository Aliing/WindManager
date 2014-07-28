package com.ah.bo.report.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.ReportPagingImpl;
import com.ah.bo.performance.AhMaxClientsCount;
import com.ah.bo.performance.AhSsidClientsCount;
import com.ah.bo.performance.AhSsidClientsCountInterface;
import com.ah.bo.report.AhLinearSeries;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.annotation.AhReportConfig;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.AhNewReportUtil.RadioModeType;
import com.ah.util.bo.report.builder.DefaultNewReportBuilder;
import com.ah.util.bo.report.freechart.AhFreechartPie;

@AhReportConfig(id=AhReportProperties.REPORT_CLIENT_DISTRIBUTION_BY_SSID,
		builder=DefaultNewReportBuilder.class)
public class ClientDistributionBySsidReport extends AhAbstractNewReport {
	public static final String CLIENT_DISTRIBUTION_GHZ24 = "GHz24";
	public static final String CLIENT_DISTRIBUTION_GHZ50 = "GHz50";
	public static final String CLIENT_DISTRIBUTION_TOTAL = "total";
	
	@Override
	public void init() {
		this.setReportTitle(MgrUtil.getUserMessage("report.title.text.clients.max.by.ssid"));
		this.setReportSummary(MgrUtil.getUserMessage("report.summary.text.clients.max.by.ssid"));
		if (this.isBlnGroupCal()
				|| CLIENT_DISTRIBUTION_GHZ24.equals(this.getRequest().getSubType())) {
			this.addGroupReportEl(CLIENT_DISTRIBUTION_GHZ24);
			this.getGroupReportEl(CLIENT_DISTRIBUTION_GHZ24).setTitle(MgrUtil.getUserMessage("report.title.text.clients.max.by.ssid.24"));
			this.getGroupReportEl(CLIENT_DISTRIBUTION_GHZ24).setSummary(MgrUtil.getUserMessage("report.summary.text.clients.max.by.ssid.24"));
		}
		if (this.isBlnGroupCal()
				|| CLIENT_DISTRIBUTION_GHZ50.equals(this.getRequest().getSubType())) {
			this.addGroupReportEl(CLIENT_DISTRIBUTION_GHZ50);
			this.getGroupReportEl(CLIENT_DISTRIBUTION_GHZ50).setTitle(MgrUtil.getUserMessage("report.title.text.clients.max.by.ssid.50"));
			this.getGroupReportEl(CLIENT_DISTRIBUTION_GHZ50).setSummary(MgrUtil.getUserMessage("report.summary.text.clients.max.by.ssid.50"));
		}
	}
	
	@Override
	public void initReportChartExportEl() {
		this.setExportChartEl(new AhFreechartPie());
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
		if (this.getReportOptions().getSsidName().equalsIgnoreCase("All")) {
			where = "timeStamp >= :s1 and timeStamp <= :s2 and apMac in (:s3) and ssid is not null and radioMode != 3";
			filter = new FilterParams(where,
				new Object[] {this.getStartTime(), this.getEndTime(), apMaces});
		} else {
			where = "timeStamp >= :s1 and timeStamp <= :s2 and apMac in (:s3) and ssid=:s4 and radioMode != 3";
			filter = new FilterParams(where,
				new Object[] {this.getStartTime(), this.getEndTime(), apMaces, this.getReportOptions().getSsidName()});
		}
		
		Class<? extends AhSsidClientsCountInterface> boClass = 
			AhNewReportUtil.getSsidClientsCountCertainImplementClass(this.getPeriodType(), this.getStartTime(), this.getEndTime(),this.getReportOptions().isScheduleMode());
		
		ReportPagingImpl<AhSsidClientsCountInterface> page = new ReportPagingImpl(boClass);
		page.setPageSize(5000);
		page.clearRowCount();
		
		boolean blnOriginalData = false;
		if (boClass.getName().equals(AhSsidClientsCount.class.getName())) {
			blnOriginalData = true;
		}
		int tmpPeriodType = this.getPeriodType();
		if (this.getReportOptions().isScheduleMode()) {
			tmpPeriodType=-2;
		}
		Long timePadding = AhNewReportUtil.getTimeIntervalPadding(600000L, tmpPeriodType, this.getStartTime(), this.getEndTime());
		Long curCountTime = this.getStartTime();
		Long nextCountTime = this.getStartTime() + timePadding;
		List<Long> timeTicks = null;
		if (blnOriginalData) {
			timeTicks = getExactTimeTicks();
		}
		timeTicks = AhNewReportUtil.getAllTimeTicksToBeCalculated(this.getStartTime(), this.getEndTime(), timePadding, timeTicks);
		int timeTicksCount = timeTicks == null ? 0 : timeTicks.size();
		int curTimeTickIdx = 0;
		if (timeTicksCount > 1) {
			curCountTime = timeTicks.get(0);
			nextCountTime = timeTicks.get(1);
			curTimeTickIdx++;
		} else {
			return;
		}
		
		List<?> bos;
		Map<String, Map<RadioModeType, Integer>> resultData = new HashMap<String, Map<RadioModeType, Integer>>();
		Map<String, Map<RadioModeType, Map<String, Integer>>> statDataTmp = new HashMap<String, Map<RadioModeType, Map<String, Integer>>>();
		
		while(page.hasNext()) {
			bos = page.next().executeQuery("select apName, ssid, clientCount, radioMode, apMac, timeStamp from " + boClass.getSimpleName(),
					sort, filter, this.getDomainId());
			
			for(Object bo : bos) {
				if(bo == null) {
					continue;
				}
				
				Object[] stat = (Object[])bo;
				String ssidName = stat[1] == null ? "N/A" : (String)stat[1];
				Integer clientCount = stat[2] == null ? 0 : (Integer)stat[2];
				Integer radioMode = stat[3] == null ? 1 : (Integer)stat[3];
				String apMac = stat[4] == null ? "N/A" : (String)stat[4];
				Long curTimeStamp = stat[5] == null ? curCountTime : (Long)stat[5];
				
				RadioModeType radioModeType = AhNewReportUtil.getRadioModeType(radioMode);
				Integer deltaValue = -1;
				if (curTimeStamp > nextCountTime
						&& curTimeTickIdx < timeTicks.size()) {
					curTimeTickIdx++;
					if (curTimeTickIdx < timeTicks.size()) {
						nextCountTime = timeTicks.get(curTimeTickIdx);
					}
					this.aggregateTmpData(resultData, statDataTmp);
				}
				
				deltaValue = -1;
				if (statDataTmp.containsKey(ssidName)) {
					if (statDataTmp.get(ssidName).containsKey(radioModeType)) {
						if (statDataTmp.get(ssidName).get(radioModeType) != null) {
							if (statDataTmp.get(ssidName).get(radioModeType).containsKey(apMac)) {
								deltaValue = statDataTmp.get(ssidName).get(radioModeType).get(apMac);
							}
						} else {
							Map<String, Integer> mapTmp = new HashMap<String, Integer>();
							statDataTmp.get(ssidName).put(radioModeType, mapTmp);
						}
					}
				} else {
					Map<RadioModeType, Map<String, Integer>> mapTmp = new HashMap<RadioModeType, Map<String, Integer>>();
					setSupportedClientTypeRange(mapTmp, null);
					for (RadioModeType keyTmp : mapTmp.keySet()) {
						mapTmp.put(keyTmp, new HashMap<String, Integer>());
					}
					statDataTmp.put(ssidName, mapTmp);
				}
				if (clientCount.compareTo(deltaValue) > 0
						&& statDataTmp.get(ssidName).containsKey(radioModeType)) {
					statDataTmp.get(ssidName).get(radioModeType).put(apMac, clientCount);
				}
				
			}
		}
		this.aggregateTmpData(resultData, statDataTmp);
		
		if (resultData != null
				&& !resultData.isEmpty()) {
			String subType = this.getRequest().getSubType();
			if (CLIENT_DISTRIBUTION_TOTAL.equals(subType)
					|| StringUtils.isBlank(subType)
					|| this.isBlnGroupCal()) {
				AhLinearSeries seriesTotal = new AhLinearSeries();
				this.addSeries(seriesTotal);
				seriesTotal.setId(1L);
				seriesTotal.setName("Total Clients");
				seriesTotal.setShowType(AhReportProperties.SERIES_TYPE_PIE);
				encapChartSeries(getSeperateDataFromResult(resultData, CLIENT_DISTRIBUTION_TOTAL), seriesTotal);
			}
			
			if (CLIENT_DISTRIBUTION_GHZ24.equals(subType)
					|| this.isBlnGroupCal()) {
				AhLinearSeries seriesGHz24 = new AhLinearSeries();
				seriesGHz24.setId(2L);
				seriesGHz24.setName("2.4 GHz Clients");
				seriesGHz24.setShowType(AhReportProperties.SERIES_TYPE_PIE);
				this.addSeriesForCertainReportEl(seriesGHz24, this.getGroupReportEl(CLIENT_DISTRIBUTION_GHZ24));
				encapChartSeries(getSeperateDataFromResult(resultData, CLIENT_DISTRIBUTION_GHZ24), seriesGHz24);
			}
			
			if (CLIENT_DISTRIBUTION_GHZ50.equals(subType)
					|| this.isBlnGroupCal()) {
				AhLinearSeries seriesGHz50 = new AhLinearSeries();
				seriesGHz50.setId(3L);
				seriesGHz50.setName("5 GHz Clients");
				seriesGHz50.setShowType(AhReportProperties.SERIES_TYPE_PIE);
				this.addSeriesForCertainReportEl(seriesGHz50, this.getGroupReportEl(CLIENT_DISTRIBUTION_GHZ50));
				encapChartSeries(getSeperateDataFromResult(resultData, CLIENT_DISTRIBUTION_GHZ50), seriesGHz50);
			}
		}
	}
	
	private void aggregateTmpData(Map<String, Map<RadioModeType, Integer>> resultData, 
			Map<String, Map<RadioModeType, Map<String, Integer>>> statDataTmp) {
		if (resultData == null
				|| statDataTmp == null
				|| statDataTmp.isEmpty()) {
			return;
		}
		Integer deltaValue = -1;
		Integer maxClientsCount = 0;
		for (String ssidKey : statDataTmp.keySet()) {
			if (!resultData.containsKey(ssidKey)) {
				Map<RadioModeType, Integer> mapTmp = new HashMap<RadioModeType, Integer>();
				setSupportedClientTypeRange(mapTmp, 0);
				mapTmp.put(RadioModeType.TOTAL, 0);
				resultData.put(ssidKey, mapTmp);
			}
			Integer curPeriodMaxCount = 0;
			for (RadioModeType modeTypeKey : resultData.get(ssidKey).keySet()) {
				deltaValue = resultData.get(ssidKey).get(modeTypeKey);
				if (deltaValue == null) deltaValue = -1;
				maxClientsCount = 0;
				if (statDataTmp.get(ssidKey).containsKey(modeTypeKey)
						&& statDataTmp.get(ssidKey).get(modeTypeKey) != null) {
					for (Integer valueTmp : statDataTmp.get(ssidKey).get(modeTypeKey).values()) {
						maxClientsCount += valueTmp;
					}

					statDataTmp.get(ssidKey).get(modeTypeKey).clear();
					
					if (maxClientsCount.compareTo(deltaValue) > 0) {
						resultData.get(ssidKey).put(modeTypeKey, maxClientsCount);
					}
					curPeriodMaxCount += maxClientsCount;
				}
			}
			
			deltaValue = resultData.get(ssidKey).get(RadioModeType.TOTAL);
			if (deltaValue == null) deltaValue = -1;
			if (curPeriodMaxCount.compareTo(deltaValue) > 0) {
				resultData.get(ssidKey).put(RadioModeType.TOTAL, curPeriodMaxCount);
			}
		}
	}
	
	private void encapChartSeries(Map<String, Integer> dataMap, AhLinearSeries series) {
		List<Map.Entry<String, Integer>> mapEntry = new ArrayList<Map.Entry<String, Integer>>(dataMap.entrySet());
		Collections.sort(mapEntry, new Comparator<Map.Entry<String, Integer>>(){
			@Override
			public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
				Integer value1 = entry1.getValue();
				if (value1 == null) value1 = 0;
				Integer value2 = entry2.getValue();
				if (value2 == null) value2 = 0;
				return value2.compareTo(value1);
			}
		});
		
		if (mapEntry.size() > AhReportProperties.MAX_DISPLAY_DATA_COUNT_FOR_REPORT) {
			mapEntry = mapEntry.subList(0, AhReportProperties.MAX_DISPLAY_DATA_COUNT_FOR_REPORT);
		}
		
		for (Map.Entry<String, Integer> entry : mapEntry) {
			if (entry.getValue() != null
					&& entry.getValue().compareTo(0) > 0) {
				series.addData(entry.getKey(), entry.getValue());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setSupportedClientTypeRange(Map map, Object defValue) {
		if (map == null) {
			return;
		}
		map.put(RadioModeType.GHz24, defValue);
		map.put(RadioModeType.GHz50, defValue);
		//map.put(RadioModeType.WIRED, defValue);
	}
	
	private Integer getSumMapValue(Map<RadioModeType, Integer> map) {
		if (map != null
				&& !map.isEmpty()) {
			Integer result = 0;
			for (Integer value : map.values()) {
				result += value;
			}
			return result;
		}
		return 0;
	}
	
	private Map<String, Integer> getSeperateDataFromResult(Map<String, Map<RadioModeType, Integer>> dataTmp, String type) {
		Map<String, Integer> result = new HashMap<String, Integer>(dataTmp.size());
		
		RadioModeType radioType;
		if (CLIENT_DISTRIBUTION_GHZ24.equals(type)) {
			radioType = RadioModeType.GHz24;
		} else if (CLIENT_DISTRIBUTION_GHZ50.equals(type)) {
			radioType = RadioModeType.GHz50;
		} else if (CLIENT_DISTRIBUTION_TOTAL.equals(type)) {
			radioType = RadioModeType.TOTAL;
		} else {
			radioType = null;
		}
		for (Map.Entry<String, Map<RadioModeType, Integer>> entry : dataTmp.entrySet()) {
			if (radioType == null) {
				result.put(entry.getKey(), getSumMapValue(entry.getValue()));
			} else {
				result.put(entry.getKey(), entry.getValue().get(radioType));
			}
		}
		
		return result;
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
		String[] items = new String[]{"Employee", "Guest", "Contractor", "Partner", "Testing"};
		Integer[] values = new Integer[]{45, 25, 20, 6, 4};
		String subType = this.getRequest().getSubType();
		Map<String, Integer> ctotal = new HashMap<String, Integer>();
		Map<String, Integer> cg24 = new HashMap<String, Integer>();
		Map<String, Integer> cg50 = new HashMap<String, Integer>();
		for (int i = 0; i < values.length; i++) {
			Integer dataTmp = values[i];
			Integer c24Tmp = dataTmp/2;
			Integer c50Tmp = dataTmp/3;
			cg24.put(items[i], c24Tmp);
			cg50.put(items[i], c50Tmp);
			ctotal.put(items[i], dataTmp);
		}
		if (CLIENT_DISTRIBUTION_TOTAL.equals(subType)
				|| StringUtils.isBlank(subType)
				|| this.isBlnGroupCal()) {
			AhLinearSeries seriesTotal = new AhLinearSeries();
			this.addSeries(seriesTotal);
			seriesTotal.setId(1L);
			seriesTotal.setName("Total Clients");
			seriesTotal.setShowType(AhReportProperties.SERIES_TYPE_PIE);
			encapChartSeries(ctotal, seriesTotal);
		}
		
		if (CLIENT_DISTRIBUTION_GHZ24.equals(subType)
				|| this.isBlnGroupCal()) {
			AhLinearSeries seriesGHz24 = new AhLinearSeries();
			seriesGHz24.setId(2L);
			seriesGHz24.setName("2.4 GHz Clients");
			seriesGHz24.setShowType(AhReportProperties.SERIES_TYPE_PIE);
			this.addSeriesForCertainReportEl(seriesGHz24, this.getGroupReportEl(CLIENT_DISTRIBUTION_GHZ24));
			encapChartSeries(cg24, seriesGHz24);
		}
		
		if (CLIENT_DISTRIBUTION_GHZ50.equals(subType)
				|| this.isBlnGroupCal()) {
			AhLinearSeries seriesGHz50 = new AhLinearSeries();
			seriesGHz50.setId(3L);
			seriesGHz50.setName("5 GHz Clients");
			seriesGHz50.setShowType(AhReportProperties.SERIES_TYPE_PIE);
			this.addSeriesForCertainReportEl(seriesGHz50, this.getGroupReportEl(CLIENT_DISTRIBUTION_GHZ50));
			encapChartSeries(cg50, seriesGHz50);
		}
	}
}
