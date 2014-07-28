package com.ah.bo.report.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.ReportPagingImpl;
import com.ah.bo.performance.AhSsidClientsCountInterface;
import com.ah.bo.report.AhLinearSeries;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.annotation.AhReportConfig;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.AhNewReportUtil.RadioModeType;
import com.ah.util.bo.report.builder.DefaultNewReportBuilder;
import com.ah.util.bo.report.freechart.AhFreechartStackedBar;

@AhReportConfig(id=AhReportProperties.REPORT_CLIENTS_NUMBER_BY_AP,
		builder=DefaultNewReportBuilder.class)
public class ClientsNumberByAPReport extends AhAbstractNewReport {

	@Override
	public void init() {
		this.setReportValueTitle("Clients");
		this.setReportCategoryTitle("Device");
		this.setReportTitle(MgrUtil.getUserMessage("report.title.text.clients.by.device"));
		this.setReportSummary(MgrUtil.getUserMessage("report.summary.text.clients.by.device"));
	}
	
	@Override
	public void initReportChartExportEl() {
		this.setExportChartEl(new AhFreechartStackedBar(true));
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
		SortParams sort = new SortParams("apMac");
		String where = "timeStamp >= :s1 and timeStamp <= :s2 and apMac in (:s3)";
		FilterParams filter = new FilterParams(where,
				new Object[] {this.getStartTime(), this.getEndTime(), apMaces});
		
		Class<? extends AhSsidClientsCountInterface> boClass = 
			AhNewReportUtil.getSsidClientsCountCertainImplementClass(this.getPeriodType(), this.getStartTime(), this.getEndTime(),this.getReportOptions().isScheduleMode());
		
		ReportPagingImpl<AhSsidClientsCountInterface> page = new ReportPagingImpl(boClass);
		page.setPageSize(5000);
		page.clearRowCount();
		
		List<?> bos;
		Map<String, Map<RadioModeType, Map<String, Integer>>> resultDataTmp = new HashMap<String, Map<RadioModeType, Map<String, Integer>>>();
		Map<String, String> apMacAndName = new HashMap<String, String>();
		
		while(page.hasNext()) {
			bos = page.next().executeQuery("select apName, clientCount, ssid, radioMode, apMac from " + boClass.getSimpleName(),
					sort, filter, this.getDomainId());
			
			for(Object bo : bos) {
				if(bo == null) {
					continue;
				}
				
				Object[] stat = (Object[])bo;
				String apName = stat[0] == null ? "N/A" : (String)stat[0];
				int clientCount = stat[1] == null ? 0 : (Integer)stat[1];
				String ssidName = stat[2] == null ? "N/A" : (String)stat[2];
				Integer radioMode = stat[3] == null ? 1 : (Integer)stat[3];
				String apMac = stat[4] == null ? "N/A" : (String)stat[4];
				
				apMacAndName.put(apMac, apName);
				
				RadioModeType radioModeType = AhNewReportUtil.getRadioModeType(radioMode);
				int deltaValue = -1;
				if (resultDataTmp.containsKey(apMac)) {
					if (resultDataTmp.get(apMac).containsKey(radioModeType)
							&& resultDataTmp.get(apMac).get(radioModeType).containsKey(ssidName)) {
						deltaValue = resultDataTmp.get(apMac).get(radioModeType).get(ssidName);
					}
				} else {
					Map<RadioModeType, Map<String, Integer>> mapTmp = new HashMap<RadioModeType, Map<String, Integer>>();
					setSupportedClientTypeRange(mapTmp, null);
					for (RadioModeType key : mapTmp.keySet()) {
						mapTmp.put(key, new HashMap<String, Integer>());
					}
					resultDataTmp.put(apMac, mapTmp);
				}
				if (clientCount > deltaValue
						&& resultDataTmp.get(apMac).containsKey(radioModeType)) {
					resultDataTmp.get(apMac).get(radioModeType).put(ssidName, clientCount);
				}
			}
		}
		
		if (resultDataTmp == null
				|| resultDataTmp.isEmpty()) return;
		
		Map<String, Map<RadioModeType, Integer>> resultData = new HashMap<String, Map<RadioModeType, Integer>>(resultDataTmp.size());
		for (Iterator<String> iter = resultDataTmp.keySet().iterator(); iter.hasNext();) {
			String keyName = iter.next();
			Map<RadioModeType, Map<String, Integer>> radioClients = resultDataTmp.get(keyName);
			if (radioClients != null
					&& !radioClients.isEmpty()) {
				Map<RadioModeType, Integer> radioCountTmp = new HashMap<RadioModeType, Integer>();
				setSupportedClientTypeRange(radioCountTmp, 0);
				for (RadioModeType key : radioClients.keySet()) {
					int totalClientsOfRadioType = 0;
					Map<String, Integer> mapTmp = radioClients.get(key);
					for (Map.Entry<String, Integer> entry : mapTmp.entrySet()) {
						totalClientsOfRadioType += entry.getValue();
					}
					radioCountTmp.put(key, totalClientsOfRadioType);
				}
				resultData.put(keyName, radioCountTmp);
			}
		}
		if (resultData != null
				&& !resultData.isEmpty()) {
			Map<RadioModeType, AhLinearSeries> seriesMap = prepareSupportedSeries();
			
			List<Map.Entry<String, Map<RadioModeType, Integer>>> mapEntry = new ArrayList<Map.Entry<String, Map<RadioModeType, Integer>>>(resultData.entrySet());
			Collections.sort(mapEntry, new Comparator<Map.Entry<String, Map<RadioModeType, Integer>>>(){
				@Override
				public int compare(Map.Entry<String, Map<RadioModeType, Integer>> entry1, Map.Entry<String, Map<RadioModeType, Integer>> entry2) {
					return getSumMapValue(entry2.getValue()).compareTo(getSumMapValue(entry1.getValue()));
				}
			});
			if (mapEntry.size() > AhReportProperties.MAX_DISPLAY_DATA_COUNT_FOR_REPORT) {
				mapEntry = mapEntry.subList(0, AhReportProperties.MAX_DISPLAY_DATA_COUNT_FOR_REPORT);
			}
			int totalData = 0;
			Map<RadioModeType, Integer> radioClientsTotalCount = new HashMap<RadioModeType, Integer>();
			setSupportedClientTypeRange(radioClientsTotalCount, 0);
			for (Map.Entry<String, Map<RadioModeType, Integer>> entry : mapEntry) {
				for (RadioModeType key : entry.getValue().keySet()) {
					if (radioClientsTotalCount.containsKey(key)) {
						radioClientsTotalCount.put(key, radioClientsTotalCount.get(key) + entry.getValue().get(key));
					}
					totalData += entry.getValue().get(key);
				}
			}
			int itemCount = mapEntry.size();
			for (RadioModeType key : radioClientsTotalCount.keySet()) {
				if (seriesMap.containsKey(key)) {
					seriesMap.get(key).addSummary("Average: " + radioClientsTotalCount.get(key)/itemCount);
				}
			}
			
			for (Map.Entry<String, Map<RadioModeType, Integer>> entry : mapEntry) {
				if (apMacAndName.containsKey(entry.getKey())) {
					this.addCategories(apMacAndName.get(entry.getKey()));
				} else {
					this.addCategories(entry.getKey());
				}
				for (RadioModeType key : entry.getValue().keySet()) {
					if (seriesMap.containsKey(key)) {
						seriesMap.get(key).addData(entry.getValue().get(key));
					}
				}
			}
		}
	}
	
	private Map<RadioModeType, AhLinearSeries> prepareSupportedSeries() {
		Map<RadioModeType, AhLinearSeries> seriesMap = new HashMap<RadioModeType, AhLinearSeries>();
		setSupportedClientTypeRange(seriesMap, null);
		Long curIndex = 1L;
		for (RadioModeType key : seriesMap.keySet()) {
			AhLinearSeries seriesTmp = new AhLinearSeries();
			seriesTmp.setId(curIndex++);
			seriesTmp.setName(key.getValue());
			seriesTmp.setStackGroup("Clients");
			seriesTmp.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
			seriesMap.put(key, seriesTmp);
		}
		List<RadioModeType> seriesMapOrderLst = new ArrayList<RadioModeType>(seriesMap.keySet());
		Collections.sort(seriesMapOrderLst, new Comparator<RadioModeType>(){
			@Override
			public int compare(RadioModeType entry1, RadioModeType entry2) {
				return entry1.ordinal() - entry2.ordinal();
			}
		});
		for (RadioModeType key : seriesMapOrderLst) {
			this.addSeries(seriesMap.get(key));
		}
		
		return seriesMap;
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
	
	protected void prepareSampleData() {
		String[] items = new String[]{"AP_10th_Grade","AP_9th_Grade","AP_Bldg5_25","AP_Gynasium1","AP_Conf_RmR",
				"AP_Assembly_Rm","AP_Bldg1_12","AP_Teachers_Rm","AP_Admin_Office","AP_Main_Library"};
		Integer[] values = new Integer[]{215, 202, 200, 195, 142, 142, 123, 80, 45, 7};
		int itemCount = items.length;
		int g24Avg = 0;
		int g50Avg = 0;
		int wiredAvg = 0;
		Map<RadioModeType, AhLinearSeries> seriesMap = prepareSupportedSeries();
		for (int i = 0; i < items.length; i++) {
			this.addCategories(items[i]);
			Integer g24Tmp = values[i]/2;
			Integer g50Tmp = values[i]/3;
			Integer wiredTmp = values[i] - g24Tmp - g50Tmp;
			g24Avg += g24Tmp;
			g50Avg += g50Tmp;
			wiredAvg += wiredTmp;
			seriesMap.get(RadioModeType.GHz24).addData(g24Tmp);
			seriesMap.get(RadioModeType.GHz50).addData(g50Tmp);
			seriesMap.get(RadioModeType.WIRED).addData(wiredTmp);
		}
		
		seriesMap.get(RadioModeType.GHz24).addSummary("Average: " + g24Avg/itemCount);
		seriesMap.get(RadioModeType.GHz50).addSummary("Average: " + g50Avg/itemCount);
		seriesMap.get(RadioModeType.WIRED).addSummary("Average: " + wiredAvg/itemCount);
	}
}
