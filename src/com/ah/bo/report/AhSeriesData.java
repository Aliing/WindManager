package com.ah.bo.report;

public class AhSeriesData {
	
	private Object name;
	
	private Object value;
	
	private String unit;
	
	/**
	 * just for assistance
	 */
	private String category;
	
	public AhSeriesData(Object name, Object value) {
		this.name = name;
		this.value = value;
	}

	public AhSeriesData(Object name, Object value, String unit) {
		this(name, value);
		this.unit = unit;
	}
	
	public Object getName() {
		return name;
	}

	public void setName(Object name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
}
