package com.ah.util;

import com.ah.be.common.NmsUtil;

public class CheckItem {
	protected Long id;

	protected String value;
	
	private short deviceType;
	
	private String[] deviceModels;
	
	private String strModels;



	public CheckItem(Long id, String value) {
		this.id = id;
		this.value = value;
	}

	public CheckItem(Long id, String value,short type, String[] models,String strModels){
		this.id = id;
		this.value = value;
		this.deviceType = type;
		this.deviceModels = models;
		this.strModels = strModels;
	}
	public Long getId() {
		return id;
	}

	public String getValue() {
		return value;
	}
	
	public short getDeviceType() {
		return deviceType;
	}

	public String[] getDeviceModels() {
		return deviceModels;
	}
	
	public String getStrModels() {
		return strModels;
	}
	public String getLongToTime(){
		if (id >= 0) {
			return NmsUtil.transformTime((int) (id / 1000));
		}
		return "N/A";
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CheckItem)) {
			return false;
		}
		return id.longValue()==(((CheckItem) other).getId().longValue());
	}

	@Override
	public int hashCode() {
		return id.intValue();
	}
}

