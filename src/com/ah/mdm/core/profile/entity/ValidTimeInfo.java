package com.ah.mdm.core.profile.entity;

public class ValidTimeInfo {
	private int validType=0;
	private String keepTime="30";
	private String effectiveStartTime;
	private String effectiveEndTime;
	private String effectiveDay;

	public int getValidType() {
		return validType;
	}
	public void setValidType(int validType) {
		this.validType = validType;
	}
	public String getKeepTime() {
		return keepTime;
	}
	public void setKeepTime(String keepTime) {
		if("0".equals(keepTime) && this.validType != 1){
			this.keepTime = "30";
		}else{
			this.keepTime = keepTime;	
		}
		
	}
	public String getEffectiveStartTime() {
		return effectiveStartTime;
	}
	public void setEffectiveStartTime(String effectiveStartTime) {
		this.effectiveStartTime = effectiveStartTime;
	}
	public String getEffectiveEndTime() {
		return effectiveEndTime;
	}
	public void setEffectiveEndTime(String effectiveEndTime) {
		this.effectiveEndTime = effectiveEndTime;
	}
	public String getEffectiveDay() {
		return effectiveDay;
	}
	public void setEffectiveDay(String effectiveDay) {
		this.effectiveDay = effectiveDay;
	}
	
}
