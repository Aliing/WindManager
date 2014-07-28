package com.ah.bo.report;

import java.util.List;

public class AhDatetimeSeries extends AhSeries {

	public void addData(Long timePoint, Object value) throws Exception {
		super.getSafeData().add(new AhSeriesData(timePoint, value));
	}
	
	public void addData(Long timePoint, Long value) throws Exception {
		super.getSafeData().add(new AhSeriesData(timePoint, value));
	}
	
	public boolean addData(List<Long> timePoints, List<Object> dataList) {
		if (timePoints == null
				|| timePoints.isEmpty()) {
			return false;
		}
		int dataLen = 0;
		if (dataList != null) {
			dataLen = dataList.size();
		}
		
		int curPoint = 1;
		for (Long timePoint : timePoints) {
			if (curPoint > dataLen) {
				super.getSafeData().add(new AhSeriesData(timePoint, null));
			} else {
				super.getSafeData().add(new AhSeriesData(timePoint, dataList.get(curPoint-1)));
			}
			curPoint++;
		}
		
		return true;
	}
	
}
