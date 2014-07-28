package com.ah.util.bo.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.performance.AhClientStats;
import com.ah.bo.performance.AhClientStatsDay;
import com.ah.bo.performance.AhClientStatsHour;
import com.ah.bo.performance.AhClientStatsInterf;
import com.ah.bo.performance.AhClientStatsWeek;
import com.ah.bo.performance.AhClientsOsInfoCount;
import com.ah.bo.performance.AhClientsOsInfoCountDay;
import com.ah.bo.performance.AhClientsOsInfoCountHour;
import com.ah.bo.performance.AhClientsOsInfoCountInterface;
import com.ah.bo.performance.AhClientsOsInfoCountWeek;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.bo.performance.AhInterfaceStatsDay;
import com.ah.bo.performance.AhInterfaceStatsHour;
import com.ah.bo.performance.AhInterfaceStatsInterf;
import com.ah.bo.performance.AhInterfaceStatsWeek;
import com.ah.bo.performance.AhNewReport;
import com.ah.bo.performance.AhNewSLAStats;
import com.ah.bo.performance.AhNewSLAStatsDay;
import com.ah.bo.performance.AhNewSLAStatsHour;
import com.ah.bo.performance.AhNewSLAStatsInterface;
import com.ah.bo.performance.AhNewSLAStatsWeek;
import com.ah.bo.performance.AhSsidClientsCount;
import com.ah.bo.performance.AhSsidClientsCountDay;
import com.ah.bo.performance.AhSsidClientsCountHour;
import com.ah.bo.performance.AhSsidClientsCountInterface;
import com.ah.bo.performance.AhSsidClientsCountWeek;
import com.ah.bo.report.AhSeries;

public class AhNewReportUtil {
	public static final short DATA_TABLE_PROPER_TO_USE_NORMAL = 1;
	public static final short DATA_TABLE_PROPER_TO_USE_HOUR = 2;
	public static final short DATA_TABLE_PROPER_TO_USE_DAY = 3;
	public static final short DATA_TABLE_PROPER_TO_USE_WEEK = 4;
	
	public static enum DataUnits {
		B("B"), KB("KB"), MB("MB"), GB("GB"), TB("TB"), PB("PB");
		
		private String unitName;
		public String getUnitName() {
			return unitName;
		}
		DataUnits() {
			this.unitName = "N/A";
		}
		DataUnits(String unitName) {
			this.unitName = unitName;
		}
	}
	
	public static enum RadioModeType {
		NONE, GHz24("2.4 GHz"), GHz50("5 GHz"), WIRED("Wired"), TOTAL("Total");
		
		private String value;
		public String getValue() {
			return this.value;
		}
		RadioModeType() {
			this.value = "N/A";
		}
		RadioModeType(String value) {
			this.value = value;
		}
	}
	
	public static short whatDataTableToUse(int period, Long startTime, Long endTime) {
		if (period >= 0) {
			switch(period) {
				case AhNewReport.NEW_REPORT_PERIOD_LASTONEHOUR:
				case AhNewReport.NEW_REPORT_PERIOD_LASTCLOCKHOUR:
					return DATA_TABLE_PROPER_TO_USE_NORMAL;
				case AhNewReport.NEW_REPORT_PERIOD_LASTONEDAY:
				case AhNewReport.NEW_REPORT_PERIOD_LASTCALENDARDAY:
					return DATA_TABLE_PROPER_TO_USE_HOUR;
				case AhNewReport.NEW_REPORT_PERIOD_LASTWEEK:
				case AhNewReport.NEW_REPORT_PERIOD_LASTCALENDARWEEK:
					return DATA_TABLE_PROPER_TO_USE_DAY;
				case AhNewReport.NEW_REPORT_PERIOD_LASTONEMONTH:
				case AhNewReport.NEW_REPORT_PERIOD_LASTCALENDARMONTH:
					return DATA_TABLE_PROPER_TO_USE_WEEK;
				default:
					return DATA_TABLE_PROPER_TO_USE_NORMAL;
			}
		} else if (period==-2) {
			Long diffTime = endTime - startTime;
			if (diffTime.compareTo(30*24*60*60*1000L) > 0) {
				return DATA_TABLE_PROPER_TO_USE_WEEK;
				// scheduler not use day table
			} else if (diffTime.compareTo(3*24*60*60*1000L + 3600000) >= 0) {
				return DATA_TABLE_PROPER_TO_USE_DAY;
			} else if (diffTime.compareTo(12*60*60*1000L) > 0) {
				return DATA_TABLE_PROPER_TO_USE_HOUR;
			}
		} else if (startTime != null && endTime != null) {
			Long diffTime = endTime - startTime;
			if (diffTime.compareTo(30*24*60*60*1000L) > 0) {
				return DATA_TABLE_PROPER_TO_USE_WEEK;
			} else if (diffTime.compareTo(3*24*60*60*1000L) > 0) {
				return DATA_TABLE_PROPER_TO_USE_DAY;
			} else if (diffTime.compareTo(12*60*60*1000L) > 0) {
				return DATA_TABLE_PROPER_TO_USE_HOUR;
			}
		}
		return DATA_TABLE_PROPER_TO_USE_NORMAL;
	}
	
	public static Class<? extends AhInterfaceStatsInterf> getInterfaceStatsCertainImplementClass(int periodType, Long startTime, Long endTime, boolean schedule) {
		short dataTableType;
		if (schedule) {
			dataTableType = AhNewReportUtil.whatDataTableToUse(-2, startTime, endTime);
		} else if (periodType == AhNewReport.NEW_REPORT_PERIOD_CUSTOM) {
			dataTableType = AhNewReportUtil.whatDataTableToUse(-1, startTime, endTime);
		} else {
			dataTableType = AhNewReportUtil.whatDataTableToUse(periodType, null, null);
		}
		
		switch (dataTableType) {
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_HOUR:
				return AhInterfaceStatsHour.class;
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_DAY:
				return AhInterfaceStatsDay.class;
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_WEEK:
				return AhInterfaceStatsWeek.class;
		}
		
		return AhInterfaceStats.class;
	}
	
	public static Class<? extends AhSsidClientsCountInterface> getSsidClientsCountCertainImplementClass(int periodType, Long startTime, Long endTime, boolean schedule) {
		short dataTableType;
		if (schedule) {
			dataTableType = AhNewReportUtil.whatDataTableToUse(-2, startTime, endTime);
		} else if (periodType == AhNewReport.NEW_REPORT_PERIOD_CUSTOM) {
			dataTableType = AhNewReportUtil.whatDataTableToUse(-1, startTime, endTime);
		} else {
			dataTableType = AhNewReportUtil.whatDataTableToUse(periodType, null, null);
		}
		
		switch (dataTableType) {
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_HOUR:
				return AhSsidClientsCountHour.class;
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_DAY:
				return AhSsidClientsCountDay.class;
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_WEEK:
				return AhSsidClientsCountWeek.class;
		}
		
		return AhSsidClientsCount.class;
	}
	
	public static Class<? extends AhClientStatsInterf> getClientStatsCertainImplementClass(int periodType, Long startTime, Long endTime,boolean schedule) {
		short dataTableType;
		if (schedule) {
			dataTableType = AhNewReportUtil.whatDataTableToUse(-2, startTime, endTime);
		} else if (periodType == AhNewReport.NEW_REPORT_PERIOD_CUSTOM) {
			dataTableType = AhNewReportUtil.whatDataTableToUse(-1, startTime, endTime);
		} else {
			dataTableType = AhNewReportUtil.whatDataTableToUse(periodType, null, null);
		}
		
		switch (dataTableType) {
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_HOUR:
				return AhClientStatsHour.class;
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_DAY:
				return AhClientStatsDay.class;
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_WEEK:
				return AhClientStatsWeek.class;
		}
		
		return AhClientStats.class;
	}
	
	public static Class<? extends AhClientsOsInfoCountInterface> getClientOSInfoCertainImplementClass(int periodType, Long startTime, Long endTime,boolean schedule) {
		short dataTableType;
		if (schedule) {
			dataTableType = AhNewReportUtil.whatDataTableToUse(-2, startTime, endTime);
		} else if (periodType == AhNewReport.NEW_REPORT_PERIOD_CUSTOM) {
			dataTableType = AhNewReportUtil.whatDataTableToUse(-1, startTime, endTime);
		} else {
			dataTableType = AhNewReportUtil.whatDataTableToUse(periodType, null, null);
		}
		
		switch (dataTableType) {
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_HOUR:
				return AhClientsOsInfoCountHour.class;
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_DAY:
				return AhClientsOsInfoCountDay.class;
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_WEEK:
				return AhClientsOsInfoCountWeek.class;
		}
		
		return AhClientsOsInfoCount.class;
	}
	
	public static Class<? extends AhNewSLAStatsInterface> getSLAStatsCertainImplementClass(int periodType, Long startTime, Long endTime,boolean schedule) {
		short dataTableType;
		if (schedule) {
			dataTableType = AhNewReportUtil.whatDataTableToUse(-2, startTime, endTime);
		} else if (periodType == AhNewReport.NEW_REPORT_PERIOD_CUSTOM) {
			dataTableType = AhNewReportUtil.whatDataTableToUse(-1, startTime, endTime);
		} else {
			dataTableType = AhNewReportUtil.whatDataTableToUse(periodType, null, null);
		}
		
		switch (dataTableType) {
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_HOUR:
				return AhNewSLAStatsHour.class;
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_DAY:
				return AhNewSLAStatsDay.class;
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_WEEK:
				return AhNewSLAStatsWeek.class;
		}
		
		return AhNewSLAStats.class;
	}
	
	public static void fillNullValuesWithPadding(Map map, Long startTime, Long endTime, Long padding) {
		fillValuesWithPadding(map, startTime, endTime, padding, null);
	}
	/**
	 * fill map with datetime info for certain time padding, defValue will be set as value
	 * 
	 * @param map map to fill, whose key type must be Long!!!!
	 * @param startTime
	 * @param endTime
	 * @param padding
	 * @return
	 */
	public static void fillValuesWithPadding(Map map, Long startTime, Long endTime, Long padding, Object defValue) {
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
		
		Long curTime = startTime;
		while (curTime <= endTime) {
			if (!map.containsKey(curTime)) {
				map.put(curTime, defValue);
			}
			curTime += padding;
		}
	}
	
	public static long getTimeIntervalPadding(long defaultValue, int periodType, long startTime, long endTime) {
		// the default is 20 minutes
		Long result = defaultValue;
		if (periodType == AhNewReport.NEW_REPORT_PERIOD_CUSTOM) {
			periodType = -1;
		}
		short type = AhNewReportUtil.whatDataTableToUse(periodType, startTime, endTime);
		
		switch (type) {
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_HOUR:
				result = 3600000L;
				break;
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_DAY:
				result = 24*3600000L;
				break;
			case AhNewReportUtil.DATA_TABLE_PROPER_TO_USE_WEEK:
				result = 7*24*3600000L;
				break;
		}
		
		return result;
	}

	public static void extendOnePointAtLast(Map map, Long lastRecordTime, Long endTime, Long padding) {
		if (lastRecordTime >= endTime
				|| padding <= 0) {
			return;
		}
		Long nextRecordTime = lastRecordTime + padding;
		if (nextRecordTime > endTime) {
			nextRecordTime = endTime;
		}
		map.put(nextRecordTime, map.get(lastRecordTime));
	}
	
	public static double dataUnitConvert(Long oriData, DataUnits fromUnit, DataUnits toUnit, int precision) {
		double result = oriData.doubleValue();
		
		int unitDiff = toUnit.ordinal() - fromUnit.ordinal();
		if (unitDiff == 0) {
			return result;
		}
		
		int absUnitDiff = Math.abs(unitDiff);
		double baseNum = 1.0;
		for (int i = 0; i < absUnitDiff; i++) {
			baseNum *= 1024;
		}
		if (unitDiff > 0) {
			result /= baseNum;
		} else {
			result *= baseNum;
		}

		if (precision >= 0) {
			result = dataFormatWithPrecision(result, precision);
		}
		
		return result;
	}
	
	public static double dataFromBToKB(Long oriData) {
		return dataFromBToKB(oriData, null);
	}
	
	public static double dataFromBToMB(Long oriData) {
		return dataFromBToMB(oriData, null);
	}
	
	public static double dataFromBToGB(Long oriData) {
		return dataFromBToGB(oriData, null);
	}
	
	public static double dataFromBToKB(Long oriData, Long diffTime) {
		int precision = diffTime == null? 2 : -1;
		double result = dataUnitConvert(oriData, DataUnits.B, DataUnits.KB, precision);
		if (diffTime != null) {
			result = dataFormatWithPrecision(result/diffTime, 2);
		}
		return result;
	}
	
	public static double dataFromBToMB(Long oriData, Long diffTime) {
		int precision = diffTime == null? 2 : -1;
		double result = dataUnitConvert(oriData, DataUnits.B, DataUnits.MB, precision);
		if (diffTime != null) {
			result = dataFormatWithPrecision(result/diffTime, 2);
		}
		return result;
	}
	
	public static double dataFromBToGB(Long oriData, Long diffTime) {
		int precision = diffTime == null? 2 : -1;
		double result = dataUnitConvert(oriData, DataUnits.B, DataUnits.GB, precision);
		if (diffTime != null) {
			result = dataFormatWithPrecision(result/diffTime, 2);
		}
		return result;
	}
	
	public static double dataFormatWithPrecision(double data, int precision) {
		double result = data;
		if (precision > 0) {
			BigDecimal b = new BigDecimal(result);
			result = b.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		
		return result;
	}
	
	public static RadioModeType getRadioModeType(int radioMode) {
		switch(radioMode) {
			case 1:
				return RadioModeType.GHz24;
			case 2:
				return RadioModeType.GHz50;
			case 3:
				return RadioModeType.WIRED;
			default:
				return RadioModeType.NONE;
		}
	}
	
	public static String getReportMarkWithSubType(Long reportId, String subType) {
		return reportId + "_" + subType;
	}
	
	public static String getReportMarkWithSubType(List<String> reports, Long reportId, String subType) {
		String result = getReportMarkWithSubType(reportId, subType);
		reports.add(result);
		return result;
	}
	
	public static Long getIdFromReportMark(String markStr) {
		if (!StringUtils.isBlank(markStr)) {
			return Long.valueOf(markStr.split("_")[0]);
		}
		
		return null;
	}
	
	public static String getSubTypeFromReportMark(String markStr) {
		if (!StringUtils.isBlank(markStr)) {
			String[] subStr = markStr.split("_");
			if (subStr != null
					&& subStr.length > 1) {
				return subStr[1];
			}
		}
		
		return "";
	}
	
	public static String getLegendStringForFreeChart(AhSeries aSeries) {
		if (aSeries != null) {
			String result = aSeries.getName();
			if (aSeries.getSummarys()!= null
					&& !aSeries.getSummarys().isEmpty()) {
				for (String summary : aSeries.getSummarys()) {
					result += "\n" + summary;
				}
			}
			return result;
		}
		
		return "";
	}
	
	public static Set<String> lowerStringArray(Set<String> strSet) {
		if (strSet != null
				&& !strSet.isEmpty()) {
			Set<String> result = new HashSet<String>(strSet.size());
			for (Iterator<String> iter = strSet.iterator(); iter.hasNext();) {
				result.add(StringUtils.lowerCase(iter.next()));
			}
			
			return result;
		}
		
		return null;
	}
	
	public static int getHexColorValue(String colorValue) {
		if (!StringUtils.isBlank(colorValue)) {
			return Integer.parseInt(colorValue.replace("#", ""), 16);
		}
		
		return 0x0;
	}
	
	public static List<Long> getAllTimeTicksToBeCalculated(Long startTime, Long endTime, Long timePadding) {
		return getAllTimeTicksToBeCalculated(startTime, endTime, timePadding, null);
	}
	
	public static List<Long> getAllTimeTicksToBeCalculated(Long startTime, Long endTime, Long timePadding, List<Long> timeTicks) {
		List<Long> result = new ArrayList<Long>();
		if (timeTicks == null
				|| timeTicks.isEmpty()) {
			Long curTime = startTime;
			if (curTime + timePadding <= endTime) {
				while (curTime + timePadding <= endTime) {
					result.add(curTime);
					curTime += timePadding;
				}
				result.add(curTime);
			}
		} else {
			result.addAll(timeTicks);
			Long definedTimeEnd = timeTicks.get(0);
			if (timeTicks.size() > 0) {
				definedTimeEnd = timeTicks.get(timeTicks.size()-1);
			}
			if (definedTimeEnd + timePadding <= endTime) {
				while (definedTimeEnd + timePadding <= endTime) {
					definedTimeEnd = definedTimeEnd + timePadding;
					result.add(definedTimeEnd);
				}
			}
		}
		return result;
	}
	
	public static String getFormattedDataUnit(String dataUnit) {
		if (!StringUtils.isBlank(dataUnit)) {
			return " " + dataUnit;
		}
		
		return "";
	}
	
	public static DataUnits getProperDataUnitForValue(Long value, int maxRange) {
		Long retValue = value;
		Long intervalValue = 1024L;
		DataUnits dataUtil = DataUnits.B;
		
		if (retValue/intervalValue > maxRange) {
			retValue = retValue/intervalValue;
			dataUtil = DataUnits.KB;
		}
		if (retValue/intervalValue > maxRange) {
			retValue = retValue/intervalValue;
			dataUtil = DataUnits.MB;
		}
		if (retValue/intervalValue > maxRange) {
			retValue = retValue/intervalValue;
			dataUtil = DataUnits.GB;
		}
		if (retValue/intervalValue > maxRange) {
			retValue = retValue/intervalValue;
			dataUtil = DataUnits.TB;
		}
		if (retValue/intervalValue > maxRange) {
			retValue = retValue/intervalValue;
			dataUtil = DataUnits.PB;
		}
		
		return dataUtil;
	}
	
	public static double convertBitsToCertainDataUnitValue(Long oriData, DataUnits toUnit) {
		return dataUnitConvert(oriData, DataUnits.B, toUnit, 2);
	}
	
	public static String getShownNameForInterfStatsRadioType(String radioType) {
		if (StringUtils.isNotBlank(radioType)) {
			if (String.valueOf(AhInterfaceStats.RADIOTYPE_24G).equals(radioType)) {
				return "2.4 GHz";
			} else if (String.valueOf(AhInterfaceStats.RADIOTYPE_5G).equals(radioType)) {
				return "5 GHz";
			}
		}
		return "";
	}
	
	public static double getDataBitFromByte(double byteVal) {
		return byteVal*8;
	}
}
