package com.ah.util.bo.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.ah.bo.report.AhReportConfigElement;
import com.ah.bo.report.AhReportContainer;
import com.ah.util.bo.report.freechart.AhFreechartWrapper;

public class AhReportGroupInvoker implements AhReportInvokerInterface {
	private AhReportRequest request;
	private List<AhReportProxy> reportProxys;
	private List<String> failedReportProxys;
	private boolean blnRunInvoke;
	
	public AhReportGroupInvoker() {
	}
	
	@Override
	public void init(AhReportRequest ar) throws Exception {
		this.init(ar, true);
	}
	
	@Override
	public void init(AhReportRequest ar, boolean blnRunInvoke) throws Exception {
		this.request = ar;
		prepare();
		this.blnRunInvoke = blnRunInvoke;
	}
	
	@Override
	public void invoke() throws Exception {
		if (!this.blnRunInvoke) {
			return;
		}
		if (this.reportProxys == null
				|| this.reportProxys.isEmpty()) {
			return;
		}
		
		for (int i = 0; i < this.reportProxys.size(); i++) {
			try {
				this.reportProxys.get(i).run();
			} catch (Exception e) {
				getSafeFailedReportProxys().add(this.reportProxys.get(i).getReportIdentifier());
				e.printStackTrace();
			}
		}
	}
	
	private void prepare() throws Exception {
		if (this.request != null
				&& this.request.getIds() != null
				&& !this.request.getIds().isEmpty()) {
			Map<Long, Integer> reportIdMap = new HashMap<Long, Integer>();
			for (Long idTmp : this.request.getIds()) {
				Integer deltaCount = 0;
				if (reportIdMap.containsKey(idTmp)) {
					deltaCount = reportIdMap.get(idTmp);
				}
				reportIdMap.put(idTmp, deltaCount + 1);
			}
			
			for (Map.Entry<Long, Integer> entry : reportIdMap.entrySet()) {
				int reqCount = entry.getValue();
				AhReportConfigElement reportConfigTmp = AhReportContainer.getReportConfig(entry.getKey());
				if (reqCount > 1
						&& reportConfigTmp != null
						&& !reportConfigTmp.isBlnGroupCalEnabled()) {
					List<String> subTypes = this.request.getReqSubTypesOfReport(entry.getKey());
					if (subTypes == null) {
						subTypes = new ArrayList<String>(reqCount);
						for (int i = 0; i < reqCount; i++) {
							subTypes.add(null);
						}
					} else {
						int tmpSize = subTypes.size();
						if (tmpSize < reqCount) {
							for (int i = 0; i < reqCount - tmpSize; i++) {
								subTypes.add(null);
							}
						}
					}
					// so it does not support group calculate
					for (int i = 0; i < reqCount; i++) {
						AhReportRequest rqTmp = this.request.clone();
						rqTmp.setId(entry.getKey());
						rqTmp.setBlnGroupCal(false);
						rqTmp.setSubType(subTypes.get(i));
						
						AhReportProxy rProxy = AhReportFactory.create(rqTmp);
						if (rProxy != null) {
							rProxy.setRequest(rqTmp);
							rProxy.init();
						}
						this.addRequestProxy(rProxy);
					}
				} else {
					AhReportRequest rqTmp = this.request.clone();
					rqTmp.setId(entry.getKey());
					rqTmp.setBlnGroupCal(entry.getValue().compareTo(1) > 0 ? true : false);

					List<String> subTypes = this.request.getReqSubTypesOfReport(entry.getKey());
					if (subTypes != null
							&& !subTypes.isEmpty()) {
						rqTmp.setSubType(subTypes.get(0));
					}
					
					AhReportProxy rProxy = AhReportFactory.create(rqTmp);
					if (rProxy != null) {
						rProxy.setRequest(rqTmp);
						rProxy.init();
					}
					this.addRequestProxy(rProxy);
				}
			}
			
		}
	}
	
	private void addRequestProxy(AhReportProxy rProxy) {
		if (this.reportProxys == null) {
			this.reportProxys = new ArrayList<AhReportProxy>();
		}
		this.reportProxys.add(rProxy);
	}
	
	@Override
	public JSONArray getJSONResult() throws JSONException {
		if (this.reportProxys != null
				&& !this.reportProxys.isEmpty()) {
			JSONArray result = new JSONArray();
			for (AhReportProxy proxyTmp : this.reportProxys) {
				if (this.getSafeFailedReportProxys().contains(proxyTmp.getReportIdentifier())) {
					result.put(proxyTmp.getResult().getFailedJSONData());
				} else {
					result.put(proxyTmp.getJSONResult());
					List<JSONArray> grpResult = proxyTmp.getGroupJSONData();
					if (grpResult != null
							&& !grpResult.isEmpty()) {
						for (JSONArray jsonArray : grpResult) {
							result.put(jsonArray);
						}
					}
				}
			}
			return result;
		}
		
		return null;
	}

	@Override
	public List<AhFreechartWrapper> getExportedJFreeCharts() throws Exception {
		if (this.reportProxys != null
				&& !this.reportProxys.isEmpty()) {
			List<AhFreechartWrapper> result = new ArrayList<AhFreechartWrapper>();
			for (AhReportProxy proxyTmp : this.reportProxys) {
				try {
					List<AhFreechartWrapper> chartResult = proxyTmp.getExportedJFreeCharts();
					if (chartResult != null
							&& !chartResult.isEmpty()) {
						result.addAll(chartResult);
					}
				} catch (Exception e) {
					// log here
					e.printStackTrace();
				}
			}
			return result;
		}
		
		return null;
	}
	
	private List<String> getSafeFailedReportProxys() {
		if (failedReportProxys == null) {
			failedReportProxys = new ArrayList<String>();
		}
		
		return failedReportProxys;
	}
	
}
