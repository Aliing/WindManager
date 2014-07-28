package com.ah.bo.report.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.bo.HmBo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.impl.ReportPagingImpl;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.OsObjectVersion;
import com.ah.bo.performance.AhClientsOsInfoCountInterface;
import com.ah.bo.performance.AhInterfaceStatsInterf;
import com.ah.bo.report.AhLinearSeries;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.annotation.AhReportConfig;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.builder.DefaultNewReportBuilder;
import com.ah.util.bo.report.freechart.AhFreechartPie;

@AhReportConfig(id=AhReportProperties.REPORT_CLIENT_TYPE_DISTRIBUTION,
		builder=DefaultNewReportBuilder.class)
public class ClientTypeDistributionReport extends AhAbstractNewReport {

	@Override
	public void init() {
		this.setReportTitle(MgrUtil.getUserMessage("report.title.text.clients.type.distribution"));
		this.setReportSummary(MgrUtil.getUserMessage("report.summary.text.clients.type.distribution"));
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
		String where;
		FilterParams filter;
		if (this.getReportOptions().getSsidName().equalsIgnoreCase("All")) {
			where = "timeStamp >= :s1 and timeStamp <= :s2 and apMac in (:s3) and ssid is not null";
			filter = new FilterParams(where,
				new Object[] {this.getStartTime(), this.getEndTime(), apMaces});
		} else {
			where = "timeStamp >= :s1 and timeStamp <= :s2 and apMac in (:s3) and ssid=:s4";
			filter = new FilterParams(where,
				new Object[] {this.getStartTime(), this.getEndTime(), apMaces, this.getReportOptions().getSsidName()});
		}
		
		Class<? extends AhClientsOsInfoCountInterface> boClass = 
			AhNewReportUtil.getClientOSInfoCertainImplementClass(this.getPeriodType(), this.getStartTime(), this.getEndTime(),this.getReportOptions().isScheduleMode());
		
		ReportPagingImpl<AhInterfaceStatsInterf> page = new ReportPagingImpl(boClass);
		page.setPageSize(5000);
		page.clearRowCount();
		
		List<?> bos;
		Map<String, Map<String, Integer>> resultDataTmp = new HashMap<String, Map<String, Integer>>();
		
		while(page.hasNext()) {
			bos = page.next().executeQuery("select osInfo, clientCount, apMac, ssid from " + boClass.getSimpleName(),
					null, filter, this.getDomainId());
			
			for(Object bo : bos) {
				if(bo == null) {
					continue;
				}
				
				Object[] stat = (Object[])bo;
				String osInfo = stat[0] == null ? "N/A" : (String)stat[0];
				Integer clientCount = stat[1] == null ? 0 : (Integer)stat[1];
				String apMac = stat[2] == null ? "N/A" : (String)stat[2];
				String ssid = stat[3] == null ? "N/A" : (String)stat[3];
				
				String idMark = apMac + "_" + ssid;
				Integer deltaValue = 0;
				if (resultDataTmp.containsKey(osInfo)) {
					if(resultDataTmp.get(osInfo).containsKey(idMark)) {
						deltaValue = resultDataTmp.get(osInfo).get(idMark);
					}
				} else {
					Map<String, Integer> mapTmp = new HashMap<String, Integer>();
					resultDataTmp.put(osInfo, mapTmp);
				}
				if (deltaValue.compareTo(clientCount) < 0) {
					resultDataTmp.get(osInfo).put(idMark, clientCount);
				}
			}
		}

		
		Map<String, Integer> resultData = null;
		List<OsObject> osObjects = QueryUtil.executeQuery(OsObject.class, null, 
				null, 
				this.getDomainId(),
				new LazyOsObjLoader());
		Map<String, String> osInfoMap = new HashMap<String, String>();
		if (osObjects != null
				&& !osObjects.isEmpty()) {
			for (OsObject os : osObjects) {
				if (os.getItems() != null
						&& !os.getItems().isEmpty()) {
					for (OsObjectVersion osVersion : os.getItems()) {
						osInfoMap.put(osVersion.getOsVersion(), os.getOsName());
					}
				}
				if (os.getDhcpItems() != null
						&& !os.getDhcpItems().isEmpty()) {
					for (OsObjectVersion osVersion : os.getDhcpItems()) {
						osInfoMap.put(osVersion.getOsVersion(), os.getOsName());
					}
				}
			}
			
			if (osInfoMap != null
					&& !osInfoMap.isEmpty()) {
				resultData = new HashMap<String, Integer>();
				for (Map.Entry<String, Map<String, Integer>> entry : resultDataTmp.entrySet()) {
					String osInfoTmp = entry.getKey();
					if (osInfoMap.containsKey(osInfoTmp)) {
						osInfoTmp = osInfoMap.get(osInfoTmp);
					}
					Integer deltaValue = 0;
					if (resultData.containsKey(osInfoTmp)) {
						deltaValue = resultData.get(osInfoTmp);
					}
					resultData.put(osInfoTmp, getMapSumValue(entry.getValue()) + deltaValue);
				}
			}
		}
		if (resultData == null
				|| resultData.isEmpty()){
			resultData = new HashMap<String, Integer>();
			for (String key : resultDataTmp.keySet()) {
				resultData.put(key, getMapSumValue(resultDataTmp.get(key)));
			}
		}
		
		if (resultData != null
				&& !resultData.isEmpty()) {
			AhLinearSeries series = new AhLinearSeries();
			this.addSeries(series);
			series.setId(1L);
			series.setName("Clients");
			series.setShowType(AhReportProperties.SERIES_TYPE_PIE);
			List<Map.Entry<String, Integer>> mapEntry = new ArrayList<Map.Entry<String, Integer>>(resultData.entrySet());
			Collections.sort(mapEntry, new Comparator<Map.Entry<String, Integer>>(){
				@Override
				public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
					return entry2.getValue().compareTo(entry1.getValue());
				}
			});
			if (mapEntry.size() > AhReportProperties.MAX_DISPLAY_DATA_COUNT_FOR_REPORT) {
				mapEntry = mapEntry.subList(0, AhReportProperties.MAX_DISPLAY_DATA_COUNT_FOR_REPORT);
			}
			Long totalData = 0L;
			for (Map.Entry<String, Integer> entry : mapEntry) {
				totalData += entry.getValue();
			}
			for (Map.Entry<String, Integer> entry : mapEntry) {
				series.addData(entry.getKey(), entry.getValue());
			}
		}
	}
	
	
	public class LazyOsObjLoader implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (bo instanceof OsObject) {
				OsObject osObject = (OsObject) bo;
				if (null != osObject.getItems()) {
					osObject.getItems().size();
				}
				if (null != osObject.getDhcpItems()) {
					osObject.getDhcpItems().size();
				}
			}
			return null;
		}
	}
	
	private Integer getMapSumValue(Map<String, Integer> mapTmp) {
		if (mapTmp != null
				&& !mapTmp.isEmpty()) {
			Integer result = 0;
			for (Integer value : mapTmp.values()) {
				result += value;
			}
			
			return result;
		}
		
		return 0;
	}
	
	protected void prepareSampleData() {
		String[] items = new String[]{"Windows PC", "Mac", "iPad", "Android", "iPhone"};
		Integer[] values = new Integer[]{35, 30, 20, 11, 4};
		AhLinearSeries series = new AhLinearSeries();
		this.addSeries(series);
		series.setId(1L);
		series.setName("Clients");
		series.setShowType(AhReportProperties.SERIES_TYPE_PIE);
		for (int i = 0; i < items.length; i++) {
			series.addData(items[i], values[i]);
		}
	}
}
