package com.ah.bo.report;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.ah.util.bo.report.freechart.AhChartExportInterf;

public class AhReportElement {
	
	public static final String SERIES_NAME_NO_DATA = "NoData";
	
	private Long id;
	private String name;
	private String title;
	private String subTitle;
	private String summary;
	private List<Object> categories;
	private List<AhSeries> series;
	private String valueTitle;
	private String categoryTitle;
	
	private Long startTime;
	private Long endTime;
	
	private boolean blnCategoriesSet;
	
	private String grpMark;
	
	private AhChartExportInterf exportChartEl;
	private TimeZone tz;
	
	public AhReportElement(Long id) {
		this.id = id;
	}
	
	public boolean isReportNull() {
		if (this.series == null
				|| this.series.isEmpty()
				|| SERIES_NAME_NO_DATA.equals(this.series.get(0).getName())) {
			return true;
		}
		
		return false;
	}
	
	public boolean addCategory(Object category) {
		if (this.categories == null) {
			this.categories = new ArrayList<Object>();
		}
		this.categories.add(category);
		blnCategoriesSet = true;
		return true;
	}
	
	public List<Object> getJudgedCategories() {
		if (blnCategoriesSet) {
			return this.categories;
		} else {
			if (this.series != null
					&& !this.series.isEmpty()) {
				List<Object> tmpCategories = new ArrayList<Object>();
				AhSeries aSeries = this.series.get(0);
				for (AhSeriesData dataTmp : aSeries.getData()) {
					if (!tmpCategories.contains(dataTmp.getName())) {
						tmpCategories.add(dataTmp.getName());
					}
				}
				return tmpCategories;
			}
		}
		return this.categories;
	}
	
	public void addSeries(AhSeries aSeries) {
		if (this.series == null) {
			this.series = new ArrayList<AhSeries>();
		}
		this.series.add(aSeries);
	}
	
	public boolean isBlnCategoriesSet() {
		return this.blnCategoriesSet;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public List<AhSeries> getSeries() {
		return series;
	}
	public void setSeries(List<AhSeries> series) {
		this.series = series;
	}
	public List<Object> getCategories() {
		return categories;
	}
	public void setCategories(List<Object> categories) {
		this.categories = categories;
		blnCategoriesSet = true;
	}

	public List<String> getCategorieStrings() {
		List<String> result = new ArrayList<>();
		if (categories != null
				&& !this.categories.isEmpty()) {
			for (Object obj : this.categories) {
				if (obj == null
						|| StringUtils.isBlank(obj.toString())) {
					result.add("");
				} else {
					result.add(obj.toString());
				}
			}
		}
		
		return result;
	}
	
	public String getValueTitle() {
		return valueTitle;
	}

	public void setValueTitle(String valueTitle) {
		this.valueTitle = valueTitle;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public String getGrpMark() {
		return grpMark;
	}

	public void setGrpMark(String grpMark) {
		this.grpMark = grpMark;
	}

	public AhChartExportInterf getExportChartEl() {
		return exportChartEl;
	}

	public void setExportChartEl(AhChartExportInterf exportChartEl) {
		this.exportChartEl = exportChartEl;
	}

	public String getCategoryTitle() {
		return categoryTitle;
	}

	public void setCategoryTitle(String categoryTitle) {
		this.categoryTitle = categoryTitle;
	}

	public TimeZone getTz() {
		return tz;
	}

	public void setTz(TimeZone tz) {
		this.tz = tz;
	}
}
