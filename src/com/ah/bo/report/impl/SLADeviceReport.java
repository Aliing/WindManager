package com.ah.bo.report.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.ReportPagingImpl;
import com.ah.bo.performance.AhNewSLAStats;
import com.ah.bo.performance.AhNewSLAStatsInterface;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.annotation.AhReportConfig;
import com.ah.util.MgrUtil;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.builder.DefaultNewReportBuilder;
import com.ah.util.bo.report.freechart.AhFreechartDatetimeStackedArea;

@AhReportConfig(id=AhReportProperties.REPORT_SLA_DEVICE,
		builder=DefaultNewReportBuilder.class)
public class SLADeviceReport extends SLABaseReport {
	public static final String SLA_TYPE_ALL = "all";
	public static final String SLA_TYPE_THROUGHPUT = "throughput";
	public static final String SLA_TYPE_CRCERROR = "crcError";
	public static final String SLA_TYPE_AIRTIME = "airtime";
	public static final String SLA_TYPE_TXDROP = "txDrop";
	public static final String SLA_TYPE_RXDROP = "rxDrop";
	public static final String SLA_TYPE_TXRETRY = "txRetry";
	
	@Override
	public void init() {
		this.setReportSummary(MgrUtil.getUserMessage("report.summary.text.sla.device"));
		this.setReportTitle(MgrUtil.getUserMessage("report.title.text.sla.device", "All"));
		this.setReportValueTitle("Percentage (%)");
		if (isSupportSubType(SLA_TYPE_THROUGHPUT)) {
			this.addGroupReportEl(SLA_TYPE_THROUGHPUT);
			this.getGroupReportEl(SLA_TYPE_THROUGHPUT).setTitle(MgrUtil.getUserMessage("report.title.text.sla.device", "Throughput"));
			this.getGroupReportEl(SLA_TYPE_THROUGHPUT).setSummary(MgrUtil.getUserMessage("report.summary.text.sla.device"));
		}
		if (isSupportSubType(SLA_TYPE_CRCERROR)) {
			this.addGroupReportEl(SLA_TYPE_CRCERROR);
			this.getGroupReportEl(SLA_TYPE_CRCERROR).setTitle(MgrUtil.getUserMessage("report.title.text.sla.device", "CRC Error"));
			this.getGroupReportEl(SLA_TYPE_CRCERROR).setSummary(MgrUtil.getUserMessage("report.summary.text.sla.device"));
		}
		if (isSupportSubType(SLA_TYPE_AIRTIME)) {
			this.addGroupReportEl(SLA_TYPE_AIRTIME);
			this.getGroupReportEl(SLA_TYPE_AIRTIME).setTitle(MgrUtil.getUserMessage("report.title.text.sla.device", "Airtime"));
			this.getGroupReportEl(SLA_TYPE_AIRTIME).setSummary(MgrUtil.getUserMessage("report.summary.text.sla.device"));
		}
		if (isSupportSubType(SLA_TYPE_TXDROP)) {
			this.addGroupReportEl(SLA_TYPE_TXDROP);
			this.getGroupReportEl(SLA_TYPE_TXDROP).setTitle(MgrUtil.getUserMessage("report.title.text.sla.device", "Tx Drop"));
			this.getGroupReportEl(SLA_TYPE_TXDROP).setSummary(MgrUtil.getUserMessage("report.summary.text.sla.device"));
		}
		if (isSupportSubType(SLA_TYPE_RXDROP)) {
			this.addGroupReportEl(SLA_TYPE_RXDROP);
			this.getGroupReportEl(SLA_TYPE_RXDROP).setTitle(MgrUtil.getUserMessage("report.title.text.sla.device", "Rx Drop"));
			this.getGroupReportEl(SLA_TYPE_RXDROP).setSummary(MgrUtil.getUserMessage("report.summary.text.sla.device"));
		}
		if (isSupportSubType(SLA_TYPE_TXRETRY)) {
			this.addGroupReportEl(SLA_TYPE_TXRETRY);
			this.getGroupReportEl(SLA_TYPE_TXRETRY).setTitle(MgrUtil.getUserMessage("report.title.text.sla.device", "Tx Retry"));
			this.getGroupReportEl(SLA_TYPE_TXRETRY).setSummary(MgrUtil.getUserMessage("report.summary.text.sla.device"));
		}
	}

	@Override
	public void initReportChartExportEl() {
		this.setExportChartEl(new AhFreechartDatetimeStackedArea());
	}
	
	@Override
	protected void doCalculate() throws Exception {
		prepareChartData();
	}
	
	private void prepareChartData() throws Exception {
		if (this.getReportOptions().getApMacList() == null
				|| this.getReportOptions().getApMacList().isEmpty()) {
			return;
		}
		
		Class<? extends AhNewSLAStatsInterface> boClass =
			AhNewReportUtil.getSLAStatsCertainImplementClass(this.getPeriodType(), this.getStartTime(), this.getEndTime(),this.getReportOptions().isScheduleMode());
		boolean blnPercent = true;
		if (boClass.getName().equals(AhNewSLAStats.class.getName())) {
			blnPercent = false;
		}
		
		int tmpPeriodType = this.getPeriodType();
		if (this.getReportOptions().isScheduleMode()) {
			tmpPeriodType=-2;
		}
		Long timePadding = AhNewReportUtil.getTimeIntervalPadding(180000L, tmpPeriodType, this.getStartTime(), this.getEndTime());
		
		List<Long> timeTicks = null;
		if (!blnPercent) {
			timeTicks = getExactTimeTicks();
		} else {
			timeTicks = AhNewReportUtil.getAllTimeTicksToBeCalculated(this.getStartTime(), this.getEndTime(), timePadding);
		}
		if (timeTicks == null
				|| timeTicks.size() < 2) {
			return;
		}
		
		Integer apTotal_Red = 0;
		Integer apTotal_Yellow = 0;
		Integer apSla_Red = 0;
		Integer apSla_Yellow = 0;
		Integer apAirTime_Red = 0;
		Integer apCrcError_Red = 0;
		Integer apRetry_Red = 0;
		Integer apTxDrop_Red = 0;
		Integer apRxDrop_Red = 0;
		
		Long curCountTime = timeTicks.get(0);
		Long calStartTime = curCountTime;
		Long calEndTime = this.getEndTime();
		if (blnPercent) {
			calStartTime = calStartTime - timePadding + 1;
		}
		
		int timeTicksCount = timeTicks == null ? 0 : timeTicks.size();
		int curTimeTickIdx = 1;
		Set<Long> usedTimeTicks = new HashSet<Long>();
		
		Set<String> apMacs = this.getReportOptions().getApMacList();
		Integer deviceCount = apMacs.size();
		SortParams sort = new SortParams("timeStamp");
		String where;
		FilterParams filter;
		
		ReportPagingImpl<AhNewSLAStats> page = new ReportPagingImpl(boClass);
		page.setPageSize(5000);
		page.clearRowCount();
		
		List<?> bos;
		Map<Long, Map<String, Map<SLAKindsType, Integer>>> resultData = new HashMap<Long, Map<String, Map<SLAKindsType, Integer>>>();
		
		where = "timeStamp >= :s1 and timeStamp <= :s2 and apMac in (:s3)";
		filter = new FilterParams(where,
			new Object[] {calStartTime, calEndTime, apMacs});
		
		while(page.hasNext()) {
			if (curTimeTickIdx > timeTicksCount - 1) break;
			
			bos = page.next().executeQuery("select apTotal_Red, apTotal_Yellow, apSla_Red, apSla_Yellow,"
					+ " apAirTime_Red, apCrcError_Red, apRetry_Red, apTxDrop_Red, apRxDrop_Red, timeStamp"
					+ " from " + boClass.getSimpleName(),
					sort, filter, this.getDomainId());
			
			for(Object bo : bos) {
				if(bo == null) {
					continue;
				}
				
				Object[] stat = (Object[])bo;
				int colIndex = 0;
				Integer apTotal_Red_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Integer apTotal_Yellow_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Integer apSla_Red_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Integer apSla_Yellow_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Integer apAirTime_Red_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Integer apCrcError_Red_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Integer apRetry_Red_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Integer apTxDrop_Red_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Integer apRxDrop_Red_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Long recordTime = stat[colIndex] == null ? curCountTime : (Long)stat[colIndex];
				
				if (recordTime > curCountTime) {
					// count values for previous stage
					if (!resultData.containsKey(curCountTime)
							|| resultData.get(curCountTime) == null) {
						Map<String, Map<SLAKindsType, Integer>> mapTmp = new HashMap<String, Map<SLAKindsType, Integer>>();
						resultData.put(curCountTime, mapTmp);
					}
					if (deviceCount < 1) {
						deviceCount = 1;
					}
					Map<String, Map<SLAKindsType, Integer>> mapTmp = resultData.get(curCountTime);
					if (blnPercent) {
						setDataValues(mapTmp, SLA_TYPE_ALL, apTotal_Red/deviceCount, apTotal_Yellow/deviceCount);
						setDataValues(mapTmp, SLA_TYPE_THROUGHPUT, apSla_Red/deviceCount, apSla_Yellow/deviceCount);
						setDataValues(mapTmp, SLA_TYPE_CRCERROR, apCrcError_Red/deviceCount);
						setDataValues(mapTmp, SLA_TYPE_AIRTIME, apAirTime_Red/deviceCount);
						setDataValues(mapTmp, SLA_TYPE_TXDROP, apTxDrop_Red/deviceCount);
						setDataValues(mapTmp, SLA_TYPE_RXDROP, apRxDrop_Red/deviceCount);
						setDataValues(mapTmp, SLA_TYPE_TXRETRY, apRetry_Red/deviceCount);
					} else {
						setDataValues(mapTmp, SLA_TYPE_ALL, apTotal_Red*100/deviceCount, apTotal_Yellow*100/deviceCount);
						setDataValues(mapTmp, SLA_TYPE_THROUGHPUT, apSla_Red*100/deviceCount, apSla_Yellow*100/deviceCount);
						setDataValues(mapTmp, SLA_TYPE_CRCERROR, apCrcError_Red*100/deviceCount);
						setDataValues(mapTmp, SLA_TYPE_AIRTIME, apAirTime_Red*100/deviceCount);
						setDataValues(mapTmp, SLA_TYPE_TXDROP, apTxDrop_Red*100/deviceCount);
						setDataValues(mapTmp, SLA_TYPE_RXDROP, apRxDrop_Red*100/deviceCount);
						setDataValues(mapTmp, SLA_TYPE_TXRETRY, apRetry_Red*100/deviceCount);
					}
					
					apTotal_Red = 0;
					apTotal_Yellow = 0;
					apSla_Red = 0;
					apSla_Yellow = 0;
					apAirTime_Red = 0;
					apCrcError_Red = 0;
					apRetry_Red = 0;
					apTxDrop_Red = 0;
					apRxDrop_Red = 0;
					
					usedTimeTicks.add(curCountTime);
					while (recordTime > curCountTime
							&& curTimeTickIdx < timeTicksCount) {
						curCountTime = timeTicks.get(curTimeTickIdx++);
					}
				}
				
				if (blnPercent) {
					apTotal_Red += apTotal_Red_tmp;
					apTotal_Yellow += apTotal_Yellow_tmp;
					apSla_Red += apSla_Red_tmp;
					apSla_Yellow += apSla_Yellow_tmp;
					apAirTime_Red += apAirTime_Red_tmp;
					apCrcError_Red += apCrcError_Red_tmp;
					apRetry_Red += apRetry_Red_tmp;
					apTxDrop_Red += apTxDrop_Red_tmp;
					apRxDrop_Red += apRxDrop_Red_tmp;
				} else {
					if (apTotal_Red_tmp > 0) {
						apTotal_Red++;
					}
					if (apTotal_Yellow_tmp > 0) {
						apTotal_Yellow++;
					}
					if (apSla_Red_tmp > 0) {
						apSla_Red++;
					}
					if (apSla_Yellow_tmp > 0) {
						apSla_Yellow++;
					}
					if (apAirTime_Red_tmp > 0) {
						apAirTime_Red++;
					}
					if (apCrcError_Red_tmp > 0) {
						apCrcError_Red++;
					}
					if (apRetry_Red_tmp > 0) {
						apRetry_Red++;
					}
					if (apTxDrop_Red_tmp > 0) {
						apTxDrop_Red++;
					}
					if (apRxDrop_Red_tmp > 0) {
						apRxDrop_Red++;
					}
				}
				
			}
		}
		
		if (!resultData.containsKey(curCountTime)) {
			Map<String, Map<SLAKindsType, Integer>> mapTmp = new HashMap<String, Map<SLAKindsType, Integer>>();
			resultData.put(curCountTime, mapTmp);
			if (blnPercent) {
				setDataValues(mapTmp, SLA_TYPE_ALL, apTotal_Red/deviceCount, apTotal_Yellow/deviceCount);
				setDataValues(mapTmp, SLA_TYPE_THROUGHPUT, apSla_Red/deviceCount, apSla_Yellow/deviceCount);
				setDataValues(mapTmp, SLA_TYPE_CRCERROR, apCrcError_Red/deviceCount);
				setDataValues(mapTmp, SLA_TYPE_AIRTIME, apAirTime_Red/deviceCount);
				setDataValues(mapTmp, SLA_TYPE_TXDROP, apTxDrop_Red/deviceCount);
				setDataValues(mapTmp, SLA_TYPE_RXDROP, apRxDrop_Red/deviceCount);
				setDataValues(mapTmp, SLA_TYPE_TXRETRY, apRetry_Red/deviceCount);
			} else {
				setDataValues(mapTmp, SLA_TYPE_ALL, apTotal_Red*100/deviceCount, apTotal_Yellow*100/deviceCount);
				setDataValues(mapTmp, SLA_TYPE_THROUGHPUT, apSla_Red*100/deviceCount, apSla_Yellow*100/deviceCount);
				setDataValues(mapTmp, SLA_TYPE_CRCERROR, apCrcError_Red*100/deviceCount);
				setDataValues(mapTmp, SLA_TYPE_AIRTIME, apAirTime_Red*100/deviceCount);
				setDataValues(mapTmp, SLA_TYPE_TXDROP, apTxDrop_Red*100/deviceCount);
				setDataValues(mapTmp, SLA_TYPE_RXDROP, apRxDrop_Red*100/deviceCount);
				setDataValues(mapTmp, SLA_TYPE_TXRETRY, apRetry_Red*100/deviceCount);
			}
			
			usedTimeTicks.add(curCountTime);
		}
		
		for (Long timetick : timeTicks) {
			if (!usedTimeTicks.contains(timetick)) {
				resultData.put(timetick, null);
			}
		}
		
		if (resultData != null
				&& !resultData.isEmpty()) {
			if (isSupportSubType(SLA_TYPE_ALL)) {
				encapChartSeries(getSeperateDataFromResult(resultData, SLA_TYPE_ALL), SLA_TYPE_ALL, curCountTime, timePadding);
			}
			if (isSupportSubType(SLA_TYPE_THROUGHPUT)) {
				encapChartSeries(getSeperateDataFromResult(resultData, SLA_TYPE_THROUGHPUT), SLA_TYPE_THROUGHPUT, curCountTime, timePadding);
			}
			if (isSupportSubType(SLA_TYPE_CRCERROR)) {
				encapChartSeries(getSeperateDataFromResult(resultData, SLA_TYPE_CRCERROR), SLA_TYPE_CRCERROR, curCountTime, timePadding);
			}
			if (isSupportSubType(SLA_TYPE_AIRTIME)) {
				encapChartSeries(getSeperateDataFromResult(resultData, SLA_TYPE_AIRTIME), SLA_TYPE_AIRTIME, curCountTime, timePadding);
			}
			if (isSupportSubType(SLA_TYPE_TXDROP)) {
				encapChartSeries(getSeperateDataFromResult(resultData, SLA_TYPE_TXDROP), SLA_TYPE_TXDROP, curCountTime, timePadding);
			}
			if (isSupportSubType(SLA_TYPE_RXDROP)) {
				encapChartSeries(getSeperateDataFromResult(resultData, SLA_TYPE_RXDROP), SLA_TYPE_RXDROP, curCountTime, timePadding);
			}
			if (isSupportSubType(SLA_TYPE_TXRETRY)) {
				encapChartSeries(getSeperateDataFromResult(resultData, SLA_TYPE_TXRETRY), SLA_TYPE_TXRETRY, curCountTime, timePadding);
			}
		} else {
			Map<Long, Map<SLAKindsType, Integer>> emptyMap = new HashMap<Long, Map<SLAKindsType, Integer>>();
			AhNewReportUtil.fillNullValuesWithPadding(emptyMap, this.getStartTime(), this.getEndTime(), timePadding);
			if (isSupportSubType(SLA_TYPE_ALL)) {
				encapChartSeries(emptyMap, SLA_TYPE_ALL, curCountTime, timePadding);
			}
			if (isSupportSubType(SLA_TYPE_THROUGHPUT)) {
				encapChartSeries(emptyMap, SLA_TYPE_THROUGHPUT, curCountTime, timePadding);
			}
			if (isSupportSubType(SLA_TYPE_CRCERROR)) {
				encapChartSeries(emptyMap, SLA_TYPE_CRCERROR, curCountTime, timePadding);
			}
			if (isSupportSubType(SLA_TYPE_AIRTIME)) {
				encapChartSeries(emptyMap, SLA_TYPE_AIRTIME, curCountTime, timePadding);
			}
			if (isSupportSubType(SLA_TYPE_TXDROP)) {
				encapChartSeries(emptyMap, SLA_TYPE_TXDROP, curCountTime, timePadding);
			}
			if (isSupportSubType(SLA_TYPE_RXDROP)) {
				encapChartSeries(emptyMap, SLA_TYPE_RXDROP, curCountTime, timePadding);
			}
			if (isSupportSubType(SLA_TYPE_TXRETRY)) {
				encapChartSeries(emptyMap, SLA_TYPE_TXRETRY, curCountTime, timePadding);
			}
		}
	}
	
	protected boolean isSupportSubType(String subType) {
		if (this.isBlnGroupCal()) {
			return true;
		}
		String subTypeTmp = this.getRequest().getSubType();
		if ((SLA_TYPE_ALL.equals(subType)
				|| StringUtils.isBlank(subType))
			&& (SLA_TYPE_ALL.equals(subTypeTmp)
					|| StringUtils.isBlank(subTypeTmp))) {
			return true;
		} else if (!StringUtils.isBlank(subTypeTmp)
				&& subTypeTmp.equals(subType)) {
			return true;
		}
		
		return false;
	}
	
	protected boolean isMainReport(String subType) {
		if (SLA_TYPE_ALL.equals(subType)
				|| StringUtils.isBlank(subType)) {
			return true;
		}
		return false;
	}
	
	protected boolean isSupportYellow(String subType) {
		if (SLA_TYPE_THROUGHPUT.equals(subType)
				|| SLA_TYPE_ALL.equals(subType)
				|| StringUtils.isBlank(subType)) {
			return true;
		}
		return false;
	}
	
	protected void prepareSampleData() {
		int tmpPeriodType = this.getPeriodType();
		if (this.getReportOptions().isScheduleMode()) {
			tmpPeriodType=-2;
		}
		Long timePadding = AhNewReportUtil.getTimeIntervalPadding(180000L, tmpPeriodType, this.getStartTime(), this.getEndTime());
		Long curCountTime = this.getStartTime();
		
		Map<Long, Map<SLAKindsType, Integer>> emptyMap = new HashMap<Long, Map<SLAKindsType, Integer>>();
		AhNewReportUtil.fillNullValuesWithPadding(emptyMap, this.getStartTime(), this.getEndTime(), timePadding);
		if (isSupportSubType(SLA_TYPE_THROUGHPUT)) {
			encapChartSeries(emptyMap, SLA_TYPE_THROUGHPUT, curCountTime, timePadding);
		}
		if (isSupportSubType(SLA_TYPE_CRCERROR)) {
			encapChartSeries(emptyMap, SLA_TYPE_CRCERROR, curCountTime, timePadding);
		}
		if (isSupportSubType(SLA_TYPE_AIRTIME)) {
			encapChartSeries(emptyMap, SLA_TYPE_AIRTIME, curCountTime, timePadding);
		}
		if (isSupportSubType(SLA_TYPE_TXDROP)) {
			encapChartSeries(emptyMap, SLA_TYPE_TXDROP, curCountTime, timePadding);
		}
		if (isSupportSubType(SLA_TYPE_RXDROP)) {
			encapChartSeries(emptyMap, SLA_TYPE_RXDROP, curCountTime, timePadding);
		}
		if (isSupportSubType(SLA_TYPE_TXRETRY)) {
			encapChartSeries(emptyMap, SLA_TYPE_TXRETRY, curCountTime, timePadding);
		}
		
		if (isSupportSubType(SLA_TYPE_ALL)) {
			List<Long> timePoints = new LinkedList<Long>();
			while (curCountTime <= this.getEndTime()) {
				timePoints.add(curCountTime);
				curCountTime += timePadding;
			}
			
			Integer[] redValues = new Integer[]{6, 12, 19, 8, 5, 0, 8, 20, 11, 9};
			Integer[] yellowValues = new Integer[]{8, 5, 0, 9, 12, 0, 8};
			
			int redLen = redValues.length;
			int yellowLen = yellowValues.length;
			
			Map<Long, Map<SLAKindsType, Integer>> resultMap = new HashMap<Long, Map<SLAKindsType, Integer>>();
			int curIdx = 0;
			for (Long timePoint : timePoints) {
				Map<SLAKindsType, Integer> mapTmp = new HashMap<SLAKindsType, Integer>();
				mapTmp.put(SLAKindsType.RED, redValues[curIdx++%redLen]);
				mapTmp.put(SLAKindsType.YELLOW, yellowValues[curIdx++%yellowLen]);
				resultMap.put(timePoint, mapTmp);
			}
			encapChartSeries(resultMap, SLA_TYPE_ALL, this.getEndTime(), timePadding);
		}
	}
	
}
