package com.ah.bo.report.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.performance.AhSLAStats;
import com.ah.bo.report.AhLinearSeries;
import com.ah.bo.report.AhReportProperties;

public abstract class SLABaseReport extends AhAbstractNewReport {
	protected String subReportSeriesNameType = "Devices(%)";
	
	protected enum SLAKindsType {
		RED("Noncompliant"), YELLOW("Warning"), GREEN("Compliant");
		
		private String value;
		public String getValue() {
			return this.value;
		}
		SLAKindsType(String value) {
			this.value = value;
		}
	}

	@Override
	public abstract void init();

	@Override
	protected abstract void doCalculate() throws Exception;
	
	protected abstract boolean isSupportSubType(String subType);
	
	protected abstract boolean isMainReport(String subType);
	
	protected abstract boolean isSupportYellow(String subType);
	
	protected Map<SLAKindsType, Integer> setFullSupportedSLAKindTypeRange(Map<SLAKindsType, Integer> map) {
		if (map == null) {
			map = new HashMap<SLAKindsType, Integer>(2);
		}
		map.put(SLAKindsType.RED, null);
		map.put(SLAKindsType.YELLOW, null);
		
		return map;
	}

	protected void setDataValues(Map<String, Map<SLAKindsType, Integer>> map, String subType, Integer redValue) {
		this.setDataValues(map, subType, redValue, null);
	}
	protected void setDataValues(Map<String, Map<SLAKindsType, Integer>> map, String subType, Integer redValue, Integer yellowValue) {
		if (!isSupportSubType(subType)) {
			return;
		}
		if (!map.containsKey(subType)) {
			Map<SLAKindsType, Integer> mapTmp = setFullSupportedSLAKindTypeRange(null);
			map.put(subType, mapTmp);
		}
		Map<SLAKindsType, Integer> mapTmp = map.get(subType);
		if (mapTmp.containsKey(SLAKindsType.RED)) {
			mapTmp.put(SLAKindsType.RED, redValue);
		}
		if (mapTmp.containsKey(SLAKindsType.YELLOW)) {
			mapTmp.put(SLAKindsType.YELLOW, yellowValue);
		}
	}
	
	protected Map<Long, Map<SLAKindsType, Integer>> getSeperateDataFromResult(Map<Long, Map<String, Map<SLAKindsType, Integer>>> map, String subType) {
		if (!isSupportSubType(subType)
				|| map == null
				|| map.isEmpty()) {
			return null;
		}
		Map<Long, Map<SLAKindsType, Integer>> result = new HashMap<Long, Map<SLAKindsType, Integer>>(map.size());
		for (Long key : map.keySet()) {
			if (map.get(key) != null
					&& map.get(key).containsKey(subType)) {
				result.put(key, map.get(key).get(subType));
			} else {
				result.put(key, null);
			}
		}
		
		return result;
	}
	
	protected void encapChartSeries(Map<Long, Map<SLAKindsType, Integer>> map, String subType, Long lastRecordTime, Long padding) {
		if (!isSupportSubType(subType)) {
			return;
		}
		List<Map.Entry<Long, Map<SLAKindsType, Integer>>> mapList = new ArrayList<Map.Entry<Long, Map<SLAKindsType, Integer>>>(map.entrySet());
		Collections.sort(mapList, new Comparator<Map.Entry<Long, Map<SLAKindsType, Integer>>>() {
			@Override
			public int compare(Map.Entry<Long, Map<SLAKindsType, Integer>> entry1, Map.Entry<Long, Map<SLAKindsType, Integer>> entry2) {
				return entry1.getKey().compareTo(entry2.getKey());
			}
		});
		
		String groupName = "slaGroup";
		Long curIndex = 1L;
		AhLinearSeries seriesRed = new AhLinearSeries();
		seriesRed.setId(curIndex++);
		seriesRed.setName(SLAKindsType.RED.getValue() + " " + subReportSeriesNameType);
		seriesRed.setShowType(AhReportProperties.SERIES_TYPE_AREA);
		seriesRed.setStackGroup(groupName);
		seriesRed.setCustomColor(AhReportProperties.namedColors.RED.getValue());
		
		AhLinearSeries seriesYellow = null;
		if (this.isSupportYellow(subType)) {
			seriesYellow = new AhLinearSeries();
			seriesYellow.setId(curIndex++);
			seriesYellow.setName(SLAKindsType.YELLOW.getValue() + " " + subReportSeriesNameType);
			seriesYellow.setShowType(AhReportProperties.SERIES_TYPE_AREA);
			seriesYellow.setStackGroup(groupName);
			seriesYellow.setCustomColor(AhReportProperties.namedColors.YELLOW.getValue());
		}
		
		AhLinearSeries seriesGreen = new AhLinearSeries();
		seriesGreen.setId(curIndex++);
		seriesGreen.setName(SLAKindsType.GREEN.getValue() + " " + subReportSeriesNameType);
		seriesGreen.setShowType(AhReportProperties.SERIES_TYPE_AREA);
		seriesGreen.setStackGroup(groupName);
		seriesGreen.setCustomColor(AhReportProperties.namedColors.SEAGREEN.getValue());
		
		if (isMainReport(subType)) {
			this.addSeries(seriesRed);
			if (seriesYellow != null) {
				this.addSeries(seriesYellow);
			}
			this.addSeries(seriesGreen);
		} else {
			this.getGroupReportEl(subType).addSeries(seriesRed);
			if (seriesYellow != null) {
				this.getGroupReportEl(subType).addSeries(seriesYellow);
			}
			this.getGroupReportEl(subType).addSeries(seriesGreen);
		}
		
		for (Map.Entry<Long, Map<SLAKindsType, Integer>> item : mapList) {
			Map<SLAKindsType, Integer> mapTmp = item.getValue();
			Integer redValue = 0;
			Integer yellowValue = 0;
			Integer greenValue = 100;
			if (mapTmp != null) {
				redValue = mapTmp.get(SLAKindsType.RED) == null ? 0 : mapTmp.get(SLAKindsType.RED);
				yellowValue = mapTmp.get(SLAKindsType.YELLOW) == null ? 0 : mapTmp.get(SLAKindsType.YELLOW);
				greenValue = 100 - redValue - yellowValue;
				greenValue = greenValue < 0 ? 0 : greenValue;
			}
			seriesRed.addData(item.getKey(), redValue);
			seriesGreen.addData(item.getKey(), greenValue);
			if (seriesYellow != null) {
				seriesYellow.addData(item.getKey(), yellowValue);
			}
		}
	}
	
	protected List<Long> getExactTimeTicks() {
		SortParams sort = new SortParams("timeStamp");
		String where = "timeStamp >= :s1 and timeStamp <= :s2 and globalFlag is true";
		FilterParams filter = new FilterParams(where,
			new Object[] {this.getStartTime(), this.getEndTime()});
		
		List<?> bos = QueryUtil.executeQuery("select stat.timeStamp from " + AhSLAStats.class.getSimpleName() + " stat", sort, filter);
		
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
}
