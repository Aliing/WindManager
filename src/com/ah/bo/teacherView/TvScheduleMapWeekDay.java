/**
 *@filename		SingleTableItem.java
 *@version
 *@author		Fiona
 *@createtime	2007-12-17 PM 03:33:15
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.teacherView;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.ah.util.MgrUtil;

/**
 * @author		fisher
 * @version		V1.0.0.0 
 */
@Embeddable
public class TvScheduleMapWeekDay implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	public static final String MONDAY_TO_FRIDAY = "0111110";
	private String symbol;
	/**
	 * format "0111110", Sunday...Saturday
	 */
	@Column(length = 7)
	private String weekday;
	
	/**
	 * @return the weekday
	 */
	public String getWeekday() {
		return weekday;
	}
	/**
	 * @param weekday the weekday to set
	 */
	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}
	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}
	/**
	 * @param symbol the symbol to set
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public boolean getCSun(){
		if (weekday.charAt(0)=='1'){
			return true;
		}
		return false;
	}
	public boolean getCMon(){
		if (weekday.charAt(1)=='1'){
			return true;
		}
		return false;
	}
	public boolean getCTue(){
		if (weekday.charAt(2)=='1'){
			return true;
		}
		return false;
	}
	public boolean getCWed(){
		if (weekday.charAt(3)=='1'){
			return true;
		}
		return false;
	}
	public boolean getCThu(){
		if (weekday.charAt(4)=='1'){
			return true;
		}
		return false;
	}
	public boolean getCFri(){
		if (weekday.charAt(5)=='1'){
			return true;
		}
		return false;
	}
	public boolean getCSat(){
		if (weekday.charAt(6)=='1'){
			return true;
		}
		return false;
	}
	public boolean getCMonFri(){
		if (weekday.substring(1, 6).equals("11111")){
			return true;
		}
		return false;
	}

	public String getWeekdayString(){
		if (weekday.equals(MONDAY_TO_FRIDAY)){
			return MgrUtil.getUserMessage("config.tv.weekday.8");
		}
		StringBuffer tmpBuf = new StringBuffer();
		boolean addBefore=false;
		if (weekday.charAt(0)=='1'){
			tmpBuf.append(MgrUtil.getUserMessage("config.tv.weekday.1"));
			addBefore=true;
		}
		if (weekday.charAt(1)=='1'){
			if (addBefore) {tmpBuf.append(", ");}
			tmpBuf.append(MgrUtil.getUserMessage("config.tv.weekday.2"));
			addBefore=true;
		}
		if (weekday.charAt(2)=='1'){
			if (addBefore) {tmpBuf.append(", ");}
			tmpBuf.append(MgrUtil.getUserMessage("config.tv.weekday.3"));
			addBefore=true;
		}
		if (weekday.charAt(3)=='1'){
			if (addBefore) {tmpBuf.append(", ");}
			tmpBuf.append(MgrUtil.getUserMessage("config.tv.weekday.4"));
			addBefore=true;
		}
		if (weekday.charAt(4)=='1'){
			if (addBefore) {tmpBuf.append(", ");}
			tmpBuf.append(MgrUtil.getUserMessage("config.tv.weekday.5"));
			addBefore=true;
		}
		if (weekday.charAt(5)=='1'){
			if (addBefore) {tmpBuf.append(", ");}
			tmpBuf.append(MgrUtil.getUserMessage("config.tv.weekday.6"));
			addBefore=true;
		}
		if (weekday.charAt(6)=='1'){
			if (addBefore) {tmpBuf.append(", ");}
			tmpBuf.append(MgrUtil.getUserMessage("config.tv.weekday.7"));
			addBefore=true;
		}
		return tmpBuf.toString(); 
	}

}
