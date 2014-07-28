package com.ah.util.bo.report;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.ah.bo.report.AhAbstractReport;
import com.ah.bo.report.AhReportElement;
import com.ah.bo.report.AhReportResult;
import com.ah.util.Tracer;
import com.ah.util.bo.report.freechart.AhChartExportInterf;
import com.ah.util.bo.report.freechart.AhFreechartWrapper;
import com.ah.util.bo.report.freechart.AhNullFreechartWrapper;

public class AhReportProxy extends AhAbstractReport {
	private static final Tracer log = new Tracer(AhReportProxy.class
			.getSimpleName());
	
	private AhAbstractReport reportExecutor;
	private AhReportResult result;
	private List<AhFreechartWrapper> exportedCharts;
	
	public String getReportIdentifier() {
		if (reportExecutor != null
				&& reportExecutor.getRequest() != null) {
			return reportExecutor.getRequest().getId() + "_" + reportExecutor.getRequest().getSubType();
		}
		return "N/A";
	}
	
	public void init() {
		this.reportExecutor.init();
	}
	
	protected void doCalculate() {
		// do nothing here
	}

	private void beforeRun() {
		
	}
	private void afterRun() {
		
	}
	public void run() throws Exception {
		this.beforeRun();
		this.reportExecutor.runBase();
		this.afterRun();
	}
	
	public void setRequest(AhReportRequest request) {
		this.reportExecutor.setRequest(request);
	}
	
	public AhReportResult getResult() {
		return this.reportExecutor.getResult();
	}
	
	public JSONArray getJSONResult() throws JSONException {
		if (this.result == null) {
			this.result = this.reportExecutor.getResult();
		}
		if (this.result != null) {
			return this.result.getJSONData();
		}
		return null;
	}
	
	public List<JSONArray> getGroupJSONData()  throws JSONException {
		if (this.result == null) {
			this.result = this.reportExecutor.getResult();
		}
		if (this.result != null) {
			return this.result.getGroupJSONData();
		}
		return null;
	}

	public List<AhFreechartWrapper> getExportedJFreeCharts() throws Exception {
		if (this.exportedCharts != null) {
			return this.exportedCharts;
		}
		
		this.reportExecutor.initReportChartExportEl();
		
		if (this.reportExecutor.getExportChartEl() == null) {
			return null;
		}
		AhChartExportInterf chartExportTmp;
		List<AhFreechartWrapper> result = new ArrayList<AhFreechartWrapper>();
		AhReportResult ahResult = this.reportExecutor.getResult();
		if (ahResult != null) {
			if (ahResult.getReportEl() != null) {
				if (ahResult.getReportEl().getExportChartEl() != null) {
					chartExportTmp = ahResult.getReportEl().getExportChartEl();
				} else {
					chartExportTmp = this.reportExecutor.getExportChartEl();
				}
				try {
					result.add(chartExportTmp.invoke(ahResult.getReportEl()));
				} catch(Exception e) {
					result.add(new AhNullFreechartWrapper(ahResult.getReportEl()));
					log.error("Failed to generate freechart for report.", e);
				}
			}
			if (ahResult.getGroupReportEls() != null
					&& !ahResult.getGroupReportEls().isEmpty()) {
				for (AhReportElement aReport : ahResult.getGroupReportEls().values()) {
					if (aReport != null) {
						if (aReport.getExportChartEl() != null) {
							chartExportTmp = aReport.getExportChartEl();
						} else {
							chartExportTmp = this.reportExecutor.getExportChartEl();
						}
						try {
							result.add(chartExportTmp.invoke(aReport));
						} catch(Exception e) {
							result.add(new AhNullFreechartWrapper(aReport));
							log.error("Failed to generate freechart for report.", e);
						}
					}
				}
			}
		}
		
		this.exportedCharts = result;
		
		return this.exportedCharts;
	}
	
	public AhAbstractReport getReportExecutor() {
		return reportExecutor;
	}

	public void setReportExecutor(AhAbstractReport reportExecutor) {
		this.reportExecutor = reportExecutor;
	}
	
}
