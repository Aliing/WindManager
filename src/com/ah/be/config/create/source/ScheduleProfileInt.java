package com.ah.be.config.create.source;

/**
 * 
 * @author zhang
 *
 */
public interface ScheduleProfileInt {
	
	public String getScheduleGuiName();

	public String getApVersion();
	
	public String getScheduleName();
	
	public String getScheduleUpdateTime();
	
	public boolean isSelectOnceType();
	
	public String getFromDateAndTimeOnce();
	
	public String getToDateAndTimeOnce();
	
	public String getFromDateRecurrent();
	
	public String getToDateRecurrent();
	
	public String getFromWeekRecurrent();
	
	public String getPpskWeekDay();
	
	public String getToWeekRecurrent();
	
	public String getFromTimeOneRecurrent();
	
	public String getToTimeOneRecurrent();
	
	public String getFromTimeTwoRecurrent();
	
	public String getToTimeTwoRecurrent();
	
}
