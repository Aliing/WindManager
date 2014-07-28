package com.ah.bo.report.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.impl.ReportPagingImpl;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.AhInterfaceStatsInterf;
import com.ah.bo.report.AhLinearSeries;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.annotation.AhReportConfig;
import com.ah.ui.actions.monitor.TrafficData;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.builder.DefaultNewReportBuilder;
import com.ah.util.bo.report.freechart.AhFreechartStackedBar;

@AhReportConfig(id=AhReportProperties.REPORT_DEVICE_ERROR,
		builder=DefaultNewReportBuilder.class)
public class UsageDeviceErrorReport extends AhAbstractNewReport {

	@Override
	public void init() {
		this.setReportTitle(MgrUtil.getUserMessage("report.title.text.errors.device"));
		this.setReportValueTitle("Percentage  Errors (%)");
		this.setReportCategoryTitle("Device");
		this.setReportSummary(MgrUtil.getUserMessage("report.summary.text.errors.device"));
	}
	
	@Override
	public void initReportChartExportEl() {
		this.setExportChartEl(new AhFreechartStackedBar());
	}

	@Override
	protected void doCalculate() throws Exception {
		prepareDataSeries();
	}
	
	private void prepareDataSeries() throws Exception {
		if (this.getReportOptions().getApMacList() == null
				|| this.getReportOptions().getApMacList().isEmpty()) {
			return;
		}
		
		List<String> apMacs = new ArrayList<String>(this.getReportOptions().getApMacList());
		String where = "timeStamp >= :s1 and timeStamp <= :s2 and apMac in (:s3) and owner.id = :s4";
		FilterParams filter = new FilterParams(where,
				new Object[] {this.getStartTime(), this.getEndTime(), apMacs, this.getDomainId()});
		
		Class<? extends AhInterfaceStatsInterf> boClass = 
			AhNewReportUtil.getInterfaceStatsCertainImplementClass(this.getPeriodType(), this.getStartTime(), this.getEndTime(),this.getReportOptions().isScheduleMode());
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ReportPagingImpl<AhInterfaceStatsInterf> page = new ReportPagingImpl(boClass);
		page.setPageSize(5000);
		page.clearRowCount();
		
		List<?> bos;
		Map<String, TrafficData> resultData = new HashMap<String, TrafficData>();
		
		while(page.hasNext()) {
			bos = page.next().executeQuery("select apName, apMac, radioType, crcErrorRate, txDrops, rxRetryRate, uniTxFrameCount, uniRxFrameCount from " + 
					boClass.getSimpleName(),
					null, filter);
			for(Object bo : bos) {
				if(bo == null) {
					continue;
				}
				
				Object[] stat = (Object[])bo;
				String apName = stat[0] == null ? "UnKnown" : (String)stat[0];
				String apMac = stat[1] == null ? "UnKnown" : (String)stat[1];
				String radioType = stat[2] == null ? "UnKnown" : String.valueOf(stat[2]);
				int crcerror = stat[3] == null ? 0 : Integer.valueOf(stat[3].toString());
				
				long txdrops = stat[4] == null ? 0 : Long.valueOf(stat[4].toString());
				long rxRetryRate  = stat[5] == null ? 0 : Long.valueOf(stat[5].toString());
				long txFrames = stat[6] == null ? 0 : Long.valueOf(stat[6].toString());
				long rxFrames = stat[7] == null ? 0 : Long.valueOf(stat[7].toString());
				
				if (resultData.get(apMac)==null) {
					TrafficData td = new TrafficData();
					td.setName(apName);
					if (isRadioType24Ghz(radioType)) {
						td.setTxdata(crcerror);
						// crcerror means wifi0 txerror
						td.setCrcError((txdrops+txFrames) ==0 ? 0:txdrops *100/(txdrops+txFrames));
						// score means wifi0 rxerror
						td.setScore(rxRetryRate);
					} else {
						td.setRxdata(crcerror);
						// TxRetry means wifi1 txerror
						td.setTxRetry((txdrops+txFrames) ==0 ? 0:txdrops*100/(txdrops+txFrames));
						// SlaCount means wifi1 rxerror
						td.setSlaCount(rxRetryRate);
					}
					resultData.put(apMac, td);
				} else {
					resultData.get(apMac).setName(apName);
					if (isRadioType24Ghz(radioType)) {
						if (crcerror > resultData.get(apMac).getTxdata()){
							resultData.get(apMac).setTxdata(crcerror);
						}
						if (txdrops+txFrames >0) {
							if (txdrops*100/(txdrops+txFrames) > resultData.get(apMac).getCrcError()){
								resultData.get(apMac).setCrcError(txdrops*100/(txdrops+txFrames));
							}
						}
//						if (rxdrops+rxFrames >0) {
							if (rxRetryRate > resultData.get(apMac).getScore()){
								resultData.get(apMac).setScore(rxRetryRate);
							}
//						}
					} else {
						if (crcerror > resultData.get(apMac).getRxdata()){
							resultData.get(apMac).setRxdata(crcerror);
						}
						if (txdrops+txFrames >0) {
							if (txdrops*100/(txdrops+txFrames) > resultData.get(apMac).getTxRetry()){
								resultData.get(apMac).setTxRetry(txdrops*100/(txdrops+txFrames));
							}
						}
//						if (rxdrops+rxFrames >0) {
							if (rxRetryRate > resultData.get(apMac).getSlaCount()){
								resultData.get(apMac).setSlaCount(rxRetryRate);
							}
//						}
					}
				}
			}
		}
		
		if (resultData != null
				&& !resultData.isEmpty()) {
			List<Map.Entry<String, TrafficData>> mapEntry = new ArrayList<Map.Entry<String, TrafficData>>(resultData.entrySet());
			Collections.sort(mapEntry, new Comparator<Map.Entry<String, TrafficData>>(){
				@Override
				public int compare(Map.Entry<String, TrafficData> entry1, Map.Entry<String, TrafficData> entry2) {
					long r2 = entry2.getValue().getTxdata() + entry2.getValue().getRxdata() 
							+  entry2.getValue().getCrcError() + entry2.getValue().getScore()
							+  entry2.getValue().getTxRetry() + entry2.getValue().getSlaCount();
					long r1 = entry1.getValue().getTxdata() + entry1.getValue().getRxdata()
							+  entry1.getValue().getCrcError() + entry1.getValue().getScore()
							+  entry1.getValue().getTxRetry() + entry1.getValue().getSlaCount();
					if (r2>r1){ return 1;}
					else if (r2==r1){ return 0;}
					else {return -1;}
				}
			});
			if (mapEntry.size() > 20) {
				mapEntry = mapEntry.subList(0, 20);
			}
			String groupName = "total_grp";
			Long seId=1L;
			List<Object> categories = new ArrayList<Object>();

			List<Object> wifi0 = new ArrayList<Object>();
			List<Object> wifi1 = new ArrayList<Object>();
			List<Object> wifi0TxError = new ArrayList<Object>();
			List<Object> wifi0RxError = new ArrayList<Object>();
			List<Object> wifi1TxError = new ArrayList<Object>();
			List<Object> wifi1RxError = new ArrayList<Object>();
			
			for (Map.Entry<String, TrafficData> entry : mapEntry) {
//				if (entry.getValue().getTxdata() + entry.getValue().getRxdata() 
//						+ entry.getValue().getCrcError() + entry.getValue().getScore() 
//						+ entry.getValue().getTxRetry()
//						+ entry.getValue().getSlaCount()>0){
					categories.add(entry.getValue().getName());
					wifi0.add(entry.getValue().getTxdata());
					wifi1.add(entry.getValue().getRxdata());
					wifi0TxError.add(entry.getValue().getCrcError());
					wifi0RxError.add(entry.getValue().getScore());
					wifi1TxError.add(entry.getValue().getTxRetry());
					wifi1RxError.add(entry.getValue().getSlaCount());
//				}
			}
			this.setCategories(categories);
			
			AhLinearSeries seriesWifi0 = new AhLinearSeries();
			seriesWifi0.setId(seId++);
			seriesWifi0.setName("2.4 GHz CRC Error Rate");
			seriesWifi0.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
			seriesWifi0.setObjectData(wifi0);
			seriesWifi0.setStackGroup(groupName);
			this.addSeries(seriesWifi0);
			
			
			AhLinearSeries seriesWifi1 = new AhLinearSeries();
			seriesWifi1.setId(seId++);
			seriesWifi1.setName("5 GHz CRC Error Rate");
			seriesWifi1.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
			seriesWifi1.setObjectData(wifi1);
			seriesWifi1.setStackGroup(groupName);
			this.addSeries(seriesWifi1);
			
			AhLinearSeries seriesWifi0Tx = new AhLinearSeries();
			seriesWifi0Tx.setId(seId++);
			seriesWifi0Tx.setName("2.4 GHz Tx Drop Rate");
			seriesWifi0Tx.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
			seriesWifi0Tx.setObjectData(wifi0TxError);
			seriesWifi0Tx.setStackGroup(groupName);
			this.addSeries(seriesWifi0Tx);
			
			AhLinearSeries seriesWifi0Rx = new AhLinearSeries();
			seriesWifi0Rx.setId(seId++);
			seriesWifi0Rx.setName("2.4 GHz Rx Retry Rate");
			seriesWifi0Rx.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
			seriesWifi0Rx.setObjectData(wifi0RxError);
			seriesWifi0Rx.setStackGroup(groupName);
			this.addSeries(seriesWifi0Rx);
			
			AhLinearSeries seriesWifi1Tx = new AhLinearSeries();
			seriesWifi1Tx.setId(seId++);
			seriesWifi1Tx.setName("5 GHz Tx Drop Rate");
			seriesWifi1Tx.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
			seriesWifi1Tx.setObjectData(wifi1TxError);
			seriesWifi1Tx.setStackGroup(groupName);
			this.addSeries(seriesWifi1Tx);
			
			AhLinearSeries seriesWifi1Rx = new AhLinearSeries();
			seriesWifi1Rx.setId(seId++);
			seriesWifi1Rx.setName("5 GHz Rx Retry Rate");
			seriesWifi1Rx.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
			seriesWifi1Rx.setObjectData(wifi1RxError);
			seriesWifi1Rx.setStackGroup(groupName);
			this.addSeries(seriesWifi1Rx);
			
		}
	}
	
	private boolean isRadioType24Ghz(String radioType) {
		if (String.valueOf(AhInterfaceStats.RADIOTYPE_24G).equals(radioType)) {
			return true;
		}
		return false;
	}
	
	protected void prepareSampleData() {
		String[] items = new String[]{"AP_10th_Grade","AP_9th_Grade","AP_Bldg5_25","AP_Gynasium1","AP_Conf_RmR"};
		Integer[] values = new Integer[]{31, 30, 20, 11, 8};
		Long seId = 1L;
		String groupName = "errGroup";
		
		AhLinearSeries seriesWifi0 = new AhLinearSeries();
		seriesWifi0.setId(seId++);
		seriesWifi0.setName("2.4 GHz CRC Error Rate");
		seriesWifi0.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
		seriesWifi0.setStackGroup(groupName);
		this.addSeries(seriesWifi0);
		
		
		AhLinearSeries seriesWifi1 = new AhLinearSeries();
		seriesWifi1.setId(seId++);
		seriesWifi1.setName("5 GHz CRC Error Rate");
		seriesWifi1.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
		seriesWifi1.setStackGroup(groupName);
		this.addSeries(seriesWifi1);
		
		AhLinearSeries seriesWifi0Tx = new AhLinearSeries();
		seriesWifi0Tx.setId(seId++);
		seriesWifi0Tx.setName("2.4 GHz Tx Drop Rate");
		seriesWifi0Tx.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
		seriesWifi0Tx.setStackGroup(groupName);
		this.addSeries(seriesWifi0Tx);
		
		AhLinearSeries seriesWifi0Rx = new AhLinearSeries();
		seriesWifi0Rx.setId(seId++);
		seriesWifi0Rx.setName("2.4 GHz Rx Retry Rate");
		seriesWifi0Rx.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
		seriesWifi0Rx.setStackGroup(groupName);
		this.addSeries(seriesWifi0Rx);
		
		AhLinearSeries seriesWifi1Tx = new AhLinearSeries();
		seriesWifi1Tx.setId(seId++);
		seriesWifi1Tx.setName("5 GHz Tx Drop Rate");
		seriesWifi1Tx.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
		seriesWifi1Tx.setStackGroup(groupName);
		this.addSeries(seriesWifi1Tx);
		
		AhLinearSeries seriesWifi1Rx = new AhLinearSeries();
		seriesWifi1Rx.setId(seId++);
		seriesWifi1Rx.setName("5 GHz Rx Retry Rate");
		seriesWifi1Rx.setShowType(AhReportProperties.SERIES_TYPE_COLUMN);
		seriesWifi1Rx.setStackGroup(groupName);
		this.addSeries(seriesWifi1Rx);
		
		for (int i = 0; i < items.length; i++) {
			this.addCategories(items[i]);
			seriesWifi0.addData(values[i]);
			seriesWifi1.addData(values[i] - 2);
			seriesWifi0Tx.addData(values[i] - 7);
			seriesWifi0Rx.addData(values[i] - 3);
			seriesWifi1Tx.addData(values[i] - 6);
			seriesWifi1Rx.addData(values[i] - 3);
		}
	}

}
