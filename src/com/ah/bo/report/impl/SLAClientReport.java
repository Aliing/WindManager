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

@AhReportConfig(id=AhReportProperties.REPORT_SLA_CLIENT,
		builder=DefaultNewReportBuilder.class)
public class SLAClientReport extends SLABaseReport {

	public static final String SLA_TYPE_ALL = "all";
	public static final String SLA_TYPE_THROUGHPUT = "throughput";
	public static final String SLA_TYPE_AIRTIME = "airtime";
	public static final String SLA_TYPE_HEALTH = "health";
	
	@Override
	public void init() {
		this.setReportSummary(MgrUtil.getUserMessage("report.summary.text.sla.client"));
		this.setReportTitle(MgrUtil.getUserMessage("report.title.text.sla.client", "All"));
		this.setReportValueTitle("Percentage (%)");
		if (isSupportSubType(SLA_TYPE_THROUGHPUT)) {
			this.addGroupReportEl(SLA_TYPE_THROUGHPUT);
			this.getGroupReportEl(SLA_TYPE_THROUGHPUT).setTitle(MgrUtil.getUserMessage("report.title.text.sla.client", "Throughput"));
			this.getGroupReportEl(SLA_TYPE_THROUGHPUT).setSummary(MgrUtil.getUserMessage("report.summary.text.sla.client"));
		}
		if (isSupportSubType(SLA_TYPE_AIRTIME)) {
			this.addGroupReportEl(SLA_TYPE_AIRTIME);
			this.getGroupReportEl(SLA_TYPE_AIRTIME).setTitle(MgrUtil.getUserMessage("report.title.text.sla.client", "Airtime"));
			this.getGroupReportEl(SLA_TYPE_AIRTIME).setSummary(MgrUtil.getUserMessage("report.summary.text.sla.client"));
		}
		if (isSupportSubType(SLA_TYPE_HEALTH)) {
			this.addGroupReportEl(SLA_TYPE_HEALTH);
			this.getGroupReportEl(SLA_TYPE_HEALTH).setTitle(MgrUtil.getUserMessage("report.title.text.sla.client", "Health"));
			this.getGroupReportEl(SLA_TYPE_HEALTH).setSummary(MgrUtil.getUserMessage("report.summary.text.sla.client"));
		}
		subReportSeriesNameType = "Clients(%)";
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
		boolean blnOriginalTblData = false;
		if (boClass.getName().equals(AhNewSLAStats.class.getName())) {
			blnOriginalTblData = true;
		}
		
		int tmpPeriodType = this.getPeriodType();
		if (this.getReportOptions().isScheduleMode()) {
			tmpPeriodType=-2;
		}
		Long timePadding = AhNewReportUtil.getTimeIntervalPadding(180000L, tmpPeriodType, this.getStartTime(), this.getEndTime());
		
		List<Long> timeTicks = null;
		if (blnOriginalTblData) {
			timeTicks = getExactTimeTicks();
		} else {
			timeTicks = AhNewReportUtil.getAllTimeTicksToBeCalculated(this.getStartTime(), this.getEndTime(), timePadding);
		}
		if (timeTicks == null
				|| timeTicks.size() < 2) {
			return;
		}
		
		Integer clientTotal_Red = 0;
		Integer clientTotal_Yellow = 0;
		Integer clientSla_Red = 0;
		Integer clientSla_Yellow = 0;
		Integer clientAirTime_Red = 0;
		Integer clientScore_Red = 0;
		Integer clientScore_Yellow = 0;
		
		Long curCountTime = timeTicks.get(0);
		Long calStartTime = curCountTime;
		Long calEndTime = this.getEndTime();
		if (!blnOriginalTblData) {
			calStartTime = calStartTime - timePadding + 1;
		}
		
		int timeTicksCount = timeTicks == null ? 0 : timeTicks.size();
		int curTimeTickIdx = 1;
		Set<Long> usedTimeTicks = new HashSet<Long>();
		Integer regionClientsCount = 0;
		
		Set<String> apMacs = this.getReportOptions().getApMacList();
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
			
			bos = page.next().executeQuery("select clientTotal_Red, clientTotal_Yellow, clientSla_Red, clientSla_Yellow,"
					+ " clientAirTime_Red, clientScore_Red, clientScore_Yellow, clientCount, timeStamp"
					+ " from " + boClass.getSimpleName(),
					sort, filter, this.getDomainId());
			
			for(Object bo : bos) {
				if(bo == null) {
					continue;
				}
				
				Object[] stat = (Object[])bo;
				int colIndex = 0;
				Integer clientTotal_Red_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Integer clientTotal_Yellow_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Integer clientSla_Red_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Integer clientSla_Yellow_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Integer clientAirTime_Red_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Integer clientScore_Red_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Integer clientScore_Yellow_tmp = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Integer curClientsCount = stat[colIndex] == null ? 0 : (Integer)stat[colIndex];
				colIndex++; Long recordTime = stat[colIndex] == null ? curCountTime : (Long)stat[colIndex];
				
				if (recordTime > curCountTime) {
					// count values for previous stage
					if (!resultData.containsKey(curCountTime)) {
						Map<String, Map<SLAKindsType, Integer>> mapTmp = new HashMap<String, Map<SLAKindsType, Integer>>();
						resultData.put(curCountTime, mapTmp);
					}
					if (regionClientsCount < 1) {
						regionClientsCount = 1;
					}
					Map<String, Map<SLAKindsType, Integer>> mapTmp = resultData.get(curCountTime);
					setDataValues(mapTmp, SLA_TYPE_ALL, clientTotal_Red/regionClientsCount, clientTotal_Yellow/regionClientsCount);
					setDataValues(mapTmp, SLA_TYPE_THROUGHPUT, clientSla_Red/regionClientsCount, clientSla_Yellow/regionClientsCount);
					setDataValues(mapTmp, SLA_TYPE_AIRTIME, clientAirTime_Red/regionClientsCount);
					setDataValues(mapTmp, SLA_TYPE_HEALTH, clientScore_Red/regionClientsCount, clientScore_Yellow/regionClientsCount);
					
					clientTotal_Red = 0;
					clientTotal_Yellow = 0;
					clientSla_Red = 0;
					clientSla_Yellow = 0;
					clientAirTime_Red = 0;
					clientScore_Red = 0;
					clientScore_Yellow = 0;
					
					regionClientsCount = 0;
					usedTimeTicks.add(curCountTime);
					while (recordTime > curCountTime
							&& curTimeTickIdx < timeTicksCount) {
						curCountTime = timeTicks.get(curTimeTickIdx++);
					}
				}
				
				clientTotal_Red += clientTotal_Red_tmp * curClientsCount;
				clientTotal_Yellow += clientTotal_Yellow_tmp * curClientsCount;
				clientSla_Red += clientSla_Red_tmp * curClientsCount;
				clientSla_Yellow += clientSla_Yellow_tmp * curClientsCount;
				clientAirTime_Red += clientAirTime_Red_tmp * curClientsCount;
				clientScore_Red += clientScore_Red_tmp * curClientsCount;
				clientScore_Yellow += clientScore_Yellow_tmp * curClientsCount;
				
				regionClientsCount += curClientsCount;
			}
		}
		
		if (!resultData.containsKey(curCountTime)) {
			Map<String, Map<SLAKindsType, Integer>> mapTmp = new HashMap<String, Map<SLAKindsType, Integer>>();
			resultData.put(curCountTime, mapTmp);
			if (regionClientsCount < 1) {
				regionClientsCount = 1;
			}
			setDataValues(mapTmp, SLA_TYPE_ALL, clientTotal_Red/regionClientsCount, clientTotal_Yellow/regionClientsCount);
			setDataValues(mapTmp, SLA_TYPE_THROUGHPUT, clientSla_Red/regionClientsCount, clientSla_Yellow/regionClientsCount);
			setDataValues(mapTmp, SLA_TYPE_AIRTIME, clientAirTime_Red/regionClientsCount);
			setDataValues(mapTmp, SLA_TYPE_HEALTH, clientScore_Red/regionClientsCount, clientScore_Yellow/regionClientsCount);
			
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
			if (isSupportSubType(SLA_TYPE_AIRTIME)) {
				encapChartSeries(getSeperateDataFromResult(resultData, SLA_TYPE_AIRTIME), SLA_TYPE_AIRTIME, curCountTime, timePadding);
			}
			if (isSupportSubType(SLA_TYPE_HEALTH)) {
				encapChartSeries(getSeperateDataFromResult(resultData, SLA_TYPE_HEALTH), SLA_TYPE_HEALTH, curCountTime, timePadding);
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
			if (isSupportSubType(SLA_TYPE_AIRTIME)) {
				encapChartSeries(emptyMap, SLA_TYPE_AIRTIME, curCountTime, timePadding);
			}
			if (isSupportSubType(SLA_TYPE_HEALTH)) {
				encapChartSeries(emptyMap, SLA_TYPE_HEALTH, curCountTime, timePadding);
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
				|| SLA_TYPE_HEALTH.equals(subType)
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
		if (isSupportSubType(SLA_TYPE_AIRTIME)) {
			encapChartSeries(emptyMap, SLA_TYPE_AIRTIME, curCountTime, timePadding);
		}
		if (isSupportSubType(SLA_TYPE_HEALTH)) {
			encapChartSeries(emptyMap, SLA_TYPE_HEALTH, curCountTime, timePadding);
		}
		
		if (isSupportSubType(SLA_TYPE_ALL)) {
			List<Long> timePoints = new LinkedList<Long>();
			while (curCountTime <= this.getEndTime()) {
				timePoints.add(curCountTime);
				curCountTime += timePadding;
			}
			
			Integer[] redValues = new Integer[]{4, 7, 9, 3, 5, 0, 8, 14, 11, 9};
			Integer[] yellowValues = new Integer[]{3, 5, 2, 1, 0, 0, 7};
			
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
