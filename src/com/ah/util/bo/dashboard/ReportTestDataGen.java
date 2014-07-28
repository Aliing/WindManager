package com.ah.util.bo.dashboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ReportTestDataGen {
	private String testCode = "1";
	
	public ReportTestDataGen(String testCode) {
		this.testCode = testCode;
	}
	
	private Integer prepareSeriesValue(int min, int max) {
		if (Math.random()*10%7 == 0) {
			return 0;
		}
		return (int)(Math.random()*(max-min) + min);
	}
	
	public String getFakeDataForTestReturn(RequestData data) {
		if (data == null) {
			return null;
		}
		
		if (prepareSeriesValue(0, 10) == 4) {
			return null;
		}
		
		String result = "";
		JSONArray jArray = new JSONArray();
		
		try {
			if ("tb".equals(this.testCode)) {
				prepareOverTimeTestData(data, jArray);
			} else if (isSpecialChart(data)) {
				doPrepareTestDataForSpecialAxises(data, jArray);
			} else {
				prepareLinearTestData(data, jArray);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		result = jArray.toString();
		return result;
	}
	
	// just for test, start
	int minValue = 0;
	int maxValue = 99;
	private JSONObject prepareBkToDeviceTestItem(RequestData data, String key) throws JSONException {
		JSONObject jObjBk = new JSONObject();
		JSONObject jObjBk1 = new JSONObject();
		jObjBk.put("BreakDown", jObjBk1);
		int totalValue = 0;
		for (int i = 0; i < 15; i++) {
			JSONObject jObjBk2 = new JSONObject();
			jObjBk1.put("100" + i, jObjBk2);
			int tmpValue = prepareSeriesValue(minValue, maxValue);
			jObjBk2.put(key, tmpValue);
			jObjBk2.put("name", "drill-Bk0" + i);
			jObjBk2.put("@bkValue", getBreakDownBkValue(data));
			totalValue += tmpValue;
		}
		jObjBk.put("original", totalValue);
		
		return jObjBk;
	}
	private JSONObject prepareBreakDownTestItem(RequestData data, String key) throws JSONException {
		int bkDownItemCount = prepareSeriesValue(2, 6);
		JSONObject jObjBk = new JSONObject();
		JSONObject jObjBk1 = new JSONObject();
		jObjBk.put("BreakDown", jObjBk1);
		int totalValue = 0;
		for (int i = 0; i < bkDownItemCount; i++) {
			JSONObject jObjBk2 = new JSONObject();
			jObjBk1.put("00" + i, jObjBk2);
			int tmpValue = prepareSeriesValue(minValue, maxValue);
			jObjBk2.put(key, tmpValue);
			jObjBk2.put("name", getAxisBreakDownName(i));
			jObjBk2.put("@bkValue", getBreakDownBkValue(data));
			totalValue += tmpValue;
		}
		
		//totalValue += this.prepareSingleNullBreakDownTestItem(data, key, jObjBk1);
		jObjBk.put("original", totalValue);
		
		return jObjBk;
	}
	
	private int prepareSingleNullBreakDownTestItem(RequestData data, String key, JSONObject jObjBk1) throws JSONException {
		JSONObject jObjBk2 = new JSONObject();
		jObjBk1.put("", jObjBk2);
		int tmpValue = prepareSeriesValue(minValue, maxValue);
		jObjBk2.put(key, tmpValue);
		jObjBk2.put("name", "");
		jObjBk2.put("@bkValue", getBreakDownBkValue(data));
		
		return tmpValue;
	}
	
	private static final String SPECIAL_AXIS_BANDWIDTH_PORTS = "bandwidth ports";
	private String[] specialBreakDownBkValues = new String[]{SPECIAL_AXIS_BANDWIDTH_PORTS};
	private boolean isSpecialBreakDownBkValue(RequestData data) {
		for (int i = 0; i < specialBreakDownBkValues.length; i++) {
			if (data.getAxis().equals(specialBreakDownBkValues[i])) {
				return true;
			}
		}
		
		return false;
	}
	private Object getBreakDownBkValue(RequestData data) {
		if (isSpecialBreakDownBkValue(data)) {
			if (SPECIAL_AXIS_BANDWIDTH_PORTS.equals(data.getAxis())) {
				return "MAC=MAC-" + prepareSeriesValue(minValue, maxValue) + ";PORT=" + prepareSeriesValue(minValue, maxValue); 
			}
		}
		return prepareSeriesValue(minValue, maxValue);
	}
	
	private static final String[] BK_AXIS_NAMES = new String[]{"Bk_item0", "Bk_item-this is really a long name0", "Bk_MAC-00AC"};
	private String getAxisBreakDownName(int count) {
		return BK_AXIS_NAMES[count%BK_AXIS_NAMES.length] + count;
	}
	private void prepareCommonTestItem(RequestData data, JSONObject jObj, String key) throws JSONException {
		if ("MAC".equals(key)) {
			jObj.put(key, "MAC--123");
		} else {
			jObj.put(key, prepareSeriesValue(minValue, maxValue));
		}
		jObj.put("@bkValue", getBreakDownBkValue(data));
	}
	private void prepareCommonNullTestItem(RequestData data, JSONObject jObj, String key) throws JSONException {
		jObj.put(key, "");
		jObj.put("@bkValue", "");
	}
	private void prepareSingleTestItem(RequestData data, JSONObject jObj) throws JSONException {
		if (data.getMetrics() != null
				&& !data.getMetrics().isEmpty()) {
			for (String key : data.getMetrics().keySet()) {
				Byte bkLevel = data.getMetrics().get(key);
				if (bkLevel == null) {
					jObj.put(key, prepareBkToDeviceTestItem(data, key));
				} else if (bkLevel != 0) {
					// break down
					if (isSpecialNoDataNeedTotalChart(data)) {
						jObj.put(key, doPrepareTestDataForSpecialNoDataNeedTotalAxises(data, key));
					} else {
						jObj.put(key, prepareBreakDownTestItem(data, key));
					}
				} else {
					// no break down
					prepareCommonTestItem(data, jObj, key);
				}
			}
		}
	}
	private void prepareSingleNullTestItem(RequestData data, JSONObject jObj) throws JSONException {
		if (data.getMetrics() != null
				&& !data.getMetrics().isEmpty()) {
			for (String key : data.getMetrics().keySet()) {
				Byte bkLevel = data.getMetrics().get(key);
				if (bkLevel == null) {
					jObj.put(key, "");
				} else if (bkLevel != 0) {
					// break down
					jObj.put(key, "");
				} else {
					// no break down
					prepareCommonNullTestItem(data, jObj, key);
				}
			}
		}
	}
	private void prepareOverTimeTestData(RequestData data, JSONArray jArray) throws JSONException {
		Long timeTmps = data.getStartTime();
		Long timeTmpe = data.getEndTime();
		if (timeTmps > timeTmpe) {
			timeTmps = data.getEndTime();
			timeTmpe = data.getStartTime();
		}
		
		//sample in request data is in second
		int sample = Math.abs(data.getSample()) * 1000;
		while (timeTmps < timeTmpe) {
			JSONObject jObj = new JSONObject();
			jArray.put(jObj);
			jObj.put(data.getAxis(), timeTmps);
			if (!this.dealSpecialOvertimeWidgetDataForKey(data, jObj, timeTmps, timeTmpe, sample)) {
				prepareSingleTestItem(data, jObj);
			}

			timeTmps += sample;
		}
	}
	private void prepareLinearTestData(RequestData data, JSONArray jArray) throws JSONException {
		if (!dealSpecialWidgetDataForKey(data, jArray)) {
			int totalCount = 10;
			if (isSpecialNoDataNeedTotalChart(data)) {
				totalCount = 1;
			}
			
			for (int itemCount = 1; itemCount <= totalCount; itemCount++) {
				JSONObject jObj = new JSONObject();
				jArray.put(jObj);
				jObj.put(data.getAxis(), getTestAxisName(itemCount));
				//jObj.put(data.getAxis(), "Test_Axis" + itemCount);
				//jObj.put(data.getAxis(), itemCount);
				prepareSingleTestItem(data, jObj);
			}
			
			/*JSONObject jObj = new JSONObject();
			jArray.put(jObj);
			jObj.put(data.getAxis(), "");
			prepareSingleNullTestItem(data, jObj);*/
		}
	}
	private String[] testAxisNames = new String[]{"Test_Axis", "MAC", "This is really a long axis name", "123", "HIVEAP-66E02"};
	private String getTestAxisName(int count) {
		return testAxisNames[count%testAxisNames.length] + count;
	}
	
	private static final String SPECIAL_AXIS_DEVICE_ALARM = "network device(s) (port) alarm level";
	private static final String SPECIAL_AXIS_DEVICE_CONFIGURATION_COMPLIANCE = "configuration compliance";
	private String[] specialAxisNames = new String[]{SPECIAL_AXIS_DEVICE_ALARM, SPECIAL_AXIS_DEVICE_CONFIGURATION_COMPLIANCE};
	private boolean isSpecialChart(RequestData data) {
		for (int i = 0; i < specialAxisNames.length; i++) {
			if (data.getAxis().equals(specialAxisNames[i])) {
				return true;
			}
		}
		
		return false;
	}
	private void doPrepareTestDataForSpecialAxises(RequestData data, JSONArray jArray) throws JSONException {
		if (data.getAxis().equals(SPECIAL_AXIS_DEVICE_ALARM)) {
			this.prepareDeviceAlarmTestData(data, jArray);
		} else if (data.getAxis().equals(SPECIAL_AXIS_DEVICE_CONFIGURATION_COMPLIANCE)) {
			this.prepareDeviceConfigComplianceTestData(data, jArray);
		}
	}
	private void prepareSpecialTestData(RequestData data, JSONArray jArray, String[] testDatas) throws JSONException {
		for (int itemCount = 1; itemCount <= testDatas.length; itemCount++) {
			JSONObject jObj = new JSONObject();
			jArray.put(jObj);
			jObj.put(data.getAxis(), testDatas[itemCount-1]);
			prepareSingleTestItem(data, jObj);
		}
	}
	private void prepareDeviceAlarmTestData(RequestData data, JSONArray jArray) throws JSONException {
		this.prepareSpecialTestData(data, jArray, new String[]{"1", "3", "4", "5"});
	}
	private void prepareDeviceConfigComplianceTestData(RequestData data, JSONArray jArray) throws JSONException {
		this.prepareSpecialTestData(data, jArray, new String[]{"Acceptable", "Weak", "Strong"});
	}
	// just for test, --end part one

	
	private boolean isSpecialNoDataNeedTotalChart(RequestData data) {
		return DaHelper.isBkNoDataNeedTotalWidget(data.getComponentKey());
	}
	private JSONObject doPrepareTestDataForSpecialNoDataNeedTotalAxises(RequestData data, String key) throws JSONException {
		JSONObject jObjBk = new JSONObject();
		JSONObject jObjBk1 = new JSONObject();
		jObjBk.put("BreakDown", jObjBk1);
		int totalValue = prepareSeriesValue(minValue, maxValue);
		if (totalValue % 7 > 3) {
			totalValue = 0;
			for (int i = 0; i < 10; i++) {
				JSONObject jObjBk2 = new JSONObject();
				jObjBk1.put("100" + i, jObjBk2);
				int tmpValue = prepareSeriesValue(minValue, maxValue);
				jObjBk2.put(key, tmpValue);
				jObjBk2.put("name", "drill-Bk0" + i);
				jObjBk2.put("@bkValue", getBreakDownBkValue(data));
				totalValue += tmpValue;
			}
		}
		jObjBk.put("original", totalValue);
		
		return jObjBk;
	}
	
	
	private boolean dealSpecialWidgetDataForKey(RequestData data, JSONArray jArray) throws JSONException {
		if (isSpecialTopApplicationSummary(data)) {
			prepareSpecialTopApplicationSummary(data, jArray);
			return true;
		}
		
		if (data.getComponentKey() == 90) {
			prepareSpecialForPortDetail(data, jArray);
			return true;
		}
		
		if (data.getComponentKey() == 13) {
			prepareSpecialClientByUpAttr(data, jArray);
			return true;
		}
		
		return false;
	}
	
	private boolean isSpecialTopApplicationSummary(RequestData data) {
		return data.getComponentKey() == 67;
	}
	private void prepareSpecialTopApplicationSummary(RequestData data, JSONArray jArray) throws JSONException {
		double[] values = new double[]{54.12, 10.1, 9.993, 6.5, 3.20, 1.6};
		for (String key : data.getMetrics().keySet()) {
			for (int i = 0; i < values.length; i++) {
				JSONObject jObj = new JSONObject();
				jArray.put(jObj);
				jObj.put(data.getAxis(), getTestAxisName(i));
				jObj.put(key, values[i]);
				jObj.put("@bkValue", prepareSeriesValue(minValue, maxValue));
			}
		}
	}
	private void prepareSpecialForPortDetail(RequestData data, JSONArray jArray) throws JSONException {
		JSONObject jObj = new JSONObject();
		jArray.put(jObj);
		jObj.put(data.getAxis(), "");
		for (String key : data.getMetrics().keySet()) {
			jObj.put(key, prepareSeriesValue(minValue, maxValue)%2);
			jObj.put("@bkValue", prepareSeriesValue(minValue, maxValue));
		}
	}
	private void prepareSpecialClientByUpAttr(RequestData data, JSONArray jArray) throws JSONException {
		for (String key : data.getMetrics().keySet()) {
			for (int i = 0; i < 15; i++) {
				String axisName = "0";
				if (i > 0) {
					axisName = String.valueOf(prepareSeriesValue(minValue, maxValue));
				}
				JSONObject jObj = new JSONObject();
				jArray.put(jObj);
				jObj.put(data.getAxis(), axisName);
				jObj.put(key, prepareSeriesValue(minValue, maxValue));
				jObj.put("@bkValue", prepareSeriesValue(minValue, maxValue));
			}
		}
	}
	
	private boolean dealSpecialOvertimeWidgetDataForKey(RequestData data, JSONObject jObj, Long curTime, Long endTime, int sample) throws JSONException {
		if (data.getComponentKey() == 20) {
			this.prepareSpecialForOverTimeDeviceInUpState(data, jObj, curTime, endTime, sample);
			return true;
		}
		
		return false;
	}
	private void prepareSpecialForOverTimeDeviceInUpState(RequestData data, JSONObject jObj, 
			Long curTime, Long endTime, int sample) throws JSONException {
		if (data.getMetrics() != null
				&& !data.getMetrics().isEmpty()) {
			for (String key : data.getMetrics().keySet()) {
				if (endTime - curTime > 3*sample) {
					jObj.put(key, 100);
				} else {
					jObj.put(key, prepareSeriesValue(minValue, maxValue));
				}
			}
		}
	}
	
}
