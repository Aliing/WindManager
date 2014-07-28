package com.ah.bo.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.ah.util.bo.report.AhReportRequest;
import com.ah.util.bo.report.freechart.AhChartExportInterf;

public abstract class AhAbstractReport {
	public static final String GROUP_MARK_DEFAULT = "default";
	
	private Long id;
	private AhReportElement reportEl;
	private AhReportRequest request;
	private AhReportResult result;
	
	private AhChartExportInterf exportChartEl;
	
	private boolean blnSampleData = false;
	
	public void initReportElement(AhReportRequest ar) {
		this.setReportId(ar.getId());
	}
	
	public void initReportChartExportEl() {
		// please override it with your own implementation
	}
	
	protected void setReportTitle(String title) {
		if (this.reportEl != null) {
			this.reportEl.setTitle(title);
		}
	}
	protected void setReportSubTitle(String subTitle) {
		if (this.reportEl != null) {
			this.reportEl.setSubTitle(subTitle);
		}
	}
	protected void setReportSummary(String summary) {
		if (this.reportEl != null) {
			this.reportEl.setSummary(summary);
		}
	}
	protected void setReportValueTitle(String valueTitle) {
		if (this.reportEl != null) {
			this.reportEl.setValueTitle(valueTitle);
		}
	}
	protected void setReportCategoryTitle(String title) {
		if (this.reportEl != null) {
			this.reportEl.setCategoryTitle(title);
		}
	}
	
	protected JSONObject encapCustomUserMessage() {
		return null;
	}
	
	/**
	 * used to init some resource, used by report proxy
	 */
	public abstract void init();
	
	public void setReportStartTime(Long startTime){
		if (this.reportEl != null) {
			this.reportEl.setStartTime(startTime);
		}
	};
	
	public void setReportEndTime(Long endTime){
		if (this.reportEl != null) {
			this.reportEl.setEndTime(endTime);
		}
	};

	public final void runBase() throws Exception {
		if (this.reportEl == null) return;
		prepareReportInitData();
		if (blnSampleData) {
			prepareSampleData();
		} else {
			doCalculate();
		}
	}
	protected abstract void doCalculate() throws Exception;
	
	protected void prepareSampleData() {
	}
	
	protected void addSeries(AhSeries series) {
		if (this.reportEl != null) {
			this.reportEl.addSeries(series);
		}
	}
	protected void setCategories(List<Object> categories) {
		if (this.reportEl != null) {
				this.reportEl.setCategories(categories);
		}
	}
	protected void addCategories(String category) {
		if (this.reportEl != null) {
			this.reportEl.addCategory(category);
		}
	}
	
	public AhReportResult getResult() {
		if (this.result == null) {
			if (blnSampleData) {
				this.reportEl.setTitle("(Sample) " + this.reportEl.getTitle());
				for (AhReportElement elTmp : this.getGroupReportEls().values()) {
					elTmp.setTitle("(Sample) " + elTmp.getTitle());
				}
			}
			prepareNullSeriesIfNeed();
			this.result = new AhReportResult();
			this.reportEl.setTz(this.request.getTimeZone());
			this.result.setReportEl(this.reportEl);
			Map<String, AhReportElement> groupElMap = this.getGroupReportEls();
			if (this.reportEl.isReportNull()) {
				AhReportElement rpEl = popNotNullAhReportFromGroup(groupElMap);
				if (rpEl != null) {
					this.result.setReportEl(rpEl);
				}
			}
			this.result.setRequest(this.request);
			this.result.setUserMessage(this.encapCustomUserMessage());
			this.result.setGroupReportEls(groupElMap);
		}
		return this.result;
	}
	
	private AhReportElement popNotNullAhReportFromGroup(Map<String, AhReportElement> groupElMap) {
		if (groupElMap == null
				|| groupElMap.isEmpty()) {
			return null;
		}
		AhReportElement result;
		for (Iterator<String> iter = groupElMap.keySet().iterator(); iter.hasNext();) {
			String key = iter.next();
			if (!groupElMap.get(key).isReportNull()) {
				result = groupElMap.get(key);
				iter.remove();
				return result;
			}
		}
		return null;
	}
	
	private final void prepareReportInitData() {
		/*if (StringUtils.isBlank(this.reportEl.getTitle())) {
			this.reportEl.setTitle(MgrUtil.getEnumString("enum.ah.report.name."+this.reportEl.getId()));
		}*/
	}
	
	private void prepareNullSeriesIfNeed() {
		if (this.reportEl.getSeries() == null
				|| this.reportEl.getSeries().isEmpty()) {
			AhSeries series = null;
			if (this.reportEl.getCategories() != null
					&& !this.reportEl.getCategories().isEmpty()) {
				Object obj0 = this.reportEl.getCategories().get(0);
				if (obj0 instanceof Long) {
					List<Long> cateTmp = new ArrayList<Long>(this.reportEl.getCategories().size());
					for (Object obj : this.reportEl.getCategories()) {
						cateTmp.add((Long)obj);
					}
					series = new AhDatetimeSeries();
					((AhDatetimeSeries)series).addData(cateTmp, new ArrayList<Object>());
				}
			}
			if (series == null) {
				series = new AhLinearSeries();
			}
			this.addSeries(series);
			series.setId(-1L);
			series.setName(AhReportElement.SERIES_NAME_NO_DATA);
		}
		
		Map<String, AhReportElement> mapTmp = getGroupReportEls();
		if (mapTmp != null
				&& !mapTmp.isEmpty()) {
			for (AhReportElement elTmp : mapTmp.values()) {
				prepareNullSeriesIfNeedForCertainReport(elTmp);
			}
		}
	}
	
	private void prepareNullSeriesIfNeedForCertainReport(AhReportElement rpElTmp) {
		if (rpElTmp == null) return;
		if (rpElTmp.getSeries() == null
				|| rpElTmp.getSeries().isEmpty()) {
			AhSeries series = null;
			if (rpElTmp.getCategories() != null
					&& !rpElTmp.getCategories().isEmpty()) {
				Object obj0 = rpElTmp.getCategories().get(0);
				if (obj0 instanceof Long) {
					List<Long> cateTmp = new ArrayList<Long>(rpElTmp.getCategories().size());
					for (Object obj : rpElTmp.getCategories()) {
						cateTmp.add((Long)obj);
					}
					series = new AhDatetimeSeries();
					((AhDatetimeSeries)series).addData(cateTmp, new ArrayList<Object>());
				}
			}
			if (series == null) {
				series = new AhLinearSeries();
			}
			this.addSeriesForCertainReportEl(series, rpElTmp);
			series.setId(-1L);
			series.setName(AhReportElement.SERIES_NAME_NO_DATA);
		}
	}
	protected void addSeriesForCertainReportEl(AhSeries series, AhReportElement rpElTmp) {
		if (rpElTmp != null) {
			if (rpElTmp.getSeries() == null) {
				rpElTmp.setSeries(new ArrayList<AhSeries>());
			}
			rpElTmp.getSeries().add(series);
		}
	}
	
	protected void setReportId(Long reportId) {
		this.id = reportId;
		this.reportEl = new AhReportElement(this.id);
		AhReportConfigElement rpConfig = AhReportContainer.getReportConfig(this.id);
		if (rpConfig != null
				&& !rpConfig.isBlnGroupCalEnabled()
				&& !StringUtils.isBlank(this.request.getSubType())) {
			this.reportEl.setGrpMark(this.request.getSubType());
		} else {
			this.reportEl.setGrpMark(GROUP_MARK_DEFAULT);
		}
	}
	
	public Long getId() {
		return id;
	}
	public void setRequest(AhReportRequest request) {
		this.request = request;
	}
	public AhReportRequest getRequest() {
		return this.request;
	}

	protected Map<String, AhReportElement> reportElsMap = new HashMap<String, AhReportElement>();
	public void addGroupReportEl(String subType) {
		AhReportElement reportElTmp = new AhReportElement(this.getId());
		reportElTmp.setTz(this.request.getTimeZone());
		reportElsMap.put(subType, reportElTmp);
	}
	public AhReportElement getGroupReportEl(String subType) {
		return reportElsMap.get(subType);
	}
	
	public Map<String, AhReportElement> getGroupReportEls() {
		return this.reportElsMap;
	}
	public AhChartExportInterf getExportChartEl() {
		return exportChartEl;
	}

	public void setExportChartEl(AhChartExportInterf exportChartEl) {
		this.exportChartEl = exportChartEl;
	}

	public boolean isBlnSampleData() {
		return blnSampleData;
	}

	public void setBlnSampleData(boolean blnSampleData) {
		this.blnSampleData = blnSampleData;
	}
}
