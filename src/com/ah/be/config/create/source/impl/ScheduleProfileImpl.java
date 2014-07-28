package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.wlan.Scheduler;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.ScheduleProfileInt;
import com.ah.util.MgrUtil;

/**
 * 
 * @author zhang
 *
 */
public class ScheduleProfileImpl implements ScheduleProfileInt {
	
	private Scheduler scheduleObj;
	private static final String NULL_STRING = "";
	private HiveAp hiveAp;
	
	private static String regexWeek = "^(Friday|Monday|Saturday|Sunday|Thursday|Tuesday|Wednesday)( to )(Friday|Monday|Saturday|Sunday|Thursday|Tuesday|Wednesday)$";
	
	public ScheduleProfileImpl(Scheduler scheduleObj, HiveAp hiveAp){
		this.scheduleObj = scheduleObj;
		this.hiveAp = hiveAp;
	}
	
	public String getScheduleGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.scheduler");
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}
	
	public String getScheduleName(){
		return scheduleObj.getSchedulerName();
	}
	
	public String getScheduleUpdateTime(){
		List<Object> scheduleList = new ArrayList<Object>();
		scheduleList.add(scheduleObj);
		return String.valueOf(CLICommonFunc.getLastUpdateTime(scheduleList));
	}
	
	public boolean isSelectOnceType(){
		return scheduleObj.getType() == Scheduler.ONE_TIME;
	}
	
	public String getFromDateAndTimeOnce(){
		return scheduleObj.getBeginDate()+" "+scheduleObj.getBeginTime();
	}
	
	public String getToDateAndTimeOnce(){
		return scheduleObj.getEndDate()+" "+scheduleObj.getEndTime();
	}
	
	public String getFromDateRecurrent(){
		String fromDate = scheduleObj.getBeginDate();
		if(fromDate != null){
			return fromDate;
		}else {
			return NULL_STRING;
		}
	}
	
	public String getToDateRecurrent(){
		String toDate = scheduleObj.getEndDate();
		if(toDate != null){
			return toDate;
		}else {
			return NULL_STRING;
		}
	}
	
	public String getFromWeekRecurrent(){
		return getFromWeek(scheduleObj.getWeeks());
	}
	
	public String getPpskWeekDay(){
		byte fromWeek = getWeekNum(this.getFromWeekRecurrent());
		byte toWeek = getWeekNum(this.getToWeekRecurrent());
		if(fromWeek == 0 && toWeek == 0){
			return "";
		}
		if(!"".equals(this.getFromWeekRecurrent()) && "".equals(this.getToWeekRecurrent())){
			toWeek = fromWeek;
		}
		String rsStr = String.valueOf(fromWeek);
		while(fromWeek != toWeek){
			if(++fromWeek > 7){
				fromWeek = (byte) (fromWeek%7);
			}
			rsStr += fromWeek;
		}
		return rsStr;
	}
	
	public static byte getWeekNum(String weekStr){
		if(weekStr == null){
			return 0;
		}
		if(weekStr.equals("Sunday")){
			return 1;
		}else if(weekStr.equals("Monday")){
			return 2;
		}else if(weekStr.equals("Tuesday")){
			return 3;
		}else if(weekStr.equals("Wednesday")){
			return 4;
		}else if(weekStr.equals("Thursday")){
			return 5;
		}else if(weekStr.equals("Friday")){
			return 6;
		}else if(weekStr.equals("Saturday")){
			return 7;
		}else{
			return 0;
		}
	}
	
	public static String getFromWeek(String weekStr){
		String weeks =  weekStr;
		if(weeks == null){
			return NULL_STRING;
		}
		
		Pattern pattern = Pattern.compile(regexWeek);
		Matcher matcher = pattern.matcher(weeks);
		return matcher.replaceAll("$1");
	}
	
	public String getToWeekRecurrent(){
		return getToWeek(scheduleObj.getWeeks());
	}
	
	public static String getToWeek(String weekStr){
		String weeks =  weekStr;
		if(weeks == null){
			return NULL_STRING;
		}
		
		Pattern pattern = Pattern.compile(regexWeek);
		Matcher matcher = pattern.matcher(weeks);
		
		String weekTo = matcher.replaceAll("$3");
		if(weekTo.equals(getFromWeek(weekStr))){
			return NULL_STRING;
		}else{
			return weekTo;
		}
	}
	
	public static String getToWeekRealForPPSK(String weekStr){
		String weeks =  weekStr;
		if(weeks == null){
			return NULL_STRING;
		}
		
		Pattern pattern = Pattern.compile(regexWeek);
		Matcher matcher = pattern.matcher(weeks);
		
		String weekTo = matcher.replaceAll("$3");
		return weekTo;
	}
	
	public String getFromTimeOneRecurrent(){
		String fromTime1 =  scheduleObj.getBeginTime();
		if(fromTime1 != null){
			return fromTime1;
		}else{
			return NULL_STRING;
		}
	}
	
	public String getToTimeOneRecurrent(){
		String toTime1 = scheduleObj.getEndTime();
		if(toTime1 != null){
			return toTime1;
		}else{
			return NULL_STRING;
		}
	}
	
	public String getFromTimeTwoRecurrent(){
		String fromTime2 = scheduleObj.getBeginTimeS();
		if(fromTime2 != null){
			return fromTime2;
		}else{
			return NULL_STRING;
		}
	}
	
	public String getToTimeTwoRecurrent(){
		String toTime2 = scheduleObj.getEndTimeS();
		if(toTime2 != null){
			return toTime2;
		}else{
			return NULL_STRING;
		}
	}
	
	public static void main(String[] args){
		String regexWeek = "^(Friday|Monday|Saturday|Sunday|Thursday|Tuesday|Wednesday)( to )(Friday|Monday|Saturday|Sunday|Thursday|Tuesday|Wednesday)$";
		Pattern pattern = Pattern.compile(regexWeek);
		Matcher matcher = pattern.matcher("Monday to Saturday");
		System.out.println(matcher.replaceAll("$3"));
	}
}
