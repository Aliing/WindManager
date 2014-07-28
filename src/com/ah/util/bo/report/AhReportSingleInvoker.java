package com.ah.util.bo.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;

import com.ah.bo.report.AhReportResult;
import com.ah.util.bo.report.freechart.AhFreechartWrapper;
import com.ah.util.bo.report.interceptor.AhReportInterceptor;

public class AhReportSingleInvoker implements AhReportInvokerInterface {
	private AhReportRequest request;
	private AhReportProxy reportProxy;
	private List<AhReportInterceptor> interceptors 
					= new ArrayList<AhReportInterceptor>();
	private Iterator<AhReportInterceptor> iterInterceptors;
	private String interceptorResult = null;
	private boolean blnReportCalculateRight = true;
	private boolean blnRunInvoke;
	
	public AhReportSingleInvoker() {
	}
	
	@Override
	public void init(AhReportRequest ar) throws Exception {
		this.init(ar, true);
	}
	
	@Override
	public void init(AhReportRequest ar, boolean blnRunInvoke) throws Exception {
		this.request = ar;
		prepare();
		iterInterceptors = interceptors.iterator();
		this.blnRunInvoke = blnRunInvoke;
	}
	
	@Override
	public void invoke() throws Exception {
		if (!this.blnRunInvoke) {
			return;
		}
		if (this.reportProxy == null) {
			return;
		}
		
		try {
			while (iterInterceptors.hasNext()) {
				if (!StringUtils.isBlank(interceptorResult)) {
					break;
				}
				interceptorResult = iterInterceptors.next().intercept(AhReportSingleInvoker.this);
			}
			
			if (StringUtils.isBlank(interceptorResult)) {
				this.reportProxy.run();
			}
		} catch (Exception e) {
			blnReportCalculateRight = false;
			e.printStackTrace();
		}
	}
	
	@Override
	public JSONArray getJSONResult() throws JSONException {
		if (this.reportProxy != null) {
			if (!blnReportCalculateRight
					|| !StringUtils.isBlank(interceptorResult)) {
				return this.reportProxy.getResult().getFailedJSONData(interceptorResult);
			} else {
				return this.reportProxy.getJSONResult();
			}
		}
		
		return null;
	}
	
	public AhReportResult getResult() {
		if (this.reportProxy != null) {
			return this.reportProxy.getResult();
		}
		
		return null;
	}
	
	private void prepare() throws Exception {
		if (this.request != null
				&& this.request.getId() != null) {
			this.reportProxy = AhReportFactory.create(this.request);
			if (this.reportProxy != null) {
				this.reportProxy.setRequest(this.request);
				this.reportProxy.init();
			}
			//this.interceptors.add(new AhPerformanceInterceptor());
		}
	}

	public AhReportRequest getRequest() {
		return request;
	}

	public void setRequest(AhReportRequest request) {
		this.request = request;
	}

	public AhReportProxy getReportProxy() {
		return reportProxy;
	}

	public void setReportProxy(AhReportProxy reportProxy) {
		this.reportProxy = reportProxy;
	}

	public List<AhReportInterceptor> getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(List<AhReportInterceptor> interceptors) {
		this.interceptors = interceptors;
	}

	@Override
	public List<AhFreechartWrapper> getExportedJFreeCharts() throws Exception {
		if (this.reportProxy != null) {
			return this.reportProxy.getExportedJFreeCharts();
		}
		
		return null;
	}
}
