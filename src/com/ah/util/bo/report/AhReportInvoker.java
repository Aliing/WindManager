package com.ah.util.bo.report;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.ah.util.bo.report.freechart.AhFreechartWrapper;

public class AhReportInvoker implements AhReportInvokerInterface {
	private AhReportInvokerInterface invoker;
	
	public AhReportInvoker() {
	}
	
	@Override
	public void init(AhReportRequest ar) throws Exception {
		this.init(ar, true);
	}
	
	public void init(AhReportRequest ar, boolean blnRunInvoke) throws Exception {
		if (ar.getIds() != null
				&& !ar.getIds().isEmpty()) {
			this.invoker = new AhReportGroupInvoker();
		} else {
			this.invoker = new AhReportSingleInvoker();
		}
		this.invoker.init(ar, blnRunInvoke);
	}

	@Override
	public void invoke() throws Exception {
		this.invoker.invoke();
	}

	@Override
	public JSONArray getJSONResult() throws JSONException {
		return this.invoker.getJSONResult();
	}

	@Override
	public List<AhFreechartWrapper> getExportedJFreeCharts() throws Exception {
		return this.invoker.getExportedJFreeCharts();
	}

}
