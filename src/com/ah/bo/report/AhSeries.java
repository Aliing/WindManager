package com.ah.bo.report;

import java.util.ArrayList;
import java.util.List;

public abstract class AhSeries {
	private Long id;
	private String name;
	private List<String> summarys;
	private String showType;
	private List<AhSeriesData> data;
	private Object stackGroup;
	private String customColor;
	private String unit;
	
	// used to identify this series when you can not use id in some cases
	private String uuKey;
	
	public boolean isNullSeries() {
		if (this.data == null
				|| this.data.isEmpty()) {
			return true;
		}
		return false;
	}
	
	protected void addData(Object name, Object value) {
		this.getSafeData().add(new AhSeriesData(name, value));
	}
	
	protected void addData(Object value){
		this.getSafeData().add(new AhSeriesData(null, value));
	}
	
	public void setStackGroup(String stackName) {
		this.stackGroup = stackName;
	}
	public void setStackGroup(int stackId) {
		this.stackGroup = stackId;
	}
	
	public List<AhSeriesData> getPairData() {
		if (this.data != null
				&& !this.data.isEmpty()) {
			if (this.data.get(0).getName() != null) {
				return this.data;
			}
		}
		return null;
	};
	
	public List<AhSeriesData> getSafeData() {
		if (this.data == null) {
			this.data = new ArrayList<AhSeriesData>();
		}
		return this.data;
	}
	
	public void addSummary(String summary) {
		if (this.summarys == null) {
			this.summarys = new ArrayList<String>();
		}
		this.summarys.add(summary);
	}
	
	public String getStackGroupString() {
		if (this.stackGroup == null) {
			return null;
		}
		return (String) this.stackGroup;
	}
	public Integer getStackGroupNumber() {
		if (this.stackGroup == null) {
			return null;
		}
		return (Integer) this.stackGroup;
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
	public String getShowType() {
		return showType;
	}
	public void setShowType(String showType) {
		this.showType = showType;
	}
	public List<String> getSummarys() {
		return summarys;
	}
	public void setSummarys(List<String> summarys) {
		this.summarys = summarys;
	}
	public List<AhSeriesData> getData() {
		return data;
	}
	public void setData(List<AhSeriesData> data) {
		this.data = data;
	}
	public void setObjectData(List<Object> data) {
		if (data != null
				&& !data.isEmpty()) {
			for (Object obj : data) {
				this.getSafeData().add(new AhSeriesData(null, obj));
			}
		}
	}
	public List<Object> getSimpleData() {
		if(this.data == null
				|| this.data.isEmpty()) {
			return null;
		}
		List<Object> result = new ArrayList<Object>();
		
		for (AhSeriesData dataTmp : this.data) {
			result.add(dataTmp.getValue());
		}
		
		return result;
	}

	public String getCustomColor() {
		return customColor;
	}

	public void setCustomColor(String customColor) {
		this.customColor = customColor;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUuKey() {
		return uuKey;
	}

	public void setUuKey(String uuKey) {
		this.uuKey = uuKey;
	}
	
}
