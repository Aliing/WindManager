package com.ah.bo.mgmt;

import java.util.List;
import java.util.Map;

import com.ah.util.CheckItem;
import com.ah.util.CheckItem3;

public class AhDataTableColumn {

	private String type;
	private String mark;
	private String editMark;
	private String display;
	private Object defaultValue;
	private boolean disabled;
	private List<CheckItem> options; 
	private Map<String,String> events;
	private List<CheckItem> changeValue;
	private List<CheckItem3> realmNameOtions; //this field is only for bonjour realm name, please don't set it.
	
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public String getType() {
		return type;
	}
	public String getMark() {
		return mark;
	}
	public String getDisplay() {
		return display;
	}
	public Object getDefaultValue() {
		return defaultValue;
	}
	public List<CheckItem> getOptions() {
		return options;
	}
	public Map<String, String> getEvents() {
		return events;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	public void setOptions(List<CheckItem> options) {
		this.options = options;
	}
	public void setEvents(Map<String, String> events) {
		this.events = events;
	}
	public String getEditMark() {
		return editMark;
	}
	public void setEditMark(String editMark) {
		this.editMark = editMark;
	}
	public void setChangeValue(List<CheckItem> changeValue) {
		this.changeValue = changeValue;
	}
	public List<CheckItem> getChangeValue() {
		return changeValue;
	}
	public List<CheckItem3> getRealmNameOtions() {
		return realmNameOtions;
	}
	public void setRealmNameOtions(List<CheckItem3> realmNameOtions) {
		this.realmNameOtions = realmNameOtions;
	}
	
}
