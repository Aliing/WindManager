package com.ah.bo.report.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.ah.be.common.DBOperationUtil;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.ReportPagingImpl;
import com.ah.bo.performance.AhClientSessionHistory;
import com.ah.bo.performance.AhClientStats;
import com.ah.bo.performance.AhClientStatsInterf;
import com.ah.bo.performance.AhNewReport;
import com.ah.bo.report.AhLinearSeries;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.annotation.AhReportConfig;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.AhNewReportUtil.DataUnits;
import com.ah.util.bo.report.builder.DefaultNewReportBuilder;
import com.ah.util.bo.report.freechart.AhFreechartStackedBar;

@AhReportConfig(id=AhReportProperties.REPORT_CLIENTS_TOPN,
		builder=DefaultNewReportBuilder.class)
public class ClientsTopNReport extends AhAbstractNewReport {
	
	@Override
	public void init() {
		this.setReportValueTitle("Data Usage " + DataUnits.B.getUnitName());
		this.setReportCategoryTitle("Client");
		this.setReportTitle(MgrUtil.getUserMessage("report.title.text.clients.data.usage"));
		this.setReportSummary(MgrUtil.getUserMessage("report.summary.text.clients.data.usage"));
	}
	
	@Override
	public void initReportChartExportEl() {
		this.setExportChartEl(new AhFreechartStackedBar());
	}

	@Override
	protected void doCalculate() throws Exception {
		prepareChartData();
	}
	
	private boolean isCalForTheRequest() {
		return ClientsTopNReport.isCalForTheRequest(getReportOptions(), getPeriodType(), getStartTime(), getEndTime());
	}
	
	public static boolean isCalForTheRequest(AhNewReport aReport) {
		return isCalForTheRequest(aReport, -1, null, null);
	}
	public static boolean isCalForTheRequest(AhNewReport aReport, int periodTypeArg, Long startTimeArg, Long endTimeArg) {
		if (aReport != null) {
			int periodTypeTmp = periodTypeArg;
			if (periodTypeTmp < 0) {
				periodTypeTmp = aReport.getReportPeriod();
			}
			Long startTimeTmp = startTimeArg;
			if (startTimeTmp == null) {
				startTimeTmp = aReport.getStartTime();
			}
			
			if (periodTypeTmp == AhNewReport.NEW_REPORT_PERIOD_LASTONEHOUR
					|| periodTypeTmp == AhNewReport.NEW_REPORT_PERIOD_LASTCLOCKHOUR
					|| periodTypeTmp == AhNewReport.NEW_REPORT_PERIOD_LASTONEDAY
					|| (aReport.isScheduleMode()
							&& aReport.getFrequency() == AhNewReport.NEW_REPORT_FREQUENCY_DAILY)) {
				return true;
			}
			
			if (periodTypeTmp == AhNewReport.NEW_REPORT_PERIOD_CUSTOM
					&& startTimeTmp > System.currentTimeMillis() - 24*3600000 - 1800000) {
				return true;
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	private void prepareChartData() throws Exception {
		if (this.getReportOptions().getApMacList() == null
				|| this.getReportOptions().getApMacList().isEmpty()) {
			return;
		}
		
		// only count for data in one day
		if (!isCalForTheRequest()) {
			return;
		}
		
		List<String> apMaces = new ArrayList<String>(this.getReportOptions().getApMacList());
		SortParams sort = new SortParams("apMac");
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
		
		Class<? extends AhClientStatsInterf> boClass = AhClientStats.class;
		
		@SuppressWarnings("unchecked")
		ReportPagingImpl<AhClientStatsInterf> page = new ReportPagingImpl(boClass);
		page.setPageSize(5000);
		page.clearRowCount();
		
		List<?> bos;
		Map<String, Long> resultData = new HashMap<String, Long>();
		
		while(page.hasNext()) {
			bos = page.next().executeQuery("select clientMac, txFrameByteCount, rxFrameByteCount from " + boClass.getSimpleName(),
					sort, filter, this.getDomainId());
			
			for(Object bo : bos) {
				if(bo == null) {
					continue;
				}
				
				Object[] stat = (Object[])bo;
				String clientMac = stat[0] == null ? "N/A" : (String)stat[0];
				Long txByteCount = stat[1] == null ? 0L : (Long)stat[1];
				Long rxByteCount = stat[2] == null ? 0L : (Long)stat[2];
				
				Long deltaValue = 0L;
				if (resultData.containsKey(clientMac)) {
					deltaValue = resultData.get(clientMac);
				}
				resultData.put(clientMac, deltaValue + txByteCount + rxByteCount);
			}
		}
		
		ClientInfo clientInfoTmp;
		if (resultData != null
				&& !resultData.isEmpty()) {
			List<Map.Entry<String, Long>> resultDataLst = new ArrayList<Map.Entry<String, Long>>(resultData.entrySet());
			Collections.sort(resultDataLst, new Comparator<Map.Entry<String, Long>>(){
				@Override
				public int compare(Map.Entry<String, Long> entry1, Map.Entry<String, Long> entry2) {
					Long value2 = entry2.getValue();
					if (value2 == null) value2 = 0L;
					Long value1 = entry1.getValue();
					if (value1 == null) value1 = 0L;
					return value2.compareTo(value1);
				}
			});
			
			int uniqueClientsCount = resultDataLst.size();
			
			if (resultDataLst.size() > AhReportProperties.MAX_DISPLAY_DATA_COUNT_FOR_REPORT) {
				resultDataLst = resultDataLst.subList(0, AhReportProperties.MAX_DISPLAY_DATA_COUNT_FOR_REPORT);
			}
			DataUnits dataUnit = AhNewReportUtil.getProperDataUnitForValue(resultDataLst.get(0).getValue(), 2);
			this.setCurDataUnit(dataUnit.getUnitName());
			
			Map<String, ClientInfo> clientInfoMaps = new HashMap<String, ClientInfo>();
			List<String> clientMacs = new ArrayList<String>(resultDataLst.size());
			String macKey = "";
			for (Map.Entry<String, Long> result : resultDataLst) {
				macKey = result.getKey();
				clientMacs.add(macKey);
				clientInfoMaps.put(macKey, new ClientInfo(macKey));
			}
			
			List<?> clients = DBOperationUtil.executeQuery("select clientMac, clientUsername, clientHostname from ah_clientsession", 
					null, 
					new FilterParams("clientMac", clientMacs), 
					this.getDomainId());
			if (clients != null
					&& !clients.isEmpty()) {
				for (Object bo : clients) {
					Object[] bosTmp = (Object[])bo;
					if (bosTmp[0] == null) {
						continue;
					}
					String cName = (String)bosTmp[1];
					String hName = (String)bosTmp[2];
					
					clientInfoTmp = clientInfoMaps.get((String)bosTmp[0]);
					if (clientInfoTmp != null) {
						clientInfoTmp.setClientUserName(cName);
						clientInfoTmp.setHostName(hName);
						clientInfoTmp.setBlnValueSet(true);
					}
				}
			}
			
			List<String> hisClientMacs = new ArrayList<String>();
			for (ClientInfo cInfo : clientInfoMaps.values()) {
				if (cInfo != null
						&& !cInfo.isBlnValueSet()) {
					hisClientMacs.add(cInfo.getMacAddress());
				}
			}
			if (hisClientMacs != null
					&& !hisClientMacs.isEmpty()) {
				List<?> hisClients = QueryUtil.executeQuery("select clientMac, clientUsername, clientHostname from "
						+ AhClientSessionHistory.class.getSimpleName(), 
						new SortParams("endTimeStamp", false), 
						new FilterParams("clientMac in (:s1) and endTimeStamp>=:s2",
								new Object[]{hisClientMacs, this.getEndTime() - 24*3600000}),
						this.getDomainId());
				if (hisClients != null
						&& !hisClients.isEmpty()) {
					for (Object bo : hisClients) {
						Object[] bosTmp = (Object[])bo;
						if (bosTmp[0] == null) {
							continue;
						}
						
						clientInfoTmp = clientInfoMaps.get((String)bosTmp[0]);
						if (clientInfoTmp != null
								&& !clientInfoTmp.isBlnValueSet()) {
							clientInfoTmp.setClientUserName((String)bosTmp[1]);
							clientInfoTmp.setHostName((String)bosTmp[2]);
							clientInfoTmp.setBlnValueSet(true);
						}
					}
				}
			}
			
			AhLinearSeries series = new AhLinearSeries();
			series.setId(1L);
			series.setName("Data Usage");
			series.addSummary("Total Clients: " + uniqueClientsCount);
			series.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
			this.addSeries(series);
			
			Long diffTime = this.getEndTime() - this.getStartTime();
			diffTime = diffTime.compareTo(0L) > 0 ? diffTime : 1;
			for (Map.Entry<String, Long> entry : resultDataLst) {
				String keyTmp = entry.getKey();
				if (clientInfoMaps.containsKey(keyTmp)) {
					clientInfoTmp = clientInfoMaps.get(keyTmp);
					if (clientInfoTmp != null) {
						if (!StringUtils.isBlank(clientInfoTmp.getClientUserName())) {
							keyTmp = clientInfoTmp.getClientUserName();
						} else if (!StringUtils.isBlank(clientInfoTmp.getHostName())) {
							keyTmp = clientInfoTmp.getHostName();
						}
						clientInfos.add(clientInfoTmp);
					} else {
						clientInfos.add(new ClientInfo(keyTmp));
					}
					
				}
				this.addCategories(keyTmp);
				series.addData(AhNewReportUtil.convertBitsToCertainDataUnitValue(entry.getValue(), dataUnit));
			}
		}
	}
	
	private List<ClientInfo> clientInfos = new ArrayList<ClientInfo>();
	public class ClientInfo {
		private String macAddress;
		private String clientUserName;
		private String hostName;
		private String deviceType;
		private String osType;
		private boolean blnValueSet;
		public ClientInfo(String macAddress) {
			this.macAddress = macAddress;
		}
		
		public String getMacAddress() {
			return macAddress;
		}
		public void setMacAddress(String macAddress) {
			this.macAddress = macAddress;
		}
		public String getClientUserName() {
			return clientUserName;
		}
		public void setClientUserName(String clientUserName) {
			this.clientUserName = clientUserName;
		}
		public String getHostName() {
			return hostName;
		}
		public void setHostName(String hostName) {
			this.hostName = hostName;
		}
		public String getDeviceType() {
			return deviceType;
		}
		public void setDeviceType(String deviceType) {
			this.deviceType = deviceType;
		}
		public String getOsType() {
			return osType;
		}
		public void setOsType(String osType) {
			this.osType = osType;
		}

		public boolean isBlnValueSet() {
			return blnValueSet;
		}
		public void setBlnValueSet(boolean blnValueSet) {
			this.blnValueSet = blnValueSet;
		}
	}
	
	@Override
	protected JSONObject encapCustomUserMessage() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("u", this.curDataUnit);
			if (clientInfos != null
					&& !clientInfos.isEmpty()) {
				JSONObject cInfo = new JSONObject();
				jsonObject.put("cinfo", cInfo);
				for (ClientInfo clientInfo : clientInfos) {
					if (clientInfo != null) {
						cInfo.put("cu", clientInfo.getClientUserName());
						cInfo.put("hn", clientInfo.getHostName());
						cInfo.put("mac", clientInfo.getMacAddress());
						cInfo.put("dt", clientInfo.getDeviceType());
						cInfo.put("os", clientInfo.getOsType());
					}
				}
			}
		} catch (Exception e) {
			return null;
		}
		
		return jsonObject;
	}
	
	private String curDataUnit;
	
	protected void prepareSampleData() {
		String[] items = new String[]{"Johns_iPhone","3C0723A127B2","3C0723bb12C3","Toms_macbook","Ann_macbook",
				"9C34C15629E1","Tchr1_iPad","Tchr2_iPad","78CA39D50356","Rick_laptop"};
		Long[] values = new Long[]{215L, 202L, 200L, 195L, 142L, 142L, 123L, 80L, 45L, 7L};
		for (int i = 0; i < values.length; i++) {
			values[i] *= 32401111L;
		}
		
		DataUnits dataUnit = AhNewReportUtil.getProperDataUnitForValue(values[0], 2);
		this.setCurDataUnit(dataUnit.getUnitName());
		
		AhLinearSeries series = new AhLinearSeries();
		series.setId(1L);
		series.setName("Data Usage");
		series.addSummary("Total Clients: " + items.length);
		series.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
		this.addSeries(series);
		
		for (int i = 0; i < values.length; i++) {
			this.addCategories(items[i]);
			series.addData(AhNewReportUtil.convertBitsToCertainDataUnitValue(values[i], dataUnit));
		}
	}

	public String getCurDataUnit() {
		return curDataUnit;
	}

	public void setCurDataUnit(String curDataUnit) {
		this.curDataUnit = curDataUnit;
		this.setReportValueTitle("Data Usage " + curDataUnit);
	}
}
