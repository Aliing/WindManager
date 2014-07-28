package com.ah.util.bo.report;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.ah.util.bo.report.freechart.AhFreechartWrapper;

public interface AhReportInvokerInterface {
	
	public void init(AhReportRequest ar) throws Exception;
	
	public void init(AhReportRequest ar, boolean blnRunInvoke) throws Exception;
	
	public void invoke() throws Exception;
	
	public JSONArray getJSONResult() throws JSONException;
	
	public List<AhFreechartWrapper> getExportedJFreeCharts() throws Exception;
}
